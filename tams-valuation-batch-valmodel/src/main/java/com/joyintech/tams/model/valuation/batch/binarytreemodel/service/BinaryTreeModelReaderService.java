package com.joyintech.tams.model.valuation.batch.binarytreemodel.service;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.ModBatchLogs;
import com.joyintech.tams.common.base.entity.ModBintreeConfig;
import com.joyintech.tams.common.base.entity.ModValBatchParams;
import com.joyintech.tams.common.base.entity.MstSharePrice;
import com.joyintech.tams.constant.ModelDictCodeList;
import com.joyintech.tams.model.valuation.batch.binarytreemodel.dao.BinaryTreeModelMapper;
import com.joyintech.tams.model.valuation.batch.binarytreemodel.dto.*;
import com.joyintech.tams.valuation.common.dto.modelDto.*;
import com.joyintech.tams.valuation.component.batch.service.ModLogsService;
import com.joyintech.tams.valuation.component.batch.service.ModParamsService;
import com.joyintech.tams.valuation.component.batch.service.ModValuationCommonService;
import com.joyintech.tams.valuation.component.common.constant.ValComponentConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 二叉树模型批处理Reader核心业务组件
 */
@Service
public class BinaryTreeModelReaderService {
    /**
     * Mapper
     */
    @Autowired
    private BinaryTreeModelMapper binaryTreeModelMapper;

    @Autowired
    private ModParamsService modParamsService;
    @Autowired
    private ModValuationCommonService modValuationCommonService;
    @Autowired
    private ModLogsService modLogsService;

    /**
     * 二叉树模型-reader
     * @param params
     * @return
     */
    public List<BinaryTreeModelReaderEntity> createReaderList(Map<String, Object> params, StepExecution stepExecution) {
        List<BinaryTreeModelReaderEntity> list = new ArrayList<BinaryTreeModelReaderEntity>();
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)params.get(ValComponentConstant.BATCH_EXECUTEINFO);
        List<ModValBatchParams> listBatchParam = modParamsService.selectByBatchExecSeqno(batchExecuteInfo.getBatchExedetailSeqNo());


        //参数收集
        //1.读取批处理画面选择的资产代码列表
        //资产代码集合
        List<String> finprodIdList = CollectionUtils.isNotEmpty(listBatchParam)?listBatchParam.stream().map((x)->x.getFinprodId()).collect(Collectors.toList()):new ArrayList<String>();

        //2.调用方法【资产基本信息查询-估值模型】取配置了模型法的并且有持仓的可转可交换债
        //跑批日
        Date platDate = (Date)params.get(ValComponentConstant.EXECUTE_DATE);

        //3.查询配置了需要跑模型的资产
        //资产基本信息查询-估值模型
        ModAssetInfoParamsDto modAssetInfoParamsDto = new ModAssetInfoParamsDto();
        //3.1设置参数：finprodIdList，跑批画面选了哪些资产就跑哪些资产，如果没选，就会跑估值方案配置了模型法估值的资产
        modAssetInfoParamsDto.setFinprodIdList(finprodIdList);
        //3.2设置参数：platDate，跑批画面选什么日期；主要约束着资产的起息日和到期日，起息日要小于等于查询日，到期日要大于等于查询日
        modAssetInfoParamsDto.setSearchDate(platDate);

        //4.查询符合条件的资产
        //4.1.可转债、可交换债
        //4.2.估值方案配置了使用模型法进行估值的
        //4.3.起息日在查询日之前且到期日在查询日之后
        List<ModAssetInfoDto> listAsset = modValuationCommonService.selectBinaryTreeAssetInfo(modAssetInfoParamsDto);


//        //4'.设置job上下文参数，在监听中使用
//        List<ModValBatchParams> paralist = new ArrayList<ModValBatchParams>();
//        if(CollectionUtils.isNotEmpty(listAsset)){
//            listAsset.stream().forEach(x->{
//                ModValBatchParams modValBatchParams = new ModValBatchParams();
//                modValBatchParams.setBatchExecSeqno(batchExecuteInfo.getBatchExedetailSeqNo());
//                modValBatchParams.setFinprodId(x.getFinprodId());
//                paralist.add(modValBatchParams);
//            });
//        }
//        stepExecution.getJobExecution().getExecutionContext().put(BATCH_PARAMS_KEY, paralist);


//        //5.封装日志信息
//        modLogsService.batchInitLogs(batchExecuteInfo.getTaskId(),batchExecuteInfo.getBatchExedetailSeqNo(),CollectionUtils.isNotEmpty(listAsset)?listAsset.stream().map((x)->x.getFinprodId()).collect(Collectors.toList()):null);

        //6.查询二叉树模型配置
        List<ModBintreeConfig> listFin = binaryTreeModelMapper.findModBintreeConfig(finprodIdList);

        //7.如果待处理资产找不到对应的配置，则设置错误日志信息
        if(CollectionUtils.isEmpty(listFin)&&CollectionUtils.isNotEmpty(listFin)){
            listAsset.stream().forEach((x)->{
                modLogsService.error((Map<String,ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),x.getFinprodId(), "【二叉树模型批处理】：未配置二叉树模型参数.");
            });
            return list;
        }

        //8.将配置信息构间为数据字典，方便后续使用
        Map<String,ModBintreeConfig> configMap = listFin.stream().collect(Collectors.toMap(ModBintreeConfig::getFinprodId,x->x));

        //9.遍历待处理资产，构间reader返回结果
        for (ModAssetInfoDto modAssetInfoDto : listAsset) {
            BinaryTreeModelReaderEntity model = new BinaryTreeModelReaderEntity();
//            BeanUtil.copyPropertiesIgnoreNull(model, modAssetInfoDto);
            model.setModAssetInfoDto(modAssetInfoDto);
            String finProdId = model.getModAssetInfoDto().getFinprodId();

            //9.1待处理资产设置二叉树配置信息，如无配置，则出警告信息
            if(configMap.containsKey(finProdId)){
                model.setModBintreeConfig(configMap.get(finProdId));
            }else{
                modLogsService.error((Map<String,ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),finProdId, "【二叉树模型批处理】：未配置二叉树模型参数.");
                continue;
            }

            //9.2 现金流信息查询
            List<ModCashFlowDto> modCashFlowDtoList = modValuationCommonService.getCashFlowPlanInfoByFinprodId(finProdId);
            model.setModCashFlowList(modCashFlowDtoList);

            //9.3 现金流计算规则查询
            List<ModCashFlowCalRuleDto> modCashFlowCalRuleDtoList = modValuationCommonService.getCashFlowCalRulesByFinprodId(finProdId);
            model.setModCashFlowCalRuleList(modCashFlowCalRuleDtoList);

            //9.4 现金流支付规则查询
            List<ModCashFlowPayRulesDto> modCashFlowPayRulesList = modValuationCommonService.getCashFlowPayRulesByFinprodId(finProdId);
            model.setModCashFlowPayRulesList(modCashFlowPayRulesList);

            //9.5 股票行情查询
            ModStockMarketParamsDto modStockMarketParamsDto = new ModStockMarketParamsDto();
            List<String> listStockId = new ArrayList<>();
            ModBintreeConfig config = model.getModBintreeConfig();
            if(config.getStockBordsttus()!=null&&config.getStockBordsttus().equals(ModelDictCodeList.上市状态_未上市.getValue())){
                listStockId.add(config.getCompStockid());
            }else{
                if(modAssetInfoDto.getConFinprodId()!=null){
                    listStockId.add(modAssetInfoDto.getConFinprodId());
                }else{
                    modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),finProdId, "【二叉树模型批处理】：债券的转股代码为空，无法继续.");
                    continue;
                }
            }
            modStockMarketParamsDto.setStockIdList(listStockId);
            //查询开始日	= 跑批日-（明细.到期日-跑批日）
            long lastQx = DateUtil.getBetweenDayNumber(platDate, modAssetInfoDto.getMdate());
//            Date startDate = DateUtil.addDay(platDate, -(int)lastQx);
//            modStockMarketParamsDto.setStartDate(startDate);
            //查询结束日 = 跑批日
            modStockMarketParamsDto.setSearchDate(platDate);
            modStockMarketParamsDto.setSearchNum(lastQx);
            List<MstSharePrice> listStock = modValuationCommonService.getStockMarketIntervalInfo(modStockMarketParamsDto);
            model.setModStockMarketInfoList(listStock);

            //9.6 曲线行情查询
            ModYeildCurveParamsDto modYeildCurveParamsDto = new ModYeildCurveParamsDto();
            List<String> listYeildId = new ArrayList<>();
            listYeildId.add(model.getModBintreeConfig().getCurveCode().toString());
            modYeildCurveParamsDto.setYeildIdList(listYeildId);
            modYeildCurveParamsDto.setSearchDate(platDate);
            Map<String, List<ModYieldCurveInfoDto>> mapYield = modValuationCommonService.getCurveInfo(modYeildCurveParamsDto);
            if (mapYield != null) {
                model.setModYieldCurveInfoList(mapYield.get(model.getModBintreeConfig().getCurveCode().toString()));
            }else{
                modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),finProdId, "【二叉树模型批处理】：取不到曲线行情数据，曲线代码："+model.getModBintreeConfig().getCurveCode().toString()+"，曲线日期："+platDate);
                continue;
            }

            list.add(model);
        }

        return list;
    }
}

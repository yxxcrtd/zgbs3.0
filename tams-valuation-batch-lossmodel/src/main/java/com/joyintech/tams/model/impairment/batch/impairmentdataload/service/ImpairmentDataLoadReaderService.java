package com.joyintech.tams.model.impairment.batch.impairmentdataload.service;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.ModelDictCodeList;
import com.joyintech.tams.model.impairment.batch.common.searchinfo.service.ImpairmentCommonService;
import com.joyintech.tams.model.impairment.batch.impairmentdataload.dao.ImpairmentDataLoadMapper;
import com.joyintech.tams.model.impairment.batch.impairmentdataload.dto.ImpairmentDataLoadStep1ReaderDto;
import com.joyintech.tams.valuation.common.dto.modelDto.ModAssetInfoDto;
import com.joyintech.tams.valuation.component.batch.service.ModLogsService;
import com.joyintech.tams.valuation.component.common.constant.ValComponentConstant;
import com.joyintech.tams.valuation.model.common.rateconfig.service.RateConfigCommonService;
import com.joyintech.tams.valuation.model.common.rateinfo.dto.ViewRatingInfoDto;
import com.joyintech.tams.valuation.model.common.rateinfo.service.RateInfoCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ImpairmentDataLoadReaderService {

    @Autowired
    private ImpairmentCommonService impairmentCommonService;
    @Autowired
    private ImpairmentDataLoadMapper impairmentDataLoadMapper;
    @Autowired
    private RateConfigCommonService rateConfigCommonService;
    @Autowired
    private RateInfoCommonService rateInfoCommonService;
    @Autowired
    private ModLogsService modLogsService;
    /**
     * 二叉树模型-reader
     * @param params
     * @return
     */
    public List<ImpairmentDataLoadStep1ReaderDto> createReaderList(Map<String, Object> params, StepExecution stepExecution) {
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)params.get("BATCH_INFO");
        List<ModValBatchParams> listBatch = (List<ModValBatchParams>) params.get("listBatch");
        //1.读取需要进行处理的资产
        //资产代码集合
        List<String> finprodIdList = new ArrayList<String>();
        if (listBatch != null && listBatch.size() > 0) {
            for (ModValBatchParams params1 : listBatch) {
                finprodIdList.add(params1.getFinprodId());
            }
        }
        //跑批日
        Date platDate = (Date)params.get("PLATE_DATE");
        Map<String, Object> para = new HashMap<String, Object>();
        para.put("searchDate", platDate);
        para.put("SEC_TYPE_01", DictCodeListExt1.证券品种_债券.getValue());
        para.put("SEC_TYPE_13", DictCodeListExt1.证券品种_其他债权类.getValue());
        para.put("finprodIdList", finprodIdList);
        List<ImpairmentDataLoadStep1ReaderDto> list = new ArrayList<>();
        List<ImpairmentDataLoadStep1ReaderDto> listErrorDto = new ArrayList<>();
        // 减值方案系统参数
        String lossType = CacheUtil.getSysParam(DictCode.减值方案类型.getValue());
        if (DictCodeListExt3.减值方案类型_普华.getValue().equals(lossType)) {
            //1、获取需要当天处理的初始数据列表
            List<ImpairmentDataLoadStep1ReaderDto> listAsset = impairmentCommonService.selectAssetTradeList(para);
            list.addAll(listAsset);
        }
        else{
            para.put("SEC_TYPE_07", DictCodeListExt1.证券品种_存款.getValue());
            para.put("SEC_TYPE_09", DictCodeListExt1.证券品种_回购.getValue());
            //1、获取需要当天处理的初始数据列表
            // 1.1 查询昨日仍然持仓资产信息
            List<ImpairmentDataLoadStep1ReaderDto> listAsset = impairmentCommonService.selectAssetInfo(para);
            if (CollectionUtils.isNotEmpty(listAsset)) {
                //  查询资产对应首次买入日期
                for(ImpairmentDataLoadStep1ReaderDto item : listAsset){
                    Map<String, Object> searchPara = new HashMap<String, Object>();
                    searchPara.put("prodId", item.getProdId());
                    searchPara.put("portfolioId",item.getPortfolioId());
                    searchPara.put("finprodId",item.getFinprodId());
                    searchPara.put("platDate",platDate);
                    // 调用方法查询首次买入日期，存入交割日，后续查询买入时点评级使用
                    item.setSettleDate(impairmentCommonService.queryFirstBuyDate(searchPara));
                }
                list.addAll(listAsset);
            }
            // 查询昨日无持仓，今日买入资产
            List<ImpairmentDataLoadStep1ReaderDto> listTdyAsset = impairmentCommonService.selectTdyAssetInfo(para);
            if(CollectionUtils.isNotEmpty(listTdyAsset)){
                list.addAll(listTdyAsset);
            }
        }
        if (!CollectionUtils.isNotEmpty(list)) {
            return list;
        }

        //2、查询外部机构评级结果，映射评级机构对应的内部评级
        // 2.1 查询所有的评级配置数据
        // 定义 变量.特殊资产评级配置
        Map<String, ModAssetGradeSpecialConfig> sepcialConfigMap = rateConfigCommonService.listAssetSpecialGradeConfigs();
        // 定义 变量.资产大类评级配置
        Map<String, ModAssetGradeConfig> configMap = rateConfigCommonService.listAssetGradeConfigs();
        // 遍历 reader查询待处理数据
        for(ImpairmentDataLoadStep1ReaderDto entity : list){
            // 定义 变量.评级配置使用情况
            ModConfigUseage modConfigUseage = new ModConfigUseage();
            // 定义 查询参数.资产信息
            ModAssetInfoDto modAssetInfoDto = new ModAssetInfoDto();
            BeanUtil.copyPropertiesIgnoreNull(modAssetInfoDto, entity);
            modAssetInfoDto.setSecType(entity.getSecTypeModel());
            modAssetInfoDto.setSecTypeDetail(entity.getSecDetailTypeModel());
            // 定义 变量.评级信息
            ViewRatingInfoDto ratingInfoDto = null;
            // 特殊资产评级配置不为空时
            if(sepcialConfigMap.get(entity.getFinprodId()) != null){
                // 特殊资产评级配置
                ModAssetGradeSpecialConfig specialConfig = sepcialConfigMap.get(entity.getFinprodId());
                //定义 变量.评级配置使用情况.配置流水号 = LGD参数Dto.流水号
                modConfigUseage.setConfigSeqno(specialConfig.getSeqno());
                //定义 变量.评级配置使用情况.配置类型 = LGD参数Dto.配置类型
                modConfigUseage.setConfigType(specialConfig.getoType());
                // 交易信息.评级配置使用情况 = 变量.评级配置使用情况
                entity.setModConfigUseage(modConfigUseage);
                //交易信息.评级类型 = 评级参数Dto.评级类型
                entity.setoType(specialConfig.getoType());
                //交易信息.评级机构 = 评级参数Dto.评级机构
                entity.setGradeOrgId(specialConfig.getGradeOrgId());
                // 交易信息.评级机构类型 = 评级参数Dto.评级机构类型
                entity.setGradeOrgType(specialConfig.getGradeOrgType());

                // 查询对应评级信息
                ratingInfoDto  = rateInfoCommonService.getRatingInfoBySpecialConfig(modAssetInfoDto,specialConfig,platDate);
            }else if(configMap.get(entity.getSecTypeModel()+entity.getSecDetailTypeModel()) != null
                || configMap.get(entity.getSecTypeModel()) != null){
                // 一般资产评级配置
                ModAssetGradeConfig config = new ModAssetGradeConfig();
                // 资产类型+资产细分类
                if(configMap.get(entity.getSecTypeModel()+entity.getSecDetailTypeModel()) != null){
                    config = configMap.get(entity.getSecTypeModel()+entity.getSecDetailTypeModel());
                }
                // 否则，直接根据资产类型取值
                else{
                    config = configMap.get(entity.getSecTypeModel());
                }
                //定义 配置流水号 = LGD参数Dto.流水号
                modConfigUseage.setConfigSeqno(config.getSeqno());
                //定义 配置类型 = LGD参数Dto.配置类型
                modConfigUseage.setConfigType(config.getoType());
                // 交易信息.评级配置使用情况 = 变量.评级配置使用情况
                entity.setModConfigUseage(modConfigUseage);
                //交易信息.评级类型 = 评级参数Dto.评级类型
                entity.setoType(config.getoType());
                //交易信息.评级机构 = 评级参数Dto.评级机构
                entity.setGradeOrgId(config.getGradeOrgId());
                // 交易信息.评级机构类型 = 评级参数Dto.评级机构类型
                entity.setGradeOrgType(config.getGradeOrgType());

                // 查询对应评级信息
                ratingInfoDto = rateInfoCommonService.getRatingInfoByTypeConfig(modAssetInfoDto,config,platDate);
            }
            else{
                // 警告：资产无对应资产评级配置
                String msg = "【资产减值过程加载批处理】：资产【"+entity.getFinprodId()+"】未能匹配到相应的评级配置信息";
                modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),entity.getFinprodId(), msg);

                listErrorDto.add(entity);
                continue;
            }
            if(ratingInfoDto != null){
                //交易数据.最新评级结果 = 评级信息.评级结果
                entity.setResultCurrent(ratingInfoDto.getRating());
                //交易数据.最终使用主体机构 = 评级信息.评级主体
                entity.setOrgIdFinal(ratingInfoDto.getOcode());
                //交易数据.主体是否持有金融牌照 = 评级信息.主体是否持有金融牌照
                entity.setIsFinLicence(ratingInfoDto.getIsFinLicence());
            }
            else{
                // 警告：资产无对应评级信息
                String msg = "【资产减值过程加载批处理】：资产【"+entity.getFinprodId()+"】未能匹配到相应的评级结果信息";
                modLogsService.warn((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),entity.getFinprodId(), msg);
                if (DictCodeListExt3.减值方案类型_毕马威.getValue().equals(lossType)) {
                    // 取系统兜底评级
                    entity.setResultCurrent(rateInfoCommonService.getBottomRateInfo(entity.getSecTypeModel(), entity.getSecDetailTypeModel()));
                }
            }
        }
        if(CollectionUtils.isNotEmpty(listErrorDto)){
            list.removeAll(listErrorDto);
        }
        return list;
    }

}

/*
 * 文件名：AssetIntdateCarryoverReaderService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年5月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.model.valuation.batch.cashflowdiscount.service;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.model.valuation.batch.cashflowdiscount.dao.CashFlowDiscountMapper;
import com.joyintech.tams.model.valuation.batch.cashflowdiscount.dto.CashFlowDiscountReaderResult;
import com.joyintech.tams.model.valuation.batch.cashflowdiscount.dto.ModCurveDto;
import com.joyintech.tams.valuation.common.dto.modelDto.*;
import com.joyintech.tams.valuation.component.batch.service.ModLogsService;
import com.joyintech.tams.valuation.component.batch.service.ModParamsService;
import com.joyintech.tams.valuation.component.batch.service.ModValuationCommonService;
import com.joyintech.tams.valuation.component.common.constant.ValComponentConstant;
import com.joyintech.tams.valuation.model.common.rateconfig.service.RateConfigCommonService;
import com.joyintech.tams.valuation.model.common.rateinfo.dto.ViewRatingInfoDto;
import com.joyintech.tams.valuation.model.common.rateinfo.service.RateInfoCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 现金流贴现批处理- reader相关业务处理
 *
 * @author zznn
 * @date 20210706
 */
@Service
@Scope("prototype")
public class CashFlowDiscountReaderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ModValuationCommonService modValuationCommonService;
    /**
     * reader方法的 Service
     */
    @Autowired
    private CashFlowDiscountMapper cashFlowDiscountMapper;

    @Autowired
    private ModParamsService modParamsService;

    @Autowired
    private ModLogsService modLogsService;

    @Autowired
    private RateInfoCommonService rateInfoCommonService;

    @Autowired
    private RateConfigCommonService rateConfigCommonService;

    /**
     * 获取数据库数据
     *
     * @param param 传递参数
     * @return List<BookDetailCompareIntoReadResult>
     * @see
     * @since 1.0
     */
    public List<CashFlowDiscountReaderResult> execute(Map<String, Object> param,StepExecution stepExecution) {
        List<CashFlowDiscountReaderResult> result = new ArrayList<>();
        Date happenDate = (Date) param.get(ValComponentConstant.EXECUTE_DATE); // 发生日：批处理任务执行信息.处理日（发生日）
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo) param.get(ValComponentConstant.BATCH_EXECUTEINFO);
        List<ModValBatchParams> listBatch = modParamsService.selectByBatchExecSeqno(batchExecuteInfo.getBatchExedetailSeqNo());

        //1.读取画面跑批选择的资产
        List<String> finprodIdList = new ArrayList<String>();
        if (listBatch != null && listBatch.size() > 0) {
            finprodIdList = listBatch.stream().map(x -> x.getFinprodId()).collect(Collectors.toList());
        }
        param.put("finprodIdList", finprodIdList);

        ModAssetInfoParamsDto modAssetInfoParamsDto = new ModAssetInfoParamsDto();
        modAssetInfoParamsDto.setSearchDate(happenDate);
        modAssetInfoParamsDto.setFinprodIdList(finprodIdList);

        //2.查询贴现法配置数据
        //此处查询出来的是所有的配置
        List<ModCurveDto> allModCurve = cashFlowDiscountMapper.getAllModCurve(modAssetInfoParamsDto);
        Map<String,ModCurveDto> configIndex = allModCurve.stream().collect(Collectors.toMap(x->x.getSecTypeModel()+x.getSecDetailTypeModel(),(y)->y));

        //3.查询待处理资产：
        // 估值方法配置为模型法的
        // 债券中剔除可转债、可交换债、优先股
        // 其他债权
        List<ModAssetInfoDto> pendingModAssetInfo = modValuationCommonService.selectAssetInfo(modAssetInfoParamsDto);

        if(CollectionUtils.isEmpty(pendingModAssetInfo)){
            //待处理资产为空，无需处理
            return result;
        }else{
            //初始化日志
//            modLogsService.batchInitLogs(batchExecuteInfo.getTaskId(),batchExecuteInfo.getBatchExedetailSeqNo(),pendingModAssetInfo.stream().map(x->x.getFinprodId()).collect(Collectors.toList()));
            Map<String, ModAssetGradeConfig> modAssetGradeConfigMap = rateConfigCommonService.listAssetGradeConfigs();
            Map<String, ModAssetGradeSpecialConfig> modAssetGradeSpecialConfigMap = rateConfigCommonService.listAssetSpecialGradeConfigs();


            if(CollectionUtils.isEmpty(allModCurve)){
                pendingModAssetInfo.forEach(x->{
                    modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),x.getFinprodId(), "【现金流贴现模型批处理】：现金流贴现模型缺少配置，无法继续.");
                });
                return result;
            }else if(modAssetGradeConfigMap ==null && modAssetGradeSpecialConfigMap==null) {
                pendingModAssetInfo.forEach(x->{
                    modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),x.getFinprodId(), "【现金流贴现模型批处理】：现金流贴现模型缺少资产评级配置，无法继续.");
                });
                return result;
            }else{
                //处理result
                pendingModAssetInfo.stream().forEach(item -> {
                    CashFlowDiscountReaderResult cashFlowDiscountReaderResult = new CashFlowDiscountReaderResult();
                    cashFlowDiscountReaderResult.setModAssetInfoDto(item);
                    String key_detail = item.getSecType()+item.getSecTypeDetail();
                    String key = item.getSecType();
                    if(configIndex.containsKey(key_detail)){
                        cashFlowDiscountReaderResult.setModCurveConfig(configIndex.get(key_detail));
                        //查找现金流计划信息
                        List<ModCashFlowDto> modCashFlows = modValuationCommonService.getCashFlowPlanInfoByFinprodId(item.getFinprodId());
                        cashFlowDiscountReaderResult.setModCashFlows(modCashFlows);
                        cashFlowDiscountReaderResult.setModCashFlowsOld(modCashFlows);
                        //查找现金流计算规则信息
                        List<ModCashFlowCalRuleDto> modCashFlowCalRules = modValuationCommonService.getCashFlowCalRulesByFinprodId(item.getFinprodId());
                        cashFlowDiscountReaderResult.setModCashFlowCalRules(modCashFlowCalRules);
                        //查找现金流支付规则
                        List<ModCashFlowPayRulesDto> modCashFlowPayRules = modValuationCommonService.getCashFlowPayRulesByFinprodId(item.getFinprodId());
                        cashFlowDiscountReaderResult.setModCashFlowPayRules(modCashFlowPayRules);

                        ViewRatingInfoDto rateInfo = null;
                        //如果配置使用的是国债曲线族，则不查找评级信息
                        if("01".equals(cashFlowDiscountReaderResult.getModCurveConfig().getCurveGroupCode())){
                            logger.info("资产配置使用国债收益率，不需要取评级信息.资产代码："+item.getFinprodId());
                        }else{
                            if(modAssetGradeSpecialConfigMap!=null && modAssetGradeSpecialConfigMap.containsKey(item.getFinprodId())){
                                ModAssetGradeSpecialConfig modAssetGradeSpecialConfig = modAssetGradeSpecialConfigMap.get(item.getFinprodId());
                                rateInfo = rateInfoCommonService.getRatingInfoBySpecialConfig(item,modAssetGradeSpecialConfig,happenDate);
                            }else if(modAssetGradeConfigMap!=null && modAssetGradeConfigMap.containsKey(key_detail)){
                                ModAssetGradeConfig modAssetGradeConfig = modAssetGradeConfigMap.get(key_detail);
                                rateInfo = rateInfoCommonService.getRatingInfoByTypeConfig(item,modAssetGradeConfig,happenDate);
                            }else if(modAssetGradeConfigMap!=null && modAssetGradeConfigMap.containsKey(key)){
                                ModAssetGradeConfig modAssetGradeConfig = modAssetGradeConfigMap.get(key);
                                rateInfo = rateInfoCommonService.getRatingInfoByTypeConfig(item,modAssetGradeConfig,happenDate);
                            }else{
                                //TODO 打印日志，找不到评级配置
                                logger.warn("资产配置中找不到评级配置.资产代码："+cashFlowDiscountReaderResult.getModAssetInfoDto().getFinprodId());
                            }
                        }
                        //处理曲线信息
                        ModCurveConfig modCurveConfig = cashFlowDiscountReaderResult.getModCurveConfig();
                        Map<String, Object> paramCurveCode = new HashMap<>();
                        paramCurveCode.put("curveGroupCode", modCurveConfig.getCurveGroupCode());
                        if (StringUtil.equals(modCurveConfig.getCurveType(), DictCodeList.收益率曲线类型_SP即期.getValue())) {
                            paramCurveCode.put("curveType", "SP");
                        } else if (StringUtil.equals(modCurveConfig.getCurveType(), DictCodeList.收益率曲线类型_MA到期.getValue())) {
                            paramCurveCode.put("curveType", "MA");
                        } else if (StringUtil.equals(modCurveConfig.getCurveType(), DictCodeList.收益率曲线类型_FS远期的即期.getValue())) {
                            paramCurveCode.put("curveType", "FS");
                        } else if (StringUtil.equals(modCurveConfig.getCurveType(), DictCodeList.收益率曲线类型_FM远期的到期.getValue())) {
                            paramCurveCode.put("curveType", "FM");
                        }else{
                            logger.error("现金流贴现模型配置的曲线类型不正确，曲线类型配置为："+modCurveConfig.getCurveType());
                        }
                        if (null != rateInfo) {
                            paramCurveCode.put("graderesult", rateInfo.getRating());
                        }else if(null!=modCurveConfig.getBottomGrade()){
                            //设置兜底评级
                            rateInfo = new ViewRatingInfoDto();
                            rateInfo.setRating(modCurveConfig.getBottomGrade());
                            paramCurveCode.put("graderesult", modCurveConfig.getBottomGrade());
                        }
                        List<String> curveCode = cashFlowDiscountMapper.getCurveCode(paramCurveCode);
                        if(CollectionUtils.isEmpty(curveCode)){
                            modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),item.getFinprodId(), "【现金流贴现模型批处理】：找不到曲线，无法继续.");
                        }else{
                            cashFlowDiscountReaderResult.setViewRatingInfoDto(rateInfo);
                            cashFlowDiscountReaderResult.setCurveCodes(curveCode);
                            result.add(cashFlowDiscountReaderResult);
                        }
                    }else{
                        modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),item.getFinprodId(), "【现金流贴现模型批处理】：现金流贴现模型缺少配置，无法继续.");
                    }
                });
            }
        }

        return result;
    }
}

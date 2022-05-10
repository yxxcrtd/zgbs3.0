/*
 * 文件名：RiskTrialDayTimeReaderService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年9月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.risktrialrds.service;

import org.apache.commons.lang.StringUtils;
import com.joyintech.batch.core.dto.SysBatchExecInfoDto;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.PtlPortfolioValuation;
import com.joyintech.tams.common.base.entity.RskIndividAssetDel;
import com.joyintech.tams.common.base.entity.RskIndividSpecialStage;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.redisinit.dto.ImplIndicator;
import com.joyintech.tams.invest.common.redisinit.service.RiskIndConditionLineService;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndividLimit;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.risktrialrds.dto.RiskTrialDayTimeReaderResult;
import com.joyintech.tams.invest.common.risktrialrds.dao.RiskTrialCommMapper;
import com.joyintech.tams.invest.common.risktrialrds.dto.*;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.joyintech.tams.common.base.entity.FinOpenPlan;

/**
 * 日间批处理生成reader相关业务处理
 * @author 裴家俊
 * @version 1.0 2019年9月2日
 * @see RiskTrialDayTimeReaderService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class RiskTrialDayTimeReaderService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private RiskTrialCommMapper riskTrialCommMapper;

    private Date supDate;

    // add by zhangrui1 自定义条件阈值 start
    @Autowired
    private RiskIndConditionLineService lineService;
    // add by zhangrui1 自定义条件阈值 end

    /**
     * 获取待处理数据
     * @param param 传递参数
     * @return List<RiskTrialDayTimeReaderResult>
     * @since 1.0
     */
    public List<RiskTrialDayTimeReaderResult> execute(Map<String, Object> param) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        SysBatchExecInfoDto dto = (SysBatchExecInfoDto)param.get(BatchConstant.BATCH_INFO);
        supDate=(Date) param.get(BatchConstant.PLATE_DATE);
        List<RiskTrialDayTimeReaderResult> resultList = new ArrayList<RiskTrialDayTimeReaderResult>();
        List<String> batchPortList=(List<String>) param.get(Const.PROD_LIST);

        logger.info("---------->指标计算批处理数据生产任务开始");
        // 获取指标任务集合
        InvestTradeTrialInput investTradeTrialInput = new InvestTradeTrialInput();
        investTradeTrialInput.setIsEnabled(BussinessDictCodeListRds.使用状态_启用.getValue());
        investTradeTrialInput.setTimePoint(BussinessDictCodeListRds.监督时分_事后监控.getValue());
        investTradeTrialInput.setSupFrequency(param.get(Const.SUP_FREQUENCY).toString());
        investTradeTrialInput.setValDate(DateUtil.format(supDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
        if(batchPortList.size()>0&&batchPortList.size()!=riskTrialCommMapper.selectAllPortfolioWithRisk()){
            Map<String,Object> paramInitMap = new HashMap<String,Object>();
            paramInitMap.put("productIdList", batchPortList);
            List<String> newPortList=riskTrialCommMapper.getPortfolioIdInFinProduct(paramInitMap);
            for(String portCodeSg:newPortList) {
                investTradeTrialInput.setPortfolioCode(portCodeSg);
                dealBasicProducer(investTradeTrialInput,resultList);
            }
        }
        else{
            dealBasicProducer(investTradeTrialInput,resultList);
        }
        //获取所要计算指标的指标参数，减少查询参数时间，提交效率
       String paramCode=StringUtil.EMPTY;
       Set<String> paramSetList=new HashSet<>();
       if(!StringUtil.isEmptyList(resultList)){
           resultList.stream().forEach(item->{
               // 调用基础算法运算符
               if(StringUtil.isNotEmpty(item.getTaskQueue())&&StringUtil.isNotEmpty(item.getTaskQueue().getPoint())) {
                   ExprTreeMath exprTree = new ExprTreeMath(item.getTaskQueue().getPoint().getExpr());
                   Set<String> paramNames = exprTree.getParamList();
                   paramSetList.addAll(paramNames);
               }
           });
           paramCode=paramSetList.size()>0?RiskUtil.convertPortfCode(StringUtils.join(paramSetList,",")):StringUtil.EMPTY;
       }else {
           return resultList;
       }
       List<SupervisionParameterDto> supervisionParameterDtoList = riskTrialCommMapper.getIndicatorParamAll(investTradeTrialInput.getValDate(),paramCode,StringUtil.equals(investTradeTrialInput.getSupFrequency(),BussinessDictCodeListRds.监督频率_日终.getValue())?BussinessDictCodeListRds.参数粒度_事后_日终.getValue():BussinessDictCodeListRds.参数粒度_事后_日间.getValue());

        //查询个性化设置相关信息start--
        List<RskIndividSpecialStage> rskIndividSpecialStageList=riskTrialCommMapper.getRiskIndividSpecialStage(investTradeTrialInput.getValDate(), BussinessDictCodeListRds.阶段类型_特殊阶段.getValue());
        List<RskIndividSpecialStage> rskIndividSpecialStageAstList=riskTrialCommMapper.getRiskIndividSpecialStage(investTradeTrialInput.getValDate(), BussinessDictCodeListRds.阶段类型_特殊资产阶段.getValue());
        List<RskIndividAssetDel> rskIndividAssetDelList=riskTrialCommMapper.getRiskIndividAstDel(investTradeTrialInput.getValDate());
        // add by zhangrui1 自定义条件阈值 start
        Map<String, Map<String, IIndividLimit>> limitMap = lineService.getRiskIndConditionLine(
            DateUtil.parse(investTradeTrialInput.getValDate()), null);
        List<RskIndividSpecialStage> temp = new ArrayList<>();
        if (!CollectionUtils.isEmpty(limitMap)) {
            limitMap.forEach((key, value) -> {
                if (!CollectionUtils.isEmpty(value)) {
                    value.entrySet().forEach(en -> {
                        IIndividLimit limit = en.getValue();
                        if (null != limit) {
                            RskIndividSpecialStage stage = new RskIndividSpecialStage();
                            stage.setApproveCond(limit.getApproveCond());
                            stage.setWarnCond(limit.getWarnCond());
                            stage.setFoulCond(limit.getFoulCond());
                            stage.setIndId(key);
                            stage.setPrtBagId(en.getKey());
                            temp.add(stage);
                        }
                    });
                }
            });
        }
        if (StringUtil.isEmptyList(rskIndividSpecialStageList)) {
            rskIndividSpecialStageList = temp;
        }
        else if (!StringUtil.isEmptyList(temp)) {
            for (RskIndividSpecialStage stage : rskIndividSpecialStageList) {
                Iterator<RskIndividSpecialStage> it = temp.iterator();
                while (it.hasNext()) {
                    RskIndividSpecialStage item = it.next();
                    if (null == item
                        || (StringUtil.equals(item.getIndId(), stage.getIndId())
                            && StringUtil.equals(item.getPrtBagId(), stage.getPrtBagId()))) {
                        it.remove();
                    }
                }
            }
            if (!StringUtil.isEmptyList(temp)) {
                rskIndividSpecialStageList.addAll(temp);
            }
        }
        // add by zhangrui1 自定义条件阈值 end
        for(RiskTrialDayTimeReaderResult riskTrialDto:resultList){
            riskTrialDto.setMapOther(Const.SUP_PARAM,supervisionParameterDtoList);
            riskTrialDto.setMapOther(Const.IND_SPE_STAGE,rskIndividSpecialStageList);
            riskTrialDto.setMapOther(Const.IND_SPE_STAGE_AST,rskIndividSpecialStageAstList);
            riskTrialDto.setMapOther(Const.IND_AST_DEL,rskIndividAssetDelList);
        }
        //查询个性化设置相关信息end--
        logger.info("---------->指标计算批处理数据生产任务结束");
        return resultList;
    }

    /**
     * 获取组合关联指标加入任务队列
     * @param investTradeTrialInput
     */
    private void dealBasicProducer(InvestTradeTrialInput investTradeTrialInput,List<RiskTrialDayTimeReaderResult> resultList){
        List<PtlPortfolioValuation>  riskPortfValList=new ArrayList<>();
        //---流动性风险改造 start---
        List<FinOpenPlan>  riskPortOpenPlanList=new ArrayList<>();
        //---流动性风险改造 end---
        investTradeTrialInput.setNextDate(DateUtil.addDay(DateUtil.parse(investTradeTrialInput.getValDate()),1));
        List<SupervisionTargetDto> taskList = riskTrialCommMapper.getPortfIndTask(investTradeTrialInput);
        //查询所有的个性化条件设置信息
        List<RiskIndividConditionDto>  riskIndividConditionDtoList =  riskTrialCommMapper.getRiskIndividCondition();
        if(!StringUtil.isEmptyList(riskIndividConditionDtoList)){
            riskPortfValList =  riskTrialCommMapper.getPortfValuation(investTradeTrialInput.getValDate());
            riskPortOpenPlanList =  riskTrialCommMapper.getPortfOpenPlan(investTradeTrialInput.getValDate());
        }
        for(SupervisionTargetDto portfolioPoint : taskList) {
            RiskTrialDayTimeReaderResult riskTrialDayTimeReaderResult=new RiskTrialDayTimeReaderResult();
           /* String[] portCount=portfolioPoint.getPortfolioLevel().equals(BussinessDictCodeList.组合层次_组合包.getValue())?portfolioPoint.getPortfConstId().split(","):portfolioPoint.getPortfolioId().split(",");
            Map<String, Object> param = new HashMap<String, Object>();
            List<List<String>> portList= RiskUtil.splitList(portCount, 1000);
            param.put("portfolioId", portList);
            int reCount=riskTrialCommMapper.getCountWithBasicPortf(param);
            if(portCount.length!=reCount){
                logger.warn("组合包中的组合在组合表出现不存在的情况，可能是该组合已被删除或到期了");
                continue;
            }*/
            Task task = new Task();
            task.setDate(supDate);
            task.setPortfolioId(portfolioPoint.getPortfolioId());
            task.setPoint(portfolioPoint);

            //判断是否设置条件设置,排除组合包
           if(portfolioPoint.getPortfolioLevel().equals(BussinessDictCodeListRds.组合层次_单组合.getValue())) {
               List<RiskIndividConditionDto>  riskIndividCondSubList =riskIndividConditionDtoList.stream().filter(item->item.getPrtBagId().equals(portfolioPoint.getPortfolioId())&&item.getIndId().equals(portfolioPoint.getIndId())).collect(Collectors.toList()) ;
               List<PtlPortfolioValuation> ptlPortValList=riskPortfValList.stream().filter(item->item.getPortfolioId().equals(portfolioPoint.getPortfolioId())).collect(Collectors.toList());
               boolean divZcFlag=true; boolean divDwFlag=true;boolean divJcFlag=false;boolean divQyFlag=false;
               for(RiskIndividConditionDto dto:riskIndividCondSubList){
                  /* if(dto.getConditionItem().equals(BussinessDictCodeListRds.条件项_资产净值控制.getValue())&&!StringUtil.isEmptyList(ptlPortValList)){
                       divZcFlag =  RiskUtil.compareNumberVal("NUM",dto.getControlType(), ArithmeticUtil.multiply(dto.getValueStr(),new BigDecimal(10000)),ArithmeticUtil.multiply(dto.getValueEnd(),new BigDecimal(10000)),ptlPortValList.get(0).getTdyAssetNetvalue());
                   }
                   if(dto.getConditionItem().equals(BussinessDictCodeListRds.条件项_单位净值控制.getValue())&&!StringUtil.isEmptyList(ptlPortValList)){
                       divDwFlag = RiskUtil.compareNumberVal("NUM",dto.getControlType(),dto.getValueStr(),dto.getValueEnd(),ptlPortValList.get(0).getTdyUnitValue());
                   }*/
                   //建仓
                   if(dto.getConditionItem().equals(BussinessDictCodeListRds.条件项_建仓期时间.getValue())){
                       //在建仓期期间不计算
                       divJcFlag = RiskUtil.compareNumberVal("DATE",BussinessDictCodeListRds.控制类型_范围控制.getValue(),dto.getValueStr(),dto.getValueEnd(),supDate);
                   }
               }
               //---流动性风险改造 start---
               divQyFlag=RiskUtil.filterEnableTime(riskIndividConditionDtoList,riskPortOpenPlanList,portfolioPoint.getPortfolioId(),portfolioPoint.getIndId(),supDate);
               //---流动性风险改造 end---
               if(!divZcFlag||!divDwFlag||divJcFlag||!divQyFlag){
                   continue;//跳过当前，执行下一个
               }
            }
           riskTrialDayTimeReaderResult.setTaskQueue(task);
           resultList.add(riskTrialDayTimeReaderResult);
        }
    }

}

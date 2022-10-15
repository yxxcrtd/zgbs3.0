package com.joyintech.tams.invest.batch.risktrialrds.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.batch.service.BatchCoreService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.common.base.entity.PtlPortfolioValuation;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.risktrialrds.dto.RiskTrialDayTimeReaderResult;
import com.joyintech.tams.invest.batch.risktrialrds.service.RiskTrialDayTimeCleanService;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risktrialrds.dao.RiskTrialCommMapper;
import com.joyintech.tams.invest.common.risktrialrds.dto.InvestTradeTrialInput;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskIndividConditionDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.SupervisionTargetDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.Task;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 何阳阳
 * @date 2022/4/25
 */
@Service("riskTrialDayEndReader")
@Scope("prototype")
public class RiskTrialDayEndReader extends BaseReader<RiskTrialDayTimeReaderResult> {

    public static final int LOOP_PROD_COUNT = 100;
    private Logger logger = LoggerFactory.getLogger(RiskTrialDayEndReader.class);

    @Autowired
    private BatchCoreService batchCoreService;

    @Autowired
    private RiskTrialDayTimeCleanService cleanService;

    private RiskTrialCommMapper riskTrialCommMapper;
    //查询所有的个性化条件设置信息
    private List<RiskIndividConditionDto> riskIndividConditionDtoList;

    public RiskTrialDayEndReader(RiskTrialCommMapper riskTrialCommMapper) {
        this.riskTrialCommMapper = riskTrialCommMapper;
        this.riskIndividConditionDtoList = riskTrialCommMapper.getRiskIndividCondition();
    }

    @Override
    public List<RiskTrialDayTimeReaderResult> createList() {
        Date platDate = (Date)super.getParams().get(BatchConstant.PLATE_DATE);
        List<String> batchPortList = (List<String>) super.getParams().get(Const.PROD_LIST);
        String portfolioId = (String) super.getParams().get("portfolioId");
        String indId = (String) super.getParams().get("indId");
        return createDataList(platDate, batchPortList, portfolioId, indId);
    }

    protected List<RiskTrialDayTimeReaderResult> createDataList(Date platDate, List<String> prodList, String portfolioIdGroup, String indIdGroup) {
        InvestTradeTrialInput investTradeTrialInput = new InvestTradeTrialInput();
        investTradeTrialInput.setIsEnabled(BussinessDictCodeListRds.使用状态_启用.getValue());
        investTradeTrialInput.setTimePoint(BussinessDictCodeListRds.监督时分_事后监控.getValue());
        investTradeTrialInput.setSupFrequency(BussinessDictCodeListRds.监督频率_日终.getValue());
        investTradeTrialInput.setValDate(DateUtil.format(platDate));

        List<RiskTrialDayTimeReaderResult> resultList = new ArrayList<>();
        logger.info("---------->日终指标计算批的日期为：{}, 产品数为: {}", investTradeTrialInput.getValDate(), prodList.size());
        if(prodList.size() < LOOP_PROD_COUNT){
            List<String> newPortList;
            if(StringUtils.isNotEmpty(portfolioIdGroup)) {
                // 查询组合
                newPortList = batchCoreService.findAllProdIdByProdGroupId(new String[]{portfolioIdGroup});
            }
            else {
                Map<String,Object> paramInitMap = new HashMap<>(1);
                paramInitMap.put("productIdList", prodList);
                newPortList = riskTrialCommMapper.getPortfolioIdInFinProduct(paramInitMap);
            }
            if(StringUtils.isNotEmpty(indIdGroup)) {
                investTradeTrialInput.setIndCodeList(batchCoreService.findAllProdIdByProdGroupId(new String[]{indIdGroup}));
            }
            logger.info("---------->组合 {}", StringUtils.join(newPortList, GlobalConstant.COMMA));
            investTradeTrialInput.setPortfolioCodeList(newPortList);
            resultList.addAll(dealBasicProducer(investTradeTrialInput));
            cleanService.removeDailyData(platDate, new ArrayList<>(resultList));
        }
        else{
            logger.info("---------->跑的是全部产品");
            resultList.addAll(dealBasicProducer(investTradeTrialInput));
            cleanService.removeDailyDataNew(platDate);
        }
        //查询个性化设置相关信息end--
        logger.info("---------->指标计算批处理数据生产任务结束，指标数量：{}", resultList.size());
        return resultList;
    }

    /**
     * 获取组合关联指标加入任务队列
     * @param investTradeTrialInput
     */
    private List<RiskTrialDayTimeReaderResult> dealBasicProducer(InvestTradeTrialInput investTradeTrialInput){
        List<RiskTrialDayTimeReaderResult> resultList = new ArrayList<>();
        Date supDate = DateUtil.parse(investTradeTrialInput.getValDate());
        investTradeTrialInput.setNextDate(DateUtil.addDay(supDate,1));
        List<SupervisionTargetDto> taskList = riskTrialCommMapper.getPortfIndTask(investTradeTrialInput);

        List<PtlPortfolioValuation>  riskPortfValList=new ArrayList<>();
        //---流动性风险改造 start---
        List<FinOpenPlan>  riskPortOpenPlanList=new ArrayList<>();
        //---流动性风险改造 end---
        if(!StringUtil.isEmptyList(riskIndividConditionDtoList)){
            riskPortfValList =  riskTrialCommMapper.getPortfValuation(investTradeTrialInput.getValDate());
            riskPortOpenPlanList =  riskTrialCommMapper.getPortfOpenPlan(investTradeTrialInput.getValDate());
        }

        for(SupervisionTargetDto portfolioPoint : taskList) {
            RiskTrialDayTimeReaderResult riskTrialDayTimeReaderResult = new RiskTrialDayTimeReaderResult();
            String uuid = UUID.randomUUID().toString();
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
        return resultList;
    }
}

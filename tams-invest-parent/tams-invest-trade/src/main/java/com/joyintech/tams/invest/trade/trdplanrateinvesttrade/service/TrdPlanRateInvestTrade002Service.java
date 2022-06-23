/*
 * 文件名：TrdPlanRateInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.trdplanrateinvesttrade.service;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashCalcRuleDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.dto.PtlRecePositionFlowDto;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.calculator.CalendarUtil;
import com.joyintech.tams.invest.common.calculator.InstanceOfClass;
import com.joyintech.tams.invest.common.calculator.InterestSubAmtCal;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.component.base.trade.impl.dao.BaseTradeProductDealMapper;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.MstFileOutputParameter;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.threadservice.JasperUtil;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.TrdCommonBaseMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.trdplanrateinvesttrade.dto.TrdPlanRateInvestTradeDto;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.common.trade.AllTradeCommonUtil.copyFileInfoByFileNo;

/**
 * 计划类资产投资交易新建
 *
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see TrdPlanRateInvestTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdPlanRateInvestTrade002Service extends CommonExportService<TrdPlanRateInvestTradeDto>
        implements BaseProcessService<TrdPlanRateInvestTradeDto> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 日期推算Service
     */
    @Autowired
    private DateCalService dateCalService;
    /**
     * 基础Mapper代理Service
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * 划款指令Service
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * 金融产品交易实体类
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    /**
     * 投资交易共通类
     */
    @Autowired(required = false)
    private TradeCommonService tradeCommonService;
    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    // 调用投管接口 add by renping 20190205 end

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;


    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private FinProductMapper finProductMapper;
    /**
     * 交易及存续期交易，保存进待办，点批量审批时候检查service
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private RepTradeMapper repTradeMapper;

    @Autowired
    private RepAssetDeptMapper repAssetDeptMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;

    @Autowired
    private TrdCommonBaseMapper trdCommonBaseMapper;
    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private BaseTradeProductDealService baseTradeProductDealService;
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private  PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;
    @Autowired
    private MstFileCommonService mstFileCommonService;
    @Autowired
    private BaseTradeProductDealMapper baseTradeProductDealMapper;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;
    
    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    
    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.MASERT)
    @Override
    public String checkForSubmit(FlowEntity entity, boolean isMutiApproval) {
        if (isMutiApproval) {
            // 业务编号
            String tradeId = entity.getTradeId();
            if (!multiApproveTradeCheckService.multiApproveTradeCheck(tradeId)) {
                return getMessage("BUSS_COM_E_0100", tradeId);
            }
        }
        return null;
    }

    /**
     * 资产代码下拉框
     *
     * @return 资产代码下拉框集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getFinprodMarketId(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdPlanRateInvestTrade/getFinprodMarketId", map, SelectOptionDto.class);
    }

    /**
     * 获取金融产品类型
     *
     * @param map 请求参数集合(金融产品代码)
     * @return 金融产品类型
     * @see
     * @since 1.0
     */
    public Map getFinprodType(Map<String, Object> map) {
        map.put("tradeDate", iPlatformDate.getSysDateStr());
        map.put("rateType", DictCodeListExt1.现金流二级类型_利息.getValue());
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdPlanRateInvestTrade/getFinprodType", map, Map.class);
    }

    /**
     * 交割日推算
     *
     * @param map 推算参数,交易日、清算速度、金融产品代码
     * @return 交割日字符串
     * @see
     * @since 1.0
     */
    public Map<String, String> settleDateCalcu(Map<String, Object> map) {
        String calendarId = "";
        Date settleDate = null;//交割日
        Date tradeDate = DateUtil.parse(map.get("tradeDate").toString());
        Boolean isHoliday = false;
        Map<String, String> resultMap = new HashMap<String, String>();
        if (bussCommonMapperDelegateService != null) {
            FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, map.get("finprodId").toString(), FinProduct.class);
            if (finProduct != null) {
                calendarId = finProduct.getCalendarId();
                if (calendarId != null) {
                    isHoliday = dateCalService.isDealCalendarHoliday(tradeDate, calendarId);
                    List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(map.get("tradeDate").toString()), DateUtil.addDay(DateUtil.parse(map.get("tradeDate").toString()), 15), calendarId);
                    if (map.get("settleSpeed") != null) {
                        if (StringUtil.equals(map.get("settleSpeed").toString(), DictCodeList.清算速度_T_0.getValue())) {
                            settleDate = tradeDate;
                        } else if (StringUtil.equals(map.get("settleSpeed").toString(), DictCodeList.清算速度_T_1.getValue())) {
                            settleDate = dateCalService.calculationTradingDay(tradeDate, 1, holidayList, "1");
                        } else if (StringUtil.equals(map.get("settleSpeed").toString(), DictCodeList.清算速度_T_2.getValue())) {
                            settleDate = dateCalService.calculationTradingDay(tradeDate, 2, holidayList, "1");
                        }
                    }
                }
            }
        }
        resultMap.put("calendarId", calendarId);
        if (settleDate != null) {
            resultMap.put("settleDate", DateUtil.format(settleDate));
        } else {
            resultMap.put("settleDate", "");
        }
        if (isHoliday.equals(false)) {
            resultMap.put("isHoliday", DictCodeList.是否_否.getValue());
        } else {
            resultMap.put("isHoliday", DictCodeList.是否_是.getValue());
        }
        return resultMap;
    }

    /**
     * 分段利息计算
     *
     * @param map 请求参数
     * @return
     * @see
     * @since 1.0
     */
    public String getIntAmtCalcu(Map<String, Object> map) {
        String intAmtStr = "";
        BigDecimal intAmt = new BigDecimal("0");
        FinCashPlan finCashPlan = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                        + "/TrdPlanRateInvestTrade/getLastVDate", map,
                FinCashPlan.class);
        Date settleDate = DateUtil.parse(map.get("settleDate").toString());
        if (finCashPlan == null || settleDate.compareTo(finCashPlan.getVdate()) < 0) {
            return "0";
        }
//        List<FinCashCalcRuleDto> finCashCalcRuleList = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
//                        + "/TrdPlanRateInvestTrade/getFinCashCalcRule", map,
//                FinCashCalcRuleDto.class);
        
        map.put("vDate", DateUtil.format(finCashPlan.getVdate()));
        List<FinCashCalcRuleDto> finCashCalcRuleList = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
              + "/TrdPlanRateInvestTrade/getFinCashCalcRule", map,
        FinCashCalcRuleDto.class);
        map.put("cashId", finCashPlan.getCashId());
        FinCashPayRule isr = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                        + "/TrdPlanRateInvestTrade/getFinCashPayRule", map,
                FinCashPayRule.class);
        FinProduct finProduct = finProductMapper.selectByPrimaryKey((String) map.get("finprodId"));
        FinCashPlanDto finCashPlanDto = new FinCashPlanDto();
        finCashPlanDto.setVdate(finCashPlan.getVdate());
        finCashPlanDto.setMdate(settleDate);
        //wangyang1 2020年4月1日  start TISHKBS-63 项目类资产利率调整后投资交易没有按照新报价计算利息
//        Map<Date,List<FinCashCalcRuleDto>> finCashCalcRuleMap= 
//            finCashCalcRuleList.stream().collect(JoyinCollectors.groupingBy(item->item.getEffDate()));
//        List<Date> effDateList = new ArrayList<Date>(finCashCalcRuleMap.keySet());
//        Collections.sort(effDateList, new Comparator<Date>() {
//            public int compare(Date d1, Date d2) {
//                return DateUtil.compareDate(d1, d2);
//            }
//        });
//        List<FinCashPlanDetailDto> finCashPlanDetailList = new ArrayList<>(); 
//        for (int i = 0; i < effDateList.size(); i++ ) { 
//            FinCashPlanDetailDto finCashPlanDetailDto= new FinCashPlanDetailDto();
//            
//            if(i==0){
//            	finCashPlanDetailDto.setVdate(finCashPlan.getVdate());
//            }else{
//            	finCashPlanDetailDto.setVdate(effDateList.get(i));
//            }
//            finCashPlanDetailDto.setMdate(i < effDateList.size()-1?effDateList.get(i+1):settleDate);
//            finCashPlanDetailDto.setYield(finCashCalcRuleMap.get(effDateList.get(i)).get(0).getYield());
//            finCashPlanDetailDto.setIntPrinAmt(new BigDecimal(map.get("prinAmt").toString()));
//            finCashPlanDetailList.add(finCashPlanDetailDto);
//        }
        
        // START modify by wj 20200430 如果利率变更   取数据vdate mdate yield取值正确
        List<FinCashPlanDetailDto> finCashPlanDetailList = new ArrayList<>(); 
        FinCashPlanDetailDto detailDto= new FinCashPlanDetailDto();
        detailDto.setCashId(finCashPlan.getCashId());
        detailDto.setVdate(finCashPlan.getVdate());
        detailDto.setIntPrinAmt(new BigDecimal(map.get("prinAmt").toString()));
        
        
        for (FinCashCalcRuleDto ruleDto : finCashCalcRuleList) {
			
        	if (ruleDto.getEffDate().compareTo(finCashPlan.getVdate()) <= 0) {
        		detailDto.setYield(ruleDto.getYield().setScale(12, RoundingMode.HALF_UP));
            }
            else if (ruleDto.getEffDate().compareTo(finCashPlan.getVdate()) > 0 
                && ruleDto.getEffDate().compareTo(finCashPlan.getMdate()) < 0) {
            	detailDto.setMdate(ruleDto.getEffDate());
                finCashPlanDetailList.add(detailDto);
                
                detailDto= new FinCashPlanDetailDto();
                detailDto.setIntPrinAmt(new BigDecimal(map.get("prinAmt").toString()));
                detailDto.setCashId(finCashPlan.getCashId());
                detailDto.setVdate(ruleDto.getEffDate());
                detailDto.setYield(ruleDto.getYield().setScale(12, RoundingMode.HALF_UP));
            }
            else if(ruleDto.getEffDate().compareTo(finCashPlan.getMdate()) >= 0) {
                break;
            }
        	
		}
        detailDto.setMdate(settleDate);
        finCashPlanDetailList.add(detailDto);
        
        // END modify by wj 20200430 如果利率变更   取数据vdate mdate yield取值正确

        

        AssetCommonInfoDto scai = new AssetCommonInfoDto();
        scai.setBasis(finCashCalcRuleList.get(0).getBasis());
        scai.setIntpayRule(DictCodeListExt1.利息分配方式_按照实际天数分配.getValue());
        scai.setVdate(finProduct.getVdate());
        scai.setMdate(finProduct.getMdate());

        try {
            
            for (FinCashPlanDetailDto finCashPlanDetailDto : finCashPlanDetailList) {
                intAmt = ArithmeticUtil.doAdd(intAmt,InterestSubAmtCal.cal(finCashPlanDto, finCashPlanDetailDto, isr, scai))  ;
            }
            
           
        } catch (ParseException e) {
            logger.error("计算利息失败", e);
        }
        //wangyang1 2020年4月1日  end
        DecimalFormat df2 = new DecimalFormat("####################0.0000000#");
        intAmtStr = df2.format(ArithmeticUtil.round(intAmt, 6, RoundingMode.HALF_UP));
        return intAmtStr;
    }

    /**
     * 合成初始化实体类
     *
     * @param tradeId 交易编号
     * @return 计划类资产实体类
     * @see
     * @since 1.0
     */
    public TrdPlanRateInvestTradeDto getInitEntity(String tradeId) {
        TrdPlanRateInvestTradeDto entity = new TrdPlanRateInvestTradeDto();
        TrdFinProductDealTotal trdFinProductDealTotal = null;
        TrdProductDeal trdProductDeal = null;
        TrdDealTotalAdd trdDealTotalAdd = null;
        String finprodId = null;
        FinProduct finProduct = null;
        TrdInvestPersonInfo002Dto trdInvestPersonInfo002Dto = new TrdInvestPersonInfo002Dto();
        if (bussCommonMapperDelegateService != null) {
            trdFinProductDealTotal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(TrdFinProductDealTotalMapper.class, tradeId, TrdFinProductDealTotal.class);
            if (trdFinProductDealTotal != null) {
                BeanUtil.copyPropertiesIgnoreNull(entity, trdFinProductDealTotal);
                if (!ObjectUtils.isEmpty(entity.getSettleSpeed())) {
                    entity.setSettleSpeed(entity.getSettleSpeed());
                }
                entity.setPrinAmt(trdFinProductDealTotal.getPrinAmt());
                entity.setFpriceAmt(trdFinProductDealTotal.getFpriceAmt());
                entity.setChannelFee(trdFinProductDealTotal.getFeeAmt());
                entity.setTradeAmt(trdFinProductDealTotal.getTradeAmt());
                if(!StringUtil.isNotEmpty(entity.getTradeAmt())) {
                	entity.setTradeAmt(new BigDecimal(0));
                }
                BeanUtil.copyPropertiesIgnoreNull(trdInvestPersonInfo002Dto, trdFinProductDealTotal);
                trdInvestPersonInfo002Dto.setContactInfo(trdFinProductDealTotal.getCounterContact());
                entity.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
                entity.setPortfolioId(trdFinProductDealTotal.getPortfolioId());
                trdDealTotalAdd = trdDealTotalAddMapper.selectByPrimaryKey(trdFinProductDealTotal.getTotleTradeId());
                if(trdDealTotalAdd!=null){
                    entity.setEntrustId(trdDealTotalAdd.getEntrustId());
                }
                finprodId = trdFinProductDealTotal.getFinprodId();
            }
            if (StringUtil.isNotEmpty(finprodId)) {
                finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, finprodId, FinProduct.class);
            }
            if (!ObjectUtils.isEmpty(finProduct)) {
                entity.setFinprodMarketId(finProduct.getFinprodMarketId());
            }
        }
        return entity;
    }

    /**
     * 获取交易录单信息
     *
     * @param entrustId 委托代码
     * @return 交易录单实体类
     * @see
     * @since 1.0
     */
    public TrdPlanRateInvestTradeDto getEtrInfo(String entrustId) {
        TrdPlanRateInvestTradeDto entity = new TrdPlanRateInvestTradeDto();
        EtrEntrust etrEntrust = null;
        EtrEntrustAdd etrEntrustAdd = null;
        String trader = tradeCommonService.getTrader(entrustId);
        if (bussCommonMapperDelegateService != null) {
            etrEntrust = bussCommonMapperDelegateService.selectBySinglePrimaryKey(EtrEntrustMapper.class, entrustId, EtrEntrust.class);
            etrEntrustAdd = bussCommonMapperDelegateService.selectBySinglePrimaryKey(EtrEntrustAddMapper.class, entrustId, EtrEntrustAdd.class);
        }
        if (etrEntrust != null) {
            FinProduct finProduct = null;
            FinProductType finProductType = null;
            TrdInvestPersonInfo002Dto trdInvestPersonInfo002Dto = new TrdInvestPersonInfo002Dto();
            String finprodId = etrEntrust.getFinprodId();
            BeanUtil.copyPropertiesIgnoreNull(entity, etrEntrust);
            BeanUtil.copyPropertiesIgnoreNull(trdInvestPersonInfo002Dto, etrEntrust);
            entity.setPrinAmt(etrEntrust.getEntrustNum());
            if (StringUtil.isNotEmpty(trader)) {
                trdInvestPersonInfo002Dto.setTrader(trader);
            }
            entity.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
//             entity.setTradeAmt(etrEntrust.getEntrustAmt());
            entity.setPrinAmt(etrEntrust.getEntrustAmt());
            if (StringUtil.isNotEmpty(etrEntrust.getEntrustNum())) {
                entity.setPrinAmt(etrEntrust.getEntrustNum());
            }

            //根据委托编号、交易编号查询委托完成额度ETR_FINISH_STATUS
            BigDecimal usedPriamt = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
                            + "/TrdPlanRateInvestTrade/queryActiveMandate?entrustId=" + entrustId,
                    BigDecimal.class);

            entity.setPrinAmt(entity.getPrinAmt().subtract(usedPriamt));

            if (StringUtil.isNotEmpty(finprodId) && bussCommonMapperDelegateService != null) {
                finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, finprodId, FinProduct.class);
                FinProductTypeKey key = new FinProductTypeKey();
                key.setFinprodId(finprodId);
                key.setBranch(0l);
                finProductType = bussCommonMapperDelegateService.selectByMultiPrimaryKey(FinProductTypeMapper.class, key,
                        FinProductType.class);
            }
            if (finProduct != null) {
                entity.setFinprodMarketId(finProduct.getFinprodMarketId());
                entity.setFinprodName(finProduct.getFinprodName());
            }
            if (finProductType != null) {
                entity.setFinprodType(finProductType.getType6());
            }
        }
        if (etrEntrustAdd != null && StringUtil.isNotEmpty(etrEntrustAdd.getSettleSpreed())) {
            entity.setMySettleSpeed("0" + (etrEntrustAdd.getSettleSpreed() + 1));
        }
        return entity;
    }


    /**
     * 金融产品实体类生成
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品实体类
     * @see
     * @since 1.0
     */
    public TrdProductDeal getEntity(TrdPlanRateInvestTradeDto dto) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto);
        BigDecimal channelFee=dto.getChannelFee();//通道费
        if(channelFee==null){
            channelFee=BigDecimal.ZERO;
        }
        BigDecimal tradeAmt=dto.getTradeAmt();//获取界面交易金额
        if(dto.getIspayUnpaidint().equals(DictCodeList.是否_是.getValue()) && StringUtil.isNotEmpty(dto.getPayUnpaidintAmt()) && dto.getPayUnpaidintAmt().compareTo(BigDecimal.ZERO)>0
                && dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())){
            tradeAmt=tradeAmt.subtract(dto.getPayUnpaidintAmt());
        }
        if (dto.getSettleSpeed() != null) {
            trdProductDeal.setSettleSpeed(dto.getSettleSpeed() - 1);
        }
        trdProductDeal.setUnitCprice(new BigDecimal("1"));
        // modify by wanshiwei ZGZGST-3595 计划类利率型资产“利率%”取值错误，取资产的计提利率 20210928 start
        trdProductDeal.setUnitInt(ArithmeticUtil.divide(dto.getInvestRate(), 100, 8));
        /*if (dto.getIntAmt() != null && dto.getTradeAmt() != null) {
            trdProductDeal.setUnitInt(ArithmeticUtil.divide(dto.getIntAmt(), tradeAmt, 8));
        }*/
        // modify by wanshiwei ZGZGST-3595 计划类利率型资产“利率%”取值错误，取资产的计提利率 20210928 end
        trdProductDeal.setUnitFprice(new BigDecimal("1"));
        trdProductDeal.setParValue(new BigDecimal("1"));
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
        trdProductDeal.setFinprodType("");
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        trdProductDeal.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue"))/*Long.valueOf(0)*/);
        trdProductDeal.setFpriceAmt(tradeAmt);//全价总额=本金+所有利息
        trdProductDeal.setIntAmt(dto.getIntAmt());
//           trdProductDeal.setSettleDate(dto.getSettleDate());
        trdProductDeal.setCpriceAmt(dto.getPrinAmt());
        trdProductDeal.setShareAmt(dto.getPrinAmt());
        trdProductDeal.setTradeAmt(tradeAmt);
        trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        trdProductDeal.setTrader(dto.getTrdInvestPersonInfo002Dto().getTrader());
        trdProductDeal.setCounterContact(dto.getTrdInvestPersonInfo002Dto().getContactInfo());
        trdProductDeal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
//        trdProductDeal.setIsOutTrade(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade());
        if (StringUtil.equals(DictCodeList.是否_否.getValue(), dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())) {
            dto.setIsOutTrade(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade());
        } else {
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
        }
        trdProductDeal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDeal.setFeeAmt(dto.getChannelFee());

      //2019-12-24 通道费改造 标的挂通道  通道费不参与计算
        trdProductDeal.setFeeAmtUnpay(dto.getChannelFee());
//        if(StringUtil.equals(DictCodeList.是否_否.getValue(),dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())){
//        	trdProductDeal.setFeeAmtPay(dto.getChannelFee());
//        }else{
//        	if(StringUtil.equals(dto.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())){
//            	trdProductDeal.setFeeAmtPay(dto.getChannelFee());
//            }else{
//            	trdProductDeal.setFeeAmtUnpay(dto.getChannelFee());
//            }
//        }

        return trdProductDeal;
    }



    /**
     * 金融产品实体类生成
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品实体类(主要生产划款指令)
     * @see
     * @since 1.0
     */
    public TrdProductDeal getOrder(TrdPlanRateInvestTradeDto dto) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto);
        BigDecimal channelFee=dto.getChannelFee();//通道费
        if(channelFee==null){
            channelFee=BigDecimal.ZERO;
        }
        BigDecimal tradeAmt=dto.getTradeAmt();//获取界面交易金额
        if (dto.getSettleSpeed() != null) {
            trdProductDeal.setSettleSpeed(dto.getSettleSpeed() - 1);
        }
        trdProductDeal.setUnitCprice(new BigDecimal("1"));
        // modify by wanshiwei ZGZGST-3595 计划类利率型资产“利率%”取值错误，取资产的计提利率 20210928 start
        trdProductDeal.setUnitInt(ArithmeticUtil.divide(dto.getInvestRate(), 100, 8));
        /*if (dto.getIntAmt() != null && dto.getTradeAmt() != null) {
            trdProductDeal.setUnitInt(ArithmeticUtil.divide(dto.getIntAmt(), tradeAmt, 8));
        }*/
        // modify by wanshiwei ZGZGST-3595 计划类利率型资产“利率%”取值错误，取资产的计提利率 20210928 end
        trdProductDeal.setUnitFprice(new BigDecimal("1"));
        trdProductDeal.setParValue(new BigDecimal("1"));
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
        trdProductDeal.setFinprodType("");
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        trdProductDeal.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue"))/*Long.valueOf(0)*/);
        trdProductDeal.setFpriceAmt(tradeAmt.subtract(channelFee));//全价总额=本金+所有利息
        trdProductDeal.setIntAmt(dto.getIntAmt());
        trdProductDeal.setCpriceAmt(dto.getPrinAmt());
        trdProductDeal.setShareAmt(dto.getPrinAmt());
        trdProductDeal.setTradeAmt(tradeAmt);
        trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        trdProductDeal.setTrader(dto.getTrdInvestPersonInfo002Dto().getTrader());
        trdProductDeal.setCounterContact(dto.getTrdInvestPersonInfo002Dto().getContactInfo());
        trdProductDeal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        if (StringUtil.equals(DictCodeList.是否_否.getValue(), dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())) {
            dto.setIsOutTrade(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade());
        } else {
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
        }
        trdProductDeal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDeal.setFeeAmt(dto.getChannelFee());
        if (StringUtil.equals(DictCodeList.是否_否.getValue(), dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())) {
            trdProductDeal.setFeeAmtPay(dto.getChannelFee());
        } else {
            if (StringUtil.equals(dto.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())) {
                trdProductDeal.setFeeAmtPay(dto.getChannelFee());
            } else {
                trdProductDeal.setFeeAmtUnpay(dto.getChannelFee());
            }
        }
        return trdProductDeal;
    }

    /**
     * 生成金融产品附属表
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品附属类
     * @see
     * @since 1.0
     */
    public TrdProductDealAdd setTrdProductDealAdd(TrdPlanRateInvestTradeDto dto) {
        TrdProductDealAdd addDto = new TrdProductDealAdd();
        addDto.setTradeId(dto.getTradeId());
        addDto.setPortfolioId(dto.getPortfolioId());
        addDto.setEntrustId(dto.getEntrustId());
        //addDto.setProdId(dto.getFinprodId());
        addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return addDto;
    }


    /**
     * 生成金融产品交易汇总附属表
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品附属类
     * @see
     * @since 1.0
     */
    public TrdDealTotalAdd setTrdDealTotalAdd(TrdPlanRateInvestTradeDto dto) {
        TrdDealTotalAdd addDto = new TrdDealTotalAdd();
        addDto.setTotleTradeId(dto.getTotleTradeId());
        addDto.setEntrustId(dto.getEntrustId());
        addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return addDto;
    }

    /**
     * 组合交易表
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 组合交易实体类
     * @see
     * @since 1.0
     */
    public List<PtlTrade> setPtlTrade(TrdPlanRateInvestTradeDto dto) {
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
        ptlTrade.setSettleDate(dto.getTradeDate());
        ptlTrades.add(ptlTrade);

        if (!dto.getPortfolioId().equals(dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrade1.setBusiNo(dto.getTradeId());
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setBusiId(dto.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
            ptlTrade1.setSettleDate(dto.getTradeDate());
            ptlTrades.add(ptlTrade1);
        }
        return ptlTrades;
    }

    /**
     * 组合交易表
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 组合交易实体类
     * @see
     * @since 1.0
     */
    public List<PtlTrade> setRecePtlTrade(TrdPlanRateInvestTradeDto dto,String tradeId) {
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(tradeId);
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
        ptlTrade.setSettleDate(dto.getTradeDate());
        ptlTrades.add(ptlTrade);

        if (!dto.getPortfolioId().equals(dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrade1.setBusiNo(tradeId);
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setBusiId(dto.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
            ptlTrade1.setSettleDate(dto.getTradeDate());
            ptlTrades.add(ptlTrade1);
        }
        return ptlTrades;
    }

    /**
     * 金融产品交易扩展
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品交易附属表实体类
     * @see
     * @since 1.0
     */
    public PaTrdProductDeal setPaTrdProductDeal(TrdPlanRateInvestTradeDto dto) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId());
        if (dto.getPs() != null && !dto.getPs().isEmpty()) {
            // 一体化对应etliu start
//              if(dto.getPs().equals(DictCodeList.交易方向_申购.getValue())){
            if (dto.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                // 一体化对应etliu end
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
                // 一体化对应etliu start
//              }else if(dto.getPs().equals(DictCodeList.交易方向_赎回.getValue())){
            } else if (dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                // 一体化对应etliu end
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());
            }
        }
        paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
        return paTrdProductDeal;
    }

    /**
     * 金融产品交易汇总扩展
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品交易汇总附属表实体类
     * @see
     * @since 1.0
     */
    public PaTrdFinProductDealTotal setPaTrdProductDealTotal(TrdPlanRateInvestTradeDto dto) {
        PaTrdFinProductDealTotal paTrdProductDeal = new PaTrdFinProductDealTotal();
        paTrdProductDeal.setTotleTradeId(dto.getTotleTradeId());
        if(dto.getPs()!=null && dto.getIspayUnpaidint()!=null && dto.getIspayUnpaidint().equals(DictCodeList.是否_是.getValue()) && dto.getPayUnpaidintAmt()!=null && dto.getPayUnpaidintAmt().compareTo(BigDecimal.ZERO)>0
                && dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
            paTrdProductDeal.setTheoryInt(dto.getPayUnpaidintAmt());//
        }
        if (dto.getPs() != null && !dto.getPs().isEmpty()) {
            if (dto.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
            } else if (dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());
            }
        }
        paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
        return paTrdProductDeal;
    }

    /**
     * 合成各实体类
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 计划类（利率型）投资交易实体类
     * @see
     * @since 1.0
     */
    public TrdPlanRateInvestTradeDto setEntity(TrdPlanRateInvestTradeDto dto) {
        List<BaseTradeProductDealDto> list = new ArrayList<BaseTradeProductDealDto>();
        FinProductTradeDto finDto = new FinProductTradeDto();
        BaseTradeProductDealDto baseDto = new BaseTradeProductDealDto();
        baseDto.setTrdFinProductDealTotal(this.getTrdTotalDeal(dto));//汇总交易表
        TrdProductDeal dealmodel=this.getEntity(dto);
        baseDto.setTrdProductDeal(dealmodel);//交易
        baseDto.setTrdProductDealAdd(this.setTrdProductDealAdd(dto));
        if (dto.getStlPayOrdersList() != null) {
            for (StlPayOrders sDto : dto.getStlPayOrdersList()) {
                sDto.setTradeId(dto.getTotleTradeId());
            }
        }
        baseDto.setStlPayOrdersList(dto.getStlPayOrdersList());
        baseDto.setTrdTradeFeeDetailList(setTrdTradeFeeDetails(dto));
        list.add(baseDto);
        finDto.setBaseTradeProductDealDtos(list);
        dto.setPtlTrade(this.setPtlTrade(dto));
        dto.setPaTrdProductDeal(this.setPaTrdProductDeal(dto));
        dto.setPaTrdFinProductDealTotal(this.setPaTrdProductDealTotal(dto));
        dto.setFinProductTradeDto(finDto);
        return dto;
    }

    private List<TrdTradeFeeDetail> setTrdTradeFeeDetails(TrdPlanRateInvestTradeDto dto) {
        List<TrdTradeFeeDetail> result = new ArrayList<>();
        TrdTradeFeeDetail trdTradeFeeDetail1 = new TrdTradeFeeDetail();
//        trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());

      //2019-12-24 通道费改造 标的挂通道  通道费不参与计算
//        if(StringUtil.equals(DictCodeList.是否_否.getValue(),dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())){
//        	trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());
//        }else{
//	        if(StringUtil.equals(dto.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())){
//	        	trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());
//	        }else{
	        	trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_否.getValue());
//	        }
//        }

        trdTradeFeeDetail1.setFeeAmt(dto.getChannelFee());
        trdTradeFeeDetail1.setTradeId(dto.getTradeId());
        trdTradeFeeDetail1.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue()); // 一次性费用类型
//        result.add(trdTradeFeeDetail1);
        if (StringUtil.isNotEmpty(trdTradeFeeDetail1.getFeeAmt())) {
            result.add(trdTradeFeeDetail1);
        }
        return result;
    }

    /**
     * 插入业务流程表
     *
     * @param entity 计划类实体类
     * @see
     * @since 1.0
     */
    public void beforeWorkFlow(TrdPlanRateInvestTradeDto entity) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size() > 0
                && entity.getBussTrdTransferOrder().get(0) != null && !entity.getBussTrdTransferOrder().get(0).toString().isEmpty()) {
            if (bussTrdTransferOrderService != null) {
                entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
            }
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, entity.getBussTrdTransferOrder().get(0));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            list.add(stlPayOrdersDto);
            entity.setStlPayOrdersList(list);
        }
        if (entity.getOperateType().equals("delete")) {
            FinProductTradeDto finProductTradeDto = entity.getFinProductTradeDto();
            List<BaseTradeProductDealDto> baseDto = finProductTradeDto.getBaseTradeProductDealDtos();
            for (BaseTradeProductDealDto baDto : baseDto) {
                WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, baDto.getTrdFinProductDealTotal());//汇总交易
                wflTrdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
                wflTrdFinProductDealTotal.setpTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                wflTrdFinProductDealTotal.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId() + "R");
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baDto.getTrdProductDeal());
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                wflTrdProductDeal.setpTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
                wflTrdProductDeal.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());//汇总交易编码
                WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                wflPaTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId());
                if (DictCodeList.交易方向_买入.getValue().equals(wflTrdProductDeal.getPs())) {
                    wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
                } else {
                    wflPaTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());
                }
                wflPaTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
                wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
                wflPaTrdFinDealTotal.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                if (DictCodeList.交易方向_买入.getValue().equals(wflTrdProductDeal.getPs())) {
                    wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
                } else {
                    wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());
                }
                wflPaTrdFinDealTotal.setDeptCode(LoginUserUtil.getDeptCode());
                wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDealAdd.getTradeId() + "R");
                wflTrdProductDealAdd.setPortfolioId(entity.getPortfolioId());
//                  bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,wflTrdProductDeal,WflPaTrdProductDealMapper.class,wflPaTrdProductDeal,WflTrdProductDealAddMapper.class,wflTrdProductDealAdd);

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
                TrdDealTotalAdd  totalAdd=trdDealTotalAddMapper.selectByPrimaryKey(baDto.getTrdFinProductDealTotal().getTotleTradeId());
                if(totalAdd!=null){
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, totalAdd);
                    wflTrdDealTotalAdd.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
                    wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                    wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
                }

                wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);//汇总交易
                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
                wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
                wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
                if (entity.getStlPayOrdersList() != null && entity.getStlPayOrdersList().size() > 0) {
                    for (StlPayOrdersDto stlPayOrdersDto : entity.getStlPayOrdersList()) {
                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                        wflStlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                        wflStlPayOrders.setTradeId(entity.getTotleTradeId() + "R");
                        if (wflStlPayOrders.getOrderId() != null) {
//                              bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class,wflStlPayOrders);
                            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                        }
                    }
                }
            }
        } else {
            TrdPlanRateInvestTradeDto dto = this.setEntity(entity);
            //TrdPlanRateInvestTradeDto dto = entity.getTrdPlanRateInvestTradeDto();
            // 调用授信共同 sjj start
            TrdProductDeal TrdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(TrdProductDeal, entity);
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            TrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            TrdProductDeal.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
            inputDto.setTrdProductDeal(TrdProductDeal);
            AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
            // 调用授信共同 sjj end

            FinProductTradeDto finProductTradeDto = dto.getFinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, this.getTrdTotalDeal(dto));
            wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdFinProductDealTotal.setWorkflowId(dto.getWorkflowId());
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, this.getEntity(dto));
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDeal.setWorkflowId(dto.getWorkflowId());
            //update by wangpeng 通道信息不登表
            wflTrdProductDeal.setChlId(null);
            //update by wnagpeng end
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, this.setTrdProductDealAdd(dto));
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
            wflTrdProductDealAdd.setWorkflowId(dto.getWorkflowId());
            wflTrdProductDealAdd.setPortfolioId(entity.getPortfolioId());
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, this.setPaTrdProductDeal(dto));
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdProductDeal.setWorkflowId(dto.getWorkflowId());
            wflPaTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId());
            WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, this.setPaTrdProductDealTotal(dto));
            wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdFinDealTotal.setWorkflowId(dto.getWorkflowId());
            wflPaTrdFinDealTotal.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
//              bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,wflTrdProductDeal,WflTrdProductDealAddMapper.class,
//                  wflTrdProductDealAdd,WflPaTrdProductDealMapper.class,wflPaTrdProductDeal);
            wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);//汇总交易
            wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);//汇总扩展
            wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
            wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);

            if (dto.getStlPayOrdersList() != null && dto.getStlPayOrdersList().size() > 0) {
                for (StlPayOrdersDto stlPayOrdersDto : dto.getStlPayOrdersList()) {
                    WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易状态_初始.getValue());
                    wflStlPayOrders.setWorkflowId(dto.getWorkflowId());
                    if (wflStlPayOrders.getOrderId() != null) {
//                          bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class,wflStlPayOrders);
                        wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
                    }
                }
            }
            if(dto.getTrdProductDealList()!=null && dto.getTrdProductDealList().size()>0){//应收款转入/转出多笔交易
                for(TrdProductDeal trdProductDeal:dto.getTrdProductDealList()){
                    WflTrdProductDeal wfldeal= new WflTrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(wfldeal, trdProductDeal);
                    wfldeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wfldeal.setWorkflowId(dto.getWorkflowId());
                    trdProductDeal.setWorkflowId(dto.getWorkflowId());

                    WflTrdProductDealAdd wflTrdDealAdd = new WflTrdProductDealAdd();
                    TrdProductDealAdd trdDealAdd = new TrdProductDealAdd();
                    trdDealAdd=this.setReceTrdProductDealAdd(dto.getPortfolioId(),
                            dto.getEntrustId(),wfldeal.getTradeId());
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdDealAdd, trdDealAdd);
                    wflTrdDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflTrdDealAdd.setWorkflowId(dto.getWorkflowId());
                    trdDealAdd.setWorkflowId(dto.getWorkflowId());
                    trdDealAdd.setPortfolioId(entity.getPortfolioId());
                    wflTrdProductDealMapper.insertSelective(wfldeal);
                    wflTrdProductDealAddMapper.insertSelective(wflTrdDealAdd);
                    List<BaseTradeProductDealDto> list = new ArrayList<BaseTradeProductDealDto>();
                    FinProductTradeDto finProductTrades = new FinProductTradeDto();
                    finProductTrades.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    BaseTradeProductDealDto baseDto = new BaseTradeProductDealDto();
                    baseDto.setRecType(DictCodeListExt1.应收款类型_还本未收利息.getValue());
                    //根据资产代码和branch查询支付日
                    PtlRecePositionFlowDto flowDto =new PtlRecePositionFlowDto();
                    flowDto.setFinprodId(trdProductDeal.getFinprodId());
                    flowDto.setBranch(trdProductDeal.getBranch());
                    flowDto.setPortfolioId(dto.getPortfolioId());
                    flowDto.setCdate(dto.getTradeDate());
                    baseDto.setPayDate(basePtlRecePositionService.getPayDate(flowDto));//该资产还本计划的支付日
                    baseDto.setTrdProductDeal(trdProductDeal);
                    baseDto.setTrdProductDealAdd(trdDealAdd);
                    list.add(baseDto);
                    finProductTrades.setBaseTradeProductDealDtos(list);

                    if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        finProductTrades.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                            finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                        });
                    }
                    basePtlRecePositionService.execute(finProductTrades);
                    if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        finProductTrades.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                        basePtlRecePositionService.insertPosition(finProductTrades);
                        finProductTrades.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                    }
                }

            }
            dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            //        委托代码不为空处理委托代码
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
                String entrustId = StringUtil.isNotEmpty(entity.getEntrustId()) ? entity.getEntrustId() : item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
                if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
                    item.setEntrustId(entrustId);
                }
            });

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            baseTradeProductDealCommService.execute(dto.getFinProductTradeDto());
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }
    }

    /**
     * 流程结束数据处理
     *
     * @param entity 存款类投资交易实体类
     * @see
     * @since 1.0
     */
    public void afterWorkFlow(TrdPlanRateInvestTradeDto entity) {
        if (entity.getOperateType().equals("NEW")) {
            WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, this.getTrdTotalDeal(entity));
            wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflTrdFinProductDealTotalMapper.deleteByPrimaryKey(wflTrdFinProductDealTotal);

            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, this.getEntity(entity));
            wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//              bussCommonMapperDelegateService.deleteByPrimaryKey(WflTrdProductDealMapper.class, wflTrdProductDeal);
            wflTrdProductDealMapper.deleteByPrimaryKey(wflTrdProductDeal);
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, this.setTrdProductDealAdd(entity));
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//              bussCommonMapperDelegateService.deleteByPrimaryKey(WflTrdProductDealAddMapper.class, wflTrdProductDealAdd);
            wflTrdProductDealAddMapper.deleteByPrimaryKey(wflTrdProductDealAdd);
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, this.setPaTrdProductDeal(entity));
            wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//              bussCommonMapperDelegateService.deleteByPrimaryKey(WflPaTrdProductDealMapper.class, wflPaTrdProductDeal);
            wflPaTrdProductDealMapper.deleteByPrimaryKey(wflPaTrdProductDeal);
            WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, this.setPaTrdProductDealTotal(entity));
            wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdFinDealTotalMapper.deleteByPrimaryKey(wflPaTrdFinDealTotal);
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            if (entity.getStlPayOrdersList() != null && entity.getStlPayOrdersList().size() > 0) {
                for (StlPayOrdersDto stlPayOrdersDto : entity.getStlPayOrdersList()) {
                    BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                    wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    if (wflStlPayOrders.getOrderId() != null) {
//                          bussCommonMapperDelegateService.deleteByPrimaryKey(WflStlPayOrdersMapper.class, wflStlPayOrders);
                        wflStlPayOrdersMapper.deleteByPrimaryKey(wflStlPayOrders);
                    }
                }
            }
            String prodRegistCode = this.getProdRegistCode(entity.getPortfolioId());

            if (prodRegistCode != null && !prodRegistCode.isEmpty()) {
                RepTrade repTrade = this.setRepTrade(entity, this.getProdRegistCode(entity.getPortfolioId()));
                // 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
                restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
                        Arrays.asList(repTrade), Boolean.class);
            }
            RepAssetDept repAssetDept = new RepAssetDept();
            repAssetDept.setRepFinprodId(entity.getFinprodId());
            repAssetDept.setRepSendStatus(DictCodeList.是否_是.getValue());
//              bussCommonMapperDelegateService.updateByPrimaryKeySelective(RepAssetDeptMapper.class,repAssetDept);
            repAssetDeptMapper.updateByPrimaryKeySelective(repAssetDept);
        } else {
            FinProductTradeDto finProductTradeDto = entity.getFinProductTradeDto();
            List<BaseTradeProductDealDto> baseDto = finProductTradeDto.getBaseTradeProductDealDtos();
            for (BaseTradeProductDealDto baDto : baseDto) {
                WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, baDto.getTrdFinProductDealTotal());
                wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdFinProductDealTotal.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId() + "R");
                wflTrdFinProductDealTotalMapper.deleteByPrimaryKey(wflTrdFinProductDealTotal);
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();

                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baDto.getTrdProductDeal());
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
//                      bussCommonMapperDelegateService.deleteByPrimaryKey(WflTrdProductDealMapper.class, wflTrdProductDeal);
                wflTrdProductDealMapper.deleteByPrimaryKey(wflTrdProductDeal);
                WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, baDto.getTrdProductDeal());
                wflPaTrdProductDeal.setTradeId(wflPaTrdProductDeal.getTradeId() + "R");
                wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
//                      bussCommonMapperDelegateService.deleteByPrimaryKey(WflPaTrdProductDealMapper.class, wflPaTrdProductDeal);
                wflPaTrdProductDealMapper.deleteByPrimaryKey(wflPaTrdProductDeal);
                WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, baDto.getTrdFinProductDealTotal());
                wflPaTrdFinDealTotal.setTotleTradeId(wflPaTrdFinDealTotal.getTotleTradeId()+ "R");
                wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflPaTrdFinDealTotalMapper.deleteByPrimaryKey(wflPaTrdFinDealTotal);
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDealAdd.getTradeId() + "R");
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
//                       bussCommonMapperDelegateService.deleteByPrimaryKey(WflTrdProductDealAddMapper.class, wflTrdProductDealAdd);
                wflTrdProductDealAddMapper.deleteByPrimaryKey(wflTrdProductDealAdd);
                if (baDto.getStlPayOrdersList() != null && baDto.getStlPayOrdersList().size() > 0) {
                    for (StlPayOrdersDto stlPayOrdersDto : baDto.getStlPayOrdersList()) {
                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                        if (wflStlPayOrders.getOrderId() != null) {
//                                  bussCommonMapperDelegateService.deleteByPrimaryKey(WflStlPayOrdersMapper.class, wflStlPayOrders);
                            wflStlPayOrdersMapper.deleteByPrimaryKey(wflStlPayOrders);
                        }
                    }
                }
                RepTradeKey repTradeKey = new RepTradeKey();
                repTradeKey.setRepBankTradeCode(baDto.getTrdProductDeal().getTotleTradeId());
                repTradeKey.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_存入银行存款.getValue());
//                      bussCommonMapperDelegateService.deleteByPrimaryKey(RepTradeMapper.class, repTradeKey);
                repTradeMapper.deleteByPrimaryKey(repTradeKey);
            }
        }
    }

    /**
     * 获取登记编号
     *
     * @param portfolioId 组合代码
     * @return 登记编号
     * @see
     * @since 1.0
     */
    public String getProdRegistCode(String portfolioId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("portfolioId", portfolioId);
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdPlanRateInvestTrade/getProdRegistCode", map, String.class);
    }

    /**
     * 设置报送信息
     *
     * @param dto            存款实体类
     * @param prodRegistCode 登记编号
     * @return 报送实体类
     * @see
     * @since 1.0
     */
    public RepTrade setRepTrade(TrdPlanRateInvestTradeDto dto, String prodRegistCode) {
        RepTrade repTrade = new RepTrade();
        repTrade.setRepFinprodRegId(prodRegistCode);
        repTrade.setRepBankTradeCode(dto.getTotleTradeId());
        if (bussCommonMapperDelegateService != null) {
            FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, dto.getFinprodId(), FinProduct.class);
            if (finProduct != null) {
                repTrade.setRepBankAssetCode(dto.getFinprodMarketId());
            }
        } else {
            repTrade.setRepBankAssetCode(dto.getFinprodId());
        }
        repTrade.setRepCcy(dto.getCcy());
        repTrade.setRepOccurMoney(dto.getTradeAmt());
        repTrade.setRepMoneyCny(dto.getTradeAmt().multiply(commonBusinessService.selectClosePrice(dto.getCcy(), DateUtil.format(dto.getTradeDate()))));
        // 一体化对应etliu start
//          if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())){
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
            // 一体化对应etliu end
            repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产买入.getValue());
        } else {
            repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
        }
        repTrade.setRepMoneyFlowTypeExp("无");
        repTrade.setRepCount(dto.getPrinAmt());
        repTrade.setRepTradeDate(dto.getTradeDate());
        repTrade.setRepTradeComponent(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
        repTrade.setRepStatusDate(dto.getTradeDate());
        repTrade.setRepRemark(dto.getRemark());
        repTrade.setRepDelCode(dto.getTotleTradeId());
        if (StringUtil.isNotEmpty(repTrade.getRepMoneyCny()) && StringUtil.isNotEmpty(repTrade.getRepCount())) {
//              ArithmeticUtil.di
            BigDecimal temp = ArithmeticUtil.multiply(ArithmeticUtil.divide(repTrade.getRepMoneyCny(), repTrade.getRepCount(), 4), new BigDecimal("100"));
            repTrade.setRepUnitPriceFull(temp);
        }
        repTrade.setRepUnitPriceNet(new BigDecimal("100"));
        return repTrade;
    }

    /**
     * 新建界面提交时插入持仓流水
     * {@inheritDoc}
     */
    @Override
    public void doInsert(TrdPlanRateInvestTradeDto entity, OperationType type) {
        if (type.toString().equals("NEW") && entity.getOperateType().equals("submit")) {
            beforeWorkFlow(entity);

        } else if (type.toString().equals("DELETE") || type.toString().equals("REVOKE")) {
            List<BaseTradeProductDealDto> baseTradeProductDealDto = restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME
                    + "/TrdPlanRateInvestTrade/getFinDto?tradeId=" + entity.getTotleTradeId(), BaseTradeProductDealDto.class);
            FinProductTradeDto dto = new FinProductTradeDto();
            entity.setFinProductTradeDto(dto);
            entity.getFinProductTradeDto().setBaseTradeProductDealDtos(baseTradeProductDealDto);
            entity.setOperateType("delete");
            beforeWorkFlow(entity);
        }
    }

    /**
     * 保存中进行提交时进行性插入持仓流水
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdPlanRateInvestTradeDto entity) {
        if (CollectionUtils.isNotEmpty(entity.getFileArray()) && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getTotleTradeId());
        }

        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        wflTrdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId());
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        wflTrdFinProductDealTotalMapper.deleteByPrimaryKey(wflTrdFinProductDealTotal);
        baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());

        if (entity.getOperateType().equals("submit")) {
            beforeWorkFlow(entity);
        }
    }

    /**
     * 作废操作,清空业务表
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
        
//        //根据工作流代码查询交易相关信息
//        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
//            ServiceApplication.TRADE_SERVICE_NAME
//                    + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);
//        //删除授信占用
//        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId()){
//            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdPlanRateInvestTradeDto entity, OperationType type) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        if (entity.getBussTrdTransferOrder() != null && entity.getBussTrdTransferOrder().size() > 0) {
            if (bussTrdTransferOrderService != null) {
                entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
            }
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, entity.getBussTrdTransferOrder().get(0));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            list.add(stlPayOrdersDto);
            entity.setStlPayOrdersList(list);
        }

        // 提前查询判断  是否  撤销 并且是 有转仓交易 并且转仓交易方向ps为买入
        String reverseTradeId = "";
        if(type == OperationType.REVOKE) {
            TrdProductDeal reverseTrdProductDeal = trdCommonBaseService.selectZCDealByTid(entity.getTotleTradeId(),true);
            if (reverseTrdProductDeal != null && StringUtil.equals(DictCodeList.交易方向_买入.getValue(), reverseTrdProductDeal.getPs())) {
                reverseTradeId = reverseTrdProductDeal.getTradeId();
            }
        }

        // TODO Auto-generated method stub
        TrdPlanRateInvestTradeDto dto = new TrdPlanRateInvestTradeDto();
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        FinProductTradeDto finProductTradeRevoke = new FinProductTradeDto();
        if (type.toString().equals("DELETE") || type.toString().equals("REVOKE")) {
            if (CollectionUtils.isNotEmpty(entity.getFileArray())
                    && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
                // 将附件和当前业务对象绑定（更新附件的业务代码）
                fileAttachmentProcess.updateBussinessNo(
                        entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                                Collectors.toList()),
                        entity.getTotleTradeId() + "R");
            }

            List<BaseTradeProductDealDto> baseTradeProductDealDto = restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME
                    + "/common/trdcommonbase/selectTotelTradeInfo?tradeId=" + entity.getTotleTradeId(), BaseTradeProductDealDto.class);
            for (BaseTradeProductDealDto dts : baseTradeProductDealDto) {
                //删除授信占用
                if (null != dts.getTrdProductDeal().getTradeId()){
                    riskCreditListCommonValidatorService.deleteCredit(dts.getTrdProductDeal().getTradeId());
                }
                
                dts.getTrdFinProductDealTotal().setWorkflowId(entity.getWorkflowId());
                dts.getTrdFinProductDealTotal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                dts.getTrdFinProductDealTotal().setRemark(entity.getCancelRemark());
                dts.getTrdFinProductDealTotal().setpTotleTradeId( dts.getTrdFinProductDealTotal().getTotleTradeId());
                dts.getTrdFinProductDealTotal().setTotleTradeId(dts.getTrdFinProductDealTotal().getTotleTradeId()+'R');
                trdFinProductDealTotalMapper.insert( dts.getTrdFinProductDealTotal());
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("isCancle", DictCodeList.是否_是.getValue());
                map.put("totalTradeId", entity.getTotleTradeId());
                baseTradeProductDealMapper.updateTrdProdTotalDeal(map);
                dts.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                dts.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                dts.getTrdProductDeal().setCancelRemark(entity.getCancelRemark());
                if(StringUtil.isNotEmpty( dts.getTrdProductDeal().getFinprodId())){
                	Map<String, Object> pageParam = new HashMap<>();
                	pageParam.put("finprodId",  dts.getTrdProductDeal().getFinprodId());
                	List<SelectOptionDto> selectOptionDto = restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getChannelIdAndCollectTypeForAsset",
                			pageParam , SelectOptionDto.class);
                	if(!StringUtil.isEmptyList(selectOptionDto)){
                		//设置通道id  撤销通道持仓
                		dts.getTrdProductDeal().setChlId(selectOptionDto.get(0).getId());
                	}
                }
                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal =
                            bussCommonMapperDelegateService.selectBySinglePrimaryKey(PaTrdProductDealMapper.class,
                                    dts.getTrdProductDeal().getTradeId(), PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + "R");
//                        bussCommonMapperDelegateService.insertBatch(PaTrdProductDealMapper.class,paTrdProductDeal);
                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);
                }
                PaTrdFinProductDealTotal paTrdProductDealTotal=
                        paTrdFinProductDealTotalMapper.selectByPrimaryKey(dts.getTrdFinProductDealTotal().getTotleTradeId());//获取汇总扩展
                if(paTrdProductDealTotal!=null){
                    paTrdProductDealTotal.setTotleTradeId(paTrdProductDealTotal.getTotleTradeId() + "R");
                    paTrdProductDealTotal.setCreateUser(LoginUserUtil.getUserName());
                    paTrdProductDealTotal.setCreateTime(new Date());
                    paTrdProductDealTotal.setUpdateUser(paTrdProductDealTotal.getCreateUser());
                    paTrdProductDealTotal.setUpdateTime(paTrdProductDealTotal.getCreateTime());
                    paTrdProductDealTotal.setWorkflowId(entity.getWorkflowId());
                    paTrdFinProductDealTotalMapper.insertSelective(paTrdProductDealTotal);
                }
                TrdDealTotalAdd totalAdd=trdDealTotalAddMapper.selectByPrimaryKey(entity.getTotleTradeId());
                if(totalAdd!=null) {
                    totalAdd.setTotleTradeId(
                            totalAdd.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    totalAdd.setCreateUser(LoginUserUtil.getUserName());
                    totalAdd.setCreateTime(new Date());
                    totalAdd.setUpdateUser(totalAdd.getCreateUser());
                    totalAdd.setUpdateTime(totalAdd.getCreateTime());
                    totalAdd.setWorkflowId(entity.getWorkflowId());
                    trdDealTotalAddMapper.insertSelective(totalAdd);
                }

                if(dts.getStlPayOrdersList()!=null && dts.getStlPayOrdersList().size()>0){
                    for (StlPayOrders stlPayOrders : dts.getStlPayOrdersList()) {
                        stlPayOrders.setOrderId(stlPayOrders.getOrderId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        stlPayOrders.setpTradeId(stlPayOrders.getTradeId());
                        stlPayOrders.setTradeId(entity.getTotleTradeId()+ BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                        stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                        stlPayOrdersMapper.insertSelective(stlPayOrders);
                    }
                }
                MstFileOutputParameter mstFileOutputParameter = mstFileCommonService.getFileInfoByBusiId(entity.getTotleTradeId());
                if(mstFileOutputParameter!=null) {
                    MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
                    mstFileInputParameter.setFileList(mstFileOutputParameter.getUploadFileList());
                    mstFileInputParameter.setBussinessId(entity.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    //框架共通组件.附件组件
                    mstFileCommonService.updateBusinessId(mstFileInputParameter);
                }
            }
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDto);
            finProductTradeRevoke.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            entity.setFinProductTradeDto(finProductTradeDto);
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTotleTradeId() + "R");

                List<TrdProductDeal>  trdProductDealLists=
                        baseTradeProductDealService.selectTrdProductDealsByTotalTradeId(entity.getTotleTradeId());//查询是否有应收款转入转出数据
                if(trdProductDealLists!=null && trdProductDealLists.size()>0){
                List<BaseTradeProductDealDto> list=new ArrayList<>();
                FinProductTradeDto reVokeFinDto = new FinProductTradeDto();
                for(TrdProductDeal deals:trdProductDealLists) {
                    BaseTradeProductDealDto reVoke=new BaseTradeProductDealDto();
                    deals.setWorkflowId(entity.getWorkflowId());
                    deals.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    deals.setCancelRemark(entity.getCancelRemark());
                    deals.setTotleTradeId(entity.getTotleTradeId());
                    if(StringUtil.isNotEmpty( deals.getFinprodId())){
                    	Map<String, Object> pageParam = new HashMap<>();
                    	pageParam.put("finprodId",  deals.getFinprodId());
                    	List<SelectOptionDto> selectOptionDto = restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getChannelIdAndCollectTypeForAsset",
                    			pageParam , SelectOptionDto.class);
                    	if(!StringUtil.isEmptyList(selectOptionDto)){
                    		//设置通道id  撤销通道持仓
                    		deals.setChlId(selectOptionDto.get(0).getId());
                    	}
                    }
                    String tradeId= deals.getTradeId();
                    deals.setTradeId(tradeId);
                    TrdProductDealAdd add = trdProductDealAddMapper.selectByPrimaryKey(tradeId);
                    reVoke.setRecType(DictCodeListExt1.应收款类型_还本未收利息.getValue());
                    reVoke.setPayDate(getPayDate(deals.getFinprodId(),deals.getBranch()));
                    if(add!=null){
                        add.setWorkflowId(entity.getWorkflowId());
                        add.setTradeId(deals.getTradeId());
                        reVoke.setTrdProductDealAdd(add);
                    }
                    reVoke.setTrdProductDeal(deals);
                    PaTrdProductDeal paTrdProductDeal=paTrdProductDealMapper.selectByPrimaryKey(tradeId);
                    if(paTrdProductDeal!=null){
                        paTrdProductDeal.setWorkflowId(entity.getWorkflowId());
                        paTrdProductDeal.setTradeId(deals.getTradeId());
                        reVoke.setPaTrdProductDeal(paTrdProductDeal);
                    }
                    PtlTradeKey key = new PtlTradeKey();
                    key.setBusiNo(tradeId);
                    key.setPortfolioId(add.getPortfolioId());
                    PtlTrade ptlTrade=ptlTradeMapper.selectByPrimaryKey(key);
                    List<PtlTrade> ptlTradeList =new ArrayList<>();
                    if(ptlTrade!=null){
                        ptlTrade.setBusiNo(tradeId+"R");
                        ptlTradeMapper.insertSelective(ptlTrade);
                        ptlTradeList.add(ptlTrade);
                    }
                    if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        reVoke.setActPortfolioId(entity.getActPortfolioId());
                        key.setPortfolioId(entity.getActPortfolioId());
                        ptlTrade = ptlTradeMapper.selectByPrimaryKey(key);
                        if (ptlTrade != null) {
                            ptlTrade.setBusiNo(tradeId + "R");
                            ptlTradeMapper.insertSelective(ptlTrade);
                        }
                        ptlTradeList.add(ptlTrade);
                    }
                    reVoke.setPtlTrade(ptlTradeList);
                    list.add(reVoke);
                }
                    finProductTradeRevoke.setBaseTradeProductDealDtos(list);
                    basePtlRecePositionService.execute(finProductTradeRevoke);
                    if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        finProductTradeRevoke.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                        basePtlRecePositionService.insertPosition(finProductTradeRevoke);
                        finProductTradeRevoke.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                }
                // 调用投管接口 add by renping 20190205 start
                for (BaseTradeProductDealDto item : list) {
                    InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                    investInterfCommDto.setPageId("TrdPlanRateInvestTrade002");
                    investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
                    investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                    investInterfCommDto.setTrdProductDeal(item.getTrdProductDeal());
                    investInterfCommDto.setTrdProductDealAdd(item.getTrdProductDealAdd());
                    if (!StringUtil.isEmptyList(item.getStlPayOrdersList())) {
                        investInterfCommDto.setStlPayOrders(item.getStlPayOrdersList().get(0));
                    }
                    investInterfCommDto.setTradeFeeDetaillist(item.getTrdTradeFeeDetailList());
                    investInterfCommDto.setOtherTypeForConvert(
                        DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
                    investInterfCommService.doInvestInterf(investInterfCommDto);
                }
                // 调用投管接口 add by renping 20190205 end
            }

            if(StringUtil.isNotEmpty(reverseTradeId)){

                TrdProductDeal trdProductDeal = new TrdProductDeal();
                trdProductDeal.setTradeId(reverseTradeId);
                trdProductDeal.setCancelRemark(entity.getCancelRemark());
                commonBusinessService.revokeReverseTrade(trdProductDeal,true);

            }

        } else {
            dto = this.setEntity(entity);
            dto.getFinProductTradeDto().setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            finProductTradeDto = entity.getFinProductTradeDto();
            if (CollectionUtils.isNotEmpty(entity.getFileArray())
                    && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
                // 将附件和当前业务对象绑定（更新附件的业务代码）
                fileAttachmentProcess.updateBussinessNo(
                        entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                                Collectors.toList()),
                        entity.getTotleTradeId());
            }
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTotleTradeId());
            jasperExecute(entity);
            if(entity.getTrdProductDealList()!=null && entity.getTrdProductDealList().size()>0){//有转入转出交易
                List<BaseTradeProductDealDto> list=new ArrayList<>();
                FinProductTradeDto finDto = new FinProductTradeDto();
                finDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                for(TrdProductDeal trdProductDeal:entity.getTrdProductDealList()){
                    TrdProductDealAdd trdDealAdd = new TrdProductDealAdd();
                    trdDealAdd=this.setReceTrdProductDealAdd(dto.getPortfolioId(),
                            dto.getEntrustId(),trdProductDeal.getTradeId());
                    trdDealAdd.setWorkflowId(dto.getWorkflowId());
                    BaseTradeProductDealDto baseDto = new BaseTradeProductDealDto();
                    baseDto.setRecType(DictCodeListExt1.应收款类型_还本未收利息.getValue());
                    baseDto.setTrdProductDeal(trdProductDeal);
                    baseDto.setTrdProductDealAdd(trdDealAdd);
                    baseDto.setPtlTrade(this.setRecePtlTrade(dto,trdProductDeal.getTradeId()));
                    PaTrdProductDeal padeal=setPaTrdProductDeal(dto);
                    padeal.setTradeId(trdProductDeal.getTradeId());
                    baseDto.setPaTrdProductDeal(padeal);
                    baseDto.setPayDate(getPayDate(trdProductDeal.getFinprodId(),trdProductDeal.getBranch()));
                    if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                        baseDto.setActPortfolioId(entity.getActPortfolioId());
                    }
                    list.add(baseDto);
                    for(PtlTrade ptlTrade:baseDto.getPtlTrade()){
                    ptlTradeMapper.insertSelective(ptlTrade);}
                }
                finDto.setBaseTradeProductDealDtos(list);
                finDto.setActPortfolioId(entity.getActPortfolioId());
                entity.setFinRecdProductTradeDto(finDto);//反向交易生成
                basePtlRecePositionService.execute(finDto);
                if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    finDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                    basePtlRecePositionService.insertPosition(finDto);
                    finDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                }

            }

        }
        //        委托代码不为空处理委托代码
        finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
            String entrustId = StringUtil.isNotEmpty(entity.getEntrustId()) ? entity.getEntrustId() : item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
            if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
                item.setEntrustId(entrustId);
            }
        });

        // 撤销时，子组合需要调估值共通
        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                finProductTrade.setActPortfolioId(entity.getActPortfolioId());
            });
        }
        baseTradeProductDealCommService.execute(finProductTradeDto);
        //后续交易会改变finProductTradeDto里通道信息，此时入库子组合信息
        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }


        dto.setOperateType(type.toString());
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                + "/TrdPlanRateInvestTrade/doComplete", entity, Integer.class);
        if (StringUtil.equals(type.toString(), OperationType.NEW.toString())) {
            tradeCommonService.sendCusdepOrder(entity.getBussTrdTransferOrder());
        }
        afterWorkFlow(entity);

        // 一体化对应etliu start
        if (!(type.toString().equals("DELETE") || type.toString().equals("REVOKE"))) {
            // 反向交易生成汇总表数据
            String autoGenerateNegateDealFlag = restTemplate.getForObject(
                    ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectParam/" + "COM" + "/"
                            + BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL,
                    String.class);
            String 反向交易编号 = null;
            if (DictCodeList.是否_是.getValue().equals(autoGenerateNegateDealFlag)
                    && (DictCodeList.交易方向_买入.getValue().equals(entity.getPs())
                    || DictCodeList.交易方向_卖出.getValue().equals(entity.getPs()))) {
                PtlPortfolio mcp = ptlPortfolioMapper.selectByPrimaryKey(entity.getTrdInvestPersonInfo002Dto().getCounterId());
                if (mcp != null) {
                    反向交易编号 = getTradeId(entity.getPs(),entity.getFinprodType());
                }
            }
            // 所有交易生效后调用的共通方法
            for (BaseTradeProductDealDto baseDto : finProductTradeDto.getBaseTradeProductDealDtos()) {
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(entity.getPortfolioId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());
                inputDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
                inputDto.setTrdTradeFeeDetail(baseDto.getTrdTradeFeeDetailList() != null && !baseDto.getTrdTradeFeeDetailList().isEmpty() ?
                        baseDto.getTrdTradeFeeDetailList().get(0) : null);
                if(反向交易编号!=null){
                    inputDto.getTrdProductDeal().setTotleTradeId(反向交易编号);
                    if (baseDto.getStlPayOrdersList() != null && !baseDto.getStlPayOrdersList().isEmpty()) {
                        for(StlPayOrdersDto stlPayOrder:baseDto.getStlPayOrdersList()){
                            stlPayOrder.setTradeId(反向交易编号);
                            stlPayOrder.setPortfolioId(entity.getTrdInvestPersonInfo002Dto().getCounterId());
                        }
                    }

                    if(entity.getFinRecdProductTradeDto()!=null){
                        inputDto.setTotleTradeId(反向交易编号);
                        inputDto.setBaseReceTradeProductDealDtoList(entity.getFinRecdProductTradeDto().getBaseTradeProductDealDtos());//应收款转出转入
                    }
                }
                // 反向交易增加 start by qiangchengxi 2019年3月12日
                AllTradeCommonUtil.setDeals(inputDto, baseDto.getTrdProductDeal(), baseDto.getTrdProductDealAdd(), baseDto.getPaTrdProductDeal(),
                        baseDto.getStlPayOrdersList(), entity.getPtlTrade(), entity.getActPortfolioId());
                // 反向交易增加 end by qiangchengxi 2019年3月12日

                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    inputDto.setActPortfolioId(entity.getActPortfolioId());
                }

                if (entity.getStlPayOrdersList() != null && !entity.getStlPayOrdersList().isEmpty()) {
                    StlPayOrders order = new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(order, entity.getStlPayOrdersList());
                    if(反向交易编号!=null){
                        order.setTradeId(反向交易编号);
                    }
                    inputDto.setStlPayOrders(order);
                }
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

                // 调用投管接口 add by renping 20190205 start
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                investInterfCommDto.setPageId("TrdPlanRateInvestTrade002");
                investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
                investInterfCommDto.setTrdProductDeal(inputDto.getTrdProductDeal());
                investInterfCommDto.setTrdProductDealAdd(inputDto.getTrdProductDealAdd());
                investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                investInterfCommDto.setTradeFeeDetaillist(baseDto.getTrdTradeFeeDetailList());
                investInterfCommDto.setOtherTypeForConvert(
                    DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
                investInterfCommService.doInvestInterf(investInterfCommDto);
                // 调用投管接口 add by renping 20190205 end

                if (反向交易编号 != null) {
                    String 交易对手_子组合 = entity.getTrdInvestPersonInfo002Dto().getCounterId();// 子组合交易对手
                    String 交易对手的母组合 = AllTradeCommonUtil.exchangePortfolio(entity.getTrdInvestPersonInfo002Dto().getCounterId());// 交易对手的母组合，设置成反向交易的组合代码

                    // 组合代码与交易对手对调
                    // 交易方向为买入；
                    // 其他字段与原始交易相同；
                    // String 原交易母组合代码 = trdProductDealAdd.getPortfolioId();
                    String 原交易子组合代码 = entity.getActPortfolioId();

                    TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
                    trdFinProductDealTotal=this.getTrdTotalDeal(entity);
                    //BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, entity);
                    trdFinProductDealTotal.setTotleTradeId(反向交易编号);
                    trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
                    trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
                    trdFinProductDealTotal.setCounterId(原交易子组合代码);
                   // trdFinProductDealTotal.setPortfolioId(交易对手的母组合);
                    trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
                    trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_计划类资产.getValue());
                    trdFinProductDealTotal.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
                    trdFinProductDealTotal.setPs(DictCodeList.交易方向_买入.getValue());
                    trdFinProductDealTotal.setPortfolioId(entity.getTrdInvestPersonInfo002Dto().getCounterId());//原来交易的交易对手
                    trdFinProductDealTotal.setIspayUnpaidint(null);//是否转让
                    trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

                    PaTrdFinProductDealTotal paTrdFinDealTotal1 = new PaTrdFinProductDealTotal();
                    BeanUtil.copyPropertiesIgnoreNull(paTrdFinDealTotal1,dto.getPaTrdFinProductDealTotal());
                    paTrdFinDealTotal1.setTotleTradeId(反向交易编号);
                    paTrdFinDealTotal1.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
                    paTrdFinProductDealTotalMapper.insertSelective(paTrdFinDealTotal1);
                    TrdDealTotalAdd trdDealTotalAdd1 = setTrdDealTotalAdd(dto);
                    trdDealTotalAdd1.setTotleTradeId(反向交易编号);
                    trdDealTotalAddMapper.insertSelective(trdDealTotalAdd1);
/*                    if (CollectionUtils.isNotEmpty(entity.getFileArray())) {
                        // 将附件和当前业务对象绑定（更新附件的业务代码）
                        fileAttachmentProcess.updateBussinessNo(
                                entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                                        Collectors.toList()),
                                反向交易编号);
                    }*/

                }
            }
            paTrdFinProductDealTotalMapper.insertSelective(dto.getPaTrdFinProductDealTotal());//汇总交易扩展
            TrdDealTotalAdd trdDealTotalAdd = setTrdDealTotalAdd(dto);
            trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
            // 一体化对应etliu end

            // 复制一份附件信息
            if (!StringUtil.isEmptyList(entity.getFileArray())) {
                for (UploadFile uploadFile : entity.getFileArray()) {
                    copyFileInfoByFileNo(uploadFile.getFileNo(), 反向交易编号);
                }
            }
        }

        // 计划类利率型交易，BU需要调用销售系统
////        调用接口  add by zhuyongkang 20191127 start
//        List<String> tradeIds = new ArrayList<>();
//        tradeIds.add(entity.getTotleTradeId());
//        try {
//            restTemplate.postForObject(
//                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
//                    Map.class);
//        } catch (Exception e) {
//
//        }
////        调用接口  add by zhuyongkang 20191127 end
    }

    @Autowired
    private JasperUtil jasperUtil;

    public void jasperExecute(TrdPlanRateInvestTradeDto entity) {
        if ("001".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))) {
            //生成划款指令附件:调用自定义接口项目生成Jasper报表 add by zhangJin 2019-07-29 start
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            trdProductDeal.setTradeId(entity.getTotleTradeId());
            trdProductDeal.setPs(entity.getPs());
            trdProductDeal.setFinprodId(entity.getFinprodId());
            jasperUtil.jasperExecute(Arrays.asList(trdProductDeal), restTemplate);
            //生成划款指令附件:调用自定义接口项目生成Jasper报表 add by zhangJin 2019-07-29 end
        }
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdPlanRateInvestTradeDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
        	try {
//              return commonBusinessService.是否需要上级审批(dto.getTrdInvestPersonInfo002Dto().getCounterId(), dto.getPrinAmt(), LoginUserUtil.getUserName(),
//                      DictCodeList.平安资产类型_计划类资产_利率型.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  start
//                      , dto.getFinprodId()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  end
//              );
              Map<String, Object> params = CommonBusinessService.setParams(dto.getTrdInvestPersonInfo002Dto().getCounterId(), dto.getPrinAmt(),LoginUserUtil.getUserName(),
                  DictCodeList.平安资产类型_计划类资产_利率型.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
                  , dto.getFinprodId(),entity.getWorkflowId());
              params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
              return commonBusinessService.是否需要上级审批(params);
          }catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        } else {
            return null;
        }
//        return null;
    }

    /**
     * @param finprodId
     * @return
     */
    public FinProduct getFinProductInfo(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
                        + "/TrdPlanRateInvestTrade/getFinProductInfo?finprodId=" + finprodId,
                FinProduct.class);
    }


    @Override
    public void doBack(TrdPlanRateInvestTradeDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTotleTradeId());
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());

        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getCancelRemark())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }


    /**
     * 金融产品total实体类生成
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品实体类
     * @see
     * @since 1.0
     */
    public TrdFinProductDealTotal getTrdTotalDeal(TrdPlanRateInvestTradeDto dto) {
        TrdFinProductDealTotal trdProductDealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealTotal, dto);
        BigDecimal channeFee=dto.getChannelFee();
        if(channeFee==null){
            channeFee=BigDecimal.ZERO;
        }
        if (dto.getSettleSpeed() != null) {
            trdProductDealTotal.setSettleSpeed(dto.getSettleSpeed() - 1);
        }
        trdProductDealTotal.setUnitCprice(new BigDecimal("1"));
        // modify by wanshiwei ZGZGST-3595 计划类（利率型）资产“利率%”取值错误，取资产的计提利率 20210928 start
        trdProductDealTotal.setUnitInt(com.joyintech.calculation.util.ArithmeticUtil.divide(dto.getInvestRate(), 100, 8));
        /*if (dto.getIntAmt() != null && dto.getTradeAmt() != null) {
            trdProductDealTotal.setUnitInt(ArithmeticUtil.divide(dto.getIntAmt(), dto.getTradeAmt(), 8));
        }*/
        // modify by wanshiwei ZGZGST-3595 存款类资产“利率%”取值错误，取资产的计提利率 20210928 end
        trdProductDealTotal.setUnitFprice(new BigDecimal("1"));
        trdProductDealTotal.setParValue(new BigDecimal("1"));
        trdProductDealTotal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
        trdProductDealTotal.setFinprodType("");
        trdProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        trdProductDealTotal.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue"))/*Long.valueOf(0)*/);
        trdProductDealTotal.setFpriceAmt(dto.getTradeAmt().subtract(channeFee));//全价总额不包含通道费
        trdProductDealTotal.setIntAmt(dto.getIntAmt());
        trdProductDealTotal.setCpriceAmt(dto.getPrinAmt());
        trdProductDealTotal.setShareAmt(dto.getPrinAmt());
        trdProductDealTotal.setTradeAmt(dto.getTradeAmt());
        trdProductDealTotal.setIsClean(DictCodeList.是否_是.getValue());
        trdProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDealTotal.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
        trdProductDealTotal.setTrader(dto.getTrdInvestPersonInfo002Dto().getTrader());
        trdProductDealTotal.setCounterContact(dto.getTrdInvestPersonInfo002Dto().getContactInfo());
        trdProductDealTotal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        if (StringUtil.equals(DictCodeList.是否_否.getValue(), dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())) {
            dto.setIsOutTrade(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade());
        } else {
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
        }
        trdProductDealTotal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        trdProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDealTotal.setFeeAmt(dto.getChannelFee());
//        if (StringUtil.equals(DictCodeList.是否_否.getValue(), dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())) {
//            trdProductDealTotal.setFeeAmtPay(dto.getChannelFee());
//        } else {
//            if (StringUtil.equals(dto.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())) {
//                trdProductDealTotal.setFeeAmtPay(dto.getChannelFee());
//            } else {
//                trdProductDealTotal.setFeeAmtUnpay(dto.getChannelFee());
//            }
//        }
        //2019-12-24 通道费改造 标的挂通道  通道费不参与计算
        trdProductDealTotal.setFeeAmtUnpay(dto.getChannelFee());
        trdProductDealTotal.setPayUnpaidintAmt(dto.getPayUnpaidintAmt());//支付未支付利息
        if(dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
            //还本不带息的资产才保存值
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("finprodId",dto.getFinprodId());
            param.put("settleDate",dto.getSettleDate());
            Map<String, Object> result=getFinprodType(param);
            if(result.get("repayIntFlag")!=null &&result.get("repayIntFlag").equals("N")) {
                trdProductDealTotal.setIspayUnpaidint(dto.getIspayUnpaidint());// 是否转让还本未支付利息
            }
        }
        trdProductDealTotal.setPortfolioId(dto.getActPortfolioId());//存子组合
        return trdProductDealTotal;
    }


    /**
     * 合成交易和对应的应收款持仓流水实体类
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 计划类（利率型）投资交易实体类
     * @see
     * @since 1.0
     */
    public TrdPlanRateInvestTradeDto setPtlReceEntity(TrdPlanRateInvestTradeDto dto,String operateType) {

        //调共通【应收款持仓共通.区间中应收款可用持仓查询】
        Map<String,Object> parmap=new HashMap<>();
        parmap.put("finprodId",dto.getFinprodId());
        parmap.put("portfolioId",dto.getPortfolioId());
        parmap.put("payDate",dto.getTradeDate());
        List<PtlRecePositionFlow> flowList=basePtlRecePositionService.findUnpayPtlRecePosition(parmap);
        List<TrdProductDealAdd> dealaddList = new ArrayList<TrdProductDealAdd>();
        List<TrdProductDeal> trdDealList = new ArrayList<TrdProductDeal>();
        List<PtlTrade> ptlTradeList=new ArrayList<PtlTrade>();
        for (PtlRecePositionFlow recedto : flowList) {
            TrdProductDeal trdProductDeal = getEntity(dto);//获取交易信息
            trdProductDeal.setTradeId(getTradeId(dto.getPs(),dto.getFinprodType()));
            trdProductDeal.setTradeAmt(recedto.getAmount());//应收款流水金额
            trdProductDeal.setShareAmt(recedto.getAmount());
            if (dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                trdProductDeal.setPs(DictCodeList.交易方向_应收款项转出.getValue());
            } else {
                trdProductDeal.setPs(DictCodeList.交易方向_应收款项转入.getValue());
            }
            trdProductDeal.setBranch(recedto.getBranch());//应收款流水BRANCH
            trdDealList.add(trdProductDeal);//组装交易信息

            TrdProductDealAdd addDto = new TrdProductDealAdd();
            addDto.setTradeId(trdProductDeal.getTradeId());
            addDto.setPortfolioId(dto.getPortfolioId());
            addDto.setEntrustId(dto.getEntrustId());
            addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
            dealaddList.add(addDto);

            List<PtlTrade> ptlTrades = new ArrayList<>();
            PtlTrade ptlTrade = new PtlTrade();
            ptlTrade.setPortfolioId(dto.getPortfolioId());
            ptlTrade.setBusiNo(trdProductDeal.getTradeId());
            ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade.setBusiId(dto.getFinprodId());
            ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
            ptlTrade.setSettleDate(dto.getTradeDate());
            ptlTrades.add(ptlTrade);

            if (!dto.getPortfolioId().equals(dto.getActPortfolioId())) {
                PtlTrade ptlTrade1 = new PtlTrade();
                ptlTrade1.setPortfolioId(dto.getActPortfolioId());
                ptlTrade1.setBusiNo(trdProductDeal.getTradeId());
                ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                ptlTrade1.setBusiId(dto.getFinprodId());
                ptlTrade1.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
                ptlTrade1.setSettleDate(dto.getTradeDate());
                ptlTrades.add(ptlTrade1);
            }

            List<BaseTradeProductDealDto> list = new ArrayList<BaseTradeProductDealDto>();
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            BaseTradeProductDealDto baseDto = new BaseTradeProductDealDto();
            baseDto.setTrdProductDeal(trdProductDeal);//交易
            baseDto.setTrdProductDealAdd(addDto);
            baseDto.setPayDate(getPayDate(trdProductDeal.getFinprodId(),trdProductDeal.getBranch()));
            list.add(baseDto);
            finProductTradeDto.setBaseTradeProductDealDtos(list);
            dto.setPtlTrade(ptlTradeList);
            dto.setPaTrdProductDeal(this.setPaTrdProductDeal(dto));
            dto.setFinProductTradeDto(finProductTradeDto);


            // 撤销时，子组合需要调估值共通
            if (dto.getActPortfolioId() != null && !dto.getActPortfolioId().equals(dto.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(dto.getActPortfolioId());
                });
            }
            basePtlRecePositionService.execute(dto.getFinProductTradeDto());
            if (StringUtil.isNotEmpty(dto.getActPortfolioId()) && !dto.getActPortfolioId().equals(dto.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(dto.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(dto.getPortfolioId()));
            }
            return dto;
        }



        return dto;
    }


    public String getTradeId(String ps,String finProdType){
            BussinessCodeParameter parameter = new BussinessCodeParameter();
        if(StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())){
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产申购);
            // 一体化对应etliu end
            if(StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, finProdType)){
                parameter.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
            }else if(StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, finProdType)){
                parameter.setType1(DictCodeList.业务编号一级分类_信托产品);
            }else{
                parameter.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
            }
        }else if(StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())){
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产赎回);
            if(StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, finProdType)){
                parameter.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
            }else if(StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, finProdType)){
                parameter.setType1(DictCodeList.业务编号一级分类_信托产品);
            }else{
                parameter.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
            }
        }
            return  autoPeriodCommonService.buildBussinessCode(parameter);
    }


    /**
     * 累计还本不带息的金额
     *
     * @param map 请求参数
     * @return
     * @see
     * @since 1.0
     */
    public String getUnpayIntAmt(Map<String, Object> map) {
        String intAmtStr = "";
        BigDecimal unpayIntAmt = new BigDecimal("0");
        unpayIntAmt=basePtlRecePositionService.unpayTotalIntAmt(map);
        if(unpayIntAmt!=null){
        DecimalFormat df2 = new DecimalFormat("####################0.0000000#");
        intAmtStr = df2.format(ArithmeticUtil.round(unpayIntAmt, 6, RoundingMode.HALF_UP));}
        return intAmtStr;
    }


    public List<TrdProductDeal> getReceTrdDeal(TrdPlanRateInvestTradeDto dto){
        //调共通【应收款持仓共通.区间中应收款可用持仓查询】
        Map<String,Object> parmap=new HashMap<>();
        parmap.put("finprodId",dto.getFinprodId());
        parmap.put("portfolioId",dto.getPortfolioId());
        parmap.put("cDate",DateUtil.format(dto.getTradeDate()));
        parmap.put("secAcctId",dto.getChlId());
        Date payDate=dto.getTradeDate();
        String payDates=DateUtil.format(payDate,"yyyyMMdd");
        parmap.put("payDate",payDates);
        List<PtlRecePositionFlow> flowList=basePtlRecePositionService.findUnpayPtlRecePosition(parmap);
        List<TrdProductDeal> trdDealList = new ArrayList<TrdProductDeal>();
        for (PtlRecePositionFlow recedto : flowList) {
            TrdProductDeal trdProductDeal = getEntity(dto);//获取交易信息
            BigDecimal tradeAmt=recedto.getAmount();
            trdProductDeal.setTradeId(getTradeId(dto.getPs(),dto.getFinprodType()));
            if(dto.getIntAmt()!=null){
                trdProductDeal.setUnitInt(trdProductDeal.getIntAmt().subtract(tradeAmt));
            }
            trdProductDeal.setFpriceAmt(tradeAmt);
            trdProductDeal.setTradeAmt(tradeAmt);//应收款流水金额
            trdProductDeal.setShareAmt(tradeAmt);
            trdProductDeal.setUnitInt(null);//单位利息
            trdProductDeal.setCpriceAmt(null);//净价总额
            trdProductDeal.setPrinAmt(BigDecimal.ZERO);
            trdProductDeal.setIntAmt(tradeAmt);
            trdProductDeal.setFeeAmt(null);
            trdProductDeal.setFeeAmtPay(null);
            trdProductDeal.setFeeAmtUnpay(null);
            if (dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                trdProductDeal.setPs(DictCodeList.交易方向_应收款项转出.getValue());
            } else {
                trdProductDeal.setPs(DictCodeList.交易方向_应收款项转入.getValue());
            }
            trdProductDeal.setBranch(recedto.getBranch());//应收款流水BRANCH
            trdDealList.add(trdProductDeal);//组装交易信息
             }
        return trdDealList;
    }


    /**
     * 生成金融产品附属表
     *
     * @param portfolioId 计划类（利率型）投资交易实体类
     * @return 金融产品附属类
     * @see
     * @since 1.0
     */
    public TrdProductDealAdd setReceTrdProductDealAdd(String portfolioId,String entrustId,String tradeId) {
        TrdProductDealAdd addDto = new TrdProductDealAdd();
        addDto.setTradeId(tradeId);
        addDto.setPortfolioId(portfolioId);
        addDto.setEntrustId(entrustId);
        addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return addDto;
    }

    @Override
    public void doRevoke(TrdPlanRateInvestTradeDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTotleTradeId());
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());
        
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getCancelRemark())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }

    public Date getPayDate(String findprodId,Long branch){
        //根据资产代码和branch查询支付日
        PtlRecePositionFlowDto flowDto =new PtlRecePositionFlowDto();
        flowDto.setFinprodId(findprodId);
        flowDto.setBranch(branch);
        return basePtlRecePositionService.getPayDate(flowDto);//该资产还本计划的支付日
    }

    //通过资产代码去找交易对手
	public Map<String, String> selectFManeger(String finprodId) {
		Map<String, String> result = new HashMap<>();
		String id = restTemplate.getForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/selectFManeger?finprodId=" + finprodId,
				String.class);
		result.put("id", id);
		return result;
	}
	
	 @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	    public void save(TrdPlanRateInvestTradeDto dto) {
	    	dto.setActPortfolioId(dto.getPortfolioId());
	    	dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
	        if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0
	                && dto.getBussTrdTransferOrder().get(0) != null && !dto.getBussTrdTransferOrder().get(0).toString().isEmpty()) {
	            if (bussTrdTransferOrderService != null) {
	            	dto.setBussTrdTransferOrder(bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
	            }
	            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
	            BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, dto.getBussTrdTransferOrder().get(0));
	            String orderId = "";
	    		if (baseGetBusinessId != null) {
	    			BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
	    			bussinessCodeParameter.setSeqNo("9998");
	    			orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
	    		} else {
	    			orderId = IDGenerator.generate();
	    		}
	    		stlPayOrdersDto.setOrderId(orderId);
	            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
	            list.add(stlPayOrdersDto);
	            dto.setStlPayOrdersList(list);
	        }
	        TrdPlanRateInvestTradeDto entity = this.setEntity(dto);
	        
	        //入库
	        entity.setActPortfolioId(entity.getPortfolioId());
	        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
	        entity.getFinProductTradeDto().setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
	        
	        FinProductTradeDto finProductTradeDto = entity.getFinProductTradeDto();
	        
	        for(BaseTradeProductDealDto baseTradeProductDealDto:finProductTradeDto.getBaseTradeProductDealDtos()) {
	        	//金融产品汇总表
	        	if(baseTradeProductDealDto.getTrdFinProductDealTotal()!=null){
	        		trdFinProductDealTotalMapper.insert(baseTradeProductDealDto.getTrdFinProductDealTotal());
	        	}

	        	//金融产品交易
	        	trdProductDealMapper.insert(baseTradeProductDealDto.getTrdProductDeal());

	        	//金融产品交易附属表
	        	trdProductDealAddMapper.insert(baseTradeProductDealDto.getTrdProductDealAdd());
	        	//划款指令表
	        	if(baseTradeProductDealDto.getStlPayOrdersList() != null){
	        		for(StlPayOrders stlPayOrders:baseTradeProductDealDto.getStlPayOrdersList()){
	        			stlPayOrdersMapper.insert(stlPayOrders);
	        		}
	        	}
	        }
	    }

}

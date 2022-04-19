package com.joyintech.tams.cfets.trd.bond.service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.*;
import com.joyintech.service.bankuplink.bond.service.CfetsBondDealQuotedPriceService;
import com.joyintech.service.bankuplink.calc.service.SecCalRequestQuoteService;
import com.joyintech.service.bankuplink.calc.service.SecCalUtil;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.bond.dto.AutoAllocationRequest;
import com.joyintech.tams.cfets.trd.bond.dto.CalRequest;
import com.joyintech.tams.cfets.trd.bond.dto.CalRespone;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.FinCashPlanDto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.SecInfoModelDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zznn
 * @date 2021/11/9
 */
@Service
public class IbuQuoteBondCalService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(IbuQuoteBondService.class);

    /**
     * 递增维度(万元)
     */
    private static BigDecimal faceDimension = new BigDecimal("0.1");

    /**
     * 递增维度(元)
     */
    private static BigDecimal amtDimension = new BigDecimal("0.01");

    @Autowired(required = false)
    private DateCalService dateCalService;

    @Autowired
    private CfetsBondDealQuotedPriceService cfetsBondDealQuotedPriceService;

    @Autowired(required = false)
    private BaseTradeFeeCommService baseTradeFeeCommService;

    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired
    private SecCalRequestQuoteService secCalRequestQuoteService;

    /**
     * 数据联动计算
     *
     * @param calRequest
     * @return
     */
    public CalRespone calculation(CalRequest calRequest) {
        CalRespone calRespone = new CalRespone();
        //CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
        //BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto, calRequest);
        //cfetsBondDealDialogueQuoteDto.setAvaNum(calRequest.getShareAmt());
        //BeanUtil.copyPropertiesIgnoreNull(calRespone,cfetsBondDealQuotedPriceService.calcAmt(cfetsBondDealDialogueQuoteDto));
        calRespone = calAmt(calRequest);
        if (null != calRespone.getFpriceAmt()) {
            calRequest.setFpriceAmt(calRespone.getFpriceAmt());
        }
        List<TrdTradeFeeDetail> trdTradeFeeDetails = calFee(calRequest);
        if (CollectionUtils.isNotEmpty(trdTradeFeeDetails)) {
            for (TrdTradeFeeDetail item : trdTradeFeeDetails) {
                if (StringUtil.equals(item.getFeeType(), BussinessDictCodeList.费用类型_交易手续费.getValue())) {
                    calRespone.setDealFeeAmt(item.getFeeAmt());
                    continue;
                }
                if (StringUtil.equals(item.getFeeType(), BussinessDictCodeList.费用类型_结算手续费.getValue())) {
                    calRespone.setSettFeeAmt(item.getFeeAmt());
                    continue;
                }
            }
        }
        //计算偏离度
        if (StringUtil.isNotEmpty(calRequest.getFinprodId()) && (null != calRespone.getPrice() || null != calRequest.getPrice())) {
            Map<String, Object> calParam = new HashMap<>();
            calParam.put("finprodId", calRequest.getFinprodId());
            calParam.put("finprodMarketId", finProductMapper.selectByPrimaryKey(calRequest.getFinprodId()).getFinprodMarketId());
            calParam.put("unitCprice", null != calRequest.getPrice() ? calRequest.getPrice() : calRespone.getPrice());
            calRespone.setDeviationDegree(calcDeviationDegree(calParam));
        }
        return calRespone;
        /*return restTemplate.postForObject(
                    ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/calcAmt",
                    calRequest, CalRespone.class);*/
    }

    /**
     * 计算费用
     *
     * @param calRequest
     * @return
     */
    public List<TrdTradeFeeDetail> calFee(CalRequest calRequest) {
        BasicTradeFeeInputDto inputDto = new BasicTradeFeeInputDto();
        inputDto.setTradeAmt(calRequest.getFpriceAmt());
        inputDto.setTradeDate(calRequest.getSettleDate());
        inputDto.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
        inputDto.setFinprodType2(BussinessDictCodeList.平安资产类型_债券.getValue());
        inputDto.setPs(calRequest.getPs());// 交易方向
        inputDto.setFpriceAmt(calRequest.getFpriceAmt());// 全价总额
        inputDto.setPrinAmt(calRequest.getShareAmt());// 券面总额
        inputDto.setIsGrade("1");
        String registOrg = cfetsIbuQuoteBondMapper.getRegistOrgByFinprodId(calRequest.getFinprodId());
        inputDto.setRegistOrg(registOrg);
        inputDto.setFeeTypeList(Arrays.asList(BussinessDictCodeList.费用类型_交易手续费.getValue(), BussinessDictCodeList.费用类型_结算手续费.getValue()));
        return baseTradeFeeCommService.innerBankFeeDeal(inputDto);
    }

    /**
     * 自动分配
     *
     * @param autoAllocationRequest
     * @return
     */
    public List<PtlAllocationDto> autoAllocation(AutoAllocationRequest autoAllocationRequest) {
        String sellPara = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
        String ps = autoAllocationRequest.getPs();
        //全价总额和净价总额为空  处理(请求报价)
        if (null == autoAllocationRequest.getCpriceAmt() || BigDecimal.ZERO.compareTo(autoAllocationRequest.getCpriceAmt()) == 0) {
            autoAllocationRequest.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(autoAllocationRequest.getShareAmt(), autoAllocationRequest.getPrice()), new BigDecimal(100), 2));
        }
        if (null == autoAllocationRequest.getFpriceAmt() || BigDecimal.ZERO.compareTo(autoAllocationRequest.getFpriceAmt()) == 0) {
            autoAllocationRequest.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(autoAllocationRequest.getShareAmt(), autoAllocationRequest.getUnitFprice()), new BigDecimal(100), 2));
        }
        //备份剩余额度
        List<PtlAllocationDto> ptlAllocations = autoAllocationRequest.getPtlAllocations();
        try {
            //无分配模式
            if (StringUtil.isEmpty(autoAllocationRequest.getDistribution())) {
                //通过各自的券面总额计算金额
                ptlAllocations = noneDistribution(autoAllocationRequest);
            } else {
                if (CollectionUtils.isNotEmpty(ptlAllocations)) {
                    ptlAllocations.forEach(item -> {
                        item.setAvaNumOld(item.getAvaNum());
                        item.setAvaNum(ArithmeticUtil.divide(item.getAvaNum(), new BigDecimal(10000), 1));
                    });
                }
                //平均分配
                if (StringUtil.equals(autoAllocationRequest.getDistribution(), "139")) {
                    ptlAllocations = aveDistribution(autoAllocationRequest);
                }
                //比例分配
                if (StringUtil.equals(autoAllocationRequest.getDistribution(), "143")) {
                    ptlAllocations = propDistribution(autoAllocationRequest);
                }
                if (CollectionUtils.isNotEmpty(ptlAllocations)) {
                    ptlAllocations.forEach(item -> {
                        item.setAvaNum(item.getAvaNumOld());
                    });
                }
            }
        } catch (Exception e) {
            logger.error("自动匹配计算异常:" + e.getMessage());
        }
        return ptlAllocations;
    }

    /**
     * 无分配模式
     *
     * @param request
     * @return
     */
    public List<PtlAllocationDto> noneDistribution(AutoAllocationRequest request) {
        List<PtlAllocationDto> ptlAllocations = request.getPtlAllocations();
        ptlAllocations.forEach(item -> {
            CalRequest calRequest = new CalRequest();
            BeanUtil.copyPropertiesIgnoreNull(calRequest, request);
            calRequest.setShareAmt(item.getShareAmt());
            CalRespone calRespone = calculation(calRequest);
            item.setDealFeeAmt(calRespone.getDealFeeAmt());
            item.setSettFeeAmt(calRespone.getSettFeeAmt());
            item.setCpriceAmt(calRespone.getCpriceAmt());
            item.setFpriceAmt(calRespone.getFpriceAmt());
            item.setIntAmt(calRespone.getIntAmt());
        });
        return ptlAllocations;
    }

    /**
     * 平均分配
     *
     * @return
     */
    public List<PtlAllocationDto> aveDistribution(AutoAllocationRequest request) {
        //以万元为最小粒度计算
        BigDecimal shareAmt = ArithmeticUtil.divide(request.getShareAmt(), new BigDecimal(10000), 1);
        List<PtlAllocationDto> ptlAllocations = request.getPtlAllocations();
        //降序
        ptlAllocations.sort((p1, p2) -> p1.getAvaNum().compareTo(p2.getAvaNum()));
        BigDecimal avg = ArithmeticUtil.divide(shareAmt, new BigDecimal(ptlAllocations.size()), 1, RoundingMode.DOWN);
        //剩余尾差
        BigDecimal tailDifference = shareAmt;
        for (PtlAllocationDto item : ptlAllocations) {
            if (item.getAvaNum().compareTo(avg) >= 0) {
                item.setShareAmt(avg);
            } else {
                item.setShareAmt(item.getAvaNum());
            }
            item.setAvaNum(ArithmeticUtil.subtract(item.getAvaNum(), item.getShareAmt()));
            tailDifference = ArithmeticUtil.subtract(tailDifference, item.getShareAmt());
        }
        handleTailDifference(ptlAllocations, tailDifference);
        calOtherForPtlAllocation(ptlAllocations, request);
        calFee(ptlAllocations, ArithmeticUtil.multiply(shareAmt, new BigDecimal(10000)), request.getDealFeeAmt(), request.getSettFeeAmt());
        return ptlAllocations;
    }

    /**
     * 比例分配
     *
     * @return
     */
    public List<PtlAllocationDto> propDistribution(AutoAllocationRequest request) {
        //以万元为最小粒度计算
        BigDecimal shareAmt = ArithmeticUtil.divide(request.getShareAmt(), new BigDecimal(10000), 1);
        List<PtlAllocationDto> ptlAllocations = request.getPtlAllocations();
        //降序
        ptlAllocations.sort((p1, p2) -> p1.getAvaNum().compareTo(p2.getAvaNum()));
        //剩余额度汇总
        BigDecimal avaNumTotal = ptlAllocations.stream().map(PtlAllocationDto::getAvaNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        //尾差
        BigDecimal tailDifference = shareAmt;
        for (PtlAllocationDto ptlAllocation : ptlAllocations) {
            ptlAllocation.setShareAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(shareAmt, ptlAllocation.getAvaNum()), avaNumTotal, 1, RoundingMode.DOWN));
            tailDifference = ArithmeticUtil.subtract(tailDifference, ptlAllocation.getShareAmt());
        }
        handleTailDifference(ptlAllocations, tailDifference);
        calOtherForPtlAllocation(ptlAllocations, request);
        calFee(ptlAllocations, ArithmeticUtil.multiply(shareAmt, new BigDecimal(10000)), request.getDealFeeAmt(), request.getSettFeeAmt());
        return ptlAllocations;
    }

    /**
     * 尾差处理(券面)
     *
     * @param ptlAllocations
     * @param tailDifference
     */
    public void handleTailDifference(List<PtlAllocationDto> ptlAllocations, BigDecimal tailDifference) {
        //递增维度
        BigDecimal addDimension = faceDimension;
        int size = ptlAllocations.size();
        //委托维度 已经完全分配的集合
        Set<String> set = new HashSet<>();
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0 && size > 0) {
            if (tailDifference.compareTo(new BigDecimal("1")) >= 0) {
                addDimension = ArithmeticUtil.divide(tailDifference, new BigDecimal(size), 1, RoundingMode.DOWN);
            }
            for (PtlAllocationDto item : ptlAllocations) {
                if ((item.getAvaNum().compareTo(BigDecimal.ZERO) == 0 || tailDifference.compareTo(BigDecimal.ZERO) <= 0)) {
                    if (set.add(item.getEntrustId())) {
                        size--;
                        continue;
                    }
                } else {
                    item.setShareAmt(ArithmeticUtil.doAdd(item.getShareAmt(), addDimension));
                    tailDifference = ArithmeticUtil.subtract(tailDifference, addDimension);
                }
            }
        }
    }

    /**
     * 尾差处理
     *
     * @param ptlAllocations
     * @param tailDifference
     * @param type           type=B06  交易费 type=B08 结算费  type=01 净价总额 type=02 全价总额
     */
    public void handleFeeTailDifference(List<PtlAllocationDto> ptlAllocations, BigDecimal tailDifference, String type) {
        ptlAllocations.sort((p1, p2) -> p2.getShareAmt().compareTo(p1.getShareAmt()));
        //递增维度
        BigDecimal addDimension = amtDimension;
        int size = ptlAllocations.size();
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0 && size > 0) {
            if (tailDifference.compareTo(new BigDecimal("1")) >= 0) {
                addDimension = ArithmeticUtil.divide(tailDifference, size, 2);
            }
            for (PtlAllocationDto item : ptlAllocations) {
                if (tailDifference.compareTo(BigDecimal.ZERO) > 0) {
                    if (StringUtil.equals(type, BussinessDictCodeList.费用类型_交易手续费.getValue())) {
                        item.setDealFeeAmt(ArithmeticUtil.doAdd(item.getDealFeeAmt(), addDimension));
                    }
                    if (StringUtil.equals(type, BussinessDictCodeList.费用类型_结算手续费.getValue())) {
                        item.setSettFeeAmt(ArithmeticUtil.doAdd(item.getSettFeeAmt(), addDimension));
                    }
                    if (StringUtil.equals(type, "01")) {
                        item.setCpriceAmt(ArithmeticUtil.doAdd(item.getCpriceAmt(), addDimension));
                    }
                    if (StringUtil.equals(type, "02")) {
                        item.setFpriceAmt(ArithmeticUtil.doAdd(item.getFpriceAmt(), addDimension));
                    }
                    tailDifference = ArithmeticUtil.subtract(tailDifference, addDimension);
                }
            }
        }
    }

    /**
     * 计算费用
     *
     * @param ptlAllocations
     * @param shareAmt       券面总额
     * @param dealFeeAmt     交易费
     * @param settFeeAmt     结算费
     */
    public void calFee(List<PtlAllocationDto> ptlAllocations, BigDecimal shareAmt, BigDecimal dealFeeAmt, BigDecimal settFeeAmt) {
        BigDecimal tailDifferenceDealFeeAmt = dealFeeAmt;
        BigDecimal tailDifferenceSettFeeamt = settFeeAmt;
        for (PtlAllocationDto item : ptlAllocations) {
            item.setDealFeeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getShareAmt(), dealFeeAmt), shareAmt, 2, RoundingMode.DOWN));
            item.setSettFeeAmt(ArithmeticUtil.divide(settFeeAmt, BigDecimal.valueOf(ptlAllocations.size()), 2, RoundingMode.DOWN));
            tailDifferenceDealFeeAmt = ArithmeticUtil.subtract(tailDifferenceDealFeeAmt, item.getDealFeeAmt());
            tailDifferenceSettFeeamt = ArithmeticUtil.subtract(tailDifferenceSettFeeamt, item.getSettFeeAmt());
        }
        handleFeeTailDifference(ptlAllocations, tailDifferenceDealFeeAmt, BussinessDictCodeList.费用类型_交易手续费.getValue());
        handleFeeTailDifference(ptlAllocations, tailDifferenceSettFeeamt, BussinessDictCodeList.费用类型_结算手续费.getValue());
    }

    /**
     * 计算组合列表其他值(除费用)
     *
     * @param ptlAllocations
     * @param request
     */
    public void calOtherForPtlAllocation(List<PtlAllocationDto> ptlAllocations, AutoAllocationRequest request) {
        //模式
        //String sellPara = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
        //String ps = request.getPs();
        BigDecimal tailDifferenceFpriceAmt = request.getFpriceAmt().setScale(2,RoundingMode.HALF_UP);
        BigDecimal tailDifferenceCpriceAmt = request.getCpriceAmt().setScale(2,RoundingMode.HALF_UP);;
        for (PtlAllocationDto item : ptlAllocations) {
            BigDecimal shareAmt = ArithmeticUtil.multiply(item.getShareAmt(), new BigDecimal(10000));
            item.setShareAmt(shareAmt);
            //TODO 需求变更
            //item.setOurEntNum(shareAmt);
            /*if(StringUtil.equals(sellPara,"02")){
                item.setOurEntNum(shareAmt);
                if(StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(ps, DictCodeList.交易方向_行权_回售.getValue())){
                    item.setLendinEntNum(shareAmt);
                }
                if(StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())){
                    item.setSellAbleNum(shareAmt);
                }
            }*/
            item.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(shareAmt, request.getCpriceAmt()), request.getShareAmt(), 2, RoundingMode.HALF_DOWN));
            item.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(shareAmt, request.getFpriceAmt()), request.getShareAmt(), 2, RoundingMode.HALF_DOWN));
            tailDifferenceCpriceAmt = ArithmeticUtil.subtract(tailDifferenceCpriceAmt, item.getCpriceAmt());
            tailDifferenceFpriceAmt = ArithmeticUtil.subtract(tailDifferenceFpriceAmt, item.getFpriceAmt());
        }
        handleFeeTailDifference(ptlAllocations, tailDifferenceCpriceAmt, "01");
        handleFeeTailDifference(ptlAllocations, tailDifferenceFpriceAmt, "02");
        ptlAllocations.forEach(item -> {
            item.setIntAmt(ArithmeticUtil.subtract(item.getFpriceAmt(), item.getCpriceAmt()));
        });
    }


    public Date getNextWorkDay(Date tradeDate, int days) {
        if (dateCalService != null) {
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate,
                    DateUtil.addDays(tradeDate, 31), CacheUtil.getSysParam("ibuVar_calendarId"));
            return dateCalService.calculationTradingDay(tradeDate, days, holiday, "1");
        }
        return null;
    }

    /**
     * 计算净价、到期收益率、行权收益率
     */
    public CalRespone calAmt(CalRequest dto) {
        CalRespone calRespone = new CalRespone();
        BeanUtil.copyPropertiesIgnoreNull(calRespone,dto);
        final int SCALE = 12;
        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        YieldPriceInParameter yieldPriceInParameter = null;
        PriceYieldInParameter priceYieldInParameter = null;
        YieldPriceOutParameter yieldPriceOutParameter = null;
        PriceYieldOutParameter priceYieldOutParameter = null;
        Map<String, Object> tempParamMap = new HashMap<String, Object>();
        tempParamMap.put("finprodId", dto.getFinprodId());
        tempParamMap.put("ps", dto.getPs());
        List<FinCashCalcRule> finCashCalcRuleList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectBondsIntResetList",
                tempParamMap, FinCashCalcRule.class);

        // c.获取债券的付息计划信息
        List<FinCashPlanDto> finCashPlanList = restTemplate.postForList(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getFinCashPlan",
                tempParamMap, FinCashPlanDto.class);
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                    DictCodeListExt1.现金流一级类型_利息.getValue())
                    && item.getVdateUnadjust() != null) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(DateUtil.dateToLocal(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(DateUtil.dateToLocal(item.getMdateUnadjust()));
                schedule.setActualStartDate(DateUtil.dateToLocal(item.getVdate()));
                schedule.setActualCutoffDate(DateUtil.dateToLocal(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                invScheduleList.add(schedule);

                intSchList.add(item);
            }
        }

        // d.获取债券基本资料信息
        SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecInfoModel",
                tempParamMap, SecInfoModelDto.class);
        SecInfoModel secInfoModel = this.toSecInfoModel(secInfoModelDto);
        // e.获取债券的还本计划信息
        List<SecPrinSchedule> secPrinScheduleList = new ArrayList<SecPrinSchedule>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(),
                    DictCodeListExt1.现金流一级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
                schedule.setFlag(
                        StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(DateUtil.dateToLocal(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }

        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(
                    BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            //多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        yieldPriceInParameter = new YieldPriceInParameter();
        yieldPriceInParameter.setCalDate(DateUtil.dateToLocal(dto.getSettleDate()));
        yieldPriceInParameter.setSecInfoModel(secInfoModel);
        yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
        yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
        yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
        priceYieldInParameter = new PriceYieldInParameter();
        priceYieldInParameter.setCalDate(DateUtil.dateToLocal(dto.getSettleDate()));
        priceYieldInParameter.setSecInfoModel(secInfoModel);
        priceYieldInParameter.setSecIntScheduleList(invScheduleList);
        priceYieldInParameter.setSecPrinScheduleList(secPrinScheduleList);
        priceYieldInParameter.setPaperIrResetList(paperIrResetList);
        yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
            if (isNotFomartSec(intSchList, secInfoModelDto)) {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                priceYieldInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
            } else {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
            }
        }
        yieldPriceInParameter.setCalMethod(secInfoModel.getCalMethod());
        priceYieldInParameter.setCalMethod(secInfoModel.getCalMethod());
        // 后续区间利息计算方案
        if (IntRateType.浮动利率.equals(secInfoModel.getIntRateType())) {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
        } else {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
        }
        // 百元面值应计利息
        BigDecimal accurInt = BigDecimal.ZERO;
        // 交易方式为“一级市场”时，单位应计利息=应计利息总额=0
        // 交易方式为“二级市场”时，单位应计利息 =e取得的百元面值应计利息/100*a取得的当前债券面额
        Date tempSettleDate = dto.getSettleDate();
        // a.当前债券面额取得（根据还本计划计算）
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("finprodId", dto.getFinprodId());
        paraMap.put("settleDate", DateUtil.format(tempSettleDate));
        BigDecimal curSecAmt = restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getCurSecAmt",
                paraMap, BigDecimal.class);

        AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
        // 计算日
//            accurIntCalInParameter.setCalDate(this.toLocalDate(dto.getSettleDate()));
        accurIntCalInParameter.setCalDate(DateUtil.dateToLocal(tempSettleDate));
        // 债券基本资料
        accurIntCalInParameter.setSecInfoModel(secInfoModel);
        // 付息计划
        accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
        // 利率重置信息
        accurIntCalInParameter.setPaperIrResetList(paperIrResetList);

        //交易所债券特殊处理start----------------------------------------------------------------
        AccurIntCalOutParameter out = null;

        if (out != null) {
            accurInt = ArithmeticUtil.round(out.getInterest(), SCALE, RoundingMode.HALF_UP);
        } else {
            // 从计算工厂中创建应计利息计算器
            AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
            // 计算利息
            AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
            accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
        }

        // 单位应计利息
        if (calRespone.getUnitInt() == null) {
            calRespone.setUnitInt(ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                    Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                            "SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam(
                            "SEC_INT_SCALE_BANK")), RoundingMode.HALF_UP));
        }
        try {
            if(StringUtil.isNotEmpty(dto.getCalculateField())){
                // 单位净价
                if (StringUtil.equals(dto.getCalculateField(), "01")) {
                    // 单位全价 = 单位净价 + 单位应计利息
                    calRespone.setUnitFprice(ArithmeticUtil.doAdd(dto.getPrice(), calRespone.getUnitInt()));
                    // 全价总额 = 单位全价 * 券面总额/100
                    calRespone.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(calRespone.getUnitFprice(), calRespone.getShareAmt()),
                            new BigDecimal(100), SCALE));
                    calRespone.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(calRespone.getPrice(), calRespone.getShareAmt()),
                            new BigDecimal(100), SCALE));
                }
                switch (dto.getCalculateField()) {
                    // 单位净价
                    case "01":
                        // 全价总额==>到期收益率
                        yieldPriceInParameter.setFullPrice(
                                calRespone.getUnitFprice()/* dto.getFpriceAmt() */);
                        yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                yieldPriceInParameter);
                        calRespone.setMatureYield(yieldPriceOutParameter.getMatYield());
                        if (secInfoModelDto.getNextExeDate() != null) {
                            // 全价总额==>行权收益率
                            yieldPriceInParameter.setNextExeDate(
                                    DateUtil.dateToLocal(secInfoModelDto.getNextExeDate()));
                            yieldPriceInParameter.setNextExePrice(
                                    secInfoModelDto.getNextExercisePrice());
                            if (yieldPriceInParameter.getNextExePrice() != null) {
                                yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                        yieldPriceInParameter);
                                calRespone.setExerYield(yieldPriceOutParameter.getExeYield());
                            } else {
                                calRespone.setExerYield(null);
                            }
                        }
                        break;
                    // 到期收益率
                    case "02":
                        // 到期收益率==>全价总额
                        priceYieldInParameter.setMatYield(dto.getMatureYield());
                        priceYieldOutParameter = priceByYieldCalculator.calculate(
                                priceYieldInParameter);
                        // dto.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                        calRespone.setUnitFprice(priceYieldOutParameter.getFullPrice());
                        // 全价总额==>行权收益率
                        if (secInfoModelDto.getNextExeDate() != null) {
                            yieldPriceInParameter.setFullPrice(
                                    calRespone.getUnitFprice()/* dto.getFpriceAmt() */);
                            yieldPriceInParameter.setNextExeDate(
                                    DateUtil.dateToLocal(secInfoModelDto.getNextExeDate()));
                            yieldPriceInParameter.setNextExePrice(
                                    secInfoModelDto.getNextExercisePrice());
                            if (yieldPriceInParameter.getNextExePrice() != null) {
                                yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                        yieldPriceInParameter);
                                // dto.setExerYield(yieldPriceOutParameter.getMatYield());
                                calRespone.setExerYield(yieldPriceOutParameter.getExeYield());
                            } else {
                                calRespone.setExerYield(null);
                            }
                        }
                        break;
                    // 行权收益率
                    case "03":
                        // 行权收益率==>全价总额
                        if (secInfoModelDto.getNextExeDate() != null) {
//                        priceYieldInParameter.setMatYield(dto.getMatureYield());
                            priceYieldInParameter.setMatYield(dto.getExerYield());//行权收益
                            yieldPriceInParameter.setNextExeDate(
                                    DateUtil.dateToLocal(secInfoModelDto.getNextExeDate()));
                            yieldPriceInParameter.setNextExePrice(
                                    secInfoModelDto.getNextExercisePrice());
                            priceYieldOutParameter = priceByYieldCalculator.calculate(
                                    priceYieldInParameter);
                            calRespone.setFpriceAmt(priceYieldOutParameter.getFullPrice());
                            calRespone.setUnitFprice(priceYieldOutParameter.getFullPrice());
                            // 全价总额==>到期收益率
                            yieldPriceInParameter.setFullPrice(calRespone.getFpriceAmt());
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(
                                    yieldPriceInParameter);
                            calRespone.setMatureYield(yieldPriceOutParameter.getMatYield());
                        }
                        break;
                    default:
                        break;
                }
                // 到期收益率 或 行权收益率
                if (StringUtil.equals(dto.getCalculateField(), "02")
                        || StringUtil.equals(dto.getCalculateField(), "03")) {
                    calRespone.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(calRespone.getUnitFprice(), calRespone.getShareAmt()),
                            new BigDecimal(100), SCALE));
                    // 单位净价 = 单位全价 - 单位应计利息
                    calRespone.setPrice(com.joyintech.calculation.util.ArithmeticUtil.doSub(calRespone.getUnitFprice(), calRespone.getUnitInt()));
                    calRespone.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(calRespone.getPrice(), calRespone.getShareAmt()),
                            new BigDecimal(100), SCALE));
                }
                if(null!=calRespone.getFpriceAmt()&&null!=calRespone.getCpriceAmt()){
                    calRespone.setIntAmt(ArithmeticUtil.subtract(calRespone.getFpriceAmt(),calRespone.getCpriceAmt()));
                }
            }
            if(StringUtil.isNotEmpty(secInfoModelDto.getAssetType11())){
                //资产四级分类
                calRespone.setAssetType11(secInfoModelDto.getAssetType11());
            }
            // 每百元金额
            calRespone.setParValue(secCalRequestQuoteService.getCurSecAmt(dto.getSettleDate(), dto.getFinprodId()));
            // 本金额
            calRespone.setParValueAmt(dto.getShareAmt().multiply(calRespone.getParValue())
                    .divide(SecCalUtil.ONE_HUNDRED).setScale(SecCalUtil.SCAL_CASH,
                            RoundingMode.HALF_UP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calRespone;
    }

    private SecInfoModel toSecInfoModel(SecInfoModelDto secInfoModelDto) {
        SecInfoModel secInfoModel = new SecInfoModel();
        secInfoModel.setStartDate(DateUtil.dateToLocal(secInfoModelDto.getStartDate()));
        secInfoModel.setEndDate(DateUtil.dateToLocal(secInfoModelDto.getEndDate()));
        if(null!=secInfoModelDto.getFirstPayDate()){
            secInfoModel.setFirstPayDate(DateUtil.dateToLocal(secInfoModelDto.getFirstPayDate()));
        }
        // 利率类型
        if (StringUtil.equals(DictCodeList.利率类型_固定利率.getValue(),
                secInfoModelDto.getIntRateType())) {
            secInfoModel.setIntRateType(IntRateType.固定利率);
        }
        else {
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }

        if (StringUtil.isNotBlank(secInfoModelDto.getBasis())) {
            String basis = StringUtil.splitToList(GlobalConstant.COMMA, secInfoModelDto.getBasis()).get(0);
            DictCodeList basisDict = DictCodeList.from(DictCode.计息基础, basis);
            if (basisDict != null){
                switch (basisDict) {
                    case 计息基础_30_360:
                        secInfoModel.setBasis(Basis._30_360);
                        break;
                    case 计息基础_A_360:
                        secInfoModel.setBasis(Basis.A_360);
                        break;
                    case 计息基础_A_365:
                        secInfoModel.setBasis(Basis.A_365);
                        break;
                    case 计息基础_A_365F:
                        secInfoModel.setBasis(Basis.A_365F);
                        break;
                    case 计息基础_A_A:
                    case 计息基础_A_A_平均分配:
                    case 计息基础_A_A_按实际天数分配:
                        secInfoModel.setBasis(Basis.A_A);
                        break;
                }
                if (basisDict.equals(DictCodeList.计息基础_A_A_平均分配)) {
                    secInfoModel.setAllocationType(AllocationType.平均分配);
                }
                else {
                    secInfoModel.setAllocationType(AllocationType.按实际天数分配);
                }
            }else {
                DictCodeListExt3 basisDict3 = DictCodeListExt3.from(DictCode.计息基础, basis);
                if (basisDict3 != null){
                    switch (basisDict3) {
                        case 计息基础_30U_360:
                            secInfoModel.setBasis(Basis._30U_360);
                            break;
                    }

                }
            }
        }
        DictCodeList bondsTypeDict = DictCodeList.from(DictCode.息票品种,
                secInfoModelDto.getBondsType());
        switch (bondsTypeDict) {
            case 息票品种_零息:
                secInfoModel.setBondsType(Variety.零息);
                break;
            case 息票品种_利随本清:
                secInfoModel.setBondsType(Variety.利随本清);
                break;
            case 息票品种_贴现:
                secInfoModel.setBondsType(Variety.贴现);
                break;
            case 息票品种_附息:
                secInfoModel.setBondsType(Variety.附息);
                break;
        }

        // 一体化对应etliu start
//        if (StringUtil.equals(DictCodeList.推算方法_起息日向后.getValue(),
        if (StringUtil.equals(DictCodeList.推算方式_起息日向后.getValue(),
                secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.起息日向后);
        }
//        else if (StringUtil.equals(DictCodeList.推算方法_到期日向前.getValue(),
        else if (StringUtil.equals(DictCodeList.推算方式_到期日向前.getValue(),
                secInfoModelDto.getCalMethod())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.到期日向前);
        }
        else {
            secInfoModel.setCalMethod(SecIntSchCalMethod.首次付息日);
        }

//        if (StringUtil.equals(DictCodeList.支付频率_付息频率_月付.getValue(),
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.月付);
        }
//        else if (StringUtil.equals(DictCodeList.支付频率_付息频率_季付.getValue(),
        else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.季付);
        }
//        else if (StringUtil.equals(DictCodeList.支付频率_付息频率_半年付.getValue(),
        else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
                // 一体化对应etliu end
                secInfoModelDto.getYearIntRate())) {
            secInfoModel.setYearIntRate(IntFrequency.半年付);
        }
        else {
            secInfoModel.setYearIntRate(IntFrequency.年付);
        }

        secInfoModel.setIssuePrice(secInfoModelDto.getIssuePrice());

        return secInfoModel;
    }

    public boolean isNotFomartSec(List<FinCashPlanDto> secIntScheduleList, SecInfoModelDto secInfoModelDto) {
        // 根据【实际起始日】排序付息计划
        Collections.sort(secIntScheduleList, new Comparator<FinCashPlanDto>() {
            @Override
            public int compare(FinCashPlanDto o1, FinCashPlanDto o2) {
                return o1.getVdateUnadjust().compareTo(o2.getVdateUnadjust());
            }
        });
        if (StringUtil.equals(DictCodeList.频率_月.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 1);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        else if (StringUtil.equals(DictCodeList.频率_季.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 3);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        else if (StringUtil.equals(DictCodeList.频率_半年.getValue(),
                secInfoModelDto.getYearIntRate())) {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdateUnadjust(), 6);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        else {
            for (FinCashPlanDto she : secIntScheduleList) {
                Date mDate = DateUtil.addYear(she.getVdateUnadjust(), 1);
                if (mDate.compareTo(she.getMdateUnadjust()) != 0) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 计算偏离度
     * @param param
     * @return
     */
    public String calcDeviationDegree(Map<String,Object> param){
        return  trdBondInvestTrade002Service.calcDeviationDegree(param);
    }
}


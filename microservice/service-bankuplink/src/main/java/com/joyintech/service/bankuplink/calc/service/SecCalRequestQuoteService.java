package com.joyintech.service.bankuplink.calc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cib.sam.algo.basicUtil.AlgCommonConstants;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.calc.dao.SecCalRequestQuoteMapper;
import com.joyintech.service.bankuplink.calc.dto.SecCalInfoModel;
import com.joyintech.service.bankuplink.calc.dto.SecInfoModelDto;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;

/**
 * 现券买卖询价交易维护
 * 
 * @author wot_duming
 */
@Service
public class SecCalRequestQuoteService extends BaseService {

    /**
     * SaSecDialogquoteDealAction日志实例
     */
    private static final Log LOGGER = LogFactory.getLog(SecCalRequestQuoteService.class);

    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * {@linkplain}
     */
    @Autowired
    private SecCalRequestQuoteMapper secCalRequestQuoteMapper;

    /**
     * 债券基本信息类型转换
     * 
     * @param secInfoModelDto 债券基本信息
     * @return 转换后的债券基本信息
     * @see
     * @since 1.0
     */
    private SecCalInfoModel convertToSecCalInfoModel(SecInfoModelDto secInfoModelDto) {
        SecCalInfoModel secCalInfoModel = new SecCalInfoModel();
        secCalInfoModel.setCouponspecies(secInfoModelDto.getBondsType());
        secCalInfoModel.setFirstrateday(secInfoModelDto.getFirstPayDate());
        secCalInfoModel.setVdate(secInfoModelDto.getStartDate());
        secCalInfoModel.setMdate(secInfoModelDto.getEndDate());
        secCalInfoModel.setIssueprice(secInfoModelDto.getIssuePrice());
        secCalInfoModel.setFacevalue(secInfoModelDto.getFaceValue() != null ? secInfoModelDto.getFaceValue() : new BigDecimal(100));
        secCalInfoModel.setPaperir(secInfoModelDto.getPaperir());
        secCalInfoModel.setInterestrate(secInfoModelDto.getIntRateType());
        // 付息频率
        int interestfrequency = 1;
        if (StringUtil.equals(BussinessDictCodeList.频率_月.getValue(),
            secInfoModelDto.getYearIntRate())) {
            interestfrequency = 12;
        }
        else if (StringUtil.equals(BussinessDictCodeList.频率_季.getValue(),
            secInfoModelDto.getYearIntRate())) {
            interestfrequency = 4;
        }
        else if (StringUtil.equals(BussinessDictCodeList.频率_半年.getValue(),
            secInfoModelDto.getYearIntRate())) {
            interestfrequency = 2;
        }
        secCalInfoModel.setInterestfrequency(interestfrequency);
        // 计息基础
        String basis = AlgCommonConstants.BASIS_A_A;
        if (StringUtil.isNotBlank(secInfoModelDto.getBasis())) {
            String tmepBasis = StringUtil.splitToList(GlobalConstant.COMMA,
                secInfoModelDto.getBasis()).get(0);
            BussinessDictCodeList basisDict = BussinessDictCodeList.from(BussinessDictCode.计息基础,
                tmepBasis);
            switch (basisDict) {
                case 计息基础_30_360:
                    basis = AlgCommonConstants.BASIS_30_360;
                    break;
                case 计息基础_A_360:
                    basis = AlgCommonConstants.BASIS_A_360;
                    break;
                case 计息基础_A_365:
                    basis = AlgCommonConstants.BASIS_A_365;
                    break;
                case 计息基础_A_365F:
                    basis = AlgCommonConstants.BASIS_A_365F;
                    break;
                case 计息基础_A_A:
                case 计息基础_A_A_B_COUP:
                case 计息基础_A_A_B_DAY:
                    basis = AlgCommonConstants.BASIS_A_A;
                    break;
            }
            secCalInfoModel.setRatebasic(basis);

            if (basisDict.equals(BussinessDictCodeList.计息基础_A_A_B_COUP)) {
                secCalInfoModel.setIntpayrule(AlgCommonConstants.INT_PAY_RULE_J);
            }
            else {
                secCalInfoModel.setIntpayrule(AlgCommonConstants.INT_PAY_RULE_D);
            }
        }

        // secCalInfoModel.setRatecode(secInfoModelDto.get);;
        return secCalInfoModel;
    }

    /**
     * 根据债券交易的录入信息计算各项指标
     * 
     * @param aActionContext Web层的上下文
     */
    public void calSecDeal(SecInfoModelDto dto) {
        LOGGER.info("ABEGIN:获取计算债券的各种指标");
        try {
            /**
             * 根据债券交易的录入信息计算各项指标 说明
             */
            // 净价
            BigDecimal cleanPrice = BigDecimal.ZERO;
            // 全价
            BigDecimal dirtyPrice = BigDecimal.ZERO;
            // 到期收益率
            BigDecimal yield = BigDecimal.ZERO;
            // 行权收益率
            BigDecimal xyield = BigDecimal.ZERO;
            // 当日应计利息
            BigDecimal accrueint = BigDecimal.ZERO;
            // 当日应计利息总额
            BigDecimal accrueintAmt = BigDecimal.ZERO;
            // 净价总额
            BigDecimal cleanPriceAmt = BigDecimal.ZERO;
            // 全价总额
            BigDecimal dirtyPriceAmt = BigDecimal.ZERO;

            // add by zhangliang 20200520 start abs显示本金额和每百元金额
            BigDecimal absPrinAmtHun = BigDecimal.ZERO;
            BigDecimal absPrinAmt = BigDecimal.ZERO;
            // add by zhangliang 20200520 end abs显示本金额和每百元金额

            // 券面总额
            BigDecimal prinamt = dto.getPrinAmt();

            // 查询债券基本信息
            SecInfoModelDto secInfoModelDto = this.getSecInfoModel(dto.getFinprodId());
            // 转换成算法需要
            SecCalInfoModel secCalInfoModel = this.convertToSecCalInfoModel(secInfoModelDto);

            // 债券种类
            String couponspecies = secInfoModelDto.getBondsType();
            // 未来现金流数值和剩余期限
            double[][] fwdEncashYearAndCashflow = null;
            // 判断债券起始日和到期日
            String vdate = DateUtil.format(secInfoModelDto.getStartDate());
            String mdate = DateUtil.format(secInfoModelDto.getEndDate());
            // 债券交割日
            String cDate = DateUtil.format(dto.getSettleDate());
            // 如果交易日小于起息日
            if (DateUtil.compareDate(cDate, vdate) < 0) {
                cDate = vdate;
            }
            // 赎回日是否有效
            String flag = "";
            // 取赎回日,回售日大于交割日且离交割日最近的日期
            Date closedToSettleDate = null;
            // 下一行权日
            Date nextExeDate = secInfoModelDto.getNextExeDate();
            /**
             * 债券的赎回日,回售日都为空,行权收益率不可勾选 债券的赎回日,回售日都小于交割日,行权收益率不可勾选 债券的赎回日,回售日存在一个大于交割日,行权收益率可勾选
             */
            if (nextExeDate != null) {
                if (DateUtil.compareDate(nextExeDate, dto.getSettleDate()) >= 0) {
                    flag = "1";
                    /**
                     * 目标：取赎回日,回售日大于交割日且离交割日最近的日期 默认取赎回日,回售日中最大的日期Max 在将该日期与交割日做比较,取大于等于交割日的日期C,
                     * 在将C日期与Max日期作比较,取最小的日期
                     */
                    closedToSettleDate = nextExeDate;
                    if (nextExeDate.getTime() >= dto.getSettleDate().getTime()) {
                        closedToSettleDate = new Date(
                            Math.min(closedToSettleDate.getTime(), nextExeDate.getTime()));
                    }
                }
                else {
                    flag = "0";
                }
            }
            // tmv.setEnddate(iPlatformDate.getSysDate());
            
            // 调用算法取得现金流
            SecCal secCal = new SecCal();
            // 发行价格
            // BigDecimal faceValue = secInfoModelDto.getFaceValue();
            BigDecimal faceValue = new BigDecimal(100);
            // 查询付息计划
            List<FinCashPlan> finCashPlanList = this.getFinCashPlan(dto.getFinprodId());
            Date tempDate = DateUtil.compareDate(dto.getSettleDate(),
                secInfoModelDto.getStartDate()) > 0 ? dto.getSettleDate() : secInfoModelDto.getStartDate();
            finCashPlanList = finCashPlanList.stream().filter(x ->
            // 过滤实际结束日比交割日小于等于的记录
            DateUtil.compareDate(x.getMdate(), tempDate) > 0).collect(Collectors.toList());

            // //债券息票品种--附息
            if (StringUtil.equals(BussinessDictCodeList.息票品种_附息.getValue(),
                secInfoModelDto.getBondsType())) {
                /**
                 * 因为系统对于还本时按照卖出进行操作的，所以对于未来现金流需要进行处理。将其转换成还本前的数字进行生成未来现金流 20190802 dongenkuo 不处理了
                 */
                List<FinCashPlan> saSecscheduleList = finCashPlanList;
                // 计算未来现金流
                fwdEncashYearAndCashflow = secCal.getForwadCashFlow(cDate, saSecscheduleList,
                    couponspecies, secCalInfoModel.getInterestfrequency(),
                    secCalInfoModel.getRatebasic());
                // 如果为付息券，可能包含提前还本，需要查询面额
                // for (int i = 0; i < saSecscheduleList.size(); i++) {
                // FinCashPlan saSecscheduleVO = saSecscheduleList.get(i);
                // // 找到交易日所在的付息区间[,)
                // if (tmv.getEnddate().compareTo(saSecscheduleVO.getIntereststartday()) >= 0
                // && tmv.getEnddate().compareTo(saSecscheduleVO.getInterestendday()) < 0) {
                // break;
                // }
                // }
            }

            // 计算当日应计利息
            // add by zjy@20180712 维护需求：贴现债，零息债券 --begin
            // SaSecinfoVO tmv1 = saSecinfoService.getSecInfoBySeccode(tmv.getSeccode());

            if (BussinessDictCodeList.息票品种_附息.getValue().equals(secInfoModelDto.getBondsType())
                || BussinessDictCodeList.息票品种_贴现.getValue().equals(
                    secInfoModelDto.getBondsType())) { // 贴现债，零息债
                // 为保持与外汇交易中心一致，暂时修改为全部走交易性，20190215，dongenkuo
                accrueint = secCal.calAccurIntForMarket(cDate, finCashPlanList, secCalInfoModel);
                // if("TRA".equals(tmv.getInvesttype())){ //会计类型
                // accrueint = secCal.calAccurInt(cDate, listSaSecscheduleVO, secinfoVO);
                // }else{
                // accrueint = new BigDecimal(0); //非交易性
                // }
            }
            else {
                // add by zjy@20180712 维护需求：贴现债，零息债券 --end
                accrueint = secCal.calAccurIntForMarket(cDate, finCashPlanList, secCalInfoModel);
            }

            // 当日应计利息总额
            accrueintAmt = accrueint.multiply(prinamt).divide(faceValue, SecCalUtil.SCAL_CASH,
                RoundingMode.HALF_UP);

            // 0,全价，1为净价，2到期收益率，3行权收益率 传值到后台计算数据信息
            // 用价格算收益率
            if (StringUtil.equals(dto.getCalculateField(), BussinessDictCodeList.计算选择方式_净价.getValue())
                || StringUtil.equals(dto.getCalculateField(), BussinessDictCodeList.计算选择方式_全价总额.getValue())) {
                // 用净价计算
                if (StringUtil.equals(dto.getCalculateField(), BussinessDictCodeList.计算选择方式_净价.getValue())) {
                    // 净价
                    cleanPrice = dto.getCleanprice();
                    // 全价
                    dirtyPrice = cleanPrice.add(accrueint);
                    // 净价总额
                    cleanPriceAmt = cleanPrice.multiply(prinamt).divide(faceValue,
                        SecCalUtil.SCAL_CASH, RoundingMode.HALF_UP);
                    // 全价总额=净价总额+应计利息总额
                    dirtyPriceAmt = cleanPriceAmt.add(accrueintAmt);
                    // add by gaoss 20190411 全价总额的计算
                }
                else if (StringUtil.equals(dto.getCalculateField(), BussinessDictCodeList.计算选择方式_全价总额.getValue())) {
                    // 全价总额(两位)
                    dirtyPriceAmt = dto.getDpriceamt().setScale(SecCalUtil.SCAL_CASH,
                        RoundingMode.HALF_UP);
                    // 全价
                    dirtyPrice = dirtyPriceAmt.multiply(faceValue).divide(prinamt,
                        SecCalUtil.SCAL_PRICE_SEC, RoundingMode.HALF_UP);
                    // 净价
                    cleanPrice = dirtyPrice.subtract(accrueint);
                    // 净价总额 = 全价总额-应计利息总额
                    cleanPriceAmt = dirtyPriceAmt.subtract(accrueintAmt);
                }
                // 计算债券到期收益率
                // 为了在生产上打出日志 查找错误
                LOGGER.error("ABEGIN:计算债券到期收益率:为了在生产上打出日志 查找错误 ");
                LOGGER.error("cDate:" + cDate + ";dirtyPrice:" + String.valueOf(dirtyPrice)
                             + ";seccode:" + dto.getFinprodId());
                String fwd0 = "";
                if (fwdEncashYearAndCashflow != null) {
                    if (fwdEncashYearAndCashflow[0] == null
                        || fwdEncashYearAndCashflow[0].length == 0) {
                    }
                    else {
                        for (int i = 0; i < fwdEncashYearAndCashflow[0].length; i++ ) {
                            fwd0 = fwd0 + String.valueOf(fwdEncashYearAndCashflow[0][i]) + ";";
                        }
                    }
                    String fwd1 = "";
                    if (fwdEncashYearAndCashflow[1] == null
                        || fwdEncashYearAndCashflow[1].length == 0) {
                    }
                    else {
                        for (int i = 0; i < fwdEncashYearAndCashflow[1].length; i++ ) {
                            fwd1 = fwd1 + String.valueOf(fwdEncashYearAndCashflow[1][i]) + ";";
                        }
                    }
                    LOGGER.error("fwdEncashYearAndCashflow[0]:" + fwd0
                                 + "fwdEncashYearAndCashflow[1]:" + fwd1);
                }

                // 按债券类别用全价计算到期收益率
                yield = secCal.ytmByPrice(cDate, fwdEncashYearAndCashflow,
                    dirtyPrice.doubleValue(), secCalInfoModel);
                // 行权收益率
                // if(secinfoVO.getRedemptiondate() != null){
                // secinfoVO.setMdate(secinfoVO.getRedemptiondate());
                // xyield = secCal.ytmByPrice(cDate,
                // fwdEncashYearAndCashflow,dirtyPrice.doubleValue(), secinfoVO);
                // }else{
                // xyield = yield;
                // }
                if ("1".equals(flag)) {
                    // secinfoVO.setMdate(secinfoVO.getRedemptiondate());
                    secCalInfoModel.setMdate(closedToSettleDate);
                    double[][] xjl = getXjl(nextExeDate, finCashPlanList,
                        fwdEncashYearAndCashflow);
                    xyield = secCal.ytmByPrice(cDate, xjl, dirtyPrice.doubleValue(),
                        secCalInfoModel);

                }
                else {
                    xyield = null;
                }
                LOGGER.error("AEND:计算债券到期收益率" + String.valueOf(yield));
            }
            else if (StringUtil.equals(dto.getCalculateField(), BussinessDictCodeList.计算选择方式_到期收益率.getValue())) {// 到期收益率，
                // 到期收益率
                yield = dto.getMatureyield().divide(SecCalUtil.ONE_HUNDRED);
                // 按债券类别用到期收益率计算全价
                dirtyPrice = secCal.priceByYtm(cDate, fwdEncashYearAndCashflow,
                    yield.doubleValue(), secCalInfoModel);
                // 净价
                cleanPrice = dirtyPrice.subtract(accrueint);
                // 净价总额
                cleanPriceAmt = cleanPrice.multiply(prinamt).divide(faceValue,
                    SecCalUtil.SCAL_CASH, RoundingMode.HALF_UP);
                // 全价总额 = 净价总额+应计利息总额
                dirtyPriceAmt = cleanPriceAmt.add(accrueintAmt);
                // 行权收益率
                // if(secinfoVO.getRedemptiondate() != null){
                // secinfoVO.setMdate(secinfoVO.getRedemptiondate());
                // xyield = secCal.ytmByPrice(cDate,
                // fwdEncashYearAndCashflow,dirtyPrice.doubleValue(), secinfoVO);
                // }else{
                // xyield = yield;
                // }
                if ("1".equals(flag)) {
                    // secinfoVO.setMdate(secinfoVO.getRedemptiondate());
                    secCalInfoModel.setMdate(closedToSettleDate);
                    double[][] xjl = getXjl(nextExeDate, finCashPlanList,
                        fwdEncashYearAndCashflow);
                    xyield = secCal.ytmByPrice(cDate, xjl, dirtyPrice.doubleValue(),
                        secCalInfoModel);
                }
                else {
                    xyield = null;
                }
            }
            else if (StringUtil.equals(dto.getCalculateField(), BussinessDictCodeList.计算选择方式_行权收益率.getValue())) {// 行权收益率
                // 全价
                BigDecimal dirtyPriceNew = BigDecimal.ZERO;
                // 行权收益率
                xyield = dto.getExerciseyield().divide(SecCalUtil.ONE_HUNDRED);
                // if(secinfoVO.getRedemptiondate() == null){
                // // 按债券类别用到期收益率计算全价
                // dirtyPriceNew = secCal.priceByYtm(cDate,
                // fwdEncashYearAndCashflow,xyield.doubleValue(), secinfoVO);
                // yield = xyield;
                // }else{
                // // 按债券类别用到期收益率计算全价
                // secinfoVO.setMdate(secinfoVO.getRedemptiondate());
                // dirtyPriceNew = secCal.priceByYtm(cDate,
                // fwdEncashYearAndCashflow,xyield.doubleValue(), secinfoVO);
                // // 到期收益率
                // yield = secCal.ytmByPrice(cDate,
                // fwdEncashYearAndCashflow,dirtyPriceNew.doubleValue(), secinfoVO);
                // }
                if ("1".equals(flag)) {
                    // 按债券类别用到期收益率计算全价
                    // secinfoVO.setMdate(secinfoVO.getRedemptiondate());
                    secCalInfoModel.setMdate(closedToSettleDate);

                    double[][] xjl = getXjl(nextExeDate, finCashPlanList,
                        fwdEncashYearAndCashflow);

                    dirtyPriceNew = secCal.priceByYtm(cDate, xjl, xyield.doubleValue(),
                        secCalInfoModel);
                    // 到期收益率
                    yield = secCal.ytmByPrice(cDate, fwdEncashYearAndCashflow,
                        dirtyPriceNew.doubleValue(), secCalInfoModel);

                }
                else {
                    // 按债券类别用到期收益率计算全价
                    dirtyPriceNew = secCal.priceByYtm(cDate, fwdEncashYearAndCashflow,
                        xyield.doubleValue(), secCalInfoModel);
                    yield = xyield;
                }
                // 按债券类别用到期收益率计算全价
                // dirtyPrice = secCal.priceByYtm(cDate,
                // fwdEncashYearAndCashflow,yield.doubleValue(), secinfoVO);
                dirtyPrice = dirtyPriceNew;
                // 净价
                cleanPrice = dirtyPrice.subtract(accrueint);
                // 净价总额
                cleanPriceAmt = cleanPrice.multiply(prinamt).divide(faceValue,
                    SecCalUtil.SCAL_CASH, RoundingMode.HALF_UP);
                // 全价总额 = 净价总额+应计利息总额
                dirtyPriceAmt = cleanPriceAmt.add(accrueintAmt);
            }

            // 返回值赋值
            // 净价
            dto.setCleanprice(
                cleanPrice.setScale(SecCalUtil.SCAL_PRICE_SEC, RoundingMode.HALF_UP));
            // 全价
            dto.setDirtyprice(
                dirtyPrice.setScale(SecCalUtil.SCAL_PRICE_SEC, RoundingMode.HALF_UP));
            // 净价总额(2位小数)
            dto.setCpriceamt(cleanPriceAmt.setScale(SecCalUtil.SCAL_CASH, RoundingMode.HALF_UP));
            // 全价总额(2位小数)
            dto.setDpriceamt(dirtyPriceAmt.setScale(SecCalUtil.SCAL_CASH, RoundingMode.HALF_UP));
            // 应计利息
            dto.setAccuir(accrueint.setScale(SecCalUtil.SCAL_PRICE_SEC, RoundingMode.HALF_UP));
            // 应计利息总额(2位小数)
            dto.setAccuiramt(accrueintAmt.setScale(SecCalUtil.SCAL_CASH, RoundingMode.HALF_UP));
            // 到期收益率
            dto.setMatureyield(yield.multiply(SecCalUtil.ONE_HUNDRED).setScale(
                SecCalUtil.SCAL_PRICE, RoundingMode.HALF_UP));
            // 行权收益率
            if (xyield != null) {
                dto.setExerciseyield(xyield.multiply(SecCalUtil.ONE_HUNDRED).setScale(
                    SecCalUtil.SCAL_PRICE, RoundingMode.HALF_UP));
            }
            else {
                dto.setExerciseyield(null);
            }
            // 结算金额(2位小数) 结算金额=交易金额+应计利息总额
            dto.setSettleamt(cleanPriceAmt.add(accrueintAmt).setScale(SecCalUtil.SCAL_CASH,
                RoundingMode.HALF_UP));

            // 交易金额(2位小数) 交易金额=净价总额
            dto.setDealamt(cleanPriceAmt.setScale(SecCalUtil.SCAL_CASH, RoundingMode.HALF_UP));

            // 每百元金额
            dto.setAbsPrinAmtHun(this.getCurSecAmt(dto.getSettleDate(), dto.getFinprodId()));
            // 本金额
            dto.setAbsPrinAmt(dto.getPrinAmt().multiply(dto.getAbsPrinAmtHun())
                .divide(SecCalUtil.ONE_HUNDRED).setScale(SecCalUtil.SCAL_CASH,
                    RoundingMode.HALF_UP));

        }
        catch (Exception e) {
        	e.printStackTrace();
            LOGGER.error("MSG:调用算法计算异常.{}",e);
            throw new BusinessException("MSG:调用算法计算异常.");
        }
        // LOGGER.info("AFINISH:获取浮动利率");

    }

    /**
     * 转换数据
     *
     * @param date 日期
     * @return LocalDate
     * @see
     * @since 1.0
     */
    private LocalDate toLocalDate(Date date) {
        if (date != null) {
            return LocalDate.parse(DateUtil.format(date));
        }
        return null;
    }

    /**
     * 计算现金流
     * 
     * @param precashdate
     * @param redemptiondate
     * @param listSaSecscheduleVO
     * @param fwdEncashYearAndCashflow
     * @return
     */
    private double[][] getXjl(Date nextExeDate, List<FinCashPlan> listSaSecscheduleVO,
                              double[][] fwdEncashYearAndCashflow) {

        int num = 0;
        boolean isHsr = false;
        BigDecimal zfbj = BigDecimal.ZERO;

        Date xqDate = nextExeDate;
        for (int i = 0; i < listSaSecscheduleVO.size(); i++ ) {
            // 回售日等于理论付息结束日
            if (isHsr) {           	
                zfbj = zfbj.add(listSaSecscheduleVO.get(i).getPrinAmt() ==null?BigDecimal.ZERO:listSaSecscheduleVO.get(i).getPrinAmt());
            }
            if (xqDate.compareTo(listSaSecscheduleVO.get(i).getMdateTerm()) <= 0
                && xqDate.compareTo(listSaSecscheduleVO.get(i).getVdateTerm()) > 0) {
                num = i + 1;
                isHsr = true;
            }

        }

        if (num != 0) {
            double[][] xjl = new double[2][num];
            for (int j = 0; j < num; j++ ) {

                if (j == num - 1) {
                    xjl[0][j] = fwdEncashYearAndCashflow[0][j] + zfbj.doubleValue();
                }
                else {
                    xjl[0][j] = fwdEncashYearAndCashflow[0][j];
                }

                xjl[1][j] = fwdEncashYearAndCashflow[1][j];
            }

            return xjl;
        }
        else {
            return fwdEncashYearAndCashflow;
        }

    }

    /**
     * 查询债券基本信息
     *
     * @param finprodId 金融产品代码
     * @return 债券基本信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public SecInfoModelDto getSecInfoModel(String finprodId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("finprodType2", BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        map.put("cashType1", BussinessDictCodeList.现金流一级类型_利息.getValue());
        map.put("finprodType", BussinessDictCodeList.现金流业务类型_金融产品.getValue());
        map.put("sysDate", iPlatformDate.getSysDate());
        map.put("YES_NO_Y", BussinessDictCodeList.是否_是.getValue());

        List<SecInfoModelDto> resultList = secCalRequestQuoteMapper.getSecInfoModel(map);
        if (!StringUtil.isEmptyList(resultList)) {
            return resultList.get(0);
        }

        return null;
    }

    /**
     * 获取债券付息计划
     *
     * @param finprodId 金融产品代码
     * @return 债券付息计划
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<FinCashPlan> getFinCashPlan(String finprodId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("cashType1", BussinessDictCodeList.现金流一级类型_利息.getValue());
        map.put("finprodType", BussinessDictCodeList.现金流业务类型_金融产品.getValue());
        return secCalRequestQuoteMapper.getFinCashPlan(map);
    }

    /**
     * a.当前债券面额取得（根据还本计划计算）
     *
     * @param settleDate 计算日
     * @param finprodId 金融产品代码
     * @return a.当前债券面额取得（根据还本计划计算）
     * @see
     * @since 1.0
     */
    public BigDecimal getCurSecAmt(Date settleDate, String finprodId) {
        Map<String, Object> param = new HashMap<>();
        param.put("cashType1", BussinessDictCodeList.现金流一级类型_本金.getValue());
        param.put("settleDate", settleDate);
        param.put("finprodId", finprodId);
        return secCalRequestQuoteMapper.getCurSecAmt(param);
    }
}

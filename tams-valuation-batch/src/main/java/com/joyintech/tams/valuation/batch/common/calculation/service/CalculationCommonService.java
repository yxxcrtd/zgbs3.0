/*
 * 文件名：CalculationCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.calculation.service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.asset.SubsectionCalculator;
import com.joyintech.calculation.asset.input.SubsectionInputParameter;
import com.joyintech.calculation.asset.ouput.SubsectionOutputParameter;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForAbsBookCalculator;
import com.joyintech.calculation.shell.AccurIntForBookCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.calculation.dao.CalculationCommonMapper;
import com.joyintech.tams.valuation.batch.common.calculation.entity.*;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.IntStream;

/**
 * 计算类共通方法
 * 
 * @author wangzhiang
 * @version 1.0 2018年5月31日
 * @see CalculationCommonService
 * @since 1.0
 */
@Service
public class CalculationCommonService {

    @Autowired
    private CalculationCommonMapper calculationCommonMapper;

    /**
     * 账务类共通
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的查询共通方法
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    @Autowired
    private BokBooksetMapper bokBooksetMapper;

    public final static int SCALE_18 = 18;
    /**
     * 计算计息基础天数
     * 
     * @param input InterestBasisDaysParameter
     * @return
     * @see
     * @since 1.0
     */
    public int calcInterestBasisDays(InterestBasisDaysParameter input) {
        assert (StringUtil.isNotEmpty(input.getBasis()));
        if (StringUtil.equals(input.getBasis(), ValDictCodeList.计息基础_债券类资产_A_A.getValue())) {
            assert (input.getPayDateBegin() != null && input.getVdate() != null
                    && input.getMdate() != null && StringUtil.isNotEmpty(input.getCalMethod()));
            if (StringUtil.equals(input.getCalMethod(), DictCodeList.推算方式_首次支付日.getValue())) {
                assert (input.getFirstPayDate() != null);
            }
            if (StringUtil.equals(input.getCalMethod(), DictCodeList.推算方式_起息日向后.getValue())) {
                return IntStream.iterate(0, i -> i + 1).filter(i -> {
                    if (input.getPayDateBegin().compareTo(
                        DateUtil.addDate(input.getVdate(), i, 0, 0)) >= 0
                        && input.getPayDateBegin().compareTo(
                            DateUtil.addDate(input.getVdate(), i + 1, 0, 0)) < 0) {
                        return true;
                    }
                    return false;
                }).map(i -> {
                    return (int)DateUtil.getBetweenDayNumber(
                        DateUtil.addDate(input.getVdate(), i, 0, 0),
                        DateUtil.addDate(input.getVdate(), i + 1, 0, 0));
                }).findFirst().orElse(365);
            }
            else if (StringUtil.equals(input.getCalMethod(), DictCodeList.推算方式_到期日向前.getValue())) {
                return IntStream.iterate(0, i -> i + 1).filter(i -> {
                    if (input.getPayDateBegin().compareTo(
                        DateUtil.addDate(input.getMdate(), -i - 1, 0, 0)) >= 0
                        && input.getPayDateBegin().compareTo(
                            DateUtil.addDate(input.getMdate(), -i, 0, 0)) < 0) {
                        return true;
                    }
                    return false;
                }).map(i -> {
                    return (int)DateUtil.getBetweenDayNumber(
                        DateUtil.addDate(input.getMdate(), -i - 1, 0, 0),
                        DateUtil.addDate(input.getMdate(), -i, 0, 0));
                }).findFirst().orElse(365);
            }
            else if (StringUtil.equals(input.getCalMethod(), DictCodeList.推算方式_首次支付日.getValue())) {
                return IntStream.iterate(0, i -> i + 1).filter(i -> {
                    if (input.getPayDateBegin().compareTo(
                        DateUtil.addDate(input.getFirstPayDate(), i, 0, 0)) >= 0
                        && input.getPayDateBegin().compareTo(
                            DateUtil.addDate(input.getFirstPayDate(), i + 1, 0, 0)) < 0) {
                        return true;
                    }
                    return false;
                }).map(i -> {
                    return (int)DateUtil.getBetweenDayNumber(
                        DateUtil.addDate(input.getFirstPayDate(), i, 0, 0),
                        DateUtil.addDate(input.getFirstPayDate(), i + 1, 0, 0));
                }).findFirst().orElse(365);
            }
            else {
                return IntStream.iterate(0, i -> i + 1).filter(i -> {
                    if (input.getPayDateBegin().compareTo(
                        DateUtil.addDate(input.getFirstPayDate(), i, 0, 0)) >= 0
                        && input.getPayDateBegin().compareTo(
                            DateUtil.addDate(input.getFirstPayDate(), i + 1, 0, 0)) < 0) {
                        return true;
                    }
                    return false;
                }).map(i -> {
                    return (int)DateUtil.getBetweenDayNumber(
                        DateUtil.addDate(input.getFirstPayDate(), i, 0, 0),
                        DateUtil.addDate(input.getFirstPayDate(), i + 1, 0, 0));
                }).findFirst().orElse(365);
            }
        }
        else if (StringUtil.equals(input.getBasis(), ValDictCodeList.计息基础_债券类资产_A_365.getValue())) {
            return 365;
        }
        else if (StringUtil.equals(input.getBasis(), ValDictCodeList.计息基础_债券类资产_A_360.getValue())
                 || StringUtil.equals(input.getBasis(),
                     ValDictCodeList.计息基础_债券类资产_30_360.getValue())
                 /**ADD BY WUJIE ON 20200208 START**/
                 || StringUtil.equals(input.getBasis(), ValDictCodeList.计息基础_债券类资产_30U_360.getValue())
                 /**ADD BY WUJIE ON 20200208 END**/
        		) {
            return 360;
        }
        else {
            return 365;
        }
    }

    /**
     * 通用计算
     * 
     * @param startDate 开始日
     * @param endDate 结束日
     * @param prinList 本金列表
     * @param quoteList 报价列表
     * @return 分段明细列表 + 总利息金额
     */
    public CalCommonResult calCommon(Date startDate, Date endDate, List<PrincipalInfo> prinList,
                                     List<QuoteInfo> quoteList) {
        // 1.分段区间计算，调用共通方法【分段区间计算】
        List<InvRateQuo> invRateQuoList = new ArrayList<>(quoteList.size());
        for (QuoteInfo quote : quoteList) {
            InvRateQuo rate = new InvRateQuo();
            if (null == quote.getInterest()) {
                rate.setPaperRate(BigDecimal.ZERO);
            }
            else {
                rate.setPaperRate(quote.getInterest());
            }
            rate.setStartDate(quote.getIssueDate());
            rate.setAccountBasis(quote.getAccountBasis());
            invRateQuoList.add(rate);
        }
        List<InvRepaySchedule> invRepayScheduleList = new ArrayList<>(prinList.size());
        for (PrincipalInfo prin : prinList) {
            InvRepaySchedule invRepay = new InvRepaySchedule();
            invRepay.setActMdate(prin.getChangeDate());
            invRepay.setRepayAmt(prin.getPrinAmount());
            invRepayScheduleList.add(invRepay);
        }
        // 调用共通方法【分段区间计算】
        List<InvRpayscheDetail> tmpList = RepayScheduleSubCal.cal(startDate, endDate,
            invRateQuoList, invRepayScheduleList, BigDecimal.ZERO, "1", InvRpayscheDetail.class);

        // 分段付息区间列表
        List<SecIntRange> secIntRangeList = new ArrayList<>();
        if (null != tmpList) {
            for (InvRpayscheDetail detail : tmpList) {
                SecIntRange sec = new SecIntRange();
                sec.setStartDate(detail.getVdate());
                sec.setEndDate(detail.getMdate());
                sec.setIntAmt(detail.getAmt());
                sec.setIntRate(detail.getYield());
                sec.setPrinInt(detail.getCapi());
                sec.setAccountBasis(detail.getAccountBasis());
                secIntRangeList.add(sec);
            }
        }
        BigDecimal 总利息金额 = BigDecimal.ZERO;
        // 2.利息计算
        for (SecIntRange secIntRange : secIntRangeList) {
            BigDecimal intAmt = calSection(secIntRange);
            secIntRange.setIntAmt(intAmt);
            总利息金额 = ArithmeticUtil.doAdd(总利息金额, intAmt);
        }

        Collections.sort(secIntRangeList, new Comparator<SecIntRange>() {

            @Override
            public int compare(SecIntRange o1, SecIntRange o2) {
                return DateUtil.compareDate(o1.getStartDate(), o2.getStartDate());
            }

        });
        CalCommonResult result = new CalCommonResult();
        result.setSecIntRangeList(secIntRangeList);
        result.setTotalInterestAmt(总利息金额);
        return result;
    }

    /**
     * 通用分段计算 该算法为【分段区间利息计算】的简易算法，该算法剔除了一些特殊的计算类型，只支持A/360、A/365和A/365F
     * 
     * @param secIntRange 分段付息区间
     * @return 利息金额
     */
    public BigDecimal calSection(SecIntRange secIntRange) {
        SubsectionCalculator calculator = new SubsectionCalculator();
        SubsectionInputParameter input = new SubsectionInputParameter();
        // 计息基础
        input.setBasis(secIntRange.getAccountBasis());
        // 计算开始日
        input.setStartDate(secIntRange.getStartDate());
        // 计算日
        input.setEndDate(secIntRange.getEndDate());
        // 利息金额
        input.setAmount(secIntRange.getIntAmt());
        // 分段利息
        input.setRate(secIntRange.getIntRate());
        // 计息本金
        input.setCorpus(secIntRange.getPrinInt());
        // 计算结果
        SubsectionOutputParameter output = calculator.calculate(input);

        return output.getIntAmt();
    }

    /**
     * 计算交易类到期日-1利息
     * 
     * @param 持仓份额
     * @param 标的利息
     * @param 标的份额
     * @return 利息
     * @see
     * @since 1.0
     */
    public BigDecimal calcMdateInterest(BigDecimal 持仓份额, BigDecimal 标的利息, BigDecimal 标的份额) {
        return ArithmeticUtil.divide(ArithmeticUtil.multiply(持仓份额, 标的利息), 标的份额);
    }

    /**
     * 产品费用计提基数计算 <功能详细描述>
     * 
     * @param feebaseType 计费基数类型
     * @param valStatBokPosition 帐套资产负债信息
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param standardCcy 本位币
     * @param happenDate 发生日
     * @param calDate 计算日期
     * @param taskPri 任务优先级
     * @param bokDetailId 账务明细代码
     * @return 计费基数
     * @see
     * @since 1.0
     */
    public BigDecimal calProductFee(String feebaseType, BokStatBokPosition valStatBokPosition,
                                    String booksetId, Date bookDate, String standardCcy,
                                    Date happenDate, Date calDate, Long taskPri,
                                    List<String> bokDetailIdList) {
        // 计费基数
        BigDecimal feebase = null;
        if (DictCodeList.基数类型_实收资本.getValue().equals(feebaseType)) {
            if (valStatBokPosition != null) {
                feebase = valStatBokPosition.getCapital();
            }
            if (feebase == null) {
                feebase = BigDecimal.ZERO;
                if (StringUtil.isEmptyList(bokDetailIdList)) {
                    SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
                    // 发生日
                    searchDto.setHappenDate(calDate);
                    // 入账日
                    searchDto.setBookDate(bookDate);
                    // 账套代码
                    searchDto.setBooksetId(booksetId);

                    // 参数.计算日 = 参数.发生日时，传入参数.任务优先级
                    if (calDate.compareTo(happenDate) == 0) {
                        searchDto.setTaskPri(taskPri); // 任务优先级
                    }

                    // 基础科目性质
                    List<String> baseSubjectNatureList = new ArrayList<>();
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.实收资本.getValue());
                    searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
                    // 期待方向
                    String expectedBalFalg = DictCodeList.余额方向_贷方.getValue();

                    // 调用共通方法：科目余额查询-期待方向
                    List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDto, expectedBalFalg);

                    // 计费基数 = 上述返回科目余额
                    if (subjectBalanceList != null && subjectBalanceList.size() > 0
                        && subjectBalanceList.get(0).getBalance() != null) {
                        for (SubjectBalance balance : subjectBalanceList) {
                            feebase = ArithmeticUtil.doAdd(feebase, balance.getBalance());
                        }
                    }
                }
                else {
                    for (String bokDetailId : bokDetailIdList) {

                        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
                        // 发生日
                        searchDto.setHappenDate(calDate);
                        // 入账日
                        searchDto.setBookDate(bookDate);
                        // 账套代码
                        searchDto.setBooksetId(booksetId);
                        // 账务明细代码
                        searchDto.setBokDetailId(bokDetailId);

                        // 参数.计算日 = 参数.发生日时，传入参数.任务优先级
                        if (calDate.compareTo(happenDate) == 0) {
                            searchDto.setTaskPri(taskPri); // 任务优先级
                        }

                        // 基础科目性质
                        List<String> baseSubjectNatureList = new ArrayList<>();
                        baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.实收资本.getValue());
                        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
                        // 期待方向
                        String expectedBalFalg = DictCodeList.余额方向_贷方.getValue();

                        // 调用共通方法：科目余额查询-期待方向
                        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                            searchDto, expectedBalFalg);

                        // 计费基数 = 上述返回科目余额
                        if (subjectBalanceList != null && subjectBalanceList.size() > 0
                            && subjectBalanceList.get(0).getBalance() != null) {
                            for (SubjectBalance balance : subjectBalanceList) {
                                feebase = ArithmeticUtil.doAdd(feebase, balance.getBalance());
                            }
                        }
                    }
                }
            }
        }
        else if (DictCodeList.基数类型_资产净值.getValue().equals(feebaseType)) {
            if (valStatBokPosition != null) {
                feebase = valStatBokPosition.getNetAssetValue();
            }

            if (feebase == null) {
                feebase = BigDecimal.ZERO;
                if (StringUtil.isEmptyList(bokDetailIdList)) {
                    SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
                    // 账套代码
                    searchDto.setBooksetId(booksetId);
                    // 科目类型
                    List<String> subjectTypeList = new ArrayList<>();
                    subjectTypeList.add(DictCodeList.科目类型_所有者权益类.getValue());
                    subjectTypeList.add(DictCodeList.科目类型_损益类.getValue());
                    searchDto.setSubjectTypeList(subjectTypeList);
                    // 入账日
                    searchDto.setBookDate(bookDate);
                    // 发生日
                    searchDto.setHappenDate(calDate);
                    // 期待方向
                    String expectedBalFalg = DictCodeList.余额方向_贷方.getValue();

                    // 调用共通方法：科目余额汇总查询
                    Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(
                            searchDto, expectedBalFalg,null);

                    // 计费基数 = 调用方法：【详细设计-计算类共通方法设计】汇率折算（余额Map，参数.本位币，参数.发生日）
                    // 方法还没更新
                    feebase = ArithmeticUtil.doAdd(feebase,
                        this.calcExchangeAmt(map, standardCcy, happenDate));
                }
                else {
                    for (String bokDetailId : bokDetailIdList) {
                        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
                        // 账套代码
                        searchDto.setBooksetId(booksetId);
                        // 科目类型
                        List<String> subjectTypeList = new ArrayList<>();
                        subjectTypeList.add(DictCodeList.科目类型_所有者权益类.getValue());
                        subjectTypeList.add(DictCodeList.科目类型_损益类.getValue());
                        searchDto.setSubjectTypeList(subjectTypeList);
                        // 入账日
                        searchDto.setBookDate(bookDate);
                        // 发生日
                        searchDto.setHappenDate(calDate);
                        // 账务明细代码
                        searchDto.setBokDetailId(bokDetailId);
                        // 期待方向
                        String expectedBalFalg = DictCodeList.余额方向_贷方.getValue();

                        // 调用共通方法：科目余额汇总查询
                        Map<String, BigDecimal> map = accountingCommonService.getSubjectBalanceCollection(
                                searchDto, expectedBalFalg,null);

                        // 计费基数 = 调用方法：【详细设计-计算类共通方法设计】汇率折算（余额Map，参数.本位币，参数.发生日）
                        // 方法还没更新
                        feebase = ArithmeticUtil.doAdd(feebase,
                            this.calcExchangeAmt(map, standardCcy, happenDate));
                    }
                }

            }
        }

        return feebase;
    }

    /**
     * 计算基数日期
     * 
     * @param cdate 日期
     * @param calendarId 交易日历
     * @param dateType 基数日期类型
     * @param finprodId 金融产品代码
     * @return 净值日
     * @see
     * @since 1.0
     */
    public Date calcBasicDate(Date cdate, String calendarId, String dateType, String finprodId) {
        if (StringUtil.equals(dateType, DictCodeList.基数日期类型_上一开放日.getValue())) {
            // 调用共通 上一开放日（日期，交易日历，金融产品代码）
            return this.getLastOpenDate(cdate, finprodId);
        }
        else if (StringUtil.equals(dateType, DictCodeList.基数日期类型_前一工作日.getValue())) {
            // 查询上一工作日（参数.日期，参数.交易日历）
            return this.getCDate(cdate, calendarId);

        }
        else if (StringUtil.equals(dateType, DictCodeList.基数日期类型_前一自然日.getValue())) {
            return DateUtil.addDays(cdate, -1);
        }
        else if (StringUtil.equals(dateType, DictCodeList.基数日期类型_当日.getValue())) {
            return cdate;
        }
        return null;
    }

    /**
     * 查询汇率
     * 
     * @param 基础币种 基础币种
     * @param 报价币种 报价币种
     * @param 交易日 交易日
     * @return 汇率
     * @see
     * @since 1.0
     */
    public BigDecimal queryExchangeRate(String 基础币种, String 报价币种, Date 交易日) {
/*        if (基础币种.equals(报价币种)) {
            return BigDecimal.ONE;
        }
        List<ExchangeInfo> rateList = calculationCommonMapper.queryExchangeRate(基础币种, 报价币种, 交易日);
        if (rateList == null || rateList.isEmpty() || rateList.get(0).getExRate() == null) {
            return BigDecimal.ONE;
        }*/
//        return rateList.get(0).getExRate();
    	return new BigDecimal(1);
    }

    /**
     * 查询汇率
     * 
     * @param 报价币种 报价币种
     * @param 交易日 交易日
     * @return 汇率
     * @see
     * @since 1.0
     */
//    public List<ExchangeInfo> queryExchangeRate(String 报价币种, Date 交易日) {
//        return calculationCommonMapper.queryExchangeRate(null, 报价币种, 交易日);
//    }

    /**
     * 汇率折算
     * 
     * @param 余额集合 余额集合
     * @param 本位币 本位币
     * @param 交易日 交易日
     * @return 折算金额
     * @see
     * @since 1.0
     */
    public BigDecimal calcExchangeAmt(Map<String, BigDecimal> 余额集合, String 本位币, Date 交易日) {
//        Map<String, List<ExchangeInfo>> 汇率集合 = queryExchangeRate(本位币, 交易日).stream().collect(
//            Collectors.groupingBy(ExchangeInfo::getBaseCcy));
//        return 余额集合.entrySet().stream().map(item -> {
//            if (StringUtil.equals(item.getKey(), 本位币)) {
//                return item.getValue();
//            }
//            if (汇率集合.containsKey(item.getKey())) {
//                return ArithmeticUtil.multiply(item.getValue(),
//                    汇率集合.get(item.getKey()).get(0).getExRate()).setScale(2, RoundingMode.HALF_UP);
//            }
//            return item.getValue();
//        }).reduce(ArithmeticUtil::doAdd).orElse(BigDecimal.ZERO);
    	return 余额集合.get(本位币);
    }

    /**
     * 查询上一工作日
     * 
     * @param cdate 查询日期
     * @param calendarId 交易日历
     * @return 日期
     * @see
     * @since 1.0
     */
    public Date getCDate(Date cdate, String calendarId) {
        return calculationCommonMapper.getCDate(cdate, "0", calendarId);
    }

    /**
     * 是否节假日
     * 
     * @param cdate 查询日期
     * @param calendarId 交易日历
     * @return 是否节假日（0：工作日；其他：节假日）
     * @see
     * @since 1.0
     */
    public String getIsHoliday(Date cdate, String calendarId) {
        return calculationCommonMapper.getIsHoliday(cdate, calendarId);
    }

    /**
     * 上一开放日
     * 
     * @param cdate 日期
     * @param calendarId 交易日历
     * @param finprodId 金融产品代码
     * @return 上一开放日
     * @see
     * @since 1.0
     */
    public Date getLastOpenDate(Date cdate, String finprodId) {
//        if (true) {// 开放模式 == 高频
//            if (true) {// 节假日是否开放 = “是”
//                return DateUtil.addDay(cdate, -1);
//            }
//            else {
//                return this.getCDate(cdate, calendarId);
//            }
//        }
//        else {
//            return calculationCommonMapper.getLastOpenDate(finprodId, cdate);
//        }
        // modify by weipeng：估值现不存开放计划，通过业绩报酬计提计划取值--魏鹏 start
        Date lastOpenDate = calculationCommonMapper.getLastOpenDate(finprodId, cdate);
        // 若无对应上一开放确认日期，则默认取产品起息日
        if(lastOpenDate==null){
            lastOpenDate =calculationCommonMapper.getVdDateByFinprodId(finprodId);
        }
        return lastOpenDate;
        // modify by weipeng：估值现不存开放计划，通过业绩报酬计提计划取值--魏鹏 end
    }

    /**
     * 交易各类费用处理
     * 
     * @param listValTradeFeeDetail 交易费用表 finprodId 金融产品代码
     * @param finprodId 金融产品代码
     * @param groupId 投资组合代码
     * @return DealTypesFeeDto
     * @see
     * @since 1.0
     */
    public DealTypesFeeDto quaryDealTypesFeeamt(List<TrdTradeFeeDetail> listValTradeFeeDetail,
                                                String booksetId, String groupId) {
        DealTypesFeeDto dealTypesFeeDto = new DealTypesFeeDto();
        // 交易费用
        BigDecimal feeAmt = BigDecimal.ZERO;
        // 印花税
        BigDecimal stampDuty = BigDecimal.ZERO;
        // 经手费
        BigDecimal brokerage = BigDecimal.ZERO;
        // 证管费
        BigDecimal certificateFee = BigDecimal.ZERO;
        // 结算费
        BigDecimal clearingFee = BigDecimal.ZERO;
        // 佣金
        BigDecimal commission = BigDecimal.ZERO;
        // 交易手续费
        BigDecimal transactionFee = BigDecimal.ZERO;
        // 结算手续费
        BigDecimal settlementFee = BigDecimal.ZERO;
        // 通道费
        BigDecimal chlFee = BigDecimal.ZERO;
        // 通道管理费
        BigDecimal chlGlFee = BigDecimal.ZERO;
        // 通道销售费
        BigDecimal chlXsFee = BigDecimal.ZERO;
        // 通道托管费
        BigDecimal chlTgFee = BigDecimal.ZERO;
        // 不随交易费用
        BigDecimal noFeeAmt = BigDecimal.ZERO;
        //账户维护费
        BigDecimal accountManageFee = BigDecimal.ZERO;
        // 账户维护费账务明细代码
        BokDetailId accountManageFeeCode = null;
        // 印花税账务明细代码
        BokDetailId stampDutyCode = null;
        // 经手费账务明细代码
        BokDetailId brokerageCode = null;
        // 证管费账务明细代码
        BokDetailId certificateFeeCode = null;
        // 结算费账务明细代码
        BokDetailId clearingFeeCode = null;
        // 佣金账务明细代码
        BokDetailId commissionCode = null;
        // 交易手续费账务明细代码
        BokDetailId transactionFeeCode = null;
        // 结算手续费账务明细代码
        BokDetailId settlementFeeCode = null;
        // 通道费账务明细代码
        BokDetailId chlFeeCode = null;
        // 通道管理费账务明细代码
        BokDetailId chlGlFeeCode = null;
        // 通道销售费账务明细代码
        BokDetailId chlXsFeeCode = null;
        // 通道托管费账务明细代码
        BokDetailId chlTgFeeCode = null;

        // 随交易支付_印花税
        BigDecimal stampDutyForDealPay = BigDecimal.ZERO;
        // 随交易支付_经手费
        BigDecimal brokerageForDealPay = BigDecimal.ZERO;
        // 随交易支付_证管费
        BigDecimal certificateFeeForDealPay = BigDecimal.ZERO;
        // 随交易支付_结算费
        BigDecimal clearingFeeForDealPay = BigDecimal.ZERO;
        // 随交易支付_佣金
        BigDecimal commissionForDealPay = BigDecimal.ZERO;
        // 随交易支付_交易手续费
        BigDecimal transactionFeeForDealPay = BigDecimal.ZERO;
        // 随交易支付_结算手续费
        BigDecimal settlementFeeForDealPay = BigDecimal.ZERO;
        // 随交易支付_印花税账务明细代码
        BokDetailId stampDutyCodeForDealPay = null;
        // 随交易支付_经手费账务明细代码
        BokDetailId brokerageCodeForDealPay = null;
        // 随交易支付_证管费账务明细代码
        BokDetailId certificateFeeCodeForDealPay = null;
        // 随交易支付_结算费账务明细代码
        BokDetailId clearingFeeCodeForDealPay = null;
        // 随交易支付_佣金账务明细代码
        BokDetailId commissionCodeForDealPay = null;
        // 随交易支付_交易手续费账务明细代码
        BokDetailId transactionFeeCodeForDealPay = null;
        // 随交易支付_结算手续费账务明细代码
        BokDetailId settlementFeeCodeForDealPay = null;
        /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  start**/
        // 其他费用
        BigDecimal otherFee = BigDecimal.ZERO;
        // 申购费
        BigDecimal subscriptionFee = BigDecimal.ZERO;
        // 赎回费
        BigDecimal redemptionFee = BigDecimal.ZERO;
        // 认购费
        BigDecimal offerFee = BigDecimal.ZERO;
        // 其他费用账务明细代码
        BokDetailId otherFeeCode = null;
        // 申购费账务明细代码 
        BokDetailId subscriptionFeeCode = null;
        // 赎回费账务明细代码
        BokDetailId redemptionFeeCode = null;
        // 认购费账务明细代码
        BokDetailId offerFeeCode = null;
        
        // 其他费用_随交易支付
        BigDecimal otherFeeForDealPay = BigDecimal.ZERO;
        // 申购费_随交易支付
        BigDecimal subscriptionFeeForDealPay = BigDecimal.ZERO;
        // 赎回费_随交易支付
        BigDecimal redemptionFeeForDealPay = BigDecimal.ZERO;
        // 认购费_随交易支付
        BigDecimal offerFeeForDealPay = BigDecimal.ZERO;
        // 其他费用账务明细代码_随交易支付
        BokDetailId otherFeeCodeForDealPay = null;
        // 申购费账务明细代码 _随交易支付
        BokDetailId subscriptionFeeCodeForDealPay = null;
        // 赎回费账务明细代码_随交易支付
        BokDetailId redemptionFeeCodeForDealPay = null;
        // 认购费账务明细代码_随交易支付
        BokDetailId offerFeeCodeForDealPay = null;
        /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  end**/

        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
        // 定义查询类型 实体类
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto);

        // 获取标的信息
//        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(finprodId,
//            targetSearchDtoList);
//        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);
        // 金融产品类型
//        String finprodType = null;
//        if (targetFinProduct != null) {
//            finprodType = targetFinProduct.getFinprodType();
//        }
        if (listValTradeFeeDetail != null && listValTradeFeeDetail.size() > 0) {
            BokBookset bokbookset = bokBooksetMapper.selectByPrimaryKey(booksetId);
            BokDetailId bokDetailId = new BokDetailId();
            if(StringUtil.isNotEmpty(bokbookset)){
            	// 取账务明细代码，下面直接用该账务明细代码即可
            	bokDetailId = bookCommonLogic.selectBokdetailId(booksetId, 
            			bokbookset.getAcctSubjectId(), bokbookset.getAcctSubjectType());
            }
            for (TrdTradeFeeDetail valTradeFeeDetail : listValTradeFeeDetail) {
                // 是否交易费用 = "是"
                if (DictCodeList.是否_是.getValue().equals(valTradeFeeDetail.getIsPayWithTrade())) {
                    feeAmt = ArithmeticUtil.doAdd(feeAmt, valTradeFeeDetail.getFeeAmt());
                    if (DictCodeListExt1.一次性费用类型_通道费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        chlFee = ArithmeticUtil.doAdd(chlFee, valTradeFeeDetail.getFeeAmt());
                        // 【【出参】交易各类费用.通道费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
                        chlFeeCode = bookCommonLogic.getValBokDetailId(
                            valTradeFeeDetail.getFeeId(), null, valTradeFeeDetail.getFeeType(),valTradeFeeDetail.getCcy());
                    }
                    else if (DictCodeListExt1.一次性费用类型_通道管理费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        chlGlFee = ArithmeticUtil.doAdd(chlGlFee, valTradeFeeDetail.getFeeAmt());
                        // 【【出参】交易各类费用.通道管理费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
                        chlGlFeeCode = bookCommonLogic.getValBokDetailId(
                            valTradeFeeDetail.getFeeId(), null, valTradeFeeDetail.getFeeType(),valTradeFeeDetail.getCcy());
                    }
                    else if (DictCodeListExt1.一次性费用类型_通道销售费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        chlXsFee = ArithmeticUtil.doAdd(chlXsFee, valTradeFeeDetail.getFeeAmt());
                        // 【【出参】交易各类费用.通道销售费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
                        chlXsFeeCode = bookCommonLogic.getValBokDetailId(
                            valTradeFeeDetail.getFeeId(), null, valTradeFeeDetail.getFeeType(),valTradeFeeDetail.getCcy());
                    }
                    else if (DictCodeListExt1.一次性费用类型_通道托管费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        chlTgFee = ArithmeticUtil.doAdd(chlTgFee, valTradeFeeDetail.getFeeAmt());
                        // 【【出参】交易各类费用.通道托管费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
                        chlTgFeeCode = bookCommonLogic.getValBokDetailId(
                            valTradeFeeDetail.getFeeId(), null, valTradeFeeDetail.getFeeType(),valTradeFeeDetail.getCcy());
                    }

                    // 随交易费用
                    if (DictCodeListExt1.一次性费用类型_印花税.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_印花税 += 费用金额
                        stampDutyForDealPay = ArithmeticUtil.doAdd(stampDutyForDealPay,
                            valTradeFeeDetail.getFeeAmt());
                        stampDutyCodeForDealPay = bokDetailId;
                        // 【出参】交易各类费用.随交易支付_印花税账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            stampDutyCodeForDealPay = bookCommonLogic.getValBokDetailId(finprodId,
//                                null, finprodType);
//                        }
//                        else {
//                            stampDutyCodeForDealPay = bookCommonLogic.getValBokDetailId(groupId,
//                                null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }

                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        stampDutyCode = stampDutyCodeForDealPay;

                    }
                    else if (DictCodeListExt1.一次性费用类型_经手费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_经手费 += 费用金额
                        brokerageForDealPay = ArithmeticUtil.doAdd(brokerageForDealPay,
                            valTradeFeeDetail.getFeeAmt());
                        brokerageCodeForDealPay = bokDetailId;
                        // 【出参】交易各类费用.随交易支付_经手费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            brokerageCodeForDealPay = bookCommonLogic.getValBokDetailId(finprodId,
//                                null, finprodType);
//                        }
//                        else {
//                            brokerageCodeForDealPay = bookCommonLogic.getValBokDetailId(groupId,
//                                null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        brokerageCode = brokerageCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_证管费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_证管费 += 费用金额
                        certificateFeeForDealPay = ArithmeticUtil.doAdd(certificateFeeForDealPay,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_证管费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            certificateFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                finprodId, null, finprodType);
//                        }
//                        else {
//                            certificateFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                groupId, null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        certificateFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        certificateFeeCode = certificateFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_结算费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_结算费 += 费用金额
                        clearingFeeForDealPay = ArithmeticUtil.doAdd(clearingFeeForDealPay,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_结算费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            clearingFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                finprodId, null, finprodType);
//                        }
//                        else {
//                            clearingFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(groupId,
//                                null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        clearingFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        clearingFeeCode = clearingFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_佣金.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_佣金 += 费用金额
                        commissionForDealPay = ArithmeticUtil.doAdd(commissionForDealPay,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_佣金账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            commissionCodeForDealPay = bookCommonLogic.getValBokDetailId(finprodId,
//                                null, finprodType);
//                        }
//                        else {
//                            commissionCodeForDealPay = bookCommonLogic.getValBokDetailId(groupId,
//                                null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        commissionCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        commissionCode = commissionCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_交易手续费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_交易手续费 += 费用金额
                        transactionFeeForDealPay = ArithmeticUtil.doAdd(transactionFeeForDealPay,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_交易手续费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            transactionFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                finprodId, null, finprodType);
//                        }
//                        else {
//                            transactionFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                groupId, null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        transactionFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        transactionFeeCode = transactionFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_结算手续费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_结算手续费 += 费用金额
                        settlementFeeForDealPay = ArithmeticUtil.doAdd(settlementFeeForDealPay,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_结算手续费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            settlementFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                finprodId, null, finprodType);
//                        }
//                        else {
//                            settlementFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                groupId, null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        settlementFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        settlementFeeCode = settlementFeeCodeForDealPay;
                    }
                    /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  start**/
                    else if (DictCodeListExt1.一次性费用类型_其他费用.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_其他费用 += 费用金额
                        otherFeeForDealPay = ArithmeticUtil.doAdd(otherFeeForDealPay, valTradeFeeDetail.getFeeAmt());
                        otherFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        otherFeeCode = otherFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_申购费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_申购费 += 费用金额
                        subscriptionFeeForDealPay = ArithmeticUtil.doAdd(subscriptionFeeForDealPay, valTradeFeeDetail.getFeeAmt());
                        subscriptionFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        subscriptionFeeCode = subscriptionFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_赎回费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_赎回费 += 费用金额
                        redemptionFeeForDealPay = ArithmeticUtil.doAdd(redemptionFeeForDealPay, valTradeFeeDetail.getFeeAmt());
                        redemptionFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        redemptionFeeCode = redemptionFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_认购费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_认购费 += 费用金额
                        offerFeeForDealPay = ArithmeticUtil.doAdd(offerFeeForDealPay, valTradeFeeDetail.getFeeAmt());
                        offerFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        offerFeeCode = offerFeeCodeForDealPay;
                    }
                    /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  end**/

                }
                else {
                    feeAmt = ArithmeticUtil.doAdd(feeAmt, valTradeFeeDetail.getFeeAmt());
                    // 不随交易费用
                    noFeeAmt = ArithmeticUtil.doAdd(noFeeAmt, valTradeFeeDetail.getFeeAmt());
                    if (DictCodeListExt1.一次性费用类型_印花税.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        stampDuty = ArithmeticUtil.doAdd(stampDuty, valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.印花税账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.印花税账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            stampDutyCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            stampDutyCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        stampDutyCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_经手费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        brokerage = ArithmeticUtil.doAdd(brokerage, valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.经手费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.经手费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            brokerageCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            brokerageCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        brokerageCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_证管费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        certificateFee = ArithmeticUtil.doAdd(certificateFee,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.证管费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.证管费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            certificateFeeCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            certificateFeeCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        certificateFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_结算费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        clearingFee = ArithmeticUtil.doAdd(clearingFee,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.结算费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.结算费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            clearingFeeCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            clearingFeeCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        clearingFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_佣金.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        commission = ArithmeticUtil.doAdd(commission,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.佣金账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.佣金账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            commissionCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            commissionCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        commissionCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_交易手续费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        transactionFee = ArithmeticUtil.doAdd(transactionFee,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.交易手续费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.交易手续费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            transactionFeeCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            transactionFeeCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        transactionFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_结算手续费.getValue().equals(
                        valTradeFeeDetail.getFeeType())) {
                        settlementFee = ArithmeticUtil.doAdd(settlementFee,
                            valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.结算手续费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.结算手续费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            settlementFeeCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            settlementFeeCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        settlementFeeCode = bokDetailId;
                    }
                    /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  start**/
                    else if (DictCodeListExt1.一次性费用类型_其他费用.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.其他费用_不随交易支付 += 费用金额
                        otherFee = ArithmeticUtil.doAdd(otherFee, valTradeFeeDetail.getFeeAmt());
                        otherFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_申购费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.申购费_不随交易支付 += 费用金额
                        subscriptionFee = ArithmeticUtil.doAdd(subscriptionFee, valTradeFeeDetail.getFeeAmt());
                        subscriptionFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_赎回费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.赎回费_不随交易支付 += 费用金额
                        redemptionFee = ArithmeticUtil.doAdd(redemptionFee, valTradeFeeDetail.getFeeAmt());
                        redemptionFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_认购费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.认购费_不随交易支付 += 费用金额
                        offerFee = ArithmeticUtil.doAdd(offerFee, valTradeFeeDetail.getFeeAmt());
                        offerFeeCode = bokDetailId;
                    }
                    /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  end**/
                    /** add by yanya on 20200228 增加一次性通道费处理  start**/
                    else if (DictCodeListExt1.一次性费用类型_通道费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        //【出参】交易各类费用.通道费_不随交易支付 += 费用金额
                        chlFee = ArithmeticUtil.doAdd(chlFee, valTradeFeeDetail.getFeeAmt());
                        chlFeeCode = bokDetailId;//根据代码来改动，跟设计有点差别
                    }
                    /** add by yanya on 20200228 增加一次性通道费处理  end**/
                    /**add jinmaodong 20210317 增加账户维护费预提功能时增加 start**/
                    else if(DictCodeListExt1.一次性费用类型_账户维护费.getValue().equals(valTradeFeeDetail.getFeeType())){
                        accountManageFee = ArithmeticUtil.doAdd(accountManageFee, valTradeFeeDetail.getFeeAmt());
                        accountManageFeeCode = bokDetailId;
                    }
                    /**add jinmaodong 20210317 增加账户维护费预提功能时增加 end**/
                }
            }

        }
        /**add jinmaodong 20210317 增加账户维护费预提功能时增加 start**/
        dealTypesFeeDto.setAccountManageFee(ArithmeticUtil.round(accountManageFee, NumberEnum.MONEY));
        dealTypesFeeDto.setAccountManageFeeCode(accountManageFeeCode);
        /**add jinmaodong 20210317 增加账户维护费预提功能时增加 end**/
        /** modify by dongjun on 20181119 start**/
        dealTypesFeeDto.setFeeAmt(ArithmeticUtil.round(feeAmt, NumberEnum.MONEY));
        dealTypesFeeDto.setNoFeeAmt(ArithmeticUtil.round(noFeeAmt, NumberEnum.MONEY));
        dealTypesFeeDto.setStampDuty(ArithmeticUtil.round(stampDuty, NumberEnum.MONEY));
        dealTypesFeeDto.setBrokerage(ArithmeticUtil.round(brokerage, NumberEnum.MONEY));
        dealTypesFeeDto.setCertificateFee(
            ArithmeticUtil.round(certificateFee, NumberEnum.MONEY));
        dealTypesFeeDto.setClearingFee(ArithmeticUtil.round(clearingFee, NumberEnum.MONEY));
        dealTypesFeeDto.setCommission(ArithmeticUtil.round(commission, NumberEnum.MONEY));
        dealTypesFeeDto.setTransactionFee(
            ArithmeticUtil.round(transactionFee, NumberEnum.MONEY));
        dealTypesFeeDto.setSettlementFee(
            ArithmeticUtil.round(settlementFee, NumberEnum.MONEY));
        dealTypesFeeDto.setStampDutyCode(stampDutyCode);
        dealTypesFeeDto.setBrokerageCode(brokerageCode);
        dealTypesFeeDto.setCertificateFeeCode(certificateFeeCode);
        dealTypesFeeDto.setClearingFeeCode(clearingFeeCode);
        dealTypesFeeDto.setCommissionCode(commissionCode);
        dealTypesFeeDto.setTransactionFeeCode(transactionFeeCode);
        dealTypesFeeDto.setSettlementFeeCode(settlementFeeCode);
        dealTypesFeeDto.setChlFee(chlFee);
        dealTypesFeeDto.setChlFeeCode(chlFeeCode);
        dealTypesFeeDto.setChlGlFee(chlGlFee);
        dealTypesFeeDto.setChlGlFeeCode(chlGlFeeCode);
        dealTypesFeeDto.setChlXsFee(chlXsFee);
        dealTypesFeeDto.setChlXsFeeCode(chlXsFeeCode);
        dealTypesFeeDto.setChlTgFee(chlTgFee);
        dealTypesFeeDto.setChlTgFeeCode(chlTgFeeCode);

        dealTypesFeeDto.setStampDutyForDealPay(
            ArithmeticUtil.round(stampDutyForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setBrokerageForDealPay(
            ArithmeticUtil.round(brokerageForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setCertificateFeeForDealPay(
            ArithmeticUtil.round(certificateFeeForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setClearingFeeForDealPay(
            ArithmeticUtil.round(clearingFeeForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setCommissionForDealPay(
            ArithmeticUtil.round(commissionForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setTransactionFeeForDealPay(
            ArithmeticUtil.round(transactionFeeForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setSettlementFeeForDealPay(
            ArithmeticUtil.round(settlementFeeForDealPay, NumberEnum.MONEY));

        dealTypesFeeDto.setStampDutyCodeForDealPay(stampDutyCodeForDealPay);
        dealTypesFeeDto.setBrokerageCodeForDealPay(brokerageCodeForDealPay);
        dealTypesFeeDto.setCertificateFeeCodeForDealPay(certificateFeeCodeForDealPay);
        dealTypesFeeDto.setClearingFeeCodeForDealPay(clearingFeeCodeForDealPay);
        dealTypesFeeDto.setCommissionCodeForDealPay(commissionCodeForDealPay);
        dealTypesFeeDto.setTransactionFeeCodeForDealPay(transactionFeeCodeForDealPay);
        dealTypesFeeDto.setSettlementFeeCodeForDealPay(settlementFeeCodeForDealPay);
        /** modify by dongjun on 20181119 end**/
        /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  start**/
        dealTypesFeeDto.setOtherFee(ArithmeticUtil.round(otherFee, NumberEnum.MONEY));//其他费用
        dealTypesFeeDto.setSubscriptionFee(ArithmeticUtil.round(subscriptionFee, NumberEnum.MONEY));//申购费
        dealTypesFeeDto.setRedemptionFee(ArithmeticUtil.round(redemptionFee, NumberEnum.MONEY));//赎回费
        dealTypesFeeDto.setOfferFee(ArithmeticUtil.round(offerFee, NumberEnum.MONEY));//认购费
        dealTypesFeeDto.setOtherFeeCode(otherFeeCode);//其他费用账务明细代码
        dealTypesFeeDto.setSubscriptionFeeCode(subscriptionFeeCode);//申购费账务明细代码
        dealTypesFeeDto.setRedemptionFeeCode(redemptionFeeCode);//赎回费账务明细代码
        dealTypesFeeDto.setOfferFeeCode(offerFeeCode);//认购费账务明细代码
        dealTypesFeeDto.setOtherFeeForDealPay(ArithmeticUtil.round(otherFeeForDealPay, NumberEnum.MONEY));//其他费用_随交易支付
        dealTypesFeeDto.setSubscriptionFeeForDealPay(ArithmeticUtil.round(subscriptionFeeForDealPay, NumberEnum.MONEY));//申购费_随交易支付
        dealTypesFeeDto.setRedemptionFeeForDealPay(ArithmeticUtil.round(redemptionFeeForDealPay, NumberEnum.MONEY));//赎回费_随交易支付
        dealTypesFeeDto.setOfferFeeForDealPay(ArithmeticUtil.round(offerFeeForDealPay, NumberEnum.MONEY));//认购费_随交易支付
        dealTypesFeeDto.setOtherFeeCodeForDealPay(otherFeeCodeForDealPay);//随交易支付_其他费用账务明细代码
        dealTypesFeeDto.setSubscriptionFeeCodeForDealPay(subscriptionFeeCodeForDealPay);//随交易支付_申购费账务明细代码
        dealTypesFeeDto.setRedemptionFeeCodeForDealPay(redemptionFeeCodeForDealPay);//随交易支付_赎回费账务明细代码
        dealTypesFeeDto.setOfferFeeCodeForDealPay(offerFeeCodeForDealPay);//随交易支付_认购费账务明细代码
        /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  end**/
        return dealTypesFeeDto;
    }

    /**
     * 判断日期是否结转
     *
     * @param date 日期
     * @param carryoverCycle 结转频率
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean checkDateNeedCarryover(Date date, String carryoverCycle) {
        // 日结直接返回true
        if (DictCodeListExt1.损益结转频率_日.getValue().equals(carryoverCycle)) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        // 判断是否月末
        if (day != cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            return false;
        }
        boolean flag = false;
        if (DictCodeListExt1.损益结转频率_月.getValue().equals(carryoverCycle)) {
            flag = true;
        }
        else if (DictCodeListExt1.损益结转频率_季度.getValue().equals(carryoverCycle)) {
            if (month == Calendar.MARCH || month == Calendar.JUNE || month == Calendar.SEPTEMBER
                || month == Calendar.DECEMBER) {
                flag = true;
            }
        }
        else if (DictCodeListExt1.损益结转频率_半年.getValue().equals(carryoverCycle)) {
            if (month == Calendar.JUNE || month == Calendar.DECEMBER) {
                flag = true;
            }
        }
        else if (DictCodeListExt1.损益结转频率_年.getValue().equals(carryoverCycle)) {
            if (month == Calendar.DECEMBER) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 质押式回购计提
     * 
     * @param tradeInfoDto 标的信息
     * @param balance 持仓份额
     * @param Amt 成本
     * @param date 计算日
     * @param calType 计提方式
     * @param intAmt 计算前已计提利息
     * @return 应计利息
     */
    public BigDecimal[] calRepo(TradeInfoDto tradeInfoDto, BigDecimal balance, BigDecimal Amt,
                                Date date, String calType, BigDecimal intAmt) {
        // 变量.累计应计利息
        BigDecimal profit = BigDecimal.ZERO;
        // 变量.发生应计利息
        BigDecimal happenIntAmt = BigDecimal.ZERO;

        if (null != tradeInfoDto) {
            // 变量.占比到期金额 = 参数.持仓份额 * 参数.标的信息.到期金额/ 参数.标的信息.名义本金
            BigDecimal propBalance = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(balance, tradeInfoDto.getMTradeAmt()),
                tradeInfoDto.getPrin());

            // if（变量.计提方式 = 【数据字典】.计提方式."区间倒减"）
            if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {
                // 出参.应计利息 = (变量.占比到期金额 - 参数.成本) * （参数.计算日 - 参数.标的信息.起息日）/ （参数.标的信息.到期日 -
                // 参数.标的信息.起息日）
                profit = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(ArithmeticUtil.subtract(propBalance, Amt),
                        new BigDecimal(
                            DateUtil.getBetweenDayNumber(tradeInfoDto.getVdate(), date))),
                    new BigDecimal(DateUtil.getBetweenDayNumber(tradeInfoDto.getVdate(),
                        tradeInfoDto.getMdate())));

                // 出参.发生应计利息 = 出参.累计应计利息 - 参数.应计利息
                happenIntAmt = ArithmeticUtil.doSub(profit, intAmt);
            }
            else {
                // if (计算日 = 参数.标的信息.到期日)
                if (DateUtil.getBetweenDayNumber(date, tradeInfoDto.getMdate()) == 0) {
                    // 出参.发生应计利息 = (变量.占比到期金额 - 参数.成本) - 参数.应计利息
                    happenIntAmt = ArithmeticUtil.doSub(ArithmeticUtil.doSub(propBalance, Amt),
                        intAmt);
                }
                else {
                    // 出参.发生应计利息 = (变量.占比到期金额 - 参数.成本) / （参数.标的信息.到期日 - 参数.标的信息.起息日）
                    happenIntAmt = ArithmeticUtil.divide(ArithmeticUtil.doSub(propBalance, Amt),
                        new BigDecimal(DateUtil.getBetweenDayNumber(tradeInfoDto.getVdate(),
                            tradeInfoDto.getMdate())));
                }
                // 出参.累计应计利息 = 出参.发生应计利息 + 参数.应计利息
                profit = ArithmeticUtil.doAdd(happenIntAmt, intAmt);
            }

        }
        BigDecimal[] arr = {profit, happenIntAmt};
        return arr;
    }

    /**
     * 计算类共通方法 --- 周期年化收益率
     * 
     * @param bokStatBokPositionList 帐套资产负债信息列表
     * @param cycBeginDate 周期开始日
     * @param cycEndDate 周期结束日
     * @param bonusReinvestList 红利再投信息列表
     * @param BasisDays 计算基础天数
     * @param feeBefOrAft 费前费后区分 [参考枚举类：ValDictCodeList.费前费后区分_XX.getValue()]
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getCysYearRate(List<BokStatBokPosition> bokStatBokPositionList,
                                     Date cycBeginDate, Date cycEndDate,
                                     List<BookLatestInfoDto> bonusReinvestList, int BasisDays,
                                     String feeBefOrAft) {
        // 定义 变量.周期年化收益率
        BigDecimal cycYearRate = BigDecimal.ZERO;
        // 定义 变量.周期万份收益
        BigDecimal cycTenThousandProfit = BigDecimal.ZERO;
        // 定义 变量.红利再投周期万份收益列表
        List<BigDecimal> bonusRstCycTenThdProfitList = new ArrayList<BigDecimal>();

        // 红利再投信息列表为空情况
        if (bonusReinvestList == null || bonusReinvestList.size() == 0) {

            // 循环账套资产负债信息
            for (BokStatBokPosition vo : bokStatBokPositionList) {
                // 费前情况
                if (ValDictCodeList.费前费后区分_费前.getValue().equals(feeBefOrAft)) {
                    // 变量.周期万份收益 = 变量.周期万份收益 + 资产负债信息.万份收益_费前
                    cycTenThousandProfit = ArithmeticUtil.doAdd(cycTenThousandProfit,
                        vo.getpYieldBef());

                } // 其他情况
                else {
                    // 变量.周期万份收益 = 变量.周期万份收益 + 资产负债信息.万份收益_费后
                    cycTenThousandProfit = ArithmeticUtil.doAdd(cycTenThousandProfit,
                        vo.getpYield());
                }
            }

            // 周期开始日和结束日不为空情况
            if (cycBeginDate != null && cycEndDate != null) {
                // 周期开始日和结束日间隔天数
                long betweenDay = DateUtil.getBetweenDayNumber(cycBeginDate, cycEndDate);
                // 间隔天数+1，且转为BigDecimal
                BigDecimal dayNum = ArithmeticUtil.doAdd(new BigDecimal(betweenDay),
                    BigDecimal.ONE);

                // 变量.周期年化收益率 = 变量.周期万份收益 /(参数.周期结束日 - 参数.周期开始日+1)* 参数.计算基础 /10000
                cycYearRate = ArithmeticUtil.multiply(
                    ArithmeticUtil.divide(cycTenThousandProfit, dayNum), // [变量.周期万份收益 /(参数.周期结束日 -
                                                                         // 参数.周期开始日+1)]
                    ArithmeticUtil.divide(new BigDecimal(BasisDays), new BigDecimal(10000)) // [参数.计算基础
                                                                                            // /10000]
                );
            }

        }
        else {

            // 循环红利再投信息
            for (BookLatestInfoDto redVo : bonusReinvestList) {
                // 定义当前红利再投层的红利再投周期万份收益列表值
                BigDecimal bonusRstCycTenThdProfitNow = BigDecimal.ZERO;

                // 循环账套资产负债信息
                for (BokStatBokPosition bokVo : bokStatBokPositionList) {
                    // 当资产负债信息的发生日 = 红利再投的发生日 的情况
                    if (bokVo.getHappenDate().compareTo(redVo.getHappenDate()) <= 0) {
                        // 费前情况
                        if (ValDictCodeList.费前费后区分_费前.getValue().equals(feeBefOrAft)) {
                            // 变量.红利再投周期万份收益列表[i] = 变量.红利再投周期万份收益列表[i]+资产负债信息[j].万份收益_费前/10000)
                            // 即（累加红利再投周期万份收益列表当前塞入值:红利再投周期万份收益 = 红利再投周期万份收益 +
                            // 资产负债信息.万份收益_费前/10000）
                            bonusRstCycTenThdProfitNow = ArithmeticUtil.doAdd(SCALE_18, RoundingMode.HALF_UP,
                                    bonusRstCycTenThdProfitNow, ArithmeticUtil.divide(
                                            bokVo.getpYieldBef(), new BigDecimal(10000)));
                        } // 其他情况
                        else {
                            // 变量.红利再投周期万份收益列表[i] = 变量.红利再投周期万份收益列表[i]+资产负债信息[j].万份收益_费后/10000)
                            // 即（累加红利再投周期万份收益列表当前塞入值:红利再投周期万份收益 = 红利再投周期万份收益 +
                            // 资产负债信息.万份收益_费后/10000）
                            bonusRstCycTenThdProfitNow = ArithmeticUtil.doAdd(SCALE_18, RoundingMode.HALF_UP,
                                    bonusRstCycTenThdProfitNow,
                                    ArithmeticUtil.divide(bokVo.getpYield(), new BigDecimal(10000)));
                        }
                    }
                }
                // 变量.红利再投周期万份收益列表 塞入该值
                bonusRstCycTenThdProfitList.add(bonusRstCycTenThdProfitNow);
            }
            // 定义当前红利再投层的红利再投周期万份收益列表值
            BigDecimal bonusRstCycTenThdProfitNow = BigDecimal.ZERO;
            // 循环账套资产负债信息
            for (BokStatBokPosition bokVo : bokStatBokPositionList) {
                // 费前情况
                if (ValDictCodeList.费前费后区分_费前.getValue().equals(feeBefOrAft)) {
                    // 变量.红利再投周期万份收益列表[i] = 变量.红利再投周期万份收益列表[i]+资产负债信息[j].万份收益_费前/10000)
                    // 即（累加红利再投周期万份收益列表当前塞入值:红利再投周期万份收益 = 红利再投周期万份收益 + 资产负债信息.万份收益_费前/10000）
                    bonusRstCycTenThdProfitNow = ArithmeticUtil.doAdd(SCALE_18, RoundingMode.HALF_UP, bonusRstCycTenThdProfitNow,
                            ArithmeticUtil.divide(bokVo.getpYieldBef(), new BigDecimal(10000)));
                } // 其他情况
                else {
                    // 变量.红利再投周期万份收益列表[i] = 变量.红利再投周期万份收益列表[i]+资产负债信息[j].万份收益_费后/10000)
                    // 即（累加红利再投周期万份收益列表当前塞入值:红利再投周期万份收益 = 红利再投周期万份收益 + 资产负债信息.万份收益_费后/10000）
                    bonusRstCycTenThdProfitNow = ArithmeticUtil.doAdd(SCALE_18, RoundingMode.HALF_UP, bonusRstCycTenThdProfitNow,
                            ArithmeticUtil.divide(bokVo.getpYield(), new BigDecimal(10000)));
                }
            }
            // 变量.红利再投周期万份收益列表 塞入该值
            bonusRstCycTenThdProfitList.add(bonusRstCycTenThdProfitNow);
            cycTenThousandProfit = BigDecimal.ONE;
            // 遍历[变量.红利再投周期万份收益列表]，遍历0 - (size-2)
            for (int index = 0; index <= bonusRstCycTenThdProfitList.size() - 1; index++ ) {

                // 定义当前周期万分收益
                BigDecimal cycTenThousandProfitNow = BigDecimal.ZERO;

                // 若为第一个
                if (index == 0) {
                    // 当前周期万分收益 = 变量.红利再投周期万份收益列表.GET(index)
                    cycTenThousandProfitNow = bonusRstCycTenThdProfitList.get(index);
                }
                else {
                    // 当前周期万分收益 = 变量.红利再投周期万份收益列表.GET(index+1) - 变量.红利再投周期万份收益列
                    cycTenThousandProfitNow = ArithmeticUtil.doSub(
                        bonusRstCycTenThdProfitList.get(index),
                        bonusRstCycTenThdProfitList.get(index - 1));
                }

                // 变量.周期万份收益 = 1*（1+当前周期万分收益）
                cycTenThousandProfit = ArithmeticUtil.multiply(cycTenThousandProfit,
                    ArithmeticUtil.doAdd(BigDecimal.ONE, cycTenThousandProfitNow));
            }

            // 周期开始日和结束日不为空情况
            if (cycBeginDate != null && cycEndDate != null) {
                // 周期开始日和结束日间隔天数
                long betweenDay = DateUtil.getBetweenDayNumber(cycBeginDate, cycEndDate);
                // 间隔天数+1，且转为BigDecimal
                BigDecimal dayNum = ArithmeticUtil.doAdd(new BigDecimal(betweenDay),
                    BigDecimal.ONE);

                // 变量.周期年化收益率 = （变量.周期万份收益-1）*10000 /(参数.周期结束日 - 参数.周期开始日+1)* 参数.计算基础 /10000
                cycYearRate = ArithmeticUtil.multiply(
                        ArithmeticUtil.doSub(cycTenThousandProfit, BigDecimal.ONE), // [（变量.周期万份收益-1）]
                        ArithmeticUtil.divide(new BigDecimal(10000), dayNum), // [10000 /(参数.周期结束日 -
                        // 参数.周期开始日+1)]
                        ArithmeticUtil.divide(new BigDecimal(BasisDays), new BigDecimal(10000))// [参数.计算基础
                        // /10000]
                );
            }

        }

        return cycYearRate;
    }

    /**
     * 计算类共通方法 --- 7日年化收益率
     * 
     * @param bokStatBokPositionList 帐套资产负债信息列表
     * @param cycBeginDate 周期开始日
     * @param cycEndDate 周期结束日
     * @param BasisDays 计算基础天数
     * @param carryover 结转频率
     * @param feeBefOrAft 费前费后区分 [参考枚举类：ValDictCodeList.费前费后区分_XX.getValue()]
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    public BigDecimal getSevenDayYearRate(List<BokStatBokPosition> bokStatBokPositionList,
                                          Date cycBeginDate, Date cycEndDate, int BasisDays,
                                          String carryover, String feeBefOrAft) {

        // 定义 变量.7日年化收益率
        BigDecimal sevenDayYearRate = BigDecimal.ZERO;
        // 定义 变量.周期万份收益
        BigDecimal cycTenThousandProfit = BigDecimal.ZERO;

        // 定义 变量.存续天数 = 周期结束日 - 周期开始日 + 1
        BigDecimal existDays = BigDecimal.ZERO;
        // 周期开始日和结束日不为空情况
        if (cycBeginDate != null && cycEndDate != null) {
            // 周期开始日和结束日间隔天数
            long betweenDay = DateUtil.getBetweenDayNumber(cycBeginDate, cycEndDate);
            // 间隔天数+1，且转为BigDecimal
            existDays = ArithmeticUtil.doAdd(new BigDecimal(betweenDay), BigDecimal.ONE);
        }

        // "非日结情况"
        // MODIFY JINMAODONG 应该根据前台的分红规则来判断是否为日结 START
//        if (!DictCodeList.收益结转频率_按日结转.getValue().equals(carryover)) {
        if(!DictCodeList.分红规则_日付.getValue().equals(carryover)){
        // MODIFY JINMAODONG 应该根据前台的分红规则来判断是否为日结 END
            // 定义下标计数
            int j = 0;
            // 循环周期开始日 到 周期结束日
            while (cycBeginDate != null && cycEndDate != null
                   && cycBeginDate.compareTo(cycEndDate) <= 0) {
                // 帐套资产负债信息列表不为空情况
                if (bokStatBokPositionList != null && bokStatBokPositionList.size() > j
                    && bokStatBokPositionList.get(j) != null) {
                    // 费前情况
                    if (ValDictCodeList.费前费后区分_费前.getValue().equals(feeBefOrAft)) {
                        // 变量.周期万份收益 = 变量.周期万份收益 +NVL(资产负债信息.get(j).万份收益_费前,0)
                        cycTenThousandProfit = ArithmeticUtil.doAdd(cycTenThousandProfit,
                            bokStatBokPositionList.get(j).getpYieldBef());
                    } // 其他情况
                    else {
                        // 变量.周期万份收益 = 变量.周期万份收益 +NVL(资产负债信息.get(j).万份收益_费后,0)
                        cycTenThousandProfit = ArithmeticUtil.doAdd(cycTenThousandProfit,
                            bokStatBokPositionList.get(j).getpYield());
                    }
                }

                // 周期开始日加1天
                cycBeginDate = DateUtil.addDay(cycBeginDate, 1);
                // 下标计数+1
                j++ ;
            }

            // 根据存续天数计算
            if (existDays.intValue() < 7) {
                // 变量.7日年化收益率 = 变量.周期万份收益 /变量.存续天数*计算基础/10000
                sevenDayYearRate = ArithmeticUtil.multiply(
                    ArithmeticUtil.divide(cycTenThousandProfit, existDays),
                    ArithmeticUtil.divide(new BigDecimal(BasisDays), new BigDecimal(10000)));
            }
            else {
                // 变量.7日年化收益率 = 变量.周期万份收益 /7*计算基础/10000
                sevenDayYearRate = ArithmeticUtil.multiply(
                    ArithmeticUtil.divide(cycTenThousandProfit, new BigDecimal(7)),
                    ArithmeticUtil.divide(new BigDecimal(BasisDays), new BigDecimal(10000)));
            }

        } // "日结情况"
        else {

            // 定义下标计数
            int j = 0;
            cycTenThousandProfit = BigDecimal.ONE;
            // 循环周期开始日 到 周期结束日
            while (cycBeginDate != null && cycEndDate != null
                   && cycBeginDate.compareTo(cycEndDate) <= 0) {

                // 获取 NVL(资产负债信息.get(j).万份收益_费前费后,0)
                BigDecimal nvlResult = BigDecimal.ZERO;
                if (bokStatBokPositionList != null && bokStatBokPositionList.size() > j
                    && bokStatBokPositionList.get(j) != null) {
                    // 费前情况
                    if (ValDictCodeList.费前费后区分_费前.getValue().equals(feeBefOrAft)) {
                        // 取万份收益_费前字段
                        nvlResult = bokStatBokPositionList.get(j).getpYieldBef();
                    } // 其他情况
                    else {
                        // 取万份收益字段
                        nvlResult = bokStatBokPositionList.get(j).getpYield();
                    }
                }

                // 变量.周期万份收益 = 1*（1+NVL(资产负债信息.get(j).万份收益_费前费后,0)/10000)
                cycTenThousandProfit = ArithmeticUtil.multiply(cycTenThousandProfit,
                    ArithmeticUtil.doAdd(BigDecimal.ONE,
                        ArithmeticUtil.divide(nvlResult, new BigDecimal(10000))));

                // 周期开始日加1天
                cycBeginDate = DateUtil.addDay(cycBeginDate, 1);
                // 下标计数+1
                j++ ;
            }

            // 根据存续天数计算
            if (existDays.intValue() < 7) {
                // 变量.7日年化收益率 = pow（变量.周期万份收益，计算基础/变量.存续天数）-1
                double powResult = Math.pow(cycTenThousandProfit.doubleValue(),
                    ArithmeticUtil.divide(new BigDecimal(BasisDays), existDays).doubleValue());
                sevenDayYearRate = ArithmeticUtil.doSub(BigDecimal.valueOf(powResult),
                    BigDecimal.ONE);

            }
            else {
                // 变量.7日年化收益率 = pow（变量.周期万份收益，计算基础/7）-1
                double powResult = Math.pow(cycTenThousandProfit.doubleValue(),
                    ArithmeticUtil.divide(new BigDecimal(BasisDays),
                        new BigDecimal(7)).doubleValue());
                sevenDayYearRate = ArithmeticUtil.doSub(BigDecimal.valueOf(powResult),
                    BigDecimal.ONE);
            }

        }

        return sevenDayYearRate;
    }

    /**
     * 债券剩余期限和剩余存续期计算
     * 
     * @param targetFinProduct 债券标的信息
     * @param listIntinfo 付息计划
     * @param calType 计算类型
     * @param nextExcerDate 下一行权日
     * @param calDate 计算日
     * @return 债券剩余期限和剩余存续期限
     * @see
     * @since 1.0
     */
    public BigDecimal[] calSecRemTermAndDuration(TargetFinProduct targetFinProduct,
                                                 List<TargetCashPlan> listIntinfo, String calType,
                                                 Date nextExcerDate, Date calDate) {// 定义
                                                                                    // 变量.剩余期限计算截止日
                                                                                    // =
                                                                                    // 参数.债券标的信息.到期日
        Date endDate = targetFinProduct.getMdate();
        // 定义 变量.下一付息日
        Date nextInterestDate = null;
        // 定义 变量.下一行权日
        // Date nextExDate = null;
        // 1、浮息债剩余存续期去下一付息日
        // IF 参数.计算类型=浮动利率
        if (DictCodeListExt1.计算类型_浮动利率.getValue().equals(calType)) {
            for (TargetCashPlan item : listIntinfo) {
                // IF （参数.计算日 >= 付息计划.计息开始日 AND 参数.计算日 < 付息计划.计息结束日）
                if (DateUtil.compareDate(calDate, item.getVdate()) >= 0
                    && DateUtil.compareDate(calDate, item.getMdate()) <= 0) {
                    // 变量.下一付息日 = 付息计划.计息结束日
                    nextInterestDate = item.getMdate();
                    // 变量.剩余期限计算截止日 = 变量.下一付息日
                    endDate = nextInterestDate;
                    break;
                }
            }
        }

        // 2、含权债处理
        // IF 参数.债券标的信息.是否含权 = 是
        if (DictCodeList.是否_是.getValue().equals(targetFinProduct.getIsRight())) {
            if (nextExcerDate != null) {
                // IF （系统参数.剩余期限计算日 = 01.行权日）
                if (GlobalConstant.DEAL_TYPE_01.equals(
                    CacheUtil.getSysParam("SEC_END_DAYS_SWITCH"))) {
                    // 变量.剩余期限计算截止日 = 参数.下一行权日
                    endDate = nextExcerDate;
                }
                else {
                    // 变量.剩余期限计算截止日 = 参数.债券标的信息.到期日
                    endDate = targetFinProduct.getMdate();
                }
                // IF 参数.计算类型=浮动利率
                if (DictCodeListExt1.计算类型_浮动利率.getValue().equals(calType)) {
                    // IF (变量.下一付息日 < 变量.下一行权日)
                    if (DateUtil.compareDate(nextInterestDate, endDate) < 0) {
                        // 变量.剩余期限计算截止日 = 变量.下一付息日
                        endDate = nextInterestDate;
                    }
                    // else {
                    // // 变量.剩余期限计算截止日 = 变量.下一行权日
                    // endDate = nextExDate;
                    // }
                }
            }
            else {
                // IF 参数.计算类型=浮动利率
                if (DictCodeListExt1.计算类型_浮动利率.getValue().equals(calType)) {
                    // 变量.剩余期限计算截止日 = 变量.下一付息日
                    endDate = nextInterestDate;
                }
                else {
                    // 变量.剩余期限计算截止日 = 参数.债券标的信息.到期日
                    endDate = targetFinProduct.getMdate();
                }
            }

        }

        // 3、交易所非贴现债扣除2-29
        // 定义 变量.包含2-29数 = 0
        int containCount = 0;
        // IF （参数.债券标的信息.交易场所=上交所或者深交所 AND 参数.债券标的信息.息票品种 ！= 贴现）
        if ((DictCodeListExt1.估值交易市场_上交所.getValue().equals(targetFinProduct.getTradeMarket())
             || DictCodeListExt1.估值交易市场_深交所.getValue().equals(targetFinProduct.getTradeMarket()))
            && !DictCodeList.息票品种_贴现.getValue().equals(targetFinProduct.getCouponSpecies())) {
            // 变量.包含2-29数 = 【参数.计算日】到【变量.剩余期限计算截止日】中包含的2-29个数
            // 定义 变量.日期 = 计算日
            Date date = calDate;
            // 循环从 计算日开始 ~ 剩余期限计算截止日,算2月29日的个数
            //MODIFY JINMAODONG 20181201 之前代码未考虑计算日 == 到期日的时候，会死循环
            while(DateUtil.compareDate(date, endDate) < 0){
                if (DateUtil.format(date, "yyyy-MM-dd").contains("02-29")) {
                    containCount++ ;
                }
                date = DateUtil.addDay(date, 1);
            }
//            do {
//                if (DateUtil.format(date, "yyyy-MM-dd").contains("02-29")) {
//                    containCount++ ;
//                }
//                date = DateUtil.addDay(date, 1);
//            }
//            while (DateUtil.compareDate(date, endDate) != 0);
        }
        // 返回结果
        // 变量.剩余期限 = 变量.剩余期限计算截止日-参数.计算日-变量.包含2-29数
        BigDecimal remainTerm = new BigDecimal(
            DateUtil.getBetweenDayNumber(calDate, endDate) - containCount);
        // 变量.剩余存续期限 = 参数.债券标的信息.到期日-参数.计算日
        BigDecimal remainDuration = new BigDecimal(
            DateUtil.getBetweenDayNumber(calDate, targetFinProduct.getMdate()));
        return new BigDecimal[] {remainTerm, remainDuration};
    }

    /**
     * 利息计提-计算百元面值的债券的利息
     * 
     * @param acctEntityEx 计提基础信息DTO
     * @param rstBaseinfo 标的信息
     * @param listIntinfo 付息计划列表
     * @param listCashinfo 报价信息列表
     * @param listPayinfo 支付规则
     * @param varFaceValue 百元面值
     * @return AccurIntCalOutParameter
     * @see
     * @since 1.0
     */
    public AccurIntCalOutParameter getCalculate(DrawBaseInfoSelectComDTO acctEntityEx,
                                                TargetFinProduct rstBaseinfo,
                                                List<TargetCashPlan> listIntinfo,
                                                List<TargetCashCalcRule> listCashinfo,
                                                List<TargetCashPayRule> listPayinfo,
                                                BigDecimal varFaceValue,
                                                DrawBaseInfoSelectComResult acctEntity) {
        // 组装参数
        AccurIntCalInParameter parameter = getCalInParam(acctEntityEx, rstBaseinfo, listIntinfo,
            listCashinfo, listPayinfo, varFaceValue, acctEntity);
        // 计算百元面值的债券的利息
        AccurIntForBookCalculator calculator2 = CalculatorFactory.createAccurIntForBookCalculator();
        AccurIntCalOutParameter output2 = calculator2.calculate(parameter);
        return output2;
    }
    
    /**
     * 利息计提-计算百元面值的债券的利息（计算日期不加1 资产重分类用）
     * 
     * @param acctEntityEx 计提基础信息DTO
     * @param rstBaseinfo 标的信息
     * @param listIntinfo 付息计划列表
     * @param listCashinfo 报价信息列表
     * @param listPayinfo 支付规则
     * @param varFaceValue 百元面值
     * @return AccurIntCalOutParameter
     * @see
     * @since 1.0
     */
    public AccurIntCalOutParameter getCalculateNotPlusDay(DrawBaseInfoSelectComDTO acctEntityEx,
                                                TargetFinProduct rstBaseinfo,
                                                List<TargetCashPlan> listIntinfo,
                                                List<TargetCashCalcRule> listCashinfo,
                                                List<TargetCashPayRule> listPayinfo,
                                                BigDecimal varFaceValue,
                                                DrawBaseInfoSelectComResult acctEntity) {
        // 组装参数
        AccurIntCalInParameter parameter = getCalInParam(acctEntityEx, rstBaseinfo, listIntinfo,
            listCashinfo, listPayinfo, varFaceValue, acctEntity);
        // 计算百元面值的债券的利息
        AccurIntCalculator calculator2 = CalculatorFactory.createAccurIntCalculator();
        AccurIntCalOutParameter output2 = calculator2.calculate(parameter);
        return output2;
    }

    /**
     * 利息计提-计算百元面值的债券的利息-ABS债用
     *
     * @param acctEntityEx 计提基础信息DTO
     * @param rstBaseinfo 标的信息
     * @param listIntinfo 付息计划列表
     * @param listCashinfo 报价信息列表
     * @param listPayinfo 支付规则
     * @param varFaceValue 百元面值
     * @return AccurIntCalOutParameter
     * @see
     * @since 1.0
     */
    public AccurIntCalOutParameter getCalculateForAbs(DrawBaseInfoSelectComDTO acctEntityEx,
                                                TargetFinProduct rstBaseinfo,
                                                List<TargetCashPlan> listIntinfo,
                                                List<TargetCashCalcRule> listCashinfo,
                                                List<TargetCashPayRule> listPayinfo,
                                                BigDecimal varFaceValue,
                                                DrawBaseInfoSelectComResult acctEntity) {
        // 组装参数
        AccurIntCalInParameter parameter = getCalInParam(acctEntityEx, rstBaseinfo, listIntinfo,
                listCashinfo, listPayinfo, varFaceValue, acctEntity);
        // 计算百元面值的债券的利息
        AccurIntForAbsBookCalculator calculator2 = CalculatorFactory.createAccurIntForAbsBookCalculator();
        AccurIntCalOutParameter output2 = calculator2.calculate(parameter);
        return output2;
    }
    
    /**
     * 算法参数组装：利息计提-计算百元面值的债券的利息 
     * 
     * @param acctEntityEx 计提基础信息DTO
     * @param rstBaseinfo 标的信息
     * @param listIntinfo 付息计划列表
     * @param listCashinfo 报价信息列表
     * @param listPayinfo 支付规则
     * @param varFaceValue 百元面值
     * @return AccurIntCalOutParameter
     * @see
     * @since 1.0
     */
    public AccurIntCalInParameter getCalInParam(DrawBaseInfoSelectComDTO acctEntityEx,
                                                TargetFinProduct rstBaseinfo,
                                                List<TargetCashPlan> listIntinfo,
                                                List<TargetCashCalcRule> listCashinfo,
                                                List<TargetCashPayRule> listPayinfo,
                                                BigDecimal varFaceValue,
                                                DrawBaseInfoSelectComResult acctEntity) {
        AccurIntCalInParameter parameter = new AccurIntCalInParameter();
        // 计算日
        // parameter.setCalDate(BasicUtil.dateToLocal(addDate(acctEntity.getHappenDate(), 1)));
        parameter.setCalDate(rstBaseinfo.getVdate().compareTo(acctEntity.getHappenDate()) > 0
                ? BasicUtil.dateToLocal(rstBaseinfo.getVdate()) : BasicUtil.dateToLocal(acctEntity.getHappenDate()));
        // 资产信息
        SecInfoModel secInfoModel = new SecInfoModel();
        // 债券起息日
        secInfoModel.setStartDate(BasicUtil.dateToLocal(rstBaseinfo.getVdate()));
        // 债券到期日
        secInfoModel.setEndDate(BasicUtil.dateToLocal(rstBaseinfo.getActmdate()));
        // 债券首次付息日
        Date firstPayDate = listPayinfo != null && listPayinfo.size() > 0 ? listPayinfo.get(
            0).getFirstPayDate() : null;
        secInfoModel.setFirstPayDate(BasicUtil.dateToLocal(firstPayDate));
        // 发行价
        secInfoModel.setIssuePrice(rstBaseinfo.getIssuePrice());
        // 债券类型
        Variety bondsType = null;
        if (DictCodeList.息票品种_贴现.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.贴现;
        }
        else if (DictCodeList.息票品种_零息.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.零息;
        }
        else if (DictCodeList.息票品种_附息.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.附息;
        }
        else if (DictCodeList.息票品种_利随本清.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.利随本清;
        }
        secInfoModel.setBondsType(bondsType);
        // 计息基础
        Basis basis = null;
        String basisString = listCashinfo != null
                             && listCashinfo.size() > 0 ? listCashinfo.get(0).getBasis() : null;
        if (DictCodeList.计息基础_A_360.getValue().equals(basisString)) {
            basis = Basis.A_360;
        }
        else if (DictCodeList.计息基础_A_365.getValue().equals(basisString)) {
            basis = Basis.A_365;
        }
        else if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)
                 || DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            basis = Basis.A_A;
        }
        else if (DictCodeList.计息基础_A_365F.getValue().equals(basisString)) {
            basis = Basis.A_365F;
        }
        else if (DictCodeList.计息基础_30_360.getValue().equals(basisString)) {
            basis = Basis._30_360;
        }
        /**ADD BY WUJIE ON 20200208 START**/
        else if (DictCodeListExt3.计息基础_30U_360.getValue().equals(basisString)) {
            basis = Basis._30U_360;
        }
        /**ADD BY WUJIE ON 20200208 END**/
        
        secInfoModel.setBasis(basis);
        // 利率类型
        IntRateType intRateType = null;
        String intRateTypeString = listCashinfo != null
                                   && listCashinfo.size() > 0 ? listCashinfo.get(
                                       0).getCalcType() : null;
        if (DictCodeList.利率类型_固定利率.getValue().equals(intRateTypeString)) {
            intRateType = IntRateType.固定利率;
        }
        else if (DictCodeList.利率类型_浮动利率.getValue().equals(intRateTypeString)) {
            intRateType = IntRateType.浮动利率;
        }
        //20200820 DENGYU 修改 债券中间利率变更，利率变更后付息日前一日债券计提错误，计提值仍是按照利率变更前的利率来计算的 start
        intRateType = IntRateType.浮动利率;
        //20200820 DENGYU 修改 债券中间利率变更，利率变更后付息日前一日债券计提错误，计提值仍是按照利率变更前的利率来计算的 end
        secInfoModel.setIntRateType(intRateType);
        // 利息分配方式
        AllocationType allocationType = null;
        if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)) {
            allocationType = AllocationType.平均分配;
        }
        else if (DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            allocationType = AllocationType.按实际天数分配;
        }
        else {
            allocationType = AllocationType.按实际天数分配;
        }
        secInfoModel.setAllocationType(allocationType);
        // 付息计划推算方式
        SecIntSchCalMethod secIntSchCalMethod = null;
        String secIntSchCalMethodString = listPayinfo != null
                                          && listPayinfo.size() > 0 ? listPayinfo.get(
                                              0).getCalcRule() : null;
        if (DictCodeList.推算方式_起息日向后.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
        }
        else if (DictCodeList.推算方式_到期日向前.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.到期日向前;
        }
        else if (DictCodeList.推算方式_首次支付日.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.首次付息日;
        }
        secInfoModel.setCalMethod(secIntSchCalMethod);
        // 付息频率
        IntFrequency yearIntRate = null;
        String yearIntRateString = listPayinfo != null && listPayinfo.size() > 0 ? listPayinfo.get(
            0).getPayCycle() : null;
        if (DictCodeList.频率_月.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.月付;
        }
        else if (DictCodeList.频率_季.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.季付;
        }
        else if (DictCodeList.频率_年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.年付;
        }
        else if (DictCodeList.频率_半年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.半年付;
        }
        else {
            yearIntRate = IntFrequency.年付;
        }
        secInfoModel.setYearIntRate(yearIntRate);
        parameter.setSecInfoModel(secInfoModel);

        // 付息计划列表
        List<SecIntSchedule> secIntScheduleList = new ArrayList<SecIntSchedule>();
        if (listIntinfo != null && listIntinfo.size() > 0) {
            for (TargetCashPlan targetCashPlan : listIntinfo) {
                SecIntSchedule secIntSchedule = new SecIntSchedule();
                // 理论起始日
                secIntSchedule.setTheoryStartDate(
                    targetCashPlan.getVdateTerm() == null ? BasicUtil.dateToLocal(
                        targetCashPlan.getVdate()) : BasicUtil.dateToLocal(
                            targetCashPlan.getVdateTerm()));
                // 理论截止日
                secIntSchedule.setTheoryCutoffDate(
                    targetCashPlan.getMdateTerm() == null ? BasicUtil.dateToLocal(
                        targetCashPlan.getMdate()) : BasicUtil.dateToLocal(
                            targetCashPlan.getMdateTerm()));
                // 实际起始日
                secIntSchedule.setActualStartDate(
                    BasicUtil.dateToLocal(targetCashPlan.getVdate()));
                // 实际截止日
                secIntSchedule.setActualCutoffDate(
                    BasicUtil.dateToLocal(targetCashPlan.getMdate()));
                // 计息本金
                secIntSchedule.setCorpus(new BigDecimal(100));
                // 利息金额
                secIntSchedule.setAmount(targetCashPlan.getCashAmt());
                secIntScheduleList.add(secIntSchedule);
            }
            parameter.setSecIntScheduleList(secIntScheduleList);
        }

        // 债券利率重置信息
        List<PaperIrReset> paperIrResetList = new ArrayList<PaperIrReset>();
        if (listCashinfo != null && listCashinfo.size() > 0) {
            for (TargetCashCalcRule targetCashCalcRule : listCashinfo) {
                PaperIrReset paperIrReset = new PaperIrReset();
                // 票面利率生效日
                paperIrReset.setParRateEffDate(
                    BasicUtil.dateToLocal(targetCashCalcRule.getEffDate()));
                // 票面利率
                paperIrReset.setParRate(targetCashCalcRule.getYield());
                paperIrResetList.add(paperIrReset);
            }
            parameter.setPaperIrResetList(paperIrResetList);
        }

        // 当前面值
        parameter.setPaperAmt(varFaceValue);
        return parameter;
    }
    
    /**
     * 
     * 摊销重算配置查询<一句话功能简述>
     * <功能详细描述>
     * @param booksetId
     * @param busiId
     * @param cdate
     * @return
     * @see
     * @since 1.0
     */
    public List<BokAmoConfig> getBokAmoConfig(String booksetId,
    		String busiId, Date cdate) {
    	return calculationCommonMapper.getBokAmoConfig(booksetId, busiId, cdate);
    }

    /**
     *
     * 质押式回购计提_交易所
     * @param tradeInfoDto 交易标的
     * @param subjectNum 持仓份额
     * @param subjectAmt 成本
     * @param calDate 计算日
     * @param calType 计提方式
     * @param subjectIntAmt 应计利息
     *
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal[] calCollateralisedRepo(TradeInfoDto tradeInfoDto, BigDecimal subjectNum, BigDecimal subjectAmt,
                                              Date calDate, String calType, BigDecimal subjectIntAmt) {
        //变量.占比到期金额 = 参数.持仓份额 * 参数.标的信息.到期金额/ 参数.标的信息.名义本金
        BigDecimal 占比到期金额 =ArithmeticUtil.divide(ArithmeticUtil.multiply(subjectNum,tradeInfoDto.getMTradeAmt()),tradeInfoDto.getPrin());
        占比到期金额=ArithmeticUtil.round(占比到期金额,NumberEnum.MONEY);
        //变量.计算日
        Date newcalDate;
        //开始日
        Date beginDate;
        //结算日
        Date endDate;
        BigDecimal 累计应计利息=BigDecimal.ZERO;
        BigDecimal 发生应计利息=BigDecimal.ZERO;
        //交易所回购计提天数方式
        String seRepDayTypeNow = tradeInfoDto.getSeRepDayType() == null ? CacheUtil.getSysParam("SE_REP_DAY_TYPE") : tradeInfoDto.getSeRepDayType();
        if(seRepDayTypeNow !=null && seRepDayTypeNow.equals(DictCodeListExt1.交易所回购计提天数方式_交易天数.getValue())) {
            newcalDate=calDate;
            beginDate=tradeInfoDto.getVdate();
            endDate=tradeInfoDto.getMdate();
        }else {
            newcalDate=DateUtil.addDay(calDate, 1);
            beginDate=tradeInfoDto.getVpayDate();
            endDate=tradeInfoDto.getMpayDate();
        }
        long dates1=DateUtil.getBetweenDayNumber(beginDate, newcalDate);
        long dates2=DateUtil.getBetweenDayNumber(beginDate, endDate);
        if(calType.equals(DictCodeList.计提方式_区间倒减.getValue())) {
            //出参.累计应计利息 = (变量.占比到期金额 - 参数.成本) * （参数.计算日 - 变量.开始日）/ （变量.结束日 - 变量.开始日）
            累计应计利息=ArithmeticUtil.divide(ArithmeticUtil.multiply(ArithmeticUtil.doSub(占比到期金额, subjectAmt),new BigDecimal(dates1)),new BigDecimal(dates2));
            累计应计利息=ArithmeticUtil.round(累计应计利息,NumberEnum.MONEY);
            //出参.发生应计利息 = 出参.累计应计利息 - 参数.应计利息
            发生应计利息=ArithmeticUtil.round(ArithmeticUtil.doSub(累计应计利息,subjectIntAmt),NumberEnum.MONEY);
        }else {
            //出参.发生应计利息 = (变量.占比到期金额 - 参数.成本) / （变量.结束日 - 变量.开始日）
            发生应计利息=ArithmeticUtil.divide(ArithmeticUtil.doSub(占比到期金额,subjectAmt),new BigDecimal(dates2));
            发生应计利息=ArithmeticUtil.round(发生应计利息,NumberEnum.MONEY);
            //出参.累计应计利息 = 出参.发生应计利息 + 参数.应计利息
            累计应计利息=ArithmeticUtil.round(ArithmeticUtil.doAdd(发生应计利息,subjectIntAmt),NumberEnum.MONEY);
        }
        return new BigDecimal[] {累计应计利息,发生应计利息};
    }
    
    /**
     * 回补基准计算
     * 
     * @param targetPerfRewardRule 业绩报酬规则
     * @param lastAccrDate 上次计提日
     * @param accrDate 本次计提日
     * @return 回补利率
     */
    public BigDecimal clawbackBenchMarkCalc(TargetFinClawbackCalcRule targetFinClawbackCalcRule, Date lastAccrDate,
                                            Date accrDate, List<TargetFinClawbackCalcRule> targetFinClawbackCalcRuleList){
    	if(targetFinClawbackCalcRule == null){
    		return BigDecimal.ZERO;
    	}
    	//基准利率生效日
    	Date benchRateDate;
    	if(StringUtil.equals(targetFinClawbackCalcRule.getActRule(), DictCodeList.生效规则_期初.getValue())){
    		benchRateDate = lastAccrDate;
    	} else {
    		benchRateDate = accrDate;
    	}
    	
    	//调整系数
    	BigDecimal coefficient = BigDecimal.ZERO;
    	//利差
    	BigDecimal spreadRate = BigDecimal.ZERO;
    	//基准利率
    	BigDecimal basicRate = BigDecimal.ZERO;

        // modify by weipeng ：此处改造为一体化版本与单估值版本的生效规则影响逻辑不同，做改造处理，若利率行情信息为空，则添加单估值逻辑 start
        Map<String, MstIntRateInfo> mapsMstIntRateInfo = finProductCommonService.getValIntRateInfo(benchRateDate, null);
        //  定义 变量.回补计提规则 = 参数.回补计提规则 ---默认初始化值
        TargetFinClawbackCalcRule clawbackCalcRule = targetFinClawbackCalcRule;
        if(mapsMstIntRateInfo == null || mapsMstIntRateInfo.isEmpty()){
            // 变量.回补计提规则  = 初始化对象，防止期初业绩基准不存在的情况
            clawbackCalcRule = new TargetFinClawbackCalcRule();
            // 业绩报酬计提规则明细与业绩报酬计提规则为一一对应，故此处根据生效日取对应的业绩报酬计提规则即可
            for(TargetFinClawbackCalcRule item : targetFinClawbackCalcRuleList){
                // 取生效日对应的业绩报酬计提规则，下面计算时取生效规则对应生效日的业绩报酬计提明细
                if(DateUtil.compareDate(benchRateDate, item.getEffDate()) == 0){
                    clawbackCalcRule = item;
                    break;
                }
            }
        }
        // modify by weipeng ：此处改造为一体化版本与单估值版本的生效规则影响逻辑不同，做改造处理，若利率行情信息为空，则添加单估值逻辑 end

    	List<TargetPerfRewardCalRuleDetail> listTargetPerfRewardCalRuleDetail = clawbackCalcRule.getTargetPerfRewardCalRuleDetailList();
        // 防止期初基准利率为空，加判空处理
        if(!CollectionUtils.isEmpty(listTargetPerfRewardCalRuleDetail)){
            // 集合需要按照层级进行排序
            Collections.sort(listTargetPerfRewardCalRuleDetail,
                new Comparator<TargetPerfRewardCalRuleDetail>() {

                    @Override
                    public int compare(TargetPerfRewardCalRuleDetail o1,
                                       TargetPerfRewardCalRuleDetail o2) {
                        return o1.getLevel() - o2.getLevel();
                    }
                });
            List<TargetPerfRewardCalRuleDetail> targetPerfRewardRuleDetailListTmp = new ArrayList<>();
            for (TargetPerfRewardCalRuleDetail targetPerfRewardRuleDetail : listTargetPerfRewardCalRuleDetail) {
                // 调整系数 = 业绩报酬规则明细.调整系数
                coefficient = targetPerfRewardRuleDetail.getCoefficient();
                // 利差 = 业绩报酬规则明细.利差
                spreadRate = targetPerfRewardRuleDetail.getSpreadRate();
                if (!StringUtil.isEmpty(targetPerfRewardRuleDetail.getBasicRate())
                    && mapsMstIntRateInfo != null && mapsMstIntRateInfo.size() > 0) {
                    /**增加为空判定 add by yanya on 20191210**/
                    if(mapsMstIntRateInfo.get(targetPerfRewardRuleDetail.getBasicRate()) != null) {
                        basicRate = mapsMstIntRateInfo.get(
                            targetPerfRewardRuleDetail.getBasicRate()).getIntRate();
                    }
                }
            }
        }

        return ArithmeticUtil.doAdd(ArithmeticUtil.multiply(basicRate,coefficient), spreadRate);
    }
    /**
     * 判断交易是否入账
     * add by wujie on 20190730
     * @return
     */
    public boolean isTradeDealAccount(String booksetId,Date tradeDate,List<String> pslist){
    	boolean flag = false;
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("BOOKSET_ID", booksetId);
    	map.put("TRADE_DATE", tradeDate);
    	map.put("pslist", pslist);
    	int count = calculationCommonMapper.tradeDealAccountCount(map);
    	if(count>0){
    		flag = true;
    	}
    	return flag;
    }

//    /**
//     * 查询汇率
//     * 最新添加，用作本位币处理（交易，如存款、债券等），原有的方法始终返回1，防止影响其他调用，故重写
//     * @param 基础币种 基础币种
//     * @param 报价币种 报价币种
//     * @param 交易日 交易日
//     * @return 汇率
//     * @see
//     * @since 1.0
//     */
//    public BigDecimal queryExchangeRateForTrade(String 基础币种, String 报价币种, Date 交易日) {
//        if (基础币种.equals(报价币种)) {
//            return BigDecimal.ONE;
//        }
//        List<ExchangeInfo> rateList = calculationCommonMapper.queryExchangeRate(基础币种, 报价币种, 交易日);
//        if (rateList == null || rateList.isEmpty() || rateList.get(0).getExRate() == null) {
//            return BigDecimal.ONE;
//        }
//        return rateList.get(0).getExRate();
//        //return new BigDecimal(1);
//    }


    /**
     * 交易各类费用处理
     *
     * @param listValTradeFeeDetail 交易费用表 finprodId 金融产品代码
     * @param booksetId 理财产品代码
     * @param groupId 投资组合代码
     * @param finprodId 金融产品代码
     * @return DealTypesFeeDto
     * @see
     * @since 1.0
     */
    public DealTypesFeeDto quaryDealTypesFeeamt(List<TrdTradeFeeDetail> listValTradeFeeDetail,
                                                String booksetId, String groupId, String finprodId) {
        DealTypesFeeDto dealTypesFeeDto = new DealTypesFeeDto();
        // 交易费用
        BigDecimal feeAmt = BigDecimal.ZERO;
        // 印花税
        BigDecimal stampDuty = BigDecimal.ZERO;
        // 经手费
        BigDecimal brokerage = BigDecimal.ZERO;
        // 证管费
        BigDecimal certificateFee = BigDecimal.ZERO;
        // 结算费
        BigDecimal clearingFee = BigDecimal.ZERO;
        // 佣金
        BigDecimal commission = BigDecimal.ZERO;
        // 交易手续费
        BigDecimal transactionFee = BigDecimal.ZERO;
        // 结算手续费
        BigDecimal settlementFee = BigDecimal.ZERO;
        // 通道费
        BigDecimal chlFee = BigDecimal.ZERO;
        // 通道管理费
        BigDecimal chlGlFee = BigDecimal.ZERO;
        // 通道销售费
        BigDecimal chlXsFee = BigDecimal.ZERO;
        // 通道托管费
        BigDecimal chlTgFee = BigDecimal.ZERO;
        // 不随交易费用
        BigDecimal noFeeAmt = BigDecimal.ZERO;
        // 印花税账务明细代码
        BokDetailId stampDutyCode = null;
        // 经手费账务明细代码
        BokDetailId brokerageCode = null;
        // 证管费账务明细代码
        BokDetailId certificateFeeCode = null;
        // 结算费账务明细代码
        BokDetailId clearingFeeCode = null;
        // 佣金账务明细代码
        BokDetailId commissionCode = null;
        // 交易手续费账务明细代码
        BokDetailId transactionFeeCode = null;
        // 结算手续费账务明细代码
        BokDetailId settlementFeeCode = null;
        // 通道费账务明细代码
        BokDetailId chlFeeCode = null;
        // 通道管理费账务明细代码
        BokDetailId chlGlFeeCode = null;
        // 通道销售费账务明细代码
        BokDetailId chlXsFeeCode = null;
        // 通道托管费账务明细代码
        BokDetailId chlTgFeeCode = null;

        // 随交易支付_印花税
        BigDecimal stampDutyForDealPay = BigDecimal.ZERO;
        // 随交易支付_经手费
        BigDecimal brokerageForDealPay = BigDecimal.ZERO;
        // 随交易支付_证管费
        BigDecimal certificateFeeForDealPay = BigDecimal.ZERO;
        // 随交易支付_结算费
        BigDecimal clearingFeeForDealPay = BigDecimal.ZERO;
        // 随交易支付_佣金
        BigDecimal commissionForDealPay = BigDecimal.ZERO;
        // 随交易支付_交易手续费
        BigDecimal transactionFeeForDealPay = BigDecimal.ZERO;
        // 随交易支付_结算手续费
        BigDecimal settlementFeeForDealPay = BigDecimal.ZERO;
        // 随交易支付_印花税账务明细代码
        BokDetailId stampDutyCodeForDealPay = null;
        // 随交易支付_经手费账务明细代码
        BokDetailId brokerageCodeForDealPay = null;
        // 随交易支付_证管费账务明细代码
        BokDetailId certificateFeeCodeForDealPay = null;
        // 随交易支付_结算费账务明细代码
        BokDetailId clearingFeeCodeForDealPay = null;
        // 随交易支付_佣金账务明细代码
        BokDetailId commissionCodeForDealPay = null;
        // 随交易支付_交易手续费账务明细代码
        BokDetailId transactionFeeCodeForDealPay = null;
        // 随交易支付_结算手续费账务明细代码
        BokDetailId settlementFeeCodeForDealPay = null;
        /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  start**/
        // 其他费用
        BigDecimal otherFee = BigDecimal.ZERO;
        // 申购费
        BigDecimal subscriptionFee = BigDecimal.ZERO;
        // 赎回费
        BigDecimal redemptionFee = BigDecimal.ZERO;
        // 认购费
        BigDecimal offerFee = BigDecimal.ZERO;
        // 其他费用账务明细代码
        BokDetailId otherFeeCode = null;
        // 申购费账务明细代码
        BokDetailId subscriptionFeeCode = null;
        // 赎回费账务明细代码
        BokDetailId redemptionFeeCode = null;
        // 认购费账务明细代码
        BokDetailId offerFeeCode = null;

        // 其他费用_随交易支付
        BigDecimal otherFeeForDealPay = BigDecimal.ZERO;
        // 申购费_随交易支付
        BigDecimal subscriptionFeeForDealPay = BigDecimal.ZERO;
        // 赎回费_随交易支付
        BigDecimal redemptionFeeForDealPay = BigDecimal.ZERO;
        // 认购费_随交易支付
        BigDecimal offerFeeForDealPay = BigDecimal.ZERO;
        // 其他费用账务明细代码_随交易支付
        BokDetailId otherFeeCodeForDealPay = null;
        // 申购费账务明细代码 _随交易支付
        BokDetailId subscriptionFeeCodeForDealPay = null;
        // 赎回费账务明细代码_随交易支付
        BokDetailId redemptionFeeCodeForDealPay = null;
        // 认购费账务明细代码_随交易支付
        BokDetailId offerFeeCodeForDealPay = null;
        /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  end**/

        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
        // 定义查询类型 实体类
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto);

        // 获取标的信息
//        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(finprodId,
//            targetSearchDtoList);
//        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);
        // 金融产品类型
//        String finprodType = null;
//        if (targetFinProduct != null) {
//            finprodType = targetFinProduct.getFinprodType();
//        }
        if (listValTradeFeeDetail != null && listValTradeFeeDetail.size() > 0) {
            BokBookset bokbookset = bokBooksetMapper.selectByPrimaryKey(booksetId);
            BokDetailId bokDetailId = new BokDetailId();
            if(StringUtil.isNotEmpty(bokbookset)){
                // 取账务明细代码，下面直接用该账务明细代码即可
                bokDetailId = bookCommonLogic.selectBokdetailId(booksetId,
                        bokbookset.getAcctSubjectId(), bokbookset.getAcctSubjectType());
            }
            for (TrdTradeFeeDetail valTradeFeeDetail : listValTradeFeeDetail) {
                // 是否交易费用 = "是"
                if (DictCodeList.是否_是.getValue().equals(valTradeFeeDetail.getIsPayWithTrade())) {
                    feeAmt = ArithmeticUtil.doAdd(feeAmt, valTradeFeeDetail.getFeeAmt());
                    if (DictCodeListExt1.一次性费用类型_通道费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        chlFee = ArithmeticUtil.doAdd(chlFee, valTradeFeeDetail.getFeeAmt());
                        // 【【出参】交易各类费用.通道费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
                        chlFeeCode = bookCommonLogic.getValBokDetailId(
                                valTradeFeeDetail.getFeeId(), null, valTradeFeeDetail.getFeeType(),valTradeFeeDetail.getCcy());
                    }
                    else if (DictCodeListExt1.一次性费用类型_通道管理费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        chlGlFee = ArithmeticUtil.doAdd(chlGlFee, valTradeFeeDetail.getFeeAmt());
                        // 【【出参】交易各类费用.通道管理费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
                        chlGlFeeCode = bookCommonLogic.getValBokDetailId(
                                valTradeFeeDetail.getFeeId(), null, valTradeFeeDetail.getFeeType(),valTradeFeeDetail.getCcy());
                    }
                    else if (DictCodeListExt1.一次性费用类型_通道销售费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        chlXsFee = ArithmeticUtil.doAdd(chlXsFee, valTradeFeeDetail.getFeeAmt());
                        // 【【出参】交易各类费用.通道销售费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
                        chlXsFeeCode = bookCommonLogic.getValBokDetailId(
                                valTradeFeeDetail.getFeeId(), null, valTradeFeeDetail.getFeeType(),valTradeFeeDetail.getCcy());
                    }
                    else if (DictCodeListExt1.一次性费用类型_通道托管费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        chlTgFee = ArithmeticUtil.doAdd(chlTgFee, valTradeFeeDetail.getFeeAmt());
                        // 【【出参】交易各类费用.通道托管费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
                        chlTgFeeCode = bookCommonLogic.getValBokDetailId(
                                valTradeFeeDetail.getFeeId(), null, valTradeFeeDetail.getFeeType(),valTradeFeeDetail.getCcy());
                    }

                    // 随交易费用
                    if (DictCodeListExt1.一次性费用类型_印花税.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_印花税 += 费用金额
                        stampDutyForDealPay = ArithmeticUtil.doAdd(stampDutyForDealPay,
                                valTradeFeeDetail.getFeeAmt());
                        stampDutyCodeForDealPay = bokDetailId;
                        // 【出参】交易各类费用.随交易支付_印花税账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            stampDutyCodeForDealPay = bookCommonLogic.getValBokDetailId(finprodId,
//                                null, finprodType);
//                        }
//                        else {
//                            stampDutyCodeForDealPay = bookCommonLogic.getValBokDetailId(groupId,
//                                null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }

                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        stampDutyCode = stampDutyCodeForDealPay;

                    }
                    else if (DictCodeListExt1.一次性费用类型_经手费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_经手费 += 费用金额
                        brokerageForDealPay = ArithmeticUtil.doAdd(brokerageForDealPay,
                                valTradeFeeDetail.getFeeAmt());
                        brokerageCodeForDealPay = bokDetailId;
                        // 【出参】交易各类费用.随交易支付_经手费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            brokerageCodeForDealPay = bookCommonLogic.getValBokDetailId(finprodId,
//                                null, finprodType);
//                        }
//                        else {
//                            brokerageCodeForDealPay = bookCommonLogic.getValBokDetailId(groupId,
//                                null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        brokerageCode = brokerageCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_证管费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_证管费 += 费用金额
                        certificateFeeForDealPay = ArithmeticUtil.doAdd(certificateFeeForDealPay,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_证管费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            certificateFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                finprodId, null, finprodType);
//                        }
//                        else {
//                            certificateFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                groupId, null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        certificateFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        certificateFeeCode = certificateFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_结算费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_结算费 += 费用金额
                        clearingFeeForDealPay = ArithmeticUtil.doAdd(clearingFeeForDealPay,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_结算费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            clearingFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                finprodId, null, finprodType);
//                        }
//                        else {
//                            clearingFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(groupId,
//                                null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        clearingFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        clearingFeeCode = clearingFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_佣金.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_佣金 += 费用金额
                        commissionForDealPay = ArithmeticUtil.doAdd(commissionForDealPay,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_佣金账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            commissionCodeForDealPay = bookCommonLogic.getValBokDetailId(finprodId,
//                                null, finprodType);
//                        }
//                        else {
//                            commissionCodeForDealPay = bookCommonLogic.getValBokDetailId(groupId,
//                                null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        commissionCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        commissionCode = commissionCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_交易手续费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_交易手续费 += 费用金额
                        transactionFeeForDealPay = ArithmeticUtil.doAdd(transactionFeeForDealPay,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_交易手续费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            transactionFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                finprodId, null, finprodType);
//                        }
//                        else {
//                            transactionFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                groupId, null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        transactionFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        transactionFeeCode = transactionFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_结算手续费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_结算手续费 += 费用金额
                        settlementFeeForDealPay = ArithmeticUtil.doAdd(settlementFeeForDealPay,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.随交易支付_结算手续费账务明细代码 = 调用账务明细代码共通（费用.费用代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            settlementFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                finprodId, null, finprodType);
//                        }
//                        else {
//                            settlementFeeCodeForDealPay = bookCommonLogic.getValBokDetailId(
//                                groupId, null, DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        settlementFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        settlementFeeCode = settlementFeeCodeForDealPay;
                    }
                    /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  start**/
                    else if (DictCodeListExt1.一次性费用类型_其他费用.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_其他费用 += 费用金额
                        otherFeeForDealPay = ArithmeticUtil.doAdd(otherFeeForDealPay, valTradeFeeDetail.getFeeAmt());
                        otherFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        otherFeeCode = otherFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_申购费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_申购费 += 费用金额
                        subscriptionFeeForDealPay = ArithmeticUtil.doAdd(subscriptionFeeForDealPay, valTradeFeeDetail.getFeeAmt());
                        subscriptionFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        subscriptionFeeCode = subscriptionFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_赎回费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_赎回费 += 费用金额
                        redemptionFeeForDealPay = ArithmeticUtil.doAdd(redemptionFeeForDealPay, valTradeFeeDetail.getFeeAmt());
                        redemptionFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        redemptionFeeCode = redemptionFeeCodeForDealPay;
                    }
                    else if (DictCodeListExt1.一次性费用类型_认购费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.随交易支付_认购费 += 费用金额
                        offerFeeForDealPay = ArithmeticUtil.doAdd(offerFeeForDealPay, valTradeFeeDetail.getFeeAmt());
                        offerFeeCodeForDealPay = bokDetailId;
                        // 由于目前账务明细代码相同，调用时统一使用不随交易支付费用账务明细代码
                        offerFeeCode = offerFeeCodeForDealPay;
                    }
                    /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  end**/

                }
                else {
                    feeAmt = ArithmeticUtil.doAdd(feeAmt, valTradeFeeDetail.getFeeAmt());
                    // 不随交易费用
                    noFeeAmt = ArithmeticUtil.doAdd(noFeeAmt, valTradeFeeDetail.getFeeAmt());
                    if (DictCodeListExt1.一次性费用类型_印花税.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        stampDuty = ArithmeticUtil.doAdd(stampDuty, valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.印花税账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.印花税账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            stampDutyCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            stampDutyCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        stampDutyCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_经手费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        brokerage = ArithmeticUtil.doAdd(brokerage, valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.经手费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.经手费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            brokerageCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            brokerageCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        brokerageCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_证管费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        certificateFee = ArithmeticUtil.doAdd(certificateFee,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.证管费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.证管费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            certificateFeeCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            certificateFeeCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        certificateFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_结算费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        clearingFee = ArithmeticUtil.doAdd(clearingFee,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.结算费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.结算费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            clearingFeeCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            clearingFeeCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        clearingFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_佣金.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        commission = ArithmeticUtil.doAdd(commission,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.佣金账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.佣金账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            commissionCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            commissionCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        commissionCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_交易手续费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        transactionFee = ArithmeticUtil.doAdd(transactionFee,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.交易手续费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.交易手续费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            transactionFeeCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            transactionFeeCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        transactionFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_结算手续费.getValue().equals(
                            valTradeFeeDetail.getFeeType())) {
                        settlementFee = ArithmeticUtil.doAdd(settlementFee,
                                valTradeFeeDetail.getFeeAmt());
                        // 【出参】交易各类费用.结算手续费账务明细代码 = 调用账务明细代码共通（【出参】交易各类费用.结算手续费账务明细代码,“交易费用类”)
//                        if (StringUtil.isNotEmpty(finprodId)) {
//                            settlementFeeCode = bookCommonLogic.getValBokDetailId(finprodId, null,
//                                finprodType);
//                        }
//                        else {
//                            settlementFeeCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                DictCodeList.金融产品类型_投资组合.getValue());
//                        }
                        settlementFeeCode = bokDetailId;
                    }
                    /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  start**/
                    else if (DictCodeListExt1.一次性费用类型_其他费用.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.其他费用_不随交易支付 += 费用金额
                        otherFee = ArithmeticUtil.doAdd(otherFee, valTradeFeeDetail.getFeeAmt());
                        otherFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_申购费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.申购费_不随交易支付 += 费用金额
                        subscriptionFee = ArithmeticUtil.doAdd(subscriptionFee, valTradeFeeDetail.getFeeAmt());
                        subscriptionFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_赎回费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.赎回费_不随交易支付 += 费用金额
                        redemptionFee = ArithmeticUtil.doAdd(redemptionFee, valTradeFeeDetail.getFeeAmt());
                        redemptionFeeCode = bokDetailId;
                    }
                    else if (DictCodeListExt1.一次性费用类型_认购费.getValue().equals(valTradeFeeDetail.getFeeType())) {
                        // 【出参】交易各类费用.认购费_不随交易支付 += 费用金额
                        offerFee = ArithmeticUtil.doAdd(offerFee, valTradeFeeDetail.getFeeAmt());
                        offerFeeCode = bokDetailId;
                    }
                    /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  end**/
                    /** add by yanya on 20191231 增加一次性通道费处理  start**/
                    else if (DictCodeListExt1.一次性费用类型_通道费.getValue().equals(valTradeFeeDetail.getFeeType())) {

                        //if（传入.标的代码 is not null
                        if(StringUtil.isNotEmpty(finprodId)) {
                            //调用共通标的类共通【通道信息查询】 参数：金融产品代码 <-	传入.标的代码
                            //返回值	-> 变量.通道信息
                            FinChnlInfoDto finChnlInfoDto = finProductCommonService.getChnlInfo(finprodId);
                            //if（变量.通道信息.收取方式 = "与回款无关"
                            if (finChnlInfoDto != null && StringUtil.equals(finChnlInfoDto.getCollectType(), DictCodeListExt2.收取方式_与回款无关.getValue())) {
                                //【出参】交易各类费用.通道费_不随交易支付 += 费用金额
                                chlFee = ArithmeticUtil.doAdd(chlFee, valTradeFeeDetail.getFeeAmt());
                                //【出参】交易各类费用.通道费账务明细代码 = 调用账务明细代码共通（变量.通道信息.金融产品代码,数据字典.金融产品类型.通道)
                                chlFeeCode = bookCommonLogic.getValBokDetailId(finChnlInfoDto.getChlId(), null,
                                        DictCodeListExt1.金融产品类型_通道.getValue(), valTradeFeeDetail.getCcy());
                                //【出参】交易各类费用.通道费业务明细代码 = 变量.通道信息.金融产品代码
                                chlFeeCode.setBusiId(finChnlInfoDto.getChlId());

                            }
                            //else
                            else {
                                //【出参】交易各类费用.交易费用 -= 费用金额
                                feeAmt = ArithmeticUtil.doSub(feeAmt, valTradeFeeDetail.getFeeAmt());
                                //【出参】交易各类费用.不随交易费用 -= 费用金额
                                noFeeAmt = ArithmeticUtil.doSub(noFeeAmt, valTradeFeeDetail.getFeeAmt());
                            }

                        }
                        else {
                            //【出参】交易各类费用.通道费_不随交易支付 += 费用金额
                            chlFee = ArithmeticUtil.doAdd(chlFee, valTradeFeeDetail.getFeeAmt());
                            chlFeeCode = bokDetailId;//根据代码来改动，跟设计有点差别
                             //if 参数.金融产品代码 is not null
                            //if(StringUtil.isNotEmpty(booksetId)) {
//                            if(bokbookset != null) {
//                                //【出参】交易各类费用.通道费账务明细代码 = 调用账务明细代码共通（参数.金融产品代码,变量.金融产品类型)
//                                chlFeeCode = bokDetailId;
////                                chlFeeCode = bookCommonLogic.getValBokDetailId(
////                                        booksetId, null, bokbookset.getAcctSubjectType(), valTradeFeeDetail.getCcy());
//                            }
//                            //else
//                            else {
//                                //【出参】交易各类费用.通道费账务明细代码 = 调用账务明细代码共通（参数.投资组合代码,“投资组合”)
//                                chlFeeCode = bookCommonLogic.getValBokDetailId(groupId, null,
//                                        DictCodeListExt1.金融产品类型_投资组合.getValue(),valTradeFeeDetail.getCcy());
//                            }
                        }

                    }
                    /** add by yanya on 20191231 增加一次性通道费处理  end**/
                }
            }

        }
        /** modify by dongjun on 20181119 start**/
        dealTypesFeeDto.setFeeAmt(ArithmeticUtil.round(feeAmt, NumberEnum.MONEY));
        dealTypesFeeDto.setNoFeeAmt(ArithmeticUtil.round(noFeeAmt, NumberEnum.MONEY));
        dealTypesFeeDto.setStampDuty(ArithmeticUtil.round(stampDuty, NumberEnum.MONEY));
        dealTypesFeeDto.setBrokerage(ArithmeticUtil.round(brokerage, NumberEnum.MONEY));
        dealTypesFeeDto.setCertificateFee(
                ArithmeticUtil.round(certificateFee, NumberEnum.MONEY));
        dealTypesFeeDto.setClearingFee(ArithmeticUtil.round(clearingFee, NumberEnum.MONEY));
        dealTypesFeeDto.setCommission(ArithmeticUtil.round(commission, NumberEnum.MONEY));
        dealTypesFeeDto.setTransactionFee(
                ArithmeticUtil.round(transactionFee, NumberEnum.MONEY));
        dealTypesFeeDto.setSettlementFee(
                ArithmeticUtil.round(settlementFee, NumberEnum.MONEY));
        dealTypesFeeDto.setStampDutyCode(stampDutyCode);
        dealTypesFeeDto.setBrokerageCode(brokerageCode);
        dealTypesFeeDto.setCertificateFeeCode(certificateFeeCode);
        dealTypesFeeDto.setClearingFeeCode(clearingFeeCode);
        dealTypesFeeDto.setCommissionCode(commissionCode);
        dealTypesFeeDto.setTransactionFeeCode(transactionFeeCode);
        dealTypesFeeDto.setSettlementFeeCode(settlementFeeCode);
        dealTypesFeeDto.setChlFee(chlFee);
        dealTypesFeeDto.setChlFeeCode(chlFeeCode);
        dealTypesFeeDto.setChlGlFee(chlGlFee);
        dealTypesFeeDto.setChlGlFeeCode(chlGlFeeCode);
        dealTypesFeeDto.setChlXsFee(chlXsFee);
        dealTypesFeeDto.setChlXsFeeCode(chlXsFeeCode);
        dealTypesFeeDto.setChlTgFee(chlTgFee);
        dealTypesFeeDto.setChlTgFeeCode(chlTgFeeCode);

        dealTypesFeeDto.setStampDutyForDealPay(
                ArithmeticUtil.round(stampDutyForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setBrokerageForDealPay(
                ArithmeticUtil.round(brokerageForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setCertificateFeeForDealPay(
                ArithmeticUtil.round(certificateFeeForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setClearingFeeForDealPay(
                ArithmeticUtil.round(clearingFeeForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setCommissionForDealPay(
                ArithmeticUtil.round(commissionForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setTransactionFeeForDealPay(
                ArithmeticUtil.round(transactionFeeForDealPay, NumberEnum.MONEY));
        dealTypesFeeDto.setSettlementFeeForDealPay(
                ArithmeticUtil.round(settlementFeeForDealPay, NumberEnum.MONEY));

        dealTypesFeeDto.setStampDutyCodeForDealPay(stampDutyCodeForDealPay);
        dealTypesFeeDto.setBrokerageCodeForDealPay(brokerageCodeForDealPay);
        dealTypesFeeDto.setCertificateFeeCodeForDealPay(certificateFeeCodeForDealPay);
        dealTypesFeeDto.setClearingFeeCodeForDealPay(clearingFeeCodeForDealPay);
        dealTypesFeeDto.setCommissionCodeForDealPay(commissionCodeForDealPay);
        dealTypesFeeDto.setTransactionFeeCodeForDealPay(transactionFeeCodeForDealPay);
        dealTypesFeeDto.setSettlementFeeCodeForDealPay(settlementFeeCodeForDealPay);
        /** modify by dongjun on 20181119 end**/
        /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  start**/
        dealTypesFeeDto.setOtherFee(ArithmeticUtil.round(otherFee, NumberEnum.MONEY));//其他费用
        dealTypesFeeDto.setSubscriptionFee(ArithmeticUtil.round(subscriptionFee, NumberEnum.MONEY));//申购费
        dealTypesFeeDto.setRedemptionFee(ArithmeticUtil.round(redemptionFee, NumberEnum.MONEY));//赎回费
        dealTypesFeeDto.setOfferFee(ArithmeticUtil.round(offerFee, NumberEnum.MONEY));//认购费
        dealTypesFeeDto.setOtherFeeCode(otherFeeCode);//其他费用账务明细代码
        dealTypesFeeDto.setSubscriptionFeeCode(subscriptionFeeCode);//申购费账务明细代码
        dealTypesFeeDto.setRedemptionFeeCode(redemptionFeeCode);//赎回费账务明细代码
        dealTypesFeeDto.setOfferFeeCode(offerFeeCode);//认购费账务明细代码
        dealTypesFeeDto.setOtherFeeForDealPay(ArithmeticUtil.round(otherFeeForDealPay, NumberEnum.MONEY));//其他费用_随交易支付
        dealTypesFeeDto.setSubscriptionFeeForDealPay(ArithmeticUtil.round(subscriptionFeeForDealPay, NumberEnum.MONEY));//申购费_随交易支付
        dealTypesFeeDto.setRedemptionFeeForDealPay(ArithmeticUtil.round(redemptionFeeForDealPay, NumberEnum.MONEY));//赎回费_随交易支付
        dealTypesFeeDto.setOfferFeeForDealPay(ArithmeticUtil.round(offerFeeForDealPay, NumberEnum.MONEY));//认购费_随交易支付
        dealTypesFeeDto.setOtherFeeCodeForDealPay(otherFeeCodeForDealPay);//随交易支付_其他费用账务明细代码
        dealTypesFeeDto.setSubscriptionFeeCodeForDealPay(subscriptionFeeCodeForDealPay);//随交易支付_申购费账务明细代码
        dealTypesFeeDto.setRedemptionFeeCodeForDealPay(redemptionFeeCodeForDealPay);//随交易支付_赎回费账务明细代码
        dealTypesFeeDto.setOfferFeeCodeForDealPay(offerFeeCodeForDealPay);//随交易支付_认购费账务明细代码
        /** add by yanya on 20191008 增加对其他费用   申购费 赎回费 认购费的处理  end**/
        return dealTypesFeeDto;
    }
    
    /**
     * 2020年1月9日 魏鹏 新增乘法运算
     * 共通算法中的数组连乘，若为空，则会赋值为1，这里改为若有一条为空，则返回0
     * @param values 运算数组
     * @return 连乘结果
     * @see
     * @since 1.0
     */
    public BigDecimal doMultiply(BigDecimal... values){
        if (values != null && values.length > 0) {
            for(BigDecimal v : values) {
                // 若有一条为空，按0处理，乘法最终结果也是0，直接返回
                if(v == null) {
                    return BigDecimal.ZERO;
                }
            }
            return ArithmeticUtil.multiply(values);
        }
        return null;
    }
    
    /**
     * 2020年1月9日 魏鹏 新增乘法运算
     * 共通算法中的数组连乘，若为空，则会赋值为1，这里改为若有一条为空，则返回0
     * @param values 运算数组
     * @return 连乘结果
     * @see
     * @since 1.0
     */
    public BigDecimal doMultiply(int scale, RoundingMode roundingMode, BigDecimal... values){
        if (values != null && values.length > 0) {
            for(BigDecimal v : values) {
                // 若有一条为空，按0处理，乘法最终结果也是0，直接返回
                if(v == null) {
                    return BigDecimal.ZERO;
                }
            }
            return ArithmeticUtil.multiply(scale, roundingMode, values);
        }
        return null;
    }


    public boolean isTradeDealAccount2(String booksetId,Date tradeDate,List<String> pslist) {

        boolean flag = false;
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("BOOKSET_ID", booksetId);
        map.put("TRADE_DATE", tradeDate);
        map.put("pslist", pslist);
        map.put("tradeStatus","01");
        int count = calculationCommonMapper.tradeDealAccountCount(map);
        if(count>0){
            flag = true;
        }
        return flag;
    }
}

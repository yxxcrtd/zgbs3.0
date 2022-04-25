/*
 * 文件名：AccountingCommonService.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.accounting.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.dao.BokStatBokPositionMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.dao.AccountingCommonMapper;
import com.joyintech.tams.valuation.batch.common.accounting.dao.ValBokSubjectInfoMapper;
import com.joyintech.tams.valuation.batch.common.accounting.entity.*;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashPlan;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import groovy.lang.Tuple2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 账务类共通方法
 *
 * @author wangzhiang
 * @version 1.0 2018年5月31日
 * @see AccountingCommonService
 * @since 1.0
 */
@Service
public class AccountingCommonService {

    /**
     * 数据库数据获取
     */
    @Autowired
    private AccountingCommonMapper accountingCommonMapper;

    /**
     * 余额表生成处理
     */
    @Autowired
    private ValBokSubjectInfoMapper valBokSubjectInfoMapper;

    /**
     * valStatBokPositionMapper
     */
    @Autowired
    private BokStatBokPositionMapper valStatBokPositionMapper;

    @Autowired
    BokBooksetMapper bokBooksetMapper;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * 全局变量：底层科目级别
     */
    private final Long BOTTOM_SUBJECT_LEVEL = 99L;


    /**
     * 公用处理：余额方向处理_期待方向
     *
     * @param expBalanceDirect 期待余额方向
     * @param actBalanceDirect 实际余额方向
     * @param actBalance 实际余额
     * @param actnum 实际数量
     * @return 余额、数量
     * @see
     * @since 1.0
     */
    public Map<String, Object> balanceDirectDealExpectDirection(String expBalanceDirect,
                                                                String actBalanceDirect,
                                                                BigDecimal actBalance,
                                                                BigDecimal actnum) {
        // 余额
        BigDecimal balance = BigDecimal.ZERO;
        // 数量
        BigDecimal num = BigDecimal.ZERO;
        // 空指针排除
        actBalance = actBalance == null ? BigDecimal.ZERO : actBalance;
        actnum = actnum == null ? BigDecimal.ZERO : actnum;

        if (expBalanceDirect.equals(DictCodeList.余额方向_借方.getValue())) {
            if (actBalanceDirect.equals(DictCodeList.余额方向_借方.getValue())) {
                balance = actBalance;
                num = actnum;
            }
            if (actBalanceDirect.equals(DictCodeList.余额方向_贷方.getValue())) {
                balance = ArithmeticUtil.multiply(actBalance, new BigDecimal(-1));
                num = ArithmeticUtil.multiply(actnum, new BigDecimal(-1));
            }
        }

        if (expBalanceDirect.equals(DictCodeList.余额方向_贷方.getValue())) {
            if (actBalanceDirect.equals(DictCodeList.余额方向_贷方.getValue())) {
                balance = actBalance;
                num = actnum;
            }
            if (actBalanceDirect.equals(DictCodeList.余额方向_借方.getValue())) {
                balance = ArithmeticUtil.multiply(actBalance, new BigDecimal(-1));
                num = ArithmeticUtil.multiply(actnum, new BigDecimal(-1));
            }
        }

        // 定义返回值
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("balance", balance);// 余额
        params.put("num", num);// 数量

        return params;
    }

    /**
     * 公用处理：余额方向处理_余额
     *
     * @param subBalanceDirect 科目余额方向
     * @param actBalanceDirect 实际余额方向
     * @param actBalance 实际余额
     * @param actnum 实际数量
     * @param actBalance 实际本币余额
     * @return 余额、数量、余额方向、本币余额
     * @see
     * @since 1.0
     */
    public Map<String, Object> balanceDirectDealBalance(String subBalanceDirect,
                                                        String actBalanceDirect,
                                                        BigDecimal actBalance, BigDecimal actnum,
                                                        BigDecimal bactBalance) {
        // 原币余额
        BigDecimal balance = BigDecimal.ZERO;
        // 本币余额
        BigDecimal benbalance = BigDecimal.ZERO;
        // 数量
        BigDecimal num = BigDecimal.ZERO;
        // 余额方向
        String balDirect = "";
        // 空指针排除
        actBalance = actBalance == null ? BigDecimal.ZERO : actBalance;
        bactBalance = bactBalance == null ? BigDecimal.ZERO : bactBalance;
        actnum = actnum == null ? BigDecimal.ZERO : actnum;

        if (!subBalanceDirect.equals(DictCodeList.余额方向_借贷双方.getValue())) {
            if (actBalanceDirect.equals(subBalanceDirect)) {
                balance = actBalance;
                benbalance = bactBalance;
                num = actnum;
            }
            else {
                balance = ArithmeticUtil.multiply(actBalance, new BigDecimal(-1));
                benbalance = ArithmeticUtil.multiply(bactBalance, new BigDecimal(-1));
                num = ArithmeticUtil.multiply(actnum, new BigDecimal(-1));
            }
            balDirect = subBalanceDirect;
        }
        else {
            if (actBalanceDirect.equals(DictCodeList.余额方向_贷方.getValue())) {
                if (actBalance.compareTo(BigDecimal.ZERO) < 0) {
                    balance = ArithmeticUtil.multiply(actBalance, new BigDecimal(-1));
                    benbalance = ArithmeticUtil.multiply(bactBalance, new BigDecimal(-1));
                    num = ArithmeticUtil.multiply(actnum, new BigDecimal(-1));
                    // 余额方向 = "借方"
                    balDirect = DictCodeList.余额方向_借方.getValue();
                }
                else {
                    balance = actBalance;
                    benbalance = bactBalance;
                    num = actnum;
                    // 余额方向 = "贷方"
                    balDirect = DictCodeList.余额方向_贷方.getValue();
                }
                // balDirect = actBalanceDirect;

            }
            if (actBalanceDirect.equals(DictCodeList.余额方向_借方.getValue())) {
                if (actBalance.compareTo(BigDecimal.ZERO) < 0) {
                    balance = ArithmeticUtil.multiply(actBalance, new BigDecimal(-1));
                    benbalance = ArithmeticUtil.multiply(bactBalance, new BigDecimal(-1));
                    num = ArithmeticUtil.multiply(actnum, new BigDecimal(-1));
                    // 余额方向 = "贷方"
                    balDirect = DictCodeList.余额方向_贷方.getValue();
                }
                else {
                    balance = actBalance;
                    benbalance = bactBalance;
                    num = actnum;
                    // 余额方向 = "借方"
                    balDirect = DictCodeList.余额方向_借方.getValue();
                }
                // balDirect = actBalanceDirect;

            }
        }

        // 定义返回值
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("balance", balance);// 原币余额
        params.put("benbalance", benbalance);// 本币余额
        params.put("num", num);// 数量
        params.put("balDirect", balDirect);// 余额方向
        return params;
    }

    /**
     * 公用处理：余额方向处理_发生额加余额
     * added by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 start
     *
     * @param subBalanceDirect 科目余额方向
     * @param actBalanceDirect 实际余额方向
     * @param actBalance 实际余额
     * @param actnum 实际数量
     * @param accrualDirect 发生额方向
     * @param accrual 原币发生额
     * @param accnum 发生数量
     * @param actBenBalance 本币实际余额
     * @param benaccrual 本币发生额
     * @return 余额、数量、余额方向
     * @see
     * @since 1.0
     */
    public Map<String, Object> balanceDirectDealAccrualAddBalance(String subBalanceDirect,
                                                                  String actBalanceDirect,
                                                                  BigDecimal actBalance,
                                                                  BigDecimal actnum,
                                                                  String accrualDirect,
                                                                  BigDecimal accrual,
                                                                  BigDecimal accnum,
                                                                  BigDecimal actBenBalance,
                                                                  BigDecimal benaccrual
    ) {
        return this.balanceDirectDealAccrualAddBalance(subBalanceDirect, actBalanceDirect, actBalance, actnum,
        accrualDirect, accrual, accnum, actBenBalance, benaccrual, DictCodeList.是否_否.getValue());
    }

    /**
     * 公用处理：余额方向处理_发生额加余额_支持本位币判断方向
     *
     * @param subBalanceDirect 科目余额方向
     * @param actBalanceDirect 实际余额方向
     * @param actBalance 实际余额
     * @param actnum 实际数量
     * @param accrualDirect 发生额方向
     * @param accrual 原币发生额
     * @param accnum 发生数量
     * @param actBenBalance 本币实际余额
     * @param benaccrual 本币发生额
     * @param bflag 是否使用本位币判断余额方向(用于科目余额汇总)
     * @return 余额、数量、余额方向
     * @see
     * @since 1.0
     */
    public Map<String, Object> balanceDirectDealAccrualAddBalance(String subBalanceDirect,
                                                                  String actBalanceDirect,
                                                                  BigDecimal actBalance,
                                                                  BigDecimal actnum,
                                                                  String accrualDirect,
                                                                  BigDecimal accrual,
                                                                  BigDecimal accnum,
                                                                  BigDecimal actBenBalance,
                                                                  BigDecimal benaccrual,
                                                                  String bflag
    ) {
        // 原币余额
        BigDecimal balance = BigDecimal.ZERO;
        // 本币余额
        BigDecimal benbalance = BigDecimal.ZERO;
        // 数量
        BigDecimal num = BigDecimal.ZERO;
        // 余额方向
        String balDirect = "";
        // 空指针排除
        actBalance = actBalance == null ? BigDecimal.ZERO : actBalance;
        actnum = actnum == null ? BigDecimal.ZERO : actnum;
        accrual = accrual == null ? BigDecimal.ZERO : accrual;
        accnum = accnum == null ? BigDecimal.ZERO : accnum;
        actBenBalance = actBenBalance == null ? BigDecimal.ZERO : actBenBalance;
        benaccrual = benaccrual == null ? BigDecimal.ZERO : benaccrual;

        if (!subBalanceDirect.equals(DictCodeList.余额方向_借贷双方.getValue())) {
            if (actBalanceDirect.equals(subBalanceDirect)) {
                balance = actBalance;
                benbalance = actBenBalance;
                num = actnum;
            }
            else {
                balance = ArithmeticUtil.multiply(actBalance, new BigDecimal(-1));
                benbalance = ArithmeticUtil.multiply(actBenBalance, new BigDecimal(-1));
                num = ArithmeticUtil.multiply(actnum, new BigDecimal(-1));
            }
            balDirect = subBalanceDirect;
            if (balDirect.equals(accrualDirect)) {
                balance = ArithmeticUtil.doAdd(balance, accrual);
                benbalance = ArithmeticUtil.doAdd(benbalance, benaccrual);
                num = ArithmeticUtil.doAdd(num, accnum);
            }
            else {
                balance = ArithmeticUtil.doSub(balance, accrual);
                benbalance = ArithmeticUtil.doSub(benbalance, benaccrual);
                num = ArithmeticUtil.doSub(num, accnum);
            }
        }
        else {
            // 参数.实际余额方向 = 参数.发生额方向
            if (actBalanceDirect.equals(accrualDirect)) {
                // 原币余额 = 参数.实际余额 + 参数.发生额
                balance = ArithmeticUtil.doAdd(actBalance, accrual);
                // 本币余额 = 参数.实际余额 + 参数.发生额
                benbalance = ArithmeticUtil.doAdd(actBenBalance, benaccrual);
                // 数量余额 = 参数.实际数量 + 参数.发生数量
                num = ArithmeticUtil.doAdd(actnum, accnum);
                // 余额方向 = 参数.实际余额方向
                balDirect = actBalanceDirect;
            }
            // added by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 start
            else if (DictCodeList.是否_是.getValue().equals(bflag)) {
                // 使用本位币方向判断
                if (actBenBalance.compareTo(benaccrual) >= 0) {
                    balance = ArithmeticUtil.doSub(actBalance, accrual);
                    benbalance = ArithmeticUtil.doSub(actBenBalance, benaccrual);
                    num = ArithmeticUtil.doSub(actnum, accnum);
                    balDirect = actBalanceDirect;
                }
            else {
                    balance = ArithmeticUtil.doSub(accrual, actBalance);
                    benbalance = ArithmeticUtil.doSub(benaccrual, actBenBalance);
                    num = ArithmeticUtil.doSub(accnum, actnum);
                    balDirect = accrualDirect;
                }
            // added by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 end
            } else {
                if (actBalance.compareTo(accrual) >= 0) {
                    balance = ArithmeticUtil.doSub(actBalance, accrual);
                    benbalance = ArithmeticUtil.doSub(actBenBalance, benaccrual);
                    num = ArithmeticUtil.doSub(actnum, accnum);
                    balDirect = actBalanceDirect;
                }
                else {
                    balance = ArithmeticUtil.doSub(accrual, actBalance);
                    benbalance = ArithmeticUtil.doSub(benaccrual, actBenBalance);
                    num = ArithmeticUtil.doSub(accnum, actnum);
                    balDirect = accrualDirect;
                }

            }
        }

        // 定义返回值
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("balance", balance);// 原币余额
        params.put("benbalance", benbalance);// 本币余额
        params.put("num", num);// 数量
        params.put("balDirect", balDirect);// 余额方向
        return params;
    }

    /**
     * 科目余额查询 - 期待方向
     *
     * @param searchDto 科目余额查询参数Dto
     * @param expBalanceDirect 期待余额方向
     * @return List<SubjectBalance> 科目余额信息List
     * @see
     * @since 1.0
     */
    public List<SubjectBalance> getSubjectBalanceExpectDirection(SubjectBalanceSearchDto searchDto,
                                                                 String expBalanceDirect) {

        // 调用共通方法：科目余额查询
        List<SubjectBalance> subjectBalanceList = this.getSubjectLatestBalance(searchDto);

        // 循环科目余额，更新相关信息
        for (SubjectBalance vo : subjectBalanceList) {
            // 获取余额方向
            String actBalanceDirect = vo.getBalFlagHappenDate();
            // 获取科目余额
            BigDecimal actBalance = (vo.getBalance() != null) ? vo.getBalance() : BigDecimal.ZERO;
            // 获取本币科目余额
            BigDecimal bactBalance = (vo.getbAmt() != null) ? vo.getbAmt() : BigDecimal.ZERO;
            // 获取数量余额
            BigDecimal actnum = vo.getNum();

            // 调用共通方法：余额方向处理_期待方向
            Map<String, Object> directionInfo = this.balanceDirectDealExpectDirection(
                    expBalanceDirect, actBalanceDirect, actBalance, actnum);

            // 调用共通方法：余额方向处理_期待方向
            Map<String, Object> directionInfoben = this.balanceDirectDealExpectDirection(
                    expBalanceDirect, actBalanceDirect, bactBalance, actnum);
            // 获取原币返回值余额
            BigDecimal balance = directionInfo.get(
                    "balance") == null ? BigDecimal.ZERO : (BigDecimal)directionInfo.get("balance");
            // 获取本币返回值余额
            BigDecimal benbalance = directionInfoben.get(
                    "balance") == null ? BigDecimal.ZERO : (BigDecimal)directionInfoben.get("balance");
            // 获取返回值数量
            BigDecimal num = directionInfo.get(
                    "num") == null ? BigDecimal.ZERO : (BigDecimal)directionInfo.get("num");

            // 塞入科目余额信息中
            vo.setBalance(balance);// 原币科目余额
            vo.setbAmt(benbalance);// 本币科目余额
            vo.setNum(num);// 数量余额
            vo.setBalFlagHappenDate(expBalanceDirect);// 余额方向
        }

        return subjectBalanceList;
    }
    /**
     * 公用处理：余额表生成处理
     *
     * @param booksetId 账套代码
     * @param bookDate 入账日期
     * @param earlyDate 期初日期
     * @param endDate 期末日期
     * @param currency 本位币
     * @return list
     * @see
     * @since 1.0
     */
    public List<ValBokBalanceDto> generateSchedulInfo(String booksetId, Date bookDate,
                                                      Date earlyDate, Date endDate,
                                                      String currency) {
        List<ValBokBalanceDto> list = new ArrayList<ValBokBalanceDto>();

        // // 0数据准备
        // List<ValBokSubject> listSubject =
        // valBokSubjectInfoMapper.findValBokSubjectInfoByBooksetId(
        // booksetId);
        // // 定义变量的科目map
        // Map<String, ValBokSubject> subjectMap = new HashMap<String, ValBokSubject>();

        // 1、原币种金额处理
        // 1.1、期初余额查询
        Map<String, List<ValBokBalanceDto>> mapEarly = valBokSubjectInfoMapper.findEarlyBalace(
                earlyDate, booksetId).stream().collect(
                Collectors.groupingBy(ValBokBalanceDto::getSubjectNo));
        // 添加余额明细
        // list.addAll(listEarly);

        // 1.2期末余额查询
        Map<String, List<ValBokBalanceDto>> mapEnd = valBokSubjectInfoMapper.findEarlyBalace(
                endDate, booksetId).stream().collect(
                Collectors.groupingBy(ValBokBalanceDto::getSubjectNo));
        // list.addAll(listEnd);
        HashSet<String> subjectNoSet = new HashSet<>();
        subjectNoSet.addAll(mapEarly.keySet());
        subjectNoSet.addAll(mapEnd.keySet());
        for (String subjectNo : subjectNoSet) {
            boolean earlyExists = mapEarly.containsKey(subjectNo);
            boolean endExists = mapEnd.containsKey(subjectNo);
            ValBokBalanceDto dto = new ValBokBalanceDto();
            if (earlyExists) {
                ValBokBalanceDto earlyDto = mapEarly.get(subjectNo).get(0);
                dto.setSubjectNo(earlyDto.getSubjectNo());
                dto.setSubjectName(earlyDto.getSubjectName());
                dto.setoCcy(earlyDto.getoCcy());
                dto.setoOpeningCdFlag(earlyDto.getBalFlag());
                dto.setoOpeningBalance(earlyDto.getoAmt());
                dto.setbOpeningCdFlag(earlyDto.getBalFlag());
                dto.setbEndingCdFlag(dto.getoOpeningCdFlag());
//                BigDecimal rate = calculationCommonService.queryExchangeRate(earlyDto.getoCcy(),
//                        currency, bookDate);
                if (endExists) {
                    ValBokBalanceDto endDto = mapEnd.get(subjectNo).get(0);
                    dto.setoHappenAmtD(
                            ArithmeticUtil.doSub(endDto.getoDAmt(), earlyDto.getoDAmt()));
                    dto.setoHappenAmtC(
                            ArithmeticUtil.doSub(endDto.getoCAmt(), earlyDto.getoCAmt()));
                    dto.setoEndingBalance(endDto.getoAmt());
                    dto.setoEndingCdFlag(dto.getoOpeningCdFlag());
                    // 本位币逻辑与设计一致
                    dto.setbHappenAmtD(
                            ArithmeticUtil.doSub(endDto.getbDAmt(), earlyDto.getbDAmt()));
                    dto.setbHappenAmtC(
                            ArithmeticUtil.doSub(endDto.getbCAmt(), earlyDto.getbCAmt()));
                    dto.setbEndingBalance(endDto.getbAmt());
                }
                else {
                    dto.setoHappenAmtD(earlyDto.getoDAmt().negate());
                    dto.setoHappenAmtC(earlyDto.getoCAmt().negate());
                    dto.setoEndingBalance(BigDecimal.ZERO);
                    dto.setoEndingCdFlag(earlyDto.getBalFlag());
                    // 本位币逻辑与设计一致
                    dto.setbHappenAmtD(earlyDto.getbDAmt().negate());
                    dto.setbHappenAmtC(earlyDto.getbCAmt().negate());
                    dto.setbEndingBalance(BigDecimal.ZERO);
                }
//                dto.setbOpeningCdFlag(dto.getoOpeningCdFlag());
//                dto.setbOpeningBalance(ArithmeticUtil.round(
//                        ArithmeticUtil.multiply(dto.getoOpeningBalance(), rate), NumberEnum.MONEY));
//                dto.setbHappenAmtC(ArithmeticUtil.round(
//                        ArithmeticUtil.multiply(dto.getoHappenAmtC(), rate), NumberEnum.MONEY));
//                dto.setbHappenAmtD(ArithmeticUtil.round(
//                        ArithmeticUtil.multiply(dto.getoHappenAmtD(), rate), NumberEnum.MONEY));
//                dto.setbEndingCdFlag(dto.getoEndingCdFlag());
//                dto.setbEndingBalance(ArithmeticUtil.round(
//                        ArithmeticUtil.multiply(dto.getoEndingBalance(), rate), NumberEnum.MONEY));
            }
            else {
                // 期初为空时
                ValBokBalanceDto endDto = mapEnd.get(subjectNo).get(0);
                dto.setSubjectNo(endDto.getSubjectNo());
                dto.setSubjectName(endDto.getSubjectName());
                dto.setoCcy(endDto.getoCcy());
                dto.setoOpeningCdFlag(endDto.getBalFlag());
                dto.setoOpeningBalance(BigDecimal.ZERO);
                dto.setoHappenAmtD(endDto.getoDAmt());
                dto.setoHappenAmtC(endDto.getoCAmt());
                dto.setoEndingCdFlag(endDto.getBalFlag());
                dto.setoEndingBalance(endDto.getoAmt());
//                BigDecimal rate = calculationCommonService.queryExchangeRate(endDto.getoCcy(),
//                        currency, bookDate);
                dto.setbOpeningCdFlag(dto.getoOpeningCdFlag());
//                dto.setbOpeningBalance(endDto.getbOpeningBalance());
//                dto.setbHappenAmtC(endDto.getbHappenAmtC());
//                dto.setbHappenAmtD(endDto.getbHappenAmtD());
//                dto.setbEndingCdFlag(endDto.getbEndingCdFlag());
//                dto.setbEndingBalance(endDto.getbEndingBalance());
                dto.setbOpeningBalance(BigDecimal.ZERO);
                dto.setbHappenAmtD(endDto.getbDAmt());
                dto.setbHappenAmtC(endDto.getbCAmt());
                dto.setbEndingCdFlag(endDto.getBalFlag());
                dto.setbEndingBalance(endDto.getbAmt());
            }
            list.add(dto);
        }
        // // 循环处理LIST<余额明细>
        // for (ValBokBalance item : list) {
        // // 调用共通方法【计算类共通方法设计-汇率折算】--根据原币余额换算本位币余额
        // Map<String, BigDecimal> balanceMap = new HashMap<>();
        // balanceMap.put(item.getoCcy(), item.getoAmt());
        // // 余额明细.综合本位币金额 = 返回值.金额
        // BigDecimal exchangeAmt = calculationCommonService.calcExchangeAmt(balanceMap, currency,
        // endDate);
        // // 将综合本位币金额记录插入到本位币余额
        // item.setbAmt(exchangeAmt);
        // // 更新list
        // list.set(list.indexOf(item), item);
        // }
        // 重新排序 order by 科目余额.科目级别，科目余额.科目号
        Collections.sort(list, new Comparator<ValBokBalanceDto>() {

            @Override
            public int compare(ValBokBalanceDto o1, ValBokBalanceDto o2) {
                // 科目级别比较大小
                // int levelSub = o1.getSubjectLevel().compareTo(o2.getSubjectLevel());
                // 科目级别相同时，比较科目号大小
                return /* (levelSub != 0) ? levelSub : */o1.getSubjectNo().compareTo(
                        o2.getSubjectNo());
            }
        });

        return list;
    }

    /**
     * 科目余额方向查询
     *
     * @param subjectNo 科目号
     * @param booksetId 账套代码
     * @return 余额方向
     * @see
     * @since 1.0
     */
    public String getBalFlag(String subjectNo, String booksetId) {
        return accountingCommonMapper.getBalFlag(subjectNo, booksetId);
    }

    /**
     * 科目余额查询共通--不需要考虑分支特殊处理
     *
     * @return List<SubjectBalance> 结果集
     * @param searchDto 科目余额查询参数Dto
     * @see
     * @since 1.0
     */
    public List<SubjectBalance> getSubjectLatestBalance(SubjectBalanceSearchDto searchDto) {
        // 定义传递参数
        // 若分支序号为空，则查询默认的分支序号
        if (searchDto.getBranch() == null) {
            searchDto.setBranch(BookConstant.DEFALUT_BRANCH); // 默认分支号
        }
        List<SubjectBalance> list = accountingCommonMapper.queryCurrentLatestBalance(searchDto);
        return list;
    }

    /**
     * 科目余额汇总查询 （相当于根据科目类别对所有的科目余额进行余额和数量的总处理）
     *
     * @return resultMap 余额Map(币种，科目余额)
     * @param searchDto 科目余额查询参数Dto
     * @param expectedBalFalg --期待余额方向
     * @param bccySum 是否原币汇总 Y:是  N:否
     * @see
     * @since 1.0
     */

    public Map<String, BigDecimal> getSubjectBalanceCollection(SubjectBalanceSearchDto searchDto,
                                                               String expectedBalFalg,String bccySum) {
        return getSubjectBalanceCollection(searchDto,expectedBalFalg, bccySum,null);
    }

    /**
     * 科目余额汇总查询 （相当于根据科目类别对所有的科目余额进行余额和数量的总处理）
     *
     * @return resultMap 余额Map(币种，科目余额)
     * @param searchDto 科目余额查询参数Dto
     * @param expectedBalFalg --期待余额方向
     * @param bccySum 是否原币汇总 Y:是  N:否
     * @see
     * @since 1.0
     */
    public Map<String, BigDecimal> getSubjectBalanceCollection(SubjectBalanceSearchDto searchDto,
                                                               String expectedBalFalg,String bccySum,Long taskPri) {
        // 查询账务明细当前表
        searchDto.setBranchHandleFlag("01");
        searchDto.setTaskPri(taskPri);// 批处理优先级设置为null
        List<SubjectBalance> list = this.getSubjectLatestBalance(searchDto);

        // 定义 出参.余额Map
        Map<String, BigDecimal> resultMap = new HashMap<>();

        //如果传值为NULL，默认汇总币种为原币
        bccySum = bccySum == null ? DictCodeList.是否_是.getValue() : bccySum;
        // list1中不存在同一key后面不同值的情况，可直接放入map中
        for (SubjectBalance item : list) {
            //定义 变量.汇总币种 =科目余额DTO.原币
            String sumCcy =item.getOCcy();
            //IF '否“ == 参数.是否原币汇总
            if(DictCodeList.是否_否.getValue().equals(bccySum)){
                //变量.汇总币种 = 科目余额DTO.本位币
                sumCcy = item.getbCcy();
            }

            // 定义 变量.科目余额 = 出参.余额Map.get(币种) != null
            BigDecimal subjectBalance = (resultMap.get(sumCcy) != null) ? resultMap.get(
                    sumCcy) : null;
            // 定义 变量.方向是否一致flag，true一致，false不一致
            boolean balFlag = item.getBalFlagHappenDate().equals(expectedBalFalg);
            // if（变量.科目余额 != null)
            if (subjectBalance != null) {
                if(DictCodeList.是否_否.getValue().equals(bccySum)){
                    // 科目余额 = 科目余额 + (余额方向 == 参数.余额方向 ? 科目余额 ：本币科目余额 * -1）
                    subjectBalance = balFlag ? ArithmeticUtil.doAdd(subjectBalance,
                            item.getbAmt()) : ArithmeticUtil.doAdd(subjectBalance,
                            ArithmeticUtil.multiply(item.getbAmt(), new BigDecimal("-1")));
                }else{
                    // 科目余额 = 科目余额 + (余额方向 == 参数.余额方向 ? 科目余额 ： 科目余额 * -1）
                    subjectBalance = balFlag ? ArithmeticUtil.doAdd(subjectBalance,
                            item.getBalance()) : ArithmeticUtil.doAdd(subjectBalance,
                            ArithmeticUtil.multiply(item.getBalance(), new BigDecimal("-1")));

                }
            }
            else {
            	if(DictCodeList.是否_否.getValue().equals(bccySum)){
            		// 科目余额 = 科目余额 + (余额方向 == 参数.余额方向 ? 科目余额 ： 本币科目余额 * -1）
                    subjectBalance = balFlag ? item.getbAmt() : ArithmeticUtil.multiply(
                            item.getbAmt(), new BigDecimal("-1"));
            	}
            	else{
            		// 科目余额 = 科目余额 + (余额方向 == 参数.余额方向 ? 科目余额 ： 科目余额 * -1）
                    subjectBalance = balFlag ? item.getBalance() : ArithmeticUtil.multiply(
                            item.getBalance(), new BigDecimal("-1"));
            	}
            }

            // 更新 resultMap
            resultMap.put(sumCcy, subjectBalance);
        }
        // 返回 出参.余额Map
        return resultMap;
    }

    /**
     * 公用处理：科目余额汇总处理
     *
     * @param balance 子余额明细
     * @param subjectLevel subjectLevel
     * @param subjectNo 科目号
     * @param balanceMap 科目余额map
     * @param subjectMap 科目map
     * @param isleaf 是否叶子节点：Y/N
     * @see
     * @since 1.0
     */
    public void collectSubjectBalance(SubjectBalance balance, String subjectNo, Long subjectLevel,
                                      Map<String, SubjectBalance> balanceMap,
                                      Map<String, BokSubject> subjectMap, String isleaf) {
        // 定义 变量.科目信息
        BokSubject subject = new BokSubject();
        if (balance == null) {
            return;
        }
        String fSubjectId;
        // if（入参.子余额明细.科目级别 = 全局变量【底层科目级别】）
        if (this.BOTTOM_SUBJECT_LEVEL.equals(subjectLevel)) {
            // 变量.科目信息 = 入参.科目MAP.get(入参.子余额明细.上级科目号)
            subject = subjectMap.get(balance.getFsubjectId());
            // 变量.科目信息.上级科目号 = 入参.子余额明细.上级科目号
            fSubjectId = balance.getFsubjectId();
        }
        else {
            subject = subjectMap.get(subjectNo);
            fSubjectId = subject.getFsubjectId();
        }
        //定义变量：是否统计原币科目余额：如果是损益科目或者非叶子节点科目的话则不统计原币科目余额
        boolean isCountBBalance = true;
        if(subject.getSubjectType().equals("05")||DictCodeList.是否_否.getValue().equals(isleaf)){
            isCountBBalance = false;
        }

        // if(参数.是否叶子节点 = "是" && 入参.子余额明细.场景代码 in
        // && 入参.子余额明细.场景代码 in ("产品-已实现损益结转",
        // "产品-本期利润已实现结转",
        // "产品-未实现损益结转",
        // "产品-未分配利润结转",
        // "产品-损益平准金结转",
        // "产品-本期利润未实现结转")

        if (DictCodeList.是否_是.getValue().equals(isleaf)
                && (DictCodeListTableId.会计场景代码_产品_已实现损益结转.getValue().equals(balance.getTableId())
                || DictCodeListTableId.会计场景代码_产品_未实现损益结转.getValue().equals(balance.getTableId())
                || DictCodeListTableId.会计场景代码_产品_未分配利润结转.getValue().equals(balance.getTableId())
                || DictCodeListTableId.会计场景代码_产品_损益平准金结转.getValue().equals(balance.getTableId())
                || DictCodeListTableId.会计场景代码_产品_本期利润未实现结转.getValue().equals(balance.getTableId())
                || DictCodeListTableId.会计场景代码_产品_本期利润已实现结转.getValue().equals(balance.getTableId()))) {
            // 入参.子余额明细.结转金额 = 子余额明细.科目余额
            balance.setoCcyKnotsAmt(balance.getBalance());
            // 入参.本位币子余额明细.结转金额 = 子余额明细.本位币科目余额
            balance.setbCoAmt(balance.getbAmt());
        }
        // if(参数.是否叶子节点 = "是"
        // && 入参.子余额明细.场景代码 in ("产品(净值型)-认购申购交易" or "产品(净值型)-红利再投"）)
        if (DictCodeList.是否_是.getValue().equals(isleaf)
                && (DictCodeListTableId.会计场景代码_产品_净值型_认购申购交易.getValue().equals(balance.getTableId())
                || DictCodeListTableId.会计场景代码_产品_净值型_红利再投.getValue().equals(balance.getTableId()))) {
            // 入参.子余额明细.申购未实现平准金 = 子余额明细.科目余额
            balance.setPurOUlamt(balance.getBalance());
            // 入参.本位币子余额明细.申购未实现平准金 = 子余额明细.本位币科目余额
            balance.setBpurOUlamt(balance.getbAmt());
        }
        // if(参数.是否叶子节点 = "是"
        // && 入参.子余额明细.场景代码 in ("产品(净值型)-赎回到期"）)
        if (DictCodeList.是否_是.getValue().equals(isleaf)
                && DictCodeListTableId.会计场景代码_产品_净值型_赎回到期.getValue().equals(balance.getTableId())) {
            // 入参.子余额明细.申购未实现平准金 = 子余额明细.科目余额
            balance.setRedOUlamt(balance.getBalance());
            // 入参.子余额明细.本位币申购未实现平准金 = 子余额明细.本位币科目余额
            balance.setBredOUlamt(balance.getbAmt());
        }
        //定义 变量.KEY = 科目号
        String key = subjectNo;
//        //IF 参数.是否叶子节点  = “是”
//        if (DictCodeList.是否_是.getValue().equals(isleaf)){
//            //变量.KEY += 入参.子余额明细.原币种
//            key = key+ balance.getOCcy();
//        }
//        else{
//            //变量.KEY += 入参.子余额明细.本币种
//            key = key+ balance.getbCcy();
//        }
        // if（科目余额MAP.get(变量.KEY) == null）
        if (balanceMap.get(key) == null) {
            // 变量.本科目余额明细
            SubjectBalance dto = new SubjectBalance();
            dto.setBooksetId(balance.getBooksetId());
            dto.setBookDate(balance.getBookDate());
            dto.setSubjectNo(subjectNo);
            dto.setFsubjectId(fSubjectId);
            dto.setSubjectLevel(subjectLevel);
            dto.setOCcy(balance.getOCcy());
            //增加本位币处理 add by chufengjie
            dto.setbCcy(balance.getbCcy());

            // 调用共通方法获取科目余额、方向、数量(原币)
            Map<String, Object> balanceDeal = this.balanceDirectDealBalance(subject.getBalFlag(),
                    balance.getBalFlagHappenDate(), balance.getBalance(), balance.getNum(),balance.getbAmt());

            //增加本位币处理 add by chufengjie 20190712 start
//            if (DictCodeList.是否_是.getValue().equals(isleaf)){
            if (isCountBBalance){
                // 设值原币科目余额
                dto.setBalance(new BigDecimal(balanceDeal.get("balance").toString()));
                //原币当日发生额
                dto.setTdyOAmt(new BigDecimal(balanceDeal.get("balance").toString()));
            }
            // 设值本币科目余额
            dto.setbAmt(new BigDecimal(balanceDeal.get("benbalance").toString()));
            //本位币当日发生额 <-上述返回.本位币科目余额
            dto.setTdyBAmt(new BigDecimal(balanceDeal.get("benbalance").toString()));
            // 设值余额数量
            dto.setNum(new BigDecimal(balanceDeal.get("num").toString()));
            // 设值余额方向
            dto.setBalFlagHappenDate(balanceDeal.get("balDirect").toString());
            // 当日发生额 方向
            dto.setTdyAmtFlag(balanceDeal.get("balDirect").toString());

            // if（入参.子余额明细.余额方向 = "借方"）
            if (DictCodeList.余额方向_借方.getValue().equals(balance.getBalFlagHappenDate())) {
//                if (DictCodeList.是否_是.getValue().equals(isleaf)){
                if (isCountBBalance){
                    // 借方余额 = 子余额明细.科目余额
                    dto.setdAmt(balance.getBalance());
                    // 原币结转余额借方 = 子余额明细.结转金额
                    dto.setoCcyKnotsAmtD(balance.getoCcyKnotsAmt());
                    // 原币当日发生额借方 = 子余额明细.科目余额
                    dto.setTdyODAmt(balance.getBalance());
                    // 原币结转当日发生额借方 = 子余额明细.结转金额
                    dto.setTdyOCoDAmt(balance.getoCcyKnotsAmt());
                    // 原币申购未实现平准金借 = 子余额明细.申购未实现平准金
                    dto.setTdyPurODUlamt(balance.getPurOUlamt());
                    // 原币赎回未实现平准金借 = 子余额明细.赎回未实现平准金
                    dto.setTdyRedODUlamt(balance.getRedOUlamt());
                }
                //本位币借方余额 = 子余额明细.本位币科目余额
                dto.setbDAmt(balance.getbAmt());
                //本位币结转余额借方 = 子余额明细.本位币结转金额
                dto.setbCoDAmt(balance.getbCoAmt());
                //本位币当日发生额借方 = 子余额明细.本位币科目余额
                dto.setTdyBDAmt(balance.getbAmt());
                //本位币结转当日发生额借方  = 子余额明细.本位币结转金额
                dto.setTdyBCoDAmt(balance.getbCoAmt());
                //本位币申购未实现平准金借 tdyPurBDUlamt= 子余额明细.本位币申购未实现平准金
                dto.setTdyPurBDUlamt(balance.getBpurOUlamt());
                //本位币赎回未实现平准金借 = 子余额明细.本位币赎回未实现平准金
                dto.setTdyRedBDUlamt(balance.getBredOUlamt());

            }
            else {
//                if (DictCodeList.是否_是.getValue().equals(isleaf)){
                if (isCountBBalance){
                    // 贷方余额 = 子余额明细.科目余额
                    dto.setcAmt(balance.getBalance());
                    // 原币结转余额贷方 = 子余额明细.结转金额
                    dto.setoCcyKnotsAmtC(balance.getoCcyKnotsAmt());
                    // 原币当日发生额贷方 = 子余额明细.科目余额
                    dto.setTdyOCAmt(balance.getBalance());
                    // 原币结转当日发生额贷方 = 子余额明细.结转金额
                    dto.setTdyOCoCAmt(balance.getoCcyKnotsAmt());
                    // 原币申购未实现平准金贷 = 子余额明细.申购未实现平准金
                    dto.setTdyPurOCUlamt(balance.getPurOUlamt());
                    // 原币赎回未实现平准金贷 = 子余额明细.赎回未实现平准金
                    dto.setTdyRedOCUlamt(balance.getRedOUlamt());
                }
                //增加本位币处理 add by chufengjie 20190712 start
                // 贷方余额 = 子余额明细.科目余额
                dto.setbCAmt(balance.getbAmt());
                // 本币结转余额贷方 = 子余额明细.结转金额
                dto.setbCoCAmt(balance.getbCoAmt());
                // 本币当日发生额贷方 = 子余额明细.科目余额
                dto.setTdyBCAmt(balance.getbAmt());
                // 本币结转当日发生额贷方 = 子余额明细.结转金额tdyBCoCAmt
                dto.setTdyBCoCAmt(balance.getbCoAmt());
                // 本币申购未实现平准金贷 = 子余额明细.申购未实现平准金tdyPurBCUlamt
                dto.setTdyPurBCUlamt(balance.getBpurOUlamt());
                // 本币赎回未实现平准金贷 = 子余额明细.赎回未实现平准金tdyRedBCUlamt
                dto.setTdyRedBCUlamt(balance.getBredOUlamt());
                //增加本位币处理 add by chufengjie 20190712 end

            }
            // 调用共通方法获取科目余额、方向、数量
            // 参数 变量.科目信息.余额方向 子余额明细.余额方向 子余额明细.科目余额 0--原币
            balanceDeal = this.balanceDirectDealBalance(subject.getBalFlag(),
                    balance.getBalFlagHappenDate(), balance.getoCcyKnotsAmt(), BigDecimal.ZERO, balance.getbCoAmt());

            // 参数 变量.科目信息.余额方向 子余额明细.余额方向 子余额明细.科目余额 0--本位币
            // 本位币结转余额 <- 上述返回.科目余额
            dto.setbCoAmt(new BigDecimal(balanceDeal.get("benbalance").toString()));
            // 本币结转当日发生额 <- 子余额明细.结转金额tdyBCoAmt
            dto.setTdyBCoAmt(new BigDecimal(balanceDeal.get("benbalance").toString()));
            //IF 参数.是否叶子节点 == "是”
//            if (DictCodeList.是否_是.getValue().equals(isleaf)){
            if (isCountBBalance){
                // 原币结转余额 <- 上述返回.科目余额
                dto.setoCcyKnotsAmt(new BigDecimal(balanceDeal.get("balance").toString()));
                // 原币结转当日发生额 <- 子余额明细.结转金额
                dto.setTdyOCoAmt(new BigDecimal(balanceDeal.get("balance").toString()));
            }
            // 结转余额方向 <- 上述返回.余额方向
            dto.setBalCoFlag(balanceDeal.get("balDirect").toString());
            // 结转当日发生额方向 <- 上述返回.余额方向
            dto.setTdyCoFlag(balanceDeal.get("balDirect").toString());
            // 是否叶子节点
            dto.setIsleaf(isleaf);
            // 存入map
            balanceMap.put(key, dto);
        }
        else {
            SubjectBalance dto = balanceMap.get(key);
            // 调用共通方法获取科目余额、方向、数量--原币
            // modified by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 start
            Map<String, Object> balanceDeal = this.balanceDirectDealAccrualAddBalance(subject.getBalFlag(),
                dto.getBalFlagHappenDate(), dto.getBalance(), dto.getNum(), balance.getBalFlagHappenDate(),
                balance.getBalance(), balance.getNum(), dto.getbAmt(), balance.getbAmt(), DictCodeList.是否_是.getValue());
            // modified by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 end
            if (isCountBBalance){
                // 设值科目余额
                dto.setBalance(new BigDecimal(balanceDeal.get("balance").toString()));
            }
            // 设值科目余额--本位币
            dto.setbAmt(new BigDecimal(balanceDeal.get("benbalance").toString()));
            // 设值余额数量
            dto.setNum(new BigDecimal(balanceDeal.get("num").toString()));
            // 设值余额方向
            dto.setBalFlagHappenDate(balanceDeal.get("balDirect").toString());
            // if（入参.子余额明细.余额方向 = "借方"）
            if (DictCodeList.余额方向_借方.getValue().equals(balance.getBalFlagHappenDate())) {
                if (isCountBBalance){
                    // 借方余额 += 子余额明细.科目余额
                    dto.setdAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, dto.getdAmt(),
                            balance.getBalance()));
                    // 原币结转余额借方 += 子余额明细.结转金额
                    dto.setoCcyKnotsAmtD(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getoCcyKnotsAmtD(), balance.getoCcyKnotsAmt()));
                    // 原币当日发生额借方 += 子余额明细.科目余额
                    dto.setTdyODAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getTdyODAmt(), balance.getBalance()));
                    // 原币结转当日发生额借方 += 子余额明细.结转金额
                    dto.setTdyOCoDAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getTdyOCoDAmt(), balance.getoCcyKnotsAmt()));
                    // 原币申购未实现平准金借 += 子余额明细.申购未实现平准金
                    dto.setTdyPurODUlamt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getTdyPurODUlamt(), balance.getPurOUlamt()));
                    // 原币赎回未实现平准金借 += 子余额明细.赎回未实现平准金
                    dto.setTdyRedODUlamt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getTdyRedODUlamt(), balance.getRedOUlamt()));
                }
                // 本位币余额借方 += 子余额明细.本位币科目余额
                dto.setbDAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, dto.getbDAmt(),
                        balance.getbAmt()));
                // 本位币结转余额借方+= 子余额明细.结转金额
                dto.setbCoDAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getbCoDAmt(), balance.getbCoAmt()));
                //本位币当日发生额借方 += 子余额明细.本位币科目余额
                dto.setTdyBDAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getTdyBDAmt(), balance.getbAmt()));
                // 本位币结转当日发生额借方 += 子余额明细.本位币结转金额
                dto.setTdyBCoDAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getTdyBCoDAmt(), balance.getbCoAmt()));
                // 本位币申购未实现平准金借 += 子余额明细.本位币申购未实现平准金
                dto.setTdyPurBDUlamt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getTdyPurBDUlamt(), balance.getBpurOUlamt()));
                // 本位币赎回未实现平准金借 += 子余额明细.本位币赎回未实现平准金
                dto.setTdyRedBDUlamt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getTdyRedBDUlamt(), balance.getBredOUlamt()));
            }
            else {
                if (isCountBBalance){
                    // 贷方余额 += 子余额明细.科目余额
                    dto.setcAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, dto.getcAmt(),
                            balance.getBalance()));
                    // 原币结转余额贷方 += 子余额明细.结转金额
                    dto.setoCcyKnotsAmtC(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getoCcyKnotsAmtC(), balance.getoCcyKnotsAmt()));
                    // 原币当日发生额贷方 += 子余额明细.科目余额
                    dto.setTdyOCAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getTdyOCAmt(), balance.getBalance()));
                    // 原币结转当日发生额贷方 += 子余额明细.结转金额
                    dto.setTdyOCoCAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getTdyOCoCAmt(), balance.getoCcyKnotsAmt()));
                    // 原币申购未实现平准金贷 += 子余额明细.申购未实现平准金
                    dto.setTdyPurOCUlamt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getTdyPurOCUlamt(), balance.getPurOUlamt()));
                    // 原币赎回未实现平准金贷 += 子余额明细.赎回未实现平准金
                    dto.setTdyRedOCUlamt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            dto.getTdyRedOCUlamt(), balance.getRedOUlamt()));
                }
                // 变量.本科目余额明细.本位币贷方余额 += 子余额明细.本位币科目余额
                dto.setbCAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, dto.getbCAmt(),
                        balance.getbAmt()));
                // 本位币结转余额贷方 += 子余额明细.本位币结转金额
                dto.setbCoCAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getbCoCAmt(), balance.getbCoAmt()));
                // 本位币当日发生额贷方 += 子余额明细.本位币科目余额
                dto.setTdyBCAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getTdyBCAmt(), balance.getbAmt()));
                // 本位币结转当日发生额贷方 += 子余额明细.本位币结转金额
                dto.setTdyBCoCAmt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getTdyBCoCAmt(), balance.getbCoAmt()));
                // 本位币申购未实现平准金贷 += 子余额明细.本位币申购未实现平准金
                dto.setTdyPurBCUlamt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getTdyPurBCUlamt(), balance.getBpurOUlamt()));
                // 本位币赎回未实现平准金贷 += 子余额明细.本位币赎回未实现平准金
                dto.setTdyRedBCUlamt(ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                        dto.getTdyRedBCUlamt(), balance.getBredOUlamt()));

            }
            if (!ArithmeticUtil.equals(balance.getoCcyKnotsAmt(), BigDecimal.ZERO)
                    ||!ArithmeticUtil.equals(balance.getbCoAmt(), BigDecimal.ZERO)) {
                //结转余额方向
                String balCoFlag  = dto.getBalCoFlag();
                //原币结转余额
                BigDecimal oCcyKnotsAmt = dto.getoCcyKnotsAmt();
                //本币结转余额
                BigDecimal bCoAmt = dto.getbCoAmt();
                // 变量.本科目余额明细.结转余额方向为空时 借方
                if(StringUtil.isEmpty(balCoFlag)){
                    balCoFlag = DictCodeList.借贷方向_借.getValue();
                }
                // 变量.变量.本科目余额明细.原币结转余额为空时 0
                if(oCcyKnotsAmt == null){
                    oCcyKnotsAmt = BigDecimal.ZERO;
                }
                // 变量.变量.本科目余额明细.本币结转余额为空时 0
                if(bCoAmt == null){
                	bCoAmt = BigDecimal.ZERO;
                }

                // 调用共通方法获取科目余额、方向、数量
                // 参数 ： 变量.科目信息.余额方向 变量.本科目余额明细.结转余额方向 变量.本科目余额明细.结转余额 0 子余额明细.结转金额 子余额明细.余额方向
                // 0
                // modified by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 start
                balanceDeal = this.balanceDirectDealAccrualAddBalance(subject.getBalFlag(), balCoFlag, oCcyKnotsAmt,
                    BigDecimal.ZERO, balance.getBalFlagHappenDate(), balance.getoCcyKnotsAmt(), BigDecimal.ZERO, bCoAmt,
                    balance.getbCoAmt(), DictCodeList.是否_是.getValue());
                // modified by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 end
                if (isCountBBalance){
                    // 原币结转余额 <- 上述返回.科目余额
                    dto.setoCcyKnotsAmt(new BigDecimal(balanceDeal.get("balance").toString()));
                }
                // 本币结转余额 <- 上述返回.本币科目余额
                dto.setbCoAmt(new BigDecimal(balanceDeal.get("benbalance").toString()));
                // 结转余额方向 <- 上述返回.余额方向
                dto.setBalCoFlag(balanceDeal.get("balDirect").toString());

                //结转当日发生额方向
                String tdyCoFlag  = dto.getTdyCoFlag();
                //原币结转当日发生额
                BigDecimal tdyOCoAmt = dto.getTdyOCoAmt();
                //本币结转当日发生额
                BigDecimal tdyBCoAmt = dto.getTdyBCoAmt();
                // 变量.本科目余额明细.结转当日发生额方向为空时 借方
                if(StringUtil.isEmpty(tdyCoFlag)){
                    tdyCoFlag = DictCodeList.借贷方向_借.getValue();
                }
                // 变量.本科目余额明细.原币结转当日发生额为空时 0
                if(tdyOCoAmt == null){
                    tdyOCoAmt = BigDecimal.ZERO;
                }
                // 变量.本科目余额明细.本币结转当日发生额为空时 0
                if(tdyBCoAmt == null){
                	tdyBCoAmt = BigDecimal.ZERO;
                }
                // 调用共通方法获取科目余额、方向、数量
                // 参数 ： 变量.科目信息.余额方向 变量.本科目余额明细.原币结转当日发生额 变量.本科目余额明细.原币结转当日发生额方向 0 子余额明细.科目余额
                // 子余额明细.余额方向 0
                // modified by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 start
                balanceDeal = this.balanceDirectDealAccrualAddBalance(subject.getBalFlag(), tdyCoFlag, tdyOCoAmt,
                    BigDecimal.ZERO, balance.getBalFlagHappenDate(), balance.getBalance(), BigDecimal.ZERO, tdyBCoAmt,
                    balance.getbCoAmt(), DictCodeList.是否_是.getValue());
                // modified by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 end
                if (isCountBBalance){
                    // 原币结转当日发生额 <- 上述返回.科目余额
                    dto.setTdyOCoAmt(new BigDecimal(balanceDeal.get("balance").toString()));
                }
                // 本币结转当日发生额 <- 上述返回.科目余额
                dto.setTdyBCoAmt(new BigDecimal(balanceDeal.get("benbalance").toString()));
                // 结转当日发生额方向 <- 上述返回.余额方向
                dto.setTdyCoFlag(balanceDeal.get("balDirect").toString());


            }
            //当日发生额方向
            String tdyAmtFlag  = dto.getTdyAmtFlag();
            //原币当日发生额
            BigDecimal tdyOAmt = dto.getTdyOAmt();
            //本币当日发生额
            BigDecimal tdyBAmt = dto.getTdyBAmt();

            // 变量.本科目余额明细.当日发生额方向为空时 借方
            if(StringUtil.isEmpty(tdyAmtFlag)){
                tdyAmtFlag = DictCodeList.借贷方向_借.getValue();
            }
            // 变量.本科目余额明细.原币当日发生额为空时 0
            if(tdyOAmt == null){
                tdyOAmt = BigDecimal.ZERO;
            }
            // 变量.本科目余额明细.原币当日发生额为空时 0
            if(tdyBAmt == null){
                tdyBAmt = BigDecimal.ZERO;
            }
            // 调用共通方法获取科目余额、方向、数量--原币
            // 参数 ： 变量.科目信息.余额方向 变量.本科目余额明细.原币当日发生额 变量.本科目余额明细.原币当日发生额方向 0 子余额明细.科目余额
            // 子余额明细.余额方向 0
            // modified by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 start
            balanceDeal = this.balanceDirectDealAccrualAddBalance(subject.getBalFlag(), tdyAmtFlag, tdyOAmt,
                BigDecimal.ZERO, balance.getBalFlagHappenDate(), balance.getBalance(), BigDecimal.ZERO, tdyBAmt,
                balance.getbAmt(), DictCodeList.是否_是.getValue());
            // modified by wangzhiang GZHSXT-2408【科目余额查询】3003科目余额为负，四级科目为正，汇总到一级科目为负 20191227 end
            if (isCountBBalance){
                // 当日发生额
                dto.setTdyOAmt(new BigDecimal(balanceDeal.get("balance").toString()));
            }
            // 本位币当日发生额
            dto.setTdyBAmt(new BigDecimal(balanceDeal.get("benbalance").toString()));
            // 当日发生额 方向
            dto.setTdyAmtFlag(balanceDeal.get("balDirect").toString());
            // 更新map
            balanceMap.put(key, dto);

        }
        // 上级科目不为空，回调
        if (StringUtil.isNotEmpty(fSubjectId)) {
            BokSubject fsubject = subjectMap.get(fSubjectId);
            collectSubjectBalance(balance, fSubjectId, fsubject.getSubjectLevel(), balanceMap,
                    subjectMap, DictCodeList.是否_否.getValue());
        }
    }

    /**
     * 科目余额查询-历史表
     *
     * @return List<SubjectBalance> 结果集
     * @param searchDto 查询参数Dto booksetId --账套Id bokDetailId --账务明细代码 happenDate --发生日 bookDate
     *            --入账日 baseSubjectNatureList --基础科目性质（集合）
     * @see
     * @since 1.0
     */
    public List<SubjectBalanceAll> getLatestSubjectBalanceAll(SubjectBalanceSearchDto searchDto) {
        // 定义传递参数
        // Map<String, Object> params = new HashMap<>();
        // params.put("booksetId", searchDto.getBooksetId());// 账套代码
        // params.put("bokDetailId", searchDto.getBokDetailId());// 账务明细代码
        // params.put("happenDate", searchDto.getHappenDate());// 发生日
        // params.put("bookDate", searchDto.getBookDate());// 入账日
        // params.put("baseSubjectNatureList", searchDto.getBaseSubjectNatureList());// 基础科目性质（集合）
        List<SubjectBalanceAll> list = accountingCommonMapper.querySubjectBalanceOfHistory(
                searchDto);
        return list;
    }

    /**
     * 公用方法：账务类共通方法-基础科目性质查询
     *
     * @param groupId 分组代码 【必填项】
     * @param acctStandardId 会计准则代码 【非必填项】
     * @return 科目性质列表
     * @see
     * @since 1.0
     */
    public List<String> getValSubjectNatureList(String groupId) {
        return accountingCommonMapper.getValSubjectNatureList(groupId,null);
    }

    /**
     * 公用方法：账务类共通方法-基础科目性质查询
     *
     * @param groupId 分组代码 【必填项】
     * @param acctStandardId 会计准则代码 【非必填项】
     * @return 科目性质列表
     * @see
     * @since 1.0
     */
    public List<String> getValSubjectNatureListWithAcctStandardId(String groupId,String acctStandardId) {
        return accountingCommonMapper.getValSubjectNatureList(groupId,acctStandardId);
    }

    /**
     * 公用方法：账务类共通方法-帐套资产负债查询
     *
     * @param sdateStart 查询开始日
     * @param sdateEnd 查询结束日
     * @param bookDate 入账日
     * @param booksetId 帐套代码
     * @param queryDates 查询日列表
     * @param finprodId 金融产品代码
     * @return 帐套资产负债信息
     * @see
     * @since 1.0
     */
/*    public List<BokStatBokPosition> getValStatBokPosition(Date sdateStart, Date sdateEnd,
                                                          Date bookDate, String booksetId,
                                                          List<Date> queryDates,
                                                          String finprodId) {

        return accountingCommonMapper.getValStatBokPosition(sdateStart, sdateEnd, bookDate,
            booksetId, queryDates, finprodId);
    }*/
    public List<BokStatBokPosition> getValStatBokPosition(BokStatBokPositionDto position) {

        return accountingCommonMapper.getValStatBokPosition(position);
    }

    /**
     * TISHKBS-1998 【估值核算-估值表】货币型产品估值表中的“本日收益-费后”统计的是当日应付利润发生额，但是并未将手工账的应付利润发生额统计进去
     * @param params
     * @return
     */
    public BigDecimal getVouchAmt(Map<String, Object> params) {
        return accountingCommonMapper.getVouchAmt(params);
    }

    /**
     * 公用方法：帐套资产负债插入及更新
     *
     * @param valStatBokPosition CDate 统计日期 bookSetId 账套代码
     * @return 帐套资产负债插入及更新数目
     * @see
     * @since 1.0
     */
    public int insertOrUpdateBokPosition(BokStatBokPosition valStatBokPosition) {
        int num = 0;
        // 入账日
        Date bookDate = valStatBokPosition.getBookDate();
        // 发生日
        Date happenDate = valStatBokPosition.getHappenDate();
        // 账套
        String booksetId = valStatBokPosition.getBooksetId();

        // 查询传入条件下帐套资产负债信息
        int count = accountingCommonMapper.queryValStatBokPosition(bookDate, happenDate,
                booksetId);

        // 如果list不为空则更新相应日期数据，else插入相应数据
        if (count > 0) {
            num = valStatBokPositionMapper.updateByPrimaryKeySelective(valStatBokPosition);
        }
        else {
            num = valStatBokPositionMapper.insert(valStatBokPosition);
        }
        return num;
    }

    /**
     * 待计提金融产品现金流查询
     *
     * @param happenDate 发生日
     * @param taskId 任务代码
     * @param cashTypeList 现金流类型集合
     * @return 金融产品信息集合
     * @see
     * @since 1.0
     */
    public List<PrdCashEntity> queryFinPrdCash(Map<String, Object> params) {
        params.put("accSubjectType", DictCodeListExt1.金融产品类型_理财产品.getValue());
        return accountingCommonMapper.queryFinPrdCash(params);
    }

    /**
     * 账套科目余额方向信息
     *
     * @return 账套科目表DTO
     * @see
     * @since 1.0
     */
    public List<BalanceDetailInfoEntity> queryAccountBalanceDirection(Map<String, Object> params) {
        return accountingCommonMapper.queryAccountBalanceDirection(params);
    }

    /**
     * 账务明细记录比对
     *
     * @param valBokBookdetailCurrent 账务明细（当前）
     * @param valBokBookdetailAll 账务明细（历史）
     * @param bookDate 入账日
     * @param balFlag 余额方向
     * @return 账套科目表DTO
     * @see
     * @since 1.0
     */
    public BokBookdetailCurrent comparisonOfAccountRecords(BokBookdetailCurrent valBokBookdetailCurrent,
                                                           BokBookdetailAll valBokBookdetailAll,
                                                           Date bookDate, String balFlag) {
        // 定义变量.比对差额DTO
        BokBookdetailCurrent dto = new BokBookdetailCurrent();
        // 历史表中没有当前表中有的数据，直接复制当前表数据 -》 变量.比对差额DTO = 账务明细（当前）
        if (valBokBookdetailAll == null) {
            setDto1(dto, valBokBookdetailCurrent);

            // 当前表中没有历史表中有的数据，按金额*-1存入调整额-》 变量.比对差额DTO = 账务明细（历史）反向
        }
        else if (valBokBookdetailCurrent == null) {
            setDto2(dto, valBokBookdetailAll, bookDate);

            // 当前表和历史表都有数据，进行计算
        }
        else if (DictCodeList.冲正标识_冲销交易.getValue().equals(
                valBokBookdetailCurrent.getOffsetFlag())) {
            setDto3(dto, valBokBookdetailAll, bookDate);
            // 账务明细（当前）.冲正标识 = 【数据字典.冲销标志.冲销交易】
        }
        else {
            // 调用共通方法【账务类共通方法设计-余额方向处理_发生额加余额SHEET】
            Map<String, Object> map = balanceDirectDealAccrualAddBalance(balFlag,
                    valBokBookdetailCurrent.getCdFlag(), valBokBookdetailCurrent.getoHappenAmt(),
                    valBokBookdetailCurrent.getHappenNumber(), valBokBookdetailAll.getCdFlag(),
                    valBokBookdetailAll.getoHappenAmt() == null ? BigDecimal.ZERO : valBokBookdetailAll.getoHappenAmt().negate(),
                    valBokBookdetailAll.getHappenNumber() == null ? BigDecimal.ZERO : valBokBookdetailAll.getHappenNumber().negate(),
                    valBokBookdetailCurrent.getbHappenAmt(),
                    valBokBookdetailAll.getbHappenAmt() == null ? BigDecimal.ZERO : valBokBookdetailAll.getbHappenAmt().negate()
                    );
            // 1.流水号: 自动生成，规则为BKS+"YYYYMMDD"+9位序列号
            // DateTime date = new DateTime(dBUtil.getSysDate());
            dto.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
            // 2.账套代码 账务明细（当前）.账套代码
            dto.setBooksetId(valBokBookdetailCurrent.getBooksetId());
            // 3.场景代码 账务明细（当前）.场景代码
            dto.setTableId(valBokBookdetailCurrent.getTableId());
            // 4.交易编号 账务明细（当前）.交易编号
            dto.setTradeId(valBokBookdetailCurrent.getTradeId());
            // 5.业务明细代码 账务明细（当前）.业务明细代码
            dto.setBusiId(valBokBookdetailCurrent.getBusiId());
            // 6.任务代码 账务明细（当前）.任务代码
            dto.setTaskId(valBokBookdetailCurrent.getTaskId());
            // 7.发生日期 账务明细（当前）.发生日期
            dto.setHappenDate(valBokBookdetailCurrent.getHappenDate());
            // 8.入账日期 账务明细（当前）.入账日期
            dto.setBookDate(valBokBookdetailCurrent.getBookDate());
            // 9.基础科目性质 账务明细（当前）.基础科目性质
            dto.setBaseSubjectNature(valBokBookdetailCurrent.getBaseSubjectNature());
            // 10.分支序号 账务明细（当前）.分支序号
            dto.setBranch(valBokBookdetailCurrent.getBranch());
            // 11.科目号 账务明细（当前）.科目号
            dto.setSubjectNo(valBokBookdetailCurrent.getSubjectNo());
            // 12.四级科目号 账务明细（当前）.四级科目号
            dto.setDetailSubjectNo(valBokBookdetailCurrent.getDetailSubjectNo());
            // 13.借贷方向 返回值.余额方向
            dto.setCdFlag(map.get("balDirect").toString());
            // 14.原币币种 账务明细（历史）.原币币种
            dto.setoCcy(valBokBookdetailCurrent.getoCcy());
            // 15.原币发生额 返回值.余额
            dto.setoHappenAmt(new BigDecimal(map.get("balance").toString()));
            // 18.发生数量 返回值..数量
            dto.setHappenNumber(new BigDecimal(map.get("num").toString()));


            /**add by wujie on 20210113 start remark:本位币处理**/
            // 本币币种 账务明细(历史表).本币币种
            dto.setbCcy(valBokBookdetailCurrent.getbCcy());
            // 本币发生额 返回值.余额
            dto.setbHappenAmt(new BigDecimal(map.get("benbalance").toString()));
            /**add by wujie on 20210113 end remark:本位币处理**/


            // 19.结息日 账务明细（历史）.结息日
            dto.setMdate(valBokBookdetailAll.getMdate());
            // 20.凭证编号 按凭证编号生成规则正常生成
            dto.setBookId(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型)); // 待定
            // 21.凭证说明 "账务明细比对调整"
            dto.setBookRemark("账务明细比对调整");
            // 22.冲正标识 数据字典.冲销标志.正常交易】
            dto.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
            // 23.凭证类型 【数据字典.凭证类型.日终】
            dto.setBookType(DictCodeList.凭证类型_日终.getValue());
            // 24.处理序列 按处理序列生成规则正常生成
            dto.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));// 待定

            // 25.换汇日期 账务明细（当前）.换汇日期
            dto.setExDate(valBokBookdetailCurrent.getExDate());
            // 26.折算汇率 账务明细（当前）.折算汇率
            dto.setExRate(valBokBookdetailCurrent.getExRate());
            // 27.创建时间 当前时间
            dto.setCreateTime(new Date());
            // 28.更新时间 当前时间
            dto.setUpdateTime(new Date());
            // 29.本币币种 账务明细（历史）.原币币种
            dto.setbCcy(valBokBookdetailCurrent.getbCcy());
            // 30.本币发生额 返回值.余额
            dto.setoHappenAmt(new BigDecimal(map.get("benbalance").toString()));
            // 账套日期
            dto.setBooksetDate(valBokBookdetailCurrent.getBooksetDate());
            // 账务明细代码
            dto.setBokDetailId(valBokBookdetailCurrent.getBokDetailId());
            // 任务优先级
            dto.setTaskPri(valBokBookdetailCurrent.getTaskPri());
            // if 返回值.余额=0时，该条记录remove
            if (ArithmeticUtil.equals(dto.getoHappenAmt(), BigDecimal.ZERO)) {
                return null;
            }
        }
        return dto;
    }

    /**
     * //当前表中没有历史表中有的数据，按金额*-1存入调整额-》 变量.比对差额DTO = 账务明细（历史）反向
     *
     * @param dto 账务明细（当前表）
     * @param model 账务明细（历史表）
     * @param bookDate 入账日
     * @see
     * @since 1.0
     */
    private void setDto3(BokBookdetailCurrent dto, BokBookdetailAll model, Date bookDate) {
        BeanUtil.copyPropertiesIgnoreNull(dto, model);
        // 1.流水号: 自动生成，规则为BKS+"YYYYMMDD"+9位序列号
        // DateTime date = new DateTime(dBUtil.getSysDate());
        dto.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
        // 15.原币发生额 返回值.余额 原币发生额 *-1
        dto.setoHappenAmt(
                model.getoHappenAmt() == null ? BigDecimal.ZERO : model.getoHappenAmt().negate());
        // 17.本币发生额 账务明细（历史）.本币发生额
        dto.setbHappenAmt(model.getbHappenAmt() == null ? BigDecimal.ZERO : model.getbHappenAmt().negate());
        // 18.发生数量 账务明细（历史）.发生数量*-1
        dto.setHappenNumber(
                model.getHappenNumber() == null ? BigDecimal.ZERO : model.getHappenNumber().negate());
        // 22.冲正标识 账务明细（历史）.冲正标识
        dto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
        // 24.处理序列 账务明细（历史）.凭证序列
        dto.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
        // 27.创建时间 当前时间
        dto.setCreateTime(new Date());
        // 28.更新时间 当前时间
        dto.setUpdateTime(new Date());

//
//        // 1.流水号: 自动生成，规则为BKS+"YYYYMMDD"+9位序列号
//        // DateTime date = new DateTime(dBUtil.getSysDate());
//        dto.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
//        // 2.账套代码 账务明细（历史）.账套代码
//        dto.setBooksetId(model.getBooksetId());
//        // 3.场景代码 账务明细（历史）.场景代码
//        dto.setTableId(model.getTableId());
//        // 4.交易编号 账务明细（历史）.交易编号
//        dto.setTradeId(model.getTradeId());
//        // 5.业务明细代码 账务明细（历史）.业务明细代码
//        dto.setBusiId(model.getBusiId());
//        // 6.任务代码 账务明细（历史）.任务代码
//        dto.setTaskId(model.getTaskId());
//        // 7.发生日期 账务明细（历史）.发生日期
//        dto.setHappenDate(model.getHappenDate());
//        // 8.入账日期 账务明细（历史）.入账日期
//        dto.setBookDate(bookDate);
//        // 9.基础科目性质 账务明细（历史）.基础科目性质
//        dto.setBaseSubjectNature(model.getBaseSubjectNature());
//        // 10.分支序号 账务明细（历史）.分支序号
//        dto.setBranch(model.getBranch());
//        // 11.科目号 账务明细（历史）.科目号
//        dto.setSubjectNo(model.getSubjectNo());
//        // 12.四级科目号 账务明细（历史）.四级科目号
//        dto.setDetailSubjectNo(model.getDetailSubjectNo());
//        // 13.借贷方向 账务明细（历史）.借贷方向
//        dto.setCdFlag(model.getCdFlag());
//        // 14.原币币种 账务明细（历史）.原币币种
//        dto.setoCcy(model.getoCcy());
//        // 15.原币发生额 返回值.余额 原币发生额 *-1
//        dto.setoHappenAmt(
//                model.getoHappenAmt() == null ? BigDecimal.ZERO : model.getoHappenAmt().negate());
//        // 16.本币币种 账务明细（历史）.本币币种
//        // dto.setbCcy(model.getbCcy());
//        // 17.本币发生额 账务明细（历史）.本币发生额
//        // dto.setbHappenAmt(model.getbHappenAmt());
//        // 18.发生数量 账务明细（历史）.发生数量*-1
//        dto.setHappenNumber(
//                model.getHappenNumber() == null ? BigDecimal.ZERO : model.getHappenNumber().negate());
//        // 19.结息日 账务明细（历史）.结息日
//        dto.setMdate(model.getMdate());
//        // 20.凭证编号 账务明细（历史）.凭证编号
//        dto.setBookId(model.getBookId());
//        // 21.凭证说明 账务明细（历史）.凭证说明
//        dto.setBookRemark(model.getBookRemark());
//        // 22.冲正标识 账务明细（历史）.冲正标识
//        dto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
//        // 反向流水号
//        dto.setrSeqno(model.getrSeqno());
//        // 23.凭证类型 账务明细（历史）.凭证类型
//        dto.setBookType(model.getBookType());
//        // 24.处理序列 账务明细（历史）.凭证序列
//        dto.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
//        // 25.换汇日期 账务明细（历史）.换汇日期
//        dto.setExDate(model.getExDate());
//        // 26.折算汇率 账务明细（历史）.折算汇率
//        dto.setExRate(model.getExRate());
//        // 27.创建时间 当前时间
//        dto.setCreateTime(new Date());
//        // 28.更新时间 当前时间
//        dto.setUpdateTime(new Date());
//        // 账套日期
//        dto.setBooksetDate(model.getBooksetDate());
//        // 账务明细代码
//        dto.setBokDetailId(model.getBokDetailId());
//        // 任务优先级
//        dto.setTaskPri(model.getTaskPri());
    }

    /**
     * //当前表中没有历史表中有的数据，按金额*-1存入调整额-》 变量.比对差额DTO = 账务明细（历史）反向
     *
     * @param dto 账务明细（当前表）
     * @param model 账务明细（历史表）
     * @param bookDate 入账日
     * @see
     * @since 1.0
     */
    private void setDto2(BokBookdetailCurrent dto, BokBookdetailAll model, Date bookDate) {
        BeanUtil.copyPropertiesIgnoreNull(dto, model);
        // 1.流水号: 自动生成，规则为BKS+"YYYYMMDD"+9位序列号
        // DateTime date = new DateTime(dBUtil.getSysDate());
        dto.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
        // 15.原币发生额 返回值.余额 原币发生额 *-1
        dto.setoHappenAmt(
                model.getoHappenAmt() == null ? BigDecimal.ZERO : model.getoHappenAmt().negate());
        // 17.本币发生额 账务明细（历史）.本币发生额
         dto.setbHappenAmt(model.getbHappenAmt() == null ? BigDecimal.ZERO : model.getbHappenAmt().negate());
        // 18.发生数量 账务明细（历史）.发生数量*-1
        dto.setHappenNumber(
                model.getHappenNumber() == null ? BigDecimal.ZERO : model.getHappenNumber().negate());
        // 24.处理序列 账务明细（历史）.凭证序列
        dto.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
        // 27.创建时间 当前时间
        dto.setCreateTime(new Date());
        // 28.更新时间 当前时间
        dto.setUpdateTime(new Date());


//        // 1.流水号: 自动生成，规则为BKS+"YYYYMMDD"+9位序列号
//        // DateTime date = new DateTime(dBUtil.getSysDate());
//        dto.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
//        // 2.账套代码 账务明细（历史）.账套代码
//        dto.setBooksetId(model.getBooksetId());
//        // 3.场景代码 账务明细（历史）.场景代码
//        dto.setTableId(model.getTableId());
//        // 4.交易编号 账务明细（历史）.交易编号
//        dto.setTradeId(model.getTradeId());
//        // 5.业务明细代码 账务明细（历史）.业务明细代码
//        dto.setBusiId(model.getBusiId());
//        // 6.任务代码 账务明细（历史）.任务代码
//        dto.setTaskId(model.getTaskId());
//        // 7.发生日期 账务明细（历史）.发生日期
//        dto.setHappenDate(model.getHappenDate());
//        // 8.入账日期 账务明细（历史）.入账日期
//        dto.setBookDate(bookDate);
//        // 9.基础科目性质 账务明细（历史）.基础科目性质
//        dto.setBaseSubjectNature(model.getBaseSubjectNature());
//        // 10.分支序号 账务明细（历史）.分支序号
//        dto.setBranch(model.getBranch());
//        // 11.科目号 账务明细（历史）.科目号
//        dto.setSubjectNo(model.getSubjectNo());
//        // 12.四级科目号 账务明细（历史）.四级科目号
//        dto.setDetailSubjectNo(model.getDetailSubjectNo());
//        // 13.借贷方向 账务明细（历史）.借贷方向
//        dto.setCdFlag(model.getCdFlag());
//        // 14.原币币种 账务明细（历史）.原币币种
//        dto.setoCcy(model.getoCcy());
//        // 15.原币发生额 返回值.余额 原币发生额 *-1
//        dto.setoHappenAmt(
//                model.getoHappenAmt() == null ? BigDecimal.ZERO : model.getoHappenAmt().negate());
//        // 16.本币币种 账务明细（历史）.本币币种
//        // dto.setbCcy(model.getbCcy());
//        // 17.本币发生额 账务明细（历史）.本币发生额
//        // dto.setbHappenAmt(model.getbHappenAmt());
//        // 18.发生数量 账务明细（历史）.发生数量*-1
//        dto.setHappenNumber(
//                model.getHappenNumber() == null ? BigDecimal.ZERO : model.getHappenNumber().negate());
//        // 19.结息日 账务明细（历史）.结息日
//        dto.setMdate(model.getMdate());
//        // 20.凭证编号 账务明细（历史）.凭证编号
//        dto.setBookId(model.getBookId());
//        // 21.凭证说明 账务明细（历史）.凭证说明
//        dto.setBookRemark(model.getBookRemark());
//        // 22.冲正标识 账务明细（历史）.冲正标识
//        dto.setOffsetFlag(model.getOffsetFlag());
//        // 23.凭证类型 账务明细（历史）.凭证类型
//        dto.setBookType(model.getBookType());
//        // 24.处理序列 账务明细（历史）.凭证序列
//        dto.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
//        // 25.换汇日期 账务明细（历史）.换汇日期
//        dto.setExDate(model.getExDate());
//        // 26.折算汇率 账务明细（历史）.折算汇率
//        dto.setExRate(model.getExRate());
//        // 27.创建时间 当前时间
//        dto.setCreateTime(new Date());
//        // 28.更新时间 当前时间
//        dto.setUpdateTime(new Date());
//        // 账套日期
//        dto.setBooksetDate(model.getBooksetDate());
//        // 账务明细代码
//        dto.setBokDetailId(model.getBokDetailId());
//        // 任务优先级
//        dto.setTaskPri(model.getTaskPri());
    }

    /**
     * 历史表中没有当前表中有的数据，直接复制当前表数据 -》 变量.比对差额DTO = 账务明细（当前）
     *
     * @param dto 账务明细（当前表）
     * @param model 账务明细（历史表）
     * @see
     * @since 1.0
     */
    private void setDto1(BokBookdetailCurrent dto, BokBookdetailCurrent model) {
        BeanUtil.copyPropertiesIgnoreNull(dto, model);
        // 1.流水号: 自动生成，规则为BKS+"YYYYMMDD"+9位序列号
        // DateTime date = new DateTime(dBUtil.getSysDate());
        dto.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));

//        // 1.流水号: 自动生成，规则为BKS+"YYYYMMDD"+9位序列号
//        // DateTime date = new DateTime(dBUtil.getSysDate());
//        dto.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
//        // 2.账套代码 账务明细（当前）.账套代码
//        dto.setBooksetId(model.getBooksetId());
//        // 3.场景代码 账务明细（当前）.场景代码
//        dto.setTableId(model.getTableId());
//        // 4.交易编号 账务明细（当前）.交易编号
//        dto.setTradeId(model.getTradeId());
//        // 5.业务明细代码 账务明细（当前）.业务明细代码
//        dto.setBusiId(model.getBusiId());
//        // 6.任务代码 账务明细（当前）.任务代码
//        dto.setTaskId(model.getTaskId());
//        // 7.发生日期 账务明细（当前）.发生日期
//        dto.setHappenDate(model.getHappenDate());
//        // 8.入账日期 账务明细（当前）.入账日期
//        dto.setBookDate(model.getBookDate());
//        // 9.基础科目性质 账务明细（当前）.基础科目性质
//        dto.setBaseSubjectNature(model.getBaseSubjectNature());
//        // 10.分支序号 账务明细（当前）.分支序号
//        dto.setBranch(model.getBranch());
//        // 11.科目号 账务明细（当前）.科目号
//        dto.setSubjectNo(model.getSubjectNo());
//        // 12.四级科目号 账务明细（当前）.四级科目号
//        dto.setDetailSubjectNo(model.getDetailSubjectNo());
//        // 13.借贷方向 账务明细（当前）.借贷方向
//        dto.setCdFlag(model.getCdFlag());
//        // 14.原币币种 账务明细（当前）.原币币种
//        dto.setoCcy(model.getoCcy());
//        // 15.原币发生额 返回值.余额 原币发生额
//        dto.setoHappenAmt(model.getoHappenAmt());
//        // 16.本币币种 账务明细（当前）.本币币种
//        dto.setbCcy(model.getbCcy());
//        // 17.本币发生额 账务明细（当前）.本币发生额
//        dto.setbHappenAmt(model.getbHappenAmt());
//        // 18.发生数量 账务明细（当前）.发生数量
//        dto.setHappenNumber(model.getHappenNumber());
//        // 19.结息日 账务明细（历史）.结息日
//        dto.setMdate(model.getMdate());
//        // 20.凭证编号 账务明细（当前）.凭证编号
//        dto.setBookId(model.getBookId());
//        // 21.凭证说明 账务明细（当前）.凭证说明
//        dto.setBookRemark(model.getBookRemark());
//        // 22.冲正标识 账务明细（当前）.冲正标识
//        dto.setOffsetFlag(model.getOffsetFlag());
//        // 23.凭证类型 账务明细（当前）.凭证类型
//        dto.setBookType(model.getBookType());
//        // 24.处理序列 账务明细（当前）.凭证序列
//        // 先使用当前表处理序列，排序好后再生出新的
//        dto.setBookOrder(model.getBookOrder());
//        // 25.换汇日期 账务明细（当前）.换汇日期
//        dto.setExDate(model.getExDate());
//        // 26.折算汇率 账务明细（当前）.折算汇率
//        dto.setExRate(model.getExRate());
//        // 27.创建时间 当前时间
//        dto.setCreateTime(model.getCreateTime());
//        // 28.更新时间 当前时间
//        dto.setUpdateTime(model.getUpdateTime());
//        // 账套日期
//        dto.setBooksetDate(model.getBooksetDate());
//        // 账务明细代码
//        dto.setBokDetailId(model.getBokDetailId());
//        // 任务优先级
//        dto.setTaskPri(model.getTaskPri());
    }

    /**
     * 公用方法：账务类共通方法-查询上一工作日
     *
     * @param cdate 查询日期
     * @param calendarId 日历编码
     * @return 日期
     * @see
     * @since 1.0
     */
    public Date getCDate(Date cdate, String calendarId) {
        return accountingCommonMapper.getCDate(cdate, "0", calendarId);
    }

    /**
     * 公用方法：账务类共通方法-是否节假日
     *
     * @param cdate 查询日期
     * @param calendarId 日历编码
     * @return 是否节假日(0：工作日；其他：节假日)
     * @see
     * @since 1.0
     */
    public String getIsHoliday(Date cdate, String calendarId) {
        return accountingCommonMapper.getIsHoliday(cdate, calendarId);
    }

    // /**
    // * 账务明细余额-区间
    // *
    // * @param searchDto 账务明细余额-区间 查询参数Dto
    // * @return 账务明细余额-区间
    // * @see
    // * @since 1.0
    // */
    // public List<SubjectBalance> getBookDetailInterval(BookDetailBalanceInvSearchDto searchDto) {
    // return accountingCommonMapper.getBookDetailInterval(searchDto);
    // }

    // /**
    // * 凭证合并配置查询
    // *
    // * @param booksetId 账套代码
    // * @return 凭证合并配置
    // * @see
    // * @since 1.0
    // */
    // public BookAccountMergeConfig getBookAccountMergeConfig(String booksetId) {
    // List<ValBokBooksetExt> configList = accountingCommonMapper.getBookAccountMergeConfig(
    // booksetId);
    // BookAccountMergeConfig config = new BookAccountMergeConfig();
    // for (ValBokBooksetExt item : configList) {
    // switch (item.getParamCode()) {
    // case "IS_COMB_TRA_VOUC":
    // config.setIsCombTraVouc(item.getParamValue());
    // break;
    // case "IS_COMB_SUP_VOUC":
    // config.setIsCombSupVouc(item.getParamValue());
    // break;
    // case "IS_COMB_SUP_REC":
    // config.setIsCombSupRec(item.getParamValue());
    // break;
    // }
    // }
    // return config;
    // }

    /**
     * 获取最大凭证序号
     *
     * @param vouchYear 年份
     * @param vouchMonth 月份
     * @param booksetId 账套代码
     * @return 最大凭证序号
     * @see
     * @since 1.0
     */
    public String getMaxBookAccountNum(String vouchYear, String vouchMonth, String booksetId) {
        return accountingCommonMapper.getMaxBookAccountNum(vouchYear, vouchMonth, booksetId);
    }

    /**
     * 凭证序号补零
     *
     * @param num 凭证序号
     * @param length 长度
     * @return 凭证序号
     * @see
     * @since 1.0
     */
    public String padBookAccountNum(String num, int length) {
        return StringUtil.leftPad(num, length, "0");
    }

    /**
     * 对应科目查询
     *
     * @param subjectNo 科目号
     * @param booksetId 账套代码
     * @param baseSubjectNature 基础科目性质列表
     * @return 科目信息列表
     * @see
     * @since 1.0
     */
    public List<CorrespondSubjectEntity> getCorrespondSubject(String subjectNo, String booksetId,
                                                              List<String> baseSubjectNature) {
        String booksetIdTmpl = null;
        BokBookset bokBookset = bokBooksetMapper.selectByPrimaryKey(booksetId);
        if(bokBookset!=null){
            booksetIdTmpl = bokBookset.getBooksetIdTmpl();
        }
        return accountingCommonMapper.getCorrespondSubject(subjectNo, booksetIdTmpl,
                baseSubjectNature);
    }
    /**
     * 获取对应科目查询
     *
     * @param subjectNo 科目号
     * @param booksetId 账套代码
     * @return 科目信息列表
     * @see
     * @since 1.0
     */
    public List<BokSubject> getSubjectInfo(Map<String, Object> map) {
        return accountingCommonMapper.getSubjectInfo(map);
    }
    /**
     * 合并场景代码
     *
     * @param tableIdList 场景代码列表
     * @return (场景代码,场景代码说明)
     * @see
     * @since 1.0
     */
    public Tuple2<String, String> mergeTableId(List<String> tableIdList) {
        if (tableIdList.size() == 1) {
            return new Tuple2<String, String>(tableIdList.get(0),
                    SysDataUtil.getNameFromDataDict(DictCode.会计场景代码, tableIdList.get(0)));
        }
        class Config {

            String 合并场景代码;

            String 合并场景代码说明;

            int hashCode;

            String[] 明细场景代码数组;

            public Config(String 合并场景代码, String 合并场景代码说明, String[] 明细场景代码数组) {
                super();
                this.合并场景代码 = 合并场景代码;
                this.合并场景代码说明 = 合并场景代码说明;
                this.明细场景代码数组 = 明细场景代码数组;
                this.hashCode = Arrays.asList(明细场景代码数组).stream().sorted().collect(
                        Collectors.joining("")).hashCode();
            }

            public boolean equals(int hashCode, List<String> tableIdList) {
                if (hashCode == this.hashCode) {
                    if (tableIdList.size() == 明细场景代码数组.length) {
                        for (String tableId : 明细场景代码数组) {
                            if (!tableIdList.contains(tableId)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }

            public int getTableIdListHashCode(List<String> tableIdList) {
                return tableIdList.stream().sorted().collect(Collectors.joining("")).hashCode();
            }
        }
        final Config[] 合并场景代码配置 = {
                new Config("9901", "公允价值损益类债券-还本付息", new String[] {"1306", "1309"}),
                new Config("9902", "摊余成本类-还本付息", new String[] {"1317", "1315"}),
                new Config("9903", "综合收益类债券-还本付息", new String[] {"1326", "1324"}),
                new Config("9904", "摊余成本类-计划资产-还本付息", new String[] {"1504", "1503"}),
                new Config("9904", "摊余成本类-计划资产-还本付息", new String[] {"1504", "1503"}),
                new Config("2513", "债券借贷-融入到期", new String[] {"2513", "2512"}),// 债券借贷交易账务处理合并场景代码 add by zhangyaf 20181129
                new Config("2507", "债券借贷-融出到期", new String[] {"2507", "2504"}),};// 债券借贷交易账务处理合并场景代码 add by zhangyaf 20181129
        int hashCode = 合并场景代码配置[0].getTableIdListHashCode(tableIdList);
        for (Config config : 合并场景代码配置) {
            if (config.equals(hashCode, tableIdList)) {
                return new Tuple2<String, String>(config.合并场景代码, config.合并场景代码说明);
            }
        }
        return new Tuple2<String, String>("", "");
    }

    // /**
    // * 交易最新账务查询
    // *
    // * @param booksetId 账套代码
    // * @param bookDate 入账日
    // * @param tradeId 交易编号
    // * @return 交易最新账务
    // * @see
    // * @since 1.0
    // */
    // public List<BookNewestInfoDto> searchNewestBook(String booksetId, Date bookDate,
    // String tradeId) {
    // return accountingCommonMapper.searchNewestBook(booksetId, bookDate, tradeId);
    // }

    /**
     * 账套参数查询
     *
     * @param booksetId 账套代码
     * @param paramCodeList 参数项 （参考枚举类：账套配置表参数项_XXX ； 如：DictCodeList.账套配置表参数项_交易凭证是否合并.getValue()）
     * @return 账套配置表DTO
     * @see
     * @since 1.0
     */
    public List<BokBooksetExt> getValBokBooksetExt(String booksetId, List<String> paramCodeList) {

        return accountingCommonMapper.getValBokBooksetExt(booksetId, paramCodeList);
    }

    /**
     * 账套参数查询（返回map<参数项，参数值>）
     *
     * @param booksetId 账套代码
     * @param paramCodeList 参数项 （参考枚举类：账套配置表参数项_XXX ； 如：DictCodeList.账套配置表参数项_交易凭证是否合并.getValue()）
     * @return map<参数项，参数值>
     * @see
     * @since 1.0
     */
    public Map<String, Object> getValBokBooksetExtForMap(String booksetId,
                                                         List<String> paramCodeList) {

        // 定义返回map
        Map<String, Object> returnMap = new HashMap<String, Object>();

        // 获取参数项对应的数据
        List<BokBooksetExt> ValBokBooksetExtList = this.getValBokBooksetExt(booksetId,
                paramCodeList);

        // 将获取到的值，转换为map<参数项，参数值>
        for (BokBooksetExt ext : ValBokBooksetExtList) {
            returnMap.put(ext.getParamCode(), ext.getParamValue());// 塞入值 - map<参数项，参数值>
        }

        return returnMap;
    }

    /**
     * 根据【估值小数位处理方式】和【估值小数位位数】，对传递数值进行处理
     *
     * @param value 传递的数值
     * @param valDecType 估值小数位处理方式
     * @param valDecDigits 估值小数位位数
     * @return BigDecimal 处理后的数值
     * @see
     * @since 1.0
     */
    public BigDecimal filterByDecTypeAndDigits(BigDecimal value, String valDecType,
                                               String valDecDigits) {

        // 定义返回值
        BigDecimal result = value == null ? BigDecimal.ZERO : value;//modify by dongjun on 20190509 空判断展开

        /// 传递参数符合要求
        if (valDecType != null && valDecDigits != null && valDecDigits.trim().length() > 0) {
            // 保留的小数位数
            int valDecDigitsNum = Integer.valueOf(valDecDigits);

            if(valDecDigitsNum < 0){
            	return result;
            } else {
                // 判断 估值小数位处理方式
                if (StringUtil.equals(DictCodeList.估值舍入规则_四舍五入.getValue(), valDecType)) {
                    result = result.setScale(valDecDigitsNum, RoundingMode.HALF_UP);
                }
                else if (StringUtil.equals(DictCodeList.估值舍入规则_舍位.getValue(), valDecType)) {
                    result = result.setScale(valDecDigitsNum, RoundingMode.DOWN);
                }
                else if (StringUtil.equals(DictCodeList.估值舍入规则_进位.getValue(), valDecType)) {
                    result = result.setScale(valDecDigitsNum, RoundingMode.UP);
                }
            }
        }

        return result;
    }
    /**
     * 凭证最新账务查询
     *
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param bokDetailId 账务明细代码
     * @param baseSubjectNature 基础科目性质
     * @param tableId 场景代码
     * @param tradeId 交易编号
     * @param happenDate 发生日
     * @param onwayDate 在途日期
     * @param beginHappenCycDate 发生周期开始日
     * @param endHappenCycDate 发生周期结束日
     * @return List<BookLatestInfoDto>
     * @see
     * @since 1.0
     */
    public List<BookLatestInfoDto> getBookLatestInfo(String booksetId, Date bookDate,
                                                     String bokDetailId,
                                                     List<String> baseSubjectNatureList,
                                                     String tableId, String tradeId,
                                                     Date happenDate, Date onwayDate,
                                                     Date beginHappenCycDate,
                                                     Date endHappenCycDate) {


        return getBookLatestInfo(booksetId, bookDate,
                bokDetailId,baseSubjectNatureList, tableId,  tradeId,happenDate,  onwayDate, beginHappenCycDate,endHappenCycDate,null);
    }

    /**
     * 凭证最新账务查询
     *
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param bokDetailId 账务明细代码
     * @param baseSubjectNature 基础科目性质
     * @param tableId 场景代码
     * @param tradeId 交易编号
     * @param happenDate 发生日
     * @param onwayDate 在途日期
     * @param beginHappenCycDate 发生周期开始日
     * @param endHappenCycDate 发生周期结束日
     * @param isadd 数量增加
     * @return List<BookLatestInfoDto>
     * @see
     * @since 1.0
     */
    public List<BookLatestInfoDto> getBookLatestInfo(String booksetId, Date bookDate,
                                                     String bokDetailId,
                                                     List<String> baseSubjectNatureList,
                                                     String tableId, String tradeId,
                                                     Date happenDate, Date onwayDate,
                                                     Date beginHappenCycDate,
                                                     Date endHappenCycDate,
                                                     String isadd) {

        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<String, Object>();
        paMap.put("booksetId", booksetId);
        paMap.put("bookDate", bookDate);
        paMap.put("bokDetailId", bokDetailId);
        paMap.put("baseSubjectNatureList", baseSubjectNatureList);
        paMap.put("tableId", tableId);
        paMap.put("tradeId", tradeId);
        paMap.put("happenDate", happenDate);
        paMap.put("onwayDate", onwayDate);
        paMap.put("offsetFlag", DictCodeList.冲正标识_正常交易.getValue());
        paMap.put("beginHappenCycDate", beginHappenCycDate);
        paMap.put("endHappenCycDate", endHappenCycDate);

        return accountingCommonMapper.getBookLatestInfo(paMap);
    }


    /**
     * 账务类共通方法设计 - 统计分析账务明细汇总查询
     *
     * @param beginDate 查询开始日 ---【非必填】
     * @param endDate 查询结束日 ---【非必填】
     * @param bookDate 入账日 ---【必填】
     * @param booksetId 账套代码 ---【必填】
     * @param bokDetailId 账务明细代码 ---【非必填】
     * @param busiId 业务明细代码---【非必填】
     * @param searchDateList 查询日列表 ---【非必填】
     * @return 账务明细汇总
     * @see
     * @since 1.0
     */
    public BokStatDetPosiiton getCountAnalysis(Date beginDate, Date endDate, Date bookDate,
                                               String booksetId, String bokDetailId, String busiId,
                                               List<Date> searchDateList) {
        return getCountAnalysis(beginDate,endDate,bookDate,booksetId, bokDetailId, busiId, searchDateList,null);
    }

    /**
     * 账务类共通方法设计 - 统计分析账务明细汇总查询
     *
     * @param beginDate 查询开始日 ---【非必填】
     * @param endDate 查询结束日 ---【非必填】
     * @param bookDate 入账日 ---【必填】
     * @param booksetId 账套代码 ---【必填】
     * @param bokDetailId 账务明细代码 ---【非必填】
     * @param busiId 业务明细代码---【非必填】
     * @param searchDateList 查询日列表 ---【非必填】
     * @return 账务明细汇总
     * @see
     * @since 1.0
     */
    public BokStatDetPosiiton getCountAnalysis(Date beginDate, Date endDate, Date bookDate,
                                               String booksetId, String bokDetailId, String busiId,
                                               List<Date> searchDateList,String isPosZero) {
        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<String, Object>();
        paMap.put("beginDate", beginDate);
        paMap.put("endDate", endDate);
        paMap.put("bookDate", bookDate);
        paMap.put("booksetId", booksetId);
        paMap.put("bokDetailId", bokDetailId);
        paMap.put("busiId", busiId);
        paMap.put("isPosZero", isPosZero);
        paMap.put("searchDateList", searchDateList);


        // 数据查询
        List<BokStatDetPosiiton> list = accountingCommonMapper.getCountAnalysis(paMap);

        // 只取第一个
        BokStatDetPosiiton bokSummaryTable = null;
        if (list != null && !list.isEmpty()) {
            bokSummaryTable = list.get(0);
        }

        return bokSummaryTable;
    }

    /**
     * 待计提现金流查询(分层)
     *
     * @param params 批处理待处理任务
     * @param profitType 收益类型
     * @param cashList 现金流类型集合
     * @return List<StayDrawSubprdEntity>
     * @see
     * @since 1.0
     */
    public List<StayDrawSubprdEntity> getStayDrawSubprdInfo(Map<String, Object> params) {
        return accountingCommonMapper.getStayDrawSubprdInfo(params);
    }
    /**
     * 待计提子产品现金流查询_指定帐套的核算主体和TA产品
     *
     * @param params 批处理待处理任务
     * @param profType 收益类型
     * @param cashList 现金流类型集合
     * @return List<StayDrawSubprdEntity>
     * @see
     * @since 1.0
     */
    public List<StayDrawSubprdEntity> getPrdInfoOrTAInfo(Map<String, Object> params,
                                                         String profitType,
                                                         List<String> cashList) {
        return accountingCommonMapper.getPrdInfoOrTAInfo(params);
    }

    /**
     * 凭证最新账务查询
     *
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param bokDetailId 账务明细代码
     * @param baseSubjectNature 基础科目性质
     * @param tableId 场景代码
     * @param tradeId 交易编号
     * @param happenDate 发生日
     * @param onwayDate 在途日期
     * @param beginHappenCycDate 发生周期开始日
     * @param endHappenCycDate 发生周期结束日
     * @return List<BookLatestInfoDto>
     * @see
     * @since 1.0
     */
    public int getBookLatestInfoOnway(String booksetId, Date bookDate, String bokDetailId,
                                      List<String> baseSubjectNatureList, String tableId,
                                      Date happenDate) {

        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<String, Object>();
        paMap.put("booksetId", booksetId);
        paMap.put("bookDate", bookDate);
        paMap.put("bokDetailId", bokDetailId);
        paMap.put("baseSubjectNatureList", baseSubjectNatureList);
        paMap.put("tableId", tableId);
        paMap.put("happenDate", happenDate);
        paMap.put("offsetFlag", DictCodeList.冲正标识_正常交易.getValue());

        return accountingCommonMapper.getBookLatestInfoOnway(paMap);
    }

    /**
     * 最新结息日查询
     *
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param bokDetailId 账务明细代码
     * @param baseSubjectNature 基础科目性质
     * @param tableIdList 场景代码
     * @param tradeId 交易编号
     * @param happenDate 发生日
     * @param onwayDate 在途日期
     * @param beginHappenCycDate 发生周期开始日
     * @param endHappenCycDate 发生周期结束日
     * @return List<BookLatestInfoDto>
     * @see
     * @since 1.0
     */
    public List<BokBookdetailCurrent> getNewestMdate(String booksetId, Date bookDate,
                                                     String bokDetailId,
                                                     List<String> baseSubjectNatureList,
                                                     List<String> tableIdList, String tradeId,
                                                     Date happenDate, Date onwayDate,
                                                     Date beginHappenCycDate,
                                                     Date endHappenCycDate) {

        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<String, Object>();
        paMap.put("booksetId", booksetId);
        paMap.put("bookDate", bookDate);
        paMap.put("bokDetailId", bokDetailId);
        paMap.put("baseSubjectNatureList", baseSubjectNatureList);
        paMap.put("tableIdList", tableIdList);
        paMap.put("tradeId", tradeId);
        paMap.put("happenDate", happenDate);
        paMap.put("onwayDate", onwayDate);
        paMap.put("offsetFlag", DictCodeList.冲正标识_正常交易.getValue());
        paMap.put("beginHappenCycDate", beginHappenCycDate);
        paMap.put("endHappenCycDate", endHappenCycDate);

        return accountingCommonMapper.getNewestMdate(paMap);
    }
    /**
     * 费用场景配置查询
     *
     * @param map
     * @return List<FeeTableSet>
     * @see
     * @since 1.0
     */
    public List<BokFeeTableSet> getFeeTableSetQuery(Map<String,Object> map){

        return accountingCommonMapper.getFeeTableSetQuery(map);
    }

    /**
     * 子产品待计提现金流查询_理财产品
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    public List<StayDrawSubprdEntity> getStayDrawSubprdInfoByPrd(Map<String, Object> params) {
        return accountingCommonMapper.getStayDrawSubprdInfoByPrd(params);
    }

    /**
     * 查找分层子结构
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    public List<StayDrawSubprdEntity> querySubBokAcctInfo(Map<String, Object> params) {
        return accountingCommonMapper.querySubBokAcctInfo(params);
    }

    /**
     * 预期收益型TA中收查询——指定帐套的核算主体和TA产品
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    public List<StayDrawSubprdEntity> queryFinPrdInfoByPrd(Map<String, Object> params) {
        return accountingCommonMapper.queryFinPrdInfoByPrd(params);
    }

    /**
     * 账务类共通方法设计 - 账务明细辅助查询
     *
     * @param searchDate 查询日 【非必填】
     * @param bookDate 入账日 【必填】
     * @param booksetId 账套代码 【必填】
     * @param bokdetailId 账务明细代码 【非必填】
     * @return BokBookdetailCurrentExt
     * @see
     * @since 1.0
     */
    public BokBookdetailCurrentExt queryBokBookdetailCurrentExt(Date searchDate, Date bookDate, String booksetId, String bokdetailId) {
        // 账套代码和入账日不能为空
        if (StringUtil.isEmpty(booksetId) || bookDate == null) {
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("searchDate", searchDate);
        params.put("bookDate", bookDate);
        params.put("booksetId", booksetId);
        params.put("bokdetailId", bokdetailId);

        BokBookdetailCurrentExt dto = accountingCommonMapper.queryBokBookdetailCurrentExt(params);

        return dto;
    }


    /**
     * 根据账务明细代码，获取分层信息
     *
     * @param bokDetailId
     * @return
     */
    public BokAcctLayeringInfo getBokAcctLayeringInfoByBokDetailId(String bokDetailId) {

        BokAcctLayeringInfo result = null;

        if (StringUtil.isNotEmpty(bokDetailId)) {
            result = accountingCommonMapper.getBokAcctLayeringInfoByBokDetailId(bokDetailId);
        }

        return result;
    }

    /**
     * 期初资产净值计算
     * @param startDate 期初日期
     * @param endDate 期末日期
     * @param vDate 起息日
     * @param beginUnitNetValue 期初单位净值
     * @param endBokStatBokPosition 期末资产负债信息
     * @param bokDetailIdList List<账务明细代码>
     * @param bookDate 入账日
     * @param booksetId 账套代码
     * @param accType 核算类型
     * @param baseType 基础类型
     * @return 期初资产净值计算
     * @see
     * @since 1.0
     */
    public BigDecimal calStateBokPositionBegin(Date startDate, Date endDate, Date vDate, BigDecimal beginUnitNetValue,
                                               BokStatBokPosition endBokStatBokPosition, List<String> bokDetailIdList,
                                               Date bookDate, String booksetId, String accType, String baseType){
        // 定义 变量.期初资产净值
        BigDecimal netAssetValueBegin = BigDecimal.ZERO;
        // if 参数.核算类型=“净值型”
        if(DictCodeList.收益类型_净值型.getValue().equals(accType)){
            // 周期内发生的红利再投份额查询
            // 定义 变量.回补周期内红利再投实收资本
            BigDecimal 回补周期内红利再投实收资本 = BigDecimal.ZERO;
            // 周期内发生的红利再投份额查询
            // 定义 变量.红利再投信息 = 调用共通方法查询：凭证最新账务查询（场景代码=“8015”，发生开始日=“参数.期初日期”，发生结束日=“参数.期末日期”，
            // 基础科目性质=“实收资本”，入账日=参数.入账日，账套代码=参数.账套代码） ※变量.红利再投信息按照发生日升序排列
            List<String> listBaseSubjectNature = new ArrayList<>();
            listBaseSubjectNature.add(DictCodeBaseSubjectNatrue.实收资本.getValue());
            List<BookLatestInfoDto> listBookLatestInfo = this.getBookLatestInfo(
                    booksetId, bookDate, null, listBaseSubjectNature,
                    DictCodeListTableId.会计场景代码_产品_净值型_红利再投.getValue(), null, null, null,
                    startDate, endDate);
            // 变量.红利再投信息筛选, 条件:账务明细.账务明细代码 in 参数账务明细代码list.账务明细代码
            if (CollectionUtils.isNotEmpty(listBookLatestInfo)) {
                listBookLatestInfo = listBookLatestInfo.stream().filter(
                        item -> bokDetailIdList.contains(item.getBokDetailId())).collect(
                        Collectors.toList());
                // LOOP 变量.红利再投信息
                for (BookLatestInfoDto item : listBookLatestInfo) {
                    // 定义 变量.回补周期内红利再投实收资本 = 变量.回补周期内红利再投实收资本 +变量.红利再投信息.原币发生额
                    回补周期内红利再投实收资本 = ArithmeticUtil.doAdd(回补周期内红利再投实收资本, item.getoHappenAmt());
                }
            }
            /**MODIFY BY YANYA ON 20190919 期初资产净值计算：输入参数增加“基数类型”，净值型产品支持存续本金计算浮动管理费    START **/
            if(DictCodeList.基数类型_实收资本.getValue().equals(baseType)) {
                // 变量.期初资产净值 = 期末帐套资产负债信息.实收资本-参数.红利再投实收资本总额
                netAssetValueBegin = ArithmeticUtil.subtract(endBokStatBokPosition.getCapital(),
                        回补周期内红利再投实收资本);
            }
            else {
                // if 参数.期初日期 <= 参数.起息日
                if (DateUtil.compareDate(startDate, vDate) <= 0) {
                    // 变量.期初资产净值 = 期末帐套资产负债信息.实收资本-参数.红利再投实收资本总额
                    netAssetValueBegin = ArithmeticUtil.subtract(endBokStatBokPosition.getCapital(),
                            回补周期内红利再投实收资本);
                }
                else {
                    // 变量.期初资产净值 = 参数.期初单位净值*（期末帐套资产负债信息.实收资本-参数.红利再投实收资本总额）
                    netAssetValueBegin = ArithmeticUtil.multiply(beginUnitNetValue,
                            ArithmeticUtil.subtract(endBokStatBokPosition.getCapital(), 回补周期内红利再投实收资本));
                }
            }
            /**MODIFY BY YANYA ON 20190919 期初资产净值计算：输入参数增加“基数类型”，净值型产品支持存续本金计算浮动管理费    END **/
        }
        else if(DictCodeList.收益类型_货币型.getValue().equals(accType)){
            // 周期内发生的红利再投份额查询
            // 定义 变量.回补周期内红利再投实收资本
            BigDecimal 回补周期内红利再投实收资本 = BigDecimal.ZERO;
            // 周期内发生的红利再投份额查询
            // 定义 变量.红利再投信息 = 调用共通方法查询：凭证最新账务查询（场景代码=“8034”，发生开始日=“参数.期初日期”，发生结束日=“参
            // 数.期末日期”，基础科目性质=“实收资本”，入账日=参数.入账日，账套代码=参数.账套代码）  ※变量.红利再投信息按照发生日升序排列
            List<String> listBaseSubjectNature = new ArrayList<>();
            listBaseSubjectNature.add(DictCodeBaseSubjectNatrue.实收资本.getValue());
            List<BookLatestInfoDto> listBookLatestInfo = this.getBookLatestInfo(
                    booksetId, bookDate, null, listBaseSubjectNature,
                    DictCodeListTableId.会计场景代码_产品_货币型_红利再投.getValue(), null, null, null,
                    startDate, endDate);
            // 变量.红利再投信息筛选, 条件:账务明细.账务明细代码 in 参数账务明细代码list.账务明细代码
            if (CollectionUtils.isNotEmpty(listBookLatestInfo)) {
                listBookLatestInfo = listBookLatestInfo.stream().filter(
                        item -> bokDetailIdList.contains(item.getBokDetailId())).collect(
                        Collectors.toList());
                // LOOP 变量.红利再投信息
                for (BookLatestInfoDto item : listBookLatestInfo) {
                    // 定义 变量.回补周期内红利再投实收资本 = 变量.回补周期内红利再投实收资本 +变量.红利再投信息.原币发生额
                    // TISHKBS-1097 当红利再投为负值的时候，系统处理计算错误 start
                    //回补周期内红利再投实收资本 = ArithmeticUtil.doAdd(回补周期内红利再投实收资本, item.getoHappenAmt());
                    if(DictCodeList.借贷方向_贷.getValue().equals(item.getCdFlag())){
                        回补周期内红利再投实收资本 = ArithmeticUtil.doAdd(回补周期内红利再投实收资本, item.getoHappenAmt());
                    }else{
                        回补周期内红利再投实收资本 = ArithmeticUtil.doAdd(回补周期内红利再投实收资本, item.getoHappenAmt().multiply( new BigDecimal(-1)));
                    }
                    // TISHKBS-1097 当红利再投为负值的时候，系统处理计算错误 end
                }
            }
            
            //吉林银行货币型产品计费基数要包含红利再投  add by kyf 20200114
            if("004".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))){
            	回补周期内红利再投实收资本 = BigDecimal.ZERO;
            }
            
            // 变量.期初资产净值 = 期末帐套资产负债信息.实收资本-参数.红利再投实收资本总额
            netAssetValueBegin = ArithmeticUtil.subtract(endBokStatBokPosition.getCapital(),
                    回补周期内红利再投实收资本);
        }
        return netAssetValueBegin;
    }

    /**
     * 年化收益率查询
     *
     * @param accrualDate 本次计提日
     * @param lastAccrualDate 上次计提日
     * @param finprodid 金融产品代码
     * @param feeBasic 计费基础
     * @param days 区间天数
     * @param bookDate 入账日
     * @param booksetId 账套代码
     * @param zcVdate 产品起息日
     * @return
     * @throws BatchException
     * @see
     * @since 1.0
     */
    public BigDecimal getNhsyl(Date accrualDate, Date lastAccrualDate, String finprodid,
                               BigDecimal feeBasic, Long days, Date bookDate, String booksetId,
                               Date zcVdate, String layeringId) {

        BigDecimal 年化收益率 = BigDecimal.ZERO;
        BigDecimal 期初的单位净值 = BigDecimal.ONE;
        BigDecimal 期初的累计净值 = BigDecimal.ONE;
        BigDecimal 期末的累计净值 = BigDecimal.ONE;
        BokStatBokPositionDto position = new BokStatBokPositionDto();
        // 查询日开始
        position.setSdateStart(lastAccrualDate);
        // 查询结束日
        position.setSdateEnd(lastAccrualDate);
        // 入账日
        position.setBookDate(bookDate);
        // 账套
        position.setBooksetId(booksetId);
        // 分层代码
        position.setLayeringId(layeringId);// modify by dongjun on 20190115
        // 上次计提日 < 变量：资产起息日
        if (DateUtil.compareDate(lastAccrualDate, zcVdate) < 0) {
            // 期初的累计净值,默认为1
            期初的累计净值 = BigDecimal.ONE;
            // 期初单位净值，默认为1
            期初的单位净值 = BigDecimal.ONE;
        }
        else {
            List<BokStatBokPosition> valStatBokPositionList = getValStatBokPosition(
                    position);
            // 根据获取的负债信息，获取对应的净值信息
            if (valStatBokPositionList != null && valStatBokPositionList.size() > 0) {
                BokStatBokPosition valStatBokPosition = valStatBokPositionList.get(0);
                期初的累计净值 = ArithmeticUtil.doAdd(valStatBokPosition.getNetUnitValue(),
                        valStatBokPosition.getTotBounsNet());
                期初的单位净值 = valStatBokPosition.getNetUnitValue();
            }
        }
        // 查询日开始
        position.setSdateStart(accrualDate);
        // 查询结束日
        position.setSdateEnd(accrualDate);
        List<BokStatBokPosition> valStatBokPositionListForNowOnce = getValStatBokPosition(
                position);
        if (valStatBokPositionListForNowOnce != null
                && valStatBokPositionListForNowOnce.size() > 0) {
            BokStatBokPosition valStatBokPositionForNowOnce = valStatBokPositionListForNowOnce.get(
                    0);

            // 期末的累计净值 = 本次计提日帐套资产负债信息.费前单位净值 + 本次计提日帐套资产负债信息.累计分红净值
            期末的累计净值 = ArithmeticUtil.doAdd(valStatBokPositionForNowOnce.getNetUnitValueBef(),
                    valStatBokPositionForNowOnce.getTotBounsNet());
        }

        // 年化收益率 = （期末的累计净值-期初的累计净值）/ 期初的单位净值 * 计息基础 / 区间天数
        年化收益率 = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(
                        ArithmeticUtil.divide(ArithmeticUtil.doSub(期末的累计净值, 期初的累计净值), 期初的单位净值), feeBasic),
                new BigDecimal(days));

        return 年化收益率;
    }
    
    public BigDecimal getNhsylHB(Date accrualDate, Date lastAccrualDate, String finprodid,
            BigDecimal feeBasic, Long days, Date bookDate, String booksetId,
            Date zcVdate, String layeringId,BigDecimal 计提结束日费后资产净值) {

    	BigDecimal 年化收益率 = BigDecimal.ZERO;
        BigDecimal 期初的单位净值 = BigDecimal.ONE;
        BigDecimal 期初的累计净值 = BigDecimal.ONE;
        BigDecimal 期末的累计净值 = BigDecimal.ONE;
        BokStatBokPositionDto position = new BokStatBokPositionDto();
        // 查询日开始
        position.setSdateStart(lastAccrualDate);
        // 查询结束日
        position.setSdateEnd(lastAccrualDate);
        // 入账日
        position.setBookDate(bookDate);
        // 账套
        position.setBooksetId(booksetId);
        // 分层代码
        position.setLayeringId(layeringId);// modify by dongjun on 20190115
        // 上次计提日 < 变量：资产起息日
        if (DateUtil.compareDate(lastAccrualDate, zcVdate) < 0) {
            // 期初的累计净值,默认为1
            期初的累计净值 = BigDecimal.ONE;
            // 期初单位净值，默认为1
            期初的单位净值 = BigDecimal.ONE;
        }
        else {
            List<BokStatBokPosition> valStatBokPositionList = getValStatBokPosition(
                    position);
            // 根据获取的负债信息，获取对应的净值信息
            if (valStatBokPositionList != null && valStatBokPositionList.size() > 0) {
                BokStatBokPosition valStatBokPosition = valStatBokPositionList.get(0);
                期初的累计净值 = ArithmeticUtil.doAdd(valStatBokPosition.getNetUnitValue(),
                        valStatBokPosition.getTotBounsNet());
                期初的单位净值 = valStatBokPosition.getNetUnitValue();
            }
        }
        // 查询日开始
        position.setSdateStart(accrualDate);
        // 查询结束日
        position.setSdateEnd(accrualDate);
        List<BokStatBokPosition> valStatBokPositionListForNowOnce = getValStatBokPosition(
                position);
        if (valStatBokPositionListForNowOnce != null
                && valStatBokPositionListForNowOnce.size() > 0) {
            BokStatBokPosition valStatBokPositionForNowOnce = valStatBokPositionListForNowOnce.get(
                    0);

            // 期末的累计净值 = 本次计提日帐套资产负债信息.费前单位净值 + 本次计提日帐套资产负债信息.累计分红净值
            期末的累计净值 = ArithmeticUtil.doAdd(ArithmeticUtil.divide(计提结束日费后资产净值,valStatBokPositionForNowOnce.getCapital()),
                    valStatBokPositionForNowOnce.getTotBounsNet());
        }

        // 年化收益率 = （期末的累计净值-期初的累计净值）/ 期初的单位净值 * 计息基础 / 区间天数
        年化收益率 = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(
                        ArithmeticUtil.divide(ArithmeticUtil.doSub(期末的累计净值, 期初的累计净值), 期初的单位净值), feeBasic),
                new BigDecimal(days));

        return 年化收益率;
    }
    
    
    /**
     * 区间到账应计利息
     *
     * @param calDate 计算日
     * @param booksetId 账套代码
     * @param tradeInfoDto 资产信息
     * @param listCashPlan 付息计划列表
     * @return interestVar 应计利息
     * @return isClean 是否已结清
     * @return isBeforeClean 是否提前结清
     * @throws BatchException
     * @see
     * @since 1.0
     */
    public Map<String,Object> intervalArrivalAccount(Date calDate,String booksetId,TargetFinProduct tradeInfoDto,List<TargetCashPlan> listCashPlan){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("calDate", calDate);
        map.put("booksetId", booksetId);
        map.put("tradeInfoDto", tradeInfoDto);
        map.put("targetCashPlan", listCashPlan);
        BigDecimal interestVar=BigDecimal.ZERO;
        String isClean=DictCodeList.是否_否.getValue();
        String isBeforeClean=DictCodeList.是否_否.getValue();
        List<TrdProductDeal> 交易集合list=accountingCommonMapper.intervalArrivalAccount(map);
        //返回值.应计利息 = sum(金融产品交易.交易金额)
        for(TrdProductDeal deal:交易集合list){
            interestVar=ArithmeticUtil.doAdd(interestVar,deal.getTradeAmt());
        }
        List<TrdProductDeal> 交易已结清list=new ArrayList<TrdProductDeal>();
        List<TrdProductDeal> 交易已提前结清list=new ArrayList<TrdProductDeal>();
        交易已结清list=交易集合list.stream().filter(item ->StringUtil.equals(DictCodeList.是否_是.getValue(), item.getIsClean())).collect(Collectors.toList());
        //返回值.是否已结清 = 是否有一条交易.是否结清 = 是
        if(交易已结清list.size()>0){
            isClean=DictCodeList.是否_是.getValue();
            //返回值.是否提前结清 = 是否有一条交易.是否结清 = 是 && 交易日 < 到期日
            交易已提前结清list=交易已结清list.stream().filter(item -> item.getTradeDate().compareTo(item.getMdate())<0).collect(Collectors.toList());
            if(交易已提前结清list.size()>0){
                isBeforeClean=DictCodeList.是否_是.getValue();
            }
        }
        map.clear();
        map.put("interestVar", interestVar);
        map.put("isClean", isClean);
        map.put("isBeforeClean", isBeforeClean);
        return map;
    }

	/**
	 * 根据账务明细代码，获取分层信息
	 * 
	 * @param bokDetailId
	 * @return
	 */
	public BokAcctLayeringInfo getLayeringInfoByBusiIdAndBranch(String busiId, Long branch) {

		BokAcctLayeringInfo result = null;
		if (StringUtil.isNotEmpty(busiId) && StringUtil.isNotEmpty(branch)) {
			result = accountingCommonMapper.getLayeringInfoByBusiIdAndBranch(busiId, branch);
		}
		return result;
	}

    /**
     * 共通【银行账场景配置查询】
     *
     * @param booksetId
     *            账套代码
     * @param accureType
     *            计提分区
     * @param dayendType
     *            是否日终出账
     * @return
     */
    public List<BokBankSceConfig> getBokBankSceConfig(String booksetId, String accureType, String dayendType) {

        Map<String, Object> param = new HashMap<>();
        param.put("booksetId", booksetId);
        param.put("accureType", accureType);
        param.put("dayendType", dayendType);
        return accountingCommonMapper.getBokBankSceConfig(param);

    }

    /**
     * 银行账计提余额  - 银行账计提账务处理
     * @param finprodId 金融产品代码
     * @param branch 分支序号
     * @param booksetId 账套代码
     * @param tableId 账套代码
     * @param vouchNum 入账日
     * @param subjectNo 科目号
     * @return 银行账计提余额
     */
    public List<BokBankBalance> findBokBankBalance(String finprodId, Long branch, String booksetId, String tableId, Date vouchNum, String subjectNo){
        Map<String, Object> param = new HashMap<>();
        param.put("finprodId", finprodId);
        param.put("branch", branch);
        param.put("booksetId", booksetId);
        param.put("tableId", tableId);
        param.put("vouchNum", vouchNum);
        param.put("subjectNo", subjectNo);
        return accountingCommonMapper.selectBokBankBalance(param);
    }

    /**
     * 科目余额查询 - 银行账计提账务处理
     * @param booksetId 账套代码
     * @param listSubjectType 科目类型
     * @param bookDate 入账日
     * @param listBaseSubjectNature 科目性质
     * @return 当日发生额
     */
    public BigDecimal findSubjectBalance(String booksetId, List<String> listSubjectType, Date bookDate, List<String> listBaseSubjectNature) {
        Map<String, Object> param = new HashMap<>();
        param.put("booksetId", booksetId);
        param.put("listSubjectType", listSubjectType);
        param.put("bookDate", bookDate);
        param.put("listBaseSubjectNature", listBaseSubjectNature);
        return accountingCommonMapper.selectSubjectBalance(param);
    }

    /**
     * 公允价值变动科目余额查询  - 银行账计提账务处理
     * @param booksetId 账套代码
     * @param listSubjectType 科目类型
     * @param bookDate 入账日
     * @param listBaseSubjectNature 科目性质
     * @param isFloat 其他综合收益是否影响公允价值变动flag
     * @return 当日发生额
     */
    public BigDecimal findFloatSubjectBalance(String booksetId, List<String> listSubjectType,  Date bookDate, List<String> listBaseSubjectNature, String isFloat) {
        Map<String, Object> param = new HashMap<>();
        param.put("booksetId", booksetId);
        param.put("listSubjectType", listSubjectType);
        param.put("bookDate", bookDate);
        param.put("listBaseSubjectNature", listBaseSubjectNature);
        param.put("isFloat", isFloat);
        return accountingCommonMapper.selectFloatSubjectBalance(param);
    }

    /** add by yanya on 20191203 增加债券实际成本数量查询方法 start**/
    /**
     * 债券实际成本数量查询
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param happenDate 发生日
     * @param bokDetailId 账务明细代码
     * @param tradeMarket 交易场所
     * @param invAim 投资目的
     * @param taskPri 任务优先级
     * @return
     */
    public BigDecimal findBondActualCostNum(String booksetId, Date bookDate, Date happenDate, String bokDetailId,
                                            String tradeMarket, String invAim, Long taskPri){
        //定义 变量.查询日期 = null
        Date qryDate = null;//happenDate;//发生日
        //if 交易场所 = 数据字典.交易场所_银行间
        if(DictCodeListExt1.交易场所_银行间.getValue().equals(tradeMarket)) {
            qryDate = happenDate;
        }
        //if(交易场所 = 数据字典.交易场所_上海证券交易所 || 交易场所 = 数据字典.交易场所_深圳证券交易所){
        else if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket)
                || DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(tradeMarket)){
            //变量.查询日期 = 参数.发生日 + 1
            qryDate = DateUtil.addDay(happenDate ,1);
        }
        else {
            qryDate = happenDate;
        }
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);// 账套代码
        searchDto.setBookDate(bookDate);// 入账日
        searchDto.setHappenDate(happenDate);// 发生日
        //earchDto.setOnwayDate(qryDate);//在途日期
        searchDto.setBokDetailId(bokDetailId);// 账务明细代码
//        List<String> baseSubjectNatureList = Lists.newArrayList();
//        baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.成本.getValue());
        String baseSubjectNature = "";
        String mrTableId = "";//买入会计场景
        String mcTableId = "";//卖出会计场景
        //if(投资目的 = 数据字典.投资目的_公允价值变动计入当期损益){
        if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(invAim)) {
            // 变量.成本基础科目性质 = 基础科目性质数据字典.公允价值损益类_成本;
            baseSubjectNature = DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue();
            mrTableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
            mcTableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_卖出.getValue();
        }
        // if(投资目的 = 数据字典.投资目的.投资目的_摊余成本类){
        else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(invAim)) {
            //变量.成本基础科目性质 = 基础科目性质数据字典.摊余成本类成本;
            baseSubjectNature = DictCodeBaseSubjectNatrue.摊余成本类成本.getValue();
            mrTableId = DictCodeListTableId.会计场景代码_摊余成本类债券_买入.getValue();
            mcTableId = DictCodeListTableId.会计场景代码_摊余成本类债券_卖出.getValue();
        }
        //if(投资目的 = 数据字典.投资目的_综合收益类成本){
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(invAim)) {
            //变量.成本基础科目性质 = 基础科目性质数据字典.公允价值损益类_成本;
            baseSubjectNature = DictCodeBaseSubjectNatrue.综合收益类成本.getValue();
            mrTableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
            mcTableId = DictCodeListTableId.会计场景代码_综合收益类债券_卖出.getValue();
        }
        searchDto.setBaseSubjectNatureList(Arrays.asList(baseSubjectNature));// 基础科目性质
        searchDto.setInvAimList(Arrays.asList(invAim));//投资目的
        searchDto.setTaskPri(taskPri);// 任务优先级
        //调用账务共通设计-> 【科目余额查询共通】
        List<SubjectBalance> subjectLatestBalanceList = getSubjectLatestBalance(searchDto);
        //定义变量.当日计算成本科目数量
        BigDecimal num = BigDecimal.ZERO;
        if(!StringUtil.isEmptyList(subjectLatestBalanceList)){
            num = subjectLatestBalanceList.get(0).getNum();
        }
        //买入场景
        //调用账务共通设计->【凭证最新账务查询】
        List<BookLatestInfoDto> mrListBookLatestInfoDto = getBookLatestInfo(booksetId, bookDate, bokDetailId,
                Arrays.asList(baseSubjectNature), mrTableId,
                null, null, qryDate, null,null);
        //LOOP 变量.买入账务明细列表->变量.账务明细
        if(!StringUtil.isEmptyList(mrListBookLatestInfoDto)) {
            //变量.当日计算成本科目数量 -= 变量.账务明细.发生数量
            for(BookLatestInfoDto item : mrListBookLatestInfoDto) {
                num = ArithmeticUtil.subtract(num, item.getHappenNumber());
            }
        }

        //卖出场景
        //调用账务共通设计->【凭证最新账务查询】
        List<BookLatestInfoDto> mcListBookLatestInfoDto = getBookLatestInfo(booksetId, bookDate, bokDetailId,
                Arrays.asList(baseSubjectNature), mcTableId,
                null, null, qryDate, null,null);
        //LOOP 变量.卖出账务明细列表->变量.账务明细
        if(!StringUtil.isEmptyList(mcListBookLatestInfoDto)) {
            //变量.当日计算成本科目数量 += 变量.账务明细.发生数量
            for(BookLatestInfoDto item : mcListBookLatestInfoDto) {
                num = ArithmeticUtil.doAdd(num, item.getHappenNumber());
            }
        }

        return num;
    }
    /** add by yanya on 20191203 增加债券实际成本数量查询方法 end**/

}

/*
 * 文件名：SecReclassificationDealService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.secreclassificationdeal.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.MarketPriceDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashCalcRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashPayRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashPlan;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;

/**
 * 债券重分类交易账务处理
 * 
 * @author leipeng
 * @version 1.0 2018年10月22日
 * @see SecReclassificationDealService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecReclassificationDealService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogic bookLogic;

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    @Autowired
    private CalculationCommonService calculationCommonService;
    
    /**
     * SEC_INT_SCALE_BANK对应小数位为空时，默认12位
     */
    public final static String SEC_INT_SCALE_BANK_DEFAULT = "12";
    /**
     * SEC_INT_SCALE_EXCHANGE对应小数位为空时，默认8位
     */
    public final static String SEC_INT_SCALE_EXCHANGE = "8";

    /**
     * process 数据处理
     * 
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param1) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param1.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }
        // reader传递的参数的明细信息
        List<FinProductDealInfoComDTO> acctEntityList = acctEntity.getListFinProductDealInfoComDTO();
        // 没有明细
        if (acctEntityList == null || acctEntityList.size() == 0) {
            return null;
        }
        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景

        // 1.数据准备
        // 变量.成本科目余额
        BigDecimal varCostSubjectAmt = BigDecimal.ZERO;

        // 变量.成本科目数量
        BigDecimal varCostSubjectNum = BigDecimal.ZERO;

        // 变量.应计利息科目余额
        BigDecimal varLxSubjectAmt = BigDecimal.ZERO;

        // 变量.公允价值变动科目余额
        BigDecimal varGySubjectAmt = BigDecimal.ZERO;

        // 变量.溢折价科目余额
        BigDecimal varYzjSubjectAmt = BigDecimal.ZERO;
        
        // 变量.零息贴现应计利息余额
        BigDecimal varLxtxSubjectAmt = BigDecimal.ZERO;

        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTO = acctEntity.getListFinProductDealInfoComDTO(); // 交易明细列表
        Long taskPri = listFinProductDealInfoComDTO.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        // 2.1.成本类科目余额

        String baseSubjectNature_cost = null;
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            baseSubjectNature_cost = DictCodeBaseSubjectNatrue.摊余成本类成本.getValue();
        }
        else if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            baseSubjectNature_cost = DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue();
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            baseSubjectNature_cost = DictCodeBaseSubjectNatrue.综合收益类成本.getValue();
        }

        // 科目余额查询-期待方向
        SubjectBalance costBalance = this.getSubjectBalanceEx(acctEntity.getBooksetId(),
            acctEntity.getBokDetailId(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
            DictCodeList.余额方向_借方.getValue(), baseSubjectNature_cost, taskPri);
        // 变量.成本科目余额 = 科目余额
        varCostSubjectAmt = costBalance.getBalance();
        // 变量.成本科目数量 = 数量
        varCostSubjectNum = costBalance.getNum();

        String baseSubjectNature_Lx = null;
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            baseSubjectNature_Lx = DictCodeBaseSubjectNatrue.摊余成本类债券应计利息.getValue();
        }
        else if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            baseSubjectNature_Lx = DictCodeBaseSubjectNatrue.公允价值损益类应计利息.getValue();
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            baseSubjectNature_Lx = DictCodeBaseSubjectNatrue.综合收益类应计利息.getValue();
        }

        // 2.2.应计利息类科目余额
        // 科目余额查询-期待方向
        SubjectBalance LxBalance = this.getSubjectBalanceEx(acctEntity.getBooksetId(),
            acctEntity.getBokDetailId(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
            DictCodeList.余额方向_借方.getValue(), baseSubjectNature_Lx, taskPri);
        // 变量.应计利息科目余额 = 科目余额
        varLxSubjectAmt = LxBalance.getBalance();

        String baseSubjectNature_GYJZBD = null;
        if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            baseSubjectNature_GYJZBD = DictCodeBaseSubjectNatrue.公允价值损益类_公允价值变动.getValue();
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            baseSubjectNature_GYJZBD = DictCodeBaseSubjectNatrue.综合收益类_公允价值变动.getValue();
        }

        // 2.3.公允价值变动类科目余额
        // 科目余额查询-期待方向
        if (StringUtil.isNotEmpty(baseSubjectNature_GYJZBD)) {
            SubjectBalance GyBalance = this.getSubjectBalanceEx(acctEntity.getBooksetId(),
                acctEntity.getBokDetailId(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
                DictCodeList.余额方向_借方.getValue(), baseSubjectNature_GYJZBD, taskPri);
            // 变量.公允价值变动科目余额 = 科目余额
            varGySubjectAmt = GyBalance.getBalance();
        }

        String baseSubjectNature_LXTZ = null;
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            baseSubjectNature_LXTZ = DictCodeBaseSubjectNatrue.摊余成本类利息调整.getValue();
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            baseSubjectNature_LXTZ = DictCodeBaseSubjectNatrue.综合收益类利息调整.getValue();
        }

        // 2.4.溢折价类科目余额
        // 科目余额查询-期待方向
        if (StringUtil.isNotEmpty(baseSubjectNature_LXTZ)) {
            SubjectBalance YzjBalance = this.getSubjectBalanceEx(acctEntity.getBooksetId(),
                acctEntity.getBokDetailId(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
                DictCodeList.余额方向_借方.getValue(), baseSubjectNature_LXTZ, taskPri);
            // 变量.公允价值变动科目余额 = 科目余额
            varYzjSubjectAmt = YzjBalance.getBalance();
        }

        for (FinProductDealInfoComDTO finProductDealInfoComDTO : listFinProductDealInfoComDTO) {

            // 获取标的信息.金融产品类型
            // //交易类账务处理结果.业务明细代码
            // TargetFinProduct targetFinProduct =
            // this.getTargetFinProduct(finProductDealInfoComDTO.getFinprodId());

            // 获取标的类金融产品信息
            // 查询条件 交易类账务处理结果.业务明细代码
            List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();
            // 标的信息条件
            TargetSearchDto searchBaseinfo = new TargetSearchDto();
            searchBaseinfo.setQueryType(ValDictCodeList.标的_信息.getValue());
            queryCriteria.add(searchBaseinfo);

            // 还本计划条件
            TargetSearchDto searchRedinfo = new TargetSearchDto();
            searchRedinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
            searchRedinfo.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
            queryCriteria.add(searchRedinfo);

            // 付息计划条件
            TargetSearchDto searchIntinfo = new TargetSearchDto();
            searchIntinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
            searchIntinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            queryCriteria.add(searchIntinfo);

            // 报价计划条件
            TargetSearchDto searchCashinfo = new TargetSearchDto();
            searchCashinfo.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
            searchCashinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            queryCriteria.add(searchCashinfo);

            // 现金流支付规则条件
            TargetSearchDto searchPayinfo = new TargetSearchDto();
            searchPayinfo.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
            searchPayinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            queryCriteria.add(searchPayinfo);
            Map<TargetSearchDto, Object> targetSearchMap = finProductCommonService.getTargetInfo(
                finProductDealInfoComDTO.getFinprodId(), queryCriteria);

            // 变量.标的信息 = 上述共通返回标的信息
            TargetFinProduct rstFinproduct = (TargetFinProduct)targetSearchMap.get(searchBaseinfo);

            // 变量.付息列表 = 上述共通返回的计划列表
            @SuppressWarnings("unchecked")
            List<TargetCashPlan> listIntinfo = targetSearchMap.get(
                searchIntinfo) == null ? null : (List<TargetCashPlan>)targetSearchMap.get(
                    searchIntinfo);

            // 变量.还本列表 = 上述共通返回还本列表
            @SuppressWarnings({"unchecked", "unused"})
            List<TargetCashPlan> listRedinfo = targetSearchMap.get(
                searchRedinfo) == null ? null : (List<TargetCashPlan>)targetSearchMap.get(
                    searchRedinfo);

            // 变量.报价列表 = 上述共通返回报价列表
            @SuppressWarnings("unchecked")
            List<TargetCashCalcRule> listCashinfo = targetSearchMap.get(
                searchCashinfo) == null ? null : (List<TargetCashCalcRule>)targetSearchMap.get(
                    searchCashinfo);

            // 变量.支付规则 = 上述共通返回支付规则列表
            @SuppressWarnings("unchecked")
            List<TargetCashPayRule> listPayinfo = targetSearchMap.get(
                searchPayinfo) == null ? null : (List<TargetCashPayRule>)targetSearchMap.get(
                    searchPayinfo);
            // if（交易方向 = "资产重分类"）
            if (finProductDealInfoComDTO.getPs().equals(DictCodeList.交易方向_资产重分类.getValue())) {
                // 2.5.零息贴现应计利息 ---因为标的在for循环内部，故零息贴现在内部定义
                // if（变量.标的信息.息票品种为零息或贴现时 and 重分类投资目的 = "公允价值"）
                String couponSpecies = rstFinproduct.getCouponSpecies();
                if ((DictCodeList.息票品种_零息.getValue().equals(couponSpecies)
                     || DictCodeList.息票品种_贴现.getValue().equals(couponSpecies))
                    && DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(
                            finProductDealInfoComDTO.getReclsInvAim())) {
                    // 1.2 百元券面处理
                    BigDecimal varFaceValue = finProductCommonService.getFaceValue(finProductDealInfoComDTO.getFinprodId(),
                        acctEntity.getHappenDate());
                    AccurIntCalOutParameter accurIntCalOutParameter = this.getCalculate(
                        rstFinproduct, listIntinfo, listCashinfo, listPayinfo,
                        varFaceValue, acctEntity);
                    // 返回值.应计利息做小数位处理
                    BigDecimal interest = accurIntCalOutParameter.getInterest();
                    // if（标的信息.交易场所 = "银行间"）
                    if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(rstFinproduct.getTradeMarket())) {
                        // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
                        interest = ArithmeticUtil.round(interest,
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                "SEC_INT_SCALE_BANK")) ? SEC_INT_SCALE_BANK_DEFAULT : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_BANK")),
                            null);
                    }
                    else {
                        // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_EXCHANGE
                        interest = ArithmeticUtil.round(interest,
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                "SEC_INT_SCALE_EXCHANGE")) ? SEC_INT_SCALE_EXCHANGE : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")),
                            null);
                    }
                    // 变量.零息贴现应计利息余额 =上述返回值 * 变量.成本科目数量 * 变量.百元券面面值 /100 
                    varLxtxSubjectAmt = ArithmeticUtil.multiply(interest, varCostSubjectNum);
                    varLxtxSubjectAmt = ArithmeticUtil.multiply(varLxtxSubjectAmt, varFaceValue);
                    varLxtxSubjectAmt = ArithmeticUtil.divide(varLxtxSubjectAmt,
                        new BigDecimal(100), NumberEnum.HIGH_PREC_NUM.getCode());
                }
                // 初始化定义 - 记账场景要素管理
                AccountDriver ad = new AccountDriver();
                // 共通元素设值 - 设置账务引擎参数
                ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景

                // 初始化定义 - 存放该笔记账的场景要素 map定义
                Map<String, Object> accountSceneMap = new HashMap<>();
                // 初始化定义 - 存放该笔记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountMap = new HashMap<>();
                // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
                Map<String, Object> accountCurrencyMap = new HashMap<>();

                // 调用共通场景元素赋值-交易类场景元素赋值
                accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity,
                    finProductDealInfoComDTO);
                // 登记托管机构 <- 变量.标的信息.登记托管机构
                accountSceneMap.put(BookConstant.REGIST_ORG, rstFinproduct.getRegistOrg());
                // 境内外 <- 变量.标的信息.境内外
                accountSceneMap.put(BookConstant.BLN_AREA, rstFinproduct.getBlnArea());
                // 备注 <- "债券重分类交易账务处理"
                accountSceneMap.put(BookConstant.BOOK_REMARK, "债券重分类交易账务处理");
                // 分类1 <- 变量.交易信息.分类1
                accountSceneMap.put(BookConstant.TYPE_1, rstFinproduct.getType1());
                // 分类2 <- 变量.交易信息.分类2
                accountSceneMap.put(BookConstant.TYPE_2, rstFinproduct.getType2());
                // 分类3 <- 变量.交易信息.分类3
                accountSceneMap.put(BookConstant.TYPE_3, rstFinproduct.getType3());
                // 分类4 <- 变量.交易信息.分类4
                accountSceneMap.put(BookConstant.TYPE_4, rstFinproduct.getType4());
                // 分类5 <- 变量.交易信息.分类5
                accountSceneMap.put(BookConstant.TYPE_5, rstFinproduct.getType5());
                // 分类6 <- 变量.交易信息.分类6
                accountSceneMap.put(BookConstant.TYPE_6, rstFinproduct.getType6());

                BigDecimal marketPrice = null;
                // 调用共通取债券最新市价
                MarketPriceDto marketPriceDto = finProductCommonService.getBondMarketPrice(
                    finProductDealInfoComDTO.getHappenDate(),
                    finProductDealInfoComDTO.getBooksetId(),
                    finProductDealInfoComDTO.getFinprodId(),
                    finProductDealInfoComDTO.getReclsInvAim());
                if (null != marketPriceDto) {
                    marketPrice = marketPriceDto.getPrice();
                }
                // 定义 变量.市价成本
                BigDecimal costPrice;
                // if（返回值.市价 = null）
                if (null == marketPrice
                		|| ArithmeticUtil.equals(marketPrice, BigDecimal.ZERO)) {
                    // 变量.市价成本 = （变量.成本科目余额 + 变量.溢折价科目余额）
                    costPrice = ArithmeticUtil.doAdd(varCostSubjectAmt, varYzjSubjectAmt);
                }
                else {
                    // 变量.市价成本 = 返回值.市价 * 变量.成本科目数量
                    costPrice = ArithmeticUtil.multiply(marketPrice, varCostSubjectNum);
                }

                // //账务明细代码1 <- 交易明细.账务明细代码
                // accountSceneMap.put(BookConstant.BOK_DETAIL_ID1,
                // finProductDealInfoComDTO.getBokDetailId());
                // 账务明细代码2 <- 调用账务明细代码共通（交易明细.金融产品代码,交易明细.金融产品类型，重分类投资目的）
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID2,
                    bookCommonLogic.getValBokDetailIdWithInvAim(
                        finProductDealInfoComDTO.getFinprodId(), null,
                        finProductDealInfoComDTO.getFinprodType(),
                        finProductDealInfoComDTO.getReclsInvAim()));

                // if（投资目的 = "摊余成本" and 重分类投资目的 = "公允价值"）
                if (finProductDealInfoComDTO.getInvAim().equals(
                    DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())
                    && finProductDealInfoComDTO.getReclsInvAim().equals(
                        DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())) {
                    // ad.会计场景代码="摊余成本类重分类为损益类"
                    ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类重分类为损益类.getValue());

                    // accountAmountMap设置如下：
                    // 市价 <- 变量.市价成本
                    accountAmountMap.put(BookConstant.MARKET_PRICE, costPrice);
                    // 应收利息（贷） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_C, varLxSubjectAmt);
                    // 应收利息（借） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_D, varLxSubjectAmt);
                    if (DictCodeList.息票品种_零息.getValue().equals(couponSpecies)
                        || DictCodeList.息票品种_贴现.getValue().equals(couponSpecies)) {
                        // 应收利息（借） <- 变量.零息贴现应计利息余额
                        accountAmountMap.put(BookConstant.REC_INT_D, varLxtxSubjectAmt);
                    }
                    // 成本余额 <- 变量.成本科目余额
                    accountAmountMap.put(BookConstant.COST_BALANCE, varCostSubjectAmt);

                    // 基础科目性质
                    Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                    Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                    accountNatrueBranchMap.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.摊余成本类成本.getValue(),
                        accountNatrueBranchMap);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    Map<String, Object> accountNatrueBranchMap2 = new HashMap<>();
                    accountNatrueBranchMap2.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue(),
                        accountNatrueBranchMap2);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    // if 变量.溢折价科目余额 > 0
                    if (varYzjSubjectAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 利息调整(贷） <- 变量.溢折价科目余额
                        accountAmountMap.put(BookConstant.INTADJ_C, varYzjSubjectAmt);
                    }
                    else {
                        // 利息调整(借） <- 变量.溢折价科目余额 * -1
                        accountAmountMap.put(BookConstant.INTADJ_D,
                            ArithmeticUtil.multiply(varYzjSubjectAmt, new BigDecimal("-1")));
                    }

                    // 定义 变量.轧差金额 = 市价 + 利息调整（借）- 成本余额 - 利息调整（贷）+ 应收利息（借） - 应收利息（贷）
                    BigDecimal zcAmt = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.MARKET_PRICE),
                            accountAmountMap.get(BookConstant.INTADJ_D), accountAmountMap.get(BookConstant.REC_INT_D)),
                        accountAmountMap.get(BookConstant.COST_BALANCE),
                        accountAmountMap.get(BookConstant.INTADJ_C), accountAmountMap.get(BookConstant.REC_INT_C));

                    // 变量.轧差金额 > 0
                    if (zcAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 轧差金额（贷） <- 变量.轧差金额
                        accountAmountMap.put(BookConstant.NETAMT_C, zcAmt);
                    }
                    else {
                        // 轧差金额（借） <- 变量.轧差金额 * -1
                        accountAmountMap.put(BookConstant.NETAMT_D,
                            ArithmeticUtil.multiply(zcAmt, new BigDecimal("-1")));

                    }

                }
                // else if（投资目的 = "摊余成本" and 重分类投资目的 = "综合收益"）
                else if (finProductDealInfoComDTO.getInvAim().equals(
                    DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())
                         && finProductDealInfoComDTO.getReclsInvAim().equals(
                             DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())) {
                    // ad.会计场景代码="摊余成本类重分类为综合收益类"
                    ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类重分类为综合收益类.getValue());

                    // accountAmountMap设置如下：
                    // 面值 <- 交易类账务处理结果.单位面值 * 变量.成本科目数量
                    accountAmountMap.put(BookConstant.PAR_VALUE, ArithmeticUtil.multiply(
                        finProductDealInfoComDTO.getParValue(), varCostSubjectNum));

                    BigDecimal balAmt = accountAmountMap.get(BookConstant.PAR_VALUE).subtract(
                        costPrice);
                    // if (面值 - 变量.市价成本 > 0)
                    if (balAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 综合收益利息调整（贷） <- 面值 - 变量.市价成本
                        accountAmountMap.put(BookConstant.FVOCI_INTADJ_C, balAmt);
                    }
                    else {
                        // 综合收益利息调整（借） <- （面值 - 变量.市价成本） * -1
                        accountAmountMap.put(BookConstant.FVOCI_INTADJ_D,
                            ArithmeticUtil.multiply(balAmt, new BigDecimal("-1")));
                    }

                    // 应收利息（贷） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_C, varLxSubjectAmt);
                    // 应收利息（借） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_D, varLxSubjectAmt);
                    // 成本余额 <- 变量.成本科目余额
                    accountAmountMap.put(BookConstant.COST_BALANCE, varCostSubjectAmt);

                    // 基础科目性质
                    Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                    Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                    accountNatrueBranchMap.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.摊余成本类成本.getValue(),
                        accountNatrueBranchMap);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    Map<String, Object> accountNatrueBranchMap2 = new HashMap<>();
                    accountNatrueBranchMap2.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.综合收益类成本.getValue(),
                        accountNatrueBranchMap2);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    // if 变量.溢折价科目余额 > 0
                    if (varYzjSubjectAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 摊余成本利息调整(贷） <- 变量.溢折价科目余额
                        accountAmountMap.put(BookConstant.AC_INTADJ_C, varYzjSubjectAmt);
                    }
                    else {
                        // 摊余成本利息调整(借） <- 变量.溢折价科目余额 * -1
                        accountAmountMap.put(BookConstant.AC_INTADJ_D,
                            ArithmeticUtil.multiply(varYzjSubjectAmt, new BigDecimal("-1")));

                    }

                    // 定义 变量.轧差金额 = 面值 + 综合收益利息调整（借）+ 摊余成本利息调整(借）- 成本余额 - 综合收益利息调整（贷） - 摊余成本利息调整（贷） + 应收利息(借)- 应收利息(贷)
                    BigDecimal zcAmt = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.PAR_VALUE),
                            accountAmountMap.get(BookConstant.FVOCI_INTADJ_D),
                            accountAmountMap.get(BookConstant.AC_INTADJ_D), accountAmountMap.get(BookConstant.REC_INT_D)),
                        accountAmountMap.get(BookConstant.COST_BALANCE),
                        accountAmountMap.get(BookConstant.FVOCI_INTADJ_C),
                        accountAmountMap.get(BookConstant.AC_INTADJ_C), accountAmountMap.get(BookConstant.REC_INT_C));

                    // 变量.轧差金额 > 0
                    if (zcAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 轧差金额（贷） <- 变量.轧差金额
                        accountAmountMap.put(BookConstant.NETAMT_C, zcAmt);
                    }
                    else {
                        // 轧差金额（借） <- 变量.轧差金额 * -1
                        accountAmountMap.put(BookConstant.NETAMT_D,
                            ArithmeticUtil.multiply(zcAmt, new BigDecimal("-1")));

                    }
                }
                // else if（投资目的 = "综合收益" and 重分类投资目的 = "摊余成本"）
                else if (finProductDealInfoComDTO.getInvAim().equals(
                    DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())
                         && finProductDealInfoComDTO.getReclsInvAim().equals(
                             DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())) {
                    // ad.会计场景代码="综合收益类重分类为摊余成本类"
                    ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类重分类为摊余成本类.getValue());
                    // 面值 <- 交易类账务处理结果.单位面值 * 变量.成本科目数量
                    accountAmountMap.put(BookConstant.PAR_VALUE, ArithmeticUtil.multiply(
                        finProductDealInfoComDTO.getParValue(), varCostSubjectNum));
                    // 应收利息（贷） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_C, varLxSubjectAmt);
                    // 应收利息（借） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_D, varLxSubjectAmt);
                    // 成本余额 <- 变量.成本科目余额
                    accountAmountMap.put(BookConstant.COST_BALANCE, varCostSubjectAmt);

                    // 基础科目性质
                    Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                    Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                    accountNatrueBranchMap.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.综合收益类成本.getValue(),
                        accountNatrueBranchMap);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    Map<String, Object> accountNatrueBranchMap2 = new HashMap<>();
                    accountNatrueBranchMap2.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.摊余成本类成本.getValue(),
                        accountNatrueBranchMap2);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    // if 变量.溢折价科目余额 > 0
                    if (varYzjSubjectAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 摊余成本利息调整(借) <- 变量.溢折价科目余额
                        accountAmountMap.put(BookConstant.AC_INTADJ_D, varYzjSubjectAmt);
                        // 综合收益利息调整(贷) <- 变量.溢折价科目余额
                        accountAmountMap.put(BookConstant.FVOCI_INTADJ_C, varYzjSubjectAmt);
                    }
                    else {
                        // 摊余成本利息调整(贷) <- 变量.溢折价科目余额 * -1
                        accountAmountMap.put(BookConstant.AC_INTADJ_C,
                            ArithmeticUtil.multiply(varYzjSubjectAmt, new BigDecimal("-1")));
                        // 综合收益利息调整(借) <- 变量.溢折价科目余额 * -1
                        accountAmountMap.put(BookConstant.FVOCI_INTADJ_D,
                            ArithmeticUtil.multiply(varYzjSubjectAmt, new BigDecimal("-1")));
                    }
                    // if 变量.公允价值变动余额 > 0
                    if (varGySubjectAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 公允价值变动（贷） <- 变量.公允价值变动科目余额
                        accountAmountMap.put(BookConstant.FVTPL_INTADJ_C, varGySubjectAmt);
                        // 公允价值变动损益（借） <- 变量.公允价值变动科目余额
                        accountAmountMap.put(BookConstant.FLOAT_1_D, varGySubjectAmt);
                    }
                    else {
                        // 公允价值变动（借） <- 变量.公允价值变动科目余额 * -1
                        accountAmountMap.put(BookConstant.FVTPL_INTADJ_D,
                            ArithmeticUtil.multiply(varGySubjectAmt, new BigDecimal("-1")));
                        // 公允价值变动损益（贷） <- 变量.公允价值变动科目余额 * -1
                        accountAmountMap.put(BookConstant.FLOAT_1_C,
                            ArithmeticUtil.multiply(varGySubjectAmt, new BigDecimal("-1")));
                    }
                }
                // else if（投资目的 = "综合收益" and 重分类投资目的 = "公允价值"）
                else if (finProductDealInfoComDTO.getInvAim().equals(
                    DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())
                         && finProductDealInfoComDTO.getReclsInvAim().equals(
                             DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())) {

                    // ad.会计场景代码="综合收益类重分类为损益类"
                    ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类重分类为损益类.getValue());
                    // 市价 <- 变量.市价成本
                    accountAmountMap.put(BookConstant.MARKET_PRICE, costPrice);
                    // 应收利息（贷） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_C, varLxSubjectAmt);
                    // 应收利息（借） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_D, varLxSubjectAmt);
                    if (DictCodeList.息票品种_零息.getValue().equals(couponSpecies)
                        || DictCodeList.息票品种_贴现.getValue().equals(couponSpecies)) {
                        // 应收利息（借） <- 变量.零息贴现应计利息余额
                        accountAmountMap.put(BookConstant.REC_INT_D, varLxtxSubjectAmt);
                    }
                    // 成本余额 <- 变量.成本科目余额
                    accountAmountMap.put(BookConstant.COST_BALANCE, varCostSubjectAmt);

                    // 基础科目性质
                    Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                    Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                    accountNatrueBranchMap.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.综合收益类成本.getValue(),
                        accountNatrueBranchMap);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    Map<String, Object> accountNatrueBranchMap2 = new HashMap<>();
                    accountNatrueBranchMap2.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue(),
                        accountNatrueBranchMap2);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    // if 变量.溢折价科目余额 > 0
                    if (varYzjSubjectAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 利息调整(贷） <- 变量.溢折价科目余额
                        accountAmountMap.put(BookConstant.INTADJ_C, varYzjSubjectAmt);
                    }
                    else {
                        // 利息调整(借） <- 变量.溢折价科目余额
                        accountAmountMap.put(BookConstant.INTADJ_D,
                            ArithmeticUtil.multiply(varYzjSubjectAmt, new BigDecimal("-1")));
                    }
                    // if 变量.公允价值变动余额 > 0
                    if (varGySubjectAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 公允价值变动（贷） <- 变量.公允价值变动科目余额
                        accountAmountMap.put(BookConstant.FVTPL_INTADJ_C, varGySubjectAmt);
                        // 公允价值变动损益（借） <- 变量.公允价值变动科目余额
                        accountAmountMap.put(BookConstant.FLOAT_1_D, varGySubjectAmt);
                    }
                    else {
                        // 公允价值变动（借） <- 变量.公允价值变动科目余额 * -1
                        accountAmountMap.put(BookConstant.FVTPL_INTADJ_D,
                            ArithmeticUtil.multiply(varGySubjectAmt, new BigDecimal("-1")));
                        // 公允价值变动损益（贷） <- 变量.公允价值变动科目余额 * -1
                        accountAmountMap.put(BookConstant.FLOAT_1_C,
                            ArithmeticUtil.multiply(varGySubjectAmt, new BigDecimal("-1")));
                    }

                    // 定义 变量.轧差金额 = 市价 + 利息调整（借）- 成本余额 - 利息调整（贷） + 应收利息(借)- 应收利息(贷)
                    BigDecimal zcAmt = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.MARKET_PRICE),
                            accountAmountMap.get(BookConstant.INTADJ_D), accountAmountMap.get(BookConstant.REC_INT_D)),
                        accountAmountMap.get(BookConstant.COST_BALANCE),
                        accountAmountMap.get(BookConstant.INTADJ_C), accountAmountMap.get(BookConstant.REC_INT_C));

                    // 变量.轧差金额 > 0
                    if (zcAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 轧差金额（贷） <- 变量.轧差金额
                        accountAmountMap.put(BookConstant.NETAMT_C, zcAmt);
                    }
                    else {
                        // 轧差金额（借） <- 变量.轧差金额 * -1
                        accountAmountMap.put(BookConstant.NETAMT_D,
                            ArithmeticUtil.multiply(zcAmt, new BigDecimal("-1")));

                    }

                }
                // else if（投资目的 = "公允价值" and 重分类投资目的 = "摊余成本"）
                else if (finProductDealInfoComDTO.getInvAim().equals(
                    DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())
                         && finProductDealInfoComDTO.getReclsInvAim().equals(
                             DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())) {

                    // ad.会计场景代码="损益类重分类为摊余成本类"
                    ad.setTableId(DictCodeListTableId.会计场景代码_损益类重分类为摊余成本类.getValue());
                    // 面值 <- 交易类账务处理结果.单位面值 * 变量.成本科目数量
                    accountAmountMap.put(BookConstant.PAR_VALUE, ArithmeticUtil.multiply(
                        finProductDealInfoComDTO.getParValue(), varCostSubjectNum));
                    // 应收利息（贷） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_C, varLxSubjectAmt);
                    // 应收利息（借） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_D, varLxSubjectAmt);
                    if (DictCodeList.息票品种_零息.getValue().equals(couponSpecies)
                        || DictCodeList.息票品种_贴现.getValue().equals(couponSpecies)) {
                        // 应收利息（借） <- 变量.零息贴现应计利息余额
                        accountAmountMap.put(BookConstant.REC_INT_D, varLxtxSubjectAmt);
                    }
                    // 成本余额 <- 变量.成本科目余额
                    accountAmountMap.put(BookConstant.COST_BALANCE, varCostSubjectAmt);

                    // 基础科目性质
                    Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                    Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                    accountNatrueBranchMap.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue(),
                        accountNatrueBranchMap);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    Map<String, Object> accountNatrueBranchMap2 = new HashMap<>();
                    accountNatrueBranchMap2.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.摊余成本类成本.getValue(),
                        accountNatrueBranchMap2);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    BigDecimal balAmt = accountAmountMap.get(BookConstant.PAR_VALUE).subtract(
                        costPrice);
                    // if (面值 - 变量.市价成本 > 0)
                    if (balAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 利息调整（贷） <- 面值 - 变量.市价成本
                        accountAmountMap.put(BookConstant.INTADJ_C, balAmt);
                    }
                    else {
                        // 利息调整（借） <- （面值 - 变量.市价成本）* -1
                        accountAmountMap.put(BookConstant.INTADJ_D,
                            ArithmeticUtil.multiply(balAmt, new BigDecimal("-1")));
                    }

                    // if 变量.公允价值变动余额 > 0
                    if (varGySubjectAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 公允价值变动（贷） <- 变量.公允价值变动科目余额
                        accountAmountMap.put(BookConstant.FVTPL_INTADJ_C, varGySubjectAmt);
                        // 公允价值变动损益（借） <- 变量.公允价值变动科目余额
                        accountAmountMap.put(BookConstant.FLOAT_1_D, varGySubjectAmt);
                    }
                    else {
                        // 公允价值变动（借） <- 变量.公允价值变动科目余额 * -1
                        accountAmountMap.put(BookConstant.FVTPL_INTADJ_D,
                            ArithmeticUtil.multiply(varGySubjectAmt, new BigDecimal("-1")));
                        // 公允价值变动损益（贷） <- 变量.公允价值变动科目余额 * -1
                        accountAmountMap.put(BookConstant.FLOAT_1_C,
                            ArithmeticUtil.multiply(varGySubjectAmt, new BigDecimal("-1")));
                    }

                    // 定义 变量.轧差金额 = 面值 + 利息调整（借）- 成本余额 - 利息调整（贷） + 应收利息(借)- 应收利息(贷)
                    BigDecimal zcAmt = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.PAR_VALUE),
                            accountAmountMap.get(BookConstant.INTADJ_D), accountAmountMap.get(BookConstant.REC_INT_D)),
                        accountAmountMap.get(BookConstant.COST_BALANCE),
                        accountAmountMap.get(BookConstant.INTADJ_C), accountAmountMap.get(BookConstant.REC_INT_C));

                    // 变量.轧差金额 > 0
                    if (zcAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 轧差金额（贷） <- 变量.轧差金额
                        accountAmountMap.put(BookConstant.NETAMT_C, zcAmt);
                    }
                    else {
                        // 轧差金额（借） <- 变量.轧差金额 * -1
                        accountAmountMap.put(BookConstant.NETAMT_D,
                            ArithmeticUtil.multiply(zcAmt, new BigDecimal("-1")));

                    }

                }
                // else if（投资目的 = "公允价值" and 重分类投资目的 = "综合收益"）
                else if (finProductDealInfoComDTO.getInvAim().equals(
                    DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())
                         && finProductDealInfoComDTO.getReclsInvAim().equals(
                             DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())) {

                    // ad.会计场景代码="损益类重分类为综合收益类"
                    ad.setTableId(DictCodeListTableId.会计场景代码_损益类重分类为综合收益类.getValue());
                    // 面值 <- 交易类账务处理结果.单位面值 * 变量.成本科目数量
                    accountAmountMap.put(BookConstant.PAR_VALUE, ArithmeticUtil.multiply(
                        finProductDealInfoComDTO.getParValue(), varCostSubjectNum));
                    // 应收利息（贷） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_C, varLxSubjectAmt);
                    // 应收利息（借） <- 变量.应计利息科目余额
                    accountAmountMap.put(BookConstant.REC_INT_D, varLxSubjectAmt);
                    if (DictCodeList.息票品种_零息.getValue().equals(couponSpecies)
                        || DictCodeList.息票品种_贴现.getValue().equals(couponSpecies)) {
                        // 应收利息（借） <- 变量.零息贴现应计利息余额
                        accountAmountMap.put(BookConstant.REC_INT_D, varLxtxSubjectAmt);
                    }
                    // 成本余额 <- 变量.成本科目余额
                    accountAmountMap.put(BookConstant.COST_BALANCE, varCostSubjectAmt);

                    // 基础科目性质
                    Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                    Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                    accountNatrueBranchMap.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue(),
                        accountNatrueBranchMap);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    Map<String, Object> accountNatrueBranchMap2 = new HashMap<>();
                    accountNatrueBranchMap2.put(BookConstant.HAPPEN_NUMBER, varCostSubjectNum);
                    accountNatrueMap.put(DictCodeBaseSubjectNatrue.综合收益类成本.getValue(),
                        accountNatrueBranchMap2);
                    ad.setAccountNatrueMap(accountNatrueMap);

                    BigDecimal balAmt = accountAmountMap.get(BookConstant.PAR_VALUE).subtract(
                        costPrice);
                    // if (面值 - 变量.市价成本 > 0)
                    if (balAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 利息调整（贷） <- 面值 - 变量.市价成本
                        accountAmountMap.put(BookConstant.INTADJ_C, balAmt);
                    }
                    else {
                        // 利息调整（借） <- （面值 - 变量.市价成本）* -1
                        accountAmountMap.put(BookConstant.INTADJ_D,
                            ArithmeticUtil.multiply(balAmt, new BigDecimal("-1")));
                    }

                    // if 变量.公允价值变动余额 > 0
                    if (varGySubjectAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 公允价值变动（贷） <- 变量.公允价值变动科目余额
                        accountAmountMap.put(BookConstant.FVTPL_INTADJ_C, varGySubjectAmt);
                        // 公允价值变动损益（借） <- 变量.公允价值变动科目余额
                        accountAmountMap.put(BookConstant.FLOAT_1_D, varGySubjectAmt);
                    }
                    else {
                        // 公允价值变动（借） <- 变量.公允价值变动科目余额 * -1
                        accountAmountMap.put(BookConstant.FVTPL_INTADJ_D,
                            ArithmeticUtil.multiply(varGySubjectAmt, new BigDecimal("-1")));
                        // 公允价值变动损益（贷） <- 变量.公允价值变动科目余额 * -1
                        accountAmountMap.put(BookConstant.FLOAT_1_C,
                            ArithmeticUtil.multiply(varGySubjectAmt, new BigDecimal("-1")));
                    }

                    // 定义 变量.轧差金额 = 面值 + 利息调整（借）- 成本余额 - 利息调整（贷） + 应收利息(借)- 应收利息(贷)
                    BigDecimal zcAmt = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.PAR_VALUE),
                            accountAmountMap.get(BookConstant.INTADJ_D), accountAmountMap.get(BookConstant.REC_INT_D)),
                        accountAmountMap.get(BookConstant.COST_BALANCE),
                        accountAmountMap.get(BookConstant.INTADJ_C), accountAmountMap.get(BookConstant.REC_INT_C));

                    // 变量.轧差金额 > 0
                    if (zcAmt.compareTo(BigDecimal.ZERO) > 0) {
                        // 轧差金额（贷） <- 变量.轧差金额
                        accountAmountMap.put(BookConstant.NETAMT_C, zcAmt);
                    }
                    else {
                        // 轧差金额（借） <- 变量.轧差金额 * -1
                        accountAmountMap.put(BookConstant.NETAMT_D,
                            ArithmeticUtil.multiply(zcAmt, new BigDecimal("-1")));

                    }

                }

                //// 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, finProductDealInfoComDTO.getCcy()); // 费用计提 <-
                                                                                       // 交易明细.交易币种
                }

                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
                returnResult.getValBokBookdetailBatch().addAll(
                    resultNowNow.getValBokBookdetailBatch());

            }

        }

        return returnResult;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri 任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalanceEx(String booksetId, String bokDetailId,
                                               Date happenDate, Date bookDate,
                                               String expBalanceDirect, String baseSubjectNature,
                                               Long taskPri) {
        // 定义返回的科目余额
        SubjectBalance balance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0);
        }
        else {
            balance.setBalance(BigDecimal.ZERO);
            balance.setNum(BigDecimal.ZERO);
        }

        return balance;
    }

    /**
     * 获取标的基本信息
     * 
     * @param bokDetailId 账务明细代码
     * @return TargetFinProduct 标的基本信息
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unused")
    private TargetFinProduct getTargetFinProduct(String bokDetailId) {
        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
        // 定义查询类型 实体类
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto);

        // 获取标的信息
        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(bokDetailId,
            targetSearchDtoList);
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);

        // 确保不为null
        if (targetFinProduct == null) {
            targetFinProduct = new TargetFinProduct();
        }

        return targetFinProduct;
    }
    
    private AccurIntCalOutParameter getCalculate(TargetFinProduct rstBaseinfo,
                                                 List<TargetCashPlan> listIntinfo,
                                                 List<TargetCashCalcRule> listCashinfo,
                                                 List<TargetCashPayRule> listPayinfo,
                                                 BigDecimal varFaceValue,
                                                 FinProductDealInfoResult acctEntity) {
        DrawBaseInfoSelectComResult result = new DrawBaseInfoSelectComResult();
        result.setHappenDate(acctEntity.getHappenDate());
        return calculationCommonService.getCalculateNotPlusDay(null, rstBaseinfo, listIntinfo, listCashinfo, listPayinfo,
            varFaceValue, result);
    }
}

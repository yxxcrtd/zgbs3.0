/*
 * 文件名：IrsTransBookNewService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5
 * 修改人：殷亚
 * 修改时间：2022年4月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.batch.irstransbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.LossAmtInputDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 利率互换交易账务处理
 *
 * @author 殷亚
 * @version 1.0
 * @date 2022/4/22 14:35
 * @see
 */
@Service
@Scope("prototype")
public class IrsTransBookNewService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的共通方法类
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 计算类共通方法
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

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
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult) param1.get(
                GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }
        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();
        // 防止报空指针异常
        if (returnResult.getValBokBookdetailBatch() == null || acctEntity.getListFinProductDealInfoComDTO() == null
                || acctEntity.getListFinProductDealInfoComDTO().isEmpty()) {
            return null;
        }
        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailId = acctEntity.getBokDetailId();// 账务明细代码
        TreeNode bookSetTreeNodNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 金融产品交易信息list
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）
        String standardCcyNow = acctEntity.getStandardCcy();
        // 3. 交易处理
        // 循环 交易类账务处理结果.交易明细列表,进行逻辑处理
        for (FinProductDealInfoComDTO dealNow : listFinProductDealInfoComDTONow) {
            // 利率互换交易开仓，包括表外记账、费用交易记账两部分，交易日处理
            if (DictCodeList.交易方向_买入.getValue().equals(dealNow.getPs())) {
                // 表外登记记账凭证生成
                AccountDriver adbuy = getAccountDriverBuy(acctEntity,booksetIdNow, bokDetailId, bookSetTreeNodNow, happenDateNow, bookDateNow, standardCcyNow, dealNow);
                BookGenerateResult resultBuy = bookLogicNew.genAccount(adbuy, new SysBatchExecInfo());
                returnResult.getValBokBookdetailBatch().addAll(
                        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                        resultBuy.getValBokBookdetailBatch());
                // 首期开仓记账凭证生成
                AccountDriver adfee = getAccountDriverFee(acctEntity,booksetIdNow, bokDetailId, bookSetTreeNodNow, happenDateNow, bookDateNow, standardCcyNow, dealNow);
                BookGenerateResult resultFee = bookLogicNew.genAccount(adfee, new SysBatchExecInfo());
                returnResult.getValBokBookdetailBatch().addAll(
                        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                        resultFee.getValBokBookdetailBatch());

            }
            // 交易方向 = 收息到账
            else if (DictCodeList.交易方向_收息到账.getValue().equals(dealNow.getPs())) {

            } else if (DictCodeList.交易方向_还本到账.getValue().equals(dealNow.getPs())) {

            }


        }

        return returnResult;
    }

    /**
     * 首期交易记账场景
     *
     * @param booksetIdNow      账套代码
     * @param bokDetailId       账务明细代码
     * @param bookSetTreeNodNow 会计场景
     * @param happenDateNow     发生日期
     * @param bookDateNow       入账日期
     * @param standardCcyNow    本币位
     * @param dealNow           交易信息
     * @return com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver
     * @author 殷亚
     * @date 2022/4/22 15:46
     * @version 1.0
     * @see
     */
    private AccountDriver getAccountDriverBuy(FinProductDealInfoResult acctEntity,String booksetIdNow, String bokDetailId, TreeNode bookSetTreeNodNow, Date happenDateNow, Date bookDateNow, String standardCcyNow, FinProductDealInfoComDTO dealNow) {
        // 获取标的信息.金融产品类型
        AccountDriver ad = new AccountDriver();// 凭证类型、账套代码、交易编号全部去掉了。
        ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        // 调用共通场景元素赋值-交易类场景元素
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 备注 = 利率互换交易登账
        accountSceneMap.put(BookConstant.BOOK_REMARK,
                "利率互换交易日表外登记");
        // 设置账务引擎参数
        ad.setTableId(DictCodeListTableId.会计场景代码_利率互换_交易日表外登记.getValue());// ad.会计场景代码=会计场景代码_利率互换_交易日表外登记;
        // 定义 变量.面值 = 交易明细.名义本金
        BigDecimal varMz = dealNow.getPrinAmt();
        // 定义 变量.应计利息 = 交易明细.利息总额
        // 记账金额元素map塞值
        // 本金金额 <- 交易明细.交易本金
        accountAmountMap.put(BookConstant.COST,
                ArithmeticUtil.round(varMz, NumberEnum.MONEY));
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        /** 本位币 币种 **/
        accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
        // 记账金额元素 putALl 随记账本位币金额map
        //accountAmountMap.putAll(accountAmountBCCYAMTMap);
        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        return ad;
    }


    /**
     * 首期交易费用记账场景
     *
     * @param booksetIdNow      账套代码
     * @param bokDetailId       账务明细代码
     * @param bookSetTreeNodNow 会计场景
     * @param happenDateNow     发生日期
     * @param bookDateNow       入账日期
     * @param standardCcyNow    本币位
     * @param dealNow           交易信息
     * @return com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver
     * @author 殷亚
     * @date 2022/4/22 15:46
     * @version 1.0
     * @see
     */
    private AccountDriver getAccountDriverFee(FinProductDealInfoResult acctEntity,String booksetIdNow, String bokDetailId, TreeNode bookSetTreeNodNow, Date happenDateNow, Date bookDateNow, String standardCcyNow, FinProductDealInfoComDTO dealNow) {
        // 获取标的信息.金融产品类型
        AccountDriver ad = new AccountDriver();// 凭证类型、账套代码、交易编号全部去掉了。
        ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        // 调用共通场景元素赋值-交易类场景元素
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 调用共通场景元素赋值-交易类场景元素
        //accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // accountSceneMap塞值
        // 备注 = 利率互换交易登账
        accountSceneMap.put(BookConstant.BOOK_REMARK,
                "利率互换首期交易");
        // 设置账务引擎参数
        ad.setTableId(DictCodeListTableId.会计场景代码_利率互换_首期交易.getValue());// ad.会计场景代码=会计场景代码_利率互换_交易日表外登记;

        List<TrdTradeFeeDetail> feeList=dealNow.getValTradeFeeDetailList();
        if(!CollectionUtils.isEmpty(feeList)){
            for(TrdTradeFeeDetail fee:feeList){
                if(DictCodeListExt1.一次性费用类型_交易手续费.getValue().equals(fee.getFeeType())){
                    if(DictCodeListExt1.是否_否.getValue().equals(fee.getIsPayWithTrade())){
                        // 交易手续费
                        accountAmountMap.put(BookConstant.TRADEFEE_C, fee.getFeeAmt());
                        accountAmountMap.put(BookConstant.TRADEFEE_D, fee.getFeeAmt());
                    }
                }
                if(DictCodeListExt1.一次性费用类型_结算费.getValue().equals(fee.getFeeType())){
                    if(DictCodeListExt1.是否_否.getValue().equals(fee.getIsPayWithTrade())){
                        // 结算手续费
                        accountAmountMap.put(BookConstant.SETTLEMENTFEE_C, fee.getFeeAmt());
                        accountAmountMap.put(BookConstant.SETTLEMENTFEE_D, fee.getFeeAmt());
                    }
                }
            }

        }
        // 费用的记账币种直接取人民币
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, standardCcyNow);
        }
        /** 本位币 币种 **/
        accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
        // 记账金额元素 putALl 随记账本位币金额map
        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        return ad;
    }


    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param acctEntity
     * @param booksetIdNow
     * @param bokDetailId
     * @param taskPri
     * @param standardCcyNow
     * @param otherBjBalanceMap
     * @param dealNow
     * @param accountSceneMap
     * @param accountAmountMap
     * @param accountCurrencyMap
     * @see
     * @since 1.0
     */
    private void cleanYskbj(FinProductDealInfoResult acctEntity,
                            String booksetIdNow, String bokDetailId, Long taskPri,
                            String standardCcyNow, Map<Long, BigDecimal> otherBjBalanceMap,
                            FinProductDealInfoComDTO dealNow, Map<String, Object> accountSceneMap,
                            Map<String, BigDecimal> accountAmountMap,
                            Map<String, Object> accountCurrencyMap
                            /**add by wujie on 20201031 start**/
            , BigDecimal glb_qtyskbjsum //其他应收款本金汇总余额
            , BigDecimal glb_qilxsum //逾期利息汇总余额
                            /**add by wujie on 20201031 end**/
            , Map<Long, BigDecimal> fxBalanceMap
    ) {
        // nvl(交易明细.汇总交易编号,交易明细.交易编号);
        if (StringUtil.isNotEmpty(dealNow.getTotleTradeId())) {
            accountSceneMap.put(BookConstant.TRADE_ID, dealNow.getTotleTradeId());
        }
        // 定义 变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)
        BigDecimal yskbjYe = otherBjBalanceMap.get(dealNow.getBranch());
        // if（变量.其他应收款-本金科目余额 == null）
        if (yskbjYe == null) {
            yskbjYe = BigDecimal.ZERO;
            // 查询交易的到期日对应的本金
            SubjectBalance otherBjBalance = this.getSubjectBalance(booksetIdNow,
                    bokDetailId, dealNow.getHappenDate(), dealNow.getBooksetDate(),
                    DictCodeList.余额方向_借方.getValue(),
                    DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), dealNow.getBranch(),
                    taskPri);
            if (otherBjBalance != null) {
                // 变量.其他应收款-本金科目余额 = 上述共通返回的科目余额
                yskbjYe = otherBjBalance.getBalance();
            }
            // 变量.其他应收款-利息科目余额MAP.put(交易明细.分支序号，变量.其他应收款-利息科目余额)
            otherBjBalanceMap.put(dealNow.getBranch(), yskbjYe);
        }
        // 定义 变量.逾期利息科目余额 
        BigDecimal bjfxYe = fxBalanceMap.get(dealNow.getBranch());
        if (bjfxYe == null) {
            bjfxYe = BigDecimal.ZERO;
            SubjectBalance bjfxBalance = this.getSubjectBalance(booksetIdNow, bokDetailId,
                    dealNow.getHappenDate(), dealNow.getBookDate(), DictCodeList.余额方向_借方.getValue(),
                    DictCodeBaseSubjectNatrueNew.应收逾期款_利息.getValue(), dealNow.getBranch(), taskPri);
            if (bjfxBalance != null) {
                // 变量.其他应收款-本金科目余额 = 上述共通返回的科目余额
                bjfxYe = bjfxBalance.getBalance();
            }
            fxBalanceMap.put(dealNow.getBranch(), bjfxYe);
        }
        // 定义 变量.清理其他应收款-本金 = 0
        BigDecimal yskbj = BigDecimal.ZERO;
        // 定义 变量.清理罚息-利息 = 0
        BigDecimal bjfx = BigDecimal.ZERO;
        // if (交易明细.是否结清 = "是")
        if (DictCodeList.是否_是.getValue().equals(dealNow.getIsClean())) {
            yskbj = yskbjYe;
            bjfx = bjfxYe;
        }
        // 变量.减值损失 = 变量.清理面值+变量.清理减值准备+变量.清理应计利息
        BigDecimal jzss = ArithmeticUtil.doAdd(yskbj, bjfx);
        // 应收款项-本金 <- 变量.清理其他应收款-本金
        accountAmountMap.put(BookConstant.OTHER_COST,
                ArithmeticUtil.round(yskbj, NumberEnum.MONEY));
        // 逾期资产应计利息 <- 变量.清理罚息-利息
        accountAmountMap.put(BookConstant.OVERINTAMT,
                ArithmeticUtil.round(bjfx, NumberEnum.MONEY));
        // if（变量.减值损失 >= 0）
        if (jzss.compareTo(BigDecimal.ZERO) >= 0) {
            // 减值损失(借) <- 变量.减值损失
            accountAmountMap.put(BookConstant.IMPAMTLOSS_D,
                    ArithmeticUtil.round(jzss, NumberEnum.MONEY));
        } else {
            // 减值损失(贷) <- 变量.减值损失*-1
            accountAmountMap.put(BookConstant.IMPAMTLOSS_C, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(jzss, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        // 因为与记账金额元素map的key及数量一致，故采用循环
        for (String keyNow : accountAmountMap.keySet()) {
            accountCurrencyMap.put(keyNow, dealNow.getCcy());// value值塞入当前交易币种
        }

        // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        BigDecimal 汇率 = BigDecimal.ONE;
        if (!dealNow.getCcy().equals(acctEntity.getStandardCcy())) {
            // 调用计算类 方法共通【汇率查询】
            // 基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
            // 汇率 = calculationCommonService.queryExchangeRateForTrade(dealNow.getCcy(),
            // standardCcyNow, happenDateNow);
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                    acctEntity.getBookDate(), acctEntity.getBooksetId(), standardCcyNow,
                    dealNow.getCcy());
            if (!StringUtil.isEmptyList(currencyMarketList)
                    && currencyMarketList.get(0) != null) {
                汇率 = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(
                            calculationCommonService.doMultiply(accountAmountMap.get(key), 汇率),
                            NumberEnum.MONEY));
        }
        // 循环本币金额元素，赋值本币
        for (String key : accountAmountBCCYAMTMap.keySet()) {
            accountCurrencyMap.put(key, standardCcyNow);// value值塞入当前交易币种
        }
        /** 本位币 币种 **/
        accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
        // 记账金额元素 putALl 随记账本位币金额map
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        // accountAmountMap再次设置如下：
        // 变量.减值损失_本位币 = 金额元素.应收款项-本金_本位币+金额元素.逾期资产应计利息_本位币
        BigDecimal jzssB = ArithmeticUtil.doAdd(
                accountAmountMap.get(BookConstant.OTHER_COST + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.OVERINTAMT + BookConstant.BCCYAMT));
        // if（变量.减值损失_本位币 >= 0）
        if (jzssB.compareTo(BigDecimal.ZERO) >= 0) {
            // 减值损失(借)_本位币 <- 变量.减值损失_本位币
            accountAmountMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(jzssB, NumberEnum.MONEY));
            accountCurrencyMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT,
                    standardCcyNow);// value值塞入当前交易币种
        } else {
            // 减值损失(贷)_本位币 <- 变量.减值损失_本位币*-1
            accountAmountMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(jzssB, new BigDecimal(-1)),
                            NumberEnum.MONEY));
            accountCurrencyMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT,
                    standardCcyNow);
        }
        // 全局变量的更新
        // 变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 交易明细.全价总额
        yskbjYe = ArithmeticUtil.doSub(yskbjYe, yskbj);
        // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
        otherBjBalanceMap.put(dealNow.getBranch(), yskbjYe);
        // 变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 交易明细.全价总额
        bjfxYe = ArithmeticUtil.doSub(bjfxYe, bjfx);
        // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
        fxBalanceMap.put(dealNow.getBranch(), bjfxYe);
        /**add by wujie on 20201031 start**/
        //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 - 金额元素.逾期款项-本金
        glb_qtyskbjsum = ArithmeticUtil.doSub(glb_qtyskbjsum, yskbj);
        //变量.逾期利息汇总余额 = 变量.逾期利息汇总余额 - 金额元素.逾期资产应计利息																			
        glb_qilxsum = ArithmeticUtil.doSub(glb_qilxsum, bjfx);
        /**add by wujie on 20201031 end**/
    }

    /**
     * 清理-其他应收款利息
     *
     * @param acctEntity
     * @param booksetIdNow
     * @param bokDetailId
     * @param taskPri
     * @param standardCcyNow
     * @param otherInterestBalanceMap
     * @param otherBjBalanceMap
     * @param dealNow
     * @param accountSceneMap
     * @param accountAmountMap
     * @param accountCurrencyMap
     * @see
     * @since 1.0
     */
    private void cleanYsklx(FinProductDealInfoResult acctEntity,
                            String booksetIdNow, String bokDetailId, Long taskPri,
                            String standardCcyNow, Map<Long, BigDecimal> otherInterestBalanceMap,
                            Map<Long, BigDecimal> otherBjBalanceMap,
                            FinProductDealInfoComDTO dealNow, Map<String, Object> accountSceneMap,
                            Map<String, BigDecimal> accountAmountMap,
                            Map<String, Object> accountCurrencyMap
                            /**add by wujie on 20201031 start**/
            , BigDecimal glb_qtysklxsum
            , BigDecimal glb_qilxsum
                            /**add by wujie on 20201031 end**/
            , Map<Long, BigDecimal> fxBalanceMap
    ) {
        // nvl(交易明细.汇总交易编号,交易明细.交易编号);
        if (StringUtil.isNotEmpty(dealNow.getTotleTradeId())) {
            accountSceneMap.put(BookConstant.TRADE_ID, dealNow.getTotleTradeId());
        }
        // 定义 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额MAP.get(交易明细.分支序号)
        BigDecimal ysklxYe = otherInterestBalanceMap.get(dealNow.getBranch());
        // if（变量.其他应收款-利息科目余额 == null）
        if (ysklxYe == null) {
            ysklxYe = BigDecimal.ZERO;
            // 查询交易的到期日对应的本金
            SubjectBalance otherBjBalance = this.getSubjectBalance(booksetIdNow,
                    bokDetailId, dealNow.getHappenDate(), dealNow.getBooksetDate(),
                    DictCodeList.余额方向_借方.getValue(),
                    DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(), dealNow.getBranch(),
                    taskPri);
            if (otherBjBalance != null) {
                // 变量.其他应收款-利息科目余额 = 上述共通返回的科目余额
                ysklxYe = otherBjBalance.getBalance();
            }
            // 变量.其他应收款-利息科目余额MAP.put(交易明细.分支序号，变量.其他应收款-利息科目余额)
            otherInterestBalanceMap.put(dealNow.getBranch(), ysklxYe);
        }
        // 定义 变量.逾期利息科目余额
        BigDecimal lxfxYe = fxBalanceMap.get(dealNow.getBranch());
        if (lxfxYe == null) {
            lxfxYe = BigDecimal.ZERO;
            // 查询交易的到期日对应的罚息利息
            SubjectBalance lxfxBalance = this.getSubjectBalance(booksetIdNow, bokDetailId,
                    dealNow.getHappenDate(), dealNow.getBookDate(), DictCodeList.余额方向_借方.getValue(),
                    DictCodeBaseSubjectNatrueNew.应收逾期款_利息.getValue(), dealNow.getBranch(), taskPri);
            if (lxfxBalance != null) {
                // 变量.其他应收款-本金科目余额 = 上述共通返回的科目余额
                lxfxYe = lxfxBalance.getBalance();
            }
            fxBalanceMap.put(dealNow.getBranch(), lxfxYe);
        }
        // 定义 变量.清理其他应收款-本金 = 0
        BigDecimal ysklx = BigDecimal.ZERO;
        // 定义 变量.清理罚息-利息 = 0
        BigDecimal lxfx = BigDecimal.ZERO;
        // if (交易明细.是否结清 = "是")
        if (DictCodeList.是否_是.getValue().equals(dealNow.getIsClean())) {
            ysklx = ysklxYe;
            lxfx = lxfxYe;
        }
        // 变量.减值损失 = 变量.清理面值+变量.清理减值准备+变量.清理应计利息
        BigDecimal jzss = ArithmeticUtil.doAdd(ysklx, lxfx);
        // 应收款项-利息 <- 变量.清理其他应收款-利息
        accountAmountMap.put(BookConstant.OTHER_INTAMT,
                ArithmeticUtil.round(ysklx, NumberEnum.MONEY));
        // 逾期资产应计利息 <- 变量.清理罚息-利息
        accountAmountMap.put(BookConstant.OVERINTAMT,
                ArithmeticUtil.round(lxfx, NumberEnum.MONEY));
        // if（变量.减值损失 >= 0）
        if (jzss.compareTo(BigDecimal.ZERO) >= 0) {
            // 减值损失(借) <- 变量.减值损失
            accountAmountMap.put(BookConstant.IMPAMTLOSS_D,
                    ArithmeticUtil.round(jzss, NumberEnum.MONEY));
        } else {
            // 减值损失(贷) <- 变量.减值损失*-1
            accountAmountMap.put(BookConstant.IMPAMTLOSS_C, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(jzss, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        // 因为与记账金额元素map的key及数量一致，故采用循环
        for (String keyNow : accountAmountMap.keySet()) {
            accountCurrencyMap.put(keyNow, dealNow.getCcy());// value值塞入当前交易币种
        }

        // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        BigDecimal 汇率 = BigDecimal.ONE;
        if (!dealNow.getCcy().equals(acctEntity.getStandardCcy())) {
            // 调用计算类 方法共通【汇率查询】
            // 基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
            // 汇率 = calculationCommonService.queryExchangeRateForTrade(dealNow.getCcy(),
            // standardCcyNow, happenDateNow);
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                    acctEntity.getBookDate(), acctEntity.getBooksetId(), standardCcyNow,
                    dealNow.getCcy());
            if (!StringUtil.isEmptyList(currencyMarketList)
                    && currencyMarketList.get(0) != null) {
                汇率 = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(
                            calculationCommonService.doMultiply(accountAmountMap.get(key), 汇率),
                            NumberEnum.MONEY));
        }
        // 循环本币金额元素，赋值本币
        for (String key : accountAmountBCCYAMTMap.keySet()) {
            accountCurrencyMap.put(key, standardCcyNow);// value值塞入当前交易币种
        }
        /** 本位币 币种 **/
        accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
        // 记账金额元素 putALl 随记账本位币金额map
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        // accountAmountMap再次设置如下：
        // 变量.减值损失_本位币 = 金额元素.应收款项-本金_本位币+金额元素.逾期资产应计利息_本位币
        BigDecimal jzssB = ArithmeticUtil.doAdd(
                accountAmountMap.get(BookConstant.OTHER_INTAMT + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.OVERINTAMT + BookConstant.BCCYAMT));
        // if（变量.减值损失_本位币 >= 0）
        if (jzssB.compareTo(BigDecimal.ZERO) >= 0) {
            // 减值损失(借)_本位币 <- 变量.减值损失_本位币
            accountAmountMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(jzssB, NumberEnum.MONEY));
            accountCurrencyMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT,
                    standardCcyNow);// value值塞入当前交易币种
        } else {
            // 减值损失(贷)_本位币 <- 变量.减值损失_本位币*-1
            accountAmountMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(jzssB, new BigDecimal(-1)),
                            NumberEnum.MONEY));
            accountCurrencyMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT,
                    standardCcyNow);
        }
        // 全局变量的更新
        // 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 变量.清理其他应收款-利息
        ysklxYe = ArithmeticUtil.doSub(ysklxYe, ysklx);
        lxfxYe = ArithmeticUtil.doSub(lxfxYe, lxfx);
        // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
        otherBjBalanceMap.put(dealNow.getBranch(), ysklxYe);
        fxBalanceMap.put(dealNow.getBranch(), lxfxYe);
        /**add by wujie on 20201031 start**/
        //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 - 金额元素.应收款项-利息
        glb_qtysklxsum = ArithmeticUtil.doSub(glb_qtysklxsum, ysklx);
        //变量.逾期利息汇总余额 = 变量.逾期利息汇总余额 - 金额元素.逾期资产应计利息																			
        glb_qilxsum = ArithmeticUtil.doSub(glb_qilxsum, lxfx);
        /**add by wujie on 20201031 end**/
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     *
     * @param booksetIdNow      账套代码
     * @param bokDetailId       账务明细代码
     * @param happenDateNow     发生日
     * @param bookDateNow       入账日
     * @param expBalanceDirect  期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param branch            分支号
     * @param taskPri           任务优先级
     * @return SubjectBalance 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetIdNow, String bokDetailId,
                                             Date happenDateNow, Date bookDateNow,
                                             String expBalanceDirect, String baseSubjectNature,
                                             Long branch, Long taskPri) {
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
        searchDto.setBooksetId(booksetIdNow);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDateNow);
        searchDto.setBookDate(bookDateNow);
        searchDto.setBranch(branch);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询-期待方向 】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0);
        }

        return balance;
    }

    /**
     * 调用共通方法：[标的类共通方法-查询交易信息]，获取标的交易信息
     *
     * @param finprodId 金融产品代码【必输项】
     * @param finprodType 金融产品类型【必输项】
     * @param branch 分支序号
     * @return ValFinProductDeal 标的查询
     * @see
     * @since 1.0
     */
    // private TradeInfoDto getTargetInfo(String finprodId, String finprodType, Long branch) {
    // // 定义返回值 - 标的交易信息
    // TradeInfoDto tradeInfoDtoForReturn = null;
    //
    // // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
    // List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(finprodId,
    // finprodType,
    // branch);
    //
    // // 当查询结果不为空
    // if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
    // // 默认取第一条标的信息
    // tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
    // }
    //
    // return tradeInfoDtoForReturn;
    // }

    /**
     * 获取标的基本信息
     *
     * @param bokDetailId 账务明细代码
     * @return TargetFinProduct 标的基本信息
     * @see
     * @since 1.0
     */
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
        TargetFinProduct targetFinProduct = (TargetFinProduct) targetMap.get(targetSearchDto);

        // 确保不为null
        if (targetFinProduct == null) {
            targetFinProduct = new TargetFinProduct();
        }

        return targetFinProduct;
    }


    /**
     * 科目余额汇总
     * add by wujie on 20201030
     */
    private BigDecimal getSubjectBalanceSum(SubjectBalanceSearchDto searchDao, String expBalanceDirect) {
        Map<String, BigDecimal> map = accountingCommonService
                .getSubjectBalanceCollection(searchDao, DictCodeList.余额方向_借方.getValue(), null, searchDao.getTaskPri());
        // 变量.其他应收本金 = 科目余额
        BigDecimal balance = BigDecimal.ZERO;
        if (map != null && map.size() > 0) {
            for (BigDecimal b : map.values()) {
                balance = b;
                break;
            }
        } else {
            balance = BigDecimal.ZERO;
        }
        return balance;
    }

    /**
     * 冲减减值计算DTO初始化
     *
     * @param acctEntity
     * @param dealNow
     * @return
     */
    private LossAmtInputDto getLossAmtInputDto(FinProductDealInfoResult acctEntity, FinProductDealInfoComDTO dealNow,
                                               String flag,//01 本金  02 利息
                                               BigDecimal happenAmt,//发生金额
                                               BigDecimal lossBalAmt,//减值准备余额
                                               BigDecimal EADAmt, //当前EAD金额
                                               BigDecimal receAmt //应收款项余额
    ) {
        LossAmtInputDto dto = new LossAmtInputDto();
        //本息标识
        dto.setFlag(flag);
        //账套代码
        dto.setBooksetId(acctEntity.getBooksetId());
        //账务明细代码
        dto.setBokDetailId(acctEntity.getBokDetailId());
        //批处理优先级
        dto.setTaskPri(dealNow.getTaskPri());
        //分支序号
        dto.setBranch(dealNow.getBranch());
        //发生日
        dto.setHappenDate(acctEntity.getHappenDate());
        //记账日
        dto.setBookDate(acctEntity.getBookDate());
        //发生金额
        dto.setHappenAmt(ArithmeticUtil.round(happenAmt, NumberEnum.MONEY));
        //减值准备余额
        dto.setLossBalAmt(lossBalAmt);
        //当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额  + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
        dto.setEADAmt(EADAmt);
        //应收款余额
        dto.setReceAmt(receAmt);
        //业务类型
        dto.setBusiType(dealNow.getFinprodType());
        return dto;
    }

}

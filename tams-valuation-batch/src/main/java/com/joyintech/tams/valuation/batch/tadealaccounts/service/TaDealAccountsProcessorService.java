/*
 * 文件名：TaDealAccountsProcessorService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：估值核算系统V1.0 修改人：yangjian 修改时间：2018年5月17日
 * 修改内容：新建 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.tadealaccounts.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListNatureGroup;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 净值型产品TA交易入账 process相关业务处理
 * 
 * @author yangjian
 * @version 1.0 2018年5月17日
 * @see TaDealAccountsProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class TaDealAccountsProcessorService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogic bookLogic;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 账务类共通方法
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的查询共通方法
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * process 数据处理
     * 
     * @param param 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param.get(GlobalConstant.READER_RESULT);

        // 若reader传递的参数或金融产品交易信息列表为空，不进行后续处理
        if (acctEntity == null || acctEntity.getListFinProductDealInfoComDTO() == null
            || acctEntity.getListFinProductDealInfoComDTO().isEmpty()) {
            return null;
        }

        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        // String invAimNow = acctEntity.getInvAim(); // 投资目的
        TreeNode bookSetTreeNodNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 金融产品交易信息list
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        // 获取金融产品代码（已与设计讨论过，同账务明细代码下对应的金融产品类型是一样的。故金融产品类型取交易明细中的第一个。）
        String finprodIdFirst = listFinProductDealInfoComDTONow.get(0).getFinprodId();

        // 获取标的信息.金融产品类型
        TargetFinProduct targetFinProduct = this.getTargetFinProduct(finprodIdFirst);

        // 获取标的信息.收益类型
        // String profitTypeNow = targetFinProduct.getProfitType();
        // 交易信息.分类1
        String type1Now = targetFinProduct.getType1();
        // 交易信息.分类2
        String type2Now = targetFinProduct.getType2();

        // 变量：实收资本
        BigDecimal capital = BigDecimal.ZERO;
        // 实收资本 = 科目余额
        SubjectBalance subjectBalance = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow, bookDateNow,
            DictCodeList.余额方向_贷方.getValue(), DictCodeBaseSubjectNatrue.实收资本.getValue(), taskPri);
        if (subjectBalance != null) {
            capital = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
        }

        // 循环金融产品交易信息list,进行逻辑处理
        for (FinProductDealInfoComDTO dealNow : listFinProductDealInfoComDTONow) {

            /**
             * modify by wujie on 20181226 start // 定义变量：净值日未实现损益 BigDecimal jzrwsxsy = this.getJZRWSXSY(booksetIdNow, dealNow.getCpriceDate(), bokDetailIdNow,
             * dealNow.getCcy(), dealNow.getTaskPri()); jzrwsxsy = jzrwsxsy == null ? BigDecimal.ZERO : jzrwsxsy; // 定义变量：净值日资产净值 BigDecimal jzrzcjz =
             * this.getJZRZCJZ(dealNow.getCpriceDate(), booksetIdNow,bookDateNow); jzrzcjz = jzrzcjz == null ? BigDecimal.ZERO : jzrzcjz;
             **/
            // 定义变量：净值日未实现损益
            BigDecimal jzrwsxsy = BigDecimal.ZERO;
            // 定义变量：净值日资产净值
            BigDecimal jzrzcjz = BigDecimal.ZERO;

            /** modify by wujie on 20181226 end **/

            // 获取交易类型
            String dealTypeNow = dealNow.getPs();
            // 交易金额
            BigDecimal tradeAmt = dealNow.getTradeAmt() == null ? BigDecimal.ZERO : dealNow.getTradeAmt();
            // 交易份额
            BigDecimal shareAmt = dealNow.getShareAmt() == null ? BigDecimal.ZERO : dealNow.getShareAmt();
            // 全价总额
            BigDecimal fpriceAmt = dealNow.getFpriceAmt() == null ? BigDecimal.ZERO : dealNow.getFpriceAmt();

            // modify by weipeng on 20220223：增加赎回费（产品留存）处理 start
            // 赎回费（产品留存）
            BigDecimal feeAmtPay = dealNow.getFeeAmtPay() == null ? BigDecimal.ZERO : dealNow.getFeeAmtPay();
            // modify by weipeng on 20220223：增加赎回费（产品留存）处理 end

            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // 共通元素设值 - 设置账务引擎参数
            ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景

            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
            accountSceneMap.put(BookConstant.TYPE_1, type1Now);// 分类1 - 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_2, type2Now);// 分类2 - 变量.交易信息.分类2

            // 根据不同的交易方向，设置其他记账凭证要素
            // 交易方向 - 产品认购，产品申购
            if (DictCodeList.交易方向_产品认购.getValue().equals(dealTypeNow) || DictCodeList.交易方向_产品申购.getValue().equals(dealTypeNow)
                || DictCodeList.交易方向_份额强增.getValue().equals(dealTypeNow)) {
                // 定义变量：净值日未实现损益
                jzrwsxsy = this.getJZRWSXSY(booksetIdNow, dealNow.getCpriceDate(), bokDetailIdNow, dealNow.getCcy(),
                    dealNow.getTaskPri());
                // 定义变量：净值日资产净值
                /**modify by wjjie on 20190703 start
                jzrzcjz = this.getJZRZCJZ(dealNow.getCpriceDate(), booksetIdNow, bookDateNow);
                **/
                jzrzcjz = this.getJZRZCJZ(dealNow.getCpriceDate(), booksetIdNow, bookDateNow,bokDetailIdNow);
                /**modify by wujie on 20190703 end**/

                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_净值型_认购申购交易.getValue());// ad.会计场景代码;

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA认购、申购交易登账");// 凭证说明

                // 记账金额元素map塞值
                accountAmountMap.put("ACCQUA", null); // 核算数量
                accountAmountMap.put("DEALAMT", tradeAmt);// 申购金额 - 取交易金额
                accountAmountMap.put("TRADE_QTY", shareAmt);// 申购份额 - 取交易份额

                // 获取 未实现损益平准金 = 变量.净值日未实现损益 * 交易金额 / 变量.净值日资产净值
                if (jzrwsxsy == null) {
                    jzrwsxsy = BigDecimal.ZERO;
                }
                BigDecimal wsxsypzjNow = ArithmeticUtil.round(
                    ArithmeticUtil.divide(ArithmeticUtil.multiply(jzrwsxsy, tradeAmt), jzrzcjz), NumberEnum.MONEY);
                // 获取 已实现损益平准金 = 交易金额 - 交易份额 - 变量.未实现损益平准金
                BigDecimal ysxsypzjNow = ArithmeticUtil.round(ArithmeticUtil.doSub(tradeAmt, shareAmt, wsxsypzjNow),
                    NumberEnum.MONEY);

                // 金额元素.已实现损益平准金
                if (ysxsypzjNow.compareTo(BigDecimal.ZERO) > 0) {
                    // 金额元素.已实现损益平准金_贷 = 变量.已实现损益平准金
                    accountAmountMap.put("PLGAIN_C", ysxsypzjNow);
                }
                else {
                    // 金额元素.已实现损益平准金_借 = 变量.已实现损益平准金*-1
                    accountAmountMap.put("PLGAIN_D", ArithmeticUtil.multiply(ysxsypzjNow, new BigDecimal(-1)));
                }
                // 金额元素.未实现损益平准金
                if (wsxsypzjNow.compareTo(BigDecimal.ZERO) > 0) {
                    // 金额元素.已实现损益平准金_贷 = 变量.已实现损益平准金
                    accountAmountMap.put("PAPERGAIN_C", wsxsypzjNow);
                }
                else {
                    // 金额元素.已实现损益平准金_借 = 变量.已实现损益平准金*-1
                    accountAmountMap.put("PAPERGAIN_D", ArithmeticUtil.multiply(wsxsypzjNow, new BigDecimal(-1)));
                }

                // 全局变量设置
                capital = ArithmeticUtil.doAdd(capital, shareAmt);// 变量：实收资本 += 金额元素.申购份额

            } // 交易方向 - 产品赎回、产品到期、份额强增、份额强减
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dealTypeNow)
                     || DictCodeList.交易方向_产品到期.getValue().equals(dealTypeNow)
                     || DictCodeList.交易方向_份额强减.getValue().equals(dealTypeNow)

                     // 与王之昂金强确认：所有产品增加（1、交易方向加提前赎回、违约赎回，估值走赎回分录，2、交易方向加提前终止，估值走到期分录。）
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dealTypeNow)
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dealTypeNow)
                     || DictCodeList.交易方向_提前终止.getValue().equals(dealTypeNow)

            ) {
                // 定义变量：净值日未实现损益
                jzrwsxsy = this.getJZRWSXSY(booksetIdNow, dealNow.getCpriceDate(), bokDetailIdNow, dealNow.getCcy(),
                    dealNow.getTaskPri());
                // 定义变量：净值日资产净值
                /**modify by wujie on 20190703 start
                jzrzcjz = this.getJZRZCJZ(dealNow.getCpriceDate(), booksetIdNow, bookDateNow);
                **/
                jzrzcjz = this.getJZRZCJZ(dealNow.getCpriceDate(), booksetIdNow, bookDateNow,bokDetailIdNow);
                /**modify by wujie on 20190703 end**/
                
                jzrwsxsy = jzrwsxsy == null ? BigDecimal.ZERO : jzrwsxsy;

                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_净值型_赎回到期.getValue());// ad.会计场景代码=净值型产品TA交易_赎回到期确认;

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA赎回、到期确认交易登账");// 凭证说明

                // 记账金额元素map塞值
                accountAmountMap.put("ACCQUA", null); // 核算数量
                accountAmountMap.put("REDEEM_QTY", shareAmt); // 赎回份额 - 取交易份额
                // modify by weipeng on 20220223：增加赎回费（产品留存）处理 start
                accountAmountMap.put("OTHER_INTAMT", feeAmtPay);// 赎回费中留存产品部分
                // modify by weipeng on 20220223：增加赎回费（产品留存）处理 end
                accountAmountMap.put("REDEEM_NETAMT", tradeAmt);// 赎回净金额 - 取交易金额

                // 获取 未实现损益平准金 = 变量.净值日未实现损益 * 交易金额 / 变量.净值日资产净值
                BigDecimal wsxsypzjNow = ArithmeticUtil.round(
                    ArithmeticUtil.divide(ArithmeticUtil.multiply(jzrwsxsy, tradeAmt), jzrzcjz), NumberEnum.MONEY);
                // modify by weipeng on 20220223：增加赎回费（产品留存）处理 start
                // 获取 已实现损益平准金 = 交易金额 + 赎回费（产品留存） - 交易份额 - 变量.未实现损益平准金
                BigDecimal ysxsypzjNow = ArithmeticUtil.round(ArithmeticUtil.doSub(ArithmeticUtil.doAdd(tradeAmt, feeAmtPay), shareAmt, wsxsypzjNow),
                    NumberEnum.MONEY);
                // modify by weipeng on 20220223：增加赎回费（产品留存）处理 end
                // 金额元素.已实现损益平准金
                if (ysxsypzjNow.compareTo(BigDecimal.ZERO) > 0) {
                    // 金额元素.已实现损益平准金_借 = 变量.已实现损益平准金
                    accountAmountMap.put("PLGAIN_D", ysxsypzjNow);
                }
                else {
                    // 金额元素.已实现损益平准金_贷 = 变量.已实现损益平准金*-1
                    accountAmountMap.put("PLGAIN_C", ArithmeticUtil.multiply(ysxsypzjNow, new BigDecimal(-1)));
                }
                // 金额元素.未实现损益平准金
                if (wsxsypzjNow.compareTo(BigDecimal.ZERO) > 0) {
                    // 金额元素.已实现损益平准金_借 = 变量.已实现损益平准金
                    accountAmountMap.put("PAPERGAIN_D", wsxsypzjNow);
                }
                else {
                    // 金额元素.已实现损益平准金_贷 = 变量.已实现损益平准金*-1
                    accountAmountMap.put("PAPERGAIN_C", ArithmeticUtil.multiply(wsxsypzjNow, new BigDecimal(-1)));
                }

                // 全局变量设置
                capital = ArithmeticUtil.subtract(capital, shareAmt);// 变量：实收资本 = 变量：实收资本 - 金额元素.赎回份额

            } // 交易方向 - 产品除权登记
            else if (DictCodeList.交易方向_产品除权登记.getValue().equals(dealTypeNow)) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_净值型_分红除权处理.getValue());// ad.会计场景代码=净值型产品TA交易_认申购;

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA除权登记交易登账");// 凭证说明

                // 记账金额元素map塞值
                accountAmountMap.put("ACCQUA", null);// 核算数量
                accountAmountMap.put("DIVIDENDAMT", fpriceAmt);// 分红金额 - 取全价总额

            } // 交易方向 -产品现金分红
            else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dealTypeNow)) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_净值型_现金分红.getValue());// ad.会计场景代码=净值型产品TA交易_认申购;

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA现金分红交易登账");// 凭证说明

                // 记账金额元素map塞值
                accountAmountMap.put("ACCQUA", null);// 核算数量
                accountAmountMap.put("PAY_DIVIDENDAMT", tradeAmt); // 支付分红金额 - 取交易金额

            } // 交易方向 - 产品红利转投
            else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dealTypeNow)) {
                // 定义变量：净值日未实现损益
                jzrwsxsy = this.getJZRWSXSY(booksetIdNow, dealNow.getCpriceDate(), bokDetailIdNow, dealNow.getCcy(),
                    dealNow.getTaskPri());
                // 定义变量：净值日资产净值
                /**modify by wujie on 20190703 start
                jzrzcjz = this.getJZRZCJZ(dealNow.getCpriceDate(), booksetIdNow, bookDateNow);
                **/
                jzrzcjz = this.getJZRZCJZ(dealNow.getCpriceDate(), booksetIdNow, bookDateNow,bokDetailIdNow);
                /**modify by wujie on 20190703 end**/
                
                jzrwsxsy = jzrwsxsy == null ? BigDecimal.ZERO : jzrwsxsy;
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_净值型_红利再投.getValue());// ad.会计场景代码=净值型产品TA交易_认申购;

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA红利转投交易登账");// 凭证说明

                // 记账金额元素map塞值
                accountAmountMap.put("ACCQUA", null);// 核算数量
                accountAmountMap.put("CUSTREV", fpriceAmt);// 客户收益 - 取全价总额
                accountAmountMap.put("TRADE_QTY", shareAmt);// 申购份额 - 取交易份额

                // 获取 未实现损益平准金 = 变量.净值日未实现损益 * 交易金额 / 变量.净值日资产净值
                BigDecimal wsxsypzjNow = ArithmeticUtil.round(
                    ArithmeticUtil.divide(ArithmeticUtil.multiply(jzrwsxsy, tradeAmt), jzrzcjz), NumberEnum.MONEY);
                // 获取 已实现损益平准金 = 全价总额- 交易份额 - 变量.未实现损益平准金
                BigDecimal ysxsypzjNow = ArithmeticUtil.round(ArithmeticUtil.doSub(fpriceAmt, shareAmt, wsxsypzjNow),
                    NumberEnum.MONEY);

                // 金额元素.已实现损益平准金
                if (ysxsypzjNow.compareTo(BigDecimal.ZERO) > 0) {
                    // 金额元素.已实现损益平准金_贷 = 变量.已实现损益平准金
                    accountAmountMap.put("PLGAIN_C", ysxsypzjNow);
                }
                else {
                    // 金额元素.已实现损益平准金_借 = 变量.已实现损益平准金*-1
                    accountAmountMap.put("PLGAIN_D", ArithmeticUtil.multiply(ysxsypzjNow, new BigDecimal(-1)));
                }
                // 金额元素.未实现损益平准金
                if (wsxsypzjNow.compareTo(BigDecimal.ZERO) > 0) {
                    // 金额元素.已实现损益平准金_贷 = 变量.已实现损益平准金
                    accountAmountMap.put("PAPERGAIN_C", wsxsypzjNow);
                }
                else {
                    // 金额元素.已实现损益平准金_借 = 变量.已实现损益平准金*-1
                    accountAmountMap.put("PAPERGAIN_D", ArithmeticUtil.multiply(wsxsypzjNow, new BigDecimal(-1)));
                }

                // 全局变量设置
                capital = ArithmeticUtil.doAdd(capital, shareAmt);// 变量：实收资本 += 金额元素.申购份额
            }

            // 记账金额元素币种信息塞值
            // 因为与记账金额元素map的key及数量一致，故采用循环
            for (String keyNow : accountAmountMap.keySet()) {
                accountCurrencyMap.put(keyNow, dealNow.getCcy());// value值塞入当前交易币种
            }

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        }

        return returnResult;
    }

    /**
     * 获取 净值日未实现损益
     * 
     * @param booksetId 账套id
     * @param cpriceDate 净值日
     * @param bokDetailId 账务明细代码
     * @param taskPri 任务优先级
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    private BigDecimal getJZRWSXSY(String booksetId, Date cpriceDate, String bokDetailId, String ccy, Long taskPri) {

        // // 定义科目性质list
        // List<String> baseSubjectNatureList = new ArrayList<>();

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);// 账套代码
        searchDto.setBokDetailId(bokDetailId);// 账务明细代码
        searchDto.setHappenDate(cpriceDate);// 发生日
        searchDto.setBookDate(cpriceDate);// 入账日
        searchDto.setTaskPri(taskPri);

        // // 定义变量：未分配利润-未实现
        // baseSubjectNatureList.clear();// 清空科目性质list
        // baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.未分配利润_未实现.getValue()); // 添加科目性质 - 未分配利润_未实现
        // searchDto.setBaseSubjectNatureList(baseSubjectNatureList);// 科目性质重置
        // BigDecimal wfplr_wsx = accountingCommonService.getSubjectBalanceCollection(searchDto, DictCodeList.借贷方向_贷.getValue()).get(ccy);
        //
        // // 定义变量：公允价值变动损益
        // baseSubjectNatureList.clear(); // 清空科目性质list
        // baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.利率掉期公允价值变动损益.getValue()); // 添加科目性质 - 公允价值变动损益-利率掉期
        // baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.外汇掉期公允价值变动损益.getValue());// 添加科目性质 - 公允价值变动损益-外汇掉期
        // baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.外汇远期公允价值变动损益.getValue());// 添加科目性质 - 公允价值变动损益-外汇远期
        // baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.外汇即期公允价值变动损益.getValue());// 添加科目性质 - 公允价值变动损益-外汇即期
        // searchDto.setBaseSubjectNatureList(baseSubjectNatureList);// 科目性质重置
        // BigDecimal gyjzbdsy = accountingCommonService.getSubjectBalanceCollection(searchDto, DictCodeList.借贷方向_贷.getValue()).get(ccy);
        //
        // // 定义变量：汇兑损益
        // baseSubjectNatureList.clear();// 清空科目性质list
        // baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.外汇即期汇兑损益.getValue());// 添加科目性质 - 汇兑损益-外汇即期
        // baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.外汇远期汇兑损益.getValue());// 添加科目性质 - 汇兑损益-外汇远期
        // searchDto.setBaseSubjectNatureList(baseSubjectNatureList);// 科目性质重置
        // BigDecimal hzsy = accountingCommonService.getSubjectBalanceCollection(searchDto, DictCodeList.借贷方向_贷.getValue()).get(ccy);
        //
        // // 定义变量：净值日未实现损益 = 未分配利润-未实现+公允价值变动损益+汇兑
        // BigDecimal jzrwsxsy = ArithmeticUtil.doAdd(wfplr_wsx, gyjzbdsy, hzsy);


        //modify jinmaodong 20210312 START
        // 上农商反馈问题修复：
        // 重现步骤
        //1.产品损益结转频率设置为月结
        //2.产品投资FVTPL\FVOCI资产
        //3.产品进行申购
        //测试结果
        //1.损益平准金计算没有包含上公允价值变动损益
        //原因定位：在申赎交易出账计算损益平准金未实现的时候，公式如下：
        //【损益平准金-未实现】=净值日未实现损益*交易金额/净值日资产净值；
        //净值日为交易信息上的“净值日”；
        //净值日未实现损益=（41040302未分配利润（未实现））+（6101（公允价值变动损益））+（6112（汇兑损益））+（40110101损益平准金（未实现）历史余额）；
        //【损益平准金-已实现】= 交易金额 - 实收资本 - 【损益平准金-未实现】；
        //查询条件中带了产品的账务明细代码，导致查询不到6101和6112科目的余额（这些科目的账务明细代码是资产）；
        //修复建议：根据产品类型进行判断：如果是非分层产品，则查询的时候不带账务明细代码；如果是分层产品，则做业务要求:损益结转频率必须设置为日结
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("booksetId", booksetId);
        List<StayDrawSubprdEntity> stayDrawSubprdEntityList = accountingCommonService.querySubBokAcctInfo(params);
        //如果核算分层信息为空，或者只有一条，则认为该产品为非分层产品，则查询未实现损益时，不带账务明细代码查。
        if(CollectionUtils.isEmpty(stayDrawSubprdEntityList)||stayDrawSubprdEntityList.size()==1){
            searchDto.setBokDetailId(null);
        }
        //modify jinmaodong 20210312 END
        ArrayList<String> baseSubjectNatureList = new ArrayList<>();
        baseSubjectNatureList.addAll(
                accountingCommonService.getValSubjectNatureList(DictCodeListNatureGroup.分组代码_损益平准交_未实现对应查询科目.getValue()));
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        BigDecimal jzrwsxsy = accountingCommonService.getSubjectBalanceCollection(searchDto, DictCodeList.借贷方向_贷.getValue(),DictCodeList.是否_否.getValue()).get(
                ccy);
        return jzrwsxsy;
    }

    /**
     * 获取 净值日资产净值
     * 
     * @param cpriceDate 净值日
     * @param booksetId 账套id
     * @param bookDate 入账日
     * @param bokDetailId 账务明细代码 ADD BY WUJIE ON 20190703
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    private BigDecimal getJZRZCJZ(Date cpriceDate, String booksetId, Date bookDate,String bokDetailId) {
        // 定义默认净值日资产净值
        BigDecimal jzrzcjz = BigDecimal.ZERO;
        BokStatBokPositionDto position = new BokStatBokPositionDto();
        // 查询日开始
        position.setSdateStart(cpriceDate);
        // 查询结束日
        position.setSdateEnd(cpriceDate);
        // 入账日
        position.setBookDate(bookDate);
        // 账套
        position.setBooksetId(booksetId);
        
        /**ADD BY WUJIE ON 20190703 START**/
        position.setBokDetailId(bokDetailId);
        /**ADD BY WUJIE ON 20190703 END**/
        
        // 定义变量：净值日资产净值 = 调用共通方法查询：帐套资产负债查询（净值日，净值日，DTO.帐套代码）.费后资产净值
        List<BokStatBokPosition> valStatBokPositionlist = accountingCommonService.getValStatBokPosition(position);
        // 获取 费后资产净值 赋给 净值日资产净值
        if (valStatBokPositionlist != null && valStatBokPositionlist.size() > 0 && valStatBokPositionlist.get(0) != null) {
            jzrzcjz = valStatBokPositionlist.get(0).getNetAssetValue();
        }

        return jzrzcjz;
    }

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
        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(bokDetailId, targetSearchDtoList);
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);

        // 确保不为null
        if (targetFinProduct == null) {
            targetFinProduct = new TargetFinProduct();
        }

        return targetFinProduct;
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
     * @return subjectBalance 科目余额信息
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate, Date bookDate,
                                             String expBalanceDirect, String baseSubjectNature, Long taskPri) {
        // 定义返回实体类
        SubjectBalance subjectBalance = null;

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
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(searchDto,
            expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
    }

}

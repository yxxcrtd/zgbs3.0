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
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
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
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        // 变量.交易各种费用.交易手续费
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










}

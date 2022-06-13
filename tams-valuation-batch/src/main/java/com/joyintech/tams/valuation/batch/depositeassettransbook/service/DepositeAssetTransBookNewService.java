/*
 * 文件名：DepositeAssetTransBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.depositeassettransbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.LossAmtInputDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accounting.service.LossCommonBatchService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.DealTypesFeeDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.constants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 存款类资产交易账务处理 process相关业务处理
 * 
 * @author weipeng
 * @version 1.0 2018年5月23日
 * @see DepositeAssetTransBookNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DepositeAssetTransBookNewService {

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
     * 减值共通
     */
    @Autowired
    private LossCommonService lossCommonService;
    
    @Autowired
    private LossCommonBatchService lossCommonBatchService;

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
        /** modify by yanya on 20191230 增加本位币处理 start **/
        String standardCcyNow = acctEntity.getStandardCcy();
        /** modify by yanya on 20191230 增加本位币处理 end **/

        // 1. 数据准备
        // 1.1 定义变量
        // 定义 变量.成本科目余额
        BigDecimal costBalance = BigDecimal.ZERO;
        // 定义 变量.成本科目数量
        BigDecimal costQuantity = BigDecimal.ZERO;
        // 定义 变量.应收利息科目余额
        BigDecimal interestBalance = BigDecimal.ZERO;
        // 变量.其他应收款-利息科目余额MAP
        Map<Long, BigDecimal> otherInterestBalanceMap = new HashMap<Long, BigDecimal>();
        /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
        // 定义  变量.减值准备科目余额
        BigDecimal jzzbYe = BigDecimal.ZERO;
        // 定义 变量.减值准备科目余额本位币
        BigDecimal jzzbYeB = BigDecimal.ZERO;
        // 变量.变量.其他应收款-本金科目余额MAP
        Map<Long, BigDecimal> otherBjBalanceMap = new HashMap<Long, BigDecimal>();
        Map<Long, BigDecimal> fxBalanceMap = new HashMap<Long, BigDecimal>();
        /** ADD BY CAOGUO ON 20201020 END **/
        // 2.调用共通查询发生前一日余额
        // 交易期待方向——借方
        String expBalanceDirect = DictCodeList.余额方向_借方.getValue();

        // 2.1 获取成本类科目余额 -【 调用共通方法：科目余额查询,获取成本类科目余额 】
        // 基础科目性质 : 成本
        SubjectBalance subjectBalance = this.getSubjectBalance(booksetIdNow, bokDetailId,
            happenDateNow, bookDateNow, expBalanceDirect, DictCodeBaseSubjectNatrueNew.定期存款_本金.getValue(),
            null, taskPri);
        // 成本类科目余额赋值
        costBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
        // 成本类科目数量赋值
        costQuantity = subjectBalance.getNum() == null ? BigDecimal.ZERO : subjectBalance.getNum();

        // 2.2 获取应收利息类科目余额 -【 调用共通方法：科目余额查询,获取成本类科目余额 】
        // 基础科目性质 : 应计利息
        subjectBalance = this.getSubjectBalance(booksetIdNow, bokDetailId, happenDateNow,
            bookDateNow, expBalanceDirect, DictCodeBaseSubjectNatrueNew.定期存款_应计利息.getValue(), null,
            taskPri);
        // 变量.应计利息余额 = DTO.科目余额
        interestBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
        
        /** ADD BY CAOGUO ON 20201020 减值相关修改 START **/
        // 2.3 减值准备类科目余额 -【 调用共通方法：科目余额查询,获取成本类科目余额 】
        // 基础科目性质 : 减值准备
        subjectBalance = this.getSubjectBalance(booksetIdNow, bokDetailId, happenDateNow,
            bookDateNow, expBalanceDirect, DictCodeBaseSubjectNatrueNew.定期存款_减值准备.getValue(), null,
            taskPri);
        // 变量.减值准备科目余额 = DTO.科目余额
        jzzbYe = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
        /** ADD BY CAOGUO ON 20201020 END **/
        
        /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
        //变量.其他应收款本金汇总余额
        BigDecimal glb_qtyskbjsum = BigDecimal.ZERO;
        //变量.其他应收款利息汇总余额
        BigDecimal glb_qtysklxsum = BigDecimal.ZERO;
        //变量.变量.逾期利息汇总余额
        BigDecimal glb_qilxsum = BigDecimal.ZERO;
        
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(acctEntity.getHappenDate());
        searchDao.setBookDate(acctEntity.getBookDate());
        //2.4.其他应收款本金余额
//        searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue())));
//        glb_qtyskbjsum = this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue());
        
        //2.5.其他应收款利息余额
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue())));
        glb_qtysklxsum = this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue());
        
        //2.6. 逾期利息余额查询
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应收逾期款_利息.getValue())));
        glb_qilxsum = this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue());
        
        
        //账套核算参数查询-调用账务类共通
        List<String> paramCodeList = new ArrayList<>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        List<BokBooksetExt> valBokBooksetExt = accountingCommonService.getValBokBooksetExt(acctEntity.getBooksetId(), 
        		paramCodeList);
        //定义 变量.还本收息冲抵减值准备处理日 = 返回值
        String offsetImptDateType  = null;
        if(!CollectionUtils.isEmpty(valBokBooksetExt)){
        	offsetImptDateType = valBokBooksetExt.get(0).getParamValue();
        }
        
        /**add by wujie on 20201031 end**/
        // 3. 交易处理
        // 循环 交易类账务处理结果.交易明细列表,进行逻辑处理
        for (FinProductDealInfoComDTO dealNow : listFinProductDealInfoComDTONow) {

            // 获取标的信息.金融产品类型
            TargetFinProduct targetFinProduct = this.getTargetFinProduct(dealNow.getFinprodId());

            AccountDriver ad = new AccountDriver();// 凭证类型、账套代码、交易编号全部去掉了。
            ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景

            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // 调用共通场景元素赋值-交易类场景元素
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);

            // accountSceneMap塞值
            // 备注 = 存款类资产交易登账
            accountSceneMap.put(BookConstant.BOOK_REMARK, "存款类资产交易登账");
            // 分类1 <- 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());
            // 分类2 <- 变量.交易信息.分类2
            accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());
            
            /** ADD BY CAOGUO ON 20201020 减值相关修改 START **/
            // 定义 变量.减值准备(借)
            BigDecimal jzzb = BigDecimal.ZERO;
            // 定义 变量.减值准备(本位币)
            BigDecimal jzzbB = BigDecimal.ZERO;
            /** ADD BY CAOGUO ON 20201020 减值相关修改 END **/
            // if（交易方向 = "买入"）
            if (DictCodeList.交易方向_买入.getValue().equals(dealNow.getPs())) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_存放同业_起息或买入.getValue());// ad.会计场景代码=存款类资产买入;
                /** ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                // 定义 变量.面值 = 交易明细.交易本金
                BigDecimal varMz = dealNow.getPrinAmt();
                // 定义 变量.应计利息 = 交易明细.利息总额
                BigDecimal varYjlx = dealNow.getIntAmt();
                // 定义 变量.减值准备 = 0
                BigDecimal varJzzb = BigDecimal.ZERO;
                // if（交易类账务处理结果.减值阶段 = "P3买入后出现减值"）
                if (DictCodeListExt3.阶段_买入后出现减值_P3.getValue().equals(dealNow.getLossStage())) {
                    // 账套核算参数查询
                    paramCodeList = new ArrayList<String>();
                    // DEPO_ACCR_TYPE 账套配置表参数项_存款计提方式
                    paramCodeList.add(ValDictCodeList.账套配置表参数项_存款计提方式.getValue());
                    // 调用账务类共通（DTO.账套代码, 参数项：如下）
                    List<BokBooksetExt> listBooksetExt = accountingCommonService.getValBokBooksetExt(
                        booksetIdNow, paramCodeList);
                    // 变量.计提方式 = 账套配置表参数项_拆借计提方式 对应的计提方式
                    String calType = DictCodeList.计提方式_每日新增.getValue();
                    if (!CollectionUtils.isEmpty(listBooksetExt)) {
                        // 获取第一条即可
                        calType = listBooksetExt.get(0).getParamValue();
                    }
                    if(DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(
                        targetFinProduct.getType1())) {
                        calType = DictCodeList.计提方式_每日新增.getValue();
                    }
                    // 面值 = 交易明细.份额
                    varMz= dealNow.getShareAmt();
                    // 调用减值相关共通-债券日间利息计算 变量.应计利息 = 上述返回.利息金额
                    varYjlx = lossCommonService.calCommonIntAmt(targetFinProduct,
                        dealNow.getShareAmt(), calType, acctEntity.getHappenDate());
                    // 变量.减值准备 = 交易明细.交易金额 - (变量.面值 + 变量.应计利息)
                    varJzzb = ArithmeticUtil.doSub(dealNow.getTradeAmt(),
                        ArithmeticUtil.doAdd(varYjlx, varMz));
                }
                /** ADD BY CAOGUO ON 20201020 END **/

                /** MODIFY BY CAOGUO ON 20201020 计算元素替换成 各项变量 **/
                // 记账金额元素map塞值
                // 本金金额 <- 交易明细.交易本金
                accountAmountMap.put(BookConstant.COST,
                    ArithmeticUtil.round(varMz, NumberEnum.MONEY));
                // 本息和 <- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
                // 应收利息 <- 交易明细.利息总额
                accountAmountMap.put(BookConstant.INTAMT,
                    ArithmeticUtil.round(varYjlx, NumberEnum.MONEY));
                // 减值准备 <- 变量.减值准备*-1
                accountAmountMap.put(BookConstant.IMPAMT,
                    ArithmeticUtil.round(varJzzb.negate(), NumberEnum.MONEY));
                /** ADD BY CAOGUO ON 20201020 END **/
                // 记账金额元素币种信息塞值--key是随记账金额map的
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealNow.getCcy());
                }
                /** modify by yanya on 20191230 增加本位币处理 start **/
                // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
                BigDecimal 汇率 = BigDecimal.ONE;
                // if(交易明细.币种 != 交易类账务处理结果..本位币){
                if (!dealNow.getCcy().equals(standardCcyNow)) {
                    // 调用计算类 方法共通【汇率查询】
                    // 基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
                    /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start **/
                    // 汇率 = calculationCommonService.queryExchangeRateForTrade(dealNow.getCcy(),
                    // standardCcyNow, happenDateNow);
                    List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                        bookDateNow, booksetIdNow, standardCcyNow, dealNow.getCcy());
                    if (!StringUtil.isEmptyList(currencyMarketList)
                        && currencyMarketList.get(0) != null) {
                        汇率 = currencyMarketList.get(0).getClosePrice();
                    }
                    /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 end **/

                    // 本币本金金额 <- 本金金额 * 汇率 用舍位处理后的结果计算并保留2位小数
                    BigDecimal 本币本金金额 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.COST), 汇率), NumberEnum.MONEY);
                    // 本币应收利息 <- 应收利息 * 汇率 用舍位处理后的结果计算并保留2位小数
                    BigDecimal 本币应收利息 = ArithmeticUtil.round(
                        ArithmeticUtil.multiply(accountAmountMap.get(BookConstant.INTAMT), 汇率),
                        NumberEnum.MONEY);
                    // 本币本息和 <- 本币本金金额 + 本币应收利息
                    BigDecimal 本币本息和 = ArithmeticUtil.doAdd(本币本金金额, 本币应收利息);
                    // 本币本金金额
                    accountAmountBCCYAMTMap.put(BookConstant.COST + BookConstant.BCCYAMT, 本币本金金额);
                    // 本币应收利息
                    accountAmountBCCYAMTMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT,
                        本币应收利息);
                    // 本币本息和
                    accountAmountBCCYAMTMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                        本币本息和);
                    /** ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                    // 本币减值准备 <- 本币应收利息 + 本币本金金额 - 本币本息和
                    BigDecimal varJzzbB = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(本币应收利息, 本币本金金额), 本币本息和);
                    accountAmountBCCYAMTMap.put(BookConstant.IMPAMT + BookConstant.BCCYAMT,
                        varJzzbB);
                    /** ADD BY CAOGUO ON 20201020 END **/
                }
                else {
                    // 本币本金金额 <- 本金金额
                    // accountAmountBCCYAMTMap.put(BookConstant.COST + BookConstant.BCCYAMT,
                    // accountAmountMap.get(BookConstant.COST));
                    // 本币应收利息 <- 应收利息
                    // accountAmountBCCYAMTMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT,
                    // accountAmountMap.get(BookConstant.INTAMT));
                    // 本币本息和 <- 本息和
                    // accountAmountBCCYAMTMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                    // accountAmountMap.get(BookConstant.DEALAMT));
                    for (String key : accountAmountMap.keySet()) {
                        accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                            accountAmountMap.get(key));
                    }
                }
                // 记账金额元素币种信息塞值--key是随记账本位币金额map的
                for (String key : accountAmountBCCYAMTMap.keySet()) {
                    accountCurrencyMap.put(key, standardCcyNow);
                }
                /** 本位币 币种 **/
                accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
                // 记账金额元素 putALl 随记账本位币金额map
                accountAmountMap.putAll(accountAmountBCCYAMTMap);
                /** modify by yanya on 20191230 增加本位币处理 end **/

                // 全局变量的更新
                /**modify BY CAOGUO ON 20201020 变量修改START **/
                // 变量.成本科目余额 = 变量.成本科目余额 + 交易明细.交易本金
                costBalance = ArithmeticUtil.doAdd(costBalance, varMz);
                // 变量.成本科目数量 = 变量.成本科目数量 + 交易明细.交易份额
                costQuantity = ArithmeticUtil.doAdd(costQuantity, dealNow.getShareAmt());
                // 变量.应收利息科目余额 = 变量.应收利息科目余额 + 交易明细.利息总额
                interestBalance = ArithmeticUtil.doAdd(interestBalance, varYjlx);   
                /**modify BY CAOGUO ON 20201020 END **/
                
                /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                // 变量.减值准备科目余额 = 变量.减值准备科目余额 + 变量.减值准备
                jzzbYe = ArithmeticUtil.doAdd(jzzbYe, varJzzb);
                jzzb = varJzzb;
                jzzbYeB = calculationCommonService.doMultiply(jzzbYe,汇率);
                jzzbB = accountAmountBCCYAMTMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT).negate();
                /**ADD BY CAOGUO ON 20201020 END**/
                
            }
            // 交易方向 = 收息到账
            else if (DictCodeList.交易方向_收息到账.getValue().equals(dealNow.getPs())) {

                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_存放同业收息.getValue());// ad.会计场景代码=存款类资产收息;

                // 定义:变量.其他应收款-利息科目余额 = 变量.其他应收款 - 利息科目余额MAP.get(交易明细.分支序号)
                BigDecimal otherInterestBalanc = otherInterestBalanceMap.get(dealNow.getBranch());

                // 变量.其他应收款-利息科目余额 == null
                if (otherInterestBalanc == null) {
                    // if(变量.标的信息.分类1 = 【数据字典】.存款类资产类型.活期类资产)
                    if (DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(
                        targetFinProduct.getType1())) {
                        // 查询交易的到期日对应的应计利息
                        SubjectBalance otherSubjectBalance = this.getSubjectBalance(booksetIdNow,
                            bokDetailId, DateUtil.addDay(dealNow.getMdate(), -1),
                            dealNow.getBooksetDate(), DictCodeList.余额方向_借方.getValue(),
                            DictCodeBaseSubjectNatrueNew.定期存款_应计利息.getValue(), null, null);

                        // 非空判断
                        if (otherSubjectBalance != null) {
                            // 变量.应收利息 = 上述共通返回的科目余额
                            otherInterestBalanc = otherSubjectBalance.getBalance();
                            // 变量.其他应收款-利息科目余额MAP.put(交易明细.分支序号，变量.应收利息)
                            otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);
                        }

                    }
                    else {
                        // 查询交易的到期日对应的应计利息
                        SubjectBalance otherSubjectBalance = this.getSubjectBalance(booksetIdNow,
                            bokDetailId, dealNow.getHappenDate(), dealNow.getBooksetDate(),
                            DictCodeList.余额方向_借方.getValue(),
                            DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(), dealNow.getBranch(),
                            taskPri);
                        // 非空判断
                        if (otherSubjectBalance != null) {
                            // 变量.其他应收款-利息科目余额 = 上述共通返回的科目余额
                            otherInterestBalanc = otherSubjectBalance.getBalance();
                            // 变量.其他应收款-利息科目余额MAP.put(交易明细.分支序号，变量.其他应收款-利息科目余额)
                            otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);
                        }
                    }

                }
                // 调用金额共通方法设置有效小数位
                otherInterestBalanc = ArithmeticUtil.round(otherInterestBalanc, NumberEnum.MONEY);

                /** modify by yanya on 20191230 增加本位币、通道费 处理 start **/
                // 实收金额 <- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
                // 通道费处理
                // 调用 共通【费用处理共通】 参考本设计书sheet【费用处理共通】 定义 变量.交易各种费用 = 返回值
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                    dealNow.getValTradeFeeDetailList(), dealNow.getProdId(),
                    dealNow.getPortfolioId(), dealNow.getFinprodId());
                // 实付通道费 <- 变量.交易各种费用.通道费
                BigDecimal 实付通道费 = dealTypesFeeDto.getChlFee();
                accountAmountMap.put(BookConstant.REAL_CHLAMT,
                    ArithmeticUtil.round(实付通道费, NumberEnum.MONEY));
                // 通道费账务明细代码 <- 变量.交易各种费用.通道费账务明细代码
                // 通道费业务明细代码 <- 变量.交易各种费用.通道费业务明细代码
                BokDetailId chlFeeCode = dealTypesFeeDto.getChlFeeCode();
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID3, chlFeeCode);

                // 定义：金额元素.应收利息
                BigDecimal yslxForJe = BigDecimal.ZERO;

                // if（交易明细.是否结清 = "是"）
                if (DictCodeList.是否_否.getValue().equals(dealNow.getIsClean())) {
                    // 金额元素.应收利息 <- 交易明细.交易金额
                    yslxForJe = ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY);
                    accountAmountMap.put(BookConstant.INTAMT, yslxForJe);
                    // 利息差额 <- 0
                    accountAmountMap.put(BookConstant.INCOMEAMT, BigDecimal.ZERO);

                }
                else {
                    // 金额元素：应收利息 <- 变量.其他应收款-利息科目余额
                    yslxForJe = otherInterestBalanc == null ? BigDecimal.ZERO : otherInterestBalanc;
                    accountAmountMap.put(BookConstant.INTAMT, yslxForJe);
                    // 交易明细.交易金额 - 金额元素.应收利息 > 0
                    // if 交易明细.交易金额 - 金额元素.应收利息 -实付通道费 > 0
                    BigDecimal subVal = ArithmeticUtil.doSub(dealNow.getTradeAmt(), yslxForJe,
                        实付通道费);
                    if (subVal.compareTo(BigDecimal.ZERO) > 0) {
                        // 利息差额_贷 <- 交易明细.交易金额 - 金额元素.应收利息 - 实付通道费
                        accountAmountMap.put(BookConstant.INCOMEAMT_C,
                            ArithmeticUtil.round(subVal, NumberEnum.MONEY.getCode(), null));
                    }
                    else {
                        // 利息差额_借 <- （交易明细.交易金额 - 金额元素.应收利息 - 实付通道费）*-1
                        accountAmountMap.put(BookConstant.INCOMEAMT_D,
                            ArithmeticUtil.round(
                                ArithmeticUtil.multiply(new BigDecimal("-1"), subVal),
                                NumberEnum.MONEY));
                    }
                }
                
                /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
                if((DictCodeListExt3.还本收息冲抵减值准备处理日_资金到账日.getValue().equals(offsetImptDateType)
                		|| DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1()))
                		&& BigDecimal.ZERO.compareTo(jzzbYe) != 0
                		){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow, 
                			DictCodeListExt1.现金流一级类型_利息.getValue(), 
                			//发生金额
                			yslxForJe, 
                			//减值准备余额
                			jzzbYe, 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                			ArithmeticUtil.doAdd(costBalance,interestBalance,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                			//应收款余额
                			otherInterestBalanc);
                	//冲抵减值金额
                	jzzb = lossCommonBatchService.calcLossAmtForDur(dto);
                }
                //减值准备  <- 变量.冲抵减值金额 * -1
                accountAmountMap.put(BookConstant.IMPAMT,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(jzzb,new BigDecimal(-1)), NumberEnum.MONEY));
                /**add by wujie on 20201031 end**/
                
                // 记账金额元素币种信息塞值--key是随记账金额map的
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealNow.getCcy());
                }
                // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
                BigDecimal 汇率 = BigDecimal.ONE;
                if (!dealNow.getCcy().equals(standardCcyNow)) {
                    // 调用计算类 方法共通【汇率查询】
                    // 基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
                    /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start **/
                    // 汇率 = calculationCommonService.queryExchangeRateForTrade(dealNow.getCcy(),
                    // standardCcyNow, happenDateNow);
                    List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                        bookDateNow, booksetIdNow, standardCcyNow, dealNow.getCcy());
                    if (!StringUtil.isEmptyList(currencyMarketList)
                        && currencyMarketList.get(0) != null) {
                        汇率 = currencyMarketList.get(0).getClosePrice();
                    }
                    /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 end **/
                    // 本币实收金额/本币实收成本（COST）<- 实收金额/本币实收成本（COST） * 汇率 用舍位处理后的结果计算并保留金额小数位
                    BigDecimal 本币实收金额 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.DEALAMT), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                        本币实收金额);
                    // 本币应收利息 <- 应收利息 * 汇率 用舍位处理后的结果计算并保留金额小数位
                    BigDecimal 本币应收利息 = ArithmeticUtil.round(
                        ArithmeticUtil.multiply(accountAmountMap.get(BookConstant.INTAMT), 汇率),
                        NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT,
                        本币应收利息);
                    // 本币实付通道费 <- 实付通道费 * 汇率
                    BigDecimal 本币实付通道费 = ArithmeticUtil.round(ArithmeticUtil.multiply(
                        accountAmountMap.get(BookConstant.REAL_CHLAMT), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT,
                        本币实付通道费);
                    // if 交易明细.利息总额 - 变量.冲减应收利息 - 实付通道费 >0
                    BigDecimal subVal = ArithmeticUtil.doSub(本币实收金额, 本币应收利息, 本币实付通道费);
                    if (subVal.compareTo(BigDecimal.ZERO) > 0) {
                        // 本币利息差额_贷 <- 本币实收金额/本币实收成本(COST)- 本币应收利息 - 本币实付通道费 保留金额小数位
                        accountAmountBCCYAMTMap.put(
                            BookConstant.INCOMEAMT_C + BookConstant.BCCYAMT,
                            ArithmeticUtil.round(subVal, NumberEnum.MONEY.getCode(), null));
                    }
                    // else
                    else {
                        // 本币利息差额_借 <- （本币实收金额/本币实收成本（COST）- 本币应收利息 - 本币实付通道费) * -1 保留金额小数位
                        accountAmountBCCYAMTMap.put(
                            BookConstant.INCOMEAMT_D + BookConstant.BCCYAMT,
                            ArithmeticUtil.round(
                                ArithmeticUtil.multiply(new BigDecimal("-1"), subVal),
                                NumberEnum.MONEY));
                    }
                   
                    BigDecimal 减值准备本币 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.IMPAMT), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.IMPAMT + BookConstant.BCCYAMT,
                        减值准备本币);
                }
                else {
                    // 本币实收金额/本币实收成本（COST）<- 实收金额/实收成本（COST）
                    // 本币应收利息<- 应收利息
                    // 本币利息差额_贷/借 <- 利息差额_贷/借
                    // 本币实付通道费 <- 实付通道费
                    for (String key : accountAmountMap.keySet()) {
                        accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                            accountAmountMap.get(key));
                    }
                }
                // 记账金额元素币种信息塞值--key是随记账本位币金额map的
                for (String key : accountAmountBCCYAMTMap.keySet()) {
                    accountCurrencyMap.put(key, standardCcyNow);
                }
                // add by weipeng on 20220222:会计引擎升级，收息到账场景增加对存款类活期类资产的利息金额元素特殊处理 start
                if (DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(
                        targetFinProduct.getType1())) {
                    // 金额元素替换为INTAMT_HQ
                    accountAmountMap.put(BookConstant.INTAMT_HQ, accountAmountMap.get(BookConstant.INTAMT));
                    accountAmountMap.put(BookConstant.INTAMT_HQ + BookConstant.BCCYAMT,
                            accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
                    // 清除原金额元素
                    accountAmountMap.remove(BookConstant.INTAMT);
                    accountAmountMap.remove(BookConstant.INTAMT + BookConstant.BCCYAMT);
                }
                // add by weipeng on 20220222:会计引擎升级，收息到账场景增加对存款类活期类资产的利息金额元素特殊处理 end
                /** 本位币 币种 **/
                accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
                // 记账金额元素 putALl 随记账本位币金额map
                accountAmountMap.putAll(accountAmountBCCYAMTMap);
                /** modify by yanya on 20191230 增加本位币、通道费 处理 end **/

                // 更新全局变量
                if (DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())) {
                    // 变量.其他应收款-利息科目余额 = 变量.应计利息科目余额 - 金额元素.应收利息
                    // otherInterestBalanc = ArithmeticUtil.doSub(interestBalance, yslxForJe);
                    // 变量.应收利息科目余额 =变量.应收利息科目余额 - 变量.冲减应收利息
                    interestBalance = ArithmeticUtil.doSub(interestBalance, yslxForJe);
                }
                else {
                    // 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 金额元素.应收利息
                    otherInterestBalanc = ArithmeticUtil.doSub(otherInterestBalanc, yslxForJe);
                    // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
                    otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);
                    /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
                    //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 - 金额元素.应收款项-利息
                    glb_qtysklxsum = ArithmeticUtil.doSub(glb_qtysklxsum,yslxForJe);
                    /**add by wujie on 20201031 end**/
                }
                /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
                //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
                jzzbYe = ArithmeticUtil.doSub(jzzbYe,jzzb);
                
                jzzbYeB = calculationCommonService.doMultiply(jzzbYe,汇率);
                jzzbB = accountAmountBCCYAMTMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT).negate();
                
                /**add by wujie on 20201031 end**/

                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<String, Map<String, Object>>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<String, Object>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
                if (DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())) {
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.定期存款_应计利息.getValue(),
                        accountNatrueBranchMap);
                }
                else {
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(),
                        accountNatrueBranchMap);
                }
                ad.setAccountNatrueMap(accountNatrueMap);

            }
            // 交易方向 = 卖出
            else if (DictCodeList.交易方向_卖出.getValue().equals(dealNow.getPs())
                     || DictCodeList.交易方向_提前支取.getValue().equals(dealNow.getPs())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_存放同业_到期或卖出.getValue());// ad.会计场景代码=会计场景代码_存放同业_到期或卖出;
                // 定义 变量.冲减应收利息
                BigDecimal scourInterest = BigDecimal.ZERO;
                // 计算冲减成本
                BigDecimal scourCost = BigDecimal.ZERO;
                // 计算冲减数量
                BigDecimal scourQuantity = BigDecimal.ZERO;
                // 活期存款类资产判断
                if (DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())) {
                    // 定义 变量.冲减应收利息 交易明细.利息金额
                    scourInterest = dealNow.getIntAmt();
                    // 计算冲减成本 变量.成本科目余额 * 交易明细.交易份额/变量.成本科目数量
                    scourCost = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(costBalance, dealNow.getShareAmt()), costQuantity,
                        NumberEnum.MONEY.getCode());
                    // 计算冲减数量 交易明细.交易份额
                    scourQuantity = dealNow.getShareAmt();
                }
                else {
                    // 定义 变量.冲减应收利息 ,设置有效小数位
                    scourInterest = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(interestBalance, dealNow.getShareAmt()),
                        costQuantity, NumberEnum.MONEY.getCode());
                    // 计算冲减成本
                    scourCost = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(costBalance, dealNow.getShareAmt()), costQuantity,
                        NumberEnum.MONEY.getCode());
                    // 计算冲减数量
                    scourQuantity = dealNow.getShareAmt();
                }
                
                // 记账金额元素map塞值
                // 本息和 <- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT, dealNow.getTradeAmt());
                // 本金金额 <- 变量.冲减成本
                accountAmountMap.put(BookConstant.COST, scourCost);
                // 应收利息 <- 变量.冲减应收利息
                accountAmountMap.put(BookConstant.INTAMT, scourInterest);
                /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                // 定义 变量.冲减减值准备 = 变量.减值准备科目余额 * 交易明细.交易份额/变量.成本科目数量          
                /**
                BigDecimal varJzzb = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(jzzbYe, dealNow.getShareAmt()), costQuantity,
                    NumberEnum.MONEY.getCode());
                **/
                BigDecimal varJzzb = BigDecimal.ZERO;
                LossAmtInputDto inputdto = this.getLossAmtInputDto(acctEntity, dealNow, 
                		null, 
                		//本次发生额 = 变量.冲减应计利息 + 变量.冲减成本
                		ArithmeticUtil.doAdd(scourCost,scourInterest), 
                		//减值准备余额
                		jzzbYe, 
                		//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                		ArithmeticUtil.doAdd(costBalance,interestBalance,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                		//应收款项余额
                		null);
                //发生份额
                inputdto.setHappenNum(scourQuantity);
            	//总份额
                inputdto.setTotalNum(costQuantity);
                
                varJzzb = lossCommonBatchService.calcLossAmtForDeal(inputdto);
                
                
                // 减值使用 <- 变量.冲减减值准备*-1
                accountAmountMap.put(BookConstant.IMPAMT, varJzzb.negate());
                /**ADD BY CAOGUO ON 20201020 END**/
                /** modify by yanya on 20191231 增加本位币、通道费 处理 start **/
                // 调用 共通【费用处理共通】 参考本设计书sheet【费用处理共通】
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                    dealNow.getValTradeFeeDetailList(), dealNow.getProdId(),
                    dealNow.getPortfolioId(), dealNow.getFinprodId());
                // 实付通道费 <- 变量.交易各种费用.通道费
                BigDecimal 实付通道费 = dealTypesFeeDto.getChlFee();
                accountAmountMap.put(BookConstant.REAL_CHLAMT,
                    ArithmeticUtil.round(实付通道费, NumberEnum.MONEY));
                // 通道费账务明细代码 <- 变量.交易各种费用.通道费账务明细代码
                // 通道费业务明细代码 <- 变量.交易各种费用.通道费业务明细代码
                BokDetailId chlFeeCode = dealTypesFeeDto.getChlFeeCode();
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, chlFeeCode);

                // 定义变量 交易明细.利息总额 - 变量.冲减应收利息 - 实付通道费
                BigDecimal subVal = ArithmeticUtil.doSub(dealNow.getIntAmt(), scourInterest,
                    实付通道费);
                // if 交易明细.利息总额 - 变量.冲减应收利息 - 实付通道费 >0
                if (subVal.compareTo(BigDecimal.ZERO) > 0) {
                    // 利息差额_贷 <- 交易明细.利息总额 - 变量.应收利息 - 实付通道费
                    accountAmountMap.put(BookConstant.INCOMEAMT_C,
                        ArithmeticUtil.round(subVal, NumberEnum.MONEY.getCode(), null));
                }
                else {
                    // 利息差额_借 <- （交易明细.利息总额 - 变量.应收利息 - 实付通道费 ）*-1
                    accountAmountMap.put(BookConstant.INCOMEAMT_D, ArithmeticUtil.round(
                        ArithmeticUtil.multiply(new BigDecimal("-1"), subVal), NumberEnum.MONEY));
                }

                // 记账金额元素币种信息塞值--key是随记账金额map的
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealNow.getCcy());
                }
                // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
                BigDecimal 汇率 = BigDecimal.ONE;
                if (!dealNow.getCcy().equals(standardCcyNow)) {
                    // 调用计算类 方法共通【汇率查询】
                    // 基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
                    List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                        bookDateNow, booksetIdNow, standardCcyNow, dealNow.getCcy());
                    if (!StringUtil.isEmptyList(currencyMarketList)
                        && currencyMarketList.get(0) != null) {
                        汇率 = currencyMarketList.get(0).getClosePrice();
                    }
                }
                // 本币实收金额/本币实收成本（COST）<- 实收金额/本币实收成本（COST） * 汇率 用舍位处理后的结果计算并保留金额小数位
                for (String key : accountAmountMap.keySet()) {
                    // 本币金额元素Map.put(金额元素+_本位币 , accountAmountMap.get(key) * 变量.汇率);保留金额元素小数位数
                    accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(
                            calculationCommonService.doMultiply(accountAmountMap.get(key), 汇率),
                            NumberEnum.MONEY));
                }
                /** 本位币 币种 **/
                accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
                accountAmountMap.putAll(accountAmountBCCYAMTMap);
                // 记账金额元素币种信息塞值--key是随记账本位币金额map的
                for (String key : accountAmountBCCYAMTMap.keySet()) {
                    accountCurrencyMap.put(key, standardCcyNow);
                }
                if (subVal.compareTo(BigDecimal.ZERO) > 0) {
                    // 本息和_本位币<- 本金金额_本位币 + 应收利息_本位币 + 本币实付通道费_本位币 + 本币利息差额_贷_本位币
                    BigDecimal 本息和 = ArithmeticUtil.doAdd(
                        accountAmountBCCYAMTMap.get(BookConstant.COST + BookConstant.BCCYAMT),
                        accountAmountBCCYAMTMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT),
                        accountAmountBCCYAMTMap.get(
                            BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT),
                        accountAmountBCCYAMTMap.get(
                            BookConstant.INCOMEAMT_C + BookConstant.BCCYAMT));
                    accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(本息和, NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                        standardCcyNow);
                }
                else {
                    // 本息和_本位币 <- 本金金额_本位币 + 应收利息_本位币 + 本币实付通道费_本位币 - 本币利息差额_借_本位币
                    BigDecimal 本息和 = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(
                        accountAmountBCCYAMTMap.get(BookConstant.COST + BookConstant.BCCYAMT),
                        accountAmountBCCYAMTMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT),
                        accountAmountBCCYAMTMap.get(
                            BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)),
                        accountAmountBCCYAMTMap.get(
                            BookConstant.INCOMEAMT_D + BookConstant.BCCYAMT));
                    accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(本息和, NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                        standardCcyNow);
                }

                /** modify by yanya on 20191231 增加本位币、通道费 处理 end **/

                // 更新全局变量
                // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
                costBalance = ArithmeticUtil.doSub(costBalance, scourCost);
                // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
                costQuantity = ArithmeticUtil.doSub(costQuantity, scourQuantity);
                // 变量.应收利息科目余额 = 变量.应收利息科目余额 - 变量.冲减应收利息
                interestBalance = ArithmeticUtil.doSub(interestBalance, scourInterest);
                /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                // 变量.减值准备科目余额 = 变量.减值准备科目余额 - 变量.减值准备
                jzzbYe = ArithmeticUtil.doSub(jzzbYe, varJzzb);
                jzzb = varJzzb.negate();
                jzzbYeB = calculationCommonService.doMultiply(jzzbYe,汇率);
                jzzbB = accountAmountBCCYAMTMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT);
                /**ADD BY CAOGUO ON 20201020 END**/

            }
            // 交易方向 = 还本到账
            else if (DictCodeList.交易方向_还本到账.getValue().equals(dealNow.getPs())) {

                // ad.会计场景代码=存放同业还本到账;
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_存放同业还本_到期.getValue());

                // 记账金额元素map塞值
                // 交易金额 <- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
                // 交易本金 <- 交易明细.全价总额
                accountAmountMap.put(BookConstant.COST,
                    ArithmeticUtil.round(dealNow.getFpriceAmt(), NumberEnum.MONEY));
                
                /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                //定义 变量.其他应收款-本金科目余额      = 变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)     
                BigDecimal qtyskBjBalAmt = otherBjBalanceMap.get(dealNow.getBranch());
                // if（变量.其他应收款-本金科目余额 == null）  
                if (qtyskBjBalAmt == null) {
                    qtyskBjBalAmt = BigDecimal.ZERO;
                    SubjectBalance otherBjBalance = this.getSubjectBalance(booksetIdNow,
                        bokDetailId, dealNow.getHappenDate(), dealNow.getBooksetDate(),
                        DictCodeList.余额方向_借方.getValue(),
                        DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), dealNow.getBranch(),
                        taskPri);
                    // 非空判断
                    if (otherBjBalance != null) {
                        // 变量.其他应收款-本金科目余额 = 上述共通返回的科目余额
                        qtyskBjBalAmt = otherBjBalance.getBalance();
                    }
                    // 变量.其他应收款-本金科目余额MAP.put(交易明细.分支序号，变量.其他应收款-本金科目余额)
                    otherBjBalanceMap.put(dealNow.getBranch(), qtyskBjBalAmt);
                }
                /**ADD BY CAOGUO ON 20201020 END**/
                
                /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
                if((DictCodeListExt3.还本收息冲抵减值准备处理日_资金到账日.getValue().equals(offsetImptDateType)
                		|| DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())
                		)
                		&& BigDecimal.ZERO.compareTo(jzzbYe) != 0
                		){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow, 
                			DictCodeListExt1.现金流一级类型_本金.getValue(), 
                			//发生金额
                			dealNow.getTradeAmt(), 
                			//减值准备余额
                			jzzbYe, 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                			ArithmeticUtil.doAdd(costBalance,interestBalance,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                			//应收款余额
                			qtyskBjBalAmt);
                	jzzb = lossCommonBatchService.calcLossAmtForDur(dto);
                }
                //减值准备  <- 变量.冲抵减值金额 * -1
                accountAmountMap.put(BookConstant.IMPAMT,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(jzzb,new BigDecimal(-1)), NumberEnum.MONEY));
                /**add by wujie on 20201031 end**/
                
                
                // 记账金额元素币种信息塞值--key是随记账金额map的
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealNow.getCcy());
                }
                // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
                BigDecimal 汇率 = BigDecimal.ONE;
                if (!dealNow.getCcy().equals(standardCcyNow)) {
                    // 调用计算类 方法共通【汇率查询】
                    // 基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
                    List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                        bookDateNow, booksetIdNow, standardCcyNow, dealNow.getCcy());
                    if (!StringUtil.isEmptyList(currencyMarketList)
                        && currencyMarketList.get(0) != null) {
                        汇率 = currencyMarketList.get(0).getClosePrice();
                    }
                }
                // 本币实收金额/本币实收成本（COST）<- 实收金额/本币实收成本（COST） * 汇率 用舍位处理后的结果计算并保留金额小数位
                for (String key : accountAmountMap.keySet()) {
                    // 本币金额元素Map.put(金额元素+_本位币 , accountAmountMap.get(key) * 变量.汇率);保留金额元素小数位数
                    accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(
                            calculationCommonService.doMultiply(accountAmountMap.get(key), 汇率),
                            NumberEnum.MONEY));
                }
                // 记账金额元素币种信息塞值--key是随记账本位币金额map的
                for (String key : accountAmountBCCYAMTMap.keySet()) {
                    accountCurrencyMap.put(key, standardCcyNow);
                }
                accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
                // 记账金额元素 putALl 随记账本位币金额map
                accountAmountMap.putAll(accountAmountBCCYAMTMap);

                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<String, Map<String, Object>>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<String, Object>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(),
                    accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
                
                /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                // 变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 交易明细.全价总额
                qtyskBjBalAmt = ArithmeticUtil.doSub(qtyskBjBalAmt, dealNow.getFpriceAmt());
                // 将变更后的余额更新到变量.其他应收款-本金科目余额MAP中
                otherBjBalanceMap.put(dealNow.getBranch(), qtyskBjBalAmt);
                /**ADD BY CAOGUO ON 20201020 END**/
                
                
                /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
                //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 - 金额元素.逾期款项-本金
                glb_qtyskbjsum = ArithmeticUtil.doSub(glb_qtyskbjsum,dealNow.getFpriceAmt());
                //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
                jzzbYe = ArithmeticUtil.doSub(jzzbYe,jzzb);
                jzzbYeB = calculationCommonService.doMultiply(jzzbYe,汇率);
                jzzbB = accountAmountBCCYAMTMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT).negate();
                /**add by wujie on 20201031 end**/
            }
            // 交易方向 = 应收款项转入
            else if (DictCodeList.交易方向_应收款项转入.getValue().equals(dealNow.getPs())) {

                // ad.会计场景代码="应收款项转仓(转入)"
                ad.setTableId(DictCodeListTableId.会计场景代码_应收款项转仓转入.getValue());

                // 定义:变量.其他应收款-利息科目余额 = 变量.其他应收款 - 利息科目余额MAP.get(交易明细.分支序号)
                BigDecimal otherInterestBalanc = otherInterestBalanceMap.get(dealNow.getBranch());

                // 变量.其他应收款-利息科目余额 == null
                if (otherInterestBalanc == null) {
                    // 查询交易的到期日对应的应计利息
                    SubjectBalance otherSubjectBalance = this.getSubjectBalance(booksetIdNow,
                        bokDetailId, dealNow.getHappenDate(), dealNow.getBooksetDate(),
                        DictCodeList.余额方向_借方.getValue(),
                        DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(), dealNow.getBranch(),
                        taskPri);

                    // 非空判断
                    if (otherSubjectBalance != null) {
                        // 变量.其他应收款-利息科目余额 = 上述共通返回的科目余额
                        otherInterestBalanc = otherSubjectBalance.getBalance();
                        // 变量.其他应收款-利息科目余额MAP.put(交易明细.分支序号，变量.其他应收款-利息科目余额)
                        otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);
                    }
                }

                // 调用金额共通方法设置有效小数位
                otherInterestBalanc = ArithmeticUtil.round(otherInterestBalanc, NumberEnum.MONEY);

                // 记账场景元素map塞值
                // 应收款项-利息 <- 交易明细.应计利息总额
                BigDecimal yskxlxForJe = ArithmeticUtil.round(dealNow.getIntAmt(),
                    NumberEnum.MONEY);
                accountAmountMap.put(BookConstant.INTAMT, yskxlxForJe);
                // 逾期款项-本金 <- 交易明细.交易本金
                accountAmountMap.put(BookConstant.COST,
                    ArithmeticUtil.round(dealNow.getPrinAmt(), NumberEnum.MONEY));
                // 实收金额 <- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));

                // 记账金额元素币种信息塞值--key是随记账金额map的
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealNow.getCcy());
                }
                /** modify by yanya on 20191230 增加本位币 处理 start **/
                // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
                BigDecimal 汇率 = BigDecimal.ONE;
                if (!dealNow.getCcy().equals(standardCcyNow)) {
                    // 调用计算类 方法共通【汇率查询】
                    // 基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
                    /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start **/
                    // 汇率 = calculationCommonService.queryExchangeRateForTrade(dealNow.getCcy(),
                    // standardCcyNow, happenDateNow);
                    List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                        bookDateNow, booksetIdNow, standardCcyNow, dealNow.getCcy());
                    if (!StringUtil.isEmptyList(currencyMarketList)
                        && currencyMarketList.get(0) != null) {
                        汇率 = currencyMarketList.get(0).getClosePrice();
                    }
                    /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 end **/
                    // 本币逾期款项-本金<- 逾期款项-本金 * 汇率 用舍位处理后的结果计算并保留金额小数位
                    BigDecimal 本币本金 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.COST), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.COST + BookConstant.BCCYAMT, 本币本金);
                    // 本币应收款项-利息 <- 应收款项-利息 * 汇率 用舍位处理后的结果计算并保留金额小数位
                    BigDecimal 本币利息 = ArithmeticUtil.round(
                        ArithmeticUtil.multiply(accountAmountMap.get(BookConstant.INTAMT), 汇率),
                        NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT, 本币利息);
                    // 本币实收金额 <- 本币逾期款项-本金 + 本币应收款项-利息
                    BigDecimal 本币实收金额 = ArithmeticUtil.round(ArithmeticUtil.doAdd(本币本金, 本币利息),
                        NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                        本币实收金额);   

                }
                else {
                    // 本币应收款项-利息 <- 应收款项-利息
                    accountAmountBCCYAMTMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT,
                        yskxlxForJe);
                    // 本币逾期款项-本金 <- 逾期款项-本金
                    accountAmountBCCYAMTMap.put(BookConstant.COST + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(dealNow.getPrinAmt(), NumberEnum.MONEY));
                    // 本币实收金额 <- 实收金额
                    accountAmountBCCYAMTMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));

                }
                // 记账金额元素币种信息塞值--key是随记账本位币金额map的
                for (String key : accountAmountBCCYAMTMap.keySet()) {
                    accountCurrencyMap.put(key, standardCcyNow);
                }
                /** 本位币 币种 **/
                accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
                // 记账金额元素 putALl 随记账本位币金额map
                accountAmountMap.putAll(accountAmountBCCYAMTMap);
                /** modify by yanya on 20191230 增加本位币 处理 end **/

                // 更新全局变量
                // 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 + 金额元素.应收款项-利息
                otherInterestBalanc = ArithmeticUtil.doAdd(otherInterestBalanc, yskxlxForJe);
                // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
                otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);

                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<String, Map<String, Object>>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<String, Object>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(),
                    accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(),
                    accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
                
                /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                //定义 变量.其他应收款-本金科目余额      = 变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)     
                BigDecimal qtyskBjBalAmt = otherBjBalanceMap.get(dealNow.getBranch());
                // if（变量.其他应收款-本金科目余额 == null）  
                if (qtyskBjBalAmt == null) {
                    qtyskBjBalAmt = BigDecimal.ZERO;
                    SubjectBalance otherBjBalance = this.getSubjectBalance(booksetIdNow,
                        bokDetailId, dealNow.getHappenDate(), dealNow.getBooksetDate(),
                        DictCodeList.余额方向_借方.getValue(),
                        DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), dealNow.getBranch(),
                        taskPri);
                    // 非空判断
                    if (otherBjBalance != null) {
                        // 变量.其他应收款-本金科目余额 = 上述共通返回的科目余额
                        qtyskBjBalAmt = otherBjBalance.getBalance();
                    }
                    // 变量.其他应收款-本金科目余额MAP.put(交易明细.分支序号，变量.其他应收款-本金科目余额)
                    otherBjBalanceMap.put(dealNow.getBranch(), qtyskBjBalAmt);
                }
                // 变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 交易明细.全价总额
                qtyskBjBalAmt = ArithmeticUtil.doAdd(qtyskBjBalAmt, dealNow.getPrinAmt());
                // 将变更后的余额更新到变量.其他应收款-本金科目余额MAP中
                otherBjBalanceMap.put(dealNow.getBranch(), qtyskBjBalAmt);
                /**ADD BY CAOGUO ON 20201020 END**/
                
                /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
                //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 + 金额元素.应收款项-利息
                glb_qtysklxsum = ArithmeticUtil.doAdd(glb_qtysklxsum,yskxlxForJe);
                //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 + 金额元素.逾期款项-本金
                glb_qtyskbjsum = ArithmeticUtil.doAdd(glb_qtyskbjsum,dealNow.getPrinAmt());
                /**add by wujie on 20201031 end**/

            }
            // 交易方向 = 应收款项转出
            else if (DictCodeList.交易方向_应收款项转出.getValue().equals(dealNow.getPs())) {

                // ad.会计场景代码="应收款项转仓(转出)"
                ad.setTableId(DictCodeListTableId.会计场景代码_应收款项转仓转出.getValue());
                // 境内外 = 变量.交易信息.境内外
                accountSceneMap.put(BookConstant.BLN_AREA, targetFinProduct.getBlnArea());
                // 资产类型
                accountSceneMap.put(BookConstant.ASSET_TYPE, targetFinProduct.getFinprodType());

                /** modif by yanya on 20201217 应收款项转出增加记账元素 是否其他债权 **/
                accountSceneMap.put(BookConstant.IS_QTZQ, targetFinProduct.getIsQtzq());

                // 定义:变量.其他应收款-利息科目余额 = 变量.其他应收款 - 利息科目余额MAP.get(交易明细.分支序号)
                BigDecimal otherInterestBalanc = otherInterestBalanceMap.get(dealNow.getBranch());
                // 变量.其他应收款-利息科目余额 == null
                if (otherInterestBalanc == null) {
                    // 查询交易的到期日对应的应计利息
                    SubjectBalance otherSubjectBalance = this.getSubjectBalance(booksetIdNow,
                        bokDetailId, dealNow.getHappenDate(), dealNow.getBooksetDate(),
                        DictCodeList.余额方向_借方.getValue(),
                        DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(), dealNow.getBranch(),
                        taskPri);

                    // 非空判断
                    if (otherSubjectBalance != null) {
                        // 变量.其他应收款-利息科目余额 = 上述共通返回的科目余额
                        otherInterestBalanc = otherSubjectBalance.getBalance();
                        // 变量.其他应收款-利息科目余额MAP.put(交易明细.分支序号，变量.其他应收款-利息科目余额)
                        otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);
                    }
                }
                

                // 调用金额共通方法设置有效小数位
                otherInterestBalanc = ArithmeticUtil.round(otherInterestBalanc, NumberEnum.MONEY);

                // 记账场景元素map塞值
                // 定义：变量.应计利息
                BigDecimal yjlxNow = BigDecimal.ZERO;
                // 定义：变量.轧差金额
                BigDecimal zcjeNow = BigDecimal.ZERO;
                // if（交易明细.是否结清 = "否"）
                if (DictCodeList.是否_否.getValue().equals(dealNow.getIsClean())) {
                    // 变量.应计利息 <- 交易明细.应计利息总额
                    yjlxNow = dealNow.getIntAmt();
                    // 变量.轧差金额 <- 0
                    zcjeNow = BigDecimal.ZERO;
                }
                else {
                    // 变量.应计利息 <- 变量.其他应收款-利息科目余额
                    yjlxNow = otherInterestBalanc;
                    // 变量.轧差金额 = 交易明细.应计利息总额 - 变量.应计利息
                    zcjeNow = ArithmeticUtil.doSub(dealNow.getIntAmt(), yjlxNow);

                    // 变量.轧差金额 >= 0
                    if (zcjeNow.compareTo(BigDecimal.ZERO) >= 0) {
                        // 轧差金额(贷) <- 变量.利息差额(变量.轧差金额)
                        accountAmountMap.put(BookConstant.NETAMT_C,
                            ArithmeticUtil.round(zcjeNow, NumberEnum.MONEY));
                    }
                    else {
                        // 轧差金额(借) <- 变量.利息差额(变量.轧差金额)*-1
                        accountAmountMap.put(BookConstant.NETAMT_D,
                            ArithmeticUtil.round(zcjeNow, NumberEnum.MONEY).abs());
                    }
                }

                // 应收款项-利息 <- 变量.应计利息
                BigDecimal yskxlxForJe = ArithmeticUtil.round(yjlxNow, NumberEnum.MONEY);
                accountAmountMap.put(BookConstant.INTAMT, yskxlxForJe);
                // 逾期款项-本金 <- 交易明细.交易本金
                accountAmountMap.put(BookConstant.COST,
                    ArithmeticUtil.round(dealNow.getPrinAmt(), NumberEnum.MONEY));
                // 实收金额 <- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
                
                /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                //定义 变量.其他应收款-本金科目余额      = 变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)     
                BigDecimal qtyskBjBalAmt = otherBjBalanceMap.get(dealNow.getBranch());
                // if（变量.其他应收款-本金科目余额 == null）  
                if (qtyskBjBalAmt == null) {
                    qtyskBjBalAmt = BigDecimal.ZERO;
                    SubjectBalance otherBjBalance = this.getSubjectBalance(booksetIdNow,
                        bokDetailId, dealNow.getHappenDate(), dealNow.getBooksetDate(),
                        DictCodeList.余额方向_借方.getValue(),
                        DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), dealNow.getBranch(),
                        taskPri);
                    // 非空判断
                    if (otherBjBalance != null) {
                        // 变量.其他应收款-本金科目余额 = 上述共通返回的科目余额
                        qtyskBjBalAmt = otherBjBalance.getBalance();
                    }
                    // 变量.其他应收款-本金科目余额MAP.put(交易明细.分支序号，变量.其他应收款-利息科目余额)
                    otherBjBalanceMap.put(dealNow.getBranch(), qtyskBjBalAmt);
                }
                /**ADD BY CAOGUO ON 20201020 END**/
                
                
                /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
                if((DictCodeListExt3.还本收息冲抵减值准备处理日_资金到账日.getValue().equals(offsetImptDateType)
                		|| DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())
                		)
                		&& BigDecimal.ZERO.compareTo(jzzbYe.subtract(jzzb)) != 0
                		){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow, 
                			DictCodeListExt1.现金流一级类型_利息.getValue(), 
                			//发生金额
                			yskxlxForJe, 
                			//减值准备余额
                			jzzbYe, 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                			ArithmeticUtil.doAdd(costBalance,interestBalance,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                			//应收款余额
                			otherInterestBalanc);
                	jzzb = ArithmeticUtil.doAdd(jzzb,lossCommonBatchService.calcLossAmtForDur(dto));
                }
                
                if((DictCodeListExt3.还本收息冲抵减值准备处理日_资金到账日.getValue().equals(offsetImptDateType)
                		|| DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())
                		)
                		&& BigDecimal.ZERO.compareTo(jzzbYe.subtract(jzzb)) != 0
                		){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow, 
                			DictCodeListExt1.现金流一级类型_本金.getValue(), 
                			//发生金额
                			dealNow.getPrinAmt(), 
                			//减值准备余额
                			jzzbYe.subtract(jzzb), 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                			ArithmeticUtil.doAdd(costBalance,interestBalance,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                			//应收款余额
                			qtyskBjBalAmt);
                	jzzb = ArithmeticUtil.doAdd(jzzb,lossCommonBatchService.calcLossAmtForDur(dto));
                }
                //减值准备  <- 变量.冲抵减值金额 * -1
                accountAmountMap.put(BookConstant.IMPAMT,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(jzzb,new BigDecimal(-1)), NumberEnum.MONEY));
                /**add by wujie on 20201031 end remark:资产减值：卖出、还本到账、收息到账**/

                
                // 记账金额元素币种信息塞值--key是随记账金额map的
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealNow.getCcy());
                }

                // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
                BigDecimal 汇率 = BigDecimal.ONE;
                if (!dealNow.getCcy().equals(standardCcyNow)) {
                    // 调用计算类 方法共通【汇率查询】
                    // 基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
                    List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                        bookDateNow, booksetIdNow, standardCcyNow, dealNow.getCcy());
                    if (!StringUtil.isEmptyList(currencyMarketList)
                        && currencyMarketList.get(0) != null) {
                        汇率 = currencyMarketList.get(0).getClosePrice();
                    }
                    // 本币应收款项-利息 <- 本金金额 * 汇率
                    BigDecimal 本币利息 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.INTAMT), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT, 本币利息);
                    // 本币逾期款项-本金 <- 逾期款项-本金 * 汇率 用舍位处理后的结果计算并保留金额小数位
                    BigDecimal 本币本金 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.COST), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.COST + BookConstant.BCCYAMT, 本币本金);
                    // 本币实收金额 <- 实收金额 * 汇率 用舍位处理后的结果计算并保留金额小数位
                    BigDecimal 本币实收金额 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.DEALAMT), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,
                        本币实收金额);
                    // 变量.本币轧差金额 = 本币实收金额 - 本币逾期款项-本金 - 本币应收款项-利息
                    BigDecimal 本币轧差金额 = ArithmeticUtil.doSub(本币实收金额, 本币本金, 本币利息);
                    // if 变量.本币轧差金额 >= 0
                    if (本币轧差金额.compareTo(BigDecimal.ZERO) > 0) {
                        // 本币轧差金额(贷) <- 变量.本币轧差金额
                        accountAmountBCCYAMTMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT,
                            ArithmeticUtil.round(本币轧差金额, NumberEnum.MONEY));
                    }
                    else {
                        // 本币轧差金额(借) <- 变量.本币轧差金额*-1
                        accountAmountBCCYAMTMap.put(BookConstant.NETAMT_D + BookConstant.BCCYAMT,
                            ArithmeticUtil.round(
                                ArithmeticUtil.multiply(本币轧差金额, new BigDecimal(-1)),
                                NumberEnum.MONEY));
                    }
                    BigDecimal 减值准备本币 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.IMPAMT), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.IMPAMT + BookConstant.BCCYAMT,
                        减值准备本币);

                }
                else {
                    for (String key : accountAmountMap.keySet()) {
                        accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                            accountAmountMap.get(key));
                    }
                }
                // 记账金额元素币种信息塞值--key是随记账本位币金额map的
                for (String key : accountAmountBCCYAMTMap.keySet()) {
                    accountCurrencyMap.put(key, standardCcyNow);
                }
                accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
                // 记账金额元素 putALl 随记账本位币金额map
                accountAmountMap.putAll(accountAmountBCCYAMTMap);

                // 更新全局变量
                // 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 金额元素.应收款项-利息
                otherInterestBalanc = ArithmeticUtil.doSub(otherInterestBalanc, yskxlxForJe);
                // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
                otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);

                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<String, Map<String, Object>>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<String, Object>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(),
                    accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(),
                    accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
                
                /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
                // 变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 交易明细.全价总额
                qtyskBjBalAmt = ArithmeticUtil.doSub(qtyskBjBalAmt, dealNow.getPrinAmt());
                // 将变更后的余额更新到变量.其他应收款-本金科目余额MAP中
                otherBjBalanceMap.put(dealNow.getBranch(), qtyskBjBalAmt);
                /**ADD BY CAOGUO ON 20201020 END**/
                
                /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
                //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 - 金额元素.应收款项-利息
                glb_qtysklxsum = ArithmeticUtil.doSub(glb_qtysklxsum,yskxlxForJe);
                //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 - 金额元素.逾期款项-本金
                glb_qtyskbjsum = ArithmeticUtil.doSub(glb_qtyskbjsum,dealNow.getPrinAmt());
                //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
                jzzbYe = ArithmeticUtil.doSub(jzzbYe,jzzb);
                jzzbYeB = calculationCommonService.doMultiply(jzzbYe,汇率);
                jzzbB = accountAmountBCCYAMTMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT).negate();
                /**add by wujie on 20201031 end remark:资产减值：卖出、还本到账、收息到账**/
            }
            // 交易方向_红利再投 add by yanya on 20191230
            else if (DictCodeList.交易方向_红利再投.getValue().equals(dealNow.getPs())) {

                // ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_存放同业收息.getValue());//
                // ad.会计场景代码=存款类资产收息;
                ad.setTableId(DictCodeListTableId.会计场景代码_存放同业_红利再投.getValue());// ad.会计场景代码=存放同业_红利再投;

                // 定义:变量.其他应收款-利息科目余额 = 变量.其他应收款 - 利息科目余额MAP.get(交易明细.分支序号)
                BigDecimal otherInterestBalanc = otherInterestBalanceMap.get(dealNow.getBranch());

                // 变量.其他应收款-利息科目余额 == null
                if (otherInterestBalanc == null) {
                    // if(变量.标的信息.分类1 = 【数据字典】.存款类资产类型.活期类资产)
                    if (DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(
                        targetFinProduct.getType1())) {
                        // 查询交易的到期日对应的应计利息
                        SubjectBalance otherSubjectBalance = this.getSubjectBalance(booksetIdNow,
                            bokDetailId, DateUtil.addDay(dealNow.getMdate(), -1),
                            dealNow.getBooksetDate(), DictCodeList.余额方向_借方.getValue(),
                            DictCodeBaseSubjectNatrueNew.定期存款_应计利息.getValue(), null, null);

                        // 非空判断
                        if (otherSubjectBalance != null) {
                            // 变量.应收利息 = 上述共通返回的科目余额
                            otherInterestBalanc = otherSubjectBalance.getBalance();
                            // 变量.其他应收款-利息科目余额MAP.put(交易明细.分支序号，变量.应收利息)
                            otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);
                        }

                    }
                    else {
                        // 查询交易的到期日对应的应计利息
                        SubjectBalance otherSubjectBalance = this.getSubjectBalance(booksetIdNow,
                            bokDetailId, dealNow.getHappenDate(), dealNow.getBooksetDate(),
                            DictCodeList.余额方向_借方.getValue(),
                            DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(), dealNow.getBranch(),
                            taskPri);

                        // 非空判断
                        if (otherSubjectBalance != null) {
                            // 变量.其他应收款-利息科目余额 = 上述共通返回的科目余额
                            otherInterestBalanc = otherSubjectBalance.getBalance();
                            // 变量.其他应收款-利息科目余额MAP.put(交易明细.分支序号，变量.其他应收款-利息科目余额)
                            otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);
                        }
                    }

                }

                // 调用金额共通方法设置有效小数位
                otherInterestBalanc = ArithmeticUtil.round(otherInterestBalanc, NumberEnum.MONEY);

                // 实收成本(COST) <- 交易明细.交易份额
                // accountAmountMap.put(BookConstant.COST,
                // ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY));
                // 转投金额 <- 交易明细.交易份额
                // 实收成本(COST) <- 交易明细.交易份额
                accountAmountMap.put(BookConstant.TRADE_QTY,
                    ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY));

                // 定义：金额元素.应收利息
                BigDecimal yslxForJe = BigDecimal.ZERO;

                // if（交易明细.是否结清 = "是"）
                if (DictCodeList.是否_否.getValue().equals(dealNow.getIsClean())) {
                    // 金额元素.应收利息 <- 交易明细.交易金额
                    // yslxForJe = ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY);
                    // 金额元素.应收利息 <- 交易明细.交易本金 modify by yanya on 20200216
                    yslxForJe = ArithmeticUtil.round(dealNow.getPrinAmt(), NumberEnum.MONEY);
                    accountAmountMap.put(BookConstant.INTAMT, yslxForJe);
                    // 利息差额 <- 0
                    accountAmountMap.put(BookConstant.INCOMEAMT, BigDecimal.ZERO);

                }
                else {
                    // 金额元素：应收利息 <- 变量.其他应收款-利息科目余额
                    yslxForJe = otherInterestBalanc == null ? BigDecimal.ZERO : otherInterestBalanc;
                    accountAmountMap.put(BookConstant.INTAMT, yslxForJe);
                    // 交易明细.交易金额 - 金额元素.应收利息 > 0
                    // if 交易明细.交易金额 - 金额元素.应收利息 > 0
                    // if 交易明细.交易本金 - 金额元素.应收利息
                    // BigDecimal subVal = ArithmeticUtil.doSub(dealNow.getTradeAmt(), yslxForJe);
                    BigDecimal subVal = ArithmeticUtil.doSub(dealNow.getPrinAmt(), yslxForJe);
                    if (subVal.compareTo(BigDecimal.ZERO) > 0) {
                        // 利息差额_贷 <- 交易明细.交易本金 - 金额元素.应收利息
                        accountAmountMap.put(BookConstant.INCOMEAMT_C,
                            ArithmeticUtil.round(subVal, NumberEnum.MONEY.getCode(), null));
                    }
                    else {
                        // 利息差额_借 <- （交易明细.交易本金 - 金额元素.应收利息）*-1
                        accountAmountMap.put(BookConstant.INCOMEAMT_D,
                            ArithmeticUtil.round(
                                ArithmeticUtil.multiply(new BigDecimal("-1"), subVal),
                                NumberEnum.MONEY));
                    }
                }
                // 记账金额元素币种信息塞值--key是随记账金额map的
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealNow.getCcy());
                }
                // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
                BigDecimal 汇率 = BigDecimal.ONE;
                if (!dealNow.getCcy().equals(standardCcyNow)) {
                    // 调用计算类 方法共通【汇率查询】
                    // 基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
                    /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start **/
                    // 汇率 = calculationCommonService.queryExchangeRateForTrade(dealNow.getCcy(),
                    // standardCcyNow, happenDateNow);
                    List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                        bookDateNow, booksetIdNow, standardCcyNow, dealNow.getCcy());
                    if (!StringUtil.isEmptyList(currencyMarketList)
                        && currencyMarketList.get(0) != null) {
                        汇率 = currencyMarketList.get(0).getClosePrice();
                    }
                    /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 end **/
                    // 本币实收金额/本币实收成本（COST）<- 实收金额/本币实收成本（COST） * 汇率 用舍位处理后的结果计算并保留金额小数位
                    // 本币实收金额/本币实收成本（COST）<- 转投金额（TRADE_QTY） * 汇率 用舍位处理后的结果计算并保留金额小数位
                    BigDecimal 本币实收金额 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.TRADE_QTY), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.TRADE_QTY + BookConstant.BCCYAMT,
                        本币实收金额);
                    // 本币应收利息 <- 应收利息 * 汇率 用舍位处理后的结果计算并保留金额小数位
                    BigDecimal 本币应收利息 = ArithmeticUtil.round(calculationCommonService.doMultiply(
                        accountAmountMap.get(BookConstant.INTAMT), 汇率), NumberEnum.MONEY);
                    accountAmountBCCYAMTMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT,
                        本币应收利息);
                    // if 交易明细.利息总额 - 变量.冲减应收利息 >0
                    BigDecimal subVal = ArithmeticUtil.doSub(本币实收金额, 本币应收利息);
                    if (subVal.compareTo(BigDecimal.ZERO) > 0) {
                        // 本币利息差额_贷 <- 本币实收金额/本币实收成本(COST)- 本币应收利息 保留金额小数位
                        accountAmountBCCYAMTMap.put(
                            BookConstant.INCOMEAMT_C + BookConstant.BCCYAMT,
                            ArithmeticUtil.round(subVal, NumberEnum.MONEY.getCode(), null));
                    }
                    // else
                    else {
                        // 本币利息差额_借 <- （本币实收金额/本币实收成本（COST）- 本币应收利息) * -1 保留金额小数位
                        accountAmountBCCYAMTMap.put(
                            BookConstant.INCOMEAMT_D + BookConstant.BCCYAMT,
                            ArithmeticUtil.round(
                                ArithmeticUtil.multiply(new BigDecimal("-1"), subVal),
                                NumberEnum.MONEY));
                    }

                }
                else {
                    // 本币实收金额/本币实收成本（COST）<- 实收金额/实收成本（COST）
                    // 本币应收利息<- 应收利息
                    // 本币利息差额_贷/借 <- 利息差额_贷/借
                    for (String key : accountAmountMap.keySet()) {
                        accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                            accountAmountMap.get(key));
                    }
                }
                // 记账金额元素币种信息塞值--key是随记账本位币金额map的
                for (String key : accountAmountBCCYAMTMap.keySet()) {
                    accountCurrencyMap.put(key, standardCcyNow);
                }
                /** 本位币 币种 **/
                accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
                // 记账金额元素 putALl 随记账本位币金额map
                accountAmountMap.putAll(accountAmountBCCYAMTMap);

                // 更新全局变量
                if (DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())) {
                    // 变量.其他应收款-利息科目余额 = 变量.应计利息科目余额 - 金额元素.应收利息
                    // otherInterestBalanc = ArithmeticUtil.doSub(interestBalance, yslxForJe);
                    // 变量.应收利息科目余额 =变量.应收利息科目余额 - 变量.冲减应收利息
                    interestBalance = ArithmeticUtil.doSub(interestBalance, yslxForJe);
                }
                else {
                    // 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 金额元素.应收利息
                    otherInterestBalanc = ArithmeticUtil.doSub(otherInterestBalanc, yslxForJe);
                    // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
                    otherInterestBalanceMap.put(dealNow.getBranch(), otherInterestBalanc);
                }

                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<String, Map<String, Object>>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<String, Object>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
                if (DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())) {
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.定期存款_应计利息.getValue(),
                        accountNatrueBranchMap);
                }
                else {
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(),
                        accountNatrueBranchMap);
                }
                ad.setAccountNatrueMap(accountNatrueMap);
            /** ADD BY CAOGUO ON 20201020 减值相关修改 START **/ 
            } else if (DictCodeListExt3.交易方向_清理核销.getValue().equals(dealNow.getPs())) {
                // ad.会计场景代码=存款类资产收息;
                ad.setTableId(DictCodeListTableId.会计场景代码_存放同业_减值清理.getValue());
                // nvl(交易明细.汇总交易编号,交易明细.交易编号);
                if (StringUtil.isNotEmpty(dealNow.getTotleTradeId())) {
                    accountSceneMap.put(BookConstant.TRADE_ID, dealNow.getTotleTradeId());
                }
                // 变量.清理应计利息 = 0
                BigDecimal cjYjLx = BigDecimal.ZERO;
                // 变量.冲减数量 = 交易明细.交易份额
                BigDecimal cjSl = dealNow.getShareAmt() == null ? BigDecimal.ZERO : dealNow.getShareAmt();
                // 变量.清理面值 = 0
                BigDecimal cjCb = BigDecimal.ZERO;
                // 变量.减值准备 = 0
                BigDecimal cjjzzb = BigDecimal.ZERO;
                // 变量.减值损失 = 0
                BigDecimal jzzbss = BigDecimal.ZERO;
                // if (交易明细.是否结清 = "是")
                if (DictCodeList.是否_是.getValue().equals(dealNow.getIsClean())) {
                    cjYjLx = interestBalance;
                    cjCb = costBalance;
                    cjjzzb = jzzbYe;
                    cjSl = costQuantity;
                }
                //变量.减值损失 = 变量.清理成本+变量.清理减值准备+变量.清理应计利息
                jzzbss = ArithmeticUtil.doAdd(cjCb,cjjzzb,cjYjLx);
                // 面值 <- 变量.冲减成本
                accountAmountMap.put(BookConstant.COST, ArithmeticUtil.round(cjCb, NumberEnum.MONEY));
                // 应计利息 <- 变量.冲减应计利息
                accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(cjYjLx, NumberEnum.MONEY));
                // if（变量.减值损失 >= 0）
                if (jzzbss.compareTo(BigDecimal.ZERO) >= 0) {
                    // 减值损失(借) <- 变量.减值损失
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_D,
                        ArithmeticUtil.round(jzzbss, NumberEnum.MONEY));
                }
                else {
                    // 减值损失(贷) <- 变量.减值损失*-1
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_C, ArithmeticUtil.round(
                        ArithmeticUtil.multiply(jzzbss, new BigDecimal(-1)), NumberEnum.MONEY));
                }
                // 减值使用 <- 变量.减值使用*-1
                accountAmountMap.put(BookConstant.IMPAMT, cjjzzb.negate());
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
                //本位币金额元素赋值 循环金额元素
                for (String key : accountAmountMap.keySet()) {
                    accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                            ArithmeticUtil.round(calculationCommonService.doMultiply(accountAmountMap.get(key), 汇率),NumberEnum.MONEY));
                }
                //循环本币金额元素，赋值本币
                for (String key : accountAmountBCCYAMTMap.keySet()) {
                    accountCurrencyMap.put(key, standardCcyNow);// value值塞入当前交易币种
                }
                /** 本位币 币种**/
                accountCurrencyMap.put(BookConstant.B_CCY, standardCcyNow);
                //记账金额元素 putALl 随记账本位币金额map
                accountAmountMap.putAll(accountAmountBCCYAMTMap);
                //accountAmountMap再次设置如下：
                // 变量.减值损失_本位币 = 金额元素.面值_本位币+金额元素.应计利息_本位币   
                BigDecimal jzssB = ArithmeticUtil.doAdd(
                    accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT),            
                    accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
                // 变量.减值损失_本位币 =变量.减值损失_本位币  -金额元素.减值准备_本位币
                jzssB = ArithmeticUtil.doSub(jzssB,
                    accountAmountMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT));
                // if（变量.减值损失_本位币 >= 0）
                if (jzssB.compareTo(BigDecimal.ZERO) >= 0) {
                    // 减值损失(借)_本位币 <- 变量.减值损失_本位币
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(jzssB, NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT,
                        standardCcyNow);// value值塞入当前交易币种
                }
                else {
                    // 减值损失(贷)_本位币 <- 变量.减值损失_本位币*-1
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(ArithmeticUtil.multiply(jzssB, new BigDecimal(-1)),
                            NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT,
                        standardCcyNow);
                }
                // 全局变量的更新
                // 变量.成本科目余额 = 变量.成本科目余额 - 冲减成本
                costBalance = ArithmeticUtil.doSub(costBalance, cjCb);
                // 变量.成本科目数量 = 变量.成本科目数量 - 冲减数量
                costQuantity = ArithmeticUtil.doSub(costQuantity, cjSl);
                // 变量.应计利息科目余额 = 变量.应计利息科目余额 - 变量.冲减应计利息
                interestBalance = ArithmeticUtil.doSub(interestBalance, cjYjLx);
                // 变量.减值准备科目余额 = 变量.减值准备科目余额 - 变量.减值使用
                jzzbYe = ArithmeticUtil.subtract(jzzbYe, cjjzzb);
                jzzb = cjjzzb.negate();
                jzzbYeB = calculationCommonService.doMultiply(jzzbYe,汇率);
                jzzbB = accountAmountBCCYAMTMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT);
            }
            else if (DictCodeListExt3.交易方向_其他应收款本金清理核销.getValue().equals(dealNow.getPs())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_存放同业_减值清理.getValue());
                cleanYskbj(acctEntity, booksetIdNow, bokDetailId, taskPri,
                    standardCcyNow, otherBjBalanceMap, dealNow, accountSceneMap, accountAmountMap,
                    accountCurrencyMap, glb_qtyskbjsum, glb_qilxsum, fxBalanceMap);
                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(),
                    accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收逾期款_利息.getValue(),
                        accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
            }
            else if (DictCodeListExt3.交易方向_其他应收款利息清理核销.getValue().equals(dealNow.getPs())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_存放同业_减值清理.getValue());
                cleanYsklx(acctEntity, booksetIdNow, bokDetailId, taskPri,
                    standardCcyNow, otherInterestBalanceMap, otherBjBalanceMap, dealNow,
                    accountSceneMap, accountAmountMap, accountCurrencyMap, glb_qtysklxsum,
                    glb_qilxsum, fxBalanceMap);
                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(),
                    accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收逾期款_利息.getValue(),
                        accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
            }
            /** ADD BY CAOGUO ON 20201020 END **/
            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
            ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
            returnResult.getValBokBookdetailBatch().addAll(
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                resultNowNow.getValBokBookdetailBatch());
            /** ADD BY CAOGUO ON 20201020 减值相关修改 START **/
            if (jzzb.compareTo(BigDecimal.ZERO) != 0) {
                BokStatLossPosiiton statLoss = null;
                for (ValBokBookdetailBatch detail : resultNowNow.getValBokBookdetailBatch()) {
                    // 变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 ="存放同业减值准备"的第一条数据
                    if (DictCodeBaseSubjectNatrueNew.定期存款_减值准备.getValue().equals(
                        detail.getBaseSubjectNature())) {
                        if (DictCodeList.交易方向_买入.getValue().equals(dealNow.getPs())) {
                            // 调用【减值相关共通-减值计提明细数据生成】
                            statLoss = lossCommonService.setLossInfoAccr(detail, jzzb, jzzbB,
                                jzzbYe, jzzbYeB, dealNow.getTradeId(), null);
                            break;
                        }
                        else if (DictCodeListExt3.交易方向_清理核销.getValue().equals(dealNow.getPs())) {
                            statLoss = lossCommonService.setLossInfoClean(detail, jzzb, jzzbB,
                                jzzbYe, jzzbYeB, dealNow.getTradeId());
                            break;
                        }
                        else {
                            // 调用【减值相关共通-减值计提明细数据生成】
                            statLoss = lossCommonService.setLossInfoUsed(detail, jzzb, jzzbB,
                                jzzbYe, jzzbYeB, dealNow.getTradeId());
                            break;
                        }
                    }
                }
                if (statLoss != null) {
                    // 变量.统计分析明细表（资产减值）减值使用列表.add(上述返回值)
                    returnResult.getBokStatLossPosiitonList().add(statLoss);
                }
            }
            /** ADD BY CAOGUO ON 20201020 END **/
        }

        return returnResult;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
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
                            ,BigDecimal glb_qtyskbjsum //其他应收款本金汇总余额
                            ,BigDecimal glb_qilxsum //逾期利息汇总余额
                            /**add by wujie on 20201031 end**/
                            ,Map<Long, BigDecimal> fxBalanceMap
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
        }
        else {
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
        }
        else {
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
        glb_qtyskbjsum = ArithmeticUtil.doSub(glb_qtyskbjsum,yskbj);
        //变量.逾期利息汇总余额 = 变量.逾期利息汇总余额 - 金额元素.逾期资产应计利息																			
        glb_qilxsum = ArithmeticUtil.doSub(glb_qilxsum,bjfx);
        /**add by wujie on 20201031 end**/
    }

    /**
     * 清理-其他应收款利息
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
                            ,BigDecimal glb_qtysklxsum
                            ,BigDecimal glb_qilxsum
                            /**add by wujie on 20201031 end**/
                            ,Map<Long, BigDecimal> fxBalanceMap
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
        }
        else {
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
        }
        else {
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
        glb_qtysklxsum = ArithmeticUtil.doSub(glb_qtysklxsum,ysklx);
        //变量.逾期利息汇总余额 = 变量.逾期利息汇总余额 - 金额元素.逾期资产应计利息																			
        glb_qilxsum = ArithmeticUtil.doSub(glb_qilxsum,lxfx);
        /**add by wujie on 20201031 end**/
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetIdNow 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDateNow 发生日
     * @param bookDateNow 入账日
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param branch 分支号
     * @param taskPri 任务优先级
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
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);

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
    private BigDecimal getSubjectBalanceSum(SubjectBalanceSearchDto searchDao,String expBalanceDirect) {
        Map<String, BigDecimal> map = accountingCommonService
            .getSubjectBalanceCollection(searchDao, DictCodeList.余额方向_借方.getValue(),null,searchDao.getTaskPri());
        // 变量.其他应收本金 = 科目余额
        BigDecimal balance = BigDecimal.ZERO;
        if(map !=null && map.size()>0){ 
            for(BigDecimal b:map.values()){
                balance = b;
                break;
            }
        }else{
            balance = BigDecimal.ZERO;
        }
        return balance;
    }
    
    /**
     * 冲减减值计算DTO初始化
     * @param acctEntity
     * @param dealNow
     * @return
     */
    private LossAmtInputDto getLossAmtInputDto(FinProductDealInfoResult acctEntity,FinProductDealInfoComDTO dealNow,
    		String flag,//01 本金  02 利息
    		BigDecimal happenAmt,//发生金额
    		BigDecimal lossBalAmt,//减值准备余额
    		BigDecimal EADAmt, //当前EAD金额
    		BigDecimal receAmt //应收款项余额
    		){
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

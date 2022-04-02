/*
 * 文件名：VatDrawAttachedProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年8月6日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.entity.BokBookset;
import com.joyintech.tams.common.base.entity.TaxBokBalanceSum;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.vatdraw.dao.VatDrawAttachedMapper;
import com.joyintech.tams.valuation.batch.vatdraw.dao.VatDrawMapper;
import com.joyintech.tams.valuation.batch.vatdraw.dto.AddTaxRuleDto;
import com.joyintech.tams.valuation.batch.vatdraw.dto.BooksetTaxConfigDto;
import com.joyintech.tams.valuation.batch.vatdraw.dto.PrdCashDto;
import com.joyintech.tams.valuation.batch.vatdraw.dto.VatBatchResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.CalendarUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 附加税计提批处理Processor逻辑Service
 *
 * @author weipeng
 * @version 1.0 2018年8月6日
 * @see VatDrawAttachedProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class VatDrawAttachedProcessorNewService {

    /**
     * 附加税批处理mapper
     */
    @Autowired
    private VatDrawAttachedMapper vatDrawAttachedMapper;

    /**
     * 增值税批处理mapper
     */
    @Autowired
    private VatDrawMapper vatDrawMapper;

    /**
     * 公用方法services
     */
    @Autowired
    private VatDrawCommonNewService vatDrawCommonNewService;

    /** remark:新产品无昨日增值税余额，当作0来处理，其他无增值税余额的情况抛错 **/
    @Autowired
    private BokBooksetMapper bokBooksetMapper;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 定义 数据小数位数 = 2
     */
    private int decimalDigits = 2;

    /**
     * 定义 明细数据位数处理方式 = 四舍五入
     */
    private RoundingMode roundingMode = RoundingMode.HALF_UP;

    /**
     * 税额结转频率
     */
    public static final String TAX_CARRYOVER_CYCLE = "TAX_CARRYOVER_CYCLE";

    /**
     * process 数据处理
     *
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public VatBatchResult execute(Map<String, Object> param1) {
        // 定义返回值
        VatBatchResult vatBatchResult = new VatBatchResult();
        BookGenerateResult returnResult = new BookGenerateResult();
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        if (vatBatchResult.getListTaxBokBalanceSum() == null) {
            vatBatchResult.setListTaxBokBalanceSum(new ArrayList<TaxBokBalanceSum>());
        }

        // 获取reader返回值
        PrdCashDto acctEntity = (PrdCashDto)param1.get(GlobalConstant.READER_RESULT);
        // 发生日期
        Date happenDate = (Date)param1.get(BookConstant.HAPPEN_DATE);
        // 获取从reader中拿到的值
        String booksetId = acctEntity.getBooksetId();// 账套代码
        Date bookDate = acctEntity.getBookDate();// 入账日期
        // 定义 今日增值税余额汇总（入库）
        List<TaxBokBalanceSum> listTdyBalanceSum = new ArrayList<TaxBokBalanceSum>();

        // 1. 取附加税计税方式信息
        List<AddTaxRuleDto> listAddTaxRuleDto = new ArrayList<>();

        if(MultilevelDataAuthorityUtil.isEnabled()){
            // 获取此账套的创建机构 查找对应的附加税 税率
            String createInst = CommonUtil.getCreateInstByID(booksetId,DictCodeList.共通业务类型_账套管理.getValue());
            // String generalInst = CommonUtil.getYHGeneralBankCode();
            if(StringUtil.isNotEmpty(createInst) ){
                listAddTaxRuleDto = vatDrawAttachedMapper.getAddTaxCalTypeByMultiLevel(booksetId,
                        happenDate, DictCodeListExt1.配置方案类型_税务方案.getValue(),createInst);
            }
        }else{
            listAddTaxRuleDto = vatDrawAttachedMapper.getAddTaxCalType(booksetId,
                    happenDate, DictCodeListExt1.配置方案类型_税务方案.getValue());
        }

        // 定义 附加税税则Map
        Map<String, AddTaxRuleDto> addTaxRuleMap = new HashMap<>();
        for (AddTaxRuleDto dto : listAddTaxRuleDto) {
            addTaxRuleMap.put(dto.getAddTaxType(), dto);
        }

        // 2. 查询账务明细最大的处理序列
        // max_tdy_处理序列 = max_增值税处理序列
        BigDecimal max_bookOrder = vatDrawAttachedMapper.querySumMaxBookOrders(happenDate,
                bookDate, booksetId, null);
        // max_附加税处理序列
        BigDecimal max_addBookOrder = vatDrawAttachedMapper.querySumMaxBookOrders(
                DateUtil.addDay(happenDate, -1), bookDate, booksetId,
                DictCodeList.附加税类型_城建税.getValue());

        // 3.查询发生日增值税余额.
        List<TaxBokBalanceSum> listTdyBalance = vatDrawAttachedMapper.queryTdyTaxBalanceSum(
                happenDate, bookDate, max_bookOrder, booksetId);
        // 分组:增值税类型,确认类型
        Map<String, TaxBokBalanceSum> tdyTaxBalanceSumMap = new HashMap<>();
        for (TaxBokBalanceSum item : listTdyBalance) {
            tdyTaxBalanceSumMap.put(item.getVatType() + item.getTaxPType(), item);
        }

        // 4 查询各账套附加税昨日余额汇总
        // 返回结果中，每种附加税类型对应只有一条数据
        List<TaxBokBalanceSum> listYtsTaxBalanceSum = vatDrawMapper.queryYtsTaxBalanceSum(
                DateUtil.addDay(happenDate, -1), bookDate, max_addBookOrder, booksetId);
        // listYtsTaxBalanceSum 转换为map，主键为 增值税类型+确认标识
        // 定义昨日汇总余额Map
        Map<String, TaxBokBalanceSum> ytsTaxBalanceSumMap = new HashMap<>();
        for (TaxBokBalanceSum item : listYtsTaxBalanceSum) {
            ytsTaxBalanceSumMap.put(item.getVatType() + item.getTaxPType(), item);
        }

        // 5:计算确认计税基数做确认附加税记账
        // 计税基数M = 利息收入当日发生额 + 红利收入当日发生额 + 价差收入当日发生额
        // 定义 利息收入当日发生额
        BigDecimal tdyHappenAmt1 = BigDecimal.ZERO;
        // 定义 红利收入当日发生额
        BigDecimal tdyHappenAmt2 = BigDecimal.ZERO;
        // 定义 价差收入当日发生额
        BigDecimal tdyHappenAmt3 = BigDecimal.ZERO;
        if (MapUtils.isNotEmpty(tdyTaxBalanceSumMap)) {
            tdyHappenAmt1 = tdyTaxBalanceSumMap.get(
                    DictCodeList.增值税类型_利息收入.getValue() + DictCodeList.增值税_确认类型_发生计税.getValue()).getTaxAmt();
            tdyHappenAmt2 = tdyTaxBalanceSumMap.get(
                    DictCodeList.增值税类型_红利收入.getValue() + DictCodeList.增值税_确认类型_发生计税.getValue()).getTaxAmt();
            tdyHappenAmt3 = tdyTaxBalanceSumMap.get(
                    DictCodeList.增值税类型_价差收入.getValue() + DictCodeList.增值税_确认类型_发生计税.getValue()).getTaxAmt();
        }

        // 计税基数M = 利息收入当日发生额 + 红利收入当日发生额 + 价差收入当日发生额
        BigDecimal M = ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, tdyHappenAmt1,
                tdyHappenAmt2, tdyHappenAmt3);

        // 共通元素设置
        // 初始化定义 - 金额场景要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // TableId = DictCodeListTableId.会计场景代码_确认增值税附加.getValue())
//        String tableId = DictCodeListTableId.会计场景代码_确认增值税附加.getValue();
        // KeyEnum.REMARK.getCode() = "确认增值税附加"
//        String remark = "确认增值税附加";
        //
        String tableId = "";
        String remark = "";
        // 20191112 魏鹏：附加税分录变更，通道与产品附加税场景分开，计入不同科目
        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
            tableId = DictCodeListTableId.会计场景代码_增值税确认_通道_确认增值税附加.getValue();
            remark = "增值税确认-通道-确认增值税附加";
        } else {
            tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认增值税附加.getValue();
            remark = "增值税确认-直投-确认增值税附加";
        }
        // 增值税汇总附加税分别入库
        // 城建税 今日附加税确认汇总入库
        /**MODIFY BY YANYA ON 20190917 根据map中动态准则 START
         this.addTdyBalanceSumToList(addTaxRuleMap, ytsTaxBalanceSumMap, acctEntity,
         accountAmountMap, listTdyBalanceSum, M, DictCodeList.附加税类型_城建税.getValue(),
         DictCodeList.增值税_确认类型_发生计税.getValue(), max_bookOrder);

         // 教育税 今日附加税确认汇总入库
         this.addTdyBalanceSumToList(addTaxRuleMap, ytsTaxBalanceSumMap, acctEntity,
         accountAmountMap, listTdyBalanceSum, M, DictCodeList.附加税类型_教育税.getValue(),
         DictCodeList.增值税_确认类型_发生计税.getValue(), max_bookOrder);

         // 地方教育税 今日附加税确认汇总入库
         this.addTdyBalanceSumToList(addTaxRuleMap, ytsTaxBalanceSumMap, acctEntity,
         accountAmountMap, listTdyBalanceSum, M, DictCodeList.附加税类型_地方教育税.getValue(),
         DictCodeList.增值税_确认类型_发生计税.getValue(), max_bookOrder); **/
        //动态循环map中附加税税则
        for(String key: addTaxRuleMap.keySet()){
            this.addTdyBalanceSumToList(addTaxRuleMap, ytsTaxBalanceSumMap, acctEntity,
                    accountAmountMap, listTdyBalanceSum, M, null, key,
                    DictCodeList.增值税_确认类型_发生计税.getValue(), max_bookOrder);
        }
        /**MODIFY BY YANYA ON 20190917 根据map中动态准则  END**/
        // 调入账共通处理
        vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark, acctEntity,
                returnResult, DictCodeList.冲正标识_正常交易.getValue());

        // 6.计算预估计税基数
        // 6.1 单日全提
        // 读取配置[是否预估利息收入增值税附加]
        // 
        List<BooksetTaxConfigDto> listConfig = vatDrawCommonNewService.queryBooksetTaxConfig(
                booksetId, "ISCALCINTALT");
        // 定义 计税基数M1 = 预估利息收入计税余额 + 预估红利收入计税余额 + 预估价差收入计税余额
        BigDecimal M1 = BigDecimal.ZERO;
        // 定义 计税基数M2 = 当日预估价差收入计税余额
        BigDecimal M2 = BigDecimal.ZERO;
        // 是的场合
        if (CollectionUtils.isEmpty(listConfig) || DictCodeList.是否_是.getValue().equals(listConfig.get(0).getConfigValue())) {
            // 定义 预估利息收入计税余额
            BigDecimal tdyTaxAmt1 = tdyTaxBalanceSumMap.get(
                    DictCodeList.增值税类型_利息收入.getValue() + DictCodeList.增值税_确认类型_预估计税.getValue()).getTaxAmt();
            // 定义 预估红利收入计税余额
            BigDecimal tdyTaxAmt2 = tdyTaxBalanceSumMap.get(
                    DictCodeList.增值税类型_红利收入.getValue() + DictCodeList.增值税_确认类型_预估计税.getValue()).getTaxAmt();
            // 计税基数M2 = 预估利息收入计税余额 + 预估红利收入计税余额
            M1 = ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, tdyTaxAmt1, tdyTaxAmt2);
        }
        // 读取配置[是否预估公允价值变动增值税附加]
        listConfig = vatDrawCommonNewService.queryBooksetTaxConfig(booksetId, "ISCALCFLOATALT");
        // 是的场合
        if (CollectionUtils.isEmpty(listConfig) || DictCodeList.是否_是.getValue().equals(listConfig.get(0).getConfigValue())) {
            // 定义 预估价差收入计税余额
            M2 = tdyTaxBalanceSumMap.get(
                    DictCodeList.增值税类型_价差收入.getValue() + DictCodeList.增值税_确认类型_预估计税.getValue()).getTaxAmt();
            // 计税基数M2 = 计税基数M2 + 预估价差收入计税余额
            M1 = ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, M, M2);
        }

        // 共通元素设置
        // 初始化定义 - 金额场景要素 map定义
        accountAmountMap = new HashMap<>();
        // 场景代码 = DictCodeListTableId.会计场景代码_增值税附加预估.getValue())
//        tableId = DictCodeListTableId.会计场景代码_增值税估计_附加税.getValue();
//        // 备注 = "增值税附加预估"
//        remark = "增值税估计-附加税";
        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
            tableId = DictCodeListTableId.会计场景代码_增值税估计_通道_附加税.getValue();
            remark = "增值税估计-通道-附加税";
        } else {
            tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_附加税.getValue();
            remark = "增值税估计-直投-附加税";
        }
        // 增值税汇总附加税分别入库
        // 城建税 今日附加税预估汇总入库
        /**MODIFY BY YANYA ON 20190917 根据map中动态准则  START
         this.addTdyBalanceSumToList(addTaxRuleMap, ytsTaxBalanceSumMap, acctEntity,
         accountAmountMap, listTdyBalanceSum, M, DictCodeList.附加税类型_城建税.getValue(),
         DictCodeList.增值税_确认类型_预估计税.getValue(), max_bookOrder);

         // 教育税 今日附加税预估汇总入库
         this.addTdyBalanceSumToList(addTaxRuleMap, ytsTaxBalanceSumMap, acctEntity,
         accountAmountMap, listTdyBalanceSum, M, DictCodeList.附加税类型_教育税.getValue(),
         DictCodeList.增值税_确认类型_预估计税.getValue(), max_bookOrder);

         // 地方教育税 今日附加税预估汇总入库
         this.addTdyBalanceSumToList(addTaxRuleMap, ytsTaxBalanceSumMap, acctEntity,
         accountAmountMap, listTdyBalanceSum, M, DictCodeList.附加税类型_地方教育税.getValue(),
         DictCodeList.增值税_确认类型_预估计税.getValue(), max_bookOrder);**/
        //动态循环map中附加税税则
        for(String key: addTaxRuleMap.keySet()){
            this.addTdyBalanceSumToList(addTaxRuleMap, ytsTaxBalanceSumMap, acctEntity,
                accountAmountMap, listTdyBalanceSum, M1,M2, key,
                DictCodeList.增值税_确认类型_预估计税.getValue(), max_bookOrder);
        }
        /**MODIFY BY YANYA ON 20190917 根据map中动态准则  END**/
        // 调入账共通处理
        vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark, acctEntity,
                returnResult, DictCodeList.冲正标识_正常交易.getValue());

        // 6.2冲销昨日全提
        // 定义昨日汇总集合 = 3.查询结果(增值税类型 in (城建税,教育税,地方教育税),确认类型=预估计税)
        Map<String, TaxBokBalanceSum> taxBalanceSumMap = new HashMap<>();
        listYtsTaxBalanceSum.stream().filter(item -> {
            /**MODIFY BY YANYA ON 20190917 增加附加税类型_水利基金税  **/
            // 增值税类型 in (城建税,教育税,地方教育税,水利基金税),确认类型=预估计税
            String vatType = item.getVatType();
            if ((vatType.equals(DictCodeList.附加税类型_城建税.getValue())
                    || vatType.equals(DictCodeList.附加税类型_教育税.getValue())
                    || vatType.equals(DictCodeList.附加税类型_地方教育税.getValue())
                    || vatType.equals(DictCodeList.附加税类型_水利基金税.getValue()))
                    && DictCodeList.增值税_确认类型_预估计税.getValue().equals(item.getTaxPType())) {
                return true;
            }
            return false;
        }).forEach(item -> {
            taxBalanceSumMap.put(item.getVatType(), item);
        });
        // if (3.查询结果(增值税类型 in (城建税,教育税,地方教育税,水利基金税),确认类型=预估计税)不为空)
        if (MapUtils.isNotEmpty(taxBalanceSumMap)) {
            // 共通元素设置
            // 初始化定义 - 金额场景要素 map定义
            accountAmountMap = new HashMap<>();
            // 场景代码 = DictCodeListTableId.会计场景代码_增值税附加预估.getValue())
//            tableId = DictCodeListTableId.会计场景代码_增值税估计_附加税.getValue();
//            // 备注 = "增值税附加预估"
//            remark = "增值税估计-附加税";
            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                tableId = DictCodeListTableId.会计场景代码_增值税估计_通道_附加税.getValue();
                remark = "增值税估计-通道-附加税";
            } else {
                tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_附加税.getValue();
                remark = "增值税估计-直投-附加税";
            }
            // 昨日预估价差收入计税余额
            /*admm by tanxuan on 20211129 新产品无昨日增值税余额，默认为0 start*/
            BigDecimal ytsTaxBalanceSy = BigDecimal.ZERO;
            if(ytsTaxBalanceSumMap != null && ytsTaxBalanceSumMap.get(
                DictCodeList.增值税类型_价差收入.getValue() + DictCodeList.增值税_确认类型_预估计税.getValue()) != null){
                ytsTaxBalanceSy = ytsTaxBalanceSumMap.get(
                    DictCodeList.增值税类型_价差收入.getValue() + DictCodeList.增值税_确认类型_预估计税.getValue()).getTaxAmt();
            }else{
                BokBookset bokBookset = bokBooksetMapper.selectByPrimaryKey(acctEntity.getBooksetId());
                if(bokBookset != null && DateUtil.compareTo(acctEntity.getHappenDate(),bokBookset.getStartDate()) == 0){
                    logger.info(acctEntity.getBooksetId() + "：新产品无昨日增值税余额，默认为0");
                }else{
                    throw new RuntimeException(acctEntity.getBooksetId()+"：该产品未获取到昨日增值税余额！");
                }
            }
            /*admm by tanxuan on 20211129 新产品无昨日增值税余额，默认为0 end*/

            for(String key : taxBalanceSumMap.keySet()) {
                this.addBalanceSumToMap(taxBalanceSumMap, accountAmountMap, key, ytsTaxBalanceSy, addTaxRuleMap);
            }

            /**MODIFY BY YANYA ON 20190918 增值税估计-附加税增加统一方法  END **/
            // 调入账共通处理
            vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark, acctEntity,
                    returnResult, DictCodeList.冲正标识_冲销交易.getValue());
        }

        // 账务明细（当前表）+科目明细入返回结果
        returnResult.setProdId(
                acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());
        vatBatchResult.setBookGenerateResult(returnResult);
        // 增值税附加税余额汇总入结果
        vatBatchResult.setListTaxBokBalanceSum(listTdyBalanceSum);
        return vatBatchResult;
    }

    /**
     * 根据增值税类型和确认标识 生成 今日附加税余额汇总 并存入入库集合中
     *
     * @param addTaxRuleMap 附加税税则Map
     * @param ytsTaxBalanceSumMap 昨日余额汇总map
     * @param acctEntity reader返回结果
     * @param accountAmountMap 金额场景元素map
     * @param listTdyBalanceSum 入库今日附加税余额汇总List
     * @param taxBase 计税基础
     * @param vatType 增值税类型
     * @param taxPType 确认标识
     * @param bookOrder 处理序列
     * @see
     * @since 1.0
     */
    private void addTdyBalanceSumToList(Map<String, AddTaxRuleDto> addTaxRuleMap,
                                        Map<String, TaxBokBalanceSum> ytsTaxBalanceSumMap,
                                        PrdCashDto acctEntity,
                                        Map<String, BigDecimal> accountAmountMap,
                                        List<TaxBokBalanceSum> listTdyBalanceSum,
                                        BigDecimal taxBase,BigDecimal tdyYgjcTaxBalance,
                                        String vatType, String taxPType,
                                        BigDecimal bookOrder) {
        // 定义 当日计税余额
        BigDecimal taxAmt = BigDecimal.ZERO;
        // 定义 预估价差收入计税余额-附加税  计入损益类科目
        BigDecimal taxAmtSy = BigDecimal.ZERO;
        // 定义 拆出预估价差收入计税余额附加税后的附加税
        BigDecimal taxAmtPi = BigDecimal.ZERO;
        // 定义 昨日附加税余额汇总 = 昨日附加税余额汇总Map.get(增值税类型+确认标识)
        TaxBokBalanceSum ystTaxBalanceSum = ytsTaxBalanceSumMap.get(vatType + taxPType);
        // 创建 今日附加税余额汇总
        TaxBokBalanceSum tdyTaxBalanceSum = vatDrawCommonNewService.createTdyBalanceSumByYst(
            ystTaxBalanceSum, bookOrder, acctEntity);
        tdyTaxBalanceSum.setVatType(vatType);
        tdyTaxBalanceSum.setTaxPType(taxPType);

        // if (附加税税则map.get(参数.增值税类型) != null)
        if (addTaxRuleMap.get(vatType) != null) {
            // 金额元素.城建税税额 = 附加税计税共通(计税基数M,附加税税则map.get(参数.增值税类型).get税率());
            BigDecimal taxRate = addTaxRuleMap.get(vatType).getTaxRate();
            taxAmt = vatDrawCommonNewService.addTaxCalculate(taxBase, taxRate, decimalDigits,
                roundingMode);

            /** add by weipeng 预估价差附加税计算逻辑  start**/
            if (DictCodeList.增值税_确认类型_预估计税.getValue().equals(taxPType)) {
                // 当日预估预估价差收入计税余额计算的附加税
                taxAmtSy = vatDrawCommonNewService.addTaxCalculate(tdyYgjcTaxBalance, taxRate, decimalDigits, roundingMode);
                /** add by weipeng 预估价差附加税计算逻辑 end**/
            }
        }
        // 即使为空也要记录入库
        else {
            taxAmt = BigDecimal.ZERO;
        }

        if (DictCodeList.增值税_确认类型_预估计税.getValue().equals(taxPType)) {
            // 计税收入余额 = 计税基数M
            tdyTaxBalanceSum.setIncomeAmtAll(taxBase);
            // 计税发生额= 今日计税余额 - 昨日.计税余额 
            BigDecimal happenAmt = ArithmeticUtil.subtract(taxAmt,
                    tdyTaxBalanceSum.getTaxAmt(), NumberEnum.MONEY.getCode(), null);
            tdyTaxBalanceSum.setHappenAmt(happenAmt);

            tdyTaxBalanceSum.setTaxAmt(taxAmt);

            /*** 将预估价差收入计税余额产生的附加税从原预估增值税附加税中拆出，计入610141增值税预估公允价值变动损益科目  ***/
            taxAmtPi = taxAmt.subtract(taxAmtSy);

            // 金额场景元素塞值
            if (vatType.equals(DictCodeList.附加税类型_城建税.getValue())) {
                // key
                accountAmountMap.put(BookConstant.T_URBAN, taxAmtPi);
                accountAmountMap.put(BookConstant.PI_URBAN, taxAmt);
                accountAmountMap.put(BookConstant.F_URBAN, taxAmtSy);

                accountAmountMap.put(BookConstant.T_URBAN+BookConstant.BCCYAMT, taxAmtPi);
                accountAmountMap.put(BookConstant.PI_URBAN+BookConstant.BCCYAMT, taxAmt);
                accountAmountMap.put(BookConstant.F_URBAN+BookConstant.BCCYAMT, taxAmtSy);
            }
            if (vatType.equals(DictCodeList.附加税类型_教育税.getValue())) {
                // key
                accountAmountMap.put(BookConstant.T_EDU, taxAmtPi);
                accountAmountMap.put(BookConstant.PI_EDU, taxAmt);
                accountAmountMap.put(BookConstant.F_EDU, taxAmtSy);

                accountAmountMap.put(BookConstant.T_EDU+BookConstant.BCCYAMT, taxAmtPi);
                accountAmountMap.put(BookConstant.PI_EDU+BookConstant.BCCYAMT, taxAmt);
                accountAmountMap.put(BookConstant.F_EDU+BookConstant.BCCYAMT, taxAmtSy);

            }
            if (vatType.equals(DictCodeList.附加税类型_地方教育税.getValue())) {
                // key
                accountAmountMap.put(BookConstant.T_LEDU, taxAmtPi);
                accountAmountMap.put(BookConstant.PI_LEDU, taxAmt);
                accountAmountMap.put(BookConstant.F_LEDU, taxAmtSy);

                accountAmountMap.put(BookConstant.T_LEDU+BookConstant.BCCYAMT, taxAmtPi);
                accountAmountMap.put(BookConstant.PI_LEDU+BookConstant.BCCYAMT, taxAmt);
                accountAmountMap.put(BookConstant.F_LEDU+BookConstant.BCCYAMT, taxAmtSy);
            }
            /**ADD BY YANYA ON 20190917 附加税类型_水利基金税 **/
            if (vatType.equals(DictCodeList.附加税类型_水利基金税.getValue())) {
                accountAmountMap.put(BookConstant.T_WATER, taxAmtPi);
                accountAmountMap.put(BookConstant.PI_WATER, taxAmt);
                accountAmountMap.put(BookConstant.F_WATER, taxAmtSy);

                accountAmountMap.put(BookConstant.T_WATER+BookConstant.BCCYAMT, taxAmtPi);
                accountAmountMap.put(BookConstant.PI_WATER+BookConstant.BCCYAMT, taxAmt);
                accountAmountMap.put(BookConstant.F_WATER+BookConstant.BCCYAMT, taxAmtSy);
            }
        }
        else {
            // 计税收入余额 = 计税基数M
            tdyTaxBalanceSum.setIncomeAmtAll(taxBase);

            BigDecimal happenAmt = BigDecimal.ZERO;
            List<BooksetTaxConfigDto> listConfig = vatDrawCommonNewService.queryBooksetTaxConfig(
                    acctEntity.getBooksetId(), TAX_CARRYOVER_CYCLE);
            String circle = DictCodeList.频率_季.getValue();
            if (listConfig != null && listConfig.size() > 0) {
                if (listConfig.get(0) != null && listConfig.get(0).getConfigValue() != null) {
                    circle = listConfig.get(0).getConfigValue();
                }
            }
            // 计税发生额 = 今日计税余额 - 昨日.计税余额
            if (CalendarUtil.isFirstDayByCircle(acctEntity.getHappenDate(), circle)) {
                happenAmt = taxAmt;
            }
            else {
                happenAmt = ArithmeticUtil.subtract(taxAmt,
                        tdyTaxBalanceSum.getTaxAmt(), NumberEnum.MONEY.getCode(), null);
            }
            tdyTaxBalanceSum.setHappenAmt(happenAmt);
            tdyTaxBalanceSum.setTaxAmt(taxAmt);

            // 金额场景元素塞值
            if (vatType.equals(DictCodeList.附加税类型_城建税.getValue())) {
                // key
                accountAmountMap.put(BookConstant.T_URBAN, happenAmt);
                accountAmountMap.put(BookConstant.VAT_URBAN, happenAmt);

                accountAmountMap.put(BookConstant.T_URBAN+BookConstant.BCCYAMT, happenAmt);
                accountAmountMap.put(BookConstant.VAT_URBAN+BookConstant.BCCYAMT, happenAmt);
            }
            if (vatType.equals(DictCodeList.附加税类型_教育税.getValue())) {
                // key
                accountAmountMap.put(BookConstant.T_EDU, happenAmt);
                accountAmountMap.put(BookConstant.VAT_EDU, happenAmt);

                accountAmountMap.put(BookConstant.T_EDU+BookConstant.BCCYAMT, happenAmt);
                accountAmountMap.put(BookConstant.VAT_EDU+BookConstant.BCCYAMT, happenAmt);
            }
            if (vatType.equals(DictCodeList.附加税类型_地方教育税.getValue())) {
                // key
                accountAmountMap.put(BookConstant.T_LEDU, happenAmt);
                accountAmountMap.put(BookConstant.VAT_LEDU, happenAmt);

                accountAmountMap.put(BookConstant.T_LEDU+BookConstant.BCCYAMT, happenAmt);
                accountAmountMap.put(BookConstant.VAT_LEDU+BookConstant.BCCYAMT, happenAmt);
            }
            /**ADD BY YANYA ON 20190917 附加税类型_水利基金税 **/
            if (vatType.equals(DictCodeList.附加税类型_水利基金税.getValue())) {
                accountAmountMap.put(BookConstant.T_WATER, happenAmt);
                accountAmountMap.put(BookConstant.VAT_WATER, happenAmt);

                accountAmountMap.put(BookConstant.T_WATER+BookConstant.BCCYAMT, happenAmt);
                accountAmountMap.put(BookConstant.VAT_WATER+BookConstant.BCCYAMT, happenAmt);
            }
        }

        // 入库
        listTdyBalanceSum.add(tdyTaxBalanceSum);

    }

    /**
     *
     * 增值税估计-附加税 并存入入库集合中 ADD BY YANYA 20190918
     *
     * @param taxBalanceSumMap 余额汇总map
     * @param accountAmountMap 金额场景元素map
     * @param vatType 增值税类型
     * @see
     * @since 1.0
     */
    private void addBalanceSumToMap(Map<String, TaxBokBalanceSum> taxBalanceSumMap,
                                    Map<String, BigDecimal> accountAmountMap,
                                    String vatType, BigDecimal ytsTaxBalanceSy,
                                    Map<String, AddTaxRuleDto> addTaxRuleMap) {
        // 定义 当日计税余额
        BigDecimal taxAmt = BigDecimal.ZERO;
        // 若附加税余额不为0，赋值
        if(taxBalanceSumMap.get(vatType) != null){
            taxAmt = taxBalanceSumMap.get(vatType).getTaxAmt();
        }
        // 定义 预估价差收入计税余额-附加税  计入损益类科目
        BigDecimal taxAmtSy = BigDecimal.ZERO;
        // 定义 拆出预估价差收入计税余额附加税后的附加税
        BigDecimal taxAmtPi = BigDecimal.ZERO;
        // if (附加税税则map.get(参数.增值税类型) != null)
        if (addTaxRuleMap.get(vatType) != null && ytsTaxBalanceSy != null) {
            // 金额元素.城建税税额 = 附加税计税共通(计税基数M,附加税税则map.get(参数.增值税类型).get税率());
            BigDecimal taxRate = addTaxRuleMap.get(vatType).getTaxRate();
            // 当日预估预估价差收入计税余额计算的附加税
            taxAmtSy = vatDrawCommonNewService.addTaxCalculate(ytsTaxBalanceSy, taxRate, decimalDigits, roundingMode);
            /** add by weipeng 预估价差附加税计算逻辑 end**/
        }
        /*** 将预估价差收入计税余额产生的附加税从原预估增值税附加税中拆出，计入610141增值税预估公允价值变动损益科目  ***/
        taxAmtPi = taxAmt.subtract(taxAmtSy);
        // 金额场景元素塞值
        if (vatType.equals(DictCodeList.附加税类型_城建税.getValue())) {
            // key
            accountAmountMap.put(BookConstant.T_URBAN, taxAmtPi.negate());
            accountAmountMap.put(BookConstant.PI_URBAN, taxAmt.negate());
            accountAmountMap.put(BookConstant.F_URBAN, taxAmtSy.negate());

            accountAmountMap.put(BookConstant.T_URBAN+BookConstant.BCCYAMT, taxAmtPi.negate());
            accountAmountMap.put(BookConstant.PI_URBAN+BookConstant.BCCYAMT, taxAmt.negate());
            accountAmountMap.put(BookConstant.F_URBAN+BookConstant.BCCYAMT, taxAmtSy.negate());
        }
        if (vatType.equals(DictCodeList.附加税类型_教育税.getValue())) {
            // key
            accountAmountMap.put(BookConstant.T_EDU, taxAmtPi.negate());
            accountAmountMap.put(BookConstant.PI_EDU, taxAmt.negate());
            accountAmountMap.put(BookConstant.F_EDU, taxAmtSy.negate());

            accountAmountMap.put(BookConstant.T_EDU+BookConstant.BCCYAMT, taxAmtPi.negate());
            accountAmountMap.put(BookConstant.PI_EDU+BookConstant.BCCYAMT, taxAmt.negate());
            accountAmountMap.put(BookConstant.F_EDU+BookConstant.BCCYAMT, taxAmtSy.negate());

        }
        if (vatType.equals(DictCodeList.附加税类型_地方教育税.getValue())) {
            // key
            accountAmountMap.put(BookConstant.T_LEDU, taxAmtPi.negate());
            accountAmountMap.put(BookConstant.PI_LEDU, taxAmt.negate());
            accountAmountMap.put(BookConstant.F_LEDU, taxAmtSy.negate());

            accountAmountMap.put(BookConstant.T_LEDU+BookConstant.BCCYAMT, taxAmtPi.negate());
            accountAmountMap.put(BookConstant.PI_LEDU+BookConstant.BCCYAMT, taxAmt.negate());
            accountAmountMap.put(BookConstant.F_LEDU+BookConstant.BCCYAMT, taxAmtSy.negate());
        }
        /**ADD BY YANYA ON 20190917 附加税类型_水利基金税 **/
        if (vatType.equals(DictCodeList.附加税类型_水利基金税.getValue())) {
            accountAmountMap.put(BookConstant.T_WATER, taxAmtPi.negate());
            accountAmountMap.put(BookConstant.PI_WATER, taxAmt.negate());
            accountAmountMap.put(BookConstant.F_WATER, taxAmtSy.negate());

            accountAmountMap.put(BookConstant.T_WATER+BookConstant.BCCYAMT, taxAmtPi.negate());
            accountAmountMap.put(BookConstant.PI_WATER+BookConstant.BCCYAMT, taxAmt.negate());
            accountAmountMap.put(BookConstant.F_WATER+BookConstant.BCCYAMT, taxAmtSy.negate());
        }
    }
}

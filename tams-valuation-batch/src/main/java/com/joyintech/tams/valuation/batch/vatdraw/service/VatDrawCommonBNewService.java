/*
 * 文件名：VatDrawCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年8月8日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TaxBokBalanceSum;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.cache.service.ChangingCacheService;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.BokSchemeConfigDto;
import com.joyintech.tams.valuation.batch.vatdraw.dao.VatDrawCommonMapper;
import com.joyintech.tams.valuation.batch.vatdraw.dto.BooksetTaxConfigDto;
import com.joyintech.tams.valuation.batch.vatdraw.dto.PrdCashDto;
import com.joyintech.tams.valuation.batch.vatdraw.dto.TaxRuleDto;
import com.joyintech.tams.valuation.batch.vatdraw.dto.ValTaxCalConfigDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.service.FinProductCommonBatchService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 增值税计提共通Service
 * 
 * @author wangzhiang
 * @version 1.0 2018年8月8日
 * @see VatDrawCommonBNewService
 * @since 1.0
 */
@Service
public class VatDrawCommonBNewService {
    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    @Autowired
    private BookCommonLogic bookCommonLogic;

    @Autowired
    private DBUtil dBUtil;

    @Autowired
    private FinProductCommonBatchService finProductCommonBatchService;
    /**
     * round(amt/(1+rate),2)*rate
     */
    public static final String TAX_CALC_FORMULA_2 = "FORMULA_2";
    
    /**
     * amt/(1+rate)*rate
     */
    public static final String TAX_CALC_FORMULA_1 = "FORMULA_1";
    /**
     * 增值税计提共通mapper
     */
    @Autowired
    private VatDrawCommonMapper vatDrawCommonMapper;

    @Autowired
    private ChangingCacheService changingCacheService;

    /**
     * 生成 记账元素共通
     * 
     * @param accountAmountMap 金额场景元素
     * @param tableId 场景代码
     * @param remark 备注
     * @param acctEntity reader返回实体
     * @return
     * @see
     * @since 1.0
     */
    public void resultCommonHandle(Map<String, BigDecimal> accountAmountMap, String tableId,
                                   String remark, PrdCashDto acctEntity,
                                   BookGenerateResult result, String offsetFlag) {
        // 组装记账元素
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 塞入记账场景树节点 = 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        // 场景元素 = tableId
        ad.setTableId(tableId);
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();

        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 共通元素设值 - 设置账务引擎参数
        // 记账场景要素map塞值
        accountSceneMap = bookCommonLogic.taxElementAssign(acctEntity);
        
        // 是否累计余额
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_否.getValue());
        
        accountSceneMap.put(BookConstant.OFFSET_FLAG, offsetFlag);
        // 备注 = remark
        accountSceneMap.put(BookConstant.BOOK_REMARK, remark);

        // 币种场景元素塞值
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, acctEntity.getStandardCcy());
        }

        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
        // 记账凭证生成
        BookGenerateResult resultNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        result.getValBokBookdetailBatch().addAll(resultNow.getValBokBookdetailBatch());
    }

    /**
     * 增值税计算共通
     * 
     * @param allIncome 含税收入
     * @param taxRate 税率
     * @param scale 小数位数
     * @param roundingMode 尾数处理方式
     * @param taxCalcFormula2 
     * @see
     * @since 1.0
     */
    public BigDecimal taxCalculate(BigDecimal allIncome, BigDecimal taxRate, int scale,
                                   RoundingMode roundingMode, String taxCalcFormula) {
        BigDecimal amount = BigDecimal.ZERO;
        if (allIncome != null && taxRate != null) {
            if (TAX_CALC_FORMULA_1.equals(taxCalcFormula)) {
                // 返回值 = 发生含税销售 * 税率 /(1+税率)
                amount = ArithmeticUtil.divide(ArithmeticUtil.multiply(allIncome, taxRate),
                        ArithmeticUtil.doAdd(taxRate, new BigDecimal(1)), scale, roundingMode);
            }
            else {
                // round(发生含税销售 * 税率,n)/(1+税率)
                //系统参数（增值税计算中间精度位数）！=null add by yanya 20190924
                if(StringUtil.isNotEmpty(CacheUtil.getSysParam("VAT_CALC_DIGIT"))) {
                    //系统参数（增值税计算中间精度位数）
                    int vatCalcDigitNum = Integer.valueOf(CacheUtil.getSysParam("VAT_CALC_DIGIT"));
                    amount = ArithmeticUtil.multiply(ArithmeticUtil.divide(allIncome,
                        ArithmeticUtil.doAdd(taxRate, new BigDecimal(1)), vatCalcDigitNum, roundingMode), taxRate).setScale(scale, roundingMode);
                }
                else {
                    amount = ArithmeticUtil.multiply(ArithmeticUtil.divide(allIncome,
                        ArithmeticUtil.doAdd(taxRate, new BigDecimal(1)), 2, roundingMode), taxRate).setScale(scale, roundingMode);
                }
            }
        }
        return amount;
    }

    /**
     * 含税收入计算共通
     * 
     * @param taxAmt 税额
     * @param taxRate 税率
     * @param scale 小数位数
     * @param roundingMode 尾数处理方式
     * @see
     * @since 1.0
     */
    public BigDecimal allIncomeCalculate(BigDecimal taxAmt, BigDecimal taxRate, int scale,
                                         RoundingMode roundingMode) {
        BigDecimal amount = BigDecimal.ZERO;
        if (taxAmt != null && taxRate != null) {
            // 返回值 = 发生含税销售 * (1+税率) /税率
            amount = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(taxAmt, ArithmeticUtil.doAdd(taxRate, new BigDecimal(1))),
                taxRate, scale, roundingMode);
        }
        return amount;
    }

    /**
     * 附加税计算共通
     * 
     * @param allIncome 含税收入
     * @param taxRate 税率
     * @param scale 小数位数
     * @param roundingMode 尾数处理方式
     * @see
     * @since 1.0
     */
    public BigDecimal addTaxCalculate(BigDecimal allIncome, BigDecimal taxRate, int scale,
                                      RoundingMode roundingMode) {
        BigDecimal amount = BigDecimal.ZERO;
        if (allIncome != null && taxRate != null) {
            // 返回值 = 发生含税销售 * 税率
            amount = ArithmeticUtil.multiply(scale, roundingMode, allIncome, taxRate);
        }
        return amount;
    }

    /**
     * 金额计入方式
     * 
     * @param isCarrt 计入方式
     * @return 金额计入方式List
     * @see
     * @since 1.0
     */
    public List<Boolean> haddleIsCarrt(String isCarrt) {
        List<Boolean> listIsCarrt = new ArrayList<Boolean>();
        if (StringUtil.isNotEmpty(isCarrt) && isCarrt.length() > 2) {
            // 0:否,1:是
            // 第一位:是否计入余额
            listIsCarrt.add("1".equals(isCarrt.substring(0, 1)));
            // 第二位:是否计入发生额
            listIsCarrt.add("1".equals(isCarrt.substring(1, 2)));
            // 第三位:是否计入结转金额
            listIsCarrt.add("1".equals(isCarrt.substring(2)));
        }
        // 否则全设为false；
        else {
            listIsCarrt.add(false);
            listIsCarrt.add(false);
            listIsCarrt.add(false);
        }
        return listIsCarrt;
    }

    /**
     * 收息处理方式
     * 
     * @param sxHandleStyle 收息处理方式
     * @return 收息处理方式List
     * @see
     * @since 1.0
     */
    public List<Boolean> isIntRepayHandle(String intRepayHandle) {
        List<Boolean> listIsSxHandleStyle = new ArrayList<Boolean>();
        if (StringUtil.isNotEmpty(intRepayHandle) && intRepayHandle.length() > 2) {
            // 0:否,1:是
            // 第一位:是否处理收息
            listIsSxHandleStyle.add("1".equals(intRepayHandle.substring(0, 1)));
            // 第二位:是否处理还本带息
            listIsSxHandleStyle.add("1".equals(intRepayHandle.substring(1, 2)));
            // 第三位:是否处理还本不带息
            listIsSxHandleStyle.add("1".equals(intRepayHandle.substring(2)));
        }
        // 否则全设为false；
        else {
            listIsSxHandleStyle.add(false);
            listIsSxHandleStyle.add(false);
            listIsSxHandleStyle.add(false);
        }
        return listIsSxHandleStyle;
    }

    /**
     * 税则共通处理
     * 
     * @param listBokDetailId 账务明细代码List
     * @param effDate 生效日
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, List<TaxRuleDto>> handleIncTariffScheduleCommon(List<String> listBokDetailId,
                                                                       Date effDate, String booksetId) {
        // 取计税方式信息,税则共通处理
        List<TaxRuleDto> listTaxRuleDto = vatDrawCommonMapper.queryTaxRules(listBokDetailId,
            effDate, booksetId, DictCodeListExt1.配置方案类型_税务方案.getValue());
        // 资产税则表Map定义 map<账务明细代码, list<税则>>
        Map<String, List<TaxRuleDto>> resultMap = new HashMap<>();
        // 封装进map
        // 先按照 账务明细代码+收入类型 分组，每组可能多条数据，再按照 优先级+生效日（转格式）排序，取最新一条，然后放入map中
        listTaxRuleDto.stream().collect(
            Collectors.groupingBy(TaxRuleDto::getGroupStr)).values().stream().map(item -> {
                item.sort((a, b) -> b.getSortStr().compareTo(a.getSortStr()));
                return item.get(0);
            }).forEach(item -> {
                putInMap(item, resultMap);
            });
        return resultMap;
    }

    public void putInMap(TaxRuleDto dto, Map<String, List<TaxRuleDto>> resultMap) {
        String key = dto.getBokDetailId();
        List<TaxRuleDto> listDto = resultMap.get(key);
        if (CollectionUtils.isEmpty(listDto)) {
            listDto = new ArrayList<>();
        }
        listDto.add(dto);
        resultMap.put(key, listDto);
    }

    /**
     * 根据 昨日附加税余额汇总 生成 今日附加税余额汇总
     * 
     * @param balanceSum 昨日附加税余额汇总
     * @param bookOrder 处理序列
     * @param accEntity reader返回结果
     * @return 生成今日附加税余额汇总
     * @see
     * @since 1.0
     */
    public TaxBokBalanceSum createTdyBalanceSumByYst(TaxBokBalanceSum balanceSum,
                                                     BigDecimal bookOrder, PrdCashDto accEntity) {
        TaxBokBalanceSum entity = new TaxBokBalanceSum();
        // 若昨日为空，则新建
        if (balanceSum == null) {
            entity.setHappenAmt(BigDecimal.ZERO);
            entity.setIncomeAmtAll(BigDecimal.ZERO);
            entity.setTaxAmt(BigDecimal.ZERO);
            entity.setTaxAmtSum(BigDecimal.ZERO);
        }
        else {
            entity.setHappenAmt(balanceSum.getHappenAmt());
            entity.setIncomeAmtAll(balanceSum.getIncomeAmtAll());
            entity.setTaxAmt(balanceSum.getTaxAmt());
            entity.setTaxAmtSum(balanceSum.getTaxAmtSum());
        }
        // 共同属性设置
        // seqNo 重新赋值
        entity.setSeqNo(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
        // 账套代码 重新赋值
        entity.setBooksetId(accEntity.getBooksetId());
        // 币种 重新赋值
        entity.setoCcy(accEntity.getStandardCcy());
        // 入账日期 重新赋值
        entity.setBookDate(accEntity.getBookDate());
        // 账套日期 重新赋值
        entity.setBooksetDate(accEntity.getBooksetDate());
        // 发生日 = 当前发生日
        entity.setHappenDate(accEntity.getHappenDate());
        // 处理序列
        entity.setBookOrder(bookOrder);
        return entity;
    }

    /**
     * 查询账套税务配置
     * 
     * @param booksetId 账套代码
     * @param configId 配置代码
     * @return
     * @see
     * @since 1.0
     */
//    public List<BooksetTaxConfigDto> queryBooksetTaxConfig(String booksetId, String configId) {
//        return vatDrawCommonMapper.queryBooksetTaxConfig(booksetId,
//            DictCodeListExt1.配置方案类型_税务方案.getValue(), configId);
//    }
    public List<BokSchemeConfigDto> queryBokSchemeConfig(String booksetId, String configId) {
        return changingCacheService.getValBokSchemeConfig(booksetId, DictCodeListExt1.配置方案类型_税务方案.getValue(), configId);
    }

    public List<ValTaxCalConfigDto> queryTaxCalConfig(String booksetId, String schemeType) {
        return changingCacheService.getValTaxCalConfig(booksetId, schemeType);
    }

    /**
     * 汇率查询
     * @param date 查询日期
     * @param booksetId 账套代码
     * @param quoteCcy 报价币种
     * @param baseCcy 基础币种
     * @return
     */
    public BigDecimal getExRate(Date date, String booksetId, String quoteCcy, String baseCcy,
                                 Map<String,BigDecimal> exRateMem) {
        // 汇率 = 汇率缓存.get（币种+"/"+本位币）
        String key = baseCcy + "/" + quoteCcy;
        if (StringUtil.equals(quoteCcy, baseCcy)) {
            return BigDecimal.ONE;
        }
        if(!exRateMem.containsKey(key)) {
            //调用计算类方法共通【汇率查询】
            List<CurrencyMarketDto> currMarkets = finProductCommonBatchService.getCurrencyMarket(date, booksetId,
                quoteCcy, baseCcy);
            if (CollectionUtils.isNotEmpty(currMarkets)) {
                // 汇率缓存.put(币种+"/"+本位币,汇率)
                BigDecimal closePrice =
                    currMarkets.get(0).getClosePrice() != null ? currMarkets.get(0).getClosePrice() : BigDecimal.ONE;
                exRateMem.put(key, closePrice);
            }
            else {
                // 汇率缓存.put(币种+"/"+本位币,汇率)
                exRateMem.put(key, BigDecimal.ONE);
            }
        }
        return exRateMem.get(key);
    }
}

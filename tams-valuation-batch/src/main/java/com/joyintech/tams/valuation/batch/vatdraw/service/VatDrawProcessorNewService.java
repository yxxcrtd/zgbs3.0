/*
 * 文件名：ValAddedTaxDealProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年7月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.service;

import com.google.common.collect.Maps;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.vatdraw.dao.VatDrawMapper;
import com.joyintech.tams.valuation.batch.vatdraw.dto.*;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.CalendarUtil;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import com.joyintech.tams.valuation.common.utils.ValStringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 增值税计提批处理Processor逻辑Service
 *
 * @author weipeng
 * @version 1.0 2018年7月31日
 * @see VatDrawProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class VatDrawProcessorNewService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(VatDrawProcessorNewService.class);

    /**
     * 增值税批处理mapper
     */
    @Autowired
    private VatDrawMapper vatDrawMapper;

    /**
     * 增值税计提公用Service
     */
    @Autowired
    private VatDrawCommonNewService vatDrawCommonNewService;

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    @Autowired
    private BookAccountGenLogicNew bookAccountGenLogicNew;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * 定义全局变量 金额计算共通小数位数
     */
    private int scale = NumberEnum.MONEY.getCode();

    /**
     * 定义 明细数据小数位数 = 10
     */
    private int decimalDigits = 14;

    /**
     * 定义 明细数据位数处理方式 = 四舍五入
     */
    private RoundingMode roundingMode = RoundingMode.HALF_UP;

    /**
     * 付息处理方式key
     */
    public static final String FX = "FX";

    /**
     * 息票品种key
     */
    public static final String CS = "CS";

    /**
     * 税额结转频率
     */
    public static final String TAX_CARRYOVER_CYCLE = "TAX_CARRYOVER_CYCLE";

    /**
     * 税额计算公式
     */
    public static final String TAX_CALC_FORMULA = "TAX_CALC_FORMULA";

    /**
     * 持有期间收益税计提方式:01:区间倒减,02:当日新增
     */
    public static final String TAX_ACCR_TYPE_0102_0202 = "TAX_ACCR_TYPE_0102_0202";

    /**
     * 金融商品转让计提方式:01:区间倒减,02:当日新增
     */
    public static final String TAX_ACCR_TYPE_0302 = "TAX_ACCR_TYPE_0302";

    /**
     * 估值增值计提方式-资产维度:01:区间倒减,02:当日新增
     */
    public static final String TAX_ACCR_TYPE_0302_01_ASS = "TAX_ACCR_TYPE_0302_01_ASS";

    /**
     * 税额结转频率-资产维度
     */
    public static final String TAX_CARRYOVER_CYCLE_ASS = "TAX_CARRYOVER_CYCLE_ASS";

    /**
     * 利息收入计提方式-资产维度:01:区间倒减,02:当日新增
     */
    public static final String TAX_ACCR_TYPE_0102_ASS = "TAX_ACCR_TYPE_0102_ASS";

    /**
     * 红利收入计提方式-资产维度:01:区间倒减,02:当日新增
     */
    public static final String TAX_ACCR_TYPE_0202_ASS = "TAX_ACCR_TYPE_0202_ASS";

    /**
     * 价差收入计提方式-资产维度:01:区间倒减,02:当日新增
     */
    public static final String TAX_ACCR_TYPE_0302_ASS = "TAX_ACCR_TYPE_0302_ASS";

    /**
     * 增值税汇总小数位处理:01:不处理,02:按资产合并处理（保留两位精度）
     */
    public static final String TAX_SUM_MODE = "TAX_SUM_MODE";

    /**
     * 增值税汇总小数位范围: 增值税类型_确认标识 逗号分割
     */
    public static final String TAX_SUM_SCOPE = "TAX_SUM_SCOPE";

    /**
     * 未实现负差抵扣办法
     */
    public static final String FLOATCALCBYDIFF = "FLOATCALCBYDIFF";

    /**
     * 是否对公允价值变动损益做暂估税
     */
    public static final String SFZGGYJZBDS = "SFZGGYJZBDS";

    /**
     * 其他综合收益是否计税
     */
    public static final String QTZHSYSFJS = "QTZHSYSFJS";

    /**
     * 辅助核算项配置信息Map
     */
    private Map<String,SubjectMappingConfig> mappingConfigMap =new HashMap<>();

    /**
     * 内部编码信息Map<科目号,内部科目编码>
     */
    private Map<String,String> innerCodeMap =new HashMap<>();

    /**
     * process 数据处理
     *
     * @param param1 参数
     * @return BookGenerateResult
     * @throws CloneNotSupportedException
     * @see
     * @since 1.0
     */
    public VatBatchResult execute(Map<String, Object> param1)
        throws CloneNotSupportedException, InvocationTargetException, IllegalAccessException {
        // 定义返回值
        VatBatchResult vatBatchResult = new VatBatchResult();
        BookGenerateResult returnResult = new BookGenerateResult();
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        if (vatBatchResult.getListTaxBokBalanceSum() == null) {
            vatBatchResult.setListTaxBokBalanceSum(new ArrayList<TaxBokBalanceSum>());
        }
        if (vatBatchResult.getListTaxBokBalance() == null) {
            vatBatchResult.setListTaxBokBalance(new ArrayList<TaxBokBalance>());
        }
        if (vatBatchResult.getListValTaxBokDetail() == null) {
            vatBatchResult.setListValTaxBokDetail(new ArrayList<TaxBokDetail>());
        }
        // 获取reader返回值
        PrdCashDto acctEntity = (PrdCashDto)param1.get(GlobalConstant.READER_RESULT);
        // 发生日期
        Date happenDate = acctEntity.getHappenDate();
        // 获取从reader中拿到的值
        String booksetId = acctEntity.getBooksetId();// 账套代码
        Date bookDate = acctEntity.getBookDate();// 入账日期

        // 1. 查询账务明细最大的处理序列
        TaxBookOrderDto maxBookOrderDto = vatDrawMapper.queryMaxBookOrders(
            DateUtil.addDay(happenDate, -1), bookDate, booksetId);
        // 定义 增值税账务明细余额最大处理序列
        BigDecimal max_bookOrder = maxBookOrderDto.getMaxBookOrder();
        // 定义 增值税汇总最大处理序列 = 查询账务明细最大的处理序列.汇总最大处理序列+1
        BigDecimal max_tdy_bookOrder = ArithmeticUtil.doAdd(maxBookOrderDto.getMaxTdyBookOrder(),
            new BigDecimal(1));

        // 2.查询账套下配置
        // 2.1 查询账套下不启用场景
        // 调用共通查询 账套税务配置
        List<BooksetTaxConfigDto> listBooksetTaxConfig = vatDrawCommonNewService.queryBooksetTaxConfig(
            booksetId, null);

        // 查询增值税已抵扣增值额最大处理序列
        BigDecimal maxBookOrder = vatDrawMapper.queryMaxDeductBookOrder();
        // 待抵扣信息MAP
        Map<String, TaxTurnedBalance> taxTurnedBalMap = new HashMap<String, TaxTurnedBalance>();
        // 是否处理待抵扣增值额
        String isCalcTurned = null;
        // 待抵扣增值额处理方式
        String turnedCalcType = null;

        //List<String> cfBokConfigs = new ArrayList<>();
        Map<String, String> cfConfigs = new HashMap<>();
        // 若账套税务配置不为空，则查询对应不启用场景
        if (CollectionUtils.isNotEmpty(listBooksetTaxConfig)) {
            listBooksetTaxConfig.forEach(item -> {
                cfConfigs.put(item.getConfigId(), item.getConfigValue());
            });

            isCalcTurned = cfConfigs.get("IS_CALC_TURNED");
            turnedCalcType = cfConfigs.get(DictCode.待抵扣增值额处理方式.getValue());
            if (DictCodeList.是否_是.getValue().equals(isCalcTurned)) {
                // 统计账务明细代码待抵扣增值额
                List<TaxTurnedBalance> taxTurnedBalList = vatDrawMapper.queryTaxTurnedBalList(
                    booksetId, happenDate, bookDate);
                if (CollectionUtils.isNotEmpty(taxTurnedBalList)) {
                    taxTurnedBalList.forEach(item -> {
                        taxTurnedBalMap.put(item.getBokDetailId(), item);
                    });
                }
            }
        }
        // 没有取到频率的情况,取为季度
        if (cfConfigs.get(TAX_CARRYOVER_CYCLE) == null) {
            cfConfigs.put(TAX_CARRYOVER_CYCLE, DictCodeList.频率_季.getValue());
        }

        // 没有取到频率的情况,取为季度
        String calcFormula = cfConfigs.get(TAX_CALC_FORMULA);
        if (calcFormula == null) {
            cfConfigs.put(TAX_CALC_FORMULA, VatDrawCommonService.TAX_CALC_FORMULA_2);
            calcFormula = VatDrawCommonService.TAX_CALC_FORMULA_2;
        }

        // 2.2 查询税额计提分类配置
        Map<String, Object> param = Maps.newHashMap();
        param.put("booksetId", acctEntity.getBooksetId());
        param.put("schemeType", DictCodeListExt1.配置方案类型_税务方案.getValue());
        List<ValTaxCalConfigDto> listValTaxCalConfigDto = vatDrawMapper.queryTaxCalConfig(
            param);
        // 定义 税额计提分类配置MAP
        Map<String, List<ValTaxCalConfigDto>> valTaxCalConfigMaps = new HashMap<>();
        for (ValTaxCalConfigDto item : listValTaxCalConfigDto) {
            // 定义key = 税额计提分类配置.场景代码 + 税额计提分类配置.科目性质
            String key = item.getTableId() + item.getSubjectNature();
            // 定义List<ValTaxCalConfigDto>
            List<ValTaxCalConfigDto> listDto = valTaxCalConfigMaps.get(key);
            // 判断是否为空,空则初始化list
            if (CollectionUtils.isEmpty(listDto)) {
                listDto = new ArrayList<ValTaxCalConfigDto>();
            }
            listDto.add(item);
            // 放入map中
            valTaxCalConfigMaps.put(key, listDto);
        }

        // 3.取发生日期初增值税明细余额
        List<TaxBokBalance> listTaxBokBalance = vatDrawMapper.queryTaxBokBalance(
            DateUtil.addDay(happenDate, -1), bookDate, max_bookOrder, booksetId);
        // 定义 增值税明细余额Map
        Map<ValTaxBokBalanceKeyDto, TaxBokBalance> taxBokBalanceMap = new HashMap<>();
        // 将list按照key值对应放入map中
        for (TaxBokBalance item : listTaxBokBalance) {
            // 定义key
            ValTaxBokBalanceKeyDto keyDto = new ValTaxBokBalanceKeyDto();
            keyDto.setBokDetailId(item.getBokDetailId());
            keyDto.setBranch(item.getBranch());//add by yanya on 20191226 分支序号
            keyDto.setIncomeSource(item.getIncomeSource());
            keyDto.setTaxPType(item.getTaxPType());
            keyDto.setVatType(item.getVatType());
            item.setSeqNo(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
            item.setBooksetId(acctEntity.getBooksetId());
            item.setBooksetDate(acctEntity.getBooksetDate());
            item.setHappenDate(acctEntity.getHappenDate());
            item.setBookDate(acctEntity.getBookDate());
            item.setBookOrder(max_tdy_bookOrder);
            // 存入 增值税明细余额Map
            taxBokBalanceMap.put(keyDto, item);
        }

        // 4' 处理2017-12-31日当天账务明细余额,生成初始化公允价值变动账务明细税明细和余额.
        // IF (参数.发生日期 = '2018-01-01' && 配置.是否对公允价值变动损益计税 不为空 && 配置.是否对公允价值变动损益计税 = '是')
        // 读取[已实现负差是否抵扣]配置
        List<BooksetTaxConfigDto> listConfig = vatDrawCommonNewService.queryBooksetTaxConfig(
            acctEntity.getBooksetId(), SFZGGYJZBDS);
        if ("2018-01-01".equals(DateUtil.format(happenDate))
            && CollectionUtils.isNotEmpty(listConfig)
            && DictCodeList.是否_是.getValue().equals(listConfig.get(0).getConfigValue())) {
            // 调用共通【基础科目性质查询】
            List<String> baseSubjectNatureList = new ArrayList<>();
            baseSubjectNatureList.addAll(accountingCommonService.getValSubjectNatureList(
                DictCodeListExt1.分组代码_公允价值查询对应科目.getValue()));
            // IF 配置.其他综合收益是否计税 不为空 && 配置.其他综合收益是否计税 = '否'
            List<BooksetTaxConfigDto> listTaxConfigDto = vatDrawCommonNewService.queryBooksetTaxConfig(
                acctEntity.getBooksetId(), QTZHSYSFJS);
            if (CollectionUtils.isNotEmpty(listTaxConfigDto)
                && DictCodeList.是否_是.getValue().equals(listTaxConfigDto.get(0).getConfigValue())) {
                baseSubjectNatureList.remove(DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue());
            }
            // 4'.1 查询发生日当天最新的凭证:
            List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                booksetId, bookDate, null, baseSubjectNatureList, null, null,
                DateUtil.parse("2017-12-31"), null, null, null);
            Collections.sort(listBookLatestInfoDto, new Comparator<BookLatestInfoDto>() {

                @Override
                public int compare(BookLatestInfoDto o1, BookLatestInfoDto o2) {
                    return o1.getBookOrder().compareTo(o2.getBookOrder());
                }
            });
            // 4'.2 根据上述查询结果,账务明细代码排重后取税则
            List<String> listBokDetailId = new ArrayList<String>();
            for (BookLatestInfoDto info : listBookLatestInfoDto) {
                if (info.getBokDetailId() != null
                    && !listBokDetailId.contains(info.getBokDetailId())) {
                    listBokDetailId.add(info.getBokDetailId());
                }
            }
            // 取税则
            Map<String, List<TaxRuleDto>> taxRuleMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(listBokDetailId)) {
                taxRuleMap = vatDrawCommonNewService.handleIncTariffScheduleCommon(listBokDetailId,
                    happenDate, booksetId);
            }
            // 4'.3 循环4'.1查询到的内容循环以下处理. 循环对象为账务明细（当前表）M
            // 定义 增值税账务明细List
            List<TaxBokDetail> listValTaxBokDetail = new ArrayList<TaxBokDetail>();
            Map<ValTaxBokBalanceKeyDto, TaxBokBalance> taxBokBalanceMem = new HashMap<>();
            for (BookLatestInfoDto item : listBookLatestInfoDto) {

                // 定义局部变量
                // 取得税额计提分类配置 = 税额计提分类配置MAP.get(账务明细（当前表）.记账场景 + 账务明细（当前表）.基础科目性质)
                List<ValTaxCalConfigDto> listTaxCalConfigDto = valTaxCalConfigMaps.get(
                    item.getTableId() + item.getBaseSubjectNature());
                // 4.1.1循环税额计提分类配置,生成明细,list为空的场合,处理下一条数据.
                if (CollectionUtils.isEmpty(listTaxCalConfigDto)) {
                    continue;
                }
                List<TaxBokDetail> ValTaxBokDetails = new ArrayList<TaxBokDetail>();
                for (ValTaxCalConfigDto configDto : listTaxCalConfigDto) {
                    // 4'.3.1 新建增值税账务明细
                    TaxBokDetail taxBokDetail = new TaxBokDetail();
                    // 流水 <- 自动生成，规则为BOOK + "YYYYMMDD"+20位序列号
                    taxBokDetail.setSeqNo(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    // 账套代码 = 参数.账套代码
                    taxBokDetail.setBooksetId(booksetId);
                    // 账务明细流水 = 账务明细（当前表）M.流水
                    taxBokDetail.setBokSeqno(item.getSeqno());
                    // 账务明细代码 = 账务明细（当前表）M.账务明细代码
                    taxBokDetail.setBokDetailId(item.getBokDetailId());
                    // 发生日期 = 账务明细（当前表）M.发生日期
                    taxBokDetail.setHappenDate(item.getHappenDate());
                    // 账套日期 = 参数.账套日期
                    taxBokDetail.setBooksetDate(acctEntity.getBooksetDate());
                    // 入账日期 = 参数.入账日期
                    taxBokDetail.setBookDate(item.getBookDate());
                    // 凭证编号 = 账务明细（当前表）M.凭证编号
                    taxBokDetail.setBookId(item.getBookId());
                    // 处理序列 = max_tdy_处理序列
                    taxBokDetail.setBookOrder(max_tdy_bookOrder);
                    // 币种 = 账务明细（当前表）M.原币
                    /**MODIFY BY WUJIE ON 20190725 START
                     taxBokDetail.setoCcy(item.getoCcy());
                     **/
                    taxBokDetail.setoCcy(item.getbCcy());
                    /**MODIFY BY WUJIE ON 20190725 END**/

                    // 增值税类型 = 税额计提分类配置.增值税类型
                    taxBokDetail.setVatType(configDto.getVatType());
                    // 确认标识 = 税额计提分类配置.确认标识
                    taxBokDetail.setTaxPType(configDto.getTaxPType());
                    // 发生场景 = 税额计提分类配置.发生场景
                    taxBokDetail.setIncomeSource(configDto.getIncomeSource());
                    //分支序号 = 账务明细（当前表）M.分支序号 add by yanya on 20191225
                    taxBokDetail.setBranch(item.getBranch());

                    // 税则字段取值逻辑
                    // 税则entity = 税则map.get(账务明细（当前表）.账务明细代码).filter(增值税类型 = '金融产品转让')
                    BigDecimal taxRate = new BigDecimal("0.03");

                    if (taxRuleMap.get(item.getBokDetailId()) == null) {
                        continue;
                    }
                    // 税则entity = 税则map.get(账务明细（当前表）M.账务明细代码).filter(增值税类型 = '金融商品转让')
                    List<TaxRuleDto> listTaxRuleDto = taxRuleMap.get(
                        item.getBokDetailId()).stream().filter(
                        t -> DictCodeList.增值税类型2_金融商品转让收入.getValue().equals(
                            t.getIncomeType())).collect(Collectors.toList());
                    // 税则为空的场合,continue继续下一条处理;
                    if (CollectionUtils.isEmpty(listTaxRuleDto)) {
                        continue;
                    }
                    // 税则 entity = list.get(0)
                    TaxRuleDto entity = listTaxRuleDto.get(0);
                    // 计税方式 = 税则entity.计税方式
                    taxBokDetail.setCalcType(entity.getTaxableSign());
                    // 税率 = 税则entity.税率
                    // 定义 税率 = 税则entity.税率
                    taxRate = entity.getTaxRate();
                    taxBokDetail.setTaxRate(taxRate);
                    // 税则代码 = 税则entity.流水号
                    taxBokDetail.setTaxRegId(entity.getSeqNo());
                    // 免税收入类型 = 税则entity.免税收入类型
                    taxBokDetail.setFeeType(entity.getFeeType());
                    /**MODIFY BY WUJIE ON 20190725 START
                     // 发生含税销售额 = 账务明细（当前表）M.原币余额（发生日）
                     BigDecimal happenAmt = item.getoAmtHappenDate();
                     **/
                    // 发生含税销售额 = 账务明细（当前表）M.本币余额（发生日）
                    BigDecimal happenAmt = item.getbAmtHappenDate();
                    /**MODIFY BY WUJIE ON 20190725 END**/



                    // if (发生科目借贷方向 != 账务明细（当前表）.借贷方向)
                    if (!configDto.getCdFlag().equals(item.getCdFlag())) {
                        // 发生含税销售额 = -1 * 发生含税销售额
                        happenAmt = ArithmeticUtil.multiply(new BigDecimal(-1), happenAmt);
                    }
                    // if (税额计提分类配置.计入进销项 = '计入减项')
                    if (configDto.getInOut().equals(DictCodeListExt1.增项_减项_计入减项.getValue())) {
                        // 发生含税销售额= -1 * 增值税金额
                        happenAmt = ArithmeticUtil.multiply(new BigDecimal(-1), happenAmt);
                    }
                    taxBokDetail.setHappenAmt(happenAmt);
                    // 当日计税发生额 = 增值税计算共通(账务明细（当前表）.本位币发生额 , 税率, 明细数据小数位数,明细数据位数处理方式)
                    taxBokDetail.setTaxAmt(vatDrawCommonNewService.taxCalculate(happenAmt, taxRate,
                        decimalDigits, roundingMode, calcFormula));
                    // 计税余额发生额 = 当日计税发生额
                    taxBokDetail.setBalAmt(taxBokDetail.getTaxAmt());
                    // 增值税账务明细List.add(增值税账务明细)
                    ValTaxBokDetails.add(taxBokDetail);
                }
                // 4'.3.2 更新余额,同4.3.2 更新余额处理
                this.updateTaxBokBalanceMap(ValTaxBokDetails, taxBokBalanceMap, acctEntity,
                    max_tdy_bookOrder, cfConfigs, taxBokBalanceMem);
                listValTaxBokDetail.addAll(ValTaxBokDetails);
            }
        }

        // 4.处理当天账务明细,生成账务明细税明细和余额
        // 4.1 查询发生日当天最新的凭证:
        List<BookLatestInfoDto> listBookLatestInfo = accountingCommonService.getBookLatestInfo(
            booksetId, bookDate, null, null, null, null, happenDate, null, null, null);
        Collections.sort(listBookLatestInfo, new Comparator<BookLatestInfoDto>() {

            @Override
            public int compare(BookLatestInfoDto o1, BookLatestInfoDto o2) {
                return o1.getBookOrder().compareTo(o2.getBookOrder());
            }
        });

        // 4.2 根据上述查询结果,账务明细代码排重后取税则
        List<String> listBokDetailId = new ArrayList<String>();
        for (BookLatestInfoDto info : listBookLatestInfo) {
            if (info.getBokDetailId() != null
                && !listBokDetailId.contains(info.getBokDetailId())) {
                listBokDetailId.add(info.getBokDetailId());
            }
            // 内部科目编码Map赋值
            innerCodeMap.put(info.getSubjectNo(), info.getBaseSubjectNature());
        }

        // 取税则
        Map<String, List<TaxRuleDto>> taxRuleDtoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(listBokDetailId)) {
            taxRuleDtoMap = vatDrawCommonNewService.handleIncTariffScheduleCommon(listBokDetailId,
                happenDate, booksetId);
        }

        // add by weipeng on 20220309：会计引擎升级，利息收入-增值税备抵区分投资目的 start
        // 查询辅助核算项配置信息
        // 定义 变量.辅助核算项Map<账套代码+账务明细代码, 辅助核算项配置>
        mappingConfigMap = bookAccountGenLogicNew.getSubjectMappingConfigList(booksetId, listBokDetailId);
        // add by weipeng on 20220309：会计引擎升级，利息收入-增值税备抵区分投资目的 end

        // 4.3 循环4.1查询到的内容循环以下处理.
        // 定义 增值税账务明细List
        List<TaxBokDetail> listValTaxBokDetail = new ArrayList<TaxBokDetail>();

        // 定义 增值税已抵扣增值额List
        List<TaxDeductedBalance> listDeductedBalance = new ArrayList<TaxDeductedBalance>();
        Map<ValTaxBokBalanceKeyDto, TaxBokBalance> taxBokBalanceMem = new HashMap<>();
        for ( BookLatestInfoDto infoDto : listBookLatestInfo) {
            // 定义局部变量
            // 本位币发生额
            BigDecimal bHappenAmt = infoDto.getbHappenAmt();
            // 本位币余额（发生日）
            BigDecimal bAmtHappenDate = infoDto.getbAmtHappenDate();
            // 取得税额计提分类配置 = 税额计提分类配置MAP.get(账务明细（当前表）.记账场景 + 账务明细（当前表）.基础科目性质)
            List<ValTaxCalConfigDto> listConfigDto = valTaxCalConfigMaps.get(
                infoDto.getTableId() + infoDto.getBaseSubjectNature());
            // 4.3.1循环税额计提分类配置,生成明细,list为空的场合,处理下一条数据.
            if (CollectionUtils.isEmpty(listConfigDto)) {
                continue;
            }
            Collections.sort(listConfigDto, new Comparator<ValTaxCalConfigDto>() {

                @Override
                public int compare(ValTaxCalConfigDto o1, ValTaxCalConfigDto o2) {
                    return o2.getTaxPType().compareTo(o1.getTaxPType());
                }
            });
            List<TaxBokDetail> ValTaxBokDetails = new ArrayList<TaxBokDetail>();

            String couponSpecies = null;
            FinCashPlan finCashPlan = null;
            for (ValTaxCalConfigDto configDto : listConfigDto) {
                // 如果条件不为空,做条件筛选
                if (StringUtil.isNotEmpty(configDto.getIntRepayHandle())) {
                    Map<String, String> prop = ValStringUtil.readProperty(
                        configDto.getIntRepayHandle());
                    // 息票品种条件
                    String cs = prop.get(CS);
                    if (StringUtil.isNotEmpty(cs)) {
                        if (couponSpecies == null) {
                            couponSpecies = vatDrawMapper.queryCouponSpecies(infoDto.getBusiId(),
                                DictCodeListExt1.金融产品类型_债券.getValue());
                        }
                        if (couponSpecies != null && !cs.contains(couponSpecies)) {
                            continue;
                        }
                    }
                    // IF (税额计提分类配置.收息处理方式 不为空)
                    String fx = prop.get(FX);
                    if (StringUtil.isNotEmpty(fx)) {
                        List<Boolean> handle = vatDrawCommonNewService.isIntRepayHandle(fx);
                        if (finCashPlan == null) {
                            finCashPlan = vatDrawMapper.queryCashPlan(infoDto.getTradeId(),
                                DictCodeListExt1.现金流二级类型_本金.getValue(),
                                DictCodeList.交易方向_收息.getValue());
                        }
                        // IF 查询结果为空
                        if (finCashPlan == null) {
                            finCashPlan = new FinCashPlan();
                            finCashPlan.setPrinAmt(BigDecimal.ZERO);
                        }
                        if (finCashPlan.getIsPayInt() == null) {
                            finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
                        }
                        // IF 现金流计划表.计划支付本金 = 0或空 && !税额计提分类配置.收息处理方式.是否处理中间收息
                        if ((finCashPlan.getPrinAmt() == null
                             || BigDecimal.ZERO.compareTo(finCashPlan.getPrinAmt()) == 0)
                            && !handle.get(0)) {
                            continue;
                        }
                        // IF 现金流计划表.计划支付本金 != 0或空 && 现金流计划表.是否支付利息 = '是' && !税额计提分类配置.是否处理还本带息
                        if ((finCashPlan.getPrinAmt() != null
                             && BigDecimal.ZERO.compareTo(finCashPlan.getPrinAmt()) != 0)
                            && finCashPlan.getIsPayInt().equals(DictCodeList.是否_是.getValue())
                            && !handle.get(1)) {
                            continue;
                        }
                        // IF 现金流计划表.计划支付本金 != 0或空 && 现金流计划表.是否支付利息 = '否' && !税额计提分类配置.是否处理还本不带息
                        if ((finCashPlan.getPrinAmt() == null
                             || BigDecimal.ZERO.compareTo(finCashPlan.getPrinAmt()) != 0)
                            && finCashPlan.getIsPayInt().equals(DictCodeList.是否_否.getValue())
                            && !handle.get(2)) {
                            continue;
                        }
                    }
                }
                // 新增增值税账务明细
                TaxBokDetail taxBokDetail = new TaxBokDetail();
                // 流水 <- 自动生成，规则为BOOK + "YYYYMMDD"+20位序列号
                taxBokDetail.setSeqNo(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                // 账套代码 = 参数.账套代码
                taxBokDetail.setBooksetId(booksetId);
                // 账务明细流水 = 账务明细（当前表）.流水
                taxBokDetail.setBokSeqno(infoDto.getSeqno());
                // 账务明细代码 = 账务明细（当前表）.账务明细代码
                taxBokDetail.setBokDetailId(infoDto.getBokDetailId());
                // 发生日期 = 账务明细（当前表）.发生日期
                taxBokDetail.setHappenDate(infoDto.getHappenDate());
                // 账套日期 = 参数.账套日期
                taxBokDetail.setBooksetDate(acctEntity.getBooksetDate());
                // 入账日期 = 参数.入账日期
                taxBokDetail.setBookDate(infoDto.getBookDate());
                // 凭证编号 = 账务明细（当前表）.凭证编号
                taxBokDetail.setBookId(infoDto.getBookId());
                // 处理序列 = max_tdy_处理序列
                taxBokDetail.setBookOrder(max_tdy_bookOrder);
                /**MODIFY BY WUJIE ON 20190725 START
                 // 币种 = 账务明细（当前表）.原币
                 taxBokDetail.setoCcy(infoDto.getoCcy());
                 **/
                // 币种 = 账务明细（当前表）.原币
                taxBokDetail.setoCcy(infoDto.getbCcy());
                /**MODIFY BY WUJIE ON 20190725 END**/
                // 增值税类型 = 税额计提分类配置.增值税类型
                taxBokDetail.setVatType(configDto.getVatType());
                // 确认标识 = 税额计提分类配置.确认标识
                taxBokDetail.setTaxPType(configDto.getTaxPType());
                // 发生场景 = 税额计提分类配置.发生场景
                taxBokDetail.setIncomeSource(configDto.getIncomeSource());
                // 收入科目号 = 账务明细（当前表）.科目号
                taxBokDetail.setSubjectNo(infoDto.getSubjectNo());
                // 分支序号 = 账务明细（当前表）M.分支序号	add by yanya 20191225
                taxBokDetail.setBranch(infoDto.getBranch());

                // 税则字段取值逻辑
                // 税则entity = 税则map.get(账务明细（当前表）.账务明细代码).filter(增值税类型 = 税额计提分类配置.增值税类型)
                BigDecimal taxRate = new BigDecimal("0.03");
                if (!configDto.getCalcType().equals(DictCodeList.增值税_计算方式_发生额累加.getValue())) {
                    if (taxRuleDtoMap.get(infoDto.getBokDetailId()) == null) {
                        continue;
                    }

                    List<TaxRuleDto> listTaxRuleDto = taxRuleDtoMap.get(
                        infoDto.getBokDetailId()).stream().filter(item -> {
                        // 定义增值税类型
                        String valType = "";
                        // 增值税类型对应数值转换
                        // 贷款业务收入同时识别成利息收入和红利收入
                        if (configDto.getVatType().equals(DictCodeList.增值税类型_利息收入.getValue())
                            || configDto.getVatType().equals(
                            DictCodeList.增值税类型_红利收入.getValue())) {
                            valType = DictCodeList.增值税类型2_贷款业务收入.getValue();
                        }
                        // 金融商品转让收入识别价差收入
                        if (configDto.getVatType().equals(
                            DictCodeList.增值税类型_价差收入.getValue())) {
                            valType = DictCodeList.增值税类型2_金融商品转让收入.getValue();
                        }
                        return valType.equals(item.getIncomeType());
                    }).collect(Collectors.toList());
                    // 税则为空的场合,continue继续下一条处理;
                    if (CollectionUtils.isEmpty(listTaxRuleDto)) {
                        continue;
                    }
                    // 税则 entity = list.get(0)
                    TaxRuleDto entity = listTaxRuleDto.get(0);
                    // 计税方式 = 税则entity.计税方式
                    taxBokDetail.setCalcType(entity.getTaxableSign());
                    // 税率 = 税则entity.税率
                    // 定义 税率 = 税则entity.税率
                    taxRate = entity.getTaxRate();
                    taxBokDetail.setTaxRate(taxRate);
                    // 税则代码 = 税则entity.流水号
                    taxBokDetail.setTaxRegId(entity.getSeqNo());
                    // 免税收入类型 = 税则entity.免税收入类型
                    taxBokDetail.setFeeType(entity.getFeeType());
                }
                // 税额相关字段取值逻辑
                // if (计算方式 = 发生额累加)
                if (configDto.getCalcType().equals(DictCodeList.增值税_计算方式_发生额累加.getValue())) {
                    // 定义 增值税金额 = 账务明细（当前表）.本位币发生额
                    BigDecimal taxAmount = infoDto.getbHappenAmt();
                    taxBokDetail.setTaxAmt(taxAmount);
                    // if (发生科目借贷方向 != 账务明细（当前表）.借贷方向)
                    if (!configDto.getCdFlag().equals(infoDto.getCdFlag())) {
                        // 当日计税发生额 = -1 * 增值税金额
                        taxBokDetail.setTaxAmt(
                            ArithmeticUtil.multiply(new BigDecimal(-1), taxAmount));
                    }
                    // if (税额计提分类配置.计入进销项 = '计入减项')
                    if (configDto.getInOut().equals(DictCodeListExt1.增项_减项_计入减项.getValue())) {
                        // 当日计税发生额 = -1 * 增值税金额
                        taxBokDetail.setTaxAmt(
                            ArithmeticUtil.multiply(new BigDecimal(-1), taxAmount));
                    }
                    // 发生含税销售额 = 含税收入计算共通(账务明细（当前表）.本位币发生额 , 税率, 明细数据小数位数,明细数据位数处理方式)
                    BigDecimal happenAmt = vatDrawCommonNewService.allIncomeCalculate(
                        taxBokDetail.getTaxAmt(), taxRate, decimalDigits, roundingMode);
                    taxBokDetail.setHappenAmt(happenAmt);
                    // 计税余额发生额 = 当日计税发生额
                    taxBokDetail.setBalAmt(taxBokDetail.getTaxAmt());
                    taxBokDetail.setCalcType("MA");
                }
                // else if (计算方式 = 发生额计算)
                else if (configDto.getCalcType().equals(DictCodeList.增值税_计算方式_发生额计算.getValue())) {
                    // 定义 发生含税销售额 = 账务明细（当前表）.本位币发生额
                    BigDecimal happenAmt = infoDto.getbHappenAmt();
                    // if (发生科目借贷方向 != 账务明细（当前表）.借贷方向)
                    if (!configDto.getCdFlag().equals(infoDto.getCdFlag())) {
                        // 发生含税销售额 = -1 * 发生含税销售额
                        happenAmt = ArithmeticUtil.multiply(new BigDecimal(-1), happenAmt);
                    }
                    // if (税额计提分类配置.计入进销项 = '计入减项')
                    if (configDto.getInOut().equals(DictCodeListExt1.增项_减项_计入减项.getValue())) {
                        // 发生含税销售额 = -1 * 发生含税销售额
                        happenAmt = ArithmeticUtil.multiply(new BigDecimal(-1), happenAmt);
                    }
                    // 发生含税销售额 = 发生含税销售额
                    taxBokDetail.setHappenAmt(happenAmt);
                    // 当日计税发生额 = 增值税计算共通(账务明细（当前表）.本位币发生额 , 税率, 明细数据小数位数,明细数据位数处理方式)
                    taxBokDetail.setTaxAmt(vatDrawCommonNewService.taxCalculate(happenAmt, taxRate,
                        decimalDigits, roundingMode, calcFormula));
                    // 计税余额发生额 = 当日计税发生额
                    taxBokDetail.setBalAmt(taxBokDetail.getTaxAmt());
                }
                // else if (计算方式 = 占比计算)
                else if (configDto.getCalcType().equals(DictCodeList.增值税_计算方式_占比计算.getValue())) {
                    // 本位币发生额
                    bHappenAmt = infoDto.getbHappenAmt();
                    // if(借贷方向 = 借方)
                    if (DictCodeList.借贷方向_借.getValue().equals(infoDto.getCdFlag())) {
                        // 账务明细（当前表）M.本位币发生额取反
                        bHappenAmt = ArithmeticUtil.multiply(new BigDecimal(-1), bHappenAmt);
                    }
                    // if(余额方向 = 贷方)
                    if (DictCodeList.余额方向_贷方.getValue().equals(infoDto.getBalFlagHappenDate())) {
                        // 账务明细（当前表）M.本位币余额（发生日）
                        bAmtHappenDate = infoDto.getbAmtHappenDate();
                        bAmtHappenDate = ArithmeticUtil.multiply(new BigDecimal(-1),
                            bAmtHappenDate);
                    }
                    // 查询占比模式下该场景余额 , 查询内容：增值税账务明细余额MAP
                    // 查询条件
                    // 定义key
                    ValTaxBokBalanceKeyDto keyDto = new ValTaxBokBalanceKeyDto();
                    // 账务明细代码 = 账务明细（当前表）.账务明细代码
                    keyDto.setBokDetailId(infoDto.getBokDetailId());
                    //分支序号 = 账务明细（当前表）M.分支序号 add by yanya on 20191225
                    keyDto.setBranch(infoDto.getBranch());
                    // 增值税类型 = 税额计提分类配置.增值税类型
                    keyDto.setVatType(configDto.getVatType());
                    // 确认类型 = "预估"
                    keyDto.setTaxPType(DictCodeList.增值税_确认类型_预估计税.getValue());
                    // 发生场景=税额计提分类配置.占比基数
                    keyDto.setIncomeSource(configDto.getCalcBase());
                    // 获取 增值税账务明细余额
                    TaxBokBalance taxBalanceDto = taxBokBalanceMap.get(keyDto);
                    if (taxBalanceDto == null) {
                        continue;
                    }
                    // 定义 发生含税销售额
                    BigDecimal happenAmtAll = BigDecimal.ZERO;
                    // IF (占比方式 = 收益占比 or 占比方式 = 成本占比)
                    if (DictCodeList.增值税_占比比例_收益占比.getValue().equals(configDto.getCalcScale())
                        || DictCodeList.增值税_占比比例_成本占比.getValue().equals(
                        configDto.getCalcScale())) {

                        // IF (占比基数 = '待抵扣增值额')
                        if (DictCodeList.增值税_发生场景_待抵扣增值额.getValue().equals(
                            configDto.getCalcBase())) {
                            // IF (配置.是否处理待抵扣增值额 = 'Y' && 配置.待抵扣增值额处理方式 = '按照数量扣减')
                            if (DictCodeList.是否_是.getValue().equals(isCalcTurned)
                                && DictCodeListExt2.待抵扣增值额处理方式_按照数量扣减.getValue().equals(
                                turnedCalcType)) {
                                // List<账务明细（当前表）> 当期凭证 = list<账务明细（当前表）>.filter(凭证编号 =
                                // 账务明细（当前表）M.凭证编号)
                                List<BookLatestInfoDto> listVouchNow = listBookLatestInfo.stream().filter(
                                    item -> item.getBookId().equals(infoDto.getBookId())).collect(
                                    Collectors.toList());
                                // 账务明细（当前表） 当前成本 = 当期凭证.filter(基础科目性质 = 占比科目性质)
                                BookLatestInfoDto costNow = listVouchNow.stream().filter(
                                    item -> configDto.getCalcSnature().equals(
                                        item.getBaseSubjectNature())).findFirst().get();
                                // 待抵扣信息 = 待抵扣信息MAP.get(账务明细（当前表）M.账务明细代码)
                                TaxTurnedBalance taxTurnedBal = taxTurnedBalMap.get(
                                    infoDto.getBokDetailId());
                                // 待抵扣增值额
                                BigDecimal incomeAmt = BigDecimal.ZERO;
                                // 抵扣增值额
                                BigDecimal incomeAmt_act = BigDecimal.ZERO;
                                // 剩余待抵扣增值额
                                BigDecimal incomeAmtLeft = BigDecimal.ZERO;
                                // 待抵扣资产份额
                                BigDecimal cbAmt = BigDecimal.ZERO;
                                // 抵扣资产份额
                                BigDecimal cbAmt_act = BigDecimal.ZERO;
                                // 剩余待抵扣资产份额
                                BigDecimal cbAmtLeft = BigDecimal.ZERO;
                                if (taxTurnedBal != null && BigDecimal.ZERO.compareTo(
                                    taxTurnedBal.getIncomeAmt()) != 0) {
                                    // 待抵扣增值额 = 待抵扣信息.待抵扣增值额
                                    incomeAmt = taxTurnedBal.getIncomeAmt();
                                    // 待抵扣资产份额 = 待抵扣信息.待抵扣资产份额
                                    cbAmt = taxTurnedBal.getCbAmt();
                                    // IF (当前成本.发生数量 >= 待抵扣信息.待抵扣资产份额)
                                    if (costNow.getHappenNumber().compareTo(
                                        taxTurnedBal.getCbAmt()) >= 0) {
                                        // 抵扣增值额 = 待抵扣信息.待抵扣增值额 * -1
                                        incomeAmt_act = taxTurnedBal.getIncomeAmt().negate();
                                        // 抵扣资产份额 = 待抵扣资产份额 * -1
                                        cbAmt_act = taxTurnedBal.getCbAmt().negate();
                                    }
                                    else {
                                        // 抵扣增值额 = 待抵扣增值额 * 当前成本.发生数量/
                                        // 待抵扣信息.待抵扣资产份额 *明细数据小数位数 * -1,明细数据位数处理方式做小数位处理
                                        incomeAmt_act = ArithmeticUtil.divide(
                                            ArithmeticUtil.multiply(taxTurnedBal.getIncomeAmt(),
                                                costNow.getHappenNumber()).negate(),
                                            taxTurnedBal.getCbAmt(), decimalDigits, roundingMode);
                                        // 抵扣资产份额 = 账务明细（当前表）M.发生数量 * -1
                                        cbAmt_act = infoDto.getHappenNumber().negate();
                                    }
                                    // 发生含税销售额 = 抵扣增值额
                                    happenAmtAll = incomeAmt_act;

                                    // IF (确认标识 == '预估计税')
                                    if (DictCodeList.增值税_确认类型_预估计税.getValue().equals(
                                        taxBokDetail.getTaxPType())) {
                                        // 剩余待抵扣增值额 = 待抵扣增值额 + 抵扣增值额
                                        incomeAmtLeft = ArithmeticUtil.doAdd(incomeAmt,
                                            incomeAmt_act);
                                        // 剩余待抵扣资产份额 = 待抵扣资产份额 + 抵扣资产份额
                                        cbAmtLeft = ArithmeticUtil.doAdd(cbAmt, cbAmt_act);

                                        // 待抵扣信息MAP更新待抵扣增值额 = 剩余待抵扣增值额
                                        taxTurnedBalMap.get(infoDto.getBokDetailId()).setIncomeAmt(
                                            incomeAmtLeft);
                                        // 待抵扣信息MAP更新待抵扣资产份额 = 剩余待抵扣资产份额
                                        taxTurnedBalMap.get(infoDto.getBokDetailId()).setCbAmt(
                                            cbAmtLeft);

                                        // 定义 增值税已抵扣增值额
                                        TaxDeductedBalance deductedBalance = new TaxDeductedBalance();
                                        // 增值税已抵扣增值额.待抵扣增值额发生额 = 抵扣增值额
                                        deductedBalance.setTaxAmt(incomeAmt_act);
                                        // 增值税已抵扣增值额.待抵扣增值税余额 = 剩余待抵扣增值额
                                        deductedBalance.setBalAmt(incomeAmtLeft);
                                        // 增值税已抵扣增值额.待抵扣资产份额发生额 = 抵扣资产份额
                                        deductedBalance.setCbAmt(cbAmt_act);
                                        // 增值税已抵扣增值额.待抵扣资产份额余额 = 剩余待抵扣资产份额
                                        deductedBalance.setCbBalance(cbAmtLeft);
                                        // 增值税已抵扣增值额.账套代码
                                        deductedBalance.setBooksetId(taxBokDetail.getBooksetId());
                                        // 增值税已抵扣增值额.账套日期
                                        deductedBalance.setBooksetDate(
                                            taxBokDetail.getBooksetDate());
                                        // 增值税已抵扣增值额.流水号
                                        deductedBalance.setSeqNo(taxBokDetail.getSeqNo());
                                        // 增值税已抵扣增值额.账务明细流水
                                        deductedBalance.setBokSeqno(taxBokDetail.getBokSeqno());
                                        // 增值税已抵扣增值额.账务明细代码
                                        deductedBalance.setBokDetailId(
                                            taxBokDetail.getBokDetailId());
                                        // 增值税已抵扣增值额.发生日期
                                        deductedBalance.setHappenDate(
                                            taxBokDetail.getHappenDate());
                                        // 增值税已抵扣增值额.入账日期
                                        deductedBalance.setBookDate(taxBokDetail.getBookDate());
                                        // 增值税已抵扣增值额.凭证编号
                                        deductedBalance.setBookId(taxBokDetail.getBookId());
                                        // 增值税已抵扣增值额.处理序列 = max_已抵扣增值额处理序列 自增长1
                                        deductedBalance.setBookOrder(
                                            maxBookOrder.add(BigDecimal.ONE));
                                        // 增值税已抵扣增值额.币种
                                        deductedBalance.setoCcy(taxBokDetail.getoCcy());

                                        listDeductedBalance.add(deductedBalance);
                                    }
                                }
                            }
                        }
                        else {
                            // IF (占比基数 != '待抵扣增值额')

                            // 计税收入余额 = 增值税账务明细余额.计税收入余额
                            BigDecimal incomeAmtAll = taxBalanceDto.getIncomeAmtAll();
                            // List<账务明细（当前表）> 当期凭证 = list<账务明细（当前表）>.filter(凭证编号 =
                            // 账务明细（当前表）M.凭证编号)
                            List<BookLatestInfoDto> listVouchNow = listBookLatestInfo.stream().filter(
                                item -> item.getBookId().equals(infoDto.getBookId())).collect(
                                Collectors.toList());
                            // 账务明细（当前表） 当前成本 = 当期凭证.filter(基础科目性质 = 占比科目性质)
                            BookLatestInfoDto costNow = listVouchNow.stream().filter(
                                item -> configDto.getCalcSnature().equals(
                                    item.getBaseSubjectNature())).findFirst().get();
                            // 发生含税销售额 = 计税收入余额 * 账务明细（当前表）M.本位币发生额(借贷方向:贷方,借方取反)/
                            // (账务明细（当前表）M.本位币发生额(借贷方向:贷方,借方取反) +
                            // 账务明细（当前表）M.本位币余额（发生日）(借贷方向:借方,贷方取反))
                            happenAmtAll = ArithmeticUtil.divide(
                                ArithmeticUtil.multiply(incomeAmtAll, costNow.getbHappenAmt()),
                                ArithmeticUtil.doAdd(costNow.getbHappenAmt(),
                                    costNow.getbAmtHappenDate()),
                                decimalDigits, roundingMode);
                        }
                    }
                    else if (DictCodeList.增值税_占比比例_全额占比.getValue().equals(
                        configDto.getCalcScale())) {
                        happenAmtAll = taxBalanceDto.getIncomeAmtAll();
                    }
                    // ELSE IF (占比方式 = 带留存占比)
                    else if (DictCodeList.增值税_占比比例_带留存占比.getValue().equals(
                        configDto.getCalcScale())) {
                        // List<账务明细（当前表）> 当期凭证 = list<账务明细（当前表）>.filter(凭证编号 = 账务明细（当前表）M.凭证编号)
                        List<BookLatestInfoDto> listVouchNow = listBookLatestInfo.stream().filter(
                            item -> item.getBookId().equals(infoDto.getBookId())).collect(
                            Collectors.toList());
                        // 账务明细（当前表） 当前成本 = 当期凭证.filter(基础科目性质 = 占比科目性质)
                        BookLatestInfoDto costNow = listVouchNow.stream().filter(
                            item -> configDto.getCalcSnature().equals(
                                item.getBaseSubjectNature())).findFirst().get();
                        // 成本占比反算收益 = 账务明细（当前表）M.本位币发生额(贷方,借方取反) * (当前成本.本位币发生额 + 当前成本.本位币余额（发生日）)
                        // / 当前成本.本位币发生额
                        BigDecimal costRCIncome = ArithmeticUtil.divide(
                            ArithmeticUtil.multiply(bHappenAmt, ArithmeticUtil.doAdd(
                                costNow.getbHappenAmt(), costNow.getbAmtHappenDate())),
                            costNow.getbHappenAmt(), scale);
                        // 轧差收益 = 成本占比反算收益 - (账务明细（当前表）M.本位币发生额(借贷方向:贷方,借方取反) +
                        // 账务明细（当前表）M.本位币余额（发生日）(余额方向:借方,贷方取反))
                        BigDecimal rollingIncome = ArithmeticUtil.subtract(costRCIncome,
                            ArithmeticUtil.doAdd(bHappenAmt, bAmtHappenDate), scale, null);
                        // 计税收入余额 = 增值税账务明细余额.计税收入余额 + 轧差收益
                        BigDecimal incomeAmtAll = ArithmeticUtil.doAdd(scale, null,
                            taxBalanceDto.getIncomeAmtAll(), rollingIncome);
                        // 发生含税销售额 = 计税收入余额 * 账务明细（当前表）M.本位币发生额/ (账务明细（当前表）M.本位币发生额 +
                        // 账务明细（当前表）M.本位币余额（发生日）)
                        // *明细数据小数位数,明细数据位数处理方式做小数位处理
                        happenAmtAll = ArithmeticUtil.divide(
                            ArithmeticUtil.multiply(incomeAmtAll, costNow.getbHappenAmt()),
                            ArithmeticUtil.doAdd(costNow.getbHappenAmt(),
                                costNow.getbAmtHappenDate()),
                            decimalDigits, roundingMode);
                    }
                    if (BigDecimal.ZERO.compareTo(happenAmtAll) == 0) {
                        continue;
                    }
                    // 定义 发生增值税金额 = 增值税计算共通(发生应交含税销售额 , 税率, 明细数据小数位数, 明细数据尾数处理方式)
                    BigDecimal happenTaxAmt = vatDrawCommonNewService.taxCalculate(happenAmtAll,
                        taxRate, decimalDigits, roundingMode, calcFormula);
                    // 获取 金额计入方式
                    List<Boolean> listIsCarrt = vatDrawCommonNewService.haddleIsCarrt(
                        configDto.getIsCarrt());
                    // if (税额计提分类配置.计入进销项 = '计入减项')
                    if (DictCodeListExt1.增项_减项_计入减项.getValue().equals(configDto.getInOut())) {
                        // 发生含税销售额 = -1 * 发生应交含税销售额
                        taxBokDetail.setHappenAmt(ArithmeticUtil.multiply(decimalDigits, null,
                            new BigDecimal(-1), happenAmtAll));
                        if (listIsCarrt != null && listIsCarrt.size() > 2) {
                            // if (金额计入方式.是否计入余额)
                            if (listIsCarrt.get(0)) {
                                // 计税余额发生额 = -1 * 发生增值税金额
                                taxBokDetail.setBalAmt(ArithmeticUtil.multiply(decimalDigits, null,
                                    new BigDecimal(-1), happenTaxAmt));
                            }
                            // if (金额计入方式.是否计入发生额)
                            if (listIsCarrt.get(1)) {
                                // 当日计税发生额 = -1 * 发生增值税金额
                                taxBokDetail.setTaxAmt(ArithmeticUtil.multiply(decimalDigits, null,
                                    new BigDecimal(-1), happenTaxAmt));
                            }
                            // if (金额计入方式.是否计入结转金额)
                            if (listIsCarrt.get(2)) {
                                // 计税余额发生额 = -1 * 发生增值税金额
                                taxBokDetail.setCarryAmt(ArithmeticUtil.multiply(decimalDigits,
                                    null, new BigDecimal(-1), happenTaxAmt));
                            }
                        }
                    }
                    else {
                        // 发生含税销售额 = 发生应交含税销售额
                        taxBokDetail.setHappenAmt(happenAmtAll);
                        if (listIsCarrt != null && listIsCarrt.size() > 2) {
                            // if (金额计入方式.是否计入余额)
                            if (listIsCarrt.get(0)) {
                                // 计税余额发生额 = 发生增值税金额
                                taxBokDetail.setBalAmt(happenTaxAmt);
                            }
                            // if (金额计入方式.是否计入发生额)
                            if (listIsCarrt.get(1)) {
                                // 当日计税发生额 = 发生增值税金额
                                taxBokDetail.setTaxAmt(happenTaxAmt);
                            }
                            // if (金额计入方式.是否计入结转金额)
                            if (listIsCarrt.get(2)) {
                                // 计税余额发生额 = 发生增值税金额
                                taxBokDetail.setCarryAmt(happenTaxAmt);
                            }
                        }
                    }
                }
                //add by lby on 20201226 start
                /**
                 * 调用共通方法：共通【基础科目性质查询】
                 * 参数名					基础科目性质查询
                 * 分组代码					增值税损益科目(对应CODE） TAX_LXSR_KMCX
                 * 返回值：					基础科目性质集合
                 *
                 * 定义变量：增值税损益科目列表 = 返回值.基础科目性质集合
                 */
                //定义变量：增值税损益科目列表 = 返回值.基础科目性质集合
                List<String> valSubjectNatureList = accountingCommonService.getValSubjectNatureList(
                        DictCodeListNatureGroup.分组代码_增值税损益科目查询.getValue());
                //账务明细（当前表） 当前损益科目 = 当期凭证.filter(基础科目性质 in （变量：增值税损益科目列表）)
                List<BookLatestInfoDto> listInfoDto = listBookLatestInfo.stream().filter(
                        item -> valSubjectNatureList.contains(item.getBaseSubjectNature())).collect(
                        Collectors.toList());
                //add by lby on 20201226 end
                // List<账务明细（当前表）> 当期凭证 = list<账务明细（当前表）>.filter(凭证编号 = 账务明细（当前表）M.凭证编号)
                // 账务明细（当前表） 当前损益科目 = 当期凭证.filter(科目号 like '6011%')
               /* List<BookLatestInfoDto> listInfoDto = listBookLatestInfo.stream().filter(
                    item -> item.getBookId().equals(infoDto.getBookId())
                            && item.getSubjectNo().startsWith("6011")).collect(
                    Collectors.toList());*/
                // IF (账务明细（当前表） 当前损益科目 != NULL
                if (!CollectionUtils.isEmpty(listInfoDto)) {
                    // 增值税账务明细.收入科目号 = 账务明细（当前表）.当前损益科目
                    taxBokDetail.setSubjectNo(listInfoDto.get(0).getSubjectNo());
                }
                else {
                    // add by weipeng on 20220309：会计引擎升级，利息收入-增值税备抵区分投资目的 start
                    SubjectMappingConfig mappingConfig = mappingConfigMap.get(infoDto.getBooksetId()+infoDto.getBokDetailId());
                    // 投资目的不为空，且为FVTPL，走投资收益-利息收入-增值税备抵科目
                    if(mappingConfig!=null && DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(mappingConfig.getInvAim())){
                        taxBokDetail.setSubjectNo("6111600101");
                    }
                    // 否则走利息收入-增值税备抵科目
                    else{
                        taxBokDetail.setSubjectNo("60119901");
                    }
                    // 增值税账务明细.收入科目号= '60119901'--利息收入-增值税备抵科目
//                    taxBokDetail.setSubjectNo("60119901");
                    // add by weipeng on 20220309：会计引擎升级，利息收入-增值税备抵区分投资目的 end
                }
                // 增值税账务明细List.add(增值税账务明细)
                ValTaxBokDetails.add(taxBokDetail);

                // IF 配置.是否处理待抵扣增值额 = 'Y'
                if (DictCodeList.是否_是.getValue().equals(isCalcTurned)) {
                    // IF (待抵扣增值额处理方式 = '按照增值额扣减')
                    if (DictCodeListExt2.待抵扣增值额处理方式_按照增值额扣减.getValue().equals(turnedCalcType)) {
                        // IF (增值税账务明细.增值税类型 = '价差收入' and 增值税账务明细.确认标识 = '发生计税'
                        // and 待抵扣增值额MAP.get(增值税账务明细.账务明细代码) 不为空且
                        // 待抵扣增值额MAP.get(增值税账务明细.账务明细代码).待抵扣增值额余额 != 0 AND 增值税账务明细.发生含税销售额 > 0
                        if (DictCodeList.税类型_价差收入.getValue().equals(taxBokDetail.getVatType())
                            && DictCodeList.增值税_确认类型_发生计税.getValue().equals(
                            taxBokDetail.getTaxPType())
                            && taxTurnedBalMap.get(taxBokDetail.getBokDetailId()) != null
                            && taxTurnedBalMap.get(
                            taxBokDetail.getBokDetailId()).getIncomeAmt().compareTo(
                            BigDecimal.ZERO) != 0
                            && taxBokDetail.getHappenAmt().compareTo(BigDecimal.ZERO) > 0) {
                            // 定义增值税账务明细抵扣 = 增值税账务明细COPY
                            //                            TaxBokDetail detail = (TaxBokDetail)taxBokDetail.clone();
                            TaxBokDetail detail = new TaxBokDetail();
                            BeanUtil.copyPropertiesIgnoreNull(detail, taxBokDetail);
                            // 剩余抵扣增值额 = 待抵扣信息MAP.get(增值税账务明细.账务明细代码)
                            TaxTurnedBalance turnedBalance = taxTurnedBalMap.get(
                                taxBokDetail.getBokDetailId());
                            // IF 增值税账务明细抵扣.发生含税销售额 > 待抵扣增值额.get(增值税账务明细.账务明细代码).待抵扣增值额余额
                            if (detail.getHappenAmt().compareTo(
                                turnedBalance.getIncomeAmt()) > 0) {
                                // 增值税账务明细抵扣.发生含税销售额 = 待抵扣增值额.get(增值税账务明细.账务明细代码).待抵扣增值额余额 * -1
                                detail.setHappenAmt(turnedBalance.getIncomeAmt().negate());
                                // 增值税账务明细抵扣.当日计税发生额 = 增值税计算共通(发生含税销售 , 税率, 明细数据小数位数,明细数据位数处理方式)
                                detail.setTaxAmt(
                                    vatDrawCommonNewService.taxCalculate(detail.getHappenAmt(),
                                        taxRate, decimalDigits, roundingMode, calcFormula));
                                // 增值税账务明细抵扣.计税余额发生额 = 增值税账务明细抵扣.当日计税发生额
                                detail.setBalAmt(detail.getTaxAmt());
                            }
                            else {
                                // 增值税账务明细抵扣.发生含税销售额 = 发生含税销售额 * -1
                                detail.setHappenAmt(detail.getHappenAmt().negate());
                                // 增值税账务明细抵扣.当日计税发生额 = 当日计税发生额 * -1
                                detail.setTaxAmt(detail.getTaxAmt().negate());
                                // 增值税账务明细抵扣.计税余额发生额 = 计税余额发生额 * -1
                                detail.setBalAmt(detail.getBalAmt().negate());
                            }
                            // 增值税账务明细List.add(增值税账务明细抵扣)
                            ValTaxBokDetails.add(detail);
                            // 剩余抵扣增值额 = 剩余抵扣增值额 + 增值税账务明细抵扣.发生含税销售额
                            turnedBalance.setIncomeAmt(ArithmeticUtil.doAdd(
                                turnedBalance.getIncomeAmt(), detail.getHappenAmt()));

                            // 定义 增值税已抵扣增值额
                            TaxDeductedBalance deductedBalance = new TaxDeductedBalance();
                            // 增值税已抵扣增值额.待抵扣增值额发生额 = 增值税账务明细抵扣.发生含税销售额
                            deductedBalance.setTaxAmt(detail.getHappenAmt());
                            // 增值税已抵扣增值额.待抵扣增值税余额 = 剩余抵扣增值额
                            deductedBalance.setBalAmt(turnedBalance.getIncomeAmt());
                            // 增值税已抵扣增值额.账套代码 = 增值税账务明细抵扣.账套代码
                            deductedBalance.setBooksetId(detail.getBookId());
                            // 增值税已抵扣增值额.账套日期 = 增值税账务明细抵扣.账套日期
                            deductedBalance.setBooksetDate(detail.getBooksetDate());
                            // 增值税已抵扣增值额.流水号 = 增值税账务明细抵扣.流水号
                            deductedBalance.setSeqNo(detail.getSeqNo());
                            // 增值税已抵扣增值额.账务明细流水 = 增值税账务明细抵扣.账务明细流水
                            deductedBalance.setBokSeqno(detail.getBokSeqno());
                            // 增值税已抵扣增值额.账务明细代码 = 增值税账务明细抵扣.账务明细代码
                            deductedBalance.setBokDetailId(detail.getBokDetailId());
                            // 增值税已抵扣增值额.发生日期 = 增值税账务明细抵扣.发生日期
                            deductedBalance.setHappenDate(detail.getHappenDate());
                            // 增值税已抵扣增值额.入账日期 = 增值税账务明细抵扣.入账日期
                            deductedBalance.setBookDate(detail.getBookDate());
                            // 增值税已抵扣增值额.凭证编号 = 增值税账务明细抵扣.凭证编号
                            deductedBalance.setBookId(detail.getBookId());
                            // 增值税已抵扣增值额.处理序列 = max_已抵扣增值额处理序列 自增长1
                            deductedBalance.setBookOrder(maxBookOrder.add(BigDecimal.ONE));
                            // 增值税已抵扣增值额.币种
                            deductedBalance.setoCcy(detail.getoCcy());

                            listDeductedBalance.add(deductedBalance);
                        }
                    }
                }
            }
            // 4.3.2 更新余额
            this.updateTaxBokBalanceMap(ValTaxBokDetails, taxBokBalanceMap, acctEntity,
                max_tdy_bookOrder, cfConfigs, taxBokBalanceMem);
            listValTaxBokDetail.addAll(ValTaxBokDetails);
        }

        // 增值税账务明细余额List.addAll(增值税账务明细余额.values())
        List<TaxBokBalance> listValTaxBokBalance = new ArrayList<>();
        listValTaxBokBalance.addAll(taxBokBalanceMap.values());
        // 设置币种为增值税账务明细余额List中的随便一条的币种
        if (CollectionUtils.isNotEmpty(listValTaxBokBalance)) {
            acctEntity.setStandardCcy(listValTaxBokBalance.get(0).getoCcy());
        }
        else {
            // 取账套的本位币
            // acctEntity.setStandardCcy(DictCodeList.币种_人民币_CNY.getValue());
            acctEntity.setStandardCcy(vatDrawMapper.getStandardCcy(booksetId));
        }

        // added by wangzhiang 【辽阳银行】利息收入增加参数，支持按资产维度先初步汇总舍位后在进行账套级别汇总 20191115 start
        List<TaxBokBalance> groupedTaxBokBalanceList = new ArrayList<>();
        if ("02".equals(cfConfigs.get(TAX_SUM_MODE))) {
            List<String> scopes = Arrays.asList(cfConfigs.get(TAX_SUM_SCOPE).split(GlobalConstant.COMMA));
            Map<String, List<TaxBokBalance>> groupedMap = listValTaxBokBalance.stream().collect(Collectors.groupingBy(
                x -> x.getVatType() + GlobalConstant.UNDER_LINE + x.getTaxPType() + GlobalConstant.COMMA
                     + x.getCalcType() + GlobalConstant.COMMA + x.getBokDetailId()));
            for (Map.Entry<String, List<TaxBokBalance>> entry : groupedMap.entrySet()) {
                if (CollectionUtils.isNotEmpty(entry.getValue())) {
                    String[] code = entry.getKey().split(GlobalConstant.COMMA);
                    if (scopes.contains(code[0])) {
                        TaxBokBalance balance = null;
                        for (TaxBokBalance entity : entry.getValue()) {
                            if (balance == null) {
                                try {
                                    balance = new TaxBokBalance();
                                    BeanUtil.copyProperties(balance, entity);
                                }
                                catch (Exception e) {
                                    logger.error(e.getMessage(),e);
                                }
                            }
                            else {
                                // 资产维度全精度合并
                                balance.setCarryAmt(ArithmeticUtil.doAdd(balance.getCarryAmt(), entity.getCarryAmt()));
                                balance.setHappenAmt(
                                    ArithmeticUtil.doAdd(balance.getHappenAmt(), entity.getHappenAmt()));
                                balance.setIncomeHappenAmt(
                                    ArithmeticUtil.doAdd(balance.getIncomeHappenAmt(), entity.getIncomeHappenAmt()));
                            }
                        }
                        groupedTaxBokBalanceList.add(balance);
                    }
                    else {
                        groupedTaxBokBalanceList.addAll(entry.getValue());
                    }
                }
            }
            groupedTaxBokBalanceList.forEach(x->{
                // 保留两位精度
                //                x.setCarryAmt(ArithmeticUtil.round(x.getCarryAmt(),NumberEnum.MONEY));
                x.setHappenAmt(ArithmeticUtil.round(x.getHappenAmt(),NumberEnum.MONEY));
            });
        }
        // 根据增值税类型和确认标识分别对各自对应的金额汇总处理
        TaxBalanceAmtDto amtDto = TaxBalanceAmtDto.createDtoByBalance(listValTaxBokBalance, groupedTaxBokBalanceList);
        // added by wangzhiang 【辽阳银行】利息收入增加参数，支持按资产维度先初步汇总舍位后在进行账套级别汇总 20191115 end

        // 没有取到频率的情况,取为每日新增
        int intScale = 2;
        if (cfConfigs.get(TAX_ACCR_TYPE_0102_0202) != null
            && DictCodeList.计提方式_区间倒减.getValue().equals(cfConfigs.get(TAX_ACCR_TYPE_0102_0202))) {
            intScale = 10;
        }

        int jcScale = 2;
        if (cfConfigs.get(TAX_ACCR_TYPE_0302) != null
            && DictCodeList.计提方式_区间倒减.getValue().equals(cfConfigs.get(TAX_ACCR_TYPE_0302))) {
            jcScale = 10;
        }
        TaxBalanceAmtDto.formatAmtDtoByScale(amtDto, intScale, jcScale);

        // 汇总价差收入处理 round(sum(增值税账务明细余额.当日发生额(小于0设值为0)),2)
        // if (BigDecimal.ZERO.compareTo(amtDto.getQrjcHappenAmt()) > 0) {
        // amtDto.setQrjcHappenAmt(BigDecimal.ZERO);
        // }

        // 5.生成账套税余额
        // 5.1 查询各账套税汇总昨日余额
        // 返回结果中，每种增值税类型对应只有一条数据
        List<TaxBokBalanceSum> listYtsTaxBalanceSum = vatDrawMapper.queryYtsTaxBalanceSum(
            DateUtil.addDay(happenDate, -1), bookDate, max_bookOrder, booksetId);

        // listYtsTaxBalanceSum 转换为map，主键为 增值税类型+确认标识
        // 定义昨日汇总余额Map
        Map<String, TaxBokBalanceSum> ytsTaxBalanceSumMap = new HashMap<>();
        for (

            TaxBokBalanceSum item : listYtsTaxBalanceSum) {
            ytsTaxBalanceSumMap.put(item.getVatType() + item.getTaxPType(), item);
        }

        // 5.2 确认各税额并记账
        // 定义 增值税余额汇总List
        List<TaxBokBalanceSum> listTdyBalanceSum = new ArrayList<TaxBokBalanceSum>();

        // 5.2.1.汇总利息收入确认
        this.handleBalanceSum1(acctEntity, returnResult, DictCodeList.增值税类型_利息收入.getValue(),
            amtDto, cfConfigs, listValTaxBokBalance);

        // 5.2.2.汇总预估利息收入计提
        // 获取对应昨日增值税汇总余额
        this.handleBalanceSum2(acctEntity, returnResult, max_tdy_bookOrder, listTdyBalanceSum,
            ytsTaxBalanceSumMap, DictCodeList.增值税类型_利息收入.getValue(),
            DictCodeList.增值税_确认类型_预估计税.getValue(), amtDto, cfConfigs, listValTaxBokBalance);

        // 5.2.3.汇总利息收入计提
        this.handleBalanceSum3(acctEntity, returnResult, max_tdy_bookOrder, listTdyBalanceSum,
            ytsTaxBalanceSumMap, DictCodeList.增值税类型_利息收入.getValue(),
            DictCodeList.增值税_确认类型_发生计税.getValue(), amtDto, cfConfigs, listValTaxBokBalance);

        // 5.2.4.红利同上处理
        // 5.2.4.1 汇总红利收入确认
        this.handleBalanceSum1(acctEntity, returnResult, DictCodeList.增值税类型_红利收入.getValue(),
            amtDto, cfConfigs, listValTaxBokBalance);

        // 5.2.4.2 汇总预估红利收入计提
        // 获取对应昨日增值税汇总余额
        this.handleBalanceSum2(acctEntity, returnResult, max_tdy_bookOrder, listTdyBalanceSum,
            ytsTaxBalanceSumMap, DictCodeList.增值税类型_红利收入.getValue(),
            DictCodeList.增值税_确认类型_预估计税.getValue(), amtDto, cfConfigs, listValTaxBokBalance);

        // 5.2.4.3 汇总红利收入计提
        this.handleBalanceSum3(acctEntity, returnResult, max_tdy_bookOrder, listTdyBalanceSum,
            ytsTaxBalanceSumMap, DictCodeList.增值税类型_红利收入.getValue(),
            DictCodeList.增值税_确认类型_发生计税.getValue(), amtDto, cfConfigs, listValTaxBokBalance);

        // 5.2.5.汇总价差收入处理
        // 定义 变量.今日价差收入增值税汇总
        TaxBokBalanceSum priceDiffBalanceSum = this.handleBalanceSum5(acctEntity, returnResult,
            max_tdy_bookOrder, listTdyBalanceSum, ytsTaxBalanceSumMap, amtDto, cfConfigs, jcScale);
        // 增值税汇总入集合
        listTdyBalanceSum.add(priceDiffBalanceSum);

        // 5.2.6.汇总公允价值变动
        // 定义 变量.今日价差收入增值税汇总
        this.handleBalanceSum6(acctEntity, returnResult, max_tdy_bookOrder, listTdyBalanceSum,
            ytsTaxBalanceSumMap, priceDiffBalanceSum, amtDto, cfConfigs, jcScale);

        // IF 配置.是否处理待抵扣增值额 = 'Y' && 待抵扣增值额处理方式 = '按照增值额扣减'
        if (DictCodeList.是否_是.getValue().equals(isCalcTurned)
            && DictCodeListExt2.待抵扣增值额处理方式_按照增值额扣减.getValue().equals(turnedCalcType)) {
            // 如果账务明细代码当前没有份额,增值税待抵扣增值额清0
            // 循环 待抵扣增值额MAP
            for (String key : taxTurnedBalMap.keySet()) {
                SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
                // 账套代码
                searchDto.setBooksetId(booksetId);
                // 基础科目性质
                searchDto.setBaseSubjectNatureList(accountingCommonService.getValSubjectNatureList(
                    DictCodeListExt1.分组代码_成本类对应科目查询.getValue()));
                // 发生日
                searchDto.setHappenDate(happenDate);
                // 入账日
                searchDto.setBookDate(bookDate);
                // 账务明细代码
                searchDto.setBokDetailId(key);
                // 获取科目余额 -【调用共通方法：科目余额汇总查询】
                Map<String, BigDecimal> feeBaseMap = accountingCommonService.getSubjectBalanceCollection(
                    searchDto, DictCodeList.借贷方向_贷.getValue(),
                    /**MODIFY BY WUJIE ON 20190725 START REMARK:本位币汇总
                     null**/
                    DictCodeList.是否_否.getValue()
                    /**MODIFY BY WUJIE ON 20190725 END**/
                );
                // IF (返回值.余额 ==0)
                if (BigDecimal.ZERO.compareTo(
                    feeBaseMap.get(taxTurnedBalMap.get(key).getoCcy())) == 0) {
                    // 定义 增值税已抵扣增值额
                    TaxDeductedBalance deductedBalance = new TaxDeductedBalance();
                    // 增值税已抵扣增值额.待抵扣增值额发生额 = 待抵扣信息MAP.VALUE.待抵扣增值额余额
                    deductedBalance.setTaxAmt(taxTurnedBalMap.get(key).getIncomeAmt());
                    // 增值税已抵扣增值额.待抵扣增值税余额 = 0
                    deductedBalance.setBalAmt(BigDecimal.ZERO);
                    // 增值税已抵扣增值额.待抵扣资产份额发生额 = 待抵扣信息MAP.VALUE.待抵扣资产份额余额
                    deductedBalance.setCbAmt(taxTurnedBalMap.get(key).getCbAmt());
                    // 增值税已抵扣增值额.待抵扣资产份额余额 = 0
                    deductedBalance.setCbBalance(BigDecimal.ZERO);
                    // 增值税已抵扣增值额.账套代码
                    deductedBalance.setBooksetId(booksetId);
                    // 增值税已抵扣增值额.账套日期
                    deductedBalance.setBooksetDate(acctEntity.getBookDate());
                    // 增值税已抵扣增值额.流水号
                    deductedBalance.setSeqNo(
                        dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    // 增值税已抵扣增值额.账务明细流水
                    deductedBalance.setBokSeqno(null);
                    // 增值税已抵扣增值额.账务明细代码
                    deductedBalance.setBokDetailId(taxTurnedBalMap.get(key).getBokDetailId());
                    // 增值税已抵扣增值额.发生日期
                    deductedBalance.setHappenDate(happenDate);
                    // 增值税已抵扣增值额.入账日期
                    deductedBalance.setBookDate(bookDate);
                    // 增值税已抵扣增值额.凭证编号
                    deductedBalance.setBookId(null);
                    // 增值税已抵扣增值额.处理序列 = max_已抵扣增值额处理序列 自增长1
                    deductedBalance.setBookOrder(maxBookOrder.add(BigDecimal.ONE));
                    // 增值税已抵扣增值额.币种
                    deductedBalance.setoCcy(taxTurnedBalMap.get(key).getoCcy());

                    listDeductedBalance.add(deductedBalance);
                }
            }
        }

        // 增值税账务明细流水入结果集
        vatBatchResult.setListValTaxBokDetail(listValTaxBokDetail);
        // 增值税账务明细余额入结果集
        vatBatchResult.setListTaxBokBalance(listValTaxBokBalance);
        // 增值税账务明细余额汇总入结果集
        vatBatchResult.setListTaxBokBalanceSum(listTdyBalanceSum);
        // 账务明细（当前表）+科目明细 入当前表
        returnResult.setProdId(
            acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());
        vatBatchResult.setBookGenerateResult(returnResult);
        // 增值税已抵扣增值额汇总入结果集
        vatBatchResult.setListTaxDeductedBalance(listDeductedBalance);

        return vatBatchResult;
    }

    /**
     * 汇总利息收入确认/汇总红利收入确认
     *
     * @param acctEntity reader返回结果
     * @param returnResult 批处理返回结果集
     * @param vatType 增值税类型
     * @param amtDto 金额类Dto
     * @param cfConfigs
     * @param listValTaxBokBalance 增值税余额明细
     * @see
     * @since 1.0
     */
    private void handleBalanceSum1(PrdCashDto acctEntity, BookGenerateResult returnResult,
                                   String vatType, TaxBalanceAmtDto amtDto,
                                   Map<String, String> cfConfigs,
                                   List<TaxBokBalance> listValTaxBokBalance) {
        String subjectHandleType = cfConfigs.get(ValDictCode.利息收入增值税科目处理方式.getValue());
        // IF (配置.增值税科目处理方式 is null || 配置.增值税科目处理方式= '使用收入备抵科目' ) {
        if (!StringUtil.isNotEmpty(subjectHandleType)
            || ValDictCodeList.利息收入增值税科目处理方式_使用收入备抵科目.getValue().equals(subjectHandleType)) {
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            String tableId = "";
            String remark = "";
            // 金额元素场景赋值
            // 定义变量 暂估结转金额
            BigDecimal carryAmt1 = BigDecimal.ZERO;
            // 定义变量 应交结转金额
            BigDecimal carryAmt2 = BigDecimal.ZERO;
            // 汇总利息收入确认
            if (vatType.equals(DictCodeList.增值税类型_利息收入.getValue())) {
                carryAmt1 = amtDto.getLxqrygCarryAmt().negate();
                carryAmt2 = amtDto.getLxqrfsCarryAmt();
                // TableId = DictCodeListTableId.会计场景代码_增值税结转-结转利息收入税-直接投资时.getValue())
                // 此处即为 会计场景代码_增值税确认_确认利息收入税_直接投资时
                if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                    tableId = DictCodeListTableId.会计场景代码_增值税确认_通道_确认利息收入税.getValue();
                    remark = "增值税确认-通道-确认利息收入税";
                } else {
                    tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认利息收入税.getValue();
                    remark = "增值税确认-直投-确认利息收入税";
                }

                // KeyEnum.REMARK.getCode() = "增值税确认_确认利息收入税_直接投资时"

                // 对应KEY
                if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                    accountAmountMap.put(BookConstant.CHL_PI_ANT, carryAmt1);
                    accountAmountMap.put(BookConstant.CHL_VAT, carryAmt2);
                    accountAmountMap.put(BookConstant.CHL_PI_ANT+BookConstant.BCCYAMT, carryAmt1);
                    accountAmountMap.put(BookConstant.CHL_VAT+BookConstant.BCCYAMT, carryAmt2);
                } else {
                    accountAmountMap.put(BookConstant.PI_ANT, carryAmt1);
                    accountAmountMap.put(BookConstant.VAT_ANT, carryAmt2);

                    accountAmountMap.put(BookConstant.PI_ANT+BookConstant.BCCYAMT, carryAmt1);
                    accountAmountMap.put(BookConstant.VAT_ANT+BookConstant.BCCYAMT, carryAmt2);
                }
                // 金额元素.差额 = 应交结转金额 - 暂估结转金额
                BigDecimal cha = ArithmeticUtil.subtract(carryAmt1, carryAmt2, scale, null);
                if (BigDecimal.ZERO.compareTo(cha) < 0) {
                    accountAmountMap.put(BookConstant.NETAMT_C, cha);
                    accountAmountMap.put(BookConstant.NETAMT_C+BookConstant.BCCYAMT, cha);
                }
                else {
                    accountAmountMap.put(BookConstant.NETAMT_D, cha.negate());
                    accountAmountMap.put(BookConstant.NETAMT_D+BookConstant.BCCYAMT, cha.negate());
                }
            }
            // 汇总红利收入确认
            if (vatType.equals(DictCodeList.增值税类型_红利收入.getValue())) {
                carryAmt1 = amtDto.getHlqrygCarryAmt().negate();
                carryAmt2 = amtDto.getHlqrfsCarryAmt();
                // TableId = DictCodeListTableId.会计场景代码_增值税确认_确认红利入税_红利下发日_直接投资时.getValue())
                //                tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认红利入税.getValue();
                //                // KeyEnum.REMARK.getCode() = "增值税确认_确认红利入税_红利下发日_直接投资时"
                //                remark = "增值税确认-直投-确认红利入税";
                if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                    tableId = DictCodeListTableId.会计场景代码_增值税确认_通道_确认红利入税.getValue();
                    remark = "增值税确认-通道-确认红利入税";
                } else {
                    tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认红利入税.getValue();
                    remark = "增值税确认-直投-确认红利入税";
                }
                // 对应KEY
                if (BigDecimal.ZERO.compareTo(carryAmt1) < 0) {
                    accountAmountMap.put(BookConstant.PI_BONUS_D, carryAmt1);
                    accountAmountMap.put(BookConstant.PI_BONUS_D+BookConstant.BCCYAMT, carryAmt1);

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_D, carryAmt1);
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_D+BookConstant.BCCYAMT, carryAmt1);
                    }
                }
                else {

                    accountAmountMap.put(BookConstant.PI_BONUS_C, carryAmt1.negate());
                    accountAmountMap.put(BookConstant.PI_BONUS_C+BookConstant.BCCYAMT, carryAmt1.negate());

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_C, carryAmt1.negate());
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_C+BookConstant.BCCYAMT, carryAmt1.negate());
                    }

                }
                if (BigDecimal.ZERO.compareTo(carryAmt2) < 0) {
                    accountAmountMap.put(BookConstant.VAT_BONUS_C, carryAmt2);
                    accountAmountMap.put(BookConstant.VAT_BONUS_C+BookConstant.BCCYAMT, carryAmt2);

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        accountAmountMap.put(BookConstant.CHL_VAT_C, carryAmt2);
                        accountAmountMap.put(BookConstant.CHL_VAT_C+BookConstant.BCCYAMT, carryAmt2);
                    }
                }
                else {
                    accountAmountMap.put(BookConstant.VAT_BONUS_D, carryAmt2.negate());
                    accountAmountMap.put(BookConstant.VAT_BONUS_D+BookConstant.BCCYAMT, carryAmt2.negate());
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        accountAmountMap.put(BookConstant.CHL_VAT_D, carryAmt2.negate());
                        accountAmountMap.put(BookConstant.CHL_VAT_D+BookConstant.BCCYAMT, carryAmt2.negate());
                    }
                }
                // 金额元素.差额 = 应交结转金额 - 暂估结转金额
                BigDecimal cha = ArithmeticUtil.subtract(carryAmt1, carryAmt2, scale, null);
                if (BigDecimal.ZERO.compareTo(cha) < 0) {
                    accountAmountMap.put(BookConstant.NETAMT_C, cha);
                    accountAmountMap.put(BookConstant.NETAMT_C+BookConstant.BCCYAMT, cha);
                }
                else {

                    accountAmountMap.put(BookConstant.NETAMT_D, cha.negate());
                    accountAmountMap.put(BookConstant.NETAMT_D+BookConstant.BCCYAMT, cha.negate());
                }
            }
            // 组装记账元素
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 塞入记账场景树节点 = 会计场景
            ad.setTreeNode(acctEntity.getBookSetTreeNode());
            // 场景代码
            ad.setTableId(tableId);
            // 入账共通处理
            vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark, acctEntity,
                returnResult, DictCodeList.冲正标识_正常交易.getValue());
        }
        // IF (配置.增值税科目处理方式 = '冲减原收入科目' ) {
        if (ValDictCodeList.利息收入增值税科目处理方式_冲减原收入科目.getValue().equals(subjectHandleType)) {
            // 定义 变量.增值税余额明细分组
            Map<String, List<TaxBokBalance>> taxBalanceMap = new HashMap<>();
            if (vatType.equals(DictCodeList.增值税类型_利息收入.getValue())) {
                // 增值税类型:利息收入,计税方式：简易计税，且按账务明细代码分组
                taxBalanceMap = listValTaxBokBalance.stream().filter(
                    item -> DictCodeList.计税方式_简易计税.getValue().equals(item.getCalcType())
                            && item.getVatType().equals(
                        DictCodeList.增值税类型_利息收入.getValue())).collect(
                    Collectors.groupingBy(TaxBokBalance::getBokDetailId));
            }
            if (vatType.equals(DictCodeList.增值税类型_红利收入.getValue())) {
                // 增值税类型:红利收入,计税方式：简易计税，且按账务明细代码分组
                taxBalanceMap = listValTaxBokBalance.stream().filter(
                    item -> DictCodeList.计税方式_简易计税.getValue().equals(item.getCalcType())
                            && item.getVatType().equals(
                        DictCodeList.增值税类型_红利收入.getValue())).collect(
                    Collectors.groupingBy(TaxBokBalance::getBokDetailId));
            }
            // 循环上述查询结果
            for (String key : taxBalanceMap.keySet()) {
                List<TaxBokBalance> listBokBalance = taxBalanceMap.get(key);
                // 初始化定义 - 存放该笔记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountMap = new HashMap<>();
                String tableId = "";
                String remark = "";
                String 内部科目编码 = innerCodeMap.get(listBokBalance.get(0).getSubjectNo());
                // 金额元素场景赋值
                // 定义变量 暂估结转金额
                BigDecimal carryAmt1 = BigDecimal.ZERO;
                // 定义变量 应交结转金额
                BigDecimal carryAmt2 = BigDecimal.ZERO;
                for (TaxBokBalance item : listBokBalance) {
                    // round(增值税账务明细余额.当日结转累计发生额(case when 确认标识 = 预估计税),2) 暂估结转金额
                    if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        carryAmt1 = ArithmeticUtil.round(item.getCarryAmt().negate(), NumberEnum.MONEY);
                    }
                    // round(增值税账务明细余额.当日结转累计发生额(case when 确认标识 = 发生计税),2) 应交结转金额
                    else if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_发生计税.getValue())) {
                        carryAmt2 = ArithmeticUtil.round(item.getCarryAmt(), NumberEnum.MONEY);
                    }
                }
                // 汇总利息收入确认
                if (vatType.equals(DictCodeList.增值税类型_利息收入.getValue())) {
                    // TableId = DictCodeListTableId.会计场景代码_增值税结转-结转利息收入税-直接投资时.getValue())
                    // 此处即为 会计场景代码_增值税确认_确认利息收入税_直接投资时
                    //                    tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认利息收入税.getValue();
                    //                    // KeyEnum.REMARK.getCode() = "增值税确认_确认利息收入税_直接投资时"
                    //                    remark = "增值税确认-直投-确认利息收入税";
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        tableId = DictCodeListTableId.会计场景代码_增值税确认_通道_确认利息收入税.getValue();
                        // KeyEnum.REMARK.getCode() = "增值税确认_确认利息收入税_直接投资时"
                        remark = "增值税确认-通道-确认利息收入税";
                    } else {
                        tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认利息收入税.getValue();
                        // KeyEnum.REMARK.getCode() = "增值税确认_确认利息收入税_直接投资时"
                        remark = "增值税确认-直投-确认利息收入税";
                    }

                    // 金额元素.暂估结转金额 = 暂估结转金额
                    accountAmountMap.put(BookConstant.PI_ANT, carryAmt1);
                    // 金额元素.应交结转金额 = 应交结转金额
                    accountAmountMap.put(BookConstant.VAT_ANT, carryAmt2);

                    // 金额元素.暂估结转金额 _本位币= 暂估结转金额
                    accountAmountMap.put(BookConstant.PI_ANT+BookConstant.BCCYAMT, carryAmt1);
                    // 金额元素.应交结转金额 _本位币 = 应交结转金额
                    accountAmountMap.put(BookConstant.VAT_ANT+BookConstant.BCCYAMT, carryAmt2);

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.暂估结转金额 = 暂估结转金额
                        accountAmountMap.put(BookConstant.CHL_PI_ANT, carryAmt1);
                        // 金额元素.应交结转金额 = 应交结转金额
                        accountAmountMap.put(BookConstant.CHL_VAT, carryAmt2);

                        // 金额元素.暂估结转金额 _本位币= 暂估结转金额
                        accountAmountMap.put(BookConstant.CHL_PI_ANT+BookConstant.BCCYAMT, carryAmt1);
                        // 金额元素.应交结转金额 _本位币 = 应交结转金额
                        accountAmountMap.put(BookConstant.CHL_VAT+BookConstant.BCCYAMT, carryAmt2);
                    }
                    // 金额元素.差额 || 收入科目号 = 应交结转金额 - 暂估结转金额
                    BigDecimal cha = ArithmeticUtil.subtract(carryAmt1, carryAmt2, scale, null);
                    if (BigDecimal.ZERO.compareTo(cha) < 0) {
                        accountAmountMap.put(BookConstant.NETAMT_C + "_" + 内部科目编码, cha);
                        accountAmountMap.put(BookConstant.NETAMT_C + "_" + 内部科目编码+BookConstant.BCCYAMT, cha);
                    }
                    else {
                        accountAmountMap.put(BookConstant.NETAMT_D + "_" + 内部科目编码,
                            cha.negate());
                        accountAmountMap.put(BookConstant.NETAMT_D + "_" + 内部科目编码+BookConstant.BCCYAMT,
                            cha.negate());
                    }
                }
                // 汇总红利收入确认
                if (vatType.equals(DictCodeList.增值税类型_红利收入.getValue())) {
                    // TableId = DictCodeListTableId.会计场景代码_增值税确认_确认红利入税_红利下发日_直接投资时.getValue())
                    //                    tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认红利入税.getValue();
                    //                    // KeyEnum.REMARK.getCode() = "增值税确认_确认红利入税_红利下发日_直接投资时"
                    //                    remark = "增值税确认-直投-确认红利入税";
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        tableId = DictCodeListTableId.会计场景代码_增值税确认_通道_确认红利入税.getValue();
                        // KeyEnum.REMARK.getCode() = "增值税确认_确认红利入税_红利下发日_直接投资时"
                        remark = "增值税确认-通道-确认红利入税";
                    } else {
                        tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认红利入税.getValue();
                        // KeyEnum.REMARK.getCode() = "增值税确认_确认红利入税_红利下发日_直接投资时"
                        remark = "增值税确认-直投-确认红利入税";
                    }
                    // 对应KEY
                    if (BigDecimal.ZERO.compareTo(carryAmt1) < 0) {

                        accountAmountMap.put(BookConstant.PI_BONUS_D, carryAmt1);
                        accountAmountMap.put(BookConstant.PI_BONUS_D+BookConstant.BCCYAMT, carryAmt1);

                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                            accountAmountMap.put(BookConstant.CHL_PI_BONUS_D, carryAmt1);
                            accountAmountMap.put(BookConstant.CHL_PI_BONUS_D+BookConstant.BCCYAMT, carryAmt1);
                        }
                    }
                    else {
                        accountAmountMap.put(BookConstant.PI_BONUS_C, carryAmt1.negate());
                        accountAmountMap.put(BookConstant.PI_BONUS_C+BookConstant.BCCYAMT, carryAmt1.negate());

                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                            accountAmountMap.put(BookConstant.CHL_PI_BONUS_C, carryAmt1.negate());
                            accountAmountMap.put(BookConstant.CHL_PI_BONUS_C+BookConstant.BCCYAMT, carryAmt1.negate());
                        }
                    }
                    if (BigDecimal.ZERO.compareTo(carryAmt2) < 0) {
                        accountAmountMap.put(BookConstant.VAT_BONUS_C, carryAmt2);
                        accountAmountMap.put(BookConstant.VAT_BONUS_C+BookConstant.BCCYAMT, carryAmt2);

                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                            accountAmountMap.put(BookConstant.CHL_VAT_C, carryAmt2);
                            accountAmountMap.put(BookConstant.CHL_VAT_C+BookConstant.BCCYAMT, carryAmt2);
                        }
                    }
                    else {
                        accountAmountMap.put(BookConstant.VAT_BONUS_D, carryAmt2.negate());
                        accountAmountMap.put(BookConstant.VAT_BONUS_D+BookConstant.BCCYAMT, carryAmt2.negate());
                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                            accountAmountMap.put(BookConstant.CHL_VAT_D, carryAmt2.negate());
                            accountAmountMap.put(BookConstant.CHL_VAT_D+BookConstant.BCCYAMT, carryAmt2.negate());
                        }
                    }
                    // 金额元素.差额 || 收入科目号 = 应交结转金额 - 暂估结转金额
                    BigDecimal cha = ArithmeticUtil.subtract(carryAmt1, carryAmt2, scale, null);
                    if (BigDecimal.ZERO.compareTo(cha) < 0) {
                        accountAmountMap.put(BookConstant.NETAMT_C + "_" + 内部科目编码, cha);
                        accountAmountMap.put(BookConstant.NETAMT_C + "_" + 内部科目编码+BookConstant.BCCYAMT, cha);
                    }
                    else {
                        accountAmountMap.put(BookConstant.NETAMT_D + "_" + 内部科目编码,
                            cha.negate());
                        accountAmountMap.put(BookConstant.NETAMT_D + "_" + 内部科目编码+BookConstant.BCCYAMT,
                            cha.negate());
                    }
                }
                // 组装记账元素
                // 初始化定义 - 记账场景要素管理
                AccountDriver ad = new AccountDriver();
                // 塞入记账场景树节点 = 会计场景
                ad.setTreeNode(acctEntity.getBookSetTreeNode());
                // 场景代码
                ad.setTableId(tableId);
                // 入账共通处理
                vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark,
                    acctEntity, returnResult, DictCodeList.冲正标识_正常交易.getValue());
            }
        }

    }

    /**
     * 5.2.2.汇总预估利息收入计提
     *
     * @param acctEntity reader返回实体
     * @param returnResult 批处理返回结果集
     * @param max_tdy_bookOrder 当前最大处理序列
     * @param listTdyBalanceSum 当前增值税汇总余额List
     * @param ytsTaxBalanceSumMap 昨日增值税汇总余额
     * @param amtDto 金额类Dto
     * @param vatType 增值税类型
     * @param taxPType 确认标识
     * @param cfConfigs
     * @param listValTaxBokBalance 增值税余额明细
     * @see
     * @since 1.0
     */
    private void handleBalanceSum2(PrdCashDto acctEntity, BookGenerateResult returnResult,
                                   BigDecimal max_tdy_bookOrder,
                                   List<TaxBokBalanceSum> listTdyBalanceSum,
                                   Map<String, TaxBokBalanceSum> ytsTaxBalanceSumMap,
                                   String vatType, String taxPType, TaxBalanceAmtDto amtDto,
                                   Map<String, String> cfConfigs,
                                   List<TaxBokBalance> listValTaxBokBalance) {
        // 定义 变量.场景Id
        String tableId = "";
        // 定义 备注 remark
        String remark = "";
        TaxBokBalanceSum tdyBalanceSum = new TaxBokBalanceSum();
        // 定义 变量.收入发生额
        BigDecimal IncomeHappenAmt = BigDecimal.ZERO;
        // 定义 变量.发生额
        BigDecimal happenAmt = BigDecimal.ZERO;
        // 预估结转金额
        BigDecimal carryAmt = BigDecimal.ZERO;
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        BigDecimal maAmt = BigDecimal.ZERO;
        // 定义变量 配置.利息收入增值税科目处理方式
        String subjectHandleType = cfConfigs.get(ValDictCode.利息收入增值税科目处理方式.getValue());
        // IF (配置.增值税科目处理方式 is null || 配置.增值税科目处理方式= '使用收入备抵科目' ) {
        if (!StringUtil.isNotEmpty(subjectHandleType)
            || ValDictCodeList.利息收入增值税科目处理方式_使用收入备抵科目.getValue().equals(subjectHandleType)) {
            if (vatType.equals(DictCodeList.增值税类型_利息收入.getValue())) {
                // 新建增值税汇总入集合
                tdyBalanceSum = vatDrawCommonNewService.createTdyBalanceSumByYst(
                    ytsTaxBalanceSumMap.get(DictCodeList.增值税类型_利息收入.getValue()
                                            + DictCodeList.增值税_确认类型_预估计税.getValue()),
                    max_tdy_bookOrder, acctEntity);
                tdyBalanceSum.setVatType(DictCodeList.增值税类型_利息收入.getValue());
                tdyBalanceSum.setTaxPType(DictCodeList.增值税_确认类型_预估计税.getValue());
                IncomeHappenAmt = amtDto.getYglxIncomeAmtAll();
                tdyBalanceSum.setIncomeAmtAll(ArithmeticUtil.doAdd(scale, null,
                    tdyBalanceSum.getIncomeAmtAll(), IncomeHappenAmt));
                happenAmt = amtDto.getYglxHappenAmt();
                carryAmt = amtDto.getLxqrygCarryAmt();
                if (BigDecimal.ZERO.compareTo(tdyBalanceSum.getIncomeAmtAll()) == 0) {
                    happenAmt = ArithmeticUtil.doAdd(tdyBalanceSum.getTaxAmt(), carryAmt).negate();
                }
                // add by weipeng on 20220309：会计引擎升级，利息收入-增值税备抵区分投资目的 start
                // 定义 变量.利息收入增值税备抵计税发生额
                BigDecimal happenAmtBd1 = BigDecimal.ZERO;
                // 定义 变量.投资收益利息收入增值税备抵计税发生额
                BigDecimal happenAmtBd2 = BigDecimal.ZERO;
                // 循环
                for (TaxBokBalance item : listValTaxBokBalance) {
                    if (DictCodeList.计税方式_简易计税.getValue().equals(item.getCalcType())
                            && item.getVatType().equals(vatType)
                            && item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        SubjectMappingConfig mappingConfig = mappingConfigMap.get(item.getBooksetId()+item.getBokDetailId());
                        // 非FVTPL，走利息收入-增值税备抵科目
                        if(mappingConfig!=null && !DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(mappingConfig.getInvAim())){
                            // 利息收入增值税备抵计税发生额 += 当日发生额
                            happenAmtBd1 = ArithmeticUtil.doAdd(happenAmtBd1, item.getHappenAmt());
                            // IF 增值税账务明细余额.计税收入余额 = 0
                            if (BigDecimal.ZERO.compareTo(item.getIncomeAmtAll()) == 0) {
                                // 利息收入增值税备抵计税发生额 += 当日发生额 + -1 * 计税余额
                                happenAmtBd1 = ArithmeticUtil.doAdd(happenAmtBd1,ArithmeticUtil.doAdd(item.getHappenAmt(),
                                        ArithmeticUtil.multiply(new BigDecimal(-1), item.getTaxAmt())));
                            }
                        }
                    }
                }
                // 将利息收入增值税备抵计税发生额舌位
                happenAmtBd1 = ArithmeticUtil.round(happenAmtBd1, NumberEnum.MONEY);
                // 投资收益利息收入备抵计税发生额 = 计税发生额 - 利息收入备抵计税发生额
                happenAmtBd2 = ArithmeticUtil.doSub(happenAmt, happenAmtBd1);

                if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                    tableId = DictCodeListTableId.会计场景代码_增值税估计_通道_利息收入.getValue();
                    // KeyEnum.REMARK.getCode() = "增值税估计-估计利息收入税-直接投资时"
                    remark = "增值税估计-通道-利息收入";
                } else {
                    tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_利息收入.getValue();
                    // KeyEnum.REMARK.getCode() = "增值税估计-估计利息收入税-直接投资时"
                    remark = "增值税估计-直投-利息收入";
                }
                // 金额场景元素塞值
                // 利息收入_增值税备抵科目 记账元素赋值
                if (BigDecimal.ZERO.compareTo(happenAmtBd1) < 0) {
                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.PI_INT_D + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue(), happenAmtBd1);
                    accountAmountMap.put(BookConstant.PI_INT_D + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd1);
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_PI_INT_D + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue(), happenAmtBd1);
                        accountAmountMap.put(BookConstant.CHL_PI_INT_D + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd1);
                    }
                }
                else{
                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.PI_INT_C + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue(), happenAmtBd1.negate());
                    accountAmountMap.put(BookConstant.PI_INT_C + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd1.negate());
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_PI_INT_C + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue(), happenAmtBd1.negate());
                        accountAmountMap.put(BookConstant.CHL_PI_INT_C + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd1.negate());
                    }
                }
                // 投资收益_利息收入_增值税备抵科目 记账元素赋值
                if (BigDecimal.ZERO.compareTo(happenAmtBd2) < 0) {
                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.PI_INT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmtBd2);
                    accountAmountMap.put(BookConstant.PI_INT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd2);
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_PI_INT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmtBd2);
                        accountAmountMap.put(BookConstant.CHL_PI_INT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd2);
                    }
                }
                else{
                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.PI_INT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmtBd2.negate());
                    accountAmountMap.put(BookConstant.PI_INT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd2.negate());
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_PI_INT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmtBd2.negate());
                        accountAmountMap.put(BookConstant.CHL_PI_INT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd2.negate());
                    }
                }
                // 对应KEY
                if (BigDecimal.ZERO.compareTo(happenAmt) < 0) {
                    // 金额元素.预估增值税-利息收入税（贷） = 发生额
                    accountAmountMap.put(BookConstant.PI_INT_C, happenAmt);
                    accountAmountMap.put(BookConstant.PI_INT_C+BookConstant.BCCYAMT, happenAmt);
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税（贷） = 发生额
                        accountAmountMap.put(BookConstant.CHL_PI_INT_C, happenAmt);
                        accountAmountMap.put(BookConstant.CHL_PI_INT_C+BookConstant.BCCYAMT, happenAmt);
                    }
                }
                else {
                    // 金额元素.预估增值税-利息收入税（借） = 发生额
                    accountAmountMap.put(BookConstant.PI_INT_D, happenAmt.negate());
                    accountAmountMap.put(BookConstant.PI_INT_D+BookConstant.BCCYAMT, happenAmt.negate());

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_PI_INT_D, happenAmt.negate());
                        accountAmountMap.put(BookConstant.CHL_PI_INT_D+BookConstant.BCCYAMT, happenAmt.negate());
                    }
                }
                // add by weipeng on 20220309：会计引擎升级，利息收入-增值税备抵区分投资目的 end
                maAmt = amtDto.getMayglxAmt();
            }
            if (vatType.equals(DictCodeList.增值税类型_红利收入.getValue())) {
                tdyBalanceSum = vatDrawCommonNewService.createTdyBalanceSumByYst(
                    ytsTaxBalanceSumMap.get(DictCodeList.增值税类型_红利收入.getValue()
                                            + DictCodeList.增值税_确认类型_预估计税.getValue()),
                    max_tdy_bookOrder, acctEntity);
                tdyBalanceSum.setVatType(DictCodeList.增值税类型_红利收入.getValue());
                tdyBalanceSum.setTaxPType(DictCodeList.增值税_确认类型_预估计税.getValue());
                IncomeHappenAmt = amtDto.getYghlIncomeAmtAll();
                tdyBalanceSum.setIncomeAmtAll(ArithmeticUtil.doAdd(scale, null,
                    tdyBalanceSum.getIncomeAmtAll(), IncomeHappenAmt));
                happenAmt = amtDto.getYghlHappenAmt();
                carryAmt = amtDto.getHlqrygCarryAmt();
                if (BigDecimal.ZERO.compareTo(tdyBalanceSum.getIncomeAmtAll()) == 0) {
                    happenAmt = ArithmeticUtil.doAdd(tdyBalanceSum.getTaxAmt(), carryAmt).negate();
                }
                // TableId = DictCodeListTableId.会计场景代码_增值税估计_估计红利收入税_直接投资时.getValue())
                //                tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_红利收入.getValue();
                //                // KeyEnum.REMARK.getCode() = "增值税估计_估计红利收入税_直接投资时"
                //                remark = "增值税估计-直投-红利收入";
                if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                    tableId = DictCodeListTableId.会计场景代码_增值税估计_通道_红利收入.getValue();
                    // KeyEnum.REMARK.getCode() = "增值税估计_估计红利收入税_直接投资时"
                    remark = "增值税估计-通道-红利收入";
                } else {
                    tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_红利收入.getValue();
                    // KeyEnum.REMARK.getCode() = "增值税估计_估计红利收入税_直接投资时"
                    remark = "增值税估计-直投-红利收入";
                }
                // 金额场景元素塞值
                // 对应KEY
                if (BigDecimal.ZERO.compareTo(happenAmt) < 0) {
                    accountAmountMap.put(BookConstant.PI_BONUS_C, happenAmt);
                    accountAmountMap.put(BookConstant.PI_BONUS_C+BookConstant.BCCYAMT, happenAmt);
                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.PI_BONUS_D + "_" + "TZSY_GLSR_ZZSBD", happenAmt);
                    accountAmountMap.put(BookConstant.PI_BONUS_D + "_" + "TZSY_GLSR_ZZSBD"+BookConstant.BCCYAMT, happenAmt);

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_C, happenAmt);
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_C+BookConstant.BCCYAMT, happenAmt);
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_D + "_" + "TZSY_GLSR_ZZSBD", happenAmt);
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_D + "_" + "TZSY_GLSR_ZZSBD"+BookConstant.BCCYAMT, happenAmt);
                    }
                }
                else {
                    accountAmountMap.put(BookConstant.PI_BONUS_D, happenAmt.negate());
                    accountAmountMap.put(BookConstant.PI_BONUS_D+BookConstant.BCCYAMT, happenAmt.negate());

                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.PI_BONUS_C + "_" + "TZSY_GLSR_ZZSBD",happenAmt.negate());

                    accountAmountMap.put(BookConstant.PI_BONUS_C + "_" + "TZSY_GLSR_ZZSBD"+BookConstant.BCCYAMT,
                        happenAmt.negate());

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_D, happenAmt.negate());
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_D+BookConstant.BCCYAMT, happenAmt.negate());
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_C + "_" + "TZSY_GLSR_ZZSBD",happenAmt.negate());
                        accountAmountMap.put(BookConstant.CHL_PI_BONUS_C + "_" + "TZSY_GLSR_ZZSBD"+BookConstant.BCCYAMT,
                            happenAmt.negate());
                    }
                }
                maAmt = amtDto.getMayghlAmt();
            }

            tdyBalanceSum.setHappenAmt(
                ArithmeticUtil.doAdd(scale, null, tdyBalanceSum.getHappenAmt(), happenAmt));
            // 计税余额 = 计税余额 + 发生额
            tdyBalanceSum.setTaxAmt(
                ArithmeticUtil.doAdd(scale, null, tdyBalanceSum.getTaxAmt(), happenAmt));
            // 计税余额 = 计税余额 + 结转发生额
            tdyBalanceSum.setTaxAmt(
                ArithmeticUtil.doAdd(scale, null, tdyBalanceSum.getTaxAmt(), carryAmt, maAmt));

            // 入账共通处理
            vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark, acctEntity,
                returnResult, DictCodeList.冲正标识_正常交易.getValue());
            // 增值税汇总入集合
            listTdyBalanceSum.add(tdyBalanceSum);
        }
        // IF (配置.增值税科目处理方式 = '冲减原收入科目' ) {
        if (ValDictCodeList.利息收入增值税科目处理方式_冲减原收入科目.getValue().equals(subjectHandleType)) {
            // 新建增值税汇总入集合
            tdyBalanceSum = vatDrawCommonNewService.createTdyBalanceSumByYst(
                ytsTaxBalanceSumMap.get(vatType + DictCodeList.增值税_确认类型_预估计税.getValue()),
                max_tdy_bookOrder, acctEntity);
            tdyBalanceSum.setVatType(vatType);
            tdyBalanceSum.setTaxPType(DictCodeList.增值税_确认类型_预估计税.getValue());
            // 循环
            for (TaxBokBalance item : listValTaxBokBalance) {
                accountAmountMap = new HashMap<>();
                if (DictCodeList.计税方式_简易计税.getValue().equals(item.getCalcType())
                    && item.getVatType().equals(vatType)
                    && item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                    // 收入发生额 = round(增值税账务明细余额.当日计税收入发生额,2) 收入发生额
                    IncomeHappenAmt = ArithmeticUtil.round(item.getIncomeHappenAmt(),
                        NumberEnum.MONEY);

                    // 发生额 = round(增值税账务明细余额.增值税金额, 2) - round(增值税账务明细余额.当日结转累计发生额, 2)
                    // -ROUND(增值税账务明细余额.增值税金额 - 增值税账务明细余额.当日计税收入发生额 - 增值税账务明细余额.当日结转累计发生额, 2)
                    // 发生额
                    // happenAmt = round(增值税账务明细余额.增值税金额, 2)
                    happenAmt = ArithmeticUtil.round(item.getTaxAmt(), NumberEnum.MONEY);
                    // happenAmt = happenAmt - round(增值税账务明细余额.当日结转累计发生额, 2)
                    happenAmt = ArithmeticUtil.doSub(happenAmt,
                        ArithmeticUtil.round(item.getCarryAmt(), NumberEnum.MONEY));
                    // happenAmt = happenAmt - ROUND(增值税账务明细余额.增值税金额 - 增值税账务明细余额.当日发生额
                    // - 增值税账务明细余额.当日结转累计发生额,2)
                    happenAmt = ArithmeticUtil.doSub(happenAmt,
                        ArithmeticUtil.round(ArithmeticUtil.doSub(item.getTaxAmt(),
                            item.getHappenAmt(), item.getCarryAmt()), NumberEnum.MONEY));

                    // 计税收入余额 = 计税收入余额 + 收入发生额
                    tdyBalanceSum.setIncomeAmtAll(ArithmeticUtil.doAdd(scale, null,
                        tdyBalanceSum.getIncomeAmtAll(), IncomeHappenAmt));
                    // 计税发生额
                    tdyBalanceSum.setHappenAmt(ArithmeticUtil.doAdd(scale, null,
                        tdyBalanceSum.getHappenAmt(), happenAmt));
                    // modified by wangzhiang BSTOO-958 附加税计提凭证错误 20191226 start
                    // 计税余额 = 计税余额 + 发生额 + 暂估结转金额(预估时即为 当日结转累计发生额)
                    //                    tdyBalanceSum.setTaxAmt(ArithmeticUtil.subtract(
                    //                        ArithmeticUtil.doAdd(tdyBalanceSum.getTaxAmt(), happenAmt),
                    //                        item.getCarryAmt(), scale, roundingMode));
                    tdyBalanceSum.setTaxAmt(ArithmeticUtil.doAdd(scale, roundingMode,
                        ArithmeticUtil.doAdd(tdyBalanceSum.getTaxAmt(), happenAmt), item.getCarryAmt()));
                    // 定义 变量.内部科目编码 = 内部科目编码Map.get(余额明细.科目号)
                    String 内部科目编码 = innerCodeMap.get(item.getSubjectNo());
                    // modified by wangzhiang BSTOO-958 附加税计提凭证错误 20191226 end
                    if (vatType.equals(DictCodeList.增值税类型_利息收入.getValue())) {
                        // TableId = DictCodeListTableId.会计场景代码_增值税估计-估计利息收入税-直接投资时.getValue())
                        //                        tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_利息收入.getValue();
                        //                        // KeyEnum.REMARK.getCode() = "增值税估计-估计利息收入税-直接投资时"
                        //                        remark = "增值税估计-直投-利息收入";
                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                            tableId = DictCodeListTableId.会计场景代码_增值税估计_通道_利息收入.getValue();
                            // KeyEnum.REMARK.getCode() = "增值税估计-估计利息收入税-直接投资时"
                            remark = "增值税估计-通道-利息收入";
                        } else {
                            tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_利息收入.getValue();
                            // KeyEnum.REMARK.getCode() = "增值税估计-估计利息收入税-直接投资时"
                            remark = "增值税估计-直投-利息收入";
                        }
                        // 金额场景元素塞值
                        // 对应KEY
                        if (BigDecimal.ZERO.compareTo(happenAmt) < 0) {
                            // 金额元素.预估增值税-利息收入税（贷） = 发生额
                            accountAmountMap.put(BookConstant.PI_INT_C, happenAmt);
                            accountAmountMap.put(BookConstant.PI_INT_C+BookConstant.BCCYAMT, happenAmt);
                            // 金额元素.预估增值税-利息收入税损益（借）|| 收入科目号 = 发生额
                            accountAmountMap.put(BookConstant.PI_INT_D + "_" + 内部科目编码,happenAmt);
                            accountAmountMap.put(BookConstant.PI_INT_D + "_" + 内部科目编码+BookConstant.BCCYAMT,happenAmt);

                            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                                // 金额元素.预估增值税-利息收入税（贷） = 发生额
                                accountAmountMap.put(BookConstant.CHL_PI_INT_C, happenAmt);
                                accountAmountMap.put(BookConstant.CHL_PI_INT_C+BookConstant.BCCYAMT, happenAmt);

                                // 金额元素.预估增值税-利息收入税损益（借）|| 收入科目号 = 发生额
                                accountAmountMap.put(BookConstant.CHL_PI_INT_D + "_" + 内部科目编码,
                                    happenAmt);

                                accountAmountMap.put(BookConstant.CHL_PI_INT_D + "_" + 内部科目编码+BookConstant.BCCYAMT,
                                    happenAmt);
                            }
                        }
                        else {
                            // 金额元素.预估增值税-利息收入税（借） = 发生额
                            accountAmountMap.put(BookConstant.PI_INT_D, happenAmt.negate());
                            accountAmountMap.put(BookConstant.PI_INT_D+BookConstant.BCCYAMT, happenAmt.negate());
                            // 金额元素.预估增值税-利息收入税损益（贷）|| 收入科目号 = 发生额
                            accountAmountMap.put(BookConstant.PI_INT_C + "_" + 内部科目编码,
                                happenAmt.negate());

                            accountAmountMap.put(BookConstant.PI_INT_C + "_" + 内部科目编码+BookConstant.BCCYAMT,
                                happenAmt.negate());

                            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                                // 金额元素.预估增值税-利息收入税（借） = 发生额
                                accountAmountMap.put(BookConstant.CHL_PI_INT_D, happenAmt.negate());
                                accountAmountMap.put(BookConstant.CHL_PI_INT_D+BookConstant.BCCYAMT, happenAmt.negate());
                                // 金额元素.预估增值税-利息收入税损益（贷）|| 收入科目号 = 发生额
                                accountAmountMap.put(BookConstant.CHL_PI_INT_C + "_" + 内部科目编码,
                                    happenAmt.negate());
                                accountAmountMap.put(BookConstant.CHL_PI_INT_C + "_" + 内部科目编码+BookConstant.BCCYAMT,
                                    happenAmt.negate());
                            }
                        }
                    }
                    if (vatType.equals(DictCodeList.增值税类型_红利收入.getValue())) {
                        // TableId = DictCodeListTableId.会计场景代码_增值税估计_估计红利收入税_直接投资时.getValue())
                        //                        tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_红利收入.getValue();
                        //                        // KeyEnum.REMARK.getCode() = "增值税估计_估计红利收入税_直接投资时"
                        //                        remark = "增值税估计-直投-红利收入";
                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                            tableId = DictCodeListTableId.会计场景代码_增值税估计_通道_红利收入.getValue();
                            // KeyEnum.REMARK.getCode() = "增值税估计_估计红利收入税_直接投资时"
                            remark = "增值税估计-通道-红利收入";
                        } else {
                            tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_红利收入.getValue();
                            // KeyEnum.REMARK.getCode() = "增值税估计_估计红利收入税_直接投资时"
                            remark = "增值税估计-直投-红利收入";
                        }
                        // 金额场景元素塞值
                        // 对应KEY
                        if (BigDecimal.ZERO.compareTo(happenAmt) < 0) {
                            accountAmountMap.put(BookConstant.PI_BONUS_C, happenAmt);
                            accountAmountMap.put(BookConstant.PI_BONUS_C+BookConstant.BCCYAMT, happenAmt);
                            // 金额元素.预估增值税-红利收入税损益（借）|| 收入科目号 = 发生额
                            accountAmountMap.put(
                                BookConstant.PI_BONUS_D + "_" + 内部科目编码, happenAmt);

                            accountAmountMap.put(
                                BookConstant.PI_BONUS_D + "_" + 内部科目编码+BookConstant.BCCYAMT, happenAmt);

                            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                                accountAmountMap.put(BookConstant.CHL_PI_BONUS_C, happenAmt);
                                accountAmountMap.put(BookConstant.CHL_PI_BONUS_C+BookConstant.BCCYAMT, happenAmt);
                                // 金额元素.预估增值税-红利收入税损益（借）|| 收入科目号 = 发生额
                                accountAmountMap.put(
                                    BookConstant.CHL_PI_BONUS_D + "_" + 内部科目编码, happenAmt);

                                accountAmountMap.put(
                                    BookConstant.CHL_PI_BONUS_D + "_" + 内部科目编码+BookConstant.BCCYAMT, happenAmt);
                            }
                        }
                        else {
                            accountAmountMap.put(BookConstant.PI_BONUS_D, happenAmt.negate());
                            accountAmountMap.put(BookConstant.PI_BONUS_D+BookConstant.BCCYAMT, happenAmt.negate());

                            // 金额元素.预估增值税-红利收入税损益（借）|| 收入科目号 = 发生额
                            accountAmountMap.put(
                                BookConstant.PI_BONUS_C + "_" + 内部科目编码,happenAmt.negate());

                            accountAmountMap.put(
                                BookConstant.PI_BONUS_C + "_" + 内部科目编码+BookConstant.BCCYAMT,
                                happenAmt.negate());

                            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                                accountAmountMap.put(BookConstant.CHL_PI_BONUS_D, happenAmt.negate());
                                accountAmountMap.put(BookConstant.CHL_PI_BONUS_D+BookConstant.BCCYAMT, happenAmt.negate());

                                // 金额元素.预估增值税-红利收入税损益（借）|| 收入科目号 = 发生额
                                accountAmountMap.put(
                                    BookConstant.CHL_PI_BONUS_C + "_" + 内部科目编码,happenAmt.negate());

                                accountAmountMap.put(
                                    BookConstant.CHL_PI_BONUS_C + "_" + 内部科目编码+BookConstant.BCCYAMT,
                                    happenAmt.negate());
                            }
                        }
                    }
                    // 入账共通处理
                    vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark,
                        acctEntity, returnResult, DictCodeList.冲正标识_正常交易.getValue());
                }
            }
            // 增值税汇总入集合
            listTdyBalanceSum.add(tdyBalanceSum);
        }
    }

    /**
     * 5.2.3.汇总利息收入计提
     *
     * @param acctEntity reader返回实体
     * @param returnResult 批处理返回结果集
     * @param max_tdy_bookOrder 当前最大处理序列
     * @param listTdyBalanceSum 当前增值税汇总余额List
     * @param ytsTaxBalanceSumMap 昨日增值税汇总余额
     * @param amtDto 金额类Dto
     * @param vatType 增值税类型
     * @param taxPType 确认标识
     * @param cfConfigs
     * @param listValTaxBokBalance 增值税余额明细
     * @see
     * @since 1.0
     */
    private void handleBalanceSum3(PrdCashDto acctEntity, BookGenerateResult returnResult,
                                   BigDecimal max_tdy_bookOrder,
                                   List<TaxBokBalanceSum> listTdyBalanceSum,
                                   Map<String, TaxBokBalanceSum> ytsTaxBalanceSumMap,
                                   String vatType, String taxPType, TaxBalanceAmtDto amtDto,
                                   Map<String, String> cfConfigs,
                                   List<TaxBokBalance> listValTaxBokBalance) {
        // 定义 变量.场景Id
        String tableId = "";
        // 定义 备注 remark
        String remark = "";
        // 新建增值税汇总入集合
        TaxBokBalanceSum tdyBalanceSum = new TaxBokBalanceSum();
        BigDecimal IncomeHappenAmt = BigDecimal.ZERO;
        BigDecimal happenAmt = BigDecimal.ZERO;
        BigDecimal carryAmt = BigDecimal.ZERO;
        BigDecimal maAmt = BigDecimal.ZERO;
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 定义变量 配置.利息收入增值税科目处理方式
        String subjectHandleType = cfConfigs.get(ValDictCode.利息收入增值税科目处理方式.getValue());
        // IF (配置.增值税科目处理方式 is null || 配置.增值税科目处理方式= '使用收入备抵科目' ) {
        if (!StringUtil.isNotEmpty(subjectHandleType)
            || ValDictCodeList.利息收入增值税科目处理方式_使用收入备抵科目.getValue().equals(subjectHandleType)) {
            if (vatType.equals(DictCodeList.增值税类型_利息收入.getValue())) {
                // 新建增值税汇总入集合
                tdyBalanceSum = vatDrawCommonNewService.createTdyBalanceSumByYst(
                    ytsTaxBalanceSumMap.get(DictCodeList.增值税类型_利息收入.getValue()
                                            + DictCodeList.增值税_确认类型_发生计税.getValue()),
                    max_tdy_bookOrder, acctEntity);
                tdyBalanceSum.setVatType(DictCodeList.增值税类型_利息收入.getValue());
                tdyBalanceSum.setTaxPType(DictCodeList.增值税_确认类型_发生计税.getValue());
                IncomeHappenAmt = amtDto.getQrlxIncomeAmtAll();
//                happenAmt = amtDto.getQrlxHappenAmt();
                carryAmt = amtDto.getLxqrfsCarryAmt();
                // add by weipeng on 20220309：会计引擎升级，利息收入-增值税备抵区分投资目的 start
                // 定义 变量.利息收入增值税备抵计税发生额
                BigDecimal happenAmtBd1 = BigDecimal.ZERO;
                // 定义 变量.投资收益利息收入增值税备抵计税发生额
                BigDecimal happenAmtBd2 = BigDecimal.ZERO;
                // 循环
                for (TaxBokBalance item : listValTaxBokBalance) {
                    if (DictCodeList.计税方式_简易计税.getValue().equals(item.getCalcType())
                            && item.getVatType().equals(vatType)
                            && item.getTaxPType().equals(DictCodeList.增值税_确认类型_发生计税.getValue())) {
                        SubjectMappingConfig mappingConfig = mappingConfigMap.get(item.getBooksetId()+item.getBokDetailId());
                        // 非FVTPL，走利息收入-增值税备抵科目
                        if(mappingConfig!=null && !DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(mappingConfig.getInvAim())){
                            // 利息收入增值税备抵计税发生额 += 当日发生额
                            happenAmtBd1 = ArithmeticUtil.doAdd(happenAmtBd1, item.getHappenAmt());
                            // IF 增值税账务明细余额.计税收入余额 = 0
                            if (BigDecimal.ZERO.compareTo(item.getIncomeAmtAll()) == 0) {
                                // 利息收入增值税备抵计税发生额 += 当日发生额 + -1 * 计税余额
                                happenAmtBd1 = ArithmeticUtil.doAdd(happenAmtBd1,ArithmeticUtil.doAdd(item.getHappenAmt(),
                                        ArithmeticUtil.multiply(new BigDecimal(-1), item.getTaxAmt())));
                            }
                        }
                    }
                }
                // 将利息收入增值税备抵计税发生额舌位
                happenAmtBd1 = ArithmeticUtil.round(happenAmtBd1, NumberEnum.MONEY);
                // 投资收益利息收入备抵计税发生额 = 计税发生额 - 利息收入备抵计税发生额
                happenAmtBd2 = ArithmeticUtil.doSub(happenAmt, happenAmtBd1);

                if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                    tableId = DictCodeListTableId.会计场景代码_增值税计提_通道_应交利息收入税.getValue();
                    // KeyEnum.REMARK.getCode() = "增值税估计_应交利息收入税_直接投资时"
                    remark = "增值税计提-通道-应交利息收入税";
                } else {
                    tableId = DictCodeListTableId.会计场景代码_增值税计提_直投_应交利息收入税.getValue();
                    // KeyEnum.REMARK.getCode() = "增值税估计_应交利息收入税_直接投资时"
                    remark = "增值税计提-直投-应交利息收入税";
                }
                // 金额场景元素塞值
                // 利息收入_增值税备抵科目 记账元素赋值
                if (BigDecimal.ZERO.compareTo(happenAmtBd1) < 0) {
                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.VAT_INT_D + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue(), happenAmtBd1);
                    accountAmountMap.put(BookConstant.VAT_INT_D + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd1);
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_VAT_D + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue(), happenAmtBd1);
                        accountAmountMap.put(BookConstant.CHL_VAT_D + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd1);
                    }
                }
                else{
                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.VAT_INT_C + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue(), happenAmtBd1.negate());
                    accountAmountMap.put(BookConstant.VAT_INT_C + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd1.negate());
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_VAT_C + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue(), happenAmtBd1.negate());
                        accountAmountMap.put(BookConstant.CHL_VAT_C + "_" + DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd1.negate());
                    }
                }
                // 投资收益_利息收入_增值税备抵科目 记账元素赋值
                if (BigDecimal.ZERO.compareTo(happenAmtBd2) < 0) {
                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.VAT_INT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmtBd2);
                    accountAmountMap.put(BookConstant.VAT_INT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd2);
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_VAT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmtBd2);
                        accountAmountMap.put(BookConstant.CHL_VAT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd2);
                    }
                }
                else{
                    // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.VAT_INT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmtBd2.negate());
                    accountAmountMap.put(BookConstant.VAT_INT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd2.negate());
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 金额元素.预估增值税-利息收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_VAT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmtBd2.negate());
                        accountAmountMap.put(BookConstant.CHL_VAT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmtBd2.negate());
                    }
                }
                // 对应KEY
                // IF 发生额 >0
                if (BigDecimal.ZERO.compareTo(happenAmt) < 0) {
                    // 对应KEY
                    accountAmountMap.put(BookConstant.VAT_INT_C, happenAmt);
                    accountAmountMap.put(BookConstant.VAT_INT_C+BookConstant.BCCYAMT, happenAmt);

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 对应KEY
                        accountAmountMap.put(BookConstant.CHL_VAT_C, happenAmt);
                        accountAmountMap.put(BookConstant.CHL_VAT_C+BookConstant.BCCYAMT, happenAmt);
                    }
                }
                else {
                    accountAmountMap.put(BookConstant.VAT_INT_D, happenAmt.negate());
                    accountAmountMap.put(BookConstant.VAT_INT_D+BookConstant.BCCYAMT, happenAmt.negate());
                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        accountAmountMap.put(BookConstant.CHL_VAT_D, happenAmt.negate());
                        accountAmountMap.put(BookConstant.CHL_VAT_D+BookConstant.BCCYAMT, happenAmt.negate());
                    }
                }
                // add by weipeng on 20220309：会计引擎升级，利息收入-增值税备抵区分投资目的 end
                maAmt = amtDto.getMaqrlxAmt();
            }
            if (vatType.equals(DictCodeList.增值税类型_红利收入.getValue())) {
                // 新建增值税汇总入集合
                tdyBalanceSum = vatDrawCommonNewService.createTdyBalanceSumByYst(
                    ytsTaxBalanceSumMap.get(DictCodeList.增值税类型_红利收入.getValue()
                                            + DictCodeList.增值税_确认类型_发生计税.getValue()),
                    max_tdy_bookOrder, acctEntity);
                tdyBalanceSum.setVatType(DictCodeList.增值税类型_红利收入.getValue());
                tdyBalanceSum.setTaxPType(DictCodeList.增值税_确认类型_发生计税.getValue());
                IncomeHappenAmt = amtDto.getQrhlIncomeAmtAll();
                happenAmt = amtDto.getQrhlHappenAmt();
                carryAmt = amtDto.getHlqrfsCarryAmt();
                // TableId = DictCodeListTableId.会计场景代码_增值税估计_应交红利收入税_直接投资时.getValue())
                //                tableId = DictCodeListTableId.会计场景代码_增值税计提_直投_应交红利收入税.getValue();
                //                // KeyEnum.REMARK.getCode() = "增值税估计_应交红利收入税_直接投资时"
                //                remark = "增值税计提-直投-应交红利收入税";
                if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                    tableId = DictCodeListTableId.会计场景代码_增值税计提_通道_应交红利收入税.getValue();
                    // KeyEnum.REMARK.getCode() = "增值税估计_应交红利收入税_直接投资时"
                    remark = "增值税计提-通道-应交红利收入税";
                } else {
                    tableId = DictCodeListTableId.会计场景代码_增值税计提_直投_应交红利收入税.getValue();
                    // KeyEnum.REMARK.getCode() = "增值税估计_应交红利收入税_直接投资时"
                    remark = "增值税计提-直投-应交红利收入税";
                }
                // IF 发生额 >0
                if (BigDecimal.ZERO.compareTo(happenAmt) < 0) {
                    // 对应KEY
                    accountAmountMap.put(BookConstant.VAT_BONUS_C, happenAmt);
                    accountAmountMap.put(BookConstant.VAT_BONUS_C+BookConstant.BCCYAMT, happenAmt);

                    // 金额元素.应交增值税-红利收入税损益（借） = 发生额
                    accountAmountMap.put(BookConstant.VAT_BONUS_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmt);
                    accountAmountMap.put(BookConstant.VAT_BONUS_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmt);

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        // 对应KEY
                        accountAmountMap.put(BookConstant.CHL_VAT_C, happenAmt);
                        accountAmountMap.put(BookConstant.CHL_VAT_C+BookConstant.BCCYAMT, happenAmt);

                        // 金额元素.应交增值税-红利收入税损益（借） = 发生额
                        accountAmountMap.put(BookConstant.CHL_VAT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(), happenAmt);
                        accountAmountMap.put(BookConstant.CHL_VAT_D + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT, happenAmt);
                    }
                }
                else {
                    accountAmountMap.put(BookConstant.VAT_BONUS_D, happenAmt.negate());
                    accountAmountMap.put(BookConstant.VAT_BONUS_D+BookConstant.BCCYAMT, happenAmt.negate());

                    // 金额元素.应交增值税-利息收入税损益（贷） = 发生额
                    accountAmountMap.put(BookConstant.VAT_BONUS_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(),happenAmt.negate());

                    accountAmountMap.put(BookConstant.VAT_BONUS_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT,happenAmt.negate());

                    if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                        accountAmountMap.put(BookConstant.CHL_VAT_D, happenAmt.negate());
                        accountAmountMap.put(BookConstant.CHL_VAT_D+BookConstant.BCCYAMT, happenAmt.negate());

                        // 金额元素.应交增值税-利息收入税损益（贷） = 发生额
                        accountAmountMap.put(BookConstant.CHL_VAT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue(),happenAmt.negate());

                        accountAmountMap.put(BookConstant.CHL_VAT_C + "_" + DictCodeBaseSubjectNatrueNew.投资收益_股利收入_增值税备抵.getValue()+BookConstant.BCCYAMT,
                            happenAmt.negate());
                    }
                }
                maAmt = amtDto.getMaqrhlAmt();
            }
            // 如果是季初:
            if (CalendarUtil.isFirstDayByCircle(acctEntity.getHappenDate(),
                cfConfigs.get(TAX_CARRYOVER_CYCLE))) {
                // 计税收入余额 = 0 + 收入发生额
                tdyBalanceSum.setIncomeAmtAll(BigDecimal.ZERO);
                // 计税余额 = 0 + 发生额
                tdyBalanceSum.setTaxAmt(BigDecimal.ZERO);
            }
            // 计税收入余额 = 计税收入余额 + 收入发生额
            tdyBalanceSum.setIncomeAmtAll(ArithmeticUtil.doAdd(scale, null,
                tdyBalanceSum.getIncomeAmtAll(), IncomeHappenAmt));

            tdyBalanceSum.setHappenAmt(
                ArithmeticUtil.doAdd(scale, null, tdyBalanceSum.getHappenAmt(), happenAmt));

            tdyBalanceSum.setHappenAmt(
                ArithmeticUtil.doAdd(scale, null, tdyBalanceSum.getHappenAmt(), carryAmt));
            // 计税余额 = 计税余额 + 发生额
            tdyBalanceSum.setTaxAmt(
                ArithmeticUtil.doAdd(scale, null, tdyBalanceSum.getTaxAmt(), happenAmt));
            // 计税余额 = 计税余额 + 结转发生额
            tdyBalanceSum.setTaxAmt(
                ArithmeticUtil.doAdd(scale, null, tdyBalanceSum.getTaxAmt(), carryAmt, maAmt));

            // 入账共通处理
            vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark, acctEntity,
                returnResult, DictCodeList.冲正标识_正常交易.getValue());
            // 增值税汇总入集合
            listTdyBalanceSum.add(tdyBalanceSum);
        }
        // IF (配置.增值税科目处理方式 = '冲减原收入科目' ) {
        if (ValDictCodeList.利息收入增值税科目处理方式_冲减原收入科目.getValue().equals(subjectHandleType)) {
            // 新建增值税汇总入集合
            tdyBalanceSum = vatDrawCommonNewService.createTdyBalanceSumByYst(
                ytsTaxBalanceSumMap.get(vatType + DictCodeList.增值税_确认类型_发生计税.getValue()),
                max_tdy_bookOrder, acctEntity);
            tdyBalanceSum.setVatType(vatType);
            tdyBalanceSum.setTaxPType(DictCodeList.增值税_确认类型_发生计税.getValue());
            // 如果是季初:
            if (CalendarUtil.isFirstDayByCircle(acctEntity.getHappenDate(),
                cfConfigs.get(TAX_CARRYOVER_CYCLE))) {
                // 计税收入余额 = 0 + 收入发生额
                tdyBalanceSum.setIncomeAmtAll(BigDecimal.ZERO);
                // 计税余额 = 0 + 发生额
                tdyBalanceSum.setTaxAmt(BigDecimal.ZERO);
            }
            // 循环
            for (TaxBokBalance item : listValTaxBokBalance) {
                accountAmountMap = new HashMap<>();
                // 定义 变量.内部科目编码 = 内部科目编码Map.get(余额明细.科目号)
                String 内部科目编码 = innerCodeMap.get(item.getSubjectNo());
                if (DictCodeList.计税方式_简易计税.getValue().equals(item.getCalcType())
                    && item.getVatType().equals(vatType)
                    && item.getTaxPType().equals(DictCodeList.增值税_确认类型_发生计税.getValue())) {
                    // 收入发生额 = round(增值税账务明细余额.当日计税收入发生额,2) 收入发生额
                    IncomeHappenAmt = ArithmeticUtil.round(item.getIncomeHappenAmt(),
                        NumberEnum.MONEY);

                    // 发生额 = round(增值税账务明细余额.增值税金额, 2) - round(增值税账务明细余额.当日结转累计发生额, 2)
                    // -ROUND(增值税账务明细余额.增值税金额 - 增值税账务明细余额.当日计税收入发生额 - 增值税账务明细余额.当日结转累计发生额, 2)
                    // 发生额
                    // happenAmt = round(增值税账务明细余额.增值税金额, 2)
                    happenAmt = ArithmeticUtil.round(item.getTaxAmt(), NumberEnum.MONEY);
                    // happenAmt = happenAmt - round(增值税账务明细余额.当日结转累计发生额, 2)
                    happenAmt = ArithmeticUtil.doSub(happenAmt,
                        ArithmeticUtil.round(item.getCarryAmt(), NumberEnum.MONEY));
                    // happenAmt = happenAmt - ROUND(增值税账务明细余额.增值税金额 - 增值税账务明细余额.当日发生额
                    // - 增值税账务明细余额.当日结转累计发生额,2)
                    happenAmt = ArithmeticUtil.doSub(happenAmt,
                        ArithmeticUtil.round(ArithmeticUtil.doSub(item.getTaxAmt(),
                            item.getHappenAmt(), item.getCarryAmt()), NumberEnum.MONEY));

                    // 计税收入余额 = 计税收入余额 + 收入发生额
                    tdyBalanceSum.setIncomeAmtAll(ArithmeticUtil.doAdd(scale, null,
                        tdyBalanceSum.getIncomeAmtAll(), IncomeHappenAmt));
                    // 计税发生额
                    tdyBalanceSum.setHappenAmt(ArithmeticUtil.doAdd(scale, null,
                        tdyBalanceSum.getHappenAmt(), happenAmt));
                    tdyBalanceSum.setHappenAmt(ArithmeticUtil.doAdd(scale, null,
                        tdyBalanceSum.getHappenAmt(), item.getCarryAmt()));
                    // 计税余额 = 计税余额 + 发生额 - 应交结转金额(预估时即为 当日结转累计发生额)
                    tdyBalanceSum.setTaxAmt(ArithmeticUtil.doAdd(scale, roundingMode,
                        ArithmeticUtil.doAdd(tdyBalanceSum.getTaxAmt(), happenAmt),
                        item.getCarryAmt()));
                    if (vatType.equals(DictCodeList.增值税类型_利息收入.getValue())) {
                        // TableId = DictCodeListTableId.会计场景代码_增值税估计_应交利息收入税_直接投资时.getValue())
                        //                        tableId = DictCodeListTableId.会计场景代码_增值税计提_直投_应交利息收入税.getValue();
                        //                        // KeyEnum.REMARK.getCode() = "增值税估计_应交利息收入税_直接投资时"
                        //                        remark = "增值税计提-直投-应交利息收入税";
                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                            tableId = DictCodeListTableId.会计场景代码_增值税计提_通道_应交利息收入税.getValue();
                            // KeyEnum.REMARK.getCode() = "增值税估计_应交利息收入税_直接投资时"
                            remark = "增值税计提-通道-应交利息收入税";
                        } else {
                            tableId = DictCodeListTableId.会计场景代码_增值税计提_直投_应交利息收入税.getValue();
                            // KeyEnum.REMARK.getCode() = "增值税估计_应交利息收入税_直接投资时"
                            remark = "增值税计提-直投-应交利息收入税";
                        }
                        // IF 发生额 >0
                        if (BigDecimal.ZERO.compareTo(happenAmt) < 0) {
                            // 对应KEY
                            accountAmountMap.put(BookConstant.VAT_INT_C, happenAmt);
                            accountAmountMap.put(BookConstant.VAT_INT_C+BookConstant.BCCYAMT, happenAmt);

                            // 金额元素.应交增值税-利息收入税损益（借）|| 收入科目号 = 发生额
                            accountAmountMap.put(BookConstant.VAT_INT_D + "_" + 内部科目编码, happenAmt);
                            accountAmountMap.put(BookConstant.VAT_INT_D + "_" + 内部科目编码+BookConstant.BCCYAMT, happenAmt);

                            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                                // 对应KEY
                                accountAmountMap.put(BookConstant.CHL_VAT_C, happenAmt);
                                accountAmountMap.put(BookConstant.CHL_VAT_C+BookConstant.BCCYAMT, happenAmt);

                                // 金额元素.应交增值税-利息收入税损益（借）|| 收入科目号 = 发生额
                                accountAmountMap.put(BookConstant.CHL_VAT_D + "_" + 内部科目编码, happenAmt);
                                accountAmountMap.put(BookConstant.CHL_VAT_D + "_" + 内部科目编码+BookConstant.BCCYAMT, happenAmt);
                            }
                        }
                        else {
                            accountAmountMap.put(BookConstant.VAT_INT_D, happenAmt.negate());
                            accountAmountMap.put(BookConstant.VAT_INT_D+BookConstant.BCCYAMT, happenAmt.negate());

                            // 金额元素.应交增值税-利息收入税损益（贷）|| 收入科目号 = 发生额
                            accountAmountMap.put(BookConstant.VAT_INT_C + "_" + 内部科目编码, happenAmt.negate());
                            accountAmountMap.put(BookConstant.VAT_INT_C + "_" + 内部科目编码+BookConstant.BCCYAMT, happenAmt.negate());

                            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                                accountAmountMap.put(BookConstant.CHL_VAT_D, happenAmt.negate());
                                accountAmountMap.put(BookConstant.CHL_VAT_D+BookConstant.BCCYAMT, happenAmt.negate());

                                // 金额元素.应交增值税-利息收入税损益（贷）|| 收入科目号 = 发生额
                                accountAmountMap.put(BookConstant.CHL_VAT_C + "_" + 内部科目编码, happenAmt.negate());
                                accountAmountMap.put(BookConstant.CHL_VAT_C + "_" + 内部科目编码+BookConstant.BCCYAMT, happenAmt.negate());
                            }
                        }
                    }
                    if (vatType.equals(DictCodeList.增值税类型_红利收入.getValue())) {
                        // TableId = DictCodeListTableId.会计场景代码_增值税估计_应交红利收入税_直接投资时.getValue())
                        //                        tableId = DictCodeListTableId.会计场景代码_增值税计提_直投_应交红利收入税.getValue();
                        //                        // KeyEnum.REMARK.getCode() = "增值税估计_应交红利收入税_直接投资时"
                        //                        remark = "增值税计提-直投-应交红利收入税";
                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                            tableId = DictCodeListTableId.会计场景代码_增值税计提_通道_应交红利收入税.getValue();
                            // KeyEnum.REMARK.getCode() = "增值税估计_应交红利收入税_直接投资时"
                            remark = "增值税计提-通道-应交红利收入税";
                        } else {
                            tableId = DictCodeListTableId.会计场景代码_增值税计提_直投_应交红利收入税.getValue();
                            // KeyEnum.REMARK.getCode() = "增值税估计_应交红利收入税_直接投资时"
                            remark = "增值税计提-直投-应交红利收入税";
                        }
                        // IF 发生额 >0
                        if (BigDecimal.ZERO.compareTo(happenAmt) < 0) {
                            // 金额元素.预估增值税-红利收入税（贷） = 发生额
                            accountAmountMap.put(BookConstant.VAT_BONUS_C, happenAmt);
                            accountAmountMap.put(BookConstant.VAT_BONUS_C+BookConstant.BCCYAMT, happenAmt);

                            // 金额元素.应交增值税-红利收入税损益（借）|| 收入科目号 = 发生额
                            accountAmountMap.put(
                                BookConstant.VAT_BONUS_D + "_" + 内部科目编码, happenAmt);
                            accountAmountMap.put(
                                BookConstant.VAT_BONUS_D + "_" + 内部科目编码+BookConstant.BCCYAMT, happenAmt);

                            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                                // 金额元素.预估增值税-红利收入税（贷） = 发生额
                                accountAmountMap.put(BookConstant.CHL_VAT_BONUS_C, happenAmt);
                                accountAmountMap.put(BookConstant.CHL_VAT_BONUS_C+BookConstant.BCCYAMT, happenAmt);

                                // 金额元素.应交增值税-红利收入税损益（借）|| 收入科目号 = 发生额
                                accountAmountMap.put(
                                    BookConstant.CHL_VAT_BONUS_D + "_" + 内部科目编码, happenAmt);
                                accountAmountMap.put(
                                    BookConstant.CHL_VAT_BONUS_D + "_" + 内部科目编码+BookConstant.BCCYAMT, happenAmt);
                            }
                        }
                        else {
                            // 金额元素.预估增值税-红利收入税（借） = 发生额
                            accountAmountMap.put(BookConstant.VAT_BONUS_D, happenAmt.negate());
                            accountAmountMap.put(BookConstant.VAT_BONUS_D+BookConstant.BCCYAMT, happenAmt.negate());

                            // 金额元素.应交增值税-红利收入税损益（贷）|| 收入科目号 = 发生额
                            accountAmountMap.put(
                                BookConstant.VAT_BONUS_C + "_" + 内部科目编码,happenAmt.negate());

                            accountAmountMap.put(
                                BookConstant.VAT_BONUS_C + "_" + 内部科目编码+BookConstant.BCCYAMT,
                                happenAmt.negate());

                            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                                // 金额元素.预估增值税-红利收入税（借） = 发生额
                                accountAmountMap.put(BookConstant.CHL_VAT_D, happenAmt.negate());

                                accountAmountMap.put(BookConstant.CHL_VAT_D+BookConstant.BCCYAMT, happenAmt.negate());

                                // 金额元素.应交增值税-红利收入税损益（贷）|| 收入科目号 = 发生额
                                accountAmountMap.put(
                                    BookConstant.CHL_VAT_C + "_" + 内部科目编码,happenAmt.negate());

                                accountAmountMap.put(
                                    BookConstant.CHL_VAT_C + "_" + 内部科目编码+BookConstant.BCCYAMT,
                                    happenAmt.negate());
                            }
                        }
                    }
                    // 入账共通处理
                    vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark,
                        acctEntity, returnResult, DictCodeList.冲正标识_正常交易.getValue());
                }
            }
            // 增值税汇总入集合
            listTdyBalanceSum.add(tdyBalanceSum);
        }
    }

    /**
     * 5.2.5.汇总价差收入处理
     *
     * @param acctEntity reader返回实体
     * @param returnResult 批处理返回结果集
     * @param max_tdy_bookOrder 当前最大处理序列
     * @param listTdyBalanceSum 当前增值税汇总余额List
     * @param ytsTaxBalance 昨日增值税汇总余额
     * @param amtDto 金额类Dto
     * @param cfConfigs
     * @param jcScale
     * @return 价差收入增值税汇总
     * @see
     * @since 1.0
     */
    private TaxBokBalanceSum handleBalanceSum5(PrdCashDto acctEntity,
                                               BookGenerateResult returnResult,
                                               BigDecimal max_tdy_bookOrder,
                                               List<TaxBokBalanceSum> listTdyBalanceSum,
                                               Map<String, TaxBokBalanceSum> ytsTaxBalance,
                                               TaxBalanceAmtDto amtDto,
                                               Map<String, String> cfConfigs, int jcScale) {
        // 获取昨日余额汇总
        TaxBokBalanceSum taxBokBalanceSum = ytsTaxBalance.get(
            DictCodeList.增值税类型_价差收入.getValue() + DictCodeList.增值税_确认类型_发生计税.getValue());
        // 新建增值税汇总入集合
        TaxBokBalanceSum tdyBalanceSum = vatDrawCommonNewService.createTdyBalanceSumByYst(
            taxBokBalanceSum, max_tdy_bookOrder, acctEntity);
        tdyBalanceSum.setVatType(DictCodeList.增值税类型_价差收入.getValue());
        tdyBalanceSum.setTaxPType(DictCodeList.增值税_确认类型_发生计税.getValue());
        // 如果是季初:
        if (CalendarUtil.isFirstDayByCircle(acctEntity.getHappenDate(),
            cfConfigs.get(TAX_CARRYOVER_CYCLE))) {
            // 定义 计税收入余额
            tdyBalanceSum.setIncomeAmtAll(BigDecimal.ZERO);
            // 计税余额 = 0
            tdyBalanceSum.setTaxAmt(BigDecimal.ZERO);
            // 计税发生额汇总 = min(0,计税发生额汇总)
            if (BigDecimal.ZERO.compareTo(tdyBalanceSum.getTaxAmtSum()) < 0) {
                tdyBalanceSum.setTaxAmtSum(BigDecimal.ZERO);
            }
        }
        // 如果是年初:
        if (CalendarUtil.isYearFirst(acctEntity.getHappenDate())) {
            // 计税发生额汇总 = 0
            tdyBalanceSum.setTaxAmtSum(BigDecimal.ZERO);
        }

        // 定义计税发生额汇总 = 计税发生额汇总
        BigDecimal taxAmtSum = tdyBalanceSum.getTaxAmtSum().add(amtDto.getMaqrjcAmt());
        // 定义 计税余额 = 计税余额
        BigDecimal taxAmt = tdyBalanceSum.getTaxAmt().add(amtDto.getMaqrjcAmt());
        // 定义 计税收入余额
        BigDecimal IncomeAmtAll = tdyBalanceSum.getIncomeAmtAll();

        BigDecimal happenAmt = amtDto.getQrjcHappenAmt();

        // 读取[已实现负差是否抵扣]配置
        List<BooksetTaxConfigDto> listConfig = vatDrawCommonNewService.queryBooksetTaxConfig(
            acctEntity.getBooksetId(), "已实现负差是否抵扣");
        // 是: (计税发生额汇总 + sum(当日发生额))M
        if (CollectionUtils.isEmpty(listConfig)
            || DictCodeList.是否_是.getValue().equals(listConfig.get(0).getConfigValue())) {
            BigDecimal M = ArithmeticUtil.doAdd(scale, null, taxAmtSum, happenAmt);
            // if(M>0)
            if (BigDecimal.ZERO.compareTo(M) < 0) {
                // 当日计税 = sum(当日发生额)
                tdyBalanceSum.setHappenAmt(M.setScale(2, RoundingMode.HALF_UP).subtract(taxAmt));
            }
            // if(M<0)
            if (BigDecimal.ZERO.compareTo(M) > 0) {
                // 当日计税 = - 昨日.计税金额
                tdyBalanceSum.setHappenAmt(
                    ArithmeticUtil.multiply(scale, null, taxAmt, new BigDecimal(-1)));
            }
        }
        // 否：基于sum(当日发生额(小于0设值0)) 计算税额P
        else {
            // 当日计税 = - 昨日.计税金额
            tdyBalanceSum.setHappenAmt(amtDto.getQrjcHappenAmtNo());
        }
        // 计税发生额汇总 = 昨日.计税发生额汇总 + P
        tdyBalanceSum.setTaxAmtSum(ArithmeticUtil.doAdd(jcScale, null, taxAmtSum, happenAmt));
        // 计税余额 = 昨日.计税余额 + 当日计税
        tdyBalanceSum.setTaxAmt(
            ArithmeticUtil.doAdd(scale, null, taxAmt, tdyBalanceSum.getHappenAmt()));
        // 计税收入余额 = 计税收入余额 + 收入发生额
        tdyBalanceSum.setIncomeAmtAll(
            ArithmeticUtil.doAdd(scale, null, IncomeAmtAll, amtDto.getQrjcIncomeAmtAll()));

        // TableId = DictCodeListTableId.会计场景代码_增值税确认-确认利息收入税-直接投资时.getValue())
        String tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认价差收入税.getValue();
        // KeyEnum.REMARK.getCode() = "增值税确认-确认利息收入税-直接投资时"
        String remark = "增值税确认-直投-确认价差收入税";
        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
            tableId = DictCodeListTableId.会计场景代码_增值税确认_通道_确认价差收入税.getValue();
            // KeyEnum.REMARK.getCode() = "增值税确认-确认利息收入税-直接投资时"
            remark = "增值税确认-通道-确认价差收入税";
        } else {
            tableId = DictCodeListTableId.会计场景代码_增值税确认_直投_确认价差收入税.getValue();
            // KeyEnum.REMARK.getCode() = "增值税确认-确认利息收入税-直接投资时"
            remark = "增值税确认-直投-确认价差收入税";
        }

        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 金额场景元素塞值
        // 金额元素.税额 = 发生额
        if (BigDecimal.ZERO.compareTo(tdyBalanceSum.getHappenAmt()) < 0) {
            accountAmountMap.put(BookConstant.VAT_INV_C, tdyBalanceSum.getHappenAmt());
            accountAmountMap.put(BookConstant.VAT_INV_C+BookConstant.BCCYAMT, tdyBalanceSum.getHappenAmt());

            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                accountAmountMap.put(BookConstant.CHL_VAT_C, tdyBalanceSum.getHappenAmt());
                accountAmountMap.put(BookConstant.CHL_VAT_C+BookConstant.BCCYAMT, tdyBalanceSum.getHappenAmt());
            }
        }
        else {
            accountAmountMap.put(BookConstant.VAT_INV_D, tdyBalanceSum.getHappenAmt().negate());
            accountAmountMap.put(BookConstant.VAT_INV_D+BookConstant.BCCYAMT, tdyBalanceSum.getHappenAmt().negate());

            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                accountAmountMap.put(BookConstant.CHL_VAT_D, tdyBalanceSum.getHappenAmt().negate());
                accountAmountMap.put(BookConstant.CHL_VAT_D+BookConstant.BCCYAMT, tdyBalanceSum.getHappenAmt().negate());
            }
        }

        // 入账共通处理
        vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark, acctEntity,
            returnResult, DictCodeList.冲正标识_正常交易.getValue());
        return tdyBalanceSum;
    }

    /**
     * 5.2.6.汇总公允价值变动
     *
     * @param acctEntity reader返回实体
     * @param returnResult 批处理返回结果集
     * @param max_tdy_bookOrder 当前最大处理序列
     * @param listTdyBalanceSum 当前增值税汇总余额List
     * @param ytsTaxBalances 昨日增值税汇总余额Map
     * @param priceDiffBalanceSum 今日价差收入增值税汇总
     * @param amtDto 金额类Dto
     * @param cfConfigs
     * @param jcScale
     * @see
     * @since 1.0
     */
    private void handleBalanceSum6(PrdCashDto acctEntity, BookGenerateResult returnResult,
                                   BigDecimal max_tdy_bookOrder,
                                   List<TaxBokBalanceSum> listTdyBalanceSum,
                                   Map<String, TaxBokBalanceSum> ytsTaxBalances,
                                   TaxBokBalanceSum priceDiffBalanceSum, TaxBalanceAmtDto amtDto,
                                   Map<String, String> cfConfigs, int jcScale) {
        // 获取昨日余额汇总
        TaxBokBalanceSum ytsTaxBalance = ytsTaxBalances.get(
            DictCodeList.增值税类型_价差收入.getValue() + DictCodeList.增值税_确认类型_预估计税.getValue());
        // 新建增值税汇总入集合
        TaxBokBalanceSum tdyBalanceSum = vatDrawCommonNewService.createTdyBalanceSumByYst(
            ytsTaxBalance, max_tdy_bookOrder, acctEntity);
        tdyBalanceSum.setVatType(DictCodeList.增值税类型_价差收入.getValue());
        tdyBalanceSum.setTaxPType(DictCodeList.增值税_确认类型_预估计税.getValue());
        // 定义计税发生额汇总 = 昨日.计税发生额汇总
        BigDecimal taxAmtSum = tdyBalanceSum.getTaxAmtSum().add(amtDto.getMaygjcAmt());
        // 定义 计税余额 = 昨日.计税余额
        BigDecimal taxAmt = tdyBalanceSum.getTaxAmt().add(amtDto.getMaygjcAmt());
        // 定义 计税收入余额
        BigDecimal IncomeAmtAll = tdyBalanceSum.getIncomeAmtAll();

        // 计税收入余额 = 计税收入余额 + 收入发生额
        tdyBalanceSum.setIncomeAmtAll(
            ArithmeticUtil.doAdd(scale, null, IncomeAmtAll, amtDto.getYgjcIncomeAmtAll()));

        BigDecimal happenAmt = amtDto.getYgjcHappenAmt();
        // (计税发生额汇总 + sum(当日发生额)) M
        BigDecimal M = ArithmeticUtil.doAdd(taxAmtSum, happenAmt);
        // 读取配置[未实现负差抵扣办法]
        List<BooksetTaxConfigDto> listConfig = vatDrawCommonNewService.queryBooksetTaxConfig(
            acctEntity.getBooksetId(), FLOATCALCBYDIFF);
        // if 实亏冲浮盈抵扣 = 浮亏抵扣
        if (CollectionUtils.isEmpty(listConfig)
            || "01".equals(listConfig.get(0).getConfigValue())) {
            // M=M+min(0,今日价差收入增值税汇总.计税发生额汇总)
            if (BigDecimal.ZERO.compareTo(priceDiffBalanceSum.getTaxAmtSum()) > 0) {
                // M = M +今日价差收入增值税汇总.计税发生额汇总
                M = ArithmeticUtil.doAdd(scale, null, M, priceDiffBalanceSum.getTaxAmtSum());
            }
        }
        // M > 0的场合
        if (BigDecimal.ZERO.compareTo(M) < 0) {
            // 当日计税 = M - 昨日.计税金额
            tdyBalanceSum.setHappenAmt(ArithmeticUtil.subtract(
                M.setScale(scale, RoundingMode.HALF_UP), taxAmt, scale, null));
        }
        // M < 0的场合
        if (BigDecimal.ZERO.compareTo(M) >= 0
            || BigDecimal.ZERO.compareTo(tdyBalanceSum.getIncomeAmtAll()) == 0) {
            // 当日计税 = - 昨日.计税金额
            tdyBalanceSum.setHappenAmt(
                ArithmeticUtil.multiply(scale, null, new BigDecimal(-1), taxAmt));
        }
        // 计税发生额汇总 = 昨日.计税发生额汇总 + sum(当日发生额)
        tdyBalanceSum.setTaxAmtSum(ArithmeticUtil.doAdd(jcScale, null, taxAmtSum, happenAmt));
        // 计税余额 = 昨日.计税余额 + 当日计税
        tdyBalanceSum.setTaxAmt(
            ArithmeticUtil.doAdd(scale, null, taxAmt, tdyBalanceSum.getHappenAmt()));

        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 共通元素设值 - 设置账务引擎参数
        // TableId = DictCodeListTableId.会计场景代码_增值税确认-确认利息收入税-直接投资时.getValue())
        String tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_公允价值变动损益.getValue();
        // KeyEnum.REMARK.getCode() = "增值税确认-确认利息收入税-直接投资时"
        String remark = "增值税估计-直投-公允价值变动损益";
        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
            tableId = DictCodeListTableId.会计场景代码_增值税估计_通道_公允价值变动损益.getValue();
            // KeyEnum.REMARK.getCode() = "增值税确认-确认利息收入税-直接投资时"
            remark = "增值税估计-通道-公允价值变动损益";
        } else {
            tableId = DictCodeListTableId.会计场景代码_增值税估计_直投_公允价值变动损益.getValue();
            // KeyEnum.REMARK.getCode() = "增值税确认-确认利息收入税-直接投资时"
            remark = "增值税估计-直投-公允价值变动损益";
        }
        // 金额场景元素塞值
        // IF 发生额 >0
        if (BigDecimal.ZERO.compareTo(tdyBalanceSum.getHappenAmt()) < 0) {
            // 没有对应KEY
            accountAmountMap.put(BookConstant.PI_INV_C, tdyBalanceSum.getHappenAmt());
            accountAmountMap.put(BookConstant.PI_INV_C+BookConstant.BCCYAMT, tdyBalanceSum.getHappenAmt());
            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                accountAmountMap.put(BookConstant.CHL_PI_INV_C, tdyBalanceSum.getHappenAmt());
                accountAmountMap.put(BookConstant.CHL_PI_INV_C+BookConstant.BCCYAMT, tdyBalanceSum.getHappenAmt());
            }
        }
        else {
            accountAmountMap.put(BookConstant.PI_INV_D, tdyBalanceSum.getHappenAmt().negate());
            accountAmountMap.put(BookConstant.PI_INV_D+BookConstant.BCCYAMT, tdyBalanceSum.getHappenAmt().negate());
            if (DictCodeListExt1.金融产品类型_通道.getValue().equals(acctEntity.getFinprodType())) {
                accountAmountMap.put(BookConstant.CHL_PI_INV_D, tdyBalanceSum.getHappenAmt().negate());
                accountAmountMap.put(BookConstant.CHL_PI_INV_D+BookConstant.BCCYAMT, tdyBalanceSum.getHappenAmt().negate());
            }
        }
        // 入账共通处理
        vatDrawCommonNewService.resultCommonHandle(accountAmountMap, tableId, remark, acctEntity,
            returnResult, DictCodeList.冲正标识_正常交易.getValue());
        // 增值税汇总入集合
        listTdyBalanceSum.add(tdyBalanceSum);
    }

    /**
     * 更新增值税账务明细余额Map
     *
     * @param listValTaxBokDetail 增值税账务明细List
     * @param taxBokBalanceMap 增值税账务明细余额MAP
     * @param acctEntity reader返回结果
     * @param bookOrder 处理序列
     * @param cfConfigs 方案参数配置
     * @param taxBokBalanceMem 增值税初始余额MAP
     * @see
     * @since 1.0
     */
    private void updateTaxBokBalanceMap(List<TaxBokDetail> listValTaxBokDetail,
                                        Map<ValTaxBokBalanceKeyDto, TaxBokBalance> taxBokBalanceMap,
                                        PrdCashDto acctEntity, BigDecimal bookOrder,
                                        Map<String, String> cfConfigs,
                                        Map<ValTaxBokBalanceKeyDto, TaxBokBalance> taxBokBalanceMem)
        throws InvocationTargetException, IllegalAccessException {
        // 循环增值税账务明细List
        for (TaxBokDetail detail : listValTaxBokDetail) {
            // 更新3查询出来结果,3中无对应数据的场合,新建一条余额数据.
            // 更新内容 : 增值税账务明细余额MAP
            // 定义key
            ValTaxBokBalanceKeyDto keyDto = new ValTaxBokBalanceKeyDto();
            // 账务明细代码 = 增值税账务明细.账务明细代码
            keyDto.setBokDetailId(detail.getBokDetailId());
            //分支序号 = 增值税账务明细.分支序号 add by yanya on 20191225
            keyDto.setBranch(detail.getBranch());
            // 增值税类型 = 增值税账务明细.增值税类型
            keyDto.setVatType(detail.getVatType());
            // 确认类型 = 增值税账务明细.确认类型
            keyDto.setTaxPType(detail.getTaxPType());
            // 发生场景=增值税账务明细.发生场景
            keyDto.setIncomeSource(detail.getIncomeSource());
            // 获取 增值税账务明细余额
            TaxBokBalance taxBalanceDto = taxBokBalanceMap.get(keyDto);
            if (taxBalanceDto == null) {
                taxBalanceDto = new TaxBokBalance();
                taxBalanceDto.setSeqNo(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                taxBalanceDto.setBooksetId(acctEntity.getBooksetId());
                taxBalanceDto.setBooksetDate(acctEntity.getBooksetDate());
                taxBalanceDto.setBokDetailId(detail.getBokDetailId());
                taxBalanceDto.setBranch(detail.getBranch());//分支序号 add  by yanya on 20191225
                taxBalanceDto.setHappenDate(detail.getHappenDate());
                taxBalanceDto.setBookDate(acctEntity.getBookDate());
                taxBalanceDto.setoCcy(detail.getoCcy());
                taxBalanceDto.setVatType(detail.getVatType());
                taxBalanceDto.setTaxPType(detail.getTaxPType());
                taxBalanceDto.setIncomeSource(detail.getIncomeSource());
                taxBalanceDto.setCalcType(detail.getCalcType());
            }
            taxBalanceDto.setSubjectNo(detail.getSubjectNo());
            if (DictCodeList.计税方式_简易计税.getValue().equals(detail.getCalcType())) {
                taxBalanceDto.setCalcType(detail.getCalcType());
            }
            taxBalanceDto.setBookOrder(bookOrder);
            // 更新 增值税账务明细余额
            // 计税收入余额 = 计税收入余额 + 增值税账务明细.发生含税销售额
            taxBalanceDto.setIncomeAmtAll(ArithmeticUtil.doAdd(decimalDigits, null,
                taxBalanceDto.getIncomeAmtAll(), detail.getHappenAmt()));
            // 当日计税收入发生额 = 当日计税收入发生额 + 增值税账务明细.发生含税销售额
            taxBalanceDto.setIncomeHappenAmt(ArithmeticUtil.doAdd(decimalDigits, null,
                taxBalanceDto.getIncomeHappenAmt(), detail.getHappenAmt()));
            // 当日结转累计发生额 = 当日结转应交累计发生额 + 增值税账务明细.当日结转发生额
            taxBalanceDto.setCarryAmt(ArithmeticUtil.doAdd(decimalDigits, null,
                taxBalanceDto.getCarryAmt(), detail.getCarryAmt()));
            // added by wangzhiang 【辽阳银行】增值税和托管插一分钱问题 2019-11-15 start
            // 估值增值是否按照资产维度倒减（四舍五入两位精度）计算
            if (!"MA".equals(detail.getCalcType()) && DictCodeList.增值税类型_价差收入.getValue().equals(detail.getVatType())
                && DictCodeList.增值税_确认类型_预估计税.getValue().equals(detail.getTaxPType()) && StringUtil.equals(
                DictCodeList.计提方式_区间倒减.getValue(), cfConfigs.get(TAX_ACCR_TYPE_0302_01_ASS))
                && detail.getIncomeSource() == null) {
                // 预估价差收入区间倒减
                calTaxIntervalDecrementByAsset(detail, cfConfigs, taxBokBalanceMem, keyDto, taxBalanceDto);
            }
            else if (!"MA".equals(detail.getCalcType()) && DictCodeList.增值税类型_价差收入.getValue().equals(detail.getVatType()) && StringUtil.equals(
                DictCodeList.计提方式_区间倒减.getValue(), cfConfigs.get(TAX_ACCR_TYPE_0302_ASS))) {
                // 价差收入区间倒减
                calTaxIntervalDecrementByAsset(detail, cfConfigs, taxBokBalanceMem, keyDto, taxBalanceDto);
            }
            else if (!"MA".equals(detail.getCalcType()) && DictCodeList.增值税类型_利息收入.getValue().equals(detail.getVatType()) && StringUtil.equals(
                DictCodeList.计提方式_区间倒减.getValue(), cfConfigs.get(TAX_ACCR_TYPE_0102_ASS))) {
                // 利息收入区间倒减
                calTaxIntervalDecrementByAsset(detail, cfConfigs, taxBokBalanceMem, keyDto, taxBalanceDto);
            }
            else if (!"MA".equals(detail.getCalcType()) && DictCodeList.增值税类型_红利收入.getValue().equals(detail.getVatType()) && StringUtil.equals(
                DictCodeList.计提方式_区间倒减.getValue(), cfConfigs.get(TAX_ACCR_TYPE_0202_ASS))) {
                // 红利收入区间倒减
                calTaxIntervalDecrementByAsset(detail, cfConfigs, taxBokBalanceMem, keyDto, taxBalanceDto);
            }
            else {
                // 每日新增
                // 当日发生额 = 当日发生额 + 增值税账务明细.当日计税发生额
                taxBalanceDto.setHappenAmt(
                    ArithmeticUtil.doAdd(decimalDigits, null, taxBalanceDto.getHappenAmt(), detail.getTaxAmt()));
                // 计税余额 = 计税余额 + 增值税账务明细.计税余额发生额
                taxBalanceDto.setTaxAmt(ArithmeticUtil.doAdd(decimalDigits, null,
                    taxBalanceDto.getTaxAmt(), detail.getBalAmt()));
            }
            // added by wangzhiang 【辽阳银行】增值税和托管插一分钱问题 2019-11-15 end
            taxBokBalanceMap.put(keyDto, taxBalanceDto);
        }
    }

    private void calTaxIntervalDecrementByAsset(TaxBokDetail detail, Map<String, String> cfConfigs,
                                                Map<ValTaxBokBalanceKeyDto, TaxBokBalance> taxBokBalanceMem,
                                                ValTaxBokBalanceKeyDto keyDto, TaxBokBalance taxBalanceDto)
        throws InvocationTargetException, IllegalAccessException {
        if (taxBokBalanceMem.containsKey(keyDto)) {
            // 昨日余额
            BigDecimal originTaxAmt = ArithmeticUtil.round(taxBokBalanceMem.get(keyDto).getTaxAmt(), NumberEnum.MONEY);
            // 如果是结转日，则用发生额计算，否则使用余额倒减
            if (cfConfigs.get(TAX_CARRYOVER_CYCLE_ASS) != null && CalendarUtil.isFirstDayByCircle(detail.getHappenDate(),
                cfConfigs.get(TAX_CARRYOVER_CYCLE_ASS))) {
                // 重算当日发生额 = 当日发生额/(1+税率)*税率
                BigDecimal happenAmt = vatDrawCommonNewService.taxCalculate(taxBalanceDto.getIncomeHappenAmt(),
                    detail.getTaxRate(), 2, roundingMode, cfConfigs.get(TAX_CALC_FORMULA));
                taxBalanceDto.setHappenAmt(happenAmt);
                // 当日余额 = 昨日余额 + 今日发生额
                taxBalanceDto.setTaxAmt(
                    ArithmeticUtil.doAdd(2, RoundingMode.HALF_UP, originTaxAmt, happenAmt));
            }
            else {
                // 当日计税余额
                BigDecimal taxAmt = vatDrawCommonNewService.taxCalculate(taxBalanceDto.getIncomeAmtAll(),
                    detail.getTaxRate(), 2, roundingMode, cfConfigs.get(TAX_CALC_FORMULA));
                // 当日计税发生额 = 当日价差收入余额/(1+税率)*税率 - 昨日余额
                taxBalanceDto.setHappenAmt(ArithmeticUtil.doSub(taxAmt, originTaxAmt));
                // 计税余额 = 计税余额 + 增值税账务明细.计税余额发生额
                taxBalanceDto.setTaxAmt(taxAmt);
            }
        }
        else {
            // 缓存昨日余额
            TaxBokBalance taxBalanceMem = new TaxBokBalance();
            BeanUtil.copyProperties(taxBalanceMem, taxBalanceDto);
            taxBokBalanceMem.put(keyDto, taxBalanceMem);
            // 如果是结转日，则用发生额计算，否则使用余额倒减
            if (cfConfigs.get(TAX_CARRYOVER_CYCLE_ASS) != null && CalendarUtil.isFirstDayByCircle(detail.getHappenDate(),
                cfConfigs.get(TAX_CARRYOVER_CYCLE_ASS))) {
                // 当日发生额 = 当日发生额/(1+税率)*税率
                BigDecimal happenAmt = vatDrawCommonNewService.taxCalculate(taxBalanceDto.getIncomeHappenAmt(),
                    detail.getTaxRate(), 2, roundingMode, cfConfigs.get(TAX_CALC_FORMULA));
                taxBalanceDto.setHappenAmt(happenAmt);
                taxBalanceDto.setTaxAmt(
                    ArithmeticUtil.doAdd(2, RoundingMode.HALF_UP, taxBalanceDto.getTaxAmt(), happenAmt));
            }
            else {
                // 当日计税余额
                BigDecimal taxAmt = vatDrawCommonNewService.taxCalculate(taxBalanceDto.getIncomeAmtAll(),
                    detail.getTaxRate(), 2, roundingMode, cfConfigs.get(TAX_CALC_FORMULA));
                // 昨日余额
                BigDecimal originTaxAmt = ArithmeticUtil.round(taxBalanceDto.getTaxAmt(), NumberEnum.MONEY);
                // 当日计税发生额 = 当日价差收入余额/(1+税率)*税率 - 昨日余额
                taxBalanceDto.setHappenAmt(ArithmeticUtil.doSub(taxAmt, originTaxAmt));
                // 计税余额 = 计税余额 + 增值税账务明细.计税余额发生额
                taxBalanceDto.setTaxAmt(taxAmt);
            }
        }
    }
}

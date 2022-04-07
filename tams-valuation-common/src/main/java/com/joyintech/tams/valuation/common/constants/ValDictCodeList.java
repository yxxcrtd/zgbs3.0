package com.joyintech.tams.valuation.common.constants;

import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典明细枚举
 *
 * @author chenzhuo
 * @version 1.0 2016年11月12日
 * @see ValDictCodeList
 * @since 1.0
 */
public enum ValDictCodeList implements BaseDictCodeList {
	/**
	 * 债券市价折算方式_按占比计算
	 */
	债券市价折算方式_按占比计算(
			ValDictCode.债券市价折算方式, "01", "按占比计算"
			),
	/**
	 * 债券市价折算方式_按金额计算
	 */
	债券市价折算方式_按金额计算(
	    ValDictCode.债券市价折算方式, "02", "按金额计算"
	    ),
    /**
     * 标的_信息
     */
    标的_信息(
        ValDictCode.标的, "RST_FINPRODUCT", "信息"
        ),
    /**
     * 标的_现金流计划
     */
    标的_现金流计划(
        ValDictCode.标的, "RST_CASH_PLAN", "现金流计划"
        ),
    /**
     * 标的_现金流支付规则
     */
    标的_现金流支付规则(
        ValDictCode.标的, "RST_CASH_PAYRULE", "现金流支付规则"
        ),
    /**
     * 标的_现金流计算规则
     */
    标的_现金流计算规则(
        ValDictCode.标的, "RST_CASH_CALCRULE", "现金流计算规则"
        ),
    /**
     * 标的_业绩报酬计提计划
     */
    标的_业绩报酬计提计划(
        ValDictCode.标的, "RST_PERF_REWARD_ACCRSCHE", "业绩报酬计提计划"
        ),
    /**
     * 标的_业绩报酬计提规则
     */
    标的_业绩报酬计提规则(
        ValDictCode.标的, "RST_PERF_REWARD_RULE", "业绩报酬计提规则"
        ),
    /**
     * 标的_业绩报酬计算规则
     */
    标的_业绩报酬计算规则(
        ValDictCode.标的, "RST_PERF_REWARD_CALC_RULE", "业绩报酬计算规则"
        ),
    /**
     * 标的_业绩报酬计算规则
     */
    标的_业绩回补计算规则(
        ValDictCode.标的, "RST_PERF_CAWLBACK_RULE", "业绩回补计算规则"
        ),
    /**
     * 账套配置表参数项_批处理是否全量
     */
    账套配置表参数项_批处理是否全量(
        ValDictCode.账套配置表参数项, "IS_ENTIRE", "批处理是否全量"
    ),
    /**
     * 账套配置表参数项_是否分币核算
     */
    账套配置表参数项_是否分币核算(
        ValDictCode.账套配置表参数项, "IS_DIFF_CCY", "是否分币核算"
    ),
    /**
     * 账套配置表参数项_交易凭证是否合并
     */
    账套配置表参数项_交易凭证是否合并(
        ValDictCode.账套配置表参数项, "IS_COMB_TRA_VOUC", "交易凭证是否合并"
    ),
    /**
     * 账套配置表参数项_补账凭证是否合并
     */
    账套配置表参数项_补账凭证是否合并(
        ValDictCode.账套配置表参数项, "IS_COMB_SUP_VOUC", "补账凭证是否合并"
    ),
    /**
     * 账套配置表参数项_补账分录是否合并
     */
    账套配置表参数项_补账分录是否合并(
        ValDictCode.账套配置表参数项, "IS_COMB_SUP_REC", "补账分录是否合并"
    ),
    /**
     * 账套配置表参数项_其他综合收益是否影响公允价值变动
     */
    账套配置表参数项_其他综合收益是否影响公允价值变动(
        ValDictCode.账套配置表参数项, "IS_INC_OCI", "其他综合收益是否影响公允价值变动"
    ),
    /**
     * 账套配置表参数项_分红尾差处理方式
     */
    账套配置表参数项_分红尾差处理方式(
        ValDictCode.账套配置表参数项, "BONUS_TAIL_MODE", "分红尾差处理方式"
    ),
    /**
     * 账套配置表参数项_活期存款是否计提
     */
    账套配置表参数项_活期存款是否计提(
        ValDictCode.账套配置表参数项, "DEPOSIT_ACCOUT", "活期存款是否计提"
    ),
    /**
     * 账套配置表参数项_损益结转频率
     */
    账套配置表参数项_损益结转频率(
        ValDictCode.账套配置表参数项, "CARRYOVER_CYCLE", "损益结转频率"
    ),

    /**
     * 账套配置表参数项_本期利润损益结转频率
     */
    账套配置表参数项_本期利润损益结转频率(
        ValDictCode.账套配置表参数项, "UP_CARRYOVER_CYCLE", "本期利润损益结转频率"
    ),

    /**
     * 账套配置表参数项_损益平准金结转频率
     */
    账套配置表参数项_损益平准金结转频率(
        ValDictCode.账套配置表参数项, "PAL_CARRYOVER_CYCLE", "损益平准金结转频率"
    ),
    /**
     * 账套配置表参数项_产品费用计提方式_固定管理费
     */
    账套配置表参数项_产品费用计提方式_固定管理费(
        ValDictCode.账套配置表参数项, "PRD_FEE_ACCR_TYPE_C03", "产品费用计提方式_固定管理费"
    ),
    /**
     * 账套配置表参数项_产品费用计提方式_浮动管理费
     */
    账套配置表参数项_区间内浮动管理费计提方式(
        ValDictCode.账套配置表参数项, "PRD_FEE_ACCR_TYPE_C04", "区间内浮动管理费计提方式"
    ),
    /**
     * 账套配置表参数项_
     */
    账套配置表参数项_产品费用计提方式_托管费(
        ValDictCode.账套配置表参数项, "PRD_FEE_ACCR_TYPE_C05", "产品费用计提方式_托管费"
    ),
    /**
     * 账套配置表参数项_产品费用计提方式_销售费
     */
    账套配置表参数项_产品费用计提方式_销售费(
        ValDictCode.账套配置表参数项, "PRD_FEE_ACCR_TYPE_C06", "产品费用计提方式_销售费"
    ),
    /**
     * 账套配置表参数项_产品费用计提方式_外包服务费
     */
    账套配置表参数项_产品费用计提方式_外包服务费(
        ValDictCode.账套配置表参数项, "PRD_FEE_ACCR_TYPE_C09", "产品费用计提方式_外包服务费"
    ),
    /**
     * 账套配置表参数项_产品费用计提方式_代销费
     */
    账套配置表参数项_产品费用计提方式_代销费(
        ValDictCode.账套配置表参数项, "PRD_FEE_ACCR_TYPE_C10", "产品费用计提方式_代销费"
    ),
    /**
     * 账套配置表参数项_产品费用计提方式_代销费
     */
    账套配置表参数项_产品费用计提方式_投资顾问费(
        ValDictCode.账套配置表参数项, "PRD_FEE_ACCR_TYPE_C18", "产品费用计提方式_投资顾问费"
    ),
    /**
     * 账套配置表参数项_回购计提方式_质押式
     */
    账套配置表参数项_回购计提方式_质押式(
        ValDictCode.账套配置表参数项, "REPO_ACCR_TYPE_F02_F03", "回购计提方式_质押式"
    ),
    /**
     * 账套配置表参数项_回购计提方式_买断式
     */
    账套配置表参数项_回购计提方式_买断式(
        ValDictCode.账套配置表参数项, "REPO_ACCR_TYPE_F04_F05", "回购计提方式_买断式"
    ),
    /**
     * 账套配置表参数项_拆借计提方式
     */
    账套配置表参数项_拆借计提方式(
        ValDictCode.账套配置表参数项, "DL_ACCR_TYPE", "拆借计提方式"
    ),
    /**
     * 账套配置表参数项_存款计提方式
     */
    账套配置表参数项_存款计提方式(
        ValDictCode.账套配置表参数项, "DEPO_ACCR_TYPE", "存款计提方式"
    ),
    /**
     * 账套配置表参数项_产品利息计提方式
     */
    账套配置表参数项_产品利息计提方式(
        ValDictCode.账套配置表参数项, "PRD_INT_ACCR_TYPE", "产品利息计提方式"
    ),
    /**
     * 账套配置表参数项_产品是否本系统计提
     */
    账套配置表参数项_产品是否本系统计提(
        ValDictCode.账套配置表参数项, "IS_PRD_INT_ACCR", "产品是否本系统计提"
    ),
    /**
     * 账套配置表参数项_估值表模板
     */
    账套配置表参数项_估值表模板(
        ValDictCode.账套配置表参数项, "VALUATION_TEMPLATE", "估值表模板"
    ),
    /**
     * 账套配置表参数项_回补方式
     */
    账套配置表参数项_回补方式(
        ValDictCode.账套配置表参数项, "ANAPLEROTIC_TYPE", "回补方式"
        ),
    /**
     * 账套配置表参数项_回补规则
     */
    账套配置表参数项_回补规则(
        ValDictCode.账套配置表参数项, "ANAPLEROTIC_RULE", "回补规则"
        ),
    /**
     * 账套配置表参数项_计划类利率型资产计提方式
     */
    账套配置表参数项_计划类利率型资产计提方式(
        ValDictCode.账套配置表参数项, "PLAN_ACCR_TYPE", "计划类利率型资产计提方式"
        ),
    /**
     * 账套配置表参数项_估值小数位处理方式
     */
    账套配置表参数项_估值小数位处理方式(
        ValDictCode.账套配置表参数项, "VAL_DEC_TYPE", "估值小数位处理方式"
    ),
    /**
     * 账套配置表参数项_估值小数位位数
     */
    账套配置表参数项_估值小数位位数(
        ValDictCode.账套配置表参数项, "VAL_DEC_DIGITS", "估值小数位位数"
    ),
    /**
     * 账套配置表参数项_收益率小数位处理方式
     */
    账套配置表参数项_收益率小数位处理方式(
        ValDictCode.账套配置表参数项, "YEILD_DEC_TYPE", "收益率小数位处理方式"
    ),
    /**
     * 账套配置表参数项_收益率小数位位数
     */
    账套配置表参数项_收益率小数位位数(
        ValDictCode.账套配置表参数项, "YEILD_DEC_DIGITS", "收益率小数位位数"
    ),
    /**
     * 账套配置表参数项_万份收益小数位位数
     */
    账套配置表参数项_万份收益小数位位数(
        ValDictCode.账套配置表参数项, "PYEILD_DEC_DIGITS", "万份收益小数位位数"
    ),
    /**
     * 账套配置表参数项_万份收益小数位尾数处理
     */
    账套配置表参数项_万份收益小数位尾数处理(
        ValDictCode.账套配置表参数项, "PYEILD_DEC_TYPE", "万份收益小数位尾数处理"
    ),
    /**
     * 账套配置表参数项_份额小数位位数
     */
    账套配置表参数项_份额小数位位数(
        ValDictCode.账套配置表参数项, "SHARE_DEC_DIGITS", "份额小数位位数"
    ),
    /**
     * 账套配置表参数项_份额小数位尾数处理
     */
    账套配置表参数项_份额小数位尾数处理(
        ValDictCode.账套配置表参数项, "SHARE_DEC_TYPE", "份额小数位尾数处理"
    ),
    /**
     * 账套配置表参数项_买断式回购费用处理方式
     */
    账套配置表参数项_收益类型(
        ValDictCode.账套配置表参数项, "PROFIT_TYPE", "收益类型"
    ),
    /**
     * 账套配置表参数项_银行间质押式回购费用处理方式
     */
    账套配置表参数项_银行间质押式回购费用处理方式(
        ValDictCode.账套配置表参数项, "IB_C_REP_FEE_EXE_TYPE", "银行间质押式回购费用处理方式"
    ),
    /**
     * 账套配置表参数项_交易所质押式回购费用处理方式
     */
    账套配置表参数项_交易所质押式回购费用处理方式(
        ValDictCode.账套配置表参数项, "TRA_C_REP_FEE_EXE_TYPE", "交易所质押式回购费用处理方式"
    ),
    /**
     * 账套配置表参数项_银行间买断式回购费用处理方式
     */
    账套配置表参数项_银行间买断式回购费用处理方式(
        ValDictCode.账套配置表参数项, "IB_O_REP_FEE_EXE_TYPE", "银行间买断式回购费用处理方式"
    ),
    /**
     * 账套配置表参数项_交易所买断式回购费用处理方式
     */
    账套配置表参数项_交易所买断式回购费用处理方式(
        ValDictCode.账套配置表参数项, "TRA_O_REP_FEE_EXE_TYPE", "交易所买断式回购费用处理方式"
    ),
    /**
     * 账套配置表参数项_债券交易费用损益计入方式
     */
    账套配置表参数项_债券交易费用损益计入方式(
        ValDictCode.账套配置表参数项, "SEC_DEAL_FEE_SUBJECT", "债券交易费用损益计入方式"
    ),
    /**
     * 账套配置表参数项_债券借贷计提方式
     */
    账套配置表参数项_债券借贷计提方式(
        ValDictCode.账套配置表参数项, "SECLEND_ACCR_TYPE", "债券借贷计提方式"
    ),
    /**
     * 账套配置表参数项_万份收益尾数处理
     */
    账套配置表参数项_万份收益尾数处理(ValDictCode.账套配置表参数项, "PYEILD_DEC_TYPE", "万份收益尾数处理"),

    /**
     * 账套配置表参数项_债券借贷计提方式
     */
    账套配置表参数项_计提份额占比收息(
            ValDictCode.账套配置表参数项, "INT_SHARE_CALC_TYPE", "计提份额占比收息"
    ),


    /**
     * 账套配置表参数项_交易所结算模式
     */
    账套配置表参数项_交易所结算模式(ValDictCode.账套配置表参数项, "SETTLEMENT_MODEL", "交易所结算模式"),

    /**
     * 账套配置表参数项_待摊费用计提方式
     */
    账套配置表参数项_待摊费用计提方式(ValDictCode.账套配置表参数项, "UNAMO_ACCR_TYPE", "待摊费用计提方式"),


    /**
     * 账套配置表参数项_债券公允价值变动计算过程小数位
     */
    账套配置表参数项_债券公允价值变动计算过程小数位(
        ValDictCode.账套配置表参数项, "SEC_FAIRVALUE_DIGITS", "债券公允价值变动计算过程小数位"
    ),


    /**
     * 费前费后区分_费前
     */
    费前费后区分_费前(
        ValDictCode.费前费后区分, "01", "费前"
    ),
    /**
     * 费前费后区分_费后
     */
    费前费后区分_费后(
        ValDictCode.费前费后区分, "02", "费后"
    ),
    /**
     * 排序类型_升序
     */
    排序类型_升序(ValDictCode.排序类型, "ASC", "排序类型_升序"),
    /**
     * 排序类型_降序
     */
    排序类型_降序(ValDictCode.排序类型, "DESC", "排序类型_降序"),
    /**
     * 债券类资产_A_A
     */
    计息基础_债券类资产_A_A(
        ValDictCode.计息基础_债券类资产, "A/A", "A/A"
    ),
    /**
     * 债券类资产_A_360
     */
    计息基础_债券类资产_A_360(
        ValDictCode.计息基础_债券类资产, "A/360", "A/360"
    ),
    /**
     * 债券类资产_A_365
     */
    计息基础_债券类资产_A_365(
        ValDictCode.计息基础_债券类资产, "A/365", "A/365"
    ),
    /**
     * 债券类资产_A_365F
     */
    计息基础_债券类资产_A_365F(
        ValDictCode.计息基础_债券类资产, "A/365F", "A/365F"
    ),
    /**
     * 债券类资产_30_360
     */
    计息基础_债券类资产_30_360(
        ValDictCode.计息基础_债券类资产, "30/360", "30/360"
    ),

    /**
     * 债券类资产_30U_360
     */
    计息基础_债券类资产_30U_360(
        ValDictCode.计息基础_债券类资产, "30U/360", "30U/360"
    ),
       /**
        * 利息收入增值税科目处理方式_使用收入备抵科目
        */
       利息收入增值税科目处理方式_使用收入备抵科目(
            ValDictCode.利息收入增值税科目处理方式, "01", "使用收入备抵科目"
            ),
       /**
        * 利息收入增值税科目处理方式_冲减原收入科目
        */
       利息收入增值税科目处理方式_冲减原收入科目(
           ValDictCode.利息收入增值税科目处理方式, "02", "冲减原收入科目"
           ),
       /**
        * 账务处理_1
        */
       账务处理_1(
    		   ValDictCode.账务处理, "1", "账务处理1"
       ),
       /**
        * 账务处理_2
        */
       账务处理_2(
    		   ValDictCode.账务处理, "2", "账务处理2"
       ),
       /**
        * 计提规则_区间累计
        */
       计提规则_区间累计(
    		   ValDictCode.计提规则, "1", "区间累计"
       ),
       /**
        * 计提规则_每日计提
        */
       计提规则_每日计提(
    		   ValDictCode.计提规则, "2", "每日计提"
       ),


    /**
     * 账套配置表参数项_浮动管理费_估值小数位
     */
    账套配置表参数项_浮动管理费_估值小数位(
            ValDictCode.账套配置表参数项, "FLOAT_DEC_DIGITS", "浮动管理费_估值小数位"
    ),
    /**
     * 账套配置表参数项_浮动管理费_估值小数尾数处理方式
     */
    账套配置表参数项_浮动管理费_估值小数尾数处理方式(
            ValDictCode.账套配置表参数项, "FLOAT_DEC_TYPE", "浮动管理费_估值小数尾数处理方式"
    ),
    /**
     * 账套配置表参数项_浮动管理费_周期年化收益率小数位数
     */
    账套配置表参数项_浮动管理费_周期年化收益率小数位数(
            ValDictCode.账套配置表参数项, "FLOAT_CYCLYEAR_YIELD_DIGITS", "浮动管理费_周期年化收益率小数位数"
    ),
    /**
     * 账套配置表参数项_浮动管理费_周期年化收益率位数处理方式
     */
    账套配置表参数项_浮动管理费_周期年化收益率位数处理方式(
            ValDictCode.账套配置表参数项, "FLOAT_CYCLYEAR_YIELD_TYPE", "浮动管理费_周期年化收益率位数处理方式"
    ),
    /**
     * 账套配置表参数项_浮动管理费_期末累计净值小数位数
     */
    账套配置表参数项_浮动管理费_期末累计净值小数位数(
            ValDictCode.账套配置表参数项, "FLOAT_ENDTOTAL_NET_DIGITS", "浮动管理费_期末累计净值小数位数"
    ),
    /**
     * 账套配置表参数项_浮动管理费_期末累计净值尾数处理方式
     */
    账套配置表参数项_浮动管理费_期末累计净值尾数处理方式(
            ValDictCode.账套配置表参数项, "FLOAT_ENDTOTAL_NET_TYPE", "浮动管理费_浮动管理费_期末累计净值尾数处理方式"
    ),
    /**
     * 账套配置表参数项_浮动管理费_期初累计净值小数位数
     */
    账套配置表参数项_浮动管理费_期初累计净值小数位数(
            ValDictCode.账套配置表参数项, "FLOAT_STARTTOTAL_NET_DIGITS", "浮动管理费_期初累计净值小数位数"
    ),
    /**
     * 账套配置表参数项_浮动管理费_期初累计净值尾数处理方式
     */
    账套配置表参数项_浮动管理费_期初累计净值尾数处理方式(
            ValDictCode.账套配置表参数项, "FLOAT_STARTTOTAL_NET_TYPE", "浮动管理费_期初累计净值尾数处理方式"
    ),
    /**
     * 账套配置表参数项_浮动管理费_期初单位净值小数位数
     */
    账套配置表参数项_浮动管理费_期初单位净值小数位数(
            ValDictCode.账套配置表参数项, "FLOAT_STARTTOTAL_UNITNET_DIGITS", "浮动管理费_期初单位净值尾数处理方式"
    ),
    /**
     * 账套配置表参数项_浮动管理费_期初单位净值尾数处理方式
     */
    账套配置表参数项_浮动管理费_期初单位净值尾数处理方式(
            ValDictCode.账套配置表参数项, "FLOAT_STARTTOTAL_UNITNET_TYPE", "浮动管理费_期初单位净值尾数处理方式"
    ),
    /**
     * 账套配置表参数项_产品费后估值小数位数_估值表展示
     */
    账套配置表参数项_产品费后估值小数位数_估值表展示(
            ValDictCode.账套配置表参数项, "SHOW_AFTER_DEC_DIGITS", "产品费后估值小数位数_估值表展示"
    ),
    /**
     * 账套配置表参数项_产品费后估值小数尾数处理方式_估值表展示
     */
    账套配置表参数项_产品费后估值小数尾数处理方式_估值表展示(
            ValDictCode.账套配置表参数项, "SHOW_AFTER_DEC_TYPE", "产品费后估值小数尾数处理方式_估值表展示"
    ),
    /**
     * 账套配置表参数项_产品费前估值小数位数_估值表展示
     */
    账套配置表参数项_产品费前估值小数位数_估值表展示(
            ValDictCode.账套配置表参数项, "SHOW_BEFORE_DEC_DIGITS", "产品费前估值小数位数_估值表展示"
    ),
    /**
     * 账套配置表参数项_产品费前估值小数尾数处理方式_估值表展示
     */
    账套配置表参数项_产品费前估值小数尾数处理方式_估值表展示(
            ValDictCode.账套配置表参数项, "SHOW_BEFORE_DEC_TYPE", "产品费前估值小数尾数处理方式_估值表展示"
    ),
    /**
     * 账套配置表参数项_周期年化收益率小数位数_估值表展示
     */
    账套配置表参数项_周期年化收益率小数位数_估值表展示(
            ValDictCode.账套配置表参数项, "SHOW_CYCLYEAR_YIELD_DIGITS", "周期年化收益率小数位数_估值表展示"
    ),
    /**
     * 账套配置表参数项_周期年化收益率小数尾数处理方式_估值表展示
     */
    账套配置表参数项_周期年化收益率小数尾数处理方式_估值表展示(
            ValDictCode.账套配置表参数项, "SHOW_CYCLYEAR_YIELD_TYPE", "周期年化收益率小数尾数处理方式_估值表展示"
    ),



    /**
     * 账套配置表参数项_累计单位净值小数位数_估值表展示
     */
    账套配置表参数项_累计单位净值小数位数_估值表展示(
    		ValDictCode.账套配置表参数项, "SHOW_TOTBOUNS_DEC_DIGITS", "累计单位净值小数位数_估值表展示"
    ),
    /**
     * 账套配置表参数项_累计单位净值小数尾数_估值表展示
     */
    账套配置表参数项_累计单位净值小数尾数_估值表展示(
    		ValDictCode.账套配置表参数项, "SHOW_TOTBOUNS_DEC_TYPE", "累计单位净值小数尾数_估值表展示"
    ),
    /**
     * 账套配置表参数项_累积万份收益小数位数_估值表展示
     */
    账套配置表参数项_累积万份收益小数位数_估值表展示(
    		ValDictCode.账套配置表参数项, "SHOW_AFTER_DEC_SUM_DIGITS", "累积万份收益小数位数_估值表展示"
    ),
    /**
     * 账套配置表参数项_累积万份收益小数尾数_估值表展示
     */
    账套配置表参数项_累积万份收益小数尾数_估值表展示(
    		ValDictCode.账套配置表参数项, "SHOW_AFTER_DEC_SUM_TYPE", "累积万份收益小数尾数_估值表展示"
    ),

    /**
     * 账套配置表参数项_本日年化收益率小数位数_估值表展示
     */
    账套配置表参数项_本日年化收益率小数位数_估值表展示(
    		ValDictCode.账套配置表参数项, "TDY_YEILD_DEC_DIGITS", "本日年化收益率小数位数_估值表展示"
    ),
    /**
     * 账套配置表参数项_本日年化收益率小数尾数_估值表展示
     */
    账套配置表参数项_本日年化收益率小数尾数_估值表展示(
    		ValDictCode.账套配置表参数项, "TDY_YEILD_DEC_TYPE", "本日年化收益率小数尾数_估值表展示"
    ),


    /**
     * 账套配置表参数项_7日年化收益率小数位数_估值表展示
     */
    账套配置表参数项_7日年化收益率小数位数_估值表展示(
    		ValDictCode.账套配置表参数项, "DAY7_YEILD_DEC_DIGITS", "7日年化收益率小数位数_估值表展示"
    ),
    /**
     * 账套配置表参数项_7日年化收益率小数尾数_估值表展示
     */
    账套配置表参数项_7日年化收益率小数尾数_估值表展示(
    		ValDictCode.账套配置表参数项, "DAY7_YEILD_DEC_TYPE", "7日年化收益率小数尾数_估值表展示"
    ),
    /**
     * 账套配置表参数项_货币型产品自动红利再投
     */
    账套配置表参数项_货币型产品自动红利再投(
            ValDictCode.账套配置表参数项, "VAL_CPRD_AUTO_BONUS_TYPE", "货币型产品自动红利再投"
    ),
    /**
     * 账套配置表参数项_汇兑损益结转频率
     */
    账套配置表参数项_汇兑损益结转频率(
            ValDictCode.账套配置表参数项, "EXCHANGE_CYCLE", "汇兑损益结转频率"
    ),

    /**
     * 账套配置表参数项_昨日单位净值_估值日期类型
     */
    账套配置表参数项_昨日单位净值_估值日期类型(
            ValDictCode.账套配置表参数项, "VALUATION_DATETYPE", "昨日单位净值_估值日期类型"
    ),
    /**
     * 账套配置表参数项_同业借款四级科目类型
     */
    账套配置表参数项_同业借款四级科目类型(
            ValDictCode.账套配置表参数项, "TYJK_FORSUBJECT", "同业借款四级科目类型"
    ),

    /**
     * 账套配置表参数项_币种折算小数位数
     */
    账套配置表参数项_币种折算小数位数(
        ValDictCode.账套配置表参数项, "CURR_CONV_DEC_DIGITS", "币种折算小数位数"
    ),
    /**
     * 账套配置表参数项_币种折算尾数处理方式
     */
    账套配置表参数项_币种折算尾数处理方式(
        ValDictCode.账套配置表参数项, "CURR_CONV_DEC_TYPE", "币种折算尾数处理方式"
    ),
    /**
     * 账套配置表参数项_本币余额汇总方式
     */
    账套配置表参数项_本币余额汇总方式(
        ValDictCode.账套配置表参数项, "B_AMT_SUMMARY_TYPE", "本币余额汇总方式"
    ),
    /**
     * 账套配置表参数项_当日计税计提基准_金融商品转让_预估
     */
    账套配置表参数项_当日计税计提基准_金融商品转让_预估(
        ValDictCode.账套配置表参数项, "TAX_ACCR_BASE_TYPE_JRSPZR_YG", "当日计税计提基准_金融商品转让_预估"
    ),

    /**
     * 账套配置表参数项_证券类型计税方式_金融商品转让_预估
     */
    账套配置表参数项_证券类型计税方式_金融商品转让_预估(
        ValDictCode.账套配置表参数项, "TAX_ASSET_TYPE_JRSPZR_YG", "证券类型计税方式_金融商品转让_预估"
    ),
    /**
     * 账套配置表参数项_当日计税计提基准_贷款业务收入_预估
     */
    账套配置表参数项_当日计税计提基准_贷款业务收入_预估(
        ValDictCode.账套配置表参数项, "TAX_ACCR_BASE_TYPE_DKYWSR_YG", "当日计税计提基准_贷款业务收入_预估"
    ),
    /**
     * 账套配置表参数项_证券类型计税方式_贷款业务收入_预估
     */
    账套配置表参数项_证券类型计税方式_贷款业务收入_预估(
        ValDictCode.账套配置表参数项, "TAX_ASSET_TYPE_DKYWSR_YG", "证券类型计税方式_贷款业务收入_预估"
    ),
    /**
     * 账套配置表参数项_当日计税计提基准_金融商品转让_发生
     */
    账套配置表参数项_当日计税计提基准_金融商品转让_发生(
        ValDictCode.账套配置表参数项, "TAX_ACCR_BASE_TYPE_JRSPZR_FS", "当日计税计提基准_金融商品转让_发生"
    ),

    /**
     * 账套配置表参数项_证券类型计税方式_金融商品转让_发生
     */
    账套配置表参数项_证券类型计税方式_金融商品转让_发生(
        ValDictCode.账套配置表参数项, "TAX_ASSET_TYPE_JRSPZR_FS", "证券类型计税方式_金融商品转让_发生"
    ),
    /**
     * 账套配置表参数项_当日计税计提基准_贷款业务收入_发生
     */
    账套配置表参数项_当日计税计提基准_贷款业务收入_发生(
        ValDictCode.账套配置表参数项, "TAX_ACCR_BASE_TYPE_DKYWSR_FS", "当日计税计提基准_贷款业务收入_发生"
    ),
    /**
     * 账套配置表参数项_证券类型计税方式_贷款业务收入_发生
     */
    账套配置表参数项_证券类型计税方式_贷款业务收入_发生(
        ValDictCode.账套配置表参数项, "TAX_ASSET_TYPE_DKYWSR_FS", "证券类型计税方式_贷款业务收入_发生"
    ),
    /**
     * 账套配置表参数项_减值测算频率
     */
    账套配置表参数项_减值测算频率(
        ValDictCode.账套配置表参数项, "LOSS_TEST_RATE", "减值测算频率"
    ),
    /**
     * 账套配置表参数项_减值测算日
     */
    账套配置表参数项_减值测算日(
        ValDictCode.账套配置表参数项, "LOSS_TEST_DATE", "减值测算日"
    ),
    /**
     * 账套配置表参数项_触发减值交易类型
     */
    账套配置表参数项_触发减值交易类型(
        ValDictCode.账套配置表参数项, "LOSS_TEST_TREAD", "触发减值交易类型"
    ),
    /**
     * 账套配置表参数项_资产到期之后是否计提减值
     */
    账套配置表参数项_资产到期之后是否计提减值(
        ValDictCode.账套配置表参数项, "AFT_EXP_IS_CAL", "资产到期之后是否计提减值"
    ),
    /**
     * 账套配置表参数项_还本是否冲减摊销
     */
    账套配置表参数项_还本是否冲减摊销(
        ValDictCode.账套配置表参数项, "REDAMOFLAG", "还本是否冲减摊销"
    ),
    /**
     * 账套配置表参数项_还本是否冲减摊销
     */
    账套配置表参数项_摊销额小于0是否只摊到0(
            ValDictCode.账套配置表参数项, "AMORTIZE_RULE", "摊销额小于剩余摊销时是否只摊销到0', '摊销额小于剩余摊销时是否只摊销到0"
    ),
    /**
     * 计税计提基准_发生额
     */
    计税计提基准_发生额(ValDictCode.计税计提基准, "01", "发生额"),
    /**
     * 计税计提基准_余额
     */
    计税计提基准_余额(ValDictCode.计税计提基准, "02", "余额"),
    /**
     * 证券类型计税方式_单资产
     */
    证券类型计税方式_单资产(ValDictCode.证券类型计税方式, "01", "单资产"),
    /**
     * 证券类型计税方式_证券类型
     */
    证券类型计税方式_证券类型(ValDictCode.证券类型计税方式, "02", "证券类型"),
    /**
     * 证券类型计税方式_全资产
     */
    证券类型计税方式_全资产(ValDictCode.证券类型计税方式, "03", "全资产"),
    /**
     * 估值表向上汇总方式_全部
     */
    估值表向上汇总方式_全部(ValDictCode.估值表向上汇总方式, "11", "全部"),
    /**
     * 估值表向上汇总方式_仅成本
     */
    估值表向上汇总方式_仅成本(ValDictCode.估值表向上汇总方式, "10", "仅成本"),
    /**
     * 估值表向上汇总方式_仅市值
     */
    估值表向上汇总方式_仅市值(ValDictCode.估值表向上汇总方式, "01", "仅市值"),

    /**
     * 是否启用倒起息_不启用
     */
    是否启用倒起息_不启用(ValDictCode.是否启用倒起息, "01", "不启用"),
    /**
     * 是否启用倒起息_启用
     */
    是否启用倒起息_启用(ValDictCode.是否启用倒起息, "02", "启用"),

    /**
     * 是否子账套_是
     */
    是否子账套_是(
            ValDictCode.是否子账套, "01", "是否子账套_是"
    ),
    /**
     * 是否子账套_否
     */
    是否子账套_否(
            ValDictCode.是否子账套, "02", "是否子账套_否"
    ),
    /**
     * 估值日期类型-上一自然日
     */
    估值日期类型_上一自然日(
            ValDictCode.账套配置表参数项, "01", "估值日期类型-上一自然日"
    ),
    /**
     * 估值日期类型-上一自然日
     */
    估值日期类型_上一工作日(
        ValDictCode.账套配置表参数项, "02", "估值日期类型-上一工作日"
        ),

    /**
     * 估值日期类型-上一自然日
     * @author zhangJin
     *
     */
    周期型产品浮动管理费是否进行理财账务处理(
        ValDictCode.账套配置表参数项, "IS_CYCLY_PRD_DEAL_ACCOUNT", "周期型产品浮动管理费是否进行理财账务处理"
        ),

    /**
     * 账套配置表参数项_债券借贷费用损益计入方式
     * add  by yanya on 20200122
     */
    账套配置表参数项_债券借贷费用损益计入方式(
            ValDictCode.账套配置表参数项, "BOND_LENDING_DEAL_FEE_SUBJECT", "债券借贷费用损益计入方式"
    ),

    /**
     * 账套配置表参数项_债权类资产公允价值变动计算过程小数位
     * add by wujie on 20201015 remark:计划类收益型资产改造
     */
    账套配置表参数项_债权类资产公允价值变动计算过程小数位(
        ValDictCode.账套配置表参数项, "DELTRIGHT_FAIRVALUE_DIGITS", "债权类资产公允价值变动计算过程小数位"
    ),

    /**
     * 账套配置表参数项_本位币差额处理方式
     */
    账套配置表参数项_本位币差额处理方式(
        ValDictCode.账套配置表参数项, "BCCYADJ_TYPE", "本位币差额处理方式"
    ),

    /**
     * 账套配置表参数项_还本收息冲抵减值准备处理日
     */
    账套配置表参数项_还本收息冲抵减值准备处理日(
        ValDictCode.账套配置表参数项, "OFFSETIMPT_DATETYPE", "还本收息冲抵减值准备处理日"
    ),
    /**
     * 账套配置表参数项_卖出冲抵减值准备处理方式
     */
    账套配置表参数项_卖出冲抵减值准备处理方式(
        ValDictCode.账套配置表参数项, "OFFSETIMPT_SHELL_TYPE", "卖出冲抵减值准备处理方式"
    ),

    /**
     * 账套配置表参数项_收息冲抵减值准备处理方式
     */
    账套配置表参数项_收息冲抵减值准备处理方式(
        ValDictCode.账套配置表参数项, "OFFSETIMPT_SX_TYPE", "收息冲抵减值准备处理方式"
    ),

    /**
     * 账套配置表参数项_收息是否冲抵减值准备
     */
    账套配置表参数项_收息是否冲抵减值准备(
        ValDictCode.账套配置表参数项, "IS_OFFSETIMPT_SX", "收息是否冲抵减值准备"
    ),
    /**
     * 账套配置表参数项_减值资产第三阶段利息计提摊销差额计入科目
     */
    账套配置表参数项_减值资产第三阶段利息计提摊销差额计入科目(
        ValDictCode.账套配置表参数项, "LOSS_P3_SECACCR_SUBJECT_NO", "减值资产第三阶段利息计提摊销差额计入科目"
    ),
    /**
     * 账套配置表参数项_减值资产第三阶段是否重新计算实际利率
     */
    账套配置表参数项_减值资产第三阶段是否重新计算实际利率(
        ValDictCode.账套配置表参数项, "LOSS_P3_SECACCR_YEILD_RECALC", "减值资产第三阶段是否重新计算实际利率"
    ),
    /** add by yanya on 20210309 增加行权相关的账套参数**/
    /**
     * 公允价值变动是否启用行权价
     */
    账套配置表参数项_公允价值变动是否启用行权价(
            ValDictCode.账套配置表参数项, "EXEC_PRICE_CAL_TYPE", "公允价值变动是否启用行权价"
    ),
    账套配置表参数项_行权回售申请交易处理(
            ValDictCode.账套配置表参数项, "EXEC_RESALEAPPLY_DEAL", "行权回售申请交易处理"
    ),
    账套配置表参数项_预提费用计提方式(
            ValDictCode.账套配置表参数项, "TOCAL_ACCR_TYPE", "预提费用计提方式"
    ),
    /**
     * 账套配置表参数项_计划类利率型资产计提方式
     */
    账套配置表参数项_项目类计提区间倒减方式(
            ValDictCode.账套配置表参数项, "PLAN_SUBTRACTION_TYPE", "项目类计提区间倒减方式"
    ),
    /**
     * 账套配置表参数项_其他综合收益计算的平准金科目
     */
    账套配置表参数项_其他综合收益计算的平准金科目(
        ValDictCode.账套配置表参数项, "OTHER_INCOME_PZJ_SUBJECT", "净值型产品认申赎场景其他综合收益计算的平准金科目"
    ),
    ;
    /**
     * 对应于数据字典code
     */
    private String value;

    /**
     * 数据字典枚举
     */
    private ValDictCode dictCode;

    /**
     * 明细名称
     */
    private String codeName;

    ValDictCodeList(ValDictCode dictCode, String value, String codeName) {
        this.dictCode = dictCode;
        this.value = value;
        this.setCodeName(codeName);
    }

    /**
     * 获得数据字典详细类
     *
     * @param value 数据字典值
     * @param dictCode 数据字典DictCode
     * @return DictCodeList
     * @see
     * @since 1.0
     */
    public static ValDictCodeList from(ValDictCode dictCode, String value) {
        ValDictCodeList[] values = ValDictCodeList.values();
        for (ValDictCodeList code : values) {
            if (StringUtil.equals(value, code.getValue()) && code.getDictCode() == dictCode) {
                return code;
            }
        }
        return null;
    }

    /**
     * 获取数据字典明细List
     *
     * @param dictCode DictCode
     * @return List&lt;SelectOptionDto&gt;
     * @see
     * @since 1.0
     */
    public static List<SelectOptionDto> fromDictCode(ValDictCode dictCode) {
        List<SelectOptionDto> result = new ArrayList<>();
        ValDictCodeList[] values = ValDictCodeList.values();
        for (ValDictCodeList code : values) {
            if (code.getDictCode() == dictCode) {
                SelectOptionDto option = new SelectOptionDto();
                option.setId(code.getValue());
                option.setText(code.getCodeName());
                result.add(option);
            }
        }
        return result;
    }

    /**
     * 获取数据字典明细valueList
     *
     * @param dictCode DictCode
     * @return List&lt;SelectOptionDto&gt;
     * @see
     * @since 1.0
     */
    public static List<String> valuesfromDictCode(ValDictCode dictCode) {
        List<String> result = new ArrayList<>();
        ValDictCodeList[] values = ValDictCodeList.values();
        for (ValDictCodeList code : values) {
            if (code.getDictCode() == dictCode) {
                result.add(code.getValue());
            }
        }
        return result;
    }

    /**
     * 获取 DictCode
     *
     * @return value.
     */
    @Override
    public ValDictCode getDictCode() {
        return dictCode;
    }

    /**
     * 获取 value
     *
     * @return value.
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * 设置 value
     *
     * @param value value
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取 codeName
     *
     * @return codeName.
     */
    @Override
    public String getCodeName() {
        return codeName;
    }

    /**
     * 获取 带有引号的名称
     *
     * @return 带有引号的名称.
     */
    public String getQuotesCodeName() {
        return "“" + codeName + "”";
    }

    /**
     * 获取Integer value
     *
     * @return Integer value
     */
    public Integer getIntegerCodeName() {
        Integer result = null;
        try {
            result = Integer.parseInt(codeName);
        }
        catch (Exception e) {
        }
        return result;
    }

    /**
     * 获取Integer value
     *
     * @return Integer value
     */
    public BigDecimal getBigDecimalCodeName() {
        BigDecimal result = null;
        try {
            result = new BigDecimal(codeName);
        }
        catch (Exception e) {
        }
        return result;
    }

    /**
     * 设置 codeName
     *
     * @param codeName codeName
     */
    @Override
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
}

/*
 * 文件名：DictCodeBaseSubjectNatrue.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.constants;

import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.util.StringUtil;

/**
 * 基础科目性质数据字典枚举
 *
 * @author 王之昂
 * @version 1.0 2016年10月19日
 * @see DictCodeBaseSubjectNatrueNew
 * @since 1.0
 */
public enum DictCodeBaseSubjectNatrueNew implements IDictCode {

    /**
     * 公允价值损益类_成本
     */
    公允价值损益类_成本(
        "GYJZSYL_CB"
    ),
    /**
     * 公允价值损益类_公允价值变动
     */
    公允价值损益类_公允价值变动(
        "GYJZSYL_GYJZBD"
    ),
    /**
     * 综合收益类成本
     */
    综合收益类_成本(
        "ZHSYL_CB"
    ),
    /**
     * 综合收益类利息调整
     */
    综合收益类_利息调整(
        "ZHSYL_LXTZ"
    ),
    /**
     * 综合收益类_公允价值变动
     */
    综合收益类_公允价值变动(
        "ZHSYL_GYJZBD"
    ),

    /**
     * 逾期资产应计利息
     */
    逾期资产应计利息 (
            "YQZCYJLX"
    ),

    /**
     * 逾期资产利息收入
     */
    逾期资产利息收入 (
            "YQZCLXSR"
    ),
    /**
     * 实收资本
     */
    实收资本 (
            "SSZB_Z"
    ),
    /**
     * 其他综合收益
     */
    其他综合收益_证券(
            "QTZHSY_ZQ"
    ),

    /**
     * 损益平准金_已实现
     */
    损益平准金_已实现(
            "SYPZJ_YSX"
    ),
    /**
     * 损益平准金_未实现
     */
    损益平准金_未实现(
            "SYPZJ_WSX"
    ),
    /**
     * 本期利润_已实现
     */
    本期利润_已实现(
            "BQLR_YSX"
    ),
    /**
     * 本期利润_未实现
     */
    本期利润_未实现(
            "BQLR_WSX"
    ),
    /**
     * 利润分配_未分配利润_已实现
     */
    利润分配_未分配利润_已实现(
            "WFPLR_YSX"
    ),
    /**
     * 利润分配_未分配利润_未实现
     */
    利润分配_未分配利润_未实现(
            "WFPLR_WSX"
    ),
    /**
     * 其他应付款_本金
     */
    其他应付款_本金(
        "QTYFK_BJ"
    ),
    /**
     * 其他应付款_利息
     */
    其他应付款_利息(
        "QTYFK_LX"
    ),

    其他应收款期权费 (
        "QTYSK_QQF"
    ),

    其他应付款期权费 (
        "QTYFK_QQF"
    ),
    /** add by weipeng on 20220113 估值系统产品3.0.4批处理科目性质改造新增科目性质 start **/
    /** ==========原科目性质修改======== start**/
    /**
     * 活期存款（现金）
     */
    活期存款_本金(
            "HQCK_BJ"
    ),
    /**
     * 活期存款_应计利息
     */
    活期存款_应计利息(
            "HQCK_YJLX"
    ),
    /**
     * 定期存款_本金（成本）
     */
    定期存款_本金(
            "DQCK_BJ"
    ),
    /**
     * 定期存款-减值准备(存放同业减值准备)
     */
    定期存款_减值准备(
            "ZFTYJZZB"
    ),
    /**
     * 应收利息_证券（其他应收款_利息）
     */
    应收利息_证券(
            "YSLX_ZQ"
    ),
    /**
     * 应收利息_资金（其他应收款_利息）
     */
    应收利息_资金(
            "YSLX_ZJ"
    ),
    /**
     * 其他应收款_利息（应收逾期款_利息）
     */
    应收逾期款_利息(
            "YSYQKLX"
    ),
    /**
     * 摊余成本类成本
     */
    摊余成本类_成本(
            "TYCBLCB"
    ),
    /**
     * 摊余成本类利息调整
     */
    摊余成本类_利息调整(
            "TYCBLLXTZ"
    ),
    /**
     * 利息收入-AC类-证券
     */
    利息收入_AC类_证券(
            "LXSR_AC_ZQ"
    ),
    摊余成本类_减值准备(
            "TYCBLTZ_JZZB"
    ),
    其他综合收益_减值准备(
            "QTZHSY_JZZB"
    ),
    /**
     * 摊余成本类_应计利息
     */
    摊余成本类_应计利息(
            "TYCBLZQYJLX"
    ),
    /**
     * 公允价值损益类_应计利息
     */
    公允价值损益类_应计利息(
            "GYJZSYLYJLX"
    ),
    /**
     * 综合收益类_应计利息
     */
    综合收益类_应计利息(
            "ZHSYL_YJLX"
    ),
    /**
     * 应付交易费用_费用
     */
    应付交易费用_费用(
            "YFJYFY"
    ),
    /**
     * 应付交易费用_佣金
     */
    应付交易费用_佣金(
            "YFYJ"
    ),
    /**
     * 清算资金往来
     */
    清算资金往来(
            "QSZJWL"
    ),
    /**
     * 应收股利(应收红利)
     */
    应收股利(
            "YSGL"
    ),
    /**
     * 运营费用
     */
    运营费用(
            "YYFY"
    ),
    /**
     * 应付运营费
     */
    应付运营费(
            "YFYYF_Z"
    ),
    /**
     * 待摊费用
     */
    待摊费用(
            "DTFY_Z"
    ),
    /**
     * 应交税费_应交增值税_价差
     */
    应交税费_应交增值税_利息(
            "LXSRS"
    ),
    /**
     * 应交税费_应交增值税_价差
     */
    应交税费_应交增值税_价差(
            "TZSYS"
    ),
    /**
     * 应交税费_应交增值税_红利
     */
    应交税费_应交增值税_红利(
            "HLSRS"
    ),
    /**
     * 应交税费_预估销项税_利息
     */
    应交税费_预估销项税_利息(
            "YGLXSRS"
    ),
    /**
     * 应交税费_预估销项税_价差
     */
    应交税费_预估销项税_价差(
            "TZSYS"
    ),
    /**
     * 应交税费_预估销项税_红利
     */
    应交税费_预估销项税_红利(
            "YGHLSRS"
    ),

    /**
     * 应交税费_应交附加税
     */
    应交税费_应交附加税(
            "YJZZSFJCJS"
    ),
    /**
     * 应交税费_预估附加税
     */
    应交税费_预估附加税(
            "YGCJSFJ"
    ),
    /**
     * 公允价值变动损益_增值税备抵
     */
    公允价值变动损益_增值税备抵(
            "GYJZBDSY_ZZSBD"
    ),
    /**
     * 公允价值变动损益_附加税备抵
     */
    公允价值变动损益_附加税备抵(
            "GYJZBDSY_FJSBD"
    ),
    /**
     * 利息收入_增值税备抵
     */
    利息收入_增值税备抵(
            "LXSR_ZZSBD"
    ),
    /**
     * 投资收益_利息收入_增值税备抵
     */
    投资收益_利息收入_增值税备抵(
            "TZSY_LXSR_ZZSBD"
    ),
    /**
     * 投资收益_价差收入_增值税备抵
     */
    投资收益_价差收入_增值税备抵(
            "TZSY_JCSR_ZZSBD"
    ),
    /**
     * 投资收益_股利收入_增值税备抵
     */
    投资收益_股利收入_增值税备抵(
            "TZSY_GLSR_ZZSBD"
    ),
    /**
     * 交易性金融负债_成本
     */
    交易性金融负债_成本(
        "JRFZCB"
    ),
    /**
     * 交易性金融负债_应计利息
     */
    交易性金融负债_应计利息(
        "JYXJRFZ_YJLX"
    ),
    /**
     * 交易性金融负债_公允价值变动
     */
    交易性金融负债_公允价值变动(
        "JYXJRFZ_GYJZBD"
    ),
    /** ==========原科目性质修改======== end**/
    /** ==========新增科目性质======== start**/
    /**
     * 定期存款_应计利息
     */
    定期存款_应计利息(
            "DQCK_YJLX"
    ),
    /**
     * 结算备付金_本金（现金）
     */
    结算备付金_本金(
            "JSBFJ_BJ"
    ),
    /**
     * 结算备付金_应计利息
     */
    结算备付金_应计利息(
            "JSBFJ_YJLX"
    ),
    /**
     * 存出保证金_本金（现金）
     */
    存出保证金_本金(
            "CCBZJ_BJ"
    ),
    /**
     * 存出保证金_应计利息
     */
    存出保证金_应计利息(
            "CCBZJ_YJLX"
    ),

    其他综合收益_申赎(
            "QTZHSY_SH"
    ),
    利润分配_应付利润(
            "LRFP_YFLR"
    ),
    应付利润(
            "YFLR_Z"
    ),
    其他应付款_其他(
            "QTYFK"
    ),
    卖出回购金融资产_成本(
            "MCHGJRZC_CB"
    ),
    卖出回购金融资产_应计利息(
            "MCHGJRZC_YJLX"
    ),
    买入返售金融资产_成本(
            "MRFSJRZC_CB"
    ),
    买入返售金融资产_应计利息(
            "MRFSJRZC_YJLX"
    ),
    买入返售金融资产_减值准备(
        "MRFSJRZC_JZZB_Z"
    ),
    应收利息_融出(
        "YSLX_RC"
    ),
    利息收入_融出(
        "LXSR_RC"
    ),
    应付利息_融入(
        "YFLX_RR"
    ),
    利息收入_融入(
        "LXSR_RR"
    ),
    期收债券本金(
        "QSZQBJ"
    ),
    期付债券本金(
        "QFZQBJ"
    ),

    /**
     * 公允价值损益类_公允价值变动
     */
    公允价值损益类_利息调整(
        "GYJZSYL_LXTZ"
    ),

    /**
     * 损益平准金_未实现
     */
    损益平准金_其他综合收益(
        "SYPZJ_QTZHSY"
    ),
    /** ==========新增科目性质======== end**/
    /** add by weipeng on 20220113 估值系统产品3.0.4批处理科目性质改造 end **/
    ;

    /**
     * 对应于数据字典code
     */
    private String value;

    DictCodeBaseSubjectNatrueNew(String value) {
        this.value = value;
    }

    /**
     * @param value 数据字典值
     * @return DictCode
     * @see
     * @since 1.0
     */
    public static DictCodeBaseSubjectNatrueNew get(String value) {
        DictCodeBaseSubjectNatrueNew[] values = DictCodeBaseSubjectNatrueNew.values();
        for (DictCodeBaseSubjectNatrueNew code : values) {
            if (StringUtil.equals(value, code.getValue())) {
                return code;
            }
        }
        return null;
    }

    /**
     * 获取 value
     *
     * @return value.
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置 value
     *
     * @param value value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 通过value生成枚举
     *
     * @param value value值
     * @return DictCode
     */
    public static DictCodeBaseSubjectNatrueNew fromValue(String value) {
        for (DictCodeBaseSubjectNatrueNew dictCode : DictCodeBaseSubjectNatrueNew.values()) {
            if (dictCode.getValue().equals(value)) {
                return dictCode;
            }
        }
        return null;
    }
}

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
        "ZHSYLCB"
    ),
    /**
     * 综合收益类利息调整
     */
    综合收益类_利息调整(
        "ZHSYLLXTZ"
    ),
    /**
     * 综合收益类_公允价值变动
     */
    综合收益类_公允价值变动(
        "ZHSYL_GYJZBD"
    ),
//    /**
//     * 基金投资_成本
//     */
//    基金投资_成本(
//        "JJTZ_CB"
//    ),
//    /**
//     * 基金投资_估值增值
//     */
//    基金投资_估值增值(
//        "JJTZ_GZZZ"
//    ),
    /**
     * 计划类资产_成本
     */
    计划类资产_成本(
        "JHLZC_CB"
    ),
    /**
     * 计划类资产_公允价值变动
     */
    计划类资产_公允价值变动(
        "JHLZC_GYJZBD"
    ),
    /**
     * 公允价值变动
     */
    公允价值变动(
        "GYJZBD"
    ),
    /**
     * 非上市债券成本
     */
    非上市债券成本(
        "FSSZQCB"
    ),

    /**
     * 应收债券借贷费用
     */
    应收债券借贷费用(
        "YSZQJDFY"
    ),
    /**
     * 应收债券借贷借出费
     */
    应收债券借贷借出费(
        "YSZQJDJCF"
    ),
    /**
     * 非上市债券应收利息
     */
    非上市债券应收利息(
        "FSSZQYSLX"
    ),
    /**
     * 应收申购款
     */
    应收申购款(
        "YSSGK"
    ),
    /**
     * 其他应收款
     */
    其他应收款(
        "QTYSK"
    ),

    /**
     * 资产减值
     */
    资产减值(
        "ZCJZ"
    ),
    /**
     * 应收逾期款_本金
     */
    应收逾期款_本金(
        "YSYQK_BJ"
    ),
    /**
     * 待摊费用
     */
    待摊费用(
        "DTFY"
    ),
    /**
     * 应付赎回款
     */
    应付赎回款(
        "YFSHK"
    ),
    /**
     * 应付赎回费
     */
    应付赎回费(
        "YFSHF"
    ),
    /**
     * 应付投顾费
     */
    应付投顾费(
        "YFTGF"
    ),
    /**
     * 应付代销费
     */
    应付代销费(
        "YFDXF"
    ),
    /**
     * 应付外包服务费
     */
    应付外包服务费(
        "YFWBFWF"
    ),
    /**
     * 应付托管费
     */
    应付托管费(
        "YFTGF1"
    ),
    /**
     * 应付销售服务费
     */
    应付销售服务费(
        "YFXSFWF"
    ),
    /**
     * 应付印花税
     */
    应付印花税(
        "YFYHS"
    ),
    /**
     * 应付经手费
     */
    应付经手费(
        "YFJSF"
    ),
    /**
     * 应付证管费
     */
    应付证管费(
        "YFZGF"
    ),
    /**
     * 应付结算费
     */
    应付结算费(
        "YFJSF1"
    ),
    /**
     * 应付交易手续费
     */
    应付交易手续费(
        "YFJYSXF"
    ),
    /**
     * 应付结算手续费
     */
    应付结算手续费(
        "YFJSSXF"
    ),
    /**
     * 合作方
     */
    合作方(
        "HZF"
    ),
    /**
     * 应交增值税附加
     */
    应交增值税附加(
        "YJZZSFJ"
    ),
    /**
     * 预估利息收入税
     */
    预估利息收入税(
        "YGLXSRS"
    ),
    /**
     * 预估红利收入税
     */
    预估红利收入税(
        "YGHLSRS"
    ),
    /**
     * 预估投资收益税
     */
    预估投资收益税(
        "YGTZSYS"
    ),
    /**
     * 预估附加税
     */
    预估附加税(
        "YGFJS"
    ),
    /**
     * 利息收入税
     */
    利息收入税(
        "LXSRS"
    ),
    /**
     * 红利收入税
     */
    红利收入税(
        "HLSRS"
    ),
    /**
     * 投资收益税
     */
    投资收益税(
        "TZSYS"
    ),
    /**
     * 已交税金
     */
    已交税金(
        "YJSJ"
    ),
    /**
     * 减免税款
     */
    减免税款(
        "JMSK"
    ),
    /**
     * 应付债券借贷借入费
     */
    应付债券借贷借入费(
        "YFZQJDJRF"
    ),
    /**
     * 应付利润
     */
    应付利润(
        "YFLR"
    ),
    /**
     * 待清算资金
     */
    待清算资金(
        "DQSZJ"
    ),
    /**
     * 其他应付款
     */
    其他应付款(
        "QTYFK"
    ),
    /**
     * 预提费用
     */
    预提费用(
        "YTFY"
    ),
    /**
     * 外汇远期头寸
     */
    外汇远期头寸(
        "WHYQTC"
    ),
    /**
     * 外汇远期评价
     */
    外汇远期评价(
        "WHYQPJ"
    ),
    /**
     * 外汇即期头寸
     */
    外汇即期头寸(
        "WHJQTC"
    ),
    /**
     * 外汇即期评价
     */
    外汇即期评价(
        "WHJQPJ"
    ),
    /**
     * 外汇远期结售汇头寸
     */
    外汇远期结售汇头寸(
        "WHYQJSHTC"
    ),
    /**
     * 外汇远期结售汇评价
     */
    外汇远期结售汇评价(
        "WHYQJSHPJ"
    ),
    /**
     * 外汇即期结售汇头寸
     */
    外汇即期结售汇头寸(
        "WHJQJSHTC"
    ),
    /**
     * 外汇即期结售汇评价
     */
    外汇即期结售汇评价(
        "WHJQJSHPJ"
    ),
    /**
     * 外汇掉期头寸
     */
    外汇掉期头寸(
        "WHDQTC"
    ),
    /**
     * 外汇掉期评价
     */
    外汇掉期评价(
        "WHDQPJ"
    ),
    /**
     * 外汇掉期
     */
    外汇掉期(
        "WHDQ"
    ),
    /**
     * 实收资本
     */
    实收资本(
        "SSZB"
    ),
    /**
     * 资本公积
     */
    资本公积(
        "ZBGJ"
    ),
    /**
     * 其他综合收益
     */
    其他综合收益(
        "QTZHSY"
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
     * 利润分配_应付利润
     */
    利润分配_应付利润(
        "LRFP_YFLR"
    ),
    /**
     * 待分配利润
     */
    待分配利润(
        "DFPLR"
    ),
    /**
     * 未分配利润_已实现
     */
    未分配利润_已实现(
        "WFPLR_YSX"
    ),
    /**
     * 未分配利润_未实现
     */
    未分配利润_未实现(
        "WFPLR_WSX"
    ),
    /**
     * 利息收入
     */
    利息收入(
        "LXSR"
    ),
    /**
     * 公允价值损益类利息收入
     */
    公允价值损益类利息收入(
        "GYJZSYLLXSR"
    ),
    /**
     * 综合收益类利息收入
     */
    综合收益类利息收入(
        "ZHSYLLXSR"
    ),
    /**
     * 基金类利息收入
     */
    基金类利息收入(
        "JJLLXSR"
    ),
    /**
     * 红利收入
     */
    红利收入(
        "HLSR"
    ),
    /**
     * 损益类利息收入
     */
    损益类利息收入(
        "SYLLXSR"
    ),
    /**
     * 债券借贷借出费收入
     */
    债券借贷借出费收入(
        "ZQJDJCFSR"
    ),
    /**
     * 公允价值变动损益
     */
    公允价值变动损益(
        "GYJZBDSY"
    ),
    /**
     * 利率掉期公允价值变动损益
     */
    利率掉期公允价值变动损益(
        "LLDQGYJZBDSY"
    ),
    /**
     * 外汇掉期公允价值变动损益
     */
    外汇掉期公允价值变动损益(
        "WHDQGYJZBDSY"
    ),
    /**
     * 外汇远期公允价值变动损益
     */
    外汇远期公允价值变动损益(
        "WHYQGYJZBDSY"
    ),
    /**
     * 外汇即期公允价值变动损益
     */
    外汇即期公允价值变动损益(
        "WHJQGYJZBDSY"
    ),
    /**
     * 投资收益
     */
    投资收益(
        "TZSY"
    ),
    /**
     * 摊余成本类投资收益
     */
    摊余成本类投资收益(
        "TYCBLTZSY"
    ),
    /**
     * 公允价值损益类投资收益
     */
    公允价值损益类投资收益(
        "GYJZSYLTZSY"
    ),
    /**
     * 综合收益类投资收益
     */
    综合收益类投资收益(
        "ZHSYLTZSY"
    ),
    /**
     * 利率掉期投资收益
     */
    利率掉期投资收益(
        "LLDQTZSY"
    ),
    /**
     * 外汇掉期投资收益
     */
    外汇掉期投资收益(
        "WHDQTZSY"
    ),
    /**
     * 投资收益增值税备抵
     */
    投资收益增值税备抵(
        "TZSYZZSBD"
    ),
    /**
     * 汇兑损益
     */
    汇兑损益(
        "HDSY"
    ),
    /**
     * 外汇即期汇兑损益
     */
    外汇即期汇兑损益(
        "WHJQHDSY"
    ),
    /**
     * 外汇远期汇兑损益
     */
    外汇远期汇兑损益(
        "WHYQHDSY"
    ),
    /**
     * 其他收入
     */
    其他收入(
        "QTSR"
    ),
    /**
     * 管理人报酬
     */
    管理人报酬(
        "GLRBC"
    ),
    /**
     * 超额管理费
     */
    超额管理费(
        "CEGLF"
    ),
    /**
     * 托管费
     */
    托管费(
        "TGF"
    ),
    /**
     * 销售服务费
     */
    销售服务费(
        "XSFWF"
    ),
    /**
     * 交易费用
     */
    交易费用(
        "JYFY"
    ),
    /**
     * 印花税支出
     */
    印花税支出(
        "YHSZC"
    ),
    /**
     * 经手费支出
     */
    经手费支出(
        "JSFZC"
    ),
    /**
     * 证管费支出
     */
    证管费支出(
        "ZGFZC"
    ),
    /**
     * 结算费支出
     */
    结算费支出(
        "JSFZC1"
    ),
    /**
     * 佣金支出
     */
    佣金支出(
        "YJZC"
    ),
    /**
     * 交易手续费支出
     */
    交易手续费支出(
        "JYSXFZC"
    ),
    /**
     * 结算手续费支出
     */
    结算手续费支出(
        "JSSXFZC"
    ),
    /**
     * 通道费用
     */
    通道费用(
        "TDFY"
    ),
    /**
     * 城建税支出
     */
    城建税支出(
        "CJSZC"
    ),
    /**
     * 教育税支出
     */
    教育税支出(
        "JYSZC"
    ),
    /**
     * 地方教育税支出
     */
    地方教育税支出(
        "DFJYSZC"
    ),
    /**
     * 利息支出
     */
    利息支出(
        "LXZC"
    ),
    /**
     * 债券借贷借入费支出
     */
    债券借贷借入费支出(
        "ZQJDJRFZC"
    ),
    /**
     * 资产减值损失
     */
    资产减值损失(
        "ZCJZSS"
    ),
    /**
     * 摊余成本类资产减值损失
     */
    摊余成本类资产减值损失(
        "TYCBLZCJZSS"
    ),
    /**
     * 可供出售资产减值损失
     */
    可供出售资产减值损失(
        "KGCSZCJZSS"
    ),
    /**
     * 计划类资产减值损失
     */
    计划类资产减值损失(
        "JHLZCJZSS"
    ),
    /**
     * 存放款项减值损失
     */
    存放款项减值损失(
        "CFKXJZSS"
    ),
    /**
     * 拆放款项减值损失
     */
    拆放款项减值损失(
        "CFKXJZSS1"
    ),
    /**
     * 其他支出
     */
    其他支出(
        "QTZC"
    ),
    /**
     * 以前年度损益调整
     */
    以前年度损益调整(
        "YQNDSYTZ"
    ),
    /**
     * 以前年度损益调整
     */
    计划类资产_利息调整(
        "JHLZC_LXTZ"
    ),
    /**
     * 公允价值损益类-计划类资产-成本
     */
    公允价值损益类_计划类资产_成本(
        "GYJZSYL_JHLZC_CB"
    ),
    /**
     * 公允价值损益类-计划类资产-公允价值变动
     */
    公允价值损益类_计划类资产_公允价值变动(
        "GYJZSYL_JHLZC_LXTZ"
    ),
//    /**
//     * 应收红利
//     */
//    应收红利(
//        "YSHL"
//    ),
    /**
     * 表外科目
     */
    表外科目(
        "BWKM"
    ),
    /**
     * 应付中债登结算手续费
     */
    应付中债登结算手续费(
        "YFZZDJSSXF"
    ),
    /**
     * 应付上清所结算手续费
     */
    应付上清所结算手续费(
        "YFSQSJSSXF"
    ),
    /**
     * 应付中登结算手续费
     */
    应付中登结算手续费(
        "YFZDJSSXF"
    ),
    应付中债登交易手续费(
            "YFZZDJYSXF"
    ),
    应付上清所交易手续费(
            "YFSQSJYSXF"
    ),

    /**
     * 应付中债登账户维护费
     */
    应付中债登账户维护费(
            "YFZZDZHWHF"
    ),
    /**
     * 应付上清所账户维护费
     */
    应付上清所账户维护费(
            "YFSQSZHWHF"
    ),
    /**
     * 应付账户维护费（暂时没用到，考虑那种上游给不到登记托管机构的情况下使用）
     */
    应付账户维护费(
            "YFZHWHF"
    ),
    /**
     * 金融负债成本
     */
    金融负债成本(
        "JRFZCB"
        ),
    /**
     * 债券借贷成本
     */
    债券借贷成本(
        "ZQJDCB"
    ),
    /**
     * 其他应付款-利息
     */
    其他应付款_利息(
        "QTYFK_LX"
    ),
    /**
     * 其他应付款-本金
     */
    其他应付款_本金(
        "QTYFK_BJ"
        ),

//    /**
//     * 计划类（货币型）资产-成本
//     */
//    计划类_货币型资产_成本(
//        "JHLHBX_CB"
//        ),
    /**
     * 计划类（净值型）资产-成本
     */
    计划类_净值型资产_成本(
        "JHLJZX_CB"
        ),

    /**
     * 外包服务费
     */
    外包服务费(
            "WBFWF"
    ),
    超额管理费报酬 (
            "CEGLFBC"
    ),
    /**
     * 股票-公允价值变动
     */
    股票_公允价值变动 (
            "GP_GYJZBD"
    ),
    /**
     * 股票-成本
     */
    股票_成本 (
            "GP_CB"
    ),

//    /**
//     * 逾期资产应计利息
//     */
//    逾期资产应计利息 (
//            "YQZCYJLX"
//    ),

    /**
     * 逾期资产利息收入
     */
    逾期资产利息收入 (
            "YQZCLXSR"
    ),

    买入期权成本 (
        "MRQQ_CB"
    ),

    买入期权公允价值变动 (
        "MRQQ_GYJZBD"
    ),

    卖出期权成本 (
        "MCQQ_CB"
    ),

    卖出期权公允价值变动 (
        "MCQQ_GYJZBD"
    ),

    其他应收款期权费 (
        "QTYSK_QQF"
    ),

    其他应付款期权费 (
        "QTYFK_QQF"
    ),

    应交增值税附加_城建税 (
        "YJZZSFJCJS"
    ),

    应交增值税附加_教育税附加 (
        "YJZZSFJJYSFJ"
    ),

    应交增值税附加_地方教育税附加 (
        "YJZZSFJDFJYSFJ"
    ),

    应交增值税附加_水利基金税附加 (
        "YJZZSFJSLJJSFJ"
    ),

    合作方利息收入税(
        "HZFLXSRS"
    ),

    合作方红利收入税(
        "HZFHLSRS"
    ),

    合作方投资收益税(
        "HZFTZSYS"
    ),

    合作方应交增值税附加_城建税 (
        "HZFYJZZSFJCJS"
    ),

    合作方应交增值税附加_教育费附加 (
    	"HZFYJZZSFJJYFFJ"
    ),

    合作方应交增值税附加_地方教育费附加 (
        "HZFYJZZSFJDFJYFFJ"
    ),

    合作方应交增值税附加_水利基金附加 (
        "HZFYJZZSFJSLJJFJ"
    ),

    合作方城建税支出(
        "HZFCJSZC"
    ),

    合作方教育税支出(
        "HZFJYSZC"
    ),

    合作方地方教育税支出(
        "HZFDFJYSZC"
    ),

    合作方水利基金支出(
        "HZFSLJJZC"
    ),
    拆放同业减值准备(
            "CFTYJZZB"
    ),

    /**add by wujie on 20201015 start remark:计划类收益型资产改造**/
	综合收益类计划类收益型资产_成本(
        "JHLZC_ZHSYL_CB"
    ),
	综合收益类计划类收益型资产_利息调整(
	        "JHLZC_ZHSYL_LXTZ"
	    ),
	综合收益类计划类收益型资产_公允价值变动(
	        "JHLZC_ZHSYL_GYJZBD"
	    ),
	综合收益类计划类收益型资产_应计利息(
	        "JHLZC_ZHSYL_YJLX"
	    ),

	公允价值类计划类收益型资产_成本(
	        "JHLZC_GYJZSYL_CB"
	    ),
	公允价值类计划类收益型资产_公允价值变动(
	        "JHLZC_GYJZSYL_GYJZBD"
	    ),
	公允价值类类计划类收益型资产_应计利息(
	        "JHLZC_GYJZSYL_YJLX"
	    ),
    /**add by wujie on 20201015 end**/
    固定管理费(
            "GDGLF"
    ),
    浮动管理费(
            "FDGLF"
    ),
    管理费(
            "CEGLF"
    ),
    /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 start**/
    买入返售减值损失(
            "MRFSJZSS"
    ),
    /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 end**/

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
    其他应收款_利息(
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
    摊余成本类_资产减值(
            "TYCBLTZ_JZZB_Z"
    ),
    其他综合收益_资产减值(
            "QTZHSYJZ"
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
            "ZHSYLYJLX"
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
            "QSKZJWL_Z"
    ),
    /**
     * 应收股利(应收红利)
     */
    应收股利(
            "YSGL_Z"
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
            "YFYYF Z"
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

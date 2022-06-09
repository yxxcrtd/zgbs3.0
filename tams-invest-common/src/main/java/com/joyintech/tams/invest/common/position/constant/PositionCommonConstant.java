/*
 * 文件名：BussinessConstant.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.position.constant;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 *
 * @author yflv
 * @version 1.0 2022年2月16日
 * @see PositionCommonConstant
 * @since 1.0
 */
public class PositionCommonConstant {
    
    public static final String INTERFACE_TYPE = "interfaceType";

    /**
     * 指令头寸类型
     */
    public static final String POSITION_TYPE_ETR = "ETR";
    /**
     * 交易头寸类型
     */
    public static final String POSITION_TYPE_TRD = "TRD";
    /**
     * 交易端画面
     */
    public static final String POSITION_TRD_PAGE = "TRD_PAGE";
    /**
     * 指令端画面
     */
    public static final String POSITION_ETR_PAGE = "ETR_PAGE";
    /**
     * 银行间现券
     */
    public static final String INTERF_TYPE_BOND001 = "Bond001";
    /**
     * 场内现券
     */
    public static final String INTERF_TYPE_BOND002 = "Bond002";
    /**
     * 场内可转债
     */
    public static final String INTERF_TYPE_BOND003 = "Bond003";
    /**
     * 基金--LOF基金
     */
    public static final String INTERF_TYPE_FUND001 = "Fund001";
    /**
     * 基金--封闭式基金
     */
    public static final String INTERF_TYPE_FUND002 = "Fund002";
    /**
     * 质押式双边回购--竞价回购
     */
    public static final String INTERF_TYPE_REPO001 = "REPO001";
    /**
     * 质押式协议回购--协议回购
     */
    public static final String INTERF_TYPE_REPO002 = "REPO002";
    /**
     * 质押式通用回购
     */
    public static final String INTERF_TYPE_REPO003 = "REPO003";
    /**
     * 股票（A股）
     */
    public static final String INTERF_TYPE_STOCK001 = "Stock001";
    /**
     * 信用保护凭证
     */
    public static final String INTERF_TYPE_CERTIFICATE001 = "Certificate001";

    /**
     * 信用保护凭证
     */
    public static final String INTERF_TYPE_CPC001 = "CPC001";
    /**
     * 业务类型
     */
    public static final String BUSI_TYPE = "busiType";
    /**
     * 业务动作
     */
    public static final String BUSI_ACT = "busiAct";
    /**
     * 金融产品代码
     */
    public static final String FINPROD_ID = "finprodId";

    public static final String BID_RESULT = "bidResult";
    /**
     * 交易方向
     */;
    public static final String PS = "ps";
    /**
     * 清算速度
     */
    public static final String SETTLE_SPEED = "settleSpeed";
    /**
     * 交易场所
     */
    public static final String TRADE_PLACE = "tradePlace";

    /**
     * 申报方式
     */
    public static final String PURCH_MODLE = "purchModle";

    /**
     * 交易系统
     */
    public static final String SHARES_TRADE_PLATFORM = "sharesTradePlatform";

    /**
     * 结算方式
     */
    public static final String DELIVERY_TYPE = "deliveryType";
    /**
     * 币种
     */
    public static final String CCY = "ccy";
    /**
     * 组合数据集
     */
    public static final String PTL_LIST = "ptlList";
    /**
     *组合数附属据集
     */
    public static final String PTL_SUB_LIST = "ptlSubList";
    /**
     * 组合现金数据集
     */
    public static final String PTL_CASH_LIST = "ptlCashList";

    /**
     * 轧差现金数据集
     */
    public static final String PTL_OFFSET_CASH_LIST = "ptlOffsetCashList";

    /**
     * 质押券数据集
     */
    public static final String PTL_SEC_LIST = "ptlSecList";

    /**
     * 解质押券数据集
     */
    public static final String PTL_RELIEVE_SEC_LIST = "ptlRelieveSecList";
    /**
     * 组合配股/新股申购数据集
     */
    public static final String PTL_RO_LIST = "ptlRoList";

    /**
     * 股票持仓数据集
     */
    public static final String SHARES_SEC_LIST = "sharesSecList";

    /**
     * 配债申购数据集
     */
    public static final String APPLY_BOND_SEC_LIST = "applyBondSecList";

    /**
     * 投资目的
     */
    public static final String INV_AIM = "invAim";
    /**
     * 流程编号
     */
    public static final String WORK_FLOW_ID = "workFlowId";
    /**
     * 当日回转
     */
    public static final String SAME_DAY_TURN = "sameDayTurn";
    /**
     * 下单委托编号
     */
    public static final String ORD_ENTRUST_ID = "ordEntrustId";
    /**
     * 下单编号
     */
    public static final String ORD_ID = "ordId";
    /**
     * 标准券代码
     */
    public static final String STANDARD_SEC_ID = "standardSecId";
    /**
     * 标准券总额
     */
    public static final String STANDARD_SEC_AMOUNT = "standardSecAmount";
    /**
     * 系统标准券代码标识
     */
    public static final String STANDAR_SEC_ID = "XN_PRODID999";

    /**
     * 汇总交易编号
     */
    public static final String TOTAL_TRADE_ID = "totalTradeId";

    /**
     * 前次交易编号
     */
    public static final String B_TOTAL_TRADE_ID = "bTotalTradeId";

    /**
     * ETF申赎方式
     */
    public static final String ETFREDEMPTION_METHOD = "ETFRedemptionMethod";
    /**
     * 融入券券面总额
     */
    public static final String INTEGRATE_SEC_AMOUNT = "integrateSecAmount";
    /**
     * 本方券券面总额
     */
    public static final String OWN_SEC_AMOUNT = "ownSecAmount";

    /**
     * 指令券面总额
     */
    public static final String ETR_SEC_AMOUNT="etrSecAmount";


}

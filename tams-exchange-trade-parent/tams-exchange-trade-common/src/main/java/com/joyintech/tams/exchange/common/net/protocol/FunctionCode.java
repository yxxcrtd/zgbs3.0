package com.joyintech.tams.exchange.common.net.protocol;

/**
 * Created by yking on 2018/5/9 0009. God bless you!
 */
public enum FunctionCode {

    /*** 基础协议 ***/
    HEARTBEAT("心跳", 1),
    ACK("确认消息", 2),
    /*** req terminal trade <--------- center ***/
    TERMINAL_LOGIN("终端登录", 3),
    REQ_ACCOUNT_LOGIN("同步新建终端连接", 4),
    REQ_ACCOUNT_LOGOUT("管理终端登录", 5),
    SERVICE_NODE("交易节点信息通知", 6),
    REQ_ACCOUNT_ENTRUST_REISSUE("账号委托通知补发申请", 11),
    REQ_ACCOUNT_TRADE_REISSUE("账号成交通知补发申请", 12),

    REQ_QRY_FINANCE("查询帐号详情", 101),
    REQ_QRY_POSITION("查询持仓详情", 102),
    REQ_QRY_POSITION_STATICS("查询持仓明细(期货)", 103),
    REQ_QRY_TRADE("查询成交", 104),
    REQ_QRY_ENTRUST("查询订单", 105),

    REQ_ENTRUST("下单", 106),
    REQ_CANCEL_ENTRUST("撤单", 107),
    REQ_INTEGRATED_ENTRUST("综合下单", 108),
    REQ_CANCEL_INTEGRATED_ENTRUST("综合撤单", 109),

    REQ_QRY_INTEGRATED_ENTRUST("查询综合订单", 110),
    REQ_QRY_INTEGRATED_QUOTE("查询大宗行情", 111),
    REQ_QRY_TRADE_ENABLE("查询最大交易数量", 112),
    REQ_QRY_HISTORY_TRADE("查询历史交易明细", 113),
    REQ_QRY_CUSTOMER_FINANCE("查询客户资产", 114),
    REQ_QRY_INSTRUMENT_INFO("查询合约数据", 115),
    REQ_PLEDGE_DISCOUNT("债券质押率", 116),
    REQ_STANDARD_BONDS("标准券查询", 117),
    REQ_QRY_INTEGRATED_BULK_QUOTE("查询大宗行情", 118),
    REQ_QRY_INTEGRATED_BULK_CLOSE_QUOTE("查询盘后大宗行情", 119),
    REQ_QRY_INTEGRATED_REPURCHASE_QUOTE("查询协议回购行情", 120),
    REQ_QRY_INTEGRATED_FIXED_INCOME_QUOTE("查询上海固收平台报价行情", 121),
    REQ_QRY_FUTURES_INSTRUMENT("查询期货合约", 122),
    REQ_QRY_FUTURES_POSITION("查询期货持仓", 123),
    REQ_QRY_FUTURES_POSITION_DETAIL("查询期货持仓明细", 124),
    REQ_QRY_FUTURES_MARGIN_RATE("查询期货保证金", 125),
    REQ_QRY_FUTURES_COMMISSION_RATE("查询期货手续费", 126),
    REQ_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE("查询我收到的协商成交行情", 130),
    /*** resp terminal trade ---------> center ***/
    RESP_START("resp start", 500),
    RESP_ACCOUNT_LOGIN("账号登录返回", 501),
    RESP_ACCOUNT_LOGOUT("账号登出返回", 502),
    RESP_QRY_FINANCE("帐号详情返回", 503),
    RESP_QRY_POSITION("持仓详情返回", 504),
    RESP_QRY_POSITION_STATICS("持仓明细(期货)返回", 505),
    RESP_QRY_TRADE("请求成交返回", 506),
    RESP_QRY_ENTRUST("请求订单返回", 507),
    RESP_QRY_INTEGRATED_ENTRUST("请求综合订单返回", 508),
    RESP_QRY_INTEGRATED_QUOTE("请求大宗行情返回", 509),
    RESP_QRY_ENABLE("请求最大交易数量返回", 510),
    RESP_QRY_HISTORY_TRADE("查询历史交易明细返回", 511),
    RESP_QRY_CUSTOMER_FINANCE("查询客户资产返回", 512),
    RESP_QRY_INSTRUMENT_INFO("查询合约数据返回", 513),

    RESP_QRY_INTEGRATED_BULK_QUOTE("查询大宗行情返回", 514),
    RESP_QRY_INTEGRATED_BULK_CLOSE_QUOTE("查询盘后大宗行情返回", 515),
    RESP_QRY_INTEGRATED_REPURCHASE_QUOTE("查询协议回购行情返回", 516),
    RESP_QRY_INTEGRATED_FIXED_INCOME_QUOTE("查询上海固收平台报价行情返回", 517),
    RESP_QRY_FUTURES_INSTRUMENT("查询期货合约返回", 518),
    RESP_QRY_FUTURES_POSITION("查询期货持仓返回", 519),
    RESP_QRY_FUTURES_POSITION_DETAIL("查询期货持仓明细返回", 526),
    RESP_QRY_FUTURES_MARGIN_RATE("查询期货保证金", 527),
    RESP_QRY_FUTURES_COMMISSION_RATE("查询期货手续费", 528),

    RESP_ENTRUST("下单返回", 520),
    RESP_CANCEL_ENTRUST("撤单返回", 521),
    RESP_INTEGRATED_ENTRUST("综合下单返回", 522),
    RESP_CANCEL_INTEGRATED_ENTRUST("综合撤单返回", 523),
    RESP_PLEDGE_DISCOUNT("债券质押率返回", 524),
    RESP_STANDARD_BONDS("标准券查询返回", 525),
    /*** notify terminal trade ---------> center ***/
    NOTIFY_START("", 1000),

    NOTIFY_ENTRUST("订单通知", 1001),
    NOTIFY_TRADE("成交通知", 1002),
    NOTIFY_INTEGRATED_ENTRUST("综合业务订单通知", 1003),
    NOTIFY_RTGS_STATUS("RTGS交收状态通知", 1004),
    END("end", 1000000000);

    public final int value;
    public final String msg;

    FunctionCode(String msg, int assetType) {
        this.msg = msg;
        this.value = assetType;
    }

}

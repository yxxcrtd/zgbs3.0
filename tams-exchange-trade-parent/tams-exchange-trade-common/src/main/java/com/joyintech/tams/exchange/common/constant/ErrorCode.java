package com.joyintech.tams.exchange.common.constant;

import com.joyintech.tams.exchange.common.enums.IEnum;

public enum ErrorCode implements IEnum<Integer> {

    SUCCESS(0, "success"),
    /**
     * 1 —— 1000系统错误号
     */
    FLUSH_INSTRUCTION_FAILED(1, "指令数据刷新失败"),

    FLUSH_TRADE_TIME_FAILED(2, "交易时间刷新失败"),

    REQ_PARAMETER_ERROR(10, "请求参数错误"),

    REQ_PARAMETER_ENTRUST_BS_ERROR(11, "委托方向错误"),

    REQ_PARAMETER_ENTRUST_PROP_ERROR(12, "委托属性错误"),

    REQ_PARAMETER_STOCK_CODE_ERROR(13, "合约代码错误"),

    ENTRUST_STATUS_ERROR(14, "委托状态错误"),

    APPLY_UN_SELL(15, "申购业务不支持卖出"),

    HS_UN_BUY(16, "回售业务不支持买入"),

    DERIVATIVE_PARAM_ERROR(18, "衍生品委托参数平仓标志和投机套保标志不能为空"),

    DERIVATIVE_INSTRUMENT_ERROR(19, "未找到合约代码"),

    INTENTION_UN_LINKMAN(21, "大宗交易意向申报没有联系人"),

    SH_FIXEDINCOME_BAOJIA_OPPOSITE_UN_CONTRACTID(22, "指定对手方成交没有约定号"),

    ACCOUNT_APPLY_NOT_UNIQUE(23, "同一股东只能进行一次该申报"),

    ENTRUST_NOT_EXITS(24, "委托不存在"),

    INSTRUCTION_APPLY_NOT_UNIQUE(25, "只能进行一次该申报"),

    DB_ERROR(100, "数据库返回错误"),

    INNER_RISK_ERROR(200, "内部风控检查错误"),

    MASTER_TOKEN_DISABLE(300, "主从模式关闭"),

    SYSTEM_ERROR(500, "服务器内部错误"),

    SYSTEM_FUNCTION_NOT_IMPLEMENT(501, "功能未实现"),

    EUREKA_SERVER_ERROR(600, "当前主节点不可用"),

    /**
     * 1001 —— 2000业务错误号
     */
    BULK_QUOTE_NOT_EXIT(1001, "该合约无行情，请更换合约代码"),

    TERMINAL_DELETE_ERROR(1002, "删除终端失败，请解除终端与账户的绑定"),

    SH_UN_SUPPORT_INSTRUMENT(1003, "上交所固收平台业务不支持该合约代码"),

    SH_UN_SUPPORT_PROTOCOL_BACK_INSTRUMENT(1004, "上交所债券协议回购业务不支持该合约代码"),

    SZ_UN_SUPPORT_PROTOCOL_BACK_INSTRUMENT(1005, "深交所债券协议回购业务不支持该合约代码"),

    API_RETURN_ERROR(1006, "接口返回错误"),

    CANCEL_ENTRUST_STATUS_ERROR(1007, "委托状态不可撤"),

    CANCEL_OTHER_ENTRUST_STATUS_ERROR(1008, "撤单委托状态为待撤审，不可重复撤单"),

    BULKTRADE_ASSET_TYPE_ERROR(1009, "大宗交易标的不是股票、债券、基金、债券质押式回购中的一种"),

    BULKTRADE_BS_ERROR(1010, "大宗交易交易方向不是 买入、卖出、正回购、逆回购中的一种"),

    BULKTRADE_PLAT_ERROR(1011, "大宗交易交易平台必须为：上交所大宗交易平台或者深交所综合协议平台"),

    BULKTRADE_ASSETTYPE1_ERROR(1012, "大宗交易资产二级分类(assetType1)不为H01-质押式协议回购"),

    QUERY_STANDARD_BOND_ERROR(1013, "标准券查询失败"),

    ENTRUST_NUM_ERROR(1014, "委托数量错误"),

    ALGORITHM_STATUS_ERROR(1015, "算法单状态错误"),

    ALGORITHM_NOT_EXIST(1016, "算法单不存在"),

    ALGORITHM_TIME_ERROR(1017, "算法单时间错误"),

    INSTRUCTION_NOT_SUPPORT_ALGORITHM_ERROR(1018, "该指令不支持算法交易"),

    INSTRUMENT_INFO_NOT_EXIT(1019, "申报代码信息不存在"),

    ENTRUST_PROP_NOT_EXIT(1020, "委托属性不存在"),

    UN_SUPPORT_ENTRUST_PROP(1021, "现券交易不支持当前委托属性"),
    TRADE_NOT_EMPTY(1022, "本方交易员不能为空"),
    DEALER_NOT_EMPTY(1023, "本方交易商ID或名称不能为空"),
    INVESTOR_NOT_EMPTY(1024, "本方交易主体ID或名称不能为空"),
    COUNTER_NOT_EMPTY(1025, "交易对手不能为空"),
    PROP_TRADE_NOT_EMPTY(1026, "对手方交易员不能为空"),
    PROP_DEALER_NOT_EMPTY(1027, "对手方交易商ID或名称不能为空"),
    PROP_INVESTOR_NOT_EMPTY(1028, "对手方交易主体ID或名称不能为空"),
    CONTRACT_ID_NOT_EMPTY(1029, "协商成交约定号不能为空"),

    /**
     * 2001 —— 3000终端错误号
     */
    TERMINAL_ERROR_CODE_START(2000, "terminal error code start"),

    TERMINAL_API_ERROR(2001, "接口api调用出错"),

    TERMINAL_NOT_ONLINE(2002, "交易网关不在线"),

    ACCOUNT_NOT_LOGIN(2003, "账号未登录，网关不在线"),

    ACCOUNT_NOT_EXIT(2004, "账号不存在"),

    QUOTE_NOT_EXIT(2005, "行情不存在"),

    TERMINAL_UN_SUPPORT_FUNCTION(2998, "柜台暂不支持该业务"),

    TERMINAL_RETURN_ERROR(2999, "柜台返回错误"),

    TERMINAL_ERROR_CODE_END(3000, "terminal error code end"),
    /**
     * 4001 —— 5000指令错误号
     */
    INSTRUCTIONS_ERROR_CODE_START(4000, "instruction error code start"),

    INSTRUCTIONS_NOT_EXIST(4001, "指令不存在"),

    INSTRUCTIONS_STATUS_ERROR(4002, "指令状态错误"),

    INSTRUCTIONS_NUMBER_ERROR(4003, "指令委托数量错误"),

    INSTRUCTIONS_CANCEL_FORBID(4004, "当前指令不可撤回"),

    INSTRUCTIONS_BALANCE_ERROR(4005, "指令委托金额错误"),

    INSTRUCTION_CANCELED_ALREADY(4006, "指令已撤回，不可重复放弃"),

    INSTRUCTION_HAVE_PENDING_APPROVAL(4007, "指令中存在审批流程中委托，不可放弃"),

    INSTRUCTION_HAVE_REPORTED_ENTRUST(4008, "指令中存在在途委托，不可放弃"),

    INSTRUCTION_HAVE_TRADE(4009, "指令中已有成交委托，不可放弃"),

    INSTRUCTION_ACCEPT_OR_REFUSE_ERROR(4010, "指令接收或放弃失败： status is not 0"),

    INSTRUCTION_ACCEPT_USER_ERROR(4011, "指令的接收人不是当前交易员"),

    INSTRUCTION_BEGIN_DATE_ERROR(4012, "指令的开始日大于平台日"),

    INSTRUCTION_END_DATE_ERROR(4013, "指令的结束日小于平台日"),

    INSTRUCTIONS_PRICE_ERROR(4014, "指令委托价格超限"),

    REQ_PARAMETER_INSTRUCTION_ID_ERROR(4015, "指令ID错误"),

    FAIR_INSTRUCTIONS_UNACCEPT_ERROR(4016, "存在需要执行公平交易但未接收的指令，请先接收指令！"),

    NOT_FAIR_INSTRUCTION_ERROR(4017, "非公平交易模式，无法进行公平交易！"),

    NOT_EXIST_FAIR_INSTRUCTION_ERROR(4018, "没有符合公平交易的指令，无法进行公平交易！"),

    INSTRUCTIONS_DERIVATIVE_BS_OC_ERROR(4030, "指令中衍生品买卖方向和开平仓标志不能为空"),

    INSTRUCTIONS_DERIVATIVE_BS_OC_NOT_MATCH_ERROR(4031, "指令中衍生品买卖方向和开平标志不匹配"),

    INSTRUCTION_PLEDGE_NUMBER_ERROR(4050, "抵押券数量错误"),

    INSTRUCTIONS_RETURN_FORBID(4051, "当前指令不可退回"),

    QUOTATION_NOT_EXIST(4052, "未获取到当前资产行情信息"),

    APPOINT_INSTRUCTION_EXIST_PARAMETER_ERROR(4997, "分发指令数据存在不合法"),

    APPOINT_INSTRUCTION_PARAMETER_ERROR(4998, "分发指令数据错误"),

    APPOINT_INSTRUCTION_ERROR(4999, "处理分发指令数据失败"),

    INSTRUCTIONS_ERROR_CODE_END(5000, "instruction error code end"),
    /**
     * 6001 —— 7000账号相关错误号
     */
    ACCOUNT_FUND_ERROR_CODE_START(6000, "account error code start"),

    ACCOUNT_FUND_NOT_FOUND(6001, "未找到账户对应的资金"),

    ACCOUNT_FUND_NOT_ENOUGH(6002, "账户下资金不足"),

    ACCOUNT_POSITION_NOT_FOUND(6003, "账号持仓查询失败"),

    ACCOUNT_POSITION_NOT_ENABLE(6004, "账号可用持仓不足"),

    ACCOUNT_POSITION_NOT_STOCK(6005, "无该合约持仓"),

    ACCOUNT_RISK_PARAMETER_NOT_EXIT(6006, "账号风控参数不存在"),

    ACCOUNT_HAD_LOGIN(6007, "账号已经登录成功"),

    ACCOUNT_HAD_LOGIN_OUT(6008, "账号尚未登录"),

    ACCOUNT_NOT_FOUND_BROKER(6009, "未找到营业部信息"),

    ACCOUNT_TRANSFORM_ERROR(6010, "账户转换失败，请检查录入的账户信息"),

    ACCOUNT_MARGIN_RATE_FLUSH_ERROR(6011, "账号保证金率刷新失败"),

    ACCOUNT_COMMISSION_RATE_FLUSH_ERROR(6012, "账号手续费率刷新失败"),

    ACCOUNT_FUND_ERROR_CODE_END(7000, "account error code end"),

    ERROR_CODE_END(100000, "error code end");

    ErrorCode(int value, String errorMsg) {
        this.value = value;
        this.errorMsg = errorMsg;
    }

    public final int value;
    public final String errorMsg;

    public int value() {
        return this.value;
    }

    public String msg() {
        return this.errorMsg;
    }

    public boolean isSuccess() {
        return this.value == 0;
    }

    public static boolean isFail(int value) {
        return value != 0;
    }

    public static boolean isSuccess(int value) {
        return value == 0;
    }

    public static String getStr(ErrorCode errorCode) {
        return errorCode.value + ":" + errorCode.errorMsg;
    }

    @Override
    public Integer getCode() {
        return value;
    }

    @Override
    public String getDescription() {
        return errorMsg;
    }
}

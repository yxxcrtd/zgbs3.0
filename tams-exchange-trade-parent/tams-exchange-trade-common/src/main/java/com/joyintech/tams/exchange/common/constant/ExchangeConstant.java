package com.joyintech.tams.exchange.common.constant;

/**
 * @ClassName ExchangeConstant
 * @Description TODO
 * @Author swwan
 * @Date 2020/6/15 17:04
 * @Version 1.0
 **/
public class ExchangeConstant {

    public static final String TERMINAL_TYPE_UFX = "ufx";
    public static final String TERMINAL_TYPE_KCBP = "kcbp";
    public static final String TERMINAL_TYPE_KDGATEWAY = "KDGateway";
    public static final String TERMINAL_TYPE_MOCK = "mock";

    public static final int ONE = 1;
    public static final String TRUE_IN_PARA = "01";
    public static final String SYS_CODE_IN_PARA = "TRD";
    public static final String FAIR_CODE_IN_PARA = "TRD_CHECK_FAIR";
    public static final String RISK_APPROVAL_IN_PARA = "TRD_CHECK_RISK_APPROVAL";
    public static final String FAIR_MODE_ENTRUST_PRICE_BEST_THAN_INSTRUCTION_IN_PARA = "TRD_FAIR_MODE_ENTRUST_PRICE_BEST_THAN_INSTRUCTION";
    public static final String FAIR_MODE_WITH_UNACCEPT_INSTRUCTION_IN_PARA = "TRD_FAIR_MODE_WITH_UNACCEPT_INSTRUCTION";
    public static final String ETR_TRADE_TIME_SWITCH = "etr_trade_time_switch";
    public static final String BOND_TRADED_UNIT = "bond_traded_unit";


    public static final String HTTP_STATUS_CODE_OK = "200";
    public static final String HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR = "500";

    public static final int PRICE_OF_STANDARD_BONDS = 100;

    public static final int RECOVER_DB2MEMORY = 0;
    public static final int RECOVER_MEMORY2DB = 1;
    public static final int RECOVER_MEMORY2DB_WITH_DELETE = 2;

    //交易端指令列表自动刷新时间(单位毫秒)
    public static final String TRD_INSTRUCTION_REFRESH_TIME = "trd_instruction_refresh_time";
    //交易端行情列表自动刷新时间(单位毫秒)
    public static final String TRD_QUOTE_REFRESH_TIME = "trd_quote_refresh_time";
    //交易端委托列表自动刷新时间(单位毫秒)
    public static final String TRD_ENTRUST_REFRESH_TIME = "trd_entrust_refresh_time";
}

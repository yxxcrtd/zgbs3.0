package com.joyintech.tams.exchange.common.microservice;

public class TradeServiceURL {

    public static String prefix = "/joyin";
    /**
     * 场内交易服务
     */
    public static String queryAccount = "/account";
    public static String addAccount = "/add/account";
    public static String updateAccount = "/update/account";
    public static String deleteAccount = "/delete/account";
    public static String connectAccount = "/account/connect";
    public static String compareAccount = "/account/compare";
    public static String queryAccountFund = "/account/fund";
    public static String queryAccountRiskParam = "/account/risk";
    public static String addAccountRiskParam = "/account/risk/add";
    public static String updateAccountRiskParam = "/account/risk/update";
    public static String deleteAccountRiskParam = "/account/risk/delete";

    public static String queryAllBroker = "/broker";
    public static String addBroker = "/addbroker";
    public static String updateBroker = "/updatebroker";
    public static String deleteBroker = "/deletebroker";

    public static String queryEntrust = "/entrust";
    public static String queryIntegratedEntrust = "/entrust/integrated";
    public static String cancelEntrust = "/entrust/cancel";
    public static String queryIntegratedQuote = "/entrust/integrated/quote";
    public static String queryEnable = "/entrust/enable";
    public static String queryEntrustPage = "/entrust/page";
    public static String queryIntegratedEntrustPage = "/entrust/integrated/page";
    public static String queryExceptionEntrust = "/entrust/exception";
    public static String cancelExceptionEntrust = "/entrust/exception/cancel";

    public static String queryOrder = "/order";
    public static String sendOrder = "/order";
    public static String sendIntegratedEntrust = "/order/integratedEntrust";
    public static String cancelOrder = "/order/cancel";

    // 下单
    public static String sendOrderWithReview = "/order/review";
    public static String sendIntegratedEntrustWithReview = "/order/integratedEntrust/review";
    public static String sendAlgorithm = "/order/algorithm";

    // 撤销
    public static String cancelEntrustWithRevoke = "/entrust/cancel/revoke";
    public static String cancelOrderWithRevoke = "/order/cancel/revoke";

    // 处理审批结果
    public static String examineOrderReview = "/order/review/examine";
    public static String examineIntegratedEntrustReview = "/integratedEntrust/review/examine";
    public static String examineEntrustRevoke = "/entrust/revoke/examine";
    public static String examineOrderRevoke = "/order/revoke/examine";

    public static String queryTrade = "/trade";
    public static String queryHistoryTrade = "/trade/history";
    public static String queryTradePage = "/trade/page";

    public static String queryInstructionAll = "/instruction/users";
    public static String flushInstructionAll = "/instruction/flush";

    public static String queryQuoteTick = "/quote/tick";
    public static String queryQuoteTickConvert = "/quote/tick/convert";

    public static String queryInstrument = "/common/instrument";

    public static String queryFuturesInstrument = "/common/futures_instrument";

    public static String queryFairCode = "/common/fairtrade";

    public static String querySystemDate = "/common/sysdate";

    public static String queryServiceNode = "/common/service/node";

    public static String changeRedisConfig = "/common/redis/use";

    public static String queryMaster = "/common/master";

    public static String getTradeDate = "/common/trade/date";

    public static String instructionConfirm = "/instruction/confirm";//指令接收|放弃接口

    public static String deleteTerminalMapping = "/delete/terminalMapping";
    public static String updateTerminalMapping = "/update/terminalMapping";
    public static String addTerminalMapping = "/add/terminalMapping";
    public static String queryTerminalMapping = "/terminalMapping";
    public static String deleteTerminal = "/delete/terminal";
    public static String updateTerminal = "/update/terminal";
    public static String addTerminal = "/add/terminal";
    public static String queryTerminal = "/terminal";
    public static String deleteServiceNode = "/delete/serviceNode";
    public static String updateServiceNode = "/update/serviceNode";
    public static String addServiceNode = "/add/serviceNode";
    public static String queryServiceNodeInfo = "/serviceNode";
    public static String querySystemMonitor = "/monitor";
    public static final String querySystemMonitorDynamic = "/monitor/dynamic";
    public static String queryCache = "/cache";
    public static String addCache = "/cache/add";
    public static String recoverCache = "/cache/recover";
    public static String clearCache = "/cache/clear";
    public static String deleteCache = "/cache/delete";
    public static String queryAvailableStandardBond = "/account/standard/bond/available";
    public static String queryCacheMonitor = "/cache/monitor";
    /**
     * 行情查询接口
     */
    public static String queryIntegratedBulkQuote = "/entrust/integrated/bulk_quote";
    public static String queryIntegratedBulkCloseQuote = "/entrust/integrated/bulk_close_quote";
    public static String queryIntegratedRepurchaseQuote = "/entrust/integrated/repurchase_quote";
    public static String queryIntegratedFixedIncomeQuote = "/entrust/integrated/fixed_income_quote";

    /**
     * 投管服务
     */
    public static String reqRiskControl = "/interf/risk/doOccupyBatchData";//风险3合1接口

    public static String reqRiskControlRelease = "/interf/risk/doOccupyReleasBatchData";//风控释放

    public static String reqTriggerRisk = "/interf/risk/doOrderRskApproval";//触发风控审批接口

    public static String queryInstructions = "/EtrTradeSysInterf/queryEtrentrustList";

    public static String queryCapitalAccount = "/MstCaptialAccount/selectForInterf";

    public static String queryBusinessDepartment = "/MstSaleDepartment/selectForInterf";

    public static String queryAccountRiskControl = "/";

    public static String acceptOrRefuse = "/EtrTradeSysInterf/handleEntrust";//指令接收|放弃接口
    public static String getRealNameByUser = "/EtrTradeSysInterf/getRealNameByUser";
    public static String calcPriceAndYield = "/etrexchange/common/etrExchangeTrade001/calcPriceAndYield";//净价、全价、到期收益率计算三合一
    public static String expireIncomeRate = "/TrdBondInvestTrade/calculate03Data";//到期收益率

    public static String unitCprice = "/TrdBondInvestTrade/calculate01Data";//净价
    public static String fpriceAmt = "/TrdBondInvestTrade/calculate02Data";//全价总额
    public static String yield = "/TrdBondInvestTrade/calculate03Data";//按照到期收益率计算
    public static String selectForInterf = "/AssetDeclarationCodeMaintenance/selectForInterf";//申报代码获取
    public static String querySysDate = "/InterfSysPara/getSysDate"; //平台日
    public static String querySysParam = "/InterfSysPara/getSysPara"; //系统参数
    public static String selectMstCounterParty = "/InterfSysTrdConfig/selectMstCounterParty";//查询交易对手信息
    public static String queryCounterSeatInfo = "/InterfSysTrdConfig/selectCounterSeatId";//查询交易对手席位信息
    public static String queryCounterTraderInfo = "/InterfSysTrdConfig/selectCounterTraderId";//查询交易对手交易员信息
    public static String addCounterSeat = "/InterfSysTrdConfig/insertCounterSeatId";//新增交易对手席位
    public static String addCounterTrader = "/InterfSysTrdConfig/insertCounterTraderId";//新增交易对手交易员

    public static String queryTradeDate = "/MstMarketCalendarInfo/queryTradeDate"; //取交易日
    public static String checkTradeDate = "/MstMarketCalendarInfo/queryIsTradeDate"; //查询输入日期是否交易日
    public static String queryTradeTime = "/InterfSysTrdConfig/getTrdTimeConfig"; //取交易时间

    public static String selectStockInfo = "/InterfSysTrdConfig/selectStockInfo";//从投管获取合约基础数据
    public static String getMinPrice = "/InterfSysTrdConfig/getMinPrice";//最小价差数据查询
    public static String getMinTrdUnitLimit = "/InterfSysTrdConfig/getMinTrdUnitLimit";//最小交易单位、交易上下限数据查询
    public static String getDealerTraderInfo = "/InterfSysTrdConfig/getDealerTraderInfo";//交易商交易员信息查询
    public static String getBidPriceConInfo = "/InterfSysTrdConfig/getBidPriceConInfo";//SPPI

    public static String getPtlAuthInfo = "/ptlmanagementinfo/getPtlAuthInfo";//组合权限查询

    public static String getPortfolioCash = "/invest/entrust/EtrCommonController/getProAvailableCash";//组合可用现金查询
    public static String getPortfolioPosition = "/invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/getAssetAvailablePosition";//组合可用持仓查询

    public static String getDeviationDegree = "/etrexchange/common/etrExchangeTrade001/calcDeviationDegree";//偏离度查询
    public static String getEtrDispatcherInfo = "/exchange/entrust/fieldetrinfoquerymain/fieldetrdispatcher/getReadonlyFormEtrInfo/FieldEtrDispatcher/FieldEtrDispatcherInfo/";//指令信息查询

    public static String pushTrade ="/EntrustMsgbyMQ/processFanout";
    public static String pushTradeForFixedIncome ="/EntrustMsgbyMQ/processManualSettleTrade";
    public static String pushProgress ="/EntrustMsgbyMQ/updateOrderFinishAmt";

    public static String getCounterAcctNoInfo = "/InterfSysTrdConfig/getCounterAcctNoInfo";//对手方资金账户信息查询

}

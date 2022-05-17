package com.joyintech.tams.exchange.terminal;

import com.alibaba.fastjson.util.TypeUtils;
import com.joyintech.framework.JoyinApplication;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.terminal.test.AccountConfig;
import com.joyintech.tams.exchange.terminal.test.TerminalAgentTest;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

//@SpringBootApplication
public class TerminalClientTest {
    private static final Logger logger = LoggerFactory.getLogger(TerminalClientTest.class);
    @Value("${agent.type}")
    private String agentType;
    @Autowired
    private AccountConfig accountConfig;

    private TerminalAgentTest terminalAgentTest = new TerminalAgentTest();

    protected AtomicLong requestID = new AtomicLong(1);// 生成订单编号

    // 股、债、基、可转债合约代码组合
    private List<String> stockCodes;
    private List<String> entrustPrice;
    private List<Integer> entrustVolume;
    private List<String> stockCode_Stocks;
    private List<String> stockCode_Bonds;
    private List<String> stockCode_Funds;

    //买卖方向设置
    private String entrustBs;
    // 回购天数设置
    private int compactTerm;
    //初始交易日
    private String firstSettleDate;
    // 固收测试债券
    private List<String> stockCode_Funds_FixedIncome;
    private List<String> entrustPrice_FixedIncome;
    // 协议回购测试债券--上海
    private List<String> stockCode_Funds_ProtocolBack_SH;
    private List<String> entrustPrice_ProtocolBack_SH;
    // 协议回购测试债券--深证
    private List<String> stockCode_Funds_ProtocolBack_SZ;
    private List<String> entrustPrice_ProtocolBack_SZ;

    //可转债 113550 --> 转股代码 191550
    private List<String> stockCode_ChangeBonds;
    private List<String> entrustPrice_ChangeBonds;
    // 测试业务类型
    private Set<String> testType;
    // 测试接口
    private static Set<String> interfaceNames;

    //大宗交易席位号
    private static List<String> propSeatNos;

    //交易员
    private static List<String> traders;

    //竞价交易entrustProp
    private static List<String> entrustProps_Stocks = new ArrayList<>();
    private static List<String> entrustProps_Bonds = new ArrayList<>();
    private static List<String> entrustProps_Funds = new ArrayList<>();
    // 大宗交易entrustProp
    private static List<String> integratedEntrustProps = new ArrayList<>();
    // 固收entrustProp
    private static List<String> integratedEntrustProps_FixedIncome = new ArrayList<>();
    // 协议回购entrustProp
    private static List<String> integratedEntrustProps_ProtocolBack_SH = new ArrayList<>();
    // 协议回购entrustProp
    private static List<String> integratedEntrustProps_ProtocolBack_SZ = new ArrayList<>();

    @PostConstruct
    public void init() {
        this.entrustBs = accountConfig.getEntrustBs();
        this.compactTerm = accountConfig.getCompactTerm();
        this.firstSettleDate = accountConfig.getFirstSettleDate();
        stockCodes = JsonUtils.getObjectListFromJson(accountConfig.getStockCodes(), String.class);
        entrustPrice = JsonUtils.getObjectListFromJson(accountConfig.getEntrustPrice(), String.class);
        entrustVolume = JsonUtils.getObjectListFromJson(accountConfig.getEntrustVolume(), Integer.class);
        stockCode_Stocks = JsonUtils.getObjectListFromJson(accountConfig.getStockCode_Stocks(), String.class);
        stockCode_Bonds = JsonUtils.getObjectListFromJson(accountConfig.getStockCode_Bonds(), String.class);
        stockCode_Funds = JsonUtils.getObjectListFromJson(accountConfig.getStockCode_Funds(), String.class);
        stockCode_ChangeBonds = JsonUtils.getObjectListFromJson(accountConfig.getStockCode_ChangeBonds(), String.class);
        entrustPrice_ChangeBonds = JsonUtils.getObjectListFromJson(accountConfig.getEntrustPrice_ChangeBonds(), String.class);
        testType = new HashSet<>(JsonUtils.getObjectListFromJson(accountConfig.getTestType(), String.class));
        interfaceNames = new HashSet<>(JsonUtils.getObjectListFromJson(accountConfig.getTestInterfaceName(), String.class));
        propSeatNos = JsonUtils.getObjectListFromJson(accountConfig.getPropSeatNo(), String.class);
        traders = JsonUtils.getObjectListFromJson(accountConfig.getTrader(), String.class);
        stockCode_Funds_FixedIncome = JsonUtils.getObjectListFromJson(accountConfig.getStockCode_Funds_FixedIncome(), String.class);
        entrustPrice_FixedIncome = JsonUtils.getObjectListFromJson(accountConfig.getEntrustPrice_FixedIncome(), String.class);
        stockCode_Funds_ProtocolBack_SH = JsonUtils.getObjectListFromJson(accountConfig.getStockCode_Funds_ProtocolBack_SH(), String.class);
        entrustPrice_ProtocolBack_SH = JsonUtils.getObjectListFromJson(accountConfig.getEntrustPrice_ProtocolBack_SH(), String.class);
        stockCode_Funds_ProtocolBack_SZ = JsonUtils.getObjectListFromJson(accountConfig.getStockCode_Funds_ProtocolBack_SZ(), String.class);
        entrustPrice_ProtocolBack_SZ = JsonUtils.getObjectListFromJson(accountConfig.getEntrustPrice_ProtocolBack_SZ(), String.class);
    }

    static {
        entrustProps_Stocks.add(EntrustProp.NORMAL.value);
        entrustProps_Bonds.add(EntrustProp.NORMAL.value);
        entrustProps_Funds.add(EntrustProp.NORMAL.value);

        entrustProps_Stocks.add(EntrustProp.ALLOTMENT.value);

        entrustProps_Stocks.add(EntrustProp.APPLY.value);
        entrustProps_Bonds.add(EntrustProp.APPLY.value);
        entrustProps_Funds.add(EntrustProp.APPLY.value);

        entrustProps_Bonds.add(EntrustProp.PS.value);
        entrustProps_Bonds.add(EntrustProp.CHANGE_TO_STOCK.value);
        entrustProps_Bonds.add(EntrustProp.HS.value);
        // 市价
        entrustProps_Stocks.add(EntrustProp.COUNTERPARTY.value);
        entrustProps_Stocks.add(EntrustProp.BEST5_TO_LIMIT.value);
        entrustProps_Stocks.add(EntrustProp.OWN_FIRST.value);
        entrustProps_Stocks.add(EntrustProp.IMMEDIATE_TO_CANCEL.value);
        entrustProps_Stocks.add(EntrustProp.BEST5_TO_CANCEL.value);
        entrustProps_Stocks.add(EntrustProp.FILL.value);

        entrustProps_Bonds.add(EntrustProp.ZYCRK.value);

        entrustProps_Funds.add(EntrustProp.JJSHSH.value);

        integratedEntrustProps.add(EntrustProp.INTENTION.value);
        integratedEntrustProps.add(EntrustProp.SET_PRICE.value);
        integratedEntrustProps.add(EntrustProp.CONFIRM.value);
        integratedEntrustProps.add(EntrustProp.LIMIT_PRICE.value);
        integratedEntrustProps.add(EntrustProp.MUTUAL_TRADE.value);
        integratedEntrustProps.add(EntrustProp.LARGE_AFTER_CLOSING.value);
        integratedEntrustProps.add(EntrustProp.LARGE_AFTER_CLOSING_PRICE.value);
        integratedEntrustProps.add(EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value);
        integratedEntrustProps.add(EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value);

        // 上海固收平台
        integratedEntrustProps_FixedIncome.add(EntrustProp.SH_FIXEDINCOME_BAOJIA_CONFIRM.value);
        integratedEntrustProps_FixedIncome.add(EntrustProp.SH_FIXEDINCOME_TRADE_CLICK.value);
        integratedEntrustProps_FixedIncome.add(EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value);
        integratedEntrustProps_FixedIncome.add(EntrustProp.SH_FIXEDINCOME_KZHCJSB.value);
        integratedEntrustProps_FixedIncome.add(EntrustProp.SH_FIXEDINCOME_ZYJCJSB.value);

        //协议回购
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_TRADE.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_RENEWAL.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_RELEASE.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_EXCHANGE.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_TERMINATE.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_EXPIRE.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value);
        integratedEntrustProps_ProtocolBack_SH.add(EntrustProp.SH_REPURCHASE_TERMINATE_REFUSE.value);

        integratedEntrustProps_ProtocolBack_SZ.add(EntrustProp.SZ_REPURCHASE_BEGIN.value);
        integratedEntrustProps_ProtocolBack_SZ.add(EntrustProp.SZ_REPURCHASE_BUYBACK.value);
        integratedEntrustProps_ProtocolBack_SZ.add(EntrustProp.SZ_REPURCHASE_RENEWAL.value);
    }


    public static void main(String[] args) {
        JoyinApplication.run(TerminalClientTest.class, args);
        TerminalClientTest terminalClientTest = SpringContextUtils.getBean(TerminalClientTest.class);
        terminalClientTest.runTest(terminalClientTest);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public void runTest(TerminalClientTest terminalClientTest) {
        if (interfaceNames.contains("login")) {
            terminalClientTest.loginTest();
        }
        if (interfaceNames.contains("entrust")) {
            terminalClientTest.entrustTest();
        }
        if (interfaceNames.contains("integratedEntrust")) {
            terminalClientTest.integratedEntrustTest();
        }
        if (interfaceNames.contains("cancelEntrust")) {
            terminalClientTest.cancelEntrustTest();
        }
        if (interfaceNames.contains("cancelIntegratedEntrust")) {
            terminalClientTest.cancelIntegratedEntrustTest();
        }
        if (interfaceNames.contains("integratedQuote")) {
            terminalClientTest.integratedQuotesTest();
        }
        if (interfaceNames.contains("customerFinance")) {
            terminalClientTest.customerFinanceTest();
        }
        if (interfaceNames.contains("pledgeDiscount")) {
            terminalClientTest.pledgeDiscountTest();
        }
        if (interfaceNames.contains("historyTrade")) {
            terminalClientTest.historyTradeTest();
        }
        if (interfaceNames.contains("standardBond")) {
            terminalClientTest.standardBondsTest();
        }
        if (interfaceNames.contains("instrument")) {
            terminalClientTest.instrumentInfoTest();
        }
        if (interfaceNames.contains("enable")) {
            terminalClientTest.enableTest();
        }
        if (interfaceNames.contains("queryEntrusts")) {
            terminalClientTest.queryEntrustsTest();
        }
        if (interfaceNames.contains("queryTrades")) {
            terminalClientTest.queryTradesTest();
        }
    }

    // 测试登录接口
    public void loginTest() {
        logger.info("----------- 账户登录接口测试 ----------");
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        // 账号登录及初始化
        ServerExtendInfo serverExtendInfo = JsonUtils.getObjectFromJson(accountConfig.getServerInfo(), ServerExtendInfo.class);
        tradeRequest.init(terminalAgentTest, serverExtendInfo, terminalAgentTest);
        List<Account> accountList = JsonUtils.getObjectListFromJson(accountConfig.getAccounts(), Account.class);
        if (AgentType.KD_GATEWAY.value.equals(agentType))
            TypeUtils.compatibleWithFieldName = true;
        if (Objects.nonNull(accountList)) {
            accountList.forEach(a -> tradeRequest.reqLogin(a, serverExtendInfo));
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 下单接口测试
    public void entrustTest() {
        logger.info("----------- 竞价交易下单测试 -------------");
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        terminalAgentTest.getAccountList().forEach(account -> {
            if (testType.contains("stock")) {
                logger.info("--------------------- 股票竞价交易测试开始 -----------------------");
                for (String stockCode_stock : stockCode_Stocks) {
                    for (String entrustProps_stock : entrustProps_Stocks) {
                        if (Market.SZSE.value.equals(StockCodeUtils.getMarket(stockCode_stock))) {
                            if (EntrustProp.BEST5_TO_LIMIT.value.equals(entrustProps_stock)) {
                                continue;
                            }
                        }
                        Entrust entrust = new Entrust();
                        entrust.setAssetType(InvestAssetTypeEnum.STOCK.value);
                        if (stockCode_stock.equals(stockCodes.get(0))) {
                            entrust.setEntrustPrice(new BigDecimal(entrustPrice.get(0)));
                            entrust.setEntrustVolume(entrustVolume.get(0));
                        } else if (stockCode_stock.equals(stockCodes.get(1))) {
                            entrust.setEntrustPrice(new BigDecimal(entrustPrice.get(1)));
                            entrust.setEntrustVolume(entrustVolume.get(1));
                        }
                        handleStockCode(tradeRequest, entrust, stockCode_stock, entrustProps_stock, account);
                    }
                }
                logger.info("--------------------- 股票竞价交易测试结束 -----------------------");
            }

            if (testType.contains("bond")) {
                logger.info("--------------------- 债券竞价交易测试开始 -----------------------");
                for (String stockCode_stock : stockCode_Bonds) {
                    entrustProps_Bonds.forEach(s -> {
                        if (!EntrustProp.CHANGE_TO_STOCK.value.equals(s)) {
                            Entrust entrust = new Entrust();
                            entrust.setAssetType(InvestAssetTypeEnum.BOND.value);
                            if (stockCode_stock.equals(stockCodes.get(2))) {
                                entrust.setEntrustPrice(new BigDecimal(entrustPrice.get(2)));
                                entrust.setEntrustVolume(entrustVolume.get(2));
                            } else if (stockCode_stock.equals(stockCodes.get(3))) {
                                entrust.setEntrustPrice(new BigDecimal(entrustPrice.get(3)));
                                entrust.setEntrustVolume(entrustVolume.get(3));
                            }
                            handleStockCode(tradeRequest, entrust, stockCode_stock, s, account);
                        }
                    });
                }
                logger.info("--------------------- 债券竞价交易测试结束 -----------------------");
            }
            if (testType.contains("fund")) {
                logger.info("--------------------- 基金竞价交易测试开始 -----------------------");
                for (String stockCode_stock : stockCode_Funds) {
                    entrustProps_Funds.forEach(s -> {
                        Entrust entrust = new Entrust();
                        entrust.setAssetType(InvestAssetTypeEnum.FUND.value);
                        if (stockCode_stock.equals(stockCodes.get(4))) {
                            entrust.setEntrustPrice(new BigDecimal(entrustPrice.get(4)));
                            entrust.setEntrustVolume(entrustVolume.get(4));
                        } else if (stockCode_stock.equals(stockCodes.get(5))) {
                            entrust.setEntrustPrice(new BigDecimal(entrustPrice.get(5)));
                            entrust.setEntrustVolume(entrustVolume.get(5));
                        }
                        handleStockCode(tradeRequest, entrust, stockCode_stock, s, account);
                    });
                }
                logger.info("--------------------- 基金竞价交易测试结束 -----------------------");
            }
            if (testType.contains("changeBond")) {
                logger.info("--------------------- 可转债竞价交易测试开始 -----------------------");
                for (String stockCode_stock : stockCode_ChangeBonds) {
                    entrustProps_Bonds.forEach(s -> {
                        if (EntrustProp.CHANGE_TO_STOCK.value.equals(s)) {
                            Entrust entrust = new Entrust();
                            entrust.setAssetType(InvestAssetTypeEnum.BOND.value);
                            entrust.setEntrustVolume(100L);
                            if (stockCode_stock.equals(stockCode_ChangeBonds.get(0))) {
                                entrust.setEntrustPrice(new BigDecimal(entrustPrice_ChangeBonds.get(0)));
                            } else if (stockCode_stock.equals(stockCode_ChangeBonds.get(1))) {
                                entrust.setEntrustPrice(new BigDecimal(entrustPrice_ChangeBonds.get(1)));
                            }
                            handleStockCode(tradeRequest, entrust, stockCode_stock, s, account);
                        }
                    });
                }
                logger.info("--------------------- 可转债竞价交易测试结束 -----------------------");
            }
        });
    }

    private void handleStockCode(ITradeRequest tradeRequest, Entrust entrust, String stockCode, String entrustProp, Account account) {
        entrust.setAccountId(account.getId());
        entrust.setStockCode(stockCode);
        if (Market.SZSE.value.equals(StockCodeUtils.getMarket(stockCode))) {
            entrust.setMarket(Market.SZSE.value);
        } else {
            entrust.setMarket(Market.SHSE.value);
        }
        processEntrust(tradeRequest, entrust, entrustProp);
    }

    private void processEntrust(ITradeRequest tradeRequest, Entrust entrust, String entrustProp) {
        try {
            Thread.sleep(accountConfig.getDelay());
            entrust.setEntrustProp(entrustProp);
            entrust.setEntrustBs(entrustBs);
            entrust.setBsFlag(EntrustUtils.getBsFlagForNormal(entrust.getEntrustProp(), entrust.getEntrustBs(), entrust.getAssetType()));
            long l = terminalAgentTest.generateReqId();
            entrust.setId("e" + l);
            entrust.setLocalEntrustId(l + "");
            terminalAgentTest.setRequestIdEntrustMap(entrust.getId(), l);
            tradeRequest.entrust(entrust, l);
            // 因为连接未建立时，默认将数据存储在repairConnectionMsg
            terminalAgentTest.compareEntrust();
        } catch (Exception e) {
            logger.error("普通委托下单失败：{}", e);
        }
    }

    public void integratedEntrustTest() {
        logger.info("------------------ 大宗交易测试开始 ------------------");
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        terminalAgentTest.getAccountList().forEach(account -> {
            if (testType.contains("stock")) {
                logger.info("--------------------- 股票大宗交易测试开始 -----------------------");
                for (String stockCode_stock : stockCode_Stocks) {
                    for (String entrustProps_stock : integratedEntrustProps) {
                        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
                        integratedEntrust.setAssetType(InvestAssetTypeEnum.STOCK.value);
                        integratedEntrust.setEntrustVolume(300000L);
                        if (stockCode_stock.equals(stockCodes.get(0))) {
                            integratedEntrust.setEntrustPrice(new BigDecimal(entrustPrice.get(0)));
                        } else if (stockCode_stock.equals(stockCodes.get(1))) {
                            integratedEntrust.setEntrustPrice(new BigDecimal(entrustPrice.get(1)));
                        }
                        processIntegratedEntrust(tradeRequest, integratedEntrust, stockCode_stock, entrustProps_stock, account);
                    }
                }
                logger.info("--------------------- 股票大宗交易测试结束 -----------------------");
            }

            if (testType.contains("bond")) {
                logger.info("--------------------- 债券大宗交易测试开始 -----------------------");
                for (String stockCode_stock : stockCode_Bonds) {
                    integratedEntrustProps.forEach(s -> {
                        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
                        integratedEntrust.setAssetType(InvestAssetTypeEnum.BOND.value);
                        integratedEntrust.setEntrustVolume(300000L);
                        if (stockCode_stock.equals(stockCodes.get(2))) {
                            integratedEntrust.setEntrustPrice(new BigDecimal(entrustPrice.get(2)));
                        } else if (stockCode_stock.equals(stockCodes.get(3))) {
                            integratedEntrust.setEntrustPrice(new BigDecimal(entrustPrice.get(3)));
                        }
                        processIntegratedEntrust(tradeRequest, integratedEntrust, stockCode_stock, s, account);
                    });
                }
                logger.info("--------------------- 债券大宗交易测试结束 -----------------------");
            }

            if (testType.contains("fund")) {
                logger.info("--------------------- 基金大宗交易测试开始 -----------------------");
                for (String stockCode_stock : stockCode_Funds) {
                    integratedEntrustProps.forEach(s -> {
                        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
                        integratedEntrust.setAssetType(InvestAssetTypeEnum.FUND.value);
                        integratedEntrust.setEntrustVolume(300000L);
                        if (stockCode_stock.equals(stockCodes.get(4))) {
                            integratedEntrust.setEntrustPrice(new BigDecimal(entrustPrice.get(4)));
                        } else if (stockCode_stock.equals(stockCodes.get(5))) {
                            integratedEntrust.setEntrustPrice(new BigDecimal(entrustPrice.get(5)));
                        }
                        processIntegratedEntrust(tradeRequest, integratedEntrust, stockCode_stock, s, account);
                    });
                }
                logger.info("--------------------- 基金大宗交易测试结束 -----------------------");
            }

            if (testType.contains("fixedIncome")) {
                logger.info("--------------------- 固收平台现券买卖测试开始 -----------------------");
                for (int i = 0; i < stockCode_Funds_FixedIncome.size(); i++) {
                    int finalI = i;
                    integratedEntrustProps_FixedIncome.forEach(s -> {
                        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
                        integratedEntrust.setAssetType(InvestAssetTypeEnum.FUND.value);
                        integratedEntrust.setEntrustVolume(50000L);
                        integratedEntrust.setEntrustPrice(new BigDecimal(entrustPrice_FixedIncome.get(finalI)));
                        processIntegratedEntrust(tradeRequest, integratedEntrust, stockCode_Funds_FixedIncome.get(finalI), s, account);
                    });
                }
                logger.info("--------------------- 固收平台现券买卖测试结束-----------------------");
            }

            if (testType.contains("protocolBack_SH")) {
                logger.info("--------------------- 协议回购交易上海测试开始 -----------------------");
                for (int i = 0; i < stockCode_Funds_ProtocolBack_SH.size(); i++) {
                    int finalI = i;
                    integratedEntrustProps_ProtocolBack_SH.forEach(s -> {
                        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
                        integratedEntrust.setAssetType(InvestAssetTypeEnum.FUND.value);
                        integratedEntrust.setEntrustVolume(100L);
                        integratedEntrust.setEntrustPrice(new BigDecimal(entrustPrice_ProtocolBack_SH.get(finalI)));
                        processIntegratedEntrust(tradeRequest, integratedEntrust, stockCode_Funds_ProtocolBack_SH.get(finalI), s, account);
                    });
                }
                logger.info("--------------------- 协议回购交易上海测试结束 -----------------------");
            }

            if (testType.contains("protocolBack_SZ")) {
                logger.info("--------------------- 协议回购交易深证测试开始 -----------------------");
                for (int i = 0; i < stockCode_Funds_ProtocolBack_SZ.size(); i++) {
                    int finalI = i;
                    integratedEntrustProps_ProtocolBack_SZ.forEach(s -> {
                        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
                        integratedEntrust.setAssetType(InvestAssetTypeEnum.FUND.value);
                        integratedEntrust.setEntrustVolume(100L);
                        integratedEntrust.setEntrustPrice(new BigDecimal(entrustPrice_ProtocolBack_SZ.get(finalI)));
                        processIntegratedEntrust(tradeRequest, integratedEntrust, stockCode_Funds_ProtocolBack_SZ.get(finalI), s, account);
                    });
                }
                logger.info("--------------------- 协议回购交易深证测试结束 -----------------------");
            }
        });


    }

    private void processIntegratedEntrust(ITradeRequest tradeRequest, IntegratedEntrust integratedEntrust, String stockCode, String entrustProp, Account account) {
        try {
            Thread.sleep(accountConfig.getDelay());
            integratedEntrust.setStockCode(stockCode);
            if (Market.SZSE.value.equals(StockCodeUtils.getMarket(stockCode))) {
                integratedEntrust.setMarket(Market.SZSE.value);
            } else {
                integratedEntrust.setMarket(Market.SHSE.value);
            }
            integratedEntrust.setEntrustProp(entrustProp);
            integratedEntrust.setEntrustBs(entrustBs);
            integratedEntrust.setBsFlag(EntrustUtils.getBsFlag(integratedEntrust.getEntrustProp(), integratedEntrust.getEntrustBs()));
            integratedEntrust.setAccountId(account.getId());
            integratedEntrust.setId("i" + requestID.incrementAndGet());
            integratedEntrust.setLocalEntrustId(integratedEntrust.getId() + "");
            long l = terminalAgentTest.generateReqId();
            if (IntegratedEntrustUtils.isBulk(integratedEntrust.getEntrustProp())) {
                integratedEntrust.setContractId((111 + l) + "");
                if (agentType.equals(AgentType.KINGDOM_KCBP.value)) {
                    integratedEntrust.setPropSeatNo(propSeatNos.get(0));
                } else if (agentType.equals(AgentType.UFX.value)) {
                    integratedEntrust.setPropSeatNo(propSeatNos.get(1));
                } else if (agentType.equals(AgentType.KD_GATEWAY.value)) {
                    integratedEntrust.setPropSeatNo(propSeatNos.get(2));
                } else if (agentType.equals(AgentType.JSD.value)) {
                    integratedEntrust.setPropSeatNo(propSeatNos.get(3));
                }
                integratedEntrust.setLiaisonTel((1241234 + l) + "");
                integratedEntrust.setLiaisonName("mock");
            } else if (IntegratedEntrustUtils.isFixedIncome(integratedEntrust.getEntrustProp())) {
                integratedEntrust.setContractId((222 + l) + "");
                if (agentType.equals(AgentType.KINGDOM_KCBP.value)) {
                    integratedEntrust.setLiaisonName(traders.get(0));//对手方交易员
                    integratedEntrust.setLiaisonTel(traders.get(0));//发起方交易员
                    integratedEntrust.setPropSeatNo(traders.get(0).substring(1, 4));//对手方交易商
                } else if (agentType.equals(AgentType.UFX.value)) {
                    integratedEntrust.setLiaisonName(traders.get(1));
                    integratedEntrust.setLiaisonTel(traders.get(1));
                    integratedEntrust.setPropSeatNo(traders.get(1).substring(1, 4));
                } else if (agentType.equals(AgentType.KD_GATEWAY.value)) {
                    integratedEntrust.setLiaisonName(traders.get(2));
                    integratedEntrust.setLiaisonTel(traders.get(2));
                    integratedEntrust.setPropSeatNo(traders.get(2).substring(1, 4));
                } else if (agentType.equals(AgentType.JSD.value)) {
                    integratedEntrust.setLiaisonName(traders.get(3));
                    integratedEntrust.setLiaisonTel(traders.get(3));
                    integratedEntrust.setPropSeatNo(traders.get(3).substring(1, 4));
                }
            } else if (IntegratedEntrustUtils.isProtocolBack_SH(integratedEntrust.getEntrustProp())) {
                integratedEntrust.setEntrustBalance(BigDecimal.valueOf(100 * integratedEntrust.getEntrustVolume()));
                integratedEntrust.setCompactTerm(compactTerm);
                integratedEntrust.setFirstSettleDate(firstSettleDate);//初始交易日期
                integratedEntrust.setOrigSerialNo((333 + l) + "");//成交编号
                integratedEntrust.setRemark("mock");//补充条款
                if (agentType.equals(AgentType.KINGDOM_KCBP.value)) {
                    integratedEntrust.setSellerNo(traders.get(0));//本方交易员代码
                    integratedEntrust.setLiaisonTel(traders.get(0));//对手方交易员代码
                    integratedEntrust.setPropSeatNo(traders.get(0).substring(1, 4));//对手方交易商
                } else if (agentType.equals(AgentType.UFX.value)) {
                    integratedEntrust.setSellerNo(traders.get(1));
                    integratedEntrust.setLiaisonTel(traders.get(1));
                    integratedEntrust.setPropSeatNo(traders.get(1).substring(1, 4));
                    integratedEntrust.setPropSeatName("JOYIN");
                } else if (agentType.equals(AgentType.KD_GATEWAY.value)) {
                    integratedEntrust.setSellerNo(traders.get(2));
                    integratedEntrust.setLiaisonTel(traders.get(2));
                    integratedEntrust.setPropSeatNo(traders.get(2).substring(1, 4));
                } else if (agentType.equals(AgentType.JSD.value)) {
                    integratedEntrust.setSellerNo(traders.get(3));
                    integratedEntrust.setLiaisonTel(traders.get(3));
                    integratedEntrust.setPropSeatNo(traders.get(3).substring(1, 4));
                }
                if (IntegratedEntrustUtils.isProtocolBack_SH_ChenJiao(integratedEntrust.getEntrustProp())) {
                    // todo 调用非公开行情接口获取数据
                    integratedEntrust.setContractId("");//交易所请求编号
                }
            } else if (IntegratedEntrustUtils.isProtocolBack_SZ(integratedEntrust.getEntrustProp())) {
                integratedEntrust.setEntrustBalance(BigDecimal.valueOf(100 * integratedEntrust.getEntrustVolume()));
                integratedEntrust.setCompactTerm(compactTerm);
                integratedEntrust.setRemark("mock");//补充条款
                if (agentType.equals(AgentType.KINGDOM_KCBP.value)) {
                    integratedEntrust.setPropSeatNo(propSeatNos.get(0));//对手方交易单元
                } else if (agentType.equals(AgentType.UFX.value)) {
                    integratedEntrust.setPropSeatNo(propSeatNos.get(0));
                } else if (agentType.equals(AgentType.KD_GATEWAY.value)) {
                    integratedEntrust.setPropSeatNo(propSeatNos.get(0));
                } else if (agentType.equals(AgentType.JSD.value)) {
                    integratedEntrust.setPropSeatNo(propSeatNos.get(0));
                }
                integratedEntrust.setContractId((444 + l) + "");//约定号
                if (EntrustProp.SZ_REPURCHASE_RENEWAL.value.equals(integratedEntrust.getEntrustProp())
                        || EntrustProp.SZ_REPURCHASE_BUYBACK.value.equals(integratedEntrust.getEntrustProp())) {
                    integratedEntrust.setOrigSerialNo("");//初始合约号
                }
            }

            terminalAgentTest.setRequestIdEntrustMap(integratedEntrust.getId(), l);
            tradeRequest.integratedEntrust(integratedEntrust, l);
            // 因为连接未建立时，默认将数据存储在repairConnectionMsg
            terminalAgentTest.compareIntegratedEntrustEntrust();
        } catch (InterruptedException e) {
            logger.error("综合委托下单失败： {}", e.getMessage());
        }
    }

    private void cancelEntrustTest() {
        // 撤单需要先查询可撤单委托
        queryEntrustsTest();
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        terminalAgentTest.getCancelEntrustList().forEach(entrust -> {
           /* if (entrust.getId().startsWith("e"))
                tradeRequest.cancelEntrust((Entrust) entrust, terminalAgentTest.generateReqId());
       */
        });
    }


    private void cancelIntegratedEntrustTest() {
        // 撤单需要先查询可撤单委托
        queryEntrustsTest();
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        terminalAgentTest.getCancelEntrustList().forEach(integratedEntrust -> {
            /*if (integratedEntrust.getId().startsWith("i"))
                tradeRequest.cancelIntegratedEntrust((IntegratedEntrust) integratedEntrust, terminalAgentTest.generateReqId());
        */
        });
    }

    // 标准券查询
    public void standardBondsTest() {
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        Account account = terminalAgentTest.getAccountList().iterator().next();
        logger.info("--------------------- 查询标准券信息  ---------------------");
        for (String stockCode_bond : stockCode_Bonds) {
            tradeRequest.queryPledgeDiscount(account.getId(), StockCodeUtils.getMarket(stockCode_bond), stockCode_bond, terminalAgentTest.generateReqId());
        }
        logger.info("--------------------- 标准券信息查询结束  ---------------------");
    }

    // 债券质押率查询
    public void pledgeDiscountTest() {
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        Account account = terminalAgentTest.getAccountList().iterator().next();
        logger.info("--------------------- 查询债券质押率信息  ---------------------");
        for (String stockCode_bond : stockCode_Bonds) {
            tradeRequest.queryPledgeDiscount(account.getId(), StockCodeUtils.getMarket(stockCode_bond), stockCode_bond, terminalAgentTest.generateReqId());
        }
        logger.info("--------------------- 债券质押率信息查询结束  ---------------------");
    }

    // 合约信息查询
    public void instrumentInfoTest() {
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        Account account = terminalAgentTest.getAccountList().iterator().next();
        logger.info("--------------------- 查询合约信息  ---------------------");
        InstrumentQryBean instrumentQryBean = new InstrumentQryBean();
        instrumentQryBean.setAccountId(account.getId());
        instrumentQryBean.setPostStr("");
        instrumentQryBean.setRowCount(1);
        for (Market market : Market.values()) {
            instrumentQryBean.setMarket(market.value);
            tradeRequest.queryInstrumentInfo(instrumentQryBean, terminalAgentTest.generateReqId());
        }
        logger.info("--------------------- 合约信息查询结束  ---------------------");
    }

    // 合约信息查询
    public void customerFinanceTest() {
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        Account account = terminalAgentTest.getAccountList().iterator().next();
        logger.info("--------------------- 查询账号资金信息  ---------------------");
        tradeRequest.queryCustomerFinance(account.getId(), terminalAgentTest.generateReqId());
        logger.info("--------------------- 账号资金信息查询结束  ---------------------");
    }

    // 对账单信息查询
    public void historyTradeTest() {
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        Account account = terminalAgentTest.getAccountList().iterator().next();
        logger.info("--------------------- 查询对账单信息  ---------------------");
        HistoryQryBean historyQryBean = new HistoryQryBean();
        historyQryBean.setAccountId(account.getId());
        historyQryBean.setStartDay("20200817");
        historyQryBean.setEndDay("2020081721");
        historyQryBean.setPageSize(5);
        historyQryBean.setPostStr("");
        tradeRequest.queryHistoryTrade(historyQryBean, terminalAgentTest.generateReqId());
        logger.info("--------------------- 对账单信息查询结束  ---------------------");
    }

    // 最大可交易数量信息查询
    public void enableTest() {
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        Account account = terminalAgentTest.getAccountList().iterator().next();
        logger.info("--------------------- 查询最大可交易数量  ---------------------");
        EnableQryBean enableQryBean = new EnableQryBean();
        enableQryBean.setAccountId(account.getId());
        for (String stockCode : stockCodes) {
            enableQryBean.setStockCode(stockCode);
            enableQryBean.setEntrustBs(entrustBs);
            enableQryBean.setEntrustPrice(15);
            enableQryBean.setEntrustProp(EntrustProp.NORMAL.value);
            tradeRequest.queryEnable(enableQryBean, terminalAgentTest.generateReqId());
        }
        logger.info("--------------------- 最大可交易数量查询结束  ---------------------");
    }

    // 最大可交易数量信息查询
    public void integratedQuotesTest() {
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        Account account = terminalAgentTest.getAccountList().iterator().next();
        logger.info("--------------------- 查询综合行情结束  ---------------------");
        QuoteQryBean quoteQryBean = new QuoteQryBean();
        quoteQryBean.setAccountId(account.getId());
        for (String stockCode : stockCodes) {
            quoteQryBean.setStockCode(stockCode);
            quoteQryBean.setEntrustBs(entrustBs);
            for (QuoteType quoteType : QuoteType.values()) {
                quoteQryBean.setQuoteType(quoteType.value);
                integratedEntrustProps.forEach(entrustProp -> {
                    tradeRequest.queryIntegratedQuotes(quoteQryBean, terminalAgentTest.generateReqId());
                });
            }
        }
        logger.info("--------------------- 综合行情信息查询结束  ---------------------");
    }

    // 查询成交信息
    public void queryTradesTest() {
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        Account account = terminalAgentTest.getAccountList().iterator().next();
        tradeRequest.reqTrades(account.getId(), terminalAgentTest.generateReqId());
    }

    // 查询委托信息
    public void queryEntrustsTest() {
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        terminalAgentTest.getAccountList().forEach(account -> {
            tradeRequest.reqEntrusts(account.getId(), terminalAgentTest.generateReqId());
        });
    }

}

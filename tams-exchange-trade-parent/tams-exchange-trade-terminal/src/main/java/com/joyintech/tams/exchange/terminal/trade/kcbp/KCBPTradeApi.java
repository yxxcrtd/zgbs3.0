package com.joyintech.tams.exchange.terminal.trade.kcbp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.bean.terminal.RTGSStatusBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.IntegratedEntrustUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import com.szkingdom.kcbpcli.KCBPClient;
import com.szkingdom.kcbpcli.KCBPClientC;
import com.szkingdom.kcbpcli.KCBPHandle;
import com.szkingdom.kcbpcli.KCBPReturnCode;
import com.szkingdom.kdencode.KDEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class KCBPTradeApi {

    private static final Logger logger = LoggerFactory.getLogger(KCBPTradeApi.class);

    private static long beginMill;

    private KCBPTradeClientImpl kcbpTradeClient;

    private KCBPHandle handle;

    private KCBPClient client;

    private TradeResponseImpl kcbpTradeResponse;

    private EntrustIdManager entrustIdManager;

    public static final String KCBP_FUNCTION_CODE_LOGIN = "410301";
    public static final String KCBP_FUNCTION_CODE_ENTRUST = "410411";
    //上海固收平台买卖委托
    public static final String KCBP_FUNCTION_CODE_ENTRUST_SH_FIXATION = "460010";
    public static final String KCBP_FUNCTION_CODE_CANCEL_ENTRUST = "410413";
    public static final String KCBP_FUNCTION_CODE_QRY_ENTRUST = "410510";
    //成交查询
    //    public static final String KCBP_FUNCTION_CODE_QRY_TRADE = "410512";
    public static final String KCBP_FUNCTION_CODE_QRY_TRADE = "410436";
    //大宗交易行情查询
    public static final String KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_SZ = "410995";
    public static final String KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_SH = "410996";
    //盘后大宗交易行情查询
    public static final String KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_CLOSE_SH = "410556";
    public static final String KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_CLOSE_SZ = "410559";
    //最大交易数量查询
    public static final String KCBP_FUNCTION_CODE_QRY_ENABLE = "410410";
    //查询历史交易明细
    public static final String KCBP_FUNCTION_CODE_QRY_HISTORY_TRADE = "411513";
    //资金查询
    public static final String KCBP_FUNCTION_CODE_QRY_FINANCE = "410502";
    //债券协议回购委托申报 -- 深证
    public static final String KCBP_FUNCTION_CODE_PROTOCOL_BACK_SZ = "411000";
    //债券协议回购委托申报 -- 上海
    //意向
    public static final String KCBP_FUNCTION_CODE_PROTOCOL_BACK_SH = "411031";
    //报价
    public static final String KCBP_FUNCTION_CODE_PROTOCOL_BACK_SH_BAOJIA = "411032";
    //成交
    public static final String KCBP_FUNCTION_CODE_PROTOCOL_BACK_SH_TRADE = "411024";
    //撤单
    public static final String KCBP_FUNCTION_CODE_PROTOCOL_BACK_SH_CANCEL = "411025";
    //标准券汇总查询
    public static final String KCBP_FUNCTION_CODE_QRY_STANDARD_BONDS = "410574";
    //质押库明细查询
    public static final String KCBP_FUNCTION_CODE_QRY_PLEDGE_DISCOUNT = "410575";
    //持仓查询
    public static final String KCBP_FUNCTION_CODE_QRY_ACCOUNT_POSITION = "410504";
    private SimpleDateFormat formatBeginDate = new SimpleDateFormat("yyyyMMdd");

    private Splitter commaSplitter = Splitter.on(",");

    private AtomicInteger offset = new AtomicInteger(0);

    private Map<String, Integer> status = Maps.newConcurrentMap();

    private String operway = "M";

//    private List<ReissueEntrustBean> reissueEntrustBeanList = new ArrayList<>();
//    private List<ReissueTradeBean> reissueTradeBeanList = new ArrayList<>();

    static {
        Calendar curDate = Calendar.getInstance();
        curDate.set(curDate
                .get(Calendar.YEAR), curDate.get(Calendar.MONTH), curDate
                .get(Calendar.DATE), 0, 0, 0);
        beginMill = curDate.getTimeInMillis();
    }

    public KCBPTradeApi(KCBPTradeClientImpl kcbpTradeClient) {
        this.kcbpTradeClient = kcbpTradeClient;
    }

    public boolean init(TradeResponseImpl kcbpTradeResponse, String operway) {
        this.kcbpTradeResponse = kcbpTradeResponse;
        if (!Strings.isNullOrEmpty(operway)) {
            this.operway = operway;
        }
        handle = new KCBPHandle();
        client = new KCBPClient();
        if (client.KCBPCLI_Init(handle) != 0) {
            logger.error("Init KCBP failed");
            return false;
        }
        client.KCBPCLI_SetOption(handle, KCBPClientC.KCBP_OPTION_CRYPT, 0);
        client.KCBPCLI_SetOption(handle, KCBPClientC.KCBP_OPTION_COMPRESS, 0);
        if (client.KCBPCLI_SetOption(handle, KCBPClientC.KCBP_OPTION_CURRENT_CONNECT, kcbpTradeClient.getTagConnectOption()) != 0) {
            logger.error("set connect option KCBP_OPTION_CURRENT_CONNECT error");
            return false;
        }
        if (client.KCBPCLI_SetOption(handle, KCBPClientC.KCBP_OPTION_AUTHENTICATION, 0) != 0) {
            logger.error("set connect option KCBP_OPTION_AUTHENTICATION error");
            return false;
        }
        if (client.KCBPCLI_SetCliTimeOut(handle, 60) != 0) {
            logger.error("set connect option KCBPCLI_SetCliTimeOut error");
            client.KCBPCLI_Exit(handle);
            return false;
        }
        int ret = client.KCBPCLI_ConnectServer(handle, kcbpTradeClient.getTagConnectOption().getSzServerName(), "KCXP00", "888888");
        if (ret != 0) {
            logger.error("KESBCLI_SQLConnect failed errorCode:{}", ret);
            client.KCBPCLI_Exit(handle);
            return false;
        }
        return true;
    }


    public ResultBean getResult() {
        // 获取第一结果集
        int ret = client.KCBPCLI_RsOpen(handle);
        if (ret != 0)
            return new ResultBean(ErrorCode.TERMINAL_API_ERROR.value, "KCBPCLI_RsOpen error");
        List<List<String>> data = Lists.newArrayList();
        // 获取第一结果集名称
        // 获取第一结果集列名表（列名之间由逗号分隔，如LEVEL,CODE,MSG）
        // 获取第一结果集列数
        int colNum = client.KCBPCLI_RsGetColNum(handle);
        if (colNum < 3)
            return new ResultBean(ErrorCode.TERMINAL_API_ERROR.value, "KCBPCLI_RsGetColNum error");
        // 获取第一结果的一行, 并需要判断业务是否执行成功
        if (client.KCBPCLI_RsFetchRow(handle) == 0) {
            // 判断业务是否调用成功
            int errorCode = Integer.parseInt(client.KCBPCLI_RsGetCol(handle, 2));
            if (errorCode != 0)
                return new ResultBean(ErrorCode.TERMINAL_API_ERROR.value, client.KCBPCLI_RsGetCol(handle, 3));
        } else {
            client.KCBPCLI_RsClose(handle);
            return new ResultBean(ErrorCode.TERMINAL_API_ERROR.value, "KCBPCLI_RsFetchRow error");
        }
        // 获取其他结果集
        while (client.KCBPCLI_RsMore(handle) == 0) {
            //添加其他结果集字段
            data.add(Lists.newArrayList(commaSplitter.split(client.KCBPCLI_RsGetColNames(handle))));
            colNum = client.KCBPCLI_RsGetColNum(handle);
            // 获取结果集的一行
            while (client.KCBPCLI_RsFetchRow(handle) == 0) {
                List<String> columns = Lists.newArrayListWithCapacity(colNum);
                for (int i = 0; i < colNum; i++) {
                    columns.add(client.KCBPCLI_RsGetCol(handle, i + 1));
                }
                data.add(columns);
            }
        }
        client.KCBPCLI_RsClose(handle);
        return new ResultBean(ErrorCode.SUCCESS, data);
    }

    public synchronized ResultBean sendCommand(String functionCode, Map<String, String> parameterValues) {
        // 登陆成功用custcert作为后续调用的入参
        try {
            client.KCBPCLI_BeginWrite(handle);
            for (Map.Entry<String, String> entry : parameterValues.entrySet()) {
                client.KCBPCLI_SetValue(handle, entry.getKey(), entry.getValue());
            }
            int ret = client.KCBPCLI_CallProgramAndCommit(handle, functionCode);
            return ret != KCBPReturnCode.RC_OK ? new ResultBean(ErrorCode.TERMINAL_API_ERROR.value,
                    "KCBPCLI_CallProgramAndCommit error: " + ret + ":" + client.KCBPCLI_GetErrorMsg(handle)) : getResult();
        } catch (Exception e) {
            logger.info("KCBP sendCommand failed {}", e);
            return new ResultBean(ErrorCode.TERMINAL_API_ERROR);
        }
    }

    public void setStatus(String accountId, AccountLoginStatus status) {
        logger.info("account {} status set to: {}", accountId, status.msg);
        this.status.put(accountId, status.value);
    }

    public int getStatus(String accountId) {
        return status.getOrDefault(accountId, AccountLoginStatus.NOT_LOGGED.value);
    }

    public ResultBean reqLogin(Account account) {
        setStatus(account.getId(), AccountLoginStatus.NOT_LOGGED);
        KDEncoder kdEncoder = new KDEncoder();
        String secretText = kdEncoder.KDEncode(6, account.getPwd(), KCBP_FUNCTION_CODE_LOGIN);
        Map<String, String> parameters = Maps.newHashMap();
        // 功能号
        parameters.put("funcid", KCBP_FUNCTION_CODE_LOGIN);
        // 客户代码
        parameters.put("custid", account.getFinanceAccount());
        // 客户机构
        parameters.put("custorgid", "");
        // 交易密码
        parameters.put("trdpwd", secretText);
        parameters.put("netaddr", kcbpTradeClient.getKcbpRegisterConfig().getAddress());
        parameters.put("neraddr2", kcbpTradeClient.getKcbpRegisterConfig().toString());
        //操作机构
        parameters.put("orgid", "");
        // 操作方式
        parameters.put("operway", operway);
        parameters.put("ext", "0");
        parameters.put("inputtype", "Z");
        parameters.put("inputid", account.getFinanceAccount());
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_LOGIN, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            KCBPAccount kcbpAccount = KCBPConverter.convertToAccount(account, (List<List<String>>) resultBean.getData());
            kcbpAccount.setAccountId(account.getId());
            Map<String, String> headMessages = Maps.newHashMap();
            headMessages.put("custid", kcbpAccount.getCustId());
            headMessages.put("custorgid", kcbpAccount.getOrgId());
            headMessages.put("custcert", kcbpAccount.getCustert());
            headMessages.put("netaddr", kcbpTradeClient.getKcbpRegisterConfig().getAddress());
            headMessages.put("neraddr2", kcbpTradeClient.getKcbpRegisterConfig().toString());
            headMessages.put("orgid", kcbpAccount.getOrgId());
            headMessages.put("ext", "0");
            headMessages.put("fundid", kcbpAccount.getFundId());
            headMessages.put("trdpwd", account.getTradePwd());
            headMessages.put("operway", operway);
            //定位串
            kcbpAccount.setTradePositionStr("");
            kcbpAccount.setEntrustPositionStr("");
            kcbpAccount.setProtocolBack_SZ_PositionStr_trade("");
            kcbpAccount.setProtocolBack_SH_PositionStr_trade("");
            kcbpAccount.setFixedIncome_PositionStr_trade("");
            kcbpAccount.setFixedIncome_PositionStr_entrust("");
            kcbpAccount.setProtocolBack_SH_PositionStr_entrust("");
            kcbpAccount.setProtocolBack_SZ_PositionStr_entrust("");
            kcbpAccount.setRTGS_PositionStr("");
            kcbpAccount.setHeadMessages(headMessages);
            kcbpTradeClient.addKCBPAccount(kcbpAccount);
            setStatus(account.getId(), AccountLoginStatus.LOGGED);
        } else {
            logger.warn("account:{} login failed reason:{}", JsonUtils.getJsonFromObject(account), resultBean.getErrorMsg());
        }
        resultBean.setData(account);
        kcbpTradeResponse.resLogin(resultBean, account.getId());
        return resultBean;
    }

    public Map<String, String> composeOrderTradeQuery(KCBPAccount kcbpAccount, String funcCode, String accountId, String postStr, String qryFlag, String beginDate) {
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        // 功能号
        parameters.put("funcid", funcCode);
        // 查询当日委托
        parameters.put("bankcode", "");
        parameters.put("ordersno", "");
        parameters.put("stkcode", "");
        parameters.put("secuid", "");
        parameters.put("market", "");
        parameters.put("qryflag", qryFlag);
        // 时间为系统时间是查询当日成交， 小于系统时间则查询历史成交
        if (Objects.nonNull(beginDate)) {
            parameters.put("begindate", beginDate);
            parameters.put("enddate", beginDate);
        }
        // 定位串
        if ("411003".equals(funcCode) || "411007".equals(funcCode) || "411006".equals(funcCode) || "411002".equals(funcCode))
            parameters.put("posstr", postStr);// 针对协议回购
        else
            parameters.put("poststr", postStr);
        parameters.put("count", "");
        return parameters;
    }

    public void reqEntrusts(String accountId, Long requestId) {
        List<Entrust> entrustList = getEntrusts(accountId);
        kcbpTradeResponse.resQueryEntrusts(entrustList, requestId);
    }

    public void reqTrades(String accountId, long requestId) {
        List<Trade> tradeList = getAllTrades(accountId);
        kcbpTradeResponse.resQueryTrade(tradeList, requestId);
    }

    private List<Entrust> getEntrusts(String accountId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(accountId);
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, KCBP_FUNCTION_CODE_QRY_ENTRUST,
                accountId, "", "1", "");
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_QRY_ENTRUST, parameters);
        return resultBean.getErrorCode() == ErrorCode.SUCCESS.value ?
                KCBPConverter.convertToEntrust(accountId, (List<List<String>>) resultBean.getData(), entrustIdManager) : Lists.newArrayList();
    }

    private List<Trade> do_getTrade(String funcCode, String accountId, String qryFlag, String beginDate) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(accountId);
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, funcCode,
                accountId, "", qryFlag, beginDate);
        ResultBean resultBean = sendCommand(funcCode, parameters);
        return resultBean.getErrorCode() == ErrorCode.SUCCESS.value ?
                KCBPConverter.convertToTrade(accountId, (List<List<String>>) resultBean.getData(), entrustIdManager) : Lists.newArrayList();
    }

    private List<Trade> getAllTrades(String accountId) {
        List<Trade> res = new ArrayList<>();
        res.addAll(getTrades(accountId));
        res.addAll(getFixedIncomeTrades(accountId));
        res.addAll(getProtocolBack_SZ_Trades(accountId));
        res.addAll(getProtocolBack_SH_Trades(accountId));
        return res;
    }

    private List<Trade> getTrades(String accountId) {
        return do_getTrade(KCBP_FUNCTION_CODE_QRY_TRADE, accountId, "1", "");
    }

    private List<Trade> getFixedIncomeTrades(String accountId) {
        return do_getTrade("460003", accountId, "1", formatBeginDate.format(new Date(System.currentTimeMillis())));
    }

    private List<Trade> getProtocolBack_SZ_Trades(String accountId) {
        return do_getTrade("411003", accountId, "", formatBeginDate.format(new Date(System.currentTimeMillis())));
    }

    private List<Trade> getProtocolBack_SH_Trades(String accountId) {
        return do_getTrade("411007", accountId, "", "");
    }

    private List<IntegratedEntrust> do_getIntegratedEntrust(String funcCode, String accountId, String qryFlag, String beginDate) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(accountId);
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, funcCode, accountId,
                "", qryFlag, beginDate);
        ResultBean resultBean = sendCommand(funcCode, parameters);
        List<Entrust> entrustsList = resultBean.getErrorCode() == ErrorCode.SUCCESS.value ?
                KCBPConverter.convertToEntrust(accountId, (List<List<String>>) resultBean.getData(), entrustIdManager) : Lists.newArrayList();
        List<IntegratedEntrust> res = Lists.newArrayListWithExpectedSize(entrustsList.size());
        entrustsList.forEach(e -> {
            IntegratedEntrust integratedEntrust = new IntegratedEntrust();
            BeanUtils.copyProperties(e, integratedEntrust);
            res.add(integratedEntrust);
        });
        return res;
    }

    private List<IntegratedEntrust> getProtocolBack_SH_Entrust(String accountId) {
        return do_getIntegratedEntrust("411006", accountId, "", formatBeginDate.format(new Date(System.currentTimeMillis())));
    }

    private List<IntegratedEntrust> getProtocolBack_SZ_Entrust(String accountId) {
        return do_getIntegratedEntrust("411002", accountId, "", formatBeginDate.format(new Date(System.currentTimeMillis())));
    }

    private List<IntegratedEntrust> getFixedIncomeEntrust(String accountId) {
        return do_getIntegratedEntrust("460001", accountId, "1", "");
    }

    private List<Trade> getAllReissueTrades(String accountId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(accountId);
        List<Trade> res = new ArrayList<>();
        res.addAll(queryIncrementTrade(kcbpAccount, true));
        res.addAll(queryFixedIncomeTrade(kcbpAccount, true));
        res.addAll(queryProtocolBack_SZ_Trade(kcbpAccount, true));
        res.addAll(queryProtocolBack_SH_Trade(kcbpAccount, true));
        return res;
    }

    private List<Entrust> getReissueEntrusts(String accountId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(accountId);
        return queryIncrementEntrust(kcbpAccount, true);
    }

    private List<IntegratedEntrust> getAllReissueIntegratedEntrust(String accountId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(accountId);
        List<IntegratedEntrust> res = new ArrayList<>();
        res.addAll(queryFixedIncomeEntrust(kcbpAccount, true));
        res.addAll(queryProtocolBack_SH_Entrust(kcbpAccount, true));
        res.addAll(queryProtocolBack_SZ_Entrust(kcbpAccount, true));
        return res;
    }

    public void reqReissueEntrust(String accountId, Long requestId) {
        Map<String, EntrustInterface> entrustInterfaceMap = new HashMap<>();
        List<Entrust> entrustList = getReissueEntrusts(accountId);
        if (Objects.isNull(entrustList)) {
            logger.info("reqQryEntrust empty, accountId = {}", accountId);
            entrustList = new ArrayList<>();
        }
        List<IntegratedEntrust> integratedEntrustList = getAllReissueIntegratedEntrust(accountId);
        entrustList.forEach(res -> {
            if (!Strings.isNullOrEmpty(res.getId())) {
                entrustInterfaceMap.put(res.getId(), res);
            }
        });
        integratedEntrustList.forEach(res -> {
            if (!Strings.isNullOrEmpty(res.getId())) {
                entrustInterfaceMap.put(res.getId(), res);
            }
        });
        kcbpTradeResponse.resQueryReissueEntrust(accountId, entrustInterfaceMap, requestId);
    }


    public void reqReissueTrade(String accountId, Long requestId) {
        List<Trade> tradeList = getAllReissueTrades(accountId);
        kcbpTradeResponse.resQueryReissueTrade(accountId, tradeList, requestId);
    }


    public void entrust(Entrust entrust, Long requestId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(entrust.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        parameters.put("funcid", KCBP_FUNCTION_CODE_ENTRUST);
        //        parameters.put("funcid",KCBP_FUNCTION_CODE_ENTRUST_SH_FIXATION);
        // 下单  1上海:B880515847 0深圳:0800109948
        if (entrust.getMarket().equals(Market.SHSE.value)) {
            parameters.put("secuid", kcbpAccount.getSecuIds().get("1"));
            parameters.put("market", "1");
        } else if (entrust.getMarket().equals(Market.SZSE.value)) {
            parameters.put("secuid", kcbpAccount.getSecuIds().get("0"));
            parameters.put("market", "0");
        } else {
            logger.info("unknown market {}", entrust.getMarket());
        }
        parameters.put("stkcode", StockCodeUtils.getStockCode(entrust.getStockCode()));
        parameters.put("bsflag", entrust.getBsFlag());
        parameters.put("price", String.valueOf(entrust.getEntrustPrice()));
        parameters.put("qty", String.valueOf(entrust.getEntrustVolume()));
        parameters.put("ordergroup", entrust.getLocalEntrustId());
        parameters.put("bankcode", "0");
        parameters.put("remark", "");
        logger.info("sendCommand parameters {}", JsonUtils.getJsonFromObject(parameters));
        logger.info("SecuId parameters {}", JsonUtils.getJsonFromObject(kcbpAccount.getSecuIds()));
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_ENTRUST, parameters);
        logger.info("sendCommand entrust result {}", JsonUtils.getJsonFromObject(resultBean));
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            KCBPConverter.convertToEntrustResponse(entrust, (List<List<String>>) resultBean.getData());
            logger.info("success resultToEntrust {}", JsonUtils.getJsonFromObject(entrust));
        } else {
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorCode(resultBean.getErrorCode());
            entrust.setErrorMsg(resultBean.getErrorMsg());
            logger.info("entrust fail error message {}", entrust.getErrorMsg());
        }
        kcbpTradeResponse.resEntrust(entrust);
    }

    //大宗，协议回购
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        logger.info("API start handle integratedEntrust {} --- status {} ", integratedEntrust.getId(), integratedEntrust.getEntrustStatus());
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(integratedEntrust.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        parameters.put("bsflag", integratedEntrust.getBsFlag());
        // 下单  1上海:B880515847 0深圳:0800109948
        if (integratedEntrust.getMarket().equals(Market.SHSE.value)) {
            parameters.put("secuid", kcbpAccount.getSecuIds().get("1"));
            parameters.put("market", "1");
            // 上交所固收平台业务
            //委托买卖
            if (IntegratedEntrustUtils.isFixedIncome_BS(integratedEntrust.getEntrustProp())) {
                processFixedIncomeBS(integratedEntrust, parameters);
            }
            //报价申报
            if (IntegratedEntrustUtils.isFixedIncome_BaoJia(integratedEntrust.getEntrustProp())) {
                processFixedIncomeBaoJia(integratedEntrust, parameters);
            }
            //成交申报
            if (IntegratedEntrustUtils.isFixedIncome_ChenJiao(integratedEntrust.getEntrustProp())) {
                processFixedIncomeChenJiao(integratedEntrust, parameters);
            }
            // 质押式协议回购
            //上海债券质押式协议回购  意向申报
            if (IntegratedEntrustUtils.isProtocolBack_SH_YiXiang(integratedEntrust.getEntrustProp())) {
                processProtocolBackSHYiXiang(integratedEntrust, parameters);
            }
            //上海债券质押式协议回购  报价申报
            if (IntegratedEntrustUtils.isProtocolBack_SH_BaoJia(integratedEntrust.getEntrustProp())) {
                processProtocolBack_SH_BaoJia(integratedEntrust, parameters);
            }
            //上海债券质押式协议回购  成交申报
            if (IntegratedEntrustUtils.isProtocolBack_SH_ChenJiao(integratedEntrust.getEntrustProp())) {
                processProtocolBack_SH_ChenJiao(integratedEntrust, parameters);
            }
        } else if (integratedEntrust.getMarket().equals(Market.SZSE.value)) {
            parameters.put("secuid", kcbpAccount.getSecuIds().get("0"));
            parameters.put("market", "0");
            //深证债券质押式协议回购
            if (IntegratedEntrustUtils.isProtocolBack_SZ(integratedEntrust.getEntrustProp())) {
                processProtocolBack_SZ(integratedEntrust, parameters);
            }else if(EntrustProp.SZ_XSCJ_XD.value.equals(integratedEntrust.getEntrustProp())) {
            	processBondXscjBS(integratedEntrust, parameters);
            }else if(EntrustProp.SZ_XSCJ_JS.value.equals(integratedEntrust.getEntrustProp())
            		|| EntrustProp.SZ_XSCJ_JJ.value.equals(integratedEntrust.getEntrustProp())) {
            	processBondXscjHF(integratedEntrust, parameters);
            }
        } else {
            logger.info("unknown market {}", integratedEntrust.getMarket());
        }
        // 大宗交易
        if (IntegratedEntrustUtils.isBulk(integratedEntrust.getEntrustProp())) {
            processBulk(integratedEntrust, parameters);
        }
        integratedEntrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
        logger.info("sendCommand parameters {}", JsonUtils.getJsonFromObject(parameters));
        ResultBean resultBean = sendCommand(parameters.get("funcid"), parameters);
        logger.info("API start finish integratedEntrust returnBean {}", JsonUtils.getJsonFromObject(resultBean));
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            KCBPConverter.convertToIntegratedEntrustResponse(integratedEntrust, (List<List<String>>) resultBean.getData());
            logger.info("API convert integratedEntrust {}", JsonUtils.getJsonFromObject(integratedEntrust));
        } else {
            integratedEntrust.setErrorCode(resultBean.getErrorCode());
            integratedEntrust.setErrorMsg(resultBean.getErrorMsg());
        }
        kcbpTradeResponse.resIntegratedEntrust(integratedEntrust);
    }

    private void processBulk(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start Bulk  {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", KCBP_FUNCTION_CODE_ENTRUST);
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode()));
        // 深证成交申报为 配对买卖 点击成交申报为 确认买卖
        if (EntrustProp.CONFIRM.value.equals(integratedEntrust.getEntrustProp())
                && Market.SZSE.value.equals(StockCodeUtils.getMarket(integratedEntrust.getStockCode()))) {
            if (EntrustBs.BUY.value.equals(integratedEntrust.getEntrustBs())) {
                parameters.put("bsflag", BSFlag.PDMR.value);
            } else {
                parameters.put("bsflag", BSFlag.PDMC.value);
            }
        } else if (EntrustProp.MUTUAL_TRADE.value.equals(integratedEntrust.getEntrustProp())
                && Market.SZSE.value.equals(StockCodeUtils.getMarket(integratedEntrust.getStockCode()))) {
            if (EntrustBs.BUY.value.equals(integratedEntrust.getEntrustBs())) {
                parameters.put("bsflag", BSFlag.QRMR.value);
            } else {
                parameters.put("bsflag", BSFlag.QRMC.value);
            }
        }
        parameters.put("price", String.valueOf(integratedEntrust.getEntrustPrice()));
        parameters.put("qty", String.valueOf(integratedEntrust.getEntrustVolume()));
        parameters.put("bankcode", "0");
        parameters.put("remark", "");
        parameters.put("linkmarket", "");//关联市场
        parameters.put("ordergroup", integratedEntrust.getLocalEntrustId());
        if (!Strings.isNullOrEmpty(integratedEntrust.getContractId()))
            parameters.put("promiseno", integratedEntrust.getContractId());//综合协议约定号
        if (!Strings.isNullOrEmpty(integratedEntrust.getPropSeatNo()))
            parameters.put("targetseat", integratedEntrust.getPropSeatNo());//对方席位
        if (!Strings.isNullOrEmpty(integratedEntrust.getLiaisonName()))
            parameters.put("linkman", integratedEntrust.getLiaisonName());//联系人
        if (!Strings.isNullOrEmpty(integratedEntrust.getLiaisonTel()))
            parameters.put("linkway", integratedEntrust.getLiaisonTel());//联系方式
        if (EntrustProp.LARGE_AFTER_CLOSING_PRICE.value.equals(integratedEntrust.getEntrustProp()))
            parameters.put("pricetype", "1");
        if (EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value.equals(integratedEntrust.getEntrustProp()))
            parameters.put("pricetype", "2");
    }

    private void processProtocolBack_SZ(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start isProtocolBack_SZ {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", KCBP_FUNCTION_CODE_PROTOCOL_BACK_SZ);
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode()));
        parameters.put("orderqty", String.valueOf(integratedEntrust.getEntrustVolume()));//委托数量
        if (EntrustProp.SZ_REPURCHASE_RENEWAL.value.equals(integratedEntrust.getEntrustProp())) {//深到期续做为续做利息
            //利息按照回购金额*回购利率*占款天数÷365计算得到
            double orderamt = integratedEntrust.getEntrustBalance().doubleValue() * integratedEntrust.getEntrustPrice().doubleValue() * integratedEntrust.getCompactTerm() / 365.0000;
            parameters.put("orderamt", String.valueOf(orderamt));
        } else {
            parameters.put("orderamt", String.valueOf(integratedEntrust.getEntrustBalance()));//融资金额
        }
        // 借用字段
        parameters.put("ghdays", String.valueOf(integratedEntrust.getCompactTerm()));//产品期限
        parameters.put("duerate", String.valueOf(integratedEntrust.getEntrustPrice()));//年利率
        parameters.put("targetseat", String.valueOf(integratedEntrust.getPropSeatNo()));//对方交易单元
        parameters.put("confernum", String.valueOf(integratedEntrust.getContractId()));//约定号
        parameters.put("zqzysno", "");//初始合约号
        if (EntrustProp.SZ_REPURCHASE_RENEWAL.value.equals(integratedEntrust.getEntrustProp())
                || EntrustProp.SZ_REPURCHASE_BUYBACK.value.equals(integratedEntrust.getEntrustProp())) {
            parameters.put("zqzysno", integratedEntrust.getOrigSerialNo());//初始合约号
        }
        parameters.put("risksignsno", "");//风险揭示签署流水号
    }
    
    private void processBondXscjBS(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start xscj_BS  {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", "410677"); // 功能： 新债协商成交买卖交易
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode())); //证券代码
        parameters.put("ownertype", "101");// 报价所有者类型  TODO需要确定这个值怎么取
        parameters.put("orderprice", String.valueOf(integratedEntrust.getEntrustPrice()));// 价格
        parameters.put("orderqty", String.valueOf(integratedEntrust.getEntrustVolume()));// 数量
        
        parameters.put("tradercode", String.valueOf(integratedEntrust.getContractId()));//本方交易员代码  TODO需要确定这个值怎么取
        parameters.put("investorid", String.valueOf(integratedEntrust.getContractId()));//本方交易主体代码  TODO需要确定这个值怎么取
        parameters.put("cpmemberid", String.valueOf(integratedEntrust.getContractId()));//对手方交易商代码  TODO需要确定这个值怎么取
        parameters.put("cptradercode", String.valueOf(integratedEntrust.getContractId()));//对手方交易员代码  TODO需要确定这个值怎么取
        parameters.put("cpinvestorid", String.valueOf(integratedEntrust.getContractId()));//对手方交易主体代码  TODO需要确定这个值怎么取
        parameters.put("confirmid", String.valueOf(integratedEntrust.getContractId()));//约定号  TODO需要确定这个值怎么取
        parameters.put("memo", String.valueOf(integratedEntrust.getContractId()));//备注  TODO需要确定这个值怎么取
        parameters.put("risksignsno", "");//风险揭示签署流水号
    }
    
    private void processBondXscjHF(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start xscj_HF  {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", "410678"); // 功能： 新债协商成交买卖交易
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode())); //证券代码
        parameters.put("trdreportid", "101");// 成交申报请求编号  TODO需要确定这个值怎么取
        parameters.put("ownertype", "101");// 报价所有者类型  TODO需要确定这个值怎么取
        parameters.put("orderprice", String.valueOf(integratedEntrust.getEntrustPrice()));// 价格
        parameters.put("orderqty", String.valueOf(integratedEntrust.getEntrustVolume()));// 数量
        
        parameters.put("tradercode", String.valueOf(integratedEntrust.getContractId()));//本方交易员代码  TODO需要确定这个值怎么取
        parameters.put("investorid", String.valueOf(integratedEntrust.getContractId()));//本方交易主体代码  TODO需要确定这个值怎么取
        parameters.put("cpmemberid", String.valueOf(integratedEntrust.getContractId()));//对手方交易商代码  TODO需要确定这个值怎么取
        parameters.put("cptradercode", String.valueOf(integratedEntrust.getContractId()));//对手方交易员代码  TODO需要确定这个值怎么取
        parameters.put("cpinvestorid", String.valueOf(integratedEntrust.getContractId()));//对手方交易主体代码  TODO需要确定这个值怎么取
        parameters.put("cpinvestortype", String.valueOf(integratedEntrust.getContractId()));//对手方交易主体类型  TODO需要确定这个值怎么取
        parameters.put("confirmid", String.valueOf(integratedEntrust.getContractId()));//约定号  TODO需要确定这个值怎么取
        parameters.put("memo", String.valueOf(integratedEntrust.getContractId()));//备注  TODO需要确定这个值怎么取
        parameters.put("risksignsno", "");//风险揭示签署流水号
    }

    private void processProtocolBack_SH_ChenJiao(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start isProtocolBack_SH_ChenJiao {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", KCBP_FUNCTION_CODE_PROTOCOL_BACK_SH_TRADE);
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode()));
        parameters.put("orderqty", String.valueOf(integratedEntrust.getEntrustVolume()));//委托数量
        if (EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(integratedEntrust.getEntrustProp())
                || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(integratedEntrust.getEntrustProp())) {
            if (PledgeeType.UN_THIRD_PARTY.value.equals(integratedEntrust.getTransCode())) {
                parameters.put("orderRestrictions", "N");//到期续作类型 N:非第三方续做  Y:第三方续做
            } else {
                parameters.put("orderRestrictions", "Y");//到期续作类型 N:非第三方续做  Y:第三方续做
            }
        }
        parameters.put("companyoperid", integratedEntrust.getSellerNo());//本方交易员代码
        // 借用字段
        parameters.put("taroperid", integratedEntrust.getLiaisonTel());//对手方交易员代码
        parameters.put("tarcompany", integratedEntrust.getPropSeatNo());//对手方交易商代码
        parameters.put("tradedate", integratedEntrust.getFirstSettleDate());//初始交易日期
        if (StringUtil.isNotEmpty(integratedEntrust.getOrigSerialNo())) {
            parameters.put("tradematchcode", integratedEntrust.getOrigSerialNo());//成交编号
        } else {
            parameters.put("tradematchcode", "0");//成交编号
        }
        parameters.put("quoterefid", integratedEntrust.getContractId());//交易所请求编号
        // F:成交申报、 X:到期续做、 C:解除质押、 H:换券申报、 Z:提前终止
        parameters.put("ordtype", KCBPConverter.getOrdtype(integratedEntrust.getEntrustProp()));//报价申报类型
        parameters.put("pledgee", integratedEntrust.getPortfolioId());//质权人
        parameters.put("risksignsno", "");//风险揭示签署流水号
    }

    private void processProtocolBack_SH_BaoJia(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start ProtocolBack_SH_BaoJia  {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", KCBP_FUNCTION_CODE_PROTOCOL_BACK_SH_BAOJIA);
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode()));
        // 初始交易2C 换券申报2Q 填写
        parameters.put("orderqty", String.valueOf(integratedEntrust.getEntrustVolume()));//委托数量
        //初始交易2C、到期续做2K 填写
        //
        parameters.put("orderamt", String.valueOf(integratedEntrust.getEntrustBalance()));//融资金额
        // 初始交易2C、到期续做2K 填写
        parameters.put("ghdays", String.valueOf(integratedEntrust.getCompactTerm()));//回购天数
        // 初始交易2C、到期续做2K、 提前终止融入资金2M  提前终止融出资金2N 需填写
        // 借用字段
        parameters.put("duerate", String.valueOf(integratedEntrust.getEntrustPrice()));//年利率
        if (EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(integratedEntrust.getEntrustProp())) {
            if (PledgeeType.UN_THIRD_PARTY.value.equals(integratedEntrust.getTransCode())) {
                parameters.put("orderRestrictions", "N");//到期续作类型 N:非第三方续做  Y:第三方续做
            } else {
                parameters.put("orderRestrictions", "Y");//到期续作类型 N:非第三方续做  Y:第三方续做
            }
        }
        // 借用liaisonTel
        parameters.put("companyoperid", integratedEntrust.getSellerNo());//本方交易员代码
        // ContractId
        parameters.put("taroperid", integratedEntrust.getLiaisonTel());//对手方交易员代码
        // PropSeatNo
        parameters.put("tarcompany", integratedEntrust.getPropSeatNo());//对手方交易商代码
        // FirstSettleDate
        parameters.put("tradedate", integratedEntrust.getFirstSettleDate());//初始交易日期
        // OrigSerialNo
        if (!Strings.isNullOrEmpty(integratedEntrust.getOrigSerialNo())) {
            parameters.put("tradematchcode", integratedEntrust.getOrigSerialNo());//成交编号
        }
        if (!Strings.isNullOrEmpty(integratedEntrust.getRemark()))
            parameters.put("remark", integratedEntrust.getRemark());//补充条款
        parameters.put("risksignsno", "");//风险揭示签署流水号
    }

    private void processProtocolBackSHYiXiang(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start ProtocolBack_SH_YiXiang  {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", KCBP_FUNCTION_CODE_PROTOCOL_BACK_SH);
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode()));
        parameters.put("orderqty", String.valueOf(integratedEntrust.getEntrustVolume()));//委托数量
        // 借用字段 entrustBalance
        parameters.put("orderamt", String.valueOf(integratedEntrust.getEntrustBalance()));//融资金额
        parameters.put("ghdays", String.valueOf(integratedEntrust.getCompactTerm()));//回购天数
        parameters.put("duerate", String.valueOf(integratedEntrust.getEntrustPrice()));//年利率
        // 该字段不确定
        parameters.put("begindate", integratedEntrust.getFirstSettleDate());//交易日期
        // 借用字段 liaisonTel
        parameters.put("companyoperid", integratedEntrust.getSellerNo());//交易员代码
        if (!Strings.isNullOrEmpty(integratedEntrust.getRemark())) {
            parameters.put("remark", integratedEntrust.getRemark());//补充条款
        }
        parameters.put("risksignsno", "");//风险揭示签署流水号
    }

    private void processFixedIncomeChenJiao(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start FixedIncome_ChenJiao  {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", "460014"); // 功能： 固收平台确定报价报价申报
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode())); //证券代码
        parameters.put("orderprice", String.valueOf(integratedEntrust.getEntrustPrice()));// 委托价格
        parameters.put("orderqty", String.valueOf(integratedEntrust.getEntrustVolume()));// 委托数量
        // 字段借用  点击成交时必填
        if (!Strings.isNullOrEmpty(integratedEntrust.getContractId()))
            parameters.put("quoterefid", integratedEntrust.getContractId());//交易所订单编号
        // 借用liaisonTel 字段
        parameters.put("traderid", integratedEntrust.getLiaisonTel());//交易员代码
        if (!Strings.isNullOrEmpty(integratedEntrust.getPropSeatNo()))
            parameters.put("targetcompany", integratedEntrust.getPropSeatNo());//对手方交易商代码
        // 调用411017获取
        parameters.put("risksignsno", "");//风险揭示签署流水号
    }

    private void processFixedIncomeBaoJia(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start FixedIncome_BaoJia  {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", "460013"); // 功能： 固收平台确定报价报价申报
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode())); //证券代码
        if (EntrustBs.BUY.value.equals(integratedEntrust.getEntrustBs())) {
            parameters.put("orderbuyprice", String.valueOf(integratedEntrust.getEntrustPrice()));// 委托买入价格
            parameters.put("orderbuyqty", String.valueOf(integratedEntrust.getEntrustVolume()));// 委托买入数量
        } else {
            parameters.put("ordersaleprice", String.valueOf(integratedEntrust.getEntrustPrice()));//委托卖出价格
            parameters.put("ordersaleqty", String.valueOf(integratedEntrust.getEntrustVolume()));//委托卖出数量
        }
        // 借用字段 discount
        parameters.put("displayqty", String.valueOf(integratedEntrust.getDiscount()));//冰山订单数量
        // 1:匿名  0:具名
        parameters.put("trdanonymous", "1");//匿名报价
        // 5:做市报价  0:非做市报价
        parameters.put("orderrestric", "0");//做市报价
        parameters.put("traderid", integratedEntrust.getLiaisonTel());//交易员代码
        // 调用411017获取
        parameters.put("risksignsno", "");//风险揭示签署流水号
    }

    private void processFixedIncomeBS(IntegratedEntrust integratedEntrust, Map<String, String> parameters) {
        logger.info("start FixedIncome_BS  {}", integratedEntrust.getBsFlag());
        parameters.put("funcid", "460010"); // 功能： 固收平台委托买卖交易
        parameters.put("stkcode", StockCodeUtils.getStockCode(integratedEntrust.getStockCode())); //证券代码
        parameters.put("price", String.valueOf(integratedEntrust.getEntrustPrice()));// 价格
        parameters.put("qty", String.valueOf(integratedEntrust.getEntrustVolume()));// 数量
        parameters.put("confernum", String.valueOf(integratedEntrust.getContractId()));//约定号
        // 2X,2Y 业务必填
        parameters.put("targettrader", integratedEntrust.getLiaisonName());//对手方交易员
        // 暂时填充 118
        parameters.put("targetcompany", integratedEntrust.getPropSeatNo());//对手方交易商
        // 借用LiaisonTel
        parameters.put("traderid", integratedEntrust.getLiaisonTel());//发起方交易员
        //  协议交易业务4J,4K 时必须输入
        // 1 客户录入  2 交易商录入
        parameters.put("accounttype", "2");//录入类型
        parameters.put("supplemental", "");//补充协议
        parameters.put("risksignsno", "");//风险揭示签署流水号
    }
    
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        cancelEntrustBean.setAgentType(AgentType.KINGDOM_KCBP.value);
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(cancelEntrustBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        parameters.put("funcid", KCBP_FUNCTION_CODE_CANCEL_ENTRUST);
        parameters.put("orderdate", cancelEntrustBean.getTradingDay());
        parameters.put("ordersno", cancelEntrustBean.getBrokerEntrustId());
        parameters.put("bsflag", cancelEntrustBean.getBsFlag());
        logger.info("cancelEntrust:{}-{}", cancelEntrustBean.getId(), cancelEntrustBean.getBrokerEntrustId());
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_CANCEL_ENTRUST, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            KCBPConverter.convertToCancelEntrustResponse(cancelEntrustBean, (List<List<String>>) resultBean.getData());
        } else {
            cancelEntrustBean.setErrorCode(resultBean.getErrorCode());
            cancelEntrustBean.setErrorMsg(resultBean.getErrorMsg());
        }
        kcbpTradeResponse.resCancelEntrust(cancelEntrustBean);
    }

    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        cancelEntrustBean.setAgentType(AgentType.KINGDOM_KCBP.value);
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(cancelEntrustBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        if (IntegratedEntrustUtils.isProtocolBack_SZ(cancelEntrustBean.getEntrustProp())) {
            parameters.put("funcid", "411001");
        }
        if (IntegratedEntrustUtils.isProtocolBack_SH(cancelEntrustBean.getEntrustProp())) {
            parameters.put("funcid", "411025");
        }
        if (IntegratedEntrustUtils.isFixedIncome(cancelEntrustBean.getEntrustProp())) {
            parameters.put("funcid", "460011");
        }
        if (IntegratedEntrustUtils.isBulk(cancelEntrustBean.getEntrustProp())) {
            parameters.put("funcid", KCBP_FUNCTION_CODE_CANCEL_ENTRUST);
        }
        parameters.put("orderdate", cancelEntrustBean.getTradingDay());
        parameters.put("ordersno", cancelEntrustBean.getBrokerEntrustId());
        parameters.put("bsflag", cancelEntrustBean.getBsFlag());
        parameters.put("market", cancelEntrustBean.getMarket());
        logger.info("cancelIntegratedEntrust:{}-{}", cancelEntrustBean.getId(), cancelEntrustBean.getBrokerEntrustId());
        ResultBean resultBean = sendCommand(parameters.get("funcid"), parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            KCBPConverter.convertToCancelIntegratedEntrustEntrustResponse(cancelEntrustBean, (List<List<String>>) resultBean.getData());
        } else {
            cancelEntrustBean.setErrorCode(resultBean.getErrorCode());
            cancelEntrustBean.setErrorMsg(resultBean.getErrorMsg());
        }
        kcbpTradeResponse.resCancelIntegratedEntrust(cancelEntrustBean);
    }

    public void queryIncrementTrade(KCBPAccount kcbpAccount) {
        queryIncrementTrade(kcbpAccount, false);
    }

    public List<Trade> queryIncrementTrade(KCBPAccount kcbpAccount, boolean reissueFlag) {
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, KCBP_FUNCTION_CODE_QRY_TRADE, kcbpAccount.getAccountId(),
                kcbpAccount.getTradePositionStr(), "1", "");
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_QRY_TRADE, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            List<Trade> trades = KCBPConverter.convertToTrade(kcbpAccount.getAccountId(), data, entrustIdManager);
            if (!reissueFlag) {
                trades.forEach(trade -> kcbpTradeClient.onTrade(trade));
            }
            if (data.size() > 1) {
                String newPositionStr = data.get(data.size() - 1).get(0);
                kcbpAccount.setTradePositionStr(newPositionStr);
                logger.info("queryIncrementTrade newPositionStr :{}", newPositionStr);
            }
            return trades;
        }
        return new ArrayList<>();
    }

    public void queryProtocolBack_SZ_Trade(KCBPAccount kcbpAccount) {
        queryProtocolBack_SZ_Trade(kcbpAccount, false);
    }

    public List<Trade> queryProtocolBack_SZ_Trade(KCBPAccount kcbpAccount, boolean reissueFlag) {
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, "411003", kcbpAccount.getAccountId(),
                kcbpAccount.getProtocolBack_SZ_PositionStr_trade(), "", formatBeginDate.format(new Date(System.currentTimeMillis())));
        ResultBean resultBean = sendCommand("411003", parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            List<Trade> trades = KCBPConverter.convertToTrade(kcbpAccount.getAccountId(), data, entrustIdManager);
            if (!reissueFlag) {
                trades.forEach(trade -> kcbpTradeClient.onTrade(trade));
            }
            if (data.size() > 1) {
                int positionStr = data.get(0).size() - 1;
                String newPositionStr = data.get(data.size() - 1).get(positionStr);
                kcbpAccount.setProtocolBack_SZ_PositionStr_trade(newPositionStr);
                logger.info("queryIncrementProtocolBack_SZ_Trade newPositionStr :{}", newPositionStr);
            }
            return trades;
        }
        return new ArrayList<>();
    }

    public void queryProtocolBack_SH_Trade(KCBPAccount kcbpAccount) {
        queryProtocolBack_SH_Trade(kcbpAccount, false);
    }

    public List<Trade> queryProtocolBack_SH_Trade(KCBPAccount kcbpAccount, boolean reissueFlag) {
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, "411007", kcbpAccount.getAccountId(),
                kcbpAccount.getProtocolBack_SH_PositionStr_trade(), "", formatBeginDate.format(new Date(System.currentTimeMillis())));
        ResultBean resultBean = sendCommand("411007", parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            List<Trade> trades = KCBPConverter.convertToTrade(kcbpAccount.getAccountId(), data, entrustIdManager);
            if (!reissueFlag) {
                trades.forEach(trade -> kcbpTradeClient.onTrade(trade));
            }
            if (data.size() > 1) {
                int positionStr = data.get(0).size() - 1;
                String newPositionStr = data.get(data.size() - 1).get(positionStr);
                kcbpAccount.setProtocolBack_SH_PositionStr_trade(newPositionStr);
                logger.info("queryIncrementProtocolBack_SH_Trade newPositionStr:{}", newPositionStr);
            }
            return trades;
        }
        return new ArrayList<>();
    }

    public void queryFixedIncomeTrade(KCBPAccount kcbpAccount) {
        queryFixedIncomeTrade(kcbpAccount, false);
    }

    public List<Trade> queryFixedIncomeTrade(KCBPAccount kcbpAccount, boolean reissueFlag) {
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, "460003", kcbpAccount.getAccountId(),
                kcbpAccount.getFixedIncome_PositionStr_trade(), "1", "");
        ResultBean resultBean = sendCommand("460003", parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            List<Trade> trades = KCBPConverter.convertToTrade(kcbpAccount.getAccountId(), data, entrustIdManager);
            if (!reissueFlag) {
                trades.forEach(trade -> kcbpTradeClient.onTrade(trade));
            }
            if (data.size() > 1) {
                int positionStr = data.get(0).size() - 1;
                String newPositionStr = data.get(data.size() - 1).get(positionStr);
                kcbpAccount.setFixedIncome_PositionStr_trade(newPositionStr);
                logger.info("queryIncrementFixedIncomeTrade newPositionStr :{}", newPositionStr);
            }
            return trades;
        }
        return new ArrayList<>();
    }

    public void queryIncrementEntrust(KCBPAccount kcbpAccount) {
        queryIncrementEntrust(kcbpAccount, false);
    }

    public List<Entrust> queryIncrementEntrust(KCBPAccount kcbpAccount, boolean reissueFlag) {
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, KCBP_FUNCTION_CODE_QRY_ENTRUST, kcbpAccount.getAccountId(),
                kcbpAccount.getEntrustPositionStr(), "1", "");
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_QRY_ENTRUST, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            List<Entrust> entrustList = KCBPConverter.convertToEntrust(kcbpAccount.getAccountId(), data, entrustIdManager);
            if (!reissueFlag) {
                entrustList.forEach(entrust -> {
                    if (!Strings.isNullOrEmpty(entrust.getId())) {
                        if (entrust.getId().startsWith(IDPrefix.NORMAL.value))
                            kcbpTradeClient.onEntrust(entrust);
                        else {
                            IntegratedEntrust integratedEntrust = new IntegratedEntrust();
                            BeanUtils.copyProperties(entrust, integratedEntrust);
                            kcbpTradeClient.onIntegratedEntrust(integratedEntrust);
                        }
                    } else {
                        logger.info("kcbp_API queryIncrementEntrust has no entrustId {}", JsonUtils.getJsonFromObject(entrust));
                    }
                });
            }
            if (data.size() > 1) {
                String newPositionStr = data.get(data.size() - 1).get(0);
                kcbpAccount.setEntrustPositionStr(newPositionStr);
                logger.info("queryIncrementEntrust newPositionStr:{}", newPositionStr);
            }
            return entrustList;
        }
        return new ArrayList<>();
    }

    public void queryProtocolBack_SZ_Entrust(KCBPAccount kcbpAccount) {
        queryProtocolBack_SZ_Entrust(kcbpAccount, false);
    }

    public List<IntegratedEntrust> queryProtocolBack_SZ_Entrust(KCBPAccount kcbpAccount, boolean reissueFlag) {
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, "411002", kcbpAccount.getAccountId(),
                kcbpAccount.getProtocolBack_SZ_PositionStr_entrust(), "", formatBeginDate.format(new Date(System.currentTimeMillis())));
        ResultBean resultBean = sendCommand("411002", parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            List<IntegratedEntrust> integratedEntrusts = queryProtocolBackAndFixedIncomeEntrust(data, kcbpAccount, reissueFlag);
            if (data.size() > 1) {
                int positionStr = data.get(0).size() - 1;
                String newPositionStr = data.get(data.size() - 1).get(positionStr);
                kcbpAccount.setProtocolBack_SZ_PositionStr_entrust(newPositionStr);
                logger.info("queryProtocolBack_SZ_Entrust newPositionStr :{}", newPositionStr);
            }
            return integratedEntrusts;
        }
        return new ArrayList<>();
    }

    public void queryProtocolBack_SH_Entrust(KCBPAccount kcbpAccount) {
        queryProtocolBack_SH_Entrust(kcbpAccount, false);
    }

    public List<IntegratedEntrust> queryProtocolBack_SH_Entrust(KCBPAccount kcbpAccount, boolean reissueFlag) {
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, "411006", kcbpAccount.getAccountId(),
                kcbpAccount.getProtocolBack_SH_PositionStr_entrust(), "", formatBeginDate.format(new Date(System.currentTimeMillis())));
        ResultBean resultBean = sendCommand("411006", parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            List<IntegratedEntrust> integratedEntrusts = queryProtocolBackAndFixedIncomeEntrust(data, kcbpAccount, reissueFlag);
            if (data.size() > 1) {
                int positionStr = data.get(0).size() - 1;
                String newPositionStr = data.get(data.size() - 1).get(positionStr);
                kcbpAccount.setProtocolBack_SH_PositionStr_entrust(newPositionStr);
                logger.info("queryProtocolBack_SH_Entrust newPositionStr :{}", newPositionStr);
            }
            return integratedEntrusts;
        }
        return new ArrayList<>();
    }

    public void queryFixedIncomeEntrust(KCBPAccount kcbpAccount) {
        queryFixedIncomeEntrust(kcbpAccount, false);
    }

    public List<IntegratedEntrust> queryFixedIncomeEntrust(KCBPAccount kcbpAccount, boolean reissueFlag) {
        Map<String, String> parameters = composeOrderTradeQuery(kcbpAccount, "460001", kcbpAccount.getAccountId(),
                kcbpAccount.getFixedIncome_PositionStr_entrust(), "1", "");
        ResultBean resultBean = sendCommand("460001", parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            List<IntegratedEntrust> integratedEntrusts = queryProtocolBackAndFixedIncomeEntrust(data, kcbpAccount, reissueFlag);
            if (data.size() > 1) {
                int positionStr = data.get(0).size() - 1;
                String newPositionStr = data.get(data.size() - 1).get(positionStr);
                kcbpAccount.setFixedIncome_PositionStr_entrust(newPositionStr);
                logger.info("queryFixedIncomeEntrust newPositionStr :{}", newPositionStr);
            }
            return integratedEntrusts;
        }
        return new ArrayList<>();
    }

    private List<IntegratedEntrust> queryProtocolBackAndFixedIncomeEntrust(List<List<String>> data, KCBPAccount kcbpAccount, boolean reissueFlag) {
        List<IntegratedEntrust> integratedEntrusts = new ArrayList<>();
        List<Entrust> entrustList = KCBPConverter.convertToEntrust(kcbpAccount.getAccountId(), data, entrustIdManager);
        entrustList.forEach(entrust -> {
            IntegratedEntrust integratedEntrust = new IntegratedEntrust();
            BeanUtils.copyProperties(entrust, integratedEntrust);
            integratedEntrusts.add(integratedEntrust);
            if (!reissueFlag) {
                kcbpTradeClient.onIntegratedEntrust(integratedEntrust);
            }
        });
        logger.debug("queryProtocolBackAndFixedIncomeEntrust entrustList:{}", JSONArray.toJSONString(entrustList));
        return integratedEntrusts;
    }

    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, long requestId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(integratedBulkQuoteQryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(integratedBulkQuoteQryBean.getStockCode());
        String market = null;
        if (marketAndStockCode[1].equals(Market.SZSE.value)) {
            parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_SZ);
            market = Market.SZSE.value;
        } else if (marketAndStockCode[1].equals(Market.SHSE.value)) {
            parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_SH);
            market = Market.SHSE.value;
        }
        if (!Strings.isNullOrEmpty(integratedBulkQuoteQryBean.getEntrustProp()) && !Strings.isNullOrEmpty(integratedBulkQuoteQryBean.getEntrustBs())) {
            parameters.put("bsflag", EntrustUtils.getBsFlag(integratedBulkQuoteQryBean.getEntrustProp(), integratedBulkQuoteQryBean.getEntrustBs()));
        }
        parameters.put("stkcode", marketAndStockCode[0]);
        logger.info("parameters {}", JsonUtils.getJsonFromObject(parameters));
        ResultBean resultBean = sendCommand(parameters.get("funcid"), parameters);
        logger.info("queryIntegratedBulkQuote result {}", JsonUtils.getJsonFromObject(resultBean));
        List<IntegratedBulkQuoteBean> integratedBulkQuoteList = null;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            if (data.size() > 0)
                integratedBulkQuoteList = KCBPConverter.convertToIntegratedBulkQuoteResponse(data, market);
        } else
            logger.info("queryIntegratedBulkQuote fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryIntegratedBulkQuote(integratedBulkQuoteList, requestId);
    }

    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, long requestId) {
        List<IntegratedBulkCloseQuoteBean> integratedBulkCloseQuoteBeans = null;
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(integratedBulkCloseQuoteQryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        String market = StockCodeUtils.getMarket(integratedBulkCloseQuoteQryBean.getStockCode());
        if (Market.SHSE.value.equals(market)) {
            //上海盘后固定价格交易行情查询
            parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_CLOSE_SH);
            if (!Strings.isNullOrEmpty(integratedBulkCloseQuoteQryBean.getStockCode()))
                parameters.put("stkcode", StockCodeUtils.getStockCode(integratedBulkCloseQuoteQryBean.getStockCode()));
            parameters.put("count", String.valueOf(integratedBulkCloseQuoteQryBean.getPageSize()));
            parameters.put("posstr", "");
            logger.info("parameters {}", JsonUtils.getJsonFromObject(parameters));
            ResultBean resultBean = sendCommand(parameters.get("funcid"), parameters);
            logger.info("queryIntegratedBulkCloseQuote SH result {}", JsonUtils.getJsonFromObject(resultBean));
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<List<String>> data = (List<List<String>>) resultBean.getData();
                if (data.size() > 0)
                    integratedBulkCloseQuoteBeans = KCBPConverter.convertToIntegratedBulkCloseQuoteResponseSH(integratedBulkCloseQuoteQryBean, data);
            } else
                logger.info("queryIntegratedBulkCloseQuote SH fail {}", resultBean.getErrorMsg());
        } else if (Market.SZSE.value.equals(market)) {
            //深圳盘后固定价格交易行情查询
            parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_CLOSE_SZ);
            if (!Strings.isNullOrEmpty(integratedBulkCloseQuoteQryBean.getStockCode()))
                parameters.put("stkcode", StockCodeUtils.getStockCode(integratedBulkCloseQuoteQryBean.getStockCode()));
            logger.info("parameters {}", JsonUtils.getJsonFromObject(parameters));
            ResultBean resultBean = sendCommand(parameters.get("funcid"), parameters);
            logger.info("queryIntegratedBulkCloseQuote SZ result {}", JsonUtils.getJsonFromObject(resultBean));
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<List<String>> data = (List<List<String>>) resultBean.getData();
                if (data.size() > 0)
                    integratedBulkCloseQuoteBeans = KCBPConverter.convertToIntegratedBulkCloseQuoteResponseSZ(data);
            } else
                logger.info("queryIntegratedBulkCloseQuote SZ fail {}", resultBean.getErrorMsg());
        }
        kcbpTradeResponse.resQueryIntegratedBulkCloseQuote(integratedBulkCloseQuoteBeans, requestId);
    }

    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, long requestId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(integratedRepurchaseQuoteQryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        // 上海债券协议回购非公开报价行情
        if (ActionIn.NON_PUBLIC_QUOTE.value == integratedRepurchaseQuoteQryBean.getActionIn()) {
            parameters.put("funcid", "411027");
            String orderType = integratedRepurchaseQuoteQryBean.getEntrustProp();
            parameters.put("ordtype", KCBPConverter.getOrdtype(orderType));
        }
        if (ActionIn.PUBLIC_QUOTE.value == integratedRepurchaseQuoteQryBean.getActionIn()) {
            parameters.put("funcid", "411029");
        }
        parameters.put("qryrec", String.valueOf(integratedRepurchaseQuoteQryBean.getPageSize()));
        logger.info("parameters {}", JsonUtils.getJsonFromObject(parameters));
        ResultBean resultBean = sendCommand(parameters.get("funcid"), parameters);
        logger.info("queryIntegratedRepurchaseQuote result {}", JsonUtils.getJsonFromObject(resultBean));
        List<IntegratedRepurchaseQuoteBean> integratedRepurchaseQuoteBeans = null;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            if (data.size() > 0)
                integratedRepurchaseQuoteBeans = KCBPConverter.convertToIntegratedRepurchaseQuoteResponse(integratedRepurchaseQuoteQryBean, data);
        } else
            logger.info("queryIntegratedRepurchaseQuote fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryIntegratedRepurchaseQuote(integratedRepurchaseQuoteBeans, requestId);
    }

    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, long requestId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(integratedFixedIncomeQuoteQryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        // 固收平台确定报价行情查询
        parameters.put("funcid", "460018");
        if (!Strings.isNullOrEmpty(integratedFixedIncomeQuoteQryBean.getStockCode()))
            parameters.put("stkcode", StockCodeUtils.getStockCode(integratedFixedIncomeQuoteQryBean.getStockCode()));
        parameters.put("market", "1");
        parameters.put("count", String.valueOf(integratedFixedIncomeQuoteQryBean.getPageSize()));
        parameters.put("posstr", "");
        logger.info("parameters {}", JsonUtils.getJsonFromObject(parameters));
        ResultBean resultBean = sendCommand(parameters.get("funcid"), parameters);
        logger.info("queryIntegratedFixedIncomeQuote result {}", JsonUtils.getJsonFromObject(resultBean));
        List<IntegratedFixedIncomeQuoteBean> integratedFixedIncomeQuoteBeans = null;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            if (data.size() > 0)
                integratedFixedIncomeQuoteBeans = KCBPConverter.convertToIntegratedFixedIncomeQuoteResponse(data);
        } else
            logger.info("queryIntegratedFixedIncomeQuote fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryIntegratedFixedIncomeQuote(integratedFixedIncomeQuoteBeans, requestId);
    }

    public void queryIntegratedQuote(QuoteQryBean quoteQryBean, long requestId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(quoteQryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        if (quoteQryBean.getQuoteType() == QuoteType.BULK_QUOTE.value) {
            String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(quoteQryBean.getStockCode());
            if (marketAndStockCode[1].equals(Market.SZSE.value)) {
                parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_SZ);
                quoteQryBean.setMarket(Market.SZSE.value);
            } else if (marketAndStockCode[1].equals(Market.SHSE.value)) {
                parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_SH);
                quoteQryBean.setMarket(Market.SHSE.value);
            }
            parameters.put("stkcode", marketAndStockCode[0]);
        }
        // 上海债券协议回购非公开报价行情
        if (quoteQryBean.getQuoteType() == QuoteType.NON_PUBLIC_PROTOCOL_BACK_QUOTE.value) {
            parameters.put("funcid", "411027");
            quoteQryBean.setMarket(Market.SHSE.value);
        }
        if (quoteQryBean.getQuoteType() == QuoteType.PUBLIC_PROTOCOL_BACK_QUOTE.value) {
            parameters.put("funcid", "411029");
            quoteQryBean.setMarket(Market.SHSE.value);
        }
        // 固收平台确定报价行情查询
        if (quoteQryBean.getQuoteType() == QuoteType.FIXED_INCOME_QUOTE.value) {
            quoteQryBean.setMarket(Market.SHSE.value);
            parameters.put("funcid", "460018");
            if (!Strings.isNullOrEmpty(quoteQryBean.getStockCode()))
                parameters.put("stkcode", StockCodeUtils.getStockCode(quoteQryBean.getStockCode()));
            parameters.put("market", "1");
            parameters.put("count", "");
            parameters.put("posstr", "");
        }
        if (quoteQryBean.getQuoteType() == QuoteType.AFTER_BULK_QUOTE.value) {
//            parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_CLOSE_SH);
//            if (!Strings.isNullOrEmpty(quoteQryBean.getStockCode()))
//                parameters.put("stkcode", StockCodeUtils.getStockCode(quoteQryBean.getStockCode()));
//            parameters.put("count", "");
//            parameters.put("posstr", "");
            String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(quoteQryBean.getStockCode());
            if (marketAndStockCode[1].equals(Market.SZSE.value)) {
                //深圳盘后固定价格交易行情查询
                parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_CLOSE_SZ);
                quoteQryBean.setMarket(Market.SZSE.value);
            } else if (marketAndStockCode[1].equals(Market.SHSE.value)) {
                //上海盘后固定价格交易行情查询
                parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_INTEGRATED_QUOTE_CLOSE_SH);
                quoteQryBean.setMarket(Market.SHSE.value);
            }
            parameters.put("stkcode", marketAndStockCode[0]);
            parameters.put("count", "");
            parameters.put("posstr", "");
        }
        logger.info("parameters {}", JsonUtils.getJsonFromObject(parameters));
        ResultBean resultBean = sendCommand(parameters.get("funcid"), parameters);
        logger.info("queryIntegratedQuote result {}", JsonUtils.getJsonFromObject(resultBean));
        List<IntegratedQuote> integratedQuoteList = null;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            if (data.size() > 0)
                integratedQuoteList = KCBPConverter.convertToIntegratedQuoteResponse(quoteQryBean, data);
        } else
            logger.info("queryIntegratedQuote fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryIntegratedQuotes(integratedQuoteList, requestId);
    }

    public void queryEnable(EnableQryBean qryBean, long requestId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(qryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(qryBean.getStockCode());
        parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_ENABLE);
        parameters.put("stkcode", marketAndStockCode[0]);
        parameters.put("bsflag", EntrustUtils.getBsFlag(qryBean.getEntrustProp(), qryBean.getEntrustBs()));
        parameters.put("price", String.valueOf(qryBean.getEntrustPrice()));
        if (marketAndStockCode[1].equals(Market.SZSE.value)) {
            parameters.put("market", "0");
            parameters.put("secuid", kcbpAccount.getSecuIds().get("0"));
        } else if (marketAndStockCode[1].equals(Market.SHSE.value)) {
            parameters.put("market", "1");
            parameters.put("secuid", kcbpAccount.getSecuIds().get("1"));
        }
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_QRY_ENABLE, parameters);
        logger.info("queryEnable result {}", JsonUtils.getJsonFromObject(resultBean));
        int enable = -1;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            enable = KCBPConverter.convertToEnable(data);
        } else
            logger.info("queryEnable fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryEnable(enable, requestId);
    }

    public void queryHistoryTrade(HistoryQryBean historyQryBean, long requestId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(historyQryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_HISTORY_TRADE);
        parameters.put("secuid", "");
        parameters.put("strdate", String.valueOf(historyQryBean.getStartDay()));
        parameters.put("enddate", String.valueOf(historyQryBean.getEndDay()));
        //0:当前合约   1:历史合约
        parameters.put("qryflag", "1");
        parameters.put("count", String.valueOf(historyQryBean.getPageSize()));
        parameters.put("market", "");
        parameters.put("stkcode", "");
        parameters.put("bankcode", "");
        parameters.put("poststr", historyQryBean.getPostStr());
        logger.info("queryHistoryTrade start {}", JsonUtils.getJsonFromObject(historyQryBean));
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_QRY_HISTORY_TRADE, parameters);
        logger.info("queryHistoryTrade result {}", JsonUtils.getJsonFromObject(resultBean));
        List<HistoryTrade> historyTradeList = null;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            historyTradeList = KCBPConverter.convertToHistoryTrade(historyQryBean.getAccountId(), data);
        } else
            logger.info("queryHistoryTrade fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryHistoryTrade(historyTradeList, requestId);
    }

    public void queryCustomerFinance(String accountId, long requestId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(accountId);
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_FINANCE);
        parameters.put("fundid", kcbpAccount.getFundId());
        parameters.put("fortuneid", "-1");
        parameters.put("moneytype", "");
        parameters.put("bsflag", "");
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_QRY_FINANCE, parameters);
        logger.info("queryCustomerFinance {}", JsonUtils.getJsonFromObject(resultBean));
        List<AccountFinance> accountFinanceList = null;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            accountFinanceList = KCBPConverter.convertToCustomerFinance(accountId, data);
        } else
            logger.info("queryCustomerFinance fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryCustomerFinance(accountFinanceList, requestId);
    }

    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, long requestId) {
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(instrumentQryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        parameters.put("funcid", "410203");
        parameters.put("fundid", kcbpAccount.getFundId());
        parameters.put("market", KCBPConverter.getKCBPMarket(instrumentQryBean.getMarket()));
        parameters.put("stkcode", "");
        parameters.put("poststr", instrumentQryBean.getPostStr());
        parameters.put("rowcount", String.valueOf(instrumentQryBean.getRowCount()));
        ResultBean resultBean = sendCommand("410203", parameters);
        logger.info("queryInstrumentInfo resultBean {}", JsonUtils.getJsonFromObject(resultBean));
        List<Instrument> instrumentList = null;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            instrumentList = KCBPConverter.convertToInstrumentInfo(data);
            logger.info("queryInstrumentInfo size {}", instrumentList.size());
        } else
            logger.info("queryInstrumentInfo fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resInstrumentInfo(instrumentList, requestId);
    }

    /***
     * @Description: 标准券查询
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/13
     */
    public void queryStandardBonds(StandardBondQryBean standardBondQryBean, Long requestId) {
        String[] marketAndStockCode = new String[2];
        String marketStr = standardBondQryBean.getMarket();
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(standardBondQryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        if (!Strings.isNullOrEmpty(standardBondQryBean.getStockCode())) {
            marketAndStockCode = StockCodeUtils.getMarketAndStockCode(standardBondQryBean.getStockCode());
            marketStr = marketAndStockCode[1];
            if (!Strings.isNullOrEmpty(standardBondQryBean.getMarket()) && !standardBondQryBean.getMarket().equals(marketAndStockCode[1])) {
                kcbpTradeResponse.resQueryStandardBonds((List<StandardBonds>) null, requestId);
                logger.error("queryStandardBonds failed. market:{},stockCode:{}", JsonUtils.getJsonFromObject(standardBondQryBean));
                return;
            }
            parameters.put("stkcode", marketAndStockCode[0]);
        }
        if (!Strings.isNullOrEmpty(marketStr)) {
            parameters.put("market", marketStr);
            if (marketStr.equals(Market.SZSE.value)) {
                parameters.put("secuid", kcbpAccount.getSecuIds().get("0"));
            } else if (marketStr.equals(Market.SHSE.value)) {
                parameters.put("secuid", kcbpAccount.getSecuIds().get("1"));
            }
        }
        parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_STANDARD_BONDS);
        parameters.put("fundid", kcbpAccount.getFundId());
        parameters.put("qryflag", "1");
        parameters.put("count", "100");
        parameters.put("poststr", "");
        logger.info("parameters:{}", JSONObject.toJSONString(parameters));
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_QRY_STANDARD_BONDS, parameters);
        logger.info("queryStandardBonds {}", JsonUtils.getJsonFromObject(resultBean));
        List<StandardBonds> standardBondsList = null;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            standardBondsList = KCBPConverter.convertToStandardBonds(data);
            kcbpTradeResponse.resQueryStandardBonds(standardBondsList, requestId);
            return;
        }
        logger.error("queryStandardBonds fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryStandardBonds(null, requestId);
    }

    /***
     * @Description: 债券质押率
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/14
     */
    public void queryPledgeDiscount(String accountId, String market, String stockCode, Long requestId) {
        String[] marketAndStockCode = new String[2];
        String marketStr = market;
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(accountId);
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        if (!Strings.isNullOrEmpty(stockCode)) {
            marketAndStockCode = StockCodeUtils.getMarketAndStockCode(stockCode);
            marketStr = marketAndStockCode[1];
            if (!Strings.isNullOrEmpty(market) && !market.equals(marketAndStockCode[1])) {
                kcbpTradeResponse.resQueryPledgeDiscount(null, requestId);
                logger.error("queryPledgeDiscount failed. market:{},stockCode:{}", market, stockCode);
                return;
            }
            parameters.put("stkcode", marketAndStockCode[0]);
        }
        if (!Strings.isNullOrEmpty(marketStr)) {
            parameters.put("market", marketStr);
            if (marketStr.equals(Market.SZSE.value)) {
                parameters.put("secuid", kcbpAccount.getSecuIds().get("0"));
            } else if (marketStr.equals(Market.SHSE.value)) {
                parameters.put("secuid", kcbpAccount.getSecuIds().get("1"));
            }
        }
        parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_PLEDGE_DISCOUNT);
        parameters.put("fundid", kcbpAccount.getFundId());
        parameters.put("qryflag", "1");
        parameters.put("count", "100");
        parameters.put("poststr", "");
        logger.info("parameters:{}", JSONObject.toJSONString(parameters));
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_QRY_PLEDGE_DISCOUNT, parameters);
        logger.info("queryPledgeDiscount {}", JsonUtils.getJsonFromObject(resultBean));
        List<PledgeDiscount> pledgeDiscountList = null;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            pledgeDiscountList = KCBPConverter.convertToPledgeDiscount(accountId, data);
            kcbpTradeResponse.resQueryPledgeDiscount(pledgeDiscountList, requestId);
            return;
        }
        logger.error("queryPledgeDiscount fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryPledgeDiscount(null, requestId);
    }

    public void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId) {
        String[] marketAndStockCode;
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(positionQueryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        if (!Strings.isNullOrEmpty(positionQueryBean.getStockCode())) {
            marketAndStockCode = StockCodeUtils.getMarketAndStockCode(positionQueryBean.getStockCode());
            String marketStr = marketAndStockCode[1];
            parameters.put("stkcode", marketAndStockCode[0]);
            if (marketStr.equals(Market.SZSE.value)) {
                parameters.put("market", "0");
                parameters.put("secuid", kcbpAccount.getSecuIds().get("0"));
            } else if (marketStr.equals(Market.SHSE.value)) {
                parameters.put("market", "1");
                parameters.put("secuid", kcbpAccount.getSecuIds().get("1"));
            }
        } else {
            parameters.put("market", "");
            parameters.put("secuid", "");
            parameters.put("stkcode", "");
        }
        parameters.put("funcid", KCBP_FUNCTION_CODE_QRY_ACCOUNT_POSITION);
        parameters.put("fundid", kcbpAccount.getFundId());
        logger.info("parameters:{}", JSONObject.toJSONString(parameters));
        ResultBean resultBean = sendCommand(KCBP_FUNCTION_CODE_QRY_ACCOUNT_POSITION, parameters);
        logger.info("queryAccountPosition {}", JsonUtils.getJsonFromObject(resultBean));
        List<PositionBean> list;
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            list = KCBPConverter.convertToAccountPosition(kcbpAccount.getFundId(), positionQueryBean.getAccountId(), data);
            kcbpTradeResponse.resQueryAccountPosition(list, requestId);
            return;
        }
        logger.error("queryAccountPosition fail {}", resultBean.getErrorMsg());
        kcbpTradeResponse.resQueryAccountPosition(null, requestId);
    }

    public void querySystemStatus(KCBPAccount kcbpAccount) {
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        parameters.put("funcid", "410232");
        ResultBean resultBean = sendCommand("410232", parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            logger.warn("当前系统状态异常：{}", JsonUtils.getJsonFromObject(resultBean));
        }
    }

    public void queryRTGSStatus(KCBPAccount kcbpAccount) {
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        final String functionCode = "411552";
        parameters.put("funcid", functionCode);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("status", "");
        parameters.put("orderid", "");
        parameters.put("count", "");
        parameters.put("poststr", kcbpAccount.getRTGS_PositionStr());
        ResultBean resultBean = sendCommand(functionCode, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<List<String>> data = (List<List<String>>) resultBean.getData();
            String accountId = kcbpAccount.getAccountId();
            List<RTGSStatusBean> rtgsStatusBeanList = KCBPConverter.convertToRTGS(accountId, data);
            rtgsStatusBeanList.forEach(rtgsStatusBean -> {
                String reportNo = rtgsStatusBean.getReportNo();
                String reportNoByRTGS = entrustIdManager.getReportNoByRTGS(accountId, reportNo);
                if (Strings.isNullOrEmpty(reportNoByRTGS)) {
                    kcbpTradeClient.onRTGSStatus(rtgsStatusBean);
                    entrustIdManager.setReportNo(accountId, reportNo);
                } else {
                    logger.info("当前订单已推送交收，{}", reportNo);
                }
            });
            if (data.size() > 1) {
                int positionStr = data.get(0).size() - 1;
                String newPositionStr = data.get(data.size() - 1).get(positionStr);
                kcbpAccount.setRTGS_PositionStr(newPositionStr);
                logger.info("查询RTGS交收信息， newPositionStr :{}", newPositionStr);
            }
        } else {
            logger.warn("KCBP RTGS交收接口调用失败，errorCode {} errorMsg {}", resultBean.getErrorCode(), resultBean.getErrorMsg());
        }
    }

    public void setEntrustIdManager(EntrustIdManager entrustIdManager) {
        this.entrustIdManager = entrustIdManager;
    }
    
    public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
        List<NegotiateQuoteReceiveBean> negotiateQuoteReceiveBeans = null;
        KCBPAccount kcbpAccount = kcbpTradeClient.getKCBPAccount(negotiateQuoteQueryBean.getAccountId());
        Map<String, String> parameters = Maps.newHashMap(kcbpAccount.getHeadMessages());
        String market = StockCodeUtils.getMarket(negotiateQuoteQueryBean.getStockCode());
        if (Market.SZSE.value.equals(market)) {
            //深圳盘后固定价格交易行情查询
            parameters.put("funcid", "410682");
            if (!Strings.isNullOrEmpty(negotiateQuoteQueryBean.getStockCode()))
                parameters.put("stkcode", StockCodeUtils.getStockCode(negotiateQuoteQueryBean.getStockCode()));
            logger.info("parameters {}", JsonUtils.getJsonFromObject(parameters));
            ResultBean resultBean = sendCommand(parameters.get("funcid"), parameters);
            logger.info("queryIntegratedBulkCloseQuote SZ result {}", JsonUtils.getJsonFromObject(resultBean));
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<List<String>> data = (List<List<String>>) resultBean.getData();
                if (data.size() > 0)
                	negotiateQuoteReceiveBeans = KCBPConverter.convertToNegotiateQuoteReceiveResponseSZ(data);
            } else
                logger.info("queryIntegratedBulkCloseQuote SZ fail {}", resultBean.getErrorMsg());
        }
        kcbpTradeResponse.resQueryNegotiateQuoteReceive(negotiateQuoteReceiveBeans, requestId);
    }
}


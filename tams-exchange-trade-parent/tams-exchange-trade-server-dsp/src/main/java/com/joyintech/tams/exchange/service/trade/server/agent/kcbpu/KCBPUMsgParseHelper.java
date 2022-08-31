package com.joyintech.tams.exchange.service.trade.server.agent.kcbpu;

import cn.hutool.core.util.NumberUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.common.util.BigDecimalSerializer;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteReceiveBean;
import com.joyintech.tams.exchange.common.constant.AgentType;
import com.joyintech.tams.exchange.common.constant.RequestStatus;
import com.joyintech.tams.exchange.common.constant.TradeType;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.service.trade.server.agent.EntrustIdManager;
import com.joyintech.tams.exchange.service.trade.util.AgentUtil;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KCBPUMsgParseHelper {

    /**
     * 转化账户
     *
     * @param resList
     * @param accountId
     * @return
     */
    public static KCBPUAccount parseAccount(List<Map<String, String>> resList, String accountId) {
        KCBPUAccount kcbpuAccount = new KCBPUAccount();
        kcbpuAccount.setAgentType(AgentType.KINGDOM_KCBPU.value);
        kcbpuAccount.setAccountId(accountId);
        Map<String, String> secuIds = Maps.newConcurrentMap();
        for (Map<String, String> map : resList) {
            kcbpuAccount.setCustId(map.get("USER_CODE"));
            kcbpuAccount.setOrgId(map.get("BRANCH"));
            kcbpuAccount.setFundId(map.get("ACCOUNT"));
            if ("00".equals(map.get("MARKET"))) {
                // 00 深圳
                secuIds.put("0", map.get("SECU_ACC"));
            } else {
                // 10 上海
                secuIds.put("1", map.get("SECU_ACC"));
            }
        }
        kcbpuAccount.setSecuIds(secuIds);
        return kcbpuAccount;
    }

    /**
     * 374 委托查询
     *
     * @param resList
     * @param accountId
     * @param entrustIdManager
     * @param tradeCode
     * @return
     */
    public static List<Entrust> parseEntrusts(List<Map<String, String>> resList, String accountId, EntrustIdManager entrustIdManager, String tradeCode) {
        List<Entrust> entrustList = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> map : resList) {
            Entrust entrust = new Entrust();
            entrust.setId(map.get("EXT_ORDER_ID"));
            entrust.setAccountId(accountId);
            entrust.setReportNo(map.get("ORDER_ID"));
            entrust.setReportTime(AgentUtil.FormatApiTimeKCBPU(map.get("ORDER_DATE")));
            entrust.setBrokerEntrustId(map.get("BIZ_NO"));
//            String key = entrust.getBrokerEntrustId() + "|" + entrust.getReportNo();
//            entrust.setId(entrustIdManager.getIdByLocalEntrustId(accountId, entrust.getLocalEntrustId()));
//            if (Strings.isNullOrEmpty(entrust.getId())) {
//                entrust.setId(entrustIdManager.getKcbpwPTEntrustId(accountId, key));
//            }
            entrust.setTradedPrice(AgentUtil.Parse2BigDecimal(map.get("PRICE")));
            entrust.setEntrustVolume(AgentUtil.Parse2BigDecimal(map.get("QTY")).longValue());
            entrust.setEntrustBalance(AgentUtil.Parse2BigDecimal(map.get("ORDER_AMT")));
            entrust.setFrozenBalance(AgentUtil.Parse2BigDecimal(map.get("ORDER_FRZ_AMT")));
            entrust.setStockName(map.get("SECU_NAME"));
            entrust.setStockCode(StockCodeUtils.formatStockCode(map.get("SECU_CODE"), AgentUtil.GetMarketKCBPU(map.get("MARKET"))));
            entrust.setTradedPrice(AgentUtil.Parse2BigDecimal(map.get("MATCHED_PRICE")));
            entrust.setTradedVolume(AgentUtil.Parse2BigDecimal(map.get("MATCHED_QTY")).longValue());
            entrust.setTradedBalance(AgentUtil.Parse2BigDecimal(map.get("MATCHED_AMT")));
            entrustList.add(entrust);
        }

        return entrustList;
    }

    public static List<IntegratedEntrust> parseIEntrusts(List<Map<String, String>> resList, String accountId, EntrustIdManager entrustIdManager, String tradeCode) {
        List<IntegratedEntrust> iEntrustList = new ArrayList<>();
        List<Entrust> entrustList = parseEntrusts(resList, accountId, entrustIdManager, tradeCode);
        entrustList.forEach(entrust -> {
            IntegratedEntrust integratedEntrust = new IntegratedEntrust();
            BeanUtils.copyProperties(entrust, integratedEntrust);
            iEntrustList.add(integratedEntrust);
        });
        return iEntrustList;
    }

    /**
     * 376	当日成交明细查询
     */
    public static List<Trade> parseTrades(List<Map<String, String>> resList, String accountId, EntrustIdManager entrustIdManager, String tradeCode) {
        List<Trade> trades = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            Trade trade = new Trade();
            trade.setAccountId(accountId);
            trade.setReportNo(resMap.get("ORDER_ID"));
            trade.setBrokerEntrustId(resMap.get("ORDER_ID"));
            //trade.setLocalEntrustId(resMap.get("ordergroup"));
            // trade.setFinanceAccount(resMap.get("fundid"));
            trade.setBsFlag(resMap.get("TRD_ID"));// 需要转换???????????????????????????????????????????????????????????????????????
            trade.setMarket(AgentUtil.GetMarketKCBPU(resMap.get("MARKET")));
            trade.setStockName(resMap.get("SECU_NAME"));
            trade.setStockCode(StockCodeUtils.formatStockCode(resMap.get("SECU_CODE"), AgentUtil.GetMarketKCBPU(resMap.get("MARKET"))));
            trade.setTradeTime(resMap.get("MATCHED_TIME"));
            trade.setTradedPrice(AgentUtil.Parse2BigDecimal(resMap.get("MATCHED_PRICE")));
            trade.setTradedVolume(AgentUtil.Parse2BigDecimal(resMap.get("MATCHED_QTY")).longValue());
            trade.setTradedBalance(AgentUtil.Parse2BigDecimal(resMap.get("MATCHED_AMT")));
            int realStatus = Integer.parseInt((String) resMap.get("matchtype"));
            // 成交类型0:普通成交1:撤单成交2:废单3:内部撤单4:撤单废单
            trade.setTradeType(realStatus);
            trade.setBrokerTradeId((String) resMap.get("MATCHED_SN"));
            if (Strings.isNullOrEmpty(trade.getBrokerTradeId())) {
                trade.setBrokerTradeId((String) resMap.get("ORDER_ID"));
            }
            trade.setTradingDay(resMap.get("TRD_DATE"));
            trade.setEntrustProp(EntrustUtils.getEntrustProp(trade.getBsFlag(), trade.getMarket()));
            trade.setEntrustBs(EntrustUtils.getEntrustBs(trade.getBsFlag()));// 需要转换???????????????????????????????????????????????????????????????????????
            if (Strings.isNullOrEmpty(trade.getBrokerTradeId()) && TradeType.probablyNoBrokerTradeId(trade.getTradeType())) {
                trade.setBrokerTradeId("cancel_" + trade.getBrokerEntrustId());
            }
            trades.add(trade);
        }
        return trades;
    }

    public static List<NegotiateQuoteReceiveBean> parseNegotiateQuoteReceive(List<Map<String, String>> resList) {
        List<NegotiateQuoteReceiveBean> list = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            NegotiateQuoteReceiveBean receiveBean = new NegotiateQuoteReceiveBean();
            receiveBean.setRequestTime(AgentUtil.FormatApiTimeToString(resMap.get("transacttime")));
            int status = Integer.parseInt((String) (resMap.get("reportstatus")));
            receiveBean.setRequestStatus(status);
            receiveBean.setRequestStatusStr(RequestStatus.getRequestStatusName(status));
            receiveBean.setStockCode(StockCodeUtils.formatStockCode(resMap.get("stkcode"), "SZ"));//证券代码
            receiveBean.setStockName(resMap.get("stkname"));//证券名称
            receiveBean.setBs(resMap.get("side"));//买卖方向
            receiveBean.setAmount(Integer.parseInt(resMap.get("orderqty")));//数量
            receiveBean.setEntrustPrice(AgentUtil.Parse2BigDecimal(resMap.get("orderprice")));//价格（净价）
            receiveBean.setExecId((String) (resMap.get("trdreportid")));

            receiveBean.setMemberId(resMap.get("cpmemberid"));//接收方交易商代码 --> 本方交易商
            receiveBean.setTraderCode(resMap.get("cptradercode"));//接收方交易员代码 --> 本方交易员
            receiveBean.setInvestorId(resMap.get("cpinvestorid"));//接收方交易主体代码 --> 本方交易主体
            receiveBean.setInvestorType(resMap.get("cpinvestortype"));//接收方交易主体类型 --> 本方交易主体类型

            receiveBean.setCpMemberId(resMap.get("memberid"));//申报方交易商 --> 对手方交易商
            receiveBean.setCpTraderCode(resMap.get("tradercode"));//申报方交易员代码 --> 对手方交易员
            receiveBean.setCpInvestorId(resMap.get("investorid"));//申报方交易主体代码 --> 对手方交易主体
            receiveBean.setCpInvestorType(resMap.get("investortype"));//申报方交易主体类型 --> 对手方交易主体类型

            receiveBean.setConfirmId(resMap.get("confirmid"));//约定号
            receiveBean.setPositionStr(resMap.get("posstr"));//定位串
            receiveBean.setRemark(resMap.get("MEMO"));//备注
            list.add(receiveBean);
        }
        return list;
    }
}

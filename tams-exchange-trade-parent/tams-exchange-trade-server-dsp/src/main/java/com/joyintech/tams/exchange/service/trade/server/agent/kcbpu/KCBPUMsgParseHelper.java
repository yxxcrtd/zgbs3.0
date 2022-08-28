package com.joyintech.tams.exchange.service.trade.server.agent.kcbpu;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteReceiveBean;
import com.joyintech.tams.exchange.common.constant.RequestStatus;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.service.trade.server.agent.EntrustIdManager;
import com.joyintech.tams.exchange.service.trade.util.AgentUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class KCBPUMsgParseHelper {

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
        List<Entrust> entrusts = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> map : resList) {
            Entrust entrust = new Entrust();
            entrust.setAccountId(accountId);
            entrust.setReportNo(map.get("BIZ_NO"));
            entrust.setBrokerEntrustId(map.get("ORDER_ID"));
            String key = entrust.getBrokerEntrustId() + "|" + entrust.getReportNo();

            if ("403".equals(tradeCode)) {
                entrust.setId(entrustIdManager.getIdByLocalEntrustId(accountId, entrust.getLocalEntrustId()));

                if (Strings.isNullOrEmpty(entrust.getId())) {
                    entrust.setId(entrustIdManager.getKcbpwPTEntrustId(accountId, key));
                }
                entrust.setAccountId(map.get("ACCOUNT"));
                entrust.setTradedPrice(BigDecimal.valueOf(Double.parseDouble(map.get("PRIC"))));
                entrust.setEntrustVolume(Long.parseLong(map.get("QTY")));
                entrust.setEntrustBalance(BigDecimal.valueOf(Double.parseDouble(map.get("ORDER_AMT"))));
                entrust.setFrozenBalance(BigDecimal.valueOf(Double.parseDouble(map.get("ORDER_FRZ_AMT"))));
                entrust.setStockName(map.get("SECU_NAME"));
                entrust.setStockCode(map.get("SECU_CODE"));
                entrust.setTradeTime(map.get("MATCHED_TIME"));
                entrust.setTradedPrice(BigDecimal.valueOf(Double.parseDouble(map.get("MATCHED_PRICE"))));
                entrust.setTradedVolume(Long.parseLong(map.get("MATCHED_QTY")));
                entrust.setTradedBalance(BigDecimal.valueOf(Double.parseDouble(map.get("MATCHED_AMT"))));
            }
            entrusts.add(entrust);
        }

        return entrusts;
    }

    public static List<IntegratedEntrust> parseIEntrusts(List<Map<String, String>> resList, String accountId, EntrustIdManager entrustIdManager, String tradeCode) {
        List<IntegratedEntrust> entrusts = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> map : resList) {
            IntegratedEntrust iEntrust = new IntegratedEntrust();
            iEntrust.setAccountId(accountId);
            iEntrust.setReportNo(map.get("BIZ_NO"));
            iEntrust.setBrokerEntrustId(map.get("ORDER_ID"));
            String key = iEntrust.getBrokerEntrustId() + "|" + iEntrust.getReportNo();

            if ("403".equals(tradeCode)) {
                iEntrust.setId(entrustIdManager.getIdByLocalEntrustId(accountId, iEntrust.getLocalEntrustId()));

                if (Strings.isNullOrEmpty(iEntrust.getId())) {
                    iEntrust.setId(entrustIdManager.getKcbpwPTEntrustId(accountId, key));
                }
                iEntrust.setAccountId(map.get("ACCOUNT"));
                iEntrust.setTradedPrice(BigDecimal.valueOf(Double.parseDouble(map.get("PRIC"))));
                iEntrust.setEntrustVolume(Long.parseLong(map.get("QTY")));
                iEntrust.setEntrustBalance(BigDecimal.valueOf(Double.parseDouble(map.get("ORDER_AMT"))));
                iEntrust.setFrozenBalance(BigDecimal.valueOf(Double.parseDouble(map.get("ORDER_FRZ_AMT"))));
                iEntrust.setStockName(map.get("SECU_NAME"));
                iEntrust.setStockCode(map.get("SECU_CODE"));
                iEntrust.setTradeTime(map.get("MATCHED_TIME"));
                iEntrust.setTradedPrice(BigDecimal.valueOf(Double.parseDouble(map.get("MATCHED_PRICE"))));
                iEntrust.setTradedVolume(Long.parseLong(map.get("MATCHED_QTY")));
                iEntrust.setTradedBalance(BigDecimal.valueOf(Double.parseDouble(map.get("MATCHED_AMT"))));
            }
            entrusts.add(iEntrust);
        }

        return entrusts;
    }

    /**
     * 376	当日成交明细查询
     */
    public static List<Trade> parseTrades(List<Map<String, String>> resList, String accountId, EntrustIdManager entrustIdManager, String tradeCode) {
        List<Trade> trades = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> Map : resList) {
            Trade trade = new Trade();
            trade.setAccountId(accountId);
            trade.setReportNo(Map.get("ORDER_ID"));
            trade.setBrokerEntrustId(Map.get("MATCHED_SN"));
            trade.setLocalEntrustId(Map.get("RECORD_SN"));
            String key = trade.getBrokerEntrustId() + "|" + trade.getReportNo();
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
            receiveBean.setEntrustPrice(BigDecimal.valueOf(Double.parseDouble(resMap.get("orderprice"))));//价格（净价）
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
            receiveBean.setRemark(resMap.get("memo"));//备注
            list.add(receiveBean);
        }
        return list;
    }
}

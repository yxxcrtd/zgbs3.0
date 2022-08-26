package com.joyintech.tams.exchange.service.trade.server.agent.kcbpu;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.service.trade.server.agent.EntrustIdManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class KCBPUMsgParseHelper {


    /**
     * 解析委托
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

            if ("403".equals(tradeCode)){
                entrust.setId(entrustIdManager.getIdByLocalEntrustId(accountId,entrust.getLocalEntrustId()));

                if (Strings.isNullOrEmpty(entrust.getId())){
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

    /**
     * 查询成交
     * 376	当日成交明细查询
     * 377	历史成交明细查询
     * 1733	协商成交转发行情查询
     * 1734	点击成交转发转发行情查询
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
}

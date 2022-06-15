package com.joyintech.tams.exchange.service.trade.management.service;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.bean.HistoryTrade;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TradeQueryBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.PresentOrHistoryQryType;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.db.domain.TradeHistory;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.request.HistoryTradeInfoRequest;
import com.joyintech.tams.exchange.common.request.PageMode;
import com.joyintech.tams.exchange.common.request.PageTradeInfoRequest;
import com.joyintech.tams.exchange.common.request.TradeInfoRequest;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.common.vo.TradeVo;
import com.joyintech.tams.exchange.datacommon.db.service.TradeHistoryService;
import com.joyintech.tams.exchange.datacommon.db.service.TradeService;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3.*;

@Component
public class TradeManagement {

    @Autowired
    private TradeService tradeService;
    @Autowired
    private TerminalHelper terminalHelper;
    @Autowired
    private TradeHistoryService tradeHistoryService;
    @Autowired
    private CommonManagement commonManagement;


    public Message<List<Trade>> queryTrade(TradeInfoRequest request) {
        return new Message<List<Trade>>(queryTrade(request.getUserId(), request.getEntrustId(), request.getInstructionId(), request.getAccountId(), request.getOrderId()));
    }

    /**
     * 根据Entrust行数据查询Trade表中数据
     */
    public ResultBean queryTrade(String userId, String entrustId, String instructionId, String accountId, String orderId) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start

        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            return new ResultBean(ErrorCode.SUCCESS, new ArrayList());
        }*/
        if (!Strings.isNullOrEmpty(entrustId)) {
            List<Trade> list = tradeService.queryTradeByEntrust(entrustId);
//            list = list.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
            setFutureStockCode(list);
            return new ResultBean(ErrorCode.SUCCESS, list);
        }
        if (!Strings.isNullOrEmpty(orderId)) {
            List<Trade> list = tradeService.queryTradeByOrder(orderId);
//            list = list.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
            setFutureStockCode(list);
            return new ResultBean(ErrorCode.SUCCESS, list);
        }
        if (!Strings.isNullOrEmpty(instructionId)) {
            List<Trade> list = tradeService.queryTradeByInstructionId(instructionId);
//            list = list.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
            setFutureStockCode(list);
            return new ResultBean(ErrorCode.SUCCESS, list);
        }
        if (!Strings.isNullOrEmpty(accountId)) {
            List<Trade> list = tradeService.queryTradeByAccountId(accountId);
//            list = list.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
            setFutureStockCode(list);
            return new ResultBean(ErrorCode.SUCCESS, list);
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
    }

    /**
     * 根据委托ID查询成交
     */
    public ResultBean queryTradeByEntrustId(String userId, String entrustId) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            return ResultBean.success(Collections.EMPTY_LIST);
        }*/

        List<Trade> list = tradeService.queryTradeByEntrust(entrustId);
//        list = list.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
        setFutureStockCode(list);
        return ResultBean.success(list);
    }

    /**
     * 根据订单ID查询成交
     */
    public ResultBean queryTradeByOrderId(String userId, String orderId) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            return ResultBean.success(Collections.EMPTY_LIST);
        }*/

        List<Trade> list = tradeService.queryTradeByOrder(orderId);
//        list = list.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
        setFutureStockCode(list);
        return ResultBean.success(list);
    }

    /**
     * 根据指令ID查询成交
     */
    public ResultBean queryTradeByInstructionId(String userId, String instructionId) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            return ResultBean.success(Collections.EMPTY_LIST);
        }*/

        List<Trade> list = tradeService.queryTradeByInstructionId(instructionId);
/*        list = list.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId()))
                .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                .collect(Collectors.toList());*/
        list = list.stream()
                .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                .collect(Collectors.toList());
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
        setFutureStockCode(list);
        List<TradeVo> trades = fillTradeInfo(list);
        return ResultBean.success(trades);
    }

    private List<TradeVo> fillTradeInfo(List<Trade> tradeList) {
        List<TradeVo> tradeVos = Lists.newArrayListWithCapacity(tradeList.size());
        tradeList.forEach(o -> {
            TradeVo trade = new TradeVo();
            BeanUtils.copyProperties(o, trade);
            String busiTypeTg = trade.getBusiTypeTg();
            if (投管业务类型_质押券出入库.getValue().equals(busiTypeTg)) {
                trade.setTradedVolumeView(BigDecimal.valueOf(trade.getTradedVolume()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                trade.setBondVolume(trade.getTradedPrice().multiply(BigDecimal.valueOf(trade.getTradedVolume())).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                trade.setTradedPrice(trade.getTradedPrice().multiply(BigDecimal.valueOf(100)));
            } else if (投管业务类型_现券交易.getValue().equals(busiTypeTg) || EntrustUtils.isPledgeAgreementRepurchase(busiTypeTg)) {
                trade.setTradedVolumeView(BigDecimal.valueOf(trade.getTradedVolume()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            } else if (投管业务类型_通用式质押回购.getValue().equals(busiTypeTg)) {
                trade.setTradedBalance(trade.getTradedBalance().divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
            }
            trade.setMarket(Market.getMarketName(o.getMarket()));
            tradeVos.add(trade);
        });
        return tradeVos;
    }

    /**
     * 根据资金账户ID查询成交
     */
    public ResultBean queryTradeByAccountId(String userId, String accountId) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            return ResultBean.success(Collections.EMPTY_LIST);
        }*/

        List<Trade> list = tradeService.queryTradeByAccountId(accountId);
//        list = list.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
        setFutureStockCode(list);
        return ResultBean.success(list);
    }

    private void setFutureStockCode(List<Trade> list) {
        list.forEach(trade -> {
            if (InvestmentAssetType.isFutures(trade.getAssetType())) {
                trade.setStockCode(StockCodeUtils.getStockCode(trade.getStockCode()));
            }
        });
    }

    public Message<List<HistoryTrade>> queryHistoryTrade(HistoryTradeInfoRequest request) {
        PageMode pageMode = request.getPageMode();
        return new Message<>(queryHistoryTrade(request.getStartDay(), request.getEndDay(), request.getAccountId(), request.getPostStr(), pageMode.getPageSize(), request.getUserId()));
    }

    public ResultBean queryHistoryTrade(String startDay, String endDay, String accountId, String postStr, int pageSize, String userId) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            return new ResultBean(ErrorCode.SUCCESS, new ArrayList());
        }*/
        List<HistoryTrade> historyTrade = terminalHelper.queryHistoryTrade(startDay, endDay, accountId, postStr, pageSize);
//        historyTrade = historyTrade.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
        return new ResultBean(ErrorCode.SUCCESS, historyTrade);
    }

    public Message<PageInfo<TradeQueryBean>> queryAllForPage(PageTradeInfoRequest request) {
        String userId = request.getUserId();
        String entrustId = request.getEntrustId();
        Integer historyType = request.getHistoryType();
        request.getPageMode().setPageSize(99999);
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        List<String> ptlAuthList = null;
        //组合权限查询
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            if (!Strings.isNullOrEmpty(entrustId)) {
                return new Message<PageInfo<TradeQueryBean>>(new PageInfo());
            }
            return new Message<PageInfo<TradeQueryBean>>(new PageInfo());
        }*/

        if (historyType == PresentOrHistoryQryType.PRESENT.value) {
            if (!Strings.isNullOrEmpty(entrustId)) {
                List<TradeQueryBean> tradeList = tradeService.queryTradeByEntrustId(entrustId);
//                tradeList = tradeList.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
                return new Message<PageInfo<TradeQueryBean>>(new PageInfo<TradeQueryBean>(tradeList));
            }
            // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
            return new Message<PageInfo<TradeQueryBean>>(tradeService.queryAllForPage(request, ptlAuthList));
        } else if (historyType == PresentOrHistoryQryType.HISTORY.value) {
            if (!Strings.isNullOrEmpty(entrustId)) {
                List<TradeQueryBean> tradeList = tradeHistoryService.queryTradeHistoryByEntrustId(entrustId);
//                tradeList = tradeList.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
                return new Message<PageInfo<TradeQueryBean>>(new PageInfo<TradeQueryBean>(tradeList));
            }
            return new Message<PageInfo<TradeQueryBean>>(tradeHistoryService.queryAllForPage(request, ptlAuthList));
        }
        return new Message<PageInfo<TradeQueryBean>>().fail(ErrorCode.REQ_PARAMETER_ERROR.value + "", ErrorCode.REQ_PARAMETER_ERROR.errorMsg);
    }

    @Deprecated
    public ResultBean queryAllForPage(int pageNum, int pageSize, int historyType, String userId, String startDay, String endDay, String accountId, String market, String stockCode, String entrustProp, String entrustId) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        List<String> ptlAuthList = null;
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            if (!Strings.isNullOrEmpty(entrustId)) {
                return new ResultBean(ErrorCode.SUCCESS, new ArrayList<>());
            }
            return new ResultBean(ErrorCode.SUCCESS, new PageInfo());
        }*/
        if (historyType == PresentOrHistoryQryType.PRESENT.value) {
            if (!Strings.isNullOrEmpty(entrustId)) {
                List<Trade> tradeList = tradeService.queryTradeByEntrust(entrustId);
//                tradeList = tradeList.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
                // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
                return new ResultBean(ErrorCode.SUCCESS, tradeList);
            }
            PageInfo<Trade> tradePageInfo = tradeService.queryAllForPage(pageNum, pageSize, userId, accountId, market, stockCode, entrustProp, ptlAuthList);
            return new ResultBean(ErrorCode.SUCCESS, tradePageInfo);
        } else if (historyType == PresentOrHistoryQryType.HISTORY.value) {
            if (!Strings.isNullOrEmpty(entrustId)) {
                List<TradeHistory> tradeList = tradeHistoryService.queryTradeHistoryByEntrust(entrustId);
                tradeList = tradeList.stream().filter(trade -> ptlAuthList.contains(trade.getPortfolioId())).collect(Collectors.toList());
                return new ResultBean(ErrorCode.SUCCESS, tradeList);
            }
            PageInfo<TradeHistory> tradeHistoryPageInfo = tradeHistoryService.queryAllForPage(pageNum, pageSize, userId, startDay, endDay, accountId, market, stockCode, entrustProp, ptlAuthList);
            return new ResultBean(ErrorCode.SUCCESS, tradeHistoryPageInfo);
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
    }
}

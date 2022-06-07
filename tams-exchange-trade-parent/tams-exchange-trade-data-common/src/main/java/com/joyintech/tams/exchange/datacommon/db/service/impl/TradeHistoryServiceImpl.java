package com.joyintech.tams.exchange.datacommon.db.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.bean.TradeQryBean;
import com.joyintech.tams.exchange.common.bean.TradeQueryBean;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.db.domain.TradeHistory;
import com.joyintech.tams.exchange.common.db.domain.TradeHistoryExample;
import com.joyintech.tams.exchange.common.request.PageMode;
import com.joyintech.tams.exchange.common.request.PageTradeInfoRequest;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.datacommon.db.dao.TradeHistoryMapper;
import com.joyintech.tams.exchange.datacommon.db.service.TradeHistoryService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TradeHistoryServiceImpl implements TradeHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(EntrustHistoryServerImpl.class);

    @Autowired
    private TradeHistoryMapper tradeHistoryMapper;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<TradeHistory> queryAll() {
        return tradeHistoryMapper.selectByExample(null);
    }

    @Override
    public void saveTradeHistory(List<TradeHistory> tradeHistories) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        TradeHistoryMapper tradeHistoryMapperBatch = sqlSession.getMapper(TradeHistoryMapper.class);
        AtomicInteger index = new AtomicInteger(0);
        tradeHistories.forEach(tradeHistory -> {
            index.getAndIncrement();
            tradeHistoryMapperBatch.insertSelective(tradeHistory);
            if (index.get() % 500 == 0) {
                sqlSession.flushStatements();
            }
        });
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public PageInfo<TradeHistory> queryAllForPage(int pageNum, int pageSize, String userId, String startDay, String endDay, String accountId, String market, String stockCode, String entrustProp, List<String> ptlAuthList) {
        TradeHistoryExample tradeHistoryExample = new TradeHistoryExample();
        TradeHistoryExample.Criteria criteria = tradeHistoryExample.createCriteria();
        if (!Strings.isNullOrEmpty(startDay))
            criteria.andTradingDayGreaterThanOrEqualTo(startDay);
        if (!Strings.isNullOrEmpty(endDay))
            criteria.andTradingDayLessThanOrEqualTo(endDay);
        if (!Strings.isNullOrEmpty(accountId))
            criteria.andAccountIdEqualTo(accountId);
        if (!Strings.isNullOrEmpty(market))
            criteria.andMarketEqualTo(market);
        if (!Strings.isNullOrEmpty(stockCode))
            criteria.andStockCodeEqualTo(stockCode);
        if (!Strings.isNullOrEmpty(entrustProp))
            criteria.andEntrustPropEqualTo(entrustProp);
        if (Objects.nonNull(ptlAuthList) && ptlAuthList.size() > 0) {
            criteria.andPortfolioIdIn(ptlAuthList);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<TradeHistory> tradeHistories = tradeHistoryMapper.selectByExample(tradeHistoryExample);
        tradeHistories.forEach(tradeHistory -> {
            if (InvestmentAssetType.isFutures(tradeHistory.getAssetType())) {
                tradeHistory.setStockCode(StockCodeUtils.getStockCode(tradeHistory.getStockCode()));
            }
        });
        return new PageInfo<>(tradeHistories);
    }

    @Override
    public PageInfo<TradeQueryBean> queryAllForPage(PageTradeInfoRequest request, List<String> ptlAuthList) {
        PageMode pageMode = request.getPageMode();
        String accountId = request.getAccountId();
        String market = request.getMarket();
        String stockCode = request.getStockCode();
        String entrustProp = request.getEntrustProp();
        String startDay = DateUtil.formatDate(request.getStartDay());
        String endDay = DateUtil.formatDate(request.getEndDay());

        TradeQryBean tradeQryBean = new TradeQryBean(accountId, market, stockCode, entrustProp, ptlAuthList, startDay, endDay);
        PageHelper.startPage(pageMode.getPageNo(), pageMode.getPageSize());
        List<TradeQueryBean> tradeHistories = tradeHistoryMapper.selectHistoryTradeByPage(tradeQryBean);
        tradeHistories.forEach(tradeHistory -> {
            if (InvestmentAssetType.isFutures(tradeHistory.getAssetType())) {
                tradeHistory.setStockCode(StockCodeUtils.getStockCode(tradeHistory.getStockCode()));
            }
            if("EX005".equals(tradeHistory.getBusiTypeTg())){
                tradeHistory.setTradedPrice(tradeHistory.getTradedPrice().multiply(BigDecimal.valueOf(100)));
            }
        });
        return new PageInfo<>(tradeHistories);
    }

    @Override
    public List<TradeHistory> queryTradeHistoryByEntrust(String entrustId) {
        TradeHistoryExample tradeHistoryExample = new TradeHistoryExample();
        TradeHistoryExample.Criteria criteria = tradeHistoryExample.createCriteria();
        criteria.andEntrustIdEqualTo(entrustId);
        return tradeHistoryMapper.selectByExample(tradeHistoryExample);
    }

    @Override
    public List<TradeQueryBean> queryTradeHistoryByEntrustId(String entrustId) {
        TradeQryBean tradeQryBean = new TradeQryBean();
        tradeQryBean.setEntrustId(entrustId);
        List<TradeQueryBean> tradeQueryBeans = tradeHistoryMapper.selectTradeByPage(tradeQryBean);
        tradeQueryBeans.forEach(tradeHistory -> {
            if("EX005".equals(tradeHistory.getBusiTypeTg())){
                tradeHistory.setTradedPrice(tradeHistory.getTradedPrice().multiply(BigDecimal.valueOf(100)));
            }
        });
        return tradeQueryBeans;
    }

    @Override
    public String queryLastClearDate() {
        String lastClearDate = tradeHistoryMapper.queryLastClearDate();
        return lastClearDate == null ? "" : lastClearDate;
    }
}

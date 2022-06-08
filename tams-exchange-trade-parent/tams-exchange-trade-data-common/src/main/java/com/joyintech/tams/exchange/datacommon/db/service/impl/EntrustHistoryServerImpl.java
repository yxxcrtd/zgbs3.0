package com.joyintech.tams.exchange.datacommon.db.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.constant.EntrustType;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.db.domain.EntrustHistory;
import com.joyintech.tams.exchange.common.db.domain.EntrustHistoryExample;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.datacommon.db.dao.EntrustHistoryMapper;
import com.joyintech.tams.exchange.datacommon.db.service.EntrustHistoryService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EntrustHistoryServerImpl implements EntrustHistoryService {
    @Autowired
    private EntrustHistoryMapper entrustHistoryMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<EntrustHistory> queryAll() {
        return entrustHistoryMapper.selectByExample(null);
    }

    @Override
    public void saveEntrustHistory(List<EntrustHistory> entrustHistories) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH,false);
        EntrustHistoryMapper entrustHistoryMapper = sqlSession.getMapper(EntrustHistoryMapper.class);
        AtomicInteger index = new AtomicInteger(0);
        entrustHistories.forEach(entrustHistory -> {
            index.getAndIncrement();
            entrustHistoryMapper.insertSelective(entrustHistory);
            if (index.get() % 500 == 0){
                sqlSession.flushStatements();
            }
        });
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public PageInfo<EntrustHistory> queryAllForPage(int pageNum, int pageSize, String startDay, String endDay, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus) {
        EntrustHistoryExample entrustHistoryExample = new EntrustHistoryExample();
        EntrustHistoryExample.Criteria criteria = entrustHistoryExample.createCriteria();
        if (!Strings.isNullOrEmpty(startDay))
            criteria.andTradingDayGreaterThanOrEqualTo(startDay);
        if (!Strings.isNullOrEmpty(endDay))
            criteria.andTradingDayLessThanOrEqualTo(endDay);
        if (!Strings.isNullOrEmpty(accountId))
            criteria.andAccountIdEqualTo(accountId);
        if (!Strings.isNullOrEmpty(portfolioId))
            criteria.andPortfolioIdEqualTo(portfolioId);
        if (!Strings.isNullOrEmpty(market))
            criteria.andMarketEqualTo(market);
        if (!Strings.isNullOrEmpty(stockCode))
            criteria.andStockCodeEqualTo(stockCode);
        if (!Strings.isNullOrEmpty(entrustProp))
            criteria.andEntrustPropEqualTo(entrustProp);
        if (entrustStatus > -100)
            criteria.andEntrustStatusEqualTo(entrustStatus);
        // 过滤撤单委托
        criteria.andEntrustTypeNotEqualTo(EntrustType.CANCEL.value);
        PageHelper.startPage(pageNum, pageSize);
        List<EntrustHistory> entrustHistoryList = entrustHistoryMapper.selectByExample(entrustHistoryExample);
        entrustHistoryList.forEach(entrustHistory -> {
            if (InvestAssetTypeEnum.isFutures(entrustHistory.getAssetType())) {
                entrustHistory.setStockCode(StockCodeUtils.getStockCode(entrustHistory.getStockCode()));
            }
        });
        return new PageInfo<>(entrustHistoryList);
    }

    @Override
    public String queryLastClearDate() {
        String lastClearDate = entrustHistoryMapper.queryLastClearDate();
        return lastClearDate == null ? "" : lastClearDate;
    }
}

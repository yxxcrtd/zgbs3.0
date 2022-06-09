package com.joyintech.tams.exchange.datacommon.db.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.bean.EntrustQryBean;
import com.joyintech.tams.exchange.common.bean.EntrustQueryBean;
import com.joyintech.tams.exchange.common.constant.EntrustType;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrustHistory;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrustHistoryExample;
import com.joyintech.tams.exchange.common.request.PageEntrustInfoRequest;
import com.joyintech.tams.exchange.common.request.PageMode;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.datacommon.db.dao.IntegratedEntrustHistoryMapper;
import com.joyintech.tams.exchange.datacommon.db.service.IntegratedEntrustHistoryService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class IntegratedEntrustHistoryServiceImpl implements IntegratedEntrustHistoryService {

    @Autowired
    private IntegratedEntrustHistoryMapper integratedEntrustHistoryMapper;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<IntegratedEntrustHistory> queryAll() {
        return integratedEntrustHistoryMapper.selectByExample(null);
    }

    @Override
    public void saveIntegratedEntrustHistory(List<IntegratedEntrustHistory> integratedEntrustHistories) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        IntegratedEntrustHistoryMapper integratedEntrustHistoryMapper = sqlSession.getMapper(IntegratedEntrustHistoryMapper.class);
        AtomicInteger index = new AtomicInteger(0);
        integratedEntrustHistories.forEach(integratedEntrustHistory -> {
            index.getAndIncrement();
            integratedEntrustHistoryMapper.insertSelective(integratedEntrustHistory);
            if (index.get() % 500 == 0) {
                sqlSession.flushStatements();
            }
        });
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public PageInfo<IntegratedEntrustHistory> queryAllForPage(int pageNum, int pageSize, String userId, String startDay, String endDay, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus, List<String> ptlAuthList) {
        IntegratedEntrustHistoryExample integratedEntrustHistoryExample = new IntegratedEntrustHistoryExample();
        IntegratedEntrustHistoryExample.Criteria criteria = integratedEntrustHistoryExample.createCriteria();
        if (!Strings.isNullOrEmpty(userId))
            criteria.andUserIdEqualTo(userId);
        if (!Strings.isNullOrEmpty(startDay))
            criteria.andTradingDayGreaterThanOrEqualTo(startDay);
        if (!Strings.isNullOrEmpty(endDay))
            criteria.andTradingDayLessThanOrEqualTo(endDay);
        if (!Strings.isNullOrEmpty(accountId))
            criteria.andAccountIdEqualTo(accountId);
        if (!Strings.isNullOrEmpty(portfolioId))
            criteria.andPortfolioIdEqualTo(portfolioId);
        if (Strings.isNullOrEmpty(portfolioId) && Objects.nonNull(ptlAuthList) && ptlAuthList.size() > 0)
            criteria.andPortfolioIdIn(ptlAuthList);
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
        List<IntegratedEntrustHistory> integratedEntrustHistoryList = integratedEntrustHistoryMapper.selectByExample(integratedEntrustHistoryExample);
        return new PageInfo<>(integratedEntrustHistoryList);
    }

    @Override
    public PageInfo<EntrustQueryBean> queryAllForPage(PageEntrustInfoRequest request, List<String> ptlAuthList) {
        PageMode pageMode = request.getPageMode();
        String userId = request.getUserId();
        String portfolioId = request.getPortfolioId();
        String accountId = request.getAccountId();
        String market = request.getMarket();
        String stockCode = request.getStockCode();
        String entrustProp = request.getEntrustProp();
        Integer entrustStatus = request.getEntrustStatus();

        EntrustQryBean entrustQryBean = new EntrustQryBean(userId, accountId, "", portfolioId, market, stockCode, entrustProp, entrustStatus, EntrustType.CANCEL.value, ptlAuthList);
        entrustQryBean.setStartDay(request.getStartDay());
        entrustQryBean.setEndDay(request.getEndDay());

        PageHelper.startPage(pageMode.getPageNo(), pageMode.getPageSize());
        List<EntrustQueryBean> historyList = integratedEntrustHistoryMapper.selectEntrustHistoryByPage(entrustQryBean);
        return new PageInfo<>(historyList);
    }

    @Override
    public PageInfo<IntegratedEntrustHistory> queryAllEntrustForPage(int pageNum, int pageSize, String userId, String startDay, String endDay, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus, List<String> ptlAuthList) {
        PageHelper.startPage(pageNum, pageSize);
        //过滤撤单委托
        EntrustQryBean entrustQryBean = new EntrustQryBean(userId, startDay, endDay, accountId, "", portfolioId, market, stockCode, entrustProp, entrustStatus, EntrustType.CANCEL.value, ptlAuthList);
        List<IntegratedEntrustHistory> integratedEntrustHistoryList = integratedEntrustHistoryMapper.selectEntrustsPageInfo(entrustQryBean);
        integratedEntrustHistoryList.forEach(integratedEntrustHistory -> {
            if (InvestAssetTypeEnum.isFutures(integratedEntrustHistory.getAssetType())) {
                integratedEntrustHistory.setStockCode(StockCodeUtils.getStockCode(integratedEntrustHistory.getStockCode()));
            }
        });
        return new PageInfo<>(integratedEntrustHistoryList);
    }

    @Override
    public PageInfo<EntrustQueryBean> queryAllEntrustForPage(PageEntrustInfoRequest request, List<String> ptlAuthList) {
        PageMode pageMode = request.getPageMode();
        String userId = request.getUserId();
        String portfolioId = request.getPortfolioId();
        String accountId = request.getAccountId();
        String market = request.getMarket();
        String stockCode = request.getStockCode();
        String entrustProp = request.getEntrustProp();
        Integer entrustStatus = request.getEntrustStatus();
        String startDay = DateUtil.formatDate(request.getStartDay());
        String endDay = DateUtil.formatDate(request.getEndDay());

        PageHelper.startPage(pageMode.getPageNo(), pageMode.getPageSize());
        //过滤撤单委托
        EntrustQryBean entrustQryBean = new EntrustQryBean(userId, startDay, endDay, accountId, "", portfolioId, market, stockCode, entrustProp, entrustStatus, EntrustType.CANCEL.value, ptlAuthList);
        List<EntrustQueryBean> entrustQueryBeanList = integratedEntrustHistoryMapper.selectEntrustsByPage(entrustQryBean);
        entrustQueryBeanList.forEach(entrustQueryBean -> {
            if (InvestAssetTypeEnum.isFutures(entrustQueryBean.getAssetType())) {
                entrustQueryBean.setStockCode(StockCodeUtils.getStockCode(entrustQueryBean.getStockCode()));
            }
            if("EX005".equals(entrustQueryBean.getBusiTypeTg())){
                entrustQueryBean.setEntrustPrice(entrustQueryBean.getEntrustPrice().multiply(BigDecimal.valueOf(100)));
            }
        });
        return new PageInfo<>(entrustQueryBeanList);
    }

    @Override
    public String queryLastClearDate() {
        String lastClearDate = integratedEntrustHistoryMapper.queryLastClearDate();
        return lastClearDate == null ? "" : lastClearDate;
    }
}

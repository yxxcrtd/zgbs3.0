package com.joyintech.tams.exchange.datacommon.db.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.EntrustQryBean;
import com.joyintech.tams.exchange.common.bean.EntrustQueryBean;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.EntrustType;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrustExample;
import com.joyintech.tams.exchange.common.request.PageEntrustInfoRequest;
import com.joyintech.tams.exchange.common.request.PageMode;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.datacommon.cache.impl.AsyncServiceImpl;
import com.joyintech.tams.exchange.datacommon.db.dao.IMybatisMapper;
import com.joyintech.tams.exchange.datacommon.db.dao.IntegratedEntrustMapper;
import com.joyintech.tams.exchange.datacommon.db.service.IntegratedEntrustService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class IntegratedEntrustServiceImpl extends AsyncServiceImpl<String, IntegratedEntrust> implements IntegratedEntrustService {
    private static final Logger logger = LoggerFactory.getLogger(IntegratedEntrustServiceImpl.class);
    @Autowired
    private IntegratedEntrustMapper integratedEntrustMapper;
    private Map<String, Map<String, IntegratedEntrust>> orderIdMap = new ConcurrentHashMap<>();
    private Map<String, Map<String, IntegratedEntrust>> userIdMap = new ConcurrentHashMap<>();
    private Map<String, Map<String, IntegratedEntrust>> instructionIdMap = new ConcurrentHashMap<>();
    private Map<String, Map<String, IntegratedEntrust>> accountIdMap = new ConcurrentHashMap<>();
//    private String tradingDay;

    private static int timeZoneOffset = 0;

    private AtomicInteger localOffset = new AtomicInteger(0);

    static {
        timeZoneOffset = TimeZone.getDefault().getRawOffset();
    }

    @Override
    public List<IntegratedEntrust> queryByUserId(String userId) {
        return new ArrayList<>(userIdMap.getOrDefault(userId, new HashMap<>()).values());
    }

    @Override
    public List<IntegratedEntrust> queryIntegratedEntrustByOrderId(String orderId) {
        return new ArrayList<>(orderIdMap.getOrDefault(orderId, new HashMap<>()).values());
    }

    @Override
    public List<IntegratedEntrust> queryIntegratedEntrustByInstructionId(String instructionId) {
        return new ArrayList<>(instructionIdMap.getOrDefault(instructionId, new HashMap<>()).values());
    }

    @Override
    public List<IntegratedEntrust> queryIntegratedEntrustByAccountId(String accountId) {
        return new ArrayList<>(accountIdMap.getOrDefault(accountId, new HashMap<>()).values());
    }

    @Override
    public IntegratedEntrust queryIntegratedEntrustByAccountIdAndBrokerEntrustId(String accountId, String brokerEntrustId) {
        Map<String, IntegratedEntrust> accountIntegratedEntrustMap = accountIdMap.get(accountId);
        if (Objects.nonNull(accountIntegratedEntrustMap)) {
            for (IntegratedEntrust integratedEntrust : accountIntegratedEntrustMap.values()) {
                if (brokerEntrustId.equals(integratedEntrust.getBrokerEntrustId()) && DateUtil.getSysDate().replace("-", "").equals(integratedEntrust.getTradingDay()))
                    return integratedEntrust;
            }
        }
        return null;
    }

    @Override
    public List<IntegratedEntrust> queryCancelIntegratedEntrustByOrigEntrustIdAndAccount(String origEntrustId, String accountId) {
        return new ArrayList<IntegratedEntrust>(accountIdMap.getOrDefault(accountId, new HashMap<>()).values())
                .stream().filter(entrust -> origEntrustId.equals(entrust.getOrigEntrustId())
                        && EntrustType.CANCEL.value.equals(entrust.getEntrustType())
                        && EntrustStatus.INVALID.value != entrust.getEntrustStatus()
                        && EntrustStatus.SYSTEM_INVALID.value != entrust.getEntrustStatus()).collect(Collectors.toList());
    }

    @Override
    public List<IntegratedEntrust> queryIntegratedEntrustByStatus(String userId, String entrustStatus, List<String> ptlAuthList) {
        List<String> statusList = StringUtil.splitToList(GlobalConstant.COMMA, entrustStatus);
        List<IntegratedEntrust> entrustList = new ArrayList<>();
        for (Map.Entry<String, Map<String, IntegratedEntrust>> entry : accountIdMap.entrySet()) {
            Map<String, IntegratedEntrust> entrustMap = entry.getValue();
            for (Map.Entry<String, IntegratedEntrust> e : entrustMap.entrySet()) {
                IntegratedEntrust entrust = e.getValue();
                if (!EntrustType.CANCEL.value.equals(entrust.getEntrustType())
                        && statusList.contains(String.valueOf(entrust.getEntrustStatus()))
                        /*&& ptlAuthList.contains(entrust.getPortfolioId())*/) {
                    entrustList.add(entrust);
                }
            }
        }
        return entrustList;
    }

    @Override
    public IntegratedEntrust queryIntegratedEntrustByLocalEntrustIdAndAccount(String localEntrustId, String accountId) {
        Map<String, IntegratedEntrust> accountIntegratedEntrustMap = accountIdMap.get(accountId);
        if (Objects.nonNull(accountIntegratedEntrustMap)) {
            for (IntegratedEntrust integratedEntrust : accountIntegratedEntrustMap.values()) {
                if (localEntrustId.equals(integratedEntrust.getLocalEntrustId()))
                    return integratedEntrust;
            }
        }
        return null;
    }

    @Override
    public PageInfo<IntegratedEntrust> queryAllForPage(int pageNum, int pageSize, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus, List<String> ptlAuthList) {
        IntegratedEntrustExample integratedEntrustExample = new IntegratedEntrustExample();
        IntegratedEntrustExample.Criteria criteria = integratedEntrustExample.createCriteria();
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
        List<IntegratedEntrust> integratedEntrustList = integratedEntrustMapper.selectByExample(integratedEntrustExample);
        return new PageInfo<>(integratedEntrustList);
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

        PageHelper.startPage(pageMode.getPageNo(), pageMode.getPageSize());
        List<EntrustQueryBean> integratedEntrustList = integratedEntrustMapper.selectIntegratedEntrustByPage(entrustQryBean);
        return new PageInfo<>(integratedEntrustList);
    }

    @Override
    public PageInfo<IntegratedEntrust> queryAllEntrustForPage(int pageNum, int pageSize, String userId, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus, List<String> ptlAuthList) {
        PageHelper.startPage(pageNum, pageSize);
        //过滤撤单委托
        EntrustQryBean entrustQryBean = new EntrustQryBean(userId, accountId, "", portfolioId, market, stockCode, entrustProp, entrustStatus, EntrustType.CANCEL.value, ptlAuthList);
        List<IntegratedEntrust> integratedEntrustList = integratedEntrustMapper.selectEntrustsPageInfo(entrustQryBean);
        integratedEntrustList.forEach(integratedEntrust -> {
            if (InvestAssetTypeEnum.isFutures(integratedEntrust.getAssetType())) {
                integratedEntrust.setStockCode(StockCodeUtils.getStockCode(integratedEntrust.getStockCode()));
            }
        });
        return new PageInfo<>(integratedEntrustList);
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

        PageHelper.startPage(pageMode.getPageNo(), pageMode.getPageSize());
        //过滤撤单委托
        EntrustQryBean entrustQryBean = new EntrustQryBean(userId, accountId, "", portfolioId, market, stockCode, entrustProp, entrustStatus, EntrustType.CANCEL.value, ptlAuthList);
        List<EntrustQueryBean> entrustQueryBeanList = integratedEntrustMapper.selectEntrustsByPage(entrustQryBean);
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
    public void clearCache() {
        memory_cache.clear();
        orderIdMap.clear();
        userIdMap.clear();
        instructionIdMap.clear();
        accountIdMap.clear();
        integratedEntrustRAMCount.set(0);
    }

    public String genLocalId(IntegratedEntrust integratedEntrust) {
        int tempOffSet = localOffset.incrementAndGet();
        if (tempOffSet > 999) {
            tempOffSet = 0;
            localOffset.set(0);
        }
        long beginMill = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - timeZoneOffset;
        String localId = String.format("%s%03d", (System.currentTimeMillis() - beginMill) / 1000, tempOffSet);
        integratedEntrust.setLocalEntrustId(localId);
        return localId;
    }

    @Override
    protected void update(IntegratedEntrust integratedEntrust) {
        saveMemoryCache(integratedEntrust);
    }

    private void saveMemoryCache(IntegratedEntrust entrust) {
        Map<String, IntegratedEntrust> userMap = userIdMap.get(entrust.getUserId());
        if (Objects.isNull(userMap)) {
            userMap = new ConcurrentHashMap<>();
            userIdMap.put(entrust.getUserId(), userMap);
        }
        int mapOrginSize = userMap.size();
        userMap.put(entrust.getId(), entrust);
        if (userMap.size() > mapOrginSize) {
            integratedEntrustRAMCount.incrementAndGet();
        }
        if (!Strings.isNullOrEmpty(entrust.getParentOrderId())) {
            Map<String, IntegratedEntrust> orderMap = orderIdMap.get(entrust.getParentOrderId());
            if (Objects.isNull(orderMap)) {
                orderMap = new ConcurrentHashMap<>();
                orderIdMap.put(entrust.getParentOrderId(), orderMap);
            }
            orderMap.put(entrust.getId(), entrust);
        }
        if (!Strings.isNullOrEmpty(entrust.getInstructionId())) {
            Map<String, IntegratedEntrust> instructionMap = instructionIdMap.get(entrust.getInstructionId());
            if (Objects.isNull(instructionMap)) {
                instructionMap = new ConcurrentHashMap<>();
                instructionIdMap.put(entrust.getInstructionId(), instructionMap);
            }
            instructionMap.put(entrust.getId(), entrust);
        }
        Map<String, IntegratedEntrust> accountMap = accountIdMap.get(entrust.getAccountId());
        if (Objects.isNull(accountMap)) {
            accountMap = new ConcurrentHashMap<>();
            accountIdMap.put(entrust.getAccountId(), accountMap);
        }
        accountMap.put(entrust.getId(), entrust);
    }

    @Override
    protected void save(IntegratedEntrust integratedEntrust) {
        update(integratedEntrust);
    }

    @Override
    protected void delete(String id) {
        IntegratedEntrust integratedEntrust = findById(id);
        if (Objects.nonNull(integratedEntrust)) {
            deleteMemoryCache(integratedEntrust);
            integratedEntrustRAMCount.decrementAndGet();
        }
    }

    private void deleteMemoryCache(IntegratedEntrust entrust) {
        if (!Strings.isNullOrEmpty(entrust.getParentOrderId()))
            orderIdMap.get(entrust.getParentOrderId()).remove(entrust.getId());
        userIdMap.get(entrust.getUserId()).remove(entrust.getId());
        if (!Strings.isNullOrEmpty(entrust.getInstructionId()))
            instructionIdMap.get(entrust.getInstructionId()).remove(entrust.getId());
        accountIdMap.get(entrust.getAccountId()).remove(entrust.getId());
    }

    @Override
    public IMybatisMapper<IntegratedEntrust, String, IntegratedEntrustExample> getMapper() {
        return integratedEntrustMapper;
    }

    @Override
    public String genDbId() {
        //1s处理不能超过3w条
        short tempOffSet = (short) Math.abs(offset.incrementAndGet());
        if (tempOffSet < 0) {
            tempOffSet = 0;
            offset.set(0);
        }
        Calendar ca = Calendar.getInstance();
        return String.format("%s%s%05d", "i", formatThreadLocal.get().format(ca.getTime()), tempOffSet);
    }

    @Override
    public void clearCacheBySettle(String date) {
        integratedEntrustRAMCount.set(0);
//        fillPlatDate(date);
    }

//    @Override
//    public void fillPlatDate(String date) {
//        tradingDay = date;
//        logger.info("integratedEntrust tradingDay is {}", tradingDay);
//    }
}

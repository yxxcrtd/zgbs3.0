package com.joyintech.tams.exchange.datacommon.db.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.EntrustQueryDto;
import com.joyintech.tams.exchange.common.bean.risk.EntrustCountBean;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.EntrustType;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.EntrustExample;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.cache.impl.AsyncServiceImpl;
import com.joyintech.tams.exchange.datacommon.db.dao.EntrustMapper;
import com.joyintech.tams.exchange.datacommon.db.dao.IMybatisMapper;
import com.joyintech.tams.exchange.datacommon.db.service.EntrustService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class EntrustServiceImpl extends AsyncServiceImpl<String, Entrust> implements EntrustService {
    private static final Logger logger = LoggerFactory.getLogger(EntrustServiceImpl.class);
    @Autowired
    private EntrustMapper entrustMapper;
    private Map<String, Map<String, Entrust>> orderIdMap = new ConcurrentHashMap<>();
    private Map<String, Map<String, Entrust>> userIdMap = new ConcurrentHashMap<>();
    private Map<String, Map<String, Entrust>> instructionIdMap = new ConcurrentHashMap<>();
    private Map<String, Map<String, Entrust>> accountIdMap = new ConcurrentHashMap<>();

    private Map<String, EntrustCountBean> entrustCountBeanMap = new ConcurrentHashMap<>();// 统计委托参数

    //记录统一账户下，相同合约的委托 stockCode accountId entrustId
    private Map<String, Map<String, Map<String, Entrust>>> selfTradeCheckMap = new ConcurrentHashMap<>();
//    private String tradingDay;

    private Set<String> entrustIdSet = new HashSet<>();

    private static int timeZoneOffset = 0;

    private AtomicInteger localOffset = new AtomicInteger(0);

    static {
        timeZoneOffset = TimeZone.getDefault().getRawOffset();
    }

    /**
     * 在oracle数据库Entrust表中根据userId查询
     */
    @Override
    public List<Entrust> queryByUserId(String userId) {
        return new ArrayList<>(userIdMap.getOrDefault(userId, new HashMap<>()).values());
    }

    /**
     * 根据OrderId查询Entrust
     */
    @Override
    public List<Entrust> queryEntrustByOrderId(String orderId) {
        return new ArrayList<>(orderIdMap.getOrDefault(orderId, new HashMap<>()).values());
    }

    @Override
    public List<Entrust> queryEntrustByInstructionId(String instructionId) {
        return new ArrayList<>(instructionIdMap.getOrDefault(instructionId, new HashMap<>()).values());
    }

    @Override
    public Entrust queryEntrustByBrokerEntrustIdAndAccount(String brokerEntrustId, String accountId) {
        Map<String, Entrust> accountEntrustMap = accountIdMap.get(accountId);
        if (Objects.nonNull(accountEntrustMap)) {
            for (Entrust entrust : accountEntrustMap.values()) {
                if (brokerEntrustId.equals(entrust.getBrokerEntrustId()) && DateUtil.getSysDate().replace("-", "").equals(entrust.getTradingDay()))
                    return entrust;
            }
        }
        return null;
    }

    @Override
    public List<Entrust> queryCancelEntrustByOrigEntrustIdAndAccount(String origEntrustId, String accountId) {
        return new ArrayList<Entrust>(accountIdMap.getOrDefault(accountId, new HashMap<>()).values())
                .stream().filter(entrust -> origEntrustId.equals(entrust.getOrigEntrustId())
                        && EntrustType.CANCEL.value.equals(entrust.getEntrustType())
                        && EntrustStatus.INVALID.value != entrust.getEntrustStatus()
                        && EntrustStatus.SYSTEM_INVALID.value != entrust.getEntrustStatus()).collect(Collectors.toList());
    }

    @Override
    public Entrust queryEntrustByLocalEntrustIdAndAccount(String localEntrustId, String accountId) {
        Map<String, Entrust> accountEntrustMap = accountIdMap.get(accountId);
        if (Objects.nonNull(accountEntrustMap)) {
            for (Entrust entrust : accountEntrustMap.values()) {
                if (localEntrustId.equals(entrust.getLocalEntrustId()))
                    return entrust;
            }
        }
        return null;
    }

    @Override
    public List<Entrust> queryEntrustByAccountId(String accountId) {
        return new ArrayList<>(accountIdMap.getOrDefault(accountId, new HashMap<>()).values());
    }

    @Override
    public void save(Entrust entrust) {
        update(entrust);
    }

    @Override
    protected void delete(String id) {
        Entrust entrust = memory_cache.get(id);
        if (Objects.nonNull(entrust)) {
            deleteMemoryCache(entrust);
            entrustRAMCount.decrementAndGet();
        }
    }

    private void deleteMemoryCache(Entrust entrust) {
        if (!Strings.isNullOrEmpty(entrust.getParentOrderId()))
            orderIdMap.get(entrust.getParentOrderId()).remove(entrust.getId());
        userIdMap.get(entrust.getUserId()).remove(entrust.getId());
        if (!Strings.isNullOrEmpty(entrust.getInstructionId()))
            instructionIdMap.get(entrust.getInstructionId()).remove(entrust.getId());
        accountIdMap.get(entrust.getAccountId()).remove(entrust.getId());
    }

    @Override
    public IMybatisMapper<Entrust, String, EntrustExample> getMapper() {
        return entrustMapper;
    }

    @Override
    public String genLocalId(Entrust entrust) {
        int tempOffSet = localOffset.incrementAndGet();
        if (tempOffSet > 9999) {
            tempOffSet = 0;
            localOffset.set(0);
        }
        long beginMill = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - timeZoneOffset;
        String localId = String.format("%s%04d", (System.currentTimeMillis() - beginMill) / 1000, tempOffSet);
        entrust.setLocalEntrustId(localId);
        return localId;
    }

    @Override
    public void update(Entrust entrust) {
        saveMemoryCache(entrust);
        // 统计账号风控所需参数
        boolean newEntrust = false;
        Entrust entrust1 = findById(entrust.getId());
        if (Objects.isNull(entrust1)) {
            newEntrust = true;
        }
        EntrustCountBean entrustCount = entrustCountBeanMap.get(entrust.getAccountId());
        if (Objects.isNull(entrustCount)) {
            entrustCount = new EntrustCountBean();
            entrustCountBeanMap.put(entrust.getAccountId(), entrustCount);
        }
        // 统计报单总数
        if (newEntrust && !EntrustType.CANCEL.value.equals(entrust.getEntrustType())) {
            entrustCount.setTotalCount(entrustCount.getTotalCount() + 1);
        }
        // 统计撤单总数
        if (EntrustType.CANCEL.value.equals(entrust.getEntrustType())
                && EntrustStatus.SUCCEEDED.value == entrust.getEntrustStatus()) {
            if (!entrustIdSet.contains(entrust.getId())) {
                entrustCount.setCancelCount(entrustCount.getCancelCount() + 1);
                entrustIdSet.add(entrust.getId());
            }
        }
        // 自成交信息统计, 筛选统计出同一个账户下相同合约的未完成的委托
        //        if (EntrustUtils.isSelfTradeTyper(entrust.getEntrustProp()) && !EntrustType.CANCEL.value.equals(entrust.getEntrustType())) {
        //            Map<String, Map<String, Entrust>> stockCodeMap = selfTradeCheckMap.get(entrust.getStockCode());
        //            if (Objects.isNull(stockCodeMap)) {
        //                stockCodeMap = new ConcurrentHashMap<>();
        //                selfTradeCheckMap.put(entrust.getStockCode(), stockCodeMap);
        //            }
        //            Map<String, Entrust> accountEntrustMap = stockCodeMap.get(entrust.getAccountId());
        //            if (Objects.isNull(accountEntrustMap)) {
        //                accountEntrustMap = new ConcurrentHashMap<>();
        //                stockCodeMap.put(entrust.getAccountId(), accountEntrustMap);
        //            }
        //            if (!EntrustStatus.isFinished(entrust.getEntrustStatus())) {
        //                accountEntrustMap.put(entrust.getId(), entrust);
        //            } else {
        //                accountEntrustMap.remove(entrust.getId());
        //            }
        //        }
    }

    private void saveMemoryCache(Entrust entrust) {
        Map<String, Entrust> userMap = userIdMap.get(entrust.getUserId());
        if (Objects.isNull(userMap)) {
            userMap = new ConcurrentHashMap<>();
            userIdMap.put(entrust.getUserId(), userMap);
        }
        if (!Strings.isNullOrEmpty(entrust.getParentOrderId())) {
            Map<String, Entrust> orderMap = orderIdMap.get(entrust.getParentOrderId());
            if (Objects.isNull(orderMap)) {
                orderMap = new ConcurrentHashMap<>();
                orderIdMap.put(entrust.getParentOrderId(), orderMap);
            }
            orderMap.put(entrust.getId(), entrust);
        }
        if (!Strings.isNullOrEmpty(entrust.getInstructionId())) {
            Map<String, Entrust> instructionMap = instructionIdMap.get(entrust.getInstructionId());
            if (Objects.isNull(instructionMap)) {
                instructionMap = new ConcurrentHashMap<>();
                instructionIdMap.put(entrust.getInstructionId(), instructionMap);
            }
            instructionMap.put(entrust.getId(), entrust);
        }
        Map<String, Entrust> accountMap = accountIdMap.get(entrust.getAccountId());
        if (Objects.isNull(accountMap)) {
            accountMap = new ConcurrentHashMap<>();
            accountIdMap.put(entrust.getAccountId(), accountMap);
        }
        accountMap.put(entrust.getId(), entrust);
    }

    @Override
    public void clearCache() {
        memory_cache.clear();
        orderIdMap.clear();
        userIdMap.clear();
        instructionIdMap.clear();
        accountIdMap.clear();
        selfTradeCheckMap.clear();
        entrustIdSet.clear();
        entrustCountBeanMap.clear();
        entrustRAMCount.set(0);
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
        return String.format("%s%s%05d", "e", formatThreadLocal.get().format(ca.getTime()), tempOffSet);
    }

    @Override
    public PageInfo<Entrust> queryAllForPage(int pageNum, int pageSize, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus) {
        EntrustExample entrustExample = new EntrustExample();
        EntrustExample.Criteria criteria = entrustExample.createCriteria();
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
        List<Entrust> entrustList = entrustMapper.selectByExample(entrustExample);
        entrustList.forEach(entrust -> {
            if (InvestmentAssetType.isFutures(entrust.getAssetType())) {
                entrust.setStockCode(StockCodeUtils.getStockCode(entrust.getStockCode()));
            }
        });
        return new PageInfo<>(entrustList);
    }

    @Override
    public double getAccountCancelRatio(String accountId) {
        EntrustCountBean entrustCountBean = entrustCountBeanMap.get(accountId);
        if (Objects.nonNull(entrustCountBean)) {
            if (entrustCountBean.getTotalCount() > 100) {
                return TradeUtils.formatDouble((double) entrustCountBean.getCancelCount() / entrustCountBean.getTotalCount(), 4);
            }
        }
        return 0;
    }

    @Override
    public int getAccountTotalCount(String accountId) {
        EntrustCountBean entrustCountBean = entrustCountBeanMap.get(accountId);
        if (Objects.nonNull(entrustCountBean))
            return entrustCountBean.getTotalCount();
        return 0;
    }

    @Override
    public Collection<Entrust> getAccountSameStockCodeEntrusts(String accountId, String stockCode) {
        Map<String, Map<String, Entrust>> stockCodeMap = selfTradeCheckMap.get(stockCode);
        if (Objects.nonNull(stockCodeMap))
            return stockCodeMap.getOrDefault(accountId, new HashMap<>()).values();
        else
            return new ArrayList<>();
    }

    @Override
    public void clearCacheBySettle(String date) {
        entrustCountBeanMap.clear();
        selfTradeCheckMap.clear();
        entrustIdSet.clear();
//        fillPlatDate(date);
    }

//    @Override
//    public void fillPlatDate(String date) {
//        tradingDay = date;
//        logger.info("entrust tradingDay is {}", tradingDay);
//    }

    @Override
    public List<Entrust> queryEntrustByStatus(String userId, String entrustStatus, List<String> ptlAuthList) {
        List<String> statusList = StringUtil.splitToList(GlobalConstant.COMMA, entrustStatus);
        List<Entrust> entrustList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Entrust>> entry : accountIdMap.entrySet()) {
            Map<String, Entrust> entrustMap = entry.getValue();
            for (Map.Entry<String, Entrust> e : entrustMap.entrySet()) {
                Entrust entrust = e.getValue();
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
    public List<EntrustQueryDto> queryEntrust2Touguan(String instructionId) {
        return entrustMapper.queryEntrust2Touguan(instructionId);
    }
}

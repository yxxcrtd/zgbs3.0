package com.joyintech.tams.exchange.service.trade.server.agent;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EntrustIdManager {
    private static final Logger logger = LoggerFactory.getLogger(EntrustIdManager.class);
    private static final String keySplit = "#";
    private static final String localEntrustPrefix = "l";
    private static final String entrustPrefix = "e";
    private static final String RTGSPrefix = "r";
    // 金证W 深圳现券协商成交 撤销成交与委托下单缓存
    // 综合撤销业务
    public static final String KCBPW_ZHC_Prefix = "KWZHC";
    // 平台撤销业务
    public static final String KCBPW_PTC_Prefix = "KWPTC";
    // 综合业务
    public static final String KCBPW_ZH_Prefix = "KWZH";
    // 平台业务
    public static final String KCBPW_PT_Prefix = "KWPT";
    private RocksDB rocksDB;
    @Value("${agent.cache.path:cache}")
    private String cachePath;
    private Map<String, String> entrustIds = Maps.newConcurrentMap();
    private Map<String, String> localEntrustIds = Maps.newConcurrentMap();
    // 仅系统日期当日有效，需要定时清理非当日数据。每日3点清理 生产环境会部署多台服务实例
    private Map<String, String> kcbpwZHCancelEntrustIds = Maps.newConcurrentMap();
    private Map<String, String> kcbpwPTCancelEntrustIds = Maps.newConcurrentMap();

    // 下单的场景
    private Map<String, String> kcbpwZHEntrustIds = Maps.newConcurrentMap();
    private Map<String, String> kcbpwPTEntrustIds = Maps.newConcurrentMap();
    /**
     * 记录交收信息
     */
    private Map<String, String> reportNos = Maps.newConcurrentMap();
    private ExecutorService persistenceService = Executors.newSingleThreadExecutor();

    static {
        RocksDB.loadLibrary();
    }

    @PostConstruct
    public void init() {
        Options options = new Options();
        options.setCreateIfMissing(true);
        try {
            rocksDB = RocksDB.open(options, cachePath);
            RocksIterator rocksIterator = rocksDB.newIterator();
            for (rocksIterator.seekToFirst(); rocksIterator.isValid(); rocksIterator.next()) {
                String key = new String(rocksIterator.key());
                String value = new String(rocksIterator.value());
                if (key.startsWith(localEntrustPrefix)) {
                    localEntrustIds.put(key, value);
                } else if (key.startsWith(RTGSPrefix)) {
                    reportNos.put(key, value);
                } else if (key.startsWith(KCBPW_ZHC_Prefix)) {
                    kcbpwZHCancelEntrustIds.put(key, value);
                } else if (key.startsWith(KCBPW_PTC_Prefix)) {
                    kcbpwPTCancelEntrustIds.put(key, value);
                } else if (key.startsWith(KCBPW_ZH_Prefix)) {
                    kcbpwZHEntrustIds.put(key, value);
                } else if (key.startsWith(KCBPW_PT_Prefix)) {
                    kcbpwPTEntrustIds.put(key, value);
                } else {
                    entrustIds.put(key, value);
                }
            }
        } catch (Exception e) {
            logger.error("rocks db open path:{} error:{}", cachePath, e);
        }
    }

    /**
     * 仅系统日期当日有效，需要定时清理非当日数据。每日3点清理 生产环境会部署多台服务实例
     */
    public void clearKCBPWCache() {

    }

    public void clearCache() {
        rocksDB.close();
        File file = new File(cachePath);
        File[] fs = file.listFiles();
        for (File file1 : fs) {
            if (file1.delete()) {
                logger.info("delete file:{} success", file1.getName());
            } else {
                logger.info("delete file:{} failed", file1.getName());
            }
        }
        init();
    }

    private String getKey(String prefix, String accountId, String entrustId) {
        return prefix + accountId + keySplit + entrustId;
    }

    private void saveIdAsync(String key, String value) {
        persistenceService.submit(() -> {
            try {
                rocksDB.put(key.getBytes(), value.getBytes());
            } catch (Exception e) {
                logger.info("save id:{}, {} failed, {}", key, value, e);
            }
        });
    }

    /**
     * 綜合撤销业务
     *
     * @param accountId
     * @param brokeEntrustId
     * @return
     */
    public String getKcbpwZHCancelEntrustId(String accountId, String brokeEntrustId) {
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            return null;
        }
        return kcbpwZHCancelEntrustIds.get(getKey(KCBPW_ZHC_Prefix, accountId, brokeEntrustId));
    }

    /**
     * 綜合业务
     *
     * @param accountId
     * @param brokeEntrustId
     * @return
     */
    public String getKcbpwZHEntrustId(String accountId, String brokeEntrustId) {
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            return null;
        }
        return kcbpwZHEntrustIds.get(getKey(KCBPW_ZH_Prefix, accountId, brokeEntrustId));
    }


    /**
     * 普通撤销业务
     *
     * @param accountId
     * @param brokeEntrustId
     * @return
     */
    public String getKcbpwPTCancelEntrustId(String accountId, String brokeEntrustId) {
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            return null;
        }
        return kcbpwPTCancelEntrustIds.get(getKey(KCBPW_PTC_Prefix, accountId, brokeEntrustId));
    }

    /**
     * 普通业务
     *
     * @param accountId
     * @param brokeEntrustId
     * @return
     */
    public String getKcbpwPTEntrustId(String accountId, String brokeEntrustId) {
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            return null;
        }
        return kcbpwPTEntrustIds.get(getKey(KCBPW_PT_Prefix, accountId, brokeEntrustId));
    }


    /**
     * 金证W 综合业务 被撤销与撤销委托编号关联临时存储
     * 仅当日有效
     *
     * @param accountId
     * @param brokeEntrustId 被撤销的委托编号
     * @param ordersNo       撤销的委托编号
     * @param orderId        撤销的交易ID
     */
    public void setKcbpwZHCancelEntrustId(String accountId, String brokeEntrustId, String ordersNo, String orderId) {
        // "ordersno": "3", "orderid": "B200000004",
        String key = getKey(KCBPW_ZHC_Prefix, accountId, brokeEntrustId);
        String value = ordersNo + "|" + orderId;
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            logger.info("brokeEntrustId is empty: id = {}", brokeEntrustId);
            return;
        }
        kcbpwZHCancelEntrustIds.put(key, value);
        saveIdAsync(key, value);
    }

    /**
     * 综合业务
     *
     * @param accountId
     * @param brokeEntrustId
     * @param id
     */
    public void setKcbpwZHEntrustId(String accountId, String brokeEntrustId, String id) {
        // "ordersno": "3", "orderid": "B200000004",
        String key = getKey(KCBPW_ZH_Prefix, accountId, brokeEntrustId);
        String value = id;
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            logger.info("brokeEntrustId is empty: id = {}", brokeEntrustId);
            return;
        }
        kcbpwZHEntrustIds.put(key, value);
        saveIdAsync(key, value);
    }

    /**
     * 金证W 普通业务 被撤销与撤销委托编号关联临时存储
     * 仅当日有效
     *
     * @param accountId
     * @param brokeEntrustId 被撤销的委托编号
     * @param ordersNo       撤销的委托编号
     * @param orderId        撤销的交易ID
     */
    public void setKcbpwPTCancelEntrustId(String accountId, String brokeEntrustId, String ordersNo, String orderId) {
        // "ordersno": "3", "orderid": "B200000004",
        String key = getKey(KCBPW_PTC_Prefix, accountId, brokeEntrustId);
        String value = ordersNo + "|" + orderId;
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            logger.info("brokeEntrustId is empty: id = {}", brokeEntrustId);
            return;
        }
        kcbpwPTCancelEntrustIds.put(key, value);
        saveIdAsync(key, value);
    }

    /**
     * 普通业务
     *
     * @param accountId
     * @param brokeEntrustId
     * @param id
     */
    public void setKcbpwPTEntrustId(String accountId, String brokeEntrustId, String id) {
        // "ordersno": "3", "orderid": "B200000004",
        String key = getKey(KCBPW_PT_Prefix, accountId, brokeEntrustId);
        String value = id;
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            logger.info("brokeEntrustId is empty: id = {}", brokeEntrustId);
            return;
        }
        kcbpwPTEntrustIds.put(key, value);
        saveIdAsync(key, value);
    }


    public void setBrokeEntrustIdMapping(String accountId, String brokeEntrustId, String id) {
        String key = getKey(entrustPrefix, accountId, brokeEntrustId);
        String value = id;
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            logger.info("brokeEntrustId is empty: id = {}", id);
            return;
        }
        entrustIds.put(key, value);
        saveIdAsync(key, value);
    }

    public String getIdByBrokeEntrustId(String accountId, String brokeEntrustId) {
        if (Strings.isNullOrEmpty(brokeEntrustId)) {
            return null;
        }
        return entrustIds.get(getKey(entrustPrefix, accountId, brokeEntrustId));
    }

    public void setLocalEntrustIdMapping(String accountId, String localEntrustId, String id) {
        String key = getKey(localEntrustPrefix, accountId, localEntrustId);
        localEntrustIds.put(key, id);
        saveIdAsync(key, id);
    }

    public String getIdByLocalEntrustId(String accountId, String localEntrustId) {
        if (Strings.isNullOrEmpty(localEntrustId)) {
            return null;
        }
        return localEntrustIds.get(getKey(localEntrustPrefix, accountId, localEntrustId));
    }

    public void setReportNo(String accountId, String reportNo) {
        String key = getKey(RTGSPrefix, accountId, reportNo);
        if (Strings.isNullOrEmpty(reportNo)) {
            logger.info("reportNo is empty: id = {}", accountId);
            return;
        }
        reportNos.put(key, reportNo);
        saveIdAsync(key, reportNo);
    }

    public String getReportNoByRTGS(String accountId, String reportNo) {
        if (Strings.isNullOrEmpty(reportNo)) {
            return null;
        }
        return reportNos.get(getKey(RTGSPrefix, accountId, reportNo));
    }
}
package com.joyintech.tams.exchange.common.cache.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Strings;
import com.joyintech.redis.core.base.PipeReadService;
import com.joyintech.redis.core.base.PipeWriteService;
import com.joyintech.redis.core.data.RedisHash;
import com.joyintech.tams.exchange.common.cache.CacheService;
import com.joyintech.tams.exchange.common.cache.config.DataServiceConfig;
import com.joyintech.tams.exchange.common.config.FastJsonRedisSerializer;
import com.joyintech.tams.exchange.common.db.domain.KeyMethodInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class RedisCacheServiceImpl<PK extends Serializable, T extends KeyMethodInterface<PK>> implements CacheService<PK, T> {
    /**
     * 日志对象
     */
    protected static final Logger logger = LoggerFactory.getLogger(RedisCacheServiceImpl.class);
    public static AtomicInteger entrustRAMCount = new AtomicInteger(0);
    public static AtomicInteger entrustRedisCount = new AtomicInteger(0);
    public static AtomicInteger integratedEntrustRAMCount = new AtomicInteger(0);
    public static AtomicInteger integratedEntrustRedisCount = new AtomicInteger(0);
    public static AtomicInteger orderRAMCount = new AtomicInteger(0);
    public static AtomicInteger orderRedisCount = new AtomicInteger(0);
    public static AtomicInteger tradeRAMCount = new AtomicInteger(0);
    public static AtomicInteger tradeRedisCount = new AtomicInteger(0);
    protected final Map<PK, T> memory_cache = new ConcurrentHashMap<>();
    @Autowired
    protected PipeWriteService<String, PK, T> pipeWriteService;
    @Autowired
    protected PipeReadService<String, PK, T> pipeReadService;
    @Autowired
    protected DataServiceConfig dataServiceConfig;
    protected RedisSerializer<String> keySerializer = new FastJsonRedisSerializer<>(String.class);
    protected RedisSerializer<T> redisSerializer;
    protected String className = null;
    protected AtomicInteger offset = new AtomicInteger(0);
    protected ThreadLocal<SimpleDateFormat> formatThreadLocal = ThreadLocal.withInitial(() ->
            new SimpleDateFormat("yyMMddHHmmss"));

    @PostConstruct
    public void coverCache() {
        coverCache(false);
    }

    public abstract void coverCache(boolean fromRedis);

    public abstract void clearCache();

    protected abstract void update(T t);

    protected abstract void save(T t);

    protected abstract void delete(PK PK);

    protected String getClassName() {
        if (Strings.isNullOrEmpty(className)) {
            className = getTClass().getName();
        }
        return className;
    }

    protected String genDbId() {
        //1s处理不能超过3w条
        short tempOffSet = (short) Math.abs(offset.incrementAndGet());
        if (tempOffSet < 0) {
            tempOffSet = 0;
            offset.set(0);
        }
        Calendar ca = Calendar.getInstance();
        return String.format("%s%05d", formatThreadLocal.get().format(ca.getTime()), tempOffSet);
    }

    @Override
    public <S extends T> void updateCache(S entity) {
        setKeyField(entity);
        PK pk = entity.getId();
        if (Objects.isNull(pk)) {
            pk = (PK) genDbId();
            entity.setId(pk);
        }
        update(entity);
        memory_cache.put(pk, entity);
        if (dataServiceConfig.isUseRedis()) {
            Map<String, Map<PK, T>> redisMap = new HashMap<>();
            Map<PK, T> beanMap = new HashMap<>();
            beanMap.put(pk, entity);
            redisMap.put(className, beanMap);
            pipeWriteService.writeList(redisMap);
        }
    }

    @Override
    public <S extends T> void updateCache(Iterable<S> entities) {
        Map<String, Map<PK, T>> redisMap = new HashMap<>();
        Map<PK, T> beanMap = new HashMap<>();
        entities.forEach(entity -> {
            setKeyField(entity);
            PK pk = entity.getId();
            if (Objects.isNull(pk)) {
                pk = (PK) genDbId();
                entity.setId(pk);
            }
            update(entity);
            memory_cache.put(pk, entity);
            if (dataServiceConfig.isUseRedis())
                beanMap.put(pk, entity);
        });
        if (dataServiceConfig.isUseRedis()) {
            redisMap.put(className, beanMap);
            pipeWriteService.writeList(redisMap);
        }
    }

    @Override
    public void deleteCache(PK pk) {
        Map<String, Map<PK, T>> redisMap = new HashMap<>();
        Map<PK, T> beanMap = new HashMap<>();
        redisMap.put(className, beanMap);
        T entity = memory_cache.get(pk);
        delete(pk);
        memory_cache.remove(pk);
        beanMap.put(pk, entity);
        pipeWriteService.deleteOne(redisMap);
    }

    @Override
    public List<PK> deleteCache(Iterable<T> entities) {
        List<PK> pkList = new ArrayList<>();
        Map<String, Map<PK, T>> redisMap = new HashMap<>();
        Map<PK, T> beanMap = new HashMap<>();
        redisMap.put(className, beanMap);
        entities.forEach(entity -> {
            PK pk = entity.getId();
            delete(pk);
            memory_cache.remove(pk);
            beanMap.put(pk, entity);
            pkList.add(pk);
        });
        pipeWriteService.deleteOne(redisMap);
        return pkList;
    }

    @Override
    public T findById(PK pk) {
        return memory_cache.get(pk);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(memory_cache.values());
    }

    public Map<PK, T> getMap() {
        return memory_cache;
    }

    protected T findFromRedis(PK pk) {
        if (dataServiceConfig.isUseRedis()) {
            RedisHash<String, PK, T> redisHash = new RedisHash<>();
            redisHash.setKey(className);
            redisHash.setField(pk);
            return pipeReadService.read(redisHash);
        }
        return null;
    }

    protected void getMapFromRedis(String key, Map<PK, T> map) {
        if (dataServiceConfig.isUseRedis()) {
            Map<String, Map<PK, T>> resultMap = pipeReadService.readAll(Collections.singletonList(key));
            if (!CollectionUtils.isEmpty(resultMap)) {
                map.putAll(resultMap.get(key));
            }
        }
    }

    protected List<T> getAllFromRedis() {
        if (Strings.isNullOrEmpty(className)) {
            className = getTClass().getName();
            redisSerializer = new FastJsonRedisSerializer<>(getTClass());
            //            setRedisSerializer();
        }
        logger.info("getAllFromRedis() called with parameters => 【{}】", className);
        Map<String, Map<PK, T>> resultMap = pipeReadService.readAll(Collections.singletonList(className));
        if (CollUtil.isNotEmpty(resultMap)) {
            Map<PK, T> beanMap = resultMap.get(className);
            if (Objects.nonNull(beanMap))
                return new ArrayList<>(beanMap.values());
        }
        return new ArrayList<>();
    }

    protected void setKeyField(T t) {
        if (Objects.nonNull(className))
            return;
        Class tClass = t.getClass();
        className = tClass.getName();
        redisSerializer = new FastJsonRedisSerializer<>(tClass);
        //        setRedisSerializer();
    }

    public Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        return tClass;
    }

    private void setRedisSerializer() {
        pipeReadService.getRedisTemplate().setKeySerializer(keySerializer);
        pipeReadService.getRedisTemplate().setHashKeySerializer(keySerializer);
        pipeReadService.getRedisTemplate().setHashValueSerializer(redisSerializer);
        pipeWriteService.getRedisTemplate().setKeySerializer(keySerializer);
        pipeWriteService.getRedisTemplate().setHashKeySerializer(keySerializer);
        pipeWriteService.getRedisTemplate().setHashValueSerializer(redisSerializer);
    }
}
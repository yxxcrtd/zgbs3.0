package com.joyintech.tams.business.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisSingleLock {

    private static final Logger logger = LoggerFactory.getLogger(RedisSingleLock.class);

    private final StringRedisTemplate redis;

    public RedisSingleLock(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /**
     * 添加Redis锁
     *
     * @param key     主键
     * @param value   value
     * @param timeout 过期时间(s)
     * @author jiangpeng
     * @date 2022/9/7 10:41
     */
    public void lock(String key, String value, long timeout) {
        do {
            Boolean lockSucess = redis.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);

            if (lockSucess == null) {
                throw new IllegalStateException();
            }
            if (!lockSucess) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                break;
            }
        } while (true);
    }

    /**
     * 释放锁
     *
     * @param key 主键
     * @author jiangpeng
     * @date 2022/9/7 10:42
     */
    public void releaseLock(String key) {
        redis.delete(key);
    }


    /**
     * 添加Redis锁
     *
     * @param key     主键
     * @param value   value
     * @param timeout 超时时间
     * @author jiangpeng
     * @date 2022/9/7 10:41
     */
    public boolean tryLock(String key, String value, long timeout) {
        logger.info("RedisSingleLock.tryLock --> key:{}", key);
        Boolean lockSuccess = redis.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
        if (lockSuccess == null) {
            throw new IllegalStateException();
        }
        return lockSuccess;
    }
}

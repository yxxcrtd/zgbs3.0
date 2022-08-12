package com.joyintech.tams.exchange.datacommon.cache.impl;

import com.joyintech.redis.core.base.RedisHashOperator;
import com.joyintech.tams.exchange.datacommon.cache.RedisOperateService;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
//import redis.clients.util.JedisClusterCRC16;

/**
 * @ClassName RedisOperService
 * @Description TODO
 * @Author swwan
 * @Date 2020/8/27 16:29
 * @Version 1.0
 **/
@Component
public class RedisOperateServiceImpl extends RedisHashOperator implements RedisOperateService {


    @Override
    public Long getHashLen (final String k) {
        return (Long) this.redisTemplate().execute((RedisCallback<Long>) connection ->hLenDoInRedis(k,connection));
    }

    protected Long hLenDoInRedis(String k, RedisConnection connection) {
        RedisSerializer keySerializer = this.redisTemplate().getKeySerializer();
        byte[] rawKey = keySerializer.serialize(k);
        return connection.hLen(rawKey);
    }
}

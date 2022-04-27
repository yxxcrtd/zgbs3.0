package com.joyintech.tams.exchange.datacommon.cache.impl;

import cn.hutool.core.collection.CollUtil;
import com.joyintech.tams.exchange.common.cache.impl.RedisCacheServiceImpl;
import com.joyintech.tams.exchange.common.constant.DBSaveType;
import com.joyintech.tams.exchange.common.db.domain.KeyMethodInterface;
import com.joyintech.tams.exchange.common.utils.ConvertListUtils;
import com.joyintech.tams.exchange.datacommon.async.AsyncPersistenceWorker;
import com.joyintech.tams.exchange.datacommon.cache.AsyncService;
import com.joyintech.tams.exchange.datacommon.db.dao.IMybatisMapper;
import com.joyintech.tams.exchange.datacommon.sync.SyncPersistenceWorker;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

public abstract class AsyncServiceImpl<PK extends Serializable, T extends KeyMethodInterface<PK>> extends RedisCacheServiceImpl<PK, T> implements AsyncService<PK, T> {
    @Autowired
    private AsyncPersistenceWorker asyncPersistenceWorker;
    @Autowired
    private SyncPersistenceWorker syncPersistenceWorker;

    public abstract IMybatisMapper<T, PK, ?> getMapper();

    @Override
    public T asyncUpdate(T t) {
        asyncUpdate(t, DBSaveType.ASYNC.value);
        return t;
    }

    @Override
    public <S extends T> Iterable<S> asyncUpdate(Iterable<S> entities) {
        asyncUpdate(entities, DBSaveType.ASYNC.value);
        return entities;
    }

    @Override
    public void asyncDel(PK pk) {
        deleteCache(pk);
        asyncPersistenceWorker.async(() -> getMapper().deleteByPrimaryKey(pk));
    }

    @Override
    public void asyncDel(Iterable<T> entities) {
        List<PK> pkList = deleteCache(entities);
        int totalSize = pkList.size();
        //批量删除sql中in里最大数量，必须小于等于1000
        int batchSize = 900;
        int batchCount = totalSize / batchSize + (totalSize % batchSize == 0 ? 0 : 1);
        for (int i = 0; i < batchCount; i++) {
            List<PK> finalPkList = ConvertListUtils.convertList(i + 1, batchSize, pkList);
            asyncPersistenceWorker.async(() -> getMapper().deleteBatchByPrimaryKey(finalPkList));
        }
    }

    @Override
    public void asyncUpdate(T t, int saveType) {
        updateCache(t);
        if (DBSaveType.ASYNC.value == saveType) {
            if (dataServiceConfig.isAsyncDb()) {
                asyncPersistenceWorker.async(() -> getMapper().updateOrInsertSelective(t));
            } else {
                getMapper().updateOrInsertSelective(t);
            }
        } else if (DBSaveType.SYNC_BATCH.value == saveType) {
            syncPersistenceWorker.sync(() -> getMapper().updateOrInsertSelective(t));
        } else {
            getMapper().updateOrInsertSelective(t);
        }
    }

    @Override
    public <S extends T> void asyncUpdate(Iterable<S> entities, int saveType) {
        updateCache(entities);
        entities.forEach(s -> {
            if (DBSaveType.ASYNC.value == saveType && dataServiceConfig.isAsyncDb()) {
                asyncPersistenceWorker.async(() -> getMapper().updateOrInsertSelective(s));
            } else {
                syncPersistenceWorker.sync(() -> getMapper().updateOrInsertSelective(s));
            }
        });
    }

    @Override
    public void coverCache(boolean fromRedis) {
        clearCache();
        List<T> list = fromRedis ? getAllFromRedis() : getMapper().selectByExample(null);
        if (CollUtil.isNotEmpty(list)) {
            logger.info("coverCache() called with parameters => 【className = {}, fromRedis = {}, listSize = {}】", getClassName(), fromRedis, list.size());
            updateCache(list);
        }
    }
}

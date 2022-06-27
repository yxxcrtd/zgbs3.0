package com.joyintech.tams.business.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadLocalUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalUtil.class);
    /**
     * 线程缓存
     */
    private static ThreadLocal<List<? extends Object>> localCache = ThreadLocal.withInitial(ArrayList::new);

    /**
     * 设置目标数据到线程缓存
     *
     * @param list 目标数据
     */
    public static final <V extends Object> void setLocalCache(List<V> list) {
        localCache.set(list);
    }

    /**
     * 设置目标数据到线程缓存
     *
     * @param list 目标数据
     */
    public static final <V extends Object> void setLocalCache(V... list) {
        localCache.set(Arrays.asList(list));
    }

    public static final List<? extends Object> getTarget() {
        return localCache.get();
    }

    public static final void cleanLocalCache() {
        localCache.remove();
    }

    /**
     * 先清空缓存 设置目标数据到线程缓存
     *
     * @param list 目标数据
     */
    public static final <V extends Object> void clearAndSetLocalCache(List<V> list) {
        cleanLocalCache();
        localCache.set(Arrays.asList(list));
    }

    /**
     * 先清空缓存 设置目标数据到线程缓存
     *
     * @param list 目标数据
     */
    public static final <V extends Object> void clearAndSetLocalCache(V... list) {
        cleanLocalCache();
        localCache.set(Arrays.asList(list));
    }
}




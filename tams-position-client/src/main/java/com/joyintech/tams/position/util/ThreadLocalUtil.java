package com.joyintech.tams.position.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLocalUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalUtil.class);
    /**
     * 线程缓存
     */
    private static ThreadLocal<Object> localCache = ThreadLocal.withInitial(Object::new);

    /**
     * 设置目标数据到线程缓存
     *
     */
    public static final <V extends Object> void setLocalCache(Object o) {
//        Map<String,Object> map = JsonUtils.object2Map(o);
        localCache.set(o);
    }

    public static final  Object getTarget() {
        return localCache.get();
    }

    public static final void cleanLocalCache() {
        localCache.remove();
    }

    /**
     * 先清空缓存 设置目标数据到线程缓存
     */
    public static final <V extends Object> void clearAndSetLocalCache(Object o) {
        cleanLocalCache();
        localCache.set(o);
    }

}




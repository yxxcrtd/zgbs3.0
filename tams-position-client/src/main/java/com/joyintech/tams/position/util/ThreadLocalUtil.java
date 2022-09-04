package com.joyintech.tams.position.util;

import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

public class ThreadLocalUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalUtil.class);
    /**
     * 线程缓存
     */
    private static ThreadLocal<ArrayList<ArrayList<String>>> localCache = ThreadLocal.withInitial(ArrayList<ArrayList<String>>::new);

    /**
     * 设置目标数据到线程缓存
     *
     */
    public static final <V extends Object> void setLocalCache(Object o) {
        Map<String,Object> map = JsonUtils.object2Map(o);
        String seqno = (String)map.get("seqno");
        String modelType = (String)map.get("modelType");
        ArrayList<String> tempList = new ArrayList<String>();
        tempList.add(seqno);
        tempList.add(modelType);
//        if(localCache.get()!=null && !StringUtil.isEmptyList(localCache.get())){
//            localCache.get().add(tempList);
//        }
        localCache.get().add(tempList);
    }

    public static final  ArrayList<ArrayList<String>> getTarget() {
        return localCache.get();
    }

    public static final void cleanLocalCache() {
        localCache.remove();
    }

//    /**
//     * 先清空缓存 设置目标数据到线程缓存
//     */
//    public static final <V extends Object> void clearAndSetLocalCache(Object o) {
//        cleanLocalCache();
//        localCache.set(o);
//    }

}




package com.joyintech.tams.exchange.service.trade.check.business;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author lifangpeng
 * @date 2022/4/6 16:51
 */
public enum CheckActions {

    /**
     * 单例
     */
    I;

    /**
     * 业务类型与数据检查器的映射Map
     */
    private static final Map<String, ICheck> ACTION_MAP = Maps.newHashMap();


    /**
     * 添加业务类型与数据检查器
     *
     * @param action        业务类型
     * @param businessCheck 数据检查器
     */
    public void put(String action, ICheck businessCheck) {
        ACTION_MAP.putIfAbsent(action, businessCheck);
    }

    /**
     * 通过业务类型获取数据检查器
     *
     * @param action 业务类型
     * @return 数据检查器
     */
    public ICheck action(String action) {
        return ACTION_MAP.get(action);
    }
}

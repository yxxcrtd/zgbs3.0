package com.joyintech.service.valuation.invest.util;

import com.google.common.collect.Lists;
import com.joyintech.framework.util.BeanUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @since  2019-08-21 10:43
 */
public class BeanUtils {

    /**
     *
     * @param listLinkedHashMap
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> mapToObject(List<Map<String, Object>> listLinkedHashMap, Class<T> clazz){
        List<T> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(listLinkedHashMap)) {
            for (Map<String, Object> item : listLinkedHashMap) {
                T entity = BeanUtil.mapToObject(item, clazz);
                list.add(entity);
            }
        }
        return list;
    }


}

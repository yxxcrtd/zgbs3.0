/*
 * 文件名：Reactor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年9月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.riskrds.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.risktrialrds.dao.SupervisionCalcDao;

/**
 * 联动映射配置
 * 
 * @author zhangrui1
 * @version 1.0 2020年9月22日
 * @see Reactor
 * @since 1.0
 */
public class Reactor {

    private String pageKey;

    private String infKey;

    private boolean isFrontControlled;

    private Set<Setting<?>> settings;

    public Reactor(String pageKey, String infKey, boolean isFrontControlled,
                   Set<Setting<?>> settings) {
        this.pageKey = pageKey;
        this.infKey = infKey;
        this.isFrontControlled = isFrontControlled;
        this.settings = settings;
    }

    public void execute(Map<String, Object> dataMap) {
        if (!CollectionUtils.isEmpty(settings)) {
            for (Setting<?> setting : settings) {
                if (setting.getType() == Setting.TYPE_VAL) {
                    Map<String, Object> newMap = new HashMap<>();
                    newMap.put(pageKey, dataMap.get(pageKey));
                    setting.setParamSet(newMap);
                    dataMap.put(infKey, setting.execute(null));
                }
                else if (setting.getType() == Setting.TYPE_MTD) {
                    setting.setParamSet(dataMap);
                    dataMap.put(infKey, setting.execute(null));
                }
                else {
                    setting.setParamSet(dataMap);
                    dataMap.put(infKey, setting.execute(sql -> {
                        if (StringUtil.isNotEmpty(sql)) {
                            return getSqlDao().selectOne(sql.toString());
                        }
                        return null;
                    }));
                }
            }
        }
    }

    public boolean isFrontControlled() {
        return isFrontControlled;
    }

    public SupervisionCalcDao getSqlDao() {
        return SpringContextUtil.getApplicationContext().getBean(SupervisionCalcDao.class);
    }
}

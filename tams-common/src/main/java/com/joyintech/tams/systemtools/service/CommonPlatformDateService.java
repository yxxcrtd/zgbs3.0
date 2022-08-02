/*
 * 文件名：PlatformDateImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年7月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.systemtools.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.systemtools.dao.CommonPlatformDateMapper;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * 公共基础数据平台日期获取
 * @author 金茂东
 * @version 1.0 2018年7月26日
 * @see CommonPlatformDateService
 * @since 1.0
 */
@Primary
@Service("serviceCommonPlatformDateService")
public class CommonPlatformDateService implements IPlatformDate {
    /**
     * 系统日期工具Mapper
     */
    @Autowired
    private CommonPlatformDateMapper commonPlatformDateMapper;

    @Autowired
    private SysDateRefreshPositionCache sysDateRefreshPositionCache;
    /**
     * 获取平台日期
     * <功能详细描述>
     * @return 日期
     * @see
     * @since 1.0
     */
    @Override
    public Date getSysDate() {
        return commonPlatformDateMapper.getSysDate();
    }

    /**
     * 更新平台日期
     * @param newDate 新平台日
     * @return 更新结果
     */
    @Override
    public int updateSysDate(Date newDate) {
        int result =  commonPlatformDateMapper.updateSysDate(newDate);
        if(result > 0) {
            // 更新缓存
            synchronized (this) {
                CacheUtil.clearAppCache(GlobalConstant.NOW_SYS_DATE);
                CacheUtil.putAppCache(GlobalConstant.NOW_SYS_DATE, newDate);
            }
            sysDateRefreshPositionCache.updateBalance(newDate);
        }
        return result;
    }
    /**
     * 获取数据库日期
     * <功能详细描述>
     * @return 日期
     * @see
     * @since 1.0
     */
    @Override
    public Date getDbDate() {
        return commonPlatformDateMapper.getDbDate();
    }
    
    /**
     * 获取平台日期字符串格式
     * <功能详细描述>
     * @return 日期
     * @see
     * @since 1.0
     */
    @Override
    public String getSysDateStr() {
        return DateUtil.format(getSysDate());
    }
}

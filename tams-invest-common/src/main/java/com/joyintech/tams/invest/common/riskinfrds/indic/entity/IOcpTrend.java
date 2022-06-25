/*
 * 文件名：IOcpTrend.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年8月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.riskinfrds.indic.entity;

import java.io.Serializable;

import com.joyintech.framework.util.StringUtil;

/**
 * 预占趋势信息
 * 
 * @author zhangrui1
 * @version 1.0 2020年8月11日
 * @see IOcpTrend
 * @since 1.0
 */
public interface IOcpTrend extends Serializable {

    /**
     * 取得 预占实占编号
     * 
     * @return 预占实占编号
     */
    String getOcpCode();

    /**
     * 设置 预占实占编号
     * 
     * @param ocpCode 预占实占编号
     */
    void setOcpCode(String ocpCode);

    /**
     * 设置 趋势
     * 
     * @param trend 趋势
     */
    void setTrend(int trend);

    /**
     * 是否趋势向坏
     * 
     * @return true:趋势向坏
     */
    boolean isTrendBad();

    // Sonar Either override Object.equals(Object), or rename the method to prevent any confusion.
    default boolean equalsW(IOcpTrend other) {
        return other != null && StringUtil.equals(other.getOcpCode(), getOcpCode());
    }
}

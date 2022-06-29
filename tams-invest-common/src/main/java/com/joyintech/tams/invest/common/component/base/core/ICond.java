/*
 * 文件名：ICond.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.component.base.core;

import java.util.Date;
import java.util.List;

/**
 * 初始化REDIS相关条件
 * 
 * @author 裴家俊
 * @version 1.0 2019年9月20日
 * @see ICond
 * @since 1.0
 */
public interface ICond {

    Date getPlateDate();

    boolean isDel();

    boolean isLoadOffset();

    boolean isUpdParVal();

    List<String> getParamCodeArray();

    List<String> getIndicatorCodeArray();

    List<String> getPortfIdArray();

    List<String> getAssetIdArray();

    List<String> getCalendarIdArray();

    List<String> getPortfBagIdArray();
    
    List<String> getCondList();

    void setPlateDate(Date plateDate);

    String getSource();

}

/*
 * 文件名：ImplCond.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.redisinit.dto;

import com.joyintech.tams.invest.common.component.base.core.ICond;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * redis初始化条件类
 * @author 裴家俊
 * @version 1.0 2019年9月20日
 * @see ImplCond
 * @since 1.0
 */
public class ImplCond  implements ICond {

    /**
     * 指标参数CODE数组
     */
    private List<String> paramCodeArray;
    /**
     * 指标CODE数组
     */
    private List<String> indicatorCodeArray;
    /**
     * 组合CODE数组
     */
    private List<String> portfIdArray;

    /**
     * 组合包CODE数组
     */
    private List<String> portfBagIdArray;

    /**
     * 资产CODE数组
     */
    private List<String> assetIdArray;

    /**
     * 日历CODE数组
     */
    private List<String> calendarIdArray;

    /**
     * 平台日
     */
    private Date platDate;

    /**
     * 是否删除操作
     */
    private boolean isDel;
    
    /**
     * 是否批操作
     */
    private boolean isLoadOffset;

    /**
     * 是否更新参数值redis
     */
    private  boolean isUpdParVal;

    private String source;

    @Override
    public Date getPlateDate() {
        return platDate;
    }

    @Override
    public boolean isDel() {
        return isDel;
    }

    @Override
    public boolean isUpdParVal() {
        return isUpdParVal;
    }

    public void setIsUpdParVal(boolean isUpdParVal) {
        this.isUpdParVal = isUpdParVal;
    }

    @Override
    public List<String> getParamCodeArray() {
        return paramCodeArray;
    }

    public void setParamCodeArray(String... paramCodeArray) {
        this.paramCodeArray=new ArrayList<>();
        for (String str : paramCodeArray) {
            this.paramCodeArray.add(str);
        }
    }

    @Override
    public List<String> getIndicatorCodeArray() {
        return indicatorCodeArray;
    }

    public void setIndicatorCodeArray(String... indicatorCodeArray) {
        this.indicatorCodeArray=new ArrayList<>();
        for (String str : indicatorCodeArray) {
            this.indicatorCodeArray.add(str);
        }
    }

    @Override
    public List<String> getPortfIdArray() {
        return portfIdArray;
    }

    public void setPortfIdArray(String... portfIdArray) {
        this.portfIdArray=new ArrayList<>();
        for (String str : portfIdArray) {
            this.portfIdArray.add(str);
        }
    }

    @Override
    public void setPlateDate(Date plateDate) {
        platDate=plateDate;
    }

    public void setDel(boolean del) {
        isDel = del;
    }

    @Override
    public List<String> getPortfBagIdArray() {
        return portfBagIdArray;
    }

    public void setPortfBagIdArray(String... portfBagIdArray) {
        this.portfBagIdArray=new ArrayList<>();
        for (String str : portfBagIdArray) {
            this.portfBagIdArray.add(str);
        }
    }

    @Override
    public List<String> getAssetIdArray() {
        return assetIdArray;
    }

    public void setAssetIdArray(String... assetIdArray) {
        this.assetIdArray=new ArrayList<>();
        for (String str : assetIdArray) {
            this.assetIdArray.add(str);
        }
    }

    @Override
    public List<String> getCalendarIdArray() {
        return calendarIdArray;
    }

    public void setCalendarIdArray(String... calendarIdArray) {
        this.calendarIdArray = new ArrayList<>();
        for (String str : calendarIdArray) {
            this.calendarIdArray.add(str);
        }
    }

    @Override
	public boolean isLoadOffset() {
		return isLoadOffset;
	}

	public void setLoadOffset(boolean isLoadOffset) {
		this.isLoadOffset = isLoadOffset;
	}

	/**
	 * 查询条件列表
	 */
	private List<String> condList;
	
	/**
	 * 设置 查询条件
	 * 
	 * @param condList 查询条件
	 */
	public void setCondList(List<String> condList){
		this.condList = condList;
	}

	@Override
	public List<String> getCondList() {
		return condList;
	}

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

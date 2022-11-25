/*
 * 文件名：batchTaskTodo.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年8月13日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.invest.batch.common.dto;

import java.util.Date;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author wangzhiang
 * @version 1.0 2018年8月13日
 * @see DealAccountingTodo
 * @since 1.0
 */
public class DealAccountingTodo {

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 账套日期
     */
    private Date bookSetDate;

    /**
     * 入账日
     */
    private Date bookDate;

    /**
     * 发生日
     */
    private Date happenDate;

    /**
     * 业务类型
     */
    private String busiType;

    /**
     * 账务明细代码
     */
    private String bokDetailId;

    /**
     * 账套开始日
     */
    private Date startDate;
    

    /**
     * 账套结束日
     */
    private Date endDate;
    
    /**
     * 是否子账套
     */
    private String isSubBookset;
    
    /**
     * 账套类型
     */
    private String booksetType;
    
    /**
     * 获取 startDate
     * @return startDate.
     */
    public Date getStartDate() {
        return startDate;
    }


    
    /**
     * 设置 startDate
     * @param startDate startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


    
    /**
     * 获取 endDate
     * @return endDate.
     */
    public Date getEndDate() {
        return endDate;
    }


    
    /**
     * 设置 endDate
     * @param endDate endDate
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    /**
     * 获取 booksetId
     * @return booksetId.
     */
    public String getBooksetId() {
        return booksetId;
    }

    
    /**
     * 设置 booksetId
     * @param booksetId booksetId
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    
    /**
     * 获取 bookSetDate
     * @return bookSetDate.
     */
    public Date getBookSetDate() {
        return bookSetDate;
    }

    
    /**
     * 设置 bookSetDate
     * @param bookSetDate bookSetDate
     */
    public void setBookSetDate(Date bookSetDate) {
        this.bookSetDate = bookSetDate;
    }

    
    /**
     * 获取 bookDate
     * @return bookDate.
     */
    public Date getBookDate() {
        return bookDate;
    }

    
    /**
     * 设置 bookDate
     * @param bookDate bookDate
     */
    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    
    /**
     * 获取 happenDate
     * @return happenDate.
     */
    public Date getHappenDate() {
        return happenDate;
    }

    
    /**
     * 设置 happenDate
     * @param happenDate happenDate
     */
    public void setHappenDate(Date happenDate) {
        this.happenDate = happenDate;
    }

    
    /**
     * 获取 busiType
     * @return busiType.
     */
    public String getBusiType() {
        return busiType;
    }

    
    /**
     * 设置 busiType
     * @param busiType busiType
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    
    /**
     * 获取 bokDetailId
     * @return bokDetailId.
     */
    public String getBokDetailId() {
        return bokDetailId;
    }

    
    /**
     * 设置 bokDetailId
     * @param bokDetailId bokDetailId
     */
    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId;
    }



	public String getIsSubBookset() {
		return isSubBookset;
	}



	public void setIsSubBookset(String isSubBookset) {
		this.isSubBookset = isSubBookset;
	}



	public String getBooksetType() {
		return booksetType;
	}



	public void setBooksetType(String booksetType) {
		this.booksetType = booksetType;
	}


}

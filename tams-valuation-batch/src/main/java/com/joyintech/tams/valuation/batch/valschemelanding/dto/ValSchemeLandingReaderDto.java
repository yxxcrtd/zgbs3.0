/*
 * 文件名：ValBatchTaskTodoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：ruanhongming
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V1.0
 */

package com.joyintech.tams.valuation.batch.valschemelanding.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 待处理账套Dto
 * 
 * @author weipeng
 * @version 1.0 2022年3月28日
 * @see ValSchemeLandingReaderDto
 * @since 1.0
 */
public class ValSchemeLandingReaderDto extends NeedOperateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 账套日期
     */
    private Date booksetDate;

    /**
     * 入账日
     */
    private Date bookDate;

    /**
     * 发生日
     */
    private Date happenDate;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 证券品种
     */
    private String secType;

    /**
     *  交易场所
     */
    private String tradeMarket;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     *  资产类型
     */
    private String type1;

    /**
     * 核算分类
     */
    private String type2;

    /**
     * 取得 账套代码
     * 
     * @return 账套代码
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 账套代码
     * 
     * @param booksetId 账套代码
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    /**
     * 取得 账套日期
     * 
     * @return 账套日期
     */
    public Date getBooksetDate() {
        return booksetDate;
    }

    /**
     * 设置 账套日期
     * 
     * @param booksetDate 账套日期
     */
    public void setBooksetDate(Date booksetDate) {
        this.booksetDate = booksetDate;
    }

    /**
     * 取得 入账日
     * 
     * @return 入账日
     */
    public Date getBookDate() {
        return bookDate;
    }

    /**
     * 设置 入账日
     * 
     * @param bookDate 入账日
     */
    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    /**
     * 取得 发生日
     * 
     * @return 发生日
     */
    public Date getHappenDate() {
        return happenDate;
    }

    /**
     * 设置 发生日
     * 
     * @param happenDate 发生日
     */
    public void setHappenDate(Date happenDate) {
        this.happenDate = happenDate;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }
}

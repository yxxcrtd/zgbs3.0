/*
 * 文件名：BatchGrpPri.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年8月14日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.invest.batch.common.dto;

import com.joyintech.framework.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangzhiang
 * @version 1.0 2018年8月14日
 * @see BatchGrpPri
 * @since 1.0
 */
public class BatchGrpPri extends BaseEntity {
    /**
     * 批处理任务组执行ID
     */
    private String detailSeqNo;
    /**
     * 任务组实例ID
     */
    private String groupSeqNo;
    /**
     * 执行日期
     */
    private Date executeDate;
    /**
     * 优先级
     */
    private BigDecimal pri;

    /**
     * 
     * @param detailSeqNo
     * @param groupSeqNo
     * @param executeDate
     * @param pri
     */
    public BatchGrpPri(String detailSeqNo, String groupSeqNo, Date executeDate, BigDecimal pri) {
        super();
        this.detailSeqNo = detailSeqNo;
        this.groupSeqNo = groupSeqNo;
        this.executeDate = executeDate;
        this.pri = pri;
    }
    /**
     *  取得 批处理任务组执行ID
     *  @return 批处理任务组执行ID
     */
    public String getDetailSeqNo() {
       return detailSeqNo;
    }

    /**
     *  设置 批处理任务组执行ID
     *  @param detailSeqNo 批处理任务组执行ID
     */
    public void setDetailSeqNo( String detailSeqNo)  {
       this.detailSeqNo = detailSeqNo;
    }

    /**
     *  取得 任务组实例ID
     *  @return 任务组实例ID
     */
    public String getGroupSeqNo() {
       return groupSeqNo;
    }

    /**
     *  设置 任务组实例ID
     *  @param groupSeqNo 任务组实例ID
     */
    public void setGroupSeqNo( String groupSeqNo)  {
       this.groupSeqNo = groupSeqNo;
    }

    /**
     *  取得 执行日期
     *  @return 执行日期
     */
    public Date getExecuteDate() {
       return executeDate;
    }

    /**
     *  设置 执行日期
     *  @param executeDate 执行日期
     */
    public void setExecuteDate( Date executeDate)  {
       this.executeDate = executeDate;
    }

    /**
     *  取得 优先级
     *  @return 优先级
     */
    public BigDecimal getPri() {
       return pri;
    }

    /**
     *  设置 优先级
     *  @param pri 优先级
     */
    public void setPri( BigDecimal pri)  {
       this.pri = pri;
    }

}

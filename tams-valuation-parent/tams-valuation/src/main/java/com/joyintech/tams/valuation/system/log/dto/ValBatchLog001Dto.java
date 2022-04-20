/*
 * 文件名：ValBatchLog001Dto.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：倪怀
 * 修改时间：2018年2月23日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.tams.valuation.system.log.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.constant.BatchDictCode;
import com.joyintech.framework.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.SysDataUtil;

/**
 * 批处理日志dto
 * 
 * @author ww1
 * @version 1.0 2018年2月23日
 * @see ValBatchLog001Dto
 * @since 1.0
 */
public class ValBatchLog001Dto extends BaseEntity implements ITableRow {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * executeinfo的seqNo也即是executelog的seqNo
     */
    private String seqNo;

    /**
     * 任务代码
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 所属任务组代码
     */
    private String taskGroupId;

    /**
     * 所属任务组
     */
    private String taskGroupName;

    /**
     * 产品组代码
     */
    private String prdGroupId;

    /**
     * 产品组名称
     */
    private String prdGroupName;

    /**
     * 业务开始日期
     */
    private String businessStartDate;

    /**
     * 业务截止日期
     */
    private String businessEndDate;

    /**
     * 批处理执行日
     */
    private String batchDate;
    
    /**
     * 耗时
     */
    private Long processTime;

    /**
     * 批处理执行开始时间
     */
    private String batchStartTime;

    /**
     * 批处理执行终止时间
     */
    private String batchEndTime;

    /**
     * 批处理执行状态
     */
    private String taskStatusString;

    /**
     * 批处理启动模式
     */
    private String startMode;

    /**
     * 任务创建人
     */
    private String createUser;

    /**
     * 日志详情
     */
    private String logInfo;
    
    /**
     * 是否警告
     */
    private String isWarn;
    
    /**
     * 批处理任务组执行ID
     */
    private String batchExedetailSeqNo;


    /**
     * 账套id
     */
    private String booksetId;


    /**
     * 金融产品ID
     */
    private String finprodId;

    /**
     * 业务编码
     */
    private String busiId;
    /**
     * 详细日志
     */
    private String resultInfo;
    /**
     * 扩展明细信息
     */
    private String detailEx;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 详情日志主键
     */
    private String exeSeqno;

    public String getExeSeqno() {
        return exeSeqno;
    }

    public void setExeSeqno(String exeSeqno) {
        this.exeSeqno = exeSeqno;
    }

    public String getBooksetId() {
        return booksetId;
    }

    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getBusiId() {
        return busiId;
    }

    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(String resultInfo) {
        this.resultInfo = resultInfo;
    }

    public String getDetailEx() {
        return detailEx;
    }

    public void setDetailEx(String detailEx) {
        this.detailEx = detailEx;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBatchExedetailSeqNo() {
		return batchExedetailSeqNo;
	}

	public void setBatchExedetailSeqNo(String batchExedetailSeqNo) {
		this.batchExedetailSeqNo = batchExedetailSeqNo;
	}

	/**
     * 获取 logInfo
     *
     * @return logInfo
     */
    public String getLogInfo() {
        return logInfo;
    }

    /**
     * 设置 log
     *
     * @param logInfo logInfo
     */
    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    /**
     * 获取 seqNo
     *
     * @return seqNo
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     * 设置 seqNo
     *
     * @param seqNo seqNo
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    /**
     * 获取 taskId
     * 
     * @return taskId.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * 设置 taskId
     * 
     * @param taskId taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 获取 taskName
     * 
     * @return taskName.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * 设置 taskName
     * 
     * @param taskName taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * 获取 taskGroupId
     * 
     * @return taskGroupId.
     */
    public String getTaskGroupId() {
        return taskGroupId;
    }

    /**
     * 设置 taskGroupId
     * 
     * @param taskGroupId taskGroupId
     */
    public void setTaskGroupId(String taskGroupId) {
        this.taskGroupId = taskGroupId;
    }

    /**
     * 获取 taskGroupName
     * 
     * @return taskGroupName.
     */
    public String getTaskGroupName() {
        return taskGroupName;
    }

    /**
     * 设置 taskGroupName
     * 
     * @param taskGroupName taskGroupName
     */
    public void setTaskGroupName(String taskGroupName) {
        this.taskGroupName = taskGroupName;
    }

    /**
     * 获取 prdGroupId
     * 
     * @return prdGroupId.
     */
    public String getPrdGroupId() {
        return prdGroupId;
    }

    /**
     * 设置 prdGroupId
     * 
     * @param prdGroupId prdGroupId
     */
    public void setPrdGroupId(String prdGroupId) {
        this.prdGroupId = prdGroupId;
    }

    /**
     * 获取 prdGroupName
     * 
     * @return prdGroupName.
     */
    public String getPrdGroupName() {
        return prdGroupName;
    }

    /**
     * 设置 prdGroupName
     * 
     * @param prdGroupName prdGroupName
     */
    public void setPrdGroupName(String prdGroupName) {
        this.prdGroupName = prdGroupName;
    }

    /**
     * 获取 businessStartDate
     * 
     * @return businessStartDate.
     */
    public String getBusinessStartDate() {
        return businessStartDate;
    }

    /**
     * 设置 businessStartDate
     * 
     * @param businessStartDate businessStartDate
     */
    public void setBusinessStartDate(String businessStartDate) {
        this.businessStartDate = businessStartDate;
    }

    /**
     * 获取 businessEndDate
     * 
     * @return businessEndDate.
     */
    public String getBusinessEndDate() {
        return businessEndDate;
    }

    /**
     * 设置 businessEndDate
     * 
     * @param businessEndDate businessEndDate
     */
    public void setBusinessEndDate(String businessEndDate) {
        this.businessEndDate = businessEndDate;
    }

    /**
     * 获取 batchDate
     * 
     * @return batchDate.
     */
    public String getBatchDate() {
        return batchDate;
    }

    /**
     * 设置 batchDate
     * 
     * @param batchDate batchDate
     */
    public void setBatchDate(String batchDate) {
        this.batchDate = batchDate;
    }

    /**
     * 获取 batchStartTime
     * 
     * @return batchStartTime.
     */
    public String getBatchStartTime() {
        return batchStartTime;
    }

    /**
     * 设置 batchStartTime
     * 
     * @param batchStartTime batchStartTime
     */
    public void setBatchStartTime(String batchStartTime) {
        this.batchStartTime = batchStartTime;
    }

    /**
     * 获取 batchEndTime
     * 
     * @return batchEndTime.
     */
    public String getBatchEndTime() {
        return batchEndTime;
    }

    /**
     * 设置 batchEndTime
     * 
     * @param batchEndTime batchEndTime
     */
    public void setBatchEndTime(String batchEndTime) {
        this.batchEndTime = batchEndTime;
    }

    /**
     * 获取 taskStatus
     * 
     * @return taskStatus.
     */
    public String getTaskStatus() {
        return SysDataUtil.getNameFromDataDict(BatchDictCode.批处理执行状态, this.taskStatusString);
    }

    /**
     * 获取 startMode
     * 
     * @return startMode.
     */
    public String getStartMode() {
        return SysDataUtil.getNameFromDataDict(BatchDictCode.批处理启动模式, this.startMode);
    }

    /**
     * 设置 startMode
     * 
     * @param startMode startMode
     */
    public void setStartMode(String startMode) {
        this.startMode = startMode;
    }

    /**
     * 获取 createUser
     * 
     * @return createUser.
     */
    public String getCreateUser() {
        return CacheUtil.getAppParamCache(GlobalConstant.SYS_USER_KEY, this.createUser);
    }

    /**
     * 设置 createUser
     * 
     * @param createUser createUser
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * 获取 taskStatusString
     *
     * @return taskStatusString
     */
    public String getTaskStatusString() {
        return taskStatusString;
    }

    /**
     * 设置 taskStatusString
     *
     * @param taskStatusString taskStatusString
     */
    public void setTaskStatusString(String taskStatusString) {
        this.taskStatusString = taskStatusString;
    }

    public Long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Long processTime) {
        this.processTime = processTime;
    }

	public String getIsWarn() {
		return isWarn;
	}
	public void setIsWarn(String isWarn) {
		this.isWarn = isWarn;
	}


}

/*
 * 文件名：BatchTaskTodo.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年8月13日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.invest.batch.common.dto;


/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangzhiang
 * @version 1.0 2018年8月13日
 * @see BatchTaskInfo
 * @since 1.0
 */
public class BatchTaskInfo {

    /**
     * 批处理任务组执行ID
     */
    private String batchSeqNo;

    /**
     * 任务代码
     */
    private String taskId;

    /**
     * 优先级
     */
    private String taskPri;
    
    /**
     * 前序任务代码
     */
    private String pTaskId;

    /**
     * 业务类型
     */
    private String busiType;

    /**
     * 是否全量账务明细
     */
    private String allDetail;

    /**
     * 跑批规则
     */
    private String taskExeRule;
    
    /**
     * 账套类型
     */
    private String booksetType;
    
    
    /**
     * 获取 batchSeqNo
     * @return batchSeqNo.
     */
    public String getBatchSeqNo() {
        return batchSeqNo;
    }


    
    /**
     * 设置 batchSeqNo
     * @param batchSeqNo batchSeqNo
     */
    public void setBatchSeqNo(String batchSeqNo) {
        this.batchSeqNo = batchSeqNo;
    }


    /**
     * 获取 taskId
     * @return taskId.
     */
    public String getTaskId() {
        return taskId;
    }

    
    /**
     * 设置 taskId
     * @param taskId taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    
    
    /**
     * 获取 taskPri
     * @return taskPri.
     */
    public String getTaskPri() {
        return taskPri;
    }



    
    /**
     * 设置 taskPri
     * @param taskPri taskPri
     */
    public void setTaskPri(String taskPri) {
        this.taskPri = taskPri;
    }



    /**
     * 获取 pTaskId
     * @return pTaskId.
     */
    public String getpTaskId() {
        return pTaskId;
    }

    
    /**
     * 设置 pTaskId
     * @param pTaskId pTaskId
     */
    public void setpTaskId(String pTaskId) {
        this.pTaskId = pTaskId;
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
     * 获取 allDetail
     * @return allDetail.
     */
    public String getAllDetail() {
        return allDetail;
    }

    
    /**
     * 设置 allDetail
     * @param allDetail allDetail
     */
    public void setAllDetail(String allDetail) {
        this.allDetail = allDetail;
    }

    
    /**
     * 获取 taskExeRule
     * @return taskExeRule.
     */
    public String getTaskExeRule() {
        return taskExeRule;
    }

    
    /**
     * 设置 taskExeRule
     * @param taskExeRule taskExeRule
     */
    public void setTaskExeRule(String taskExeRule) {
        this.taskExeRule = taskExeRule;
    }



	public String getBooksetType() {
		return booksetType;
	}



	public void setBooksetType(String booksetType) {
		this.booksetType = booksetType;
	}
    
}

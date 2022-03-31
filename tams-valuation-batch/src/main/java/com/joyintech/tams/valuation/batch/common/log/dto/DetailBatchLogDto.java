package com.joyintech.tams.valuation.batch.common.log.dto;
/*
 * @ClassName DetailBatchLog
 * @Description 详情日志实体类
 * @Date 2022/3/28 14:18
 * @Author 殷亚
 * @Version 1.0
 **/

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetailBatchLogDto {
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //任务组ID
    private String groupId;
    //任务组名称
    private String groupName;
    //任务ID
    private String taskId;
    //任务名称
    private String taskName;
    //任务调度bean
    private String taskBeanName;
    //开始时间
    private LocalDateTime start;
    //结束时间
    private LocalDateTime end;
    //类名
    private String className;
    //方法名
    private String methodName;
    //参数
    private Object[] args;
    //账套ID
    private String booksetId;
    //金融产品ID
    private String finprodId;
    //业务编码
    private String busiId;
    //执行结果
    private Object result;
    //扩展明细
    private List<DetailBatchLogExpand> detailList;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskBeanName() {
        return taskBeanName;
    }

    public void setTaskBeanName(String taskBeanName) {
        this.taskBeanName = taskBeanName;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public List<DetailBatchLogExpand> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DetailBatchLogExpand> detailList) {
        this.detailList = detailList;
    }
}

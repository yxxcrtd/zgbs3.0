package com.joyintech.tams.valuation.batch.common.log;
/*
 * @ClassName DetailBatchLog
 * @Description 详情日志实体类
 * @Date 2022/3/28 14:18
 * @Author 殷亚
 * @Version 1.0
 **/

import com.alibaba.fastjson.JSON;
import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetailBatchLog {
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
    //账套ID
    private String booksetId;
    //业务代码
    private String busiId;
    //业务明细代码
    private String bokDetailId;
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
    //执行结果
    private Object result;
    //明细日志
    private List<DetailBatchLog> detailList;


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

    public String getBooksetId() {
        return booksetId;
    }

    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    public String getBusiId() {
        return busiId;
    }

    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }

    public String getBokDetailId() {
        return bokDetailId;
    }

    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId;
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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public List<DetailBatchLog> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DetailBatchLog> detailList) {
        this.detailList = detailList;
    }


}

package com.joyintech.tams.valuation.batch.common.log.dto;
/*
 * @ClassName DetailBatchLog
 * @Description 详情日志实体类
 * @Date 2022/3/28 14:18
 * @Author 殷亚
 * @Version 1.0
 **/

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.base.BaseEntity;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class DetailBatchLogDto extends BaseEntity {
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //执行信息seq =sys_batch_exec_info.seqno
    private String exeSeqno;
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
    //执行时间
    private Date excuteDate;
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

    private String argsClob;
    //账套ID
    private String booksetId;
    //金融产品ID
    private String finprodId;
    //业务编码
    private String busiId;
    //执行结果
    private Object resultInfo;
    //扩展明细
    private List<CustomDetailBatchLogDto> detailList;
    private String detailEx;

    /**
     * 文件路径
     */
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExeSeqno() {
        return exeSeqno;
    }

    public void setExeSeqno(String exeSeqno) {
        this.exeSeqno = exeSeqno;
    }

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

    public Date getExcuteDate() {
        return excuteDate;
    }

    public void setExcuteDate(Date excuteDate) {
        this.excuteDate = excuteDate;
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
        this.argsClob =JSON.toJSONString(args);
    }

    public String getArgsClob() {
        return argsClob;
    }

    public void setArgsClob(String argsClob) {
        this.argsClob = argsClob;
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

    public Object getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(Object resultInfo) {
        this.resultInfo = resultInfo;
    }

    public List<CustomDetailBatchLogDto> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<CustomDetailBatchLogDto> detailList) {
        this.detailList = detailList;
        this.detailEx =getDetailJson(detailList);
    }

    public String getDetailEx() {
        return detailEx;
    }

    public void setDetailEx(String detailEx) {
        this.detailEx = detailEx;
    }

    /* @Author 殷亚
     * @Description TODO 组装日志明细的json，中文描述反射调用注解生成
     * @Date  2022/3/30 15:19
     * @Version 1.0
     * @Param [dto]
     * @Return java.lang.String
     **/
    public String getDetailJson(List<CustomDetailBatchLogDto> list){
        StringBuffer json=new StringBuffer();
        Field[] field = CustomDetailBatchLogDto.class.getDeclaredFields();
        Field[] pfield = CustomDetailBatchLogDto.class.getSuperclass().getDeclaredFields();
        json.append("[");
        for(CustomDetailBatchLogDto dto:list) {
            json.append("{");
            if (pfield != null) {
                for (Field fie : pfield) {
                    if (!fie.isAccessible()) {
                        fie.setAccessible(true);
                    }
                    try {
                        String val = (String) fie.get(dto);
                        DetailLogConfig annon = fie.getAnnotation(DetailLogConfig.class);
                        String dis = annon.description();
                        boolean show = annon.show();
                        if (show&&val!=null) {
                            json.append(dis + ":" + val + ",");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
            if (field != null) {
                for (Field fie : field) {
                    if (!fie.isAccessible()) {
                        fie.setAccessible(true);
                    }
                    try {
                        String val = (String) fie.get(dto);
                        DetailLogConfig annon = fie.getAnnotation(DetailLogConfig.class);
                        String dis = annon.description();
                        boolean show = annon.show();
                        if (show&&val!=null) {
                            json.append(dis + ":" + val + ",");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
                json.delete(json.length() - 1, json.length());
            }


            json.append("},");
        }
        json.delete(json.length() - 1, json.length());
        json.append("]");
        return json.toString();
    }
}

package com.joyintech.tams.exchange.common.db.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 指令分发数据记录
 *
 * @author lifangpeng
 * @date 2022/4/2 11:41
 */
public class RecordLog implements Serializable {
    private static final long serialVersionUID = -474744290921878236L;

    /**
     * 主键
     */
    private String id;
    /**
     * 业务编号1
     */
    private String busiCode1;
    /**
     * 业务编号2
     */
    private String busiCode2;
    /**
     * 业务编号3
     */
    private String busiCode3;
    /**
     * 记录信息
     */
    private String recordInfo;
    /**
     * 组合信息
     */
    private String detailInfo;
    /**
     * 质押券信息
     */
    private String pledgeInfo;
    /**
     * 类型：类型：0-指令分发，1-成交，2-手动交收，3-执行进度
     */
    private int logType;
    /**
     * 状态：0-新建，1-检查通过，2-检查不通过
     */
    private int status;
    /**
     * 检查结果描述
     */
    private String checkMsg;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 修改时间
     */
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusiCode1() {
        return busiCode1;
    }

    public void setBusiCode1(String busiCode1) {
        this.busiCode1 = busiCode1;
    }

    public String getBusiCode2() {
        return busiCode2;
    }

    public void setBusiCode2(String busiCode2) {
        this.busiCode2 = busiCode2;
    }

    public String getBusiCode3() {
        return busiCode3;
    }

    public void setBusiCode3(String busiCode3) {
        this.busiCode3 = busiCode3;
    }

    public String getRecordInfo() {
        return recordInfo;
    }

    public void setRecordInfo(String recordInfo) {
        this.recordInfo = recordInfo;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getPledgeInfo() {
        return pledgeInfo;
    }

    public void setPledgeInfo(String pledgeInfo) {
        this.pledgeInfo = pledgeInfo;
    }

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCheckMsg() {
        return checkMsg;
    }

    public void setCheckMsg(String checkMsg) {
        this.checkMsg = checkMsg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

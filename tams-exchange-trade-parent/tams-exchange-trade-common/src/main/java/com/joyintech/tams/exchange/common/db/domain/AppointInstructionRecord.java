package com.joyintech.tams.exchange.common.db.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 指令分发数据记录
 *
 * @author lifangpeng
 * @date 2022/4/2 11:41
 */
public class AppointInstructionRecord implements Serializable {
    private static final long serialVersionUID = -474744290921878236L;

    /**
     * 主键
     */
    private String id;
    /**
     * 指令编号
     */
    private String entrustId;
    /**
     * 指令信息
     */
    private String entrustInfo;
    /**
     * 组合信息
     */
    private String detailInfo;
    /**
     * 质押券信息
     */
    private String pledgeInfo;
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
     * 创建时间
     */
    private Date createTime;
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

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getEntrustInfo() {
        return entrustInfo;
    }

    public void setEntrustInfo(String entrustInfo) {
        this.entrustInfo = entrustInfo;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

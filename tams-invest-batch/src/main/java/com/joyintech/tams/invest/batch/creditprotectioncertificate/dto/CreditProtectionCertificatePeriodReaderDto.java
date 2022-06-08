package com.joyintech.tams.invest.batch.creditprotectioncertificate.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;

import java.util.Date;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
public class CreditProtectionCertificatePeriodReaderDto extends NeedOperateEntity {
    private String bussinessCode;

    private String finprodId;

    private String workflowId;
    private String createUser;
    private String createDept;
    private Date createTime;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateDept() {
        return createDept;
    }

    public void setCreateDept(String createDept) {
        this.createDept = createDept;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBussinessCode() {
        return bussinessCode;
    }

    public void setBussinessCode(String bussinessCode) {
        this.bussinessCode = bussinessCode;
    }
}

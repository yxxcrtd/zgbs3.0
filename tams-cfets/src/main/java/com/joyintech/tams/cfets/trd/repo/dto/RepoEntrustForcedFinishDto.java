package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.util.List;

/**
 * @author jiangpeng
 * @date 2022/01/13
 */
public class RepoEntrustForcedFinishDto extends BaseEntity implements ProcessEntity {

    @PrimaryKey(PrimaryKey.Field.PK_1)
    private String entrustId;

    private String loginUserId;

    private String entrustIds;

    private List<RepoEntrustDto> entrustList;

    private List<RepoDealTransactionDetailsDto> dealTransactionList;

    /**
     * 强制完成原因
     */
    private String remark;

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

    public String getEntrustIds() {
        return entrustIds;
    }

    public void setEntrustIds(String entrustIds) {
        this.entrustIds = entrustIds;
    }

    public List<RepoEntrustDto> getEntrustList() {
        return entrustList;
    }

    public void setEntrustList(List<RepoEntrustDto> entrustList) {
        this.entrustList = entrustList;
    }

    public List<RepoDealTransactionDetailsDto> getDealTransactionList() {
        return dealTransactionList;
    }

    public void setDealTransactionList(List<RepoDealTransactionDetailsDto> dealTransactionList) {
        this.dealTransactionList = dealTransactionList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}


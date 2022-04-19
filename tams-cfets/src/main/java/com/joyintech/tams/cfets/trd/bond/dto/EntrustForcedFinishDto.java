package com.joyintech.tams.cfets.trd.bond.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.util.List;

/**
 * @author zznn
 * @date 2021/12/23
 */
public class EntrustForcedFinishDto extends BaseEntity implements ProcessEntity {

    @PrimaryKey(PrimaryKey.Field.PK_1)
    private String entrustId;

    private String loginUserId;

    private String entrustIds;

    private List<EntrustDto> entrustList;

    private List<DealTransactionDetailsDto> dealTransactionList;

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

    public List<EntrustDto> getEntrustList() {
        return entrustList;
    }

    public void setEntrustList(List<EntrustDto> entrustList) {
        this.entrustList = entrustList;
    }

    public List<DealTransactionDetailsDto> getDealTransactionList() {
        return dealTransactionList;
    }

    public void setDealTransactionList(List<DealTransactionDetailsDto> dealTransactionList) {
        this.dealTransactionList = dealTransactionList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}


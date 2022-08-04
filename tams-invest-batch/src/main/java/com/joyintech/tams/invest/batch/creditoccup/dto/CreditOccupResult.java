package com.joyintech.tams.invest.batch.creditoccup.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;

import java.util.Date;
import java.util.List;

/**
 * write结果
 * @author zznn
 * @date 2021/8/10
 */
public class CreditOccupResult extends NeedToWriteEntity {

    private List<CreditUsingDto> CreditUsings;

    private String updateUser;

    private Date updateTime;

    public List<CreditUsingDto> getCreditUsings() {
        return CreditUsings;
    }

    public void setCreditUsings(List<CreditUsingDto> creditUsings) {
        CreditUsings = creditUsings;
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


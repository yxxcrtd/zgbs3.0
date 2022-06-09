package com.joyintech.tams.invest.batch.creditprotectioncertificate.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;

import java.util.List;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
public class CreditProtectionCertificatePeriodWriterDto extends NeedToWriteEntity {
    private String bussinessCode;
    private String finprodId;

    private List<CreditProtectionCertificatePeriodExpireDataDto> expireDatas;

    public String getBussinessCode() {
        return bussinessCode;
    }

    public void setBussinessCode(String bussinessCode) {
        this.bussinessCode = bussinessCode;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public List<CreditProtectionCertificatePeriodExpireDataDto> getExpireDatas() {
        return expireDatas;
    }

    public void setExpireDatas(List<CreditProtectionCertificatePeriodExpireDataDto> expireDatas) {
        this.expireDatas = expireDatas;
    }
}

package com.joyintech.tams.component.business.riskall.dto;

import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesOutputDto;

import java.util.List;

public class RiskCommonOutputDto {

    /**
     * 名单试算结果
     */
    List<RiskNameListResult> riskNameListResultList;
    /**
     * 指标试算结果明细
     */
    private List<RiskTrialRetDto> riskTrialRetDto;
    /**
     * 指标试算结果
     */
    private String rskStatus;
    /**
     * 授信试算结果明细
     */
    private List<CreditLinesOutputDto> creditResultList;
    /**
     * 授信试算结果
     */
    private String creditStatus;
    /**
     * 授权试算结果
     */
    private List<RiskAuthorizeDto> authorizeList;

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public String getRskStatus() {
        return rskStatus;
    }

    public void setRskStatus(String rskStatus) {
        this.rskStatus = rskStatus;
    }

    public List<CreditLinesOutputDto> getCreditResultList() {
        return creditResultList;
    }

    public void setCreditResultList(List<CreditLinesOutputDto> creditResultList) {
        this.creditResultList = creditResultList;
    }

    public List<RiskAuthorizeDto> getAuthorizeList() {
        return authorizeList;
    }

    public void setAuthorizeList(List<RiskAuthorizeDto> authorizeList) {
        this.authorizeList = authorizeList;
    }

    public String getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(String creditStatus) {
        this.creditStatus = creditStatus;
    }

    public List<RiskNameListResult> getRiskNameListResultList() {
        return riskNameListResultList;
    }

    public void setRiskNameListResultList(List<RiskNameListResult> riskNameListResultList) {
        this.riskNameListResultList = riskNameListResultList;
    }
}

package com.joyintech.tams.component.business.riskall.dto;

import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesOutputDto;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;

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
    private String rskTrialStatus;
    /**
     * 授信试算结果明细
     */
    private List<CreditLinesOutputDto> creditResultList;

    /**
     * 汇总试算结果
     */
    private String rskStatus;
    /**
     * 试算不通过，硬控提示信息
     */
    private String errorMsg;

    /**
     * 试算不通过，硬控提示信息
     */
    private String confirmMsg;

    /**
     * 风险审批结果
     */
    private String riskApproval;
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

    public String getRskTrialStatus() {
        return rskTrialStatus;
    }

    public void setRskTrialStatus(String rskTrialStatus) {
        this.rskTrialStatus = rskTrialStatus;
    }

    public String getRskStatus() {
        return rskStatus;
    }

    public void setRskStatus(String rskStatus) {
        this.rskStatus = rskStatus;
    }

    public String getRskStatusStr() {
        return SysDatadictUtil.getNameFromDataDict(DictCode.试算结果,rskStatus);
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

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public String getConfirmMsg() {
        return confirmMsg;
    }
    public void setConfirmMsg(String confirmMsg) {
        this.confirmMsg = confirmMsg;
    }
    public String getRiskApproval() {
        return riskApproval;
    }
    public void setRiskApproval(String riskApproval) {
        this.riskApproval = riskApproval;
    }

    public List<RiskNameListResult> getRiskNameListResultList() {
        return riskNameListResultList;
    }

    public void setRiskNameListResultList(List<RiskNameListResult> riskNameListResultList) {
        this.riskNameListResultList = riskNameListResultList;
    }
}

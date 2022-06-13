/*
 * 文件名：BussFinRepaymentDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dto;

import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  发行人机构信息
 * 〈功能详细描述〉
 * @author wangyang1
 * @version 1.0 2022年4月21日
 * @see InterfCustomerInfoDto
 * @since 1.0
 */
@ApiModel(value="发行人机构信息",description="发行人机构信息")
public class InterfCustomerInfoDto {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name="customerId",value="市场机构代码")
    private String customerId;

    @ApiModelProperty(name="customerAbbr",value="市场机构简称")
    private String customerAbbr;

    @ApiModelProperty(name="customerName",value="市场机构名称")
    private String customerName;

    @ApiModelProperty(name="hsType",value="核算分类")
    private String hsType;

    @ApiModelProperty(name="partyType3",value="机构类型（技术领域）")
    private String partyType3;

    @ApiModelProperty(name="partyType2",value="机构类型（经济类型）")
    private String partyType2;

    @ApiModelProperty(name="industry",value="所属行业")
    private String industry;

    @ApiModelProperty(name="industry2",value="行业二级分类")
    private String industry2;

    @ApiModelProperty(name="companyNature",value="企业性质")
    private String companyNature;

    @ApiModelProperty(name="partyType1",value="机构类型（规模）")
    private String partyType1;

    @ApiModelProperty(name="insitituteCode",value="组织机构代码/统一社会信用代码")
    private String insitituteCode;

    @ApiModelProperty(name="isGov",value="是否政府融资平台")
    private String isGov;

    @ApiModelProperty(name="mainCapital",value="核心资本（万元）")
    private BigDecimal mainCapital;

    @ApiModelProperty(name="registerCapital",value="注册资本（万元）")
    private BigDecimal registerCapital;

    @ApiModelProperty(name="province",value="所属省")
    private String province;

    @ApiModelProperty(name="city",value="所属市")
    private String city;

    @ApiModelProperty(name="county",value="所属县")
    private String county;

    @ApiModelProperty(name="holdingType",value="控股类型")
    private String holdingType;

    @ApiModelProperty(name="CUSTOMER_OBJ_TYPE",value="客户主体类型")
    private String customerObjType;

    @ApiModelProperty(name="isListedCompany",value="是否上市公司")
    private String isListedCompany;

    @ApiModelProperty(name="isGroupCustomer",value="是否集团客户")
    private String isGroupCustomer;

    @ApiModelProperty(name="parentCustomerId",value="所属集团客户编号")
    private String parentCustomerId;

    @ApiModelProperty(name="parentCustomerName",value="所属集团客户名称")
    private String parentCustomerName;

    @ApiModelProperty(name="isPrivate",value="是否民营企业")
    private String isPrivate;

    @ApiModelProperty(name="isEscapeDept",value="是否为逃废债企业")
    private String isEscapeDept;

    @ApiModelProperty(name="creditType",value="授信策略")
    private String creditType;

    @ApiModelProperty(name="isCreditRelatedTrd",value="是否授信类关联交易")
    private String isCreditRelatedTrd;

    @ApiModelProperty(name="structAdjustType",value="产业结构调整类型")
    private String structAdjustType;

    @ApiModelProperty(name="indusUpgrateType",value="工业转型升级标识")
    private String indusUpgrateType;

    @ApiModelProperty(name="mainBusi",value="主营业务")
    private String mainBusi;

    @ApiModelProperty(name="busiScope",value="经营范围")
    private String busiScope;

    @ApiModelProperty(name="remark",value="备注")
    private String remark;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerAbbr() {
        return customerAbbr;
    }

    public void setCustomerAbbr(String customerAbbr) {
        this.customerAbbr = customerAbbr;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getHsType() {
        return hsType;
    }

    public void setHsType(String hsType) {
        this.hsType = hsType;
    }

    public String getPartyType3() {
        return partyType3;
    }

    public void setPartyType3(String partyType3) {
        this.partyType3 = partyType3;
    }

    public String getPartyType2() {
        return partyType2;
    }

    public void setPartyType2(String partyType2) {
        this.partyType2 = partyType2;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getIndustry2() {
        return industry2;
    }

    public void setIndustry2(String industry2) {
        this.industry2 = industry2;
    }

    public String getCompanyNature() {
        return companyNature;
    }

    public void setCompanyNature(String companyNature) {
        this.companyNature = companyNature;
    }

    public String getPartyType1() {
        return partyType1;
    }

    public void setPartyType1(String partyType1) {
        this.partyType1 = partyType1;
    }

    public String getInsitituteCode() {
        return insitituteCode;
    }

    public void setInsitituteCode(String insitituteCode) {
        this.insitituteCode = insitituteCode;
    }

    public String getIsGov() {
        return isGov;
    }

    public void setIsGov(String isGov) {
        this.isGov = isGov;
    }

    public BigDecimal getMainCapital() {
        return mainCapital;
    }

    public void setMainCapital(BigDecimal mainCapital) {
        this.mainCapital = mainCapital;
    }

    public BigDecimal getRegisterCapital() {
        return registerCapital;
    }

    public void setRegisterCapital(BigDecimal registerCapital) {
        this.registerCapital = registerCapital;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getHoldingType() {
        return holdingType;
    }

    public void setHoldingType(String holdingType) {
        this.holdingType = holdingType;
    }

    public String getCustomerObjType() {
        return customerObjType;
    }

    public void setCustomerObjType(String customerObjType) {
        this.customerObjType = customerObjType;
    }

    public String getIsListedCompany() {
        return isListedCompany;
    }

    public void setIsListedCompany(String isListedCompany) {
        this.isListedCompany = isListedCompany;
    }

    public String getIsGroupCustomer() {
        return isGroupCustomer;
    }

    public void setIsGroupCustomer(String isGroupCustomer) {
        this.isGroupCustomer = isGroupCustomer;
    }

    public String getParentCustomerId() {
        return parentCustomerId;
    }

    public void setParentCustomerId(String parentCustomerId) {
        this.parentCustomerId = parentCustomerId;
    }

    public String getParentCustomerName() {
        return parentCustomerName;
    }

    public void setParentCustomerName(String parentCustomerName) {
        this.parentCustomerName = parentCustomerName;
    }

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getIsEscapeDept() {
        return isEscapeDept;
    }

    public void setIsEscapeDept(String isEscapeDept) {
        this.isEscapeDept = isEscapeDept;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public String getIsCreditRelatedTrd() {
        return isCreditRelatedTrd;
    }

    public void setIsCreditRelatedTrd(String isCreditRelatedTrd) {
        this.isCreditRelatedTrd = isCreditRelatedTrd;
    }

    public String getStructAdjustType() {
        return structAdjustType;
    }

    public void setStructAdjustType(String structAdjustType) {
        this.structAdjustType = structAdjustType;
    }

    public String getIndusUpgrateType() {
        return indusUpgrateType;
    }

    public void setIndusUpgrateType(String indusUpgrateType) {
        this.indusUpgrateType = indusUpgrateType;
    }

    public String getMainBusi() {
        return mainBusi;
    }

    public void setMainBusi(String mainBusi) {
        this.mainBusi = mainBusi;
    }

    public String getBusiScope() {
        return busiScope;
    }

    public void setBusiScope(String busiScope) {
        this.busiScope = busiScope;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

/*
 * 文件名：PtlManagementInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年9月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.portfolio.ptlmanagementinfo.dto;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.PtlValParam;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.portfolio.dto.FundAccountInfoDto;
import com.joyintech.tams.component.business.portfolio.dto.SecuritiesAccountInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.validate.PtlManagementInfo001Validator;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * 组合管理实体类
 * @author fanshuanghu
 * @version 1.0 2018年9月13日
 * @see PtlManagementInfoDto
 * @since 1.0
 */
@PageId("PtlManagementInfo002")
@ValidLogic(targetClass = PtlManagementInfo001Validator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class PtlManagementInfoDto extends PtlPortfolio implements ITableRow,ProcessEntity{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -286288564346458527L;
    /**
     * 是否保本
     */
    private String isGuarantee;
    
    /**
     * 开放类型，开放式、封闭式
     */
    @NotEmpty(fieldName="PtlManagementInfo002.issueMode",groups = {ValidateGroupEdit.class})
    private String openType;

    /**
     * 募集方式，公募、私募
     */
    @NotEmpty(fieldName="PtlManagementInfo002.raisetype",groups = {ValidateGroupEdit.class})
    private String raiseType;

    /**
     * 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     */
    @NotEmpty(fieldName="PtlManagementInfo002.investType",groups = {ValidateGroupEdit.class})
    private String investNature;
    /**
     * 选择的行
     */
    private List<PtlManagementInfoDto> selectedItem;
    /**
     * 操作类型
     */
    private String operateType;
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String pageModel;

    private List<String> userId;
    
    /**
     * 获取 userId
     * @return userId.
     */
    public List<String> getUserId() {
        return userId;
    }
    
    /**
     * 设置 userId
     * @param userId userId
     */
    public void setUserId(List<String> userId) {
        this.userId = userId;
    }
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String elementId;
    /**
     * 资金账户信息实体类
     */
    @Valid
    @NotEmpty(fieldName="PtlManagementInfo001.funInfo",groups = {ValidateGroupEdit.class})
    private List<FundAccountInfoDto> fundAccountInfoDto;
    /**
     * 证券账户实体类
     */
    @Valid
    @NotEmpty(fieldName="PtlManagementInfo001.secInfo",groups = {ValidateGroupEdit.class})
    private List<SecuritiesAccountInfoDto> securitiesAccountInfoDto;
    /**
     * 证券账户实体类
     */
    
    private PtlValParam ptlValParam;

    /**
     * 继承比例
     */
    private BigDecimal disPercent;

    /**
     * 投资策略名称
     */
    private String strategyName;
    /**
     * 是否有收益
     */
    public String isGuaranteeStr;

    /**
     * 子组合代码
     */
    private String childPortfolioId;
    /**
     * 子组合名称
     */
    private String childPortfolioName;

    /**
     * 附件信息DTO
     */
    private List<UploadFile> fileArray;

    /**
     * 组合系列
     */
    @Valid
    @NotEmpty(fieldName="PtlManagementInfo002.portfolioSeries",groups = {ValidateGroupEdit.class})
    private String portfolioSeries;


    public String getInvestNatureStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.投资性质, this.investNature);
    }
    public String getRaiseTypeStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.募集方式, this.raiseType);
    }
    public String getOpenTypeStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.开放类型, this.openType);
    }
    /**
     * 获取 openType
     * @return openType.
     */
    public String getOpenType() {
        return openType;
    }
    /**
     * 设置 openType
     * @param openType openType
     */
    public void setOpenType(String openType) {
        this.openType = openType;
    }
    /**
     * 获取 raiseType
     * @return raiseType.
     */
    public String getRaiseType() {
        return raiseType;
    }
    /**
     * 设置 raiseType
     * @param raiseType raiseType
     */
    public void setRaiseType(String raiseType) {
        this.raiseType = raiseType;
    }
    /**
     * 获取 investNature
     * @return investNature.
     */
    public String getInvestNature() {
        return investNature;
    }
    
    /**
     * 设置 investNature
     * @param investNature investNature
     */
    public void setInvestNature(String investNature) {
        this.investNature = investNature;
    }
    @Override
    @Length(fieldName="PtlManagementInfo001.portfolioName",max=200,groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
    @NotEmpty(fieldName="PtlManagementInfo001.portfolioName",groups = {ValidateGroupEdit.class})
    public String getPortfolioName(){
        return super.getPortfolioName();
    }
    @Override
    @NotEmpty(fieldName="PtlManagementInfo001.portfolioType", groups = {ValidateGroupEdit.class})
    public String getPortfolioType(){
        return super.getPortfolioType();
    }
    @Override
    @NotEmpty(fieldName="PtlManagementInfo001.revenueType", groups = {ValidateGroupEdit.class})
    public String getProfitType(){
        return super.getProfitType();
    }
    @Override
    @NotEmpty(fieldName="PtlManagementInfo001.vDate",groups = {ValidateGroupEdit.class})
    public Date getVdate(){
        return super.getVdate();
    }
    @Override
    @NotEmpty(fieldName="PtlManagementInfo001.mDate", groups = {ValidateGroupEdit.class})
    public Date getMdate(){
        return super.getMdate();
    }
    /**
     * 获取 isGuaranteeStr
     * @return isGuaranteeStr.
     */
    public String getIsGuaranteeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, isGuarantee);
    }
    
    /**
     * 设置 isGuaranteeStr
     * @param isGuaranteeStr isGuaranteeStr
     */
    public void setIsGuaranteeStr(String isGuaranteeStr) {
        this.isGuaranteeStr = isGuaranteeStr;
    }
    /**
     * 设置主键和限制
     * {@inheritDoc}
     */
    @Override
    @Length(fieldName="PtlManagementInfo001.portfolioId",max=32)
    @NotEmpty(fieldName="PtlManagementInfo001.portfolioId",groups = {ValidateGroupEdit.class})
    @PrimaryKey(Field.PK_1)
    public String getPortfolioId(){
        return super.getPortfolioId();
    }
    /**
     * 获取 fundAccountInfoDto
     * @return fundAccountInfoDto.
     */
    @Valid
    @NotEmpty(fieldName="PtlManagementInfo001.funInfo",groups = {ValidateGroupEdit.class})
    public List<FundAccountInfoDto> getFundAccountInfoDto() {
        return fundAccountInfoDto;
    }
    /**
     * 设置 fundAccountInfoDto
     * @param fundAccountInfoDto fundAccountInfoDto
     */
    public void setFundAccountInfoDto(List<FundAccountInfoDto> fundAccountInfoDto) {
        this.fundAccountInfoDto = fundAccountInfoDto;
    }
    /**
     * 获取 securitiesAccountInfoDto
     * @return securitiesAccountInfoDto.
     */
    @Valid
    @NotEmpty(fieldName="PtlManagementInfo001.secInfo",groups = {ValidateGroupEdit.class})
    public List<SecuritiesAccountInfoDto> getSecuritiesAccountInfoDto() {
        return securitiesAccountInfoDto;
    }
    /**
     * 设置 securitiesAccountInfoDto
     * @param securitiesAccountInfoDto securitiesAccountInfoDto
     */
    public void setSecuritiesAccountInfoDto(List<SecuritiesAccountInfoDto> securitiesAccountInfoDto) {
        this.securitiesAccountInfoDto = securitiesAccountInfoDto;
    }
    /**
     * 获取 ptlValParam
     * @return ptlValParam.
     */
    public PtlValParam getPtlValParam() {
        return ptlValParam;
    }
    /**
     * 设置 ptlValParam
     * @param ptlValParam ptlValParam
     */
    public void setPtlValParam(PtlValParam ptlValParam) {
        this.ptlValParam = ptlValParam;
    }
    /**
     * 有效状态
     * @return
     * @see
     * @since 1.0
     */
    public String getStatusStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.有效状态, DictCodeListExt1.有效状态_有效.getValue());
    }
    /**
     * 获取 profitTypeStr
     * @return profitTypeStr.
     */
    public String getProfitTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.收益类型,super.getProfitType());
    }
    /**
     * 获取 portfolioTypeStr
     * @return portfolioTypeStr.
     */
    public String getPortfolioTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.组合类型,super.getPortfolioType());
    }

    /**
     * 获取 portfolioSeriesStr
     * @return portfolioSeriesStr.
     */
    public String getPortfolioSeriesStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.组合系列,this.portfolioSeries);
    }
    /**
     * 获取 isGuarantee
     * @return isGuarantee.
     */
    public String getIsGuarantee() {
        return isGuarantee;
    }
    /**
     * 设置 isGuarantee
     * @param isGuarantee isGuarantee
     */
    public void setIsGuarantee(String isGuarantee) {
        this.isGuarantee = isGuarantee;
    }
    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<PtlManagementInfoDto> getSelectedItem() {
        return selectedItem;
    }
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<PtlManagementInfoDto> selectedItem) {
        this.selectedItem = selectedItem;
    }
    /**
     * 获取 operateType
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }
    /**
     * 设置 operateType
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
    /**
     * 获取 pageModel
     * @return pageModel.
     */
    public String getPageModel() {
        return pageModel;
    }
    /**
     * 设置 pageModel
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }
    /**
     * 获取 elementId
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }
    /**
     * 设置 elementId
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public BigDecimal getDisPercent() {
        return disPercent;
    }

    public void setDisPercent(BigDecimal disPercent) {
        this.disPercent = disPercent;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getChildPortfolioId() {
        return childPortfolioId;
    }

    public void setChildPortfolioId(String childPortfolioId) {
        this.childPortfolioId = childPortfolioId;
    }

    public String getChildPortfolioName() {
        return childPortfolioName;
    }

    public void setChildPortfolioName(String childPortfolioName) {
        this.childPortfolioName = childPortfolioName;
    }

    public List<UploadFile> getFileArray() {
        return fileArray;
    }

    public void setFileArray(List<UploadFile> fileArray) {
        this.fileArray = fileArray;
    }

    public String getPortfolioSeries() {
        return portfolioSeries;
    }

    public void setPortfolioSeries(String portfolioSeries) {
        this.portfolioSeries = portfolioSeries;
    }

}

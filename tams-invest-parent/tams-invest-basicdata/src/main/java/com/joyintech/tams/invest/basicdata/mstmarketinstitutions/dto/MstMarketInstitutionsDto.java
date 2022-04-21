/*
 * 文件名：MstMarketInstitutionsDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年8月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.basicdata.mstmarketinstitutions.dto;

import java.math.BigDecimal;
import java.util.List;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.tams.common.base.entity.MstCustomerInfoAddWithBLOBs;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.invest.basicdata.mstmarketinstitutions.validator.MstMarketInstitutionsValidator;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.dto.ProcessEntity;
/**
 * 
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年8月20日
 * @see MstMarketInstitutionsDto
 * @since 1.0
 */
@PageId("MstMarketInstitutions002")
@ValidLogic(targetClass = MstMarketInstitutionsValidator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class MstMarketInstitutionsDto extends MstCustomerInfoAddWithBLOBs implements ITableRow, ProcessEntity{
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 操作类型
     */
    private String operateType;
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String elementId;
    
    /**
     * 选中的数据
     */
    private transient List<MstMarketInstitutionsDto> selectedItem;
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String pageModel;
    
    /**
     * 客户名称
     */
    @Length(fieldName = "MstMarketInstitutions002.customerName", max = 60, groups = {ValidateGroupEdit.class})
    @NotEmpty(fieldName = "MstMarketInstitutions002.customerName", groups = {ValidateGroupEdit.class})
    private String customerName;
    
    /**
     * 客户评级
     */
    private List<MstBondCreditRating> mstBondCreditRating;

    /*添加'是否持有金融牌照'数据字典值 add by lishuping 20190823 start*/
    /**
     * 是否持有金融牌照
     */
    private String isFinLicence;

    /**
     * isFinLicence
     * @return isFinLicence
     */
    public String getIsFinLicence() {
        return isFinLicence;
    }

    /**
     * isFinLicence
     * @param isFinLicence isFinLicence
     */
    public void setIsFinLicence(String isFinLicence) {
        this.isFinLicence = isFinLicence;
    }

    /**
     * 数据字典代码转换成数据字典值
     * @return
     */
    public String getIsFinLicenceStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsFinLicence());
    }
    /*添加'是否持有金融牌照'数据字典值 add by lishuping 20190823 end*/

    /**
     * 
     * 客户代码
     * @return String
     */
    public String getCustomerId(){
        return super.getCustomerId();
    }
    /**
     * @return String
     * 机构类型(技术领域)
     */
    @NotEmpty(fieldName = "MstMarketInstitutions002.partyType3", groups = {ValidateGroupEdit.class})
    public String getPartyType3Str(){
        return SysDataUtil.getNameFromDataDict(DictCode.发行机构类型_按技术领域划分, super.getPartyType3());
    }
    
    /**
     * @return String
     * 机构类型(经济类型)
     */
    @NotEmpty(fieldName = "MstMarketInstitutions002.partyType2", groups = {ValidateGroupEdit.class})
    public String getPartyType2Str(){
        return SysDataUtil.getNameFromDataDict(DictCode.企业性质, super.getPartyType2());
    }
    
    /**
     * @return String
     * 机构类型(规模)
     */
    @NotEmpty(fieldName = "MstMarketInstitutions002.partyType1", groups = {ValidateGroupEdit.class})
    public String getPartyType1Str(){
        // 20200819 机构类型（规模）参数取值 市场机构机构类型（规模） wangwei start
//        return SysDataUtil.getNameFromDataDict(DictCode.发行机构类型_按规模划分, super.getPartyType1());
        return SysDataUtil.getNameFromDataDict(DictCode.市场机构机构类型, super.getPartyType1());
        // 20200819 机构类型（规模）参数取值 市场机构机构类型（规模） wangwei end
    }
    
    /**
     * @return String
     * 组织机构代码/统一社会信用代码
     */
    @NotEmpty(fieldName = "MstMarketInstitutions002.insitituteCode", groups = {ValidateGroupEdit.class})
    public String getInsitituteCode(){
        return super.getInsitituteCode();
    }
    
    /**
     * @return String
     * 是否政府融资平台
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.isGov", groups = {ValidateGroupEdit.class})
    public String getIsGovStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, super.getIsGov());
    }
    
    /**
     * @return BigDecimal
     * 核心资本(万元)
     */
    //@Length(fieldName = "MstMarketInstitutions002.mainCapital", max = 30, groups = {ValidateGroupEdit.class})
    //@NotEmpty(fieldName = "MstMarketInstitutions002.mainCapital", groups = {ValidateGroupEdit.class})
    public BigDecimal getMainCapital(){
        return super.getMainCapital();
    }
    
    /**
     * @return BigDecimal
     * 核心资本(万元)
     */
    //@Length(fieldName = "MstMarketInstitutions002.registerCapital", max = 30, groups = {ValidateGroupEdit.class})
    //@NotEmpty(fieldName = "MstMarketInstitutions002.registerCapital", groups = {ValidateGroupEdit.class})
    public BigDecimal getRegisterCapital(){
        return super.getRegisterCapital();
    }
    
    /**
     * @return String
     * 所属省
     */
    @NotEmpty(fieldName = "MstMarketInstitutions002.province", groups = {ValidateGroupEdit.class})
    public String getProvince(){
        return super.getProvince();
    }
    
    /**
     * @return String
     * 所属市
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.city", groups = {ValidateGroupEdit.class})
    public String getCity(){
        return super.getCity();
    }
    
    /**
     * @return String
     * 所属县
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.city", groups = {ValidateGroupEdit.class})
    public String getCounty(){
        return super.getCounty();
    }
    
    /**
     * @return String
     * 控股类型
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.holdingType", groups = {ValidateGroupEdit.class})
    public String getHoldingType(){
        return super.getHoldingType();
    }
    
    /**
     * @return String
     * 客户主体类型
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.customerObjType", groups = {ValidateGroupEdit.class})
    public String getCustomerObjType(){
        return super.getCustomerObjType();
    }
    
    /**
     * @return String
     * 是否上市公司
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.isListedCompany", groups = {ValidateGroupEdit.class})
    public String getIsListedCompanyStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, super.getIsListedCompany());
    }
    
    /**
     * @return String
     * 是否集团客户
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.isGroupCustomer", groups = {ValidateGroupEdit.class})
    public String getIsGroupCustomer(){
        return super.getIsGroupCustomer();
    }
    
    /**
     * @return String
     * 所属集团客户编号
     */
    //@Length(fieldName = "MstMarketInstitutions002.parentCustomerId", max = 32, groups = {ValidateGroupEdit.class})
    //@NotEmpty(fieldName = "MstMarketInstitutions002.parentCustomerId", groups = {ValidateGroupEdit.class})
    public String getParentCustomerId(){
        return super.getParentCustomerId();
    }
    
    /**
     * @return String
     * 所属集团客户名称
     */
    //@Length(fieldName = "MstMarketInstitutions002.parentCustomerName", max = 60, groups = {ValidateGroupEdit.class})
    //@NotEmpty(fieldName = "MstMarketInstitutions002.parentCustomerName", groups = {ValidateGroupEdit.class})
    public String getParentCustomerName(){
        return super.getParentCustomerName();
    }
    
    /**
     * @return String
     * 是否民营企业
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.isPrivate", groups = {ValidateGroupEdit.class})
    public String getIsPrivate(){
        return super.getIsPrivate();
    }
    
    /**
     * @return String
     * 是否为逃废债企业
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.isEscapeDept", groups = {ValidateGroupEdit.class})
    public String getIsEscapeDept(){
        return super.getIsEscapeDept();
    }
    
    /**
     * @return String
     * 授信策略
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.creditType", groups = {ValidateGroupEdit.class})
    public String getCreditType(){
        return super.getCreditType();
    }
    
    /**
     * @return String
     * 是否授信类关联交易
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.isCreditRelatedTrd", groups = {ValidateGroupEdit.class})
    public String getIsCreditRelatedTrd(){
        return super.getIsCreditRelatedTrd();
    }
    
    /**
     * @return String
     * 产业结构调整类型
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.structAdjustType", groups = {ValidateGroupEdit.class})
    public String getStructAdjustType(){
        return super.getStructAdjustType();
    }
    
    /**
     * @return String
     * 工业转型升级标识
     */
    //@NotEmpty(fieldName = "MstMarketInstitutions002.indusUpgrateType", groups = {ValidateGroupEdit.class})
    public String getIndusUpgrateType(){
        return super.getIndusUpgrateType();
    }
    
//    /**
//     * @return String
//     * 经营范围
//     */
//    //@NotEmpty(fieldName = "MstMarketInstitutions002.busiScope", groups = {ValidateGroupEdit.class})
//    public String getBusiScope(){
//        return super.getBusiScope();
//    }
//
//    /**
//     * @return String
//     * 主营业务
//     */
//    //@NotEmpty(fieldName = "MstMarketInstitutions002.mainBusi", groups = {ValidateGroupEdit.class})
//    public String getMainBusi(){
//        return super.getMainBusi();
//    }
    /**
     * @return String
     * 外部代码2，对于平安存BECIF号
     */
    //@Length(fieldName = "MstMarketInstitutions002.BECIF", max = 32, groups = {ValidateGroupEdit.class})
    //@NotEmpty(fieldName = "MstMarketInstitutions002.BECIF", groups = {ValidateGroupEdit.class})
    public String getLinkId2(){
        return super.getLinkId2();
    }
    
    /**
     * 取得 国标二级分类
     * @return 国标二级分类
     */
    @NotEmpty(fieldName = "MstMarketInstitutions002.industrySecondClass", groups = {ValidateGroupEdit.class})
    public String getIndustry2Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.机构所属行业二级分类 , super.getIndustry2());
    }
    
    /**
     * 取得 国标一级分类
     * @return 国标一级分类
     */
    @NotEmpty(fieldName = "MstMarketInstitutions002.industry", groups = {ValidateGroupEdit.class})
    public String getIndustryStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.机构所属行业一级分类 , super.getIndustry());
    }
    public List<MstBondCreditRating> getMstBondCreditRating() {
        return mstBondCreditRating;
    }


    
    public void setMstBondCreditRating(List<MstBondCreditRating> mstBondCreditRating) {
        this.mstBondCreditRating = mstBondCreditRating;
    }


    public String getOperateType() {
        return operateType;
    }

    
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    
    public String getElementId() {
        return elementId;
    }

    
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    
    public List<MstMarketInstitutionsDto> getSelectedItem() {
        return selectedItem;
    }

    
    public void setSelectedItem(List<MstMarketInstitutionsDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    
    public String getPageModel() {
        return pageModel;
    }

    
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }
    /*市场机构名称流程中check modified by lishuping 20191023 start*/
    @PrimaryKey(value=Field.PK_1)
    public String getCustomerName() {
        return customerName;
    }
    /*市场机构名称流程中check modified by lishuping 20191023 end*/
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 来源：0-成功不需要处理,1-接口返回需要处理
     */
    private int dealFlag;

    /**
     * 是否发行人
     */
    private String isIssuer;

    public int getDealFlag() {
        return dealFlag;
    }

    public void setDealFlag(int dealFlag) {
        this.dealFlag = dealFlag;
    }

    public String getIsIssuer() {
        return isIssuer;
    }

    public void setIsIssuer(String isIssuer) {
        this.isIssuer = isIssuer;
    }
}

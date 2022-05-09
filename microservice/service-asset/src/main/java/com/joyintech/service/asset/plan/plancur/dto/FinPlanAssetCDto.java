/*
 * 文件名：FinPlanAssetCDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.asset.plan.plancur.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.RepAssetOther;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.service.product.common.dto.BussFinProductOpenPlanDto;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author qiangchengxi
 * @version 1.0 2019年1月7日
 * @see FinPlanAssetCDto
 * @since 1.0
 */
public class FinPlanAssetCDto extends FinProduct {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    /*   增加新字段 add by zhuyongkang 2019/09/27 start*/
    /**
     *中债资产其他要素entity
     */
    private RepAssetOther repAssetOther;

    public RepAssetOther getRepAssetOther() {
        return repAssetOther;
    }

    public void setRepAssetOther(RepAssetOther repAssetOther) {
        this.repAssetOther = repAssetOther;
    }
    /**
     * 法审编号
     */
    private String trialNo;
    /**
     * 合同号
     */
    private String contractNo;
    /**
     * 申购确认速度，如果资产只有一个确认速度，则存该字段
     */
    private String purSpeed;

    /**
     * 赎回确认速度
     */
    private String redSpeed;

    /**
     * 增信措施
     */
    private String creditEnhanceMeasure;

    /**
     * 基金经理姓名
     */
    private String fundMangerName;

    public String getTrialNo() {
        return trialNo;
    }

    public void setTrialNo(String trialNo) {
        this.trialNo = trialNo;
    }

    @Override
    public String getContractNo() {
        return contractNo;
    }

    @Override
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getPurSpeed() {
        return purSpeed;
    }

    public void setPurSpeed(String purSpeed) {
        this.purSpeed = purSpeed;
    }

    public String getRedSpeed() {
        return redSpeed;
    }

    public void setRedSpeed(String redSpeed) {
        this.redSpeed = redSpeed;
    }
    /* 增加新字段 add by zhuyongkang 2019/09/27 end*/
    /**
    *
    */
    private String operateType;

    /**
     * 界面类型
     */
    private String pageModel;

    /**
     * 选中的数据列表
     */
    private transient List<FinPlanAssetCDto> selectedItem;

    /**
     * 画面元素
     */
    private String elementId;

    /**
     * 起息日开始日期
     */
    private String startVDate;

    /**
     * 起息日结束日期
     */
    private String endVDate;

    /**
     * 到息日开始日期
     */
    private String startMDate;

    /**
     * 到息日结束日期
     */
    private String endMDate;

    /**
     * 一级分类
     */
    private String category;

    /**
     * 二级分类
     */
    private String type;

    /**
     * 添加字段资产二-五级代码及其get、set方法
     * lishuping
     * 2019/7/18
     * start
     */
    /**
     * 资产二级代码
     */
    private String type9;

    /**
     * 资产三级代码
     */
    private String type10;

    /**
     * 资产四级代码
     */
    private String type11;

    /**
     * 资产五级代码
     */
    private String type12;
    /**
     * 添加字段资产二-五级代码及其get、set方法
     * lishuping
     * 2019/7/18
     * end
     */

    /**
     * 交易方式
     *//*
    private String transMode;*/

    /**
     * 是否按日复利
     */
    private String isCompoundInt;

    /**
     * 人行信托代码
     */
    private String bankTrustCode;
    /**
     * 投资性质_固定收益类
     */
    private String investmentAttributes;
    /**
     * 资产提供行，多选，逗号分隔
     */
    private String assetProvider;
    /**
     * 分支序号
     */
    private Long branch;
    /**
     * 授信额度代码
     */
    private String seqno;

    /**
     * 授信配置代码
     */
    private String creditMstId;
    
    //add by dongzhiling start at 2019-02-22
    /**
     * 组合entity
     */
    private PtlPortfolio ptlPortfolio;
    //add end

    /**
     * 授信客户编号（机构授信）/金融产品代码（资产授信）/资产系列代码（资产系列授信）
     */
    private String customerId;

    /**
     * 批复流水号
     */
    private String pflsh;


    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 已占用额度，初始值为0
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal hapCreditAmt;

    /**
     * 实际授信主体
     */
    private String actualCreditProvid;
    /**
     * 是否占用主体授信
     */
    private String creditFlag;
    /**
     * 资产管理计划报送信息输入组件
     */
    @Valid
    private BussRepAssetMngProdInfo bussRepAssetMngProdInfo;
    /**
     * bussRepAssetMngProdInfoDto
     */
    private BussRepAssetMngProdInfoDto bussRepAssetMngProdInfoDto;
    /**
     * 参与方信息
     */
    @Valid
    private BussParticipantInfoDto bussParticipantInfoDto;
    /**
     * 分红计划组件
     */
    @Valid
    private BussFinIntPayDto bussFinIntPayDto;
    /**
     * 附件上传
     */
    private List<SysUploadFileInfo> fileArray;

    // 增加推荐方与推荐费率 add by renping 20191014 start
    /**
     * 推荐方
     */
    private String recommender;

    /**
     * 取得 推荐方
     * @return 推荐方
     */
    public String getRecommender() {
        return recommender;
    }

    /**
     * 设置 推荐方
     * @param recommender 推荐方
     */
    public void setRecommender(String recommender) {
        this.recommender = recommender == null ? null : recommender.trim();
    }
    /**
     * 资产推荐方名称
     */
    private String recommenderName;

    /**
     * 取得 资产推荐方名称
     * @return 资产推荐方名称
     */
    public String getRecommenderName() {
        return recommenderName;
    }

    /**
     * 设置 资产推荐方名称
     * @param recommenderName 资产推荐方名称
     */
    public void setRecommenderName(String recommenderName) {
        this.recommenderName = recommender == null ? null : recommenderName.trim();
    }
    // 增加推荐方与推荐费率 add by renping 20191014 end

    /**
     * 是否每日开放申赎
     */
    private String isOpenEveryDay;

    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductOpenPlanDto bussFinProductOpenPlanDto;
    /**
     * add by zhangweiqi
     */
    @Valid
    private BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto;

    /**
     * 获取 operateType
     * 
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置 operateType
     * 
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 获取 type9
     *
     * @return type9
     */
    public String getType9() { return type9; }

    /**
     * 设置 type9
     *
     * @param type9 type9
     */
    public void setType9(String type9) {this.type9 = type9; }

    /**
     * 获取 type10
     *
     * @return otype10
     */
    public String getType10() { return type10; }

    /**
     * 设置 type10
     *
     * @param type10 type10
     */
    public void setType10(String type10) { this.type10 = type10; }

    /**
     * 获取 type11
     *
     * @return type11
     */
    public String getType11() { return type11; }

    /**
     * 设置 type11
     *
     * @param type11 type11
     */
    public void setType11(String type11) { this.type11 = type11; }

    /**
     * 获取 type12
     *
     * @return type12
     */
    public String getType12() { return type12; }

    /**
     * 设置 type12
     *
     * @param type12 type12
     */
    public void setType12(String type12) { this.type12 = type12; }

    /**
     * 获取 pageModel
     * 
     * @return pageModel.
     */
    public String getPageModel() {
        return pageModel;
    }

    /**
     * 设置 pageModel
     * 
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    /**
     * 获取 selectedItem
     * 
     * @return selectedItem.
     */
    public List<FinPlanAssetCDto> getSelectedItem() {
        return selectedItem;
    }

    /**
     * 设置 selectedItem
     * 
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<FinPlanAssetCDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * 获取 elementId
     * 
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * 设置 elementId
     * 
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 获取 startVDate
     * 
     * @return startVDate.
     */
    public String getStartVDate() {
        return startVDate;
    }

    /**
     * 设置 startVDate
     * 
     * @param startVDate startVDate
     */
    public void setStartVDate(String startVDate) {
        this.startVDate = startVDate;
    }

    /**
     * 获取 endVDate
     * 
     * @return endVDate.
     */
    public String getEndVDate() {
        return endVDate;
    }

    /**
     * 设置 endVDate
     * 
     * @param endVDate endVDate
     */
    public void setEndVDate(String endVDate) {
        this.endVDate = endVDate;
    }

    /**
     * 获取 startMDate
     * 
     * @return startMDate.
     */
    public String getStartMDate() {
        return startMDate;
    }

    /**
     * 设置 startMDate
     * 
     * @param startMDate startMDate
     */
    public void setStartMDate(String startMDate) {
        this.startMDate = startMDate;
    }

    /**
     * 获取 endMDate
     * 
     * @return endMDate.
     */
    public String getEndMDate() {
        return endMDate;
    }

    /**
     * 设置 endMDate
     * 
     * @param endMDate endMDate
     */
    public void setEndMDate(String endMDate) {
        this.endMDate = endMDate;
    }

    /**
     * 获取 category
     * 
     * @return category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置 category
     * 
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取 type
     * 
     * @return type.
     */
    public String getType() {
        return type;
    }

    /**
     * 设置 type
     * 
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取 transMode
     * 
     * @return transMode.
     *//*
    public String getTransMode() {
        return transMode;
    }

    *//**
     * 设置 transMode
     * 
     * @param transMode transMode
     *//*
    public void setTransMode(String transMode) {
        this.transMode = transMode;
    }*/

    /**
     * 获取 isCompoundInt
     * 
     * @return isCompoundInt.
     */
    public String getIsCompoundInt() {
        return isCompoundInt;
    }

    /**
     * 设置 isCompoundInt
     * 
     * @param isCompoundInt isCompoundInt
     */
    public void setIsCompoundInt(String isCompoundInt) {
        this.isCompoundInt = isCompoundInt;
    }

    /**
     * 获取 bankTrustCode
     * 
     * @return bankTrustCode.
     */
    public String getBankTrustCode() {
        return bankTrustCode;
    }

    /**
     * 设置 bankTrustCode
     * 
     * @param bankTrustCode bankTrustCode
     */
    public void setBankTrustCode(String bankTrustCode) {
        this.bankTrustCode = bankTrustCode;
    }

    
    /**
     * 获取 investmentAttributes
     * @return investmentAttributes.
     */
    public String getInvestmentAttributes() {
        return investmentAttributes;
    }

    
    /**
     * 设置 investmentAttributes
     * @param investmentAttributes investmentAttributes
     */
    public void setInvestmentAttributes(String investmentAttributes) {
        this.investmentAttributes = investmentAttributes;
    }

    

    
    
    /**
     * 获取 assetProvider
     * @return assetProvider.
     */
    public String getAssetProvider() {
        return assetProvider;
    }

    
    
    /**
     * 获取 branch
     * @return branch.
     */
    public Long getBranch() {
        return branch;
    }

    
    /**
     * 设置 branch
     * @param branch branch
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }
    
    
    /**
     * 获取 seqno
     * @return seqno.
     */
    public String getSeqno() {
        return seqno;
    }

    
    /**
     * 设置 seqno
     * @param seqno seqno
     */
    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    
    /**
     * 获取 creditMstId
     * @return creditMstId.
     */
    public String getCreditMstId() {
        return creditMstId;
    }

    
    /**
     * 设置 creditMstId
     * @param creditMstId creditMstId
     */
    public void setCreditMstId(String creditMstId) {
        this.creditMstId = creditMstId;
    }

    
    /**
     * 获取 customerId
     * @return customerId.
     */
    public String getCustomerId() {
        return customerId;
    }

    
    /**
     * 设置 customerId
     * @param customerId customerId
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    
    /**
     * 获取 pflsh
     * @return pflsh.
     */
    public String getPflsh() {
        return pflsh;
    }

    
    /**
     * 设置 pflsh
     * @param pflsh pflsh
     */
    public void setPflsh(String pflsh) {
        this.pflsh = pflsh;
    }
    
    
    
    /**
     * 获取 hapCreditAmt
     * @return hapCreditAmt.
     */
    public BigDecimal getHapCreditAmt() {
        return hapCreditAmt;
    }

    
    /**
     * 设置 hapCreditAmt
     * @param hapCreditAmt hapCreditAmt
     */
    public void setHapCreditAmt(BigDecimal hapCreditAmt) {
        this.hapCreditAmt = hapCreditAmt;
    }

    /**
     * 获取 actualCreditProvid
     * @return actualCreditProvid.
     */
    public String getActualCreditProvid() {
        return actualCreditProvid;
    }

    
    /**
     * 设置 actualCreditProvid
     * @param actualCreditProvid actualCreditProvid
     */
    public void setActualCreditProvid(String actualCreditProvid) {
        this.actualCreditProvid = actualCreditProvid;
    }
    
    
    /**
     * 获取 creditFlag
     * @return creditFlag.
     */
    public String getCreditFlag() {
        return creditFlag;
    }

    
    /**
     * 设置 creditFlag
     * @param creditFlag creditFlag
     */
    public void setCreditFlag(String creditFlag) {
        this.creditFlag = creditFlag;
    }

    /**
     * 设置 assetProvider
     * @param assetProvider assetProvider
     */
    public void setAssetProvider(String assetProvider) {
        this.assetProvider = assetProvider;
    }
    
    
    /**
     * 获取 bussRepAssetMngProdInfo
     * @return bussRepAssetMngProdInfo.
     */
    public BussRepAssetMngProdInfo getBussRepAssetMngProdInfo() {
        return bussRepAssetMngProdInfo;
    }

    
    /**
     * 设置 bussRepAssetMngProdInfo
     * @param bussRepAssetMngProdInfo bussRepAssetMngProdInfo
     */
    public void setBussRepAssetMngProdInfo(BussRepAssetMngProdInfo bussRepAssetMngProdInfo) {
        this.bussRepAssetMngProdInfo = bussRepAssetMngProdInfo;
    }

    
    /**
     * 获取 bussRepAssetMngProdInfoDto
     * @return bussRepAssetMngProdInfoDto.
     */
    public BussRepAssetMngProdInfoDto getBussRepAssetMngProdInfoDto() {
        return bussRepAssetMngProdInfoDto;
    }

    
    /**
     * 设置 bussRepAssetMngProdInfoDto
     * @param bussRepAssetMngProdInfoDto bussRepAssetMngProdInfoDto
     */
    public void setBussRepAssetMngProdInfoDto(BussRepAssetMngProdInfoDto bussRepAssetMngProdInfoDto) {
        this.bussRepAssetMngProdInfoDto = bussRepAssetMngProdInfoDto;
    }

    /**
     * 获取 bussParticipantInfoDto
     * @return bussParticipantInfoDto.
     */
    public BussParticipantInfoDto getBussParticipantInfoDto() {
        return bussParticipantInfoDto;
    }

    
    /**
     * 设置 bussParticipantInfoDto
     * @param bussParticipantInfoDto bussParticipantInfoDto
     */
    public void setBussParticipantInfoDto(BussParticipantInfoDto bussParticipantInfoDto) {
        this.bussParticipantInfoDto = bussParticipantInfoDto;
    }

    
    /**
     * 获取 bussFinIntPayDto
     * @return bussFinIntPayDto.
     */
    public BussFinIntPayDto getBussFinIntPayDto() {
        return bussFinIntPayDto;
    }

    
    /**
     * 设置 bussFinIntPayDto
     * @param bussFinIntPayDto bussFinIntPayDto
     */
    public void setBussFinIntPayDto(BussFinIntPayDto bussFinIntPayDto) {
        this.bussFinIntPayDto = bussFinIntPayDto;
    }

    
    /**
     * 获取 fileArray
     * @return fileArray.
     */
    public List<SysUploadFileInfo> getFileArray() {
        return fileArray;
    }

    
    /**
     * 设置 fileArray
     * @param fileArray fileArray
     */
    public void setFileArray(List<SysUploadFileInfo> fileArray) {
        this.fileArray = fileArray;
    }

    
    /**
     * 获取 ptlPortfolio
     * @return ptlPortfolio.
     */
    public PtlPortfolio getPtlPortfolio() {
        return ptlPortfolio;
    }

    
    /**
     * 设置 ptlPortfolio
     * @param ptlPortfolio ptlPortfolio
     */
    public void setPtlPortfolio(PtlPortfolio ptlPortfolio) {
        this.ptlPortfolio = ptlPortfolio;
    }

    public String getCreditEnhanceMeasure() {
        return creditEnhanceMeasure;
    }

    public void setCreditEnhanceMeasure(String creditEnhanceMeasure) {
        this.creditEnhanceMeasure = creditEnhanceMeasure;
    }

    public String getFundMangerName() {
        return fundMangerName;
    }

    public void setFundMangerName(String fundMangerName) {
        this.fundMangerName = fundMangerName;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getIsOpenEveryDay() {
        return isOpenEveryDay;
    }

    public void setIsOpenEveryDay(String isOpenEveryDay) {
        this.isOpenEveryDay = isOpenEveryDay;
    }

    /**
     * 获取 bussFinProductOpenPlanDto
     * @return bussFinProductOpenPlanDto.
     */
    public BussFinProductOpenPlanDto getBussFinProductOpenPlanDto() {
        return bussFinProductOpenPlanDto;
    }

    /**
     * 设置 bussFinProductOpenPlanDto
     * @param bussFinProductOpenPlanDto bussFinProductOpenPlanDto
     */
    public void setBussFinProductOpenPlanDto(BussFinProductOpenPlanDto bussFinProductOpenPlanDto) {
        this.bussFinProductOpenPlanDto = bussFinProductOpenPlanDto;
    }

    public BussFinProductOpenPlanDto getBussFinProductOpenPlanRedDto() {
        return bussFinProductOpenPlanRedDto;
    }

    public void setBussFinProductOpenPlanRedDto(BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto) {
        this.bussFinProductOpenPlanRedDto = bussFinProductOpenPlanRedDto;
    }
}

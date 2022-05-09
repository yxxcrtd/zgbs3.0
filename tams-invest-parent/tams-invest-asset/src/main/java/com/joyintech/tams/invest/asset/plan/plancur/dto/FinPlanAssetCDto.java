/*
 * 文件名：FinPlanAssetCDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.plan.plancur.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.*;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.component.business.asset.dto.BussFinIntPayDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductOpenPlanDto;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.component.business.report.asset.plan.dto.BussRepAssetMngProdInfo;
import com.joyintech.tams.component.business.report.asset.plan.dto.BussRepAssetMngProdInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.asset.plan.plancur.valid.FinPlanAssetCValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2019年1月7日
 * @see FinPlanAssetCDto
 * @since 1.0
 */
@PageId("FinPlanAssetC002")
@ValidLogic(targetClass = FinPlanAssetCValidator.class,groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class FinPlanAssetCDto extends FinProduct implements ProcessEntity{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
 /*   增加新字段 add by zhuyongkang 2019/09/27 start*/
    /**
     * 金融产品附属表entity
     */
    private FinProductAdd finProductAdd;
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
    /**
     * 获取 trialNo
     * @return trialNo.
     */
    public String getTrialNo() {
        return trialNo;
    }
    /**
     * 设置 trialNo
     * @param trialNo trialNo
     */
    public void setTrialNo(String trialNo) {
        this.trialNo = trialNo;
    }
    /**
     * 获取 finProductAdd
     * @return finProductAdd.
     */
    public FinProductAdd getFinProductAdd() {
        return finProductAdd;
    }
    /**
     * 设置 finProductAdd
     * @param finProductAdd finProductAdd
     */
    public void setFinProductAdd(FinProductAdd finProductAdd) {
        this.finProductAdd = finProductAdd;
    }
    /**
     * 获取 contractNo
     * @return contractNo.
     */
    @Override
    @NotEmpty(fieldName = "FinPlanAssetC002.contractNo",groups={ValidateGroupEdit.class})
    public String getContractNo() {
        return contractNo;
    }
    /**
     * 设置 contractNo
     * @param contractNo contractNo
     */
    @Override
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }
    /**
     * 获取 purSpeed
     * @return purSpeed.
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.purSpeed",groups={ValidateGroupEdit.class})
    public String getPurSpeed() {
        return purSpeed;
    }
    /**
     * 设置 purSpeed
     * @param purSpeed purSpeed
     */
    public void setPurSpeed(String purSpeed) {
        this.purSpeed = purSpeed;
    }
    /**
     * 获取 redSpeed
     * @return redSpeed.
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.redSpeed",groups={ValidateGroupEdit.class})
    public String getRedSpeed() {
        return redSpeed;
    }
    /**
     * 设置 redSpeed
     * @param redSpeed redSpeed
     */
    public void setRedSpeed(String redSpeed) {
        this.redSpeed = redSpeed;
    }
    /* 增加新字段 add by zhuyongkang 2019/09/27 end*/
    /**
     * 操作类型
     */
    private String operateType;    
    /**
     * 界面类型
     */
    private String pageModel; 
    /**
     * 画面元素
     */
    private String elementId;
    /**
     * 选中的数据列表
     */
    private transient List<FinPlanAssetCDto> selectedItem;
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
     * 一级分类
     */
    private String category;
    /**
     * 二级分类
     */
    private String type;
    //add by dongzhiling start at 2019-02-22
    /**
     * 组合entity
     */
    private PtlPortfolio ptlPortfolio;
    //add end
    /**
     * 交易方式
     *//*
    private String transMode;*/
    /**
     * 出账编号
     *//*
    private String cmsId;
    *//**
     * BECIF号
     *//*
    private String cmsBecif;
    *//**
     * 关联合同编号
     *//*
    private String cmsContractId;*/
    /**
     * 资产提供行，多选，逗号分隔
     */
    private String assetProvider;
    /**
     * 发行人名称
     */
    private String issuerName;
    /**
     * 托管人名称
     */
    private String trusteeIdName;
    /**
     * 附件临时Id
     */
    private String fjId;
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

    /**
     * 授信客户编号（机构授信）/金融产品代码（资产授信）/资产系列代码（资产系列授信）
     */
    private String customerId;

    /**
     * 批复流水号
     */
    private String pflsh;
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
     * @param recommender 资产推荐方名称
     */
    public void setRecommenderName(String recommenderName) {
        this.recommenderName = recommender == null ? null : recommenderName.trim();
    }
    // 增加推荐方与推荐费率 add by renping 20191014 end
    /**
     * 获取 operateType
     * @return operateType.
     */
    /**
     * 取得 市场代码，可以按规则生成，或者手输，不校验唯一。对于理财产品分层，同母金融产品代码对应的市场代码。
     * @return 市场代码，可以按规则生成，或者手输，不校验唯一。对于理财产品分层，同母金融产品代码对应的市场代码。
     */
    @Override
    //YUCHUBS-564 资产代码的格式去掉校验 guyunfei 2019/09/04 start
    //@Pattern(fieldName = "FinPlanAssetC002.finprodMarketId",regx = "^(?=.*[a-zA-Z]+)(?=.*[0-9]+)[a-zA-Z0-9]+$",message = "FW_E_0002",groups={ValidateGroupEdit.class})
    //YUCHUBS-564 资产代码的格式去掉校验 guyunfei 2019/09/04 end
    @Code(fieldName = "FinPlanAssetC002.finprodMarketId", length = 32, groups = {ValidateGroupEdit.class})
//    @NotEmpty(fieldName = "FinPlanAssetC002.finprodMarketId", groups = {ValidateGroupEdit.class})
    @PrimaryKey(Field.PK_1)
    public String getFinprodMarketId() {
        return super.getFinprodMarketId();
    }
    /**
     * 取得 起息日
     * @return 起息日
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.vDate",groups={ValidateGroupEdit.class})
    public Date getVdate() {
        return super.getVdate();
    }

    /**
     * 取得 交易日历
     * @return 交易日历
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.calendarId",groups={ValidateGroupEdit.class})
    public String getCalendarId() {
        return super.getCalendarId();
    }

    /**
     * 取得 币种（本金币种、募集币种）
     * @return 币种（本金币种、募集币种）
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.ccy",groups={ValidateGroupEdit.class})
    public String getCcy() {
        return super.getCcy();
    }

    /**
     * 取得 金融产品全称
     * @return 金融产品全称
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.finprodName",groups={ValidateGroupEdit.class})
    public String getFinprodName() {
        return super.getFinprodName();
    }

    /**
     * 取得 托管人
     * @return 托管人
     */
//    @NotEmpty(fieldName = "FinPlanAssetC002.trusteeId",groups={ValidateGroupEdit.class})
    public String getTrusteeId() {
        return super.getTrusteeId();
    }

    
    /**
     * 取得 管理人
     * @return 管理人
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.manager",groups={ValidateGroupEdit.class})
    public String getManager() {
        return super.getManager();
    }
    /**
     * 取得 发行规模
     * @return 发行规模
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.issueAmt",groups={ValidateGroupEdit.class})
    @Amount(fieldName = "FinPlanAssetC002.issueAmt", maxIntBit = 12, maxDecBit = 2,groups={ValidateGroupEdit.class})
    public BigDecimal getIssueAmt() {
        return super.getIssueAmt();
    }
/*    @NotEmpty(fieldName = "FinPlanAssetC002.capiIncomeFeature",groups={ValidateGroupEdit.class})*/
    public String getCapiIncomeFeature(){
        return super.getCapiIncomeFeature();
    }
    /**
     * 取得 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    @Override
    @PrimaryKey(Field.PK_2)
    public String getFinprodType2() {
        return super.getFinprodType2();
    }
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
    
    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<FinPlanAssetCDto> getSelectedItem() {
        return selectedItem;
    }
    
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<FinPlanAssetCDto> selectedItem) {
        this.selectedItem = selectedItem;
    }
    
    /**
     * 获取 startVDate
     * @return startVDate.
     */
    public String getStartVDate() {
        return startVDate;
    }
    
    /**
     * 设置 startVDate
     * @param startVDate startVDate
     */
    public void setStartVDate(String startVDate) {
        this.startVDate = startVDate;
    }
    
    /**
     * 获取 endVDate
     * @return endVDate.
     */
    public String getEndVDate() {
        return endVDate;
    }
    
    /**
     * 设置 endVDate
     * @param endVDate endVDate
     */
    public void setEndVDate(String endVDate) {
        this.endVDate = endVDate;
    }
    
    /**
     * 获取 startMDate
     * @return startMDate.
     */
    public String getStartMDate() {
        return startMDate;
    }
    
    /**
     * 设置 startMDate
     * @param startMDate startMDate
     */
    public void setStartMDate(String startMDate) {
        this.startMDate = startMDate;
    }
    
    /**
     * 获取 endMDate
     * @return endMDate.
     */
    public String getEndMDate() {
        return endMDate;
    }
    
    /**
     * 设置 endMDate
     * @param endMDate endMDate
     */
    public void setEndMDate(String endMDate) {
        this.endMDate = endMDate;
    }
    

    /**
     * 获取 type9
     *
     * @return type9
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.type9",groups={ValidateGroupEdit.class})
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
    @NotEmpty(fieldName = "FinPlanAssetC002.type10",groups={ValidateGroupEdit.class})
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
//    @NotEmpty(fieldName = "FinPlanAssetC002.type11",groups={ValidateGroupEdit.class})
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
//    @NotEmpty(fieldName = "FinPlanAssetC002.type12",groups={ValidateGroupEdit.class})
    public String getType12() { return type12; }

    /**
     * 设置 type12
     *
     * @param type12 type12
     */
    public void setType12(String type12) { this.type12 = type12; }

    /**
     * 获取 category
     * @return category.
     */
//    @NotEmpty(fieldName = "FinPlanAssetC002.category",groups={ValidateGroupEdit.class})
    public String getCategory() {
        return category;
    }

    /**
     * 设置 category
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * 获取 type
     * @return type.
     */
//    @NotEmpty(fieldName = "FinPlanAssetC002.type",groups={ValidateGroupEdit.class})
    public String getType() {
        return type;
    }
    
    /**
     * 设置 type
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * 获取 transMode
     * @return transMode.
     *//*
    @NotEmpty(fieldName = "FinPlanAssetC002.transMode",groups={ValidateGroupEdit.class})
    public String getTransMode() {
        return transMode;
    }
    
    *//**
     * 设置 transMode
     * @param transMode transMode
     *//*
    public void setTransMode(String transMode) {
        this.transMode = transMode;
    }*/
    
    /**
     * 获取 cmsId
     * @return cmsId.
     *//*
    public String getCmsId() {
        return cmsId;
    }
    
    *//**
     * 设置 cmsId
     * @param cmsId cmsId
     *//*
    public void setCmsId(String cmsId) {
        this.cmsId = cmsId;
    }
    
    *//**
     * 获取 cmsBecif
     * @return cmsBecif.
     *//*
    public String getCmsBecif() {
        return cmsBecif;
    }
    
    *//**
     * 设置 cmsBecif
     * @param cmsBecif cmsBecif
     *//*
    public void setCmsBecif(String cmsBecif) {
        this.cmsBecif = cmsBecif;
    }
    
    *//**
     * 获取 cmsContractId
     * @return cmsContractId.
     *//*
    public String getCmsContractId() {
        return cmsContractId;
    }
    
    *//**
     * 设置 cmsContractId
     * @param cmsContractId cmsContractId
     *//*
    public void setCmsContractId(String cmsContractId) {
        this.cmsContractId = cmsContractId;
    }*/
    
    /**
     * 获取 issuerName
     * @return issuerName.
     */
    public String getIssuerName() {
        return issuerName;
    }
    
    /**
     * 设置 issuerName
     * @param issuerName issuerName
     */
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }
    
    /**
     * 获取 trusteeIdName
     * @return trusteeIdName.
     */
    public String getTrusteeIdName() {
        return trusteeIdName;
    }
    
    /**
     * 设置 trusteeIdName
     * @param trusteeIdName trusteeIdName
     */
    public void setTrusteeIdName(String trusteeIdName) {
        this.trusteeIdName = trusteeIdName;
    }
    
    /**
     * 获取 fjId
     * @return fjId.
     */
    public String getFjId() {
        return fjId;
    }
    
    /**
     * 设置 fjId
     * @param fjId fjId
     */
    public void setFjId(String fjId) {
        this.fjId = fjId;
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
     * 将一级分类对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getCategoryAbbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.计划类资产一级分类, this.getCategory());
    }

    /*lishuping 2019/7/23 start*/
    /**
     * 将二级分类对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getTypeAbbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.计划类资产二级分类, this.getType());
    }
    /**
     * 将资产二级分类对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getType9Abbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, this.getType9());
    }
    /**
     * 将资产三级分类对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getType10Abbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产三级分类, this.getType10());
    }
    /**
     * 将资产四级分类对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getType11Abbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, this.getType11());
    }
    /**
     * 将资产五级分类对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getType12Abbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产五级分类, this.getType12());
    }
    /*lishuping 2019/7/23 end*/
    /**
     * 将交易方式对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getTransModeAbbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, this.getTradeMarket());
    }
    
    /**
     * 将币种对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getCcyAbbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
    }
    
    /**
     * 获取 isCompoundInt
     * @return isCompoundInt.
     */
    public String getIsCompoundInt() {
        return isCompoundInt;
    }
    
    /**
     * 设置 isCompoundInt
     * @param isCompoundInt isCompoundInt
     */
    public void setIsCompoundInt(String isCompoundInt) {
        this.isCompoundInt = isCompoundInt;
    }
    
    
    /**
     * 获取 assetProvider
     * @return assetProvider.
     */
    public String getAssetProvider() {
        return assetProvider;
    }
    
    /**
     * 设置 assetProvider
     * @param assetProvider assetProvider
     */
    public void setAssetProvider(String assetProvider) {
        this.assetProvider = assetProvider;
    }
    /**
     * 获取 bankTrustCode
     * @return bankTrustCode.
     */
    @Pattern(fieldName = "FinPlanAssetC002.bankTrustCode",regx = "^[0-9a-zA-Z]+$",message = "FW_E_0002",groups={ValidateGroupEdit.class})
    public String getBankTrustCode() {
        return bankTrustCode;
    }
    
    
    /**
     * 获取 investmentAttributes
     * @return investmentAttributes.
     */
    @NotEmpty(fieldName = "FinPlanAssetC002.investmentAttributes",groups={ValidateGroupEdit.class})
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
     * 设置 bankTrustCode
     * @param bankTrustCode bankTrustCode
     */
    public void setBankTrustCode(String bankTrustCode) {
        this.bankTrustCode = bankTrustCode;
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
    public String getVdateStr(){
        return DateUtil.format(super.getVdate());
    }
    public String getMdateStr(){
        return DateUtil.format(super.getMdate());
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

    @NotEmpty(fieldName = "FinPlanAssetC002.isOpenEveryDay",groups={ValidateGroupEdit.class})
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

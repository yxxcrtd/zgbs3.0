package com.joyintech.tams.invest.basicdata.mstcommissionset.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.tams.common.base.entity.TrdFeeConfig;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.TrdFeeConfigDetail;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.basicdata.mstcommissionset.validator.MstCommissionSetValidator;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * 佣金设置验证
 * @author 裴家俊
 * @version 1.0 2019年3月22日
 * @see MstCommissionSetDto
 * @since 1.0
 */
@PageId("MstCommissionSet002")
@ValidLogic(targetClass = MstCommissionSetValidator.class, groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class MstCommissionSetDto extends TrdFeeConfig implements ProcessEntity {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 选择数据
     */
    private List<MstCommissionSetDto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;

    /**
     * 目的和意义以及可能会用到的地方
     */
    private String pageModel;

    private String secAcctName;

    /**
     * 计费值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal chargeValue;

    /**
     * 费用上限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeMaxi;

    /**
     * 费用下限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeMini;

    /**
     * 计费比率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal chargeRate;

    /**
     * 费用值/费率值
     */
    private String feeRate;

    /**
     * 画面Id - 工作流参数
     */
    private String pageId;

    /**
     * 流程中做pk1存json串
     */
    private String allData;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    @PrimaryKey(value = Field.PK_1)
    public String getAllData() {
        return allData;
    }

    public void setAllData(String allData) {
        this.allData = allData;
    }

    /**
     *  获取费用类型
     * @return 费用类型
     */
    @NotEmpty(fieldName = "MstCommissionSet002.feeType", groups = {ValidateGroupEdit.class})
    @Override
    public String getFeeType() {
        return super.getFeeType();
    }

    /**
     * 证券账号
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "MstCommissionSet002.tradeMarket", groups = {ValidateGroupEdit.class})
    public String getTradeMarket(){
        return super.getTradeMarket();
    }

    /**
     * 交易品种
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "MstCommissionSet002.tradeType", groups = {ValidateGroupEdit.class})
    public String getTradeType(){
        return super.getTradeType();
    }

    /**
     * 交易方向
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "MstCommissionSet002.ps", groups = {ValidateGroupEdit.class})
    public String getPs(){
        return super.getPs();
    }

    /**
     * 计费类型
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "MstCommissionSet002.chargeType", groups = {ValidateGroupEdit.class})
    public String getChargeType(){
        return super.getChargeType();
    }

    /**
     * 生效日
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "MstCommissionSet002.effDate", groups = {ValidateGroupEdit.class})
    public Date getEffDate(){
        return super.getEffDate();
    }
    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public String getSecAcctName() {
        return secAcctName;
    }

    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    @Override
    @PrimaryKey(value=Field.PK_2)
    public String getBatchSeq() {
        return StringUtil.isNotEmpty(super.getBatchSeq())?super.getBatchSeq():StringUtil.EMPTY;
    }

    /**
     * 操作类型
     * @return String
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 操作类型
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 选择的数据
     * @return List
     */
    public List<MstCommissionSetDto> getSelectedItem() {
        return selectedItem;
    }

    /**
     * 选择的数据
     *
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<MstCommissionSetDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * 要素id
     * @return String
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * 要素id
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 取得 计费值
     * @return 计费值
     */
    public BigDecimal getChargeValue() {
        return chargeValue;
    }

    /**
     * 设置 计费值
     * @param chargeValue 计费值
     */
    public void setChargeValue(BigDecimal chargeValue) {
        this.chargeValue = chargeValue;
    }

    /**
     * 取得 费用上限
     * @return 费用上限
     */
    public BigDecimal getFeeMaxi() {
        return feeMaxi;
    }

    /**
     * 设置 费用上限
     * @param feeMaxi 费用上限
     */
    public void setFeeMaxi(BigDecimal feeMaxi) {
        this.feeMaxi = feeMaxi;
    }

    /**
     * 取得 费用下限
     * @return 费用下限
     */
    public BigDecimal getFeeMini() {
        return feeMini;
    }

    /**
     * 设置 费用下限
     * @param feeMini 费用下限
     */
    public void setFeeMini(BigDecimal feeMini) {
        this.feeMini = feeMini;
    }

    /**
     * 取得 计费比率
     * @return 计费比率
     */
    public BigDecimal getChargeRate() {
        return chargeRate;
    }

    /**
     * 设置 计费比率
     * @param chargeRate 计费比率
     */
    public void setChargeRate(BigDecimal chargeRate) {
        this.chargeRate = chargeRate;
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所, this.getTradeMarket());
    }
    public String getTradeTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.平安资产类型, this.getTradeType());
    }
    public String getPsStr() {
        if(StringUtil.isNotEmpty(super.getPs()) ){
            List<String> psList=new ArrayList<String>();
            for(String psStr:super.getPs().split(",")){
                if(psStr.startsWith("T")){
                    psList.add(SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向, psStr));
                }
                else{
                    psList.add(SysDataUtil.getNameFromDataDict(BussinessDictCode.回购拆借方向, psStr));
                }
            }
            return String.join(",", psList);
        }
        return super.getPs();
    }
    public String getTermTypeStr() {
        if(StringUtil.isNotEmpty(super.getTermType()) ){
            List<String> psList=new ArrayList<String>();
            for(String psStr:super.getTermType().split(",")){
                psList.add(SysDataUtil.getNameFromDataDict(BussinessDictCode.期限品种,psStr));
            }
            return String.join(",", psList);
        }
        return super.getTermType();
    }
    public String getChargeTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.计费类型, this.getChargeType());
    }
    public String getChargeBasisStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.计费基数, this.getChargeBasis());
    }
    public String getFeeTypeStr(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.费用类型, this.getFeeType());
    }
    public String getIsPayWithTradeStr(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.是否, this.getIsPayWithTrade());
    }

    public String getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(String feeRate) {
        this.feeRate = feeRate;
    }

    //add by hudongyang 2020-08-05 start
    private List<TrdFeeConfigDetail>  trdFeeConfigDetailList;
    /**
     * 选择的数据
     * @return List
     */
    public List<TrdFeeConfigDetail> getTrdFeeConfigDetailList() {
        return trdFeeConfigDetailList;
    }

    /**
     * 选择的数据
     *
     * @param trdFeeConfigDetailList trdFeeConfigDetailList
     */
    public void setTrdFeeConfigDetailList(List<TrdFeeConfigDetail> trdFeeConfigDetailList) {
        this.trdFeeConfigDetailList = trdFeeConfigDetailList;
    }

    public String getAssetClassify1Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易所费用资产分类一, this.getAssetClassify1());
    }
    public String getAssetClassify2Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易所费用资产分类二, this.getAssetClassify2());
    }
    public String getCommCalMethodStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.佣金计算方式, this.getCommCalMethod());
    }
    public String getIsGradeStr(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.是否, this.getIsGrade());
    }
    public String getBrokerBaerFeeStr() {
        if(StringUtil.isNotEmpty(super.getBrokerBaerFee()) ){
            List<String> brokerBaerFeeList=new ArrayList<String>();
            for(String brokerBaerFeeStr:super.getBrokerBaerFee().split(",")){
                brokerBaerFeeList.add(SysDataUtil.getNameFromDataDict(BussinessDictCode.费用类型, brokerBaerFeeStr));
            }
            return String.join(",", brokerBaerFeeList);
        }
        return super.getBrokerBaerFee();
    }
    //add by hudongyang 2020-08-05 end
}

//package com.joyintech.tams.exchange.common.db.domain;
//
//import com.joyintech.tams.exchange.common.enums.ICodeEnum;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.Date;
//
///**
// * @author swwan
// * @date 2022/4/14 - 11:03
// */
///**
// * 交易所价格时间配置表
// */
//@ApiModel(value="交易所价格时间配置表")
//public class TrdPriceTimeConfig implements Serializable {
//    /**
//     * 配置代码
//     */
//    @ApiModelProperty(value="配置代码")
//    private BigDecimal configNo;
//
//    /**
//     * 指令/交易
//     */
//    @ApiModelProperty(value="指令/交易")
//    private String etrTrad;
//
//    /**
//     * 交易场所
//     */
//    @ApiModelProperty(value="交易场所")
//    private String tradeMarket;
//
//    /**
//     * 交易平台
//     */
//    @ApiModelProperty(value="交易平台")
//    private String tradePlat;
//
//    /**
//     * 资产分类一
//     */
//    @ApiModelProperty(value="资产分类一")
//    private String assetClassify1;
//
//    /**
//     * 资产分类二
//     */
//    @ApiModelProperty(value="资产分类二")
//    private String assetClassify2;
//
//    /**
//     * 资产分类三
//     */
//    @ApiModelProperty(value="资产分类三")
//    private String assetClassify3;
//
//    /**
//     * 交易方式
//     */
//    @ApiModelProperty(value="交易方式")
//    private String dealMode;
//
//    /**
//     * 申报价格范围类型
//     */
//    @ApiModelProperty(value="申报价格范围类型")
//    private String priceTimeType;
//
//    /**
//     * 开始时间
//     */
//    @ApiModelProperty(value="开始时间")
//    private String openTime;
//
//    /**
//     * 结束时间
//     */
//    @ApiModelProperty(value="结束时间")
//    private String closeTime;
//
//    /**
//     * 备注
//     */
//    @ApiModelProperty(value="备注")
//    private String remark;
//
//    /**
//     * 创建人
//     */
//    @ApiModelProperty(value="创建人")
//    private String createUser;
//
//    /**
//     * 创建部门
//     */
//    @ApiModelProperty(value="创建部门")
//    private String createDept;
//
//    /**
//     * 创建时间
//     */
//    @ApiModelProperty(value="创建时间")
//    private Date createTime;
//
//    /**
//     * 更新人
//     */
//    @ApiModelProperty(value="更新人")
//    private String updateUser;
//
//    /**
//     * 更新时间
//     */
//    @ApiModelProperty(value="更新时间")
//    private Date updateTime;
//
//    private static final long serialVersionUID = 1L;
//
//    public BigDecimal getConfigNo() {
//        return configNo;
//    }
//
//    public void setConfigNo(BigDecimal configNo) {
//        this.configNo = configNo;
//    }
//
//    public String getEtrTrad() {
//        return etrTrad;
//    }
//
//    public void setEtrTrad(String etrTrad) {
//        this.etrTrad = etrTrad == null ? null : etrTrad.trim();
//    }
//
//    public String getTradeMarket() {
//        return tradeMarket;
//    }
//
//    public void setTradeMarket(String tradeMarket) {
//        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
//    }
//
//    public String getTradePlat() {
//        return tradePlat;
//    }
//
//    public void setTradePlat(String tradePlat) {
//        this.tradePlat = tradePlat == null ? null : tradePlat.trim();
//    }
//
//    public String getAssetClassify1() {
//        return assetClassify1;
//    }
//
//    public void setAssetClassify1(String assetClassify1) {
//        this.assetClassify1 = assetClassify1 == null ? null : assetClassify1.trim();
//    }
//
//    public String getAssetClassify2() {
//        return assetClassify2;
//    }
//
//    public void setAssetClassify2(String assetClassify2) {
//        this.assetClassify2 = assetClassify2 == null ? null : assetClassify2.trim();
//    }
//
//    public String getAssetClassify3() {
//        return assetClassify3;
//    }
//
//    public void setAssetClassify3(String assetClassify3) {
//        this.assetClassify3 = assetClassify3 == null ? null : assetClassify3.trim();
//    }
//
//    public String getDealMode() {
//        return dealMode;
//    }
//
//    public void setDealMode(String dealMode) {
//        this.dealMode = dealMode == null ? null : dealMode.trim();
//    }
//
//    public String getPriceTimeType() {
//        return priceTimeType;
//    }
//
//    public void setPriceTimeType(String priceTimeType) {
//        this.priceTimeType = priceTimeType == null ? null : priceTimeType.trim();
//    }
//
//    public String getOpenTime() {
//        return openTime;
//    }
//
//    public void setOpenTime(String openTime) {
//        this.openTime = openTime == null ? null : openTime.trim();
//    }
//
//    public String getCloseTime() {
//        return closeTime;
//    }
//
//    public void setCloseTime(String closeTime) {
//        this.closeTime = closeTime == null ? null : closeTime.trim();
//    }
//
//    public String getRemark() {
//        return remark;
//    }
//
//    public void setRemark(String remark) {
//        this.remark = remark == null ? null : remark.trim();
//    }
//
//    public String getCreateUser() {
//        return createUser;
//    }
//
//    public void setCreateUser(String createUser) {
//        this.createUser = createUser == null ? null : createUser.trim();
//    }
//
//    public String getCreateDept() {
//        return createDept;
//    }
//
//    public void setCreateDept(String createDept) {
//        this.createDept = createDept == null ? null : createDept.trim();
//    }
//
//    public Date getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(Date createTime) {
//        this.createTime = createTime;
//    }
//
//    public String getUpdateUser() {
//        return updateUser;
//    }
//
//    public void setUpdateUser(String updateUser) {
//        this.updateUser = updateUser == null ? null : updateUser.trim();
//    }
//
//    public Date getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(Date updateTime) {
//        this.updateTime = updateTime;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getClass().getSimpleName());
//        sb.append(" [");
//        sb.append("Hash = ").append(hashCode());
//        sb.append(", configNo=").append(configNo);
//        sb.append(", etrTrad=").append(etrTrad);
//        sb.append(", tradeMarket=").append(tradeMarket);
//        sb.append(", tradePlat=").append(tradePlat);
//        sb.append(", assetClassify1=").append(assetClassify1);
//        sb.append(", assetClassify2=").append(assetClassify2);
//        sb.append(", assetClassify3=").append(assetClassify3);
//        sb.append(", dealMode=").append(dealMode);
//        sb.append(", priceTimeType=").append(priceTimeType);
//        sb.append(", openTime=").append(openTime);
//        sb.append(", closeTime=").append(closeTime);
//        sb.append(", remark=").append(remark);
//        sb.append(", createUser=").append(createUser);
//        sb.append(", createDept=").append(createDept);
//        sb.append(", createTime=").append(createTime);
//        sb.append(", updateUser=").append(updateUser);
//        sb.append(", updateTime=").append(updateTime);
//        sb.append("]");
//        return sb.toString();
//    }
//
//    public enum ETRTRADEnum implements ICodeEnum<String> {
//        ETR("ETR"),
//        TRAD("TRAD"),
//        ;
//        private final String code;
//
//        ETRTRADEnum(String code) {
//            this.code = code;
//        }
//
//        @Override
//        public String getCode() {
//            return code;
//        }
//    }
//}
/*
 * 文件名：ValuationSolutionManage003Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年7月19日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dto;

import java.util.List;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.validator.ValuationMethodScheme003Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * 估值方法方案 - 资产分类估值
 * 
 * @author zhangwei
 * @version 1.0 2018年8月27日
 * @see ValuationMethodScheme003Dto
 * @since 1.0
 */

@PageId("ValuationMethodScheme003")
@ValidLogic(targetClass = ValuationMethodScheme003Validator.class, groups = {ValidateGroupEdit.class,ValidateGroupDelete.class})
public class ValuationMethodScheme003Dto extends BaseEntity implements ProcessEntity {

    /**
	 * 序列号
	 */
	private static final long serialVersionUID = 2896156279776484261L;

	/**
     * 方案明细代码
     */
    @PrimaryKey(value = Field.PK_1)
//    @NotBlank(message = "方案明细代码不能为空！", groups = {ValidateGroupEdit.class})
    private String schemeDetailId;

    /**
     * 方案代码
     */
    @PrimaryKey(value = Field.PK_2)
//    @NotBlank(message = "方案代码不能为空！", groups = {ValidateGroupEdit.class})
    private String schemeId;

    /**
     * 证券品种
     */
    @PrimaryKey(value = Field.PK_3)
//    @NotBlank(message = "证券品种不能为空！", groups = {ValidateGroupEdit.class})
    private String secType;

    /**
     * 交易市场
     */
    @PrimaryKey(value = Field.PK_4)
    private String tradeMarket;

    /**
     * 证券细分类
     */
    @PrimaryKey(value = Field.PK_5)
    private String secDetailType;

    /**
     * 投资目的
     */
    @PrimaryKey(value = Field.PK_6)
    private String invAim;

    /**
     * 估值方法
     */
//    @NotBlank(message = "估值方法不能为空！", groups = {ValidateGroupEdit.class})
    private String valuationMethod;

    /**
     * 估值市价
     */
    private String valuationPrice;

    /**
     * 估值第二市价
     */
    private String valuationPrice2;

    /**
     * 行情位数
     */
    private Long quotaDigit;

    /**
     * 估值日期 - 行情日期
     */
    private String valuationDate;

    /**
     * 开始日
     */
    @PrimaryKey(value = Field.PK_7)
//    @NotBlank(message = "开始日不能为空！", groups = {ValidateGroupEdit.class})
    private String vdate;

    /**
     * 结束日
     */
    private String mdate;

    /**
     * 估值来源
     */
    private String valuationSource;
    
    /**
     * 行情尾数处理方式
     */
    private String priceTailMode;
    
    /**
     * 价格类型
     */
    private String priceClass;

    /**
     * 处理操作
     */
    private String operateType;

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String pageModel;

    /**
     * 估值模型
     */
    private String valuationId;

    public String getValuationId() {
        return valuationId;
    }

    public void setValuationId(String valuationId) {
        this.valuationId = valuationId;
    }

    public String getValuationSource() {
        return valuationSource;
    }

    public void setValuationSource(String valuationSource) {
        this.valuationSource = valuationSource;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getSchemeDetailId() {
        return schemeDetailId;
    }

    public void setSchemeDetailId(String schemeDetailId) {
        this.schemeDetailId = schemeDetailId;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getSecDetailType() {
        return secDetailType;
    }

    public void setSecDetailType(String secDetailType) {
        this.secDetailType = secDetailType;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getValuationMethod() {
        return valuationMethod;
    }

    public void setValuationMethod(String valuationMethod) {
        this.valuationMethod = valuationMethod;
    }

    public String getValuationPrice() {
        return valuationPrice;
    }

    public void setValuationPrice(String valuationPrice) {
        this.valuationPrice = valuationPrice;
    }

    public Long getQuotaDigit() {
        return quotaDigit;
    }

    public void setQuotaDigit(Long quotaDigit) {
        this.quotaDigit = quotaDigit;
    }

    public String getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(String valuationDate) {
        this.valuationDate = valuationDate;
    }

    public String getVdate() {
        return vdate;
    }

    public void setVdate(String vdate) {
        this.vdate = vdate;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getValuationPrice2() {
        return valuationPrice2;
    }

    public void setValuationPrice2(String valuationPrice2) {
        this.valuationPrice2 = valuationPrice2;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }
    
    public String getPriceTailMode() {
        return priceTailMode;
    }

    public void setPriceTailMode(String priceTailMode) {
        this.priceTailMode = priceTailMode;
    }
    
    public String getPriceClass() {
        return priceClass;
    }

    public void setPriceClass(String priceClass) {
        this.priceClass = priceClass;
    }
    
    /**
     * 选中的数据
     */
    private transient List<ValuationMethodScheme003Dto> listValuationMethodSchemeDto;


    public List<ValuationMethodScheme003Dto> getListValuationMethodSchemeDto() {
        return listValuationMethodSchemeDto;
    }

    public void setListValuationMethodSchemeDto(List<ValuationMethodScheme003Dto> listValuationMethodSchemeDto) {
        this.listValuationMethodSchemeDto = listValuationMethodSchemeDto;
    }

}

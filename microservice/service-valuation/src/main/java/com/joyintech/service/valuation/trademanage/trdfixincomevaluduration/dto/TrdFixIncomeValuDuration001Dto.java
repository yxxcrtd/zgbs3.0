/*
 * 文件名：TrdLendInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.valuation.trademanage.trdfixincomevaluduration.dto;


import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class TrdFixIncomeValuDuration001Dto extends WflTrdProductDeal implements ProcessEntity {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdFixIncomeValuDuration001Dto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;

    /**
     * 交易编号
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    private String tradeId;

    /**
     * 金融产品代码
     */
    private String prodId;

    /**
     * 金融产品名称
     */
    private String prodName;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 交易类型
     */
    private String ps;

    /**
     * 会计分类
     */
    private String invAim;

    /**
     * 交易日
     */

    private Date tradeDate;

    /**
     * 资金到账日
     */
    private Date settleDate;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 实收本金
     */
    private BigDecimal actCapi;

    /**
     * 实收利息
     */
    private BigDecimal actInt;

    /**
     * 是否结清
     */
    private String isClean2;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String myAcctNo;

    /**
     * 数据来源
     */
    private String dataSource;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public List<TrdFixIncomeValuDuration001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdFixIncomeValuDuration001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getActCapi() {
        return actCapi;
    }

    public void setActCapi(BigDecimal actCapi) {
        this.actCapi = actCapi;
    }

    public BigDecimal getActInt() {
        return actInt;
    }

    public void setActInt(BigDecimal actInt) {
        this.actInt = actInt;
    }

    public String getIsClean2() {
        return isClean2;
    }

    public void setIsClean2(String isClean2) {
        this.isClean2 = isClean2;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getMyAcctNo() {
        return myAcctNo;
    }

    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}

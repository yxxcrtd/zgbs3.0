/*
 * 文件名：TrdRepoTrade001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdintovaluationduration.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.trademanage.bond.dto.TrdBondTradeDto;
import com.joyintech.tams.valuation.trademanage.trdintovaluationduration.validator.TrdIntoValuationDuration001Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@PageId("TrdIntoValuationDuration002")
@ValidLogic(targetClass = TrdIntoValuationDuration001Validator.class, groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class TrdIntoValuationDuration001Dto extends BaseEntity implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 交易编号
     */

    private String tradeId;

    /**
     *  产品代码
     */
    private String prodId;

    /**
     *  产品名称
     */
    private String prodName;

    /**
     *  资产代码
     */
    private String secId;

    /**
     *  资产名称
     */
    private String secAbbr;

    /**
     * 交易类型
     */
    private String ps;

    /**
     * 计息开始日
     */
    private Date vdate;

    /**
     * 计息结束日
     */
    private Date mdate;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 交割日
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
     * 其他费用
     */
    private BigDecimal otherAmt;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String myAcctNo;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdIntoValuationDuration001Dto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;

    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.prodId", groups = {ValidateGroupEdit.class})
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.prodName", groups = {ValidateGroupEdit.class})
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.secId", groups = {ValidateGroupEdit.class})
    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.secAbbr", groups = {ValidateGroupEdit.class})
    public String getSecAbbr() {
        return secAbbr;
    }

    public void setSecAbbr(String secAbbr) {
        this.secAbbr = secAbbr;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.mdate", groups = {ValidateGroupEdit.class})
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.tradeDate", groups = {ValidateGroupEdit.class})
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.settleDate", groups = {ValidateGroupEdit.class})
    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.actCapi", groups = {ValidateGroupEdit.class})
    public BigDecimal getActCapi() {
        return actCapi;
    }

    public void setActCapi(BigDecimal actCapi) {
        this.actCapi = actCapi;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.actInt", groups = {ValidateGroupEdit.class})
    public BigDecimal getActInt() {
        return actInt;
    }

    public void setActInt(BigDecimal actInt) {
        this.actInt = actInt;
    }

    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.settleAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdIntoValuationDuration002.myAcctNo", groups = {ValidateGroupEdit.class})
    public String getMyAcctNo() {
        return myAcctNo;
    }

    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo;
    }

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

    public List<TrdIntoValuationDuration001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdIntoValuationDuration001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    //导出转化计息开始日
    private  String vdateStr;

    public String getVdateStr() {
        String vdateStr=" ";
        if(getTradeDate() !=null){
            vdateStr=DateUtil.format(getVdate());
        }
        return vdateStr;
    }

    //导出转化计息结束日
    private  String mdateStr;

    public String getMdateStr() {
        String mdateStr=" ";
        if(getTradeDate() !=null){
            mdateStr=DateUtil.format(getMdate());
        }
        return mdateStr;
    }

    //导出转化交易日
    private  String tradeDateStr;

    public String getTradeDateStr() {
        String tradeDateStr=" ";
        if(getTradeDate() !=null){
            tradeDateStr=DateUtil.format(getTradeDate());
        }
        return tradeDateStr;
    }
    //导出转化交割日
    private  String settleDateStr;

    public String getSettleDateStr() {
        String settleDateStr=" ";
        if(getSettleDate() !=null){
            settleDateStr=DateUtil.format(getSettleDate());
        }
        return settleDateStr;
    }

    /**
     * 取得 交易方向str
     *
     * @return 交易方向
     */
    public String getPsStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps))?
                SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps):ps;
    }

    /**
     * 取得 币种Fmt
     *
     * @return 币种
     */
    public String getCcyFmt() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.币种, ccy))?
                SysDataUtil.getNameFromDataDict(DictCode.币种, ccy):ccy;
    }

    //导出保留小数


    //实收利息 2位
    public String getActIntStr(){
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getActInt()==null?"0.00":decimalFormat.format(this.getActInt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }


    //实收本金 2位
    public String getActCapiStr(){
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getActCapi()==null?"0.00":decimalFormat.format(this.getActCapi().setScale(2,BigDecimal.ROUND_HALF_UP));
    }


    //结算金额 2位
    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }
}

/*
 * 文件名：TrdRepoTrade001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.dto;

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
import com.joyintech.tams.valuation.trademanage.trdintovaluationduration.validator.TrdIntoValuationDuration001Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@PageId("TrdFixIncomeValuDuration002")
@ValidLogic(targetClass = TrdIntoValuationDuration001Validator.class, groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class TrdFixIncomeValuDuration001Dto extends BaseEntity implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 交易编号
     */

    private String tradeId;


    /**
     * 组合代码  产品代码
     */
    private String portfolioId;


    /**
     * 组合名称  产品名称
     */
    private String portfolioName;

    /**
     * 市场机构代码  资产代码
     */
    private String finprodMarketId;

    /**
     * 金融产品名称  资产名称
     */
    private String finprodName;


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
     * 其他费用
     */
    private BigDecimal otherAmt;

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
    private String trdAcctId;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * elementId
     */
    private String elementId;


    /**
     * 融入券存续期实体类
     */
    private List<TrdFixIncomeValuDuration001Dto> selectedItem;

    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return portfolioId;
    }


    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.portfolioName", groups = {ValidateGroupEdit.class})
    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.finprodMarketId", groups = {ValidateGroupEdit.class})
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.finprodName", groups = {ValidateGroupEdit.class})
    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.invAim", groups = {ValidateGroupEdit.class})
    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.tradeDate", groups = {ValidateGroupEdit.class})
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.settleDate", groups = {ValidateGroupEdit.class})
    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.actCapi", groups = {ValidateGroupEdit.class})
    public BigDecimal getActCapi() {
        return actCapi;
    }

    public void setActCapi(BigDecimal actCapi) {
        this.actCapi = actCapi;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.actInt", groups = {ValidateGroupEdit.class})
    public BigDecimal getActInt() {
        return actInt;
    }

    public void setActInt(BigDecimal actInt) {
        this.actInt = actInt;
    }

//    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.otherAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.isClean2", groups = {ValidateGroupEdit.class})
    public String getIsClean2() {
        return isClean2;
    }

    public void setIsClean2(String isClean2) {
        this.isClean2 = isClean2;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.settleAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdFixIncomeValuDuration002.trdAcctId", groups = {ValidateGroupEdit.class})
    public String getTrdAcctId() {
        return trdAcctId;
    }

    public void setTrdAcctId(String trdAcctId) {
        this.trdAcctId = trdAcctId;
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

    public List<TrdFixIncomeValuDuration001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdFixIncomeValuDuration001Dto> selectedItem) {
        this.selectedItem = selectedItem;
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
    //导出转化资金到账日
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

//    public String getPsStr(){
//        String psStr="";
//        if(getPs()!=null){
//            if(getPs().equals("T01")){
//                psStr = "还本";
//            }else if(getPs().equals("T02")){
//                psStr="到期";
//            }
//        }
//        return psStr;
//    }

    /**
     * 取得 币种Fmt
     *
     * @return 币种
     */
    public String getCcyFmt() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.币种, ccy))?
                SysDataUtil.getNameFromDataDict(DictCode.币种, ccy):ccy;
    }

    /**
     * 获取投资目的中文名称
     * @return invAimStr
     */
    public String getInvAimStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.投资目的, invAim))?
                SysDataUtil.getNameFromDataDict(DictCode.投资目的, invAim):invAim;
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

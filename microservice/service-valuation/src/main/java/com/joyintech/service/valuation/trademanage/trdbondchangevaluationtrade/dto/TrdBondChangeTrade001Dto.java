package com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class TrdBondChangeTrade001Dto extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 交易编号
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    private String tradeId;

    /**
     * 外部成交编号
     */
    private String outTradeId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 产品名称
     */
    private String finprodName;

    /**
     * 转出证券代码
     */
    private String oAssetId;

    /**
     * 转出证券名称
     */
    private String oAssetAbbr;

    /**
     * 转入证券代码
     */
    private String cAssetId;

    /**
     * 转入证券名称
     */
    private String cAssetAbbr;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 交割日期
     */
    private Date settleDate;

    /**
     * 会计分类
     */
    private String invAim;

    /**
     * 转出数量
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal oTradeNum;

    /**
     * 转出金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal oTradeAmt;

    /**
     * 转入数量
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal cTradeNum;

    /**
     * 转入金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal cTradeAmt;

    /**
     * 币种
     */
    private String ccy;


    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String acctNo;

    /**
     * 选中的数据
     */
    private transient List<TrdBondChangeTrade001Dto> selectedItem;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 要素id
     */
    private String elementId;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getoAssetId() {
        return oAssetId;
    }

    public void setoAssetId(String oAssetId) {
        this.oAssetId = oAssetId;
    }

    public String getoAssetAbbr() {
        return oAssetAbbr;
    }

    public void setoAssetAbbr(String oAssetAbbr) {
        this.oAssetAbbr = oAssetAbbr;
    }

    public String getcAssetId() {
        return cAssetId;
    }

    public void setcAssetId(String cAssetId) {
        this.cAssetId = cAssetId;
    }

    public String getcAssetAbbr() {
        return cAssetAbbr;
    }

    public void setcAssetAbbr(String cAssetAbbr) {
        this.cAssetAbbr = cAssetAbbr;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
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

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
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

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getInvAimStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.投资目的, invAim))?
                SysDataUtil.getNameFromDataDict(DictCode.投资目的, invAim):invAim;
    }

    public BigDecimal getoTradeNum() {
        return oTradeNum;
    }

    public void setoTradeNum(BigDecimal oTradeNum) {
        this.oTradeNum = oTradeNum;
    }

    public BigDecimal getoTradeAmt() {
        return oTradeAmt;
    }

    public void setoTradeAmt(BigDecimal oTradeAmt) {
        this.oTradeAmt = oTradeAmt;
    }

    public BigDecimal getcTradeNum() {
        return cTradeNum;
    }

    public void setcTradeNum(BigDecimal cTradeNum) {
        this.cTradeNum = cTradeNum;
    }

    public BigDecimal getcTradeAmt() {
        return cTradeAmt;
    }

    public void setcTradeAmt(BigDecimal cTradeAmt) {
        this.cTradeAmt = cTradeAmt;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
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

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public List<TrdBondChangeTrade001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdBondChangeTrade001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getOTradeNumStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getoTradeNum()==null?"0.00":decimalFormat.format(this.getoTradeNum().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getOTradeAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getoTradeAmt()==null?"0.00":decimalFormat.format(this.getoTradeAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getCTradeNumStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getcTradeNum()==null?"0.00":decimalFormat.format(this.getcTradeNum().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getCTradeAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getcTradeAmt()==null?"0.00":decimalFormat.format(this.getcTradeAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }
}

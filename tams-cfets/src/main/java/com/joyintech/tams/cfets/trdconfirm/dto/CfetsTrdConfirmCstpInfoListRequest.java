package com.joyintech.tams.cfets.trdconfirm.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 银行间交易成交确认
 * 查询Cstp下行列表分页查询条件DTO
 * @author
 * @version 1.0 2021年10月26日
 * @see CfetsTrdConfirmCstpInfoListRequest
 * @since 1.0
 */
@ApiModel(value="查询Cstp下行列表请求参数",description="查询Cstp下行列表请求参数")
public class CfetsTrdConfirmCstpInfoListRequest {
    @ApiModelProperty(name="regAssetType",value="资产类型",example="01")
    private String regAssetType;

    @ApiModelProperty(name="finprodId",value="资产代码",example="字符串1,字符串2,...")
    private String finprodId;

    @ApiModelProperty(name="ps",value="交易方向",example="T01")
    private String ps;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="01")
    private String settleSpeed;

    @ApiModelProperty(name="acctNo",value="托管账户",example="字符串1,字符串2,...")
    private String acctNo;

    @ApiModelProperty(name="secAcctId",value="托管账户",example="字符串1,字符串2,...")
    private String secAcctId;

    @ApiModelProperty(name="execid",value="成交编号",example="A1")
    private String execid;

    @ApiModelProperty(name="quoteid",value="报价编号",example="T01")
    private String quoteid;

    @ApiModelProperty(name="traderid",value="本方",example="T01")
    private String traderid;

    @ApiModelProperty(name="counterTrader",value="对手方",example="T01")
    private String counterTrader;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

    @ApiModelProperty(name="selectedItem",value="选中数据",example="字符串1")
    private transient List<CfetsTrdConfirmRegInfoListResponse> selectedItem;

    public String getRegAssetType() {
        return regAssetType;
    }

    public void setRegAssetType(String regAssetType) {
        this.regAssetType = regAssetType;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
    }

    public String getQuoteid() {
        return quoteid;
    }

    public void setQuoteid(String quoteid) {
        this.quoteid = quoteid;
    }

    public String getTraderid() {
        return traderid;
    }

    public void setTraderid(String traderid) {
        this.traderid = traderid;
    }

    public String getCounterTrader() {
        return counterTrader;
    }

    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }


    public List<CfetsTrdConfirmRegInfoListResponse> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsTrdConfirmRegInfoListResponse> selectedItem) {
        this.selectedItem = selectedItem;
    }
}


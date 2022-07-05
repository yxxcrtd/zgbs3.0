/*
 * 文件名：EtrExchangeTradeDto002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 *
 * @author lby
 * @version 1.0 2020年9月17日
 * @see EtrExchangeUnOpenQuotaRequestDto
 * @since 1.0
 */
@ApiModel(value="非公开行情查询请求参数",description="非公开行情查询请求参数")
public class EtrExchangeUnOpenQuotaRequestDto {

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;
    private String requestId;

    @ApiModelProperty(name="bussType",value="业务类型",example="A1")
    private String bussType;

    @ApiModelProperty(name="zyj",value="质押券代码列表",example="A1")
    private List<String> zyj;

    @ApiModelProperty(name="search",value="关键字",example="A1")
    private String search;


    @ApiModelProperty(name = "requestNo", value = "请求编号")
    private String requestNo;
    @ApiModelProperty(name = "origSerialNo", value = "初始交易编号")
    private String origSerialNo;
    @ApiModelProperty(name = "account", value = "本方资金账户")
    private String account;
    @ApiModelProperty(name = "requestType", value = "请求类型： 首期-F,到期续做-X,解除质押-C,中途换券-H,提前购回-Z")
    private String requestType;
    @ApiModelProperty(name = "stockCode", value = "质押券")
    private String stockCode;
    @ApiModelProperty(name = "pledgeNumber", value = "质押数量")
    private BigDecimal pledgeNumber;
    @ApiModelProperty(name = "repoAmount", value = "回购金额")
    private BigDecimal repoAmount;
    @ApiModelProperty(name = "compactTerm", value = "回购期限")
    private Integer compactTerm;
    @ApiModelProperty(name = "repoRate", value = "回购利率")
    private BigDecimal repoRate;
    @ApiModelProperty(name = "settleAmount", value = "结算金额")
    private BigDecimal settleAmount;
    @ApiModelProperty(name = "firstSettleDate", value = "首次结算日")
    private String firstSettleDate;
    @ApiModelProperty(name = "remark", value = "补充协议")
    private String remark;

    public String getBussType() {
        return bussType;
    }

    public void setBussType(String bussType) {
        this.bussType = bussType;
    }

    public List<String> getZyj() {
        return zyj;
    }

    public void setZyj(List<String> zyj) {
        this.zyj = zyj;
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


    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getOrigSerialNo() {
        return origSerialNo;
    }

    public void setOrigSerialNo(String origSerialNo) {
        this.origSerialNo = origSerialNo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public BigDecimal getRepoAmount() {
        return repoAmount;
    }

    public void setRepoAmount(BigDecimal repoAmount) {
        this.repoAmount = repoAmount;
    }

    public Integer getCompactTerm() {
        return compactTerm;
    }

    public void setCompactTerm(Integer compactTerm) {
        this.compactTerm = compactTerm;
    }

    public BigDecimal getRepoRate() {
        return repoRate;
    }

    public void setRepoRate(BigDecimal repoRate) {
        this.repoRate = repoRate;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    public String getFirstSettleDate() {
        return firstSettleDate;
    }

    public void setFirstSettleDate(String firstSettleDate) {
        this.firstSettleDate = firstSettleDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

package com.joyintech.tams.cfets.etr.bondfirstbid.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: kchen
 * @Date: 2022/4/19 9:31
 */
@ApiModel(value="一级投标指令提交类",description="一级投标指令提交类")
public class CfetsEtrBondFirstBid002Dto extends EtrEntrust implements ProcessEntity {

    /**
     * 按钮id
     */
    @ApiModelProperty(name="elementId",value="按钮id")
    private String elementId;

    /**
     * 操作类型
     */
    @ApiModelProperty(name="operateType",value="操作类型")
    private String operateType;


    /**
     *  发行方式
     */
    @ApiModelProperty(name="issueClass",value="发行方式")
    private String issueClass;

    /**
     *  发行方式字符串
     */
    @ApiModelProperty(name="issueClassStr",value="发行方式字符串")
    private String issueClassStr;

    /**
     * 发行价格
     */
    @ApiModelProperty(name = "issuePrice", value = "发行价格")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal issuePrice;

    /**
     * 收益率，利息贴现为参考收益率，附息为票面利率
     */
    @ApiModelProperty(name = "referYield", value = "收益率，利息贴现为参考收益率，附息为票面利率")
    private String referYield;


    /**
     *  招标标的
     */
    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    /**
     *  招标标的字符串
     */
    @ApiModelProperty(name="tenderTargetStr",value="招标标的字符串")
    private String tenderTargetStr;

    /**
     *  投标起始日
     */
    @ApiModelProperty(name="tenderSdate",value="投标起始日")
    private Date tenderSdate;

    /**
     *  投标截止日
     */
    @ApiModelProperty(name="tenderEdate",value="投标截止日")
    private Date tenderEdate;

    /**
     *  缴款截止日
     */
    @ApiModelProperty(name="payEdate",value="缴款截止日")
    private Date payEdate;

    /**
     *  缴款截止日格式化
     */
    @ApiModelProperty(name="payEdateStr",value="缴款截止日格式化")
    private String payEdateStr;

    /**
     *  指令日期格式化
     */
    @ApiModelProperty(name="entrustDateStr",value="指令日期格式化")
    private String entrustDateStr;

    /**
     *  发行日
     */
    @ApiModelProperty(name="issueDate",value="发行日")
    private Date issueDate;

    @ApiModelProperty(name="issueDateStr",value="发行日格式化")
    private String issueDateStr;

    /**
     *  缴款日
     */
    @ApiModelProperty(name="payDate",value="缴款日")
    private Date payDate;

    @ApiModelProperty(name="payDateStr",value="缴款日格式化")
    private String payDateStr;

    /**
     * 风险试算实体类
     */
    @ApiModelProperty(name="riskTrialRetDto",value="风险试算实体类")
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 风险试算dto
     */
    @ApiModelProperty(name="cfetsEtribdoRiskcalReturn",value="风险试算dto",example="CfetsEtribdoRiskcalReturn")
    private CfetsEtribdoRiskcalResponse cfetsEtribdoRiskcalReturn;

    /**
     *  汇总投标券面总额
     */
    @ApiModelProperty(name="totalBiddingNum",value="汇总投标券面总额")
    private BigDecimal totalBiddingNum;

    /**
     * 组合分配：投标产品列表
     */
    @ApiModelProperty(name="biddingProductList",value="组合分配",example="T01", required = true)
    List<CfetsBiddingProductDto> biddingProductList;

    /**
     * 多组合汇总指令ID
     */
    @ApiModelProperty(name="totalEtrustId",value="多组合汇总指令ID")
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    private String totalEtrustId;

    /**
     * 软控是否继续 Y：继续
     */
    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    private String softSkipMode;

    List<CfetsEtrBondFirstBid002Dto> etrInvestBondFirstBidDtos;

    /**
     * 资产简称
     */
    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="资产简称")
    private String finprodAbbr;

    /**
     * 接收状态
     */
    @ApiModelProperty(name="recStatus",value="接收状态",example="01")
    private String recStatus;

    /**
     * 交易方向
     */
    @ApiModelProperty(name="ps",value="交易方向",example="T01")
    private String ps;

    /**
     * 证券托管户
     */
    @ApiModelProperty(name="secAcctId",value="证券托管户",example="证券托管户")
    private String secAcctId;

    /**
     * 文件上传dto
     */
    @ApiModelProperty(name="fileArray",value="文件上传dto",example="文件上传dto")
    private List<SysUploadFileInfo> fileArray;

    /**
     * 指令下达时间
     */
    @ApiModelProperty(name="transmitTime",value="指令下达时间",example="18:30:00")
    private Date transmitTime;

    /**
     * 投资人员信息DTO
     */
    @Valid
    @ApiModelProperty(name="etrInvestPersonInfo",value="投资人员信息DTO")
    private BussEtrInvestPersonInfoDto etrInvestPersonInfo;

    /**
     * 选中的数据
     */
    @ApiModelProperty(name="selectedItem",value="选中的数据")
    private transient List<CfetsEtrBondFirstBid002Dto> selectedItem;

    /**
     * 交易对手名称
     */
    @ApiModelProperty(name="counterName",value="交易对手名称")
    private String counterName;

    /**
     * 市场代码
     */
    @ApiModelProperty(name = "finprodMarketId", value = "市场代码")
    private String finprodMarketId;

    /**
     * 系统动作
     */
    @ApiModelProperty(name = "systemAct", value = "系统动作")
    private String systemAct;

    @ApiModelProperty(name = "invManagerName", value = "投资经理名称")
    private String invManagerName;

    @ApiModelProperty(name = "ccyStr", value = "币种名称")
    private String ccyStr;

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public Date getTenderSdate() {
        return tenderSdate;
    }

    public void setTenderSdate(Date tenderSdate) {
        this.tenderSdate = tenderSdate;
    }

    public Date getTenderEdate() {
        return tenderEdate;
    }

    public void setTenderEdate(Date tenderEdate) {
        this.tenderEdate = tenderEdate;
    }

    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public CfetsEtribdoRiskcalResponse getCfetsEtribdoRiskcalReturn() {
        return cfetsEtribdoRiskcalReturn;
    }

    public void setCfetsEtribdoRiskcalReturn(CfetsEtribdoRiskcalResponse cfetsEtribdoRiskcalReturn) {
        this.cfetsEtribdoRiskcalReturn = cfetsEtribdoRiskcalReturn;
    }

    public BigDecimal getTotalBiddingNum() {
        return totalBiddingNum;
    }

    public void setTotalBiddingNum(BigDecimal totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    public List<CfetsBiddingProductDto> getBiddingProductList() {
        return biddingProductList;
    }

    public void setBiddingProductList(List<CfetsBiddingProductDto> biddingProductList) {
        this.biddingProductList = biddingProductList;
    }

    public String getTotalEtrustId() {
        return totalEtrustId;
    }

    public void setTotalEtrustId(String totalEtrustId) {
        this.totalEtrustId = totalEtrustId;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public List<CfetsEtrBondFirstBid002Dto> getEtrInvestBondFirstBidDtos() {
        return etrInvestBondFirstBidDtos;
    }

    public void setEtrInvestBondFirstBidDtos(List<CfetsEtrBondFirstBid002Dto> etrInvestBondFirstBidDtos) {
        this.etrInvestBondFirstBidDtos = etrInvestBondFirstBidDtos;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(String recStatus) {
        this.recStatus = recStatus;
    }

    @Override
    public String getPs() {
        return ps;
    }

    @Override
    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public List<SysUploadFileInfo> getFileArray() {
        return fileArray;
    }

    public void setFileArray(List<SysUploadFileInfo> fileArray) {
        this.fileArray = fileArray;
    }

    public Date getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(Date transmitTime) {
        this.transmitTime = transmitTime;
    }

    public BussEtrInvestPersonInfoDto getEtrInvestPersonInfo() {
        return etrInvestPersonInfo;
    }

    public void setEtrInvestPersonInfo(BussEtrInvestPersonInfoDto etrInvestPersonInfo) {
        this.etrInvestPersonInfo = etrInvestPersonInfo;
    }

    public List<CfetsEtrBondFirstBid002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsEtrBondFirstBid002Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getReferYield() {
        return referYield;
    }

    public void setReferYield(String referYield) {
        this.referYield = referYield;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public String getIssueClassStr() {
        if (StringUtil.isEmpty(getIssueClass())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, getIssueClass());
    }

    public void setIssueClassStr(String issueClassStr) {
        this.issueClassStr = issueClassStr;
    }

    public String getTenderTargetStr() {
        if (StringUtil.isEmpty(getIssueClass())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, getIssueClass());
    }

    public void setTenderTargetStr(String tenderTargetStr) {
        this.tenderTargetStr = tenderTargetStr;
    }

    public String getPayEdateStr() {
        if (null != this.getPayEdate()) {
            return DateUtil.format(this.getPayEdate());
        }
        return payEdateStr;
    }

    public void setPayEdateStr(String payEdateStr) {
        this.payEdateStr = payEdateStr;
    }

    public String getEntrustDateStr() {
        if (null != this.getEntrustDate()) {
            return DateUtil.format(this.getEntrustDate());
        }
        return entrustDateStr;
    }

    public void setEntrustDateStr(String entrustDateStr) {
        this.entrustDateStr = entrustDateStr;
    }

    public String getIssueDateStr() {
        if (null != this.getIssueDate()) {
            return DateUtil.format(this.getIssueDate());
        }
        if (null != this.getTenderSdate() &&  null != this.getTenderEdate()) {
            return DateUtil.format(this.getTenderSdate())+"~"+DateUtil.format(this.getTenderEdate());
        }
        return issueDateStr;
    }

    public void setIssueDateStr(String issueDateStr) {
        this.issueDateStr = issueDateStr;
    }

    public String getPayDateStr() {
        if (null != this.getPayDate()) {
            return DateUtil.format(this.getPayDate());
        }
        return payDateStr;
    }

    public void setPayDateStr(String payDateStr) {
        this.payDateStr = payDateStr;
    }

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public String getCcyStr() {
        if (StringUtil.isEmpty(getCcy())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.币种, getCcy());
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
    }
}

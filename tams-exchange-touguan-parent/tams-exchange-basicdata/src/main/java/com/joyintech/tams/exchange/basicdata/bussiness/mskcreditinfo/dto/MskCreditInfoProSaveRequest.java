package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto;


import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;



public class MskCreditInfoProSaveRequest extends BaseEntity implements ProcessEntity{

    @ApiModelProperty(name="operateType",value="操作类型",example="submit/save", required = true)
    private String operateType;
    @ApiModelProperty(name="elementId",value="按钮id",example="MstCreditMarketInfo001_btn_submit/MstCreditMarketInfo001_btn_save")
    private String elementId;
    @ApiModelProperty(name="pageId",value="d",example="MstCreditMarketInfo001")
    private String pageId;
    @ApiModelProperty(name="selectedItem",value="选中数据",example="A1,A2..", required = true)
    private List<MskCreditInfoProSaveRequest> selectedItem;

    @ApiModelProperty(name="secid",value="凭证代码",example="A1", required = true)
    private String secid;

    @ApiModelProperty(name="cdate",value="估值日",example="A1", required = true)
    private Date cdate;

    @ApiModelProperty(name="mdprice",value="估值全价",example="A1", required = true)
    private String mdprice;

    @ApiModelProperty(name="mcprice",value="估值净价",example="A1", required = true)
    private String mcprice;

    @ApiModelProperty(name="mint",value="估值利息",example="A1", required = true)
    private String mint;

    @ApiModelProperty(name="myield",value="估值收益率",example="A1", required = true)
    private BigDecimal myield;

    @ApiModelProperty(name="mMduration",value="估价修正久期",example="A1", required = true)
    private String mMduration;

    @ApiModelProperty(name="mCnvxty",value="市价凸性",example="A1", required = true)
    private String mCnvxty;

    @ApiModelProperty(name="issueMarketNum",value="发行市场数",example="A1", required = true)
    private String issueMarketNum;

    @ApiModelProperty(name="creditType",value="信用类型",example="A1", required = true)
    private String creditType;

    @ApiModelProperty(name="valueSource",value="估值来源",example="A1", required = true)
    private String valueSource;

    @ApiModelProperty(name="cdateStart",value="起息日开始日",example="2021-01-01")
    private String cdateStart;

    @ApiModelProperty(name="cdateEnd",value="起息日结束日",example="2021-01-01")
    private String cdateEnd;

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    private String processState;

    public String getProcessState() {
        return processState;
    }
    public void setProcessState(String processState) {
        this.processState = processState;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getCdateStart() {
        return cdateStart;
    }

    public void setCdateStart(String cdateStart) {
        this.cdateStart = cdateStart;
    }

    public String getCdateEnd() {
        return cdateEnd;
    }

    public void setCdateEnd(String cdateEnd) {
        this.cdateEnd = cdateEnd;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getSecid() {
        return secid;
    }

    public void setSecid(String secid) {
        this.secid = secid;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public String getMdprice() {
        return mdprice;
    }

    public void setMdprice(String mdprice) {
        this.mdprice = mdprice;
    }

    public String getMcprice() {
        return mcprice;
    }

    public void setMcprice(String mcprice) {
        this.mcprice = mcprice;
    }

    public String getMint() {
        return mint;
    }

    public void setMint(String mint) {
        this.mint = mint;
    }

    public BigDecimal getMyield() {
        return myield;
    }

    public void setMyield(BigDecimal myield) {
        this.myield = myield;
    }

    public String getmMduration() {
        return mMduration;
    }

    public void setmMduration(String mMduration) {
        this.mMduration = mMduration;
    }

    public String getmCnvxty() {
        return mCnvxty;
    }

    public void setmCnvxty(String mCnvxty) {
        this.mCnvxty = mCnvxty;
    }

    public String getIssueMarketNum() {
        return issueMarketNum;
    }

    public void setIssueMarketNum(String issueMarketNum) {
        this.issueMarketNum = issueMarketNum;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public String getValueSourceStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.估值来源, this.getValueSource());
    }
    public String getValueSource() {
        return valueSource;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
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

    public List<MskCreditInfoProSaveRequest> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<MskCreditInfoProSaveRequest> selectedItem) {
        this.selectedItem = selectedItem;
    }

}

package com.joyintech.tams.business.asset.protect.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class CreditProtectionCertificateRequest {

    @ApiModelProperty(name="pageId",value="画面ID",example="CreditProtectionCertificate001")
    private String pageId;

    @ApiModelProperty(name="finprodIdAbrr",value="资产代码")
    private String finprodIdAbrr;

    @ApiModelProperty(name="issueRshortStr",value="创立机构")
    private String issueRshortStr;

    @ApiModelProperty(name="refEntity",value="参考实体")
    private String refEntity;

    @ApiModelProperty(name="refSec",value="参考债项")
    private String refSec;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="initValue",value="初始值(方法：getfinprodIdForAdd(选填))")
    private String initValue;

    @ApiModelProperty(name="search",value="查询对象")
    private String search;

    @ApiModelProperty(name="vdate",value="起息日",example="2021-01-01")
    private List<String> vdate;

    @ApiModelProperty(name="vdateEnd",value="起息日结束日",example="2021-01-01")
    private String vdateEnd;

    @ApiModelProperty(name="vdateStart",value="起息日开始日",example="2021-01-01")
    private String vdateStart;

    @ApiModelProperty(name="mdate",value="到期日")
    private  List<String>  mdate;

    @ApiModelProperty(name="mdateEnd",value="到期日结束日",example="2021-01-01")
    private String mdateEnd;

    @ApiModelProperty(name="mdateStart",value="到期日开始日",example="2021-01-01")
    private String mdateStart;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getFinprodIdAbrr() {
        return finprodIdAbrr;
    }

    public void setFinprodIdAbrr(String finprodIdAbrr) {
        this.finprodIdAbrr = finprodIdAbrr;
    }

    public String getIssueRshortStr() {
        return issueRshortStr;
    }

    public void setIssueRshortStr(String issueRshortStr) {
        this.issueRshortStr = issueRshortStr;
    }

    public String getRefEntity() {
        return refEntity;
    }

    public void setRefEntity(String refEntity) {
        this.refEntity = refEntity;
    }

    public String getRefSec() {
        return refSec;
    }

    public void setRefSec(String refSec) {
        this.refSec = refSec;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getInitValue() {
        return initValue;
    }

    public void setInitValue(String initValue) {
        this.initValue = initValue;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getVdate() {
        return vdate;
    }

    public void setVdate(List<String> vdate) {
        this.vdate = vdate;
    }

    public String getVdateEnd() {
        return vdateEnd;
    }

    public void setVdateEnd(String vdateEnd) {
        this.vdateEnd = vdateEnd;
    }

    public String getVdateStart() {
        return vdateStart;
    }

    public void setVdateStart(String vdateStart) {
        this.vdateStart = vdateStart;
    }

    public List<String> getMdate() {
        return mdate;
    }

    public void setMdate(List<String> mdate) {
        this.mdate = mdate;
    }

    public String getMdateEnd() {
        return mdateEnd;
    }

    public void setMdateEnd(String mdateEnd) {
        this.mdateEnd = mdateEnd;
    }

    public String getMdateStart() {
        return mdateStart;
    }

    public void setMdateStart(String mdateStart) {
        this.mdateStart = mdateStart;
    }
}

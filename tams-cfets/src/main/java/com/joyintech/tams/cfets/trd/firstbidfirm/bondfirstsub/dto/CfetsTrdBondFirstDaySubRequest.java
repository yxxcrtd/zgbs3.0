package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "当日申购列表查询条件", description = "当日申购列表查询条件")
public class CfetsTrdBondFirstDaySubRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "finprodMarketIds", value = "市场代码,列表 etrAddSearch,pageDaySubDetail 接口必传", example = "[A,B]")
    private List<String> finprodMarketIds;

    @ApiModelProperty(name = "secManageAcctIds ", value = "证券账户,列表 etrAddSearch,pageDaySubDetail 接口必传", example = "[A,B]")
    private List<String> secManageAcctIds;

    @ApiModelProperty(name = "ouParty", value = "本方",example = "1234123")
    private String ouParty;

    @ApiModelProperty(name = "issuer", value = "发行人",example = "q23412")
    private String issuer;

    @ApiModelProperty(name = "search", value = "关键词",example = "XXXX")
    private String search;

    @ApiModelProperty(name = "entrustId", value = "指令代码",example = "WTBOND202204201111")
    private String entrustId;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public List<String> getFinprodMarketIds() {
        return finprodMarketIds;
    }

    public void setFinprodMarketIds(List<String> finprodMarketIds) {
        this.finprodMarketIds = finprodMarketIds;
    }

    public List<String> getSecManageAcctIds() {
        return secManageAcctIds;
    }

    public void setSecManageAcctIds(List<String> secManageAcctIds) {
        this.secManageAcctIds = secManageAcctIds;
    }

    public String getOuParty() {
        return ouParty;
    }

    public void setOuParty(String ouParty) {
        this.ouParty = ouParty;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }
}
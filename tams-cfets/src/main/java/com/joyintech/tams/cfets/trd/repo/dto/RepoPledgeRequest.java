package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.background.component.common.pack.common.util.ArithmeticUtil;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 质押券请求参数
 *
 * @author jiangpeng
 * @date 2021/12/09
 */
@ApiModel(value = "质押券请求参数", description = "质押券请求参数")
public class RepoPledgeRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'portfolioId', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(required = false, name = "pledgeDtos", value = "已质押债券")
    private List<RepoEntrustPledgeDto> pledgeDtos;

    @ApiModelProperty(required = false, name = "entrustIdList", value = "指令代码列表", example = "['WTREPO202107081619']")
    private List<String> entrustIdList;

    @ApiModelProperty(required = false, name = "finprodIdList", value = "债券代码", example = "xxx.IB")
    private List<String> finprodIdList;

    @ApiModelProperty(required = false, name = "finprodTypeList", value = "债券类型List", example = "['003']")
    private List<String> finprodTypeList;

    @ApiModelProperty(required = false, name = "repRegistOrgList", value = "登记托管机构代码List", example = "['01']")
    private List<String> repRegistOrgList;

    @ApiModelProperty(required = false, name = "peroidStr", value = "代偿期", example = "1.0Y")
    private String peroidStr;

    @ApiModelProperty(required = false, name = "pledgeStockId", value = "债券库代码", example = "99999")
    private String pledgeStockId;

    @ApiModelProperty(required = false, name = "pledgeStockIdList", value = "债券库代码List", example = "['99999']")
    private List<String> pledgeStockIdList;

    public List<RepoEntrustPledgeDto> getPledgeDtos() {
        return pledgeDtos;
    }

    public void setPledgeDtos(List<RepoEntrustPledgeDto> pledgeDtos) {
        this.pledgeDtos = pledgeDtos;
    }

    public List<String> getEntrustIdList() {
        return entrustIdList;
    }

    public void setEntrustIdList(List<String> entrustIdList) {
        this.entrustIdList = entrustIdList;
    }

    public List<String> getFinprodIdList() {
        return finprodIdList;
    }

    public void setFinprodIdList(List<String> finprodIdList) {
        this.finprodIdList = finprodIdList;
    }

    public List<String> getFinprodTypeList() {
        return finprodTypeList;
    }

    public void setFinprodTypeList(List<String> finprodTypeList) {
        this.finprodTypeList = finprodTypeList;
    }

    public List<String> getRepRegistOrgList() {
        return repRegistOrgList;
    }

    public void setRepRegistOrgList(List<String> repRegistOrgList) {
        this.repRegistOrgList = repRegistOrgList;
    }

    public String getPeroidStr() {
        return peroidStr;
    }

    public void setPeroidStr(String peroidStr) {
        this.peroidStr = peroidStr;
    }

    public BigDecimal getPeroid() {
        if(StringUtil.isEmpty(this.getPeroidStr())){
            return null;
        }
        String peroidDateType = this.getPeroidStr().substring(this.getPeroidStr().length() - 1);
        String peroidNum = this.getPeroidStr().substring(0, this.getPeroidStr().length() - 1);
        BigDecimal peroid = new BigDecimal(peroidNum);
        if (StringUtil.equals("D", peroidDateType)) {
            peroid = ArithmeticUtil.divide(peroid, new BigDecimal(365), 2, RoundingMode.HALF_UP);
        }
        return peroid;
    }

    public String getPledgeStockId() {
        return pledgeStockId;
    }

    public void setPledgeStockId(String pledgeStockId) {
        this.pledgeStockId = pledgeStockId;
    }

    public List<String> getPledgeStockIdList() {
        return pledgeStockIdList;
    }

    public void setPledgeStockIdList(List<String> pledgeStockIdList) {
        this.pledgeStockIdList = pledgeStockIdList;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }
}


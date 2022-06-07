package com.joyintech.tams.business.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "页面展示配置实体类")
public class CfgDataTableShow {

    @ApiModelProperty(name = "pageCode", value = "页面编码")
    private String pageCode;

    @ApiModelProperty(name = "dtCode", value = "区域编码")
    private String dtCode;

    @ApiModelProperty(name = "dtCode", value = "特殊条件编码")
    private String spcdCode;

    @ApiModelProperty(name = "colCode", value = "列的字段名")
    private String colCode;

    @ApiModelProperty(name = "colName", value = "列的名称")
    private String colName;

    @ApiModelProperty(name = "dictCode", value = "字典值的字段名")
    private String dictCode;

    @ApiModelProperty(name = "seqNo", value = "顺序号")
    private Integer seqNo;

    @ApiModelProperty(name = "disPlayed", value = "是否显示(0:不显示,1:显示)")
    private Integer disPlayed;

    @ApiModelProperty(name = "frozen", value = "是否冻结(0:不冻结,1:冻结)")
    private Integer frozen;

    @ApiModelProperty(name = "sort", value = "是否排序")
    private Integer sort;

    @ApiModelProperty(name = "maxLen", value = "最大长度(px单位)")
    private Integer maxLen;

    @ApiModelProperty(name = "extendCfg", value = "扩张配置")
    private String extendCfg;

    @ApiModelProperty(name = "effeRange", value = "有效范围(0:全局,1:个人)")
    private Integer effeRange;

    @ApiModelProperty(name = "rangeId", value = "范围ID(见有效范围字段)")
    private String rangeId;

    @ApiModelProperty(name = "status", value = "可用状态(0:不可用,1:可用)")
    private Integer status;

    @ApiModelProperty(name = "remark1", value = "备注1")
    private Integer remark1;

    @ApiModelProperty(name = "remark2", value = "备注2")
    private String remark2;

    @ApiModelProperty(name = "remark3", value = "备注3")
    private String remark3;

    @ApiModelProperty(name = "iskey", value = "是否表格主键(0:否,1:是)")
    private Integer iskey;

    @ApiModelProperty(name = "isHide", value = "是否隐藏域(N:否,Y:是)")
    private String isHide;

    public String getPageCode() {
        return pageCode;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public String getDtCode() {
        return dtCode;
    }

    public void setDtCode(String dtCode) {
        this.dtCode = dtCode;
    }

    public String getSpcdCode() {
        return spcdCode;
    }

    public void setSpcdCode(String spcdCode) {
        this.spcdCode = spcdCode;
    }

    public String getColCode() {
        return colCode;
    }

    public void setColCode(String colCode) {
        this.colCode = colCode;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public Integer getDisPlayed() {
        return disPlayed;
    }

    public void setDisPlayed(Integer disPlayed) {
        this.disPlayed = disPlayed;
    }

    public Integer getFrozen() {
        return frozen;
    }

    public void setFrozen(Integer frozen) {
        this.frozen = frozen;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(Integer maxLen) {
        this.maxLen = maxLen;
    }

    public String getExtendCfg() {
        return extendCfg;
    }

    public void setExtendCfg(String extendCfg) {
        this.extendCfg = extendCfg;
    }

    public Integer getEffeRange() {
        return effeRange;
    }

    public void setEffeRange(Integer effeRange) {
        this.effeRange = effeRange;
    }

    public String getRangeId() {
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRemark1() {
        return remark1;
    }

    public void setRemark1(Integer remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public Integer getIskey() {
        return iskey;
    }

    public void setIskey(Integer iskey) {
        this.iskey = iskey;
    }

    public String getIsHide() {
        return isHide;
    }

    public void setIsHide(String isHide) {
        this.isHide = isHide;
    }
}

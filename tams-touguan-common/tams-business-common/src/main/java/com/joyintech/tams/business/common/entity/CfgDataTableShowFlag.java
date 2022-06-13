package com.joyintech.tams.business.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "数据表格设置标识")
public class CfgDataTableShowFlag {

    @ApiModelProperty(name = "pageCode", value = "页面编码")
    private String pageCode;

    @ApiModelProperty(name = "dtCode", value = "数据表格编号")
    private String dtCode;

    @ApiModelProperty(name = "pageName", value = "页面编码名")
    private String pageName;

    @ApiModelProperty(name = "dtname", value = "数据表格编号名")
    private String dtname;

    @ApiModelProperty(name = "effeRange", value = "有效范围(0:全局,1:个人)")
    private Integer effeRange;

    @ApiModelProperty(name = "rangeId", value = "范围ID(见有效范围字段)")
    private String rangeId;
    
    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private String updateTime;

    @ApiModelProperty(name = "spcdCode", value = "特殊条件编码")
    private String spcdCode;

    public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

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

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getDtname() {
        return dtname;
    }

    public void setDtname(String dtname) {
        this.dtname = dtname;
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

    public String getSpcdCode() {
        return spcdCode;
    }

    public void setSpcdCode(String spcdCode) {
        this.spcdCode = spcdCode;
    }
}

package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 头寸信息查询，请求参数
 */
@ApiModel(value="指令关闭发送头寸参数",description="指令关闭发送头寸参数")
public class CfetsEtrIbClosePosEtrRequest {

    @ApiModelProperty(name="busiType",value="业务类型",example="1")
    private String busiType;

    @ApiModelProperty(name="ps",value="交易方向",example="T01")
    private String ps;

    @ApiModelProperty(name="closeId",value="关闭编号",example="1")
    private String closeId;

    @ApiModelProperty(name="systemAct",value="系统动作",example="T01")
    private String systemAct;

    @ApiModelProperty(name="busiAct",value="业务动作",example="1")
    private String busiAct;

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public String getBusiAct() {
        return busiAct;
    }

    public void setBusiAct(String busiAct) {
        this.busiAct = busiAct;
    }

    public String getCloseId() {
        return closeId;
    }

    public void setCloseId(String closeId) {
        this.closeId = closeId;
    }
}

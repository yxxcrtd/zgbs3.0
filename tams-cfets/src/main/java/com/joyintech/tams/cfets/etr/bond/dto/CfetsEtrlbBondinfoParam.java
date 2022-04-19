package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="债券指令初始化参数",description="债券指令初始化参数")
public class CfetsEtrlbBondinfoParam {

	@ApiModelProperty(name="cmdsUrl",value="债券基本信息-行情信息",example="ws://ip:port/....")
	private String cmdsUrl;
	
	@ApiModelProperty(name="needWorkFlow",value="是否走工作流",example="true/false")
	private boolean needWorkFlow;

	public String getCmdsUrl() {
		return cmdsUrl;
	}

	public void setCmdsUrl(String cmdsUrl) {
		this.cmdsUrl = cmdsUrl;
	}

	public boolean isNeedWorkFlow() {
		return needWorkFlow;
	}

	public void setNeedWorkFlow(boolean needWorkFlow) {
		this.needWorkFlow = needWorkFlow;
	}
	
}

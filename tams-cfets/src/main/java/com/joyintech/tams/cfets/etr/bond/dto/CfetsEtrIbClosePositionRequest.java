package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 头寸信息查询，请求参数
 */
@ApiModel(value="指令批量关闭发送头寸请求参数",description="指令批量关闭发送头寸请求参数")
public class CfetsEtrIbClosePositionRequest {

    @ApiModelProperty(name="userId",value="用户名",example="1")
    private String userId;


    @ApiModelProperty(name="totalTradeId",value="汇总交易编号",example="1")
    private String totalTradeId;


    @ApiModelProperty(name="modelType",value="指令或交易标识",example="1")
    private String modelType;

    @ApiModelProperty(name="etrList",value="指令数据集",example="[字符串1,字符串2,...]")
    private List<CfetsEtrIbClosePosEtrRequest> data;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTotalTradeId() {
        return totalTradeId;
    }

    public void setTotalTradeId(String totalTradeId) {
        this.totalTradeId = totalTradeId;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public List<CfetsEtrIbClosePosEtrRequest> getData() {
        return data;
    }

    public void setData(List<CfetsEtrIbClosePosEtrRequest> data) {
        this.data = data;
    }
}

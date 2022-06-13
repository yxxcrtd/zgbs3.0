package com.joyintech.tams.business.asset.bond.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 操作记录返回参数DTO
 * @author wangyang1
 * @version 1.0 2022年4月14日
 * @see SearchInfoOperateListResponse
 * @since 1.0
 */
@ApiModel(value="操作记录返回数据",description="操作记录返回数据")
public class SearchInfoOperateListResponse {

    @ApiModelProperty(name="seqNo",value="序号",example="A1")
    private String seqNo;

    @ApiModelProperty(name="entrustId",value="资产代码",example="A1",required = true)
    private String entrustId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name="operDate",value="操作日期",example="2021-01-01")
    private String operDate;

    @ApiModelProperty(name="operTime",value="操作时间",example="2021-01-01")
    private String operTime;

    @ApiModelProperty(name="operType",value="操作类型",example="A1")
    private String operType;

    @ApiModelProperty(name="busiDes",value="业务说明",example="A1")
    private String busiDes;

    @ApiModelProperty(name="userId",value="操作员",example="A1")
    private String userId;

    public String getOperTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.操作类型, this.getOperType());
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getOperDate() {
        return operDate;
    }

    public void setOperDate(String operDate) {
        this.operDate = operDate;
    }

    public String getOperTime() {
        return operTime;
    }

    public void setOperTime(String operTime) {
        this.operTime = operTime;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getBusiDes() {
        return busiDes;
    }

    public void setBusiDes(String busiDes) {
        this.busiDes = busiDes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }
}

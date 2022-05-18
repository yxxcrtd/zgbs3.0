package com.joyintech.tams.cfets.etrdispatcher.dto;

import com.joyintech.framework.validation.ValidMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author: kchen
 * @Date: 2022/4/27 23:24
 */
@ApiModel(value="指令分发校验结果",description="指令分发校验结果")
public class CfetsEtrDispatcherResponse {

    @ApiModelProperty(name = "finprodId", value = "产品编码", example = "字符串1")
    private String finprodId;

    @ApiModelProperty(name="traderList",value="交易员列表",example="A1")
    private List<String> traderList;

    @ApiModelProperty(name="entrustIdList",value="指令编号",example="A1")
    private List<String> entrustIdList;

    @ApiModelProperty(name="readOnlyField",value="交易员字段是否可修改",example="Y/N")
    private String readOnlyField;

    @ApiModelProperty(name="bidCode",value="条件code",example="01 已分发不通过；02 首次分发 03 直接分发 04 更新已分发")
    private String bidCode;

    private List<ValidMessage> errorList;


    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public List<String> getTraderList() {
        return traderList;
    }

    public void setTraderList(List<String> traderList) {
        this.traderList = traderList;
    }

    public List<String> getEntrustIdList() {
        return entrustIdList;
    }

    public void setEntrustIdList(List<String> entrustIdList) {
        this.entrustIdList = entrustIdList;
    }

    public String getReadOnlyField() {
        return readOnlyField;
    }

    public void setReadOnlyField(String readOnlyField) {
        this.readOnlyField = readOnlyField;
    }

    public String getBidCode() {
        return bidCode;
    }

    public void setBidCode(String bidCode) {
        this.bidCode = bidCode;
    }

    public List<ValidMessage> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ValidMessage> errorList) {
        this.errorList = errorList;
    }
}

package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author jiangpeng
 * @date 2022年05月14日 16:45
 */
@ApiModel(value = "指令信息", description = "指令列表信息")
public class CfetsTrdFirstBidFirmEntrustDto {

    @ApiModelProperty(name = "entrustStatus", value = "指令状态", example = "04")
    private String entrustStatus;

    @ApiModelProperty(name = "entrustStatusStr", value = "指令状态名称,展示用", example = "已完成")
    private String entrustStatusStr;

    @ApiModelProperty(name = "entrustId", value = "指令代码", example = "WTBOND202204191858")
    private String entrustId;

    @ApiModelProperty(name = "finprodId", value = "资产代码,系统内代码一般不用于显示", example = "F01_aa.IB")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码,显示用代码", example = "aa.IB")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "债券aa")
    private String finprodAbbr;

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getEntrustStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.委托状态, getEntrustStatus());
    }

    public void setEntrustStatusStr(String entrustStatusStr) {
        this.entrustStatusStr = entrustStatusStr;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }
}

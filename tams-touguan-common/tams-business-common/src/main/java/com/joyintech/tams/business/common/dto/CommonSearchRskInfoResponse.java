package com.joyintech.tams.business.common.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 场外指令分发
 * 风险试算返回数据
 * @author sjj
 * @version 1.0 2021年10月26日
 * @see CommonSearchRskInfoResponse
 * @since 1.0
 */
@ApiModel(value="风险试算返回数据",description="风险试算返回数据")
public class CommonSearchRskInfoResponse {

    @ApiModelProperty(name="supStat",value="状态",example="违规")
    private String supStat;

    @ApiModelProperty(name="indName",value="指标名称",example="字符串1")
    private String indName;

    @ApiModelProperty(name="indType",value="指标类型",example="01")
    private String indType;

    @ApiModelProperty(name="portfolioName",value="组合（包）名称",example="字符串1")
    private String portfolioName;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="字符串1")
    private String finprodAbbr;

    @ApiModelProperty(name="warnCond",value="预警阈值",example="字符串1")
    private String warnCond;

    @ApiModelProperty(name="approveCond",value="审批线",example="字符串1")
    private String approveCond;

    @ApiModelProperty(name="foulCond",value="违规阈值",example="字符串1")
    private String foulCond;

    @ApiModelProperty(name="supVal",value="当前实际值",example="01")
    private String supVal;

    @ApiModelProperty(name="oldSupVal",value="原值",example="字符串1")
    private String oldSupVal;

    @ApiModelProperty(name="supTrend",value="趋势",example="字符串1")
    private String supTrend;

    public String getSupStat() {
        return supStat;
    }

    public void setSupStat(String supStat) {
        this.supStat = supStat;
    }

    public String getIndName() {
        return indName;
    }

    public void setIndName(String indName) {
        this.indName = indName;
    }

    public String getIndType() {
        return indType;
    }

    public void setIndType(String indType) {
        this.indType = indType;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getWarnCond() {
        return warnCond;
    }

    public void setWarnCond(String warnCond) {
        this.warnCond = warnCond;
    }

    public String getApproveCond() {
        return approveCond;
    }

    public void setApproveCond(String approveCond) {
        this.approveCond = approveCond;
    }

    public String getFoulCond() {
        return foulCond;
    }

    public void setFoulCond(String foulCond) {
        this.foulCond = foulCond;
    }

    public String getSupVal() {
        return supVal;
    }

    public void setSupVal(String supVal) {
        this.supVal = supVal;
    }

    public String getOldSupVal() {
        return oldSupVal;
    }

    public void setOldSupVal(String oldSupVal) {
        this.oldSupVal = oldSupVal;
    }

    public String getSupTrend() {
        return supTrend;
    }

    public void setSupTrend(String supTrend) {
        this.supTrend = supTrend;
    }

    /**
     * 将监控状态对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getOtherSupStat(){
//        return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.监督状态, this.getSupStat());
        if(StringUtil.isNotEmpty(this.getSupStat())){
            if(StringUtil.equals(this.getSupStat(),"0")){
                return "正常";
            }else if(StringUtil.equals(this.getSupStat(),"1")){
                return "预警";
            }else  if(StringUtil.equals(this.getSupStat(),"2")){
                return "审批";
            }else  if(StringUtil.equals(this.getSupStat(),"3")){
                return "违规";
            }else  if(StringUtil.equals(this.getSupStat(),"4")){
                return "异常";
            }
        }
        return null;
    }

    public String getOtherIndType() {
        return SysDataUtil.getNameFromDataDict(DictCode.指标类型, this.getIndType());
    }
}

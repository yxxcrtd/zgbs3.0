package com.joyintech.tams.invest.asset.newtenderbond.dto;

import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.util.SysDataUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 评级信息
 */
@ApiModel(value = "MstBondCreditRatingDto", description = "新债标的评级信息")
public class MstBondCreditRatingDto {

    @ApiModelProperty(name = "oType", value = "评级类型，主体评级、债项评级")
    private String oType;

    @ApiModelProperty(name = "oTypeValue", value = "评级类型字典值")
    private String oTypeValue;

    @ApiModelProperty(name = "shortLongTerm", value = "长短期")
    private String shortLongTerm;

    @ApiModelProperty(name = "shortLongTermValue", value = "长短期字典值")
    private String shortLongTermValue;

    @ApiModelProperty(name = "gradeDate", value = "评级日期")
    private Date gradeDate;

    @ApiModelProperty(name = "gradeResult", value = "评级结果")
    private String gradeResult;

    @ApiModelProperty(name = "gradeOrg", value = "评级机构")
    private String gradeOrg;


    public String getoType() {
        return oType;
    }

    public void setoType(String oType) {
        this.oType = oType;
    }

    public String getShortLongTerm() {
        return shortLongTerm;
    }

    public void setShortLongTerm(String shortLongTerm) {
        this.shortLongTerm = shortLongTerm;
    }

    public Date getGradeDate() {
        return gradeDate;
    }

    public void setGradeDate(Date gradeDate) {
        this.gradeDate = gradeDate;
    }

    public String getGradeResult() {
        return gradeResult;
    }

    public void setGradeResult(String gradeResult) {
        this.gradeResult = gradeResult;
    }

    public String getGradeOrg() {
        return gradeOrg;
    }

    public void setGradeOrg(String gradeOrg) {
        this.gradeOrg = gradeOrg;
    }

    public String getoTypeValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级类型,this.oType);
    }

    public void setoTypeValue(String oTypeValue) {
        this.oTypeValue = oTypeValue;
    }

    public String getShortLongTermValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.长期短期,this.shortLongTerm);
    }

    public void setShortLongTermValue(String shortLongTermValue) {
        this.shortLongTermValue = shortLongTermValue;
    }
}

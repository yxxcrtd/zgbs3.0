package com.joyintech.service.basicdata.marketdata.externaloptions.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * @author 姚轶文
 * @create 2020- 01-06 9:42
 */
public class ComExternalOptions001Dto extends BaseEntity {

    private String finprodId; //金融产品代码

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String targetId;
    public String targetName;
    public String targetType;
    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    /**
     * null
     */
    private String finprodId2;

    /**
     * null
     */
    private String valDate;

    /**
     * 处理操作
     */
    private String operateType;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String pageModel;

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }
    /**
     * 数据来源
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_2)
    @NotBlank(message = "数据来源不能为空!", groups = ValidateGroupEdit.class)
    private String datasourceType;


    /**
     * 取得 数据来源
     * @return 数据来源
     */
    public String getDatasourceType() {
        return datasourceType;
    }

    /**
     * 设置 数据来源
     * @param datasourceType 数据来源
     */
    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType == null ? null : datasourceType.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getFinprodId2() {
        return finprodId2;
    }

    /**
     * 设置 null
     * @param finprodId2 null
     */
    public void setFinprodId2(String finprodId2) {
        this.finprodId2 = finprodId2 == null ? null : finprodId2.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getValDate() {
        return valDate;
    }

    /**
     * 设置 null
     * @param valDate null
     */
    public void setValDate(String valDate) {
        this.valDate = valDate;
    }

    /**
     * null
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal valuation;

    /**
     * null
     */
    private String valmodelId;

    /**
     * null
     */
    private String ccy;

    /**
     * null
     */
    private String inputType;

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getValuation() {
        return valuation;
    }

    /**
     * 设置 null
     * @param valuation null
     */
    public void setValuation(BigDecimal valuation) {
        this.valuation = valuation;
    }

    /**
     * 取得 null
     * @return null
     */
    public String getValmodelId() {
        return valmodelId;
    }

    /**
     * 设置 null
     * @param valmodelId null
     */
    public void setValmodelId(String valmodelId) {
        this.valmodelId = valmodelId == null ? null : valmodelId.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 null
     * @param ccy null
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getInputType() {
        return inputType;
    }

    /**
     * 设置 null
     * @param inputType null
     */
    public void setInputType(String inputType) {
        this.inputType = inputType == null ? null : inputType.trim();
    }

    private String publishDate;
    /**
     * 取得 null
     * @return null
     */
    public String getPublishDate() {
        return publishDate;
    }

    /**
     * 设置 null
     * @param publishDate null
     */
    // @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}

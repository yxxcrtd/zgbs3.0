/*
 * 文件名：SppiTemplatePwcDto
 * 版权：Copyright 2017-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020-10-09
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.asset.sppi.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.SppiTest;
import com.joyintech.tams.common.base.entity.SppiTestQa;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.asset.sppi.valid.SppiTestValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SPPI测试详细信息DTO
 *
 * @author Jiaoxujin
 * @version 1.0 2020-10-09
 */
//@PageId("SppiTest002")
@ValidLogic(targetClass = SppiTestValidator.class, groups = {ValidateGroupEdit.class})
public class SppiTemplateDto extends SppiTest implements ProcessEntity {

    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 资产名称
     */
    private String finprodName;

    /**
     * 资产类型<br />
     * 债券类资产、项目类资产、计划类利率型资产
     */
    private String assetType;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 页面模式（查看、新建、编辑）
     */
    private String pageModel;

    /**
     * 要素id
     */
    private String elementId;

    /**
     * 问题及答案列表
     */
    private List<SppiTestQa> qaList;

    /**
     * 问题输入框类型集合
     */
    private Map<String, String> inputAttrMap;

    /**
     * 问题值集合
     */
    private Map<String, String> inputValueMap;

    /**
     * 必填项问题列表，值为form表单中的input的name<br />
     * 验证check用
     */
    private List<String> requiredQaList;

    /**
     * 禁用的问题(ID)列表<br />
     *  仅驳回修改初始化时采用
     */
    private List<String> disabledFieldList;

    public SppiTemplateDto() {
    }

    @Override
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    public String getSppiTestId() {
        return super.getSppiTestId();
    }

    @Override
    @NotEmpty(fieldName = "SppiTest002.finprodId", groups = {ValidateGroupEdit.class})
    public String getFinprodId() {
        return super.getFinprodId();
    }

    @NotEmpty(fieldName = "SppiTest002.finprodName", groups = {ValidateGroupEdit.class})
    public String getFinprodName() {
        return finprodName;
    }

    @NotEmpty(fieldName = "SppiTest002.bdAssetType", groups = {ValidateGroupEdit.class})
    public String getAssetType() {
        return assetType;
    }

    @Override
    @Length(fieldName = "SppiTest002.debtToolDesc", max = 400, groups = {ValidateGroupEdit.class})
    public String getDebtToolDesc() {
        return super.getDebtToolDesc();
    }

    @Override
    @Length(fieldName = "SppiTest002.debtLayerDesc", max = 400, groups = {ValidateGroupEdit.class})
    public String getDebtLayerDesc() {
        return super.getDebtLayerDesc();
    }

    @Override
    @Length(fieldName = "SppiTest002.investType", max = 100, groups = {ValidateGroupEdit.class})
    public String getInvestTypeDesc() {
        return super.getInvestTypeDesc();
    }

    @Override
    @Length(fieldName = "SppiTest002.tradeManageType", max = 100, groups = {ValidateGroupEdit.class})
    public String getManageTypeDesc() {
        return super.getManageTypeDesc();
    }

    @Override
    @Length(fieldName = "SysCommon.remark", max = 400, groups = {ValidateGroupEdit.class})
    public String getRemark() {
        return super.getRemark();
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetTypeName() {
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, this.getAssetType());
    }

    /**
     * 问题及答案集合，KEY为QUESTION_NO，对应模板界面HTML上的FORM项的name
     */
    public Map<String, SppiTestQa> getQaMap() {
        if (CollectionUtils.isNotEmpty(this.qaList)) {
            return this.qaList.stream().collect(Collectors.toMap(SppiTestQa::getQuestionNo, q -> q));
        }
        return new HashMap<>();
    }

    public List<SppiTestQa> getQaList() {
        return qaList;
    }

    public void setQaList(List<SppiTestQa> qaList) {
        this.qaList = qaList;
    }

    public List<String> getRequiredQaList() {
        return requiredQaList;
    }

    public void setRequiredQaList(List<String> requiredQaList) {
        this.requiredQaList = requiredQaList;
    }

    public Map<String, String> getInputAttrMap() {
        return inputAttrMap;
    }

    public void setInputAttrMap(Map<String, String> inputAttrMap) {
        this.inputAttrMap = inputAttrMap;
    }

    public Map<String, String> getInputValueMap() {
        return inputValueMap;
    }

    public void setInputValueMap(Map<String, String> inputValueMap) {
        this.inputValueMap = inputValueMap;
    }

    public List<String> getDisabledFieldList() {
        return disabledFieldList;
    }

    public void setDisabledFieldList(List<String> disabledFieldList) {
        this.disabledFieldList = disabledFieldList;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }
}

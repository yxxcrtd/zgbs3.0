/*
 * 文件名：SppiTemplatePwcDto
 * 版权：Copyright 2017-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020-10-09
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.asset.sppi.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.SppiTest;
import com.joyintech.tams.common.base.entity.SppiTestQa;
import com.joyintech.tams.constant.DictCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SPPI测试详细信息DTO
 *
 * @author Jiaoxujin
 * @version 1.0 2020-10-09
 */
public class SppiTemplateDto extends SppiTest {
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

    public String getFinprodName() {
        return finprodName;
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

    public String getAssetType() {
        return assetType;
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
}

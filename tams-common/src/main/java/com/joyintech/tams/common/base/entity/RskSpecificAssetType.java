package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

public class RskSpecificAssetType extends BaseEntity {
    /**
     * 特殊资产类型ID
     */
    private String specificId;

    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 一级分类
     */
    private String type1;

    /**
     * 二级分类
     */
    private String type2;

    /**
     * 三级分类
     */
    private String type3;

    /**
     * 业务类型
     */
    private String type4;

    /**
     * 资产代码
     */
    private String finprodId;

    /**
     * 是否不良资产/收受益权
     */
    private String isBadasset;

    /**
     * 是否未上市股权/收受益权
     */
    private String isUnlistedequity;

    /**
     * 是否国务院银行业监督管理机构另有规定
     */
    private String isRegulation;

    /**
     * 是否信贷资产及其收受益权
     */
    private String isCreditAsset;

    /**
     * 是否流动性受限
     */
    private String isFlowLimited;

    /**
     * 是否无活跃交易市场且需要采用估值技术确定公允价值资产
     */
    private String isLiquidity1;

    /**
     * 是否不具备活跃交易市场或者在活跃市场中无报价
     */
    private String isLiquidity2;

    /**
     * 是否7个工作日可变现资产
     */
    private String isLiquidity3;

    /**
     * 取得 特殊资产类型ID
     * @return 特殊资产类型ID
     */
    public String getSpecificId() {
        return specificId;
    }

    /**
     * 设置 特殊资产类型ID
     * @param specificId 特殊资产类型ID
     */
    public void setSpecificId(String specificId) {
        this.specificId = specificId == null ? null : specificId.trim();
    }

    /**
     * 取得 分支序号
     * @return 分支序号
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 分支序号
     * @param branch 分支序号
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 取得 一级分类
     * @return 一级分类
     */
    public String getType1() {
        return type1;
    }

    /**
     * 设置 一级分类
     * @param type1 一级分类
     */
    public void setType1(String type1) {
        this.type1 = type1 == null ? null : type1.trim();
    }

    /**
     * 取得 二级分类
     * @return 二级分类
     */
    public String getType2() {
        return type2;
    }

    /**
     * 设置 二级分类
     * @param type2 二级分类
     */
    public void setType2(String type2) {
        this.type2 = type2 == null ? null : type2.trim();
    }

    /**
     * 取得 三级分类
     * @return 三级分类
     */
    public String getType3() {
        return type3;
    }

    /**
     * 设置 三级分类
     * @param type3 三级分类
     */
    public void setType3(String type3) {
        this.type3 = type3 == null ? null : type3.trim();
    }

    /**
     * 取得 业务类型
     * @return 业务类型
     */
    public String getType4() {
        return type4;
    }

    /**
     * 设置 业务类型
     * @param type4 业务类型
     */
    public void setType4(String type4) {
        this.type4 = type4 == null ? null : type4.trim();
    }

    /**
     * 取得 资产代码
     * @return 资产代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 资产代码
     * @param finprodId 资产代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 是否不良资产/收受益权
     * @return 是否不良资产/收受益权
     */
    public String getIsBadasset() {
        return isBadasset;
    }

    /**
     * 设置 是否不良资产/收受益权
     * @param isBadasset 是否不良资产/收受益权
     */
    public void setIsBadasset(String isBadasset) {
        this.isBadasset = isBadasset == null ? null : isBadasset.trim();
    }

    /**
     * 取得 是否未上市股权/收受益权
     * @return 是否未上市股权/收受益权
     */
    public String getIsUnlistedequity() {
        return isUnlistedequity;
    }

    /**
     * 设置 是否未上市股权/收受益权
     * @param isUnlistedequity 是否未上市股权/收受益权
     */
    public void setIsUnlistedequity(String isUnlistedequity) {
        this.isUnlistedequity = isUnlistedequity == null ? null : isUnlistedequity.trim();
    }

    /**
     * 取得 是否国务院银行业监督管理机构另有规定
     * @return 是否国务院银行业监督管理机构另有规定
     */
    public String getIsRegulation() {
        return isRegulation;
    }

    /**
     * 设置 是否国务院银行业监督管理机构另有规定
     * @param isRegulation 是否国务院银行业监督管理机构另有规定
     */
    public void setIsRegulation(String isRegulation) {
        this.isRegulation = isRegulation == null ? null : isRegulation.trim();
    }

    /**
     * 取得 是否信贷资产及其收受益权
     * @return 是否信贷资产及其收受益权
     */
    public String getIsCreditAsset() {
        return isCreditAsset;
    }

    /**
     * 设置 是否信贷资产及其收受益权
     * @param isCreditAsset 是否信贷资产及其收受益权
     */
    public void setIsCreditAsset(String isCreditAsset) {
        this.isCreditAsset = isCreditAsset == null ? null : isCreditAsset.trim();
    }

    /**
     * 取得 是否流动性受限
     * @return 是否流动性受限
     */
    public String getIsFlowLimited() {
        return isFlowLimited;
    }

    /**
     * 设置 是否流动性受限
     * @param isFlowLimited 是否流动性受限
     */
    public void setIsFlowLimited(String isFlowLimited) {
        this.isFlowLimited = isFlowLimited == null ? null : isFlowLimited.trim();
    }

    public String getIsLiquidity1() {
        return isLiquidity1;
    }

    public void setIsLiquidity1(String isLiquidity1) {
        this.isLiquidity1 = isLiquidity1;
    }

    public String getIsLiquidity2() {
        return isLiquidity2;
    }

    public void setIsLiquidity2(String isLiquidity2) {
        this.isLiquidity2 = isLiquidity2;
    }

    public String getIsLiquidity3() {
        return isLiquidity3;
    }

    public void setIsLiquidity3(String isLiquidity3) {
        this.isLiquidity3 = isLiquidity3;
    }
}
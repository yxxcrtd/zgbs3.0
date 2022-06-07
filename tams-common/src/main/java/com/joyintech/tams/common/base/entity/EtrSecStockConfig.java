package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

public class EtrSecStockConfig extends BaseEntity {
    /**
     * 设置编号
     */
    private String configId;

    /**
     * 质押库编号
     */
    private String pledgeStockId;

    /**
     * 资产一级分类
     */
    private String windtypeone;

    /**
     * 资产二级分类
     */
    private String windtypetwo;

    /**
     * 资产三级分类
     */
    private String windtypethree;

    /**
     * 主体评级
     */
    private String mGradeResult;

    /**
     * 债项评级
     */
    private String secGradeResult;

    /**
     * 是否次级债
     */
    private String isSecBond;

    /**
     * 是否含权债
     */
    private String isRight;

    /**
     * 是否私募债
     */
    private String isPpb;

    /**
     * 取得 设置编号
     * @return 设置编号
     */
    public String getConfigId() {
        return configId;
    }

    /**
     * 设置 设置编号
     * @param configId 设置编号
     */
    public void setConfigId(String configId) {
        this.configId = configId == null ? null : configId.trim();
    }

    /**
     * 取得 质押库编号
     * @return 质押库编号
     */
    public String getPledgeStockId() {
        return pledgeStockId;
    }

    /**
     * 设置 质押库编号
     * @param pledgeStockId 质押库编号
     */
    public void setPledgeStockId(String pledgeStockId) {
        this.pledgeStockId = pledgeStockId == null ? null : pledgeStockId.trim();
    }

    /**
     * 取得 资产一级分类
     * @return 资产一级分类
     */
    public String getWindtypeone() {
        return windtypeone;
    }

    /**
     * 设置 资产一级分类
     * @param windtypeone 资产一级分类
     */
    public void setWindtypeone(String windtypeone) {
        this.windtypeone = windtypeone == null ? null : windtypeone.trim();
    }

    /**
     * 取得 资产二级分类
     * @return 资产二级分类
     */
    public String getWindtypetwo() {
        return windtypetwo;
    }

    /**
     * 设置 资产二级分类
     * @param windtypetwo 资产二级分类
     */
    public void setWindtypetwo(String windtypetwo) {
        this.windtypetwo = windtypetwo == null ? null : windtypetwo.trim();
    }

    public String getWindtypethree() {
        return windtypethree;
    }

    public void setWindtypethree(String windtypethree) {
        this.windtypethree = windtypethree;
    }

    /**
     * 取得 主体评级
     * @return 主体评级
     */
    public String getmGradeResult() {
        return mGradeResult;
    }

    /**
     * 设置 主体评级
     * @param mGradeResult 主体评级
     */
    public void setmGradeResult(String mGradeResult) {
        this.mGradeResult = mGradeResult == null ? null : mGradeResult.trim();
    }

    /**
     * 取得 债项评级
     * @return 债项评级
     */
    public String getSecGradeResult() {
        return secGradeResult;
    }

    /**
     * 设置 债项评级
     * @param secGradeResult 债项评级
     */
    public void setSecGradeResult(String secGradeResult) {
        this.secGradeResult = secGradeResult == null ? null : secGradeResult.trim();
    }

    /**
     * 取得 是否次级债
     * @return 是否次级债
     */
    public String getIsSecBond() {
        return isSecBond;
    }

    /**
     * 设置 是否次级债
     * @param isSecBond 是否次级债
     */
    public void setIsSecBond(String isSecBond) {
        this.isSecBond = isSecBond == null ? null : isSecBond.trim();
    }

    /**
     * 取得 是否含权债
     * @return 是否含权债
     */
    public String getIsRight() {
        return isRight;
    }

    /**
     * 设置 是否含权债
     * @param isRight 是否含权债
     */
    public void setIsRight(String isRight) {
        this.isRight = isRight == null ? null : isRight.trim();
    }

    /**
     * 取得 是否私募债
     * @return 是否私募债
     */
    public String getIsPpb() {
        return isPpb;
    }

    /**
     * 设置 是否私募债
     * @param isPpb 是否私募债
     */
    public void setIsPpb(String isPpb) {
        this.isPpb = isPpb == null ? null : isPpb.trim();
    }
}
package com.joyintech.tams.touguan.system.bond.special.dto;

import com.joyintech.framework.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;


/**
 * 特殊债券信息导入DTO
 *
 * @author zhuys
 * @date 2022-05-06
 */
public class FinProductSpecialImportDto extends BaseEntity {

    /**
     * 序号
     */
    private String serialNumber;

    /**
     * 债券代码
     */
    private String finprodMarketId;

    /**
     * 债券简称
     */
    private String finprodAbbr;

    /**
     * 是否资产支持证券
     */
    private String isAbs;

    /**
     * 是否特殊债
     */
    private String isSpecialDebt;

    /**
     * 是否到期违约债
     */
    private String isDefaultBondIndicator;

    /**
     * 特殊债标识录入方式
     */
    private String inputType;

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getIsAbs() {
        return isAbs;
    }

    public void setIsAbs(String isAbs) {
        this.isAbs = isAbs;
    }

    public String getIsSpecialDebt() {
        return isSpecialDebt;
    }

    public void setIsSpecialDebt(String isSpecialDebt) {
        this.isSpecialDebt = isSpecialDebt;
    }

    public String getIsDefaultBondIndicator() {
        return isDefaultBondIndicator;
    }

    public void setIsDefaultBondIndicator(String isDefaultBondIndicator) {
        this.isDefaultBondIndicator = isDefaultBondIndicator;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }
}
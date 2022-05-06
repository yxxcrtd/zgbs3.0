/*
 * 文件名：CfetsPortfolioAcctRelaDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yufei
 * 修改时间：2020年5月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.basic.cfetsportfolioacctrela.dto;

import java.util.List;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.tams.common.base.entity.IbuPortfolioAcctRela;
import com.joyintech.tams.invest.bankuplink.basic.cfetsportfolioacctrela.validator.CfetsPortfolioAcctRela001DtoValidator;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * CFETS账户关联 〈功能详细描述〉
 * 
 * @author yufei
 * @version 1.0 2020年5月12日
 * @see CfetsPortfolioAcctRela001Dto
 * @since 1.0
 */
@PageId("CfetsPortfolioAcctRela002")
@ValidLogic(targetClass = CfetsPortfolioAcctRela001DtoValidator.class)
public class CfetsPortfolioAcctRela001Dto extends IbuPortfolioAcctRela implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -9053851674075921923L;

    /**
     * 机构21位码
     */
    private String partyCustomerId;

    /**
     * 机构全称
     */
    private String partyName;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 元素Id
     */
    private String elementId;

    /**
     * 机构21位码旧值
     */
    private String partyIdOld;

    /**
     * 账户来源旧值
     */
    private String cfetsAcctTypeOld;

    /**
     * 组合代码旧值
     */
    private String portfolioIdOld;

    /**
     * 选中的数据
     */
    private transient List<CfetsPortfolioAcctRela001Dto> selectedItem;

    /**
     * 获取 selectedItem
     * 
     * @return selectedItem.
     */
    public List<CfetsPortfolioAcctRela001Dto> getSelectedItem() {
        return selectedItem;
    }

    /**
     * 设置 selectedItem
     * 
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<CfetsPortfolioAcctRela001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * 获取 cfetsAcctTypeStr
     * 
     * @return cfetsAcctTypeStr.
     */
    public String getCfetsAcctTypeStr() {
        if (this.getCfetsAcctType() != null) {
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.账户来源,
                this.getCfetsAcctType());
        }
        else {
            return null;
        }
    }

    /**
     * 获取 elementId
     * 
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * 设置 elementId
     * 
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 获取 operateType
     * 
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置 operateType
     * 
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 获取 partyCustomerId
     * 
     * @return partyCustomerId.
     */
    @NotEmpty(fieldName = "CfetsPortfolioAcctRela002.partyCustomerId")
    public String getPartyCustomerId() {
        return partyCustomerId;
    }

    /**
     * 设置 partyCustomerId
     * 
     * @param partyCustomerId partyCustomerId
     */
    public void setPartyCustomerId(String partyCustomerId) {
        this.partyCustomerId = partyCustomerId;
    }

    /**
     * 获取 partyName
     * 
     * @return partyName.
     */
    @NotEmpty(fieldName = "CfetsPortfolioAcctRela001.partyName")
    public String getPartyName() {
        return partyName;
    }

    /**
     * 设置 partyName
     * 
     * @param partyName partyName
     */
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    /**
     * 获取 portfolioName
     * 
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     * 
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsPortfolioAcctRela001.portfolioId")
    public String getPortfolioId() {
        return super.getPortfolioId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsPortfolioAcctRela002.partyId")
    public String getPartyId() {
        return super.getPartyId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsPortfolioAcctRela001.cfetsAcctType")
    public String getCfetsAcctType() {
        return super.getCfetsAcctType();
    }

    /**
     * 获取 partyIdOld
     * 
     * @return partyIdOld.
     */
    @PrimaryKey(value = Field.PK_2)
    public String getPartyIdOld() {
        return partyIdOld;
    }

    /**
     * 设置 partyIdOld
     * 
     * @param partyIdOld partyIdOld
     */
    public void setPartyIdOld(String partyIdOld) {
        this.partyIdOld = partyIdOld;
    }

    /**
     * 获取 cfetsAcctTypeOld
     * 
     * @return cfetsAcctTypeOld.
     */
    @PrimaryKey(value = Field.PK_1)
    public String getCfetsAcctTypeOld() {
        return cfetsAcctTypeOld;
    }

    /**
     * 设置 cfetsAcctTypeOld
     * 
     * @param cfetsAcctTypeOld cfetsAcctTypeOld
     */
    public void setCfetsAcctTypeOld(String cfetsAcctTypeOld) {
        this.cfetsAcctTypeOld = cfetsAcctTypeOld;
    }

    /**
     * 获取 portfolioIdOld
     * 
     * @return portfolioIdOld.
     */
    // TISHKS-1003:去除组合代码旧值的PK3注解 modify by fuyongsheng 20220505 start
    // @PrimaryKey(value = Field.PK_3)
    // TISHKS-1003:去除组合代码旧值的PK3注解 modify by fuyongsheng 20220505 end
    public String getPortfolioIdOld() {
        return portfolioIdOld;
    }

    /**
     * 设置 portfolioIdOld
     * 
     * @param portfolioIdOld portfolioIdOld
     */
    public void setPortfolioIdOld(String portfolioIdOld) {
        this.portfolioIdOld = portfolioIdOld;
    }

}

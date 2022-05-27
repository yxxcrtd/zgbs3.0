package com.joyintech.tams.invest.basicdata.standardcouponreconversionrate.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.StdBondMoreConvrateInfo;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.basicdata.standardcouponreconversionrate.validator.StandardCouponReConversionRateValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.util.Date;
import java.util.List;


@PageId("StandardCouponReConversionRate002")
@ValidLogic(targetClass = StandardCouponReConversionRateValidator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
public class StandardCouponReConversionRateDto extends StdBondMoreConvrateInfo implements ITableRow,ProcessEntity {


    private transient List<StandardCouponReConversionRateDto> selectedItem;
    private String operateType;

    private String elementId;

    private String brokerName; //券商


    private String accountTypeStr;//资金账户

    private String finprodIdStr;//资产代码

    private String accountType ; //资金账户

    private String tandardRate;//标准券再折算率

    private String acctNoStr;

    public String getAcctNoStr() {
        return acctNoStr;
    }

    public void setAcctNoStr(String acctNoStr) {
        this.acctNoStr = acctNoStr;
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

    public List<StandardCouponReConversionRateDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<StandardCouponReConversionRateDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getAccountTypeStr() {
        if(StringUtil.equals(this.accountType,"BROKER")){
            return "所有";
        }else{
            return this.accountTypeStr;
        }
    }

    public void setAccountTypeStr(String accountTypeStr) {
        this.accountTypeStr = accountTypeStr;
    }

    public void setFinprodIdStr(String finprodIdStr) {
        this.finprodIdStr = finprodIdStr;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @NotEmpty(fieldName = "StandardCouponReConversionRate002.tandardRate",groups = {ValidateGroupEdit.class})
    public String getTandardRate() {
        return tandardRate;
    }

    public void setTandardRate(String tandardRate) {
        this.tandardRate = tandardRate;
    }


    private String brokerId;

    /**
     * 资产代码
     */
    private String finprodId;


    @Override
    @PrimaryKey(PrimaryKey.Field.PK_1)
    @NotEmpty(fieldName = "StandardCouponReConversionRate001.broker",groups = {ValidateGroupEdit.class})
    public String getBrokerId() {
        return brokerId;
    }

    @Override
    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    @Override
    @PrimaryKey(PrimaryKey.Field.PK_2)
    public String getFinprodId() {
        return finprodId;
    }

    @Override
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 金融产品简称
     */
    private String finprodAbbr;

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getFinprodIdStr() {
        if(StringUtil.equals(this.finprodId,"BROKER")){
            return "所有";
        }else{
            return this.finprodId;
        }
    }
    /**
     * 开始日期
     */
    private String startDate;

    @Override
    @PrimaryKey(PrimaryKey.Field.PK_3)
    @NotEmpty(fieldName = "StandardCouponReConversionRate001.startDate",groups = {ValidateGroupEdit.class})
    public String getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    private String finprodMarketId;

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }


    public String getFinprodMarketIdStr() {
        if(StringUtil.equals(getFinprodId(),"BROKER")){
            return "所有";
        }else{
            return this.getFinprodMarketId();
        }
    }
}

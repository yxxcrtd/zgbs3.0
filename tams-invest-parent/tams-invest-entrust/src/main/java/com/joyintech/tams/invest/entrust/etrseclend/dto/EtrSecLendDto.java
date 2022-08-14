package com.joyintech.tams.invest.entrust.etrseclend.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Amount;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etrseclend.validator.EtrSecLendValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
/**
 * 债券借贷投资委托dto
 * 〈功能详细描述〉
 * @author yjm
 * @version 1.0 2019年6月4日
 * @see com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto
 * @since 1.0
 */
@PageId("EtrSecLend002")
@ValidLogic(targetClass = EtrSecLendValidator.class , groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class EtrSecLendDto extends EtrEntrust implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 982173889603979235L;

    /**
     * 标的债券代码
     */
    private String targetFinprodId;
    /**
     * 标的债券名称
     */
    private String targetFinprodName;
    /**
     * 借贷方向
     */
    private String busiPs;
    /**
     * 证券托管户
     */
    private String secAcctId;
    /**
     * 期限类型
     */
    private String termType;
    /**
     * 清算速度
     */
    private String settleSpeed;
    /**
     * 交易日历
     */
    private String calendarId;
    /**
     * 借贷费率
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal busiRate;
    /**
     * 标的债券可以持仓
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal objectAvailablePosition;

    /**
     * 券面总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal totalAmt;
    /**
     * 文件上传dto
     */
    private List<SysUploadFileInfo> fileArray;

    /**
     * 投资人员信息DTO
     */
    @Valid
    private BussEtrInvestPersonInfoDto etrInvestPersonInfo;

    /**
     * 指定交易员
     */
    private String trader;

    /**
     * 委托关闭方式
     */
    private String CloseWay;

    /**
     * 选中的数据
     */
    private transient List<EtrSecLendDto> selectedItem;
    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 组合名称
     */
    private String portfolioName;
    /**
     * 添加质押券信息
     */
    private BussRepoSecDto bussRepoSec;

    /**
     * 完成额度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal finishAmt;

    /**
     * 交易完成度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal transactioncom;

    /**
     * 按钮id
     */
    private String elementId;


    /**
     * 投资处室
     */
    private String room;
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;
    /**
     * 交割日期
     */
    private Date settleDate;

    private String decisionAct;//决策动作

    private String systemAct;//系统动作

    private String balanceCheck;//是否校验余额

    public String getDecisionAct() {
        return decisionAct;
    }

    public void setDecisionAct(String decisionAct) {
        this.decisionAct = decisionAct;
    }

    /**
     * 获取 settleDate
     * @return settleDate.
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 settleDate
     * @param settleDate settleDate
     */
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 获取 riskTrialRetDto
     * @return riskTrialRetDto.
     */
    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }



    /**
     * 设置 riskTrialRetDto
     * @param riskTrialRetDto riskTrialRetDto
     */
    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    /**
     * 获取 fileArray
     * @return fileArray.
     */
    public List<SysUploadFileInfo> getFileArray() {
        return fileArray;
    }


    /**
     * 设置 fileArray
     * @param fileArray fileArray
     */
    public void setFileArray(List<SysUploadFileInfo> fileArray) {
        this.fileArray = fileArray;
    }
    /**
     * 获取 etrInvestPersonInfo
     * @return etrInvestPersonInfo.
     */
    public BussEtrInvestPersonInfoDto getEtrInvestPersonInfo() {
        return etrInvestPersonInfo;
    }



    /**
     * 设置 etrInvestPersonInfo
     * @param etrInvestPersonInfo etrInvestPersonInfo
     */
    public void setEtrInvestPersonInfo(BussEtrInvestPersonInfoDto etrInvestPersonInfo) {
        this.etrInvestPersonInfo = etrInvestPersonInfo;
    }




    /**
     * 获取 trader
     * @return trader.
     */
    public String getTrader() {
        return trader;
    }




    /**
     * 设置 trader
     * @param trader trader
     */
    public void setTrader(String trader) {
        this.trader = trader;
    }




    /**
     * 获取 closeWay
     * @return closeWay.
     */
    public String getCloseWay() {
        return CloseWay;
    }




    /**
     * 设置 closeWay
     * @param closeWay closeWay
     */
    public void setCloseWay(String closeWay) {
        CloseWay = closeWay;
    }

    /**
     * 获取 room
     * @return room.
     */
    public String getRoom() {
        return room;
    }



    /**
     * 设置 room
     * @param room room
     */
    public void setRoom(String room) {
        this.room = room;
    }


    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<EtrSecLendDto> getSelectedItem() {
        return selectedItem;
    }


    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto> selectedItem) {
        this.selectedItem = selectedItem;
    }


    /**
     * 获取 operateType
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }



    /**
     * 设置 operateType
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }


    /**
     * 获取 elementId
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }



    /**
     * 设置 elementId
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 获取 portfolioName
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 finishAmt
     * @return finishAmt.
     */
    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    /**
     * 设置 finishAmt
     * @param finishAmt finishAmt
     */
    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    /**
     * 获取 transactioncom
     * @return transactioncom.
     */
    public BigDecimal getTransactioncom() {
        return transactioncom;
    }


    /**
     * 设置 transactioncom
     * @param transactioncom transactioncom
     */
    public void setTransactioncom(BigDecimal transactioncom) {
        this.transactioncom = transactioncom;
    }

    /**
     *
     * 交易场所
     */
    @Override
    @NotEmpty(fieldName = "EtrSecLend.tradeMarket", groups = {ValidateGroupEdit.class})
    public String getTradeMarket() {
        return super.getTradeMarket();
    }
    /**
     *
     * 取得组合代码
     */
    @Override
    @NotEmpty(fieldName = "EtrSecLend.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return super.getPortfolioId();
    }

    /**
     * 取得 委托开始日
     * @return 委托开始日
     */
    @Override
    @NotEmpty(fieldName = "EtrSecLend.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return super.getVdate();
    }

    /**
     * 取得 委托结束日
     * @return 委托结束日
     */
    @Override
    @NotEmpty(fieldName = "EtrSecLend.mdate", groups = {ValidateGroupEdit.class})
    public Date getMdate() {
        return super.getMdate();
    }

    /**
     * 取得 币种
     * @return 币种
     */
    @Override
    @NotEmpty(fieldName = "EtrSecLend.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return super.getCcy();
    }

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    @Override
    @NotEmpty(fieldName = "EtrSecLend.finprodId", groups = {ValidateGroupEdit.class})
    public String getFinprodId() {
        return super.getFinprodId();
    }

    /**
     * 取得 委托代码
     * @return 委托代码
     */
    @Override
    @PrimaryKey(value = PrimaryKey.Field.PK_1)
    public String getEntrustId() {
        return super.getEntrustId();
    }



    /**
     * 获取 targetFinprodId
     * @return targetFinprodId.
     */
    public String getTargetFinprodId() {
        return targetFinprodId;
    }


    /**
     * 设置 targetFinprodId
     * @param targetFinprodId targetFinprodId
     */
    public void setTargetFinprodId(String targetFinprodId) {
        this.targetFinprodId = targetFinprodId;
    }


    /**
     * 获取 targetFinprodName
     * @return targetFinprodName.
     */
    public String getTargetFinprodName() {
        return targetFinprodName;
    }


    /**
     * 设置 targetFinprodName
     * @param targetFinprodName targetFinprodName
     */
    public void setTargetFinprodName(String targetFinprodName) {
        this.targetFinprodName = targetFinprodName;
    }


    /**
     * 获取 busiPs
     * @return busiPs.
     */
    @NotEmpty(fieldName = "EtrSecLend.busiPs", groups = {ValidateGroupEdit.class})
    public String getBusiPs() {
        return busiPs;
    }


    /**
     * 设置 busiPs
     * @param busiPs busiPs
     */
    public void setBusiPs(String busiPs) {
        this.busiPs = busiPs;
    }


    /**
     * 获取 totalAmt
     * @return totalAmt.
     */
    @NotEmpty(fieldName = "EtrSecLend.totalAmt", groups = {ValidateGroupEdit.class})
    @Amount(fieldName = "EtrSecLend.totalAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getTotalAmt() {
        return totalAmt;
    }


    /**
     * 设置 totalAmt
     * @param totalAmt totalAmt
     */
    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    /**
     * 获取 secAcctId
     * @return secAcctId.
     */
    @NotEmpty(fieldName = "EtrSecLend.secAcctId", groups = {ValidateGroupEdit.class})
    public String getSecAcctId() {
        return secAcctId;
    }


    /**
     * 设置 secAcctId
     * @param secAcctId secAcctId
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 获取 termType
     * @return termType.
     */
    public String getTermType() {
        return termType;
    }


    /**
     * 设置 termType
     * @param termType termType
     */
    public void setTermType(String termType) {
        this.termType = termType;
    }


    /**
     * 获取 settleSpeed
     * @return settleSpeed.
     */
    public String getSettleSpeed() {
        return settleSpeed;
    }


    /**
     * 设置 settleSpeed
     * @param settleSpeed settleSpeed
     */
    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    /**
     * 获取 calendarId
     * @return calendarId.
     */
    public String getCalendarId() {
        return calendarId;
    }


    /**
     * 设置 calendarId
     * @param calendarId calendarId
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取 busiRate
     * @return busiRate.
     */
    public BigDecimal getBusiRate() {
        return busiRate;
    }


    /**
     * 设置 busiRate
     * @param busiRate busiRate
     */
    public void setBusiRate(BigDecimal busiRate) {
        this.busiRate = busiRate;
    }

    /**
     * 获取 objectAvailablePosition
     * @return objectAvailablePosition.
     */
    public BigDecimal getObjectAvailablePosition() {
        return objectAvailablePosition;
    }


    /**
     * 设置 objectAvailablePosition
     * @param objectAvailablePosition objectAvailablePosition
     */
    public void setObjectAvailablePosition(BigDecimal objectAvailablePosition) {
        this.objectAvailablePosition = objectAvailablePosition;
    }

    /**
     * 获取 bussRepoSec
     * @return bussRepoSec.
     */
    public BussRepoSecDto getBussRepoSec() {
        return bussRepoSec;
    }


    /**
     * 设置 bussRepoSec
     * @param bussRepoSec bussRepoSec
     */
    public void setBussRepoSec(BussRepoSecDto bussRepoSec) {
        this.bussRepoSec = bussRepoSec;
    }

    /**
     *
     * 设置委托状态数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusEntrustStatus(){
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, super.getEntrustStatus());
    }
    /**
     *
     * 设置币种数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusCcy(){
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());

    }

    public String getBusBusiPs() {
        return SysDataUtil.getNameFromDataDict(DictCode.回购拆借方向,getBusiPs());
    }
    /**
     *
     * 设置交易场所数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusTradeMarket(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }
    /**
     * 当前处理人
     */
    private String nextUser;

    public String getNextUser() {
        return nextUser;
    }

    public void setNextUser(String nextUser) {
        this.nextUser = nextUser;
    }
    public String getMdateStr() {
        return DateUtil.format(super.getMdate());
    }

    public String getVdateStr() {
        return DateUtil.format(super.getVdate());
    }

    public String getBalanceCheck() {
        return balanceCheck;
    }

    public void setBalanceCheck(String balanceCheck) {
        this.balanceCheck = balanceCheck;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }
}

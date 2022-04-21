
package com.joyintech.tams.invest.entrust.etrstockconnect.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etrstockconnect.validator.EtrStockConnect002Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  指令新建
 * @see EtrStockConnectDto
 * @since 1.0
 */
@PageId("EtrStockConnectMain")
@ValidLogic(targetClass = EtrStockConnect002Validator.class , groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class EtrStockConnectDto extends EtrEntrust implements ITableRow, ProcessEntity {

	
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 画面类型
     */
    private String pageModel;
    
    /**
     * 按钮id
     */
    private String elementId;

    /**
     * 执行进度
     */
    private BigDecimal implementPercent;
    /**
     * 成交进度
     */
    private BigDecimal dealPercent;

    /**
     * 母组合代码
     */
    private String parentPortfolioId;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 资产名称
     */
    private String finprodName;

    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 港股通市场
     */
    private String hkseMarket;

    /**
     * 港股通市场
     */
    private String targetType;

    /**
     * 委托关闭方式
     */
    private String closeWay;

    /**
     * 记录操作类型
     */
    private String operType;

    /**
     * 指令金额(港元)
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal entrustAmtHk;

    /**
     * 参考汇率
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal referenceRate;

    /**
     * 参考汇率
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal referenceRateIn;

    /**
     * 参考汇率
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal referenceRateOut;

    /**
     * 参考汇率
     */
    private String referenceRateStr;

    /**
     * 计算指标
     */
    private String calFlag;
    
    /**
     * 序号
     */
    private String indexId;
    
    /**
     * 多指令导入
     */
    private boolean importFlow;

    /**
     * 证券账户
     */
    private String secAcctId;

    /**
     * 证券账户
     */
    private String acctNo;

    /**
     * 最新价
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal closePrice;

    /**
     * 最小买卖单位
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal minSaleUnit;

    /**
     * 资产可用持仓
     */
    private BigDecimal assetAvailablePosition;

    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;
    
    /**
     * 文件上传dto
     */
    private List<SysUploadFileInfo> fileArray;

    /**
     * 选中的数据
     */
    private List<EtrStockConnectDto> selectedItem;

    /**
     * 人员信息业务组件DTO
     */
    private BussEtrInvestPersonInfoDto etrInvestPersonInfo;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public BigDecimal getImplementPercent() {
        return implementPercent;
    }

    public void setImplementPercent(BigDecimal implementPercent) {
        this.implementPercent = implementPercent;
    }

    public BigDecimal getImplementPercentAge(){
        if(StringUtil.isNotEmpty(dealPercent)){
            return implementPercent.multiply(new BigDecimal(100));
        }else{
            return new BigDecimal(0);
        }
    }

    public BigDecimal getDealPercent() {
        return dealPercent;
    }

    public void setDealPercent(BigDecimal dealPercent) {
        this.dealPercent = dealPercent;
    }

    public BigDecimal getDealPercentAge(){
        if(StringUtil.isNotEmpty(dealPercent)){
            return dealPercent.multiply(new BigDecimal(100));
        }else{
            return new BigDecimal(0);
        }
    }

    public String getParentPortfolioId() {
        return parentPortfolioId;
    }

    public void setParentPortfolioId(String parentPortfolioId) {
        this.parentPortfolioId = parentPortfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getHkseMarket() {
        return hkseMarket;
    }

    public void setHkseMarket(String hkseMarket) {
        this.hkseMarket = hkseMarket;
    }

    @NotEmpty(fieldName = "EtrStockConnect002.targetType", groups = {ValidateGroupEdit.class})
    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getCloseWay() {
        return closeWay;
    }

    public void setCloseWay(String closeWay) {
        this.closeWay = closeWay;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    @NotEmpty(fieldName = "EtrStockConnect001.entrustAmtHk", groups = {ValidateGroupEdit.class})
    public BigDecimal getEntrustAmtHk() {
        return entrustAmtHk;
    }

    public void setEntrustAmtHk(BigDecimal entrustAmtHk) {
        this.entrustAmtHk = entrustAmtHk;
    }

    public BigDecimal getReferenceRate() {
        return referenceRate;
    }

    public void setReferenceRate(BigDecimal referenceRate) {
        this.referenceRate = referenceRate;
    }

    public BigDecimal getReferenceRateIn() {
        return referenceRateIn;
    }

    public void setReferenceRateIn(BigDecimal referenceRateIn) {
        this.referenceRateIn = referenceRateIn;
    }

    public BigDecimal getReferenceRateOut() {
        return referenceRateOut;
    }

    public void setReferenceRateOut(BigDecimal referenceRateOut) {
        this.referenceRateOut = referenceRateOut;
    }

    public String getReferenceRateStr() {
        return referenceRateStr;
    }

    public void setReferenceRateStr(String referenceRateStr) {
        this.referenceRateStr = referenceRateStr;
    }

    public String getCalFlag() {
        return calFlag;
    }

    public void setCalFlag(String calFlag) {
        this.calFlag = calFlag;
    }

    public List<SysUploadFileInfo> getFileArray() {
        return fileArray;
    }

    public void setFileArray(List<SysUploadFileInfo> fileArray) {
        this.fileArray = fileArray;
    }

    public BussEtrInvestPersonInfoDto getEtrInvestPersonInfo() {
        return etrInvestPersonInfo;
    }

    public void setEtrInvestPersonInfo(BussEtrInvestPersonInfoDto etrInvestPersonInfo) {
        this.etrInvestPersonInfo = etrInvestPersonInfo;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public boolean isImportFlow() {
        return importFlow;
    }

    public void setImportFlow(boolean importFlow) {
        this.importFlow = importFlow;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public List<EtrStockConnectDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<EtrStockConnectDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getMinSaleUnit() {
        return minSaleUnit;
    }

    public void setMinSaleUnit(BigDecimal minSaleUnit) {
        this.minSaleUnit = minSaleUnit;
    }

    public BigDecimal getAssetAvailablePosition() {
        return assetAvailablePosition;
    }

    public void setAssetAvailablePosition(BigDecimal assetAvailablePosition) {
        this.assetAvailablePosition = assetAvailablePosition;
    }

    /**
     * 取得 委托代码
     * @return 委托代码
     */
    @Override
    @PrimaryKey(value = Field.PK_1)
    public String getEntrustId() {
        return super.getEntrustId();
    }

    @Override
    @NotEmpty(fieldName = "EtrStockConnect002.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId(){
        return super.getPortfolioId();
    }

    @Override
    @NotEmpty(fieldName = "EtrStockConnect002.finprodId", groups = {ValidateGroupEdit.class})
    public String getFinprodId(){
        return super.getFinprodId();
    }

    @Override
    @NotEmpty(fieldName = "EtrStockConnect002.ps", groups = {ValidateGroupEdit.class})
    public String getPs(){
        return super.getPs();
    }

    @Override
    @NotEmpty(fieldName = "EtrStockConnect002.priceModel", groups = {ValidateGroupEdit.class})
    public String getPriceModel() {
        return super.getPriceModel();
    }

    @Override
    @NotEmpty(fieldName = "EtrStockConnect002.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate(){
        return super.getVdate();
    }

    @Override
    @NotEmpty(fieldName = "EtrStockConnect002.mdate", groups = {ValidateGroupEdit.class})
    public Date getMdate(){
        return super.getMdate();
    }

    @Override
    @NotEmpty(fieldName = "EtrStockConnect002.entrustNum", groups = {ValidateGroupEdit.class})
    public BigDecimal getEntrustNum(){
        return super.getEntrustNum();
    }

    @Override
    @NotEmpty(fieldName = "EtrStockConnect002.entrustAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getEntrustAmt(){
        return super.getEntrustAmt();
    }


    /**
     * 数据字典
     */
    public String getPriceModelStr(){
        if (StringUtil.isEmpty(getPriceModel())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, getPriceModel());
    }

    public String getPsStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, getPs());
    }

    public String getTradeMarketStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
    }


    public String getEntrustStatusStr(){
        if (StringUtil.isEmpty(getEntrustStatus())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, getEntrustStatus());
    }

    public String getHkseMarketStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否沪港通或深港通, getHkseMarket());
    }


}

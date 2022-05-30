package com.joyintech.tams.invest.trade.project.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.trade.dto.BussTrdAvailableCashQuery;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.trade.project.TrdProjectInvestTradeDto;
import com.joyintech.tams.invest.common.risk.dto.RiskCreditListCommonDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.trade.project.validator.TrdProjectInvestTradeEditDtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: tams-invest-parent
 * @description: 详情页面dto
 * @author: 强成西
 * @create: 2018-12-05 09:20
 **/
@PageId("TrdProjectInvestTrade002")
@ValidLogic(targetClass = TrdProjectInvestTradeEditDtoValidator.class, groups = {ValidateGroupEdit.class})
public class TrdProjectInvestTrade002Dto extends TrdProjectInvestTradeDto implements ProcessEntity {

    /**
     * 资产到期日
     */
    private Date finmdate;
    /**
     * 附件上传
     */
    private List<UploadFile> uploadFileList;

    /**
     * 组合分配
     */
    private List<BussTrdTransfenpeiDto> bussTrdTransfenpeiDto;

    /**
     * 划款指令
     */
    private List<BussTrdTransferOrderDto> bussTrdTransferOrder;

    /**
     * 人员信息
     */
    @Valid
    private TrdInvestPersonInfo002Dto trdInvestPersonInfo;

    /**
     * 可用资产实体类
     */
    private BussTrdAvailableCashQuery bussTrdAvailableCashQuery;

    /**
     * 撤销理由
     */
    private String revokeReason;

    /**
     * 委托时的本金
     */
    private BigDecimal entrustPrinAmt;

    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;
    
    /**
     * 交易日历
     */
    private String calendarId;

    /**
     * 通道代码
     */
    private String chlId;

    /**
     * 通道费
     */
    private BigDecimal channelFee;
    
    /**
     * 通道收取方式
     */
    private String collectType;
    /**
     * 通道性质
     */
    private String payType;
    
    /**
     * 交易对手类型
     */
    private String counterType;

    /**
     * 交易编码
     */
    private String tradeId;

    private String pageModel;


    /**
     * 组合T日可用现金，用于APP显示
     * @return
     */
    private String appAvalibleCash;

    /**
     * 组合T+1日可用现金,用于APP显示
     * @return
     */
    private String appAvalibleCash1;

    /**
     * 资产可用持仓，用于APP显示
     * @return
     */
    private  String appAssetPosition;

    /**
     * 转仓投资目的
     */
    private String turnInvAim;

    /**
     *业务模式
     */
    private String businessModel;

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getAppAvalibleCash() {
        return appAvalibleCash;
    }

    public void setAppAvalibleCash(String appAvalibleCash) {
        this.appAvalibleCash = appAvalibleCash;
    }

    public String getAppAvalibleCash1() {
        return appAvalibleCash1;
    }

    public void setAppAvalibleCash1(String appAvalibleCash1) {
        this.appAvalibleCash1 = appAvalibleCash1;
    }

    public String getAppAssetPosition() {
        return appAssetPosition;
    }

    public void setAppAssetPosition(String appAssetPosition) {
        this.appAssetPosition = appAssetPosition;
    }

    /**
     * 金融产品交易实体类-供应收款类型交易
     */
    private FinProductTradeDto finRecdProductTradeDto;
    
    private RiskCreditListCommonDto riskCreditListCommonDto;

    /**
     * 获取 counterType
     *
     * @return counterType
     */
    public String getCounterType() {
        return counterType;
    }

    /**
     * 设置 counterType
     *
     * @param counterType counterType
     */
    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }
    /**
     * 获取通道代码
     * @return
     */
    public String getChlId() {
        return chlId;
    }

    /**
     * 设置通道代码
     * @param chlId
     */
    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    public BigDecimal getChannelFee() {
        return channelFee;
    }

    public void setChannelFee(BigDecimal channelFee) {
        this.channelFee = channelFee;
    }

    /**
     * 获取 riskTrialRetDto
     * 
     * @return riskTrialRetDto.
     */
    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    /**
     * 设置 riskTrialRetDto
     * 
     * @param riskTrialRetDto riskTrialRetDto
     */
    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    @Override
    @PrimaryKey(PrimaryKey.Field.PK_3)
    public String getEntrustId() {
        return super.getEntrustId();
    }

    @PrimaryKey(PrimaryKey.Field.PK_2)
    public String getBizType() {
        return DictCodeList.共通业务类型_项目类资产交易.getValue();
    }

    @PrimaryKey(PrimaryKey.Field.PK_1)
    @Override
    public String getBizCode() {
        return super.getBizCode();
    }

    @Override
    @NotEmpty(fieldName = "TrdLendDuration001.prinAmt", groups = ValidateGroupEdit.class)
    public BigDecimal getPrinAmt() {
        return super.getPrinAmt();
    }

    @Override
    @NotEmpty(fieldName = "FinProductFeePay002.feeTable.actPayAmt", groups = ValidateGroupEdit.class)
    public BigDecimal getTradeAmt() {
        return super.getTradeAmt();
    }

    @Override
    @NotEmpty(fieldName = "组合代码", groups = ValidateGroupEdit.class)
    public String getPortfolioId() {
        return super.getPortfolioId();
    }

    @Override
    @NotEmpty(fieldName = "资产代码", groups = ValidateGroupEdit.class)
    public String getFinprodMarketId() {
        return super.getFinprodMarketId();
    }

    public Date getFinmdate() {
        return finmdate;
    }

    public void setFinmdate(Date finmdate) {
        this.finmdate = finmdate;
    }

    /**
     * 获取 uploadFileList
     *
     * @return uploadFileList
     */
    public List<UploadFile> getUploadFileList() {
        return uploadFileList;
    }

    /**
     * 设置 uploadFileList
     *
     * @param uploadFileList uploadFileList
     */
    public void setUploadFileList(List<UploadFile> uploadFileList) {
        this.uploadFileList = uploadFileList;
    }

    /**
     * 获取 bussTrdTransferOrder
     *
     * @return bussTrdTransferOrder
     */
    public List<BussTrdTransferOrderDto> getBussTrdTransferOrder() {
        return bussTrdTransferOrder;
    }

    /**
     * 设置 bussTrdTransferOrder
     *
     * @param bussTrdTransferOrder bussTrdTransferOrder
     */
    public void setBussTrdTransferOrder(List<BussTrdTransferOrderDto> bussTrdTransferOrder) {
        this.bussTrdTransferOrder = bussTrdTransferOrder;
    }

    /**
     * 获取 trdInvestPersonInfo
     *
     * @return trdInvestPersonInfo
     */
    public TrdInvestPersonInfo002Dto getTrdInvestPersonInfo() {
        return trdInvestPersonInfo;
    }

    /**
     * 设置 trdInvestPersonInfo
     *
     * @param trdInvestPersonInfo trdInvestPersonInfo
     */
    public void setTrdInvestPersonInfo(TrdInvestPersonInfo002Dto trdInvestPersonInfo) {
        this.trdInvestPersonInfo = trdInvestPersonInfo;
    }

    /**
     * 获取 bussTrdAvailableCashQuery
     *
     * @return bussTrdAvailableCashQuery
     */
    public BussTrdAvailableCashQuery getBussTrdAvailableCashQuery() {
        return bussTrdAvailableCashQuery;
    }

    /**
     * 设置 bussTrdAvailableCashQuery
     *
     * @param bussTrdAvailableCashQuery bussTrdAvailableCashQuery
     */
    public void setBussTrdAvailableCashQuery(BussTrdAvailableCashQuery bussTrdAvailableCashQuery) {
        this.bussTrdAvailableCashQuery = bussTrdAvailableCashQuery;
    }

    /**
     * 获取 revokeReason
     *
     * @return revokeReason
     */
    public String getRevokeReason() {
        return revokeReason;
    }

    /**
     * 设置 revokeReason
     *
     * @param revokeReason revokeReason
     */
    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }

    /**
     * 获取 entrustPrinAmt
     *
     * @return entrustPrinAmt
     */
    public BigDecimal getEntrustPrinAmt() {
        return entrustPrinAmt;
    }

    /**
     * 设置 entrustPrinAmt
     *
     * @param entrustPrinAmt entrustPrinAmt
     */
    public void setEntrustPrinAmt(BigDecimal entrustPrinAmt) {
        this.entrustPrinAmt = entrustPrinAmt;
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

	public String getCollectType() {
		return collectType;
	}

	public void setCollectType(String collectType) {
		this.collectType = collectType;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public List<BussTrdTransfenpeiDto> getBussTrdTransfenpeiDto() {
		return bussTrdTransfenpeiDto;
	}

	public void setBussTrdTransfenpeiDto(List<BussTrdTransfenpeiDto> bussTrdTransfenpeiDto) {
		this.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
	}

    /**
     * 获取 tradeId
     *
     * @return tradeId
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 tradeId
     *
     * @param tradeId tradeId
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

	public String getPageModel() {
		return pageModel;
	}

	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}


    public List<TrdProductDeal> trdProductDealList;

    /**
     * 获取 trdProductDealList
     *
     * @return trdProductDealList
     */
    public List<TrdProductDeal> getTrdProductDealList() {
        return trdProductDealList;
    }

    /**
     * 设置 trdProductDealList
     *
     * @param trdProductDealList trdProductDealList
     */
    public void setTrdProductDealList(List<TrdProductDeal> trdProductDealList) {
        this.trdProductDealList = trdProductDealList;
    }

    /**
     * 获取 finRecdProductTradeDto
     *
     * @return finRecdProductTradeDto
     */
    public FinProductTradeDto getFinRecdProductTradeDto() {
        return finRecdProductTradeDto;
    }

    /**
     * 设置 finRecdProductTradeDto
     *
     * @param finRecdProductTradeDto finRecdProductTradeDto
     */
    public void setFinRecdProductTradeDto(FinProductTradeDto finRecdProductTradeDto) {
        this.finRecdProductTradeDto = finRecdProductTradeDto;
    }

    public RiskCreditListCommonDto getRiskCreditListCommonDto() {
        return riskCreditListCommonDto;
    }

    public void setRiskCreditListCommonDto(RiskCreditListCommonDto riskCreditListCommonDto) {
        this.riskCreditListCommonDto = riskCreditListCommonDto;
    }
    private boolean isTransfer;

    public boolean isTransfer() {
        return isTransfer;
    }

    public void setTransfer(boolean transfer) {
        isTransfer = transfer;
    }

    // modify by etliu 20220521 start
    // 3.0.6场内 ZGCPCN-688:项目类资产交易新建画面，提交时提示【请输入投资目的】，但是画面上只有业务模式，并且业务模式只读
    // 修改为初始值有值的时候不可改，为空的时候可改
//    @NotEmpty(fieldName = "EtrInvestBondInfo002.invAim", groups = {ValidateGroupEdit.class})
    @NotEmpty(fieldName = "EtrBondFirstLBidding002.businessModel", groups = {ValidateGroupEdit.class})
    @Override
    public String getInvAim() {
    	return super.getInvAim();
    }
    // modify by etliu 20220521 end

    public String getTurnInvAim() {
        return turnInvAim;
    }

    public void setTurnInvAim(String turnInvAim) {
        this.turnInvAim = turnInvAim;
    }
}

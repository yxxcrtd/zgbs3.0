/*
 * 文件名：TrdBondLendInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年06月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.bondlend.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.dto.RiskCreditListCommonDto;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.bondlend.validator.TrdBondLendInvestTradeDtoValidator;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 债券借贷交易Dto
 *
 * @author 裴家俊
 * @version 1.0 2019年6月25日
 * @see TrdBondLendInvestTradeDto
 * @since 1.0
 */
@PageId("TrdBondLendInvestTrade002")
@ValidLogic(targetClass = TrdBondLendInvestTradeDtoValidator.class, groups = ValidateGroupEdit.class)
public class TrdBondLendInvestTradeDto extends TrdProductDeal implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -7438510730454849159L;
    
	/**
     * 取得 交易编号
     *
     * @return 交易编号
     */
    @Override
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getTradeId() {
        return super.getTradeId();
    }

    /**
     * 取得 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     *
     * @return 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    @Override
    @PrimaryKey(PrimaryKey.Field.PK_2)
    public String getFinprodType2() {
        return super.getFinprodType2();
    }

    /**
     * 有效状态
     */
    private String validStatus;

    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 组合代码
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.portfolioId",groups = ValidateGroupEdit.class)
    private String portfolioId;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 资产名称
     */
    private String finprodName;

    /**
     * 清算速度
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.settleSpreed",groups = ValidateGroupEdit.class)
    private String settleSpeedStr;

    /**
     * 交易日历
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.calendarId",groups = ValidateGroupEdit.class)
    private String calendarId;

    /**
     * 期限品种
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.termType",groups = ValidateGroupEdit.class)
    private String termType;

    /**
     * 期限(天数，交易类金融产品表中)
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.term",groups = ValidateGroupEdit.class)
    private Long term;

    /**
     * 首次结算方式(金融产品交易中结算方式)
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.firstSettleType",groups = ValidateGroupEdit.class)
    private String deliveryType;
    /**
     * 到期结算方式(交易类金融产品中到期结算方式)
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.matSettleType",groups = ValidateGroupEdit.class)
    private String mDeliveryType;

    /**
     * 争议解决方案
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.conflictSolveWay",groups = ValidateGroupEdit.class)
    private String conflictSolveWay;

    /**
     * 借贷费率（交易类金融产品中的到期收益率）
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.mYield",groups = ValidateGroupEdit.class)
    private BigDecimal mYield;

    /**
     * 借贷费用（交易类金融产品中的到期金额）
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.mTradeAmt",groups = ValidateGroupEdit.class)
    private BigDecimal mTradeAmt;
    /**
     * 实际占券天数
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.actAmtDays",groups = ValidateGroupEdit.class)
    private String actAmtDays;
    /**
     * 出/回款账号
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.recAcctNo",groups = ValidateGroupEdit.class)
    private  String acctOwenAcctId;
    /**
     * 对手方账号
     */
    @NotEmpty(fieldName = "TrdBondLendInvestTrade002.payAcctNo",groups = ValidateGroupEdit.class)
    private  String acctRivalId;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 画面模式
     */
    private String pageModel;

    /**
     * 工作流状态
     */
    private String workflowStatus;

    /**
     * 计算类型
     */
    private String calculateField;

    /**
     * 金融产品交易Entity
     */
    private TrdProductDeal trdProductDeal;

    /**
     * 金融产品交易附属
     */
    private TrdProductDealAdd trdProductDealAdd;

    /**
     * 组合交易
     */
    private PtlTrade ptlTrade;

    /**
     * 金融产品交易扩展
     */
    private PaTrdProductDeal paTrdProductDeal;

    /**
     * 金融产品分类
     */
    private FinProductType finProductType;

    /**
     * 交易类金融产品Entity
     */
    @Valid
    private FinTradeProductDto finTradeProduct;

    /**
     * 交易类金融产品附属表
     */
    private FinTradeProductAdd finTradeProductAdd;

    /**
     * 金融产品账户信息
     */
    private StlFinProductAcct stlFinProductAcct;

    /**
     * 金融产品交易业务流程信息
     */
    private WflTrdProductDeal wflTrdProductDeal;

    /**
     * 金融产品交易业务流程其他信息
     */
    private WflTrdProductDealOth wflTrdProductDealOth;

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
     * 添加质押券信息
     */
    private BussRepoSecDto bussRepoSec;

    /**
     * 附件上传
     */
    private List<UploadFile> uploadFileList;

    /**
     * 有效状态
     */
    private String validStatusText;

    /**
     * 交易方向
     */
    private String psText;

    /**
     * 交易场所
     */
    private String tradeMarketText;

    /**
     * 币种
     */
    private String ccyText;

    /**
     * 交易日
     */
    private String tradeDateText;

    /**
     * 交割日
     */
    private String settleDateText;

    /**
     * 委托时的券面总额
     */
    private BigDecimal entrustNum;

    /**
     * 委托时的净价
     */
    private BigDecimal entrustPrice;

    /**
     * 委托时的到期收益率
     */
    private BigDecimal entrustYield;

    /**
     * 委托时的行权收益率
     */
    private BigDecimal entrustExerYield;

    /**
     * 撤销说明
     */
    private String revokeReason;

    /**
     * 金融产品代码（系统自动生成）
     */
    private String systemFinprodId;

    /**
     * 资产可用持仓
     */
    private BigDecimal assetPosition;

    /**
     * 补充条款
     */
    private String supplyClause;
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;
    /**
     * 子组合代码
     */
    private String actPortfolioId;

    /**
     * 质押劵置换安排
     */
    private String exchangePledge;
    
    private RiskCreditListCommonDto riskCreditListCommonDto;


    private String balanceCheck;//是否校验余额

    public String getExchangePledge() {
        return exchangePledge;
    }

    public void setExchangePledge(String exchangePledge) {
        this.exchangePledge = exchangePledge;
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
     * 取得 交易日期
     *
     * @return 交易日期
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.tradeDate",groups = ValidateGroupEdit.class)
    @Override
    public Date getTradeDate() {
        return super.getTradeDate();
    }

    /**
     * 取得 交易方向，买入、卖出、收息、还本、申购、赎回等
     *
     * @return 交易方向，买入、卖出、收息、还本、申购、赎回等
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.ps",groups = ValidateGroupEdit.class)
    @Override
    public String getPs() {
        return super.getPs();
    }

    /**
     * 取得 金融产品代码
     *
     * @return 金融产品代码
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.finprodId",groups = ValidateGroupEdit.class)
    @Override
    public String getFinprodId() {
        return super.getFinprodId();
    }

    /**
     * 取得 交易场所，银行间、上交所、深交所、柜面等
     *
     * @return 交易场所，银行间、上交所、深交所、柜面等
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.tradeMarket",groups = ValidateGroupEdit.class)
    @Override
    public String getTradeMarket() {
        return super.getTradeMarket();
    }

    /**
     * 取得 银行间成交编号
     */    
    @Override
    public String getfTradeId() {
        return super.getfTradeId();
    }

    /**
     * 取得 券面总额
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.tradeAmt",groups = ValidateGroupEdit.class)
    @Override
    public BigDecimal getShareAmt() {
        return super.getShareAmt();
    }

    /**
     * 取得 证券管理户代码
     *
     * @return 证券管理户代码
     */
//    @NotEmpty(fieldName = "TrdBondInvestTrade002.secManageAcctId")
    @Override
    public String getSecManageAcctId() {
        return super.getSecManageAcctId();
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
     * 获取 isExercise
     *
     * @return isExercise
     */
    public String getIsExercise() {
        return isExercise;
    }

    /**
     * 设置 isExercise
     *
     * @param isExercise isExercise
     */
    public void setIsExercise(String isExercise) {
        this.isExercise = isExercise;
    }

    /**
     * 是否行权
     */
    private String isExercise;

    /**
     * 获取 validStatus
     *
     * @return validStatus
     */
    public String getValidStatus() {
        return validStatus;
    }

    /**
     * 设置 validStatus
     *
     * @param validStatus validStatus
     */
    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    /**
     * 获取 entrustId
     *
     * @return entrustId
     */
    //huwenbin 指令接收交易录单验证数据已存在流程中
    @PrimaryKey(PrimaryKey.Field.PK_3)
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 设置 entrustId
     *
     * @param entrustId entrustId
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    /**
     * 获取 portfolioId
     *
     * @return portfolioId
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     *
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 portfolioName
     *
     * @return portfolioName
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
     * 获取 finprodName
     *
     * @return finprodName
     */
    public String getFinprodName() {
        return finprodName;
    }

    /**
     * 设置 finprodName
     *
     * @param finprodName finprodName
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    /**
     * 获取 calendarId
     *
     * @return calendarId
     */
    public String getCalendarId() {
        return calendarId;
    }

    /**
     * 设置 calendarId
     *
     * @param calendarId calendarId
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取 operateType
     *
     * @return operateType
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
     * 获取 pageModel
     *
     * @return pageModel
     */
    public String getPageModel() {
        return pageModel;
    }

    /**
     * 设置 pageModel
     *
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    /**
     * 获取 trdProductDealAdd
     *
     * @return trdProductDealAdd
     */
    public TrdProductDealAdd getTrdProductDealAdd() {
        return trdProductDealAdd;
    }

    /**
     * 设置 trdProductDealAdd
     *
     * @param trdProductDealAdd trdProductDealAdd
     */
    public void setTrdProductDealAdd(TrdProductDealAdd trdProductDealAdd) {
        this.trdProductDealAdd = trdProductDealAdd;
    }

    /**
     * 获取 ptlTrade
     *
     * @return ptlTrade
     */
    public PtlTrade getPtlTrade() {
        return ptlTrade;
    }

    /**
     * 设置 ptlTrade
     *
     * @param ptlTrade ptlTrade
     */
    public void setPtlTrade(PtlTrade ptlTrade) {
        this.ptlTrade = ptlTrade;
    }

    /**
     * 获取 paTrdProductDeal
     *
     * @return paTrdProductDeal
     */
    public PaTrdProductDeal getPaTrdProductDeal() {
        return paTrdProductDeal;
    }

    /**
     * 设置 paTrdProductDeal
     *
     * @param paTrdProductDeal paTrdProductDeal
     */
    public void setPaTrdProductDeal(PaTrdProductDeal paTrdProductDeal) {
        this.paTrdProductDeal = paTrdProductDeal;
    }

    /**
     * 获取 calculateField
     *
     * @return calculateField
     */
    public String getCalculateField() {
        return calculateField;
    }

    /**
     * 设置 calculateField
     *
     * @param calculateField calculateField
     */
    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    /**
     * 获取 uploadFileList
     *
     * @return uploadFileList.
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
     * 获取 workflowStatus
     *
     * @return workflowStatus.
     */
    public String getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * 设置 workflowStatus
     *
     * @param workflowStatus workflowStatus
     */
    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    /**
     * 获取 ccyText
     *
     * @return validStatusText
     */
    public String getValidStatusText() {
        validStatusText = SysDataUtil.getNameFromDataDict(DictCode.交易流程状态, validStatus);
        return validStatusText;
    }

    /**
     * 设置 validStatusText
     *
     * @param validStatusText validStatusText
     */
    public void setValidStatusText(String validStatusText) {
        this.validStatusText = validStatusText;
    }

    /**
     * 获取 psText
     *
     * @return psText.
     */
    public String getPsText() {
        psText = SysDataUtil.getNameFromDataDict(DictCode.回购拆借方向, this.getPs());
        return psText;
    }

    /**
     * 设置 psText
     *
     * @param psText psText
     */
    public void setPsText(String psText) {
        this.psText = psText;
    }

    /**
     * 获取 tradeMarketText
     *
     * @return tradeMarketText.
     */
    public String getTradeMarketText() {
        tradeMarketText = SysDataUtil.getNameFromDataDict(DictCode.交易场所,
            this.getTradeMarket());
        return tradeMarketText;
    }

    /**
     * 设置 tradeMarketText
     *
     * @param tradeMarketText tradeMarketText
     */
    public void setTradeMarketText(String tradeMarketText) {
        this.tradeMarketText = tradeMarketText;
    }

    /**
     * 获取 ccyText
     *
     * @return ccyText.
     */
    public String getCcyText() {
        ccyText = SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
        return ccyText;
    }

    /**
     * 设置 ccyText
     *
     * @param ccyText ccyText
     */
    public void setCcyText(String ccyText) {
        this.ccyText = ccyText;
    }

    /**
     * 获取 tradeDateText
     *
     * @return tradeDateText.
     */
    public String getTradeDateText() {
        tradeDateText = DateUtil.format(this.getTradeDate());
        return tradeDateText;
    }

    /**
     * 设置 tradeDateText
     *
     * @param tradeDateText tradeDateText
     */
    public void setTradeDateText(String tradeDateText) {
        this.tradeDateText = tradeDateText;
    }

    /**
     * 获取 settleDateText
     *
     * @return settleDateText.
     */
    public String getSettleDateText() {
        settleDateText = DateUtil.format(this.getSettleDate());
        return settleDateText;
    }

    /**
     * 设置 settleDateText
     *
     * @param settleDateText settleDateText
     */
    public void setSettleDateText(String settleDateText) {
        this.settleDateText = settleDateText;
    }

    /**
     * 获取 settleSpeedStr
     *
     * @return settleSpeedStr.
     */
    public String getSettleSpeedStr() {
        return settleSpeedStr;
    }

    /**
     * 设置 settleSpeedStr
     *
     * @param settleSpeedStr settleSpeedStr
     */
    public void setSettleSpeedStr(String settleSpeedStr) {
        this.settleSpeedStr = settleSpeedStr;
    }

    /**
     * 获取 entrustNum
     *
     * @return entrustNum.
     */
    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    /**
     * 设置 entrustNum
     *
     * @param entrustNum entrustNum
     */
    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    /**
     * 获取 entrustPrice
     *
     * @return entrustPrice.
     */
    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }

    /**
     * 设置 entrustPrice
     *
     * @param entrustPrice entrustPrice
     */
    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    /**
     * 获取 entrustYield
     *
     * @return entrustYield.
     */
    public BigDecimal getEntrustYield() {
        return entrustYield;
    }

    /**
     * 设置 entrustYield
     *
     * @param entrustYield entrustYield
     */
    public void setEntrustYield(BigDecimal entrustYield) {
        this.entrustYield = entrustYield;
    }

    /**
     * 获取 entrustExerYield
     *
     * @return entrustExerYield.
     */
    public BigDecimal getEntrustExerYield() {
        return entrustExerYield;
    }

    /**
     * 设置 entrustExerYield
     *
     * @param entrustExerYield entrustExerYield
     */
    public void setEntrustExerYield(BigDecimal entrustExerYield) {
        this.entrustExerYield = entrustExerYield;
    }

    /**
     * 获取 revokeReason
     *
     * @return revokeReason.
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
     * 获取 systemFinprodId
     *
     * @return systemFinprodId.
     */
    public String getSystemFinprodId() {
        return systemFinprodId;
    }

    /**
     * 设置 systemFinprodId
     *
     * @param systemFinprodId systemFinprodId
     */
    public void setSystemFinprodId(String systemFinprodId) {
        this.systemFinprodId = systemFinprodId;
    }

    /**
     * 获取 assetPosition
     *
     * @return assetPosition.
     */
    public BigDecimal getAssetPosition() {
        return assetPosition;
    }

    /**
     * 设置 assetPosition
     *
     * @param assetPosition assetPosition
     */
    public void setAssetPosition(BigDecimal assetPosition) {
        this.assetPosition = assetPosition;
    }

    /**
     * 交易金额
     */
    private BigDecimal dealAmt;

	public BigDecimal getDealAmt() {
		return dealAmt;
	}

	public void setDealAmt(BigDecimal dealAmt) {
		this.dealAmt = dealAmt;
	}

	/**
	 * 费用明细表
	 */
	private TrdTradeFeeDetail trdTradeFeeDetail;

	public TrdTradeFeeDetail getTrdTradeFeeDetail() {
		return trdTradeFeeDetail;
	}

	public void setTrdTradeFeeDetail(TrdTradeFeeDetail trdTradeFeeDetail) {
		this.trdTradeFeeDetail = trdTradeFeeDetail;
	}

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    @Override
    public String getDeliveryType() {
        return deliveryType;
    }

    @Override
    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getmDeliveryType() {
        return mDeliveryType;
    }

    public void setmDeliveryType(String mDeliveryType) {
        this.mDeliveryType = mDeliveryType;
    }

    public BigDecimal getmYield() {
        return mYield;
    }

    public void setmYield(BigDecimal mYield) {
        this.mYield = mYield;
    }

    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    public String getConflictSolveWay() {
        return conflictSolveWay;
    }

    public void setConflictSolveWay(String conflictSolveWay) {
        this.conflictSolveWay = conflictSolveWay;
    }

    public String getActAmtDays() {
        return actAmtDays;
    }

    public void setActAmtDays(String actAmtDays) {
        this.actAmtDays = actAmtDays;
    }

    public String getAcctOwenAcctId() {
        return acctOwenAcctId;
    }

    public void setAcctOwenAcctId(String acctOwenAcctId) {
        this.acctOwenAcctId = acctOwenAcctId;
    }

    public String getAcctRivalId() {
        return acctRivalId;
    }

    public void setAcctRivalId(String acctRivalId) {
        this.acctRivalId = acctRivalId;
    }

    public FinProductType getFinProductType() {
        return finProductType;
    }

    public void setFinProductType(FinProductType finProductType) {
        this.finProductType = finProductType;
    }

    public FinTradeProductDto getFinTradeProduct() {
        return finTradeProduct;
    }

    public void setFinTradeProduct(FinTradeProductDto finTradeProduct) {
        this.finTradeProduct = finTradeProduct;
    }

    public WflTrdProductDeal getWflTrdProductDeal() {
        return wflTrdProductDeal;
    }

    public void setWflTrdProductDeal(WflTrdProductDeal wflTrdProductDeal) {
        this.wflTrdProductDeal = wflTrdProductDeal;
    }

    public WflTrdProductDealOth getWflTrdProductDealOth() {
        return wflTrdProductDealOth;
    }

    public void setWflTrdProductDealOth(WflTrdProductDealOth wflTrdProductDealOth) {
        this.wflTrdProductDealOth = wflTrdProductDealOth;
    }

    public BussRepoSecDto getBussRepoSec() {
        return bussRepoSec;
    }

    public void setBussRepoSec(BussRepoSecDto bussRepoSec) {
        this.bussRepoSec = bussRepoSec;
    }

    public TrdProductDeal getTrdProductDeal() {
        return trdProductDeal;
    }

    public void setTrdProductDeal(TrdProductDeal trdProductDeal) {
        this.trdProductDeal = trdProductDeal;
    }

    public StlFinProductAcct getStlFinProductAcct() {
        return stlFinProductAcct;
    }

    public void setStlFinProductAcct(StlFinProductAcct stlFinProductAcct) {
        this.stlFinProductAcct = stlFinProductAcct;
    }
    
    /**
     * 获取 actPortfolioId
     * @return actPortfolioId.
     */
    public String getActPortfolioId() {
        return actPortfolioId;
    }
    
    /**
     * 设置 actPortfolioId
     * @param actPortfolioId actPortfolioId
     */
    public void setActPortfolioId(String actPortfolioId) {
        this.actPortfolioId = actPortfolioId;
    }

    public FinTradeProductAdd getFinTradeProductAdd() {
        return finTradeProductAdd;
    }

    public void setFinTradeProductAdd(FinTradeProductAdd finTradeProductAdd) {
        this.finTradeProductAdd = finTradeProductAdd;
    }

    public String getSupplyClause() {
        return supplyClause;
    }

    public void setSupplyClause(String supplyClause) {
        this.supplyClause = supplyClause;
    }

    public RiskCreditListCommonDto getRiskCreditListCommonDto() {
        return riskCreditListCommonDto;
    }

    public void setRiskCreditListCommonDto(RiskCreditListCommonDto riskCreditListCommonDto) {
        this.riskCreditListCommonDto = riskCreditListCommonDto;
    }

    public String getBalanceCheck() {
        return balanceCheck;
    }

    public void setBalanceCheck(String balanceCheck) {
        this.balanceCheck = balanceCheck;
    }
}

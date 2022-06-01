 package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

 import com.joyintech.framework.annotation.PageId;
 import com.joyintech.framework.base.BaseEntity;
 import com.joyintech.tams.component.business.riskrds.dto.PageModel;
 import com.joyintech.workflow.engine.annotation.PrimaryKey;
 import com.joyintech.workflow.engine.dto.ProcessEntity;
 import io.swagger.annotations.ApiModel;
 import io.swagger.annotations.ApiModelProperty;

 import java.math.BigDecimal;
 import java.util.Date;
 import java.util.List;

 /**
 * 中标结果维护
 * @author wangyang1
 * @version 1.0 2022年4月14日
 * @see CfetsPrimaryBidManageSaveRequest
 * @since 1.0
 */
@PageId("CfetsPrimaryBidManage002")
@ApiModel(value="中标结果维护新建画面表单",description="中标结果维护新建画面表单")
public class CfetsPrimaryBidManageSaveRequest extends BaseEntity implements ProcessEntity{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

     /**
      * 汇总中标编号
      */
     private String totalWinId;

    /**
     * 操作类型
     */
    @ApiModelProperty(name="operateType",value="操作类型",example="submit/save", required = true)
    private String operateType;

    /**
     * 操作类型
     */
    @ApiModelProperty(name="operateStatus",value="操作状态",required = true)
    private String operateStatus;

    @ApiModelProperty(name="elementId",value="按钮id",example="FinBondAsset002_btn_submit/FinBondAsset002_btn_save")
    private String elementId;

    @ApiModelProperty(name="pageId",value="画面ID",example="FinBondAsset002")
    private String pageId;

    @ApiModelProperty(name="pageModel",value="分页对象")
    private PageModel pageModel;

     @ApiModelProperty(name="isWin",value="中标状态")
     private String isWin ;

    /**资产代码*/
    @ApiModelProperty(name="finprodId",value="资产代码")
    private String finprodId ;

     @ApiModelProperty(name="finprodMarketId",value="市场代码")
     private String finprodMarketId ;

    /**资产简称*/
    @ApiModelProperty(name="finprodAbbr",value="资产简称")
    private String finprodAbbr;

    /**本方交易员*/
    @ApiModelProperty(name="ouTraderId",value="本方交易员")
    private String ouTraderId;

    /**交易对手*/
    @ApiModelProperty(name="counterId",value="交易对手")
    private String counterId;

    /**招标标的*/
    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    /**币种*/
    @ApiModelProperty(name="ccy",value="币种")
    private String ccy;

    /**投标日*/
    @ApiModelProperty(name="bidDate",value="投标日")
    private List<Date> bidDate;

     @ApiModelProperty(name="bidDateStart",value="投标开始日")
     private Date bidDateStart;

     @ApiModelProperty(name="bidDateEnd",value="投标结束日")
     private Date bidDateEnd;

    /**缴款日*/
    @ApiModelProperty(name="payDate",value="缴款日")
    private Date payDate;

    /**债权债务登记日*/
    @ApiModelProperty(name="creditorDebtDate",value="债权债务登记日")
    private Date creditorDebtDate;

    /**上市日*/
    @ApiModelProperty(name="listingDate",value="上市日")
    private Date listingDate;

    /**投标方式*/
    @ApiModelProperty(name="winType",value="中标方式")
    private String winType;

    /**缴款方式*/
    @ApiModelProperty(name="payType",value="缴款方式")
    private String payType;

    /**汇总投标券面总额*/
    @ApiModelProperty(name="totalBiddingNum",value="汇总投标券面总额(万元)")
    private BigDecimal totalBiddingNum;

    /**中标券面总额*/
    @ApiModelProperty(name="winPrinamt",value="中标券面总额（万元）")
    private BigDecimal winPrinamt;

    /**中标标位*/
    @ApiModelProperty(name="winPrice",value="中标标位")
    private BigDecimal winPrice;

    /**结算服务费*/
    @ApiModelProperty(name="settFeeAmt",value="结算服务费（元）")
    private BigDecimal settFeeAmt;

    /**备注*/
    @ApiModelProperty(name="remark",value="备注")
    private String remark ;

     @ApiModelProperty(name="tradeMarket",value="交易场所（交易所）")
     private String tradeMarket;

     /**结算服务费*/
     @ApiModelProperty(name="intAmt",value="应计利息（元）")
     private BigDecimal intAmt;

    @ApiModelProperty(name="allocationDetails",value="分配明细")
    private List<CfetsPrimaryBidManageSaveDetailDto> allocationDetails ;

     @ApiModelProperty(name="selectedItem",value="分配明细")
     private List<CfetsPrimaryBidManageSaveRequest> selectedItem ;

     @ApiModelProperty(name="calcAmountDto",value="金额信息")
     private CfetsBidManageCalcAmountResponse calcAmountDto ;

     @ApiModelProperty(name="payAmt",value="缴款金额")
     private BigDecimal payAmt;

     @ApiModelProperty(name="status",value="数据状态")
     private String status;

     @ApiModelProperty(name="circulationCode",value="上市流通代码")
     private String circulationCode;

     @ApiModelProperty(name="seriYN",value="连续投标限制")
     private String seriYN;

     @ApiModelProperty(name="couponSpecies",value="息票品种")
     private String couponSpecies;

     @ApiModelProperty(name="bidModle",value="投标方式")
     private String bidModle;


     private BaseBidManageBaseInfoDto baseBidManageBaseInfoDto;

     public String getOperateType() {
         return operateType;
     }

     public void setOperateType(String operateType) {
         this.operateType = operateType;
     }

     public String getOperateStatus() {
         return operateStatus;
     }

     public void setOperateStatus(String operateStatus) {
         this.operateStatus = operateStatus;
     }

     public String getElementId() {
         return elementId;
     }

     public void setElementId(String elementId) {
         this.elementId = elementId;
     }

     public String getPageId() {
         return pageId;
     }

     public void setPageId(String pageId) {
         this.pageId = pageId;
     }

     public PageModel getPageModel() {
         return pageModel;
     }

     public void setPageModel(PageModel pageModel) {
         this.pageModel = pageModel;
     }

     public String getFinprodId() {
         return finprodId;
     }

     public void setFinprodId(String finprodId) {
         this.finprodId = finprodId;
     }

     public String getFinprodAbbr() {
         return finprodAbbr;
     }

     public void setFinprodAbbr(String finprodAbbr) {
         this.finprodAbbr = finprodAbbr;
     }

     public String getOuTraderId() {
         return ouTraderId;
     }

     public void setOuTraderId(String ouTraderId) {
         this.ouTraderId = ouTraderId;
     }

     public String getCounterId() {
         return counterId;
     }

     public void setCounterId(String counterId) {
         this.counterId = counterId;
     }

     public String getTenderTarget() {
         return tenderTarget;
     }

     public void setTenderTarget(String tenderTarget) {
         this.tenderTarget = tenderTarget;
     }

     public String getCcy() {
         return ccy;
     }

     public void setCcy(String ccy) {
         this.ccy = ccy;
     }

     public List<Date> getBidDate() {
         return bidDate;
     }

     public void setBidDate(List<Date> bidDate) {
         this.bidDate = bidDate;
     }

     public Date getPayDate() {
         return payDate;
     }

     public void setPayDate(Date payDate) {
         this.payDate = payDate;
     }

     public Date getCreditorDebtDate() {
         return creditorDebtDate;
     }

     public void setCreditorDebtDate(Date creditorDebtDate) {
         this.creditorDebtDate = creditorDebtDate;
     }

     public Date getListingDate() {
         return listingDate;
     }

     public void setListingDate(Date listingDate) {
         this.listingDate = listingDate;
     }

     public String getWinType() {
         return winType;
     }

     public void setWinType(String winType) {
         this.winType = winType;
     }

     public String getPayType() {
         return payType;
     }

     public void setPayType(String payType) {
         this.payType = payType;
     }

     public BigDecimal getTotalBiddingNum() {
         return totalBiddingNum;
     }

     public void setTotalBiddingNum(BigDecimal totalBiddingNum) {
         this.totalBiddingNum = totalBiddingNum;
     }

     public BigDecimal getWinPrinamt() {
         return winPrinamt;
     }

     public void setWinPrinamt(BigDecimal winPrinamt) {
         this.winPrinamt = winPrinamt;
     }

     public BigDecimal getWinPrice() {
         return winPrice;
     }

     public void setWinPrice(BigDecimal winPrice) {
         this.winPrice = winPrice;
     }

     public BigDecimal getSettFeeAmt() {
         return settFeeAmt;
     }

     public void setSettFeeAmt(BigDecimal settFeeAmt) {
         this.settFeeAmt = settFeeAmt;
     }

     public String getRemark() {
         return remark;
     }

     public void setRemark(String remark) {
         this.remark = remark;
     }

     public List<CfetsPrimaryBidManageSaveDetailDto> getAllocationDetails() {
         return allocationDetails;
     }

     public void setAllocationDetails(List<CfetsPrimaryBidManageSaveDetailDto> allocationDetails) {
         this.allocationDetails = allocationDetails;
     }
     @PrimaryKey(PrimaryKey.Field.PK_1)
     public String getTotalWinId() {
         return totalWinId;
     }

     public void setTotalWinId(String totalWinId) {
         this.totalWinId = totalWinId;
     }

     public List<CfetsPrimaryBidManageSaveRequest> getSelectedItem() {
         return selectedItem;
     }

     public void setSelectedItem(List<CfetsPrimaryBidManageSaveRequest> selectedItem) {
         this.selectedItem = selectedItem;
     }

     public String getTradeMarket() {
         return tradeMarket;
     }

     public void setTradeMarket(String tradeMarket) {
         this.tradeMarket = tradeMarket;
     }

     public BigDecimal getIntAmt() {
         return intAmt;
     }

     public void setIntAmt(BigDecimal intAmt) {
         this.intAmt = intAmt;
     }

     @ApiModelProperty(name="bidId",value="投标编号")
     private String bidId;

     public String getBidId() {
         return bidId;
     }

     public void setBidId(String bidId) {
         this.bidId = bidId;
     }

     public BigDecimal getPayAmt() {
         return payAmt;
     }

     public void setPayAmt(BigDecimal payAmt) {
         this.payAmt = payAmt;
     }

     @Override
     public String getStatus() {
         return status;
     }

     @Override
     public void setStatus(String status) {
         this.status = status;
     }

     public BaseBidManageBaseInfoDto getBaseBidManageBaseInfoDto() {
         return baseBidManageBaseInfoDto;
     }

     public void setBaseBidManageBaseInfoDto(BaseBidManageBaseInfoDto baseBidManageBaseInfoDto) {
         this.baseBidManageBaseInfoDto = baseBidManageBaseInfoDto;
     }

     public CfetsBidManageCalcAmountResponse getCalcAmountDto() {
         return calcAmountDto;
     }

     public void setCalcAmountDto(CfetsBidManageCalcAmountResponse calcAmountDto) {
         this.calcAmountDto = calcAmountDto;
     }

     public Date getBidDateStart() {
         return bidDateStart;
     }

     public void setBidDateStart(Date bidDateStart) {
         this.bidDateStart = bidDateStart;
     }

     public Date getBidDateEnd() {
         return bidDateEnd;
     }

     public void setBidDateEnd(Date bidDateEnd) {
         this.bidDateEnd = bidDateEnd;
     }

     public String getCirculationCode() {
         return circulationCode;
     }

     public void setCirculationCode(String circulationCode) {
         this.circulationCode = circulationCode;
     }

     public String getSeriYN() {
         return seriYN;
     }

     public void setSeriYN(String seriYN) {
         this.seriYN = seriYN;
     }

     public String getIsWin() {
         return isWin;
     }

     public void setIsWin(String isWin) {
         this.isWin = isWin;
     }

     public String getCouponSpecies() {
         return couponSpecies;
     }

     public void setCouponSpecies(String couponSpecies) {
         this.couponSpecies = couponSpecies;
     }

     public String getFinprodMarketId() {
         return finprodMarketId;
     }

     public void setFinprodMarketId(String finprodMarketId) {
         this.finprodMarketId = finprodMarketId;
     }

     public String getBidModle() {
         return bidModle;
     }

     public void setBidModle(String bidModle) {
         this.bidModle = bidModle;
     }
 }

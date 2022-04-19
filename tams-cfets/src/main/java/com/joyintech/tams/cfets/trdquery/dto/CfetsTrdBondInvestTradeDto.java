/*
 * 文件名：TrdBondInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.cfets.manualsettle.dto.PortIntFenPeiInfoDto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.trade.dto.BussTrdAvailableCashQuery;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.dto.cstp.InterfTradSecdealDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.dto.RiskCreditListCommonDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 债券交易Dto
 *
 * @author Zane.Y
 * @version 1.0 2018年9月7日
 * @see CfetsTrdBondInvestTradeDto
 * @since 1.0
 */
public class CfetsTrdBondInvestTradeDto extends TrdFinProductDealTotal implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -7438510730454849159L;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 指令类型
     */
    private String zhilingtype;

    //    资产推荐方 新增 zhuyongkang 20191023 start
    private String assetRecommandOrg;
    //    资产推荐方 新增 zhuyongkang 20191023 start

    /**
     * 组合分配
     */
    private List<BussTrdTransfenpeiDto> bussTrdTransfenpeiDto;
	
	/**
     *功能描述:报价编号
     * @author zhangnian
     * @date 2021/1/18 0018
     * @param  * @param null
     * @return
    */
    private String quoteId;
	
	/**
     * 是否是银行间下行自动录单数据
     * @return
     */
    private boolean autoTradeFlag;
	
/**
     * 交易金额(原交易金额被券面总额所用)
     */
    private BigDecimal tradeRepAmt;
	 /**
     * 导入标识
     */
    private boolean importFlag;
	
	/**
     * 账户来源
     */
    private String secAcctSource;

    /**
     * 父页面的parentPageId，用于区分字典值控制字段
     */
    private String parentPageId;

    public String getParentPageId() {
        return parentPageId;
    }

    public void setParentPageId(String parentPageId) {
        this.parentPageId = parentPageId;
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
     * 取得 交易编号
     *
     * @return 交易编号
     */
    @Override
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getTotleTradeId() {
        return super.getTotleTradeId();
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
     * 实体组合代码
     */
    private String actPortfolioId;
    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 资产名称
     */
    private String finprodName;

    /**
     * 合作资产
     */
    private String chlFinprodId;

    private String isOfferFlag;//是否存在报价信息

    public String getChlFinprodId() {
        return chlFinprodId;
    }

    public void setChlFinprodId(String chlFinprodId) {
        this.chlFinprodId = chlFinprodId;
    }

    /**
     * 清算速度
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.settleSpreed")
    private String settleSpeedStr;

    /**
     * 交易日历
     */
    private String calendarId;

    /**
     * 债券一级分类
     */
    private String windtypeone;

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

    private List<TrdProductDeal> trdProductDeals;
    /**
     * 金融产品交易附属
     */
    private List<TrdProductDealAdd> trdProductDealAdds;

    /**
     * 金融产品交易扩展
     */
    private List<PaTrdProductDeal> paTrdProductDeals;

    /**
     * 汇总扩展表
     */
    private PaTrdFinProductDealTotal paTrdFinProductDealTotal;

    /**
     * 汇总附属表
     */
    private TrdDealTotalAdd trdDealTotalAdd;
    /**
     * 组合交易
     */
    private List<PtlTrade> ptlTrade;

    /**
     * 划款指令
     */
    private List<BussTrdTransferOrderDto> bussTrdTransferOrder;

    /**
     * 人员信息
     */
    private CfetsTrdInvestPersonInfoDto trdInvestPersonInfo;

    /**
     * 附件上传
     */
    private List<UploadFile> uploadFileList;

    /**
     * 券面总额
     */
    private BigDecimal totalFaceValue;

    /**
     * 是否含权债
     */
    private String isRight;

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
     * 交易市场
     */
    private String tradeMarketModeText;

    /**
     * 交易平台
     */
    private String tradePlatText;

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
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;
    /**
     * 经手费
     */
    private BigDecimal exchangeAmt;
    
    /**
     * 交易所资金账户可用现金
     */
    private BigDecimal tradeMarketCash;

    public BigDecimal getExchangeAmt() {
        return exchangeAmt;
    }

    public void setExchangeAmt(BigDecimal exchangeAmt) {
        this.exchangeAmt = exchangeAmt;
    }
    //---add by 裴家俊 2019/03/28---start---------
    /**
     * 结算费
     */
    private BigDecimal settleAmt;
    /**
     * 结算服务费费率
     */
    private BigDecimal settleAmtRate;
    /**
     * 交易手续费
     */
    private BigDecimal procedureAmt;

    /**
     * 交易手续费费率
     */
    private BigDecimal procedureAmtRate;
    
    /**
     * 佣金
     */
    private BigDecimal commissionAmt;

    /**
     * 结算服务费
     */
    private BigDecimal settleServiceAmt;

    public BigDecimal getSettleServiceAmt() {
        return settleServiceAmt;
    }

    public void setSettleServiceAmt(BigDecimal settleServiceAmt) {
        this.settleServiceAmt = settleServiceAmt;
    }

    /**
     * 印花税
     */
    private BigDecimal stampDutyAmt;
    /**
     * 佣金费率
     */
    private BigDecimal commissionAmtRate;

    // add by wangguoxian 费用设置 20180808 start
    /**
     * 佣金是否随交易支付
     */
    private String isPayWithTrade;
    // add by wangguoxian 费用设置 20180808 end

 // add by wangpeng 费用设置 20180818 start
    /**
     * 印花税是否随交易支付
     */
    private String stampDutyIsPayWithTrade;
    // add by wangpeng 费用设置 20180818 end

    private String settleIsPayWithTrade;

    private String exchangeIsPayWithTrade;

    /**
     * 本方
     */
    private String ouPartyId;
    /**
     * 本方交易员
     */
    private String ouTraderId;

    /**
     *成交编号
     * @return
     */
    private String execid;
    /**
     *资产代码
     * @return
     */
    private String finprodMarketId;
    /**
     *资产简称
     * @return
     */
    private String finprodAbbr;

    /**
     *券面总额
     * @return
     */
    private BigDecimal qty;

    /**
     * 本方Str
     * @return
     */
    private String ouPartyIdStr;
    /**
     * 本方交易员Str
     * @return
     */
    private String ouTraderIdStr;
    /**
     * 对方Str
     * @return
     */
    private String counterIdStr;
    /**
     * 对方交易员Str
     * @return
     */
    private String counterTraderStr;

    public String getSettleIsPayWithTrade() {
        return settleIsPayWithTrade;
    }

    public void setSettleIsPayWithTrade(String settleIsPayWithTrade) {
        this.settleIsPayWithTrade = settleIsPayWithTrade;
    }

    public String getExchangeIsPayWithTrade() {
        return exchangeIsPayWithTrade;
    }

    public void setExchangeIsPayWithTrade(String exchangeIsPayWithTrade) {
        this.exchangeIsPayWithTrade = exchangeIsPayWithTrade;
    }

    /**
     * 过户手续费
     */
    private BigDecimal transferAmt;

    /**
     * 交易金额(原交易金额被券面总额所用)
     */
    private BigDecimal tradeAmtAndFee;

    /**
     * 本方券券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal ownTradeAmt;

    /**
     * 融入券券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal integrateTradeAmt;

    /**
     * 本方券应计利息
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal ownIntAmt;

    /**
     * 融入券应计利息
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal integrateIntAmt;

    /**
     * 本方券全价总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal ownFpriceAmt;

    /**
     * 融入券全价总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal integrateFpriceAmt;

    /**
     * 融入券净价总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal integrateCpriceAmt;

    /**
     * 交易费用LIST
     */
    private List<TrdTradeFeeDetail> tradeFeeList;

    //---add by 裴家俊 2019/03/28---end---------


    // 通道交易 新增 强成西 2019/03/26 start
    /**
     * 通道费
     */
    private BigDecimal channelFee;

    /**
     * 通道性质
     */
    private String payType;
    
    /**
     * 通道收取方式
     */
    private String collectType;

    /**
     * 可用资产实体类
     */
    private BussTrdAvailableCashQuery bussTrdAvailableCashQuery;
    /**
     * 交易对手类型
     */
    private String counterType;

    //交易预录入
    private String preTradeId;

    /**
     * 债券成交回报信息
     */
    private InterfTradSecdealDto interfTradSecdeal;

    /**
     * 返款费率
     */
    private BigDecimal refundRate;
    /**
     * 返款金额
     */
    private BigDecimal refundAmount;


    /**
     *业务模式
     */
    private String businessModel;


    public String getPreTradeId() {
        return preTradeId;
    }

    public void setPreTradeId(String preTradeId) {
        this.preTradeId = preTradeId;
    }

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
     * 债券一级市场缴款页面标识  新增 葛佳傲 2019/7/26
     */
    private String bondFirstSign;

    /**
     * 投管二级分类 新增 wangpeng 2019/8/16
     */
    private String type6;   

    /**新增资产二、三、四级分类	王贵奇	2019/7/30 开始*/   
    
    /**
     * 资产二级类型
     */
    private String type9;    
    
    /**
     * 资产三级类型
     */
    private String type10;   
    
    /**
     * 资产四级类型
     */
    private String type11;
 //增加获取，设置偏离度 by赵东东 2019.9.24 start
    /**
     * 获取偏离度
     * @return
     */
 public BigDecimal getDeviation() {
		return deviation;
	}
     /**
      * 设置偏离度
      * @param deviation
      */
	public void setDeviation(BigDecimal deviation) {
		this.deviation = deviation;
	}
 //增加获取，设置偏离度 by赵东东 2019.9.24 end
	// 增加偏离度by赵东东 2019.9.23 start
    /**
     * 偏离度
     */
    private BigDecimal deviation;
    // 增加偏离度by赵东东 2019.9.23 end
    /**
     * QUERRYPARTY返回的发送murex的交易对手
     */
    private String mxCounterId;


    private BigDecimal bussTrdAvailableCashQueryavalibleCash;

    /**
     * 融入券卖空可用持仓
     */
    private BigDecimal assetIntegrateSaleAvailablePosition;

    /**
     * 本方券可用持仓
     */
    private BigDecimal assetOwnAvailablePosition;

    /**
     * 融入券可用持仓
     */
    private BigDecimal assetIntegrateAvailablePosition;

    /**
     * 授信来源
     */
    private String creditSource;
    
    private RiskCreditListCommonDto riskCreditListCommonDto;

    /**
     * 投标一级市场债券
     */
    private String oFinprodId;

    /**
     * 投标编号
     */
    private String bidId;

    /**
     *投标标位
     */
    private BigDecimal bidPrice;
    /**
     * 清算状态
     */
    private String cleanStatus;
    /**
     * 交收状态
     */
    private String settleStatus;
    /**
     * 有效状态
     */
    private String effectiveStatus;




    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public BigDecimal getBussTrdAvailableCashQueryavalibleCash() {
        return bussTrdAvailableCashQueryavalibleCash;
    }

    public void setBussTrdAvailableCashQueryavalibleCash(BigDecimal bussTrdAvailableCashQueryavalibleCash) {
        this.bussTrdAvailableCashQueryavalibleCash = bussTrdAvailableCashQueryavalibleCash;
    }

    /**
     * 获取 assetIntegrateSaleAvailablePosition
     *
     * @return assetIntegrateSaleAvailablePosition
     */
    public BigDecimal getAssetIntegrateSaleAvailablePosition() {
        return assetIntegrateSaleAvailablePosition;
    }

    /**
     * 设置 assetIntegrateSaleAvailablePosition
     *
     * @param assetIntegrateSaleAvailablePosition assetIntegrateSaleAvailablePosition
     */
    public void setAssetIntegrateSaleAvailablePosition(BigDecimal assetIntegrateSaleAvailablePosition) {
        this.assetIntegrateSaleAvailablePosition = assetIntegrateSaleAvailablePosition;
    }

    /**
     * 获取 assetOwnAvailablePosition
     *
     * @return assetOwnAvailablePosition
     */
    public BigDecimal getAssetOwnAvailablePosition() {
        return assetOwnAvailablePosition;
    }

    /**
     * 设置 assetOwnAvailablePosition
     *
     * @param assetOwnAvailablePosition assetOwnAvailablePosition
     */
    public void setAssetOwnAvailablePosition(BigDecimal assetOwnAvailablePosition) {
        this.assetOwnAvailablePosition = assetOwnAvailablePosition;
    }

    /**
     * 获取 assetIntegrateAvailablePosition
     *
     * @return assetIntegrateAvailablePosition
     */
    public BigDecimal getAssetIntegrateAvailablePosition() {
        return assetIntegrateAvailablePosition;
    }

    /**
     * 设置 assetIntegrateAvailablePosition
     *
     * @param assetIntegrateAvailablePosition assetIntegrateAvailablePosition
     */
    public void setAssetIntegrateAvailablePosition(BigDecimal assetIntegrateAvailablePosition) {
        this.assetIntegrateAvailablePosition = assetIntegrateAvailablePosition;
    }

    public String getMxCounterId() {
        return mxCounterId;
    }

    public void setMxCounterId(String mxCounterId) {
        this.mxCounterId = mxCounterId;
    }

    public String getType9() {
		return type9;
	}

	public void setType9(String type9) {
		this.type9 = type9;
	}

	public String getType10() {
		return type10;
	}

	public void setType10(String type10) {
		this.type10 = type10;
	}

	public String getType11() {
		return type11;
	}

	public void setType11(String type11) {
		this.type11 = type11;
	}

	/**新增资产二、三、四级分类	王贵奇	2019/7/30 结束*/
    
    /**
     * 获取 payType
     *
     * @return payType
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 设置 payType
     *
     * @param payType payType
     */
    public void setPayType(String payType) {
        this.payType = payType;
    }

    /**
     * 获取 channelFee
     *
     * @return channelFee
     */
    public BigDecimal getChannelFee() {
        return channelFee;
    }

    /**
     * 设置 channelFee
     *
     * @param channelFee channelFee
     */
    public void setChannelFee(BigDecimal channelFee) {
        this.channelFee = channelFee;
    }
    // 通道交易 新增 强成西 2019/03/26 end
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

    public String getBondFirstSign() {
        return bondFirstSign;
    }

    public void setBondFirstSign(String bondFirstSign) {
        this.bondFirstSign = bondFirstSign;
    }

    /**
     * 取得 交易日期
     *
     * @return 交易日期
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.tradeDate")
    @Override
    public Date getTradeDate() {
        return super.getTradeDate();
    }

    /**
     * 取得 交割日期
     *
     * @return 交割日期
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.settleDate")
    @Override
    public Date getSettleDate() {
        return super.getSettleDate();
    }

    /**
     * 取得 交易方向，买入、卖出、收息、还本、申购、赎回等
     *
     * @return 交易方向，买入、卖出、收息、还本、申购、赎回等
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.ps")
    @Override
    public String getPs() {
        return super.getPs();
    }

    /**
     * 取得 金融产品代码
     *
     * @return 金融产品代码
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.finprodId")
    @Override
    public String getFinprodId() {
        return super.getFinprodId();
    }

    /**
     * 取得 交易场所，银行间、上交所、深交所、柜面等
     *
     * @return 交易场所，银行间、上交所、深交所、柜面等
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.tradeMarket")
    @Override
    public String getTradeMarket() {
        return super.getTradeMarket();
    }

    /**
     * 取得 交易市场，一级市场、二级市场
     *
     * @return 交易市场，一级市场、二级市场
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.tradeMarketMode", groups = ValidateGroupEdit.class )
    @Override
    public String getTradeMarketMode() {
        return super.getTradeMarketMode();
    }

    /**
     * 取得 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     *
     * @return 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    // 交易所债一级市场时，交易平台非必填 delete by renping 20191105 start
    // @NotEmpty(fieldName = "TrdBondInvestTrade002.tradePlat", groups = ValidateGroupEdit.class )
    // 交易所债一级市场时，交易平台非必填 add by renping 20191105 end
    @Override
    public String getTradePlat() {
        return super.getTradePlat();
    }

    // /**
    // * 取得 投资目的
    // *
    // * @return 投资目的
    // */
    // @NotEmpty(fieldName = "TrdBondInvestTrade002.invAim")
    // @Override
    // public String getInvAim() {
    // return super.getInvAim();
    // }

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

//    /**
//     * 取得 交易金额
//     *
//     * @return 交易金额
//     */
//    @NotEmpty(fieldName = "TrdBondInvestTrade002.tradeAmt")
//    @Override
//    public BigDecimal getTradeAmt() {
//        return super.getTradeAmt();
//    }

    /**
     * 取得 单位净价
     *
     * @return 单位净价
     */
    @NotEmpty(fieldName = "TrdBondInvestTrade002.unitCprice")
    @Override
    public BigDecimal getUnitCprice() {
        return super.getUnitCprice();
    }

    /**
     * 取得 到期收益率
     *
     * @return 到期收益率
     */
//    @NotEmpty(fieldName = "TrdBondInvestTrade002.yield")
    @Override
    public BigDecimal getYield() {
        return super.getYield();
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
    @Override
    @NotEmpty(fieldName = "TrdBondInvestTrade002.portfolioId")
    public String getPortfolioId() {
        return super.getPortfolioId();
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
     * 获取 ptlTrade
     *
     * @return ptlTrade
     */
    public List<PtlTrade> getPtlTrade() {
        return ptlTrade;
    }

    /**
     * 设置 ptlTrade
     *
     * @param ptlTrade ptlTrade
     */
    public void setPtlTrade(List<PtlTrade> ptlTrade) {
        this.ptlTrade = ptlTrade;
    }

    /**
     * 获取 trdProductDeals
     *
     * @return trdProductDeals
     */
    public List<TrdProductDeal> getTrdProductDeals() {
        return trdProductDeals;
    }

    /**
     * 设置 trdProductDeals
     *
     * @param trdProductDeals trdProductDeals
     */
    public void setTrdProductDeals(List<TrdProductDeal> trdProductDeals) {
        this.trdProductDeals = trdProductDeals;
    }

    /**
     * 获取 trdProductDealAdds
     *
     * @return trdProductDealAdds
     */
    public List<TrdProductDealAdd> getTrdProductDealAdds() {
        return trdProductDealAdds;
    }

    /**
     * 设置 trdProductDealAdds
     *
     * @param trdProductDealAdds trdProductDealAdds
     */
    public void setTrdProductDealAdds(List<TrdProductDealAdd> trdProductDealAdds) {
        this.trdProductDealAdds = trdProductDealAdds;
    }

    /**
     * 获取 paTrdProductDeals
     *
     * @return paTrdProductDeals
     */
    public List<PaTrdProductDeal> getPaTrdProductDeals() {
        return paTrdProductDeals;
    }

    /**
     * 设置 paTrdProductDeals
     *
     * @param paTrdProductDeals paTrdProductDeals
     */
    public void setPaTrdProductDeals(List<PaTrdProductDeal> paTrdProductDeals) {
        this.paTrdProductDeals = paTrdProductDeals;
    }

    /**
     * 获取 paTrdFinProductDealTotal
     *
     * @return paTrdFinProductDealTotal
     */
    public PaTrdFinProductDealTotal getPaTrdFinProductDealTotal() {
        return paTrdFinProductDealTotal;
    }

    /**
     * 设置 paTrdFinProductDealTotal
     *
     * @param paTrdFinProductDealTotal paTrdFinProductDealTotal
     */
    public void setPaTrdFinProductDealTotal(PaTrdFinProductDealTotal paTrdFinProductDealTotal) {
        this.paTrdFinProductDealTotal = paTrdFinProductDealTotal;
    }

    /**
     * 获取 windtypeone
     *
     * @return windtypeone
     */
    public String getWindtypeone() {
        return windtypeone;
    }

    /**
     * 设置 windtypeone
     *
     * @param windtypeone windtypeone
     */
    public void setWindtypeone(String windtypeone) {
        this.windtypeone = windtypeone;
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
     * 获取 totalFaceValue
     *
     * @return totalFaceValue.
     */
    public BigDecimal getTotalFaceValue() {
        return totalFaceValue;
    }

    /**
     * 设置 totalFaceValue
     *
     * @param totalFaceValue totalFaceValue
     */
    public void setTotalFaceValue(BigDecimal totalFaceValue) {
        this.totalFaceValue = totalFaceValue;
    }

    /**
     * 获取 isRight
     *
     * @return isRight.
     */
    public String getIsRight() {
        return isRight;
    }

    /**
     * 设置 isRight
     *
     * @param isRight isRight
     */
    public void setIsRight(String isRight) {
        this.isRight = isRight;
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
        psText = SysDataUtil.getNameFromDataDict(DictCode.交易方向, this.getPs());
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
     * 获取 tradeMarketModeText
     *
     * @return tradeMarketModeText.
     */
    public String getTradeMarketModeText() {
        tradeMarketModeText = SysDataUtil.getNameFromDataDict(DictCode.交易市场,
            this.getTradeMarketMode());
        return tradeMarketModeText;
    }

    /**
     * 设置 tradeMarketModeText
     *
     * @param tradeMarketModeText tradeMarketModeText
     */
    public void setTradeMarketModeText(String tradeMarketModeText) {
        this.tradeMarketModeText = tradeMarketModeText;
    }

    /**
     * 获取 tradePlatText
     *
     * @return tradePlatText.
     */
    public String getTradePlatText() {
        tradePlatText = SysDataUtil.getNameFromDataDict(DictCode.交易平台,
            this.getTradePlat());
        return tradePlatText;
    }

    /**
     * 设置 tradePlatText
     *
     * @param tradePlatText tradePlatText
     */
    public void setTradePlatText(String tradePlatText) {
        this.tradePlatText = tradePlatText;
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
     * 
     * 获取结算费
     * @return 结算费
     * @see
     * @since 1.0
     */
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    /**
     * 
     * 设置结算费
     * @param settleAmt 结算费
     * @see
     * @since 1.0
     */
    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    /**
     * 
     * 获取交易手续费
     * @return 交易手续费
     * @see
     * @since 1.0
     */
    public BigDecimal getProcedureAmt() {
        return procedureAmt;
    }

    /**
     * 
     * 设置交易手续费
     * @param procedureAmt 交易手续费
     * @see
     * @since 1.0
     */
    public void setProcedureAmt(BigDecimal procedureAmt) {
        this.procedureAmt = procedureAmt;
    }

    /**
     * 
     * 获取交易手续费费率
     * @return 交易手续费费率
     * @see
     * @since 1.0
     */
    public BigDecimal getProcedureAmtRate() {
        return procedureAmtRate;
    }

    /**
     * 
     * 设置交易手续费费率
     * @param procedureAmtRate 交易手续费费率
     * @see
     * @since 1.0
     */
    public void setProcedureAmtRate(BigDecimal procedureAmtRate) {
        this.procedureAmtRate = procedureAmtRate;
    }

    /**
     * 
     * 获取佣金
     * @return 佣金
     * @see
     * @since 1.0
     */
    public BigDecimal getCommissionAmt() {
        return commissionAmt;
    }

    /**
     * 
     * 设置佣金
     * @param commissionAmt 佣金
     * @see
     * @since 1.0
     */
    public void setCommissionAmt(BigDecimal commissionAmt) {
        this.commissionAmt = commissionAmt;
    }

    /**
     * 
     * 获取佣金费率
     * @return 佣金费率
     * @see
     * @since 1.0
     */
    public BigDecimal getCommissionAmtRate() {
        return commissionAmtRate;
    }

    // add by wangguoxian 费用设置 20180808 start
    /**
     * 获取佣金是否随交易支付
     * @return 佣金是否随交易支付
     */
    public String getIsPayWithTrade() {
        return isPayWithTrade;
    }

    /**
     * 设置佣金是否随交易支付
     * @param isPayWithTrade 佣金是否随交易支付
     */
    public void setIsPayWithTrade(String isPayWithTrade) {
        this.isPayWithTrade = isPayWithTrade;
    }
    // add by wangguoxian 费用设置 20180808 end

    /**
     * 
     * 设置佣金费率
     * @param commissionAmtRate 佣金费率
     * @see
     * @since 1.0
     */
    public void setCommissionAmtRate(BigDecimal commissionAmtRate) {
        this.commissionAmtRate = commissionAmtRate;
    }

    /**
     * 
     * 获取过户手续费
     * @return 过户手续费
     * @see
     * @since 1.0
     */
    public BigDecimal getTransferAmt() {
        return transferAmt;
    }

    /**
     * 
     * 设置过户手续费
     * @param transferAmt 过户手续费
     * @see
     * @since 1.0
     */
    public void setTransferAmt(BigDecimal transferAmt) {
        this.transferAmt = transferAmt;
    }

    /**
     * 
     * 获取交易金额(原交易金额被券面总额所用)
     * @return 交易金额(原交易金额被券面总额所用)
     * @see
     * @since 1.0
     */
    public BigDecimal getTradeAmtAndFee() {
        return tradeAmtAndFee;
    }

    /**
     * 
     * 设置交易金额(原交易金额被券面总额所用)
     * @param tradeAmtAndFee 交易金额(原交易金额被券面总额所用)
     * @see
     * @since 1.0
     */
    public void setTradeAmtAndFee(BigDecimal tradeAmtAndFee) {
        this.tradeAmtAndFee = tradeAmtAndFee;
    }

    /**
     * 
     * 获取交易费用LIST
     * @return 交易费用LIST
     * @see
     * @since 1.0
     */
    public List<TrdTradeFeeDetail> getTradeFeeList() {
        return tradeFeeList;
    }

    /**
     * 
     * 设置交易费用LIST
     * @param tradeFeeList 交易费用LIST
     * @see
     * @since 1.0
     */
    public void setTradeFeeList(List<TrdTradeFeeDetail> tradeFeeList) {
        this.tradeFeeList = tradeFeeList;
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
	private List<TrdTradeFeeTotal> trdTradeFeeTotals;

	/**
	 * 汇总费用明细表
	 */
	private List<TrdTradeFeeDetail> trdTradeFeeDetails;

    /**
     * 选中的数据
     */
    private transient List<CfetsTrdBondInvestTradeDto> selectedItem;

    /**
     * 获取 trdTradeFeeTotals
     *
     * @return trdTradeFeeTotals
     */
    public List<TrdTradeFeeTotal> getTrdTradeFeeTotals() {
        return trdTradeFeeTotals;
    }

    /**
     * 设置 trdTradeFeeTotals
     *
     * @param trdTradeFeeTotals trdTradeFeeTotals
     */
    public void setTrdTradeFeeTotals(List<TrdTradeFeeTotal> trdTradeFeeTotals) {
        this.trdTradeFeeTotals = trdTradeFeeTotals;
    }

    /**
     * 获取 trdTradeFeeDetails
     *
     * @return trdTradeFeeDetails
     */
    public List<TrdTradeFeeDetail> getTrdTradeFeeDetails() {
        return trdTradeFeeDetails;
    }

    /**
     * 设置 trdTradeFeeDetails
     *
     * @param trdTradeFeeDetails trdTradeFeeDetails
     */
    public void setTrdTradeFeeDetails(List<TrdTradeFeeDetail> trdTradeFeeDetails) {
        this.trdTradeFeeDetails = trdTradeFeeDetails;
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

	public String getType6() {
		return type6;
	}

	public void setType6(String type6) {
		this.type6 = type6;
	}

	public String getStampDutyIsPayWithTrade() {
		return stampDutyIsPayWithTrade;
	}

	public void setStampDutyIsPayWithTrade(String stampDutyIsPayWithTrade) {
		this.stampDutyIsPayWithTrade = stampDutyIsPayWithTrade;
	}

	public BigDecimal getStampDutyAmt() {
		return stampDutyAmt;
	}

	public void setStampDutyAmt(BigDecimal stampDutyAmt) {
		this.stampDutyAmt = stampDutyAmt;
	}



    /**
     * 获取 actPortfolioId
     *
     * @return actPortfolioId
     */
    public String getActPortfolioId() {
        return actPortfolioId;
    }

    /**
     * 设置 actPortfolioId
     *
     * @param actPortfolioId actPortfolioId
     */
    public void setActPortfolioId(String actPortfolioId) {
        this.actPortfolioId = actPortfolioId;
    }

	public String getCollectType() {
		return collectType;
	}

	public void setCollectType(String collectType) {
		this.collectType = collectType;
	}

    /**
     * 获取 ownTradeAmt
     *
     * @return ownTradeAmt
     */
    @NotEmpty(fieldName = "EtrInvestBondInfo002.entrustOwnNum")
    public BigDecimal getOwnTradeAmt() {
        return ownTradeAmt;
    }

    /**
     * 设置 ownTradeAmt
     *
     * @param ownTradeAmt ownTradeAmt
     */
    public void setOwnTradeAmt(BigDecimal ownTradeAmt) {
        this.ownTradeAmt = ownTradeAmt;
    }

    /**
     * 获取 integrateTradeAmt
     *
     * @return integrateTradeAmt
     */
    @NotEmpty(fieldName = "EtrInvestBondInfo002.entrustIntegrateNum")
    public BigDecimal getIntegrateTradeAmt() {
        return integrateTradeAmt;
    }

    /**
     * 设置 integrateTradeAmt
     *
     * @param integrateTradeAmt integrateTradeAmt
     */
    public void setIntegrateTradeAmt(BigDecimal integrateTradeAmt) {
        this.integrateTradeAmt = integrateTradeAmt;
    }

    /**
     * 获取 ownIntAmt
     *
     * @return ownIntAmt
     */
    public BigDecimal getOwnIntAmt() {
        return ownIntAmt;
    }

    /**
     * 设置 ownIntAmt
     *
     * @param ownIntAmt ownIntAmt
     */
    public void setOwnIntAmt(BigDecimal ownIntAmt) {
        this.ownIntAmt = ownIntAmt;
    }

    /**
     * 获取 integrateIntAmt
     *
     * @return integrateIntAmt
     */
    public BigDecimal getIntegrateIntAmt() {
        return integrateIntAmt;
    }

    /**
     * 设置 integrateIntAmt
     *
     * @param integrateIntAmt integrateIntAmt
     */
    public void setIntegrateIntAmt(BigDecimal integrateIntAmt) {
        this.integrateIntAmt = integrateIntAmt;
    }

    /**
     * 获取 ownFpriceAmt
     *
     * @return ownFpriceAmt
     */
    public BigDecimal getOwnFpriceAmt() {
        return ownFpriceAmt;
    }

    /**
     * 设置 ownFpriceAmt
     *
     * @param ownFpriceAmt ownFpriceAmt
     */
    public void setOwnFpriceAmt(BigDecimal ownFpriceAmt) {
        this.ownFpriceAmt = ownFpriceAmt;
    }

    /**
     * 获取 integrateFpriceAmt
     *
     * @return integrateFpriceAmt
     */
    public BigDecimal getIntegrateFpriceAmt() {
        return integrateFpriceAmt;
    }

    /**
     * 设置 integrateFpriceAmt
     *
     * @param integrateFpriceAmt integrateFpriceAmt
     */
    public void setIntegrateFpriceAmt(BigDecimal integrateFpriceAmt) {
        this.integrateFpriceAmt = integrateFpriceAmt;
    }

    /**
     * 获取 integrateCpriceAmt
     *
     * @return integrateCpriceAmt
     */
    public BigDecimal getIntegrateCpriceAmt() {
        return integrateCpriceAmt;
    }

    /**
     * 设置 integrateCpriceAmt
     *
     * @param integrateCpriceAmt integrateCpriceAmt
     */
    public void setIntegrateCpriceAmt(BigDecimal integrateCpriceAmt) {
        this.integrateCpriceAmt = integrateCpriceAmt;
    }

    public String getAssetRecommandOrg() {
        return assetRecommandOrg;
    }

    public void setAssetRecommandOrg(String assetRecommandOrg) {

        this.assetRecommandOrg = assetRecommandOrg;
    }
    public String getZhilingtype() {
        return zhilingtype;
    }

    public void setZhilingtype(String zhilingtype) {
        this.zhilingtype = zhilingtype;
    }

    public List<BussTrdTransfenpeiDto> getBussTrdTransfenpeiDto() {
        return bussTrdTransfenpeiDto;
    }

    public void setBussTrdTransfenpeiDto(List<BussTrdTransfenpeiDto> bussTrdTransfenpeiDto) {
        this.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
    }

    /**
     * 获取 trdDealTotalAdd
     *
     * @return trdDealTotalAdd
     */
    public TrdDealTotalAdd getTrdDealTotalAdd() {
        return trdDealTotalAdd;
    }

    /**
     * 设置 trdDealTotalAdd
     *
     * @param trdDealTotalAdd trdDealTotalAdd
     */
    public void setTrdDealTotalAdd(TrdDealTotalAdd trdDealTotalAdd) {
        this.trdDealTotalAdd = trdDealTotalAdd;
    }
	
		
	public String turnInvAim;

    
    /**
     * 获取 turnInvAim
     * @return turnInvAim.
     */
    public String getTurnInvAim() {
        return turnInvAim;
    }

    
    /**
     * 设置 turnInvAim
     * @param turnInvAim turnInvAim
     */
    public void setTurnInvAim(String turnInvAim) {
        this.turnInvAim = turnInvAim;
    }

    public RiskCreditListCommonDto getRiskCreditListCommonDto() {
        return riskCreditListCommonDto;
    }

    public void setRiskCreditListCommonDto(RiskCreditListCommonDto riskCreditListCommonDto) {
        this.riskCreditListCommonDto = riskCreditListCommonDto;
    }

    public String getCreditSource() {
        return creditSource;
    }

    public void setCreditSource(String creditSource) {
        this.creditSource = creditSource;
    }
    // 合并 by 华兴 start
    /**
     * 金融产品交易附属
     */
    private TrdProductDealAdd trdProductDealAdd;
    /**
     * 金融产品交易业务流程表
     */
    private WflTrdProductDealAdd wflTrdProductDealAdd;

    /**
     * 金融产品交易扩展
     */
    private PaTrdProductDeal paTrdProductDeal;
    /**
     * 待转让本金
     */
    private BigDecimal assignPrinamt;
    /**
     * 待转让利息
     */
    private BigDecimal assignIntamt;
    /**
     * 组合分配
     */
    List<PortIntFenPeiInfoDto> cfetsBussCombDistribtList;

    public TrdProductDealAdd getTrdProductDealAdd() {
        return trdProductDealAdd;
    }

    public void setTrdProductDealAdd(TrdProductDealAdd trdProductDealAdd) {
        this.trdProductDealAdd = trdProductDealAdd;
    }

    public WflTrdProductDealAdd getWflTrdProductDealAdd() {
        return wflTrdProductDealAdd;
    }

    public void setWflTrdProductDealAdd(WflTrdProductDealAdd wflTrdProductDealAdd) {
        this.wflTrdProductDealAdd = wflTrdProductDealAdd;
    }

    public PaTrdProductDeal getPaTrdProductDeal() {
        return paTrdProductDeal;
    }

    public void setPaTrdProductDeal(PaTrdProductDeal paTrdProductDeal) {
        this.paTrdProductDeal = paTrdProductDeal;
    }

    public BigDecimal getSettleAmtRate() {
        return settleAmtRate;
    }

    public void setSettleAmtRate(BigDecimal settleAmtRate) {
        this.settleAmtRate = settleAmtRate;
    }

    public BigDecimal getAssignPrinamt() {
        return assignPrinamt;
    }

    public void setAssignPrinamt(BigDecimal assignPrinamt) {
        this.assignPrinamt = assignPrinamt;
    }

    public BigDecimal getAssignIntamt() {
        return assignIntamt;
    }

    public void setAssignIntamt(BigDecimal assignIntamt) {
        this.assignIntamt = assignIntamt;
    }
    // 合并 by 华兴 end

    /**
     * 按钮Id
     */
    private String elementId;

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

	public BigDecimal getTradeMarketCash() {
		return tradeMarketCash;
	}

	public void setTradeMarketCash(BigDecimal tradeMarketCash) {
		this.tradeMarketCash = tradeMarketCash;
	}
	public InterfTradSecdealDto getInterfTradSecdeal() {
        return interfTradSecdeal;
    }

    public void setInterfTradSecdeal(InterfTradSecdealDto interfTradSecdeal) {
        this.interfTradSecdeal = interfTradSecdeal;
    }
	public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

	@PrimaryKey(PrimaryKey.Field.PK_4)
    public String getfTradeId2() {
        return this.getfTradeId();
    }
	public boolean isAutoTradeFlag() {
        return autoTradeFlag;
    }

    public void setAutoTradeFlag(boolean autoTradeFlag) {
        this.autoTradeFlag = autoTradeFlag;
    }

    public BigDecimal getTradeRepAmt() {
        return tradeRepAmt;
    }

    public void setTradeRepAmt(BigDecimal tradeRepAmt) {
        this.tradeRepAmt = tradeRepAmt;
    }

    public boolean isImportFlag() {
        return importFlag;
    }

    public void setImportFlag(boolean importFlag) {
        this.importFlag = importFlag;
    }

    public String getSecAcctSource() {
        return secAcctSource;
    }

    public void setSecAcctSource(String secAcctSource) {
        this.secAcctSource = secAcctSource;
    }

    public String getIsOfferFlag() {
        return isOfferFlag;
    }

    public void setIsOfferFlag(String isOfferFlag) {
        this.isOfferFlag = isOfferFlag;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public BigDecimal getRefundRate() {
        return refundRate;
    }

    public void setRefundRate(BigDecimal refundRate) {
        this.refundRate = refundRate;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getoFinprodId() {
        return oFinprodId;
    }

    public void setoFinprodId(String oFinprodId) {
        this.oFinprodId = oFinprodId;
    }

    public String getBidId() {
        return bidId;
    }


    public void setBidId(String bidId) {
        this.bidId = bidId;
    }
     public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public CfetsTrdInvestPersonInfoDto getTrdInvestPersonInfo() {
        return trdInvestPersonInfo;
    }

    public void setTrdInvestPersonInfo(CfetsTrdInvestPersonInfoDto trdInvestPersonInfo) {
        this.trdInvestPersonInfo = trdInvestPersonInfo;
    }

    public String getOuPartyId() {
        return ouPartyId;
    }

    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }

    public String getOuTraderId() {
        return ouTraderId;
    }

    public void setOuTraderId(String ouTraderId) {
        this.ouTraderId = ouTraderId;
    }

    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public List<CfetsTrdBondInvestTradeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsTrdBondInvestTradeDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getCleanStatus() {
        return cleanStatus;
    }

    public void setCleanStatus(String cleanStatus) {
        this.cleanStatus = cleanStatus;
    }

    public String getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus;
    }

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public List<PortIntFenPeiInfoDto> getCfetsBussCombDistribtList() {
        return cfetsBussCombDistribtList;
    }

    public void setCfetsBussCombDistribtList(List<PortIntFenPeiInfoDto> cfetsBussCombDistribtList) {
        this.cfetsBussCombDistribtList = cfetsBussCombDistribtList;
    }

    public String getOuPartyIdStr() {
        return ouPartyIdStr;
    }

    public void setOuPartyIdStr(String ouPartyIdStr) {
        this.ouPartyIdStr = ouPartyIdStr;
    }

    public String getOuTraderIdStr() {
        return ouTraderIdStr;
    }

    public void setOuTraderIdStr(String ouTraderIdStr) {
        this.ouTraderIdStr = ouTraderIdStr;
    }

    public String getCounterIdStr() {
        return counterIdStr;
    }

    public void setCounterIdStr(String counterIdStr) {
        this.counterIdStr = counterIdStr;
    }

    public String getCounterTraderStr() {
        return counterTraderStr;
    }

    public void setCounterTraderStr(String counterTraderStr) {
        this.counterTraderStr = counterTraderStr;
    }
}

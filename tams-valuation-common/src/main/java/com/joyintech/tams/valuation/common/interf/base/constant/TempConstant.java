/*
 * 文件名：TempConstant.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：qiangchengxi
 * 修改时间：2018年6月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.common.interf.base.constant;

/**
 * 定义批次/模板名称变量
 * @author wanwgei1 
 * @version 1.0 2018年6月22日
 * @see TempConstant
 * @since 1.0
 */
public class TempConstant {
    
    /**
     * 接口数据entity包路径
     */
    public static final String  ENTITY_PATH = "com.joyintech.tams.valuation.common.interf.tempTables.entity.";
    
    /**
     * 基础数据接口
     */
    public static final String  净值型产品基础信息表 = "ValPrdInfoN";
    public static final String  净值型产品开放规则表 = "ValPrdOpenRule";
    public static final String  净值型产品开放计划表 = "ValPrdOpenPlanN";
    public static final String  净值型产品费用基础信息表 = "ValPrdFeeInfoN";
    public static final String  净值型产品费用计提规则表 = "ValPrdFloatFeeN";
    public static final String  净值型产品业绩报酬计算规则 = "ValFloatCalcRuleN";
    public static final String  净值型产品浮动管理费计算明细规则表 = "ValPrdFloatFeeDetailN";
    public static final String  净值型产品付费计划表 = "ValPrdPayPlanN";
    public static final String  净值型产品费用计提计划表 = "ValPrdFeeCalPlanN";
    public static final String  净值型产品费率调整信息表 = "ValPrdRateAdjustN";
    public static final String  净值型产品业绩基准信息表 = "ValPrdBenchmarkDetailN";
    public static final String  净值型产品业绩回补规则 = "ValPrdCawlbackCalcRuleN";
    
    public static final String  存款类资产基础信息  = "ValBotAstInfo";
    public static final String  存款类资产利率调整  = "ValBotAstRateAdjust";
    public static final String  存款类资产付息计划  = "ValBotAstRatePlan";
    public static final String  存款类资产还本计划  = "ValBotAstCapPlan";
    public static final String  存款类资产还本付息规则  = "ValBotAstRepayCapInt";
 
    
    // 基金类资产
    public static final String 基金类资产定义货币基金基础信息 = "ValFundMnfBaseInfo"; 
    public static final String 基金类资产定义货币基金付息计划 = "ValFundMnfPayPlan";
    public static final String 基金类资产定义货币基金付息规则 = "ValFundMnfPayRule";
    public static final String 基金类资产定义净值型基金基础信息 = "ValFundNBaseInfoN";
    
    /**
     * 负债端交易接口
     */
    public static final String  净值型产品存续期交易表  = "ValPrdPeriodDealN";
    public static final String  净值型产品分红交易表 = "ValPrdBonusDealN";
    public static final String  净值型产品费用交易表 = "ValPrdFeeDealN";
    public static final String  净值型产品分红除权表 = "ValPrdDelDealN";
    
    /**
     * 产品交易数据接口
     */
    public static final String  货币型产品存续期交易表 = "ValPrdPeriodDealC";
    public static final String  货币型产品分红交易表 = "ValPrdBonusDealC";
    public static final String  货币型产品费用交易表 = "ValPrdFeeDealC";
    /**
     * 资产端交易接口
     */
    public static final String  存款类资产存款交易  = "ValBotAstTrade";
    public static final String  存款类资产存款存续期表  = "ValBotPeriodFund";
    public static final String  存款类资产应收款项转让交易  = "ValBotReceicableTrade";
    public static final String  债券买卖交易  = "ValSecVestAssetDeal";
    public static final String  债券存续期交易  = "ValSecVestPeriod";
    public static final String  债券应收款项转让交易  = "ValSecReceicableTrade";
    public static final String  债券借贷交易 ="ValSeclendDeal";
    public static final String  债券借贷标的券存续期交易="ValSeclendAssetPeriodDeal";
    public static final String  债券借贷标的券卖出与购回="ValSeclendAssetDeal";
    
    //逾期交易
    public static final String  逾期交易  = "ValFxDeal";
    
    
    
    /**
     * 资产端交易接口
     * 基金类资产
     */
    public static final String 买入卖出交易 ="ValFundTrade";
    public static final String 申购申请交易 ="ValApplyPurTade";
    public static final String 申购确认交易 = "ValAffirmPurTrade";
    public static final String 货币基金赎回确认交易 = "ValFundMnfRedAffirm";
    public static final String 净值型基金赎回确认交易 = "ValFundNRedAffirmN";
    public static final String 赎回到账交易 = "ValRedAccoutTrade";
    public static final String 现金分红交易 = "ValCashBonusTrade";
    public static final String 红利再投交易= "ValBonusResTrade";
    /** add by yanya on 20191116 增加基金份额折算交易 start**/
    public static final String 基金份额折算交易 = "ValFundShareConvert";
    /** add by yanya on 20191116 增加基金份额折算交易 end**/
    /** add by tanxuan on 20200110 start**/
    public static final String 基金转换交易 = "ValFundChangeDeal";
    /** add by tanxuan on 20200110 end**/
    
    /**
     * 计划类资产（利率型）
     */
    public static final String  计划类资产利率型基础信息表 = "ValMngAssetInfo";
    public static final String  计划类资产利率型利率调整信息表 = "ValMngRateAdjInfo";
    public static final String  计划类资产利率型还本付息规则表 = "ValMngRepayCapInt";
    public static final String  计划类资产利率型还本计划表 = "ValMngCapPlan";
    public static final String  计划类资产利率型付息计划表 = "ValMngIntPlan";
    public static final String  计划类资产利率型买卖交易表 = "ValMngBuyTrade";
    public static final String  计划类资产利率型存续期交易表 = "ValMngPeriodTrade";
    public static final String  计划类资产利率型应收款项转让交易表 = "ValMngReceicableTrade";
    /**
     * 计划类资产（净值型）
     */
    public static final String  计划类资产净值型基础信息表 = "ValMngAssetInfoN";
    /**
     * 其他基础数据
     */
    public static final String 机构信息 = "ValAgencyInfo";
    public static final String 账户信息 = "ValAccountInfo";
    public static final String 标的账户信息表 = "ValSubjectInfo";
    public static final String 节假日信息表 = "ValHolidayInfo";
    public static final String 交易对手 = "ValMstCounterParty";
    public static final String 评级信息 = "ValMstBondCreditRating";

    /**
     * 交易信息
     */
    public static final String  回购交易  = "ValRepDealSec";
    public static final String  买断式回购派息交易  = "ValRepDividendDeal";
    /**
     *行情数据接口模板
     */
    public static final  String  场外金融产品估值行情="HqParFundInfo";
    public static final  String  中债估值行情="MainTable";
    public static final  String  开放式基金估值="par_openfund_info";
    public static final  String  中证估值行情="bond_valuation";
    public static final  String  债券估值行情="HqBondValuationInfo";
    public static final  String  债券市场行情="HqBondMarketInfo";
    public static final  String  基金市场行情="HqFundMarketInfo";
    public static final  String  利率行情="HqIntRateInfo";
    public static final  String  中债登估值_平安="CBondAnalysisCNBD";  
    public static final  String  中国债券交易所债券行情="CBondEODPrices";
    public static final  String  中国债券行情_净价="Cbondpricesnet";
    public static final  String  中国债券行情_全价="Cbondprices";
    public static final  String  中证估值="Cbondanalysiscsi";
    public static final  String  中国债券上海清算所债券估值="Cbondanalysisshc";
    public static final  String  中国封闭式基金日行情="Chinaclosedfundeodprice";
    public static final  String  债券行权计划行情="ValSecVestPlan";
    public static final  String  基金市场估值="SRC_FUNDVALUE"; 
    public static final  String  债券市价估值="SRC_CBVLT";
    public static final  String  基础利率重置="SRC_INTEREST_RATE";
    public static final  String  咨询债券市场行情="SRC_SHIBORPRICES";
    /**
     * 基础数据接口
     * 债券
     */
    public static final String 债券基础信息="ValSecBaseInfo";
    public static final String 债券利率调整信息 = "ValSecRateAdjust";
    public static final String 债券还本付息规则 = "ValSecPayRateRule";
    public static final String 债券还本付息计划= "ValSecPayRatePlan";
    public static final String 债券行权计划 = "ValSecVestPlan";
    
    /**
     * 拆借标的
     */
    public static final String 拆借交易标的信息= "ValDlTarget";
    
    /**
     * 拆借付息规则
     */
    public static final String 拆借付息规则 = "ValDlTargetRateRule";
    
    /** add by dongjun on 20181127 start **/
    /**
     * 拆借利率调整
     */
    public static final String 拆借利率调整 = "ValDlTargetRateAdjust";
    /** add by dongjun on 20181127 start **/
    
    /**
     * 拆借付息计划
     */
    public static final String 拆借付息计划  = "ValDlTargetRatePlan";
    
    /**
     * 债券回购标的
     */
    public static final String 债券回购标的信息= "ValRepDealSecSubject";
    public static final String 债券回购质押品信息= "ValRepDealAsset";

    
    /**
     * 拆借交易
     */
    public static final String 拆借交易= "ValDlDeal";
    
    /**
     * 拆借收息
     */
    public static final String 拆借收息 = "ValDlRateDeal";
    
    /**
     * 拆借应收款项转让交易
     */
    public static final String 拆借应收款项转让交易 = "ValDlReceicableTrade";
    
    
    /**
     * 货币型产品基础数据接口
     */
    public static final String  货币型产品基础信息表 = "ValPrdInfoC";
    public static final String  货币型产品开放计划表 = "ValPrdOpenPlanC";
    public static final String  货币型产品费用基础信息表 = "ValPrdFeeInfoC";
    public static final String  货币型产品费用计提规则 = "ValPrdFloatFeeC";
    public static final String  货币型产品业绩报酬计算规则 = "ValFloatCalcRuleC";
    public static final String  货币型产品浮动管理费计算明细规则表 = "ValPrdFloatFeeDetailC";
    public static final String  货币型产品付费计划表 = "ValPrdPayPlanC";
    public static final String  货币型产品费用计提计划表 = "ValPrdFeeCalPlanC";
    public static final String  货币型产品费率调整信息表 = "ValPrdRateAdjustC";
    public static final String  货币型产品业绩基准信息表 = "ValPrdBenchmarkDetailC";
    public static final String 	货币型产品业绩回补规则 = "ValPrdCawlbackCalcRuleC";

    /**
     * 预期收益型产品基础数据接口
     */
    public static final String 预期收益型产品基础信息表 = "ValPrdBaseInfo";
    public static final String 预期收益型产品费用基础信息表 = "ValPrdFeeBaseinfo";
    public static final String 预期收益型产品付费计划表 = "ValPrdPayPlan";
    public static final String 预期收益型产品费率调整信息表 = "ValPrdRateAdjustInfo";
    public static final String 预期收益型产品开放计划 = "ValPrdOpenPlan";
    public static final String 预期收益型产品付息规则 = "ValPrdPayRule";
    public static final String 预期收益型产品付息计划 = "ValPrdAccrualPlan";
    public static final String 预期收益型产品费用计提计划 = "ValPrdFeeCalPlan";
    public static final String 预期收益型产品产品报价调整信息 = "ValPrdPriceAdjustInfo";
    public static final String 预期收益型产品费用计提规则="ValPrdFloatFee";
    public static final String 预期收益型产品业绩报酬计算规则="ValFloatCalcRule";
    public static final String 预期收益型产品浮动管理费计算明细规则="ValPrdFloatFeeDetail";
    /**
     * 活期存款利息-交易数据接口
     */
    public static final String 活期存款收息交易 = "ValCurrentDepositDealInt";
    public static final String 活期存款转仓交易 = "ValCurrentDepositDealTrans";
    
    /**
     * 活期存款利息-基础数据接口
     */
    public static final String 活期存款利率信息表 = "ValCurrentDepositRate";
    
    
    /**
     * 增值税交易
     */
    public static final String 增值税交易= "ValAddValueTaxDeal";
    
    /**
     * 预期型产品交易数据接口
     */
    public static final String  预期型产品存续期交易表 = "ValPrdPeriodDeal";
    public static final String  预期型产品费用交易表 = "ValPrdFeeDeal";
    
    /**
     * 费用交易
     */
    public static final String  费用交易表  = "ValFeeDeal";
    
    /**
     * 委外(非公募基金）行情
     */
    public static final String  委外行情  = "ValNfundRate";
    
    /**
     * 债券借贷基础数据接口
     */
    public static final String  债券借贷标的信息  = "ValSeclendInfo";
    public static final String  债券借贷质押债券信息  = "ValSeclendMortgage";
    
    /**
     * 计划类货币型-基础数据接口 
     */
    public static final String  计划类资产货币型基础信息  = "ValMngMnfBaseInfo";
    public static final String  计划类资产货币型分红规则  = "ValMngMnfPayRule";
    public static final String  计划类资产货币型分红计划  = "ValMngMnfPayPlan";
    
    /**
     * 结算指令 add by wujie on 20190123
     */
    public static final String  结算指令  = "ValCleanOrder";
    
    
    public static final String 计划类资产货币型买入卖出交易="ValMngMnfTrade";
    public static final String 计划类资产货币型存续期交易="ValMngMnfCashBonusTrade";
    public static final String 计划类资产货币型申购申请交易="ValMngMnfApplyPurTade";
    public static final String 计划类资产货币型申购确认交易="ValMngMnfAffirmPurTrade";
    public static final String 计划类资产货币型赎回到账交易="ValMngMnfRedAccoutTrade";
    public static final String 计划类资产货币型赎回确认交易="ValMngMnfRedAffirm";
    
    public static final String 产品回补交易="ValPrdSupplementDeal";
    
    /**
     * 通道
     */
    public static final String 通道基础信息="ValChannelBaseInfo";
    public static final String 通道费用基础信息="ValChannelFeeInfo";
    public static final String 通道利率信息="ValChannelRateAdjust";
    
    public static final String 理财产品借通道投资交易="ValChannelProdDeal";
    public static final String 通道产品费用交易="ValChannelProdFeeDeal";

    /**
     * 数据回传
     */
    public static final String  数据回传_增值税流水 = "ValAddTaxFlowBack";
    
    /**
     *费用计提交易
     */
    public static final String 费用计提交易="ValFeeDrawDeal";
 
    /**
     *逾期标的接口     add by zhangquan 20191227
     */
    public static final String 逾期标的基本信息="ValFxTargetInfo";
    public static final String 逾期标的利率信息="ValFxTargetRateAdjust";
    public static final String 逾期标的现金流计划="ValFxTargetPlan";
    
    /**
     *场外期权标的接口     add by tanxuan 20200109
     */
    public static final String 场外期权标的信息="ValOptTradeSubject";
    
    /**
     *场外期权交易     add by tanxuan 20200110
     */
    public static final String 场外期权交易="ValOtcOptTrade";

    /**
     * 债券行权交易
     * add by yanya on 20210309
     */
    public static final String 债券行权交易="ValSecExecDeal";

    /**
     * 股票基础信息
     * add by yanya on 20210827
     */
    public static final String 股票基础信息="ValStockBaseInfo";

    /**
     * 股票行情接口
     * add by yanya on 20210831
     */
    public static final String 股票行情="ValStockQuotationInfo";

    /**
     * 自定义现金流交易
     */
    public static final String 自定义现金流交易= "ValCashflowTrade";

    /**
     * 利率互换标的信息
     */
    public static final String 利率互换标的信息= "ValIrsInfo";
    /**
     * 利率互换付息计划
     */
    public static final String 利率互换付息计划= "ValIrsCashPlan";
    /**
     * 利率互换交易信息
     */
    public static final String 利率互换交易信息= "ValIrsDeal";
    /**
     * 利率互换存续期交易
     */
    public static final String 利率互换存续期交易= "ValIrsPerIodDeal";




}


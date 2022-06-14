package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bidding;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDetailDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dao.ExchangeTransMatchCommonMapper;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dto.ExchangeTransMatchBaseTradeDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huwei1
 * @description
 * @createDate 2022/5/18
 */
@Service
public class ExchangeTransMatchBidding1Service extends BaseService {

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Resource
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    @Autowired
    private ExchangeTransMatchCommonMapper exchangeTransMatchCommonMapper;

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextpath.single:false}")
    private boolean isSingle;

    @Value("${application.micorservice.contextpath.commonService:http://commonService}")
    private String commonServiceName;

    public List<ExchangeTransMatchBaseTradeDto> genFinProductTradeDto(ExchangeTransMatchCommonDto transDto,
                                                                      ExchangeTransMatchCommonEntrustDto entrustDto) {
        List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtoList = new ArrayList<>();

        //生成金融产品汇总交易信息
        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(transDto,entrustDto);

        if (DictCodeListExt3.投资指令类型_多组合单资产指令.getValue().equals(entrustDto.getEntrustClass())) {
            for (ExchangeTransMatchCommonEntrustDetailDto detailDto : entrustDto.getEntrustDetailDtoList()) {
                if(detailDto.getMarchQty() == null || detailDto.getMarchQty().compareTo(BigDecimal.ZERO) <= 0){
                    continue;
                }
                ExchangeTransMatchCommonEntrustDto entrustDtoTmp = new ExchangeTransMatchCommonEntrustDto();
                BeanUtil.copyPropertiesIgnoreNull(entrustDtoTmp,entrustDto);
                entrustDtoTmp.setPortfolioId(detailDto.getPortfolioId());
                entrustDtoTmp.setMarchQty(detailDto.getMarchQty());
                entrustDtoTmp.setMarchAmt(detailDto.getMarchAmt());
                entrustDtoTmp.setMarchFeeAmtUnPay(detailDto.getMarchFeeAmtUnPay());
                entrustDtoTmp.setMarchFeeAmtPay(detailDto.getMarchFeeAmtPay());

                ExchangeTransMatchBaseTradeDto baseTradeProductDealDto = new ExchangeTransMatchBaseTradeDto();
                //生成金融产品汇总交易信息
                baseTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
                //生成金融产品汇总交易附属信息
                baseTradeProductDealDto.setTrdDealTotalAdd(this.generateDealTotalAdd(entrustDtoTmp,baseTradeProductDealDto.getTrdFinProductDealTotal()));
                //生成金融产品交易信息
                baseTradeProductDealDto.setTrdProductDeal(this.genTrdProductDeal(transDto,entrustDtoTmp,baseTradeProductDealDto.getTrdFinProductDealTotal()));
                //生成组合交易表数据
                baseTradeProductDealDto.setPtlTrade(this.generatePtlTrade(entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成费用信息list
                baseTradeProductDealDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(transDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成划款指令
                baseTradeProductDealDto.setStlPayOrdersList(this.generateStlPayOrdersList(transDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                baseTradeProductDealDto.setTrdBankManualSettleFlow(this.trdBankManualSettleFlow(trdFinProductDealTotal));
                baseTradeProductDealDto.setTrdBankManualSettleInfo(this.trdBankManualSettleInfo(transDto, trdFinProductDealTotal));

                baseTradeProductDealDtoList.add(baseTradeProductDealDto);
            }
        }

        if (DictCodeListExt3.投资指令类型_单组合单资产指令.getValue().equals(entrustDto.getEntrustClass())) {
            ExchangeTransMatchBaseTradeDto baseTradeProductDealDto = new ExchangeTransMatchBaseTradeDto();
            // TODO 金融产品汇总交易信息
            baseTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
            // TODO 生成金融产品汇总交易附属信息
            baseTradeProductDealDto.setTrdDealTotalAdd(this.generateDealTotalAdd(entrustDto,baseTradeProductDealDto.getTrdFinProductDealTotal()));
            // TODO 生成金融产品交易信息
            baseTradeProductDealDto.setTrdProductDeal(this.genTrdProductDeal(transDto,entrustDto,baseTradeProductDealDto.getTrdFinProductDealTotal()));
            // TODO 生成组合交易表数据
            baseTradeProductDealDto.setPtlTrade(this.generatePtlTrade(entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            // TODO 生成费用信息list
            baseTradeProductDealDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(transDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            // TODO 生成划款指令
            baseTradeProductDealDto.setStlPayOrdersList(this.generateStlPayOrdersList(transDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));

            baseTradeProductDealDto.setTrdBankManualSettleFlow(this.trdBankManualSettleFlow(trdFinProductDealTotal));
            baseTradeProductDealDto.setTrdBankManualSettleInfo(this.trdBankManualSettleInfo(transDto, trdFinProductDealTotal));

            baseTradeProductDealDtoList.add(baseTradeProductDealDto);
        }

        return baseTradeProductDealDtoList;
    }

    private TrdBankManualSettleFlow trdBankManualSettleFlow(TrdFinProductDealTotal trdFinProductDealTotal) {
        TrdBankManualSettleFlow trdBankManualSettleFlow = new TrdBankManualSettleFlow();
        // 交收流水号
        // trdBankManualSettleFlow.setSeqNo();//自动生成
        // 交收编号
        trdBankManualSettleFlow.setSettleSeqNo(trdFinProductDealTotal.getTotleTradeId());
        // 交收时间
        trdBankManualSettleFlow.setSettleTime(trdFinProductDealTotal.getTradeDate());
        // 金融产品代码
        trdBankManualSettleFlow.setFinprodId(trdFinProductDealTotal.getFinprodId());
        // 交割数量
        trdBankManualSettleFlow.setSettleNum(trdFinProductDealTotal.getShareAmt());
        // 交割金额
        trdBankManualSettleFlow.setSettleAmt(trdFinProductDealTotal.getFpriceAmt());
        return trdBankManualSettleFlow;
    }

    private TrdBankManualSettleInfo trdBankManualSettleInfo(SettleTransDataTotal settleTransDataTotal, TrdFinProductDealTotal trdFinProductDealTotal) {
        TrdBankManualSettleInfo trdBankManualSettleInfo = new TrdBankManualSettleInfo();
        // 交收编号
        trdBankManualSettleInfo.setSettleSeqNo(trdFinProductDealTotal.getTotleTradeId());
        // 交收状态
        trdBankManualSettleInfo.setSettleStatus(settleTransDataTotal.getDeliveryResult());
        // 原业务编号
        trdBankManualSettleInfo.setTradeId(trdFinProductDealTotal.getbTradeId());
        // 交收日期
        trdBankManualSettleInfo.setSettleDate(trdFinProductDealTotal.getTradeDate());
        // 金融产品代码
        trdBankManualSettleInfo.setFinprodId(trdFinProductDealTotal.getFinprodId());
        // 交割数量
        trdBankManualSettleInfo.setSettleNum(trdFinProductDealTotal.getShareAmt());
        // 交割金额
        trdBankManualSettleInfo.setSettleAmt(trdFinProductDealTotal.getFpriceAmt());
        // 业务类别
        trdBankManualSettleInfo.setBusiType("TRD002");
        return trdBankManualSettleInfo;
    }

    private List<StlPayOrdersDto> generateStlPayOrdersList(ExchangeTransMatchCommonDto transDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();

        StlPayOrdersDto stlPayOsrdersDto = createStlPayOrders(transDto, entrustDto, trdProductDeal);
        // 收付方向
        if(com.joyintech.framework.util.StringUtil.equals(DictCodeList.交易方向_买入.getValue(), transDto.getPs())) {
            stlPayOsrdersDto.setPs(DictCodeListExt1.收付方向_支出.getValue());
        } else {
            stlPayOsrdersDto.setPs(DictCodeList.收付方向_收入.getValue());
        }
        stlPayOrdersList.add(stlPayOsrdersDto);
        return stlPayOrdersList;
    }

    protected StlPayOrdersDto createStlPayOrders(ExchangeTransMatchCommonDto dealDto,ExchangeTransMatchCommonEntrustDto entrustDto,TrdProductDeal trdProductDeal) {
        // 指令
        StlPayOrdersDto stlPayOrders = new StlPayOrdersDto();
        // 指令编号
        stlPayOrders.setOrderId(this.buildBussinessIdBySeqno("9998"));
        // 指令类型
        stlPayOrders.setOrderType(null);
        // 结算日期
        stlPayOrders.setSettleDate(trdProductDeal.getSettleDate());
        // 本方资金账户
        stlPayOrders.setMyAcctNo(dealDto.getAcctNo());
        // 结算金额
        stlPayOrders.setSettleAmt(null != trdProductDeal.getTradeAmt() ? trdProductDeal.getTradeAmt() : BigDecimal.ZERO);
        // 币种
        stlPayOrders.setCcy(trdProductDeal.getCcy());
        // 是否需要发送
        stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
        // 是否汇总交易
        stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
        // 交易编号
        stlPayOrders.setTradeId(com.joyintech.framework.util.StringUtil.isNotEmpty(trdProductDeal.getTotleTradeId())?trdProductDeal.getTotleTradeId():trdProductDeal.getTradeId());
        // 指令状态
        stlPayOrders.setOrderType(DictCodeListExt1.交易状态_初始.getValue());
        // 投资组合代码
        stlPayOrders.setPortfolioId(entrustDto.getPortfolioId());
        // 备注
        stlPayOrders.setRemark("数据清算自动处理");
        // 划款类型
        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        return stlPayOrders;
    }

    /**
     * 生成业务编号
     */
    public String buildBussinessIdBySeqno(String seqno) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("seqNo", seqno);
        // 生成的业务编号(此处对性能影响比较大)
        if (isSingle) {
            return simpleRestTemplate.postForObject(commonServiceName + "/core/selectRule", param, String.class);
        }
        return restTemplate.postForObject(commonServiceName + "/core/selectRule", param,String.class);
    }

    /**
     * 金融产品交易费用明细list
     */
    public List<TrdTradeFeeDetail> genTrdTradeFeeDetailList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<TrdTradeFeeDetail> trdTradeFeeDetailList = new ArrayList<>();
        TrdTradeFeeDetail stampFee = new TrdTradeFeeDetail();
        stampFee.setTradeId(trdProductDeal.getTradeId());
        stampFee.setFeeType(DictCodeListExt1.一次性费用类型_印花税.getValue());
        stampFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        stampFee.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getSxfAmt()));
        stampFee.setRegistOrg(trdProductDeal.getRegistOrg());

        TrdTradeFeeDetail jsfFee = new TrdTradeFeeDetail();
        jsfFee.setTradeId(trdProductDeal.getTradeId());
        jsfFee.setFeeType(DictCodeListExt1.一次性费用类型_经手费.getValue());
        jsfFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        jsfFee.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getJsfAmt()));
        jsfFee.setRegistOrg(trdProductDeal.getRegistOrg());

        TrdTradeFeeDetail ghfFee = new TrdTradeFeeDetail();
        ghfFee.setTradeId(trdProductDeal.getTradeId());
        ghfFee.setFeeType(DictCodeListExt1.一次性费用类型_过户费.getValue());
        ghfFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        ghfFee.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getGhfAmt()));
        ghfFee.setRegistOrg(trdProductDeal.getRegistOrg());

        TrdTradeFeeDetail zgfFee = new TrdTradeFeeDetail();
        zgfFee.setTradeId(trdProductDeal.getTradeId());
        zgfFee.setFeeType(DictCodeListExt1.一次性费用类型_证管费.getValue());
        zgfFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        zgfFee.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getZgfAmt()));
        zgfFee.setRegistOrg(trdProductDeal.getRegistOrg());

        TrdTradeFeeDetail sxfFee = new TrdTradeFeeDetail();
        sxfFee.setTradeId(trdProductDeal.getTradeId());
        sxfFee.setFeeType(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
        sxfFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        sxfFee.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getSxfAmt()));
        sxfFee.setRegistOrg(trdProductDeal.getRegistOrg());

        TrdTradeFeeDetail settleFee = new TrdTradeFeeDetail();
        settleFee.setTradeId(trdProductDeal.getTradeId());
        settleFee.setFeeType(DictCodeListExt1.一次性费用类型_结算费.getValue());
        settleFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        settleFee.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getSettleFee()));
        settleFee.setRegistOrg(trdProductDeal.getRegistOrg());

        TrdTradeFeeDetail commissionAmt = new TrdTradeFeeDetail();
        commissionAmt.setTradeId(trdProductDeal.getTradeId());
        commissionAmt.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
        commissionAmt.setIsPayWithTrade(dealDto.getIsPayWithTrade());
        commissionAmt.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getCommissionAmt()));
        commissionAmt.setRegistOrg(trdProductDeal.getRegistOrg());

        trdTradeFeeDetailList.add(stampFee);
        trdTradeFeeDetailList.add(jsfFee);
        trdTradeFeeDetailList.add(ghfFee);
        trdTradeFeeDetailList.add(zgfFee);
        trdTradeFeeDetailList.add(sxfFee);
        trdTradeFeeDetailList.add(settleFee);
        trdTradeFeeDetailList.add(commissionAmt);
        return trdTradeFeeDetailList;
    }

    private List<PtlTrade> generatePtlTrade(ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<PtlTrade> ptlTradeList = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        // 组合代码
        ptlTrade.setPortfolioId(entrustDto.getPortfolioId());
        // 业务编号
        ptlTrade.setBusiNo(trdProductDeal.getTradeId());
        // 业务表
        ptlTrade.setBusiTable(DictCodeListStl.业务表_金融产品交易表.getValue());
        // 业务明细代码
        ptlTrade.setBusiId(trdProductDeal.getFinprodId());
        // 投资目的
        ptlTrade.setInvAim(trdProductDeal.getInvAim());
        // 证券托管户代码
        ptlTrade.setSecManageAcctId(trdProductDeal.getSecManageAcctId());
        // 实际发生日
        ptlTrade.setSettleDate(trdProductDeal.getSettleDate());
        // 业务类型
        ptlTrade.setBusiType(DictCodeListStl.共通业务类型_信用保护凭证交易.getValue());

        ptlTradeList.add(ptlTrade);
        return ptlTradeList;
    }

    private TrdProductDeal genTrdProductDeal(ExchangeTransMatchCommonDto transDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        /// 交易字段设置共通
        TrdProductDeal trdProductDeal = createProductDeal(transDto, entrustDto,trdFinProductDealTotal);
        // 清算速度
        trdProductDeal.setSettleSpeed(transDto.getSettleSpeed());
        // 单位净价
        trdProductDeal.setUnitCprice(transDto.getUnitCprice());
        // 单位利息
        trdProductDeal.setUnitInt(transDto.getUnitInt());
        // 单位全价
        trdProductDeal.setUnitFprice(transDto.getUnitFprice());
        // 单位面值
        trdProductDeal.setParValue(null);
        // 净价总额，交易份额，交易本金，利息总额，全价总额，随交易支付费用，交易金额，未随交易支付费用，总交易费用
        calcFeeAmtPay(transDto, entrustDto,trdProductDeal);
        // 交易方向
        trdProductDeal.setPs(entrustDto.getPs());
        // 到期收益率和行权收益率
        this.calc(trdProductDeal);
        trdProductDeal.setTgBusiType(getTgBusiType(entrustDto.getPs()));

        // 到期收益率
        trdFinProductDealTotal.setYield(trdProductDeal.getYield());
        // 行权收益率
        trdFinProductDealTotal.setExerYield(trdProductDeal.getExerYield());

        return trdProductDeal;
    }

    private String getTgBusiType(String ps) {
        if(com.joyintech.framework.util.StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())) {
            return DictCodeListStl.业务场景分类明细_信用保护凭证_买入.getValue();
        }
        if(com.joyintech.framework.util.StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
            return DictCodeListStl.业务场景分类明细_信用保护凭证_卖出.getValue();
        }
        return null;
    }

    public TrdProductDeal createProductDeal(ExchangeTransMatchCommonDto transDto, ExchangeTransMatchCommonEntrustDto entrustDto,TrdFinProductDealTotal trdFinProductDealTotal) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (entrustDto.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
            codeParameter.setCommonBusinessType(DictCodeListStl.业务场景分类明细_信用保护凭证_买入);
        }
        if (entrustDto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
            codeParameter.setCommonBusinessType(DictCodeListStl.业务场景分类明细_信用保护凭证_卖出);
        }
        // 交易编号
        trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        // 业务类型
        trdProductDeal.setBusiType(null);
        // 金融产品代码
        trdProductDeal.setFinprodId(entrustDto.getFinprodId());
        // 金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(entrustDto.getFinprodType2());
        // 金融产品类型
        trdProductDeal.setFinprodType(transDto.getFinprodType());
        // 分支序号
        trdProductDeal.setBranch(0L);
        // 交易日
        trdProductDeal.setTradeDate(transDto.getTradeDate());
        // 起息日
        trdProductDeal.setVdate(null);
        // 到期日
        trdProductDeal.setMdate(null);
        // 交付日
        trdProductDeal.setPayDate(transDto.getVpayDate());
        // 交割日
        trdProductDeal.setSettleDate(transDto.getSettleDate());
        // 交易币种
        trdProductDeal.setCcy(transDto.getCcy());
        // 净值日期 TODO trdProductDeal.setCpriceDate(??);
        // 投资目的
        if(com.joyintech.framework.util.StringUtil.isNotEmpty(entrustDto.getInvAim())){
            trdProductDeal.setInvAim(entrustDto.getInvAim());
        } else {
            // 查询持仓的投资目的
            Map<String,Object> param = new HashMap<>();
            param.put("dealDate", trdProductDeal.getTradeDate());
            param.put("finProductId",trdProductDeal.getFinprodId());
            param.put("secManageAcctId",transDto.getSecAcctId());
            List<PtlSecPosition>  ptlSecPositions = exchangeTransMatchCommonMapper.searchPtlSecPositionList(param);
            if(CollectionUtils.isNotEmpty(ptlSecPositions)){
                trdProductDeal.setInvAim(ptlSecPositions.get(0).getInvAim());
            }
        }
        // 交易状态
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 原交易编号
        trdProductDeal.setpTradeId(null);
        // 是否结清
        trdProductDeal.setIsClean(null);
        // 汇总交易编号
        trdProductDeal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        // 反向交易编号
        trdProductDeal.setrTradeId(null);
        // 通道代码
        trdProductDeal.setChlId(null);
        // 交易对手
        trdProductDeal.setCounterId(null);
        // 是否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        trdProductDeal.setTradeMarket(transDto.getTradeMarket());
        // 交易平台
        trdProductDeal.setTradePlat(transDto.getTradePlat());
        // 交易市场
        trdProductDeal.setTradeMarketMode(transDto.getTradeMarketMode());

        if(com.joyintech.framework.util.StringUtil.equals(transDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
        }
        else if(com.joyintech.framework.util.StringUtil.equals(transDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
            trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
        }
        // 本方交易员
        trdProductDeal.setTrader(null);
        // 对手方交易员
        trdProductDeal.setCounterTrader(null);
        // 对手方联系方式
        trdProductDeal.setCounterContact(null);
        // 证券管理户代码
        trdProductDeal.setSecManageAcctId(transDto.getSecAcctId());
        // 关联系统交易编号
        trdProductDeal.setfTradeId(null);
        // 现金流代码
        trdProductDeal.setCashId(null);
        // 登记托管机构
        trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
        // 备注
        trdProductDeal.setRemark("数据清算自动处理");
        // 撤销说明
        trdProductDeal.setCancelRemark(null);
        // 销售代码
        trdProductDeal.setSaleCode(null);
        // 计提份额
        trdProductDeal.setAccrNum(null);
        // 交收方式
        trdProductDeal.setSettlemenType(transDto.getSettleMode());
        // 席位号
        trdProductDeal.setSeatId(transDto.getSeatNumber());
        // 投资组合代码
        trdProductDeal.setPortfolioId(transDto.getPortfolioId());

        return trdProductDeal;
    }

    protected void calcFeeAmtPay(ExchangeTransMatchCommonDto dealDto,ExchangeTransMatchCommonEntrustDto entrustDto,TrdProductDeal trdProductDeal) {
        // 净价总额
        trdProductDeal.setCpriceAmt(ArithmeticUtil.multiply(dealDto.getUnitCprice(),entrustDto.getMarchQty()));
        // 交易份额
        trdProductDeal.setShareAmt(entrustDto.getMarchQty());
        // 交易本金
        trdProductDeal.setPrinAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),new BigDecimal(100)));
        // 利息总额
        trdProductDeal.setIntAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitInt()));
        // 全价总额
        trdProductDeal.setFpriceAmt(entrustDto.getMarchAmt());
        // 随交易支付费用
        trdProductDeal.setFeeAmtPay(entrustDto.getMarchFeeAmtPay());
        // 交易金额
        // trdProductDeal.setTradeAmt(ArithmeticUtil.subtract(trdProductDeal.getFpriceAmt(),trdProductDeal.getFeeAmtPay()).setScale(2, RoundingMode.HALF_UP));
        if(DictCodeList.交易方向_买入.getValue().equals(entrustDto.getPs())){
            trdProductDeal.setTradeAmt(ArithmeticUtil.doAdd(trdProductDeal.getFpriceAmt(),trdProductDeal.getFeeAmtPay()).setScale(2, RoundingMode.HALF_UP));
        }else{
            trdProductDeal.setTradeAmt(ArithmeticUtil.subtract(trdProductDeal.getFpriceAmt(),trdProductDeal.getFeeAmtPay()).setScale(2, RoundingMode.HALF_UP));
        }
        // 未随交易支付费用
        trdProductDeal.setFeeAmtUnpay(entrustDto.getMarchFeeAmtUnPay());
        // 总交易费用
        trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(entrustDto.getMarchFeeAmtPay(), entrustDto.getMarchFeeAmtUnPay()));
    }
    /**
     * 计算债券到期收益率和行权收益率
     * @param productDeal
     */
    public void calc(TrdProductDeal productDeal) {
        // 设置计算参数
        TrdBondInvestTradeDto dealParam = new TrdBondInvestTradeDto();
        dealParam.setFinprodId(productDeal.getFinprodId());
        dealParam.setTradeAmt(productDeal.getShareAmt().multiply(BigDecimal.valueOf(100)));
        dealParam.setSettleDate(productDeal.getSettleDate());
        dealParam.setVdate(productDeal.getVdate());
        dealParam.setTradeMarket(productDeal.getTradeMarket());
        dealParam.setTradeMarketMode(productDeal.getTradeMarketMode());
        dealParam.setPs(productDeal.getPs());
        dealParam.setUnitInt(productDeal.getUnitInt());
        dealParam.setUnitFprice(productDeal.getUnitFprice());

        // 设置计算类型为全价总额（根据全价总额计算到期收益率和行权收益率）
        dealParam.setCalculateField("fpriceAmt");
        dealParam.setFpriceAmt(productDeal.getFpriceAmt());
        dealParam.setIntAmt(productDeal.getIntAmt());
        dealParam.setInvAim(productDeal.getInvAim());

        // 调用债券交易计算逻辑
        AjaxResult ajaxResult = trdBondInvestTrade002Service.calculateData(dealParam);

        if (ajaxResult != null && StringUtil.isNotEmpty(ajaxResult.getData())) {
            Object result = ajaxResult.getData();
            if(result instanceof Map) {
                Map<String, Object> calcResult = (Map)result;
                Object yield = calcResult.get("yield");
                Object exerYield = calcResult.get("exerYield");
                if(yield instanceof BigDecimal) {
                    productDeal.setYield((BigDecimal)yield);
                }
                else if (yield != null) {
                    productDeal.setYield(new BigDecimal(yield.toString()));
                }
                if(exerYield instanceof BigDecimal) {
                    productDeal.setExerYield((BigDecimal)exerYield);
                }
                else if (exerYield != null) {
                    productDeal.setExerYield(new BigDecimal(exerYield.toString()));
                }
                if (productDeal.getYield() != null){
                    productDeal.setYield(productDeal.getYield().divide(BigDecimal.valueOf(100)));
                }
                if (productDeal.getExerYield() != null){
                    productDeal.setExerYield(productDeal.getExerYield().divide(BigDecimal.valueOf(100)));
                }
            }
        }
    }

    private TrdDealTotalAdd generateDealTotalAdd(ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        TrdDealTotalAdd trdDealTotalAdd =  new TrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, trdFinProductDealTotal);
        trdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        trdDealTotalAdd.setPurchDate(trdFinProductDealTotal.getTradeDate());
        trdDealTotalAdd.setPurAmt(trdFinProductDealTotal.getPrinAmt());
        trdDealTotalAdd.setEntrustId(entrustDto.getEntrustId());
        return trdDealTotalAdd;
    }

    private TrdFinProductDealTotal genTrdFinProductDealTotal(ExchangeTransMatchCommonDto transDto, ExchangeTransMatchCommonEntrustDto entrustDto) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();

        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (entrustDto.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
            codeParameter.setCommonBusinessType(DictCodeListStl.业务场景分类明细_信用保护凭证_买入);
        }
        if (entrustDto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
            codeParameter.setCommonBusinessType(DictCodeListStl.业务场景分类明细_信用保护凭证_卖出);
        }

        // 汇总交易编号
        trdFinProductDealTotal.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        // 业务类型
        trdFinProductDealTotal.setBusiType(DictCodeListStl.共通业务类型_信用保护凭证交易.getValue());
        // 金融产品代码
        trdFinProductDealTotal.setFinprodId(transDto.getFinprodId());
        // 金融产品类型（投管分类）
        trdFinProductDealTotal.setFinprodType2(transDto.getFinprodType2());
        // 金融产品类型
        trdFinProductDealTotal.setFinprodType(transDto.getFinprodType());
        // 分支序号
        trdFinProductDealTotal.setBranch(0L);
        // 交易日
        trdFinProductDealTotal.setTradeDate(transDto.getTradeDate());
        // 起息日
        trdFinProductDealTotal.setVdate(null);
        // 到期日
        trdFinProductDealTotal.setMdate(null);
        // TODO 交付日 trdFinProductDealTotal.setPayDate(transDto.getVpayDate());
        // 交割日
        trdFinProductDealTotal.setSettleDate(transDto.getSettleDate());
        // 清算速度
        trdFinProductDealTotal.setSettleSpeed(transDto.getSettleSpeed());
        // 交易币种
        trdFinProductDealTotal.setCcy(transDto.getCcy());
        // 单位净价
        trdFinProductDealTotal.setUnitCprice(transDto.getUnitCprice());
        // 单位利息
        trdFinProductDealTotal.setUnitInt(transDto.getUnitInt());
        // 单位全价
        trdFinProductDealTotal.setUnitFprice(transDto.getUnitFprice());
        // 单位面值
        trdFinProductDealTotal.setParValue(transDto.getParValue());
        // 净价总额
        trdFinProductDealTotal.setCpriceAmt(transDto.getCpriceAmt());
        // 交易份额
        trdFinProductDealTotal.setShareAmt(transDto.getShareAmt());
        // 交易本金
        trdFinProductDealTotal.setPrinAmt(transDto.getPrinAmt());
        // 利息总额
        trdFinProductDealTotal.setIntAmt(BigDecimal.ZERO);
        // 全价总额
        trdFinProductDealTotal.setFpriceAmt(transDto.getPrinAmt());
        // TODO 随交易支付费用 trdFinProductDealTotal.setFeeAmtPay(transDto.getTotalFeeAmtPay());
        // 交易金额
        trdFinProductDealTotal.setTradeAmt(transDto.getPrinAmt());
        // TODO 未随交易支付费用 trdFinProductDealTotal.setFeeAmtUnpay(transDto.getTotalUnFeeAmtPay());
        // TODO 总交易费用 trdFinProductDealTotal.setFeeAmt(transDto.getPrinAmt());
        // 交易方向
        trdFinProductDealTotal.setPs(transDto.getPs());
        // TODO 到期收益率 trdFinProductDealTotal.getYield();
        // TODO 行权收益率 trdFinProductDealTotal.getExerYield();
        // 投资目的
        trdFinProductDealTotal.setIntAmt(transDto.getIntAmt());
        // TODO 结算方式
        // 交易状态
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        // 原交易编号
        trdFinProductDealTotal.setpTradeId(null);
        // 是否结清
        trdFinProductDealTotal.setIsClean(null);
        // 现金流代码
        trdFinProductDealTotal.setCashId(null);
        // 证券管理户代码
        trdFinProductDealTotal.setSecManageAcctId(transDto.getSecAcctId());
        // 前置交易编号
        trdFinProductDealTotal.setbTradeId(null);
        // 通道代码
        trdFinProductDealTotal.setChlId(null);
        // 交易对手
        trdFinProductDealTotal.setCounterId(null);
        // 是否外部交易
        trdFinProductDealTotal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        trdFinProductDealTotal.setTradeMarket(transDto.getTradeMarket());
        // 交易平台
        trdFinProductDealTotal.setTradePlat(transDto.getTradePlat());
        // 交易市场
        trdFinProductDealTotal.setTradeMarketMode(transDto.getTradeMarketMode());
        // 投资组合代码
        trdFinProductDealTotal.setPortfolioId(transDto.getPortfolioId());
        // 备注
        trdFinProductDealTotal.setRemark("数据清算自动处理");
        // 投管业务类型
        trdFinProductDealTotal.setTgBusiType(transDto.getBusiType());
        // 首期结算方式
        trdFinProductDealTotal.setDeliveryType("券款兑付");
        // TODO 交收方式  席位号  委托代码
        return trdFinProductDealTotal;
    }
}

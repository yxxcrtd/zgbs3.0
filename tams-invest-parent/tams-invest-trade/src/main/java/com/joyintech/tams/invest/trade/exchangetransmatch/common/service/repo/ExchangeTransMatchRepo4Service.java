package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.TrdProductDealDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dao.ExchangeTransMatchCommonMapper;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dto.ExchangeTransMatchBaseTradeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 质押式协议回购（到期续作）
 */
@Service
public class ExchangeTransMatchRepo4Service extends ExchangeTransMatchRepoCommonService {
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private ExchangeTransMatchCommonMapper exchangeTransMatchCommonMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    /**
     * 生成金融产品交易信息实体
     * @param dealDto
     * @param entrustDto
     * @return
     */
    @Override
    public List<ExchangeTransMatchBaseTradeDto> genFinProductTradeDto(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto) {
        List<ExchangeTransMatchBaseTradeDto> exchangeTransMatchBaseTradeDtoList = new ArrayList<>();
        //生成汇总表数据
        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(dealDto, entrustDto);
        TrdDealTotalAdd trdDealTotalAdd = this.generateDealTotalAdd(dealDto, entrustDto,trdFinProductDealTotal);

        // 首期成交交易信息---处理汇总表的金融产品代码，如果是存在首期则用首期的金融产品代码，如果不存在则使用买入那笔的交易编号
//        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(),dealDto.getMpayDate(),DictCodeList.交易方向_买入.getValue()
//                ,entrustDto.getPortfolioId());
        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(), dealDto.getMpayDate(), dealDto.getSeatNumber(),
                dealDto.getSecAcctId());
        TrdProductDealAdd  firstRepoTradeAdd = trdProductDealAddMapper.selectByPrimaryKey(firstRepoTrade.getTradeId());

        List<SettleTransData> settleTransDataList = this.handleSettleTransData(dealDto,entrustDto);

        for(SettleTransData settleTransData:settleTransDataList) {
            ExchangeTransMatchBaseTradeDto exchangeTransMatchBaseTradeDto = new ExchangeTransMatchBaseTradeDto();
            TrdProductDeal trdProductDeal = genTrdProductDeal(dealDto, entrustDto,trdFinProductDealTotal,settleTransData);

            exchangeTransMatchBaseTradeDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
            //生成汇总表数据
            exchangeTransMatchBaseTradeDto.setTrdDealTotalAdd(trdDealTotalAdd);
            //生成金融产品交易信息
            exchangeTransMatchBaseTradeDto.setTrdProductDeal(trdProductDeal);
            //生成金融产品交易附属表信息
            TrdProductDealAdd trdProductDealAdd = this.genTrdProductDealAdd(dealDto, entrustDto, trdProductDeal);
            if(DictCodeList.交易方向_收息到账.getValue().equals(trdProductDeal.getPs())
                    || DictCodeList.交易方向_还本到账.getValue().equals(trdProductDeal.getPs())){
                trdProductDealAdd.setDealerNo(firstRepoTradeAdd.getDealerNo());
                trdProductDealAdd.setDealerPartyNo(firstRepoTradeAdd.getDealerPartyNo());
                trdProductDealAdd.setOutDealerNo(firstRepoTradeAdd.getOutDealerNo());
                trdProductDealAdd.setOutDealerPartyNo(firstRepoTradeAdd.getOutDealerPartyNo());
            }
            exchangeTransMatchBaseTradeDto.setTrdProductDealAdd(trdProductDealAdd);
            //生成组合交易表数据
            exchangeTransMatchBaseTradeDto.setPtlTrade(this.generatePtlTrade(dealDto, entrustDto, trdProductDeal));
            //生成费用信息list
            exchangeTransMatchBaseTradeDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(dealDto,entrustDto,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
            //生成划款指
            exchangeTransMatchBaseTradeDto.setStlPayOrdersList(this.generateStlPayOrdersList(dealDto,entrustDto,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
            //生成金融产品交易扩展
            exchangeTransMatchBaseTradeDto.setPaTrdProductDeal(this.genPaTrdProductDeal(dealDto, entrustDto, trdProductDeal));

            if(StringUtil.equals(trdProductDeal.getPs(),DictCodeList.交易方向_买入.getValue())) {
                exchangeTransMatchBaseTradeDto.setFinTradeProduct(this.genFinTradeProduct(settleTransData, trdProductDeal));
                exchangeTransMatchBaseTradeDto.setFinProductType(this.genFinProductType(dealDto,trdProductDeal));
                exchangeTransMatchBaseTradeDto.setFinDealPledgeList(this.genFinDealPledgeList(dealDto,trdProductDeal));
            }

            exchangeTransMatchBaseTradeDtoList.add(exchangeTransMatchBaseTradeDto);
        }

        //存在第三方无首期，直接做到期续做的场景，金融产品ID需要特殊处理，如果存在首期就取首期的金融产品代码，没有就用买入的交易编号
        if(!StringUtil.isEmptyList(settleTransDataList)) {
            if(StringUtil.isNotEmpty(firstRepoTrade)) {
                //金融产品代码
                trdFinProductDealTotal.setFinprodId(firstRepoTrade.getFinprodId());
            }
            else {
                ExchangeTransMatchBaseTradeDto ExchangeTransMatchBaseTradeDtoT01 = exchangeTransMatchBaseTradeDtoList.stream().filter(item->StringUtil.equals(item.getTrdProductDeal().getPs(),DictCodeList.交易方向_买入.getValue())).findFirst().get();
                if(StringUtil.isNotEmpty(ExchangeTransMatchBaseTradeDtoT01)) {
                    //金融产品代码
                    trdFinProductDealTotal.setFinprodId(ExchangeTransMatchBaseTradeDtoT01.getTrdProductDeal().getTradeId());
                }
            }

            for(ExchangeTransMatchBaseTradeDto item:exchangeTransMatchBaseTradeDtoList) {
                //金融产品代码 : 交易方向 = 买入时同交易编号，否则为交易明细.金融产品代码
                if(!StringUtil.equals(item.getTrdProductDeal().getPs(),DictCodeList.交易方向_买入.getValue())) {
                    item.getTrdProductDeal().setFinprodId(trdFinProductDealTotal.getFinprodId());
                }

                for(PtlTrade ptlTrade: item.getPtlTrade()) {
                    ptlTrade.setBusiId(item.getTrdProductDeal().getFinprodId());
                }
            }
        }

        return exchangeTransMatchBaseTradeDtoList;
    }

    public List<FinDealPledge> genFinDealPledgeList(ExchangeTransMatchCommonDto dealDto,TrdProductDeal trdProductDeal){
        List<FinDealPledge> finDealPledgeList = new ArrayList<>();
        if(!StringUtil.isEmptyList(dealDto.getSettleTransDataTotalPledgeList())) {
            for(SettleTransDataTotalPledge transDataTotalPledge:dealDto.getSettleTransDataTotalPledgeList()) {
                FinDealPledge finDealPledge = new FinDealPledge();
                finDealPledge.setFinprodId(trdProductDeal.getFinprodId());
                finDealPledge.setBranch(trdProductDeal.getBranch());
                finDealPledge.setPledgeFinprodId(transDataTotalPledge.getPledgeFinprodId());
                finDealPledge.setPledgeFaceValue(transDataTotalPledge.getPledgeFaceValue());
                finDealPledge.setPledgeNumber(transDataTotalPledge.getPledgeNumber());
                finDealPledge.setPledgeRatio(transDataTotalPledge.getPledgeRatio());
                finDealPledge.setPledgeAmt(transDataTotalPledge.getPledgeAmt());
                finDealPledgeList.add(finDealPledge);
            }
        }

        return finDealPledgeList;
    }

    /**
     * 生成金融产品交易汇总信息
     * @param dealDto
     * @param entrustDto
     * @return
     */
    @Override
    public TrdFinProductDealTotal genTrdFinProductDealTotal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto) {
        TrdFinProductDealTotal dealTotal = new TrdFinProductDealTotal();

        // 交易编号，按现有的交易编号生成规则，自动生成
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), dealDto.getFinprodType2())) {
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
        }   else {
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
        }
        dealTotal.setTotleTradeId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));
        // 业务类型
        dealTotal.setBusiType(DictCodeList.共通业务类型_质押式回购存续期.getValue());
        // 金融产品类型
        dealTotal.setFinprodType(dealDto.getFinprodType());
        //金融产品类型投管
        dealTotal.setFinprodType2(dealDto.getFinprodType2());
        // 交易市场
        dealTotal.setTradeMarketMode(dealDto.getTradeMarketMode());
        // 分支序号
        dealTotal.setBranch(0L);
        // 交易日
        dealTotal.setTradeDate(dealDto.getTradeDate());
        // 起息日
        dealTotal.setVdate(dealDto.getVdate());
        // 到期日
        dealTotal.setMdate(dealDto.getMdate());
        //交付日
        dealTotal.setPayDate(dealDto.getVpayDate());
        // 交割日
        dealTotal.setSettleDate(dealDto.getSettleDate());
        // 清算速度
        dealTotal.setSettleSpeed(StringUtil.isNotEmpty(dealDto.getSettleSpeed())?dealDto.getSettleSpeed():0L);
        // 交易币种
        dealTotal.setCcy(dealDto.getCcy());
        // 单位净价
        dealTotal.setUnitCprice(dealDto.getUnitCprice());
        // 单位利息
        dealTotal.setUnitInt(null);
        // 单位全价
        dealTotal.setUnitFprice(null);
        // 单位面值
        dealTotal.setParValue(null);
        // 净价总额
        dealTotal.setCpriceAmt(null);
        // 交易份额
        dealTotal.setShareAmt(entrustDto.getMarchQty());
        BigDecimal proportion = ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getShareAmt(),2);
        // 交易本金
        dealTotal.setPrinAmt(ArithmeticUtil.multiply(proportion,dealDto.getPrinAmt()));
        // 利息总额
        dealTotal.setIntAmt(BigDecimal.ZERO);
        // 随交易支付费用
        dealTotal.setFeeAmtPay(ArithmeticUtil.multiply(proportion,dealDto.getTotalFeeAmtPay()));
        // 全价总额 交易本金 + 利息总额
        dealTotal.setFpriceAmt(ArithmeticUtil.multiply(proportion,ArithmeticUtil.doAdd(dealTotal.getPrinAmt(),dealTotal.getIntAmt())));
        // 交易金额 正回购到期续作：全价总额 - 随交易支付费用 逆回购到期续作：全价总额+ 随交易支付费用
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), dealDto.getFinprodType2())) {
            dealTotal.setTradeAmt(ArithmeticUtil.multiply(proportion,ArithmeticUtil.subtract(dealTotal.getFpriceAmt(),dealTotal.getFeeAmtPay())));
        }
        else {
            dealTotal.setTradeAmt(ArithmeticUtil.multiply(proportion,ArithmeticUtil.doAdd(dealTotal.getFpriceAmt(),dealTotal.getFeeAmtPay())));
        }
        // 未随交易支付费用
        dealTotal.setFeeAmtUnpay(ArithmeticUtil.multiply(proportion,dealDto.getTotalUnFeeAmtPay()));
        // 总交易费用
        dealTotal.setFeeAmt(ArithmeticUtil.doAdd(dealTotal.getFeeAmtPay(),dealTotal.getFeeAmtUnpay()));
        // 交易方向
        dealTotal.setPs(entrustDto.getPs());
        // 交易状态
        dealTotal.setTradeStatus(BussinessDictCodeList.交易状态_初始.getValue());
        // 是否已撤销
        dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        // 证券管理户代码
        dealTotal.setSecManageAcctId(dealDto.getSecAcctId());
        //关联系统交易编号
        dealTotal.setfTradeId(dealDto.getSeqId());
        // 前置交易编号
        dealTotal.setbTradeId(null);
        // 是否外部交易
        dealTotal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        dealTotal.setTradeMarket(dealDto.getTradeMarket());

        if(StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            dealTotal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
        }
        else if(StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
            dealTotal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
        }
        // 交易平台
        dealTotal.setTradePlat(dealDto.getTradePlat());

        // 投资组合代码
        dealTotal.setPortfolioId(entrustDto.getPortfolioId());

        //到期本金
        dealTotal.setDqprinAmt(entrustDto.getMarchQty());

        // 投管业务类型 : 如果交易方向 = ‘到期续作’，并且汇总成交.金融产品类型（投管） = 债券质押式正回购，则为‘债券质押式正回购’。
        //如果交易方向 = ‘到期续作’，并且汇总成交.金融产品类型（投管） = 债券质押式逆回购，则为‘债券质押式逆回购’。
        if(StringUtil.equals(dealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            dealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_正回购.getValue());
        }
        else {
            dealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购.getValue());
        }
        //首期结算方式
        dealTotal.setDeliveryType(DictCodeListExt1.结算方式_券款对付.getValue());
        //到期结算方式
        dealTotal.setmDeliveryType(DictCodeListExt1.结算方式_券款对付.getValue());
        //利率
        dealTotal.setRate(dealDto.getUnitCprice());
        //回购品种
        dealTotal.setTermType(dealDto.getTermType());

        //汇总表的期限=Mdate-Vdate
        dealTotal.setRepoTerm(DateUtil.getBetweenDayNumber(dealDto.getVdate(),dealDto.getMdate()));
        //是否逾期
        dealTotal.setIsOverdueDeal(DictCodeList.是否_否.getValue());
        // 备注
        dealTotal.setRemark("数据清算自动处理");
        // 本方交易员
        dealTotal.setTrader(dealDto.getTrader());
        // 对手方交易员
        dealTotal.setCounterTrader(dealDto.getCounterTrader());
        return dealTotal;
    }


    public TrdProductDeal genTrdProductDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal,SettleTransData settleTransData) {

            TrdProductDealDto trdProductDeal = new TrdProductDealDto();
            // 交易编号，按现有的交易编号生成规则，自动生成
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), dealDto.getFinprodType2())) {
                bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
            }   else {
                bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
            }
            trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));

            //金融产品代码 : 交易方向 = 买入时同交易编号，否则为交易明细.金融产品代码
            if(StringUtil.equals(settleTransData.getPs(),DictCodeList.交易方向_买入.getValue())) {
                trdProductDeal.setFinprodId(trdProductDeal.getTradeId());
            }

            // 金融产品类型
            trdProductDeal.setFinprodType(settleTransData.getFinprodType());
            // 金融产品类型_投管
            trdProductDeal.setFinprodType2(settleTransData.getFinprodType2());
            // 分支序号
            trdProductDeal.setBranch(0L);
            // 交易日
            trdProductDeal.setTradeDate(settleTransData.getTradeDate());
            // 起息日
            trdProductDeal.setVdate(settleTransData.getVdate());
            // 到期日
            trdProductDeal.setMdate(settleTransData.getMdate());
            //支付日
            trdProductDeal.setPayDate(settleTransData.getVpayDate());
            //交割日
            trdProductDeal.setSettleDate(settleTransData.getSettleDate());
            //清算速度
            trdProductDeal.setSettleSpeed(StringUtil.isNotEmpty(settleTransData.getSettleSpeed())?settleTransData.getSettleSpeed():0L);
            // 交易币种
            trdProductDeal.setCcy(settleTransData.getCcy());
            // 净值日期
            trdProductDeal.setCpriceDate(null);
            //单位净价
            trdProductDeal.setUnitCprice(null);
            // 单位利息
            trdProductDeal.setUnitInt(null);
            // 单位全价
            trdProductDeal.setUnitFprice(null);
            // 单位面值
            trdProductDeal.setParValue(null);
            // 净价总额
            trdProductDeal.setCpriceAmt(null);
            //交易份额
            trdProductDeal.setShareAmt(settleTransData.getShareAmt());
            //交易本金
            trdProductDeal.setPrinAmt(settleTransData.getPrinAmt());
            //利息总额
            trdProductDeal.setIntAmt(settleTransData.getIntAmt());
            //全价总额
            trdProductDeal.setFpriceAmt(settleTransData.getFpriceAmt());

            //随交易费用和不随交易支付费用
            if(StringUtil.equals(settleTransData.getIsPayWithTrade(),DictCodeList.是否_是.getValue())) {
                trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(settleTransData.getStampFee(),settleTransData.getJsfAmt(),settleTransData.getGhfAmt(),settleTransData.getZgfAmt(),settleTransData.getSxfAmt(),settleTransData.getSettleFee(),settleTransData.getCommissionAmt()));
                trdProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
            }
            else {
                trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(settleTransData.getStampFee(),settleTransData.getJsfAmt(),settleTransData.getGhfAmt(),settleTransData.getZgfAmt(),settleTransData.getSxfAmt(),settleTransData.getSettleFee()));
                trdProductDeal.setFeeAmtUnpay(settleTransData.getCommissionAmt());
            }

            // if 正回购买入时，交易明细的交易本金 - 明细费用随交易支付的费用 逆回购买入时，交易明细的交易本金+ 明细费用随交易支付的费用 else 交易明细明细对应字段
            if(StringUtil.equals(settleTransData.getPs(),DictCodeList.交易方向_买入.getValue())) {
                if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), dealDto.getFinprodType2())) {
                    trdProductDeal.setTradeAmt(ArithmeticUtil.subtract(settleTransData.getPrinAmt(),trdProductDeal.getFeeAmtPay()));
                }   else {
                    trdProductDeal.setTradeAmt(ArithmeticUtil.doAdd(settleTransData.getPrinAmt(),trdProductDeal.getFeeAmtPay()));
                }
            }
            else {
                //交易金额
                trdProductDeal.setTradeAmt(settleTransData.getTradeAmt());
            }

            //总费用
            trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(trdProductDeal.getFeeAmtPay(),trdProductDeal.getFeeAmtUnpay()));
            // 投资目的
            trdProductDeal.setInvAim(null);
            //交易方向
            trdProductDeal.setPs(settleTransData.getPs());
            // 交易状态
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            // 是否已撤销
            trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
            //汇总交易编号
            trdProductDeal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
            // 原交易编号
            trdProductDeal.setpTradeId(null);
            // 是否结清
            trdProductDeal.setIsClean(null);
            // 反向交易编号
            trdProductDeal.setrTradeId(null);
            // 通道代码
            trdProductDeal.setChlId(null);
            // 交易对手
            trdProductDeal.setCounterId(null);
            // 是否外部交易
            trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
            // 交易场所
            trdProductDeal.setTradeMarket(settleTransData.getTradeMarket());
            // 交易平台
            trdProductDeal.setTradePlat(settleTransData.getTradePlat());
            // 交易市场
            trdProductDeal.setTradeMarketMode(settleTransData.getTradeMarketMode());

            if(StringUtil.equals(settleTransData.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
            }
            else if(StringUtil.equals(settleTransData.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
            }
            // 本方交易员
            trdProductDeal.setTrader(dealDto.getTrader());
            // 对手方交易员
            trdProductDeal.setCounterTrader(dealDto.getCounterTrader());
            // 对手方联系方式
            trdProductDeal.setCounterContact(null);
            // 证券管理户代码
            trdProductDeal.setSecManageAcctId(settleTransData.getSecAcctId());
            // 关联系统交易编号
            trdProductDeal.setfTradeId(settleTransData.getCjbh());
            // 现金流代码
            trdProductDeal.setCashId(null);
            // 登记托管机构
            trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
            // 备注
            trdProductDeal.setRemark(null);
            // 撤销说明
            trdProductDeal.setCancelRemark(null);
            // 销售代码
            trdProductDeal.setSaleCode(null);
            // 计提份额
            trdProductDeal.setAccrNum(null);
            // 交收方式
            trdProductDeal.setSettlemenType(DictCodeListStl.交收方式_非担保.getValue());
            // 席位号
            trdProductDeal.setSeatId(settleTransData.getSeatNumber());
            // 投资组合代码
            trdProductDeal.setPortfolioId(entrustDto.getPortfolioId());

            if(StringUtil.equals(settleTransData.getPs(),DictCodeList.交易方向_收息到账.getValue())
            || StringUtil.equals(settleTransData.getPs(),DictCodeList.交易方向_还本到账.getValue())) {
                if(StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                    trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_质押式正回购到期.getValue());
                }
                else {
                    trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_质押式逆回购到期.getValue());
                }
            }
            else if(StringUtil.equals(settleTransData.getPs(),DictCodeList.交易方向_买入.getValue())){
                if(StringUtil.equals(dealDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                    trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_债券质押式正回购.getValue());
                }
                else {
                    trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_债券质押式逆回购.getValue());
                }
            }

            trdProductDeal.setRemark("数据清算自动处理");
            return trdProductDeal;
    }

    /**
     * 处理成交明细
     * @param dealDto
     * @param entrustDto
     * @return
     */
    private List<SettleTransData> handleSettleTransData(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto) {
        List<SettleTransData> settleTransDataList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("seqId",dealDto.getSeqId());
        map.put("tradeIdRaw",dealDto.getTradeIdRaw());
        List<SettleTransData> list = exchangeTransMatchCommonMapper.getSettleTransDataList(map);
        for(SettleTransData item:list) {
            if(StringUtil.equals(item.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                if(StringUtil.equals(item.getBusiSceneCalss(), DictCodeListStl.业务场景分类明细_债券协议正回购_首期_买入.getValue())
                || StringUtil.equals(item.getBusiSceneCalss(), DictCodeListStl.业务场景分类明细_债券协议逆回购_首期_买入.getValue())) {
                    SettleTransData settleTransData = new SettleTransData();
                    BeanUtil.copyPropertiesIgnoreNull(settleTransData,item);
                    settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
                    //占比份额比例
                    BigDecimal shareRate = ArithmeticUtil.divide(entrustDto.getMarchQty(),item.getShareAmt(),2);
                    settleTransData.setStampFee(ArithmeticUtil.multiply(shareRate,item.getStampFee()));
                    settleTransData.setJsfAmt(ArithmeticUtil.multiply(shareRate,item.getJsfAmt()));
                    settleTransData.setGhfAmt(ArithmeticUtil.multiply(shareRate,item.getGhfAmt()));
                    settleTransData.setZgfAmt(ArithmeticUtil.multiply(shareRate,item.getZgfAmt()));
                    settleTransData.setSxfAmt(ArithmeticUtil.multiply(shareRate,item.getSxfAmt()));
                    settleTransData.setSettleFee(ArithmeticUtil.multiply(shareRate,item.getSettleFee()));
                    settleTransData.setCommissionAmt(ArithmeticUtil.multiply(shareRate,item.getCommissionAmt()));

                    settleTransData.setShareAmt(entrustDto.getMarchQty());
                    settleTransData.setPrinAmt(entrustDto.getMarchQty());
                    settleTransData.setFpriceAmt(ArithmeticUtil.multiply(shareRate,item.getFpriceAmt()));
                    settleTransData.setTradeAmt(ArithmeticUtil.multiply(shareRate,item.getTradeAmt()));
                    //交易数据明细.到期利息 =  汇总成交匹配结果表.匹配份额*成交数据明细.交易净价/成交数据明细.计息基础*（成交数据明细.到期交收日 - 成交数据明细.首期交收日）
                    BigDecimal mIntAmt = ArithmeticUtil.multiply(ArithmeticUtil.divide(ArithmeticUtil.multiply(entrustDto.getMarchQty(),item.getUnitCprice()),new BigDecimal(365),2), new BigDecimal(DateUtil.getBetweenDayNumber(item.getMpayDate(),item.getVpayDate())));
                    settleTransData.setmIntAmt(mIntAmt);
                    settleTransData.setmTradeAmt(ArithmeticUtil.doAdd(settleTransData.getShareAmt(),settleTransData.getmIntAmt()));

                    settleTransDataList.add(settleTransData);
                }
                else {//到期拆分为收息到账、还本到账
                    //收息到账
                    if(BigDecimal.ZERO.compareTo(item.getmIntAmt())<0) {
                        SettleTransData IntSettleTransData = new SettleTransData();
                        BeanUtil.copyPropertiesIgnoreNull(IntSettleTransData,item);

                        IntSettleTransData.setShareAmt(BigDecimal.ZERO);
                        IntSettleTransData.setPrinAmt(BigDecimal.ZERO);
                        //交易金额 = 到期利息
                        IntSettleTransData.setTradeAmt(item.getmIntAmt());
                        //全价总额 = 交易金额
                        IntSettleTransData.setFpriceAmt(IntSettleTransData.getTradeAmt());
                        //利息总额 = 交易金额
                        IntSettleTransData.setIntAmt(IntSettleTransData.getTradeAmt());

                        IntSettleTransData.setStampFee(BigDecimal.ZERO);
                        IntSettleTransData.setJsfAmt(BigDecimal.ZERO);
                        IntSettleTransData.setGhfAmt(BigDecimal.ZERO);
                        IntSettleTransData.setZgfAmt(BigDecimal.ZERO);
                        IntSettleTransData.setSxfAmt(BigDecimal.ZERO);
                        IntSettleTransData.setSettleFee(BigDecimal.ZERO);
                        IntSettleTransData.setCommissionAmt(BigDecimal.ZERO);

                        IntSettleTransData.setPs(DictCodeList.交易方向_收息到账.getValue());
//                        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(),dealDto.getMpayDate(),
//                                DictCodeList.交易方向_买入.getValue(),entrustDto.getPortfolioId());
                        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(), dealDto.getMpayDate(),
                                dealDto.getSeatNumber(), dealDto.getSecAcctId());
                        if(StringUtil.isNotEmpty(firstRepoTrade)) {
                            IntSettleTransData.setFinprodId(firstRepoTrade.getFinprodId());
                        }
                        IntSettleTransData.setBranch(0L);
                        settleTransDataList.add(IntSettleTransData);
                    }

                    //还本到账
                    if(BigDecimal.ZERO.compareTo(item.getmPrinAmt())<0) {
                        SettleTransData prinSettleTransData = new SettleTransData();
                        BeanUtil.copyPropertiesIgnoreNull(prinSettleTransData,item);

                        prinSettleTransData.setShareAmt(item.getmPrinAmt());
                        prinSettleTransData.setPrinAmt(item.getmPrinAmt());
                        prinSettleTransData.setTradeAmt(item.getmPrinAmt());
                        prinSettleTransData.setFpriceAmt(item.getmPrinAmt());
                        prinSettleTransData.setPs(DictCodeList.交易方向_还本到账.getValue());

                        prinSettleTransData.setIntAmt(BigDecimal.ZERO);
                        prinSettleTransData.setStampFee(BigDecimal.ZERO);
                        prinSettleTransData.setJsfAmt(BigDecimal.ZERO);
                        prinSettleTransData.setGhfAmt(BigDecimal.ZERO);
                        prinSettleTransData.setZgfAmt(BigDecimal.ZERO);
                        prinSettleTransData.setSxfAmt(BigDecimal.ZERO);
                        prinSettleTransData.setSettleFee(BigDecimal.ZERO);
                        prinSettleTransData.setCommissionAmt(BigDecimal.ZERO);

//                        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(),dealDto.getMpayDate(),
//                                DictCodeList.交易方向_买入.getValue(),entrustDto.getPortfolioId());
                        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(), dealDto.getMpayDate(),
                                dealDto.getSeatNumber(), dealDto.getSecAcctId());
                        if(StringUtil.isNotEmpty(firstRepoTrade)) {
                            prinSettleTransData.setFinprodId(firstRepoTrade.getFinprodId());
                        }
                        prinSettleTransData.setBranch(0L);
                        settleTransDataList.add(prinSettleTransData);
                    }
                }

            }
            else if(StringUtil.equals(item.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                if(StringUtil.equals(item.getPs(), DictCodeListExt3.交易方向_到期续做.getValue())) {
                    //首期
                    SettleTransData firstSettleTransData = new SettleTransData();
                    BeanUtil.copyPropertiesIgnoreNull(firstSettleTransData,item);
                    firstSettleTransData.setPs(DictCodeList.交易方向_买入.getValue());
                    //占比份额比例
                    BigDecimal shareRate = ArithmeticUtil.divide(entrustDto.getMarchQty(),item.getShareAmt(),2);
                    firstSettleTransData.setStampFee(ArithmeticUtil.multiply(shareRate,item.getStampFee()));
                    firstSettleTransData.setJsfAmt(ArithmeticUtil.multiply(shareRate,item.getJsfAmt()));
                    firstSettleTransData.setGhfAmt(ArithmeticUtil.multiply(shareRate,item.getGhfAmt()));
                    firstSettleTransData.setZgfAmt(ArithmeticUtil.multiply(shareRate,item.getZgfAmt()));
                    firstSettleTransData.setSxfAmt(ArithmeticUtil.multiply(shareRate,item.getSxfAmt()));
                    firstSettleTransData.setSettleFee(ArithmeticUtil.multiply(shareRate,item.getSettleFee()));
                    firstSettleTransData.setCommissionAmt(ArithmeticUtil.multiply(shareRate,item.getCommissionAmt()));

                    firstSettleTransData.setShareAmt(entrustDto.getMarchQty());
                    firstSettleTransData.setPrinAmt(entrustDto.getMarchQty());
                    firstSettleTransData.setFpriceAmt(entrustDto.getMarchQty());
                    firstSettleTransData.setTradeAmt(entrustDto.getMarchQty());

                    //交易数据明细.到期利息 =  汇总成交匹配结果表.匹配份额*成交数据明细.交易净价/成交数据明细.计息基础*（成交数据明细.到期交收日 - 成交数据明细.首期交收日）
                    BigDecimal mIntAmt = ArithmeticUtil.multiply(ArithmeticUtil.divide(ArithmeticUtil.multiply(entrustDto.getMarchQty(),item.getUnitCprice()),new BigDecimal(365),2), new BigDecimal(DateUtil.getBetweenDayNumber(item.getMpayDate(),item.getVpayDate())));
                    firstSettleTransData.setmIntAmt(mIntAmt);
                    firstSettleTransData.setmTradeAmt(ArithmeticUtil.doAdd(firstSettleTransData.getShareAmt(),firstSettleTransData.getmIntAmt()));

                    settleTransDataList.add(firstSettleTransData);

                    //到期->拆成收息到账，还本到账
                    //收息到账
                    if(BigDecimal.ZERO.compareTo(item.getmIntAmt())<0) {
                        SettleTransData IntSettleTransData = new SettleTransData();
                        BeanUtil.copyPropertiesIgnoreNull(IntSettleTransData,item);

                        IntSettleTransData.setShareAmt(BigDecimal.ZERO);
                        IntSettleTransData.setPrinAmt(BigDecimal.ZERO);
                        //交易金额 = 成交数据明细.到期利息*汇总成交匹配结果表.匹配份额/成交数据明细.到期本金
                        IntSettleTransData.setTradeAmt(item.getmIntAmt());
                        //全价总额 = 交易金额
                        IntSettleTransData.setFpriceAmt(IntSettleTransData.getTradeAmt());
                        //利息总额 = 交易金额
                        IntSettleTransData.setIntAmt(IntSettleTransData.getTradeAmt());

                        IntSettleTransData.setStampFee(BigDecimal.ZERO);
                        IntSettleTransData.setJsfAmt(BigDecimal.ZERO);
                        IntSettleTransData.setGhfAmt(BigDecimal.ZERO);
                        IntSettleTransData.setZgfAmt(BigDecimal.ZERO);
                        IntSettleTransData.setSxfAmt(BigDecimal.ZERO);
                        IntSettleTransData.setSettleFee(BigDecimal.ZERO);
                        IntSettleTransData.setCommissionAmt(BigDecimal.ZERO);

                        IntSettleTransData.setPs(DictCodeList.交易方向_收息到账.getValue());
//                        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(),dealDto.getMpayDate(),
//                                DictCodeList.交易方向_买入.getValue(),entrustDto.getPortfolioId());
                        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(), dealDto.getMpayDate(),
                                dealDto.getSeatNumber(), dealDto.getSecAcctId());
                        if(StringUtil.isNotEmpty(firstRepoTrade)) {
                            IntSettleTransData.setFinprodId(firstRepoTrade.getFinprodId());
                            IntSettleTransData.setVdate(firstRepoTrade.getVdate());
                            IntSettleTransData.setMdate(firstRepoTrade.getMdate());
                        }
                        IntSettleTransData.setBranch(0L);
                        settleTransDataList.add(IntSettleTransData);
                    }

                    //还本到账
                    if(BigDecimal.ZERO.compareTo(item.getmPrinAmt())<0) {
                        SettleTransData prinSettleTransData = new SettleTransData();
                        BeanUtil.copyPropertiesIgnoreNull(prinSettleTransData,item);

                        prinSettleTransData.setShareAmt(item.getmPrinAmt());
                        prinSettleTransData.setPrinAmt(item.getmPrinAmt());
                        prinSettleTransData.setTradeAmt(item.getmPrinAmt());
                        prinSettleTransData.setFpriceAmt(item.getmPrinAmt());
                        prinSettleTransData.setPs(DictCodeList.交易方向_还本到账.getValue());

                        prinSettleTransData.setIntAmt(BigDecimal.ZERO);
                        prinSettleTransData.setStampFee(BigDecimal.ZERO);
                        prinSettleTransData.setJsfAmt(BigDecimal.ZERO);
                        prinSettleTransData.setGhfAmt(BigDecimal.ZERO);
                        prinSettleTransData.setZgfAmt(BigDecimal.ZERO);
                        prinSettleTransData.setSxfAmt(BigDecimal.ZERO);
                        prinSettleTransData.setSettleFee(BigDecimal.ZERO);
                        prinSettleTransData.setCommissionAmt(BigDecimal.ZERO);

//                        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(),dealDto.getMpayDate(),
//                                DictCodeList.交易方向_买入.getValue(),entrustDto.getPortfolioId());
                        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(), dealDto.getMpayDate(),
                                dealDto.getSeatNumber(), dealDto.getSecAcctId());
                        if(StringUtil.isNotEmpty(firstRepoTrade)) {
                            prinSettleTransData.setFinprodId(firstRepoTrade.getFinprodId());
                            prinSettleTransData.setVdate(firstRepoTrade.getVdate());
                            prinSettleTransData.setMdate(firstRepoTrade.getMdate());
                        }
                        prinSettleTransData.setBranch(0L);
                        settleTransDataList.add(prinSettleTransData);
                    }
                }
            }

        }
        return settleTransDataList;
    }




}

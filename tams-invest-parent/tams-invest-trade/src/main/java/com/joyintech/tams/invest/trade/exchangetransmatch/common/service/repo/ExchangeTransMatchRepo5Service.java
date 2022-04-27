package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
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
 * 质押式协议回购（提前终止/到期）
 */
@Service
public class ExchangeTransMatchRepo5Service extends ExchangeTransMatchRepoCommonService {
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private ExchangeTransMatchCommonMapper exchangeTransMatchCommonMapper;
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

        List<TrdProductDeal> dealList = this.genTrdProductDealList(dealDto, entrustDto,trdFinProductDealTotal);

        for (TrdProductDeal trdProductDeal : dealList) {
            ExchangeTransMatchBaseTradeDto exchangeTransMatchBaseTradeDto = new ExchangeTransMatchBaseTradeDto();
            exchangeTransMatchBaseTradeDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
            //生成汇总表数据
            exchangeTransMatchBaseTradeDto.setTrdDealTotalAdd(trdDealTotalAdd);
            //生成金融产品交易信息
            exchangeTransMatchBaseTradeDto.setTrdProductDeal(trdProductDeal);
            //生成金融产品交易附属表信息
            exchangeTransMatchBaseTradeDto.setTrdProductDealAdd(this.genTrdProductDealAdd(dealDto, entrustDto, trdProductDeal));
            //生成组合交易表数据
            exchangeTransMatchBaseTradeDto.setPtlTrade(this.generatePtlTrade(dealDto, entrustDto, trdProductDeal));
            //生成费用信息list
            exchangeTransMatchBaseTradeDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(dealDto,entrustDto,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
            //生成划款指
            exchangeTransMatchBaseTradeDto.setStlPayOrdersList(this.generateStlPayOrdersList(dealDto,entrustDto,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
            //生成金融产品交易扩展
            exchangeTransMatchBaseTradeDto.setPaTrdProductDeal(this.genPaTrdProductDeal(dealDto, entrustDto, trdProductDeal));

            exchangeTransMatchBaseTradeDtoList.add(exchangeTransMatchBaseTradeDto);
        }
        return exchangeTransMatchBaseTradeDtoList;
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
        // 金融产品代码
//        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(),dealDto.getVdate(),DictCodeList.交易方向_买入.getValue(),
//                entrustDto.getPortfolioId());
        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(), dealDto.getVdate(),
                dealDto.getSeatNumber(), dealDto.getSecAcctId());
        if(StringUtil.isNotEmpty(firstRepoTrade)) {
            //金融产品代码
            dealTotal.setFinprodId(firstRepoTrade.getFinprodId());
            // 金融产品类型
            dealTotal.setFinprodType(firstRepoTrade.getFinprodType());
            //金融产品类型投管
            dealTotal.setFinprodType2(firstRepoTrade.getFinprodType2());
            // 交易市场
            dealTotal.setTradeMarketMode(firstRepoTrade.getTradeMarketMode());
        }
        // 分支序号
        dealTotal.setBranch(0L);
        // 交易日
        dealTotal.setTradeDate(dealDto.getTradeDate());
        // 起息日
        dealTotal.setVdate(dealDto.getVdate());
        // 到期日
        dealTotal.setMdate(dealDto.getTradeDate());
        //交付日
        dealTotal.setPayDate(null);
        // 交割日
        dealTotal.setSettleDate(dealDto.getTradeDate());
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
        dealTotal.setIntAmt(ArithmeticUtil.multiply(proportion,dealDto.getIntAmt()));
        // 全价总额
        dealTotal.setFpriceAmt(ArithmeticUtil.multiply(proportion,dealDto.getFpriceAmt()));
        // 随交易支付费用
        dealTotal.setFeeAmtPay(BigDecimal.ZERO);
        // 交易金额
        dealTotal.setTradeAmt(ArithmeticUtil.multiply(proportion,dealDto.getTradeAmt()));
        // 未随交易支付费用
        dealTotal.setFeeAmtUnpay(BigDecimal.ZERO);
        // 总交易费用
        dealTotal.setFeeAmt(ArithmeticUtil.doAdd(dealTotal.getFeeAmtPay(),dealTotal.getFeeAmtUnpay()));
        // 交易方向
        dealTotal.setPs(dealDto.getPs());
        // 到期收益率
        dealTotal.setYield(null);
        // 行权收益率
        dealTotal.setExerYield(null);
        // 交易状态
        dealTotal.setTradeStatus(BussinessDictCodeList.交易状态_初始.getValue());
        // 是否已撤销
        dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        // 证券管理户代码
        dealTotal.setSecManageAcctId(dealDto.getSecAcctId());
        // 前置交易编号
        dealTotal.setbTradeId(null);
        // 是否外部交易
        dealTotal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        dealTotal.setTradeMarket(dealDto.getTradeMarket());
        //关联系统交易编号
        dealTotal.setfTradeId(dealDto.getSeqId());
        //是否逾期
        dealTotal.setIsOverdueDeal(DictCodeList.是否_否.getValue());

        // GZYHTZJY-1039:交易对手应为空 fuyongsheng modify 20220106 start
//        if(StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
//            dealTotal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
//        }
//        else if(StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
//            dealTotal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
//        }
        // GZYHTZJY-1039:交易对手应为空 fuyongsheng modify 20220106 end
        // 交易平台
        dealTotal.setTradePlat(dealDto.getTradePlat());

        // 投资组合代码
        dealTotal.setPortfolioId(entrustDto.getPortfolioId());

        //到期本金
        dealTotal.setDqprinAmt(entrustDto.getMarchQty());
        // 投管业务类型
        dealTotal.setTgBusiType(DictCodeListExt3.共通业务类型_质押券置换.getValue());
        // 如果汇总成交.金融产品类型（投管） = 债券质押式正回购，则为‘债券质押式正回购到期’。否则为‘债券质押式逆回购到期’
        // GZYHTZJY-1039:modify by fuyongsheng 20220106 start
        if(StringUtil.equals(dealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            // dealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_正回购.getValue());
            dealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_正回购到期.getValue());
        }
        else {
            // dealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购.getValue());
            dealTotal.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购到期.getValue());
        }
        // GZYHTZJY-1039:modify by fuyongsheng 20220106 end
        //首期结算方式
        dealTotal.setDeliveryType(DictCodeListExt1.结算方式_券款对付.getValue());
        //到期结算方式
        dealTotal.setmDeliveryType(DictCodeListExt1.结算方式_券款对付.getValue());
        // 备注
        dealTotal.setRemark("数据清算自动处理");

        return dealTotal;
    }

    public List<TrdProductDeal> genTrdProductDealList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        List<SettleTransData> settleTransDataList = this.handleSettleTransData(dealDto,entrustDto);
        for(SettleTransData settleTransData:settleTransDataList) {
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
            else {
                trdProductDeal.setFinprodId(trdFinProductDealTotal.getFinprodId());
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
            // GZYHTZJY-1039 paydate无需赋值 modify fuyongsheng 20220107 start
            // trdProductDeal.setPayDate(settleTransData.getVpayDate());
            // GZYHTZJY-1039 paydate无需赋值 modify fuyongsheng 20220107 end
            //交割日
            trdProductDeal.setSettleDate(settleTransData.getSettleDate());
            //清算速度
            trdProductDeal.setSettleSpeed(StringUtil.isNotEmpty(settleTransData.getSettleSpeed())?settleTransData.getSettleSpeed():0L);
            // 交易币种
            trdProductDeal.setCcy(settleTransData.getCcy());
            // 净值日期
            trdProductDeal.setCpriceDate(null);
            //单位净价
            trdProductDeal.setUnitCprice(settleTransData.getUnitCprice());
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
            if(StringUtil.equals(settleTransData.getPs(),DictCodeList.交易方向_还本到账.getValue())) {
                //全价总额
                trdProductDeal.setFpriceAmt(settleTransData.getPrinAmt());
            }
            else {
                //全价总额
                trdProductDeal.setFpriceAmt(settleTransData.getFpriceAmt());
            }

            //交易金额
            trdProductDeal.setTradeAmt(settleTransData.getTradeAmt());
            //随交易费用和不随交易支付费用
            if(StringUtil.equals(settleTransData.getIsPayWithTrade(),DictCodeList.是否_是.getValue())) {
                trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(settleTransData.getJsfAmt(),settleTransData.getGhfAmt(),settleTransData.getZgfAmt(),settleTransData.getSxfAmt(),settleTransData.getSettleFee(),settleTransData.getCommissionAmt()));
                trdProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
            }
            else {
                trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(settleTransData.getJsfAmt(),settleTransData.getGhfAmt(),settleTransData.getZgfAmt(),settleTransData.getSxfAmt(),settleTransData.getSettleFee()));
                trdProductDeal.setFeeAmtUnpay(settleTransData.getCommissionAmt());
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
            // GZYHTZJY-1039:交易对手应为空 fuyongsheng modify 20220106 start
//            if(StringUtil.equals(settleTransData.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
//                trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
//            }
//            else if(StringUtil.equals(settleTransData.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
//                trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
//            }
            // GZYHTZJY-1039:交易对手应为空 fuyongsheng modify 20220106 end
            // 本方交易员
            trdProductDeal.setTrader(null);
            // 对手方交易员
            trdProductDeal.setCounterTrader(null);
            // 对手方联系方式
            trdProductDeal.setCounterContact(null);
            // 证券管理户代码
            trdProductDeal.setSecManageAcctId(settleTransData.getSecAcctId());
            // 关联系统交易编号
            trdProductDeal.setfTradeId(null);
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
            trdProductDeal.setSettlemenType(null);
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
            trdProductDeal.setRemark("数据清算自动处理");
            trdProductDealList.add(trdProductDeal);

        }

        return trdProductDealList;
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
            //收息到账
            if(BigDecimal.ZERO.compareTo(item.getmIntAmt())<0) {
                SettleTransData IntSettleTransData = new SettleTransData();
                BeanUtil.copyPropertiesIgnoreNull(IntSettleTransData,item);

                IntSettleTransData.setShareAmt(BigDecimal.ZERO);
                IntSettleTransData.setPrinAmt(BigDecimal.ZERO);
                //交易金额 = 成交数据明细.到期利息*汇总成交匹配结果表.匹配份额/成交数据明细.到期本金
                IntSettleTransData.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getmIntAmt(),entrustDto.getMarchQty()),item.getmPrinAmt(),2));
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
//                TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(),dealDto.getVdate(),DictCodeList.交易方向_买入.getValue(),
//                        entrustDto.getPortfolioId());
                TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(), dealDto.getVdate(),
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

                prinSettleTransData.setShareAmt(entrustDto.getMarchQty());
                prinSettleTransData.setPrinAmt(entrustDto.getMarchQty());
                prinSettleTransData.setTradeAmt(entrustDto.getMarchQty());
                prinSettleTransData.setPs(DictCodeList.交易方向_还本到账.getValue());

                prinSettleTransData.setIntAmt(BigDecimal.ZERO);
                prinSettleTransData.setStampFee(BigDecimal.ZERO);
                prinSettleTransData.setJsfAmt(BigDecimal.ZERO);
                prinSettleTransData.setGhfAmt(BigDecimal.ZERO);
                prinSettleTransData.setZgfAmt(BigDecimal.ZERO);
                prinSettleTransData.setSxfAmt(BigDecimal.ZERO);
                prinSettleTransData.setSettleFee(BigDecimal.ZERO);
                prinSettleTransData.setCommissionAmt(BigDecimal.ZERO);

//                TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(),dealDto.getVdate(),DictCodeList.交易方向_买入.getValue(),
//                        entrustDto.getPortfolioId());
                TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(), dealDto.getVdate(),
                        dealDto.getSeatNumber(), dealDto.getSecAcctId());
                if(StringUtil.isNotEmpty(firstRepoTrade)) {
                    prinSettleTransData.setFinprodId(firstRepoTrade.getFinprodId());
                }
                prinSettleTransData.setBranch(0L);
                settleTransDataList.add(prinSettleTransData);
            }

        }
        return settleTransDataList;
    }
}

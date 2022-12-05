package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 可转债申购确认
 */
@Service
public class ExchangeTransMatchBond3Service extends ExchangeTransMatchBondCommonService {


    @Override
    protected void setDealTotal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        // 业务类型
        trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
        // 到期收益率
        trdFinProductDealTotal.setYield(null);
        // 行权收益率
        trdFinProductDealTotal.setExerYield(null);
        // 首期结算方式
        trdFinProductDealTotal.setDeliveryType("DVP");
        // 净价总额
        trdFinProductDealTotal.setCpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitCprice()));
        // 全价总额
        trdFinProductDealTotal.setFpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitFprice()));
        // 交易本金
        trdFinProductDealTotal.setTradeAmt(ArithmeticUtil.subtract(trdFinProductDealTotal.getFpriceAmt(),entrustDto.getMarchFeeAmtPay()));
        // 利息总额
        trdFinProductDealTotal.setIntAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitInt()));
    }

    /**
     * 生成汇总附属交易
     * @param dealDto
     * @param entrustDto
     * @return
     */
    @Override
    public TrdDealTotalAdd generateDealTotalAdd(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        TrdDealTotalAdd trdDealTotalAdd =  new TrdDealTotalAdd();
        // 根据前置交易编号去申购数据
        TrdDealTotalAdd trdDealTotalAddSQ = this.selectTotleDealByTotleId(trdFinProductDealTotal.getbTradeId());
        if (trdDealTotalAddSQ != null){
            BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, trdDealTotalAddSQ);
        }
        BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, trdFinProductDealTotal);
        trdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());

        return  trdDealTotalAdd;
    }


    /**
     * 可转债申购确认
     * @param
     * @return
     */
    @Override
    protected TrdProductDeal createDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        // 交易字段设置共通
        TrdProductDeal trdProductDeal = createProductDeal(dealDto, entrustDto,trdFinProductDealTotal);

        trdProductDeal.setUnitCprice(new BigDecimal(100));
        // 单位利息
        trdProductDeal.setUnitInt(BigDecimal.ZERO);
        // 单位全价
        trdProductDeal.setUnitFprice(new BigDecimal(100));
        // 单位面值
        trdProductDeal.setParValue(new BigDecimal(100));
        // 净价总额，交易份额，交易本金，利息总额，全价总额，随交易支付费用，交易金额，未随交易支付费用，总交易费用，成交编号
        calcFeeAmtPay(dealDto, entrustDto,trdProductDeal);
        // 可转债申购确认交易本金是中签券面总额，不是净价总额
        trdProductDeal.setPrinAmt(entrustDto.getMarchQty().multiply(new BigDecimal(100)));
        // 交易方向
        trdProductDeal.setPs(DictCodeList.交易方向_申购确认.getValue());
        // 到期收益率和行权收益率
        this.calc(trdProductDeal, entrustDto);
        trdProductDeal.setbTradeId(this.selectBTradeId(dealDto.getApplyTradeId(), DictCodeList.交易方向_申购.getValue()));

        trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());

        // 到期收益率
        trdFinProductDealTotal.setYield(trdProductDeal.getYield());
        // 行权收益率
        trdFinProductDealTotal.setExerYield(trdProductDeal.getExerYield());

        return trdProductDeal;
    }

//    @Override
//    public List<StlPayOrdersDto> generateStlPayOrdersList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
//        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();
//
//        StlPayOrdersDto stlPayOsrdersDto = createStlPayOrders(dealDto, entrustDto, trdProductDeal);
//        // 收付方向
//        if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dealDto.getPs())) {
//            stlPayOsrdersDto.setPs(DictCodeListExt1.收付方向_支出.getValue());
//        }
//        else {
//            stlPayOsrdersDto.setPs(DictCodeList.收付方向_收入.getValue());
//        }
//        stlPayOrdersList.add(stlPayOsrdersDto);
//
//        return stlPayOrdersList;
//    }
}

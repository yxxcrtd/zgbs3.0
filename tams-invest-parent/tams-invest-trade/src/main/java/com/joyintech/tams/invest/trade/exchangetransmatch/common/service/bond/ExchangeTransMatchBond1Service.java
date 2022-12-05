package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDetailDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 债券买入、卖出、行权回售
 */
@Service
public class ExchangeTransMatchBond1Service extends ExchangeTransMatchBondCommonService {



    /**
     * 债券买入、卖出、行权回售
     * @param
     * @return
     */
    @Override
    protected void setDealTotal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto,TrdFinProductDealTotal trdFinProductDealTotal) {
        // 业务类型
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 单位净价
        trdFinProductDealTotal.setUnitCprice(dealDto.getUnitCprice());
        // 单位利息
        trdFinProductDealTotal.setUnitInt(dealDto.getUnitInt());
        // 单位全价
        trdFinProductDealTotal.setUnitFprice(dealDto.getUnitFprice());
        // 单位面值
        trdFinProductDealTotal.setParValue(dealDto.getParValue());
        // 净价总额
//        trdFinProductDealTotal.setCpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitCprice()).setScale(2, RoundingMode.HALF_UP));
        if(StringUtil.equals(entrustDto.getEntrustClass(),DictCodeListExt3.投资指令类型_多组合单资产指令.getValue())) {
            trdFinProductDealTotal.setCpriceAmt(entrustDto.getEntrustDetailDtoList().stream().map(ExchangeTransMatchCommonEntrustDetailDto::getMarchCpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add));
        } else {
            trdFinProductDealTotal.setCpriceAmt(entrustDto.getMarchCpriceAmt());
        }
        // 全价总额
        trdFinProductDealTotal.setFpriceAmt(entrustDto.getMarchAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
        // 交易本金
        if (StringUtil.equals(trdFinProductDealTotal.getPs(), DictCodeList.交易方向_买入.getValue())
                || StringUtil.equals(trdFinProductDealTotal.getPs(), DictCodeList.交易方向_卖出.getValue()) ) {
            trdFinProductDealTotal.setPrinAmt(trdFinProductDealTotal.getCpriceAmt());
        }
        // 交易金额
        if (StringUtil.equals(entrustDto.getPs(), DictCodeList.交易方向_买入.getValue())
            || StringUtil.equals(entrustDto.getPs(), DictCodeList.交易方向_申购.getValue())
            || StringUtil.equals(entrustDto.getPs(), DictCodeList.交易方向_申购确认.getValue())
            || StringUtil.equals(entrustDto.getPs(), DictCodeListExt3.交易方向_配债.getValue())
        ) {
            trdFinProductDealTotal.setTradeAmt(ArithmeticUtil.doAdd(trdFinProductDealTotal.getFpriceAmt(),entrustDto.getMarchFeeAmtPay()));
        } else if (StringUtil.equals(entrustDto.getPs(), DictCodeList.交易方向_卖出.getValue())
                || StringUtil.equals(entrustDto.getPs(), DictCodeList.交易方向_行权_回售.getValue())) {
            trdFinProductDealTotal.setTradeAmt(ArithmeticUtil.subtract(trdFinProductDealTotal.getFpriceAmt(),entrustDto.getMarchFeeAmtPay()));
        } else {
            trdFinProductDealTotal.setTradeAmt(trdFinProductDealTotal.getFpriceAmt());
        }
        // 利息总额  =  全价总额- 净价总额
        trdFinProductDealTotal.setIntAmt(ArithmeticUtil.subtract(trdFinProductDealTotal.getFpriceAmt(),trdFinProductDealTotal.getCpriceAmt()));
        // 到期收益率--在TrdProductDeal生成的时候同时设值
        trdFinProductDealTotal.setYield(null);
        // 行权收益率--在TrdProductDeal生成的时候同时设值
        trdFinProductDealTotal.setExerYield(null);
        // 首期结算方式
        trdFinProductDealTotal.setDeliveryType("DVP");
    }

    // ZGZGST-4155:场内匹配生成的交易数据中展示对应的指令编号 fuyongsheng add 20211208 start
    @Override
    public TrdDealTotalAdd generateDealTotalAdd(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        TrdDealTotalAdd trdDealTotalAdd =  new TrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, trdFinProductDealTotal);
        trdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        trdDealTotalAdd.setPurchDate(trdFinProductDealTotal.getTradeDate());
        trdDealTotalAdd.setPurAmt(trdFinProductDealTotal.getPrinAmt());
        trdDealTotalAdd.setEntrustId(entrustDto.getEntrustId());

        return trdDealTotalAdd;
    }
    // ZGZGST-4155:场内匹配生成的交易数据中展示对应的指令编号 fuyongsheng add 20211208 end

    @Override
    protected TrdProductDeal createDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        // 交易字段设置共通
        TrdProductDeal trdProductDeal = createProductDeal(dealDto, entrustDto,trdFinProductDealTotal);
        trdProductDeal.setSettleSpeed(dealDto.getSettleSpeed());
        // 单位净价
        trdProductDeal.setUnitCprice(dealDto.getUnitCprice());
        // 单位利息
        trdProductDeal.setUnitInt(dealDto.getUnitInt());
        // 单位全价
        trdProductDeal.setUnitFprice(dealDto.getUnitFprice());
        // 单位面值
        trdProductDeal.setParValue(dealDto.getParValue());
        // 净价总额，交易份额，交易本金，利息总额，全价总额，随交易支付费用，交易金额，未随交易支付费用，总交易费用
        calcFeeAmtPay(dealDto, entrustDto,trdProductDeal);
        // 交易方向
        trdProductDeal.setPs(entrustDto.getPs());
        // 到期收益率和行权收益率
        this.calc(trdProductDeal, entrustDto);
        trdProductDeal.setTgBusiType(getTgBusiType(entrustDto.getPs()));

        // 到期收益率
        trdFinProductDealTotal.setYield(trdProductDeal.getYield());
        // 行权收益率
        trdFinProductDealTotal.setExerYield(trdProductDeal.getExerYield());



        return trdProductDeal;
    }

    @Override
    public List<StlPayOrdersDto> generateStlPayOrdersList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();

        StlPayOrdersDto stlPayOsrdersDto = createStlPayOrders(dealDto, entrustDto, trdProductDeal);
        // 收付方向
        if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dealDto.getPs())) {
            stlPayOsrdersDto.setPs(DictCodeListExt1.收付方向_支出.getValue());
        }
        else {
            stlPayOsrdersDto.setPs(DictCodeList.收付方向_收入.getValue());
        }
        stlPayOrdersList.add(stlPayOsrdersDto);

        return stlPayOrdersList;
    }


}

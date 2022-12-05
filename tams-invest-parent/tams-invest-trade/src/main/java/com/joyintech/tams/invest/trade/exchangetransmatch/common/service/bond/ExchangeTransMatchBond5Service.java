package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 配债
 */
@Service
public class ExchangeTransMatchBond5Service extends ExchangeTransMatchBondCommonService {

    /**
     * 配债
     * @param
     * @return
     */
    @Override
    protected void setDealTotal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
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
        trdFinProductDealTotal.setCpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitCprice()));
        // 全价总额
        trdFinProductDealTotal.setFpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitFprice()));
        // 交易本金
        trdFinProductDealTotal.setTradeAmt(ArithmeticUtil.subtract(trdFinProductDealTotal.getFpriceAmt(),entrustDto.getMarchFeeAmtPay()));
        // 利息总额
        trdFinProductDealTotal.setIntAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitInt()));
        // 到期收益率
        trdFinProductDealTotal.setYield(null);
        // 行权收益率
        trdFinProductDealTotal.setExerYield(null);
        // 首期结算方式
        trdFinProductDealTotal.setDeliveryType("DVP");
    }

    // add by etliu 20220220 start
    // 广农商二阶段 GNS-894:【交易查询】和【债券二级交易】页面查询结果：委托代码未展示
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
    // add by etliu 20220220 end

    @Override
    public List<StlPayOrdersDto> generateStlPayOrdersList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();

        StlPayOrdersDto stlPayOsrdersDto = createStlPayOrders(dealDto, entrustDto, trdProductDeal);
        // 收付方向
        stlPayOsrdersDto.setPs(DictCodeListExt1.收付方向_支出.getValue());
        stlPayOrdersList.add(stlPayOsrdersDto);

        return stlPayOrdersList;
    }

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
        trdProductDeal.setParValue(null);
        // 净价总额，交易份额，交易本金，利息总额，全价总额，随交易支付费用，交易金额，未随交易支付费用，总交易费用
        calcFeeAmtPay(dealDto, entrustDto,trdProductDeal);
        // 交易方向
        trdProductDeal.setPs(entrustDto.getPs());
        // 到期收益率和行权收益率
        this.calc(trdProductDeal, entrustDto);

        // GNS-879: 赋值投管业务类型 modify by fuyongsheng 20220217 start
        // trdProductDeal.setTgBusiType("163");
        // 投管业务类型
        trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        // GNS-879: 赋值投管业务类型 modify by fuyongsheng 20220217 end

        // GNS-879:交易日期应是清算日期 modify by fuyongsheng 20220217 start
        trdProductDeal.setTradeDate(dealDto.getSettleDate());
        // 净值日期
        trdProductDeal.setCpriceDate(dealDto.getTradeDate());
        // GNS-879:交易日期应是清算日期 modify by fuyongsheng 20220217 end

        // 到期收益率
        trdFinProductDealTotal.setYield(trdProductDeal.getYield());
        // 行权收益率
        trdFinProductDealTotal.setExerYield(trdProductDeal.getExerYield());

        return trdProductDeal;
    }
}

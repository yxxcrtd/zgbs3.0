package com.joyintech.tams.settle.batch.datagather.processor.bond;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.settle.batch.datadeal.service.StlDataDealProcessCommonService;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;

/**
 * 债券买入、卖出、行权回售
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherBond1Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherBond1Processor extends StlDataGatherBondBaseProcessor {

    @Autowired(required = false)
    private StlDataGatherService stlDataGatherService;

    @Autowired
    protected StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Override
    protected void setDealTotal(TotalTrdProductDealDto dealTotal, TrdProductDealDto trdProductDealDto) {
        // 业务类型
        dealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 单位净价
        dealTotal.setUnitCprice(trdProductDealDto.getUnitCprice());
        // 单位利息
        dealTotal.setUnitInt(trdProductDealDto.getUnitInt());
        // 单位全价
        dealTotal.setUnitFprice(trdProductDealDto.getUnitFprice());
        // 单位面值
        dealTotal.setParValue(trdProductDealDto.getParValue());
        // 净价总额
        dealTotal.setCpriceAmt(trdProductDealDto.getCpriceAmt());
        // 交易本金
        dealTotal.setTradeAmt(trdProductDealDto.getTradeAmt());
        // 利息总额
        dealTotal.setIntAmt(trdProductDealDto.getIntAmt());
        // 全价总额
        dealTotal.setFpriceAmt(trdProductDealDto.getFpriceAmt());
        // 到期收益率
        dealTotal.setYield(trdProductDealDto.getYield());
        // 行权收益率
        dealTotal.setExerYield(trdProductDealDto.getExerYield());
        // 首期结算方式
        dealTotal.setDeliveryType("DVP");
        // 交收方式
        dealTotal.setSettlemenType(trdProductDealDto.getSettlemenType());
    }

    @Override
    protected List<TrdTradeFeeDetail> generateTradeFeeDetail(List<TrdProductDealDto> dealList) {
        return createTradeFeeDetail(dealList,false, dealList.get(0).getCcy());
    }

    @Override
    protected List<StlPayOrders> generateStlPayOrdersList(List<TotalTrdProductDealDto> dealTotalList, List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
        List<StlPayOrders> payOrdersList = new ArrayList<>(dealTotalList.size());
        if(CollectionUtils.isNotEmpty(dealTotalList)) {
            for (int i = 0; i < dealTotalList.size(); i++) {
                TrdFinProductDealTotal total = dealTotalList.get(i);
                TrdProductDealDto dealDto = dealList.get(i);
                StlPayOrders stlPayOrders = createStlPayOrders(total, dealDto, settleTransDataDtos);
                // 收付方向
                if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dealDto.getPs())) {
                    stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
                }
                else {
                    stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
                }
                payOrdersList.add(stlPayOrders);
            }
        }
        return payOrdersList;
    }

    /**
     * 债券买入、卖出、行权回售
     * @param settleTransDataDtoList 按指令编号汇总后的成交明细，交易方向一致
     * @return
     */
    @Override
    protected List<TrdProductDealDto> createDeal(List<SettleTransDataDto> settleTransDataDtoList, boolean buildTradeId) {
        SettleTransDataDto settleTransDataDto =  settleTransDataDtoList.get(0);
        // 净价总额合计
        BigDecimal totalCpriceAmt = BigDecimal.ZERO;
        // 利息总额
        BigDecimal totalIntAmt = BigDecimal.ZERO;
        // 交易份额合计
        BigDecimal totalShareAmt = BigDecimal.ZERO;
        for (SettleTransDataDto item : settleTransDataDtoList) {
            totalCpriceAmt = ArithmeticUtil.doAdd(totalCpriceAmt, item.getCpriceAmt());
            totalShareAmt = ArithmeticUtil.doAdd(totalShareAmt, item.getShareAmt());
            totalIntAmt = ArithmeticUtil.doAdd(totalIntAmt, item.getIntAmt());
        }
        //单位净价 = ∑净价总额/∑交易份额
        BigDecimal unitCprice = ArithmeticUtil.divide(totalCpriceAmt, totalShareAmt, 8);
        //单位利息 = ∑利息总额/∑交易份额
        BigDecimal unitInt = ArithmeticUtil.divide(totalIntAmt, totalShareAmt, 8);
        // 交易字段设置共通
        TrdProductDealDto trdProductDeal = createProductDeal(settleTransDataDto, buildTradeId);
        // 交易编号（共通方法已设置）
        // 业务类型（共通方法已设置）
        // 金融产品代码（共通方法已设置）
        // 金融产品类型（投管分类）（共通方法已设置）
        // 分支序号（共通方法已设置）
        // 交易日（共通方法已设置）
        // 起息日（共通方法已设置）
        // 到期日（共通方法已设置）
        // 交付日（共通方法已设置）
        // 交割日（共通方法已设置）
        // 清算速度
        trdProductDeal.setSettleSpeed(settleTransDataDto.getSettleSpeed());
        // 交易币种（共通方法已设置）
        // 净值日期（共通方法已设置）
        // 单位净价
        trdProductDeal.setUnitCprice(unitCprice);
        // 单位利息
        trdProductDeal.setUnitInt(unitInt);
        // 单位全价
        trdProductDeal.setUnitFprice(ArithmeticUtil.doAdd(unitCprice, unitInt));
        // 单位面值
//        trdProductDeal.setParValue(null);
        // 净价总额，交易份额，交易本金，利息总额，全价总额，随交易支付费用，交易金额，未随交易支付费用，总交易费用
        calcFeeAmtPay(trdProductDeal, settleTransDataDtoList);
        // 交易方向
        trdProductDeal.setPs(settleTransDataDto.getPs());
        // 到期收益率和行权收益率
        stlDataGatherService.calc(trdProductDeal);
        // 投资目的（共通方法已设置）
        // 结算方式
        trdProductDeal.setSettMode("DVP");
        // 交易状态（共通方法已设置）
        // 是否已撤销（共通方法已设置）
        // 原始交易编号（共通方法已设置）
        // 是否结清（共通方法已设置）
        // 反向交易编号（共通方法已设置）
        // 反向交易投资目的（共通方法已设置）
        // 前置交易编号
//        trdProductDeal.setbTradeId(null);
        // 通道代码（共通方法已设置）
        // 交易对手（共通方法已设置）
        // 是否外部交易（共通方法已设置）
        // 交易场所（共通方法已设置）
        // 交易平台（共通方法已设置）
        // 交易市场（共通方法已设置）
        // 本方交易员（共通方法已设置）
        // 对手方交易员（共通方法已设置）
        // 对手方联系方式（共通方法已设置）
        // 证券管理户代码（共通方法已设置）
        // 关联系统交易编号（共通方法已设置）
        // 现金流代码（共通方法已设置）
        // 登记托管机构（共通方法已设置）
        // 备注（共通方法已设置）
        // 撤销说明（共通方法已设置）
        // 投管业务类型
        trdProductDeal.setTgBusiType(getTgBusiType(settleTransDataDto.getPs()));
        // 销售代码（共通方法已设置）
        // 计提份额（共通方法已设置）
        // 交收方式（共通方法已设置）
        // 席位号（共通方法已设置）
        // 投资组合代码（共通方法已设置）
        // 委托代码（共通方法已设置）
        // 成交编号（共通方法已设置）
        // 权益登记日（共通方法已设置）
        // 权益登记日份额（共通方法已设置）
        // 金融产品代码2
//        trdProductDeal.setFinprodId2(null);
//        // 金融产品类型2
//        trdProductDeal.setFinprodTypeExt(null);
//        // 分支序号2
//        trdProductDeal.setBranch2(null);
//        // 单位净价2
//        trdProductDeal.setUnitCprice2(null);
//        // 交易本金2
//        trdProductDeal.setPrinAmt2(null);
//        // 交易份额2
//        trdProductDeal.setShareAmt2(null);
//        // 利息总额2
//        trdProductDeal.setIntAmt2(null);
//        // 投资目的2
//        trdProductDeal.setInvAim2(null);
        return Arrays.asList(trdProductDeal);
    }

    /**
     * 获取投管业务类型
     * @param ps 交易方向
     * @return
     */
    private String getTgBusiType(String ps) {
        if(StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())) {
            return DictCodeListExt1.平安业务类型_债券类资产买入.getValue();
        }
        else if(StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
            return DictCodeListExt1.平安业务类型_债券类资产卖出.getValue();
        }
        else if(StringUtil.equals(ps, DictCodeList.交易方向_行权_回售.getValue())) {
            return DictCodeListExt1.平安业务类型_债券类资产行权.getValue();
        }
        return null;
    }
}

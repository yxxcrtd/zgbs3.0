package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.shares;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.TrdProductDealDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 股票（新股中签、增发(配售)、配股、买入、卖出、申购）
 * @author meifaxin
 * @date 2021/11/1
 * @see ExchangeTransMatchShares1Service
 * @since 1.0
 */
@Service
public class ExchangeTransMatchShares1Service extends ExchangeTransMatchSharesCommonService {
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 生成股票新股中签交易TRD_PRODUCT_DEAL的数据
     *
     * @param dealDto    ExchangeTransMatchCommonDto
     * @param entrustDto ExchangeTransMatchCommonEntrustDto
     */
    @Override
    public TrdProductDeal createDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto,TrdFinProductDealTotal trdFinProductDealTotal) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        // 随交易支付费用
        BigDecimal feeAmtPay = BigDecimal.ZERO;
        // 未随交易支付费用
        BigDecimal feeAmtUnpay = BigDecimal.ZERO;
        // ∑全价总额
        BigDecimal cpriceAmtSum = BigDecimal.ZERO;
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), dealDto.getIsPayWithTrade())) {
            feeAmtPay = ArithmeticUtil.doAdd(feeAmtPay, dealDto.getJsfAmt(), dealDto.getGhfAmt(), dealDto.getZgfAmt(),
                    dealDto.getSxfAmt(), dealDto.getSettleFee(), dealDto.getStampFee(), dealDto.getCommissionAmt());
        } else {
            feeAmtPay = ArithmeticUtil.doAdd(feeAmtPay, dealDto.getJsfAmt(), dealDto.getGhfAmt(), dealDto.getZgfAmt(),
                    dealDto.getSxfAmt(), dealDto.getSettleFee(), dealDto.getStampFee());
            feeAmtUnpay = ArithmeticUtil.doAdd(feeAmtUnpay, dealDto.getCommissionAmt());
        }
        // 交易编号
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setCommonBusinessType(DictCodeListExt2.共通业务类型_股票投资交易);
        trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));
        // 金融产品代码
        trdProductDeal.setFinprodId(dealDto.getFinprodId());
        // 金融产品类型
        trdProductDeal.setFinprodType(dealDto.getFinprodType());
        // 金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(dealDto.getFinprodType2());
        // 业务类型
        trdProductDeal.setBusiType(dealDto.getBusiType());
        // 分支序号
        trdProductDeal.setBranch(0L);
        // 交易日
        trdProductDeal.setTradeDate(dealDto.getTradeDate());
        // 交割日
        trdProductDeal.setSettleDate(dealDto.getSettleDate());
        // 清算速度
        trdProductDeal.setSettleSpeed(dealDto.getSettleSpeed());
        // 交易币种
        trdProductDeal.setCcy(dealDto.getCcy());
        // 净值日期
        trdProductDeal.setCpriceDate(dealDto.getTradeDate());
        // 单位净价
//            BigDecimal unitCprice = ArithmeticUtil.divide(cpriceAmtSum, dealDto.getShareAmt(), 14);
        trdProductDeal.setUnitCprice(dealDto.getUnitCprice());
//            // 配股需要计算价格
//            if (StringUtil.equals(dealDto.getPs(), DictCodeListExt2.交易方向_配股.getValue())){
//                trdProductDeal.setUnitCprice(ArithmeticUtil.divide(dealDto.getTradeAmt(), dealDto.getShareAmt(), 14));
//            }
        // 净价总额
        trdProductDeal.setCpriceAmt(dealDto.getFpriceAmt());
        // 交易份额
        trdProductDeal.setShareAmt(entrustDto.getMarchQty());
        // 交易本金
        trdProductDeal.setPrinAmt(entrustDto.getMarchAmt());
        // 随交易支付费用
        trdProductDeal.setFeeAmtPay(entrustDto.getMarchFeeAmtPay());
        // 交易金额
        // ZGTFCP-1094:股票需要根据交易方向加减费用 modify by fuyongsheng 20220316 start
        // trdProductDeal.setTradeAmt(ArithmeticUtil.subtract(entrustDto.getMarchAmt(), entrustDto.getMarchFeeAmtPay()));
        if(DictCodeList.交易方向_买入.getValue().equals(dealDto.getPs())
                || DictCodeList.交易方向_申购.getValue().equals(dealDto.getPs())
                || DictCodeListExt2.交易方向_增发.getValue().equals(dealDto.getPs())
                || DictCodeListExt2.交易方向_配股.getValue().equals(dealDto.getPs())){
            trdProductDeal.setTradeAmt(ArithmeticUtil.doAdd(entrustDto.getMarchAmt(), entrustDto.getMarchFeeAmtPay()));
        }else{
            trdProductDeal.setTradeAmt(ArithmeticUtil.subtract(entrustDto.getMarchAmt(), entrustDto.getMarchFeeAmtPay()));
        }
        // ZGTFCP-1094:股票需要根据交易方向加减费用 modify by fuyongsheng 20220316 end
        // 未随交易支付费用
        trdProductDeal.setFeeAmtUnpay(entrustDto.getMarchFeeAmtUnPay());
        // 总交易费用
        trdProductDeal.setFeeAmt(entrustDto.getMarchFeeAmtUnPay());
        // 交易方向
        trdProductDeal.setPs(dealDto.getPs());
        // 交易状态
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 是否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        trdProductDeal.setTradeMarket(dealDto.getTradeMarket());
        // 交易平台
        trdProductDeal.setTradePlat(dealDto.getTradePlat());
        // 交易市场
        trdProductDeal.setTradeMarketMode(dealDto.getTradeMarketMode());

        if (StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
        } else if (StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
            trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
        }
        // 证券管理户代码
        trdProductDeal.setSecManageAcctId(dealDto.getSecAcctId());
        // 登记托管机构
        trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
        // 投管业务类型
        trdProductDeal.setTgBusiType(DictCodeListExt2.平安业务类型_股票交易.getValue());
        // 交收方式
        trdProductDeal.setSettlemenType(dealDto.getSettleMode());
        // 席位号
        trdProductDeal.setSeatId(dealDto.getSeatNumber());
        // 投资组合代码
        trdProductDeal.setPortfolioId(entrustDto.getPortfolioId());

        if(StringUtil.equals(entrustDto.getEntrustClass(),DictCodeListExt3.投资指令类型_多组合单资产指令.getValue())) {
            trdProductDeal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        }

        // GNS-878:配股生成产品交易表的交易日期应该是清算日期 modify by fuyongsheng 20220217 start
        // GNS-862:配股的价格 modify by fuyongsheng 20220218 start
        if(DictCodeListExt2.交易方向_配股.getValue().equals(dealDto.getPs())){
            trdProductDeal.setTradeDate(dealDto.getSettleDate());
            trdProductDeal.setUnitCprice(entrustDto.getPrice());
            trdProductDeal.setCpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),trdProductDeal.getUnitCprice()));
            trdProductDeal.setUnitFprice(entrustDto.getPrice());
            trdProductDeal.setFpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),trdProductDeal.getUnitFprice()));
            trdProductDeal.setTradeAmt(ArithmeticUtil.subtract(trdProductDeal.getFpriceAmt(),entrustDto.getMarchFeeAmtPay()));
        }
        // GNS-862:配股的价格 modify by fuyongsheng 20220218 end
        // GNS-878:配股生成产品交易表的交易日期应该是清算日期 modify by fuyongsheng 20220217 end
        trdProductDeal.setRemark("数据清算自动处理");
        return trdProductDeal;
    }
}



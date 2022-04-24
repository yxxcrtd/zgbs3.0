package com.joyintech.tams.exchange.common.utils;

import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.bean.OrderPlaceBean;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.db.domain.BatchInfo;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;

import java.util.Objects;

public class CheckParamUtils {
    public static ErrorCode checkOrderParam(OrderPlaceBean orderPlaceBean) {
        return getErrorCode(orderPlaceBean.getUserId(), orderPlaceBean.getInstructionId(), orderPlaceBean.getEntrustBs(), orderPlaceBean.getStockCode(), orderPlaceBean.getEntrustProp(), orderPlaceBean.getBatchInfo(), "", "", orderPlaceBean.getOffsetFlag(), orderPlaceBean.getHedgeFlag());
    }

    public static ErrorCode checkIntegratedEntrustParam(IntegratedEntrust integratedEntrust) {
        if (EntrustUtils.isNewBond(integratedEntrust.getBusiTypeTg())) {
            return getErrorCode(integratedEntrust);
        }

        return getErrorCode(integratedEntrust.getUserId(), integratedEntrust.getInstructionId(), integratedEntrust.getEntrustBs(), integratedEntrust.getStockCode(), integratedEntrust.getEntrustProp(), null, integratedEntrust.getLiaisonName(), integratedEntrust.getContractId(), null, null);
    }

    public static ErrorCode checkFuturesOrderParam(String market, String offsetFlag, String hedgeFlag) {
        if (Market.isFutures(market)) {
            if (Strings.isNullOrEmpty(offsetFlag) || Strings.isNullOrEmpty(hedgeFlag)) {
                return ErrorCode.DERIVATIVE_PARAM_ERROR;
            }
        }
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode getErrorCode(String userId, String instructionId, String entrustBs, String stockCode, String entrustProp, BatchInfo batchInfo, String liaisonName, String contractId, String offsetFlag, String hedgeFlag) {
        if (Strings.isNullOrEmpty(instructionId))
            return ErrorCode.REQ_PARAMETER_INSTRUCTION_ID_ERROR;
        if (Strings.isNullOrEmpty(entrustBs))
            return ErrorCode.REQ_PARAMETER_ENTRUST_BS_ERROR;
        if (Strings.isNullOrEmpty(stockCode) || StockCodeUtils.getMarketAndStockCode(stockCode).length != 2)
            return ErrorCode.REQ_PARAMETER_STOCK_CODE_ERROR;
        if (Objects.isNull(batchInfo) && Strings.isNullOrEmpty(entrustProp))
            return ErrorCode.REQ_PARAMETER_ENTRUST_PROP_ERROR;

        // 检查适配的交易单元
        String market = StockCodeUtils.getMarket(stockCode);
        if (IntegratedEntrustUtils.isFixedIncome(entrustProp) && Market.SZSE.value.equals(market))
            return ErrorCode.SH_UN_SUPPORT_INSTRUMENT;
        if (IntegratedEntrustUtils.isProtocolBack_SZ(entrustProp) && Market.SHSE.value.equals(market))
            return ErrorCode.SH_UN_SUPPORT_PROTOCOL_BACK_INSTRUMENT;
        if (IntegratedEntrustUtils.isProtocolBack_SH(entrustProp) && Market.SZSE.value.equals(market))
            return ErrorCode.SZ_UN_SUPPORT_PROTOCOL_BACK_INSTRUMENT;
        if (EntrustProp.APPLY.value.equals(entrustProp) && EntrustBs.SELL.value.equals(entrustBs)) {
            return ErrorCode.APPLY_UN_SELL;
        }
        if (EntrustProp.HS.value.equals(entrustProp) && EntrustBs.BUY.value.equals(entrustBs)) {
            return ErrorCode.HS_UN_BUY;
        }
        if (EntrustProp.INTENTION.value.equals(entrustProp) && "".equals(liaisonName)) {
            return ErrorCode.INTENTION_UN_LINKMAN;
        }
        if (EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value.equals(entrustProp) && "".equals(contractId)) {
            return ErrorCode.SH_FIXEDINCOME_BAOJIA_OPPOSITE_UN_CONTRACTID;
        }
        //检查期货平仓套保标志
        return CheckParamUtils.checkFuturesOrderParam(market, offsetFlag, hedgeFlag);
    }

    public static ErrorCode getErrorCode(IntegratedEntrust integratedEntrust) {
        final String stockCode = integratedEntrust.getStockCode();
        if (Strings.isNullOrEmpty(stockCode) || StockCodeUtils.getMarketAndStockCode(stockCode).length != 2) {
            return ErrorCode.REQ_PARAMETER_STOCK_CODE_ERROR;
        }
        // 检查委托属性
        if (IntegratedEntrustUtils.isNegotiatedTrade(integratedEntrust.getEntrustProp())) {
            return ErrorCode.UN_SUPPORT_ENTRUST_PROP;
        }
        final String market = StockCodeUtils.getMarket(stockCode);
        //检查本方交易员
        if (Strings.isNullOrEmpty(integratedEntrust.getTradeId())) {
            return ErrorCode.TRADE_NOT_EMPTY;
        }
        //检查本方交易商
        if (Strings.isNullOrEmpty(integratedEntrust.getDealerId()) || Strings.isNullOrEmpty(integratedEntrust.getDealerName())) {
            return ErrorCode.DEALER_NOT_EMPTY;
        }
        //检查本方交易主体
        if (Strings.isNullOrEmpty(integratedEntrust.getInvestorId()) || Strings.isNullOrEmpty(integratedEntrust.getInvestorName())) {
            return ErrorCode.INVESTOR_NOT_EMPTY;
        }
        //检查交易对手
        if (Strings.isNullOrEmpty(integratedEntrust.getCounterId())) {
            return ErrorCode.COUNTER_NOT_EMPTY;
        }
        //检查对手方交易员
        if (Strings.isNullOrEmpty(integratedEntrust.getPropTradeId())) {
            return ErrorCode.PROP_TRADE_NOT_EMPTY;
        }
        //检查对手方交易商
        if (Strings.isNullOrEmpty(integratedEntrust.getPropDealerId()) || Strings.isNullOrEmpty(integratedEntrust.getPropDealerName())) {
            return ErrorCode.PROP_DEALER_NOT_EMPTY;
        }
        //检查对手方交易主体
        if (Strings.isNullOrEmpty(integratedEntrust.getPropInvestorId()) || Strings.isNullOrEmpty(integratedEntrust.getPropInvestorName())) {
            return ErrorCode.PROP_INVESTOR_NOT_EMPTY;
        }
        //检查约定号
        if (Strings.isNullOrEmpty(integratedEntrust.getContractId())) {
            return ErrorCode.CONTRACT_ID_NOT_EMPTY;
        }

        return getErrorCode(integratedEntrust.getUserId(), integratedEntrust.getInstructionId(), integratedEntrust.getEntrustBs(), stockCode, integratedEntrust.getEntrustProp(), null, integratedEntrust.getLiaisonName(), integratedEntrust.getContractId(), null, null);
    }

    public static boolean checkBuyUnit(OrderInterface orderInterface, long entrustVolume, int buyUnit) {
        if (buyUnit == 0) return true;
        if (entrustVolume % buyUnit != 0) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足最小买入单位:" + buyUnit);
            return false;
        }
        return true;
    }

}

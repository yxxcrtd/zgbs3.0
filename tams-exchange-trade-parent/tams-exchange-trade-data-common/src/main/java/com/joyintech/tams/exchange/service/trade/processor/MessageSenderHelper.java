package com.joyintech.tams.exchange.service.trade.processor;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.investment.TradeMessage;
import com.joyintech.tams.exchange.common.constant.BusinessFlag;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.constant.instruction.BusinessType;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeList;
import com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.IntegratedEntrustUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionPledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * @author lifangpeng
 * @date 2022/4/12 13:51
 */
@Component
public class MessageSenderHelper {
    private static final Logger logger = LoggerFactory.getLogger(MessageSenderHelper.class);

    @Autowired
    private InstructionPledgeService instructionPledgeService;

    public TradeMessage createTradeMessage(Trade trade, Instruction instruction, EntrustInterface entrust) {
        TradeMessage tradeMessage = TradeMessage.createTradeMessage(trade, entrust);
        //委托数量
        tradeMessage.setEntrustVolume((double) entrust.getEntrustVolume());
        if (Objects.nonNull(instruction) && Objects.nonNull(tradeMessage)) {
            createTradeMsg(trade, instruction, entrust, tradeMessage);
            //todo 期货保证金
            if (InvestAssetTypeEnum.FUTURES.value.equals(instruction.getAssetType())) {
                tradeMessage.setDealMargin(TradeUtils.multiply(trade.getTradedBalance(), BigDecimal.valueOf(0.1), 0));
            }
        } else {
            logger.warn("instruction not found: {}, trade id :{}", trade.getInstructionId(), trade.getId());
        }
        return tradeMessage;
    }

    private void createTradeMsg(Trade trade, Instruction instruction, EntrustInterface entrust, TradeMessage tradeMessage) {
        tradeMessage.setAcctNo(instruction.getAcctNo());
        tradeMessage.setSecAcctId(instruction.getSecAcctId());
        tradeMessage.setPortfolioId(instruction.getPortfolioId());
        tradeMessage.setTradeMarket(instruction.getTradeMarket());
        tradeMessage.setTradePlat(instruction.getTradePlat());
        tradeMessage.setPS(instruction.getPs());
        tradeMessage.setCcy(instruction.getCcy());
        tradeMessage.setTradingDay(trade.getTradingDay());
        if (InvestmentAssetType1.BIDDING_REPO.value.equals(instruction.getAssetType1())) {
            //投管要求质押式回购这里传入期限品种
            tradeMessage.setFinprodMarketId(trade.getStockName());
        } else if (InvestAssetTypeEnum.BOND.value.equals(instruction.getAssetType())) {
            //投管要求债券买卖，成交价格统一填净价价格
            tradeMessage.setDealPrice(entrust.getEntrustPrice());
        }
        if (entrust instanceof IntegratedEntrust) {
            tradeMessage.setCounterId(((IntegratedEntrust) entrust).getCounterId());
        }
        String finprodId = instruction.getFinprodId();
        if (BusinessFlag.PROTOCOL_BACK.value.equals(trade.getBusinessFlag())) {
            tradeMessage.setValueDate(((IntegratedEntrust) entrust).getFirstSettleDate());
            tradeMessage.setMaturityDate(((IntegratedEntrust) entrust).getBackDate());
            tradeMessage.setCounterId((((IntegratedEntrust) entrust).getLiaisonName()));
            // 确保协议回购，无抵押券时才组装相关的finprodId
            InstructionPledge instructionPledge = instructionPledgeService.queryInstructionPledgesByInstructionIdAndStockCode(instruction.getEntrustId(), entrust.getStockCode());
            if (Objects.isNull(instructionPledge) && Strings.isNullOrEmpty(finprodId)) {
                finprodId = IntegratedEntrustUtils.formatFinprodId(entrust.getAssetType(), entrust.getStockCode());
                logger.info("未找到相关抵押券信息,填入 FinprodId {}", finprodId);
            }
            //当期续作首期交易编号，更新为当前笔成交编号+
            if (EntrustProp.isRepurchase_First(entrust.getEntrustProp())) {
                tradeMessage.setTrdMatchID(trade.getBrokerTradeId());
            } else {
                tradeMessage.setTrdMatchID(((IntegratedEntrust) entrust).getOrigSerialNo());
                // 中途换券，投管要求传入折算比列
                if (EntrustProp.isRepurchaseExchange(trade.getEntrustProp())) {
                    tradeMessage.setPledgeRatio(trade.getDiscount());
                }
            }
        }
        tradeMessage.setFinprodId(finprodId);
    }


    public TradeMessage createTradeMsg(Trade trade, Instruction instruction, EntrustInterface entrust) {
        TradeMessage tradeMessage = new TradeMessage();
        tradeMessage.setId(trade.getId());
        tradeMessage.setSettleDay(trade.getTradingDay());
        tradeMessage.setEntrustId(trade.getInstructionId());
        tradeMessage.setOrderId(trade.getEntrustId());
        tradeMessage.setEntrustNo(trade.getBrokerEntrustId());
        tradeMessage.setDealNo(trade.getBrokerTradeId());
        tradeMessage.setAcctNo(instruction.getAcctNo());
        tradeMessage.setFinprodMarketId(trade.getStockCode());
        tradeMessage.setDealVolume((double) trade.getTradedVolume());
        tradeMessage.setDealPrice(trade.getTradedPrice());
        tradeMessage.setDealBalance(trade.getTradedBalance());
        tradeMessage.setTradingDay(trade.getTradingDay());
        tradeMessage.setSecAcctId(instruction.getSecAcctId());
        tradeMessage.setPortfolioId(instruction.getPortfolioId());
        tradeMessage.setTradeMarket(instruction.getTradeMarket());
        tradeMessage.setTradePlat(instruction.getTradePlat());
        tradeMessage.setPS(instruction.getPs());
        tradeMessage.setCcy(instruction.getCcy());
        tradeMessage.setFinprodId(instruction.getFinprodId());
        //委托数量
        tradeMessage.setEntrustVolume((double) entrust.getEntrustVolume());

        if (entrust instanceof IntegratedEntrust) {
            tradeMessage.setCounterId(((IntegratedEntrust) entrust).getCounterId());
        }

        // 此处 if-else 是否合理？
        if (InvestmentAssetType1.BIDDING_REPO.value.equals(instruction.getAssetType1())) {
            //投管要求质押式回购这里传入期限品种
            tradeMessage.setFinprodMarketId(trade.getStockName());
        } else if (InvestAssetTypeEnum.BOND.value.equals(instruction.getAssetType())) {
            //投管要求债券买卖，成交价格统一填净价价格
            tradeMessage.setDealPrice(entrust.getEntrustPrice());
        }
        // 协议回购的特殊处理
        if (BusinessFlag.PROTOCOL_BACK.value.equals(trade.getBusinessFlag())) {
            tradeMessage.setValueDate(((IntegratedEntrust) entrust).getFirstSettleDate());
            tradeMessage.setMaturityDate(((IntegratedEntrust) entrust).getBackDate());
            tradeMessage.setCounterId((((IntegratedEntrust) entrust).getLiaisonName()));

            // 金融产品代码
            // 确保协议回购，无抵押券时才组装相关的finprodId
            InstructionPledge instructionPledge = instructionPledgeService.queryInstructionPledgesByInstructionIdAndStockCode(instruction.getEntrustId(), entrust.getStockCode());
            String finprodId = instruction.getFinprodId();
            if (Objects.isNull(instructionPledge) && Strings.isNullOrEmpty(finprodId)) {
                finprodId = IntegratedEntrustUtils.formatFinprodId(entrust.getAssetType(), entrust.getStockCode());
                logger.info("未找到相关抵押券信息,填入 FinprodId {}", finprodId);
                tradeMessage.setFinprodId(finprodId);
            }
            // 首期交易编号
            // 当期续作首期交易编号，更新为当前笔成交编号+
            if (EntrustProp.isRepurchase_First(entrust.getEntrustProp())) {
                tradeMessage.setTrdMatchID(trade.getBrokerTradeId());
            } else {
                tradeMessage.setTrdMatchID(((IntegratedEntrust) entrust).getOrigSerialNo());
            }

            // 折算比例
            if (EntrustProp.isRepurchaseExchange(trade.getEntrustProp())) {
                tradeMessage.setPledgeRatio(trade.getDiscount());
            }
        } else {
            // 非协议回购的特殊处理
            tradeMessage.setFullPrice(trade.getTradedVolume() > 0 ?
                    TradeUtils.divide(trade.getTradedBalance(), trade.getTradedVolume(), BigDecimal.ROUND_HALF_UP).doubleValue() : 0);
        }
        if (Objects.nonNull(trade.getFullPrice()) && trade.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
            tradeMessage.setFullPrice(trade.getFullPrice().doubleValue());
        }

        //协议回购
        if (BusinessFlag.PROTOCOL_BACK.value.equals(trade.getBusinessFlag())) {
            //到期结算金额
            tradeMessage.setDealBalance(entrust.getBackBalance());
        } else if (InvestAssetTypeEnum.isBond(trade.getAssetType())
                && EntrustProp.isTrade(entrust.getEntrustProp())
                && Objects.nonNull(tradeMessage.getFullPrice())
                && tradeMessage.getFullPrice() != 0) {
            //成交全价金额
            tradeMessage.setDealBalance(TradeUtils.multiply(BigDecimal.valueOf(tradeMessage.getFullPrice()), tradeMessage.getDealVolume().longValue(), 2));
        } else if(InvestAssetTypeEnum.isCreditProtectionCertificate(trade.getAssetType())){ //信用保护凭证
            tradeMessage.setCounterId(((IntegratedEntrust) entrust).getCounterId());//交易对手
            tradeMessage.setPropAcctNo(((IntegratedEntrust) entrust).getPropAcctNo());//对手资金账户
        }

        // 业务类型（手工交收用）
        String busiType = handleBusiType(trade, instruction, entrust);
        tradeMessage.setBusiType(busiType);

        String extend2 = trade.getExtend2();
        Map<String, Object> map;
        if (StringUtil.isNotEmpty(extend2)) {
            map = JsonUtils.getObjectFromJson(extend2, Map.class);
        } else {
            map = Maps.newHashMap();
        }
        map.put("busiType", busiType);
        trade.setExtend2(JsonUtils.getJsonFromObject(map));

        return tradeMessage;
    }

    private String handleBusiType(Trade trade, Instruction instruction, EntrustInterface entrust) {
        String bsFlag = entrust.getBsFlag();
        String entrustBs = entrust.getEntrustBs();
        String tradePlat = instruction.getTradePlat();
        String assetType = instruction.getAssetType();

        // 协议回购
        if (IntegratedEntrustUtils.isProtocolBack(trade.getEntrustProp())) {
            return handleProtocolBakBusiType(trade, instruction, entrust);
        }

        // 深交所综合协议平台债券
        if (DictCodeList.交易平台_综合协议交易平台.getValue().equals(tradePlat) && InvestAssetTypeEnum.isBond(assetType)) {
            if (EntrustBs.BUY.value.equals(entrustBs)) {
                return BusinessType.INTEGRATED_PROTOCOL_BUY.value;
            } else if (EntrustBs.SELL.value.equals(entrustBs)) {
                return BusinessType.INTEGRATED_PROTOCOL_SELL.value;
            }
        }

        // 上交所固定收益平台债券
        if (DictCodeList.交易平台_固定收益平台.getValue().equals(tradePlat) && InvestAssetTypeEnum.isBond(assetType)
                // GNS-952:更换判断固定收益平台方法 modify by fuyongsheng 20220224 start
                // && BusinessFlag.FIXED_INCOME.value.equals(bsFlag)
                && IntegratedEntrustUtils.isFixedIncome(trade.getEntrustProp())
            // GNS-952:更换判断固定收益平台方法 modify by fuyongsheng 20220224 end
        ) {
            // 上海固收
            if (EntrustBs.BUY.value.equals(entrustBs)) {
                return BusinessType.FIXED_INCOME_BUY.value;
            } else if (EntrustBs.SELL.value.equals(entrustBs)) {
                return BusinessType.FIXED_INCOME_SELL.value;
            }
        }

        return null;
    }

    private String handleProtocolBakBusiType(Trade trade, Instruction instruction, EntrustInterface entrust) {
        String entrustProp = entrust.getEntrustProp();
        String entrustBs = entrust.getEntrustBs();

        String busiType = null;

        // 上海协议回购
        // 初始交易
        if (EntrustProp.SH_REPURCHASE_TRADE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value.equals(entrustProp)) {
            // GNS-940:正回购是买 modify by fuyongsheng 20220222 start
            // busiType = EntrustBs.SELL.value.equals(entrustBs) ?
            busiType = EntrustBs.BUY.value.equals(entrustBs) ?
                    // GNS-940:正回购是买 modify by fuyongsheng 20220222 end
                    BusinessType.SH_SELL_PROTOCOL_BACK_BEGIN.value : BusinessType.SH_ANTI_PROTOCOL_BACK_BEGIN.value;
        }
        // 到期续作
        else if (EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(entrustProp)) {
            // GNS-940:正回购是买 modify by fuyongsheng 20220222 start
            // busiType = EntrustBs.SELL.value.equals(entrustBs) ?
            busiType = EntrustBs.BUY.value.equals(entrustBs) ?
                    // GNS-940:正回购是买 modify by fuyongsheng 20220222 end
                    BusinessType.SH_SELL_PROTOCOL_BACK_RENEWAL.value : BusinessType.SH_ANTI_PROTOCOL_BACK_RENEWAL.value;
        }
        // 中途换券
        else if (EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value.equals(entrustProp)) {
            // GNS-940:正回购是买 modify by fuyongsheng 20220222 start
            // busiType = EntrustBs.SELL.value.equals(entrustBs) ?
            busiType = EntrustBs.BUY.value.equals(entrustBs) ?
                    // GNS-940:正回购是买 modify by fuyongsheng 20220222 end
                    BusinessType.SH_SELL_PROTOCOL_BACK_EXCHANGE.value : BusinessType.SH_ANTI_PROTOCOL_BACK_EXCHANGE.value;
        }
        // GNS-954:上交所协议回购到期购回 modify by fuyongsheng 20220224 start
        // 到期购回
        else if (EntrustProp.SH_REPURCHASE_EXPIRE.value.equals(entrustProp)) {
            busiType = EntrustBs.BUY.value.equals(entrustBs) ? BusinessType.SH_SELL_PROTOCOL_BACK_BUYBACK.value : BusinessType.SH_ANTI_PROTOCOL_BACK_BUYBACK.value;
        }
        // GNS-954:上交所协议回购到期购回 modify by fuyongsheng 20220224 end
        // 解除质押
        else if (EntrustProp.SH_REPURCHASE_RELEASE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value.equals(entrustProp)) {
            // GNS-940:正回购是买 modify by fuyongsheng 20220222 start
            // busiType = EntrustBs.SELL.value.equals(entrustBs) ?
            busiType = EntrustBs.BUY.value.equals(entrustBs) ?
                    // GNS-940:正回购是买 modify by fuyongsheng 20220222 end
                    BusinessType.SH_SELL_PROTOCOL_BACK_RELEASE.value : BusinessType.SH_ANTI_PROTOCOL_BACK_RELEASE.value;
        }
        // 提前购回
        else if (EntrustProp.SH_REPURCHASE_TERMINATE.value.equals(entrustProp)
                // GNS-955:增加提前购回判断条件 modify by fuyongsheng 20220224 start
                || EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TERMINATE_REFUSE.value.equals(entrustProp)
            // GNS-955:增加提前购回判断条件 modify by fuyongsheng 20220224 end
        ) {
            // GNS-940:正回购是买 modify by fuyongsheng 20220222 start
            // busiType = EntrustBs.SELL.value.equals(entrustBs) ?
            busiType = EntrustBs.BUY.value.equals(entrustBs) ?
                    // GNS-940:正回购是买 modify by fuyongsheng 20220222 end
                    BusinessType.SH_SELL_PROTOCOL_BACK_TERMINATE.value : BusinessType.SH_ANTI_PROTOCOL_BACK_TERMINATE.value;
        }

        // 深圳协议回购
        // 初始交易
        else if (EntrustProp.SZ_REPURCHASE_BEGIN.value.equals(entrustProp)) {
            // GNS-940:正回购是买 modify by fuyongsheng 20220222 start
            // busiType = EntrustBs.SELL.value.equals(entrustBs) ?
            busiType = EntrustBs.BUY.value.equals(entrustBs) ?
                    // GNS-940:正回购是买 modify by fuyongsheng 20220222 end
                    BusinessType.SZ_SELL_PROTOCOL_BACK_BEGIN.value : BusinessType.SZ_ANTI_PROTOCOL_BACK_BEGIN.value;
        }
        // 提前购回&到期购回
        else if (EntrustProp.SZ_REPURCHASE_BUYBACK.value.equals(entrustProp)) {
            // modify by etliu 20220226 start
            // 广农商二阶段 GNS-953:【广农商场内清算】深交所协议回购 正/逆回购首期，手工交收-日终清结算后，头寸更新有误
//            if (trade.getTradingDay().equals(((IntegratedEntrust) entrust).getBackDate())) {
            if (StringUtil.equals("T95", instruction.getPs())) {
                // modify by etliu 20220226 end
                // GNS-940:正回购是买 modify by fuyongsheng 20220222 start
                // busiType = EntrustBs.SELL.value.equals(entrustBs) ?
                busiType = EntrustBs.BUY.value.equals(entrustBs) ?
                        // GNS-940:正回购是买 modify by fuyongsheng 20220222 end
                        BusinessType.SZ_SELL_PROTOCOL_BACK_BUYBACK.value : BusinessType.SZ_ANTI_PROTOCOL_BACK_BUYBACK.value;
            } else {
                // GNS-940:正回购是买 modify by fuyongsheng 20220222 start
                // busiType = EntrustBs.SELL.value.equals(entrustBs) ?
                busiType = EntrustBs.BUY.value.equals(entrustBs) ?
                        // GNS-940:正回购是买 modify by fuyongsheng 20220222 end
                        BusinessType.SZ_SELL_PROTOCOL_BACK_TERMINATE.value : BusinessType.SZ_ANTI_PROTOCOL_BACK_TERMINATE.value;
            }

        }
        // 到期续作
        else if (EntrustProp.SZ_REPURCHASE_RENEWAL.value.equals(entrustProp)) {
            // GNS-940:正回购是买 modify by fuyongsheng 20220222 start
            // busiType = EntrustBs.SELL.value.equals(entrustBs) ?
            busiType = EntrustBs.BUY.value.equals(entrustBs) ?
                    // GNS-940:正回购是买 modify by fuyongsheng 20220222 end
                    BusinessType.SZ_SELL_PROTOCOL_BACK_RENEWAL.value : BusinessType.SZ_ANTI_PROTOCOL_BACK_RENEWAL.value;
        }

        return busiType;
    }

}

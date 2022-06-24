package com.joyintech.tams.invest.common.trade.service.impl.etrcash;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年06月24日
 * @since 1.0
 */
@Service
public class EtrCpcCashPositionDealService extends EtrCashPositionBaseService{

    public static void deal信用保护凭证EntrustClose(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
        //可转债特殊处理
        EtrEntrustAdd etrEntrustAdd = entrustCommonDto.getEtrEntrustAdd();
        // 交易方向
        String ps = etrEntrust.getPs();
        BigDecimal etrFinish = EtrCommonCashPositionDealService.getEtrFinishAmt(etrEntrust.getEntrustId());
        // BigDecimal 委托全价 = gen委托全价(etrEntrust.getEntrustId(),entrustCommonDto.getEtrEntrust().getEntrustDate());

        Map<String, Object> map = new HashMap<>();
        map.put("entrustId", etrEntrust.getEntrustId());
        map.put("portfolioId", etrEntrust.getPortfolioId());

        if (DictCodeList.交易方向_买入.getValue().equals(ps)
                // add by dubaojun 2020-08-14 场内交易增加配债逻辑 start
                || DictCodeListExt3.交易方向_配债.getValue().equals(ps)
                || DictCodeList.交易方向_申购.getValue().equals(ps)) {
            // add by dubaojun 2020-08-14 场内交易增加配债逻辑 end
            // 可用现金 关闭日增加剩余委托券面总额*委托全价/100
            EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                    entrustCommonDto);
            String acctId = null;
            // if (StringUtil.isNotEmpty(etrEntrust.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(
            //                // etrCommonCashPositionDealService.queryChlType(etrEntrust.getChlId()))) {
            // acctId = EtrCommonCashPositionDealService.select通道资金账户(etrEntrust.getChlId());
            // }
            // else {
            // acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
            // DictCodeListExt2.资金账户类型_托管户.getValue());
            // }
            if(StringUtil.isNotEmpty(etrEntrustAdd) &&
                    StringUtil.isNotEmpty(etrEntrustAdd.getPurchType()) && StringUtil.equals(etrEntrustAdd.getPurchType(), DictCodeListExt2.申购类型_优先配售.getValue())){
                acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
            }else{
                String secManageAcctId = null;
                if(StringUtil.isNotEmpty(etrEntrustAdd)){
                    secManageAcctId = entrustCommonDto.getEtrEntrustAdd().getSecAcctId();
                }
                acctId = gen账号(etrEntrust.getTradeMarket(), etrEntrust.getChlId(), etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                        secManageAcctId,entrustCommonDto.getEtrEntrust().getCounterId(), etrCashPositionFlowDto);
            }
            //查询指令现金流水，获取需要冲销的指令金额
            BigDecimal amount =  EtrCommonCashPositionDealService.getEtrSurplusAmt(etrEntrust.getEntrustId(), etrEntrust.getPortfolioId());
            // 场内指令关闭：指令金额-已完成的金额，
            if(StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    ||StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                //场内交易的日间交易没有产生指令现金流水，所以amout不包含已完成的交易的金额，需要查一次临时交易表
                map.put("entrustClass", etrEntrust.getEntrustClass());
                amount = ArithmeticUtil.subtract(amount, EtrCommonCashPositionDealService.getEtrExchangeFinishAmtMuilt(map));
                //amount = ArithmeticUtil.subtract(amount, EtrCommonCashPositionDealService.getEtrExchangeFinishAmt(map));
            }
            if (StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
                map.put("sumTradeFlag", "01");
                amount = ArithmeticUtil.subtract(amount, EtrCommonCashPositionDealService.getEtrExchangeFinishAmtMuilt(map));
            }

            etrCashPositionFlowDto.setAmount(amount);
            etrCashPositionFlowDto.setAcctId(acctId);
            etrCashPositionFlowDto.setOffsetId(etrEntrust.getEntrustId());
            etrCashPositionFlowDto.setCloseFlag("01"); // 委托关闭特殊处理, 委托关闭后, 再撤销交易时, 委托状态会变成已接收, 这时应该要把这笔数据删除
            etrCashPositionFlowDto.setOffsetFlag("01");
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
        }
        else if (is卖出(ps)) {
            EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(entrustCommonDto);
            // 可用持仓 关闭日增加剩余委托券面总额/100
            if (StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                // 场内交易的日间交易已完成额度不能取额度表里的数据，因为如果是按金额下指令的，额度表中存的是金额，所以从交易表和交易零时表中查询实际发生的额度汇总
                // 冲销是否sum生效交易数据
                map.put("sumTradeFlag","01");
                map.put("entrustClass", etrEntrust.getEntrustClass());
                etrFinish = EtrCommonCashPositionDealService.getEtrExchangeFinishAmtMuilt(map);
                //etrFinish = EtrCommonCashPositionDealService.getEtrExchangeFinishAmt(map);
                if (StringUtil.equals(DictCodeListExt3.交易方向_质押券出库.getValue(), ps) || StringUtil.equals(DictCodeListExt3.交易方向_质押券入库.getValue(), ps)) {
                    etrFinish = etrCommonCashPositionDealService.selectSumStandardTicket(etrEntrust.getEntrustId(), ps);
                    if(null == etrFinish){//没有成交任何标准券
                        etrFinish = new BigDecimal(0);
                    }
                }
                if (StringUtil.equals(DictCodeListExt3.交易方向_质押券出库.getValue(), ps)) {// 标准券出入库
                    etrFinish = etrFinish.multiply(new BigDecimal(-1));
                    etrSecPositionFlowDto.setFinprodId("XN_PRODID999");
                    etrSecPositionFlowDto.setAmount(ArithmeticUtil.subtract(etrEntrust.getEntrustAmt(), etrFinish));
                } else {
                    etrSecPositionFlowDto.setAmount(ArithmeticUtil.subtract(etrEntrust.getEntrustNum(), etrFinish));
                }
            } else {
                etrSecPositionFlowDto.setAmount(ArithmeticUtil.subtract(etrEntrust.getEntrustNum(), etrFinish));
            }
            etrSecPositionFlowDto.setCloseFlag("01"); // 委托关闭特殊处理, 委托关闭后,再撤销交易时, 委托状态会变成已接收,这时应该要把这笔数据删除
            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
        }
    }


    /**
     * 获取现金流水需要的账号
     *
     * @param tradePlace 交易场所
     * @param chlId 通道代码
     * @param portfolioId 组合代码
     * @param ccy 币种
     * @param secManageAcctId 证券托管户
     * @param counterId 交易对手
     * @param is卖出  是否是卖出
     * @param etrCashPositionFlowDto 流水dto
     * @return 账号
     */
    private static String gen账号(String tradePlace, String chlId, String portfolioId, String ccy, String secManageAcctId,String counterId,
                                boolean is卖出,
                                EtrCashPositionFlowDto etrCashPositionFlowDto){
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())
                || (StringUtil.equals(chlType, DictCodeListExt2.划款方式_逐笔划款.getValue()) && StringUtil.isBlank(counterId))) {
            etrCashPositionFlowDto.setChlFinprodId(chlId);
            return EtrCommonCashPositionDealService.select通道资金账户(chlId);
        }
        else {
            // 交易所无通道
            etrCashPositionFlowDto.setChlFinprodId(null);
            if (StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_境外交易场所.getValue())) {
                return EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt2.资金账户类型_托管户.getValue());
            }
            else if ((!StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_银行间.getValue()) &&
                    !StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_商业银行柜台.getValue()))
                    && StringUtil.isNotEmpty(secManageAcctId) &&!(is卖出&&EtrCommonCashPositionDealService.isInTrade(counterId))) {
                return EtrCommonCashPositionDealService.select资金账户(secManageAcctId, ccy);
            }
            else {
                // 银行间深交所修改需求同一使用托管户（逐笔通道）
                return EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt2.资金账户类型_托管户.getValue());
            }
        }
    }

    private static String gen账号1(String tradePlace, String chlId, String portfolioId, String ccy, String secManageAcctId,String counterId,
                                 boolean is卖出,
                                 EtrCashPositionFlowDto etrCashPositionFlowDto){
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
            etrCashPositionFlowDto.setChlFinprodId(chlId);
            return EtrCommonCashPositionDealService.select通道资金账户(chlId);
        }
        else {
            // 交易所无通道
            etrCashPositionFlowDto.setChlFinprodId(null);
            if (StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_境外交易场所.getValue())) {
                return EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt2.资金账户类型_托管户.getValue());
            }
          /* else if (!StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_银行间.getValue()) && StringUtil.isNotEmpty(secManageAcctId)
           &&!(is卖出&&EtrCommonCashPositionDealService.isInTrade(counterId))) {
           return EtrCommonCashPositionDealService.select资金账户(secManageAcctId, ccy);
          }*/
            else {
                // 银行间深交所修改需求同一使用托管户（逐笔通道）
                return EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt2.资金账户类型_托管户.getValue());
            }
        }
    }

    private static boolean is卖出(String 交易方向) {
        if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), 交易方向)
                || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), 交易方向)
                || StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), 交易方向)
                || StringUtil.equals(DictCodeListExt3.交易方向_债转股.getValue(), 交易方向)
                || StringUtil.equals(DictCodeListExt3.交易方向_质押券入库.getValue(), 交易方向)
                || StringUtil.equals(DictCodeListExt3.交易方向_质押券出库.getValue(), 交易方向)
        ) {
            return true;
        }
        return false;
    }

    /**
     * 获取现金流水需要的账号
     *
     * @param tradePlace 交易场所
     * @param chlId 通道代码
     * @param portfolioId 组合代码
     * @param ccy 币种
     * @param secManageAcctId 证券托管户
     * @param counterId 交易对手
     * @param etrCashPositionFlowDto 流水dto
     * @return 账号
     */
    public static String gen账号(String tradePlace, String chlId, String portfolioId, String ccy, String secManageAcctId,String counterId,
                               EtrCashPositionFlowDto etrCashPositionFlowDto) {
        return gen账号(tradePlace,chlId,portfolioId,ccy,secManageAcctId,counterId,false,etrCashPositionFlowDto);
    }

}

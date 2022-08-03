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
public class EtrCpcCashPositionDealService extends EtrCashPositionBaseService {

    public static void deal信用保护凭证EntrustClose(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
        //可转债特殊处理
        EtrEntrustAdd etrEntrustAdd = entrustCommonDto.getEtrEntrustAdd();
        // 交易方向
        String ps = etrEntrust.getPs();
        BigDecimal etrFinish = EtrCommonCashPositionDealService.getEtrFinishAmt(etrEntrust.getEntrustId());

        Map<String, Object> map = new HashMap<>();
        map.put("entrustId", etrEntrust.getEntrustId());
        map.put("portfolioId", etrEntrust.getPortfolioId());

        if (DictCodeList.交易方向_买入.getValue().equals(ps) || DictCodeList.交易方向_申购.getValue().equals(ps)) {
            EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                    entrustCommonDto);
            String acctId = null;
            if (DictCodeList.交易方向_申购.getValue().equals(ps)) {
                acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
            } else {
                String secManageAcctId = null;
                if (StringUtil.isNotEmpty(etrEntrustAdd)) {
                    secManageAcctId = entrustCommonDto.getEtrEntrustAdd().getSecAcctId();
                }
                acctId = EtrCommonCashPositionDealService.select资金账户(secManageAcctId, etrEntrust.getCcy());
            }
            //查询指令现金流水，获取需要冲销的指令金额
            BigDecimal amount = EtrCommonCashPositionDealService.getEtrSurplusAmt(etrEntrust.getEntrustId(), etrEntrust.getPortfolioId());
            // 场内指令关闭：指令金额-已完成的金额，
            if (StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
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
        } else if (DictCodeList.交易方向_卖出.getValue().equals(ps)) {
            EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(entrustCommonDto);
            // 可用持仓 关闭日增加剩余委托券面总额/100
            if (StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                // 场内交易的日间交易已完成额度不能取额度表里的数据，因为如果是按金额下指令的，额度表中存的是金额，所以从交易表和交易零时表中查询实际发生的额度汇总
                // 冲销是否sum生效交易数据
                map.put("sumTradeFlag", "01");
                map.put("entrustClass", etrEntrust.getEntrustClass());
                etrFinish = EtrCommonCashPositionDealService.getEtrExchangeFinishAmtMuilt(map);

                etrSecPositionFlowDto.setAmount(ArithmeticUtil.subtract(etrEntrust.getEntrustNum(), etrFinish));
            } else {
                etrSecPositionFlowDto.setAmount(ArithmeticUtil.subtract(etrEntrust.getEntrustNum(), etrFinish));
            }
            etrSecPositionFlowDto.setCloseFlag("01"); // 委托关闭特殊处理, 委托关闭后,再撤销交易时, 委托状态会变成已接收,这时应该要把这笔数据删除
            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
        }
    }

}

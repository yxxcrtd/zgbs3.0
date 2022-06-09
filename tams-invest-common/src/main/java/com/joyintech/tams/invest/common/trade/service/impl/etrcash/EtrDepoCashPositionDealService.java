package com.joyintech.tams.invest.common.trade.service.impl.etrcash;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService.getSeqNo;

/**
 * @program: 2.1.0
 * @description:
 * @author: 强成西
 * @create: 2019-10-12 15:07
 **/
@Service
public class EtrDepoCashPositionDealService extends EtrCashPositionBaseService {

    public static void deal存款CashPosition(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                          EtrPositionHoldDto etrPositionHoldDto) {
        TrdProductDeal deal = finProductTradeDto.getTrdProductDeal();
        TrdProductDealAdd dealAdd = finProductTradeDto.getTrdProductDealAdd();
        List<StlPayOrdersDto> stlPayOrdersDtos = finProductTradeDto.getStlPayOrdersList();
        // 通道代码
        String chlId = deal.getChlId();
        // 交易方向
        String ps = deal.getPs();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        etrCashPositionFlowDto.setOrderId(deal.getTradeId());
        if (StringUtil.isEmpty(chlId)) { // 直投
            String acctId = EtrCommonCashPositionDealService.select组合托管户(
                    dealAdd.getPortfolioId(), deal.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
            if (DictCodeList.交易方向_买入.getValue().equals(ps)) {
            	//START modify by wj  20200429 修复TISHKBS-288 在指令接收进行存款类交易录入，提交后在下一审批人审批时报错 acctId为null
            	etrCashPositionFlowDto.setAcctId(acctId);
            	//END modify by wj  20200429 修复TISHKBS-288 在指令接收进行存款类交易录入，提交后在下一审批人审批时报错
                deal交易生效(持仓类型, etrPositionHoldDto, deal, dealAdd, entrustId, etrSecPositionFlowDto, etrCashPositionFlowDto);
            }
            else if (DictCodeList.交易方向_卖出.getValue().equals(ps)) {
                if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                    /*if (StringUtil.isEmpty(entrustId)) { // 无委托
                        // 可用现金 托管户 交易日增加交易金额
                        etrCashPositionFlowDto.setAmount(deal.getTradeAmt());
                        etrCashPositionFlowDto.setAcctId(acctId);
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                    }
                    else { // 有委托
                           // 可用现金 托管户 交易日增加交易金额
                        etrCashPositionFlowDto.setAmount(deal.getTradeAmt());
                        etrCashPositionFlowDto.setAcctId(acctId);
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                    }*/
                    etrCashPositionFlowDto.setAmount(deal.getTradeAmt());
                    etrCashPositionFlowDto.setAcctId(acctId);
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                }
                if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                    if (StringUtil.isEmpty(entrustId)) {
                        // 可用持仓 交易日减少本金金额
                        //                利息没有printAmt金额
                        if(deal.getPrinAmt() != null){
                            etrSecPositionFlowDto.setAmount(deal.getPrinAmt().negate());
                            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                        }
                    }
                }
            }
            else {
                deal存续期(持仓类型, etrPositionHoldDto, deal, dealAdd, stlPayOrdersDtos, ps, etrSecPositionFlowDto,
                    etrCashPositionFlowDto, acctId);
            }
        }
        else { // 借通道
               // 通道性质
            String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
            if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_逐笔划款.getValue())) {
                etrCashPositionFlowDto.setChlFinprodId(null);
                String acctId = EtrCommonCashPositionDealService.select组合托管户(
                        dealAdd.getPortfolioId(), deal.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
                etrCashPositionFlowDto.setAcctId(acctId);
                if (DictCodeList.交易方向_买入.getValue().equals(ps)) {
                    deal交易生效(持仓类型, etrPositionHoldDto, deal, dealAdd, entrustId, etrSecPositionFlowDto, etrCashPositionFlowDto);
                }
                else if (DictCodeList.交易方向_卖出.getValue().equals(ps)) {
                    if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                       /* if (StringUtil.isEmpty(entrustId)) { // 无委托
                            // 可用现金 托管户 交易日增加交易金额
                            etrCashPositionFlowDto.setAmount(deal.getTradeAmt());
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        }
                        else { // 有委托
                               // 可用现金 托管户 交易日增加交易金额
                            etrCashPositionFlowDto.setAmount(deal.getTradeAmt());
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        }*/

                        etrCashPositionFlowDto.setAmount(deal.getTradeAmt());
                        etrCashPositionFlowDto.setAcctId(acctId);
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                    }
                    if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                        if (StringUtil.isEmpty(entrustId)) {
                            // 可用持仓 通道 交易日减少本金金额
                            //                利息没有printAmt金额
                            if(deal.getPrinAmt() != null){
                                etrSecPositionFlowDto.setAmount(deal.getPrinAmt().negate());
                                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                            }
                        }
                    }
                }
                else {
                    deal存续期(持仓类型, etrPositionHoldDto, deal, dealAdd, stlPayOrdersDtos, ps, etrSecPositionFlowDto,
                        etrCashPositionFlowDto, acctId);
                }
            }
            // 一次性划款通道
            else {
                String acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId);
                if (DictCodeList.交易方向_买入.getValue().equals(ps)) {
                    if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                        if (StringUtil.isEmpty(entrustId)) { // 无委托
                            // 可用持仓 交易日增加本金金额
                            etrSecPositionFlowDto.setAmount(deal.getPrinAmt());
                            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                        }
                        else { // 有委托
                               // 可用持仓 交易日增加本金金额
                            etrSecPositionFlowDto.setAmount(deal.getPrinAmt());
                            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);

                            if (deal.getIntAmt() != null && deal.getIntAmt().compareTo(BigDecimal.ZERO) > 0) {
                                // 有委托增加一笔利息现金的减少
                                EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                                BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                                etrCashPositionFlowDto1.setSeqno(getSeqNo());
                                etrCashPositionFlowDto1.setAmount(ArithmeticUtil.subtract(BigDecimal.ZERO, deal.getIntAmt()));
                                etrCashPositionFlowDto1.setOffsetFlag(null);
                                etrCashPositionFlowDto1.setOffsetId(null);
                                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                            }
                        }
                    }
                    if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                        if (StringUtil.isEmpty(entrustId)) {
                            // 通道 交易日减少交易本金金额
                            etrCashPositionFlowDto.setAmount(deal.getTradeAmt().negate());
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        }
                    }
                }
                else if (DictCodeList.交易方向_卖出.getValue().equals(ps)) {
                    if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                        /*if (StringUtil.isEmpty(entrustId)) { // 无委托
                            // 可用现金 通道 交易日增加交易金额
                            etrCashPositionFlowDto.setAmount(deal.getTradeAmt());
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        }
                        else { // 有委托
                               // 可用现金 通道 交易日增加交易金额
                            etrCashPositionFlowDto.setAmount(deal.getTradeAmt());
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        }*/
                        etrCashPositionFlowDto.setAmount(deal.getTradeAmt());
                        etrCashPositionFlowDto.setAcctId(acctId);
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                    }
                    if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                        if (StringUtil.isEmpty(entrustId)) {
                            // 可用持仓 通道 交易日减少本金金额
                            //                利息没有printAmt金额
                            if(deal.getPrinAmt() != null){
                                etrSecPositionFlowDto.setAmount(deal.getPrinAmt().negate());
                                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                            }
                        }
                    }
                }
                else if (DictCodeList.交易方向_收息.getValue().equals(ps)) {
                    if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                        if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                            deal(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
                        }
                    }
                    if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                        if (StringUtil.isEmpty(entrustId)) { // 无委托
                            // 预增现金 通道 资金到账日增加到账金额
                            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                                预增现金处理(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
                            }
                        }
                    }
                }
                else if (DictCodeList.交易方向_提前支取.getValue().equals(ps)) {
                    if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                        // 可用现金 通道 资金到账日增加到账金额
                        // 预增现金 通道 资金到账日减少到账金额
                        if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                            deal(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
                        }
                    }
                    if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                        if (StringUtil.isEmpty(entrustId)) { // 无委托
                            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                                // 预增现金 通道 资金到账日增加到账金额
                                预增现金处理(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
                            }
                            // 可用持仓 通道 提交日减少提前支取金额
                            if (deal.getPrinAmt() != null && BigDecimal.ZERO.compareTo(deal.getPrinAmt()) < 0) {
                                etrSecPositionFlowDto.setAmount(deal.getPrinAmt().negate());
                                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                            }
                        }
                    }
                }
                else if (DictCodeList.交易方向_到期.getValue().equals(ps)) {
                    if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                        // 可用现金 通道 资金到账日增加到账金额
                        // 预增现金 通道 资金到账日减少到账金额
                        if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                            deal(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
                        }
                    }
                    if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                        if (StringUtil.isEmpty(entrustId)) { // 无委托
                            // 可用持仓 通道 提交日削减至0
                            // 可用持仓 提交日削减至0
                            if (deal.getPrinAmt() != null && BigDecimal.ZERO.compareTo(deal.getPrinAmt()) < 0) {
                                etrSecPositionFlowDto.setAmount(deal.getPrinAmt().negate());
                                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                            }
                            // 预增现金 通道 资金到账日增加到账金额
                            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                                预增现金处理(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
                            }
                        }
                    }
                }
            }
        }

    }

    private static void deal存续期(DictCodeList 持仓类型, EtrPositionHoldDto etrPositionHoldDto, TrdProductDeal deal,
                                TrdProductDealAdd dealAdd, List<StlPayOrdersDto> stlPayOrdersDtos, String ps,
                                EtrSecPositionFlowDto etrSecPositionFlowDto, EtrCashPositionFlowDto etrCashPositionFlowDto, String acctId) {
        if (stlPayOrdersDtos == null || stlPayOrdersDtos.isEmpty()) {
            return;
        }

        if (DictCodeList.交易方向_收息.getValue().equals(ps)) {
            if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                deal(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
            }
            if (DictCodeList.持仓类型_可用.equals(持仓类型)  || etrPositionHoldDto.isNeedNegative()) { // 提交
                stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                    etrCashPositionFlowDto.setOrderId(stlPayOrdersDto.getOrderId());
                    // 预增现金 资金到账日增加到账金额
                    EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto);
                    etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt());
                    etrCashPositionFlowDto2.setAcctId(stlPayOrdersDto.getMyAcctNo());
                    etrCashPositionFlowDto2.setCdate(stlPayOrdersDto.getSettleDate());
                    etrCashPositionFlowDto2.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                    etrCashPositionFlowDto2.setSeqno(getSeqNo());
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
                });
            }
        }
        else if (DictCodeList.交易方向_提前支取.getValue().equals(ps)) {
            if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                deal(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
            }
            if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                etrCashPositionFlowDto1.setSeqno(getSeqNo());
                预增现金处理(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto1, acctId);
                // 可用持仓 提交日减少提前支取金额
                etrSecPositionFlowDto.setAmount(deal.getPrinAmt().negate());
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
        else if (DictCodeList.交易方向_到期.getValue().equals(ps)) {
            if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                deal(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
            }
            if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                // 可用持仓 提交日削减至0
//                利息没有printAmt金额
                if(deal.getPrinAmt() != null){
                    etrSecPositionFlowDto.setAmount(deal.getPrinAmt().negate());
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                }
                // 预增现金 资金到账日增加到账金额
                EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                etrCashPositionFlowDto1.setSeqno(getSeqNo());
                预增现金处理(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto1, acctId);
            }
        }
        //活期存放结息   start
        else if (DictCodeList.交易方向_收息到账.getValue().equals(ps)) {
            if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                deal(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId);
            }
            if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
                if(deal.getIntAmt() != null){
                    etrSecPositionFlowDto.setAmount(deal.getIntAmt());
                }
                EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                etrCashPositionFlowDto1.setSeqno(getSeqNo());
                预增现金处理(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto1, acctId);
            }
        }
        //活期存放结息
    }

    private static void deal交易生效(DictCodeList 持仓类型, EtrPositionHoldDto etrPositionHoldDto, TrdProductDeal deal,
                                 TrdProductDealAdd dealAdd, String entrustId, EtrSecPositionFlowDto etrSecPositionFlowDto,
                                 EtrCashPositionFlowDto etrCashPositionFlowDto) {
        if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                // 可用持仓 交易日增加本金金额
                etrSecPositionFlowDto.setAmount(deal.getPrinAmt());
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
            else { // 有委托
                   // 可用持仓 交易日增加本金金额
                etrSecPositionFlowDto.setAmount(deal.getPrinAmt());
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);

                if (deal.getIntAmt() != null && deal.getIntAmt().compareTo(BigDecimal.ZERO) > 0) {
                    // 有委托增加一笔利息现金的减少
                    EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                    etrCashPositionFlowDto1.setSeqno(getSeqNo());
                    etrCashPositionFlowDto1.setAmount(ArithmeticUtil.subtract(BigDecimal.ZERO, deal.getIntAmt()));
                    etrCashPositionFlowDto1.setOffsetFlag(null);
                    etrCashPositionFlowDto1.setOffsetId(null);
                    etrCashPositionFlowDto1.setAcctId(EtrCommonCashPositionDealService.select组合托管户(dealAdd.getPortfolioId(), deal.getCcy(),
                            DictCodeListExt2.资金账户类型_托管户.getValue()));
                    
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                }
            }
        }
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 提交
            if (StringUtil.isEmpty(entrustId)) {
                // 托管户 可用现金 交易日减少交易本金金额
                etrCashPositionFlowDto.setAmount(deal.getTradeAmt().negate());
                etrCashPositionFlowDto.setAcctId(EtrCommonCashPositionDealService.select组合托管户(dealAdd.getPortfolioId(), deal.getCcy(),
                        DictCodeListExt2.资金账户类型_托管户.getValue()));
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }
    }

    private static void deal(EtrPositionHoldDto etrPositionHoldDto, List<StlPayOrdersDto> stlPayOrdersDtos,
                             EtrCashPositionFlowDto etrCashPositionFlowDto, String acctId) {
        stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
            etrCashPositionFlowDto.setOrderId(stlPayOrdersDto.getOrderId());
            // 可用现金 资金到账日增加到账金额
            EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
            etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt());
            etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
            etrCashPositionFlowDto1.setCdate(stlPayOrdersDto.getSettleDate());
            etrCashPositionFlowDto1.setSeqno(getSeqNo());
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);

            // 预增现金 资金到账日减少到账金额
            EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto1);
            etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt().negate());
            etrCashPositionFlowDto2.setAcctId(stlPayOrdersDto.getMyAcctNo());
            etrCashPositionFlowDto2.setCdate(stlPayOrdersDto.getSettleDate());
            etrCashPositionFlowDto2.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
            etrCashPositionFlowDto2.setSeqno(getSeqNo());
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
        });
    }

    private static void 预增现金处理(EtrPositionHoldDto etrPositionHoldDto, List<StlPayOrdersDto> stlPayOrdersDtos,
                               EtrCashPositionFlowDto etrCashPositionFlowDto, String acctId) {
        stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
            // 预增现金 资金到账日增加到账金额
            etrCashPositionFlowDto.setOrderId(stlPayOrdersDto.getOrderId());
            EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto);
            etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt());
            etrCashPositionFlowDto2.setAcctId(stlPayOrdersDto.getMyAcctNo());
            etrCashPositionFlowDto2.setCdate(stlPayOrdersDto.getSettleDate());
            etrCashPositionFlowDto2.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
            etrCashPositionFlowDto2.setSeqno(getSeqNo());
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
        });
    }

    public static void deal存款EntrustStart(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        if (entrustCommonDto != null) {
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            // 交易方向
            String ps = etrEntrust.getPs();
            if (DictCodeList.交易方向_买入.getValue().equals(ps)) {
                // 委托生成的现金流水
                EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                    entrustCommonDto);
                String acctId;
                if (StringUtil.isNotEmpty(etrEntrust.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(
                    etrCommonCashPositionDealService.queryChlType(etrEntrust.getChlId()))) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(etrEntrust.getChlId());
                }
                else {
                    acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                        DictCodeListExt2.资金账户类型_托管户.getValue());
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
                // 减少委托本金金额
                etrCashPositionFlowDto.setAmount(etrEntrust.getEntrustAmt().negate());
                etrCashPositionFlowDto.setAcctId(acctId);
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
            else if (DictCodeList.交易方向_卖出.getValue().equals(ps)) {
                // 委托生成的持仓流水
                // 减少本金金额
                EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
                    entrustCommonDto);
                etrSecPositionFlowDto.setAmount(etrEntrust.getEntrustAmt().negate());
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
    }

    public static void deal存款EntrustClose(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
        // 交易方向
        String ps = etrEntrust.getPs();
        BigDecimal etrFinish = EtrCommonCashPositionDealService.getEtrFinishAmt(etrEntrust.getEntrustId());
        if (DictCodeList.交易方向_买入.getValue().equals(ps)) {
            // 可用现金 关闭日增加剩余委托本金金额
            EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                    entrustCommonDto);
            String acctId;
            if (StringUtil.isNotEmpty(etrEntrust.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(
                    etrCommonCashPositionDealService.queryChlType(etrEntrust.getChlId()))) {
                acctId = EtrCommonCashPositionDealService.select通道资金账户(etrEntrust.getChlId());
            }
            else {
                acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                        DictCodeListExt2.资金账户类型_托管户.getValue());
                etrCashPositionFlowDto.setChlFinprodId(null);
            }

            // 关闭日增加剩余委托本金金额
            etrCashPositionFlowDto.setAmount(ArithmeticUtil.subtract(etrEntrust.getEntrustAmt(), etrFinish));
            etrCashPositionFlowDto.setAcctId(acctId);
            etrCashPositionFlowDto.setOffsetId(etrEntrust.getEntrustId());
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
        }
        else if (DictCodeList.交易方向_卖出.getValue().equals(ps)) {
            EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
                    entrustCommonDto);
            // 可用持仓 关闭日增加剩余委托本金金额
            etrSecPositionFlowDto.setAmount(ArithmeticUtil.subtract(etrEntrust.getEntrustAmt(), etrFinish));
            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
        }
    }
}

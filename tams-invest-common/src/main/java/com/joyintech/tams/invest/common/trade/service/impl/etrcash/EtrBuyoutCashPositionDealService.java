package com.joyintech.tams.invest.common.trade.service.impl.etrcash;

import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.PtlSecPositionFlow;
import com.joyintech.tams.common.base.entity.PtlSecValutionKey;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService.getSeqNo;

/**
 * @program: 2.1.0
 * @description:买断式回购头寸
 * @author: 强成西
 * @create: 2019-10-12 15:06
 **/
@Service
@Lazy(false)
public class EtrBuyoutCashPositionDealService extends EtrCashPositionBaseService {

    static BigDecimal 债券面值 = new BigDecimal(100);
    /**
     * 买断式回购头寸处理
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    public static void deal买断式回购CashPosition(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                      EtrPositionHoldDto etrPositionHoldDto) {
        // 资产类型
        String finprodType = finProductTradeDto.getTrdProductDeal().getFinprodType2();
        String ps = finProductTradeDto.getTrdProductDeal().getPs() ;
        List<StlPayOrdersDto> stlPayOrdersDtos = finProductTradeDto.getStlPayOrdersList();
        if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())){
            if(StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())){
                deal_买断式_正回购到期(finProductTradeDto,持仓类型,etrPositionHoldDto,stlPayOrdersDtos) ;
            }else{
                deal_买断式_正回购(finProductTradeDto,持仓类型,etrPositionHoldDto) ;
            }
        }else{
            if(StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())){
                deal_买断式_逆回购到期(finProductTradeDto,持仓类型,etrPositionHoldDto,stlPayOrdersDtos) ;
            }else{
                deal_买断式_逆回购(finProductTradeDto,持仓类型,etrPositionHoldDto) ;
            }
        }
    }


    /**
     * 买断式回购_正回购
     *
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_买断式_正回购(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                      EtrPositionHoldDto etrPositionHoldDto) {
        // 委托代码(未使用)
        //String entrustId = finProductTradeDto.getEntrustId();
        // 资产代码
//        String assetCode = finProductTradeDto.getTrdProductDeal().getFinprodId();
        // 交易日期
//        Date tradeDate = finProductTradeDto.getTrdProductDeal().getTradeDate();
//        BigDecimal 债券面值 = EtrCommonCashPositionDealService.gen债券面值(assetCode, tradeDate);
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // 交易生成的持仓流水（质押券使用）
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);

        // 交易生成的持仓流水（交易使用）
        EtrSecPositionFlowDto etrSecPositionFlowTrdDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
                finProductTradeDto);

        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId() ;
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy() ;
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易生效
        if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
            // 有委托  or 无委托   统一处理
            // 现金流水dto处理    交割日减少(交易金额-交易券面总额*委托全价/债券面值）
            etrCashPositionFlowDto.setAmount(交易日金额);
            // 设置账号
            String acctId;
            // 通道交易时
            if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId) ;
            }
            else {
                acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy,
                        DictCodeListExt1.资金账户类型_产品托管户.getValue()) ;
                etrCashPositionFlowDto.setChlFinprodId(null);
            }
            etrCashPositionFlowDto.setAcctId(acctId);
            etrCashPositionFlowDto.setOrderId(finProductTradeDto.getTrdProductDeal().getTradeId());
            // 添加现金流水到目标dto
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);

            if (StringUtil.isEmpty(entrustId)) { // 无委托
                // 持仓流水dto处理   交割日增加交易券面总额/债券面值
                //如果是交易到期，没有份额，只有到账金额，所以数量=到期券结算金额/100
                etrSecPositionFlowTrdDto.setAmount(finProductTradeDto.getSecAmount());
//                etrSecPositionFlowDto.setFinprodId(finProductTradeDto.getSecLendFinprodId());

                // 添加持仓流水到目标dto
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowTrdDto);
            }
        }
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                // 持仓流水dto处理   交割日增加交易券面总额/债券面值
                //如果是交易到期，没有份额，只有到账金额，所以数量=到期券结算金额/100
                etrSecPositionFlowDto.setAmount(finProductTradeDto.getSecAmount().negate());
                etrSecPositionFlowDto.setFinprodId(finProductTradeDto.getSecLendFinprodId());

                // 添加持仓流水到目标dto
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
    }
    /**
     * 买断式_正回购到期
     *
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_买断式_正回购到期(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                EtrPositionHoldDto etrPositionHoldDto,List<StlPayOrdersDto> stlPayOrdersDtos) {
        // 委托代码(未使用)
        //String entrustId = finProductTradeDto.getEntrustId();
        // 资产代码
//        String assetCode = finProductTradeDto.getTrdProductDeal().getFinprodId();
        // 交易日期
//        Date tradeDate = finProductTradeDto.getTrdProductDeal().getTradeDate();
//        BigDecimal 债券面值 = EtrCommonCashPositionDealService.gen债券面值(assetCode, tradeDate);
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
//        BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // 委托全价获取方式
        //  BigDecimal 委托全价 = finProductTradeDto.getTrdProductDeal().getFpriceAmt();
        // 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
        //  BigDecimal amount = ArithmeticUtil.subtract(交易日金额,
        //      ArithmeticUtil.divide(ArithmeticUtil.multiply(交易券面总额, 委托全价), 债券面值, 2));
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId() ;
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy() ;
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                    stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                        // 现金流水dto处理
                        EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                        etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                        // 设置账号
                        String acctId;
                        // 通道交易划款方式_一次性划款时，使用通道资金账户
                        if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                            acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId);
                        } else {
                            acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy,
                                DictCodeListExt1.资金账户类型_产品托管户.getValue());
                            etrCashPositionFlowDto1.setChlFinprodId(null);
                        }
                        etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
                        etrCashPositionFlowDto1.setSeqno(getSeqNo());
                        // 添加现金流水到目标dto
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                    });
                }
            }
        }
        // 交易生效
        if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
            // 持仓流水dto处理   交割日增加交易券面总额/债券面值
            setEtrSecPosition(etrPositionHoldDto.getPtlSecPositionFlows(), etrSecPositionFlowDto, finProductTradeDto.getTrdProductDeal());
        }
    }

    private static void setEtrSecPosition(List<EtrSecPositionFlowDto> ptlSecPositionFlows, EtrSecPositionFlowDto etrSecPositionFlowDto, TrdProductDeal trdProductDeal) {
        Map<String, Object> params = new HashMap<>();
        if (DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(trdProductDeal.getFinprodType2())) {
            params.put("holdingType", DictCodeList.持仓类型_融出.getValue());
        }
        else {
            params.put("holdingType", DictCodeList.持仓类型_融入.getValue());
        }

        params.put("finprodId", trdProductDeal.getFinprodId());
        params.put("mDate", trdProductDeal.getMdate());
        List<PtlSecPositionFlow> flows = etrCommonCashPositionDealService.selectSecFlow(params);
        if (flows != null && !flows.isEmpty()) {
            flows.forEach(flow -> {
                EtrSecPositionFlowDto flowDto = new EtrSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(flowDto, flow);
                flowDto.setCdate(trdProductDeal.getMdate());
                flowDto.setSeqno(IDGenerator.generate());
                flowDto.setTradeId(trdProductDeal.getTradeId());
                if (DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(trdProductDeal.getFinprodType2())) {
                    flowDto.setAmount(flow.getAmount().negate());
                }
                flowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());

                ptlSecPositionFlows.add(flowDto);
            });
        }
        // JJTZJY-134 1.做完逆回购到期，指令可用持仓未清零处理， 2.逆回购交易入指令现金流水的时候，将一笔流水分成两笔来入，并且汇总金额不一致 start
        etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(trdProductDeal.getPrinAmt(),BigDecimal.valueOf(100),4, RoundingMode.HALF_UP));
        if (DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(trdProductDeal.getFinprodType2())) {
            etrSecPositionFlowDto.setAmount(etrSecPositionFlowDto.getAmount().negate());
        }
        ptlSecPositionFlows.add(etrSecPositionFlowDto);
        // JJTZJY-134 1.做完逆回购到期，指令可用持仓未清零处理， 2.逆回购交易入指令现金流水的时候，将一笔流水分成两笔来入，并且汇总金额不一致 end
    }

    /**
     * 买断式_逆回购
     *
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_买断式_逆回购(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                      EtrPositionHoldDto etrPositionHoldDto) {
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 资产代码
//        String assetCode = finProductTradeDto.getTrdProductDeal().getFinprodId();
//        // 交易日期
//        Date tradeDate = finProductTradeDto.getTrdProductDeal().getTradeDate();
//        BigDecimal 债券面值 = EtrCommonCashPositionDealService.gen债券面值(assetCode, tradeDate);
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // 委托全价获取方式
        BigDecimal 首次交易全价 = finProductTradeDto.getTrdProductDeal().getUnitFprice();
//        // 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
//        BigDecimal amount = ArithmeticUtil.subtract(交易日金额,
//            ArithmeticUtil.divide(ArithmeticUtil.multiply(交易券面总额, 委托全价), 债券面值, 2));

        String secAcctId = finProductTradeDto.getTrdProductDeal().getSecManageAcctId();
        if (StringUtil.isEmpty(secAcctId) && StringUtil.isEmpty(finProductTradeDto.getTrdProductDeal().getChlId())) {
            secAcctId = "999999";
        }
        else if (StringUtil.isNotEmpty(finProductTradeDto.getTrdProductDeal().getChlId())) {
            secAcctId = finProductTradeDto.getTrdProductDeal().getChlId();
        }
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);
        // 交易生成的质押券持仓流水
        EtrSecPositionFlowDto bondEtrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
                finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId() ;
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy() ;
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易生效
        if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
            // 持仓流水dto处理   交割日增加交易券面总额/债券面值
            etrSecPositionFlowDto.setAmount(finProductTradeDto.getSecAmount());
            bondEtrSecPositionFlowDto.setAmount(finProductTradeDto.getSecAmount());
            bondEtrSecPositionFlowDto.setFinprodId(finProductTradeDto.getSecLendFinprodId()); // 买断式回购，持仓是债券标的，从前面塞过来的，不要删
             // 添加持仓流水到目标dto
            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            etrPositionHoldDto.getPtlSecPositionFlows().add(bondEtrSecPositionFlowDto);
            // 有委托 处理现金流水
            if(StringUtil.isNotEmpty(entrustId)){
                PtlSecValutionKey ptlSecValutionKey = new PtlSecValutionKey();
                ptlSecValutionKey.setCdate(DateUtil.addDay(finProductTradeDto.getTrdProductDeal().getSettleDate(), -1));// 委托结束日
                ptlSecValutionKey.setPortfolioId(finProductTradeDto.getTrdProductDealAdd().getPortfolioId());
                ptlSecValutionKey.setFinprodId(finProductTradeDto.getTrdProductDeal().getFinprodId());
                ptlSecValutionKey.setInvAim(finProductTradeDto.getTrdProductDeal().getInvAim());
                ptlSecValutionKey.setSecAcctId(secAcctId);
                BigDecimal 应计利息 = EtrCommonCashPositionDealService.get应计利息FromValution(ptlSecValutionKey) ;
                BigDecimal 首次回购最大净价 = EtrCommonCashPositionDealService.get首次回购最大净价(entrustId) ;
                BigDecimal 委托首次全价 = ArithmeticUtil.doAdd(首次回购最大净价,应计利息) ;
                //减少交易券面总额*（交易首次全价-委托首次全价）/100   其中：委托首次全价用委托首次净价最大值和委托结束日计算
                BigDecimal amount = ArithmeticUtil.divide(ArithmeticUtil.multiply(交易券面总额,
                    ArithmeticUtil.subtract(首次交易全价,委托首次全价)), 债券面值, 2);
                // 现金流水dto处理    减少交易券面总额*（交易首次全价-委托首次全价）/100
                etrCashPositionFlowDto.setAmount(amount.negate());
                // 设置账号
                String acctId;
                // 通道交易划款方式_一次性划款时，使用通道资金账户
                if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId) ;
                }
                else {
                    acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy,
                            DictCodeListExt1.资金账户类型_产品托管户.getValue()) ;
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
                etrCashPositionFlowDto.setAcctId(acctId);
                etrCashPositionFlowDto.setOrderId(finProductTradeDto.getTrdProductDeal().getTradeId());
                // 添加现金流水到目标dto
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                // 现金流水dto处理    减少交易券面总额*（交易首次全价-委托首次全价）/100
                etrCashPositionFlowDto.setAmount(交易日金额.negate());
                // 设置账号
                String acctId;
                // 通道交易划款方式_一次性划款时，使用通道资金账户
                if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId) ;
                }
                else {
                    acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy,
                        DictCodeListExt1.资金账户类型_产品托管户.getValue()) ;
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
                etrCashPositionFlowDto.setAcctId(acctId);
                etrCashPositionFlowDto.setOrderId(finProductTradeDto.getTrdProductDeal().getTradeId());
                // 添加现金流水到目标dto
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }
    }

    /**
     * 买断式_逆回购到期
     *
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_买断式_逆回购到期(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                      EtrPositionHoldDto etrPositionHoldDto,List<StlPayOrdersDto> stlPayOrdersDtos) {
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 资产代码
//        String assetCode = finProductTradeDto.getTrdProductDeal().getFinprodId();
//        // 交易日期
//        Date tradeDate = finProductTradeDto.getTrdProductDeal().getTradeDate();
//        BigDecimal 债券面值 = EtrCommonCashPositionDealService.gen债券面值(assetCode, tradeDate);
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
//        // 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
//        BigDecimal amount = ArithmeticUtil.subtract(交易日金额,
//            ArithmeticUtil.divide(ArithmeticUtil.multiply(交易券面总额, 委托全价), 债券面值, 2));
        PtlSecValutionKey ptlSecValutionKey = new PtlSecValutionKey();
        ptlSecValutionKey.setCdate(DateUtil.addDay(finProductTradeDto.getTrdProductDeal().getSettleDate(), -1));// 委托结束日
        ptlSecValutionKey.setPortfolioId(finProductTradeDto.getTrdProductDealAdd().getPortfolioId());
        ptlSecValutionKey.setFinprodId(finProductTradeDto.getTrdProductDeal().getFinprodId());
        ptlSecValutionKey.setInvAim(finProductTradeDto.getTrdProductDeal().getInvAim());
        String secAcctId = finProductTradeDto.getTrdProductDeal().getSecManageAcctId();
        if (StringUtil.isEmpty(secAcctId) && StringUtil.isEmpty(finProductTradeDto.getTrdProductDeal().getChlId())) {
            secAcctId = "999999";
        }
        else if (StringUtil.isNotEmpty(finProductTradeDto.getTrdProductDeal().getChlId())) {
            secAcctId = finProductTradeDto.getTrdProductDeal().getChlId();
        }
        ptlSecValutionKey.setSecAcctId(secAcctId);
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId() ;
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy() ;
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                // 持仓流水dto处理   交割日增加交易券面总额/债券面值
                setEtrSecPosition(etrPositionHoldDto.getPtlSecPositionFlows(), etrSecPositionFlowDto, finProductTradeDto.getTrdProductDeal());

                // 修改账号的取值逻辑，否则前台提交会报错 20200712 start
                // 设置账号
                String acctId;
                // 通道交易划款方式_一次性划款时，使用通道资金账户
                if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId) ;
                }
                else {
                    acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy,
                            DictCodeListExt1.资金账户类型_产品托管户.getValue()) ;
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
                // 修改账号的取值逻辑，否则前台提交会报错 20200712 end

                if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                    // 预增现金
                    stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                        EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                        etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt());
                        // 设置账号
                        etrCashPositionFlowDto1.setAcctId(acctId);
//                        etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
                        etrCashPositionFlowDto1.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                        etrCashPositionFlowDto1.setSeqno(getSeqNo());
                        // 添加现金流水到目标dto
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                    });
                }
            }
        }
        // 交易生效
        if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                    EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                    etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt());

                    // 修改账号的取值逻辑，否则前台提交会报错 20200712 start
                    // 设置账号
                    String acctId;
                    // 通道交易划款方式_一次性划款时，使用通道资金账户
                    if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                        acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId) ;
                    }
                    else {
                        acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy,
                                DictCodeListExt1.资金账户类型_产品托管户.getValue()) ;
                        etrCashPositionFlowDto.setChlFinprodId(null);
                    }
                    // 修改账号的取值逻辑，否则前台提交会报错 20200712 end


                    // 设置账号
                    // 通道交易划款方式_一次性划款时，使用通道资金账户
                    if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                    } else {
                        etrCashPositionFlowDto1.setChlFinprodId(null);
                    }
                    etrCashPositionFlowDto1.setAcctId(acctId);
//                    etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
                    etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
                    etrCashPositionFlowDto1.setSeqno(getSeqNo());
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);

                    // 预增现金处理
                    EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto1);
                    etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                    etrCashPositionFlowDto2.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                    etrCashPositionFlowDto2.setSeqno(getSeqNo());
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
                });
            }
        }
    }

    /**
     * 买断式_委托开始日
     *
     * @param entrustCommonDto
     * @param etrPositionHoldDto
     */
    public static void deal买断式回购EntrustStart(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        if (entrustCommonDto != null) {
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            // 正回购  or 逆回购
            String ps = etrEntrust.getFinprodType2();
            if (DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(ps)) {
                // 委托生成的持仓流水
                // 减少本金金额
                EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
                    entrustCommonDto);
                etrSecPositionFlowDto.setAmount(etrEntrust.getEntrustNum().negate());
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
            else if (DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(ps)) {
                PtlSecValutionKey ptlSecValutionKey = new PtlSecValutionKey();
                ptlSecValutionKey.setCdate(DateUtil.addDay(etrEntrust.getMdate(), -1));// 委托结束日
                ptlSecValutionKey.setPortfolioId(etrEntrust.getPortfolioId());
                ptlSecValutionKey.setFinprodId(etrEntrust.getFinprodId());
                ptlSecValutionKey.setInvAim(etrEntrust.getInvAim());
                String secAcctId = entrustCommonDto.getEtrEntrustAdd().getSecAcctId();
                if (StringUtil.isEmpty(secAcctId) && StringUtil.isEmpty(entrustCommonDto.getEtrEntrust().getChlId())) {
                    secAcctId = "999999";
                }
                else if (StringUtil.isNotEmpty(entrustCommonDto.getEtrEntrust().getChlId())) {
                    secAcctId = entrustCommonDto.getEtrEntrust().getChlId();
                }
                ptlSecValutionKey.setSecAcctId(secAcctId);
                BigDecimal 应计利息 = EtrCommonCashPositionDealService.get应计利息FromValution(ptlSecValutionKey) ;
                BigDecimal 首次回购最大净价 = EtrCommonCashPositionDealService.get首次回购最大净价(etrEntrust.getEntrustId()) ;
                if(首次回购最大净价==null){
                    首次回购最大净价 = entrustCommonDto.getEtrEntrustAdd().getFirstUnitCprice2();
                }
                // "减少委托券面总额*委托首次全价/100 其中：委托首次全价用委托首次净价最大值和委托结束日计算"
                BigDecimal amount = ArithmeticUtil.multiply(etrEntrust.getEntrustNum(),ArithmeticUtil.doAdd(应计利息,首次回购最大净价));
                // 委托生成的现金流水
                EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                    entrustCommonDto);
                String acctId;
                if (StringUtil.isNotEmpty(etrEntrust.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(
                    EtrCommonCashPositionDealService.queryChlType(etrEntrust.getChlId()))) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(etrEntrust.getChlId());
                }else{
                    acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                        DictCodeListExt2.资金账户类型_托管户.getValue());
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
                // 减少委托本金金额
                etrCashPositionFlowDto.setAmount(amount.negate());
                etrCashPositionFlowDto.setAcctId(acctId);
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }
    }

    /**
     * 买断式_委托关闭日
     *
     * @param entrustCommonDto
     * @param etrPositionHoldDto
     */
    public static void deal买断式回购EntrustClose(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        if (entrustCommonDto != null) {
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            // 正回购  or 逆回购
            String ps = etrEntrust.getFinprodType2();
            if (DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(ps)) {
                // 委托生成的持仓流水
                // 减少本金金额
                EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
                    entrustCommonDto);
                etrSecPositionFlowDto.setAmount(etrEntrust.getEntrustNum());
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
            else if (DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(ps)) {
                PtlSecValutionKey ptlSecValutionKey = new PtlSecValutionKey();
                ptlSecValutionKey.setCdate(DateUtil.addDay(etrEntrust.getMdate(), -1));// 委托结束日
                ptlSecValutionKey.setPortfolioId(etrEntrust.getPortfolioId());
                ptlSecValutionKey.setFinprodId(etrEntrust.getFinprodId());
                ptlSecValutionKey.setInvAim(etrEntrust.getInvAim());
                String secAcctId = entrustCommonDto.getEtrEntrustAdd().getSecAcctId();
                if (StringUtil.isEmpty(secAcctId) && StringUtil.isEmpty(entrustCommonDto.getEtrEntrust().getChlId())) {
                    secAcctId = "999999";
                }
                else if (StringUtil.isNotEmpty(entrustCommonDto.getEtrEntrust().getChlId())) {
                    secAcctId = entrustCommonDto.getEtrEntrust().getChlId();
                }
                ptlSecValutionKey.setSecAcctId(secAcctId);
                BigDecimal 应计利息 = EtrCommonCashPositionDealService.get应计利息FromValution(ptlSecValutionKey) ;
                BigDecimal 首次回购最大净价 = EtrCommonCashPositionDealService.get首次回购最大净价(etrEntrust.getEntrustId()) ;
                if(首次回购最大净价==null){
                    首次回购最大净价 = entrustCommonDto.getEtrEntrustAdd().getFirstUnitCprice2();
                }
                // 委托券面总额*委托首次全价/100 其中：委托首次全价用委托首次净价最大值和委托结束日计算
                BigDecimal amount = ArithmeticUtil.multiply(etrEntrust.getEntrustNum(),ArithmeticUtil.doAdd(应计利息,首次回购最大净价));
                // 委托生成的现金流水
                EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                    entrustCommonDto);
                String acctId;
                if (StringUtil.isNotEmpty(etrEntrust.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(
                    EtrCommonCashPositionDealService.queryChlType(etrEntrust.getChlId()))) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(etrEntrust.getChlId());
                }else{
                    acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                        DictCodeListExt2.资金账户类型_托管户.getValue());
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
                etrCashPositionFlowDto.setAmount(amount);
                etrCashPositionFlowDto.setAcctId(acctId);
                etrCashPositionFlowDto.setOffsetId(etrEntrust.getEntrustId());
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }
    }
}

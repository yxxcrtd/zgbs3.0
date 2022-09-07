package com.joyintech.tams.invest.common.trade.service.impl.etrcash;

import com.google.common.collect.Lists;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.dao.EtrCommonCashPositionMapper;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.joyintech.tams.invest.common.constant.BussinessConstant.XN_STANDARD_BOND_PRODID;
import static com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService.getSeqNo;

/**
 * @program: 2.1.0
 * @description:质押式回购头寸
 * @author: 强成西
 * @create: 2019-10-12 15:06
 **/
@Service
@Lazy(false)
public class EtrRepoCashPositionDealService extends EtrCashPositionBaseService {

    static BigDecimal 债券面值 = new BigDecimal(100);
    
    private static EtrCommonCashPositionDealService etrCommonCashPositionDealService;
    
    @Autowired(required = true)
    private EtrCommonCashPositionDealService etrCommonCashPositionDealService1;

    @PostConstruct
    public void init(){
        etrCommonCashPositionDealService = etrCommonCashPositionDealService1;
    }

    /**
     * 标准券_入库处理——影响债券持仓和标准券持仓
     * 
     * @param list
     * @param 出入库方向
     * @param etrPositionHoldDto
     */
    public static void deal标准券_出入库CashPosition(List<EtrSecPositionFlowDto> list, DictCodeList 出入库方向,
                                               EtrPositionHoldDto etrPositionHoldDto) {
        // 标准券改变总额
        BigDecimal countAmt = BigDecimal.ZERO;
        for (EtrSecPositionFlowDto dto : list) {
            countAmt = countAmt.add(dto.getAmount());
            dto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
            // 如果是入库削减债券持仓,取反
            if (DictCodeList.出入库方向_入库.equals(出入库方向)) {
                dto.setAmount(dto.getAmount().negate());
            }
            else {
                dto.setAmount(dto.getAmount());
            }
            dto.setSeqno(IDGenerator.generate());
            etrPositionHoldDto.getPtlSecPositionFlows().add(dto);
        }
        if (countAmt.compareTo(BigDecimal.ZERO) > 0) {
            EtrSecPositionFlowDto etrSecPositionFlowDto = new EtrSecPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto, list.get(0));
            // 如果是入库削减债券持仓,取反
            if (DictCodeList.出入库方向_入库.equals(出入库方向)) {
                etrSecPositionFlowDto.setAmount(countAmt); // 标准券总额
            }
            else {
                etrSecPositionFlowDto.setAmount(countAmt.negate()); // 标准券总额
            }
            // 金融产品代码使用虚拟的资产
            etrSecPositionFlowDto.setFinprodId(XN_STANDARD_BOND_PRODID);
            etrSecPositionFlowDto.setSeqno(IDGenerator.generate());
            etrSecPositionFlowDto.setInvAim("999999");
            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
        }
    }

    /**
     * 质押式回购头寸处理
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    public static void deal质押式回购CashPosition(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                             EtrPositionHoldDto etrPositionHoldDto,String ieFlag) {
        // 交易场所
        String tradePlace = finProductTradeDto.getTrdProductDeal().getTradeMarket();
        // 标准券管理 （直投） 标准券管理（借通道） 未处理
        // 资产类型
        String finprodType = finProductTradeDto.getTrdProductDeal().getFinprodType2();
        String ps = finProductTradeDto.getTrdProductDeal().getPs();
        // 委托代码
        String entrustId = finProductTradeDto.getTrdProductDealAdd().getEntrustId();
        List<StlPayOrdersDto> stlPayOrdersDtos = finProductTradeDto.getStlPayOrdersList();
        // 交易场所为银行间
        if (StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_银行间.getValue())) {
            if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                if (StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())) {
                    deal_银行间_正回购到期(finProductTradeDto, 持仓类型, etrPositionHoldDto, stlPayOrdersDtos);
                }
                else {
                    deal_银行间_正回购(finProductTradeDto, 持仓类型, etrPositionHoldDto);
                }
            }
            else {
                if (StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())) {
                    deal_银行间_逆回购到期(finProductTradeDto, 持仓类型, etrPositionHoldDto, stlPayOrdersDtos);
                }
                else if (StringUtil.isEmpty(entrustId) || StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) {
                    deal_银行间_逆回购(finProductTradeDto, 持仓类型, etrPositionHoldDto);
                }
            }
        }
        // 交易场所为交易所
        else if (StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_上海证券交易所.getValue())
                 || StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
            // H01:质押式协议回购 , H01:质押式竞价回购

            if (StringUtil.equals(finProductTradeDto.getAssetType2(), DictCodeList.回购类型_质押式协议回购.getValue())) {
                if (StringUtil.isNotEmpty(ieFlag)) {// 到期续做场景特殊处理
                    if (StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())) {
                        if (ieFlag.equals(DictCodeListExt1.收支方向_支出.getValue())) {
                            deal_交易所_协议正回购到期(finProductTradeDto, 持仓类型, etrPositionHoldDto, stlPayOrdersDtos);
                        } else {// 收入
                            deal_交易所_逆回购到期(finProductTradeDto, 持仓类型, etrPositionHoldDto, stlPayOrdersDtos);
                        }
                    }
                }
                else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                    if (StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())) {
                        deal_交易所_协议正回购到期(finProductTradeDto, 持仓类型, etrPositionHoldDto, stlPayOrdersDtos);
                    }
                    else {
                        deal_交易所_协议正回购(finProductTradeDto, 持仓类型, etrPositionHoldDto);
                    }
                }
                else {
                    if (StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())) {
                        deal_交易所_逆回购到期(finProductTradeDto, 持仓类型, etrPositionHoldDto, stlPayOrdersDtos);
                    }
                    else if (StringUtil.isEmpty(entrustId) || null != finProductTradeDto.getTrdProductDeal()) {
                        deal_交易所_逆回购(finProductTradeDto, 持仓类型, etrPositionHoldDto);
                    }
                }
            }
            else if (StringUtil.equals(finProductTradeDto.getAssetType2(), DictCodeList.回购类型_质押式竞价回购.getValue())) {
                if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                    if (StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())) {
                        deal_交易所_竞价正回购到期(finProductTradeDto, 持仓类型, etrPositionHoldDto, stlPayOrdersDtos);
                    }
                    else {
                        deal_交易所_竞价正回购(finProductTradeDto, 持仓类型, etrPositionHoldDto);
                    }
                }
                else {
                    if (StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())) {
                        // 相同逻辑，若逻辑发生变化请重新写调用方法，不修改原方法。目的：便于理清业务逻辑
                        deal_交易所_逆回购到期(finProductTradeDto, 持仓类型, etrPositionHoldDto, stlPayOrdersDtos);
                    }
                    else if (StringUtil.isEmpty(entrustId) || null != finProductTradeDto.getTrdProductDeal()) {
                        // 相同逻辑，若逻辑发生变化请重新写调用方法，不修改原方法。目的：便于理清业务逻辑
                        deal_交易所_逆回购(finProductTradeDto, 持仓类型, etrPositionHoldDto);
                    }
                }
            }
        }
    }

    /**
     * 质押式回购_银行间_正回购
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_银行间_正回购(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                     EtrPositionHoldDto etrPositionHoldDto) {
        // 委托代码(未使用)
        // String entrustId = finProductTradeDto.getEntrustId();
        // 资产代码
        String assetCode = finProductTradeDto.getTrdProductDeal().getFinprodId();
        // 交易日期
        Date tradeDate = finProductTradeDto.getTrdProductDeal().getTradeDate();
//        BigDecimal 债券面值 = EtrCommonCashPositionDealService.gen债券面值(assetCode, tradeDate);
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易生效
        if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
            // 有委托 or 无委托 统一处理
            // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
            etrCashPositionFlowDto.setAmount(交易日金额);
            // 设置账号
            String acctId;
            // 通道交易时
            if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId);
            }
            else {
                acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt1.资金账户类型_产品托管户.getValue());
                etrCashPositionFlowDto.setChlFinprodId(null);
            }
            etrCashPositionFlowDto.setAcctId(acctId);
            etrCashPositionFlowDto.setOrderId(finProductTradeDto.getTrdProductDeal().getTradeId());
            // 添加现金流水到目标dto
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            // 新业务
            if (StringUtil.isNotEmpty(entrustId)
                    && !StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) {
                // 有委托时，冲销委托时影响的质押券信息，重新以交易的质押券来影响持仓
                if(!etrPositionHoldDto.isNeedNegative()) {
                    etrPositionHoldDto.getPtlSecPositionFlows().addAll(EtrCommonCashPositionDealService.findNegateSecFlowByEntrustId(entrustId, finProductTradeDto.getTrdProductDeal().getTradeId()));
                    etrPositionHoldDto.getPtlSecPositionFlows().addAll(生成质押券的实时头寸(finProductTradeDto, !etrPositionHoldDto.isNeedNegative(), new ArrayList<>()));
                }else{
                    // 撤销时直接冲销
                    etrPositionHoldDto.getPtlSecPositionFlows().addAll(
                            EtrCommonCashPositionDealService.findNegateSecFlowByEntrustId(
                                    finProductTradeDto.getTrdProductDeal().getTradeId(), finProductTradeDto.getTrdProductDeal().getTradeId()));
                    //update by zznn 20210925 start
                    //从数据库取值时已经取反   撤销时又置反   不改共通  故这里置反一次
                    if(!StringUtil.isEmptyList(etrPositionHoldDto.getPtlSecPositionFlows())){
                        List<EtrSecPositionFlowDto> ptlSecPositionFlows = etrPositionHoldDto.getPtlSecPositionFlows();
                        ptlSecPositionFlows.forEach(item ->{
                            item.setAmount(item.getAmount().negate());
                        });
                        etrPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
                    }
                    //update by zznn 20210925 end
                }
            }
            if (StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) {
                // 写入指令持仓流水-质押式回购
                etrSecPositionFlowDto.setAmount(交易券面总额);
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
            if (StringUtil.isEmpty(entrustId)
                    && StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) { // 无委托
                // 持仓流水dto处理 交易日减少交易券面总额/100
//                etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(交易券面总额, 债券面值, 2).negate());
                // 添加持仓流水到目标dto
//                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                // 写入质押品的组合持仓流水-债券
                List<EtrSecPositionFlowDto> ptlSecPositionFlowList = ptlSecPositionFlowLogic(finProductTradeDto, !etrPositionHoldDto.isNeedNegative());
                if( etrPositionHoldDto.isNeedNegative()){
                    for(EtrSecPositionFlowDto temp :ptlSecPositionFlowList){
                        temp.setAmount(temp.getAmount().negate());
                    }
                }
                etrPositionHoldDto.getPtlSecPositionFlows().addAll(ptlSecPositionFlowList);
                // 写入质押品的组合持仓流水-质押式回购
                etrSecPositionFlowDto.setAmount(交易券面总额);
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
    }

    /**
     * 质押式回购_银行间_逆回购
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_银行间_逆回购(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                     EtrPositionHoldDto etrPositionHoldDto) {
        BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
//         交易生成的持仓流水
         EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
         finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                etrCashPositionFlowDto.setAmount(交易日金额.negate());
                // 设置账号
                String acctId;
                // 通道交易划款方式_一次性划款时，使用通道资金账户
                if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId);
                }
                else {
                    acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt1.资金账户类型_产品托管户.getValue());
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
                etrCashPositionFlowDto.setAcctId(acctId);
                etrCashPositionFlowDto.setOrderId(finProductTradeDto.getTrdProductDeal().getTradeId());
                // 添加现金流水到目标dto
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);

                
            }
        }
        if (StringUtil.isEmpty(entrustId) || StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) {
            if (DictCodeList.持仓类型_实际.equals(持仓类型)){
                etrSecPositionFlowDto.setAmount(交易日金额);
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }    
        }
    }

    /**
     * 质押式回购_银行间_正回购到期
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_银行间_正回购到期(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                       EtrPositionHoldDto etrPositionHoldDto, List<StlPayOrdersDto> stlPayOrdersDtos) {
        // 委托代码(未使用)
        // String entrustId = finProductTradeDto.getEntrustId();
        // 资产代码
        String assetCode = finProductTradeDto.getTrdProductDeal().getFinprodId();
        // 交易日期
        Date tradeDate = finProductTradeDto.getTrdProductDeal().getTradeDate();
//        BigDecimal 债券面值 = EtrCommonCashPositionDealService.gen债券面值(assetCode, tradeDate);
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        // BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // BigDecimal 委托全价 = finProductTradeDto.getTrdProductDeal().getFpriceAmt();
        // 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
        // BigDecimal amount = ArithmeticUtil.subtract(交易日金额,
        // ArithmeticUtil.divide(ArithmeticUtil.multiply(交易券面总额, 委托全价), 债券面值, 2));
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();

        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative() || finProductTradeDto.isBatch()
                // 自动交收批处理没有发起流程
                || StringUtil.equals("03", finProductTradeDto.getStage())) {
//            if (StringUtil.isEmpty(entrustId)) { // 无委托
                // 持仓流水dto处理 交割日增加交易券面总额/债券面值
//                etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(交易券面总额, 债券面值, 2));
                // 添加持仓流水到目标dto
//                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
//                到期日每只券增加交易质押券面总额/100
//                etrPositionHoldDto.getPtlSecPositionFlows().addAll(ptlSecPositionFlowLogic(finProductTradeDto, false));
                if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                    stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                        // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                        EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                        etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                        // 设置账号
                        // 通道交易划款方式_一次性划款时，使用通道资金账户
                        if (!StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                        	etrCashPositionFlowDto1.setChlFinprodId(null);
                        }
                        etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
                        etrCashPositionFlowDto1.setSeqno(getSeqNo());
                        // 添加现金流水到目标dto
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                    });
                }
//            }
        }
        // 交易生效 
        //modify by mashukai 20210326 ZGBSS-633 质押式银行间正回购到期交易生效后，质押持仓未释放 start
        if (DictCodeList.持仓类型_实际.equals(持仓类型) || etrPositionHoldDto.isNeedNegative() || finProductTradeDto.isBatch()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                //到期日每只券增加交易质押券面总额/100
                etrPositionHoldDto.getPtlSecPositionFlows().addAll(ptlSecPositionFlowLogic(finProductTradeDto, false));
            } else { // 委托
                List<EtrSecPositionFlowDto> etrSecPositionFlowDtos = Lists.newArrayList();
                if (!etrPositionHoldDto.isNeedNegative()) {
                    //到期日每只券增加交易质押券面总额/100
                    // 冲销首期交易数据
                    if(StringUtil.equals(finProductTradeDto.getTrdProductDeal().getPs(), DictCodeList.交易方向_到期.getValue())){
                        etrSecPositionFlowDto.setAmount(交易券面总额.negate());
                        etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                    }
//                    etrPositionHoldDto.getPtlSecPositionFlows().addAll(ptlSecPositionFlowLogic(finProductTradeDto, false));
                    // 查找委托时的指令持仓,用于冲销
                    etrSecPositionFlowDtos = EtrCommonCashPositionDealService.selectAllPositionByTotle(entrustId);
                } else {
                    // 原始交易编号
                    String orginTradeId = finProductTradeDto.getTrdProductDeal().getTradeId();
                    if(orginTradeId.lastIndexOf("R")>0) {
                        orginTradeId = orginTradeId.substring(0, orginTradeId.lastIndexOf("R"));
                    }
                    // 撤销时,查找原始交易指令持仓流水,用于冲销
                    etrSecPositionFlowDtos = EtrCommonCashPositionDealService.selectAllPositionByTotle(orginTradeId);
                }
                etrSecPositionFlowDtos.forEach(item -> {
                    item.setSeqno(getSeqNo());
                    //生效日
                    item.setCdate(finProductTradeDto.getTrdProductDeal().getTradeDate());
                    item.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                    item.setTradeId(finProductTradeDto.getTrdProductDeal().getTradeId());
                    item.setPortfolioId(finProductTradeDto.getTrdProductDealAdd().getPortfolioId());
                    item.setBranch(0l);
                    item.setCreateTime(new Date());
                    item.setUpdateTime(new Date());
                    // 金额取反 (撤销时,金额在其他地方取反,此处不用取反)
                    if (!etrPositionHoldDto.isNeedNegative()) {
                        item.setAmount(item.getAmount().negate());
                    }
                });
                etrPositionHoldDto.getPtlSecPositionFlows().addAll(etrSecPositionFlowDtos);
            }
        }
        //modify by mashukai 20210326 ZGBSS-633 质押式银行间正回购到期交易生效后，质押持仓未释放 end
    }

    /**
     * 质押式回购_银行间_逆回购到期
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_银行间_逆回购到期(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                       EtrPositionHoldDto etrPositionHoldDto, List<StlPayOrdersDto> stlPayOrdersDtos) {
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
         finProductTradeDto);
        BigDecimal prinAmt = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易生效
        if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                    // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                    EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                    etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt());
                    etrCashPositionFlowDto1.setCdate(stlPayOrdersDto.getSettleDate());
                    // 设置账号
                    // 通道交易划款方式_一次性划款时，使用通道资金账户
                    if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                    } else {
                        etrCashPositionFlowDto1.setChlFinprodId(null);
                    }
                    etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
                    etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
                    etrCashPositionFlowDto1.setSeqno(getSeqNo());
                    // 添加现金流水到目标dto
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                    if (!StringUtil.equals(finProductTradeDto.getStage(), "03")) {
                        // 预增现金 资金到账日增加到账金额
                        EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto1);
                        etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                        etrCashPositionFlowDto2.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                        etrCashPositionFlowDto2.setSeqno(getSeqNo());
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
                    }else{
                        // 本币业务首期登录了,批处理没有流程直接处理
                        if(StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) {
                            //资产持仓流水减少券面总额
                            etrSecPositionFlowDto.setAmount(prinAmt.negate());
                            // 添加现金流水到目标dto
                            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                        }
                    }
                });
            }
        }
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()||finProductTradeDto.isBatch()) {
            if (StringUtil.isEmpty(entrustId) || StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) { // 无委托
                if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                    stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                        // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                        EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                        etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt());
                        etrCashPositionFlowDto1.setCdate(stlPayOrdersDto.getSettleDate());
                        // 设置账号
                        // 通道交易划款方式_一次性划款时，使用通道资金账户
                        if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                        } else {
                            etrCashPositionFlowDto1.setChlFinprodId(null);
                        }
                        etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
                        etrCashPositionFlowDto1.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                        etrCashPositionFlowDto1.setSeqno(getSeqNo());
                        // 添加现金流水到目标dto
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
    
                    });
                }
                //首期没登录  这里注释 -> modify by jp 本币业务首期登录了,此处做判断
                if(StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) {
                    //资产持仓流水减少券面总额
                    etrSecPositionFlowDto.setAmount(prinAmt.negate());
                    // 添加现金流水到目标dto
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                }
            }
        }
    }

    /**
     * 质押式回购_交易所_协议正回购
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_交易所_协议正回购(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                       EtrPositionHoldDto etrPositionHoldDto) {
        // 委托代码(未使用)
        // String entrustId = finProductTradeDto.getEntrustId();
        // 资产代码
        String assetCode = finProductTradeDto.getTrdProductDeal().getFinprodId();
        // 交易日期
        Date tradeDate = finProductTradeDto.getTrdProductDeal().getTradeDate();
//        BigDecimal 债券面值 = EtrCommonCashPositionDealService.gen债券面值(assetCode, tradeDate);
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易生效
        if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
            // 有委托 or 无委托 统一处理
            // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
            etrCashPositionFlowDto.setAmount(交易日金额);
            // 设置账号
            String acctId;
            // 通道交易时
            if (StringUtil.isNotEmpty(chlId)) {
                if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId);
                }
                else {
                    acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt1.资金账户类型_产品托管户.getValue());
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
            }
            else {
                acctId = EtrCommonCashPositionDealService.select资金账户(finProductTradeDto.getTrdProductDeal().getSecManageAcctId(),
                        finProductTradeDto.getTrdProductDeal().getCcy());
                etrCashPositionFlowDto.setChlFinprodId(null);
            }
            etrCashPositionFlowDto.setAcctId(acctId);
            etrCashPositionFlowDto.setOrderId(finProductTradeDto.getTrdProductDeal().getTradeId());
            // 添加现金流水到目标dto
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);

            if (StringUtil.isNotEmpty(entrustId)) {
                // 有委托时，冲销委托时影响的质押券信息，重新以交易的质押券来影响持仓
                etrPositionHoldDto.getPtlSecPositionFlows().addAll(EtrCommonCashPositionDealService.findNegateSecFlowByEntrustId(entrustId, finProductTradeDto.getTrdProductDeal().getTradeId()));
                etrPositionHoldDto.getPtlSecPositionFlows().addAll(生成质押券的实时头寸(finProductTradeDto, !etrPositionHoldDto.isNeedNegative(), new ArrayList<>()));
            }else {
                //无委托时
                etrSecPositionFlowDto.setAmount(交易日金额);
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                // 添加持仓流水到目标dto
                etrPositionHoldDto.getPtlSecPositionFlows().addAll(ptlSecPositionFlowLogic(finProductTradeDto, !etrPositionHoldDto.isNeedNegative()));
            }
        }
    }

    /**
     * 写入质押品的组合持仓流水
     *
     * @param finProductTradeDto    画面参数Dto
     * @param flag 是否是关闭委托
     */
    private static List<EtrSecPositionFlowDto> ptlSecPositionFlowLogic(BaseTradeProductDealDto finProductTradeDto, boolean flag) {
        // List<组合持仓流水DTO>(若上记的List<回购质押品DTO>为空，则此为空，反之循环构造DTO)
        // 通过上记输出的List<输出的质押品DTO>，循环该List构造组合持仓流水DTO
        List<EtrSecPositionFlowDto> list = new ArrayList<>() ;
        String ps = finProductTradeDto.getTrdProductDeal().getPs();
        
        // 若是到期交易，直接查询 指令持仓流水数据然后取反
        if (StringUtil.equals(ps, DictCodeList.交易方向_到期.getValue())) {
            String tradeId = finProductTradeDto.getTrdProductDeal().getFinprodId() ;
            list = EtrCommonCashPositionDealService.selectAllPositionByTotle(tradeId);
            if(list.size() >0 ){
                // 修改相关取值，重新入库
                for (EtrSecPositionFlowDto etrSecPositionFlowDto : list) {
                    etrSecPositionFlowDto.setSeqno(getSeqNo());
                    //生效日
                    etrSecPositionFlowDto.setCdate(finProductTradeDto.getTrdProductDeal().getTradeDate());
                    etrSecPositionFlowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                    etrSecPositionFlowDto.setTradeId(finProductTradeDto.getTrdProductDeal().getTradeId());
                    etrSecPositionFlowDto.setPortfolioId(finProductTradeDto.getTrdProductDealAdd().getPortfolioId());
                    etrSecPositionFlowDto.setBranch(0l);
                    etrSecPositionFlowDto.setCreateTime(new Date());
                    etrSecPositionFlowDto.setUpdateTime(new Date());
                    etrSecPositionFlowDto.setCreateUser(LoginUserUtil.getUserName());
                    etrSecPositionFlowDto.setUpdateUser(LoginUserUtil.getUserName());
                    // 金额取反
                    etrSecPositionFlowDto.setAmount(etrSecPositionFlowDto.getAmount().negate());
                }
            }
        }else{
            生成质押券的实时头寸(finProductTradeDto, flag, list);
        }
        return list ;
    }

    private static List<EtrSecPositionFlowDto> 生成质押券的实时头寸(BaseTradeProductDealDto input, boolean flag, List<EtrSecPositionFlowDto> list) {
        BussRepoSecDto bussRepoSecDto = input.getBussRepoSec();
        if (bussRepoSecDto != null && !StringUtil.isEmptyList(bussRepoSecDto.getPledgeBondList())) {
            // 可质押券信息（含投资目的）
            Map<String, List<PledgeBondInfoDto>> avaMap = etrCommonCashPositionDealService.selectAvaPledgeWithInvAim(input)
                    .stream().collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
            avaMap.entrySet().stream().forEach(entry -> {
                Collections.sort(entry.getValue(), Comparator.comparing(PledgeBondInfoDto::getInvAimSort));
            });
            EtrSecPositionFlowDto etrSecPositionFlowDto;
            for (PledgeBondInfoDto pledgeBond : bussRepoSecDto.getPledgeBondList()) {
                // 待分配投资目的的质押券面总额
                BigDecimal pledgePrin = pledgeBond.getPledgePrin();
                List<PledgeBondInfoDto> pledgeBondInfoDtos = avaMap.get(pledgeBond.getBondCode());
                if(!StringUtil.isEmptyList(pledgeBondInfoDtos)){
                    for (PledgeBondInfoDto avaBond : pledgeBondInfoDtos) {
                        etrSecPositionFlowDto = new EtrSecPositionFlowDto();
                        // 当前投资目的可分配券面总额
                        BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                        // 当前投资目的决定分配的券面总额
                        BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;
    
                        etrSecPositionFlowDto.setSeqno(getSeqNo());
                        // 组合代码 = 画面.组合代码
                        etrSecPositionFlowDto.setPortfolioId(input.getTrdProductDealAdd().getPortfolioId());
                        // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
                        String secAcctId = input.getTrdProductDeal().getSecManageAcctId();
                        if (StringUtil.isEmpty(secAcctId) && StringUtil.isEmpty(input.getTrdProductDeal().getChlId())) {
                            secAcctId = "999999";
                        }
                        else if (StringUtil.isNotEmpty(input.getTrdProductDeal().getChlId())) {
                            secAcctId = input.getTrdProductDeal().getChlId();
                        }
                        etrSecPositionFlowDto.setSecAcctId(secAcctId);
                        // 金融产品代码 = 输出质押品DTO.金融产品代码
                        etrSecPositionFlowDto.setFinprodId(pledgeBond.getFinprodId());
                        // 分支序号 = 系统固定参数，默认为0
                        etrSecPositionFlowDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                        // 持仓类型
                        etrSecPositionFlowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                        // 日期 = 金融产品交易Entity.交易日
                        etrSecPositionFlowDto.setCdate(input.getTrdProductDeal().getVdate());
                        if(StringUtil.equals(input.getTrdProductDeal().getPs(),DictCodeList.交易方向_到期.getValue())){
                            etrSecPositionFlowDto.setCdate(input.getTrdProductDeal().getSettleDate());
                        }
    
                        // 数量 = 输出质押品DTO.质押数量
                        BigDecimal amt = BigDecimal.ZERO ;
                        // modify by wanshiwei ZGZGST-3314 【场内质押式回购交易】质押式协议回购，做了正回购交易，撤销后实时资产头寸查询的指令可用持仓错误 20210928 start
                        // BaseEtrCashPositionCommServiceImpl.java executeEtrCashPosition 后面做了取反
                        /*if(flag){ // 委托开始减少,委托关闭增加*/
                            amt = ArithmeticUtil.divide(prin, new BigDecimal("100"), 4).negate() ;
                        /*}else{
                            amt = ArithmeticUtil.divide(prin, new BigDecimal("100"), 4) ;
                        }*/
                        // modify by wanshiwei ZGZGST-3314 【场内质押式回购交易】质押式协议回购，做了正回购交易，撤销后实时资产头寸查询的指令可用持仓错误 20210928 end
                        etrSecPositionFlowDto.setAmount(amt);
                        // 币种 = 金融产品交易Entity.币种
                        etrSecPositionFlowDto.setCcy(input.getTrdProductDeal().getCcy());
                        // 交易编号 = 金融产品交易Entity.交易编号
                        etrSecPositionFlowDto.setTradeId(input.getTrdProductDeal().getTradeId());
                        // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                        etrSecPositionFlowDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());
    
                        if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
    //                            if (StringUtil.isNotEmpty(input.getActPortfolioId()) && !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
    //                                PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
    //                                BeanUtil.copyPropertiesIgnoreNull(subResultDto, etrSecPositionFlowDto);
    //                                subResultDto.setPortfolioId(input.getActPortfolioId());
    //                                lstPtlSecPositionFlowDto.add(subResultDto);
    //                            }
                            list.add(etrSecPositionFlowDto);
                        }
                        // 分配完毕后,停止可质押券信息（含投资目的）的循环
                        pledgePrin = pledgePrin.subtract(prin);
                        if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
                            break;
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 质押式回购_交易所_协议正回购到期
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_交易所_协议正回购到期(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                         EtrPositionHoldDto etrPositionHoldDto, List<StlPayOrdersDto> stlPayOrdersDtos) {
        // 委托代码(未使用)
        // String entrustId = finProductTradeDto.getEntrustId();
        // 资产代码
        String assetCode = finProductTradeDto.getTrdProductDeal().getFinprodId();
        // 交易日期
        Date tradeDate = finProductTradeDto.getTrdProductDeal().getTradeDate();
//        BigDecimal 债券面值 = EtrCommonCashPositionDealService.gen债券面值(assetCode, tradeDate);
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        // BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // 委托全价获取方式
        // BigDecimal 委托全价 = finProductTradeDto.getTrdProductDeal().getFpriceAmt();
        // 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
        // BigDecimal amount = ArithmeticUtil.subtract(交易日金额,
        // ArithmeticUtil.divide(ArithmeticUtil.multiply(交易券面总额, 委托全价), 债券面值, 2));
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易提交 增加撤销数据录入
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative() || finProductTradeDto.isBatch()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                if (StringUtil.equals(finProductTradeDto.getTrdProductDeal().getFinprodType2(),
                    DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                    etrPositionHoldDto.getPtlSecPositionFlows().addAll(ptlSecPositionFlowLogic(finProductTradeDto, false));
                }// 到期续做逆回购金额支付方向会走到这个方法
                if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                    stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                        // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                        EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                        etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                        // 非一次性划款不入通道
                        if (StringUtil.isNotEmpty(chlId)) {
                            if (!StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                                etrCashPositionFlowDto1.setChlFinprodId(null);
                            }
                        }
                        // 设置账号
                        etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
                        etrCashPositionFlowDto1.setSeqno(getSeqNo());
                        // 添加现金流水到目标dto
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                    });
                }
            }
        }
    }

    /**
     * 质押式回购_交易所_逆回购
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_交易所_逆回购(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                     EtrPositionHoldDto etrPositionHoldDto) {
        BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        BigDecimal 交易日本金 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
//         交易生成的持仓流水
         EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                etrCashPositionFlowDto.setAmount(交易日金额.negate());
                // 设置账号
                String acctId;
                // 通道交易划款方式_一次性划款时，使用通道资金账户
                if (StringUtil.isNotEmpty(chlId)) {
                    if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                        acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId);
                    }
                    else {
                        acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt1.资金账户类型_产品托管户.getValue());
                        etrCashPositionFlowDto.setChlFinprodId(null);
                    }
                }
                else {
                    acctId = EtrCommonCashPositionDealService.select资金账户(finProductTradeDto.getTrdProductDeal().getSecManageAcctId(),
                            finProductTradeDto.getTrdProductDeal().getCcy());
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
                etrCashPositionFlowDto.setAcctId(acctId);
                etrCashPositionFlowDto.setOrderId(finProductTradeDto.getTrdProductDeal().getTradeId());
                // 添加现金流水到目标dto
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }
        if (DictCodeList.持仓类型_实际.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
//            if (StringUtil.isEmpty(entrustId)
//            || null != finProductTradeDto.getTrdProductDeal()
//            ) { // 无委托
                // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                etrSecPositionFlowDto.setAmount(交易日本金);
                // 添加现金流水到目标dto
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
//            }
            // 登录两笔流水:1、通过本金冲销指令流水 2、交易金额流水
            etrCashPositionFlowDto.setAcctId(EtrCommonCashPositionDealService.select资金账户(
                    finProductTradeDto.getTrdProductDeal().getSecManageAcctId(), finProductTradeDto.getTrdProductDeal().getCcy()));
            EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
            etrCashPositionFlowDto1.setAmount(交易日本金);
            etrCashPositionFlowDto1.setSeqno(getSeqNo());
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);

            etrCashPositionFlowDto.setAmount(交易日金额.negate());
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
        }
    }

    /**
     * 质押式回购_交易所_协议逆回购到期
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_交易所_逆回购到期(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                       EtrPositionHoldDto etrPositionHoldDto, List<StlPayOrdersDto> stlPayOrdersDtos) {
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
                finProductTradeDto);
        // 交易日金额
        BigDecimal tradeAmt = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // 交易本金
        BigDecimal prinAmt = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();

        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 通道性质
        String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易生效
        if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                    // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                    EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                    etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt());
                    etrCashPositionFlowDto1.setCdate(stlPayOrdersDto.getSettleDate());
                    // 设置账号
                    // 通道交易划款方式_一次性划款时，使用通道资金账户
                    if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                    } else {
                        etrCashPositionFlowDto1.setChlFinprodId(null);
                    }
                    etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
                    etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
                    etrCashPositionFlowDto1.setSeqno(getSeqNo());
                    // 添加现金流水到目标dto
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                    if(!finProductTradeDto.isNotWorkflow()){
                        // 预增现金 资金到账日增加到账金额
                        EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto1);
    //                    String acctId1;
                        // 通道交易划款方式_一次性划款时，使用通道资金账户
    //                    if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
    //                        acctId1 = EtrCommonCashPositionDealService.select通道资金账户(chlId);
    //                    } else {
    //                        acctId1 = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt1.资金账户类型_产品托管户.getValue());
    //                        etrCashPositionFlowDto2.setChlFinprodId(null);
    //                    }
                        etrCashPositionFlowDto2.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                        etrCashPositionFlowDto2.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                        etrCashPositionFlowDto2.setSeqno(getSeqNo());
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
                    }
                });
            }
            if(finProductTradeDto.isNotWorkflow()){
                if(finProductTradeDto.getTrdProductDeal().getPrinAmt() != null){
                    etrSecPositionFlowDto.setAmount(finProductTradeDto.getTrdProductDeal().getPrinAmt().negate());
                }else{
                    etrSecPositionFlowDto.setAmount(BigDecimal.ZERO);
                }
                // 统一挪到方法最后添加
                // etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative() || finProductTradeDto.isBatch()) {
            if(StringUtil.equals(finProductTradeDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())){
                etrPositionHoldDto.getPtlSecPositionFlows().addAll(ptlSecPositionFlowLogic(finProductTradeDto,false));
            }//到期续做正回购金额收入方向会走到这个方法
            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                    if(!finProductTradeDto.isNotWorkflow()){
                        // 预增现金 资金到账日增加到账金额
                        EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto);
                        // 通道交易划款方式_一次性划款时，使用通道资金账户
                        if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                        } else {
                            etrCashPositionFlowDto2.setChlFinprodId(null);
                        }
                        etrCashPositionFlowDto2.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt());
                        etrCashPositionFlowDto2.setOrderId(stlPayOrdersDto.getOrderId());
                        etrCashPositionFlowDto2.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                        etrCashPositionFlowDto2.setSeqno(getSeqNo());
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
                    }
                });
            }
            if(StringUtil.isEmpty(finProductTradeDto.getEntrustId())) { // 无委托
                etrSecPositionFlowDto.setAmount(prinAmt.negate());
                // 添加现金流水到目标dto
                // 统一挪到方法最后添加
                // etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
        etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
    }

    /**
     * 质押式回购_交易所_竞价正回购
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_交易所_竞价正回购(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                       EtrPositionHoldDto etrPositionHoldDto) {
        // 委托代码(未使用)
        // String entrustId = finProductTradeDto.getEntrustId();
        // 资产代码
        String assetCode = finProductTradeDto.getTrdProductDeal().getFinprodId();
        // 交易日期
        Date tradeDate = finProductTradeDto.getTrdProductDeal().getTradeDate();
//        BigDecimal 债券面值 = EtrCommonCashPositionDealService.gen债券面值(assetCode, tradeDate);
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();
        BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 通道代码
        String chlId = finProductTradeDto.getTrdProductDeal().getChlId();
        // 通道性质
         String chlType = StringUtil.isEmpty(chlId) ? null : EtrCommonCashPositionDealService.queryChlType(chlId);
        // 交易生效
        if (DictCodeList.持仓类型_实际.equals(持仓类型)|| etrPositionHoldDto.isNeedNegative()) {//modify by jiangpeng on 20210406 ZGBSS-1403 质押式竞价正回购撤销流程生效后，实时资产头寸查询中指令可用持仓未减少 start
            // 有委托 or 无委托 统一处理
            // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
            etrCashPositionFlowDto.setAmount(交易日金额);
            // 设置账号
            String acctId;

            // 通道交易时
            if (StringUtil.isNotEmpty(chlId)) {
                if (StringUtil.equals(chlType, DictCodeListExt2.划款方式_一次性划款.getValue())) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(chlId);
                }
                else {
                    acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy, DictCodeListExt1.资金账户类型_产品托管户.getValue());
                    etrCashPositionFlowDto.setChlFinprodId(null);
                }
            }
            else {
                acctId = EtrCommonCashPositionDealService.select资金账户(finProductTradeDto.getTrdProductDeal().getSecManageAcctId(),
                        finProductTradeDto.getTrdProductDeal().getCcy());
                etrCashPositionFlowDto.setChlFinprodId(null);
            }
            etrCashPositionFlowDto.setAcctId(acctId);
            etrCashPositionFlowDto.setOrderId(finProductTradeDto.getTrdProductDeal().getTradeId());
            // 添加现金流水到目标dto
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            
            //添加指令可用持仓
            if (StringUtil.isEmpty(entrustId)
                    // 正回购登录指令持仓 modify by fuyongsheng  20220713 start
                    || null != finProductTradeDto.getTrdProductDeal()
                    // 正回购登录指令持仓 modify by fuyongsheng  20220713 end
            ) {
                etrSecPositionFlowDto.setAmount(交易券面总额);
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
//            if (StringUtil.isEmpty(entrustId)) { // modify by jiangpeng on 20210617 TISHKBS-2296 委托指令生效后不更新标准券持仓,交易录单提交时再更新
                // 标准券
                EtrSecPositionFlowDto etrSecPositionFlowDtoTemp = new EtrSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDtoTemp, etrSecPositionFlowDto);
                etrSecPositionFlowDtoTemp.setSeqno(getSeqNo());
                etrSecPositionFlowDtoTemp.setAmount(ArithmeticUtil.divide(交易券面总额, 债券面值, 2).negate());
                etrSecPositionFlowDtoTemp.setFinprodId(XN_STANDARD_BOND_PRODID);
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDtoTemp);
                // 添加持仓流水到目标dto
//            }
        }
        //add by jiangpeng on 20210406 ZGBSS-1403 质押式竞价正回购撤销流程生效后，实时资产头寸查询中指令可用持仓未减少 start
        if (etrPositionHoldDto.isNeedNegative()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                EtrSecPositionFlowDto temp = new EtrSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(temp, etrSecPositionFlowDto);
                temp.setAmount(交易日金额);
                etrPositionHoldDto.getPtlSecPositionFlows().add(temp);
            }
        }
        // add by jiangpeng on 20210406 ZGBSS-1403 质押式竞价正回购撤销流程生效后，实时资产头寸查询中指令可用持仓未减少 end
    }

    /**
     * 质押式回购_交易所_竞价正回购到期
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    private static void deal_交易所_竞价正回购到期(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                         EtrPositionHoldDto etrPositionHoldDto, List<StlPayOrdersDto> stlPayOrdersDtos) {
        // BigDecimal 交易日金额 = finProductTradeDto.getTrdProductDeal().getTradeAmt();
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
                finProductTradeDto);
        // 组合代码
        String portfolioId = finProductTradeDto.getTrdProductDealAdd().getPortfolioId();
        // 币种
        String ccy = finProductTradeDto.getTrdProductDeal().getCcy();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        BigDecimal 交易券面总额 = finProductTradeDto.getTrdProductDeal().getPrinAmt();

        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
                finProductTradeDto);

        if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
            //标准券持仓
            etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(交易券面总额, new BigDecimal(100), 2));
            etrSecPositionFlowDto.setFinprodId(XN_STANDARD_BOND_PRODID);
            // 添加持仓流水到目标dto
            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            if(finProductTradeDto.isNotWorkflow()){
                if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                    stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                        // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                        EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto);
                        etrCashPositionFlowDto2.setOrderId(stlPayOrdersDto.getOrderId());
                        etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                        etrCashPositionFlowDto2.setCdate(stlPayOrdersDto.getSettleDate());
                        // 设置账号
                        String acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy,
                                DictCodeListExt1.资金账户类型_产品托管户.getValue());
                        etrCashPositionFlowDto2.setChlFinprodId(null);
                        etrCashPositionFlowDto2.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto2.setSeqno(getSeqNo());
                        // 添加现金流水到目标dto
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
                    });
                }
                // 交易本身的持仓
                生成交易持仓2(finProductTradeDto.getTrdProductDeal().getPrinAmt().negate(), etrPositionHoldDto, etrSecPositionFlowDto, finProductTradeDto.getTrdProductDeal().getFinprodId());
            }
        }
        // 交易提交
        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative() || finProductTradeDto.isBatch()) {
            if (StringUtil.isEmpty(entrustId)) { // 无委托
                if(!finProductTradeDto.isNotWorkflow()){
                    if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                        stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                            // 现金流水dto处理 交割日减少(交易金额-交易券面总额*委托全价/债券面值）
                            EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
                            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto);
                            etrCashPositionFlowDto2.setOrderId(stlPayOrdersDto.getOrderId());
                            etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                            etrCashPositionFlowDto2.setCdate(stlPayOrdersDto.getSettleDate());
                            // 设置账号
                            String acctId = EtrCommonCashPositionDealService.select组合托管户(portfolioId, ccy,
                                    DictCodeListExt1.资金账户类型_产品托管户.getValue());
                            etrCashPositionFlowDto2.setChlFinprodId(null);
                            etrCashPositionFlowDto2.setAcctId(stlPayOrdersDto.getMyAcctNo());
                            etrCashPositionFlowDto2.setSeqno(getSeqNo());
                            // 添加现金流水到目标dto
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
                        });
                    }
                    // 交易本身的持仓
                    生成交易持仓2(finProductTradeDto.getTrdProductDeal().getPrinAmt().negate(), etrPositionHoldDto, etrSecPositionFlowDto, finProductTradeDto.getTrdProductDeal().getFinprodId());
                }
            }
        }
    }

    /**
     * 质押式回购_委托开始日
     * 
     * @param entrustCommonDto
     * @param etrPositionHoldDto
     */
    public static void deal质押式回购EntrustStart(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        if (entrustCommonDto != null) {
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            // 交易场所
            String tradePlace = etrEntrust.getTradeMarket();
            // 正回购 or 逆回购
            String ps = etrEntrust.getFinprodType2();
            // H01:质押式协议回购 , H01:质押式竞价回购
//            String type6 = baseAssetCashPositionCommMapper.selectFundType(etrEntrust.getFinprodId());
            String type6 = entrustCommonDto.getEtrEntrustAdd().getAssetType1() ;
            if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(ps)) {
                // 委托生成的持仓流水
                // 减少本金金额
                EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
                    entrustCommonDto);
                // 交易所竞价回购 改变标准券持仓
                if ((StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                    && StringUtil.equals(type6, DictCodeList.回购类型_质押式竞价回购.getValue())) {
                    etrSecPositionFlowDto.setFinprodId(XN_STANDARD_BOND_PRODID);
//                    etrSecPositionFlowDto.setAmount(etrEntrust.getEntrustAmt().negate());
                    etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(etrEntrust.getEntrustAmt(), 债券面值, 2).negate());
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                }else{
                    // 质押品的组合持仓流水
                    etrPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlowLogic(entrustCommonDto,true)) ;
                }
                
            }
            else if (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(ps)) {
                // 委托生成的现金流水
                EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                    entrustCommonDto);
                String acctId;
                if (StringUtil.isNotEmpty(etrEntrust.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(
                    EtrCommonCashPositionDealService.queryChlType(etrEntrust.getChlId()))) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(etrEntrust.getChlId());
                }
                else if (StringUtil.isNotEmpty(etrEntrust.getChlId()) && DictCodeListExt2.划款方式_逐笔划款.getValue().equals(
                    EtrCommonCashPositionDealService.queryChlType(etrEntrust.getChlId()))) {
                    etrCashPositionFlowDto.setChlFinprodId(null);
                    acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                        DictCodeListExt1.资金账户类型_产品托管户.getValue());
                }else{
                    etrCashPositionFlowDto.setChlFinprodId(null);
                    if (StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_银行间.getValue())) {
                        acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                            DictCodeListExt1.资金账户类型_产品托管户.getValue());
                    }
                    else {// 交易所逆回购的委托开始，使用资金账户
                        acctId = EtrCommonCashPositionDealService.select资金账户(entrustCommonDto.getEtrEntrustAdd().getSecAcctId(), etrEntrust.getCcy());
                    }
                }
                // 减少委托本金金额
                etrCashPositionFlowDto.setAmount(etrEntrust.getEntrustAmt().negate());
                etrCashPositionFlowDto.setAcctId(acctId);
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }
    }

    /**
     * 质押式回购_委托关闭日
     * 
     * @param entrustCommonDto
     * @param etrPositionHoldDto
     */
    public static void deal质押式回购EntrustClose(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        if (entrustCommonDto != null) {
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            EtrEntrustAdd etrEntrustAdd = entrustCommonDto.getEtrEntrustAdd();
            String tradePlace = etrEntrust.getTradeMarket();
            // 正回购 or 逆回购
            String ps = etrEntrust.getFinprodType2();
            if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(ps)) {
                // 委托生成的持仓流水
                // 减少本金金额
                EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
                    entrustCommonDto);
                // 交易所竞价回购 改变标准券持仓
                // H01:质押式协议回购 , H01:质押式竞价回购
                String type6 = entrustCommonDto.getEtrEntrustAdd().getAssetType1() ;
                if ((StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                    && StringUtil.equals(type6, DictCodeList.回购类型_质押式竞价回购.getValue())) {
                    etrSecPositionFlowDto.setFinprodId(XN_STANDARD_BOND_PRODID);
//                    etrSecPositionFlowDto.setAmount(etrEntrust.getEntrustAmt());
                   // etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(etrEntrust.getEntrustAmt(), 债券面值, 2).negate());
                   // etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                    BigDecimal entrustAmt = etrEntrust.getEntrustAmt();
//                    EtrEntrustAdd etrEntrustAdd = entrustCommonDto.getEtrEntrustAdd();
                    if (null != etrEntrustAdd && null != etrEntrustAdd.getTargetType()
                            && etrEntrustAdd.getTargetType().equals(DictCodeListExt3.目标类型_数量.getValue())) {
                        entrustAmt = etrEntrust.getEntrustNum();
                    }
                    //获取已完成额度
                    Map<String,Object> map=new HashMap<>();
                    map.put("sumTradeFlag","01");
                    map.put("entrustClass", etrEntrust.getEntrustClass());
                    map.put("portfolioId",etrEntrust.getPortfolioId());
                    map.put("entrustId",etrEntrust.getEntrustId());
                    BigDecimal finishAmt = EtrCommonCashPositionDealService.getEtrExchangeFinishAmtMuilt(map);
                    //BigDecimal finishAmt = EtrCommonCashPositionDealService.getEtrFinishAmt(etrEntrust.getEntrustId());
                    etrSecPositionFlowDto.setAmount(ArithmeticUtil.subtract(entrustAmt, ArithmeticUtil.divide(finishAmt, 债券面值, 2)));
                    etrSecPositionFlowDto.setCloseFlag("01"); // 委托关闭特殊处理, 委托关闭后, 再撤销交易时, 委托状态会变成已接收, 这时应该要把这笔数据删除
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                } else if ((StringUtil.isEmpty(entrustCommonDto.getIsExistNew())
                        || StringUtil.equals(entrustCommonDto.getIsExistNew(), DictCodeList.是否_否.getValue()))) {
                    //新业务 在交易执行时才占用债券的指令可用持仓 所以强制完成时不用释放
                    // 质押品的组合持仓流水
                    etrPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlowLogic(entrustCommonDto,false)) ;
                }
            }
            else if (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(ps)) {
                // 委托生成的现金流水
                EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                    entrustCommonDto);
                String acctId;
                if (StringUtil.isNotEmpty(etrEntrust.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(
                    EtrCommonCashPositionDealService.queryChlType(etrEntrust.getChlId()))) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(etrEntrust.getChlId());
                }
                else {
                    etrCashPositionFlowDto.setChlFinprodId(null);
                    if (StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_银行间.getValue())) {
                        acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                            DictCodeListExt1.资金账户类型_产品托管户.getValue());
                    }
                    else {// 交易所逆回购的委托开始，使用资金账户
                        acctId = EtrCommonCashPositionDealService.select资金账户(entrustCommonDto.getEtrEntrustAdd().getSecAcctId(),
                                etrEntrust.getCcy());
                    }
                }
                if(StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_银行间.getValue())){
                    //获取已完成额度
                    Map<String,Object> map=new HashMap<>();
                    map.put("sumTradeFlag","01");
                    map.put("entrustClass", etrEntrust.getEntrustClass());
                    map.put("portfolioId",etrEntrust.getPortfolioId());
                    map.put("entrustId",etrEntrust.getEntrustId());
                    BigDecimal finishAmt = EtrCommonCashPositionDealService.getEtrFinishAmtMuilt(map);
                    //BigDecimal finishAmt = EtrCommonCashPositionDealService.getEtrFinishAmt(etrEntrust.getEntrustId());
                    BigDecimal closeAmt = etrEntrustAdd.getCloseAmt();
                    // 已完成额度 = 交易表中有效额度 + 已关闭额度
                    finishAmt = ArithmeticUtil.doAdd(finishAmt, closeAmt);
                    // 减少委托本金金额
                    etrCashPositionFlowDto.setAmount(ArithmeticUtil.subtract(etrEntrust.getEntrustAmt(), finishAmt));
                }else{ // 交易所
                    // 1、根据指令查询已匹配交易数据
                    List<TrdProductDeal> trdProductDeals = EtrCommonCashPositionDealService.selectTrdProductDealByEntrustId(
                            etrEntrust.getEntrustId());
                    // 交易总金额
                    BigDecimal tradeAmtSum = BigDecimal.ZERO;
                    if(!CollectionUtils.isEmpty(trdProductDeals)){
                        tradeAmtSum = trdProductDeals.stream().map(TrdProductDeal::getTradeAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                    // 2、冲销指令金额和已匹配交易数据的差额
                    etrCashPositionFlowDto.setAmount(ArithmeticUtil.subtract(etrEntrust.getEntrustAmt(), tradeAmtSum));
                }
//                etrEntrustAdd.setCloseAmt(ArithmeticUtil.doAdd(closeAmt, etrCashPositionFlowDto.getAmount()));
                etrCashPositionFlowDto.setAcctId(acctId);
                etrCashPositionFlowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                etrCashPositionFlowDto.setOffsetId(etrEntrust.getEntrustId());
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }
    }

    /**
     * 质押式回购_委托部分回冲
     *
     * @param entrustCommonDto
     * @param etrPositionHoldDto
     */
    public static void dealRepoEntrustClose(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        if (entrustCommonDto != null) {
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            EtrEntrustAdd etrEntrustAdd = entrustCommonDto.getEtrEntrustAdd();
            String tradePlace = etrEntrust.getTradeMarket();
            // 正回购 or 逆回购
            String ps = etrEntrust.getFinprodType2();
            if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(ps)) {
                // 委托生成的持仓流水
                // 减少本金金额
                EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
                        entrustCommonDto);
                // 交易所竞价回购 改变标准券持仓
                // H01:质押式协议回购 , H01:质押式竞价回购
                String type6 = entrustCommonDto.getEtrEntrustAdd().getAssetType1();
                if (!(StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_上海证券交易所.getValue())
                        || StringUtil.equals(tradePlace, DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                        && StringUtil.equals(entrustCommonDto.getIsSettleFail(), DictCodeList.是否_是.getValue())) {
                    // 新业务 在交易执行时才占用债券的指令可用持仓 所以强制完成时不用释放
                    // 交收失败时需要释放
                    // 质押品的组合持仓流水
//                    etrPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlowLogic(entrustCommonDto, false));
                    // 减少交收失败的金额
                    if (entrustCommonDto.getDeal() != null
                            && entrustCommonDto.getDeal().getTradeAmt() != null) {
                        etrEntrustAdd.setCloseAmt(ArithmeticUtil.doAdd(etrEntrustAdd.getCloseAmt(), entrustCommonDto.getDeal().getTradeAmt()));
                    }
                }
            }
            else if (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(ps)) {
                // 委托生成的现金流水
                EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                        entrustCommonDto);
                String acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                        DictCodeListExt1.资金账户类型_产品托管户.getValue());
                //获取已完成额度
                BigDecimal closeAmt = etrEntrustAdd.getCloseAmt();
                BigDecimal amount = BigDecimal.ZERO;
                if(entrustCommonDto.isCloseEtr()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("entrustId", etrEntrust.getEntrustId());
                    map.put("portfolioId", etrEntrust.getPortfolioId());
                    map.put("sumTradeFlag", "02");
                    // 指令额度 - 已关闭额度 -已成交额度 - 强制完成额度 = 未使用额度
                    amount = ArithmeticUtil.subtract(ArithmeticUtil.subtract(
                            ArithmeticUtil.subtract(etrEntrust.getEntrustAmt(), etrEntrustAdd.getCloseAmt()),
                            entrustCommonDto.getForcedFinishAmt() != null ? BigDecimal.ZERO : etrEntrustAdd.getForcedFinishAmt()),
                            EtrCommonCashPositionDealService.getEtrFinishAmtMuilt(map));
                }
                // 减少交收失败的金额
                if(entrustCommonDto.getDeal()!=null
                        &&entrustCommonDto.getDeal().getTradeAmt()!=null) {
                    amount = entrustCommonDto.getDeal().getTradeAmt();
                }
                etrCashPositionFlowDto.setAmount(amount);
                etrEntrustAdd.setCloseAmt(ArithmeticUtil.doAdd(closeAmt, etrCashPositionFlowDto.getAmount()));
                etrCashPositionFlowDto.setAcctId(acctId);
                etrCashPositionFlowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                etrCashPositionFlowDto.setOffsetId(etrEntrust.getEntrustId());
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }
    }

    /**
     * 写入质押品的组合持仓流水
     *
     * @param input    画面参数Dto
     * @param flag 是否是关闭委托
     */
    private static List<EtrSecPositionFlowDto> ptlSecPositionFlowLogic(EntrustCommonDto input,boolean flag) {
        // List<组合持仓流水DTO>(若上记的List<回购质押品DTO>为空，则此为空，反之循环构造DTO)
        // 通过上记输出的List<输出的质押品DTO>，循环该List构造组合持仓流水DTO
        List<EtrSecPositionFlowDto> list = new ArrayList<>() ;
        // 查询委托的质押券信息
        Map<String, Object> initSearchParam = new HashMap<>();
        initSearchParam.put("portfolioId", input.getEtrEntrust().getPortfolioId());
        initSearchParam.put("ps",input.getEtrEntrust().getPs());
        initSearchParam.put("entrustId", input.getEtrEntrust().getEntrustId());
        BussRepoSecDto bussRepoSecDto = new BussRepoSecDto() ;
        if(input.getBussRepoSecDto() == null){
            bussRepoSecDto = etrCommonCashPositionDealService.selectBussRepoSec(initSearchParam);
        }else{
            bussRepoSecDto = input.getBussRepoSecDto() ;
        }
        if (!StringUtil.isEmptyList(bussRepoSecDto.getPledgeBondList())) {
            // 可质押券信息（含投资目的）
            Map<String, List<PledgeBondInfoDto>> avaMap = etrCommonCashPositionDealService.selectAvaPledgeWithInvAim(input)
                    .stream().collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
            avaMap.entrySet().stream().forEach(entry -> {
                Collections.sort(entry.getValue(), (o1, o2) -> o1.getInvAimSort().compareTo(o2.getInvAimSort()));
            });
            EtrSecPositionFlowDto etrSecPositionFlowDto;
            for (PledgeBondInfoDto pledgeBond : bussRepoSecDto.getPledgeBondList()) {
                // 待分配投资目的的质押券面总额
                BigDecimal pledgePrin = pledgeBond.getPledgePrin();
                if(avaMap.get(pledgeBond.getBondCode())!=null) {
                    for (PledgeBondInfoDto avaBond : avaMap.get(pledgeBond.getBondCode())) {
                        etrSecPositionFlowDto = new EtrSecPositionFlowDto();
                        // 当前投资目的可分配券面总额
                        BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                        // 当前投资目的决定分配的券面总额
                        BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

                        etrSecPositionFlowDto.setSeqno(getSeqNo());
                        // 组合代码 = 画面.组合代码
                        etrSecPositionFlowDto.setPortfolioId(input.getEtrEntrust().getPortfolioId());
                        // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
                        String secAcctId = input.getEtrEntrustAdd().getSecAcctId();
                        if (StringUtil.isEmpty(secAcctId) && StringUtil.isEmpty(input.getEtrEntrust().getChlId())) {
                            secAcctId = avaBond.getSecAcctId();
                        } else if (StringUtil.isNotEmpty(input.getEtrEntrust().getChlId())) {
                            secAcctId = input.getEtrEntrust().getChlId();
                        }
                        etrSecPositionFlowDto.setSecAcctId(secAcctId);
                        // 金融产品代码 = 输出质押品DTO.金融产品代码
                        etrSecPositionFlowDto.setFinprodId(pledgeBond.getFinprodId());
                        // 分支序号 = 系统固定参数，默认为0
                        etrSecPositionFlowDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                        // 持仓类型
                        etrSecPositionFlowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                        // 日期 = 金融产品交易Entity.交易日
                        etrSecPositionFlowDto.setCdate(input.getEtrEntrust().getVdate());
                        // 数量 = 输出质押品DTO.质押数量
                        BigDecimal amt = BigDecimal.ZERO;
                        if (flag) { // 委托开始减少,委托关闭增加
                            amt = ArithmeticUtil.divide(prin, new BigDecimal("100"), 4).negate();
                        } else {
                            amt = ArithmeticUtil.divide(prin, new BigDecimal("100"), 4);
                        }
                        etrSecPositionFlowDto.setAmount(amt);
                        // 币种 = 金融产品交易Entity.币种
                        etrSecPositionFlowDto.setCcy(input.getEtrEntrust().getCcy());
                        // 交易编号 = 金融产品交易Entity.交易编号
                        etrSecPositionFlowDto.setTradeId(input.getEtrEntrust().getEntrustId());
                        // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                        etrSecPositionFlowDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

                        if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
//                            if (StringUtil.isNotEmpty(input.getActPortfolioId()) && !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
//                                PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
//                                BeanUtil.copyPropertiesIgnoreNull(subResultDto, etrSecPositionFlowDto);
//                                subResultDto.setPortfolioId(input.getActPortfolioId());
//                                lstPtlSecPositionFlowDto.add(subResultDto);
//                            }
                            list.add(etrSecPositionFlowDto);
                        }
                        // 分配完毕后,停止可质押券信息（含投资目的）的循环
                        pledgePrin = pledgePrin.subtract(prin);
                        if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
                            break;
                        }
                    }
                }
            }
        }
        return list ;
    }

    private static void 生成交易持仓2(BigDecimal prinAmt, EtrPositionHoldDto etrPositionHoldDto, EtrSecPositionFlowDto etrSecPositionFlowDto, String finprodId) {
        EtrSecPositionFlowDto etrTmp = new EtrSecPositionFlowDto();
        BeanUtil.copyPropertiesIgnoreNull(etrTmp,etrSecPositionFlowDto);
        etrTmp.setAmount(prinAmt);
        etrTmp.setSeqno(getSeqNo());
        etrTmp.setFinprodId(finprodId);
        // 交易本身的持仓
        etrPositionHoldDto.getPtlSecPositionFlows().add(etrTmp);
    }
}
/*
 * 文件名：AutoPeriodPlanInterestCommonService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：etliu
 * 修改时间：2018年12月17日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.autoperiod.planinterest;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTrade;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.period.trdplanintduration.TrdPlanInterestDurationDto;
import com.joyintech.tams.invest.common.dto.period.trdprojduration.TrdProjectDurationDto;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计划类资产利率型存续期共通Service 批处理自动存续期和存续期画面共用逻辑
 *
 * @author etliu
 * @version 1.0 2018年12月17日
 * @see AutoPeriodPlanInterestCommonService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class AutoPeriodPlanInterestCommonService extends BaseService {

    /**
     * AutoPeriodCommonService
     */
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;

    /**
     * 以组合维度生成dto，就是以利息明细维度组装新的dto，作为属性保存到流程entity中<br/>
     * 汇总 <br/>
     * 汇总扩展 <br/>
     * 汇总费用明细 <br/>
     * List<Dto> | 交易 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 附属 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 扩展 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 费用明细 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 组合交易 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| List<划款指令>
     */
    public BaseTotalTradeInfoDto genFinProductTradeDto(TrdPlanInterestDurationDto entity) {
        BaseTotalTradeInfoDto result = new BaseTotalTradeInfoDto();
        // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        TrdFinProductDealTotal trdFinProductDealTotal = gen汇总流程表(entity);
        result.setTrdFinProductDealTotal(trdFinProductDealTotal);

        // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = gen汇总扩展业务流程(entity, trdFinProductDealTotal);
        result.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);

        TrdTradeFeeDetail trdTradeFeeDetailTotal = gen汇总交易费用明细(trdFinProductDealTotal, entity);
        result.setTrdTradeFeeDetail(trdTradeFeeDetailTotal);

        List<BaseTradeInfoDto> baseTradeInfoDtoList = new ArrayList<>();
        result.setBaseTradeInfoDtoList(baseTradeInfoDtoList);
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
//       划款指令
        List<BussTrdTransferOrderDto> stlPayOrdersList = autoPeriodCommonService.save(entity.getBussTrdTransferOrder());
        List<FinAssetIntInfo> 利息明细 = entity.getFinAssetIntInfoList();
        利息明细.forEach(finAssetIntInfo -> {
            BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
            baseTradeInfoDtoList.add(baseTradeInfoDto);
            gen利息明细汇总表和扩展表(finAssetIntInfo, entity, baseTradeInfoDto);
            //封装还本不带息的交易
            if(entity.getIsClean()!=null && entity.getPayUnpaidintAmt()!=null && entity.getPayUnpaidintAmt().compareTo(BigDecimal.ZERO)>0
                    && finAssetIntInfo.getActrRepaymentUnpayInterest()!=null && finAssetIntInfo.getActrRepaymentUnpayInterest().compareTo(BigDecimal.ZERO)>0){//
                List<BaseTradeInfoDto> 还本不带息列表=get还本不带息列表明细(entity,finAssetIntInfo,parameter,
                        baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                baseTradeInfoDto.setRepaymentUnpayInterestBaseTradeInfoDtoList(还本不带息列表);
            }

            List<String> cashIds = StringUtil.splitToList(",", finAssetIntInfo.getCashId());
            List<String> cashTypes = StringUtil.splitToList(",", finAssetIntInfo.getCashType());
            new ArrayList<TrdProductDeal>();
//                      生成划款指令
            // 存续期增加通道费 modify by renping 20191018 start
            BigDecimal fPriceAmt = baseTradeInfoDto.getTrdFinProductDealTotal().getFpriceAmt();
            baseTradeInfoDto.getTrdFinProductDealTotal().setFpriceAmt(
                baseTradeInfoDto.getTrdFinProductDealTotal().getTradeAmt());

            // add by 王声之 20220601 start
            // 3.0.6场内 ZGCPCN-1014: 计划类利率型存续期画面，付息或还本或到期，点确认，提交提示系统异常
            for (BussTrdTransferOrderDto tempOrderDto : stlPayOrdersList) {
                tempOrderDto.setInvAim(finAssetIntInfo.getInvAim());
            }
            // add by 王声之 20220601 end

            StlPayOrders stl = autoPeriodCommonService.gen划款指令流程(stlPayOrdersList,
                baseTradeInfoDto.getTrdFinProductDealTotal(), entity.getWorkflowId());
            baseTradeInfoDto.getTrdFinProductDealTotal().setFpriceAmt(fPriceAmt);
            // 存续期增加通道费 modify by renping 20191018 end
            baseTradeInfoDto.setStlPayOrders(stl);
            //            生成子组合利息明细共同方法
            Map<String,Object> subMap = new HashMap<>();
            subMap.put("portfolioId",finAssetIntInfo.getPortfolioId());
//            持仓时间
            subMap.put("dealDate", entity.getmDate());
//            资产代码
            subMap.put("finprodMarketId",entity.getFinprodMarketId());
//            金融产品代码
            subMap.put("finprodId",entity.getFinprodId());
            Map<String,String> defaultMap = new HashMap<>();
//            自定义不累加
            defaultMap.put("yield",finAssetIntInfo.getYield().toString());
            subMap.put("defaultMap",JsonUtils.object2Json(defaultMap));
////            汇总交易编号
//            subMap.put("totleTradeId",entity.getTotleTradeId());
            Map<String, BigDecimal> preMap = new HashMap<>();
            preMap.put("returnAmount",finAssetIntInfo.getReturnAmount());
            preMap.put("theoryAmt",finAssetIntInfo.getTheoryAmt());
            preMap.put("fpriceAmt",finAssetIntInfo.getFpriceAmt());
            preMap.put("taxAmount",finAssetIntInfo.getTaxAmount());
            preMap.put("qty",finAssetIntInfo.getQty());
            // 存续期增加通道费 add by renping 20191018 start
            if (finAssetIntInfo.getChannelFee() != null) {
                preMap.put("channelFee", finAssetIntInfo.getChannelFee());
            }
            if(finAssetIntInfo.getRepaymentUnpayInterest()!=null){
                preMap.put("repaymentUnpayInterest",finAssetIntInfo.getRepaymentUnpayInterest());}//理论还本不带息
            if(finAssetIntInfo.getActrRepaymentUnpayInterest()!=null){
                preMap.put("actrRepaymentUnpayInterest",finAssetIntInfo.getActrRepaymentUnpayInterest());}//实际还本不带息
            // 存续期增加通道费 add by renping 20191018 end
//            划款指令结算金额
            if(stl != null){
                preMap.put("settleAmt",stl.getSettleAmt());
            }
            subMap.put("preMap", JsonUtils.object2Json(preMap));
            subMap.put(GlobalConstant.WORKFLOW_ID,entity.getWorkflowId());
            List<FinAssetIntInfo> 子组合利息明细 = autoPeriodCommonService.distributeDataByPercent(subMap);
//            子组合交易
            List<BaseTradeInfoDto> subBaseTradeInfoList = new ArrayList<>();
            baseTradeInfoDto.setBaseTradeInfoDtoList(subBaseTradeInfoList);
            for (int i = 0; i < cashIds.size(); i++ ) {
                String ps = AutoPeriodCommonService.change现金流类型To交易方向(cashTypes.get(i));

                if (DictCodeList.交易方向_收息.getValue().equals(ps)) {
                    TrdProductDeal deal收息 = 生成收息交易(entity, paTrdFinProductDealTotal, parameter, finAssetIntInfo, baseTradeInfoDto,
                        cashIds, i);
                    // 存续期增加通道费 add by renping 20191018 start
                    setChannelFeeDetail(entity, finAssetIntInfo, baseTradeInfoDto, deal收息);
                    // 存续期增加通道费 add by renping 20191018 end
                    baseTradeInfoDto.setPtlTrade(getPtlTrade(entity, finAssetIntInfo, deal收息));
                }
                else {
                    TrdProductDeal deal = getWflTrdProductDeal(entity, parameter, finAssetIntInfo,
                        DictCodeList.交易方向_还本.getValue().equals(ps) ? DictCodeList.交易方向_还本 : DictCodeList.交易方向_到期,
                        baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId(), cashIds.get(i));
                    BaseTrade baseTrade = new BaseTrade();
                    // 存续期增加通道费 add by renping 20191018 start
                    if (cashIds.size() == 1
                        && (finAssetIntInfo.getIntAmt() == null
                            || finAssetIntInfo.getIntAmt().compareTo(BigDecimal.ZERO) == 0)) {
                        setChannelFeeDetail(entity, finAssetIntInfo, baseTradeInfoDto, deal);
                    }
                    // 存续期增加通道费 add by renping 20191018 end
                    baseTrade.setTrdProductDeal(deal);
                    baseTrade.setPaTrdProductDeal(getPaTrdProductDeal(finAssetIntInfo, deal, entity.getCashType().contains(
                        DictCodeList.现金流类型_中间还本.getValue()) ? DictCodeListExt1.平安业务类型_计划类资产_利率型_中间还本 : DictCodeListExt1.平安业务类型_计划类资产_利率型_到期));
                    baseTrade.setTrdProductDealAdd(getTrdProductDealAdd(entity, finAssetIntInfo, deal));
                    baseTrade.setPtlTrade(getPtlTrade(entity, finAssetIntInfo, deal));
                    baseTradeInfoDto.setBaseTrade(baseTrade);

                    if (cashIds.size() == 1 && finAssetIntInfo.getFpriceAmt().compareTo(finAssetIntInfo.getReturnAmount()) > 0) {
                        TrdProductDeal deal收息 = 生成收息交易(entity, paTrdFinProductDealTotal, parameter, finAssetIntInfo, baseTradeInfoDto, cashIds, i);
                        // 存续期增加通道费 add by renping 20191018 start
                        setChannelFeeDetail(entity, finAssetIntInfo, baseTradeInfoDto, deal收息);
                        // 存续期增加通道费 add by renping 20191018 end
                    }
                }
                int temp = i;
                子组合利息明细.forEach(subFinAssetIntInfo->{
                    StlPayOrders subStl = new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(subStl,stl);
                    subFinAssetIntInfo.setPortfolioId(subFinAssetIntInfo.getSubPortfolioId());
                    subStl.setSettleAmt(subFinAssetIntInfo.getSettleAmt());
                    subStl.setPortfolioId(subFinAssetIntInfo.getSubPortfolioId());
//                    子组合交易dto
                    BaseTradeInfoDto subBaseTradeInfoDto = new BaseTradeInfoDto();
                    //                        设置划款指令
                    subBaseTradeInfoDto.setStlPayOrders(subStl);
//                    子组合交易dto放入list
                    subBaseTradeInfoList.add(subBaseTradeInfoDto);
                    if (DictCodeList.交易方向_收息.getValue().equals(ps)) {
                        TrdProductDeal deal收息 = 生成子组合收息交易(entity, parameter, subFinAssetIntInfo, subBaseTradeInfoDto,
                                cashIds, temp,baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                        // 存续期增加通道费 add by renping 20191018 start
                        setChannelFeeDetailSub(entity, subFinAssetIntInfo, subBaseTradeInfoDto,
                            deal收息);
                        // 存续期增加通道费 add by renping 20191018 end
                        subBaseTradeInfoDto.setPtlTrade(getPtlTrade(entity, subFinAssetIntInfo, deal收息));
                    }
                    else {
                        TrdProductDeal deal = getWflTrdProductDeal(entity, parameter, subFinAssetIntInfo,
                                DictCodeList.交易方向_还本.getValue().equals(ps) ? DictCodeList.交易方向_还本 : DictCodeList.交易方向_到期,
                                baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId(), cashIds.get(temp));
                        // 存续期增加通道费 add by renping 20191018 start
                        if (cashIds.size() == 1 && (subFinAssetIntInfo.getIntAmt() == null
                                                    || subFinAssetIntInfo.getIntAmt().compareTo(
                                                        BigDecimal.ZERO) == 0)) {
                            setChannelFeeDetailSub(entity, subFinAssetIntInfo, subBaseTradeInfoDto,
                                deal);
                        }
                        // 存续期增加通道费 add by renping 20191018 end
                        BaseTrade baseTrade = new BaseTrade();
                        baseTrade.setTrdProductDeal(deal);
                        baseTrade.setPaTrdProductDeal(getPaTrdProductDeal(subFinAssetIntInfo, deal, entity.getCashType().contains(
                                DictCodeList.现金流类型_中间还本.getValue()) ? DictCodeListExt1.平安业务类型_计划类资产_利率型_中间还本 : DictCodeListExt1.平安业务类型_计划类资产_利率型_到期));
                        baseTrade.setTrdProductDealAdd(getTrdProductDealAdd(entity, subFinAssetIntInfo, deal));
                        baseTrade.setPtlTrade(getPtlTrade(entity, subFinAssetIntInfo, deal));
                        subBaseTradeInfoDto.setBaseTrade(baseTrade);
                        if (cashIds.size() == 1 && subFinAssetIntInfo.getFpriceAmt().compareTo(subFinAssetIntInfo.getReturnAmount()) > 0) {
                            // 存续期增加通道费 modify by renping 20191018 start
                            TrdProductDeal deal2 = 生成子组合收息交易(entity, parameter, subFinAssetIntInfo,
                                subBaseTradeInfoDto, cashIds, temp,
                                baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                            setChannelFeeDetailSub(entity, subFinAssetIntInfo, subBaseTradeInfoDto,
                                deal2);
                            // 存续期增加通道费 modify by renping 20191018 end
                        }
                    }
                    if(  baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList()!=null){
                        //子组合封装还本不带息
                        List<BaseTradeInfoDto> 子组合还本不带息列表=get还本不带息列表明细(entity,subFinAssetIntInfo,parameter,baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                        subBaseTradeInfoDto.setRepaymentUnpayInterestBaseTradeInfoDtoList(子组合还本不带息列表);
                    }

                });
            }
        });
        return result;

    }

    // 存续期增加通道费 add by renping 20191018 start
    /**
     * 设置子组合通道费明细信息
     * 
     * @param entity 参数
     * @param subFinAssetIntInfo
     * @param subBaseTradeInfoDto
     * @param deal
     * @see
     * @since 1.0
     */
    private void setChannelFeeDetailSub(TrdPlanInterestDurationDto entity,
                                        FinAssetIntInfo subFinAssetIntInfo,
                                        BaseTradeInfoDto subBaseTradeInfoDto,
                                        TrdProductDeal deal) {
        if (entity.getChannelFee() != null) {
            deal.setFeeAmt(
                subFinAssetIntInfo.getChannelFee() == null ? BigDecimal.ZERO : subFinAssetIntInfo.getChannelFee());
            TrdTradeFeeDetail detail = new TrdTradeFeeDetail();
            List<TrdTradeFeeDetail> detailList = new ArrayList<TrdTradeFeeDetail>();
            detail.setTradeId(deal.getTradeId());
            detail.setFeeAmt(subFinAssetIntInfo.getChannelFee());
            detail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            detail.setCcy(deal.getCcy());
//            if (entity.isChannel() && StringUtil.equals(entity.getCollectType(),
//                DictCodeListExt2.收取方式_回款前扣除.getValue())) {
//                detail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
//                deal.setFeeAmtPay(
//                    subFinAssetIntInfo.getChannelFee() == null ? BigDecimal.ZERO : subFinAssetIntInfo.getChannelFee());
//                deal.setTradeAmt(ArithmeticUtil.subtract(deal.getTradeAmt(),
//                    subFinAssetIntInfo.getChannelFee()));
//            }
//            else {
                detail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                deal.setFeeAmtUnpay(
                    subFinAssetIntInfo.getChannelFee() == null ? BigDecimal.ZERO : subFinAssetIntInfo.getChannelFee());
//            }
            detail.setFeeId(DictCodeListExt1.一次性费用类型_通道费.getValue());
            detail.setChlFinprodId(entity.getChlId());
            detailList.add(detail);
            subBaseTradeInfoDto.setTrdTradeFeeDetail(detailList);
        }
    }

    /**
     * 设置通道费明细信息
     * 
     * @param entity 参数
     * @param finAssetIntInfo
     * @param baseTradeInfoDto
     * @param deal收息
     * @see
     * @since 1.0
     */
    private void setChannelFeeDetail(TrdPlanInterestDurationDto entity,
                                     FinAssetIntInfo finAssetIntInfo,
                                     BaseTradeInfoDto baseTradeInfoDto, TrdProductDeal deal收息) {
        if (entity.getChannelFee() != null) {
            deal收息.setFeeAmt(
                finAssetIntInfo.getChannelFee() == null ? BigDecimal.ZERO : finAssetIntInfo.getChannelFee());
            TrdTradeFeeDetail detail = new TrdTradeFeeDetail();
            List<TrdTradeFeeDetail> detailList = new ArrayList<TrdTradeFeeDetail>();
            detail.setTradeId(deal收息.getTradeId());
            detail.setFeeAmt(finAssetIntInfo.getChannelFee());
            detail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            detail.setCcy(deal收息.getCcy());
//            if (entity.isChannel() && StringUtil.equals(entity.getCollectType(),
//                DictCodeListExt2.收取方式_回款前扣除.getValue())) {
//                detail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
//                deal收息.setFeeAmtPay(
//                    finAssetIntInfo.getChannelFee() == null ? BigDecimal.ZERO : finAssetIntInfo.getChannelFee());
//                deal收息.setTradeAmt(ArithmeticUtil.subtract(deal收息.getTradeAmt(),
//                    finAssetIntInfo.getChannelFee()));
//            }
//            else {
                detail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                deal收息.setFeeAmtUnpay(
                    finAssetIntInfo.getChannelFee() == null ? BigDecimal.ZERO : finAssetIntInfo.getChannelFee());
//            }
            detail.setFeeId(DictCodeListExt1.一次性费用类型_通道费.getValue());
            detail.setChlFinprodId(entity.getChlId());
            detailList.add(detail);
            baseTradeInfoDto.setTrdTradeFeeDetail(detailList);
        }
    }
    // 存续期增加通道费 add by renping 20191018 end

    private TrdProductDeal 生成收息交易(TrdPlanInterestDurationDto entity, PaTrdFinProductDealTotal paTrdFinProductDealTotal,
                                  BussinessCodeParameter parameter, FinAssetIntInfo finAssetIntInfo,
                                  BaseTradeInfoDto baseTradeInfoDto, List<String> cashIds, int i) {
        TrdProductDeal deal收息 = getWflTrdProductDeal(entity, parameter, finAssetIntInfo, DictCodeList.交易方向_收息,
            baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId(), cashIds.get(i));
        baseTradeInfoDto.setTrdProductDeal(deal收息);

        baseTradeInfoDto.setPaTrdProductDeal(
            getPaTrdProductDeal(finAssetIntInfo, deal收息, DictCodeListExt1.平安业务类型_计划类资产_利率型_中间付息));
        baseTradeInfoDto.setTrdProductDealAdd(getTrdProductDealAdd(entity, finAssetIntInfo, deal收息));
        if(paTrdFinProductDealTotal != null){
            paTrdFinProductDealTotal.setRate(baseTradeInfoDto.getPaTrdProductDeal().getTheoryRate());
        }
        return deal收息;
    }

    /**
     * 生成子组合收息交易
     * @param entity 画面entity
     * @param parameter 业务编号生成参数
     * @param subFinAssetIntInfo
     * @param baseTradeInfoDto
     * @param cashIds
     * @param i
     * @param totleTradeId
     * @return
     */
    private TrdProductDeal 生成子组合收息交易(TrdPlanInterestDurationDto entity,
                                  BussinessCodeParameter parameter, FinAssetIntInfo subFinAssetIntInfo,
                                  BaseTradeInfoDto baseTradeInfoDto, List<String> cashIds, int i,String totleTradeId) {
        TrdProductDeal deal收息 = getWflTrdProductDeal(entity, parameter, subFinAssetIntInfo, DictCodeList.交易方向_收息,
                totleTradeId, cashIds.get(i));
        baseTradeInfoDto.setTrdProductDeal(deal收息);
        baseTradeInfoDto.setPaTrdProductDeal(
                getPaTrdProductDeal(subFinAssetIntInfo, deal收息, DictCodeListExt1.平安业务类型_计划类资产_利率型_中间付息));
        baseTradeInfoDto.setTrdProductDealAdd(getTrdProductDealAdd(entity, subFinAssetIntInfo, deal收息));
        return deal收息;
    }
    /**
     * 登录流程中数据
     *
     * @param baseTotalTradeInfoDto 流程中数据
     * @see
     * @since 1.0
     */
    public void insertWflData(BaseTotalTradeInfoDto baseTotalTradeInfoDto) {
        if (baseTotalTradeInfoDto == null) {
            return;
        }

        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doInsertBaseTradeInfo",
            baseTotalTradeInfoDto, String.class);
    }

    private static TrdFinProductDealTotal gen汇总流程表(TrdPlanInterestDurationDto entity) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        // trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setPs(AutoPeriodCommonService.change现金流类型To交易方向(entity.getCashType()));
        trdFinProductDealTotal.setBranch(
            AutoPeriodCommonService.changePsToBranch(entity.getmDate(), trdFinProductDealTotal.getPs()));
        trdFinProductDealTotal.setTradeDate(DateTime.parse(entity.getArrivalDate()).toDate());
        trdFinProductDealTotal.setVdate(DateTime.parse(entity.getvDate()).toDate());
        trdFinProductDealTotal.setMdate(DateTime.parse(entity.getmDate()).toDate());
        trdFinProductDealTotal.setSettleDate(DateTime.parse(entity.getArrivalDate()).toDate());
        trdFinProductDealTotal.setShareAmt(entity.getReturnAmount());
        trdFinProductDealTotal.setPrinAmt(entity.getReturnAmount());
        trdFinProductDealTotal.setIntAmt(ArithmeticUtil.subtract(entity.getFpriceAmt(), entity.getReturnAmount()));
        trdFinProductDealTotal.setFpriceAmt(entity.getFpriceAmt());
        // 存续期增加通道费 add by renping 20191018 start
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        if (entity.getChannelFee() != null) {
            trdFinProductDealTotal.setFeeAmt(entity.getChannelFee());
//            if (entity.isChannel() && StringUtil.equals(entity.getCollectType(),
//                DictCodeListExt2.收取方式_回款前扣除.getValue())) {
//                trdFinProductDealTotal.setFeeAmtPay(
//                    entity.getChannelFee() == null ? BigDecimal.ZERO : entity.getChannelFee());
//            }
//            else {
                trdFinProductDealTotal.setFeeAmtUnpay(
                    entity.getChannelFee() == null ? BigDecimal.ZERO : entity.getChannelFee());
//            }
        }
        // 存续期增加通道费 add by renping 20191018 end
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setIsClean(entity.getIsClean());//是否结清
        trdFinProductDealTotal.setPayUnpaidintAmt(entity.getPayUnpaidintAmt());//累计还本未付利息
        trdFinProductDealTotal.setChlId(entity.getChlId());
        trdFinProductDealTotal.setRemark(entity.getRemark());
        return trdFinProductDealTotal;
    }

    private static PaTrdFinProductDealTotal gen汇总扩展业务流程(TrdPlanInterestDurationDto entity,
                                                        TrdFinProductDealTotal trdFinProductDealTotal) {
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        paTrdFinProductDealTotal.setTheoryPayDate(DateTime.parse(entity.getInterestPayDay()).toDate());
        paTrdFinProductDealTotal.setTheoryInt(entity.getIntAmtTheory());
        paTrdFinProductDealTotal.setTheoryPrin(entity.getReturnAmount());
        paTrdFinProductDealTotal.setRemainPrinAmt(entity.getQty());
        paTrdFinProductDealTotal.setRate(ArithmeticUtil.divide(entity.getYield(), 100, 5));
        paTrdFinProductDealTotal.setPaBussType(entity.getPaBussType());
        paTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        return paTrdFinProductDealTotal;
    }

    private TrdTradeFeeDetail gen汇总交易费用明细(TrdFinProductDealTotal trdFinProductDealTotal, TrdPlanInterestDurationDto entity) {
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        trdTradeFeeDetail.setFeeAmt(entity.getTaxAmount());
        trdTradeFeeDetail.setTradeId(entity.getBusiCode());
        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_投管_税费.getValue()); // 一次性费用类型
        return trdTradeFeeDetail;
    }

    private void gen利息明细汇总表和扩展表(FinAssetIntInfo finAssetIntInfo, TrdPlanInterestDurationDto entity,
                                BaseTradeInfoDto baseTradeInfoDto) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(
            autoPeriodCommonService.buildBussinessCode(new BussinessCodeParameter(DictCodeList.共通业务类型_资产存续期交易)));
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        // trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        trdFinProductDealTotal.setCcy(entity.getFinAssetInfo().getCcy());
        trdFinProductDealTotal.setPs(AutoPeriodCommonService.change现金流类型To交易方向(entity.getCashType()));
        trdFinProductDealTotal.setTradeDate(DateTime.parse(entity.getArrivalDate()).toDate());
        trdFinProductDealTotal.setVdate(DateTime.parse(finAssetIntInfo.getvDate()).toDate());
        trdFinProductDealTotal.setMdate(DateTime.parse(finAssetIntInfo.getmDate()).toDate());
        trdFinProductDealTotal.setSettleDate(DateTime.parse(entity.getArrivalDate()).toDate());
        trdFinProductDealTotal.setShareAmt(finAssetIntInfo.getReturnAmount());
        trdFinProductDealTotal.setPrinAmt(finAssetIntInfo.getReturnAmount());
        trdFinProductDealTotal.setPortfolioId(finAssetIntInfo.getPortfolioId());
        trdFinProductDealTotal.setIntAmt(
            ArithmeticUtil.subtract(finAssetIntInfo.getFpriceAmt(), finAssetIntInfo.getReturnAmount()));
        trdFinProductDealTotal.setFpriceAmt(finAssetIntInfo.getFpriceAmt());
        trdFinProductDealTotal.setTradeAmt(finAssetIntInfo.getTradeAmt());
        // 存续期增加通道费 add by renping 20191018 start
        if (entity.getChannelFee() != null) {
        	trdFinProductDealTotal.setTradeAmt(
                    ArithmeticUtil.subtract(finAssetIntInfo.getTradeAmt(), finAssetIntInfo.getTaxAmount()));
//            if (entity.isChannel() && finAssetIntInfo.getTradeAmt() != null && StringUtil.equals(
//                entity.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())) {
//                trdFinProductDealTotal.setFeeAmtPay(
//                    finAssetIntInfo.getChannelFee() == null ? BigDecimal.ZERO : finAssetIntInfo.getChannelFee());
//            }
//            else {
                trdFinProductDealTotal.setFeeAmtUnpay(
                    finAssetIntInfo.getChannelFee() == null ? BigDecimal.ZERO : finAssetIntInfo.getChannelFee());
//            }
            trdFinProductDealTotal.setFeeAmt(
                finAssetIntInfo.getChannelFee() == null ? BigDecimal.ZERO : finAssetIntInfo.getChannelFee());

        }
        // 存续期增加通道费 add by renping 20191018 end
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setIsClean(entity.getIsClean());
        trdFinProductDealTotal.setPayUnpaidintAmt(finAssetIntInfo.getActrRepaymentUnpayInterest());//每个组合的区间实际的未付利息
        trdFinProductDealTotal.setpTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setChlId(entity.getChlId());
        //添加投资目的赋值 add by yzg 20210528 start
        trdFinProductDealTotal.setInvAim(finAssetIntInfo.getInvAim());
        //添加投资目的赋值 add by yzg 20210528 end

        baseTradeInfoDto.setTrdFinProductDealTotal(trdFinProductDealTotal);

        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        paTrdFinProductDealTotal.setTheoryPayDate(DateTime.parse(entity.getInterestPayDay()).toDate());
        paTrdFinProductDealTotal.setRate(ArithmeticUtil.divide(entity.getYield(), 100, 5));
        paTrdFinProductDealTotal.setTheoryAmt(finAssetIntInfo.getTheoryAmt());
//        持有金额
        paTrdFinProductDealTotal.setRemainPrinAmt(finAssetIntInfo.getQty());
        paTrdFinProductDealTotal.setPaBussType(entity.getPaBussType());
        paTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        baseTradeInfoDto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
    }

    private TrdProductDeal getWflTrdProductDeal(TrdPlanInterestDurationDto entity, BussinessCodeParameter parameter,
                                                FinAssetIntInfo finAssetIntInfo, DictCodeList 交易方向, String totleTradeId,
                                                String cashId) {
        TrdProductDeal deal = new TrdProductDeal();
        deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
        deal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue());
        deal.setFinprodId(entity.getFinprodId());
        deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        // deal.setFinprodType(); TODO 估值核算 金融产品类型
        deal.setCcy(entity.getFinAssetInfo().getCcy());
        deal.setTradeDate(DateTime.parse(entity.getArrivalDate()).toDate());
        deal.setVdate(DateTime.parse(entity.getvDate()).toDate());
        deal.setMdate(DateTime.parse(entity.getmDate()).toDate());
        deal.setSettleDate(deal.getTradeDate());
        deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        deal.setIsCancel(DictCodeList.是否_否.getValue());
        deal.setWorkflowId(entity.getWorkflowId());
        deal.setTotleTradeId(totleTradeId);
        deal.setStatus(交易方向.getValue());
        deal.setBranch(AutoPeriodCommonService.changePsToBranch(entity.getmDate(), deal.getPs()));
        deal.setCashId(cashId);
        //增加投资目的 add by yzg start
        deal.setInvAim(finAssetIntInfo.getInvAim());
        //增加投资目的 add by yzg end
        deal.setBranch(AutoPeriodCommonService.changePsToBranch(entity.getmDate(), 交易方向.getValue()));
        if (DictCodeList.交易方向_收息 == 交易方向) {
            // 一体化对应etliu start
            // deal.setShareAmt(finAssetIntInfo.getQty());
            deal.setShareAmt(null);
            // 一体化对应etliu end
            BigDecimal actRepaymenAmt=finAssetIntInfo.getActrRepaymentUnpayInterest();//实际区间未付利息
            if(actRepaymenAmt==null){//还本不带息的场景
                actRepaymenAmt=BigDecimal.ZERO;
            }
            BigDecimal amount= ArithmeticUtil.doAdd(finAssetIntInfo.getReturnAmount(),actRepaymenAmt);//本金+还本不带息的利息
            deal.setIntAmt(ArithmeticUtil.subtract(finAssetIntInfo.getFpriceAmt(),amount));

            //deal.setIntAmt(ArithmeticUtil.subtract(finAssetIntInfo.getFpriceAmt(), finAssetIntInfo.getReturnAmount()));
            deal.setFpriceAmt(deal.getIntAmt());
            deal.setTradeAmt(deal.getIntAmt());
            deal.setPs(DictCodeList.交易方向_收息到账.getValue());
        }
        else if (DictCodeList.交易方向_到期 == 交易方向 || DictCodeList.交易方向_还本 == 交易方向) {
            deal.setShareAmt(finAssetIntInfo.getQty());
            deal.setPrinAmt(finAssetIntInfo.getReturnAmount());
            deal.setFpriceAmt(finAssetIntInfo.getReturnAmount());
            deal.setTradeAmt(finAssetIntInfo.getReturnAmount());
            deal.setPs(DictCodeList.交易方向_还本到账.getValue());
        }
        // 存续期增加通道费 modify by renping 20191018 start
        deal.setChlId(entity.getChlId());
        // 存续期增加通道费 modify by renping 20191018 end
        // 一体化对应etliu start
        deal.setParValue(BigDecimal.ONE);
        deal.setIsClean(entity.getIsClean());
        // 一体化对应etliu end

        return deal;
    }

    private PaTrdProductDeal getPaTrdProductDeal(FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal,
                                                 DictCodeListExt1 平安业务类型计划类资产利率型) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(deal.getTradeId());
        paTrdProductDeal.setTheoryRate(ArithmeticUtil.divide(finAssetIntInfo.getYield(), 100, 8));
        // 理论本息总额 - 还本金额
        paTrdProductDeal.setTheoryInt(ArithmeticUtil.subtract(finAssetIntInfo.getTheoryAmt(), deal.getPrinAmt()));
        paTrdProductDeal.setPaBussType(平安业务类型计划类资产利率型.getValue());
        paTrdProductDeal.setWorkflowId(deal.getWorkflowId());
        return paTrdProductDeal;
    }

    private TrdProductDealAdd getTrdProductDealAdd(TrdPlanInterestDurationDto entity, FinAssetIntInfo finAssetIntInfo,
                                                   TrdProductDeal deal) {
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(deal.getTradeId());
        trdProductDealAdd.setPortfolioId(finAssetIntInfo.getPortfolioId());
        // trdProductDealAdd.setProdId(entity.getFinprodId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setWorkflowId(deal.getWorkflowId());
//        丁志鹏 20190924 添加持仓存储 start
        trdProductDealAdd.setRegDateAmt(finAssetIntInfo.getAmount());
//        丁志鹏 20190924 添加持仓存储 end
        return trdProductDealAdd;
    }

    private List<PtlTrade> getPtlTrade(TrdPlanInterestDurationDto entity, FinAssetIntInfo finAssetIntInfo, TrdProductDeal deal收息) {
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue());
        ptlTrade.setSettleDate(DateTime.parse(entity.getArrivalDate()).toDate());
        ptlTrade.setPortfolioId(finAssetIntInfo.getPortfolioId());
        ptlTrade.setBusiNo(deal收息.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setWorkflowId(entity.getWorkflowId());
        ptlTrades.add(ptlTrade);
        return ptlTrades;
    }

    public List<BaseTradeInfoDto> get还本不带息列表明细(TrdPlanInterestDurationDto entity, FinAssetIntInfo finAssetIntInfo,
                                               BussinessCodeParameter parameter,String totleTradeId){
        String isClean=entity.getIsClean();//是否结清
        List<BaseTradeInfoDto> baseTradeInfoDtoList =new ArrayList<>();
        // for(FinAssetIntInfo intInfo:entity.getFinAssetIntInfoList()){
        List<BaseTradeInfoDto> baseTradeInfoList =new ArrayList<>();
 //       if(finAssetIntInfo.getActrRepaymentUnpayInterest()!=null && finAssetIntInfo.getActrRepaymentUnpayInterest().compareTo
        //       (BigDecimal.ZERO)>0) {
            //根据利息明细查询应收款
            Map<String, Object> param = new HashMap<>();
            param.put("chlId", entity.getChlId());//通道代码
            param.put("finprodId", entity.getFinprodId());//资产代码
            param.put("portfolioId", finAssetIntInfo.getPortfolioId());//组合代码
            //增加投资目的 add by yzg 20201022 start
            param.put("INV_AIM", finAssetIntInfo.getInvAim());
            //增加投资目的 add by yzg 20201022 end
            List<PtlRecePosition> recePositionList = basePtlRecePositionService.getRepayAmtList(param);
            baseTradeInfoList = getPtlReceFlow(entity, recePositionList, finAssetIntInfo, entity.getIsClean(), totleTradeId, parameter);//获取每个组合的还本不带息列表
 //       }
        baseTradeInfoDtoList.addAll(baseTradeInfoList);
        //  }
        return baseTradeInfoDtoList;
    }

    private List<BaseTradeInfoDto>  getPtlReceFlow(TrdPlanInterestDurationDto entity,List<PtlRecePosition> list,
                                                   FinAssetIntInfo finAssetIntInfo,String isClean,
                                                   String totaltradeId,BussinessCodeParameter parameter){
        List<BaseTradeInfoDto> baseTradeInfoDtoList=new ArrayList<>();
        if(isClean!=null && isClean.equals(DictCodeList.是否_否.getValue()) && list.size()>0){
            //【变量】数量=当前组合的区间未付利息/当前组合的理论本息总额*当前组合的实收本息总额*-1
            BigDecimal sumbranchAmt=BigDecimal.ZERO;
            for(int i=0;i<list.size();i++){
                if(i==list.size()-1) {//最后一笔倒减
                    BigDecimal amt=finAssetIntInfo.getActrRepaymentUnpayInterest().subtract(sumbranchAmt);//实收区间未付利息总额-区间未付利息
                    list.get(i).setAmount(amt);
                }else {
                    BigDecimal branchAmt=BigDecimal.ZERO;
                    PtlRecePosition recePosition = list.get(i);//获取组合多个branch
                    BigDecimal amount = recePosition.getAmount();
                    branchAmt= amount.multiply(finAssetIntInfo.getActrRepaymentUnpayInterest()).divide(finAssetIntInfo.getRepaymentUnpayInterest()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    sumbranchAmt=sumbranchAmt.add(branchAmt);
                    list.get(i).setAmount(branchAmt);
                }
            }
        }

        for(PtlRecePosition recePosition:list) {
            BaseTradeInfoDto tradeInfoDto=new BaseTradeInfoDto();
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            trdProductDeal= getWflTrdProductDeal(entity,parameter,finAssetIntInfo,DictCodeList.交易方向_收息,totaltradeId,null);
            BigDecimal tradeAmt=recePosition.getAmount();
            trdProductDeal.setFpriceAmt(tradeAmt);
            trdProductDeal.setTradeAmt(tradeAmt);//应收款流水金额
            trdProductDeal.setShareAmt(tradeAmt);
            trdProductDeal.setUnitInt(null);//单位利息
            trdProductDeal.setCpriceAmt(null);//净价总额
            trdProductDeal.setPrinAmt(BigDecimal.ZERO);
            trdProductDeal.setIntAmt(tradeAmt);
            trdProductDeal.setFeeAmt(null);
            trdProductDeal.setFeeAmtPay(null);
            trdProductDeal.setFeeAmtUnpay(null);
            trdProductDeal.setPs(DictCodeList.交易方向_应收款项转出.getValue());
            trdProductDeal.setBranch(recePosition.getBranch());//应收款流水BRANCH
            trdProductDeal.setIsClean(isClean);//是否结清
            //增加投资目的 add by yzg start 
            trdProductDeal.setInvAim(finAssetIntInfo.getInvAim());
            //增加投资目的 add by yzg end 
            tradeInfoDto.setTrdProductDeal(trdProductDeal);

            TrdProductDealAdd addDto = new TrdProductDealAdd();
            addDto.setTradeId(trdProductDeal.getTradeId());
            addDto.setPortfolioId(finAssetIntInfo.getPortfolioId());
            addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
            tradeInfoDto.setTrdProductDealAdd(addDto);

            List<PtlTrade> ptlTrades = new ArrayList<>();
            PtlTrade ptlTrade = new PtlTrade();
            ptlTrade.setPortfolioId(finAssetIntInfo.getPortfolioId());
            ptlTrade.setBusiNo(trdProductDeal.getTradeId());
            ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade.setBusiId(finAssetIntInfo.getFinprodId());
            ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue());
            ptlTrade.setSettleDate(trdProductDeal.getTradeDate());
            ptlTrades.add(ptlTrade);
            tradeInfoDto.setPtlTrade(ptlTrades);

            PaTrdProductDeal paTrdProductDeal= new PaTrdProductDeal();
            paTrdProductDeal=getPaTrdProductDeal(finAssetIntInfo, trdProductDeal, DictCodeListExt1.平安业务类型_计划类资产_利率型_中间付息);
            tradeInfoDto.setPaTrdProductDeal(paTrdProductDeal);
            baseTradeInfoDtoList.add(tradeInfoDto);
        }

        return baseTradeInfoDtoList;
    }
}

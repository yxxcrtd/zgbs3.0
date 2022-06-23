/*
 * 文件名：AutoPeriodDepoCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年12月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.autoperiod.depo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTrade;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trddepoduration.TrdDepoDurationCommonDto;
import com.joyintech.tams.invest.common.dto.period.trdprojduration.TrdProjectDurationDto;

/**
 * 存款类资产存续期共通Service 批处理自动存续期和存续期画面共用逻辑
 *
 * @author etliu
 * @version 1.0 2018年12月17日
 * @see AutoPeriodDepoCommonService
 * @since 1.0
 */
@Service
public class AutoPeriodDepoCommonService {

    /**
     * AutoPeriodCommonService
     */
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    /**
     * 汇总业务流程 <功能详细描述>
     *
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public static WflTrdFinProductDealTotal gen汇总流程表(TrdDepoDurationCommonDto entity) {
        WflTrdFinProductDealTotal trdFinProductDealTotal = new WflTrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        // 估值核算 金融产品类型
        // trdFinProductDealTotal.setFinprodType();
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        // trdFinProductDealTotal.setCashId(entity.getCashId());
        // 画面.类型
        trdFinProductDealTotal.setPs(entity.getPs());
        // 资金到账日
        trdFinProductDealTotal.setTradeDate(entity.getTradeDate());
        trdFinProductDealTotal.setVdate(entity.getVdateProd());
        trdFinProductDealTotal.setMdate(entity.getMdateProd());
        trdFinProductDealTotal.setSettleDate(entity.getTradeDate());
        // 存续期增加通道费 modify by renping 20191018 start
        trdFinProductDealTotal.setFpriceAmt(entity.getTradeAmtReal());
        // 画面.实收本息总额-画面.实收利息总额
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
        // 画面.实收本息总额-画面.实收利息总额
        trdFinProductDealTotal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
        trdFinProductDealTotal.setFeeAmt(entity.getChannelFee());
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), entity.getCollectType())) {
                trdFinProductDealTotal.setFeeAmtPay(entity.getChannelFee());
            }
            else {
                trdFinProductDealTotal.setFeeAmtUnpay(entity.getChannelFee());
            }
        }else{
        	trdFinProductDealTotal.setFeeAmtUnpay(entity.getChannelFee());
        }
        
        // 存续期增加通道费 modify by renping 20191018 end
        trdFinProductDealTotal.setIntAmt(entity.getIntAmt());
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        // trdFinProductDealTotal.setCounterId(entity.getCounterId());
        // trdFinProductDealTotal.setTrader(entity.getTrader());
        // trdFinProductDealTotal.setCounterTrader(entity.getCounterTrader());
        trdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建中.getValue());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        // 撤销
        if (StringUtil.equalsIgnoreCase(BussinessConstant.REVOKE, entity.getMode())) {
            trdFinProductDealTotal.setpTradeId(entity.getpTradeId());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());
        } else {
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        }
        return trdFinProductDealTotal;
    }

    /**
     * 汇总交易扩展 <功能详细描述>
     *
     * @param entity
     * @param trdFinProductDealTotal
     * @return
     * @see
     * @since 1.0
     */
    public static WflPaTrdFinDealTotal gen汇总扩展业务流程(TrdDepoDurationCommonDto entity,
            WflTrdFinProductDealTotal trdFinProductDealTotal) {
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        wflPaTrdFinDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        // 区间票面利率（%） 画面.提前支取利率%
        wflPaTrdFinDealTotal.setRate(ArithmeticUtil.divide(entity.getEarlyWithdrawalRate(), 100, 5));
        // 理论本息总额
        wflPaTrdFinDealTotal.setTheoryAmt(entity.getFpriceAmt());
        // 理论应计利息
        wflPaTrdFinDealTotal.setTheoryInt(entity.getTheoryInt());
        // 提前支取金额
        wflPaTrdFinDealTotal.setTheoryPrin(entity.getEarlyWithdrawalAmt());
        if (StringUtil.equals(entity.getOpType(), "draw")) {
            wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_存款提前支取.getValue());
        } else {
            wflPaTrdFinDealTotal.setPaBussType(entity.getPingAnCodeSin());
        }
        // 剩余本金
        wflPaTrdFinDealTotal.setRemainPrinAmt(entity.getRemainPrinAmt());
        // 存续期增加通道费 add by renping 20191018 start
        trdFinProductDealTotal.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), entity.getCollectType())) {
                trdFinProductDealTotal.setFeeAmtPay(entity.getChannelFee());
                trdFinProductDealTotal.setFeeAmtUnpay(entity.getFeeAmt());
            }
            else {
                trdFinProductDealTotal.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
            }
        }else{
        	trdFinProductDealTotal.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
        }
        
        // 存续期增加通道费 add by renping 20191018 end
        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建中.getValue());
        return wflPaTrdFinDealTotal;
    }

    public List<WflTrdProductDeal> gen交易业务流程(TrdDepoDurationCommonDto entity) {
        List<String> cashIds = StringUtil.splitToList(",", entity.getCashId());
        List<String> cashTypes = StringUtil.splitToList(",", entity.getCashType());
        // List<WflTrdProductDeal> wflTrdProductDeals = new ArrayList<>();
        List<WflTrdProductDeal> deals = new ArrayList<>();
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        // 获取原交易编号
        List<TrdProductDeal> orgTrade = null;
        Map<String, String> idMap = new HashMap<>();
        if (StringUtil.equalsIgnoreCase(BussinessConstant.REVOKE, entity.getMode())) {
            orgTrade = autoPeriodCommonService.findDealByTotalId(entity.getpTradeId());
            if (CollectionUtils.isNotEmpty(orgTrade)) {
                // 处理类型和交易代码的关系
                orgTrade.forEach(item -> {
                    idMap.put(item.getPs(), item.getTradeId());
                });
            }
        }
        for (int i = 0; i < cashIds.size(); i++) {
            WflTrdProductDeal deal = new WflTrdProductDeal();
            deal.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
            deal.setFinprodId(entity.getFinprodId());
            deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
            // deal.setFinprodType(); TODO 估值核算 金融产品类型
            // deal.setSecManageAcctId(entity.getSecManageAcctId());
            deal.setCcy(entity.getCcy());
            // deal.setPs(entity.getCashType());
            deal.setTradeDate(entity.getTradeDate());
            deal.setVdate(entity.getVdateProd());
            deal.setMdate(entity.getMdateProd());
            deal.setSettleDate(entity.getTradeDate());
//            deal.setTradeMarket(entity.getTradeMarket());
            deal.setWorkflowStatus(DictCodeList.数据状态_新建中.getValue());
            deal.setWorkflowId(entity.getWorkflowId());
            deal.setTotleTradeId(entity.getBusiCode());
            deal.setCashId(cashIds.get(i));
            if (StringUtil.equals(entity.getOpType(), "draw")) {
                deal.setPs(DictCodeList.交易方向_提前支取.getValue());
                // 存续期增加通道费 modify by renping 20191018 start
                deal.setShareAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
                deal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
                deal.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
                if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
                	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(),
                            entity.getCollectType())) {
                            deal.setFeeAmtPay(entity.getChannelFee());
                            deal.setFeeAmtUnpay(entity.getFeeAmt());
                        }
                        else {
                            deal.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
                        }
                }else{
                	deal.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
                }
                
                // 存续期增加通道费 modify by renping 20191018 end
                deal.setIntAmt(entity.getIntAmt());
                deal.setTradeAmt(entity.getTradeAmt());
            }
            else if (DictCodeList.现金流类型_到期还本.getValue().equals(cashTypes.get(i))) {
                deal.setPs(DictCodeList.交易方向_到期.getValue());
                deal.setBranch(AutoPeriodCommonService.changePsToBranch(DateUtil.format(entity.getMdate()), deal.getPs()));
                deal.setPs(DictCodeList.交易方向_还本到账.getValue());
                // 存续期增加通道费 modify by renping 20191018 start
                deal.setShareAmt(
                    ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
                deal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
                deal.setTradeAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
                if (entity.getIntAmt() == null
                    || entity.getIntAmt().compareTo(BigDecimal.ZERO) <= 0) {
                    deal.setFeeAmt(entity.getChannelFee());
                    if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
                    	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(),
                                entity.getCollectType())) {
                                deal.setFeeAmtPay(entity.getChannelFee());
                            }
                            else {
                                deal.setFeeAmtUnpay(entity.getChannelFee());
                            }
                    }else{
                    	deal.setFeeAmtUnpay(entity.getChannelFee());
                    }
                    
                }
                // 存续期增加通道费 modify by renping 20191018 start
            } else if (StringUtil.equals(DictCodeList.现金流类型_到期收息.getValue(), cashTypes.get(i))
                    || StringUtil.equals(DictCodeList.现金流类型_中间收息.getValue(), cashTypes.get(i))) {
//                deal.setBranch(AutoPeriodCommonService.changePsToBranch(DateUtil.format(entity.getMdate()), deal.getPs()));
                Date tempMdate = entity.getMdateProd() == null ? entity.getMdate() : entity.getMdateProd();
                deal.setBranch(AutoPeriodCommonService.changePsToBranch(DateUtil.format(tempMdate), deal.getPs()));
                deal.setPs(DictCodeList.交易方向_收息到账.getValue());
                deal.setShareAmt(entity.getRemainPrinAmt());
                // 实收利息总额
                deal.setIntAmt(entity.getIntAmt());
                // 存续期增加通道费 modify by renping 20191018 start
                deal.setTradeAmt(entity.getTradeAmt());
                deal.setFeeAmt(entity.getChannelFee());
                if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
                	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(),
                            entity.getCollectType())) {
                            deal.setFeeAmtPay(entity.getChannelFee());
                        }
                        else {
                            deal.setFeeAmtUnpay(entity.getChannelFee());
                        }
                }else{
                	  deal.setFeeAmtUnpay(entity.getChannelFee());
                }
                
                // 存续期增加通道费 modify by renping 20191018 start
            }
            if (StringUtil.equalsIgnoreCase(BussinessConstant.REVOKE, entity.getMode())) {
                // 设置原交易代码
                deal.setpTradeId(idMap.get(cashTypes.get(i)));
                // 设置交易编号
                deal.setTradeId(idMap.get(cashTypes.get(i)) + 'R');
                deal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                deal.setIsCancel(DictCodeList.是否_是.getValue());
            } else {
                deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
                deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                deal.setIsCancel(DictCodeList.是否_否.getValue());
            }
            
            // 一体化对应etliu start
            // 存续期增加通道费 modify by renping 20191018 start
            deal.setFpriceAmt(entity.getTradeAmtReal());
            // 存续期增加通道费 modify by renping 20191018 start
            // 一体化对应etliu end
            
            deals.add(deal);
        }

        return deals;
    }

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
    public BaseTotalTradeInfoDto genFinProductTradeDto(TrdDepoDurationCommonDto entity) {
        BaseTotalTradeInfoDto result = new BaseTotalTradeInfoDto();
        // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        TrdFinProductDealTotal trdFinProductDealTotal = gen汇总流程表2(entity);
        result.setTrdFinProductDealTotal(trdFinProductDealTotal);
        // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = gen汇总扩展业务流程2(entity, trdFinProductDealTotal);
        result.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
        //活期存放结息没用费用明细
        if(!StringUtil.equals("demand",entity.getOpType())){
            TrdTradeFeeDetail trdTradeFeeDetailTotal = gen汇总交易费用明细(trdFinProductDealTotal, entity);
            result.setTrdTradeFeeDetail(trdTradeFeeDetailTotal);
        }
        else{
            TrdDealTotalAdd trdDealTotalAdd = gen交易汇总附属(entity);
            result.setTrdDealTotalAdd(trdDealTotalAdd);
        }
        List<BaseTradeInfoDto> baseTradeInfoDtoList = new ArrayList<>();
        result.setBaseTradeInfoDtoList(baseTradeInfoDtoList);

        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        //       划款指令
        List<BussTrdTransferOrderDto> stlPayOrdersList = autoPeriodCommonService.save(entity.getBussTrdTransferOrder());
        List<FinAssetIntInfo> 利息明细 = entity.getFinAssetIntInfoList();
        //活期存放结息
        if(StringUtil.isEmptyList(利息明细)&&StringUtil.equals("demand",entity.getOpType())){
            BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
            baseTradeInfoDtoList.add(baseTradeInfoDto);
            处理活期存放结息dto(entity,baseTradeInfoDto,parameter,entity.getPortfolioId());
            stlPayOrdersList.forEach(item->{
                item.setPortfolioId(entity.getPortfolioId());
            });
            StlPayOrders stl = autoPeriodCommonService.gen划款指令流程(stlPayOrdersList, trdFinProductDealTotal, entity.getWorkflowId());
            baseTradeInfoDto.setStlPayOrders(stl);
            //处理母组合交易
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                List<BaseTradeInfoDto> subBaseTradeInfoDtoList = new ArrayList<>();
                baseTradeInfoDto.setBaseTradeInfoDtoList(subBaseTradeInfoDtoList);
                BaseTradeInfoDto subBaseTradeInfoDto = new BaseTradeInfoDto();
                处理活期存放结息dto(entity,subBaseTradeInfoDto,parameter,entity.getActPortfolioId());
                if(StringUtil.equals(entity.getIntSettType(),DictCodeListExt3.结息方式_现金结息.getValue())){
                    StlPayOrders subStl= new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(subStl,stl);
                    subStl.setPortfolioId(entity.getActPortfolioId());
                    subBaseTradeInfoDto.setStlPayOrders(subStl);
                }
                //处理子组合交易
                if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    baseTradeInfoDto.setBaseTradeInfoDtoList(subBaseTradeInfoDtoList);
                    subBaseTradeInfoDtoList.add(subBaseTradeInfoDto);
                    处理活期存放结息dto(entity,subBaseTradeInfoDto,parameter,entity.getActPortfolioId());
                    StlPayOrders subStl= new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(subStl,stl);
                    subStl.setPortfolioId(entity.getActPortfolioId());
                    subBaseTradeInfoDto.setStlPayOrders(subStl);
                }
            }
        }
//        提前支取，无利息明细
        else if(StringUtil.isEmptyList(利息明细)&&StringUtil.equals("draw",entity.getOpType())) {
        	BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
            baseTradeInfoDtoList.add(baseTradeInfoDto);
            处理提前支取交易dto(entity,baseTradeInfoDto,parameter,entity.getPortfolioId());
            stlPayOrdersList.forEach(item->{
                item.setPortfolioId(entity.getPortfolioId());
            });
            StlPayOrders stl = autoPeriodCommonService.gen划款指令流程(stlPayOrdersList, trdFinProductDealTotal, entity.getWorkflowId());
            baseTradeInfoDto.setStlPayOrders(stl);
            // 存续期增加通道费 add by renping 20191018 start
            baseTradeInfoDto.setTrdTradeFeeDetail(
                gen交易通道费用明细(baseTradeInfoDto.getTrdProductDeal(), null, entity));
            // 存续期增加通道费 add by renping 20191018 end
//            处理子组合交易
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                List<BaseTradeInfoDto> subBaseTradeInfoDtoList = new ArrayList<>();
                baseTradeInfoDto.setBaseTradeInfoDtoList(subBaseTradeInfoDtoList);
                BaseTradeInfoDto subBaseTradeInfoDto = new BaseTradeInfoDto();
                subBaseTradeInfoDtoList.add(subBaseTradeInfoDto);
                处理提前支取交易dto(entity,subBaseTradeInfoDto,parameter,entity.getActPortfolioId());
                StlPayOrders subStl= new StlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(subStl,stl);
                subStl.setPortfolioId(entity.getActPortfolioId());
                subBaseTradeInfoDto.setStlPayOrders(subStl);
                // 存续期增加通道费 add by renping 20191018 start
                subBaseTradeInfoDto.setTrdTradeFeeDetail(
                    gen交易通道费用明细(subBaseTradeInfoDto.getTrdProductDeal(), null, entity));
                // 存续期增加通道费 add by renping 20191018 end
            }
        }
        else {
        	if (!StringUtil.isEmptyList(利息明细)) {
				利息明细.forEach(finAssetIntInfo -> {
						BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
						baseTradeInfoDtoList.add(baseTradeInfoDto);
						gen利息明细汇总表和扩展表(finAssetIntInfo, entity, baseTradeInfoDto);
						List<String> cashIds = StringUtil.splitToList(",", finAssetIntInfo.getCashId());
						List<String> cashTypes = StringUtil.splitToList(",", finAssetIntInfo.getCashType());
						 //                      生成划款指令
                        StlPayOrders stl = autoPeriodCommonService.gen划款指令流程(stlPayOrdersList, baseTradeInfoDto.getTrdFinProductDealTotal(), entity.getWorkflowId());
                    baseTradeInfoDto.setStlPayOrders(stl);
                    //            生成子组合利息明细共同方法
                    Map<String,Object> subMap = new HashMap<>();
                    subMap.put("portfolioId",finAssetIntInfo.getPortfolioId());
//            持仓时间
                    subMap.put("dealDate", finAssetIntInfo.getmDate());
//            资产代码
                    subMap.put("finprodMarketId",entity.getFinprodMarketId());
//            金融产品代码
                    subMap.put("finprodId",entity.getFinprodId());
                    Map<String,String> defaultMap = new HashMap<>();
//            自定义不累加
                    defaultMap.put("loadRate",finAssetIntInfo.getLoadRate().toString());
                    defaultMap.put("vDate",finAssetIntInfo.getvDate());
                    defaultMap.put("mDate",finAssetIntInfo.getmDate());
                    subMap.put("defaultMap", JsonUtils.object2Json(defaultMap));
////            汇总交易编号
//            subMap.put("totleTradeId",entity.getTotleTradeId());
                    Map<String, BigDecimal> preMap = new HashMap<>();
                    preMap.put("remainPrinAmt",finAssetIntInfo.getRemainPrinAmt());
                    preMap.put("tradeAmt",finAssetIntInfo.getTradeAmt());
                    // 存续期增加通道费 add by renping 20191018 start
                    preMap.put("channelFee", finAssetIntInfo.getChannelFee());
                    // 存续期增加通道费 add by renping 20191018 end
                    preMap.put("returnAmount",finAssetIntInfo.getReturnAmount());
                    preMap.put("theoryInt",finAssetIntInfo.getTheoryInt());
                    preMap.put("intAmt",finAssetIntInfo.getIntAmt());
                    preMap.put("qty",finAssetIntInfo.getQty());
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
                        for (int i = 0; i < cashIds.size(); i++) {
							String ps = AutoPeriodCommonService.change现金流类型To交易方向(cashTypes.get(i));
							if (DictCodeList.交易方向_收息.getValue().equals(ps)) {
							TrdProductDeal deal = 生成交易2(entity,baseTradeInfoDto, cashIds, DictCodeList.交易方向_收息.getValue(),finAssetIntInfo,i);
							baseTradeInfoDto.setTrdProductDeal(deal);
							baseTradeInfoDto.setTrdProductDealAdd(gen交易附属表2(deal,finAssetIntInfo));
		//                  处理组合交易
                            baseTradeInfoDto.setPtlTrade(gen组合交易2(entity, deal, finAssetIntInfo));
                            baseTradeInfoDto.setTrdTradeFeeDetail(
                                this.gen交易通道费用明细(deal, finAssetIntInfo, entity));
							} 
							else if (DictCodeList.交易方向_到期.getValue().equals(ps)) {
		//                    	生成交易
								TrdProductDeal deal = 生成交易2(entity, baseTradeInfoDto,cashIds, ps,finAssetIntInfo, i);
								BaseTrade baseTrade = new BaseTrade();
		//                    	设置交易表数据
								baseTrade.setTrdProductDeal(deal);
								baseTrade.setTrdProductDealAdd(gen交易附属表2(deal,finAssetIntInfo));
		//                      处理组合交易
								baseTrade.setPtlTrade(gen组合交易2(entity,deal,finAssetIntInfo));
		//                      设置本金对应所有交易
								baseTradeInfoDto.setBaseTrade(baseTrade);
			                    // 存续期增加通道费 add by renping 20191018 start
	                            baseTradeInfoDto.setTrdTradeFeeDetail(
	                                this.gen交易通道费用明细(deal, finAssetIntInfo, entity));
	                            // 存续期增加通道费 add by renping 20191018 end
							}
                            int temp = i;
                            子组合利息明细.forEach(subFinAssetIntInfo->{
                                subFinAssetIntInfo.setPortfolioId(subFinAssetIntInfo.getSubPortfolioId());
                                StlPayOrders subStl = new StlPayOrders();
                                BeanUtil.copyPropertiesIgnoreNull(subStl,stl);
                                subStl.setSettleAmt(subFinAssetIntInfo.getSettleAmt());
                                subStl.setPortfolioId(subFinAssetIntInfo.getSubPortfolioId());
//                    子组合交易dto
                                BaseTradeInfoDto subBaseTradeInfoDto = new BaseTradeInfoDto();
                                //                        设置划款指令
                                subBaseTradeInfoDto.setStlPayOrders(subStl);
//                    子组合交易dto放入list
                                subBaseTradeInfoList.add(subBaseTradeInfoDto);
                                if (DictCodeList.交易方向_收息.getValue().equals(ps)) {
                                    TrdProductDeal deal = 生成交易2(entity,baseTradeInfoDto, cashIds, DictCodeList.交易方向_收息.getValue(),subFinAssetIntInfo,temp);
                                    subBaseTradeInfoDto.setTrdProductDeal(deal);
                                    subBaseTradeInfoDto.setTrdProductDealAdd(gen交易附属表2(deal,subFinAssetIntInfo));
                                    //                  处理组合交易
                                    subBaseTradeInfoDto.setPtlTrade(gen组合交易2(entity,deal,subFinAssetIntInfo));
                                    // 存续期增加通道费 add by renping 20191018 start
                                    baseTradeInfoDto.setTrdTradeFeeDetail(
                                        this.gen交易通道费用明细(deal, finAssetIntInfo, entity));
                                    // 存续期增加通道费 add by renping 20191018 end
                                }
                                else if (DictCodeList.交易方向_到期.getValue().equals(ps)) {
                                    //                    	生成交易
                                    TrdProductDeal deal = 生成交易2(entity, baseTradeInfoDto,cashIds, ps,subFinAssetIntInfo, temp);
                                    BaseTrade baseTrade = new BaseTrade();
                                    //                    	设置交易表数据
                                    baseTrade.setTrdProductDeal(deal);
                                    baseTrade.setTrdProductDealAdd(gen交易附属表2(deal,subFinAssetIntInfo));
                                    //                      处理组合交易
                                    baseTrade.setPtlTrade(gen组合交易2(entity,deal,subFinAssetIntInfo));
                                    //                      设置本金对应所有交易
                                    subBaseTradeInfoDto.setBaseTrade(baseTrade);
                                }
                            });
						}

				});
			}
        }
        return result;
    }

    /**
     * 生成附属交易汇总
     * <功能详细描述>
     * @param deal 交易
     * @param finAssetIntInfo 利息明细
     * @return 附属交易
     * @see
     * @since 1.0
     */
    public static TrdDealTotalAdd gen交易汇总附属(TrdDepoDurationCommonDto entity){
        TrdDealTotalAdd add = new TrdDealTotalAdd();
        add.setTotleTradeId(entity.getTotleTradeId());
        add.setWorkflowId(entity.getWorkflowId());
        add.setMpFinprodId(entity.getFinprodId());
        add.setIntSettType(entity.getIntSettType());
        return add;
    }

    /**
	 * 生成附属交易
	 * <功能详细描述>
	 * @param deal 交易
	 * @param finAssetIntInfo 利息明细
	 * @return 附属交易
	 * @see
	 * @since 1.0
	 */
	private TrdProductDealAdd gen交易附属表2(TrdProductDeal deal, FinAssetIntInfo finAssetIntInfo) {
		TrdProductDealAdd dealAdd = new TrdProductDealAdd();
		dealAdd.setPortfolioId(finAssetIntInfo.getPortfolioId());
		dealAdd.setTradeId(deal.getTradeId());
        //        丁志鹏 20190924 添加持仓存储 start
        dealAdd.setRegDateAmt(finAssetIntInfo.getAmount());
        //        丁志鹏 20190924 添加持仓存储 end
        dealAdd.setWorkflowId(deal.getWorkflowId());
		return dealAdd;
	}

	/**
	 * 处理提前支取交易dto
	 * <功能详细描述>
	 * @param entity 业务entity
	 * @param baseTradeInfoDto 待处理交易dto
     * @param parameter 业务编号共通
     * @param  portfolioId 组合代码
	 * @see
	 * @since 1.0
	 */
	private void 处理提前支取交易dto(TrdDepoDurationCommonDto entity, BaseTradeInfoDto baseTradeInfoDto, BussinessCodeParameter parameter,String portfolioId) {
//		处理交易
		TrdProductDeal deal = new TrdProductDeal();
        deal.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
        deal.setFinprodId(entity.getFinprodId());
        deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        deal.setCcy(entity.getCcy());
        deal.setTradeDate(entity.getTradeDate());
        deal.setVdate(entity.getVdateProd());
        deal.setMdate(entity.getMdateProd());
        deal.setSettleDate(entity.getTradeDate());
        deal.setStatus(DictCodeList.数据状态_新建中.getValue());
        deal.setWorkflowId(entity.getWorkflowId());
        deal.setTotleTradeId(entity.getBusiCode());
//      资产联动出的cashId
        deal.setCashId(entity.getCashId());
        deal.setPs(DictCodeList.交易方向_提前支取.getValue());
        // 存续期增加通道费 modify by renping 20191018 start
        deal.setShareAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
        deal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
        // 存续期增加通道费 modify by renping 20191018 end
        deal.setIntAmt(entity.getIntAmt());
        deal.setTradeAmt(entity.getTradeAmt());
        deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
        deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        deal.setIsCancel(DictCodeList.是否_否.getValue());
        deal.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(),
                    entity.getCollectType())) {
                    deal.setFeeAmtPay(entity.getChannelFee());
                }
                else {
                    deal.setFeeAmtUnpay(entity.getChannelFee());
                }
        }else{
        	deal.setFeeAmtUnpay(entity.getChannelFee());
        }
        // 通道新增 start
        deal.setChlId("999999".equals(entity.getChlId()) ? null : entity.getChlId());
        // 通道新增 end

        baseTradeInfoDto.setTrdProductDeal(deal);

        TrdProductDealAdd dealAdd = new TrdProductDealAdd();
        dealAdd.setTradeId(deal.getTradeId());
        dealAdd.setPortfolioId(portfolioId);
        dealAdd.setWorkflowId(entity.getWorkflowId());
        baseTradeInfoDto.setTrdProductDealAdd(dealAdd);

        List<PtlTrade> result = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
//		提前支取为组合维度
		ptlTrade.setPortfolioId(portfolioId);
		ptlTrade.setBusiNo(deal.getTradeId());
		ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
		ptlTrade.setBusiId(entity.getFinprodId());
		ptlTrade.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
		ptlTrade.setSecManageAcctId("999999");
		ptlTrade.setSettleDate(entity.getTradeDate());
        ptlTrade.setWorkflowId(entity.getWorkflowId());
        result.add(ptlTrade);
        baseTradeInfoDto.setPtlTrade(result);
	}

    /**
     * 处理活期存放结息dto
     * <功能详细描述>
     * @param entity 业务entity
     * @param baseTradeInfoDto 待处理交易dto
     * @param parameter 业务编号共通
     * @param  portfolioId 组合代码
     * @see
     * @since 1.0
     */
    private void 处理活期存放结息dto(TrdDepoDurationCommonDto entity, BaseTradeInfoDto baseTradeInfoDto, BussinessCodeParameter parameter,String portfolioId) {
//		处理交易
        TrdProductDeal deal = new TrdProductDeal();
        deal.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
        deal.setFinprodId(entity.getFinprodId());
        deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        deal.setCcy(entity.getCcy());
        deal.setTradeDate(entity.getTradeDate());
        deal.setVdate(entity.getVdateProd());
        deal.setMdate(entity.getMdateProd());
        deal.setSettleDate(entity.getTradeDate());
        deal.setStatus(DictCodeList.数据状态_新建中.getValue());
        deal.setWorkflowId(entity.getWorkflowId());
        deal.setTotleTradeId(entity.getBusiCode());
        deal.setCashId(entity.getCashId());
        if(StringUtil.equals(entity.getIntSettType(), DictCodeListExt3.结息方式_结转为本金.getValue())){
            deal.setPs(DictCodeList.交易方向_红利再投.getValue());
            deal.setShareAmt(entity.getIntAmt());
            deal.setPrinAmt(entity.getIntAmt());
            deal.setIntAmt(entity.getIntAmt());
        }
        else{
            deal.setPs(DictCodeList.交易方向_收息到账.getValue());
            deal.setTradeAmt(entity.getTradeAmt());
        }
        deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
        deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        deal.setIsCancel(DictCodeList.是否_否.getValue());
        deal.setChlId("999999".equals(entity.getChlId()) ? null : entity.getChlId());
        Date tempMdate = entity.getMdateProd() == null ? entity.getTradeDate() : entity.getMdateProd();
        Long branch = AutoPeriodCommonService.changePsToBranch(DateUtil.format(tempMdate), deal.getPs());
        deal.setBranch(branch==null?0:branch);
        baseTradeInfoDto.setTrdProductDeal(deal);
        TrdProductDealAdd dealAdd = new TrdProductDealAdd();
        dealAdd.setTradeId(deal.getTradeId());
        dealAdd.setPortfolioId(portfolioId);
        dealAdd.setWorkflowId(entity.getWorkflowId());
        baseTradeInfoDto.setTrdProductDealAdd(dealAdd);
        List<PtlTrade> result = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(portfolioId);
        ptlTrade.setBusiNo(deal.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
        ptlTrade.setSecManageAcctId("999999");
        ptlTrade.setSettleDate(entity.getTradeDate());
        ptlTrade.setWorkflowId(entity.getWorkflowId());
        result.add(ptlTrade);
        baseTradeInfoDto.setPtlTrade(result);
    }

	/**
     * 
     * 2.0 生成组合交易entity
     * <功能详细描述>
     * @param entity 业务实体
     * @param deal 交易entity
     * @param finAssetIntInfo 利息明细
     * @return 组合交易dto
     * @see
     * @since 1.0
     */
    private List<PtlTrade> gen组合交易2(TrdDepoDurationCommonDto entity, TrdProductDeal deal, FinAssetIntInfo finAssetIntInfo) {
        List<PtlTrade> result = new ArrayList<>();
        PtlTrade ptlTrade2 = new PtlTrade();
        ptlTrade2.setPortfolioId(finAssetIntInfo.getPortfolioId());
        ptlTrade2.setBusiNo(deal.getTradeId());
        ptlTrade2.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade2.setBusiId(entity.getFinprodId());
        ptlTrade2.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
        ptlTrade2.setSecManageAcctId("999999");
        ptlTrade2.setSettleDate(entity.getTradeDate());
        ptlTrade2.setWorkflowId(entity.getWorkflowId());
        result.add(ptlTrade2);
        return result;
    }
    /**
     * 
     * 2.0生成交易
     * <功能详细描述>
     * @param entity 业务entity
     * @param baseTradeInfoDto 汇总交易
     * @param cashIds 交易编号
     * @param 交易方向 交易方向
     * @param finAssetIntInfo 利息信息
     * @param i 执行序号
     * @return 生成的交易dto
     * @see
     * @since 1.0
     */
    private TrdProductDeal 生成交易2(TrdDepoDurationCommonDto entity, BaseTradeInfoDto baseTradeInfoDto, List<String> cashIds, String 交易方向,
        FinAssetIntInfo finAssetIntInfo, int i) {
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        TrdProductDeal deal = new TrdProductDeal();
        deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
//        deal.setBranch(AutoPeriodCommonService.changePsToBranch(DateUtil.format(entity.getMdate()), 交易方向));
        Date tempMdate = entity.getMdateProd() == null ? entity.getMdate() : entity.getMdateProd();
        deal.setBranch(AutoPeriodCommonService.changePsToBranch(DateUtil.format(tempMdate), 交易方向));
        deal.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
        deal.setTotleTradeId(baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
        deal.setFinprodId(entity.getFinprodId());
        deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        deal.setCcy(entity.getCcy());
        deal.setTradeDate(entity.getTradeDate());
        deal.setVdate(DateUtil.parse(finAssetIntInfo.getvDate()));
        deal.setMdate(DateUtil.parse(finAssetIntInfo.getmDate()));
        deal.setSettleDate(entity.getTradeDate());
        deal.setWorkflowId(entity.getWorkflowId());
        deal.setCashId(cashIds.get(i));
        deal.setShareAmt(ArithmeticUtil.divide(finAssetIntInfo.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        if (StringUtil.equals(entity.getOpType(), "draw")) {
            deal.setPs(DictCodeList.交易方向_提前支取.getValue());
            // 存续期增加通道费 modify by renping 20191018 start
            deal.setShareAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
            deal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
            deal.setIntAmt(entity.getIntAmt());
            deal.setTradeAmt(entity.getTradeAmt());
            deal.setFeeAmt(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
            	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(),
                        entity.getCollectType())) {
                        deal.setFeeAmtPay(entity.getChannelFee());
                        deal.setFeeAmtUnpay(entity.getFeeAmt());
                    }
                    else {
                        deal.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
                    }
            }else{
            	deal.setFeeAmtUnpay(ArithmeticUtil.doAdd(entity.getFeeAmt(), entity.getChannelFee()));
            }
            
        } else if (交易方向.equals(DictCodeList.交易方向_到期.getValue())){
            deal.setShareAmt(finAssetIntInfo.getShareAmt());
            deal.setPrinAmt(finAssetIntInfo.getReturnAmount());
            // 存续期增加通道费 modify by renping 20191018 end
            deal.setTradeAmt(ArithmeticUtil.subtract(finAssetIntInfo.getTradeAmt(), finAssetIntInfo.getIntAmt()));
              //update by  zhangnian 2020/03/17  start
            // 还本那笔到账金额多减了通道费(预期本金不减通道费)，前台改动较多，后台add回来!
            if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())
                    &&StringUtil.isNotEmpty(finAssetIntInfo.getIntAmt())&&StringUtil.isNotEmpty(finAssetIntInfo.getChannelFee())){
                deal.setTradeAmt(ArithmeticUtil.doAdd(deal.getTradeAmt(), finAssetIntInfo.getChannelFee()));
            }
            //update by  zhangnian 2020/03/17  end
            deal.setPs(DictCodeList.交易方向_还本到账.getValue());
        }else if (交易方向.equals(DictCodeList.交易方向_收息.getValue())) {
//            deal.setPs(DictCodeList.交易方向_收息.getValue());
            deal.setPs(DictCodeList.交易方向_收息到账.getValue());
            deal.setShareAmt(finAssetIntInfo.getRemainPrinAmt());
            // 实收利息总额
            deal.setIntAmt(finAssetIntInfo.getIntAmt());
            deal.setTradeAmt(finAssetIntInfo.getIntAmt());
        }
        
// deal.setFinprodType(); TODO 估值核算 金融产品类型
        deal.setSecManageAcctId(entity.getSecManageAcctId());
        deal.setCcy(entity.getCcy());
//        deal.setPs(交易方向);
        deal.setTradeDate(entity.getTradeDate());
        deal.setSettleDate(deal.getTradeDate());
//        deal.setShareAmt(ArithmeticUtil.divide(finAssetIntInfo.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        if (DictCodeList.交易方向_收息.getValue().equals(交易方向)) {
            deal.setIntAmt(finAssetIntInfo.getIntAmt());
            deal.setTradeAmt(finAssetIntInfo.getIntAmt());
            // 存续期增加通道费 modify by renping 20191018 start
            deal.setFeeAmt(finAssetIntInfo.getChannelFee());
            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
            	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(),
                        entity.getCollectType())) {
                        deal.setFeeAmtPay(finAssetIntInfo.getChannelFee());
                        deal.setTradeAmt(ArithmeticUtil.subtract(finAssetIntInfo.getIntAmt(), finAssetIntInfo.getChannelFee()));
                    }
                    else {
                        deal.setFeeAmtUnpay(finAssetIntInfo.getChannelFee());
                        deal.setTradeAmt(finAssetIntInfo.getIntAmt());
                    }
            }else{
            	deal.setFeeAmtUnpay(finAssetIntInfo.getChannelFee());
                deal.setTradeAmt(finAssetIntInfo.getIntAmt());
            }
            
        } else if (DictCodeList.交易方向_还本.getValue().equals(交易方向)
                || DictCodeList.交易方向_到期.getValue().equals(交易方向)) {
            deal.setPrinAmt(finAssetIntInfo.getReturnAmount());
            if (entity.getIntAmt() == null || entity.getIntAmt().compareTo(BigDecimal.ZERO) <= 0) {
                deal.setFeeAmt(finAssetIntInfo.getChannelFee());
                if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
                	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(),
                            entity.getCollectType())) {
                            deal.setFeeAmtPay(finAssetIntInfo.getChannelFee());
                            deal.setTradeAmt(ArithmeticUtil.subtract(finAssetIntInfo.getReturnAmount(),
                            		finAssetIntInfo.getChannelFee()));
                        }
                        else {
                            deal.setFeeAmtUnpay(finAssetIntInfo.getChannelFee());
                            deal.setTradeAmt(finAssetIntInfo.getReturnAmount());
                        }
                }else{
                	deal.setFeeAmtUnpay(finAssetIntInfo.getChannelFee());
                    deal.setTradeAmt(finAssetIntInfo.getReturnAmount());
                }
                
            }
            // 存续期增加通道费 modify by renping 20191018 end
        }
//        投资目的
        deal.setInvAim(finAssetIntInfo.getInvAim());
        deal.setTradeMarket(entity.getTradeMarket());
        deal.setCashId(cashIds.get(i));
        deal.setIsCancel(DictCodeList.是否_否.getValue());
        deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        deal.setWorkflowId(entity.getWorkflowId());
        deal.setStatus(DictCodeList.数据状态_新建.getValue());
        // <!-- 通道交易 新增 强成西 2019/03/26 start -->
        deal.setChlId("999999".equals(entity.getChlId()) ? null : entity.getChlId());
        // <!-- 通道交易 新增 强成西 2019/03/26 end -->
        deal.setIsClean(entity.getIsClean());
        return deal;
    }
    private void gen利息明细汇总表和扩展表(FinAssetIntInfo finAssetIntInfo, TrdDepoDurationCommonDto entity,
            BaseTradeInfoDto baseTradeInfoDto) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(autoPeriodCommonService
                .buildBussinessCode(new BussinessCodeParameter(DictCodeList.共通业务类型_资产存续期交易)));
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
// trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setPs(AutoPeriodCommonService.change现金流类型To交易方向(entity.getCashType()));
        trdFinProductDealTotal.setTradeDate(entity.getTradeDate());
        trdFinProductDealTotal.setSettleDate(entity.getTradeDate());
        trdFinProductDealTotal.setVdate(DateTime.parse(finAssetIntInfo.getvDate()).toDate());
        trdFinProductDealTotal.setMdate(DateTime.parse(finAssetIntInfo.getmDate()).toDate());
        
        // 存续期增加通道费 modify by renping 20191018 start
        // 画面.实收本息总额-画面.实收利息总额
        trdFinProductDealTotal.setShareAmt(finAssetIntInfo.getRemainPrinAmt());
        // 画面.实收本息总额-画面.实收利息总额
        trdFinProductDealTotal.setPrinAmt(finAssetIntInfo.getReturnAmount());
        trdFinProductDealTotal.setFeeAmt(finAssetIntInfo.getChannelFee());
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), entity.getCollectType())) {
                trdFinProductDealTotal.setFeeAmtPay(finAssetIntInfo.getChannelFee());
            }
            else {
                trdFinProductDealTotal.setFeeAmtUnpay(finAssetIntInfo.getChannelFee());
            }
        }else{
        	 trdFinProductDealTotal.setFeeAmtUnpay(finAssetIntInfo.getChannelFee());
        }
        
        // 存续期增加通道费 modify by renping 20191018 end
        trdFinProductDealTotal.setIntAmt(finAssetIntInfo.getIntAmt());
        trdFinProductDealTotal.setTradeAmt(finAssetIntInfo.getTradeAmt());
        
        trdFinProductDealTotal.setPortfolioId(finAssetIntInfo.getPortfolioId());
        
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setpTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setIsClean(entity.getIsClean());
        // <!-- 通道交易 新增 强成西 2019/03/26 start -->
        trdFinProductDealTotal.setChlId("999999".equals(entity.getChlId()) ? null : entity.getChlId());
        // <!-- 通道交易 新增 强成西 2019/03/26 end -->
        baseTradeInfoDto.setTrdFinProductDealTotal(trdFinProductDealTotal);

        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        // 理论本息总额
        paTrdFinProductDealTotal.setTheoryAmt(finAssetIntInfo.getFpriceAmt());
        // 理论应计利息
        paTrdFinProductDealTotal.setTheoryInt(finAssetIntInfo.getTheoryInt());
        paTrdFinProductDealTotal.setPaBussType(getPaBussTypeByCashType(finAssetIntInfo.getCashType()));
        paTrdFinProductDealTotal.setRemainPrinAmt(finAssetIntInfo.getRemainPrinAmt());
//      理论还本金额
        paTrdFinProductDealTotal.setTheoryPrin(finAssetIntInfo.getReturnAmount());
        //        借款利率
        paTrdFinProductDealTotal.setRate(ArithmeticUtil.divide(entity.getLoadRate(), 100, 4));
        paTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        baseTradeInfoDto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
    }
    /**
     * 
     * 根据现金流类型转换为平安业务类型
     * <功能详细描述>
     * @param cashType 现金流类型
     * @return
     * @see
     * @since 1.0
     */
    private String getPaBussTypeByCashType(String cashType) {
    	String paBussType = DictCodeListExt1.平安业务类型_存款提前支取.getValue();
        if (StringUtil.isNotEmpty(cashType)) {
            if (cashType.contains(DictCodeList.现金流类型_到期还本.getValue())) {
            	paBussType = DictCodeListExt1.平安业务类型_存款到期.getValue();
            }
            else if(cashType.contains(DictCodeList.现金流类型_中间收息.getValue())){
            	paBussType = DictCodeListExt1.平安业务类型_存款中间付息.getValue();
            }
        }
        return paBussType;
    }
    /**
     * 根据业务entity获取汇总扩展entity
     * 
     * @param entity     业务entity
     * @param trdFinProductDealTotal 汇总entity
     * @return 组装的entity
     * @see
     * @since 1.0
     */
    private static PaTrdFinProductDealTotal gen汇总扩展业务流程2(TrdDepoDurationCommonDto entity,
            TrdFinProductDealTotal trdFinProductDealTotal) {
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        // 区间票面利率（%） 画面.提前支取利率%
        paTrdFinProductDealTotal.setRate(ArithmeticUtil.divide(entity.getEarlyWithdrawalRate(), 100, 5));
        // 理论本息总额
        paTrdFinProductDealTotal.setTheoryAmt(entity.getFpriceAmt());
        // 理论应计利息
        paTrdFinProductDealTotal.setTheoryInt(entity.getTheoryInt());
        // 提前支取金额
        paTrdFinProductDealTotal.setTheoryPrin(entity.getEarlyWithdrawalAmt());
//      资产汇总交易不处理，提前支取处理
        if (StringUtil.equals(entity.getOpType(), "draw")) {
            paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_存款提前支取.getValue());
        }
        else {
            paTrdFinProductDealTotal.setPaBussType(entity.getPingAnCodeSin());
        }
        // 剩余本金
        paTrdFinProductDealTotal.setRemainPrinAmt(entity.getRemainPrinAmt());
        paTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        paTrdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建中.getValue());
        return paTrdFinProductDealTotal;
    }

    /**
     * 
     * 转换获取交易费 <功能详细描述>
     * 
     * @param trdFinProductDealTotal 汇总交易entity
     * @param entity                 业务实体
     * @return 交易费用明细
     * @see
     * @since 1.0
     */
    private TrdTradeFeeDetail gen汇总交易费用明细(TrdFinProductDealTotal trdFinProductDealTotal,
            TrdDepoDurationCommonDto entity) {
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
//        画面的资产推荐费
        trdTradeFeeDetail.setFeeAmt(entity.getFeeAmt());
        trdTradeFeeDetail.setTradeId(entity.getBusiCode());
        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_投管_资产推荐费.getValue()); // 一次性费用类型        
        trdTradeFeeDetail.setCcy(entity.getCcy());
        return trdTradeFeeDetail;
    }

    // 存续期增加通道费 add by renping 20191018 start
    /**
     * 转换获取通道费
     * 
     * @param trdFinProductDealTotal 汇总交易entity
     * @param entity 业务实体
     * @return 交易费用明细
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> gen交易通道费用明细(TrdProductDeal deal, FinAssetIntInfo finAssetIntInfo,
                                            TrdDepoDurationCommonDto entity) {
        if (entity.getChannelFee() == null
            || entity.getChannelFee().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        } 
        BigDecimal channelFee = null;
        // 提前支取
        if(finAssetIntInfo == null) {
            channelFee = entity.getChannelFee();
        }
        else if (StringUtil.isNotEmpty(finAssetIntInfo.getChannelFee())
            && finAssetIntInfo.getChannelFee().compareTo(BigDecimal.ZERO) > 0) {
        	channelFee = finAssetIntInfo.getChannelFee();
        } else {
        	return null;
        }
        List<TrdTradeFeeDetail> detailList = new ArrayList<TrdTradeFeeDetail>();
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        	if (entity.isChannel() && StringUtil.equals(entity.getCollectType(),
                    DictCodeListExt2.收取方式_回款前扣除.getValue())) {
                    trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                }
                else {
                    trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                }
        }else{
        	trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        }
        
        // 画面的通道费
        trdTradeFeeDetail.setFeeAmt(channelFee);
        trdTradeFeeDetail.setTradeId(deal.getTradeId());
        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue()); // 一次性费用类型
        trdTradeFeeDetail.setFeeId(DictCodeListExt1.一次性费用类型_通道费.getValue());
        trdTradeFeeDetail.setCcy(entity.getCcy());
        detailList.add(trdTradeFeeDetail);
        return detailList;
    }
    // 存续期增加通道费 modify by renping 20191018 end

    /**
     * 汇总业务流程 <功能详细描述>
     *
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public static TrdFinProductDealTotal gen汇总流程表2(TrdDepoDurationCommonDto entity) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        // 估值核算 金融产品类型
        // trdFinProductDealTotal.setFinprodType();
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setBranch(AutoPeriodCommonService.changePsToBranch(DateUtil.format(entity.getMdate()),
                trdFinProductDealTotal.getPs()));
        // 画面.类型
        if (StringUtil.equals(entity.getOpType(), "demand")) {
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_中间付息.getValue());
        }
        else{
            trdFinProductDealTotal.setPs(entity.getPs());
        }
        // 资金到账日
        trdFinProductDealTotal.setTradeDate(entity.getTradeDate());
        trdFinProductDealTotal.setVdate(entity.getVdateProd());
        trdFinProductDealTotal.setMdate(entity.getMdateProd());
        trdFinProductDealTotal.setSettleDate(entity.getTradeDate());
        // 存续期增加通道费 modify by renping 20191018 start
        trdFinProductDealTotal.setFpriceAmt(entity.getTradeAmtReal());
        // 画面.实收本息总额-画面.实收利息总额
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
        // 画面.实收本息总额-画面.实收利息总额
        trdFinProductDealTotal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
        trdFinProductDealTotal.setFeeAmt(entity.getChannelFee());
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
        	if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), entity.getCollectType())) {
                trdFinProductDealTotal.setFeeAmtPay(entity.getChannelFee());
            }
            else {
                trdFinProductDealTotal.setFeeAmtUnpay(entity.getChannelFee());
            }
        }else{
        	trdFinProductDealTotal.setFeeAmtUnpay(entity.getChannelFee());
        }
        
        // 存续期增加通道费 modify by renping 20191018 end
        trdFinProductDealTotal.setIntAmt(entity.getIntAmt());
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        // <!-- 通道交易 新增 强成西 2019/03/26 start -->
        trdFinProductDealTotal.setChlId("999999".equals(entity.getChlId()) ? null : entity.getChlId());
        // <!-- 通道交易 新增 强成西 2019/03/26 end -->
        trdFinProductDealTotal.setIsClean(entity.getIsClean());
        trdFinProductDealTotal.setRemark(entity.getRemark());
        return trdFinProductDealTotal;
    }
}

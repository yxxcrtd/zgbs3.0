/*
 * 文件名：PeriodCommonUtil.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：etliu 修改时间：2018年12月13日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.autoperiod.bond;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTrade;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurDbInertDto;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurationDto;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 债券资产存续期共通Service 批处理自动存续期和存续期画面共用逻辑
 *
 * @author etliu
 * @version 1.0 2018年12月13日
 * @see AutoPeriodBondCommonService
 * @since 1.0
 */
@Service
public class AutoPeriodBondCommonService {

    /**
     * AutoPeriodCommonService
     */
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;
    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired(required = false)
    private BaseGetBussinessId baseGetBussinessId;
//
//    private BigDecimal result;

    public static List<TrdProductDeal> genTrdProductDealForCashFlow(TrdBondDurationDto entity) {
        List<WflTrdProductDeal> wflDeals = entity.getTrdBondDurDbInertDto().getWflTrdProductDealList();
        Map<String, List<WflTrdProductDeal>> invAimMap = wflDeals.stream().collect(
            JoyinCollectors.groupingBy(wflTrdProductDeal -> wflTrdProductDeal.getInvAim()));
        List<TrdProductDeal> deals = new ArrayList<>();
        invAimMap.forEach((s, wflTrdProductDeals) -> {
            if (wflTrdProductDeals.size() == 1) {
                TrdProductDeal deal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(deal, wflTrdProductDeals.get(0));
                deal.setPs(deal.getStatus());
                deals.add(deal);
            }
            else if (wflTrdProductDeals.size() > 1) {
                TrdProductDeal deal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(deal, wflTrdProductDeals.get(0));
                if (!DictCodeList.交易方向_收息.getValue().equals(deal.getStatus())) {
                    deal.setPs(deal.getStatus());
                }
                for (int i = 1; i < wflTrdProductDeals.size(); i++ ) {
                    deal.setPrinAmt(ArithmeticUtil.doAdd(deal.getPrinAmt(), wflTrdProductDeals.get(i).getPrinAmt()));
                    deal.setIntAmt(ArithmeticUtil.doAdd(deal.getIntAmt(), wflTrdProductDeals.get(i).getIntAmt()));
                    deal.setTradeAmt(ArithmeticUtil.doAdd(deal.getTradeAmt(), wflTrdProductDeals.get(i).getTradeAmt()));
                    if (!DictCodeList.交易方向_收息.getValue().equals(wflTrdProductDeals.get(i).getStatus())) {
                        deal.setPs(wflTrdProductDeals.get(i).getStatus());
                    }
                }
                deals.add(deal);
            }
        });
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
    public BaseTotalTradeInfoDto genFinProductTradeDto(TrdBondDurationDto entity) {
        BaseTotalTradeInfoDto result = new BaseTotalTradeInfoDto();
        // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        TrdFinProductDealTotal trdFinProductDealTotal = gen汇总流程表2(entity);
        result.setTrdFinProductDealTotal(trdFinProductDealTotal);
        // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = gen汇总扩展业务流程2(entity, trdFinProductDealTotal);
        result.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);

        List<BaseTradeInfoDto> baseTradeInfoDtoList = new ArrayList<>();
        result.setBaseTradeInfoDtoList(baseTradeInfoDtoList);

        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        //       划款指令
        List<BussTrdTransferOrderDto> stlPayOrdersList = autoPeriodCommonService.save(entity.getBussTrdTransferOrder());

        // 根据利息明细处理汇总及交易dto
        List<FinAssetIntInfo> 利息明细 = entity.getFinAssetIntInfoList();
        利息明细.forEach(finAssetIntInfo -> {
            BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
            baseTradeInfoDtoList.add(baseTradeInfoDto);
            gen利息明细汇总表和扩展表(finAssetIntInfo, entity, baseTradeInfoDto);
            List<String> cashIds = StringUtil.splitToList(",", finAssetIntInfo.getCashId());
            List<String> cashTypes = StringUtil.splitToList(",", finAssetIntInfo.getCashType());
            //                      生成划款指令
          //update zhangweiqi 2019-10-15 通道费修改 begin
            BigDecimal fPriceAmt=baseTradeInfoDto.getTrdFinProductDealTotal().getFpriceAmt();
            baseTradeInfoDto.getTrdFinProductDealTotal().setFpriceAmt(baseTradeInfoDto.getTrdFinProductDealTotal().getTradeAmt());
            
            StlPayOrders stl = autoPeriodCommonService.gen划款指令流程(stlPayOrdersList, baseTradeInfoDto.getTrdFinProductDealTotal(), entity.getWorkflowId());
            baseTradeInfoDto.getTrdFinProductDealTotal().setFpriceAmt(fPriceAmt);
           ////update zhangweiqi 2019-10-15 通道费修改 end
            baseTradeInfoDto.setStlPayOrders(stl);
            //            生成子组合利息明细共同方法
            Map<String,Object> subMap = new HashMap<>();
            subMap.put("portfolioId",finAssetIntInfo.getPortfolioId());
//            持仓时间
            subMap.put("dealDate", entity.getmDate());
//            资产代码
            subMap.put("finprodMarketId", null);
//            金融产品代码
            subMap.put("finprodId",entity.getFinprodId());
            Map<String,String> defaultMap = new HashMap<>();
//            自定义不累加
            if (finAssetIntInfo.getYield() != null) {
                defaultMap.put("yield",finAssetIntInfo.getYield().toString());
            }
            subMap.put("defaultMap", JsonUtils.object2Json(defaultMap));
////            汇总交易编号
//            subMap.put("totleTradeId",entity.getTotleTradeId());
            Map<String, BigDecimal> preMap = new HashMap<>();
            preMap.put("prinAmtTheory",finAssetIntInfo.getPrinAmtTheory());
            preMap.put("intAmtTheory",finAssetIntInfo.getIntAmtTheory());
            preMap.put("intAmt",finAssetIntInfo.getIntAmt());
            preMap.put("tradeAmt",finAssetIntInfo.getTradeAmt());
            preMap.put("fpriceAmt",finAssetIntInfo.getFpriceAmt());
            preMap.put("qty",finAssetIntInfo.getQty());
            //update zhangweiqi 2019-10-15 通道费修改 add
            if (finAssetIntInfo.getChannelFee()!=null) {
                preMap.put("channelFee",finAssetIntInfo.getChannelFee());
            }
          //update zhangweiqi 2019-10-15 通道费修改 end
//            划款指令结算金额
            if(stl != null){
                preMap.put("settleAmt",stl.getSettleAmt());
            }
            subMap.put("preMap", JsonUtils.object2Json(preMap));
            subMap.put("invAim", finAssetIntInfo.getInvAim());
            subMap.put(GlobalConstant.WORKFLOW_ID,entity.getWorkflowId());
            List<FinAssetIntInfo> 子组合利息明细 = autoPeriodCommonService.distributeDataByPercent(subMap);
//            子组合交易
            List<BaseTradeInfoDto> subBaseTradeInfoList = new ArrayList<>();
            baseTradeInfoDto.setBaseTradeInfoDtoList(subBaseTradeInfoList);
            for (int i = 0; i < cashIds.size(); i++ ) {
                String ps = AutoPeriodCommonService.change现金流类型To交易方向(cashTypes.get(i));
                if (DictCodeList.交易方向_收息.getValue().equals(ps)) {
                    TrdProductDeal deal = 生成交易2(entity, baseTradeInfoDto, cashIds, DictCodeList.交易方向_收息.getValue(),
                        finAssetIntInfo, i, baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                  //update zhangweiqi 通道费修改 2019-10-15 begin
                    if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
                    	deal.setTradeAmt(deal.getTradeAmt().subtract(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee()));
                    }
                    if(entity.getChannelFee()!=null){
                    	baseTradeInfoDto.setTrdTradeFeeDetail(getFeeDetailList(deal,finAssetIntInfo,entity));
                    }
                  //update zhangweiqi 通道费修改 2019-10-15 end

                    baseTradeInfoDto.setTrdProductDeal(deal);
                    // 设置利息对应所有交易
                    baseTradeInfoDto.setTrdProductDealAdd(gen交易附属表2(deal, finAssetIntInfo));
                    // 处理组合交易
                    baseTradeInfoDto.setPtlTrade(gen组合交易2(entity, deal, finAssetIntInfo));
                }
                else if (DictCodeList.交易方向_还本.getValue().equals(ps)
                         || DictCodeList.交易方向_到期.getValue().equals(ps)) {
                    // 生成交易
                    TrdProductDeal deal = 生成交易2(entity, baseTradeInfoDto, cashIds, ps, finAssetIntInfo, i, baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                    BaseTrade baseTrade = new BaseTrade();
                    //update zhangweiqi 通道费修改 2019-10-15 begin
                    if(cashIds.size()==1&& StringUtil.isNotEmpty(finAssetIntInfo.getIntAmt())
                            && finAssetIntInfo.getIntAmt().compareTo(BigDecimal.ZERO) ==0){
                      if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
                    	deal.setTradeAmt(deal.getTradeAmt().subtract(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee()));
                      }
                      if(entity.getChannelFee()!=null){
                    	baseTradeInfoDto.setTrdTradeFeeDetail(getFeeDetailList(deal,finAssetIntInfo,entity));
                      }
                    }
                    //update zhangweiqi 通道费修改 2019-10-15 end
                    // 设置交易表数据
                    baseTrade.setTrdProductDeal(deal);
                    // 设置附属表数据
                    baseTrade.setTrdProductDealAdd(gen交易附属表2(deal, finAssetIntInfo));
                    // 处理组合交易
                    baseTrade.setPtlTrade(gen组合交易2(entity, deal, finAssetIntInfo));
                    // 设置本金对应所有交易
                    baseTradeInfoDto.setBaseTrade(baseTrade);
                    // 单笔还本带息生成收息交易
                    if (cashIds.size() == 1 && StringUtil.isNotEmpty(finAssetIntInfo.getIntAmt())
                        && finAssetIntInfo.getIntAmt().compareTo(BigDecimal.ZERO) >= 0) {
                        TrdProductDeal deal2 = 生成交易2(entity, baseTradeInfoDto, cashIds, DictCodeList.交易方向_收息.getValue(),
                            finAssetIntInfo, i, baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                      //update zhangweiqi 通道费修改 2019-10-15 begin
                        
                          if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
                        	  deal2.setTradeAmt(deal2.getTradeAmt().subtract(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee()));
                        	}
                          if(entity.getChannelFee()!=null){
                        	baseTradeInfoDto.setTrdTradeFeeDetail(getFeeDetailList(deal2,finAssetIntInfo,entity));
                          }
                       //update zhangweiqi 通道费修改 2019-10-15 end
                        // 设置利息对应所有交易
                        baseTradeInfoDto.setTrdProductDeal(deal2);
                        baseTradeInfoDto.setTrdProductDealAdd(gen交易附属表2(deal2, finAssetIntInfo));
                        // 处理组合交易
                        baseTradeInfoDto.setPtlTrade(gen组合交易2(entity, deal2, finAssetIntInfo));
                    }
                }
                int temp = i;
                子组合利息明细.forEach(subFinAssetIntInfo->{
                    subFinAssetIntInfo.setInvAim(finAssetIntInfo.getInvAim());
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
                        TrdProductDeal deal收息 = 生成子组合收息交易(entity, parameter, subFinAssetIntInfo, subBaseTradeInfoDto,
                                cashIds, temp, baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                      //update zhangweiqi 通道费修改 2019-10-15 begin
                        if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
                        	deal收息.setTradeAmt(deal收息.getTradeAmt().subtract(subFinAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:subFinAssetIntInfo.getChannelFee()));
                        	
                        }
                        if(entity.getChannelFee()!=null){
                          subBaseTradeInfoDto.setTrdTradeFeeDetail(getFeeDetailList(deal收息,subFinAssetIntInfo,entity));
                        }
                      //update zhangweiqi 通道费修改 2019-10-15 end
                        subBaseTradeInfoDto.setPtlTrade(gen组合交易2(entity, deal收息, subFinAssetIntInfo));
                    }
                    else {
                        TrdProductDeal deal = 生成交易2(entity, baseTradeInfoDto, cashIds,
                                DictCodeList.交易方向_还本.getValue().equals(ps) ? DictCodeList.交易方向_还本.getValue() : DictCodeList.交易方向_到期.getValue(),
                                subFinAssetIntInfo, temp, baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                        //update zhangweiqi 通道费修改 2019-10-15 begin
                        if(cashIds.size()==1&& StringUtil.isNotEmpty(subFinAssetIntInfo.getIntAmt())
                                && subFinAssetIntInfo.getIntAmt().compareTo(BigDecimal.ZERO) ==0){
                        if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
                        	deal.setTradeAmt(deal.getTradeAmt().subtract(subFinAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:subFinAssetIntInfo.getChannelFee()));
                         }
                        if(entity.getChannelFee()!=null){
                        	subBaseTradeInfoDto.setTrdTradeFeeDetail(getFeeDetailList(deal,subFinAssetIntInfo,entity));
                        }
                        }
                      //update zhangweiqi 通道费修改 2019-10-15 end
                        BaseTrade baseTrade = new BaseTrade();
                        baseTrade.setTrdProductDeal(deal);
                        baseTrade.setTrdProductDealAdd(gen交易附属表2(deal, subFinAssetIntInfo));
                        baseTrade.setPtlTrade(gen组合交易2(entity, deal, subFinAssetIntInfo));
                        subBaseTradeInfoDto.setBaseTrade(baseTrade);
                        if (cashIds.size() == 1 && StringUtil.isNotEmpty(subFinAssetIntInfo.getIntAmt())
                                && subFinAssetIntInfo.getIntAmt().compareTo(BigDecimal.ZERO) > 0) {
                        	 
                        	生成子组合收息交易(entity,parameter, subFinAssetIntInfo, subBaseTradeInfoDto, cashIds, temp,baseTradeInfoDto.getTrdFinProductDealTotal().getTotleTradeId());
                        	//update zhangweiqi 通道费修改 2019-10-15 begin
                        	TrdProductDeal deal2=subBaseTradeInfoDto.getTrdProductDeal();
                        	if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
                        		deal2.setTradeAmt(deal2.getTradeAmt().subtract(subFinAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:subFinAssetIntInfo.getChannelFee()));
                            }
                            if(entity.getChannelFee()!=null){
                            	subBaseTradeInfoDto.setTrdTradeFeeDetail(getFeeDetailList(deal2,subFinAssetIntInfo,entity));
                            }
                            //update zhangweiqi 通道费修改 2019-10-15 end
                        }
                    }
                });
            }
        });

        // 将附件和当前业务对象绑定（更新附件的业务代码）
        if(entity.getUploadFileList() !=null){
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getBusiCode());
        }

        return result;
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
    private TrdProductDeal 生成子组合收息交易(TrdBondDurationDto entity,
                                     BussinessCodeParameter parameter, FinAssetIntInfo subFinAssetIntInfo,
                                     BaseTradeInfoDto baseTradeInfoDto, List<String> cashIds, int i, String totleTradeId) {
        TrdProductDeal deal收息 = 生成交易2(entity, baseTradeInfoDto, cashIds, DictCodeList.交易方向_收息.getValue(),subFinAssetIntInfo, i, totleTradeId);
        baseTradeInfoDto.setTrdProductDeal(deal收息);
        baseTradeInfoDto.setTrdProductDealAdd(gen交易附属表2(deal收息, subFinAssetIntInfo));
        baseTradeInfoDto.setPtlTrade(gen组合交易2(entity, deal收息, subFinAssetIntInfo));
        return deal收息;
    }

    /**
     * 2.0 生成组合交易entity <功能详细描述>
     * 
     * @param entity 业务实体
     * @param deal 交易entity
     * @param finAssetIntInfo 利息明细
     * @return 组合交易dto
     * @see
     * @since 1.0
     */
    private List<PtlTrade> gen组合交易2(TrdBondDurationDto entity, TrdProductDeal deal, FinAssetIntInfo finAssetIntInfo) {
        List<PtlTrade> result = new ArrayList<>();
        PtlTrade ptlTrade2 = new PtlTrade();
        ptlTrade2.setPortfolioId(finAssetIntInfo.getPortfolioId());
        ptlTrade2.setBusiNo(deal.getTradeId());
        // 添加投资目的
        if(StringUtil.equals(finAssetIntInfo.getInvAim(),"999999")){
            ptlTrade2.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
        }
        else{
            ptlTrade2.setInvAim(finAssetIntInfo.getInvAim());
        }
        ptlTrade2.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade2.setBusiId(entity.getFinprodId());
        ptlTrade2.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        ptlTrade2.setSecManageAcctId(entity.getSecManageAcctId());
        ptlTrade2.setSettleDate(DateUtil.parse(entity.getTradeDate()));
        result.add(ptlTrade2);
        return result;
    }

    /**
     * 2.0生成附属表entity <功能详细描述>
     * 
     * @param deal 交易entity
     * @param finAssetIntInfo 利息明细entity
     * @return 附属entity
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd gen交易附属表2(TrdProductDeal deal, FinAssetIntInfo finAssetIntInfo) {
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setPortfolioId(finAssetIntInfo.getPortfolioId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setWorkflowId(deal.getWorkflowId());
        trdProductDealAdd.setTradeId(deal.getTradeId());
        trdProductDealAdd.setStatus(DictCodeList.数据状态_新建.getValue());
        //        丁志鹏 20190924 添加持仓存储 start
        trdProductDealAdd.setRegDateAmt(finAssetIntInfo.getAmount());
        //        丁志鹏 20190924 添加持仓存储 end
        return trdProductDealAdd;
    }

    /**
     * 2.0生成交易 <功能详细描述>
     * 
     * @param entity 业务entity
     * @param baseTradeInfoDto 汇总交易
     * @param cashIds 现金流代码
     * @param 交易方向 交易方向
     * @param finAssetIntInfo 利息信息
     * @param i 执行序号
     * @param totleTradeId
     * @return 生成的交易dto
     * @see
     * @since 1.0
     */
    private TrdProductDeal 生成交易2(TrdBondDurationDto entity, BaseTradeInfoDto baseTradeInfoDto, List<String> cashIds, String 交易方向,
                                 FinAssetIntInfo finAssetIntInfo, int i, String totleTradeId) {
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        TrdProductDeal deal = new TrdProductDeal();
        deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
        deal.setBranch(AutoPeriodCommonService.changePsToBranch(entity.getmDate(), 交易方向));
        deal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        deal.setTotleTradeId(totleTradeId);
        deal.setFinprodId(entity.getFinprodId());
        deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // deal.setFinprodType(); TODO 估值核算 金融产品类型
        if (!entity.isChannel()) {
            deal.setSecManageAcctId(entity.getSecManageAcctId());
        }
        else {
            deal.setChlId("999999".equals(entity.getSecManageAcctId()) ? null : entity.getSecManageAcctId());
        }
        deal.setCcy(entity.getCcy());
        deal.setStatus(交易方向);
        //deal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        deal.setTradeDate(DateUtil.parse(DateUtil.getSysDate()));
        deal.setVdate(DateUtil.parse(entity.getvDate()));
        deal.setMdate(DateUtil.parse(entity.getmDate()));
        //deal.setSettleDate(deal.getTradeDate());
        deal.setSettleDate(DateUtil.parse(entity.getTradeDate()));
        deal.setShareAmt(ArithmeticUtil.divide(finAssetIntInfo.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        if (DictCodeList.交易方向_收息.getValue().equals(交易方向)) {
            deal.setIntAmt(finAssetIntInfo.getIntAmt());
            deal.setTradeAmt(finAssetIntInfo.getIntAmt());
            deal.setPs(DictCodeList.交易方向_收息到账.getValue());
        }
        else if (DictCodeList.交易方向_还本.getValue().equals(交易方向) || DictCodeList.交易方向_到期.getValue().equals(交易方向)) {
            deal.setPs(DictCodeList.交易方向_还本到账.getValue());
            deal.setTradeAmt(ArithmeticUtil.subtract(finAssetIntInfo.getTradeAmt(), finAssetIntInfo.getIntAmt()));
            deal.setPrinAmt(ArithmeticUtil.subtract(finAssetIntInfo.getTradeAmt(), finAssetIntInfo.getIntAmt()));
        	//update by  zhangnian 2020/03/17  start
            // 还本那笔到账金额多减了通道费(预期本金不减通道费)，前台改动较多，后台add回来!
            if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())
            &&StringUtil.isNotEmpty(finAssetIntInfo.getIntAmt())&&StringUtil.isNotEmpty(finAssetIntInfo.getChannelFee())){
                deal.setTradeAmt(ArithmeticUtil.doAdd(deal.getTradeAmt(), finAssetIntInfo.getChannelFee()));
                deal.setPrinAmt(ArithmeticUtil.doAdd(deal.getPrinAmt(), finAssetIntInfo.getChannelFee()));
            }
            //update by  zhangnian 2020/03/17  end
        }
        // 一体化对应etliu start
        deal.setFpriceAmt(deal.getTradeAmt());
        // 一体化对应etliu end
        // 投资目的
//        投资目的为999999 登入卖空到交易
        if(StringUtil.equals(finAssetIntInfo.getInvAim(),"999999")){
             deal.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
        }
        else{
            deal.setInvAim(finAssetIntInfo.getInvAim());
        }
        deal.setTradeMarket(entity.getTradeMarket());
        deal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        deal.setCashId(cashIds.get(i));
        deal.setIsCancel(DictCodeList.是否_否.getValue());
        deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        deal.setWorkflowId(entity.getWorkflowId());
        deal.setAccrNum(finAssetIntInfo.getAmount());
        deal.setIsClean(entity.getIsClean());
        return deal;
    }

    /**
     * 2.0改造生成汇总交易entity <功能详细描述>
     * 
     * @param entity 源entity
     * @return 目标entity
     * @see
     * @since 1.0
     */
    public TrdFinProductDealTotal gen汇总流程表2(TrdBondDurationDto entity) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        // trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        if (!entity.isChannel()) {
            trdFinProductDealTotal.setSecManageAcctId(entity.getSecManageAcctId());
        }
        else {
            trdFinProductDealTotal.setChlId("999999".equals(entity.getSecManageAcctId()) ? null : entity.getSecManageAcctId());
        }
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setPs(AutoPeriodCommonService.change现金流类型To交易方向(entity.getCashType()));
        trdFinProductDealTotal.setBranch(
            AutoPeriodCommonService.changePsToBranch(entity.getmDate(), trdFinProductDealTotal.getPs()));
        //trdFinProductDealTotal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        trdFinProductDealTotal.setTradeDate(DateUtil.parse(DateUtil.getSysDate()));
        trdFinProductDealTotal.setVdate(DateUtil.parse(entity.getvDate()));
        trdFinProductDealTotal.setMdate(DateUtil.parse(entity.getmDate()));
        trdFinProductDealTotal.setSettleDate(DateUtil.parse(entity.getTradeDate()));
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.divide(entity.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        trdFinProductDealTotal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt()));
        trdFinProductDealTotal.setIntAmt(entity.getIntAmt());
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        //update zhangweiqi 存续期通道费修改  2019-10-15 begin
        if(entity.getTradeAmtReal()!=null){
            trdFinProductDealTotal.setFpriceAmt(entity.getTradeAmtReal());
            trdFinProductDealTotal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmtReal(), entity.getIntAmt()));
        }
        if(entity.getChannelFee()!=null){
        	trdFinProductDealTotal.setFeeAmt(entity.getChannelFee());	
        	if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
        		trdFinProductDealTotal.setFeeAmtPay(entity.getChannelFee()==null?BigDecimal.ZERO:entity.getChannelFee());
        	}else{
        		trdFinProductDealTotal.setFeeAmtUnpay(entity.getChannelFee()==null?BigDecimal.ZERO:entity.getChannelFee());
        	}
        }//update zhangweiqi 存续期通道费修改  2019-10-15 end
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsClean(entity.getIsClean());
        //多级法人改造 add by caiqun 20200615
        if(MultilevelDataAuthorityUtil.isEnabled()){
            // 机构代码
            trdFinProductDealTotal.setInstitutionCode(MultilevelDataAuthorityUtil.getOrganizationCode(entity.getCreateUser()));
        }
        //多级法人改造 add by caiqun 20200615
        return trdFinProductDealTotal;
    }

    /**
     * 2.0改造生成汇总扩展交易entity <功能详细描述>
     * 
     * @param entity 业务entity
     * @param trdFinProductDealTotal 汇总entity
     * @return 目标entity
     * @see
     * @since 1.0
     */
    private PaTrdFinProductDealTotal gen汇总扩展业务流程2(TrdBondDurationDto entity, TrdFinProductDealTotal trdFinProductDealTotal) {
        PaTrdFinProductDealTotal wflPaTrdFinDealTotal = new PaTrdFinProductDealTotal();
        wflPaTrdFinDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        wflPaTrdFinDealTotal.setUnitInt(entity.getUnitIntAmt());
        wflPaTrdFinDealTotal.setUnitPrin(entity.getIntPrinAmt());
        wflPaTrdFinDealTotal.setPrin(entity.getPrinAmt());
        wflPaTrdFinDealTotal.setRate(ArithmeticUtil.divide(entity.getYield(), 100, 5));
        wflPaTrdFinDealTotal.setPaBussType(entity.getPaBussType());
        wflPaTrdFinDealTotal.setTheoryAmt(entity.getFpriceAmt());
        wflPaTrdFinDealTotal.setTheoryPayDate(DateUtil.parse(entity.getPayDate()));
        wflPaTrdFinDealTotal.setTheoryInt(entity.getIntAmtTheory());
        wflPaTrdFinDealTotal.setTheoryPrin(entity.getPrinAmtTheory());
        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
        return wflPaTrdFinDealTotal;
    }

    /**
     * 2.0根据利息明细生成汇总交易及扩展 <功能详细描述>
     * 
     * @param finAssetIntInfo 利息明细
     * @param entity entity
     * @param baseTradeInfoDto 交易dto
     * @see
     * @since 1.0
     */
    private void gen利息明细汇总表和扩展表(FinAssetIntInfo finAssetIntInfo, TrdBondDurationDto entity, BaseTradeInfoDto baseTradeInfoDto) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(
            autoPeriodCommonService.buildBussinessCode(new BussinessCodeParameter(DictCodeList.共通业务类型_资产存续期交易)));
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setIsClean(entity.getIsClean());
        // trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        if (!entity.isChannel()) {
            trdFinProductDealTotal.setSecManageAcctId(entity.getSecManageAcctId());
        }
        else {
            trdFinProductDealTotal.setChlId("999999".equals(entity.getSecManageAcctId()) ? null : entity.getSecManageAcctId());
        }
        trdFinProductDealTotal.setCcy(entity.getCcy());
        // 投资目的
        if(StringUtil.equals(finAssetIntInfo.getInvAim(),"999999")){
            trdFinProductDealTotal.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
        }
        else{
            trdFinProductDealTotal.setInvAim(finAssetIntInfo.getInvAim());
        }
        trdFinProductDealTotal.setPs(AutoPeriodCommonService.change现金流类型To交易方向(entity.getCashType()));
        trdFinProductDealTotal.setBranch(
            AutoPeriodCommonService.changePsToBranch(entity.getmDate(), trdFinProductDealTotal.getPs()));
        //trdFinProductDealTotal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        trdFinProductDealTotal.setTradeDate(DateUtil.parse(DateUtil.getSysDate()));
        trdFinProductDealTotal.setVdate(DateUtil.parse(entity.getvDate()));
        trdFinProductDealTotal.setMdate(DateUtil.parse(entity.getmDate()));
        trdFinProductDealTotal.setSettleDate(DateUtil.parse(entity.getTradeDate()));
        trdFinProductDealTotal.setPortfolioId(finAssetIntInfo.getPortfolioId());
        // 利息明细信息.持有面额/画面.还本面额
        // 利息明细信息.实收本息总额-利息明细信息.实收利息
        // 利息明细信息.实收利息
        // 利息明细信息.实收本息总额
        // 利息明细信息.实收本息总额
        trdFinProductDealTotal.setShareAmt(
            ArithmeticUtil.divide(finAssetIntInfo.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        trdFinProductDealTotal.setPrinAmt(ArithmeticUtil.subtract(finAssetIntInfo.getTradeAmt(), entity.getIntAmt()));
        trdFinProductDealTotal.setIntAmt(finAssetIntInfo.getIntAmt());
        trdFinProductDealTotal.setTradeAmt(finAssetIntInfo.getTradeAmt());
        //update zhangweiqi 通道费修改 2019-10-15 begin
        if(entity.isChannel()&&finAssetIntInfo.getTradeAmt()!=null&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
        	trdFinProductDealTotal.setFpriceAmt(finAssetIntInfo.getTradeAmt().add(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee()));
        	 trdFinProductDealTotal.setPrinAmt(ArithmeticUtil.subtract(finAssetIntInfo.getFpriceAmt(), entity.getIntAmt()));
        }else{
        	 trdFinProductDealTotal.setFpriceAmt(finAssetIntInfo.getTradeAmt());
        }
        if(entity.getChannelFee()!=null){
        	if(entity.isChannel()&&finAssetIntInfo.getTradeAmt()!=null&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
        		trdFinProductDealTotal.setFeeAmtPay(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee());
        	}else{
        		trdFinProductDealTotal.setFeeAmtUnpay(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee());
        	}
        	trdFinProductDealTotal.setFeeAmt(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee());
        	
        }
      //update zhangweiqi 通道费修改 2019-10-15 end
       
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setpTotleTradeId(entity.getBusiCode());
        if (!entity.isChannel()) {
            trdFinProductDealTotal.setSecManageAcctId(entity.getSecManageAcctId());
        }
        else {
            trdFinProductDealTotal.setChlId("999999".equals(entity.getSecManageAcctId()) ? null : entity.getSecManageAcctId());
        }
        //多级法人改造 add by caiqun 20200708
        trdFinProductDealTotal.setInstitutionCode(MultilevelDataAuthorityUtil.getOrganizationCode(entity.getCreateUser())); // 机构代码
        //多级法人改造 add by caiqun 20200708
        baseTradeInfoDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        paTrdFinProductDealTotal.setUnitInt(entity.getUnitIntAmt());
        paTrdFinProductDealTotal.setUnitPrin(entity.getIntPrinAmt());
        paTrdFinProductDealTotal.setPrin(entity.getPrinAmt());
        paTrdFinProductDealTotal.setRate(ArithmeticUtil.divide(entity.getYield(), 100, 5));
        paTrdFinProductDealTotal.setPaBussType(entity.getPaBussType());
        // 利息明细信息.理论本息总额
        paTrdFinProductDealTotal.setTheoryAmt(finAssetIntInfo.getFpriceAmt());
        paTrdFinProductDealTotal.setTheoryPayDate(DateUtil.parse(entity.getPayDate()));
        // 利息明细信息.理论应收利息
        // 利息明细信息.还本金额
        paTrdFinProductDealTotal.setTheoryInt(finAssetIntInfo.getIntAmtTheory());
        paTrdFinProductDealTotal.setTheoryPrin(finAssetIntInfo.getPrinAmtTheory());
        paTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        paTrdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        baseTradeInfoDto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
    }

    /**
     * 债券存续期交易 构造将要登录的业务流程表数据
     *
     * @param entity 参数dto
     * @see
     * @since 1.0
     */
    public void genBondDataForInsertDto(TrdBondDurationDto entity) {
        // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        WflTrdFinProductDealTotal trdFinProductDealTotal = gen汇总流程表(entity);

        // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = gen汇总扩展业务流程(entity, trdFinProductDealTotal);

        // 金融产品交易Entity -> 金融产品交易业务流程表
        List<WflTrdProductDeal> wflTrdProductDeals = gen交易业务流程(entity);

        List<WflTrdProductDealAdd> wflTrdProductDealAdds = gen交易附属表(wflTrdProductDeals, entity);

        List<PtlTrade> ptlTrades = gen组合交易(entity, wflTrdProductDeals);

        TrdBondDurDbInertDto trdBondDurDbInertDto = new TrdBondDurDbInertDto();
        trdBondDurDbInertDto.setWflTrdFinProductDealTotal(trdFinProductDealTotal);
        trdBondDurDbInertDto.setWflPaTrdFinDealTotal(wflPaTrdFinDealTotal);
        trdBondDurDbInertDto.setWflTrdProductDealList(wflTrdProductDeals);
        trdBondDurDbInertDto.setWflTrdProductDealAdds(wflTrdProductDealAdds);
        trdBondDurDbInertDto.setPtlTrades(ptlTrades);
        entity.setTrdBondDurDbInertDto(trdBondDurDbInertDto);
    }

    /**
     * 债券存续期交易 构造将要登录的业务流程表数据
     *
     * @param entity 参数dto
     * @see
     * @since 1.0
     */
    public void genBondDataForInsertDtoNew(TrdBondDurationDto entity) {
        // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
        WflTrdFinProductDealTotal trdFinProductDealTotal = genNew汇总流程表(entity);

        // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = gen汇总扩展业务流程(entity, trdFinProductDealTotal);

//        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
//        List<WflStlPayOrders> wflStlPayOrdersList = new ArrayList<WflStlPayOrders>();
//        WflStlPayOrders stlPayOrdersDto = genNew划款指令流程(entity,trdFinProductDealTotal);
//        wflStlPayOrdersList.add(stlPayOrdersDto);

        TrdBondDurDbInertDto trdBondDurDbInertTemp = genNew交易业务流程(entity);
//        // 金融产品交易Entity -> 金融产品交易业务流程表
//        List<WflTrdProductDeal> wflTrdProductDeals = genNew交易业务流程(entity);
//
//        List<WflTrdProductDealAdd> wflTrdProductDealAdds = genNew交易附属表(wflTrdProductDeals, entity);
//
//        List<PtlTrade> ptlTrades = gen组合交易(entity, wflTrdProductDeals);

        TrdBondDurDbInertDto trdBondDurDbInertDto = new TrdBondDurDbInertDto();
        trdBondDurDbInertDto.setWflTrdFinProductDealTotal(trdFinProductDealTotal);
        trdBondDurDbInertDto.setWflPaTrdFinDealTotal(wflPaTrdFinDealTotal);
        trdBondDurDbInertDto.setWflTrdProductDealList(trdBondDurDbInertTemp.getWflTrdProductDealList());
        trdBondDurDbInertDto.setWflTrdProductDealAdds(trdBondDurDbInertTemp.getWflTrdProductDealAdds());
        trdBondDurDbInertDto.setWflStlPayOrdersList(trdBondDurDbInertTemp.getWflStlPayOrdersList());
//        trdBondDurDbInertDto.setPtlTrades(ptlTrades);
        entity.setTrdBondDurDbInertDto(trdBondDurDbInertDto);
    }

    private List<WflTrdProductDealAdd> gen交易附属表(List<WflTrdProductDeal> wflTrdProductDeals, TrdBondDurationDto entity) {
        List<WflTrdProductDealAdd> result = new ArrayList<>();
        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
            WflTrdProductDealAdd add = new WflTrdProductDealAdd();
            add.setPortfolioId(entity.getPortfolioId());
            add.setProdId(entity.getFinprodId());
            add.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
            add.setWorkflowId(entity.getWorkflowId());
            add.setTradeId(wflTrdProductDeal.getTradeId());
            add.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            add.setStatus(DictCodeList.数据状态_新建.getValue());
            result.add(add);
        });
        return result;
    }

    /**
     * 为调用共通组件组装的entity
     *
     * @param entity
     * @return
     */
    public static TrdProductDeal getTrdProductDeal(TrdBondDurationDto entity) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(entity.getBusiCode());
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        trdProductDeal.setFinprodId(entity.getFinprodId());
        // trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        if (!entity.isChannel()) {
            trdProductDeal.setSecManageAcctId(entity.getSecManageAcctId());
        }
        else {
            trdProductDeal.setChlId("999999".equals(entity.getSecManageAcctId()) ? null : entity.getSecManageAcctId());
        }
        trdProductDeal.setCcy(entity.getCcy());
        String ps = AutoPeriodCommonService.change现金流类型To交易方向(entity.getCashType());
        trdProductDeal.setPs(ps);
        trdProductDeal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        trdProductDeal.setVdate(DateUtil.parse(entity.getvDate()));
        trdProductDeal.setMdate(DateUtil.parse(entity.getmDate()));
        trdProductDeal.setSettleDate(DateUtil.parse(entity.getTradeDate()));
        trdProductDeal.setShareAmt(ArithmeticUtil.divide(entity.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        trdProductDeal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt()));
        trdProductDeal.setIntAmt(entity.getIntAmt());
        trdProductDeal.setTradeAmt(entity.getTradeAmt());
        trdProductDeal.setTradeMarket(entity.getTradeMarket());

        // 一体化对应etliu start
        trdProductDeal.setFpriceAmt(trdProductDeal.getTradeAmt());
        // 一体化对应etliu end

        return trdProductDeal;
    }

    public WflTrdFinProductDealTotal gen汇总流程表(TrdBondDurationDto entity) {
        WflTrdFinProductDealTotal trdFinProductDealTotal = new WflTrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        if (!entity.isChannel()) {
            trdFinProductDealTotal.setSecManageAcctId(entity.getSecManageAcctId());
        }
        else {
            trdFinProductDealTotal.setChlId("999999".equals(entity.getSecManageAcctId()) ? null : entity.getSecManageAcctId());
        }
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setPs(AutoPeriodCommonService.change现金流类型To交易方向(entity.getCashType()));
        trdFinProductDealTotal.setBranch(
            AutoPeriodCommonService.changePsToBranch(entity.getmDate(), trdFinProductDealTotal.getPs()));
        trdFinProductDealTotal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        trdFinProductDealTotal.setVdate(DateUtil.parse(entity.getvDate()));
        trdFinProductDealTotal.setMdate(DateUtil.parse(entity.getmDate()));
        trdFinProductDealTotal.setSettleDate(DateUtil.parse(entity.getTradeDate()));
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.divide(entity.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmtTheory()));
        trdFinProductDealTotal.setIntAmt(entity.getIntAmtTheory());
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setTgBusiType(AutoPeriodCommonService.change交易方向To投管类型(trdFinProductDealTotal.getPs()));
        trdFinProductDealTotal.setIsClean(DictCodeList.是否_是.getValue());
        trdFinProductDealTotal.setFpriceAmt(trdFinProductDealTotal.getTradeAmt());
        trdFinProductDealTotal.setTradeMarket(entity.getTradeMarket());
        trdFinProductDealTotal.setTradeMarketMode(BussinessDictCodeList.交易市场_二级市场.getValue());
        trdFinProductDealTotal.setCounterId(entity.getIssuer());
        return trdFinProductDealTotal;
    }

    private List<WflTrdProductDeal> gen交易业务流程(TrdBondDurationDto entity) {
        List<String> cashIds = StringUtil.splitToList(",", entity.getCashId());
        List<String> cashTypes = StringUtil.splitToList(",", entity.getCashType());
        List<WflTrdProductDeal> wflTrdProductDeals = new ArrayList<>();
        for (int i = 0; i < cashIds.size(); i++ ) {
            String ps = AutoPeriodCommonService.change现金流类型To交易方向(cashTypes.get(i));
            if (DictCodeList.交易方向_收息.getValue().equals(ps)) {
                生成交易(entity, cashIds, DictCodeList.交易方向_收息.getValue(), wflTrdProductDeals, i);
            }
            else if (DictCodeList.交易方向_还本.getValue().equals(ps) || DictCodeList.交易方向_到期.getValue().equals(ps)) {
                生成交易(entity, cashIds, ps, wflTrdProductDeals, i);
                if (entity.getIntAmt() != null && entity.getIntAmt().compareTo(BigDecimal.ZERO) > 0 && cashIds.size() == 1) {
                    生成交易(entity, cashIds, DictCodeList.交易方向_收息.getValue(), wflTrdProductDeals, i);
                }
            }
        }
        BigDecimal allPrinAmt = BigDecimal.ZERO;
        BigDecimal allIntAmt = BigDecimal.ZERO;
        BigDecimal maxPrinAmt = BigDecimal.ZERO;
        BigDecimal maxIntAmt = BigDecimal.ZERO;
        int maxPrinAmtNum = 0;
        int maxIntAmtNum = 0;
        for (int j = 0; j < wflTrdProductDeals.size(); j++ ) {
            if (DictCodeList.交易方向_收息到账.getValue().equals(wflTrdProductDeals.get(j).getPs())
                && wflTrdProductDeals.get(j).getIntAmt().compareTo(maxIntAmt) > 0) {
                maxIntAmtNum = j;
                maxIntAmt = wflTrdProductDeals.get(j).getIntAmt();
            }
            else if ((DictCodeList.交易方向_还本到账.getValue().equals(wflTrdProductDeals.get(j).getPs()))
                     && wflTrdProductDeals.get(j).getPrinAmt().compareTo(maxPrinAmt) > 0) {
                maxPrinAmtNum = j;
                maxPrinAmt = wflTrdProductDeals.get(j).getPrinAmt();
            }
        }

        for (int j = 0; j < wflTrdProductDeals.size(); j++ ) {
            if (DictCodeList.交易方向_收息到账.getValue().equals(wflTrdProductDeals.get(j).getPs()) && j != maxIntAmtNum) {
                allIntAmt = ArithmeticUtil.doAdd(allIntAmt, wflTrdProductDeals.get(j).getIntAmt());
            }
            else if ((DictCodeList.交易方向_还本到账.getValue().equals(wflTrdProductDeals.get(j).getPs()))
                     && j != maxPrinAmtNum) {
                allPrinAmt = ArithmeticUtil.doAdd(allPrinAmt, wflTrdProductDeals.get(j).getPrinAmt());
            }
        }
        if (wflTrdProductDeals != null && !wflTrdProductDeals.isEmpty()) {
            wflTrdProductDeals.get(maxPrinAmtNum).setPrinAmt(
                    ArithmeticUtil.subtract(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt()), allPrinAmt));
            wflTrdProductDeals.get(maxIntAmtNum).setIntAmt(ArithmeticUtil.subtract(entity.getIntAmt(), allIntAmt));
        }
        return wflTrdProductDeals;
    }

    private WflPaTrdFinDealTotal gen汇总扩展业务流程(TrdBondDurationDto entity, WflTrdFinProductDealTotal trdFinProductDealTotal) {
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        wflPaTrdFinDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        wflPaTrdFinDealTotal.setUnitInt(entity.getUnitIntAmt());
        wflPaTrdFinDealTotal.setUnitPrin(entity.getIntPrinAmt());
        wflPaTrdFinDealTotal.setPrin(entity.getPrinAmt());
        wflPaTrdFinDealTotal.setRate(ArithmeticUtil.divide(entity.getYield(), 100, 5));
        wflPaTrdFinDealTotal.setPaBussType(entity.getPaBussType());
        wflPaTrdFinDealTotal.setTheoryAmt(entity.getFpriceAmt());
        wflPaTrdFinDealTotal.setTheoryPayDate(DateUtil.parse(entity.getPayDate()));
        wflPaTrdFinDealTotal.setTheoryInt(entity.getIntAmtTheory());
        wflPaTrdFinDealTotal.setTheoryPrin(entity.getPrinAmtTheory());
        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflPaTrdFinDealTotal.setPaBussType(trdFinProductDealTotal.getTgBusiType());
        return wflPaTrdFinDealTotal;
    }

    public WflTrdFinProductDealTotal genNew汇总流程表(TrdBondDurationDto entity) {
        WflTrdFinProductDealTotal trdFinProductDealTotal = new WflTrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        if (!entity.isChannel()) {
            trdFinProductDealTotal.setSecManageAcctId(entity.getSecManageAcctId());
        }
        else {
            trdFinProductDealTotal.setChlId("999999".equals(entity.getSecManageAcctId()) ? null : entity.getSecManageAcctId());
        }
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setPs(AutoPeriodCommonService.change现金流类型To交易方向(entity.getCashType()));
        trdFinProductDealTotal.setBranch(
                AutoPeriodCommonService.changePsToBranch(entity.getmDate(), trdFinProductDealTotal.getPs()));
        trdFinProductDealTotal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        trdFinProductDealTotal.setVdate(DateUtil.parse(entity.getvDate()));
        trdFinProductDealTotal.setMdate(DateUtil.parse(entity.getmDate()));
        trdFinProductDealTotal.setSettleDate(DateUtil.parse(entity.getPayDate()));
        trdFinProductDealTotal.setShareAmt(ArithmeticUtil.divide(entity.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmtTheory()));
        trdFinProductDealTotal.setIntAmt(entity.getIntAmtTheory());
        if(StringUtil.equals(trdFinProductDealTotal.getPs(),DictCodeList.交易方向_收息.getValue())){
            trdFinProductDealTotal.setTradeAmt(trdFinProductDealTotal.getIntAmt());
        }else{
            trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        }
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setTgBusiType(AutoPeriodCommonService.change交易方向To投管类型(trdFinProductDealTotal.getPs()));
        trdFinProductDealTotal.setIsClean(DictCodeList.是否_是.getValue());
        trdFinProductDealTotal.setFpriceAmt(trdFinProductDealTotal.getTradeAmt());
        trdFinProductDealTotal.setTradeMarket(entity.getTradeMarket());
        trdFinProductDealTotal.setTradeMarketMode(BussinessDictCodeList.交易市场_二级市场.getValue());
        trdFinProductDealTotal.setCounterId(entity.getIssuer());
        return trdFinProductDealTotal;
    }

    private TrdBondDurDbInertDto genNew交易业务流程(TrdBondDurationDto entity) {

        TrdBondDurDbInertDto trdBondDurDbInertTemp =new TrdBondDurDbInertDto();

        List<String> cashIds = StringUtil.splitToList(",", entity.getCashId());
        List<String> cashTypes = StringUtil.splitToList(",", entity.getCashType());
        String ps;
        if(cashTypes.contains(DictCodeList.现金流类型_中间还本.getValue())){
            ps = DictCodeList.交易方向_还本.getValue();
        }else if(cashTypes.contains(DictCodeList.现金流类型_到期还本.getValue())){
            ps = DictCodeList.交易方向_到期.getValue();
        }else{
            ps = DictCodeList.交易方向_收息.getValue();
        }
        //组装还本金额、理论应收利息、实际应收利息
        //汇总券面总额
        BigDecimal sumPrinAmt = entity.getQty();
        //汇总应计利息
        BigDecimal sumIntAmt = entity.getIntAmtTheory();
        //汇总券面总额
        BigDecimal sumPrimAmt = ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmtTheory());
        if(StringUtil.equals(ps,DictCodeList.交易方向_收息.getValue())){
            sumPrimAmt = BigDecimal.ZERO;
        }
        //已使用应计利息
        BigDecimal usedIntAmtSum = BigDecimal.ZERO;
        //已占用券面总额
        BigDecimal usedPrimAmtSum = BigDecimal.ZERO;

        List<WflTrdProductDeal> wflTrdProductDeals = new ArrayList<>();
        List<WflTrdProductDealAdd> wflTrdProductDealAdds = new ArrayList<>();
        List<PtlTrade> ptlTradeList = new ArrayList<>();
        List<WflStlPayOrders> wflStlPayOrdersList = new ArrayList<>();

        for (int i = 0; i < entity.getFinAssetIntInfoList().size(); i++ ) {
            if (i == entity.getFinAssetIntInfoList().size() - 1) {
                //最后一行倒减
                entity.getFinAssetIntInfoList().get(i).setIntAmtTheory(ArithmeticUtil.subtract(sumIntAmt,usedIntAmtSum));
                entity.getFinAssetIntInfoList().get(i).setPrinAmt(ArithmeticUtil.subtract(sumPrimAmt,usedPrimAmtSum));
            }else{
                //非最后一行按照占比计算应计利息
                BigDecimal prinAmt = ArithmeticUtil.multiply(entity.getFinAssetIntInfoList().get(i).getAmount(),new BigDecimal(100));
                //比例
                BigDecimal rate = ArithmeticUtil.divide(prinAmt,sumPrinAmt,12);
                BigDecimal intAmtTheory = ArithmeticUtil.round(ArithmeticUtil.multiply(sumIntAmt,rate),2,RoundingMode.HALF_UP);
                usedIntAmtSum = ArithmeticUtil.doAdd(usedIntAmtSum,intAmtTheory);

                BigDecimal intPrimTheory = ArithmeticUtil.round(ArithmeticUtil.multiply(sumPrimAmt,rate),2,RoundingMode.HALF_UP);
                usedPrimAmtSum = ArithmeticUtil.doAdd(usedPrimAmtSum,intPrimTheory);

                entity.getFinAssetIntInfoList().get(i).setIntAmtTheory(intAmtTheory);
                entity.getFinAssetIntInfoList().get(i).setPrinAmt(intPrimTheory);
            }
            生成交易New(entity, ps, trdBondDurDbInertTemp, 0, entity.getFinAssetIntInfoList().get(i), wflTrdProductDeals,
                    wflTrdProductDealAdds, ptlTradeList, wflStlPayOrdersList);
        }
//        for(FinAssetIntInfo finAssetIntInfo : entity.getFinAssetIntInfoList()){
//
//            //生成交易New(entity, cashIds, ps , wflTrdProductDeals, 0);
//            生成交易New(entity, cashIds, ps , trdBondDurDbInertTemp, 0,finAssetIntInfo);
//        }
//        for (int i = 0; i < cashIds.size(); i++ ) {
//            String ps = AutoPeriodCommonService.change现金流类型To交易方向(cashTypes.get(i));
//            生成交易New(entity, cashIds, ps , wflTrdProductDeals, i);
//            if (DictCodeList.交易方向_收息.getValue().equals(ps)) {
//                生成交易New(entity, cashIds, DictCodeList.交易方向_收息.getValue(), wflTrdProductDeals, i);
//            }
//            else if (DictCodeList.交易方向_还本.getValue().equals(ps) || DictCodeList.交易方向_到期.getValue().equals(ps)) {
//                生成交易New(entity, cashIds, ps, wflTrdProductDeals, i);
//                if (entity.getIntAmt() != null && entity.getIntAmt().compareTo(BigDecimal.ZERO) > 0 && cashIds.size() == 1) {
//                    生成交易(entity, cashIds, DictCodeList.交易方向_收息.getValue(), wflTrdProductDeals, i);
//                }
//            }
//        }
//        BigDecimal allPrinAmt = BigDecimal.ZERO;
//        BigDecimal allIntAmt = BigDecimal.ZERO;
//        BigDecimal maxPrinAmt = BigDecimal.ZERO;
//        BigDecimal maxIntAmt = BigDecimal.ZERO;
//        int maxPrinAmtNum = 0;
//        int maxIntAmtNum = 0;
//        for (int j = 0; j < wflTrdProductDeals.size(); j++ ) {
//            if (DictCodeList.交易方向_收息到账.getValue().equals(wflTrdProductDeals.get(j).getPs())
//                    && wflTrdProductDeals.get(j).getIntAmt().compareTo(maxIntAmt) > 0) {
//                maxIntAmtNum = j;
//                maxIntAmt = wflTrdProductDeals.get(j).getIntAmt();
//            }
//            else if ((DictCodeList.交易方向_还本到账.getValue().equals(wflTrdProductDeals.get(j).getPs()))
//                    && wflTrdProductDeals.get(j).getPrinAmt().compareTo(maxPrinAmt) > 0) {
//                maxPrinAmtNum = j;
//                maxPrinAmt = wflTrdProductDeals.get(j).getPrinAmt();
//            }
//        }
//
//        for (int j = 0; j < wflTrdProductDeals.size(); j++ ) {
//            if (DictCodeList.交易方向_收息到账.getValue().equals(wflTrdProductDeals.get(j).getPs()) && j != maxIntAmtNum) {
//                allIntAmt = ArithmeticUtil.doAdd(allIntAmt, wflTrdProductDeals.get(j).getIntAmt());
//            }
//            else if ((DictCodeList.交易方向_还本到账.getValue().equals(wflTrdProductDeals.get(j).getPs()))
//                    && j != maxPrinAmtNum) {
//                allPrinAmt = ArithmeticUtil.doAdd(allPrinAmt, wflTrdProductDeals.get(j).getPrinAmt());
//            }
//        }
//        if (wflTrdProductDeals != null && !wflTrdProductDeals.isEmpty()) {
//            wflTrdProductDeals.get(maxPrinAmtNum).setPrinAmt(
//                    ArithmeticUtil.subtract(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt()), allPrinAmt));
//            wflTrdProductDeals.get(maxIntAmtNum).setIntAmt(ArithmeticUtil.subtract(entity.getIntAmt(), allIntAmt));
//        }

        return trdBondDurDbInertTemp;
    }

    private void 生成交易New(TrdBondDurationDto entity, String 交易方向, TrdBondDurDbInertDto trdBondDurDbInertTemp,
                         int i, FinAssetIntInfo finAssetIntInfo, List<WflTrdProductDeal> wflTrdProductDeals,
                         List<WflTrdProductDealAdd> wflTrdProductDealAdds, List<PtlTrade> ptlTradeList, List<WflStlPayOrders> wflStlPayOrdersList) {

        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        WflTrdProductDeal deal = new WflTrdProductDeal();
        deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
        deal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        deal.setFinprodId(entity.getFinprodId());
        deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        deal.setBranch(AutoPeriodCommonService.changePsToBranch(entity.getmDate(), 交易方向));
        deal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        deal.setVdate(DateUtil.parse(entity.getvDate()));
        deal.setMdate(DateUtil.parse(entity.getmDate()));
        deal.setSettleDate(deal.getTradeDate());
        deal.setCcy(entity.getCcy());
        deal.setShareAmt(finAssetIntInfo.getAmount());
        //TODO
        deal.setIntAmt(finAssetIntInfo.getIntAmtTheory());
        deal.setPrinAmt(finAssetIntInfo.getPrinAmt());
        deal.setFpriceAmt(deal.getPrinAmt().add(deal.getIntAmt()));
//        if(!StringUtil.equals(交易方向,DictCodeList.交易方向_付息.getValue())){
//            deal.setFpriceAmt(deal.getPrinAmt().add(deal.getIntAmt()));
//        }else{
//            deal.setFpriceAmt(deal.getIntAmt());
//        }
        deal.setTradeAmt(deal.getFpriceAmt());

        deal.setPs(交易方向);
        deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        deal.setIsCancel(DictCodeList.是否_否.getValue());
        deal.setIsClean(DictCodeList.是否_是.getValue());
        deal.setTotleTradeId(entity.getBusiCode());
        deal.setTradeMarket(entity.getTradeMarket());
        deal.setSecManageAcctId(entity.getSecManageAcctId());
        deal.setAccrNum(finAssetIntInfo.getAmount());

        WflTrdProductDealAdd dealadd = new WflTrdProductDealAdd();
        dealadd.setTradeId(deal.getTradeId());
        dealadd.setPortfolioId(finAssetIntInfo.getPortfolioId());
        dealadd.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
        dealadd.setWorkflowId(deal.getWorkflowId());
        dealadd.setStatus(DictCodeList.数据状态_新建.getValue());
        dealadd.setOurShareAmt(deal.getShareAmt());
        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        WflStlPayOrders stlPayOrdersDto = genNew划款指令流程(entity, deal, dealadd);
        wflStlPayOrdersList.add(stlPayOrdersDto);

        PtlTrade ptlTrade2 = new PtlTrade();
        ptlTrade2.setPortfolioId(dealadd.getPortfolioId());
        ptlTrade2.setBusiNo(deal.getTradeId());
        ptlTrade2.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade2.setInvAim(deal.getInvAim());
        ptlTrade2.setBusiId(entity.getFinprodId());
        ptlTrade2.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        ptlTrade2.setSecManageAcctId(entity.getSecManageAcctId());
        ptlTrade2.setSettleDate(DateUtil.parse(entity.getTradeDate()));

        wflTrdProductDeals.add(deal);
        wflTrdProductDealAdds.add(dealadd);
        ptlTradeList.add(ptlTrade2);

        trdBondDurDbInertTemp.setWflTrdProductDealList(wflTrdProductDeals);
        trdBondDurDbInertTemp.setWflTrdProductDealAdds(wflTrdProductDealAdds);
        trdBondDurDbInertTemp.setPtlTrades(ptlTradeList);
        trdBondDurDbInertTemp.setWflStlPayOrdersList(wflStlPayOrdersList);
    }

    private List<WflTrdProductDealAdd> genNew交易附属表(List<WflTrdProductDeal> wflTrdProductDeals, TrdBondDurationDto entity) {
        List<WflTrdProductDealAdd> result = new ArrayList<>();
        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
            WflTrdProductDealAdd add = new WflTrdProductDealAdd();
            add.setPortfolioId(entity.getPortfolioId());
            add.setProdId(entity.getFinprodId());
            add.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
            add.setWorkflowId(entity.getWorkflowId());
            add.setTradeId(wflTrdProductDeal.getTradeId());
            add.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            add.setStatus(DictCodeList.数据状态_新建.getValue());
            result.add(add);
        });
        return result;
    }

    /**
     * 生成划款指令
     *
     * @param entity 交易数据
     * @return 划款指令
     * @see
     * @since 1.0
     */
    private WflStlPayOrders genNew划款指令流程(TrdBondDurationDto entity,WflTrdProductDeal trdProductDeal,WflTrdProductDealAdd trdProductDealAdd) {
        WflStlPayOrders wflstlPayOrders = new WflStlPayOrders();
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setSeqNo("9998");
        String orderId;
        if (baseGetBussinessId != null) {
            orderId = baseGetBussinessId.buildBussinessCode(bussinessCodeParameter);
        } else {
            orderId = IDGenerator.generate();
        }
        wflstlPayOrders.setOrderId(orderId);
        wflstlPayOrders.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
//        String acctNo = autoPeriodCommonService.selectDealCapacctInfo(trdProductDeal.getSecManageAcctId(), trdProductDeal.getCcy());
        String acctId = EtrCommonCashPositionDealService.select组合托管户(trdProductDealAdd.getPortfolioId(),
                trdProductDeal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
        wflstlPayOrders.setMyAcctNo(acctId);
        wflstlPayOrders.setSettleDate(trdProductDeal.getSettleDate() == null ? trdProductDeal.getTradeDate() : trdProductDeal.getSettleDate());
        wflstlPayOrders.setPs(DictCodeListExt1.收支方向_收入.getValue());
        wflstlPayOrders.setSettleAmt(trdProductDeal.getTradeAmt());
        wflstlPayOrders.setCcy(trdProductDeal.getCcy());
        wflstlPayOrders.setSettleStatus(DictCodeList.结算状态_已结算.getValue());
        wflstlPayOrders.setIsNeedSend(DictCodeListExt1.是否_是.getValue());
        wflstlPayOrders.setIsTotalTrade(DictCodeListExt1.是否_是.getValue());
        wflstlPayOrders.setTradeId(trdProductDeal.getTradeId());
        wflstlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
        wflstlPayOrders.setPortfolioId(trdProductDealAdd.getPortfolioId());
        return wflstlPayOrders;
    }

    private void 生成交易(TrdBondDurationDto entity, List<String> cashIds, String 交易方向, List<WflTrdProductDeal> wflTrdProductDeals,
                      int i) {
        List<String> invAims = StringUtil.splitToList(",", entity.getInvAim());
        List<String> amountList = StringUtil.splitToList(",", entity.getAmountList());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (String s : amountList) {
            totalAmount = ArithmeticUtil.doAdd(totalAmount, new BigDecimal(s));
        }
        BigDecimal sumPrinAmt = ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt());
        for (int j = 0; j < invAims.size(); j++ ) {
            BigDecimal prinAmt = ArithmeticUtil.round(
                ArithmeticUtil.multiply(ArithmeticUtil.divide(new BigDecimal(amountList.get(j)), totalAmount, 12), sumPrinAmt), 2,
                RoundingMode.HALF_UP);
            BigDecimal intAmt = ArithmeticUtil.round(
                ArithmeticUtil.multiply(ArithmeticUtil.divide(new BigDecimal(amountList.get(j)), totalAmount, 12),
                    entity.getIntAmt()),
                2, RoundingMode.HALF_UP);
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
            WflTrdProductDeal deal = new WflTrdProductDeal();
            if (entity.getTrdBondDurDbInertDto() != null && entity.getTrdBondDurDbInertDto().getWflTrdProductDealList() != null) {
                for (WflTrdProductDeal wflTrdProductDeal : entity.getTrdBondDurDbInertDto().getWflTrdProductDealList()) {
                    if (wflTrdProductDeal.getPs().equals(交易方向) && wflTrdProductDeal.getInvAim().equals(invAims.get(j))) {
                        deal.setTradeId(wflTrdProductDeal.getTradeId());
                        return;
                    }
                }
            }
            else {
                deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
            }
            if(StringUtil.equals(invAims.get(j),"999999")){
                deal.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
            }
            else{
                deal.setInvAim(invAims.get(j));
            }
            deal.setBranch(AutoPeriodCommonService.changePsToBranch(entity.getmDate(), 交易方向));
            deal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
            deal.setTotleTradeId(entity.getBusiCode());
            deal.setFinprodId(entity.getFinprodId());
            deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            // deal.setFinprodType(); TODO 估值核算 金融产品类型
            if (!entity.isChannel()) {
                deal.setSecManageAcctId(entity.getSecManageAcctId());
            }
            else {
                deal.setChlId("999999".equals(entity.getSecManageAcctId()) ? null : entity.getSecManageAcctId());
            }
            deal.setCcy(entity.getCcy());
            deal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
            deal.setVdate(DateUtil.parse(entity.getvDate()));
            deal.setMdate(DateUtil.parse(entity.getmDate()));
            deal.setSettleDate(deal.getTradeDate());
            deal.setShareAmt(new BigDecimal(amountList.get(j)));
            deal.setStatus(交易方向);
            if (DictCodeList.交易方向_收息.getValue().equals(交易方向)) {
                deal.setIntAmt(intAmt);
                deal.setTradeAmt(intAmt);
                deal.setPs(DictCodeList.交易方向_收息到账.getValue());
            }
            else if (DictCodeList.交易方向_还本.getValue().equals(交易方向)
                     || DictCodeList.交易方向_到期.getValue().equals(交易方向)) {
                deal.setTradeAmt(prinAmt);
                deal.setPrinAmt(prinAmt);
                deal.setPs(DictCodeList.交易方向_还本到账.getValue());
            }
            deal.setTradeMarket(entity.getTradeMarket());
            deal.setCashId(cashIds.get(i));
            deal.setIsCancel(DictCodeList.是否_否.getValue());
            deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            deal.setWorkflowId(entity.getWorkflowId());
            deal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            deal.setIsClean(DictCodeList.是否_否.getValue());

            // 一体化对应etliu start
            deal.setFpriceAmt(deal.getTradeAmt());
            // 一体化对应etliu end

            wflTrdProductDeals.add(deal);
        }
        // else {
        // deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
        // }
        // deal.setBranch(AutoPeriodCommonService.changePsToBranch(entity.getmDate(), 交易方向));
        // deal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        // deal.setTotleTradeId(entity.getBusiCode());
        // deal.setFinprodId(entity.getFinprodId());
        // deal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // // deal.setFinprodType(); 估值核算 金融产品类型
        // deal.setSecManageAcctId(entity.getSecManageAcctId());
        // deal.setCcy(entity.getCcy());
        // deal.setPs(交易方向);
        // deal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        // deal.setVdate(DateUtil.parse(entity.getvDate()));
        // deal.setMdate(DateUtil.parse(entity.getmDate()));
        // deal.setSettleDate(deal.getTradeDate());
        // deal.setShareAmt(ArithmeticUtil.divide(entity.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        //
        // if (DictCodeList.交易方向_收息.getValue().equals(交易方向)) {
        // deal.setIntAmt(entity.getIntAmt());
        // deal.setTradeAmt(entity.getIntAmt());
        // } else if (DictCodeList.交易方向_还本.getValue().equals(交易方向)
        // || DictCodeList.交易方向_到期.getValue().equals(交易方向)) {
        // deal.setTradeAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt()));
        // deal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt()));
        // }
        //
        // deal.setTradeMarket(entity.getTradeMarket());
        // deal.setCashId(cashIds.get(i));
        // deal.setIsCancel(DictCodeList.是否_否.getValue());
        // deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // deal.setWorkflowId(entity.getWorkflowId());
        // deal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        // wflTrdProductDeals.add(deal);
    }

    private List<PtlTrade> gen组合交易(TrdBondDurationDto entity, List<WflTrdProductDeal> wflTrdProductDeals) {
        List<PtlTrade> result = new ArrayList<>();
        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
            PtlTrade ptlTrade2 = new PtlTrade();
            ptlTrade2.setPortfolioId(entity.getPortfolioId());
            ptlTrade2.setBusiNo(wflTrdProductDeal.getTradeId());
            ptlTrade2.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade2.setInvAim(wflTrdProductDeal.getInvAim());
            ptlTrade2.setBusiId(entity.getFinprodId());
            ptlTrade2.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
            ptlTrade2.setSecManageAcctId(entity.getSecManageAcctId());
            ptlTrade2.setSettleDate(DateUtil.parse(entity.getTradeDate()));
            result.add(ptlTrade2);
        });
        return result;
    }
    /**
     * add by zhangweiqi 生成通道费明细
     */
    private List<TrdTradeFeeDetail>  getFeeDetailList(TrdProductDeal deal,FinAssetIntInfo finAssetIntInfo,TrdBondDurationDto entity){
    	deal.setFeeAmt(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee());
    	TrdTradeFeeDetail detail=new TrdTradeFeeDetail();
    	List<TrdTradeFeeDetail> detailList=new ArrayList<TrdTradeFeeDetail>();
    	detail.setTradeId(deal.getTradeId());
    	detail.setFeeAmt(finAssetIntInfo.getChannelFee());
    	detail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
    	detail.setCcy(deal.getCcy());
    	if(entity.isChannel()&&StringUtil.equals(entity.getCollectType(),DictCodeListExt2.收取方式_回款前扣除.getValue())){
    		detail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
    		deal.setFeeAmtPay(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee());
    	}else{
    		detail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
    		deal.setFeeAmtUnpay(finAssetIntInfo.getChannelFee()==null?BigDecimal.ZERO:finAssetIntInfo.getChannelFee());
    	}
    	detail.setFeeId(DictCodeListExt1.一次性费用类型_通道费.getValue());
    	detail.setChlFinprodId(entity.getSecManageAcctId());
    	detail.setCreateUser(deal.getCreateUser());
    	detail.setCreateTime(deal.getCreateTime());
    	detail.setUpdateUser(deal.getUpdateUser());
    	detail.setUpdateTime(deal.getUpdateTime());
    	detailList.add(detail);
    	return detailList;
    }
}

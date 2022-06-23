/*
 * 文件名：AutoPeriodLendCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年12月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.autoperiod.lend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.trdlendduration.TrdLendDurationCommonDto;

/**
 * 拆借存续期共通Service 批处理自动存续期和存续期画面共用逻辑
 *
 * @author etliu
 * @version 1.0 2018年12月14日
 * @see AutoPeriodLendCommonService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class AutoPeriodLendCommonService extends BaseService {

    /**
     * AutoPeriodCommonService
     */
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    public static WflTrdFinProductDealTotal gen汇总流程表(TrdLendDurationCommonDto entity) {
        WflTrdFinProductDealTotal trdFinProductDealTotal = new WflTrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_拆借存续期交易.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        // trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
        trdFinProductDealTotal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        trdFinProductDealTotal.setFinprodType2(entity.getFinprodType2());
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_到期.getValue());
        trdFinProductDealTotal.setTradeDate(entity.getTradeDate());
        trdFinProductDealTotal.setVdate(entity.getVdate());
        trdFinProductDealTotal.setMdate(entity.getMdate());
        trdFinProductDealTotal.setSettleDate(entity.getSettleDate());
        trdFinProductDealTotal.setShareAmt(entity.getPrinAmt());
        trdFinProductDealTotal.setPrinAmt(entity.getPrinAmt());
        trdFinProductDealTotal.setIntAmt(
            ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getPrinAmt()));
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        trdFinProductDealTotal.setCounterId(entity.getCounterId());
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setTrader(entity.getTrader());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setRemark(entity.getRemark());
        return trdFinProductDealTotal;
    }

    public static WflPaTrdFinDealTotal gen汇总扩展业务流程(TrdLendDurationCommonDto entity,
                                            WflTrdFinProductDealTotal trdFinProductDealTotal) {
       WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
       wflPaTrdFinDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
       wflPaTrdFinDealTotal.setRate(ArithmeticUtil.divide(entity.getIntRate(), 100, 5));
       wflPaTrdFinDealTotal.setTheoryAmt(entity.getTheoryAmt());
        if (DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue().equals(entity.getFinprodType2())) {
            wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_特拆拆入到期.getValue());
        }
        else if (DictCodeListExt1.金融产品类型_投管用_拆入.getValue().equals(entity.getFinprodType2())) {
            wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆入到期.getValue());
        }
        else if (DictCodeListExt1.金融产品类型_投管用_拆出.getValue().equals(entity.getFinprodType2())) {
            wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆出到期.getValue());
        }
       wflPaTrdFinDealTotal.setTheoryInt(entity.getIntAmt());
       wflPaTrdFinDealTotal.setTheoryPrin(entity.getPrinAmt());
       wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
       wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
       return wflPaTrdFinDealTotal;
   }

    public List<WflTrdProductDeal> gen交易业务流程(TrdLendDurationCommonDto entity, List<WflPaTrdProductDeal> wflPaTrdProductDeals,
                                             List<WflTrdProductDealAdd> wflTrdProductDealAdds) {
        List<FinCashPlan> 现金流 = restTemplate.postForList(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/findFinCashInfo",
            entity.getFinprodId(), FinCashPlan.class);
        List<WflTrdProductDeal> deals = new ArrayList<>();
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
      //获取原交易编号
        List<TrdProductDeal> orgTrade = null;
        Map<String,String>idMap=new HashMap<>();
        if(StringUtil.equalsIgnoreCase(BussinessConstant.REVOKE, entity.getMode())) {
            orgTrade = autoPeriodCommonService.findDealByTotalId(entity.getpTradeId());
            if(CollectionUtils.isNotEmpty(orgTrade)) {
//              处理类型和交易代码的关系
                orgTrade.forEach(item->{
                    idMap.put(item.getPs(), item.getTradeId());
                });
            }
        }
        现金流.forEach(finCashPlan -> {
            WflTrdProductDeal deal = new WflTrdProductDeal();
            deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
            deal.setBusiType(DictCodeList.共通业务类型_拆借存续期交易.getValue());
            deal.setFinprodId(entity.getFinprodId());
            deal.setFinprodType2(entity.getFinprodType2());
            // deal.setFinprodType(); TODO 估值核算 金融产品类型
            deal.setCcy(entity.getCcy());
            deal.setTradeDate(entity.getSettleDate());
            deal.setVdate(entity.getVdate());
            deal.setMdate(entity.getMdate());
            deal.setSettleDate(deal.getTradeDate());
            deal.setShareAmt(entity.getPrinAmt());
            deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            deal.setIsCancel(DictCodeList.是否_否.getValue());
            deal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            deal.setWorkflowId(entity.getWorkflowId());
            deal.setTotleTradeId(entity.getTotleTradeId());
            deal.setCashId(finCashPlan.getCashId());
            deal.setCounterId(entity.getCounterId());
            deal.setTrader(entity.getTrader());
            if (DictCodeList.现金流类型_到期还本.getValue().equals(finCashPlan.getCashType())) {
                deal.setPs(DictCodeList.交易方向_到期.getValue());
                deal.setBranch(AutoPeriodCommonService.changePsToBranch(DateUtil.format(entity.getMdate()), deal.getPs()));
                deal.setPs(DictCodeList.交易方向_还本到账.getValue());
                deal.setTradeAmt(entity.getPrinAmt());
                deal.setPrinAmt(entity.getPrinAmt());
            }
            else if (DictCodeList.现金流类型_到期收息.getValue().equals(
                finCashPlan.getCashType())) {
                deal.setPs(DictCodeList.交易方向_收息.getValue());
                deal.setBranch(AutoPeriodCommonService.changePsToBranch(DateUtil.format(entity.getMdate()), deal.getPs()));
                deal.setPs(DictCodeList.交易方向_收息到账.getValue());
                deal.setIntAmt(
                    ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getPrinAmt()));
                deal.setTradeAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getPrinAmt()));
            }
            if(StringUtil.equals(BussinessConstant.REVOKE, entity.getMode())) {
//              设置原交易代码
              deal.setpTradeId(idMap.get(finCashPlan.getCashType()));
//              设置交易编号
              deal.setTradeId(idMap.get(finCashPlan.getCashType()) +'R');
              deal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
              deal.setIsCancel(DictCodeList.是否_否.getValue());
          }
          else{
              deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
              deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
              deal.setIsCancel(DictCodeList.是否_否.getValue());
          }

            WflPaTrdProductDeal paDeal = new WflPaTrdProductDeal();
            paDeal.setWorkflowId(deal.getWorkflowId());
            paDeal.setWorkflowStatus(deal.getWorkflowStatus());
            paDeal.setTradeId(deal.getTradeId());
            if (DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue().equals(entity.getFinprodType2())) {
                paDeal.setPaBussType(DictCodeListExt1.平安业务类型_特拆拆入到期.getValue());
            }
            else if (DictCodeListExt1.金融产品类型_投管用_拆入.getValue().equals(entity.getFinprodType2())) {
                paDeal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆入到期.getValue());
            }
            else if (DictCodeListExt1.金融产品类型_投管用_拆出.getValue().equals(entity.getFinprodType2())) {
                paDeal.setPaBussType(DictCodeListExt1.平安业务类型_普通拆出到期.getValue());
            }
            deals.add(deal);
            wflPaTrdProductDeals.add(paDeal);
            
            WflTrdProductDealAdd dealAdd = new WflTrdProductDealAdd();
            dealAdd.setWorkflowId(deal.getWorkflowId());
            dealAdd.setWorkflowStatus(deal.getWorkflowStatus());
            dealAdd.setTradeId(deal.getTradeId());
            dealAdd.setPortfolioId(entity.getPortfolioId());
            wflTrdProductDealAdds.add(dealAdd);
        });
        return deals;
    }
}

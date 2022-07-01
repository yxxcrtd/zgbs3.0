/*
 * 文件名：TrdCommonBaseService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.trade.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.common.dao.PeriodCommonMapper;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.TrdStaSecInfoDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonParam;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTrade;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.PreTrdCommonMapper;
import com.joyintech.tams.invest.common.trade.dao.TrdCommonBaseMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.PreTrdCommonDto;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 交易预录入共通组件Service
 *
 * @author daizhiwei
 * @version 1.0 2020年1月19日
 * @see PreTrdCommonBaseService
 * @since 1.0excutePosition
 */
@Service
public class PreTrdCommonBaseService extends BaseService {

    @Autowired
    private EtrCommonCashPositionDealService etrCommonCashPositionDealService;
    @Autowired
    private MapperDelegateService mapperDelegateService;
    @Autowired
    private PeriodCommonMapper periodCommonMapper;
    @Autowired
    private PreTrdCommonMapper preTrdCommonMapper;
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    @Autowired
    private EtrFinishStatusMapper etrFinishStatusMapper;

    //组合头寸
    public void excutePosition(PreTrdCommonDto dto, boolean navigate){
        List<TrdProductDealPre> trdProductDealPreList = dto.getTrdProductDealPreList();
        List<TrdProductDealAddPre> trdProductDealAddPreList = dto.getTrdProductDealAddPreList();
        List<PtlSecPositionFlow> positionFlows = new ArrayList<>();
        List<PtlCashPositionFlow> ptlCashPositionFlows = new ArrayList<>();
        List<EtrSecPositionFlow> etrSecPositionFlows = new ArrayList<>();
        List<EtrCashPositionFlow> etrCashPositionFlows = new ArrayList<>();
        if (!StringUtil.isEmptyList(trdProductDealPreList)){
            for (TrdProductDealPre trdProductDealPre : trdProductDealPreList){

                for (TrdProductDealAddPre trdProductDealAddPre : trdProductDealAddPreList) {
                    if (StringUtil.equals(trdProductDealAddPre.getTradeId(),trdProductDealPre.getTradeId())){
                        deal组合及指令头寸(trdProductDealPre,trdProductDealAddPre,positionFlows,ptlCashPositionFlows,etrSecPositionFlows,etrCashPositionFlows);
                    }
                }

            }
        }
        //是否撤销
        if (navigate){
            for (PtlSecPositionFlow ptlSecPositionFlow : positionFlows){
                ptlSecPositionFlow.setTradeId(ptlSecPositionFlow.getTradeId() + "R");
                ptlSecPositionFlow.setAmount(ArithmeticUtil.multiply(ptlSecPositionFlow.getAmount(),new BigDecimal(-1)));
            }
            for (PtlCashPositionFlow ptlCashPositionFlow : ptlCashPositionFlows){
                ptlCashPositionFlow.setOrderId(ptlCashPositionFlow.getOrderId() + "R");
                ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(ptlCashPositionFlow.getAmount(),new BigDecimal(-1)));
            }
            for (EtrSecPositionFlow etrSecPositionFlow : etrSecPositionFlows){
                etrSecPositionFlow.setTradeId(etrSecPositionFlow.getTradeId() + "R");
                etrSecPositionFlow.setAmount(ArithmeticUtil.multiply(etrSecPositionFlow.getAmount(),new BigDecimal(-1)));
            }
            for (EtrCashPositionFlow etrCashPositionFlow : etrCashPositionFlows){
                etrCashPositionFlow.setOrderId(etrCashPositionFlow.getOrderId() + "R");
                etrCashPositionFlow.setAmount(ArithmeticUtil.multiply(etrCashPositionFlow.getAmount(),new BigDecimal(-1)));
            }
        }
        // 数据是否已入新头寸
        if(StringUtil.isNotEmpty(dto.getIsExistNew())) {
            ptlCashPositionFlows.forEach(item->item.setIsExistNew(dto.getIsExistNew()));
            positionFlows.forEach(item->item.setIsExistNew(dto.getIsExistNew()));
        }

        //组合持仓流水
        mapperDelegateService.batchInsertSelective(PtlCashPositionFlowMapper.class,ptlCashPositionFlows);
        //组合现金流水
        mapperDelegateService. batchInsertSelective(PtlSecPositionFlowMapper.class,positionFlows);
        //委托指令预录入场合
        if (!StringUtil.isEmptyList(etrSecPositionFlows) && !StringUtil.isEmptyList(etrCashPositionFlows)){
            // 数据是否已入新头寸
            if(StringUtil.isNotEmpty(dto.getIsExistNew())) {
                etrSecPositionFlows.forEach(item->item.setIsExistNew(dto.getIsExistNew()));
                etrCashPositionFlows.forEach(item->item.setIsExistNew(dto.getIsExistNew()));
            }
            //指令持仓流水
            mapperDelegateService.batchInsertSelective(EtrSecPositionFlowMapper.class,etrSecPositionFlows);
            //指令现金流水
            mapperDelegateService. batchInsertSelective(EtrCashPositionFlowMapper.class,etrCashPositionFlows);
        }
    }

    private void deal组合及指令头寸(TrdProductDealPre trdProductDealPre, TrdProductDealAddPre trdProductDealAddPre, List<PtlSecPositionFlow> positionFlows, List<PtlCashPositionFlow> ptlCashPositionFlows, List<EtrSecPositionFlow> etrSecPositionFlows, List<EtrCashPositionFlow> etrCashPositionFlows) {
        String portfolioId = trdProductDealAddPre.getPortfolioId();
        String entrustId = trdProductDealAddPre.getEntrustId();
        //调用共通工具组件查询母组合代码
        String 母组合代码 = AllTradeCommonUtil.exchangePortfolio(portfolioId);
        if (StringUtil.isNotEmpty(母组合代码) && !母组合代码.equals(portfolioId)) {
            trdProductDealAddPre.setPortfolioId(母组合代码);
            deal组合及指令头寸(trdProductDealPre,trdProductDealAddPre,positionFlows,ptlCashPositionFlows,etrSecPositionFlows,etrCashPositionFlows);
        }
        //组合持仓流水
        PtlSecPositionFlow ptlSecPositionFlow = new PtlSecPositionFlow();
        ptlSecPositionFlow.setSeqno(IDGenerator.generate());
        ptlSecPositionFlow.setPortfolioId(portfolioId);
        ptlSecPositionFlow.setSecAcctId(trdProductDealPre.getSecManageAcctId() == null ? "999999" : trdProductDealPre.getSecManageAcctId());
        ptlSecPositionFlow.setFinprodId(trdProductDealPre.getFinprodId());
        ptlSecPositionFlow.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        ptlSecPositionFlow.setInvAim("999999");
        ptlSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
        ptlSecPositionFlow.setCdate(trdProductDealPre.getSettleDate());
        ptlSecPositionFlow.setCcy(trdProductDealPre.getCcy());
        if (DictCodeListExt1.共通业务类型_债券交易预录入.getValue().equals(trdProductDealPre.getBusiType())
                && DictCodeList.交易方向_卖出.getValue().equals(trdProductDealPre.getPs())){
            ptlSecPositionFlow.setAmount(ArithmeticUtil.multiply(trdProductDealPre.getShareAmt(),new BigDecimal(-1)));
        }else {
            ptlSecPositionFlow.setAmount(trdProductDealPre.getShareAmt());
        }
        ptlSecPositionFlow.setTradeId(trdProductDealPre.getTradeId());
        positionFlows.add(ptlSecPositionFlow);
        //组合现金流水
        PtlCashPositionFlow ptlCashPositionFlow = new PtlCashPositionFlow();
        ptlCashPositionFlow.setSeqno(IDGenerator.generate());
        ptlCashPositionFlow.setPortfolioId(portfolioId);
        String acctNo = etrCommonCashPositionDealService.select组合托管户(portfolioId,trdProductDealPre.getCcy(),DictCodeListExt1.资金账户类型_产品托管户.getValue());
        ptlCashPositionFlow.setAcctId(acctNo);
        ptlCashPositionFlow.setCcy(trdProductDealPre.getCcy());
        ptlCashPositionFlow.setCdate(trdProductDealPre.getSettleDate());
        ptlCashPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
        if (DictCodeListExt1.共通业务类型_债券交易预录入.getValue().equals(trdProductDealPre.getBusiType())
                && DictCodeList.交易方向_卖出.getValue().equals(trdProductDealPre.getPs())){
            ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(trdProductDealPre.getShareAmt(),new BigDecimal(-1)));
        }else {
            ptlCashPositionFlow.setAmount(trdProductDealPre.getShareAmt());
        }
        ptlCashPositionFlow.setOrderId(trdProductDealPre.getTradeId());
        ptlCashPositionFlow.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
        ptlCashPositionFlows.add(ptlCashPositionFlow);
        //构建指令头寸
        if (StringUtil.isNotEmpty(entrustId)){
            //指令持仓流水
            EtrSecPositionFlow etrSecPositionFlow = new EtrSecPositionFlow();
            etrSecPositionFlow.setSeqno(IDGenerator.generate());
            etrSecPositionFlow.setPortfolioId(portfolioId);
            etrSecPositionFlow.setSecAcctId(trdProductDealPre.getSecManageAcctId() == null ? "999999" : trdProductDealPre.getSecManageAcctId());
            etrSecPositionFlow.setFinprodId(trdProductDealPre.getFinprodId());
            etrSecPositionFlow.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            etrSecPositionFlow.setInvAim("999999");
            etrSecPositionFlow.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
            etrSecPositionFlow.setCdate(trdProductDealPre.getSettleDate());
            etrSecPositionFlow.setCcy(trdProductDealPre.getCcy());
            if (DictCodeListExt1.共通业务类型_债券交易预录入.getValue().equals(trdProductDealPre.getBusiType())
                    && DictCodeList.交易方向_卖出.getValue().equals(trdProductDealPre.getPs())){
                etrSecPositionFlow.setAmount(ArithmeticUtil.multiply(trdProductDealPre.getShareAmt(),new BigDecimal(-1)));
            }else {
                etrSecPositionFlow.setAmount(trdProductDealPre.getShareAmt());
            }
            etrSecPositionFlow.setTradeId(trdProductDealPre.getTradeId());
            etrSecPositionFlows.add(etrSecPositionFlow);

            //指令现金流水
            EtrCashPositionFlow etrCashPositionFlow = new EtrCashPositionFlow();
            etrCashPositionFlow.setSeqno(IDGenerator.generate());
            etrCashPositionFlow.setPortfolioId(portfolioId);
            etrCashPositionFlow.setAcctId(acctNo);
            etrCashPositionFlow.setCcy(trdProductDealPre.getCcy());
            etrCashPositionFlow.setCdate(trdProductDealPre.getSettleDate());
            etrCashPositionFlow.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
            if (DictCodeListExt1.共通业务类型_债券交易预录入.getValue().equals(trdProductDealPre.getBusiType())
                    && DictCodeList.交易方向_卖出.getValue().equals(trdProductDealPre.getPs())){
                etrCashPositionFlow.setAmount(ArithmeticUtil.multiply(trdProductDealPre.getShareAmt(),new BigDecimal(-1)));
            }else {
                etrCashPositionFlow.setAmount(trdProductDealPre.getShareAmt());
            }
            etrCashPositionFlow.setOrderId(trdProductDealPre.getTradeId());
            etrCashPositionFlows.add(etrCashPositionFlow);
        }
    }

    //撤销入流程中
    public void insertWflTable(Map<String, Object> params) {
        List<String> subBizCodes = periodCommonMapper.findSubTotalTradeId((String) params.get("bizCode"));
        if (subBizCodes != null && !subBizCodes.isEmpty()) {
            for (String subBizCode : subBizCodes) {
                Map<String, Object> params1 = new HashMap<>();
                params1.putAll(params);
                params1.put("bizCode", subBizCode);
                doRevoke(params1);
            }
        }
        doRevoke(params);
    }

    public void doRevoke(Map<String, Object> params){
        params.put("REVOKE_TRADE_STATUS", DictCodeListExt1.交易状态_撤销.getValue());
        List<String> tradeIdList = preTrdCommonMapper.selectByTotleTradeId(params);
        // 汇总交易表
        periodCommonMapper.insertWflTotle(params);
        // 金融产品交易预录入流程表、金融产品交易预录入附属流程表
        if (!StringUtil.isEmptyList(tradeIdList)){
            tradeIdList.forEach(item -> {
                Map<String, Object> params1 = new HashMap<>();
                params1.put("tradeId",item);
                params1.putAll(params);
                preTrdCommonMapper.insertWflTrdProductDealPre(params1);
                preTrdCommonMapper.insertWflTrdProductDealAddPre(params1);
            });
        }
    }
    //撤销生效
    public void doRevokeComplete(Map<String, Object> params) {
        params.put("REVOKE_TRADE_STATUS", DictCodeListExt1.交易状态_撤销.getValue());
        params.put("isCancel",DictCodeList.是否_否.getValue());
        //更新金融产品交易汇总表原交易数据
        TrdFinProductDealTotal trdFinProductDealTotal = trdFinProductDealTotalMapper.selectByPrimaryKey((String) params.get("bizCode"));
        if (StringUtil.isNotEmpty(trdFinProductDealTotal)){
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());
            trdFinProductDealTotal.setUpdateUser((String) params.get("user"));
            trdFinProductDealTotal.setUpdateTime(new Date());
            trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotal);
            //插入撤销交易数据
            trdFinProductDealTotal.setpTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
            trdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId() + "R");
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotal.setUpdateUser((String) params.get("user"));
            trdFinProductDealTotal.setUpdateTime(new Date());
            trdFinProductDealTotal.setCreateDept((String) params.get("dept"));
            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
        }
        //新增金融产品交易预录入表撤销数据
        preTrdCommonMapper.insertTrdProductDealPre(params);
        //更新金融产品交易预录入表原交易数据
        preTrdCommonMapper.updateTrdProductDealPre(params);
        //新增金融产品交易预录入附属表撤销数据
        preTrdCommonMapper.insertTrdProductDealAddPre(params);

    }
    //删除原回购质押品
    public void deleteFinDealPledgePre(String workflowId) {
        Map<String, Object> params = new HashMap<>();
        params.put("workflowId",workflowId);
        preTrdCommonMapper.deleteFinDealPledgePre(params);
    }
    //获取剩余委托额度
    public BigDecimal getEntrustSurAmt(String entrustId) {
        Map<String,Object> params = new HashMap<>();
        params.put("entrustId",entrustId);
        return preTrdCommonMapper.getEntrustSurAmt(params);
    }
    //获取组合名称
    public String getPortfolioName(String portfolioId) {
        Map<String,Object> params = new HashMap<>();
        params.put("portfolioId",portfolioId);
        return preTrdCommonMapper.getPortfolioName(params);
    }
    //获取债券简称
    public String getFinprodAbbr(String finprodId) {
        Map<String,Object> params = new HashMap<>();
        params.put("finprodId",finprodId);
        return preTrdCommonMapper.getFinprodAbbr(params);
    }
    //更新委托完成度List
    public void updateEtrEntrustAmt(PreTrdCommonDto preTrdCommonDto, boolean flag) {
        List<TrdProductDealPre> trdProductDealPreList = preTrdCommonDto.getTrdProductDealPreList();
        List<TrdProductDealAddPre> trdProductDealAddPreList = preTrdCommonDto.getTrdProductDealAddPreList();
        if (!StringUtil.isEmptyList(trdProductDealPreList) && !StringUtil.isEmptyList(trdProductDealAddPreList)){
            for (TrdProductDealPre trdProductDealPre : trdProductDealPreList){
                trdProductDealAddPreList.forEach(trdProductDealAddPre ->{
                    if (trdProductDealPre.getTradeId().equals(trdProductDealAddPre.getTradeId())){
                        updateEtrEntrustAmt(trdProductDealPre,trdProductDealAddPre,flag);
                    }
                });
            }
        }
    }
    //更新委托完成度
    public void updateEtrEntrustAmt(TrdProductDealPre trdProductDealPre, TrdProductDealAddPre trdProductDealAddPre, boolean flag) {
        if (!trdProductDealPre.getTradeId().equals(trdProductDealAddPre.getTradeId()) || StringUtil.isEmpty(trdProductDealAddPre.getEntrustId())){
            return;
        }
        if (flag){
            EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            etrFinishStatus.setEntrustId(trdProductDealAddPre.getEntrustId());
            etrFinishStatus.setTradeId(trdProductDealPre.getTradeId());
            etrFinishStatusMapper.deleteByPrimaryKey(etrFinishStatus);
        }else {
            EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            etrFinishStatus.setEntrustId(trdProductDealAddPre.getEntrustId());
            etrFinishStatus.setTradeId(trdProductDealPre.getTradeId());
            etrFinishStatus.setTrader(trdProductDealPre.getTrader());
            etrFinishStatus.setFinishDate(trdProductDealPre.getSettleDate());
            etrFinishStatus.setEntFinishAmt(trdProductDealPre.getShareAmt());
            etrFinishStatusMapper.insertSelective(etrFinishStatus);
        }
    }


    public void deletePosition(String workflowId) {
        Map<String,Object> params = new HashMap<>();
        params.put("workflowId",workflowId);
        preTrdCommonMapper.deletePtlSecPositionFlow(params);
        preTrdCommonMapper.deletePtlCashPositionFlow(params);
        preTrdCommonMapper.deleteEtrSecPositionFlow(params);
        preTrdCommonMapper.deleteEtrCashPositionFlow(params);
    }

    /**
     * 根据交易编号查询该交易是否已被撤销
     *
     * @param tradeId 交易编号
     * @return 该交易是否已被撤销
     * @see
     * @since 1.0
     */
    public Boolean isAlreadyRevoked(String totleTradeId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("totleTradeId", totleTradeId);
        paramMap.put("YES_NO_YES", DictCodeList.是否_是.getValue());
        paramMap.put("TRADE_STATUS_03", DictCodeListExt1.交易流程状态_撤销.getValue());
        return preTrdCommonMapper.selectRevokedDataCount(paramMap) > 0;

    }
}

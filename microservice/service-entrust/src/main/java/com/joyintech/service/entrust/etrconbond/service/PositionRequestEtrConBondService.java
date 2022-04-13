package com.joyintech.service.entrust.etrconbond.service;

import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.entrust.etrconbond.dao.EtrConBondMapper;
import com.joyintech.service.entrust.etrconbond.dto.EtrConBondDto;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt2.申购类型_普通申购;
import static com.joyintech.tams.constant.DictCodeListExt3.业务动作_投资指令;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

@Service
public class PositionRequestEtrConBondService {

    public static final String STANDAR_SEC_ID = "XN_PRODID999";
    @Autowired
    private CommonPositionEngineService commonPositionEngineService;

    @Autowired
    private EtrConBondMapper etrConBondMapper;

    /**
     * EtrConBondDto请求对象使用
     *
     * @param entity
     */
    public void sendEtrConBondDto(EtrConBondDto entity, String decisionAct, String systemAct) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
//        String interType = commonPositionEngineService.getPositionInterType("ETR", entity.getTradePlat(), finprodType, entity.getPs(), entity.getOptType(), entity.getClass().getName());
        this.bond003(entity, resultMap);
        PositionHttpRequest.send(resultMap);
    }

    /**
     * doDelete时使用---作废指令-共通接口
     *
     * @param entity
     */
    public void sendDelete(EtrConBondDto entity) {
        PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(), "ETR", DictCodeListExt3.头寸业务类型_网下申购.getValue(), DictCodeList.交易方向_申购.getValue());
    }

    /**
     * 撤销时使用---撤销指令-共通接口
     *
     * @param entity
     */
    public void sendRevoke(EtrConBondDto entity) {
        PositionHttpRequest.dorevoke(entity.getEntrustId(), "TRD", DictCodeListExt3.头寸业务类型_网下申购.getValue(), DictCodeList.交易方向_申购.getValue());
    }

    /**
     * 关闭时使用---关闭-共通接口
     *
     * @param entity
     */
    public void sendClose(String entrustId, String closeWay) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userId", SecurityUtil.getLoginUser().getUserName());
//        resultMap.put("totalTradeId", entity.getWorkflowId());
        resultMap.put("modelType", "ETR");
        resultMap.put("busiType", DictCodeListExt3.头寸业务类型_网下申购.getValue());
        resultMap.put("ps", DictCodeList.交易方向_申购.getValue());
        resultMap.put("decisionAct", DictCodeListExt3.系统动作_关闭.getValue());
        resultMap.put("systemAct", DictCodeListExt3.系统动作_关闭.getValue());
        resultMap.put("closeId", entrustId);
        PositionHttpRequest.batchClose(resultMap);
    }

    /**
     * 可转债-指令    bond003
     *
     * @param entity
     * @param resultMap
     */
    private void bond003(EtrConBondDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", "Bond003");
        resultMap.put(BUSI_ACT, 业务动作_投资指令.getValue());
        String deliveryType = commonPositionEngineService.getDeliveryType(entity.getFinprodId());
        resultMap.put(DELIVERY_TYPE, commonPositionEngineService.getDeliveryType(entity.getFinprodId()));
        resultMap.put(SETTLE_SPEED, commonPositionEngineService.parseSettleSpreed(deliveryType));
        resultMap.put("ps", DictCodeList.交易方向_申购.getValue());
        resultMap.put("busiType", DictCodeListExt3.头寸业务类型_网下申购.getValue());
        if (申购类型_普通申购.getValue().equals(entity.getPurchType())) {
            resultMap.put(PURCH_MODLE, DictCodeListExt2.申购方式_网下申购.getValue());
        } else {
            resultMap.put(PURCH_MODLE, DictCodeListExt2.申购方式_网下优先配售.getValue());
        }
        resultMap.put("tradePlace", entity.getTradeMarket());
        // 交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, commonPositionEngineService.getSharesTradePlatform(entity.getTradePlat(), entity.getPs(), "", ""));

        resultMap.put("workFlowId", entity.getWorkflowId());
        // 日期
        resultMap.put("cdate", DateUtil.format(entity.getVdate()));
        // 币种
        if (StringUtils.isNotEmpty(entity.getCcy())) {
            resultMap.put(CCY, entity.getCcy());
        } else {
            resultMap.put(CCY, DictCodeList.币种_人民币_CNY.getValue());
        }
        // 前次交易编号
        if (DictCodeListExt3.系统动作_修改.getValue().equals((String) resultMap.get("decisionAct"))) {
            resultMap.put(B_TOTAL_TRADE_ID, entity.getEntrustId());
        } else {
            resultMap.put(B_TOTAL_TRADE_ID, null);
        }

        Map<String, String> accnoParm = new HashMap<>();
        accnoParm.put("ccy", entity.getCcy());
        accnoParm.put("portfolioId", entity.getPortfolioId());
        accnoParm.put("acctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        String accNo = etrConBondMapper.getAccNo(accnoParm);

        List<Map<String, Object>> ptlCashList = new ArrayList<>(1);
        Map<String, Object> ptlCash = new HashMap<>(8);
        ptlCash.put("portfolioId", entity.getPortfolioId());
        ptlCash.put("cashAcctId", accNo);
        ptlCash.put("tradeId", null);
        ptlCash.put("etrId", entity.getEntrustId());
        ptlCash.put("secAmount", entity.getEntrustNum());
        BigDecimal entrustAmt = entity.getEntrustAmt();
        BigDecimal cashAmount = BigDecimal.ZERO;
        if (Objects.isNull(entrustAmt) || BigDecimal.ZERO.compareTo(entrustAmt) > -1) {
            BigDecimal entrustPrice = Objects.isNull(entity.getPrice()) ? new BigDecimal(0) : entity.getPrice();
            cashAmount = cashAmount.add(entity.getEntrustNum().multiply(entrustPrice));
        } else {
            cashAmount = cashAmount.add(entrustAmt);
        }
        ptlCash.put("cashAmount", cashAmount);
        ptlCashList.add(ptlCash);
        resultMap.put("ptlCashList", ptlCashList);

        List<Map<String, Object>> bondSecList = new ArrayList<>(1);
        Map<String, Object> bondSec = new HashMap<>();
        bondSec.put("portfolioId", entity.getPortfolioId());
        bondSec.put("finprodId", entity.getFinprodId());
        bondSec.put("secAcctId", entity.getSecAcctId());
        bondSec.put("cashAcctId", accNo);
        bondSec.put("etrId", entity.getEntrustId());
        bondSec.put("secAmount", commonPositionEngineService.getSecAmountByBondUnit(entity.getEntrustNum()));
        bondSec.put("cashAmount", BigDecimal.ZERO);
        //融入券券面总额
        bondSec.put(INTEGRATE_SEC_AMOUNT, 0);
        bondSecList.add(bondSec);
        resultMap.put("bondSecList", bondSecList);

        // 申购数据集
        List<Map<String, Object>> applyBondSecList = new ArrayList<>(1);
        Map<String, Object> applyBondSec = new HashMap<>();
        applyBondSec.put("portfolioId", entity.getPortfolioId());
        applyBondSec.put("finprodId", entity.getFinprodId());
        applyBondSec.put("secAcctId", entity.getSecAcctId());
        applyBondSec.put("cashAcctId", accNo);
        applyBondSec.put("etrId", entity.getEntrustId());
        applyBondSec.put("secAmount", commonPositionEngineService.getSecAmountByBondUnit(entity.getEntrustNum()));
        applyBondSec.put("cashAmount", null);
        applyBondSecList.add(applyBondSec);
        resultMap.put("applyBondSecList", applyBondSecList);
    }

    /**
     * 解析清算速度
     * <p>
     * 清算速度	见数据字典：清算速度
     * "IF 变量.结算方式 = 数据字典.DELIVERY_TYPE.净额结算 THEN 数据字典.清算速度.T+1
     * ELSE IF 变量.结算方式 = 数据字典.DELIVERY_TYPE.RTGS THEN 数据字典.清算速度.T+0
     * ELSE 数据字典.清算速度.T+1
     *
     * @param resultMap 封装集合
     * @return 清算速度
     */
    private String parseSettleSpreed(String deliveryType) {
        String settleSpreed = BussinessDictCodeList.清算速度_T加1.getValue();
        if (DictCodeListExt3.结算方式_头寸引擎_净额结算.getValue().equals(deliveryType)) {
            settleSpreed = BussinessDictCodeList.清算速度_T加1.getValue();
        } else if ((DictCodeListExt3.结算方式_头寸引擎_RTGS.getValue()).equals(deliveryType)) {
            settleSpreed = BussinessDictCodeList.清算速度_T加0.getValue();
        }
        return settleSpreed;
    }
}

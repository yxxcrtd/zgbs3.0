package com.joyintech.service.trade.conbond.service;

import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.conbond.dao.TrdConBondMapper;
import com.joyintech.service.trade.conbond.dto.TrdConBondDto;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_修改;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

@Service
public class PositionRequestTrdConBondService {

    public static final String STANDAR_SEC_ID = "XN_PRODID999";

    @Autowired
    private CommonPositionEngineService commonPositionEngineService;

    @Autowired
    private TrdConBondMapper trdConBondMapper;

    /**
     * TrdConBondDto请求对象使用
     *
     * @param entity
     */
    public void sendTrdConBondDto(TrdConBondDto entity, String decisionAct, String systemAct) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
        this.bond003(entity, resultMap);
        PositionHttpRequest.send(resultMap);
    }

    /**
     * doDelete时使用---作废指令-共通接口
     *
     * @param workflowId
     */
    public void sendDelete(String workflowId) {
        PositionHttpRequest.doDeleteByWorkFlowId(workflowId, POSITION_TYPE_TRD, DictCodeListExt3.头寸业务类型_网下申购.getValue(), DictCodeList.交易方向_申购.getValue());
    }

    /**
     * 撤销时使用---撤销指令-共通接口
     *
     * @param entity
     */
    public void sendRevoke(TrdConBondDto entity) {
        PositionHttpRequest.dorevoke(entity.getTrdProductDeal().getTradeId(), POSITION_TYPE_TRD, DictCodeListExt3.头寸业务类型_网下申购.getValue(), DictCodeList.交易方向_申购.getValue());
    }

    /**
     * 可转债-指令    bond003
     *
     * @param entity
     * @param resultMap
     */
    private void bond003(TrdConBondDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_BOND003);
        resultMap.put(BUSI_TYPE, DictCodeListExt3.头寸业务类型_网下申购.getValue());
        resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_交易登记.getValue());
        resultMap.put(PS, DictCodeList.交易方向_申购.getValue());
        String deliveryType = commonPositionEngineService.getDeliveryType(entity.getFinprodId());
        resultMap.put(SETTLE_SPEED, commonPositionEngineService.parseSettleSpreed(deliveryType));
        if (DictCodeListExt2.申购类型_普通申购.getValue().equals(entity.getPurchType())) {
            resultMap.put(PURCH_MODLE, DictCodeListExt2.申购方式_网下申购.getValue());
        } else {
            resultMap.put(PURCH_MODLE, DictCodeListExt2.申购方式_网下优先配售.getValue());
        }
        resultMap.put(TRADE_PLACE, entity.getTradeMarket());
        resultMap.put(SHARES_TRADE_PLATFORM, null);
        resultMap.put(DELIVERY_TYPE, deliveryType);
        resultMap.put(WORK_FLOW_ID, null);
        resultMap.put("cdate", entity.getPurchDate());
        resultMap.put(CCY, entity.getCcy());
        if(resultMap.get("decisionAct").equals(系统动作_修改.getValue())) {
            resultMap.put(B_TOTAL_TRADE_ID, entity.getBusiCode());
        }

        Map<String, String> accnoParm = new HashMap<>();
        accnoParm.put(CCY, entity.getCcy());
        accnoParm.put("portfolioId", entity.getPortfolioId());
        accnoParm.put("acctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        String accNo = trdConBondMapper.getAccNo(accnoParm);

        List<Map<String, Object>> ptlCashList = new ArrayList<>();
        Map<String, Object> ptlCash = new HashMap<>();
        ptlCash.put("portfolioId", entity.getPortfolioId());
        ptlCash.put("cashAcctId", accNo);
        ptlCash.put("tradeId", entity.getTrdProductDeal().getTradeId());
        ptlCash.put("etrId", entity.getEntrustId());
        //网下申购，普通申购传50万，优先配售用券面总额
        if (StringUtil.isNotBlank(entity.getPurchType()) && StringUtil.equals(entity.getPurchType(), DictCodeListExt2.申购类型_优先配售.getValue())) {
            ptlCash.put("cashAmount", entity.getTradeAmt());
        }else {
            ptlCash.put("cashAmount", new BigDecimal("500000"));
        }

        ptlCashList.add(ptlCash);
        resultMap.put("ptlCashList", ptlCashList);

        List<Map<String, Object>> bondSecList = new ArrayList<>();
        Map<String, Object> bondSec = new HashMap<>();
        bondSec.put("portfolioId", entity.getPortfolioId());
        bondSec.put("finprodId", entity.getFinprodId());
        bondSec.put("secAcctId", entity.getSecManageAcctId());
        bondSec.put("etrId", entity.getEntrustId());
        bondSec.put("secAmount", entity.getTradeAmt());
        bondSec.put("integrateSecAmount",BigDecimal.ZERO);
        bondSec.put("ownSecAmount",entity.getTradeAmt());
        bondSec.put("tradeId",entity.getTrdProductDeal().getTradeId());
        ptlCash.put("etrId", entity.getEntrustId());
        bondSecList.add(bondSec);
        resultMap.put("bondSecList", bondSecList);

        resultMap.put("balanceCheck", DictCodeList.是否_否.getValue());
        resultMap.put("userId", SecurityUtil.getLoginUser().getUserName());
        resultMap.put("totalTradeId", entity.getBusiCode());
        resultMap.put("flowStatus", DictCodeListExt3.流程节点_流程提交.getValue());
        resultMap.put("modelType", DictCodeListExt1.子系统代码_委托.getValue());
    }
}

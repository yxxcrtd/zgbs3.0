package com.joyintech.service.portfolio.position.service;

import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.portfolio.position.constant.PositionConstant;
import com.joyintech.service.portfolio.ptlfundallocation.dto.PtlFundAllocationDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.position.servive.PositionRequestHandle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

@Service
public class PositionRequestPtlFoundAllocation002Request {

    /**
     * 头寸引擎
     */
    @Autowired
    private PositionRequestHandle positionRequestHandle;


    /**
     * PtlFundAllocationDto请求对象使用
     *
     * @param entity
     */
    public void sendEtrExchangeTradeSaveRequest(PtlFundAllocationDto entity, String decisionAct, String systemAct) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
        this.setCommonField(entity, resultMap);

        positionRequestHandle.send(resultMap);
    }



    /**
     * doDelete时使用---作废-共通接口
     *
     * @param entity
     */
    public void sendDelete(PtlFundAllocationDto entity) {
        String busiType = null;
        String ps = null;
        if(Objects.equals(entity.getPayType(),DictCodeListExt1.指令类型_银证转账_银转证.getValue())
                ||Objects.equals(entity.getPayType(),"01")||Objects.equals(entity.getPayType(),"HK01")){
            busiType = DictCodeListExt3.头寸业务类型_银证转账.getValue();
            ps = DictCodeListExt3.交易方向_银证转账.getValue();
        } else {
            busiType = DictCodeListExt3.头寸业务类型_证银转账.getValue();
            ps = DictCodeListExt3.交易方向_证银转账.getValue();
        }
        positionRequestHandle.doDeleteByWorkFlowId(entity.getWorkflowId(), POSITION_TYPE_TRD, busiType, ps);
    }

    /**
     * 撤销时使用---撤销-共通接口
     *
     * @param entity
     */
    public void sendRevoke(PtlFundAllocationDto entity) {
        String busiType = null;
        String ps = null;
        if(Objects.equals(entity.getPayType(),DictCodeListExt1.指令类型_银证转账_银转证.getValue())
                ||Objects.equals(entity.getPayType(),"01")||Objects.equals(entity.getPayType(),"HK01")){
            busiType = DictCodeListExt3.头寸业务类型_银证转账.getValue();
            ps = DictCodeListExt3.交易方向_银证转账.getValue();
        } else {
            busiType = DictCodeListExt3.头寸业务类型_证银转账.getValue();
            ps = DictCodeListExt3.交易方向_证银转账.getValue();
        }
        positionRequestHandle.dorevoke(entity.getTradeId(), POSITION_TYPE_TRD, busiType, ps);
    }

    /**
     * 设置公共字段
     *
     * @param entity    实例对象
     * @param resultMap 封装集合
     */
    private void setCommonField(PtlFundAllocationDto entity, Map<String, Object> resultMap) {

        String balanceCheck = "Y";
        if (StringUtil.isNotEmpty(entity.getSoftSkipMode()) && StringUtil.equals(entity.getSoftSkipMode(), "Y")) {
            balanceCheck = "N";
        }
        resultMap.put("userId", SecurityUtil.getLoginUser().getUserName());
        resultMap.put("totalTradeId", entity.getTradeId());
        resultMap.put("balanceCheck", balanceCheck);
        if(Objects.equals(entity.getPositionType(),"commit")){
            resultMap.put("flowStatus", DictCodeListExt3.流程节点_审批生效.getValue());
        } else {
            resultMap.put("flowStatus", DictCodeListExt3.流程节点_流程提交.getValue());
        }
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());

        //业务类型 交易方向
        if(Objects.equals(entity.getPayType(),DictCodeListExt1.指令类型_银证转账_银转证.getValue())
        ||Objects.equals(entity.getPayType(),"01")||Objects.equals(entity.getPayType(),"HK01")){
            resultMap.put("busiType", DictCodeListExt3.头寸业务类型_银证转账.getValue());
            resultMap.put(PS, DictCodeListExt3.交易方向_银证转账.getValue());
        } else {
            resultMap.put("busiType", DictCodeListExt3.头寸业务类型_证银转账.getValue());
            resultMap.put(PS, DictCodeListExt3.交易方向_证银转账.getValue());
        }
        //业务动作
        resultMap.put("busiAct", DictCodeListExt3.业务动作_资金划拨新建.getValue());
        // 币种
        if (StringUtils.isNotEmpty(entity.getCcy())) {
            resultMap.put(CCY, entity.getCcy());
        } else {
            resultMap.put(CCY, DictCodeList.币种_人民币_CNY.getValue());
        }
        if(StringUtil.equals(entity.getPositionType(), "update")){
            resultMap.put("bTotalTradeId", entity.getTradeId());
        } else {
            resultMap.put("bTotalTradeId", null);
        }
        resultMap.put("workFlowId", entity.getWorkflowId());
        // 日期
        resultMap.put("cdate", DateUtil.format(entity.getSettleDate()));
        resultMap.put(INTERFACE_TYPE, PositionConstant.INTERF_TYPE_TRANSFER_001);

        //设置转账详情列表
        List<Map<String, Object>> ptlTrusteeshipList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("portfolioId",entity.getPortfolioId());
        map.put("tradeId",entity.getTradeId());
        map.put("cashAcctId",entity.getMyAcctNo());
        map.put("cashAmount",entity.getSettleAmt());
        ptlTrusteeshipList.add(map);
        //设置收款账户列表

        List<Map<String, Object>> ptlSecuritiesList = new ArrayList<>();
        Map<String, Object> maps = new HashMap<>();
        maps.put("portfolioId",entity.getPortfolioId());
        maps.put("tradeId",entity.getTradeId());
        maps.put("cashAcctId",entity.getCounterAcctNo());
        maps.put("cashAmount",entity.getSettleAmt());
        ptlSecuritiesList.add(maps);
        // 托管户和证券户数据
        if (Objects.equals(entity.getPayType(), DictCodeListExt1.指令类型_银证转账_银转证.getValue())
                || Objects.equals(entity.getPayType(), "01") || Objects.equals(entity.getPayType(), "HK01")) {
            resultMap.put("ptlTrusteeshipList", ptlTrusteeshipList);//托管户
            resultMap.put("ptlSecuritiesList", ptlSecuritiesList);//证券户
        } else {
            resultMap.put("ptlTrusteeshipList", ptlSecuritiesList);//证券户
            resultMap.put("ptlSecuritiesList", ptlTrusteeshipList);//托管户
        }
    }



}

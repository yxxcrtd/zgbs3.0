package com.joyintech.tams.exchange.entrust.etrexchange.position.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto.EtrCreditProtectionCertificateApplyRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.position.bean.PositionRequestBaseBean;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeOperateSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePortfolioSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

@Service
public class PositionRequestServiceTradSaveCpcRequest {
    /**
     * 日志对象
     */
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CommonPositionEngineService commonPositionEngineService;

    public void sendEtrExchangeTradeSaveRequest(EtrCreditProtectionCertificateApplyRequest entity, String decisionAct, String systemAct) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
        this.setCommonField(entity, resultMap,decisionAct);//设置公共字段，个性化的需要在具体接口中覆盖
        PositionHttpRequest.send(resultMap);
    }

    /**
     * doDelete时使用---作废指令-共通接口
     *
     * @param entity
     */
    public void sendDelete(EtrCreditProtectionCertificateApplyRequest entity) {
        String busiType = DictCodeListExt3.头寸业务类型_信用保护凭证交易.getValue();
        PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(), POSITION_TYPE_ETR, busiType, entity.getPs());
    }
    /**
     * 关闭时使用---关闭-共通接口
     *
     * @param entity
     */
    public void sendClose(EtrExchangeTradeOperateSecRepoRequest entity) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userId", SecurityUtil.getLoginUser().getUserName());
        // 关闭指令时，无流程ID，故生成UUID传入
        resultMap.put("totalTradeId", IDGenerator.generate(32));
        resultMap.put("modelType", POSITION_TYPE_ETR);

        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        //设置头寸业务类型
        map.put(PS, entity.getPs());
        map.put(BUSI_ACT, DictCodeListExt3.业务动作_指令手动关闭.getValue());
        map.put("systemAct", DictCodeListExt3.系统动作_关闭.getValue());
        map.put("closeId", entity.getEntrustId());
        map.put(BUSI_TYPE, DictCodeListExt3.头寸业务类型_信用保护凭证交易.getValue());
        list.add(map);
        resultMap.put("data", list);
        PositionHttpRequest.batchClose(resultMap);
    }

    /**
     * 设置公共字段
     *
     * @param entity    实例对象
     * @param resultMap 封装集合
     */
    private void setCommonField(EtrCreditProtectionCertificateApplyRequest entity, Map<String, Object> resultMap,String decisionAct) {
        String balanceCheck = "Y";
        if (StringUtil.isNotEmpty(entity.getSoftSkipMode()) && StringUtil.equals(entity.getSoftSkipMode(), "Y")) {
            balanceCheck = "N";
        }
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_CPC001);
        resultMap.put("userId", SecurityUtil.getLoginUser().getUserName());
        resultMap.put("totalTradeId", entity.getEntrustId());
        resultMap.put("balanceCheck", balanceCheck);
        resultMap.put("flowStatus", DictCodeListExt3.流程节点_流程提交.getValue());
        resultMap.put("modelType", DictCodeListExt1.子系统代码_委托.getValue());

        //设置头寸业务类型
        resultMap.put(BUSI_TYPE, DictCodeListExt3.头寸业务类型_信用保护凭证交易.getValue());
        //业务动作
        resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_投资指令.getValue());
        // 交易方向
        resultMap.put(PS, entity.getPs());
        // 交易场所	见数据字典：交易场所
        resultMap.put(TRADE_PLACE, entity.getTradeMarket());
        // 流程编号	指令的工作流ID
        resultMap.put(WORK_FLOW_ID, entity.getWorkflowId());
        // 日期
        resultMap.put("cdate", DateUtil.format(entity.getVdate()));
        // 币种
        if (StringUtils.isNotEmpty(entity.getCcy())) {
            resultMap.put(CCY, entity.getCcy());
        } else {
            resultMap.put(CCY, DictCodeList.币种_人民币_CNY.getValue());
        }
        // 前次交易编号
        if (DictCodeListExt3.系统动作_修改.getValue().equals(decisionAct)) {
            resultMap.put(B_TOTAL_TRADE_ID, entity.getEntrustId());
        } else {
            resultMap.put(B_TOTAL_TRADE_ID, null);
        }

        if (CollectionUtils.isNotEmpty(entity.getPortfolioList())) {
            int portfolioListSize = entity.getPortfolioList().size();
            PositionRequestBaseBean bean;

            List<Map<String, Object>> ptlList = new ArrayList<>(portfolioListSize);
            List<Map<String, Object>> ptlSubList = new ArrayList<>(portfolioListSize);
            fillAttributes(entity);
            for (EtrExchangeTradePortfolioSecRepoRequest item : entity.getPortfolioList()) {
                bean = new PositionRequestBaseBean(item);
                BigDecimal price = Objects.isNull(entity.getPrice()) ? new BigDecimal(0) : entity.getPrice();
                bean.setCashAmount(price.multiply(item.getEntrustNum()).setScale(2, RoundingMode.HALF_UP));
                BigDecimal secAmount = commonPositionEngineService.getSecAmountByBondUnit(item.getEntrustNum());
                bean.setSecAmount(secAmount.setScale(2, RoundingMode.HALF_UP));

                Map<String, Object> map = BeanUtil.object2Map(bean);
                map.put("etrSecAmount", entity.getEntrustNum());
                map.put("tradeId", "");
                ptlList.add(map);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("portfolioId", bean.getPortfolioId());
                map2.put("finprodId", bean.getFinprodId());
                map2.put("secAcctId", bean.getSecAcctId());
                map2.put("secAmount", bean.getSecAmount());
                map2.put("tradeId", "");
                ptlSubList.add(map2);
            }
            resultMap.put(PTL_LIST, ptlList);
            resultMap.put(PTL_SUB_LIST, ptlSubList);
        }
    }
    private void fillAttributes(EtrCreditProtectionCertificateApplyRequest entity) {
        for (EtrExchangeTradePortfolioSecRepoRequest item : entity.getPortfolioList()) {
            item.setEntrustId(entity.getEntrustId());
            item.setFinprodId(entity.getFinprodId());
        }
    }
}

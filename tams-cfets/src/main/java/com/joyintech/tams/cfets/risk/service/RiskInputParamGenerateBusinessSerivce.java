package com.joyintech.tams.cfets.risk.service;

import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.joyintech.tams.constant.DictCodeListExt3.*;

/**
 * 构造风险报文入参
 *
 * @author jiangpeng
 * @date 2022/6/10 11:39
 */
@Service
public class RiskInputParamGenerateBusinessSerivce extends RiskCommonService {
    private static final Logger logger = LoggerFactory.getLogger(RiskInputParamGenerateBusinessSerivce.class);

    /**
     * 组装一级投标指令
     *
     * @param decisionAct  系统动作
     * @param flowStatus   流程节点
     * @param isCheck      是否校验
     * @param businessJson Json数据
     * @return com.joyintech.tams.business.common.pojo.BaseMap
     * @author jiangpeng
     * @date 2022/6/10 11:43
     */
    BaseMap generateEtrBondFirstBid(String decisionAct, String flowStatus, boolean isCheck, String businessJson) {
        logger.debug("组装一级投标指令:" + businessJson);
        CfetsEtrBondFirstBidSaveRequest dto = JsonUtils.json2Object(businessJson, CfetsEtrBondFirstBidSaveRequest.class);
        // 接口字段名
        String interfaceType = NodeConfigConstant.BOND001;
        // 交易场所
        String tradePlace = 交易场所_银行间.getValue();
        if (!StringUtil.equals(dto.getTradeMarket(), 交易场所_银行间.getValue())
                && !StringUtil.equals(dto.getTradeMarket(), 交易场所_商业银行柜台.getValue())) {
            interfaceType = NodeConfigConstant.BOND002;
            tradePlace = null;
        }
        return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, interfaceType)
                .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级投标.getValue())
                .put("tradePlace", tradePlace)
                .put("ps", dto.getPs())
                .put("isCheck", isCheck)
                .put("data", dto);
    }


}

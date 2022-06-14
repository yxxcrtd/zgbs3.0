package com.joyintech.tams.cfets.risk.service;

import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveRequest;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
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
                .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级投标.getValue())
                .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                .put(NodeConfigConstant.TRADE_PLACE, tradePlace)
                .put(NodeConfigConstant.PS, DictCodeList.交易方向_买入.getValue())
                .put(NodeConfigConstant.IS_CHECK, isCheck)
                .put(NodeConfigConstant.DATA, dto);
    }

    /**
     * 组装一级投标确认
     *
     * @param decisionAct  系统动作
     * @param flowStatus   流程节点
     * @param isCheck      是否校验
     * @param businessJson Json数据
     * @return com.joyintech.tams.business.common.pojo.BaseMap
     * @author jiangpeng
     * @date 2022/6/10 11:43
     */
    BaseMap generateCfetsTrdFirstBidFirmInfo(String decisionAct, String flowStatus, boolean isCheck, String businessJson) {
        logger.debug("组装一级投标确认:" + businessJson);
        CfetsTrdFirstBidFirmInfoDto dto = JsonUtils.json2Object(businessJson, CfetsTrdFirstBidFirmInfoDto.class);
        // 接口字段名
        String interfaceType = NodeConfigConstant.BOND001;
        // 交易场所
        String tradePlace = 交易场所_银行间.getValue();
        if (!StringUtil.equals(dto.getTradeMarket(), 交易场所_银行间.getValue())
                && !StringUtil.equals(dto.getTradeMarket(), 交易场所_商业银行柜台.getValue())) {
            interfaceType = NodeConfigConstant.BOND002;
            tradePlace = null;
        }
        String purchModle = DictCodeListExt4.头寸申购方式_分销买入.getValue();
        if (StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), dto.getBiddingMethod())) {
            purchModle = DictCodeListExt4.投标方式_代投代缴.getValue();
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), dto.getBiddingMethod())) {
            purchModle = DictCodeListExt4.投标方式_自主投标.getValue();
        }
        return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, interfaceType)
                .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级投标.getValue())
                .put(NodeConfigConstant.BUSI_ACT, 业务动作_投标确认.getValue())
                .put(NodeConfigConstant.PURCH_MODLE, purchModle)
                .put(NodeConfigConstant.TRADE_PLACE, tradePlace)
                .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                .put(NodeConfigConstant.PS, DictCodeList.交易方向_买入.getValue())
                .put(NodeConfigConstant.IS_CHECK, isCheck)
                .put(NodeConfigConstant.DATA, dto);
    }

    /**
     * 组装一级申购
     *
     * @param decisionAct  系统动作
     * @param flowStatus   流程节点
     * @param isCheck      是否校验
     * @param businessJson Json数据
     * @return com.joyintech.tams.business.common.pojo.BaseMap
     * @author jiangpeng
     * @date 2022/6/10 11:43
     */
    BaseMap generateCfetsTrdBondFirstSubInfo(String decisionAct, String flowStatus, boolean isCheck, String businessJson) {
        logger.debug("组装一级申购:" + businessJson);
        CfetsTrdBondFirstSubInfoDto dto = JsonUtils.json2Object(businessJson, CfetsTrdBondFirstSubInfoDto.class);
        return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级申购.getValue())
                .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
                .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                .put(NodeConfigConstant.PS, DictCodeList.交易方向_买入.getValue())
                .put(NodeConfigConstant.IS_CHECK, isCheck)
                .put(NodeConfigConstant.DATA, dto);
    }

    /**
     * 组装中标结果维护
     *
     * @param decisionAct  系统动作
     * @param flowStatus   流程节点
     * @param isCheck      是否校验
     * @param businessJson Json数据
     * @return com.joyintech.tams.business.common.pojo.BaseMap
     * @author jiangpeng
     * @date 2022/6/10 11:43
     */
    BaseMap generateCfetsPrimaryBidManage(String decisionAct, String flowStatus, boolean isCheck, String businessJson) {
        CfetsPrimaryBidManageSaveRequest dto = JsonUtils.json2Object(businessJson, CfetsPrimaryBidManageSaveRequest.class);
        BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                .put(NodeConfigConstant.BUSI_ACT, 业务动作_中标结果维护.getValue())
                .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级投标.getValue())
                .put(NodeConfigConstant.TRADE_PLACE, dto.getTradeMarket())
                .put(NodeConfigConstant.PS, DictCodeList.交易方向_买入.getValue())
                .put(NodeConfigConstant.IS_CHECK, isCheck)
                .put(NodeConfigConstant.DATA, dto);
        if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            map.put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND002);
        }
        return map;
    }


}

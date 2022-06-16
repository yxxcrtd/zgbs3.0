package com.joyintech.tams.cfets.risk.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.common.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribSaveRequest;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfoService;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrRepoSaveRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtribRepoInfo003Dto;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoinfo003Service;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleDto;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleInsertDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveRequest;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.repo.dto.RepoPtlAllocationDto;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdBondInvestTradeDto;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmManualMatchInfoRequest;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdFirstBondInvestTradeDto;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdRepoTrade003Dto;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdRepoTrade002Dto;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonInputDto;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonOutputDto;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import com.joyintech.tams.component.business.riskall.service.RiskNodeConfigService;
import com.joyintech.tams.component.business.riskall.utils.RiskUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesDealsMapper;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.workflow.engine.WorkflowContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt3.*;
import static com.joyintech.tams.invest.common.constant.BussinessDictCodeList.交易方向_行权回售;

/**
 * 用来组装风险试算需要的指标、授信、授权、名单试算所需要的参数
 *
 * @author gwl
 * @date 2022-06-02
 */
@Service
public class RiskInputParamSerivce extends RiskCommonService {

    private static Logger logger = LoggerFactory.getLogger(RiskInputParamSerivce.class);


    @Autowired
    private RiskNodeConfigService riskNodeConfigService;
    @Autowired
    private InterfRiskRdsService interfRiskRdsService;
    @Autowired
    private CreditLinesDealsMapper creditLinesDealsMapper;
    @Autowired
    private CfetsEtrIbBondinfoService cfetsEtrIbBondinfoService;
    @Autowired
    private CfetsEtrIbRepoinfo003Service cfetsEtrIbRepoinfo003Service;

    /**
     * 构造风险报文入参
     */
    @Autowired
    private RiskInputParamGenerateBusinessSerivce riskInputParamGenerateBusinessSerivce;

    /**
     * 构建授权试算参数
     */
    @Autowired
    private RiskInputBuildAuthorizeParamSerivce riskInputBuildAuthorizeParamSerivce;

    /**
     * 构建授信计算参数
     */
    @Autowired
    private RiskInputBuildCreditParamSerivce riskInputBuildCreditParamSerivce;

    /**
     * 构建指标计算参数
     */
    @Autowired
    private RiskInputBuildRiskRdsParamSerivce riskInputBuildRiskRdsParamSerivce;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    /**
     * 构造入参
     *
     * @param obj
     * @param decisionAct
     * @param flowStatus
     * @param isCheck
     * @param pageId
     * @return
     */
    private BaseMap generateBusinessDto(Object obj, String decisionAct, String flowStatus, boolean isCheck, String pageId) {
        ProcessMark processMark;
        String businessJson = JsonUtils.object2Json(obj);
        // 入参为 workflowId 时 ，需要从流程中获取业务参数
        if (obj instanceof String) {
            processMark = WorkflowContext.getProcessMarkService().getBusinessInfo((String) obj);
            pageId = processMark.getPageId();
            businessJson = processMark.getBusinessData();
        }
        if (obj instanceof CfetsEtribBondInfo003Dto || StringUtil.equals(pageId, "CfetsEtrIbBondinfo003")) {
            CfetsEtribBondInfo003Dto dto = JsonUtils.json2Object(businessJson, CfetsEtribBondInfo003Dto.class);
            if (StringUtil.isEmpty(dto.getPs()) && CollectionUtils.isNotEmpty(dto.getSelectedItem())) {
                dto.setPs(dto.getSelectedItem().get(0).getPs());
            }
            if (StringUtil.equals(dto.getAssetType(), DictCodeList.平安资产类型_质押式回购.getValue())) {
                return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.REPO001)
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                        .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                        .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                        .put(NodeConfigConstant.PS, dto.getPs())
                        .put(NodeConfigConstant.IS_CHECK, isCheck)
                        .put(NodeConfigConstant.DATA, dto);
            } else {
                BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                        .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                        .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                        .put(NodeConfigConstant.PS, dto.getPs())
                        .put(NodeConfigConstant.IS_CHECK, isCheck)
                        .put(NodeConfigConstant.DATA, dto);
                if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                    map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
                }
                return map;
            }
        }
        if (obj instanceof CfetsEtribSaveRequest || StringUtil.equals(pageId, "CfetsEtrBondInfo")) {
            CfetsEtribSaveRequest allDto = JsonUtils.json2Object(businessJson, CfetsEtribSaveRequest.class);
            CfetsEtribBondInfo003Dto dto = cfetsEtrIbBondinfoService.packageEquity(allDto);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);

            if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
            }
            return map;
        }
        if (obj instanceof CfetsBondDealQuoteCommonDto || StringUtil.equals(pageId, "IbuQuoteBond004")) {
            CfetsBondDealQuoteCommonDto allDto = JsonUtils.json2Object(businessJson, CfetsBondDealQuoteCommonDto.class);
            CfetsBondDealRegisterQuoteDto dto = new CfetsBondDealRegisterQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(dto, allDto);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);
            if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
            }
            return map;
        }
        if (obj instanceof CfetsBondDealRegisterQuoteDto || StringUtil.equals(pageId, "IbuQuoteBond004")) {
            CfetsBondDealRegisterQuoteDto dto = JsonUtils.json2Object(businessJson, CfetsBondDealRegisterQuoteDto.class);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);
            if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
            }
            return map;
        }
        // 交收
        if (obj instanceof CfetsManualSettleDto || StringUtil.equals(pageId, "CfetsManualSettle")) {
            return riskInputParamGenerateBusinessSerivce.generateCfetsManualSettle(decisionAct, flowStatus, isCheck, businessJson);
        }
        // 成交确认-现券交易
        if (obj instanceof CfetsTrdBondInvestTradeDto) {
            CfetsTrdBondInvestTradeDto dto = JsonUtils.json2Object(businessJson, CfetsTrdBondInvestTradeDto.class);

            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_成交确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, dto.getTradeMarket())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);
            if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
            }
            return map;
        }
        if (obj instanceof TrdBondInvestTradeDto) {//交易撤销
            TrdBondInvestTradeDto dto = JsonUtils.json2Object(businessJson, TrdBondInvestTradeDto.class);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_成交确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);
            if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
            }
            return map;
        }

        // 银行间质押式回购-指令
        if (obj instanceof CfetsEtribRepoInfo003Dto || obj instanceof CfetsEtrRepoSaveRequest || StringUtil.equals(pageId, "CfetsEtrIbRepoinfo003")) {
            CfetsEtribRepoInfo003Dto dto = JsonUtils.json2Object(businessJson, CfetsEtribRepoInfo003Dto.class);
            cfetsEtrIbRepoinfo003Service.packageEquity(dto,new ArrayList<>());
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.REPO001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);
        }
        // 银行间质押式回购-对话/请求登记、对话报价（上行）check
        if (obj instanceof CfetsRepoDealQuoteCommonDto) {
            CfetsRepoDealQuoteCommonDto dto = JsonUtils.json2Object(businessJson, CfetsRepoDealQuoteCommonDto.class);
            if (!StringUtil.equals(dto.getOperatorType(), "done")) {
                return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.REPO001)
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                        .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                        .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                        .put(NodeConfigConstant.PS, dto.getPs())
                        .put(NodeConfigConstant.IS_CHECK, isCheck)
                        .put(NodeConfigConstant.DATA, dto);
            } else {
                // 对话报价成交check
                return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.REPO001)
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_对话报价.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, 系统动作_成交.getValue())
                        .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                        .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                        .put(NodeConfigConstant.PS, dto.getPs())
                        .put(NodeConfigConstant.IS_CHECK, isCheck)
                        .put(NodeConfigConstant.DATA, dto);
            }
        }
        // 银行间质押式回购-对话/请求登记 提交
        if (obj instanceof CfetsRepoDealRegisterQuoteDto || StringUtil.equals(pageId, "IbuQuoteRepo004")) {
            CfetsRepoDealRegisterQuoteDto dto = JsonUtils.json2Object(businessJson, CfetsRepoDealRegisterQuoteDto.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.REPO001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);

        }
        // 银行间质押式回购-对话报价（上行）成交
        if (obj instanceof CfetsRepoDealQuotedPrice001Dto || StringUtil.equals(pageId, "IbuQuoteRepo003")) {
//            CfetsRepoDealQuotedPrice001Dto dto = JsonUtils.json2Object(businessJson, CfetsRepoDealQuotedPrice001Dto.class);
//            if (StringUtil.equals(dto.getOperatorType(),"done")){
//                return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.REPO001)
//                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_对话报价.getValue())
//                        .put(NodeConfigConstant.DECISION_ACT, 系统动作_成交.getValue())
//                        .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
//                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
//                        .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
//                        .put(NodeConfigConstant.PS, dto.getPs())
//                        .put(NodeConfigConstant.IS_CHECK, isCheck)
//                        .put(NodeConfigConstant.DATA, dto);
//            }
        }
        // 银行间交易 - 成交确认 || 手工匹配
        if (obj instanceof CfetsTrdRepoTrade003Dto || StringUtil.equals(pageId, "CfetsTrdConfirm")) {
            CfetsTrdRepoTrade003Dto dto = JsonUtils.json2Object(businessJson, CfetsTrdRepoTrade003Dto.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.REPO001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_成交确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);
        }
        // 交易撤销
        if (obj instanceof CfetsTrdRepoTrade002Dto || StringUtil.equals(pageId, "CfetsTrdQuery")) {
            CfetsTrdRepoTrade002Dto dto = JsonUtils.json2Object(businessJson, CfetsTrdRepoTrade002Dto.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.REPO001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_成交确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, 交易场所_银行间.getValue())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);
        }
        /***********************************************一级债券 start********************************************/
        /***************** 指令 *********************/
        // 一级投标指令 check
        if (obj instanceof CfetsEtrBondFirstBidSaveRequest || StringUtil.equals(pageId, "CfetsEtrBondFirstBid002")) {
            return riskInputParamGenerateBusinessSerivce.generateEtrBondFirstBid(decisionAct, flowStatus, isCheck, businessJson);
        }
        /***************** 登记 *********************/
        // 一级投标确认
        if (obj instanceof CfetsTrdFirstBidFirmInfoDto || StringUtil.equals(pageId, "CfetsTrdFirstBidFirm002")) {
            return riskInputParamGenerateBusinessSerivce.generateCfetsTrdFirstBidFirmInfo(decisionAct, flowStatus, isCheck, businessJson);
        }
        // 一级申购
        if (obj instanceof CfetsTrdBondFirstSubInfoDto || StringUtil.equals(pageId, "TenderBondSub002")) {
            return riskInputParamGenerateBusinessSerivce.generateCfetsTrdBondFirstSubInfo(decisionAct, flowStatus, isCheck, businessJson);
        }
        /***************** 成交/中标结果维护 ***********/
        if (obj instanceof CfetsTrdFirstBondInvestTradeDto) {
            return riskInputParamGenerateBusinessSerivce.generateCfetsTrdFirstBondInvestTrade(decisionAct, flowStatus, isCheck, businessJson);
        }
        // 中标结果维护
        if (obj instanceof CfetsPrimaryBidManageSaveRequest || StringUtil.equals(pageId, "CfetsPrimaryBidManage002")) {
            return riskInputParamGenerateBusinessSerivce.generateCfetsPrimaryBidManage(decisionAct, flowStatus, isCheck, businessJson);
        }
        /***************** 交收 *********************/
        // 见上方交收统一处理
        /***********************************************一级债券   end********************************************/
        return null;
    }

    /**
     * 指标、授信、名单、授权参数组装
     *
     * @param obj
     * @param decisionAct
     * @param flowStatus
     * @param pageId
     * @return
     */
    public RiskCommonOutputDto buildParamThenRisk(Object obj, String decisionAct, String flowStatus, String pageId) {
        BaseMap map = generateBusinessDto(obj, decisionAct, flowStatus, true, pageId);
        RiskCommonInputDto inputDto = getRiskInputParam(map);
        if (inputDto == null) return new RiskCommonOutputDto();
        return doRisk(inputDto);
    }

    /**
     * 指标和授信占用
     *
     * @param obj
     * @param decisionAct
     * @param flowStatus
     * @param pageId
     */
    public void buildParamThenOccupy(Object obj, String decisionAct, String flowStatus, String pageId) {
        BaseMap map = generateBusinessDto(obj, decisionAct, flowStatus, false, pageId);
        RiskCommonInputDto inputDto = getRiskInputParam(map);
        if (inputDto == null) {
            return;
        }
        doOccupy(inputDto);
    }


    /**
     * 先获取配置信息，然后根据配置信息
     * 进行判断是否需要组装报文
     *
     * @param param 入参
     * @return
     */
    public RiskCommonInputDto getRiskInputParam(BaseMap param) {
        if (Objects.isNull(param)) {
            return null;
        }
        RiskNodeConfigDtl dtl = riskNodeConfigService.getNodeConfig(param);
        if (dtl == null) {
            return null;
        }
        Boolean isCheck = param.get("isCheck") == null ? false : (Boolean) param.get("isCheck");
        Object data = param.get(NodeConfigConstant.DATA);
        RiskCommonInputDto inputDto = new RiskCommonInputDto();
        BeanUtil.copyPropertiesIgnoreNull(inputDto, dtl);
        this.buildAuthorizeParam(inputDto, data, dtl);
        inputDto.setCreditInputList(this.buildCreditParam(data, dtl));
        inputDto.setRiskRdsDtoList(this.buildRiskRdsParam(data, dtl, isCheck));
        inputDto.setRiskNameList(this.buildRiskNameParam(data, dtl));
        logger.info("{}", JsonUtils.object2Json(inputDto));
        return inputDto;
    }


    /**
     * 构建指标计算参数
     *
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<InterfRiskRdsDto> buildRiskRdsParam(Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        if (this.isControl(dtl.getIndicatorControl())) {
            return null;
        }
        List<Map<String, Object>> list = null;
        if (data instanceof CfetsEtribBondInfo003Dto) {//银行间债券指令
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            CfetsEtribBondInfo003Dto dto = (CfetsEtribBondInfo003Dto) data;
            // 质押式回购指令关闭 共用
            if (StringUtil.isNotEmpty(dto.getAssetType())) {
                riskRdsDto.setAssetType(dto.getAssetType());
            }
            if (CollectionUtils.isNotEmpty(dto.getEtrInvestBondInfoDtos())) {
                list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getEtrInvestBondInfoDtos(), null);
            } else if (CollectionUtils.isNotEmpty(dto.getSelectedItem())) {
                list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getSelectedItem(), null);
            }
        }
        // 一级投标确认
        if (data instanceof CfetsTrdFirstBidFirmInfoDto) {
            list = riskInputBuildRiskRdsParamSerivce.buildRiskRdsCfetsTrdFirstBidFirmInfoParam(data, dtl, isCheck);
        }
        // 一级申购
        if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            list = riskInputBuildRiskRdsParamSerivce.buildRiskRdsCfetsTrdBondFirstSubInfoParam(data, dtl, isCheck);
        }
        // 成交确认-一级申购
        if (data instanceof CfetsTrdFirstBondInvestTradeDto) {
            list = riskInputBuildRiskRdsParamSerivce.buildRiskRdsCfetsTrdFirstBondInvestTradeParam(data, dtl, isCheck);
        }
        if (data instanceof CfetsPrimaryBidManageSaveRequest) {
            list = riskInputBuildRiskRdsParamSerivce.buildRiskRdsCfetsPrimaryBidManageSaveRequestParam(data, dtl, isCheck);
        }
        if (data instanceof CfetsTrdBondInvestTradeDto) {//银行间债券成交确认
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            CfetsTrdBondInvestTradeDto entity = (CfetsTrdBondInvestTradeDto) data;
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            List<TrdBondInvestTradeDto> list1 = new ArrayList<>();
            for (CfetsTrdBondInvestTradeDto item : entity.getDtoList()) {
                TrdBondInvestTradeDto bondDto = new TrdBondInvestTradeDto();
                BeanUtil.copyPropertiesIgnoreNull(bondDto, item);
                list1.add(bondDto);
            }
            if (CollectionUtils.isEmpty(list1) && !CollectionUtils.isEmpty(entity.getBussTrdTransfenpeiDto())) {//处理撤销时
                for (BussTrdTransfenpeiDto item : entity.getBussTrdTransfenpeiDto()) {
                    TrdBondInvestTradeDto bondDto = new TrdBondInvestTradeDto();
                    BeanUtil.copyPropertiesIgnoreNull(bondDto, item);
                    bondDto.setTotleTradeId(item.getTradeId());
                    list1.add(bondDto);
                }
            }
            if (CollectionUtils.isEmpty(list1) && CollectionUtils.isNotEmpty(entity.getTradeDtoList())) {
                list1.addAll(entity.getTradeDtoList());
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, list1, null);
        }
        if (data instanceof TrdBondInvestTradeDto) {//银行间债券交易撤销
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            TrdBondInvestTradeDto entity = (TrdBondInvestTradeDto) data;
            if (CollectionUtils.isNotEmpty(entity.getTrdBondInvestTradeDtoList())) {
                list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, entity.getTrdBondInvestTradeDtoList(), null);
            }
        }
        if (data instanceof CfetsManualSettleInsertDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            CfetsManualSettleInsertDto dto = (CfetsManualSettleInsertDto) data;
            if (StringUtil.isNotEmpty(dto.getTrdBankManualSettleInfo())
                    && (StringUtil.equals(dto.getTrdBankManualSettleInfo().getBusiType(), 头寸业务类型_债券一级投标.getValue())
                    || StringUtil.equals(dto.getTrdBankManualSettleInfo().getBusiType(), 头寸业务类型_债券一级申购.getValue()))) {
                riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            } else if (StringUtil.isNotEmpty(dto.getTrdBankManualSettleInfo())
                    && (StringUtil.equals(dto.getTrdBankManualSettleInfo().getBusiType(), 头寸业务类型_质押式回购到期.getValue()))) {
                riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            } else {
                riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            }
            List<TrdBondInvestTradeDto> list1 = new ArrayList<>();
            for (TrdBankManualSetDetail item : dto.getListTrdBnakSetDetails()) {
                TrdBondInvestTradeDto bondDto = new TrdBondInvestTradeDto();
                BeanUtil.copyPropertiesIgnoreNull(bondDto, item);
                bondDto.setTotleTradeId(item.getTradeId());
                bondDto.setTradeAmt(item.getSettleAmt());
                bondDto.setShareAmt(item.getSettleNum());
                bondDto.setFpriceAmt(item.getSettleAmt());
                bondDto.setPs(dto.getPs());
                list1.add(bondDto);
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, list1, null);
        }
        if (data instanceof CfetsBondDealRegisterQuoteDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            CfetsBondDealRegisterQuoteDto dto = (CfetsBondDealRegisterQuoteDto) data;
            List<Map> bussTrdTransfenpeiDto = new ArrayList<>();
            for (PtlAllocationDto ptlAllocationDto : dto.getPtlAllocatios()) {
                Map<String, Object> stringObjectMap = BeanUtil.object2Map(ptlAllocationDto);
                stringObjectMap.put("tradeAmount", ptlAllocationDto.getShareAmt());
                stringObjectMap.put("entrustNum", ptlAllocationDto.getShareAmt());
                stringObjectMap.put("fpriceAmt", ptlAllocationDto.getShareAmt());
                stringObjectMap.put("portfolioCode", ptlAllocationDto.getPortfolioId());
                stringObjectMap.put("finprodId", dto.getFinprodId());
                stringObjectMap.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                stringObjectMap.put("ps", dto.getPs());
                stringObjectMap.put("ccy", "CNY");
                stringObjectMap.put("tradeMarket", 交易场所_银行间.getValue());
                stringObjectMap.put("mdate", dto.getActSetteDate());
                bussTrdTransfenpeiDto.add(stringObjectMap);
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, bussTrdTransfenpeiDto, null);
        }
        // 银行间回购指令
        if (data instanceof CfetsEtribRepoInfo003Dto) {
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getEtrInvestRepoInfoDtos(), null);
        }
        // 银行间质押式回购登记
        if (data instanceof CfetsRepoDealRegisterQuoteDto) {
            CfetsRepoDealRegisterQuoteDto businessDto = (CfetsRepoDealRegisterQuoteDto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getPtlAllocations(), null);
        }
        // 银行间质押式回购-对话/请求登记、对话报价（上行）check
        if (data instanceof CfetsRepoDealQuoteCommonDto) {
            CfetsRepoDealQuoteCommonDto businessDto = (CfetsRepoDealQuoteCommonDto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getPtlAllocations(), null);
        }
        // 银行间质押式回购-对话报价（上行）成交
        if (data instanceof CfetsRepoDealQuotedPrice001Dto) {
            CfetsRepoDealQuotedPrice001Dto businessDto = (CfetsRepoDealQuotedPrice001Dto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getPtlAllocationList(), null);
        }
        // 银行间质押式回购  成交确认 || 手工匹配
        if (data instanceof CfetsTrdRepoTrade003Dto) {
            CfetsTrdRepoTrade003Dto businessDto = (CfetsTrdRepoTrade003Dto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            businessDto.getTrdProductDeals().forEach(item -> {
                item.setTotleTradeId(null);
                item.setFinprodId(null);
                item.setPs(businessDto.getPs());
            });
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getTrdProductDeals(), null);
        }
        // 交易撤销
        if (data instanceof CfetsTrdRepoTrade002Dto) {
            CfetsTrdRepoTrade002Dto tradeInfo = (CfetsTrdRepoTrade002Dto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            if (StringUtil.isEmptyList(tradeInfo.getBaseTradeProductDealDtos())) {
                return null;
            }
            List<TrdProductDeal> mapList = Arrays.asList(tradeInfo.getBaseTradeProductDealDtos().get(0).getTrdProductDeal());
            mapList.forEach(item -> {
                item.setTotleTradeId(null);
                item.setFinprodId(null);
                item.setPs(tradeInfo.getPs());
            });
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, mapList, null);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            List<InterfRiskRdsDto> result = new ArrayList<>();
            list.forEach(e -> result.add(BeanUtil.mapToObject(e, InterfRiskRdsDto.class)));
            return result;
        }
        return null;
    }

    /**
     * 构建授信计算参数
     *
     * @param data
     * @return
     */
    public List<CreditLinesInputDto> buildCreditParam(Object data, RiskNodeConfigDtl dtl) {
        if (this.isControl(dtl.getCreditControl())) {
            return null;
        }
        List<CreditLinesInputDto> result = new ArrayList<>();
        if (data instanceof CfetsEtribBondInfo003Dto) {//银行间债券指令
            List<CfetsEtribBondInfo003Dto> list = ((CfetsEtribBondInfo003Dto) data).getEtrInvestBondInfoDtos();
            if (CollectionUtils.isEmpty(list)) {
                list = ((CfetsEtribBondInfo003Dto) data).getSelectedItem();
            }
            //这里处理授信
            list.forEach(e -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, e);
                this.generateInputDto(inputDto, e, e);
                result.add(inputDto);
            });

        }
        if (data instanceof CfetsTrdBondInvestTradeDto) {//成交确认
            CfetsTrdBondInvestTradeDto entity = (CfetsTrdBondInvestTradeDto) data;
            entity.getDtoList().forEach(e -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, data);
                this.generateInputDto(inputDto, e, null);
                result.add(inputDto);
            });
        }
        // 成交确认-一级申购
        if (data instanceof CfetsTrdFirstBondInvestTradeDto) {
            result.addAll(riskInputBuildCreditParamSerivce.buildCreditCfetsTrdFirstBondInvestTradeParam(data, dtl));
        }
        // 一级投标指令
        if (data instanceof CfetsEtrBondFirstBidSaveRequest) {
            result.addAll(riskInputBuildCreditParamSerivce.buildCreditCfetsEtrBondFirstBidParam(data, dtl));
        }
        // 一级投标确认
        if (data instanceof CfetsTrdFirstBidFirmInfoDto) {
            result.addAll(riskInputBuildCreditParamSerivce.buildCreditCfetsTrdFirstBidFirmInfoParam(data, dtl));
        }
        // 一级申购
        if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            result.addAll(riskInputBuildCreditParamSerivce.buildCreditCfetsTrdBondFirstSubInfoParam(data, dtl));
        }
        // 中表结果维护
        if (data instanceof CfetsPrimaryBidManageSaveRequest) {
            result.addAll(riskInputBuildCreditParamSerivce.buildCfetsPrimaryBidManageSaveRequestParam(data, dtl));
        }
        if (data instanceof CfetsManualSettleInsertDto) {
            CfetsManualSettleInsertDto dto = (CfetsManualSettleInsertDto) data;
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, dto);
            this.generateInputDto(inputDto, dto.getTrdBankManualSettleInfo(), dto);
            result.add(inputDto);
        }
        if (data instanceof TrdBondInvestTradeDto) {//银行间债券交易撤销
            TrdBondInvestTradeDto entity = (TrdBondInvestTradeDto) data;
            if (CollectionUtils.isNotEmpty(entity.getTrdBondInvestTradeDtoList())) {
                entity.getTrdBondInvestTradeDtoList().forEach(e -> {
                    CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, entity);
                    this.generateInputDto(inputDto, e, null);
                    result.add(inputDto);
                });
            }
        }
        // 银行间回购指令
        if (data instanceof CfetsEtribRepoInfo003Dto) {
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            // 组合分配信息
            List<CfetsEtribRepoInfo003Dto> list = businessDto.getEtrInvestRepoInfoDtos();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, item);
                this.generateInputDto(inputDto, businessDto, item);
                result.add(inputDto);
            });
        }
        if (data instanceof CfetsRepoDealRegisterQuoteDto) {
            // 银行间回购登记
            CfetsRepoDealRegisterQuoteDto businessDto = (CfetsRepoDealRegisterQuoteDto) data;
            // 组合分配信息
            List<RepoPtlAllocationDto> list = businessDto.getPtlAllocations();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, item);
                this.generateInputDto(inputDto, businessDto, item);
                result.add(inputDto);
            });
        }
        // 银行间质押式回购-对话/请求登记、对话报价（上行）check
        if (data instanceof CfetsRepoDealQuoteCommonDto) {
            CfetsRepoDealQuoteCommonDto businessDto = (CfetsRepoDealQuoteCommonDto) data;
            // 组合分配信息
            List<RepoPtlAllocationDto> list = businessDto.getPtlAllocations();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, item);
                this.generateInputDto(inputDto, businessDto, item);
                result.add(inputDto);
            });
        }
        // 银行间质押式回购-对话报价（上行）成交
        if (data instanceof CfetsRepoDealQuotedPrice001Dto) {
            CfetsRepoDealQuotedPrice001Dto businessDto = (CfetsRepoDealQuotedPrice001Dto) data;
            // 组合分配信息
            List<com.joyintech.tams.invest.common.dto.common.PtlAllocationDto> list
                    = businessDto.getPtlAllocationList();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, item);
                this.generateInputDto(inputDto, businessDto, item);
                result.add(inputDto);
            });
        }
        // 银行间质押式回购  成交确认 || 手工匹配
        if (data instanceof CfetsTrdRepoTrade003Dto) {
            CfetsTrdRepoTrade003Dto businessDto = (CfetsTrdRepoTrade003Dto) data;
            //这里处理授信
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, businessDto);
            this.generateInputDto(inputDto, businessDto, null);
            result.add(inputDto);

        }
        if (data instanceof CfetsTrdRepoTrade002Dto) {
            // 银行间质押式回购  成交确认撤销
            CfetsTrdRepoTrade002Dto businessDto = (CfetsTrdRepoTrade002Dto) data;
            //这里处理授信
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, businessDto);
            generateInputDto(inputDto, businessDto, null);
            result.add(inputDto);
        }
        return result;
    }


    /**
     * 构建名单计算参数
     *
     * @param data
     * @return
     */
    public List<RiskNameListDto> buildRiskNameParam(Object data, RiskNodeConfigDtl dtl) {
        if (this.isControl(dtl.getNameListControl())) {
            return null;
        }
        RiskNameListDto listDto = new RiskNameListDto();
        if (data instanceof CfetsTrdFirstBidFirmInfoDto) {
            // 一级投标确认
            Map<String, Object> map = BeanUtil.object2Map(data);
            listDto.setFinprodId((String) map.get("finprodId"));
            listDto.setCounterId((String) map.get("counterId"));
        } else if (data instanceof CfetsEtrBondFirstBidSaveRequest) {
            // 一级投标指令
            Map<String, Object> map = BeanUtil.object2Map(data);
            listDto.setFinprodId((String) map.get("finprodId"));
            listDto.setCounterId((String) map.get("counterId"));
        } else if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            // 一级申购
            CfetsTrdBondFirstSubInfoDto dto = (CfetsTrdBondFirstSubInfoDto) data;
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(dto.getProductDtos().get(0).getEntrustId());
            listDto.setFinprodId(dto.getFinprodId());
            listDto.setCounterId(etrEntrust.getCounterId());
        } else if (data instanceof CfetsPrimaryBidManageSaveRequest) {
            // 中标结果维护
            Map<String, Object> map = BeanUtil.object2Map(data);
            listDto.setFinprodId((String) map.get("finprodId"));
            listDto.setCounterId((String) map.get("counterId"));
        } else if (data instanceof CfetsEtribRepoInfo003Dto) {
            // 银行间回购指令
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
        } else if (data instanceof CfetsRepoDealRegisterQuoteDto) {
            // 银行间回购指令
            CfetsRepoDealRegisterQuoteDto businessDto = (CfetsRepoDealRegisterQuoteDto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
        } else if (data instanceof CfetsRepoDealQuoteCommonDto) {
            // 银行间质押式回购-对话/请求登记、对话报价（上行）check
            CfetsRepoDealQuoteCommonDto businessDto = (CfetsRepoDealQuoteCommonDto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
        } else if (data instanceof CfetsRepoDealQuotedPrice001Dto) {
            // 银行间质押式回购-对话报价（上行）成交
            CfetsRepoDealQuotedPrice001Dto businessDto = (CfetsRepoDealQuotedPrice001Dto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
        } else if (data instanceof CfetsTrdRepoTrade003Dto) {
            // 银行间质押式回购 - 成交确认  ||  手工确认
            CfetsTrdRepoTrade003Dto businessDto = (CfetsTrdRepoTrade003Dto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
        } else if (data instanceof CfetsTrdRepoTrade003Dto) {
            // 交易撤销
            CfetsTrdRepoTrade003Dto tradeInfo = (CfetsTrdRepoTrade003Dto) data;
            listDto.setCounterId(tradeInfo.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(tradeInfo.getPs());
        } else if (data instanceof CfetsManualSettleInsertDto) {
            CfetsManualSettleInsertDto tradeInfo = (CfetsManualSettleInsertDto) data;
            TrdProductDeal trdProductDeal = trdProductDealMapper.selectByPrimaryKey(tradeInfo.getTrdBankManualSettleInfo().getTradeId());
            listDto.setFinprodId(tradeInfo.getTrdBankManualSettleInfo().getFinprodId());
            listDto.setCounterId(trdProductDeal.getCounterId());
        } else {
            Map<String, Object> map = BeanUtil.object2Map(data);
            listDto.setFinprodId((String) map.get("finprodId"));
            listDto.setCounterId((String) map.get("counterId"));
            //如果是质押式回购
            //listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            //listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            //listDto.setInputValue3(DictCodeListExt3.行内资产三级分类_正回购.getValue());
        }
        return Lists.newArrayList(listDto);
    }

    /**
     * 构建授权试算参数
     *
     * @param data
     * @param dtl
     * @return
     */
    public void buildAuthorizeParam(RiskCommonInputDto dto, Object data, RiskNodeConfigDtl dtl) {
        if (this.isControl(dtl.getAuthorizeControl())) return;
        if (data instanceof CfetsTrdFirstBidFirmInfoDto) {
            // 一级投标确认
            riskInputBuildAuthorizeParamSerivce.buildCfetsTrdFirstBidFirmInfoParam(dto, data);
        } else if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            // 一级申购
            riskInputBuildAuthorizeParamSerivce.buildCfetsTrdBondFirstSubInfoParam(dto, data);
        } else if (data instanceof CfetsPrimaryBidManageSaveRequest) {
            // 中标结果维护
            riskInputBuildAuthorizeParamSerivce.buildCfetsPrimaryBidManageSaveRequestParam(dto, data);
        } else if (data instanceof CfetsEtribRepoInfo003Dto) {
            // 银行间回购指令
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getEntrustAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsRepoDealRegisterQuoteDto) {
            // 银行间回购交易
            CfetsRepoDealRegisterQuoteDto businessDto = (CfetsRepoDealRegisterQuoteDto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsRepoDealQuoteCommonDto) {
            // 银行间质押式回购-对话/请求登记、对话报价（上行）check
            CfetsRepoDealQuoteCommonDto businessDto = (CfetsRepoDealQuoteCommonDto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsRepoDealQuotedPrice001Dto) {
            // 银行间质押式回购-对话报价（上行）成交
            CfetsRepoDealQuotedPrice001Dto businessDto = (CfetsRepoDealQuotedPrice001Dto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getDealAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsTrdRepoTrade003Dto) {
            // 银行间质押式回购  成交确认 || 手工匹配
            CfetsTrdRepoTrade003Dto businessDto = (CfetsTrdRepoTrade003Dto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsTrdRepoTrade002Dto) {
            // 交易撤销
            CfetsTrdRepoTrade002Dto businessDto = (CfetsTrdRepoTrade002Dto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsManualSettleInsertDto) { //成交确认
            CfetsManualSettleInsertDto businessDto = (CfetsManualSettleInsertDto) data;
            TrdProductDeal trdProductDeal = trdProductDealMapper.selectByPrimaryKey(businessDto.getTradeId());
            dto.setCounterId(trdProductDeal.getCounterId());
            dto.setPrinAmt(businessDto.getTrdBankManualSettleInfo().getSettleAmt());
            if (StringUtil.equals(businessDto.getTrdBankManualSettleInfo().getBusiType(), 头寸业务类型_债券一级投标.getValue())
                    || StringUtil.equals(businessDto.getTrdBankManualSettleInfo().getBusiType(), 头寸业务类型_债券一级申购.getValue())) {
                dto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            } else if (StringUtil.equals(businessDto.getTrdBankManualSettleInfo().getBusiType(), 头寸业务类型_质押式回购到期.getValue())) {
                dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            } else {
                dto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            }
            dto.setTradeMarket(trdProductDeal.getTradeMarket());
            dto.setPs(businessDto.getPs());
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(businessDto.getTrdBankManualSettleInfo().getFinprodId());
            key.setBranch(Long.valueOf(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            dto.setAssetSecType(finProductType.getType9());
            dto.setFinprodId(businessDto.getTrdBankManualSettleInfo().getFinprodId());
        } else if (data instanceof CfetsEtrBondFirstBidSaveRequest) {
            // 一级投标指令
            riskInputBuildAuthorizeParamSerivce.buildEtrBondFirstBidAuthorizeParam(dto, data);
        } else {
            Map<String, Object> map = BeanUtil.object2Map(data);
            dto.setCounterId((String) map.get("counterId"));
            dto.setPrinAmt((BigDecimal) map.get("tradeAmt"));
            dto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            dto.setTradeMarket((String) map.get("tradeMarket"));
            dto.setPs((String) map.get("ps"));
            dto.setAssetSecType((String) map.get("assetSecType"));
            dto.setFinprodId((String) map.get("finprodId"));
        }
    }

    /**
     * 为空或者不控返回true
     *
     * @param control
     * @return
     */
    private boolean isControl(String control) {
        return StringUtil.isEmpty(control) || StringUtil.equals(control, DictCodeListRsk.风险控制类别_不控.getValue());
    }

    /**
     * 构建授信参数
     *
     * @param obj
     * @return
     */
    public void generateInputDto(CreditLinesInputDto creditLinesInputDto, Object obj, Object detailObj) {
        if (obj instanceof CfetsEtribBondInfo003Dto) {//银行间债券指令
            CfetsEtribBondInfo003Dto inputDto = (CfetsEtribBondInfo003Dto) obj;
            // 质押式回购指令关闭
            if (StringUtil.equals(inputDto.getAssetType(), DictCodeList.平安资产类型_质押式回购.getValue())) {
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                creditLinesInputDto.setTradeDate(inputDto.getVdate());
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
                creditLinesInputDto.setPsPayment(inputDto.getAssetType1());
                creditLinesInputDto.setSaleCode(inputDto.getPs());
                creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                creditLinesInputDto.setCounterId(inputDto.getCounterId());
                creditLinesInputDto.setTradeAmt(inputDto.getEntrustAmt());
            } else {
                creditLinesInputDto.setTradeId(inputDto.getEntrustId());
                creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                creditLinesInputDto.setTradeDate(inputDto.getVdate());
                creditLinesInputDto.setPs(inputDto.getPs());
                if (StringUtil.isNotEmpty(inputDto.getEtrInvestPersonInfo())) {
                    creditLinesInputDto.setCounterId(inputDto.getEtrInvestPersonInfo().getCounterId());
                }
                creditLinesInputDto.setTradeAmt(inputDto.getEntrustNum());
                if (inputDto.getEntrustNum() != null) {
                    creditLinesInputDto.setShareAmt(inputDto.getEntrustNum().divide(new BigDecimal(100), RoundingMode.HALF_UP));
                }
                //最新面值
                BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(creditLinesInputDto);
                if (newFaceValue == null) {
                    newFaceValue = new BigDecimal(100);
                }
                if (creditLinesInputDto.getShareAmt() != null) {
                    creditLinesInputDto.setTradeAmt(newFaceValue.multiply(creditLinesInputDto.getShareAmt()));
                } else if (creditLinesInputDto.getTradeAmt() != null) {
                    creditLinesInputDto.setTradeAmt(newFaceValue.multiply(ArithmeticUtil.divide(creditLinesInputDto.getTradeAmt(), 100, 2)));
                }
            }
        }
        if (obj instanceof CfetsTrdBondInvestTradeDto) {//债券成交确认
            CfetsTrdBondInvestTradeDto inputDto = (CfetsTrdBondInvestTradeDto) obj;
            //creditLinesInputDto = new CreditLinesInputDto();
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            creditLinesInputDto.setEntrustId(inputDto.getEntrustId());
            // 债券卖出取交割日
            if (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue()) || (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_行权_回售.getValue()))) {
                creditLinesInputDto.setPs(DictCodeList.交易方向_卖出.getValue());
                creditLinesInputDto.setTradeDate(inputDto.getSettleDate());
            }
            if (StringUtil.isNotEmpty(inputDto.getTrdInvestPersonInfo())) {
                creditLinesInputDto.setCounterId(inputDto.getTrdInvestPersonInfo().getCounterId());
            }
            creditLinesInputDto.setTradeId(inputDto.getTotleTradeId());
            creditLinesInputDto.setTradeAmt(inputDto.getTradeAmt());
            //最新面值
            BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(creditLinesInputDto);
            if (newFaceValue == null) {
                newFaceValue = new BigDecimal(100);
            }
            if (creditLinesInputDto.getShareAmt() != null) {
                creditLinesInputDto.setTradeAmt(newFaceValue.multiply(creditLinesInputDto.getShareAmt()));
            } else if (creditLinesInputDto.getTradeAmt() != null) {
                creditLinesInputDto.setTradeAmt(newFaceValue.multiply(ArithmeticUtil.divide(creditLinesInputDto.getTradeAmt(), 100, 2)));
            }
        }
        if (obj instanceof TrdBondInvestTradeDto) {//债券撤销
            TrdBondInvestTradeDto inputDto = (TrdBondInvestTradeDto) obj;
            //creditLinesInputDto = new CreditLinesInputDto();
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            creditLinesInputDto.setEntrustId(inputDto.getEntrustId());
            creditLinesInputDto.setTradeDate(inputDto.getTradeDate());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setTradeId(inputDto.getTradeId());
            // 债券卖出取交割日
            if (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue()) || (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_行权_回售.getValue()))) {
                creditLinesInputDto.setPs(DictCodeList.交易方向_卖出.getValue());
                creditLinesInputDto.setTradeDate(inputDto.getSettleDate());
            }
            if (StringUtil.isNotEmpty(inputDto.getTrdInvestPersonInfo())) {
                creditLinesInputDto.setCounterId(inputDto.getTrdInvestPersonInfo().getCounterId());
            }
            creditLinesInputDto.setTradeId(inputDto.getTotleTradeId());
            creditLinesInputDto.setTradeAmt(inputDto.getTradeAmt());
            //最新面值
            BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(creditLinesInputDto);
            if (newFaceValue == null) {
                newFaceValue = new BigDecimal(100);
            }
            if (creditLinesInputDto.getShareAmt() != null) {
                creditLinesInputDto.setTradeAmt(newFaceValue.multiply(creditLinesInputDto.getShareAmt()));
            } else if (creditLinesInputDto.getTradeAmt() != null) {
                creditLinesInputDto.setTradeAmt(newFaceValue.multiply(ArithmeticUtil.divide(creditLinesInputDto.getTradeAmt(), 100, 2)));
            }
        }
        if (obj instanceof TrdBankManualSettleInfo) {
            TrdBankManualSettleInfo inputDto = (TrdBankManualSettleInfo) obj;
            CfetsManualSettleInsertDto insertDto = (CfetsManualSettleInsertDto) detailObj;
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
            creditLinesInputDto.setTradeId(inputDto.getTradeId());
            creditLinesInputDto.setTradeAmt(inputDto.getSettleAmt());
            creditLinesInputDto.setTradeDate(inputDto.getSettleDate());
            if (inputDto.getBusiType().equals(头寸业务类型_质押式回购到期.getValue()) && insertDto.getPs().equals(交易方向_正回购.getValue())) {
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
            } else if (inputDto.getBusiType().equals(头寸业务类型_质押式回购到期.getValue()) && insertDto.getPs().equals(交易方向_逆回购.getValue())) {
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            } else {
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            }
        }
        // 银行间回购指令
        if (obj instanceof CfetsEtribRepoInfo003Dto) {
            CfetsEtribRepoInfo003Dto inputDto = (CfetsEtribRepoInfo003Dto) obj;
            CfetsEtribRepoInfo003Dto item = (CfetsEtribRepoInfo003Dto) detailObj;
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            creditLinesInputDto.setTradeId(item.getEntrustId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setTradeDate(inputDto.getVdate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
            creditLinesInputDto.setPsPayment(inputDto.getAssetType1());
            creditLinesInputDto.setSaleCode(inputDto.getPs());
            creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
            creditLinesInputDto.setCounterId(inputDto.getCounterId());
            creditLinesInputDto.setTradeAmt(item.getEntrustAmt());

        }
        if (obj instanceof CfetsRepoDealRegisterQuoteDto) {
            CfetsRepoDealRegisterQuoteDto inputDto = (CfetsRepoDealRegisterQuoteDto) obj;
            RepoPtlAllocationDto item = (RepoPtlAllocationDto) detailObj;

            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            creditLinesInputDto.setTradeId(item.getEntrustId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setTradeDate(inputDto.getTradeDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
            creditLinesInputDto.setPsPayment(inputDto.getRepoType());
            creditLinesInputDto.setSaleCode(inputDto.getPs());
            creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
            creditLinesInputDto.setCounterId(inputDto.getCounterId());
            creditLinesInputDto.setTradeAmt(item.getTradeAmt());

        }
        // 银行间质押式回购-对话/请求登记、对话报价（上行）check
        if (obj instanceof CfetsRepoDealQuoteCommonDto) {
            CfetsRepoDealQuoteCommonDto inputDto = (CfetsRepoDealQuoteCommonDto) obj;
            RepoPtlAllocationDto item = (RepoPtlAllocationDto) detailObj;

            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            creditLinesInputDto.setTradeDate(inputDto.getTradeDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
            creditLinesInputDto.setPsPayment(DictCodeList.回购类型_质押式回购.getValue());
            creditLinesInputDto.setSaleCode(inputDto.getPs());
            creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
            creditLinesInputDto.setCounterId(inputDto.getCounterId());
            creditLinesInputDto.setTradeAmt(item.getTradeAmt());

        }
        // 银行间质押式回购-对话报价（上行）成交
        if (obj instanceof CfetsRepoDealQuotedPrice001Dto) {
            CfetsRepoDealQuotedPrice001Dto inputDto = (CfetsRepoDealQuotedPrice001Dto) obj;
            PtlAllocationDto item = (PtlAllocationDto) detailObj;

            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            creditLinesInputDto.setTradeId(inputDto.getQuoteId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setTradeDate(inputDto.getTradeDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
            creditLinesInputDto.setPsPayment(DictCodeList.回购类型_质押式回购.getValue());
            creditLinesInputDto.setSaleCode(inputDto.getPs());
            creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
            creditLinesInputDto.setCounterId(inputDto.getCounterId());
            creditLinesInputDto.setTradeAmt(item.getCpriceAmt());

        }
        // 成交确认 || 手工匹配
        if (obj instanceof CfetsTrdRepoTrade003Dto || obj instanceof CfetsTrdConfirmManualMatchInfoRequest) {
            CfetsTrdRepoTrade003Dto detailDto = (CfetsTrdRepoTrade003Dto) obj;
            if (StringUtil.isEmptyList(detailDto.getTrdProductDeals())
                    || StringUtil.isEmptyList(detailDto.getTrdProductDealAdds())) {
                return;
            }
            TrdProductDeal trdProductDeal = detailDto.getTrdProductDeals().get(0);
            TrdProductDealAdd trdProductDealAdd = detailDto.getTrdProductDealAdds().get(0);
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            creditLinesInputDto.setTradeId(trdProductDeal.getTradeId());
            creditLinesInputDto.setEntrustId(trdProductDealAdd.getEntrustId());
            creditLinesInputDto.setFinprodId(detailDto.getFinprodId());
            creditLinesInputDto.setTradeDate(detailDto.getTradeDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
            creditLinesInputDto.setPsPayment(DictCodeList.回购类型_质押式回购.getValue());
            creditLinesInputDto.setSaleCode(detailDto.getPs());
            creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
            creditLinesInputDto.setCounterId(detailDto.getCounterId());
            creditLinesInputDto.setTradeAmt(trdProductDeal.getTradeAmt());
        }
        // 交易撤销
        if (obj instanceof CfetsTrdRepoTrade002Dto) {
            CfetsTrdRepoTrade002Dto inputDto = (CfetsTrdRepoTrade002Dto) obj;
            List<BaseTradeProductDealDto> list = inputDto.getBaseTradeProductDealDtos();
            if (StringUtil.isEmptyList(list)) {
                return;
            }
            TrdProductDeal trdProductDeal = list.get(0).getTrdProductDeal();
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            // 指令编号+组合代码
            creditLinesInputDto.setTradeId(trdProductDeal.getTradeId());
            creditLinesInputDto.setFinprodId(trdProductDeal.getFinprodId());
            creditLinesInputDto.setTradeDate(trdProductDeal.getTradeDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
            creditLinesInputDto.setPsPayment(DictCodeList.回购类型_质押式回购.getValue());
            creditLinesInputDto.setSaleCode(inputDto.getPs());
            creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
            creditLinesInputDto.setCounterId(trdProductDeal.getCounterId());
            creditLinesInputDto.setTradeAmt(trdProductDeal.getTradeAmt());
        }
        if (obj instanceof CfetsManualSettleInsertDto) {
            CfetsManualSettleInsertDto inputDto = (CfetsManualSettleInsertDto) obj;
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
            if (StringUtil.equals(inputDto.getTrdBankManualSettleInfo().getBusiType(), 头寸业务类型_质押式回购到期.getValue())) {
                TrdProductDeal trdProductDeal = trdProductDealMapper.selectByPrimaryKey(inputDto.getTradeId());
                TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(inputDto.getTradeId());
                creditLinesInputDto.setFinprodId(null);
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                creditLinesInputDto.setTradeId(inputDto.getTradeId());
                creditLinesInputDto.setCounterId(trdProductDeal.getCounterId());
                creditLinesInputDto.setTradeAmt(trdProductDeal.getPrinAmt());
                creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
                creditLinesInputDto.setPsPayment(DictCodeList.回购类型_质押式回购.getValue());
                creditLinesInputDto.setSaleCode(inputDto.getPs());
                return;
            }
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            TrdProductDeal trdProductDeal = trdProductDealMapper.selectByPrimaryKey(inputDto.getTradeId());
            TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(inputDto.getTradeId());
            creditLinesInputDto.setEntrustId(trdProductDealAdd.getEntrustId());
            creditLinesInputDto.setTradeDate(inputDto.getTrdBankManualSettleInfo().getSettleDate());
            creditLinesInputDto.setCounterId(trdProductDeal.getCounterId());
            creditLinesInputDto.setTradeId(inputDto.getTradeId());
            creditLinesInputDto.setTradeAmt(inputDto.getTrdBankManualSettleInfo().getSettleAmt());
            BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(creditLinesInputDto);
            if (newFaceValue == null) {
                newFaceValue = new BigDecimal(100);
            }
            if (creditLinesInputDto.getShareAmt() != null) {
                creditLinesInputDto.setTradeAmt(newFaceValue.multiply(creditLinesInputDto.getShareAmt()));
            } else if (creditLinesInputDto.getTradeAmt() != null) {
                creditLinesInputDto.setTradeAmt(newFaceValue.multiply(ArithmeticUtil.divide(creditLinesInputDto.getTradeAmt(), 100, 2)));
            }
        }
    }
}

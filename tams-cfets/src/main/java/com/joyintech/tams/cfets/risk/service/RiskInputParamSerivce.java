package com.joyintech.tams.cfets.risk.service;

import com.google.common.collect.Lists;
import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.bankuplink.common.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribSaveRequest;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfoService;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsBiddingProductDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrRepoSaveRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtribRepoInfo003Dto;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleDto;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleInsertDto;
import com.joyintech.tams.cfets.manualsettle.service.CfetsManualSettleService;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmEntrustDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmProductDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubEntrustDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveDetailDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveRequest;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.repo.dto.RepoPtlAllocationDto;
import com.joyintech.tams.cfets.trdconfirm.dto.*;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdQueryTradeInfoListResponse;
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
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesDealsMapper;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade002Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private CfetsManualSettleService cfetsManualSettleService;

    @Autowired
    private RiskInputParamGenerateBusinessSerivce riskInputParamGenerateBusinessSerivce;

    @Autowired
    private RiskInputBuildAuthorizeParamSerivce riskInputBuildAuthorizeParamSerivce;

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
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
        }
        if (obj instanceof CfetsEtribSaveRequest || StringUtil.equals(pageId, "CfetsEtrBondInfo")) {
            CfetsEtribSaveRequest allDto = JsonUtils.json2Object(businessJson, CfetsEtribSaveRequest.class);
            CfetsEtribBondInfo003Dto dto = cfetsEtrIbBondinfoService.packageEquity(allDto);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
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
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
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
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
            if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
            }
            return map;
        }
        if (obj instanceof CfetsManualSettleDto || StringUtil.equals(pageId, "CfetsManualSettle")) {
            CfetsManualSettleDto allDto = JsonUtils.json2Object(businessJson, CfetsManualSettleDto.class);
            List<CfetsManualSettleInsertDto> list = cfetsManualSettleService.handleSettleData(allDto);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_交收确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券存续期交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", allDto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", list.get(0));
            if (allDto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || allDto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                map.put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.REPO003);
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购到期.getValue());
            }
            if (StringUtil.equals(allDto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
            }
            if (StringUtil.equals(allDto.getButtonType(), 交收操作类型_回溯.getValue())) {
                map.put(NodeConfigConstant.BUSI_ACT, 业务动作_交收回溯.getValue());
            }
            if (StringUtil.equals(allDto.getButtonType(), 交收操作类型_交收失败.getValue())) {
                map.put(NodeConfigConstant.BUSI_ACT, 业务动作_交收失败.getValue());
            }
            if (StringUtil.equals(allDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                    || StringUtil.equals(allDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                map.put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND002);
            }
        }
        if (obj instanceof CfetsTrdFirstBidFirmInfoDto || StringUtil.equals(pageId, "CfetsTrdFirstBidFirm002")) {
            CfetsTrdFirstBidFirmInfoDto dto = JsonUtils.json2Object(businessJson, CfetsTrdFirstBidFirmInfoDto.class);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级投标.getValue())
                    .put("tradePlace", dto.getTradeMarket())
                    .put("ps", DictCodeList.交易方向_买入.getValue())
                    .put("isCheck", isCheck)
                    .put("data", dto);
            if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())
              || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                map.put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND002);
            }
            return map;
        }
        if (obj instanceof CfetsTrdBondFirstSubInfoDto || StringUtil.equals(pageId, "TenderBondSub002")) {
            CfetsTrdBondFirstSubInfoDto dto = JsonUtils.json2Object(businessJson, CfetsTrdBondFirstSubInfoDto.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级申购.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", DictCodeList.交易方向_买入.getValue())
                    .put("isCheck", isCheck)
                    .put("data", dto);
        }
        if (obj instanceof CfetsPrimaryBidManageSaveRequest || StringUtil.equals(pageId, "CfetsPrimaryBidManage002")) {
            CfetsPrimaryBidManageSaveRequest dto = JsonUtils.json2Object(businessJson, CfetsPrimaryBidManageSaveRequest.class);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_中标结果维护.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级投标.getValue())
                    .put("tradePlace", dto.getTradeMarket())
                    .put("ps", DictCodeList.交易方向_买入.getValue())
                    .put("isCheck", isCheck)
                    .put("data", dto);
            if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                    || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                map.put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND002);
            }
            return map;
        }
        if (obj instanceof CfetsTrdBondInvestTradeDto) {
            CfetsTrdBondInvestTradeDto dto = JsonUtils.json2Object(businessJson, CfetsTrdBondInvestTradeDto.class);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_成交确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", dto.getTradeMarket())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
            if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
            }
            return map;
        }
        if (obj instanceof CfetsTrdFirstBondInvestTradeDto) {
            CfetsTrdFirstBondInvestTradeDto dto = JsonUtils.json2Object(businessJson, CfetsTrdFirstBondInvestTradeDto.class);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_成交确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", dto.getTradeMarket())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
            if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                    || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                map.put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND002);
            }
            return map;
        }
        if(obj instanceof TrdBondInvestTradeDto) {//交易撤销
            TrdBondInvestTradeDto dto = JsonUtils.json2Object(businessJson, TrdBondInvestTradeDto.class);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_成交确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
            if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易.getValue());
            }
            return map;
        }

        // 银行间质押式回购-指令
        if (obj instanceof CfetsEtribRepoInfo003Dto || obj instanceof CfetsEtrRepoSaveRequest || StringUtil.equals(pageId, "CfetsEtrIbRepoinfo003")){
            CfetsEtribRepoInfo003Dto dto = JsonUtils.json2Object(businessJson, CfetsEtribRepoInfo003Dto.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "REPO001")
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
        }
        // 银行间质押式回购-对话/请求登记、对话报价（上行）check
        if (obj instanceof CfetsRepoDealQuoteCommonDto) {
            CfetsRepoDealQuoteCommonDto dto = JsonUtils.json2Object(businessJson, CfetsRepoDealQuoteCommonDto.class);
            if (!StringUtil.equals(dto.getOperatorType(),"done")){
                return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "REPO001")
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                        .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                        .put("tradePlace", 交易场所_银行间.getValue())
                        .put("ps", dto.getPs())
                        .put("isCheck", isCheck)
                        .put("data", dto);
            }else {
                // 对话报价成交check
                return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "REPO001")
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_对话报价.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, 系统动作_成交.getValue())
                        .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                        .put("tradePlace", 交易场所_银行间.getValue())
                        .put("ps", dto.getPs())
                        .put("isCheck", isCheck)
                        .put("data", dto);
            }
        }
        // 银行间质押式回购-对话/请求登记 提交
        if (obj instanceof CfetsRepoDealRegisterQuoteDto || StringUtil.equals(pageId, "IbuQuoteRepo004")) {
            CfetsRepoDealRegisterQuoteDto dto = JsonUtils.json2Object(businessJson, CfetsRepoDealRegisterQuoteDto.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "REPO001")
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);

        }
        // 银行间质押式回购-对话报价（上行）成交
        if (obj instanceof CfetsRepoDealQuotedPrice001Dto || StringUtil.equals(pageId, "IbuQuoteRepo003")) {
//            CfetsRepoDealQuotedPrice001Dto dto = JsonUtils.json2Object(businessJson, CfetsRepoDealQuotedPrice001Dto.class);
//            if (StringUtil.equals(dto.getOperatorType(),"done")){
//                return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "REPO001")
//                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_对话报价.getValue())
//                        .put(NodeConfigConstant.DECISION_ACT, 系统动作_成交.getValue())
//                        .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
//                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
//                        .put("tradePlace", 交易场所_银行间.getValue())
//                        .put("ps", dto.getPs())
//                        .put("isCheck", isCheck)
//                        .put("data", dto);
//            }
        }
        // 银行间交易 - 成交确认 || 手工匹配
        if (obj instanceof CfetsTrdRepoTrade003Dto || StringUtil.equals(pageId, "CfetsTrdConfirm")) {
            CfetsTrdRepoTrade003Dto dto = JsonUtils.json2Object(businessJson, CfetsTrdRepoTrade003Dto.class);
            String busiAct = "";
            if (dto.getOprType().equals(DictCodeListExt3.操作类型_手工匹配确认.getValue())) {
                busiAct = DictCodeListExt3.操作类型_手工匹配确认.getValue();
            } else {
                busiAct = 业务动作_成交确认.getValue();
            }
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "REPO001")
                    .put(NodeConfigConstant.BUSI_ACT, busiAct)
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
        }
        // 交易撤销
        if (obj instanceof TrdRepoTrade002Dto || StringUtil.equals(pageId, "CfetsTrdQuery")) {
            TrdRepoTrade002Dto dto = JsonUtils.json2Object(businessJson, TrdRepoTrade002Dto.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "REPO001")
                    .put(NodeConfigConstant.BUSI_ACT, "业务类型_撤销")
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_质押式回购首期.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
        }
        // 一级投标指令 check
        if (obj instanceof CfetsEtrBondFirstBidSaveRequest || StringUtil.equals(pageId, "CfetsEtrBondFirstBid002")) {
            return riskInputParamGenerateBusinessSerivce.generateEtrBondFirstBid(decisionAct, flowStatus, isCheck, businessJson);
        }
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
        if (Objects.isNull(param)){
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
    public List<InterfRiskRdsDto> buildRiskRdsParam(Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        if (this.isControl(dtl.getIndicatorControl())) {
            return null;
        }
        List<Map<String, Object>> list = null;
        if (data instanceof CfetsEtribBondInfo003Dto) {//银行间债券指令
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            CfetsEtribBondInfo003Dto dto = (CfetsEtribBondInfo003Dto) data;
            if (CollectionUtils.isNotEmpty(dto.getEtrInvestBondInfoDtos())) {
                list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getEtrInvestBondInfoDtos(), null);
            } else if (CollectionUtils.isNotEmpty(dto.getSelectedItem())) {
                list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getSelectedItem(), null);
            }
        }
        if (data instanceof CfetsTrdFirstBidFirmInfoDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
            CfetsTrdFirstBidFirmInfoDto dto = (CfetsTrdFirstBidFirmInfoDto) data;
            if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), dto.getBiddingMethod())) {
                //  自主投标
                riskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_一级投标确认_投标_自主投标.getValue());
            } else if (StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), dto.getBiddingMethod())) {
                //代投代缴
                riskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_一级投标确认_投标_代投代缴.getValue());
            } else if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), dto.getBiddingMethod())) {
                // 分销买入
                riskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入.getValue());
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getProductDtos(), null);
        }
        if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
            riskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_一级投标确认_申购_登记.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, ((CfetsTrdBondFirstSubInfoDto) data).getProductDtos(), null);
        }
        if (data instanceof CfetsPrimaryBidManageSaveRequest) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
            CfetsPrimaryBidManageSaveRequest dto = (CfetsPrimaryBidManageSaveRequest) data;
            if (StringUtil.equals(dto.getBidModle(),DictCodeListExt4.投标方式_自主投标.getValue())) {
                riskRdsDto.setEventStatus(DictCodeListExt4.共通业务类型_中标结果维护自主投标.getValue());
            }else{
                riskRdsDto.setEventStatus(DictCodeListExt4.共通业务类型_中标结果维护代投代缴.getValue());
            }
            dto.getAllocationDetails().forEach(item->{
                item.setBidId(dto.getBidId());
                item.setBidDate(dto.getPayDate());
            });
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getAllocationDetails(), null);
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
            if (CollectionUtils.isEmpty(entity.getDtoList())) {//处理撤销时
                for (BussTrdTransfenpeiDto item : entity.getBussTrdTransfenpeiDto()) {
                    TrdBondInvestTradeDto bondDto = new TrdBondInvestTradeDto();
                    BeanUtil.copyPropertiesIgnoreNull(bondDto, item);
                    bondDto.setTotleTradeId(item.getTradeId());
                    list1.add(bondDto);
                }
            }
            if (CollectionUtils.isNotEmpty(entity.getTradeDtoList())) {
                list1.addAll(entity.getTradeDtoList());
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, list1, null);
        }
        if (data instanceof CfetsTrdFirstBondInvestTradeDto) {//债券成交确认
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            CfetsTrdFirstBondInvestTradeDto entity = (CfetsTrdFirstBondInvestTradeDto) data;
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            List<TrdBondInvestTradeDto> list1 = new ArrayList<>();
            for (BussTrdTransfenpeiDto item : entity.getBussTrdTransfenpeiDto()) {
                TrdBondInvestTradeDto bondDto = new TrdBondInvestTradeDto();
                BeanUtil.copyPropertiesIgnoreNull(bondDto, item);
                bondDto.setTotleTradeId(item.getTradeId());
                bondDto.setTradeAmt(item.getSettleAmt());
                bondDto.setShareAmt(item.getShareAmt());
                bondDto.setFpriceAmt(item.getSettleAmt());
                bondDto.setPs(entity.getPs());
                list1.add(bondDto);
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, list1, null);
        }
        if(data instanceof TrdBondInvestTradeDto) {//银行间债券交易撤销
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            TrdBondInvestTradeDto entity = (TrdBondInvestTradeDto) data;
            if(CollectionUtils.isNotEmpty(entity.getTrdBondInvestTradeDtoList())) {
                list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, entity.getTrdBondInvestTradeDtoList(), null);
            }
        }
        if (data instanceof CfetsManualSettleInsertDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            CfetsManualSettleInsertDto dto = (CfetsManualSettleInsertDto) data;
            if(StringUtil.isNotEmpty(dto.getTrdBankManualSettleInfo())
                    && (StringUtil.equals(dto.getTrdBankManualSettleInfo().getBusiType(),头寸业务类型_债券一级投标.getValue())
                    || StringUtil.equals(dto.getTrdBankManualSettleInfo().getBusiType(),头寸业务类型_债券一级申购.getValue()))){
                riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            } else if (StringUtil.isNotEmpty(dto.getTrdBankManualSettleInfo())
                    && (StringUtil.equals(dto.getTrdBankManualSettleInfo().getBusiType(),头寸业务类型_质押式回购到期.getValue()))) {
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
            for(PtlAllocationDto ptlAllocationDto:dto.getPtlAllocatios()){
                Map<String, Object> stringObjectMap = BeanUtil.object2Map(ptlAllocationDto);
                stringObjectMap.put("tradeAmount",ptlAllocationDto.getShareAmt());
                stringObjectMap.put("entrustNum",ptlAllocationDto.getShareAmt());
                stringObjectMap.put("fpriceAmt",ptlAllocationDto.getShareAmt());
                stringObjectMap.put("portfolioCode",ptlAllocationDto.getPortfolioId());
                stringObjectMap.put("finprodId",dto.getFinprodId());
                stringObjectMap.put("finprodType2",DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                stringObjectMap.put("ps",dto.getPs());
                stringObjectMap.put("ccy","CNY");
                stringObjectMap.put("tradeMarket",交易场所_银行间.getValue());
                stringObjectMap.put("mdate",dto.getActSetteDate());
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
        if (data instanceof CfetsRepoDealRegisterQuoteDto){
            CfetsRepoDealRegisterQuoteDto businessDto = (CfetsRepoDealRegisterQuoteDto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getPtlAllocations(), null);
        }
        // 银行间质押式回购登记
        if (data instanceof CfetsRepoDealQuoteCommonDto){
            CfetsRepoDealQuoteCommonDto businessDto = (CfetsRepoDealQuoteCommonDto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getPtlAllocations(), null);
        }
        // 银行间质押式回购登记
        if (data instanceof CfetsRepoDealQuotedPrice001Dto){
            CfetsRepoDealQuotedPrice001Dto businessDto = (CfetsRepoDealQuotedPrice001Dto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getPtlAllocationList(), null);
        }
        // 银行间质押式回购  成交确认 || 手工交收
        if (data instanceof CfetsTrdRepoTrade003Dto){
            CfetsTrdRepoTrade003Dto businessDto = (CfetsTrdRepoTrade003Dto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getDtoList(), null);
        }
        // 交易撤销
        if (data instanceof CfetsTrdQueryTradeInfoListResponse){
            CfetsTrdQueryTradeInfoListResponse tradeInfo = (CfetsTrdQueryTradeInfoListResponse) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, tradeInfo.getSelectedItem(), null);
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
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, e, e);
                result.add(inputDto);
            });

        }
        if (data instanceof CfetsTrdBondInvestTradeDto) {
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
            this.generateInputDto(inputDto, data, null);
            result.add(inputDto);
        }
        if (data instanceof CfetsTrdFirstBondInvestTradeDto) {
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
            this.generateInputDto(inputDto, data, null);
            result.add(inputDto);
        }
        // 一级投标指令
        if (data instanceof CfetsEtrBondFirstBidSaveRequest) {
            List<CfetsBiddingProductDto> list = ((CfetsEtrBondFirstBidSaveRequest) data).getBiddingProductList();
            //这里处理授信
            list.forEach(e -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, data, e);
                result.add(inputDto);
            });

        }
        if (data instanceof CfetsTrdFirstBidFirmInfoDto) {
            List<CfetsTrdFirstBidFirmProductDto> list = ((CfetsTrdFirstBidFirmInfoDto) data).getProductDtos();
            //这里处理授信
            list.forEach(e -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, data, e);
                result.add(inputDto);
            });

        }
        if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            List<CfetsTrdBondFirstSubProductDto> list = ((CfetsTrdBondFirstSubInfoDto) data).getProductDtos();
            //这里处理授信
            list.forEach(e -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, data, e);
                result.add(inputDto);
            });

        }
        if (data instanceof CfetsPrimaryBidManageSaveRequest) {
            List<CfetsPrimaryBidManageSaveDetailDto> list = ((CfetsPrimaryBidManageSaveRequest) data).getAllocationDetails();
            //这里处理授信
            list.forEach(e -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, data, e);
                result.add(inputDto);
            });

        }
        if (data instanceof CfetsManualSettleInsertDto) {
            CfetsManualSettleInsertDto dto = (CfetsManualSettleInsertDto) data;
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
            this.generateInputDto(inputDto, dto.getTrdBankManualSettleInfo(), dto);
            result.add(inputDto);
        }
        if (data instanceof CfetsManualSettleInsertDto) {

        }
        // 银行间回购指令
        if (data instanceof CfetsEtribRepoInfo003Dto) {
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            // 组合分配信息
            List<CfetsEtribRepoInfo003Dto> list = businessDto.getEtrInvestRepoInfoDtos();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, businessDto, item);
                result.add(inputDto);
            });
        }
        if (data instanceof CfetsRepoDealRegisterQuoteDto){
            // 银行间回购登记
            CfetsRepoDealRegisterQuoteDto businessDto = (CfetsRepoDealRegisterQuoteDto) data;
            // 组合分配信息
            List<RepoPtlAllocationDto> list = businessDto.getPtlAllocations();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, businessDto,item);
                result.add(inputDto);
            });
        }
        if (data instanceof CfetsRepoDealQuoteCommonDto){
            // 银行间回购登记
            CfetsRepoDealQuoteCommonDto businessDto = (CfetsRepoDealQuoteCommonDto) data;
            // 组合分配信息
            List<RepoPtlAllocationDto> list = businessDto.getPtlAllocations();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, businessDto,item);
                result.add(inputDto);
            });
        }
        if (data instanceof CfetsRepoDealQuotedPrice001Dto){
            // 银行间回购登记
            CfetsRepoDealQuotedPrice001Dto businessDto = (CfetsRepoDealQuotedPrice001Dto) data;
            // 组合分配信息
            List<com.joyintech.tams.invest.common.dto.common.PtlAllocationDto> list
                    = businessDto.getPtlAllocationList();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, businessDto,item);
                result.add(inputDto);
            });
        }
        if (data instanceof CfetsTrdRepoTrade003Dto){
            // 银行间回购登记
            CfetsTrdRepoTrade003Dto businessDto = (CfetsTrdRepoTrade003Dto) data;
            // 组合分配信息
            List<CfetsTrdRepoTrade003Dto> list = businessDto.getDtoList();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, businessDto, item);
                result.add(inputDto);
            });
        }
        if (data instanceof TrdRepoTrade002Dto){
            // 银行间回购登记
            TrdRepoTrade002Dto businessDto = (TrdRepoTrade002Dto) data;
            // 组合分配信息
            List<PtlTrade> list = businessDto.getPtlTrade();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, businessDto, item);
                result.add(inputDto);
            });
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
            Map<String, Object> map = BeanUtil.object2Map(data);
            listDto.setFinprodId((String) map.get("finprodId"));
            listDto.setCounterId((String) map.get("counterId"));
        } else if (data instanceof CfetsEtrBondFirstBidSaveRequest) {
            Map<String, Object> map = BeanUtil.object2Map(data);
            listDto.setFinprodId((String) map.get("finprodId"));
            listDto.setCounterId((String) map.get("counterId"));
        } else if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            CfetsTrdBondFirstSubInfoDto dto = (CfetsTrdBondFirstSubInfoDto) data;
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(dto.getProductDtos().get(0).getEntrustId());
            listDto.setFinprodId(dto.getFinprodId());
            listDto.setCounterId(etrEntrust.getCounterId());
        } else if (data instanceof CfetsPrimaryBidManageSaveRequest) {
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
        } else if (data instanceof CfetsRepoDealRegisterQuoteDto){
            // 银行间回购指令
            CfetsRepoDealRegisterQuoteDto businessDto = (CfetsRepoDealRegisterQuoteDto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
        }else if (data instanceof CfetsRepoDealQuoteCommonDto){
            // 银行间回购指令
            CfetsRepoDealQuoteCommonDto businessDto = (CfetsRepoDealQuoteCommonDto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
        }else if (data instanceof CfetsRepoDealQuotedPrice001Dto){
            // 银行间回购指令
            CfetsRepoDealQuotedPrice001Dto businessDto = (CfetsRepoDealQuotedPrice001Dto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
        } else if (data instanceof CfetsTrdRepoTrade003Dto){
            // 银行间质押式回购 - 成交确认  ||  手工确认
            CfetsTrdRepoTrade003Dto businessDto = (CfetsTrdRepoTrade003Dto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
        } else if (data instanceof CfetsTrdQueryTradeInfoListResponse){
            // 交易撤销
            CfetsTrdQueryTradeInfoListResponse tradeInfo = (CfetsTrdQueryTradeInfoListResponse) data;
            listDto.setCounterId(tradeInfo.getSelectedItem().get(0).getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(tradeInfo.getSelectedItem().get(0).getPs());
        } else if (data instanceof CfetsManualSettleInsertDto){
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
            CfetsTrdFirstBidFirmInfoDto infoDto = (CfetsTrdFirstBidFirmInfoDto) data;
            Map<String, Object> map = BeanUtil.object2Map(data);
            dto.setCounterId(infoDto.getCounterId());
            dto.setPrinAmt(infoDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            dto.setTradeMarket(infoDto.getTradeMarket());
            dto.setPs("T01");
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(infoDto.getFinprodId());
            key.setBranch(Long.valueOf(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            dto.setAssetSecType(finProductType.getType9());
            dto.setFinprodId(infoDto.getFinprodId());
        } else if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            CfetsTrdBondFirstSubInfoDto infoDto = (CfetsTrdBondFirstSubInfoDto) data;
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(infoDto.getProductDtos().get(0).getEntrustId());
            Map<String, Object> map = BeanUtil.object2Map(data);
            dto.setCounterId(etrEntrust.getCounterId());
            dto.setPrinAmt(etrEntrust.getEntrustAmt());
            dto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            dto.setTradeMarket(etrEntrust.getTradeMarket());
            dto.setPs(DictCodeList.交易方向_买入.getValue());
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(infoDto.getFinprodId());
            key.setBranch(Long.valueOf(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            dto.setAssetSecType(finProductType.getType9());
            dto.setFinprodId(infoDto.getFinprodId());
        } else if (data instanceof CfetsPrimaryBidManageSaveRequest) {
            CfetsPrimaryBidManageSaveRequest infoDto = (CfetsPrimaryBidManageSaveRequest) data;
            Map<String, Object> map = BeanUtil.object2Map(data);
            dto.setCounterId(infoDto.getCounterId());
            dto.setPrinAmt(infoDto.getTotalBiddingNum());
            dto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            dto.setTradeMarket(infoDto.getTradeMarket());
            dto.setPs(DictCodeList.交易方向_买入.getValue());
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(infoDto.getFinprodId());
            key.setBranch(Long.valueOf(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            dto.setAssetSecType(finProductType.getType9());
            dto.setFinprodId(infoDto.getFinprodId());
        } else if (data instanceof CfetsEtribRepoInfo003Dto) {
            // 银行间回购指令
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getEntrustAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        }  else if (data instanceof CfetsRepoDealRegisterQuoteDto){
            // 银行间回购交易
            CfetsRepoDealRegisterQuoteDto businessDto = (CfetsRepoDealRegisterQuoteDto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsRepoDealQuoteCommonDto){
            // 银行间回购交易
            CfetsRepoDealQuoteCommonDto businessDto = (CfetsRepoDealQuoteCommonDto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsRepoDealQuotedPrice001Dto){
            // 银行间回购交易
            CfetsRepoDealQuotedPrice001Dto businessDto = (CfetsRepoDealQuotedPrice001Dto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getDealAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsTrdRepoTrade003Dto) {
            // 银行间交易 - 成交确认
            CfetsTrdRepoTrade003Dto businessDto = (CfetsTrdRepoTrade003Dto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof TrdRepoTrade002Dto) {
            // 交易撤销
            TrdRepoTrade002Dto businessDto = (TrdRepoTrade002Dto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
        } else if (data instanceof CfetsManualSettleInsertDto) { //成交确认
            CfetsManualSettleInsertDto businessDto = (CfetsManualSettleInsertDto)data;
            TrdProductDeal trdProductDeal = trdProductDealMapper.selectByPrimaryKey(businessDto.getTradeId());
            dto.setCounterId(trdProductDeal.getCounterId());
            dto.setPrinAmt(businessDto.getTrdBankManualSettleInfo().getSettleAmt());
            if (StringUtil.equals(businessDto.getTrdBankManualSettleInfo().getBusiType(),头寸业务类型_债券一级投标.getValue())
             || StringUtil.equals(businessDto.getTrdBankManualSettleInfo().getBusiType(),头寸业务类型_债券一级申购.getValue())) {
                dto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
            } else if (StringUtil.equals(businessDto.getTrdBankManualSettleInfo().getBusiType(),头寸业务类型_质押式回购到期.getValue())) {
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
        if (obj instanceof CfetsTrdBondInvestTradeDto) {//债券成交确认
            CfetsTrdBondInvestTradeDto inputDto = (CfetsTrdBondInvestTradeDto) obj;
            creditLinesInputDto = new CreditLinesInputDto();
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
        if (obj instanceof CfetsTrdFirstBondInvestTradeDto) {//债券成交确认
            CfetsTrdFirstBondInvestTradeDto inputDto = (CfetsTrdFirstBondInvestTradeDto) obj;
            creditLinesInputDto = new CreditLinesInputDto();
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
        if (obj instanceof CfetsEtrBondFirstBidSaveRequest) {//一级投标指令
            CfetsEtrBondFirstBidSaveRequest inputDto = (CfetsEtrBondFirstBidSaveRequest) obj;
            CfetsBiddingProductDto detailDto = (CfetsBiddingProductDto) detailObj;
            creditLinesInputDto.setTradeId(inputDto.getEntrustId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            creditLinesInputDto.setTradeDate(inputDto.getVdate());
            creditLinesInputDto.setPs(inputDto.getPs());
            creditLinesInputDto.setCounterId(inputDto.getCounterId());
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
        if (obj instanceof CfetsTrdFirstBidFirmInfoDto) {//一级投标确认
            CfetsTrdFirstBidFirmInfoDto inputDto = (CfetsTrdFirstBidFirmInfoDto) obj;
            CfetsTrdFirstBidFirmEntrustDto detailDto = (CfetsTrdFirstBidFirmEntrustDto) detailObj;
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(detailDto.getEntrustId());
            creditLinesInputDto = new CreditLinesInputDto();
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            creditLinesInputDto.setTradeId(detailDto.getEntrustId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
            creditLinesInputDto.setTradeDate(etrEntrust.getVdate());
            creditLinesInputDto.setPs(etrEntrust.getPs());
            creditLinesInputDto.setCounterId(etrEntrust.getCounterId());
            creditLinesInputDto.setTradeAmt(etrEntrust.getEntrustAmt());
            if (etrEntrust.getEntrustNum() != null) {
                creditLinesInputDto.setShareAmt(etrEntrust.getEntrustAmt().divide(new BigDecimal(100), RoundingMode.HALF_UP));
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
        if (obj instanceof CfetsTrdBondFirstSubInfoDto) {//一级投标申购
            CfetsTrdBondFirstSubInfoDto inputDto = (CfetsTrdBondFirstSubInfoDto) obj;
            CfetsTrdBondFirstSubEntrustDto detailDto = (CfetsTrdBondFirstSubEntrustDto) detailObj;
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(detailDto.getEntrustId());
            creditLinesInputDto = new CreditLinesInputDto();
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            creditLinesInputDto.setTradeId(detailDto.getEntrustId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
            creditLinesInputDto.setTradeDate(etrEntrust.getVdate());
            creditLinesInputDto.setPs(etrEntrust.getPs());
            creditLinesInputDto.setCounterId(etrEntrust.getCounterId());
            creditLinesInputDto.setTradeAmt(etrEntrust.getEntrustAmt());
            if (etrEntrust.getEntrustNum() != null) {
                creditLinesInputDto.setShareAmt(etrEntrust.getEntrustAmt().divide(new BigDecimal(100), RoundingMode.HALF_UP));
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
        if (obj instanceof CfetsPrimaryBidManageSaveRequest) {//中标结果维护
            CfetsPrimaryBidManageSaveRequest inputDto = (CfetsPrimaryBidManageSaveRequest) obj;
            CfetsPrimaryBidManageSaveDetailDto detailDto = (CfetsPrimaryBidManageSaveDetailDto) detailObj;
            creditLinesInputDto = new CreditLinesInputDto();
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            creditLinesInputDto.setEntrustId(detailDto.getEntrustId());
            creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
            creditLinesInputDto.setTradeDate(DateUtil.platDateFromCache());
            creditLinesInputDto.setCounterId(inputDto.getCounterId());
            creditLinesInputDto.setTradeId(inputDto.getOuTraderId());
            creditLinesInputDto.setTradeAmt(detailDto.getTotalBidPrinamt());
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
            if (StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue(), inputDto.getPs())) {
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                // 指令编号+组合代码
                creditLinesInputDto.setTradeId(inputDto.getEntrustId() + "-" + item.getPortfolioId());
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
        }
        if (obj instanceof CfetsRepoDealRegisterQuoteDto){
            CfetsRepoDealRegisterQuoteDto inputDto = (CfetsRepoDealRegisterQuoteDto) obj;
            RepoPtlAllocationDto item = (RepoPtlAllocationDto) detailObj;
            if (StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue(),inputDto.getPs())) {
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                // 指令编号+组合代码
                creditLinesInputDto.setTradeId(inputDto.getTotleTradeId() + "-" + item.getPortfolioId());
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
        }
        if (obj instanceof CfetsRepoDealQuoteCommonDto){
            CfetsRepoDealQuoteCommonDto inputDto = (CfetsRepoDealQuoteCommonDto) obj;
            RepoPtlAllocationDto item = (RepoPtlAllocationDto) detailObj;
            if (StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue(),inputDto.getPs())) {
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                // 指令编号+组合代码
                creditLinesInputDto.setTradeId(inputDto.getTotleTradeId() + "-" + item.getPortfolioId());
                creditLinesInputDto.setTradeDate(inputDto.getTradeDate());
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
                creditLinesInputDto.setPsPayment(DictCodeList.回购类型_质押式回购.getValue());
                creditLinesInputDto.setSaleCode(inputDto.getPs());
                creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                creditLinesInputDto.setCounterId(inputDto.getCounterId());
                creditLinesInputDto.setTradeAmt(item.getTradeAmt());
            }
        }
        if (obj instanceof CfetsRepoDealQuotedPrice001Dto){
            CfetsRepoDealQuotedPrice001Dto inputDto = (CfetsRepoDealQuotedPrice001Dto) obj;
            PtlAllocationDto item = (PtlAllocationDto) detailObj;
            if (StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue(),inputDto.getPs())) {
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                // 指令编号+组合代码
                creditLinesInputDto.setTradeId(inputDto.getQuoteId() + "-" + item.getPortfolioId());
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
        }
        // 成交确认 || 手工匹配
        if (obj instanceof CfetsTrdRepoTrade003Dto || obj instanceof CfetsTrdConfirmManualMatchInfoRequest) {
            CfetsTrdRepoTrade003Dto detailDto = (CfetsTrdRepoTrade003Dto) detailObj;
            if (!StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue(),detailDto.getPs())) {
                return;
            }
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            // 指令编号+组合代码
            creditLinesInputDto.setTradeId(detailDto.getTotleTradeId() + "-" + detailDto.getPortfolioId());
            creditLinesInputDto.setFinprodId(detailDto.getFinprodId());
            creditLinesInputDto.setTradeDate(detailDto.getTradeDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
            creditLinesInputDto.setPsPayment(detailDto.getRepoType());
            creditLinesInputDto.setSaleCode(detailDto.getPs());
            creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
            creditLinesInputDto.setCounterId(detailDto.getCounterId());
            creditLinesInputDto.setTradeAmt(detailDto.getTradeAmt());
        }
        // 交易撤销
        if (obj instanceof TrdRepoTrade002Dto) {
            TrdRepoTrade002Dto inputDto = (TrdRepoTrade002Dto) obj;
            PtlTrade item = (PtlTrade) detailObj;
            if (!StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue(),inputDto.getPs())) {
                return ;
            }
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.风险占用类型_实占释放.getValue());
            // 指令编号+组合代码
            creditLinesInputDto.setTradeId(inputDto.getTotleTradeId() + "-" + item.getPortfolioId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setTradeDate(inputDto.getTradeDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
//            creditLinesInputDto.setPsPayment(tradeInfo.getRepoType());
            creditLinesInputDto.setSaleCode(inputDto.getPs());
            creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
            creditLinesInputDto.setCounterId(inputDto.getCounterId());
            creditLinesInputDto.setTradeAmt(inputDto.getTradeAmt());
        }
        if (obj instanceof CfetsManualSettleInsertDto) {
            CfetsManualSettleInsertDto inputDto = (CfetsManualSettleInsertDto) obj;
            creditLinesInputDto = new CreditLinesInputDto();
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
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

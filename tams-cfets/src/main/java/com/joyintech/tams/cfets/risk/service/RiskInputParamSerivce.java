package com.joyintech.tams.cfets.risk.service;

import com.google.common.collect.Lists;
import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.service.bankuplink.common.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribSaveRequest;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfoService;
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
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdBondInvestTradeDto;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
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
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesDealsMapper;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.workflow.engine.WorkflowContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    /**
     * 构造入参
     *
     * @param obj
     * @param decisionAct
     * @param flowStatus
     * @param isCheck
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
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
        }
        if (obj instanceof CfetsBondDealRegisterQuoteDto || StringUtil.equals(pageId, "IbuQuoteBond004")) {
            CfetsBondDealRegisterQuoteDto dto = JsonUtils.json2Object(businessJson, CfetsBondDealRegisterQuoteDto.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
        }
        if (obj instanceof CfetsManualSettleDto || StringUtil.equals(pageId, "CfetsManualSettle")) {
            CfetsManualSettleDto allDto = JsonUtils.json2Object(businessJson, CfetsManualSettleDto.class);
            List<CfetsManualSettleInsertDto> list = cfetsManualSettleService.handleSettleData(allDto);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_交收确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", allDto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", list.get(0));
            if (StringUtil.equals(allDto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易);
            }
        }
        if (obj instanceof CfetsTrdFirstBidFirmInfoDto || StringUtil.equals(pageId, "CfetsTrdFirstBidFirm002")) {
            CfetsTrdFirstBidFirmInfoDto dto = JsonUtils.json2Object(businessJson, CfetsTrdFirstBidFirmInfoDto.class);
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(dto.getProductDtos().get(0).getEntrustId());
            String ps = etrEntrust.getPs();
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级投标.getValue())
                    .put("tradePlace", dto.getTradeMarket())
                    .put("ps", ps)
                    .put("isCheck", isCheck)
                    .put("data", dto);
        }
        if (obj instanceof CfetsTrdBondFirstSubInfoDto || StringUtil.equals(pageId, "TenderBondSub002")) {
            CfetsTrdBondFirstSubInfoDto dto = JsonUtils.json2Object(businessJson, CfetsTrdBondFirstSubInfoDto.class);
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(dto.getProductDtos().get(0).getEntrustId());
            String ps = etrEntrust.getPs();
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券一级申购.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", ps)
                    .put("isCheck", isCheck)
                    .put("data", dto);
        }
        if (obj instanceof CfetsTrdBondInvestTradeDto) {
            CfetsTrdBondInvestTradeDto dto = JsonUtils.json2Object(businessJson, CfetsTrdBondInvestTradeDto.class);
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_交收确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", isCheck)
                    .put("data", dto);
            if (StringUtil.equals(dto.getPs(), 交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易);
            }
            return map;
        }
        // 银行间回购指令
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
        return null;
    }

    /**
     * 指标、授信、名单、授权check
     **/
    public RiskCommonOutputDto buildParamThenRisk(Object obj, String decisionAct, String flowStatus, String pageId) {
        BaseMap map = generateBusinessDto(obj, decisionAct, flowStatus, true, pageId);
        RiskCommonInputDto inputDto = getRiskInputParam(map);
        if (inputDto == null) return null;
        return doRisk(inputDto);
    }

    /**
     * 指标和授信占用
     **/
    public void buildParamThenOccupy(Object obj, String decisionAct, String flowStatus, String pageId) {
        BaseMap map = generateBusinessDto(obj, decisionAct, flowStatus, false, pageId);
        RiskCommonInputDto inputDto = getRiskInputParam(map);
        if (inputDto == null) return;
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
        RiskNodeConfigDtl dtl = riskNodeConfigService.getNodeConfig(param);
        if (dtl == null) return null;
        Boolean isCheck = param.get("isCheck") == null ? false : (Boolean) param.get("isCheck");
        Object data = param.get(NodeConfigConstant.DATA);
        RiskCommonInputDto inputDto = new RiskCommonInputDto();
        BeanUtil.copyPropertiesIgnoreNull(inputDto, dtl);
        this.buildAuthorizeParam(inputDto, data, dtl);
        inputDto.setCreditInputList(this.buildCreditParam(data, dtl));
        inputDto.setRiskRdsDtoList(this.buildRiskRdsParam(data, dtl, isCheck));
        inputDto.setRiskNameList(this.buildRiskNameParam(data, dtl));
        return inputDto;
    }


    /**
     * 构建指标计算参数
     *
     * @param data
     * @return
     */
    public List<InterfRiskRdsDto> buildRiskRdsParam(Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        if (this.isControl(dtl.getIndicatorControl())) return null;
        List<Map<String, Object>> list = null;
        if (data instanceof CfetsEtribBondInfo003Dto) {//银行间债券指令
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            CfetsEtribBondInfo003Dto dto = (CfetsEtribBondInfo003Dto) data;
            if (CollectionUtils.isNotEmpty(dto.getEtrInvestBondInfoDtos())) {
                list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, ((CfetsEtribBondInfo003Dto) data).getEtrInvestBondInfoDtos(), null);
            } else if (CollectionUtils.isNotEmpty(dto.getSelectedItem())) {
                list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, ((CfetsEtribBondInfo003Dto) data).getSelectedItem(), null);
            }
        }
        if (data instanceof CfetsTrdFirstBidFirmInfoDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, ((CfetsTrdFirstBidFirmInfoDto) data).getProductDtos(), null);
        }
        if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, ((CfetsTrdBondFirstSubInfoDto) data).getProductDtos(), null);
        }
        if (data instanceof CfetsTrdBondInvestTradeDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            CfetsTrdBondInvestTradeDto entity = (CfetsTrdBondInvestTradeDto) data;
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
            if (CollectionUtils.isEmpty(entity.getTradeDtoList())) {
                list1.addAll(entity.getTradeDtoList());
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, list1, null);
        }
        if (data instanceof CfetsManualSettleInsertDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            CfetsManualSettleInsertDto dto = (CfetsManualSettleInsertDto) data;
            List<TrdBondInvestTradeDto> list1 = new ArrayList<>();
            for (TrdBankManualSetDetail item : dto.getListTrdBnakSetDetails()) {
                TrdBondInvestTradeDto bondDto = new TrdBondInvestTradeDto();
                BeanUtil.copyPropertiesIgnoreNull(bondDto, item);
                bondDto.setTotleTradeId(item.getTradeId());
                list1.add(bondDto);
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, list1, null);
        }
        if(data instanceof CfetsBondDealRegisterQuoteDto) {
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            CfetsBondDealRegisterQuoteDto dto = (CfetsBondDealRegisterQuoteDto) data;
            List<TrdBondInvestTradeDto> list1 = new ArrayList<>();
            for (PtlAllocationDto item : dto.getPtlAllocatios()) {
                TrdBondInvestTradeDto bondDto = new TrdBondInvestTradeDto();
                BeanUtil.copyPropertiesIgnoreNull(bondDto, item);
                bondDto.setTotleTradeId(item.getEntrustId());
                list1.add(bondDto);
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, list1, null);
        }
        // 银行间回购指令
        if (data instanceof CfetsEtribRepoInfo003Dto){
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, businessDto.getEtrInvestRepoInfoDtos(), null);
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
        if (this.isControl(dtl.getCreditControl())) return null;
        List<CreditLinesInputDto> result = new ArrayList<>();
        if (data instanceof CfetsEtribBondInfo003Dto) {//银行间债券指令
            List<CfetsEtribBondInfo003Dto> list = ((CfetsEtribBondInfo003Dto) data).getEtrInvestBondInfoDtos();
            //这里处理授信
            list.forEach(e -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, data, e);
                result.add(inputDto);
            });

        }
        if (data instanceof CfetsTrdBondInvestTradeDto) {
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
            this.generateInputDto(inputDto, data, null);
            result.add(inputDto);
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
        if (data instanceof CfetsManualSettleInsertDto) {
            CfetsManualSettleInsertDto dto = (CfetsManualSettleInsertDto) data;
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
            this.generateInputDto(inputDto, dto.getTrdBankManualSettleInfo(), dto.getTrdBankManualSettleInfo());
            result.add(inputDto);
        }
        if (data instanceof CfetsManualSettleInsertDto) {

        }
        // 银行间回购指令
        if (data instanceof CfetsEtribRepoInfo003Dto){
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            // 组合分配信息
            List<CfetsEtribRepoInfo003Dto> list = businessDto.getEtrInvestRepoInfoDtos();
            //这里处理授信
            list.forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
                this.generateInputDto(inputDto, businessDto,item);
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
        if (this.isControl(dtl.getNameListControl())) return null;
        RiskNameListDto listDto = new RiskNameListDto();
        if (data instanceof CfetsTrdFirstBidFirmInfoDto) {
            Map<String, Object> map = BeanUtil.object2Map(data);
            listDto.setFinprodId((String) map.get("finprodId"));
            listDto.setCounterId((String) map.get("counterId"));
        } else if (data instanceof CfetsTrdBondFirstSubInfoDto) {
            CfetsTrdBondFirstSubInfoDto dto = (CfetsTrdBondFirstSubInfoDto) data;
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(dto.getProductDtos().get(0).getEntrustId());
            listDto.setFinprodId(dto.getFinprodId());
            listDto.setCounterId(etrEntrust.getCounterId());
        } else if (data instanceof CfetsEtribRepoInfo003Dto){
            // 银行间回购指令
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            listDto.setCounterId(businessDto.getCounterId());
            listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
            listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
            listDto.setInputValue3(businessDto.getPs());
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
        if (this.isControl(dtl.getNameListControl())) return;
        if (data instanceof CfetsTrdFirstBidFirmInfoDto) {
            CfetsTrdFirstBidFirmInfoDto infoDto = (CfetsTrdFirstBidFirmInfoDto) data;
            Map<String, Object> map = BeanUtil.object2Map(data);
            dto.setCounterId(infoDto.getCounterId());
            dto.setPrinAmt(infoDto.getTradeAmt());
            dto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
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
            dto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            dto.setTradeMarket(etrEntrust.getTradeMarket());
            dto.setPs("T01");
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(infoDto.getFinprodId());
            key.setBranch(Long.valueOf(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            dto.setAssetSecType(finProductType.getType9());
            dto.setFinprodId(infoDto.getFinprodId());
        } else if (data instanceof CfetsEtribRepoInfo003Dto){
            // 银行间回购指令
            CfetsEtribRepoInfo003Dto businessDto = (CfetsEtribRepoInfo003Dto) data;
            dto.setCounterId(businessDto.getCounterId());
            dto.setPrinAmt(businessDto.getEntrustAmt());
            dto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            dto.setTradeMarket(交易场所_银行间.getValue());
            dto.setPs(businessDto.getPs());
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
        if (obj instanceof CfetsTrdFirstBidFirmInfoDto) {
            CfetsTrdFirstBidFirmInfoDto inputDto = (CfetsTrdFirstBidFirmInfoDto) obj;
            CfetsTrdFirstBidFirmEntrustDto detailDto = (CfetsTrdFirstBidFirmEntrustDto) detailObj;
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(detailDto.getEntrustId());
            creditLinesInputDto.setTradeId(detailDto.getEntrustId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            creditLinesInputDto.setTradeDate(etrEntrust.getVdate());
            creditLinesInputDto.setPs(etrEntrust.getPs());
            creditLinesInputDto.setCounterId(etrEntrust.getCounterId());
            creditLinesInputDto.setTradeAmt(etrEntrust.getEntrustNum());
            if (etrEntrust.getEntrustNum() != null) {
                creditLinesInputDto.setShareAmt(etrEntrust.getEntrustNum().divide(new BigDecimal(100), RoundingMode.HALF_UP));
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
        if (obj instanceof CfetsTrdBondFirstSubInfoDto) {//银行间债券指令
            CfetsTrdBondFirstSubInfoDto inputDto = (CfetsTrdBondFirstSubInfoDto) obj;
            CfetsTrdBondFirstSubEntrustDto detailDto = (CfetsTrdBondFirstSubEntrustDto) detailObj;
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(detailDto.getEntrustId());
            creditLinesInputDto.setTradeId(detailDto.getEntrustId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            creditLinesInputDto.setTradeDate(etrEntrust.getVdate());
            creditLinesInputDto.setPs(etrEntrust.getPs());
            creditLinesInputDto.setCounterId(etrEntrust.getCounterId());
            creditLinesInputDto.setTradeAmt(etrEntrust.getEntrustNum());
            if (etrEntrust.getEntrustNum() != null) {
                creditLinesInputDto.setShareAmt(etrEntrust.getEntrustNum().divide(new BigDecimal(100), RoundingMode.HALF_UP));
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
        if (obj instanceof TrdBankManualSettleInfo) {
            TrdBankManualSettleInfo inputDto = (TrdBankManualSettleInfo) obj;
            BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
            creditLinesInputDto.setTradeId(inputDto.getTradeId());
            creditLinesInputDto.setTradeAmt(inputDto.getSettleAmt());
            creditLinesInputDto.setTradeDate(inputDto.getSettleDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        }
        // 银行间回购指令
        if (obj instanceof CfetsEtribRepoInfo003Dto){
            CfetsEtribRepoInfo003Dto inputDto = (CfetsEtribRepoInfo003Dto) obj;
            CfetsEtribRepoInfo003Dto item = (CfetsEtribRepoInfo003Dto) detailObj;
            if (StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue(),inputDto.getPs())){
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                // 指令编号+组合代码
                creditLinesInputDto.setTradeId(inputDto.getEntrustId()+"-"+item.getPortfolioId());
                creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                creditLinesInputDto.setTradeDate(inputDto.getVdate());
                creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
                creditLinesInputDto.setPsPayment(inputDto.getAssetType1());
                creditLinesInputDto.setSaleCode(inputDto.getPs());
                creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                creditLinesInputDto.setCounterId(inputDto.getCounterId());
                creditLinesInputDto.setTradeAmt(inputDto.getEntrustAmt());
            }
        }
    }
}

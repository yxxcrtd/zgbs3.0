package com.joyintech.tams.exchange.entrust.risk.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.common.base.entity.EtrEntrustDetail;
import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonInputDto;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonOutputDto;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import com.joyintech.tams.component.business.riskall.service.RiskNodeConfigService;
import com.joyintech.tams.component.business.riskall.utils.RiskUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto.EtrCreditProtectionCertificateApplyRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeOperateSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePortfolioSecRepoRequest;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.workflow.engine.WorkflowContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt3.业务动作_投资指令;
import static com.joyintech.tams.constant.DictCodeListExt3.头寸业务类型_信用保护凭证交易;

@Service
public class ExchangeRiskInputParamService extends RiskCommonService {

    private static Logger logger = LoggerFactory.getLogger(ExchangeRiskInputParamService.class);

    @Autowired
    private RiskNodeConfigService riskNodeConfigService;
    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

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
     * 指标、授信、名单、授权参数组装
     *
     * @param obj
     * @param decisionAct
     * @param flowStatus
     * @param pageId
     * @return
     */
    public RiskCommonOutputDto buildParamThenRisk(Object obj, String decisionAct, String flowStatus, String pageId, String eventType) {
        BaseMap map = generateBusinessDto(obj, decisionAct, flowStatus, true, pageId);
        RiskCommonInputDto inputDto = getRiskInputParam(map);
        if (inputDto == null) {
            return new RiskCommonOutputDto();
        }
        inputDto.setEventType(eventType);
        return doRisk(inputDto);
    }

    /**
     * 根据决策配置构造风险报文参数
     * @param param
     * @return
     */
    private RiskCommonInputDto getRiskInputParam(BaseMap param) {
        if (Objects.isNull(param)) {
            return null;
        }
        RiskNodeConfigDtl dtl = riskNodeConfigService.getNodeConfig(param);
        if (dtl == null) {
            return null;
        }
        Boolean isCheck;
        isCheck = param.get("isCheck") != null && (Boolean) param.get("isCheck");
        Object data = param.get(NodeConfigConstant.DATA);
        RiskCommonInputDto inputDto = new RiskCommonInputDto();
        BeanUtil.copyPropertiesIgnoreNull(inputDto, dtl);
        this.buildAuthorizeParam(inputDto, data, dtl, isCheck);
        inputDto.setCreditInputList(this.buildCreditParam(data, dtl));
        inputDto.setRiskRdsDtoList(this.buildRiskRdsParam(data, dtl, isCheck));
        inputDto.setRiskNameList(this.buildRiskNameParam(data, dtl,isCheck));
        logger.info("组装后保报文数据：{}", JsonUtils.object2Json(inputDto));
        return inputDto;
    }


    /**
     * 名单参数构造
     * @param data
     * @param dtl
     * @param isCheck
     * @return
     */
    private List<RiskNameListDto> buildRiskNameParam(Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        if (!isCheck) {
            return null;
        }
        if(this.isControl(dtl.getNameListControl())) {
            return null;
        }
        RiskNameListDto listDto = new RiskNameListDto();
        // CDS(信用保护凭证),新建、check、占用、退回、作废
        if (data instanceof EtrCreditProtectionCertificateApplyRequest){
            EtrCreditProtectionCertificateApplyRequest dto = (EtrCreditProtectionCertificateApplyRequest)data;
            listDto.setFinprodId(dto.getFinprodId());
            listDto.setCounterId(dto.getCounterId());
        }
        return Lists.newArrayList(listDto);
    }


    /**
     * 指标参数构造
     * @param data
     * @param dtl
     * @param isCheck
     * @return
     */
    private List<InterfRiskRdsDto> buildRiskRdsParam(Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        if (this.isControl(dtl.getIndicatorControl())) {
            return null;
        }
        List<Map<String, Object>> list = null;
        // CDS(信用保护凭证),新建、check、占用、退回、作废
        if (data instanceof EtrCreditProtectionCertificateApplyRequest){
            EtrCreditProtectionCertificateApplyRequest dto = (EtrCreditProtectionCertificateApplyRequest)data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());
            riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
            if(StringUtil.isEmptyList(dto.getPortfolioList())){
                return null;
            }
            int size = dto.getPortfolioList().size();
            dto.getPortfolioList().forEach(item -> {
                item.setPs(dto.getPs());
                item.setFinprodId(dto.getFinprodId());
                if (size > 1){
                    item.setEntrustId(dto.getEntrustId()+"-"+item.getPortfolioId());
                }else {
                    item.setEntrustId(dto.getEntrustId());
                }
                item.setEntrustNum(ArithmeticUtil.multiply(item.getEntrustNum(),new BigDecimal("10000")));
            });
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getPortfolioList(), map -> map.put("fromEtrExchange",DictCodeList.是否_是.getValue()));
        }

        if (CollectionUtils.isNotEmpty(list)) {
            List<InterfRiskRdsDto> result = new ArrayList<>();
            list.forEach(e -> result.add(BeanUtil.mapToObject(e, InterfRiskRdsDto.class)));
            return result;
        }
        return null;
    }

    /**
     * 授信参数构造
     * @param data
     * @param dtl
     * @return
     */
    private List<CreditLinesInputDto> buildCreditParam(Object data, RiskNodeConfigDtl dtl) {
        if (this.isControl(dtl.getCreditControl())) {
            return null;
        }
        List<CreditLinesInputDto> result = new ArrayList<>();
        // CDS(信用保护凭证),新建、check、占用、退回、作废
        if (data instanceof EtrCreditProtectionCertificateApplyRequest){
            EtrCreditProtectionCertificateApplyRequest dto = (EtrCreditProtectionCertificateApplyRequest)data;
            if(StringUtil.isEmptyList(dto.getPortfolioList())){
                return null;
            }
            int size = dto.getPortfolioList().size();
            dto.getPortfolioList().forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, dto);
                generateInputDto(inputDto, dto, item);
                if (size > 1){
                    inputDto.setTradeId(dto.getEntrustId()+"-"+item.getPortfolioId());
                }else {
                    inputDto.setTradeId(dto.getEntrustId());
                }
                result.add(inputDto);
            });
        }
        return result;
    }


    /**
     * 授权参数构造
     * @param inputDto
     * @param data
     * @param dtl
     * @param isCheck
     */
    private void buildAuthorizeParam(RiskCommonInputDto inputDto, Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        if (!isCheck) {
            return;
        }
        // CDS(信用保护凭证),新建、check、占用、退回、作废
        if (data instanceof EtrCreditProtectionCertificateApplyRequest){
            EtrCreditProtectionCertificateApplyRequest dto = (EtrCreditProtectionCertificateApplyRequest)data;
            inputDto.setCounterId(dto.getCounterId());
            inputDto.setPrinAmt(ArithmeticUtil.multiply(dto.getEntrustNum(),new BigDecimal("10000")));
            if(Objects.isNull(inputDto.getPrinAmt())) {
                inputDto.setPrinAmt(dto.getEntrustAmt());
            }
            inputDto.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());
            inputDto.setTradeMarket(dto.getTradeMarket());
            inputDto.setPs(dto.getPs());
            inputDto.setAssetSecType(dto.getAssetType2());
            inputDto.setFinprodId(dto.getFinprodId());
        }

    }

    /**
     * 构造决策配置入参
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
        // CDS(信用保护凭证)指令,新建、check、占用、退回、作废
        if (obj instanceof EtrCreditProtectionCertificateApplyRequest || StringUtil.equals(pageId, "etrCreditProtectionCertificate")) {
            EtrCreditProtectionCertificateApplyRequest dto = JsonUtils.json2Object(businessJson, EtrCreditProtectionCertificateApplyRequest.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "CPC001")
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_信用保护凭证交易.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, dto.getTradeMarket())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);

        }
        // CDS(信用保护凭证),撤回、关闭
        if (obj instanceof EtrExchangeTradeOperateSecRepoRequest || StringUtil.equals(pageId, "etrCreditProtectionCertificate003")) {
            EtrCreditProtectionCertificateApplyRequest dto = JsonUtils.json2Object(businessJson, EtrCreditProtectionCertificateApplyRequest.class);
            List<EtrExchangeTradePortfolioSecRepoRequest> list = new ArrayList<>();
            if (StringUtil.isNoneEmpty(dto.getPortfolioId())){
                Arrays.asList(dto.getPortfolioId().split(",")).forEach(item ->{
                    EtrExchangeTradePortfolioSecRepoRequest detailDto = new EtrExchangeTradePortfolioSecRepoRequest();
                    BeanUtil.copyPropertiesIgnoreNull(detailDto,dto);
                    detailDto.setEntrustId(dto.getEntrustId());
                    detailDto.setPortfolioId(item);
                    list.add(detailDto);
                });
            }
            dto.setPortfolioList(list);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "CPC001")
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_信用保护凭证交易.getValue())
                    .put(NodeConfigConstant.TRADE_PLACE, dto.getTradeMarket())
                    .put(NodeConfigConstant.PS, dto.getPs())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);

        }

        return null;
    }

    /**
     * 构建授信参数
     *
     * @param obj
     * @return
     */
    public void generateInputDto(CreditLinesInputDto creditLinesInputDto, Object obj, Object detailObj) {
        //CDS(信用保护凭证)指令
        if (obj instanceof EtrCreditProtectionCertificateApplyRequest) {
            EtrCreditProtectionCertificateApplyRequest inputDto = (EtrCreditProtectionCertificateApplyRequest) obj;
            EtrExchangeTradePortfolioSecRepoRequest detail = (EtrExchangeTradePortfolioSecRepoRequest)detailObj;
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            creditLinesInputDto.setTradeId(inputDto.getEntrustId() + "-" + detail.getPortfolioId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setTradeDate(inputDto.getVdate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_信用保护凭证.getValue());
            creditLinesInputDto.setPs(inputDto.getPs());
            creditLinesInputDto.setCounterId(inputDto.getCounterId());
            creditLinesInputDto.setTradeAmt(ArithmeticUtil.multiply(detail.getEntrustNum(),new BigDecimal("10000")));
        }
    }

}

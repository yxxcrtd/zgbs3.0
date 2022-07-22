package com.joyintech.tams.exchange.period.risk.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonInputDto;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import com.joyintech.tams.component.business.riskall.service.RiskNodeConfigService;
import com.joyintech.tams.component.business.riskall.utils.RiskUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.BillingDetailsDto;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import com.joyintech.tams.exchange.period.risk.dto.ManualSettleDto;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.redisinit.dto.ImplProdAssetAmount;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IProdAssetAmount;
import com.joyintech.tams.invest.common.riskinfrds.info.service.ProdAssetAmountService;
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


@Service
public class PeriodRiskInputParamService extends RiskCommonService {

    private static Logger logger = LoggerFactory.getLogger(PeriodRiskInputParamService.class);

    @Autowired
    private RiskNodeConfigService riskNodeConfigService;
    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;
    @Autowired
    private ProdAssetAmountService prodAssetAmtSrc;

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
        if (data instanceof CreditProtectionCertificatePeriodSaveDto){
            CreditProtectionCertificatePeriodSaveDto dto = (CreditProtectionCertificatePeriodSaveDto)data;
            listDto.setFinprodId(dto.getFinprodId());
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
        // CDS(信用保护凭证),存续期
        if (data instanceof CreditProtectionCertificatePeriodSaveDto){
            CreditProtectionCertificatePeriodSaveDto dto = (CreditProtectionCertificatePeriodSaveDto)data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());
            riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
            riskRdsDto.setEventStatus(BussinessDictCodeListRds.事件类型_投资交易_存续期.getValue());
            // 影响信用保护凭证本身相关参数
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getBillingDetails(), map -> {
                // 处理交易方向、交易金额等
                map.put("tradeId",dto.getBusiCode() + "-" + map.get("portfolioId") + "-" + DictCodeList.交易方向_到期.getValue());
                map.put("ps",DictCodeList.交易方向_到期.getValue());
                BigDecimal num = map.get("repNomCapi") == null ? BigDecimal.ZERO : new BigDecimal(map.get("repNomCapi").toString());
                IProdAssetAmount assetAmount = prodAssetAmtSrc.getProdAssetAmount(map.get("portfolioId").toString(),dto.getFinprodId());
                BigDecimal tradeAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(num,assetAmount.getAmount()),
                                            assetAmount.getNum(),2, RoundingMode.HALF_UP);
                map.put("tradeAmt",tradeAmt);
                map.put("finprodId",dto.getFinprodId());
            });
            // 影响现金规模
            list.addAll(interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getBillingDetails(), map -> {
                // 处理交易方向、交易金额等
                map.put("tradeId",dto.getBusiCode() + "-" + map.get("portfolioId"));
                map.put("ps",DictCodeList.交易方向_收取.getValue());
                map.put("effectCash","Y");
                BigDecimal tradeAmt = map.get("deliveryAmount") == null ? BigDecimal.ZERO : new BigDecimal(map.get("deliveryAmount").toString());
                map.put("tradeAmt",tradeAmt);
            }));
        }
        // CDS(信用保护凭证),交收失败
        if (data instanceof ManualSettleDto){
            ManualSettleDto dto = (ManualSettleDto)data;
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());
            riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getSelectedItem(), map -> {
                // 处理交易方向、交易金额等
                map.put("tradeId",dto.getEntrustNo() + "-" + map.get("portfolioId") + "-" + dto.getEntrustOrderId() );
            });
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
        if (data instanceof CreditProtectionCertificatePeriodSaveDto){
            CreditProtectionCertificatePeriodSaveDto dto = (CreditProtectionCertificatePeriodSaveDto)data;
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, dto);
            generateInputDto(inputDto, dto, dto);
            result.add(inputDto);

        }
        // CDS(信用保护凭证),交收失败
        if (data instanceof ManualSettleDto){
            ManualSettleDto dto = (ManualSettleDto)data;
            dto.getSelectedItem().forEach(item -> {
                CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, dto);
                generateInputDto(inputDto, dto, item);
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
        // CDS(信用保护凭证)存续期
        if (obj instanceof CreditProtectionCertificatePeriodSaveDto || StringUtil.equals(pageId, "CreditProtectionCertificatePeriod002")) {
            CreditProtectionCertificatePeriodSaveDto dto = JsonUtils.json2Object(businessJson, CreditProtectionCertificatePeriodSaveDto.class);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "CPC001")
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, DictCodeListExt3.头寸业务类型_信用保护凭证存续期.getValue())
                    .put(NodeConfigConstant.PS, DictCodeList.交易方向_到期.getValue())
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dto);

        }
        if (obj instanceof ManualSettleDto || StringUtil.equals(pageId, "ManualSettle001")) {
            ManualSettleDto dto = JsonUtils.json2Object(businessJson, ManualSettleDto.class);
            List<ManualSettleDto> list = new ArrayList<>();
            if (StringUtil.isNoneEmpty(dto.getPortfolioId())){
                Arrays.asList(dto.getPortfolioId().split(",")).forEach(item ->{
                    ManualSettleDto detailDto = new ManualSettleDto();
                    BeanUtil.copyPropertiesIgnoreNull(detailDto,dto);
                    detailDto.setOrderId(dto.getOrderId());
                    detailDto.setPortfolioId(item);
                    list.add(detailDto);
                });
            }
            dto.setSelectedItem(list);
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "CPC001")
                    .put(NodeConfigConstant.BUSI_ACT, DictCodeListExt3.业务动作_交收失败.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, DictCodeListExt3.头寸业务类型_信用保护凭证交易.getValue())
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
        //CDS(信用保护凭证)存续期
        if (obj instanceof CreditProtectionCertificatePeriodSaveDto) {
            CreditProtectionCertificatePeriodSaveDto inputDto = (CreditProtectionCertificatePeriodSaveDto) obj;
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            creditLinesInputDto.setTradeId(inputDto.getBusiCode());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setTradeDate(inputDto.getTradeDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_信用保护凭证.getValue());
            creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
            BigDecimal tradeAmt = BigDecimal.ZERO;
            for (BillingDetailsDto billingDetailsDto : inputDto.getBillingDetails()) {
                BigDecimal repNomCapi = billingDetailsDto.getRepNomCapi();
                if (repNomCapi != null) {
                    tradeAmt = tradeAmt.add(repNomCapi);
                }
            }
            creditLinesInputDto.setTradeAmt(tradeAmt);
        }
        //CDS(信用保护凭证)交收失败
        if (obj instanceof ManualSettleDto) {
            ManualSettleDto inputDto = (ManualSettleDto) obj;
            ManualSettleDto detail = (ManualSettleDto) detailObj;
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            creditLinesInputDto.setTradeId(inputDto.getEntrustNo() + "-" + detail.getPortfolioId() + "-" + inputDto.getEntrustOrderId());
            creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
            creditLinesInputDto.setTradeDate(inputDto.getTradeDate());
            creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
            creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_信用保护凭证.getValue());
            creditLinesInputDto.setPs(inputDto.getPs());
            // 交收失败不回冲预占
            creditLinesInputDto.setFinprodType2("ManualSettle");
        }

    }

}

package com.joyintech.interf.bankname.riskinterf.service;

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
import com.joyintech.tams.component.business.riskall.dto.RiskCommonOutputDto;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import com.joyintech.tams.component.business.riskall.service.RiskNodeConfigService;
import com.joyintech.tams.component.business.riskall.utils.RiskUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.workflow.engine.WorkflowContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;



@Service
public class ExchangeTrdRiskInputParamService extends RiskCommonService {

    private static Logger logger = LoggerFactory.getLogger(ExchangeTrdRiskInputParamService.class);

    @Autowired
    private RiskNodeConfigService riskNodeConfigService;
    @Autowired
    private InterfRiskInterfService<InterfRiskRdsDto> interfRiskRdsService;
    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    /**
     * 指标和授信占用
     *  @param dataMap
     * @param decisionAct
     * @param flowStatus
     */
    public Map<String, Object> buildParamThenOccupy(Map<String, Object> dataMap, String decisionAct, String flowStatus,ClientData resData) {
        BaseMap map = generateBusinessDto(dataMap, decisionAct, flowStatus, false);
        RiskCommonInputDto inputDto = getRiskInputParam(map);
        if (inputDto == null) {
            return new HashMap<>();
        }
        resData = doOccupy(inputDto);
        return resData.getBody();
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
        Map<String,Object> dataMap = JsonUtils.object2Map(data);
        List<InterfRiskRdsDto> result = new ArrayList<>();
        // CDS(信用保护凭证),新建、check、占用、退回、作废
        String assetType = (String) dataMap.get("assetType");
        String eventType = (String) dataMap.get("eventType");
        List<Map<String, Object>> listMap = (List<Map<String, Object>>) dataMap.get("rskList");
        if(StringUtil.equals(assetType, DictCodeList.平安资产类型_信用保护凭证.getValue())){
            if (StringUtil.isEmptyList(listMap)){
                return null;
            }
            InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
            riskRdsDto.setAssetType(assetType);
            riskRdsDto.setEventType(eventType);
            listMap.forEach(item -> {
                try {
                    InterfRiskRdsDto interfRiskRdsDto = interfRiskRdsService.prepareRiskEntity(item,riskRdsDto);
                    interfRiskRdsDto.setPreOccupyCode(interfRiskRdsDto.getEntrustCode());
                    interfRiskRdsDto.setAssetCode(trdCommonBaseService.queryFinprodIdByFinprodMarketId(interfRiskRdsDto.getAssetCode(), assetType));
                    result.add(interfRiskRdsDto);
                } catch (Exception e) {
                    logger.info("风险指标调用参数组装异常:{}",e.getMessage());
                }

            });

        }
        return result;
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
        return new ArrayList<>();
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


    }

    /**
     * 构造决策配置入参
     *
     * @param dataMap
     * @param decisionAct
     * @param flowStatus
     * @param isCheck
     * @return
     */
    private BaseMap generateBusinessDto(Map<String, Object> dataMap, String decisionAct, String flowStatus, boolean isCheck) {
        String assetType = (String) dataMap.get("assetType");
        List<Map<String, Object>> listMap = (List<Map<String, Object>>) dataMap.get("rskList");
        if(StringUtil.equals(assetType, DictCodeList.平安资产类型_信用保护凭证.getValue())){
            if (StringUtil.isEmptyList(listMap)){
                return null;
            }
            return BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, "CPC001")
                    .put(NodeConfigConstant.BUSI_ACT, DictCodeListExt3.业务动作_交易登记.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, decisionAct)
                    .put(NodeConfigConstant.FLOW_STATUS, flowStatus)
                    .put(NodeConfigConstant.BUSI_TYPE, DictCodeListExt3.头寸业务类型_信用保护凭证交易.getValue())
                    .put(NodeConfigConstant.PS, listMap.get(0).get("ps"))
                    .put(NodeConfigConstant.IS_CHECK, isCheck)
                    .put(NodeConfigConstant.DATA, dataMap);
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
    }

}

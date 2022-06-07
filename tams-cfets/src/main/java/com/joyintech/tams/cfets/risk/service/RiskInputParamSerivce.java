package com.joyintech.tams.cfets.risk.service;

import com.google.common.collect.Lists;
import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.service.bankuplink.common.dto.TrdBondInvestTradeDto;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtribBondInfo003Service;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdBondInvestTradeDto;
import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
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
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.*;

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


    /**
     * 风险试算和校验
     *
     * @param param
     * @return
     */
    public RiskCommonOutputDto doRisk(BaseMap param) {
        WorkflowContext.getProcessMarkService().getBusinessInfo("").getPageId();
        RiskCommonInputDto inputDto = getRiskInputParam(param);
        if (inputDto == null) return null;
        return doRisk(inputDto);
    }

    private BaseMap generateBusinessDto(Object obj,String decisionAct,String flowStatus,boolean isCheck){
        String pageId = "";
        ProcessMark processMark;
        String businessJson = JsonUtils.object2Json(obj);
        // 入参为 workflowId 时 ，需要从流程中获取业务参数
        if (obj instanceof String){
            processMark = WorkflowContext.getProcessMarkService().getBusinessInfo((String) obj);
            pageId = processMark.getPageId();
            businessJson = processMark.getBusinessData();
        }
        if (obj instanceof CfetsEtribBondInfo003Dto || StringUtil.equals(pageId,"CfetsEtrIbBondinfo003")) {
            CfetsEtribBondInfo003Dto dto = JsonUtils.json2Object(businessJson,CfetsEtribBondInfo003Dto.class);
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
        return null;
    }

    /**
     * 指标、授信、名单、授权check
     **/
    public RiskCommonOutputDto buildParamThenRisk(Object obj,String decisionAct,String flowStatus,boolean isCheck) {
        BaseMap map = generateBusinessDto(obj,decisionAct,flowStatus,isCheck);
        RiskCommonInputDto inputDto = getRiskInputParam(map);
        if (inputDto == null) return null;
        return doRisk(inputDto);
    }

    /**
     * 指标和授信占用
     **/
    public void buildParamThenOccupy(Object obj,String decisionAct,String flowStatus,boolean isCheck) {
        BaseMap map = generateBusinessDto(obj,decisionAct,flowStatus,isCheck);
        RiskCommonInputDto inputDto = getRiskInputParam(map);
        if (inputDto == null) return;
        doOccupy(inputDto);
    }

    /**
     * 指标和授信占用
     *
     * @param param
     */
    public void doOccupy(BaseMap param) {
        RiskCommonInputDto inputDto = getRiskInputParam(param);
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
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, ((CfetsEtribBondInfo003Dto) data).getEtrInvestBondInfoDtos(), null);
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
            if(CollectionUtils.isEmpty(entity.getDtoList())) {//处理撤销时
                for(BussTrdTransfenpeiDto item : entity.getBussTrdTransfenpeiDto()) {
                    TrdBondInvestTradeDto bondDto = new TrdBondInvestTradeDto();
                    BeanUtil.copyPropertiesIgnoreNull(bondDto, item);
                    bondDto.setTotleTradeId(item.getTradeId());
                    list1.add(bondDto);
                }
            }
            list = interfRiskRdsService.getRiskRdsListData(riskRdsDto, list1, null);
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
                this.generateInputDto(inputDto, data);
                result.add(inputDto);
            });

        }
        if (data instanceof CfetsTrdBondInvestTradeDto) {
            CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl);
            this.generateInputDto(inputDto, data);
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
        if (this.isControl(dtl.getNameListControl())) return null;
        RiskNameListDto listDto = new RiskNameListDto();
        Map<String, Object> map = BeanUtil.object2Map(data);
        listDto.setFinprodId((String) map.get("finprodId"));
        listDto.setCounterId((String) map.get("counterId"));
        //如果是质押式回购
        //listDto.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
        //listDto.setInputValue2(DictCodeList.回购类型_质押式回购.getValue());
        //listDto.setInputValue3(DictCodeListExt3.行内资产三级分类_正回购.getValue());


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
        Map<String, Object> map = BeanUtil.object2Map(data);
        dto.setCounterId((String) map.get("counterId"));
        dto.setPrinAmt((BigDecimal) map.get("tradeAmt"));
        dto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
        dto.setTradeMarket((String) map.get("tradeMarket"));
        dto.setPs((String) map.get("ps"));
        dto.setAssetSecType((String) map.get("assetSecType"));
        dto.setFinprodId((String) map.get("finprodId"));

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
    public void generateInputDto(CreditLinesInputDto creditLinesInputDto, Object obj) {
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
    }

}

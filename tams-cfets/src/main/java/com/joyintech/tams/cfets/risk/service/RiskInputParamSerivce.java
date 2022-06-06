package com.joyintech.tams.cfets.risk.service;

import com.google.common.collect.Lists;
import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonInputDto;
import com.joyintech.tams.component.business.riskall.service.RiskNodeConfigService;
import com.joyintech.tams.component.business.riskall.utils.RiskUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.risk.service.RiskNameListCommonBatchService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesDealsMapper;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用来组装风险试算需要的指标、授信、授权、名单试算所需要的参数
 *
 * @author gwl
 * @date 2022-06-02
 */
@Service
public class RiskInputParamSerivce {

    @Autowired
    private RiskNodeConfigService riskNodeConfigService;
    @Autowired
    private InterfRiskRdsService interfRiskRdsService;
    @Autowired
    private CreditLinesDealsMapper creditLinesDealsMapper;
    @Autowired
    private RiskNameListCommonBatchService riskNameListCommonBatchService;
    @Autowired
    private CommonBusinessService commonBusinessService;


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
        Object data = param.get(NodeConfigConstant.DATA);
        RiskCommonInputDto inputDto = new RiskCommonInputDto();
        this.buildAuthorizeParam(inputDto, data, dtl);
        inputDto.setCreditInputList(this.buildCreditParam(data, dtl));
        inputDto.setRiskRdsDtoList(this.buildRiskRdsParam(data, dtl, (Boolean) param.get("isCheck")));
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
            list.forEach(e -> e.put("infCode","RSK011"));
            System.out.println(JsonUtils.object2Json(interfRiskRdsService.sendListDataCommon(list, riskRdsDto.getInfCode())));
            System.out.println("1");
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
            AjaxResult ajaxResult = AllTradeCommonUtil.checkBeforeTradeComplete(result);
            System.out.println("1");
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

        System.out.println(JsonUtils.object2Json(riskNameListCommonBatchService.名单批量试算(Lists.newArrayList(listDto))));
        System.out.println("1");
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
        System.out.println(commonBusinessService.是否交易员授权(dto.getCounterId(), dto.getPrinAmt(), dto.getAssetType(), null, dto.getTradeMarket(), dto.getPs(), dto.getFinprodId()));
        System.out.println("1");
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
    }

}

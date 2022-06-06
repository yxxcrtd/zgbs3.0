package com.joyintech.tams.component.business.riskall.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.FinProductTypeConfig;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.riskall.dao.RiskCommonMapper;
import com.joyintech.tams.component.business.riskall.dto.*;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risk.dao.RiskNamListCommonMapper;
import com.joyintech.tams.invest.common.risk.dto.RiskNamListCommonDto;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.risk.service.RiskNameListCommonBatchService;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.CreditLinesUpdate;
import com.joyintech.tams.invest.common.trade.CreditLinesUpdateImpl;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesOutputDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 风险升级 4合一 接口组件
 *
 * @author 代志伟
 * @version 1.0 2022年5月30日
 * @see RiskCommonService
 * @since 1.0
 */

@Service
public class RiskCommonService {

    private static final Logger logger = LoggerFactory.getLogger(RiskCommonService.class);

    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

    @Autowired
    private RiskNameListCommonBatchService riskNameListCommonBatchService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private IPlatformDate platformDate;

    @Autowired
    private RiskNamListCommonMapper riskNamListCommonMapper;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    @Autowired
    private RiskCommonMapper riskCommonMapper;

    @Autowired(required = false)
    @Qualifier("loadBalancedRestTemplate")
    private JoyinRestTemplate restTemplate;

    /**
     * 匹配场景信息
     * @return RiskSceneConfigDto
     */
    protected RiskSceneConfigDto matchRiskSceneConfig(){


        return new RiskSceneConfigDto();
    }

    /**
     * 风险占用
     * @param riskCommonInputDto
     */
    protected void doOccupy(RiskCommonInputDto riskCommonInputDto){
        // 判断授信是否控制
        // 入参不为空、授信控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险控制类别_不控.getValue())
            && !StringUtil.isEmptyList(riskCommonInputDto.getCreditInputList())){
            String occupyType = "";
            List<CreditLinesInputDto> list = new ArrayList<>();
            if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_预占.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_预占.getValue();
                list.addAll(riskCommonInputDto.getCreditInputList());
            }else if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_实占.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_实占.getValue();
                list.addAll(riskCommonInputDto.getCreditInputList());
            }else if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_预占释放.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_预占.getValue();
                list.addAll(riskCommonInputDto.getCreditInputList());
            }else if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_实占释放.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_实占.getValue();
                list.addAll(riskCommonInputDto.getCreditInputList());
            }else if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_预占释放再预占.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_预占.getValue();
                // 处理差分预占参数，接口支持程度待确认
                riskCommonInputDto.getCreditInputList().forEach(item -> {
                    item.setCreditControl(riskCommonInputDto.getCreditControl());
                    // 预占释放再预占场景，针对每一笔报文构造对应的释放报文
                    CreditLinesInputDto inputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(inputDto,item);
                    inputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    // 先释放
                    list.add(inputDto);
                    // 再预占
                    list.add(item);
                });
            }else if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占释放再实占.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_实占.getValue();
                // 处理差分预占参数，接口支持程度待确认
                riskCommonInputDto.getCreditInputList().forEach(item -> {
                    item.setCreditControl(riskCommonInputDto.getCreditControl());
                    // 实占释放再实占场景，针对每一笔报文构造对应的释放报文
                    CreditLinesInputDto inputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(inputDto,item);
                    inputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    // 先释放
                    list.add(inputDto);
                    // 再实占
                    list.add(item);
                });
            }else if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占预占释放.getValue())) {
                // 接口调用方式待梳理
            }

            CreditLinesUpdate creditLinesUpdate = SpringContextUtil.getBean(CreditLinesUpdateImpl.class);
            if (creditLinesUpdate != null) {
                if (StringUtil.equals(occupyType,DictCodeListRsk.授信占用类别_预占.getValue())){
                    creditLinesUpdate.occupyCreditLinesEtr(list,platformDate.getSysDate());
                }
                if (StringUtil.equals(occupyType,DictCodeListRsk.授信占用类别_实占.getValue())){
                    creditLinesUpdate.occupyCreditLinesTrd(list,platformDate.getSysDate());
                }
            }
        }
        // 入参不为空、指标控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getIndicatorControl(), DictCodeListRsk.风险控制类别_不控.getValue())
            && !StringUtil.isEmptyList(riskCommonInputDto.getRiskRdsDtoList())) {
            InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
            interfRiskRdsDto.setAssetType(riskCommonInputDto.getAssetType());
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_预占.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
                interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue());
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_预占释放.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
                interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue());
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占释放.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue());
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_预占释放再预占.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
                interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue());
                // 处理差分预占参数，接口支持程度待确认
                riskCommonInputDto.getRiskRdsDtoList().forEach(item -> item.setIsDiffOccupy(DictCodeList.是否_是.getValue()));
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占释放再实占.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
                // 处理差分实占参数，接口暂未支持
                riskCommonInputDto.getRiskRdsDtoList().forEach(item -> item.setIsDiffOccupy(DictCodeList.是否_是.getValue()));
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占预占释放.getValue())) {
                // 接口调用方式待梳理
            }
            List<Map<String, Object>> sourceList = riskCommonInputDto.getRiskRdsDtoList().stream().map(JsonUtils::object2Map).collect(Collectors.toList());
            interfRiskRdsService.sendListDataCommon(sourceList, interfRiskRdsDto.getInfCode());

        }
    }

    /**
     * 风险试算
     * @param riskCommonInputDto
     * @return RiskCommonOutputDto
     */
    protected RiskCommonOutputDto doRisk(RiskCommonInputDto riskCommonInputDto){
        RiskCommonOutputDto riskCommonOutputDto = new RiskCommonOutputDto();
        // 判断授信是否控制
        // 入参不为空、授信控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险控制类别_不控.getValue())
            && !StringUtil.isEmptyList(riskCommonInputDto.getCreditInputList())){
            List<CreditLinesInputDto> list = new ArrayList<>();
            if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_预占.getValue())) {
                list.addAll(riskCommonInputDto.getCreditInputList());
            }else if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_实占.getValue())) {
                list.addAll(riskCommonInputDto.getCreditInputList());
            }else if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_预占释放.getValue())) {
                list.addAll(riskCommonInputDto.getCreditInputList());
            }else if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_实占释放.getValue())) {
                list.addAll(riskCommonInputDto.getCreditInputList());
            }else if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_预占释放再预占.getValue())
                || StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占释放再实占.getValue())) {
                // 处理差分预占参数，接口支持程度待确认
                riskCommonInputDto.getCreditInputList().forEach(item -> {
                    item.setCreditControl(riskCommonInputDto.getCreditControl());
                    // 预占释放再预占场景，针对每一笔报文构造对应的释放报文
                    CreditLinesInputDto inputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(inputDto,item);
                    inputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    // 先释放
                    list.add(inputDto);
                    // 再预占(实占)
                    list.add(item);
                });
            }else if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占释放再实占.getValue())) {
                // 处理差分预占参数，接口支持程度待确认
                riskCommonInputDto.getCreditInputList().forEach(item -> {
                    // 实占释放再实占场景，针对每一笔报文构造对应的释放报文
                    CreditLinesInputDto inputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(inputDto,item);
                    inputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    // 先释放
                    list.add(inputDto);
                    // 再实占
                    list.add(item);
                });
            }
            CreditLinesUpdate creditLinesUpdate = SpringContextUtil.getBean(CreditLinesUpdateImpl.class);
            if (creditLinesUpdate != null) {
                List<CreditLinesOutputDto> resultList = creditLinesUpdate.occupyCreditLinesCheck(list);
                riskCommonOutputDto.setCreditResultList(resultList);
            }
        }
        // 判断风险指标是否控制
        // 入参不为空、指标控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getIndicatorControl(), DictCodeListRsk.风险控制类别_不控.getValue())
            && !StringUtil.isEmptyList(riskCommonInputDto.getRiskRdsDtoList())){

            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_预占释放再预占.getValue())) {
                // 处理差分预占参数，接口支持程度待确认
                riskCommonInputDto.getRiskRdsDtoList().forEach(item -> item.setIsDiffOccupy(DictCodeList.是否_是.getValue()));
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占释放再实占.getValue())) {
                // 处理差分预占参数，接口支持程度待确认
                riskCommonInputDto.getRiskRdsDtoList().forEach(item -> item.setIsDiffOccupy(DictCodeList.是否_是.getValue()));
            }
            try {
                List<Map<String, Object>> sourceList = riskCommonInputDto.getRiskRdsDtoList().stream().map(JsonUtils::object2Map).collect(Collectors.toList());
                ClientData rsp = interfRiskRdsService.sendListDataCommon(sourceList, BussinessDictCodeListRds.接口系统ID_风险试算批量接口.getValue());
                riskCommonOutputDto.setRskStatus(String.valueOf(rsp.getBody().get("rskStatus")));
                List<RiskTrialRetDto> riskTrialRetDto = JsonUtils.json2List(JsonUtils.object2Json(rsp.getBodyField("RSK_LIST")),RiskTrialRetDto.class);
                riskCommonOutputDto.setRiskTrialRetDto(riskTrialRetDto);
            } catch (Exception e) {
                logger.error("指标接口调用异常:{}",e.getMessage());
            }
        }
        // 判断授权是否控制
        // 入参不为空、授权控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getAuthorizeControl(), DictCodeListRsk.风险控制类别_不控.getValue())){
            List<RiskAuthorizeDto> authorizeList = new ArrayList<>();
            RiskAuthorizeDto riskAuthorizeDto = excessTraderAuthorizeAmt(riskCommonInputDto);
            if (Objects.nonNull(riskAuthorizeDto)){
                authorizeList.add(riskAuthorizeDto);
            }
            riskCommonOutputDto.setAuthorizeList(authorizeList);
        }
        // 判断名单是否控制
        // 名单控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getNameListControl(), DictCodeListRsk.风险控制类别_不控.getValue())){
            if(!StringUtil.isEmptyList(riskCommonInputDto.getRiskNameList())) {
                List<RiskNameListDto> riskNameListDtoList = riskCommonInputDto.getRiskNameList();
                List<RiskNameListResult> resultList = riskNameListValid(riskNameListDtoList,riskCommonInputDto.getNameListType());
                riskCommonOutputDto.setRiskNameListResultList(resultList);
            }
        }
        
        return riskCommonOutputDto;
    }

    /**
     * 判断是否超过交易员授权额度
     * @param riskCommonInputDto
     * @return
     */
    private RiskAuthorizeDto excessTraderAuthorizeAmt(RiskCommonInputDto riskCommonInputDto){
        if (StringUtil.isNotEmpty(riskCommonInputDto.getCounterId())){
            PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(riskCommonInputDto.getCounterId());
            // 内部交易走授权
            if (portfolio != null) {
                return new RiskAuthorizeDto();
            }
        }
        FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(riskCommonInputDto.getAssetType(),riskCommonInputDto.getAssetType());
        Map<String,Object> params = new HashMap<>();
        params.put("userName", LoginUserUtil.getUserName());
        params.put("assetType", riskCommonInputDto.getAssetType());
        params.put("assetSecType", riskCommonInputDto.getAssetType());
        params.put("tradeMarket", StringUtil.isEmpty(riskCommonInputDto.getTradeMarket()) ? "999999" : riskCommonInputDto.getTradeMarket());
        params.put("tradeDirection", riskCommonInputDto.getPs());
        // {参数字典.授权类型.授权、参数字典.授权类型.无限授权}
        params.put("AUTHORIZE_TYPE_02", DictCodeList.授权类型_授权.getValue());
        params.put("AUTHORIZE_TYPE_03", DictCodeList.授权类型_无限授权.getValue());
        params.put("AUTHORIZE_TYPE_01", DictCodeList.授权类型_不授权.getValue());
        if (Objects.nonNull(finProductTypeConfig)){
            params.put("assetType", finProductTypeConfig.getOutputValue1());
            params.put("assetSecType", finProductTypeConfig.getOutputValue2());
        }
        // 查询授权信息（全量信息，包括资产类型、交易场所、交易方向、授权类型、授权金额）
        List<RiskAuthorizeDto> riskAuthorizeDtoList = riskCommonMapper.findRiskAuthorize(params);
        RiskAuthorizeDto riskAuthorizeDto = null;
        if (!StringUtil.isEmptyList(riskAuthorizeDtoList)){
            riskAuthorizeDto = riskAuthorizeDtoList.get(0);
        }
        BigDecimal authorizeLimitAmt = BigDecimal.ZERO;
        BigDecimal prinAmt = riskCommonInputDto.getPrinAmt();
        if(Objects.nonNull(riskAuthorizeDto)){
            authorizeLimitAmt  = riskAuthorizeDto.getAuthorizeLimitAmt();
            riskAuthorizeDto.setRskStatus(DictCodeListRsk.试算结果_通过.getValue());
        }
        // 业务金额为空
        if (prinAmt == null || prinAmt.compareTo(BigDecimal.ZERO) == 0 || Objects.isNull(riskAuthorizeDto)){
            return riskAuthorizeDto;
        }
        // 无授权数据 或者 授权额度不足
        if (StringUtil.equals(riskAuthorizeDto.getAuthorizeType(),DictCodeList.授权类型_授权.getValue()) && prinAmt.compareTo(authorizeLimitAmt) > 0){
            riskAuthorizeDto.setAuthorizeLimit(true);
            riskAuthorizeDto.setRskStatus(DictCodeListRsk.试算结果_不通过.getValue());
        }
        return riskAuthorizeDto;
    }

    private FinProductTypeConfig getRskAuthorizeType(String assetType, String assetSecType) {
        if (StringUtil.isEmpty(assetType)){
            return null;
        }
        FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
        finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_授权分类.getValue());
        finProductTypeConfig.setInputValue1(assetType);
        finProductTypeConfig.setInputValue2(assetSecType);

        return restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
                finProductTypeConfig, FinProductTypeConfig.class);

    }

    /**
     * 名单校验
     * @param riskNameListDtoList
     * @param nameListType
     * @return
     */
    private List<RiskNameListResult> riskNameListValid(List<RiskNameListDto> riskNameListDtoList,String nameListType){
        List<RiskNamListCommonDto> dtoList = new ArrayList<RiskNamListCommonDto>();
        List<RiskNameListResult> resultList = new ArrayList<>();
        for(RiskNameListDto riskNameList : riskNameListDtoList){
            Map<String,Object> param = new HashMap<>();
            //通过资产id get相关信息
            param.put("RISK_TYPE", DictCodeListExt2.配置类型_风险分类.getValue());
            if(StringUtil.isNotEmpty(riskNameList.getFinprodId())){
                param.put("finprodId",riskNameList.getFinprodId());
                dtoList.addAll(riskNamListCommonMapper.getAllInfoByFinprodId(param));
            }else{
                param.put("inputValue1",riskNameList.getInputValue1());
                param.put("inputValue2",riskNameList.getInputValue2());
                param.put("inputValue3",riskNameList.getInputValue3());
                dtoList.addAll(riskNamListCommonMapper.getAllInfoByNoFinprodId(param));
            }
            if(StringUtil.isNotEmpty(riskNameList.getCounterId())){
                param.put("counterId",riskNameList.getCounterId());
            }
            for(RiskNamListCommonDto item: dtoList) {
                // 名单校验列表为空
                if (item == null || StringUtil.isEmpty(item.getListId())){
                    continue;
                }
                // 名单控制类别为资产名单时，过滤掉机构名单(名单主体明细类型 不是资产)，不校验
                if (StringUtil.equals(nameListType,DictCodeListRsk.名单控制类别_资产.getValue())
                    && !StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_资产.getValue())){
                    continue;
                }
                // 名单控制类别为机构名单时，过滤掉资产名单(名单主体明细类型是资产)，不校验
                if (StringUtil.equals(nameListType,DictCodeListRsk.名单控制类别_机构.getValue())
                    && StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_资产.getValue())){
                    continue;
                }
                // 名单主体明细类型为资产、资产代码为空时
                if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_资产.getValue())
                    && StringUtil.isNotEmpty(item.getFinprodId())){
                    Map<String, Object> nameListMap = new HashMap<>();
                    nameListMap.put("listId", item.getListId());
                    nameListMap.put("finprodId", item.getFinprodId());
                    nameListMap.put("finprodName", item.getFinprodName());
                    nameListMap.put("finprodAbbr", item.getFinprodAbbr());
                    nameListMap.put("finprodMarketId", item.getFinprodMarketId());
                    List<RiskNamListCommonDto> dtos = riskNamListCommonMapper.selectAssetCount(nameListMap);
                    if (!StringUtil.isEmptyList(dtos)) {
                        RiskNameListResult riskNameListResult = new RiskNameListResult();
                        riskNameListResult.setListName(item.getListName());
                        riskNameListResult.setName(item.getFinprodAbbr());
                        riskNameListResult.setListDetailType("资产名单");
                        if (StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_黑名单.getValue())) {
                            riskNameListResult.setErrorMsg("资产在黑名单内: " + item.getFinprodId());
                            riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_不通过.getValue());
                            riskNameListResult.setListType(DictCodeListRsk.名单属性_黑名单.getValue());
                        }else {
                            riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_通过.getValue());
                            riskNameListResult.setListType(DictCodeListRsk.名单属性_白名单.getValue());
                        }
                        resultList.add(riskNameListResult);
                    }
                    continue;
                }
                Map<String, Object> nameListMap = new HashMap<>();
                nameListMap.put("listId", item.getListId());
                String customerId = "";
                if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_发行人.getValue())){
                    customerId = item.getIssuer();
                }
                if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_管理人.getValue())){
                    customerId = item.getManager();
                }
                if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_融资人.getValue())){
                    customerId = item.getFinancier();
                }
                if (StringUtil.isNotEmpty(customerId)){
                    RiskNamListCommonDto riskNameListCommonDto = riskNamListCommonMapper.selectCustomerInfo(customerId);
                    if (isCustomerMatch(riskNameListCommonDto, nameListMap)) {
                        RiskNameListResult riskNameListResult = new RiskNameListResult();
                        riskNameListResult.setListName(item.getListName());
                        riskNameListResult.setName(item.getFinprodAbbr());
                        riskNameListResult.setListDetailType("机构名单");
                        if (StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_黑名单.getValue())) {
                            riskNameListResult.setErrorMsg("机构在黑名单内: "+riskNameListCommonDto.getCustomerName());
                            riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_不通过.getValue());
                            riskNameListResult.setListType(DictCodeListRsk.名单属性_黑名单.getValue());
                        }else {
                            riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_通过.getValue());
                            riskNameListResult.setListType(DictCodeListRsk.名单属性_白名单.getValue());
                        }
                        resultList.add(riskNameListResult);
                    }
                    continue;
                }
                if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt3.名单主体明细类型_交易对手.getValue())) {
                    if (param.containsKey("counterId") && StringUtil.isNotEmpty(param.get("counterId"))) {
                        RiskNamListCommonDto riskNameListCommonDto = riskNamListCommonMapper.selectCustomerInfo2(param.get("counterId").toString(), DictCodeListExt1.交易对手类型_市场机构.getValue());
                        if (isCustomerMatch(riskNameListCommonDto, nameListMap)) {
                            RiskNameListResult riskNameListResult = new RiskNameListResult();
                            riskNameListResult.setListName(item.getListName());
                            riskNameListResult.setName(item.getFinprodAbbr());
                            riskNameListResult.setListDetailType("机构名单");
                            if (StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_黑名单.getValue())) {
                                riskNameListResult.setErrorMsg("交易对手在黑名单内: "+riskNameListCommonDto.getCustomerName());
                                riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_不通过.getCodeName());
                                riskNameListResult.setListType(DictCodeListRsk.名单属性_黑名单.getCodeName());
                            }else {
                                riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_通过.getCodeName());
                                riskNameListResult.setListType(DictCodeListRsk.名单属性_白名单.getCodeName());
                            }
                            resultList.add(riskNameListResult);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 机构是否匹配
     * @param riskNameListCommonDto
     * @param nameListMap
     * @return
     */
    private boolean isCustomerMatch(RiskNamListCommonDto riskNameListCommonDto, Map<String, Object> nameListMap){
        if(null!=riskNameListCommonDto && StringUtil.isNotEmpty(riskNameListCommonDto.getCustomerId())){
            nameListMap.put("finprodId", riskNameListCommonDto.getInsitituteCode());
            nameListMap.put("finprodName", riskNameListCommonDto.getCustomerName());
            nameListMap.put("finprodAbbr", riskNameListCommonDto.getCustomerName());
            List<RiskNamListCommonDto> dtos = riskNamListCommonMapper.selectAssetCount(nameListMap);
            return CollectionUtils.isNotEmpty(dtos);
        }
        return false;
    }
}

package com.joyintech.tams.component.business.riskall.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductTypeConfig;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.riskall.dao.RiskCommonMapper;
import com.joyintech.tams.component.business.riskall.dto.*;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
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

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

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
                list.forEach(item -> item.setCreditControl(riskCommonInputDto.getCreditControl()));
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
            // 处理软硬控参数
            if (StringUtil.equals(riskCommonInputDto.getIndicatorControl(), DictCodeListRsk.风险控制类别_软控.getValue())){
                riskCommonInputDto.getRiskRdsDtoList().forEach(item -> {
                    item.setIsIgnoreFoul(DictCodeList.是否_是.getValue());
                    item.setIgnoreStatus(DictCodeList.是否_是.getValue());
                });
            }else {
                riskCommonInputDto.getRiskRdsDtoList().forEach(item -> {
                    item.setIsIgnoreFoul(DictCodeList.是否_否.getValue());
                    item.setIgnoreStatus(DictCodeList.是否_否.getValue());
                });
            }
            List<Map<String, Object>> sourceList = riskCommonInputDto.getRiskRdsDtoList().stream().map(JsonUtils::object2Map).collect(Collectors.toList());
            ClientData resp = interfRiskRdsService.sendListDataCommon(sourceList, interfRiskRdsDto.getInfCode());
            // 调用接口返回错误信息时抛出异常
            if (resp != null && StringUtil.equals(resp.getHeadField(InterfUtil.std400msgt), InterfUtil.E)
                && StringUtil.isNotEmpty(resp.getHeadField(InterfUtil.stdrtninfo))) {
                throw new BusinessException(InterfUtil.MSG + resp.getHeadField(InterfUtil.stdrtninfo));
            }

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
                    // 预占释放再预占场景，针对每一笔报文构造对应的释放报文
                    CreditLinesInputDto inputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(inputDto,item);
                    inputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    // 先释放
                    list.add(inputDto);
                    // 再预占(实占)
                    list.add(item);
                });
            }
            CreditLinesUpdate creditLinesUpdate = SpringContextUtil.getBean(CreditLinesUpdateImpl.class);
            if (creditLinesUpdate != null) {
                // 处理软硬控
                list.forEach(item -> item.setCreditControl(riskCommonInputDto.getCreditControl()));
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
                riskCommonOutputDto.setRskTrialStatus(String.valueOf(rsp.getBody().get("rskStatus")));
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
        // 根据软硬控参数处理风险试算结果
        buildResult(riskCommonInputDto,riskCommonOutputDto);
        return riskCommonOutputDto;
    }

    /**
     * 根据软硬控参数处理风险试算结果
     * 1．当风险校验开关均为软控，即名单、授信、风险、授权校验均为软控时，点击提交：
     * 1)	当汇总风险试算结果为‘试算通过’时，点击提交按钮，不做提示；
     * 2)	当汇总风险试算结果为‘试算不通过’，点击提交时进行校验提示，即：
     * a.	当名单试算结果不通过，提示‘名单风险试算不通过，是否确认提交！’；
     * b.	授信试算结果不通过，提示‘授信风险试算不通过，是否确认提交！’；
     * c.	指标试算结果不通过，即指标状态中存在“违规”状态时，提示‘指标试算不通过，是否确认提交!’；
     * d.	指标试算结果为指标审批，即指标状态中存在“审批”状态且无“违规”状态时，提示‘指标审批，是否确认提交!’
     * e.	发起人授权试算结果为“超授权”，提示‘发起人超授权，是否确认提交！’；
     * f.	存在多个试算结果不通过时，用‘，’间隔，见输入输出描述；
     * 2．当风险校验开关为硬控时，点击提交：
     * 1)	当汇总试算结果为‘试算通过’时，不做提示；
     * 2)	当汇总试算结果为‘试算不通过’时，点击提交时进行校验提示，即：
     * a.	当名单试算结果不通过，且名单开关为硬控时，提示‘名单风险试算不通过，不允许提交！’；
     * b.	当授信试算结果不通过，且授信开关为硬控时，提示‘授信风险试算不通过，不允许提交！’；
     * c.	当指标试算结果不通过，即指标状态中存在“违规”状态，且指标开关为硬控时，提示‘指标风险试算不通过，不允许提交！’；
     * d.	当发起人授权试算结果为“超授权”，提示‘发起人超授权，不允许提交！’；
     * 3.风险审批结果判断
     * 1)	当上述参数开关为软控时，不触发风险审批；
     * 2)	当上述参数开关为硬控时，判断是否触发风险审批，触发则进入风险审批流；
     * @param riskCommonInputDto
     * @param riskCommonOutputDto
     */
    private void buildResult(RiskCommonInputDto riskCommonInputDto, RiskCommonOutputDto riskCommonOutputDto) {
        if (Objects.isNull(riskCommonOutputDto)){
            return;
        }
        riskCommonOutputDto.setRskStatus(DictCodeListRsk.试算结果_通过.getValue());
        StringBuffer confirmMsg = new StringBuffer("");
        // 名单校验
        List<RiskNameListResult> riskNameListResultList = riskCommonOutputDto.getRiskNameListResultList();
        if (!StringUtil.isEmptyList(riskNameListResultList)){
            for (RiskNameListResult riskNameListResult : riskNameListResultList){
                if (StringUtil.equals(riskNameListResult.getRskStatus(),DictCodeListRsk.试算结果_不通过.getValue())
                        && StringUtil.isNotEmpty(riskNameListResult.getErrorMsg())){
                    riskCommonOutputDto.setRskStatus(DictCodeListRsk.试算结果_不通过.getValue());
                    if (StringUtil.equals(riskCommonInputDto.getNameListControl(),DictCodeListRsk.风险控制类别_硬控.getValue())){
                        riskCommonOutputDto.setErrorMsg("名单风险试算不通过，不允许提交！");
                        // 存在硬控消息只提示其中一个，返回结果
                        return;
                    }else {
                        // 软控消息拼接，并且一种风险类型只拼接一次
                        confirmMsg.append("名单风险试算不通过，是否确认提交！");
                        break;
                    }
                }
            }
        }
        // 授信校验
        List<CreditLinesOutputDto> creditResultList = riskCommonOutputDto.getCreditResultList();
        if (!StringUtil.isEmptyList(creditResultList)){
            for (CreditLinesOutputDto creditLinesOutputDto : creditResultList){
                if (StringUtil.equals(creditLinesOutputDto.getRskStatus(),DictCodeListRsk.试算结果_不通过.getValue())
                    && StringUtil.isNotEmpty(creditLinesOutputDto.getErrMessage())){
                    riskCommonOutputDto.setRskStatus(DictCodeListRsk.试算结果_不通过.getValue());
                    if (StringUtil.equals(riskCommonInputDto.getCreditControl(),DictCodeListRsk.风险控制类别_硬控.getValue())){
                        riskCommonOutputDto.setErrorMsg("授信风险试算不通过，不允许提交！");
                        // 存在硬控消息只提示其中一个，返回结果
                        return;
                    }else {
                        // 软控消息拼接，并且一种风险类型只拼接一次
                        confirmMsg.append("授信风险试算不通过，是否确认提交！");
                        break;
                    }
                }
            }
        }
        // 指标校验
        String riskTrialStatus = riskCommonOutputDto.getRskTrialStatus();
        if (StringUtil.equals(riskTrialStatus,BussinessDictCodeListRds.指标状态_禁止.getValue())){
            riskCommonOutputDto.setRskStatus(DictCodeListRsk.试算结果_不通过.getValue());
            if (StringUtil.equals(riskCommonInputDto.getIndicatorControl(),DictCodeListRsk.风险控制类别_硬控.getValue())){
                riskCommonOutputDto.setErrorMsg("指标风险试算不通过，不允许提交！");
                // 存在硬控消息只提示其中一个，返回结果
                return;
            }else {
                // 软控消息拼接，并且一种风险类型只拼接一次
                confirmMsg.append("指标风险试算不通过，是否确认提交！");
            }

        }
        if (StringUtil.equals(riskTrialStatus,BussinessDictCodeListRds.指标状态_审批.getValue())){
            if (StringUtil.equals(riskCommonInputDto.getIndicatorControl(),DictCodeListRsk.风险控制类别_硬控.getValue())){
                riskCommonOutputDto.setRiskApproval(DictCodeList.是否_是.getValue());
                riskCommonOutputDto.setRskStatus(DictCodeListRsk.试算结果_风险审批.getValue());
                confirmMsg.append("风险审批，是否进入下一审批流？");
            }else {
                // 软控消息拼接，并且一种风险类型只拼接一次
                confirmMsg.append("指标审批，是否确认提交!");
            }

        }
        // 授权校验
        List<RiskAuthorizeDto> riskAuthorizeDtoList = riskCommonOutputDto.getAuthorizeList();
        if (!StringUtil.isEmptyList(riskAuthorizeDtoList)){
            for (RiskAuthorizeDto riskAuthorizeDto : riskAuthorizeDtoList){
                if (StringUtil.equals(riskAuthorizeDto.getRskStatus(),DictCodeListRsk.试算结果_不通过.getValue())){
                    riskCommonOutputDto.setRskStatus(DictCodeListRsk.试算结果_不通过.getValue());
                    if (StringUtil.equals(riskCommonInputDto.getAuthorizeControl(),DictCodeListRsk.风险控制类别_硬控.getValue())){
                        riskCommonOutputDto.setErrorMsg("发起人超授权，不允许提交！");
                        // 存在硬控消息只提示其中一个，返回结果
                        return;
                    }else {
                        // 软控消息拼接，并且一种风险类型只拼接一次
                        confirmMsg.append("发起人超授权，是否确认提交！");
                        break;
                    }
                }
            }
        }
        riskCommonOutputDto.setConfirmMsg(confirmMsg.toString());
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
        FinProductTypeConfig finProductTypeConfig = getRskAuthorizeType(riskCommonInputDto.getAssetType(),riskCommonInputDto.getFinprodId());
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

    private FinProductTypeConfig getRskAuthorizeType(String assetType, String assetId) {
        if (assetId != null && assetId.length() > 0) {
            FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
            finProductTypeKey.setFinprodId(assetId);
            finProductTypeKey.setBranch(0L);
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
            FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
            finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_授权分类.getValue());
            finProductTypeConfig.setInputValue1(finProductType.getType8());
            finProductTypeConfig.setInputValue2(finProductType.getType9());
            finProductTypeConfig.setInputValue3(finProductType.getType10());
            finProductTypeConfig.setInputValue4(finProductType.getType11());
            finProductTypeConfig.setInputValue5(finProductType.getType12());
            return restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",finProductTypeConfig, FinProductTypeConfig.class);
        } else if (com.joyintech.framework.util.StringUtil.isNotEmpty(assetType)) {
            FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
            finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_授权分类.getValue());
            finProductTypeConfig.setInputValue1(assetType);
            return restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType", finProductTypeConfig, FinProductTypeConfig.class);
        }
        return null;

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

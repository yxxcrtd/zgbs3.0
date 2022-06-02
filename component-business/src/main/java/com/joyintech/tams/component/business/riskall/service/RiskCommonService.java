package com.joyintech.tams.component.business.riskall.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.riskall.dto.*;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risk.dao.RiskNamListCommonMapper;
import com.joyintech.tams.invest.common.risk.dto.RiskNamListCommonDto;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.risk.service.RiskNameListCommonBatchService;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CreditLinesUpdate;
import com.joyintech.tams.invest.common.trade.CreditLinesUpdateImpl;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesOutputDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
            try {
                ClientData rsp = interfRiskRdsService.sendRiskRdsListData(interfRiskRdsDto, riskCommonInputDto.getRiskRdsDtoList(), null);
            } catch (Exception e) {
                logger.error("指标接口调用异常:{}",e.getMessage());
            }
        }
        // 判断授信是否控制
        // 入参不为空、授信控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险控制类别_不控.getValue())
            && !StringUtil.isEmptyList(riskCommonInputDto.getCreditInputList())){
            String occupyType = "";
            List<CreditLinesInputDto> list = new ArrayList<>();
            if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_预占.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_预占.getValue();
                list.addAll(riskCommonInputDto.getCreditInputList());
            }
            if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_实占.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_实占.getValue();
                list.addAll(riskCommonInputDto.getCreditInputList());
            }
            if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_预占释放.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_预占.getValue();
                list.addAll(riskCommonInputDto.getCreditInputList());
            }
            if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_实占释放.getValue())) {
                occupyType = DictCodeListRsk.授信占用类别_实占.getValue();
                list.addAll(riskCommonInputDto.getCreditInputList());
            }
            if (StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险占用类型_预占释放再预占.getValue())) {
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
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占释放再实占.getValue())) {
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
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占预占释放.getValue())) {
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
    }

    /**
     * 风险试算
     * @param riskCommonInputDto
     * @return RiskCommonOutputDto
     */
    protected RiskCommonOutputDto doRisk(RiskCommonInputDto riskCommonInputDto){
        RiskCommonOutputDto riskCommonOutputDto = new RiskCommonOutputDto();
        // 判断风险指标是否控制
        // 入参不为空、指标控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getIndicatorControl(), DictCodeListRsk.风险控制类别_不控.getValue())
            && !StringUtil.isEmptyList(riskCommonInputDto.getRiskRdsDtoList())){
            InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
            interfRiskRdsDto.setAssetType(riskCommonInputDto.getAssetType());
            interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_预占.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_预占释放再预占.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
                // 处理差分预占参数，接口支持程度待确认
                riskCommonInputDto.getRiskRdsDtoList().forEach(item -> item.setIsDiffOccupy(DictCodeList.是否_是.getValue()));
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占释放再实占.getValue())) {
                interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                // 处理差分预占参数，接口支持程度待确认
                riskCommonInputDto.getRiskRdsDtoList().forEach(item -> item.setIsDiffOccupy(DictCodeList.是否_是.getValue()));
            }

            try {
                ClientData rsp = interfRiskRdsService.sendRiskRdsListData(interfRiskRdsDto, riskCommonInputDto.getRiskRdsDtoList(), null);
                riskCommonOutputDto.setRskStatus(String.valueOf(rsp.getBody().get("rskStatus")));
                List<RiskTrialRetDto> riskTrialRetDto = JsonUtils.json2List(JsonUtils.object2Json(rsp.getBodyField("RSK_LIST")),RiskTrialRetDto.class);
                riskCommonOutputDto.setRiskTrialRetDto(riskTrialRetDto);
            } catch (Exception e) {
                logger.error("指标接口调用异常:{}",e.getMessage());
            }
        }
        // 判断授信是否控制
        // 入参不为空、授信控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getCreditControl(), DictCodeListRsk.风险控制类别_不控.getValue())
            && !StringUtil.isEmptyList(riskCommonInputDto.getCreditInputList())){
            List<CreditLinesInputDto> list = new ArrayList<>();
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_预占释放再预占.getValue())) {
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
            }
            if (StringUtil.equals(riskCommonInputDto.getIndicatorOccupy(), DictCodeListRsk.风险占用类型_实占释放再实占.getValue())) {
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
        // 判断授权是否控制
        // 入参不为空、授权控制类别为软控、硬控
        if (!StringUtil.equals(riskCommonInputDto.getAuthorizeControl(), DictCodeListRsk.风险控制类别_不控.getValue())){
            List<RiskAuthorizeDto> authorizeList = new ArrayList<>();
            RiskAuthorizeDto riskAuthorizeDto = new RiskAuthorizeDto();
            boolean authorizeLimit = commonBusinessService.是否超过交易员额度(riskCommonInputDto.getCounterId(),riskCommonInputDto.getPrinAmt(),riskCommonInputDto.getAssetType()
                    ,riskCommonInputDto.getAssetSecType(),riskCommonInputDto.getTradeMarket(),riskCommonInputDto.getPs());

            riskAuthorizeDto.setAuthorizeLimit(authorizeLimit);
            authorizeList.add(riskAuthorizeDto);
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

        return new RiskAuthorizeDto();
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
                            riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_不通过.getCodeName());
                            riskNameListResult.setListType(DictCodeListRsk.名单属性_黑名单.getCodeName());
                        }else {
                            riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_通过.getCodeName());
                            riskNameListResult.setListType(DictCodeListRsk.名单属性_白名单.getCodeName());
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
                            riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_不通过.getCodeName());
                            riskNameListResult.setListType(DictCodeListRsk.名单属性_黑名单.getCodeName());
                        }else {
                            riskNameListResult.setRskStatus(DictCodeListRsk.试算结果_通过.getCodeName());
                            riskNameListResult.setListType(DictCodeListRsk.名单属性_白名单.getCodeName());
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

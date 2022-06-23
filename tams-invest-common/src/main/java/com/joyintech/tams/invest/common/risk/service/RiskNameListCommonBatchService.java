package com.joyintech.tams.invest.common.risk.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.common.risk.dao.RiskNamListCommonMapper;
import com.joyintech.tams.invest.common.risk.dto.RiskNamListCommonDto;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;

/**
 * 名单控制批量试算
 */
@Service
public class RiskNameListCommonBatchService {

    private static final Logger logger = LoggerFactory.getLogger(RiskNameListCommonBatchService.class);

    @Autowired
    private RiskNamListCommonMapper riskNamListCommonMapper;

    public List<RiskNameListDto> 名单批量试算(List<RiskNameListDto> list){
            List<RiskNamListCommonDto> dtoList = new ArrayList<RiskNamListCommonDto>();
            Map<String,Object> resultMap = new HashMap<>();
            //是否验证资产
            boolean isCheckAsset = Boolean.parseBoolean(CacheUtil.getSysParam("IS_CHECK_ASSET"));
            //是否验证机构
            boolean isCheckOrg = Boolean.parseBoolean(CacheUtil.getSysParam("IS_CHECK_ORG"));
            boolean assetFlag = true;boolean orgTrusteeFlag = true;
            boolean orgIssuerFlag = true;boolean orgManagerFlag = true;
            boolean orgFinancierFlag = true;boolean orgCounterIdFlag = true;
            boolean assetFlagBlack = true;boolean orgTrusteeFlagBlack = true;
            boolean orgIssuerFlagBlack = true;boolean orgManagerFlagBlack = true;
            boolean orgFinancierFlagBlack = true;boolean orgCounterIdFlagBlack = true;
            for(RiskNameListDto riskNameList : list){
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
                if(null!=dtoList&&!dtoList.isEmpty()){
                    for(RiskNamListCommonDto item: dtoList) {
                        try {
                            boolean 匹配 = true;
                            if (null != item && StringUtil.isNotEmpty(item.getListId())) {
                                Map<String, Object> nameListMap = new HashMap<>();
                                //1.判断资产是否匹配上
                                if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_资产.getValue())
                                        && StringUtil.isNotEmpty(item.getFinprodId())) {
                                    if (isCheckAsset) {
                                        nameListMap.put("listId", item.getListId());
                                        nameListMap.put("finprodId", item.getFinprodId());
                                        nameListMap.put("finprodName", item.getFinprodName());
                                        nameListMap.put("finprodAbbr", item.getFinprodAbbr());
                                        nameListMap.put("finprodMarketId", item.getFinprodMarketId());
                                        List<RiskNamListCommonDto> dtos = riskNamListCommonMapper.selectAssetCount(nameListMap);
                                        if (CollectionUtils.isNotEmpty(dtos)) {
                                            if (assetFlagBlack && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_黑名单.getValue())) {
                                                assetFlagBlack = false;
                                            }
                                        } else {
                                            if (assetFlag && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_白名单.getValue())) {
                                                assetFlag = false;
                                            }
                                        }
                                    }
                                } else {
                                    if (isCheckOrg) {
                                        nameListMap.put("listId", item.getListId());
                                        RiskNamListCommonDto riskNameListCommonDto = new RiskNamListCommonDto();
                                        //2.判断机构是否匹配上
                            /*if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_托管人.getValue())) {
                                if(StringUtil.isNotEmpty(item.getTrusteeId())){
                                    riskNamListCommonDto = riskNamListCommonMapper.selectCustomerInfo(item.getTrusteeId());
                                    匹配 = 机构是否匹配(riskNamListCommonDto,nameListMap);
                                    if(匹配){
                                        if(orgTrusteeFlagBlack&&StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_黑名单.getValue())){
                                            orgTrusteeFlagBlack = false;
                                        }
                                    }
                                    else {
                                        if(orgTrusteeFlag&&StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_白名单.getValue())){
                                            orgTrusteeFlag = false;
                                        }
                                    }
                                    continue;
                                }
                            }else */
                                        if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_发行人.getValue())) {
                                            if (StringUtil.isNotEmpty(item.getIssuer())) {
                                                riskNameListCommonDto = riskNamListCommonMapper.selectCustomerInfo(item.getIssuer());
                                                匹配 = 机构是否匹配(riskNameListCommonDto, nameListMap);
                                                if (匹配) {
                                                    if (orgIssuerFlagBlack && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_黑名单.getValue())) {
                                                        orgIssuerFlagBlack = false;
                                                    }
                                                } else {
                                                    if (orgIssuerFlag && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_白名单.getValue())) {
                                                        orgIssuerFlag = false;
                                                    }
                                                }
                                                continue;
                                            }
                                        } else if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_管理人.getValue())) {
                                            if (StringUtil.isNotEmpty(item.getManager())) {
                                                riskNameListCommonDto = riskNamListCommonMapper.selectCustomerInfo(item.getManager());
                                                匹配 = 机构是否匹配(riskNameListCommonDto, nameListMap);
                                                if (匹配) {
                                                    if (orgManagerFlagBlack && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_黑名单.getValue())) {
                                                        orgManagerFlagBlack = false;
                                                    }
                                                } else {
                                                    if (orgManagerFlag && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_白名单.getValue())) {
                                                        orgManagerFlag = false;
                                                    }
                                                }
                                                continue;
                                            }
                                        } else if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt2.名单主体明细类型_融资人.getValue())) {
                                            if (StringUtil.isNotEmpty(item.getFinancier())) {
                                                riskNameListCommonDto = riskNamListCommonMapper.selectCustomerInfo(item.getFinancier());
                                                匹配 = 机构是否匹配(riskNameListCommonDto, nameListMap);
                                                if (匹配) {
                                                    if (orgFinancierFlagBlack && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_黑名单.getValue())) {
                                                        orgFinancierFlagBlack = false;
                                                    }
                                                } else {
                                                    if (orgFinancierFlag && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_白名单.getValue())) {
                                                        orgFinancierFlag = false;
                                                    }
                                                }
                                                continue;
                                            }
                                        } else if (StringUtil.equals(item.getListDetailIdType(), DictCodeListExt3.名单主体明细类型_交易对手.getValue())) {
                                            if (param.containsKey("counterId") && StringUtil.isNotEmpty(param.get("counterId"))) {
                                                riskNameListCommonDto = riskNamListCommonMapper.selectCustomerInfo2(param.get("counterId").toString(), DictCodeListExt1.交易对手类型_市场机构.getValue());
                                                匹配 = 机构是否匹配(riskNameListCommonDto, nameListMap);
                                                if (匹配) {
                                                    if (orgCounterIdFlagBlack && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_黑名单.getValue())) {
                                                        orgCounterIdFlagBlack = false;
                                                    }
                                                } else {
                                                    if (orgCounterIdFlag && StringUtil.equals(item.getListType(), DictCodeListRsk.名单属性_白名单.getValue())) {
                                                        orgCounterIdFlag = false;
                                                    }
                                                }
                                                continue;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
                resultMap.put("isAssetMatch",assetFlag);
                resultMap.put("isAssetMatchBlack",assetFlagBlack);
                resultMap.put("isOrgMatch",orgTrusteeFlag&&orgIssuerFlag&&orgManagerFlag&&orgFinancierFlag&&orgCounterIdFlag);
                resultMap.put("isOrgMatchBlack",orgTrusteeFlagBlack&&orgIssuerFlagBlack&&orgManagerFlagBlack&&orgFinancierFlagBlack&&orgCounterIdFlagBlack);
                riskNameList.setErrorMap(resultMap);
            }
            return list;
        }

        private Boolean 机构是否匹配(RiskNamListCommonDto riskNameListCommonDto, Map<String, Object> nameListMap){
            if(null!=riskNameListCommonDto&&StringUtil.isNotEmpty(riskNameListCommonDto.getCustomerId())){
                nameListMap.put("finprodId", riskNameListCommonDto.getInsitituteCode());
                nameListMap.put("finprodName", riskNameListCommonDto.getCustomerName());
                nameListMap.put("finprodAbbr", riskNameListCommonDto.getCustomerName());
                List<RiskNamListCommonDto> dtos = riskNamListCommonMapper.selectAssetCount(nameListMap);
                if (CollectionUtils.isNotEmpty(dtos)) {
                    return true;
                }
            }
            return false;
        }


        public RiskNameListDto getRiskNameByFinprodId(String finprodid){
            RiskNameListDto dto = null;
            Map<String, Object> param = new HashMap<>();
            param.put("finprodId",finprodid);
            List<RiskNameListDto> nameList = this.riskNamListCommonMapper.getNameListByFinprodId(param);
            if(!StringUtil.isEmptyList(nameList)) {
                dto = nameList.get(0);
            }
            return dto;
        }
}

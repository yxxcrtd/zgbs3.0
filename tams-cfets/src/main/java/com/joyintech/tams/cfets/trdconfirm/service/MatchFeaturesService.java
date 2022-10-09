/*
 * 文件名：CfetsTrdConfirmService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：sjj
 * 修改时间：2021年11月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trdconfirm.service;

import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.cfets.trdconfirm.dao.MatchFeaturesMapper;
import com.joyintech.tams.cfets.trdconfirm.dto.BankSysParaSub;
import com.joyintech.tams.cfets.trdconfirm.dto.MatchBusType;
import com.joyintech.tams.cfets.trdconfirm.dto.MatchDetFeatures;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt4;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 银行间交易成交确认-匹配元素
 *
 * @author jiangpeng
 * @date 2022/9/27 9:38
 */
@Service("matchFeaturesService")
public class MatchFeaturesService extends BaseService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MatchFeaturesMapper matchFeaturesMapper;

    /**
     * 匹配元素查询
     * 匹配元素按钮点击后，查询匹配元素配置的详细情况
     *
     * @return java.util.List<com.joyintech.tams.cfets.trdconfirm.dto.matchBusType>
     * @author jiangpeng
     * @date 2022/9/27 9:35
     */
    public List<MatchBusType> queryMatchFeatures() {
        Map<String, Object> param = new HashMap<>();
        param.put("matchType01", DictCodeListExt4.匹配方式_自动匹配.getValue());
        param.put("matchType02", DictCodeListExt4.匹配方式_手工匹配.getValue());
        param.put("matchFeaturesList", Arrays.asList(DictCodeListExt4.匹配业务类型_一级申购.getValue(),
                DictCodeListExt4.匹配业务类型_现券买卖.getValue(),
                DictCodeListExt4.匹配业务类型_质押式回购首期.getValue()));
        // 是否隐藏自动匹配元素中的可选要素
        String isHideAutoMatch = CacheUtil.getSysParam("isHideAutoMatch");
        param.put("isHideAutoMatch", StringUtil.isNotEmpty(isHideAutoMatch) ? isHideAutoMatch : DictCodeList.是否_是.getValue());

        return matchFeaturesMapper.queryMatchFeatures(param);
    }

    /**
     * 匹配元素保存
     *
     * @param matchBusTypes 匹配元素
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/9/27 10:37
     */
    public AjaxResult saveMatchFeatures(List<MatchBusType> matchBusTypes) {
        // 判空
        if (CollectionUtils.isEmpty(matchBusTypes)) {
            return new AjaxResult("匹配元素为空,不可保存!");
        }
        Map<String, BankSysParaSub> bankSysParaSubMap = new HashMap<>();
        for (MatchBusType matchBusType : matchBusTypes) {
            // 判空
            if (CollectionUtils.isEmpty(matchBusType.getDetails())
                    || StringUtil.isEmpty(matchBusType.getCode())
                    || StringUtil.isEmpty(matchBusType.getName())
                    || StringUtil.isEmpty(matchBusType.getType())) {
                logger.error("匹配元素中存在空值:{}", JsonUtils.object2Json(matchBusType));
                continue;
            }
            // 前端转后台
            for (MatchDetFeatures feature : matchBusType.getDetails()) {
                BankSysParaSub bankSysParaSub;
                if (bankSysParaSubMap.get(matchBusType.getCode() + feature.getCode()) != null) {
                    bankSysParaSub = bankSysParaSubMap.get(matchBusType.getCode() + feature.getCode());
                } else {
                    bankSysParaSub = new BankSysParaSub();
                    bankSysParaSub.setParaCode(matchBusType.getCode());
                    bankSysParaSub.setSubParaCode(feature.getCode());
                }
                if (StringUtil.isEmpty(feature.getType())) {
                    logger.error("匹配元素明细中存在空值:{}", JsonUtils.object2Json(feature));
                    continue;
                }
                if (StringUtil.equals(DictCodeListExt4.匹配方式_自动匹配.getValue(), matchBusType.getType())) {
                    bankSysParaSub.setParamValue1(feature.getType());
                }
                if (StringUtil.equals(DictCodeListExt4.匹配方式_手工匹配.getValue(), matchBusType.getType())) {
                    bankSysParaSub.setParamValue2(feature.getType());
                }
                bankSysParaSubMap.put(matchBusType.getCode() + feature.getCode(), bankSysParaSub);
            }
        }
        // map 转list
        List<BankSysParaSub> updateList = new ArrayList<>(bankSysParaSubMap.values());
        matchFeaturesMapper.updateMatchFeatures(updateList);
        return new AjaxResult();
    }

    /**
     * 自动匹配元素查询
     *
     * @return java.util.List<com.joyintech.tams.cfets.trdconfirm.dto.matchBusType>
     * @author jiangpeng
     * @date 2022/9/27 9:35
     */
    public Map<String, MatchDetFeatures> queryAutoMatchFeatures(String businessType) {
        Map<String, Object> param = new HashMap<>();
        param.put("matchType01", DictCodeListExt4.匹配方式_自动匹配.getValue());
        param.put("matchType02", DictCodeListExt4.匹配方式_手工匹配.getValue());
        if (StringUtil.equals(businessType, "BID")) {
            param.put("matchFeaturesList", Collections.singletonList(DictCodeListExt4.匹配业务类型_一级申购.getValue()));
        }
        if (StringUtil.equals(businessType, "BOND")) {
            param.put("matchFeaturesList", Collections.singletonList(DictCodeListExt4.匹配业务类型_现券买卖.getValue()));
        }
        if (StringUtil.equals(businessType, "REPO")) {
            param.put("matchFeaturesList", Collections.singletonList(DictCodeListExt4.匹配业务类型_质押式回购首期.getValue()));
        }
        // 是否隐藏自动匹配元素中的可选要素
        param.put("isHideAutoMatch", DictCodeList.是否_是.getValue());

        List<MatchBusType> list = matchFeaturesMapper.queryAutoMatchFeatures(param);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0).getDetails().stream()
                .collect(Collectors.toMap(MatchDetFeatures::getCode, item -> item));
    }

    /**
     * 手工匹配元素查询
     *
     * @return java.util.List<com.joyintech.tams.cfets.trdconfirm.dto.matchBusType>
     * @author jiangpeng
     * @date 2022/9/27 9:35
     */
    public Map<String, MatchDetFeatures> queryManualMatchFeatures(String businessType) {
        Map<String, Object> param = new HashMap<>();
        param.put("matchType01", DictCodeListExt4.匹配方式_自动匹配.getValue());
        param.put("matchType02", DictCodeListExt4.匹配方式_手工匹配.getValue());
        if (StringUtil.equals(businessType, "BID")) {
            param.put("matchFeaturesList", Collections.singletonList(DictCodeListExt4.匹配业务类型_一级申购.getValue()));
        }
        if (StringUtil.equals(businessType, "BOND")) {
            param.put("matchFeaturesList", Collections.singletonList(DictCodeListExt4.匹配业务类型_现券买卖.getValue()));
        }
        if (StringUtil.equals(businessType, "REPO")) {
            param.put("matchFeaturesList", Collections.singletonList(DictCodeListExt4.匹配业务类型_质押式回购首期.getValue()));
        }
        List<MatchBusType> list = matchFeaturesMapper.queryManualMatchFeatures(param);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0).getDetails().stream()
                .collect(Collectors.toMap(MatchDetFeatures::getCode, item -> item));
    }
}

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
import com.joyintech.tams.cfets.trdconfirm.dao.MatchFeaturesMapper;
import com.joyintech.tams.cfets.trdconfirm.dto.BankSysParaSub;
import com.joyintech.tams.cfets.trdconfirm.dto.MatchBusType;
import com.joyintech.tams.cfets.trdconfirm.dto.MatchDetFeatures;
import com.joyintech.tams.constant.DictCodeListExt4;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        param.put("matchFeatures01", DictCodeListExt4.匹配业务类型_一级申购.getValue());
        param.put("matchFeatures02", DictCodeListExt4.匹配业务类型_现券买卖.getValue());
        param.put("matchFeatures03", DictCodeListExt4.匹配业务类型_质押式回购首期.getValue());

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
                    bankSysParaSub.setParamValue1(feature.getType());
                }
                bankSysParaSubMap.put(matchBusType.getCode() + feature.getCode(), bankSysParaSub);
            }
        }
        // map 转list
        List<BankSysParaSub> updateList = new ArrayList<>(bankSysParaSubMap.values());
        matchFeaturesMapper.updateMatchFeatures(updateList);
        return new AjaxResult();
    }
}

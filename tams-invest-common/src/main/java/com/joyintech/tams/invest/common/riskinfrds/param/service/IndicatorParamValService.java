/*
 * 文件名：IndicatorParamValService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年8月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.riskinfrds.param.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.joyintech.redis.common.entity.Value;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.constant.RedisConstant;
import com.joyintech.redis.core.base.PipeReadService;
import com.joyintech.redis.core.base.PipeWriteService;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;

/**
 * 指标参数值读写服务
 * 
 * @author zhangrui1
 * @version 1.0 2019年8月29日
 * @see IndicatorParamValService
 * @since 1.0
 */
@Service
public class IndicatorParamValService {

    /**
     * 指标参数值读信息服务
     */
    @Autowired
    private PipeReadService<String, String, IValue<Object>> valReadSrv;

    /**
     * 指标参数值写信息服务
     */
    @Autowired
    private PipeWriteService<String, String, IValue<Object>> valWriteSrv;

    /**
     * 取得 所有指标参数值
     * 
     * @param paraValList 指标参数查询条件
     * @return 所有指标参数值 {@link Map}<参数代码,参数值>
     * @see
     * @since 1.0
     */
    public Map<String, Map<String, IValue<Object>>> getParamVal(Map<String, Map<String, IValue<Object>>> paraValList) {
        if (CollectionUtils.isEmpty(paraValList)) {
            return new HashMap<>();
        }
        // 从Redis取得参数值
        Map<String, Map<String, IValue<Object>>> res=checkBatchKey(paraValList,(list)->{
            return valReadSrv.readList(
                    changeKey(list, true));
        });
        if(CollectionUtils.isEmpty(res)){
            res = valReadSrv.readList(
                    changeKey(paraValList, true));
        }
        return changeKey(res, false);
    }
    /**
     * 删除 删除指标参数值
     *
     * @param paraValMap 指标参数值列表
     * @see
     * @since 1.0
     */
    public void delParamVal(Map<String, Map<String, IValue<Object>>> paraValMap) {
        if (CollectionUtils.isEmpty(paraValMap)) {
            return;
        }
        // 删除Redis参数值
        valWriteSrv.deleteList(changeKey(paraValMap, true));
    }


    /**
     * 更新 所有指标参数值
     * 
     * @param paraValMap 指标参数值列表
     * @see
     * @since 1.0
     */
    public void setParamVal(Map<String, Map<String, IValue<Object>>> paraValMap) {
        if (CollectionUtils.isEmpty(paraValMap)) {
            return;
        }
        // 更新Redis参数值
        valWriteSrv.writeList(changeKey(paraValMap, true));
    }

    /**
     * 检查有无批量redis识别域的结构类型，比如押品参数等
     * @param paraValList
     * @param function
     * 备注：把原识别域用指定分隔符重新切割成List，里面每个进行查询参数值，然后再重新拼接构造成一条
     * @return
     */
    private Map<String, Map<String, IValue<Object>>> checkBatchKey(Map<String, Map<String, IValue<Object>>> paraValList,
                                                                   Function< Map<String, Map<String, IValue<Object>>>, Map<String, Map<String, IValue<Object>>>> function) {
        Map<String, Map<String, IValue<Object>>> paramList = new HashMap<>();
        paraValList.entrySet().forEach(en -> {
            String key = en.getKey();
            Map<String, IValue<Object>> val = en.getValue();
            for(String k:val.keySet()){
                //redis识别域
                if(StringUtil.contains(k, RiskUtil.UP_LINE)){
                    Map<String, IValue<Object>> valTotal=new HashMap<>();
                    StringBuilder sb=new StringBuilder();
                    for(String lk:k.split(RiskUtil.UP_LINE_SPLIT)){
                        Map<String, IValue<Object>> valSub=new HashMap<>();
                        IValue<Object> rValue = val.get(k);
                        valSub.put(lk,rValue);
                        paramList.put(key, valSub);
                        Map<String, Map<String, IValue<Object>>> retSub=function.apply(paramList);
                        IValue<Object> rVal=retSub.get(RedisKeyEnum.指标参数值.key()+key).get(lk);
                        String values=StringUtil.isNotEmpty(rVal)?(String)rVal.value():null;
                        if(StringUtil.isNotEmpty(values))
                            sb.append(values).append(",");
                    };
                    if(StringUtil.isNotEmpty(sb)) {
                        IValue<Object> rValue = new Value<>(str->str);
                        rValue.value(sb.toString());
                        valTotal.put(k, rValue);
                        paramList.put(key,valTotal);
                    }
                }
            };
        });
        return paramList;
    }

    private Map<String, Map<String, IValue<Object>>> changeKey(Map<String, Map<String, IValue<Object>>> paraValList,
                                                                   boolean isForRedis) {
        Map<String, Map<String, IValue<Object>>> paramList = new HashMap<>();
        String header = RedisKeyEnum.指标参数值.key();
        paraValList.entrySet().forEach(en -> {
            String key = en.getKey();
            if (isForRedis) {
                key = header + key;
            }
            else {
                key = StringUtil.remove(key, header);
            }
            Map<String, IValue<Object>> val = en.getValue();
            paramList.put(key, val);
        });
        return paramList;
    }

    private List<String> changeKey(List<String> keys, boolean isForRedis) {
        String header = RedisKeyEnum.指标参数值.key();
        List<String> resList = new ArrayList<>();
        keys.forEach(k -> {
            if (isForRedis) {
                resList.add(header + k);
            }
            else {
                resList.add(StringUtil.remove(k, header));
            }
        });
        return resList;
    }

    public Map<String, Map<String, IValue<Object>>> readAll(List<String> keyList) {
        return changeKey(valReadSrv.readAll(changeKey(keyList, true)), false);
    }

    public List<String> allKeys(String key) {
        return changeKey(valReadSrv.allKeys(RedisConstant.STAR + RedisKeyEnum.指标参数值.key() + key),
            false);
    }

    public List<String> allHKeys(String key, String field) {
        String header = RedisKeyEnum.指标参数值.key();
        return valReadSrv.allHKeys(header + key, RedisConstant.STAR + field + RedisConstant.STAR);
    }
}

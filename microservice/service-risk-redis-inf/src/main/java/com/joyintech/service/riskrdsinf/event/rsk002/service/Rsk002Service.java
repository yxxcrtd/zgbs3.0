/*
 * 文件名：Rsk002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年8月5日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.riskrdsinf.event.rsk002.service;

import java.util.*;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.service.riskrdsinf.common.service.OccupyReleasService;
import com.joyintech.service.riskrdsinf.event.rsk005.service.Rsk005Service;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IProdInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.service.riskrdsinf.cache.InterfCacheUtil;
import com.joyintech.service.riskrdsinf.common.service.EventBaseService;
import com.joyintech.service.riskrdsinf.core.annotation.INF;
import com.joyintech.service.riskrdsinf.core.client.ClientData;
import com.joyintech.service.riskrdsinf.core.exception.InterfException;
import com.joyintech.service.riskrdsinf.event.rsk002.entity.Rsk002InEntity;
import com.joyintech.tams.common.base.entity.RskIndicatorParaCalcdetail;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.calendar.entity.ICalendar;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEvent;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicator;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOccupyRelParam;
import com.joyintech.tams.invest.common.riskinfrds.param.entity.IParam;
import com.joyintech.tams.invest.common.riskinfrds.prod.entity.IPtlIndicator;

/**
 * 交易完成接口处理详细
 * 
 * @author zhangrui1
 * @version 1.0 2019年8月5日
 * @see Rsk002Service
 * @since 1.0
 */
@Service
@INF("RSK002")
public class Rsk002Service extends OccupyReleasService<Rsk002InEntity> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Map<String, Rsk002InEntity>> handle(ClientData request,
                                                              ClientData response)
        throws InterfException {
        // add by zhangrui 有预占的实占试算 start
        IOccupyRelParam ocpRelParam = null;
        // add by zhangrui 有预占的实占试算 end
        // 做过预占的风险试算直接通过
        if (isPreOcp(request)) {
            // 预占/实占编号
            String occupyCode = (String)request.getBodyField("preOccupyCode");
            // 预占实占参数信息
            ocpRelParam = getOccupy(occupyCode, PRE_OCCUPY);
            if (ocpRelParam != null) {
                setAllOcpVal(ocpRelParam);
            }
            // add by zhangrui 有预占的实占试算 end
    	}
        // add by zhangrui1 差分预占 start
        else if (isDiffOcp(request.getBody())) {
            // 预占/实占编号
            String occupyCode = (String)request.getBodyField(ENTRUST_CODE);
            // 预占实占参数信息
            ocpRelParam = getOccupy(occupyCode, PRE_OCCUPY);
        }
        else if (StringUtil.isNotEmpty(request.getBodyField(EVENT_ID))) {
            // 预占/实占编号
            String occupyCode = (String)request.getBodyField(EVENT_ID);
            // 预占实占参数信息
            ocpRelParam = getOccupy(occupyCode, StringUtil.equals(request.getBodyField(EVENT_TYPE),
                "0") ? PRE_OCCUPY : REL_OCCUPY);
        }
        // add by zhangrui1 差分预占 end
        // add by zhangrui 有预占的实占试算 start
        final IOccupyRelParam preOcpParam = ocpRelParam;
        // add by zhangrui 有预占的实占试算 end
    	// 指标参数计算明细
    	List<RskIndicatorParaCalcdetail> details = new ArrayList<>();
        // 取得报文体
        Map<String, Object> mapData = new HashMap<>();
        mapData.putAll(request.getBody());
        String portfolioCode = (String)mapData.get(PORTFOLIOCODE);
        // 备份组合代码
        mapData.put(PROD_ID, portfolioCode);
        // 取得产品信息
        IProdInfo prodInfo = getProdInfo(portfolioCode);
        // 是否启动风险
        String riskEnabled = null;
        if (prodInfo != null) {
            // 是否启动风险
            riskEnabled = prodInfo.getRiskEnabled();
        }
        else {
        	throw new InterfException("产品代码【" + portfolioCode + "】对应的信息不存在！");
        }
        // 未启动风险则不进行预占
        if (CommonUtil.isType(riskEnabled, BussinessDictCodeListRds.是否_否)) {
            throw new InterfException("产品(" + portfolioCode + ")未启动风险!");
        }

        // 交易方向数据字典转换
        changePsValue(mapData);
        // 取得资产基本信息
        Map<String, Object> assetInfo = getAssetInfo(mapData);
        if (!isAssetNoNeeds()) {
            // 若资产基本信息不为空
            if (!CollectionUtils.isEmpty(assetInfo)) {
                mapData.putAll(assetInfo);
            }
            else if (StringUtil.isNotEmpty(mapData.get(ASSETCODE))) {
                throw new InterfException(mapData.get(ASSETCODE) + "的资产信息不存在");
            }
            else if (StringUtil.isNotEmpty(mapData.get(MARKET_CODE))) {
                throw new InterfException(mapData.get(MARKET_CODE) + "的资产信息不存在");
            }
        }
        // 取得基金估值行情
        if (isCalcAmount()) {
        	calcAmount(mapData, ()->getUnitPrice((String) mapData.get(ASSETCODE)));
        }
        // 取得日历信息
        ICalendar calendar = getCalendar((String)mapData.get(CALENDAR_ID));
        // 匹配风险事件
        List<IEvent> eveList = getEvent(mapData, calendar);
        // 若风险事件不存在，则试算通过
        if (StringUtil.isEmptyList(eveList)) {
            // 试算通过
            response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
            response.setBodyField(RSK_LIST, new ArrayList<>());
            getLogger().info(LOG_INFO_EVENT);
            return null;
        }
        // 从风险事件列表中取出相关的参数列表
        Set<String> paraIdList = getParamIdList(eveList);
        // 取得组合关联指标信息
        IPtlIndicator ptlInd = getPtlIndicator(mapData);
        // 若无 组合关联指标信息，则试算通过
        if (ptlInd == null) {
            response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
            response.setBodyField(RSK_LIST, new ArrayList<>());
            getLogger().info(LOG_INFO_PASS);
            return null;
        }
        // 取得所有指标代码
        Set<String> indIdList = getIndIdList(ptlInd);
        // 若组合未关联指标信息，则试算通过
        if (CollectionUtils.isEmpty(indIdList)) {
            getLogger().info(LOG_INFO_PASS);
            response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
            response.setBodyField(RSK_LIST, new ArrayList<>());
            return null;
        }
        // 取得所有的指标信息
        Map<String, IIndicator> indMap = getIndicator(indIdList);
        // 若指标信息不存在，则抛出异常
        if (CollectionUtils.isEmpty(indMap)) {
            throw new InterfException("指标信息不存在");
        }
        // 判断监督时点，若不满足则指标不计算,判断是否剔除资产，若满足则指标不计算
        checkTpAndIndivAd(indMap, mapData, ptlInd);
        // 当指标都被剔除时，直接试算通过
        if (CollectionUtils.isEmpty(ptlInd.getPtlIndicator())) {
            response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
            response.setBodyField(RSK_LIST, new ArrayList<>());
            getLogger().info("无需要计算的指标，试算通过");
            return null;
        }
        // 风险事件相关参数与指标相关参数糅合去重(并集)
        Map<String, Set<String>> ptlIndMap = ptlInd.getPtlIndicator();
        unionParamId(indMap, paraIdList, ptlIndMap);
        //wangyang1 风险二级法人改造 start
        if (MultilevelDataAuthorityUtil.isEnabled() && StringUtil.isNotEmpty(mapData.get("instCode"))) {
            String instCode = mapData.get("instCode").toString();
            Set<String> paraIdListChd = new HashSet<>();
            paraIdList.forEach(item->{
                if (item.indexOf(BussinessDictCodeListRds.指标参数实现类型_固定值.getValue())!=-1) {
                    paraIdListChd.add(item.replace(BussinessDictCodeListRds.指标参数实现类型_固定值.getValue(),instCode));
                }
            });
            paraIdList.addAll(paraIdListChd);
        }
        //wangyang1 风险二级法人改造 end
        // 取得所有指标参数信息
        Map<String, IParam> paramMap = getParam(paraIdList);
        // 若指标参数不存在则抛出异常
        if (CollectionUtils.isEmpty(paramMap)) {
            throw new InterfException("指标参数信息不存在");
        }
        // 构建组合与指标参数索引
        Map<String, Map<String, String>> ptlParamMap = getPtlParamMap(ptlInd, indMap);

        Map<String, Map<String, IValue<Object>>> paraList = createCondition(paramMap, mapData, ptlParamMap);
        log("涉及的指标参数有：【{}】", paraList.entrySet(), en->en != null ? en.getKey() : "");
        // 输出.head.返回备注信息
        setRemark(response,"涉及的指标参数有：", paraList.entrySet(), en->en != null ? en.getKey() : "",paramMap);
        // 预占/实占编号
        String eventId = (String)request.getBodyField(EVENT_ID);
        // 预占实占参数信息
        IOccupyRelParam ocpParam = getOccupy(eventId, StringUtil.equals(request.getBodyField(EVENT_TYPE),"0") ? PRE_OCCUPY : REL_OCCUPY);
        // 差分试算，编号相同，有相同编号占用的情况下，在释放的基础上进行试算 start
        Map<String, Map<String, IValue<Object>>> oldParaValMap = new HashMap<>();
        // 若占用（预占、实占）信息存在则表示该指令或交易非首次提交，需要在释放原先占用的基础上进行试算、但释放结果不能写redis
        if (ocpParam != null) {
            getLogger().info("该笔指令或交易已存在占用信息");
            // add by zhangrui1 差分预占 start
            if (!isDiffOcp(request.getBody())) {
                // add by zhangrui1 差分预占 end
                // 预占信息存在时，在释放原来占用的基础上进行试算
                request.getBody().put("revokeFlg", BussinessDictCodeListRds.是否_是.getValue());
                // 只走释放逻辑计算释放后的指标参数值，不写Redis
                request.getBody().put("writeRedis", BussinessDictCodeListRds.是否_否.getValue());
                super.handle(request,response);
                // 从线程变量中取得 释放后的指标参数值（临时值、未写入Redis）
                String occupyCode = (String)request.getBodyField(EVENT_ID);
                // 从线程变量中取得 释放后的指标参数值（临时值、未写入Redis）
                Object obj = getServiceInfo(occupyCode);
                if (obj != null){
                    oldParaValMap.putAll((Map<String, Map<String, IValue<Object>>>) obj);
                }
            }
        }
        // 执行事务处理（Redis操作）
        handleByLock(() -> {
            // add by zhangrui1 start
            calcMixAmount(mapData, false, false, null);
            // add by zhangrui1 end
            String uuid =  String.valueOf(System.currentTimeMillis());
            mapData.put("uuid", uuid);
            // 取得所有指标参数的值
            Map<String, Map<String, IValue<Object>>> paraValMap = getParamValSrv().getParamVal(paraList);
            // 若指标参数值未初始化到Redis则抛出异常
            if (CollectionUtils.isEmpty(paraValMap)) {
                //----update by zxjjpei 20200318 若存在固定值参数则让通过 start---
                if(checkSqlParamType(paramMap)){
                    throw new InterfException("指标参数值不存在");
                }else {
                    getLogger().info("没有SQL类型参数且指标参数值不存在，直接通过");
                    return null;
                }
                //----update by zxjjpei 20200318 若存在固定值参数则让通过 end---
            }
            // 将释放之后的指标参数值覆盖到从Redis中取得的
            mergeMap(paraValMap,oldParaValMap);
            // 计算参数值
            Map<String, Map<String, IValue<Object>>> newValMap = calcParamVal(eveList,
                paraValMap, getRelParam(mapData, preOcpParam), CALC_SCENE_OCP_DIFF, mapData,
                details);
            // 固定值参数
            Map<String, Object> map = getFixedParaVal(paramMap,mapData);
            // add by zhangrui1 同向变动特殊处理 start
            Map<String, IIndicator> leftIndMap = getIndTrendCalcSrc().execute(indMap, paramMap,
                getIndTrendCalcSrc().filterValMap(new HashMap<>(), paraValMap), eveList, mapData,
                (newIndMap, indValMap, newIndValMap) -> {
                    IPtlIndicator newPtlInd = getIndTrendCalcSrc().newPtlInd(ptlInd, newIndMap);
                    // 指标参数原值
                    Map<String, Map<String, Object>> pValMap = getIndTrendCalcSrc().parseParaVal(
                        ptlParamMap, indValMap);
                    // 加入固定值
                    unionParaVal(pValMap, map);
                    // 指标计算原值
                    calcIndicator(newPtlInd, newIndMap, mapData, pValMap, true);
                    // 指标参数新值
                    pValMap = getIndTrendCalcSrc().parseParaVal(ptlParamMap, newIndValMap);
                    // 加入固定值
                    unionParaVal(pValMap, map);
                    // 指标计算原值
                    calcIndicator(newPtlInd, newIndMap, mapData, pValMap, false);
                });
            if (CollectionUtils.isEmpty(leftIndMap)) {
                return null;
            }
            IPtlIndicator newPtlInd = getIndTrendCalcSrc().newPtlInd(ptlInd, leftIndMap);
            // add by zhangrui1 同向变动特殊处理 end
            // ---zxjjpei---枚举型参数 走库型参数 20200312 start-----
            Map<String, Object> mapFbd = getForbidParaVal(paramMap,mapData);
            long startTime = System.currentTimeMillis();
            Map<String, Object> mapDbCal =getDataBaseParaVal(eveList,ptlParamMap,paramMap,mapData);
            long endTime = System.currentTimeMillis();
            getLogger().info("获取走数据库的方法参数消耗的时间：{}", (endTime - startTime));
            // ---zxjjpei---枚举型参数 走库型参数 20200312 end-----
            // 指标参数原值
            Map<String, Map<String, Object>> pValMap = parseParaVal(ptlParamMap, paraValMap,paramMap);
            // 加入固定值
            unionParaVal(pValMap, map);
            // --add by jjpei 20200312 加入走库参数 start---
            unionParaVal(pValMap, mapDbCal);
            // --add by jjpei 20200312 加入走库参数 end---
            //----add by jjpei 20200312 把固定值与走库加上画面的这一笔进行计算 start-------
            calcFixParamVal(eveList,map);
            calcFixParamVal(eveList,mapDbCal);
            //----add by jjpei 20200312 把固定值与走库加上画面的这一笔进行计算 end-------
            // 指标计算原值
            // add by zhangrui1 同向变动特殊处理 start
            /**
            calcIndicator(ptlInd, indMap, mapData, pValMap, true);
            */
            calcIndicator(newPtlInd, indMap, mapData, pValMap, true);
            // add by zhangrui1 同向变动特殊处理 end
            // 指标参数新值
            pValMap = parseParaVal(ptlParamMap, calcValWithoutEvt(getParamIdList(eveList), eveList, newValMap),paramMap);
            // 加入固定值
            unionParaVal(pValMap, map);
            // ---zxjjpei---加入枚举型参数值 走库型参数 20200312 start-----
            unionParaVal(pValMap,mapFbd);
            unionParaVal(pValMap,mapDbCal);
            // ---zxjjpei---加入枚举型参数值 走库型参数 20200312 end-----
            // 指标计算新值
            // add by zhangrui1 同向变动特殊处理 start
            /**
            calcIndicator(ptlInd, indMap, mapData, pValMap, false);
            */
            calcIndicator(newPtlInd, indMap, mapData, pValMap, false);
            // add by zhangrui1 同向变动特殊处理 end
            return null;
        });
        indMap.entrySet().forEach(en -> checkIndTrend(en.getValue()));
        // 处理返回结果
        writeResponse(response, indMap, paramMap);
        // add by zhangrui1 试算返回值增加资产名称 start
        handleResponse(response, assetInfo, 0);
        // add by zhangrui1 试算返回值增加资产名称 end
        return null;
    }

    @Override
    protected void saveData(Map<String, Object> mapDatas, boolean isApprove, String user) throws InterfException {

    }

    /**
     * 是否做过预占
     * 
     * @param request 请求报文
     * @return true:做过预占
     */
    protected boolean isPreOcp(ClientData request){
        // 预占编号
        String preCode = (String)request.getBodyField("preOccupyCode");
        // 预占信息存在
        return getOccupy(preCode, PRE_OCCUPY) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkWithCond(Rsk002InEntity obj, ClientData response) {
    	String ps = obj.getPs();
    	String infAssetNoNeedPs = InterfCacheUtil.getSysParam(INF_ASSET_NONEED_PS);
    	if (StringUtil.contains(infAssetNoNeedPs, ps)) {
    		return false;
    	}
        if (StringUtil.isEmpty(obj.getAssetCode()) && (StringUtil.isEmpty(obj.getMarketCode()) || StringUtil.isEmpty(obj.getTradePlace()))
        		&& StringUtil.isEmpty(obj.getAssetType())) {
            doException(response, "资产大类、资产代码、以及市场代码和交易场所不能同时为空", null);
            return true;
        }
        return false;
    }

}

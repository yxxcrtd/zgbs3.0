/*
 * 文件名：Rsk011Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.1.2
 * 修改人：dengyu
 * 修改时间：2020年3月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.1.2
 */

package com.joyintech.service.riskrdsinf.event.rsk011.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.tams.constant.DictCodeList;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.service.riskrdsinf.cache.InterfCacheUtil;
import com.joyintech.service.riskrdsinf.common.service.EventBaseService;
import com.joyintech.service.riskrdsinf.core.annotation.INF;
import com.joyintech.service.riskrdsinf.core.client.ClientData;
import com.joyintech.service.riskrdsinf.core.constant.InfColumns;
import com.joyintech.service.riskrdsinf.core.exception.InterfException;
import com.joyintech.service.riskrdsinf.event.rsk002.entity.Rsk002InEntity;
import com.joyintech.tams.common.base.entity.RskIndicatorParaCalcdetail;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.riskinfrds.base.service.IndicatorTrendCalcService;
import com.joyintech.tams.invest.common.riskinfrds.calendar.entity.ICalendar;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEvent;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicator;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicatorTrend;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IProdInfo;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOccupyRelParam;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.OccupyRelParam;
import com.joyintech.tams.invest.common.riskinfrds.param.entity.IParam;
import com.joyintech.tams.invest.common.riskinfrds.prod.entity.IPtlIndicator;

/**
 * 风险批量试算服务接口处理
 * 
 * @author dengyu
 * @version 1.0 2020年3月23日
 * @see Rsk011Service
 * @since 1.0
 */
@Service
@INF("RSK011")
public class Rsk011Service extends EventBaseService<Rsk002InEntity> {

    /**
     * 试算列表
     */
    public static final String RSK_CALC_LIST = "rskCalcList";

    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<Map> getList(Map<String, Object> dataMap)
        throws IOException {
        Object obj = dataMap.get(RSK_CALC_LIST);
        if (obj instanceof List) {
            return (List<Map>)obj;
        }
        if (obj instanceof String) {
            String listStr = (String)obj;
            return JsonUtils.json2List(listStr, Map.class);
        }
        return new ArrayList<>();
    }
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected Map<String, Map<String, Rsk002InEntity>> handle(ClientData request,  ClientData response)
        throws InterfException {

    	// 指标参数计算明细
     	List<RskIndicatorParaCalcdetail> details = new ArrayList<>();
        //组合，指标，指标值
     	// modify by zhangrui1 start
     	//Map<String, Map<String, Map<String, IValue<Object>>>> cachedMap =new HashMap<>();
     	Map<String, Map<String, IValue<Object>>> localCache = new HashMap<>();
     	// modify by zhangrui1 end
        
        // add by zhangrui1 批量试算时暂存指标趋势信息 start
        List<IIndicatorTrend> indTrendList = new ArrayList<>();
        List<IOccupyRelParam> ocpList = new ArrayList<>();
        int index = 0;
        // add by zhangrui1 批量试算时暂存指标趋势信息 end
        Map<String, Object> body = request.getBody();
        // 试算列表
        List<Map> mapList = new ArrayList<>();
        try {
            mapList = getList(body);
        }
        catch (IOException e) {
            throw new InterfException(e);
        }
        HashMap <String ,IProdInfo> prodInfoCache =new HashMap<String ,IProdInfo>();
        
        initIProdInfo(prodInfoCache,mapList);
        
        List< ClientData> groupMap = new ArrayList<>();

        // 选出每组正反同步委托/交易相同的参数并存到缓存
        if (isSync(body)) pickSameParam(mapList, body);
        // add by zhangrui 有预占的实占试算 start
        Map<String, IOccupyRelParam> ocpRelParamMap = new HashMap<>();
        List<String> preList = new ArrayList<>();
        mapList.forEach(mapData -> {
            if (isPreOcp(mapData)) {
                preList.add(
                    (String)mapData.get("preOccupyCode") + GlobalConstant.COLON + PRE_OCCUPY);
            }
            // add by zhangrui1 差分预占 start
            else if (isDiffOcp(mapData)) {
                // 预占/实占编号
                String occupyCode = (String)mapData.get(ENTRUST_CODE);
                preList.add(occupyCode + GlobalConstant.COLON + PRE_OCCUPY);
            }
            else if (StringUtil.isNotEmpty(mapData.get(EVENT_ID))) {
                // 预占/实占编号
                String occupyCode = (String)mapData.get(EVENT_ID);
                preList.add(occupyCode + GlobalConstant.COLON + PRE_OCCUPY);
            }
            // add by zhangrui1 差分预占 end
        });
        // 做过预占的风险试算直接通过
        if (!StringUtil.isEmptyList(preList)) {
            // 预占实占参数信息
            ocpRelParamMap.putAll(
                getOcpService().getMapFromRedis(RedisKeyEnum.预占实占信息.key(), preList));
        }
        // add by zhangrui 有预占的实占试算 end
        // add by zhangrui1 增加groupId区分试算结果 start
        int groupId = 0;
        Map<Object, Integer> innerGroup = new HashMap<>();
        // add by zhangrui1 增加groupId区分试算结果 end
        for (Map mapData : mapList) {
        	// 添加日期元素 王贵奇 20200512
        	mapData.put(InfColumns.COM_SYSDATE.fieldName(), body.get(InfColumns.COM_SYSDATE.fieldName()));
            ClientData rsp = new ClientData();
            ClientData res = new ClientData();
            // add by zhangrui1 start
            res.setHead(request.getHead());
            res.setBody(mapData);
            exchangeCcy(res);
            // add by zhangrui1 end
            groupMap.add(rsp);
             // 取得产品信息
            String portfolioCode = (String)mapData.get(PORTFOLIOCODE);
            mapData.put(PROD_ID, portfolioCode);
          IProdInfo prodInfo=prodInfoCache.get(portfolioCode);
          // 是否启动风险
          String riskEnabled = null;
          if (prodInfo != null) {
              riskEnabled = prodInfo.getRiskEnabled();
          }else {
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
            // add by zhangrui1 差分预占 start
            Object occupyCodeStr = mapData.get("preOccupyCode");
            if (isDiffOcp(mapData)) {
                occupyCodeStr = mapData.get(ENTRUST_CODE);
            }
            // add by zhangrui1 差分预占 end
            // add by zhangrui 有预占的实占试算 start
            IOccupyRelParam preOcpParam = ocpRelParamMap.get(
                occupyCodeStr + GlobalConstant.COLON + PRE_OCCUPY);
            setAllOcpVal(preOcpParam);
            // add by zhangrui 有预占的实占试算 end
            // 取得日历信息
            ICalendar calendar = getCalendar((String) mapData.get(CALENDAR_ID));
            // 匹配风险事件
            List<IEvent> eveList = getEvent(mapData, calendar);
            // 若风险事件不存在，则试算通过
            // 触发的风险事件,不计算指标,则试算通过
            List<IEvent> eveList1 = new ArrayList();
            eveList1.addAll(eveList);
            if (StringUtil.isEmptyList(eveList1) || !isCalcIndicator(eveList1)) {
                // 试算通过
                response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
                response.setBodyField(RSK_LIST, new ArrayList<>());
                getLogger().info(LOG_INFO_EVENT);
                continue;
            }
          
          // 从风险事件列表中取出相关的参数列表
          Set<String> paraIdList = getParamIdList(eveList);
            // 对于批量交易出现两笔正反同步交易时，排除影响的同一参数
            removeSameParam(paraIdList, mapData, eveList);
          // 取得组合关联指标信息
          IPtlIndicator ptlInd = getPtlIndicator(mapData);
          // 若无 组合关联指标信息，则试算通过
          if (ptlInd == null) {
              response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
              response.setBodyField(RSK_LIST, new ArrayList<>());
              getLogger().info(LOG_INFO_PASS);
              continue;
          }
          //*********************************风险计算*************************************************
          
          // 取得所有指标代码
          Set<String> indIdList = getIndIdList(ptlInd);
          // 若组合未关联指标信息，则试算通过
          if (CollectionUtils.isEmpty(indIdList)) {
              getLogger().info(LOG_INFO_PASS);
              response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
              response.setBodyField(RSK_LIST, new ArrayList<>());
              continue;
          }
          log("处理前的产品关联指标详细：【{}】", ptlInd.getPtlIndicator().entrySet(), en->en != null ? en.getKey() + "=[" + StringUtil.join(en.getValue(), GlobalConstant.COMMA) + "]" : "");
         
          // 取得所有的指标信息
          Map<String, IIndicator> indMap = getIndicator(indIdList);
          // 若指标信息不存在，则抛出异常
          if (CollectionUtils.isEmpty(indMap)) {
              throw new InterfException("指标信息不存在");
          }
          // 构建组合与指标参数索引
          Map<String, Map<String, String>> ptlParamMap = getPtlParamMap(ptlInd, indMap);

          Map<String, Set<String>> ptlIndMap = ptlInd.getPtlIndicator();
          // 将风险事件关联的指标参数挂到组合（包）
          putEventParam(ptlParamMap, paraIdList);
          // 风险事件相关参数与指标相关参数糅合去重(并集)
          unionParamId(indMap, paraIdList, ptlIndMap);
          // 一条指标都没有的时候
          if (CollectionUtils.isEmpty(indMap) || CollectionUtils.isEmpty(indMap.values())) {
              // 状态
              response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
              // 试算结果
              response.setBodyField(RSK_LIST, new ArrayList<>());
              continue;
          }
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
          log("涉及的指标参数有：【{}】", paramMap.entrySet(), en->en != null ? en.getKey() : "");

          // add by zhangrui1 start
          setServiceInfo("paramMap", paramMap);
          // add by zhangrui1 end
          checkTpAndIndivAd(indMap, mapData, ptlInd);
          // 当指标都被剔除时，试算通过，直接进行预占
          if (CollectionUtils.isEmpty(ptlIndMap) || CollectionUtils.isEmpty(ptlIndMap.values())) {
              getLogger().info("无指标需要计算，试算通过");
              // 状态
              response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
              // 试算结果
              response.setBodyField(RSK_LIST, new ArrayList<>());
              continue;
          }
          log("处理后的产品关联指标详细：【{}】", ptlInd.getPtlIndicator().entrySet(), en->en != null ? en.getKey() + "=[" + StringUtil.join(en.getValue(), GlobalConstant.COMMA) + "]" : "");
          // add by zhangrui1 批量试算时暂存指标趋势信息 start
          final int occupyCode = index;
          // add by zhangrui1 批量试算时暂存指标趋势信息 end
          // 执行事务处理（Redis操作）
          handleByLock(() -> {
              // add by zhangrui1 start
              calcMixAmount(mapData, false, false, null);
              // add by zhangrui1 end
              String uuid =  String.valueOf(System.currentTimeMillis());
              mapData.put("uuid", uuid);
            
              //需要从redis检索的值
              Map<String, Map<String, IValue<Object>>> condition=createCondition(paramMap, mapData, ptlParamMap);
              // modify by zhangrui1 start
              //Map<String, Map<String, IValue<Object>>> paraValMap =  new HashMap<>();
              // 取得所有指标参数的值
              //if(cachedMap.containsKey(portfolioCode)){
            	//  paraValMap = cachedMap.get(portfolioCode);
              //}else{
               //paraValMap = getParamValSrv().getParamVal(condition);
               //cachedMap.put(portfolioCode, paraValMap);
              //}
              // 每一轮影响的参数可能都不一样，不能完全依赖上一次的结果，应该取出本次受影响的参数，然后将上次计算后的参数再更新进来
              Map<String, Map<String, IValue<Object>>> paraValMap = getParamValSrv().getParamVal(condition);
              mergeMap(paraValMap, localCache);
              // modify by zhangrui1 end

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
              
              // add by zhangrui1 批量试算时暂存指标趋势信息 start
              IOccupyRelParam newOcpParam = preOcpParam == null ? new OccupyRelParam() : preOcpParam;
              // 预占时需要记录生成的预占信息
                if (preOcpParam == null) {
                    ((OccupyRelParam)newOcpParam).setOccupyCode(String.valueOf(occupyCode));
                    ((OccupyRelParam)newOcpParam).setOccupyType(PRE_OCCUPY);
                    ((OccupyRelParam)newOcpParam).setItems(new HashMap<>());
                }
              // add by zhangrui1 批量试算时暂存指标趋势信息 end
                // add by zhangrui1 一级投标的修改预占试算 start
                removeBeforePre(paraValMap, preOcpParam, mapData);
                // add by zhangrui1 一级投标的修改预占试算 end
              // 计算参数值
                Map<String, Map<String, IValue<Object>>> newValMap = calcParamVal(eveList,
                    paraValMap, getRelParam(mapData, newOcpParam), CALC_SCENE_OCP_DIFF, mapData,
                    details);
              
              // modify by zhangrui1 计算结果出现重复问题对应 start
              //refreshCache(newValMap,cachedMap,portfolioCode);
              // modify by zhangrui1 计算结果出现重复问题对应 end
              
              if (!CollectionUtils.isEmpty(indMap) && !CollectionUtils.isEmpty(indMap.values())) {
                  // 固定值参数
                  Map<String, Object> map = getFixedParaVal(paramMap,mapData);
                  // add by zhangrui1 同向变动特殊处理 start
                  Map<String, IIndicator> leftIndMap = getIndTrendCalcSrc().executeList(indMap, paramMap,
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
                      // add by zhangrui1 批量试算时暂存指标趋势信息 start
                      }, infoMap -> {
                            // 前面试算的趋势和假设预占信息存到缓存
                            if (!CollectionUtils.isEmpty(ocpList)) {
                                Map<String, IOccupyRelParam> ocpMap = (Map<String, IOccupyRelParam>)infoMap.get(
                                    IndicatorTrendCalcService.OCP_LIST);
                                ocpList.forEach(ocp -> ocpMap.put(ocp.getField(), ocp));
                            }
                            if (!CollectionUtils.isEmpty(indTrendList)) {
                                Map<String, IIndicatorTrend> indTrendMap = (Map<String, IIndicatorTrend>)infoMap.get(
                                    IndicatorTrendCalcService.INDICATOR_TREND);
                                indTrendList.forEach(indTrend -> getIndTrendCalcSrc().addIndTrendToMap(indTrend, indTrendMap));
                                setTrendInd(indMap, indTrendMap, portfolioCode);
                            }
                            Map<String, Map<String, IValue<Object>>> temp = (Map<String, Map<String, IValue<Object>>>)infoMap.get(
                                IndicatorTrendCalcService.OLD_VAL_MAP);
                            mergeMap(temp, localCache);
                      // add by zhangrui1 批量试算时暂存指标趋势信息 end
                      });
                    if (!CollectionUtils.isEmpty(leftIndMap)) {
                  IPtlIndicator newPtlInd = getIndTrendCalcSrc().newPtlInd(ptlInd, leftIndMap);
                  // add by zhangrui1 同向变动特殊处理 end
                  // ---zxjjpei---枚举型参数 走库型参数 20200312 start-----
                  Map<String, Object> mapFbd = getForbidParaVal(paramMap,mapData);
                  Map<String, Object> mapDbCal =getDataBaseParaVal(eveList,ptlParamMap,paramMap,mapData);
                  // ---zxjjpei---枚举型参数 走库型参数 20200312 end-----
                  // 指标参数原值
                  Map<String, Map<String, Object>> pValMap = parseParaVal(ptlParamMap, paraValMap, paramMap);
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
                  pValMap = parseParaVal(ptlParamMap, calcValWithoutEvt(getParamIdList(eveList), eveList, newValMap), paramMap);
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
                    }
                  // add by zhangrui1 同向变动特殊处理 end
                  indMap.entrySet().forEach(en -> checkIndTrend(en.getValue()));
                  // 处理返回结果
                  writeResponse(rsp, indMap, paramMap);
                  // add by zhangrui1 批量试算时暂存指标趋势信息 start
                  if (StringUtil.equals(mapData.get(EVENT_TYPE), "0")) {
                      ocpList.add(newOcpParam);
                      indTrendList.addAll(getIndTrendCalcSrc().createIndTrend(portfolioCode, occupyCode + GlobalConstant.COLON + PRE_OCCUPY, indMap));
                  }
                  // add by zhangrui1 批量试算时暂存指标趋势信息 end
              }
              // add by zhangrui1 计算结果出现重复问题对应 start
              mergeMap(localCache, newValMap);
              // add by zhangrui1 计算结果出现重复问题对应 end
              return 0;
          });
          // add by zhangrui1 批量试算时暂存指标趋势信息 start
          index++;
          // add by zhangrui1 批量试算时暂存指标趋势信息 end

          // add by zhangrui1 试算返回值增加资产名称 start
          if (CommonUtil.isType((String)mapData.get("isInnerTrd"), BussinessDictCodeListRds.是否_是)) {
                Integer thisGroup = innerGroup.get(mapData.get(PORTFOLIOCODE));
                if (thisGroup == null) {
                    thisGroup = groupId;
                    innerGroup.put(mapData.get(COUNTERID), groupId);
                    groupId++ ;
                }
                handleResponse(rsp, assetInfo, thisGroup);
          }
          else {
              handleResponse(rsp, assetInfo, groupId);
              groupId++;
          }
          // add by zhangrui1 试算返回值增加资产名称 end 
          //*********************************风险计算*************************************************
        }
        // 设置返回
        setResponse(groupMap, response);
        return null;
    }

    
    /**
     * 合并时算结果
     * 
     * @param groupMap 试算结果
     * @param response 输出
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected void setResponse(List<ClientData> groupMap, ClientData response) {
        String rtnCode = InfColumns.COM_RTNCODE.fieldName();
        String rtnMsg = InfColumns.COM_RTNMSG.fieldName();
        response.setHeadField(rtnCode, AAAAAAA);
        response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
        if (!CollectionUtils.isEmpty(groupMap)) {
            List<Map<String, Object>> rskList = new ArrayList<>();
            groupMap.forEach(value -> {
                if (value != null && !value.isEmpty()) {
                    String rtCd = (String)value.getHeadField(rtnCode);
                    if (!StringUtil.equals(AAAAAAA, rtCd) && !StringUtil.isEmpty(rtCd)) {
                        response.setHeadField(rtnCode, rtCd);
                        response.setHeadField(rtnMsg, value.getHeadField(rtnMsg));
                    }
                    // 当有一个资产不达标时则不通过
                    String kriStatus = (String)value.getBodyField(RSKSTATUS);
                    String rskStatus = (String)response.getBodyField(RSKSTATUS);
                    rskStatus = CommonUtil.compareStr(kriStatus, rskStatus,
                        BussinessDictCodeListRds.运算符_大于.getValue()) ? kriStatus : rskStatus;
                    response.setBodyField(RSKSTATUS, rskStatus);
                    // 合并试算结果
                    List<Map<String, Object>> temp = (List<Map<String, Object>>)value.getBodyField(
                        RSK_LIST);
                    if (temp != null) {
                        rskList.addAll(temp);
                    }
                }
            });
            response.setBodyField(RSK_LIST, rskList);
        }
    }
 
    // add by zhangrui1 start
    /**
     * 将newMap内容覆盖到targetMap中去
     * 
     * @param targetMap 目标MAP
     * @param newMap 变化MAP
     */
    protected void mergeMap(final Map<String, Map<String, IValue<Object>>> targetMap, Map<String, Map<String, IValue<Object>>> newMap) {
        if (CollectionUtils.isEmpty(newMap)) {
            return;
        }
        newMap.entrySet().forEach(en -> {
            String key = en.getKey();
            Map<String, IValue<Object>> temp = targetMap.getOrDefault(key, new HashMap<>());
            Map<String, IValue<Object>> old = en.getValue();
            if (!CollectionUtils.isEmpty(old) && !CollectionUtils.isEmpty(temp)) {
                old.entrySet().forEach(item -> {
                    String code = item.getKey();
//                    if (temp.containsKey(code) && item.getValue() != null) {
//                        temp.put(code, item.getValue());
//                    }
                    //ZGZGST-2925 多个组合都需要缓存同一个指标参数到本地的时候，第一个组合缓存后会导致后续组合的指标参数不能被缓存到
                    if (item.getValue() != null) {
                        temp.put(code, item.getValue());
                    }
                });
            }
            else if (CollectionUtils.isEmpty(temp)) {
                temp.putAll(old);
            }
            if (!checkUpRdsParam(key)) return;
            targetMap.put(key, temp);
        });
     }
//	private void refreshCache(Map<String, Map<String, IValue<Object>>> newValMap,
//			Map<String, Map<String, Map<String, IValue<Object>>>> cachedMap, String portfolioCode) {
//		if(cachedMap.containsKey(portfolioCode)){
//			cachedMap.get(portfolioCode).putAll(newValMap);
//		}else{
//			Map<String, Map<String, Map<String, IValue<Object>>>> newMap =new HashMap<>();
//			newMap.put(portfolioCode, newValMap);
//		cachedMap.putAll(newMap);
//		}
//	}
    // add by zhangrui1 end
    @SuppressWarnings("rawtypes")
    private void initIProdInfo(HashMap<String,IProdInfo> prodInfoCache, List<Map> mapList) {
        for (Map mapData : mapList) {
          String portfolioCode = (String)mapData.get(PORTFOLIOCODE);
          if(!prodInfoCache.containsKey(portfolioCode)){
        	  IProdInfo  prodInfo = getProdInfo(portfolioCode);
        	  prodInfoCache.put(portfolioCode, prodInfo);
        }
       }
	}
	/**
     * 是否做过预占
     * 
     * @param request 请求报文
     * @return true:做过预占
     */
    protected boolean isPreOcp(Map<String, Object> request) {
        // 预占编号
        String preCode = (String)request.get("preOccupyCode");
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

    private void setTrendInd(Map<String, IIndicator> indMap,
                             Map<String, IIndicatorTrend> indTrendMap, String portfolioCode) {
        // 过滤出没有趋势的指标信息, ZGZGST-1821 如果不展示原值，那么指标就不存在趋势问题，直接放入没有趋势的指标信息中
        Map<String, IIndicator> leftInd = CommonUtil.mapFilter(indMap,
            ind -> !indTrendMap.containsKey(ind.getKey() + GlobalConstant.COLON + portfolioCode) || StringUtil.equals(DictCodeList.是否_否.getValue(),ind.getValue().getIsDisplayOriginal()));
        getIndTrendCalcSrc().setServiceInfo(IndicatorTrendCalcService.NO_TREND_INDICATOR, leftInd);
        // 过滤出有指标趋势的指标信息，ZGZGST-1821 只有展示原值的指标才有趋势问题
        Map<String, IIndicator> rightInd = CommonUtil.mapFilter(indMap,
            ind -> indTrendMap.containsKey(ind.getKey() + GlobalConstant.COLON + portfolioCode) && StringUtil.equals(DictCodeList.是否_是.getValue(),ind.getValue().getIsDisplayOriginal()));
        getIndTrendCalcSrc().setServiceInfo(IndicatorTrendCalcService.HAS_TREND_INDICATOR,
            rightInd);
    }
}

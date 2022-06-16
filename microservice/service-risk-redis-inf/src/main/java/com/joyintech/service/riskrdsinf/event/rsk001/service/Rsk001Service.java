/*
 * 文件名：Rsk001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年8月5日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.riskrdsinf.event.rsk001.service;

import java.math.BigDecimal;
import java.util.*;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.msg.entity.MessageBody.Operation;
import com.joyintech.service.riskrdsinf.common.service.EventBaseService;
import com.joyintech.service.riskrdsinf.common.service.OccupyDbService;
import com.joyintech.service.riskrdsinf.core.annotation.INF;
import com.joyintech.service.riskrdsinf.core.client.ClientData;
import com.joyintech.service.riskrdsinf.core.exception.InterfException;
import com.joyintech.service.riskrdsinf.core.util.InfMapValUtil;
import com.joyintech.service.riskrdsinf.event.rsk001.entity.Rsk001InEntity;
import com.joyintech.service.riskrdsinf.event.rsk005.service.Rsk005Service;
import com.joyintech.tams.common.base.entity.RskIndicatorParaCalcdetail;
import com.joyintech.tams.common.base.entity.RskIsupTrialCls;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.calendar.entity.ICalendar;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEvent;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicator;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IProdInfo;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOccupyRelParam;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOcpRelParamItem;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.OccupyRelParam;
import com.joyintech.tams.invest.common.riskinfrds.param.entity.IParam;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamValService;
import com.joyintech.tams.invest.common.riskinfrds.prod.entity.IPtlIndicator;

/**
 * 风险试算接口处理详细
 * 
 * @author zhangrui1
 * @version 1.0 2019年8月5日
 * @see Rsk001Service
 * @since 1.0
 */
@INF(value = "RSK001")
@Service
public class Rsk001Service extends EventBaseService<Rsk001InEntity> {

    public static final String PRE_OCP_FAILED = "未触发风控事项，预占失败";

    /**
     * 状态_占用
     */
    public static final String STATUS_OCCUPY = "0";

    @Autowired
    private OccupyDbService busiService;

    @Autowired
    private Rsk005Service rsk005Service;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Map<String, Rsk001InEntity>> handle(ClientData request,
                                                              ClientData response)
        throws InterfException {
        // 取得报文体
        Map<String, Object> mapData = new HashMap<>();
        // 取得报文体
        mapData.putAll(request.getBody());
        try {
            // 交易方向数据字典转换
            changePsValue(mapData);
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
                throw new InterfException("产品(" + portfolioCode + ")未启动风险不能进行预占");
            }
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
            // 金额*上调系数
            factor(mapData);
            // 预占编号 (系统代码:委托编号)
            String occupyCode = /*getSysCode() + GlobalConstant.COLON
                                +*/ (String)request.getBodyField(EVENT_ID);
            // 预占实占参数信息
            IOccupyRelParam ocpRelParam = getOccupy(occupyCode, PRE_OCCUPY);
            // 若预占信息存在则表示该委托做过预占，不需要再进行后续操作
            if (ocpRelParam != null) {
                getLogger().info("该笔委托已存在预占信息");
                // add by zhangrui1 差分预占 start
                if (!isDiffOcp(mapData)) {
                // add by zhangrui1 差分预占 end
                // 预占信息存在时，撤销原来的预占重新预占
                request.getBody().put("revokeFlg", BussinessDictCodeListRds.是否_是.getValue());
                rsk005Service.execute(request);
                    ocpRelParam = null;
                // del by zhangrui1 可重复预占 start
                // setRtnCode(response, InfDictCodeList.信息码_提示.getValue(), 4);
                // setRtnMsg(response, "该笔委托已存在预占信息");
                // return null;
                // del by zhangrui1 可重复预占 end
                // add by zhangrui1 差分预占 start
                }
                // add by zhangrui1 差分预占 end
            }
            /************************************/
            /** 风险计算 ***************************/
            /************************************/
            // 取得日历信息
            ICalendar calendar = getCalendar((String)mapData.get(CALENDAR_ID));
            // 匹配风险事件
            List<IEvent> eveList = getEvent(mapData, calendar);
            // 若风险事件不存在，则预占失败
            if (StringUtil.isEmptyList(eveList)) {
                throw new InterfException(PRE_OCP_FAILED);
            }
            // 触发的风险事件不计算指标
            List<IEvent> eveList1 = new ArrayList();
            eveList1.addAll(eveList);
            if (!isCalcIndicator(eveList1)) {
                getLogger().info("触发的风控事项不计算指标");
                // 取得组合关联指标信息
                IPtlIndicator ptlInd = getPtlIndicator(mapData);
                occupyWithoutCalc(occupyCode, eveList, ocpRelParam, mapData, ptlInd);
                // 状态
                response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
                // 试算结果
                response.setBodyField(RSK_LIST, new ArrayList<>());
                return null;
            }
            // 取得组合关联指标信息
            IPtlIndicator ptlInd = getPtlIndicator(mapData);
            // 若无 组合关联指标信息，则试算通过，直接进行预占
            if (ptlInd == null) {
                getLogger().info(LOG_INFO_PASS);
                occupyWithoutCalc(occupyCode, eveList, ocpRelParam, mapData, null);
                // 状态
                response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
                // 试算结果
                response.setBodyField(RSK_LIST, new ArrayList<>());
                return null;
            }
            occupyWithCalc(request, response, occupyCode, ptlInd, eveList, ocpRelParam, mapData);
            // add by zhangrui1 试算返回值增加资产名称 start
            handleResponse(response, assetInfo, 0);
            // add by zhangrui1 试算返回值增加资产名称 end
        }
        catch (Exception e) {
            getLogger().error("处理异常", e);
            throw new InterfException(e.getMessage());
        }
        // TODO 禁（可）投指标处理
        return null;
    }

    /**
     * 风险计算后未达到违规线时进行预占
     * 
     * @param request 请求数据
     * @param response 应答数据
     * @param occupyCode 预占编号
     * @param ptlInd 组合关联指标
     * @param eveList 风险事件
     * @param ocpRelParam 预占信息
     * @param mapData 接口输入
     * @throws InterfException 接口异常
     * @see
     * @since 1.0
     */
    private void occupyWithCalc(ClientData request, ClientData response, String occupyCode,
                                IPtlIndicator ptlInd, List<IEvent> eveList,
                                // add by zhangrui1 差分预占 start
                                IOccupyRelParam ocpRelParam,
                                // add by zhangrui1 差分预占 end
                                Map<String, Object> mapData)
        throws InterfException {
    	// 指标参数计算明细
    	List<RskIndicatorParaCalcdetail> details = new ArrayList<>();

        // 取得所有指标代码
        Set<String> indIdList = getIndIdList(ptlInd);
        // 若组合未关联指标信息，则试算通过，直接进行预占
        if (CollectionUtils.isEmpty(indIdList)) {
            getLogger().info(LOG_INFO_PASS);
            occupyWithoutCalc(occupyCode, eveList, ocpRelParam, mapData, ptlInd);
            // 状态
            response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
            // 试算结果
            response.setBodyField(RSK_LIST, new ArrayList<>());
            return;
        }

        log("处理前的产品关联指标详细：【{}】", ptlInd.getPtlIndicator().entrySet(), en->en != null ? en.getKey() + "=[" + StringUtil.join(en.getValue(), GlobalConstant.COMMA) + "]" : "");

        // 取得所有的指标信息
        Map<String, IIndicator> indMap = getIndicator(indIdList);
        // 若指标信息不存在，则抛出异常，不进行预占处理
        if (CollectionUtils.isEmpty(indMap)) {
            getLogger().info("指标信息不存在，试算通过");
            occupyWithoutCalc(occupyCode, eveList, ocpRelParam, mapData, ptlInd);
            // 状态
            response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
            // 试算结果
            response.setBodyField(RSK_LIST, new ArrayList<>());
            return;
        }
        // 构建组合与指标参数索引
        Map<String, Map<String, String>> ptlParamMap = getPtlParamMap(ptlInd, indMap);

        Map<String, Set<String>> ptlIndMap = ptlInd.getPtlIndicator();
        // 从风险事件列表中取出相关的参数列表
        Set<String> paraIdList = getParamIdList(eveList);
        // 对于批量交易出现两笔正反同步交易时，排除影响的同一参数
        removeSameParam(paraIdList, mapData, eveList);
        // 将风险事件关联的指标参数挂到组合（包）
        putEventParam(ptlParamMap, paraIdList);
        // 风险事件相关参数与指标相关参数糅合去重(并集)
        unionParamId(indMap, paraIdList, ptlIndMap);
        // 一条指标都没有的时候
        if (CollectionUtils.isEmpty(indMap) || CollectionUtils.isEmpty(indMap.values())) {
            occupyWithoutCalc(occupyCode, eveList, ocpRelParam, mapData, ptlInd);
            // 状态
            response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
            // 试算结果
            response.setBodyField(RSK_LIST, new ArrayList<>());
            return;
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
        setServiceInfo("paramMap", paramMap);
        log("涉及的指标参数有：【{}】", paramMap.entrySet(), en->en != null ? en.getKey() : "");
        setRemark(response,"涉及的指标参数有：", paramMap.entrySet(), en->en != null ? en.getKey() : "",paramMap);
        RskIsupTrialCls isupTrialCls = new RskIsupTrialCls();
        // 业务类型
        isupTrialCls.setTradeType((String)request.getBodyField(EVENT_TYPE));
        // 业务编号
        isupTrialCls.setTradeId(occupyCode);

        // 判断监督时点，若不满足则指标不计算,判断是否剔除资产，若满足则指标不计算
        checkTpAndIndivAd(indMap, mapData, ptlInd);
        // 当指标都被剔除时，试算通过，直接进行预占
        if (CollectionUtils.isEmpty(ptlIndMap) || CollectionUtils.isEmpty(ptlIndMap.values())) {
            getLogger().info("无指标需要计算，试算通过");
            occupyWithoutCalc(occupyCode, eveList, ocpRelParam, mapData, ptlInd);
            // 状态
            response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
            // 试算结果
            response.setBodyField(RSK_LIST, new ArrayList<>());
            return;
        }

        log("处理后的产品关联指标详细：【{}】", ptlInd.getPtlIndicator().entrySet(), en->en != null ? en.getKey() + "=[" + StringUtil.join(en.getValue(), GlobalConstant.COMMA) + "]" : "");

        // 执行事务处理（Redis操作）
        int type = handleByLock(() -> {

            String uuid =  String.valueOf(System.currentTimeMillis());
            mapData.put("uuid", uuid);
            // 取得所有指标参数的值
            Map<String, Map<String, IValue<Object>>> paraValMap = getParamValSrv().getParamVal(
                createCondition(paramMap, mapData, ptlParamMap));
            // 若指标参数值未初始化到Redis则抛出异常
            if (CollectionUtils.isEmpty(paraValMap)) {
                //----update by zhangrui1 2020030706 若存在固定值参数则让通过 start---
                if(checkSqlParamType(paramMap)){
                    throw new InterfException("指标参数值不存在");
                }else {
                    getLogger().info("没有SQL类型参数且指标参数值不存在，直接通过");
                    return -1;
                }
                // ----update by zhangrui1 2020030706 若存在固定值参数则让通过 end---
            }
            // 添加预占参数信息
            OccupyRelParam newOcpRelParam = new OccupyRelParam();
            Map<String, IOcpRelParamItem> ocpPmItem = new HashMap<>();
            newOcpRelParam.setItems(ocpPmItem);
            // add by zhangrui1 差分预占 start
            if (ocpRelParam != null){
                BeanUtil.copyPropertiesIgnoreNull(newOcpRelParam,ocpRelParam);
            }
//            newOcpRelParam = ocpRelParam != null ? (OccupyRelParam)ocpRelParam : newOcpRelParam;
            // add by zhangrui1 差分预占 end
            // 计算参数值
            Map<String, Map<String, IValue<Object>>> newValMap = calcParamVal(eveList,
                paraValMap, newOcpRelParam, CALC_SCENE_OCP_DIFF, mapData, details);
            if (!CollectionUtils.isEmpty(indMap) && !CollectionUtils.isEmpty(indMap.values())) {
                // 固定值参数
                Map<String, Object> map = getFixedParaVal(paramMap,mapData);
                // add by zhangrui1 同向变动特殊处理 start
                Map<String, IIndicator> leftIndMap = getIndTrendCalcSrc().execute(indMap, paramMap,
                    getIndTrendCalcSrc().filterValMap(new HashMap<>(), paraValMap), eveList,
                    mapData,
                    (newIndMap, indValMap, newIndValMap) -> {
                        IPtlIndicator newPtlInd = getIndTrendCalcSrc().newPtlInd(ptlInd,
                            newIndMap);
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
                if (!CollectionUtils.isEmpty(leftIndMap)) {
                    IPtlIndicator newPtlInd = getIndTrendCalcSrc().newPtlInd(ptlInd, leftIndMap);
                // add by zhangrui1 同向变动特殊处理 end
                // ---zhangrui1---枚举型参数 走库型参数 20200706 start-----
                Map<String, Object> mapFbd = getForbidParaVal(paramMap,mapData);
                Map<String, Object> mapDbCal =getDataBaseParaVal(eveList,ptlParamMap,paramMap,mapData);
                // ---zhangrui1---枚举型参数 走库型参数 20200706 end-----
                // 指标参数原值
                Map<String, Map<String, Object>> pValMap = parseParaVal(ptlParamMap, paraValMap,paramMap);
                // 加入固定值
                unionParaVal(pValMap, map);
                // --add by zhangrui1 20200706 加入走库参数 start---
                unionParaVal(pValMap, mapDbCal);
                // --add by zhangrui1 20200706 加入走库参数 end---
                //----add by zhangrui1 20200706 把固定值与走库加上画面的这一笔进行计算 start-------
                calcFixParamVal(eveList,map);
                calcFixParamVal(eveList,mapDbCal);
                //----add by zhangrui1 20200706 把固定值与走库加上画面的这一笔进行计算 end-------
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
                // ---zhangrui1---加入枚举型参数值 走库型参数 20200706 start-----
                unionParaVal(pValMap,mapFbd);
                unionParaVal(pValMap,mapDbCal);
                // ---zhangrui1---加入枚举型参数值 走库型参数 20200706 end-----
                // 指标计算新值
                // add by zhangrui1 同向变动特殊处理 start
                /**
                calcIndicator(ptlInd, indMap, mapData, pValMap, false);
                indMap.entrySet().forEach(en -> checkIndTrend(en.getValue()));
                */
                calcIndicator(newPtlInd, indMap, mapData, pValMap, false);
                }
                indMap.entrySet().forEach(en -> checkIndTrend(en.getValue()));
                mapData.put(PORTFOLIOCODE, mapData.get(PROD_ID));
                // 设置指标趋势到本地缓存
                getIndTrendCalcSrc().setTrend(indMap, mapData, occupyCode + GlobalConstant.COLON + PRE_OCCUPY);
                // add by zhangrui1 同向变动特殊处理 end
                // 处理返回结果
                writeResponse(response, indMap, paramMap);
                int res = isStopForReturn(request, mapData, response);
                if (res != 0) {
                    return res;
                }
            }
            // 更新Redis
            getParamValSrv().setParamVal(filterNoUpRdsParam(newValMap));
            // add by zhangrui1 差分预占 start
            if (!isDiffOcp(mapData)) {
            // add by zhangrui1 差分预占 start
            newOcpRelParam.setOccupyCode(occupyCode);
            // 预占/实占类型
            newOcpRelParam.setOccupyType(PRE_OCCUPY);
            // 预占/实占参数信息列表
            newOcpRelParam.setItems(ocpPmItem);
            // 设置净值
            newOcpRelParam.setNetPrice((BigDecimal) mapData.get("unitNet"));
                // add by zhangrui1 差分预占 start
            }
            // add by zhangrui1 差分预占 end
            // 更新预占/实占信息
            setOccupyRelParam(newOcpRelParam);
            // add by zhangrui1 同向变动特殊处理 start
            // 更新指标趋势信息
            getIndTrendCalcSrc().saveIndTrendList();
            // add by zhangrui1 同向变动特殊处理 end
            return 0;
        });
        String rskStatus = (String)response.getBodyField(RSKSTATUS);
        // 预占结果保存
        if (type == 0) {
            // 更新DB
            Rsk001InEntity record = InfMapValUtil.mapDataToObject(mapData, Rsk001InEntity.class);
            // 设置预占编号
            record.setEventId(occupyCode);
            // 设置组合代码
            record.setProdId((String)mapData.get(PROD_ID));
            // 设置备份数据
            record.setAmount(record.getAmountOld());
            record.setTradeNumOld(record.getTradeNumOld());
            // 报文流水号
            record.setSeqNo(getSeqNo());
            // 预占信息保存DB
            busiService.savePreOcpInfo(record,
                CommonUtil.isType(rskStatus, BussinessDictCodeListRds.指标状态_审批), STATUS_OCCUPY,
                getInfId());

            // 试算结论
            isupTrialCls.setConclusion(rskStatus);
            // 设置流水号
            isupTrialCls.setSeqNo((String) mapData.get("uuid"));
            // 保存风险计算结论
            setUserDt(isupTrialCls);
            saveDb(Operation.W, isupTrialCls);
            // 保存监督指标计算结果
            saveRskIsupTrial(response, mapData, occupyCode);
            // 参数计算明细
            freshDetail(details, paramMap);
            saveDetails(details, mapData, occupyCode);
        }
    }

    /**
     * 无指标计算，直接进行预占
     * 
     * @param occupyCode 预占编号
     * @param eveList 风险事件
     * @param ocpRelParam 预占信息
     * @param mapData 接口输入
     * @param ptlInd 组合关联指标信息
     * @throws InterfException 接口异常
     * @see
     * @since 1.0
     */
    private void occupyWithoutCalc(String occupyCode, List<IEvent> eveList,
                                   // add by zhangrui1 差分预占 start
                                   IOccupyRelParam ocpRelParam,
                                   // add by zhangrui1 差分预占 end
                                   Map<String, Object> mapData, IPtlIndicator ptlInd)
        throws InterfException {
    	// 指标参数计算明细
    	List<RskIndicatorParaCalcdetail> details = new ArrayList<>();
        // 从风险事件列表中取出相关的参数列表
        Set<String> paraIdList = getParamIdList(eveList);
        // 对于批量交易出现两笔正反同步交易时，排除影响的同一参数
        removeSameParam(paraIdList, mapData, eveList);
        // 取得所有指标参数信息
        Map<String, IParam> paramMap = getParam(paraIdList);
        // 若指标参数不存在则抛出异常
        if (CollectionUtils.isEmpty(paramMap)) {
            throw new InterfException("指标参数信息不存在，预占失败");
        }
        setServiceInfo("paramMap", paramMap);
        // 取得组合关联所有指标代码
        Set<String> indIdList = getIndIdList(ptlInd);
        // 指标参数查询条件
        Map<String, Map<String, IValue<Object>>> paraCond = null;
        // 若无 组合关联指标信息，则只需组合代码参与指标参数计算
        if (CollectionUtils.isEmpty(indIdList)) {
            // 根据参数信息构建参数值查询条件
            paraCond = createParaCond(paramMap, mapData);
        }
        else {
            // 取得所有的指标信息
            Map<String, IIndicator> indMap = getIndicator(indIdList);
            // 若指标信息不存在，则只需组合代码参与指标参数计算
            if (CollectionUtils.isEmpty(indMap) || CollectionUtils.isEmpty(indMap.values())) {
                paraCond = createParaCond(paramMap, mapData);
            }
            else {
                // 构建组合与指标参数索引
                Map<String, Map<String, String>> ptlParamMap = getPtlParamMap(ptlInd, indMap);
                //------add by zhangrui1 20200706 将风险事件关联的指标参数挂到组合（包）start----
                putEventParam(ptlParamMap, paraIdList);
                //------add by zhangrui1 20200706 将风险事件关联的指标参数挂到组合（包）end----
                paraCond = createCondition(paramMap, mapData, ptlParamMap);
            }
        }
        final Map<String, Map<String, IValue<Object>>> paraList = paraCond;

        log("涉及的指标参数有：【{}】", paraCond.entrySet(), en->en != null ? en.getKey() : "");

        // 执行事务处理（Redis操作）
        int type = handleByLock(() -> {
            String uuid =  String.valueOf(System.currentTimeMillis());
            mapData.put("uuid", uuid);
            IndicatorParamValService service = getParamValSrv();
            // 取得所有指标参数的值
            Map<String, Map<String, IValue<Object>>> paraValMap = service.getParamVal(
                paraList);
            // 若指标参数值未初始化到Redis则抛出异常
            if (CollectionUtils.isEmpty(paraValMap)) {
                if(checkSqlParamType(paramMap)){
                    throw new InterfException("指标参数值不存在");
                }else {
                    getLogger().info("没有SQL类型参数且指标参数值不存在，直接通过");
                    return -1;
                }
            }
            // 添加预占参数信息
            OccupyRelParam newOcpRelParam = new OccupyRelParam();
            Map<String, IOcpRelParamItem> ocpPmItem = new HashMap<>();
            newOcpRelParam.setItems(ocpPmItem);
            // add by zhangrui1 差分预占 start
            newOcpRelParam = ocpRelParam != null ? (OccupyRelParam)ocpRelParam : newOcpRelParam;
            // add by zhangrui1 差分预占 end
            // 计算参数值
            Map<String, Map<String, IValue<Object>>> newValMap = calcParamVal(eveList,
                paraValMap, newOcpRelParam, CALC_SCENE_OCP_DIFF, mapData, details);
            // 更新Redis
            service.setParamVal(filterNoUpRdsParam(newValMap));
            // add by zhangrui1 差分预占 start
            if (!isDiffOcp(mapData)) {
                // add by zhangrui1 差分预占 start
            newOcpRelParam.setOccupyCode(occupyCode);
            // 预占/实占类型
            newOcpRelParam.setOccupyType(PRE_OCCUPY);
            // 预占/实占参数信息列表
            newOcpRelParam.setItems(ocpPmItem);
            // 设置净值
            newOcpRelParam.setNetPrice((BigDecimal) mapData.get("unitNet"));
                // add by zhangrui1 差分预占 start
            }
            // add by zhangrui1 差分预占 start
            // 更新预占/实占信息
            setOccupyRelParam(newOcpRelParam);
            return 0;
        });
        if (type == 0) {
            // 更新DB
            Rsk001InEntity record = InfMapValUtil.mapDataToObject(mapData, Rsk001InEntity.class);
            // 设置预占编号
            record.setEventId(occupyCode);
            // 设置组合代码
            record.setProdId((String)mapData.get(PROD_ID));
            // 设置备份数据
            record.setAmount(record.getAmountOld());
            record.setTradeNum(record.getTradeNumOld());
            // 报文流水号
            record.setSeqNo(getSeqNo());
            // 实占信息保存DB
            busiService.savePreOcpInfo(record, false, STATUS_OCCUPY, getInfId());
            // 参数计算明细
            freshDetail(details, paramMap);
            saveDetails(details, mapData, occupyCode);
        }
    }

    /**
     * 根据参数信息构建参数值查询条件
     * 
     * @param paramMap 参数信息
     * @param mapData 接口输入
     * @return 参数值查询条件
     * @see
     * @since 1.0
     */
    private Map<String, Map<String, IValue<Object>>> createParaCond(Map<String, IParam> paramMap,
                                                                        Map<String, Object> mapData) {
        Map<String, Map<String, IValue<Object>>> resList = new HashMap<>();
        paramMap.entrySet().forEach(en -> {
            // 指标参数代码
            String paraCode = en.getKey();
            // 指标参数信息
            IParam param = en.getValue();
            if (param != null
                && !CommonUtil.isType(param.getParaDisplayType(),
                    BussinessDictCodeListRds.指标参数实现类型_类名)
                && !CommonUtil.isType(param.getParaDisplayType(),
                    BussinessDictCodeListRds.指标参数实现类型_固定值)) {
                // Redis存储识别域
                String[] rdsFields = param.getRedisFieldArr();
                if (rdsFields != null && rdsFields.length > 0) {
                // 指标参数值Redis识别域
                String field = getFiled(rdsFields, mapData);
                Map<String, IValue<Object>> map = new HashMap<>();
                map.put(field, null);
                resList.put(paraCode, map);
                }
            }
        });
        return resList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkWithCond(Rsk001InEntity obj, ClientData response) {
    	if (isAssetNoNeeds()) {
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

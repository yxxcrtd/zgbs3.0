/*
 * 文件名：Rsk005Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年8月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.riskrdsinf.common.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.joyintech.service.riskrdsinf.core.constant.InfColumns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.riskrdsinf.core.client.ClientData;
import com.joyintech.service.riskrdsinf.core.exception.InterfException;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.common.entity.Value;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.common.base.entity.RskIndicatorParaCalcdetail;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOccupyRelParam;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOcpRelParamItem;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamValService;

/**
 * 预占/实占释放接口详细处理
 * 
 * @author zhangrui1
 * @version 1.0 2019年8月27日
 * @see OccupyReleasService
 * @since 1.0
 */
public abstract class OccupyReleasService<V> extends EventBaseService<V> {

    @Autowired
    private OccupyDbService busiService;
    
    public static final String REVOKEFLG = "revokeFlg";

    // add by zhangrui1 处理委托完成 start
    public static final String PRE_LEFT_AMT = "preLeftAmt";
    // add by zhangrui1 处理委托完成 end

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Map<String, V>> handle(ClientData request, ClientData response)
        throws InterfException {
        Map<String, Object> mapData = new HashMap<>();
        // 取得报文体
        mapData.putAll(request.getBody());
        // 从报文体取得预占/实占编号
        String occupyCode = /*getSysCode() + GlobalConstant.COLON
                            +*/ (String)request.getBodyField(EVENT_ID);
        mapData.put(EVENT_ID, occupyCode);
        // 从Redis取得预占/实占信息
        IOccupyRelParam ocpRelParam = getOccupyRelParam(occupyCode, getOccupyType());
        boolean isPreOcp = getOccupyType() == PRE_OCCUPY;
        // 若预占/实占信息不存在
        if (ocpRelParam == null /*|| CollectionUtils.isEmpty(ocpRelParam.getItems())*/) {
            //---update by zxjjpei 20200318--不予提示 start----------
            //throw new InterfException((getOccupyType() == PRE_OCCUPY ? "预占" : "实占") + "对应的信息不存在!");
            response.setHeadField(InfColumns.COM_STDREMARK.fieldName(),(getOccupyType() == PRE_OCCUPY ? "预占" : "实占") + "对应的信息不存在!");
            return null;
            //---update by zxjjpei 20200318--不予提示 end----------
        }
        //没有任何风险特征值也存实占编号
        if(CollectionUtils.isEmpty(ocpRelParam.getItems())){
            handleByLock(() -> {
                String uuid =  String.valueOf(System.currentTimeMillis());
                mapData.put("uuid", uuid);
                // 撤销的场合，直接删除预占实占信息
                if (isRevoke(mapData)) {
                    getOcpService().deleteInfoFromRedis(RedisKeyEnum.预占实占信息.key(), occupyCode + GlobalConstant.COLON + getOccupyType());
                }
                // 更新预占/实占信息
                else {
                    setOccupyRelParam(ocpRelParam);
                }
                return ocpRelParam;
            });
            // 更新DB
            saveData(mapData, false, getInfId());
            return null;
        }
        // 净值
        calcAmount(mapData, ()->ocpRelParam.getNetPrice());
        // 预占/实占信息相关参数列表
        Map<String, IOcpRelParamItem> items = ocpRelParam.getItems();
        // 构造取参数值的条件
        Map<String, Map<String, IValue<Object>>> hashList = makeHashList(items, mapData, true);
        IOccupyRelParam preOcpParam2 = getPreOcpInfo(ocpRelParam, isPreOcp, hashList, mapData);
        final IndicatorParamValService paramValSrv = getParamValSrv();
        List<RskIndicatorParaCalcdetail> details = new ArrayList<>();
        handleByLock(() -> {
            // add by zhangrui1 start
            Set<String> codeList = null;
            if (isPreOcp) {
                codeList = ocpRelParam.getOcpCodeList();
            }
            else if (preOcpParam2 != null) {
                codeList = preOcpParam2.getOcpCodeList();
            }
            calcMixAmount(request.getBody(), isRevoke(mapData), isPreOcp, codeList, true);
            // add by zhangrui1 end
            String uuid =  String.valueOf(System.currentTimeMillis());
            mapData.put("uuid", uuid);
            // 取得所有指标参数的值
            Map<String, Map<String, IValue<Object>>> paraValMap = paramValSrv.getParamVal(hashList);
            // 若指标参数值未初始化到Redis则抛出异常
            if (CollectionUtils.isEmpty(paraValMap)) {
                throw new InterfException("指标参数值不存在");
            }
            // 计算参数值、预占/实占额度
            calcParamVal(paraValMap, mapData, items, details, preOcpParam2);
            // 有编号相同的占用的情况下进行试算，要在释放的基础上进行试算，并且不能写入Redis,所以释放结果存到线程变量
            if (StringUtil.equals(mapData.get("writeRedis"),BussinessDictCodeListRds.是否_否.getValue())){
                String ocpCode = (String) request.getBody().get("eventId");
                setServiceInfo(ocpCode,paraValMap);
                return ocpRelParam;
            }
            // 更新参数值信息
            paramValSrv.setParamVal(paraValMap);
            // 实占撤销的场合，直接删除实占信息
            // 预占撤销的场合，若无实占则删除
            if (isRevoke(mapData, isPreOcp, ocpRelParam) && !isDiffOcp(mapData)
                // 委托完成时不删除预占信息
                && !StringUtil.isNotEmpty(mapData.get(ETRCOMPLETEFLG))) {
                getOcpService().deleteInfoFromRedis(RedisKeyEnum.预占实占信息.key(), occupyCode + GlobalConstant.COLON + getOccupyType());
            }
            // 更新预占/实占信息
            else {
                setOccupyRelParam(ocpRelParam);
            }
            // 更新预占信息
            if (preOcpParam2 != null) {
                setOccupyRelParam(preOcpParam2);
            }
            // add by zhangrui1 start
            // 更新资产实时规模信息
            refreshAssetAmout();
            // add by zhangrui1 end
            return ocpRelParam;
        });
        // 更新DB
        saveData(mapData, false, getInfId());
        // 参数计算明细
        saveDetails(details, mapData, occupyCode);
        return null;
    }

    /**
     * 实占撤销的场合，直接删除实占信息<br>
     * 预占撤销的场合，若无实占则删除
     * 
     * @param mapData 报文
     * @param isPreOcp 是否预占
     * @param ocpRelParam 预占信息
     * @return true：删除预占实占信息
     */
    protected boolean isRevoke(Map<String, Object> mapData, boolean isPreOcp,
                             IOccupyRelParam ocpRelParam) {
        return (isRevoke(mapData) && !isPreOcp)
               || (isPreOcp && isRevoke(mapData)
                   && null == getServiceInfo(ocpRelParam.getField()));
    }

    protected IOccupyRelParam getPreOcpInfo(IOccupyRelParam ocpRelParam, boolean isPreOcp,
                                          Map<String, Map<String, IValue<Object>>> hashList,
                                          Map<String, Object> mapData)
        throws InterfException {
        Set<String> codeList = ocpRelParam.getOcpCodeList();
        IOccupyRelParam preOcpParam = null;
        if (!isPreOcp && !CollectionUtils.isEmpty(codeList)) {
            // add by zhangrui1 start
            codeList.remove(null);
            // add by zhangrui1 end
            String preCode = codeList.stream().findFirst().orElse(null);
            if (!StringUtil.isEmpty(preCode)) {
                preOcpParam = getOccupy(preCode, PRE_OCCUPY);
                hashList.putAll(
                    makeHashList(preOcpParam == null ? new HashMap<>() : preOcpParam.getItems(), mapData, false));
            }
        }
        // 预占撤销时若还存在实占信息则释放剩余额度
        else if (isPreOcp && isRevoke(mapData) && !CollectionUtils.isEmpty(codeList)) {
            // 设置预占下实占的总金额
            setAllOcpVal(ocpRelParam);
        }
        return preOcpParam;
    }

    /**
     * 构建取得参数值的条件KEY-FIELD
     * 
     * @param items 预占相关参数信息
     * @return 取得参数值的条件
     * @see
     * @since 1.0
     */
    protected Map<String, Map<String, IValue<Object>>> makeHashList(Map<String, IOcpRelParamItem> items,
                                                                    Map<String, Object> mapData,
                                                                    boolean isCheck)
        throws InterfException {
        Map<String, Map<String, IValue<Object>>> hashList = new HashMap<>();
        for(Map.Entry<String,IOcpRelParamItem> en:items.entrySet()) {
            String paraCode = en.getKey();
            IOcpRelParamItem item = en.getValue();
            // 预占实战对象
            String ocpObj = item.getOccupyItem();
            // 预占实战值
            BigDecimal ocpVal = item.getOccupyVal();
            Object rlsObj = mapData.get(ocpObj);
            // 释放的值
            BigDecimal rlsVal = new BigDecimal(rlsObj == null ? "0" : rlsObj.toString());
            // 若预占实占的值小于释放的值则抛出异常
            if (!isRevoke(mapData)
                && CommonUtil.compareV(ocpVal, rlsVal, BussinessDictCodeListRds.运算符_小于.getValue())
                && isCheck) {
                // modify by zhangrui1 start
                // 配合参数值计算后小于0时参数值设置为0，这里就必须允许部分释放大于预占实占时的值
                /*
                String type = getOccupyType() == PRE_OCCUPY ? "预占" : "实占";
                throw new InterfException(type + "释放的值超过" + type + "实际的值,释放失败！");
                */
                // mapData.put(ocpObj, ocpVal);
                // modify by zhangrui1 end
            }
            Set<String> fields = item.getRedisFields();
            Map<String, IValue<Object>> map = hashList.getOrDefault(paraCode, new HashMap<>());
            fields.forEach(str -> map.put(str, null));
            hashList.put(paraCode, map);
        }
        return hashList;
    }

    /**
     * 是否撤销预占/实占
     * 
     * @param mapData 请求数据
     * @return true:是
     */
    protected boolean isRevoke(Map<String, Object> mapData){
    	String revokeFlg = (String) mapData.get(REVOKEFLG);
        return CommonUtil.isType(revokeFlg, BussinessDictCodeListRds.是否_是);
    }
    
    /**
     * 计算参数值及预占额度
     * 
     * @param paraValMap 参数值列表
     * @param mapData 预占信息
     * @param items 预占相关参数信息
     * @param preOcpParam 实占时的预占信息
     * @see
     * @since 1.0
     */
    protected void calcParamVal(Map<String, Map<String, IValue<Object>>> paraValMap,
                                Map<String, Object> mapData, Map<String, IOcpRelParamItem> items,
                                List<RskIndicatorParaCalcdetail> details,
                                IOccupyRelParam preOcpParam) {
        Map<String, BigDecimal> offsetMap = new HashMap<>();
        paraValMap.entrySet().forEach(en -> {
            // 参数代码
            String key = en.getKey();
            // Map<Redis存储识别域, 参数值>
            Map<String, IValue<Object>> valMap = en.getValue();
            // 预占/实占参数信息
            IOcpRelParamItem item = items.get(key);
            if (item == null) {
                return;
            }
            // 预占/实占额度
            BigDecimal ocpVal = ArithmeticUtil.doAdd(item.getOccupyVal(), item.getOffsetVal());
            ocpVal = ocpVal == null ? BigDecimal.ZERO : ocpVal;
            // 预占/实占对象
            String relItem = item.getOccupyItem();
            // 释放的额度
            BigDecimal objVal = BigDecimal.ZERO;
            objVal = new BigDecimal(mapData.getOrDefault(relItem, BigDecimal.ZERO).toString());
            // 占比金额
            if (StringUtil.equals(relItem, MIXAMOUNTKEY)) {
                objVal = getMixAmount();
            }
            // 撤销的场合，直接释放全部
            if (isRevoke(mapData)) {
            	objVal = getLeftPreOcp(mapData.get(EVENT_ID) + GlobalConstant.COLON + getOccupyType(), key, ocpVal);
            	objVal = objVal.max(objVal);
            	mapData.put(relItem, objVal);
            }
            // 取反
            BigDecimal temp = objVal.negate();
            // 减掉释放的额度
            ocpVal = ocpVal.subtract(objVal);
            // 当预占释放后结果<0时，设置为0
            ocpVal = ocpVal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : ocpVal;
            // 计算参数值
            valMap.entrySet().forEach(valEn -> calcParam(mapData, item, valEn, temp, details));
            // add by zhangrui1 差分预占释放 start
            if (isDiffOcp(mapData) || isEtrCompleteY(mapData) || isEtrCompleteN(mapData)) {
                return;
            }
            // add by zhangrui1 差分预占释放 end
            // 设置预占/实占金额
            item.setOccupyVal(ocpVal);
            if (StringUtil.endsWith(key, BussinessDictCodeListRds.参数粒度_实时.getValue())) {
                offsetMap.put(key, temp);
            }
        });
        if (preOcpParam != null) {
            Map<String, IOcpRelParamItem> preItems = preOcpParam.getItems();
            paraValMap.entrySet().forEach(en -> {
                // 参数代码
                String key = en.getKey();
                // Map<Redis存储识别域, 参数值>
                Map<String, IValue<Object>> valMap = en.getValue();
                if (!preItems.containsKey(key)) {
                    return;
                }
                // 预占实占参数信息
                IOcpRelParamItem item = preItems.get(key);
                // 预占/实占操作符号
                String oper = item.getOccupyOper();
                String tKey = StringUtil.substring(key, 0, key.length() - 2)
                              + BussinessDictCodeListRds.参数粒度_实时.getValue();
                BigDecimal temp = offsetMap.get(tKey);
                BigDecimal offset = item.getOffsetVal();
                if (offset != null && offset.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal val = offset.min(temp.abs());
                    offset = offset.subtract(temp.abs());
                    offset = BigDecimal.ZERO.max(offset);
                    // 计算参数值
                    valMap.entrySet().forEach(valEn -> calcParam(key, valEn, val.negate(), oper, details));
                }
                item.setOffsetVal(offset);
            });
        }
    }

    /**
     * 取得 剩余预占额度<br>
     * 预占额度 - 全部实占额度
     * 
     * @param preOcpCode 预占编号
     * @param paraCode 参数代码
     * @param ocpVal 预占额度
     * @return 剩余预占额度
     */
    protected BigDecimal getLeftPreOcp(String preOcpCode, String paraCode, BigDecimal ocpVal) {
        return ocpVal == null ? BigDecimal.ZERO : ocpVal.subtract(getAllOcpVal(preOcpCode, paraCode));
    }

    // add by zhangrui1 差分预占释放 start
    protected void calcParam(Map<String, Object> mapData, IOcpRelParamItem item,
                             Entry<String, IValue<Object>> valEn, BigDecimal temp,
                             List<RskIndicatorParaCalcdetail> details) {
        BigDecimal diffVal = temp;
        // 如果数据差分预占释放
        if (isDiffOcp(mapData)) {
            diffVal = item.removeDiffOcp(valEn.getKey(), (String)mapData.get(DIFF_OCCUPY_FLAG));
            diffVal = diffVal.negate();
        }
        // add by zhangrui1 委托完成 start
        else if (isEtrCompleteY(mapData) && isRevoke(mapData)) {
            if (CollectionUtils.isEmpty(item.getDiffOcpMap())
                || !item.getDiffOcpMap().containsKey(PRE_LEFT_AMT)) {
                item.putDiffOcp(PRE_LEFT_AMT, PRE_LEFT_AMT, temp.negate(), false);
            }
        }
        else if (isEtrCompleteN(mapData) && isRevoke(mapData)) {
            diffVal = item.removeDiffOcp(PRE_LEFT_AMT, PRE_LEFT_AMT);
        }
        // add by zhangrui1 委托完成 end
        calcParam(item.getParamCode(), valEn, item.doFormula(diffVal), item.getOccupyOper(), details);
    }
    // add by zhangrui1 差分预占释放 end
    private void calcParam(String key, Entry<String, IValue<Object>> valEn, BigDecimal temp,
                           String oper, List<RskIndicatorParaCalcdetail> details) {
        // 计算明细
        RskIndicatorParaCalcdetail detail = new RskIndicatorParaCalcdetail();
        int index = StringUtil.indexOf(key, GlobalConstant.COLON);
        // 参数代码
        detail.setParaCode(StringUtil.substring(key, 0, index));
        // 参数粒度
        detail.setParaGranularity(StringUtil.substring(key, index + 1));
        IValue<Object> val = valEn.getValue();
        if (val != null) {
            // ----zxjjpei 更改类型------------------
            BigDecimal bVal = (BigDecimal)val.value();
            // 原值
            detail.setOldValue(bVal);
            bVal = CommonUtil.math(bVal, temp, oper);
            val.set(bVal);
        }
        else {
            val = new Value<>(
                str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
            BigDecimal bVal = CommonUtil.math(BigDecimal.ZERO, temp, oper);
            val.set(bVal);
            // 原值
            detail.setOldValue(BigDecimal.ZERO);
        }
        // 新值
        detail.setNewValue((BigDecimal)val.value());
        String[] keyArr = StringUtil.split(valEn.getKey(), GlobalConstant.COMMA);
        // 设置产品代码
        detail.setPortfolioId(keyArr != null && keyArr.length > 0 ? keyArr[0] : "");
        // 设置资产代码
        detail.setAssetCode(keyArr != null && keyArr.length > 1 ? keyArr[1] : "");
        // 变化值
        detail.setChangeValue(CommonUtil.math(BigDecimal.ZERO, temp, oper));
        details.add(detail);
    }

    protected int getOccupyType() {
        return PRE_OCCUPY;
    }

    @Override
    protected boolean isReleaseOcp() {
        return true;
    }

    /**
     * 获取 busiService
     * 
     * @return busiService.
     */
    public OccupyDbService getBusiService() {
        return busiService;
    }

    /**
     * 将实占/预占释放信息保存到DB
     * 
     * @param mapDatas 实占/预占释放信息
     * @param isApprove 是否审批状态
     * @param user 用户
     * @throws InterfException 异常
     * @see
     * @since 1.0
     */
    protected abstract void saveData(Map<String, Object> mapDatas, boolean isApprove, String user)
        throws InterfException;
}

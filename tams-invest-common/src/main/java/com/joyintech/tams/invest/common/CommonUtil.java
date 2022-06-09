/*
 * 文件名：CommonUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年10月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.*;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.common.entity.Value;
import com.joyintech.redis.lock.Locker;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicator;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndividLimit;
import com.joyintech.tams.invest.common.riskinfrds.param.entity.IParam;
import com.joyintech.tams.invest.common.risktrial.util.RiskUtil;
import org.springframework.util.CollectionUtils;

/**
 * 组件和微服务使用的业务相关工具类
 *
 * @author 何阳阳
 * @version 1.0 2018年10月23日
 * @see CommonUtil
 * @since 1.0
 */
public final class CommonUtil {

    private CommonUtil() {

    }

    /**
     * 现金流代码生成
     * @param busiNo 业务代码
     * @param busiType 业务类型
     * @param branch 分支序号
     * @param busiType2 现金流二级类型
     * @return 现金流代码
     */
    public static String generateCashFlowCode(String busiNo, String busiType, long branch,
                                              String busiType2) {
        final String sepChar = String.valueOf((char)256);

        String tmpStr = StringUtil.joinString(
            Arrays.asList(StringUtil.defaultString(busiNo), StringUtil.defaultString(busiType),
                String.valueOf(branch), StringUtil.defaultString(busiType2)),
            sepChar);

        return EncryptUtil.getInstance().md5(tmpStr);
    }

    /**
     * 获取数据字典明细
     *
     * @param dictCode 数据字典代码
     * @param itemValue 字典明细参数值
     * @return 数据字典明细
     */
    public static DataDictItem getDataDictItemByName(String dictCode, String itemValue) {
        if (StringUtil.isNotEmpty(itemValue)) {
            List<? extends DataDictItem> items = SysDataUtil.getDataDictItem(dictCode);
            if (null != items) {
                return items.stream().filter(
                        item -> StringUtil.equals(item.getItemValue(), itemValue)).findFirst().orElse(
                        null);
            }
        }
        return null;
    }

    /**
     * 判断数据字典明细类型
     *
     * @param type 判断对象
     * @param dict 目标类型
     * @return <b>true:</b> 是目标类型</br>
     *         <b>false:</b>不是目标类型
     * @see
     * @since 1.0
     */
    public static final boolean isType(String type, BaseDictCodeList dict) {
        return StringUtil.equals(type, dict.getValue());
    }

    /**
     * 比较字符串大小
     *
     * @param str1 被比较字符串
     * @param str2 比较字符串
     * @param type 预期大小关系（<=、>、>=、<、=、IN、NO）
     * @return <b>true:</b> 符合预期</br>
     *         <b>false:</b>不符合预期
     * @see
     * @since 1.0
     */
    public static final boolean compareStr(String str1, String str2, String type) {
        if (isType(type, BussinessDictCodeListRds.运算符_不包含)) {
            return !StringUtil.contains(addComma(str2), addComma(str1));
        }
        if (isType(type, BussinessDictCodeListRds.运算符_包含)) {
            return StringUtil.contains(addComma(str2), addComma(str1));
        }
        return compareV(str1, str2, type);
    }

    private static String addComma(String str) {
        return GlobalConstant.COMMA + str + GlobalConstant.COMMA;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <K> Object findValue(Map<K, Object> dataMap, K key) {
        if (CollectionUtils.isEmpty(dataMap)) {
            return null;
        }
        if (dataMap.containsKey(key)) {
            return dataMap.get(key);
        }
        for(Entry<K, Object> en : dataMap.entrySet()) {
            Object v = en.getValue();
            if (v instanceof Map) {
                Object obj = findValue((Map) v, key);
                if (obj != null) {
                    return obj;
                }
            }
        }
        return null;
    }

public static final BigDecimal getBigDecimal(Map<String, Object> map, String key,
                                                 BigDecimal defVal) {
        Object obj = map.get(key);
        if (obj == null) {
            return defVal;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal)obj;
        }
        return new BigDecimal(obj.toString());
    }

    @SuppressWarnings("unchecked")
    public static final Map<String, Object> toMap(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>)obj;
        }
        else {
            return BeanUtil.object2Map(obj);
        }
    }
    /**
     * 比较大小
     *
     * @param obj1 被比较对象
     * @param obj2 比较对象
     * @param type 预期大小关系（<=、>、>=、<、=）
     * @return <b>true:</b> 符合预期</br>
     *         <b>false:</b>不符合预期
     * @see
     * @since 1.0
     */
    public static final <V> boolean compareV(Comparable<V> obj1, V obj2, String type) {
        if (obj1 == null || obj2 == null) {
            return false;
        }
        int res = obj1.compareTo(obj2);
        if (res > 0) {
            return isType(type, BussinessDictCodeListRds.运算符_大于)
                    || isType(type, BussinessDictCodeListRds.运算符_大于等于)
                    || isType(type, BussinessDictCodeListRds.运算符_不等于);
        }
        if (res < 0) {
            return isType(type, BussinessDictCodeListRds.运算符_小于)
                    || isType(type, BussinessDictCodeListRds.运算符_小于等于)
                    || isType(type, BussinessDictCodeListRds.运算符_不等于);
        }
        return isType(type, BussinessDictCodeListRds.运算符_小于等于)
                || isType(type, BussinessDictCodeListRds.运算符_大于等于)
                || isType(type, BussinessDictCodeListRds.运算符_等于);
    }

    public static final BigDecimal math(BigDecimal val1, BigDecimal val2, String type) {
        if (isType(type, BussinessDictCodeListRds.操作符_加)) {
            return ArithmeticUtil.doAdd(val1, val2);
        }
        if (isType(type, BussinessDictCodeListRds.操作符_减)) {
            return ArithmeticUtil.subtract(val1, val2);
        }
        if (isType(type, BussinessDictCodeListRds.操作符_乘)) {
            int scale = val1.scale();
            // modify by etliu 20210831 start
//            return ArithmeticUtil.multiply(val1, val2).setScale(scale, RoundingMode.HALF_UP);
            BigDecimal tempNum = ArithmeticUtil.multiply(val1, val2);
            if (tempNum != null) {
                tempNum = tempNum.setScale(scale, RoundingMode.HALF_UP);
            }
            return tempNum;
            // modify by etliu 20210831 end
        }
        if (isType(type, BussinessDictCodeListRds.操作符_除)) {
            int scale = val1.scale();
            return ArithmeticUtil.divide(val1, val2, scale, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    /**
     * 取得 指标的分母参数
     *
     * @param ind 指标信息
     * @return 分母参数
     */
    public static final String getIndicatorFm(IIndicator ind){
        String div = "/";
        if (ind == null) {
            return StringUtil.EMPTY;
        }
        String expr = ind.getExprGrad();
        if (StringUtil.isEmpty(expr) || !StringUtil.contains(expr, div)) {
            return StringUtil.EMPTY;
        }
        return StringUtil.split(expr, div)[1];
    }
    /**
     * 将message中的指标参数代码替换为指标参数名称
     *
     * @param msg message
     * @param paraMap 指标参数列表
     * @return 替换后的message
     */
    public static final String replaceMsgWithName(String msg, Map<String, IParam> paraMap){
        String start = "${";
        String end = "}";
        if (!CollectionUtils.isEmpty(paraMap) && StringUtil.contains(msg, start)) {
            for(Map.Entry<String, IParam> en : paraMap.entrySet()) {
                String paraCode = en.getKey();
                paraCode = start + paraCode + end;
                if (StringUtil.contains(msg, paraCode)) {
                    msg = StringUtil.replace(msg, paraCode, en.getValue().getParaName());
                }
                else if (!StringUtil.contains(msg, start)) {
                    break;
                }
            }
        }
        return msg;
    }

    /**
     * 汇率换算（外币换算人名币）
     *
     * @param amout 需要换算的金额
     * @param exchangeVal 汇率
     * @return 换算的结果
     */
    public static final BigDecimal exchangeToCny(BigDecimal amout, BigDecimal exchangeVal){
        if (amout == null) {
            return BigDecimal.ZERO;
        }
        if (exchangeVal == null) {
            return amout;
        }
        return math(amout, exchangeVal, BussinessDictCodeListRds.操作符_乘.getValue());
    }

    public static String convertKriVal(Object val, IIndividLimit ind) {
        if (val == null) {
            return "";
        }
        BigDecimal resVal = null;
        if (val instanceof BigDecimal) {
            resVal = (BigDecimal) val;
        }
        else {
            resVal = new BigDecimal(val.toString());
        }
        String approval = ind.getApproveCond();
        String warn = ind.getWarnCond();
        String foul = ind.getFoulCond();
        String unitInStr = null;
        if (StringUtil.isNotEmpty(approval)) {
            unitInStr = approval;
        }
        else if (StringUtil.isNotEmpty(warn)) {
            unitInStr = warn;
        }
        else if (StringUtil.isNotEmpty(foul)) {
            unitInStr = foul;
        }
        else {
            return StringUtil.numberToString(resVal);
        }
        String unit = "";
        // %
        if (StringUtil.contains(unitInStr, BussinessDictCodeListRds.单位_PERCENT.getValue())) {
            resVal = CommonUtil.math(resVal, new BigDecimal(100), BussinessDictCodeListRds.操作符_乘.getValue());
            unit = RiskUtil.convertValueToUnit(BussinessDictCodeListRds.单位_PERCENT.getValue());
        }
        // BP
        else if (StringUtil.contains(unitInStr, BussinessDictCodeListRds.单位_BP.getValue())) {
            resVal = CommonUtil.math(resVal, new BigDecimal(10000), BussinessDictCodeListRds.操作符_乘.getValue());
            unit = RiskUtil.convertValueToUnit(BussinessDictCodeListRds.单位_BP.getValue());
        }
        // 万元
        else if (StringUtil.contains(unitInStr, BussinessDictCodeListRds.单位_万元.getValue())) {
            resVal = ArithmeticUtil.divide(resVal, new BigDecimal(10000), 6, RoundingMode.HALF_UP);
            unit = RiskUtil.convertValueToUnit(BussinessDictCodeListRds.单位_万元.getValue());
        }
        // 元
        else if (StringUtil.contains(unitInStr, BussinessDictCodeListRds.单位_元.getValue())) {
            unit = RiskUtil.convertValueToUnit(BussinessDictCodeListRds.单位_元.getValue());
        }
        // 年
        else if (StringUtil.contains(unitInStr, BussinessDictCodeListRds.单位_年.getValue())) {
            unit = RiskUtil.convertValueToUnit(BussinessDictCodeListRds.单位_年.getValue());
        }
        // 日
        else if (StringUtil.contains(unitInStr, BussinessDictCodeListRds.单位_日.getValue())) {
            unit = RiskUtil.convertValueToUnit(BussinessDictCodeListRds.单位_日.getValue());
        }
        if(null != resVal){
            resVal = resVal.setScale(4,BigDecimal.ROUND_HALF_UP);
        }
        return StringUtil.numberToString(resVal) + unit;
    }
    /**
     * 业务处理，每5ms轮询取锁，当获取锁时执行，否则等待
     *
     * @param helper 处理逻辑接口
     * @return 处理结果
     * @see
     * @since 1.0
     */
    public static <T> T handleByLock(DoHelper<T> helper) {
        T t = null;
        while (true) {
            // 如果能获取锁则执行处理，否则阻塞
            if (Locker.lock()) {
                try {
                    t = helper.handle();
                }
                finally {
                    // 释放锁
                    Locker.unLock();
                }
                break;
            }
            try {
                Thread.sleep(5);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return t;
    }

    public interface DoHelper<T> {

        T handle();
    }

    // add by zhangrui1 start
    public static final <V> void removeNull(Map<String, V> mapData) {
        if (CollectionUtils.isEmpty(mapData)) {
            return;
        }
        Iterator<Entry<String, V>> it = mapData.entrySet().iterator();
        while(it.hasNext()) {
            Entry<String, V> en = it.next();
            if (null == en.getValue()) {
                it.remove();
            }
        }
    }

    /**
     * 过滤Map记录
     *
     * @param map 数据源
     * @param filter 过滤条件
     * @return 过滤结果
     */
    public static final <T, P> Map<T, P> mapFilter(Map<T, P> map, Predicate<Entry<T, P>> filter) {
        if (CollectionUtils.isEmpty(map) || filter == null) {
            return map;
        }
        return map.entrySet().stream().filter(filter).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public static final <K, V> Map<K, V> copyToNew(Map<K, V> map, Function<V, V> toNewV) {
        Map<K, V> newMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(map)) {
            map.entrySet().forEach(en -> {
                K key = en.getKey();
                V v = en.getValue();
                if (v == null) {
                    return;
                }
                V newV = toNewV.apply(v);
                if (newV == null) {
                    return;
                }
                newMap.put(key, newV);
            });
        }
        return newMap;
    }

    public static final Map<String, Map<String, IValue<BigDecimal>>> copyToNew(Map<String, Map<String, IValue<BigDecimal>>> valMap) {
        return copyToNew(valMap, map -> {
            if (CollectionUtils.isEmpty(map)) {
                return null;
            }
            return copyToNew(map, val -> {
                IValue<BigDecimal> newVal = new Value<>(
                        str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                newVal.set(val.value());
                return newVal;
            });
        });
    }

    public static final <T, P extends Collection<T>> Collection<T> filter(Collection<T> list,
                                                                          Class<P> beanClass,
                                                                          Predicate<T> filter) {
        if (CollectionUtils.isEmpty(list) || null == filter) {
            return list;
        }
        return list.stream().filter(filter).collect(
                Collectors.toCollection(() -> newInstance(beanClass)));
    }

    public static final <P> P newInstance(Class<P> beanClass) {
        try {
            return beanClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 分批次执行
     *
     * @param dtoList 对象
     * @param offset 每次执行数量
     * @param consumer 执行内容
     */
    public static final <V> void doBatchList(List<V> dtoList, int offset,
                                             Consumer<List<V>> consumer) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        // 按设定的offset分批次执行
        int size = dtoList.size();
        int tail = size % offset;
        if (tail != 0) {
            consumer.accept(dtoList.subList(0, tail));
        }
        int len = size / offset;
        for (int i = 0; i < len; i++ ) {
            int start = tail + i * offset;
            consumer.accept(dtoList.subList(start, start + offset));
        }
    }

    // add by zhangrui1 start
    /**
     * 从报文体中取出列表参数
     *
     * @param mapData 报文数据
     * @param key key
     * @return list内容
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final List<Map> getList(Map<String, Object> mapData, String key) {
        if (CollectionUtils.isEmpty(mapData)) {
            return new ArrayList<>();
        }
        Object list = mapData.get(key);
        if (list instanceof List) {
            return (List<Map>)list;
        }
        if (list instanceof String) {
            String listStr = (String)list;
            try {
                return JsonUtils.json2List(listStr, Map.class);
            }
            catch (IOException e) {
                //
            }
        }
        return new ArrayList<>();
    }
    // add by zhangrui1 end
}

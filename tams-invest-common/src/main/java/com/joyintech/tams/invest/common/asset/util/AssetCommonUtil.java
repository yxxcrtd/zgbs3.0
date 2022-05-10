package com.joyintech.tams.invest.common.asset.util;

import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.asset.annotation.DictEnum;
import com.joyintech.tams.invest.common.asset.annotation.DictList;
import com.joyintech.tams.invest.common.asset.annotation.Number;
import com.joyintech.tams.invest.common.asset.dao.AssetGenerateMapper;
import com.joyintech.tams.invest.common.asset.dto.AssetGenerateEntry;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.common.riskinfrds.calendar.entity.ICalendar;
import com.joyintech.tams.invest.common.riskinfrds.calendar.service.CalendarService;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetInfo;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetInfoRedisService;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 金融产品共通Util
 * @author 裴家俊
 * @version 1.0 2021年7月20日
 * @see AssetCommonUtil
 * @since 1.0
 */
@Service
public class AssetCommonUtil {

    private static AssetGenerateMapper assetGenerateMapper;

    private static AssetInfoRedisService assetInfoSrc;

    private static CalendarService calendarSrc;

    private static Map<String,BigDecimal> faceValMap=new HashMap<>();

    private static Map<String,Boolean> isOverBondMap=new HashMap<>();

    public static String COUPON_SPECIES="COUPON_SPECIES";

    public static String CCY="CCY";

    public static String IDATE="VDATE";

    public static String ISSUE_AMT="ISSUE_AMT";

    public static String 证券账号="ACCT_NO";

    public static String 登记机构="REGIST_ORG";

    public static String 估价净价="NETPRICE";

    public static String 产品净资产="TDY_ASSET_NETVALUE";

    private static String 境外="02";

    private static String[] KEY_ARRAY=new String[]{"finprodIdList","portfolioIdList","valDate","FINPROD_TYPE_TG_01"};

    private static List<String> astTypeList=Arrays.asList(DictCodeListExt1.金融产品类型_债券.getValue(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());

    public static List<String> couponTypeList=Arrays.asList(DictCodeList.息票品种_贴现.getValue(), DictCodeList.息票品种_零息.getValue());

    public static List<String> openTypeList=Arrays.asList(DictCodeList.开放模式_周期开放.getValue(),DictCodeList.开放模式_定期开放.getValue(),DictCodeList.开放模式_高频开放.getValue());

    /**
     * redis数据初始化
     */
    private static RiskRedisInitService<ImplCond> redisRriter;

    /**
     * 获取标的面值
     * @param finProdId
     * @return
     */
    private static void getFaceValDb(String finProdId){
        AssetGenerateEntry astEntry=assetGenerateMapper.qryFaceOrOtherInf(finProdId);
        BigDecimal faceVal=StringUtil.isNotEmpty(astEntry)?convert(astEntry.getFaceValue(),BigDecimal.class,obj->new BigDecimal(obj.toString())):null;
        faceValMap.put(finProdId,StringUtil.isNotEmpty(faceVal)?faceVal:new BigDecimal(100));
    }

    /**
     * 获取标的是否是境外债
     * @param finProdId
     */
    private static void isOverSeasBondDb(String finProdId){
        AssetGenerateEntry astEntry=assetGenerateMapper.qryFaceOrOtherInf(finProdId);
        if(StringUtil.isNotEmpty(astEntry)){
            if(astTypeList.contains(astEntry.getFinprodType2())&&StringUtil.equals(astEntry.getBlnArea(),境外)){
                isOverBondMap.put(finProdId,true);
            }
        }
    }

    /**
     * 获取标的面值（优先从redis中获取）
     * @param finProdId
     * @return
     */
    public static BigDecimal getFaceValRds(String finProdId){
        Map<String, IAssetInfo> mapAsset = assetInfoSrc.getMapFromRedis(RedisKeyEnum.资产基本信息.key(), Arrays.asList(finProdId));
        for(IAssetInfo iAst:mapAsset.values()){
            //return StringUtil.isNotEmpty(iAst.getFaceValue())?iAst.getFaceValue():new BigDecimal(100);
            return new BigDecimal(100);
        }
        return getFaceVal(finProdId);
    }

    /**
     * 获取标的信息(redis)，可自定义获取标的里各字段
     * @param func
     * @param finProdId
     * @param <R>
     * @return
     */
    public static <R> R getAssetInfo(String finProdId,Function<IAssetInfo,R> func){
        if(StringUtil.isBlank(finProdId)){
            return null;
        }
        IAssetInfo iAssetInfo = assetInfoSrc.getInfoFromRedis(RedisKeyEnum.资产基本信息.key(), finProdId);
        R r=null;
        if(!StringUtil.isNotEmpty(iAssetInfo)&&StringUtil.isNotEmpty(finProdId)){
            //若redis取不到从数据库取
            ImplCond cond=new ImplCond();
            cond.setDel(false);
            cond.setPlateDate(DateUtil.parse(DateUtil.getSysDate()));
            cond.setAssetIdArray(finProdId);
            redisRriter.dealAssetData(cond);
            iAssetInfo=assetInfoSrc.getInfoFromRedis(RedisKeyEnum.资产基本信息.key(), finProdId);
        }
        r= StringUtil.isNotEmpty(iAssetInfo)?func.apply(iAssetInfo):null;

        return r;
    }

    /**
     * 获取标的信息(从DB)，可自定义获取标的里各字段
     * @param func
     * @param finProdId
     * @param <R>
     * @return
     */
    public static <R> R getAssetInfoDb(String finProdId,Function<AssetGenerateEntry,R> func){
        if(StringUtil.isBlank(finProdId)){
            return null;
        }
        AssetGenerateEntry astEntry=assetGenerateMapper.qryFaceOrOtherInf(finProdId);
        R r=null;
        if(StringUtil.isNotEmpty(astEntry)){
            r=func.apply(astEntry);
        }
        return r;
    }

    /**
     * 获取交易信息(从DB)，可自定义获取交易信息
     * @param tradeList
     * @param finprodList
     * @param func
     * @param <R>
     * @return
     */
    public static <R> R getTradeInfo(List<String> tradeList,List<String> finprodList,Function<List<AssetGenerateEntry>,R> func){
        if(StringUtil.isEmptyList(finprodList)&&StringUtil.isEmptyList(tradeList)){
            return null;
        }
        List<AssetGenerateEntry> trdList=assetGenerateMapper.getTradeInfo(tradeList,finprodList);
        R r=null;
        if(!StringUtil.isEmptyList(trdList)){
            r=func.apply(trdList);
        }
        return r;
    }

    /**
     * 获取组合信息，可自定义获取标的里各字段
     * @param func
     * @param finProdId
     * @param <R>
     * @return
     */
    public static <R> R getPortfolioInfo(String portfolioId,Function<AssetGenerateEntry,R> func){
        if(StringUtil.isBlank(portfolioId)){
            return null;
        }
        AssetGenerateEntry astEntry=assetGenerateMapper.qryPortfolioIdInfo(portfolioId);
        R r=null;
        if(StringUtil.isNotEmpty(astEntry)){
            r=func.apply(astEntry);
        }
        return r;
    }

    /**
     * 获取日历信息
     * @param calendarId
     * @param <R>
     * @return
     */
    public static <R> R getCalendar(String calendarId,Function<ICalendar,R> func) {
        if(StringUtil.isBlank(calendarId)){
            return null;
        }
        ICalendar iCalendar=calendarSrc.getCalendar(calendarId);
        if(!StringUtil.isNotEmpty(iCalendar)){
            //若redis取不到从数据库取
            ImplCond cond=new ImplCond();
            cond.setDel(false);
            cond.setPlateDate(DateUtil.parse(DateUtil.getSysDate()));
            cond.setCalendarIdArray(calendarId);
            redisRriter.dealCalendarData(cond);
            iCalendar=calendarSrc.getCalendar(calendarId);
        }
        R r=null;
        if(StringUtil.isNotEmpty(iCalendar)){
            r=func.apply(iCalendar);
        }
        return r;
    }

    /**
     * 获取标的面值,未获取到默认100
     * @param key
     * @return
     */
    public static BigDecimal getFaceVal(String key) {
        if(StringUtil.isEmpty(key)){
            return new BigDecimal(100);
        }
        return getAstCommon(key,faceValMap,obj->getFaceValDb(obj));
    }

    /**
     * 获取标的境外债标识，true代表是境外债
     * @param key
     * @return
     */
    public static Boolean isOverBond(String key){
        if(StringUtil.isEmpty(key)){
            return false;
        }
        Boolean ret= getAstCommon(key,isOverBondMap,obj->isOverSeasBondDb(obj));
        return StringUtil.isNotEmpty(ret)?ret:false;
    }

    private static <R> R getAstCommon(String key, Map<String,R> map, Consumer<String> consumer){
        if (CollectionUtils.isEmpty(map) ||!StringUtil.isNotEmpty(map.get(key))) {
            consumer.accept(key);
        }
        return map.get(key);
    }

    /**
     * 汇率转换（转成人民币）
     * @param ccy
     * @param finProdId
     * @param valDate
     * @return
     */
    public static BigDecimal exchangeRate(String ccy,String finProdId, Date valDate){
        if(StringUtil.isEmpty(ccy)&&StringUtil.isNotEmpty(finProdId)){
            ccy=AssetCommonUtil.getAssetInfoDb(finProdId,obj->obj.getCcy());
        }
        if(StringUtil.isEmpty(ccy)||StringUtil.equals(ccy,DictCodeList.币种_人民币_CNY.getValue())){
            return BigDecimal.ONE;
        }
        BigDecimal rate= assetGenerateMapper.getExchangeRate(ccy, DictCodeList.币种_人民币_CNY.getValue(),valDate);
        if(StringUtil.isNotEmpty(rate)){
            return rate;
        }else {
            return BigDecimal.ONE;
        }
    }

    /**
     * 比较指定天数产品投资周期（目前按产品需求以赎回计划优先计算)
     * @param purPlanList   画面传过来的申购计划
     * @param purRule   画面传过来的申购基本要素
     * @param repPlanList   画面传过来的赎回计划
     * @param repRule   画面传过来的赎回基本要素
     * @param compare   比如判断是否大于90天，传90
     * @param <T>
     * @param <K>
     * @return  备注 小于返回-1，等于返回0，大于返回1
     */
    public static <T extends FinOpenPlan,K extends FinOpenRule> Integer comparePtlCycle(List<T> purPlanList,K purRule,
                                                                                        List<T> repPlanList,K repRule,Integer compare){
        if((Objects.isNull(purRule)&&Objects.isNull(repRule))||null==compare){
            return null;
        }
        if(!checkNull(purPlanList,purRule)||!checkNull(purPlanList,purRule)){
            return null;
        }
        //按产品需求以赎回计划优先处理,若是申赎同频，则就以申购计划为准
        if((!Objects.isNull(purRule)&&CommonUtil.isType(purRule.getOpenType(),DictCodeList.申赎开放类型_申赎开放))
                ||Objects.isNull(repRule)||StringUtil.isBlank(repRule.getOpenMode())){
            repPlanList=purPlanList;
            repRule=purRule;
        }
        //若为封闭式退出
        if(!openTypeList.contains(repRule.getOpenMode())){
            return null;
        }
        //若是高频开放的产品默认日开
        if(CommonUtil.isType(repRule.getOpenMode(),DictCodeList.开放模式_高频开放)){
            return BigDecimal.ONE.compareTo(new BigDecimal(compare));
        }
        //获取投资周期
        Long days=getPtlInvestPeriod(repPlanList,repRule);
        return null==days?null:days.compareTo(compare.longValue());
    }

    /**
     * 比较指定天数产品投资周期（目前按产品需求以赎回计划优先计算）
     * @param ptlList 指定产品(支持多个)
     * @param compare 比如判断是否大于90天，传90
     * @return 备注 KEY 产品代码 VALUE：小于返回-1，等于返回0，大于返回1
     */
    public static Map<String,Integer> comparePtlCycle(List<String> ptlList,Integer compare){
        if(StringUtil.isEmptyList(ptlList)||null==compare){
            return null;
        }
        //查询开放规则表
        List<FinOpenRule> finOpenRuleList=assetGenerateMapper.getFinOpenRule(ptlList.stream().map(item->{
            item=DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()+GlobalConstant.UNDER_LINE+item;
            return item;
        }).collect(Collectors.toList()));
        if(StringUtil.isEmptyList(finOpenRuleList)){
            return null;
        }
        //以产品分组为key,value为规则基本信息List
        Map<String,List<FinOpenRule>> ptlRuleMap = finOpenRuleList.stream().collect(JoyinCollectors.groupingBy(x->StringUtil.replaceAll(x.getCashId(),
                DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()+GlobalConstant.UNDER_LINE,StringUtil.EMPTY)));

        //查询开放计划表
        List<FinOpenPlan> ptlPlanList = assetGenerateMapper.getFinOpenPlan(ptlList.stream().map(item->{
            item=DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()+GlobalConstant.UNDER_LINE+item;
            return item;
        }).collect(Collectors.toList()));
        //以产品分组为key,value为开放赎回计划List
        Map<String,List<FinOpenPlan>> ptlPlanMap = StringUtil.isEmptyList(ptlPlanList)?null:ptlPlanList.stream().collect(JoyinCollectors.groupingBy(x->StringUtil.replaceAll(x.getFinprodId(),
                DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()+GlobalConstant.UNDER_LINE,StringUtil.EMPTY)));

        Map<String,Integer> retMap=new HashMap<>();
        ptlRuleMap.forEach((k,vRuleList)->{
            //若是高频开放的产品默认日开(目前产品设计高频开放没有计划信息)
            if(!StringUtil.isEmptyList(vRuleList)&&CommonUtil.isType(vRuleList.get(0).getOpenMode(),DictCodeList.开放模式_高频开放)){
                retMap.put(k,BigDecimal.ONE.compareTo(new BigDecimal(compare)));
                return;
            }
            //若没有计划跳出当前循环
            if(CollectionUtils.isEmpty(ptlPlanMap)||StringUtil.isEmptyList(ptlPlanMap.get(k))){
                retMap.put(k,null);
            }
            Long days=getPtlInvestPeriod(ptlPlanMap.get(k),vRuleList.get(0));
            retMap.put(k,null==days?null:days.compareTo(compare.longValue()));
        });
        return retMap;
    }

    /**
     * 获取产品投资周期
     * @param tPlanList  需要处理的计划(开放或赎回计划)
     * @return
     * 备注：计算相邻两个开放计划的赎回确认日的时间间隔，当日期差小于N天时即周期小于N天；
     * 当没有赎回计划时，则计算相邻两个开放计划的申购确认日的时间间隔；若3)	当【生成计划】的只包含1条数据时，
     * 以【开放计划信息】模块中【推算方法】字段的信息确定开始日，进而确定周期。
     * 则以【起息日】字段作为“开始日”，用第1条记录的【赎回确认日】减去“开始日”确定周期，无【赎回确认日】时采用【申购确认日】进行计算
     */
    public static <T extends FinOpenPlan,F extends FinOpenRule> Long getPtlInvestPeriod(List<T> tPlanList,F tRule){
        if(StringUtil.isEmptyList(tPlanList)){
            return null;
        }
        //处理开放计划只有一条的情况
        if(1==tPlanList.size()){
            return DateUtil.getBetweenDayNumber(getAssetInfo(tPlanList.get(0).getFinprodId(),obj->obj.getVdate()),
                    tPlanList.get(0).getOpenVdate());
        }
        //若是1条以上则用后一条计划减去前一条再取最小值
        List<Long> daysList = new ArrayList<>();
        tPlanList.stream().reduce((pre,cru)->{
            daysList.add(DateUtil.getBetweenDayNumber(pre.getOpenVdate(),
                    cru.getOpenVdate()));
            return cru;
        });
        /*RiskUtil.dealList(tPlan, new ArrayList<>(),(list)->{
            return DateUtil.getBetweenDayNumber(list.get(0).getOpenVdate(),
                    list.get(1).getOpenVdate());
        }, (list) -> {
            daysList.addAll(list);
        });*/
        if (!StringUtil.isEmptyList(daysList)) {
            return Collections.min(daysList);
        }
        return null;
    }

    /**
     * 获取行情
     * @param keyList
     * @param valDate
     * @param predicate (没有过滤传Null)
     * @param func
     * @param <R>
     * @return
     */
    public static <R> List<R> getBondValuation(List<String> keyList, Date valDate, Predicate<Map> predicate,Function<Map,R> func){
        if(StringUtil.isEmptyList(keyList)||StringUtil.isEmptyList(keyList.stream().filter(item->StringUtil.isNotEmpty(item)).collect(Collectors.toList()))){
            return null;
        }
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put(KEY_ARRAY[0],keyList);
        paramMap.put(KEY_ARRAY[2],valDate);
        paramMap.put(KEY_ARRAY[3],DictCodeListExt1.金融产品类型_投管用_债券.getValue()+ GlobalConstant.UNDER_LINE);
        List<Map> listMap=assetGenerateMapper.getBondValuation(paramMap);
        return assembleData(listMap,predicate,func);
    }

    /**
     * 获取产品估值数据
     * @param keyList
     * @param valDate
     * @param predicate (没有过滤传Null)
     * @param func
     * @param <R>
     * @return
     */
    public static <R> List<R> getAstNetVal(List<String> keyList, Date valDate, Predicate<Map> predicate,Function<Map,R> func){
        if(StringUtil.isEmptyList(keyList)||StringUtil.isEmptyList(keyList.stream().filter(item->StringUtil.isNotEmpty(item)).collect(Collectors.toList()))){
            return null;
        }
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put(KEY_ARRAY[1],keyList);
        paramMap.put(KEY_ARRAY[2],valDate);
        List<Map> listMap=assetGenerateMapper.getAstNetVal(paramMap);
        return assembleData(listMap,predicate,func);
    }

    /**
     * 获取产品证券账户
     * @param keyList
     * @param predicate (没有过滤传Null)
     * @param func
     * @param <R>
     * @return
     */
    public static <R> List<R> getStlAccount(List<String> keyList,Predicate<Map> predicate,Function<Map,R> func){
        if(StringUtil.isEmptyList(keyList)||StringUtil.isEmptyList(keyList.stream().filter(item->StringUtil.isNotEmpty(item)).collect(Collectors.toList()))){
            return null;
        }
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put(KEY_ARRAY[1],keyList);
        List<Map> listMap=assetGenerateMapper.getStlAccount(paramMap);
        return assembleData(listMap,predicate,func);
    }

    private static <T,R> List<R> assembleData(List<T> listMap,Predicate<T> predicate,Function<T,R> func){
        List<R> rList=new ArrayList<>();
        if(!StringUtil.isEmptyList(listMap)){
            List<T> filterList=listMap.stream().filter(item->StringUtil.isNotEmpty(predicate)?predicate.test(item):true).collect(Collectors.toList());
            filterList.stream().forEach(item->{
                R r=func.apply(item);
                rList.add(r);
            });
        }
        return rList;
    }

    public static <T extends FinProduct> void setIsOverBondMap(T t) {
        if(StringUtil.isNotEmpty(t.getFinprodId())&&astTypeList.contains(t.getFinprodType2())&&StringUtil.equals(t.getBlnArea(),境外)) {
            isOverBondMap.put(t.getFinprodId(),true);
        }
    }

    public static <T extends FinProductAdd> void setFaceValMap(T t) {
        if(StringUtil.isNotEmpty(t.getFinprodId())&&StringUtil.isNotEmpty(t.getFaceValue())) {
            faceValMap.put(t.getFinprodId(), t.getFaceValue());
        }
    }

    public static BigDecimal getFaceValMap(String key) {
        return faceValMap.get(key);
    }

    /**
     * Entity转化成指定行数据
     * @param data 指定行数据
     * @param clz Entity类型
     * @return Object[]
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public static <T> Object[] parseEntryToArray(Object[] data,T entity,Class<T> clz)
            throws BusinessException {
        // 数据为空或者受体类不存在时返回空
        if (data == null || data.length == 0 || clz == null) {
            return null;
        }
        // 实例化受体类
        try {
            // 受体类的方法列表
            Method[] fList = clz.getMethods();
            for (Method md : fList) {
                // 受体类的方法注解
                Number idx = md.getAnnotation(Number.class);
                // 受体类的方法不是get方法或者没有注解时跳过
                if (!StringUtil.startsWith(md.getName(), "get") || idx == null) {
                    continue;
                }
                int i = idx.value();
                String returnType = md.getGenericReturnType().getTypeName();
                Object retObj=md.invoke(entity);
                if (StringUtil.equals(returnType, Date.class.getName())) {
                    // 设置值
                    data[i]=StringUtil.isNotEmpty(retObj)?DateUtil.format((Date)retObj,DateUtil.DEFAULT_DATE_FORMAT_EN):StringUtil.EMPTY;
                }else if (StringUtil.equals(returnType, BigDecimal.class.getName())) {
                    // 单位判断
                    DictCodeListExt1 unitDict = idx.unit();
                    if(DictCodeListExt1.单位_万元==unitDict){
                        data[i] = StringUtil.isNotEmpty(retObj) ? ArithmeticUtil.divide((BigDecimal)retObj,new BigDecimal(10000),2) : StringUtil.EMPTY;
                    } else{
                        data[i] = StringUtil.isNotEmpty(retObj) ? retObj : StringUtil.EMPTY;
                    }
                }else{
                    data[i] = StringUtil.isNotEmpty(retObj) ? retObj : StringUtil.EMPTY;
                }
            }
            return data;
        }
        catch (Exception e) {
            throw new BusinessException("数据解析异常",e);
        }
    }

    /**
     * 文件行数据转化成Entity
     * @param data 文件行数据
     * @param clz Entity类型
     * @param <T> 返回类型
     * @return Entity实例
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public static <T> T parseArrayToEntry(String[] data, Class<T> clz)
            throws BusinessException {
        // 数据为空或者受体类不存在时返回空
        if (data == null || data.length == 0 || clz == null) {
            return null;
        }
        // 实例化受体类
        try {
            T entity = clz.newInstance();
            // 受体类的方法列表
            Method[] fList = clz.getMethods();
            for (Method md : fList) {
                // 受体类的方法注解
                Number idx = md.getAnnotation(Number.class);
                // 受体类的方法不是set方法或者没有注解时跳过
                if (!StringUtil.startsWith(md.getName(), "set") || idx == null) {
                    continue;
                }
                int i = idx.value();
                // 参数类型
                String paramType = md.getGenericParameterTypes()[0].getTypeName();
                String val = data[i];
                if (StringUtil.isEmpty(val)) {
                    continue;
                }
                // String类型时
                if (isClassType(paramType, String.class)) {
                    // 关联数据字典
                    DictList dict = md.getAnnotation(DictList.class);
                    // 数据字典转换
                    if (dict != null) {
                        List<? extends DataDictItem> dictTypeList= SysDataUtil.getDataDictItem(dict.link().getValue());
                        String valExt=val;
                        for(DataDictItem item:dictTypeList){
                            if (StringUtil.equals(dict.match()== DictEnum.MIX?item.getItemCode()+item.getItemValue():dict.match()== DictEnum.NMAE?item.getItemValue():item.getItemCode(), val)) {
                                valExt=item.getItemCode();
                                break;
                            }else{
                                valExt=null;
                            }
                        }
                        val=valExt;
                    }
                    md.invoke(entity, val);
                }
                // Date类型时
                else if (isClassType(paramType, Date.class)) {
                    // 设置值
                    md.invoke(entity, val.contains("/")?DateUtil.parseDate(val, DateUtil.DEFAULT_ANOTHER_FORMAT):val.contains("-")?DateUtil.parseDate(val, DateUtil.DEFAULT_DATE_FORMAT_EN):DateUtil.parseDate(val, DateUtil.DEFAULT_OTHER_FORMAT));
                }
                // BigDecimal类型时
                else if (isClassType(paramType, BigDecimal.class)) {
                    // 设置值
                    md.invoke(entity, new BigDecimal(val));
                }
                // int类型时
                else if (isClassType(paramType, Integer.class)) {
                    // 设置值
                    md.invoke(entity, new Integer(val));
                }
                // long类型时
                else if (isClassType(paramType, Long.class)) {
                    // 设置值
                    md.invoke(entity, new Long(val));
                }
                else {
                    throw new BusinessException("受体类属性(" + md.getName() + ")类型(" + paramType
                            + ")定义不符合规范:【String, Date, BigDecimal, int, long】");
                }
            }
            return entity;
        }
        catch (InstantiationException | IllegalAccessException | SecurityException
                | IllegalArgumentException | InvocationTargetException | ParseException e) {
            throw new BusinessException("文件解析异常");
        }
    }

    /**
     * 判断Class类型
     *
     * @param type 对象
     * @param clz 目标
     * @return true:符合预期；false:不符合
     * @see
     * @since 1.0
     */
    private static <T> boolean isClassType(String type, Class<T> clz) {
        String clzName = clz.getName();
        return StringUtil.equals(clzName, type);
    }

    /**
     * 检查List与对应的Entity不为空或List可为空
     * @param tList
     * @param k
     * @param <T>
     * @param <K>
     * @return
     */
    private static <T,K> Boolean checkNull(List<T> tList,K k){
        return (!StringUtil.isEmptyList(tList)&&!Objects.isNull(k))||StringUtil.isEmptyList(tList);
    }

    /**
     * Object对象转化为指定类型
     * @param obj
     * @param beanClass
     * @param consumer
     * @param <T>
     * @return
     */
    public static <T> T convert(Object obj, Class<T> beanClass, Function<Object, T> consumer) {
        if (!StringUtil.isNotEmpty(obj)) {
            return null;
        }
        else if (obj.getClass() == beanClass) {
            return (T) obj;
        }
        else if (consumer != null) {
            return consumer.apply(obj);
        }
        return null;
    }

    @Autowired(required = false)
    public void setCalendarSrc(CalendarService calendarSrc) {
        AssetCommonUtil.calendarSrc = calendarSrc;
    }

    @Autowired(required = false)
    public void setAssetGenerateMapper(AssetGenerateMapper assetGenerateMapper) {
        AssetCommonUtil.assetGenerateMapper = assetGenerateMapper;
    }

    @Autowired(required = false)
    public void setAssetInfoRedisService(AssetInfoRedisService assetInfoSrc) {
        AssetCommonUtil.assetInfoSrc = assetInfoSrc;
    }
}

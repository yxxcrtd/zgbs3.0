package com.joyintech.tams.invest.common.risktrialrds.util;

import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.jdbc.datasource.DynamicDataSource;
import com.joyintech.framework.util.*;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.asset.util.AssetCommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.riskinfrds.calendar.entity.ICalendar;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskIndividConditionDto;
import com.joyintech.tams.invest.common.risktrialrds.reflect.PluginClass;
import com.joyintech.tams.invest.common.risktrialrds.reflect.REF;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 部分方法风险试算共通
 * 
 * @author 裴家俊
 * @version 1.0 2018年11月30日
 * @see RiskUtil
 * @since 1.0
 */
public class RiskUtil {

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(RiskUtil.class);

	public static final String redisKey = "RKEY";
	public static final String redisVal = "RVALUE";
	public static final String paramPref = "${";
	public static final String paramPrefZy = "\\$\\{";
	public static final String paramSuff = "}";
	public static final String LIMIT = "LIMIT_";
	public static final String FORBID = "FORBID_";
	//添加内置阈值型 add by wz 20200418
	public static final String INNER = "INNER_";
	public static final String AND = "AND";
	public static final String OR = "OR";
	public static final String COMMON_PLUGIN_REFLECT="pluginClassRdsDao";
	public static final String COMMON_PLUGIN_NAME="com.joyintech.tams.invest.common.risktrialrds.reflect.impl.";
	public static final String MAX = "MAX";
	public static final String MIN = "MIN";
	public static final String PLATE_DATE = "PLATE_DATE";
	public static final String DELIMITER=new String(new byte[] { 3 });
	public static final String REMIND_FILE_PRE = "CPLRSK_ZDTX_";
	public static final String FILE_OK = ".ok";
	public static final int FTP_PORT = 21;
	public static final String FTP_ENCODE = "UTF-8";
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final String RISK_RESULT_ENTITY="riskTrialRetDto";
    public static final String APPROVE_LINE_ONE="APPROVE_LINE_ONE";
    public static final String APPROVE_LINE_TWO="APPROVE_LINE_TWO";
    public static final String APPROVE_LINE_THR="APPROVE_LINE_THR";

	public static final String DURATION_SERVICE = "riskManageCommonService";

	public static final String RSK_PORT_INFO="RSK_PORT_INFO";
	public static final String RSK_CONDITION_INFO="RSK_CONDITION_INFO";
	public static final String RSK_VALUATION_INFO="RSK_STAGE_INFO";
	public static final String RSK_ASTRULECASH_INFO="RSK_ASTRULECASH_INFO";
	public static final String RSK_STAGE_INFO="RSK_STAGE_INFO";
	public static final String RSK_STAGEAST_INFO="RSK_STAGEAST_INFO";
	public static final String RSK_ASTDEL_INFO="RSK_ASTDEL_INFO";
	public static final String PAGE_REDIS_FLAG="pageRedisFlag";
	public static final String RISK_REFRESH_TIEM="riskRefreshTime";

	public static final long riskRefreshTime=8000L;
	/*合并模拟测算代码 panbin 2020-8-11 end*/
	public static final String IS_DISPLAY_PEND_PERIOD="IS_DISPLAY_PEND_PERIOD";
	public static final String UP_LINE = "||";
	public static final String UP_LINE_SPLIT = "\\|\\|";
	private static Map<String, PluginClass<?>> pluginMap = new HashMap<>();

	private static Map<String,String> classifyDimensionMap= new HashMap<String, String>() {
		{
			put(DictCodeListExt1.分类维度_收益类型.getValue(),"PROFIT_TYPE");
			put(DictCodeListExt1.分类维度_开放类型.getValue(),"PA_PTL_PORTFOLIO.OPEN_TYPE");
			put(DictCodeListExt1.分类维度_募集方式.getValue(),"RAISE_TYPE");
			put(DictCodeListExt1.分类维度_投资性质.getValue(),"PA_PTL_PORTFOLIO.INVEST_NATURE");
			put(BussinessDictCodeListRds.分类维度_是否面向单一投资者.getValue(),"IS_FACE_SINGLEINVESTOR");
			put(BussinessDictCodeListRds.分类维度_说明书是否披露风险标志.getValue(),"DIRECT_DISCLOSURE_RISK_FLAG");
			put(BussinessDictCodeListRds.分类维度_开放周期.getValue(),"PTL_PORTFOLIO.PORTFOLIO_ID");
		}
	};

	public static Map<String,Integer> openCycleMap= new HashMap<String, Integer>() {
		{
			put(BussinessDictCodeListRds.开放周期_小于90天.getValue(),90);
			put(BussinessDictCodeListRds.开放周期_大于等于90天.getValue(),90);
		}
	};

	/**
	 * 初始化指标参数利用反射类模块Service列表
	 *
	 * @see
	 * @since 1.0
	 */
	private static void init() {
		ApplicationContext context = SpringContextUtil.getApplicationContext();
		if (context == null) {
			return;
		}
		Map<String, Object> beans = context.getBeansWithAnnotation(REF.class);
		if (beans == null || beans.isEmpty()) {
			return;
		}
		beans.entrySet().forEach(en -> {
			Object obj = en.getValue();
			if (obj instanceof PluginClass) {
				PluginClass<?> svc = (PluginClass<?>)obj;
				String refId = svc.getRefId();
				pluginMap.put(refId, svc);
			}
		});
	}

	/**
	 * 根据反射类ID取得反射实现类模块
	 *
	 * @param id 接口ID
	 * @return 反射实现模块
	 * @see
	 * @since 1.0
	 */
	public static final PluginClass<?> getBean(String id) {
		if (pluginMap == null || pluginMap.isEmpty()) {
			init();
		}
		return pluginMap.get(id);
	}

	/**
	 * @param str
	 * @return
	 */
	public static String convertPortfCode(String str) {
		if (!StringUtil.isNotEmpty(str)) {
			return "";
		}
		// 拆分组合代码
		String[] strPortfCodes = str.split(java.util.regex.Pattern.quote((new Character(',')).toString()), -1);

		StringBuilder builder = new StringBuilder();
		builder.append("(");

		for (String strPortfCode : strPortfCodes) {
			builder.append("'");
			builder.append(strPortfCode);
			builder.append("'");
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(")");

		return builder.toString();
	}

	/**
	 * 部分单位转换数值
	 * 
	 * @param unit
	 * @param value
	 * @return
	 */
	public static Double convertUnitToValue(String unit, Double value) {
		if (BussinessDictCodeListRds.单位_万元.getValue().equals(unit)) {
			return value * 10000;
		}
		if (BussinessDictCodeListRds.单位_PERCENT.getValue().equals(unit)) {
			return ArithmeticUtil.divide(BigDecimal.valueOf(value),new BigDecimal(100),10).doubleValue() ;
		}
		if (BussinessDictCodeListRds.单位_BP.getValue().equals(unit)) {
			return ArithmeticUtil.divide(BigDecimal.valueOf(value),new BigDecimal(10000),10).doubleValue() ;
		}
		return value;
	}

	/**
	 * 部分单位转换为汉字
	 * 
	 * @param unit
	 * @return
	 */
	public static String convertValueToUnit(String unit) {
		if ("-".equals(unit))
			return StringUtil.EMPTY;
		else
			return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.单位, unit);
	}
	
	/*
	 * 对于超过固定数量的集合进行二次分割----add by 裴家俊  2019/03/22--------
	 */
	public static List<List<String>> splitList(String[] listArr, int len) {
		List<String> list=Arrays.asList(listArr);
		if (list == null || list.size() == 0 || len < 1) {
			return null;
		}
		List<List<String>> result = new ArrayList<List<String>>();
		int size = list.size();
		int count = (size + len - 1) / len;
		for (int i = 0; i < count; i++) {
			List<String> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
			result.add(subList);
		}
		return result;
	}
	

	private RiskUtil(){
	    // private the constructor
	}
	
    public static String transferWarnFoul(String value, String indCellType) {
        String wg = StringUtil.EMPTY;
        if (StringUtil.isNotEmpty(value)) {
			Map<?, ?> detailJson =JsonUtils.json2Object(value,Map.class);
			Map<?, ?> vThreshold1Map = (Map<?, ?>)detailJson.get("threshold_1");
            if (indCellType.indexOf(RiskUtil.LIMIT) != -1||indCellType.indexOf(RiskUtil.INNER) != -1) {
                    if (StringUtil.isNotEmpty(vThreshold1Map.get("value"))) {
                        wg = (String)vThreshold1Map.get("op") + vThreshold1Map.get("value")
                             + valueConvertUnit((String)vThreshold1Map.get("unit"));
                    }
            }
            else {
                /* 禁止型 */
                if(StringUtil.isNotEmpty(vThreshold1Map.get("value_cn"))){
                	wg=vThreshold1Map.get("value_cn").toString();
                	wg=wg.substring(0, wg.length() - 1);
				}
            }
        }
        return wg;
    }

    /**
     * 单位转换
     */
    private static String valueConvertUnit(String value) {
        if (StringUtil.EMPTY.equals(value)) {
            return value;
        }
		if(BussinessDictCodeListRds.单位_无.getValue().equals(value)){
			return StringUtil.EMPTY;
		}
        String retStr = StringUtil.EMPTY;
        DataDict dataDict = CacheUtil.getDictCode(BussinessDictCodeRds.单位.getValue());
        List<? extends DataDictItem> itemList = dataDict.getItems();
        for (DataDictItem v : itemList) {
            if (v.getItemCode().equals(value)) {
                if(BussinessDictCodeListRds.单位_无.getValue().equals(value)){
                    retStr=StringUtil.EMPTY;
                }else {
                    retStr = v.getItemValue();
                }
                break;
            }
        }
        return retStr;
    }

    /**
     * 获取解析指标参数共通
     */
    public  static  List<String> analysisIndParam(String param,List<String> paramList){
        if(StringUtil.isNotEmpty(param)&&param.indexOf(RiskUtil.paramPref)!=-1&&param.indexOf(paramSuff)!=-1){
            String paramPre=param.substring(param.indexOf(paramPref)+2, param.indexOf(paramSuff));
            String exprNew=param.substring(param.indexOf(paramSuff)+1);
            paramList.add(paramPre);
            analysisIndParam(exprNew,paramList);
        }
        return paramList;
    }

	/**
	 * 取得新的参数代码
	 * @param exp
	 * @param cond
	 * @return
	 */
	public static String getParamWithMode(Set<String> fixParamList,String exp,String cond){
		Map<String,String> expMap=new HashMap<>();
		if(!StringUtil.isNotEmpty(exp)){
			return exp;
		}
		String[] expArr=new String[]{exp};
		//无禁止线不出指标 start
		if(cond.indexOf(">")>-1||cond.indexOf(">=")>-1){
			expMap=dealParamMinMax(fixParamList,expArr,expMap,RiskUtil.MAX);
		}else if(cond.indexOf("<")>-1||cond.indexOf("<=")>-1){
			expMap=dealParamMinMax(fixParamList,expArr,expMap,RiskUtil.MIN);
		}else{
			expMap=dealParamMinMax(fixParamList,expArr,expMap,RiskUtil.MAX);
		}
		//无禁止线不出指标end
//		if(cond.indexOf("<")>-1||cond.indexOf("<=")>-1){
//			expMap=dealParamMinMax(fixParamList,expArr,expMap,RiskUtil.MIN);
//		}
		for (Map.Entry<String, String> entry : expMap.entrySet()) {
			expArr[0]=expArr[0].replaceAll(entry.getKey()+RiskUtil.paramSuff,entry.getValue()+RiskUtil.paramSuff);
		}
		return expArr[0];
	}

	/**
	 * 处理参数的MAX及Min
	 * @param expArr
	 * @param expMap
	 * @return
	 */
	private static Map<String,String> dealParamMinMax(Set<String> fixParamList,String[] expArr,Map<String,String> expMap,String direction){
		if(expArr[0].indexOf("/")>-1){
			String fenZi=expArr[0].substring(0,expArr[0].indexOf("/"));
			String fenMu=expArr[0].substring(expArr[0].indexOf("/")+1,expArr[0].length());
			dealMinMax(fixParamList,fenZi,expMap,direction.equals(RiskUtil.MAX)?RiskUtil.MAX:RiskUtil.MIN);
			for (Map.Entry<String, String> entry : expMap.entrySet()) {
				expArr[0]=expArr[0].substring(0,expArr[0].indexOf("/")).replaceAll(entry.getKey(),entry.getValue())+"/"+fenMu;
			}
			if(fenMu.indexOf("/")>-1){
				dealParamMinMax(fixParamList,new String[]{fenMu},expMap,direction);
			}
			dealMinMax(fixParamList,fenMu,expMap,direction.equals(RiskUtil.MAX)?RiskUtil.MIN:RiskUtil.MAX);
			return expMap;
		}
		if(expArr[0].indexOf("*")>-1){
			String fenZi=expArr[0].substring(0,expArr[0].indexOf("*"));
			String fenMu=expArr[0].substring(expArr[0].indexOf("*")+1,expArr[0].length());
			dealMinMax(fixParamList,fenZi,expMap,direction.equals(RiskUtil.MAX)?RiskUtil.MAX:RiskUtil.MIN);
			for (Map.Entry<String, String> entry : expMap.entrySet()) {
				expArr[0]=expArr[0].substring(0,expArr[0].indexOf("*")).replaceAll(entry.getKey(),entry.getValue())+"*"+fenMu;
			}
			if(fenMu.indexOf("*")>-1){
				dealParamMinMax(fixParamList,new String[]{fenMu},expMap,direction);
			}
			dealMinMax(fixParamList,fenMu,expMap,direction.equals(RiskUtil.MAX)?RiskUtil.MAX:RiskUtil.MIN);
			return expMap;
		}
		//处理即没有除法也没有乘法的表达式
		dealMinMax(fixParamList,expArr[0],expMap,direction);
		return expMap;
	}

	/**
	 * 处理单个表达式中的最大最小值，比如分子或分母或没有除法及乘法的表达式
	 * @param entry
	 * @param entryRet
	 * @param operate
	 */
    private  static void dealMinMax(Set<String> fixParamList,String entry,Map<String,String> entryRet,String operate){
		String[] expAry=entry.split(RiskUtil.paramSuff);//分子
		List<String> listEnty=Arrays.asList(expAry).stream().filter(item->!item.equals("")&&!item.equals("(")&&!item.equals(")")).collect(Collectors.toList());
		String[] expAryRel=listEnty.toArray(new String[listEnty.size()]);
		for(String expStr:expAryRel){
			List<String> listEntySub=Arrays.asList(expStr.split(RiskUtil.paramPrefZy)).stream().filter(item->!item.equals("")&&!item.equals("(")&&!item.equals(")")).collect(Collectors.toList());
			String[] expArySec=listEntySub.toArray(new String[listEntySub.size()]);
			if(operate.equals(MAX)) {
				if (expArySec.length > 1 ) {
					if(expArySec[0].equals("-")) {
						//若参数是固定值，直接赋值FIX
						if(fixParamList.contains(expArySec[1])){
							entryRet.put(expArySec[1], expArySec[1] + GlobalConstant.COLON + BussinessDictCodeListRds.指标参数实现类型_固定值.getValue());
						}
						else {
							entryRet.put(expArySec[1], expArySec[1] + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_卖出.getValue());
						}
					}
					else{
						//若参数是固定值，直接赋值FIX
						if(fixParamList.contains(expArySec[1])){
							entryRet.put(expArySec[1], expArySec[1] + GlobalConstant.COLON + BussinessDictCodeListRds.指标参数实现类型_固定值.getValue());
						}
						else {
							entryRet.put(expArySec[1], expArySec[1] + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_买入.getValue());
						}
					}
				} else {
					//若参数是固定值，直接赋值FIX
					if(fixParamList.contains(expArySec[0])){
						entryRet.put(expArySec[0],expArySec[0] + GlobalConstant.COLON + BussinessDictCodeListRds.指标参数实现类型_固定值.getValue());
					}else {
						entryRet.put(expArySec[0], expArySec[0] + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_买入.getValue());
					}
				}
			}
			else{
				if (expArySec.length > 1 ) {
					if(expArySec[0].equals("-")) {
						//若参数是固定值，直接赋值FIX
						if(fixParamList.contains(expArySec[1])){
							entryRet.put(expArySec[1], expArySec[1] + GlobalConstant.COLON + BussinessDictCodeListRds.指标参数实现类型_固定值.getValue());
						}else {
							entryRet.put(expArySec[1], expArySec[1] + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_买入.getValue());
						}
					}
					else{
						//若参数是固定值，直接赋值FIX
						if(fixParamList.contains(expArySec[1])){
							entryRet.put(expArySec[1],expArySec[1] + GlobalConstant.COLON + BussinessDictCodeListRds.指标参数实现类型_固定值.getValue());
						}else {
							entryRet.put(expArySec[1], expArySec[1] + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_卖出.getValue());
						}
					}
				} else {
					//若参数是固定值，直接赋值FIX
					if(fixParamList.contains(expArySec[0])){
						entryRet.put(expArySec[0],expArySec[0] + GlobalConstant.COLON + BussinessDictCodeListRds.指标参数实现类型_固定值.getValue());
					}else {
						entryRet.put(expArySec[0], expArySec[0] + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_卖出.getValue());
					}
				}
			}
		}
	}

	/**
	 * 根据类型比较大小
	 * @param type
	 * @param valStr
	 * @param valEnd
	 * @return
	 */
	public static boolean compareNumberVal(String valType,String type, BigDecimal valStr, BigDecimal valEnd, Object comVal){
		if(valType.equals("DATE")){
			Date dateStr = new Date();
			dateStr.setTime(valStr.longValue());
			Date dateEnd = new Date();
			dateEnd.setTime(valEnd.longValue());
			return DateUtil.compareDate(simpleDateFormat.format(dateStr),DateUtil.format((Date) comVal))<=0&&DateUtil.compareDate(simpleDateFormat.format(dateEnd),DateUtil.format((Date) comVal))>=0;
		}else {
			if( comVal instanceof BigDecimal ) {
				BigDecimal comValN = (BigDecimal) comVal;
				if (type.equals(BussinessDictCodeListRds.控制类型_范围控制.getValue())) {
					return comValN.compareTo(valStr) >= 0 && comValN.compareTo(valEnd) <= 0;
				}
				if (type.equals(BussinessDictCodeListRds.控制类型_大于.getValue())) {
					return comValN.compareTo(valStr) > 0;
				}
				if (type.equals(BussinessDictCodeListRds.控制类型_大于等于.getValue())) {
					return comValN.compareTo(valStr) >= 0;
				}
				if (type.equals(BussinessDictCodeListRds.控制类型_小于.getValue())) {
					return comValN.compareTo(valStr) < 0;
				}
				if (type.equals(BussinessDictCodeListRds.控制类型_小于等于.getValue())) {
					return comValN.compareTo(valStr) <= 0;
				}
			}

		}
		return true;
	}

	/**
	 * Map转实体
	 * @param mapOther
	 * @param key
	 * @param cls
	 * @param <T>
	 * @return
	 */
	public static <T>  List<T> getMapOtherObj(Map<String,Object> mapOther,String key, Class<T> cls){
		List<T> t=(List<T>)mapOther.get(key);
		return t;
	}

	/**
	 * 取指标状态中最小的状态
	 * @param supState
	 * @return
	 */
	public static String getMinState(List<String> supState){
		List<Integer> supStateNum=new ArrayList<>();
		supState.forEach(item->{
			if(item.equals(BussinessDictCodeListRds.监督状态_正常.getValue())){
				supStateNum.add(0);
			}
			if(item.equals(BussinessDictCodeListRds.监督状态_触发预警.getValue())){
				supStateNum.add(1);
			}
			if(item.equals(BussinessDictCodeListRds.监督状态_触发审批.getValue())){
				supStateNum.add(2);
			}
			if(item.equals(BussinessDictCodeListRds.监督状态_触发超限.getValue())){
				supStateNum.add(3);
			}
			if(item.equals(BussinessDictCodeListRds.监督状态_异常.getValue())){
				supStateNum.add(4);
			}
		});
		if(Collections.min(supStateNum)==0){
			return BussinessDictCodeListRds.监督状态_正常.getValue();
		}
		if(Collections.min(supStateNum)==1){
			return BussinessDictCodeListRds.监督状态_触发预警.getValue();
		}
		if(Collections.min(supStateNum)==2){
			return BussinessDictCodeListRds.监督状态_触发审批.getValue();
		}
		if(Collections.min(supStateNum)==3){
			return BussinessDictCodeListRds.监督状态_触发超限.getValue();
		}
		if(Collections.min(supStateNum)==4){
			return BussinessDictCodeListRds.监督状态_异常.getValue();
		}
		return Collections.min(supStateNum).toString();
	}

	/**
	 * 取指标状态中最大的状态
	 * @param supState
	 * @return
	 */
	public static String getMaxState(List<String> supState){
		List<Integer> supStateNum=new ArrayList<>();
		supState.forEach(item->{
			if(item.equals(BussinessDictCodeListRds.监督状态_正常.getValue())){
				supStateNum.add(0);
			}
			if(item.equals(BussinessDictCodeListRds.监督状态_触发预警.getValue())){
				supStateNum.add(1);
			}
			if(item.equals(BussinessDictCodeListRds.监督状态_触发审批.getValue())){
				supStateNum.add(2);
			}
			if(item.equals(BussinessDictCodeListRds.监督状态_触发超限.getValue())){
				supStateNum.add(3);
			}
			if(item.equals(BussinessDictCodeListRds.监督状态_异常.getValue())){
				supStateNum.add(4);
			}
		});
		if(Collections.max(supStateNum)==0){
			return BussinessDictCodeListRds.监督状态_正常.getValue();
		}
		if(Collections.max(supStateNum)==1){
			return BussinessDictCodeListRds.监督状态_触发预警.getValue();
		}
		if(Collections.max(supStateNum)==2){
			return BussinessDictCodeListRds.监督状态_触发审批.getValue();
		}
		if(Collections.max(supStateNum)==3){
			return BussinessDictCodeListRds.监督状态_触发超限.getValue();
		}
		if(Collections.max(supStateNum)==4){
			return BussinessDictCodeListRds.监督状态_异常.getValue();
		}
		return Collections.max(supStateNum).toString();
	}

	/**
	 * 获取列的SQL
	 * @param linkSign
	 * @param column
	 * @param valueStr
	 * @return
	 */
	public static String getTableColumnSql(String linkSign,String column,String valueStr,String sqlWhere){
		if(!StringUtil.isNotEmpty(linkSign)){
			sqlWhere=" ("+column+" in "+ RiskUtil.convertPortfCode(valueStr)+")" +sqlWhere;
			int rightCount=RiskUtil.getWordCount(sqlWhere,")");
			int leftCount=RiskUtil.getWordCount(sqlWhere,"(");
			for(int i=0;i<rightCount-leftCount;i++){
				sqlWhere="("+sqlWhere;
			}
		}
		else {
			if (linkSign.equals("OR")) {
				sqlWhere = " OR (" + column + " in " + RiskUtil.convertPortfCode(valueStr) + ")" +")" +sqlWhere;
			}
			if (linkSign.equals("AND")) {
				sqlWhere = " AND (" + column + " in " + RiskUtil.convertPortfCode(valueStr) + ")" +")"+ sqlWhere;
			}
		}
		return sqlWhere;
	}
	/**
	 * 获取对应数据库字段
	 * @param val
	 * @return
	 */
	public static String getTableColumn(String val){
		return StringUtil.isNotEmpty(classifyDimensionMap.get(val))?classifyDimensionMap.get(val):StringUtil.EMPTY;
	}

	/**
	 * 统计字符串中出现某个特定字符的个数
	 * @param targetStr
	 * @param compStr
	 * @return
	 */
	public static int getWordCount(String targetStr,String compStr){
		int fromIndex = 0;
		int count = 0;
		while(true){
			int index = targetStr.indexOf(compStr, fromIndex);
			if(-1 != index){
				fromIndex = index + 1;
				count++;
			}else{
				break;
			}
		}
		return count;
	}

	public static String getLogString(Exception e){
		StringBuilder sb = new StringBuilder();
		sb.append("处理异常, ");
		StackTraceElement[] trace = e.getStackTrace();
		sb.append("<br/>");
		sb.append(e.getMessage());
		sb.append("<br/>");
		int i = 0;
		for (StackTraceElement traceElement : trace) {
			if (i <= 30) {
				sb.append("&nbsp;&nbsp;at " + traceElement + "<br/>");
				i++ ;
			}
			else {
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 特殊字符转换
	 * @param item
	 * @param flag 正反向
	 * @return
	 */
	public static String specialCharacterConversion(String item,boolean flag){
		if(StringUtil.isNotEmpty(item)){
			if(flag) {
				return item.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"");
			}else{
				return item.replaceAll("<","&lt;" ).replaceAll(">","&gt;" ).replaceAll("\"","&quot;" );
			}
		}
		return item;
	}

	/**
	 * check是否是日期
	 * @param param 参数
	 * @return check结果
	 */
	public  static <T> boolean isInvalidDate(T param) {
		if (StringUtil.isNotEmpty(param)) {
			Pattern  pattern = Pattern.compile("(\\d{4})-(\\d+)-(\\d+).*");
			Matcher matcher = pattern.matcher(param.toString());
			if(matcher.matches()){
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 实体转Map
	 * @param obj
	 * @return
	 */
	public static Map<String,Object> getObjectToMap(Object obj){
		Map<String, Object> mapTrd = new HashMap<>();
		try {
			mapTrd = JsonUtils.stringToMap(JsonUtils.object2Json(obj));
		}catch (Exception e){
			mapTrd =JsonUtils.object2Map(obj);
		}
		return mapTrd;
	}

	/**
	 * 从redis获取值转成map类型
	 * @param map
	 * @return
	 */
	public static Map<String, Map<String, Object>> changeRedisValueToMap(Map<String, Map<String, IValue<Object>>> map) {
		Map<String, Map<String, Object>> resMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(map)) {
			map.entrySet().forEach(en -> {
				String key = en.getKey();
				Map<String, Object> vMap = new HashMap<>();
				Map<String, IValue<Object>> value = en.getValue();
				if (!CollectionUtils.isEmpty(value)) {
					value.entrySet().forEach(mp -> {
						String field = mp.getKey();
						IValue<Object> obj = mp.getValue();
						if (obj != null) {
							vMap.put(field, obj.value());
						}
					});
					resMap.put(key, vMap);
				}
			});
		}
		return resMap;
	}

	/**
	 * 数据插入数据库
	 *
	 * @param tableName
	 *            表名
	 * @param sqlList
	 *            输入语句列表
	 * @param basicDataSource
	 *            DB信息
	 */
	public static void insertBatchData(String tableName, List<String> sqlList, DynamicDataSource basicDataSource) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = basicDataSource.getConnection();

			// 关闭数据库连接 自动提交事务
			conn.setAutoCommit(false);

			stmt = conn.createStatement();
			for (int i = 0; i < sqlList.size(); i++) {
				if (StringUtil.isNotEmpty(sqlList.get(i))) {
					stmt.addBatch(sqlList.get(i));
				}
				try {
						if (i > 0 && i % 10000 == 0) {
							stmt.executeBatch();
							// 提交事务
							conn.commit();
							stmt.clearBatch();
						} else if (i == sqlList.size() - 1) {
							stmt.executeBatch();
							// 提交事务
							conn.commit();
							stmt.clearBatch();
						}
				} catch (SQLException e) {
					logger.error("数据插入数据出错...表名：" + tableName,e);
					continue;
				}
			}
			stmt.close();
		} catch (SQLException e) {
			logger.error("数据插入数据出错...表名：" + tableName,e);
		} finally {
			try {
				if(stmt!=null)
				{
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}

	public static Long getRefreshTime(){
		return getTimeCommon((list)->{
			return Long.parseLong(list.get(0));
		});
	}

	public static Long getRefreshMulTime(){
		return getTimeCommon((list)->{
			return list.size()>1?Long.parseLong(list.get(1)):riskRefreshTime;
		});
	}

	private static long getTimeCommon(Function<List<String>,Long> function){
		Object refreshTime=CacheUtil.getSysParam(RiskUtil.RISK_REFRESH_TIEM);
		if(StringUtil.isNotEmpty(refreshTime)){
			List<String> timeList=StringUtil.splitToList(",",refreshTime.toString());
			return function.apply(timeList);
		}
		return riskRefreshTime;
	}

	public static <T> void dealList(List<T> list, List<Long> daysList,Function<List<T>,Long> function,Consumer<List<Long>> consumer){
		if(list.size()>1) {
			daysList.add(function.apply(list));
			list.remove(0);
			dealList(list,daysList,function,consumer);
		}else{
			consumer.accept(daysList);
		}
	}

	/**
	 * 过滤条件设置启用时间逻辑
	 * @param riskIndividConditionDtoList
	 * @param riskPtlOpenPlanList
	 * @param portfolioId
	 * @param paramIndId
	 * @return
	 */
	public static boolean filterEnableTime(List<RiskIndividConditionDto> riskIndividConditionDtoList, List<FinOpenPlan> riskPtlOpenPlanList, String portfolioId, String paramIndId, Date supDate){
		List<RiskIndividConditionDto>  riskIndividCondSubList =riskIndividConditionDtoList.stream().filter(item->item.getPrtBagId().equals(portfolioId)
				&&item.getIndId().equals(paramIndId)&&CommonUtil.isType(item.getConditionItem(),
				BussinessDictCodeListRds.条件项_启用时间)).collect(Collectors.toList()) ;
		if(StringUtil.isEmptyList(riskIndividCondSubList)){
			return true;
		}
		List<FinOpenPlan> ptlOpenPlanList=riskPtlOpenPlanList.stream().filter(item->StringUtil.replaceAll(item.getFinprodId(), DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()+GlobalConstant.UNDER_LINE,StringUtil.EMPTY)
				.equals(portfolioId)).collect(Collectors.toList());
		//若没有开放计划若归入不在启用时间内
		if(StringUtil.isEmptyList(ptlOpenPlanList)){
			return false;
		}
		List<FinOpenPlan> purOpenList=ptlOpenPlanList.stream().filter(item->Arrays.asList(DictCodeList.申赎开放类型_申购开放.getValue(),
				DictCodeList.申赎开放类型_申赎开放.getValue()).contains(item.getOpenType())).collect(Collectors.toList());
		List<FinOpenPlan> repOpenList=ptlOpenPlanList.stream().filter(item->Arrays.asList(DictCodeList.申赎开放类型_赎回开放.getValue(),
				DictCodeList.申赎开放类型_申赎开放.getValue()).contains(item.getOpenType())).collect(Collectors.toList());
		if(StringUtil.isEmptyList(purOpenList)||StringUtil.isEmptyList(repOpenList)){
			return false;
		}
		boolean divQyFlag=true;
		for(RiskIndividConditionDto dto:riskIndividCondSubList){
			//从redis里获取日历信息
			String calendarId=AssetCommonUtil.getAssetInfo(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()
					+GlobalConstant.UNDER_LINE+portfolioId,obj->obj.getCalendarCode());
			ICalendar iCalendar=AssetCommonUtil.getCalendar(calendarId, obj->obj);
			//在启用期间才计算
			Date sDate=dto.getEnableStrTime(purOpenList.get(0),repOpenList.get(0),iCalendar);
			Date eDate=dto.getEnableEndTime(purOpenList.get(0),repOpenList.get(0),iCalendar);
			if(null!=sDate&&null!=eDate) {
				divQyFlag=DateUtil.compareDate(sDate, supDate) <= 0&&DateUtil.compareDate(eDate, supDate) >= 0;
			}else if(null!=sDate){
				divQyFlag=DateUtil.compareDate(sDate, supDate) <= 0;
			}else if(null!=eDate){
				divQyFlag=DateUtil.compareDate(eDate, supDate) >= 0;
			}
		}
		return divQyFlag;
	}
}

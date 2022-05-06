package com.joyintech.tams.invest.common.risktrialrds.reflect.duration.service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetInfo;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetInfoRedisService;
import com.joyintech.tams.invest.common.riskinfrds.param.entity.IParam;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamValService;
import com.joyintech.tams.invest.common.risktrialrds.dao.SupervisionCalcDao;
import com.joyintech.tams.invest.common.risktrialrds.dto.SystemParam;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 久期风险模块
 */
@Service
public class RiskManageCommonService {

	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SupervisionCalcDao supervisionCalcDao;

	/**
	 * 资产基本信息更新Redis服务
	 */
	@Autowired
	private AssetInfoRedisService assetInfoSrc;

	/**
	 * 指标参数值读写服务
	 */
	@Autowired
	private IndicatorParamValService paramValSrv;

	/**
	 * 指标参数信息Redis读写信息服务
	 */
	@Autowired
	private IndicatorParamService indParamSrv;

	private static String 证券估价市价="MBGZQSCHQGJSJ:01";
	private static String 模板单产品偏离度01="MBDZCPTYCBFGZPLD:01";
	private static String 模板单产品偏离度04="MBDZCPTYCBFGZPLD:04";
	private static String 模板单产品偏离度05="MBDZCPTYCBFGZPLD:05";
	private static String 模板产品资产端久期01="MBCPZCDJQ:01";
	private static String 模板产品资产端久期04="MBCPZCDJQ:04";
	private static String 模板产品资产端久期05="MBCPZCDJQ:05";

	/**
	 * 计算偏离度
	 * @param systemParam
	 * @return
	 */
	public List<BigDecimal> calcPrdDeviation(SystemParam systemParam){
		List<BigDecimal> resultList=new ArrayList<>();
		BigDecimal result=BigDecimal.ZERO;
		String sqlStr=模板单产品偏离度01;
		if(StringUtil.equals(systemParam.getSupFrequency(),BussinessDictCodeListRds.监督频率_日间.getValue())){
			sqlStr=模板单产品偏离度04;
		}
		if(StringUtil.equals(systemParam.getSupFrequency(),BussinessDictCodeListRds.监督频率_日终.getValue())){
			sqlStr=模板单产品偏离度05;
		}
		Map<String, IParam> paramMap=indParamSrv.getParamList(Arrays.asList(sqlStr));
		if(!StringUtil.isNotEmpty(paramMap)||!StringUtil.isNotEmpty(paramMap.get(sqlStr))){
			resultList.add(result);
			return resultList;
		}
		systemParam.setPortfolioCode(RiskUtil.convertPortfCode(systemParam.getPortfolioCode()));
		List<Map<String, Object>> resultSet=supervisionCalcDao.selectOne(systemParam.replaceForString(paramMap.get(sqlStr).getContent()));
		if(StringUtil.isEmptyList(resultSet)||resultSet.get(0).keySet().size()<1){
			resultList.add(result);
			return resultList;
		}
		//取第一条数据
		Map<String,Object> priceMap=resultSet.get(0);
		List<Object> priceList = priceMap.values().stream().collect(Collectors.toList());
		if(StringUtil.equals(systemParam.getSupFrequency(),BussinessDictCodeListRds.参数粒度_实时.getValue())){
			//数据库模板中确保第一列是成本总价格，第二列为公允总价格
			BigDecimal 成本法总价格=convert(priceList.get(0),BigDecimal.class,obj->new BigDecimal(obj.toString()));
			BigDecimal 公允总价格=convert(priceList.get(1),BigDecimal.class,obj->new BigDecimal(obj.toString()));
			if(!StringUtil.isNotEmpty(成本法总价格)||!StringUtil.isNotEmpty(公允总价格)){
				resultList.add(result);
				return resultList;
			}
			//实时计算时，画面上需要出原值则先计算原值
			BigDecimal oldDeviation=calcPrdOldDeviation(成本法总价格,公允总价格);
			resultList.add(oldDeviation);
			BigDecimal 现金=BigDecimal.ZERO;
			if(StringUtil.isNotEmpty(systemParam.getTradeAmt())){
				现金=systemParam.getTradeAmt().negate();
			}
			//开始计算现值
			if(StringUtil.isNotEmpty(systemParam.getAssetCode())){
				Map<String, IAssetInfo> assetInfoMap=assetInfoSrc.getMapFromRedis(RedisKeyEnum.资产基本信息.key(),Arrays.asList(systemParam.getAssetCode()));
				if(!CollectionUtils.isEmpty(assetInfoMap)) {
					for (String key : assetInfoMap.keySet()) {
						if (StringUtil.equals(assetInfoMap.get(key).getAssetType(), DictCodeList.平安资产类型_债券.getValue())) {
							if(StringUtil.equals(systemParam.getInvAim(), DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())) {
								Map<String, Map<String, Object>> paramValMap = RiskUtil.changeRedisValueToMap(paramValSrv.readAll(Arrays.asList(证券估价市价)));
								BigDecimal 成本法总价格减数 = 成本法总价格;// 成本法总价格+画面一笔持仓-画面一笔现金，抵捎掉ArithmeticUtil.doAdd(成本法总价格, systemParam.getTradeAmt());
								BigDecimal 公允总价格被减数 = ArithmeticUtil.doAdd(公允总价格, StringUtil.isNotEmpty(paramValMap.get(证券估价市价)) ? ArithmeticUtil.multiply(convert(paramValMap.get(证券估价市价).get(key), BigDecimal.class, obj -> new BigDecimal(obj.toString())), StringUtil.isNotEmpty(systemParam.getTradeNum()) ? systemParam.getTradeNum() : BigDecimal.ZERO) : ArithmeticUtil.multiply(new BigDecimal(100), StringUtil.isNotEmpty(systemParam.getTradeNum()) ? systemParam.getTradeNum() : BigDecimal.ZERO));
								公允总价格被减数 = ArithmeticUtil.doAdd(公允总价格被减数, 现金);
								result = ArithmeticUtil.divide(new BigDecimal(Math.abs(ArithmeticUtil.subtract(成本法总价格减数, 公允总价格被减数).doubleValue())), 公允总价格被减数, 6);
							}else{
								BigDecimal 公允总价格被减数 = ArithmeticUtil.doAdd(公允总价格, 现金);
								result = ArithmeticUtil.divide(new BigDecimal(Math.abs(ArithmeticUtil.subtract(成本法总价格, 公允总价格).doubleValue())), 公允总价格被减数, 6);
							}
						} else {
							BigDecimal 成本法总价格减数 = 成本法总价格;//成本法总价格+画面一笔持仓-画面一笔现金，抵捎掉ArithmeticUtil.doAdd(成本法总价格, systemParam.getTradeAmt());
							BigDecimal 公允总价格被减数 = 公允总价格;//公允总价格+画面一笔持仓-画面一笔现金,，抵捎掉ArithmeticUtil.doAdd(公允总价格, systemParam.getTradeAmt());
							result = ArithmeticUtil.divide(new BigDecimal(Math.abs(ArithmeticUtil.subtract(成本法总价格减数, 公允总价格被减数).doubleValue())), 公允总价格被减数, 6);
						}
					}
				}
			}else{
				// 计算公式：（成本法总价格-公允总价格）/公允总价格(把原公式持仓数量销掉了)
				成本法总价格=ArithmeticUtil.subtract(成本法总价格, 现金);
				公允总价格=ArithmeticUtil.subtract(公允总价格, 现金);
				result = new BigDecimal(Math.abs(ArithmeticUtil.subtract(成本法总价格, 公允总价格).doubleValue()));;
				result = ArithmeticUtil.divide(result, 公允总价格, 6);
			}
		}else {
			result=convert(priceList.get(0),BigDecimal.class,obj->new BigDecimal(obj.toString()));
		}
		resultList.add(result);
		return resultList;
	}

	/**
	 * 计算偏离度原值
	 * @param 成本法总价格
	 * @param 公允总价格
	 * @return
	 */
	private BigDecimal calcPrdOldDeviation (BigDecimal 成本法总价格,BigDecimal 公允总价格){
		BigDecimal resultOld=BigDecimal.ZERO;
		// 计算公式：（成本法总价格-公允总价格）/公允总价格(把原公式持仓数量销掉了)
		resultOld = new BigDecimal(Math.abs(ArithmeticUtil.subtract(成本法总价格, 公允总价格).doubleValue()));
		resultOld = ArithmeticUtil.divide(resultOld, 公允总价格, 6);
		return resultOld;
	}


	/**
	 * 指定类型转换
	 * @param obj
	 * @param beanClass
	 * @param consumer
	 * @param <T>
	 * @return
	 */
	private  <T> T convert(Object obj, Class<T> beanClass, Function<Object, T> consumer) {
		if (obj == null) {
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
}

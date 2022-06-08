/*
 * 文件名：BusiFinBondRatingInfoImplService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.busicommon.productRating.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.busicommon.productRating.dao.CommonProductRatingMapper;
import com.joyintech.tams.busicommon.productRating.dto.CommonProductRatingDto;
import com.joyintech.tams.busicommon.productRating.service.CommonProductRatingService;
import com.joyintech.tams.common.base.dao.MstBondCreditRatingMapper;
import com.joyintech.tams.common.base.dao.MstInnerCreditRatingMapper;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.tams.common.base.entity.MstInnerCreditRating;
import com.joyintech.tams.common.base.entity.MstRatingParamConfig;
/**
 * 产品评级
 * @author 
 * @version 1.0 2019年11月5日
 * @see CommonProductRatingImplService
 * @since 1.0
 */
@Service
public class CommonProductRatingImplService extends BaseService<CommonProductRatingDto> implements CommonProductRatingService{
	
	@Autowired
    private CommonProductRatingMapper commonProductRatingMapper;
	
	//根据type选择表进行插入
	@Autowired
	private MstBondCreditRatingMapper mstBondCreditRatingMapper;
	
	@Autowired
	private MstInnerCreditRatingMapper mstInnerCreditRatingMapper;
	@Override
	public List<CommonProductRatingDto> getFinProductRating(Map<String, Object> map) {
		//评级显示类型  原始、标准    传值为空取数据库配置数据
		if(!StringUtil.isNotEmpty(map.get("mstDisType"))) {
			if(!StringUtil.isNotEmpty(map.get("oType"))) {
	    		map.put("oType", "03");		//为空为债项主体综合场景
	    	}
	    	map.put("paramCode", "MST_DIS_TYPE");
	    	List<MstRatingParamConfig> ratingParamConfig = commonProductRatingMapper.getMstRatingParamConfig(map);
	    	if(ratingParamConfig == null || ratingParamConfig.size() == 0) {
	    		Map<String, Object> configMap = new HashMap<>();
				configMap.put("oType", "99");		//查不出数据查询共同
	    		ratingParamConfig = commonProductRatingMapper.getMstRatingParamConfig(configMap);
	    	}
	    	if(ratingParamConfig == null || ratingParamConfig.size() == 0) {
	    		map.put("mstDisType", null);
	    	}
	    	else {
	    		map.put("mstDisType",ratingParamConfig.get(0).getParamValue());
	    	}
		}
		map.put("sqlSearchString", "(MARKETID IS NULL OR ORGID IS NULL)");		
		return commonProductRatingMapper.getProductBondInnerRating(map);
	}

	@Override
	public List<CommonProductRatingDto> getProductOrgRating(Map<String, Object> map) {
		map.put("oType", "01");		//对象类型 01 主体
		return getFinProductRating(map);
	}

	@Override
	public List<CommonProductRatingDto> getProductMarketRating(Map<String, Object> map) {
		map.put("oType", "02");		//对象类型 02 债项
		return getFinProductRating(map);
	}
	
	@Override
	public List<CommonProductRatingDto> getZtBestRatingByOrgId(Map<String, Object> map) {
		map.put("oType", "01");		//对象类型 01 主体
		map.put("sqlSearchString", "(MARKETID IS NULL OR ORGID IS NULL)");		
		return getBestProductRating(map);
	}

	@Override
	public List<CommonProductRatingDto> getZxBestRatingByMarketId(Map<String, Object> map) {
		map.put("oType", "02");		//对象类型 02 债项
		map.put("sqlSearchString", "(MARKETID IS NULL OR ORGID IS NULL)");		
		return getBestProductRating(map);
	}

	@Override
	public List<CommonProductRatingDto> getBestRatingByMarketId(Map<String, Object> map) {
		map.put("sqlSearchString", "(MARKETID IS NULL OR ORGID IS NULL)");		
		return getBestProductRating(map);
	}

	@Override
	public List<CommonProductRatingDto> getZtBestRatingByMarketId(Map<String, Object> map) {
		map.put("sqlSearchString", "(MARKETID IS NOT NULL AND ORGID IS NOT NULL)");		
		return getBestProductRating(map);
	}

	@Override
	public List<CommonProductRatingDto> getZxztBestRatingByMarketId(Map<String, Object> map) {
		map.put("oType", "03");		//对象类型 03 债项主体综合
		return getBestProductRating(map);
	}

	private List<CommonProductRatingDto> getBestProductRating(Map<String, Object> map) {
		//1、获取产品最优化规则
		Map<String, Object> ruleMap = getBeatRatingRule(map);
		//最优参数数据
		Map<String, Object> bestRuleMap = new HashMap<>();
		//获取评级优先参数配置
		List<MstRatingParamConfig> mstRatingParamConfig = (List<MstRatingParamConfig>) ruleMap.get("MST_RATING_PARAM_CONFIG");
		for(MstRatingParamConfig config : mstRatingParamConfig) {
			//获取排序顺序   key ： PARAM_RAT_SORT
			if(StringUtil.equals("PARAM_RAT_SORT", config.getParamCode())) {
				bestRuleMap.put("PARAM_RAT_SORT", config.getParamValue());
			}
			//基准数据类型   key ： BASE_DATE_TYPE	01：各评级机构最新99：全量数据	
			if(StringUtil.equals("BASE_DATE_TYPE", config.getParamCode())) {
				bestRuleMap.put("BASE_DATE_TYPE", config.getParamValue());
			}
			//评级显示类型   key ： MST_DIS_TYPE	01：原始，02：标准
			if(StringUtil.equals("MST_DIS_TYPE", config.getParamCode())) {
				bestRuleMap.put("MST_DIS_TYPE", config.getParamValue());
			}
		}
		//拼接sql
		String[] sortRules = bestRuleMap.get("PARAM_RAT_SORT").toString().split(",");
		String sqlOtypeParam = "";	//sql字段中的排序规则表评级对象类型
		boolean sortFlag = true;	//是否排序标识	判断排序规则表是否有数据
		//未配置基础排序规则
		if(sortRules.length == 0) {
			sortFlag = false;
		}
		//判断是否配置排序规则数据
		if(StringUtil.isNotEmpty(ruleMap.get("MST_RATING_ATTR_ORDER_COUNT")) && 
				Integer.valueOf(ruleMap.get("MST_RATING_ATTR_ORDER_COUNT").toString()) > 0) {
			//存在评级对象对应的评级规则
			sqlOtypeParam = map.get("oType").toString();
		}
		else if(StringUtil.isNotEmpty(ruleMap.get("MST_RATING_ATTR_ORDER_COMMON")) && 
					Integer.valueOf(ruleMap.get("MST_RATING_ATTR_ORDER_COMMON").toString()) > 0){
			sqlOtypeParam = "99";
		}
		else {
			//未配置排序规则
			sortFlag = false;
		}
		StringBuilder sqlBuild = new StringBuilder();
		sqlBuild.append(" temp");
		map.put("sqlSortString", sqlBuild.toString());
		if(sortFlag) {
			//连接sql
			for(int i=0;i<sortRules.length;i++) {
				sqlBuild.append(" LEFT JOIN MST_RATING_ATTR_ORDER mro").append(i).append(" ON mro").append(i)
					.append(".O_TYPE='").append(sqlOtypeParam).append("' AND mro").append(i).append(".ATTR_CODE = '")
						.append(sortRules[i]).append("' and temp.").append(sortRules[i].replaceAll("_", ""))
							.append(" = mro").append(i).append(".ATTR_VALUE");
			}
			//排序规则
			sqlBuild.append(" order by ");
			for(int i=0;i<sortRules.length;i++) {
				sqlBuild.append(" CAST(").append(" mro").append(i).append(".ATTR_ORDER AS INTEGER) DESC,");
			}
			map.put("sqlSortString", sqlBuild.toString().substring(0, sqlBuild.toString().length()-1));
		}
		if(!StringUtil.isNotEmpty(map.get("mstDisType"))) {
			//评级显示类型    01 原始  02 标准
			map.put("mstDisType", bestRuleMap.get("MST_DIS_TYPE"));
		}
		if(!StringUtil.isNotEmpty(map.get("baseDataType"))) {
			//基准数据类型    01：各评级机构最新99：全量数据
			map.put("baseDataType", bestRuleMap.get("BASE_DATE_TYPE"));
		}
		return commonProductRatingMapper.getBestProductRating(map);
	}
	
	/**
	 * 获取最优化规则
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getBeatRatingRule(Map<String, Object> map) {
		//评级对象类型为空传03   即债项、主体 总体
		if(!StringUtil.isNotEmpty(map.get("oType"))) {
			map.put("oType", "03");
		}
		Map<String, Object> result = new HashMap<>();
    	//对象类型   主体、债券
    	String oType = map.get("oType").toString();		
    	List<MstRatingParamConfig> ratingParamConfig = commonProductRatingMapper.getMstRatingParamConfig(map);
    	if(ratingParamConfig == null || ratingParamConfig.size() == 0) {
    		map.put("oType", "99");		//查不出数据查询共同
    		ratingParamConfig = commonProductRatingMapper.getMstRatingParamConfig(map);
    	}
    	result.put("MST_RATING_PARAM_CONFIG", ratingParamConfig);
    	//查询排序规则表是否有对象类型对应规则记录数量  MST_RATING_ATTR_ORDER
    	map.put("oType", oType);		//查不出数据查询共同
    	int attrOrderCount = commonProductRatingMapper.getMstRatingAttrOrderCount(map);
    	if(attrOrderCount > 0) {
    		result.put("MST_RATING_ATTR_ORDER_COUNT", attrOrderCount);		//对象类型对应的排序规则数据记录
    		result.put("MST_RATING_ATTR_ORDER_COMMON", attrOrderCount);		//共通 99对应的排序规则记录
    	}
    	else {
    		map.put("oType", "99");		//查不出数据查询共通
    		int attrOrderCommonCount = commonProductRatingMapper.getMstRatingAttrOrderCount(map);
    		result.put("MST_RATING_ATTR_ORDER_COUNT", attrOrderCount);		//对象类型对应的排序规则数据记录
    		result.put("MST_RATING_ATTR_ORDER_COMMON", attrOrderCommonCount);		//共通 99对应的排序规则记录
    	}
		result.put("MST_RATING_PARAM_CONFIG",ratingParamConfig);
		return result;
	}
	
	@Override
	public String getProductRatingOrderSql(Map<String, Object> map) {
		//1、获取产品最优化规则
		Map<String, Object> ruleMap = getBeatRatingRule(map);
		//最优参数数据
		Map<String, Object> bestRuleMap = new HashMap<>();
		//获取评级优先参数配置
		List<MstRatingParamConfig> mstRatingParamConfig = (List<MstRatingParamConfig>) ruleMap.get("MST_RATING_PARAM_CONFIG");
		for(MstRatingParamConfig config : mstRatingParamConfig) {
			//获取排序顺序   key ： PARAM_RAT_SORT
			if(StringUtil.equals("PARAM_RAT_SORT", config.getParamCode())) {
				bestRuleMap.put("PARAM_RAT_SORT", config.getParamValue());
			}
			//基准数据类型   key ： BASE_DATE_TYPE	01：各评级机构最新99：全量数据	
			if(StringUtil.equals("BASE_DATE_TYPE", config.getParamCode())) {
				bestRuleMap.put("BASE_DATE_TYPE", config.getParamValue());
			}
			//评级显示类型   key ： MST_DIS_TYPE	01：原始，02：标准
			if(StringUtil.equals("MST_DIS_TYPE", config.getParamCode())) {
				bestRuleMap.put("MST_DIS_TYPE", config.getParamValue());
			}
		}
		//拼接sql
		String sqlSortString = "";		//sql片段结果
		String[] sortRules = bestRuleMap.get("PARAM_RAT_SORT").toString().split(",");
		String sqlOtypeParam = "";	//sql字段中的排序规则表评级对象类型
		boolean sortFlag = true;	//是否排序标识	判断排序规则表是否有数据
		//未配置基础排序规则
		if(sortRules.length == 0) {
			sortFlag = false;
		}
		//判断是否配置排序规则数据
		if(StringUtil.isNotEmpty(ruleMap.get("MST_RATING_ATTR_ORDER_COUNT")) && 
				Integer.valueOf(ruleMap.get("MST_RATING_ATTR_ORDER_COUNT").toString()) > 0) {
			//存在评级对象对应的评级规则
			sqlOtypeParam = map.get("oType").toString();
		}
		else if(StringUtil.isNotEmpty(ruleMap.get("MST_RATING_ATTR_ORDER_COMMON")) && 
					Integer.valueOf(ruleMap.get("MST_RATING_ATTR_ORDER_COMMON").toString()) > 0){
			sqlOtypeParam = "99";
		}
		else {
			//未配置排序规则
			sortFlag = false;
		}
		StringBuilder sqlBuild = new StringBuilder();
		sqlBuild.append(" temp");
		sqlSortString = sqlBuild.toString();
		if(sortFlag) {
			//连接sql
			for(int i=0;i<sortRules.length;i++) {
				sqlBuild.append(" LEFT JOIN MST_RATING_ATTR_ORDER mro").append(i).append(" ON mro").append(i)
					.append(".O_TYPE='").append(sqlOtypeParam).append("' AND mro").append(i).append(".ATTR_CODE = '")
						.append(sortRules[i]).append("' and temp.").append(sortRules[i].replaceAll("_", ""))
							.append(" = mro").append(i).append(".ATTR_VALUE");
			}
			//排序规则
			sqlBuild.append(" order by ");
			for(int i=0;i<sortRules.length;i++) {
				sqlBuild.append(" CAST(").append(" mro").append(i).append(".ATTR_ORDER AS INTEGER) DESC,");
			}
			sqlSortString = sqlBuild.toString().substring(0, sqlBuild.toString().length()-1);
		}
		return sqlSortString;
	}
	
	//根据type选择表进行删除
	@Override
	public int delete(Object entity , int type) {
		if (type==1) {
			return commonProductRatingMapper.deleteByPrimaryKey((MstBondCreditRating) entity);
		}else if (type==2) {
			return commonProductRatingMapper.deleteInnerByPrimaryKey((MstInnerCreditRating) entity);
		}
		return -1;
	}
	//根据type选择表进行更新
	@Override
	public int update(Object entity,int type) {
		if(type==1) {
			return commonProductRatingMapper.updateByPrimaryKey((MstBondCreditRating) entity);
		}else if (type==2) {
			return commonProductRatingMapper.updateInnerByPrimaryKey((MstInnerCreditRating) entity);
		}
		return -1;
	}
	
	@Override
	public void insert(Object entity,int type) {
		//if(type==1 && entity.getClass().getName().equals(MstBondCreditRating.class.getName())) {
		if(type==1 && entity instanceof MstBondCreditRating) {
			mstBondCreditRatingMapper.insert((MstBondCreditRating) entity);
		}else if (type==2) {
			mstInnerCreditRatingMapper.insert((MstInnerCreditRating) entity);
		}
	} 
}

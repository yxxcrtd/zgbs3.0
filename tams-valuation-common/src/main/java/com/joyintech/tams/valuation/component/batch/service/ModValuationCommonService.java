package com.joyintech.tams.valuation.component.batch.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.MstSharePrice;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.ModelDictCodeList;
import com.joyintech.tams.valuation.common.dto.modelDto.*;
import com.joyintech.tams.valuation.component.batch.dao.ModAssetInfoMapper;
import com.joyintech.tams.valuation.component.common.utils.CheckObjectNullUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 估值模型共同查询服务
 * @author zhangfei
 * @date 2021年7月5日16:50:36
 */
@Service
public class ModValuationCommonService extends BaseService {
	
	private static final Logger logger = LoggerFactory.getLogger(ModValuationCommonService.class);
	
	@Autowired
	private ModAssetInfoMapper modAssetInfoMapper;

	/**
	 *  获取现金流贴现法资产信息集合
	 * @param paraMap
	 * @return
	 */
	public List<ModAssetInfoDto> selectAssetInfo(ModAssetInfoParamsDto paraMap){
		try{
			if(null == paraMap.getSearchDate()){
				logger.info("查询日不能为空！");
				throw new Exception("查询日不能为空!");
			}
			return modAssetInfoMapper.selectAssetInfo(paraMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *  获取资产信息集合
	 * @param paraMap
	 * @return
	 */
	public List<ModAssetInfoDto> selectIncomeMethodAssetInfo(ModAssetInfoParamsDto paraMap){
		try{
			if(null == paraMap.getSearchDate()){
				logger.info("查询日不能为空！");
				throw new Exception("查询日不能为空!");
			}
			return modAssetInfoMapper.selectIncomeMethodAssetInfo(paraMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 *  查询二叉树模型需要跑的资产信息
	 * @param paraMap
	 * @return
	 */
	public List<ModAssetInfoDto> selectBinaryTreeAssetInfo(ModAssetInfoParamsDto paraMap){
		try{
			if(null == paraMap.getSearchDate()){
				logger.info("查询日不能为空！");
				throw new Exception("查询日不能为空!");
			}
			return modAssetInfoMapper.selectBinaryTreeAssetInfo(paraMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取资产现金流计划集合
	 * @param finprodId
	 * @return
	 */
	public List<ModCashFlowDto> getCashFlowPlanInfoByFinprodId(String finprodId){
		try{
			if(StringUtil.isEmpty(finprodId)){
				logger.info("资产代码不能为空！");
				throw new Exception("资产代码不能为空!");
			}
			return modAssetInfoMapper.getCashFlowPlanInfoByFinprodId(finprodId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 现金流计算规则查询
	 * @param finprodId
	 * @return
	 */
	public List<ModCashFlowCalRuleDto>  getCashFlowCalRulesByFinprodId(String finprodId){
		try {
			if(StringUtil.isEmpty(finprodId)){
				logger.info("资产代码不能为空！");
				throw new Exception("资产代码不能为空!");
			}
			return modAssetInfoMapper.getCashFlowCalRulesByFinprodId(finprodId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 现金流支付规则查询
	 * @param finprodId
	 * @return
	 */
	public List<ModCashFlowPayRulesDto>  getCashFlowPayRulesByFinprodId(String finprodId){
		try{
			if(StringUtil.isEmpty(finprodId)){
				logger.info("资产代码不能为空！");
				throw new Exception("资产代码不能为空!");
			}
			return modAssetInfoMapper.getCashFlowPayRulesByFinprodId(finprodId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 股票行情查询
	 * @param paraMap
	 * @return
	 */
	public List<MstSharePrice> getStockMarketInfo(ModStockMarketParamsDto paraMap){
		try {
			if(null == paraMap.getSearchDate()){
				logger.info("查询日不能为空！");
				throw new Exception("查询日不能为空!");
			}
			return modAssetInfoMapper.getStockMarketInfo(paraMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 股票行情区间查询
	 * @param paraMap
	 * @return
	 */
	public List<MstSharePrice> getStockMarketIntervalInfo(ModStockMarketParamsDto paraMap){
		try{
			if(null == paraMap.getSearchDate()||0==paraMap.getSearchNum()){
				logger.info("查询结束日不能为空，查询数量不能为0！");
				throw new Exception("查询结束日不能为空，查询数量不能为0!");
			}
			return modAssetInfoMapper.getStockMarketInfo(paraMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取评级信息
	 * @return
	 */
	public ModRateDataDto getRateInfo(ModRateParamsDto dto) {
		ModRateDataDto rateResult = null;
		try{
			if(CheckObjectNullUtils.checkObjFieldsIsNull(dto)){
				logger.info("参数不能为空！");
				throw new Exception("参数不能为空!");
			}
			//查询参数封装
			Map<String,Object> paraMap = new HashMap<String,Object>();
			paraMap.put("ocode", dto.getRateId());
			paraMap.put("gradedate",dto.getSearchDate());
			//证券品种 = 数据字典.证券品种_模型.债券
			if(DictCodeListExt1.证券品种_债券.getValue().equals(dto.getModSecType())) {
				paraMap.put("otype",ModelDictCodeList.模型_评级类型_债项评级.getValue());
				paraMap.put("resource",ModelDictCodeList.模型_评级来源类型_外部.getValue());
				rateResult = modAssetInfoMapper.selectRateDataByMap(paraMap);
				if(rateResult == null) {
					paraMap.put("otype",ModelDictCodeList.模型_评级类型_主体评级.getValue());
					rateResult = modAssetInfoMapper.selectRateDataByMap(paraMap);
					if(rateResult == null) {
						paraMap.put("otype",ModelDictCodeList.模型_评级类型_债项评级.getValue());
						paraMap.put("resource",ModelDictCodeList.模型_评级来源类型_内部.getValue());
						rateResult = modAssetInfoMapper.selectRateDataByMap(paraMap);
						if( rateResult == null ) {
							paraMap.put("otype",ModelDictCodeList.模型_评级类型_主体评级.getValue());
							rateResult = modAssetInfoMapper.selectRateDataByMap(paraMap);
						}
					}
				}
			}else {
				paraMap.put("otype",ModelDictCodeList.模型_评级类型_主体评级.getValue());
				paraMap.put("resource",ModelDictCodeList.模型_评级来源类型_内部.getValue());
				rateResult = modAssetInfoMapper.selectRateDataByMap(paraMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rateResult;
	}
	
	/**
	 * 曲线行情查询(按曲线代码进行分组，并按关键年限点进行升序排列)
	 * 金茂东修改 ：
	 * @param dto
	 * @return
	 */
	public Map<String,List<ModYieldCurveInfoDto>> getCurveInfo(ModYeildCurveParamsDto dto){
		try {
			Map<String,List<ModYieldCurveInfoDto>> resultMap = new HashMap<String,List<ModYieldCurveInfoDto>>();
			if(null == dto.getYeildIdList() || null == dto.getSearchDate()){
				logger.info("参数不能为空！");
				throw new Exception("参数不能为空!");
			}
			// 查询曲线行情信息
			List<ModYieldCurveInfoDto> resultList = modAssetInfoMapper.selectYieldCurveInfo(dto);
			if(CollectionUtils.isEmpty(resultList)){
				return resultMap;
			}
			resultMap = resultList.stream().collect(Collectors.groupingBy(item -> item.curveCode, Collectors.toList()));
			List<ModYieldCurveInfoDto> tempList = new ArrayList<ModYieldCurveInfoDto>();
			for(String key:resultMap.keySet()){
				tempList = resultMap.get(key);
				ModYieldCurveInfoDto maxDate = tempList.stream().max(Comparator.comparing(ModYieldCurveInfoDto::getCurveDate)).get();
				tempList =  tempList.stream().filter(item -> DateUtil.compareDate(item.getCurveDate(),maxDate.getCurveDate()) == 0).sorted(Comparator.comparing(ModYieldCurveInfoDto::getKeyPoint)).collect(Collectors.toList());
				resultMap.put(key,tempList);
			}
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 曲线行情区间查询
	 * @param dto
	 * @return
	 */
	public Map<String,Map<Date,List<ModYieldCurveInfoDto>>> getYeildCurveIntervalinfo(ModYeildCurveParamsDto dto){
		Map<String,Map<Date,List<ModYieldCurveInfoDto>>> resultMap = new HashMap<String,Map<Date,List<ModYieldCurveInfoDto>>>();
		try {
			if(CheckObjectNullUtils.checkObjFieldsIsNull(dto)){
				logger.info("参数不能为空！");
				throw new Exception("参数不能为空!");
			}
			Map<String,List<ModYieldCurveInfoDto>> codeMap = new HashMap<String,List<ModYieldCurveInfoDto>>();
			// 查询曲线行情信息
			List<ModYieldCurveInfoDto> resultList = modAssetInfoMapper.selectYieldCurveInfo(dto);
			//曲线代码分组后结果
			codeMap = resultList.stream().collect(Collectors.groupingBy(item -> item.curveCode, Collectors.toList()));
			List<ModYieldCurveInfoDto> tempList = new ArrayList<ModYieldCurveInfoDto>();
			Map<Date,List<ModYieldCurveInfoDto>> tempMap = new HashMap<Date,List<ModYieldCurveInfoDto>>();
			// 曲线日期分组
			for(String key:codeMap.keySet()){
				tempList = codeMap.get(key);
				tempMap = tempList.stream().sorted(Comparator.comparing(ModYieldCurveInfoDto::getKeyPoint)).collect(Collectors.groupingBy(item -> item.curveDate, Collectors.toList()));
				resultMap.put(key, tempMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
		
	}


}

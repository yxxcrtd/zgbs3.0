package com.joyintech.tams.invest.common.risktrialrds.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risktrialrds.dict.SupervisionStat;
import com.joyintech.tams.invest.common.risktrialrds.dto.ExprTreeMath;
import com.joyintech.tams.invest.common.risktrialrds.dto.InvestTradeTrialInput;
import com.joyintech.tams.invest.common.risktrialrds.dto.PointDetail;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.SupervisionParameterDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.SupervisionTargetDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.SystemParam;
import com.joyintech.tams.invest.common.risktrialrds.reflect.PluginClass;
import com.joyintech.tams.invest.common.risktrialrds.reflect.PluginClassDao;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;


@Service("riskTrialCommCallableRds")
public class RiskTrialCommCallable {

	/**
	 * 日志
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SupervisionCalcSvc supervisionCalcSvc;

	
	public List<RiskTrialRetDto> getRiskTrialValue(List<SupervisionTargetDto> taskList,InvestTradeTrialInput inputDto) {
		List<RiskTrialRetDto> riskTrialRetList=new ArrayList<RiskTrialRetDto>();
		for (SupervisionTargetDto rskTaskDto : taskList) {
			// 构造
			SystemParam sysParam = new SystemParam();
			sysParam.setValDate(inputDto.getValDate());
			sysParam.setAssetCode(inputDto.getAssetCode());
			//add by 裴家俊     2019-02-25  添加交易编号条件作为去除本身这笔流程中交易
			sysParam.setTradeCode(RiskUtil.convertPortfCode(StringUtil.isNotEmpty(inputDto.getTradeCode())?inputDto.getTradeCode():"999999999"+Const.tradefix));
			// supervision point
			String objectCode = rskTaskDto.getPortfolioLevel().equals(BussinessDictCodeListRds.组合层次_组合包.getValue())?rskTaskDto.getPortfConstId():
					rskTaskDto.getPortfolioId();
	
			try {
				sysParam.setPortfolioCode(RiskUtil.convertPortfCode(objectCode));// 单个组合成分/组合包成分
				// 调用基础算法运算符
				ExprTreeMath exprTree = new ExprTreeMath(rskTaskDto.getExpr());
				Set<String> paramNames = exprTree.getParamList();
				Map<String, Object> params = new HashMap<String, Object>();
				boolean logFlag = true;
				for (String paramName : paramNames) {
					SupervisionParameterDto indicatorParam = supervisionCalcSvc.getIndicatorParam(paramName,
							BussinessDictCodeListRds.参数粒度_实时.getValue());
					if (BussinessDictCodeListRds.指标参数实现类型_SQL.getValue().equals(indicatorParam.getParaDisplayType())) {
						String sql = indicatorParam.getContent();
						Object paramValue = supervisionCalcSvc.getIndicatorParamValue(sql, sysParam, rskTaskDto.getIndType(),rskTaskDto.getFoulCond(),"01");// 获取每个参数实际值
						if (paramValue == null) {
							if(rskTaskDto.getIndType().indexOf(RiskUtil.LIMIT)!=-1){
								params.put(paramName,new BigDecimal(0));
							}
							else{
								//logFlag = false;
								params=null;
							}
							logger.warn("组合(包)代码:" + objectCode + "指标名称:" + rskTaskDto.getIndName() + "参数:" + paramName
									+ "值为null\n");
							
						}
						else{
							if (params != null){
								params.put(paramName,paramValue);
							}
						}
						
					}
					if (BussinessDictCodeListRds.指标参数实现类型_类名.getValue().equals(indicatorParam.getParaDisplayType())) {
						String className = indicatorParam.getContent();
						PluginClass pluginClass = RiskUtil.getBean(className.trim());
						pluginClass.setSysParameter(inputDto,sysParam);
						Object paramValue = pluginClass.getParameterValue();// 获取每个参数实际值
						if (paramValue == null) {
							if(rskTaskDto.getIndType().indexOf(RiskUtil.LIMIT)!=-1){
								params.put(paramName,new BigDecimal(0));
							}
							else{
								//logFlag = false;//禁止项为空时，默认是正常的
								params=null;
							}
							logger.warn("组合(包)代码:" + objectCode + "指标名称:" + rskTaskDto.getIndName() + "参数:" + paramName
									+ "值为null\n");
						}
						else{
							params.put(paramName,paramValue);
						}
					}
					if (BussinessDictCodeListRds.指标参数实现类型_固定值.getValue().equals(indicatorParam.getParaDisplayType())) {
						params.put(paramName, indicatorParam.getContent());
					}
					
					if(params != null && params.get(paramName) != null && BussinessDictCodeListRds.是否_是.getValue().equals(indicatorParam.getIsMath())&&rskTaskDto.getIndType().indexOf(RiskUtil.LIMIT)!=-1){
						params.put(paramName,ArithmeticUtil.doAdd(new  BigDecimal(params.get(paramName).toString()),inputDto.getTradeAmount()));
					}
				}
				if (!logFlag) {
					continue;
				}
				Object indicatorValue=null;
				if(null!=params){
					String exprMath=exprTree.replaceParamByMath(rskTaskDto.getExpr(), params,rskTaskDto.getIndType());
					//Object indicatorValue = exprTree.calc(params);// 实际指标值
					try{
					 indicatorValue = exprTree.calc(exprMath);// 实际指标值
					}catch (Exception e) {
						logger.warn(
								"组合(包)代码:" + objectCode + "指标名称:" + rskTaskDto.getIndName() + "监督实际值出现分母为0的情况");
						continue;
					}
					 if (indicatorValue instanceof Double) {
							Double checkDou = (Double) indicatorValue;
							if (Double.isInfinite(checkDou) || Double.isNaN(checkDou)) {
								logger.warn(
										"组合(包)代码:" + objectCode + "指标名称:" + rskTaskDto.getIndName() + "监督实际值非法或为空，可能出现分母为0的情况");
								continue;
							}
						}
				}
				
				SupervisionStat supStat=null;
				if(rskTaskDto.getIndType().indexOf(RiskUtil.FORBID)!=-1&&null==indicatorValue){
					  supStat= SupervisionStat.NORMAL;
				}
				else{
					// 调用阈值解析
					PointDetail pointDetail = new PointDetail(rskTaskDto.getFoulCond(),rskTaskDto.getApproveCond(),rskTaskDto.getWarnCond(),rskTaskDto.getIndType());
					 supStat = pointDetail.check(indicatorValue);
				}
	            if(null != indicatorValue ){
					//构造结果实体
					RiskTrialRetDto riskTrialRetDto = new RiskTrialRetDto();
					//----modify by 裴家俊  2019/03/18-------start---
					riskTrialRetDto.setPortfolioId(rskTaskDto.getPortfolioId());
					//----modify by 裴家俊  2019/03/18-------end---
					riskTrialRetDto.setIndName(rskTaskDto.getIndName());
					riskTrialRetDto.setIndId(rskTaskDto.getIndId());
					riskTrialRetDto.setIndType(rskTaskDto.getIndType());
					riskTrialRetDto.setSupDate(DateUtil.parse(sysParam.getValDate()));
					//---add by 裴家俊  2019/03/18--------start----------
					riskTrialRetDto.setPortfolioLevel(rskTaskDto.getPortfolioLevel());
					//---add by 裴家俊  2019/03/18--------end----------
					if(StringUtil.isNotEmpty(indicatorValue))
						riskTrialRetDto.setSupVal(indicatorValue.toString());
					riskTrialRetDto.setSupStat(supStat.name());
					//riskTrialRetDto.setWarnCond(pointDetail.showWThreshold());
					//riskTrialRetDto.setFoulCond(pointDetail.showVThreshold());
					riskTrialRetDto.setWarnCond(rskTaskDto.getWarnCond());
					riskTrialRetDto.setFoulCond(rskTaskDto.getFoulCond());
					riskTrialRetList.add(riskTrialRetDto);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return riskTrialRetList;
	}
}

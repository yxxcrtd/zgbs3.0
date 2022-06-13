package com.joyintech.tams.invest.common.risktrial.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.risktrial.dict.SupervisionStat;
import com.joyintech.tams.invest.common.risktrial.dto.ExprTreeMath;
import com.joyintech.tams.invest.common.risktrial.dto.InvestTradeTrialInput;
import com.joyintech.tams.invest.common.risktrial.dto.PointDetail;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrial.dto.SupervisionParameterDto;
import com.joyintech.tams.invest.common.risktrial.dto.SupervisionTargetDto;
import com.joyintech.tams.invest.common.risktrial.dto.SystemParam;
import com.joyintech.tams.invest.common.risktrial.reflect.PluginClass;
import com.joyintech.tams.invest.common.risktrial.reflect.PluginClassDao;
import com.joyintech.tams.invest.common.risktrial.util.Const;
import com.joyintech.tams.invest.common.risktrial.util.RiskUtil;


@Service
public class RiskTrialCommCallable {

	/**
	 * 日志
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	 private static final List<String> 资产_基金_债券二级 = Arrays.asList(DictCodeListExt1.指标类型_禁止投资_资产类型.getValue(),
	            DictCodeListExt1.指标类型_禁止投资_基金类型.getValue(),
	            DictCodeListExt1.指标类型_禁止投资_债券二级类型.getValue());
	 
	@Autowired
	private SupervisionCalcSvc supervisionCalcSvc;

	
	public List<RiskTrialRetDto> getRiskTrialValue(List<SupervisionTargetDto> taskList,InvestTradeTrialInput inputDto) {
		List<RiskTrialRetDto> riskTrialRetList=new ArrayList<RiskTrialRetDto>();
		for (SupervisionTargetDto rskTaskDto : taskList) {
			String exprMath="";
			// 构造
			SystemParam sysParam = new SystemParam();
			sysParam.setValDate(inputDto.getValDate());
			sysParam.setAssetCode(inputDto.getAssetCode());
			//add by 裴家俊     2019-02-25  添加交易编号条件作为去除本身这笔流程中交易
			sysParam.setTradeCode(RiskUtil.convertPortfCode(StringUtil.isNotEmpty(inputDto.getTradeCode())?inputDto.getTradeCode():"999999999"+Const.tradefix));
			// supervision point
			//---add by jjpei 20190816--塞交易dto进参数实体中-------
			sysParam.setAssetType(inputDto.getAssetType());
			sysParam.setAssetSecType(inputDto.getAssetSecType());
			sysParam.setAssetThrType(inputDto.getAssetThrType());
			sysParam.setInvestProporty(inputDto.getInvestProporty());
			sysParam.setTrdProductDeal(inputDto.getTrdProductDeal()!=null?inputDto.getTrdProductDeal():new TrdProductDeal());
			//---add by jjpei 20190816--塞交易dto进参数实体中-------
			String objectCode = rskTaskDto.getPortfolioLevel().equals(DictCodeListExt1.组合层次_组合包.getValue())?rskTaskDto.getPortfConstId():
					rskTaskDto.getPortfolioId();
	
			try {
				sysParam.setPortfolioCode(RiskUtil.convertPortfCode(objectCode));// 单个组合成分/组合包成分
				// 调用基础算法运算符
				//ExprTree exprTree = new ExprTree(rskTaskDto.getExpr());
				ExprTreeMath exprTree = new ExprTreeMath(rskTaskDto.getExpr());
				//Set<String> paramNames = exprTree.getParamNameSet();
				Set<String> paramNames = exprTree.getParamList();
				Map<String, Object> params = new HashMap<String, Object>();
				boolean logFlag = true;
				for (String paramName : paramNames) {
					/*//禁止类型中不需要查询库
					if(资产_基金_债券二级.contains(rskTaskDto.getIndType())){
						if(DictCodeListExt1.指标类型_禁止投资_资产类型.getValue().equals(rskTaskDto.getIndType())){
							params.put(paramName,inputDto.getAssetType());
						}
						else{
							if(StringUtil.isNotEmpty(inputDto.getAssetSecType())){
								params.put(paramName,inputDto.getAssetSecType());
							}
							else
								logFlag = false;
						}
						continue;
					}*/
					
					SupervisionParameterDto indicatorParam = supervisionCalcSvc.getIndicatorParam(paramName,
							DictCodeList.指标参数粒度_实时.getValue());
					if (DictCodeListExt1.指标参数实现类型_SQL.getValue().equals(indicatorParam.getParaDisplayType())) {
						String sql = indicatorParam.getContent();
						Object paramValue = supervisionCalcSvc.getIndicatorParamValue(sql, sysParam, rskTaskDto.getIndType(),rskTaskDto.getFoulCond(),"01");// 获取每个参数实际值
						if (paramValue == null && params != null) {
							if(rskTaskDto.getIndType().indexOf(RiskUtil.LIMIT)!=-1){
								params.put(paramName,new BigDecimal(0));
							}
							else{
								params=null;
							}
							logger.warn("组合(包)代码:" + objectCode + "指标名称:" + rskTaskDto.getIndName() + "参数:" + paramName
									+ "值为null\n");
						}
						else{
							if(params != null){
								params.put(paramName,paramValue);	
							}
						}
						
					}
					if (DictCodeListExt1.指标参数实现类型_类名.getValue().equals(indicatorParam.getParaDisplayType())) {
						String className = indicatorParam.getContent();
						PluginClass plugclass = (PluginClass) Class.forName(RiskUtil.COMMON_PLUGIN_NAME+className.trim()).newInstance();
						plugclass.setSysParameter(sysParam,
								(PluginClassDao) SpringContextUtil.getBean(RiskUtil.COMMON_PLUGIN_REFLECT),inputDto);
						Object paramValue = plugclass.getParamterValue();// 获取每个参数实际值
						if (paramValue == null) {
							if(rskTaskDto.getIndType().indexOf(RiskUtil.LIMIT)!=-1 && params != null){
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
							if (params != null){
								params.put(paramName,paramValue);
							}
						}
					}
					if (params != null && DictCodeListExt1.指标参数实现类型_固定值.getValue().equals(indicatorParam.getParaDisplayType())) {
						params.put(paramName, indicatorParam.getContent());
					}
					//-----add by jjpei 20190815---是否参与画面计算跟资产类型或投资性质比较---start------
					indicatorParam.setIsMath(checkIsMath(indicatorParam,inputDto));
					//-----add by jjpei 20190815---是否参与画面计算跟资产类型或投资性质比较---end------
					if(DictCodeList.是否_是.getValue().equals(indicatorParam.getIsMath())&&rskTaskDto.getIndType().indexOf(RiskUtil.LIMIT)!=-1){
//						params.put(paramName,ArithmeticUtil.doAdd(new  BigDecimal(params.get(paramName).toString()),inputDto.getTradeAmount()));
						
						// etliu特殊处理: XJDQYNLGZCCZZ (分子（现金+到期日在一年内的（国债持仓市值+中央银行票据持仓市值+政策性金融债持仓市值））)
						// 若是【到期日在一年以内的国债、中央银行票据和政策性金融债券】，则不处理，否则，则买入减现金，卖出加现金
						if (StringUtil.equals("XJDQYNLGZCCZZ", paramName)) {
						    boolean needSpecialFlg = false;
						    if (StringUtil.isNotBlank(inputDto.getAssetCode()) 
						        && StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), inputDto.getAssetType())
						        && (StringUtil.equals(DictCodeList.WIND一级分类_含债券一级分类等_国债.getValue(), inputDto.getAssetSecType())
						            || StringUtil.equals(DictCodeList.WIND一级分类_含债券一级分类等_央行票据.getValue(), inputDto.getAssetSecType())
						            || StringUtil.equals(DictCodeList.WIND一级分类_含债券一级分类等_金融债.getValue(), inputDto.getAssetSecType())
						            )
						        ) {
						        FinProductMapper finProductMapper = SpringContextUtil.getBean(FinProductMapper.class);
						        FinProduct finProduct = finProductMapper.selectByPrimaryKey(inputDto.getAssetCode());
						        // 到期日在一年以内
						        if (finProduct != null && finProduct.getMdate() != null
						            && DateUtil.compareDate(finProduct.getMdate(), DateUtil.parse(inputDto.getValDate())) >= 0
						            && DateUtil.compareDate(finProduct.getMdate(), DateUtil.addMonthDate(DateUtil.parse(inputDto.getValDate()), 12)) <= 0) {
						            needSpecialFlg = true;
						        }
						    }
						    
						    // 若是【到期日在一年以内的国债、中央银行票据和政策性金融债券】，则不处理
						    if (needSpecialFlg) {
						        // do nothing
						    }
						    // 否则，则买入减现金，卖出加现金
						    else {
						        BigDecimal tempTradeAmount = inputDto.getTradeAmount();
						        // 拆借、回购特殊处理
						        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_拆入.getValue(), inputDto.getAssetType())
						            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue(), inputDto.getAssetType())
						            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), inputDto.getAssetType())
										|| StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue(), inputDto.getAssetType())) {
						            tempTradeAmount = inputDto.getTradeAmount().abs();
						        }
						        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_拆出.getValue(), inputDto.getAssetType())
                                    || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), inputDto.getAssetType())
										|| StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue(), inputDto.getAssetType())) {
						            tempTradeAmount = inputDto.getTradeAmount().abs().negate();
						        }
						        else {
						            tempTradeAmount = inputDto.getTradeAmount().negate();
						        }

								if(params.get(paramName) != null){
									params.put(paramName,ArithmeticUtil.doAdd(new BigDecimal(params.get(paramName).toString()), tempTradeAmount));
								} 
						    }
						}
                        // etliu特殊处理: SONGSLLCCPTZ((分子)固定收益类理财产品投资于存款、债券等债权类资产的比例不低于80%) 拆出和逆回购 应该是加
                        else if (StringUtil.equals("SONGSLLCCPTZ", paramName) && params != null && params.get(paramName) != null) {
                            if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_拆出.getValue(), inputDto.getAssetType())
                                || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), inputDto.getAssetType())
									|| StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue(), inputDto.getAssetType())) {
                                params.put(paramName,ArithmeticUtil.doAdd(new BigDecimal(params.get(paramName).toString()),inputDto.getTradeAmount().abs()));
                            }
                            else {
                                params.put(paramName,ArithmeticUtil.doAdd(new BigDecimal(params.get(paramName).toString()),inputDto.getTradeAmount()));
                            }
                        }
						// etliu特殊处理: NHGZJYE(逆回购资金余额) 应该是加
						else if (StringUtil.equals("NHGZJYE", paramName) && params != null && params.get(paramName) != null) {
						    params.put(paramName,ArithmeticUtil.doAdd(new BigDecimal(params.get(paramName).toString()),inputDto.getTradeAmount().abs()));
						}
						// etliu 美元债的指标，页面上最好能处理下，非美元债时，值不加到该指标上。
						else if (StringUtil.equals("MYZTZGM", paramName)) {
						    boolean needPlusFlg = false;
						    if (StringUtil.isNotBlank(inputDto.getAssetCode()) 
                                && StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), inputDto.getAssetType())) {
						        FinProductMapper finProductMapper = SpringContextUtil.getBean(FinProductMapper.class);
                                FinProduct finProduct = finProductMapper.selectByPrimaryKey(inputDto.getAssetCode());
                                
                                if (finProduct != null && StringUtil.equals(BussinessDictCodeList.币种_美元.getValue(), finProduct.getCcy())) {
                                    needPlusFlg = true;
                                }
						    }
						    
						    if (needPlusFlg && params.get(paramName) != null) {
						        params.put(paramName,ArithmeticUtil.doAdd(new BigDecimal(params.get(paramName).toString()),inputDto.getTradeAmount()));
						    }
						    else {
						        // 非美元债 do nothing
						    }
							//所有产品投资同一资产的资金总规模不超过300亿的特殊处理
                        }else if(params != null && StringUtil.equals("DCPTZTYZCZJGM", paramName) && params.get(paramName) != null){
							if(StringUtil.isBlank(inputDto.getAssetCode())
									&& (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_拆出.getValue(), inputDto.getAssetType())
										||StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), inputDto.getAssetType())||
											StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue(), inputDto.getAssetType()))){
								params.put(paramName,ArithmeticUtil.doAdd(new BigDecimal(params.get(paramName).toString()),inputDto.getTradeAmount().abs()));
							}else{
								params.put(paramName,ArithmeticUtil.doAdd(new BigDecimal(params.get(paramName).toString()),inputDto.getTradeAmount()));
							}
						}
						// 正常场景
						else {
							if (params != null && params.get(paramName) != null){
								params.put(paramName,ArithmeticUtil.doAdd(new BigDecimal(params.get(paramName).toString()),inputDto.getTradeAmount()));
							}
						}
					}
				}
				if (!logFlag) {
					continue;
				}
				Object indicatorValue=null;
				if(null!=params){
					 exprMath=exprTree.replaceParamByMath(rskTaskDto.getExpr(), params,rskTaskDto.getIndType());
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
					PointDetail pointDetail = new PointDetail(rskTaskDto.getFoulCond(),rskTaskDto.getWarnCond(),rskTaskDto.getIndType());
					 supStat = pointDetail.check(indicatorValue);
				}
	
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
					riskTrialRetDto.setSupVal(indicatorValue==null?"":indicatorValue.toString());
				riskTrialRetDto.setSupStat(supStat.name());
				//riskTrialRetDto.setWarnCond(pointDetail.showWThreshold());
				//riskTrialRetDto.setFoulCond(pointDetail.showVThreshold());
				riskTrialRetDto.setWarnCond(rskTaskDto.getWarnCond());
				riskTrialRetDto.setFoulCond(rskTaskDto.getFoulCond());
				riskTrialRetDto.setExpr(rskTaskDto.getExpr());
				riskTrialRetDto.setInvestedAmountExpr(exprMath);
				riskTrialRetList.add(riskTrialRetDto);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return riskTrialRetList;
	}

	/**
	 * 检查是否参与画面计算
	 * @param indicatorParam
	 * @param inputDto
	 * @return
	 */
	private String checkIsMath(SupervisionParameterDto indicatorParam,InvestTradeTrialInput inputDto){
		boolean isMath=false;//默认不置反
		if(StringUtil.isNotEmpty(indicatorParam.getInvestProporty())&&StringUtil.isNotEmpty(inputDto.getInvestProporty())){
			if(indicatorParam.getInvestProporty().indexOf(inputDto.getInvestProporty())==-1)	{
				isMath=true;//置反
			}
		}
		else if(StringUtil.isNotEmpty(indicatorParam.getParaAssetType())) {
				if (StringUtil.isNotEmpty(inputDto.getAssetType()) && indicatorParam.getParaAssetType().indexOf(inputDto.getAssetType()) == -1) {
					isMath = true;//置反
				}
			}
		if(isMath){
			if(DictCodeList.是否_否.getValue().equals(indicatorParam.getIsMath())){
				indicatorParam.setIsMath(DictCodeList.是否_是.getValue());
			}
			if(DictCodeList.是否_是.getValue().equals(indicatorParam.getIsMath())){
				indicatorParam.setIsMath(DictCodeList.是否_否.getValue());
			}
		}
		return  indicatorParam.getIsMath();
	}
}

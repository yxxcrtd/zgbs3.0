package com.joyintech.tams.invest.common.risktrial.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.risktrial.RiskTrialComm;
import com.joyintech.tams.invest.common.risktrial.dao.RiskTrialCommMapper;
import com.joyintech.tams.invest.common.risktrial.dict.LogLvl;
import com.joyintech.tams.invest.common.risktrial.dict.SupervisionStat;
import com.joyintech.tams.invest.common.risktrial.dto.ExprTreeMath;
import com.joyintech.tams.invest.common.risktrial.dto.InvestTradeTrialInput;
import com.joyintech.tams.invest.common.risktrial.dto.IsupLogDto;
import com.joyintech.tams.invest.common.risktrial.dto.PointDetail;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDetailDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrial.dto.SupervisionParameterDto;
import com.joyintech.tams.invest.common.risktrial.dto.SupervisionTargetDto;
import com.joyintech.tams.invest.common.risktrial.dto.SystemParam;
import com.joyintech.tams.invest.common.risktrial.dto.Task;
import com.joyintech.tams.invest.common.risktrial.reflect.PluginClass;
import com.joyintech.tams.invest.common.risktrial.reflect.PluginClassDao;
import com.joyintech.tams.invest.common.risktrial.service.RiskTrialCommCallable;
import com.joyintech.tams.invest.common.risktrial.service.SupervisionCalcSvc;
import com.joyintech.tams.invest.common.risktrial.service.TaskQueue;
import com.joyintech.tams.invest.common.risktrial.util.Const;
import com.joyintech.tams.invest.common.risktrial.util.RiskUtil;
import com.joyintech.tams.invest.common.risktrial.util.TradeCommUtil;

/**
 * 风险试算入口 〈功能详细描述〉
 * 
 * @author 裴家俊
 * @version 1.0 2018年11月28日
 * @see RiskTrialCommImpl
 * @since 1.0
 */
@Component
public class RiskTrialCommImpl extends BaseService implements RiskTrialComm {

	/**
	 * 日志
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SupervisionCalcSvc supervisionCalcSvc;

	@Autowired
	private RiskTrialCommMapper riskTrialCommMapper;

	@Autowired
	private RiskTrialCommCallable riskTrialCommCallable;

	/**
	 * 按交易代码获取多笔交易是否超审批线
	 * getRiskExceedApproveByTradeId(RiskTrialRetDto.class,{tradeId})
	 * @param riskMap  以key值存的是workflowId，value为画面传过去的风险实体
	 * @param <T>
	 * @return 以各工作流编号为key，存储以审批线1，2，3为key，value为boolean类型的值结构的Map
	 */
	@Override
	public <T extends RiskTrialRetDto> Map<String, Object> getRiskExceedApproveByEntity(Map<String,List<T>> riskMap) {
		Map<String,Object> mapRetList= new HashMap<>();
		if(StringUtil.isNotEmpty(riskMap)){
			riskMap.keySet().forEach(key->{
				if(!StringUtil.isEmptyList(riskMap.get(key))){
					//获取是否超审批标识
					Map<String,Object> mapRet= dealGetApproval(riskMap.get(key));
					mapRetList.put(key,mapRet);
				}
			});
			return mapRetList;
		}
		return null;
	}

	/**
	 * 按工作流代码获取多笔交易是否超审批线
	 * 调用示例 getRiskExceedApproveByFlowId(RiskTrialRetDto.class,{workflowId})
	 * @param workflowArr 工作流代码(可传多个)
	 * @return 以各工作流代码为key，存储以审批线1，2，3为key，value为boolean类型的值结构的Map
	 */
	@Override
	public  Map<String, Object> getRiskExceedApproveByFlowId(String... workflowArr) {
		Map<String,Object> mapRetList= new HashMap<>();
		if(StringUtil.isNotEmpty(workflowArr)){
			for(String workflowId:workflowArr) {
				Map<String, Object> map = new HashMap<>();
				map.put("workflowId", workflowId);
				map.put("beanName", com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil.RISK_RESULT_ENTITY);
				// 获取业务entity
				String riskRet = restTemplate.postForObject(
						ServiceApplication.COMMON_SERVICE_NAME + "/core/getBusinessEntityByWorkFlowId", map,
						String.class);
				Map<String, Object> riskRetJson = JsonUtils.json2Object(riskRet, Map.class);
				if (StringUtil.isNotEmpty(riskRetJson.get(com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil.RISK_RESULT_ENTITY))) {
					List<RiskTrialRetDto> riskList = (List<RiskTrialRetDto>) riskRetJson.get(com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil.RISK_RESULT_ENTITY);
					if (!StringUtil.isEmptyList(riskList)) {
						//获取是否超审批标识
						Map<String,Object> mapRet= dealGetApproval(riskList);
						mapRetList.put(workflowId,mapRet);
					}
				}
			}
			return mapRetList;
		}
		return null;
	}

	/**
	 * 按交易代码获取多笔交易是否超审批线
	 * getRiskExceedApproveByTradeId(RiskTrialRetDto.class,{tradeId})
	 * @param t
	 * @param tradeArr 交易代码(可传多个)
	 * @param <T>
	 * @return 以各交易编号为key，存储以审批线1，2，3为key，value为boolean类型的值结构的Map
	 */
	@Override
	public <T extends RiskTrialRetDto> Map<String, Object> getRiskExceedApproveByTradeId(Class<T> t,String... tradeArr) {
		Map<String,Object> mapRetList= new HashMap<>();
		if(StringUtil.isNotEmpty(tradeArr)){
			for(String tradeId:tradeArr) {
				Map<String, Object> map = new HashMap<>();
				map.put("tradeId", tradeId);
				List<T> riskList =  restTemplate.postForList(ServiceApplication.COMMON_SERVICE_NAME + "/RiskSelect/select",
						map, t);
				if (!StringUtil.isEmptyList(riskList)) {
					//获取是否超审批标识
					Map<String,Object> mapRet= dealGetApproval(riskList);
					mapRetList.put(tradeId,mapRet);
				}
			}
			return mapRetList;
		}
		return null;
	}
	
	@Override
	public List<RiskTrialRetDto> actualRiskTrialDeal(InvestTradeTrialInput inputDto) throws InterruptedException {
		TradeCommUtil.tradeAmountDeal(inputDto);
		
		List<RiskTrialRetDto> riskTrialRetList=new ArrayList<RiskTrialRetDto>();
		inputDto.setIsEnabled(DictCodeListExt1.使用状态_启用.getValue());
		
		// etliu添加，是否需要按照assetThrType进行条件过滤
		if (StringUtil.equals(DictCodeList.WIND一级分类_含债券一级分类等_金融债.getValue(), inputDto.getAssetSecType())
		    || StringUtil.equals(DictCodeList.基金类资产类型_非货币基金.getValue(), inputDto.getAssetSecType())
				|| StringUtil.equals(DictCodeList.项目类一级分类_非标债权.getValue(), inputDto.getAssetSecType())) {
		    inputDto.setNeedAssetThrType(true);
		}
		else {
		    inputDto.setNeedAssetThrType(false);
		}
		
		// 获取指标任务集合
		List<SupervisionTargetDto> taskList = riskTrialCommMapper.getPortfIndTask(inputDto);
		/*try {
			 riskTrialRetList=riskTrialCommCallable.getRiskTrialValue(taskList, inputDto);
		} catch (Exception e) {
		}*/
        final ExecutorService executor = Executors.newCachedThreadPool();
        final List<Callable<List<RiskTrialRetDto>>> taskCallableList = new ArrayList<>();
        final Map<String,List<SupervisionTargetDto>> mapTaskMap=taskList.stream().collect(Collectors.groupingBy(SupervisionTargetDto::getIndType));
		mapTaskMap.keySet().forEach(key -> 
			taskCallableList.add(new Callable<List<RiskTrialRetDto>>() {
	            @Override
	            public List<RiskTrialRetDto> call()
	                throws Exception {
	                logger.debug("按指标类型多线程计算指标");
	                List<RiskTrialRetDto> listIndType=riskTrialCommCallable.getRiskTrialValue(mapTaskMap.get(key), inputDto);
	                riskTrialRetList.addAll(listIndType);
	                return riskTrialRetList;
	            }
	        })
		);
		executor.invokeAll(taskCallableList);
	    executor.shutdown();
		return riskTrialRetList;
	}

	@Override
	public void afterRiskTrialDeal(TaskQueue taskQueue) {
		Task task = taskQueue.pop();
		
		while(task != null) {
			// 构造
			SystemParam sysParam = new SystemParam();
			sysParam.setValDate(Const.DATE_FORMAT.format(task.getDate()));
			//---------add by jjpei 20190829----TOUGUAN784,容错-------
			sysParam.setTrdProductDeal(new TrdProductDeal());
			//---------add by jjpei 20190829----TOUGUAN784,容错-------
			// supervision point
			SupervisionTargetDto point = task.getPoint();
			String objectCode = point.getPortfolioLevel().equals(DictCodeListExt1.组合层次_组合包.getValue())?point.getPortfConstId():point.getPortfolioId();
	
			try {
				sysParam.setPortfolioCode(RiskUtil.convertPortfCode(objectCode));// 单个组合成分/组合包成分
				// 调用基础算法运算符
				ExprTreeMath exprTree = new ExprTreeMath(point.getExpr());
				Set<String> paramNames = exprTree.getParamList();
				Map<String, Object> params = new HashMap<String, Object>();
				boolean logFlag = true;
				Map<String, Object> logMap = new HashMap<String, Object>();
				for (String paramName : paramNames) {
					SupervisionParameterDto indicatorParam = supervisionCalcSvc.getIndicatorParam(paramName,
							DictCodeList.指标参数粒度_事后监控.getValue());
					if (DictCodeListExt1.指标参数实现类型_SQL.getValue().equals(indicatorParam.getParaDisplayType())) {
						String sql = indicatorParam.getContent();
						Object paramValue = supervisionCalcSvc.getIndicatorParamValue(sql, sysParam, point.getIndType(),point.getFoulCond(),"03");// 获取每个参数实际值
						if (paramValue == null) {
							logFlag=false;
							if(logMap.get("eMsg")==null){
								logMap.put("eMsg", " 参数"+paramName+"值为null\n");
							}
							else{
								logMap.put("eMsg", logMap.get("eMsg").toString()+" 参数"+paramName+"值为null\n");
							}
						}
						params.put(paramName, paramValue);
					}
					if (DictCodeListExt1.指标参数实现类型_类名.getValue().equals(indicatorParam.getParaDisplayType())) {
						String className = indicatorParam.getContent();
						PluginClass plugclass = (PluginClass) Class.forName(RiskUtil.COMMON_PLUGIN_NAME+className.trim()).newInstance();
						plugclass.setSysParameter(sysParam,
								(PluginClassDao) SpringContextUtil.getBean(RiskUtil.COMMON_PLUGIN_REFLECT),null);
						Object paramValue = plugclass.getParamterValue();// 获取每个参数实际值
						if (paramValue == null) {
							logFlag = false;
							if(logMap.get("eMsg")==null){
								logMap.put("eMsg", " 参数"+paramName+"值为空\n");
							}
							else{
								logMap.put("eMsg", logMap.get("eMsg").toString()+" 参数"+paramName+"值为空\n");
							}
						}
						params.put(paramName, paramValue);
					}
					if (DictCodeListExt1.指标参数实现类型_固定值.getValue().equals(indicatorParam.getParaDisplayType())) {
						params.put(paramName, indicatorParam.getContent());
					}
					
				}
				if(!logFlag){
					//日志文件
		    		insertLogTrial(point,LogLvl.WARNING,logMap.get("eMsg").toString(),sysParam);
		    		task = taskQueue.pop();
		    		continue;//中止当前进程
				}
				//Object indicatorValue = exprTree.calc(params);// 实际指标值
				boolean mxFlag=exprTree.checkListByParams(params);
				List<Map<String, Object>> proParamList=exprTree.productListByParams(params);
				List<Double> mainIndValue=new ArrayList<Double>();
				
				// 调用阈值解析
				PointDetail pointDetail = new PointDetail(point.getFoulCond(),point.getWarnCond(),point.getIndType());
				
				for(Map<String, Object> param:proParamList){
					String exprMath=exprTree.replaceParamByMath(point.getExpr(), param,point.getIndType());
					Object indicatorValue=null;
					try{
						indicatorValue = exprTree.calc(exprMath);// 实际指标值
					}catch (Exception e) {
						insertLogTrial(point,LogLvl.ERROR,"组合(包)代码:" + objectCode + "指标名称:" + point.getIndName() + "监督实际值非法或为空，可能出现分母为0的情况",sysParam);
						//task = taskQueue.pop();
			    		continue;//中止当前进程
					}
					if (indicatorValue instanceof Double) {
						Double checkDou = (Double) indicatorValue;
						if (Double.isInfinite(checkDou) || Double.isNaN(checkDou)) {
				    		insertLogTrial(point,LogLvl.WARNING,"组合(包)代码:" + objectCode + "指标名称:" + point.getIndName() + "监督实际值非法或为空，可能出现分母为0的情况",sysParam);
				    		//task = taskQueue.pop();
				    		continue;//中止当前进程
						}
					}
		
					//判断出监督状态(正常，违规，预警)
					SupervisionStat supStat = pointDetail.check(indicatorValue);
		
					if(!mxFlag){
						//调用插入明细方法
						insertRiskTrialDetail(param,point,sysParam,indicatorValue,supStat);
						mainIndValue.add(Double.parseDouble(indicatorValue.toString()));
					}
					else{
						insertRiskTrial(point,sysParam,indicatorValue,supStat);
						insertLogTrial(point,LogLvl.INFO,"自动监督成功",sysParam);
					}
					
				}
				if(!mxFlag&&mainIndValue.size()>0){
					//根据阈值判断出最大 或 最小值
					Object indicatorValue = supervisionCalcSvc.getMaxMinValueByListRisk(point.getIndType(), point.getFoulCond(), mainIndValue);
					//判断出监督状态(正常，违规，预警)
					SupervisionStat supStat = pointDetail.check(indicatorValue);
					insertRiskTrial(point,sysParam,indicatorValue,supStat);
					insertLogTrial(point,LogLvl.INFO,"自动监督成功",sysParam);
				}
	    		
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				insertLogTrial(point,LogLvl.ERROR,"自动监督失败："+e.getMessage(),sysParam);
			}
			task = taskQueue.pop();
		}
	}

	private void insertRiskTrial(SupervisionTargetDto point, SystemParam sysParam, Object indicatorValue,
			SupervisionStat supStat) {
		// 构造结果实体
		RiskTrialRetDto riskTrialRetDto = new RiskTrialRetDto();
		riskTrialRetDto.setPortfolioId(point.getPortfolioId());
		riskTrialRetDto.setIndId(point.getIndId());
		riskTrialRetDto.setSupDate(DateUtil.parse(sysParam.getValDate()));
		if(null!=indicatorValue)
			riskTrialRetDto.setSupVal(indicatorValue.toString());
		riskTrialRetDto.setSupStat(supStat.name());
		riskTrialRetDto.setWarnCond(point.getWarnCond());
		riskTrialRetDto.setFoulCond(point.getFoulCond());
		supervisionCalcSvc.deletePointSupervisionValue(riskTrialRetDto);
		supervisionCalcSvc.addPointSupervisionValue(riskTrialRetDto);
	}
	private void insertRiskTrialDetail(Map<String, Object> param,SupervisionTargetDto point,SystemParam sysParam,Object indicatorValue,SupervisionStat supStat){
		//构造结果实体
		RiskTrialRetDetailDto riskTrialRetDto = new RiskTrialRetDetailDto();
		riskTrialRetDto.setPortfolioId(point.getPortfolioId());
		riskTrialRetDto.setIndId(point.getIndId());
		if(null!=param.get(Const.parAstCode))
			riskTrialRetDto.setAssetCode(param.get(Const.parAstCode).toString());
		if(null!=param.get(Const.parAstName))
			riskTrialRetDto.setAssetName(param.get(Const.parAstName).toString());
		riskTrialRetDto.setSupDate(DateUtil.parse(sysParam.getValDate()));
		if(null!=indicatorValue)
			riskTrialRetDto.setSupVal(indicatorValue.toString());
		riskTrialRetDto.setSupStat(supStat.name());
		supervisionCalcSvc.deleteMxPointSupervisionValue(riskTrialRetDto);
		supervisionCalcSvc.addMxPointSupervisionValue(riskTrialRetDto);
	}
	
	private void insertLogTrial(SupervisionTargetDto point,LogLvl logLvl,String message,SystemParam sysParam){
		//日志文件
		IsupLogDto logdto = new IsupLogDto();
		logdto.setLogId(IDGenerator.generate());
		logdto.setPortfolioId(point.getPortfolioId());
		logdto.setMessage(message);
		logdto.setLogLvl(logLvl);
		logdto.setIndId(point.getIndId());
		logdto.setLogTime(DateUtil.parse(sysParam.getValDate()));
		supervisionCalcSvc.writeLog(logdto);
	}

	/**
	 * 比较获取是否超审批线标识
	 * @param riskList
	 * @param <T>
	 * @return
	 */
	private <T extends RiskTrialRetDto> Map<String,Object>  dealGetApproval(List<T> riskList){
		Map<String,Object> mapRet=new HashMap<>();
		//审批线1
		mapRet.put(com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil.APPROVE_LINE_ONE, StringUtil.isNotEmpty(riskList.get(0).getTotalConclu()) ? StringUtil.equals(riskList.get(0).getTotalConclu(), BussinessDictCodeListRds.指标状态_审批.getValue()) : false);
		//审批线2，3暂未开发,默认为false
		mapRet.put(com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil.APPROVE_LINE_TWO, false);
		mapRet.put(com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil.APPROVE_LINE_THR, false);
		return mapRet;
	}
}

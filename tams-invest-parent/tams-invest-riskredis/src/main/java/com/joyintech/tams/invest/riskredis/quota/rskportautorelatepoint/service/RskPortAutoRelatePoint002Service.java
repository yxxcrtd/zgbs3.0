package com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.service;

import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.RskRuleIndicator;
import com.joyintech.tams.common.base.entity.RskRuleInfo;
import com.joyintech.tams.common.base.entity.RskRulePoolConfig;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.prodinfo.BussRuleService;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.dto.RskPortAutoRelatePoint002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

import java.util.*;

@Service
public class RskPortAutoRelatePoint002Service extends CommonExportService<RskPortAutoRelatePoint002Dto>
implements BaseProcessService<RskPortAutoRelatePoint002Dto> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BussRuleService<RskPortAutoRelatePoint002Dto> bussRuleService;

	/**
	 * 线程池
	 */
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	/**
	 * redis数据初始化
	 */
	@Autowired
	private RiskRedisInitService<ImplCond> redisRriter;

	@Override
	public void doComplete(RskPortAutoRelatePoint002Dto entity, OperationType type) {
		String action = StringUtil.EMPTY;
        // entity.setSystemCode("SYS");
        if (OperationType.DELETE.equals(type)) {
            action = "deleteRule";
        }
        else if (OperationType.NEW.equals(type)) {
            action = "insertRule";
        }else{
        	action = "updateRule";
        }
		restTemplate.postForObject(
            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskPortAutoRelatePoint/" + action, entity,
            Integer.class);
        //----------add by 裴家俊-------规则修改时重新关联组合-------20191015-----------
		if(action.equals("insertRule")) {
			//异步匹配组合插入组合包关联组合表
			threadPoolTaskExecutor.execute(()->{
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(),e);
				}
				List<String> portIdList=entity.matchPortfolioInfo((ruleList)->{
					return bussRuleService.matchRulePortForPage(ruleList);
				});
				if(StringUtil.isEmptyList(portIdList)){
					logger.info("--------->规则["+entity.getRuleId()+"]未匹配到产品------------");
					return;
				}
				logger.info("--------->规则["+entity.getRuleId()+"]匹配到产品数："+portIdList.size()+"<----------");
				bussRuleService.insertRulePortList(portIdList, Arrays.asList(entity.getRuleId()),()->null);
			});
		}
		if(action.equals("updateRule")) {
			threadPoolTaskExecutor.execute(()->{
				try {
					Thread.sleep(RiskUtil.getRefreshTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				List<String> portIdList=entity.matchPortfolioInfo((ruleList)->{
					return bussRuleService.matchRulePortForPage(ruleList);
				});
				if(StringUtil.isEmptyList(portIdList)){
					logger.info("--------->规则["+entity.getRuleId()+"]未匹配到产品------------");
					return;
				}
				logger.info("--------->规则["+entity.getRuleId()+"]匹配到产品数："+portIdList.size()+"<----------");
				bussRuleService.insertRulePortList(portIdList,Arrays.asList(entity.getRuleId()),()->null);

				logger.info("----------->修改规则后处理自动关联与redis初始化任务--------------");
				List<String> indIdList=bussRuleService.queryRuleRelInd(entity);
				if(StringUtil.isEmptyList(indIdList)){
					logger.info("---------规则["+entity.getRuleId()+"]未匹配到指标------------");
					return;
				}

				logger.info("----------->异步插入规则包与组合记录及自动关联指标记录开始--------------");
				bussRuleService.relatePortIndicator(portIdList,indIdList);
				logger.info("----------->异步插入规则包与组合记录及自动关联指标记录结束--------------");
				if (CommonUtil.isType(CacheUtil.getSysParam(RiskUtil.PAGE_REDIS_FLAG), BussinessDictCodeListRds.是否_是)) {
					logger.info("----------->规则包编辑装载redis开始--------------");
					ImplCond cond = new ImplCond();
					String[] prodIdList = portIdList.toArray(new String[portIdList.size()]);
					cond.setPortfIdArray(prodIdList);
					redisRriter.dealRiskPtlIndicator(cond);
					logger.info("----------->规则包编辑装载redis结束--------------");
				}
			});
		}

	}

	/**
	 * 根据规则编号查询规则信息
	 * @param ruleId 规则编号
	 * @return 规则信息
	 */
	public RskRuleInfo getBussinessEntityByRuleId(String ruleId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ruleId", ruleId);
		return restTemplate.postForObject(
				ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskPortAutoRelatePoint/getBussinessEntityByRuleId",
				map, RskRuleInfo.class);
	}

	/**
	 * 根据规则编号查询规则指标信息
	 * @param ruleId 规则编号
	 * @param indId 指标编号
	 * @return 规则信息
	 */
	public RskRuleIndicator getRelInfoByRuleId(String ruleId,String indId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ruleId", ruleId);
		map.put("indId", indId);
		return restTemplate.postForObject(
				ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskPortAutoRelatePoint/getRelInfoByRuleId",
				map, RskRuleIndicator.class);
	}

	public boolean isExist(RskPortAutoRelatePoint002Dto dto) {
		return restTemplate.postForObject(
                ServiceApplication.RISK_RDS_SERVICE_NAME +
                "/RskPortAutoRelatePoint/isExist",
                dto,boolean.class);
	}
	public boolean isAreadyHaveInd(String ruleId) {
		return restTemplate.getForObject(
                ServiceApplication.RISK_RDS_SERVICE_NAME +
                "/RskPortAutoRelatePoint/isAreadyHaveInd?ruleId="+ruleId,boolean.class);
	}
}

package com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.service;

import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.prodinfo.BussRuleService;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.dto.RskPortAutoRelatePoint002Dto;
import com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.dto.RskPortAutoRelatePointDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

@Service
public class RskPortAutoRelatePointService  extends CommonExportService<RskPortAutoRelatePointDto>
implements BaseProcessService<RskPortAutoRelatePointDto> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BussRuleService<RskPortAutoRelatePointDto> bussRuleService;

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
	public void doComplete(RskPortAutoRelatePointDto dto, OperationType type) {
		 String action = StringUtil.EMPTY;
	        // entity.setSystemCode("SYS");
	        if (OperationType.DELETE.equals(type)) {
	            action = "delete";
	        }
	        else if (OperationType.NEW.equals(type)) {
	            action = "insert";
	        }
	        restTemplate.postForObject(
	            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskPortAutoRelatePoint/" + action, dto,
	            Integer.class);
		   //----------add by 裴家俊-------指标添加时重新关联组合-------20191015-----------
			if(action.equals("insert")) {
				threadPoolTaskExecutor.execute(()->{
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(),e);
					}
					logger.info("----------->添加指标后处理自动关联与redis初始化任务--------------");
					//库中结合画面上，为了解决分布式事务一块
					Set<String> pageSet=dto.getSelectedItem().stream().map(entry->entry.getIndId()).collect(Collectors.toSet());
					List<String> indIdList=bussRuleService.queryRuleRelInd(dto);
					Set<String> indIdSet=new HashSet<>(indIdList);
					indIdSet.addAll(pageSet);
					List<String> indIdListNew=new ArrayList<>(indIdSet);
					//库中结合画面上，为了解决分布式事务一块
					if(StringUtil.isEmptyList(indIdList)){
						logger.info("---------规则未匹配到指标------------");
						return;
					}
					List<String> portIdList=bussRuleService.queryRuleRelPort(dto);
					portIdList=StringUtil.isEmptyList(portIdList)?bussRuleService.matchRulePort(dto):portIdList;
					if(StringUtil.isEmptyList(portIdList)){
						logger.info("---------规则[\"+dto.getRuleId()+\"]未匹配到产品------------");
						return;
					}
					logger.info("--------->规则[\"+dto.getRuleId()+\"]匹配到产品数："+portIdList.size()+"<----------");

					logger.info("----------->异步自动关联开始--------------");
					bussRuleService.relatePortIndicator(portIdList,indIdListNew);
					logger.info("----------->异步自动关联结束--------------");
					if (CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
						logger.info("----------->装载redis开始--------------");
						ImplCond cond = new ImplCond();
						String[] prodIdList = portIdList.toArray(new String[portIdList.size()]);
						cond.setPortfIdArray(prodIdList);
						redisRriter.dealRiskPtlIndicator(cond);
						logger.info("----------->装载redis结束--------------");
					}
				});
			}
	}

	public List<TreeNode<RskPortAutoRelatePointDto>> getRuleName(Map<String, Object> map) {
		List<TreeNode<RskPortAutoRelatePointDto>> retList = new ArrayList<>();
        Page<TreeNode<RskPortAutoRelatePointDto>> page = new Page<>();
        // 获得查询得到的指标名称名称
        List<RskPortAutoRelatePointDto> list = restTemplate.postForList(
                ServiceApplication.RISK_RDS_SERVICE_NAME +
                        "/RskPortAutoRelatePoint/getRuleName",
                map,RskPortAutoRelatePointDto.class);
        if (!StringUtil.isEmptyList(list)) {
            // 将查询到的结果赋值树节点
            for (RskPortAutoRelatePointDto dto : list) {
                TreeNode data = new TreeNode();
                data.setId(dto.getRuleId());
                data.setName(dto.getRuleName());
                data.setOpen(true);
                retList.add(data);
            }
            page.setRoot(retList);
            page.setRecords(retList.size());
            return retList;
        }
        else {
            return null;
        }
	}

	public Page<RskPortAutoRelatePointDto> selectForPage(Map<String, Object> pageParam) {
		return restTemplate.postForPage(
                ServiceApplication.RISK_RDS_SERVICE_NAME +
                "/RskPortAutoRelatePoint/selectForPage",
                pageParam,RskPortAutoRelatePointDto.class);
	}

	public Page<RskPortAutoRelatePoint002Dto> selectAllRule(Map<String, Object> pageParam) {
		String pageModel = (String)pageParam.get("pageModel");
		Page<RskPortAutoRelatePoint002Dto> page = new Page<>();
		//如果规则编号不为空是编辑页面
		if(!StringUtil.equals(pageModel, GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
			page = restTemplate.postForPage(
	                ServiceApplication.RISK_RDS_SERVICE_NAME +
	                "/RskPortAutoRelatePoint/selectAllRule",
	                pageParam,RskPortAutoRelatePoint002Dto.class);
		}
		return page;
	}

	public int insertRule(RskPortAutoRelatePointDto dto) {
		return restTemplate.postForObject(
                ServiceApplication.RISK_RDS_SERVICE_NAME +
                "/RskPortAutoRelatePoint/insertRule",
                dto,int.class);
	}

	public int updateRule(RskPortAutoRelatePointDto dto) {
		return restTemplate.postForObject(
                ServiceApplication.RISK_RDS_SERVICE_NAME +
                "/RskPortAutoRelatePoint/updateRule",
                dto,int.class);
	}

	public int deleteRule(RskPortAutoRelatePointDto dto) {
		return restTemplate.postForObject(
                ServiceApplication.RISK_RDS_SERVICE_NAME +
                "/RskPortAutoRelatePoint/deleteRule",
                dto,int.class);
	}

	public Page<RskPortAutoRelatePointDto> findAllInt(Map<String, Object> pageParam) {
		Page<RskPortAutoRelatePointDto> page = new Page<>();
		if(StringUtil.isNotEmpty(pageParam.get("workflowId"))){
			RskPortAutoRelatePointDto dto = this.getBussinessEntityByWorkflowId((String)pageParam.get("workflowId"));
	            List<RskPortAutoRelatePointDto> list = new ArrayList<RskPortAutoRelatePointDto>();
	            if(dto.getSelectedItem() != null){
	                list=dto.getSelectedItem();
	            }
	            else{
	                list.add(dto); 
	            }
	            page.setRoot(list);
	            page.setRecords(list.size());
		}else if(StringUtil.isNotEmpty(pageParam.get("ruleId"))){
			page = restTemplate.postForPage(
	                ServiceApplication.RISK_RDS_SERVICE_NAME +
	                "/RskPortAutoRelatePoint/findAllInt",
	                pageParam,RskPortAutoRelatePointDto.class);
		}
		return page;
	}


}

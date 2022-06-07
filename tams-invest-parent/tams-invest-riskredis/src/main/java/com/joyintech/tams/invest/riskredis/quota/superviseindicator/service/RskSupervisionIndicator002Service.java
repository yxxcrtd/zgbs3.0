package com.joyintech.tams.invest.riskredis.quota.superviseindicator.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.dto.RskPortfolioIndicatorDto;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.dto.RskSupervisionIndicator002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 监督指标查询service 〈功能详细描述〉
 * 
 * @author 裴家俊
 * @version 1.0 2018年10月23日
 * @see RskSupervisionIndicator002Service
 * @since 1.0
 */
@Service
public class RskSupervisionIndicator002Service extends CommonExportService<RskSupervisionIndicator002Dto> implements BaseProcessService<RskSupervisionIndicator002Dto> {

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(RskSupervisionIndicator002Service.class);

    /**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisRriter;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(RskSupervisionIndicator002Dto entity, OperationType type) {
        String action = StringUtil.EMPTY;
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
        }
        else if (OperationType.NEW.equals(type)) {
        	if(entity.getRskPortfolioIndicatorDto().getStartDate()!=null
        			&&DateUtil.compareDate(entity.getRskPortfolioIndicatorDto().getStartDate(), DateUtil.parse(DateUtil.format(DateUtil.platDateFromCache())))<0){
        		entity.getRskPortfolioIndicatorDto().setStartDate(DateUtil.parse(DateUtil.format(DateUtil.platDateFromCache())));
        	}
            action = "insert";
        }
        else {
            action = "update";
        }
        restTemplate.postForObject(
            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/" + action, entity,
            Integer.class);
        String pageRedisFlag = CacheUtil.getSysParam("pageRedisFlag");
        if (CommonUtil.isType(pageRedisFlag, BussinessDictCodeListRds.是否_是)) {
            final String actionRedis=action;
            threadPoolTaskExecutor.execute(()->{
                try {
                    logger.info("===========指标管理进入redis开始=============");
                    Thread.sleep(6000);
                    // 更新Redis
                    ImplCond cond = new ImplCond();
                    cond.setDel(StringUtil.equals(actionRedis, "delete"));
                    cond.setIndicatorCodeArray(entity.getIndId());
                    redisRriter.dealRiskIndicator(cond);

                    if(OperationType.NEW.equals(type)&&!StringUtil.isEmptyList(entity.getSelectedPortfIndItem())){
                        // 更新Redis
                        cond = new ImplCond();
                        cond.setDel(StringUtil.equals(actionRedis, "delete"));
                        List<RskPortfolioIndicatorDto> dtoList = entity.getSelectedPortfIndItem();
                        if (StringUtil.isEmptyList(dtoList)) {
                            cond.setPortfIdArray(entity.getPortfolioId());
                        }
                        else {
                            String[] prodIdList = dtoList.stream().map(item -> item.getPortfolioId()).collect(
                                    Collectors.toList()).toArray(new String[dtoList.size()]);
                            cond.setPortfIdArray(prodIdList);
                        }
                        redisRriter.dealRiskPtlIndicator(cond);
                    }
                    logger.info("===========指标管理进入redis结束=============");
                } catch (InterruptedException e) {
                    logger.error("===========指标管理进入redis异常=============");
                }

            });
        }

    }

    /**
     * 根据监督指标编号查询指标信息
     * 
     * @param indId 指标编号
     * @return 指标信息
     * @see
     * @since 1.0
     */
    public RskSupervisionIndicator002Dto getBussinessEntityByIndId(String indId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("indId", indId);
        return restTemplate.postForObject(
            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/querySuperIndicatorInfoByIndId",
            map, RskSupervisionIndicator002Dto.class);
    }

    /**
     * 根据监督指标编号查询指标信息
     *
     * @param indId 指标编号
     * @return 指标信息
     * @see
     * @since 1.0
     */
    public RskSupervisionIndicator002Dto getBussinessEntityByIndId_RULE(String indId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("indId", indId);
        return simpleRestTemplate.postForObject(
                ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/querySuperIndicatorInfoByIndId_RULE",
                map, RskSupervisionIndicator002Dto.class);
    }

    /**
     * 新建场合时查询监督指标名称是否重复
     * 
     * @param indId
     */
    public int searchRepeatByIndName(String indId, String indName) {
        return restTemplate.getForObject(ServiceApplication.RISK_RDS_SERVICE_NAME
                                               + "/RskSupervisionIndicator/searchRepeatByIndName?indId="
                                               + indId + "&indName=" + indName,
            Integer.class);
    }

    /**
     * 删除时查询指标是否跟组合关联
     * 
     * @param indId
     */
    public int searchReleteCountByIndId(String indId) {
        return restTemplate.getForObject(ServiceApplication.RISK_RDS_SERVICE_NAME
                                               + "/RskSupervisionIndicator/searchReleteCountByIndId?indId="
                                               + indId,
            Integer.class);
    }

    /**
     * 删除时查询指标是否跟规则关联
     * @param indId
     */
    public int searchReleteCountByRuleId(String indId) {
        return restTemplate.getForObject(ServiceApplication.RISK_RDS_SERVICE_NAME
                        + "/RskSupervisionIndicator/searchReleteCountByRuleId?indId="
                        + indId,
                Integer.class);
    }

    public Page<RskSupervisionIndicator002Dto> selectForPage(Map<String, Object> parameters) {
        Page<RskSupervisionIndicator002Dto> page = new Page<RskSupervisionIndicator002Dto>();
        return page;
    }

    public int isConfigure(String indId) {
        return restTemplate.getForObject(
            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/isConfigure?indId="
                                               + indId,
            Integer.class);
    }

}

package com.joyintech.tams.invest.product.messageadjust.maintenance.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.asset.util.AssetCommonUtil;
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
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.product.messageadjust.maintenance.dto.FinOpenPlanMaintenance002Dto;
import com.joyintech.tams.invest.product.messageadjust.maintenance.dto.OpenPlanParamDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * @author zhangliang
 * @version 1.0 2018年11月28日
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FinOpenPlanMaintenance002Service extends BaseService<FinOpenPlanMaintenance002Dto> implements BaseProcessService<FinOpenPlanMaintenance002Dto> {
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(FinOpenPlanMaintenance002Service.class);

    @Autowired
    private BussRuleService bussRuleService;

    /**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisRriter;

    /**
     * 线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	public void doComplete(FinOpenPlanMaintenance002Dto entity, OperationType type) {
        entity.setOperateType(type.toString());
        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinOpenPlanMaintenance/doUpdate",
				entity, Integer.class);
        //---add by 裴家俊 匹配风险规则包来关联指标且装载到redis 20220326 start---
        assembleRedis(entity);
        //---add by 裴家俊 匹配风险规则包来关联指标且装载到redis 20220326 end---
	}

    /**
     * 装载到redis里
     * @param entity
     */
    private void assembleRedis(FinOpenPlanMaintenance002Dto entity){
        if (CommonUtil.isType(CacheUtil.getSysParam(RiskUtil.PAGE_REDIS_FLAG), BussinessDictCodeListRds.是否_是)) {
            String portfolioId=StringUtil.replace(entity.getFinprodId(),
                    DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()+ GlobalConstant.UNDER_LINE,StringUtil.EMPTY);
            bussRuleService.matchRulePortByPort(AssetCommonUtil.getProdInfo(portfolioId,obj->obj));
            threadPoolTaskExecutor.execute(()->{
                try {
                    Thread.sleep(RiskUtil.getRefreshTime());
                    ImplCond cond = new ImplCond();
                    cond.setDel(false);
                    cond.setPortfIdArray(portfolioId);
                    logger.info("----------->修改产品开放计划至组合关联指标进入redis开始--------------");
                    redisRriter.dealRiskPtlIndicator(cond);
                    logger.info("----------->修改产品开放计划至组合关联指标进入redis结束--------------");
                } catch (InterruptedException e) {
                    logger.error("===========修改产品开放计划至组合关联指标进入redis异常=============",e);
                }
            });
        }
    }
	
    public OpenPlanParamDto selectOpenPlanParamDto(String finprodId) {
    	
    	Map<String,Object> paraMap = new HashMap<String,Object>();
    	paraMap.put("finprodId", finprodId);
    	
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
            + "/FinOpenPlanMaintenance/selectOpenPlanParamDto",paraMap,OpenPlanParamDto.class);
        
    }
    
    /**
     * 产品代码联想下拉框
     */
    public List<SelectOptionDto> productCodeList(Map<String,Object> param) {
        //数据字典.金融产品类型.理财产品, 数据字典.金融产品类型.理财产品分层
        param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        param.put("operationType", DictCodeList.开放类型_开放式.getValue());
        return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME
                + "/FinOpenPlanMaintenance/productCodeList",param,SelectOptionDto.class);      
    }
}

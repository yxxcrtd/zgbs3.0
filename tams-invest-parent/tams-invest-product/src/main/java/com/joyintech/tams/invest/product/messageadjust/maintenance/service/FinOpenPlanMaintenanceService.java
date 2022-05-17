/*
 * 文件名：FinOpenPlanMaintenanceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年10月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.product.messageadjust.maintenance.service;

import java.util.ArrayList;
import java.util.Date;
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
import com.joyintech.tams.invest.product.messageadjust.maintenance.dto.FinOpenPlanMaintenance002Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.product.messageadjust.maintenance.dto.FinOpenPlanMaintenanceDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 开放计划维护service
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年10月22日
 * @see FinOpenPlanMaintenanceService
 * @since 1.0
 */
@Service
public class FinOpenPlanMaintenanceService extends CommonExportService<FinOpenPlanMaintenanceDto> 
    implements BaseProcessService<FinOpenPlanMaintenanceDto>{

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

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(FinOpenPlanMaintenanceService.class);

    /**
     * 获取节假日信息
     */
    @Autowired(required=false)
    private DateCalService dateCalService;
    /**
     * 产品代码的联想下拉框
     * <功能详细描述>
     * @param param param
     * @return SelectOptionDto SelectOptionDto
     * @see
     * @since 1.0
     */
    
    public List<SelectOptionDto> getFinprodIds(Map<String,Object> param) {
        if(!StringUtil.isNotEmpty(param.get("initValue"))){
            User user = SecurityUtil.getLoginUser();
            param.put("userName", user.getUserName());
        }
        return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME
            + "/FinOpenPlanMaintenance/getFinprodIds",param,SelectOptionDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(FinOpenPlanMaintenanceDto dto, OperationType type) {
        String action = StringUtil.EMPTY;
        // entity.setSystemCode("SYS");
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
        }
        else if (OperationType.NEW.equals(type)) {
            action = "insert";
        }
        else {
            List<FinOpenPlanMaintenanceDto> selectedDto = dto.getSelectedDto();
            for(FinOpenPlanMaintenanceDto entity:selectedDto){
              //取得营业日准则
                Map<String,Object> param = new HashMap<String,Object>();
                param.put("cashId", dto.getCashId());
                param.put("RANSOM_ISSUE_MODE_03", DictCodeList.申赎开放类型_申赎开放.getValue());
                //营业日准则
                String tradedayRule = getByCashId(param);
                //获取封闭期限
                long closeDays =  getByFinprodId(dto.getFinprodId());
                if(entity.getOpenVdate() != null){
                    Date closeDay = DateUtil.addDay(entity.getOpenVdate(), (int)closeDays);
                  //设置开放周期到期日
                    entity.setOpenMdate(closeDay);
                    //获取交易日历代码
                    String calendarId = getCalendarIdByFinprodId(dto.getFinprodId());
                    //获取调整后的到期日
                    Date mdate = getMdate(entity.getOpenVdate(),entity.getOpenMdate(),calendarId,tradedayRule);
                    entity.setOpenMdate(mdate);
                }
            }
            action = "update";
        }
        restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinOpenPlanMaintenance/" + action, dto,
            Integer.class);
        //---add by 裴家俊 匹配风险规则包来关联指标且装载到redis 20220326 start---
        assembleRedis(dto);
        //---add by 裴家俊 匹配风险规则包来关联指标且装载到redis 20220326 end---
    }

    /**
     * 装载到redis里
     * @param entity
     */
    private void assembleRedis(FinOpenPlanMaintenanceDto entity){
        if (CommonUtil.isType(CacheUtil.getSysParam(RiskUtil.PAGE_REDIS_FLAG), BussinessDictCodeListRds.是否_是)) {
            String portfolioId=StringUtil.replace(entity.getFinprodId(),
                    DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()+ GlobalConstant.UNDER_LINE,StringUtil.EMPTY);
            bussRuleService.matchRulePortByPort(AssetCommonUtil.getProdInfo(portfolioId, obj->obj));
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

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param openVdate openVdate
     * @param openMdate openMdate
     * @param calendarId 交易日历代码
     * @param tradedayRule 营业日准则
     * @return Date
     * @see
     * @since 1.0
     */
    private Date getMdate(Date openVdate, Date openMdate, String calendarId,String tradedayRule) {
      //获取节假日日历信息
        List<String> holidayList = dateCalService.getDealCalendarHoliday(openVdate
            , openMdate, calendarId);
        if(holidayList == null || holidayList.size()<1){
            return openMdate;
        }
        String endDate = holidayList.get((holidayList.size()-1));
        if(openMdate.compareTo(DateUtil.parse(endDate)) > 0){
            return openMdate;
        }
        List<Date> holiList = new ArrayList<Date>();
        if(holidayList != null && holidayList.size()>0){
            for(String holi:holidayList){
                holiList.add(DateUtil.parse(holi));
            }
        }
        
        if(holiList != null && holiList.size()>0 && tradedayRule != null){
            //调整开放周期到期日
            if(StringUtil.equals(tradedayRule, DictCodeList.营业日准则_向后调整.getValue())){
                Date date = AdjustedTradeDayCal.getAdjustedTradeDayByDate(openMdate, tradedayRule, holiList);
//                return getMdate(openVdate,date,calendarId,tradedayRule);
                return date;
            }
        }
        return openMdate;
    }

    /**
     * 页面联动
     * <功能详细描述>
     * @param param param
     * @return FinOpenPlanMaintenanceDto
     * @see
     * @since 1.0
     */
    public List<FinOpenPlanMaintenanceDto> selectForPage(Map<String, Object> param) {
        String workflowId = (String)param.get("workflowId");
        if (StringUtil.isNotEmpty(workflowId)) {
            FinOpenPlanMaintenanceDto dto = this.getBussinessEntityByWorkflowId(
                workflowId);
            if(StringUtil.equals(dto.getFinprodId(), param.get("finprodId"))){
                List<FinOpenPlanMaintenanceDto> list = new ArrayList<>();
                list.addAll(dto.getSelectedDto());
                return list;
            }
        }
        User user = SecurityUtil.getLoginUser();
        param.put("userName", user.getUserName());
        return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME
            + "/FinOpenPlanMaintenance/findPage",param,FinOpenPlanMaintenanceDto.class);
    }
    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
            "/com/joyintech/tams/invest/product/messageadjust/maintenance/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<FinOpenPlanMaintenanceDto> selectPageData(Map<String, Object> selectParam) {
        try {
            List<FinOpenPlanMaintenanceDto> list = selectForPage(selectParam);
            Page<FinOpenPlanMaintenanceDto> page = new Page<FinOpenPlanMaintenanceDto>();
            page.setRoot(list);
            page.setRecords(list.size());
            return page;
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
    /**
     * 根据条件查询开放规则表
     * <功能详细描述>
     * @param param param
     * @return FinOpenRule FinOpenRule
     * @see
     * @since 1.0
     */
    public String getByCashId(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
            + "/FinOpenPlanMaintenance/getByCashId",param,String.class);
    }

    /**
     * 查询封闭期限
     * <功能详细描述>
     * @param finprodId finprodId
     * @return Long Long
     * @see
     * @since 1.0
     */
    public Long getByFinprodId(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME
            + "/FinOpenPlanMaintenance/getByFinprodId?finprodId="+finprodId,Long.class);
    }

    /**
     * 获取交易日历代码
     * <功能详细描述>
     * @param finprodId finprodId
     * @return String
     * @see
     * @since 1.0
     */
    public String getCalendarIdByFinprodId(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME
            + "/FinOpenPlanMaintenance/getCalendarIdByFinprodId?finprodId="+finprodId,String.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param finprodId finprodId
     * @return String
     * @see
     * @since 1.0
     */
    public String getFinprodMarketIdByFinprodId(String finprodId) {
        return restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME
            + "/FinOpenPlanMaintenance/getFinprodMarketIdByFinprodId?finprodId="+finprodId,String.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String,String> sendOpenPlanSale(List<FinOpenPlanMaintenanceDto> dtoList) {
        return restTemplate.postForObject(
                ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/FinProductInterf/sendOpenPlanSale", dtoList,
                Map.class);
    }

}

/*
 * 文件名：EquityBusiInfo001Controller.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：zyf
 * 修改时间：2017年9月19日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */

package com.joyintech.tams.report.submitmanagement.equitybusiinforegist.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.batch.batchstart.service.SysBatchStart001Service;
import com.joyintech.tams.common.base.entity.ChbBatchPara;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.report.common.constant.BatchBeanName;
import com.joyintech.tams.report.common.constant.SvrServiceApplication;
import com.joyintech.tams.report.common.dto.ExcelValidDto;
import com.joyintech.tams.report.submitexamination.checkreport.service.CheckReport001Service;
import com.joyintech.tams.report.submitmanagement.equitybusiinforegist.dto.EquityBusiInfoRegist001Dto;
import com.joyintech.tams.report.submitmanagement.equityinforegist.dto.EquityInfoRegist001Dto;
import com.joyintech.tams.report.system.subinfobatchlog.dto.ChbSubinfoBatchLog;
import com.joyintech.tams.report.system.subinfobatchlog.service.ChbSubinfoBatchLogService;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 *
 * @author zyf
 * @version 1.0 2017年9月19日
 * @see EquityBusiInfoRegistMService
 * @since 1.0
 */
@Service
public class EquityBusiInfoRegistMService extends BaseService<EquityInfoRegist001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(EquityBusiInfoRegistMService.class);
    
    /**
     * 进度条接口
     */
    @Autowired
    private ProgressBarService progressBarService;
    /**
     * 投资者持有数量信息登记
     */
    @Autowired
    private EquityBusiInfoRegist001Service equityBusiInfoRegist001Service;

    @Autowired
    private CheckReport001Service checkReport001Service;

    @Autowired
    private SysBatchStart001Service sysBatchStart001Service;

    /**
     * 债报送信息登记生成日志servcie
     */
    @Autowired
    private ChbSubinfoBatchLogService chbSubinfoBatchLogService;

    /**
     * 进度条ID
     */
    private static final String PROCESS_KEY = "exportProcessBar";


    /**
     * 业务画面查询事件
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    public Page<EquityBusiInfoRegist001Dto> selectForPage(Map<String, Object> param) {
        return restTemplate.postForPage(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/findPage", param,
                EquityBusiInfoRegist001Dto.class);
    }

    /**
     * 查询事件(无分页)
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    public List<EquityBusiInfoRegist001Dto> searchInfo(Map<String, Object> param) {
        return restTemplate.postForList(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/queryEquityBusiInfoRegist",
                param,
                EquityBusiInfoRegist001Dto.class);
    }

    @ResponseBody
    public List<SuggestDto> queryPrdNameList(String param) {
        return restTemplate.postForList(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/queryPrdNameList", param,
                SuggestDto.class);
    }
    /**
     * 产品名称下拉框
     *
     * @return 产品名称列表
     */
    @RequestMapping("getPrdName")
    @ResponseBody
    public List<SuggestDto> getPrdName(Map<String,Object> paramMap) {
        return checkReport001Service.queryPrdNameList(paramMap);
    }


    class MultiThread implements Runnable {
        private String pageId;
        private Integer size;
        public MultiThread(String pageId,Integer size) {
            this.pageId = pageId;
            this.size = size;
        }
        @Override
        public void run() {
            try {
                int a =size/1000;
                double d=0.1;
                for(int i=0;i<a;i++){
                    d=d+0.01d;
                    if(d>0.98d){
                        break;
                    }
                    Thread.sleep(2000);
                    progressBarService.updatePercent(pageId, PROCESS_KEY, d, false);
                }
            }
            catch (InterruptedException e) {
                logger.error(e.getMessage(),e);
            }

        }

    }


    /**
     * 报送检查
     *
     * @return 检查结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    public List<ExcelValidDto> sendBeforeCheck(Map<String, Object> param) {
        return restTemplate.postForList(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/sendBeforeCheck", param,
                ExcelValidDto.class);
    }
    /**
     * 报送检查
     *
     * @return 检查结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    public List<ExcelValidDto> submitBeforeCheck(Map<String, Object> param) {
        return restTemplate.postForList(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/submitBeforeCheck", param,
                ExcelValidDto.class);
    }
    /**
     * 中债直连报送
     *
     * @param map
     *            传入需要报送的主键
     * @return 反馈信息
     * @see
     * @since 1.0
     */
    public void doSubmit(Map<String, Object> map) {
        restTemplate.postForObject(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/submitChb", map,
                Integer.class);
    }
    /**
     * 手工处理功能
     *
     * @return 提示信息
     * @param map 条件map
     * @seea
     * @since 1.0
     */
    @ResponseBody
    public AjaxResult doUpdate(@RequestParam Map<String, Object> map) {
//    	String message=
//    	restTemplate.postForObject(
// 	       		SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/doUpdate", map,
// 	       	String.class);
//    	return new AjaxResult(this.getMessage(message));
        if (map.get("flag") != null && "01".equals(map.get("flag"))) {
            // if(equityBusiInfoRegist001Service.checkIfInBatchByTaskBeanName(BatchBeanName.投资者明细信息登记手工处理.getBeanName())){
            // 处理全部数据
            ChbBatchPara record = new ChbBatchPara();
            record.setTaskId(BatchBeanName.投资者明细信息登记手工处理.getTaskId());
            record.setParaId(IDGenerator.generate());
            if (map.get("key") != null) {
                record.setParas(map.get("key").toString());
//                chbBatchParaMapper.insert(record);
                restTemplate.postForList(
                        SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/insert", record,
                        ChbBatchPara.class);
            }
            sysBatchStart001Service.executeBatch();
            return new AjaxResult(this.getMessage("REPORT_MANUAL_0009"));
            // }else{
            // return new AjaxResult(this.getMessage("REPORT_MANUAL_0008"));
            // }
        }
        else {
            if (equityBusiInfoRegist001Service.checkIfInBatchByTaskBeanName(
                    BatchBeanName.投资者明细信息登记手工处理.getTaskId())) {
//                equityBusiInfoRegist001Service.updateSumStaus(map);

                restTemplate.postForObject(
                        SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/updateSumStaus",map,
                        boolean.class);

            }
            else {
                return new AjaxResult(this.getMessage("REPORT_MANUAL_0008"));
            }
        }

        return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
    }
    @SuppressWarnings("unchecked")
    public Map<String, Object>selectDeletedAndUnsubmittedData(){
        return restTemplate.getForObject(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/selectDeletedAndUnsubmittedData",
                Map.class);
    }
    public boolean checkIfInBatchByTaskBeanName(String beanname) {
        return restTemplate.getForObject(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/checkIfInBatchByTaskBeanName",
                boolean.class);
    }
    public void updateSumStaus(Map<String, Object> map) {
        restTemplate.getForObject(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/updateSumStaus",
                boolean.class);
    }
    public void updateSumStausAll(Map<String, Object> map) {
        restTemplate.getForObject(
                SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/updateSumStausAll",
                boolean.class);
    }
    /**
     * 取消手工处理功能
     *
     * @return 提示信息
     * @param map 条件map
     * @seea
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(value = "/cancelMannul", method = RequestMethod.POST)
    public AjaxResult cancelMannul(@RequestBody Map<String, Object> map) {

        // add by liubin
        if (map.get("flag") != null && "01".equals(map.get("flag"))) {
            // 取消手工处理全部数据
            restTemplate.postForObject(
                    SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/cancelMannulAll",map,
                    boolean.class);
            return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
        }
        else {
            List<ValidMessage> errorList =
                    restTemplate.postForList(
                            SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/checkIsNotSubmit",map,
                            ValidMessage.class);
//            		equityBusiInfoRegist001Service.checkIsNotSubmit(map);
            if (CollectionUtils.isNotEmpty(errorList)) {
                AjaxResult webResult = new AjaxResult();
                // webResult.appendMessageList(errorList);
                webResult.setData(errorList.get(0).getMessage());
                return webResult;
            }
            restTemplate.postForObject(
                    SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/cancelMannul",map,
                    boolean.class);
        }

        return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
    }

    public void executeBatch(){
        sysBatchStart001Service.executeBatch();
    }
    /**
     * 导入数据文件之前检查数据
     *
     * @param fileName 导入的文件名
     * @return 处理结果
     */
    @ResponseBody
    @RequestMapping(value = "checkConfirmMsgForAllManual", method = RequestMethod.POST)
    public AjaxResult checkConfirmMsgForAllManual(String fileName) {
        AjaxResult ajaxResult = new AjaxResult();
        if (!equityBusiInfoRegist001Service.checkIfInBatchByTaskBeanName(
                BatchBeanName.投资者明细信息登记手工处理.getTaskId())) {
            ajaxResult.setMessage(this.getMessage("REPORT_MANUAL_0008"), MessageType.错误消息);
        }

        return ajaxResult;
    }

    /**
     * 导入数据文件之前检查数据
     *
     * @param fileName 导入的文件名
     * @return 处理结果
     */
    @ResponseBody
    @RequestMapping(value = "checkConfirmMsgForAllCheck", method = RequestMethod.POST)
    public AjaxResult checkConfirmMsgForAllCheck(String fileName) {
        AjaxResult ajaxResult = new AjaxResult();
        if (!equityBusiInfoRegist001Service.checkIfInBatchByTaskBeanName(
                BatchBeanName.投资者明细信息数据检查批处理.getTaskId())) {
            ajaxResult.setMessage(this.getMessage("REPORT_MANUAL_0014"), MessageType.错误消息);
        }

        return ajaxResult;
    }

    @ResponseBody
    @RequestMapping("doCheck")
    public AjaxResult doCheck(@RequestParam Map<String, Object> map) {

        // // 处理全部数据
        // ChbBatchPara record =new ChbBatchPara();
        // record.setTaskId(BatchBeanName.投资者明细信息数据检查批处理.getTaskId());
        // record.setParaId(IDGenerator.generate());
        // record.setParas(map.get("key").toString());
        // chbBatchParaMapper.insert(record);
        sysBatchStart001Service.executeBatch();
        return new AjaxResult(this.getMessage("REPORT_MANUAL_0009"));

    }

    /**
     * 导出中债excel之前检查数据量
     *
     * @param param 查询条件
     * @return 处理结果
     */
    @ResponseBody
    @RequestMapping(value = "countExportNum")
    public int countExportNum(@RequestParam Map<String, Object> param) {
        // AjaxResult ajaxResult = new AjaxResult();
        int dataNum = equityBusiInfoRegist001Service.countExportNum(param);
        return dataNum;

    }

    /**
     * 启动批处理导出
     * @param param
     */
    public AjaxResult batchExport(Map<String, Object> param) {
        AjaxResult ajaxResult = new AjaxResult();
        // 是否未执行或正在执行中
        if (!equityBusiInfoRegist001Service.checkIfInBatchByTaskBeanName(
                BatchBeanName.投资者明细信息登记导出.getTaskId())) {
            ajaxResult.setMessage("投资者明细信息登记导出批处理正在执行中或待执行！", MessageType.错误消息);
            return ajaxResult;
        }
        ChbSubinfoBatchLog log = new ChbSubinfoBatchLog();
        log.setBatchExeId(IDGenerator.generate());
        log.setTaskId(BatchBeanName.投资者明细信息登记导出.getTaskId());
        log.setBusiConfirmDate(param.get("busiConfirmDate")==null?"":param.get("busiConfirmDate").toString());
        log.setOperator(LoginUserUtil.getUserName());
        log.setFileType(DictCodeListExt3.报送文件类别_投资者明细信息登记.getValue());
        log.setDealStatus(DictCodeListExt3.报送处理状态_未处理.getValue());
        log.setGenCondition(param.get("key")==null?"":param.get("key").toString());
        log.setCreateUser(LoginUserUtil.getUserName());
        log.setCreateTime(new Date());
        log.setUpdateUser(LoginUserUtil.getUserName());
        log.setUpdateTime(new Date());
        // 日志存表
        restTemplate.postForObject(SvrServiceApplication.COMMON_SERVICE_NAME + "/ChbSubinfoBatchLog/insert", log,
                ChbSubinfoBatchLog.class);
        // 启动批处理
        sysBatchStart001Service.executeBatch();
        // 批处理启动成功，可在批处理监控页面查看进度
//		return new AjaxResult("导出excel文件批处理启动成功，可在批处理监控页面查看进度");
        ajaxResult.setData(true);
        return ajaxResult;
    }
    //add zhangjinyang 2020-12-21
    /**
     * 启动批处理导出
     * @param param
     */
    public AjaxResult batchExportAndDoManual(Map<String, Object> param) {
        AjaxResult ajaxResult = new AjaxResult();
        // 是否未执行或正在执行中
        if (!equityBusiInfoRegist001Service.checkIfInBatchByTaskBeanName(
                BatchBeanName.投资者明细信息登记导出.getTaskId())) {
            ajaxResult.setMessage("投资者明细信息登记导出批处理正在执行中或待执行！", MessageType.错误消息);
            return ajaxResult;
        }
         if (!equityBusiInfoRegist001Service.checkIfInBatchByTaskBeanName(
                BatchBeanName.投资者明细信息登记手工处理.getTaskId())) {
            ajaxResult.setMessage("投资者明细信息登记手工处理批处理正在执行中或待执行！", MessageType.错误消息);
            return ajaxResult;
        }
        ChbSubinfoBatchLog log = new ChbSubinfoBatchLog();
        log.setBatchExeId(IDGenerator.generate());
        log.setTaskId(BatchBeanName.投资者明细信息登记导出.getTaskId());
        log.setBusiConfirmDate(param.get("busiConfirmDate")==null?"":param.get("busiConfirmDate").toString());
        log.setOperator(LoginUserUtil.getUserName());
        log.setFileType(DictCodeListExt3.报送文件类别_投资者明细信息登记.getValue());
        log.setDealStatus(DictCodeListExt3.报送处理状态_未处理.getValue());
        log.setGenCondition(param.get("key")==null?"":param.get("key").toString());
        log.setCreateUser(LoginUserUtil.getUserName());
        log.setCreateTime(new Date());
        log.setUpdateUser(LoginUserUtil.getUserName());
        log.setUpdateTime(new Date());
        // 日志存表
        restTemplate.postForObject(SvrServiceApplication.COMMON_SERVICE_NAME + "/ChbSubinfoBatchLog/insert", log,
                ChbSubinfoBatchLog.class);
        //手工处理相关
        ChbBatchPara record = new ChbBatchPara();
        record.setTaskId(BatchBeanName.投资者明细信息登记手工处理.getTaskId());
        record.setParaId(IDGenerator.generate());
        if (param.get("key") != null) {
            record.setParas(param.get("key").toString());
//             chbBatchParaMapper.insert(record);
            restTemplate.postForObject(
                    SvrServiceApplication.CHB3_SERVICE_NAME + "/EquityBusiInfoRegist/insert", record,
                    ChbBatchPara.class);
        }

        // 启动批处理
        sysBatchStart001Service.executeBatch();
        // 批处理启动成功，可在批处理监控页面查看进度
//		return new AjaxResult("导出excel文件批处理启动成功，可在批处理监控页面查看进度");
        ajaxResult.setData(true);
        return ajaxResult;
    }
}

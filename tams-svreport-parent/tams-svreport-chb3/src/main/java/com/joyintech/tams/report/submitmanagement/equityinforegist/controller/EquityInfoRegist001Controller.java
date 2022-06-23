/*
 * 文件名：EquityInfoRegist.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：Administrator
 * 修改时间：2017年9月19日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */

package com.joyintech.tams.report.submitmanagement.equityinforegist.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.constant.BatchBeanName;
import com.joyintech.tams.report.common.dto.ExcelValidDto;
import com.joyintech.tams.report.common.util.ReportGlobalConstant;
import com.joyintech.tams.report.submitmanagement.equityinforegist.dto.EquityInfoRegist001Dto;
import com.joyintech.tams.report.submitmanagement.equityinforegist.service.EquityInfoRegist001Service;
import com.joyintech.tams.report.submitmanagement.equityinforegist.service.EquityInfoRegistMService;
import com.joyintech.tams.systemtools.util.CommonUtil;



/**
 * 投资者身份信息登记画面
 *
 * @author Administrator
 * @version 1.0 2017年9月19日
 * @see EquityInfoRegist
 * @since 1.0
 */
@Controller
@PageId("EquityInfoRegist001")
@RequestMapping("/svreport/submitmanagement/equityinforegist/EquityInfoRegist001")
public class EquityInfoRegist001Controller extends BaseController {
    /**
     * 001Service
     */
    @Autowired
    private EquityInfoRegist001Service equityInfoRegist001Service;
    @Autowired
    private EquityInfoRegistMService equityInfoRegistMService;

//    /**
//     * 报送前检查service//使用机构代码下拉框
//     */
//    @Autowired
//    private CheckReport001Service checkReport001Service;
    /**
     * 初始化
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        Map<String , Object>map=RequestUtil.getPageParam();
        if(StringUtil.isNotEmpty(map.get("submitStatus"))){
            model.addAttribute("submitStatus", map.get("submitStatus").toString().trim());
            if(StringUtil.isNotEmpty(map.get("status"))){
                model.addAttribute("status", map.get("status").toString().trim());
            }
            if(StringUtil.isNotEmpty(map.get("submitFeedbackStatus"))){
            	model.addAttribute("submitFeedbackStatus", map.get("submitFeedbackStatus").toString().trim());
            }
            model.addAttribute("searchDataStart", map.get("searchDataStart"));
            model.addAttribute("searchDataEnd", map.get("searchDataEnd"));
            model.addAttribute("isMain","01");
        }
        String myOrgCode=CommonUtil.getOrgCode();
        if(StringUtil.isNotEmpty(myOrgCode)){
            model.addAttribute("MY_AGENCY_CODE",myOrgCode);
        }
        super.setSelectOptionToModel(model, DictCode.报送数据状态,
            DictCode.个人证件类别,DictCode.机构证件类别,
            DictCode.报送状态,DictCode.报送反馈状态,
            DictCode.投资者类别);
        List<SelectOptionDto> listSendStatus = querySendStatus();
        model.addAttribute("indentifyMark",RequestUtil.getParam("indentifyMark"));
        // 增加发送状态
        model.addAttribute("SUBMIT_SEND_STATUS", JsonUtils.objectArray2Json(listSendStatus.toArray()));
        return super.init(model);
    }
    /**
     *
     * 查询功能
     * <功能详细描述>
     * @return 存储了查询结果的list
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(ReportGlobalConstant.SEARCH_PAGE)
    public Page<EquityInfoRegist001Dto> search(){
        Map<String, Object> param = RequestUtil.getPageParam();
        return equityInfoRegistMService.searchForPage(param);
    }

    /**
     *
     * 查询功能(无分页)
     * <功能详细描述>
     * @return 存储了查询结果的list
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/searchInfo")
    public List<EquityInfoRegist001Dto> searchInfo(@RequestParam Map<String,Object> param){
        return equityInfoRegistMService.queryForEquityInfoRegistPage(param);
    }
    /**
     *
     * 导出功能
     * <功能详细描述>
     * @return 结果
     * @see
     * @since 1.0
     */
    @RequestMapping(value = ACTION_EXPORT)
    public ResponseEntity<byte[]> export() {
//        List<EquityInfoRegist001Dto> list = this.equityInfoRegist001Service
//            .queryExportRegistList(RequestUtil.getPageParam());
//        String flag = (String)RequestUtil.getPageParam().get("flag");
//        File outFile = null;
//        if(StringUtil.equals("0", flag)){
//            outFile = equityInfoRegist001Service.makeFile(
//                "/com/joyintech/tams/report/submitmanagement/equityinforegist/conf/export_conf.xml", list);
//        }
//        else{
//            String blank = " ";
//            for (EquityInfoRegist001Dto equityInfoRegist001Dto : list) {
//                if (StringUtil.contains(equityInfoRegist001Dto.getSubmitFeedbackStatus(), blank)) {
//                    equityInfoRegist001Dto.setSubmitFeedbackStatus(StringUtil.split(equityInfoRegist001Dto.getSubmitFeedbackStatus(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getSubmitStatusName(), blank)) {
//                    equityInfoRegist001Dto.setSubmitStatus(StringUtil.split(equityInfoRegist001Dto.getSubmitStatusName(), " ")[1]);
//                }
//                else {
//                    equityInfoRegist001Dto.setSubmitStatus(equityInfoRegist001Dto.getSubmitStatusName());
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getType(), blank)) {
//                    equityInfoRegist001Dto.setType(StringUtil.split(equityInfoRegist001Dto.getType(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getStatusName(), blank)) {
//                    equityInfoRegist001Dto.setStatus(StringUtil.split(equityInfoRegist001Dto.getStatusName(), " ")[1]);
//                }
//                else {
//                    equityInfoRegist001Dto.setStatus(equityInfoRegist001Dto.getStatusName());
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getIsBelongBank(), blank)) {
//                    equityInfoRegist001Dto.setIsBelongBank(StringUtil.split(equityInfoRegist001Dto.getIsBelongBank(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getHolderMark(), blank)) {
//                    equityInfoRegist001Dto.setHolderMark(StringUtil.split(equityInfoRegist001Dto.getHolderMark(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getIsBelongBank(), blank)) {
//                    equityInfoRegist001Dto.setIsBelongBank(StringUtil.split(equityInfoRegist001Dto.getIsBelongBank(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getHolderType(), blank)) {
//                    equityInfoRegist001Dto.setHolderType(StringUtil.split(equityInfoRegist001Dto.getHolderType(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getHolderSex(), blank)) {
//                    equityInfoRegist001Dto.setHolderSex(StringUtil.split(equityInfoRegist001Dto.getHolderSex(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getHolderRiskPrefer(), blank)) {
//                    equityInfoRegist001Dto.setHolderRiskPrefer(StringUtil.split(equityInfoRegist001Dto.getHolderRiskPrefer(), " ")[1]);
//                }
//            }
//            outFile = equityInfoRegist001Service.makeFile(
//                "/com/joyintech/tams/report/submitmanagement/equityinforegist/conf/export_conf_all.xml", list);
//        }
//        return ExportUtil.export(
//            this.getMessage(this.getPageId()) + GlobalConstant.OPEN_PARENTHESIS
//                                 + DateUtil.format(new Date()) + GlobalConstant.CLOSE_PARENTHESIS + GlobalConstant.POINT
//                                 + FileNameUtil.getFileExtName(outFile),
//            outFile, true);

        Map<String, Object> pageParam = RequestUtil.getPageParam();
        if(StringUtil.isNotEmpty((String) pageParam.get("keyList"))){
            pageParam.put("export_method_flg", "exportSelect");
        }else{
            pageParam.put("export_method_flg", "export");
        }

        return equityInfoRegist001Service.export(this.getPageId(), pageParam);
    }
    /**
     *
     * 导入
     * <功能详细描述>
     * @param fileName 文件
     * @return 导入数据
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/importFile")
    public List<ExcelValidDto> importData(String fileName){
        return this.equityInfoRegistMService.importData(fileName);
    }
    /**
     *手工处理功能
     * @return 提示信息
     * @param map 条件map
     * @seea
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(value = ReportGlobalConstant.PAGE_UPDATE ,method = RequestMethod.POST)
    public AjaxResult doUpdate(@RequestParam Map<String,Object> map){
    	return equityInfoRegistMService.doUpdate(map);
        //return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
    }

    /**
     *取消手工处理功能
     * @return 提示信息
     * @param map 条件map
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(value = "/cancelMannul" ,method = RequestMethod.POST)
    public AjaxResult cancelMannul(@RequestBody Map<String,Object> map) {
    	//add by liubin 
    	if(map.get("flag") != null && "01".equals(map.get("flag"))){
    		equityInfoRegistMService.cancelMannulAll(map);
    	}else{
    		 String upKey = (String)map.get("updateKeys");//删除的主键
    	        List<ValidMessage> errorList = equityInfoRegistMService.checkIsNotSubmit(upKey);
    	        if(CollectionUtils.isNotEmpty(errorList)){
    	        	AjaxResult webResult= new AjaxResult();
    	            webResult.appendMessageList(errorList);
                    // 防止提示信息后卡死 add by weiyuqiang 2019-05-21 start
                    webResult.setStatuCode(HttpStatus.OK.value());
                    // 防止提示信息后卡死 add by weiyuqiang 2019-05-21 end
    	            return webResult;
    	        }
    	        equityInfoRegistMService.cancelMannul(upKey);
    	}
        return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
    }
    /**
     *
     * 检查
     * <功能详细描述>
     * @param dto 参数
     * @param result 结果
     * @return 检查结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(value="checkEquityRegist")
    public AjaxResult check(@Valid @RequestBody EquityInfoRegist001Dto dto,BindingResult result){
        if(result.hasErrors()) {
            return new AjaxResult(result);
        }
        return null;
    }

    /**
     * 报送检查
     * @param map 查询条件
     * @return 检查结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("reportCheck")
    public List<ExcelValidDto> reportCheck() {
        return equityInfoRegistMService.reportCheck(RequestUtil.getPageParam());
    }
    /**
     * 报送检查
     * @return 检查结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(value = "/sendBeforeCheck",method = RequestMethod.POST )
    public List<ExcelValidDto> sendBeforeCheck() {
    	return equityInfoRegistMService.sendBeforeCheck(RequestUtil.getPageParam());
    }
    /**
     * 中债直连报送
     * @param map 传入需要报送的主键
     * @return 反馈信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(value = "/submitChb" ,method = RequestMethod.POST)
    public AjaxResult doSubmit(@RequestBody Map<String,Object> map){
    	equityInfoRegistMService.doSubmit(map);
        return new AjaxResult(this.getMessage("REPORT_COMMON_I_0018"));
    }
    /**
     * 将选择的数据导出成excel
     * @return 返回需要的格式
     * @see
     * @since 1.0
     */
    @RequestMapping(value = "/exportSelect")
    public ResponseEntity<byte[]> exportSelect(){
        Map<String,Object> map = RequestUtil.getPageParam();
        String exportKeys =(String) map.get("expKeys");
        List<EquityInfoRegist001Dto> list = this.equityInfoRegistMService.queryExportSelectRegistList(exportKeys);
        String flag = (String)RequestUtil.getPageParam().get("flag");
        File outFile = null;
        for (EquityInfoRegist001Dto equityInfoRegist001Dto : list) {
			if(!StringUtil.equals(DictCodeListExt2.数据类型_中债_识别标识变更.getValue(), equityInfoRegist001Dto.getType())){
				equityInfoRegist001Dto.setOrgIndentifyMark("");
			}
		}
        if(StringUtil.equals("0", flag)){
            outFile = equityInfoRegist001Service.makeFile(
                "/com/joyintech/tams/report/submitmanagement/equityinforegist/conf/export_conf.xml", list);
        }
        else{
//            String blank = " ";
//            for (EquityInfoRegist001Dto equityInfoRegist001Dto : list) {
//                if (StringUtil.contains(equityInfoRegist001Dto.getSubmitFeedbackStatus(), blank)) {
//                    equityInfoRegist001Dto.setSubmitFeedbackStatus(StringUtil.split(equityInfoRegist001Dto.getSubmitFeedbackStatus(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getSubmitStatusName(), blank)) {
//                    equityInfoRegist001Dto.setSubmitStatus(StringUtil.split(equityInfoRegist001Dto.getSubmitStatusName(), " ")[1]);
//                }
//                else {
//                    equityInfoRegist001Dto.setSubmitStatus(equityInfoRegist001Dto.getSubmitStatusName());
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getStatusName(), blank)) {
//                    equityInfoRegist001Dto.setStatus(StringUtil.split(equityInfoRegist001Dto.getStatusName(), " ")[1]);
//                }
//                else {
//                    equityInfoRegist001Dto.setStatus(equityInfoRegist001Dto.getStatusName());
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getType(), blank)) {
//                    equityInfoRegist001Dto.setType(StringUtil.split(equityInfoRegist001Dto.getType(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getIsBelongBank(), blank)) {
//                    equityInfoRegist001Dto.setIsBelongBank(StringUtil.split(equityInfoRegist001Dto.getIsBelongBank(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getHolderMark(), blank)) {
//                    equityInfoRegist001Dto.setHolderMark(StringUtil.split(equityInfoRegist001Dto.getHolderMark(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getIsBelongBank(), blank)) {
//                    equityInfoRegist001Dto.setIsBelongBank(StringUtil.split(equityInfoRegist001Dto.getIsBelongBank(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getHolderType(), blank)) {
//                    equityInfoRegist001Dto.setHolderType(StringUtil.split(equityInfoRegist001Dto.getHolderType(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getHolderSex(), blank)) {
//                    equityInfoRegist001Dto.setHolderSex(StringUtil.split(equityInfoRegist001Dto.getHolderSex(), " ")[1]);
//                }
//                if (StringUtil.contains(equityInfoRegist001Dto.getHolderRiskPrefer(), blank)) {
//                    equityInfoRegist001Dto.setHolderRiskPrefer(StringUtil.split(equityInfoRegist001Dto.getHolderRiskPrefer(), " ")[1]);
//               }
//            }
            outFile = equityInfoRegist001Service.makeFile(
                "/com/joyintech/tams/report/submitmanagement/equityinforegist/conf/export_conf_all.xml", list);
        }
        return ExportUtil.export(
            this.getMessage(this.getPageId()) + GlobalConstant.OPEN_PARENTHESIS
                                 + DateUtil.format(new Date()) + GlobalConstant.CLOSE_PARENTHESIS + GlobalConstant.POINT
                                 + FileNameUtil.getFileExtName(outFile),
            outFile, true);

        /*Map<String, Object> pageParam = RequestUtil.getPageParam();
        pageParam.put("export_method_flg", "exportSelect");

        return equityInfoRegist001Service.export(this.getPageId(), pageParam);*/
    }

    /**
     * 导出删除模板
     * @return 返回需要的格式
     * @see
     * @since 1.0
     */
    @RequestMapping(value = "/exportDel")
    public ResponseEntity<byte[]> exportDel() {
    	 Map<String,Object> map = RequestUtil.getPageParam();
         String exportKeys =(String) map.get("expKeys");
         List<EquityInfoRegist001Dto> list = this.equityInfoRegistMService.queryExportSelectRegistList(exportKeys);
         String flag = (String)RequestUtil.getPageParam().get("flag");
         File outFile = equityInfoRegist001Service.makeFile(
                 "/com/joyintech/tams/report/submitmanagement/equityinforegist/conf/export_conf_del.xml", list);
         return ExportUtil.export(
                 this.getMessage(this.getPageId()) + GlobalConstant.OPEN_PARENTHESIS
                                      + DateUtil.format(new Date()) + GlobalConstant.CLOSE_PARENTHESIS + GlobalConstant.POINT
                                      + FileNameUtil.getFileExtName(outFile),
                 outFile, true);
      /*Map<String, Object> pageParam = RequestUtil.getPageParam();
      pageParam.put("export_method_flg", "exportDel");

      return equityInfoRegist001Service.export(this.getPageId(), pageParam);*/
  }

    /**
     * 中债直连报送 二次报送
     * @param paramMap 传入需要报送的主键
     * @return 反馈信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(value = "/submitCbAgain" ,method = RequestMethod.POST)
    public AjaxResult submitCbAgain(@RequestBody Map<String,Object> paramMap){
    	equityInfoRegistMService.submitCbAgain(paramMap);
        return new AjaxResult(this.getMessage("REPORT_COMMON_I_0018"));
    }
    /**
     * @Title: querySendStatus
     * @Description: (报送登记发送状态)
     * @param @return    参数
     * @return List<SelectOptionDto>    返回类型
     * @throws
     */
     private List<SelectOptionDto> querySendStatus() {
     	List<SelectOptionDto> listSendStatus = new ArrayList<SelectOptionDto>();
     	SelectOptionDto selectOptionDto1 = new SelectOptionDto();
         selectOptionDto1.setId("01");
         selectOptionDto1.setText("发送成功");
         selectOptionDto1.setCol("");
         listSendStatus.add(selectOptionDto1);
         SelectOptionDto selectOptionDto2 = new SelectOptionDto();
         selectOptionDto2.setId("02");
         selectOptionDto2.setText("发送失败");
         selectOptionDto2.setCol("");
         listSendStatus.add(selectOptionDto2);
     	return listSendStatus;
     }
     
     /**
      * 检查是否有未执行或正在执行中的批处理任务 
      *
      * @param fileName 导入的文件名
      * @return 处理结果
      */
     @ResponseBody
     @RequestMapping(value = "checkConfirmMsgForAllManual")
     public AjaxResult checkConfirmMsgForAllManual() {
         AjaxResult ajaxResult = new AjaxResult();
         if (!equityInfoRegistMService.checkIfInBatchByTaskBeanName(
             BatchBeanName.投资者身份信息登记手工处理.getTaskId())) {
             ajaxResult.setMessage(this.getMessage("REPORT_MANUAL_0008"), MessageType.错误消息);
         }
         return ajaxResult;
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
         int dataNum = equityInfoRegist001Service.countExportNum(param);
         return dataNum;

     }
     /**
      * 导出检查
      * @param param
      * @return
      */
     @ResponseBody
     @RequestMapping(value = "checkExport")
 	public AjaxResult checkExport(@RequestBody Map<String, Object> param) {
 		AjaxResult ajaxResult = new AjaxResult();
 		boolean batchExport = false;
 		// 未勾选
 		if (!RequestUtil.getPageParam().containsKey("keyList")) {
 			// 中债报送批处理导出数据量。大于等于此参数则需要批处理导出。
 			String batchExportNum = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_SUBINFO_BATCH_EXPORT_NUM);
 			if (!StringUtil.isNotEmpty(batchExportNum)) {
 				batchExportNum = "1000000"; // 默认为100w
 			}
 			int dataNum = equityInfoRegist001Service.countExportNum(param);
 			if (dataNum >= Integer.parseInt(batchExportNum)) {
 				batchExport = true;
 			}
 		}
 		ajaxResult.setData(batchExport);
 		return ajaxResult;
 	}
     /**
      * 启动批处理导出
      * @param param
      * @return
      */
     @ResponseBody
     @RequestMapping(value = "batchExport")
 	public AjaxResult batchExport(@RequestParam Map<String, Object> param) {
     	return equityInfoRegist001Service.batchExport(param);
 	}
     
     /**
      * 启动批处理导出和手工处理
      * @param param
      * @return
      */
     @ResponseBody
     @RequestMapping(value = "batchExportAndDoManual")
 	public AjaxResult batchExportAndDoManual(@RequestParam Map<String, Object> param) {
     	return equityInfoRegist001Service.batchExportAndDoManual(param);
 	}
}
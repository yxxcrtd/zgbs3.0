/*
 * 文件名：InvestorsHoldInfoRegist001Controller.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：zuoyinyi
 * 修改时间：2018年5月8日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.tams.report.submitmanagement.investorsholdinforegist.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.joyintech.framework.select.SuggestDto;
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
import com.joyintech.tams.report.common.constant.BatchBeanName;
import com.joyintech.tams.report.common.dto.ExcelValidDto;
import com.joyintech.tams.report.submitmanagement.investorsholdinforegist.dto.InvestorsHoldInfoRegist001Dto;
import com.joyintech.tams.report.submitmanagement.investorsholdinforegist.service.InvestorsHoldInfoRegist001Service;
import com.joyintech.tams.report.submitmanagement.investorsholdinforegist.service.InvestorsHoldInfoRegistMService;

/**
 * 投资者持有信息登记
 *
 * @author zuoyinyi
 * @version 1.0 2018年5月8日
 * @see InvestorsHoldInfoRegist001Controller
 * @since 1.0
 */
@Controller
@PageId("InvestorsHoldInfoRegist001")
@RequestMapping("/svreport/submitmanagement/investorsholdinforegist/InvestorsHoldInfoRegist001")
public class InvestorsHoldInfoRegist001Controller extends BaseController {

	/**
	 * 投资者持有数量信息登记
	 *
	 */
	@Autowired
	private InvestorsHoldInfoRegistMService investorsHoldInfoRegistMService;
	@Autowired
	private InvestorsHoldInfoRegist001Service investorsHoldInfoRegist001Service;
	


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String init(Model model) {
		Map<String, Object> map = RequestUtil.getPageParam();
		// add by zcw 中债三期优化 start
		model.addAttribute("holdDateStr",DateUtil.format(DateUtil.addDay(DateUtil.platDateFromCache(), -1)));
		// add by zcw 中债三期优化 end
		if (StringUtil.isNotEmpty(map.get("submitStatus"))) {
			model.addAttribute("submitStatus", map.get("submitStatus").toString().trim());
			if (StringUtil.isNotEmpty(map.get("status"))) {
				model.addAttribute("status", map.get("status").toString().trim());
			}
			if(StringUtil.isNotEmpty(map.get("submitFeedbackStatus"))){
            	model.addAttribute("submitFeedbackStatus", map.get("submitFeedbackStatus").toString().trim());
            }
			model.addAttribute("searchDataStart", map.get("searchDataStart"));
			model.addAttribute("searchDataEnd", map.get("searchDataEnd"));
			model.addAttribute("isMain", "01");
		}
		super.setSelectOptionToModel(model, DictCode.报送状态, DictCode.报送数据状态, DictCode.报送反馈状态);
		List<SelectOptionDto> listSendStatus = querySendStatus();
        // 增加发送状态
        model.addAttribute("SUBMIT_SEND_STATUS", JsonUtils.objectArray2Json(listSendStatus.toArray())); 
		if (StringUtil.isNotEmpty(RequestUtil.getParam("validKey"))) {
			// 报送前检查链接数据主键解析
			String[] validKeyArr = StringUtil.split(RequestUtil.getParam("validKey"), GlobalConstant.COMMA);
			InvestorsHoldInfoRegist001Dto dto = new InvestorsHoldInfoRegist001Dto();
			dto.setPrdRegistCode(StringUtil.split(validKeyArr[0], " ")[1]);
			dto.setIndentifyMark(StringUtil.split(validKeyArr[1], " ")[1]);
			dto.setHoldDate(DateUtil.parse(StringUtil.split(validKeyArr[2], " ")[1]));
			dto.setCcy(StringUtil.split(validKeyArr[3], " ")[1]);
			dto.setRegistBankCode(StringUtil.split(validKeyArr[4], " ")[1]);
			model.addAttribute("validKey", dto);
		}
		return super.init(model);
	}

	/**
	 *
	 * 机构代码下拉框取得值
	 *
	 * @return 下拉框值
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("/getInstitutionCode")
	public List<SelectOptionDto> getInstitutionCode() {
		String param = super.getRequest().getParameter("param");
		List<SuggestDto> list = investorsHoldInfoRegistMService.getInstitutionCode(param);
		List<SelectOptionDto> resultList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (SuggestDto suggestDto : list) {
				SelectOptionDto resultDto = new SelectOptionDto();
				resultDto.setId(suggestDto.getCode());
				resultDto.setText(suggestDto.getShortName());
				resultList.add(resultDto);
			}
		}
		return resultList;
	}

	/**
	 *
	 * 业务画面查询事件
	 *
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("findPage")
	public Page<InvestorsHoldInfoRegist001Dto> selectAllData() {
		Page<InvestorsHoldInfoRegist001Dto> page = investorsHoldInfoRegistMService
				.selectForPage(RequestUtil.getPageParam());
		return page;
	}

	/**
	 *
	 * 查询事件
	 *
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("searchInfo")
	public List<InvestorsHoldInfoRegist001Dto> searchInfo(@RequestParam Map<String,Object> param) {
		return investorsHoldInfoRegistMService.queryForHoldInfoRegist(param);
	}
	/**
	 *
	 * 导入
	 *
	 * @param fileName
	 *            文件
	 * @return 导入数据
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("/importFile")
	public List<ExcelValidDto> importData(String fileName) {
		return this.investorsHoldInfoRegistMService.importData(fileName);
	}

	/**
	 *
	 * 业务画面导出事件
	 *
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("export")
	public ResponseEntity<byte[]> export() {
		if (RequestUtil.getPageParam().containsKey("keyList")) {
			String flag = (String) RequestUtil.getPageParam().get("flag");
			List<InvestorsHoldInfoRegist001Dto> dtoList = investorsHoldInfoRegistMService
					.selectAllList(RequestUtil.getParameters(getRequest()));
			File outFile = null;
			if (StringUtil.equals("0", flag)) {
				outFile = investorsHoldInfoRegist001Service.makeFile(
						"/com/joyintech/tams/report/submitmanagement/investorsholdinforegist/conf/export_conf.xml", dtoList);
			} else if(StringUtil.equals("1", flag)){
				outFile = investorsHoldInfoRegist001Service.makeFile(
						"/com/joyintech/tams/report/submitmanagement/investorsholdinforegist/conf/export_conf_all.xml",
						dtoList);
			}
			else {
				outFile = investorsHoldInfoRegist001Service.makeFile(
						"/com/joyintech/tams/report/submitmanagement/investorsholdinforegist/conf/export_conf_del.xml",
						dtoList);
			}
			return ExportUtil.export(this.getMessage(this.getPageId()) + GlobalConstant.OPEN_PARENTHESIS
					+ DateUtil.format(new Date()) + GlobalConstant.CLOSE_PARENTHESIS + GlobalConstant.POINT
					+ FileNameUtil.getFileExtName(outFile), outFile, true);
		}

		else {
			return investorsHoldInfoRegist001Service.export(this.getPageId(), RequestUtil.getPageParam());
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
	@RequestMapping("sendBeforeCheck")
	public List<ExcelValidDto> sendBeforeCheck() {
		Map<String, Object> param = RequestUtil.getPageParam();
		return investorsHoldInfoRegistMService.sendBeforeCheck(param);
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
	@ResponseBody
	@RequestMapping(value = "/submitChb", method = RequestMethod.POST)
	public AjaxResult doSubmit(@RequestBody Map<String, Object> map) {
		investorsHoldInfoRegistMService.doSubmit(map);
		return new AjaxResult(this.getMessage("REPORT_COMMON_I_0018"));
	}

	/**
	 * 手工处理功能
	 *
	 * @return 提示信息
	 * @param map
	 *            条件map
	 * @seea
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("doManual")
	public AjaxResult doUpdate(@RequestParam Map<String, Object> map) {
		return investorsHoldInfoRegistMService.doUpdate(map);
		//return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
	}

	/**
	 * 取消手工处理功能
	 *
	 * @return 提示信息
	 * @param map
	 *            条件map
	 * @seea
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping(value = "/cancelMannul", method = RequestMethod.POST)
	public AjaxResult cancelMannul(@RequestBody Map<String, Object> map) {
		
		
		//add by liubin 
		if(map.get("flag") != null && "01".equals(map.get("flag"))){
			//取消手工处理全部数据
			investorsHoldInfoRegistMService.cancelMannulAll(map);
		}else{
			List<ValidMessage> errorList = investorsHoldInfoRegistMService.checkIsNotSubmit(map);
			if (CollectionUtils.isNotEmpty(errorList)) {
				AjaxResult webResult = new AjaxResult();
				webResult.appendMessageList(errorList);
                // 防止提示信息后卡死 add by weiyuqiang 2019-05-21 start
                webResult.setStatuCode(HttpStatus.OK.value());
                // 防止提示信息后卡死 add by weiyuqiang 2019-05-21 end
				return webResult;
			}
			investorsHoldInfoRegistMService.cancelMannul(map);
		}
		
		return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
	}
	/**
	 * 重新报送
	 *
	 * @param map
	 *            传入需要报送的主键
	 * @return 反馈信息
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping(value = "/submitCbAgain", method = RequestMethod.POST)
	public AjaxResult doSubmitAgain(@RequestBody Map<String, Object> map) {
		investorsHoldInfoRegistMService.doSubmitAgain(map);
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
	        if (!investorsHoldInfoRegistMService.checkIfInBatchByTaskBeanName(
	            BatchBeanName.投资者持有信息登记手工处理.getTaskId())) {
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
	         int dataNum = investorsHoldInfoRegist001Service.countExportNum(param);
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
	 			int dataNum = investorsHoldInfoRegist001Service.countExportNum(param);
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
	     	return investorsHoldInfoRegist001Service.batchExport(param);
	 	}
	     
	     /**
	      * 启动批处理导出及手工处理
	      * @param param
	      * @return
	      */
	     @ResponseBody
	     @RequestMapping(value = "batchExportAnddoManual")
	 	public AjaxResult batchExportAnddoManual(@RequestParam Map<String, Object> param) {
	     	return investorsHoldInfoRegist001Service.batchExportAnddoManual(param);
	 	}

}
/*
 * 文件名：ChbAstValueRegist001Controller.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：zhangzhongliang
 * 修改时间：2019年1月24日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */
 
package com.joyintech.tams.svreport.chb.astvalueregist.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.component.svrreport.chb.contant.SendDataCheckType;
import com.joyintech.component.svrreport.chb.dto.ChbAstValueSubDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.svreport.chbsd.assetvalsub.service.SdAssetValRegBatchService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.institution.service.InstitutionService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.svreport.chb.astvalueregist.dto.ChbAstValueRegist001Dto;
import com.joyintech.tams.svreport.chb.astvalueregist.service.ChbAstValueRegist001Service;
import com.joyintech.tams.svreport.chb.astvalueregist.service.ChbAstValueRegistMService;
import com.joyintech.tams.systemtools.util.CommonUtil;


/**
 * 资产估值信息登记界面Controller
 * 〈功能详细描述〉
 * @author 张忠良
 * @version 1.0 2019年02月24日
 * @see ChbAstValueRegist001Controller
 * @since 1.0
 */
@Controller
@PageId("SvrChbAstValueRegist001")
@RequestMapping("/chb/astvalueregist/ChbAstValueRegist001")
public class ChbAstValueRegist001Controller extends BaseController{
    /**
     * service
     */
    @Autowired
    private ChbAstValueRegist001Service chbAstValueRegist001Service;
    @Autowired
    private ChbAstValueRegistMService chbAstValueRegistMService;
    
	/**
	 * 机构管理service
	 */
	@Autowired
	private InstitutionService institutionService;

	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SdAssetValRegBatchService sdAssetValRegBatchService;
	
    /**
	 * {@inheritDoc}
	 */
	@Override
	public String init(Model model) {
		Map<String, Object> map = RequestUtil.getPageParam();
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
		model.addAttribute("SVR_REPORT_CODE", DictCodeListExt2.报表名称_资产估值信息登记.getValue());
		model.addAttribute("FILE_HANDLE_STATUS", DictCodeListExt3.中债一二三期文件处理状态_未处理.getValue());
		super.setSelectOptionToModel(model, DictCode.报送状态, DictCode.报送数据状态, DictCode.报送反馈状态);

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
		List<SuggestDto> list = chbAstValueRegistMService.getInstitutionCode(param);
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
	public Page<ChbAstValueRegist001Dto> searchPage() {
		Page<ChbAstValueRegist001Dto> page = chbAstValueRegistMService
				.searchPage(RequestUtil.getPageParam());
		return page;
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
		//update by leipeng 20200711
		Map<String,Object> pageParam = RequestUtil.getPageParam();
		List<String>  orgCode = CommonUtil.getTreeInstCode();
 		pageParam.put("orgCode", orgCode);
		return chbAstValueRegist001Service.export(this.getPageId(), pageParam);
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
	public AjaxResult doUpdate(@RequestBody Map<String, Object> map) {
		chbAstValueRegistMService.updateSumStaus(map);
		return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
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
		String result = chbAstValueRegist001Service.cancelMannul(map);
		if("fail".equals(result)){
			AjaxResult ajaxresult =  new AjaxResult("有未报送的数据",MessageType.错误消息);
			ajaxresult.setStatuCode(HttpStatus.OK.value());
			return ajaxresult;
		}
		return new AjaxResult(this.getMessage("REPORT_COMMON_I_0008"));
	}

	/**start  中债一二期直连代码  add  by  wangzehui  20201012*/

	/***
	 * 报送按钮的检查
	 * @return
	 */
	@ResponseBody
	@RequestMapping("submitBeforeCheck")
	public Map<String,Object> submitBeforeCheck() {
		Map<String, Object> param = RequestUtil.getPageParam();
		return  chbAstValueRegistMService.submitBeforeCheck(param);
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
	public AjaxResult doSubmit(@RequestBody Map<String, Object> param) {

		List<ChbAstValueRegist001Dto> list=new ArrayList<>();
		// 选择行数据时
		if (param.containsKey("keyList")) {
			String keyList = param.get("keyList").toString();
			try {
				list = JsonUtils.json2List(keyList,
						ChbAstValueRegist001Dto.class);
			}
			catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}else{
			Page<ChbAstValueRegist001Dto> page = chbAstValueRegistMService
					.searchPage(RequestUtil.getPageParam());
			if(page!=null&&page.getRoot()!=null&&page.getRoot().size()>0){
				list = page.getRoot();
			}
			//查询数据
		}

		//报文拼接：2、进行数据结构转化，把[1]中取得的List，转化为报文拼接组件中调用方法的数据结构类
		List<ChbAstValueSubDto> dataList = new ArrayList<ChbAstValueSubDto>();
		for(ChbAstValueRegist001Dto sdDto:list){
			ChbAstValueSubDto target = new ChbAstValueSubDto();
			BeanUtil.copyPropertiesIgnoreNull(target, sdDto);
			dataList.add(target);
		}

		SendDataCheckType sendDataCheckType = SendDataCheckType.报送;
		if (StringUtil.equals(param.get("opertionType"), "reSubmitChb")){
			sendDataCheckType = SendDataCheckType.重新报送;
		}
		sdAssetValRegBatchService.sendBatchCommon(dataList, sendDataCheckType);

		return new AjaxResult(this.getMessage("REPORT_COMMON_I_0018"));
	}

	/**end  中债一二期直连代码  add  by  wangzehui  2020100910*/

}

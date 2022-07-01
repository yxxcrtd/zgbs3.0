/*
 * 文件名：TrdCommandDispatcher003Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2018年9月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.investtrade.commanddispatcher.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.tams.invest.trade.investtrade.commanddispatcher.dto.TrdCommandDispatcher001Dto;
import com.joyintech.tams.invest.trade.investtrade.commanddispatcher.service.TrdCommandDispatcher001Service;

/**
 * 
 * 指令分发页面-自动分发
 * 〈功能详细描述〉
 * @author xkni
 * @version 1.0 2020年1月09日
 * @see TrdCommandDispatcher003Controller
 * @since 1.0
 */
@Controller
@PageId("TrdCommandDispatcher003")
@RequestMapping("/invest/trade/investtrade/commanddispatcher/TrdCommandDispatcher003")
public class TrdCommandDispatcher003Controller extends BaseController{
	/**
     * 指令分发service注入
     */
	@Autowired
	private TrdCommandDispatcher001Service trdCommandDispatcher001Service;
	
	@Override
	@RequestMapping(PAGE_INIT)
	public String init(Model model) {
		String entrustIds=getParam("entrustIds");
		String finprodType2s=getParam("finprodType2s");
		model.addAttribute("finprodType2s", finprodType2s);
		model.addAttribute("entrustIds", entrustIds);
		List<TrdCommandDispatcher001Dto> dto = trdCommandDispatcher001Service.selectEtrAutoRuleAndDetail();
        if(CollectionUtils.isNotEmpty(dto)){

            model.addAttribute("autodispensetime", dto.get(0).getVdateStr()+" - "+dto.get(0).getMdateStr());
           if(StringUtil.isNotBlank(dto.get(0).getDistributeRule())){
			   model.addAttribute("distributeRule",dto.get(0).getDistributeRule());
		   }
           if(StringUtil.isNotBlank(dto.get(0).getDistributeParameter())){
				model.addAttribute("distributeParameter",dto.get(0).getDistributeParameter());
		   }
        }
        else{

            model.addAttribute("autodispensetime", "");
        }
		
		return super.init(model);
	}

	/**
	 * 自动分发时的保存check
	 * <功能详细描述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
    @RequestMapping("addAutoTimeCheck")
	public AjaxResult addAutoTimeCheck(@RequestBody TrdCommandDispatcher001Dto dto){
        AjaxResult result=new AjaxResult();
        result.setErrorList(trdCommandDispatcher001Service.addAutoTimeCheck(dto.getVdate()));
        
        result.setData("自动分发设置成功");
        return result;
	}
	/**
	 * 自动分发的保存
	 * <功能详细描述>
	 * @param dto
	 * @throws Exception
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("addAutoTime")
	public AjaxResult addAutoTime(@RequestBody TrdCommandDispatcher001Dto dto) {
//		trdCommandDispatcher001Service.addAutoTime(dto);
	    //自动分发保存的check
	    AjaxResult result=new AjaxResult();
//	    result.setErrorList(trdCommandDispatcher001Service.addAutoTimeCheck(dto.getVdate()));
	    /*if(result.getErrorList().size() == 0){
	        List<TrdCommandDispatcher001Dto> list=trdCommandDispatcher001Service.selectEtrAutoTime();
	        Date currentTime = DateUtil.parse((String)SecurityUtil.getFromSession(GlobalConstant.SESSION_SYS_DATE));
	        for(TrdCommandDispatcher001Dto autoTime:list){
	            if(autoTime.getVdate().compareTo(currentTime) <=0 && autoTime.getMdate().compareTo(currentTime) >=0){
	                trdCommandDispatcher001Service.addAutoEtrDistribute(dto);
	                break;
	            }
	        } 
	    }*/
	    trdCommandDispatcher001Service.deleteEtrAutoTime();
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("vdate", dto.getVdateStr());
	    map.put("mdate", dto.getMdateStr());
	    map.put("distributeRule",dto.getDistributeRule());
	    map.put("distributeParameter",dto.getDistributeParameter());
	    if(dto.getVdateStr().equals("1900-01-01") && dto.getMdateStr().equals("2099-12-31")){
	    
	    }
	    else{
            // modify by dubaojun 2020-06-29 上海农商行项目设置异常时需要报出错误消息 start
	        int count = trdCommandDispatcher001Service.insertEtrAutoTime(map);
	        if(count == 0) {
	            result.appendMessage(new ValidMessage("自动分发设置失败，请联系管理员！"));
	        }
            // modify by dubaojun 2020-06-29 上海农商行项目设置异常时需要报出错误消息 end
	    }
	    return result;
	}
	
	 
}

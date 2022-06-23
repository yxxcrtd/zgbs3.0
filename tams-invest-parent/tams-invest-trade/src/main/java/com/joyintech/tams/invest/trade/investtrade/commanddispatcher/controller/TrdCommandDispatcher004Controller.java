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

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.trade.investtrade.commanddispatcher.dto.TrdCommandDispatcher001Dto;
import com.joyintech.tams.invest.trade.investtrade.commanddispatcher.dto.TrdCommandDispatcher004Dto;
import com.joyintech.tams.invest.trade.investtrade.commanddispatcher.service.TrdCommandDispatcher001Service;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 指令分发页面-自动分发
 * 〈功能详细描述〉
 * @author wangkang
 * @version 1.0 2018年9月19日
 * @see TrdCommandDispatcher004Controller
 * @since 1.0
 */
@Controller
@PageId("TrdCommandDispatcher004")
@RequestMapping("/invest/trade/investtrade/commanddispatcher/TrdCommandDispatcher004")
public class TrdCommandDispatcher004Controller extends BaseController{
	/**
     * 指令分发service注入
     */
	@Autowired
	private TrdCommandDispatcher001Service trdCommandDispatcher001Service;
	
	@Override
	@RequestMapping(PAGE_INIT)
	public String init(Model model) {
		setSelectOptionToModel(model, DictCode.平安资产类型);
		return super.init(model);
	}

	/**
	 * 指令分发权限分配check
	 * <功能详细描述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("authConfigCheck")
	public AjaxResult authConfigCheck(@RequestBody List<TrdCommandDispatcher004Dto> dtolist){
		AjaxResult result=new AjaxResult();
		String msg = trdCommandDispatcher001Service.authConfigCheck(dtolist);
		if(StringUtil.isEmpty(msg))
	   {
		result.setData("ok");
    	}
	else
	{
		result.setData(msg);
	}
	return result;
	}

	/**
	 * 指令分发权限分配保存
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
    @RequestMapping("saveTraderAuthList")
	public AjaxResult saveTraderAuthList(@RequestBody List<TrdCommandDispatcher004Dto> dto){
        AjaxResult result=new AjaxResult();
		Map<String,Object> param = RequestUtil.getParameters(getRequest());
        trdCommandDispatcher001Service.saveTraderAuthList(dto);
        result.setData("保存成功");
        return result;
	}

	/**
	 * 查询交易员指令分发权限配置列表
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getTraderAuthList")
	public List<TrdCommandDispatcher004Dto> selectAllCommonChar() {
		Map<String,Object> param = RequestUtil.getParameters(getRequest());
		List<TrdCommandDispatcher001Dto> traderAuthList = trdCommandDispatcher001Service.getTraderAuthList(param);
		List<TrdCommandDispatcher004Dto> returnList=new ArrayList<>();
		traderAuthList.forEach(item->
				{
					TrdCommandDispatcher004Dto trdCommandDispatcher004Dto=new TrdCommandDispatcher004Dto();
					trdCommandDispatcher004Dto.setUserName(item.getUserName());
					trdCommandDispatcher004Dto.setRealName(item.getRealName());
					trdCommandDispatcher004Dto.setFinprodType(item.getFinprodType2()==null?"":item.getFinprodType2());
					returnList.add(trdCommandDispatcher004Dto);
				}
		);
		return returnList;
	}
	 
}

/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etragrereptrade.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
//import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etragrereptrade.dto.EtrAgreRepTrade003Dto;
import com.joyintech.tams.invest.entrust.etragrereptrade.dto.EtrAgreRepTradeDto;
import com.joyintech.tams.invest.entrust.etragrereptrade.service.EtrAgreRepTrade001Service;
import com.joyintech.tams.invest.entrust.etragrereptrade.service.EtrAgreRepTrade003Service;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.EtrExchangeTrade003Dto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.service.EtrExchangeTrade001Service;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * @author chendijun
 * @version 1.0 2020年7月1日
 * @see EtrAgreRepTrade003Controller
 * @since 1.0
 */
@Controller
@PageId("EtrAgreRepTrade003")
@RequestMapping("/invest/entrust/etragrereptrade/EtrAgreRepTrade003")
public class EtrAgreRepTrade003Controller extends BaseController{

    @Autowired
    private EtrExchangeTrade001Service etrExchangeTrade001Service;
    @Autowired
    private EtrAgreRepTrade003Service etrAgreRepTrade003Service;
    @Autowired
    private EtrAgreRepTrade001Service etrAgreRepTrade001Service;
    @Autowired
    private EtrCommonService etrCommonService;
//    @Autowired
//    private CommonPositionService commonPositionService;

    @Override
    public String init(Model model) {
    	model.addAttribute("ENTRUST_STATUS_05", DictCodeList.委托状态_已退回.getValue());
        model.addAttribute("ENTRUST_STATUS_07", DictCodeList.委托状态_已撤回.getValue());
        return super.init(model);
        
    }
    
    
    @ResponseBody
    @RequestMapping("findPage")
    public Page<EtrExchangeTrade003Dto> findPage(){
        Page<EtrExchangeTrade003Dto> page = new Page<EtrExchangeTrade003Dto>();
        return page;
    }

    @ResponseBody
    @RequestMapping("selectEtrEntrust")
    public Page<EtrAgreRepTrade003Dto> selectEtrEntrust(){
        Page<EtrAgreRepTrade003Dto>  result = etrAgreRepTrade003Service.selectEtrEntrust(RequestUtil.getPageParam());
        return result;
    }


    @ResponseBody
    @RequestMapping("closeCheck")
    public AjaxResult closeCheck(@RequestBody @Validated({ ValidateGroupEdit.class }) EtrExchangeTrade003Dto dto,
			BindingResult result) {
        AjaxResult webResult = new AjaxResult();
        if (result.hasErrors()) {
			return new AjaxResult(result);
		}
        List<ValidMessage> errList = new ArrayList<>();
        List<EtrExchangeTrade003Dto>  etrExchangeTrade003Dto = dto.getSelectedItem();
        for(EtrExchangeTrade003Dto item : etrExchangeTrade003Dto){
            //银行间保持一致
            if(!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())
            &&!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已退回.getValue())
                    &&!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已撤回.getValue())){
//                errList.add(new ValidMessage(item.getEntrustStatusString()+"的指令不允许关闭!"));
                errList.add(new ValidMessage("该委托状态不是已退回/已接收/已撤回，无法关闭!"));
            }
        }
        if(null!=errList&&errList.size()>0){
            webResult.setErrorList(errList);
            return webResult;
        }
        return webResult;
    }

    @ResponseBody
    @RequestMapping("revokeCheck")
    public AjaxResult revokeCheck(@RequestBody @Validated({ ValidateGroupEdit.class }) EtrExchangeTrade003Dto dto,
			BindingResult result) {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errList = new ArrayList<>();
        List<EtrExchangeTrade003Dto> etrExchangeTrade003Dto = dto.getSelectedItem();
        for (EtrExchangeTrade003Dto item : etrExchangeTrade003Dto) {
            if (!(StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_待接收.getValue())
            		||StringUtil.equals(item.getEntrustStatus(), DictCodeListExt3.委托状态_待分发.getValue()))){
//                errList.add(new ValidMessage(item.getEntrustStatusString() + "的指令不允许撤回!"));
                errList.add(new ValidMessage("该委托状态不是待接收、待分发，无法撤回!"));
            }
        }
        if(null!=errList&&errList.size()>0){
            webResult.setErrorList(errList);
            return webResult;
        }
        return webResult;
    }


    @ResponseBody
    @RequestMapping("entrustClose")
    public AjaxResult close(@RequestBody EtrAgreRepTrade003Dto dto) throws Exception {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errList = new ArrayList<>();
      //判断指令是否已经分发，没分发就不调用交易系统指令状态变更接口；
		List<EtrAgreRepTrade003Dto> etrDistributeList = new ArrayList<EtrAgreRepTrade003Dto>();
		for (EtrAgreRepTrade003Dto selectedItem : dto.getSelectedItem()) {
			EtrDistribute etrDistribute = etrExchangeTrade001Service.getDistributeByEntrustId(selectedItem.getEntrustId());
			if (etrDistribute != null && StringUtils.equals(etrDistribute.getEntrustId(), selectedItem.getEntrustId())) {
				etrDistributeList.add(selectedItem);
			}
		}
        //调用【交易系统指令状态变更接口】
		if (etrDistributeList != null && etrDistributeList.size() > 0) {
			ValidMessage validMessage = etrCommonService.validEtrClose(dto.getSelectedItem(), DictCodeList.委托状态_已关闭.getValue());
			if (null != validMessage) {
				errList.add(validMessage);
				webResult.setErrorList(errList);
				return webResult;
			}
		}
        List<String> entrustIdList = new ArrayList<>();
        for(EtrAgreRepTrade003Dto item : dto.getSelectedItem()){
            item.setFromEtrExchange(dto.getFromEtrExchange());
            item.setAssetType2(DictCodeList.回购类型_质押式协议回购.getValue());

            EtrExchangeTrade003Dto etrExchangeTrade003Dto = new EtrExchangeTrade003Dto();
            BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade003Dto,item);
            etrExchangeTrade001Service.closeDelegate(etrExchangeTrade003Dto);
            // 指令持仓流水、现金流水处理
            etrAgreRepTrade001Service.revokeOrCloseEntrust(item);
            //释放风险接口释放
            entrustIdList.add(item.getEntrustId());
        }
        EntrustCompleteUtil.doComplete(entrustIdList);
        return new AjaxResult(this.getMessage("ETR_X_003"));
    }

    @ResponseBody
    @RequestMapping("entrustRevoke")
	public AjaxResult revoke(@RequestBody EtrAgreRepTrade003Dto dto) throws Exception {
		AjaxResult webResult = new AjaxResult();
		List<ValidMessage> errList = new ArrayList<>();

		//判断指令是否已经分发，没分发就不调用交易系统指令状态变更接口；
		List<EtrAgreRepTrade003Dto> etrDistributeList = new ArrayList<EtrAgreRepTrade003Dto>();
		for(EtrAgreRepTrade003Dto selectedItem : dto.getSelectedItem()){
			EtrDistribute etrDistribute = etrExchangeTrade001Service.getDistributeByEntrustId(selectedItem.getEntrustId());
			if(etrDistribute != null && StringUtils.equals(etrDistribute.getEntrustId(), selectedItem.getEntrustId())){
				etrDistributeList.add(selectedItem);
			}
		}
		// 调用【交易系统指令状态变更接口】
		if (etrDistributeList != null && etrDistributeList.size() > 0) {
			ValidMessage validMessage = etrCommonService.validEtrClose(etrDistributeList, DictCodeList.委托状态_已撤回.getValue());
			if (null != validMessage) {
				errList.add(validMessage);
				webResult.setErrorList(errList);
				return webResult;
			}
		}

		for (EtrAgreRepTrade003Dto item : dto.getSelectedItem()) {
            item.setFromEtrExchange(dto.getFromEtrExchange());
            item.setAssetType2(DictCodeList.回购类型_质押式协议回购.getValue());

            EtrExchangeTrade003Dto etrExchangeTrade003Dto = new EtrExchangeTrade003Dto();
            BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade003Dto,item);
			etrExchangeTrade001Service.recallDelegate(etrExchangeTrade003Dto);
            // 指令持仓流水、现金流水处理
            etrAgreRepTrade001Service.revokeOrCloseEntrust(item);
            //查询首期指令信息
            Map<String,Object> keyMap = new HashMap<>();
            keyMap.put("entrustId",item.getEntrustId());
            EtrAgreRepTradeDto etrAgreRepTradeDto= etrAgreRepTrade001Service.getEntrustInfoByEntrustId(keyMap);
            item.setTradeId(etrAgreRepTradeDto.getTradeId());
            item.setIntiTradeId(etrAgreRepTradeDto.getIntiTradeId());
            item.setBusiPs(etrAgreRepTradeDto.getBusiPs());
            // 释放风险接口释放
            EntrustRdsCommonAop.doPreOccupy(item, BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue(),DictCodeList.平安资产类型_质押式回购.getValue());
        }
		return new AjaxResult(this.getMessage("撤回成功"));
    }

}

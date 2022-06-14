/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrexchangetrade.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
//import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
/*import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;*/
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.EtrExchangeTrade003Dto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.service.EtrExchangeTrade001Service;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * @author chendijun
 * @version 1.0 2020年7月1日
 * @see EtrExchangeTrade003Controller
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTrade003")
@RequestMapping("/invest/entrust/etrexchangetrade/EtrExchangeTrade003")
public class EtrExchangeTrade003Controller extends BaseController{

    @Autowired
    private EtrExchangeTrade001Service etrExchangeTrade001Service;

    @Autowired
    private EtrCommonService etrCommonService;

    @Override
    public String init(Model model) {
        model.addAttribute("PRICE_MODEL_PRM03", DictCodeListExt2.价格模式_市价指令.getValue());
        
        // 场内交易指令画面指令列表自动刷新时间（毫秒）
        long etrExchange_entrustTable_refresh_period = 20000;
        String tempSysParaVal = CacheUtil.getSysParam(
            BussinessConstant.ETREXCHANGE_ENTRUSTTABLE_REFRESH_PERIOD);
        if (StringUtil.isNotBlank(tempSysParaVal)
            && !StringUtil.equals(BussinessConstant.ETREXCHANGE_ENTRUSTTABLE_REFRESH_PERIOD,
                tempSysParaVal)) {
            try {
                etrExchange_entrustTable_refresh_period = Long.parseLong(tempSysParaVal);
            }
            catch (Exception e) {
            }
        }
        model.addAttribute("etrExchange_entrustTable_refresh_period", etrExchange_entrustTable_refresh_period);
        
        return super.init(model);
    }
    
    
    @ResponseBody
    @RequestMapping("findPage")
    public Page<EtrExchangeTrade003Dto> findPage(){
        Page<EtrExchangeTrade003Dto> page = new Page<EtrExchangeTrade003Dto>();
        return page;
    }

    @ResponseBody
    @RequestMapping("selectEtrExchange")
    public Page<EtrExchangeTrade003Dto> selectEtrExchange(){
        Page<EtrExchangeTrade003Dto>  result = etrExchangeTrade001Service.selectEtrExchange(RequestUtil.getPageParam());
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
                errList.add(new ValidMessage(item.getEntrustStatusString()+"的指令不允许关闭!"));
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
                errList.add(new ValidMessage(item.getEntrustStatusString() + "的指令不允许撤回!"));
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
    public AjaxResult close(@RequestBody EtrExchangeTrade003Dto dto) throws Exception {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errList = new ArrayList<>();
      //判断指令是否已经分发，没分发就不调用交易系统指令状态变更接口；
		List<EtrExchangeTrade003Dto> etrDistributeList = new ArrayList<EtrExchangeTrade003Dto>();
		for (EtrExchangeTrade003Dto selectedItem : dto.getSelectedItem()) {
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
        // modify by zhangrui1 委托关闭释放剩余预占 start
        List<String> entrustIdList = new ArrayList<>();
        for(EtrExchangeTrade003Dto item : dto.getSelectedItem()){
            etrExchangeTrade001Service.closeDelegate(item);
            //释放风险接口释放
            entrustIdList.add(item.getEntrustId());
        }
        EntrustCompleteUtil.doComplete(entrustIdList);
        // modify by zhangrui1 委托关闭释放剩余预占 end
        return new AjaxResult(this.getMessage("ETR_X_003"));
    }

    @ResponseBody
    @RequestMapping("entrustRevoke")
	public AjaxResult revoke(@RequestBody EtrExchangeTrade003Dto dto) throws Exception {
		AjaxResult webResult = new AjaxResult();
		List<ValidMessage> errList = new ArrayList<>();

		//判断指令是否已经分发，没分发就不调用交易系统指令状态变更接口；
		List<EtrExchangeTrade003Dto> etrDistributeList = new ArrayList<EtrExchangeTrade003Dto>();
		for(EtrExchangeTrade003Dto selectedItem : dto.getSelectedItem()){
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

		for (EtrExchangeTrade003Dto item : dto.getSelectedItem()) {
			etrExchangeTrade001Service.recallDelegate(item);
			// 释放风险接口释放
			
            if(StringUtil.equals(item.getPs(),DictCodeListExt3.交易方向_债转股.getValue())) {
                EntrustRdsCommonAop.doPreOccupy(item, BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(),item.getPaAssetType()
                    );
            } else {
                EntrustRdsCommonAop.doPreOccupy(item, BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue(),item.getPaAssetType());
            }
			 
		}
		return new AjaxResult(this.getMessage("撤回成功"));
    }

}

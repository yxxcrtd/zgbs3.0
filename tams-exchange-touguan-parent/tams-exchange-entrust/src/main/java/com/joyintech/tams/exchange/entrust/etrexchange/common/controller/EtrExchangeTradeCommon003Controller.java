/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.common.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.component.base.core.MstWorkflowCommonService;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeOperateRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon002Service;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * @author chendijun
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeCommon003Controller
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTradeCommon003")
@RequestMapping("/etrexchange/common/EtrExchangeTradeCommon003")
@Api(tags = "场内交易-指令共通接口")
public class EtrExchangeTradeCommon003Controller extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(EtrExchangeTradeCommon003Controller.class);

    @Autowired
    private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;

    @Autowired
    private EtrExchangeTradeCommon002Service etrExchangeTradeCommon002Service;

    @Autowired
    private EtrCommonService etrCommonService;

    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @PostMapping("/getPageParams")
    public Message<Map<String,Object>> init() {
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("PRICE_MODEL_PRM03", DictCodeListExt2.价格模式_市价指令.getValue());
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
        initPageParams.put("etrExchange_entrustTable_refresh_period", etrExchange_entrustTable_refresh_period);
        return new Message<Map<String, Object>>(initPageParams).success();
    }


    @ApiOperation(value = "关闭校验", notes = "关闭校验")
    @ResponseBody
    @PostMapping("closeCheck")
    public Message<AjaxResult> closeCheck(@RequestBody EtrExchangeTradeOperateRequest dto,
			BindingResult result) {
        try {
            AjaxResult ajax = new AjaxResult();
            if (result.hasErrors()) {
                return new Message<AjaxResult>().success().setData(new AjaxResult(result));
            }
            List<ValidMessage> errList = new ArrayList<>();
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            List<EtrExchangeTradeOperateRequest>  tradeOperateRequests = dto.getSelectedItem();
            for(EtrExchangeTradeOperateRequest item : tradeOperateRequests){
                //银行间保持一致
                if(!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())
                &&!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已退回.getValue())
                        &&!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已撤回.getValue())){
                    errList.add(new ValidMessage(item.getEntrustStatusString()+"的指令不允许关闭!"));
                    break;
                }else if(StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())
                        &&item.getDealPercent()!=null && BigDecimal.ZERO.compareTo(item.getDealPercent())<0){
                    errList.add(new ValidMessage(item.getEntrustStatusString()+"成交进度大于0的指令不允许关闭!"));
                }else if(StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())
                        &&item.getImplementPercent()!=null && BigDecimal.ZERO.compareTo(item.getImplementPercent())<0){
                    errList.add(new ValidMessage(item.getEntrustStatusString()+"执行进度大于0的指令不允许关闭!"));
                }
                wflValidInputParameter.setBusinessDataPk1(item.getEntrustId());
                if (WorkFlowUtil.isNotFromToDoList() && mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                    errList.add(new ValidMessage("所选对象存在有效或流程中的数据不能关闭"));
                }
            }
            if(null!=errList&&errList.size()>0){
                ajax.setErrorList(errList);
            }
            if(null!=ajax){
                return new Message<AjaxResult>().success().setData(ajax);
            }else{
                return new Message<AjaxResult>().success().setMessage("校验通过");
            }
        }catch (Exception e) {
            logger.error("多组合指令新建保存/提交校验异常",e);
            return new Message<AjaxResult>().fail().setMessage("校验失败");
        }
    }

    @ApiOperation(value = "撤回校验", notes = "撤回校验")
    @ResponseBody
    @PostMapping("revokeCheck")
    public Message<AjaxResult> revokeCheck(@RequestBody EtrExchangeTradeOperateRequest dto,
			BindingResult result) {
        try {
            AjaxResult webResult = new AjaxResult();
            List<ValidMessage> errList = new ArrayList<>();
            List<EtrExchangeTradeOperateRequest> etrExchangeTrade003Dto = dto.getSelectedItem();
            for (EtrExchangeTradeOperateRequest item : etrExchangeTrade003Dto) {
                if (!(StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_待接收.getValue())
                        || StringUtil.equals(item.getEntrustStatus(), DictCodeListExt3.委托状态_待分发.getValue()))) {
                    errList.add(new ValidMessage(item.getEntrustStatusString() + "的指令不允许撤回!"));
                }
            }
            if (null != errList && errList.size() > 0) {
                webResult.setErrorList(errList);
                return new Message<AjaxResult>().success().setData(webResult);
            }
            return new Message<AjaxResult>().success().setData(webResult);
        }catch (Exception e) {
            logger.error("多组合指令新建保存/提交校验异常",e);
            return new Message<AjaxResult>().fail().setMessage("校验失败");
        }

    }

    @ApiOperation(value = "关闭操作", notes = "关闭操作")
    @ResponseBody
    @PostMapping("entrustClose")
    public Message<AjaxResult> close(@RequestBody EtrExchangeTradeOperateRequest dto) throws Exception {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errList = new ArrayList<>();
      //判断指令是否已经分发，没分发就不调用交易系统指令状态变更接口；
		List<EtrExchangeTradeOperateRequest> etrDistributeList = new ArrayList<EtrExchangeTradeOperateRequest>();
		for (EtrExchangeTradeOperateRequest selectedItem : dto.getSelectedItem()) {
			EtrDistribute etrDistribute = etrExchangeTradeCommon001Service.getDistributeByEntrustId(selectedItem.getEntrustId());
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
                return new Message<AjaxResult>().success().setData(webResult);
			}
		}
        // modify by zhangrui1 委托关闭释放剩余预占 start
        List<String> entrustIdList = new ArrayList<>();
        for(EtrExchangeTradeOperateRequest item : dto.getSelectedItem()){
            etrExchangeTradeCommon001Service.closeDelegate(item);
            //释放风险接口释放
            entrustIdList.add(item.getEntrustId());
        }
        EntrustCompleteUtil.doComplete(entrustIdList);
        // modify by zhangrui1 委托关闭释放剩余预占 end
        return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("ETR_X_003")));
    }


    @ApiOperation(value = "撤回操作", notes = "撤回操作")
    @ResponseBody
    @PostMapping("entrustRevoke")
	public Message<AjaxResult> revoke(@RequestBody EtrExchangeTradeOperateRequest dto) throws Exception {
		AjaxResult webResult = new AjaxResult();
		List<ValidMessage> errList = new ArrayList<>();

		//判断指令是否已经分发，没分发就不调用交易系统指令状态变更接口；
		List<EtrExchangeTradeOperateRequest> etrDistributeList = new ArrayList<EtrExchangeTradeOperateRequest>();
		for(EtrExchangeTradeOperateRequest selectedItem : dto.getSelectedItem()){
			EtrDistribute etrDistribute = etrExchangeTradeCommon001Service.getDistributeByEntrustId(selectedItem.getEntrustId());
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
				return new Message<AjaxResult>().success().setData(webResult);
			}
		}
		for (EtrExchangeTradeOperateRequest item : dto.getSelectedItem()) {
			etrExchangeTradeCommon001Service.recallDelegate(item);
			// 释放风险接口释放
			
            if(StringUtil.equals(item.getPs(),DictCodeListExt3.交易方向_债转股.getValue())) {
                EntrustRdsCommonAop.doPreOccupy(item, BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(),item.getPaAssetType()
                    );
            } else {
                EntrustRdsCommonAop.doPreOccupy(item, BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue(),item.getPaAssetType());
            }
			 
		}
		return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("撤回成功")));
    }

}

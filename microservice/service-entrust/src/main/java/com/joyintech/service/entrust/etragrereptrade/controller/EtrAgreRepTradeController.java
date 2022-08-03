/*
 * 文件名：EtrBondFirstBiddingController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.entrust.etragrereptrade.controller;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.entrust.etragrereptrade.dto.EtrAgreRepTrade002Dto;
import com.joyintech.service.entrust.etragrereptrade.dto.EtrAgreRepTrade003Dto;
import com.joyintech.service.entrust.etragrereptrade.dto.OnWayRep002Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ReflectUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.entrust.etragrereptrade.dto.EtrAgreRepTradeDto;
import com.joyintech.service.entrust.etragrereptrade.service.EtrAgreRepTradeService;
import com.joyintech.service.entrust.etrexchangetrade.dto.EtrExchangeTrade001Dto;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;


/**
 * 场内交易委托
 * 
 * @author zhangnian
 * @version 1.0 2019年10月24日
 * @see EtrAgreRepTradeController
 * @since 1.0
 */
@RestController("EtrAgreRepTradeCtr_dtx")
@RequestMapping("/EtrAgreRepTrade")
public class EtrAgreRepTradeController {
    @Autowired
    private EtrAgreRepTradeService etrAgreRepTradeService;

    /**
     * 对手方交易员联想文本框初始化。
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    @PostMapping("/selectCounterTrader")
    public List<SuggestDto> selectCounterTrader(@RequestBody Map<String, String> map) {
        return etrAgreRepTradeService.selectCounterTrader(map);
    }

    /**
     * 本方交易员联想文本框初始化。
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    @PostMapping("/selectTrader")
    public List<SuggestDto> selectTrader(@RequestBody Map<String, String> map) {
        return etrAgreRepTradeService.selectTrader(map);
    }

    @PostMapping("/findPositionsPage/{page}/{length}")
    public Page<EtrAgreRepTrade002Dto> findPositionsPage(@RequestBody Map<String, Object> pageParam,
                                                         @PathVariable("page") int page,
                                                         @PathVariable("length") int length)
            throws Exception {
        return etrAgreRepTradeService.findPositionsPage(page, length, pageParam);
    }
    @PostMapping("/findOnWayRepPage/{page}/{length}")
    public Page<OnWayRep002Dto> findOnWayRepPage(@RequestBody Map<String, Object> pageParam,
                                                 @PathVariable("page") int page,
                                                 @PathVariable("length") int length)
            throws Exception {
        return etrAgreRepTradeService.findOnWayRepPage(page, length, pageParam);
    }

    /**
     * 查询原成交信息
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    @PostMapping("/getEntrustInfoByTradeId")
    public List<EtrAgreRepTradeDto> getEntrustInfoByTradeId(@RequestBody Map<String, String> map) {
        return etrAgreRepTradeService.getEntrustInfoByTradeId(map);
    }
    /**
     * 查询原成交信息
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    @PostMapping("/getEntrustInfoByEntrustId")
    public List<EtrAgreRepTradeDto> getEntrustInfoByEntrustId(@RequestBody Map<String, String> map) {
        return etrAgreRepTradeService.getEntrustInfoByEntrustId(map);
    }

    @PostMapping("selectEtrEntrust/{page}/{length}")
    public Page<EtrAgreRepTrade003Dto> selectEtrEntrust(@RequestBody Map<String,Object> param, @PathVariable("page") int page, @PathVariable("length") int length){
        return etrAgreRepTradeService.selectEtrEntrust(page,length,param);
    }

    /**
     * 保存check
     * @param entity
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    @RequestMapping("checkSave")
    public AjaxResult checkSave(@RequestBody EtrAgreRepTradeDto entity, BindingResult result) throws IllegalAccessException, InvocationTargetException {
    	AjaxResult ajax = new AjaxResult(etrAgreRepTradeService.checkSave(entity, result));
    	Field[] fields = ReflectUtil.getDeclaredFields(ajax);
    	for(Field field : fields) {
    		if(StringUtil.equals(field.getName(), "messageType")) {
    			field.setAccessible(true);
    	    	ReflectionUtils.setField(field, ajax, MessageType.错误消息.getCode());
    	    	break;
    		}
    	}
    	return ajax;
   }
    /**
     * 
     * 工作流完成方法
     * <功能详细描述>
     * @param dto
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doComplete")
    public void doComplete(@RequestBody EtrAgreRepTradeDto dto) throws IllegalAccessException, InvocationTargetException{
    	etrAgreRepTradeService.doComplete(dto, dto.getOperateType());
    }
    
    @PostMapping("buildWflEntity")
    public BaseEtrInvestInfoDto buildWflEntity(@RequestBody EtrAgreRepTradeDto dto) throws IllegalAccessException, InvocationTargetException{
        return etrAgreRepTradeService.buildWflEntity(dto);
    }

    /**
     * getptlAcctNo
     * @param map
     * @return
     */
    @PostMapping("/getptlAcctNo")
    public List<SelectOptionDto> getptlAcctNo(@RequestBody Map<String, String> map) {
        return etrAgreRepTradeService.getptlAcctNo(map);
    }
    /**
     * getptlAcctNo
     * @param map
     * @return
     */
    @PostMapping("/queryOtherSecPosition")
    public BigDecimal queryOtherSecPosition(@RequestBody Map<String, Object> map) {
        return etrAgreRepTradeService.queryOtherSecPosition(map);
    }

    /**
     *  查询首期的到期结算金额
     *
     * @param map 检索参数
     * @return 到期结算金额
     */
    @PostMapping("/getFirstTradeAmtByTradeId")
    public BigDecimal getFirstTradeAmtByTradeId(@RequestBody Map<String, String> map) {
        return etrAgreRepTradeService.getFirstTradeAmtByTradeId(map);
    }
}

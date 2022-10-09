/*
 * 文件名：CfetsQuotedCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.common.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.system.base.entity.SysUserAdd;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealEntrustOrderDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.service.CfetsBondDealQuotedPrice002Service;
import com.joyintech.tams.invest.bankuplink.bond.service.CfetsBondDealQuotedPrice003Service;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;

/**
 * 银行间上行共通Service
 * 
 * @author zhangliang
 * @version 1.0 2020年5月11日
 * @see CfetsQuotedCommonService
 * @since 1.0
 */
@Service
public class CfetsQuotedCommonService extends BaseService {

    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * DateCalService
     */
    @Autowired(required = false)
    private DateCalService dateCalService;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPrice002Service cfetsBondDealQuotedPrice002Service;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPrice003Service cfetsBondDealQuotedPrice003Service;

    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    /**
     * 根据查询查询符合条件的委托列表
     * 
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryEntrustInfoList(Map<String, Object> param) {
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("loginUserId", LoginUserUtil.getUserName());
        if(StringUtil.isNotEmpty(param.get("noLoginUserId"))){
        	param.remove("loginUserId");
        }
        return restTemplate.postForPage(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                        + "/CfetsBondDealQuotedPrice/queryEntrustInfoList",
            param, SelectOptionDto.class);
    }

    // ZGZGST-1936:CFETS历史报价查询-指令代码使用文本框 fuyongsheng 20210904 add start
    /**
     * 根据查询查询符合条件的历史委托列表
     *
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public Page<SuggestDto> queryHisEntrustInfoList(Map<String, Object> param) {
        param.put("loginUserId", LoginUserUtil.getUserName());
        if(StringUtil.isNotEmpty(param.get("noLoginUserId"))){
            param.remove("loginUserId");
        }
        return restTemplate.postForPage(ServiceApplication.BANKUPLINK_SERVICE_NAME
                        + "/CfetsBondDealQuotedPrice/queryHisEntrustInfoList",
                param, SuggestDto.class);
    }
    // ZGZGST-1936:CFETS历史报价查询-指令代码使用文本框 fuyongsheng 20210904 add end

    /**
     * 委托放弃处理
     * 
     * @param entrustId 委托代码
     * @return 执行是否成功
     * @see
     * @since 1.0
     */
    public AjaxResult entrustGiveUpHandle(String entrustId) {
        AjaxResult ajaxResult = new AjaxResult();
        String flag = restTemplate.getForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                 + "/CfetsQuotedCommon/entrustGiveUpHandle?entrustId="
                                                 + entrustId,
                                                 String.class);
        if(StringUtil.equals("1", flag)){
        	ajaxResult.appendMessage(new ValidMessage("已接收且完成度为0指令,才能执行放弃操作！"));
		}
		else if(StringUtil.equals("2", flag)){
			ajaxResult.appendMessage(new ValidMessage("存在流程中的报价，不可放弃！"));
		}

        return ajaxResult;
    }
    
    /**
     * 批量放弃委托
     */
    public List<ValidMessage> entrustGiveUpHandleList(Map<String,Object> param){
		return restTemplate.postForList(
	    	ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/entrustGiveUpHandleList", param,ValidMessage.class);
	}

    /**
     * 查询本方交易员列表
     * 
     * @param param 入参
     * @return 本方交易员列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryMySideTraderPage(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                        + "/CfetsQuotedCommon/queryMySideTraderPage",
            param, SelectOptionDto.class);
    }

    /**
     * 查询本方交易员列表
     * 
     * @param param 入参
     * @return 本方交易员列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryMySideTraderList(Map<String, Object> param) {
        return restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                        + "/CfetsQuotedCommon/queryMySideTraderList",
            param, SelectOptionDto.class);
    }

    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryMySidePage(Map<String, Object> param) {
        return restTemplate.postForPage(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/queryMySidePage",
            param, SelectOptionDto.class);
    }
    
    
    /**
     * 查询对手方机构列表
     * 
     * @param param 入参
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryCounterSidePage(Map<String, Object> param) {
        return restTemplate.postForPage(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/queryCounterSidePage",
            param, SelectOptionDto.class);
    }

    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryMySideList(Map<String, Object> param) {
        return restTemplate.postForList(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/queryMySideList",
            param, SelectOptionDto.class);
    }

    /**
     * 获取下一个工作日
     *
     * @param parameters 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    public Date getNextWorkDay(Date tradeDate) {
        if (dateCalService != null) {
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate,
                DateUtil.addDays(tradeDate, 31), CacheUtil.getSysParam("ibuVar_calendarId"));
            return dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
        }
        return null;
    }
    
    /**
     * 计算结束日前的连续的节假日天数
     * 
     * @param mdate 结束日
     * @return 连续的节假日天数
     * @throws Exception 
     */
    public Long getBetHoliDay(Date mdate) throws Exception {
    	Long count = 0L;
    	if (dateCalService != null) {
            List<String> holiday = dateCalService.getDealCalendarHoliday(DateUtil.addDays(mdate, -31),
            		mdate, CacheUtil.getSysParam("ibuVar_calendarId"));
            String day = dateCalService.calculationTradingDayForBefore(DateUtil.format(mdate), 1, holiday, "1");
            Long betDay = DateUtil.getBetweenDayNumber(DateUtil.parse(day), mdate);
            if (betDay != 1L) {
            	count = betDay - 1;
            }
        }
    	return count;
    }

    /**
     * 获取工作日
     *
     * @param parameters 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    public String getWorkDay(Map<String, Object> parameters) {
        if (dateCalService != null) {
            Date tradeDate = DateUtil.parse((String)parameters.get("tradeDate"));
            String settleSpreed = (String)parameters.get("settleSpreed");
            Date tradingDay = null;
            //首期结算日
            if(StringUtil.isNotEmpty(settleSpreed)){
                tradeDate = DateUtil.addDay(tradeDate,1);
            }
            if (dateCalService.isDealCalendarHoliday(tradeDate,
                CacheUtil.getSysParam("ibuVar_calendarId"))) {
                List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate,
                    DateUtil.addDays(tradeDate, 31), CacheUtil.getSysParam("ibuVar_calendarId"));
                tradingDay = dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
            }
            else {
                tradingDay = tradeDate;
            }
            return DateUtil.format(tradingDay);
        }
        return null;
    }

    /**
     * check操作是否合规
     * 
     * @param bondQuoteInfoDto 入参对象
     * @return 判断结果
     * @see
     * @since 1.0
     */
    public AjaxResult checkOperationAllow(BondQuoteInfoDto bondQuoteInfoDto) {
        AjaxResult ar = new AjaxResult();

        // 做市方-> 请求报价(回复非做市方数据)
        // 非做市方-> 请求报价(主动发起多个交易对手)
        // 询价方-> 对话报价(主动发起对话报价)
        // 回复方-> 对话报价(回复别人发起的对话报价)

        // 撤回
        boolean statusChangeFlag = false;
        boolean validFlag = false;
        String msg ="";
        //统一处理
        //报价权限(是否属于该交易员)
        if(StringUtil.equals(bondQuoteInfoDto.getValidStatus(),BussinessDictCodeList.有效状态_有效.getValue())){
            Map<String,Object> mapOpr= new HashMap<>();
            mapOpr.put("quoteIdTyep","bond");
            mapOpr.put("quoteId",bondQuoteInfoDto.getQuoteId());
            boolean opeQuotedPrice = cfetsQuotedCommonService.isOpeQuotedPrice(mapOpr);
            if(!opeQuotedPrice){
                ar.appendMessage(new ValidMessage("交易员无权限操作报价,报价编号:"+bondQuoteInfoDto.getQuoteId()));
                return ar;
            }
        }
        if (StringUtil.equals(bondQuoteInfoDto.getOperationType(), "recall")) {
            if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_询价方.getValue())) {
                CfetsBondDealDialogueQuoteDto cetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                    bondQuoteInfoDto.getQuoteId(),bondQuoteInfoDto.getValidStatus());
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cetsBondDealDialogueQuoteDto)) {
                    statusChangeFlag = true;
                    if ((StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_已新增.getValue())
                         || StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                             BussinessDictCodeList.银行间上行报价状态_已修改.getValue()))
                        && StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                            BussinessDictCodeList.银行间上行发送状态_成功.getValue()))
                        validFlag = true;
                }
                msg ="交易状态和发送状态分别为已新增/成功，已修改/成功的对话报价，才能执行撤回操作！";
            }
            else if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_做市方.getValue())) {
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cfetsBondDealRequestQuoteDto)) {
                    statusChangeFlag = true;
                    if ((StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_已回复.getValue())
                         || StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                             BussinessDictCodeList.银行间上行报价状态_已修改.getValue()))
                        && !StringUtil.isNotEmpty(bondQuoteInfoDto.getSendStatus())) {
                        validFlag = true;
                    }
                }
                msg ="本方角色为做市方，交易状态和发送状态分别为已回复/成功，已修改/空的请求报价，才能执行撤回操作！";
            }
            else if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_非做市方.getValue())) {
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                /*if (this.checkQuoteStatusChange(bondQuoteInfoDto, cfetsBondDealRequestQuoteDto)) {
                    statusChangeFlag = true;
                    if ((StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_已回复.getValue())
                         && !StringUtil.isNotEmpty(bondQuoteInfoDto.getSendStatus()))
                        || (StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                            BussinessDictCodeList.银行间上行报价状态_待回复.getValue())
                            && StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                                BussinessDictCodeList.银行间上行发送状态_成功.getValue()))) {
                        validFlag = true;
                    }
                }*/
                //请求报价撤回  只需要查报价表的状态,无需查看子报价的状态
                if ((StringUtil.equals(cfetsBondDealRequestQuoteDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_已回复.getValue())
                        && !StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getSendStatus()))
                        || (StringUtil.equals(cfetsBondDealRequestQuoteDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_待回复.getValue())
                        && StringUtil.equals(cfetsBondDealRequestQuoteDto.getSendStatus(),
                        BussinessDictCodeList.银行间上行发送状态_成功.getValue()))) {
                    statusChangeFlag = true;
                    validFlag = true;
                }
                msg ="本方角色为非做市方，交易状态和发送状态分别为待回复/成功，已回复/空的请求报价，才能执行撤回操作！";
            }
            else {
            	msg ="交易状态和发送状态分别为已新增/成功，已修改/成功的对话报价，才能执行撤回操作！";
                statusChangeFlag = true;
            }

            ar.setData("请确认是否撤回报价？");
        }
        // 复制
        else if (StringUtil.equals(bondQuoteInfoDto.getOperationType(), "copy")) {
            if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_询价方.getValue())) {
                CfetsBondDealDialogueQuoteDto cetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cetsBondDealDialogueQuoteDto)) {
                    statusChangeFlag = true;
                    if (((StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_已新增.getValue())
                    	  ||StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                                  BussinessDictCodeList.银行间上行报价状态_已修改.getValue())
                          || StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                              BussinessDictCodeList.银行间上行报价状态_已撤回.getValue()))
                         && StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                             BussinessDictCodeList.银行间上行发送状态_成功.getValue()))
                        || ((StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                            BussinessDictCodeList.银行间上行报价状态_已成交.getValue())
                             || StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                                 BussinessDictCodeList.银行间上行报价状态_已终止.getValue()))
                            && !StringUtil.isNotEmpty(bondQuoteInfoDto.getSendStatus())))
                        validFlag = true;
                }
                msg ="交易状态和发送状态分别为已新增/成功，已修改/成功，已撤回/成功，已成交/空，已终止/空的对话报价，才能执行复制操作！";
            }
            else if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_非做市方.getValue())) {
				CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service
						.getBondRequestQuote(bondQuoteInfoDto.getQuoteId(), null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cfetsBondDealRequestQuoteDto)) {
                    statusChangeFlag = true;
                    if (((StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_待回复.getValue())
                          || StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                              BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())
                          || StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                              BussinessDictCodeList.银行间上行报价状态_已成交.getValue()))
                         && StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                             BussinessDictCodeList.银行间上行发送状态_成功.getValue()))
                        || ((StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                            BussinessDictCodeList.银行间上行报价状态_已终止.getValue())
                             || StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                                 BussinessDictCodeList.银行间上行报价状态_已过期.getValue()))
                            && !StringUtil.isNotEmpty(bondQuoteInfoDto.getSendStatus()))) {
                        validFlag = true;
                    }
                }
                msg ="本方角色为非做市方，交易状态和发送状态分别为待回复/成功，已撤回/成功，已终止/空,已过期/空，已成交/成功的请求报价，才能执行复制操作！";
            }
            else {
            	msg ="交易状态和发送状态分别为已新增/成功，已修改/成功，已撤回/成功，已成交/空，已终止/空的对话报价，才能执行复制操作！";
                statusChangeFlag = true;
            }
        }
        // 回复
        else if (StringUtil.equals(bondQuoteInfoDto.getOperationType(), "answer")) {
            if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_回复方.getValue())) {
                CfetsBondDealDialogueQuoteDto cetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cetsBondDealDialogueQuoteDto)) {
                    statusChangeFlag = true;
                    if (StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_待回复.getValue())
                        && !StringUtil.isNotEmpty(bondQuoteInfoDto.getSendStatus()))
                        validFlag = true;
                }
            }
            else if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_做市方.getValue())) {
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cfetsBondDealRequestQuoteDto)) {
                    statusChangeFlag = true;
                    if (StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_待回复.getValue())
                        && !StringUtil.isNotEmpty(bondQuoteInfoDto.getSendStatus())) {
                        validFlag = true;
                    }
                }
            }
            else if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_非做市方.getValue())) {
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cfetsBondDealRequestQuoteDto)) {
                    statusChangeFlag = true;
                    if (StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_已回复.getValue())
                        && !StringUtil.isNotEmpty(bondQuoteInfoDto.getSendStatus())) {
                        validFlag = true;
                    }
                }
            }
            else {
                statusChangeFlag = true;
            }

            ar.setData("请确认是否回复报价？");
        }
        // 回退
        else if (StringUtil.equals(bondQuoteInfoDto.getOperationType(), "doBack")) {
            if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.交易类型_对话报价.getValue())) {
                CfetsBondDealDialogueQuoteDto cetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cetsBondDealDialogueQuoteDto)) {
                    statusChangeFlag = true;
                    if (StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                        BussinessDictCodeList.银行间上行发送状态_失败.getValue())) {
                        validFlag = true;
                    }
                }
            }
            else if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.交易类型_请求报价.getValue())) {
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cfetsBondDealRequestQuoteDto)) {
                    statusChangeFlag = true;
                    if (StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                        BussinessDictCodeList.银行间上行发送状态_失败.getValue())) {
                        validFlag = true;
                    }
                }
            }
            else {
                statusChangeFlag = true;
            }

            ar.setData("请确认是否回退报价？");
        }
        // 删除
        else if (StringUtil.equals(bondQuoteInfoDto.getOperationType(), "doDel")) {
            if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.交易类型_对话报价.getValue())) {
                CfetsBondDealDialogueQuoteDto cetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cetsBondDealDialogueQuoteDto)) {
                    statusChangeFlag = true;
                    if (StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                        BussinessDictCodeList.银行间上行发送状态_待发送.getValue())) {
                        validFlag = true;
                    }
                }
            }
            else if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.交易类型_请求报价.getValue())) {
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cfetsBondDealRequestQuoteDto)) {
                    statusChangeFlag = true;
                    if (StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                        BussinessDictCodeList.银行间上行发送状态_待发送.getValue())) {
                        validFlag = true;
                    }
                }
            }

            ar.setData("请确认是否删除报价？");
        }
        // 刷新
        else if (StringUtil.equals(bondQuoteInfoDto.getOperationType(), "reFresh")) {
            if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.交易类型_对话报价.getValue())) {
                CfetsBondDealDialogueQuoteDto cetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cetsBondDealDialogueQuoteDto)) {
                    statusChangeFlag = true;
                    if (StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_新增中.getValue())
                        && StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                            BussinessDictCodeList.银行间上行发送状态_已发送.getValue())) {
                        validFlag = true;
                    }
                }
            }
            else if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.交易类型_请求报价.getValue())) {
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cfetsBondDealRequestQuoteDto)) {
                    statusChangeFlag = true;
                    if (StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_新增中.getValue())
                        && StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                            BussinessDictCodeList.银行间上行发送状态_已发送.getValue())) {
                        validFlag = true;
                    }
                }
            }
        }
        //修改
        else if (StringUtil.equals(bondQuoteInfoDto.getOperationType(), "edit")) {
            if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.交易类型_对话报价.getValue())) {
                CfetsBondDealDialogueQuoteDto cetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                    bondQuoteInfoDto.getQuoteId(),null);
                if (this.checkQuoteStatusChange(bondQuoteInfoDto, cetsBondDealDialogueQuoteDto)) {
                    statusChangeFlag = true;
                    if ((StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_已新增.getValue()) || StringUtil.equals(bondQuoteInfoDto.getTradeStatus(),
                                BussinessDictCodeList.银行间上行报价状态_已修改.getValue()))
                        && StringUtil.equals(bondQuoteInfoDto.getSendStatus(),
                            BussinessDictCodeList.银行间上行发送状态_成功.getValue())) {
                        validFlag = true;
                    }
                }
                msg ="交易状态和发送状态分别为已新增/成功，已修改/成功的对话报价，才能执行修改操作！";
            }else{
            	msg ="请求报价非做市方不允许进行修改操作！";
            }
        }

        if(StringUtil.isNotEmpty(msg)){
        	if (!statusChangeFlag||!validFlag) {
        		ar.appendMessage(new ValidMessage(msg));
        	}
        }else{
        	if (!statusChangeFlag) {
                ar.appendMessage(new ValidMessage("状态已变更，请重新检查数据状态！"));
            }
            if (!validFlag) {
                ar.appendMessage(new ValidMessage("选择记录不满足要求！"));
            }
        }
        return ar;
    }

    /**
     * 校验画面对象与数据库对象数据状态是否一致
     * 
     * @param bondQuoteInfoDto 画面传入对象
     * @param cfetsBondDealEntrustOrderDto 数据库查询对象
     * @return 是否一致
     * @see
     * @since 1.0
     */
    private boolean checkQuoteStatusChange(BondQuoteInfoDto bondQuoteInfoDto,
                                           CfetsBondDealEntrustOrderDto cfetsBondDealEntrustOrderDto) {
        boolean flag = false;
        if (StringUtil.equals(cfetsBondDealEntrustOrderDto.getQuoteRoleType(),
            bondQuoteInfoDto.getQuoteRoleType())
            && StringUtil.equals(
                StringUtil.defaultString(cfetsBondDealEntrustOrderDto.getSendStatus(), ""),
                StringUtil.defaultString(bondQuoteInfoDto.getSendStatus(), ""))
            && StringUtil.equals(
                StringUtil.defaultString(cfetsBondDealEntrustOrderDto.getTradeStatus(), ""),
                StringUtil.defaultString(bondQuoteInfoDto.getTradeStatus(), ""))) {
            flag = true;
        }

        return flag;
    }

    /**
     * 根据报价编号查询错误信息
     * 
     * @param quoteId String 报价代码
     * @return String 错误信息
     * @see
     * @since 1.0
     */
    public String selectErrlog(String quoteId) {
        return restTemplate.getForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                         + "/CfetsQuotedCommon/selectErrlog?quoteId=" + quoteId,
            String.class);
    }

    /**
     * 根据查询查询符合条件的组合代码
     * 
     * @param param 入参
     * @return 组合代码
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> selectPortfolioId(Map<String, Object> param) {
        String userId = SecurityUtil.getLoginUser().getUserName();
        param.put("userId", userId == null ? "" : userId);
        param.put("platDate", iPlatformDate.getSysDateStr());
		if (null != param.get("responseMySide") && StringUtils.isNotEmpty(param.get("responseMySide").toString())) {
			param.put("mySide", param.get("responseMySide").toString());
		}
        return restTemplate.postForPage(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/selectPortfolioId",
            param, SelectOptionDto.class);
    }

    /**
     * 获取新建界面的资产代码联想下拉框框
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryFinprodIdPage(Map<String, Object> param) {
    	Page<SelectOptionDto> page = restTemplate.postForPage(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/queryFinprodIdPage",
            param, SelectOptionDto.class);
    	return page;
    }

    /**
     * 获取新建界面的资产代码联想下拉框框
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryFinprodIdList(Map<String, Object> param) {
        return restTemplate.postForList(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/queryFinprodIdList",
            param, SelectOptionDto.class);

    }

    /**
     * 获取金融产品代码列表
     *
     * @return 金融产品代码列表
     * @see
     * @since 1.0
     */
    public List<String> queryFinprodMarketIdList() {
        return restTemplate.getForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                       + "/CfetsQuotedCommon/queryFinprodMarketIdList",
            String.class);

    }

    /**
     * 根据当前登录用户查询对应的交易员id<br/>
     * <功能详细描述>
     * 
     * @param param 参数
     * @return 对应的交易员id
     * @see
     * @since 1.0
     */
    public String queryLoginTraderId(Map<String, Object> param) {
        param.put("loginUserId", LoginUserUtil.getUserName());
        //TODO
        List<SysUserAdd> sysUserList = restTemplate.postForList(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/queryLoginTraderId",
                param, SysUserAdd.class);
        if (!StringUtil.isEmptyList(sysUserList)) {
        	return sysUserList.get(0).getIbuTrader();
        }
        return StringUtil.EMPTY;
    }
    
    /**
     * 根据查询条件查询指令编号
     * 
     * @param param 入参
     * @return 报价编号
     * @see
     * @since 1.0
     */
    public Page<SuggestDto> queryCfetsEntrustIdList(Map<String, Object> param) {
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("loginUserId", LoginUserUtil.getUserName());
        return restTemplate.postForPage(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/queryCfetsEntrustIdList",
            param, SuggestDto.class);
    }

    public Page<SelectOptionDto> selectEntrustId(Map<String, Object> param) {
        return restTemplate.postForPage(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/selectEntrustId",
                param, SelectOptionDto.class);
    }

    /**
     * 是否可以操作报价
     * @param param
     * @return
     */
    public boolean isOpeQuotedPrice(Map<String, Object> param) {
        param.put("loginUserId", LoginUserUtil.getUserName());
        return restTemplate.postForObject(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/isOpeQuotedPrice",
                param, Boolean.class);
    }
}

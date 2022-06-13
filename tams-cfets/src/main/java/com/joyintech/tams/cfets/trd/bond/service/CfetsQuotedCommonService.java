/*
 * 文件名：CfetsQuotedCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.bond.service;

import com.joyintech.background.component.uplink.interbank.database.dao.IbuQuoteBondMapper;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteBond;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.bond.service.CfetsBondDealQuotedPriceService;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.bond.dto.EntrustAvaShareAmtDto;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.cfets.position.service.PositionInfoService;
import com.joyintech.tams.common.base.dao.SppiImportDataMapper;
import com.joyintech.tams.common.base.dao.StlSecAcctInfoMapper;
import com.joyintech.tams.common.base.entity.SppiImportData;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealEntrustOrderDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 银行间上行共通Service
 * 
 * @author zhangliang
 * @version 1.0 2020年5月11日
 * @see CfetsQuotedCommonService
 * @since 1.0
 */
@Service("vueCfetsQuotedCommonService")
public class CfetsQuotedCommonService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(CfetsQuotedCommonService.class);

    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;
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

    @Autowired
    private CfetsBondDealQuotedPriceService cfetsBondDealQuotedPrice;

    @Autowired
    private com.joyintech.service.bankuplink.common.service.CfetsQuotedCommonService cfetsQuotedCommonServiceMicro;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private CfetsIbuQuoteBondEtrMapper cfetsIbuQuoteBondEtrMapper;

    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired
    private SppiImportDataMapper sppiImportDataMapper;

    @Autowired
    private IbuQuoteBondMapper ibuQuoteBondMapper;
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
        String flag = cfetsQuotedCommonServiceMicro.entrustGiveUpHandle(entrustId);
        /*String flag = restTemplate.getForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                 + "/CfetsQuotedCommon/entrustGiveUpHandle?entrustId="
                                                 + entrustId,
                                                 String.class);*/
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
        List<ValidMessage> errorList=new ArrayList<ValidMessage>();
        List<String> entrustIds=(List<String>)param.get("entrustIds");
        if(StringUtil.isNotEmpty(entrustIds)){
            for(String entrustId : entrustIds){
                String flag  = cfetsQuotedCommonServiceMicro.entrustGiveUpHandle(entrustId);
                if(StringUtil.equals("1", flag)){
                    errorList.add(new ValidMessage("已接收且完成度为0指令,才能执行放弃操作！"+"指令编号："+entrustId));
                }
                else if(StringUtil.equals("2", flag)){
                    errorList.add(new ValidMessage("存在流程中的报价，不可放弃！"+"指令编号："+entrustId));
                }
            }
        }
        return errorList;
		/*return restTemplate.postForList(
	    	ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/entrustGiveUpHandleList", param,ValidMessage.class);*/
	}


    /**
     * 获取下一个工作日
     * @param tradeDate
     * @return
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
                msg ="报价状态和发送状态分别为已新增/成功，已修改/成功的对话报价，才能执行撤回操作！";
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
                msg ="本方角色为做市方，报价状态和发送状态分别为已回复/成功，已修改/空的请求报价，才能执行撤回操作！";
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
                msg ="本方角色为非做市方，报价状态和发送状态分别为待回复/成功，已回复/空的请求报价，才能执行撤回操作！";
            }
            else {
            	msg ="报价状态和发送状态分别为已新增/成功，已修改/成功的对话报价，才能执行撤回操作！";
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
                msg ="报价状态和发送状态分别为已新增/成功，已修改/成功，已撤回/成功，已成交/空，已终止/空的对话报价，才能执行复制操作！";
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
                msg ="本方角色为非做市方，报价状态和发送状态分别为待回复/成功，已撤回/成功，已终止/空,已过期/空，已成交/成功的请求报价，才能执行复制操作！";
            }
            else {
            	msg ="报价状态和发送状态分别为已新增/成功，已修改/成功，已撤回/成功，已成交/空，已终止/空的对话报价，才能执行复制操作！";
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
                BussinessDictCodeList.报价方式_对话报价.getValue())) {
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
                BussinessDictCodeList.报价方式_请求报价.getValue())) {
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
                BussinessDictCodeList.报价方式_对话报价.getValue())) {
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
                BussinessDictCodeList.报价方式_请求报价.getValue())) {
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
                BussinessDictCodeList.报价方式_对话报价.getValue())) {
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
                BussinessDictCodeList.报价方式_请求报价.getValue())) {
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
                BussinessDictCodeList.报价方式_对话报价.getValue())) {
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
                msg ="报价状态和发送状态分别为已新增/成功，已修改/成功的对话报价，才能执行修改操作！";
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
     * 审批环节状态check
     * @param quoteId
     * @param tradeStatus
     * @return
     */
    public  boolean checkQuoteStatusChange(String quoteId,String tradeStatus) {
        boolean flag = false;
        IbuQuoteBond ibuQuoteBond = ibuQuoteBondMapper.selectByPrimaryKey(quoteId);
        if(null!=ibuQuoteBond &&!StringUtil.isEmpty(tradeStatus) && !StringUtil.equals(ibuQuoteBond.getTradeStatus(),tradeStatus)){
            return true;
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
        return cfetsQuotedCommonServiceMicro.selectErrlog(quoteId);
       /* return restTemplate.getForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                         + "/CfetsQuotedCommon/selectErrlog?quoteId=" + quoteId,
            String.class);*/
    }

    /**
     * 是否可以操作报价
     * @param param
     * @return
     */
    public boolean isOpeQuotedPrice(Map<String, Object> param) {
        param.put("loginUserId", LoginUserUtil.getUserName());
        return cfetsQuotedCommonServiceMicro.isOpeQuotedPrice(param);
        /*return restTemplate.postForObject(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/isOpeQuotedPrice",
                param, Boolean.class);*/
    }

    /**
     * 前台转后台
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    public CfetsBondDealDialogueQuoteDto createDialogue(CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
        preparCfetsBondDealQuoteCommon(cfetsBondDealQuoteCommonDto);
        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto,cfetsBondDealQuoteCommonDto);
        cfetsBondDealDialogueQuoteDto.setAvaNum(cfetsBondDealQuoteCommonDto.getShareAmt());
        cfetsBondDealDialogueQuoteDto.setSettleSpreed(cfetsBondDealQuoteCommonDto.getSettleSpreedCode());
        cfetsBondDealDialogueQuoteDto.setSettleCcy(cfetsBondDealQuoteCommonDto.getCcy());
        return cfetsBondDealDialogueQuoteDto;
    }

    /**
     * 后台转前台
     * @param cfetsBondDealDialogueQuoteDto
     * @return
     */
    public CfetsBondDealQuoteCommonDto createDialogue2(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto = JsonUtils.json2Object(JsonUtils.object2Json(cfetsBondDealDialogueQuoteDto),CfetsBondDealQuoteCommonDto.class);
        cfetsBondDealQuoteCommonDto.setShareAmt(ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getAvaNum(),new BigDecimal(10000)));
        cfetsBondDealQuoteCommonDto.setSettleSpreedCode(cfetsBondDealDialogueQuoteDto.getSettleSpreed());
        cfetsBondDealQuoteCommonDto.setCcy(cfetsBondDealDialogueQuoteDto.getSettleCcy());
        return cfetsBondDealQuoteCommonDto;
    }

    /***
     * 前台转后台
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    public CfetsBondDealRequestQuoteDto createReueust(CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        preparCfetsBondDealQuoteCommon(cfetsBondDealQuoteCommonDto);
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = JsonUtils.json2Object(JsonUtils.object2Json(cfetsBondDealQuoteCommonDto),CfetsBondDealRequestQuoteDto.class);
        cfetsBondDealRequestQuoteDto.setAvaNum(cfetsBondDealQuoteCommonDto.getShareAmt());
        cfetsBondDealRequestQuoteDto.setSettleSpreed(cfetsBondDealQuoteCommonDto.getSettleSpreedCode());
        cfetsBondDealRequestQuoteDto.setSettleCcy(cfetsBondDealQuoteCommonDto.getCcy());
        if(CollectionUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getCounterInfoList())){
            List<String> counterIdList = new ArrayList<>();
            List<String>  detIdList = new ArrayList<>();
            cfetsBondDealRequestQuoteDto.getCounterInfoList().forEach(item ->{
                counterIdList.add(item.getCounterId());
                detIdList.add(item.getDetId());
            });
            cfetsBondDealRequestQuoteDto.setCounterIdList(counterIdList);
            cfetsBondDealRequestQuoteDto.setDetIdList(detIdList);
            //非成交的时候 cfetsBondDealRequestQuoteDto.getCounterInfoList()清空
            if(!StringUtil.equals(cfetsBondDealQuoteCommonDto.getOperatorType(),"done")){
                cfetsBondDealRequestQuoteDto.setCounterInfoList(new ArrayList<>());
            }
        }
        return cfetsBondDealRequestQuoteDto;
    }

    /**
     * 后台转前台
     * @param cfetsBondDealRequestQuoteDto
     * @return
     */
    public CfetsBondDealQuoteCommonDto createReueust2(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto = JsonUtils.json2Object(JsonUtils.object2Json(cfetsBondDealRequestQuoteDto),CfetsBondDealQuoteCommonDto.class);
        cfetsBondDealQuoteCommonDto.setShareAmt(ArithmeticUtil.multiply(cfetsBondDealRequestQuoteDto.getAvaNum(),new BigDecimal(10000)));
        cfetsBondDealQuoteCommonDto.setSettleSpreedCode(cfetsBondDealRequestQuoteDto.getSettleSpreed());
        cfetsBondDealQuoteCommonDto.setCcy(cfetsBondDealRequestQuoteDto.getSettleCcy());
        return cfetsBondDealQuoteCommonDto;
    }


    /**
     * 对话组合分配列表  后台转前台
     * @param cfetsBondDealDialogueQuoteDtoList
     * @return
     */
    public List<PtlAllocationDto> createPtlAllocationList(List<CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuoteDtoList){
        List<PtlAllocationDto> result = new ArrayList<>();
        for (CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto : cfetsBondDealDialogueQuoteDtoList) {
            PtlAllocationDto ptlAllocationDto = new PtlAllocationDto();
            BeanUtil.copyPropertiesIgnoreNull(ptlAllocationDto,cfetsBondDealDialogueQuoteDto);
            ptlAllocationDto.setShareAmt(ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getAvaNum(),new BigDecimal(10000)));
            ptlAllocationDto.setAvaNum(ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getEntrustAvaNum(),new BigDecimal(10000)));
            result.add(ptlAllocationDto);
        }
        return result;
    }

    /**
     * 对话组合分配列表 前台转后台
     * @param ptlAllocationDtoList
     * @return
     */
    public List<CfetsBondDealDialogueQuoteDto> createCfetsBondDealDialogueQuoteDtoList(List<PtlAllocationDto> ptlAllocationDtoList){
        List<CfetsBondDealDialogueQuoteDto> result = new ArrayList<>();
        for (PtlAllocationDto ptlAllocationDto : ptlAllocationDtoList) {
            CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto,ptlAllocationDto);
            cfetsBondDealDialogueQuoteDto.setAvaNum(ptlAllocationDto.getShareAmt());
            cfetsBondDealDialogueQuoteDto.setEntrustAvaNum(ptlAllocationDto.getAvaNum());
            result.add(cfetsBondDealDialogueQuoteDto);
        }
        return result;
    }

    /**
     * 请求组合分配列表  后台转前台
     * @param cfetsBondDealRequestQuoteDtoList
     * @return
     */
    public List<PtlAllocationDto> createRequestPtlAllocationList(List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList){
        List<PtlAllocationDto> result = new ArrayList<>();
        for (CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto : cfetsBondDealRequestQuoteDtoList) {
            PtlAllocationDto ptlAllocationDto = new PtlAllocationDto();
            BeanUtil.copyPropertiesIgnoreNull(ptlAllocationDto,cfetsBondDealRequestQuoteDto);
            ptlAllocationDto.setShareAmt(ArithmeticUtil.multiply(cfetsBondDealRequestQuoteDto.getAvaNum(),new BigDecimal(10000)));
            ptlAllocationDto.setAvaNum(ArithmeticUtil.multiply(cfetsBondDealRequestQuoteDto.getEntrustAvaNum(),new BigDecimal(10000)));
            result.add(ptlAllocationDto);
        }
        return result;
    }

    /**
     * 请求组合分配列表 前台转后台
     * @param ptlAllocationDtoList
     * @return
     */
    public List<CfetsBondDealRequestQuoteDto> createCfetsBondDealRequestQuoteDtoList(List<PtlAllocationDto> ptlAllocationDtoList){
        List<CfetsBondDealRequestQuoteDto> result = new ArrayList<>();
        for (PtlAllocationDto ptlAllocationDto : ptlAllocationDtoList) {
            CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRequestQuoteDto,ptlAllocationDto);
            cfetsBondDealRequestQuoteDto.setAvaNum(ptlAllocationDto.getShareAmt());
            cfetsBondDealRequestQuoteDto.setEntrustAvaNum(ptlAllocationDto.getAvaNum());
            result.add(cfetsBondDealRequestQuoteDto);
        }
        return result;
    }

    /**
     * 前端元-万元  % 转换(对话、请求)
     * @param cfetsBondDealQuoteCommonDto
     */
    public void preparCfetsBondDealQuoteCommon(CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        //收益率*100
        cfetsBondDealQuoteCommonDto.setMatureYield(ArithmeticUtil.multiply(cfetsBondDealQuoteCommonDto.getMatureYield(),new BigDecimal(100)));
        cfetsBondDealQuoteCommonDto.setExerYield(ArithmeticUtil.multiply(cfetsBondDealQuoteCommonDto.getExerYield(),new BigDecimal(100)));
        //券面万元
        cfetsBondDealQuoteCommonDto.setShareAmt(ArithmeticUtil.divide(cfetsBondDealQuoteCommonDto.getShareAmt(),new BigDecimal(10000),2));
        //分配列表元转化万元
        if(CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
            cfetsBondDealQuoteCommonDto.getPtlAllocatios().stream().forEach(item ->{
                item.setAvaNum(ArithmeticUtil.divide(item.getAvaNum(),new BigDecimal(10000),2));
                item.setShareAmt(ArithmeticUtil.divide(item.getShareAmt(),new BigDecimal(10000),2));
                item.setOurEntNum(ArithmeticUtil.divide(item.getOurEntNum(),new BigDecimal(10000),2));
                item.setLendinEntNum(ArithmeticUtil.divide(item.getLendinEntNum(),new BigDecimal(10000),2));
                item.setSellAbleNum(ArithmeticUtil.divide(item.getSellAbleNum(),new BigDecimal(10000),2));
                item.setMatureYield(ArithmeticUtil.multiply(item.getMatureYield(),new BigDecimal(100)));
                item.setExerYield(ArithmeticUtil.multiply(item.getExerYield(),new BigDecimal(100)));
            });
        }
    }


    /**
     * 头寸check
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    public  AjaxResult  positionCheck(CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        String ps = cfetsBondDealQuoteCommonDto.getPs();
        try{
            // 提交时新头寸已校验,此处不需校验
//            if(StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())){
//                //按组合汇总全价总额
//                Map<String, BigDecimal> map = cfetsBondDealQuoteCommonDto.getPtlAllocatios().stream().collect(Collectors.groupingBy(PtlAllocationDto::getPortfolioId,
//                                                Collectors.mapping(PtlAllocationDto::getFpriceAmt,
//                                                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
//                for(String key : map.keySet()){
//                    BigDecimal fpriceAmt = map.get(key);
//
//                    Map<String,String> accnoParm = new HashMap<>();
//                    accnoParm.put("ccy",cfetsBondDealQuoteCommonDto.getCcy());
//                    accnoParm.put("portfolioId",key);
//                    accnoParm.put("acctType",DictCodeListExt1.资金账户类型_产品托管户.getValue());
//                    String accNo = cfetsIbuQuoteBondEtrMapper.getAccNo(accnoParm);
//                    if(StringUtil.isEmpty(accNo)){
//                        accNo = stlSecAcctInfoMapper.selectByPrimaryKey(cfetsBondDealQuoteCommonDto.getSecManageAcctId()).getTrdAcctId();
//                    }
//                    BaseMap baseMap = positionInfoService.queryTrdAvailableCash(Arrays.asList(key), DateUtil.format(cfetsBondDealQuoteCommonDto.getTradeDate()), accNo, cfetsBondDealQuoteCommonDto.getCcy(), "03", Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()), null);
//                    if(null!=baseMap&&fpriceAmt.compareTo((BigDecimal) baseMap.get("cashT"))>0){
//                        errorList.add(new ValidMessage(StringUtil.join("组合可用现金不足,组合代码:[",key,"]")));
//                        ajaxResult.setErrorList(errorList);
//                        break;
//                    }
//                }
//            }
            if(StringUtil.equals(ps,DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(ps,DictCodeList.交易方向_行权_回售.getValue())){
                //按组合汇总券面总额
                Map<String, BigDecimal> map = cfetsBondDealQuoteCommonDto.getPtlAllocatios().stream().collect(Collectors.groupingBy(PtlAllocationDto::getPortfolioId,
                        Collectors.mapping(PtlAllocationDto::getShareAmt,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
                for(String key : map.keySet()){
                    BigDecimal shareAmt = map.get(key);
                    BaseMap baseMap = positionInfoService.queryTrdPosition(Arrays.asList(key), DateUtil.format(cfetsBondDealQuoteCommonDto.getTradeDate()), cfetsBondDealQuoteCommonDto.getFinprodId());
                    if(null!=baseMap&&shareAmt.compareTo((BigDecimal) baseMap.get("ZCTRKYCC"))>0){
                        errorList.add(new ValidMessage(StringUtil.join("组合资产可用持仓不足,组合代码:[",key,"]")));
                        ajaxResult.setErrorList(errorList);
                        break;
                    }
                }
            }
        }catch (Exception e){
            logger.error("头寸验证异常:"+e.getMessage());
        }
        return ajaxResult;
    }

    /**
     * 获取指令流程中占用的券面总额
     * @param entrustId
     * @return
     */
    public BigDecimal getWflShareAmtByEntrustId(String entrustId){
        return cfetsIbuQuoteBondMapper.getWflShareAmtByEntrustId(entrustId);
    }

    /**
     * 获取指令流程中占用的券面总额
     * @param entrustId
     * @return
     */
    public BigDecimal getWflShareAmtByEntrustId(String entrustId,String totleTradeId){
        return cfetsIbuQuoteBondMapper.getWflShareAmtByEntrustIdAndWorkFlowId(entrustId,totleTradeId);
    }

    public  AjaxResult reclasCheck(CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if(StringUtil.equals(cfetsBondDealQuoteCommonDto.getPs(), DictCodeList.交易方向_买入.getValue())&&CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
            //资产是否有赎回或者行权计划
            //是否不满足使权不重大购入价条件
            Map<String, Object> entrustReclasMap = isEntrustReclas(cfetsBondDealQuoteCommonDto.getFinprodId(), cfetsBondDealQuoteCommonDto.getPrice(), cfetsBondDealQuoteCommonDto.getTradeDate());
            if((boolean)entrustReclasMap.get("isEntrustReclas")){
                cfetsBondDealQuoteCommonDto.getPtlAllocatios().forEach(item ->{
                    if(StringUtil.equals(item.getBusinessModel(), DictCodeListExt3.业务模式_收取合同约定的现金流.getValue())){
                        BaseMap baseMap = positionInfoService.queryTrdPosition(Arrays.asList(item.getPortfolioId()), DateUtil.format(cfetsBondDealQuoteCommonDto.getTradeDate()), cfetsBondDealQuoteCommonDto.getFinprodId());
                        //
                        if(null!=baseMap&&baseMap.containsKey("ZCTRKYCC")&&((BigDecimal)baseMap.get("ZCTRKYCC")).compareTo(BigDecimal.ZERO)>0){
                            errorList.add(new ValidMessage(StringUtil.join("交易净价不满足使权不重大的购入条件:",entrustReclasMap.get("price"),"业务模式由收取合同现金流调整为出售,请资产重分类!")));
                        }else{
                            errorList.add(new ValidMessage(StringUtil.join("交易净价不满足使权不重大的购入条件:",entrustReclasMap.get("price"),"业务模式由收取合同现金流调整为出售!")));
                        }
                    }
                });
            }
        }
        ajaxResult.setErrorList(errorList);
        ajaxResult.setStatuCode(220);
        return ajaxResult;
    }
    /**
     * 是否重分类
     * @param finprodId
     * @param price
     * @return
     */
    public Map<String,Object> isEntrustReclas(String finprodId,BigDecimal price,Date sysDate){
        Map<String,Object> result = new HashMap<>();
        result.put("isEntrustReclas",false);
        SppiImportData sppiImportData = sppiImportDataMapper.selectByPrimaryKey(finprodId);
        try{
            if(null!=sppiImportData&&StringUtil.isNotEmpty(sppiImportData.getBidPriceCon())){
                String[] split = sppiImportData.getBidPriceCon().split(",");
                Map<String,Object> param = new HashMap<>();
                param.put("finprodId",finprodId);
                param.put("sysDate",sysDate);
                if(price.compareTo(new BigDecimal(split[1]))<=0&&cfetsIbuQuoteBondMapper.countBondHasEexrPlan(param)>0){
                    result.put("price",new BigDecimal(split[1]));
                    result.put("isEntrustReclas",true);
                    //需要资产重分类
                    return result;
                }
            }
        }catch (Exception e){
            logger.error("资产重分类判断异常:"+e.getMessage());
        }
        return  result;
    }

    /**
     * check 锚定值  净价 到期收益率  行权收益率
     * @param entity
     * @return
     */
    public AjaxResult checkcheckAnchoring(CfetsBondDealQuoteCommonDto entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        String ps = entity.getPs();
        //以选择的锚定值check
        String anchoringValue = entity.getCalculateField();
        List<PtlAllocationDto> ptlAllocatios = entity.getPtlAllocatios();
        entity.setPrice(ArithmeticUtil.round(entity.getPrice(), 4, RoundingMode.HALF_UP));
        entity.setMatureYield(ArithmeticUtil.round(entity.getMatureYield(), 4, RoundingMode.HALF_UP));
        entity.setExerYield(ArithmeticUtil.round(entity.getExerYield(), 4, RoundingMode.HALF_UP));
        for (PtlAllocationDto ptlAllocationDto : ptlAllocatios) {
            ptlAllocationDto.setPrice(ArithmeticUtil.round(ptlAllocationDto.getPrice(), 4, RoundingMode.HALF_UP));
            ptlAllocationDto.setMatureYield(ArithmeticUtil.round(ptlAllocationDto.getMatureYield(), 4, RoundingMode.HALF_UP));
            ptlAllocationDto.setExerYield(ArithmeticUtil.round(ptlAllocationDto.getExerYield(), 4, RoundingMode.HALF_UP));
            String priceModel = ptlAllocationDto.getPriceModel();
            if(StringUtil.equals(ps,DictCodeList.交易方向_买入.getValue())){
                if(StringUtil.equals(anchoringValue,"01")){
                    if(StringUtil.equals(priceModel, com.joyintech.background.component.order.exchange.common.constant.DictCodeList.价格模式_指定价格.getValue())
                            &&entity.getPrice().compareTo(ptlAllocationDto.getPrice())!=0){
                        errorList.add(new ValidMessage("存在指定价格指令，需按指定价格进行交易!指令代码:"+ptlAllocationDto.getEntrustId()));
                    }
                    if(StringUtil.equals(priceModel, com.joyintech.background.component.order.exchange.common.constant.DictCodeList.价格模式_限价指令.getValue())
                            &&entity.getPrice().compareTo(ptlAllocationDto.getPrice())>0){
                        errorList.add(new ValidMessage("净价不能大于指令净价!指令代码:"+ptlAllocationDto.getEntrustId()));
                    }
                }
                if(StringUtil.equals(anchoringValue,"02")&&entity.getMatureYield().compareTo(ptlAllocationDto.getMatureYield())<0){
                    errorList.add(new ValidMessage("到期收益率不能小于指令到期收益率！"));
                }
                if(StringUtil.equals(anchoringValue,"03")&&entity.getExerYield().compareTo(ptlAllocationDto.getExerYield())<0){
                    errorList.add(new ValidMessage("行权收益率不能小于指令行权收益率！"));
                }
            }
            if(StringUtil.equals(ps,DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(ps,DictCodeList.交易方向_行权_回售.getValue())){
                if(StringUtil.equals(anchoringValue,"01")){
                    if(StringUtil.equals(priceModel, com.joyintech.background.component.order.exchange.common.constant.DictCodeList.价格模式_指定价格.getValue())
                            &&entity.getPrice().compareTo(ptlAllocationDto.getPrice())!=0){
                        errorList.add(new ValidMessage("存在指定价格指令，需按指定价格进行交易!指令代码:"+ptlAllocationDto.getEntrustId()));
                    }
                    if(StringUtil.equals(priceModel, com.joyintech.background.component.order.exchange.common.constant.DictCodeList.价格模式_限价指令.getValue())
                            &&entity.getPrice().compareTo(ptlAllocationDto.getPrice())<0){
                        errorList.add(new ValidMessage("净价不能小于指令净价!指令代码:"+ptlAllocationDto.getEntrustId()));
                    }
                }
                if(StringUtil.equals(anchoringValue,"02")&&entity.getMatureYield().compareTo(ptlAllocationDto.getMatureYield())>0){
                    errorList.add(new ValidMessage("到期收益率不能大于指令到期收益率！"));
                }
                if(StringUtil.equals(anchoringValue,"03")&&entity.getExerYield().compareTo(ptlAllocationDto.getExerYield())>0){
                    errorList.add(new ValidMessage("行权收益率不能大于指令行权收益率！"));
                }
            }
            if(CollectionUtils.isNotEmpty(errorList)){
                ajaxResult.setErrorList(errorList);
                return ajaxResult;
            }
        }
        return ajaxResult;
    }

    /**
     * 交易类型按钮点击检查
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    public AjaxResult radioPartyIdCheck(CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if(StringUtil.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())&&StringUtil.equals(cfetsBondDealQuoteCommonDto.getTradeMarket(),DictCodeListExt1.交易场所_银行间.getValue())){
            cfetsBondDealQuoteCommonDto.getPtlAllocatios().forEach(item ->{
                AjaxResult ar = checkpartyId(item.getEntrustId(), item.getPortfolioId(), cfetsBondDealQuoteCommonDto.getQuoteType());
                if(StringUtil.isNotEmpty(ar.getMessage())){
                    errorList.add(new ValidMessage(ar.getMessage()));
                }
            });
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 检查是否存在未关联交易账户
     * @param entrustId
     * @param portfolioId
     * @param quoteType
     * @return
     */
    public AjaxResult checkpartyId(String entrustId,String  portfolioId,String quoteType) {
        AjaxResult ajaxResult = new AjaxResult();
        String exportShow = CacheUtil.getSysParam("EXPORT_SHOW");
        //防止用户不刷新页面  去绑定账户   查一次数据库
        String partyId = cfetsIbuQuoteBondMapper.getPartyIdByPtl(portfolioId);
        if (((StringUtil.equals(exportShow, "01") && StringUtil.equals(quoteType, DictCodeListExt3.报价系统区分类型_现券登记报价.getValue()))
                || (StringUtil.equals(exportShow, "01") && StringUtil.equals(quoteType, DictCodeListExt3.报价系统区分类型_质押式回购登记报价.getValue()))
                || (StringUtil.equals(exportShow, "02") && (StringUtil.equals(quoteType, DictCodeListExt3.报价系统区分类型_现券对话报价.getValue())
                || StringUtil.equals(quoteType, DictCodeListExt3.报价系统区分类型_现券请求报价.getValue())
                || StringUtil.equals(quoteType, DictCodeListExt3.报价系统区分类型_质押式回购对话报价.getValue()))))
                && StringUtil.isEmpty(partyId)) {
            ajaxResult.setMessage(StringUtil.join("指令", entrustId, "下组合未关联交易账户，请至[银行间业务参数管理-CFETS关联账户]中维护"));
        }
        return ajaxResult;
    }


    /**
     * 以执行为维度  统计可用的本方券和融入券
     * @param param
     * @return
     */
    public   List<EntrustAvaShareAmtDto> listAvaShareAmt(Map<String,Object> param){
        List<String> quoteStatusIbu = Arrays.asList(com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_新增中.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已新增.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_修改中.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已修改.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_撤回中.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已成交.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_待回复.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_成交中.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_终止中.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已回复.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_拒绝中.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_回复中.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_交谈中.getValue()
        ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已交谈.getValue()
         ,com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已交谈.getValue());
        param.put("QUOTE_STUTAS_IBUS",quoteStatusIbu);
        return cfetsIbuQuoteBondEtrMapper.listAvaShareAmt(param);
    }

    /**
     * 处理本方券 融入券  卖空购回
     * @param ptlAllocatios
     * @param workflowId
     * @param ps
     */
    public Map<String,PtlAllocationDto> handOurAndLendinShareAmt(List<PtlAllocationDto> ptlAllocatios,String workflowId,String ps,String quoteId){
        Map<String, PtlAllocationDto> result  = new HashMap<>();
        if(CollectionUtils.isNotEmpty(ptlAllocatios)){
            //模式
            String sellPara = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
            //设置一：买入先填充融入券指令额度；卖出先扣除本方券指令额度
            //设置二：按指令上券面总额、本方券券面总额、融入券券面总额的比例计算交易上券面总额、本方券券面总额、融入券券面总额
            String sellParaAddRule = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED_ADDRULE");
            //处理本方券、融入券
            Map<String,Object> param = new HashMap<>();
            List<String> entrustIds = new ArrayList<>();
            ptlAllocatios.forEach(item ->{entrustIds.add(item.getEntrustId());});
            param.put("entrustIds",entrustIds);
            param.put("workflowId",workflowId);
            //去除当前该笔报价的占用额度
            param.put("quoteId",quoteId);
            List<EntrustAvaShareAmtDto> entrustAvaShareAmtDtos = cfetsQuotedCommonService.listAvaShareAmt(param);
            Map<String, List<EntrustAvaShareAmtDto>> entrustAvaShareAmtGroup = entrustAvaShareAmtDtos.stream().collect(Collectors.groupingBy(EntrustAvaShareAmtDto::getEntrustId));
            for (PtlAllocationDto ptlAllocatio : ptlAllocatios){
                EntrustAvaShareAmtDto entrustAvaShareAmtDto = entrustAvaShareAmtGroup.get(ptlAllocatio.getEntrustId()).get(0);
                if(null!=entrustAvaShareAmtDto){
                    BigDecimal shareAmt = ptlAllocatio.getShareAmt();
                    BigDecimal ourEntNum = entrustAvaShareAmtDto.getOurEntNum();
                    BigDecimal lendinEntNum = entrustAvaShareAmtDto.getLendinEntNum();
                    BigDecimal avaOurShareAmt = ArithmeticUtil.subtract(ourEntNum, entrustAvaShareAmtDto.getUsedOurShareAmt());
                    BigDecimal avaLendinShareAmt = ArithmeticUtil.subtract(lendinEntNum, entrustAvaShareAmtDto.getUsedLendinShareAmt());
                    if(StringUtil.equals(sellPara,"01")||(StringUtil.equals(sellPara,"02")&&StringUtil.equals(sellParaAddRule,"01"))){
                        if(StringUtil.equals(ps,DictCodeList.交易方向_买入.getValue())){
                            if(shareAmt.compareTo(avaLendinShareAmt)>=0){
                                ptlAllocatio.setLendinShareAmt(avaLendinShareAmt);
                                ptlAllocatio.setOurShareAmt(ArithmeticUtil.subtract(shareAmt,avaLendinShareAmt));
                            }else{
                                ptlAllocatio.setLendinShareAmt(shareAmt);
                                ptlAllocatio.setOurShareAmt(BigDecimal.ZERO);
                            }
                        }
                        if(StringUtil.equals(ps,DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(ps,DictCodeList.交易方向_行权_回售.getValue())){
                            if(shareAmt.compareTo(avaOurShareAmt)>=0){
                                ptlAllocatio.setOurShareAmt(avaOurShareAmt);
                                ptlAllocatio.setLendinShareAmt(ArithmeticUtil.subtract(shareAmt,avaOurShareAmt));
                            }else{
                                ptlAllocatio.setOurShareAmt(shareAmt);
                                ptlAllocatio.setLendinShareAmt(BigDecimal.ZERO);
                            }
                        }
                    }
                    if(StringUtil.equals(sellPara,"02")&&StringUtil.equals(sellParaAddRule,"02")){
                        //比例计算
                        if(ourEntNum.compareTo(BigDecimal.ZERO)==0){ptlAllocatio.setOurShareAmt(BigDecimal.ZERO);ptlAllocatio.setLendinShareAmt(shareAmt);}
                        if(lendinEntNum.compareTo(BigDecimal.ZERO)==0){ptlAllocatio.setOurShareAmt(shareAmt);ptlAllocatio.setLendinShareAmt(BigDecimal.ZERO);}
                        if(ourEntNum.compareTo(BigDecimal.ZERO)!=0&&lendinEntNum.compareTo(BigDecimal.ZERO)!=0){
                            //递增维度(万元)
                            BigDecimal faceDimension = new BigDecimal("0.1");
                            BigDecimal total = ArithmeticUtil.doAdd(ourEntNum, lendinEntNum);
                            //万元计算  舌位到千位
                            BigDecimal shareAmt2 = ArithmeticUtil.divide(shareAmt, new BigDecimal(10000),1,RoundingMode.DOWN);
                            BigDecimal tailDifference = shareAmt2;
                            BigDecimal ourEntNumdivide = ArithmeticUtil.divide(ArithmeticUtil.multiply(ourEntNum, shareAmt2), total, 1, RoundingMode.DOWN);
                            BigDecimal lendinEntNumdivide = ArithmeticUtil.divide(ArithmeticUtil.multiply(lendinEntNum, shareAmt2), total, 1, RoundingMode.DOWN);
                            if(avaOurShareAmt.compareTo(ourEntNumdivide)>=0){
                                ptlAllocatio.setOurShareAmt(ourEntNumdivide);
                                avaOurShareAmt = ArithmeticUtil.subtract(avaOurShareAmt,ourEntNumdivide);
                            }else{
                                avaOurShareAmt = BigDecimal.ZERO;
                                ptlAllocatio.setOurShareAmt(avaOurShareAmt);
                            }
                            if(avaLendinShareAmt.compareTo(lendinEntNumdivide)>=0){
                                ptlAllocatio.setLendinShareAmt(lendinEntNumdivide);
                                avaLendinShareAmt = ArithmeticUtil.subtract(avaLendinShareAmt,lendinEntNumdivide);
                            }else{
                                avaLendinShareAmt = BigDecimal.ZERO;
                                ptlAllocatio.setLendinShareAmt(avaLendinShareAmt);
                            }
                            tailDifference = ArithmeticUtil.subtract(tailDifference,ptlAllocatio.getOurShareAmt());tailDifference = ArithmeticUtil.subtract(tailDifference,ptlAllocatio.getLendinShareAmt());
                            //尾差处理
                            while(tailDifference.compareTo(BigDecimal.ZERO)>0){
                                if(StringUtil.equals(ps,DictCodeList.交易方向_买入.getValue())){
                                    if(tailDifference.compareTo(BigDecimal.ZERO)>0&&avaLendinShareAmt.compareTo(BigDecimal.ZERO)>0){
                                        tailDifference = ArithmeticUtil.subtract(tailDifference,faceDimension);
                                        ptlAllocatio.setLendinShareAmt(ArithmeticUtil.doAdd(ptlAllocatio.getLendinShareAmt(),faceDimension));
                                    }
                                    if(tailDifference.compareTo(BigDecimal.ZERO)>0&&avaOurShareAmt.compareTo(BigDecimal.ZERO)>0){
                                        tailDifference = ArithmeticUtil.subtract(tailDifference,faceDimension);
                                        ptlAllocatio.setOurShareAmt(ArithmeticUtil.doAdd(ptlAllocatio.getOurShareAmt(),faceDimension));
                                    }
                                }
                                else{
                                    if(tailDifference.compareTo(BigDecimal.ZERO)>0&&avaOurShareAmt.compareTo(BigDecimal.ZERO)>0){
                                        tailDifference = ArithmeticUtil.subtract(tailDifference,faceDimension);
                                        ptlAllocatio.setOurShareAmt(ArithmeticUtil.doAdd(ptlAllocatio.getOurShareAmt(),faceDimension));
                                    }
                                    if(tailDifference.compareTo(BigDecimal.ZERO)>0&&avaLendinShareAmt.compareTo(BigDecimal.ZERO)>0){
                                        tailDifference = ArithmeticUtil.subtract(tailDifference,faceDimension);
                                        ptlAllocatio.setLendinShareAmt(ArithmeticUtil.doAdd(ptlAllocatio.getLendinShareAmt(),faceDimension));
                                    }
                                }

                            }
                            ptlAllocatio.setOurShareAmt(ArithmeticUtil.multiply(ptlAllocatio.getOurShareAmt(),new BigDecimal(10000)));
                            ptlAllocatio.setLendinShareAmt(ArithmeticUtil.multiply(ptlAllocatio.getLendinShareAmt(),new BigDecimal(10000)));
                        }
                    }
                    if(StringUtil.equals(sellPara,"03")){
                        ptlAllocatio.setOurShareAmt(shareAmt);
                        ptlAllocatio.setLendinShareAmt(BigDecimal.ZERO);
                    }
                }
                result.put(ptlAllocatio.getEntrustId(),ptlAllocatio);
            }
        }
        return result;
    }

    /**
     * 获取发行人名称
     * @param finprodId
     * @return
     */
    public String getIssuserName(String finprodId){
        return cfetsIbuQuoteBondMapper.getIssuserName(finprodId);
    }

}

/*
 * 文件名：TrdFxswapInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：renping
 * 修改时间：2019年6月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.trdfxswapinvesttrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.trade.common.controller.TrdCommonController;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.trdfxswapinvesttrade.dto.TrdFxswapInvestTrade001Dto;
import com.joyintech.tams.invest.trade.trdfxswapinvesttrade.service.TrdFxswapInvestTrade002Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 外汇掉期交易新建
 * 
 * @author renping
 * @version 1.0 2019年6月25日
 * @see TrdFxswapInvestTrade001Controller
 * @since 1.0
 */
@Controller
@PageId("TrdFxswapInvestTrade002")
@RequestMapping("/invest/trade/trdfxswapinvesttrade/TrdFxswapInvestTrade002")
public class TrdFxswapInvestTrade002Controller extends BaseController {

    @Autowired
    private TradeCommonService tradeCommonService;

    /**
     * 注入业务主键生产类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 划款指令
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * 注入外汇掉期交易Service
     */
    @Autowired
    private TrdFxswapInvestTrade002Service trdFxswapInvestTrade002Service;

    /**
     * 通用Mapper共同代理方法
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired(required = false)
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;
    
    @Autowired
    private TrdCommonController trdCommonController;

    /**
     * 初始化方法 {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.交易流程状态, DictCode.币种,
            DictCode.外汇掉期期限类型, DictCode.货币对, DictCode.外汇期限, DictCode.外汇交易类型, DictCode.我方角色,
            DictCode.外汇交易模式, DictCode.结算方式, DictCode.是否, DictCode.持有目的);
        model.addAttribute("FINPROD_TYPE_F13", DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        model.addAttribute("PS_BUY_SELL", DictCodeListExt2.交易方向_买卖.getValue());
        model.addAttribute("CNY_CCY", DictCodeList.币种_人民币_CNY.getValue());
        model.addAttribute("USD_CCY", DictCodeList.币种_美元_USD.getValue());
        model.addAttribute("JPY_CNY_CCY_PAIR", DictCodeList.货币对_JPY_CNY.getValue());
        model.addAttribute("OVERNIGHT_EXC_FXS_TERM_TYPE",
            DictCodeListExt2.外汇期限类型_Overnight.getValue());
        model.addAttribute("SPOT_EXC_FXS_TERM_TYPE", DictCodeListExt2.外汇期限类型_Spot.getValue());
        model.addAttribute("TOM_EXC_FXS_TERM_TYPE", DictCodeListExt2.外汇期限类型_Tom.getValue());
        model.addAttribute("SPOT_FXA_EXC_FXS_TERM_TYPE",
            DictCodeListExt2.外汇期限类型_即期对远期掉期.getValue());
        model.addAttribute("FXA_FXA_EXC_FXS_TERM_TYPE",
            DictCodeListExt2.外汇期限类型_远期对远期掉期.getValue());
        model.addAttribute("TODAY_TERM_TYPE_EXC", DictCodeListExt2.外汇期限_TODAY.getValue());
        model.addAttribute("TOM_TERM_TYPE_EXC", DictCodeListExt2.外汇期限_TOM.getValue());
        model.addAttribute("SPOT_TERM_TYPE_EXC", DictCodeListExt2.外汇期限_SPOT.getValue());
        model.addAttribute("ONE_DAY_TERM_TYPE_EXC", DictCodeListExt2.外汇期限_1D.getValue());
        model.addAttribute("ONE_WEEK_TERM_TYPE_EXC", DictCodeListExt2.外汇期限_1W.getValue());
        model.addAttribute("FGN_CY_EXC_DEAL_TYPE", DictCodeListExt2.外汇交易类型_外币对.getValue());
        model.addAttribute("OTC_DEAL_MODE", DictCodeListExt2.外汇交易模式_询价交易.getValue());
        model.addAttribute("BANK_EXCH_DEAL_MODE", DictCodeListExt2.外汇交易模式_本行结售汇.getValue());
        model.addAttribute("BIL_SETT_SETT_MODE", DictCodeListExt2.结算方式_双边全额清算.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("COUNTER_TYPE_01", DictCodeListExt1.交易对手类型_市场机构.getValue());
        //----add by jjpei 20190718--------交易监控风险试算-----------start--------
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("USE_STATUS_01", DictCodeListExt1.使用状态_启用.getValue());
        //----add by jjpei 20190718--------交易监控风险试算-----------end--------
        String entrustId = getParam("entrustId");
        model.addAttribute("businessId", entrustId);
        String tradeId = getParam("tradeId");
        String tradeStatus = getParam("tradeStatus");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);

        TrdFxswapInvestTrade001Dto entity = new TrdFxswapInvestTrade001Dto();
        // 工作流查看时
        if (StringUtil.equals(tradeStatus, DictCodeListExt1.交易流程状态_流程中.getValue())
            || !WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            entity = trdFxswapInvestTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("trdInvestPersonInfo002Dto",
                JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
            model.addAttribute("riskTrialRetDto",
                JsonUtils.object2Json(entity.getRiskTrialRetDto()));
            model.addAttribute("entityList",
                JsonUtils.object2Json(entity.getBussTrdTransferOrder()));
        }
        // 工作流查看以外且交易编号非空时
        else if (StringUtil.isNotEmpty(tradeId)) {
            // 查询画面双击查看或修改流程中以外的数据时
            entity = trdFxswapInvestTrade002Service.getDealInfo(tradeId, tradeStatus);
            // 人员信息
            TrdInvestPersonInfo002Dto trdInvestPersonInfo002Dto = new TrdInvestPersonInfo002Dto();
            trdInvestPersonInfo002Dto.setTrader(entity.getTrader());
            trdInvestPersonInfo002Dto.setCounterId(entity.getCounterId());
            trdInvestPersonInfo002Dto.setContactInfo(entity.getCounterContact());
            trdInvestPersonInfo002Dto.setCounterTrader(entity.getCounterTrader());
            entity.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
            if(StringUtil.isEmpty(entity.getTradeId1())){
                entity.setTradeId1(entity.getTradeId());
            } else {
                entity.setTradeId1(entity.getTradeId() + "," + entity.getTradeId1());
            }
            // 指令信息
            model.addAttribute("entity", entity);
            model.addAttribute("trdInvestPersonInfo002Dto",
                JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
            model.addAttribute("tradeId", tradeId);
        }
        // 有委托代码新建模式打开时
        else if (entrustId != null) {
            entity = trdFxswapInvestTrade002Service.getDealInfoByEntrustId(entrustId);
            if (entity != null) {
                String trader = "";
                if (bussCommonMapperDelegateService != null) {
                    trader = trdFxswapInvestTrade002Service.getTrader(entrustId);
                }
                TrdInvestPersonInfo002Dto trdInvestPersonInfo002Dto = new TrdInvestPersonInfo002Dto();
                if (trader != null && !"".equals(trader)) {
                    trdInvestPersonInfo002Dto.setTrader(trader);
                }
                trdInvestPersonInfo002Dto.setCounterId(entity.getCounterId());
                trdInvestPersonInfo002Dto.setCounterTrader(entity.getCounterTrader());
                trdInvestPersonInfo002Dto.setContactInfo(entity.getCounterContact());
                entity.setEntrustId(entrustId);
                entity.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
                model.addAttribute("trdInvestPersonInfo002Dto",
                    JsonUtils.object2Json(entity.getTrdInvestPersonInfo002Dto()));
                entity.setTradeDate(iPlatformDate.getSysDate());
            }
            model.addAttribute("entity", entity);
        }
        else {
            entity.setTradeDate(iPlatformDate.getSysDate());
            model.addAttribute("entity", entity);
        }
        if (entity.getTradeId() == null) {
            entity.setTradeId("");
        }
        //add by lizhi 是否限制划款指令开关 2020年2月26日  start
        model.addAttribute("GEN_PAY_ORDERS_AUTO",CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  end
        model.addAttribute("PINAN_ASSET_TYPE_F15",DictCodeListExt2.平安资产类型_外汇掉期.getValue());
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        return super.init(model);
    }

    /**
     * 交易日
     * 
     * @return 交易日
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getInitDate")
    public String getInitDate() {
        return iPlatformDate.getSysDateStr();
    }

    /**
     * 获取持仓、可用现金等
     * 
     * @return 金额集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("available")
    public List<SuggestDto> available() {
        Map<String, Object> map = RequestUtil.getPageParam();
        List<SuggestDto> list = trdFxswapInvestTrade002Service.available(map);
        return list;
    }

    /**
     * 划款指令生成实体类
     * 
     * @param dto 外汇掉期投资交易实体类
     * @return 金融产品交易实体类
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntity")
    public List<TrdProductDeal> getEntity() {
        Map<String, Object> map = RequestUtil.getPageParam();
        TrdFxswapInvestTrade001Dto dto = BeanUtil.mapToObject(map,
            TrdFxswapInvestTrade001Dto.class);
        TrdInvestPersonInfo002Dto trdInvestPersonInfo002Dto = new TrdInvestPersonInfo002Dto();
        dto.setTrdInvestPersonInfo002Dto(trdInvestPersonInfo002Dto);
        dto.getTrdInvestPersonInfo002Dto().setContactInfo(
            map.get("trdInvestPersonInfo002Dto[contactInfo]").toString());
        dto.getTrdInvestPersonInfo002Dto().setCounterId(
            map.get("trdInvestPersonInfo002Dto[counterId]").toString());
        dto.getTrdInvestPersonInfo002Dto().setCounterTrader(
            map.get("trdInvestPersonInfo002Dto[counterTrader]").toString());
        dto.getTrdInvestPersonInfo002Dto().setIsOutTrade(
            map.get("trdInvestPersonInfo002Dto[isOutTrade]").toString());
        dto.getTrdInvestPersonInfo002Dto().setTrader(
            map.get("trdInvestPersonInfo002Dto[trader]").toString());
        return trdFxswapInvestTrade002Service.getEntity(dto);
    }

    /**
     * 画面点击保存按钮时check
     * 
     * @param dto 实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) TrdFxswapInvestTrade001Dto dto, BindingResult result) {
    	
    	if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        // 指令接收交易录单验证数据已存在流程中
       /* if (StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()) {
    		String str = tradeCommonService.check(dto.getEntrustId(), this.getPageId());
    		if(StringUtil.isNotEmpty(str)){
    			AjaxResult res= new AjaxResult();
				res.setMessage(str, MessageType.错误消息);
    			return res;
    		}
        	// 查询交易流程中录入的总金额	王贵奇	2019/9/3
        	BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(dto.getEntrustId());
            BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(dto.getEntrustId(), dto.getPortfolioId());
            BigDecimal finAmt =StringUtil.isNotEmpty(dto.getPrinAmt())?dto.getPrinAmt():StringUtil.isNotEmpty(dto.getShareAmt())?dto.getShareAmt():dto.getTradeAmt();
            if(entrustNum.compareTo(wflPrinAmt.add(finAmt)) == -1) {
            	AjaxResult res= new AjaxResult();
            	 List<ValidMessage> errorList = new ArrayList<>();
            	 ValidMessage validMessage = new ValidMessage("prinAmt",
            			 "可用委托金额不足,当前最多可委托金额为:"+ entrustNum.subtract(wflPrinAmt));
                     errorList.add(validMessage);
                res.setErrorList(errorList);
                res.setMessageType(MessageType.错误消息);
                return res;
            }
        }*/
        // add by wangguoxian 修复YUCHUBS-98投资端-外汇远掉期委托及交易，买入外汇时判断持仓提示错误 start
        // 货币对
        String curPair = dto.getCurPair();
        // 交易货币
        String dealCur = dto.getDealCur();
        // 基准货币金额
        BigDecimal baseCurAmt = null;
        // 基准货币可用持仓
        BigDecimal baseCurPos = null;
        // 非基准货币金额
        BigDecimal noBaseCurAmt = null;
        // 非基准货币可用持仓
        BigDecimal noBaseCurPos = null;

        if (StringUtil.isNotEmpty(curPair) && curPair.indexOf(dealCur) == 0){
            // 交易货币是基准货币
            baseCurAmt = dto.getnDealCurAmt();
            baseCurPos = dto.getDealCurPos();
            // 对应货币是非基准货币
            noBaseCurAmt = dto.getnCorCurAmt();
            noBaseCurPos = dto.getCorCurPos();

        }
        else if (StringUtil.isNotEmpty(curPair) && curPair.indexOf(dealCur) > 0){
            // 交易货币是非基准货币
            noBaseCurAmt = dto.getnDealCurAmt();
            noBaseCurPos = dto.getDealCurPos();
            // 对应货币是基准货币
            baseCurAmt = dto.getnCorCurAmt();
            baseCurPos = dto.getCorCurPos();
        }

        // 当交易方向为买入时，非基准货币对应的金额不大于可用持仓；否则提交时弹框提醒但不禁止提交
        if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_买卖.getValue())) {
            if (noBaseCurAmt != null && noBaseCurPos != null && noBaseCurAmt.compareTo(noBaseCurPos) == 1) {
                AjaxResult ar = new AjaxResult();
                ar.setData(
                        this.getMessage("TRD_SPOTEX_002", this.getMessage("TrdFxswapInvestTrade002.t01"),
                                this.getMessage("TrdFxswapInvestTrade002.noBaseCur")));
                ar.setMessageType(MessageType.确认消息);
                return ar;
            }
        }

        // 当交易方向为卖出时，基准货币对应的金额不大于可用持仓；否则提交时弹框提醒但不禁止提交
        if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_卖买.getValue())) {
            if (baseCurAmt != null && baseCurPos != null && baseCurAmt.compareTo(baseCurPos) == 1) {
                AjaxResult ar = new AjaxResult();
                ar.setData(
                        this.getMessage("TRD_SPOTEX_002", this.getMessage("TrdFxswapInvestTrade002.t02"),
                                this.getMessage("TrdFxswapInvestTrade002.baseCur")));
                ar.setMessageType(MessageType.确认消息);
                return ar;
            }
        }
        // add by wangguoxian 修复YUCHUBS-98投资端-外汇远掉期委托及交易，买入外汇时判断持仓提示错误 end
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 合成外汇掉期投资交易实体类
     * 
     * @param dto 交易对手实体类
     * @param portfolioId 组合id
     * @return 投资交易实体类
     * @see
     * @since 1.0
     */
    public TrdFxswapInvestTrade001Dto createEntity(TrdFxswapInvestTrade001Dto dto,
                                                   String portfolioId, TrdProductDeal dealDto) {
        dto.getTrdInvestPersonInfo002Dto().setCounterId(dealDto.getCounterId());
        dto.getTrdInvestPersonInfo002Dto().setTrader(dealDto.getTrader());
        dto.getTrdInvestPersonInfo002Dto().setContactInfo(dealDto.getCounterContact());
        dto.getTrdInvestPersonInfo002Dto().setCounterTrader(dealDto.getCounterTrader());
        dto.getTrdInvestPersonInfo002Dto().setIsOutTrade(dealDto.getIsOutTrade());
        dto.getTrdInvestPersonInfo002Dto().setTraderDept(dealDto.getCreateDept());
        dto.setPortfolioId(portfolioId);
        if (dealDto.getCounterTrader() == null || dealDto.getCounterTrader().isEmpty()) {
            TrdProductDeal temp = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                TrdProductDealMapper.class, dealDto.getTradeId().substring(0, 18),
                TrdProductDeal.class);
            dto.getTrdInvestPersonInfo002Dto().setCounterId(temp.getCounterId());
            dto.getTrdInvestPersonInfo002Dto().setTrader(temp.getTrader());
            dto.getTrdInvestPersonInfo002Dto().setContactInfo(temp.getCounterContact());
            dto.getTrdInvestPersonInfo002Dto().setCounterTrader(temp.getCounterTrader());
            dto.getTrdInvestPersonInfo002Dto().setIsOutTrade(temp.getIsOutTrade());
            dto.getTrdInvestPersonInfo002Dto().setTraderDept(temp.getCreateDept());
        }
        return dto;
    }

    /**
     * 获取组合代码
     * 
     * @return 联想文本框
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public List<SuggestDto> getPortfolioId() {
        Map<String, Object> map = RequestUtil.getPageParam();
        map.put("userId", SecurityUtil.getLoginUser().getUid());
        return trdFxswapInvestTrade002Service.getPortfolioId(map);
    }
    
    /*添加交割日与期限联动规则 gaopeng 2019/10/08 start*/
    /**
     * 推算近端交割日
     * @return 近端交割日
     */
    @RequestMapping("getvdate")
    @ResponseBody
    public Map<String,Object> getvdate(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdFxswapInvestTrade002Service.getvdate(map);
    }
    
    /**
     * 推算远端交割日
     * @return 远端交割日
     */
    @RequestMapping("getmdate")
    @ResponseBody
    public Map<String,Object> getmdate(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdFxswapInvestTrade002Service.getmdate(map);
    }
    /*添加交割日与期限联动规则 gaopeng 2019/10/08 end*/

    /**
     * 交易费用计算
     * 
     * @return 交易费用金额
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFeeAmt")
    public BigDecimal getFeeAmt(@RequestBody TrdFxswapInvestTrade001Dto dto) {
        return trdFxswapInvestTrade002Service.getFeeAmt(dto);
    }

    /**
     * 保存
     * 
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) TrdFxswapInvestTrade001Dto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        
        
        
        ProcessParameter<TrdFxswapInvestTrade001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdFxswapInvestTrade001Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        if (StringUtil.isEmpty(dto.getTradeId())) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setCommonBusinessExtType(
                DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_外汇掉期投资交易));
            dto.setTradeId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));
        }
        //----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            AjaxResult ar=interfRiskRdsService.checkRiskRds(new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(), DictCodeListExt2.平安资产类型_外汇掉期.getValue(),
                    (en)->interfRiskRdsService.sendRiskRdsData(dto,en));
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        //----------add by jjpei 20200114 核风险版本 end----------
        // 划款
        if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
            dto.setBussTrdTransferOrder(
                bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
            for (BussTrdTransferOrderDto item : dto.getBussTrdTransferOrder()) {
                StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, item);
                list.add(stlPayOrdersDto);
            }
            dto.setStlPayOrdersList(list);
        }
        // 附件
        if (CollectionUtils.isNotEmpty(dto.getFileArray())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getTradeId());
        }
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getTradeId());
        params.setBusinessType(DictCodeListExt2.共通业务类型_外汇掉期投资交易.getValue());
        // 业务说明：“交易编号：” + 上述取得的交易编号+“资产名称：”+画面.资产名称+“交易日：”+画面.交易日
        params.setComment(entity -> this.getMessage("TrdFxswapInvestTrade001.tradeId") + "("
                                    + entity.getTradeId() + ")" + "-"
                                    + this.getMessage("TrdFxswapInvestTrade001.curPair") + "("
                                    + entity.getCurPair() + ")" + "-"
                                    + this.getMessage("TrdFxswapInvestTrade001.tradeDate") + "("
                                    + DateUtil.format(entity.getTradeDate()) + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = trdFxswapInvestTrade002Service.start(processParameter, dto);
        
        //授信占用额度
//        trdCommonController.delLinesDeal(dto.getRiskCreditListCommonDto(),dto.getTradeId());
        return new AjaxResult(msg);
    }
}

/*
 * 文件名：TrdPlanCcyInvestTrade003Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.planccy.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;

import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.trade.planccy.dto.TrdPlanCcyInvestTradeConfirmDto;
import com.joyintech.tams.invest.trade.planccy.dto.TrdPlanCcyInvestTradeDto;
import com.joyintech.tams.invest.trade.planccy.service.TrdPlanCcyInvestTrade001Service;
import com.joyintech.tams.invest.trade.planccy.service.TrdPlanCcyInvestTrade003Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2019年1月7日
 * @see TrdPlanCcyInvestTrade005Controller
 * @since 1.0
 */
@Controller
@PageId("TrdPlanCcyInvestTrade005")
@RequestMapping("/invest/trade/planccy/TrdPlanCcyInvestTrade005")
public class TrdPlanCcyInvestTrade005Controller extends BaseController {

    @Autowired
    private TrdPlanCcyInvestTrade003Service trdPlanCcyInvestTrade003Service;
    
    /**
     * 清算指令共通Service
     */
    @Autowired(required = false)
    private BussSettleOrderCommService bussSettleOrderCommService;

    /**
     * 日期相关
     */
    @Autowired
    private DateCalService dateCalService;

    /**
     * 划款指令业务组件service
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 确认service
     */
    @Autowired
    private TrdPlanCcyInvestTrade001Service trdPlanCcyInvestTrade001Service;
    
    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        /*添加二三四级数据字典 lishuping 2019/8/6 start*/
        setSelectOptionToModel(model,
            new DictCode[] {DictCode.计划类资产一级分类,DictCode.计划类资产二级分类, DictCode.交易场所,
                DictCode.交易方向, DictCode.清算速度, DictCode.币种,DictCode.行内资产二级分类,DictCode.行内资产三级分类,DictCode.行内资产四级分类,});
        /*添加二三四级数据字典 lishuping 2019/8/6 end*/
        model.addAttribute("ps_T05", DictCodeList.交易方向_申购确认.getValue());
        model.addAttribute("ps_T19", DictCodeList.交易方向_赎回确认.getValue());
        model.addAttribute("ps_T24", DictCodeList.交易方向_赎回到账.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        
        TrdPlanCcyInvestTradeConfirmDto dto = new TrdPlanCcyInvestTradeConfirmDto();
        String tradeId = this.getParam("tradeId");
        String status = this.getParam("validStatus");
        // String finprodId = this.getParam("finprodId");
        String ps = this.getParam("ps");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String pageModel = getParam("pageModel");
        // guyunfei 20190910 start
        String feeType = "";
        if(StringUtil.equals(ps, DictCodeList.交易方向_申购确认.getValue())){
            feeType = DictCodeListExt1.一次性费用类型_申购费.getValue();
        }else{
            feeType = DictCodeListExt1.一次性费用类型_赎回费.getValue();
        }
        // guyunfei 20190910 end
        if (StringUtil.isNotEmpty(tradeId)) {
            if (StringUtil.equals(ps, DictCodeList.交易方向_赎回到账.getValue())) {
                List<BussTrdTransferOrderBase> bttob = null;
                String isFromCheck = this.getParam("isFromCheck");
                String statusValid = this.getParam("statusValid");
                if(StringUtil.equals(isFromCheck, "true")){
                    if(StringUtil.equals(statusValid, DictCodeListExt1.画面数据状态_有效.getValue())){
                        dto = trdPlanCcyInvestTrade003Service.getTrdFundDto(tradeId,feeType);
                    }else{
                        dto = trdPlanCcyInvestTrade003Service.getTrdFundDtoForAccount(tradeId);
                        bttob = trdPlanCcyInvestTrade003Service.selectOrdersByBizCode(tradeId);
                        model.addAttribute("single", 1);
                    }
                }else{
                    if (StringUtil.equals(status, DictCodeListExt1.交易流程状态_有效.getValue())) {
                        dto = trdPlanCcyInvestTrade003Service.getTrdFundDto(tradeId,feeType);
                    }else if(StringUtil.equals(status, DictCodeListExt1.交易流程状态_撤销.getValue())){
                        dto = trdPlanCcyInvestTrade003Service.getTrdFundDto(tradeId,feeType);
                    }
                    else {
                        dto = trdPlanCcyInvestTrade003Service.getTrdFundDtoForAccount(tradeId);
                        bttob = trdPlanCcyInvestTrade003Service.selectOrdersByBizCode(tradeId);
                    }
                }
                if (bttob != null && bttob.size() > 0) {
                    List<BussTrdTransferOrderDto> bttodList = new ArrayList<>();
                    BussTrdTransferOrderDto bttod = new BussTrdTransferOrderDto();
                    BeanUtil.copyPropertiesIgnoreNull(bttod, bttob.get(0));
                    bttodList.add(bttod);
                    model.addAttribute("entity_bussRepFinProductInfoDto",
                        JsonUtils.object2Json(bussTrdTransferOrderService.save(bttodList).get(0)));

                }
                TrdInvestPersonInfo002Dto tipiDto = dto.getTrdInvestPersonInfo();
                model.addAttribute("entity_trdInvestPersonInfo", JsonUtils.object2Json(tipiDto));
                model.addAttribute("businessId", dto.getTradeId());
                model.addAttribute("tradeId", dto.getTradeId());
            }
            // 双击查看反显流程中的数据
            else if (StringUtil.equals(status, DictCodeListExt1.交易流程状态_流程中.getValue())
                && !StringUtil.equals(ps, DictCodeList.交易方向_赎回到账.getValue())) {
                dto = trdPlanCcyInvestTrade003Service.getBussinessEntityByWorkflowId(workflowId);
                BussTrdTransferOrderDto bttoDto = dto.getBussTrdTransferOrder().get(0);
                TrdInvestPersonInfo002Dto tipiDto = dto.getTrdInvestPersonInfo();
                model.addAttribute("entity_bussRepFinProductInfoDto",JsonUtils.object2Json(bttoDto));
                model.addAttribute("entity_trdInvestPersonInfo", JsonUtils.object2Json(tipiDto));
                model.addAttribute("businessId", workflowId);
                model.addAttribute("workflowId",workflowId);
            }
            // 双击查看反显非流程中的数据
            else {
                dto = trdPlanCcyInvestTrade003Service.getTrdFundDto(tradeId,feeType);
                if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回.getValue())){
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("finprodId", dto.getFinprodId());
                    Map<String, Object> result = trdPlanCcyInvestTrade003Service.getPurSpeed(param);
                    Long speed = dto.getSettleSpeed();
                    String calId = (String)result.get("CALID");
                    if (speed == 1) {
                        Date tradeDate = dto.getTradeDate();
                        List<String> holidayList = dateCalService.getDealCalendarHoliday(
                            DateUtil.parse(DateUtil.format(tradeDate)),
                            DateUtil.addDay(DateUtil.parse(DateUtil.format(tradeDate)), 15), calId);
                        Date tradeDay = dateCalService.calculationTradingDay(
                            DateUtil.parse(DateUtil.format(tradeDate)), 1, holidayList, "1");
                        dto.setTradeDate(tradeDay);
                    }
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())) {
                    Map<String, Object> accountTradId = new HashMap<>();
                    if (StringUtil.equals(status, DictCodeListExt1.交易流程状态_撤销.getValue())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("tradeId", dto.getpTradeId());
                        map.put("N", DictCodeList.是否_否.getValue());
                        accountTradId = trdPlanCcyInvestTrade001Service.getAccountId(map);
                        if(accountTradId == null){
                            accountTradId = trdPlanCcyInvestTrade001Service.selectAccountTrdIdinwfl(dto.getpTradeId());
                        }
                    }
                    else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("tradeId", dto.getTradeId());
                        map.put("N", DictCodeList.是否_否.getValue());
                        accountTradId = trdPlanCcyInvestTrade001Service.getAccountId(map);
                        if(accountTradId == null){
                            accountTradId = trdPlanCcyInvestTrade001Service.selectAccountTrdIdinwfl(dto.getTradeId());
                        }
                    }
                    List<BussTrdTransferOrderBase> bttob = new ArrayList<>();
                    if (accountTradId != null) {
                        if (StringUtil.equals((String)accountTradId.get("WORKFLOWID"), "0")) {
                            bttob = trdPlanCcyInvestTrade003Service.selectOrdersByBizCode(
                                (String)accountTradId.get("TRADID"));
                        }
                        else {
                            bttob = bussSettleOrderCommService.selectOrdersByBizCode(
                                (String)accountTradId.get("TRADID"));
                        }
                        if (bttob != null && bttob.size() > 0) {
                            List<BussTrdTransferOrderDto> bttodList = new ArrayList<>();
                            BussTrdTransferOrderDto bttod = new BussTrdTransferOrderDto();
                            BeanUtil.copyPropertiesIgnoreNull(bttod, bttob.get(0));
                            bttodList.add(bttod);
                            model.addAttribute("entity_bussRepFinProductInfoDto",
                                JsonUtils.object2Json(
                                    bussTrdTransferOrderService.save(bttodList).get(0)));
                        }
                    }
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                    dto.setPs(DictCodeList.交易方向_申购确认.getValue());
                }
                else if (StringUtil.equals(dto.getPs(),
                    DictCodeList.交易方向_赎回.getValue())) {
                    dto.setPs(DictCodeList.交易方向_赎回确认.getValue());
                }else if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue()) && !StringUtil.equals(pageModel, GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE)){
                    dto.setPs(DictCodeList.交易方向_赎回到账.getValue());
                    dto.setPageModel(GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE);
                    model.addAttribute("single", 1);
                    dto.setbTradeId(dto.getTradeId());
                }
                if (!StringUtil.isNotEmpty(dto.getbTradeId())) {
                    dto.setbTradeId(dto.getTradeId());
                }

                model.addAttribute("tradeId", dto.getTradeId());
                model.addAttribute("entity_trdInvestPersonInfo",
                    JsonUtils.object2Json(dto.getTrdInvestPersonInfo()));
                model.addAttribute("businessId", dto.getTradeId());
            }
        }
        else if (!WorkFlowUtil.isNotFromToDoList()) {
            dto = trdPlanCcyInvestTrade003Service.getBussinessEntityByWorkflowId(workflowId);
            if (dto.getBussTrdTransferOrder() != null
                && dto.getBussTrdTransferOrder().size() > 0) {
                BussTrdTransferOrderDto bttoDto = dto.getBussTrdTransferOrder().get(0);
                model.addAttribute("entity_bussRepFinProductInfoDto",
                    JsonUtils.object2Json(bttoDto));
            }
            TrdInvestPersonInfo002Dto tipiDto = dto.getTrdInvestPersonInfo();
            model.addAttribute("entity_trdInvestPersonInfo", JsonUtils.object2Json(tipiDto));
            model.addAttribute("businessId", workflowId);
        }
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        dto.setProfitType(DictCodeList.收益类型_货币型.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        dto.setEntrustId(null);
        model.addAttribute("entity", dto);
//        if(StringUtil.isNotEmpty(tradeId)){
//        	model.addAttribute("bussTrdTransfenpeiDto", trdPlanCcyInvestTrade001Service.selectAllTrade(tradeId));	
//        }
        model.addAttribute("totleTradeId", this.getParam("totleTradeId"));
        model.addAttribute("xps", dto.getPs());
        model.addAttribute("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        model.addAttribute("ACCT_TYPE_A07", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        return super.init(model);
    }
    
    @RequestMapping("selectTrdTransfenpeiList")
    @ResponseBody
    public List<BussTrdTransfenpeiDto> selectTrdTransfenpeiList()
         {
        return trdPlanCcyInvestTrade001Service.selectTrdTransfenpeiList(RequestUtil.getPageParam());
    }
    
    /**
     * 查询单位净值 <功能详细描述>
     * 
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("getPrice")
    @ResponseBody
    public BigDecimal getPrice(@RequestBody Map<String, Object> map) {
        return trdPlanCcyInvestTrade003Service.getPrice(map);
    }

    /**
     * 保存按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) TrdPlanCcyInvestTradeConfirmDto dto, BindingResult result) {
        AjaxResult ar = commonCheck(result, dto.getOperateType());
        /*if (dto.getTradeAmt() != null && dto.getProAvailableCash() != null) {
            if (dto.getTradeAmt().compareTo(dto.getProAvailableCash()) == 1) {
                ar.setData("交易金额大于组合可用金额（可透支）," + (String)ar.getData());
            }
        }*/
        
        //授权管理  isAuthorizationSoftcontrol true:软控  false:硬控
        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
        if (!isAuthorizationSoftcontrol) {
            if (authorizationvalid(dto)){
                ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
                errorList.add(new ValidMessage(getMessage("TRD_E_0016", "领导")));
                ar.setErrorList(errorList);
            }
        }
        
        //校验流程是否已存在 yjm start 20190816
        WflValidInputParameter parameter = new WflValidInputParameter();
        parameter.setPageId(getPageId());
        //parameter.setBusinessDataPk2(BussinessDictCodeList.共同业务类型_计划类资产_净值型_投资交易.getValue());
        parameter.setBusinessDataPk3(dto.getTradeId());
        if (mstWorkflowCommonService.isInWflProcess(parameter)){
            ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
            errorList.add(new ValidMessage("待确认的数据已存在，请在待办事项中处理。"));
            ar.setErrorList(errorList);
        }
        //校验流程是否已存在 yjm end 20190816
        if (StringUtil.isNotEmpty(dto.getCalendarId()) && dto.getTradeDate() != null) {
            boolean flg = dateCalService.isDealCalendarHoliday(dto.getTradeDate(),
                dto.getCalendarId());
            if (flg == true) {
                ar.setData("交易日期为节假日," + (String)ar.getData());
            }
        }
        return ar;
    }
    
    @RequestMapping("checkAuthorization")
    @ResponseBody
    public AjaxResult checkAuthorization(@RequestBody TrdPlanCcyInvestTradeConfirmDto dto) {
        AjaxResult wr = new AjaxResult();
        List<JoyinMessage> joyinMessages = new ArrayList<>();
        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
        if (isAuthorizationSoftcontrol) {
            if (authorizationvalid(dto)){
                JoyinMessage ms = new JoyinMessage("交易本金金额超过领导授权额度，是否确认提交!", MessageType.确认消息);
                joyinMessages.add(ms);
                wr.setJoyinMessages(joyinMessages);
            } 
        }
        return wr;
    }
    
    public boolean authorizationvalid(Object value) {
        // 副总裁/总裁助理授权额度检查相关处理
        String counterId = null;
        BigDecimal prinAmt = null;
        String 平安资产类型 = null;
        String windtypeone = null;
        String tradeMarket = null;
        String ps = null;
        // 资产类型改为授权类型 add by renping 2019-08-06  start
        String finprodId = null;
        // 资产类型改为授权类型 add by renping 2019-08-06  end
        
        boolean isNumCheck = true;
        if (value instanceof TrdPlanCcyInvestTradeConfirmDto) {
            TrdPlanCcyInvestTradeConfirmDto inputDto = (TrdPlanCcyInvestTradeConfirmDto) value;

            // 副总裁/总裁助理授权额度检查相关处理
            counterId = inputDto.getCounterId();
            prinAmt = inputDto.getTradeAmt();
            if (prinAmt == null || prinAmt.compareTo(BigDecimal.ZERO) <= 0) {
                prinAmt = inputDto.getShareAmt();
            }
            //多组合时用最大的券面总额
            if (null != inputDto.getBussTrdTransfenpeiDto() && inputDto.getBussTrdTransfenpeiDto().size() > 0){
                prinAmt = inputDto.getBussTrdTransfenpeiDto().stream().map(BussTrdTransfenpeiDto::getTradeAmt).max((x1, x2) -> x1.compareTo(x2)).get();
            }
            平安资产类型 = DictCodeList.平安资产类型_计划类资产_货币型.getValue();
            // 修改交易场所by赵东东  2019.8.23  start
            tradeMarket = inputDto.getTradeMarket();
            // 修改交易场所by赵东东  2019.8.23  end
            ps = inputDto.getPs();
            finprodId = inputDto.getFinprodId();
            // 资产类型改为授权类型 add by renping 2019-08-06  end
            
            //赎回时不对份额进行授权校验
            if (DictCodeList.交易方向_赎回.getValue().equals(ps) && (inputDto.getPrinAmt() == null || inputDto.getPrinAmt().compareTo(BigDecimal.ZERO) <= 0)){
                isNumCheck = false;
            }
        }
        // 授权额度检查（当本金金额高于副总裁/总裁助理相应资产的授权额度时，提示消息）
        if (prinAmt != null && isNumCheck) {
            CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
            Map<String, Object> params = CommonBusinessService.setParams(counterId, prinAmt, LoginUserUtil.getUserName(),平安资产类型, windtypeone, tradeMarket, ps
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    , finprodId , null);
            if (commonBusinessService != null && commonBusinessService.是否超过副总额额度(params)) {
                // 交易本金金额超过副总裁/总裁助理授权额度，无法发起交易!
                return true;
            }
        }
        return false;
    }

    @RequestMapping("save")
    @ResponseBody
    AjaxResult save(@RequestBody TrdPlanCcyInvestTradeConfirmDto dto, BindingResult br) throws InvocationTargetException, IllegalAccessException, WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        List<TrdPlanCcyInvestTradeConfirmDto> dtoList = new ArrayList<>();
    	List<BussTrdTransfenpeiDto> fenpeiList = dto.getBussTrdTransfenpeiDto();//组合分配
    	List<BussTrdTransferOrderDto> orderList = dto.getBussTrdTransferOrder();//划款指令
    	for(int i = 0; i < fenpeiList.size(); i++){
    		TrdPlanCcyInvestTradeConfirmDto tempDto = new TrdPlanCcyInvestTradeConfirmDto();
    		BeanUtil.copyProperties(tempDto, dto);
    		List<BussTrdTransferOrderDto> tmpOrderList = new ArrayList<BussTrdTransferOrderDto>();
    		if(!StringUtil.isEmptyList(fenpeiList)){
    			if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())){
    				tmpOrderList.add(orderList.get(i));
    				tempDto.setBussTrdTransferOrder(tmpOrderList);//划款指令
    			}
    				tempDto.setEntrustId(fenpeiList.get(i).getEntrustId());//委托代码
    				//申购赎回确认前置交易编号存，原交易的交易编号
    				tempDto.setbTradeId(fenpeiList.get(i).getTradeId());
					tempDto.setPortfolioId(fenpeiList.get(i).getPortfolioId());//组合代码
					tempDto.setTradeAmt(fenpeiList.get(i).getTradeAmt());//交易金额
					tempDto.setPrinAmt(fenpeiList.get(i).getTradeAmt());
					tempDto.setShareAmt(fenpeiList.get(i).getShareAmt());//交易份额
					tempDto.setIntAmt(fenpeiList.get(i).getIntAmt());//应计利息总额
					tempDto.setFeeAmtPay(fenpeiList.get(i).getFeeAmtPay());
					if (baseGetBusinessId != null) {
			            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
			            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_交易);
			            if(StringUtil.equals(tempDto.getType7(), DictCodeList.计划类资产二级分类_委托债权.getValue())){
			                codeParameter.setType1(DictCodeList.计划类资产二级分类_委托债权);
			            }else if(StringUtil.equals(tempDto.getType7(), DictCodeList.计划类资产二级分类_委托权益.getValue())){
			                codeParameter.setType1(DictCodeList.计划类资产二级分类_委托权益);
			            }else if(StringUtil.equals(tempDto.getType7(), DictCodeList.计划类资产二级分类_自主管理.getValue())){
			                codeParameter.setType1(DictCodeList.计划类资产二级分类_自主管理);
			            }
			            tempDto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
			        }
			        else {
			        	tempDto.setTradeId(IDGenerator.generate());
			        }
					dtoList.add(tempDto);
    		}
    	}
        ProcessParameter<TrdPlanCcyInvestTradeConfirmDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdPlanCcyInvestTradeConfirmDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId("TrdPlanCcyInvestTrade003");
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        }
        else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId("TrdPlanCcyInvestTrade003");
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode((entity) -> entity.getTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("TrdFundInvestTrade002.tradeId"), entity.getTradeId(),
            getMessage("TrdFundInvestTrade002.finprodName"),
            entity.getFinprodName(),
            getMessage("TrdFundInvestTrade002.tradeDate"),
            DateUtil.format(entity.getTradeDate())));
        String totleTradeId = IDGenerator.generate();
        for(TrdPlanCcyInvestTradeConfirmDto confirmDto :dtoList){
        	prepareEntity(confirmDto);
        	confirmDto.setTotleTradeId(totleTradeId);
        }
        String msg = trdPlanCcyInvestTrade003Service.start(processParameter, dtoList.toArray(new TrdPlanCcyInvestTradeConfirmDto[]{}));
        return new AjaxResult(msg);
    }

    /**
     * 处理基金投资交易自定义dto <功能详细描述>
     * 
     * @param dto
     * @see
     * @since 1.0
     */
    private void prepareEntity(TrdPlanCcyInvestTradeConfirmDto dto) {
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
//        dto.setTradeAmt(dto.getPrinAmt());
        // dto.setShareAmt(
        // ArithmeticUtil.divide(dto.getPrinAmt(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        dto.setYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100)));
        dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100)));
        dto.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
        dto.setIsCancel(DictCodeList.是否_否.getValue());
        dto.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
        dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        dto.setIsClean(DictCodeList.是否_是.getValue());
        dto.setFeeAmt(ArithmeticUtil.doAdd(dto.getFeeAmtPay(), dto.getChannelFee()));
        dto.setSettleDate(dto.getTradeDate());
        // dto.setbTradeId(dto.getTradeId());
        dto.setIssuePartyId(CacheUtil.getSysParam(BussinessConstant.ISSUE_PARTY_ID));
        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            dto.setTrader(personInfo.getTrader());
            dto.setCounterId(personInfo.getCounterId());
            dto.setIsOutTrade(personInfo.getIsOutTrade());
            dto.setCounterTrader(personInfo.getCounterTrader());
            dto.setCounterContact(personInfo.getContactInfo());
        }
        // 金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
            trdProductDealAdd.setPurCfmDate(dto.getTradeDate());
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())) {
            trdProductDealAdd.setRedCfmDate(dto.getRedCfmDate());
        }
        trdProductDealAdd.setTradeId(dto.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setEntrustId(dto.getEntrustId());
//        trdProductDealAdd.setProdId(dto.getFinprodId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        dto.setTrdProductDealAdd(trdProductDealAdd);
        // 组合交易表
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
        ptlTrade.setSettleDate(dto.getTradeDate());
        ptlTrades.add(ptlTrade);

        if (!dto.getPortfolioId().equals(dto.getActPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrade1.setBusiNo(dto.getTradeId());
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setBusiId(dto.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
            ptlTrade1.setSettleDate(dto.getTradeDate());
            ptlTrades.add(ptlTrade1);
        }
        dto.setPtlTrade(ptlTrades);
        // 划款指令
        // 在申购确认时获取划款指令dto
        if (StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), dto.getPs())) {
            List<BussTrdTransferOrderBase> bttob = bussSettleOrderCommService.selectOrdersByBizCode(
                dto.getbTradeId());
            if (bttob != null && bttob.size() > 0) {
                List<BussTrdTransferOrderDto> bttodList = new ArrayList<>();
                BussTrdTransferOrderDto bttod = new BussTrdTransferOrderDto();
                BeanUtil.copyPropertiesIgnoreNull(bttod, bttob.get(0));
                bttodList.add(bttod);
                dto.setBussTrdTransferOrder(bttodList);
            }
        }
        if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
            dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTradeId());
            dto.setBussTrdTransferOrder(
                bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            dto.getBussTrdTransferOrder().get(0).setIsTotalTrade(DictCodeList.是否_否.getValue());
        }
        // 金融产品交易扩展
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId());
        String paBussType = null;
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_申购申请.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_申购确认.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_赎回申请.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_赎回确认.getValue();
        }
        paTrdProductDeal.setPaBussType(paBussType);
        paTrdProductDeal.setDeptCode(personInfo==null?null:personInfo.getTraderDept());
        dto.setPaTrdProductDeal(paTrdProductDeal);
        dto.setPortfolioId(dto.getActPortfolioId());
    }
}

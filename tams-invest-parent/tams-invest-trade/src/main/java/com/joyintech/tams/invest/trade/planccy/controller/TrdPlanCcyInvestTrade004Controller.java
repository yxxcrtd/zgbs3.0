/*
 * 文件名：TrdPlanCcyInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.planccy.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.planccy.dto.TrdPlanCcyInvestTradeDto;
import com.joyintech.tams.invest.trade.planccy.service.TrdPlanCcyInvestTrade002Service;
import com.joyintech.tams.invest.trade.planccy.service.TrdPlanCcyInvestTrade003Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2019年1月7日
 * @see TrdPlanCcyInvestTrade004Controller
 * @since 1.0
 */
@Controller
@PageId("TrdPlanCcyInvestTrade004")
@RequestMapping("/invest/trade/planccy/TrdPlanCcyInvestTrade004")
public class TrdPlanCcyInvestTrade004Controller extends BaseController {

	@Autowired
	private TradeCommonService tradeCommonService;
	
    @Autowired
    private TrdPlanCcyInvestTrade002Service trdPlanCcyInvestTrade002Service;

    @Autowired
    private TrdPlanCcyInvestTrade003Service trdPlanCcyInvestTrade003Service;

    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;

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
    
    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        /*添加资产二级-四级分类的数据字典值 lishuping 2019/7/30 start*/
        setSelectOptionToModel(model,
            new DictCode[] {DictCode.计划类资产一级分类, DictCode.计划类资产二级分类,
                DictCode.交易场所, DictCode.交易方向, DictCode.清算速度,
                DictCode.币种, DictCode.行内资产二级分类, DictCode.行内资产三级分类,
                    DictCode.行内资产四级分类,DictCode.分配方式});
        /*添加资产二级-四级分类的数据字典值 lishuping 2019/7/30 end*/
        model.addAttribute("ps_T04", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("ps_T06", DictCodeList.交易方向_赎回.getValue());
        model.addAttribute("ps_mr", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("ps_mc", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("settlet1", DictCodeList.清算速度_T_1.getValue());
        model.addAttribute("settlet2", DictCodeListExt2.清算速度_T_2.getValue());
        
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        
        TrdPlanCcyInvestTradeDto dto = new TrdPlanCcyInvestTradeDto();
        String tradeId = this.getParam("tradeId");
        String status = this.getParam("validStatus");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (StringUtil.isNotEmpty(tradeId)) {
            // 双击查看反显流程中的数据
            if (StringUtil.equals(status, DictCodeListExt1.交易流程状态_流程中.getValue())) {
                dto = trdPlanCcyInvestTrade002Service.getBussinessEntityByWorkflowId(workflowId);
                if (dto.getBussTrdTransferOrder() != null
                    && dto.getBussTrdTransferOrder().size() > 0) {
                    BussTrdTransferOrderDto bttoDto = dto.getBussTrdTransferOrder().get(0);
                    model.addAttribute("entity_bussRepFinProductInfoDto",
                        JsonUtils.object2Json(bttoDto));
                }
                TrdInvestPersonInfo002Dto tipiDto = dto.getTrdInvestPersonInfo();
                model.addAttribute("riskTrialRetDto",
                    JsonUtils.object2Json(dto.getRiskTrialRetDto()));
                model.addAttribute("entity_trdInvestPersonInfo", JsonUtils.object2Json(tipiDto));
                model.addAttribute("businessId", workflowId);
            }
            // 双击查看反显非流程中的数据
            else {
                dto = trdPlanCcyInvestTrade002Service.getTrdFundDto(tradeId);
                if(StringUtil.isNotEmpty(dto.getSettleSpeed())){
                    if (dto.getSettleSpeed() == 0) {
                        dto.setSettleSpreed(DictCodeList.清算速度_T_0.getValue());
                    }
                    else if (dto.getSettleSpeed() == 1) {
                        dto.setSettleSpreed(DictCodeList.清算速度_T_1.getValue());
                    }
                    else if (dto.getSettleSpeed() == 2) {
                        dto.setSettleSpreed(DictCodeList.清算速度_T_2.getValue());
                    }
                }
                model.addAttribute("tradeId", dto.getTradeId());
                model.addAttribute("entity_trdInvestPersonInfo",
                    JsonUtils.object2Json(dto.getTrdInvestPersonInfo()));
                model.addAttribute("businessId", dto.getTradeId());
            }
        }
        else if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            dto = trdPlanCcyInvestTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            if (dto.getBussTrdTransferOrder() != null
                && dto.getBussTrdTransferOrder().size() > 0) {
                BussTrdTransferOrderDto bttoDto = dto.getBussTrdTransferOrder().get(0);
                model.addAttribute("entity_bussRepFinProductInfoDto",
                    JsonUtils.object2Json(bttoDto));
            }
            TrdInvestPersonInfo002Dto tipiDto = dto.getTrdInvestPersonInfo();
            model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(dto.getRiskTrialRetDto()));
            model.addAttribute("entity_trdInvestPersonInfo", JsonUtils.object2Json(tipiDto));
            model.addAttribute("businessId", workflowId);
        }
        else {
            String entrustId = this.getParam("entrustId");
            String entrustAmtTotle = this.getParam("entrustAmtTotle");
            if (StringUtil.isNotEmpty(entrustId)) {
            	String[] entrustIdArr = entrustId.split(",");
                BaseEtrInvestInfoDto entrustDto = trdPlanCcyInvestTrade002Service.getEtrustDto(entrustIdArr[0]);
                if (entrustDto != null) {
                    EtrEntrust eedto = entrustDto.getEtrEntrust();
                    if (eedto != null) {
                        dto.setFinprodId(eedto.getFinprodId());
//                        dto.setPortfolioId(eedto.getPortfolioId());
                        dto.setPs(eedto.getPs());
//                        dto.setTradeAmt(new BigDecimal(entrustAmtTotle));
//                        dto.setPrinAmt(eedto.getEntrustAmt());
//                        dto.setShareAmt(eedto.getEntrustNum());
//                        dto.setIntAmt(eedto.getPrice());
                        TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
                        trdInvestPersonInfo.setCounterTrader(eedto.getCounterTrader());
                        trdInvestPersonInfo.setIsOutTrade(eedto.getIsOutTrade());
                        trdInvestPersonInfo.setCounterId(eedto.getCounterId());
                        trdInvestPersonInfo.setContactInfo(eedto.getContactInfo());
                        if (entrustDto.getEtrDistributeList() != null
                            && entrustDto.getEtrDistributeList().size() > 0) {
                            trdInvestPersonInfo.setTrader(
                                entrustDto.getEtrDistributeList().get(0).getTrader());
                        }
                        model.addAttribute("entity_trdInvestPersonInfo",
                            JsonUtils.object2Json(trdInvestPersonInfo));
                    }
                }
                dto.setEntrustId(entrustId);
                model.addAttribute("businessId", entrustId);
            }
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        //---add by 裴家俊 -2019/034/02----------start-------
        model.addAttribute("FINPROD_TYPE_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        //---add by 裴家俊 -2019/034/02----------end-------
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        dto.setProfitType(DictCodeList.收益类型_货币型.getValue());
        model.addAttribute("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        model.addAttribute("ACCT_TYPE_A07", DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        model.addAttribute("entity", dto);
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
     * 资产代码联想下拉框 <功能详细描述>
     * 
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinprodId")
    @ResponseBody
    List<SelectOptionDto> getFinprodId(@RequestParam Map<String, Object> params) {
        return trdPlanCcyInvestTrade002Service.getFinprodId(params);
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
        ValidateGroupEdit.class}) TrdPlanCcyInvestTradeDto dto, BindingResult result) {
        AjaxResult ar = commonCheck(result, dto.getOperateType());
        if (dto.getTradeAmt() != null && dto.getProAvailableCash() != null) {
            if (dto.getTradeAmt().compareTo(dto.getProAvailableCash()) == 1) {
                ar.setData("交易金额大于组合可用金额（可透支）," + (String)ar.getData());
            }
        }
       /* if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
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
            	 ValidMessage validMessage = new ValidMessage("shareAmt",
            			 "可用委托数量不足,当前最多可委托数量为:"+ entrustNum.subtract(wflPrinAmt));
                     errorList.add(validMessage);
                res.setErrorList(errorList);
                res.setMessageType(MessageType.错误消息);
                return res;
            }
    	}*/
        if (StringUtil.isNotEmpty(dto.getCalendarId()) && dto.getTradeDate() != null) {
            boolean flg = dateCalService.isDealCalendarHoliday(dto.getTradeDate(),
                dto.getCalendarId());
            if (flg == true) {
                ar.setData("交易日期为节假日," + (String)ar.getData());
            }
        }

        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) ){
            // 调用基础组件.【组合可用现金和实际现金查询更新组件.查询持仓(可用)】
            Map<String, Object> paramMap = new HashMap<String, Object>();
            //      paramMap.put("portfolioId", dto.getPortfolioId());
            paramMap.put("tradeDate", DateUtil.format(dto.getTradeDate()));
            paramMap.put("ccy", dto.getCcy());
            //paramMap.put("calendarId", dto.getCalendarId());
            paramMap.put("chlId", dto.getChlId());
            paramMap.put("finprodId", dto.getFinprodId());
            //获得资产维度的持仓
            Map<String, Object>  resultMap = bussTrdAvailableCashQueryService.getFinAvaPosition(paramMap);

            BigDecimal position = new BigDecimal((String) resultMap.get("POSITION"));
            //融资金额
            BigDecimal issueAmt = new BigDecimal((String)resultMap.get("ISSUEAMT"));
            position = com.joyintech.framework.util.ArithmeticUtil.doAdd(position, dto.getTradeAmt());
            //资产 融资金额、发行金额
            if (issueAmt.compareTo(position) < 0) {
                ar.setData(getMessage("TRD_C_0005"));
            }
        }

        return ar;
    }

    /**
     * 新建/修改流程 <功能详细描述>
     * 
     * @param dto
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("save")
    @ResponseBody
    public AjaxResult save(@RequestBody TrdPlanCcyInvestTradeDto dto, BindingResult br) throws InvocationTargetException, IllegalAccessException, IOException, InstantiationException, WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        List<TrdPlanCcyInvestTradeDto> dtoList = new ArrayList<>();
    	List<BussTrdTransfenpeiDto> fenpeiList = dto.getBussTrdTransfenpeiDto();//组合分配
    	List<BussTrdTransferOrderDto> orderList = dto.getBussTrdTransferOrder();//划款指令
    	for(int i = 0; i < fenpeiList.size(); i++){
    		TrdPlanCcyInvestTradeDto tempDto = new TrdPlanCcyInvestTradeDto();
    		BeanUtil.copyProperties(tempDto, dto);
    		List<BussTrdTransferOrderDto> tmpOrderList = new ArrayList<BussTrdTransferOrderDto>();
    		if(!StringUtil.isEmptyList(fenpeiList)){
    			if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) 
    					|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())){
    				if(!(fenpeiList.get(i).getTradeAmt().compareTo(BigDecimal.ZERO) == 1)){
    					continue;
    				}
    			}else if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()) 
    					|| StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())){
    				if(!(fenpeiList.get(i).getShareAmt().compareTo(BigDecimal.ZERO) == 1)){
    					continue;
    				}
    			}
    			if(!StringUtil.isEmptyList(orderList)){
    				tmpOrderList.add(orderList.get(i));
    				tempDto.setBussTrdTransferOrder(tmpOrderList);
    			}
    				tempDto.setEntrustId(fenpeiList.get(i).getEntrustId());//委托代码
					tempDto.setPortfolioId(fenpeiList.get(i).getPortfolioId());//组合代码
					tempDto.setTradeAmt(fenpeiList.get(i).getTradeAmt());//交易金额
					tempDto.setPrinAmt(fenpeiList.get(i).getTradeAmt());
					tempDto.setShareAmt(fenpeiList.get(i).getShareAmt());//交易份额
					tempDto.setIntAmt(fenpeiList.get(i).getIntAmt());//应计利息总额
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
					 tempDto.setRiskTrialRetDto(null);
                     //多组合交易对风险试算处理逻辑
                     if(CollectionUtils.isNotEmpty(dto.getRiskTrialRetDto())){
                          List<RiskTrialRetDto> riskTrialRetlist = new ArrayList<>();
                          for (RiskTrialRetDto riskTrialRetDto:dto.getRiskTrialRetDto()) {
                             if(StringUtils.equals(riskTrialRetDto.getPortfolioCode(),fenpeiList.get(i).getPortfolioId())){
                                 riskTrialRetlist.add(riskTrialRetDto); 
                             }
                             List<String> colist= interfRiskRdsService.queryPortfolioPackageDtlByBagId(riskTrialRetDto.getPortfolioCode());
                             if (!CollectionUtils.isEmpty(colist) && colist.contains(fenpeiList.get(i).getPortfolioId())) {
                                 riskTrialRetlist.add(riskTrialRetDto); 
                             } 
                          }  
                          tempDto.setRiskTrialRetDto(riskTrialRetlist);
                     }
					dtoList.add(tempDto);
    		}
    	}
    	//----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            AjaxResult ar =interfRiskRdsService.checkBatchRiskRds(dtoList,new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(), BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),DictCodeList.平安资产类型_计划类资产_货币型.getValue(),
                    (sm,en)->interfRiskRdsService.sendRiskRdsData(sm,en));
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        //----------add by jjpei 20200114 核风险版本 end---------- 
        ProcessParameter<TrdPlanCcyInvestTradeDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdPlanCcyInvestTradeDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId("TrdPlanCcyInvestTrade002");
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        }
        else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId("TrdPlanCcyInvestTrade002");
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode((entity) -> entity.getTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("TrdFundInvestTrade002.tradeId"), entity.getTradeId(),
            getMessage("TrdFundInvestTrade002.finprodName"),
            //GlobalConstant.SPLIT,
            entity.getFinprodName(), getMessage("TrdFundInvestTrade002.tradeDate"),
            DateUtil.format(entity.getTradeDate())));
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
        	String totleTradeId = IDGenerator.generate();
        	for (TrdPlanCcyInvestTradeDto trdPlanCcyInvestTradeDto : dtoList) {
        		prepareEntity(trdPlanCcyInvestTradeDto);
        		trdPlanCcyInvestTradeDto.setTotleTradeId(totleTradeId);
        	}
        }
        String msg = trdPlanCcyInvestTrade002Service.start(processParameter, dtoList.toArray(new TrdPlanCcyInvestTradeDto[]{}));
        return new AjaxResult(msg);
    }

    /**
     * 处理基金投资交易自定义dto <功能详细描述>
     * 
     * @param dto
     * @see
     * @since 1.0
     */
    private TrdPlanCcyInvestTradeDto prepareEntity(TrdPlanCcyInvestTradeDto dto) {
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
//        dto.setTradeAmt(dto.getPrinAmt());
        dto.setYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100)));
        dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100)));
        dto.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
        dto.setIsCancel(DictCodeList.是否_否.getValue());
        dto.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
        dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        if (StringUtil.equals(dto.getSettleSpreed(), DictCodeList.清算速度_T_0.getValue())) {
            dto.setSettleSpeed((long)0);
        }
        else if (StringUtil.equals(dto.getSettleSpreed(),
            DictCodeList.清算速度_T_1.getValue())) {
            dto.setSettleSpeed((long)1);
        } else if (StringUtil.equals(dto.getSettleSpreed(),
                DictCodeList.清算速度_T_2.getValue())) {
            dto.setSettleSpeed((long)2);
        }
        dto.setSettleDate(dto.getTradeDate());
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
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("finprodId", dto.getFinprodId());
            Map<String, Object> result = trdPlanCcyInvestTrade003Service.getPurSpeed(param);
            String speed = (String)result.get("SPEED");
            String calId = (String)result.get("CALID");
            if (StringUtil.equals(speed, "1")) {
                Date tradeDate = dto.getTradeDate();
                List<String> holidayList = dateCalService.getDealCalendarHoliday(
                    DateUtil.parse(DateUtil.format(tradeDate)),
                    DateUtil.addDay(DateUtil.parse(DateUtil.format(tradeDate)), 15), calId);
                Date tradeDay = dateCalService.calculationTradingDay(
                    DateUtil.parse(DateUtil.format(tradeDate)), 1, holidayList, "1");
                trdProductDealAdd.setPurCfmDate(tradeDay);
            }
        }
        else {
            trdProductDealAdd.setRedCfmDate(dto.getRedCfmDate());
        }
        trdProductDealAdd.setRedCfmDate(dto.getRedCfmDate());
        trdProductDealAdd.setTradeId(dto.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setEntrustId(dto.getEntrustId());
        // trdProductDealAdd.setProdId(dto.getFinprodId());
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
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_申购确认.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_赎回申请.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_赎回确认.getValue();
        }
        paTrdProductDeal.setPaBussType(paBussType);
        paTrdProductDeal.setDeptCode(personInfo==null?null:personInfo.getTraderDept());
        dto.setPaTrdProductDeal(paTrdProductDeal);
        dto.setPortfolioId(dto.getActPortfolioId());
		return dto;
    }

    /**
     * 组合可用现金查询 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getProAvailableCash")
    public Map<String, Object> getProAvailableCash(@RequestBody Map<String, Object> param) {
        return trdPlanCcyInvestTrade002Service.getProAvailableCash(param);
    }

    /**
     * 查询组合可用持仓 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getPtlSecPosition")
    public BigDecimal getPtlSecPosition(@RequestBody PtlSecPositionFlowDto dto) {
        return trdPlanCcyInvestTrade002Service.getPtlSecPosition(dto);
    }
    
    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_交易);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return StringUtil.trimHtmlTag(index);
    }
}

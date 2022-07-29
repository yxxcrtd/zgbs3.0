package com.joyintech.tams.invest.trade.transfer.trdassetsProject.controller;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.dto.BussTrdAvailableCashQuery;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import com.joyintech.tams.invest.trade.transfer.assign.dto.TrdAssetAssignDto;
import com.joyintech.tams.invest.trade.transfer.assign.service.TrdAssetAssignService;
import com.joyintech.tams.invest.trade.transfer.trdassetsProject.dto.TrdAssetsProject001Dto;
import com.joyintech.tams.invest.trade.transfer.trdassetsProject.dto.TrdSplitProject001Dto;
import com.joyintech.tams.invest.trade.transfer.trdassetsProject.service.TrdAssetsProject001Service;
import com.joyintech.tams.invest.trade.trdplanrateinvesttrade.service.TrdPlanRateInvestTrade002Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import com.joyintech.framework.grid.Page;
/*import com.joyintech.tams.invest.period.trdprojduration.dto.TrdProjectDurationProcessDto;
import com.joyintech.tams.invest.period.trdprojduration.service.TrdProjectDuration001Service;*/


/**
 * 项目类资产转仓
 */
@RequestMapping("/invest/trade/transfer/trdassetsProject/TrdAssetsProject001")
@PageId("TrdAssetsProject001")
@Controller
public class TrdAssetsProject001Controller extends BaseController {

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    /**
     * trdAssetsProject001Service
     */
    @Autowired
    TrdAssetsProject001Service trdAssetsProject001Service;
    /**
     * trdPlanRateInvestTrade002Service
     */
    @Autowired
    TrdPlanRateInvestTrade002Service trdPlanRateInvestTrade002Service;
    
    /**
     * TrdProjectDuration001Service 项目类资产存续期service
     */
    /*@Autowired
    private TrdProjectDuration001Service trdProjectDuration001Service;*/

    @Autowired
    private TrdAssetAssignService trdAssetAssignService;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;
    
    @Override
    public String init(Model model){
        //Map<String ,Object> param = RequestUtil.getPageParam();
        String workflowStatus = this.getParam("workflowStatus");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        //param.put("BRANCH_0",Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        TrdAssetsProject001Dto dto = new TrdAssetsProject001Dto();
        
        if (StringUtil.equals(workflowStatus, DictCodeListExt1.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(workflowId))) {
            dto = trdAssetsProject001Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entityData", JsonUtils.object2Json(dto.getProjectListInfoList()));
            model.addAttribute("formTodoList", true);
        } 
        else{
            String finprodMarketId = RequestUtil.getParam("finprodMarketId");
            BigDecimal prinamt = new BigDecimal(RequestUtil.getParam("printAmt"));
        	//param.put("FinprodMarketId",finprodMarketId);
        	dto = trdAssetsProject001Service.getBusinessEntity(finprodMarketId);
        	dto.setFinprodMarketId(finprodMarketId);
        	dto.setFinprodName(RequestUtil.getParam("finprodName"));
        	dto.setInvestRate(new BigDecimal(RequestUtil.getParam("yield")));
        	dto.setChlId(RequestUtil.getParam("chlId"));
        	dto.setPrinAmt(prinamt);//本金金额       	              	        	
        	dto.setInvTransPortf(RequestUtil.getParam("portfolioId"));
        	dto.setSecAcctId(RequestUtil.getParam("secAcctId"));
        	dto.setCpriceAmt(dto.getPrinAmt());
            Map<String,Object> paraMap = new HashMap<>();
            paraMap.put("portfolioId",dto.getInvTransPortf());
            paraMap.put("finprodId","F18_" + finprodMarketId);
            paraMap.put("settleDate",DateUtil.format(dto.getTradeDate()));
            paraMap.put("basis",dto.getBasis());
            paraMap.put("investRate",ArithmeticUtil.divide(dto.getInvestRate(),new BigDecimal(100),6));
            paraMap.put("prinAmt",dto.getPrinAmt());
          //剩余可转仓本金金额，为可用持仓，若存在流程中数据应扣减当前流程中转仓数据start
            paraMap.put("searchDate", DateUtil.getSysDate());
            paraMap.put("secAcctId", dto.getSecAcctId());
            paraMap.put("invAim", RequestUtil.getParam("invAim"));
            paraMap.put("ccy", RequestUtil.getParam("ccy"));
            paraMap.put("chlId", RequestUtil.getParam("chlId"));
        	BigDecimal wflAmt = trdAssetsProject001Service.selectWflAmtAsset(paraMap);
        	if(wflAmt != null) {
        		BigDecimal newUnSplitPrinAmt = ArithmeticUtil.doAdd(prinamt,wflAmt);
        		dto.setUnSplitPrinAmt(newUnSplitPrinAmt);
        	}else {        		
        		dto.setUnSplitPrinAmt(prinamt);//剩余可转仓金额
        	}
        	//剩余可转仓本金金额，为可用持仓，若存在流程中数据应扣减当前流程中转仓数据end
            String intAmt = trdPlanRateInvestTrade002Service.getIntAmtCalcu(paraMap);
            if(StringUtil.isNotEmpty(intAmt)){
                dto.setUnitIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(new BigDecimal(intAmt),new BigDecimal(100)),dto.getPrinAmt(),4));
                dto.setIntAmt(new BigDecimal(intAmt));
            }
        	dto.setTradeAmt(ArithmeticUtil.doAdd(dto.getPrinAmt(),dto.getIntAmt()));
        	dto.setUnitCprice(new BigDecimal(100));
        	dto.setUnitFprice(ArithmeticUtil.doAdd(dto.getUnitCprice(),dto.getUnitIntAmt()));
			Map<String, String> params = new HashMap<>();
			params.put("portfolioId", dto.getInvTransPortf());
			params.put("finprodId", "F18_" + finprodMarketId);
			params.put("payDate", DateUtil.format(dto.getTradeDate()));
			params.put("prinamt", prinamt.toPlainString());
			TrdAssetAssignDto assignDto = trdAssetAssignService.calcAssignAmt(params);
			if (assignDto != null) {
				//dto.setAssignPrinamt(assignDto.getAssignPrinamt());
				dto.setAssignIntamt(assignDto.getAssignIntamt());
				dto.setLiBranch(assignDto.getLiBranch());
				dto.setIsNeedAssign(assignDto.getIsNeedAssign());
			}
        }
        model.addAttribute("entity", dto);
        this.setSelectOptionToModel(model,DictCode.交易方向,
                DictCode.交易场所, DictCode.清算速度, DictCode.有效状态, DictCode.币种, DictCode.计息基础);
        model.addAttribute("T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("FINPROD_TYPE_F18", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("risk_math_switch", CacheUtil.getSysParam("risk_math_switch"));
        
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
     * 风险试算
     * @param model 参数
     * @return
     */
    @RequestMapping("riskMathComm")
    public String riskMathPage(Model model){
        Map<String,Object> param = RequestUtil.getPageParam();
        List<RiskTrialRetDto> riskList = new ArrayList<>();
        if(StringUtil.isNotEmpty(param.get("results")) && !StringUtil.equals((String)param.get("results"),"{}")){
            riskList = JSONArray.parseArray((String)param.get("results"),RiskTrialRetDto.class);
        }
        model.addAttribute("riskList",riskList);
        setSelectOptionToModel(model, DictCode.指标类型, DictCode.监督状态, DictCode.单位,
                DictCode.监督类型, DictCode.监督频率, DictCode.金融产品类型_投管用, DictCode.WIND一级分类_含债券一级分类等,
                DictCode.基金类资产二级类型, DictCode.资产评级, DictCode.申万行业一级类型, DictCode.指标类型映射编码);
        return "/trade/resolute/common/RiskMathComm";
    }

    /**
     * 查询组合信息
     * * @return
     */
    @ResponseBody
    @RequestMapping("selectPortfolioId")
    public List<SelectOptionDto> selectPortfolioId() {
         Map<String, Object> param = RequestUtil.getPageParam();
        param.put("userId", LoginUserUtil.getUserName());
        return trdAssetsProject001Service.selectPortfolioId(param);
    }
    /**
     * 添加校验
     */
    @ResponseBody
    @RequestMapping("addCheck")
    public AjaxResult addCheck(@RequestBody Map<String,Object>param) throws IOException {
        AjaxResult result = new AjaxResult();

        List<ValidMessage> errorList = new ArrayList<>();
        List<TrdAssetsProject001Dto> listInfoDtoList = new ArrayList<>();
        TrdAssetsProject001Dto listInfoDto = new TrdAssetsProject001Dto();
        TrdAssetsProject001Dto dto = new TrdAssetsProject001Dto();

        if(!StringUtil.isNotEmpty(param.get("portfolioId"))){
            errorList.add(new ValidMessage("portfolioId","请输入组合代码！"));
        }
        if(!StringUtil.isNotEmpty(param.get("splitPrinAmt"))){
            errorList.add(new ValidMessage("splitPrinAmt","请输入本金金额！"));
        }
        else{
            if(BigDecimal.ZERO.compareTo(new BigDecimal((String)param.get("splitPrinAmt")))>=0){
                errorList.add(new ValidMessage("splitPrinAmt","本金金额必须大于0！"));
            }
        }

        BigDecimal splitTradeAmt=BigDecimal.ZERO;//转仓交易金额
        BigDecimal splitPrinAmt=BigDecimal.ZERO;//转仓交易本金
        BigDecimal unSplitPrinAmt=BigDecimal.ZERO;//剩余可转仓金额
        if(StringUtil.isNotEmpty(param.get("splitTradeAmt"))){
            splitTradeAmt=new BigDecimal((String)param.get("splitTradeAmt"));//转仓交易金额
        }
        if(StringUtil.isNotEmpty(param.get("splitPrinAmt"))){
            splitPrinAmt=new BigDecimal((String)param.get("splitPrinAmt"));//转仓交易本金
        }
        if(StringUtil.isNotEmpty(param.get("unSplitPrinAmt"))){
            unSplitPrinAmt=new BigDecimal((String)param.get("unSplitPrinAmt"));//剩余可拆分金额
        }
        BussTrdAvailableCashQuery bussTrdAvailableCashQuery= new BussTrdAvailableCashQuery();
        if(StringUtil.isNotEmpty(param.get("bussTrdAvailableCashQuery"))){
            bussTrdAvailableCashQuery = JsonUtils.json2Object(JsonUtils.object2Json(param.get("bussTrdAvailableCashQuery")),BussTrdAvailableCashQuery.class);//组合dto
        }
        if(StringUtil.isNotEmpty(splitPrinAmt) && StringUtil.isNotEmpty(unSplitPrinAmt) && splitPrinAmt.compareTo(unSplitPrinAmt)>0){
            errorList.add(new ValidMessage("splitPrinAmt","转仓金额不能大于剩余可转仓本金金额！"));
        }
        if(errorList!=null && errorList.size()>0){
            result.setStatuCode(500);
            result.setErrorList(errorList);
        }
        else{
            //拆分交易金额不能大于组合剩余本金
            if(StringUtil.isNotEmpty(splitTradeAmt)&&StringUtil.isNotEmpty(bussTrdAvailableCashQuery)&&StringUtil.isNotEmpty(bussTrdAvailableCashQuery.getAvalibleCash())){
                if(splitTradeAmt.compareTo(bussTrdAvailableCashQuery.getAvalibleCash())>0){
                    result.setMessage("组合剩余现金不足！", MessageType.确认消息);
                }
            }
            result.setStatuCode(200);
            listInfoDto = trdAssetsProject001Service.setProjectListInfoDto(param);
            listInfoDtoList.add(listInfoDto);
            dto.setProjectListInfoList(listInfoDtoList);
            result.setData(dto);
        }
        return result;
    }


    /**
     * 保存时校验
     *
     * @param dto    dto
     * @param rst result
     * @return
     */
	@RequestMapping("saveCheck")
	@ResponseBody
	public AjaxResult check(@RequestBody TrdSplitProject001Dto dto, BindingResult rst) {
		AjaxResult result = new AjaxResult();
		if (StringUtil.isEmptyList(dto.getProjectListInfoList())) {
			result.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			result.setMessage("转仓列表信息不能空", MessageType.错误消息);
			return result;
		}
        if (StringUtil.isNotEmpty(dto.getChlId())) {
            Integer count;
            String str = "";
            Map<String, Object> sMap = new HashMap<>();
            for (TrdSplitProject001Dto ptl : dto.getProjectListInfoList()) {
                sMap.put("chlId", dto.getChlId());
                sMap.put("counterId", ptl.getPortfolioId());
                count = trdBondInvestTrade002Service.selectchlTrade(sMap);
                if (count <= 0) {
                    if (!StringUtil.equals(str, "")) {
                        str += "</br>";
                    }
                    str += "交易对手(" + ptl.getPortfolioId() + ")通过一次性通道投资请先进行划款！";
                }
            }
            if(StringUtil.isNotEmpty(str)){
                result.setMessage(str, MessageType.错误消息);
                return result;
            }
        }

		//查询项目类资产存续期交易 状态为 01-待处理、03-流程中 的数据  2020-10-22 start
		/*Map<String, Object> params = new HashMap<String,Object>();
		params.put("finprodId", dto.getFinprodId());//金融产品代码
		params.put("finprodMarketId", dto.getFinprodMarketId());//市场代码
		params.put("payDateStart", DateUtil.format(dto.getSettleDate(), DateUtil.DEFAULT_DATE_FORMAT_EN));//理论支付日开始
		params.put("payDateEnd", DateUtil.format(dto.getSettleDate(), DateUtil.DEFAULT_DATE_FORMAT_EN));//理论支付日结束
		params.put("statusValid", "01,03");//数据状态

		Page<TrdProjectDurationProcessDto> pageData = trdProjectDuration001Service.findPage(params);
		
		if(pageData != null && pageData.getRoot() != null && !pageData.getRoot().isEmpty()) {
			result.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			result.setMessage("该资产当天存在存续期记录，请先处理！", MessageType.错误消息);
			return result;
		}*/
		//查询项目类资产存续期交易 状态为 01-待处理、03-流程中  2020-10-22 end
		
		StringBuilder tips = new StringBuilder();
		for (TrdSplitProject001Dto item : dto.getProjectListInfoList()) {
			BigDecimal tradeAmt = item.getSplitTradeAmt() == null ? BigDecimal.ZERO : item.getSplitTradeAmt();
			BigDecimal prinAmt = item.getPrinAmt() == null ? BigDecimal.ZERO : item.getPrinAmt();
			BigDecimal intAmt = item.getSplitIntAmt() == null ? BigDecimal.ZERO : item.getSplitIntAmt();
			if (tradeAmt.compareTo(prinAmt.add(intAmt)) != 0) {
				tips.append("【" + item.getPortfolioId() + "】交易金额不等于本息和！</br>");
			}
		}
		if (StringUtil.isNotEmpty(tips)) {
			result.setData("请确认转仓列表, 是否继续提交？</br>" + tips.toString());
			return result;
		} else {
			return commonCheck(rst, dto.getOperateType());
		}
	}
    /**
     * 保存处理
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @RequestMapping("save")
    @ResponseBody
    public AjaxResult save(@RequestBody TrdAssetsProject001Dto dto) throws Exception {
        ProcessParameter<TrdAssetsProject001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdAssetsProject001Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        for (TrdAssetsProject001Dto listDto:dto.getProjectListInfoList()) {
            if(StringUtil.isBlank(listDto.getTradeId())) {
                if (baseGetBusinessId != null) {
                    BussinessCodeParameter codeParameter = TrdAssetsProject001Service.genBizCode(dto);
                    listDto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                } else {
                    listDto.setTradeId(IDGenerator.generate());
                }
            }
        }
        params.setBusinessType(DictCodeList.共通业务类型_项目类资产交易.getValue());

        if (BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
            for (TrdAssetsProject001Dto entity:dto.getProjectListInfoList()) {
                entity.setInvTransPortf(dto.getInvTransPortf());
                entity.setWorkflowId(dto.getWorkflowId());
                entity.setCcy(dto.getCcy());
                entity.setProjectType(dto.getProjectType());
                entity.setVdate(dto.getVdate());
                entity.setMdate(dto.getMdate());
                entity.setCpriceAmt(dto.getCpriceAmt());
                entity.setUnitIntAmt(dto.getUnitIntAmt());
                entity.setUnitFprice(dto.getUnitFprice());
                entity.setUnitCprice(dto.getUnitCprice());
                entity.setBaseTradeInfoDtos(trdAssetsProject001Service.genFinProductTradeDto(entity));
            }
        }
        // 转仓编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产转仓);
            dto.setZcId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            dto.setZcId(IDGenerator.generate());
        }

        // ----------add by 裴家俊 20220729 批量实占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            List<TrdAssetsProject001Dto> project001DtoList=dto.getProjectListInfoList().stream().map(item->{
                TrdAssetsProject001Dto trdAssetsProject001Risk=new TrdAssetsProject001Dto();
                BeanUtil.copyPropertiesIgnoreNull(trdAssetsProject001Risk,item);
                trdAssetsProject001Risk.setPortfolioId(item.getInvTransPortf());
                trdAssetsProject001Risk.setCounterId(item.getPortfolioId());
                trdAssetsProject001Risk.setPs(DictCodeList.交易方向_卖出.getValue());
                trdAssetsProject001Risk.setTradeAmt(StringUtil.isNotEmpty(item.getSplitTradeAmt())?item.getSplitTradeAmt():item.getPrinAmt());
                return trdAssetsProject001Risk;
            }).collect(Collectors.toList());
            AjaxResult ar = TradeRdsCommonAop.doListOccupy(project001DtoList,
                    DictCodeList.平安资产类型_项目类资产.getValue(), true, null);
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by 裴家俊 20220729 批量实占功能 end----------

        params.setBusinessCode(dto.getZcId());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("交易编号：" + dto.getZcId(), "资产名称：" + dto.getFinprodName(),
                "起息日：" + DateUtil.format(dto.getVdate())));
        processParameter.setWorkFlowParameter(params);
        String msg = trdAssetsProject001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}

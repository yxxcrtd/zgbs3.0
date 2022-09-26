package com.joyintech.tams.invest.trade.transfer.trdrolloverpledgedrepo.controller;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.dto.BussTrdAvailableCashQuery;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.transfer.assetstransfer.dto.AssetsTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.trdrolloverpledgedrepo.dto.FinDealPledgeDto;
import com.joyintech.tams.invest.trade.transfer.trdrolloverpledgedrepo.dto.TrdRolloverPledgedRepo001Dto;
import com.joyintech.tams.invest.trade.transfer.trdrolloverpledgedrepo.service.TrdRolloverPledgedRepo001Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * 质押式回购交易转仓
 */
@RequestMapping("/invest/trade/transfer/trdrolloverpledgedrepo/TrdRolloverPledgedRepo001")
@PageId("TrdRolloverPledgedRepo001")
@Controller
public class TrdRolloverPledgedRepo001Controller extends BaseController {

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    /**
     * trdRolloverPledgedRepo001Service
     */
    @Autowired
    private TrdRolloverPledgedRepo001Service trdRolloverPledgedRepo001Service;
    /**
     * bussCommonMapperDelegateService
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;
    @Override
    public String init(Model model){
        Map<String ,Object> param = RequestUtil.getPageParam();
        TrdRolloverPledgedRepo001Dto dto = new TrdRolloverPledgedRepo001Dto();
        String workflowStatus = (String)param.get("workflowStatus");
        String workflowId = (String)param.get("workflowId");
        String tradeId = (String)param.get("tradeId");
        if (StringUtil.equals(workflowStatus, DictCodeListExt1.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(workflowId))) {
            dto = trdRolloverPledgedRepo001Service.getBussinessEntityByWorkflowId(workflowId);
            if(dto.getPledgedRepoInfoList().size()>0){
                model.addAttribute("PledgedRepoInfoListData", JsonUtils.object2Json(dto.getPledgedRepoInfoList()));
                model.addAttribute("formTodoList", true);
            }
            model.addAttribute(GlobalConstant.WORKFLOW_ID, workflowId);
        }
        else{
            if(StringUtil.isNotEmpty(tradeId)){
                if(bussCommonMapperDelegateService!=null){
                    dto = trdRolloverPledgedRepo001Service.getData(tradeId);
                    AssetsTransfer001Dto data = BeanUtil.mapToObject(param, AssetsTransfer001Dto.class);
                    if(StringUtil.isNotEmpty(data)){
                        BigDecimal printAmt = data.getPrintAmt();
                        dto.setPrinAmt(printAmt);
                        dto.setAvaRolloverAmt(printAmt);
                        dto.setInvTransPortf(dto.getPortfolioId());
                        dto.setPortfolioName(data.getPortfolioName());
                        dto.setSplitTradeId(dto.getTradeId());
                        dto.setFinprodId(dto.getTradeId());
                        dto.setTradeDate(iPlatformDate.getSysDate());
                    }
                }
            }
        }
        model.addAttribute("entity",dto);
        this.setSelectOptionToModel(model, DictCode.回购拆借方向, DictCode.回购类型,
                DictCode.交易场所, DictCode.清算速度, DictCode.有效状态, DictCode.币种, DictCode.计息基础,
                DictCode.计息基础,DictCode.回购期限品种);
        model.addAttribute("BACK_LEND_DIRECTION_03", DictCodeList.回购拆借方向_正回购.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_04", DictCodeList.回购拆借方向_逆回购.getValue());
        model.addAttribute("H01",DictCodeList.回购类型_质押式协议回购.getValue());
        model.addAttribute("tradeMarket01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("tradeMarket03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("tradeMarket04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
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
     * 查询组合信息
     * * @return
     */
    @ResponseBody
    @RequestMapping("selectPortfolioId")
    public List<SelectOptionDto> selectPortfolioId() {
         Map<String, Object> param = RequestUtil.getPageParam();
        param.put("userId", LoginUserUtil.getUserName());
        return trdRolloverPledgedRepo001Service.selectPortfolioId(param);
    }
    /**
     * 添加校验
     */
    @ResponseBody
    @RequestMapping("addCheck")
    public AjaxResult addCheck(@RequestBody Map<String,Object>param) throws IOException {
        AjaxResult result = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        List<JoyinMessage> joyinMessages = new ArrayList<>();
        List<TrdRolloverPledgedRepo001Dto> listInfoDtoList = new ArrayList<>();
        TrdRolloverPledgedRepo001Dto listInfoDto = new TrdRolloverPledgedRepo001Dto();
        TrdRolloverPledgedRepo001Dto dto = new TrdRolloverPledgedRepo001Dto();
        if(!StringUtil.isNotEmpty(param.get("portfolioId"))){
            errorList.add(new ValidMessage("portfolioId","请输入组合代码！"));
        }
        if(!StringUtil.isNotEmpty(param.get("rolloverPrinAmt"))){
            errorList.add(new ValidMessage("rolloverPrinAmt","请输入本金金额！"));
        }
        BigDecimal rolloverTradeAmt = BigDecimal.ZERO;
        BigDecimal rolloverPrinAmt = BigDecimal.ZERO;
        BigDecimal avaRolloverAmt = BigDecimal.ZERO;
        BigDecimal sumRolloverPrinAmt = BigDecimal.ZERO;
        String ps = "";
        if(StringUtil.isNotEmpty((String)param.get("rolloverTradeAmt"))){
            rolloverTradeAmt = new BigDecimal((String)param.get("rolloverTradeAmt"));//转仓交易金额
        }
        if(StringUtil.isNotEmpty((String)param.get("rolloverPrinAmt"))){
            rolloverPrinAmt = new BigDecimal((String)param.get("rolloverPrinAmt"));//转仓本金金额
        }
        if(StringUtil.isNotEmpty((String)param.get("avaRolloverAmt"))){
            avaRolloverAmt = new BigDecimal((String)param.get("avaRolloverAmt"));//剩余可转仓本金金额
        }
        if(StringUtil.isNotEmpty((String)param.get("ps"))){
            ps = (String)param.get("ps");
        }
        if(StringUtil.isNotEmpty((String)param.get("sumRolloverPrinAmt"))){
            sumRolloverPrinAmt = new BigDecimal((String)param.get("sumRolloverPrinAmt"));
        }
        if(StringUtil.isNotEmpty(rolloverPrinAmt) && StringUtil.isNotEmpty(avaRolloverAmt) && rolloverPrinAmt.compareTo(avaRolloverAmt)>0){
            errorList.add(new ValidMessage("rolloverPrinAmt","本金金额不能大于剩余未转仓本金金额！"));
        }
        if(StringUtil.isNotEmpty(rolloverPrinAmt) && rolloverPrinAmt.compareTo(BigDecimal.ZERO)<=0){
            errorList.add(new ValidMessage("rolloverPrinAmt","本金金额必须大于0！"));
        }
        else{
            List<FinDealPledgeDto> fdpdlist = new ArrayList<>();
            if(StringUtil.isNotEmpty(param.get("pledgedRepoRolloverList"))){
                fdpdlist = JsonUtils.json2List(JsonUtils.object2Json(param.get("pledgedRepoRolloverList")),FinDealPledgeDto.class);
                if(StringUtil.isNotEmpty(ps) && ps.equals(DictCodeList.回购拆借方向_正回购.getValue())){
                    if(sumRolloverPrinAmt.compareTo(BigDecimal.valueOf(0))==0){
                        errorList.add(new ValidMessage("【转仓交易汇总券面总额】不能为0"));
                    }
                    if(!StringUtil.isEmptyList(fdpdlist)){
                        for (FinDealPledgeDto dto1: fdpdlist) {
                            if(StringUtil.isNotEmpty(dto1.getDesiPledgePrin()) && dto1.getDesiPledgePrin().compareTo(dto1.getPrdPledgePrin())>0){
                                JoyinMessage ms = new JoyinMessage("质押券:"+dto1.getPledgeFinprodId()+"本次转仓指定质押券面总额大于产品组合可质押券面总额,请检查质押券转仓列表",
                                        MessageType.确认消息);
                                joyinMessages.add(ms);
                            }
                        }
                    }
                } else {
                    for (FinDealPledgeDto dto1: fdpdlist) {
                        if(StringUtil.isNotEmpty(dto1.getDesiPledgePrin()) && dto1.getDesiPledgePrin().compareTo(dto1.getPrdPledgePrin())>0){
                            JoyinMessage ms = new JoyinMessage("质押券:"+dto1.getPledgeFinprodId()+"本次转仓指定质押券面总额大于产品组合可质押券面总额,请检查质押券转仓列表",
                                    MessageType.确认消息);
                            joyinMessages.add(ms);
                        }
                    }
                }
//                for (FinDealPledgeDto dto1: fdpdlist) {
//                    if(StringUtil.isNotEmpty(dto1.getDesiPledgePrin()) && dto1.getDesiPledgePrin().compareTo(dto1.getPrdPledgePrin())>0){
//                    	JoyinMessage ms = new JoyinMessage("质押券:"+dto1.getPledgeFinprodId()+"本次转仓指定质押券面总额大于产品组合可质押券面总额,请检查质押券转仓列表",
//                    			MessageType.确认消息);
//                        joyinMessages.add(ms);
//                    }
//                }
            }
        }
        if(joyinMessages!=null && joyinMessages.size()>0) {
        	result.setJoyinMessages(joyinMessages);
        }
        if(errorList!=null && errorList.size()>0){
            result.setStatuCode(500);
            result.setErrorList(errorList);
        }
        else{
            if(StringUtil.isNotEmpty(param.get("ps")) && StringUtil.equals(param.get("ps"),DictCodeList.回购拆借方向_逆回购.getValue())){
                BussTrdAvailableCashQuery bs = new BussTrdAvailableCashQuery();
                if(StringUtil.isNotEmpty(param.get("bussTrdAvailableCashQuery"))){
                    bs = JsonUtils.json2Object(JsonUtils.object2Json(param.get("bussTrdAvailableCashQuery")),BussTrdAvailableCashQuery.class);
                }
                //转仓交易金额不能大于组合剩余本金
                if(StringUtil.isNotEmpty(rolloverTradeAmt)&&StringUtil.isNotEmpty(bs)&&StringUtil.isNotEmpty(bs.getAvalibleCash())){
                    if(rolloverTradeAmt.compareTo(bs.getAvalibleCash())>0){
                        result.setMessage("组合剩余现金不足！", MessageType.确认消息);
                    }
                }
            }
            result.setStatuCode(200);
            listInfoDto = trdRolloverPledgedRepo001Service.setListInfoDto(param);
            listInfoDtoList.add(listInfoDto);
            dto.setPledgedRepoInfoList(listInfoDtoList);
            result.setData(dto);
        }
        return result;
    }
    /**
     * 风险侧算
     * @param model 参数
     * @return
     */
    @RequestMapping("riskMathComm")
    public String riskMathPage(Model model){
        Map<String, Object> param = RequestUtil.getPageParam();
        model.addAttribute("param",param);List<RiskTrialRetDto> riskList = new ArrayList<>();
        if(StringUtil.isNotEmpty(param.get("results")) && !StringUtil.equals((String)param.get("results"),"{}")){
            riskList = JSONArray.parseArray((String)param.get("results"),RiskTrialRetDto.class);
        }
        model.addAttribute("riskList",riskList);

        setSelectOptionToModel(model, DictCode.指标类型, DictCode.监督状态, DictCode.单位,
                DictCode.监督类型, DictCode.监督频率, DictCode.金融产品类型_投管用, DictCode.WIND一级分类_含债券一级分类等,
                DictCode.基金类资产二级类型, DictCode.资产评级, DictCode.申万行业一级类型, DictCode.指标类型映射编码);
        return "/invest/trade/resolute/common/RiskMathComm";
    }
    /**
     * 查询质押券信息
     */
    @ResponseBody
    @RequestMapping("selectPledgedRepoRolloverList")
    public AjaxResult selectPledgedRepoRolloverList(@RequestBody Map<String,Object>param){
        AjaxResult result = new AjaxResult();        
        List<FinDealPledgeDto> pledgedRepoRolloverList = trdRolloverPledgedRepo001Service.selectPledgedRepoRolloverList(param);   
        
        List<FinDealPledgeDto> pledgedRepoRolloverList1 = new ArrayList<>();
        if(StringUtil.isNotEmpty(param.get("pledgedRepoList")) && !StringUtil.equals((String)param.get("pledgedRepoList"),"{}")){
            pledgedRepoRolloverList1 = JSONArray.parseArray((String)param.get("pledgedRepoList"),FinDealPledgeDto.class);
        }
        for(int i=0;i<pledgedRepoRolloverList.size();i++) {
        	pledgedRepoRolloverList.get(i).setAvaPledgePrin(pledgedRepoRolloverList.get(i).getPledgePrin());
    	  if(pledgedRepoRolloverList1.size() != 0) {
    	  for(int m=0;m<pledgedRepoRolloverList1.size();m++) {
    		  if(StringUtil.equals(pledgedRepoRolloverList1.get(m).getPledgeFinprodId(), pledgedRepoRolloverList.get(i).getPledgeFinprodId())) {
    			  if(pledgedRepoRolloverList1.get(m).getDesiPledgePrin() != null ) {
    				  BigDecimal avaPledgePrin = pledgedRepoRolloverList.get(i).getAvaPledgePrin().subtract(pledgedRepoRolloverList1.get(m).getDesiPledgePrin());
    				pledgedRepoRolloverList.get(i).setAvaPledgePrin(avaPledgePrin);
    			  }else {
    				  pledgedRepoRolloverList.get(i).setAvaPledgePrin(pledgedRepoRolloverList.get(i).getAvaPledgePrin());
    			  }
    		  }
    	  }
        }else {
        	pledgedRepoRolloverList.get(i).setAvaPledgePrin(pledgedRepoRolloverList.get(i).getPledgePrin());
        }
            pledgedRepoRolloverList.get(i).setAvaPledgePrin1(pledgedRepoRolloverList.get(i).getAvaPledgePrin());
        }
        result.setData(pledgedRepoRolloverList);
        return result;
    }

    /**
     *  查看质押券
     * @return 质押券信息列表
     */
    @RequestMapping("showPledgedInfo")
    public String showPledgedInfo(Model model){
        Map<String,Object> param = RequestUtil.getPageParam();
        List<FinDealPledgeDto> pledgedRepoRolloverList = new ArrayList<>();
        if(StringUtil.isNotEmpty(param.get("pledgedRepoRolloverList")) && !StringUtil.equals((String)param.get("pledgedRepoRolloverList"),"{}")){
            pledgedRepoRolloverList = JSONArray.parseArray((String)param.get("pledgedRepoRolloverList"),FinDealPledgeDto.class);
        }
        model.addAttribute("finDealPledgeList",pledgedRepoRolloverList);
        return "/invest/trade/transfer/trdrolloverpledgedrepo/PledgedSecInfo";
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
    public AjaxResult check(@RequestBody TrdRolloverPledgedRepo001Dto dto, BindingResult rst) {
        AjaxResult result = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        String confirmMsg ="";
        StringBuilder tips = new StringBuilder();
        if(StringUtil.isEmptyList(dto.getPledgedRepoInfoList())||dto.getPledgedRepoInfoList().size()==0){
            errorList.add(new ValidMessage("列表信息不能空！"));
        }
        else{
            if(StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())
                    && StringUtil.equals(dto.getRepoType(), DictCodeList.回购类型_质押式协议回购.getValue())){
//                BigDecimal sumPrinAmt = BigDecimal.ZERO;

                for (TrdRolloverPledgedRepo001Dto entity:dto.getPledgedRepoInfoList()){
                    BigDecimal sumPrinAmt = BigDecimal.ZERO;
                    for(FinDealPledgeDto deal : entity.getPledgedRepoRolloverList()){
                        sumPrinAmt= ArithmeticUtil.doAdd(deal.getDesiPledgePrin().multiply(deal.getPledgeRatio()).divide(BigDecimal.valueOf(100),2,BigDecimal.ROUND_HALF_UP),sumPrinAmt);
                    }

                    BigDecimal tradeAmt = entity.getRolloverTradeAmt() == null ? BigDecimal.ZERO : entity.getRolloverTradeAmt();
        			BigDecimal prinAmt = entity.getRolloverPrinAmt() == null ? BigDecimal.ZERO : entity.getRolloverPrinAmt();
        			BigDecimal intAmt = entity.getRolloverRepoIntAmt() == null ? BigDecimal.ZERO : entity.getRolloverRepoIntAmt();
        			if (tradeAmt.compareTo(prinAmt.add(intAmt)) != 0) {
        				tips.append("【" + entity.getPortfolioId() + "】交易金额不等于本息和！</br>");
        			}
                    if(prinAmt.compareTo(sumPrinAmt)>0){
                        tips.append("【" + entity.getPortfolioId() + "】本金金额大于转仓交易汇总券面总额</br>");
//                        confirmMsg = "本金金额大于转仓交易汇总券面总额，是否确认提交？";
                    }
                }
//                if(dto.getPrinAmt().compareTo(sumPrinAmt)>0){
//                    confirmMsg = "本金金额大于转仓交易汇总券面总额，是否确认提交？";
//                }
            }
        }
        if (StringUtil.isNotEmpty(dto.getChlId())) {
            Integer count;
            String str = "";
            Map<String, Object> sMap = new HashMap<>();
            for (TrdRolloverPledgedRepo001Dto ptl : dto.getPledgedRepoInfoList()) {
                sMap.put("chlId", dto.getChlId());
                sMap.put("counterId", ptl.getPortfolioId());
                count = trdBondInvestTrade002Service.selectchlTrade(sMap);
                if (count <= 0) {
                    errorList.add(new ValidMessage("交易对手(" + ptl.getPortfolioId() + ")通过一次性通道投资请先进行划款！"));
                }
            }
        }
        if(errorList.size()>0){
            result.setErrorList(errorList);
            return result;
        }
        if (confirmMsg.length() > 0) {
            result.setData(confirmMsg);
        } else if (StringUtil.isNotEmpty(tips)) {
        	result.setData("请确认转仓列表, 是否继续提交？</br>" + tips.toString());
        } else {
            return commonCheck(rst, dto.getOperateType());
        }
        return result;
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
    AjaxResult save(@RequestBody TrdRolloverPledgedRepo001Dto dto) throws WorkFlowException {
        for (TrdRolloverPledgedRepo001Dto listDto:dto.getPledgedRepoInfoList()) {
            if(StringUtil.isBlank(listDto.getTradeId())) {
                if (baseGetBusinessId != null) {
                    BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                    if (StringUtil.equals(dto.getPs(),
                            DictCodeList.回购拆借方向_正回购.getValue())) {
                        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                    } else {
                        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                    }
                    listDto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                } else {
                    listDto.setTradeId(IDGenerator.generate());
                }
            }
        }
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典（根据交易方向与回购类型来综合判断）
        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        } else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        }
        ProcessParameter<TrdRolloverPledgedRepo001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdRolloverPledgedRepo001Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        } else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        // 转仓编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产转仓);
            dto.setZcId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            dto.setZcId(IDGenerator.generate());
        }
        workFlowParameter.setBusinessCode((entity) -> entity.getZcId());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                getMessage("交易编号") + GlobalConstant.OPEN_PARENTHESIS + entity.getZcId() + GlobalConstant.CLOSE_PARENTHESIS,
                getMessage("组合名称") + GlobalConstant.OPEN_PARENTHESIS + entity.getPortfolioName() + GlobalConstant.CLOSE_PARENTHESIS,
                getMessage("交易日") + GlobalConstant.OPEN_PARENTHESIS + DateUtil.format(entity.getTradeDate()) + GlobalConstant.CLOSE_PARENTHESIS));
        dto.setTrader(LoginUserUtil.getUserName());
        dto.setDeptCode(LoginUserUtil.getDeptCode());
        String msg = trdRolloverPledgedRepo001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}

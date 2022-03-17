/*
 * 文件名：TrdDepositInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.service.TrdDepositValuationTrade002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 存款交易新建
 * @author fanshuanghu
 * @version 1.0 2018年10月10日
 * @see TrdDepositValuationTrade002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdDepositValuationTrade002")
@RequestMapping("/valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade002")
public class TrdDepositValuationTrade002Controller extends BaseController {
//	@Autowired
//	private TradeCommonService tradeCommonService;
//    /**
//     * 注入业务主键生产类
//     */
//    @Autowired(required=false)
//    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 注入存款类交易Service
     */
    @Autowired
    private TrdDepositValuationTrade002Service trdDepositValuationTrade002Service;


    @Autowired(required = false)
    private CommonBusinessService commonBusinessService;

    @Autowired
    private IPlatformDate iPlatformDate;


    /**
     * 初始化方法
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.计息基础, DictCode.是否, DictCode.存款类资产类型, DictCode.币种
        		);// 新增资产二、三、四级分类 王贵奇 2019/8/1
        // 一体化对应etliu start
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        // 一体化对应etliu end

        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("FINPROD_TYPE_F07", DictCodeListExt1.金融产品类型_存款.getValue());
        String portfolioId = getParam("portfolioId");
        String tradeId = getParam("tradeId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            TrdDepositValuationTrade001Dto entity = trdDepositValuationTrade002Service.getBussinessEntityByWorkflowId(
                    workflowId);
            model.addAttribute("entity",entity);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("tradeId", entity.getTradeId());
        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdDepositValuationTrade001Dto entity = trdDepositValuationTrade002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }else{
            //初始化值
            TrdDepositValuationTrade001Dto entity = new TrdDepositValuationTrade001Dto();
            entity.setOtherAmt(new BigDecimal("0.00"));   //其他费用
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            model.addAttribute("entity", entity);
        }
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());

        return super.init(model);

    }


    /**
     * 策略计划类交易代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdDepositValuationTrade002Service.createPlanTradeId();
    }

//    /**
//     * 自定义产品代码下拉框
//     * @param request
//     * @return
//     */
//    @RequestMapping("/findPortfolioId")
//    @ResponseBody
//    public Page<SelectOptionDto> findPortfolioId(HttpServletRequest request) {
//        Map<String, Object> param =RequestUtil.getPageParam();
//        //param.put("finprodType", BussinessDictCodeList.金融产品类型_计划类资产.getValue()); //F17
//        param.put("finprodType", "F16"); //测试
//        return trdDepositValuationTrade002Service.findPortfolioId(param);
//    }
//
//
//    /**
//     * 自定义资产代码下拉框
//     * @param request
//     * @return
//     */
//    @RequestMapping("/findFinprodId")
//    @ResponseBody
//    public Page<SelectOptionDto> findFinprodId(HttpServletRequest request) {
//        Map<String, Object> param =RequestUtil.getPageParam();
//        //param.put("finprodType", BussinessDictCodeList.金融产品类型_计划类资产.getValue()); //F17
//        param.put("finprodType", "F01"); //测试
//        return trdDepositValuationTrade002Service.findFinprodId(param);
//    }



    //交易对手下拉框
    @ResponseBody
    @RequestMapping("/getCounterId")
    public Page<SelectOptionDto> getCounterId(){
        return trdDepositValuationTrade002Service.getCounterId(RequestUtil.getPageParam());
    }



    /**
     * 保存按钮点击时的check
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdDepositValuationTrade001Dto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }


    /**
     * @Description:保存
     * @date: 2021/12/30
     * @param:
     * @return:
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdDepositValuationTrade001Dto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        WorkFlowParameter<TrdDepositValuationTrade001Dto> params = new WorkFlowParameter<TrdDepositValuationTrade001Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());

        //设置主键
        if(!StringUtil.isNotEmpty(dto.getTradeId())){
            Date sysDate= DateUtil.platDateFromCache();
            //新建初始化交易编号
            dto.setTradeId(trdDepositValuationTrade002Service.createPlanTradeId());
        }

        params.setBusinessCode(dto.getTradeId());
        params.setBusinessType(DictCodeList.共通业务类型_存款类资产交易.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> SpringContextUtil.getTextValue("交易编号")+"("+ entity.getTradeId() +")"+"-"+SpringContextUtil.getTextValue("交易日") + "(" + DateUtil.format(entity.getTradeDate()) + ")"
        );
        //给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<TrdDepositValuationTrade001Dto> parameter = new ProcessParameter<TrdDepositValuationTrade001Dto>();
        parameter.setWorkFlowParameter(params);
        //系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        String msg = trdDepositValuationTrade002Service.start(parameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 资金账户下拉框
     *
     * @return 资产代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getTrdAcctId")
    public List<SelectOptionDto> getTrdAcctId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("portfolioId",param.get("finprodId"));
        return commonBusinessService.getTrdAcctId(param);
    }

}

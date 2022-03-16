/*
 * 文件名：TrdFxSpotRateInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfxswapvaluationtrade.controller;

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
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdfxswapvaluationtrade.dto.TrdFxSwapValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdfxswapvaluationtrade.service.TrdFxSwapValuationTrade002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @Description:外汇远期交易新建
 * @auther: yuzj
 * @date: 2022/01/11
 * @param:
 * @return:
 */
@Controller
@PageId("TrdFxSwapValuationTrade002")
@RequestMapping("/valuation/trademanage/trdfxswapvaluationtrade/TrdFxSwapValuationTrade002")
public class TrdFxSwapValuationTrade002Controller extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
     * 注入外汇远期service002
     */
    @Autowired
    private TrdFxSwapValuationTrade002Service trdFxSwapValuationTrade002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;


    /**
     * 初始化方法
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向,
                DictCode.是否, DictCode.币种,DictCode.货币对,DictCode.期限,DictCode.借贷方向_表外,
                DictCode.外汇交易模式,DictCode.结算方式,DictCode.我方角色,DictCode.外汇掉期期限类型);

        // 一体化对应etliu start
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        // 一体化对应etliu end
        
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("FINPROD_TYPE_F17", DictCodeListExt1.金融产品类型_计划类资产.getValue());
        String portfolioId = getParam("portfolioId");
        String tradeId = getParam("tradeId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if(StringUtil.isNotEmpty(workflowId)){
            TrdFxSwapValuationTradeDto entity= trdFxSwapValuationTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdFxSwapValuationTradeDto entity = trdFxSwapValuationTrade002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }else{
            //初始化值
            TrdFxSwapValuationTradeDto entity = new TrdFxSwapValuationTradeDto();
            entity.setProcedureAmt(new BigDecimal("0.00"));      //交易手续费
            entity.setSettleServiceAmt(new BigDecimal("0.00"));  //结算服务费
            entity.setFeeAmtQT(new BigDecimal("0.00"));          //其他费用
            model.addAttribute("entity", entity);
        }
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        return super.init(model);
    }

    /**
     * 资产代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 15:34
     * @param:
     * @return:
     */
    @RequestMapping("selectFinprodId")
    @ResponseBody
    public List<SelectOptionDto> selectFinprodId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        // 查询组合添加权限限制
        //param.put("assetType", BussinessDictCodeList.金融产品类型_计划类资产.getValue()); //F17
        param.put("assetType", "F01"); //测试
        return commonBusinessService.getAssetId(param);
    }


    /**
     * 自定义产品代码下拉框
     * @param request
     * @return
     */
    @RequestMapping("/findPortfolioId")
    @ResponseBody
    public Page<SelectOptionDto> findPortfolioId(HttpServletRequest request) {
        Map<String, Object> param =RequestUtil.getPageParam();
        //param.put("finprodType", BussinessDictCodeList.金融产品类型_计划类资产.getValue()); //F17
        param.put("finprodType", "F01"); //测试
        return trdFxSwapValuationTrade002Service.findPortfolioId(param);
    }

    /**
     * 自定义资产代码下拉框
     * @param request
     * @return
     */
    @RequestMapping("/findFinprodId")
    @ResponseBody
    public Page<SelectOptionDto> findFinprodId(HttpServletRequest request) {
        Map<String, Object> param =RequestUtil.getPageParam();
        //param.put("assetType", BussinessDictCodeList.金融产品类型_计划类资产.getValue()); //F17
        param.put("assetType", "F01"); //测试
        return trdFxSwapValuationTrade002Service.findFinprodId(param);
    }

    /**
     * @Description:画面点击保存按钮时check
     * @auther: yuzj
     * @date: 2021/12/30
     * @param:
     * @return:
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdFxSwapValuationTradeDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        //add by yuzj on 2021-12-02 end
        AjaxResult ar = commonCheck(result, dto.getOperateType());
        return ar;
    }


    /**
     * @Description:资金账户信息
     * @auther: yuzj
     * @date: 2021/12/30
     * @param:
     * @return:
     */
    @RequestMapping("/getTrdAcctId")
    @ResponseBody
    public List<SelectOptionDto> getTrdAcctId(){
        return commonBusinessService.getTrdAcctId(RequestUtil.getPageParam());
    }


    /**
     * @Description:保存
     * @auther: yuzj
     * @date: 2021/12/30
     * @param:
     * @return:
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdFxSwapValuationTradeDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        WorkFlowParameter<TrdFxSwapValuationTradeDto> params = new WorkFlowParameter<TrdFxSwapValuationTradeDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getTradeId());
        params.setBusinessType(DictCodeListExt2.共通业务类型_外汇远期投资交易.getValue());

        //设置主键
        if(!StringUtil.isNotEmpty(dto.getTradeId())){
            Date sysDate= DateUtil.platDateFromCache();
            //新建初始化交易编号
            dto.setTradeId(trdFxSwapValuationTrade002Service.createPlanTradeId());
        }

        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> SpringContextUtil.getTextValue("TrdFxSwapValuationTrade001.tradeId")+"("+ entity.getTradeId() +")"+"-"+SpringContextUtil.getTextValue("TrdFxSwapRateValuationTrade002.tradeDate") + "(" + DateUtil.format(entity.getTradeDate()) + ")"
        );
        //给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<TrdFxSwapValuationTradeDto> parameter = new ProcessParameter<TrdFxSwapValuationTradeDto>();
        parameter.setWorkFlowParameter(params);
        //系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        String msg = trdFxSwapValuationTrade002Service.start(parameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 策略计划类交易代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdFxSwapValuationTrade002Service.createPlanTradeId();
    }


}

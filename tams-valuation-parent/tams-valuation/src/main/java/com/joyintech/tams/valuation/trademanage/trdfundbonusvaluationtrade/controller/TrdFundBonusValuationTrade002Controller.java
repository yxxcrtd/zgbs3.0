
package com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.dto.TrdFundBonusValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.service.TrdFundBonusValuationTrade002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:基金存续期新建
 * @auther: yuzj
 * @date: 2022/02/11
 * @param:
 * @return:
 */
@Controller
@PageId("TrdFundBonusValuationTrade002")
@RequestMapping("/valuation/trademanage/trdfundbonusvaluationtrade/TrdFundBonusValuationTrade002")
public class TrdFundBonusValuationTrade002Controller extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
     * 注入计划类service
     */
    @Autowired
    private TrdFundBonusValuationTrade002Service trdFundBonusValuationTrade002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;


    /**
     * 初始化方法
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向,DictCode.交易场所,
                DictCode.是否, DictCode.币种);

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
            TrdFundBonusValuationTradeDto entity= trdFundBonusValuationTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdFundBonusValuationTradeDto entity = trdFundBonusValuationTrade002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }else{
            //初始化值
            TrdFundBonusValuationTradeDto entity = new TrdFundBonusValuationTradeDto();
            entity.setFeeAmt(new BigDecimal("0.00"));            //交易费用
            entity.setFeeAmtQT(new BigDecimal("0.00"));
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            model.addAttribute("entity", entity);
        }

        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        return super.init(model);
    }


    /**
     * @Description:产品代码下拉框
     * @auther: yuzj
     * @date: 2021/12/29
     * @param:
     * @return:
     */
    @RequestMapping("selectPortfolioId")
    @ResponseBody
    public List<SelectOptionDto> selectPortfolioId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        // 查询组合添加权限限制
        //param.put("finprodType", BussinessDictCodeList.金融产品类型_计划类资产.getValue()); //F17
        param.put("finprodType", "F01"); //测试
        return commonBusinessService.getProdId(param);
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
//        param.put("finprodType", "F01"); //测试
        return trdFundBonusValuationTrade002Service.findPortfolioId(param);
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
        return trdFundBonusValuationTrade002Service.findFinprodId(param);
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
            ValidateGroupEdit.class}) TrdFundBonusValuationTradeDto dto, BindingResult result) {
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
            ValidateGroupEdit.class}) TrdFundBonusValuationTradeDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        WorkFlowParameter<TrdFundBonusValuationTradeDto> params = new WorkFlowParameter<TrdFundBonusValuationTradeDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());

        //设置主键
        if(!StringUtil.isNotEmpty(dto.getTradeId())){
            Date sysDate= DateUtil.platDateFromCache();
            //新建初始化交易编号
            dto.setTradeId(trdFundBonusValuationTrade002Service.createFundBonusTradeId());
        }

        params.setBusinessCode(dto.getTradeId());
        params.setBusinessType(DictCodeList.共通业务类型_基金类资产交易.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> SpringContextUtil.getTextValue("TrdFundBonusValuationTrade001.tradeId")+"("+ entity.getTradeId() +")"+"-"+SpringContextUtil.getTextValue("TrdFundValuationTrade002.tradeDate") + "(" + DateUtil.format(entity.getTradeDate()) + ")"
        );
        //给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<TrdFundBonusValuationTradeDto> parameter = new ProcessParameter<TrdFundBonusValuationTradeDto>();
        parameter.setWorkFlowParameter(params);
        //系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        String msg = trdFundBonusValuationTrade002Service.start(parameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 策略基金存续期代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createFundBonusTradeId")
    public String createFundBonusTradeId() {
        return trdFundBonusValuationTrade002Service.createFundBonusTradeId();
    }


}

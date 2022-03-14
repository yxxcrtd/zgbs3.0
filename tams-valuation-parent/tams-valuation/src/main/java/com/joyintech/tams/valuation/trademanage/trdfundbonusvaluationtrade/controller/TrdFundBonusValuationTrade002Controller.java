
package com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.dto.TrdFundBonusValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.service.TrdFundBonusValuationTrade002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

        String tradeId = getParam("tradeId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);

        if(StringUtil.isNotEmpty(workflowId)){
            TrdFundBonusValuationTradeDto entity= trdFundBonusValuationTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdFundBonusValuationTradeDto entity = trdFundBonusValuationTrade002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }

        return super.init(model);
    }

    /**
     * 资产代码下拉框
     *
     * @return 资产代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAssetId")
    public List<SelectOptionDto> getAssetId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("assetType","F06");
        return commonBusinessService.getAssetId(param);
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

        if(DictCodeList.交易方向_现金分红.getValue().equals(dto.getPs())){
            // 设置流程业务说明, entity即传入的dto
            params.setComment(
                    entity -> SpringContextUtil.getTextValue("TrdFundBonusValuationTrade002.tradeId")+"("+ entity.getTradeId() +")"+"-"+SpringContextUtil.getTextValue("TrdFundValuationTrade002.tradeDate") + "(" + DateUtil.format(entity.getTradeDate()) + ")"
            );
        }else if(DictCodeList.交易方向_红利再投.getValue().equals(dto.getPs())){
            // 设置流程业务说明, entity即传入的dto
            params.setComment(
                    entity -> SpringContextUtil.getTextValue("TrdFundBonusValuationTrade002.tradeId")+"("+ entity.getTradeId() +")"+"-"+SpringContextUtil.getTextValue("TrdFundValuationTrade002.tradeDate") + "(" + DateUtil.format(entity.getTradeDate_hlzt()) + ")"
            );
        }else if( DictCodeList.交易方向_份额强增.getValue().equals(dto.getPs())
                || DictCodeList.交易方向_份额强减.getValue().equals(dto.getPs())){
            // 设置流程业务说明, entity即传入的dto
            params.setComment(
                    entity -> SpringContextUtil.getTextValue("TrdFundBonusValuationTrade002.tradeId")+"("+ entity.getTradeId() +")"+"-"+SpringContextUtil.getTextValue("TrdFundValuationTrade002.tradeDate") + "(" + DateUtil.format(entity.getTradeDate_share()) + ")"
            );
        }

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

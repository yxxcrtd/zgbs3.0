/*
 * 文件名：TrdLendInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.controller;

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
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.dto.TrdFixIncomeValuDuration001Dto;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.service.TrdFixIncomeValuDuration002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 固收类存续期新建
 */
@Controller
@PageId("TrdFixIncomeValuDuration002")
@RequestMapping("/valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration002")
public class TrdFixIncomeValuDuration002Controller extends BaseController {
    /**
     * 注入固收类存续期Service
     */
    @Autowired
    private TrdFixIncomeValuDuration002Service trdFixIncomeValuDuration002Service;

    @Autowired(required = false)
    private CommonBusinessService commonBusinessService;

    public String init(Model model) {
        this.setSelectOptionToModel(model,DictCode.交易方向,DictCode.投资目的,DictCode.是否, DictCode.币种);
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());

        //平台日
        model.addAttribute("iPlatformDate", DateUtil.parse(DateUtil.getSysDate()));

        String tradeId = getParam("tradeId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);

        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            TrdFixIncomeValuDuration001Dto entity = trdFixIncomeValuDuration002Service.getBussinessEntityByWorkflowId(
                    workflowId);
            if(!StringUtil.isNotEmpty(entity.getTradeId())){
                entity.setTradeId("提交后自动生成，无需填写");
            }
            model.addAttribute("entity",entity);
            model.addAttribute("workflowId", workflowId);
        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            Map<String, Object> map = new HashMap<>();
            map.put("tradeId",tradeId);
            map.put("busiType",DictCodeList.业务类型_计划类资产交易_类债券资产交易.getValue());
            TrdFixIncomeValuDuration001Dto entity = trdFixIncomeValuDuration002Service.queryTradeInfoByTradeId(map);
            model.addAttribute("entity", entity);
        }

        return super.init(model);
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
        String[] accTypeArr =  {DictCodeListExt1.资金账户类型_产品托管户.getValue(),
                DictCodeListExt1.资金账户类型_券商资金账户.getValue(),
                DictCodeListExt1.资金账户类型_结算备付金账户.getValue(),};
        param.put("finprodId",param.get("prodId"));
        param.put("accTypeArr",accTypeArr);
        return commonBusinessService.getTrdAcctId(param);
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
            ValidateGroupEdit.class}) TrdFixIncomeValuDuration001Dto dto, BindingResult result) {
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
            ValidateGroupEdit.class}) TrdFixIncomeValuDuration001Dto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        WorkFlowParameter<TrdFixIncomeValuDuration001Dto> params = new WorkFlowParameter<TrdFixIncomeValuDuration001Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());

        params.setBusinessCode(dto.getAssetId());  //资产代码
        params.setBusinessType(DictCodeListExt3.共通业务类型_固收类资产存续期.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment((entity) -> WorkFlowUtil.concatComment(
                        getMessage("资产代码") + GlobalConstant.COLON + entity.getAssetId(),
                        getMessage("交易日期") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate()))
        );
        //给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<TrdFixIncomeValuDuration001Dto> parameter = new ProcessParameter<TrdFixIncomeValuDuration001Dto>();
        parameter.setWorkFlowParameter(params);
        //系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        String msg = trdFixIncomeValuDuration002Service.start(parameter, dto);
        return new AjaxResult(msg);
    }

}

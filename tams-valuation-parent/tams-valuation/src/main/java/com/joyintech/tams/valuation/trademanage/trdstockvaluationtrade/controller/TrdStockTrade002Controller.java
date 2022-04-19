/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.accounadjustmanage.manualvouchertemplete.dto.ValManualVoucherTemplete003AddDto;
import com.joyintech.tams.valuation.accounadjustmanage.manualvouchertemplete.dto.ValManualVoucherTemplete003AddValidatorDto;
import com.joyintech.tams.valuation.accounadjustmanage.manualvouchertemplete.dto.ValManualVoucherTemplete003Dto;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.service.BaseCommonService;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto.TrdStockTradeAddDto;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto.TrdStockTradeDto;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.service.TrdStockTrade002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 债券交易新建
 *
 * @author Zane.Y
 * @version 1.0 2018年9月7日
 * @see TrdStockTrade002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdStockTrade002")
@RequestMapping("/valuation/trademanage/trdstockvaluationtrade/TrdStockTrade002")
public class TrdStockTrade002Controller extends BaseController {

    @Autowired
    private TrdStockTrade002Service trdStockTrade002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private BaseCommonService baseCommonService;
    
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.币种, DictCode.是否);
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String tradeId = getParam("tradeId");

        //平台日
        model.addAttribute("iPlatformDate", DateUtil.parse(DateUtil.getSysDate()));

        if(StringUtil.isNotEmpty(workflowId)){
            TrdStockTradeDto entity= trdStockTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            if(!StringUtil.isNotEmpty(entity.getTradeId())){
                entity.setTradeId("提交后自动生成，无需填写");
            }
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);
        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdStockTradeDto entity = trdStockTrade002Service.queryTradeInfoByTradeId(tradeId);
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
        param.put("assetType","F10");
        return commonBusinessService.getAssetId(param);
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
     * 点击新增一个空的分录行
     *
     * @param dto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("doAdd")
    public AjaxResult doAdd(@Valid @RequestBody TrdStockTradeAddDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        TrdStockTradeAddDto dto1 = new TrdStockTradeAddDto();
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setData(dto1);
        return ajaxResult;
    }

    /**
     * 根据流程id，获取对应的凭证新建时的分录信息
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("/getAddList")
    @ResponseBody
    public List<ValManualVoucherTemplete003AddDto> getAddList() {

        List<ValManualVoucherTemplete003AddDto> returnList = new ArrayList<>();

        Map<String,Object> map= RequestUtil.getPageParam();

        String workflowId = (String)map.get("workflowId");

        String tmpCode = String.valueOf(map.get("tmpCode"));

//        String flag=getFlag();
//
//        //根据流程id获取对应的凭证新建时的分录信息
//        if(workflowId!=null && workflowId.length()>0){
//            TrdStockTradeDto entity= trdStockTrade002Service.getBussinessEntityByWorkflowId(workflowId);
//            returnList = entity.getListAddDto();
//        }else if(!StringUtil.isNotEmpty(workflowId) && tmpCode!=null && tmpCode.length()>0){
//            List<ValManualVoucherTemplete003AddDto> entity = trdStockTrade002Service.searchList(tmpCode);
//            returnList = entity;
//        }else if(flag!="" &&"saveTmp".equals(flag)){
//            returnList=getListAddDto();
//            setFlag("");
//        }

            // 若为空给予一个空list，不能为null，否则前台报错。
            returnList = new ArrayList<ValManualVoucherTemplete003AddDto>();

        return returnList;
    }

    /**
     * 确认校验
     *
     * @param TrdStockTradeDto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdStockTradeDto TrdStockTradeDto,
                              BindingResult result) {
        return commonCheck(result, TrdStockTradeDto.getOperateType());
    }


    /**
     * 保存提交
     *
     * @param dto
     * @return 画面输入参数
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("save")
    @ResponseBody
    AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdStockTradeDto dto, BindingResult br)
        throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdStockTradeDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdStockTradeDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getAssetId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("资产代码") + GlobalConstant.COLON + entity.getAssetId(),
            getMessage("交易日") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate())));

        String msg = trdStockTrade002Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

}

package com.joyintech.tams.invest.period.trdoptionduration.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.period.trdoptionduration.dto.OptionDurationDetailDto;
import com.joyintech.tams.invest.period.trdoptionduration.service.TrdOptionDuration002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 期权存续期
 * @author panbin
 * @version 2019年12月25日
 * @see TrdOptionDuration001Controller
 */
@PageId("TrdOptionDuration002")
@RequestMapping("/invest/period/trdoptionduration/TrdOptionDuration002")
@Controller
public class TrdOptionDuration002Controller extends BaseController {

    @Autowired
    private TrdOptionDuration002Service trdOptionDuration002Service;
    @Autowired
    private IPlatformDate iPlatformDate;

    @Override
    public String init(Model model) {
        super.setSelectOptionToModel(model, DictCode.交易方向,DictCode.行权方式,
                DictCode.结构类型,DictCode.期权费支付类型,DictCode.是否,DictCode.收付方向);
        OptionDurationDetailDto entity = new OptionDurationDetailDto();
        Map<String, Object> map = RequestUtil.getPageParam();
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String finprodId = this.getParam("finprodId");
        String xqType = getParam("xqType");
        model.addAttribute("finprodId", finprodId);
        model.addAttribute("workflowId", workflowId);
        if (StringUtil.isNotEmpty(workflowId)){ //流程中的页面
            entity = trdOptionDuration002Service.getBussinessEntityByWorkflowId(workflowId);
            //update zhangweiqi 2020-02-12 流程中显示原编号begin
            if(StringUtil.equals(entity.getOperateType(), "revoke")){
            if(StringUtil.isNotEmpty(entity.getFinprodId()))
            entity.setTotleTradeId(entity.getFinprodId());
            if(StringUtil.isNotEmpty(entity.getpTradeId()))
            entity.setBusiCode(entity.getpTradeId());
            if(entity.getZfDate()==null)
            entity.setZfDate(entity.getXqDate());
            }
            //update zhangweiqi 2020-02-12 流程中显示原编号end
            entity.setMdate(DateUtil.parse(entity.getMdateStr()));
            model.addAttribute("totleTradeId", entity.getTotleTradeId());
        }
        else {//查看和编辑页面
            String pageModel = (String) map.get("pageModel");
            map.put("sysDate", DateUtil.getSysDate());
            entity = trdOptionDuration002Service.getMyEntity(map);
            entity.setIsfqxq(DictCodeList.是否_否.getValue());
            if(entity.getXqDate() == null){
                entity.setXqDate(iPlatformDate.getSysDate());
            }
            if(entity.getZfDate() == null){
                entity.setZfDate(iPlatformDate.getSysDate());
            }
            if(StringUtil.isEmpty(entity.getSffx())){
                entity.setSffx(DictCodeList.收付方向_收入.getValue());
            }
           if(StringUtil.isNotEmpty(xqType)){
               entity.setXqType(xqType);
           }
           if(!"待处理".equals(entity.getDataStatus())){
               model.addAttribute("totleTradeId", entity.getTotleTradeId());
           }
            model.addAttribute("entity", entity);
        }
        if(CollectionUtils.isNotEmpty(entity.getBussTrdTransferOrder())) {
            model.addAttribute("entity_bussTrdTransferOrderDto", JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
        }
        model.addAttribute(GlobalConstant.ENTITY,entity);
        model.addAttribute("GEN_PAY_ORDERS_AUTO", CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"));
        return super.init(model);
    }


    /**
     * 画面点击保存按钮时check
     *
     * @param dto 实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) OptionDurationDetailDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存
     *
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) OptionDurationDetailDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<OptionDurationDetailDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<OptionDurationDetailDto> params = new WorkFlowParameter<>();
        if("FQXQ".equals(dto.getXqType())){
            params.setPageId("TrdOptionDuration003");
        }else{
            params.setPageId(this.getPageId());
        }
        params.setElementId(dto.getElementId());
        //获取汇总交易编号
        dto.setBusiCode(trdOptionDuration002Service.getTradeId(dto.getXqType()));
        dto.setTotleTradeId(dto.getTotleTradeId());
        params.setBusinessCode(dto.getBusiCode());
        if(StringUtil.equals(dto.getXqType(),"DQXQ")){
            params.setBusinessType(DictCodeListExt2.共通业务类型_期权存续期到期交易.getValue());
            params.setComment(entity -> WorkFlowUtil.concatComment(
                    "交易编号:" + entity.getTotleTradeId() + "(" + SysDataUtil.getNameFromDataDict(DictCode.共通业务类型,
                            DictCodeListExt2.共通业务类型_期权存续期到期交易.getValue()) + ")"));
        }else if(StringUtil.equals(dto.getXqType(),"TQXQ")){
            params.setBusinessType(DictCodeListExt2.共通业务类型_期权存续期提前行权交易.getValue());
            params.setComment(entity -> WorkFlowUtil.concatComment(
                    "交易编号:" + entity.getTotleTradeId() + "(" + SysDataUtil.getNameFromDataDict(DictCode.共通业务类型,
                            DictCodeListExt2.共通业务类型_期权存续期提前行权交易.getValue()) + ")"));
        }else{
            params.setBusinessType(DictCodeListExt2.共通业务类型_期权存续期放弃行权交易.getValue());
            params.setComment(entity -> WorkFlowUtil.concatComment(
                    "交易编号:" + entity.getTotleTradeId() + "(" + SysDataUtil.getNameFromDataDict(DictCode.共通业务类型,
                            DictCodeListExt2.共通业务类型_期权存续期放弃行权交易.getValue()) + ")"));
        }

        // 设置流程业务说明, entity即传入的dto

        processParameter.setWorkFlowParameter(params);
        String msg = trdOptionDuration002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }


    @RequestMapping("/getoption")
    @ResponseBody
    public Page<SelectOptionDto> getoption() {
        return trdOptionDuration002Service.getoption(RequestUtil.getPageParam());
    }
}

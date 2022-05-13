package com.joyintech.tams.cfets.manualsettle.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.manualsettle.dto.*;
import com.joyintech.tams.cfets.manualsettle.service.CfetsManualSettleService;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@PageId("CfetsManualSettle")
@RequestMapping("/cfets/manualsettle/CfetsManualSettle")
@Api(tags = "银行间手工交收")
public class CfetsManualSettleController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsManualSettleController.class);

    @Autowired
    private CfetsManualSettleService cfetsManualSettleService;

    @Autowired
    private IPlatformDate platformDateImpl;

    @Override
    public String init(Model model) {

        CfetsManualSettleDto entity = null;
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        // 代办中过来
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            entity = cfetsManualSettleService.getBussinessEntityByWorkflowId(
                    workflowId);
        }
        model.addAttribute(GlobalConstant.ENTITY, entity);

        if(entity != null){
            List<CfetsManualSettleDto> settleinfoList = new ArrayList<>();
            settleinfoList.add(entity);
            model.addAttribute("settleinfoList", settleinfoList);
        }
        return super.init(model);
    }

    /**
     * 手动交收check
     * 前台传入整条数据
     * @param cfetsRequest br
     * @return Message<AjaxResult>
     * @author sjj
     */
    @ResponseBody
    @ApiOperation(value = "交收", notes = "交收")
    @PostMapping("doManualSettleCheck")
    public Message<AjaxResult> doManualSettleCheck(@RequestBody CfetsManualSettleRequest cfetsRequest, BindingResult br) {
        //画面Check
        AjaxResult ajaxResult = cfetsManualSettleService.checkManualSettleSave(cfetsRequest, br);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }
    /**
     * 手动交收
     * 前台传入整条数据
     * @param cfetsRequest br
     * @return Message<AjaxResult>
     * @author sjj
     */
    @ResponseBody
    @ApiOperation(value = "交收", notes = "交收")
    @PostMapping("doManualSettle")
    public Message<AjaxResult> doManualSettle(@RequestBody CfetsManualSettleRequest cfetsRequest, BindingResult br)
            throws Exception {

        List<CfetsManualSettleDto> dtoList = cfetsRequest.getSelectedItem();
        //保存
        ProcessParameter<CfetsManualSettleDto> processParameter = new ProcessParameter<CfetsManualSettleDto>();
        WorkFlowParameter<CfetsManualSettleDto> params = new WorkFlowParameter<CfetsManualSettleDto>();
        params.setPageId("CfetsManualSettle");
        params.setElementId(dtoList.get(0).getElementId());
        params.setBusinessCode(item -> item.getTradeId());
        params.setBusinessType(DictCodeListExt3.共通业务类型_交收管理.getValue());

        String finalOpertType;

        if(dtoList.get(0)!=null && StringUtil.equals(dtoList.get(0).getButtonType(),DictCodeListExt3.交收操作类型_回溯.getValue())){
            finalOpertType = DictCodeListExt3.交收操作类型_回溯.getCodeName();
        }else if(dtoList.get(0)!=null && StringUtil.equals(dtoList.get(0).getButtonType(),DictCodeListExt3.交收操作类型_交收.getValue())){
            finalOpertType = DictCodeListExt3.交收操作类型_交收.getCodeName();
        }else{
            finalOpertType = DictCodeListExt3.交收操作类型_交收失败.getCodeName();
        }

        String finalOpertType1 = finalOpertType;
        DecimalFormat format = new DecimalFormat("#,###.00");

        params.setComment((entity) -> {
            String settleArg = getMessage("交收金额") + GlobalConstant.COLON + (entity.getSettleAmt()== null?"":format.format(entity.getSettleAmt())) + "元";
            if(StringUtil.equals(entity.getPs(), BussinessDictCodeList.交易方向_买入.getValue())){
                settleArg = getMessage("交收数量") + GlobalConstant.COLON + entity.getSettleNum();
            }
            if(StringUtil.isNotEmpty(entity.getFinprodAbbr())) {
                return WorkFlowUtil.concatComment(
                        getMessage("交收日期") + GlobalConstant.COLON + entity.getSettleDate(),
                        getMessage("原业务编号") + GlobalConstant.COLON + entity.getTradeId(),
                        getMessage("业务类型") + GlobalConstant.COLON + entity.getTgBusiTypeStr(),
                        (getMessage("资产简称") + GlobalConstant.COLON + entity.getFinprodAbbr()),
                        getMessage("交收操作") + GlobalConstant.COLON + finalOpertType1,
                        settleArg);
            }else {
                return WorkFlowUtil.concatComment(
                        getMessage("交收日期") + GlobalConstant.COLON + entity.getSettleDate(),
                        getMessage("原业务编号") + GlobalConstant.COLON + entity.getTradeId(),
                        getMessage("业务类型") + GlobalConstant.COLON + entity.getTgBusiTypeStr(),
                        getMessage("交收操作") + GlobalConstant.COLON + finalOpertType1,
                        settleArg);
            }
        });
        processParameter.setWorkFlowParameter(params);
        try{
            String msg = cfetsManualSettleService.start(processParameter,
                    dtoList.toArray(new CfetsManualSettleDto[dtoList.size()]));
            return new Message<AjaxResult>().success().setMessage("处理完成");
        }catch (Exception e) {
            logger.error("交收失败",e);
            //return new Message<AjaxResult>().fail().setMessage("交收失败");
            return PositionUtil.ajaxErrorReturn(e);
        }
    }

}

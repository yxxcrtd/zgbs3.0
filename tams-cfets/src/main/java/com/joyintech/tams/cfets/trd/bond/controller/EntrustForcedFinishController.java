package com.joyintech.tams.cfets.trd.bond.controller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.trd.bond.dto.EntrustForcedFinishDto;
import com.joyintech.tams.cfets.trd.bond.dto.EntrustRequest;
import com.joyintech.tams.cfets.trd.bond.service.EntrustForcedFinishService;
import com.joyintech.tams.cfets.trd.bond.service.IbuQuoteBondService;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zznn
 * @date 2021/12/23
 */
@Controller
@PageId("EntrustForcedFinish")
@RequestMapping("/cfets/bankuplink/bond/EntrustForcedFinish")
@Api(tags = "指令强制完成接口")
public class EntrustForcedFinishController extends BaseController {

    @Autowired
    private EntrustForcedFinishService entrustForcedFinishService;
    @Autowired
    private IbuQuoteBondService ibuQuoteBondService;

    @Override
    public String init(Model model) {
        EntrustForcedFinishDto entrustForcedFinishDto = new EntrustForcedFinishDto();
        if (com.joyintech.framework.util.StringUtil.equals(this.getParam("workflowStatus"), BussinessDictCodeList.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && com.joyintech.framework.util.StringUtil.isNotBlank(getParam(GlobalConstant.WORKFLOW_ID)))) {
            entrustForcedFinishDto = entrustForcedFinishService.getBussinessEntityByWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
            if(null!=entrustForcedFinishDto){
                EntrustRequest entrustRequest = new EntrustRequest();
                entrustRequest.setEntrustId(entrustForcedFinishDto.getEntrustId());
                entrustForcedFinishDto.setEntrustList(ibuQuoteBondService.listEntrust(entrustRequest,entrustForcedFinishDto.getLoginUserId()));
                entrustForcedFinishDto.setDealTransactionList(ibuQuoteBondService.listDealTransactionDetails(entrustForcedFinishDto.getEntrustId()));
                if(StringUtil.isNotEmpty(entrustForcedFinishDto.getDealTransactionList())){
                    entrustForcedFinishDto.getDealTransactionList().forEach(item->{
                        item.setMatureYield(ArithmeticUtil.multiply(item.getMatureYield(),new BigDecimal(100)));
                        item.setExerYield(ArithmeticUtil.multiply(item.getExerYield(),new BigDecimal(100)));
                    });
                }
            }
        }
        model.addAttribute(GlobalConstant.ENTITY, entrustForcedFinishDto);
        return super.init(model);
    }


    @ApiOperation(value = "强制完成检查", notes = "强制完成检查")
    @ResponseBody
    @RequestMapping("/doForcedFinishCheck")
    public Message<AjaxResult> doForcedFinishCheck(@RequestBody Map<String,Object> param) {
        BondQuoteInfoDto bondQuoteInfoDto = new BondQuoteInfoDto();
        AjaxResult ar = null;
        List<String> entrustIds = StringUtil.splitToList(",",(String)param.get("entrustIds"));
        param.put("entrustIds",entrustIds);
        return new Message<AjaxResult>().success().setData(ibuQuoteBondService.doForcedFinishCheck(param,false));
    }

    @ApiOperation(value = "登记提交(保存)", notes = "登记提交(保存)")
    @RequestMapping("save")
    @ResponseBody
    public Message<AjaxResult> save(@RequestBody EntrustForcedFinishDto dto, BindingResult br) throws Exception{
        if (br.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(br));
        }
        List<String> entrustIds = StringUtil.splitToList(",",dto.getEntrustIds());
        String remark = dto.getRemark();
        List<EntrustForcedFinishDto> result = new ArrayList<>();
        entrustIds.forEach(item ->{
            EntrustForcedFinishDto entrustForcedFinishDto = new EntrustForcedFinishDto();
            entrustForcedFinishDto.setEntrustId(item);
            entrustForcedFinishDto.setLoginUserId(LoginUserUtil.getUserName());
            entrustForcedFinishDto.setRemark(remark);
            result.add(entrustForcedFinishDto);
        });
        ProcessParameter<EntrustForcedFinishDto> processParameter = new ProcessParameter<EntrustForcedFinishDto>();
        WorkFlowParameter<EntrustForcedFinishDto> params = new WorkFlowParameter<EntrustForcedFinishDto>();
        params.setPageId("EntrustForcedFinish");
        params.setElementId("EntrustForcedFinish_btn_submit");
        params.setBusinessType(DictCodeListExt3.共通业务类型_指令强制完成.getValue());
        params.setBusinessCode(item->item.getEntrustId());
        params.setComment(entity -> getMessage("指令代码") + "(" + entity.getEntrustId()+")");
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.MODIFY);
        String msg = entrustForcedFinishService.start(processParameter,
                    result.toArray(new EntrustForcedFinishDto[result.size()]));
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }
}


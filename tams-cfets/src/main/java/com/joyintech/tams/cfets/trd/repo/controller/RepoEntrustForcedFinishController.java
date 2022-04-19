package com.joyintech.tams.cfets.trd.repo.controller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.trd.repo.dto.RepoEntrustForcedFinishDto;
import com.joyintech.tams.cfets.trd.repo.dto.RepoEntrustRequest;
import com.joyintech.tams.cfets.trd.repo.service.IbuQuoteRepoService;
import com.joyintech.tams.cfets.trd.repo.service.RepoEntrustForcedFinishService;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 质押式回购指令强制完成接口
 *
 * @author jiangpeng
 * @date 2022/01/10
 */
@Controller
@PageId("RepoEntrustForcedFinish")
@RequestMapping("/cfets/bankuplink/repo/RepoEntrustForcedFinish")
@Api(tags = "质押式回购指令强制完成接口")
public class RepoEntrustForcedFinishController extends BaseController {

    @Autowired
    private RepoEntrustForcedFinishService repoEntrustForcedFinishService;

    @Autowired
    private IbuQuoteRepoService ibuQuoteRepoService;

    @Override
    public String init(Model model) {
        RepoEntrustForcedFinishDto entrustForcedFinishDto = new RepoEntrustForcedFinishDto();
        if (StringUtil.equals(this.getParam("workflowStatus"), BussinessDictCodeList.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && com.joyintech.framework.util.StringUtil.isNotBlank(getParam(GlobalConstant.WORKFLOW_ID)))) {
            entrustForcedFinishDto = repoEntrustForcedFinishService.getBussinessEntityByWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
            if (null != entrustForcedFinishDto) {
                RepoEntrustRequest entrustRequest = new RepoEntrustRequest();
                entrustRequest.setEntrustId(entrustForcedFinishDto.getEntrustId());
                entrustForcedFinishDto.setEntrustList(ibuQuoteRepoService.listEntrust(entrustRequest, entrustForcedFinishDto.getLoginUserId()));
                entrustForcedFinishDto.setDealTransactionList(ibuQuoteRepoService.listDealTransactionDetailsNoUserId(entrustForcedFinishDto.getEntrustId()));
            }
        }
        //银行间质押式回购质押券模式：01无质押券库，02单个质押库，03多个质押券库
        model.addAttribute("repo_pledge_model", CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
        model.addAttribute(GlobalConstant.ENTITY, entrustForcedFinishDto);
        return super.init(model);
    }


    @ApiOperation(value = "强制完成检查", notes = "强制完成检查")
    @ResponseBody
    @RequestMapping("/doForcedFinishCheck")
    public Message<AjaxResult> doForcedFinishCheck(@RequestBody Map<String, Object> param) {
        BondQuoteInfoDto bondQuoteInfoDto = new BondQuoteInfoDto();
        AjaxResult ar = null;
        List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
        param.put("entrustIdList", entrustIds);
        return new Message<AjaxResult>().success().setData(ibuQuoteRepoService.doForcedFinishCheck(param, false));
    }

    @ApiOperation(value = "登记提交(保存)", notes = "登记提交(保存)")
    @RequestMapping("save")
    @ResponseBody
    public Message<AjaxResult> save(@RequestBody RepoEntrustForcedFinishDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(br));
        }
        List<String> entrustIds = StringUtil.splitToList(",", dto.getEntrustIds());
        String remark = "强制完成原因：" + dto.getRemark();
        List<RepoEntrustForcedFinishDto> result = new ArrayList<>();
        entrustIds.forEach(item -> {
            RepoEntrustForcedFinishDto entrustForcedFinishDto = new RepoEntrustForcedFinishDto();
            entrustForcedFinishDto.setEntrustId(item);
            entrustForcedFinishDto.setLoginUserId(LoginUserUtil.getUserName());
            entrustForcedFinishDto.setRemark(remark);
            result.add(entrustForcedFinishDto);
        });
        ProcessParameter<RepoEntrustForcedFinishDto> processParameter = new ProcessParameter<RepoEntrustForcedFinishDto>();
        WorkFlowParameter<RepoEntrustForcedFinishDto> params = new WorkFlowParameter<RepoEntrustForcedFinishDto>();
        params.setPageId("RepoEntrustForcedFinish");
        params.setElementId("RepoEntrustForcedFinish_btn_submit");
        params.setBusinessType(DictCodeListExt3.共通业务类型_指令强制完成.getValue());
        params.setBusinessCode(RepoEntrustForcedFinishDto::getEntrustId);
        params.setComment(entity -> getMessage("指令代码") + "(" + entity.getEntrustId() + ")");
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.MODIFY);
        String msg = repoEntrustForcedFinishService.start(processParameter,
                result.toArray(new RepoEntrustForcedFinishDto[result.size()]));
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }
}


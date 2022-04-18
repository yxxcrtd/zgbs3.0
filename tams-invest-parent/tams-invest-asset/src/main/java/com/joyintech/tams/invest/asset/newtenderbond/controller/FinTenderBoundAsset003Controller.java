package com.joyintech.tams.invest.asset.newtenderbond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.invest.asset.newtenderbond.dto.FinTenderBoundAsset003Dto;
import com.joyintech.tams.invest.asset.newtenderbond.service.FinTenderBoundAsset003Service;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@PageId("FinTenderBoundAsset003")
@RequestMapping("/invest/asset/tenderBoundAsset003")
@Api(tags = "新债标的上市流通")
public class FinTenderBoundAsset003Controller extends BaseController {
    @Autowired
    private FinTenderBoundAsset003Service finTenderBoundAsset003Service;

    @ResponseBody
    @PostMapping("/check")
    @ApiOperation(value = "上市流通/撤销校验", notes = "上市流通/撤销校验")
    public Message check(@RequestBody FinTenderBoundAsset003Dto dto) {
        List<ValidMessage> validMessageList = finTenderBoundAsset003Service.valid(dto);
        if (CollectionUtils.isNotEmpty(validMessageList)) {
            return Message.fail("校验失败", validMessageList);
        } else {
            return Message.success("校验成功", null);
        }
    }

    @ResponseBody
    @PostMapping("/save")
    @ApiOperation(value = "上市流通/撤销", notes = "上市流通/撤销")
    public Message<AjaxResult> save(@RequestBody FinTenderBoundAsset003Dto dto) {
        ProcessParameter<FinTenderBoundAsset003Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<FinTenderBoundAsset003Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(FinTenderBoundAsset003Dto::getFinprodMarketId);
        params.setBusinessType(BussinessDictCodeList.共同业务类型_一级投标债券类资产管理.getValue());
        if (StringUtil.equals(dto.getOperateType(), OperationType.NEW.name())) {
            processParameter.setOperationType(OperationType.NEW);
        } else if (StringUtil.equals(dto.getOperateType(), OperationType.REVOKE.name())) {
            processParameter.setOperationType(OperationType.REVOKE);
        }
        processParameter.setWorkFlowParameter(params);
        try {
            finTenderBoundAsset003Service.start(processParameter, dto);
        } catch (Exception e) {
            super.getLogger().error("新债标的流程发起失败", e);
            return Message.fail("流程发起失败");
        }
        return Message.success("处理成功", null);
    }
}

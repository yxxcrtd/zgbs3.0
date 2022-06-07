package com.joyintech.tams.exchange.entrust.common.controller;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.component.base.core.MstWorkflowCommonService;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import com.joyintech.tams.exchange.entrust.common.dto.EtrExchangeCommonRequest;
import com.joyintech.tams.exchange.entrust.common.dto.EtrExchangePortfolioAllocationRequest;
import com.joyintech.tams.exchange.entrust.common.dto.EtrExchangePortfolioAllocationResponse;
import com.joyintech.tams.exchange.entrust.common.service.EtrExchangeCommonService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/etrexchange/common/")
@Api(tags = "公共共通校验接口")
public class EtrExchangeCommonController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Autowired
    EtrExchangeCommonService etrExchangeCommonService;

    /**
     * 流程中数据校验
     *
     * @param request request
     * @param result  BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "流程中数据check", notes = "流程中数据check")
    @ResponseBody
    @PostMapping("workFlowCheck")
    public Message<AjaxResult> workFlowCheck(@RequestBody @Validated() EtrExchangeCommonRequest request,
                                             BindingResult result) {
        logger.info("workFlowCheck ->> 流程中数据校验请求参数：{}", JSON.toJSONString(request));
        List<ValidMessage> errorList = new ArrayList<>();
        //check流程中是否有数据
        WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
        BeanUtil.copyPropertiesIgnoreNull(wflValidInputParameter, request);
        AjaxResult ajax = new AjaxResult();
        if (WorkFlowUtil.isNotFromToDoList() && mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
            errorList.add(new ValidMessage("所选对象存在有效或流程中的数据不能修改或提交"));
            ajax.setErrorList(errorList);
            return new Message<AjaxResult>().success().setData(ajax);
        }
        return new Message<AjaxResult>().success().setData(ajax);
    }

    /**
     * 查询指令组合分配列表
     *
     * @param dto dto
     * @return 处理结果
     * @throws Exception
     *             异常
     */
    @ApiOperation(value = "查询指令组合分配列表", notes = "查询指令组合分配列表")
    @PostMapping("/selectEtrPortfolioAllocationList")
    @ResponseBody
    public Message<EtrExchangePortfolioAllocationResponse>  selectEtrPortfolioAllocationList(@RequestBody EtrExchangePortfolioAllocationRequest dto )  {
        try{
            EtrExchangePortfolioAllocationResponse response=etrExchangeCommonService.selectEtrPortfolioAllocationList(dto);
            return new Message<EtrExchangePortfolioAllocationResponse>().success().setData(response);
        }catch (Exception e){
            return new Message<EtrExchangePortfolioAllocationResponse>().fail().setData(null);
        }
    }
}

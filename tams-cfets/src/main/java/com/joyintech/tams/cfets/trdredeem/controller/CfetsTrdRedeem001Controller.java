/*
 * 文件名：CfetsBondDealQuotedPrice002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trdredeem.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.cfets.trd.bond.service.CfetsQuotedCommonService;
import com.joyintech.tams.cfets.trd.bond.service.IbuQuoteBondService;
import com.joyintech.tams.cfets.trd.repo.service.CfetsRepoQuotedPrice004Service;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemCheckRequst;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemDealCommonDto;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemRegInfoListResponse;
import com.joyintech.tams.cfets.trdredeem.service.CfetsTrdRedeem001Service;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service.getBussinessCodeParameter;

/**
 * 赎回登记
 */
@Controller
@PageId("CfetsTrdRedeem001")
@RequestMapping("/cfets/trdredeem/CfetsTrdRedeem001")
@Api(tags = "赎回登记新建")
public class CfetsTrdRedeem001Controller extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdRedeem001Controller.class);


    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsTrdRedeem001Service cfetsTrdRedeem001Service;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private IbuQuoteBondService ibuQuoteBondService;
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;
    @Autowired
    private CfetsRepoQuotedPrice004Service cfetsRepoQuotedPrice004Service;
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向,  BussinessDictCode.币种);
        CfetsTrdRedeemDealRegisterQuoteDto cfetsTrdRedeemDealRegisterQuoteDto = new CfetsTrdRedeemDealRegisterQuoteDto();
        CfetsTrdRedeemDealCommonDto cfetsTrdRedeemDealCommonDto = new CfetsTrdRedeemDealCommonDto();
        if (StringUtil.equals(this.getParam("workflowStatus"), BussinessDictCodeList.交易流程状态_流程中.getValue())
            || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(getParam(GlobalConstant.WORKFLOW_ID)))) {
            cfetsTrdRedeemDealRegisterQuoteDto = cfetsTrdRedeem001Service.getBussinessEntityByWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
            model.addAttribute(GlobalConstant.WORKFLOW_ID,getParam(GlobalConstant.WORKFLOW_ID));
            BeanUtil.copyPropertiesIgnoreNull(cfetsTrdRedeemDealCommonDto,cfetsTrdRedeemDealRegisterQuoteDto);
            //券面万元
           // cfetsTrdRedeemDealCommonDto.setShareAmt(ArithmeticUtil.divide(cfetsTrdRedeemDealCommonDto.getShareAmt(),new BigDecimal(10000),2));
            //分配列表元转化万元
           /* if(CollectionUtils.isNotEmpty(cfetsTrdRedeemDealCommonDto.getPtlAllocatios())){
                cfetsTrdRedeemDealCommonDto.getPtlAllocatios().stream().forEach(item ->{
                    item.setShareAmt(ArithmeticUtil.divide(item.getShareAmt(),new BigDecimal(10000),2));
                });
            }*/
        }
        model.addAttribute("ptlAllocatios", JsonUtils.object2Json(cfetsTrdRedeemDealCommonDto.getPtlAllocatios()));
        model.addAttribute(GlobalConstant.ENTITY, cfetsTrdRedeemDealCommonDto);
        return super.init(model);
    }

   /* @ApiOperation(value = "登记初始化", notes = "登记初始化")
    @PostMapping("/initReg")
    @ResponseBody
    public Message<CfetsTrdRedeemDealCommonDto> initPrice(@RequestBody CfetsTrdRedeemDealCommonDto cfetsTrdRedeemDealCommonDto){
        cfetsTrdRedeemDealCommonDto = cfetsTrdRedeem001Service.initReg(cfetsTrdRedeemDealCommonDto);
        return new Message<CfetsTrdRedeemDealCommonDto>().success().setData(cfetsTrdRedeemDealCommonDto);
    }*/

    @ApiOperation(value = "当日登记撤销检查", notes = "当日登记撤销检查")
    @ResponseBody
    @PostMapping("/checkDayRegRevokeCheck")
    public Message<AjaxResult> checkDayRegRevokeCheck(@RequestBody CfetsTrdRedeemCheckRequst cfetsBondRegCheckRequst) {
        return new Message<AjaxResult>().success().setData(cfetsTrdRedeem001Service.checkDayRegRevokeCheck(cfetsBondRegCheckRequst));
    }


    @ApiOperation(value = "当日登记撤销", notes = "当日登记撤销")
    @ResponseBody
    @PostMapping("regRevoke")
    public Message<AjaxResult> regRevoke(@RequestBody CfetsTrdRedeemCheckRequst cfetsBondRegCheckRequst) throws WorkFlowException{
        //前后台转换
        AjaxResult ajaxResult = cfetsTrdRedeem001Service.checkDayRegRevokeCheck(cfetsBondRegCheckRequst);
        if(CollectionUtils.isNotEmpty(ajaxResult.getErrorList())){
            return new Message<AjaxResult>().success().setData(ajaxResult);
        }
        List<CfetsTrdRedeemDealRegisterQuoteDto> CfetsBondDealRegisterQuoteDtos = new ArrayList<>();
        for (CfetsTrdRedeemRegInfoListResponse cfetsBondRegCheckDetaileRequst : cfetsBondRegCheckRequst.getCfetsBondRegCheckDetaileRequsts()) {
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId("CfetsTrdRedeem001");
            wflValidInputParameter.setBusinessDataPk1(cfetsBondRegCheckDetaileRequst.getBusiCode());
            ProcessMark processMark = cfetsRepoQuotedPrice004Service.getBusinessInfoByPageIdAndPk(wflValidInputParameter);
            CfetsTrdRedeemDealRegisterQuoteDto cfetsTrdRedeemDealRegisterQuoteDto =cfetsTrdRedeem001Service.getBussinessEntity(processMark);
            cfetsTrdRedeemDealRegisterQuoteDto.setTotleTradeId(cfetsBondRegCheckDetaileRequst.getBusiCode());
            cfetsTrdRedeemDealRegisterQuoteDto.setRevokeReason(cfetsBondRegCheckRequst.getRemark());
            cfetsTrdRedeemDealRegisterQuoteDto.setOperatorType("revoke");
            CfetsBondDealRegisterQuoteDtos.add(cfetsTrdRedeemDealRegisterQuoteDto);
        }
        ProcessParameter<CfetsTrdRedeemDealRegisterQuoteDto> processParameter = new ProcessParameter<CfetsTrdRedeemDealRegisterQuoteDto>();
        WorkFlowParameter<CfetsTrdRedeemDealRegisterQuoteDto> workFlowParameter = new WorkFlowParameter<CfetsTrdRedeemDealRegisterQuoteDto>();
        workFlowParameter.setPageId("CfetsTrdRedeem001");
        workFlowParameter.setElementId("CfetsTrdRedeem001_btn_revoke");
        workFlowParameter.setBusinessCode(item -> item.getTotleTradeId());
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_赎回登记.getValue());
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setOperationType(OperationType.REVOKE);
        String msg = cfetsTrdRedeem001Service.start(processParameter,
                CfetsBondDealRegisterQuoteDtos.toArray(new CfetsTrdRedeemDealRegisterQuoteDto[CfetsBondDealRegisterQuoteDtos.size()]));
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }

    @ApiOperation(value = "登记提交检查", notes = "登记提交检查")
    @ResponseBody
    @PostMapping("/check")
    public Message<AjaxResult> check(@RequestBody CfetsTrdRedeemDealCommonDto cfetsTrdRedeemDealCommonDto){
        //check必填项
        AjaxResult ajaxResult = cfetsTrdRedeem001Service.checkNotEmpty(cfetsTrdRedeemDealCommonDto);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    /**
     * 待办事项页面,保存校验
     * @param CfetsTrdRedeemDealCommonDto
     * @return
     */
    @PostMapping("/checkByHome")
    @ResponseBody
    public AjaxResult checkByHome(@RequestBody CfetsTrdRedeemDealCommonDto cfetsTrdRedeemDealCommonDto){
        //check必填项
        return cfetsTrdRedeem001Service.checkNotEmpty(cfetsTrdRedeemDealCommonDto);
    }

    @ApiOperation(value = "登记提交(保存)", notes = "登记提交(保存)")
    @ResponseBody
    @PostMapping("save")
	public Message<AjaxResult> save(@RequestBody CfetsTrdRedeemDealCommonDto dto, BindingResult br) throws Exception{
        CfetsTrdRedeemDealRegisterQuoteDto cfetsTrdRedeemDealRegisterQuoteDto = new CfetsTrdRedeemDealRegisterQuoteDto();
        //前台实体转成后台dto
        BeanUtil.copyPropertiesIgnoreNull(cfetsTrdRedeemDealRegisterQuoteDto, dto);
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = getBussinessCodeParameter(dto.getPs());
            cfetsTrdRedeemDealRegisterQuoteDto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            cfetsTrdRedeemDealRegisterQuoteDto.setTotleTradeId(IDGenerator.generate());
        }
        cfetsTrdRedeemDealRegisterQuoteDto.setMySideTrader(SecurityUtil.getLoginUser().getUserName());
		ProcessParameter<CfetsTrdRedeemDealRegisterQuoteDto> processParameter = new ProcessParameter<CfetsTrdRedeemDealRegisterQuoteDto>();
		WorkFlowParameter<CfetsTrdRedeemDealRegisterQuoteDto> params = new WorkFlowParameter<CfetsTrdRedeemDealRegisterQuoteDto>();
		params.setPageId("CfetsTrdRedeem001");
		params.setElementId(cfetsTrdRedeemDealRegisterQuoteDto.getElementId());
		params.setBusinessType(DictCodeListExt3.共通业务类型_赎回登记.getValue());
		params.setBusinessCode(cfetsTrdRedeemDealRegisterQuoteDto.getTotleTradeId());
        cfetsTrdRedeemDealRegisterQuoteDto.setQuoteType(DictCodeListExt3.报价系统区分类型_现券登记报价.getValue());
		processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.NEW);
        String msg = null;
        try{
            msg = cfetsTrdRedeem001Service.start(processParameter, cfetsTrdRedeemDealRegisterQuoteDto);
            return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        }
        catch (Exception e) {
            logger.error("赎回登记新建失败",e);
            return PositionUtil.ajaxErrorReturn(e);
        }
	}
    @ApiOperation(value = "赎回时组合分配列表查询", notes = "赎回时组合分配列表查询")
    @PostMapping("/queryCfetsBussCombDisList/{secId:.+}")
    @ResponseBody
    public Message<List<PtlAllocationDto>> queryCfetsBussCombDisList(@PathVariable String secId) {
        try {
            List<PtlAllocationDto> list = cfetsTrdRedeem001Service.queryCfetsBussCombDisList(secId);
            return new Message<List<PtlAllocationDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询该资产对应的组合分配列表异常", e);
            return new Message<List<PtlAllocationDto>>().fail().setMessage("查询异常");
        }
    }


}

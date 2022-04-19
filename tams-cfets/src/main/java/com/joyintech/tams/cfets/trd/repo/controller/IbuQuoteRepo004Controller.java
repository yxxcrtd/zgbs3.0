/*
 * 文件名：CfetsBondDealQuotedPrice002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.repo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.dto.CfetsSearchInfoRequest;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondRegCheckDetaileRequst;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondRegCheckRequst;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.repo.service.CfetsRepoQuotedPrice004Service;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 本币交易-质押式回购交易 买卖(登记)
 */
@Controller
@PageId("IbuQuoteRepo004")
@RequestMapping("/cfets/bankuplink/repo/IbuQuoteRepo004")
@Api(tags = "CFETS质押式回购交易-登记接口")
public class IbuQuoteRepo004Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(IbuQuoteRepo004Controller.class);
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsRepoQuotedPrice004Service cfetsRepoQuotedPrice004Service;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向, BussinessDictCode.清算速度, DictCode.回购方式, DictCode.价格模式,
                BussinessDictCode.结算方式, BussinessDictCode.清算类型, BussinessDictCode.币种, DictCode.期限品种_回购,DictCode.登记托管机构);
        model.addAttribute("ACCOUNT_SPEED_0", BussinessDictCodeList.清算速度_T加0.getValue());
        model.addAttribute("ACCOUNT_SPEED_1", BussinessDictCodeList.清算速度_T加1.getValue());
        model.addAttribute("ACCOUNT_SPEED_2", BussinessDictCodeList.清算速度_T加2.getValue());
        model.addAttribute("yesNoFlag_yes", BussinessDictCodeList.是否_是.getValue());
        CfetsRepoDealRegisterQuoteDto cfetsRepoDealRegisterQuoteDto = new CfetsRepoDealRegisterQuoteDto();
        if (StringUtil.equals(this.getParam("workflowStatus"), BussinessDictCodeList.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(getParam(GlobalConstant.WORKFLOW_ID)))) {
            cfetsRepoDealRegisterQuoteDto = cfetsRepoQuotedPrice004Service.getBussinessEntityByWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
            model.addAttribute(GlobalConstant.WORKFLOW_ID, getParam(GlobalConstant.WORKFLOW_ID));
        }
        model.addAttribute("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
        model.addAttribute("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        model.addAttribute("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        model.addAttribute("quotaPageModel", "new");
        model.addAttribute("ptlAllocations", cfetsRepoDealRegisterQuoteDto.getPtlAllocations());
        model.addAttribute("repoEntrustPledgeDtos", cfetsRepoDealRegisterQuoteDto.getRepoEntrustPledgeDtos());
        //分配方式
        model.addAttribute("distribution", cfetsRepoDealRegisterQuoteDto.getDistribution());
        //本币导出按钮配置
        model.addAttribute("EXPORT_SHOW", CacheUtil.getSysParam("EXPORT_SHOW"));
        model.addAttribute("ps", cfetsRepoDealRegisterQuoteDto.getPs());
        model.addAttribute("quoteStyle", cfetsRepoDealRegisterQuoteDto.getQuoteStyle());
        // 对手方是否可编辑
        String isEdit = DictCodeList.是否_是.getValue();
        List<String> entrustIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cfetsRepoDealRegisterQuoteDto.getPtlAllocations())) {
            cfetsRepoDealRegisterQuoteDto.getPtlAllocations().forEach(item -> entrustIdList.add(item.getEntrustId()));
        }
        if (StringUtil.equals(cfetsRepoDealRegisterQuoteDto.getQuoteStyle(), "05")
                || cfetsRepoQuotedPrice004Service.getIsExistCounterId(entrustIdList) > 0) {
            isEdit = DictCodeList.是否_否.getValue();
        }
        model.addAttribute("isEdit", isEdit);
        //卖空模式
        model.addAttribute(GlobalConstant.ENTITY, cfetsRepoDealRegisterQuoteDto);
        return super.init(model);
    }

    @ApiOperation(value = "登记提交检查", notes = "登记提交检查")
    @PostMapping("/check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto) {
        //check必填项
        AjaxResult ajaxResult = cfetsRepoQuotedPrice004Service.checkSave(cfetsRepoDealQuoteCommonDto);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @PostMapping("/checkByHome")
    @ResponseBody
    public AjaxResult checkByHome(@RequestBody CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto) {
        //check必填项
        AjaxResult ajaxResult = cfetsRepoQuotedPrice004Service.checkSave(cfetsRepoDealQuoteCommonDto);
        return ajaxResult;
    }

    @ApiOperation(value = "登记提交(保存)", notes = "登记提交(保存)")
    @RequestMapping("save")
    @ResponseBody
    public Message<AjaxResult> save(@RequestBody CfetsRepoDealQuoteCommonDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(br));
        }
        CfetsRepoDealRegisterQuoteDto cfetsBondDealRegisterQuoteDto = new CfetsRepoDealRegisterQuoteDto();
        //前台实体转成后台dto
        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRegisterQuoteDto, dto);
        cfetsBondDealRegisterQuoteDto.setRepoTerm((long) dto.getRepoTerm());
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            if (StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_正回购.getValue())) {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
            } else {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
            }
            cfetsBondDealRegisterQuoteDto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            cfetsBondDealRegisterQuoteDto.setTotleTradeId(IDGenerator.generate());
        }
        cfetsBondDealRegisterQuoteDto.setOperatorType(dto.getOperateType());
        ProcessParameter<CfetsRepoDealRegisterQuoteDto> processParameter = new ProcessParameter<CfetsRepoDealRegisterQuoteDto>();
        WorkFlowParameter<CfetsRepoDealRegisterQuoteDto> params = new WorkFlowParameter<CfetsRepoDealRegisterQuoteDto>();
        params.setPageId("IbuQuoteRepo004");
        params.setElementId(dto.getElementId());
        params.setBusinessType(DictCodeListExt3.共通业务类型_本币业务交易_质押式回购_登记.getValue());
        params.setBusinessCode(cfetsBondDealRegisterQuoteDto.getTotleTradeId());
        String counterTrader = null;
        if (StringUtil.isNotEmpty(dto.getCounterTrader())
                && StringUtil.equals(CacheUtil.getSysParam("EXPORT_SHOW"), "01")) {
            CfetsSearchInfoRequest item = new CfetsSearchInfoRequest();
            item.setDealType("02");
            item.setPartyId(dto.getCounterId());
            item.setSearch(dto.getCounterTrader());
            List<SelectOptionDto> list = joyinbusinessCommonService.queryMySideTraderOrCountTrader(item);
            if (CollectionUtils.isNotEmpty(list)) {
                counterTrader = "-对手方交易员:" + list.get(0).getText() + "-";
            }
        }
        if (StringUtil.isNotEmpty(dto.getCounterTrader())
                && StringUtil.equals(CacheUtil.getSysParam("EXPORT_SHOW"), "02")) {
            counterTrader = "-对手方交易员:" + dto.getCounterTrader() + "-";
        }
        String str = "交易日期:" + DateUtil.format(iPlatformDate.getSysDate());
        if (StringUtil.isNotEmpty(dto.getRepoTerm())) {
            str += "-回购期限:" + dto.getRepoTerm() + "天";
        }
        str += StringUtil.isEmpty(counterTrader) ? "-" : counterTrader;
        if (StringUtil.isNotEmpty(dto.getTradeAmt())) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            str += "交易金额:" + df.format(ArithmeticUtil.round(dto.getTradeAmt(), 2, RoundingMode.HALF_UP)) + "元";
        }
        String finalStr = str;
        params.setComment(entity -> finalStr);
        cfetsBondDealRegisterQuoteDto.setQuoteType(DictCodeListExt3.报价系统区分类型_质押式回购登记报价.getValue());
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.NEW);
        try {
            String msg = cfetsRepoQuotedPrice004Service.start(processParameter, cfetsBondDealRegisterQuoteDto);
            return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        } catch (Exception e) {
            logger.error("保存质押式回购新建失败", e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }

    @ApiOperation(value = "当日登记撤销检查", notes = "当日登记撤销检查")
    @ResponseBody
    @RequestMapping("/checkDayRegRevokeCheck")
    public Message<AjaxResult> checkDayRegRevokeCheck(@RequestBody CfetsBondRegCheckRequst cfetsBondRegCheckRequst) {
        return new Message<AjaxResult>().success().setData(cfetsRepoQuotedPrice004Service.checkDayRegRevokeCheck(cfetsBondRegCheckRequst));
    }


    @ApiOperation(value = "当日登记撤销", notes = "当日登记撤销")
    @ResponseBody
    @RequestMapping("regRevoke")
    public Message<AjaxResult> regRevoke(@RequestBody CfetsBondRegCheckRequst cfetsBondRegCheckRequst) throws WorkFlowException {
        //前后台转换
        AjaxResult ajaxResult = cfetsRepoQuotedPrice004Service.checkDayRegRevokeCheck(cfetsBondRegCheckRequst);
        if (CollectionUtils.isNotEmpty(ajaxResult.getErrorList())) {
            return new Message<AjaxResult>().success().setData(ajaxResult);
        }
        List<CfetsRepoDealRegisterQuoteDto> cfetsRepoDealRegisterQuoteDtos = new ArrayList<>();
        for (CfetsBondRegCheckDetaileRequst cfetsBondRegCheckDetaileRequst : cfetsBondRegCheckRequst.getCfetsBondRegCheckDetaileRequsts()) {
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId("IbuQuoteRepo004");
            wflValidInputParameter.setBusinessDataPk1(cfetsBondRegCheckDetaileRequst.getQuoteId());
            ProcessMark processMark = cfetsRepoQuotedPrice004Service.getBusinessInfoByPageIdAndPk(wflValidInputParameter);
            CfetsRepoDealRegisterQuoteDto cfetsBondDealRegisterQuoteDto = cfetsRepoQuotedPrice004Service.getBussinessEntity(processMark);
            cfetsBondDealRegisterQuoteDto.setTotleTradeId(cfetsBondRegCheckDetaileRequst.getQuoteId());
            cfetsBondDealRegisterQuoteDto.setRemark(StringUtil.join("交易登记撤销原因：",cfetsBondRegCheckRequst.getRemark()));
            cfetsBondDealRegisterQuoteDto.setOperatorType("revoke");
            cfetsRepoDealRegisterQuoteDtos.add(cfetsBondDealRegisterQuoteDto);
        }
        ProcessParameter<CfetsRepoDealRegisterQuoteDto> processParameter = new ProcessParameter<CfetsRepoDealRegisterQuoteDto>();
        WorkFlowParameter<CfetsRepoDealRegisterQuoteDto> workFlowParameter = new WorkFlowParameter<CfetsRepoDealRegisterQuoteDto>();
        workFlowParameter.setPageId("IbuQuoteRepo004");
        workFlowParameter.setElementId("IbuQuoteRepo004_btn_revoke");
        workFlowParameter.setBusinessCode(CfetsRepoDealRegisterQuoteDto::getTotleTradeId);
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_本币业务交易_质押式回购_登记.getValue());
        workFlowParameter.setComment(entity -> getMessage("CFETSHIS_QUOTEID") + "(" + entity.getTotleTradeId() + ")");
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setOperationType(OperationType.REVOKE);
        String msg = cfetsRepoQuotedPrice004Service.start(processParameter,
                cfetsRepoDealRegisterQuoteDtos.toArray(new CfetsRepoDealRegisterQuoteDto[cfetsRepoDealRegisterQuoteDtos.size()]));
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }


}

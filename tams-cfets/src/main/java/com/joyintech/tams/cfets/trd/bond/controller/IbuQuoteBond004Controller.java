/*
 * 文件名：CfetsBondDealQuotedPrice002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.bond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondRegCheckDetaileRequst;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondRegCheckRequst;
import com.joyintech.tams.cfets.trd.bond.service.CfetsBondDealQuotedPrice004Service;
import com.joyintech.tams.cfets.trd.bond.service.CfetsQuotedCommonService;
import com.joyintech.tams.cfets.trd.bond.service.IbuQuoteBondService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service.getBussinessCodeParameter;

/**
 * 本币交易登记
 */
@Controller
@PageId("IbuQuoteBond004")
@RequestMapping("/cfets/bond/IbuQuoteBond004")
@Api(tags = "CFETS债券交易-登记")
public class IbuQuoteBond004Controller extends BaseController {

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPrice004Service cfetsBondDealQuotedPrice004Service;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private IbuQuoteBondService ibuQuoteBondService;
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    @Autowired
    private com.joyintech.tams.cfets.trd.bond.service.CfetsQuotedCommonService cfetsQuotedCommonServiceVue;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向, BussinessDictCode.清算速度, DictCode.分配方式,
            BussinessDictCode.结算方式, BussinessDictCode.清算类型, BussinessDictCode.币种,DictCode.业务模式,DictCode.价格模式);
        model.addAttribute("ACCOUNT_SPEED_0", BussinessDictCodeList.清算速度_T加0.getValue());
        model.addAttribute("ACCOUNT_SPEED_1", BussinessDictCodeList.清算速度_T加1.getValue());
        model.addAttribute("ACCOUNT_SPEED_2", BussinessDictCodeList.清算速度_T加2.getValue());
        model.addAttribute("yesNoFlag_yes", BussinessDictCodeList.是否_是.getValue());
        model.addAttribute("assetTypeOne_abs", BussinessDictCodeList.平安资产一级分类_资产支持证券.getValue());
        model.addAttribute("calcTypeUnitCprice", BussinessDictCodeList.计算选择方式_净价.getValue());
        model.addAttribute("calcTypeYield", BussinessDictCodeList.计算选择方式_到期收益率.getValue());
        model.addAttribute("calcTypeExecYield", BussinessDictCodeList.计算选择方式_行权收益率.getValue());
        CfetsBondDealRegisterQuoteDto cfetsBondDealRegisterQuoteDto = new CfetsBondDealRegisterQuoteDto();
        CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto = new CfetsBondDealQuoteCommonDto();
        if (StringUtil.equals(this.getParam("workflowStatus"), BussinessDictCodeList.交易流程状态_流程中.getValue())
            || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(getParam(GlobalConstant.WORKFLOW_ID)))) {
            cfetsBondDealRegisterQuoteDto = cfetsBondDealQuotedPrice004Service.getBussinessEntityByWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
            model.addAttribute(GlobalConstant.WORKFLOW_ID,getParam(GlobalConstant.WORKFLOW_ID));
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealQuoteCommonDto,cfetsBondDealRegisterQuoteDto);
            //收益率*100
            if(null!=cfetsBondDealQuoteCommonDto.getMatureYield()){
                cfetsBondDealQuoteCommonDto.setMatureYield(ArithmeticUtil.multiply(cfetsBondDealQuoteCommonDto.getMatureYield(),new BigDecimal(100)));
            }
            if(null!=cfetsBondDealQuoteCommonDto.getExerYield()){
                cfetsBondDealQuoteCommonDto.setExerYield(ArithmeticUtil.multiply(cfetsBondDealQuoteCommonDto.getExerYield(),new BigDecimal(100)));
            }
            //券面万元
            cfetsBondDealQuoteCommonDto.setShareAmt(ArithmeticUtil.divide(cfetsBondDealQuoteCommonDto.getShareAmt(),new BigDecimal(10000),2));
            //分配列表元转化万元
            if(CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
                cfetsBondDealQuoteCommonDto.getPtlAllocatios().stream().forEach(item ->{
                    item.setAvaNum(ArithmeticUtil.divide(item.getAvaNum(),new BigDecimal(10000),2));
                    item.setShareAmt(ArithmeticUtil.divide(item.getShareAmt(),new BigDecimal(10000),2));
                    item.setOurEntNum(ArithmeticUtil.divide(item.getOurEntNum(),new BigDecimal(10000),2));
                    item.setLendinEntNum(ArithmeticUtil.divide(item.getLendinEntNum(),new BigDecimal(10000),2));
                    item.setSellAbleNum(ArithmeticUtil.divide(item.getSellAbleNum(),new BigDecimal(10000),2));
                    item.setMatureYield(ArithmeticUtil.multiply(item.getMatureYield(),new BigDecimal(100)));
                    item.setExerYield(ArithmeticUtil.multiply(item.getExerYield(),new BigDecimal(100)));
                });
            }
        }
        model.addAttribute("PRICE_MODEL_PRM01",DictCodeListExt2.价格模式_指定价格.getValue());
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        model.addAttribute("quotaPageModel","new");
        model.addAttribute("ptlAllocatios", JsonUtils.object2Json(cfetsBondDealQuoteCommonDto.getPtlAllocatios()));
        //分配方式
        model.addAttribute("distribution",cfetsBondDealQuoteCommonDto.getDistribution());
        //卖空模式
        model.addAttribute("sellPara",CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        model.addAttribute("tradeDate", cfetsBondDealQuoteCommonDto.getTradeDate() !=null ? cfetsBondDealQuoteCommonDto.getTradeDate().getTime() : null);
        model.addAttribute(GlobalConstant.ENTITY, cfetsBondDealQuoteCommonDto);
        return super.init(model);
    }

    @ApiOperation(value = "登记初始化", notes = "登记初始化")
    @PostMapping("/initReg")
    @ResponseBody
    public Message<CfetsBondDealQuoteCommonDto> initPrice(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        cfetsBondDealQuoteCommonDto = cfetsBondDealQuotedPrice004Service.initReg(cfetsBondDealQuoteCommonDto);
        return new Message<CfetsBondDealQuoteCommonDto>().success().setData(cfetsBondDealQuoteCommonDto);
    }

    @ApiOperation(value = "当日登记撤销检查", notes = "当日登记撤销检查")
    @ResponseBody
    @RequestMapping("/checkDayRegRevokeCheck")
    public Message<AjaxResult> checkDayRegRevokeCheck(@RequestBody CfetsBondRegCheckRequst cfetsBondRegCheckRequst) {
        return new Message<AjaxResult>().success().setData(ibuQuoteBondService.checkDayRegRevokeCheck(cfetsBondRegCheckRequst));
    }


    @ApiOperation(value = "当日登记撤销", notes = "当日登记撤销")
    @ResponseBody
    @RequestMapping("regRevoke")
    public Message<AjaxResult> regRevoke(@RequestBody CfetsBondRegCheckRequst cfetsBondRegCheckRequst) throws WorkFlowException{
        //前后台转换
        AjaxResult ajaxResult = ibuQuoteBondService.checkDayRegRevokeCheck(cfetsBondRegCheckRequst);
        if(CollectionUtils.isNotEmpty(ajaxResult.getErrorList())){
            return new Message<AjaxResult>().success().setData(ajaxResult);
        }
        List<CfetsBondDealRegisterQuoteDto> CfetsBondDealRegisterQuoteDtos = new ArrayList<>();
        for (CfetsBondRegCheckDetaileRequst cfetsBondRegCheckDetaileRequst : cfetsBondRegCheckRequst.getCfetsBondRegCheckDetaileRequsts()) {
            //获取初始化的值
            CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto = new CfetsBondDealQuoteCommonDto();
            cfetsBondDealQuoteCommonDto.setQuoteId(cfetsBondRegCheckDetaileRequst.getQuoteId());
            cfetsBondDealQuoteCommonDto.setValidStatus(BussinessDictCodeList.有效状态_有效.getValue());
            cfetsBondDealQuoteCommonDto = cfetsBondDealQuotedPrice004Service.initReg(cfetsBondDealQuoteCommonDto);
            CfetsBondDealRegisterQuoteDto cfetsBondDealRegisterQuoteDto = new CfetsBondDealRegisterQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRegisterQuoteDto,cfetsBondDealQuoteCommonDto);
            cfetsBondDealRegisterQuoteDto.setTotleTradeId(cfetsBondRegCheckDetaileRequst.getQuoteId());
            cfetsBondDealRegisterQuoteDto.setRemark(StringUtil.join("交易登记撤销原因：",cfetsBondRegCheckRequst.getRemark()));
            cfetsBondDealRegisterQuoteDto.setOperatorType("revoke");
            CfetsBondDealRegisterQuoteDtos.add(cfetsBondDealRegisterQuoteDto);
        }
        ProcessParameter<CfetsBondDealRegisterQuoteDto> processParameter = new ProcessParameter<CfetsBondDealRegisterQuoteDto>();
        WorkFlowParameter<CfetsBondDealRegisterQuoteDto> workFlowParameter = new WorkFlowParameter<CfetsBondDealRegisterQuoteDto>();
        workFlowParameter.setPageId("IbuQuoteBond004");
        workFlowParameter.setElementId("IbuQuoteBond004_btn_revoke");
        workFlowParameter.setBusinessCode(item -> item.getTotleTradeId());
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_本币业务交易_登记.getValue());

        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setOperationType(OperationType.REVOKE);
        String msg = cfetsBondDealQuotedPrice004Service.start(processParameter,
                CfetsBondDealRegisterQuoteDtos.toArray(new CfetsBondDealRegisterQuoteDto[CfetsBondDealRegisterQuoteDtos.size()]));
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }

    @ApiOperation(value = "登记提交检查", notes = "登记提交检查")
    @PostMapping("/check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        //check必填项
        AjaxResult ajaxResult = cfetsBondDealQuotedPrice004Service.checkNotEmpty(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isEmpty(ajaxResult.getErrorList())){
            ajaxResult = cfetsQuotedCommonServiceVue.checkcheckAnchoring(cfetsBondDealQuoteCommonDto);
        }
        if(CollectionUtils.isEmpty(ajaxResult.getErrorList())){
            ajaxResult = cfetsQuotedCommonService.positionCheck(cfetsBondDealQuoteCommonDto);
        }
        if(CollectionUtils.isEmpty(ajaxResult.getErrorList())){
            ajaxResult= cfetsQuotedCommonService.reclasCheck(cfetsBondDealQuoteCommonDto);
        }
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @ApiOperation(value = "登记提交(保存)", notes = "登记提交(保存)")
	@RequestMapping("save")
    @ResponseBody
	public Message<AjaxResult> save(@RequestBody CfetsBondDealQuoteCommonDto dto) {
        CfetsBondDealRegisterQuoteDto cfetsBondDealRegisterQuoteDto = new CfetsBondDealRegisterQuoteDto();
        //前台实体转成后台dto
        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRegisterQuoteDto, dto);
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = getBussinessCodeParameter(dto.getPs());
            cfetsBondDealRegisterQuoteDto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            cfetsBondDealRegisterQuoteDto.setTotleTradeId(IDGenerator.generate());
        }
        //登记回售  对手方显示发行人名称
        if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())&&StringUtil.isEmpty(cfetsBondDealRegisterQuoteDto.getCounterName())){
            cfetsBondDealRegisterQuoteDto.setCounterName(cfetsQuotedCommonServiceVue.getIssuserName(cfetsBondDealRegisterQuoteDto.getFinprodId()));
        }
		ProcessParameter<CfetsBondDealRegisterQuoteDto> processParameter = new ProcessParameter<CfetsBondDealRegisterQuoteDto>();
		WorkFlowParameter<CfetsBondDealRegisterQuoteDto> params = new WorkFlowParameter<CfetsBondDealRegisterQuoteDto>();
		params.setPageId("IbuQuoteBond004");
        params.setElementId("IbuQuoteBond004_btn_submit");
		params.setBusinessType(DictCodeListExt3.共通业务类型_本币业务交易_登记.getValue());
		params.setBusinessCode(cfetsBondDealRegisterQuoteDto.getTotleTradeId());
        DecimalFormat  df = new DecimalFormat("###,###.00");
        cfetsBondDealRegisterQuoteDto.setQuoteType(DictCodeListExt3.报价系统区分类型_现券登记报价.getValue());
		processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.NEW);
        String msg = null;
        try{
            msg = cfetsBondDealQuotedPrice004Service.start(processParameter, cfetsBondDealRegisterQuoteDto);
        }catch (Exception e){
            //e.printStackTrace();
            return PositionUtil.ajaxErrorReturn(e);
        }
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
	}


}

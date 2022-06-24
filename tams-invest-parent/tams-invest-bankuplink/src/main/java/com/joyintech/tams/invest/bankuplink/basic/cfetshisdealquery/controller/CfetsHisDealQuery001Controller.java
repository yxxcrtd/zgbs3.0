package com.joyintech.tams.invest.bankuplink.basic.cfetshisdealquery.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.invest.bankuplink.basic.cfetshisdealquery.dto.CfetsHisDealQuery001Dto;
import com.joyintech.tams.invest.bankuplink.basic.cfetshisdealquery.service.CfetsHisDealQuery001Service;
import com.joyintech.tams.invest.bankuplink.basic.cfetshisdealquery.service.CfetsHisDealQuery002Service;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.bankuplink.repo.dto.RepoQuoteInfoDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;

/**
 * CFETS历史报价查询 Controller
 */
@Controller
@PageId("CfetsHisDealQuery001")
@RequestMapping("/invest/bankuplink/basic/cfetshisdealquery/CfetsHisDealQuery001")
public class CfetsHisDealQuery001Controller extends BaseController {

    /**
     * CFETS历史报价查询 Service
     */
    @Autowired
    CfetsHisDealQuery001Service cfetsHisDealQuery001Service;

    /**
     * CFETS历史报价质押式回购导出Service
     */
    @Autowired
    CfetsHisDealQuery002Service cfetsHisDealQuery002Service;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * {@linkplain}
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    /**
     * 初始化 Model
     * 
     * @param model
     * @return
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向);
        this.setSelectOptionToModel(model, BussinessDictCode.银行间成交状态);
        this.setSelectOptionToModel(model, BussinessDictCode.回购拆借方向);
        model.addAttribute("ps_pay_name", BussinessDictCodeList.交易方向_买入.getCodeName());
        model.addAttribute("ps_sell_name", BussinessDictCodeList.交易方向_卖出.getCodeName());
        model.addAttribute("quoteRoleTypeFZS", BussinessDictCodeList.银行间上行本方角色_非做市方.getCodeName());
        // Added by JIAOXUJIN on 2020-06-09
        model.addAttribute("loginTraderId",
            cfetsQuotedCommonService.queryLoginTraderId(RequestUtil.getPageParam()));
        return super.init(model);
    }

    /**
     * 现券买卖历史报价查询 list
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryHisDealList")
    public Page<BondQuoteInfoDto> queryHisDealList() {
        List<BondQuoteInfoDto> ChangeListForBondPage = cfetsHisDealQuery001Service.queryHisDealList(
            RequestUtil.getPageParam());
        return new Page<>(ChangeListForBondPage);
    }

    /**
     * 现券买卖历史报价查询 page
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryHisDealPage")
    public Page<BondQuoteInfoDto> queryHisDealPage() {
        // Page<BondQuoteInfoDto> a
        // =cfetsHisDealQuery001Service.queryHisDealPage(RequestUtil.getPageParam());
        return cfetsHisDealQuery001Service.queryHisDealPage(RequestUtil.getPageParam());
    }

    /**
     * 质押式回购历史报价查询 list
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryPledgeRepoList")
    public Page<RepoQuoteInfoDto> queryPledgeRepoList() {
        List<RepoQuoteInfoDto> ChangeListForRepoPage = cfetsHisDealQuery001Service.queryPledgeRepoList(
            RequestUtil.getPageParam());
        return new Page<>(ChangeListForRepoPage);
    }

    /**
     * 质押式回购历史报价查询 page
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryPledgeRepoPage")
    public Page<RepoQuoteInfoDto> queryPledgeRepoPage() {
        return cfetsHisDealQuery001Service.queryPledgeRepoPage(RequestUtil.getPageParam());
    }

    /**
     * 查询保价编号list
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryQuoteIdList")
    public List<SuggestDto> queryQuoteIdList() {
        Map<String, Object> map = RequestUtil.getPageParam();
        return cfetsHisDealQuery001Service.queryQuoteIdList(map);
    }

    /**
     * Cfest历史报价查询 导出
     * 
     * @return
     * @throws Exception
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export()
        throws Exception {
        Map<String, Object> param = RequestUtil.getPageParam();
        String tableId = (String)param.get("tableId");
        String date = GlobalConstant.OPEN_PARENTHESIS+DateUtil.format(DateUtil.platDateFromCache())+GlobalConstant.CLOSE_PARENTHESIS;
        if (StringUtil.equals(tableId, "dvpTransferTable")) {
            return cfetsHisDealQuery001Service.export("CFETS现券买卖历史报价查询"+date, param);
        }
        else if (StringUtil.equals(tableId, "transferTable")) {
            return cfetsHisDealQuery002Service.export("CFETS质押式回购历史报价查询"+date, param);
        }
        else {
            return null;
        }
    }

    /**
     * CFETS历史报价查询 共通check
     * 
     * @param dto
     * @param result
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody CfetsHisDealQuery001Dto dto, BindingResult result) {
        AjaxResult checkResult = new AjaxResult();
        ArrayList<ValidMessage> errorList = new ArrayList<>();
        Date today = iPlatformDate.getSysDate();

        if (DateUtil.compareDate(today, dto.getQuoteDate()) != 0) {
            errorList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_027")));
        }

        // 判断资产类型：bond债券、repo回购
        WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
        Map<String, Object> map = new HashMap<>();
        map.put("quoteId", dto.getQuoteId());
        if (StringUtil.equals(dto.getFlag(), "bond")) {
            Page<BondQuoteInfoDto> bondPage = cfetsHisDealQuery001Service.queryHisDealPage(map);
            if (bondPage == null || StringUtil.isEmptyList(bondPage.getRoot())
                || !StringUtil.equals(bondPage.getRoot().get(0).getTradeStatus(),
                    dto.getTradeStatus())){
                errorList.add(new ValidMessage(this.getMessage("CFETS_COMMON_E_038")));
            }else{
                if (!StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已成交.getValue())) {
                    errorList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_026")));
                }
            }

            if (StringUtil.equals(dto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_非做市方.getValue())) {
                wflValidInputParameter.setPageId("CfetsBondDealQuotedPrice003");
            }
            else {
                wflValidInputParameter.setPageId("CfetsBondDealQuotedPrice002");
            }
        }
        else if (StringUtil.equals(dto.getFlag(), "repo")) {
            Page<RepoQuoteInfoDto> repoPage = cfetsHisDealQuery001Service.queryPledgeRepoPage(map);
            if (repoPage == null || StringUtil.isEmptyList(repoPage.getRoot())
                || !StringUtil.equals(repoPage.getRoot().get(0).getTradeStatus(),
                    dto.getTradeStatus())){
                errorList.add(new ValidMessage(this.getMessage("CFETS_COMMON_E_038")));
            }else{
                if (!StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已成交.getValue())) {
                    errorList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_026")));
                }
            }
            wflValidInputParameter.setPageId("CfetsRepoDealQuotedPrice002");
        }
        wflValidInputParameter.setBusinessDataPk1(dto.getQuoteId());
        if (mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
            errorList.add(new ValidMessage(getMessage("CFETS_COMMON_E_037")));
        }

        if (CollectionUtils.isNotEmpty(errorList)) {
            checkResult.setErrorList(errorList);
        }

        return checkResult;
    }

    /**
     * 现券买卖历史报价 撤销
     * 
     * @param dto
     * @param br
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @RequestMapping("dvpRevoke")
    public AjaxResult dvpDelete(@RequestBody @Validated({
        ValidateGroupDelete.class}) CfetsHisDealQuery001Dto dto, BindingResult br)
        throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<CfetsHisDealQuery001Dto> processParameter = new ProcessParameter<CfetsHisDealQuery001Dto>();
        WorkFlowParameter<CfetsHisDealQuery001Dto> params = new WorkFlowParameter<CfetsHisDealQuery001Dto>();
        params.setPageId("CfetsHisDealQuery001");
        params.setElementId("CfetsHisDealQuery001_btn_bondRevoke");
        params.setBusinessCode(item -> item.getQuoteId());
        params.setBusinessType(BussinessDictCodeList.共同业务类型_银行间上行.getValue());
        //params.setBusinessType(dto.getQuoteId());
        dto.setFlag("01");// 01 现券买卖历史报价 02 质押式回购历史报价
        params.setComment(
            entity -> getMessage("CFETSHIS_QUOTEID") + "(" + entity.getQuoteId() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = cfetsHisDealQuery001Service.start(processParameter,
            dto.getSelectedItem().toArray(
                new CfetsHisDealQuery001Dto[dto.getSelectedItem().size()]));
        return new AjaxResult(msg);
    }

    /**
     * 质押式回购历史报价 撤销
     * 
     * @param dto
     * @param br
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @RequestMapping("revoke")
    public AjaxResult delete(@RequestBody @Validated({
        ValidateGroupDelete.class}) CfetsHisDealQuery001Dto dto, BindingResult br)
        throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<CfetsHisDealQuery001Dto> processParameter = new ProcessParameter<CfetsHisDealQuery001Dto>();
        WorkFlowParameter<CfetsHisDealQuery001Dto> params = new WorkFlowParameter<CfetsHisDealQuery001Dto>();
        params.setPageId("CfetsHisDealQuery001");
        params.setElementId("CfetsHisDealQuery001_btn_repoRevoke");
        params.setBusinessCode(item -> item.getQuoteId());
        params.setBusinessType(dto.getQuoteId());
        dto.setFlag("02");// 01 现券买卖历史报价 02 质押式回购历史报价
        params.setComment(
            entity -> getMessage("CFETSHIS_QUOTEID") + "(" + entity.getQuoteId() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = cfetsHisDealQuery001Service.start(processParameter,
            dto.getSelectedItem().toArray(
                new CfetsHisDealQuery001Dto[dto.getSelectedItem().size()]));
        return new AjaxResult(msg);
    }

}

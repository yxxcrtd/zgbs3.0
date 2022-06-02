/*
 * 文件名：EtrPlanAssetC002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2019年1月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrplanassetc.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.controller.EtrCommonController;
import com.joyintech.tams.invest.entrust.etrplanassetc.dto.EtrPlanAssetCDto;
import com.joyintech.tams.invest.entrust.etrplanassetc.service.EtrPlanAssetCService;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 计划类货币新建
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2019年1月3日
 * @see EtrPlanAssetC002Controller
 * @since 1.0
 */
@Controller
@PageId("EtrPlanAssetC002")
@RequestMapping("/invest/entrust/etrplanassetc/EtrPlanAssetC002")
public class EtrPlanAssetC002Controller extends BaseController {

    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 计划类货币service
     */
    @Autowired
    private EtrPlanAssetCService etrPlanAssetCService;

    @Autowired
    private PlatformDate platformDate;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向);
        /*添加二-四级分类数据字典 lishuping 2019/7/29 start*/
        this.setSelectOptionToModel(model, DictCode.计划类资产一级分类, DictCode.行内资产二级分类,
                DictCode.行内资产三级分类, DictCode.行内资产四级分类);
        /*添加二-四级分类数据字典 lishuping 2019/7/29 end*/
        this.setSelectOptionToModel(model, DictCode.币种);
//        Date sysDate = CacheUtil.getAppCache(GlobalConstant.NOW_SYS_DATE, Date.class);
        Date sysDate = platformDate.getSysDate(LoginUserUtil.getUserName());
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("T04", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("T06", DictCodeList.交易方向_赎回.getValue());
        model.addAttribute("T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T02", DictCodeList.交易方向_卖出.getValue());
        String entrustId = getParam("entrustId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            EtrPlanAssetCDto dto = etrPlanAssetCService.getBussinessEntityByWorkflowId(
                workflowId);
            dto.setProfitType(DictCodeList.收益类型_货币型.getValue());
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("riskTrialRetDto", JsonUtils.object2Json(dto.getRiskTrialRetDto()));
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",dto.getWorkflowId());
            // ZGZGST-2045:审批页面展示流程中的风险试算结果需要workflowId fuyongsheng 20210907 add start
            model.addAttribute("workflowId", workflowId);
            // ZGZGST-2045:审批页面展示流程中的风险试算结果需要workflowId fuyongsheng 20210907 add end
        }
        else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(entrustId)) {
            EtrPlanAssetCDto dto = etrPlanAssetCService.selectByKey(entrustId);
            if (dto.getEntrustAmt2() == null) {
                dto.setEntrustAmt2(dto.getEntrustAmt());
            }
            if (dto.getEntrustNum2() == null) {
                dto.setEntrustNum2(dto.getEntrustNum());
            }
            dto.setProfitType(DictCodeList.收益类型_货币型.getValue());
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("tradeId", dto.getEntrustId());
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId", dto.getEntrustId());
        }
      //新建初始化
        else if (WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(entrustId)) {
            EtrPlanAssetCDto entity = new EtrPlanAssetCDto();
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            entity.setProfitType(DictCodeList.收益类型_货币型.getValue());
            model.addAttribute(GlobalConstant.ENTITY, entity);
        }
        // ZGZGST-1428 交易对手必填 fuyongsheng 202108271501 add start
        model.addAttribute("single", "1");
        // ZGZGST-1428 交易对手必填 fuyongsheng 202108271501 add end
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        return super.init(model);
    }
    
    /**
     * 获取组合代码联想文本框信息 <功能详细描述>
     * 
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getentrustIdAdd")
    public Page<SelectOptionDto> getentrustIdAdd() {
        Page<SelectOptionDto> temp = etrPlanAssetCService.getentrustIdAdd(RequestUtil.getPageParam());
        return temp;
    }
    /**
     * 
     * 组合可用现金查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getProAvailableCash")
    public Map<String, Object> getProAvailableCash(@RequestBody Map<String, Object> param){
        return etrPlanAssetCService.getProAvailableCash(param);
    }
    /**
     * 
     * 组合可用持仓查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAssetAvailablePosition")
    public BigDecimal getAssetAvailablePosition(@RequestBody EtrSecPositionDto dto){
        return etrPlanAssetCService.getAssetAvailablePosition(dto);
    }
    /**
     * 获取新建界面的资产代码联想下拉框框
     * 
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getfinprodIdForAdd")
    public Page<SelectOptionDto> getfinprodIdForAdd() {
        Page<SelectOptionDto> temp = etrPlanAssetCService.getfinprodIdForAdd(RequestUtil.getPageParam());
        return temp;
    }
    /**
     * 保存按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) EtrPlanAssetCDto dto, BindingResult result) {
        // add by zhangpuyu 2019/1/20 start
        String confirmMsg = "";
        String changeLineMark = "<br/>";
        // 可用现金软校验
        if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
                DictCodeListExt1.是或否_是.getValue())
                && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                    ||StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()))
                && dto.getProAvailableCash() != null
                && dto.getEntrustAmt() != null) {
            if (dto.getEntrustAmt().compareTo(dto.getProAvailableCash()) == 1) {
                confirmMsg = this.getMessage("TRD_C_0002");
            }
        }
        // 可用持仓软校验
        else if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                DictCodeListExt1.是或否_是.getValue())){
            if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
            		&& dto.getEntrustNum2() != null){
                if (dto.getEntrustNum2().compareTo(dto.getAssetAvailablePosition()) > 0){
                    confirmMsg = this.getMessage("ETR_X_019");
                }
            }else if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
            		&& dto.getEntrustNum() != null){
                if (dto.getEntrustNum().compareTo(dto.getAssetAvailablePosition()) > 0){
                    confirmMsg = this.getMessage("ETR_X_019");
                }
            }
        }
        // add by zhangpuyu 2019/1/20 start
        return commonCheck(result, dto.getOperateType());
    }
    
    @RequestMapping("checkAuthorization")
    @ResponseBody
    public AjaxResult checkAuthorization(@RequestBody EtrPlanAssetCDto dto) {
        //授权管理  委托授权额度check
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH),"01")){
            EtrCommonController etrCommonController = SpringContextUtil.getBean(
                EtrCommonController.class);
            return etrCommonController.授权是否匹配(dto);
        }
        return new AjaxResult();
    }
    
    /**
     * 保存/提交
     * 
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrPlanAssetCDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getEntrustId())){
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_投资委托);
            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
            dto.setEntrustId(code);
        }
        // ----------add by yushilong 20200721 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
                    DictCodeList.平安资产类型_计划类资产_货币型.getValue());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by yushilong 20200721 预占功能 end----------
        ProcessParameter<EtrPlanAssetCDto> processParameter = new ProcessParameter<EtrPlanAssetCDto>();
        WorkFlowParameter<EtrPlanAssetCDto> params = new WorkFlowParameter<EtrPlanAssetCDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getEntrustId());
        params.setBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_投资委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        if(dto.getEntrustAmt() != null){
            params.setComment(
                entity -> this.getMessage("EtrPlanAssetC001") + "(" + entity.getPortfolioId() + ")"
                          + "-" + this.getMessage("EtrPlanAssetC001.ps")+"("+SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs())+")" + "-" + "("
                          + this.getMessage("EtrPlanAssetC001.vdate") + ":" + DateUtil.format(entity.getVdate()) 
                          + ")-(" + this.getMessage("EtrPlanAssetC001.mdate") + ":"
                          + DateUtil.format(entity.getMdate()) + ")-"
                          + this.getMessage("EtrPlanAssetC001.entrustAmt") + ":"
                          + entity.getEntrustAmt());
        }else if(dto.getEntrustNum() != null){
            params.setComment(
                entity -> this.getMessage("EtrPlanAssetC001") + "(" + entity.getPortfolioId() + ")"
                          + "-" + this.getMessage("EtrPlanAssetC001.ps")+"("+SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs())+")" + "-" + "("
                          + this.getMessage("EtrPlanAssetC001.vdate") + ":" + DateUtil.format(entity.getVdate()) 
                          + ")-(" + this.getMessage("EtrPlanAssetC001.mdate") + ":"
                          + DateUtil.format(entity.getMdate()) + ")-"
                          + this.getMessage("EtrPlanAssetC002.entrustNum") + ":"
                          + entity.getEntrustNum());
        }else if(dto.getEntrustNum2() != null){
            params.setComment(
                entity -> this.getMessage("EtrPlanAssetC001") + "(" + entity.getPortfolioId() + ")"
                          + "-" + this.getMessage("EtrPlanAssetC001.ps")+"("+SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs())+")" + "-" + "("
                          + this.getMessage("EtrPlanAssetC001.vdate") + ":" + DateUtil.format(entity.getVdate()) 
                          + ")-(" + this.getMessage("EtrPlanAssetC001.mdate") + ":"
                          + DateUtil.format(entity.getMdate()) + ")-"
                          + this.getMessage("EtrPlanAssetC002.entrustNum") + ":"
                          + entity.getEntrustNum2());
        }else{
            params.setComment(entity -> this.getMessage("计划类资产（货币型）投资委托"));// add by xxg 20210803
        }
        processParameter.setWorkFlowParameter(params);
        String msg = etrPlanAssetCService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}

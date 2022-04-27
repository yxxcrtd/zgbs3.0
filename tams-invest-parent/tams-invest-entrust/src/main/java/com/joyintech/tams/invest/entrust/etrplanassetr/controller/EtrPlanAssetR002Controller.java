/*
 * 文件名：EtrInvestBondInfo002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrplanassetr.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.WorkflowContext;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.common.controller.EtrCommonController;
import com.joyintech.tams.invest.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.invest.entrust.etrplanassetr.service.EtrPlanAssetRService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 债券投资委托新建 〈功能详细描述〉
 * 
 * @author zhangjun
 * @version 1.0 2018年9月10日
 * @see EtrPlanAssetR002Controller
 * @since 1.0
 */
@Controller
@PageId("EtrPlanAssetR002")
@RequestMapping("/invest/entrust/etrplanassetr/EtrPlanAssetR002")
public class EtrPlanAssetR002Controller extends BaseController {
    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 债券投资委托service
     */
    @Autowired
    private EtrPlanAssetRService etrPlanAssetService;

    /**
     * 初始化002新建页面
     */
    @Override
    public String init(Model model) {
        
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model
                , DictCode.计划类资产一级分类
                /*2,3,4数据字典、zhuyongkang 2019/7/29  start */
                , DictCode.行内资产二级分类
                , DictCode.行内资产三级分类
                , DictCode.行内资产四级分类
                , DictCode.投资目的, DictCode.业务模式);
                /*原因、zhuyongkang 2019/7/29  start */
        this.setSelectOptionToModel(model, DictCode.币种);
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate",DateUtil.format(sysDate));

        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
       //展示投资目的或业务模式参数
     // 一体化对应etliu start
//        model.addAttribute("T04", DictCodeList.交易方向_申购.getValue());
//        model.addAttribute("T06", DictCodeList.交易方向_赎回.getValue());
        model.addAttribute("T04", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T06", DictCodeList.交易方向_卖出.getValue());
     // 一体化对应etliu end
        
        String entrustId = getParam("entrustId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            EtrPlanAssetRDto dto = etrPlanAssetService.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",dto.getWorkflowId());
            model.addAttribute("single", "1");
        }
        else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(entrustId)) {
            EtrPlanAssetRDto dto = etrPlanAssetService.selectByKey(entrustId);
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("tradeId", dto.getEntrustId());
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId", dto.getEntrustId());
            model.addAttribute("single", "1");

            // 获取workflowId 加载风险试算结果
            EtrPlanAssetRDto dto1 = new EtrPlanAssetRDto();
            dto1.setEntrustId(entrustId);
            ProcessMark processMark = WorkflowContext.getProcessMarkService().getParameterByAnnotation(dto1);
            //转换当前页面
            processMark.setPageId("EtrPlanAssetR002");
            String workflowId_T = WorkflowContext.getProcessMarkService().getLastWorkflowId(processMark);
            model.addAttribute("workflowId", workflowId_T);
        }
        else if (WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(entrustId)) {
            EtrPlanAssetRDto entity = new EtrPlanAssetRDto();
            model.addAttribute("single", "1");
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            model.addAttribute(GlobalConstant.ENTITY, entity);
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
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
    public Page<SelectOptionDto> getentrustIdAdd(){
        Page<SelectOptionDto> temp = etrPlanAssetService.getentrustIdAdd(RequestUtil.getPageParam());
        return temp;
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
        Page<SelectOptionDto> temp = etrPlanAssetService.getfinprodIdForAdd(RequestUtil.getPageParam());
        return temp;
    }
    /**
     * 
     * 通过资产代码带出资产类型
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/searchType")
    public String searchType(String finprodId){
        return StringUtil.trimHtmlTag(etrPlanAssetService.searchType(finprodId));
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
        return etrPlanAssetService.getProAvailableCash(param);
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
        return etrPlanAssetService.getAssetAvailablePosition(dto);
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
        ValidateGroupEdit.class}) EtrPlanAssetRDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }

        // add by zhangpuyu 2019/1/20 start
        String confirmMsg = "";
        String changeLineMark = "<br/>";
        // 可用现金软校验
        if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
                DictCodeListExt1.是或否_是.getValue())
                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                && dto.getProAvailableCash() != null
                && dto.getEntrustAmt()!=null) {
            if (dto.getEntrustAmt().compareTo(dto.getProAvailableCash()) == 1) {
                confirmMsg = this.getMessage("TRD_C_0002");
            }
        }
        // 可用持仓软校验
        else if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                DictCodeListExt1.是或否_是.getValue())
                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                && dto.getAssetAvailablePosition() != null
                && dto.getEntrustNum()!=null) {
            if (dto.getEntrustNum().compareTo(dto.getAssetAvailablePosition()) > 0) {
                confirmMsg = this.getMessage("ETR_X_019");
            }
        }
        // add by zhangpuyu 2019/1/20 start
        
        return commonCheck(result, dto.getOperateType());
    }
    
    @RequestMapping("checkAuthorization")
    @ResponseBody
    public AjaxResult checkAuthorization(@RequestBody EtrPlanAssetRDto dto) {
        //授权管理  委托授权额度check
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH),"01")){
            EtrCommonController etrCommonController = SpringContextUtil.getBean(
                EtrCommonController.class);
            return etrCommonController.授权是否匹配(dto);
        }
        return new AjaxResult();
    }
    
    /**
     * 
     * 委托存款保存业务流程
     * <功能详细描述>
     * @param dto 业务实体
     * @param br AjaxResult
     * @return AjaxResul
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrPlanAssetRDto dto, BindingResult br) throws Exception {//去除指令暂存校验 add by xxg 20210802
        if(br.hasErrors()){
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getEntrustId())){
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_利率型_投资委托);
            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
            dto.setEntrustId(code);
        }
        // ----------add by yushilong 20200721 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
                    DictCodeList.平安资产类型_计划类资产_利率型.getValue());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by yushilong 20200721 预占功能 end----------
        ProcessParameter<EtrPlanAssetRDto> processParameter = new ProcessParameter<EtrPlanAssetRDto>();
        WorkFlowParameter<EtrPlanAssetRDto> params = new WorkFlowParameter<EtrPlanAssetRDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item->item.getEntrustId());
        params.setBusinessType(DictCodeList.共通业务类型_计划类资产_利率型_投资委托.getValue());

        // 设置流程业务说明, entity即传入的dto
        //业务说明：拆借委托投资+"("+组合代码+")"+"-"+交易方向+"-"+"（委托开始日："+委托开始日+"）-"+"（委托结束日："+委托结束日+"）-"+"金额："+金额"
        params.setComment(
            entity -> SpringContextUtil.getTextValue("EtrPlanAssetR001")+ "(" + entity.getPortfolioId()+ ")" 
                +  "-" + SpringContextUtil.getTextValue("EtrPlanAssetR001.ps")+ "(" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs())+ ")"  +  "-" + "("
                + SpringContextUtil.getTextValue("EtrPlanAssetR001.vdate")+"：" + DateUtil.format(entity.getVdate())+ ")"
                +  "-" + "("+SpringContextUtil.getTextValue("EtrPlanAssetR001.mdate")+"：" + DateUtil.format(entity.getMdate())+ ")-"+SpringContextUtil.getTextValue("EtrPlanAssetR001.entrustAmt1")+"："+ (entity.getEntrustAmt() != null ? entity.getEntrustAmt() : entity.getEntrustNum()));
        processParameter.setWorkFlowParameter(params);
        String msg = etrPlanAssetService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}

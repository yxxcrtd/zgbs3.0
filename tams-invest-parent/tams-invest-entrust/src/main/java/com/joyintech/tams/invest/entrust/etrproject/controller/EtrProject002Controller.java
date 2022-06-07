/*
 * 文件名：EtrProject002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrproject.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
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
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.common.controller.EtrCommonController;
import com.joyintech.tams.invest.entrust.etrproject.dto.EtrProjectDto;
import com.joyintech.tams.invest.entrust.etrproject.service.EtrProjectService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 项目类委托新建
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年10月11日
 * @see EtrProject002Controller
 * @since 1.0
 */
@Controller
@PageId("EtrProject002")
@RequestMapping("/invest/entrust/etrproject/EtrProject002")
public class EtrProject002Controller extends BaseController {
    
    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;

    /**
     * 计划类净值委托service
     */
    @Autowired
    private EtrProjectService etrProjectService;

    /**
     * 页面初始化
     */
    @Override
    public String init(Model model) {
        
        this.setSelectOptionToModel(model, DictCode.币种);
        this.setSelectOptionToModel(model, DictCode.项目类一级分类);
        this.setSelectOptionToModel(model, DictCode.交易方向);
        // 项目类资产五级分类改造 add by renping 2019-08-01 start
        this.setSelectOptionToModel(model, DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类,
        		DictCode.投资目的, DictCode.业务模式);
        // 项目类资产五级分类改造 add by renping 2019-08-01 end
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        model.addAttribute("T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T02", DictCodeList.交易方向_卖出.getValue());
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        String entrustId = getParam("entrustId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        RequestUtil.getPageParam();
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            EtrProjectDto dto = etrProjectService.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",dto.getWorkflowId());
        }
        else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(entrustId)) {
            EtrProjectDto dto = etrProjectService.selectByKey(entrustId);
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("tradeId", dto.getEntrustId());
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",dto.getEntrustId());
        }
      //新建初始化
        else if (WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(entrustId)) {
            EtrProjectDto entity = new EtrProjectDto();
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            model.addAttribute(GlobalConstant.ENTITY, entity);
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
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
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getentrustIdAdd")
    public Page<SelectOptionDto> getentrustIdAdd(){
        Page<SelectOptionDto> temp = etrProjectService.getentrustIdAdd(RequestUtil.getPageParam());
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
        Page<SelectOptionDto> temp = etrProjectService.getfinprodIdForAdd(RequestUtil.getPageParam());
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
        return StringUtil.trimHtmlTag(etrProjectService.searchType(finprodId));
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
        return etrProjectService.getProAvailableCash(param);
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
        return etrProjectService.getAssetAvailablePosition(dto);
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
        ValidateGroupEdit.class}) EtrProjectDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        
        // 可用现金软校验
        if (dto.getProAvailableCash() != null) {
           if (dto.getEntrustAmt().compareTo(dto.getProAvailableCash()) == 1) {
               AjaxResult ar = new AjaxResult();
               ar.setData(this.getMessage("TRD_C_0002"));
               return ar;
           }
        }
        
        return commonCheck(result, dto.getOperateType());
    }
    
    @RequestMapping("checkAuthorization")
    @ResponseBody
    public AjaxResult checkAuthorization(@RequestBody EtrProjectDto dto) {
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
     * @throws WorkFlowException 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrProjectDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getEntrustId())){
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_项目类资产投资委托);
            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
            dto.setEntrustId(code);
        }
        // ----------add by yushilong 20200721 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
                    DictCodeList.平安资产类型_项目类资产.getValue());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by yushilong 20200721 预占功能 end----------
        ProcessParameter<EtrProjectDto> processParameter = new ProcessParameter<EtrProjectDto>();
        WorkFlowParameter<EtrProjectDto> params = new WorkFlowParameter<EtrProjectDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getEntrustId());
        params.setBusinessType(DictCodeList.共通业务类型_项目类资产投资委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> this.getMessage("EtrProject001") + "(" + entity.getPortfolioId() + ")"
                      + "-" + this.getMessage("EtrProject001.ps") + "-" + "("
                      + this.getMessage("EtrProject001.vdate") + ":" +DateUtil.format(entity.getVdate())
                      + ")-(" + this.getMessage("EtrProject001.mdate") + ":"
                      +DateUtil.format(entity.getMdate())+ ")-"
                      + this.getMessage("EtrProject001.entrustAmt") + ":"
                      + entity.getEntrustAmt());
        processParameter.setWorkFlowParameter(params);
        String msg = etrProjectService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}

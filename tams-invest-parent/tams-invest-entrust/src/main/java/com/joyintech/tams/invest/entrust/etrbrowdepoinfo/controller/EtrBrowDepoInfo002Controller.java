/*
 * 文件名：EtrBrowDepoInfo002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrbrowdepoinfo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.controller.EtrCommonController;
import com.joyintech.tams.invest.entrust.etrbrowdepoinfo.dto.EtrBrowDepoInfoDto;
import com.joyintech.tams.invest.entrust.etrbrowdepoinfo.service.EtrBrowDepoInfoService;
import com.joyintech.tams.invest.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.WorkflowContext;
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
import java.util.Objects;

/**
 * 存款委托新建
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年9月20日
 * @see EtrBrowDepoInfo002Controller
 * @since 1.0
 */
@Controller
@PageId("EtrBrowDepoInfo002")
@RequestMapping("/invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo002")
public class EtrBrowDepoInfo002Controller extends BaseController{
    
    /**
     * 自定义service
     */
    @Autowired
    private EtrBrowDepoInfoService etrBrowDepoInfoService;
    
    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    
    /**
     * 页面初始化
     */
    @Override
    public String init(Model model) {
        
        this.setSelectOptionToModel(model, DictCode.币种);
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model, DictCode.存款类资产类型);
        // 新增资产二、三、四级分类 王贵奇 2019/8/1 开始
        this.setSelectOptionToModel(model, DictCode.行内资产二级分类);
        this.setSelectOptionToModel(model, DictCode.行内资产三级分类);
        this.setSelectOptionToModel(model, DictCode.行内资产四级分类);
        // 新增资产二、三、四级分类 王贵奇 2019/8/1 结束
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        EtrBrowDepoInfoDto dto = new EtrBrowDepoInfoDto();
        dto.setPs(DictCodeList.交易方向_买入.getValue());
        dto.setVdate(sysDate);
        dto.setMdate(sysDate);
        /*dto.setvDate(sysDate);
        dto.setmDate(sysDate);*/
        //dto.setFinprodType2("01");
        dto.setCcy("CNY");
        model.addAttribute(GlobalConstant.ENTITY, dto);
        model.addAttribute("YES",DictCodeList.是否_是.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String entrustId = getParam("entrustId");
        String finprodId = getParam("finprodId");
        String portfolioId = getParam("portfolioId");
        //
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            
            EtrBrowDepoInfoDto etrBrowDepoInfoDto = etrBrowDepoInfoService.getBussinessEntityByWorkflowId(
                workflowId);
            //前端js保存时已经除以100了，所以展示要乘以100 add by daizhiwei 20200223
            if (etrBrowDepoInfoDto.getPrice() != null){
                etrBrowDepoInfoDto.setPrice(etrBrowDepoInfoDto.getPrice().multiply(new BigDecimal(100)));
            }
            model.addAttribute(GlobalConstant.ENTITY, etrBrowDepoInfoDto);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("person_entity", JsonUtils.object2Json(etrBrowDepoInfoDto.getBussEtrInvestPersonInfoDto()));
            model.addAttribute("businessId", workflowId);
        }
        else if (StringUtil.isNotEmpty(entrustId) && WorkFlowUtil.isNotFromToDoList()) {
            EtrBrowDepoInfoDto etrBrowDepoInfoDto = etrBrowDepoInfoService.selectByKey(entrustId,finprodId,portfolioId);
            BussEtrInvestPersonInfoDto personInfoDto = etrBrowDepoInfoDto.getBussEtrInvestPersonInfoDto();
            model.addAttribute(GlobalConstant.ENTITY, etrBrowDepoInfoDto);
            model.addAttribute("tradeId", entrustId);
//            model.addAttribute("person_entity", JsonUtils.object2Json(personInfoDto));
            model.addAttribute("businessId", etrBrowDepoInfoDto.getEntrustId());

            // 获取workflowId 加载风险试算结果
            EtrBrowDepoInfoDto dto1 = new EtrBrowDepoInfoDto();
            dto1.setEntrustId(entrustId);
            ProcessMark processMark = WorkflowContext.getProcessMarkService().getParameterByAnnotation(dto1);
            //转换当前页面
            processMark.setPageId("EtrBrowDepoInfo002");
            String workflowId_T = WorkflowContext.getProcessMarkService().getLastWorkflowId(processMark);
            model.addAttribute("workflowId", workflowId_T);

            if (Objects.nonNull(personInfoDto) && StringUtil.isEmpty(personInfoDto.getTrader())) {
                EtrPlanAssetRDto planAssetRDto = etrPlanAssetService.getBussinessEntityByWorkflowId(workflowId_T);
                if (Objects.nonNull(planAssetRDto.getEtrInvestPersonInfo())) {
                    personInfoDto.setTrader(planAssetRDto.getEtrInvestPersonInfo().getTrader());
                }
            }
            model.addAttribute("person_entity", JsonUtils.object2Json(personInfoDto));
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("T01",DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T02",DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
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
    @RequestMapping("/getSelectPortfolioId")
    public Page<SelectOptionDto> getSelectPortfolioId(){
        Page<SelectOptionDto> temp = etrBrowDepoInfoService.getSelectPortfolioId(RequestUtil.getPageParam());
        return temp;
    }

    /**
     * 获取通道
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/getChannelId")
    public Page<SelectOptionDto> getChannelId(){
        Page<SelectOptionDto> temp = etrBrowDepoInfoService.getChannelId(RequestUtil.getPageParam());
        return temp;
    }

    /**
     * 获取通道可用现金
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/getChannelAvailableCash")
    public BigDecimal getChannelAvailableCash(@RequestBody Map<String, Object> param){
        BigDecimal bigDecimal = etrBrowDepoInfoService.getChannelAvailableCash(param);
        if(bigDecimal != null){
            return bigDecimal;
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取新建界面的资产代码联想下拉框框
     * 
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getSelectFinprodMarketId")
    public Page<SelectOptionDto> getSelectFinprodMarketId() {
        Page<SelectOptionDto> temp = etrBrowDepoInfoService.getSelectFinprodMarketId(RequestUtil.getPageParam());
        return temp;
    }
    
    /**
     * 保存按钮点击时的check
     *
     * @param dto 业务实体
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) EtrBrowDepoInfoDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        
        // 可用现金软校验
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
            && dto.getProAvailableCash() != null) {
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
    public AjaxResult checkAuthorization(@RequestBody EtrBrowDepoInfoDto dto) {
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
    public AjaxResult save(@RequestBody EtrBrowDepoInfoDto dto, BindingResult br) throws Exception{//保存时，去除校验 add by xxg 20210802
        if(br.hasErrors()){
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getEntrustId())){
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_存款类资产投资委托);
            param.setType1(StringUtil.equals(dto.getFinprodType2(),DictCodeList.存款类资产类型_同业借款.getValue())
                ?DictCodeList.业务编号一级分类_同业借款:DictCodeList.业务编号一级分类_存放同业);
            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
            //画面.资产类型==数据字典缓存.存款类资产类型.同业借款？数据字典缓存.业务编号一级分类.同业借款:数据字典缓存.业务编号一级分类.存放同业
            dto.setEntrustId(code);
        }
        // ----------add by yushilong 20200721 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
                    DictCodeList.平安资产类型_存款.getValue());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by yushilong 20200721 预占功能 end----------
        ProcessParameter<EtrBrowDepoInfoDto> processParameter = new ProcessParameter<EtrBrowDepoInfoDto>();
        WorkFlowParameter<EtrBrowDepoInfoDto> params = new WorkFlowParameter<EtrBrowDepoInfoDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item->item.getEntrustId());
        params.setBusinessType(DictCodeList.共通业务类型_存款类资产投资委托.getValue());

        // 设置流程业务说明, entity即传入的dto
        //业务说明：存款委托投资+"("+组合代码+")"+"-"+交易方向+"-"+"（委托开始日："+委托开始日+"）-"+"（委托结束日："+委托结束日+"）-"+"金额："+金额"
        params.setComment(
            entity -> getMessage("EtrBrowDepoInfo001")+ "(" + entity.getPortfolioId()+ ")" 
                +  "-" + getMessage("EtrBrowDepoInfo001.ps")+ "(" + entity.getPsStr() + ")"  +  "-" + 
                "("+getMessage("EtrBrowDepoInfo001.entrustVDate")+"：" + DateUtil.format(entity.getVdate()) + ")"
                +  "-("+getMessage("EtrBrowDepoInfo001.entrustMDate")+"：" + DateUtil.format(entity.getMdate()) + ")-"+getMessage("EtrBrowDepoInfo001.entrustAmt")+"："+entity.getEntrustAmt());
        processParameter.setWorkFlowParameter(params);
        //dto.getBussEtrInvestPersonInfoDto().setInvManagerId(SecurityUtil.getLoginUser().getUserName());
        String msg = etrBrowDepoInfoService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    
    /**
     * 
     * 组合可用现金查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getProAvailableCash")
    public Map<String, Object> getProAvailableCash(@RequestBody Map<String, Object> param){
        return etrBrowDepoInfoService.getProAvailableCash(param);
    }
    
    /**
     * 
     * 组合T+1可用现金查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getProAvailableCash1")
    public BigDecimal getProAvailableCash1(@RequestBody PtlCashPositionFlowDto dto){
        return etrBrowDepoInfoService.getProAvailableCash1(dto);
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
        return etrBrowDepoInfoService.getAssetAvailablePosition(dto);
    }

}

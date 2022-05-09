/*
 * 文件名：FinPlanProfitAsset002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.plan.planProfit.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
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
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.asset.plan.planProfit.dto.FinPlanProfitAssetDto;
import com.joyintech.tams.invest.asset.plan.planProfit.service.FinPlanProfitAssetService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 计划类资产（利率型）新建和编辑controller
 * 
 * @author liuwei
 * @version 1.0 2018年9月14日
 * @see FinPlanProfitAsset002Controller
 * @since 1.0
 */
@Controller
@PageId("FinPlanProfitAsset002")
@RequestMapping("/invest/asset/plan/planProfit/FinPlanProfitAsset002")
public class FinPlanProfitAsset002Controller extends BaseController{
    
    /**
     * 计划类资产（利率型）service
     */
    @Autowired
    private FinPlanProfitAssetService finPlanProfitAssetService;
    /**
     * 平台日期组件接口
     */
    @Autowired(required=false)
    private IPlatformDate iPlatformDate;

    /**
     * 业务编号共通组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId baseGetBussinessId;

    /**
     * 各种业务共通组件
     */
    @Autowired(required=false)
    private CommonBusinessService commonBusinessService;

    /**
     * 计划类资产（利率型）新建界面的初始化
     * @return 初始化结果
     * @see
     * @since 1.0
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model){
        this.setSelectOptionToModel(model,
        		DictCode.计划类资产一级分类,
        		DictCode.计划类资产二级分类,
        		/* 添加数据字典、zhuyongkang、2019/7/19、start*/
        		DictCode.行内资产二级分类,
        		DictCode.行内资产三级分类,
        		DictCode.行内资产四级分类,
        		DictCode.行内资产五级分类,
        		/* 添加数据字典、zhuyongkang、2019/7/19、start*/
        		DictCode.清算速度,
                DictCode.币种,
                DictCode.本金收益特征,
                DictCode.计息基础,
                DictCode.资产_负债二级类别,
                DictCode.投资性质,
                DictCode.资产来源,
                DictCode.是否,
                DictCode.资产来源条线,
                DictCode.增信措施);
        model.addAttribute("FINPROD_TYPE_TG_F17",DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        //资产类型
        model.addAttribute("INT_ASSET_TYPE", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.addAttribute("YES", DictCodeList.是否_是.getValue());
        model.addAttribute("ASSET_TYPE_PARENTPLAN", DictCodeListExt1.金融产品类型_投管用_母资管计划.getValue());
        model.addAttribute("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
        model.addAttribute("YES", DictCodeList.是否_是.getValue());
        String finprodId = getParam("finprodId");
        /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
        String finprodMarketId=getParam("finprodMarketId");
        boolean isQuickInput = Boolean.parseBoolean(getParam("isQuickInput"));
        if (StringUtil.isNotEmpty(finprodMarketId)&&isQuickInput){
            finprodId=finPlanProfitAssetService.getFinprodId(finprodMarketId);
        }
        /* 增加快捷录入 add by zhuyongkang 2019/10/12 end*/
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        //来自代办
        if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            // 根据工作流与留痕处理提供的共通方法获取业务实体
            FinPlanProfitAssetDto finPlanAssetRInfo = finPlanProfitAssetService.getBussinessEntityByWorkflowId(
                    workflowId);
            // 设置到model中
            model.addAttribute(GlobalConstant.WORKFLOW_ID,workflowId);
            model.addAttribute("entity", finPlanAssetRInfo);
        }else{
          //新建界面
            if(!StringUtil.isNotEmpty(finprodId)&& !StringUtil.isNotEmpty(workflowId)){
                FinPlanProfitAssetDto dto = new FinPlanProfitAssetDto();
                dto.setVdate(iPlatformDate!=null?iPlatformDate.getSysDate():DateUtil.now());
                dto.setMdate(iPlatformDate!=null?iPlatformDate.getSysDate():DateUtil.now());
                dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                dto.setTransMode(DictCodeList.清算速度_T_0.getValue());
                dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
                dto.setFjId("fj" + System.currentTimeMillis());
                model.addAttribute("entity", dto);
            }
            //编辑或查看界面
            else if(StringUtil.isNotEmpty(finprodId) && !StringUtil.isNotEmpty(workflowId)){
                if (!isQuickInput){
                    model.addAttribute("isTransInAsset", commonBusinessService.isTransInAsset(finprodId)!=0?true:false);
                }
                FinPlanProfitAssetDto finPlanAssetRInfo = finPlanProfitAssetService.getBasicInfoByFinprodId(finprodId);
                //finPlanAssetRInfo.setReferRate(finPlanAssetRInfo.getInvestRate().subtract(finPlanAssetRInfo.getBuyOutRate()));
                if(null!=finPlanAssetRInfo){
                    finPlanAssetRInfo.setFjId(finPlanAssetRInfo.getFinprodId());
                    //finPlanAssetRInfo.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
                    if(finPlanAssetRInfo.getBuyOutRate() !=null){
                    	finPlanAssetRInfo.setBuyOutRate(finPlanAssetRInfo.getBuyOutRate().multiply(new BigDecimal(100)));
                    }
                }
                model.addAttribute("entity",finPlanAssetRInfo);
                model.addAttribute("finprodId", finprodId);
            }
        }
        model.addAttribute("publicOffPlan", DictCodeList.计划类资产二级分类_公募计划.getValue());
        model.addAttribute("line",DictCodeListExt1.资产来源_行内.getValue());

        model.addAttribute("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
        /*model.addAttribute("isFromTodo", !WorkFlowUtil.isNotFromToDoList());*/
      //取系统参数
        model.addAttribute("CE_MODEL", CacheUtil.getSysParam(BussinessConstant.CEMODEL));
        model.addAttribute("INVEST_01", DictCodeList.投资性质_固定收益类.getValue());
        model.addAttribute("INVEST_02", DictCodeList.投资性质_权益类.getValue());
        model.addAttribute("PLAN_ASSET_SEC_TYPE_02", DictCodeList.计划类资产二级分类_委托债权.getValue());
        model.addAttribute("PLAN_ASSET_SEC_TYPE_03", DictCodeList.计划类资产二级分类_委托权益.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        model.addAttribute("COMMON_BUSINESS_TYPE", DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        model.addAttribute("ISChongQing", CacheUtil.getSysParam("ISChongQing"));
        return super.init(model);
    }
    /**
     * 放款分行的联想下拉框事件
     *
     * @return 查询的资产代码和资产名称列表
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getBank")
    public List<SelectOptionDto> getBank(){
        Map<String,Object>map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = finPlanProfitAssetService.getBank(map);
        return temp;
    }
    /**
     * 出账编号的联想下拉框事件
     *
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCmsId")
    public List<SelectOptionDto> getCmsId(){
        Map<String,Object>map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = finPlanProfitAssetService.getCmsId(map);
        return temp;
    }
    /**
     * 出账编号联动事件
     *
     * @param cmsId 出账编号
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getChange")
    public FinPlanProfitAssetDto getChange(String cmsId){
        return finPlanProfitAssetService.getChange(cmsId);
    }

//    /**
//     * 资产代码自动生成
//     *
//     * @return 查询结果
//     * @see
//     * @since 1.0
//     */
//    @ResponseBody
//    @RequestMapping("/generateFinprodMarketId")
//    public String generateFinprodMarketId(){
//        Map<String,Object>map = RequestUtil.getPageParam();
//        String category="";
//        // 业务代码生成参数
//        BussinessCodeParameter parameter = new BussinessCodeParameter();
//        parameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_利率型_信息);
//        if(StringUtil.equals(""))
//        if(map.get("category").toString() == "N01") {
//            category = "05";
//        }
//        else if(map.get("category").toString() == "N02"){
//            category = "06";
//        }
//        else if(map.get("category").toString() == "N04"){
//            category = "07";
//        }
//        parameter.setType1(DictCodeList.from(DictCode.计划类资产一级分类, category));
//        // 修改业务编号生成方式
//        String code = baseGetBussinessId.buildBussinessCode(parameter);
//        return code;
//    }

    /**
     * 管理人的联想下拉框事件
     *
     * @return 查询的资产代码和资产名称列表
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getManager")
    public Page<SelectOptionDto> getManager(){
        Map<String,Object> map = RequestUtil.getPageParam();
        Page<SelectOptionDto> temp = finPlanProfitAssetService.getManager(map);
        return temp;
    }
    
    /**
     * 托管人的联想下拉框事件
     *
     * @return 查询的资产代码和资产名称列表
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getTrusteeId")
    public List<SelectOptionDto> getTrusteeId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = finPlanProfitAssetService.getTrusteeId(map);
        return temp;
    }
    
    /**
     * 交易日历的联想下拉框事件
     *
     * @return 查询的资产代码和资产名称列表
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCalendarId")
    public List<SelectOptionDto> getCalendarId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = finPlanProfitAssetService.getCalendarId(map);
        return temp;
    }
    /**
     * 资产提供行的多选下拉框事件
     * 
     * @return 查询的资产代码和资产名称列表
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getBankList")
    public List<SelectOptionDto> getBankList(){
        Map<String,Object>map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = finPlanProfitAssetService.getBankList(map);
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
            ValidateGroupEdit.class}) FinPlanProfitAssetDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }

        if (StringUtil.equals(dto.getParentPlan(), DictCodeList.是否_否.getValue())) {
            if (null == dto.getBuyOutRate()) {
                AjaxResult res= new AjaxResult();
                List<ValidMessage> errorList = new ArrayList<>();
                ValidMessage validMessage = new ValidMessage("buyOutRate",
                        getMessage("COMMON_E_0057", getMessage("FinPlanProfitAsset002.buyOutRate")));
                errorList.add(validMessage);
                res.setErrorList(errorList);
                res.setMessageType(MessageType.错误消息);
                return res;
            }
        }
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存操作
     *
     * @param dto dto
     * @param br BindingResult
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) FinPlanProfitAssetDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getFinprodMarketId())){
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_利率型_信息);
            if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_委托债权.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_委托债券);
            }
            else if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_委托权益.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_委托权益);
            }
            else if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_公募计划.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_公募计划);
            }
            else if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_自主管理.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_自主管理);
            }
            /*修改生成资产代码规则、zhuyongkang 2019/7/30  start */
           /* else if(StringUtil.equals(dto.getCategory(),DictCodeList.计划类资产一级分类_期货资产管理计划.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_期货资产管理计划);
            }
            else if(StringUtil.equals(dto.getCategory(),DictCodeList.计划类资产一级分类_其他资产管理计划.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_其他资产管理计划);
            }
            else if(StringUtil.equals(dto.getCategory(),DictCodeList.计划类资产一级分类_信托产品.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_信托产品);
            }
            else if(StringUtil.equals(dto.getCategory(),DictCodeList.计划类资产一级分类_保险资产管理计划.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
            }
            else if(StringUtil.equals(dto.getCategory(),DictCodeList.计划类资产一级分类_券商资产管理计划.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
            }
            else if(StringUtil.equals(dto.getCategory(),DictCodeList.计划类资产一级分类_基金资产管理计划.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_基金资产管理计划);
            }*/
            /*、zhuyongkang 2019/7/30  end */
            // 修改业务编号生成方式
            String code = baseGetBussinessId.buildBussinessCode(parameter);
            dto.setFinprodMarketId(code);
        }
        //update by zhangnian  20191018 start
        //快速录入，dto.getFinprodId()不为空
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)
                && commonBusinessService !=null){
            dto.setFinprodId(commonBusinessService.getFinProdId(dto.getFinprodMarketId(),
                    dto.getFinprodType2(), null));
        }
        /*if(!StringUtil.isNotEmpty(dto.getFinprodId()) && commonBusinessService !=null){
            dto.setFinprodId(commonBusinessService.getFinProdId(dto.getFinprodMarketId(),
                    dto.getFinprodType2(), null));
        }*/
        //update by zhangnian  20191018  end
        ProcessParameter<FinPlanProfitAssetDto> processParameter = new ProcessParameter<FinPlanProfitAssetDto>();
        WorkFlowParameter<FinPlanProfitAssetDto> params = new WorkFlowParameter<FinPlanProfitAssetDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getFinprodMarketId());
        params.setBusinessType(DictCodeList.共通业务类型_计划类资产_利率型_信息.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> getMessage("FinPlanProfitAsset002.finprodMarketId") + "(" + entity.getFinprodMarketId() + ")"
                + getMessage("FinPlanProfitAsset002.finprodName") + "(" + dto.getFinprodName() + ")");
        processParameter.setWorkFlowParameter(params);
        /*dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());*/
        String msg = finPlanProfitAssetService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}

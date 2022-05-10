/*
 * 文件名：FinPlanNetAsset002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.plan.planNet.controller;

import java.util.List;
import java.util.Map;

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
import com.joyintech.tams.invest.asset.plan.planNet.dto.FinPlanNetAssetDto;
import com.joyintech.tams.invest.asset.plan.planNet.service.FinPlanNetAssetService;
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
 * 计划类资产（净值型）新建和修改controller
 * 
 * @author liuwei
 * @version 1.0 2018年9月7日
 * @see FinPlanNetAsset002Controller
 * @since 1.0
 */
@Controller
@PageId("FinPlanNetAsset002")
@RequestMapping("/invest/asset/plan/planNet/FinPlanNetAsset002")
public class FinPlanNetAsset002Controller extends BaseController{
    
    /**
     * 计划类资产（净值型）service
     */
    @Autowired
    private FinPlanNetAssetService finPlanNetAssetService;
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
     * 计划类资产（净值型）新建界面的初始化
     * @return 初始化结果
     * @see
     * @since 1.0
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model){
    	//新增行内五级分类 zhangpuyu 2019-7-25 start 
        this.setSelectOptionToModel(model,DictCode.计划类资产一级分类,DictCode.行内资产二级分类,DictCode.行内资产三级分类,DictCode.行内资产四级分类,
        		DictCode.行内资产五级分类,DictCode.资产_负债二级类别,
        // 新增行内五级分类 zhangpuyu 2019-7-25 end
            DictCode.计划类资产二级分类,DictCode.清算速度,DictCode.币种,DictCode.资金实际投向,
            DictCode.本金收益特征,DictCode.投资性质,DictCode.是否,DictCode.增信措施);
        model.addAttribute("FINPROD_TYPE_TG_F17",DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.addAttribute("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
        model.addAttribute("YES", DictCodeList.是否_是.getValue());
        String finprodId = getParam("finprodId");
        // 增加快捷录入功能 by 赵东东 2019.10.12 start 
        String finprodMarketId = getParam("finprodMarketId");
        if(StringUtil.isNotEmpty(finprodMarketId)&&StringUtil.isEmpty(finprodId)) {
        	finprodId = finPlanNetAssetService.getFinprodId(finprodMarketId);
        }
        // 增加快捷录入功能 by 赵东东 2019.10.12 end
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if(!StringUtil.isNotEmpty(finprodId) && WorkFlowUtil.isNotFromToDoList()){
            //新建界面初始化
            FinPlanNetAssetDto dto = new FinPlanNetAssetDto();
            //默认平台日期
            dto.setVdate(iPlatformDate!=null?iPlatformDate.getSysDate():DateUtil.now());
//            dto.setMdate(iPlatformDate!=null?iPlatformDate.getSysDate():DateUtil.now());
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            dto.setTransMode(DictCodeList.清算速度_T_0.getValue());
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            dto.setFjId("fj" + System.currentTimeMillis());
//            dto.setInvestmentAttributes(DictCodeList.投资性质_权益类.getValue());
            model.addAttribute("entity", dto);
        }
        else if(StringUtil.isNotEmpty(finprodId) && WorkFlowUtil.isNotFromToDoList()){
            FinPlanNetAssetDto finPlanAssetNInfo = finPlanNetAssetService.getBasicInfoByFinprodId(finprodId);
            if(null!=finPlanAssetNInfo){
                finPlanAssetNInfo.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
                finPlanAssetNInfo.setFjId(finPlanAssetNInfo.getFinprodId());
            }
            if (StringUtil.isNotEmpty(getParam("pageModel"))){
                finPlanAssetNInfo.setPageModel(getParam("pageModel"));     
            }
            model.addAttribute("entity",finPlanAssetNInfo);
            model.addAttribute("finprodId", finprodId);
        }
        else if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            // 根据工作流与留痕处理提供的共通方法获取业务实体
            FinPlanNetAssetDto mstCalendarTypeInfo = finPlanNetAssetService.getBussinessEntityByWorkflowId(
                    workflowId);
            mstCalendarTypeInfo.setFjId(workflowId);
            // 设置到model中
            model.addAttribute(GlobalConstant.WORKFLOW_ID,workflowId);
            model.addAttribute("entity", mstCalendarTypeInfo);
        }
        model.addAttribute("publicOffPlan", DictCodeList.计划类资产二级分类_公募计划.getValue());
      //取系统参数
        model.addAttribute("CE_MODEL", CacheUtil.getSysParam(BussinessConstant.CEMODEL));
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        return super.init(model);
    }

    /**
     * 出账编号的联想下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCmsId")
    public List<SelectOptionDto> getCmsId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = finPlanNetAssetService.getCmsId(map);
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
    public FinPlanNetAssetDto getChange(String cmsId){
        return finPlanNetAssetService.getChange(cmsId);
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
//        // 业务代码生成参数
//        BussinessCodeParameter parameter = new BussinessCodeParameter();
//        parameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_信息);
//        parameter.setType1(DictCodeList.from(DictCode.计划类资产二级分类,map.get("type").toString()));
//        // 修改业务编号生成方式
//        String code = baseGetBussinessId.buildBussinessCode(parameter);
//        return code;
//    }

    /**
     * 发行人的联想下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getIssuer")
    public Page<SelectOptionDto> getIssuer(){
        Map<String,Object>map = RequestUtil.getPageParam();
        Page<SelectOptionDto> temp = finPlanNetAssetService.getIssuer(map);
        return temp;
    }
    /**
     * 实际管理人的联想下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getActualManager")
    public Page<SelectOptionDto> getActualManager(){
        Map<String,Object> map = RequestUtil.getPageParam();
        Page<SelectOptionDto> temp = finPlanNetAssetService.getActualManager(map);
        return temp;
    }
    /**
     * 
     * 管理人的联想下拉框事件
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getManager")
    public Page<SelectOptionDto> getManager(){
        Map<String,Object>map = RequestUtil.getPageParam();
        Page<SelectOptionDto> temp = finPlanNetAssetService.getManager(map);
        return temp;
    }
    /**
     * 托管人的联想下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getTrusteeId")
    public List<SelectOptionDto> getTrusteeId(){
        Map<String,Object>map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = finPlanNetAssetService.getTrusteeId(map);
        return temp;
    }
    
    /**
     * 交易日历的联想下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCalendarId")
    public List<SelectOptionDto> getCalendarId(){
        Map<String,Object>map = RequestUtil.getPageParam();
        List<SelectOptionDto> temp = finPlanNetAssetService.getCalendarId(map);
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
            ValidateGroupEdit.class}) FinPlanNetAssetDto dto, BindingResult result) {
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
            ValidateGroupEdit.class}) FinPlanNetAssetDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getFinprodMarketId())) {
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_信息);
            if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_公募计划.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_公募计划);
            }
            if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_委托债权.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_委托债券);
            }
            if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_委托权益.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_委托权益);
            }
            if(StringUtil.equals(dto.getType(),DictCodeList.计划类资产二级分类_自主管理.getValue())){
                parameter.setType1(DictCodeList.业务编号一级分类_自主管理);
            }
            // 修改业务编号生成方式
            String code = baseGetBussinessId.buildBussinessCode(parameter);
            dto.setFinprodMarketId(code);
        }
        if(!StringUtil.isNotEmpty(dto.getFinprodId()) && commonBusinessService !=null){
            dto.setFinprodId(commonBusinessService.getFinProdId(dto.getFinprodMarketId(),
                    DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue(), null));
        }
        ProcessParameter<FinPlanNetAssetDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<FinPlanNetAssetDto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getFinprodMarketId());
        params.setBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_信息.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> getMessage("FinPlanNetAsset002.finprodMarketId") + "(" + entity.getFinprodMarketId() + ")" +
                getMessage("FinPlanNetAsset002.finprodName") + "(" + dto.getFinprodName() + ")");
        processParameter.setWorkFlowParameter(params);
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        String msg = finPlanNetAssetService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}

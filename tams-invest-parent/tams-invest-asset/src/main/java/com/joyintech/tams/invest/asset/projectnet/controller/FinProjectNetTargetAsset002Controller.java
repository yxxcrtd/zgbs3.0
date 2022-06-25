/*
 * 文件名：FinProjectTargetAsset002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.projectnet.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.asset.bond.dto.FinBondTargetAssetDto;
import com.joyintech.tams.invest.asset.project.dto.FinProjectTargetAssetDto;
import com.joyintech.tams.invest.asset.projectnet.dto.FinProjectNetTargetAssetDto;
import com.joyintech.tams.invest.asset.projectnet.service.FinProjectNetTargetAssetService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权益类资产
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月25日
 * @see FinProjectNetTargetAsset002Controller
 * @since 1.0
 */
@Controller
@PageId("FinProjectNetTargetAsset002")
@RequestMapping("/invest/asset/projectnet/FinProjectNetTargetAsset002")
public class FinProjectNetTargetAsset002Controller extends BaseController{

    @Autowired
    private FinProjectNetTargetAssetService finProjectNetTargetAssetService;
    
    @Autowired 
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired
    private CommonBusinessService commonBusinessService;
    
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;
    
    /**
     * 初始化数据字典下拉框
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.资产_负债二级类别,
            DictCode.项目类资产二级类型,DictCode.项目类一级分类, DictCode.资产来源, DictCode.投资性质,
        DictCode.资产来源条线, DictCode.币种, DictCode.本金收益特征, DictCode.证监会行业分类1, DictCode.证监会行业分类2, DictCode.机构所属行业一级分类,
        DictCode.机构所属行业二级分类, DictCode.收受益权类型, DictCode.重点监控行业和领域类别, DictCode.担保方式, DictCode.收益类型
        // 项目类资产五级分类改造 add by renping 2019-07-25 start
        ,DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类, DictCode.行内资产五级分类};
        // 项目类资产五级分类改造 add by renping 2019-07-25 end
    
    @Override
    public String init(Model model){
        //初始化基本信息画面数据字典
        setSelectOptionToModel(model, DICT_CODES);
        String finprodId = this.getParam("finprodId");
        //String status = getParam("status");
        String workflowId = getParam("workflowId");
        model.addAttribute("YES",DictCodeList.是否_是.getValue());
        model.addAttribute("isTradeCalendar",DictCodeList.是否_是.getValue());
        model.addAttribute("ASSET_SOURCE_01", DictCodeListExt1.资产来源_行内.getValue());
        model.addAttribute("ASSET_SOURCE_02", DictCodeListExt1.资产来源_行外.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_01", DictCodeList.项目类一级分类_非标债权.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_02", DictCodeList.项目类一级分类_结构化证券.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_03", DictCodeList.项目类一级分类_其他权益类投资.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_04", DictCodeList.项目类一级分类_其他产业投资基金.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_05", DictCodeList.项目类一级分类_股票定向增发投资.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_06", DictCodeList.项目类一级分类_基础设施基金.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_07", DictCodeList.项目类一级分类_券商债权收益权投资.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_08", DictCodeList.项目类一级分类_信贷资产流转项目.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_09", DictCodeList.项目类一级分类_券商收益凭证.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_10", DictCodeList.项目类一级分类_股票质押式回购.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_11", DictCodeList.项目类一级分类_理财直接融资工具.getValue());
        model.addAttribute("ASSET_TYPE_SECOND_LEVEL_1201", DictCodeListExt2.资产_负债二级类别_票据类.getValue());
        model.addAttribute("ASSET_TYPE_SECOND_LEVEL_1210", DictCodeListExt2.资产_负债二级类别_信用证.getValue());
        model.addAttribute("ASSET_TYPE_SECOND_LEVEL_1299", DictCodeListExt2.资产_负债二级类别_其他非标准化债权类投资.getValue());
        model.addAttribute("ASSET_SOURCE_01", DictCodeListExt1.资产来源_行内.getValue());
        model.addAttribute("ASSET_SOURCE_02", DictCodeListExt1.资产来源_行外.getValue());
        model.addAttribute("INVEST_01", DictCodeList.投资性质_固定收益类.getValue());
        model.addAttribute("INVEST_01", DictCodeList.投资性质_固定收益类.getValue());
        model.addAttribute("INVEST_02", DictCodeList.投资性质_权益类.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F19", DictCodeListExt3.平安资产类型_权益类资产.getValue());
        model.addAttribute("PROFIT_TYPE_01", DictCodeList.收益类型_净值型.getValue());
        model.addAttribute("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
        String finprodMarketId=getParam("finprodMarketId");
        boolean isQuickInput = Boolean.parseBoolean(getParam("isQuickInput"));
        if (isQuickInput&&StringUtil.isNotEmpty(finprodMarketId)){
            Map<String,Object> quickInputMap = new HashMap<String,Object>();
            quickInputMap.put("finprodMarketId",finprodMarketId);
            FinProjectTargetAssetDto dto = finProjectNetTargetAssetService.getBasicInfo(quickInputMap);
            if(null!=dto){
                finprodId = dto.getFinprodId();
            }
        }
        // 项目类资产五级分类改造 add by renping 2019-07-25 end
        //不来自于代办
        if(WorkFlowUtil.isNotFromToDoList()&& StringUtil.isNotEmpty(finprodId)) {
            if(!isQuickInput){
                model.addAttribute("isTransInAsset",commonBusinessService.isTransInAsset(finprodId)!=0?true:false);
            }
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("finprodId", finprodId);
            //根据主键获取业务实体--标的金融产品基本信息
            FinProjectTargetAssetDto finProjectTargetAssetDto = finProjectNetTargetAssetService.getBasicInfo(
                paramMap);
            //快录原标的可能有占用额度
            if(isQuickInput){
                finProjectTargetAssetDto.setHapCreditAmt(BigDecimal.ZERO);
                // 快录时，不默认带出附件信息
                finProjectTargetAssetDto.setFinprodId("");
            }
            model.addAttribute("entity", finProjectTargetAssetDto);
        }//来自于代办
        else if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            //根据工作流与留痕处理提供的共通方法获取业务实体
            FinProjectNetTargetAssetDto finProjectTargetAssetDto = finProjectNetTargetAssetService.getBussinessEntityByWorkflowId(workflowId);
            //设置到model中
            model.addAttribute("entity", finProjectTargetAssetDto);
        }else{
            FinBondTargetAssetDto finBondTargetAssetDto = new FinBondTargetAssetDto();
            finBondTargetAssetDto.setFinprodId(IDGenerator.generate());
        }
        //资产类型
        model.addAttribute("INT_ASSET_TYPE", DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
        model.addAttribute("workflowId",workflowId);
        model.addAttribute("pageModel",this.getPageModel());
        model.addAttribute("pageId",this.getPageId());
      //取系统参数
        model.addAttribute("CE_MODEL", CacheUtil.getSysParam(BussinessConstant.CEMODEL));
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        model.addAttribute("pflshType", DictCodeList.是否_是.getValue());
        model.addAttribute("isQuickInput",isQuickInput);
        //add by zhangnian  20191008 end
        //add by lizhi 是否显示中债信息开关 2020年2月28日  start
        model.addAttribute("ASSET_INFO_CHB_HIDE",
                CacheUtil.getSysParam(BussinessConstant.ASSET_INFO_CHB_HIDE)==null?
                        DictCodeListExt1.是或否_是.getValue(): CacheUtil.getSysParam(BussinessConstant.ASSET_INFO_CHB_HIDE));
        //add by lizhi 是否显示中债信息开关 2020年2月28日  end
        model.addAttribute("CUSTOM_ASSET_TYPE_TWO_P03", "P03");
        // 快录时，不默认带出附件信息
        if (isQuickInput){
            model.addAttribute("businessId", "");
        }
        return super.init(model);
    }
    
    /**
     * check--保存
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) FinProjectNetTargetAssetDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 融资人、担保人、投资顾问（下拉框）
     * @return
     */
    @ResponseBody
    @RequestMapping("/getFinancier")
    public Page<SelectOptionDto> getFinancier(){
        Map<String,Object>map = RequestUtil.getPageParam();
        Page<SelectOptionDto> temp = finProjectNetTargetAssetService.getFinancier(map);
        return temp;
    }

    @ResponseBody
    @RequestMapping("/getGuarantee")
    public Page<SelectOptionDto> getGuarantee(){
        Map<String,Object>map = RequestUtil.getPageParam();
        return finProjectNetTargetAssetService.getGuarantee(map);
    }
    /**
     * 
     * 保存
     * @param dto
     * @param br
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(PAGE_SAVE)
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) FinProjectNetTargetAssetDto dto, BindingResult br)
        throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
      //生成金融产品代码
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            dto = finProjectNetTargetAssetService.getFinprodId(dto);
        }
        //文件信息
        if (CollectionUtils.isNotEmpty(dto.getFileArray())
            && StringUtil.isNotEmpty(dto.getFinprodId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getFinprodId());
        }
        dto.setFinprodType2(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
        WorkFlowParameter<FinProjectNetTargetAssetDto> params = new WorkFlowParameter<FinProjectNetTargetAssetDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getFinprodMarketId());
        params.setBusinessType(DictCodeListExt3.共通业务类型_权益类资产信息.getValue());
        // 设置流程业务说明
        params.setComment(entity ->
        WorkFlowUtil.concatComment(this.getMessage("FinProjectTargetAsset002.finprodId")+GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodMarketId()+GlobalConstant.CLOSE_PARENTHESIS,
            this.getMessage("FinProjectTargetAsset002.finprodName")+GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodName()+GlobalConstant.CLOSE_PARENTHESIS,
            this.getMessage("FinProjectTargetAsset002.vdate")+GlobalConstant.OPEN_PARENTHESIS+DateUtil.format(entity.getVdate())+GlobalConstant.CLOSE_PARENTHESIS));
        // 给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<FinProjectNetTargetAssetDto> parameter = new ProcessParameter<FinProjectNetTargetAssetDto>();
        parameter.setWorkFlowParameter(params);
        // 系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        String msg = finProjectNetTargetAssetService.start(parameter, dto);
        return new AjaxResult(msg);
    }
    
}

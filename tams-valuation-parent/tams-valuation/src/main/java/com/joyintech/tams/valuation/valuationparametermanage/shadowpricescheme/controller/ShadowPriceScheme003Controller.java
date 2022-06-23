/*
 * 文件名：ShadowPriceScheme003Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月24日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme001Dto;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme002Dto;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.service.ShadowPriceScheme001Service;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.service.ShadowPriceScheme002Service;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 影子方案右侧明细界面新建 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author lishicheng
 * @version 1.0 2018年11月24日
 * @see ShadowPriceScheme003Controller
 * @since 1.0
 */
@Controller
@PageId("ShadowPriceScheme003")
@RequestMapping("/valuation/valuationparametermanage/shadowpricescheme/ShadowPriceScheme003")
public class ShadowPriceScheme003Controller extends BaseController {

    @Autowired
    private ShadowPriceScheme001Service ShadowPriceScheme001Service;

    @Autowired
    private ShadowPriceScheme002Service ShadowPriceScheme002Service;

    @Override
    public String init(Model model) {

        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);
        String schemeDetailId = getParam("schemeDetailId");
        // String schemeId = getParam("schemeId");
        String schName = getParam("schName");
        String operateType = getParam("operateType");
        ShadowPriceScheme002Dto dto = new ShadowPriceScheme002Dto();

        // 修改
        if (StringUtil.isNotEmpty(schemeDetailId) && "edit".equals(operateType)) {
            dto = ShadowPriceScheme002Service.selectSchemeDetailBySchId(schemeDetailId);
            // 日期格式 化
            dto.setVdate(DateUtil.format(DateUtil.parse(dto.getVdate()), "yyyy-MM-dd"));
            dto.setSchemeId(schName);
            model.addAttribute("entity", dto);
        }
        // 复制
        if (StringUtils.isNotEmpty(schemeDetailId) && "copy".equals(operateType)) {
            dto = ShadowPriceScheme002Service.selectSchemeDetailBySchId(schemeDetailId);
            // 日期格式 化
            dto.setVdate(DateUtil.format(DateUtil.parse(dto.getVdate()), "yyyy-MM-dd"));
            if ((StringUtils.isNotEmpty(dto.getSchemeDetailId()))) {
                dto.setSchemeDetailId(IDGenerator.generate());// 复制 自动生成 主键
            }
            dto.setSchemeId(schName);
            model.addAttribute("entity", dto);
        }
        // 新建
        if (StringUtils.isEmpty(schemeDetailId) && "add".equals(operateType)) {
            dto.setSchemeDetailId(IDGenerator.generate());// 新建 自动生成 主键
            dto.setSchemeId(schName);
            model.addAttribute("entity", dto);
        }
        else if (StringUtil.isNotEmpty(workflowId)) {
            ShadowPriceScheme002Dto entity = ShadowPriceScheme002Service.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);
        } // 双击查看
        if (StringUtil.isNotEmpty(schemeDetailId) && "view".equals(operateType)) {
            dto = ShadowPriceScheme002Service.selectSchemeDetailBySchId(schemeDetailId);
            // 日期格式 化
            dto.setVdate(DateUtil.format(DateUtil.parse(dto.getVdate()), "yyyy-MM-dd"));
            dto.setSchemeId(schName);
            model.addAttribute("entity", dto);
        }

        dto.setOperateType(operateType);

        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.证券品种, null);
        // 证券品种移除基金
        List<SelectOptionDto> options = selectDto.getOptions();
        //ZGBS-151 证券品种只支持债券
        List<SelectOptionDto> options1 = new ArrayList<SelectOptionDto>();
        for (SelectOptionDto item : options) {
            if (DictCodeListExt1.证券品种_债券.getValue().equals(item.getId())) {
                options1.add(item);
                break;
            }
        }
        model.addAttribute("SEC_TYPE", JsonUtils.objectArray2Json(options1.toArray()));
        super.setSelectOptionToModel(model, DictCode.估值交易市场);
        super.setSelectOptionToModel(model, DictCode.估值市价);
        // 估值市价移除管理人报价
        selectDto = this.getSelectDataFromDataDic(DictCode.估值市价, null);
        options = selectDto.getOptions();
        for (SelectOptionDto item : options) {
            if (DictCodeList.估值市价_管理人报价.getValue().equals(item.getId())) {
                options.remove(item);
                break;
            }
        }
        model.addAttribute("VALUATION_PRICE", JsonUtils.objectArray2Json(options.toArray()));
        super.setSelectOptionToModel(model, DictCode.估值日期);
        super.setSelectOptionToModel(model, DictCode.债券类型);
        super.setSelectOptionToModel(model, DictCode.基金类型);
		super.setSelectOptionToModel(model, DictCode.估值舍入规则);
		super.setSelectOptionToModel(model, DictCode.价格类别);
        // model.addAttribute("FINPRODID_LIST",JsonUtils.objectArray2Json(this.findScheme().toArray()));
        return super.init(model);
    }

    /**
     * 下拉框 ： 方案
     * 
     * @return
     * @throws Exception
     */
    public List<SelectOptionDto> findScheme() {
        return ShadowPriceScheme001Service.getSchemeForSelect(RequestUtil.getPageParam());
    }

    /**
     * 新增/修改验证：基础验证、唯一性
     * 
     * @param dto
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Valid ShadowPriceScheme002Dto dto, BindingResult bindingResult)
         {
        String msg = StringUtil.EMPTY;
        String operateType = dto.getOperateType();
        // 基础验证通过
        if (!bindingResult.hasErrors()) {
            // 验证估值第一市价与第二市价不能相等
            if(StringUtil.equals(dto.getValuationPrice(), dto.getValuationPrice2())){
                return new AjaxResult("第一市价和第二市价不能为同一个", MessageType.错误消息);
            }
            // 价格类型的非空判定
            if(DictCodeListExt1.证券品种_债券.getValue().equals(dto.getSecType())){
			    if(DictCodeList.估值市价_收盘价.getValue().equals(dto.getValuationPrice())
			    || DictCodeList.估值市价_平均价.getValue().equals(dto.getValuationPrice())
			    || DictCodeList.估值市价_最高价.getValue().equals(dto.getValuationPrice())
			    || DictCodeList.估值市价_最低价.getValue().equals(dto.getValuationPrice())
			    || DictCodeList.估值市价_卖一价.getValue().equals(dto.getValuationPrice())
			    || DictCodeList.估值市价_买一价.getValue().equals(dto.getValuationPrice())
			    || DictCodeList.估值市价_收盘价.getValue().equals(dto.getValuationPrice2())
			    || DictCodeList.估值市价_平均价.getValue().equals(dto.getValuationPrice2())
			    || DictCodeList.估值市价_最高价.getValue().equals(dto.getValuationPrice2())
			    || DictCodeList.估值市价_最低价.getValue().equals(dto.getValuationPrice2())
			    || DictCodeList.估值市价_卖一价.getValue().equals(dto.getValuationPrice2())
			    || DictCodeList.估值市价_买一价.getValue().equals(dto.getValuationPrice2())){
                    if(StringUtils.isEmpty(dto.getPriceClass())) {
                        return new AjaxResult("价格类型不能为空！", MessageType.错误消息);
                    }
			    }
			}
            // 唯一性验证：方案代码 + 证券品种+交易场所+证券细分类+开始日组成唯一性数据。保存时进行检查，若已存在对应数据，
            Map<String, Object> params = Maps.newHashMap();
            ShadowPriceScheme001Dto sps = ShadowPriceScheme002Service.getschemeIdByname(
                dto.getSchemeId());
            params.put("schemeId", sps.getSchId());
            params.put("secType", dto.getSecType());
            params.put("tradeMarket", dto.getTradeMarket());
            params.put("secDetailType", dto.getSecDetailType());
            params.put("vdate", dto.getVdate());
            List<ShadowPriceScheme002Dto> list = ShadowPriceScheme002Service.checkShadowPriceDetial(
                params);
            if (CollectionUtils.isNotEmpty(list)) {
                if(OPERATE_TYPE_SUBMIT.equals(operateType)){
                    msg = "影子定价明细表中已存在" + "["
                            + SysDataUtil.getNameFromDataDict(DictCode.证券品种, dto.getSecType()) + "]_["
                            + SysDataUtil.getNameFromDataDict(DictCode.估值交易市场, dto.getTradeMarket())
                            + "]_[";

                    if (StringUtil.isNotEmpty(dto.getSecDetailType())) {
                        msg += SysDataUtil.getNameFromDataDict(DictCode.债券类型, dto.getSecDetailType())
                                + "]_[";
                    }
                    msg += dto.getVdate() + "]的数据，请确保数据唯一性!";
                    return new AjaxResult(msg, MessageType.错误消息);
                }else if(OPERATE_TYPE_SAVE.equals(operateType)){
                    for (ShadowPriceScheme002Dto dtos : list){
                        if(StringUtil.isNotEmpty(dto.getSchemeDetailId()) && !StringUtil.equals(dtos.getSchemeDetailId(),dto.getSchemeDetailId())){
                            msg = "影子定价明细表中已存在" + "["
                                    + SysDataUtil.getNameFromDataDict(DictCode.证券品种, dto.getSecType()) + "]_["
                                    + SysDataUtil.getNameFromDataDict(DictCode.估值交易市场, dto.getTradeMarket())
                                    + "]_[";

                            if (StringUtil.isNotEmpty(dto.getSecDetailType())) {
                                msg += SysDataUtil.getNameFromDataDict(DictCode.债券类型, dto.getSecDetailType())
                                        + "]_[";
                            }
                            msg += dto.getVdate() + "]的数据，请确保数据唯一性!";
                            return new AjaxResult(msg, MessageType.错误消息);
                        }
                    }
                }
            }
            // 修改验证
            if (OPERATE_TYPE_SAVE.equals(dto.getOperateType()) && !bindingResult.hasErrors()) {
                // 判断当前流程是否存在
                if (ShadowPriceScheme002Service.checkBusinessDataInFlow(dto)) {
                    // 流程存在情况
                    return new AjaxResult("该条数据在流程中，不能修改", MessageType.错误消息);
                }
            }
        }

        return commonCheck(bindingResult, operateType);
    }

    /**
     * 新建或修改
     * 
     * @param dto
     * @param bindingResult
     * @return
     */
    /*
     * @ResponseBody
     * @RequestMapping(value = PAGE_SAVE, method = RequestMethod.POST) public AjaxResult
     * save(@RequestBody @Valid ShadowPriceScheme002Dto dto, BindingResult bindingResult) { String
     * msg = StringUtil.EMPTY; if (StringUtils.isEmpty(dto.getSchemeDetailId())) {
     * dto.setSchemeDetailId(IDGenerator.generate()); } String operateType = dto.getOperateType();
     * if ("submit".equals(operateType)) { ShadowPriceScheme002Service.insert(dto); msg = "新建成功！";
     * } else if ("save".equals(operateType)) {
     * ShadowPriceScheme002Service.updateByPrimaryKeySelective(dto); msg = "修改成功！"; } return new
     * AjaxResult(msg); }
     */
    /**
     * 影子方案明细新建走流程 <一句话功能简述> <功能详细描述>
     * 
     * @param dto
     * @param br
     * @return
     */
    @ResponseBody
    @RequestMapping(value = PAGE_SAVE, method = RequestMethod.POST)
    public AjaxResult doSave(@RequestBody @Validated({ValidateGroupEdit.class}) ShadowPriceScheme002Dto dto, BindingResult br) {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 通过获取的影子方案name获取id
        ShadowPriceScheme001Dto sps = ShadowPriceScheme002Service.getschemeIdByname(
            dto.getSchemeId());
        dto.setSchemeId(sps.getSchId());
        // 启动流程
        ProcessParameter<ShadowPriceScheme002Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<ShadowPriceScheme002Dto> params = new WorkFlowParameter<>();

        // 业务代码
        params.setBusinessCode(dto.getSchemeDetailId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_影子定价方案.getValue());
        // 页面id
        params.setPageId(this.getPageId());
        // 元素id
        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));

        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> WorkFlowUtil.concatComment("影子定价方案明细新建：", entity.getSchemeDetailId(), // 方案明细代码
                entity.getCreateUser()// 创建人
            ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = ShadowPriceScheme002Service.start(proParam, dto);
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }

    /**
     * 明细修改走流程 <一句话功能简述> <功能详细描述>
     * 
     * @param dto
     * @param br
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "doUpdate", method = RequestMethod.POST)
    public AjaxResult doUpdate(@RequestBody @Validated({ValidateGroupEdit.class}) ShadowPriceScheme002Dto dto, BindingResult br) {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 通过获取的影子方案name获取id
        ShadowPriceScheme001Dto sps = ShadowPriceScheme002Service.getschemeIdByname(
            dto.getSchemeId());
        dto.setSchemeId(sps.getSchId());
        // 启动流程
        ProcessParameter<ShadowPriceScheme002Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<ShadowPriceScheme002Dto> params = new WorkFlowParameter<>();

        // 业务代码
        params.setBusinessCode(dto.getSchemeId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_影子定价方案.getValue());
        // 页面id
        params.setPageId(this.getPageId());
        // 元素id
        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("影子定价明细方案修改：", entity.getSchemeId(), // 方案代码
            entity.getCreateUser()// 创建人
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = ShadowPriceScheme002Service.start(proParam, dto);
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }
}

/*
 * 文件名：ReportPageManage002Controller.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：jiangsucheng
 * 修改时间：2018年11月6日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */

package com.joyintech.tams.svreport.chb.assetinfodetail.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.svreport.chb.assetinfodetail.dto.AssetInfoDetailDto;
import com.joyintech.tams.svreport.chb.assetinfodetail.service.AssetInfoDetailMService;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author jiangsucheng
 * @version 1.0 2018年11月6日
 * @see AssetInfoDetail002Controller
 * @since 1.0BussFinBondRepInfo
 */
@PageId("AssetInfoDetail002")
@Controller
@RequestMapping("/chb/assetinfodetail/AssetInfoDetail002")
public class AssetInfoDetail002Controller extends BaseController {

    /**
     * 资产要素service
     */
    @Autowired
    private AssetInfoDetailMService assetInfoDetailMService;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public String init(Model model){
        super.setSelectOptionToModel(model, DictCode.资产_负债类别, DictCode.资产_负债二级类别, DictCode.报送反馈状态,
                DictCode.交易流通场所_中债,DictCode.币种_中债,
                DictCode.中债_上游资产一级分类, DictCode.中债_上游资产二级分类,DictCode.中债_上游资产三级分类,DictCode.中债_上游资产四级分类,
                DictCode.中债_上游资产五级分类);

        String assetUuid = this.getParam("assetUuid");
        String dataStatus = this.getParam("dataStatus");
//        String issuepartyid= CommonUtil.selectBankCode();
        String issuepartyid= CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String jsonObject= JSONObject.toJSONString(assetInfoDetailMService.getDataListLink());
        model.addAttribute("AssetTypeListLink", jsonObject);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {// 判断是否存在于工作流中
            AssetInfoDetailDto dto = assetInfoDetailMService.getBussinessEntityByWorkflowId(
                    workflowId);
            model.addAttribute("pageModel", dto.getPageModel());
            model.addAttribute(GlobalConstant.ENTITY, dto);
        }else {
            AssetInfoDetailDto dto = new AssetInfoDetailDto();
            dto.setIssuePartyId(issuepartyid);
            model.addAttribute(GlobalConstant.ENTITY, dto);
            //资产编号不为空则为修改.
            if (StringUtil.isNotEmpty(assetUuid) && StringUtil.isNotEmpty(dataStatus)) {
                dto = assetInfoDetailMService.getAssetInfoDetail(assetUuid, dataStatus);
                model.addAttribute(GlobalConstant.ENTITY, dto);
            }
        }
        return super.init(model);
    }

    @Override
    public Object after(String draftobj, Model model) {
        return JsonUtils.json2Object(draftobj,AssetInfoDetailDto.class);
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
            ValidateGroupEdit.class}) AssetInfoDetailDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 保存
     *
     * @param dto 实体类
     * @param br 结果
     * @return 结果集
     * @throws WorkFlowException 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) AssetInfoDetailDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<AssetInfoDetailDto> processParameter = new ProcessParameter<AssetInfoDetailDto>();
        WorkFlowParameter<AssetInfoDetailDto> params = new WorkFlowParameter<AssetInfoDetailDto>();
        params.setPageId("AssetInfoDetail002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getAssetCode());
        params.setBusinessType(DictCodeListExt3.共通业务类型_资产基本信息维护.getValue());
        processParameter.setWorkFlowParameter(params);
        String msg = assetInfoDetailMService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    
    @ResponseBody
    @RequestMapping("getValue")
    public BigDecimal getValue(@RequestBody Map<String, Object> param) {
        return assetInfoDetailMService.getTargetAmt(param);
    }

    /**
     * 根据界面选择获取二级资产类型
     * @return
     */
    @ResponseBody
    @RequestMapping("getAssetType2")
    public List<SelectOptionDto> getAssetType2(){
        List<SelectOptionDto> list = new ArrayList<>();
        Map<String, Object> params = RequestUtil.getPageParam();
        //如果一级资产未选择.则直接使用系统的资产负债类别
        if (StringUtil.isEmpty((String) params.get("orgAssetType1")) || "null".equals(params.get("orgAssetType1"))) {
//            params.put("orgAssetType1", null);
        }else{
            if (StringUtil.isEmpty((String) params.get("orgAssetType2")) || "null".equals(params.get("orgAssetType2"))) {
                params.put("orgAssetType2", null);
            }
            else if (StringUtil.isEmpty((String) params.get("orgAssetType3")) || "null".equals(params.get("orgAssetType3"))) {
                params.put("orgAssetType3", null);
            }else if (StringUtil.isEmpty((String) params.get("orgAssetType4")) || "null".equals(params.get("orgAssetType4"))) {
                params.put("orgAssetType4", null);
            }else if (StringUtil.isEmpty((String) params.get("orgAssetType5")) || "null".equals(params.get("orgAssetType5"))) {
                params.put("orgAssetType5", null);
            }
            list = assetInfoDetailMService.getAssetTye2(params);
        }
        if(StringUtil.isEmptyList(list)){
            SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.资产_负债二级类别, null);
            for (SelectOptionDto dto : selectDto1.getOptions()) {
                // add by zhangliang 20210204 增加前台输入过滤 start
                if (StringUtil.isNotEmpty(params.get("search"))){
                    if (StringUtil.indexOf(dto.getId(), params.get("search").toString()) > -1
                        || StringUtil.indexOf(dto.getText(), params.get("search").toString()) > -1){
                        list.add(dto);
                    }
                }else{
                    list.add(dto);
                }
//                list.add(dto);
                // add by zhangliang 20210204 增加前台输入过滤 end
            }
        }
        return list;
    }


    /**
     * 根据界面选择获取二级资产类型
     * @return
     */
    @ResponseBody
    @RequestMapping("selectCounterId")
    public List<SelectOptionDto> selectCounterId(){
        List<SelectOptionDto> list = new ArrayList<>();
        Map<String, Object> params = RequestUtil.getPageParam();
        list = assetInfoDetailMService.selectCounterId(params);
        return list;
    }
}

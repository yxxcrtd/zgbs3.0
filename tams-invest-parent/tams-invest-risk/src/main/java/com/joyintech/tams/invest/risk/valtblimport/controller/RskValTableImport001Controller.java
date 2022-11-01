/*
 * 文件名：RskValTableImport001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年12月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.risk.valtblimport.controller;

import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalrelateimport.service.RskValRelateImport001Service;
import com.joyintech.tams.invest.risk.valtblimport.service.RskValTableImportCommonService;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalrelateimport.dto.RskValRelateImport001Dto;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalrelateimport.service.RskValRelateImport002Service;
import com.joyintech.tams.invest.risk.valtblimport.dto.RskValTableImportParamDto;
import com.joyintech.tams.invest.risk.valtblimport.service.RskValTableImportService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年12月27日
 * @see RskValTableImport001Controller
 * @since 1.0
 */
@Controller
@PageId("RskValTableImport001")
@RequestMapping("/invest/risk/valtblimport/RskValTableImport001")
public class RskValTableImport001Controller extends BaseController{

    @Autowired
    private RskValRelateImport002Service rskValRelateImport002Service;
    @Autowired
    private RskValRelateImport001Service rskValRelateImport001Service;
    
    @Autowired
    private RskValTableImportService rskValTableImportService;
    @Autowired
    private ProgressBarService progressBarService;
    @Autowired
    private RskValTableImportCommonService rskValTableImportCommonService;
    @Override
    public String init(Model model) {
        String chbValSplitStr = CacheUtil.getSysParam(BussinessConstant.CHB_VAL_SPLIT_STR);
        model.addAttribute("chbValSplitStr",chbValSplitStr);
        return super.init(model);
    }
    
    /**
     * 获取组合关联映射信息
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("selectRskValRelateImportById")
    public RskValRelateImport001Dto selectRskValRelateImportById(@RequestBody RskValRelateImport001Dto dto){
        if(StringUtil.isNotEmpty(dto.getPtlId())){
            dto =  rskValRelateImport002Service.selectInfoById(dto.getPtlId());
        }
        return dto;
    }
    
    /**
     * 点击预览时check
     * <功能详细描述>
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) RskValTableImportParamDto dto, BindingResult result) {
        AjaxResult res= new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //检查上传的估值表是否符合要求
        String  valTableFileName = dto.getValTableFileName();
        // 解析上传文件名称，获取资产名称和估值日期
        String chbValSplitStr = CacheUtil.getSysParam(BussinessConstant.CHB_VAL_SPLIT_STR);
        String[] arr = valTableFileName.split(chbValSplitStr);
        Boolean isError = false;
        if (arr.length != 2) {
            ValidMessage validMessage = new ValidMessage("上传的估值表文件名称不符合格式要求");
            errorList.add(validMessage);
            isError=true;
        } else {
            Date valDate = rskValTableImportCommonService.getDateFromStr(arr[1]);
            if (Objects.isNull(valDate)) {
                ValidMessage validMessage = new ValidMessage("上传的估值表文件名称不符合格式要求");
                errorList.add(validMessage);
                isError=true;
            }else{
                SelectOptionDto info = rskValTableImportCommonService.getAssetInfoFromName(arr[0]);
                if (Objects.isNull(info)) {
                    ValidMessage validMessage = new ValidMessage("上传的估值表文件资产名称不存在");
                    errorList.add(validMessage);
                    isError=true;
                }
            }
        }
        //新增通过系统参数控制可导入的估值表最小日期   系统参数：MAX_SUB_VALDATE
        String days = StringUtil.defaultString(CacheUtil.getSysParam("MAX_SUB_VALDATE"),"16");
        if(DateUtil.compareDate(dto.getValDate(),DateUtil.parse(DateUtil.addDay(DateUtil.getSysDate(),-1*Integer.valueOf(days))))<0){
            ValidMessage validMessage = new ValidMessage("超出可导入的估值表日期:" + DateUtil.addDay(DateUtil.getSysDate(),-1*Integer.valueOf(days)));
            errorList.add(validMessage);
        }
        if(isError){
            res.setErrorList(errorList);
            res.setMessageType(MessageType.错误消息);
            return res;
        }
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 点击预览时check
     * <功能详细描述>
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkZip")
    public AjaxResult checkZip(@RequestBody @Validated({ValidateGroupEdit.class}) RskValTableImportParamDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 检查导入文件名称
     *
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkFileName")
    public AjaxResult checkFileName(@RequestBody RskValTableImportParamDto dto, BindingResult result) {
        AjaxResult ajaxResult = new AjaxResult();
        SelectOptionDto info = null;

        String chbValSplitStr = CacheUtil.getSysParam(BussinessConstant.CHB_VAL_SPLIT_STR);
        String[] arr = dto.getValTableFileName().split(chbValSplitStr);
        if (arr.length == 2) {
            info = rskValTableImportCommonService.getAssetInfoFromName(arr[0]);
        }
        ajaxResult.setData(info);
        return ajaxResult;
    }

    @ResponseBody
    @RequestMapping("uploadFile")
    public AjaxResult uploadFile() {
        // 更新文件上传进度为100%
        progressBarService.removePercent(this.getPageId(), ProgressBarService.FILE_UPLOAD_ID);
        return new AjaxResult();
    }

    @RequestMapping(value = "/batchUploadFile")
    @ResponseBody
    public List<ExcelValidDto> batchUploadFile(HttpServletRequest request, HttpServletResponse response, ModelMap model){
        String pageId = StringUtil.trim(request.getParameter(GlobalConstant.PAGE_ID)) == null ? "RskValTableImport001" : StringUtil.trim(request.getParameter(GlobalConstant.PAGE_ID));
        String filePath = request.getParameter("fileName");
        progressBarService.updatePercent(pageId, ProgressBarService.FILE_UPLOAD_ID, 0.5D, false);
        List<ExcelValidDto> excelValidDtos = rskValRelateImport001Service.batchUploadFile(pageId, filePath);
        progressBarService.removePercent(pageId, ProgressBarService.FILE_UPLOAD_ID);
        return excelValidDtos;
    }
    /**
     * 组合代码下拉框(分页)
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("getGroupIdInfo")
    @ResponseBody
    public Page<SelectOptionDto> getGroupIdInfo()
             {
        return rskValTableImportService.getGroupIdInfo(RequestUtil.getPageParam());
    }
}

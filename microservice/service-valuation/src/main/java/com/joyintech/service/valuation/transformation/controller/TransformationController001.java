/*
 * 文件名：TransformationController001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年11月8日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.transformation.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joyintech.framework.file.SafelyFile;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.common.interf.base.dto.BalanceTransplantDto;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.system.excelimp.dto.ExcelImpFileInfoDto;
import com.joyintech.system.excelimp.dto.ExcelImpFileSheetInfoDto;
import com.joyintech.system.excelimp.service.ExcelImpHandle001Service;
import com.joyintech.service.valuation.datatransplant.dto.TemplateCheckDto;
import com.joyintech.service.valuation.transformation.constants.TransConstant;
import com.joyintech.service.valuation.transformation.dto.ResultData;
import com.joyintech.service.valuation.transformation.service.AssetBalanceTransplantService;
import com.joyintech.service.valuation.transformation.service.ProdBalanceTransplantService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.service.DataTransplantCommonService;

/**
 * 数据移植模板转换
 * 
 * @version 1.0 2018年11月8日
 * @see TransformationController001
 * @since 1.0
 */
@RestController(value="serviceTransformationController001")
@RequestMapping("/Transformation001")
public class TransformationController001 extends BaseController {

    /**
     * ExcelImpHandle001Service
     */
    @Autowired
    private ExcelImpHandle001Service excelImpHandle001Service;

    @Autowired
    private ProdBalanceTransplantService prodBalanceTransplantService;
    
    @Autowired
    private AssetBalanceTransplantService assetBalanceTransplantService;
    
    @Autowired
	private DataTransplantCommonService dataTransplantCommonService;

    /**
     * ProgressBarService
     */
    @Autowired(required = false)
    private ProgressBarService progressBarService;
    
    /**
     * excel2003格式
     */
    public static final String EXCEL_XLS = "xls";

    /**
     * excel2007以上格式
     */
    public static final String EXCEL_XLSX = "xlsx";
    
    /**
     * excel2007以上格式
     */
    public static final String EXCEL_XLSM = "xlsm";


    /**
     * 文件上传
     *
     * @param request 请求
     * @param response 响应
     * @param model ModelAndView
     * @return 保存文件信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("/uploadFile")
    public AjaxResult uploadFile(@RequestBody Map<String, Object> params) throws FileNotFoundException {
    	HttpServletRequest request = (HttpServletRequest) params.get("HttpServletRequest");
    	HttpServletResponse response = (HttpServletResponse) params.get("HttpServletResponse");
    	ModelMap model = (ModelMap) params.get("ModelMap");
    	
    	ResultData result  = new ResultData();
    	AjaxResult ajaxResult = null;
    	
        // 获取默模版类型
        String modelType = request.getParameter("modelType");
        
        // 模版类型校验（模版类型不能为空）
        if (modelType == null || modelType.trim().length() == 0) {
        	ajaxResult = new AjaxResult("请选择模版类型", MessageType.警告消息);
        	result.setRstCode(TransConstant.文件上传参数CHECK_未通过);
        	ajaxResult.setData(result);
            return ajaxResult;
        }
        
        
        // 获取文件名
        String fileName1 = request.getParameter("fileName")==null?"":request.getParameter("fileName");
        File tempFile1 =new SafelyFile(fileName1);
        String fName1 = tempFile1.getName();
        // 获取后缀名
        String suffix = fName1.substring(fName1.lastIndexOf(".")+1);
        // 判断文件是不是一个有效的Excel文件
        if(!suffix.toLowerCase().equals(EXCEL_XLS)&&!suffix.toLowerCase().equals(EXCEL_XLSX)
        		&&!suffix.toLowerCase().equals(EXCEL_XLSM)){
        	ajaxResult = new AjaxResult("上传模板不是一个有效的Excel文件", MessageType.警告消息);
        	result.setRstCode(TransConstant.文件上传参数CHECK_未通过);
        	ajaxResult.setData(result);
        	return ajaxResult;
        }
        // 读取excel文件
        ExcelImpFileInfoDto dto = excelImpHandle001Service.readFile(request);

        // 文件名
        String fileName = request.getParameter("fileName");
        File tempFile =new SafelyFile(fileName);
        String fName=tempFile.getName();
        //校验文件名是否和所选模板一致
        boolean flag=checkFileName(modelType,fName);
        //如果不一致
        if(!flag){
        	ajaxResult = new AjaxResult("模板类型要与模板名保持一致", MessageType.警告消息);
        	result.setRstCode(TransConstant.文件上传参数CHECK_未通过);
        	ajaxResult.setData(result);
        	return ajaxResult;
        }
        // 返回值
        AjaxResult returnResult = new AjaxResult("解析、转换成功，请点击【下载】按钮进行下载", MessageType.提示消息);

        // 获取sheet数据，（第一个sheet有导入数据）
        if (dto != null && dto.getSheetInfoList() != null
            && dto.getSheetInfoList().get(0) != null) {
            // 获取导入数据的sheet信息
            ExcelImpFileSheetInfoDto sheetInfo = dto.getSheetInfoList().get(0);
            // 获取导入数据
            List<List<String>> sheetDataList = sheetInfo.getSheetDataList();

            // 数据转换
            if (sheetDataList != null && !sheetDataList.isEmpty()) {
                // 根据模版类型调用对应方法
                if (DictCodeListExt1.数据移植转换模板类型_产品余额报表.getValue().equals(modelType)) {
                	// 调用对应方法，返回值为AjaxResult
                	result = prodBalanceTransplantService.execute(sheetDataList);
                    if (CollectionUtils.isEmpty(result.getCheckList())) {
                        progressBarService.removePercent("Transformation001", ProgressBarService.FILE_UPLOAD_ID);
                        returnResult.setData(result);
                        result.setModelType(TransConstant.下载模板名称_产品);
                        return returnResult;
                    }
                    ajaxResult = new AjaxResult();
                    ajaxResult.setData(result);
                    return ajaxResult;
                }
                else if (DictCodeListExt1.数据移植转换模板类型_资产余额报表.getValue().equals(modelType)) {
                    // 调用对应方法，返回值为AjaxResult
                	result = assetBalanceTransplantService.execute(sheetDataList);
                    if (CollectionUtils.isEmpty(result.getCheckList())) {
                        progressBarService.removePercent("Transformation001", ProgressBarService.FILE_UPLOAD_ID);
                        returnResult.setData(result);
                        result.setModelType(TransConstant.下载模板名称_资产);
                        return returnResult;
                    }
                    ajaxResult = new AjaxResult();
                    ajaxResult.setData(result);
                    return ajaxResult;
                }
                else {
                    returnResult = new AjaxResult("无对应操作", MessageType.提示消息);
                    result.setRstCode(TransConstant.文件上传参数CHECK_未通过);
                    returnResult.setData(result);
                }
            }
        }
        return returnResult;
    }
     
    
    private boolean checkFileName(String modelType,String fName){
    	boolean falg=false;
    	if (DictCodeListExt1.数据移植转换模板类型_产品余额报表.getValue().equals(modelType)){
    		if(fName.startsWith("产品余额报表_")){
    			falg=true;
    		}
    	}
    	else if (DictCodeListExt1.数据移植转换模板类型_资产余额报表.getValue().equals(modelType)){
    		if(fName.startsWith("资产余额报表_")){
    			falg=true;
    		}
    	}
    	return falg;
    }
    
}

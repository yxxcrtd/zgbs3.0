/*
 * 文件名：DataTransplantController001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年11月8日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.datatransplant.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joyintech.framework.file.SafelyFile;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.datatransplant.dto.TemplateCheckDto;
import com.joyintech.service.valuation.datatransplant.service.AccountsBalanceTransplantService;
import com.joyintech.service.valuation.datatransplant.service.PositionTemplate001Service;
import com.joyintech.service.valuation.datatransplant.service.ValueAddedTaxService;
import com.joyintech.service.valuation.datatransplant.service.VatNinetyTemplateService;
import com.joyintech.system.excelimp.dto.ExcelImpFileInfoDto;
import com.joyintech.system.excelimp.dto.ExcelImpFileSheetInfoDto;
import com.joyintech.system.excelimp.service.ExcelImpHandle001Service;
import com.joyintech.tams.common.base.entity.ValSubjectTableid;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 数据移植
 * 
 * @version 1.0 2018年11月8日
 * @see DataTransplantController001
 * @since 1.0
 */
@RestController("vataTransplant001Ctl_dtx")
//@RestController(value="serviceDataTransplantController001")
@RequestMapping("/DataTransplant001")
public class DataTransplantController001 extends BaseController {

    /**
     * ExcelImpHandle001Service
     */
    @Autowired
    private ExcelImpHandle001Service excelImpHandle001Service;

    @Autowired
    private AccountsBalanceTransplantService accountsBalanceTransplantService;

    @Autowired
    private ValueAddedTaxService valueAddedTaxService;

    @Autowired
    private PositionTemplate001Service positionTemplate001Service;

    @Autowired
    private VatNinetyTemplateService vatNinetyTemplateService;

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
     * 重写init方法，加载页面 {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        // 数据移植 - 模版类型
        this.setSelectOptionToModel(model, DictCode.数据移植模版类型);
        return super.init(model);
    }

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
    @Transactional
    @DTXTransaction
    @RequestMapping(value = "/uploadFile")
    @ResponseBody
    public AjaxResult uploadFile(HttpServletRequest request, HttpServletResponse response,
                                 ModelMap model) throws FileNotFoundException {

        // 获取默模版类型
        String modelType = request.getParameter("modelType");

        // 模版类型校验（模版类型不能为空）
        if (modelType == null || modelType.trim().length() == 0) {
            return new AjaxResult("请选择模版类型", MessageType.警告消息);
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
        	return new AjaxResult("上传模板不是一个有效的Excel文件", MessageType.警告消息);
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
        	  return new AjaxResult("模板类型要与模板名保持一致", MessageType.警告消息);
        }
        
        
        // 返回值
        AjaxResult returnResult = new AjaxResult(getMessage("VAL_EXCELIMP_E_0020"), MessageType.提示消息);

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
                if (DictCodeListExt1.数据移植模版类型_头寸模版.getValue().equals(modelType)) {
                    // 调用对应方法，返回值为AjaxResult
                    List<TemplateCheckDto> checkList = positionTemplate001Service.insert(
                        sheetDataList);
                    if (CollectionUtils.isEmpty(checkList)) {
                        progressBarService.removePercent("DataTransplant001", ProgressBarService.FILE_UPLOAD_ID);
                        return returnResult;
                    }
                    AjaxResult ajaxResult = new AjaxResult();
                    ajaxResult.setData(checkList);
                    return ajaxResult;
                }
                else if (DictCodeListExt1.数据移植模版类型_增值税模版.getValue().equals(modelType)) {
                    // 调用对应方法，返回值为AjaxResult
                    List<TemplateCheckDto> checkList = valueAddedTaxService.valueAddedTaxExec(
                        sheetDataList);
                    if (CollectionUtils.isEmpty(checkList)) {
                        progressBarService.removePercent("DataTransplant001", ProgressBarService.FILE_UPLOAD_ID);
                        return returnResult;
                    }
                    AjaxResult ajaxResult = new AjaxResult();
                    ajaxResult.setData(checkList);
                    return ajaxResult;
                }
                else if (DictCodeListExt1.数据移植模版类型_账务余额模版.getValue().equals(modelType)) {
                    // 调用对应方法，返回值为AjaxResult
                    List<TemplateCheckDto> checkList = accountsBalanceTransplantService.execute(
                        sheetDataList, modelType);
                    if (CollectionUtils.isEmpty(checkList)) {
                        progressBarService.removePercent("DataTransplant001", ProgressBarService.FILE_UPLOAD_ID);
                        return returnResult;
                    }
                    AjaxResult ajaxResult = new AjaxResult();
                    ajaxResult.setData(checkList);
                    return ajaxResult;
                }
//                else if (DictCodeListExt1.数据移植模版类型_账务余额在途债券模版.getValue().equals(modelType)) {
//                    // 调用对应方法，返回值为AjaxResult
//                    List<TemplateCheckDto> checkList = accountsBalanceTransplantService.execute(
//                        sheetDataList, modelType);
//                    if (CollectionUtils.isEmpty(checkList)) {
//                        progressBarService.removePercent("DataTransplant001", ProgressBarService.FILE_UPLOAD_ID);
//                        return returnResult;
//                    }
//                    AjaxResult ajaxResult = new AjaxResult();
//                    ajaxResult.setData(checkList);
//                    return ajaxResult;
//                }
                else if (DictCodeListExt1.数据移植模版类型_增值税_90号文模版.getValue().equals(modelType)) {
                    // 调用对应方法，返回值为AjaxResult
                    List<TemplateCheckDto> checkList = vatNinetyTemplateService.vatNinetyTemplateExec(
                        sheetDataList);
                    if (CollectionUtils.isEmpty(checkList)) {
                        progressBarService.removePercent("DataTransplant001", ProgressBarService.FILE_UPLOAD_ID);
                        return returnResult;
                    }
                    AjaxResult ajaxResult = new AjaxResult();
                    ajaxResult.setData(checkList);
                    return ajaxResult;
                }
                else {
                    returnResult = new AjaxResult("无对应操作", MessageType.提示消息);
                }
            }
        }

        return returnResult;
    }
   
    /**
     * 根据条件分页查询
     * 
     * @return
     * @throws IOException
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("searchList")
    @ResponseBody
    public List<TemplateCheckDto> findPage()
        throws IOException {
        String errorData = this.getParam("errorData");
        List<TemplateCheckDto> list = new ArrayList<>();
        if (StringUtil.isNotEmpty(errorData)) {
            list = JsonUtils.json2List(errorData, TemplateCheckDto.class);
        }
        progressBarService.removePercent("DataTransplant001", ProgressBarService.FILE_UPLOAD_ID);
        return list;
    }
    
    
    private boolean checkFileName(String modelType,String fName){
    	boolean falg=false;
    	if (DictCodeListExt1.数据移植模版类型_头寸模版.getValue().equals(modelType)){
    		if(fName.startsWith("头寸模板_")){
    			falg=true;
    		}
    	}
    	else if (DictCodeListExt1.数据移植模版类型_增值税模版.getValue().equals(modelType)){
    		if(fName.startsWith("增值税模板_")){
    			falg=true;
    		}
    	}
      else if (DictCodeListExt1.数据移植模版类型_账务余额模版.getValue().equals(modelType)){
    	  if(fName.startsWith("账务余额模板_")){
  			falg=true;
  		}
      }
//      else if (DictCodeListExt1.数据移植模版类型_账务余额在途债券模版.getValue().equals(modelType)){
//    	  if(fName.startsWith("账务余额模板（在途债券）_")){
//    			falg=true;
//    		}
//      }
      else if (DictCodeListExt1.数据移植模版类型_增值税_90号文模版.getValue().equals(modelType)) {
    	  if(fName.startsWith("增值税_90号文模板_")){
  			falg=true;
  		}
      }
    	return falg;
    }
    @GetMapping("/getTableIds")
    public List<ValSubjectTableid> getTableIds(){
    	return accountsBalanceTransplantService.getTableIds();
    }
}

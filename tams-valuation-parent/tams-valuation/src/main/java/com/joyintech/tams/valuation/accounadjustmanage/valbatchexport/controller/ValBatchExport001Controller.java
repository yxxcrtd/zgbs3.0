/*
 * 文件名：AssetLiabilityStatementQuery001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年9月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.accounadjustmanage.valbatchexport.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.accounadjustmanage.assetbook.service.ValAssetBook001Service;
import com.joyintech.tams.valuation.accounadjustmanage.ownerequitychangenew.service.OwnerEquityChangeNew001Service;
import com.joyintech.tams.valuation.accounadjustmanage.valbatchexport.common.ValBatchExport001Util;
import com.joyintech.tams.valuation.accounadjustmanage.valbatchexport.dto.ValBatchExport001Dto;
import com.joyintech.tams.valuation.accounadjustmanage.valbatchexport.dto.ValBatchExport001SearchDto;
import com.joyintech.tams.valuation.accounadjustmanage.valbatchexport.service.ValBatchExport001Service;
import com.joyintech.tams.valuation.accounadjustmanage.valprofitquerynew.service.ValProfitNewQuery001Service;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.SftpTools;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.dto.AssetLiabilityStatementNewQuery001SearchDto;
import com.joyintech.tams.valuation.financialstatement.assetliailitystatementnew.service.AssetLiabilityStatementNewQuery001Service;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;


/**
 * 
 * @author huanghao
 *  三表批量导出
 */

@Controller
@PageId("ValBatchExport001")
@RequestMapping("/valuation/accounadjustmanage/valbatchexport/ValBatchExport001")
public class ValBatchExport001Controller extends BaseController {

    
    @Autowired
    private ValBatchExport001Service service;
    
    @Autowired
    private AssetLiabilityStatementNewQuery001Service assetLiabilityStatementNewQuery001Service;
    
    @Autowired
    private OwnerEquityChangeNew001Service ownerEquityChangeNew001Service;
    
    @Autowired
    private ValProfitNewQuery001Service valProfitNewQuery001Service;

    @Autowired
    private ValAssetBook001Service valAssetBook001Service;
    
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(ValBatchExport001Controller.class);
    
    @Override
    public String init(Model model) {
        String YJZZ = CacheUtil.getSysParam("UNITFYCACCOUNTANT_MODEL_CODE");
        String[] yjzuArr = null;
        if (StringUtil.isNotEmpty(YJZZ)) {
            yjzuArr = YJZZ.split(",");
        }
        model.addAttribute("YJZZ", yjzuArr);
        model.addAttribute("lastDimenVal", DictCodeListExt1.上期维度_环比.getValue());
        this.setSelectOptionToModel(model, DictCode.批处理账套类型);
        this.setSelectOptionToModel(model, DictCode.上期维度);
        super.setSelectOptionToModel(model, DictCode.报表期限类型);
        this.setSelectOptionToModel(model, DictCode.报表维度);
        this.setSelectOptionToModel(model, DictCode.报表类型);
        return super.init(model);
    }

    /**
     * 查询条件检查
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Valid ValBatchExport001SearchDto dto, BindingResult result) {
        
        if (null != dto.getBooksetId() && dto.getBooksetId().length<=0) {
        		return new AjaxResult("请输入账套代码。", MessageType.错误消息);
        	}
        if (StringUtil.isEmpty(dto.getReportType())) {
            return new AjaxResult("请输入报表类型。", MessageType.错误消息);
        }else if(dto.getReportType().contains("02")||dto.getReportType().contains("03")){
            if(StringUtil.isEmpty(dto.getLastDimen())){
                return new AjaxResult("请输入上期维度。", MessageType.错误消息);
            }
        }
        if (result != null && !result.hasErrors()) {// 基础验证通过
            String reportTermType = dto.getReportTermType();// 报表期限类型
            String reportTerm = dto.getReportTerm();// 报表期限明细 : 半年，季度明细

            if ((DictCodeListExt1.报表期限类型_半年报.getValue().equals(reportTermType)
                 || DictCodeListExt1.报表期限类型_季报.getValue().equals(reportTermType))
                && StringUtils.isEmpty(reportTerm)) {// 报表期限类型为半年报或季报时，报表期限不能为空
                return new AjaxResult("期限不能为空！", MessageType.错误消息);
            }
        }
        return commonCheck(result, "");
    }

    /**
     * 检索账套代码(只取理财账套)
     * 
     * @return 账套代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getBooksetId2")
    public List<SuggestDto> getBooksetId2()
        throws Exception {
        String param = super.getRequest().getParameter("param");
        Map<String, Object> params = new HashMap<>();
        params.put("param",param);
        return valAssetBook001Service.getBooksetId2(params);
    }

    /**
     * 联动：报表期限类型 - 季度 半年
     * 
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("getTermLink")
    public List<SelectOptionDto> getTradeMarket()
        throws Exception {
        Map<String, Object> param = RequestUtil.getPageParam();
        String reportTermType = (String)param.get("reportTermType");

        param.put("dict_code_parent", DictCode.报表期限类型.getValue());
        param.put("item_code_parent", param.get("reportTermType"));
        if (DictCodeListExt1.报表期限类型_半年报.getValue().equals(reportTermType)) {
            param.put("dict_code_child", DictCode.半年.getValue());
            return assetLiabilityStatementNewQuery001Service.getLinkSelectOption(param);
        }
        if (DictCodeListExt1.报表期限类型_季报.getValue().equals(reportTermType)) {
            param.put("dict_code_child", DictCode.季度.getValue());
            return assetLiabilityStatementNewQuery001Service.getLinkSelectOption(param);
        }
        return null;
    }

    /**
     * 获得 表头信息
     * 
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping("getHeadInfo")
    public Map<String, String> getHeadInfo(@RequestBody ValBatchExport001SearchDto dto)
        throws ParseException {
        Map<String, String> map = Maps.newHashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date reportDate = service.analyseBalanceDate(dto, ValBatchExport001Util.ENDING_BALANCE);

        map.put("statementDate", sdf.format(reportDate));
        map.put("statementType", SysDataUtil.getNameFromDataDict(DictCode.报表期限类型, dto.getReportTermType()));
        map.put("org", CacheUtil.getSysParam(GlobalConstant.财务报表_编制单位));
        return map;
    }

    /**
     * 获得 资产
     * 
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping("findAssets")
    public List<ValBatchExport001Dto> findAssets(ValBatchExport001SearchDto dto)
        throws ParseException {
    	List<ValBatchExport001Dto> dtoList = new ArrayList<ValBatchExport001Dto>();
    	Map<String, Object> paraMap = RequestUtil.getParameters(getRequest());
    	// 报表维度为单账套
    	if ("01".equals(String.valueOf(paraMap.get("reportDimen")))) {
    		dtoList = service.findAssetsStatement(dto);
    	} else {
    		// 报表维度为全账套
            Map<String, Object> params = new HashMap<>();
            params.put("param",super.getRequest().getParameter("param"));
    		dto.setAccountList(valAssetBook001Service.getBooksetId2(params));
    		dtoList = service.findAssetsStatementAll(dto);
    	}

        if(CollectionUtils.isNotEmpty(dtoList)) {
            dtoList.add(new ValBatchExport001Dto());
            dtoList.add(new ValBatchExport001Dto());
            dtoList.add(new ValBatchExport001Dto());
            dtoList.add(new ValBatchExport001Dto());
            dtoList.addAll(ValBatchExport001Util.getAssetListSum(dtoList));

            dtoList.forEach(item -> {
                if (StringUtils.isEmpty(item.getItemName())) {
                    item.setInitialBalance(null);
                    item.setEndingBalance(null);
                }
            });
        }
        
        // 全账套情况下,添加样式
        if ("02".equals(String.valueOf(paraMap.get("reportDimen")))) {
    		service.dealStyle(dtoList);
    	}

        return dtoList;
    }

    /**
     * 获得 负债和所有者权益
     * 
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping("findLiabilityInterest")
    public List<ValBatchExport001Dto> findLiabilityInterest(ValBatchExport001SearchDto dto)
        throws ParseException {
    	List<ValBatchExport001Dto> dtoList = new ArrayList<ValBatchExport001Dto>();
    	Map<String, Object> paraMap = RequestUtil.getParameters(getRequest());
    	// 报表维度为单账套
    	if ("01".equals(String.valueOf(paraMap.get("reportDimen")))) {
    		dtoList = service.findLiabilityInterestStatement(dto);
    	} else {
    		// 报表维度为全账套
            Map<String, Object> params = new HashMap<>();
            params.put("param",super.getRequest().getParameter("param"));
    		dto.setAccountList(valAssetBook001Service.getBooksetId2(params));
    		dtoList = service.findLiabilityInterestStatementAll(dto);
    	}
    	if(CollectionUtils.isNotEmpty(dtoList)) {
            dtoList.addAll(ValBatchExport001Util.getLiabilityInterestListSum(dtoList));
        }
    	
    	// 全账套情况下,添加样式
    	if ("02".equals(String.valueOf(paraMap.get("reportDimen")))) {
    		service.dealStyle(dtoList);
    	}
        return dtoList;
    }

    /**
     * 导出信息excel
     * 
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export(ValBatchExport001SearchDto dto,HttpServletResponse response)
        throws Exception {
        List<String> paths = new ArrayList<String>();
    	Map<String, Object> paraMap = RequestUtil.getPageParam();
    	paraMap.remove("reportDimen");
    	paraMap.put("reportDimen", DictCodeListExt2.批处理账套类型_账套.getValue());
    	dto.setReportDimen(DictCodeListExt2.批处理账套类型_账套.getValue());
    	
    	String reportDate = dto.getReportDate() ;//报表时间
    	String reportTermType = dto.getReportTermType();//报表期限类型
    	String reportTerm = dto.getReportTerm();//期限明细
    	String reportDimen = dto.getReportDimen();//报表维度
    	String[] ids = null;
    	ids = dto.getBooksetId();
    	String booksetId =  StringUtils.join(ids, ",");//账套代码
    	String reportType = dto.getReportType();//报表类型
    	
    	List<SuggestDto> accountList = new ArrayList<SuggestDto>();
    	if (DictCodeListExt2.批处理账套类型_账套组.getValue().equals(paraMap.get("batchBooksetType"))) {//账套组-进行账套查询
    		accountList = valAssetBook001Service.getBooksetIdsByGroups(paraMap);
    		paraMap.put("accounts", accountList);
    		dto.setAccountList(accountList);
    	}else{//多账套查询
    	    String[] strings = dto.getBooksetId();
    	    if(strings !=null){
                for(String bookset : strings){
                    SuggestDto e = new SuggestDto();
                    e.setCode(bookset);
                    accountList.add(e);
                }
            }
    	    dto.setAccountList(accountList);
    	    paraMap.put("accounts", accountList);
    	}
    	
    	//资产负债表导出
    	AssetLiabilityStatementNewQuery001SearchDto asdto = new AssetLiabilityStatementNewQuery001SearchDto();
    	asdto.setReportDate(reportDate);//报表时间
    	asdto.setReportTermType(reportTermType);//报表期限类型
    	asdto.setReportTerm(reportTerm);//期限明细
    	asdto.setReportDimen(reportDimen);//报表维度
    	asdto.setBooksetId(booksetId);//账套代码
    	asdto.setAccountList(dto.getAccountList());
    	if(reportType.contains(DictCodeListExt2.报表类型_资产负债表.getValue())){
    	    List<String> asPath =  assetLiabilityStatementNewQuery001Service.exportExcelBatch(asdto);
    	    paths.addAll(asPath);
    	}
    	
    	if(reportType.contains(DictCodeListExt2.报表类型_所有者权益表.getValue())){
    	    paraMap.put("valDate", reportDate);
    	    //所有者权益表
    	    List<String> ownPath = ownerEquityChangeNew001Service.exportExcelBatch(paraMap);
    	    paths.addAll(ownPath);
    	}
        
        
    	if(reportType.contains(DictCodeListExt2.报表类型_利润表.getValue())){
    	    //利润表
            String booksetIds = String.join(",",(List)paraMap.get("booksetIds"));
            paraMap.put("booksetIds",booksetIds);
    	    List<String> proPath =  valProfitNewQuery001Service.exportExcelBatch(paraMap);
    	    paths.addAll(proPath);
    	}
    	//压缩文件
        String exportFilePath = CacheUtil.getSysParam(GlobalConstant.COM_PROP_ACCESS_EXPORTPATH);
        String zipFileName = FileNameUtil.createFileName("zip");
        File zipFile = new File(exportFilePath + File.separator + zipFileName);
        FileOutputStream stream = null;
        ZipOutputStream zipOut = null;
        try {
            stream = new FileOutputStream(zipFile);
            zipOut = new ZipOutputStream(stream);
            InputStream input = null;
            String filename = "";
            SftpTools sftpTools = new SftpTools();
            try {
                // 上传文件到文件服务器上
                String ip = CacheUtil.getSysParam("FILESERVER_HOST");// 主机IP
                Integer port = Integer.parseInt(CacheUtil.getSysParam("FILESERVER_PORT"));// 主机端口
                String username = CacheUtil.getSysParam("FILESERVER_USERNAME");// 主机用户名
                String password = CacheUtil.getSysParam("FILESERVER_PASSWORD");// 主机密码或秘钥地址
                String sftpLoginType = CacheUtil.getSysParam("SFTP_LOGIN_TYPE");// SFTP登录方式

				sftpTools.login(username, password, ip, port, sftpLoginType);// sftp登录
                
            } catch (Exception e) {
                logger.error("",e);
            }
            for (String path : paths) {
                String dateString = DateUtil.format(new Date());
                File file = new File(path);
                String remotePath = CacheUtil.getSysParam("FILESERVER_REMOTE_PATH");
                //modify jinmaodong 20181229 路径错误，应该是带有日期文件夹的
                String remoteFile = CacheUtil.getSysParam("FILESERVER_REMOTE_PATH") + "/" + dateString + "/" + filename;
                try {
                    // 下载路径从SYS_PARA获取
                    String localdir = CacheUtil.getSysParam("EXPORT_VALBOKBANLANCE");
                    sftpTools.getFile(remotePath, remoteFile, localdir);
                    input = new FileInputStream(file);
                    zipOut.putNextEntry(new ZipEntry(file.getName()));
                    int tmp = 0;
                    while ((tmp = input.read()) != -1) {
                        zipOut.write(tmp);
                    }
                } catch (Exception e) {
                    logger.error("",e);
                } finally {
                    if (input != null) {
                        input.close();
                    }
                }
            }
            sftpTools.close();
            zipOut.flush();
        } catch (Exception e) {
            logger.error("",e);
        } finally {
            if (zipOut != null) {
                zipOut.close();
            }
            if (stream != null) {
                stream.close();
            }
        }
        return ExportUtil.export(zipFileName, zipFile, true);
    }
    
    /**
     * 获取默认日期信息
     * 
     * @return AssetLiabilityStatementQuery001SearchDto
     * @throws Exception exception
     */
    @ResponseBody
    @RequestMapping("getDateInfo")
    public ValBatchExport001SearchDto getDateInfo()
        throws Exception {
        return service.getDateInfo(RequestUtil.getPageParam());
    }
}

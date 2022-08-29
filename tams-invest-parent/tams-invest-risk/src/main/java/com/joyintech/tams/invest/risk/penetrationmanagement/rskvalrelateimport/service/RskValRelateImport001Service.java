/*
 * 文件名：RskValRelateImport001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dongzhiling
 * 修改时间：2018年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.risk.penetrationmanagement.rskvalrelateimport.service;

import java.io.File;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.dao.RskOutValtblImportHisMapper;
import com.joyintech.tams.common.base.dao.ValtblImportHisTraceMapper;
import com.joyintech.tams.common.base.entity.ChbAssetInfo;
import com.joyintech.tams.common.base.entity.RskOutValtblImportHis;
import com.joyintech.tams.common.base.entity.RskOutValtblImportHisKey;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalassetcode.dto.RskValtblAssetSubMapDtlDto;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalassetcode.service.RskValAssetCode001Service;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalstructure.dto.RskValStructureDto;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalstructure.service.RskValStructureService;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalsumname.dto.RskValSumnameDto;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalsumname.service.RskValSumname001Service;
import com.joyintech.tams.invest.risk.valtblimport.dto.RskValTableImportParamDto;
import com.joyintech.tams.invest.risk.valtblimport.dto.RskValtblAssetSubDto;
import com.joyintech.tams.invest.risk.valtblimport.dto.ZipFileAssetInfoDto;
import com.joyintech.tams.invest.risk.valtblimport.service.RskValTableImportCommonService;
import com.joyintech.tams.invest.risk.valtblimport.service.RskValTableImportService;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalrelateimport.dto.RskValRelateImport001Dto;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author dongzhiling
 * @version 1.0 2018年12月20日
 * @see RskValRelateImport001Service
 * @since 1.0
 */
@Service
public class RskValRelateImport001Service extends CommonExportService<RskValRelateImport001Dto> {
    @Autowired
    private RskValTableImportCommonService rskValTableImportCommonService;
    @Autowired
    private RskValRelateImport002Service rskValRelateImport002Service;
    @Autowired
    private RskValTableImportService rskValTableImportService;
    @Autowired
    private ProgressBarService progressBarService;
    @Autowired
    private ValtblImportHisTraceMapper valtblImportHisTraceMapper;
    @Autowired
    private RskOutValtblImportHisMapper rskOutValtblImportHisMapper;
    // 平台日期
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

//    @Autowired(required = false)
    private static final Logger logger = LoggerFactory.getLogger(RskValRelateImport001Service.class);
    /**
     * 指标结构映射
     */
    @Autowired
    private RskValStructureService rskValStructureService;

    /**
     * 资产科目映射
     */
    @Autowired
    private RskValAssetCode001Service rskValAssetCode001Service;

    /**
     * 汇总科目映射
     */
    @Autowired
    private RskValSumname001Service rskValSumname001Service;
    /**
     * 画面查询 <功能详细描述>
     * 
     * @param parameters parameters
     * @return
     * @see
     * @since 1.0
     */
    public Page<RskValRelateImport001Dto> selecForPage(Map<String, Object> parameters) {
        if (StringUtil.isNotEmpty(parameters.get("groupId"))) {
            parameters.put("groupId", StringUtil.splitToList(null, parameters.get("groupId").toString()));
        }
        parameters.put("loginUserName", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(
            ServiceApplication.RISK_SERVICE_NAME + "/RskValRelateImport/selectForPage",
            parameters, RskValRelateImport001Dto.class);
    }

    /**
     * <一句话功能简述>
     * 组合代码联想下拉框
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryProdIdInfo(Map<String, Object> pageParam) {
//        pageParam.put("loginUserName", SecurityUtil.getLoginUser().getUserName());
        pageParam.put("selectTarget", "PortfolioId");
        pageParam.put("portfolioType", DictCodeListExt1.组合类型_委外组合.getValue());
        return restTemplate.postForList(
            ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/select" + pageParam.get("selectTarget").toString(),
            pageParam, SelectOptionDto.class);
    }

    /**
     * <一句话功能简述>
     * 批量删除
     * @param dto
     * @see
     * @since 1.0
     */
    public int delete(RskValRelateImport001Dto dto) {
        return restTemplate.postForObject(
            ServiceApplication.RISK_SERVICE_NAME + "/RskValRelateImport/deleteList", dto,
            Integer.class);
    }
    
    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
            "/com/joyintech/tams/invest/risk/penetrationmanagement/rskvalrelateimport/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RskValRelateImport001Dto> selectPageData(Map<String, Object> selectParam) {
        return selecForPage(selectParam);
    }
    public List<ExcelValidDto> batchUploadFile(String pageId, String filePath) {
        // 更新导入的进度条
        progressBarService.updatePercent(pageId, ProgressBarService.FILE_UPLOAD_ID, 0.5d, false);

        //获取所有错误信息
        List<ExcelValidDto> errorList = new ArrayList<>();
        // 获取本地ZIP包路径
        File file = new SafelyFile(filePath);
        List<File> fileList = new ArrayList<>();
        if (Objects.isNull(file)) {
            errorList.add(new ExcelValidDto(StringUtils.EMPTY, StringUtils.EMPTY, "上传估值表zip文件失败"));
            return errorList;
        }

        if (!RskValTableImportCommonService.decompressZip(file, fileList)) {
            errorList.add(new ExcelValidDto(StringUtils.EMPTY, StringUtils.EMPTY, "解压估值表zip文件失败"));
            return errorList;
        }

        if (!checkFileNum(fileList, errorList)) {
            return errorList;
        }

        List<ZipFileAssetInfoDto> list = new ArrayList<>();
        int sizeLength=fileList.size();
        for (int j=0;j<fileList.size();j++) {
            File file1=fileList.get(j);
            Boolean isError= checkFile(file1, errorList, list,filePath);
            if(isError){
                sizeLength=sizeLength-1;
            }
        }
      /*  if (errorList.size() > 0) {
            return errorList;
        }*/
        progressBarService.updatePercent(pageId, ProgressBarService.FILE_UPLOAD_ID, 0.55d, false);
        saveData(pageId, sizeLength, list, errorList,filePath);
        if (errorList.size() > 0) {
            return errorList;
        }
        return errorList;
    }

    private void saveData(String pageId, int sizeLength, List<ZipFileAssetInfoDto> list, List<ExcelValidDto> errorList,String filePath) {
        String deptCode=(LoginUserUtil.getInstCode()!= null && LoginUserUtil.getInstCode().size()>0) ? LoginUserUtil.getInstCode().get(0):null;
        //Date supDate = iPlatformDate.getSysDate();
        Date supDate=DateUtil.platDateFromCache();
        Date startDate = DateUtil.now();
        //默认最大的线程数
        int maxPoolCapacitySize = 50;
        //获取系统参数最大的缓存的线程队列
        if (StringUtil.isNotEmpty(CacheUtil.getSysParam("BATCH_IMPORT_VAL_MAX_THREAD"))) {
            maxPoolCapacitySize = Integer.parseInt(CacheUtil.getSysParam("BATCH_IMPORT_VAL_MAX_THREAD"));
        }
        int listSize = sizeLength;
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();//线程池
        int initPoolSize = listSize < 10 ? listSize : 10;//初始化线程数
        int actMaxPoolSize = listSize < maxPoolCapacitySize ? listSize : maxPoolCapacitySize;//最大线程数
        taskExecutor.initialize();//初始化
        taskExecutor.setCorePoolSize(initPoolSize);//初始线程数
        taskExecutor.setMaxPoolSize(maxPoolCapacitySize);//最大线程数
        taskExecutor.setQueueCapacity(actMaxPoolSize);//缓存队列
        taskExecutor.setThreadNamePrefix("svrval-batch-import-");//线程前缀

        List<Future<String>> tasks = new ArrayList<>();
        for (ZipFileAssetInfoDto dto : list) {
            String fileName = dto.getFileName();
            Date valDate = dto.getDate();
            SelectOptionDto info = dto.getInfo();
            String fileNameText=dto.getFileNameText();

            Future<String> singleExportFileDto = taskExecutor.submit(new Callable<String>() {
                @Override
                public String call() {
                    String res=dealSvrValFile(info.getId(), info.getText(), valDate, fileName,fileNameText,filePath);
                    if(StringUtil.isNotEmpty(res)){
                         rskValTableImportCommonService.saveRskOutValtblImportHis(fileNameText, filePath, info.getId(), info.getText(), valDate,
                                    DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表汇总科目映射异常.getValue(),startDate,supDate,deptCode,null);
                    }
                    return res;
                }
            });
            tasks.add(singleExportFileDto);
        }

        //循环查看每个线程的执行结果，如果全部执行完，或者执行时间超过20分钟则取消循环
        long maxForTime = 20 * 60 * 1000;//20分钟
        long startForTime = System.currentTimeMillis();//开始循环的时间

        try {
            Set<Integer> taskSet = new HashSet<>();
            int successNum = 0;
            while (true) {
                boolean isAllDone = true;
                for (Future<String> taskResult : tasks) {
                    isAllDone &= (taskResult.isDone() || taskResult.isCancelled());
                    if (taskResult.isDone() || taskResult.isCancelled()) {
                        if (taskSet.add(taskResult.hashCode())) {
                            successNum++;
                            // 更新导入的进度条
                            double processD = (55 + 45 * successNum / list.size()) / 100.0;
                            progressBarService.updatePercent(pageId, ProgressBarService.FILE_UPLOAD_ID, processD, false);
                            if (taskResult.get() != null) {
                                String arr[] = taskResult.get().split(GlobalConstant.COMMA);
                                if (arr.length == 2) {
                                    errorList.add(new ExcelValidDto(arr[0], "", taskResult.get()));
                                } else {
                                    errorList.add(new ExcelValidDto("", "", taskResult.get()));
                                }
                            }
                        }
                    }
                }
                if (isAllDone || (System.currentTimeMillis() - startForTime) > maxForTime) {
                    // 任务都执行完毕，跳出循环
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(200);
            }
        } catch (Exception e) {
            logger.error("saveData error: ", e);
            errorList.add(new ExcelValidDto(StringUtils.EMPTY, StringUtils.EMPTY, "保存估值表文件失败"));
        } finally {
            taskExecutor.shutdown();
        }
    }



    private String dealSvrValFile(String assetCode, String assetName, Date valDate, String fileName,String fileNameText,String filePath) {
        Date supDate =DateUtil.platDateFromCache();
        // 获取映射信息
        RskValRelateImport001Dto import001Dto = rskValRelateImport002Service.selectInfoById(assetCode);
        if (Objects.isNull(import001Dto) || Objects.isNull(import001Dto.getStructMapId())
                || Objects.isNull(import001Dto.getAssetSubMapId()) || Objects.isNull(import001Dto.getSumsubMapId())) {
            return assetName + ",获取映射信息失败";
        }
        // 预览
        RskValTableImportParamDto dto = getRskValTableImportParamDto(valDate, fileName, import001Dto);
        List<RskValtblAssetSubDto> rskValtblAssetSubDtos = null;
        try {
            rskValtblAssetSubDtos = rskValTableImportService.viewValTable(dto);
        } catch (Exception e) {
            return assetName + ",导入失败";
        }
        if (Objects.isNull(rskValtblAssetSubDtos)) {
            return assetName + ",导入失败";
        }
        // 保存
        RskValtblAssetSubDto rskValtblAssetSubDto = getRskValtblAssetSubDto(assetCode, valDate, rskValtblAssetSubDtos);
        try {
            List<String> errorList= rskValTableImportService.insert(rskValtblAssetSubDto);
            if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()){
                String deptCode=(LoginUserUtil.getInstCode()!= null && LoginUserUtil.getInstCode().size()>0) ? LoginUserUtil.getInstCode().get(0):null;
                Date startDate = DateUtil.now();
                StringBuilder sb = new StringBuilder(org.apache.commons.lang3.StringUtils.EMPTY);
                for (String item : errorList) {
                    sb.append("[");
                    sb.append(item);
                    sb.append("]");
                    sb.append(GlobalConstant.HTML_NEWLINE);
                }
                String errorCode=StringUtil.EMPTY;
                if(StringUtil.isNotEmpty(errorList) && errorList.size()>0){
                    errorCode=DictCodeListExt1.估值表自动解析错误码_自动加券失败.getValue();
                }
                //保存委托估值导入记录表
                String chbValSplitStr = CacheUtil.getSysParam(BussinessConstant.CHB_VAL_SPLIT_STR);
                rskValTableImportCommonService.saveRskOutValtblImportHis(fileNameText, filePath, assetCode, assetName, valDate,
                        DictCodeListExt1.估值表自动解析处理状态_正常结束.getValue(),errorCode,startDate,supDate,deptCode,sb.toString());
            }
        } catch (ParseException e) {
            return assetName + ",保存失败";
        }
       /* if (dto.getWarnMap().get(assetName) > 0) {
            return assetName + ",存在" + dto.getWarnMap().get(assetName) + "条科目未配置映射";
        }*/
        return null;
    }

    /**
     * 组装RskValtblAssetSubDto
     *
     * @param assetCode
     * @param valDate
     * @param rskValtblAssetSubDtos
     * @return
     */
    private RskValtblAssetSubDto getRskValtblAssetSubDto(String assetCode, Date valDate, List<RskValtblAssetSubDto> rskValtblAssetSubDtos) {
        RskValtblAssetSubDto rskValtblAssetSubDto = new RskValtblAssetSubDto();
        rskValtblAssetSubDto.setSelectedItem(rskValtblAssetSubDtos);
        rskValtblAssetSubDto.setPtlId(assetCode);
        rskValtblAssetSubDto.setValDate(valDate);
        rskValtblAssetSubDto.setImpMode(DictCodeListExt1.导入方式_手工.getValue());
        return rskValtblAssetSubDto;
    }

    /**
     * 组装RskValTableImportParamDto
     *
     * @param valDate
     * @param fileName
     * @param import001Dto
     * @return
     */
    private RskValTableImportParamDto getRskValTableImportParamDto(Date valDate, String fileName, RskValRelateImport001Dto import001Dto) {
        RskValTableImportParamDto dto = new RskValTableImportParamDto();
        dto.setValTableFileName(fileName);
        dto.setValDate(valDate);
        BeanUtil.copyPropertiesIgnoreNull(dto, import001Dto);
        dto.setStartRow(Long.toString(import001Dto.getStartRow()));
        dto.setSubjectCodeCol(Long.toString(import001Dto.getSubjectCodeCol()));
        dto.setAssetName(import001Dto.getName());
        return dto;
    }

    private boolean checkFileNum(List<File> fileList, List<ExcelValidDto> errorList) {
        int maxNum = 100;
        if (CacheUtil.getSysParam("BATCH_IMPORT_VAL_MAX") != null) {
            maxNum = Integer.parseInt(CacheUtil.getSysParam("BATCH_IMPORT_VAL_MAX"));
        }
        if (fileList.size() > maxNum) {
            errorList.add(new ExcelValidDto(StringUtils.EMPTY, StringUtils.EMPTY, "批量上传估值表文件的数量不能超过："+maxNum));
            return false;
        }
        if (StringUtil.isEmptyList(fileList)) {
            errorList.add(new ExcelValidDto(StringUtils.EMPTY, StringUtils.EMPTY, "批量上传估值表文件为空"));
            return false;
        }
        return true;
    }

    private Boolean checkFile(File file, List<ExcelValidDto> errorList, List<ZipFileAssetInfoDto> list,String filePath) {
        String deptCode=(LoginUserUtil.getInstCode()!= null && LoginUserUtil.getInstCode().size()>0) ? LoginUserUtil.getInstCode().get(0):null;
        Boolean isError=false;
       // Date supDate = iPlatformDate.getSysDate();
        Date supDate =DateUtil.platDateFromCache();
        Date startDate = DateUtil.now();
        String fileName = file.getName();
        String chbValSplitStr = CacheUtil.getSysParam(BussinessConstant.CHB_VAL_SPLIT_STR);
        String[] arr = fileName.split(chbValSplitStr);
        if (arr.length != 2) {
            errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "，不符合格式要求"));
            rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, StringUtil.EMPTY, fileName, supDate,
                    DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_文件名异常组合or日期格式错误.getValue(),startDate,supDate,deptCode,null);
            isError=true;
        } else {
            Date valDate = rskValTableImportCommonService.getDateFromStr(arr[1]);
            String days = StringUtil.defaultString(CacheUtil.getSysParam("MAX_SUB_VALDATE"),"16");
            if (DateUtil.compareDate(valDate,DateUtil.parse(DateUtil.addDay(iPlatformDate.getSysDateStr(),-1*Integer.valueOf(days))))<0){
                errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "超出可导入的估值表日期" + DateUtil.addDay(iPlatformDate.getSysDateStr(),-1*Integer.valueOf(days))));
                rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, StringUtil.EMPTY, fileName, supDate,
                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_超过可配置的估值日期.getValue(),startDate,supDate,deptCode,null);
                isError=true;
            }else{
                if (Objects.isNull(valDate)) {
                    errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "，不符合格式要求"));
                    rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, StringUtil.EMPTY, fileName, supDate,
                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_文件名异常组合or日期格式错误.getValue(),startDate,supDate,deptCode,null);
                    isError=true;
                }else{
                    SelectOptionDto info = rskValTableImportCommonService.getAssetInfoFromName(arr[0]);
                    if (Objects.isNull(info)) {
                        errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "，组合名称不存在"));
                        rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, StringUtil.EMPTY, arr[0], valDate,
                                DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_无对应委外组合.getValue(),startDate,supDate,deptCode,null);

                        isError=true;
                    }
                    if(!isError){
                        //根据组合信息，查询是否有对应的映射信息
                        RskValRelateImport001Dto import001Dto = rskValRelateImport002Service.selectInfoById(info.getId());
                        if(null!=import001Dto){
                            if(Objects.isNull(import001Dto.getStructMapId())){
                                errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "，指标结构映射不能为空"));
                                rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, info.getId(), import001Dto.getName(), valDate,
                                        DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表指标结构映射异常.getValue(),startDate,supDate,deptCode,null);
                                isError=true;
                            }else{
                                //指标结构映射
                                List<RskValStructureDto> rskValStructureList = rskValStructureService.selectForList(import001Dto.getStructMapId());
                                if(rskValStructureList==null || rskValStructureList.size()==0){
                                    errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "，指标结构映射数据不能为空"));
                                    rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, info.getId(), import001Dto.getName(), valDate,
                                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表指标结构映射异常.getValue(),startDate,supDate,deptCode,null);
                                    isError=true;
                                }
                            }
                            if(Objects.isNull(import001Dto.getAssetSubMapId())){
                                errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "，资产科目映射不能为空"));
                                rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, info.getId(), import001Dto.getName(), valDate,
                                        DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表资产科目映射异常.getValue(),startDate,supDate,deptCode,null);
                                isError=true;
                            }else{
                                ///资产科目映射
                                List<RskValtblAssetSubMapDtlDto> rskValAssetCodeList= rskValAssetCode001Service.selectDtlList(import001Dto.getAssetSubMapId());
                                if(rskValAssetCodeList==null || rskValAssetCodeList.size()==0){
                                    errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "，资产科目映射数据不能为空"));
                                    rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, info.getId(), import001Dto.getName(), valDate,
                                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表资产科目映射异常.getValue(),startDate,supDate,deptCode,null);
                                    isError=true;
                                }
                            }
                            if(Objects.isNull(import001Dto.getSumsubMapId())){
                                errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "，汇总科目映射不能为空"));
                                rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, info.getId(), import001Dto.getName(), valDate,
                                        DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表汇总科目映射异常.getValue(),startDate,supDate,deptCode,null);
                                isError=true;
                            }else{
                                //汇总科目映射
                                List<RskValSumnameDto> rskValSumnameList =  rskValSumname001Service.selectForList(import001Dto.getSumsubMapId());
                                if(rskValSumnameList==null || rskValSumnameList.size()==0){
                                    errorList.add(new ExcelValidDto(fileName, StringUtils.EMPTY, fileName + "，汇总科目映射数据不能为空"));
                                    rskValTableImportCommonService.saveRskOutValtblImportHis(fileName, filePath, info.getId(), import001Dto.getName(), valDate,
                                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表汇总科目映射异常.getValue(),startDate,supDate,deptCode,null);
                                    isError=true;
                                }
                            }
                        }
                    }
                    if(!isError){
                        ZipFileAssetInfoDto dto = new ZipFileAssetInfoDto(file.getPath(), info, valDate,fileName);
                        list.add(dto);
                    }
                }
            }
        }
        return isError;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        boolean flag = false;
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 获取路径下的所有文件/文件夹
     * @param directoryPath 需要遍历的文件夹路径
     * @param isAddDirectory 是否将子文件夹的路径也添加到list集合中
     * @return
     */
    public static List<String> getAllFile(String directoryPath,boolean isAddDirectory) {
        List<String> list = new ArrayList<String>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if(isAddDirectory){
                    list.add(file.getAbsolutePath());
                }
                list.addAll(getAllFile(file.getAbsolutePath(),isAddDirectory));
            } else {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }

}

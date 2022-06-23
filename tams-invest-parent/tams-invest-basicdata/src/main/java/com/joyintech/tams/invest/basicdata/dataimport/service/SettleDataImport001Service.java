/*
 * 文件名：SettleDataImport001Service.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年08月06日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.invest.basicdata.dataimport.service;

import com.google.gson.JsonObject;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.Product;
import com.joyintech.framework.base.entity.*;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.*;
import com.joyintech.system.batch.batchstart.service.SysBatchStart001Service;
import com.joyintech.tams.common.base.entity.SettleTransPara;
import com.joyintech.tams.invest.basicdata.dataimport.dto.SettleTransResultDto;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tools.util.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据导入Service
 * @author 王波
 * @date 2020年08月06日
 * @see SettleDataImport001Service
 * @since 1.0
 */
@Service
public class SettleDataImport001Service extends BaseService {

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextpath.single:false}")
    private boolean isSingle;

    @Autowired
    private SysBatchStart001Service sysBatchStart001Service;

    private static final String SZD = "上中登";

    private static final String SZE = "深中登";

    // SzdJsmx数据文件02
    private static final String SZD_JSMX_02 = "jsmx02";
    // SzdJsmx数据文件03
    private static final String SZD_JSMX_03 = "jsmx03";

    private static final String SZD_ZQBD = "zqbd";

    private static final String SZD_ABCSJ = "abcsj";

    private static final String SJSMX0 = "sjsmx0";

    private static final String SJSMX1 = "sjsmx1";

    private static final String SJSFX = "sjsfx";

    private static final String SJSJG = "sjsjg";

    private static final String ZSMX = "zsmx";
    
    private static final String SZD_ZQY = "zqy";
    
    private static final String SJSKS = "sjsks";

    /**
     * 查询树列表
     * @param params 参数
     * @return 树列表
     */
    public List<TreeNode> getInterGroup(Map<String, Object> params) {
        List<TreeNode> list = new ArrayList<>();

        // 文件路径
        String stlDataDownFilePath = CacheUtil.getSysParam("STL_DATA_DOWN_FILE_PATH");

        List<String> dateDirList = FileUtil.getFileList(stlDataDownFilePath);
        if (StringUtil.isEmptyList(dateDirList)) {
            dateDirList = Collections.emptyList();
        }
        
        // 排序
		Collections.sort(dateDirList, new Comparator<String>() {
		       @Override
		       public int compare(String s1, String s2) {
		           return DateUtil.compareDate(s2, s1);
		       }
		  });
        
        List<String> nodeStrList = new ArrayList<>();
        String dateStr = (String)params.get("fileName");
        if (StringUtil.isNotEmpty(dateStr)) {
            for (String dateDir : dateDirList) {
                if (StringUtil.containsIgnoreCase(dateDir, dateStr)) {
                    nodeStrList.add(dateDir);
                }
            }
        } else {
            nodeStrList.addAll(dateDirList);
        }

        if (CollectionUtils.isNotEmpty(nodeStrList)) {
            for (String dateDir : nodeStrList) {
                generateTree(list, stlDataDownFilePath, dateDir);
            }
        }

        return list;
    }

    private void generateTree(List<TreeNode> list, String stlDataDownFilePath, String dateDir) {
    	
    	boolean isChecked = StringUtil.equals(dateDir, DateUtil.format(DateUtil.platDateFromCache(), DateUtil.DEFAULT_OTHER_FORMAT));
    	
        TreeNode pNode = new TreeNode();
        pNode.setName(dateDir);
        pNode.setId(dateDir);
        pNode.setChecked(isChecked);
        list.add(pNode);
        List<String> fileList = FileUtil.getFileList(stlDataDownFilePath + File.separator + dateDir);
        if (CollectionUtils.isNotEmpty(fileList)) {
            TreeNode szdNode = new TreeNode();
            szdNode.setName(SZD);
            szdNode.setId(SZD + GlobalConstant.SPLIT + pNode.getId());
            szdNode.setpId(pNode.getId());
            szdNode.setChecked(isChecked);
            szdNode.setOpen(isChecked);
            list.add(szdNode);

            TreeNode szeNode = new TreeNode();
            szeNode.setName(SZE);
            szeNode.setId(SZE + GlobalConstant.SPLIT + pNode.getId());
            szeNode.setpId(pNode.getId());
            szeNode.setChecked(isChecked);
            szeNode.setOpen(isChecked);
            list.add(szeNode);

            for (String fileName : fileList) {
                if (StringUtil.containsIgnoreCase(fileName, SZD_JSMX_02)
                        || StringUtil.containsIgnoreCase(fileName, SZD_JSMX_03)
                        || StringUtil.containsIgnoreCase(fileName, SZD_ZQBD)
                        || StringUtil.containsIgnoreCase(fileName, SZD_ABCSJ)
                        || StringUtil.containsIgnoreCase(fileName, SZD_ZQY)) {
                	TreeNode treeNode = new TreeNode();
                    treeNode.setName(fileName);
                    treeNode.setId(fileName + GlobalConstant.SPLIT + szdNode.getId());
                    treeNode.setpId(szdNode.getId());
                    treeNode.setChecked(isChecked);
                    treeNode.setOpen(isChecked);
                    list.add(treeNode);
                } else if (StringUtil.containsIgnoreCase(fileName, SJSMX0)
                        || StringUtil.containsIgnoreCase(fileName, SJSMX1)
                        || StringUtil.containsIgnoreCase(fileName, SJSFX)
                        || StringUtil.containsIgnoreCase(fileName, SJSJG)
                        || StringUtil.containsIgnoreCase(fileName, ZSMX)
                        || StringUtil.containsIgnoreCase(fileName, SJSKS)) {
                	TreeNode treeNode = new TreeNode();
                    treeNode.setName(fileName);
                    treeNode.setId(fileName + GlobalConstant.SPLIT + szeNode.getId());
                    treeNode.setpId(szeNode.getId());
                    treeNode.setChecked(isChecked);
                    treeNode.setOpen(isChecked);
                    list.add(treeNode);
                }
            }
        }
    }

    /**
     * 获取日志记录
     * @param params 查询参数
     * @return 分页日志
     */
    public Page<SettleTransResultDto> getInterActionLog(Map<String, Object> params) {
        if(isSingle) {
            return simpleRestTemplate.postForPage(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataImport001/findLogPage",
                    params, SettleTransResultDto.class);
        }
        else {
            return restTemplate.postForPage(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataImport001/findLogPage",
                    params, SettleTransResultDto.class);
        }

    }

    /**
     * 执行按钮操作
     * @param pageParam
     */
    public String execute(Map<String, Object> pageParam) {
        JsonObject json = new JsonObject();
        int isCheck = 0;

        // 选中的数据文件
        String datas = (String)pageParam.get("datas");

        String portfolioId = (String)pageParam.get("portfolioId");
        if (StringUtil.isEmpty(portfolioId)) {
            portfolioId = null;
        }
        List<SettleTransPara> settleTransParas = new ArrayList<>();

        if (StringUtil.isNotEmpty(datas)) {
            String[] dataArr = datas.split(GlobalConstant.COMMA);
            for (String data: dataArr) {
                String[] dataPart = data.split(GlobalConstant.SPLIT);
                if (dataPart.length != 3) {
                    continue;
                }
                SettleTransPara settleTransPara = new SettleTransPara();
                // 文件名
                settleTransPara.setDataFileName(dataPart[0]);
                // 导入日期
                settleTransPara.setImportDate(DateUtil.parse(dataPart[2]));
                // 组合代码
                settleTransPara.setPortfolioId(portfolioId);
                // 任务ID
                settleTransPara.setTaskId("50001");
                // seqno
                settleTransPara.setSeqno(IDGenerator.generate());
                settleTransParas.add(settleTransPara);
            }
            if (CollectionUtils.isNotEmpty(settleTransParas)) {
                Map<Date, List<SettleTransPara>> dateMap = settleTransParas.stream().collect(Collectors.groupingBy(SettleTransPara::getImportDate));
                Set<Date> importDates = dateMap.keySet();
                if (checkIsBatchRunning() != 0) {
                    json.addProperty("isCheck", isCheck);
                    json.addProperty("checkMsg", "正在进行批处理, 请稍后重试!");
                    return json.toString();
                }
                if(isSingle) {
                    simpleRestTemplate.postForObject(
                            ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataImport001/insertSettleTransParas",
                            settleTransParas, Integer.class);
                }
                else {
                    restTemplate.postForObject(
                            ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataImport001/insertSettleTransParas",
                            settleTransParas, Integer.class);
                }

                executeBatch(importDates);
            } else {
                json.addProperty("checkMsg", "请选择导入数据文件!");
                return json.toString();
            }
        }
        isCheck = 1;
        json.addProperty("isCheck", isCheck);
        return json.toString();
    }

    /**
     * 执行批处理
     * @param importDates
     */
    private void executeBatch(Set<Date> importDates) {
        for (Date importDate : importDates) {
            String selectedBatchGroup = "50001";
            String selectedProductGroup = "";
            String startDate = DateUtil.format(importDate, DateUtil.DEFAULT_DATE_FORMAT_EN);
            String endDate = DateUtil.format(importDate, DateUtil.DEFAULT_DATE_FORMAT_EN);
            String batchGroupType = GlobalConstant.BATCH_EXECUTE_TYPE_SINGLE;
            String prodGroupType = GlobalConstant.BATCH_EXECUTE_TYPE_GROUP;
            sysBatchStart001Service.executeBatch(selectedBatchGroup,
                    selectedProductGroup, startDate, endDate, batchGroupType, prodGroupType);
        }
    }

    /**
     * 检查批处理是否正在运行
     * @return
     */
    private int checkIsBatchRunning() {
        if(isSingle) {
            return simpleRestTemplate.postForObject(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataImport001/checkIsBatchRunning",
                    null, Integer.class);
        }
        else {
            return restTemplate.postForObject(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/SettleDataImport001/checkIsBatchRunning",
                    null, Integer.class);
        }

    }
}

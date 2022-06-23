package com.joyintech.tams.valuation.accounadjustmanage.checkcommon.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.JSchException;
import com.joyintech.framework.file.SafelyFile;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.util.*;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.dao.BokFileInfoMapper;
import com.joyintech.tams.common.base.dao.BokValbalTableFileMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.accounadjustmanage.balancetablegeneration.service.BalTableGenerate001Service;
import com.joyintech.tams.valuation.accounadjustmanage.checkcommon.dao.ExecuteMapper;
import com.joyintech.tams.valuation.accounadjustmanage.checkcommon.dto.*;
import com.joyintech.tams.valuation.accounadjustmanage.checkcommon.util.BigDecimalConverterUtilForValuareconcy;
import com.joyintech.tams.valuation.accounadjustmanage.checkcommon.util.ExcelUtilForValuareconcy;
import com.joyintech.tams.valuation.accounadjustmanage.valuationtablegeneration.dao.ValTableGenerate001Mapper;
import com.joyintech.tams.valuation.accounadjustmanage.valuationtablegeneration.dto.ExportFileDto;
import com.joyintech.tams.valuation.accounadjustmanage.valuationtablegeneration.service.ValTableGenerate001Service;
import com.joyintech.tams.valuation.accounadjustmanage.voucherquery.dto.ValAssetbook001Dto;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import com.joyintech.tams.valuation.common.utils.SftpTools;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.tools.util.ExcelCellValueFunction;
import com.joyintech.tools.util.ExcelTools;

/**
 * 对账服务类
 * 
 * @author yangjian
 * @version 1.0 2018年11月15日
 * @see ExecuteService
 * @since 1.0
 */
@Service
public class ExecuteService {

    /** 本系统标志 */
    private static final String MAP_FLAG_MY = "MY";

    /** 对方系统标志 */
    private static final String MAP_FLAG_OTH = "OTH";

    /** 重复科目代码后缀 */
    private static final String SUBJECT_SUFFIX = "-B";

    /**
     * 估值对账模板文件
     */
    private static final String VALUATION_TEMP_FILE = "COMPARE_ValuationReconciliation.xls";

    /**
     * 余额对账模板文件
     */
    private static final String BALANCE_TEMP_FILE = "COMPARE_BalanceReconciliation.xls";

    /**
     * 文件夹分隔符
     */
    private static final String SEPARATOR_NOW = "/";

    /**
     * executeMapper
     */
    @Autowired
    private ExecuteMapper executeMapper;

    /**
     * progressBarService
     */
    @Autowired
    private ProgressBarService progressBarService;

    /**
     * 文件信息表mapper
     */
    @Autowired
    BokFileInfoMapper bokFileInfoMapper;

    /**
     * 估值表余额表上传文件信息mapper
     */
    @Autowired
    BokValbalTableFileMapper bokValbalTableFileMapper;

    /**
     * ValTableGenerate001Mapper
     */
    @Autowired
    private ValTableGenerate001Mapper mapper;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 账套查询mapper
     */
    @Autowired
    private BokBooksetMapper bokBooksetMapper;

    /**
     * valTableGenerate001Service
     */
    @Autowired
    private ValTableGenerate001Service valTableGenerate001Service;

    /**
     * balTableGenerate001Service
     */
    @Autowired
    private BalTableGenerate001Service balTableGenerate001Service;

    /**
     * 标的类金融产品查询Mapper
     */
    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 执行对账
     * 
     * @param schemeType 对账类型
     * @return 结果
     * @throws Exception 错误
     * @since 1.0
     */
    public List<String> execute(List<ConfirmPaDto> paDtoList) throws IOException {
        // 错误信息
        List<String> errorMsgList = new ArrayList<String>();

        // 单对账处理
        for (ConfirmPaDto dto : paDtoList) {
            errorMsgList.addAll(this.executeOneProduct(dto));
        }

        return errorMsgList;
    }

    /**
     * 取消对账
     * 
     * @param schemeType 对账类型
     * @return 结果
     * @throws Exception 错误
     * @since 1.0
     */
    public void doCancle(List<ConfirmPaDto> paDtoList) {
        // 循环进行更新
        for (ConfirmPaDto dto : paDtoList) {
            BokValbalTableStatus bokValbalTableStatus = new BokValbalTableStatus();
            // 主键
            bokValbalTableStatus.setFinprodId(dto.getFinprodId());
            bokValbalTableStatus.setValDate(this.DateStringToDate(dto.getValDateString()));
            bokValbalTableStatus.setValBalFlag(dto.getValBalFlag());
            /// 状态更新
            bokValbalTableStatus.setCheckStatus(DictCodeList.对账状态_未对账.getValue());// 对账状态
            bokValbalTableStatus.setCheckResult(null);// 对账结果
            // 更新操作
            executeMapper.updateBokValbalTableStatusAndResult(bokValbalTableStatus);
        }
    }

    /**
     * 单产品对账处理
     * 
     * @param prdValManDto 产品估值
     * @return 产品估值
     * @throws Exception 错误
     * @since 1.0
     */
    private List<String> executeOneProduct(ConfirmPaDto confirmPaDto) throws IOException {

        // 错误信息
        List<String> errorMsgList = new ArrayList<String>();

        // 产品代码
        String prodId = confirmPaDto.getFinprodId();
        // 产品名称
        String prodName = confirmPaDto.getFinprodName();

        // 获取产品对应的账套代码
        String booksetIdNow = executeMapper.getBooksetIdByPrd(prodId);
        if (StringUtil.isEmpty(booksetIdNow)) {
            errorMsgList.add("产品：" + prodName + "(" + prodId + ") " + "，日期[" + confirmPaDto.getValDateString() + "],未找到产品所属账套。");
            return errorMsgList;
        }

        // 获取本方路径
        // 根据产品代码查询账套id
        ValAssetbook001Dto bookdto = this.getBooksetInfo(prodId);
        // 时间格式化
        Date valDatenow = this.StringToDate(confirmPaDto.getValDateString());
        // 在服务器上生成估值表或余额表，并且返回文件生成信息（针对本方估值表余额表文件）
        ExportFileDto exportFileDto = this.getMyFileInfo(bookdto.getBooksetId(), DateUtil.format(valDatenow, "yyyy-MM-dd"),
            confirmPaDto.getValBalFlag());
        String myFilePath = exportFileDto.getFilePath();

        // 获取对方文件路径信息
        Map<String, Object> otherFilePathPa = new HashMap<String, Object>();
        otherFilePathPa.put("finprodId", confirmPaDto.getFinprodId());// 产品代码
        otherFilePathPa.put("valDateString", confirmPaDto.getValDateString());// 估值日期
        otherFilePathPa.put("valBalFlag", confirmPaDto.getValBalFlag());// 估值表余额表标识
        otherFilePathPa.put("fileType", confirmPaDto.getOtherFileType());// 对方文件类型
        List<FileInfoDto> otherFilePathList = executeMapper.getFileInfoList(otherFilePathPa);
        // 获取对方文件信息
        FileInfoDto otherFileInfo = new FileInfoDto();
        if (StringUtil.isEmptyList(otherFilePathList)) {
            errorMsgList.add("产品：" + prodName + "(" + prodId + ") " + "，日期[" + confirmPaDto.getValDateString() + "], 未找到对应的"
                             + SysDataUtil.getNameFromDataDict(DictCode.文件类型, confirmPaDto.getOtherFileType()));
            return errorMsgList;
        }
        else {
            otherFileInfo = otherFilePathList.get(0);
        }

        // 获取该产品对应的方案信息
        ReconcileSchemeInfo prdSchemeInfo = this.getPrdSchemeInfo(prodId, confirmPaDto.getValBalFlag());
        // 若方案类型为空，则不进行对账，提示没有对应的对账方案
        if (prdSchemeInfo == null) {
            // 没有对应的对账方案
            errorMsgList.add("产品：" + prodName + "(" + prodId + ") " + "，日期[" + confirmPaDto.getValDateString() + "],未配置对账方案");
            return errorMsgList;
        }
        // 方案代码
        String schemeId = prdSchemeInfo.getSchemeId();

        // 先删除原对账结果，避免出现数据冲突。
        this.deleteOldCompareResult(prodId, schemeId, this.DateStringToDate(confirmPaDto.getValDateString()));

        // 读取本方文件和对方文件excel内容
        List<List<String>> myFileList = null;
        List<List<String>> othFileList = null;
        try {
            myFileList = this.readerFileContent(myFilePath, 0, prdSchemeInfo.getMyStartRowNo().intValue() - 1, false);
            othFileList = this.readerFileContent(otherFileInfo.getFilePath(), 0,
                prdSchemeInfo.getOthersideStartRowNo().intValue() - 1, true);

            // TODO：用于测试本方对方文件，后期删除
            // myFilePath = "OUTPUT/ZX/BF/20181115/本方文件.xls";
            // myFileList = this.readerFileContent(myFilePath, 0, prdSchemeInfo.getMyStartRowNo().intValue() - 1, true);
            // String otherFilePath = "OUTPUT/ZX/BF/20181115/他方文件.xls";
            // othFileList = this.readerFileContent(otherFilePath, 0, prdSchemeInfo.getOthersideStartRowNo().intValue() - 1, true);

            // 本方文件为空
            if (StringUtil.isEmptyList(myFileList)) {
                errorMsgList.add(
                    "产品：" + prodName + "(" + prodId + ") " + "，日期[" + confirmPaDto.getValDateString() + "]，本方文件未找到或为空。");
                return errorMsgList;
            }
            if (StringUtil.isEmptyList(othFileList)) {
                errorMsgList.add(
                    "产品：" + prodName + "(" + prodId + ") " + "，日期[" + confirmPaDto.getValDateString() + "]，对方文件未找到或为空。");
                return errorMsgList;
            }
        }
        catch (Exception e) {
            errorMsgList.add("产品：" + prodName + "(" + prodId + ") " + "，日期[" + confirmPaDto.getValDateString() + "],未找到文件");
            return errorMsgList;
        }

        // 结构映射
        List<ReconcileSchemeStructConfigDto> structList = executeMapper.getStructList(schemeId);
        // 检查数据是否为空
        if (structList == null || structList.isEmpty()) {
            // 没有对应的对账方案
            errorMsgList.add("对应方案：" + prdSchemeInfo.getSchemeName() + "(" + prdSchemeInfo.getSchemeId() + ") " + "未配置结构映射");
            return errorMsgList;
        }

        // 科目列结构映射MAP 本方列ID : 对方列ID
        Map<String, String> subjectStructMap = new LinkedHashMap<String, String>();
        // 汇总列Map 本方列ID : 对方列ID
        Map<String, String> totalStructMap = new LinkedHashMap<String, String>();
        // 处理结构映射
        this.handleStructList(subjectStructMap, totalStructMap, structList);

        // 列ID对应字符串（适用于本系统对比他系统）
        Map<String, String> colName2ColNoStructMap = null;
        colName2ColNoStructMap = structList.stream().collect(JoyinCollectors.toMap(x -> x.getMyColRemark(), x -> x.getMyColId()));

        String mySubjectIdColId = colName2ColNoStructMap.get("科目代码");
        int mySubjectIdColIndex = ExcelUtilForValuareconcy.columnToIndex(mySubjectIdColId);
        String othSubjectIdColId = subjectStructMap.get(mySubjectIdColId);
        int othSubjectIdColIndex = ExcelUtilForValuareconcy.columnToIndex(othSubjectIdColId);

        // 本系统数据Map<科目代码， Map<列号，数据>>
        Map<String, Map<String, String>> myFileDataMap = getFileDataMap(MAP_FLAG_MY, myFileList, structList, mySubjectIdColIndex);

        // 对方系统数据Map<科目代码， Map<列号，数据>>
        Map<String, Map<String, String>> othFileDataMap = getFileDataMap(MAP_FLAG_OTH, othFileList, structList,
            othSubjectIdColIndex);

        // 获取本方科目id集合
        List<String> mySubjectIdList = new ArrayList<>();
        if(myFileDataMap!=null)
        for (String keyString : myFileDataMap.keySet()) {
            mySubjectIdList.add(keyString);
        }

        // 获取他方科目id集合
        List<String> otherSubjectIdList = new ArrayList<String>();
        if(othFileDataMap!=null)
        for (String keyString : othFileDataMap.keySet()) {
            otherSubjectIdList.add(keyString);
        }

        // 科目映射
        List<ReconcileSchemeSubjectConfigDto> subjectList = executeMapper.getSubjectList(schemeId, booksetIdNow, mySubjectIdList,
            otherSubjectIdList);
        // 检查数据是否为空
        if (subjectList == null || subjectList.isEmpty()) {
            // 没有对应的对账方案
            errorMsgList.add("对应方案：" + prdSchemeInfo.getSchemeName() + "(" + prdSchemeInfo.getSchemeId() + ") " + "未配置科目映射");
            return errorMsgList;
        }

        // 本系统科目映射分组信息 : Map<本方科目代码 ， List<对方科目代码>>
        Map<String, List<String>> mySubjectGroupMap = new HashMap<String, List<String>>();
        // 对方系统科目映射分组信息 : Map<对方科目代码 ， List<本方科目代码>>
        Map<String, List<String>> othSubjectGroupMap = new HashMap<String, List<String>>();
        // 处理科目映射
        this.handleSubjectList(mySubjectGroupMap, othSubjectGroupMap, subjectList);

        // 《《【本系统对比对方系统】》》
        String reconcileStatusForMy = this.doCompare(booksetIdNow, confirmPaDto, prdSchemeInfo, subjectStructMap, totalStructMap,
            colName2ColNoStructMap, mySubjectGroupMap, othSubjectGroupMap, myFileDataMap, othFileDataMap,
            DictCodeListExt1.对比类型_本系统对比对方系统.getValue());

        // 获取他方对比机构映射
        Map<String, String> subjectStructMapForOther = this.mapKeyValueConvert(subjectStructMap);
        // 获取他方对比汇总结构映射
        Map<String, String> totalStructMapForOther = this.mapKeyValueConvert(totalStructMap);
        // 列ID对应字符串（适用于他系统对比本系统）
        Map<String, String> colName2ColNoStructMapForOther = structList.stream().collect(JoyinCollectors.toMap(
            x -> StringUtil.isEmpty(x.getOthersideColRemark()) ? x.getMyColRemark() : x.getOthersideColRemark(),
            x -> x.getOthersideColId()));

        // 《《【对方系统对比本系统】【注：若本方与他们结构映射表中，本方ID与他们ID不一致。导出的他方对比本方模版要以他方ID为主】》》
        String reconcileStatusForOther = this.doCompare(booksetIdNow, confirmPaDto, prdSchemeInfo, subjectStructMapForOther,
            totalStructMapForOther, colName2ColNoStructMapForOther, othSubjectGroupMap, mySubjectGroupMap, othFileDataMap,
            myFileDataMap, DictCodeListExt1.对比类型_对方系统对比本系统.getValue());

        // 生成对账结果excel文件(此方法从POC中【ProductValuationReconciliationShow.js - doCreate】 迁移过来。)
        errorMsgList.addAll(this.createCompareExcel(confirmPaDto));

        // 没有错误信息，给予状态修改。
        if (errorMsgList == null || errorMsgList.size() == 0) {

            // 获取对账结果,[本系统对比对方系统]与[对方系统对比本系统]任一对账结果为不一致，则对账不一致。
            String reconcileStatusLast = DictCodeList.对账结果_对账一致.getValue();
            if (DictCodeList.对账结果_对账不一致.getValue().equals(reconcileStatusForMy)
                || DictCodeList.对账结果_对账不一致.getValue().equals(reconcileStatusForOther)) {
                reconcileStatusLast = DictCodeList.对账结果_对账不一致.getValue();
            }

            // 根据主键， 更新【估值表余额表状态表】中的对账状态和对账结果。
            BokValbalTableStatus bokValbalTableStatus = new BokValbalTableStatus();
            // 主键
            bokValbalTableStatus.setFinprodId(confirmPaDto.getFinprodId());
            bokValbalTableStatus.setValDate(this.DateStringToDate(confirmPaDto.getValDateString()));
            bokValbalTableStatus.setValBalFlag(confirmPaDto.getValBalFlag());
            /// 状态更新
            bokValbalTableStatus.setCheckStatus(DictCodeList.对账状态_已对账.getValue());// 对账状态
            bokValbalTableStatus.setCheckResult(reconcileStatusLast);// 对账结果
            // 更新操作
            executeMapper.updateBokValbalTableStatusAndResult(bokValbalTableStatus);
        }

        // TODO:由于当前采用的是生成对账文件放在服务器上方案，故删除数据库存放的对比数据,避免无用数据。调试测试，可以注释该方法。
        this.deleteOldCompareResult(prodId, schemeId, this.DateStringToDate(confirmPaDto.getValDateString()));

        return errorMsgList;
    }

    /**
     * 从FTP路径中，读取excel内容
     * 
     * @param ftpPath ftp存放路径
     * @param sheetIndex 工作表索引(下标从0开始)
     * @param skipRows 跳过的表头(行数)
     * @param IsFromFtp 文件是否在ftp上（true是：在ftp服务器，false：在本地应用服务器）
     * @return List<List<String>>
     * @throws Exception
     * @see
     * @since 1.0
     */
    private List<List<String>> readerFileContent(String filePath, int sheetIndex, int skipRows, Boolean IsFromFtp) throws JSchException, IOException {
        // 读取本方文件和对方文件excel内容
        List<List<String>> contentList = null;

        // 路径中，["\"]替换为["/"]；
        String path = this.replaceStringForPath(filePath);

        // 若是从先从ftp中获取文件，经过改步骤
        if (IsFromFtp != null && IsFromFtp) {
            // 获取文件名
            String fileNameFromPath = path.substring(path.lastIndexOf("/") + 1);

            // 从ftp中拿取文件到服务器
            SftpTools sftpTools = new SftpTools();
            String remoteFile = path;// ftp存放路径（包含文件名称）
            String localdir = CacheUtil.getSysParam("EXPORT_VALBOKBANLANCE") + SEPARATOR_NOW + "COMPAREREADER";// 从ftp拿到该文件，放再服务器存放路径。

            try {
                String ip = CacheUtil.getSysParam("FILEUPLOAD_HOST");// 主机IP
                Integer port = Integer.parseInt(CacheUtil.getSysParam("FILEUPLOAD_PORT"));// 主机端口
                String username = CacheUtil.getSysParam("FILEUPLOAD_USERNAME");// 主机用户名
                String password = CacheUtil.getSysParam("FILEUPLOAD_PASSWORD");// 主机密码
                String sftpLoginType = CacheUtil.getSysParam("SFTP_LOGIN_TYPE");// SFTP登录方式

                sftpTools.login(username, password, ip, port, sftpLoginType);// sftp登录
                sftpTools.getFile(null, remoteFile, localdir);// 从ftp中拿到文件到服务器中
            }
            finally {
                sftpTools.close();// 关闭登录
            }

            // 获取存放在服务器中的路径
            path = localdir + SEPARATOR_NOW + fileNameFromPath;
        }

        // 文件下载到客户端
        File file = new SafelyFile(path);
        // 从ftp下载到服务器后，判断该文件是否存在
        if (file.isFile() && file.exists()) {
            // 读取excel文件转换为list
            try {
                contentList = ExcelUtilForValuareconcy.readToList(file, sheetIndex, skipRows);
            }
            finally {
                // 删除存放在服务器上的临时文件
                file.delete();
            }

        }

        // excel获取值，首尾空格去除。
        if (contentList != null && !contentList.isEmpty()) {
            // 去除首尾公共
            for (List<String> list : contentList) {
                for (int index = 0; index < list.size(); index++ ) {
                    String nowValue = list.get(index).trim();
                    list.set(index, nowValue);
                }
            }
        }

        return contentList;
    }

    /**
     * 获取产品方案信息
     * 
     * @param prodId 产品Id
     * @param valBalFlag 估值表余额表标识
     * @return 产品方案信息
     * @since 1.0
     */
    public ReconcileSchemeInfo getPrdSchemeInfo(String prodId, String valBalFlag) {

        // 获取方案类型
        String schemeType = null;
        if (DictCodeList.文件类型_估值表.getValue().equals(valBalFlag)) {
            schemeType = DictCodeListExt1.配置方案类型_估值表对账方案.getValue();

        }
        else if (DictCodeList.文件类型_余额表.getValue().equals(valBalFlag)) {
            schemeType = DictCodeListExt1.配置方案类型_余额表对账方案.getValue();

        }
        else {
            return null;
        }

        List<ReconcileSchemeInfo> prdSchemeInfoList = executeMapper.getSchemeInfo(prodId, schemeType);
        ReconcileSchemeInfo prdSchemeInfo = null;
        if (!StringUtil.isEmptyList(prdSchemeInfoList)) {
            prdSchemeInfo = prdSchemeInfoList.get(0);
        }

        // 若账套未挂方案，获取默认方案。
        if (prdSchemeInfo == null) {
            List<ReconcileSchemeInfo> prdSchemeInfoDefalutList = executeMapper.getSchemeInfoForDefault(prodId, schemeType);
            if (!StringUtil.isEmptyList(prdSchemeInfoDefalutList)) {
                prdSchemeInfo = prdSchemeInfoDefalutList.get(0);
            }
        }

        return prdSchemeInfo;
    }

    /**
     * 处理结构映射
     * 
     * @param subjectStructMap 科目行结构映射
     * @param totalStructMap 汇总行科目映射
     * @param structList 结构映射List
     * @since 1.0
     */
    private void handleStructList(Map<String, String> subjectStructMap, Map<String, String> totalStructMap,
                                  List<ReconcileSchemeStructConfigDto> structList) {
        if (StringUtil.isEmptyList(structList)) {
            return;
        }
        for (ReconcileSchemeStructConfigDto structDto : structList) {
            if (DictCodeListExt1.估值对账配置_区块类型_科目行.getValue().equals(structDto.getBlockType())) {
                subjectStructMap.put(structDto.getMyColId(), structDto.getOthersideColId());
            }
            else if (DictCodeListExt1.估值对账配置_区块类型_汇总行.getValue().equals(structDto.getBlockType())) {
                totalStructMap.put(structDto.getMyColId(), structDto.getOthersideColId());
            }
        }
    }

    /**
     * 处理科目映射
     * 
     * @param mySubjectGroupMap 本系统科目映射
     * @param othSubjectGroupMap 对方系统科目映射
     * @param subjectList 科目映射List
     * @since 1.0
     */
    private void handleSubjectList(Map<String, List<String>> mySubjectGroupMap, Map<String, List<String>> othSubjectGroupMap,
                                   List<ReconcileSchemeSubjectConfigDto> subjectList) {
        if (StringUtil.isEmptyList(subjectList)) {
            return;
        }
        for (ReconcileSchemeSubjectConfigDto subjectDto : subjectList) {
            String mySubjectId = subjectDto.getMySubjectId();
            String othSubjectId = subjectDto.getOthersideSubjectId();
            if (mySubjectGroupMap.containsKey(mySubjectId)) {
                mySubjectGroupMap.get(mySubjectId).add(othSubjectId);
            }
            else {
                List<String> othSubjectList = new ArrayList<String>();
                othSubjectList.add(othSubjectId);
                mySubjectGroupMap.put(mySubjectId, othSubjectList);
            }

            if (othSubjectGroupMap.containsKey(othSubjectId)) {
                othSubjectGroupMap.get(othSubjectId).add(mySubjectId);
            }
            else {
                List<String> mySubjectList = new ArrayList<String>();
                mySubjectList.add(mySubjectId);
                othSubjectGroupMap.put(othSubjectId, mySubjectList);
            }
        }

    }

    /**
     * 组装数据Map<科目号，Map<列号，列数据>>
     * 
     * @param myOrOthFlag 本方或对方系统标志
     * @param fileList 文件数据
     * @param structList 结构映射List
     * @param subjectIdColIndex 科目代码列号（数值）
     * @return 组装数据
     * @since 1.0
     */
    private Map<String, Map<String, String>> getFileDataMap(String myOrOthFlag, List<List<String>> fileList,
                                                            List<ReconcileSchemeStructConfigDto> structList,
                                                            int subjectIdColIndex) {
        LinkedHashMap<String, Map<String, String>> fileDataMap = null;
        try {

            fileDataMap = new LinkedHashMap<String, Map<String, String>>();

            for (List<String> rowDataList : fileList) {
                Map<String, String> rowDataMap = new LinkedHashMap<String, String>();
                for (int i = 0; i < rowDataList.size(); i++ ) {
                    rowDataMap.put(ExcelUtilForValuareconcy.indexToColumn(i + 1), rowDataList.get(i));
                }
                if (fileDataMap.containsKey(rowDataList.get(subjectIdColIndex - 1))) {
                    fileDataMap.put(rowDataList.get(subjectIdColIndex - 1) + SUBJECT_SUFFIX, rowDataMap);
                }
                else {
                    fileDataMap.put(rowDataList.get(subjectIdColIndex - 1), rowDataMap);
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

        return fileDataMap;
    }

    /**
     * 对账处理：对比类型_本系统对比对方系统
     * 
     * @param booksetId 当前账套代码
     * @param prdValManDto 产品信息
     * @param prdSchemeInfo 方案信息
     * @param subjectStructMap 科目行列Map
     * @param totalStructMap 汇总行列Map
     * @param colName2ColNoStructMap Map<列名,列号>
     * @param mySubjectGroupMap 主方科目数据
     * @param othSubjectGroupMap 对比方科目数据
     * @param myFileDataMap 主文件数据
     * @param othFileDataMap 对比文件数据
     * @param compareType 对比类型
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    private String doCompare(String booksetId, ConfirmPaDto prdValManDto, ReconcileSchemeInfo prdSchemeInfo,
                             Map<String, String> subjectStructMap, Map<String, String> totalStructMap,
                             Map<String, String> colName2ColNoStructMap, Map<String, List<String>> mySubjectGroupMap,
                             Map<String, List<String>> othSubjectGroupMap, Map<String, Map<String, String>> myFileDataMap,
                             Map<String, Map<String, String>> othFileDataMap, String compareType) {

        // 产品行对账结果
        List<ReconcileProductRowResult> rowResultList = new ArrayList<ReconcileProductRowResult>();
        // 产品列对账结果
        List<ReconcileProductColResult> colResultList = new ArrayList<ReconcileProductColResult>();

        // 记录对方所有数据，用于本方对比万后筛选后剩下的数据
        Map<String, Map<String, String>> othFileDataMapTemp = new LinkedHashMap<String, Map<String, String>>();
        othFileDataMapTemp.putAll(othFileDataMap);

        // 循环数据
        Long rowNo = 0L;
        // 单科目允许误差
        BigDecimal errorNumber = prdSchemeInfo.getErrorNumber() == null ? BigDecimal.ZERO : prdSchemeInfo.getErrorNumber();
        // 科目代码列
        String mySubjectIdColId = colName2ColNoStructMap.get("科目代码");
        // 科目名称列
        String subjectNameColId = colName2ColNoStructMap.get("科目名称");

        // 对比逻辑1/5步骤：从我方数据出发，进行一轮数据对比，包括【一对一】【多对一】【一对多】处理。================================================================
        Map<String, Object> compareFromMyDateStep = this.compareFromMyDateStep(mySubjectIdColId, subjectNameColId, errorNumber,
            rowNo, othFileDataMapTemp, booksetId, prdValManDto, prdSchemeInfo, subjectStructMap, totalStructMap,
            colName2ColNoStructMap, mySubjectGroupMap, othSubjectGroupMap, myFileDataMap, othFileDataMapTemp, compareType);
        String reconcileStatusForcompareFromMyDateStep = (String)compareFromMyDateStep.get("reconcileStatus");// 对账结果
        rowNo = (Long)compareFromMyDateStep.get("rowNo");// 我方excel数据序号
        rowResultList.addAll((List<ReconcileProductRowResult>)compareFromMyDateStep.get("rowResultList"));// 入库行对比数据
        colResultList.addAll((List<ReconcileProductColResult>)compareFromMyDateStep.get("colResultList"));// 入库对比列数据
        List<SurplusDataDto> surplusDataDtoList = (List<SurplusDataDto>)compareFromMyDateStep.get("surplusDataDtoList");// 获取我方数据一轮对比后剩余的数据

        // 对比逻辑2/5步骤：从我方数据出发，进行二轮数据对比，科目性质的【多对多】处理。===========================================================================
        Map<String, Object> compareFromManyToManyStep = this.compareFromManyToManyStep(rowResultList, colResultList,
            surplusDataDtoList, mySubjectIdColId, subjectNameColId, errorNumber, rowNo, othFileDataMapTemp, booksetId,
            prdValManDto, prdSchemeInfo, subjectStructMap, totalStructMap, colName2ColNoStructMap, mySubjectGroupMap,
            othSubjectGroupMap, myFileDataMap, othFileDataMapTemp, compareType);
        String reconcileStatusForcompareFromManyToManyStep = (String)compareFromManyToManyStep.get("reconcileStatus");// 对账结果
        rowNo = (Long)compareFromManyToManyStep.get("rowNo");// 我方excel数据序号
        List<SurplusDataDto> noConfigDataListForMany = (List<SurplusDataDto>)compareFromManyToManyStep.get(
            "noConfigDataListForMany");// 多对多过滤后剩余的数据

        // 对比逻辑3/5步骤：我方对比他方，执行向上汇总处理(只支持我方系统对比他方系统)。===========================================================================
        Map<String, Object> compareFromMergeUpStep = this.compareFromMergeUpStep(rowResultList, colResultList,
            noConfigDataListForMany, mySubjectIdColId, subjectNameColId, errorNumber, rowNo, othFileDataMapTemp, booksetId,
            prdValManDto, prdSchemeInfo, subjectStructMap, totalStructMap, colName2ColNoStructMap, mySubjectGroupMap,
            othSubjectGroupMap, myFileDataMap, othFileDataMapTemp, compareType);
        String reconcileStatusForcompareFromMergeUpStep = (String)compareFromMergeUpStep.get("reconcileStatus");// 对账结果
        rowNo = (Long)compareFromMergeUpStep.get("rowNo");// 我方excel数据序号
        List<SurplusDataDto> noConfigDataListForMergeUp = (List<SurplusDataDto>)compareFromMergeUpStep.get(
            "noConfigDataListForMergeUp");// 多对多过滤后剩余的数据

        // 对比逻辑4/5步骤：剩余数据【我方excel剩余数据】和【对方excel剩余数据】处理。===========================================================================
        Map<String, Object> compareSurplusDataStep = this.compareSurplusDataStep(noConfigDataListForMergeUp, mySubjectIdColId,
            subjectNameColId, errorNumber, rowNo, othFileDataMapTemp, booksetId, prdValManDto, prdSchemeInfo, subjectStructMap,
            totalStructMap, colName2ColNoStructMap, mySubjectGroupMap, othSubjectGroupMap, myFileDataMap, othFileDataMapTemp,
            compareType);
        String reconcileStatusForcompareSurplusDataStep = (String)compareSurplusDataStep.get("reconcileStatus");// 对账结果
        rowNo = (Long)compareSurplusDataStep.get("rowNo");// 我方excel数据序号
        rowResultList.addAll((List<ReconcileProductRowResult>)compareSurplusDataStep.get("rowResultList"));// 入库行对比数据
        colResultList.addAll((List<ReconcileProductColResult>)compareSurplusDataStep.get("colResultList"));// 入库对比列数据

        // 对比逻辑5/5步骤：对比结果数据入库。=================================================================================================================
        this.insertDbForCompareDate(rowResultList, colResultList);

        // 对账状态
        String reconcileStatus = DictCodeList.对账结果_对账一致.getValue();
        if (DictCodeList.对账结果_对账不一致.getValue().equals(reconcileStatusForcompareFromMyDateStep)
            || DictCodeList.对账结果_对账不一致.getValue().equals(reconcileStatusForcompareFromManyToManyStep)
            || DictCodeList.对账结果_对账不一致.getValue().equals(reconcileStatusForcompareFromMergeUpStep)
            || DictCodeList.对账结果_对账不一致.getValue().equals(reconcileStatusForcompareSurplusDataStep)) {
            reconcileStatus = DictCodeList.对账结果_对账不一致.getValue();
        }

        // 返回对比结果
        return reconcileStatus;
    }

    /**
     * 对账逻辑方法模块化 ：从我方数据出发，进行一轮数据对比，包括【一对一】【多对一】【一对多】处理。
     * 
     * @param mySubjectIdColId 科目代码列
     * @param subjectNameColId 科目名称列
     * @param errorNumber 单科目允许误差
     * @param rowNo 我方excel数据序号
     * @param othFileDataMapTemp
     * @param booksetId 当前账套代码
     * @param prdValManDto 产品信息
     * @param prdSchemeInfo 方案信息
     * @param subjectStructMap 科目行列Map
     * @param totalStructMap 汇总行列Map
     * @param colName2ColNoStructMap Map<列名,列号>
     * @param mySubjectGroupMap 主方科目数据
     * @param othSubjectGroupMap 对比方科目数据
     * @param myFileDataMap 主文件数据
     * @param othFileDataMap 对比文件数据
     * @param compareType 对比类型
     * @return
     * @see
     * @since 1.0
     */
    private Map<String, Object> compareFromMyDateStep(String mySubjectIdColId, String subjectNameColId, BigDecimal errorNumber,
                                                      Long rowNo, Map<String, Map<String, String>> othFileDataMapTemp,
                                                      String booksetId, ConfirmPaDto prdValManDto,
                                                      ReconcileSchemeInfo prdSchemeInfo, Map<String, String> subjectStructMap,
                                                      Map<String, String> totalStructMap,
                                                      Map<String, String> colName2ColNoStructMap,
                                                      Map<String, List<String>> mySubjectGroupMap,
                                                      Map<String, List<String>> othSubjectGroupMap,
                                                      Map<String, Map<String, String>> myFileDataMap,
                                                      Map<String, Map<String, String>> othFileDataMap, String compareType) {

        // 对账状态
        String reconcileStatus = DictCodeList.对账结果_对账一致.getValue();
        // 产品代码
        String prodId = prdValManDto.getFinprodId();
        // 方案代码
        String schemeId = prdSchemeInfo.getSchemeId();

        // 产品行对账结果
        List<ReconcileProductRowResult> rowResultList = new ArrayList<ReconcileProductRowResult>();
        // 产品列对账结果
        List<ReconcileProductColResult> colResultList = new ArrayList<ReconcileProductColResult>();

        // 本系统对比对方系统
        List<String> more2OneSubjectIdList = new ArrayList<String>();

        // 获取我方数据一轮对比后剩余的数据
        List<SurplusDataDto> surplusDataDtoList = new ArrayList<SurplusDataDto>();

        // 从本方excel数据出发，根据【本方科目映射关系】关系，进行数据对比
        for (Entry<String, Map<String, String>> entry : myFileDataMap.entrySet()) {
            // 获取本方excel数据中的科目id
            String mySubjectId = entry.getKey();
            if (more2OneSubjectIdList.contains(mySubjectId)) {
                continue;
            }

            // 根据【本方excel数据中的科目id】，从【本方科目映射关系】中，获取【映射关系数据】list
            boolean addSuffix = false;
            List<String> othSubjectList = mySubjectGroupMap.get(mySubjectId);
            if (othSubjectList == null && mySubjectId.endsWith(SUBJECT_SUFFIX)) {
                mySubjectId = mySubjectId.replace(SUBJECT_SUFFIX, "");
                othSubjectList = mySubjectGroupMap.get(mySubjectId);
                addSuffix = true;
            }

            // 分情况处理
            // 以【本方excel数据中的科目id】从【本方科目映射关系】中，关联的【映射关系数据】只有一个，即【本方一对一，或本方多对一】
            if (othSubjectList != null && othSubjectList.size() == 1) {
                String othSubjectId = othSubjectList.get(0);
                String othSubjectIdB = othSubjectId;
                if (addSuffix) {
                    othSubjectIdB = othSubjectId + SUBJECT_SUFFIX;
                }
                List<String> mySubjectList = othSubjectGroupMap.get(othSubjectId);
                // 【一对一】
                if (mySubjectList != null && mySubjectList.size() == 1) {
                    rowNo++ ;
                    /** 一对一 */
                    // 本系统行数据
                    Map<String, String> myRowDataMap = entry.getValue();
                    // myFileDataMapTemp.remove(entry.getKey());
                    // 对方系统行数据
                    Map<String, String> othRowDataMap = othFileDataMap.get(othSubjectIdB);
                    othFileDataMapTemp.remove(othSubjectIdB);

                    ReconcileProductRowResult rowResult = new ReconcileProductRowResult();
                    String compareId = UUID.randomUUID().toString().replaceAll("-", "");
                    rowResult.setCompareId(compareId);
                    rowResult.setProdId(prodId);
                    rowResult.setSchemeId(schemeId);
                    rowResult.setCompareDate(this.DateStringToDate(prdValManDto.getValDateString()));
                    rowResult.setCompareType(compareType);
                    rowResult.setCompareRowno(rowNo);
                    rowResult.setMySubjectId(mySubjectId);
                    rowResult.setOthersideSubjectId(othRowDataMap == null ? null : othSubjectId);
                    rowResultList.add(rowResult);

                    Map<String, String> structMap = this.hasChinese(mySubjectId) ? totalStructMap : subjectStructMap;
                    for (Entry<String, String> structEntry : structMap.entrySet()) {
                        String myColId = structEntry.getKey();
                        String myData = removeStrFormat(myRowDataMap.get(myColId));
                        myData = removeStrFormat(myData);
                        String othColId = structEntry.getValue();
                        String othData = removeStrFormat(othRowDataMap == null ? null : othRowDataMap.get(othColId));
                        if (mySubjectIdColId.equals(myColId)) {
                            continue;
                        }
                        if (subjectNameColId.equals(myColId)) {
                            if (StringUtil.isEmpty(rowResult.getMySubjectName())) {
                                rowResult.setMySubjectName(myData);
                            }
                            if (StringUtil.isEmpty(rowResult.getOthersideSubjectName())) {
                                rowResult.setOthersideSubjectName(othData);
                            }
                            // 非汇总行，不进行科目对比，汇总行支持科目对比
                            if (!this.hasChinese(mySubjectId)) {// 科目行情况
                                continue;
                            }
                        }

                        ReconcileProductColResult colResult = new ReconcileProductColResult();
                        BigDecimal myDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(myData);
                        BigDecimal othDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(othData);
                        BigDecimal difference = ArithmeticUtil.doSub(myDataNum, othDataNum);

                        if (difference.abs().compareTo(errorNumber) > 0) {
                            // 对账不一致
                            reconcileStatus = DictCodeList.对账结果_对账不一致.getValue();
                        }
                        colResult.setCompareId(compareId);
                        colResult.setCompareCol(structEntry.getKey());
                        colResult.setMyData(myData);
                        colResult.setOthersideData(othData);
                        colResult.setNeedcompare(this.getNeedcompare(difference));
                        if (StringUtil.isNotEmpty(myData) || StringUtil.isNotEmpty(othData)) {
                            colResult.setDifference(difference.toString());
                        }
                        colResultList.add(colResult);
                    }

                }
                // 【多对一】
                if (mySubjectList != null && mySubjectList.size() > 1) {
                    /** 多对一 */
                    List<Map<String, String>> myRowDataMapList = new ArrayList<Map<String, String>>();

                    // 合计行数据
                    Map<String, String> totalMyRowData = new HashMap<String, String>();

                    for (String more2OneSubjectId : mySubjectList) {
                        more2OneSubjectIdList.add(more2OneSubjectId);
                        Map<String, String> myRowData = myFileDataMap.get(more2OneSubjectId);
                        // myFileDataMapTemp.remove(more2OneSubjectId);

                        if (this.isNotEmptyMap(myRowData)) {
                            // 合计map ,如果为空，直接将第一条put，否则累计
                            if (this.isEmptyMap(totalMyRowData)) {
                                totalMyRowData.putAll(myRowData);
                            }
                            else {
                                // 累加合计行数据
                                for (Entry<String, String> totalEntry : totalMyRowData.entrySet()) {
                                    BigDecimal myDataValue = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(
                                        removeStrFormat(myRowData.get(totalEntry.getKey())));
                                    BigDecimal totalValue = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(
                                        totalEntry.getValue()).add(myDataValue);
                                    totalMyRowData.put(totalEntry.getKey(), totalValue.toString());
                                }
                            }
                            myRowDataMapList.add(myRowData);
                        }
                    }

                    // 对方系统行数据
                    Map<String, String> othRowDataMap = othFileDataMap.get(othSubjectId);
                    othFileDataMapTemp.remove(othSubjectId);

                    int rowIndex = 0;
                    for (Map<String, String> myRowDataMap : myRowDataMapList) {
                        rowNo++ ;
                        rowIndex++ ;

                        String m2OnemySubjectId = myRowDataMap.get(mySubjectIdColId);
                        ReconcileProductRowResult rowResult = new ReconcileProductRowResult();
                        String compareId = UUID.randomUUID().toString().replaceAll("-", "");
                        rowResult.setCompareId(compareId);
                        rowResult.setProdId(prodId);
                        rowResult.setSchemeId(schemeId);
                        rowResult.setCompareDate(this.DateStringToDate(prdValManDto.getValDateString()));
                        rowResult.setCompareType(compareType);
                        rowResult.setCompareRowno(rowNo);
                        rowResult.setMySubjectId(m2OnemySubjectId);
                        rowResult.setOthersideSubjectId(othRowDataMap == null ? null : othSubjectId);
                        rowResultList.add(rowResult);

                        Map<String, String> structMap = this.hasChinese(mySubjectId) ? totalStructMap : subjectStructMap;
                        for (Entry<String, String> structEntry : structMap.entrySet()) {
                            String myColId = structEntry.getKey();
                            // String myData = totalMyRowData.get(myColId);
                            String myData = myRowDataMap.get(myColId);
                            myData = removeStrFormat(myData);
                            String othColId = structEntry.getValue();
                            String othData = removeStrFormat(othRowDataMap == null ? null : othRowDataMap.get(othColId));
                            if (mySubjectIdColId.equals(myColId)) {
                                continue;
                            }
                            if (subjectNameColId.equals(myColId)) {
                                if (StringUtil.isEmpty(rowResult.getMySubjectName())) {
                                    rowResult.setMySubjectName(myData);
                                }
                                if (StringUtil.isEmpty(rowResult.getOthersideSubjectName())) {
                                    rowResult.setOthersideSubjectName(othData);
                                }
                                // 非汇总行，不进行科目对比，汇总行支持科目对比
                                if (!this.hasChinese(mySubjectId)) {// 科目行情况
                                    continue;
                                }
                            }
                            ReconcileProductColResult colResult = new ReconcileProductColResult();
                            // BigDecimal myDataNum =
                            // BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(myData);
                            BigDecimal myDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(
                                totalMyRowData.get(myColId));
                            BigDecimal othDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(othData);
                            BigDecimal difference = ArithmeticUtil.doSub(myDataNum, othDataNum);

                            if (difference.abs().compareTo(errorNumber) > 0) {
                                // 对账不一致
                                reconcileStatus = DictCodeList.对账结果_对账不一致.getValue();
                            }

                            colResult.setCompareId(compareId);
                            colResult.setCompareCol(structEntry.getKey());
                            colResult.setMyData(myData);
                            colResult.setNeedcompare(this.getNeedcompare(difference));
                            // 多对一时，第一条显示差异，其他的显示“#”
                            if (StringUtil.isNotEmpty(myData) || StringUtil.isNotEmpty(othData)) {
                                colResult.setDifference(rowIndex == 1 ? difference.toString() : "#");
                                colResult.setOthersideData(rowIndex == 1 ? othData : "#");
                            }
                            colResultList.add(colResult);

                        }
                    }

                }
            }
            // 以【本方excel数据中的科目id】从【本方科目映射关系】中，关联的【映射关系数据】有多个，即【本方一对多】
            else if (othSubjectList != null && othSubjectList.size() > 1) {
                /** 一对多 */
                Map<String, String> myRowDataMap = entry.getValue();
                // myFileDataMapTemp.remove(entry.getKey());

                Map<String, String> totalOthRowData = new HashMap<String, String>();
                List<ReconcileProductRowResult> detailRowResultList = new ArrayList<ReconcileProductRowResult>();
                Long totalRowNo = ++rowNo;
                String totalCompareId = UUID.randomUUID().toString().replaceAll("-", "");
                for (String othSubjectId : othSubjectList) {
                    rowNo++ ;
                    Map<String, String> othRowDataMap = othFileDataMap.get(othSubjectId);
                    othFileDataMapTemp.remove(othSubjectId);

                    if (this.isNotEmptyMap(othRowDataMap)) { // 合计map
                                                             // ,如果为空，直接将第一条put，否则累计
                        if (this.isEmptyMap(totalOthRowData)) {
                            totalOthRowData.putAll(othRowDataMap);
                        }
                        else {
                            for (Entry<String, String> totalEntry : totalOthRowData.entrySet()) {
                                BigDecimal totalValue = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(
                                    totalEntry.getValue()).add(
                                        BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(
                                            othRowDataMap.get(totalEntry.getKey())));
                                totalOthRowData.put(totalEntry.getKey(), totalValue.toString());
                            }
                        }

                        ReconcileProductRowResult detailRowResult = new ReconcileProductRowResult();
                        String compareId = UUID.randomUUID().toString().replaceAll("-", "");
                        detailRowResult.setCompareId(compareId);
                        detailRowResult.setProdId(prodId);
                        detailRowResult.setSchemeId(schemeId);
                        detailRowResult.setCompareDate(this.DateStringToDate(prdValManDto.getValDateString()));
                        detailRowResult.setCompareType(compareType);
                        detailRowResult.setCompareRowno(rowNo);
                        // rowResult.setMySubjectId(mySubjectId);
                        detailRowResult.setOthersideSubjectId(othSubjectId);
                        detailRowResultList.add(detailRowResult);

                        Map<String, String> structMap = this.hasChinese(othSubjectId) ? totalStructMap : subjectStructMap;
                        for (Entry<String, String> structEntry : structMap.entrySet()) {
                            String myColId = structEntry.getKey();
                            String myData = myRowDataMap.get(myColId);
                            myData = removeStrFormat(myData);
                            String othColId = structEntry.getValue();
                            String othData = removeStrFormat(othRowDataMap.get(othColId));
                            if (mySubjectIdColId.equals(myColId)) {
                                continue;
                            }
                            if (subjectNameColId.equals(myColId)) {
                                if (StringUtil.isEmpty(detailRowResult.getMySubjectName())) {
                                    detailRowResult.setMySubjectName(myData);
                                }
                                if (StringUtil.isEmpty(detailRowResult.getOthersideSubjectName())) {
                                    detailRowResult.setOthersideSubjectName(othData);
                                }
                                // 非汇总行，不进行科目对比，汇总行支持科目对比
                                if (!this.hasChinese(mySubjectId)) {// 科目行情况
                                    continue;
                                }
                            }
                            ReconcileProductColResult colResult = new ReconcileProductColResult();
                            // 改为在合计中进行对比
                            // BigDecimal myDataNum =
                            // BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(myData);
                            // BigDecimal othDataNum =
                            // BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(othData);
                            // BigDecimal difference =
                            // ArithmeticUtil.doSub(myDataNum, othDataNum);
                            //
                            // if (difference.abs().compareTo(errorNumber) > 0)
                            // {
                            // // 对账不一致
                            // reconcileStatus =
                            // DictCodeList.对账结果_对账不一致.getValue();
                            // }

                            BigDecimal difference = BigDecimal.ZERO;

                            colResult.setCompareId(compareId);
                            colResult.setCompareCol(structEntry.getKey());
                            // colResult.setMyData(myData);
                            colResult.setOthersideData(othData);
                            colResult.setNeedcompare(this.getNeedcompare(difference));// 多对一暂时不用对比，在合计中进行对比
                            colResult.setDifference(difference.toString());
                            colResultList.add(colResult);
                        }

                    }
                }
                // 合计行
                ReconcileProductRowResult totalRowResult = new ReconcileProductRowResult();

                totalRowResult.setCompareId(totalCompareId);
                totalRowResult.setProdId(prodId);
                totalRowResult.setSchemeId(schemeId);
                totalRowResult.setCompareDate(this.DateStringToDate(prdValManDto.getValDateString()));
                totalRowResult.setCompareType(compareType);
                totalRowResult.setCompareRowno(totalRowNo);
                totalRowResult.setMySubjectId(mySubjectId);
                totalRowResult.setOthersideSubjectId("合计");

                rowResultList.add(totalRowResult);
                rowResultList.addAll(detailRowResultList);

                Map<String, String> structMap = this.hasChinese(mySubjectId) ? totalStructMap : subjectStructMap;
                for (Entry<String, String> structEntry : structMap.entrySet()) {
                    String myColId = structEntry.getKey();
                    if (mySubjectIdColId.equals(myColId)) {
                        continue;
                    }

                    ReconcileProductColResult colResult = new ReconcileProductColResult();
                    String othColId = structEntry.getValue();
                    String myData = removeStrFormat(myRowDataMap.get(myColId));
                    String othData = removeStrFormat(totalOthRowData.get(othColId));

                    if (subjectNameColId.equals(myColId)) {
                        if (StringUtil.isEmpty(totalRowResult.getMySubjectName())) {
                            totalRowResult.setMySubjectName(myData);
                        }
                        // 非汇总行，不进行科目对比，汇总行支持科目对比
                        if (!this.hasChinese(mySubjectId)) {// 科目行情况
                            continue;
                        }
                    }

                    BigDecimal myDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(myData);
                    BigDecimal othDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(othData);
                    BigDecimal difference = ArithmeticUtil.doSub(myDataNum, othDataNum);

                    if (difference.abs().compareTo(errorNumber) > 0) {
                        // 对账不一致
                        reconcileStatus = DictCodeList.对账结果_对账不一致.getValue();
                    }

                    colResult.setCompareId(totalCompareId);
                    colResult.setCompareCol(structEntry.getKey());
                    colResult.setMyData(myData);
                    colResult.setOthersideData(othData);
                    colResult.setNeedcompare(this.getNeedcompare(difference));
                    if (StringUtil.isNotEmpty(myData) || StringUtil.isNotEmpty(othData)) {
                        colResult.setDifference(difference.toString());
                    }
                    colResultList.add(colResult);

                }
            }
            // 记录本方剩余的数据，用于后续逻辑判断，即再过滤一篇是不是多对多。
            else {
                // 累加序号
                rowNo++ ;
                // 记录多余的数据
                SurplusDataDto surplusDataDtoForMy = new SurplusDataDto();
                surplusDataDtoForMy.setSubjectId(mySubjectId);
                surplusDataDtoForMy.setRowNo(rowNo);
                surplusDataDtoForMy.setExcelData(entry.getValue());

                // 添加到一轮对比后的数据，用于后续逻辑判断
                surplusDataDtoList.add(surplusDataDtoForMy);
            }
        }

        // 定义返回值
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("reconcileStatus", reconcileStatus);// 对账结果
        returnMap.put("rowNo", rowNo);// 我方excel数据序号
        returnMap.put("rowResultList", rowResultList);// 入库行对比数据
        returnMap.put("colResultList", colResultList);// 入库对比列数据

        returnMap.put("surplusDataDtoList", surplusDataDtoList);// 我方数据对比后剩余的数据

        return returnMap;
    }

    /**
     * 对账逻辑方法模块化 ：从我方数据出发，进行二轮数据对比，科目性质的【多对多】处理。
     * 
     * @param rowResultList 已经对比结束的行数据
     * @param colResultList 已经对比结束的列数据
     * @param surplusDataDtoList 一轮对比后剩余的我方数据
     * @param mySubjectIdColId 科目代码列
     * @param subjectNameColId 科目名称列
     * @param errorNumber 单科目允许误差
     * @param rowNo 我方excel数据序号
     * @param othFileDataMapTemp
     * @param booksetId 当前账套代码
     * @param prdValManDto 产品信息
     * @param prdSchemeInfo 方案信息
     * @param subjectStructMap 科目行列Map
     * @param totalStructMap 汇总行列Map
     * @param colName2ColNoStructMap Map<列名,列号>
     * @param mySubjectGroupMap 主方科目数据
     * @param othSubjectGroupMap 对比方科目数据
     * @param myFileDataMap 主文件数据
     * @param othFileDataMap 对比文件数据
     * @param compareType 对比类型
     * @return
     * @see
     * @since 1.0
     */
    private Map<String, Object> compareFromManyToManyStep(List<ReconcileProductRowResult> rowResultList,
                                                          List<ReconcileProductColResult> colResultList,
                                                          List<SurplusDataDto> surplusDataDtoList, String mySubjectIdColId,
                                                          String subjectNameColId, BigDecimal errorNumber, Long rowNo,
                                                          Map<String, Map<String, String>> othFileDataMapTemp, String booksetId,
                                                          ConfirmPaDto prdValManDto, ReconcileSchemeInfo prdSchemeInfo,
                                                          Map<String, String> subjectStructMap,
                                                          Map<String, String> totalStructMap,
                                                          Map<String, String> colName2ColNoStructMap,
                                                          Map<String, List<String>> mySubjectGroupMap,
                                                          Map<String, List<String>> othSubjectGroupMap,
                                                          Map<String, Map<String, String>> myFileDataMap,
                                                          Map<String, Map<String, String>> othFileDataMap, String compareType) {

        // 对账状态
        String reconcileStatus = DictCodeList.对账结果_对账一致.getValue();
        // 产品代码
        String prodId = prdValManDto.getFinprodId();
        // 方案代码
        String schemeId = prdSchemeInfo.getSchemeId();

        // 定义多对多过滤后剩余的数据
        List<SurplusDataDto> noConfigDataListForMy = new ArrayList<SurplusDataDto>();

        // 【多对多逻辑处理】：1.【前序数据准备处理】***********************************************************
        // 定义无任何映射关系的我方数据
        List<SurplusDataDto> noConfigDataList = new ArrayList<SurplusDataDto>();
        // 定义多对多数据【即我方excel存在性质映射的三级科目和四级科目】
        List<SurplusDataDto> manyToManyList = new ArrayList<SurplusDataDto>();
        // 记录存在对方性质映射四级数据的我方3级科目(支持去重)。
        Set<String> haveNatureDateThreeSubject = new HashSet<String>();

        // 获取本方excel剩余的科目id
        List<String> mySupSubjectIdList = new ArrayList<String>();
        for (SurplusDataDto myDate : surplusDataDtoList) {
            mySupSubjectIdList.add(myDate.getSubjectId());
        }

        // 获取对方excel剩余的科目id
        List<String> otherSupSubjectIdList = new ArrayList<String>();
        for (String key : othFileDataMapTemp.keySet()) {
            otherSupSubjectIdList.add(key);
        }

        // 获取科目性质表对应的我方所有科目组合信息
        List<NatureSubjectInfo> subjectInfoInSchemeNatureForMy = new ArrayList<NatureSubjectInfo>();
        // 获取科目性质表对应的他方所有科目组合信息
        List<NatureSubjectInfo> subjectInfoInSchemeNatureForOther = new ArrayList<NatureSubjectInfo>();

        // 区分【我方对比他方多对多】和【他方对比我方多对多】
        if (DictCodeListExt1.对比类型_本系统对比对方系统.getValue().equals(compareType)) {
            // 科目性质表对应的我方所有科目组合信息
            if (mySupSubjectIdList != null && !mySupSubjectIdList.isEmpty()) {
                subjectInfoInSchemeNatureForMy = executeMapper.getMySubjectInfoInSchemeNature(schemeId, booksetId,
                    mySupSubjectIdList);
            }
            // 科目性质表对应的他方所有科目组合信息
            if (otherSupSubjectIdList != null && !otherSupSubjectIdList.isEmpty()) {
                subjectInfoInSchemeNatureForOther = executeMapper.getOtherSubjectInfoInSchemeNature(schemeId, booksetId,
                    otherSupSubjectIdList);
            }
        }
        else {
            // 科目性质表对应的我方所有科目组合信息
            if (mySupSubjectIdList != null && !mySupSubjectIdList.isEmpty()) {
                subjectInfoInSchemeNatureForMy = executeMapper.getOtherSubjectInfoInSchemeNature(schemeId, booksetId,
                    mySupSubjectIdList);
            }
            // 科目性质表对应的他方所有科目组合信息
            if (otherSupSubjectIdList != null && !otherSupSubjectIdList.isEmpty()) {
                subjectInfoInSchemeNatureForOther = executeMapper.getMySubjectInfoInSchemeNature(schemeId, booksetId,
                    otherSupSubjectIdList);
            }
        }

        // 循环剩余数据，进行逻辑处理
        for (SurplusDataDto myDate : surplusDataDtoList) {

            // 定义是是否为多对多数据
            Boolean isManyToMany = false;

            // 过滤存在于科目性质表的我方数据，即多对多数据
            for (NatureSubjectInfo dto : subjectInfoInSchemeNatureForMy) {
                if (myDate.getSubjectId().equals(dto.getSubjectId())) {
                    // 记录存在映射的数据
                    myDate.setThreeSubject(dto.getThreeSubject());// 记录当前对应3级科目
                    myDate.setMyAssetId(dto.getMyAssetId());// 本方资产
                    myDate.setMappingAssetId(dto.getMappingAssetId());// 对应的映射资产
                    myDate.setNature(dto.getNature());// 记录当前科目性质

                    // 是否为多对多数据
                    isManyToMany = true;

                    // 跳出判断循环
                    break;
                }
            }

            // 为多对多数据
            if (isManyToMany) {
                // 添加到多对多数据中【即我方excel存在性质映射的三级科目和四级科目】
                manyToManyList.add(myDate);
            }
            // 非多对多数据，即无任何映射数据
            else {
                // 添加到无任何映射关系的我方数据
                noConfigDataList.add(myDate);
            }
        }

        // 【多对多逻辑处理】：2.【先进行我方四级科目处理】************************************************************
        for (SurplusDataDto myDataDto : manyToManyList) {
            // 获取当前科目
            String subjectIdNow = myDataDto.getSubjectId();
            // 获取当前三级科目
            String threeSubjectIdNow = myDataDto.getThreeSubject();
            // 获取当前本方资产
            String myAssetIdNow = myDataDto.getMyAssetId();
            // 获取当前本方映射资产
            String mappingAssetIdNow = myDataDto.getMappingAssetId();
            // 获取当前科目性质
            String natureNow = myDataDto.getNature();

            // 当前只进行四级科目处理,无资产代表非四级科目，当前不处理
            if (StringUtil.isEmpty(myAssetIdNow)) {
                continue;
            }

            // 根据本方【科目性质】【资产】获取对应的他方四级科目
            String subjectIdOther = null;// 获取的四级科目
            String threeSubjectOther = null;// 四级科目对应的3级科目数据
            for (NatureSubjectInfo otherDto : subjectInfoInSchemeNatureForOther) {
                // 获取与我方映射资产一致，且对应科目性质一致的数据集
                if (StringUtil.isNotEmpty(otherDto.getMyAssetId()) && otherDto.getMyAssetId().equals(mappingAssetIdNow)
                    && StringUtil.isNotEmpty(otherDto.getNature()) && otherDto.getNature().equals(natureNow)) {

                    // 与需求确认，资产是一一对应的，故只会存在一条与之对应的数据。
                    subjectIdOther = otherDto.getSubjectId();
                    // 他方该四级科目对应的三级科目
                    threeSubjectOther = otherDto.getThreeSubject();

                    // 跳出循环
                    break;
                }
            }

            // 存在映射资产（进行一一对应）,逻辑处理参考我方科目映射一对一处理
            if (StringUtil.isNotEmpty(subjectIdOther) && othFileDataMap.get(subjectIdOther) != null
                && !othFileDataMap.get(subjectIdOther).isEmpty()) {
                // 对方系统行数据
                Map<String, String> othRowDataMap = othFileDataMap.get(subjectIdOther);

                // 去除他方存的四级科目数据
                othFileDataMapTemp.remove(subjectIdOther);
                // 同时去除他方该四级科目对应的三级科目数据
                othFileDataMapTemp.remove(threeSubjectOther);

                // 记录存在对方性质映射四级数据的我方3级科目(支持去重)。
                haveNatureDateThreeSubject.add(threeSubjectIdNow);

                // 本系统行数据
                Map<String, String> myRowDataMap = myDataDto.getExcelData();

                // 定义行对比结果数据
                ReconcileProductRowResult rowResult = new ReconcileProductRowResult();
                String compareId = UUID.randomUUID().toString().replaceAll("-", "");
                rowResult.setCompareId(compareId);
                rowResult.setProdId(prodId);
                rowResult.setSchemeId(schemeId);
                rowResult.setCompareDate(this.DateStringToDate(prdValManDto.getValDateString()));
                rowResult.setCompareType(compareType);
                rowResult.setCompareRowno(myDataDto.getRowNo());
                rowResult.setMySubjectId(subjectIdNow);
                rowResult.setOthersideSubjectId(subjectIdOther);
                rowResultList.add(rowResult);

                // 定义列对比结果数据
                Map<String, String> structMap = this.hasChinese(subjectIdNow) ? totalStructMap : subjectStructMap;
                for (Entry<String, String> structEntry : structMap.entrySet()) {
                    String myColId = structEntry.getKey();
                    String myData = removeStrFormat(myRowDataMap.get(myColId));
                    myData = removeStrFormat(myData);
                    String othColId = structEntry.getValue();
                    String othData = removeStrFormat(othRowDataMap == null ? null : othRowDataMap.get(othColId));
                    if (mySubjectIdColId.equals(myColId)) {
                        continue;
                    }
                    if (subjectNameColId.equals(myColId)) {
                        if (StringUtil.isEmpty(rowResult.getMySubjectName())) {
                            rowResult.setMySubjectName(myData);
                        }
                        if (StringUtil.isEmpty(rowResult.getOthersideSubjectName())) {
                            rowResult.setOthersideSubjectName(othData);
                        }
                        // 非汇总行，不进行科目对比，汇总行支持科目对比
                        if (!this.hasChinese(subjectIdNow)) {// 科目行情况
                            continue;
                        }
                    }

                    ReconcileProductColResult colResult = new ReconcileProductColResult();
                    BigDecimal myDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(myData);
                    BigDecimal othDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(othData);
                    BigDecimal difference = ArithmeticUtil.doSub(myDataNum, othDataNum);

                    if (difference.abs().compareTo(errorNumber) > 0) {
                        // 对账不一致
                        reconcileStatus = DictCodeList.对账结果_对账不一致.getValue();
                    }
                    colResult.setCompareId(compareId);
                    colResult.setCompareCol(structEntry.getKey());
                    colResult.setMyData(myData);
                    colResult.setOthersideData(othData);
                    colResult.setNeedcompare(this.getNeedcompare(difference));
                    if (StringUtil.isNotEmpty(myData) || StringUtil.isNotEmpty(othData)) {
                        colResult.setDifference(difference.toString());
                    }
                    colResultList.add(colResult);
                }

            }
            // 是四级科目，但是不存在映射资产
            else {
                // 添加到无任何映射关系的我方数据
                noConfigDataList.add(myDataDto);
            }

        }

        // 【多对多逻辑处理】：3.【进行我方三级科目处理】*************************************************************
        for (SurplusDataDto myDataDto : manyToManyList) {
            // 获取当前科目
            String subjectIdNow = myDataDto.getSubjectId();
            // 获取当前三级科目
            String threeSubjectIdNow = myDataDto.getThreeSubject();
            // 获取当前本方资产
            String myAssetIdNow = myDataDto.getMyAssetId();

            // 当前只进行三级科目处理,有资产代表四级科目，当前不处理
            if (StringUtil.isNotEmpty(myAssetIdNow)) {
                continue;
            }

            // 存在有对方科目性质映射的数据
            if (haveNatureDateThreeSubject.contains(threeSubjectIdNow)) {
                // 本系统行数据
                Map<String, String> myRowDataMap = myDataDto.getExcelData();

                // 定义行对比结果数据
                ReconcileProductRowResult rowResult = new ReconcileProductRowResult();
                String compareId = UUID.randomUUID().toString().replaceAll("-", "");
                rowResult.setCompareId(compareId);
                rowResult.setProdId(prodId);
                rowResult.setSchemeId(schemeId);
                rowResult.setCompareDate(this.DateStringToDate(prdValManDto.getValDateString()));
                rowResult.setCompareType(compareType);
                rowResult.setCompareRowno(myDataDto.getRowNo());
                rowResult.setMySubjectId(subjectIdNow);
                rowResult.setOthersideSubjectId("下级汇总");
                rowResultList.add(rowResult);

                // 定义列对比结果数据
                Map<String, String> structMap = this.hasChinese(subjectIdNow) ? totalStructMap : subjectStructMap;
                for (Entry<String, String> structEntry : structMap.entrySet()) {
                    String myColId = structEntry.getKey();
                    String myData = removeStrFormat(myRowDataMap.get(myColId));
                    myData = removeStrFormat(myData);
                    // String othColId = structEntry.getValue();
                    // String othData = removeStrFormat(othRowDataMap == null ?
                    // null : othRowDataMap.get(othColId));

                    // 当为科目id列，不进行处理
                    if (mySubjectIdColId.equals(myColId)) {
                        continue;
                    }

                    // 当为科目名列
                    if (subjectNameColId.equals(myColId)) {
                        if (StringUtil.isEmpty(rowResult.getMySubjectName())) {
                            rowResult.setMySubjectName(myData);
                        }
                        if (StringUtil.isEmpty(rowResult.getOthersideSubjectName())) {
                            rowResult.setOthersideSubjectName("下级汇总");
                        }
                        // 非汇总行，不进行科目对比，汇总行支持科目对比
                        if (!this.hasChinese(subjectIdNow)) {// 科目行情况
                            continue;
                        }
                    }

                    // 获取当前的他方数据，直接从我方的四级科目获取
                    BigDecimal totalOtherData = BigDecimal.ZERO;

                    // 循环已经对比结束的数据
                    for (ReconcileProductRowResult rowDto : rowResultList) {
                        // 入库数据为当前科目的下级
                        if (StringUtil.isNotEmpty(rowDto.getMySubjectId()) && !rowDto.getMySubjectId().equals(subjectIdNow)
                            && rowDto.getMySubjectId().contains(subjectIdNow)) {
                            // 获取当前对比id和对比列下的数据
                            for (ReconcileProductColResult colDate : colResultList) {
                                // 累计当前对比id和对比列下的数据
                                if (colDate.getCompareId().equals(rowDto.getCompareId())
                                    && colDate.getCompareCol().equals(myColId)) {
                                    totalOtherData = ArithmeticUtil.doAdd(totalOtherData,
                                        BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(colDate.getOthersideData()));
                                }
                            }
                        }
                    }

                    // 获取累加后的他方数据
                    String othData = totalOtherData.toString();

                    // 列数据
                    ReconcileProductColResult colResult = new ReconcileProductColResult();

                    // 需求提出：三级二级一级下级汇总中，只有一级显示差异。
                    // BigDecimal myDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(myData);
                    // BigDecimal othDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(othData);
                    // BigDecimal difference = ArithmeticUtil.doSub(myDataNum, othDataNum);
                    BigDecimal difference = BigDecimal.ZERO;

                    if (difference.abs().compareTo(errorNumber) > 0) {
                        // 对账不一致
                        reconcileStatus = DictCodeList.对账结果_对账不一致.getValue();
                    }
                    colResult.setCompareId(compareId);
                    colResult.setCompareCol(structEntry.getKey());
                    colResult.setMyData(myData);
                    colResult.setOthersideData(othData);
                    colResult.setNeedcompare(this.getNeedcompare(difference));
                    if (StringUtil.isNotEmpty(myData) || StringUtil.isNotEmpty(othData)) {
                        colResult.setDifference(difference.toString());
                    }
                    colResultList.add(colResult);
                }

            }
            // 不存在映射性质的3级科目数据
            else {
                // 添加到无任何映射关系的我方数据
                noConfigDataList.add(myDataDto);
            }
        }

        // 获取最后经过多对多处理后依旧剩余的本方数据(处理多对多四级和三级之后数据)
        noConfigDataListForMy.addAll(noConfigDataList);

        // 定义返回值
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("reconcileStatus", reconcileStatus);// 对账结果
        returnMap.put("rowNo", rowNo);// 我方excel数据序号

        returnMap.put("noConfigDataListForMany", noConfigDataListForMy);// 定义多对多过滤后剩余的数据

        return returnMap;
    }

    /**
     * 对账逻辑方法模块化 ：从我方数据出发，将剩余数据进行【二级】【一级】向上汇总（只针对我方对比他方）。
     * 
     * @param rowResultList 已经对比结束的行数据
     * @param colResultList 已经对比结束的列数据
     * @param noConfigDataListForMy 剩余的我方数据
     * @param mySubjectIdColId 科目代码列
     * @param subjectNameColId 科目名称列
     * @param errorNumber 单科目允许误差
     * @param rowNo 我方excel数据序号
     * @param othFileDataMapTemp
     * @param booksetId 当前账套代码
     * @param prdValManDto 产品信息
     * @param prdSchemeInfo 方案信息
     * @param subjectStructMap 科目行列Map
     * @param totalStructMap 汇总行列Map
     * @param colName2ColNoStructMap Map<列名,列号>
     * @param mySubjectGroupMap 主方科目数据
     * @param othSubjectGroupMap 对比方科目数据
     * @param myFileDataMap 主文件数据
     * @param othFileDataMap 对比文件数据
     * @param compareType 对比类型
     * @return
     * @see
     * @since 1.0
     */
    private Map<String, Object> compareFromMergeUpStep(List<ReconcileProductRowResult> rowResultList,
                                                       List<ReconcileProductColResult> colResultList,
                                                       List<SurplusDataDto> noConfigDataListForMy, String mySubjectIdColId,
                                                       String subjectNameColId, BigDecimal errorNumber, Long rowNo,
                                                       Map<String, Map<String, String>> othFileDataMapTemp, String booksetId,
                                                       ConfirmPaDto prdValManDto, ReconcileSchemeInfo prdSchemeInfo,
                                                       Map<String, String> subjectStructMap, Map<String, String> totalStructMap,
                                                       Map<String, String> colName2ColNoStructMap,
                                                       Map<String, List<String>> mySubjectGroupMap,
                                                       Map<String, List<String>> othSubjectGroupMap,
                                                       Map<String, Map<String, String>> myFileDataMap,
                                                       Map<String, Map<String, String>> othFileDataMap, String compareType) {
        // 对账状态
        String reconcileStatus = DictCodeList.对账结果_对账一致.getValue();
        // 产品代码
        String prodId = prdValManDto.getFinprodId();
        // 方案代码
        String schemeId = prdSchemeInfo.getSchemeId();

        // 获取剩余数据
        List<SurplusDataDto> noConfigDataListForMergeUp = new ArrayList<SurplusDataDto>();
        noConfigDataListForMergeUp.addAll(noConfigDataListForMy);

        // 若为【我方对比他方】，执行向上汇总操作
        if (DictCodeListExt1.对比类型_本系统对比对方系统.getValue().equals(compareType)) {

            // 获取我方剩余的数据科目集合
            List<String> noConfigSubjectIdListForMy = new ArrayList<>();
            for (SurplusDataDto dto : noConfigDataListForMy) {
                noConfigSubjectIdListForMy.add(dto.getSubjectId());
            }

            // 汇总级别
            List<String> leveList = new ArrayList<String>();
            leveList.add("2");// 先对2级科目进行汇总
            leveList.add("1");// 再对1级科目进行汇总

            // 根据级别进行汇总，此方法是为了方法公用
            for (String leveNow : leveList) {

                // 根据剩余数据和级别，获取对应科目集合
                List<String> sjList = new ArrayList<String>();
                if (noConfigSubjectIdListForMy != null && !noConfigSubjectIdListForMy.isEmpty()) {
                    sjList = executeMapper.getSubjectIdWithFillter(booksetId, leveNow, noConfigSubjectIdListForMy);
                }

                // 若无科目集合进行下一个循环
                if (sjList == null || sjList.isEmpty()) {
                    continue;
                }

                // 对剩余数据中，当前级别科目进行汇总
                for (SurplusDataDto noConfigDto : noConfigDataListForMy) {

                    // 只处理当前级别科目中的数据
                    if (sjList.contains(noConfigDto.getSubjectId())) {

                        // 根据科目id，获取下级科目集合
                        List<String> lowerSubjectList = executeMapper.getSubjectIdByFsubject(booksetId,
                            noConfigDto.getSubjectId());

                        // 本系统行数据
                        Map<String, String> myRowDataMap = noConfigDto.getExcelData();

                        // 定义行对比结果数据
                        ReconcileProductRowResult rowResult = new ReconcileProductRowResult();
                        String compareId = UUID.randomUUID().toString().replaceAll("-", "");
                        rowResult.setCompareId(compareId);
                        rowResult.setProdId(prodId);
                        rowResult.setSchemeId(schemeId);
                        rowResult.setCompareDate(this.DateStringToDate(prdValManDto.getValDateString()));
                        rowResult.setCompareType(compareType);
                        rowResult.setCompareRowno(noConfigDto.getRowNo());
                        rowResult.setMySubjectId(noConfigDto.getSubjectId());
                        rowResult.setOthersideSubjectId("下级汇总");
                        rowResultList.add(rowResult);

                        // 定义列对比结果数据
                        Map<String, String> structMap = this.hasChinese(
                            noConfigDto.getSubjectId()) ? totalStructMap : subjectStructMap;
                        for (Entry<String, String> structEntry : structMap.entrySet()) {
                            String myColId = structEntry.getKey();
                            String myData = removeStrFormat(myRowDataMap.get(myColId));
                            myData = removeStrFormat(myData);
                            // String othColId = structEntry.getValue();
                            // String othData = removeStrFormat(othRowDataMap == null ? null : othRowDataMap.get(othColId));

                            // 当为科目id列，不进行处理
                            if (mySubjectIdColId.equals(myColId)) {
                                continue;
                            }

                            // 当为科目名列
                            if (subjectNameColId.equals(myColId)) {
                                if (StringUtil.isEmpty(rowResult.getMySubjectName())) {
                                    rowResult.setMySubjectName(myData);
                                }
                                if (StringUtil.isEmpty(rowResult.getOthersideSubjectName())) {
                                    rowResult.setOthersideSubjectName("下级汇总");
                                }
                                // 非汇总行，不进行科目对比，汇总行支持科目对比
                                if (!this.hasChinese(noConfigDto.getSubjectId())) {// 科目行情况
                                    continue;
                                }
                            }

                            // 获取当前的他方数据，直接从我方的四级科目获取
                            BigDecimal totalOtherData = BigDecimal.ZERO;

                            // 循环已经对比结束的数据
                            for (ReconcileProductRowResult rowDto : rowResultList) {
                                // 为当前需要对比科目的下级科目
                                if (lowerSubjectList.contains(rowDto.getMySubjectId())) {
                                    // 获取当前对比id和对比列下的数据
                                    for (ReconcileProductColResult colDate : colResultList) {
                                        // 累计当前对比id和对比列下的数据
                                        if (colDate.getCompareId().equals(rowDto.getCompareId())
                                            && colDate.getCompareCol().equals(myColId)) {
                                            totalOtherData = ArithmeticUtil.doAdd(totalOtherData,
                                                BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(
                                                    colDate.getOthersideData()));
                                        }
                                    }
                                }
                            }

                            // 获取累加后的他方数据
                            String othData = totalOtherData.toString();

                            // 列数据
                            ReconcileProductColResult colResult = new ReconcileProductColResult();

                            // 需求提出：三级二级一级下级汇总中，只有一级显示差异。
                            BigDecimal difference = BigDecimal.ZERO;
                            if ("1".equals(leveNow)) {
                                BigDecimal myDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(myData);
                                BigDecimal othDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(othData);
                                difference = ArithmeticUtil.doSub(myDataNum, othDataNum);
                            }

                            if (difference.abs().compareTo(errorNumber) > 0) {
                                // 对账不一致
                                reconcileStatus = DictCodeList.对账结果_对账不一致.getValue();
                            }
                            colResult.setCompareId(compareId);
                            colResult.setCompareCol(structEntry.getKey());
                            colResult.setMyData(myData);
                            colResult.setOthersideData(othData);
                            colResult.setNeedcompare(this.getNeedcompare(difference));
                            if (StringUtil.isNotEmpty(myData) || StringUtil.isNotEmpty(othData)) {
                                colResult.setDifference(difference.toString());
                            }
                            colResultList.add(colResult);
                        }

                        // 去除当前已经处理过的值
                        noConfigDataListForMergeUp.remove(noConfigDto);

                    }

                }
            }

        }

        // 定义返回值
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("reconcileStatus", reconcileStatus);// 对账结果
        returnMap.put("rowNo", rowNo);// 我方excel数据序号

        returnMap.put("noConfigDataListForMergeUp", noConfigDataListForMergeUp);// 定义汇总过滤后剩余的数据

        return returnMap;
    }

    /**
     * 对账逻辑方法模块化 ：剩余数据【我方excel剩余数据】和【对方excel剩余数据】处理
     * 
     * @param noConfigDataListForMy 过滤后剩余的我方数据
     * @param mySubjectIdColId 科目代码列
     * @param subjectNameColId 科目名称列
     * @param errorNumber 单科目允许误差
     * @param rowNo 我方excel数据序号
     * @param othFileDataMapTemp
     * @param booksetId 当前账套代码
     * @param prdValManDto 产品信息
     * @param prdSchemeInfo 方案信息
     * @param subjectStructMap 科目行列Map
     * @param totalStructMap 汇总行列Map
     * @param colName2ColNoStructMap Map<列名,列号>
     * @param mySubjectGroupMap 主方科目数据
     * @param othSubjectGroupMap 对比方科目数据
     * @param myFileDataMap 主文件数据
     * @param othFileDataMap 对比文件数据
     * @param compareType 对比类型
     * @return
     * @see
     * @since 1.0
     */
    private Map<String, Object> compareSurplusDataStep(List<SurplusDataDto> noConfigDataListForMy, String mySubjectIdColId,
                                                       String subjectNameColId, BigDecimal errorNumber, Long rowNo,
                                                       Map<String, Map<String, String>> othFileDataMapTemp, String booksetId,
                                                       ConfirmPaDto prdValManDto, ReconcileSchemeInfo prdSchemeInfo,
                                                       Map<String, String> subjectStructMap, Map<String, String> totalStructMap,
                                                       Map<String, String> colName2ColNoStructMap,
                                                       Map<String, List<String>> mySubjectGroupMap,
                                                       Map<String, List<String>> othSubjectGroupMap,
                                                       Map<String, Map<String, String>> myFileDataMap,
                                                       Map<String, Map<String, String>> othFileDataMap, String compareType) {

        // 对账状态
        String reconcileStatus = DictCodeList.对账结果_对账一致.getValue();
        // 产品代码
        String prodId = prdValManDto.getFinprodId();
        // 方案代码
        String schemeId = prdSchemeInfo.getSchemeId();

        // 产品行对账结果
        List<ReconcileProductRowResult> rowResultList = new ArrayList<ReconcileProductRowResult>();
        // 产品列对账结果
        List<ReconcileProductColResult> colResultList = new ArrayList<ReconcileProductColResult>();

        // 【步骤一：对比我方对比结束后剩余的数据】
        // 处理最后我方没有对应的数据，即我方excel无任何映射的数据
        if (noConfigDataListForMy != null && !noConfigDataListForMy.isEmpty()) {
            for (SurplusDataDto noConfigDto : noConfigDataListForMy) {
                // 没有对应的映射关系
                Map<String, String> myRowDataMap = noConfigDto.getExcelData();
                // myFileDataMapTemp.remove(entry.getKey());
                ReconcileProductRowResult rowResult = new ReconcileProductRowResult();
                String compareId = UUID.randomUUID().toString().replaceAll("-", "");
                rowResult.setCompareId(compareId);
                rowResult.setProdId(prodId);
                rowResult.setSchemeId(schemeId);
                rowResult.setCompareDate(this.DateStringToDate(prdValManDto.getValDateString()));
                rowResult.setCompareType(compareType);
                rowResult.setCompareRowno(noConfigDto.getRowNo());
                rowResult.setMySubjectId(noConfigDto.getSubjectId());
                // rowResult.setOthersideSubjectId(othSubjectId);

                rowResultList.add(rowResult);

                Map<String, String> structMap = this.hasChinese(noConfigDto.getSubjectId()) ? totalStructMap : subjectStructMap;
                for (Entry<String, String> structEntry : structMap.entrySet()) {

                    String myColId = structEntry.getKey();
                    if (mySubjectIdColId.equals(myColId)) {
                        continue;
                    }

                    ReconcileProductColResult colResult = new ReconcileProductColResult();
                    // String othColId = structEntry.getValue();
                    String myData = removeStrFormat(myRowDataMap.get(myColId));
                    // String othData =
                    // removeStrFormat(othRowDataMap.get(othColId));
                    // BigDecimal myDataNum =
                    // BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(myData);

                    if (subjectNameColId.equals(myColId)) {
                        if (StringUtil.isEmpty(rowResult.getMySubjectName())) {
                            rowResult.setMySubjectName(myData);
                        }
                        // 非汇总行，不进行科目对比，汇总行支持科目对比
                        if (!this.hasChinese(noConfigDto.getSubjectId())) {// 科目行情况
                            continue;
                        }
                    }
                    // BigDecimal othDataNum =
                    // BigDecimalConverterUtil.stringToBigDecimal(othData);

                    // 代码注掉原因：未配置映射关系的项目部影响对账结果
                    // if (difference.abs().compareTo(errorNumber) > 0) {
                    // // 对账不一致
                    // reconcileStatus =
                    // DictCodeListOther.对账结果_对账不一致.getValue();
                    // }

                    // BigDecimal difference = myDataNum;
                    BigDecimal difference = BigDecimal.ZERO;// 多余文件不需要对比

                    colResult.setCompareId(compareId);
                    colResult.setCompareCol(structEntry.getKey());
                    colResult.setMyData(myData);
                    // colResult.setOthersideData(othData);
                    colResult.setNeedcompare(this.getNeedcompare(difference));
                    if (StringUtil.isNotEmpty(myData)) {
                        colResult.setDifference(difference.toString());
                    }
                    colResultList.add(colResult);
                }
            }
        }

        // 【步骤二：对比完本方excel数据后，对方excel数据还剩余情况】
        // 情况1：（【本方科目与当前他方科目存在科目映射关系，但是本方excel无该科目数据，对方excel有该科目数据】情况），即他方科目存在于映射中。
        // 情况2：（【本方科目与当前他方科目不存在映射关系，记录对方excel该科目数据，本方该科目数据已在上面无映射中记录过】情况），即他方科目不存在与映射中。
        // 注：根据需求，无映射关系的数据，不进行对账结果处理，且不影响对账结果。故情况1需要对比，情况2不需要对比
        for (Entry<String, Map<String, String>> entry : othFileDataMapTemp.entrySet()) {
            String othSubjectId = entry.getKey();

            // 判断当前他方excel多出来的他方数据，是上述情况1（有科目映射），还是上述情况2（无科目映射）导致。
            Boolean needComp = false;
            for (String key : mySubjectGroupMap.keySet()) {
                // 获取关联的对方科目映射
                List<String> subjectList = mySubjectGroupMap.get(key);
                // 判断对方科目映射中是否有该科目,若包含，代表情况1（有科目映射，但是本方无数据），需要对比。
                if (StringUtil.isNotEmpty(othSubjectId) && subjectList.contains(othSubjectId)) {
                    needComp = true;
                }

            }

            rowNo++ ;
            // 对方系统行数据
            Map<String, String> othRowDataMap = entry.getValue();

            ReconcileProductRowResult rowResult = new ReconcileProductRowResult();
            String compareId = UUID.randomUUID().toString().replaceAll("-", "");
            rowResult.setCompareId(compareId);
            rowResult.setProdId(prodId);
            rowResult.setSchemeId(schemeId);
            rowResult.setCompareDate(this.DateStringToDate(prdValManDto.getValDateString()));
            rowResult.setCompareType(compareType);
            rowResult.setCompareRowno(rowNo);
            // rowResult.setMySubjectId(mySubjectId);
            rowResult.setOthersideSubjectId(othSubjectId);
            rowResultList.add(rowResult);

            Map<String, String> structMap;
            if (this.hasChinese(othSubjectId)) {
                structMap = totalStructMap;
            }
            else {
                structMap = subjectStructMap;
            }
            for (Entry<String, String> structEntry : structMap.entrySet()) {
                String myColId = structEntry.getKey();
                String othColId = structEntry.getValue();
                String othData = removeStrFormat(othRowDataMap.get(othColId));
                othData = removeStrFormat(othData);
                // String othColId = structEntry.getValue();
                // String othData =
                // removeStrFormat(othRowDataMap.get(othColId));

                // 科目id
                if (mySubjectIdColId.equals(myColId)) {
                    continue;
                }
                // 科目名称
                if (subjectNameColId.equals(myColId)) {
                    if (StringUtil.isEmpty(rowResult.getOthersideSubjectName())) {
                        rowResult.setOthersideSubjectName(othData);;
                    }

                    // 非汇总行，不进行科目对比，汇总行支持科目对比
                    if (!this.hasChinese(othSubjectId)) {// 科目行情况
                        continue;
                    }
                }

                ReconcileProductColResult colResult = new ReconcileProductColResult();
                BigDecimal myDataNum = BigDecimal.ZERO;
                BigDecimal othDataNum = BigDecimalConverterUtilForValuareconcy.stringToBigDecimal(othData);

                // 判断是否要进行对比
                BigDecimal difference = BigDecimal.ZERO;
                if (needComp) {// 需要对比
                    // 对比差值
                    difference = ArithmeticUtil.doSub(myDataNum, othDataNum);
                    if (difference.abs().compareTo(errorNumber) > 0) {
                        // 对账不一致
                        reconcileStatus = DictCodeList.对账结果_对账不一致.getValue();
                    }
                }
                else {// 不需要对比
                    difference = BigDecimal.ZERO;// 不进行对比
                }

                colResult.setCompareId(compareId);
                colResult.setCompareCol(structEntry.getKey());
                // colResult.setMyData(myData);
                colResult.setOthersideData(othData);
                colResult.setNeedcompare(this.getNeedcompare(difference));
                if (StringUtil.isNotEmpty(othData)) {
                    colResult.setDifference(difference.toString());
                }
                colResultList.add(colResult);
            }

        }

        // 定义返回值
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("reconcileStatus", reconcileStatus);// 对账结果
        returnMap.put("rowNo", rowNo);// 我方excel数据序号
        returnMap.put("rowResultList", rowResultList);// 入库行对比数据
        returnMap.put("colResultList", colResultList);// 入库对比列数据

        return returnMap;
    }

    /**
     * 对账逻辑方法模块化：对比结果数据入库
     * 
     * @param rowResultList 行对比结果
     * @param colResultList 列对比结果
     * @return
     * @see
     * @since 1.0
     */
    private void insertDbForCompareDate(List<ReconcileProductRowResult> rowResultList,
                                        List<ReconcileProductColResult> colResultList) {

        // 行对比结果插入
        if (rowResultList != null && !rowResultList.isEmpty()) {
            // 定义一个固定大小插入存放集合
            List<List<ReconcileProductRowResult>> list = DataHandleUtil.splitList(rowResultList, 200);
            for (List<ReconcileProductRowResult> listInsert : list) {
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    executeMapper.insertRowResultBatch(listInsert);
                }
            }
        }

        // 列对比结果插入
        if (colResultList != null && !colResultList.isEmpty()) {
            // 定义一个固定大小插入存放集合
            List<List<ReconcileProductColResult>> list = DataHandleUtil.splitList(colResultList, 200);
            for (List<ReconcileProductColResult> listInsert : list) {
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    executeMapper.insertColResultBatch(listInsert);
                }
            }
        }
    }

    /**
     * 字符串时间转换为Date
     * 
     * @param DateString 【2018/11/2】格式字符串
     * @return
     * @see
     * @since 1.0
     */
    public Date DateStringToDate(String DateString) {
        // 定义返回时间
        Date resultDate = null;

        // 传递字符串不为空，进行时间转换
        if (DateString != null) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                resultDate = formatter.parse(DateString);
            }
            catch (ParseException e) {
                logger.error(e.getMessage(),e);
            }
        }

        return resultDate;
    }

    /**
     * 判断是否含有汉字
     *
     * @param value 判断对象
     * @return 含汉字true,否则false
     * @since 1.0
     */
    public boolean hasChinese(String value) {
        if (StringUtil.isEmpty(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[^\u4e00-\u9fa5]+");
        Matcher matcher = pattern.matcher(value);
        return !matcher.matches();
    }

    /**
     * 去除金额字段的逗号
     * 
     * @param value 源值
     * @return 去除逗号后的值
     * @since 1.0
     */
    private String removeStrFormat(String value) {
        if (StringUtil.isEmpty(value)) {
            return value;
        }
        return StringUtil.replace(value, ",", "");
    }

    /**
     * 判断是否有差异
     * 
     * @param difference 差异值
     * @return 差异值大于0，返回是，否则返回否
     * @since 1.0
     */
    private String getNeedcompare(BigDecimal difference) {
        if (difference == null) {
            return DictCodeList.是否_否.getValue();
        }
        return difference.signum() != 0 ? DictCodeList.是否_是.getValue() : DictCodeList.是否_否.getValue();
    }

    /**
     * Description:判断Map是否为空
     *
     * @param map 需要判断的map
     * @return 非空时返回true，为空返回false
     */
    public boolean isNotEmptyMap(Map<?, ?> map) {
        return !isEmptyMap(map);
    }

    /**
     * Description:判断Map是否为空
     *
     * @param map 需要判断的map
     * @return 为空时返回true，非空返回false
     */
    public boolean isEmptyMap(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 删除原对账结果
     * 
     * @param prodId 产品代码
     * @param schemeId 方案代码
     * @param valueDate 对账日期
     * @since 1.0
     */
    private void deleteOldCompareResult(String prodId, String schemeId, Date valueDate) {
        // 先删列对账结果，根据条件：产品代码，方案代码，对账日期，查到所有的compareId,然后删除
        executeMapper.deleteOldColResult(prodId, schemeId, valueDate);
        // 删除行对账结果
        executeMapper.deleteOldRowResult(prodId, schemeId, valueDate);
    }

    /**
     * 获取对账明细
     * 
     * @param prodId 产品代码
     * @param valueDate 对账日期
     * @param schemeType 方案类型
     * @param schemeId 方案代码
     * @param compareType 对账类型 本系统对比对方系统 、对方系统对比本系统
     * @param filter 过滤条件
     * @param subjectLevel 科目级别
     * @param colList 显示列
     * @return 对账明细
     * @see
     * @since 1.0
     */
    public List<CompareColDateDtoForExcel> getResultDetailList(String prodId, String valueDate, String schemeType,
                                                               String schemeId, String compareType, String filter,
                                                               Integer subjectLevel, List<String> colList) {

        List<ProductValuationReconciliationShowDto> showDtoList = executeMapper.getResultDetailList(prodId, valueDate, schemeType,
            schemeId, compareType, filter, subjectLevel, colList);
        if (StringUtil.isEmptyList(showDtoList)) {
            return new ArrayList<CompareColDateDtoForExcel>();
        }

        List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();
        for (ProductValuationReconciliationShowDto showDto : showDtoList) {
            Map<String, Object> colMap = new HashMap<String, Object>();
            colMap.put("compareId", showDto.getCompareId());
            colMap.put("mySubjectId", showDto.getMySubjectId());
            colMap.put("mySubjectName", showDto.getMySubjectName());
            colMap.put("othersideSubjectId", showDto.getOthersideSubjectId());
            colMap.put("othersideSubjectName", showDto.getOthersideSubjectName());
            colMap.put("remark", showDto.getRemark());

            if (showDto.getColResultList() != null && !showDto.getColResultList().isEmpty()) {

                for (ReconcileProductColResult colResult : showDto.getColResultList()) {
                    colMap.put("myData_" + colResult.getCompareCol(), stringToBigDecimal(colResult.getMyData()));
                    colMap.put("othersideData_" + colResult.getCompareCol(), stringToBigDecimal(colResult.getOthersideData()));
                    colMap.put("difference_" + colResult.getCompareCol(), stringToBigDecimal(colResult.getDifference()));
                }
            }
            resultMapList.add(colMap);
        }

        // 备注：为适用框架excel导出方法，此处要将List<Map<String,
        // Object>>转换为List<CompareColDateDtoForExcel>。
        // 暂时【X_myData】【X_othersideData】【X_difference】只支持到Z，后续不够再加
        List<CompareColDateDtoForExcel> dtoList = new ArrayList<CompareColDateDtoForExcel>();
        for (Map<String, Object> map : resultMapList) {
            CompareColDateDtoForExcel dto = (CompareColDateDtoForExcel)this.map2Object(map, CompareColDateDtoForExcel.class);
            if (dto != null) {
                dtoList.add(dto);
            }
        }

        return dtoList;
    }

    /**
     * 字符串转BigDecimal，对于非法输入返回null
     * 
     * @param str 源串
     * @return 数值
     * @see
     * @since 1.0
     */
    public static BigDecimal stringToBigDecimal(String str) {
        BigDecimal ret = null;
        try {
            ret = new BigDecimal(str);
        }
        catch (Exception e) {
        }
        return ret;
    }

    /**
     * 生成对账excel
     * 
     * @param confirmPaDto
     * @return
     * @see
     * @since 1.0
     */
    private List<String> createCompareExcel(ConfirmPaDto confirmPaDto) {
        // 错误信息
        List<String> errorMsgList = new ArrayList<String>();

        // 产品代码
        String prodId = confirmPaDto.getFinprodId();
        // 产品名称
        String prodName = confirmPaDto.getFinprodName();
        // 对账日期
        String valueDate = confirmPaDto.getValDateString();
        // 估值表余额表标识
        String valBalFlag = confirmPaDto.getValBalFlag();

        Map<String, Object> excelMap = new HashMap<String, Object>();
        excelMap.put("prodName", prodName);
        excelMap.put("prodId", prodId);
        excelMap.put("compareDate", valueDate);

        // // 导出的文件名称
        // String exportFileName1 = SysDataUtil.getNameFromDataDict(DictCode.文件类型, valBalFlag) + "_" + DictCodeListExt1.对比类型_本系统对比对方系统.getCodeName() + "_" +
        // prodName + "_" + prodId + "_" + valueDate + ".xls";
        // String exportFileName2 = SysDataUtil.getNameFromDataDict(DictCode.文件类型, valBalFlag) + "_" + DictCodeListExt1.对比类型_对方系统对比本系统.getCodeName() + "_" +
        // prodName + "_" + prodId + "_" + valueDate + ".xls";
        //
        // // 生成对比excel，同时返回保存路径
        // Map<String, String> resultMap1 = getCreateFileId(prodId, prodName, valueDate, valBalFlag, DictCodeListExt1.对比类型_本系统对比对方系统.getValue(), excelMap,
        // exportFileName1);
        // Map<String, String> resultMap2 = getCreateFileId(prodId, prodName, valueDate, valBalFlag, DictCodeListExt1.对比类型_对方系统对比本系统.getValue(), excelMap,
        // exportFileName2);

        // 导出的文件名称
        String exportFileName = SysDataUtil.getNameFromDataDict(DictCode.文件类型, valBalFlag) + "_" + "对账结果" + "_" + prodName + "_"
                                + prodId + "_" + valueDate + ".xls";

        // 生成对比excel，同时返回保存路径
        Map<String, String> resultMap = getCreateFileId(prodId, prodName, valueDate, valBalFlag, excelMap, exportFileName);

        // 获取路径
        String resultFilePath = null;
        if (resultMap != null) {
            if (StringUtil.isNotEmpty(resultMap.get("errorString"))) {
                errorMsgList.add(resultMap.get("errorString"));
                return errorMsgList;
            }
            else {
                resultFilePath = resultMap.get("pathString");
            }

        }

        // 有返回路径，代表生成文件成功
        if (StringUtil.isNotEmpty(resultFilePath)) {
            // 对比文件信息入库,包含旧的对比文件数据删除。
            this.insertFileInfo(prodId, valBalFlag, exportFileName, resultFilePath, valueDate);

        } // 生成文件失败情况
        else {
            errorMsgList.add("产品：" + prodName + "(" + prodId + ") " + "对账结果文件生成失败");
        }

        return errorMsgList;
    }

    /**
     * 对比文件信息入库,包含旧的对比文件数据删除。
     * 
     * @param prodId 产品id
     * @param schemeType 对账类型[估值表，余额表]
     * @param exportFileName1 [对比文件]文件名称
     * @param resultFilePath1 [对比文件]文件路径
     * @param valueDate 估值日期
     * @see
     * @since 1.0
     */
    private void insertFileInfo(String prodId, String schemeType, String exportFileName, String resultFilePath,
                                String valueDate) {

        // 按类型进行数据入库
        if (DictCodeList.文件类型_估值表.getValue().equals(schemeType)) {
            // 1.删除原来的对比文件
            Map<String, Object> delParams = new HashMap<String, Object>();
            delParams.put("finprodId", prodId);
            delParams.put("valDate", valueDate);
            delParams.put("valBalFlag", schemeType);
            List<String> fileTypeList = new ArrayList<>();
            fileTypeList.add(DictCodeList.文件类型_估值表.getValue());
            delParams.put("fileTypeList", fileTypeList);
            // 删除旧的对应的对比文件信息(注:先删除【文件信息】表，再删除【估值表余额表上传文件信息】表)
            executeMapper.deleteBokFileInfoByBokValbalTableFileForCompare(delParams);
            executeMapper.deleteBokValbalTableFileForCompare(delParams);

            // 2. 【估值表对账结果文件】 入库
            // 【文件信息表记录表-入库】
            BokFileInfo bokFileInfoForMy = new BokFileInfo();// 文件信息表记录表
            bokFileInfoForMy.setFileId(IDGenerator.generate());// 文件主键
            bokFileInfoForMy.setFileType(DictCodeList.文件类型_估值表.getValue());// 设置文件类型
            bokFileInfoForMy.setFileName(exportFileName);// 文件名称
            bokFileInfoForMy.setFilePath(resultFilePath);// 文件路径
            bokFileInfoMapper.insertSelective(bokFileInfoForMy);
            // 【 估值表余额表上传文件信息表表-入库】
            BokValbalTableFile bokValbalTableFileForMy = new BokValbalTableFile(); // 估值表余额表上传文件信息表
            bokValbalTableFileForMy.setFinprodId(prodId);// 产品代码
            bokValbalTableFileForMy.setValDate(this.DateStringToDate(valueDate));// 估值时间
            bokValbalTableFileForMy.setValBalFlag(schemeType);// 设置估值表余额表标志
            bokValbalTableFileForMy.setFileId(bokFileInfoForMy.getFileId());// 文件主键
            bokValbalTableFileForMy.setFileType(bokFileInfoForMy.getFileType());// 设置文件类型
            bokValbalTableFileMapper.insertSelective(bokValbalTableFileForMy);

        }
        else if (DictCodeList.文件类型_余额表.getValue().equals(schemeType)) {
            // 1.删除原来的对比文件
            Map<String, Object> delParams = new HashMap<String, Object>();
            delParams.put("finprodId", prodId);
            delParams.put("valDate", valueDate);
            delParams.put("valBalFlag", schemeType);
            List<String> fileTypeList = new ArrayList<>();
            fileTypeList.add(DictCodeList.文件类型_余额表.getValue());
            delParams.put("fileTypeList", fileTypeList);
            // 删除旧的对应的对比文件信息(注:先删除【文件信息】表，再删除【估值表余额表上传文件信息】表)
            executeMapper.deleteBokFileInfoByBokValbalTableFileForCompare(delParams);
            executeMapper.deleteBokValbalTableFileForCompare(delParams);

            // 2.【余额表对账结果文件】 入库
            // 【文件信息表记录表-入库】
            BokFileInfo bokFileInfoForMy = new BokFileInfo();// 文件信息表记录表
            bokFileInfoForMy.setFileId(IDGenerator.generate());// 文件主键
            bokFileInfoForMy.setFileType(DictCodeList.文件类型_余额表.getValue());// 设置文件类型
            bokFileInfoForMy.setFileName(exportFileName);// 文件名称
            bokFileInfoForMy.setFilePath(resultFilePath);// 文件路径
            bokFileInfoMapper.insertSelective(bokFileInfoForMy);
            // 【 估值表余额表上传文件信息表表-入库】
            BokValbalTableFile bokValbalTableFileForMy = new BokValbalTableFile(); // 估值表余额表上传文件信息表
            bokValbalTableFileForMy.setFinprodId(prodId);// 产品代码
            bokValbalTableFileForMy.setValDate(this.DateStringToDate(valueDate));// 估值时间
            bokValbalTableFileForMy.setValBalFlag(schemeType);// 设置估值表余额表标志
            bokValbalTableFileForMy.setFileId(bokFileInfoForMy.getFileId());// 文件主键
            bokValbalTableFileForMy.setFileType(bokFileInfoForMy.getFileType());// 设置文件类型
            bokValbalTableFileMapper.insertSelective(bokValbalTableFileForMy);
        }

    }

    /**
     * 创建文件
     * 
     * @param prodId 产品代码
     * @param prodName 产品名称
     * @param valueDate 对账日期
     * @param valBalFlag 对账类型
     * @param excelMap 数据
     * @param exportFileName 导出文件名称
     * @return 创建对比结果文件代码
     * @exception Exception 异常
     * @since 1.0
     */
    private Map<String, String> getCreateFileId(String prodId, String prodName, String valueDate, String valBalFlag,
                                                Map<String, Object> excelMap, String exportFileName) {

        // 定义返回值
        Map<String, String> mapReturn = new HashMap<String, String>();
        String errorKey = "errorString";
        String pathKey = "pathString";
        mapReturn.put(errorKey, null);// 错误信息
        mapReturn.put(pathKey, null);// 上传SFTP后的路径

        // 获取方案信息
        ReconcileSchemeInfo schemeInfo = this.getPrdSchemeInfo(prodId, valBalFlag);

        // 获取数据(本系统对比对方系统)
        List<CompareColDateDtoForExcel> rowDataListForMy = this.getResultDetailList(prodId, valueDate, valBalFlag,
            schemeInfo.getSchemeId(), DictCodeListExt1.对比类型_本系统对比对方系统.getValue(), null, null, null);
        excelMap.put("rowDataListForMy", rowDataListForMy);

        // 获取数据(对方系统对比本系统)
        List<CompareColDateDtoForExcel> rowDataListForOther = this.getResultDetailList(prodId, valueDate, valBalFlag,
            schemeInfo.getSchemeId(), DictCodeListExt1.对比类型_对方系统对比本系统.getValue(), null, null, null);
        excelMap.put("rowDataListForOther", rowDataListForOther);

        // 文件存放目录（服务器临时存放路径)
        String outputFilepathTemp = CacheUtil.getSysParam(GlobalConstant.COM_PROP_ACCESS_EXPORTPATH) + SEPARATOR_NOW + "ZBLS"
                                    + SEPARATOR_NOW + "COMPARE" + SEPARATOR_NOW + valBalFlag + SEPARATOR_NOW + exportFileName;

        // 文件存放目录（ftp上传真实路径)
        String outputFilepathReal = CacheUtil.getSysParam("FILEUPLOAD_REMOTE_PATH") + SEPARATOR_NOW + "ZB" + SEPARATOR_NOW
                                    + "COMPARE-" + valBalFlag;// 上传文件夹路径

		// 获取模版流
		InputStream stream = null;

        // 生成excel（在服务器改路径生成一个excel临时文件）
        try {
            // 获取对应模版名称
            String tempName = getTempFile(valBalFlag);

            // 获取模版路径(使用估值表余额表文件夹形式)
            String tplPath = CacheUtil.getSysParam("BATCH_TPL_OUT") + SEPARATOR_NOW + tempName;

            // 获取模版文件
            File templeteFile = new SafelyFile(tplPath);
            // 检查模版是否存在
            if (!templeteFile.exists()) {
                String errorInfo = "产品：" + prodName + "(" + prodId + ") " + "估值表余额表生成对账文件失败：未找到对应的"
                                   + SysDataUtil.getNameFromDataDict(DictCode.文件类型, valBalFlag) + "模版。";
                logger.error(errorInfo);
                mapReturn.put(errorKey, errorInfo);// 错误信息
                mapReturn.put(pathKey, null);// 上传SFTP后的路径
                return mapReturn;
            }

            // 获取模版流
			stream = new FileInputStream(templeteFile);

            // 生成excel(框架自带方法)
            ExcelTools.writeExcel(stream, tempName, excelMap, outputFilepathTemp, new ExcelCellValueFunction() {

                @Override
                public void execute(String tplKey, Object value, Cell cell, Row row, Sheet sheet, Workbook workbook) {

                    // 对比结果不为0，显示为红色。
                    if (StringUtil.startsWith(tplKey, "rowDataList") && tplKey.contains("difference_")
                        && value instanceof BigDecimal && BigDecimal.ZERO.compareTo(new BigDecimal(value.toString())) != 0) {

                        // 获取原来格式
                        CellStyle newStyle = workbook.createCellStyle();
                        newStyle.cloneStyleFrom(cell.getCellStyle());

                        // 修改字体颜色为红色
                        Font ztFont = workbook.createFont();
                        ztFont.setColor(Font.COLOR_RED); // 将字体设置为“红色”

                        // 字体颜色设置
                        newStyle.setFont(ztFont);
                        cell.setCellStyle(newStyle);
                    }
                }
            });

            // 判断是否生成,若未生成，直接返回
            File file = new SafelyFile(outputFilepathTemp);
            if (!(file.isFile() && file.exists())) {
                String errorInfo = "产品：" + prodName + "(" + prodId + ") " + "估值表余额表生成对账文件失败："
                                   + SysDataUtil.getNameFromDataDict(DictCode.文件类型, valBalFlag) + "服务器生成临时对比文件失败。";
                logger.error(errorInfo);
                mapReturn.put(errorKey, errorInfo);// 错误信息
                mapReturn.put(pathKey, null);// 上传SFTP后的路径
                return mapReturn;
            }

        }
        catch (Exception e) {
            String errorInfo = "产品：" + prodName + "(" + prodId + ") " + "估值表余额表生成对账文件失败："
                               + SysDataUtil.getNameFromDataDict(DictCode.文件类型, valBalFlag) + "服务器生成临时对比文件失败。";
            logger.error(errorInfo + "错误信息：" + e.getMessage());
            mapReturn.put(errorKey, errorInfo);// 错误信息
            mapReturn.put(pathKey, null);// 上传SFTP后的路径
            return mapReturn;
        }
        finally {
			if(stream != null) {
				try {
					stream.close();
				}
				catch (IOException e) {
                    logger.error(e.getMessage(),e);
				}
			}
        }

        // 临时保存文件，名称格式化修改。
        outputFilepathTemp = FileNameUtil.formatFileName(outputFilepathTemp);
        File fileNow = new SafelyFile(outputFilepathTemp);
        boolean rec = fileNow.renameTo(new SafelyFile(outputFilepathTemp));
        if (rec) {
            logger.info("产品：" + prodName + "(" + prodId + ") " + "对账，服务器生成对比文件处理成功。");
        }

        // 上传服务器（ftp服务器）(文件路径相同的时候，会覆盖就文件，故不用删除)。
        SftpTools sftpTools = new SftpTools();
        try {
            String ip = CacheUtil.getSysParam("FILEUPLOAD_HOST");// 主机IP
            Integer port = Integer.parseInt(CacheUtil.getSysParam("FILEUPLOAD_PORT"));// 主机端口
            String username = CacheUtil.getSysParam("FILEUPLOAD_USERNAME");// 主机用户名
            String password = CacheUtil.getSysParam("FILEUPLOAD_PASSWORD");// 主机密码或秘钥地址
            String sftpLoginType = CacheUtil.getSysParam("SFTP_LOGIN_TYPE");// SFTP登录方式

            sftpTools.login(username, password, ip, port, sftpLoginType);// sftp登录
            sftpTools.putFile(outputFilepathTemp, outputFilepathReal); // 上传至文件服务器
            sftpTools.close();// 关闭登录

            progressBarService.updatePercent("DocumentManagement002", ProgressBarService.FILE_UPLOAD_ID, 1D, false);

            // 定义返回值
            mapReturn.put(errorKey, null);// 错误信息
            mapReturn.put(pathKey, outputFilepathReal + SEPARATOR_NOW + exportFileName);// 上传SFTP后的路径
            return mapReturn;
        }
        catch (Exception e) {
            sftpTools.close();// 关闭登录
            String errorInfo = "产品：" + prodName + "(" + prodId + ") " + "估值表余额表生成对账文件失败："
                               + SysDataUtil.getNameFromDataDict(DictCode.文件类型, valBalFlag) + "上传sftp失败。";
            logger.error(errorInfo + "错误信息：" + e.getMessage());
            mapReturn.put(errorKey, errorInfo);// 错误信息
            mapReturn.put(pathKey, null);// 上传SFTP后的路径
            return mapReturn;
        }
        finally {
            // 删除存放的临时文件
            File file = new SafelyFile(outputFilepathTemp);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }

    }

    /**
     * 获取模板文件（自定义路径/文件名（含后缀名））
     * 
     * @param schemeType 对账类型
     * @return 模板文件
     * @since 1.0
     */
    private String getTempFile(String schemeType) {
        String tempName = null;
        if (DictCodeList.文件类型_估值表.getValue().equals(schemeType)) {
            tempName = VALUATION_TEMP_FILE;
        }
        else if (DictCodeList.文件类型_余额表.getValue().equals(schemeType)) {
            tempName = BALANCE_TEMP_FILE;

        }
        return tempName;
    }

    /**
     * 字符串中,["\"]替换为["/"]
     * 
     * @param value
     * @return
     * @see
     * @since 1.0
     */
    public String replaceStringForPath(String value) {

        if (StringUtil.isNotEmpty(value)) {
            value = value.replaceAll("\\\\", "/");
        }

        return value;
    }

    /**
     * 对比结果调整
     * 
     * @param schemeType 对账类型
     * @return 结果
     * @throws Exception 错误
     * @since 1.0
     */
    public void doAdjust(List<ConfirmPaDto> paDtoList) {
        // 循环进行更新
        for (ConfirmPaDto dto : paDtoList) {
            BokValbalTableStatus bokValbalTableStatus = new BokValbalTableStatus();
            // 主键
            bokValbalTableStatus.setFinprodId(dto.getFinprodId());
            bokValbalTableStatus.setValDate(this.DateStringToDate(dto.getValDateString()));
            bokValbalTableStatus.setValBalFlag(dto.getValBalFlag());
            /// 状态更新
            bokValbalTableStatus.setCheckStatus(DictCodeList.对账状态_已对账.getValue());// 对账状态
            bokValbalTableStatus.setCheckResult(DictCodeList.对账结果_对账一致.getValue());// 对账结果
            // 更新操作
            executeMapper.updateBokValbalTableStatusAndResult(bokValbalTableStatus);
        }
    }

    /**
     * 获取产品对应的账套信息
     * 
     * @param prodId 产品代码
     * @return
     * @see
     * @since 1.0
     */
    public ValAssetbook001Dto getBooksetInfo(String prodId) {
        ValAssetbook001Dto bookdto = mapper.queryBooksetId(prodId);
        return bookdto;
    }

    /**
     * 字符串转日期
     * 
     * @param DateString （2018-10-20）格式字符串
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    public Date StringToDate(String DateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date result = null;
        try {
            result = sdf.parse(DateString);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(),e);
        }

        return result;
    }

    /**
     * map中Key和value互换（针对key和value中都不存在重复值）
     * 
     * @param mapOld
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, String> mapKeyValueConvert(Map<String, String> mapOld) {

        Map<String, String> newMap = null;

        if (mapOld != null && !mapOld.isEmpty()) {
            newMap = new HashMap<String, String>();

            // key value 位置互换
            for (String key : mapOld.keySet()) {
                newMap.put(mapOld.get(key), key);
            }
        }

        return newMap;
    }

    /**
     * 在服务器上生成估值表或余额表，并且返回文件生成信息（针对本方估值表余额表文件）
     * 
     * @param booksetId
     * @param valDate
     * @param schemeType 对账类型[估值表，余额表]
     * @return
     * @throws IOException
     * @see
     * @since 1.0
     */
    public ExportFileDto getMyFileInfo(String booksetId, String valDate, String schemeType)
        throws IOException {

        // 账套信息
        BokBookset bokBookset = bokBooksetMapper.selectByPrimaryKey(booksetId);

        // 金融产品信息
        FinProduct finprod = this.getFinProductByBookset(booksetId);

        // 导出文件信息
        ExportFileDto exportFileDto = null;
        // 获取导出文件信息
        if (DictCodeList.文件类型_估值表.getValue().equals(schemeType)) {
            exportFileDto = valTableGenerate001Service.createExcel(booksetId, valDate, bokBookset.getBooksetFullname());
        }
        else if (DictCodeList.文件类型_余额表.getValue().equals(schemeType)) {
            exportFileDto = balTableGenerate001Service.createExcel(booksetId, valDate, finprod.getFinprodId(),
                finprod.getFinprodName());

        }

        return exportFileDto;
    }

    /**
     * 通过账套获取对应资产名称和资产代码
     * 
     * @param String
     * @return
     * @see
     * @since 1.0
     */
    public FinProduct getFinProductByBookset(String booksetId) {
        BokBookset bokBookset = bokBooksetMapper.selectByPrimaryKey(booksetId);
        /*
         * FinProductKey key = new FinProductKey(); // 账套的核算主体代码关联金融产品标的表的金融产品代码 key.setFinprodId(bokBookset.getAcctSubjectId()); // 金融产品类型=理财产品
         * key.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
         */
        return finProductMapper.selectByPrimaryKey(bokBookset.getAcctSubjectId());
    }

    /**
     * MAP转实体类
     * 
     * @param map
     * @param clazz
     * @return
     * @see
     * @since 1.0
     */
    public Object map2Object(Map<String, Object> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }

        Object obj = null;

        try {
            obj = clazz.newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();

            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

        return obj;
    }

}

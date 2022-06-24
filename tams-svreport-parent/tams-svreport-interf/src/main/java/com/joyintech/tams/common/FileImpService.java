/*
 * 文件名：FileImpService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2019年4月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.common;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import com.joyintech.framework.file.SafelyFile;
import com.joyintech.tams.common.svreport.datacheck.beforeinsert.dto.ChbCheckTableInfo;
import com.joyintech.tams.common.svreport.datacheck.beforeinsert.service.CommonCodeCheckBatchService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.report.interf.common.constant.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ReflectionUtils;
import org.xml.sax.SAXException;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.ChbFileReadMonitorInfoMapper;
import com.joyintech.tams.common.base.entity.ChbFileReadMonitorInfo;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.common.svreport.datacheck.dao.ChbInterfErrodataMapper;
import com.joyintech.tams.common.svreport.datacheck.dto.ChbInterfErrodataWithBLOBs;
import com.joyintech.tams.common.svreport.datacheck.dto.CheckRule;
import com.joyintech.tams.common.svreport.datacheck.service.DataCheckService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.TxtParseUtil;
import com.joyintech.tams.report.common.XmlParseUtil;
import com.joyintech.tams.report.common.util.ReportGlobalConstant;
import com.joyintech.tams.report.common.util.SysDataUtil;
import com.joyintech.tams.report.framework.util.FtpUtil;
import com.joyintech.tams.report.interf.common.constant.InterfConstants;
import com.joyintech.tams.report.interf.common.imp.ImportGenerator;
import com.joyintech.tams.report.interf.common.imp.sqlldr.ImpColumn;
import com.joyintech.tams.report.interf.common.service.InterfService;
import com.joyintech.tams.report.interf.common.util.InterfUtil;
import com.joyintech.tams.report.interf.common.util.SftpUtil;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
import com.joyintech.tams.systemtools.util.CommonUtil;

/**
 * 外部数据文件接口基类
 * @author chenxc
 * @version 1.0 2019年4月30日
 * @see FileImpService
 * @since 1.7
 */
public abstract class FileImpService extends InterfService{

//    /**
//     * 维护表常量
//     */
//    private static final String[] XML_FILE_NAME = new String[] {"CHB_EQU_HOLD", "CHB_EQUDET","CHB_INVESTHOLD_INFO","MST_EXCHANGE_INFO", "MKT_HOLIDAY"
//            ,"CHB_PRD_BASEINFO", "CHB_PRD_DEAL", "BTA_CLIENT_AREA_"};

    /**
     * 募集总量接口临时表名
     */
    protected  String  raisetotTableName="CHB_PRD_RAISETOT_SUB_TMP";
    /**
     * 申报接口临时表名
     */
    protected  String  issueTableName="CHB_PRD_ISSUE_SUB_TMP";
    /**
     * 交易接口临时表名
     */
    protected  String  botPosTableName="CHB_BOT_AST_POS_SUB_TMP";

    /**
     * 产品交易接口临时表名
     */
    protected String investTradeTableName = "CHB_INVEST_TRADE_SUB_TMP";

    /**
     * 资产估值信息接口临时表名
     */
    protected String astValInfoTableName = "CHB_AST_VAL_SUB_TMP";

    /**
     * 存续交易接口临时表名
     */
    protected String dealTableName = "CHB_PRD_DEAL_SUB_TMP";

    /**
     * 产品申报接口临时表名
     */
    protected String declareTableName = "CHB_PRD_DECLARE_SUB_TMP";


    /**
     * 产品终止登记接口临时表名
     */
    protected String prdendTableName = "CHB_PRD_END_SUB_TMP";

    /**
     * 资产负债接口临时表
     */
    protected String chbAsseTableName = "CHB_ASSET_INFO_TMP";

    @Autowired
    private ChbFileReadMonitorInfoMapper chbFileReadMonitorInfoMapper;

    @Autowired
    private DataCheckService dataCheckService;

    @Autowired
    private CommonCodeCheckBatchService commonCodeCheckBatchService;

    /**
     * mybatis的sqlsession管理核心
     */
    @Autowired
    private SqlSessionTemplate template;
    /**
     * 数据批量一次性insert条数(单条数据字段较多)
     */
    @Value("${application.reportInsertLongCount}")
    private int reportInsertLongCount;
    /**
     * 上游接口文件编码格式
     */
    @Value("${application.reportInterfFileEncoding}")
    protected String reportInterfFileEncoding;

    /**
     * 上游接口解析分割符
     */
    @Value("${application.reportInterfSplitCharacter}")
    protected String reportInterfSplitCharacter;

    /**
     * 上游接口文件是否需要.ok文件
     */
    @Value("${application.reportInterfIsNeedOkFile}")
    protected String reportInterfIsNeedOkFile;

    /**
     * 传输完成标志文件扩展名
     */
    @Value("${application.reportInterfOkFileExtension}")
    protected String reportInterfOkFileExtension;

    public List<InterfFile> queryAndDownloadFiles(String currentDate,List<String> containsFile){

        return queryAndDownloadFiles(currentDate,containsFile,reportInterfIsNeedOkFile);
    }
    /**
     * 查看并下载目录
     * @param currentDate 日期目录
     * @param containFile 传文件名规范
     * @param ifneedOk 是否需要ok文件 01需要 02不需要
     * @return
     */
    public List<InterfFile> queryAndDownloadFiles(String currentDate,List<String> containsFile,String ifneedOk) {

        List<InterfFile> filesVectorAll = new ArrayList<InterfFile>();

        Map<String, String> mapPath = SysDataUtil.getSysDictDataMap(DictCode.数据来源);
        Iterator<Map.Entry<String, String>> entries = mapPath.entrySet().iterator();
        // ftpType
        String ftpType = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY,
                ReportGlobalConstant.SYS_PARA_FTP_TYPE);
        //ftp url
        String url = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY,ReportGlobalConstant.SYS_PARA_SALES_FTP_IP);
        // PORT
        String port = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY,ReportGlobalConstant.SYS_PARA_SALES_FTP_PROT);
        // ftp连接用户名
        String username = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY,ReportGlobalConstant.SYS_PARA_SALES_FTP_USER);
        // ftp密码
        String password = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY, ReportGlobalConstant.SYS_PARA_SALES_FTP_PASSWORD);
        //临时文件存放目录
        String localPath = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY, GlobalConstant.COM_PROP_ACCESS_EXPORTPATH);
        //清理本地临时文件目录
        logger.info("文件列表：{},是否需要ok:{}",StringUtil.joinString(containsFile, GlobalConstant.COMMA),ifneedOk);
        logger.info("清理本地临时文件目录：{}",localPath);
        InterfUtil.deleteLocalTmpFile(localPath,containsFile);
        String type;
        while (entries.hasNext()) {
            Map.Entry<String, String> entry_type = entries.next();
            type = entry_type.getKey();
            List<InterfFile> filesVector = new ArrayList<InterfFile>();
            String isOpen = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY,ReportGlobalConstant.DICT_INTERF_IMPORT_SWITCH);
            if (DictCodeList.是否_中债_是.getValue().equals(isOpen)) {
                try{
//            		//遍历解析文件名称
//        			List<String> containsFile = new ArrayList<String>();
//        			Map<String, String> mapPathName = SysDataUtil.getSysDictDataMap(ReportDictCode.上游接口文件命名规范);
//        			Iterator<String> iter = mapPathName.keySet().iterator();
//        			while(iter.hasNext()){
//        				containsFile.add(iter.next());
//        			}
                    //参数对应目录
                    String remotePath = this.getParaVauleByparaCode(type);
                    //实际取数目录
                    String finalPath = remotePath+currentDate;
                    logger.info("实际取数目录："+finalPath);
                    if(StringUtil.equals(ftpType, "03")){
                        //本地
                        File finalDir = new SafelyFile(finalPath);
                        if (finalDir.exists() && finalDir.isDirectory()) {
                            Collection<File> files = FileUtils.listFiles(finalDir,
                                    null, false);
                            for (File ff : files) {
                                String fileName = ff.getName();
                                if(fileName.contains("ok")){
                                    continue;
                                }
                                if (containsFile == null || CollectionUtils.exists(containsFile, name -> {
                                    return fileName.contains((String)name);
                                })) {
                                    File lf = new SafelyFile(localPath + fileName);
                                    if (!lf.exists()) {
                                        // 本地文件夹不存在先创建文件夹 add by zcw
                                        File saveFileDir = new File(localPath);
                                        if (!saveFileDir.exists()) {
                                            saveFileDir.mkdirs();
                                        }
                                        lf.createNewFile();
                                        FileUtils.copyFile(ff, lf);
//                                        logger.info("下载文件：{}成功！",localPath + fileName);
//                                        File lf1 = new File("F:/home/tamsapp/file1/"+ fileName);
//                                        FileUtils.copyFile(lf, lf1);
                                    }
                                    InterfFile ifile = new InterfFile();
                                    ifile.setFile(lf);
                                    ifile.setFtpType(ftpType);
                                    ifile.setFilename(fileName);
                                    ifile.setUrl(url);
                                    ifile.setPassword(password);
                                    ifile.setUsername(username);
                                    ifile.setPort(Integer.parseInt(port));
                                    ifile.setLocalPath(localPath);
                                    ifile.setRemotePath(finalPath);
                                    // 数据来源
                                    ifile.setDataSource(type);
                                    filesVector.add(ifile);
                                }
                            }
                        }
                    }
                    else if(StringUtil.equals(ftpType, "02")){
                        Vector<?> vector = SftpUtil.getSFtpFile(username, url, Integer.parseInt(port), password,finalPath);
                        if (null != vector) {
                            for (Object item : vector) {
                                LsEntry entry = (LsEntry)item;
                                String fileName = entry.getFilename();
                                if (containsFile == null || CollectionUtils.exists(containsFile, name -> {
                                    return fileName.contains((String)name);
                                })) {
                                    File lf = new SafelyFile(localPath + fileName);
                                    if (!lf.exists()) {
                                        SftpUtil.downloadSftp(username, url, Integer.parseInt(port), password,finalPath, entry.getFilename(), localPath);
                                    }
                                    InterfFile ifile = new InterfFile();
                                    ifile.setFile(lf);
                                    ifile.setFtpType(ftpType);
                                    ifile.setFilename(fileName);
                                    ifile.setUrl(url);
                                    ifile.setPassword(password);
                                    ifile.setUsername(username);
                                    ifile.setPort(Integer.parseInt(port));
                                    ifile.setLocalPath(localPath);
                                    ifile.setRemotePath(finalPath);
                                    // 数据来源
                                    ifile.setDataSource(type);
                                    filesVector.add(ifile);
                                }
                            }
                        }
                    }
                    else if(StringUtil.equals(ftpType, "01")){
                        //FTP
                        List<File> lfiles = FtpUtil.downXMLFile(url, Integer.parseInt(port), username, password, finalPath,
                                localPath, containsFile);
                        for(File file : lfiles){
                            InterfFile ifile = new InterfFile();
                            ifile.setFile(file);
                            ifile.setFtpType(ftpType);
                            ifile.setFilename(file.getName());
                            ifile.setUrl(url);
                            ifile.setPassword(password);
                            ifile.setUsername(username);
                            ifile.setPort(Integer.parseInt(port));
                            ifile.setLocalPath(localPath);
                            ifile.setRemotePath(finalPath);
                            // 数据来源
                            ifile.setDataSource(type);
                            filesVector.add(ifile);
                        }
                    }
                    /*
                     * 对下载成功后的文件做处理，具体处理逻辑：
                     * 1、若需要.ok文件，则将下载后的文件与.ok文件进行比较，存在即保留，否则丢弃
                     * 2、若不需要.ok文件，则直接返回
                     */
                    filesVector = dealFiles(filesVector, type, ftpType, currentDate,ifneedOk);
                    if(filesVector!=null)
                        filesVectorAll.addAll(filesVector);
                }catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }

            }
        }
        // 去除不符合.txt和.xml的文件
        Iterator<InterfFile> it = filesVectorAll.iterator();
        while(it.hasNext()){
            InterfFile x = it.next();
            if (StringUtil.isNotEmpty(x.getFilename())){
                // 截取文件后缀名
                String suffxFileName = x.getFilename().substring(x.getFilename().lastIndexOf(GlobalConstant.POINT)+1);
                if ("txt".equalsIgnoreCase(suffxFileName)
                        || "xml".equalsIgnoreCase(suffxFileName)||suffxFileName.contains(x.getFilename())){
                    continue;
                } else{
                    logger.info("文件名类型不为.xml|.txt不解析！");
                    it.remove();
                }
            } else{
                logger.info("文件名不存在不进行解析！");
                it.remove();
            }
        }
        return filesVectorAll;
    }


    /**
     * 将文件解析为list字符串
     * @param xmlFile
     * @param remotePath
     * @return map
     * 				dataList -> 数据集合
     * 				remoteDir -> 处理成功或失败 success/fail
     * @throws FileNotFoundException
     */
    public Map<String, Object> parseFile(InterfFile xmlFile) throws FileNotFoundException{
        Map<String, Object> map = new HashMap<String,Object>();
        String remoteDir = "success";
        List<Map<String, String>> infoList = null;
//    	List<Map<String, String>> infoVectorFinal = new ArrayList<Map<String, String>>();
//    	for (InterfFile filetemp : xmlFiles) {
        File file = xmlFile.getFile();
        String realName = file.getName();
        String suffix = FileNameUtil.getFileExtName(realName);
        // 根据文件类型设置扩展名
        if (InterfConstants.CONTENTSRC_FILE_XML.equals(xmlFile.getFileType())){
            if(suffix.contains("BTA_CLIENT_AREA")){
                suffix = "BTA_CLIENT_AREA";
            }else{
                suffix = "xml";
            }
        } else if (InterfConstants.CONTENTSRC_FILE_TXT.equals(xmlFile.getFileType())){
            if(suffix.contains("BTA_CLIENT_AREA")){
                suffix = "BTA_CLIENT_AREA";
            }else{
                suffix = "txt";
            }
        } else {
            if(suffix.contains("BTA_CLIENT_AREA")){
                suffix = "BTA_CLIENT_AREA";
            }else{
                logger.info("数据库配置数据来源配置错误，应配置为T|X！");
                remoteDir = "fail";
                suffix = "NULL";
            }
        }

//            Collection<File> files = FileUtils.listFiles(file.getParentFile(),
//                    null, false);
//            logger.info("临时文件目录：{}",file.getParentFile().getAbsolutePath());
//            if (CollectionUtils.isNotEmpty(files)){
//                for (File f :files){
//                    logger.info("临时文件目录：文件{}",f.getAbsolutePath());
//                }
//            }
        InputStream in = new FileInputStream(file);

        // 将文件中的特殊字符传转换成转义字符 by caozhenming 20211022 start
        // 将输入流转换成字符串
        String inputString = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        String xmlString = inputString.replaceAll("&","&amp;");
        // 将字符串转换成输入流
        in = new ByteArrayInputStream(xmlString.getBytes());
        // 将文件中的特殊字符传转换成转义字符 by caozhenming 20211022 end
        // 解析处理开始前，登录监控信息表，状态处理中
        ChbFileReadMonitorInfo monitorInfoDto = new ChbFileReadMonitorInfo();
        monitorInfoDto.setHandleSeqno(
                CommonUtil.getSequenceIndexData(DataIndexSequenceType.上游文件读取处理监控序列号));
        monitorInfoDto.setHandleFileName(realName);
        monitorInfoDto.setHandleFilePath(xmlFile.getRemotePath());
        monitorInfoDto.setFileReadStartTime(new Date());
        monitorInfoDto.setFileHandleStatus(DictCodeListExt2.上游接口文件读取处理状态_处理中.getValue());

        // 如果没有给接口类型则不做文件监控
        if (StringUtil.isNotEmpty(xmlFile.getInterfType())){// 添加监控
            monitorInfoDto.setHandleFileType(xmlFile.getInterfType());
        } else {
            monitorInfoDto.setHandleFileType("NOFILETYPE");
        }

        try {
            if ("xml".equalsIgnoreCase(suffix)) {
                try {
                    infoList = XmlParseUtil.parse(in);
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    logger.error(e.getMessage(),e);
                    remoteDir = "fail";
                }
            } else if ("txt".equalsIgnoreCase(suffix)) {
                try {
                    infoList = TxtParseUtil.parse(in, reportInterfFileEncoding, reportInterfSplitCharacter);
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    logger.error(e.getMessage(),e);
                    remoteDir = "fail";
                }
            }else{
                try {
                    byte[] b = { 0x01, 0x01 };
                    String splitCharacter = new String(b);
                    infoList = TxtParseUtil.parseTxtFx(in, reportInterfFileEncoding, splitCharacter);
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                    remoteDir = "fail";
                }
            }
            if(infoList!=null)
            {
                monitorInfoDto.setFileDataCount(new BigDecimal(infoList.size()));
            }

            chbFileReadMonitorInfoMapper.insert(monitorInfoDto);
            this.handleMonitorInfoAfterMainService(monitorInfoDto, remoteDir);
        }
        catch (Exception e) {
            logger.error("上游接口数据处理失败：", e);
            this.handleMonitorInfoAfterException(monitorInfoDto, e);
            remoteDir = "fail";
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }

        map.put("dataList",  this.copyDataList(infoList));
        map.put("remoteDir", remoteDir);

//        }
        return map;
    }

    /**
     * 数据校验
     * @param infoList 解析的数据list
     * @param fireName 文件名称
     * @param interfType 接口类型
     * @param dataType 检查数据类型
     * @param checkEuplicatedKeys 重复性检查的keys
     * @Param bussinessKey 业务主键用于关联错误信息
     * @return 返回待处理数据
     */
    public List<Map<String, String>> checkData(boolean deleteFlag, List<Map<String,String>> infoList,String fireName,
                                               String interfType,String dataType,String bussinessKey,String... checkEuplicatedKeys ){
        /**start 新增删除逻辑  add  by  dongxiangyang 20201017    */
        //根据业务主键和类型进行删除
        List<String>  listMap=infoList.stream().map(map -> {
            if(StringUtil.isEmpty(bussinessKey) || StringUtil.isEmpty(map.get(bussinessKey))) {
                StringBuffer keyVFinal = new StringBuffer();
                for(String key: checkEuplicatedKeys){
                    keyVFinal.append(map.get(key));
                    keyVFinal.append("|");
                }
                return keyVFinal.toString();
            }
            return map.get(bussinessKey);
        }).collect(Collectors.toList());
        deletAllErroData(listMap, interfType);
        /**end 新增删除逻辑  add  by  dongxiangyang 20201017    */
        List<Map<String, String>> listDupError = DataCheckService.checkDuplicatedData(infoList, checkEuplicatedKeys);
        //总部或分支机构信息登记表SUB双独立业务主键重复校验特殊处理
        if(dataType.equals("CHB_SALER_INST_SUB")) {
            List<Map<String, String>> SalerInsNameListError = DataCheckService.checkDuplicatedData(infoList, new String[]{"SALER_ORG_NAME"});
            if(SalerInsNameListError.size() != 0){
                for (Map<String, String> erro : SalerInsNameListError) {
                    listDupError.add(erro);
                }
            }
        }
        // add by zhangliang 20210418 TMP调用SUB校验配置 start
        List<CheckRule> listRules = dataCheckService.queryCheckRules(ChbCheckTableInfo.genDataType(dataType));
        // add by zhangliang 20210418 TMP调用SUB校验配置 end
        List<Map<String, String>> listError = DataCheckService.checkDataList(infoList,listRules, deleteFlag);
        // 将重复项入库
        if (deleteFlag && CollectionUtils.isNotEmpty(listError)){
            listDupError.addAll(listError);
        }
        ChbInterfErrodataWithBLOBs data=null;
//        StringBuffer bf=null;
        List<ChbInterfErrodataWithBLOBs> erroList=new ArrayList<ChbInterfErrodataWithBLOBs>();
        for(Map<String, String> erro:listDupError){
//            data=new ChbInterfErrodataWithBLOBs();
//            String dataId = null;
//            if(erro.get(bussinessKey) == null || "".equals(erro.get(bussinessKey))){
//                //若根据业务主键找不到值，则根据checkEuplicatedKeys联合主键来进行手工md5加密，并赋值
//                StringBuffer keyVFinal = new StringBuffer();
//                for(String key: checkEuplicatedKeys){
//                    keyVFinal.append(erro.get(key));
//                    keyVFinal.append("|");
//                }
////                String md5Key = DigestUtils.md5Hex(keyVFinal.toString());
//                dataId = keyVFinal.toString();
//            }else{
//                //若根据业务主键获取到值，则直接赋值
//                dataId = erro.get(bussinessKey);
//            }
//            data.setDatauuid(IDGenerator.generate(36));
////            data.setDataid(erro.get(bussinessKey));
//            // 根据检查的数据类型设置错误信息的interfType
//            data.setInterfType(dataType);
//            data.setFireName(fireName);
////            bf=new StringBuffer();
//            String msg=erro.get("ERRMSG");
////            for(String key:erro.keySet()){
////                if(key==null){
////                    continue;
////                }
////                if("ERRMSG".equals(key)){
////                    msg=erro.get(key);
////                }else{
////                    bf.append(key+":"+erro.get(key)+",");
////
////                }
////            }
//            erro.remove("ERRMSG");
//            data.setOrgData(JsonUtils.object2Json(erro));
//            data.setErrMsg(msg);
//            data.setStatus("E");
//            data.setCreateUser("system");
//            data.setUpdateUser("system");
//            data.setCreateTime(new Date());
//            data.setUpdateTime(new Date());
            data = createChbInterfErrdata(fireName, interfType, erro, bussinessKey, checkEuplicatedKeys);
            if(data != null) {
                erroList.add(data);
            }
        }
        if(!erroList.isEmpty()){
            insertErroData(erroList);
        }
//        chbInterfErrodataMapper.deleteByPrimaryKey(data.getDataid());
//        chbInterfErrodataMapper.insert(data);
        return listError;
    }

    /**
     * 根据错误消息创建需要入库的Dto
     *
     * @param tableName 检查的表名
     * @param map 数据
     * @param dataId 业务主键
     * @return 需要入库的Dto
     */
    public ChbInterfErrodataWithBLOBs createChbInterfErrdata(String fireName,
                                                             String interfType, Map<String, String> map,
                                                             String bussinessKey, String... checkEuplicatedKeys) {
        String msg = map.get("ERRMSG");
        if (StringUtil.isEmpty(msg)) {
            return null;
        }
        String dataId = null;
        if(StringUtil.isEmpty(bussinessKey) || StringUtil.isEmpty(map.get(bussinessKey))){
            //若根据业务主键找不到值，则根据checkEuplicatedKeys联合主键来进行赋值
            StringBuffer keyVFinal = new StringBuffer();
            for(String key: checkEuplicatedKeys){
                keyVFinal.append(map.get(key));
                keyVFinal.append("|");
            }
            dataId = keyVFinal.toString();
        }
        else {
            dataId = map.get(bussinessKey);
        }
        // 拼装错误信息
        ChbInterfErrodataWithBLOBs errodata = new ChbInterfErrodataWithBLOBs();
        // 主键uuid
        errodata.setDatauuid(IDGenerator.generate(36));
        // 文件名
        errodata.setFireName(fireName);
        // 业务主键
        errodata.setDataid(dataId);
        // 接口类型
        errodata.setInterfType(interfType);
        // 拼接错误字段信息
        map.remove("ERRMSG");
        errodata.setOrgData(JsonUtils.object2Json(map));
        // 错误提示信息
        errodata.setErrMsg(msg);
        // 数据状态
        errodata.setStatus("E");
        errodata.setCreateUser("system");
        errodata.setUpdateUser("system");
        errodata.setCreateTime(new Date());
        errodata.setUpdateTime(new Date());
        return errodata;
    }

    /**
     * 数据校验
     * @param infoList 解析的数据list
     * @param fireName 文件名称
     * @param interfType 接口类型
     * @param dataType 检查数据类型
     * @param checkEuplicatedKeys 重复性检查的keys
     * @Param bussinessKey 业务主键用于关联错误信息
     * @return 返回待处理数据
     */
    public List<Map<String, String>> checkData(List<Map<String,String>> infoList,String fireName,
                                               String interfType,String dataType,String bussinessKey,String... checkEuplicatedKeys ){
        return checkData(true, infoList, fireName, interfType, dataType, bussinessKey, checkEuplicatedKeys);
    }

    /**
     * 数据校验
     * @param infoList 解析的数据list
     * @param fireName 文件名称
     * @param checkTableInfo 接口类型
     * @return 返回待处理数据
     */
    public List<Map<String, String>> checkDataCodeCheck(List<Map<String,String>> infoList, String fireName, ChbCheckTableInfo checkTableInfo) {
        // 配置脚本校验
        checkData(false, infoList, fireName, checkTableInfo.getTableName(), checkTableInfo.getTableName(), checkTableInfo.getBussinessKey(), checkTableInfo.getCheckEuplicatedKeys());
        // 代码校验
        commonCodeCheckBatchService.codeCheckCommon(infoList, checkTableInfo, fireName);

        return infoList;
    }

    /**
     * 功能:数据入库
     * @since 1.7.0
     * @version 1.7.0
     * @author yuluan 2019年5月5日
     * @param assetInfoList 错误信息
     * @param
     */
    public void insertErroDataMerge(List<ChbInterfErrodataWithBLOBs> erroList) {
        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            int count = 0;
            logger.debug("错误信息数据插入开始--------------" + new Date());
            ChbInterfErrodataMapper batchMapper = session.getMapper(ChbInterfErrodataMapper.class);
            int batchCount = reportInsertLongCount==0?50:reportInsertLongCount;//每批commit的个数
            int batchLastIndex = batchCount;//每批最后一个的下标
            for(int index = 0; index < erroList.size();){
                if(batchLastIndex > erroList.size()-1){
                    batchLastIndex = erroList.size();
                    batchMapper.insertChbInterfErrodata(erroList.subList(index, batchLastIndex));
                    break;//数据插入完毕,退出循环
                }else{
                    batchMapper.insertChbInterfErrodata(erroList.subList(index, batchLastIndex));
                    index = batchLastIndex;//设置下一批下标
                    batchLastIndex = index + batchCount;
                }
                count ++;
                // 每20次提交
                if (count % 20 == 0) {
                    session.commit();
                    // 清理缓存，防止溢出
                    session.clearCache();
                }
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        catch (Exception e) {
            session.rollback();
            logger.error("SQL执行异常：", e);
        }
        finally {
            session.close();
        }
        logger.debug("错误信息数据插入结束--------------" + new Date());
    }

    public void insertErroData(List<ChbInterfErrodataWithBLOBs> erroList) {
        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            int count = 0;
            logger.debug("错误信息数据插入开始--------------" + new Date());
            ChbInterfErrodataMapper batchMapper = session.getMapper(ChbInterfErrodataMapper.class);
            /**start 新增删除逻辑  delete  by  dongxiangyang 20201017    */
//            int batchCount = reportInsertLongCount==0?50:reportInsertLongCount;//每批commit的个数
//            int batchLastIndex = batchCount;//每批最后一个的下标
//            for(ChbInterfErrodataWithBLOBs single: erroList){
//            	batchMapper.deleteByPrimaryKey(single.getDataid());
//            	 count ++;
//                 // 每20次提交
//                 if (count % 20 == 0) {
//                     session.commit();
//                     // 清理缓存，防止溢出
//                     session.clearCache();
//                 }
//            }
            /**end 新增删除逻辑  delete  by  dongxiangyang 20201017    */
            for(ChbInterfErrodataWithBLOBs single: erroList){
                batchMapper.insert(single);
                count ++;
                // 每20次提交
                if (count % 20 == 0) {
                    session.commit();
                    // 清理缓存，防止溢出
                    session.clearCache();
                }
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        catch (Exception e) {
            session.rollback();
            logger.error("SQL执行异常：", e);
        }
        finally {
            session.close();
        }
        logger.debug("错误信息数据插入结束--------------" + new Date());
    }

    /**start 新增删除逻辑  add  by  dongxiangyang 20201017    */
    public void deletAllErroData(List<String> erroList,String tableName) {
        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            int count = 0;
            logger.debug("错误信息数据删除开始--------------" + new Date());
            ChbInterfErrodataMapper batchMapper = session.getMapper(ChbInterfErrodataMapper.class);
            for(String single: erroList){
                Map<String,String> map  =new  HashMap<String,String>();
                map.put("tableName", tableName);
                map.put("dataid", single);
                batchMapper.deleteByPrimaryKeyType(map);
                count ++;
                // 每20次提交
                if (count % 20 == 0) {
                    session.commit();
                    // 清理缓存，防止溢出
                    session.clearCache();
                }
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        catch (Exception e) {
            session.rollback();
            logger.error("SQL执行异常：", e);
        }
        finally {
            session.close();
        }
        logger.debug("错误信息数据删除结束--------------" + new Date());
    }
    /**end 新增删除逻辑  add  by  dongxiangyang 20201017    */
//
//    protected void insertErroDataCodeCheck(List<ChbInterfErrodataWithBLOBs> erroList,String tableName) {
//        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
//        try {
//            int count = 0;
//            logger.debug("错误信息数据插入开始--------------" + new Date());
//            ChbInterfErrodataMapper batchMapper = session.getMapper(ChbInterfErrodataMapper.class);
//            /**start 新增删除逻辑  delete  by  dongxiangyang 20201017    */
////            int batchCount = reportInsertLongCount==0?50:reportInsertLongCount;//每批commit的个数
////            int batchLastIndex = batchCount;//每批最后一个的下标
////            for(ChbInterfErrodataWithBLOBs single: erroList){
////                batchMapper.deleteByPrimaryKeyType(tableName,single.getDataid());
////                 count ++;
////                 // 每20次提交
////                 if (count % 20 == 0) {
////                     session.commit();
////                     // 清理缓存，防止溢出
////                     session.clearCache();
////                 }
////            }
//            /**end 新增删除逻辑  delete  by  dongxiangyang 20201017    */
//            for(ChbInterfErrodataWithBLOBs single: erroList){
//                batchMapper.insert(single);
//                count ++;
//                // 每20次提交
//                if (count % 20 == 0) {
//                    session.commit();
//                    // 清理缓存，防止溢出
//                    session.clearCache();
//                }
//            }
//            session.commit();
//            // 清理缓存，防止溢出
//            session.clearCache();
//        }
//        catch (Exception e) {
//            session.rollback();
//            logger.error("SQL执行异常：", e);
//        }
//        finally {
//            session.close();
//        }
//        logger.debug("错误信息数据插入结束--------------" + new Date());
//    }

    protected void insertErroDataCodeCheck(List<ChbInterfErrodataWithBLOBs> erroList,String tableName) {
        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            int count = 0;
            logger.debug("错误信息数据插入开始--------------" + new Date());
            ChbInterfErrodataMapper batchMapper = session.getMapper(ChbInterfErrodataMapper.class);
//            int batchCount = reportInsertLongCount==0?50:reportInsertLongCount;//每批commit的个数
//            int batchLastIndex = batchCount;//每批最后一个的下标
//            for(ChbInterfErrodataWithBLOBs single: erroList){
//            	Map<String,String> map  =new  HashMap<String,String>();
//            	map.put("tableName", tableName);
//            	map.put("dataid", single.getDataid());
//            	batchMapper.deleteByPrimaryKeyType(map);
//            	 count ++;
//                 // 每20次提交
//                 if (count % 20 == 0) {
//                     session.commit();
//                     // 清理缓存，防止溢出
//                     session.clearCache();
//                 }
//            }
            for(ChbInterfErrodataWithBLOBs single: erroList){
                batchMapper.insert(single);
                count ++;
                // 每20次提交
                if (count % 20 == 0) {
                    session.commit();
                    // 清理缓存，防止溢出
                    session.clearCache();
                }
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        catch (Exception e) {
            session.rollback();
            logger.error("SQL执行异常：", e);
        }
        finally {
            session.close();
        }
        logger.debug("错误信息数据插入结束--------------" + new Date());
    }

    /**
     * 将文件入库
     * @param saveFlag:F－全量，I－增量
     * @param  dateSource 数据来源
     */
    public abstract String saveData(List<Map<String, String>> listData,String saveFlag,String dateSource);

    /**
     * 将最终处理好的文件进行入库，自行实现，包括但不限定于以下步骤：
     * @param listData 解析出的数据
     * @param interfFile 接口文件
     * 1、checkData --校验数据
     * 2、saveData --入库
     * @param fileLists
     */
    public abstract String importDB(List<Map<String, String>> listData,InterfFile interfFile, String date);

    public void clearFile(InterfFile interfFile){
        // ftp文件移动
        if (StringUtil.isNotEmpty(interfFile.getRemotePath())) {
            InterfUtil.moveFilesMulThread(interfFile.getFtpType(),interfFile.getUrl(),interfFile.getPort(),interfFile.getUsername(),interfFile.getPassword()
                    ,interfFile.getRemotePath(),interfFile.getFilename(),interfFile.getMoveRemovePath());
        }
        // 删除临时文件
        interfFile.getFile().delete();
        logger.info("删除文件：{}成功！",interfFile.getFile().getAbsolutePath());
    }


    /**
     * 文件读取主处理后的处理，根据remoteDir判断文件是否成功处理
     * 设置到读取上游文件监控信息dto
     *
     * @param monitorInfoDto 读取上游文件监控信息dto
     * @param remoteDir remoteDir
     * @see
     * @since 1.0
     */
    private void handleMonitorInfoAfterMainService(ChbFileReadMonitorInfo monitorInfoDto,
                                                   String remoteDir) {
        monitorInfoDto.setFileReadEndTime(new Date());
        if (StringUtil.equals(remoteDir, "success")) {
            monitorInfoDto.setFileHandleStatus(DictCodeListExt2.上游接口文件读取处理状态_正常结束.getValue());
        }
        else {
            monitorInfoDto.setFileHandleStatus(DictCodeListExt2.上游接口文件读取处理状态_异常结束.getValue());
            //add by liubin start 当异常在解析时未捕获时，即handLeResultMsg为空，则处理给个默认值
            if(monitorInfoDto.getFileHandleResultMsg() == null || "".equals(monitorInfoDto.getFileHandleResultMsg())){
                monitorInfoDto.setFileHandleResultMsg("数据文件有问题，已移动到fail文件夹下");
            }
            //add by liubin end

        }

        chbFileReadMonitorInfoMapper.updateByPrimaryKeyWithBLOBs(monitorInfoDto);
    }

    /**
     * 文件读取主处理发生异常后的处理
     *
     * @param monitorInfoDto 读取上游文件监控信息dto
     * @param e 异常
     * @see
     * @since 1.0
     */
    private void handleMonitorInfoAfterException(ChbFileReadMonitorInfo monitorInfoDto,
                                                 Exception e) {
        monitorInfoDto.setFileReadEndTime(new Date());
        monitorInfoDto.setFileHandleStatus(DictCodeListExt2.上游接口文件读取处理状态_异常结束.getValue());
        monitorInfoDto.setFileHandleResultMsg("异常信息：" + e.getMessage());
        chbFileReadMonitorInfoMapper.updateByPrimaryKeyWithBLOBs(monitorInfoDto);
    }

    /**
     * 对下载后的文件进行处理
     * 	若需要.ok文件 则将下载后的fileVectors文件与.ok文件进行匹配，判断是否需要入库处理
     * @param fileVectors
     * @param resouse 上游系统源类型
     * @param ftpType 文件类型 01 FTP 02 SFTP 03 本地
     * @param currentDate 日期
     * @param ifneedOk 是否需要ok文件 01需要 02 不需要
     * @return 处理后的文件列表
     */
    private List<InterfFile> dealFiles(List<InterfFile> fileVectors,String resouse,String ftpType,String currentDate,String ifneedOk){
        try {
            //参数对应目录
            String remotePath = this.getParaVauleByparaCode(resouse);
            //实际取数目录
            String finalPath = remotePath+currentDate;
            //ftp url
            String url = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY,ReportGlobalConstant.SYS_PARA_SALES_FTP_IP);
            // PORT
            String port = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY,ReportGlobalConstant.SYS_PARA_SALES_FTP_PROT);
            // ftp连接用户名
            String username = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY,ReportGlobalConstant.SYS_PARA_SALES_FTP_USER);
            // ftp密码
            String password = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY, ReportGlobalConstant.SYS_PARA_SALES_FTP_PASSWORD);
            //临时文件存放目录
            String localPath = SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY, GlobalConstant.COM_PROP_ACCESS_EXPORTPATH);
            if (SVReportGlobalConstant.IS_NEED_OK_01.equals(ifneedOk)) {
                // .ok标志文件处理
                List<String> okFileList = InterfUtil.downInterfOKFile(ftpType, url, Integer.parseInt(port), username,
                        password, finalPath, localPath, reportInterfOkFileExtension);
                if (StringUtil.isEmptyList(okFileList)) {
                    //若为空，则表明均不满足，直接返回null
                    return null;
                }

                for (int i = fileVectors.size() - 1; i >= 0; i-- ) {
                    if (!okFileList.contains(StringUtil.splitToList(GlobalConstant.POINT,
                            fileVectors.get(i).getFilename()).get(0))) {
                        fileVectors.remove(i);
                    }
                }

                if (StringUtil.isEmptyList(fileVectors)) {
                    return null;
                }
            }else{
                //不需要.ok文件 直接返回
                return fileVectors;
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        return fileVectors;
    }


    /**
     * 创建数据文件
     *
     * @param path 文件路径
     * @param baseinfoList 对象列表
     * @param columns 数据库表对应的列
     */
    public void createDataFile(String path, List<? extends BaseEntity> baseinfoList,List<ImpColumn> columns) {
        // 生成txt文件
        List<String> lines = new ArrayList<>(baseinfoList.size());
        baseinfoList.forEach(item -> lines.add(ImportGenerator.buildTxt(item, columns)));
        try {
            FileUtils.writeLines(
                    new SafelyFile(path + GlobalConstant.UNDER_LINE + UUID.randomUUID().toString() + ".tmp"),
                    "UTF-8", lines, true);
        }
        catch (IOException e) {
            e.getMessage();
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 功能:将表字段转换成属性字段
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年4月29日
     * @param str
     * @return
     * String
     */
    public String subBlank(String str) {
        if(str==null){
            return null;
        }
        String[] strarray = str.split("_");
        if(strarray.length == 0){
            return null;
        }
        String result = strarray[0];
        for (int i = 1; i < strarray.length; i++)
            result = result + (new StringBuilder()).append(Character.toUpperCase(strarray[i].charAt(0))).append(strarray[i].substring(1)).toString();
        return result;
    }

    public List<Map<String, String>> copyDataList(List<Map<String, String>> src){
        List<Map<String, String>> dest = new ArrayList<Map<String, String>>();
        if (CollectionUtils.isNotEmpty(src)){
            for( Map<String, String> map : src){
                Map<String,String> cmap= new HashMap<String,String>();
                for(String key :map.keySet()){
                    cmap.put(key, map.get(key));
                }
                dest.add(cmap);
            }
        }
        return dest;
    }

    /**
     *
     * @param dataList
     * @param clz
     * @return
     */
    public List<? extends BaseEntity> convertMapToEntity(List<Map<String, String>> dataList, Class clz) {
        // 返回对象list
        List<BaseEntity> rs = new ArrayList<>();

        for (Map<String, String> map : dataList) {
            BaseEntity baseEntity = null;
            try {
                baseEntity  = (BaseEntity) clz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            baseEntity.setStatus(DictCodeListExt1.维护数据状态_有效.getValue());
            for (String key : map.keySet()) {
                // 空数据和错误数据置为待处理
                if(key==null||key.equals("ERRMSG")){
                    baseEntity.setStatus(DictCodeListExt1.维护数据状态_待处理.getValue());
                    continue;
                }
                // 获取map值
                String value = map.get(key);
                // 反射通过字段名寻找字段
                Field field = ReflectionUtils.findField(clz, subBlank(key.toLowerCase()));

                if(field==null){
                    continue;
                }
                field.setAccessible(true);
                Type type = field.getGenericType();
                if("java.lang.String".equals(type.getTypeName())){
                    try {
                        field.set(baseEntity, value);
                    } catch ( IllegalAccessException e) {
                        logger.error(e.getMessage(),e);
                    }
                }else if ("java.math.BigDecimal".equals(type.getTypeName())) {
                    try {
                        field.set(baseEntity ,(value == null || "".equals(value)) ? null : new BigDecimal(value));
                    } catch ( IllegalAccessException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
                else if ("java.lang.Integer".equals(type.getTypeName())) {
                    try {
                        field.set(baseEntity ,(value == null || "".equals(value)) ? null : Integer.valueOf(value));
                    } catch (IllegalAccessException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
                else if ("java.lang.Short".equals(type.getTypeName())) {
                    try {
                        field.set(baseEntity, (value == null || "".equals(value)) ? null : Short.valueOf(value));
                    } catch ( IllegalAccessException e) {
                        logger.error(e.getMessage(),e);
                    }
                } else if ("java.lang.Long".equals(type.getTypeName())) {
                    try {
                        field.set(baseEntity, (value == null || "".equals(value)) ? null : Long.valueOf(value));
                    } catch ( IllegalAccessException e) {
                        logger.error(e.getMessage(),e);
                    }
                } else if ("java.util.Date".equals(type.getTypeName())) {
                    if (value != null&&""!=value) {
                        try {
                            field.set(baseEntity, DateUtil.parse(value));
                        } catch ( IllegalAccessException e) {
                            logger.error(e.getMessage(),e);
                        }
                    }
                } else{
                    try {
                        field.set(baseEntity, value);
                    } catch ( IllegalAccessException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
            // 创建人
            baseEntity.setCreateUser("interf");
            // 更新人
            baseEntity.setUpdateUser("interf");
            if(baseEntity.getCreateTime() == null){
                baseEntity.setCreateTime(new Date());
            }
            if(baseEntity.getUpdateTime() == null){
                baseEntity.setUpdateTime(new Date());
            }
            // 创建人
            baseEntity.setCreateDept(CommonUtil.getDeptCode());

            rs.add(baseEntity);
        }
        return rs;
    }

}

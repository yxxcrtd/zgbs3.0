/*
 * 文件名：ChbFeedbackMsgService.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年10月12日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.component.svrreport.chb.feedbackmsg.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.joyintech.background.component.common.pack.common.util.DateUtil;
import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.component.svrreport.chb.contant.ChbConstant;
import com.joyintech.component.svrreport.chb.contant.DictCode;
import com.joyintech.component.svrreport.chb.contant.DictCodeList;
import com.joyintech.component.svrreport.chb.dto.*;
import com.joyintech.component.svrreport.chb.feedbackmsg.dao.ChbFeedbackFileComponentMapper;
import com.joyintech.component.svrreport.chb.feedbackmsg.dao.ChbFeedbackMsgComponentMapper;
import com.joyintech.component.svrreport.chb.feedbackmsg.dto.ChbFeedbackFile;
import com.joyintech.component.svrreport.chb.msglog.dao.ChbComponentMsgLogMapper;
import com.joyintech.component.svrreport.chb.msglog.service.ChbComponentMsgLogService;
import com.joyintech.component.svrreport.chb.msgsend.dto.ExtFprDClient;
import com.joyintech.component.svrreport.chb.util.ChbXmlUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * 中债反馈信息处理
 * @author 王波
 * @date 2020年10月12日
 * @see ChbFeedbackMsgService
 * @since 1.0
 */
@Service
public class ChbFeedbackMsgService {
    private static final Logger logger = LoggerFactory.getLogger(ChbFeedbackMsgService.class);

    @Value("${application.chb.msg-start-time:0600}")
    private String msgStartTime;
    /***配置系统参数需要以  msg-start-time: '0600' 进行配置***/

    @Value("${application.chb.msg-end-time:2200}")
    private String msgEndTime;

    @Autowired
    private ExtFprDClient dclientComponent;

    @Value("${application.chb.download-file-path:}")
    private String downloadFilePath;

    @Value("${application.chb.reportCbFeedBackHandleCountOneTime:10000}")
    private int reportCbFeedBackHandleCountOneTime;

    /**
     * 日志状态处理
     */
    @Autowired
    private ChbComponentMsgLogService chbComponentMsgLogService;

    @Autowired
    private ChbComponentMsgLogMapper chbComponentMsgLogMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 没多少次提交一次
     */
    @Value("${application.chb.maxSqlNum:1000}")
    private int maxSqlNum;

    /**
     * 中债反馈信息Mapper
     */
    @Autowired
    private ChbFeedbackMsgComponentMapper chbFeedbackMsgComponentMapper;

    /**
     * 中债反馈文件下载获取方式(01：DI获取   02：本地获取)
     */
    @Value("${application.chb.diDirectAccessFlg:01}")
    private String diDirectAccessFlg;

    /**
     * 中债一二三期部署方式(01:独立一二期  03：独立三期  00：一起部署)
     */
    @Value("${application.chb.chbPerNo:00}")
    private String chbPerNo;

    @Value("${application.chb.download-file-path-movepath:}")
    private String downloadFilePathMovePath;

    /**
     * ChbFeedbackFileComponentMapper
     */
    @Autowired
    private ChbFeedbackFileComponentMapper chbFeedbackFileComponentMapper;

    /**
     * 一二期报表定义
     */
    private static List<String> oneTwoReportList = Arrays.asList(DictCodeList.报表名称_产品申报登记.getValue(), DictCodeList.报表名称_产品发行登记.getValue(),
            DictCodeList.报表名称_机构和私人银行产品发行登记.getValue(),DictCodeList.报表名称_募集总量登记.getValue(),DictCodeList.报表名称_产品存续期登记.getValue(),
            DictCodeList.报表名称_产品终止登记.getValue(),DictCodeList.报表名称_资产要素信息登记.getValue(),DictCodeList.报表名称_资产交易信息登记.getValue(),
            DictCodeList.报表名称_资管计划底层持仓信息登记.getValue(),DictCodeList.报表名称_资产估值信息登记.getValue(),DictCodeList.报表名称_从业人员登记.getValue());

    /**
     * 三期报表定义
     */
    private static List<String> threeReportList = Arrays.asList(DictCodeList.报表名称_投资者身份信息登记.getValue(),
            DictCodeList.报表名称_投资者持有信息登记.getValue(),DictCodeList.报表名称_业务明细信息登记.getValue());


    /**
     * 查询本地待处理反馈文件列表
     * @return
     */
    public List<ChbFeedbackFile> queryChbFeedbackFileList(String rtnRepMesNo){
        return chbFeedbackFileComponentMapper.queryChbFeedbackFileList(DictCodeList.中债一二三期文件处理状态_未处理.getValue(), rtnRepMesNo);
    }

    /**
     * 查询异地待处理反馈文件列表
     * @return
     */
    public List<ChbInfoReportDto> selectReportNoByRepMesNo(String repMesNo){
        return chbComponentMsgLogMapper.selectReportNoByRepMesNo(repMesNo);
    }

    public List<String> queryChbOrgRegistBankCodeList(){
        return chbFeedbackMsgComponentMapper.queryChbOrgRegistBankCodeList();
    }

    /**
     * 获取产品登记编码用
     *
     * @param asynReportEntity 返回信息
     * @param fdc 中债客户端
     * @param sysDate 业务日期
     * @see
     * @since 1.0
     */
    public void getProdRecode(AsynReport40Entity asynReportEntity, Date sysDate) {
        // 下载文件地址
    	String filePath = getDownloadFilePath();
        // 文件ID
        String fileId = asynReportEntity.getFileId();
        // 确认文件名
        String fileName = asynReportEntity.getFileName();
        InterfLog log = new InterfLog();
        log.setInterfId(DictCodeList.接口系统ID_获取产品登记编码.getValue());
        log.setBizDate(DateUtil.format(sysDate, DateUtil.DEFAULT_OTHER_FORMAT));
        chbComponentMsgLogService.printLog(log, DictCodeList.接口类型_开始.getValue(), ChbConstant.SUCCESS, ChbConstant.UNWARNING, null, false);
        String fileXml = getFile(fileId, filePath);
        // 解析结果
        XbrliDownLoadXml xml = null;
        if (StringUtils.isNotEmpty(fileXml)) {
            // 解析下载文件
            xml = analysisFile(filePath, fileName);
                 if (xml != null) {
                     // 产品登记编码信息
                     List<InformationEntity> productRegistrationCodeInformations = xml.getProductRegistrationCodeInformationTuple() != null ? xml.getProductRegistrationCodeInformationTuple() : new ArrayList<InformationEntity>();
                     //产品登记编码回灌更新到持有信息
                     List<ChbPrdresistCode> coverInvestorsList=new ArrayList<>();
                     // 循环产品登记编码信息
                     for (InformationEntity info : productRegistrationCodeInformations) {
                         // 产品代码
                         CommonNodeEntity product = info.getProductCode();
                         // 产品登记代码
                         CommonNodeEntity productRegistrationCode = info.getProductRegistrationCode();
                         // 行内标识码
                         CommonNodeEntity preIdentificationCode = info.getPreIdentificationCode();
                         if (product == null && preIdentificationCode == null) {
                             continue;
                         }
                         if (productRegistrationCode == null) {
                             continue;
                         }
                         // 更新产品登记编码表 如果产品登记编码表存在该产品，则更新，不存在则插入
                         // 产品代码
                         String prodid = product != null ? product.getValue() : preIdentificationCode.getValue();
                         // 产品登记代码
                         String prodRecode = productRegistrationCode.getValue();

                         // 登记银行代码
                         String registBankCode = prodRecode.substring(0,6);
                         logger.info("获取取得文件内信息:registBankCode:"+registBankCode+",prodid:"+prodid);

                         // 根据登记银行代码获取机构代码
                         Map<String, Object> paramMap = new HashMap<String, Object>();
                         paramMap.put("registBankCode", registBankCode);
                         String orgCode = chbFeedbackMsgComponentMapper.selectOrgCodeByRegistBankCode(paramMap);

                         // 查询产品登记编码表中是否存在该产品
                         ChbPrdresistCodeKey key = new ChbPrdresistCodeKey();
                         //modify by liubin start 以产品登记编码作为维护进行查看
                         key.setReg_code(prodRecode);
//                         key.setProdid(prdId);
                         //modiy by liubin end
                         key.setOrgCode(orgCode);
                         key.setFlag("1");
                         key.setProdid(prodid);
                         logger.info("获取取得文件内信息"+key.toString());
                         ChbPrdresistCode record = chbFeedbackMsgComponentMapper.selectByPrimaryKey(key);
                         if (record == null) {
                        	 record = new ChbPrdresistCode();
                             // 产品代码
                             record.setProdid(prodid);

                             record.setProdInsId(prodid);
                             // 机构代码
                             record.setOrgCode(orgCode);
                             // 产品登记编码
                             record.setRegCode(prodRecode);
                             record.setRemark("中债直连反馈");

                             record.setCreateTime(new Date());
                             record.setUpdateTime(new Date());
                             record.setCreateUser("system");
                             record.setUpdateUser("system");
                             record.setCreateDept("");

                             chbFeedbackMsgComponentMapper.insertSelective(record);
                         }
                         else {
                         	/*
                         	 * modify by liubin start 若找到记录，判断产品代码与中债回灌的产品代码是否一致、
                         	 * 若一致，不做任何操作，
                         	 * 若不一致，则将产品登记编码中旧的产品代码对应的持有信息产品代码全部更新成回灌的产品代码，更新完成后再将旧数据更新
                         	 */
                             // 产品登记编码
//                             record.setRegCode(prodRecode);
                             record.setNewProdId(prodid);
                             record.setProdInsId(prodid);
                             if(prodid != null && !prodid.equals(record.getProdid())){
                            	 chbFeedbackMsgComponentMapper.updateByPrimaryKeySelective(record);
                             }
//                             chbPrdresistCodeMapper.updateByPrimaryKeySelective(record);
                         }
                         // modify by etliu 20180829 end
                         coverInvestorsList.add(record);
                     }
                     chbComponentMsgLogService.printLog(log, DictCodeList.接口类型_报文转化.getValue(), ChbConstant.SUCCESS, ChbConstant.UNWARNING, null, true);

//                    logger.info("更新持有信息产品登记编码开始......");
//                    investorsHoldInfo001InterfService.refreshStatusAData(coverInvestorsList);
//                    logger.info("更新持有信息产品登记编码结束......");
                 }
                 else {
                     chbComponentMsgLogService.printLog(log, DictCodeList.接口类型_报文转化.getValue(), ChbConstant.FAILURE, ChbConstant.WARNING, "解析结果失败", true);
                 }
        }
    }


    /**
     * 分析message
     *
     * @param asynReportEntity 返回信息
     * @param sysDate 业务日期
     * @param chbFeedbackFile 反馈文件对象
     * @param registBankCode 发行机构代码
     * @see
     * @since 1.0
     */
    public ChbFeedbackFile analysisData(AsynReportEntity asynReportEntity, Date sysDate, ChbFeedbackFile chbFeedbackFile, String registBankCode, List<ChbInfoReportDto> reports) {
        // 获取文件路径
        String filePath = getDownloadFilePath();
        // 文件夹不存在，创建好
        if (!FileUtil.exist(filePath)) {
            FileUtil.mkdir(filePath);
        }

        // 报送报文编码
        String repMesNo = asynReportEntity.getMsgHeader().getBizInfo().getOrigMsgId().getValue();
        // 文件ID
        String fileId = asynReportEntity.getDocument().getFileId();
        // 确认文件名
        String fileName = asynReportEntity.getDocument().getFileName();
        // 报送类型
        String submitType = this.splitSubmitType(fileName, registBankCode);
        // 报文接收时间
        Date reportSendDate = null;
        if (StringUtil.isNotEmpty(asynReportEntity.getMsgHeader().getBasicInfo().getMsgSndDateTime())){
            // 2021-05-20T15:25:41.373
            String reportSendDateStr = asynReportEntity.getMsgHeader().getBasicInfo().getMsgSndDateTime().getValue().replace("T", " ");
            reportSendDate = DateUtil.parse(reportSendDateStr);
        }
        logger.info("报文内容:"+asynReportEntity.toString());
        InterfLog log = new InterfLog();
        log.setInterfId(DictCodeList.接口系统ID_直连结果分析.getValue());
        log.setBizDate(DateUtil.format(sysDate, DateUtil.DEFAULT_OTHER_FORMAT));
        // 写日志
        chbComponentMsgLogService.printLog(log, DictCodeList.接口类型_开始.getValue(), ChbConstant.SUCCESS, ChbConstant.UNWARNING, null, false);

        // 中债直连数据
        ChbInfoReportDto report = new ChbInfoReportDto();
        // 报送报文编码
        report.setRepMesNo(repMesNo);

        if (CollectionUtils.isEmpty(reports)) {
            // 第一次需要记录反馈文件列表，后续处理时不需要记录
            if (!StringUtil.isNotEmpty(chbFeedbackFile)){
                getFile(fileId, filePath);

                chbFeedbackFile = new ChbFeedbackFile();
                chbFeedbackFile.setRtnRepMesNo(repMesNo);//反馈-报文编码
                chbFeedbackFile.setFileId(fileId);// 文件ID
                chbFeedbackFile.setFileName(fileName);// 文件名称
                chbFeedbackFile.setFilePath(filePath);// 文件路径
                chbFeedbackFile.setReportEtrType(submitType);
                chbFeedbackFile.setFeedbackMsg(JsonUtils.object2Json(asynReportEntity));// 反馈消息CLOB
                chbFeedbackFile.setHandleStatus(DictCodeList.中债一二三期文件处理状态_未处理.getValue());
                chbFeedbackFile.setReportAcceptDate(DateUtil.now());// 报文接收时间
                chbFeedbackFile.setReportSendDate(reportSendDate);//报文发送时间
                chbFeedbackFileComponentMapper.insert(chbFeedbackFile);
                logger.info("中债文件正常下载后记录反馈文件管理表结束");
            }

            chbComponentMsgLogService.printLog(log, DictCodeList.接口类型_检索数据.getValue(), ChbConstant.FAILURE
                    , ChbConstant.WARNING,
                    "报送编码 不存在" + repMesNo, true);
            return null;
        }
        submitType=reports.get(0).getSubmitType();
        // 报送编码
        String reportNo = reports.get(0).getReportNo();
        // 报送编码
        report.setReportNo(reportNo);
        // 确认文件ID
        report.setCheckFileId(fileId);
        // 确认结果码
        report.setCheckResult(asynReportEntity.getDocument().getErrorCode());
        // 确认结果消息
        report.setCheckResultMsg(asynReportEntity.getDocument().getErrorText());
        // 确认文件名
        report.setCheckFileName(fileName);
        // 确认总件数
        report.setCheckTotalCount(NumberUtil.parseInt(asynReportEntity.getDocument().getTotalCount()));
        // 确认成功件数
        report.setCheckSuccessCount(NumberUtil.parseInt(asynReportEntity.getDocument().getSuccessCount()));
        // 确认失败件数
        report.setCheckFailedCount(NumberUtil.parseInt(asynReportEntity.getDocument().getFailedCount()));

        String fileXml = null;
        // 当反馈文件列表为空时直接从本地获取文件
        if (StringUtil.isNotEmpty(chbFeedbackFile)){
            fileXml = "999";// 此处代码定义只是为了控制下面非空的判断可以正常进入
        }
        // 当反馈文件列表不为空时处理，从di下载反馈文件
        else{
            fileXml = getFile(fileId, filePath);

            chbFeedbackFile = new ChbFeedbackFile();
            chbFeedbackFile.setRtnRepMesNo(repMesNo);//反馈-报文编码
            chbFeedbackFile.setSendRepMesNo(reportNo);//发送-报文编码
            chbFeedbackFile.setReportEtrType(submitType);// 报文指令类型submitType
            chbFeedbackFile.setFileId(fileId);// 文件ID
            chbFeedbackFile.setFileName(fileName);// 文件名称
            chbFeedbackFile.setFilePath(filePath);// 文件路径
            chbFeedbackFile.setFeedbackMsg(JsonUtils.object2Json(asynReportEntity));// 反馈消息CLOB
            chbFeedbackFile.setHandleStatus(DictCodeList.中债一二三期文件处理状态_未处理.getValue());
            chbFeedbackFile.setReportAcceptDate(DateUtil.now());// 报文接收时间
            chbFeedbackFile.setReportSendDate(reportSendDate);//报文发送时间
            chbFeedbackFileComponentMapper.insert(chbFeedbackFile);
            logger.info("中债文件正常下载后记录反馈文件管理表结束");
        }

        if (oneTwoReportList.contains(submitType) && StringUtil.equals(chbPerNo, "03")){
            logger.info("一二期文件，非三期服务器处理，跳过");
            return null;
        }
        if (threeReportList.contains(submitType) && StringUtil.equals(chbPerNo, "01")){
            logger.info("三期文件，非一二期服务器处理，跳过");
            return null;
        }
        logger.info("开始处理本服务器文件，文件类型：" + DictCodeList.from(DictCode.报表名称, submitType).getCodeName());

        String fileNo = null;
        // 解析结果
        List<XbrliDownLoadXml> xmlList = new ArrayList<XbrliDownLoadXml>();

        // 下载成功时
        logger.info("根据下载文件更新反馈状态开始");
        if (StringUtils.isNotEmpty(fileXml)) {
            logger.info("filexml不为空时进行数据修改");
            xmlList = this.updateSubStatus(filePath,fileName,reportNo,submitType, true);
        }
        if (CollectionUtil.isEmpty(xmlList)) {
            logger.info("xmlList为空时进行数据修改");
            // 更新中债报送结果
            ChbSubmitStatusDto status = new ChbSubmitStatusDto();
            // 报送编码
            status.setReportNo(reportNo);
            // 反馈状态
            status.setSubmitFeedbackStatus(DictCodeList.报送反馈状态_审核文件下载失败.getValue());
            // 更新数据
            if (DictCodeList.报表名称_投资者明细信息登记.getValue().equals(submitType)) {
                // 更新投资者明细反馈状态表
                chbFeedbackMsgComponentMapper.updateChbEquDetSubmitStatus(status);
            } else if (DictCodeList.报表名称_投资者身份信息登记.getValue().equals(submitType)
                    || DictCodeList.报表名称_投资者持有信息登记.getValue().equals(submitType)) {
                /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
                status.setFlag(submitType);
                /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
                // 更新中债三期反馈状态表
                chbFeedbackMsgComponentMapper.updateChbSubmitStatus(status);
            } else {
                // 中债一、二期的反馈结果
                chbFeedbackMsgComponentMapper.updateChbSubmitStatusTwo(status);

                if (StrUtil.equals(DictCodeList.报表名称_资产估值信息登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbAssetValSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_产品申报登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbPrdDeclareSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_产品发行登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbPrdIssueSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_募集总量登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbPrdRaisetotSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_产品存续期登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbPrdDealSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_产品终止登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbPrdEndSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_资管计划底层持仓信息登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbBottomAstPosSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_资产交易信息登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbInvestTradeSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_资产要素信息登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbAssetInfoSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_从业人员登记.getValue(), submitType)) {
                    chbFeedbackMsgComponentMapper.updateChbEmployeeInfoSub(status);
                }
            }

        }
        // 内部文件代码
        report.setInsideFileId(fileNo);
        // 确认时间
        report.setReplyTime(new Date());

        // 更新中债直连报送状态
        chbFeedbackMsgComponentMapper.updateInfoReportByPrimaryKey(report);
        // 打印日志
        chbComponentMsgLogService.printLog(log, DictCodeList.接口类型_结束.getValue(), ChbConstant.SUCCESS
                , ChbConstant.UNWARNING, null, false);

        logger.info("根据下载文件更新反馈状态结束");

        ChbFeedbackFile chbFeedbackFileUp = new ChbFeedbackFile();
        chbFeedbackFileUp.setCreateUser(null);
        chbFeedbackFileUp.setCreateTime(null);
        chbFeedbackFileUp.setCreateDept(null);
        chbFeedbackFileUp.setHandleStatus(DictCodeList.中债一二三期文件处理状态_已处理.getValue());
        chbFeedbackFileUp.setUpdateTime(DateUtil.now());
        chbFeedbackFileUp.setMoveFlag(DictCodeList.是否_中债_是.getValue());
        chbFeedbackFileUp.setSendRepMesNo(reportNo);//发送-报文编码
        chbFeedbackFileUp.setReportEtrType(submitType);// 报文指令类型submitType
        chbFeedbackFileUp.setRtnRepMesNo(repMesNo);

        return chbFeedbackFileUp;

    }

    /**
     * 更新中债一二期反馈结果
     * @param statusList 反馈结果
     *
     * @param updateFlag 更新标识,如果是手工匹配不需要更新two表
     */
    private void updateSubmitStatusTwo(List<ChbSubmitStatusDto> statusList, String submitType, boolean updateFlag) {
        // 获取sql 连接， 批量执行模式
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            ChbFeedbackMsgComponentMapper componentMapper = sqlSession.getMapper(ChbFeedbackMsgComponentMapper.class);
            int index = 0;
            for (ChbSubmitStatusDto status : statusList) {
                if(updateFlag){
                    componentMapper.updateChbSubmitStatusTwo(status);
                }
                if (StrUtil.equals(DictCodeList.报表名称_资产估值信息登记.getValue(), submitType)) {
                    componentMapper.updateChbAssetValSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_产品申报登记.getValue(), submitType)) {
                    componentMapper.updateChbPrdDeclareSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_产品发行登记.getValue(), submitType)) {
                    componentMapper.updateChbPrdIssueSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_募集总量登记.getValue(), submitType)) {
                    componentMapper.updateChbPrdRaisetotSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_产品存续期登记.getValue(), submitType)) {
                    componentMapper.updateChbPrdDealSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_产品终止登记.getValue(), submitType)) {
                    componentMapper.updateChbPrdEndSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_资管计划底层持仓信息登记.getValue(), submitType)) {
                    componentMapper.updateChbBottomAstPosSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_资产交易信息登记.getValue(), submitType)) {
                    componentMapper.updateChbInvestTradeSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_资产要素信息登记.getValue(), submitType)) {
                    componentMapper.updateChbAssetInfoSub(status);
                }
                else if (StrUtil.equals(DictCodeList.报表名称_从业人员登记.getValue(), submitType)) {
                    componentMapper.updateChbEmployeeInfoSub(status);
                }
                index ++;
                if (index == maxSqlNum) {
                    sqlSession.commit();
                    index = 0;
                }
            }
        } catch (Exception e) {
            logger.error("批量执行失败", e);
            sqlSession.rollback();
        } finally {
            sqlSession.commit();
            sqlSession.close();
        }
    }


    /**
     * 更新中债三期反馈结果
     * @param statusList 反馈结果
     */
    private void updateSubmitStatus(List<ChbSubmitStatusDto> statusList) {
        // 获取sql 连接， 批量执行模式
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            ChbFeedbackMsgComponentMapper componentMapper = sqlSession.getMapper(ChbFeedbackMsgComponentMapper.class);
            int index = 0;
            for (ChbSubmitStatusDto status : statusList) {
                componentMapper.updateChbSubmitStatus(status);
                index ++;
                if (index == maxSqlNum) {
                    sqlSession.commit();
                    index = 0;
                }
            }
        } catch (Exception e) {
            logger.error("批量执行失败", e);
            sqlSession.rollback();
        } finally {
            sqlSession.commit();
            sqlSession.close();
        }
    }

    /**
     * 更新投资者明细 反馈状态
     * @param statusList 反馈结果
     */
    private void updateEquDetSubmitStatus(List<ChbSubmitStatusDto> statusList) {
        // 获取sql 连接， 批量执行模式
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            ChbFeedbackMsgComponentMapper componentMapper = sqlSession.getMapper(ChbFeedbackMsgComponentMapper.class);
            int index = 0;
            for (ChbSubmitStatusDto status : statusList) {
                componentMapper.updateChbEquDetSubmitStatus(status);
                index ++;
                if (index == maxSqlNum) {
                    sqlSession.commit();
                    index = 0;
                }
            }
        } catch (Exception e) {
            logger.error("批量执行失败", e);
            sqlSession.rollback();
        } finally {
            sqlSession.commit();
            sqlSession.close();
        }
    }


    /**
     * 解析下载文件
     *
     * @param filePath 文件路径
     * @param fileName 压缩包名称
     * @return 解析结果
     * @see
     * @since 1.0
     */
    private XbrliDownLoadXml analysisFile(String filePath, String fileName) {
        XbrliDownLoadXml xml = null;
        List<String> files = this.disCompress(filePath + fileName, filePath);
        if (CollectionUtils.isEmpty(files)) {
            return xml;
        }
        List<String> datas = null;
        try {
            datas = this.utilGetFile(filePath + files.get(0), "UTF-8");

        }
        catch (IOException e) {
            // 文件解析失败
            return xml;
        }
        if (CollectionUtils.isEmpty(datas)) {
            return xml;
        }
        StringBuffer s = new StringBuffer();
        for (String data : datas) {
            s.append(data);
        }
        xml = this.converyToJavaBean(
            s.toString().replaceAll("xbrli:xbrl", "root").replaceAll(":", ""),
            XbrliDownLoadXml.class);
        return xml;
    }


    /**
	 * 读取文件
	 *
	 * @param fullPath
	 *            文件全路径
	 * @param encode
	 *            编码方式
	 * @return List<String> 文件内容
	 * @throws IOException
	 *             IOException
	 * @see
	 * @since 1.0
	 */
	public static List<String> utilGetFile(String fullPath, String encode) throws IOException {
		File file = new File(fullPath);
		if (!file.exists()) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		FileInputStream fis=null;
		InputStreamReader isr=null;
		BufferedReader reader=null;
		try{
		// 以行为单位读取文件内容，一次读一整行
		fis=new FileInputStream(file);
		isr=new InputStreamReader(fis, encode);
		reader = new BufferedReader(isr);
		String tempString = null;
		// 一次读入一行，直到读入null为文件结束
		while ((tempString = reader.readLine()) != null) {
			// 显示行号
			result.add(tempString);
		}
		fis.close();
		isr.close();
		reader.close();
		}
		catch(IOException e){
			logger.error(e.getMessage(),e);
		    throw new IOException();
		}
		finally {
          if(fis!=null){
              try {
                fis.close();
            }
            catch (IOException e) {
//                e.printStackTrace();
                logger.error(e.getMessage(),e);
            }
          }
          if(isr!=null){
              try {
                  isr.close();
              }
              catch (IOException e) {
//                  e.printStackTrace();
                  logger.error(e.getMessage(),e);
              }
          }
          if(reader!=null){
              try {
                  reader.close();
              }
              catch (IOException e) {
//                  e.printStackTrace();
                  logger.error(e.getMessage(),e);
              }
          }
        }
		return result;
	}


    /**
     * xml-->bean
     *
     * @param xml 报文字符串
     * @param c bean类型
     * @return T bean对象
     * @param <T> bean class
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <T> T converyToJavaBean(String xml, Class<T> c) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T)unmarshaller.unmarshal(new StringReader(xml));
        }
        catch (Exception e) {
            logger.error("报文：" + xml);
            logger.error(e.getMessage(),e);
            return null;
        }
        return t;
    }


    /**
     * 解压文件
     *
     * @param zipFile 压缩文件
     * @param disCompressPath 解压路径
     * @return 消息list
     * @see
     * @since 1.0
     */
    public static List<String> disCompress(String zipFile, String disCompressPath) {
        ZipInputStream zin = null;
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        FileOutputStream out = null;
        List<String> fileList = new ArrayList<String>();
        try {
            zin = new ZipInputStream(new FileInputStream(zipFile));// 输入源zip路径
            bin = new BufferedInputStream(zin);
            java.util.zip.ZipEntry entry;

            while ((entry = zin.getNextEntry()) != null && !entry.isDirectory()) {
                File fout = new File(disCompressPath, entry.getName());
                fileList.add(entry.getName());
                if (!fout.exists()) {
                    (new File(fout.getParent())).mkdirs();
                }
                out = new FileOutputStream(fout);
                bout = new BufferedOutputStream(out);
                int b;
                while ((b = bin.read()) != -1) {
                    bout.write(b);
                }
                bout.close();
                out.close();
            }

        }
        catch (FileNotFoundException e) {
            logger.error("解压文件异常",e);
            return null;
        }
        catch (IOException e) {
            logger.error("解压文件异常",e);
            return null;
        }
        finally {
            try {
                if (bin != null) {
                    bin.close();
                }
                if (zin != null) {
                    zin.close();
                }
                if (out != null) {
                	out.close();
                }
                if (bout != null) {
                	bout.close();
                }
            }
            catch (IOException e) {
                logger.error("解压文件关闭流异常",e);
            }

        }
        return fileList;
    }

    /**
     * 解析下载文件(数据条数过多时，分批处理)
     *
     * @param filePath 文件路径
     * @param fileName 压缩包名称
     * @return 解析结果
     * @see
     * @since 1.0
     */
    private List<XbrliDownLoadXml> analysisXmlFileList(String filePath, String fileName) {
        List<XbrliDownLoadXml> xmlList = new ArrayList<XbrliDownLoadXml>();

        File outFile = ZipUtil.unzip(filePath + fileName, filePath);
        List<String> files = CollectionUtil.toList(outFile.list());
        if (CollectionUtils.isEmpty(files)) {
            return xmlList;
        }

        List<String> datas = null;
        try {
            datas = FileUtil.readLines(filePath + fileName.replace(".zip",".xml"), "UTF-8");
        }
        catch (Exception e) {
            // 文件解析失败
            return xmlList;
        }
        if (CollectionUtils.isEmpty(datas)) {
            return xmlList;
        }

        List<String> contentList = new ArrayList<String>();

        StringBuffer headSb = new StringBuffer();
        String footSb = "</root>";
        StringBuffer contentSb = new StringBuffer();
        int contentCount = 0;

        for (String data : datas) {
            if (data.contains("</xbrli:xbrl>")) {
                continue;
            }

            if (data.contains("<wemax-ret:ErrorInformationTuple>")
                    || data.contains("<wemax-ret:CorrectInformationTuple>")) {
                contentSb.append(this.formatRowStr(data));
            }
            else if (contentSb.length() == 0 && contentList.size() == 0) {
                headSb.append(this.formatRowStr(data));
            }
            else {
                contentSb.append(this.formatRowStr(data));

                if (data.contains("</wemax-ret:ErrorInformationTuple>")
                        || data.contains("</wemax-ret:CorrectInformationTuple>")) {
                    contentCount++;

                    if (contentCount == reportCbFeedBackHandleCountOneTime) {
                        StringBuffer xmlSb = new StringBuffer();
                        contentList.add(
                                xmlSb.append(headSb).append(contentSb).append(footSb).toString());
                        contentSb.setLength(0);
                        contentCount = 0;
                    }
                }
            }
        }

        if (contentSb.length() > 0) {
            StringBuffer xmlSb = new StringBuffer();
            contentList.add(
                    xmlSb.append(headSb).append(contentSb).append(footSb).toString());
            contentSb.setLength(0);
            xmlSb.setLength(0);
        }

        for (String content : contentList) {
            XbrliDownLoadXml xml = ChbXmlUtil.convertToJavaBean(content, XbrliDownLoadXml.class);

            xmlList.add(xml);
        }

        return xmlList;
    }

    /**
     * 格式化反馈报文单行内容
     *
     * @param rowStr 反馈报文单行内容
     * @return 格式化后内容
     * @see
     * @since 1.0
     */
    private String formatRowStr(String rowStr) {
        String resultStr = "";
        if (StrUtil.isBlank(rowStr)) {
            return resultStr;
        }
        resultStr = rowStr.replaceAll("xbrli:xbrl", "root").replaceAll(":", "").trim();

        return resultStr;
    }


    /**
     * 下载文件
     *
     * @param fileID 文件Id
     * @param filePath 压缩包路径
     * @return 消息list
     * @see
     * @since 1.0
     */
    private String getFile(String fileID, String filePath) {
        String i = null;
        try {
            logger.info("dclient.downloadFile文件下载开始,fileid=" + fileID);
            // 下载文件
            i = dclientComponent.downloadFile(fileID, filePath);
            logger.info("dclient.downloadFile文件下载结束,fileid=" + fileID);
        }
        catch (Exception e) {
            logger.error("下载中债文件异常", e);
        }
        return i;
    }


    /**
     * 获取下载路径
     * @return 下载路径
     */
    public String getDownloadFilePath() {
        if (StrUtil.isEmpty(downloadFilePath)) {
            return  System.getProperty("user.home") + File.separator + "chb" +  File.separator + "download" + File.separator;
        }
        return downloadFilePath;
    }

    /**
     * 获取下载文件解释后的移植路径
     * @return 下载文件解释后的移植路径
     */
    public String getDownloadFilePathMovePath() {
        if (StrUtil.isEmpty(downloadFilePathMovePath)) {
            return  System.getProperty("user.home") + File.separator + "chb" +  File.separator + "download" + File.separator + "bak" + File.separator;
        }
        return downloadFilePathMovePath;
    }

    /**
     * 获取外部API返回结果
     *
     * @param kind 消息类型
     * @return 消息list
     * @see
     * @since 1.0
     */
    public List<AsynReportEntity> getMessage(String kind) {
        // 消息list
        List<AsynReportEntity> asynReportEntitys = new ArrayList<AsynReportEntity>();
        try {
            // 获取消息
            @SuppressWarnings("unchecked")
            List<String> requests = dclientComponent.getMessage(kind);//TODO
//            List<String> requests = Arrays.asList("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                    "<Root>\n" +
//                    "<MsgHeader>\n" +
//                    "<BizInfo>\n" +
//                    "<SndCustomerId>0000007798</SndCustomerId>\n" +
//                    "<RcvCustomerId>0001001063</RcvCustomerId>\n" +
//                    "<LogicalDIId>WMRS0001001063210302090603</LogicalDIId>\n" +
//                    "<BizGroup>WMRS</BizGroup>\n" +
//                    "<MsgType>WMRS.203.001.01</MsgType>\n" +
//                    "<MsgId></MsgId>\n" +
//                    "<OrigMsgId>1121051232000014</OrigMsgId>\n" +
//                    "<SyncType>0</SyncType>\n" +
//                    "<ProcResult>0</ProcResult>\n" +
//                    "</BizInfo>\n" +
//                    "<BasicInfo>\n" +
//                    "<VrsnId>01</VrsnId>\n" +
//                    "<SenderSignature>NULL</SenderSignature>\n" +
//                    "<MsgSndDateTime>2021-05-12T15:51:21.943</MsgSndDateTime>\n" +
//                    "<MsgDirection>1</MsgDirection>\n" +
//                    "</BasicInfo>\n" +
//                    "<XtnInfo/>\n" +
//                    "</MsgHeader>\n" +
//                    "<Document>\n" +
//                    "<SysType>WMRS</SysType>\n" +
//                    "<MsgType>WMRS.203.001.01</MsgType>\n" +
//                    "<FileId>rvVWF1qjfDdG3I4ycEF</FileId>\n" +
//                    "<FileName>Z70024-106-20210422-03-FB-202104221526XX.zip</FileName>\n" +
//                    "<OrigFileName>Z70024-203-20210512-06.zip</OrigFileName>\n" +
//                    "<SuccessCount>200000</SuccessCount>\n" +
//                    "<FailedCount>0</FailedCount>\n" +
//                    "<TotalCount>200000</TotalCount>\n" +
//                    "<ErrorCode>0000</ErrorCode>\n" +
//                    "<ErrorText>处理完成</ErrorText>\n" +
//                    "</Document>\n" +
//                    "</Root>");
            if (CollectionUtils.isEmpty(requests)) {
                return asynReportEntitys;
            }
            // 循环消息
            for (String request : requests) {
                // 转换成Entity
                AsynReportEntity asynReportEntity = ChbXmlUtil.convertToJavaBean(request,
                        AsynReportEntity.class);
                asynReportEntitys.add(asynReportEntity);
            }

        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
            return asynReportEntitys;
        }
        return asynReportEntitys;
    }


    /**
     * 获取外部API返回结果(40类报文用)
     *
     * @param fdc 外部API
     * @param kind 消息类型
     * @return 消息list
     * @see
     * @since 1.0
     */
    public List<AsynReport40Entity> getMessageFor40(String kind) {
        // 消息list
        List<AsynReport40Entity> asynReportEntitys = new ArrayList<AsynReport40Entity>();
        try {
        	// 获取消息
        	@SuppressWarnings("unchecked")
            List<String> requests = dclientComponent.getMessage(kind);
            if (CollectionUtils.isEmpty(requests)) {
                return asynReportEntitys;
            }
            // 循环消息
            for (String request : requests) {
                // 转换成Entity
                AsynReport40Entity asynReportEntity = ChbXmlUtil.convertToJavaBean(request,
                    AsynReport40Entity.class);
                asynReportEntitys.add(asynReportEntity);
            }

        }
        catch (Exception e) {
        	logger.error(e.getMessage(),e);
            return asynReportEntitys;
        }
        return asynReportEntitys;
    }

    /**
     * 记录反馈文件管理表
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @param sendRepMesNo 发送-报文编码
     * @param reportEtrType 报文指令类型
     * @param rtnRepMesNo 反馈-报文编码
     */
    public void updateChbFeedbackFileByNo(ChbFeedbackFile chbFeedbackFile){
        chbFeedbackFileComponentMapper.updateByPrimaryKeySelective(chbFeedbackFile);
    }


    /**
     * 更新各登记SUB反馈状态
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @param reportNo 报送编码
     * @param submitType 报表类型
     * @param updateFlag 更新标识,如果是手工匹配不需要更新two表
     */
    public List<XbrliDownLoadXml> updateSubStatus(String filePath, String fileName, String reportNo, String submitType, boolean updateFlag){
        List<XbrliDownLoadXml> xmlList = this.analysisXmlFileList(filePath, fileName);
        //投资者明细信息反馈状态错误集合
        List<ChbSubmitStatusDto> equdeterros=new ArrayList<>();
        //投资者明细信息反馈状态成功集合
        List<ChbSubmitStatusDto> equdetinfos=new ArrayList<ChbSubmitStatusDto>();

        //中债三期反馈状态错误集合
        List<ChbSubmitStatusDto> submiterrors =new ArrayList<>();
        //中债三期反馈状态成功集合
        List<ChbSubmitStatusDto> submitinfos=new ArrayList<ChbSubmitStatusDto>();

        //中债一、二期反馈状态错误集合
        List<ChbSubmitStatusDto> submitTwoerrors =new ArrayList<>();
        //中债一、二期反馈状态成功集合
        List<ChbSubmitStatusDto> submitTwoinfos=new ArrayList<ChbSubmitStatusDto>();

        for (XbrliDownLoadXml xml : xmlList) {
            // 错误信息
            List<InformationEntity> errors = xml.getErrorInformationTuple();
            if (CollectionUtil.isNotEmpty(errors)) {
                // 循环错误信息
                for (InformationEntity info : errors) {
                    // 更新中债报送结果
                    ChbSubmitStatusDto status = new ChbSubmitStatusDto();
                    // 报送编码
                    status.setReportNo(reportNo);
                    // 登记序号
                    status.setSubmitNum(info.getRegistrationSerialNumber().getValue());
                    // 反馈状态
                    status.setSubmitFeedbackStatus(DictCodeList.报送反馈状态_中债退回.getValue());
                    // 错误信息
                    status.setSubmitFeedbackMsg(info.getErrorMessage().getValue());
                    // 反馈状态
                    // 更新中债报送关系
                    if (DictCodeList.报表名称_投资者明细信息登记.getValue().equals(submitType)) {
                        equdeterros.add(status);
                    } else if (DictCodeList.报表名称_投资者身份信息登记.getValue().equals(submitType)
                            || DictCodeList.报表名称_投资者持有信息登记.getValue().equals(submitType)) {
                        /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
                        status.setFlag(submitType);
                        /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
                        submiterrors.add(status);
                    } else {
                        submitTwoerrors.add(status);
                    }
                }
            }
            // 成功信息
            List<InformationEntity> infos = xml.getCorrectInformationTuple();
            if (CollectionUtil.isNotEmpty(infos)) {
                // 循环成功信息
                for (InformationEntity info : infos) {
                    // 更新中债报送结果
                    ChbSubmitStatusDto status = new ChbSubmitStatusDto();
                    // 报送编码
                    status.setReportNo(reportNo);
                    // 登记序号
                    status.setSubmitNum(info.getRegistrationSerialNumber().getValue());
                    // 反馈状态
                    status.setSubmitFeedbackStatus(DictCodeList.报送反馈状态_中债通过.getValue());
                    // 更新数据
                    if (DictCodeList.报表名称_投资者明细信息登记.getValue().equals(submitType)) {
                        equdetinfos.add(status);
                    } else if (DictCodeList.报表名称_投资者身份信息登记.getValue().equals(submitType)
                            || DictCodeList.报表名称_投资者持有信息登记.getValue().equals(submitType)) {
                        /***三期CHB_SUBMIT_STATUS去除改造  20220310**/

                        status.setFlag(submitType);
                        /***三期CHB_SUBMIT_STATUS去除改造  20220310**/

                        submitinfos.add(status);
                    } else {
                        // 中债一、二期的反馈结果
                        submitTwoinfos.add(status);
                    }
                }
            }

        }
        // 更新中债一二期反馈状态
        if (CollectionUtil.isNotEmpty(submitTwoerrors)) {
            updateSubmitStatusTwo(submitTwoerrors, submitType, updateFlag);
        }
        if (CollectionUtil.isNotEmpty(submitTwoinfos)) {
            updateSubmitStatusTwo(submitTwoinfos, submitType, updateFlag);
        }
        // 更新中债三期反馈状态
        if (CollectionUtil.isNotEmpty(submiterrors)) {
            updateSubmitStatus(submiterrors);
        }
        if (CollectionUtil.isNotEmpty(submitinfos)) {
            updateSubmitStatus(submitinfos);
        }

        // 更新投资者明细反馈状态
        if (CollectionUtil.isNotEmpty(equdeterros)) {
            updateEquDetSubmitStatus(equdeterros);
        }
        if (CollectionUtil.isNotEmpty(equdetinfos)) {
            updateEquDetSubmitStatus(equdetinfos);
        }

        return xmlList;
    }

    /**
     * 根据文件名称获取报表类型
     * @param fileName 文件名称
     * @param registBankCode 发行机构代码
     * @return 报表类型
     */
    private String splitSubmitType(String fileName, String registBankCode){
        // 报送类型
        String submitType = "";
        if (StringUtil.isNotEmpty(fileName) && StringUtil.isNotEmpty(registBankCode)){
            String reportType = fileName.substring(registBankCode.length() + 1, registBankCode.length() + 4);

            if (ChbConstant.FILE_113_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_产品申报登记.getValue();
            }
            else if (ChbConstant.FILE_102_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_机构和私人银行产品发行登记.getValue();
            }
            else if (ChbConstant.FILE_114_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_产品发行登记.getValue();
            }
            else if (ChbConstant.FILE_104_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_募集总量登记.getValue();
            }
            else if (ChbConstant.FILE_105_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_产品存续期登记.getValue();
            }
            else if (ChbConstant.FILE_106_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_资产要素信息登记.getValue();
            }
            else if (ChbConstant.FILE_108_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_资产交易信息登记.getValue();
            }
            else if (ChbConstant.FILE_109_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_资产估值信息登记.getValue();
            }
            else if (ChbConstant.FILE_110_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_资管计划底层持仓信息登记.getValue();
            }
            else if (ChbConstant.FILE_111_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_产品终止登记.getValue();
            }
            else if (ChbConstant.FILE_112_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_从业人员登记.getValue();
            }
            else if (ChbConstant.FILE_203_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_分类总量信息登记.getValue();
            }
            else if (ChbConstant.FILE_201_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_投资者身份信息登记.getValue();
            }
            else if (ChbConstant.FILE_202_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_投资者持有信息登记.getValue();
            }
            else if (ChbConstant.FILE_203_TYPE.equals(reportType)) {
                submitType = DictCodeList.报表名称_投资者明细信息登记.getValue();
            }

        }

        return submitType;
    }
}

/*
 * 文件名：CnBondService.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：lihuadong
 * 修改时间：2017年5月17日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */

package com.joyintech.tams.report.interf.cnbond.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.ChbInfoReportMapper;
import com.joyintech.tams.common.base.dao.ChbPrdresistCodeMapper;
import com.joyintech.tams.common.base.entity.ChbInfoReport;
import com.joyintech.tams.common.base.entity.ChbPrdresistCode;
import com.joyintech.tams.common.base.entity.ChbPrdresistCodeKey;
import com.joyintech.tams.common.base.entity.ChbSubmitStatus;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.ReportGlobalConstant;
import com.joyintech.tams.report.interf.cnbond.dao.CnBondInterfMapper;
import com.joyintech.tams.report.interf.cnbond.dto.BaseInfoEntity;
import com.joyintech.tams.report.interf.cnbond.dto.BusinessForXMLEntity;
import com.joyintech.tams.report.interf.cnbond.dto.CnBondInterfItem;
import com.joyintech.tams.report.interf.cnbond.dto.InterfCnBondSubmitPara;
import com.joyintech.tams.report.interf.cnbond.ext.ExtFprDClient;
import com.joyintech.tams.report.interf.common.constant.InterfConstant;
import com.joyintech.tams.report.interf.common.dto.*;
import com.joyintech.tams.report.interf.common.service.InterfService;
import com.joyintech.tams.report.interf.common.util.CnBondUtil;
import com.joyintech.tams.report.interf.common.util.FileUtil;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * 中债报送
 *
 * @author lihuadong
 * @version 1.0 2017年5月17日
 * @see CnBondInterfService
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CnBondInterfService extends InterfService {

    /**
     * 中债反馈文件内容单次处理数据条数
     */
    @Value("${application.reportCbFeedBackHandleCountOneTime}")
    private int reportCbFeedBackHandleCountOneTime;

    /**
     * .WMRS
     */
    private static final String WMRS = "WMRS.";

    /**
     * .001.01
     */
    private static final String STRING_001_01 = ".001.01";

    /**
     * 个人产品申报登记报文 报文类型
     */
    private static final String FILE_101_TYPE = "101";

    /**
     * 机构和私人银行产品发行登记
     */
    private static final String FILE_102_TYPE = "102";

    /**
     * 一般个人产品发行登记
     */
    private static final String FILE_103_TYPE = "103";

    /**
     * 募集总量登记
     */
    private static final String FILE_104_TYPE = "104";

    /**
     * 存续期登记
     */
    private static final String FILE_105_TYPE = "105";

    /**
     * 资产负债登记
     */
    private static final String FILE_106_TYPE = "106";

    /**
     * 交易登记
     */
    private static final String FILE_108_TYPE = "108";

    /**
     * 估值信息登记
     */
    private static final String FILE_109_TYPE = "109";

    /**
     * 资管计划底层资产信息登记
     */
    private static final String FILE_110_TYPE = "110";

    /**
     * 终止登记
     */
    private static final String FILE_111_TYPE = "111";

    /**
     * 从业人员登记
     */
    private static final String FILE_112_TYPE = "112";

    /**
     * 投资者身份信息登记
     */
    private static final String FILE_201_TYPE = "201";

    /**
     * 业务明细信息登记
     */
    private static final String FILE_202_TYPE = "202";

    /**
     * 分类总量信息登记
     */
    private static final String FILE_203_TYPE = "203";

    /**
     * 中债报送结果code 0000 传输正常
     */
    private static final String XCPTNRTRCD_0000 = "0000";

    /**
     * .xml 常量
     */
    private static final String FILE_XML = ".xml";

    /**
     * .zip 常量
     */
    private static final String FILE_ZIP = ".zip";

    /**
     * 固定值 ：Tuple
     */
    private static final String TUPLE = "Tuple";

    /**
     * 报文 TITLE 属性1
     */
    private static final String TITLE_PROPERTY1 = "http://www.w3.org/2001/XMLSchema-instance";

    /**
     * 报文 TITLE 属性2
     */
    private static final String TITLE_PROPERTY2 = "http://xbrl.org/2006/xbrldi http://www.xbrl.org/2006/xbrldi-2006.xsd";

    /**
     * 报文 TITLE 属性 3
     */
    private static final String TITLE_PROPERTY3 = "http://www.chinawealth.com.cn/taxonomy/2016-04-30/cas/chinawealth/chinawealth_entry_point";

    /**
     * 报文 TITLE 属性 4
     */
    private static final String TITLE_PROPERTY4 = "http://www.xbrl.org/2003/linkbase";

    /**
     * 报文 TITLE 属性 5
     */
    private static final String TITLE_PROPERTY5 = "http://xbrl.mof.gov.cn/taxonomy/2015-03-31/cas";

    /**
     * 报文 TITLE 属性 6
     */
    private static final String TITLE_PROPERTY6 = "http://www.xbrl.org/dtr/type/numeric";

    /**
     * 报文 TITLE 属性 7
     */
    private static final String TITLE_PROPERTY7 = "http://www.xbrl.org/dtr/type/non-numeric";

    /**
     * 报文 TITLE 属性 8
     */
    private static final String TITLE_PROPERTY8 = "http://xbrl.ifrs.org/taxonomy/2014-03-05/ifrs-full";

    /**
     * 报文 TITLE 属性 9
     */
    private static final String TITLE_PROPERTY9 = "http://xbrl.org/2005/xbrldt";

    /**
     * 报文 TITLE 属性 10
     */
    private static final String TITLE_PROPERTY10 = "http://www.chinawealth.com.cn/taxonomy/2016-04-30/cas/chinawealth/rol_chinawealth_2016-04-30";

    /**
     * 报文 TITLE 属性11
     */
    private static final String TITLE_PROPERTY11 = "http://www.chinawealth.com.cn/taxonomy/2016-04-30/cas/chinawealth";

    /**
     * 报文 TITLE 属性 12
     */
    private static final String TITLE_PROPERTY12 = "http://xbrl.org/2006/xbrldi";

    /**
     * 报文 TITLE 属性13
     */
    private static final String TITLE_PROPERTY13 = "http://www.xbrl.org/2003/instance";

    /**
     * 报文 TITLE 属性14
     */
    private static final String TITLE_PROPERTY14 = "http://www.chinawealth.com.cn/wei/2016-04-30";

    /**
     * 报文 TITLE 属性15
     */
    private static final String TITLE_PROPERTY15 = "http://www.xbrl.org/2003/iso4217";

    /**
     * 报文 TITLE 属性16
     */
    private static final String TITLE_PROPERTY16 = "http://www.w3.org/1999/xlink";

    /**
     * 报文 TITLE 属性17
     */
    private static final String TITLE_PROPERTY17 = "http://www.chinawealth.com.cn/taxonomy/2016-04-30/chinawealth/chinawealth_entry_point_2016-04-30.xsd";

    /**
     * 报文 LINK_TYPE
     */
    private static final String LINK_TYPE = "simple";

    /**
     * 消息类型 20
     */
    private static final String MSG_KIND_20 = "20";

    /**
     * 消息类型 40
     */
    private static final String MSG_KIND_40 = "40";

    /**
     * 分隔符
     */
    private static final String SPLIT_STRING1 = "\\|";

    /**
     * context id 前缀
     */
    private static final String CD = "C-D-";

    /**
     * context id 前缀
     */
    private static final String CI = "C-I-";

    /**
     * SCHNAME
     */
    private static final String SCHNAME = "http://www.pbc.gov.cn/";

    /**
     * ISO 4217
     */
    private static final String ISO_4217 = "iso4217";

    /**
     * wemax-unit:Month
     */
    private static final String WEMAX_UNIT_MONTH = "wemax-unit:Month";

    /**
     * wemax-unit:Count
     */
    private static final String WEMAX_UNIT_COUNT = "wemax-unit:Count";

    /**
     * wemax-unit:Day
     */
    private static final String WEMAX_UNIT_DAY = "wemax-unit:Day";

    /**
     * xbrli:pure
     */
    private static final String XBRLI_PURE = "xbrli:pure";

    /**
     * wemax-unit:Piece
     */
    private static final String WEMAX_UNIT_PIECE = "wemax-unit:Piece";

    /**
     * 接口执行状态_成功
     */
    private static final String SUCCESS = DictCodeListExt2.执行结果_成功.getValue();

    // /**
    // * 日志全局变量
    // */
    // private String PARTLY_SUCCESS = DictCodeList.接口执行状态_部分成功.getValue();

    /**
     * 接口执行状态_失败
     */
    private static final String FAILURE = DictCodeListExt2.执行结果_失败.getValue();

    /**
     * 预警标示_预警
     */
    private static final String WARNING = DictCodeListExt2.预警标示_预警.getValue();

    /**
     * 预警标示_不预警
     */
    private static final String UNWARNING = DictCodeListExt2.预警标示_不预警.getValue();

    /**
     * 单位 DAY
     */
    public static final String DAY = "Day";

    /**
     * 单位 Month
     */
    public static final String MONTH = "Month";

    /**
     * 单位 PURE
     */
    public static final String PURE = "PURE";

    /**
     * 单位PIECE
     */
    public static final String PIECE = "PIECE";

    /**
     * 单位 MonthPerCount
     */
    public static final String MONTHPERCOUNT = "MonthPerCount";

    /**
     * 中债报送mapper
     */
    @Autowired
    private CnBondInterfMapper cnBondInterfMapper;

    // /**
    // * 中债报送关系
    // */
    // @Autowired
    // private ChbReportRelationMapper chbReportRelationMapper;
    // /**
    // * 中债报送结果
    // */
    // @Autowired
    // private ChbSubmitStatusMapper chbSubmitStatusMapper;

    /**
     * 中债直连报送
     */
    @Autowired
    private ChbInfoReportMapper chbInfoReportMapper;

    /**
     * 产品登记编码
     */
    @Autowired
    private ChbPrdresistCodeMapper chbPrdresistCodeMapper;

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(CnBondInterfService.class);
    /**
     * 更新持有信息产品登记编码
     */
    @Autowired
    private InvestorsHoldInfo001InterfService investorsHoldInfo001InterfService;
    /**
     * 添加中债挡板直连标识
     * 02直连，01标识挡板
     */
    @Value("${application.chb.dangbanFlag:02}")
    private String dangbanFlag;

    /**
     * 中债报送
     *
     * @param paras 报送参数list
     * @see
     * @since 1.0
     */
  public void sendCnBond(List<InterfCnBondSubmitPara> paras) {
//  public List<ChbSubmitStatus> sendCnBond(List<InterfCnBondSubmitPara> paras) {
        // 判断参数列表是否为空
        if (CollectionUtils.isEmpty(paras)) {
            return ;
        }

        // 日志数据
        List<InterfLog> interfLogList = new ArrayList<InterfLog>();
        // 中债直连报送数据
        List<ChbInfoReport> chbInfoReportList = new ArrayList<ChbInfoReport>();
        // 中债报送状态数据
        List<ChbSubmitStatus> chbSubmitStatusList = new ArrayList<ChbSubmitStatus>();

        // 逐条报送
        for (InterfCnBondSubmitPara para : paras) {
            sendCnBond(para.getDealType(), para.getInfos(), para.getSysDate(), para.getRegistSeq(),
                para.getRegistBankCode(), interfLogList, chbInfoReportList, chbSubmitStatusList,para);
        }

        // 分批登录日志数据
        logger.info("CnBondInterfService insertInterfLogList处理开始：" + DateUtil.getCurrentDateTime());
        insertInterfLogList(interfLogList);
        logger.info("CnBondInterfService insertInterfLogList处理结束：" + DateUtil.getCurrentDateTime());

        // 分批登录中债直连报送数据
        logger.info("CnBondInterfService insertChbInfoReportListByPart处理开始：" + DateUtil.getCurrentDateTime());
        this.insertChbInfoReportListByPart(chbInfoReportList);
        logger.info("CnBondInterfService insertChbInfoReportListByPart处理结束：" + DateUtil.getCurrentDateTime());
        // 分批登录中债报送状态数据
        logger.info("CnBondInterfService insertChbSubmitStatusListByPart处理开始：" + DateUtil.getCurrentDateTime());
        if(!StringUtil.isEmptyList(paras)&&paras.get(0).getDealType().equals(DictCodeListExt2.报表名称_投资者明细信息登记.getValue())){
            this.insertChbEquDetSubmitStatusListByPart(chbSubmitStatusList);
        }else{
        	this.insertChbSubmitStatusListByPart(chbSubmitStatusList);
        }
        logger.info("CnBondInterfService insertChbSubmitStatusListByPart处理结束：" + DateUtil.getCurrentDateTime());
  }

    /**
     * 中债报送
     *
     * @param dealType 交易类型
     * @param infos 报送信息
     * @param sysDate 报送日期
     * @param registSeqnos 报送流水号(因投资者身份登记与业务明细内容为csv格式，故直接将报送流水号传入)
     * @param registBankCode 登记银行编码
     * @param interfLogList 出参 批量录入日志
     * @param chbInfoReportList 出参 批量录入中债直连报送
     * @param chbSubmitStatusList 出参 批量录入中债报送状态
     * @param para  报送流水号+识别标识
     * @see
     * @since 1.0
     */
    private void sendCnBond(String dealType, List<Map<String, CnBondInterfItem>> infos,
                            Date sysDate, List<String> registSeqnos, String registBankCode,
                            List<InterfLog> interfLogList, List<ChbInfoReport> chbInfoReportList,
                            List<ChbSubmitStatus> chbSubmitStatusList, InterfCnBondSubmitPara para) {
    	logger.info("CnBondInterfService 报文拼装处理开始：" + DateUtil.getCurrentDateTime());

        // 中债路径
        String cbFilepath = getParaVauleByparaCode(InterfConstant.KEY_CB_FILEPATH);
        // 报送日期yyyymmdd
        String date = DateUtil.format(sysDate, DateUtil.DEFAULT_OTHER_FORMAT);
        InterfLog log = new InterfLog();
        log.setInterfId(DictCodeListExt2.接口系统ID_中债直连.getValue());
        log.setBizDate(date);
        // printLog(log, DictCodeList.接口类型_开始.getValue(), SUCCESS, UNWARNING, null, false);
        interfLogList.addAll(
            createLogList(log, DictCodeListExt2.接口类型_开始.getValue(), SUCCESS, UNWARNING, null, false));

        BusinessForXMLEntity entity = new BusinessForXMLEntity();
        // 设值共通参数
        setCommmonInfo(entity, dealType);
        // 标签contextList
        List<XbrliContextEntity> contextList = new ArrayList<XbrliContextEntity>();
        entity.setXbrliContextEntity(contextList);
        // context1
        XbrliContextEntity context1 = getContextEntity1(sysDate, registBankCode);
        // context2
        XbrliContextEntity context2 = getContextEntity2(sysDate, registBankCode);
        // contextRefIds
        String[] contextRefIds = new String[] {context1.getXbrliContextEntityId()};;
        List<BaseInfoEntity> bases = new ArrayList<BaseInfoEntity>();

        String fileType = null;

        if (DictCodeListExt2.报表名称_产品申报登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setFilingRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                context2.getXbrliContextEntityId()};
            fileType = FILE_101_TYPE;
        }
        else if (DictCodeListExt2.报表名称_机构和私人银行产品发行登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setIssueRegistrationForAgencyAndPrivateBankTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                context2.getXbrliContextEntityId()};
            fileType = FILE_102_TYPE;
        }
        else if (DictCodeListExt2.报表名称_产品发行登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setIssueRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = FILE_103_TYPE;
        }
        else if (DictCodeListExt2.报表名称_募集总量登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setInitialSubscriptionRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = FILE_104_TYPE;
        }
        else if (DictCodeListExt2.报表名称_产品存续期登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setSubsequentSubscriptionRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                context2.getXbrliContextEntityId()};
            fileType = FILE_105_TYPE;
        }
        else if (DictCodeListExt2.报表名称_资产要素信息登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setAssetsAndLiabilitiesRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = FILE_106_TYPE;
        }
        else if (DictCodeListExt2.报表名称_资产交易信息登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setTransactionRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                context2.getXbrliContextEntityId()};
            fileType = FILE_108_TYPE;
        }
        else if (DictCodeListExt2.报表名称_资产估值信息登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setValuationRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = FILE_109_TYPE;
        }
        else if (DictCodeListExt2.报表名称_资管计划底层持仓信息登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setAssetManagementPlansUnderlyingAssetsLiabilitiesRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                context2.getXbrliContextEntityId()};
            fileType = FILE_110_TYPE;
        }
        else if (DictCodeListExt2.报表名称_产品终止登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setTerminationRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = FILE_111_TYPE;
        }
        else if (DictCodeListExt2.报表名称_从业人员登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setPractitionerRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = FILE_112_TYPE;
        }
        else if (DictCodeListExt2.报表名称_分类总量信息登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setInvestorsSummationOfDiffenrentTypesInformationRegistrationTuple(bases);
            contextList.add(context1);
            fileType = FILE_203_TYPE;
        }
        else if (DictCodeListExt2.报表名称_投资者身份信息登记.getValue().equals(dealType)) {
            // 基本信息标签
            CommonNodeEntity node = new CommonNodeEntity();
            // 标签值
            node.setValue(
                infos.get(0).get("InvestorsInformationRegistrationCSVDataExplanatory").getValue());
            // contextRef
            node.setContextRef(contextRefIds[0]);
            entity.setInvestorsInformationRegistrationCSVDataExplanatory(node);
            contextList.add(context1);
            fileType = FILE_201_TYPE;
        }
        else if (DictCodeListExt2.报表名称_投资者持有信息登记.getValue().equals(dealType)) {
            // 基本信息标签
            CommonNodeEntity node = new CommonNodeEntity();
            // 标签值
            node.setValue(
                infos.get(0).get("InvestorsInvestedInformationRegistrationCsvDataExplanatory").getValue());
            // contextRef
            node.setContextRef(contextRefIds[0]);
            entity.setInvestorsInvestedInformationRegistrationCsvDataExplanatory(node);
            contextList.add(context1);
            fileType = FILE_202_TYPE;
        }
        else if (DictCodeListExt2.报表名称_投资者明细信息登记.getValue().equals(dealType)) {
            // 基本信息标签
            CommonNodeEntity node = new CommonNodeEntity();
            // 标签值
            node.setValue(infos.get(0).get(
                "InvestorsBusinessDetailsInformationRegistrationCsvDataExplanatory").getValue());
            // contextRef
            node.setContextRef(contextRefIds[0]);
            entity.setInvestorsBusinessDetailsInformationRegistrationCsvDataExplanatory(node);
            contextList.add(context1);
            fileType = FILE_203_TYPE;
        }
        // 设置基本信息 并返回使用单位
        if (!(DictCodeListExt2.报表名称_投资者身份信息登记.getValue().equals(dealType)
              || DictCodeListExt2.报表名称_投资者持有信息登记.getValue().equals(dealType)
              || DictCodeListExt2.报表名称_投资者明细信息登记.getValue().equals(dealType))) {
            Map<String, XbrliUnitEntity> returnMaps = setEntity(bases, infos, contextRefIds, log);
            // 标签xbrli:unit List
            List<XbrliUnitEntity> xbrliUnitEntities = new ArrayList<XbrliUnitEntity>(
                returnMaps.values());
            // 设值标签xbrli:unit
            entity.setXbrliUnitEntities(xbrliUnitEntities);
        }
        // 文件List
        List<File> files = new ArrayList<File>();
        // 相对路径list
        List<String> resourceFilePath = new ArrayList<String>();
        // 登记流水号 list
        // List<String> registSeqnos = new ArrayList<String>();
        getFile(bases, files, resourceFilePath, registSeqnos);
        // 文件名
        String fileName = registBankCode + GlobalConstant.SPLIT + fileType + GlobalConstant.SPLIT
                          + date + GlobalConstant.SPLIT;
        int maxCount = getMaxFileNumber(cbFilepath, fileName);
        // 压缩包名称
        String fileZip = fileName + FileUtil.leftZero(maxCount + 1, 2) + FILE_ZIP;
        fileName = fileName + FileUtil.leftZero(maxCount + 1, 2) + FILE_XML;
        // 将entity 转换成xml报文
        CnBondUtil.generationXML(entity, BusinessForXMLEntity.class, cbFilepath, fileName);
        logger.info("CnBondInterfService 报文拼装处理结束：" + DateUtil.getCurrentDateTime());
        // 添加报文文件
        files.add(new SafelyFile(cbFilepath + fileName));
        // 添加压缩文件路径
        resourceFilePath.add(fileName);
        // 压缩文件
        logger.info("CnBondInterfService 压缩处理开始：" + DateUtil.getCurrentDateTime());
        CnBondUtil.compress(fileZip, cbFilepath, files, resourceFilePath);
        logger.info("CnBondInterfService 压缩处理结束：" + DateUtil.getCurrentDateTime());
        // 中债报送
        ExtFprDClient.setRegBankCode(registBankCode);

        logger.info("CnBondInterfService 报送中债处理开始：" + DateUtil.getCurrentDateTime());
        AsynReportEntity asynReportEntity = markDangBan();
//        	CnBondUtil.recurrence(WMRS + fileType + STRING_001_01,cbFilepath + fileZip);
        /** 根据挡板标识来确认是否打开 start**/
        if(StringUtil.equals(dangbanFlag, DictCodeList.是否_中债_是.getValue())) {
            asynReportEntity = markDangBan();
        }else {
            asynReportEntity = CnBondUtil.recurrence(WMRS + fileType + STRING_001_01, cbFilepath + fileZip);
        }
        /** 根据挡板标识来确认是否打开 end**/
        logger.info("CnBondInterfService 报送中债处理结束：" + DateUtil.getCurrentDateTime());
        File a = new SafelyFile("./AppCfg/DC/cfg");
        if (asynReportEntity == null) {
            // printLog(log, DictCodeList.接口类型_发送联机交易.getValue(), FAILURE, WARNING,
            // "返回报文为空,文件地址：" + a.exists(), true);
            interfLogList.addAll(createLogList(log, DictCodeListExt2.接口类型_发送联机交易.getValue(), FAILURE,
                WARNING, "返回报文为空,文件地址：" + a.exists(), true));

            return;
        }
        // 登陸中债直连报送直連表 中债报送关系
        saveData(asynReportEntity, registSeqnos, dealType, sysDate, chbInfoReportList,
            chbSubmitStatusList,para);
        // printLog(log, DictCodeList.接口类型_结束.getValue(), SUCCESS, UNWARNING, null, false);
        interfLogList.addAll(
            createLogList(log, DictCodeListExt2.接口类型_结束.getValue(), SUCCESS, UNWARNING, null, false));
    }
    AsynReportEntity markDangBan(){
        AsynReportEntity as=new AsynReportEntity();
        AsynDocumentEntity document=new AsynDocumentEntity();
        as.setDocument(document);
        AsynSysXcptnEntity sysXcptn =new AsynSysXcptnEntity();
        document.setSysXcptn(sysXcptn);
        CommonNodeEntity fileId=new CommonNodeEntity();
        CommonNodeEntity xcptnRtrCd=new CommonNodeEntity();
        CommonNodeEntity xcptnRtrTx=new CommonNodeEntity();
        fileId.setValue("01");
        xcptnRtrCd.setValue("0000");
        xcptnRtrTx.setValue("01");
        sysXcptn.setFileId(fileId);
        sysXcptn.setXcptnRtrCd(xcptnRtrCd);
        sysXcptn.setXcptnRtrTx(xcptnRtrTx);
        AsynMsgHeaderEntity msgHeader=new AsynMsgHeaderEntity();
        AsynBizInfoEntity bizInfo=new AsynBizInfoEntity();
        CommonNodeEntity origMsgId=new CommonNodeEntity();
        origMsgId.setValue(getSequenceIndexData(DataIndexSequenceType.接口应用, FROMAT_18));
        bizInfo.setOrigMsgId(origMsgId);
        msgHeader.setBizInfo(bizInfo);
        as.setMsgHeader(msgHeader);
        return as;
    }
    /**
     * 附件处理
     *
     * @param bases 基本信息
     * @param files 文件list
     * @param registSeqnos 流水号
     * @param resourceFilePath 上传路径
     * @see
     * @since 1.0
     */
    private void getFile(List<BaseInfoEntity> bases, List<File> files,
                         List<String> resourceFilePath, List<String> registSeqnos) {
        // 基本信息节点list
        List<CommonNodeListEntity> nodelists = new ArrayList<CommonNodeListEntity>();
        for (BaseInfoEntity base : bases) {
            if (base.getRegistrationSerialNumber() != null) {
                // 登记流水号
                registSeqnos.add(getVaule(base.getRegistrationSerialNumber()));
            }
            else if (base.getUnderlyingAssetTuple() != null) {
                for (CommonNodeListEntity entity : base.getUnderlyingAssetTuple()) {
                    registSeqnos.add(getVaule(entity.getRegistrationSerialNumber()));
                }
            }
            if (base.getMainDocumentsTuple() != null) {
                // 报告主文件
                nodelists.addAll(base.getMainDocumentsTuple());
            }
            if (base.getWealthManagementProductFeasibilityReportsTuple() != null) {
                // 理财产品可行性评估报告
                nodelists.addAll(base.getWealthManagementProductFeasibilityReportsTuple());
            }
            if (base.getInternalAuthorizedDocumentsTuple() != null) {
                // 内部审核文件
                nodelists.addAll(base.getInternalAuthorizedDocumentsTuple());
            }
            if (base.getDueDiligenceDocumentsOnRelevantPartiesTuple() != null) {
                // 对理财产品投资管理人、托管人、投资顾问等相关方的尽职调查文件
                nodelists.addAll(base.getDueDiligenceDocumentsOnRelevantPartiesTuple());

            }
            if (base.getLegalDocumentsSignedByRelevantPartiesTuple() != null) {
                // 与理财产品投资管理人、托管人、投资顾问等相关方签署的法律文件
                nodelists.addAll(base.getLegalDocumentsSignedByRelevantPartiesTuple());

            }
            if (base.getWealthManagementProductSalesDocumentsTuple() != null) {
                // 理财产品销售文件
                nodelists.addAll(base.getWealthManagementProductSalesDocumentsTuple());

            }
            if (base.getWealthManagementProductSpecificationsTuple() != null) {
                // 理财产品说明书
                nodelists.addAll(base.getWealthManagementProductSpecificationsTuple());

            }
            if (base.getWealthManagementProductMarketingDocumentsTuple() != null) {
                // 理财产品宣传材料
                nodelists.addAll(base.getWealthManagementProductMarketingDocumentsTuple());

            }
            if (base.getOtherDocumentsTuple() != null) {
                // 其他材料
                nodelists.addAll(base.getOtherDocumentsTuple());
            }

        }
        // nodeList 为空
        if (CollectionUtils.isEmpty(nodelists)) {
            return;
        }
        // 基本信息子节点list
        List<CommonNodeEntity> subnodelists = new ArrayList<CommonNodeEntity>();
        for (CommonNodeListEntity node : nodelists) {
            // 报告主文件
            if (node.getMainDocuments() != null) {
                subnodelists.add(node.getMainDocuments());
            }
            // 理财产品可行性评估报告
            if (node.getWealthManagementProductFeasibilityReports() != null) {
                subnodelists.add(node.getWealthManagementProductFeasibilityReports());
            }
            // 内部审核文件
            if (node.getInternalAuthorizedDocuments() != null) {
                subnodelists.add(node.getInternalAuthorizedDocuments());
            }
            // 对理财产品投资管理人、托管人、投资顾问等相关方的尽职调查文件
            if (node.getDueDiligenceDocumentsOnRelevantParties() != null) {
                subnodelists.add(node.getDueDiligenceDocumentsOnRelevantParties());
            }
            // 与理财产品投资管理人、托管人、投资顾问等相关方签署的法律文件
            if (node.getLegalDocumentsSignedByRelevantParties() != null) {
                subnodelists.add(node.getLegalDocumentsSignedByRelevantParties());
            }
            // 理财产品销售文件
            if (node.getWealthManagementProductSalesDocuments() != null) {
                subnodelists.add(node.getWealthManagementProductSalesDocuments());
            }
            // 理财产品说明书
            if (node.getWealthManagementProductSpecifications() != null) {
                subnodelists.add(node.getWealthManagementProductSpecifications());
            }
            // 理财产品宣传材料
            if (node.getWealthManagementProductMarketingDocuments() != null) {
                subnodelists.add(node.getWealthManagementProductMarketingDocuments());
            }
            // 其他材料
            if (node.getOtherDocuments() != null) {
                subnodelists.add(node.getOtherDocuments());
            }
        }
        // 循环基本信息子节点list
        for (CommonNodeEntity subnode : subnodelists) {
            // 获取文件路径
            String vaule = subnode.getValue();
            if (StringUtils.isEmpty(vaule)) {
                break;
            }
            // 解析 参数
            String[] str = vaule.split(SPLIT_STRING1);
            if (str.length < 3) {
                break;
            }
            // // 登记编号
            // String serialNumber = str[0];
            // // 文件No
            // String fileNo = str[1];
            // // 文件名称
            // String fileName = str[2];
            // MstUploadInfo info = mstUploadInfoMapper.selectByPrimaryKey(fileNo);
            // File file = new File(info.getPath());
            // // 添加文件
            // files.add(file);
            // // 获取上传文件路径
            // fileName = serialNumber + InterfConstant.VIRGULE + fileName;
            // // 设置上传文件路径
            // resourceFilePath.add(fileName);
            // // 上传文件路径
            // subnode.setValue(fileName);
        }
    }

    /**
     * 业务Entity 设值
     *
     * @param bases 基本信息List
     * @param infos 信息
     * @param contextRefIds contextRefIds
     * @param log 日志
     * @return 需要使用的单位
     * @see
     * @since 1.0
     */
    private Map<String, XbrliUnitEntity> setEntity(List<BaseInfoEntity> bases,
                                                   List<Map<String, CnBondInterfItem>> infos,
                                                   String[] contextRefIds, InterfLog log) {
        // 全部单位标签
        Map<String, XbrliUnitEntity> mapUtils = getUnits();
        // 使用的单位标签
        Map<String, XbrliUnitEntity> returnMaps = new LinkedHashMap<String, XbrliUnitEntity>();
        // 循环处理
        for (Map<String, CnBondInterfItem> map : infos) {
            // 基本信息
            BaseInfoEntity entity = new BaseInfoEntity();
            // 对基本信息设值
            setBaseInfo(map, entity, contextRefIds, mapUtils, returnMaps, log);
            // 添加基本信息
            bases.add(entity);
        }
        return returnMaps;
    }

    /**
     * 获取 xbrli:unit MAP
     *
     * @return 返回所有的单位map
     * @see
     * @since 1.0
     */
    private Map<String, XbrliUnitEntity> getUnits() {
        Map<String, XbrliUnitEntity> map = new HashMap<String, XbrliUnitEntity>();

        // 人民币
        XbrliUnitEntity cny = new XbrliUnitEntity();
        cny.setId(DictCodeListExt2.币种全称_中债_人民币.getValue());
        cny.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_人民币.getValue());
        map.put(DictCodeListExt2.币种全称_中债_人民币.getValue(), cny);

        // 港元(HKD)
        XbrliUnitEntity hkd = new XbrliUnitEntity();
        hkd.setId(DictCodeListExt2.币种全称_中债_港元.getValue());
        hkd.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_港元.getValue());
        map.put(DictCodeListExt2.币种全称_中债_港元.getValue(), hkd);

        // 美元(USD)
        XbrliUnitEntity usd = new XbrliUnitEntity();
        usd.setId(DictCodeListExt2.币种全称_中债_美元.getValue());
        usd.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_美元.getValue());
        map.put(DictCodeListExt2.币种全称_中债_美元.getValue(), usd);

        // 欧元(EUR)
        XbrliUnitEntity eur = new XbrliUnitEntity();
        eur.setId(DictCodeListExt2.币种全称_中债_欧元.getValue());
        eur.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_欧元.getValue());
        map.put(DictCodeListExt2.币种全称_中债_欧元.getValue(), eur);

        // 日元(JPY)
        XbrliUnitEntity jpy = new XbrliUnitEntity();
        jpy.setId(DictCodeListExt2.币种全称_中债_日元.getValue());
        jpy.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_日元.getValue());
        map.put(DictCodeListExt2.币种全称_中债_日元.getValue(), jpy);

        // 澳门元(MOP)
        XbrliUnitEntity mop = new XbrliUnitEntity();
        mop.setId(DictCodeListExt2.币种全称_中债_澳门元.getValue());
        mop.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_澳门元.getValue());
        map.put(DictCodeListExt2.币种全称_中债_澳门元.getValue(), mop);

        // 瑞士法郎(CHF)
        XbrliUnitEntity chf = new XbrliUnitEntity();
        chf.setId(DictCodeListExt2.币种全称_中债_瑞士法郎.getValue());
        chf.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_瑞士法郎.getValue());
        map.put(DictCodeListExt2.币种全称_中债_瑞士法郎.getValue(), chf);

        // 英镑(GBP)
        XbrliUnitEntity gbp = new XbrliUnitEntity();
        gbp.setId(DictCodeListExt2.币种全称_中债_英镑.getValue());
        gbp.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_英镑.getValue());
        map.put(DictCodeListExt2.币种全称_中债_英镑.getValue(), gbp);

        // 俄罗斯卢布(RUB)
        XbrliUnitEntity rub = new XbrliUnitEntity();
        rub.setId(DictCodeListExt2.币种全称_中债_俄罗斯卢布.getValue());
        rub.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_俄罗斯卢布.getValue());
        map.put(DictCodeListExt2.币种全称_中债_俄罗斯卢布.getValue(), rub);

        // 加拿大元(CAD)
        XbrliUnitEntity cad = new XbrliUnitEntity();
        cad.setId(DictCodeListExt2.币种全称_中债_加拿大元.getValue());
        cad.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_加拿大元.getValue());
        map.put(DictCodeListExt2.币种全称_中债_加拿大元.getValue(), cad);

        // 丹麦克朗(DKK)
        XbrliUnitEntity dkk = new XbrliUnitEntity();
        dkk.setId(DictCodeListExt2.币种全称_中债_丹麦克朗.getValue());
        dkk.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_丹麦克朗.getValue());
        map.put(DictCodeListExt2.币种全称_中债_丹麦克朗.getValue(), dkk);

        // 澳大利亚元(AUD)
        XbrliUnitEntity aud = new XbrliUnitEntity();
        aud.setId(DictCodeListExt2.币种全称_中债_澳大利亚元.getValue());
        aud.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_澳大利亚元.getValue());
        map.put(DictCodeListExt2.币种全称_中债_澳大利亚元.getValue(), aud);

        // 瑞典克朗(SEK)
        XbrliUnitEntity sek = new XbrliUnitEntity();
        sek.setId(DictCodeListExt2.币种全称_中债_瑞典克朗.getValue());
        sek.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_瑞典克朗.getValue());
        map.put(DictCodeListExt2.币种全称_中债_瑞典克朗.getValue(), sek);

        // 新加坡元（SGD）
        XbrliUnitEntity sgd = new XbrliUnitEntity();
        sgd.setId(DictCodeListExt2.币种全称_中债_新加坡元.getValue());
        sgd.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_新加坡元.getValue());
        map.put(DictCodeListExt2.币种全称_中债_新加坡元.getValue(), sgd);

        // 新台币（TWD）
        XbrliUnitEntity twd = new XbrliUnitEntity();
        twd.setId(DictCodeListExt2.币种全称_中债_新台币.getValue());
        twd.setXbrliMeasure(ISO_4217 + GlobalConstant.COLON + DictCodeListExt2.币种全称_中债_新台币.getValue());
        map.put(DictCodeListExt2.币种全称_中债_新台币.getValue(), twd);

        // Day 天
        XbrliUnitEntity day = new XbrliUnitEntity();
        day.setId(DAY);
        day.setXbrliMeasure(WEMAX_UNIT_DAY);
        map.put(DAY, day);
        // Month 月
        XbrliUnitEntity month = new XbrliUnitEntity();
        month.setId(MONTH);
        month.setXbrliMeasure(WEMAX_UNIT_MONTH);
        map.put(MONTH, month);
        // Piece 个
        XbrliUnitEntity piece = new XbrliUnitEntity();
        piece.setId(PIECE);
        piece.setXbrliMeasure(WEMAX_UNIT_PIECE);
        map.put(PIECE, piece);
        // PURE 份
        XbrliUnitEntity pure = new XbrliUnitEntity();
        pure.setId(PURE);
        pure.setXbrliMeasure(XBRLI_PURE);
        map.put(PURE, pure);
        // MonthPerCount(一次几个月)
        XbrliUnitEntity monthPerCount = new XbrliUnitEntity();
        monthPerCount.setId(MONTHPERCOUNT);
        XbrliDivide xbrliDivide = new XbrliDivide();
        XbrliuUnitDenominator xbrliUnitNumerator = new XbrliuUnitDenominator();
        xbrliUnitNumerator.setXbrliMeasure(WEMAX_UNIT_MONTH);
        XbrliuUnitDenominator xbrliuUnitDenominator = new XbrliuUnitDenominator();
        xbrliuUnitDenominator.setXbrliMeasure(WEMAX_UNIT_COUNT);
        xbrliDivide.setXbrliUnitNumerator(xbrliUnitNumerator);
        xbrliDivide.setXbrliuUnitDenominator(xbrliuUnitDenominator);
        monthPerCount.setXbrliDivide(xbrliDivide);
        map.put(MONTHPERCOUNT, monthPerCount);
        return map;
    }

    /**
     * 获取 ContextEntity
     *
     * @param date 业务日期
     * @return ContextEntity
     * @see
     * @since 1.0
     */
    private XbrliContextEntity getContextEntity1(Date date, String registBankCode) {
        // 业务日期 yyyyMMdd
        String sysDateCD = DateUtil.format(date, DateUtil.DEFAULT_OTHER_FORMAT);
        String sysDate = DateUtil.format(date, DateUtil.DEFAULT_DATE_FORMAT_EN);
        // 获取业务日期前一天
        Date dateBefore = DateUtil.addDay(date, -1);
        // 业务日期前一天
        String sysDateBefore = DateUtil.format(dateBefore, DateUtil.DEFAULT_DATE_FORMAT_EN);
        String sysDateBeforeCD = DateUtil.format(dateBefore, DateUtil.DEFAULT_OTHER_FORMAT);
        // 标签 xbrli:context
        XbrliContextEntity entity = new XbrliContextEntity();
        // XbrliContext 属性 ID
        entity.setXbrliContextEntityId(
            CD + sysDateBeforeCD + InterfConstant.FILE_MIDLINE + sysDateCD);
        // 标签xbrli:entity
        XbrliEntity xbrliEntity = new XbrliEntity();
        // 标签xbrli:identifier
        XbrliIdentifierEntity xbrliIdentifierEntity = new XbrliIdentifierEntity();
        // 标签xbrli:identifier scheme属性
        xbrliIdentifierEntity.setScheme(SCHNAME);
        // 标签xbrli:identifier值
        xbrliIdentifierEntity.setXbrliIdentifier(registBankCode);
        xbrliEntity.setEntityXbrliIdentifier(xbrliIdentifierEntity);
        entity.setXbrliEntity(xbrliEntity);
        // 标签 xbrli:period
        XbrliPeriodEntity periodEntity = new XbrliPeriodEntity();
        // startDate
        periodEntity.setXbrliStartDate(sysDateBefore);
        // endDate
        periodEntity.setXbrliEndDate(sysDate);
        entity.setXbrliPeriodEntity(periodEntity);
        return entity;
    }

    /**
     * 获取 ContextEntity
     *
     * @param date 业务日期
     * @return ContextEntity
     * @see
     * @since 1.0
     */
    private XbrliContextEntity getContextEntity2(Date date, String registBankCode) {
        // 业务日期 yyyyMMdd DEFAULT_DATE_FORMAT_EN
        String sysDateCI = DateUtil.format(date, DateUtil.DEFAULT_OTHER_FORMAT);
        String sysDate = DateUtil.format(date, DateUtil.DEFAULT_DATE_FORMAT_EN);

        // 标签 xbrli:context
        XbrliContextEntity entity = new XbrliContextEntity();
        // XbrliContext 属性 ID
        entity.setXbrliContextEntityId(CI + sysDateCI);
        // 标签xbrli:entity
        XbrliEntity xbrliEntity = new XbrliEntity();
        // 标签xbrli:identifier
        XbrliIdentifierEntity xbrliIdentifierEntity = new XbrliIdentifierEntity();
        // 标签xbrli:identifier scheme属性
        xbrliIdentifierEntity.setScheme(SCHNAME);
        // 标签xbrli:identifier值
        xbrliIdentifierEntity.setXbrliIdentifier(registBankCode);
        xbrliEntity.setEntityXbrliIdentifier(xbrliIdentifierEntity);
        entity.setXbrliEntity(xbrliEntity);
        // 标签 xbrli:period
        XbrliPeriodEntity periodEntity = new XbrliPeriodEntity();
        // Instant
        periodEntity.setXbrliInstant(sysDate);
        entity.setXbrliPeriodEntity(periodEntity);
        return entity;
    }

    /**
     * 设值报文共通属性
     *
     * @param entity 报文信息
     * @see
     * @since 1.0
     */
    private void setCommmonInfo(BusinessForXMLEntity entity, String type) {
        if (!DictCodeListExt2.报表名称_分类总量信息登记.getValue().equals(type)) {
            // 标题1
            entity.setXmlnsXsi(TITLE_PROPERTY1);
            // 标题2
            entity.setXsiSchemaLocation(TITLE_PROPERTY2);
        }

        // 标题3
        entity.setXmlnsChinawealthEntryPoint(TITLE_PROPERTY3);
        // 标题4
        entity.setXmlnsLink(TITLE_PROPERTY4);
        // 标题5
        entity.setXmlnsCas(TITLE_PROPERTY5);
        // 标题6
        entity.setXmlnsNum(TITLE_PROPERTY6);
        // 标题7
        entity.setXmlnsNonnum(TITLE_PROPERTY7);
        // 标题8
        entity.setXmlnsIfrsFull(TITLE_PROPERTY8);
        // 标题9
        entity.setXmlnsXbrldt(TITLE_PROPERTY9);
        // 标题10
        entity.setRolChinawealth(TITLE_PROPERTY10);
        // 标题11
        entity.setXmlnsWemax(TITLE_PROPERTY11);
        if (DictCodeListExt2.报表名称_分类总量信息登记.getValue().equals(type)) {
            entity.setXmlnsXsi1(TITLE_PROPERTY1);
        }
        else {
            // 标题12
            entity.setXmlnsXbrldi(TITLE_PROPERTY12);
        }
        // 标题13
        entity.setXmlnsXbrli(TITLE_PROPERTY13);
        // 标题14
        entity.setXmlnsWei(TITLE_PROPERTY14);
        // 标题15
        entity.setXmlnsIso4217(TITLE_PROPERTY15);
        // 标题16
        entity.setXmlnsXlink(TITLE_PROPERTY16);
        LinkSchemaRefEntity linkSchemaRefEntity = new LinkSchemaRefEntity();
        // 标题17
        linkSchemaRefEntity.setXlinkHref(TITLE_PROPERTY17);
        // 报文 LINK_TYPE
        linkSchemaRefEntity.setXlinkType(LINK_TYPE);
        entity.setLinkSchemaRef(linkSchemaRefEntity);
    }

    /**
     * 将基本信息map放到entity里面
     *
     * @param items 基本信息项
     * @param entity 基本信息entity
     * @param contextRefIds contextRefIds
     * @param mapUtils 单位map
     * @param returnMap 返回值map
     * @param log 日志
     * @see
     * @since 1.0
     */
    private void setBaseInfo(Map<String, CnBondInterfItem> items, BaseInfoEntity entity,
                             String[] contextRefIds, Map<String, XbrliUnitEntity> mapUtils,
                             Map<String, XbrliUnitEntity> returnMap, InterfLog log) {
        // 循环处理每一项
        for (Entry<String, CnBondInterfItem> item : items.entrySet()) {
            // 逐项设值
            try {
                setBaseInfo(item.getKey(), item.getValue(), entity, contextRefIds);
                // 添加使用的标签
                returnMap.put(item.getValue().getUnitRef(),
                    mapUtils.get(item.getValue().getUnitRef()));
            }
            catch (Exception e) {
                logger.error("逐项设值失败：" + item.getKey());
                printLog(log, DictCodeListExt2.接口类型_报文转化.getValue(), FAILURE, WARNING,
                    "逐项设值失败：" + item.getKey(), false);

            }
        }
    }

    /**
     * 将基本信息map放到entity里面
     *
     * @param key 键值
     * @param item 基本信息项
     * @param entity 基本信息entity
     * @param contextRefIds contextRefIds
     * @throws SecurityException 异常
     * @throws NoSuchMethodException 异常
     * @throws InvocationTargetException 异常
     * @throws IllegalArgumentException 异常
     * @throws IllegalAccessException 异常
     * @see
     * @since 1.0
     */
    private void setBaseInfo(String key, CnBondInterfItem item, Feild entity,
                             String[] contextRefIds)
        throws NoSuchMethodException, SecurityException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {
        // 项目值
        String value = item.getValue();
        // check key and value
        if (StringUtils.isEmpty(key)) {
            return;
        }
        // 键值是否是循环结构
        if (isEndWithTuple(key) && StringUtils.isNotEmpty(value)) {

            // 基本信息标签list
            List<CommonNodeListEntity> nodes = new ArrayList<CommonNodeListEntity>();
            // 子标签
            String nodeKey = key.substring(0, key.length() - 5);
            // 以“，”拆分值
            for (String val : value.split(GlobalConstant.COMMA)) {
                // 基本信息标签
                CommonNodeListEntity node = new CommonNodeListEntity();
                // 基本信息子标签
                CommonNodeEntity subNode = new CommonNodeEntity();
                // 标签值
                subNode.setValue(val);
                // 精度
                subNode.setDecimals(item.getDecimals());
                // contextRef
                subNode.setContextRef(contextRefIds[0]);
                // 单位
                subNode.setUnitRef(item.getUnitRef());
                // 判断标签是否当日生效
                if (isNodeTodayAffect(nodeKey) && contextRefIds.length == 2) {
                    subNode.setContextRef(contextRefIds[1]);
                }
                // 反射设值
                Method m = node.getClass().getMethod(InterfConstant.SET + nodeKey,
                    CommonNodeEntity.class);
                m.invoke(node, subNode);
                // 添加标签
                nodes.add(node);
            }
            // 反射设值
            Method m = entity.getClass().getMethod(InterfConstant.SET + key, List.class);
            m.invoke(entity, nodes);
        }
        else if (isEndWithTuple(key) && item.getMaps() != null) {
            // 基本信息标签list
            List<CommonNodeListEntity> nodes = new ArrayList<CommonNodeListEntity>();
            for (Map<String, CnBondInterfItem> map : item.getMaps()) {
                // 基本信息标签
                CommonNodeListEntity node = new CommonNodeListEntity();
                // 循环 map
                for (Entry<String, CnBondInterfItem> subitem : map.entrySet()) {
                    setBaseInfo(subitem.getKey(), subitem.getValue(), node, contextRefIds);
                }
                nodes.add(node);
            }
            // 反射设值
            Method m = entity.getClass().getMethod(InterfConstant.SET + key, List.class);
            m.invoke(entity, nodes);
        }
        else if (StringUtils.isNotEmpty(value)) {

            // 基本信息标签
            CommonNodeEntity node = new CommonNodeEntity();
            // 标签值
            node.setValue(value);
            // 精度
            node.setDecimals(item.getDecimals());
            // contextRef
            node.setContextRef(contextRefIds[0]);
            // 单位
            node.setUnitRef(item.getUnitRef());

            // 判断标签是否当日生效
            if (isNodeTodayAffect(key) && contextRefIds.length == 2) {
                node.setContextRef(contextRefIds[1]);
            }
            // 反射设值
            Method m = entity.getClass().getMethod(InterfConstant.SET + key,
                CommonNodeEntity.class);
            m.invoke(entity, node);
        }
    }

    /**
     * 是否当日生效
     *
     * @param nodeKey 节点
     * @return 判断结果
     * @see
     * @since 1.0
     */
    private Boolean isNodeTodayAffect(String nodeKey) {
        // 当日生效标签数组
        String[] str = {"InvestmentThreshold", "InitialNetValue", "NetValueOfProduct",
            "AggregateNetValue", "InitialNetValueCNY", "NetValueOfProductCNY",
            "AggregateNetValueCNY", "PerUnitTransactionPriceNet", "PerUnitTransactionPriceFull",
            "Quantity", "PerUnitValuationNet", "PerUnitValuationFull",
            "AggregateIssuedQuantityOfAssetManagementPlan",
            "AggregateIssuedAmountOfAssetManagementPlanCNY",
            "NonInvestedAmountofAssetManagementPlanCNY", "QuantityOfUnderlyingAssetsLiabilities",
            "MarketValueOfUnderlyingAssetsLiabilitiesCNY"};
        // 循环标签数组
        for (String s : str) {
            // 判断标签是否在数组中
            if (s.equals(nodeKey)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否以Tuple结尾
     *
     * @param str 字符串
     * @return check 结果
     * @see
     * @since 1.0
     */
    private Boolean isEndWithTuple(String str) {
        // 判断标签长度
        if (StringUtils.isEmpty(str) || str.length() <= 5) {
            return false;
        }
        // 获取标签后五位
        String lst = str.substring(str.length() - 5, str.length());
        // 返回结果
        return TUPLE.equals(lst);
    }

    /**
     * 获取路径下文件 最大文件number
     *
     * @param path 路径
     * @param fileName 文件名
     * @return 最大文件number
     * @see
     * @since 1.0
     */
    private Integer getMaxFileNumber(String path, String fileName) {
        List<String> fileList = FileUtil.getFileList(path);
        // 最大文件number
        int maxFileNumber = 0;
        if (CollectionUtils.isEmpty(fileList)) {
            return 0;
        }
        for (String name : fileList) {
            // 判断文件是否包含code字符 并且有.dat
            if (StringUtils.isEmpty(name) || !name.contains(fileName)
                || !name.contains(FILE_XML)) {
                continue;
            }
            // 当前文件number
            String fileNumber = name.replace(fileName, "").replace(FILE_XML, "");
            if (!StringUtil.isNumeric(fileNumber)) {
                continue;
            }
            // 取束最大值
            if (maxFileNumber < Integer.parseInt(fileNumber)) {
                maxFileNumber = Integer.parseInt(fileNumber);
            }
        }
        return maxFileNumber;
    }

    /**
     * 登陸中债直连报送直連表 中债报送状态表
     *
     * @param asynReportEntity 发送返回信息
     * @param registSeqno 报送流水号
     * @param busitype 业务类型
     * @param sysDate 业务日期
     * @param chbInfoReportList 出参 批量录入中债直连报送
     * @param chbSubmitStatusList 出参 批量录入中债报送状态
     * @param para 报送数据信息
     * @see
     * @since 1.0
     */
    private void saveData(AsynReportEntity asynReportEntity, List<String> registSeqno,
                          String busitype, Date sysDate, List<ChbInfoReport> chbInfoReportList,
                          List<ChbSubmitStatus> chbSubmitStatusList, InterfCnBondSubmitPara para) {
        ChbInfoReport report = new ChbInfoReport();
        // 报送编码
        String reportNo = getSequenceIndexData(DataIndexSequenceType.接口应用, FROMAT_18);
        // 报送结果码
        String rcntCode = getVaule(asynReportEntity.getDocument().getSysXcptn().getXcptnRtrCd());
        // 报送编码
        report.setReportNo(reportNo);
        // 报送报文编码
        report.setRepMesNo(getVaule(asynReportEntity.getMsgHeader().getBizInfo().getOrigMsgId()));
        // 报送结果码
        report.setUploadResult(rcntCode);
        // 报送结果信息
        report.setUploadResultMsg(
            getVaule(asynReportEntity.getDocument().getSysXcptn().getXcptnRtrTx()));
        // 报送结果文件代码
        report.setUploadFileId(getVaule(asynReportEntity.getDocument().getSysXcptn().getFileId()));
        // 业务日期
        report.setSendTime(sysDate);
        // 报送条数
        report.setCheckTotalCount(registSeqno.size());
        // 有效
        report.setStatus(DictCodeListExt1.维护数据状态_有效.getValue());
        // chbInfoReportMapper.insertSelective(report);
        // 报送类型
        report.setSubmitType(busitype);
        
        chbInfoReportList.add(report);

        for (String seq : registSeqno) {
            // 中债报送状态
            ChbSubmitStatus submitStatus = new ChbSubmitStatus();
            // 报送序列号
            // submitStatus.setSeqno(getSequenceIndexData(DataIndexSequenceType.接口应用, FROMAT_18));
            submitStatus.setSeqno(seq);
            // 报送编码
            submitStatus.setReportNo(reportNo);
            // 报送流水号
            submitStatus.setSubmitNum(seq);
            // 业务类型
            submitStatus.setSubmitType(busitype);
            //识别标识  通过流水号获取
            submitStatus.setUniqueIdenti(para.getRegistSeqKeyMap().get(seq));
            submitStatus.setEquityConfDate(para.getEquityConfDate());
            if (XCPTNRTRCD_0000.equals(rcntCode)) {
                submitStatus.setSubmitFeedbackStatus(DictCodeListExt2.报送反馈状态_发送成功.getValue());
            }
            else {
                submitStatus.setSubmitFeedbackStatus(DictCodeListExt2.报送反馈状态_发送失败.getValue());
            }
            // 有效
            submitStatus.setStatus(DictCodeListExt1.维护数据状态_有效.getValue());
            // chbSubmitStatusMapper.insertSelective(submitStatus);
            chbSubmitStatusList.add(submitStatus);
        }
    }

    /**
     * @param entity entity
     * @return 值
     */
    private String getVaule(CommonNodeEntity entity) {
        if (entity == null) {
            return null;
        }
        return entity.getValue();
    }

    /**
     * 轮询中债报送结果
     *
     * @param sysDate 业务日期
     * @see
     * @since 1.0
     */
    public void getMessage(Date sysDate) {
        logger.info("开始处理读取中债报送反馈结果文件_getMessage_start");

        // 早上06：00至晚上22:00为中债开放时间，其余时间段限制不去调用中债API获取反馈消息
        try {
            // 当前系统时间
            String nowTimeStr = DateUtil.format(DateUtil.now(), "HHmm");

            // 调用中债API获取反馈消息开始时间
            String repCbGetmessageStarttime =
                SvrDataUtil.getSysParam(ReportGlobalConstant.SYS_PARA_REP_CB_GETMESSAGE_STARTTIME);
            // 调用中债API获取反馈消息结束时间
            String repCbGetMessageEndtime =
                SvrDataUtil.getSysParam(ReportGlobalConstant.SYS_PARA_REP_CB_GETMESSAGE_ENDTIME);

            if (nowTimeStr.compareTo(repCbGetmessageStarttime) < 0
                || nowTimeStr.compareTo(repCbGetMessageEndtime) > 0) {
                logger.info("当前系统时间不在调用中债API获取反馈消息规定时间段内，本次处理结束_getMessage_end");

                return;
            }
        }
        catch (Exception e) {
        	logger.error(e.getMessage(),e);
        }

        Map<String, String> serverMap = ExtFprDClient.getDimServer();
        if (null != serverMap && !serverMap.isEmpty()) {
            serverMap.forEach((regBankCode, serverUrl) -> {
                ExtFprDClient.setRegBankCode(regBankCode);
                getMessageSigle(sysDate);
            });
        }
        else {
            getMessageSigle(sysDate);
        }
        logger.info("中债报送反馈结果文件读取处理完成_getMessage_end");
    }

    /**
     * 轮询中债报送结果(单个直连机)
     *
     * @param sysDate 业务日期
     * @see
     * @since 1.0
     */
    private void getMessageSigle(Date sysDate) {
        // 轮询中债报送结果
        List<AsynReportEntity> asynReportEntitys = CnBondUtil.getMessage(MSG_KIND_20);
        for (AsynReportEntity asynReportEntity : asynReportEntitys) {
            try {
                logger.info("analysisData开始");
                analysisData(asynReportEntity, sysDate);
                logger.info("analysisData结束");
            }
            catch (Exception e) {
                logger.error("analysisData出现异常，异常内容：" + e.getMessage(),e);
                continue; // 当前报错跳过执行下一条
            }
        }
        // 获取产品登记编码用
        List<AsynReport40Entity> asynReportEntitys2 = CnBondUtil.getMessageFor40(MSG_KIND_40);
        for (AsynReport40Entity asynReportEntity : asynReportEntitys2) {
            try {
                logger.info("getProdRecode开始");
                getProdRecode(asynReportEntity, sysDate);
                logger.info("getProdRecode结束");
            }
            catch (Exception e) {
                logger.error("getProdRecode出现异常，异常内容：" + e.getMessage(),e);
                continue; // 当前报错跳过执行下一条
            }
        }
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
    private void getProdRecode(AsynReport40Entity asynReportEntity, Date sysDate) {
        // 下载文件地址
        String filePath = getParaVauleByparaCode(InterfConstant.KEY_CB_DOWNLOAD_FILEPATH);
        // 文件ID
//        String fileId = asynReportEntity.getDocument().getFileId();
        String fileId = asynReportEntity.getFileId();
        // 确认文件名
//        String fileName = asynReportEntity.getDocument().getFileName();
        String fileName = asynReportEntity.getFileName();
        InterfLog log = new InterfLog();
        log.setInterfId(DictCodeListExt2.接口系统ID_获取产品登记编码.getValue());
        log.setBizDate(DateUtil.format(sysDate, DateUtil.DEFAULT_OTHER_FORMAT));
        printLog(log, DictCodeListExt2.接口类型_开始.getValue(), SUCCESS, UNWARNING, null, false);
        String fileXml = CnBondUtil.getFile(fileId, filePath);
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
                    String prdId = product != null ? product.getValue() : preIdentificationCode.getValue();
                    // 产品登记代码
                    String prodRecode = productRegistrationCode.getValue();
                    // modify by etliu 20180829 start
//                  // 更新产品登记编码
//                  ChbPrdresistCodeKey key = new ChbPrdresistCodeKey();
//                  key.setProdid(prdId);
//                  key.setOrgCode(StringUtil.substring(prodRecode, 0, 6));
//                  ChbPrdresistCode record = chbPrdresistCodeMapper.selectByPrimaryKey(key);
//                  if (record == null) {
//                      record = new ChbPrdresistCode();
//                      // 产品登记编码
//                      record.setRegCode(prodRecode);
//                      // 产品名称
//                      // record.setProdname(prodname);
//                      // 产品起息日
//                      // record.setVdate(vdate);
//                      // 产品到期日
//                      // record.setMdate(mdate);
//                      // 投资者类型
//                      // record.setInvestor(investor);
//                      record.setRemark("中债直连反馈");
//                      chbPrdresistCodeMapper.insertSelective(record);
//                  }
//                  else {
//                      // 产品登记编码
//                      record.setRegCode(prodRecode);
//                      chbPrdresistCodeMapper.updateByPrimaryKeySelective(record);
//                  }
                    // 登记银行代码
                    String registBankCode = StringUtil.substring(prodRecode, 0, 6);
                    // 根据登记银行代码获取机构代码
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("registBankCode", registBankCode);
                    String orgCode = cnBondInterfMapper.selectOrgCodeByRegistBankCode(paramMap);

                    // 查询产品登记编码表中是否存在该产品
                    ChbPrdresistCodeKey key = new ChbPrdresistCodeKey();
                    //modify by liubin start 以产品登记编码作为维护进行查看
                    key.setReg_code(prodRecode);
//                    key.setProdid(prdId);
                    //modiy by liubin end
                    key.setOrgCode(orgCode);
                    key.setFlag("1");
                    
                    ChbPrdresistCode record = chbPrdresistCodeMapper.selectByPrimaryKey(key);
                    if (record == null) {
                        record = new ChbPrdresistCode();
                        // 产品代码
                        record.setProdid(prdId);
                        // 机构代码
                        record.setOrgCode(orgCode);
                        // 产品登记编码
                        record.setRegCode(prodRecode);
                        record.setRemark("中债直连反馈");
                        chbPrdresistCodeMapper.insertSelective(record);
                    }
                    else {
                    	/*
                    	 * modify by liubin start 若找到记录，判断产品代码与中债回灌的产品代码是否一致、
                    	 * 若一致，不做任何操作，
                    	 * 若不一致，则将产品登记编码中旧的产品代码对应的持有信息产品代码全部更新成回灌的产品代码，更新完成后再将旧数据更新
                    	 */
                        // 产品登记编码
//                        record.setRegCode(prodRecode);
                        record.setNewProdId(prdId);
                        if(prdId != null && !prdId.equals(record.getProdid())){
                        	chbPrdresistCodeMapper.updateByPrimaryKeySelective(record);
                        }
//                        chbPrdresistCodeMapper.updateByPrimaryKeySelective(record);
                    }
                    // modify by etliu 20180829 end
                    coverInvestorsList.add(record);
                }
                printLog(log, DictCodeListExt2.接口类型_报文转化.getValue(), SUCCESS, UNWARNING, null, true);

               logger.info("更新持有信息产品登记编码开始......");
               investorsHoldInfo001InterfService.refreshStatusAData(coverInvestorsList);
               logger.info("更新持有信息产品登记编码结束......");
            }
            else {
                printLog(log, DictCodeListExt2.接口类型_报文转化.getValue(), FAILURE, WARNING, "解析结果失败", true);
            }
        }
    }

    /**
     * 分析message
     *
     * @param asynReportEntity 返回信息
     * @param fdc 中债客户端
     * @param sysDate 业务日期
     * @see
     * @since 1.0
     */
    private void analysisData(AsynReportEntity asynReportEntity, Date sysDate) {
        // 下载文件地址
        String filePath = getParaVauleByparaCode(InterfConstant.KEY_CB_DOWNLOAD_FILEPATH);
        // 中债直连数据
        ChbInfoReport report = new ChbInfoReport();
        // 报送报文编码
        String repMesNo = asynReportEntity.getMsgHeader().getBizInfo().getOrigMsgId().getValue();
        // 报送报文编码
        report.setRepMesNo(repMesNo);
        InterfLog log = new InterfLog();
        log.setInterfId(DictCodeListExt2.接口系统ID_直连结果分析.getValue());
        log.setBizDate(DateUtil.format(sysDate, DateUtil.DEFAULT_OTHER_FORMAT));
        printLog(log, DictCodeListExt2.接口类型_开始.getValue(), SUCCESS, UNWARNING, null, false);

        // 查询 报送编码
        List<ChbInfoReport> reports = cnBondInterfMapper.selectReportNoByRepMesNo(report);
        if (CollectionUtils.isEmpty(reports)) {
            printLog(log, DictCodeListExt2.接口类型_检索数据.getValue(), FAILURE, WARNING,
                "报送编码 不存在" + repMesNo, true);
            return;
        }
        String submitType=reports.get(0).getSubmitType();
        // 报送编码
        String reportNo = reports.get(0).getReportNo();
        // 报送编码
        report.setReportNo(reportNo);
        // 文件ID
        String fileId = asynReportEntity.getDocument().getFileId();
        // 确认文件名
        String fileName = asynReportEntity.getDocument().getFileName();
        // 确认文件ID
        report.setCheckFileId(fileId);
        // 确认结果码
        report.setCheckResult(asynReportEntity.getDocument().getErrorCode());
        // 确认结果消息
        report.setCheckResultMsg(asynReportEntity.getDocument().getErrorText());
        // 确认文件名
        report.setCheckFileName(fileName);
        // 确认总件数
        report.setCheckTotalCount(parseInt(asynReportEntity.getDocument().getTotalCount()));
        // 确认成功件数
        report.setCheckSuccessCount(parseInt(asynReportEntity.getDocument().getSuccessCount()));
        // 确认失败件数
        report.setCheckFailedCount(parseInt(asynReportEntity.getDocument().getFailedCount()));
        String fileXml = CnBondUtil.getFile(fileId, filePath);
        String fileNo = null;
        // 解析结果
//        XbrliDownLoadXml xml = null;
        List<XbrliDownLoadXml> xmlList = new ArrayList<XbrliDownLoadXml>();

        // 下载成功时
        logger.info("根据下载文件更新反馈状态开始");
        if (StringUtils.isNotEmpty(fileXml)) {
            // 上传下载文件
            // fileNo = uploadFile(filePath, fileName);
            // 解析下载文件
//            xml = analysisFile(filePath, fileName);
            xmlList = this.analysisXmlFileList(filePath, fileName);
            //投资者明细信息反馈状态错误集合
            List<ChbSubmitStatus> equdeterros=new ArrayList<ChbSubmitStatus>();
            //投资者明细信息反馈状态成功集合
            List<ChbSubmitStatus> equdetinfos=new ArrayList<ChbSubmitStatus>();
            for (XbrliDownLoadXml xml : xmlList) {
//            if (xml != null) {
                // 错误信息
                List<InformationEntity> errors = xml.getErrorInformationTuple() != null ? xml.getErrorInformationTuple() : new ArrayList<InformationEntity>();
                // 循环错误信息
                for (InformationEntity info : errors) {
                    // 更新中债报送结果
                    ChbSubmitStatus status = new ChbSubmitStatus();
                    // 报送编码
                    status.setReportNo(reportNo);
                    // 登记序号
                    status.setSubmitNum(info.getRegistrationSerialNumber().getValue());
                    // 反馈状态
                    status.setSubmitFeedbackStatus(DictCodeListExt2.报送反馈状态_中债退回.getValue());
                    // 错误信息
                    status.setSubmitFeedbackMsg(info.getErrorMessage().getValue());
                    /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
                    status.setSubmitType(submitType);
                    /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
                    // 反馈状态
                    // 更新中债报送关系
                    if(DictCodeListExt2.报表名称_投资者明细信息登记.getValue().equals(submitType)){
                        equdeterros.add(status);
                    }else{
                        cnBondInterfMapper.updateChbSubmitStatus(status);
                    }
                }
                // 成功信息
                List<InformationEntity> infos = xml.getCorrectInformationTuple() != null ? xml.getCorrectInformationTuple() : new ArrayList<InformationEntity>();
                // 循环成功信息
                for (InformationEntity info : infos) {
                    // 更新中债报送结果
                    ChbSubmitStatus status = new ChbSubmitStatus();
                    // 报送编码
                    status.setReportNo(reportNo);
                    // 登记序号
                    status.setSubmitNum(info.getRegistrationSerialNumber().getValue());
                    // 反馈状态
                    status.setSubmitFeedbackStatus(DictCodeListExt2.报送反馈状态_中债通过.getValue());
                    /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
                    status.setSubmitType(submitType);
                    /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
                    // 更新数据
                    if(DictCodeListExt2.报表名称_投资者明细信息登记.getValue().equals(submitType)){
                        equdetinfos.add(status);
                    }else{
                        cnBondInterfMapper.updateChbSubmitStatus(status);
                    }
                }
            }
            if(DictCodeListExt2.报表名称_投资者明细信息登记.getValue().equals(submitType)){
                //如果成功数不为0 全部更新为成功再一个一个更新失败
                if(!StringUtil.isEmptyList(equdetinfos)){
                	logger.info("明细登记更新【成功状态】开始=============>>>>>>>>>>");
                    // 更新中债报送结果
                    ChbSubmitStatus status = new ChbSubmitStatus();
                    // 报送编码
                    status.setReportNo(reportNo);
                    // 反馈状态
                    status.setSubmitFeedbackStatus(DictCodeListExt2.报送反馈状态_中债通过.getValue());
                    // 更新数据
                    cnBondInterfMapper.updateChbEquDetSubmitStatus(status);
                	logger.info("明细登记更新【成功状态】结束=============<<<<<<<<<<<");
                }
            	int resultCnt = 0;
            	if(!StringUtil.isEmptyList(equdeterros)){
            		logger.info("明细登记更新【失败状态】开始=============>>>>>>>>>>");
                     resultCnt = equdeterros.size();
                     mergerChbEqudetStatus(equdeterros, resultCnt);
             		logger.info("明细登记更新【失败状态】结束=============<<<<<<<<<<");
            	}
            }
        }
//        if (xml == null) {
        if (StringUtil.isEmptyList(xmlList)) {
            // 更新中债报送结果
            ChbSubmitStatus status = new ChbSubmitStatus();
            // 报送编码
            status.setReportNo(reportNo);
            // 反馈状态
            status.setSubmitFeedbackStatus(DictCodeListExt2.报送反馈状态_审核文件下载失败.getValue());
            /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
            status.setSubmitType(submitType);
            /***三期CHB_SUBMIT_STATUS去除改造  20220310**/
            // 更新数据
            if(DictCodeListExt2.报表名称_投资者明细信息登记.getValue().equals(submitType)){
                cnBondInterfMapper.updateChbEquDetSubmitStatus(status);
            }else{
                cnBondInterfMapper.updateChbSubmitStatus(status);
            }
        }
        // 内部文件代码
        report.setInsideFileId(fileNo);
        // 确认时间
        report.setReplyTime(sysDate);
        chbInfoReportMapper.updateByPrimaryKeySelective(report);
        printLog(log, DictCodeListExt2.接口类型_结束.getValue(), SUCCESS, UNWARNING, null, false);

        logger.info("根据下载文件更新反馈状态结束");
    }

    private void mergerChbEqudetStatus(List<ChbSubmitStatus> equdetinfos, int resultCnt) {
		int updateSize = 500;
		if (resultCnt <= updateSize) {
			cnBondInterfMapper.mergerChbEquDetSubmitStatus(equdetinfos);
		}else{
		    int part = resultCnt / updateSize; // 分批数
		    List<ChbSubmitStatus> newDataList = new ArrayList<ChbSubmitStatus>();
		    newDataList.addAll(equdetinfos);
		    for (int i = 0; i < part; i++ ) {
		    	cnBondInterfMapper.mergerChbEquDetSubmitStatus(newDataList.subList(0, updateSize));
		        newDataList.subList(0, updateSize).clear();
		    }
		    // 最后剩下的数据
		    if (CollectionUtils.isNotEmpty(newDataList)) {
		    	cnBondInterfMapper.mergerChbEquDetSubmitStatus(newDataList);
		    }
		}
	}
    
    /**
     * 将字符串转成int
     *
     * @param str 字符串
     * @return 数字
     * @see
     * @since 1.0
     */
    private int parseInt(String str) {
        int i = 0;
        try {
            i = Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
        	logger.error("字符串转int异常" + e.getMessage(),e);
        }
        return i;

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
        List<String> files = CnBondUtil.disCompress(filePath + fileName, filePath);
        if (CollectionUtils.isEmpty(files)) {
            return xml;
        }
        List<String> datas = null;
        try {
            datas = FileUtil.getFile(filePath + files.get(0), "UTF-8");

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
        xml = CnBondUtil.converyToJavaBean(
            s.toString().replaceAll("xbrli:xbrl", "root").replaceAll(":", ""),
            XbrliDownLoadXml.class);
        return xml;
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

        List<String> files = CnBondUtil.disCompress(filePath + fileName, filePath);
        if (CollectionUtils.isEmpty(files)) {
            return xmlList;
        }

        List<String> datas = null;
        try {
            datas = FileUtil.getFile(filePath + files.get(0), "UTF-8");
        }
        catch (IOException e) {
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
            XbrliDownLoadXml xml = CnBondUtil.converyToJavaBean(content, XbrliDownLoadXml.class);

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
        if (StringUtil.isBlank(rowStr)) {
            return resultStr;
        }
        resultStr = rowStr.replaceAll("xbrli:xbrl", "root").replaceAll(":", "").trim();

        return resultStr;
    }

    //
    // /**
    // * 上传文件
    // *
    // * @param filePath 文件路径
    // * @param fileName 压缩包名称
    // * @return 文件代码
    // * @see
    // * @since 1.0
    // */
    // private String uploadFile(String filePath, String fileName) {
    // MstUploadInfo info = new MstUploadInfo();
    // // 文件代码
    // String seq = this.getSequenceIndexData(DataIndexSequenceType.系统管理, FROMAT_18);
    // // 文件名
    // info.setFileName(fileName);
    // // 上传路径
    // info.setPath(filePath);
    // // 备注
    // info.setRemark("中债报送文件");
    // // 有效
    // info.setStatus(DictCodeList.数据状态_有效.getValue());
    // // 文件代码
    // info.setFileNo(seq);
    // mstUploadInfoMapper.insert(info);
    // return seq;
    // }

    /**
     * 分批登录中债直连报送数据
     *
     * @param chbInfoReportList 中债直连报送数据
     * @see
     * @since 1.0
     */
    private void insertChbInfoReportListByPart(List<ChbInfoReport> chbInfoReportList) {
        if (StringUtil.isEmptyList(chbInfoReportList)) {
            return;
        }

        int maxDataCountOneTime = 100;

        // 报送数据
        int dataCount = chbInfoReportList.size();
        if (dataCount <= maxDataCountOneTime) {
            cnBondInterfMapper.insertChbInfoReportList(chbInfoReportList);
        }
        else {
            // 分批处理
            List<ChbInfoReport> newDataList = new ArrayList<ChbInfoReport>();
            newDataList.addAll(chbInfoReportList);

            int part = dataCount / maxDataCountOneTime; // 分批数
            for (int i = 0; i < part; i++ ) {
                cnBondInterfMapper.insertChbInfoReportList(
                    newDataList.subList(0, maxDataCountOneTime));
                newDataList.subList(0, maxDataCountOneTime).clear();
            }
            // 最后剩下的数据
            if (CollectionUtils.isNotEmpty(newDataList)) {
                cnBondInterfMapper.insertChbInfoReportList(newDataList);
            }
        }
    }

    /**
     * 分批登录中债报送状态数据
     *
     * @param chbInfoReportList 中债报送状态数据
     * @see
     * @since 1.0
     */
    private void insertChbSubmitStatusListByPart(List<ChbSubmitStatus> chbSubmitStatusList) {
        if (StringUtil.isEmptyList(chbSubmitStatusList)) {
            return;
        }

        /***三期CHB_SUBMIT_STATUS去除改造  同批次要么身份要么持有,不会同时进  20220310**/
        String SubmitsType = chbSubmitStatusList.get(0).getSubmitType();
        /***三期CHB_SUBMIT_STATUS去除改造  同批次要么身份要么持有,不会同时进 20220310**/

        int maxDataCountOneTime = 100;

        // 报送数据
        int dataCount = chbSubmitStatusList.size();
        if (dataCount <= maxDataCountOneTime) {
            cnBondInterfMapper.insertChbSubmitStatusList(chbSubmitStatusList,SubmitsType);
        }
        else {
            // 分批处理
            List<ChbSubmitStatus> newDataList = new ArrayList<ChbSubmitStatus>();
            newDataList.addAll(chbSubmitStatusList);

            int part = dataCount / maxDataCountOneTime; // 分批数
            for (int i = 0; i < part; i++ ) {
                cnBondInterfMapper.insertChbSubmitStatusList(
                    newDataList.subList(0, maxDataCountOneTime),SubmitsType);
                newDataList.subList(0, maxDataCountOneTime).clear();
            }
            // 最后剩下的数据
            if (CollectionUtils.isNotEmpty(newDataList)) {
                cnBondInterfMapper.insertChbSubmitStatusList(newDataList,SubmitsType);
            }
        }
    }
    /**
     * 分批登录中债报送状态数据
     *
     * @param chbInfoReportList 中债报送状态数据
     * @see
     * @since 1.0
     */
    public void insertChbEquDetSubmitStatusListByPart(List<ChbSubmitStatus> chbSubmitStatusList) {
        if (StringUtil.isEmptyList(chbSubmitStatusList)) {
            return;
        }

        int maxDataCountOneTime = 100;

        // 报送数据
        int dataCount = chbSubmitStatusList.size();
        if (dataCount <= maxDataCountOneTime) {
            cnBondInterfMapper.insertChbEquDetSubmitStatusList(chbSubmitStatusList);
        }
        else {
            // 分批处理
            List<ChbSubmitStatus> newDataList = new ArrayList<ChbSubmitStatus>();
            newDataList.addAll(chbSubmitStatusList);

            int part = dataCount / maxDataCountOneTime; // 分批数
            for (int i = 0; i < part; i++ ) {
                cnBondInterfMapper.insertChbEquDetSubmitStatusList(
                    newDataList.subList(0, maxDataCountOneTime));
                newDataList.subList(0, maxDataCountOneTime).clear();
            }
            // 最后剩下的数据
            if (CollectionUtils.isNotEmpty(newDataList)) {
                cnBondInterfMapper.insertChbEquDetSubmitStatusList(newDataList);
            }
        }
    }
}
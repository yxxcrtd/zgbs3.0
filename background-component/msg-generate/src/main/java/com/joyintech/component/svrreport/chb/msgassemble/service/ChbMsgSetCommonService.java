/*
 * 文件名：ChbMsgSetBaseService.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年09月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.component.svrreport.chb.msgassemble.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.joyintech.background.component.common.pack.common.util.DateUtil;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.component.svrreport.chb.contant.ChbConstant;
import com.joyintech.component.svrreport.chb.contant.DictCodeList;
import com.joyintech.component.svrreport.chb.dto.*;
import com.joyintech.component.svrreport.chb.util.ChbXmlUtil;
import com.joyintech.component.svrreport.chb.util.InterfLogUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 报文拼接共通服务
 * @author 王波
 * @date 2020年09月25日
 * @see ChbMsgSetCommonService
 * @since 1.0
 */
@Service
public class ChbMsgSetCommonService {

    private static final Logger logger = LoggerFactory.getLogger(ChbMsgSetCommonService.class);

    @Value("${application.chb.filepath:}")
    private String chbfilepath;

    /**
     * AutoId
     */
    private int autoId = 0;
    


    /**
     * 需要拼接的报文
     * @param chbBondSubmitPara 标准接口参数
     */
    public ChbMsgBuildInfoDto buildMsg(ChbBondSubmitPara chbBondSubmitPara) throws Exception {
    	ChbMsgBuildInfoDto res = new ChbMsgBuildInfoDto();
        logger.debug("开始拼接报文...");
        autoId = 0;
        // 报送类型
        String dealType = chbBondSubmitPara.getDealType();
        // 报送日期yyyymmdd
        String date = DateUtil.format(chbBondSubmitPara.getSysDate(), DateUtil.DEFAULT_OTHER_FORMAT);
        // 创建日志
        InterfLog log = new InterfLog();
        log.setInterfId(DictCodeList.接口系统ID_中债直连.getValue());
        log.setBizDate(date);
        res.setInterfLog(log);
        res.getLogList().addAll(
                InterfLogUtil.createLogList(log, DictCodeList.接口类型_开始.getValue(), ChbConstant.SUCCESS, ChbConstant.UNWARNING, null, false));
        BusinessForXMLEntity entity = new BusinessForXMLEntity();

        // 设值共通参数
        setCommmonInfo(entity, chbBondSubmitPara.getDealType());

        // 标签contextList
        List<XbrliContextEntity> contextList = new ArrayList<>();
        entity.setXbrliContextEntity(contextList);

        Date sysDate = chbBondSubmitPara.getSysDate();
        String registBankCode = chbBondSubmitPara.getRegistBankCode();
        // context1
        XbrliContextEntity context1 = getContextEntity1(sysDate, registBankCode);
        // context2
        XbrliContextEntity context2 = getContextEntity2(sysDate, registBankCode);
        // contextRefIds
        String[] contextRefIds = new String[] {context1.getXbrliContextEntityId()};
        List<BaseInfoEntity> bases = new ArrayList<BaseInfoEntity>();

        String fileType = setMsgInfos(chbBondSubmitPara, dealType, entity
                , contextList, context1, context2, contextRefIds, bases);
        if(contextList.size() == 2){//修复contextRefIds值无法带回的问题@modify zhaoyongsheng 20201028
        	contextRefIds = new String[] {context1.getXbrliContextEntityId(),
        			context2.getXbrliContextEntityId()};
        }
        // 设置基本信息 并返回使用单位
        if (!(DictCodeList.报表名称_投资者身份信息登记.getValue().equals(dealType)
                || DictCodeList.报表名称_投资者持有信息登记.getValue().equals(dealType)
                || DictCodeList.报表名称_投资者明细信息登记.getValue().equals(dealType))) {
            Map<String, XbrliUnitEntity> returnMaps = setEntity(bases, chbBondSubmitPara.getInfos(), contextRefIds, log);
            /*清理returnMaps空元素 @modify zhaoyongsheng 20201028 start*/
            returnMaps.entrySet().removeIf(entry -> entry == null);
            returnMaps.entrySet().removeIf(entry -> entry.getKey() == null);
            returnMaps.entrySet().removeIf(entry -> entry.getValue() == null);
            /*清理returnMaps空元素 @modify zhaoyongsheng 20201028 end*/
            // 标签xbrli:unit List
            List<XbrliUnitEntity> xbrliUnitEntities = new ArrayList<XbrliUnitEntity>(
                    returnMaps.values());
            // 设值标签xbrli:unit
            entity.setXbrliUnitEntities(xbrliUnitEntities);
        }

        // 文件List
        List<File> files = new ArrayList<>();
        // 相对路径list
        List<String> resourceFilePath = new ArrayList<>();
        // 登记流水号 list
        getFile(bases, files, resourceFilePath,  chbBondSubmitPara.getRegistSeq());
        // 文件名
        String fileName = registBankCode + ChbConstant.SPLIT + fileType + ChbConstant.SPLIT
                + date + ChbConstant.SPLIT;

        // 获取中债文件路径
        String cbFilepath = getChbFilePath();
        if (!FileUtil.exist(chbfilepath)) {
            FileUtil.mkdir(chbfilepath);
        }
        int maxCount = getMaxFileNumber(cbFilepath, fileName);
        // 压缩包名称
        String fileZip = fileName + NumberUtil.decimalFormat("00", maxCount + 1) + ChbConstant.FILE_ZIP;

        fileName = fileName + NumberUtil.decimalFormat("00", maxCount + 1) + ChbConstant.FILE_XML;
        
        ChbXmlUtil.generationXML(entity, BusinessForXMLEntity.class, cbFilepath, fileName);


        // 压缩文件
        compressMsg(chbBondSubmitPara.getAttachPathList(), cbFilepath + fileName, cbFilepath + fileZip);


        res.setFilePath(cbFilepath + fileZip);
        logger.debug("报文拼装处理结束：" + DateUtil.getCurrentDateTime());

        return res;
    }

    /**
     * 附件文件
     * @param attachFiles 附件文件
     * @param srcPath 中债XML文件
     * @param zipPath zip文件
     */
    private static void compressMsg(List<String> attachFiles, String srcPath, String zipPath) {

        if (CollectionUtil.isNotEmpty(attachFiles)) {
            File zipFile = FileUtil.file(zipPath);
            // 将附件文件先解压
            List<File> zipFileList = new ArrayList<>();
            for (String attachFile : attachFiles) {
                File unzipFile = new File(attachFile);
                for(File file :unzipFile.listFiles()){
                	zipFileList.add(file);
                }
                
            }

            zipFileList.add(FileUtil.file(srcPath));
            File [] zipFiles = zipFileList.toArray(new File[zipFileList.size()]);
            ZipUtil.zip(zipFile, CharsetUtil.defaultCharset(), true, null, zipFiles);
        }
        else {
            ZipUtil.zip(srcPath, zipPath);
        }

    }

//    public static void main(String[] args) {
//        String srcPath = "E:\\2.txt";
//
//        String zipPath = "E:\\1.zip";
//
//        List<String> zipFiles = new ArrayList<>();
//        zipFiles.add("E:\\LZCSGMCP23.zip");
//
//        compressMsg(zipFiles, srcPath, zipPath);
//    }

    /**
     * 获取中债文件路径
     * @return
     */
    private String getChbFilePath() {
        if (StrUtil.isEmpty(chbfilepath)) {
            chbfilepath = System.getProperty("user.home") + File.separator + "chb" +  File.separator + "send" + File.separator;
        }
        return chbfilepath;
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
        List<String> fileList = FileUtil.listFileNames(path);
        // 最大文件number
        int maxFileNumber = 0;
        if (CollectionUtils.isEmpty(fileList)) {
            return 0;
        }
        for (String name : fileList) {
            // 判断文件是否包含code字符 并且有.dat
            if (StringUtils.isEmpty(name) || !name.contains(fileName)
                    || !name.contains(ChbConstant.FILE_XML)) {
                continue;
            }
            // 当前文件number
            String fileNumber = name.replace(fileName, "").replace(ChbConstant.FILE_XML, "");
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
/*          registSeqnos数据已被传入，这里没必要再添加一次 @modify zhaoyongsheng 20201020
 * 	        if (base.getRegistrationSerialNumber() != null) {
                // 登记流水号
                registSeqnos.add(getVaule(base.getRegistrationSerialNumber()));
            }
            else if (base.getUnderlyingAssetTuple() != null) {
                for (CommonNodeListEntity entity : base.getUnderlyingAssetTuple()) {
                    registSeqnos.add(getVaule(entity.getRegistrationSerialNumber()));
                }
            }*/
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
            String[] str = vaule.split(ChbConstant.SPLIT_STRING1);
            if (str.length < 3) {
                break;
            }
        }
    }
    
    /**
     * @param entity entity
     * @return 值
     */
/*    private String getVaule(CommonNodeEntity entity) {
        if (entity == null) {
            return null;
        }
        return entity.getValue();
    }
*/
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
                                                   List<Map<String, CnBondInterfItemDto>> infos,
                                                   String[] contextRefIds, InterfLog log)  throws Exception {
        // 全部单位标签
        Map<String, XbrliUnitEntity> mapUtils = getUnits();
        // 使用的单位标签
        Map<String, XbrliUnitEntity> returnMaps = new LinkedHashMap<String, XbrliUnitEntity>();
        // 循环处理
        for (Map<String, CnBondInterfItemDto> map : infos) {
            autoId++;
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
    private void setBaseInfo(Map<String, CnBondInterfItemDto> items, BaseInfoEntity entity,
                             String[] contextRefIds, Map<String, XbrliUnitEntity> mapUtils,
                             Map<String, XbrliUnitEntity> returnMap, InterfLog log) throws Exception {
        for(Map.Entry<String, CnBondInterfItemDto> dto :items.entrySet()){
            setBaseInfo(dto.getKey(), dto.getValue(), entity, contextRefIds,mapUtils,returnMap);
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
    private void setBaseInfo(String key, CnBondInterfItemDto item, Feild entity,
                             String[] contextRefIds, Map<String, XbrliUnitEntity> mapUtils,
                             Map<String, XbrliUnitEntity> returnMap)
            throws Exception {
        // 项目值
        String value = item.getValue();
        // check key and value
        if (StringUtils.isEmpty(key)) {
            return;
        }
        // 键值是否是循环结构
        if (isEndWithTuple(key) && StringUtils.isNotEmpty(value)) {
            autoId ++;
            // 基本信息标签list
            List<CommonNodeListEntity> nodes = new ArrayList<CommonNodeListEntity>();
            // 子标签
            String nodeKey = key.substring(0, key.length() - 5);
            // 以“，”拆分值
            for (String val : value.split(ChbConstant.COMMA)) {
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
                //id
                subNode.setId("f" + ( ++ autoId));
                // 判断标签是否当日生效
                if (isNodeTodayAffect(nodeKey) && contextRefIds.length == 2) {
                    subNode.setContextRef(contextRefIds[1]);
                }
                // 反射设值
                Method m = node.getClass().getMethod(ChbConstant.SET + nodeKey,
                        CommonNodeEntity.class);
                m.invoke(node, subNode);
                // 添加标签
                nodes.add(node);
            }
            // 反射设值
            Method m = entity.getClass().getMethod(ChbConstant.SET + key, List.class);
            m.invoke(entity, nodes);
        }
        else if (isEndWithTuple(key) && item.getMaps() != null) {
            // 基本信息标签list
            List<CommonNodeListEntity> nodes = new ArrayList<CommonNodeListEntity>();
            for (Map<String, CnBondInterfItemDto> map : item.getMaps()) {
                autoId ++;
                // 基本信息标签
                CommonNodeListEntity node = new CommonNodeListEntity();
                // 循环 map
                for (Map.Entry<String, CnBondInterfItemDto> subitem : map.entrySet()) {
                    setBaseInfo(subitem.getKey(), subitem.getValue(), node, contextRefIds,mapUtils,returnMap);
                }
                nodes.add(node);
            }
            // 反射设值
            Method m = entity.getClass().getMethod(ChbConstant.SET + key, List.class);
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

            node.setId("f" + ( ++ autoId));

            // 判断标签是否当日生效
            if (isNodeTodayAffect(key) && contextRefIds.length == 2) {
                node.setContextRef(contextRefIds[1]);
            }
            // 反射设值
            Method m = entity.getClass().getMethod(ChbConstant.SET + key,
                    CommonNodeEntity.class);
            m.invoke(entity, node);
        }
        // 添加使用的标签
        returnMap.put(item.getUnitRef(),mapUtils.get(item.getUnitRef()));
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
                "MarketValueOfUnderlyingAssetsLiabilitiesCNY",
                "AggregateQuantityOfAssetManagementProductOrOutsourcingAsset",
                "AggregateIssuedAmountOfAssetManagementProductOrOutsourcingAssetCNY",
                "NonInvestedAmountOfAssetManagementProductOrOutsourcingAssetCNY"};
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
        return ChbConstant.TUPLE.equals(lst);
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
        cny.setId(ChbConstant.币种全称_中债_人民币);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        cny.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_人民币);
        map.put(ChbConstant.币种全称_中债_人民币, cny);

        // 港元(HKD)
        XbrliUnitEntity hkd = new XbrliUnitEntity();
        hkd.setId(ChbConstant.币种全称_中债_港元);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        hkd.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_港元);
        map.put(ChbConstant.币种全称_中债_港元, hkd);

        // 美元(USD)
        XbrliUnitEntity usd = new XbrliUnitEntity();
        usd.setId(ChbConstant.币种全称_中债_美元);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        usd.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_美元);
        map.put(ChbConstant.币种全称_中债_美元, usd);

        // 欧元(EUR)
        XbrliUnitEntity eur = new XbrliUnitEntity();
        eur.setId(ChbConstant.币种全称_中债_欧元);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        eur.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_欧元);
        map.put(ChbConstant.币种全称_中债_欧元, eur);

        // 日元(JPY)
        XbrliUnitEntity jpy = new XbrliUnitEntity();
        jpy.setId(ChbConstant.币种全称_中债_日元);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        jpy.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_日元);
        map.put(ChbConstant.币种全称_中债_日元, jpy);

        // 澳门元(MOP)
        XbrliUnitEntity mop = new XbrliUnitEntity();
        mop.setId(ChbConstant.币种全称_中债_澳门元);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        mop.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_澳门元);
        map.put(ChbConstant.币种全称_中债_澳门元, mop);

        // 瑞士法郎(CHF)
        XbrliUnitEntity chf = new XbrliUnitEntity();
        chf.setId(ChbConstant.币种全称_中债_瑞士法郎);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        chf.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_瑞士法郎);
        map.put(ChbConstant.币种全称_中债_瑞士法郎, chf);

        // 英镑(GBP)
        XbrliUnitEntity gbp = new XbrliUnitEntity();
        gbp.setId(ChbConstant.币种全称_中债_英镑);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        gbp.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_英镑);
        map.put(ChbConstant.币种全称_中债_英镑, gbp);

        // 俄罗斯卢布(RUB)
        XbrliUnitEntity rub = new XbrliUnitEntity();
        rub.setId(ChbConstant.币种全称_中债_俄罗斯卢布);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        rub.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_俄罗斯卢布);
        map.put(ChbConstant.币种全称_中债_俄罗斯卢布, rub);

        // 加拿大元(CAD)
        XbrliUnitEntity cad = new XbrliUnitEntity();
        cad.setId(ChbConstant.币种全称_中债_加拿大元);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        cad.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_加拿大元);
        map.put(ChbConstant.币种全称_中债_加拿大元, cad);

        // 丹麦克朗(DKK)
        XbrliUnitEntity dkk = new XbrliUnitEntity();
        dkk.setId(ChbConstant.币种全称_中债_丹麦克朗);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        dkk.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_丹麦克朗);
        map.put(ChbConstant.币种全称_中债_丹麦克朗, dkk);

        // 澳大利亚元(AUD)
        XbrliUnitEntity aud = new XbrliUnitEntity();
        aud.setId(ChbConstant.币种全称_中债_澳大利亚元);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        aud.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_澳大利亚元);
        map.put(ChbConstant.币种全称_中债_澳大利亚元, aud);

        // 瑞典克朗(SEK)
        XbrliUnitEntity sek = new XbrliUnitEntity();
        sek.setId(ChbConstant.币种全称_中债_瑞典克朗);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        sek.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_瑞典克朗);
        map.put(ChbConstant.币种全称_中债_瑞典克朗, sek);

        // 新加坡元（SGD）
        XbrliUnitEntity sgd = new XbrliUnitEntity();
        sgd.setId(ChbConstant.币种全称_中债_新加坡元);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        sgd.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_新加坡元);
        map.put(ChbConstant.币种全称_中债_新加坡元, sgd);

        // 新台币（TWD）
        XbrliUnitEntity twd = new XbrliUnitEntity();
        twd.setId(ChbConstant.币种全称_中债_新台币);//修改XbrliUnitEntity对象属性映射 @modify zhaoyongsheng 20201028
        twd.setXbrliMeasure(ChbConstant.ISO_4217 + ChbConstant.COLON + ChbConstant.币种全称_中债_新台币);
        map.put(ChbConstant.币种全称_中债_新台币, twd);

        // Day 天
        XbrliUnitEntity day = new XbrliUnitEntity();
        day.setId(ChbConstant.DAY);
        day.setXbrliMeasure(ChbConstant.WEMAX_UNIT_DAY);
        map.put(ChbConstant.DAY, day);
        // Month 月
        XbrliUnitEntity month = new XbrliUnitEntity();
        month.setId(ChbConstant.MONTH);
        month.setXbrliMeasure(ChbConstant.WEMAX_UNIT_MONTH);
        map.put(ChbConstant.MONTH, month);
        // Piece 个
        XbrliUnitEntity piece = new XbrliUnitEntity();
        piece.setId(ChbConstant.PIECE);
        piece.setXbrliMeasure(ChbConstant.WEMAX_UNIT_PIECE);
        map.put(ChbConstant.PIECE, piece);
        // PURE 份
        XbrliUnitEntity pure = new XbrliUnitEntity();
        pure.setId(ChbConstant.PURE);
        pure.setXbrliMeasure(ChbConstant.XBRLI_PURE);
        map.put(ChbConstant.PURE, pure);
        // MonthPerCount(一次几个月)
        XbrliUnitEntity monthPerCount = new XbrliUnitEntity();
        monthPerCount.setId(ChbConstant.MONTHPERCOUNT);
        XbrliDivide xbrliDivide = new XbrliDivide();
        XbrliuUnitDenominator xbrliUnitNumerator = new XbrliuUnitDenominator();
        xbrliUnitNumerator.setXbrliMeasure(ChbConstant.WEMAX_UNIT_MONTH);
        XbrliuUnitDenominator xbrliuUnitDenominator = new XbrliuUnitDenominator();
        xbrliuUnitDenominator.setXbrliMeasure(ChbConstant.WEMAX_UNIT_COUNT);
        xbrliDivide.setXbrliUnitNumerator(xbrliUnitNumerator);
        xbrliDivide.setXbrliuUnitDenominator(xbrliuUnitDenominator);
        monthPerCount.setXbrliDivide(xbrliDivide);
        map.put(ChbConstant.MONTHPERCOUNT, monthPerCount);
        
        //单位：人
        XbrliUnitEntity person = new XbrliUnitEntity();
        person.setId(ChbConstant.PERSON);
        person.setXbrliMeasure(ChbConstant.WEMAX_UNIT_PERSON);
        map.put(ChbConstant.PERSON, person);
        
        return map;
    }


    /**
     * 分别设置报文的信息
     * @param chbBondSubmitPara
     * @param dealType
     * @param entity
     * @param contextList
     * @param context1
     * @param context2
     * @param contextRefIds
     * @param bases
     * @return
     */
    private String setMsgInfos(ChbBondSubmitPara chbBondSubmitPara,
                               String dealType,
                               BusinessForXMLEntity entity,
                               List<XbrliContextEntity> contextList,
                               XbrliContextEntity context1,
                               XbrliContextEntity context2,
                               String[] contextRefIds,
                               List<BaseInfoEntity> bases) {
        String fileType = null;
        if (DictCodeList.报表名称_产品申报登记.getValue().equals(dealType)) {
            // 设置基本信息@modify zhaoyongsheng 修复主标签显示  20201028
            entity.setFilingRegistrationElementsOfPublicOrPrivateProductTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                    context2.getXbrliContextEntityId()};
            fileType = ChbConstant.FILE_113_TYPE;
        }
        else if (DictCodeList.报表名称_机构和私人银行产品发行登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setIssueRegistrationForAgencyAndPrivateBankTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                    context2.getXbrliContextEntityId()};
            fileType = ChbConstant.FILE_102_TYPE;
        }
        else if (DictCodeList.报表名称_产品发行登记.getValue().equals(dealType)) {
            // 设置基本信息@modify zhaoyongsheng 修复主标签显示  20201028
            entity.setIssueRegistrationForPublicOrPrivateProductTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = ChbConstant.FILE_114_TYPE;
        }
        else if (DictCodeList.报表名称_募集总量登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setInitialSubscriptionRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = ChbConstant.FILE_104_TYPE;
        }
        else if (DictCodeList.报表名称_产品存续期登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setSubsequentSubscriptionRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                    context2.getXbrliContextEntityId()};
            fileType = ChbConstant.FILE_105_TYPE;
        }
        else if (DictCodeList.报表名称_资产要素信息登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setAssetsAndLiabilitiesRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = ChbConstant.FILE_106_TYPE;
        }
        else if (DictCodeList.报表名称_资产交易信息登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setTransactionRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                    context2.getXbrliContextEntityId()};
            fileType = ChbConstant.FILE_108_TYPE;
        }
        else if (DictCodeList.报表名称_资产估值信息登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setValuationRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                    context2.getXbrliContextEntityId()};
            fileType = ChbConstant.FILE_109_TYPE;
        }
        else if (DictCodeList.报表名称_资管计划底层持仓信息登记.getValue().equals(dealType)) {
            // 设置基本信息 @modify zhaoyongsheng 修复主标签显示  20201028
            entity.setUnderlyingAssetsLiabilitiesRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            // 添加context 标签
            contextList.add(context2);
            // contextRefIds
            contextRefIds = new String[] {context1.getXbrliContextEntityId(),
                    context2.getXbrliContextEntityId()};
            fileType = ChbConstant.FILE_110_TYPE;
        }
        else if (DictCodeList.报表名称_产品终止登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setTerminationRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = ChbConstant.FILE_111_TYPE;
        }
        else if (DictCodeList.报表名称_从业人员登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setPractitionerRegistrationTuple(bases);
            // 添加context 标签
            contextList.add(context1);
            fileType = ChbConstant.FILE_112_TYPE;
        }
        else if (DictCodeList.报表名称_分类总量信息登记.getValue().equals(dealType)) {
            // 设置基本信息
            entity.setInvestorsSummationOfDiffenrentTypesInformationRegistrationTuple(bases);
            contextList.add(context1);
            fileType = ChbConstant.FILE_203_TYPE;
        }
        else if (DictCodeList.报表名称_投资者身份信息登记.getValue().equals(dealType)) {
            // 基本信息标签
            CommonNodeEntity node = new CommonNodeEntity();
            // 标签值
            node.setValue(
                    chbBondSubmitPara.getInfos().get(0).get("InvestorsInformationRegistrationCSVDataExplanatory").getValue());
            // contextRef
            node.setContextRef(contextRefIds[0]);
            entity.setInvestorsInformationRegistrationCSVDataExplanatory(node);
            contextList.add(context1);
            fileType = ChbConstant.FILE_201_TYPE;
        }
        else if (DictCodeList.报表名称_投资者持有信息登记.getValue().equals(dealType)) {
            // 基本信息标签
            CommonNodeEntity node = new CommonNodeEntity();
            // 标签值
            node.setValue(
                    chbBondSubmitPara.getInfos().get(0).get("InvestorsInvestedInformationRegistrationCsvDataExplanatory").getValue());
            // contextRef
            node.setContextRef(contextRefIds[0]);
            entity.setInvestorsInvestedInformationRegistrationCsvDataExplanatory(node);
            contextList.add(context1);
            fileType = ChbConstant.FILE_202_TYPE;
        }
        else if (DictCodeList.报表名称_投资者明细信息登记.getValue().equals(dealType)) {
            // 基本信息标签
            CommonNodeEntity node = new CommonNodeEntity();
            // 标签值
            node.setValue(chbBondSubmitPara.getInfos().get(0).get(
                    "InvestorsBusinessDetailsInformationRegistrationCsvDataExplanatory").getValue());
            // contextRef
            node.setContextRef(contextRefIds[0]);
            entity.setInvestorsBusinessDetailsInformationRegistrationCsvDataExplanatory(node);
            contextList.add(context1);
            fileType = ChbConstant.FILE_203_TYPE;
        }
        return fileType;
    }

    /**
     * 设值报文共通属性
     *
     * @param entity 报文信息
     * @see
     * @since 1.0
     */
    private void setCommmonInfo(BusinessForXMLEntity entity, String type) {
        if (!DictCodeList.报表名称_分类总量信息登记.getValue().equals(type)) {
            // 标题1
            entity.setXmlnsXsi(ChbConstant.TITLE_PROPERTY1);
            // 标题2
            entity.setXsiSchemaLocation(ChbConstant.TITLE_PROPERTY2);
        }

        // 标题3
        entity.setXmlnsChinawealthEntryPoint(ChbConstant.TITLE_PROPERTY3);
        // 标题4
        entity.setXmlnsLink(ChbConstant.TITLE_PROPERTY4);
        // 标题5
        entity.setXmlnsCas(ChbConstant.TITLE_PROPERTY5);
        // 标题6
        entity.setXmlnsNum(ChbConstant.TITLE_PROPERTY6);
        // 标题7
        entity.setXmlnsNonnum(ChbConstant.TITLE_PROPERTY7);
        // 标题8
        entity.setXmlnsIfrsFull(ChbConstant.TITLE_PROPERTY8);
        // 标题9
        entity.setXmlnsXbrldt(ChbConstant.TITLE_PROPERTY9);
        if (DictCodeList.报表名称_投资者身份信息登记.getValue().equals(type)
             || DictCodeList.报表名称_投资者持有信息登记.getValue().equals(type)
             || DictCodeList.报表名称_投资者明细信息登记.getValue().equals(type)) {
	        // 标题10
	        entity.setRolChinawealth(ChbConstant.TITLE_PROPERTY10);
        }
        // 标题11
        entity.setXmlnsWemax(ChbConstant.TITLE_PROPERTY11);

        // 中债一二期需添加 start
        entity.setXmlnsWemaxRet(ChbConstant.TITLE_WEMAZ_RET);
        entity.setXmlnsWemaxUnit(ChbConstant.TITLE_WEMAZ_UNIT);
        // 中债一二期需添加 end
        if (DictCodeList.报表名称_分类总量信息登记.getValue().equals(type)) {
            entity.setXmlnsXsi1(ChbConstant.TITLE_PROPERTY1);
        }
        else {
            // 标题12
            entity.setXmlnsXbrldi(ChbConstant.TITLE_PROPERTY12);
        }
        // 标题13
        entity.setXmlnsXbrli(ChbConstant.TITLE_PROPERTY13);
        // 标题14
        entity.setXmlnsWei(ChbConstant.TITLE_PROPERTY14);
        // 标题15
        entity.setXmlnsIso4217(ChbConstant.TITLE_PROPERTY15);
        // 标题16
        entity.setXmlnsXlink(ChbConstant.TITLE_PROPERTY16);


        LinkSchemaRefEntity linkSchemaRefEntity = new LinkSchemaRefEntity();
        // 标题17
        linkSchemaRefEntity.setXlinkHref(ChbConstant.TITLE_PROPERTY17);
        // 报文 LINK_TYPE
        linkSchemaRefEntity.setXlinkType(ChbConstant.LINK_TYPE);
        entity.setLinkSchemaRef(linkSchemaRefEntity);
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
                ChbConstant.CD + sysDateBeforeCD + ChbConstant.SPLIT + sysDateCD);
        // 标签xbrli:entity
        XbrliEntity xbrliEntity = new XbrliEntity();
        // 标签xbrli:identifier
        XbrliIdentifierEntity xbrliIdentifierEntity = new XbrliIdentifierEntity();
        // 标签xbrli:identifier scheme属性
        xbrliIdentifierEntity.setScheme(ChbConstant.SCHNAME);
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
        entity.setXbrliContextEntityId(ChbConstant.CI + sysDateCI);
        // 标签xbrli:entity
        XbrliEntity xbrliEntity = new XbrliEntity();
        // 标签xbrli:identifier
        XbrliIdentifierEntity xbrliIdentifierEntity = new XbrliIdentifierEntity();
        // 标签xbrli:identifier scheme属性
        xbrliIdentifierEntity.setScheme(ChbConstant.SCHNAME);
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


}

/*
 * 文件名：EtrInvestAllInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrinvestallinfo.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.exportpdf.service.CommonExportPdfService;
import com.joyintech.tams.invest.entrust.etrbrowdepoinfo.dto.EtrBrowDepoInfoDto;
import com.joyintech.tams.invest.entrust.etrinvestallinfo.dto.EtrAllInfoExportPdfDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.entrust.etrinvestallinfo.dto.EtrInvestAllInfoDto;
import com.joyintech.tams.invest.entrust.etrrepo.dto.EtrRepoDto;

/**
 * 所有投资委托画面段service 〈功能详细描述〉
 *
 * @author xiexinting
 * @version 1.0 2018年9月7日
 * @see EtrInvestAllInfoService
 * @since 1.0
 */
@Service
public class EtrInvestAllInfoService extends CommonExportService<EtrInvestAllInfoDto> {

    /**
     * add by zengchengfa 20210803导出pdf共通
     */
    @Autowired
    private CommonExportPdfService commonExportPdfService;

    /**
     * 获取组合代码的联想文本框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */

    public List<SuggestDto> getentrustId(String param) {
        return restTemplate.getForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestAllInfo/getentrustId?entrustId="+param,
                SuggestDto.class);
    }

    /**
     * 获取资产编号的联想文本框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getfinprodId(String param) {
        return restTemplate.getForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestAllInfo/getfinprodId?finprodId="+param,
                SuggestDto.class);

    }

    /**
     * 所有投资委托信息查询 <功能详细描述>
     *
     * @param param
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<EtrInvestAllInfoDto> selectForPage(Map<String, Object> param){
        if(StringUtil.isNotEmpty((String)param.get("entrustDate"))){
            List<String> dateList =StringUtil.splitToList(" ", (String)param.get("entrustDate"));
            param.put("startDate",dateList.get(0));
            param.put("endDate",dateList.get(2));
        }
        if(StringUtil.isNotEmpty((String)param.get("finprodType2"))){
            List<String> dateList =StringUtil.splitToList(",", (String)param.get("finprodType2"));
            param.put("finprodType2List",dateList);
        }

        if(StringUtil.isNotEmpty(param.get("entrustStatus"))){
            List<String> entrustStatusList = StringUtil.splitToList(",",(String)param.get("entrustStatus"));
            param.put("entrustStatusList", entrustStatusList);
        }
        //交易方向为利率互换 转换
        if(StringUtil.isNotEmpty((String)param.get("ps"))){
            if( StringUtil.equals((String)param.get("ps") , DictCode.互换方式.getValue()+DictCodeListExt2.互换方式_收取固定.getValue()) ){
                param.put("ps", DictCodeListExt2.互换方式_收取固定.getValue());
                param.put("IRSps", DictCodeListExt2.互换方式_收取固定.getValue());
            }
            if( StringUtil.equals((String)param.get("ps") , DictCode.互换方式.getValue()+DictCodeListExt2.互换方式_收取浮动.getValue()) ){
                param.put("ps", DictCodeListExt2.互换方式_收取浮动.getValue());
                param.put("IRSps", DictCodeListExt2.互换方式_收取浮动.getValue());
            }
        }
        param.put("IRSType_01", DictCodeListExt2.互换方式_收取固定.getValue());
        param.put("IRSType_02", DictCodeListExt2.互换方式_收取浮动.getValue());
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        Page<EtrInvestAllInfoDto> page = restTemplate.postForPage(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestAllInfo/findPage", param,
                EtrInvestAllInfoDto.class);
        List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
                param, SysUser.class);
        for(EtrInvestAllInfoDto dto:page.getRoot()){
            if(StringUtil.isNotEmpty(dto.getNextUser())){
                String sysUsers = userList.stream().filter(x-> Arrays.asList(dto.getNextUser().split(",")).contains(x.getUserName()))
                        .map(SysUser::getRealName)
                        .collect(Collectors.joining(",","",""));
                dto.setNextUser(sysUsers);
            }
            if(StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_竞价交易.getValue())
            ||StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_大宗交易.getValue())
                    ||StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_协议现券.getValue())
                    ||StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_债转股.getValue())
                    ||StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_质押券出入库.getValue())
                    ||StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_配债.getValue())
                    ||StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_可转债申购.getValue())
                    ||StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_新股申购.getValue())
                    ||StringUtils.equals(dto.getFinprodType2(), DictCodeListExt2.平安资产类型_信用保护凭证.getValue())
                    ||StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_通用式质押回购.getValue())
                    ||(StringUtils.equals(dto.getTradeMarket(),"01") &&  StringUtils.equals(dto.getFinprodType2(),DictCodeListExt2.平安资产类型_债券.getValue()) &&
                    StringUtils.equals(dto.getPs(),DictCodeList.交易方向_行权_回售.getValue())
                    )
            ){
                dto.setSettleSpreed(null);
            }


        }
        return page;
    }

    public EtrInvestAllInfoDto getById(String entrustId){
        if(StringUtils.isEmpty(entrustId)){
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("entrustId", entrustId);
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestAllInfo/getById", map,
                EtrInvestAllInfoDto.class);
    }

    /**
     * 导出
     *
     * @param exportParam 导出信息
     * @see
     * @since 1.0
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/invest/entrust/etrinvestallinfo/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<EtrInvestAllInfoDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    /**
     * PB文件导出前数据检查 add by caoxue 2019-12-31
     *
     * @return 返回检查结果
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    public EtrRepoDto checkPBRepoType(Map<String, Object> params) {
        if(StringUtil.isNotEmpty((String)params.get("entrustId"))){
            EtrRepoDto etrRepoDto = restTemplate.getForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrRepo/selectByKey?entrustId=" + params.get("entrustId"),
                    EtrRepoDto.class);

            return etrRepoDto;
        }
        return null;
    }
    /**
     * 查询PB导出信息 add by caoxue 2019-12-31
     *
     * @param param
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public List<EtrInvestAllInfoDto> selectExportPB(Map<String, Object> param) {
        return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestAllInfo/selectExportPB", param,
                EtrInvestAllInfoDto.class);
    }

    /**
     * 导出pdf数据 add by zengchengfa 20210730
     * @param param
     * @return
     */
    public List<EtrAllInfoExportPdfDto> selectExportPdf(Map param){
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestAllInfo/selectExportPdf", param,
                EtrAllInfoExportPdfDto.class);
    }

    /**
     * 导出pdf add by zengchengfa 20210730
     * @param pageId 画面id
     * @param param 参数
     * @return
     */
    public ResponseEntity<byte[]> exportPdf(String pageId, Map param){
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        String assetType = param.get("assetType")==null?"":param.get("assetType").toString();
        List<EtrAllInfoExportPdfDto> datas = selectExportPdf(param);
        DecimalFormat df2 = new DecimalFormat("#0.00##");
        DecimalFormat df3 = new DecimalFormat("#0.000##");
        DecimalFormat df4 = new DecimalFormat("#0.0000##");
        datas.forEach(exportPdfDto -> {
            if(StringUtil.isNotEmpty(exportPdfDto.getEntrustAmt())){
                exportPdfDto.setEntrustAmt(new BigDecimal(df2.format(exportPdfDto.getEntrustAmt())));
            }
            // 交易面额
            if(StringUtil.isNotEmpty(exportPdfDto.getEntrustNum())){
                exportPdfDto.setEntrustNum(new BigDecimal(df2.format(exportPdfDto.getEntrustNum())));
            }
            if(DictCodeList.平安资产类型_质押式回购.getValue().equalsIgnoreCase(assetType)){
                // 回购利率
                if(StringUtil.isNotEmpty(exportPdfDto.getPrice())){
                    exportPdfDto.setPrice(new BigDecimal(df2.format(exportPdfDto.getPrice()
                            .setScale(2, BigDecimal.ROUND_HALF_UP))));
                }
            }else{
                // 交易净价
                if(StringUtil.isNotEmpty(exportPdfDto.getPrice())){
                    exportPdfDto.setPrice(new BigDecimal(df3.format(exportPdfDto.getPrice()
                            .setScale(3, BigDecimal.ROUND_HALF_UP))));
                }
            }
            if(StringUtil.isNotEmpty(exportPdfDto.getConRatio())){
                // 多个质押券,折算比例中间有,分隔
                if(exportPdfDto.getConRatio().contains(",")){
                    String[] conRatios = exportPdfDto.getConRatio().split(",");
                    StringJoiner joiner = new StringJoiner(",");
                    for(int index=0; index<conRatios.length; index++){
                        joiner.add(df2.format(new BigDecimal(conRatios[index]).setScale(2, BigDecimal.ROUND_HALF_UP)));
                    }
                    exportPdfDto.setConRatio(joiner.toString());
                }else{
                    exportPdfDto.setConRatio(df2.format(new BigDecimal(exportPdfDto.getConRatio())
                            .setScale(2, BigDecimal.ROUND_HALF_UP)));
                }
            }
            // 估值净价
            if(StringUtil.isNotEmpty(exportPdfDto.getValuePrice())){
                exportPdfDto.setValuePrice(new BigDecimal(df4.format(exportPdfDto.getValuePrice()
                        .setScale(4, BigDecimal.ROUND_HALF_UP))));
            }
            // 价格偏离度
            if(StringUtil.isNotEmpty(exportPdfDto.getDeviation())){
                exportPdfDto.setDeviation(df2.format(new BigDecimal(exportPdfDto.getDeviation())
                        .setScale(2, BigDecimal.ROUND_HALF_UP)) + BussinessDictCodeList.单位_百分比.getCodeName());
            }
        });
        return commonExportPdfService.exportPdf(pageId,getConfigFile(pageId, assetType),datas);
    }

    /**
     * 获取配置文件绝对路径 add by zengchengfa 20210730
     *
     * @param pageId pageId
     * @param assetType 资产类型
     * @return
     */
    public String getConfigFile(String pageId, String assetType){
        String configFile ;
        String configFilePath = "";
        String basePath = "com/joyintech/tams/invest/entrust/etrinvestallinfo/conf/";
        String tempPath = CacheUtil.getSysParam("access_exportfilepath");
        String tempConfigFilePath="";
        if(DictCodeList.平安资产类型_债券.getValue().equalsIgnoreCase(assetType)){
            configFilePath = basePath + "export_conf_pdf_bond.xml";
            tempConfigFilePath = tempPath + "/" + pageId + '_' + "export_conf_pdf_bond.xml";
        }else if(DictCodeList.平安资产类型_质押式回购.getValue().equalsIgnoreCase(assetType)){
            configFilePath = basePath + "export_conf_pdf_rep.xml";
            tempConfigFilePath = tempPath + "/" + pageId + '_' + "export_conf_pdf_rep.xml";
        }else{
            configFilePath = basePath + "export_conf_pdf_other.xml";
            tempConfigFilePath = tempPath + "/" + pageId + '_' + "export_conf_pdf_other.xml";
        }
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(configFilePath);
        // 在临时目录下生成配置文件
        commonExportPdfService.createTempConfigFile(in, tempPath, tempConfigFilePath);
        return tempConfigFilePath;
    }
}

/*
 * 文件名：BeforeBatchCheck.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2019年4月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.svreport.chb.datacheck.beforesub.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.ChbPrdDeclareSub;
import com.joyintech.tams.common.base.entity.ChbPrdresistCode;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.common.svreport.datacheck.beforeinsert.dao.CommonCodeCheckMapper;
import com.joyintech.tams.common.svreport.datacheck.dto.ChbCheckRuleout;
import com.joyintech.tams.common.svreport.datacheck.dto.CheckRule;
import com.joyintech.tams.common.svreport.datacheck.service.DataCheckService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.dao.ChbDeadLineMapper;
import com.joyintech.tams.report.common.dto.ChbDeadLine001Dto;
import com.joyintech.tams.report.common.util.SysDataUtil;
import com.joyintech.tams.report.submitmanagement.checkreport.dto.CheckReport001Dto;
import com.joyintech.tams.svreport.chb.common.SvrCustomerNumCheckService;
import com.joyintech.tams.svreport.chb.datacheck.beforesub.dao.ChbBeforeSubErrorCheckMapper;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 中债一期、二期报送前检查，部分报表的强制性检查
 * 
 * @author chenxc
 * @version 1.0 2019年4月16日
 * @see BeforeSubErrorCheck
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BeforeSubErrorCheck {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ChbBeforeSubErrorCheckMapper chbBeforeSubErrorCheckMapper;
    
    @Autowired
    private DataCheckService dataCheckService;

    @Autowired
    private CommonCodeCheckMapper commonCodeCheckMapper;

    /**
     * 客户数检查共通方法
     */
    @Autowired
    private SvrCustomerNumCheckService svrCustomerNumCheckService;
	
	@Autowired
    private ChbDeadLineMapper chbDeadLineMapper;
   
    
    /**
     * 
     * 存续期登记检查
     * @param checkdate
     * @return
     * @see
     * @since 1.0
     */
    public List<CheckReport001Dto> checkProdDurationReg(Date checkdate,List<String> keys,Map<String, List<ChbCheckRuleout>> mapRuleout) {
        logger.info("产品存续期错误检查开始.");
       List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();
       
        // 读取规则
//        List<CheckRule> listRules = dataCheckService.queryCheckRules("CHB_PRD_DEAL_SUB");
        
        //读取数据，查询登记表数据
        List<Map<String,Object>> listData = chbBeforeSubErrorCheckMapper.selectProdDealSub(checkdate,keys);
        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,Object> singleMap:listData){
        	Map <String,String> smap=new HashMap<String,String>();
        	for(String singlekey:singleMap.keySet()){
        		smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
        	}
        	listDataStr.add(smap);
        }
        // 数据校验
//        List<Map<String, String>> listError = DataCheckService.checkDataList(listDataStr, listRules);
        List<Map<String, String>> listError = new ArrayList<>();
        for (Map<String,String> map : listDataStr){
            checkDurationReg(map);
            if (StringUtil.isNotEmpty(map.get("ERRMSG"))){
                listError.add(map);
            }
        }

        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("PROD_ID"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("REGIST_SEQNO"));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_产品存续期登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_产品存续期登记.getCodeName());
            chbReportCheck.setValidKey("产品代码:"+m.get("PROD_ID")+ (StringUtil.isNotEmpty(m.get("MDATE")) ? ";业务结束日:"+ DateUtil.format(DateUtil.parse(m.get("MDATE"))): ""));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(checkdate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }            
            
            //将报送表中数据设置成待维护
            // chbBeforeSubErrorCheckMapper.updateProdDealSub(m.get("REGIST_SEQNO"));
        }
        logger.info("产品存续期错误检查结束.");
        return resultList;
    }
    
    /**
     * 
     * 产品终止检查
     * @param checkdate
     * @return
     * @see
     * @since 1.0
     */
    public List<CheckReport001Dto> checkProdEndReg(Date checkdate,List<String> listProd,Map<String, List<ChbCheckRuleout>> mapRuleout) {
        logger.info("产品终止登记错误检查开始");
       List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();
       
        // 读取规则
//        List<CheckRule> listRules = dataCheckService.queryCheckRules("CHB_PRD_END_SUB");
        
        //读取数据，查询登记表数据
        List<Map<String,Object>> listData = chbBeforeSubErrorCheckMapper.selectProdEndSub(checkdate,listProd);
        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,Object> singleMap:listData){
        	Map <String,String> smap=new HashMap<String,String>();
        	for(String singlekey:singleMap.keySet()){
        		smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
        	}
        	listDataStr.add(smap);
        }
        // 数据校验
//        List<Map<String, String>> listError = DataCheckService.checkDataList(listDataStr, listRules);
        List<Map<String, String>> listError = new ArrayList<>();
        for (Map<String,String> map : listDataStr){
            checkBeforTerminationRegist(map);
            if (StringUtil.isNotEmpty(map.get("ERRMSG"))){
                listError.add(map);
            }
        }
        
        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("PROD_ID"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("REGIST_SEQNO"));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_产品终止登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_产品终止登记.getCodeName());
            chbReportCheck.setValidKey("产品代码:"+m.get("PROD_ID"));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(checkdate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }            
            
            //将报送表中数据设置成待维护
            // chbBeforeSubErrorCheckMapper.updateProdEndSub(m.get("REGIST_SEQNO"));
        }
        logger.info("产品终止登记错误检查结束");
        return resultList;
    }
    
    /**
     * 
     * 发行登记检查
     * @param checkdate
     * @return
     * @see
     * @since 1.0
     */
    public List<CheckReport001Dto> checkProdIssueReg(Date checkdate,List<String> listProd,Map<String, List<ChbCheckRuleout>> mapRuleout) {
        logger.info("产品发行登记错误检查开始.");
       List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();
       
        // 读取规则
//        List<CheckRule> listRules = dataCheckService.queryCheckRules("CHB_PRD_ISSUE_SUB");
        
        //读取数据，查询登记表数据
        List<Map<String,Object>> listData = chbBeforeSubErrorCheckMapper.selectProdIssueSub(checkdate,listProd);
        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,Object> singleMap:listData){
        	Map <String,String> smap=new HashMap<String,String>();
        	for(String singlekey:singleMap.keySet()){
        		smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
        	}
        	listDataStr.add(smap);
        }

        // 数据校验
//        List<Map<String, String>> listError = DataCheckService.checkDataList(listDataStr, listRules);

        List<Map<String, String>> listError = new ArrayList<>();
        for (Map<String,String> map : listDataStr){
            // 只有产品状态应该为“报告通过”的才可以进行发行登记。
            checkBeforProdPersonIssuer(map);
            if (StringUtil.isNotEmpty(map.get("ERRMSG"))){
                listError.add(map);
            }
        }

        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("PROD_ID"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("REGIST_SEQNO"));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_产品发行登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_产品发行登记.getCodeName());
            chbReportCheck.setValidKey("产品代码:"+m.get("PROD_ID"));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(checkdate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }            
            
            //将报送表中数据设置成待维护
            // chbBeforeSubErrorCheckMapper.updateProdIssueSub(m.get("REGIST_SEQNO"));
        }
        logger.info("产品发行登记错误检查结束.");
        return resultList;
    }

    /**
     *
     * 发行登记检查
     * @param checkdate
     * @return
     * @see
     * @since 1.0
     */
    public List<CheckReport001Dto> checkProdRaiseTotalReg(Date checkdate,List<String> listProd,Map<String, List<ChbCheckRuleout>> mapRuleout) {
        logger.info("产品募集总量登记错误检查开始.");
        List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();

        //读取数据，查询登记表数据
        List<Map<String,Object>> listData = chbBeforeSubErrorCheckMapper.selectProdRaiseTotalSub(checkdate,listProd);
        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,Object> singleMap:listData){
            Map <String,String> smap=new HashMap<String,String>();
            for(String singlekey:singleMap.keySet()){
                smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
            }
            listDataStr.add(smap);
        }

        List<Map<String, String>> listError = new ArrayList<>();
        for (Map<String,String> map : listDataStr){
            // 只有产品状态应该为“报告通过”的才可以进行发行登记。
            checkBeforTotalRegist(map, checkdate);
            if (StringUtil.isNotEmpty(map.get("ERRMSG"))){
                listError.add(map);
            }
        }

        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("PROD_ID"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("REGIST_SEQNO"));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_募集总量登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_募集总量登记.getCodeName());
            chbReportCheck.setValidKey("产品代码:"+m.get("PROD_ID"));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                    DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(checkdate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }

            //将报送表中数据设置成待维护
            // chbBeforeSubErrorCheckMapper.updateProdRaiseTotalSub(m.get("REGIST_SEQNO"));
        }
        logger.info("产品募集总量登记错误检查结束.");
        return resultList;
    }
    
    /**
     * 
     * 投资交易登记检查
     * @param checkdate
     * @return
     * @see
     * @since 1.0
     */
    public List<CheckReport001Dto> checkInvestTradeReg(Date checkdate,List<String> listProd,Map<String, List<ChbCheckRuleout>> mapRuleout) {
        logger.info("投资交易登记错误检查开始");
       List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();
       
        // 读取规则
        //List<CheckRule> listRules = dataCheckService.queryCheckRules("CHB_INVEST_TRADE_SUB");
        
        //读取数据，查询登记表数据
        List<Map<String,Object>> listData = chbBeforeSubErrorCheckMapper.selectInvestTradeSub(checkdate,listProd);
        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,Object> singleMap:listData){
        	Map <String,String> smap=new HashMap<String,String>();
        	for(String singlekey:singleMap.keySet()){
        		smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
        	}
        	listDataStr.add(smap);
        }
        // 数据校验
        //List<Map<String, String>> listError = DataCheckService.checkDataList(listDataStr, listRules);
        List<Map<String, String>> listError = new ArrayList<>();

        for (Map<String,String> map : listDataStr){
            // 判断产品是否募集或已终止。
            checkBeforTradeRegist(map);
            if (StringUtil.isNotEmpty(map.get("ERRMSG"))){
                listError.add(map);
            }
        }
        
        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("PROD_ID"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("REGIST_SEQNO"));
            chbReportCheck.setUniqueKey(m.get("PROD_ID")+GlobalConstant.COMMA+m.get("ASSET_CODE")+GlobalConstant.COMMA+m.get("TRADE_CODE")+GlobalConstant.COMMA+m.get("CUR_ASSET_TYPE")+" "+SvrDataUtil.getNameFromDataDict(DictCode.资金流动类型,m.get("CUR_ASSET_TYPE")));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_资产交易信息登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_资产交易信息登记.getCodeName());
            chbReportCheck.setValidKey("产品代码:"+m.get("PROD_ID") + (StringUtil.isNotEmpty(m.get("ASSET_CODE")) ? (";资产代码:"+m.get("ASSET_CODE")): "")
            													  +(StringUtil.isNotEmpty(m.get("TRADE_CODE")) ? (";行内交易编码:"+m.get("TRADE_CODE")): "")
            													  +(StringUtil.isNotEmpty(m.get("CUR_ASSET_TYPE")) ? (";资金流动类型:"+m.get("CUR_ASSET_TYPE")+SvrDataUtil.getNameFromDataDict(DictCode.资金流动类型,m.get("CUR_ASSET_TYPE"))): ""));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(checkdate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }            
            
            //将报送表中数据设置成待维护
            // chbBeforeSubErrorCheckMapper.updateInvestTradeSub(m.get("REGIST_SEQNO"));
        }
        logger.info("投资交易登记错误检查结束.");
        return resultList;
    }
    
    /**
     * 
     * 资产要素登记检查
     * @param checkdate
     * @return
     * @see
     * @since 1.0
     */
    public List<CheckReport001Dto> checkAssetInfoReg(Date checkdate,List<String> keys,Map<String, List<ChbCheckRuleout>> mapRuleout) {
        logger.info("资产要素登记错误检查开始");
       List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();
       
        // 读取规则
        List<CheckRule> listRules = dataCheckService.queryCheckRules("CHB_ASSET_INFO_SUB");
        
        //读取数据，查询登记表数据
        List<Map<String,Object>> listData = chbBeforeSubErrorCheckMapper.selectAssetInfoSub(checkdate,keys);
        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,Object> singleMap:listData){
        	Map <String,String> smap=new HashMap<String,String>();
        	for(String singlekey:singleMap.keySet()){
        		smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
        	}
        	listDataStr.add(smap);
        }
        // 数据校验
        List<Map<String, String>> listError = DataCheckService.checkDataList(listDataStr, listRules,false);
        
        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("PROD_ID"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("REGIST_SEQNO"));
            //chbReportCheck.setUniqueKey(m.get("PROD_ID")+GlobalConstant.COMMA+m.get("ASSET_CODE"));
            chbReportCheck.setUniqueKey(m.get("ASSET_CODE"));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_资产要素信息登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_资产要素信息登记.getCodeName());
            chbReportCheck.setValidKey("资产代码:"+m.get("ASSET_CODE"));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(checkdate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }            
            
            //将报送表中数据设置成待维护
            //chbBeforeSubErrorCheckMapper.updateAssetInfoSub(m.get("REGIST_SEQNO"));
        }
        logger.info("资产要素错误检查结束.");
        return resultList;
    }
    
    /**start   中债一二期报送前检查   add   by  ***  20201010 */
    /**
     * 
     * 存续期登记检查
     * @param checkdate
     * @return
     * @see
     * @since 1.0
     */
    public List<CheckReport001Dto> checkProdDeclareSub(Date checkdate,List<String> listProd,Map<String, List<ChbCheckRuleout>> mapRuleout) {
        logger.info("产品申报登记强制错误检查开始.");
        List<Map<String,Object>> listData = chbBeforeSubErrorCheckMapper.selectProdDeclareSub(checkdate,listProd);

        //定义返回值
        List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();
       
        //读取数据，查询登记表数据
        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,Object> singleMap:listData){
        	Map <String,String> smap=new HashMap<String,String>();
        	for(String singlekey:singleMap.keySet()){
        		smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
        	}
        	listDataStr.add(smap);
        }
        List<Map<String, String>> listError = new ArrayList<>();
        // 数据校验
        for(Map<String, String>  map:listDataStr){
        	checkBeforProdDeclareSub(map);
        	listError.add(map);
        }
        
        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("PROD_ID"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("REGIST_SEQNO"));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_产品申报登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_产品申报登记.getCodeName());
            chbReportCheck.setValidKey("产品代码:"+m.get("PROD_ID"));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(checkdate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }            
            
            //将报送表中数据设置成待维护
            // chbBeforeSubErrorCheckMapper.updateProdDeclareSub(m.get("REGIST_SEQNO"));
        }
        logger.info("产品申报登记强制错误检查结束.");
        return resultList;
    }
    
    /**
     * 申报数据检查
     * @param map
     */
    public  void checkBeforProdDeclareSub(Map<String, String>  dataMap){
        StringBuffer errMsgSb = new StringBuffer();
    	
    	//须填报已登记的产品审批人身份证号
        String  appoverId=  dataMap.get("APPROVER_ID"); //产品审批人身份证号
        if(!isInEmpoyeeSub(appoverId, "02")){
            errMsgSb.append("须填报已登记的产品审批人身份证号!");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        //须填报已登记的产品设计人身份证号
        String  designerId=  dataMap.get("DESIGNER_ID"); //产品设计人身份证号
        if(!isInEmpoyeeSub(designerId, "03")){
            errMsgSb.append("须填报已登记的产品设计人身份证号!");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        //须填报已登记的投资经理身份证号
        String  managerId=  dataMap.get("MANAGER_ID"); //投资经理身份证号
        if(!isInEmpoyeeSub(managerId, "01")){
            errMsgSb.append("须填报已登记的投资经理身份证号!");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }
        
        //募集起始日期（从） 有效性验证
        //募集方式 01公募 02私募
        String  raiseType=  dataMap.get("RAISE_TYPE"); 
        //募集起始日期（从）
        Date raisePsTrdate = DateUtil.parse(dataMap.get("RAISE_PSTRDATE"));          
        
        //最早募集起始日-公募
        ChbDeadLine001Dto holidaymarket001DtoGM =  chbDeadLineMapper.getChbDeadline(CacheUtil.getSysParam(SVReportGlobalConstant.SVR_CALENDAR_ID),
        		DateUtil.format(DateUtil.now()), "02", 10);        
        //最早募集起始日-私募
        ChbDeadLine001Dto holidaymarket001DtoSM =  chbDeadLineMapper.getChbDeadline(CacheUtil.getSysParam(SVReportGlobalConstant.SVR_CALENDAR_ID),
        		DateUtil.format(DateUtil.now()), "02", 2);
        
        if(DictCodeListExt2.募集方式_中债_公募.getValue().equals(raiseType)) {
        	//公募产品：募集起始日期(从)应晚于申报登记日至少 10个工作日
        	if(raisePsTrdate.compareTo(holidaymarket001DtoGM.getBusiDate()) == -1){
                errMsgSb.append("公募产品：募集起始日期(从)应晚于申报登记日至少 10个工作日");
                errMsgSb.append(GlobalConstant.VERTICAL_LINE);
            }
        }else if(DictCodeListExt2.募集方式_中债_私募.getValue().equals(raiseType)) {
        	//私募产品：募集起始日期(从)应晚于申报登记日至少 2 个工作日
        	if(raisePsTrdate.compareTo(holidaymarket001DtoSM.getBusiDate()) == -1){
                errMsgSb.append("私募产品：募集起始日期(从)应晚于申报登记日至少 2 个工作日");
                errMsgSb.append(GlobalConstant.VERTICAL_LINE);
            }
        }
                      
        //是否上传9大附件
        //String fileNo = ;
        if(!isUploadFile(dataMap)){
            errMsgSb.append("没有上传9大附件!");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        String errMSg = "";
        if (errMsgSb.length() > 0){
            errMSg = errMsgSb.substring(0, errMsgSb.length() - GlobalConstant.VERTICAL_LINE.length());
        }
        dataMap.put("ERRMSG", errMSg);

    }

    private boolean isUploadFile(Map<String, String> dataMap) {
        return StringUtil.isNotEmpty(chbBeforeSubErrorCheckMapper.isUploadFile(dataMap));
    }


    /**start  判断身份证号是否有效   add by  wangzehui  20201010**/
    public boolean isInEmpoyeeSub(String id, String employeeType){
        int i = chbBeforeSubErrorCheckMapper.isInEmpoyeeSub(id, employeeType);
        return i>0;
    }
    /**end  判断身份证号是否有效   add by  wangzehui  20201010**/
    public void checkBeforProdPersonIssuer(Map<String, String> dataMap) {
        StringBuffer errMsgSb = new StringBuffer();
        String  prodId=  dataMap.get("PROD_ID");
        //判断产品代码是否已申报
        int i = chbBeforeSubErrorCheckMapper.isInProDeclareReg(prodId);
        if(i<1){
            errMsgSb.append("只有产品状态应该为“报告通过”的才可以进行报送");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        // 查询产品登记编码对应的产品是否存在
        if(!checkProdRegistCodeExist(dataMap.get("PROD_REGIST_CODE"))){
            errMsgSb.append("该产品不存在。");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        String errMSg = "";
        if (errMsgSb.length() > 0){
            errMSg = errMsgSb.substring(0, errMsgSb.length() - GlobalConstant.VERTICAL_LINE.length());
        }
        dataMap.put("ERRMSG", errMSg);
    }

    /**
     *
     * 资产估值信息登记检查
     * @param checkdate
     * @return
     * @see
     * @since 1.0
     */
    public List<CheckReport001Dto> checkAstValueSub(Date checkdate,Map<String, List<ChbCheckRuleout>> mapRuleout,List<String> keys) {
        logger.info("资产估值信息登记强制错误检查开始.");
        List<Map<String,Object>> listData = chbBeforeSubErrorCheckMapper.selectChbAstValSub(checkdate,keys);

        //定义返回值
        List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();

        //读取数据，查询登记表数据
        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,Object> singleMap:listData){
            Map <String,String> smap=new HashMap<String,String>();
            for(String singlekey:singleMap.keySet()){
                smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
            }
            listDataStr.add(smap);
        }
        List<Map<String, String>> listError = new ArrayList<>();
        // 数据校验
        for(Map<String, String>  map:listDataStr){
            checkBeforAstValue(map);
            listError.add(map);
        }

        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("ASSET_CODE"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("REGIST_SEQNO"));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_资产估值信息登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_资产估值信息登记.getCodeName());
            chbReportCheck.setValidKey("资产代码:"+m.get("ASSET_CODE"));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                    DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(checkdate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }

            //将报送表中数据设置成待维护
            // chbBeforeSubErrorCheckMapper.updateProdDeclareSub(m.get("REGIST_SEQNO"));
        }
        logger.info("资产估值信息登记强制错误检查结束.");
        return resultList;
    }

    public void checkBeforAstValue(Map<String, String> dataMap) {
        String errMsg = "";
        String assetCode = dataMap.get("ASSET_CODE");
        //判断产品的行内资产/资产负债编码是否存在
        int i = chbBeforeSubErrorCheckMapper.isInAssetInfoSub(assetCode);
        if(i<1){
            errMsg+="行内资产/负债编码为"+assetCode+"的资产不存在, 不能新建估值信息";
        }
        dataMap.put("ERRMSG", errMsg);
    }

    public void checkBeforTradeRegist(Map<String, String> dataMap) {
        StringBuffer errMsgSb = new StringBuffer();
        String prodId = dataMap.get("PROD_ID");
        //判断产品是否已募集或已终止
        int i = chbBeforeSubErrorCheckMapper.isInRaisetotSub(prodId);
        int s = chbBeforeSubErrorCheckMapper.isInPrdEndSub(prodId);
        if(i<1&&s<1){
            errMsgSb.append("只有产品状态为“已募集登记”或“已终止登记”的产品才可以报送");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }
        String assetCode = dataMap.get("ASSET_CODE");
        if (StringUtil.isNotEmpty(assetCode)){
            int j = chbBeforeSubErrorCheckMapper.isInAssetInfoSub(assetCode);
            if(j<1){
                errMsgSb.append("未进行资产要素登记！");
                errMsgSb.append(GlobalConstant.VERTICAL_LINE);
            }
        }
        // 查询产品登记编码对应的产品是否存在
        if(!checkProdRegistCodeExist(dataMap.get("PROD_REGIST_CODE"))){
            errMsgSb.append("该产品不存在。");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        String errMSg = "";
        if (errMsgSb.length() > 0){
            errMSg = errMsgSb.substring(0, errMsgSb.length() - GlobalConstant.VERTICAL_LINE.length());
        }
        dataMap.put("ERRMSG", errMSg);
    }


    public void checkDurationReg(Map<String, String> dataMap) {
        StringBuffer errMsgSb = new StringBuffer();
        String prodId = dataMap.get("PROD_ID");
        //判断产品是否已募集或已终止
        int i = chbBeforeSubErrorCheckMapper.isInRaisetotSub(prodId);
        int s = chbBeforeSubErrorCheckMapper.isInPrdEndSub(prodId);
        if(i<1&&s<1){
            errMsgSb.append("只有产品状态为“已募集登记”或“已终止登记”的产品才可以报送");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        // 查询产品登记编码对应的产品是否存在
        if(!checkProdRegistCodeExist(dataMap.get("PROD_REGIST_CODE"))){
            errMsgSb.append("该产品不存在。");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        String errMSg = "";
        if (errMsgSb.length() > 0){
            errMSg = errMsgSb.substring(0, errMsgSb.length() - GlobalConstant.VERTICAL_LINE.length());
        }
        dataMap.put("ERRMSG", errMSg);
    }

    public void checkBeforTerminationRegist(Map<String, String> dataMap) {
        StringBuffer errMsgSb = new StringBuffer();
        String prodId = dataMap.get("PROD_ID");
        //判断产品是否已经募集
        int i = chbBeforeSubErrorCheckMapper.isInRaisetotSub(prodId);
        if(i<1){
            errMsgSb.append("只有产品状态为“已募集登记”的产品才可以报送");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        // 查询产品登记编码对应的产品是否存在
        if(!checkProdRegistCodeExist(dataMap.get("PROD_REGIST_CODE"))){
            errMsgSb.append("该产品不存在。");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        String errMSg = "";
        if (errMsgSb.length() > 0){
            errMSg = errMsgSb.substring(0, errMsgSb.length() - GlobalConstant.VERTICAL_LINE.length());
        }
        dataMap.put("ERRMSG", errMSg);
    }

    private void checkBeforTotalRegist(Map<String, String> dataMap, Date checkdate) {
        StringBuffer errMsgSb = new StringBuffer();
        String prodId = dataMap.get("PROD_ID");
        int i = chbBeforeSubErrorCheckMapper.isInPrdIssueSub(prodId);
        if(i<1){
            errMsgSb.append("只有产品状态应该为“已发行登记”的产品才可以报送");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        // 查询产品登记编码对应的产品是否存在
        if(!checkProdRegistCodeExist(dataMap.get("PROD_REGIST_CODE"))){
            errMsgSb.append("该产品不存在。");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        // 产品销售区域超出产品申报登记选择的区域，备注必填。
        List<String> saleAreaList = new ArrayList<>();
        // 产品销售区域及募集金额
        String saleAreaAmt = dataMap.get("SALE_AREA_AMT");
        if (StringUtil.isNotEmpty(saleAreaAmt)){
            List<String> singleSaleAreaAmtList = Arrays.asList(saleAreaAmt.split(GlobalConstant.SEMICOLON));// 370000 山东省,60000.00
            for (String singleAreaAmt : singleSaleAreaAmtList){
                List<String> ccyAmtDetailList = Arrays.asList(singleAreaAmt.split(" "));
                if (!StringUtil.isEmptyList(ccyAmtDetailList)){
                    saleAreaList.add(ccyAmtDetailList.get(0));
                }
            }
        }
        HashSet<String> saleAreaSet = new HashSet<String>(saleAreaList);
        if (saleAreaSet != null && StringUtil.isNotEmpty(prodId)){
            Map<String, Object> map = new HashMap<>();
            map.put("prodId", prodId);
            List<ChbPrdDeclareSub> chbPrdDeclareSubList = commonCodeCheckMapper.queryChbPrdDeclareSubByRegCode(map);
            if(!StringUtil.isEmptyList(chbPrdDeclareSubList) && StringUtil.isNotEmpty(chbPrdDeclareSubList.get(0).getSaleArea())){
                List<String> declareAreaList = Arrays.asList(chbPrdDeclareSubList.get(0).getSaleArea().split(GlobalConstant.COMMA));
                boolean flag = false;
                for (String area : saleAreaSet){
                    if (!declareAreaList.contains(area)){
                        flag = true;
                        break;
                    }
                }
                if (flag && StringUtil.isEmpty(dataMap.get("REMARK"))){
                    errMsgSb.append("产品销售区域超出产品申报登记选择的区域，备注必填。");
                    errMsgSb.append(GlobalConstant.VERTICAL_LINE);
                }
            }
        }

        //当系统参数设置为【是否做客户数检查 = 01：是】的时候，进行以下处理：
        if(StringUtil.isNotEmpty(prodId) && StringUtil.equals("01",SvrDataUtil.getSysParam("SAVE_CUS_NUM_CHECK_01"))){
            List<CheckReport001Dto> resList = svrCustomerNumCheckService.checkProdRaiseTotalRegCusNumSingle(checkdate, prodId);
            if (!StringUtil.isEmptyList(resList)){
                resList.forEach(x -> {
                    errMsgSb.append(x.getErrorInfo());
                    errMsgSb.append(GlobalConstant.VERTICAL_LINE);
                });
            }
        }

        String errMSg = "";
        if (errMsgSb.length() > 0){
            errMSg = errMsgSb.substring(0, errMsgSb.length() - 1);
        }
        dataMap.put("ERRMSG", errMSg);
    }

    public void checkBeforBotAstPositionRegist(Map<String, String> dataMap) {
        StringBuffer errMsgSb = new StringBuffer();
        String assetCode = dataMap.get("ASSET_CODE");
        String planAssetId = dataMap.get("PLAN_ASSET_ID");
        //判断对应资管及委外资产行内资产/负债编码是否匹配数据库中已有的记录
        int i = chbBeforeSubErrorCheckMapper.isInAssetInfoSub(planAssetId);
        if(i<1){
            errMsgSb.append("对应资管及委外资产行内资产/负债编码为:"+planAssetId+"的资管及委外资产不存在");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }
        //判断底层资产行内资产负债/编码是否匹配数据库中已有的记录
        int j = chbBeforeSubErrorCheckMapper.isInAssetInfoSub(assetCode);
        if(j<1){
            errMsgSb.append("底层资产行内资产负债/编码为:"+assetCode+"的底层资产不存在");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        List<String> assetTypeLit = chbBeforeSubErrorCheckMapper.isOutsourOrAssetMana(planAssetId);
        if (!StringUtil.isEmptyList(assetTypeLit)
                && !StringUtil.isEmptyList(assetTypeLit.stream().filter(x -> !StringUtil.equals(x, "12")
                                                                            && !StringUtil.equals(x, "16")
                                                                            && !StringUtil.equals(x, "19")).collect(Collectors.toList()))){
            errMsgSb.append("对应资管及委外资产行内资产/负债编码为:"+planAssetId+"的资产不属于资管及委外资产");
            errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }

        String errMSg = "";
        if (errMsgSb.length() > 0){
            errMSg = errMsgSb.substring(0, errMsgSb.length() - GlobalConstant.VERTICAL_LINE.length());
        }
        dataMap.put("ERRMSG", errMSg);
    }

    public Collection<? extends CheckReport001Dto> checkChbBotAstReg(Date checkdate, List<String> listProd, Map<String, List<ChbCheckRuleout>> mapRuleout) {
        logger.info("投资交易登记错误检查开始");
        List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();

        // 读取规则
        //List<CheckRule> listRules = dataCheckService.queryCheckRules("CHB_INVEST_TRADE_SUB");

        //读取数据，查询登记表数据
        List<Map<String,Object>> listData = chbBeforeSubErrorCheckMapper.selectChbBotAst(checkdate,listProd);
        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,Object> singleMap:listData){
            Map <String,String> smap=new HashMap<String,String>();
            for(String singlekey:singleMap.keySet()){
                smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
            }
            listDataStr.add(smap);
        }
        // 数据校验
        //List<Map<String, String>> listError = DataCheckService.checkDataList(listDataStr, listRules);
        List<Map<String, String>> listError = new ArrayList<>();

        for (Map<String,String> map : listDataStr){
            // 判断产品是否募集或已终止。
            checkBeforBotAstPositionRegist(map);
            if (StringUtil.isNotEmpty(map.get("ERRMSG"))){
                listError.add(map);
            }
        }

        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("PROD_ID"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("REGIST_SEQNO"));
            chbReportCheck.setUniqueKey(m.get("PROD_ID")+GlobalConstant.COMMA+m.get("ASSET_CODE"));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_资管计划底层持仓信息登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_资管计划底层持仓信息登记.getCodeName());
            chbReportCheck.setValidKey("资管计划代码:"+m.get("PLAN_ASSET_ID")+";资产代码:"+m.get("ASSET_CODE")+";持仓日期:"+m.get("POS_DATE").substring(0, 10));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                    DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(checkdate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }

            //将报送表中数据设置成待维护
            // chbBeforeSubErrorCheckMapper.updateChbBotAstSub(m.get("REGIST_SEQNO"));
        }
        logger.info("投资交易登记错误检查结束.");
        return resultList;
    }

    /**
     *  查询产品登记编码对应的产品是否存在
     * @param prodRegistCode 产品登记编码
     * @return 是否存在
     */
    private boolean checkProdRegistCodeExist(String prodRegistCode){
        boolean existFlag = true;
        if (StringUtil.isNotEmpty(prodRegistCode)){
            List<ChbPrdresistCode> chbPrdresistCodeList = commonCodeCheckMapper.queryChbPrdresistCodeByRegCode(prodRegistCode);
            if (StringUtil.isEmptyList(chbPrdresistCodeList)){
                existFlag = false;
            }
        }
        return existFlag;
    }
    /**end   中债一二期报送前检查   add   by  ***  20201010 */
    
    /**
     * 从业人员check
     * @param checkdate
     * @param listProd
     * @param mapRuleout
     * @return
     */
    public Collection<? extends CheckReport001Dto> checkChbEmployeeReg(List<Map<String,String>> listData, Date batchDate,List<String> listProd, Map<String, List<ChbCheckRuleout>> mapRuleout) {
        logger.info("从业人员登记错误检查开始");
        List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();

        List<Map<String,String>> listDataStr=new ArrayList<Map<String,String>>();
        for(Map<String,String> singleMap:listData){
            Map <String,String> smap=new HashMap<String,String>();
            for(String singlekey:singleMap.keySet()){
                smap.put(singlekey, singleMap.get(singlekey)==null?null:singleMap.get(singlekey).toString());
            }
            listDataStr.add(smap);
        }
        // 数据校验
        List<Map<String, String>> listError = new ArrayList<>();
        Iterator<Map<String, String>> it = listDataStr.iterator();
        while(it.hasNext()){
        	Map<String,String> map = it.next();
        	//发行机构代码必须与登记银行代码一致check
        	checkBankCode(map);
        	
        	//报文中不能有重复数据
//        	checkDupliData(map,it);
        	
        	//已报送的数据不能重复登记
            checkReSubData(map);
           
            if (StringUtil.isNotEmpty(map.get("ERRMSG"))){
                listError.add(map);
            }
        }

        for ( Map<String,String> m:listError ) {
            //增加例外情况过滤
            if(mapRuleout.containsKey(m.get("EMPLOYEE_NAME") + GlobalConstant.VERTICAL_LINE +m.get("IDENTIFY_NUMBER"))){
                continue;
            }
            //不合格的数据加入到不合格列表
            CheckReport001Dto chbReportCheck = new CheckReport001Dto();
            chbReportCheck.setCheckSeqno(IDGenerator.generate(32));
            chbReportCheck.setRefseqno(m.get("SUMIT_SEQ"));
            chbReportCheck.setUniqueKey(m.get("EMPLOYEE_NAME")+GlobalConstant.COMMA+m.get("IDENTIFY_NUMBER"));
            chbReportCheck.setRepCode(DictCodeListExt2.报表名称_从业人员登记.getValue());
            chbReportCheck.setRepName(DictCodeListExt2.报表名称_从业人员登记.getCodeName());
            chbReportCheck.setValidKey("姓名:"+m.get("EMPLOYEE_NAME")+";身份证号:"+m.get("IDENTIFY_NUMBER"));
            chbReportCheck.setCheckResult(SysDataUtil.getCodeFromDataDictByName(
                    DictCode.检查结果, DictCodeListExt2.检查结果_检查不通过.getCodeName()));
            chbReportCheck.setDataTime(batchDate);
            String errorInfo=m.get("ERRMSG");
            if(!errorInfo.equals("")){
                chbReportCheck.setErrorInfo(errorInfo);
                resultList.add(chbReportCheck);
            }
        }
        logger.info("投资交易登记错误检查结束.");
        return resultList;
    }
    
    /**
     * 发行机构代码必须与登记银行代码一致check
     * @param dataMap
     */
    private void checkBankCode(Map<String, String> dataMap) {
    	StringBuffer errMsgSb = new StringBuffer();
    	//发行机构代码
        String issueParthId = dataMap.get("ISSUE_PARTY_ID");
//        String bankCode = CommonUtil.selectBankCode();
        String bankCode = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        if(!bankCode.equals(issueParthId)){
        	 errMsgSb.append("发行机构代码:" + issueParthId +"必须与登记银行代码相同");
             errMsgSb.append(GlobalConstant.VERTICAL_LINE);
        }
        dataMap.put("ERRMSG", errMsgSb.toString());
	}
    
    /**
     * 从业人员类型+身份证号码不能重复
     * @param map
     * @param listDataStr
     */
    /*private void checkDupliData(Map<String,String> dataMap,Iterator<Map<String, String>> iterator) {
    	StringBuffer errMsgSb = new StringBuffer();
    	if(StringUtil.isNotEmpty(dataMap.get("ERRMSG"))){
    		errMsgSb.append(dataMap.get("ERRMSG"));
    	}
    	while(iterator.hasNext()){
    		Map<String,String> map1 = iterator.next();
    		if(getKey(dataMap).equals(getKey(map1))){
    			errMsgSb.append("从业人员类型: " + (dataMap.get("EMPLOYEE_TYPE") + " " + SvrDataUtil.getNameFromDataDict(DictCode.从业人员类型, dataMap.get("EMPLOYEE_TYPE")))
    			                +" 身份证件号: " + dataMap.get("IDENTIFY_NUMBER") + "不能重复");
                errMsgSb.append(GlobalConstant.VERTICAL_LINE);
    		}
    	}
        dataMap.put("ERRMSG", errMsgSb.toString());
	}*/

    /**
     * 同一从业人员类型，身份证号不能重复登记
     * @param dataMap
     */
	private void checkReSubData(Map<String, String> dataMap) {
		StringBuffer errMsgSb = new StringBuffer();
    	if(StringUtil.isNotEmpty(dataMap.get("ERRMSG"))){
    		errMsgSb.append(dataMap.get("ERRMSG"));
    	}
        //已登记人员
		int cnt = chbBeforeSubErrorCheckMapper.isInEmployeeSub(getKey(dataMap));
		if(cnt > 0){
			errMsgSb.append("从业人员类型: " + (dataMap.get("EMPLOYEE_TYPE") + " " + SvrDataUtil.getNameFromDataDict(DictCode.从业人员类型, dataMap.get("EMPLOYEE_TYPE")))
			                +" 身份证件号: " + dataMap.get("IDENTIFY_NUMBER") + "已注册,不能重复注册");
			errMsgSb.append(GlobalConstant.VERTICAL_LINE);
		}
		
        String errMSg = "";
        if (errMsgSb.length() > 0){
            errMSg = errMsgSb.substring(0, errMsgSb.length() - GlobalConstant.VERTICAL_LINE.length());
        }
        
        dataMap.put("ERRMSG", errMSg);
		
	}
    
    private String getKey(Map<String,String> map){
    	return map.get("EMPLOYEE_TYPE") + GlobalConstant.VERTICAL_LINE + map.get("IDENTIFY_NUMBER");
    }
}

/*
 * 文件名：ChbAstValueRegist001Service.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：zhangzhongliang
 * 修改时间：2019年1月28日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.tams.svreport.chb.astvalueregist.service;


import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.BaseMapper;
import com.joyintech.tams.common.base.entity.ChbAssetSubStatus;
import com.joyintech.tams.common.svreport.chb.constant.CheckConfConstants;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.common.svreport.datacheck.beforeinsert.util.SvrMaptoEntityUtil;
import com.joyintech.tams.common.svreport.datacheck.dao.ChbCheckRuleoutMapper;
import com.joyintech.tams.common.svreport.datacheck.dto.ChbCheckRuleout;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.dto.ExcelValidDto;
import com.joyintech.tams.report.common.service.SvrCommonExportService;
import com.joyintech.tams.report.interf.common.constant.DateUtil;
import com.joyintech.tams.report.submitmanagement.checkreport.dao.CheckReport001Mapper;
import com.joyintech.tams.report.submitmanagement.checkreport.dto.CheckReport001Dto;
import com.joyintech.tams.svreport.chb.assetinforeg.dao.AssetInfoRegist001Mapper;
import com.joyintech.tams.svreport.chb.astvalueregist.dao.ChbAstValSubHisMapper;
import com.joyintech.tams.svreport.chb.astvalueregist.dao.ChbAstValueRegist001Mapper;
import com.joyintech.tams.svreport.chb.astvalueregist.dto.ChbAstValueRegist001Dto;
import com.joyintech.tams.svreport.chb.datacheck.beforesub.service.BeforeSubErrorCheck;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 资产估值信息登记service
 *
 * @author zhangzhongliang
 * @version 1.0 2019年2月24日
 * @see ChbAstValueRegist001Service
 * @since 1.0
 */
@Service(value="svrChbAstValueRegist001Service")
public class ChbAstValueRegist001Service extends SvrCommonExportService<ChbAstValueRegist001Dto> {

	/**
	 * 投资者持有信息登记mapper
	 */
	@Autowired
	private ChbAstValueRegist001Mapper chbAstValueRegist001Mapper;

	@Autowired
	private ChbAstValSubHisMapper chbAstValSubHisMapper;
	
	
	@Autowired
	private AssetInfoRegist001Mapper assetInfoRegist001Mapper;
	
    /**
     * 长sql最大一次插入或者更新条数
     */
    @Value("${application.reportInsertLongCount}")
    public  String reportInsertLongCount;
    /**
     * sql最大一次最大删除条数
     */
    @Value("${application.reportSelectOrDeleteCount}")
    public  String reportSelectOrDeleteCount;
    /**
     * 短sql最大一次最大插入条数
     */
    @Value("${application.reportInsertCount}")
    public  String reportInsertCount;
    /**
     * 短sql最大一次最大插入条数
     */
    @Value("${application.reportSelectOrDeleteCountForShenFen}")
    public  int reportSelectOrDeleteCountForShenFen;

    /**
     * 检查通过标志
     */
    @SuppressWarnings("unused")
    private static final String IS_SUCESS_INVESTORS_CHECK = "01";
	@Autowired
	private BeforeSubErrorCheck beforeSubErrorCheck;

	@Autowired
	private SvrMaptoEntityUtil svrMaptoEntityUtil;
	private String tableName = "CHB_AST_VAL_SUB";

	/**
	 * 中债基础数据mapper
	 */
	@Autowired
	private BaseMapper baseMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CheckReport001Mapper checkReport001Mapper;

	@Autowired
	private ChbCheckRuleoutMapper chbCheckRuleoutMapper;

	/**
	 * 查询页面数据
	 *
	 * @param pageParam
	 *            查询条件
	 * @return 结果列表
	 * @see
	 * @since 1.0
	 */
	public Page<ChbAstValueRegist001Dto> searchPage(Map<String, Object> pageParam) {
		dealSearchParam(pageParam);
		Page<ChbAstValueRegist001Dto> page = null;
		//add by leipeng 20200628
//		pageParam.put("orgCode", CommonUtil.getTreeInstCode());
//		String issuePartyId = CommonUtil.selectBankCode();
		String issuePartyId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        if (pageParam.get(GlobalConstant.PARAM_PAGE) != null
                && pageParam.get(GlobalConstant.PARAM_LENGTH) != null){
    		int pagenum = Integer.parseInt(pageParam.get(GlobalConstant.PARAM_PAGE).toString());
            int legth = Integer.parseInt(pageParam.get(GlobalConstant.PARAM_LENGTH).toString());
            if(CommonUtil.isBAmodel()){
    			page = queryForPage(
        				pagenum,legth,()->chbAstValueRegist001Mapper.searchPageEx(pageParam));
    			//update byleipeng  【资产要素登记】公共资产，发行机构代码根据登录人维护 机构登记银行代码管理 登记银行代码
    			page.getRoot().forEach(item -> {
    				if (StringUtil.isEmpty(item.getIssuePartyId())){
    					item.setIssuePartyId(issuePartyId);
    				}
    			});
    		}else{
    			page = queryForPage(
        				pagenum,legth,()->chbAstValueRegist001Mapper.searchPage(pageParam));
    		}
        } else {
        	if(CommonUtil.isBAmodel()){
    			page = queryForPage(ChbAstValueRegist001Mapper.class,"searchPageEx",pageParam);
    			//update byleipeng  【资产要素登记】公共资产，发行机构代码根据登录人维护 机构登记银行代码管理 登记银行代码
    			page.getRoot().forEach(item -> {
    				if (StringUtil.isEmpty(item.getIssuePartyId())){
    					item.setIssuePartyId(issuePartyId);
    				}
    			});

    		}else{
    			page = queryForPage(ChbAstValueRegist001Mapper.class,"searchPage",pageParam);
    		}
        }
        dealResultlist(page.getRoot());
		return page;
	}
	
	private void dealResultlist(List<ChbAstValueRegist001Dto> resultlist){
		if(!CollectionUtils.isEmpty(resultlist)){
			for(ChbAstValueRegist001Dto dto : resultlist){
				if(StringUtil.isNotEmpty(dto.getSubmitFeedbackStatus())){
					dto.setSubmitFeedbackStatus(SvrDataUtil.getNameFromDataDict(DictCode.报送反馈状态,dto.getSubmitFeedbackStatus()));
				}

				//银行模式时,资产数据取当前机构的产品登记编码
				if(!CommonUtil.isBAmodel()){
					dto.setIssuePartyId(CommonUtil.selectBankCode());
				}
			}
		}
	}

	/**
	 * 处理查询前的参数 <功能详细描述>
	 *
	 * @param pageParam
	 * @see
	 * @since 1.0
	 */
	public void dealSearchParam(Map<String, Object> pageParam) {
		// 选择行数据时
		if (pageParam.containsKey("keyList")) {
			String keyList = pageParam.get("keyList").toString();
			try {
				List<ChbAstValueRegist001Dto> astValueList = JsonUtils.json2List(keyList,
						ChbAstValueRegist001Dto.class);
				pageParam.put("astValueList", astValueList);
			} catch (IOException e) {
	            logger.error(e.getMessage(),e);
			}
		} else {
			// 查询参数条件
			// 数据状态
			String status = (String) pageParam.get("status");
			// 报送状态
			String sendStatus = (String) pageParam.get("sendStatus");
			// 反馈状态
			String submitFeedbackStatus = (String) pageParam.get("submitFeedbackStatus");


			// 将数据状态当做多选下拉框处理
			if (StringUtil.isNotBlank(status)) {
				pageParam.put("statusList", StringUtil.splitToList(GlobalConstant.COMMA, status));
			}
			// 将报送状态当做多选下拉框处理
			if (StringUtil.isNotBlank(sendStatus)) {
				pageParam.put("sendStatusList", StringUtil.splitToList(GlobalConstant.COMMA, sendStatus));
				if(StringUtil.splitToList(GlobalConstant.COMMA, sendStatus).contains(DictCodeListExt2.报送状态_未报送.getValue())){
					pageParam.put("notSubmitStatus", DictCodeListExt2.报送状态_未报送.getValue());
				}
			}
			// 将反馈状态当做多选下拉框处理
			if (StringUtil.isNotBlank(submitFeedbackStatus)) {
				pageParam.put("submitFeedbackStatusList",
						StringUtil.splitToList(GlobalConstant.COMMA, submitFeedbackStatus));
			}

		}
	}

	/**
	 * 导出功能查询
	 *
	 * @param pageParam
	 *            查询参数
	 * @return 结果集
	 * @see
	 * @since 1.0
	 */

	@SuppressWarnings("unchecked")
	public List<ChbAstValueRegist001Dto> selectAllList(Map<String, Object> pageParam) {
		dealSearchParam(pageParam);
		List<ChbAstValueRegist001Dto> dataList =  (List<ChbAstValueRegist001Dto>) pageParam.get("astValueList");// 查询参数列表

		List<ChbAstValueRegist001Dto> dtoList = new ArrayList<ChbAstValueRegist001Dto>();// 存放查询结果集
		if(CollectionUtils.isNotEmpty(dataList)){
//			selectAllDataBach(dataList,dtoList);
		}
		else{
			dtoList = chbAstValueRegist001Mapper.searchPage(pageParam);
		}


		return dtoList;
	}
	
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    protected void buildExportParam(ExportParam exportParam) {
        Map<String, Object> pageParam = exportParam.getSelectParam();
        dealSearchParam(pageParam);
        String pageIds = (String)pageParam.get("pageIds");
        if(StringUtil.isNotEmpty(pageIds)) {
            List<String> pageNames =  Arrays.asList(pageIds.split(GlobalConstant.COMMA));
            pageParam.replace("pageIds", pageIds, pageNames);
        }
        else{
            pageParam.replace("pageIds", pageIds, null);
        }

        // 导出方式flg
        String exportflag = (String)pageParam.get("exportflag");

        // 导出（未选中数据）
        if (StringUtil.equals(exportflag, "export")) {
            // 设置参数
            String flag = (String)pageParam.get("flag");
            String configFile = null;
            // 导出中债excel
            if (StringUtil.equals("0", flag)) {
                configFile = "/com/joyintech/tams/svreport/chb/astvalueregist/conf/export_conf.xml";
            }
            else if(StringUtil.equals("1", flag)) {
                configFile = "/com/joyintech/tams/svreport/chb/astvalueregist/conf/export_conf_all.xml";
            }
            else if(StringUtil.equals("2", flag)) {
            	configFile = "/com/joyintech/tams/svreport/chb/astvalueregist/conf/export_conf_del.xml";
            }
            exportParam.config(configFile).select(() -> chbAstValueRegist001Mapper.searchPage(pageParam));
        }
        // 将选择的数据导出成excel
        else if (StringUtil.equals(exportflag, "exportSelected")) {
        	List<ChbAstValueRegist001Dto> dataList =  (List<ChbAstValueRegist001Dto>) pageParam.get("astValueList");// 查询参数列表

            String flag = (String)pageParam.get("flag");
            String configFile = null;
            // 导出中债excel
            if (StringUtil.equals("0", flag)) {
                configFile = "/com/joyintech/tams/svreport/chb/astvalueregist/conf/export_conf.xml";
            }
            else {
                configFile = "/com/joyintech/tams/svreport/chb/astvalueregist/conf/export_conf_all.xml";
            }
            exportParam.config(configFile).select(() -> chbAstValueRegist001Mapper.queryExportListByKeys(dataList));
        }

     // 将选择的数据导出成excel删除模板
        else if (StringUtil.equals(exportflag, "exportDel")) {
        	List<ChbAstValueRegist001Dto> dataList =  (List<ChbAstValueRegist001Dto>) pageParam.get("astValueList");// 查询参数列表
            String flag = (String)pageParam.get("flag");
            String configFile = null;

            if (StringUtil.equals("2", flag)) {
                configFile = "/com/joyintech/tams/svreport/chb/astvalueregist/conf/export_conf_del.xml";
            }
            exportParam.config(configFile).select(() -> chbAstValueRegist001Mapper.queryExportListByKeys(dataList));
        }
    }


    public List<ChbAstValueRegist001Dto> searchPageExport(Map<String,Object> map){
         return chbAstValueRegist001Mapper.searchPage(map);
    }
    
    public List<ChbAstValueRegist001Dto> queryExportListByKeys(List<ChbAstValueRegist001Dto> registList){
        return chbAstValueRegist001Mapper.queryExportListByKeys(registList);
    }
    @Override
    public com.joyintech.framework.grid.Page<ChbAstValueRegist001Dto> selectPageData(Map<String, Object> pageParam) {
//        List<ChbAstValueRegist001Dto> data = null;
        //update by leipeng 20191221 修改导出为框架导出
    	Page<ChbAstValueRegist001Dto> page = null;
    	//update by leipeng 20200701 资产多法人改造
//    	String issuePartyId = CommonUtil.selectBankCode();
		String issuePartyId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
 		List<String>  orgCode = (List<String>) pageParam.get("orgCode");
		 int pagenum = Integer.parseInt(pageParam.get(GlobalConstant.PARAM_PAGE).toString());
	     int legth = Integer.parseInt(pageParam.get(GlobalConstant.PARAM_LENGTH).toString());
        String exportMethodFlg = (String)pageParam.get("exportflag");
        if (StringUtil.equals(exportMethodFlg, "export")) {
        	//update by leipeng 20200701 资产多法人改造 如果联社模式使用查询..非联社模式沿用以前方式
        	if(CommonUtil.isBAmodel()){
    			page = searchPage(pageParam);
    		}else{

				page = queryForPage(
						pagenum,legth,()->chbAstValueRegist001Mapper.searchPage(pageParam));
			}
//            data =  restTemplate.postForList(
//                    SvrServiceApplication.CHB_SERVICE_NAME + "/ChbAstValueRegist/searchPageExport",pageParam,
//                    ChbAstValueRegist001Dto.class);
        }  else {
            @SuppressWarnings("unchecked")
            List<ChbAstValueRegist001Dto> dataList =  (List<ChbAstValueRegist001Dto>) pageParam.get("astValueList");

           //update by leipeng 20200701 资产多法人改造
    		if(CommonUtil.isBAmodel()){
    			page = queryForPage(
        				pagenum,legth,()->chbAstValueRegist001Mapper.queryExportListByKeysEx(dataList,orgCode));
    			//update byleipeng  【资产要素登记】公共资产，发行机构代码根据登录人维护 机构登记银行代码管理 登记银行代码
    			page.getRoot().forEach(item -> {
    				if (StringUtil.isEmpty(item.getIssuePartyId())){
    					item.setIssuePartyId(issuePartyId);
    				}
    			});
    		}else{
    			page = queryForPage(
        				pagenum,legth,()->chbAstValueRegist001Mapper.queryExportListByKeys(dataList));
    		}
//    		dealResultlist(page.getRoot());
            
//            page =  restTemplate.postForList(
//                        SvrServiceApplication.CHB_SERVICE_NAME + "/ChbAstValueRegist/queryExportListByKeys",dataList,
//                        ChbAstValueRegist001Dto.class);
        }
//        com.joyintech.framework.grid.Page<ChbAstValueRegist001Dto> page = new com.joyintech.framework.grid.Page<ChbAstValueRegist001Dto>();
//        page.setRoot(data);
//        page.setTotal(data.size());
        return  page;
    }
    /**
 	 * 更新数据状态
 	 * update by leipeng 20200701  多法人数据改造
 	 * @param upKey
 	 * @see
 	 * @since 1.0
 	 */
 	@SuppressWarnings("unchecked")
 	@Transactional(rollbackFor = Exception.class)
 	public void updateSumStaus(Map<String, Object> map) {
 		//update by leipeng 20200702 联社模式资产类型更新方法修改
		if(CommonUtil.isBAmodel()){
			map.put("orgCode", CommonUtil.getTreeInstCode());

			//联社模式
			updateSubStatisEx(map);
		}else{
			//银行模式
			updateSubStatis(map);
		}
 		
 		
 		
 	}
 	
 	
 	/**
 	 * update by leipeng 20200701 资产多法人改造
	 * 更新登记编码信息方法-联社模式
	 * @param map
	 */
	public void updateSubStatisEx(Map<String, Object> map){
		dealSearchParam(map);
 		List<ChbAstValueRegist001Dto> chbBotAstPositionRegistListOrg  = new ArrayList<ChbAstValueRegist001Dto>();
 		chbBotAstPositionRegistListOrg = (List<ChbAstValueRegist001Dto>)map.get("astValueList");
 		if(StringUtil.isEmptyList(chbBotAstPositionRegistListOrg)){
 			//按条件查询全部
 			chbBotAstPositionRegistListOrg = chbAstValueRegist001Mapper.searchPageEx(map);
 		}
 		
 		List<ChbAstValueRegist001Dto> chbBotAstPositionRegistList  = new ArrayList<ChbAstValueRegist001Dto>();
        for(ChbAstValueRegist001Dto m : chbBotAstPositionRegistListOrg){
			// modify by zhangliang 20210309 手工处理与前台逻辑一致：只能选择报送状态为未报送、或报送状态为接口报送且反馈状态为发送失败和中债退回的数据进行手工处理 start
//            // 手工处理只处理未报送的
//            if (DictCodeListExt2.报送状态_未报送.getValue().equals(m.getSendStatus())){
//            	chbBotAstPositionRegistList.add(m);
//            }
			chbBotAstPositionRegistList.add(m);
			// modify by zhangliang 20210309 手工处理与前台逻辑一致：只能选择报送状态为未报送、或报送状态为接口报送且反馈状态为发送失败和中债退回的数据进行手工处理 end
        }
 		
 		if(!CollectionUtils.isEmpty(chbBotAstPositionRegistList)){
 			// 每批处理的数据条数
 			int partSize = 500;
 			// 存放业务数据的临时list，数据量不超过partSize
 			List<ChbAstValueRegist001Dto> tmpList = new ArrayList<ChbAstValueRegist001Dto>();
 			// 分批次处理
 			for (int j = 0; j < chbBotAstPositionRegistList.size(); j++) {
 				tmpList.add(chbBotAstPositionRegistList.get(j));
 				if (partSize == tmpList.size() || j == chbBotAstPositionRegistList.size() - 1) {
 					//找sub表有没有已报送的数据
 					List<String> regList = chbAstValueRegist001Mapper.selectRegistSeqnoListEx(tmpList,"02",CommonUtil.getInstitutionCodeByLoginUser_ms());
 					//更新sub表数据（中债通过、手工处理）
 					this.updateInterfSubmitAndSeqEx(DictCodeListExt2.报送状态_手工处理.getValue(), tmpList);
// 		        	if(!CollectionUtils.isEmpty(regList)){
// 						List<List<String>> regseqnolist = createStringList(regList,20);
// 						for(int i=0;i<regseqnolist.size();i++){
// 							//将已报送的数据移至his表 insert select 
// 							chbAstValSubHisMapper.insertSubHisBatch(regseqnolist.get(i));
// 							//将已报送的数据从sub表物理删除
// 							chbAstValueRegist001Mapper.deleteRegistSeqnoList(regseqnolist.get(i));
// 						}
// 					}
// 					tmpList.clear();
 				}
 			}
        	
        }
	}
	
	
	/**
	 * update by leipeng 20200701 资产多法人改造
	 * 更新登记编码信息方法-银行模式
	 * @param map
	 */
	public void updateSubStatis(Map<String, Object> map){
		dealSearchParam(map);
 		List<ChbAstValueRegist001Dto> chbBotAstPositionRegistListOrg  = new ArrayList<ChbAstValueRegist001Dto>();
 		chbBotAstPositionRegistListOrg = (List<ChbAstValueRegist001Dto>)map.get("astValueList");
 		if(StringUtil.isEmptyList(chbBotAstPositionRegistListOrg)){
 			//按条件查询全部
 			chbBotAstPositionRegistListOrg = chbAstValueRegist001Mapper.searchPage(map);
 		}
 		
 		List<ChbAstValueRegist001Dto> chbBotAstPositionRegistList  = new ArrayList<ChbAstValueRegist001Dto>();
        for(ChbAstValueRegist001Dto m : chbBotAstPositionRegistListOrg){
			// modify by zhangliang 20210309 手工处理与前台逻辑一致：只能选择报送状态为未报送、或报送状态为接口报送且反馈状态为发送失败和中债退回的数据进行手工处理 start
//            // 手工处理只处理未报送的
//            if (DictCodeListExt2.报送状态_未报送.getValue().equals(m.getSendStatus())){
//            	chbBotAstPositionRegistList.add(m);
//            }
			chbBotAstPositionRegistList.add(m);
			// modify by zhangliang 20210309 手工处理与前台逻辑一致：只能选择报送状态为未报送、或报送状态为接口报送且反馈状态为发送失败和中债退回的数据进行手工处理 end
        }
 		
 		if(!CollectionUtils.isEmpty(chbBotAstPositionRegistList)){
 			// 每批处理的数据条数
 			int partSize = 500;
 			// 存放业务数据的临时list，数据量不超过partSize
 			List<ChbAstValueRegist001Dto> tmpList = new ArrayList<ChbAstValueRegist001Dto>();
 			// 分批次处理
 			for (int j = 0; j < chbBotAstPositionRegistList.size(); j++) {
 				tmpList.add(chbBotAstPositionRegistList.get(j));
 				if (partSize == tmpList.size() || j == chbBotAstPositionRegistList.size() - 1) {
 					//找sub表有没有已报送的数据
 					List<String> regList = chbAstValueRegist001Mapper.selectRegistSeqnoList(tmpList,"02");
 					//更新sub表数据（中债通过、手工处理）
 					this.updateInterfSubmitAndSeq(DictCodeListExt2.报送状态_手工处理.getValue(), tmpList);
 		        	if(!CollectionUtils.isEmpty(regList)){
 						List<List<String>> regseqnolist = createStringList(regList,20);
 						for(int i=0;i<regseqnolist.size();i++){
 							//将已报送的数据移至his表 insert select 
 							chbAstValSubHisMapper.insertSubHisBatch(regseqnolist.get(i));
 							//将已报送的数据从sub表物理删除
 							chbAstValueRegist001Mapper.deleteRegistSeqnoList(regseqnolist.get(i));
 						}
 					}
 					tmpList.clear();
 				}
 			}
        	
        }
 	}
 	
 	
 	/**
 	 * 分批次执行updateInterfSubmitAndSeq
 	 *
 	 * @param investList
 	 *            数据
 	 * @see
 	 * @since 1.0
 	 */
 	@Transactional(rollbackFor = Exception.class)
 	public void updateInterfSubmitAndSeq(String sendStatus, List<ChbAstValueRegist001Dto> investList) {
 		if (StringUtil.isEmptyList(investList)) {
 			return;
 		}
 		int maxDataCountOneTime = reportSelectOrDeleteCountForShenFen;
 		int dataCount = investList.size();
 		if (dataCount <= maxDataCountOneTime) {
 			chbAstValueRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus, investList);
 			return;
 		}

 		// 对于批处理报送全量的数据，直接更新所有检查通过的未报送数据，状态修改为接口报送
// 		if (isSendAllData) {
// 		    logger.info("updateAllInterfSubmitAndSeqBatch全量更新登记表start===");
// 		    chbAstValueRegist001Mapper.updateAllInterfSubmitAndSeqBatch(sendStatus);
// 		    logger.info("updateAllInterfSubmitAndSeqBatch全量更新登记表end===");
 //
// 		    return;
// 		}

 		List<ChbAstValueRegist001Dto> newDataList = new ArrayList<ChbAstValueRegist001Dto>();
 		newDataList.addAll(investList);

 		// 分批处理
 		int part = dataCount / maxDataCountOneTime; // 分批数
 		for (int i = 0; i < part; i++) {
 			logger.info("单次更新登记表start===");
 			chbAstValueRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus,
                 newDataList.subList(0, maxDataCountOneTime));
 			newDataList.subList(0, maxDataCountOneTime).clear();
 			logger.info("单次更新登记表end===");
 		}
 		// 最后剩下的数据
 		if (CollectionUtils.isNotEmpty(newDataList)) {
 			logger.info("单次更新登记表start===");
 			chbAstValueRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus, newDataList);
 			logger.info("单次更新登记表end===");
 		}
 	}
 	
 	
 	
 	/**
 	 * 分批次执行updateInterfSubmitAndSeq --联社模式
 	 *update by leipeng 20200701 资产多法人改造
 	 * @param investList
 	 *            数据
 	 * @see
 	 * @since 1.0
 	 */
 	@Transactional(rollbackFor = Exception.class)
 	public void updateInterfSubmitAndSeqEx(String sendStatus, List<ChbAstValueRegist001Dto> investList) {
 		if (StringUtil.isEmptyList(investList)) {
 			return;
 		}
 		List<ChbAssetSubStatus> assetStatusList = new ArrayList<>();
 		for (ChbAstValueRegist001Dto chbAstValueRegist001Dto : investList) {
 			ChbAssetSubStatus status = new ChbAssetSubStatus();
 			
 			status.setAssetType("02");
 			status.setRegistSeqno(chbAstValueRegist001Dto.getRegistSeqno());
 			status.setInstitutionCode(CommonUtil.getInstitutionCodeByLoginUser_ms());
 			status.setSubmitFeedbackStatus(DictCodeListExt2.报送结果_审核通过.getValue());
			status.setSendStatus(DictCodeListExt2.报送状态_手工处理.getValue());
 			assetStatusList.add(status);
		}
 		
 		
 		int maxDataCountOneTime = reportSelectOrDeleteCountForShenFen;
 		int dataCount = investList.size();
 		if (dataCount <= maxDataCountOneTime) {
 			assetInfoRegist001Mapper.mergeSubStatusList(assetStatusList);
			chbAstValueRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus, investList);
 			return;
 		}

 		// 对于批处理报送全量的数据，直接更新所有检查通过的未报送数据，状态修改为接口报送
// 		if (isSendAllData) {
// 		    logger.info("updateAllInterfSubmitAndSeqBatch全量更新登记表start===");
// 		    chbAstValueRegist001Mapper.updateAllInterfSubmitAndSeqBatch(sendStatus);
// 		    logger.info("updateAllInterfSubmitAndSeqBatch全量更新登记表end===");
 //
// 		    return;
// 		}

 		List<ChbAstValueRegist001Dto> newDataList = new ArrayList<ChbAstValueRegist001Dto>();
 		newDataList.addAll(investList);

 		// 分批处理
 		int part = dataCount / maxDataCountOneTime; // 分批数
 		for (int i = 0; i < part; i++) {
 			logger.info("单次更新登记表start===");
 			assetInfoRegist001Mapper.mergeSubStatusList(assetStatusList.subList(0, maxDataCountOneTime));
			chbAstValueRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus,newDataList.subList(0, maxDataCountOneTime));
			newDataList.subList(0, maxDataCountOneTime).clear();
 			assetStatusList.subList(0, maxDataCountOneTime).clear();
 			logger.info("单次更新登记表end===");
 		}
 		// 最后剩下的数据
 		if (CollectionUtils.isNotEmpty(newDataList)) {
 			logger.info("单次更新登记表start===");
 			assetInfoRegist001Mapper.mergeSubStatusList(assetStatusList);
			chbAstValueRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus, newDataList);
 			logger.info("单次更新登记表end===");
 		}
 	}

 	
 	/**
 	 * 已手工处理的数据将报送状态改为未报送
 	 * update by leipeng 20200701  多法人数据改造
 	 * @param upKey
 	 *            传入主键
 	 * @see
 	 * @since 1.0
 	 */
 	@SuppressWarnings("unchecked")
 	@Transactional(rollbackFor = Exception.class)
 	public String cancelMannul(Map<String, Object> map) {
 		//update by leipeng 20200701 资产多法人改造
 		String flag = "fail";
		if(CommonUtil.isBAmodel()){
			map.put("orgCode", CommonUtil.getTreeInstCode());
			flag = cancelManulEx(map);
		}else{
			flag = cancelManulNormal(map);
		}
		return flag;
 	}
 	
 	
 	/**
	 * 取消手工处理 - 联社版
	 * update by leipeng 20200701  多法人数据改造
	 * @param map
	 * @return
	 */
	public String cancelManulEx(Map<String, Object> map){
		dealSearchParam(map);
 		List<ChbAstValueRegist001Dto> list = new ArrayList<ChbAstValueRegist001Dto>();
 		if(map.get("flag") != null && "01".equals(map.get("flag"))){
 			list = chbAstValueRegist001Mapper.searchPageEx(map);
 		}else{
 			list = (List<ChbAstValueRegist001Dto>) map.get("astValueList");
 		}
 		
 	// 报送状态为接口报送且反馈状态为发送失败和中债退回的数据 该部分数据的报送流水号列表
        List<ChbAssetSubStatus> registSeqList = new ArrayList<ChbAssetSubStatus>();
//        List<String> assetList = new ArrayList<>();
		
 		
 		String  orgCode = CommonUtil.getInstitutionCodeByLoginUser_ms();
 		if(!CollectionUtils.isEmpty(list)){ 			
        	//找sub表有没有未报送的数据
			List<String> unregList = chbAstValueRegist001Mapper.selectRegistSeqnoListEx(list,"01",orgCode);
			if(!CollectionUtils.isEmpty(unregList)){
				return "fail";
			}
			
			for(ChbAstValueRegist001Dto m : list){
				ChbAssetSubStatus status = new ChbAssetSubStatus();
				status.setAssetType("02");
				status.setRegistSeqno(m.getRegistSeqno());
				status.setInstitutionCode(CommonUtil.getInstitutionCodeByLoginUser_ms());
				
				registSeqList.add(status);     
			}
			//删除删除登记数据
            assetInfoRegist001Mapper.deleteSubStausEx(registSeqList);
			chbAstValueRegist001Mapper.cancelMannultRegist(list);
//        	//查询his表，取最新的已报送数据
// 			List<String> seqnolist = chbAstValSubHisMapper.selectByprodId(list);
// 			chbAstValueRegist001Mapper.cancelMannultRegist(list);
//        	if(!CollectionUtils.isEmpty(seqnolist)){
//				List<List<String>> regList = createStringList(seqnolist, 20);
//				for(int i=0;i<regList.size();i++){
//					//将his表数据移至sub表
//					chbAstValueRegist001Mapper.insertSeletForSub(regList.get(i));
//					//his表数据物理删除
//					chbAstValSubHisMapper.deleteBySeqno(regList.get(i));
//				}
//			}
        }
 		return "success";
	}
	
	
	/**
	 * 取消手工处理 - 银行版
	 * update by leipeng 20200701  多法人数据改造
	 * @param map
	 * @return
	 */
	public String cancelManulNormal(Map<String, Object> map){
		dealSearchParam(map);
 		List<ChbAstValueRegist001Dto> list = new ArrayList<ChbAstValueRegist001Dto>();
 		if(map.get("flag") != null && "01".equals(map.get("flag"))){
 			list = chbAstValueRegist001Mapper.searchPage(map);
 		}else{
 			list = (List<ChbAstValueRegist001Dto>) map.get("astValueList");
 		}
 		if(!CollectionUtils.isEmpty(list)){
        	//找sub表有没有未报送的数据
			List<String> unregList = chbAstValueRegist001Mapper.selectRegistSeqnoList(list,"01");
			if(!CollectionUtils.isEmpty(unregList)){
				return "fail";
			}
        	//查询his表，取最新的已报送数据
 			List<String> seqnolist = chbAstValSubHisMapper.selectByprodId(list);
 			chbAstValueRegist001Mapper.cancelMannultRegist(list);
        	if(!CollectionUtils.isEmpty(seqnolist)){
				List<List<String>> regList = createStringList(seqnolist, 20);
				for(int i=0;i<regList.size();i++){
					//将his表数据移至sub表
					chbAstValueRegist001Mapper.insertSeletForSub(regList.get(i));
					//his表数据物理删除
					chbAstValSubHisMapper.deleteBySeqno(regList.get(i));
				}
			}
        }
 		return "success";
 	}

 	private List<List<String>> createStringList(List<String> targe,int size) {
		List<List<String>> listArr = new ArrayList<List<String>>();
		//获取被拆分的数组个数
		int arrSize = targe.size()%size==0?targe.size()/size:targe.size()/size+1;
		for(int i=0;i<arrSize;i++) {
			List<String> sub = new ArrayList<String>();
			//把指定索引数据放入到list中
			for(int j = i*size;j <= size*(i+1)-1;j++) {
				if(j <= targe.size()-1) {
					sub.add(targe.get(j));
				}
			}
			listArr.add(sub);
		}
		return listArr;
	}

	/**start    中债一二期报送直连  add by    wangzehui  20201012 */
	public Map<String,Object> submitBeforeCheck(Map<String, Object> param) {
		List<ExcelValidDto> result=new ArrayList<ExcelValidDto>();
		List<ChbAstValueRegist001Dto> list=new ArrayList<>();
		Map<String,Object> returnMap = new HashMap<>();
		// 选择行数据时
		if (param.containsKey("keyList")) {
			String keyList = param.get("keyList").toString();
			try {
				list = JsonUtils.json2List(keyList,
						ChbAstValueRegist001Dto.class);
			}
			catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}else{
			// modify by zhangliang 20210315 直连报送全量查询 start
			dealSearchParam(param);
//			String issuePartyId = CommonUtil.selectBankCode();
			String issuePartyId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
			if(CommonUtil.isBAmodel()){
				list = chbAstValueRegist001Mapper.searchPageEx(param);
				list.forEach(item -> {
					if (StringUtil.isEmpty(item.getIssuePartyId())){
						item.setIssuePartyId(issuePartyId);
					}
				});

			}else{
				list = chbAstValueRegist001Mapper.searchPage(param);
			}
			dealResultlist(list);
			// modify by zhangliang 20210315 直连报送全量查询 end
		}
		if(CollectionUtils.isNotEmpty(list)){
			List<String> keys = list.stream().map(string -> string.getRegistSeqno()).collect(Collectors.toList());
			List<CheckReport001Dto> resultlist  = check(null,keys);
			Map<String,CheckReport001Dto> checkmap = resultlist.stream().collect(Collectors.toMap(CheckReport001Dto::getRefseqno,(p)->p,(key1, key2)->key1));
			int i=result.size();
			for(ChbAstValueRegist001Dto chbAstValueRegist001Dto : list){
				ExcelValidDto excelValidDto = new ExcelValidDto();
				String validKey ="行内资产负债编码："+chbAstValueRegist001Dto.getAssetCode()+ GlobalConstant.COMMA
						+"数据日期："+ DateUtil.format(chbAstValueRegist001Dto.getStatusDate())+ GlobalConstant.COMMA;
				excelValidDto.setValidKey(validKey);
				excelValidDto.setRowNum(String.valueOf(i));
				excelValidDto.setDataTime(chbAstValueRegist001Dto.getUpdateTime());
				excelValidDto.setIsLogicCheck(DictCodeList.是否_是.getValue());
				excelValidDto.setOrgCode(chbAstValueRegist001Dto.getIssuePartyId());
				excelValidDto.setCreateDept(chbAstValueRegist001Dto.getCreateDept());
				excelValidDto.setCheckResult("02");
				if(checkmap.get(chbAstValueRegist001Dto.getRegistSeqno())!=null){
					excelValidDto.setCheckResult(checkmap.get(chbAstValueRegist001Dto.getRegistSeqno()).getCheckResult());
					excelValidDto.setErrorInfo(checkmap.get(chbAstValueRegist001Dto.getRegistSeqno()).getErrorInfo());
					excelValidDto.setWarnInfo(checkmap.get(chbAstValueRegist001Dto.getRegistSeqno()).getWarnInfo());
					result.add(excelValidDto);
				}else{
					excelValidDto.setCheckResult("01");
					result.add(excelValidDto);
				}
				i++;
			}
		}
		else{
			result.add(new ExcelValidDto(StringUtil.EMPTY, String.valueOf(0), "无报送数据", null));
		}

		returnMap.put("excelValidDtoList",result);
		returnMap.put("resultList",list);
		return  returnMap;
	}

	/**
	 * @Description 检查登记表中小于今天所有未报送的数据
	 * @return
	 * @return List<CheckReport001Dto>
	 * @version 1.7 2019年6月24日
	 */
	public List<CheckReport001Dto> check(Date batchDate,List<String> keys){
		Date checkdate = DateUtil.parse(DateUtil.getSysDate());
		// 供跑批使用
		if (StringUtil.isNotEmpty(batchDate)){
			checkdate = batchDate;
		}
		// 数据日期为系统时间
		// 删除今天所有已检查的数据
		checkReport001Mapper.delReportDatasByCodeAndDate(DictCodeListExt2.报表名称_资产估值信息登记.getValue(),checkdate,keys);
		//增加例外情况过滤
		List<ChbCheckRuleout> listRuleout=chbCheckRuleoutMapper.selectDataByCheckTypeAndReprotCode(CheckConfConstants.CHB_CHECK_TYPE_B, CheckConfConstants.CHB_AST_VAL_SUB);
		Map<String, List<ChbCheckRuleout>> mapRuleout = listRuleout.stream().collect(
				JoyinCollectors.groupingBy(x -> x.getAssetCode()));

		//检查数据
		//报送前强制的错误检查
		List<CheckReport001Dto> resultList = beforeSubErrorCheck.checkAstValueSub(checkdate,mapRuleout,keys);

		//报错检查异常数据
		int batchCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"100" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));// 每批commit的个数
		int batchLastIndex = batchCount;// 每批最后一个的下标
		for (int index = 0; index < resultList.size();) {
			if (batchLastIndex > resultList.size() - 1) {
				batchLastIndex = resultList.size();
				checkReport001Mapper.batchInsert(resultList.subList(index, batchLastIndex));
				break;// 数据删除完毕,退出循环
			} else {
				checkReport001Mapper.batchInsert(resultList.subList(index, batchLastIndex));
				index = batchLastIndex;// 设置下一批下标
				batchLastIndex = index + batchCount;
			}
		}
		return resultList;
	}
	/**end    中债一二期报送直连  add by    wangzehui  20201012 */
}

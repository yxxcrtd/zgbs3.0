/*
 * 文件名：InvestorsHoldInfoRegist001Service.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：zuoyinyi
 * 修改时间：2018年5月8日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.tams.svreport.chb.assetinforeg.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.dao.BaseMapper;
import com.joyintech.tams.common.svreport.chb.constant.CheckConfConstants;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.datacheck.beforeinsert.util.SvrMaptoEntityUtil;
import com.joyintech.tams.common.svreport.datacheck.dao.ChbCheckRuleoutMapper;
import com.joyintech.tams.common.svreport.datacheck.dto.ChbCheckRuleout;
import com.joyintech.tams.report.common.dto.ExcelValidDto;
import com.joyintech.tams.report.submitmanagement.checkreport.dao.CheckReport001Mapper;
import com.joyintech.tams.report.submitmanagement.checkreport.dto.CheckReport001Dto;
import com.joyintech.tams.svreport.chb.datacheck.beforesub.service.BeforeSubCheck;
import com.joyintech.tams.svreport.chb.datacheck.beforesub.service.BeforeSubErrorCheck;
import com.joyintech.tams.svreport.chb.proddurationreg.dto.ProdDurationReg001Dto;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.ChbAssetSubStatusMapper;
import com.joyintech.tams.common.base.entity.ChbAssetSubStatus;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.report.common.constant.SvrServiceApplication;
import com.joyintech.tams.svreport.chb.assetinforeg.dao.AssetInfoRegist001Mapper;
import com.joyintech.tams.svreport.chb.assetinforeg.dao.ChbAssetInfoSubHisMapper;
import com.joyintech.tams.svreport.chb.assetinforeg.dto.AssetInfoRegist001Dto;
import com.joyintech.tams.svreport.chb.assetinforeg.dto.ChbAssetInfoSubHisDto;
import com.joyintech.tams.svreport.chb.investtrade.dao.MstCounterPartyDtoMapper;
import com.joyintech.tams.systemtools.util.CommonUtil;
/**
 * 资产要素登记service
 * @author weiyuqiang
 * @version 1.6.0 2019年1月21日
 * @see AssetInfoRegist001Service
 * @since 1.6.0
 */
@Service(value="svrAssetInfoRegist001Service")
@Transactional(rollbackFor = Exception.class)
public class AssetInfoRegist001Service extends CommonExportService<AssetInfoRegist001Dto> {

	/**
	 * 资产要素登记mapper
	 */
	@Autowired
	private AssetInfoRegist001Mapper assetInfoRegist001Mapper;
    
	/**
     * 资产要素履历mapper
     */
    @Autowired
    private ChbAssetInfoSubHisMapper chbAssetInfoSubHisMapper;
    /**
     * 交易对手mapper
     */
    @Autowired
    private MstCounterPartyDtoMapper mstCounterPartyDtoMapper;
    
    /**
     * 多法人模式保存状态字段
     *  */
    @Autowired
    private ChbAssetSubStatusMapper assetSubStatusMapper;
	@Autowired
    private CheckReport001Mapper checkReport001Mapper;
    @Autowired
    private ChbCheckRuleoutMapper chbCheckRuleoutMapper;
    @Autowired
    private BeforeSubErrorCheck beforeSubErrorCheck;
    @Autowired
    private BeforeSubCheck beforeSubCheck;

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 短sql最大一次最大插入条数
     */
    @Value("${application.reportInsertCount}")
    public  int reportInsertCount;
    
    /**
     * 短sql最大一次最大插入条数
     */
    @Value("${application.reportSelectOrDeleteCountForShenFen}")
    public  int reportSelectOrDeleteCountForShenFen;

    /**
     * sqlldr用数据库连接
     */
    @Value("${spring.datasource.reportSqlldrDatabaseName}")
    private String reportSqlldrDatabaseName;
    
    @Autowired
    private SqlSessionTemplate template;

    /**
     * 数据批量一次性insert条数(单条数据字段较多)
     */
    @Value("${application.reportInsertLongCount}")
    private int reportInsertLongCount;

    /**
     * 数据批量一次查询或者删除条数
     */
    @Value("${application.reportSelectOrDeleteCount}")
    private int reportSelectOrDeleteCount;

	@Autowired
	private SvrMaptoEntityUtil svrMaptoEntityUtil;
	private String tableName = "CHB_ASSET_INFO_SUB";

	/**
	 * 中债基础数据mapper
	 */
	@Autowired
	private BaseMapper baseMapper;
	/**
	 * 查询页面数据
	 *
	 * @param pageParam
	 *            查询条件
	 * @return 结果列表
	 * @see
	 * @since 1.6.0
	 */
	public Page<AssetInfoRegist001Dto> searchForPage(Map<String, Object> pageParam) {
		dealSearchParam(pageParam);
        Page<AssetInfoRegist001Dto> page;
		//update byleipeng  【资产要素登记】公共资产，发行机构代码根据登录人维护 机构登记银行代码管理 登记银行代码

//        String issuePartyId = CommonUtil.selectBankCode();
		String issuePartyId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        if (pageParam.get(GlobalConstant.PARAM_PAGE) != null
                && pageParam.get(GlobalConstant.PARAM_LENGTH) != null){
            int pagenum = Integer.parseInt(pageParam.get(GlobalConstant.PARAM_PAGE).toString());
            int legth = Integer.parseInt(pageParam.get(GlobalConstant.PARAM_LENGTH).toString());
            //update by leipeng 20191123
            List<String> assetTypeList = new ArrayList<>();
            
            if(null!=pageParam.get("assetType")&&StringUtil.isNotEmpty(pageParam.get("assetType"))){
            	if(pageParam.get("assetType").equals(DictCodeListExt3.资产_负债类别_公募和私募基金.getValue())){
            		assetTypeList.add(DictCodeListExt2.资产_负债类别_公募基金.getValue());
            		assetTypeList.add(DictCodeListExt2.资产_负债类别_私募基金.getValue());
            	}else{
            		assetTypeList.add(pageParam.get("assetType").toString());
            	}
            }
            pageParam.put("assetTypeList", assetTypeList);
			//通道投资资产管理产品是否穿透报送
			String isChlBtmStr = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
			pageParam.put("isChlBtmStr",isChlBtmStr);
            
            //add by leipeng 20200628
    		if(CommonUtil.isBAmodel()){
    			page = queryForPage(
                        pagenum,legth,()->assetInfoRegist001Mapper.selectAssetInfoByPageEx(pageParam));
    			//update byleipeng  【资产要素登记】公共资产，发行机构代码根据登录人维护 机构登记银行代码管理 登记银行代码

    			page.getRoot().forEach(item -> {
    				if (StringUtil.isEmpty(item.getIssuePartyId())){
    					item.setIssuePartyId(issuePartyId);
    				}
    			});
    			
    		}else{
    			page = queryForPage(
                        pagenum,legth,()->assetInfoRegist001Mapper.selectAssetInfoByPage(pageParam));
    		}
            
        } else {
        	
        	if(CommonUtil.isBAmodel()){
            	page = queryForPage(AssetInfoRegist001Mapper.class,"selectAssetInfoByPageEx",pageParam);
    			//update byleipeng  【资产要素登记】公共资产，发行机构代码根据登录人维护 机构登记银行代码管理 登记银行代码

            	page.getRoot().forEach(item -> {
    				if (StringUtil.isEmpty(item.getIssuePartyId())){
    					item.setIssuePartyId(issuePartyId);
    				}
    			});
    		}else{
            	page = queryForPage(AssetInfoRegist001Mapper.class,"selectAssetInfoByPage",pageParam);

    		}
            
        }
        dealDataList(page.getRoot(), false);
        dealDataListExt(page.getRoot(), false);
        return page;
	}

	/**
	 * 处理查询结果 <功能详细描述>
	 *
	 * @param list
	 *            待处理数据
	 * @param isExport
	 *            是否是导出
	 * @see
	 * @since 1.0
	 */
	public void dealDataList(List<AssetInfoRegist001Dto> list, boolean isExport) {
		list.forEach(item -> {
			// 此处处理查询后结果的枚举值对应关系
			if (item.getAssetYield() != null){
				item.setAssetYield(item.getAssetYield().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setAssetYield(item.getAssetYield().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getFloatFactor() != null){
				item.setFloatFactor(item.getFloatFactor().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setFloatFactor(item.getFloatFactor().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getTotalFeeRate() != null){
				item.setTotalFeeRate(item.getTotalFeeRate().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setTotalFeeRate(item.getTotalFeeRate().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getIncomeRatio() != null){
				item.setIncomeRatio(item.getIncomeRatio().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setIncomeRatio(item.getIncomeRatio().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getDerInstrRatio() != null){
				item.setDerInstrRatio(item.getDerInstrRatio().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setDerInstrRatio(item.getDerInstrRatio().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getMaxYield() != null){
				item.setMaxYield(item.getMaxYield().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setMaxYield(item.getMaxYield().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getMinYield() != null){
				item.setMinYield(item.getMinYield().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setMinYield(item.getMinYield().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getMaxRate() != null){
				item.setMaxRate(item.getMaxRate().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setMaxRate(item.getMaxRate().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getMinRate() != null){
				item.setMinRate(item.getMinRate().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setMinRate(item.getMinRate().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getTruFeeRate() != null){
				item.setTruFeeRate(item.getTruFeeRate().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setTruFeeRate(item.getTruFeeRate().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getTradeFeeRate() != null){
				item.setTradeFeeRate(item.getTradeFeeRate().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setTradeFeeRate(item.getTradeFeeRate().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getMgrFeeRate() != null){
				item.setMgrFeeRate(item.getMgrFeeRate().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setMgrFeeRate(item.getMgrFeeRate().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getAgencyFeeRate() != null){
				item.setAgencyFeeRate(item.getAgencyFeeRate().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setAgencyFeeRate(item.getAgencyFeeRate().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getOtherFeeRate() != null){
				item.setOtherFeeRate(item.getOtherFeeRate().setScale(5, BigDecimal.ROUND_HALF_UP));
//				item.setOtherFeeRate(item.getOtherFeeRate().multiply(new BigDecimal(100)).setScale(5, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getAmt() != null){
				item.setAmt(item.getAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getTargetAmt() != null){
				item.setTargetAmt(item.getTargetAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			if (item.getRemainTerm() != null){
				item.setRemainTerm(item.getRemainTerm().setScale(2, BigDecimal.ROUND_HALF_UP));
			}
            if (item.getCcy() != null){
                item.setCcyName(item.getCcy());
            }
            if (StringUtil.isNotEmpty(item.getProProperty())){
            	if("16".equals(item.getAssetTpye())) {//委外资产使用新的字典枚举，否则用老的
        			item.setProPropertyName1(item.getProProperty()+" "+SvrDataUtil.getNameFromDataDict(DictCode.委外投资属性_中债,item.getProProperty()));
        			item.setProProperty1(item.getProPropertyName1());
            	} else {
            		item.setProPropertyName(item.getProProperty()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资管计划属性,item.getProProperty()));
    				item.setProProperty(item.getProPropertyName());
            	}
			}
			if(DictCodeListExt2.资产_负债类别_债券类资产_理财直接融资工具_同业存单.getValue().equals(item.getAssetType())){
            	//债券类资产，“代码”列不应展示后缀
            	if(StringUtil.isNotEmpty(item.getAssetId())){
            		String assetId=item.getAssetId();
            		if(assetId.contains(".")){
            			assetId=assetId.substring(0,assetId.indexOf("."));
            			item.setAssetId(assetId);
            		}
            	}
            }
            if (item.getAssetType() != null && isExport){
                // 用于双击对比
    			item.setAssetTypeName(SvrDataUtil.getNameFromDataDict(DictCode.资产_负债类别,item.getAssetTpye()));
    			item.setAssetType(item.getAssetTypeName());
            }
			if (StringUtil.isNotEmpty(item.getTradeMarket())){
				item.setTradeMarketName(item.getTradeMarket()+" "+SvrDataUtil.getNameFromDataDict(DictCode.交易流通场所_中债,item.getTradeMarket()));
				item.setTradeMarket(item.getTradeMarketName());
			}
			if (StringUtil.isNotEmpty(item.getAssetType2())){
				item.setAssetType2Name(item.getAssetType2()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资产_负债二级类别,item.getAssetType2()));
				item.setAssetType2(item.getAssetType2Name());
			}
			if (StringUtil.isNotEmpty(item.getBasis())){
				item.setBasisName(item.getBasis()+" "+SvrDataUtil.getNameFromDataDict(DictCode.计息基础_中债,item.getBasis()));
				item.setBasis(item.getBasisName());
			}
			if (StringUtil.isNotEmpty(item.getIsStruct())){
				item.setIsStructName(item.getIsStruct()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是否_中债,item.getIsStruct()));
				item.setIsStruct(item.getIsStructName());
			}
			if (StringUtil.isNotEmpty(item.getTargetType())){
				if(item.getTargetType().contains(GlobalConstant.COMMA)) {
					List<String> targetTypeList = StringUtil.splitToList(GlobalConstant.COMMA, item.getTargetType());
					if (!StringUtil.isEmptyList(targetTypeList)){
						List<String> targetTypeNameList = new ArrayList<String>();
						for(String targetType : targetTypeList) {
							targetTypeNameList.add(targetType+" "+SvrDataUtil.getNameFromDataDict(DictCode.标的类别,targetType));
						}
						String targetTypeName = StringUtil.joinString(targetTypeNameList, GlobalConstant.COMMA);
						item.setTargetType(targetTypeName);
						item.setTargetTypeName(targetTypeName);
					}
				}else {
					item.setTargetTypeName(item.getTargetType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.标的类别,item.getTargetType()));
					item.setTargetType(item.getTargetTypeName());
				}
			}
			if (StringUtil.isNotEmpty(item.getChbSectype())){
				item.setChbSectypeName(item.getChbSectype()+" "+SvrDataUtil.getNameFromDataDict(DictCode.具体类别,item.getChbSectype()));
				item.setChbSectype(item.getChbSectypeName());
			}
			if (StringUtil.isNotEmpty(item.getIssueMode())){
				item.setIssueModeName(item.getIssueMode()+" "+SvrDataUtil.getNameFromDataDict(DictCode.债券类资产发行方式,item.getIssueMode()));
				item.setIssueMode(item.getIssueModeName());
			}
//			// 债项评级
//			if (!StringUtil.isNoneBlank(item.getSecCreditGradeType())){
//				if (SVReportDictCodeList.债项评级_短期.getDictCode().equals(item.getSecCreditGradeType())){
//					item.setSecCreditGradeName(SvrDataUtil.getNameFromDataDict(SVReportDictCode.资产评级短期,item.getSecCreditGrade()));
//				} else{
//					item.setSecCreditGradeName(SvrDataUtil.getNameFromDataDict(SVReportDictCode.资产评级,item.getSecCreditGrade()));
//				}
//			}
//			// 主体评级（信用评级在get方法中实现）
//			item.setSecMainGradeName(SvrDataUtil.getNameFromDataDict(SVReportDictCode.资产评级,item.getSecMainGrade()));
			if (StringUtil.isNotEmpty(item.getSecMainGrade())){
				item.setSecMainGradeName(item.getSecMainGrade()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资产评级,item.getSecMainGrade()));
				item.setSecMainGrade(item.getSecMainGradeName());
			}
			if (StringUtil.isNotEmpty(item.getSecCreditGrade())){
				item.setSecCreditGradeName(item.getSecCreditGrade()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资产评级,item.getSecCreditGrade()));
				item.setSecCreditGrade(item.getSecCreditGradeName());
			}
//			if (StringUtil.isNotEmpty(item.getSecCreditGradeType())){
//				item.setSecCreditGradeTypeName(item.getSecCreditGradeType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资产评级,item.getSecCreditGradeType()));
//				item.setSecCreditGradeType(item.getSecCreditGradeTypeName());
//			}
			if (StringUtil.isNotEmpty(item.getSecCreditGradeType())){
				item.setSecCreditGradeTypeName(item.getSecCreditGradeType());
			}
			if (StringUtil.isNotEmpty(item.getPartyType1())){
				item.setPartyType1Name(item.getPartyType1()+" "+SvrDataUtil.getNameFromDataDict(DictCode.发行机构类型_按规模划分,item.getPartyType1()));
				item.setPartyType1(item.getPartyType1Name());
			}
			if (StringUtil.isNotEmpty(item.getPartyType3())){
				item.setPartyType3Name(item.getPartyType3()+" "+SvrDataUtil.getNameFromDataDict(DictCode.发行机构类型_按技术领域划分,item.getPartyType3()));
				item.setPartyType3(item.getPartyType3Name());
			}
			if (StringUtil.isNotEmpty(item.getPartyType2())){
				item.setPartyType2Name(item.getPartyType2()+" "+SvrDataUtil.getNameFromDataDict(DictCode.发行机构类型_按经济类型划分,item.getPartyType2()));
				item.setPartyType2(item.getPartyType2Name());
			}
			if (StringUtil.isNotEmpty(item.getIndustry())){
			    item.setIndustryName(item.getIndustry()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业一级分类,item.getIndustry()));
				item.setIndustry(item.getIndustryName());
			}
			if (StringUtil.isNotEmpty(item.getIndustryLevel2())){
				item.setIndustryLevel2Name(item.getIndustryLevel2()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业二级分类,item.getIndustryLevel2()));
				item.setIndustryLevel2(item.getIndustryLevel2Name());
			}
			item.setIndustryName(item.getIndustryLevel2Name());
			if (StringUtil.isNotEmpty(item.getRegistOrg())){
				item.setRegistOrgName(item.getRegistOrg()+" "+SvrDataUtil.getNameFromDataDict(DictCode.登记托管机构_中债,item.getRegistOrg()));
				item.setRegistOrg(item.getRegistOrgName());
			}
			if (StringUtil.isNotEmpty(item.getTargetType1())){
				item.setTargetType1Name(item.getTargetType1()+" "+SvrDataUtil.getNameFromDataDict(DictCode.回购标的类别,item.getTargetType1()));
				item.setTargetType1(item.getTargetType1Name());
			}
			if (StringUtil.isNotEmpty(item.getProfitType())){
				item.setProfitTypeName(item.getProfitType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.收受益权类型,item.getProfitType()));
				item.setProfitType(item.getProfitTypeName());
			}
			if (StringUtil.isNotEmpty(item.getIsResale())){
				item.setIsResaleName(item.getIsResale()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是否_中债,item.getIsResale()));
				item.setIsResale(item.getIsResaleName());
			}
			if (StringUtil.isNotEmpty(item.getRateFlag())){
				item.setRateFlagName(item.getRateFlag()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是否_中债,item.getRateFlag()));
				item.setRateFlag(item.getRateFlagName());
			}
			if (StringUtil.isNotEmpty(item.getBasisType())){
				item.setBasisTypeName(item.getBasisType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.基准利率,item.getBasisType()));
				item.setBasisType(item.getBasisTypeName());
			}
//			if (StringUtil.isNotEmpty(item.getIndustry1())){
//				item.setIndustry1Name(item.getIndustry1()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业一级分类,item.getIndustry1()));
//                item.setIndustry1(item.getIndustry1Name());
//			}
			if (StringUtil.isNotEmpty(item.getIndustryLevel2Type())){
				item.setIndustryLevel2TypeName(item.getIndustryLevel2Type()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业二级分类,item.getIndustryLevel2Type()));
				item.setIndustryLevel2Type(item.getIndustryLevel2TypeName());
	            item.setIndustry1Name(item.getIndustryLevel2TypeName());
	            item.setIndustry1(item.getIndustryLevel2TypeName());
			}
//			if (StringUtil.isNotEmpty(item.getProIndustry())){
//				item.setProIndustryName(item.getProIndustry()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业一级分类,item.getProIndustry()));
//                item.setProIndustry(item.getProIndustryName());
//			}
			if (StringUtil.isNotEmpty(item.getProIndustryLevel2())){
				item.setProIndustryLevel2Name(item.getProIndustryLevel2()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业二级分类,item.getProIndustryLevel2()));
				item.setProIndustryLevel2(item.getProIndustryLevel2Name());
	            item.setProIndustryName(item.getProIndustryLevel2Name());
	            item.setProIndustry(item.getProIndustryLevel2Name());
			}
			if (StringUtil.isNotEmpty(item.getIndustryFlag())){
				item.setIndustryFlagName(item.getIndustryFlag()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是否_中债,item.getIndustryFlag()));
				item.setIndustryFlag(item.getIndustryFlagName());
			}
			if (StringUtil.isNotEmpty(item.getMonitType())){
				item.setMonitTypeName(item.getMonitType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.重点监控行业和领域类别,item.getMonitType()));
				item.setMonitType(item.getMonitTypeName());
			}
			if (StringUtil.isNotEmpty(item.getGuaType())){
				item.setGuaTypeName(item.getGuaType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.担保方式,item.getGuaType()));
				item.setGuaType(item.getGuaTypeName());
			}
			if (StringUtil.isNotEmpty(item.getFinancierGrade())){
				item.setFinancierGradeName(item.getFinancierGrade()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资产评级,item.getFinancierGrade()));
				item.setFinancierGrade(item.getFinancierGradeName());
			}
			if (StringUtil.isNotEmpty(item.getInGrade())){
				item.setInGradeName(item.getInGrade());
//				item.setInGrade(item.getInGradeName());
			}
			if (StringUtil.isNotEmpty(item.getOutGrade())){
				item.setOutGradeName(item.getOutGrade()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资产评级,item.getOutGrade()));
				item.setOutGrade(item.getOutGradeName());
			}
//			item.setFinancierGradeName(SvrDataUtil.getNameFromDataDict(SVReportDictCode.资产评级,item.getFinancierGrade()));
//			item.setInGradeName(SvrDataUtil.getNameFromDataDict(SVReportDictCode.资产评级,item.getInGrade()));
//			item.setOutGradeName(SvrDataUtil.getNameFromDataDict(SVReportDictCode.资产评级,item.getOutGrade()));
			/*if (StringUtil.isNotEmpty(item.getFinArea())){
				item.setFinAreaName(item.getFinArea()+" "+SvrDataUtil.getNameFromDataDict(DictCode.行政区划_市,item.getFinArea()));
				item.setFinArea(item.getFinAreaName());
			}*/
			if (StringUtil.isNotEmpty(item.getFinArea())){
				if(StringUtil.isNotEmpty(SvrDataUtil.getNameFromDataDict(DictCode.行政区划_省市县,item.getFinArea()))){
					item.setFinAreaName(item.getFinArea()+" "+SvrDataUtil.getNameFromDataDict(DictCode.行政区划_省市县,item.getFinArea()));
					item.setFinArea(item.getFinAreaName());
				}
				else if(StringUtil.isNotEmpty(SvrDataUtil.getNameFromDataDict(DictCode.行政区划_省,item.getFinArea()))){
					item.setFinAreaName(item.getFinArea()+" "+SvrDataUtil.getNameFromDataDict(DictCode.行政区划_省,item.getFinArea()));
					item.setFinArea(item.getFinAreaName());
				}else if(StringUtil.isNotEmpty(SvrDataUtil.getNameFromDataDict(DictCode.行政区划_市,item.getFinArea()))){
					item.setFinAreaName(item.getFinArea()+" "+SvrDataUtil.getNameFromDataDict(DictCode.行政区划_市,item.getFinArea()));
					item.setFinArea(item.getFinAreaName());
				}else if(StringUtil.isNotEmpty(SvrDataUtil.getNameFromDataDict(DictCode.行政区划_县,item.getFinArea()))){
					item.setFinAreaName(item.getFinArea()+" "+SvrDataUtil.getNameFromDataDict(DictCode.行政区划_县,item.getFinArea()));
					item.setFinArea(item.getFinAreaName());
				}
			}
			if (StringUtil.isNotEmpty(item.getIsRight())){
				item.setIsRightName(item.getIsRight()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是否_中债,item.getIsRight()));
				item.setIsRight(item.getIsRightName());
			}
			if (StringUtil.isNotEmpty(item.getNotstandSecType())){
				item.setNotstandSecTypeName(item.getNotstandSecType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.票据类信用证类资产类型,item.getNotstandSecType()));
				item.setNotstandSecType(item.getNotstandSecTypeName());
			}
			if (StringUtil.isNotEmpty(item.getIndustryShares())){
				item.setIndustrySharesName(item.getIndustryShares()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业一级分类,item.getIndustryShares()));
                item.setIndustryShares(item.getIndustrySharesName());
			}
			if (StringUtil.isNotEmpty(item.getIndustryShares2())){
				item.setIndustryShares2Name(item.getIndustryShares2()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业二级分类,item.getIndustryShares2()));
				item.setIndustryShares2(item.getIndustryShares2Name());
			}
			item.setIndustrySharesName(item.getIndustryShares2Name());
			if (StringUtil.isNotEmpty(item.getSharesType())){
				item.setSharesTypeName(item.getSharesType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.股票类型,item.getSharesType()));
				item.setSharesType(item.getSharesTypeName());
			}
			if (StringUtil.isNotEmpty(item.getInvestStage())){
				item.setInvestStageName(item.getInvestStage()+" "+SvrDataUtil.getNameFromDataDict(DictCode.投资阶段,item.getInvestStage()));
				item.setInvestStage(item.getInvestStageName());
			}
			if (StringUtil.isNotEmpty(item.getSharesType1())){
				item.setSharesType1Name(item.getSharesType1()+" "+SvrDataUtil.getNameFromDataDict(DictCode.发行机构类型_按规模划分,item.getSharesType1()));
				item.setSharesType1(item.getSharesType1Name());
			}
			if (StringUtil.isNotEmpty(item.getSharesType3())){
				item.setSharesType3Name(item.getSharesType3()+" "+SvrDataUtil.getNameFromDataDict(DictCode.发行机构类型_按技术领域划分,item.getSharesType3()));
				item.setSharesType3(item.getSharesType3Name());
			}
			if (StringUtil.isNotEmpty(item.getSharesType2())){
				item.setSharesType2Name(item.getSharesType2()+" "+SvrDataUtil.getNameFromDataDict(DictCode.发行机构类型_按经济类型划分,item.getSharesType2()));
				item.setSharesType2(item.getSharesType2Name());
			}
			if (StringUtil.isNotEmpty(item.getIsPledge())){
				item.setIsPledgeName(item.getIsPledge()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是否_中债,item.getIsPledge()));
				item.setIsPledge(item.getIsPledgeName());
			}
			if (StringUtil.isNotEmpty(item.getTargetType3())){
				if(item.getTargetType3().contains(GlobalConstant.COMMA)) {
					List<String> targetTypeList = StringUtil.splitToList(GlobalConstant.COMMA, item.getTargetType3());
					if (!StringUtil.isEmptyList(targetTypeList)){
						List<String> targetTypeNameList = new ArrayList<String>();
						for(String targetType : targetTypeList) {
							targetTypeNameList.add(targetType+" "+SvrDataUtil.getNameFromDataDict(DictCode.标的类别,targetType));
						}
						String targetTypeName = StringUtil.joinString(targetTypeNameList, GlobalConstant.COMMA);
						item.setTargetType3(targetTypeName);
						item.setTargetType3Name(targetTypeName);
					}
				}else {
					item.setTargetType3Name(item.getTargetType3()+" "+SvrDataUtil.getNameFromDataDict(DictCode.标的类别,item.getTargetType3()));
					item.setTargetType3(item.getTargetType3Name());
				}
			}
			if (StringUtil.isNotEmpty(item.getCountryArea())){
				item.setCountryAreaName(item.getCountryArea()+" "+SvrDataUtil.getNameFromDataDict(DictCode.所属国家或地区,item.getCountryArea()));
				item.setCountryArea(item.getCountryAreaName());
			}
//			if (StringUtil.isNotEmpty(item.getFcrGradeOut())){
//				item.setFcrGradeOut(SvrDataUtil.getCodeFromDataDictByName(DictCode.资产评级,item.getFcrGradeOut())+" "+item.getFcrGradeOut());
//			}
//			if (StringUtil.isNotEmpty(item.getFcrGradeIn())){
//				item.setFcrGradeIn(SvrDataUtil.getCodeFromDataDictByName(DictCode.资产评级,item.getFcrGradeIn())+" "+item.getFcrGradeIn());
//			}
//			if (StringUtil.isNotEmpty(item.getPartyGrade())){
//				item.setPartyGradeName(item.getPartyGrade()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资产评级,item.getPartyGrade()));
//				item.setPartyGrade(item.getPartyGrade());
//			}
			if (StringUtil.isNotEmpty(item.getPartyGrade())){
				item.setPartyGradeName(item.getPartyGrade());
			}
//			item.setPartyGradeName(SvrDataUtil.getNameFromDataDict(SVReportDictCode.资产评级,item.getPartyGrade()));
			if (StringUtil.isNotEmpty(item.getTruActType())){
				item.setTruActTypeName(item.getTruActType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资金实际投向,item.getTruActType()));
				item.setTruActType(item.getTruActTypeName());
			}
			if (StringUtil.isNotEmpty(item.getBuyStruct())){
				item.setBuyStructName(item.getBuyStruct()+" "+SvrDataUtil.getNameFromDataDict(DictCode.购买结构,item.getBuyStruct()));
				item.setBuyStruct(item.getBuyStructName());
			}
			if (StringUtil.isNotEmpty(item.getManageType())){
				item.setManageTypeName(item.getManageType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.管理方式,item.getManageType()));
				item.setManageType(item.getManageTypeName());
			}
			if (StringUtil.isNotEmpty(item.getIsCtrlBottom())){
				item.setIsCtrlBottomName(item.getIsCtrlBottom()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是否_中债,item.getIsCtrlBottom()));
				item.setIsCtrlBottom(item.getIsCtrlBottomName());
			}
			if (StringUtil.isNotEmpty(item.getRegistOrg1())){
				item.setRegistOrg1Name(item.getRegistOrg1()+" "+SvrDataUtil.getNameFromDataDict(DictCode.登记备案机构,item.getRegistOrg1()));
				item.setRegistOrg1(item.getRegistOrg1Name());
			}
			if (StringUtil.isNotEmpty(item.getIsGovernFund())){
				item.setIsGovernFundName(item.getIsGovernFund()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是否_中债,item.getIsGovernFund()));
				item.setIsGovernFund(item.getIsGovernFundName());
			}
			if (StringUtil.isNotEmpty(item.getGovernInvestFund())){
				item.setGovernInvestFundName(item.getGovernInvestFund()+" "+SvrDataUtil.getNameFromDataDict(DictCode.政府投资基金投向,item.getGovernInvestFund()));
				item.setGovernInvestFund(item.getGovernInvestFundName());
			}
			if (StringUtil.isNotEmpty(item.getInAssetType())){
				item.setInAssetTypeName(item.getInAssetType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.行内资产类别,item.getInAssetType()));
				item.setInAssetType(item.getInAssetTypeName());
			}
			if (StringUtil.isNotEmpty(item.getFinancierType1())){
				item.setFinancierType1Name(item.getFinancierType1()+" "+SvrDataUtil.getNameFromDataDict(DictCode.发行机构类型_按规模划分,item.getFinancierType1()));
				item.setFinancierType1(item.getFinancierType1Name());
			}
			if (StringUtil.isNotEmpty(item.getFinancierType3())){
				item.setFinancierType3Name(item.getFinancierType3()+" "+SvrDataUtil.getNameFromDataDict(DictCode.发行机构类型_按经济类型划分,item.getFinancierType3()));
				item.setFinancierType3(item.getFinancierType3Name());
			}
			if (StringUtil.isNotEmpty(item.getFinancierType2())){
				item.setFinancierType2Name(item.getFinancierType2()+" "+SvrDataUtil.getNameFromDataDict(DictCode.发行机构类型_按技术领域划分,item.getFinancierType2()));
				item.setFinancierType2(item.getFinancierType2Name());
			}
			item.setStatusName(SvrDataUtil.getNameFromDataDict(DictCode.报送数据状态,item.getStatus()));
//			item.setStatus(item.getStatusName());
			item.setSendStatusName(SvrDataUtil.getNameFromDataDict(DictCode.报送状态,item.getSendStatus()));
//            item.setSendStatus(item.getSendStatusName());
			item.setSubmitFeedbackStatusName(SvrDataUtil.getNameFromDataDict(DictCode.报送反馈状态,item.getSubmitFeedbackStatus()));
            item.setSubmitFeedbackStatus(item.getSubmitFeedbackStatusName());
//			if (StringUtil.isNotEmpty(item.getCapUseIndu())){
//				item.setCapUseInduName(item.getCapUseIndu()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业一级分类,item.getCapUseIndu()));
//                item.setCapUseIndu(item.getCapUseInduName());
//			}
			if (StringUtil.isNotEmpty(item.getCapUseInduLevel2())){
				item.setCapUseInduLevel2Name(item.getCapUseInduLevel2()+" "+SvrDataUtil.getNameFromDataDict(DictCode.机构所属行业二级分类,item.getCapUseInduLevel2()));
				item.setCapUseInduLevel2(item.getCapUseInduLevel2Name());
                item.setCapUseInduName(item.getCapUseInduLevel2Name());
                item.setCapUseIndu(item.getCapUseInduLevel2Name());
			}
			//是否为标准化票据
			if(StringUtil.isNotEmpty(item.getIsBzhpj())) {
				item.setIsBzhpj(item.getIsBzhpj()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getIsBzhpj()));
			}
			//是否为银行理财产品
			if(StringUtil.isNotEmpty(item.getFinancialProductFlag())) {
				item.setFinancialProductFlag(item.getFinancialProductFlag()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getFinancialProductFlag()));
			}
			//是否为固定收益类
			if(StringUtil.isNotEmpty(item.getProfitFlag())) {
				item.setProfitFlag(item.getProfitFlag()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getProfitFlag()));
			}
			//是否为同业借款
			if(StringUtil.isNotEmpty(item.getIsDepositFlag())) {
				item.setIsDepositFlag(item.getIsDepositFlag()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getIsDepositFlag()));
			}
		});
	}
	
	/**
	 * 中债五月份升级增加的相关字典字段
	 * @param list
	 * @param flag
	 */
	public void dealDataListExt(List<AssetInfoRegist001Dto> list,boolean flag){
		list.forEach(item -> {
			//存款类型
			if(StringUtil.isNotEmpty(item.getDepoType())) {
				item.setDepoType(item.getDepoType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.存款类型,item.getDepoType()));
			}
			//计息类型
			if(StringUtil.isNotEmpty(item.getCouponSpecies())) {
				item.setCouponSpecies(item.getCouponSpecies()+" "+SvrDataUtil.getNameFromDataDict(DictCode.计息类型,item.getCouponSpecies()));
			}
			//规则付息标识
			if(StringUtil.isNotEmpty(item.getInterestRuleFlag())) {
				item.setInterestRuleFlag(item.getInterestRuleFlag()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getInterestRuleFlag()));
			}
			//利息分布方式
			if(StringUtil.isNotEmpty(item.getIntpayRule())) {
				item.setIntpayRule(item.getIntpayRule()+" "+SvrDataUtil.getNameFromDataDict(DictCode.利息分布方式,item.getIntpayRule()));
			}
			//对手方类型
			if(StringUtil.isNotEmpty(item.getRivalPartyType())) {
				item.setRivalPartyType(item.getRivalPartyType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.对手方类型,item.getRivalPartyType()));
			}
			//是否有浮动因子
			if(StringUtil.isNotEmpty(item.getFloatFactorFlag())) {
				item.setFloatFactorFlag(item.getFloatFactorFlag()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getFloatFactorFlag()));
			}
			//结构档次
			if(StringUtil.isNotEmpty(item.getStructGrade())) {
				item.setStructGrade(item.getStructGrade()+" "+SvrDataUtil.getNameFromDataDict(DictCode.结构档次,item.getStructGrade()));
			}
			//还本方式
			if(StringUtil.isNotEmpty(item.getRepaymentType())) {
				item.setRepaymentType(item.getRepaymentType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.还本方式,item.getRepaymentType()));
			}
			//分期还本条款标识
			if(StringUtil.isNotEmpty(item.getPeriodRepaymentFlag())) {
				item.setPeriodRepaymentFlag(item.getPeriodRepaymentFlag()+" "+SvrDataUtil.getNameFromDataDict(DictCode.分期还本条款标识,item.getPeriodRepaymentFlag()));
			}
			//抵质押物类型
			if(StringUtil.isNotEmpty(item.getPledgeType())) {
				item.setPledgeType(item.getPledgeType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.抵质押物类型,item.getPledgeType()));
			}
			//担保性质
			if(StringUtil.isNotEmpty(item.getNatureGuarantee())) {
				item.setNatureGuarantee(item.getNatureGuarantee()+" "+SvrDataUtil.getNameFromDataDict(DictCode.担保性质,item.getNatureGuarantee()));
			}
			//担保人与融资人关系
			if(StringUtil.isNotEmpty(item.getRelationFinan())) {
				item.setRelationFinan(item.getRelationFinan()+" "+SvrDataUtil.getNameFromDataDict(DictCode.担保人与融资人关系,item.getRelationFinan()));
			}
			//含权类型
			if(StringUtil.isNotEmpty(item.getContainType())) {
				item.setContainType(item.getContainType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.含权类型,item.getContainType()));
			}
			//行权方式
			if(StringUtil.isNotEmpty(item.getExerciseType())) {
				item.setExerciseType(item.getExerciseType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.资产行权方式,item.getExerciseType()));
			}
			//永续条款类型
			if(StringUtil.isNotEmpty(item.getPerpetualClauseType())) {
				item.setPerpetualClauseType(item.getPerpetualClauseType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.永续条款类型,item.getPerpetualClauseType()));
			}
			//利息递延条款类型
			if(StringUtil.isNotEmpty(item.getDeferredInterestType())) {
				item.setDeferredInterestType(item.getDeferredInterestType()+" "+SvrDataUtil.getNameFromDataDict(DictCode.利息递延条款类型,item.getDeferredInterestType()));
			}
			//递延利息是否计息
			if(StringUtil.isNotEmpty(item.getDeferredInterestInt())) {
				item.setDeferredInterestInt(item.getDeferredInterestInt()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getDeferredInterestInt()));
			}
			//部分赎回标识
			if(StringUtil.isNotEmpty(item.getPartRedemFlag())) {
				item.setPartRedemFlag(item.getPartRedemFlag()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getPartRedemFlag()));
			}


			//银行模式时,资产数据取当前机构的产品登记编码
			if(!CommonUtil.isBAmodel()){
				item.setIssuePartyId(CommonUtil.selectBankCode());
			}
			
			//是否为债转股
			if(StringUtil.isNotEmpty(item.getDeptEquity())) {
				item.setDeptEquity(item.getDeptEquity()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getDeptEquity()));
			}
			//持有目的
			if(StringUtil.isNotEmpty(item.getHoldPurpose())) {
				item.setHoldPurpose(item.getHoldPurpose()+" "+SvrDataUtil.getNameFromDataDict(DictCode.持有目的,item.getHoldPurpose()));
			}
			//是否由金融资产投资公司发行
			if(StringUtil.isNotEmpty(item.getFinancialComissue())) {
				item.setFinancialComissue(item.getFinancialComissue()+" "+SvrDataUtil.getNameFromDataDict(DictCode.是或否,item.getFinancialComissue()));
			}
		});
		
	}

    /**
     * 设置查询参数
     *
     * @param param 参数
     */
    private void dealSearchParam(Map<String, Object> param) {
        if (null != param) {// 选择行数据时
    		if (param.containsKey("keyList")) {
    			String keyList = param.get("keyList").toString();
    			try {
    				// 转换keyList
    				List<AssetInfoRegist001Dto> investList = JsonUtils.json2List(keyList,
    						AssetInfoRegist001Dto.class);
    				param.put("investList", investList);
    			} catch (IOException e) {
    	            logger.error(e.getMessage(),e);
    			}
    		} else {
	        	// 资产二级类别
	            String assetTypeTwo = (String)param.get("assetTypeTwo");
	            // 交易流通市场
	            String tradeMarket = (String)param.get("tradeMarket");
	            // 报送状态
	            String submitStatus = (String)param.get("submitStatus");
	          //反馈状态
	            String submitFeedbackStatus = (String)param.get("submitFeedbackStatus");
	            if(!StringUtil.isBlank(assetTypeTwo)){
	            	List<String> assetTypeTwoList = StringUtil.splitToList(GlobalConstant.COMMA, assetTypeTwo);
	                param.put("assetTypeTwoList", assetTypeTwoList);
	            }
	            if(!StringUtil.isBlank(tradeMarket)){
	            	List<String> tradeMarketList = StringUtil.splitToList(GlobalConstant.COMMA, tradeMarket);
	                param.put("tradeMarketList", tradeMarketList);
	            }
	            if(!StringUtil.isBlank(submitStatus)){
	            	List<String> submitStatusList = StringUtil.splitToList(GlobalConstant.COMMA, submitStatus);
	                param.put("submitStatusList", submitStatusList);
	                if(submitStatusList.contains(DictCodeListExt2.报送状态_未报送.getValue())){
						param.put("notSubmitStatus", DictCodeListExt2.报送状态_未报送.getValue());
					}
	            }
	            if(!StringUtil.isBlank(submitFeedbackStatus)){
	            	List<String> submitFeedbackStatusList = StringUtil.splitToList(GlobalConstant.COMMA, submitFeedbackStatus);
	                param.put("submitFeedbackStatusList", submitFeedbackStatusList);
	            }
	            // 数据状态
	            String status = (String) param.get("status");
	            // 将数据状态当做多选下拉框处理
	            if (StringUtil.isNotBlank(status)) {
	                param.put("statusList", StringUtil.splitToList(GlobalConstant.COMMA, status));
	            }

				List<String> assetTypeList = new ArrayList<>();
				if(null!=param.get("assetType")&&StringUtil.isNotEmpty(param.get("assetType"))){
					if(param.get("assetType").equals(DictCodeListExt3.资产_负债类别_公募和私募基金.getValue())){
						assetTypeList.add(DictCodeListExt2.资产_负债类别_公募基金.getValue());
						assetTypeList.add(DictCodeListExt2.资产_负债类别_私募基金.getValue());
					}else{
						assetTypeList.add(param.get("assetType").toString());
					}
				}
				param.put("assetTypeList", assetTypeList);
			}
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        Map<String, Object> pageParam = exportParam.getSelectParam();
        String pageIds = (String)pageParam.get("pageIds");
        if(StringUtil.isNotEmpty(pageIds)) {
            List<String> pageNames =  Arrays.asList(pageIds.split(GlobalConstant.COMMA));
            pageParam.replace("pageIds", pageIds, pageNames);
        }
        else{
            pageParam.replace("pageIds", pageIds, null);
        }
        // 导出方式flg
        String exportMethodFlg = (String)pageParam.get("export_method_flg");
        // 导出（未选中数据）
        if (StringUtil.equals(exportMethodFlg, "export")) {
            // 设置参数
        	dealSearchParam(pageParam);
            exportParam.config("/com/joyintech/tams/svreport/chb/assetinforeg/conf/export_conf.xml").select(() ->
					dealDataList(assetInfoRegist001Mapper.selectAssetInfoByPage(pageParam), false));
        }
        // 将选择的数据导出成excel
        else if (StringUtil.equals(exportMethodFlg, "exportSelect")) {
            String exportKeys =(String) pageParam.get("expKeys");
            String assetTypes =(String) pageParam.get("assetTypes");

			// 处理画面参数
			List<String> listParm = StringUtil.splitToList(GlobalConstant.COMMA, exportKeys);
			pageParam.put("listParm", listParm);
			pageParam.put("assetTypes", assetTypes);

			// add by zhangliang 20201109 公募和私募基金特殊处理 start
			if(StringUtil.equals(assetTypes, DictCodeListExt3.资产_负债类别_公募和私募基金.getValue())){
				List<String> assetTypeList = new ArrayList<>();
				assetTypeList.add(DictCodeListExt2.资产_负债类别_公募基金.getValue());
				assetTypeList.add(DictCodeListExt2.资产_负债类别_私募基金.getValue());
				pageParam.put("assetTypes", StringUtil.EMPTY);
				pageParam.put("assetTypeList", assetTypeList);
			}
			// add by zhangliang 20201109 公募和私募基金特殊处理 end
            exportParam.config("/com/joyintech/tams/svreport/chb/assetinforeg/conf/export_conf.xml").select(() ->
					dealDataList(assetInfoRegist001Mapper.selectExportSelectListSeq(pageParam), false));
        }

     }
    
    /****
     * 多法人改造
     * update by leipeng 20200701 资产多法人改造
     * @param param
     * @return
     */
    public List<AssetInfoRegist001Dto> selectAssetInfoByPage(Map<String, Object> param){
    	List<AssetInfoRegist001Dto> list = new ArrayList<>();
		//update byleipeng  【资产要素登记】公共资产，发行机构代码根据登录人维护 机构登记银行代码管理 登记银行代码

//    	 String issuePartyId = CommonUtil.selectBankCode();
		String issuePartyId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
		//通道投资资产管理产品是否穿透报送
		String isChlBtmStr = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
		param.put("isChlBtmStr",isChlBtmStr);
    	//多法人改造
		if(CommonUtil.isBAmodel()){
			list = assetInfoRegist001Mapper.selectAssetInfoByPageEx(param);
			list.forEach(item -> {
				if (StringUtil.isEmpty(item.getIssuePartyId())){
					item.setIssuePartyId(issuePartyId);
				}
			});
		}else{
			list = assetInfoRegist001Mapper.selectAssetInfoByPage(param);
		}
        return list;
    }
    /**
     * 查询选择后导出的数据
     * update by leipeng 20200701 资产多法人改造
     */
    public List<AssetInfoRegist001Dto> selectExportSelectListSeq(Map<String, Object> map){
		//update byleipeng  【资产要素登记】公共资产，发行机构代码根据登录人维护 机构登记银行代码管理 登记银行代码

//    	String issuePartyId = CommonUtil.selectBankCode();
		String issuePartyId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
    	List<AssetInfoRegist001Dto> list = new ArrayList<>();
		//通道投资资产管理产品是否穿透报送
		String isChlBtmStr = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
		map.put("isChlBtmStr",isChlBtmStr);
    	//多法人改造
		if(CommonUtil.isBAmodel()){
			list = assetInfoRegist001Mapper.selectExportSelectListSeqEx(map);
			list.forEach(item -> {
				if (StringUtil.isEmpty(item.getIssuePartyId())){
					item.setIssuePartyId(issuePartyId);
				}
			});
		}else{
			list = assetInfoRegist001Mapper.selectExportSelectListSeq(map);
		}
        return list;    	
    }
    
    @Override
    public com.joyintech.framework.grid.Page<AssetInfoRegist001Dto> selectPageData(Map<String, Object> pageParam) {
        List<AssetInfoRegist001Dto> data = null;
        String exportMethodFlg = (String)pageParam.get("export_method_flg");
        
    	//多法人改造获取当前登录机构
        pageParam.put("orgCode", CommonUtil.getTreeInstCode());
        if (StringUtil.equals(exportMethodFlg, "export")) {
            data =  restTemplate.postForList(
                    SvrServiceApplication.CHB_SERVICE_NAME + "/AssetInfoRegist/selectAssetInfoByPage",pageParam,
                    AssetInfoRegist001Dto.class);
        }  else {
            @SuppressWarnings("unchecked")
            List<String> listParm = (List<String>) pageParam.get("listParm");
            pageParam.put("listParm", listParm);
            data =  restTemplate.postForList(
                        SvrServiceApplication.CHB_SERVICE_NAME + "/AssetInfoRegist/selectExportSelectListSeq",pageParam,
                        AssetInfoRegist001Dto.class);
        }
        com.joyintech.framework.grid.Page<AssetInfoRegist001Dto> page = new com.joyintech.framework.grid.Page<AssetInfoRegist001Dto>();
        page.setRoot(data);
//        page.setTotal(data.size());
		if(!CollectionUtils.isEmpty(page.getRoot())){
			for(AssetInfoRegist001Dto dto : page.getRoot()){
				dto.setBelongInstitutionCode(null);
			}
		}
		int pageSize = StringUtil.integerValueOf(pageParam.get(GlobalConstant.PARAM_LENGTH).toString(), StringUtil.integerValueOf(CacheUtil.getSysParam(GlobalConstant.EXPORT_EXCEL_ROW_COUNT),50000));

		if(data!=null && data.size() > 0){
			page.setTotal(data.size()/pageSize+1);
		}else{
			page.setTotal(0);
		}

        return  page;
    }

	/**
     * 配置项中非空处理
     * @param Count
     * @param flag
     * @return
     */
    public int parsToInt(String count,String flag){
   	 if(StringUtil.isNotEmpty(count)&&Integer.parseInt(count)<=0){
   		 if("1".equals(flag)){
   	         	return 20;
   		 }
   		 else if("2".equals(flag)){
	         	return 500;
   		 }
   		 else {
	         	return 100;
   		 }
        }
        else{
        	return Integer.parseInt(count);
        }
   }

	/**
	 * 功能:导出前格式化字段
	 * @since 1.6.0
	 * @version 1.6.0
	 * @author：weiyuqiang 2019年1月25日
	 */
    @Override
    protected void beforeCreateExcel(List<AssetInfoRegist001Dto> contents) {
        // 格式化数据字典值
    	dealDataList(contents, false);
    	//增加中债五月份升级字段
    	dealDataListExt(contents, false);
    }

    /**
     * 功能:根据主键查询资产要素信息
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年1月24日
     */
//	public List<AssetInfoRegist001Dto> queryExportSelectList(List<String> registNoList) {
//		return assetInfoRegist001Mapper.selectExportSelectListSeq(registNoList);
//	}

    
    /**
     * 手动处理
     * update by leipeng 20200701 资产多法人改造
     * @param map 
     */
	@SuppressWarnings("unchecked")
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
	 * 更新登记编码信息方法-银行模式
	 * @param map
	 */
	public void updateSubStatis(Map<String, Object> map){
		dealSearchParam(map);
		List<AssetInfoRegist001Dto> assetInfoRegListOrg = new ArrayList<AssetInfoRegist001Dto>();
		//选择的手动处理
		assetInfoRegListOrg = (List<AssetInfoRegist001Dto>) map.get("investList");
        if (StringUtil.isEmptyList(assetInfoRegListOrg)) {
        	//未选中数据
            assetInfoRegListOrg = assetInfoRegist001Mapper.selectAssetInfoByPage(map);
		}
        List<AssetInfoRegist001Dto> assetInfoRegList = new ArrayList<AssetInfoRegist001Dto>();
        for(AssetInfoRegist001Dto m : assetInfoRegListOrg){
			// modify by zhangliang 20210309 手工处理与前台逻辑一致：只能选择报送状态为未报送、或报送状态为接口报送且反馈状态为发送失败和中债退回的数据进行手工处理 start
//            // 手工处理只处理未报送的
//            if (DictCodeListExt2.报送状态_未报送.getValue().equals(m.getSendStatus())
//                    ||DictCodeListExt2.报送状态_未报送.getValue().equals(m.getSendStatusName())){
//                assetInfoRegList.add(m);
//            }
			assetInfoRegList.add(m);
			// modify by zhangliang 20210309 手工处理与前台逻辑一致：只能选择报送状态为未报送、或报送状态为接口报送且反馈状态为发送失败和中债退回的数据进行手工处理 end
        }
        // 报送状态为接口报送且反馈状态为发送失败和中债退回的数据 该部分数据的报送流水号列表
        List<String> registSeqList = new ArrayList<String>();
//        List<String> assetList = new ArrayList<>();
		for(AssetInfoRegist001Dto m : assetInfoRegList){
		    // 手工处理只处理未报送的
		    registSeqList.add(m.getRegistSeqno());
//			assetList.add(m.getAssetCode());
		}
		
//		// 处理履历表数据和已报送的数据
//		dealRegHis(assetList,true);
        // 报送状态为接口报送且反馈状态为发送失败和中债退回的数据 更新反馈状态
        if (!StringUtil.isEmptyList(registSeqList)) {
            // 分批处理
            int maxDataCountOneTime = reportInsertCount;
            int dataCount = registSeqList.size();
            if (dataCount <= maxDataCountOneTime) {
            	assetInfoRegist001Mapper.updateSubmitStatusToOk(registSeqList,DictCodeListExt2.报送状态_手工处理.getValue());
            }
            else {
                List<String> newDataList = new ArrayList<String>();
                newDataList.addAll(registSeqList);

                // 分批处理
                int part = dataCount / maxDataCountOneTime; // 分批数
                for (int i = 0; i < part; i++ ) {
                	assetInfoRegist001Mapper.updateSubmitStatusToOk(newDataList.subList(0, maxDataCountOneTime),DictCodeListExt2.报送状态_手工处理.getValue());
                    newDataList.subList(0, maxDataCountOneTime).clear();
                }
                // 最后剩下的数据
                if (CollectionUtils.isNotEmpty(newDataList)) {
                	assetInfoRegist001Mapper.updateSubmitStatusToOk(newDataList,DictCodeListExt2.报送状态_手工处理.getValue());
                }
            }
        }
		this.updateInterfSubmitAndSeq(DictCodeListExt2.报送状态_手工处理.getValue(), assetInfoRegList, false,true);
	}
	
	
	/**
	 * 更新登记编码信息方法-联社模式
	 * update by leipeng 20200701 多法人改造
	 * @param map
	 */
	public void updateSubStatisEx(Map<String, Object> map){
		dealSearchParam(map);
		List<AssetInfoRegist001Dto> assetInfoRegListOrg = new ArrayList<AssetInfoRegist001Dto>();
		//选择的手动处理
		assetInfoRegListOrg = (List<AssetInfoRegist001Dto>) map.get("investList");
		//通道投资资产管理产品是否穿透报送
		String isChlBtmStr = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
		map.put("isChlBtmStr",isChlBtmStr);
        if (StringUtil.isEmptyList(assetInfoRegListOrg)) {
        	//未选中数据
            assetInfoRegListOrg = assetInfoRegist001Mapper.selectAssetInfoByPageEx(map);
		}
        List<AssetInfoRegist001Dto> assetInfoRegList = new ArrayList<AssetInfoRegist001Dto>();
        for(AssetInfoRegist001Dto m : assetInfoRegListOrg){
            // 手工处理只处理未报送的
			// modify by zhangliang 20210309 手工处理与前台逻辑一致：只能选择报送状态为未报送、或报送状态为接口报送且反馈状态为发送失败和中债退回的数据进行手工处理 start
//            if (DictCodeListExt2.报送状态_未报送.getValue().equals(m.getSendStatus())
//                    ||DictCodeListExt2.报送状态_未报送.getValue().equals(m.getSendStatusName())){
//                assetInfoRegList.add(m);
//            }
			assetInfoRegList.add(m);
			// modify by zhangliang 20210309 手工处理与前台逻辑一致：只能选择报送状态为未报送、或报送状态为接口报送且反馈状态为发送失败和中债退回的数据进行手工处理 end
        }
        // 报送状态为接口报送且反馈状态为发送失败和中债退回的数据 该部分数据的报送流水号列表
        List<ChbAssetSubStatus> registSeqList = new ArrayList<ChbAssetSubStatus>();
//        List<String> assetList = new ArrayList<>();
		for(AssetInfoRegist001Dto m  : assetInfoRegList){
			ChbAssetSubStatus status = new ChbAssetSubStatus();
			//登记表主键
			status.setRegistSeqno(m.getRegistSeqno());
			//标识资产基本信息
			status.setAssetType("01");
			status.setSubmitFeedbackStatus(DictCodeListExt2.报送结果_审核通过.getValue());
			status.setSendStatus(DictCodeListExt2.报送状态_手工处理.getValue());
			status.setInstitutionCode(CommonUtil.getInstitutionCodeByLoginUser_ms());
		    registSeqList.add(status);
//			assetList.add(m.getAssetCode());
		}
		
//		// 处理履历表数据和已报送的数据
//		dealRegHis(assetList,true);
        // 报送状态为接口报送且反馈状态为发送失败和中债退回的数据 更新反馈状态
        if (!StringUtil.isEmptyList(registSeqList)) {
            // 分批处理
            int maxDataCountOneTime = reportInsertCount;
            int dataCount = registSeqList.size();
            if (dataCount <= maxDataCountOneTime) {
            	// 
            	assetInfoRegist001Mapper.mergeSubStatusList(registSeqList);
            }
            else {
                List<ChbAssetSubStatus> newDataList = new ArrayList<ChbAssetSubStatus>();
                newDataList.addAll(registSeqList);

                // 分批处理
                int part = dataCount / maxDataCountOneTime; // 分批数
                for (int i = 0; i < part; i++ ) {
                	
                	assetInfoRegist001Mapper.mergeSubStatusList(newDataList.subList(0, maxDataCountOneTime));
                	
                    newDataList.subList(0, maxDataCountOneTime).clear();
                }
                // 最后剩下的数据
                if (CollectionUtils.isNotEmpty(newDataList)) {
                	assetInfoRegist001Mapper.mergeSubStatusList(newDataList);
                }
            }
        }
		this.updateInterfSubmitAndSeq(DictCodeListExt2.报送状态_手工处理.getValue(), assetInfoRegList, false,false);
	}
	
	
	/**
	 * @Description 取消手工处理
	 * @param map
	 * @return boolean 是否同时存在已登记和未登记的数据 
	 * @version 3.0 2019年1月24日
	 * update by leipeng 20200701 资产多法人改造
	 */
	@SuppressWarnings("unchecked")
	public boolean cancelMannul(Map<String, Object> map) {
		boolean flag = false;
		if(CommonUtil.isBAmodel()){
			flag = cancelManulEx(map);
		}else{
			flag = cancelManulNormal(map);
		}
		return flag;
	}
	
	
	/**
	 * 取消手工处理 - 联社版
	 * @param map
	 * @return
	 */
	public boolean cancelManulEx(Map<String, Object> map){
		dealSearchParam(map);
		List<AssetInfoRegist001Dto> assetInfoList = new ArrayList<AssetInfoRegist001Dto>();
		// 用于判断是否同时存在未报送和已报送的数据
		// 所有未登记的数据
        Map<String, Object> querymap = new HashMap<>();
        querymap.put("submitStatusList", StringUtil.splitToList(GlobalConstant.COMMA, DictCodeListExt2.报送状态_未报送.getValue()));
        querymap.put("notSubmitStatus", DictCodeListExt2.报送状态_未报送.getValue());
        querymap.put("orgCode", CommonUtil.getTreeInstCode());
		//通道投资资产管理产品是否穿透报送
		String isChlBtmStr = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
		querymap.put("isChlBtmStr",isChlBtmStr);
        List<AssetInfoRegist001Dto> assetInfoNotRegList = assetInfoRegist001Mapper.selectAssetInfoByPageEx(querymap);
        Map<String,String> assetInfoNotRegMap = assetInfoNotRegList.stream().collect(Collectors.toMap(AssetInfoRegist001Dto::getAssetCode,
                AssetInfoRegist001Dto::getAssetCode,(key1,key2)->key2));
		//选择的手动处理
		assetInfoList = (List<AssetInfoRegist001Dto>) map.get("investList");
		//通道投资资产管理产品是否穿透报送
		map.put("isChlBtmStr",isChlBtmStr);
        if (StringUtil.isEmptyList(assetInfoList)) {
        	//未选中数据
        	map.put("sendStatus", "03");
        	assetInfoList = assetInfoRegist001Mapper.selectAssetInfoByPageEx(map);
		}
		// 报送状态为接口报送且反馈状态为发送失败和中债退回的数据 该部分数据的报送流水号列表
        List<ChbAssetSubStatus> registSeqList = new ArrayList<ChbAssetSubStatus>();
//        List<String> assetList = new ArrayList<>();
		List<String> registSeqstatusList = new ArrayList<String>();
		for(AssetInfoRegist001Dto m : assetInfoList){
			ChbAssetSubStatus status = new ChbAssetSubStatus();
			status.setAssetType("01");
			status.setRegistSeqno(m.getRegistSeqno());
			status.setInstitutionCode(null!=map.get("belongOrgCode")?map.get("belongOrgCode").toString():"");
			
			registSeqList.add(status);
			registSeqstatusList.add(m.getRegistSeqno());
//            assetList.add(m.getAssetCode());
            // 如果没有该产品的数据则插入如果有则判断是否同时存在未报送和已报送
            // 只要有未报送的登记数据就不允许取消手工处理
            if (DictCodeListExt2.报送状态_手工处理.getValue().equals(m.getSendStatus())
                    && assetInfoNotRegMap.get(m.getAssetCode()) != null){
                return true;
            }
                    
		}
//        // 处理履历表数据和已报送的数据
//        dealRegHis(assetList,false);
		if (!StringUtil.isEmptyList(registSeqList)) {
            // 分批处理
            int maxDataCountOneTime = reportInsertCount;
            int dataCount = registSeqList.size();
            if (dataCount <= maxDataCountOneTime) {
                // 将履历表中履历号最大的数据挪回登记表
            	
                assetInfoRegist001Mapper.deleteSubStausEx(registSeqList );
//                // 删除履历表中移除数据
            	assetInfoRegist001Mapper.updateAssetInfoRegist(registSeqstatusList);
            }
            else {
                List<ChbAssetSubStatus> newDataList = new ArrayList<ChbAssetSubStatus>();
                newDataList.addAll(registSeqList);
				List<String> newDataStatusList = new ArrayList<String>();
				newDataStatusList.addAll(registSeqstatusList);

                // 分批处理
                int part = dataCount / maxDataCountOneTime; // 分批数
                for (int i = 0; i < part; i++ ) {
                    assetInfoRegist001Mapper.deleteSubStausEx(registSeqList.subList(0, maxDataCountOneTime));
					assetInfoRegist001Mapper.updateAssetInfoRegist(newDataStatusList.subList(0, maxDataCountOneTime));
                    newDataList.subList(0, maxDataCountOneTime).clear();
					newDataStatusList.subList(0, maxDataCountOneTime).clear();
                    assetInfoList.subList(0, maxDataCountOneTime).clear();
                }
                // 最后剩下的数据
                if (CollectionUtils.isNotEmpty(newDataList)) {
                    assetInfoRegist001Mapper.deleteSubStausEx(registSeqList);
					assetInfoRegist001Mapper.updateAssetInfoRegist(newDataStatusList);

                }
            }
        }
		return false;
	}
	
	/**
	 * 取消手工处理银行版
	 * @param map
	 * @return
	 * update by leipeng 20200701 资产多法人改造
	 */
	public boolean cancelManulNormal(Map<String, Object> map){
		
		dealSearchParam(map);
		List<AssetInfoRegist001Dto> assetInfoList = new ArrayList<AssetInfoRegist001Dto>();
		// 用于判断是否同时存在未报送和已报送的数据
		// 所有未登记的数据
        Map<String, Object> querymap = new HashMap<>();
        querymap.put("submitStatusList", StringUtil.splitToList(GlobalConstant.COMMA, DictCodeListExt2.报送状态_未报送.getValue()));
		querymap.put("notSubmitStatus", DictCodeListExt2.报送状态_未报送.getValue());
        List<AssetInfoRegist001Dto> assetInfoNotRegList = assetInfoRegist001Mapper.selectAssetInfoByPage(querymap);
        Map<String,String> assetInfoNotRegMap = assetInfoNotRegList.stream().collect(Collectors.toMap(AssetInfoRegist001Dto::getAssetCode,
                AssetInfoRegist001Dto::getAssetCode,(key1,key2)->key2));
		//选择的手动处理
		assetInfoList = (List<AssetInfoRegist001Dto>) map.get("investList");
        if (StringUtil.isEmptyList(assetInfoList)) {
        	//未选中数据
        	map.put("sendStatus", "03");
        	assetInfoList = assetInfoRegist001Mapper.selectAssetInfoByPage(map);
		}
		// 报送状态为接口报送且反馈状态为发送失败和中债退回的数据 该部分数据的报送流水号列表
        List<String> registSeqList = new ArrayList<String>();
//        List<String> assetList = new ArrayList<>();
		for(AssetInfoRegist001Dto m : assetInfoList){
			registSeqList.add(m.getRegistSeqno());
//            assetList.add(m.getAssetCode());
            // 如果没有该产品的数据则插入如果有则判断是否同时存在未报送和已报送
            // 只要有未报送的登记数据就不允许取消手工处理
            if (DictCodeListExt2.报送状态_手工处理.getValue().equals(m.getSendStatus())
                    && assetInfoNotRegMap.get(m.getAssetCode()) != null){
                return true;
            }
                    
		}
//        // 处理履历表数据和已报送的数据
//        dealRegHis(assetList,false);
		if (!StringUtil.isEmptyList(registSeqList)) {
            // 分批处理
            int maxDataCountOneTime = reportInsertCount;
            int dataCount = registSeqList.size();
            if (dataCount <= maxDataCountOneTime) {
                // 将履历表中履历号最大的数据挪回登记表
                assetInfoRegist001Mapper.insertReportDataFromHis(assetInfoList);
                // 删除履历表中移除数据
                assetInfoRegist001Mapper.deleteLastSubmitHis(assetInfoList);
            	assetInfoRegist001Mapper.updateAssetInfoRegist(registSeqList);
            }
            else {
                List<String> newDataList = new ArrayList<String>();
                newDataList.addAll(registSeqList);

                // 分批处理
                int part = dataCount / maxDataCountOneTime; // 分批数
                for (int i = 0; i < part; i++ ) {
                    // 将履历表中履历号最大的数据挪回登记表
                    assetInfoRegist001Mapper.insertReportDataFromHis(assetInfoList.subList(0, maxDataCountOneTime));
                    // 删除履历表中移除数据
                    assetInfoRegist001Mapper.deleteLastSubmitHis(assetInfoList.subList(0, maxDataCountOneTime));
                	assetInfoRegist001Mapper.updateAssetInfoRegist(newDataList.subList(0, maxDataCountOneTime));
                    newDataList.subList(0, maxDataCountOneTime).clear();
                    assetInfoList.subList(0, maxDataCountOneTime).clear();
                }
                // 最后剩下的数据
                if (CollectionUtils.isNotEmpty(newDataList)) {
                    // 将履历表中履历号最大的数据挪回登记表
                    assetInfoRegist001Mapper.insertReportDataFromHis(assetInfoList);
                    // 删除履历表中移除数据
                    assetInfoRegist001Mapper.deleteLastSubmitHis(assetInfoList);
                	assetInfoRegist001Mapper.updateAssetInfoRegist(newDataList);
                }
            }
        }
		return false;
		
	}
	
	/**
	 * @Description 分批次执行updateInterfSubmitAndSeq
	 * @param sendStatus 报送状态
	 * @param assetInfoList 资产要素list
	 * @param isSendAllData 是否全量报送
     * @param isUpdateHis 是否更新履历表
	 * @return void
	 * @version 3.0 2019年1月24日
	 * update by leipeng 20200701 资产多法人改造
	 */
	private void updateInterfSubmitAndSeqEx(String sendStatus, List<AssetInfoRegist001Dto> assetInfoList, 
	        boolean isSendAllData ,boolean isUpdateHis) {
		if (StringUtil.isEmptyList(assetInfoList)) {
			return;
		}
		int maxDataCountOneTime = reportInsertLongCount;
		int dataCount = assetInfoList.size();
		if (dataCount <= maxDataCountOneTime) {
		    if (isUpdateHis){
                //已报送数据进留痕表
    		    assetInfoRegist001Mapper.insertReportHis(assetInfoList);
                //删除正式表已报送数据
                assetInfoRegist001Mapper.deleteLastSubmitRegist(assetInfoList);
		    }
			assetInfoRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus, assetInfoList);
			return;
		}

		// 对于批处理报送全量的数据，直接更新所有检查通过的未报送数据，状态修改为接口报送
		if (isSendAllData) {
		    logger.info("updateAllInterfSubmitAndSeqBatch全量更新登记表start===");
            if (isUpdateHis){
                //已报送数据进留痕表
                assetInfoRegist001Mapper.insertReportHis(assetInfoList);
                //删除正式表已报送数据
                assetInfoRegist001Mapper.deleteLastSubmitRegist(assetInfoList);
            }
		    assetInfoRegist001Mapper.updateAllInterfSubmitAndSeqBatch(sendStatus);
		    logger.info("updateAllInterfSubmitAndSeqBatch全量更新登记表end===");
		    return;
		}

		List<AssetInfoRegist001Dto> newDataList = new ArrayList<AssetInfoRegist001Dto>();
		newDataList.addAll(assetInfoList);

		// 分批处理
		int part = dataCount / maxDataCountOneTime; // 分批数
		for (int i = 0; i < part; i++) {
			logger.info("单次更新登记表start===");
            if (isUpdateHis){
                //已报送数据进留痕表
                assetInfoRegist001Mapper.insertReportHis(newDataList.subList(0, maxDataCountOneTime));
                //删除正式表已报送数据
                assetInfoRegist001Mapper.deleteLastSubmitRegist(newDataList.subList(0, maxDataCountOneTime));
            }
			assetInfoRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus,
                newDataList.subList(0, maxDataCountOneTime));
			newDataList.subList(0, maxDataCountOneTime).clear();
			logger.info("单次更新登记表end===");
		}
		// 最后剩下的数据
		if (CollectionUtils.isNotEmpty(newDataList)) {
			logger.info("单次更新登记表start===");
            if (isUpdateHis){
                //已报送数据进留痕表
                assetInfoRegist001Mapper.insertReportHis(newDataList);
                //删除正式表已报送数据
                assetInfoRegist001Mapper.deleteLastSubmitRegist(newDataList);
            }
			assetInfoRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus, newDataList);
			logger.info("单次更新登记表end===");
		}
	}
	
	
	/**
	 * @Description 分批次执行updateInterfSubmitAndSeq
	 * @param sendStatus 报送状态
	 * @param assetInfoList 资产要素list
	 * @param isSendAllData 是否全量报送
     * @param isUpdateHis 是否更新履历表
	 * @return void
	 * @version 3.0 2019年1月24日
	 */
	private void updateInterfSubmitAndSeq(String sendStatus, List<AssetInfoRegist001Dto> assetInfoList, 
	        boolean isSendAllData ,boolean isUpdateHis) {
		if (StringUtil.isEmptyList(assetInfoList)) {
			return;
		}
		int maxDataCountOneTime = reportInsertLongCount;
		int dataCount = assetInfoList.size();
		if (dataCount <= maxDataCountOneTime) {
		    if (isUpdateHis){
                //已报送数据进留痕表
    		    assetInfoRegist001Mapper.insertReportHis(assetInfoList);
                //删除正式表已报送数据
                assetInfoRegist001Mapper.deleteLastSubmitRegist(assetInfoList);
		    }
			assetInfoRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus, assetInfoList);
			return;
		}

		// 对于批处理报送全量的数据，直接更新所有检查通过的未报送数据，状态修改为接口报送
		if (isSendAllData) {
		    logger.info("updateAllInterfSubmitAndSeqBatch全量更新登记表start===");
            if (isUpdateHis){
                //已报送数据进留痕表
                assetInfoRegist001Mapper.insertReportHis(assetInfoList);
                //删除正式表已报送数据
                assetInfoRegist001Mapper.deleteLastSubmitRegist(assetInfoList);
            }
		    assetInfoRegist001Mapper.updateAllInterfSubmitAndSeqBatch(sendStatus);
		    logger.info("updateAllInterfSubmitAndSeqBatch全量更新登记表end===");
		    return;
		}

		List<AssetInfoRegist001Dto> newDataList = new ArrayList<AssetInfoRegist001Dto>();
		newDataList.addAll(assetInfoList);

		// 分批处理
		int part = dataCount / maxDataCountOneTime; // 分批数
		for (int i = 0; i < part; i++) {
			logger.info("单次更新登记表start===");
            if (isUpdateHis){
                //已报送数据进留痕表
                assetInfoRegist001Mapper.insertReportHis(newDataList.subList(0, maxDataCountOneTime));
                //删除正式表已报送数据
                assetInfoRegist001Mapper.deleteLastSubmitRegist(newDataList.subList(0, maxDataCountOneTime));
            }
			assetInfoRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus,
                newDataList.subList(0, maxDataCountOneTime));
			newDataList.subList(0, maxDataCountOneTime).clear();
			logger.info("单次更新登记表end===");
		}
		// 最后剩下的数据
		if (CollectionUtils.isNotEmpty(newDataList)) {
			logger.info("单次更新登记表start===");
            if (isUpdateHis){
                //已报送数据进留痕表
                assetInfoRegist001Mapper.insertReportHis(newDataList);
                //删除正式表已报送数据
                assetInfoRegist001Mapper.deleteLastSubmitRegist(newDataList);
            }
			assetInfoRegist001Mapper.updateInterfSubmitAndSeqBatch(sendStatus, newDataList);
			logger.info("单次更新登记表end===");
		}
	}

    /**
     * 查询资产代码
     * @param pageParam 参数
     * @return 结果list
     * @see
     * @since 1.6.0
     */
    public List<SelectOptionDto> queryAssetCodeList(Map<String, Object> pageParam) {
        String orgCode = (String)pageParam.get("orgCode");
        List<String> orgCodeList = StringUtil.splitToList(GlobalConstant.COMMA, orgCode);
        if (StringUtil.isBlank(orgCode)) {
            orgCodeList = CommonUtil.getOrgCodeListForSearch();
        }
        pageParam.put("orgCodeList", orgCodeList);
        List<SelectOptionDto> dtoList = new ArrayList<SelectOptionDto>();
        //在所选的机构list不为空或者是用户为管理员时
        dtoList = assetInfoRegist001Mapper.selectAssetCodeList(pageParam);
        return dtoList;
    }
    
    /**
     * 功能:处理履历表数据
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年5月21日
     * @param assetList 资产代码列表
     * @param isManual 是否手工处理
     * void
     */
    @SuppressWarnings("unused")
    private void dealRegHis(List<String> assetList,boolean isManual){
        // 手工处理
        if (isManual) {
            // 查询需要进入履历表的数据
            List<ChbAssetInfoSubHisDto> assetRegHisList = new ArrayList<>();
            // 分批处理防止数据过多报错
            int queryCount = 200;
            int lastIndex = queryCount;
            for (int index = 0; index < assetList.size();) {
                if (lastIndex > assetList.size()) {
                    lastIndex = assetList.size();
                }
                List<ChbAssetInfoSubHisDto> assetRegHisListAdd = assetInfoRegist001Mapper
                        .selectAssetInfoRegHisByAssetCode(assetList.subList(index, lastIndex));
                if (CollectionUtils.isNotEmpty(assetRegHisListAdd)) {
                    assetRegHisList.addAll(assetRegHisListAdd);
                }
                index = lastIndex;
                lastIndex += queryCount;
            }
            if (CollectionUtils.isNotEmpty(assetRegHisList)) {
                // 登记表的seqno
                List<String> regseqnoList = new ArrayList<>();
                for (ChbAssetInfoSubHisDto assetInfoSubHis : assetRegHisList){
                    regseqnoList.add(assetInfoSubHis.getHisNo());
                }
                // 已报送的数据进入履历表
                logger.debug("资产要素信息登记履历表插入数据：插入开始----------");
                int count = 0;
                SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
                try {
                    ChbAssetInfoSubHisMapper batchMapper = session.getMapper(ChbAssetInfoSubHisMapper.class);
                    for (ChbAssetInfoSubHisDto asset : assetRegHisList) {
                        batchMapper.insert(asset);
                        count++;
                        if (count % 1000 == 0) {
                            session.commit();
                            // 清理缓存，防止溢出
                            session.clearCache();
                        }
                    }
                    session.commit();
                    // 清理缓存，防止溢出
                    session.clearCache();
                } catch (Exception e) {
                	logger.error(e.getMessage(),e);
                    session.rollback();
                    throw new BusinessException(e);
                } finally {
                    session.close();
                }
                logger.debug("资产要素信息登记履历表插入数据：插入结束----------");
                // 插入前先删除未报送的登记数据
                logger.debug("资产要素信息登记删除已报送数据：删除开始----------");
                // 数据批量一次删除条数,如果参数设置过大则强制定位50
                int batchDeleteCount = reportSelectOrDeleteCount;
                // 每批最后一个的下标
                int batchDeleteLastIndex = batchDeleteCount;
                for (int index = 0; index < regseqnoList.size();) {
                    if (batchDeleteLastIndex > regseqnoList.size()) {
                        batchDeleteLastIndex = regseqnoList.size();
                        assetInfoRegist001Mapper.deleteByRegistseqno(
                                regseqnoList.subList(index, batchDeleteLastIndex));
                        index = batchDeleteLastIndex;// 设置下一批下标
                        batchDeleteLastIndex = index + batchDeleteCount;
                    }
                }
                logger.debug("资产要素信息登记删除已报送数据：删除结束----------");
            }
        } else {
            // 查询需要进入登记表的数据
            List<AssetInfoRegist001Dto> assetRegList = new ArrayList<>();
            // 分批处理防止数据过多报错
            int queryCount = 200;
            int lastIndex = queryCount;
            for (int index = 0; index < assetList.size();) {
                if (lastIndex > assetList.size()) {
                    lastIndex = assetList.size();
                }
                List<AssetInfoRegist001Dto> assetRegListAdd = chbAssetInfoSubHisMapper
                        .selectAssetInfoRegByAssetCode(assetList.subList(index, lastIndex));
                if (CollectionUtils.isNotEmpty(assetRegListAdd)) {
                    assetRegList.addAll(assetRegListAdd);
                }
                index = lastIndex;
                lastIndex += queryCount;
            }
            if (CollectionUtils.isNotEmpty(assetRegList)) {
                // 履历表的hisno
                List<String> hisnoList = new ArrayList<>();
                for (AssetInfoRegist001Dto assetInfoSub : assetRegList){
                    hisnoList.add(assetInfoSub.getRegistSeqno());
                }
                // 已报送的数据进入履历表
                logger.debug("资产要素信息登记表插入数据：插入开始----------");
                int count = 0;
                SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
                try {
                    AssetInfoRegist001Mapper batchMapper = session.getMapper(AssetInfoRegist001Mapper.class);
                    for (AssetInfoRegist001Dto asset : assetRegList) {
                        batchMapper.insert(asset);
                        count++;
                        if (count % 1000 == 0) {
                            session.commit();
                            // 清理缓存，防止溢出
                            session.clearCache();
                        }
                    }
                    session.commit();
                    // 清理缓存，防止溢出
                    session.clearCache();
                } catch (Exception e) {
                	logger.error(e.getMessage(),e);
                    session.rollback();
                    throw new BusinessException(e);
                } finally {
                    session.close();
                }
                logger.debug("资产要素信息登记表插入数据：插入结束----------");
                // 插入前先删除未报送的登记数据
                logger.debug("资产要素信息登记履历表删除数据：删除开始----------");
                // 数据批量一次删除条数,如果参数设置过大则强制定位50
                int batchDeleteCount = reportSelectOrDeleteCount;
                // 每批最后一个的下标
                int batchDeleteLastIndex = batchDeleteCount;
                for (int index = 0; index < hisnoList.size();) {
                    if (batchDeleteLastIndex > hisnoList.size()) {
                        batchDeleteLastIndex = hisnoList.size();
                        chbAssetInfoSubHisMapper.deleteByHisno(
                                hisnoList.subList(index, batchDeleteLastIndex));
                        index = batchDeleteLastIndex;// 设置下一批下标
                        batchDeleteLastIndex = index + batchDeleteCount;
                    }
                }
                logger.debug("资产要素信息登记履历表删除数据：删除结束----------");
            }
        }
    }
    
    /**	功能:获取需要检查的登记数据
     * @param param
     * @return
     */
    public List<AssetInfoRegist001Dto> getCheckRepList(Map<String, Object> param) {
    	dealSearchParam(param);
        List<AssetInfoRegist001Dto> list = assetInfoRegist001Mapper.selectAssetInfoByPage(param);
        return list;
    }
    
    /**
     * 功能:对所选择的数据进行数据检查
     * 01 数据检查    02 自动报送的检查
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年6月25日
     * @param keyList type 
     * @return 
     * List<ExcelValidDto>
     */
    public List<ExcelValidDto> checkData(Map<String, Object> map, String type , Date executeDate) {
    	//判断前台是否勾选数据
    	List<AssetInfoRegist001Dto> keyList = new ArrayList<AssetInfoRegist001Dto>();
        Map<String, Object> param = map;
        if (map == null){
        	param = new HashMap<>();
        }
        if (!param.containsKey("keyList")) {
        	if(StringUtil.equals(type, "01")){
        		//前台数据检查未勾选数据查询所有需要检查的数据
        		keyList = getCheckRepList(param);
        	}else{
        		//报送前检查自动检查批处理查询所有需要检查的数据
        		param.put("mdate", executeDate);
        		keyList = assetInfoRegist001Mapper.selectAutoAssetInfo(param);
        	}
        } else {
            String dataList = map.get("keyList").toString();
            try {
                keyList = JsonUtils.json2List(dataList,
                		AssetInfoRegist001Dto.class);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
        if (CollectionUtils.isEmpty(keyList)) {
            List<ExcelValidDto> checkResult = new ArrayList<ExcelValidDto>();
            checkResult.add(new ExcelValidDto(StringUtil.EMPTY, String.valueOf(0), "无报送数据", null));
            return checkResult;
        }
    	
        List<AssetInfoRegist001Dto> checkKeys = new ArrayList<AssetInfoRegist001Dto>();
        checkKeys.addAll(keyList);
        Map<String,CheckReport001Dto> checkmap = new HashMap<String,CheckReport001Dto>();
        Map<String,AssetInfoRegist001Dto> repmap = new HashMap<String,AssetInfoRegist001Dto>();
        List<AssetInfoRegist001Dto> deallist = assetInfoRegist001Mapper.selectDataListByRegList(checkKeys);
        List<ExcelValidDto> result = new ArrayList<ExcelValidDto>();
//        repmap = deallist.stream().collect(Collectors.toMap(InvestTradeRegist001Dto::getRegistSeqno,(p)->p,(key1,key2)->key1));
        repmap = deallist.stream().collect(Collectors.toMap(p->p.getAssetCode(),(p)->p,(key1,key2)->key1));
        // 查询检查表
        List<CheckReport001Dto> resultlist = new ArrayList<CheckReport001Dto>();
        // 检查登记表中小于今天所有未报送的数据
        if(CollectionUtils.isNotEmpty(checkKeys)){

        	List<String> keys = checkKeys.stream().map(string -> string.getAssetCode()).collect(Collectors.toList());
            // 调用检查方法
            resultlist.addAll(check(executeDate,keys));
            // 同一资产同一产品汇总一条
            Map<String,String> showInMap = new HashMap<String,String>();
            checkmap = resultlist.stream().collect(Collectors.toMap(CheckReport001Dto::getUniqueKey,(p)->p,(key1,key2)->key1));
            for(AssetInfoRegist001Dto key : checkKeys){
                String uniqueKey = key.getAssetCode();
                // 已经有的资产和产品不重复出数据检查
                if (!showInMap.containsKey(uniqueKey)){
                    showInMap.put(uniqueKey, uniqueKey);
                } else {
                    // 已经出过则跳过
                    continue;
                }
                if(checkmap.get(uniqueKey)!=null){
                    ExcelValidDto exc=new ExcelValidDto();
                    exc.setCheckResult(checkmap.get(uniqueKey).getCheckResult());
                    exc.setErrorInfo(checkmap.get(uniqueKey).getErrorInfo());
                    exc.setWarnInfo(checkmap.get(uniqueKey).getWarnInfo());
                    exc.setValidKey(checkmap.get(uniqueKey).getValidKey());
                    result.add(exc);
                }else{
                    ExcelValidDto exc=new ExcelValidDto();
                    exc.setValidKey("资产代码:"+repmap.get(uniqueKey).getAssetCode());
                    exc.setCheckResult("01");
                    result.add(exc);
                }
            }
        }
        return result;
    }
    
    
    /**
     * @Description 检查登记表中小于今天所有未报送的数据
     * @return
     * @return List<CheckReport001Dto>
     * @version 1.7 2019年6月24日
     */
    public List<CheckReport001Dto> check(Date executeDate,List<String> keys){
        // 数据日期为系统时间
        Date checkdate = DateUtil.parse(DateUtil.getSysDate());
		// 供跑批使用
		if (StringUtil.isNotEmpty(executeDate)){
			checkdate = executeDate;
		}
        // 删除今天所有已检查的数据
        checkReport001Mapper.delReportDatasByCodeAndDate(DictCodeListExt2.报表名称_资产要素信息登记.getValue(),checkdate,keys);
        //增加例外情况过滤
        List<ChbCheckRuleout> listInvestTradeRuleout=chbCheckRuleoutMapper.selectDataByCheckTypeAndReprotCode(CheckConfConstants.CHB_CHECK_TYPE_B, CheckConfConstants.CHB_INVEST_TRADE_SUB);
        Map<String, List<ChbCheckRuleout>> mapInvestTradeRuleout = listInvestTradeRuleout.stream().collect(
                 JoyinCollectors.groupingBy(x -> x.getProdId()));
        //检查数据
        List<CheckReport001Dto> resultList = new ArrayList<CheckReport001Dto>();
        //报送前强制的错误检查
        resultList.addAll(beforeSubErrorCheck.checkAssetInfoReg(checkdate,keys,mapInvestTradeRuleout));
        //警告逻辑检查
        //resultList.addAll(beforeSubCheck.checkInvestReg(checkdate,mapInvestTradeRuleout));
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
    
    public List<ExcelValidDto> checkBeforeSubmit(Date executeDate){
		return checkData(null, "02", executeDate);
	}

	/**start    中债一二期报送直连  add by    wangzehui  20201012 */
	public Map<String,Object> submitBeforeCheck(Map<String, Object> param) {
		// 币种数据字典
		DataDict ccyDto = CacheUtil.getDictCode(DictCode.币种_中债.getValue());
		List<ExcelValidDto> result=new ArrayList<ExcelValidDto>();
		List<AssetInfoRegist001Dto> list=new ArrayList<>();
		Map<String,Object> returnMap = new HashMap<>();
		// 选择行数据时
		if (param.containsKey("keyList")) {
			String keyList = param.get("keyList").toString();
			try {
				list = JsonUtils.json2List(keyList,
						AssetInfoRegist001Dto.class);
			}
			catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}else{
			 dealSearchParam(param);
			 list = assetInfoRegist001Mapper.selectAssetInfoByPage(param);
			dealDataList(list, false);
			dealDataListExt(list, false);
			//查询数据
		}
		if (CollectionUtils.isEmpty(list)) {
			returnMap.put("excelValidDtoList", Arrays.asList(new ExcelValidDto(StringUtil.EMPTY, String.valueOf(0), "无报送数据", null)));
			return returnMap;
		}
		// 查询数据库表字段
		List<String> listCloumn = baseMapper.getTableColumnList(tableName);
		//调用共通 将list<接口表DTO>转换成List<Map<Key, Object>>格式
		List<Map<String, String>> listMap = svrMaptoEntityUtil.convertEntityToMap(list, listCloumn);
		int i=result.size();
		for(Map<String, String>  dataMap:listMap){
			//beforeSubErrorCheck.checkBeforProdDeclareSub(dataMap);
			ExcelValidDto excelValidDto = new ExcelValidDto();
			String validKey ="行内资产负债编码："+dataMap.get("ASSET_CODE")+ GlobalConstant.COMMA
					+"数据日期："+dataMap.get("STATUS_DATE")+ GlobalConstant.COMMA;
			excelValidDto.setValidKey(validKey);
			excelValidDto.setRowNum(String.valueOf(i));
			excelValidDto.setDataTime(com.joyintech.framework.util.DateUtil.parse(dataMap.get("UPDATE_TIME")));
			excelValidDto.setIsLogicCheck(DictCodeList.是否_是.getValue());
			excelValidDto.setOrgCode(dataMap.get("ISSUE_PARTY_ID"));
			excelValidDto.setCreateDept(dataMap.get("CREATE_DEPT"));
			excelValidDto.setCheckResult("02");
			String erro=dataMap.get("ERRMSG");
			if(StringUtil.isNotEmpty(erro)){
				excelValidDto.setErrorInfo(erro);
				result.add(excelValidDto);
			}else{
				//excelValidDto.setValidKey("产品代码:"+dataMap.get("PROD_ID"));
				excelValidDto.setCheckResult("01");
				result.add(excelValidDto);
			}
			i++;
		}
		returnMap.put("excelValidDtoList",result);
		returnMap.put("resultList",list);
		return  returnMap;
	}



	/**end    中债一二期报送直连  add by    wangzehui  20201012 */
	 /**
     * {@inheritDoc}
     * 增加日期字段转换
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
		//add by tuqirong 2022-01-18 start 导出单元格格式被覆盖问题修改
    	if (cell.getRow().getRowNum() > 2) {
			CellStyle cStyle = cell.getRow().getSheet().getWorkbook().createCellStyle();
			int index = cell.getRow().getSheet().getRow(1).getPhysicalNumberOfCells();
			for (int i = 0; i < index; i++) {
				cStyle.cloneStyleFrom(cell.getRow().getSheet().getRow(2).getCell(cell.getColumnIndex()).getCellStyle());
				if (i == cell.getColumnIndex()) {
					cell.setCellStyle(cStyle);
				}else {
					if (!StringUtil.isNotEmpty(cell.getRow().getCell(i))) {
						cell.getRow().createCell(i).setCellStyle(cStyle);
					}
				}
			}

		}
		//add by tuqirong 2022-01-18 end  导出单元格格式被覆盖问题修改
    	if(StringUtil.startsWith(propName, "equityQuit")||StringUtil.startsWith(propName, "firstExerciseDate")) {
        	if(value instanceof Long){
				Date cellValue = new Date((long) value);
				cell.setCellValue(DateUtil.format(cellValue,"yyyyMMdd"));
			}
        }
    }
}

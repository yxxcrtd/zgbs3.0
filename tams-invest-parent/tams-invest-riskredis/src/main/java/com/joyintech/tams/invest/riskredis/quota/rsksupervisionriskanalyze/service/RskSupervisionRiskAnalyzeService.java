package com.joyintech.tams.invest.riskredis.quota.rsksupervisionriskanalyze.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.riskredis.quota.rsksupervisionriskanalyze.dto.RskSupervisionRiskAnalyzeDto;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: tams-invest-parent
 * @description: RskSupervisionRiskAnalyzeService
 * @author: 裴家俊
 * @create: 2019-10-01
 **/
@Service
public class RskSupervisionRiskAnalyzeService extends CommonExportService<RskSupervisionRiskAnalyzeDto> {
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(RskSupervisionRiskAnalyzeService.class);

	/*@Autowired
	private RoleCommonService roleCommonService;*/

	@Autowired
	private FinProductMapper finProductMapper;

    /**
     * 查询界面询事件
     *
     * @param map 页面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<RskSupervisionRiskAnalyzeDto> selectForPage(Map<String, Object> map){
		/*if(StringUtil.isNotEmpty(CacheUtil.getSysParam(BussinessConstantRds.PTL_AUTHORITY_SWITCH))&&BussinessDictCodeListRds.是或否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstantRds.PTL_AUTHORITY_SWITCH))){
			User secUser=(User)SecurityUtil.getLoginUser();
			if(StringUtil.isNotEmpty(secUser)) {
				map.put("loginUserName", secUser.getUid());
				List<String> roles=roleCommonService.findRolePos(secUser.getUserName());
				if(!StringUtil.isEmptyList(roles)&&StringUtil.equals(roles.get(0),BussinessDictCodeListRds.角色岗位_风险岗.getValue())){
					map.remove("loginUserName");
				}
			}
		}*/
		if (StringUtil.isNotEmpty(map.get("prodManager"))) {
			map.put("prodManager", StringUtil.splitToList(null, map.get("prodManager").toString()));
		}else{
			map.remove("prodManager");
		}
        Page<RskSupervisionRiskAnalyzeDto> page = restTemplate.postForPage(
                ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionRiskAnalyze/findPage",
                map, RskSupervisionRiskAnalyzeDto.class);
        return page;
    }
    
    /**
     * 查询界面初始化事件
     *
     * @param map 页面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<RskSupervisionRiskAnalyzeDto> selectForPageInit(Map<String, Object> map){
		/*if(StringUtil.isNotEmpty(CacheUtil.getSysParam(BussinessConstantRds.PTL_AUTHORITY_SWITCH))&&BussinessDictCodeListRds.是或否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstantRds.PTL_AUTHORITY_SWITCH))){
			User secUser=(User)SecurityUtil.getLoginUser();
			if(StringUtil.isNotEmpty(secUser)) {
				map.put("loginUserName", secUser.getUid());
				List<String> roles=roleCommonService.findRolePos(secUser.getUserName());
				if(!StringUtil.isEmptyList(roles)&&StringUtil.equals(roles.get(0),BussinessDictCodeListRds.角色岗位_风险岗.getValue())){
					map.remove("loginUserName");
				}
			}
		}*/
        Page<RskSupervisionRiskAnalyzeDto> page = restTemplate.postForPage(
                ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionRiskAnalyze/findPageInit",
                map, RskSupervisionRiskAnalyzeDto.class);
        return page;
    }

    /**
     * 查询界面询事件
     *
     * @param map 页面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<RskSupervisionRiskAnalyzeDto> selectForPageMx(Map<String, Object> map){
        Page<RskSupervisionRiskAnalyzeDto> page = restTemplate.postForPage(
                ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionRiskAnalyze/findPageMx",
                map, RskSupervisionRiskAnalyzeDto.class);
		for (RskSupervisionRiskAnalyzeDto dto:page.getRoot()){
			FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getAssetCode());
			if (StringUtil.isNotEmpty(finProduct)){
				dto.setIsShow(DictCodeList.是否_是.getValue());
			}
		}
        return page;
    }

    /**
     * 查询界面询事件
     *
     * @param map 页面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<RskSupervisionRiskAnalyzeDto> selectForPageFu(Map<String, Object> map){
        Page<RskSupervisionRiskAnalyzeDto> page = restTemplate.postForPage(
                ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionRiskAnalyze/findPageFu",
                map, RskSupervisionRiskAnalyzeDto.class);
        return page;
    }
    
    /**
     * 查询界面询事件
     *
     * @param map 页面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<RskSupervisionRiskAnalyzeDto> selectForPageNx(Map<String, Object> map){
        Page<RskSupervisionRiskAnalyzeDto> page = restTemplate.postForPage(
                ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionRiskAnalyze/findPageNx",
                map, RskSupervisionRiskAnalyzeDto.class);
        return page;
    }

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
    	
    	Map<String,Object> selectParam=exportParam.getSelectParam();
    	if(StringUtil.isNotEmpty(selectParam.get("exportFlag"))&&"03".equals(selectParam.get("exportFlag").toString())){
	        exportParam.config(
	                "/com/joyintech/tams/invest/riskredis/quota/rsksupervisionriskanalyze/conf/export_dtl_conf.xml").dataSource(
	                DataSourceType.MASERT);
    	}
    	else if(StringUtil.isNotEmpty(selectParam.get("exportFlag"))&&"04".equals(selectParam.get("exportFlag").toString())){
 		 	 exportParam.config(
	                "/com/joyintech/tams/invest/riskredis/quota/rsksupervisionriskanalyze/conf/export_foul_conf.xml").dataSource(
	                DataSourceType.MASERT);
   	}
    	else if(StringUtil.isNotEmpty(selectParam.get("exportFlag"))&&"05".equals(selectParam.get("exportFlag").toString())){
  		 	 exportParam.config(
	                "/com/joyintech/tams/invest/riskredis/quota/rsksupervisionriskanalyze/conf/export_dtl_conf2.xml").dataSource(
	                DataSourceType.MASERT);
    	}
    	else {
    		 exportParam.config(
 	                "/com/joyintech/tams/invest/riskredis/quota/rsksupervisionriskanalyze/conf/export_conf.xml").dataSource(
 	                DataSourceType.MASERT);
    	}
    	
    }
    /**
     * 设置日期格式
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(("supDate".equals(propName)) && value != null){
			cell.setCellValue(DateUtil.format((Date)value));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RskSupervisionRiskAnalyzeDto> selectPageData(Map<String, Object> selectParam) {
        try {
        	Page<RskSupervisionRiskAnalyzeDto> pageList=new Page<RskSupervisionRiskAnalyzeDto>();
			if(StringUtil.isNotEmpty(selectParam.get("exportFlag"))&&"03".equals(selectParam.get("exportFlag").toString())){
	        	pageList= selectForPageMx(selectParam);
	        	
        	}
			else if(StringUtil.isNotEmpty(selectParam.get("exportFlag"))&&"04".equals(selectParam.get("exportFlag").toString())){
	        	pageList= selectForPageFu(selectParam);
	        	
        	}
			else if(StringUtil.isNotEmpty(selectParam.get("exportFlag"))&&"05".equals(selectParam.get("exportFlag").toString())){
	        	pageList= selectForPageNx(selectParam);
	        	
        	}
			else{
				pageList=selectForPage(selectParam);
			}
			for(RskSupervisionRiskAnalyzeDto dto:pageList.getRoot()) {
				if(null!=dto.getSupVal())
					dto.setSupVal(transferSupVal(dto.getSupVal(),dto.getFoulCond(),dto.getIndType()));
				if(null!=dto.getFoulCond())
					dto.setFoulCond(transferWarnFoul(dto.getFoulCond(),dto.getIndType()));
				//start 
				if(null!=dto.getWarnCond())
					dto.setWarnCond(transferWarnFoul(dto.getWarnCond(),dto.getIndType()));
				if(null!=dto.getApproveCond())
					dto.setApproveCond(transferWarnFoul(dto.getApproveCond(),dto.getIndType()));
				//end
				
			}
			return pageList;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
    }

    public String transferSupVal(String value,String foulValue,String indCellType) throws IOException {
    	if(!StringUtil.isNotEmpty(value)){
    		return value;
    	}
    	String wg=value;
    	if(indCellType.indexOf("FORBID_")!=-1){

    	}
    	else{
    		ObjectMapper mapper = new ObjectMapper();
    		Map<?, ?>  detailJson= mapper.readValue(JsonSanitizer.sanitize(foulValue), Map.class);
        	Map<?, ?> vThreshold1Map = (Map<?, ?>)detailJson.get("threshold_1");
        	String curunit=valueConvertUnit((String)vThreshold1Map.get("unit"));
        	if("PERCENT".equals((String)vThreshold1Map.get("unit"))){
        		wg=ArithmeticUtil.round(BigDecimal.valueOf(Double.valueOf(wg)*100),4,RoundingMode.HALF_UP)+curunit;
        	}
        	else if("WRMB".equals((String)vThreshold1Map.get("unit"))){
        		wg=ArithmeticUtil.round(BigDecimal.valueOf(Double.valueOf(wg)/10000),6,RoundingMode.HALF_UP)+curunit;
        	}
        	else if("BP".equals((String)vThreshold1Map.get("unit"))){
        		wg=ArithmeticUtil.round(BigDecimal.valueOf(Double.valueOf(wg)*10000),4,RoundingMode.HALF_UP)+curunit;
        	}
        	else{
        		wg=ArithmeticUtil.round(BigDecimal.valueOf(Double.valueOf(wg)),6,RoundingMode.HALF_UP)+curunit;
        	}
    	}
    	return wg;
    }
    
    public String transferWarnFoul(String value,String indCellType) throws IOException {
    	String wg=StringUtil.EMPTY;
    	ObjectMapper mapper = new ObjectMapper();
		Map<?, ?>  detailJson= mapper.readValue(JsonSanitizer.sanitize(value), Map.class);
	
    	Map<?, ?> vThreshold1Map = (Map<?, ?>)detailJson.get("threshold_1");
		if (StringUtil.isNotEmpty(value)) {
			if (indCellType.indexOf("LIMIT_") != -1) {
				if (StringUtil.isNotEmpty(vThreshold1Map.get("value"))) {
					wg = (String)vThreshold1Map.get("op") + vThreshold1Map.get("value")
							+ valueConvertUnit((String)vThreshold1Map.get("unit"));
				}
			}
			else {
				/* 禁止型 */
				wg = StringUtil.isNotEmpty(vThreshold1Map.get("value_cn"))?vThreshold1Map.get("value_cn").toString().substring(0, vThreshold1Map.get("value_cn").toString().length() - 1):StringUtil.EMPTY;
			}
		}
    	return wg;
    }
    
    /**
     * 单位转换
     */
    private String valueConvertUnit(String value){
    	if(StringUtil.EMPTY.equals(value)){
    		return value;
    	}
    	// ZGZGST-1644:对无单位进行特殊处理 fuyongsheng 20210828 add start
		if(BussinessDictCodeListRds.单位_无.getValue().equals(value)){
			return StringUtil.EMPTY;
		}
		// ZGZGST-1644:对无单位进行特殊处理 fuyongsheng 20210828 add end
    	String retStr=StringUtil.EMPTY;
    	DataDict dataDict = CacheUtil.getDictCode(BussinessDictCodeRds.单位.getValue());
    	List<? extends DataDictItem> itemList = dataDict.getItems();
    	for(DataDictItem v:itemList){
    		if(v.getItemCode().equals(value)){
    			retStr=v.getItemValue();
    			break;
    		}
    	};
    	return retStr;
    };

	public Page<SelectOptionDto> getUserName(Map<String, Object> map) {
		return restTemplate.postForPage(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionRiskAnalyze/getUserName", map,
				SelectOptionDto.class);
	}


	public Page<SelectOptionDto>  selectIndName(Map<String, Object> map) {
		return restTemplate.postForPage(
				ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionRiskAnalyze/selectIndName", map,
				SelectOptionDto.class);

	}

	public Page<SelectOptionDto> selectGroupName(Map<String, Object> map) {
		return restTemplate.postForPage(
				ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionRiskAnalyze/selectGroupName", map,
				SelectOptionDto.class);

	}

}

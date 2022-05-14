package com.joyintech.tams.invest.riskredis.quota.superviseindicator.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.json.JsonSanitizer;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;

import com.joyintech.tams.invest.riskredis.quota.superviseindicator.dto.RskPortfolioIndicatorDto;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.common.riskinfrds.indic.service.IndicatorService;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.dto.RskSupervisionIndicator002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 监督指标查询service 〈功能详细描述〉
 * 
 * @author 裴家俊
 * @version 1.0 2018年10月23日
 * @see RskSupervisionIndicator001Service
 * @since 1.0
 */
@Service
public class RskSupervisionIndicator001Service extends CommonExportService<RskSupervisionIndicator002Dto>
		implements BaseProcessService<RskSupervisionIndicator002Dto> {

	private String investType = "INVEST_NATURE";
	private String openType = "OPEN_TYPE";
	private String raiseType = "RAISE_TYPE";

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(RskSupervisionIndicator001Service.class);
	/**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisRriter;
    @Autowired
    private IndicatorService indicatorService;

	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private  Map<String ,Object> mapSuper=new HashMap<String,Object>();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doComplete(RskSupervisionIndicator002Dto entity, OperationType type) {
		 String action = StringUtil.EMPTY;
	        if (OperationType.DELETE.equals(type)) {
	            action = "delete";
	        }
	        else if (OperationType.NEW.equals(type)) {
	            action = "insert";
	        }
	        else {
	            action = "update";
	        }
	        restTemplate.postForObject(
	            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/" + action, entity,
	            Integer.class);
		String pageRedisFlag = CacheUtil.getSysParam("pageRedisFlag");
		if (CommonUtil.isType(pageRedisFlag, BussinessDictCodeListRds.是否_是)) {
			final String actionRedis=action;
			threadPoolTaskExecutor.execute(()->{
				try {
					logger.info("===========指标管理删除进入redis开始=============");
					Thread.sleep(6000);
					// 更新Redis
					ImplCond cond = new ImplCond();
					cond.setDel(StringUtil.equals(actionRedis, "delete"));
					cond.setIndicatorCodeArray(entity.getIndId());
					redisRriter.dealRiskIndicator(cond);
					logger.info("===========指标管理删除进入redis结束=============");
				} catch (InterruptedException e) {
					logger.error("===========指标管理删除进入redis异常=============");
				}

			});
		}

	}
	/**
	 * 插入操作
	 */
	public void doInsert(RskSupervisionIndicator002Dto entity ) {
		 	String action ="insert";
	        restTemplate.postForObject(
	            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/" + action, entity,
	            Integer.class);

	}
	
	/**
	 * 指标名称的联想下拉框 <功能详细描述>
	 * @param param
	 * @return SelectOptionDto
	 * @see
	 * @since 1.0
	 */
	public List<SelectOptionDto> getIndicatorParamList() {
		return restTemplate.getForList(
				ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/getIndicatorParamList?param=''",
				SelectOptionDto.class);
	}

	/**
	 * 动态获取禁投或可投项--add by jjpei 20190811---
	 * @param forbidEnum
	 * @return SelectOptionDto
	 * @see
	 * @since 1.0
	 */
	public List<SelectOptionDto> getForbidEnumInfo(String forbidEnum) {
		Map<String, Object> parameters=new HashMap<>();
		parameters.put("forbidEnum",forbidEnum);
		return restTemplate.postForList(
				ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/getForbidEnumInfo",parameters,
				SelectOptionDto.class);
	}

	/**
	 * 画面查询 <功能详细描述>
	 * 
	 * @param parameters
	 *            parameters
	 * @return RskSupervisionIndicatorDto
	 * @see
	 * @since 1.0
	 */
	public Page<RskSupervisionIndicator002Dto> selectForPage(Map<String, Object> parameters) {
		return restTemplate.postForPage(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/selectForPage",
				parameters, RskSupervisionIndicator002Dto.class);
	}

	/**
	 * 导出
	 */
	@Override
	protected void buildExportParam(ExportParam exportParam) {
		exportParam.config("/com/joyintech/tams/invest/riskredis/quota/superviseindicator/conf/export_conf.xml")
				.dataSource(DataSourceType.MASERT);
	}

	 /**
     * 设置预警与违规格式
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
       /* if(("warnCond".equals(propName)) && null!=value ){
        	 try {
				cell.setCellValue(transferWarnFoul(value,cell));
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
        }
        if(("foulCond".equals(propName)) && null!=value ){
            try {
				cell.setCellValue(transferWarnFoul(value,cell));
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
        }*/
    }
    
    private String transferWarnFoul(String value,String indCellType) throws IOException {
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
    	String retStr=StringUtil.EMPTY;
    	DataDict dataDict = CacheUtil.getDictCode(BussinessDictCodeRds.单位.getValue());
    	List<? extends DataDictItem> itemList = dataDict.getItems();
    	for(DataDictItem v:itemList){
    		if(v.getItemCode().equals(value)&&!StringUtil.equals(value, BussinessDictCodeListRds.单位_无.getValue())){
    			retStr=v.getItemValue();
    			break;
    		}
    	};
    	return retStr;
    };
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<RskSupervisionIndicator002Dto> selectPageData(Map<String, Object> selectParam) {
		try {
			Page<RskSupervisionIndicator002Dto> pageList=selectForPage(selectParam);
			for(RskSupervisionIndicator002Dto dto:pageList.getRoot()) {
				if(StringUtil.isNotEmpty(dto.getWarnCond()))
					dto.setWarnCond(transferWarnFoul(dto.getWarnCond(),dto.getIndType()));
				if(StringUtil.isNotEmpty(dto.getFoulCond()))
					dto.setFoulCond(transferWarnFoul(dto.getFoulCond(),dto.getIndType()));
				if(StringUtil.isNotEmpty(dto.getApproveCond())){
					dto.setApproveCond(transferWarnFoul(dto.getApproveCond(),dto.getIndType()));
				}
			}
			return pageList;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<TreeNode> getAllPortfolioBySort(String type,String indId,String portfName,String profitType,String issueMode,String raiseTypeCond,String investTypeCond,String isFaceSingleInvestor) {

		List<DataDictItem> profitList = new ArrayList<DataDictItem>();
		List<TreeNode> result = new ArrayList<TreeNode>();
		List<TreeNode> treeProList = new ArrayList<TreeNode>();
		if (BussinessDictCodeListRds.分类维度_收益类型.getValue().equals(type)) {
			treeProList = restTemplate.getForList(
					ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/getAllPortfolioByProfit?indId=" + indId+"&portfName="+portfName+"&profitType="+profitType+"&issueMode="+issueMode+"&raiseTypeCond="+raiseTypeCond+"&investTypeCond="+investTypeCond+"&isFaceSingleInvestor="+isFaceSingleInvestor,
					TreeNode.class);
			profitList = (List<DataDictItem>) SysDataUtil.getDataDictItem(BussinessDictCodeRds.收益类型.getValue());

		} else {
			if (BussinessDictCodeListRds.分类维度_开放类型.getValue().equals(type)) {
				treeProList = restTemplate.getForList(ServiceApplication.RISK_RDS_SERVICE_NAME
						+ "/RskSupervisionIndicator/getAllPortfolioByOther?fields=" + openType+"&indId=" + indId+"&portfName="+portfName+"&profitType="+profitType+"&issueMode="+issueMode+"&raiseTypeCond="+raiseTypeCond+"&investTypeCond="+investTypeCond+"&isFaceSingleInvestor="+isFaceSingleInvestor, TreeNode.class);
				profitList = (List<DataDictItem>) SysDataUtil.getDataDictItem(BussinessDictCodeRds.开放类型.getValue());
			}
			if (BussinessDictCodeListRds.分类维度_募集方式.getValue().equals(type)) {
				treeProList = restTemplate.getForList(ServiceApplication.RISK_RDS_SERVICE_NAME
						+ "/RskSupervisionIndicator/getAllPortfolioByOther?fields=" + raiseType+"&indId=" + indId+"&portfName="+portfName+"&profitType="+profitType+"&issueMode="+issueMode+"&raiseTypeCond="+raiseTypeCond+"&investTypeCond="+investTypeCond+"&isFaceSingleInvestor="+isFaceSingleInvestor, TreeNode.class);
				profitList = (List<DataDictItem>) SysDataUtil.getDataDictItem(BussinessDictCodeRds.募集方式.getValue());
			}
			if (BussinessDictCodeListRds.分类维度_投资性质.getValue().equals(type)) {
				treeProList = restTemplate.getForList(ServiceApplication.RISK_RDS_SERVICE_NAME
						+ "/RskSupervisionIndicator/getAllPortfolioByOther?fields=" + investType+"&indId=" + indId+"&portfName="+portfName+"&profitType="+profitType+"&issueMode="+issueMode+"&raiseTypeCond="+raiseTypeCond+"&investTypeCond="+investTypeCond+"&isFaceSingleInvestor="+isFaceSingleInvestor, TreeNode.class);
				profitList = (List<DataDictItem>) SysDataUtil.getDataDictItem(BussinessDictCodeRds.投资性质.getValue());
			}
		}
		if (profitList.size() > 0) {
			for (DataDictItem map : profitList) {
				TreeNode treeDto = new TreeNode();
				treeDto.setId(map.getItemCode());
				treeDto.setName(map.getItemValue());
				treeDto.setpId("");
				treeDto.setOpen(true);
				result.add(treeDto);
			}
		}
		result.addAll(treeProList);
		return result;

	}
	
	@SuppressWarnings("rawtypes")
	public List<TreeNode> getAllPortfolioBag(String bagName,String indId) {
		List<TreeNode> treeProList = new ArrayList<>();
		treeProList = restTemplate.getForList(
				ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/getAllPortfolioBag?bagName=" + bagName+"&indId="+indId,
				TreeNode.class);
		return treeProList;

	}
	
	public void playOrstop(List<RskSupervisionIndicator002Dto> listEntity) {
		restTemplate.postForObject(
	            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/updateIsEnabled", listEntity,
	            Integer.class);
		String pageRedisFlag = CacheUtil.getSysParam("pageRedisFlag");
        if (CommonUtil.isType(pageRedisFlag, BussinessDictCodeListRds.是否_是)) {
        	for(RskSupervisionIndicator002Dto e:listEntity){
        	if(e.getIsEnabled()==BussinessDictCodeListRds.使用状态_启用.getValue()){
        		// 更新Redis
                ImplCond cond = new ImplCond();
                cond.setPortfIdArray(e.getIndId());
                redisRriter.dealRiskIndicator(cond);
        	}else{
        		// 更新Redis
                ImplCond cond = new ImplCond();
                cond.setIndicatorCodeArray(e.getIndId());
        		indicatorService.deleteInfoFromRedis(RedisKeyEnum.指标信息配置.key(),
                        cond.getIndicatorCodeArray().toArray(
                            new String[cond.getIndicatorCodeArray().size()]));
        	}
        	List<String> portfolioIdList = restTemplate.getForList(
    	            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndicator/getAllPortfolioId?indId="+ e.getIndId(),
    	            String.class);
        	// 更新Redis--组合关联指标
            ImplCond cond2 = new ImplCond();
            String[] prodIdList = portfolioIdList.toArray(
                    new String[portfolioIdList.size()]);
            cond2.setPortfIdArray(prodIdList);
            redisRriter.dealRiskPtlIndicator(cond2);
        	}
        }
	}
}

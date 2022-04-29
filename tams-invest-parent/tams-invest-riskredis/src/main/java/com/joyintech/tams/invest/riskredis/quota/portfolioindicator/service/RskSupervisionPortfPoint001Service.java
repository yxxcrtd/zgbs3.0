package com.joyintech.tams.invest.riskredis.quota.portfolioindicator.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;

import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.dto.RskPortfolioIndicator002Dto;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.constant.OperationType;

/**
 * 组合关联监督指标service 〈功能详细描述〉
 * 
 * @author 裴家俊
 * @version 1.0 2018年10月23日
 * @see RskSupervisionPortfPoint001Service
 * @since 1.0
 */
@Service
public class RskSupervisionPortfPoint001Service extends CommonExportService<RskPortfolioIndicator002Dto>
		implements BaseProcessService<RskPortfolioIndicator002Dto> {
	/**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisRriter;
    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(RskSupervisionPortfPoint001Service.class);

	@Override
	public void doComplete(RskPortfolioIndicator002Dto entity, OperationType type) {
		// TODO Auto-generated method stub
		
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
	public Page<RskPortfolioIndicator002Dto> selectForPage(Map<String, Object> parameters) {
		Page<RskPortfolioIndicator002Dto> page =  restTemplate.postForPage(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionPortfPoint/selectForPage",
				parameters, RskPortfolioIndicator002Dto.class);
		for (RskPortfolioIndicator002Dto dto : page.getRoot())
		{
			if(StringUtil.isNotEmpty(dto.getEndDate())){
				dto.setEffDateRange(DateUtil.format(dto.getStartDate())+"-"+DateUtil.format(dto.getEndDate()));
			}else{
				dto.setEffDateRange(DateUtil.format(dto.getStartDate())+"-");
			}
		}
		return page;
	}

	/**
	 * 指标棳树画面查询 <功能详细描述>
	 * 
	 * @param parameters
	 * @return RskSupervisionIndicatorDto
	 * @see
	 * @since 1.0
	 */
	public Page<RskPortfolioIndicator002Dto> selectTableIndForPage(Map<String, Object> parameters) {
		parameters.put("platDate", DateUtil.platDateFromCache());
		Page<RskPortfolioIndicator002Dto> page =  restTemplate.postForPage(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionPortfPoint/selectTableIndForPage",
				parameters, RskPortfolioIndicator002Dto.class);
		for (RskPortfolioIndicator002Dto dto : page.getRoot())
		{
			if(StringUtil.isNotEmpty(dto.getEndDate())){
				dto.setEffDateRange(DateUtil.format(dto.getStartDate())+"-"+DateUtil.format(dto.getEndDate()));
			}else{
				dto.setEffDateRange(DateUtil.format(dto.getStartDate())+"-");
			}
		}
		return page;
	}
	/**
	 * 导出
	 */
	@Override
	protected void buildExportParam(ExportParam exportParam) {
		Map<String,Object> selectParam=exportParam.getSelectParam();
		if(null!=selectParam.get("indexTab")&&"A".equals(selectParam.get("indexTab"))){
			exportParam.config("/com/joyintech/tams/invest/riskredis/quota/portfolioindicator/conf/export_conf2.xml")
					.dataSource(DataSourceType.MASERT);
		}else {
			exportParam.config("/com/joyintech/tams/invest/riskredis/quota/portfolioindicator/conf/export_conf.xml")
					.dataSource(DataSourceType.MASERT);
		}
		
	}

	 /**
     * {@inheritDoc}
     */
    @Override
    public Page<RskPortfolioIndicator002Dto> selectPageData(Map<String, Object> selectParam) {
    	try {
    		Page<RskPortfolioIndicator002Dto> pageList = new Page<>();
    		if(null!=selectParam.get("indexTab")&&"A".equals(selectParam.get("indexTab"))){
    			pageList = selectTableIndForPage(selectParam);
    		}else{
    			pageList=selectForPage(selectParam);
    		}
			for(RskPortfolioIndicator002Dto dto:pageList.getRoot()) {
				if(StringUtil.isNotEmpty(dto.getWarnCond()))
					dto.setWarnCond(transferWarnFoul(dto.getWarnCond(),dto.getIndType()));
				if(StringUtil.isNotEmpty(dto.getFoulCond()))
					dto.setFoulCond(transferWarnFoul(dto.getFoulCond(),dto.getIndType()));
				if(StringUtil.isNotEmpty(dto.getApproveCond())){
					dto.setApproveCond(transferWarnFoul(dto.getApproveCond(),dto.getIndType()));
				}
				if(StringUtil.isNotEmpty(dto.getEndDate())){
					dto.setEffDateRange(DateUtil.format(dto.getStartDate())+"-"+DateUtil.format(dto.getEndDate()));
				}else{
					dto.setEffDateRange(DateUtil.format(dto.getStartDate())+"-");
				}
			}
			return pageList;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
        return null;
    }
    private String transferWarnFoul(String value,String indCellType) throws IOException {
		String wg=StringUtil.EMPTY;
		ObjectMapper mapper = new ObjectMapper();
		Map<?, ?>  detailJson= mapper.readValue(value, Map.class);

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<TreeNode> getTreeIndicatorByIndType(String indType) {
		List<DataDictItem> profitList = new ArrayList<DataDictItem>();
		List<TreeNode> result = new ArrayList<TreeNode>();
		List<TreeNode> treeProList = new ArrayList<>();
		treeProList = restTemplate.getForList(
				ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionPortfPoint/getTreeIndicatorByIndType?indType=" + indType,
				TreeNode.class);
		profitList = (List<DataDictItem>) SysDataUtil.getDataDictItem(BussinessDictCodeRds.指标类型.getValue());
		if (profitList.size() > 0) {
			for (DataDictItem map : profitList) {
				if(StringUtil.isNotEmpty(indType)&&!StringUtil.equals(indType, map.getItemCode())){
					continue;
				}
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
	public void playPortfolio(Map<String, Object> parameters) {
		restTemplate.postForObject(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionPortfPoint/playPortfolio",
				parameters, Integer.class);
		String pageRedisFlag = CacheUtil.getSysParam("pageRedisFlag");
        if (CommonUtil.isType(pageRedisFlag, BussinessDictCodeListRds.是否_是)) {
			try {
				logger.info("===========组合启动风险进入redis开始=============");
				Thread.sleep(2000);
				// 更新Redis
				ImplCond cond = new ImplCond();
				cond.setPortfIdArray((String)parameters.get("portfolioId"));
				cond.setPlateDate(platformDateService.getSysDate());
				redisRriter.dealPortData(cond);
                logger.info("===========组合启动风险进入redis结束=============");
			} catch (InterruptedException e) {
				logger.error("===========组合启动风险进入redis异常=============",e);
			}
        }
	}
	// add by zhuzhen 20191128 组合关联指标删除时校验联合设置 start
	/**
	 * 根据组合代码，指标代码，查询个性化联合设置数据是否存在
	 * @param dto
	 * @return 
	 * @see
	 * @since 1.0
	 */
    public boolean selectIndividUnite(Map<String, Object> parameters) {
        return restTemplate.postForObject(
            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionPortfPoint/selectIndividUnite",
            parameters, boolean.class);
    }
    // add by zhuzhen 20191128 组合关联指标删除时校验联合设置 end
}

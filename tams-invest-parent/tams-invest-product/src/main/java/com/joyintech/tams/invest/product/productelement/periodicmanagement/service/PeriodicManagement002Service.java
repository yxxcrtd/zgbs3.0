package com.joyintech.tams.invest.product.productelement.periodicmanagement.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinBaseRuleInfoMapper;
import com.joyintech.tams.common.base.dao.FinCashCalcRuleMapper;
import com.joyintech.tams.common.base.dao.FinCashInfoMapper;
import com.joyintech.tams.common.base.dao.FinCashPayRuleMapper;
import com.joyintech.tams.common.base.dao.FinFloatCalcRuleDetailMapper;
import com.joyintech.tams.common.base.dao.FinFloatCalcRuleMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductAddMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.entity.FinBaseRuleInfo;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinFloatCalcRule;
import com.joyintech.tams.common.base.entity.FinFloatCalcRuleDetail;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.component.business.prod.dto.BussBenchMarkInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductFloatFeeDto;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.dto.PeriodicManagement001Dto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 期次管理-新建
 * 
 * @author zhaojintao
 */
@Service("PeriodicManagement002Service")
public class PeriodicManagement002Service extends CommonExportService<PeriodicManagement001Dto>
		implements BaseProcessService<PeriodicManagement001Dto> {

	@Autowired
	private FinFloatCalcRuleDetailMapper finFloatCalcRuleDetailMapper;

	@Autowired
	private FinFloatCalcRuleMapper finFloatCalcRuleMapper;

	@Autowired
	private FinCashCalcRuleMapper finCashCalcRuleMapper;

	@Autowired
	private FinTradeProductMapper finTradeProductMapper;

	@Autowired
	private FinTradeProductAddMapper finTradeProductAddMapper;

	@Autowired
	private FinCashInfoMapper finCashInfoMapper;

	@Autowired
	private FinCashPayRuleMapper finCashPayRuleMapper;
	
	@Autowired
	private FinBaseRuleInfoMapper finBaseRuleInfoMapper;
	@Autowired
	private IPlatformDate iPlatformDate;
	

	/**
	 * 期次管理-新建
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	public void doComplete(PeriodicManagement001Dto entity, OperationType type) {
		if(type.equals(OperationType.DELETE)){
			restTemplate.postForObject(
					ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/doCompeleteDelete", entity,
					Integer.class);
		}
		else if(type.equals(OperationType.MODIFY)){
		    restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/doCompeleteDelete", entity,
                Integer.class);
		    insertEntity(entity);
		}else if(type.equals(OperationType.NEW)){
		    insertEntity(entity);
        }
//		// 二级法人权限处理 WY
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            //创建机构代码
            String createInst =   MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
            entity.setCreateInst(createInst); 
            // 有查询权限的机构代码
            List<String> searchOrgCodeList = null;
            // 有编辑权限的机构代码
            List<String> editOrgCodeList = null;
            // 新建或修改时重新设置二级法人权限，删除时则清空
            if (!OperationType.DELETE.equals(type)) {
                //获取所有父级机构。  拥有查看权限
                searchOrgCodeList =  com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(createInst);
                // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
                editOrgCodeList = Arrays.asList(entity.getCreateInst());
            }
            // 记录二级法人权限
            insertDataAuthTypeInst(entity.getPeriodProdId(), searchOrgCodeList, editOrgCodeList,com.joyintech.tams.constant.DictCodeList.共通业务类型_期次管理.getValue());
        }  
	}

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity
     * @see
     * @since 1.0
     */
    private void insertEntity(PeriodicManagement001Dto entity) {
        // 生成交易类金融产品Entity
		FinTradeProduct finTradeProduct = this.genFinTradeProduct(entity);
		finTradeProductMapper.insertSelective(finTradeProduct);

		// 交易类金融产品附属Entity
		FinTradeProductAdd finTradeProductAdd = this.genFinTradeProductAdd(finTradeProduct);
		finTradeProductAddMapper.insertSelective(finTradeProductAdd);
		//modify by wm 2019-12-22 产品披露业绩基准当不是区间披露时也存储 start
//		if(StringUtil.equals(entity.getBussBenchMarkInfoDto().getPublishType(), DictCodeListExt1.是或否_否.getValue())){
//			FinBaseRuleInfo finBaseRuleInfo业绩基准 = this.genFinBaseRuleInfo业绩基准(entity);
//			finBaseRuleInfoMapper.insertSelective(finBaseRuleInfo业绩基准);
//		}
		FinBaseRuleInfo finBaseRuleInfo业绩基准 = this.genFinBaseRuleInfo业绩基准(entity);
		finBaseRuleInfoMapper.insertSelective(finBaseRuleInfo业绩基准);
		//modify by wm 2019-12-22 产品披露业绩基准当不是区间披露时也存储 end
		
		FinBaseRuleInfo finBaseRuleInfo浮动管理费 = this.genFinBaseRuleInfo浮动管理费(entity);
		finBaseRuleInfoMapper.insertSelective(finBaseRuleInfo浮动管理费);
		
		if (StringUtil.isNotEmpty(entity.getPeriodCashId())) {
			// 现金流信息entity
			FinCashInfo finCashInfo = new FinCashInfo();
			// 查询期限的现金流信息
			Map<String, Object> params = new HashMap<>();
			params.put("periodCashId", entity.getPeriodCashId());/* 期限现金流Id */
			params.put("platDate", DateUtil.format(DateUtil.platDateFromCache()));
			FinCashInfo finCashInfoTmp = finCashInfoMapper.selectByPrimaryKey(entity.getPeriodCashId());
			BeanUtil.copyPropertiesIgnoreNull(finCashInfo, finCashInfoTmp);
			finCashInfo.setCashId(CommonUtil.generateCashFlowCode(finTradeProduct.getFinprodId(),
					DictCodeListExt1.现金流业务类型_金融产品.getValue(), finTradeProduct.getBranch(),
					DictCodeListExt1.现金流二级类型_浮动管理费.getValue()));
			finCashInfo.setBranch(finTradeProduct.getBranch());
			finCashInfo.setFeeId(entity.getBussFinProductFloatFeeDto().getFeeId());
			//期次新建的时候类型存理财子产品
			finCashInfo.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());		//业务类型
			finCashInfoMapper.insertSelective(finCashInfo);

			// 现金流支付规则Entity
			FinCashPayRule finCashPayRule = new FinCashPayRule();
			// 查询期限的现金流计算规则
			FinCashPayRule finCashPayRuleTmp = restTemplate.postForObject(
					ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectCashPayRule",
					params, FinCashPayRule.class);
			if(finCashPayRuleTmp!=null){
				BeanUtil.copyPropertiesIgnoreNull(finCashPayRule, finCashPayRuleTmp);
				finCashPayRule.setCashId(finCashInfo.getCashId());
				finCashPayRule.setBranch(finTradeProduct.getBranch());
				finCashPayRule.setEffDate(DateUtil.platDateFromCache());
				finCashPayRule.setRemark(entity.getBussFinProductFloatFeeDto().getRemark());
				finCashPayRuleMapper.insertSelective(finCashPayRule);
			}

			// 现金流计算规则表Entity
			FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
			// 查询期限的现金流计算规则
			FinCashCalcRule finCashCalcRuleTmp = restTemplate.postForObject(
					ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectCashCalcRule",
					params, FinCashCalcRule.class);
			if(finCashCalcRuleTmp != null){
				BeanUtil.copyPropertiesIgnoreNull(finCashCalcRule, finCashCalcRuleTmp);
				finCashCalcRule.setCashId(finCashInfo.getCashId());
				finCashCalcRule.setBranch(finTradeProduct.getBranch());
				finCashCalcRule.setBasis(entity.getBussFinProductFloatFeeDto().getBasis());
				finCashCalcRule.setEffDate(DateUtil.platDateFromCache());
				finCashCalcRuleMapper.insertSelective(finCashCalcRule);
			}

			// 业绩报酬计算规则Entity
			// 查询期限的业绩报酬计算规则
			FinFloatCalcRule finFloatCalcRule = new FinFloatCalcRule();
			FinFloatCalcRule finFloatCalcRuleTmp = restTemplate.postForObject(
					ServiceApplication.PRODUCT_SERVICE_NAME
							+ "/PeriodicManagement/selectFinFloatCalcRule",
					params, FinFloatCalcRule.class);
			if(finFloatCalcRuleTmp != null){
				BeanUtil.copyPropertiesIgnoreNull(finFloatCalcRule, finFloatCalcRuleTmp);
				finFloatCalcRule.setCashId(finCashInfo.getCashId());
				finFloatCalcRule.setEffDate(DateUtil.platDateFromCache());
				finFloatCalcRuleMapper.insertSelective(finFloatCalcRule);
			}

			// 业绩报酬计算规则明细List
			// 查询期限的业绩报酬计算规则明细list
			for (FinFloatCalcRuleDetail finFloatCalcRuleDetail : entity.getFinFloatCalcRuleDetailList()) {
				finFloatCalcRuleDetail.setCashId(finCashInfo.getCashId());
				finFloatCalcRuleDetail.setEffDate(DateUtil.platDateFromCache());
				finFloatCalcRuleDetail.setBaseRuleId(finBaseRuleInfo浮动管理费.getBaseRuleId());
				if(finFloatCalcRuleDetail.getSpreadRate()!=null){
					finFloatCalcRuleDetail.setSpreadRate(finFloatCalcRuleDetail.getSpreadRate().divide(new BigDecimal(100)));
				}
				if(finFloatCalcRuleDetail.getChargeRatio()!=null){
					finFloatCalcRuleDetail.setChargeRatio(finFloatCalcRuleDetail.getChargeRatio().divide(new BigDecimal(100)));
				}
				//BS2.1.0BSTOZ-886 修复bug 期次管理-同一产品建第二条期次，复核的时候报系统异常 start 20191025 wangcheng  
				if(finFloatCalcRuleDetailMapper.updateByPrimaryKey(finFloatCalcRuleDetail)==0){
					finFloatCalcRuleDetailMapper.insert(finFloatCalcRuleDetail);
				}
				//BS2.1.0 /BSTOZ-886 修复bug 期次管理-同一产品建第二条期次，复核的时候报系统异常 end 20191025 wangcheng  
			}
		}
    }

	private FinBaseRuleInfo genFinBaseRuleInfo浮动管理费(PeriodicManagement001Dto entity) {
		FinBaseRuleInfo finBaseRuleInfo = new FinBaseRuleInfo();
		BeanUtil.copyPropertiesIgnoreNull(finBaseRuleInfo, entity.getBussFinProductFloatFeeDto());
		//业绩基准代码 = 期限产品代码 + 分支序号 + DictCodeListExt1.业绩基准类型_浮动管理费.getValue()
		finBaseRuleInfo.setBaseRuleId(CommonUtil.generateCashFlowCode(entity.getPeriodProdId(), null,
				Long.valueOf(entity.getBranch()), DictCodeListExt1.业绩基准类型_浮动管理费.getValue()));
		finBaseRuleInfo.setEffDate(DateUtil.platDateFromCache());
		finBaseRuleInfo.setFinprodId(entity.getPeriodProdId());
		finBaseRuleInfo.setBranch(Long.valueOf(entity.getBranch()));
		finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
		finBaseRuleInfo.setRemark(entity.getBussFinProductFloatFeeDto().getRemark());
		if(entity.getBussFinProductFloatFeeDto()!=null){
			if(entity.getBussFinProductFloatFeeDto().getRate()!=null){
				finBaseRuleInfo.setRate(entity.getBussFinProductFloatFeeDto().getRate().divide(new BigDecimal(100)));
			}
			if(entity.getBussFinProductFloatFeeDto().getSpreadRate()!=null){
				finBaseRuleInfo.setSpreadRate(entity.getBussFinProductFloatFeeDto().getSpreadRate().divide(new BigDecimal(100)));
			}
		}
		return finBaseRuleInfo;
	}

	private FinBaseRuleInfo genFinBaseRuleInfo业绩基准(PeriodicManagement001Dto entity) {
		FinBaseRuleInfo finBaseRuleInfo = new FinBaseRuleInfo();
		BeanUtil.copyPropertiesIgnoreNull(finBaseRuleInfo, entity.getBussBenchMarkInfoDto());
		//业绩基准代码 = 期限产品代码 + 分支序号 + DictCodeListExt1.业绩基准类型_产品披露.getValue()
		finBaseRuleInfo.setBaseRuleId(CommonUtil.generateCashFlowCode(entity.getPeriodProdId(), null,
				Long.valueOf(entity.getBranch()), DictCodeListExt1.业绩基准类型_产品披露.getValue()));
		finBaseRuleInfo.setEffDate(DateUtil.platDateFromCache());
		finBaseRuleInfo.setFinprodId(entity.getPeriodProdId());
		finBaseRuleInfo.setBranch(Long.valueOf(entity.getBranch()));
		finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.业绩基准类型_产品披露.getValue());
		if(entity.getBussBenchMarkInfoDto()!=null){
			if(entity.getBussBenchMarkInfoDto().getRate()!=null){
				finBaseRuleInfo.setRate(entity.getBussBenchMarkInfoDto().getRate().divide(new BigDecimal(100)));
			}
			if(entity.getBussBenchMarkInfoDto().getSpreadRate()!=null){
				finBaseRuleInfo.setSpreadRate(entity.getBussBenchMarkInfoDto().getSpreadRate().divide(new BigDecimal(100)));
			}
			if(entity.getBussBenchMarkInfoDto().getRateLimit()!=null){
				finBaseRuleInfo.setRateLimit(entity.getBussBenchMarkInfoDto().getRateLimit().divide(new BigDecimal(100)));
			}
			if(entity.getBussBenchMarkInfoDto().getRateLower()!=null){
				finBaseRuleInfo.setRateLower(entity.getBussBenchMarkInfoDto().getRateLower().divide(new BigDecimal(100)));
			}
		}
		return finBaseRuleInfo;
	}

	public FinTradeProductAdd genFinTradeProductAdd(FinTradeProduct entity) {
		FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
		finTradeProductAdd.setFinprodId(entity.getFinprodId());
		finTradeProductAdd.setFinprodType(entity.getFinprodType());
		finTradeProductAdd.setFinprodType2(entity.getFinprodType2());
		finTradeProductAdd.setBranch(entity.getBranch());
		return finTradeProductAdd;
	}

	public FinTradeProduct genFinTradeProduct(PeriodicManagement001Dto entity) {
		FinTradeProduct finTradeProduct = new FinTradeProduct();
		finTradeProduct.setFinprodId(entity.getPeriodProdId());
		finTradeProduct.setPeriodId(entity.getPeriodId());
		finTradeProduct.setBranch(Long.valueOf(entity.getBranch()));
		finTradeProduct.setFinprodType(BussinessDictCodeList.金融产品类型_理财子产品.getValue());
		finTradeProduct.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_理财子产品.getValue());
		finTradeProduct.setProfitType(entity.getProfitType());
		finTradeProduct.setCcy(entity.getCcy());
		if(StringUtil.equals(entity.getTermType(), DictCodeListExt2.期限类型_多固定期限.getValue())){
			finTradeProduct.setTerm(Long.valueOf(entity.getInvestCycle()));
		}else{
			finTradeProduct.setTerm(Long.valueOf(entity.getInvestCycle2()));
		}
		finTradeProduct.setVdate(entity.getStartDate());
		finTradeProduct.setMdate(entity.getEndDate());
		return finTradeProduct;
	}

	/**
	 * 根据产品代码联动查询，基本信息
	 * 
	 * @param finprodId
	 * @return
	 */
	public PeriodicManagement001Dto selectInfoByfinprodId(String finprodId,String branch,String periodId) {
		PeriodicManagement001Dto dto = new PeriodicManagement001Dto();
		Map<String, Object> params = new HashMap<>();
		params.put("finprodId", finprodId);
		params.put("branch", branch);
		params.put("periodId", periodId); 
		List<PeriodicManagement001Dto> dtoListTmp = restTemplate.postForList(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectBaseInfoList", params,
				PeriodicManagement001Dto.class);
		if(!StringUtil.isEmptyList(dtoListTmp)){
			BeanUtil.copyPropertiesIgnoreNull(dto, dtoListTmp.get(0));
		}
		return dto;
	}

	/**
	 * 查询浮动管理费明细列表
	 * 
	 * @param map
	 * @return
	 */
	public List<FinFloatCalcRuleDetail> selectFloatFeeList(Map<String, Object> params) {
		params.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
		params.put("cashType2", DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
		params.put("type01", DictCodeListExt1.现金流业务类型_金融产品.getValue());
		params.put("effDate", iPlatformDate.getSysDateStr());
		List<FinFloatCalcRuleDetail> list = new ArrayList<>();
		if(StringUtil.isNotEmpty(params.get("periodProdId"))){
			params.put("type01", DictCodeListExt1.金融产品类型_理财子产品.getValue());
			if(!StringUtil.isNotEmpty(params.get("periodId"))) {
				params.put("type01", DictCodeListExt1.现金流业务类型_金融产品.getValue());
			}
			list = restTemplate.postForList(
					ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectFloatFeeList", params,
					FinFloatCalcRuleDetail.class);
		}
		return list;
	}

	/**
	 * 查询投资周期下拉框
	 * 
	 * @param params
	 * @return
	 */
	public Page<SelectOptionDto> getInvestCycle(Map<String, Object> params) {
		return restTemplate.postForPage(ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/getInvestCycle",
				params, SelectOptionDto.class);
	}

	public PeriodicManagement001Dto selectPeriodProdId(String finprodId, String investCycle) {
		Map<String, Object> params = new HashMap<>();
		params.put("finprodId", finprodId);
		params.put("investCycle", investCycle);
		return restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectPeriodProdId", params,
				PeriodicManagement001Dto.class);
	}
	//增加参数 解决 BSTOT-689 期次确认界面，浮动管理费部分字段没有联动出来； add by cxq 20200122
	public PeriodicManagement001Dto selectAllInfo(String periodProdId,String branch,String termType) {
		PeriodicManagement001Dto dto = new PeriodicManagement001Dto();
		Map<String, Object> params = new HashMap<>();
		params.put("periodProdId", periodProdId);
		params.put("platDate", DateUtil.format(DateUtil.platDateFromCache()));
		params.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
		params.put("cashType2", DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
		params.put("baseRuleType", DictCodeListExt1.业绩基准类型_产品披露.getValue());
		params.put("type01", DictCodeListExt1.现金流业务类型_金融产品.getValue());
		params.put("branch", branch); 
		BussBenchMarkInfoDto bussBenchMarkInfoDto = restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectBenchMarkInfo", params,
				BussBenchMarkInfoDto.class);
		if (!StringUtil.isNotEmpty(bussBenchMarkInfoDto)) {
		    params.put("branch", 0); 
	        bussBenchMarkInfoDto = restTemplate.postForObject(
	                ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectBenchMarkInfo", params,
	                BussBenchMarkInfoDto.class);
        }
		
		dto.setBussBenchMarkInfoDto(bussBenchMarkInfoDto);

		params.put("branch", 0); 
		BussFinProductFloatFeeDto bussFinProductFloatFeeDto = restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectFloatFeeInfo", params,
				BussFinProductFloatFeeDto.class);
		
		Map<String, Object> params2 = new HashMap<>();
		params2.put("periodProdId", periodProdId);
		params2.put("platDate", DateUtil.format(DateUtil.platDateFromCache()));
		params2.put("baseRuleType", DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
		if(StringUtil.equals(termType, DictCodeListExt2.期限类型_多固定期限.getValue())&& StringUtil.isNotEmpty(branch)) {
			params2.put("branch", Long.valueOf(branch));
		}
		else {
			params2.put("branch", 0);
		}
		BussBenchMarkInfoDto bussBenchMarkInfoDto浮动管理费 = restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectBenchMarkInfo", params2,
				BussBenchMarkInfoDto.class);
		if(bussFinProductFloatFeeDto!=null && bussBenchMarkInfoDto浮动管理费!=null){
			bussFinProductFloatFeeDto.setRate(bussBenchMarkInfoDto浮动管理费.getRate());
			bussFinProductFloatFeeDto.setRateType(bussBenchMarkInfoDto浮动管理费.getRateType());
			bussFinProductFloatFeeDto.setIndexId(bussBenchMarkInfoDto浮动管理费.getIndexId());
			bussFinProductFloatFeeDto.setCoefficient(bussBenchMarkInfoDto浮动管理费.getCoefficient());
			bussFinProductFloatFeeDto.setSpreadRate(bussBenchMarkInfoDto浮动管理费.getSpreadRate());
			bussFinProductFloatFeeDto.setResetType(bussBenchMarkInfoDto浮动管理费.getResetType());
			bussFinProductFloatFeeDto.setRemark(bussBenchMarkInfoDto浮动管理费.getRemark());
		}
		dto.setBussFinProductFloatFeeDto(bussFinProductFloatFeeDto);
		return dto;
	}

	public FinBaseRuleInfo queryFinBaseRuleInfo(PeriodicManagement001Dto entity, String value) {
		Map<String, Object> param = new HashMap<>();
		param.put("periodProdId", entity.getPeriodProdId());
		param.put("branch", Long.valueOf(entity.getBranch()));
		param.put("periodId", entity.getPeriodId());
		param.put("BASE_RULE_TYPE", value);
		param.put("effDate", DateUtil.format(entity.getEffDate(), DateUtil.DEFAULT_DATE_FORMAT_EN)); 
		return restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/queryFinBaseRuleInfo", param,
				FinBaseRuleInfo.class);
	}
	
}
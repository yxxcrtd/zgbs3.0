package com.joyintech.tams.invest.riskredis.quota.supervisionindividuation.service;

import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.common.base.dao.RskIndividConditionCustomMapper;
import com.joyintech.tams.common.base.entity.RskIndividConditionCustom;
import com.joyintech.tams.invest.riskredis.quota.supervisionindividuation.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
@Service
public class RskSupervisionIndividuation001Service extends BaseService<RskSupervisionIndividuationDto> 
implements BaseProcessService<RskSupervisionIndividuationDto> {
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(RskSupervisionIndividuation001Service.class);

    /**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisRriter;

	@Autowired
	private RskIndividConditionCustomMapper rskIndividConditionCustomMapper;

	public List<RskIndividConditionDto> findPageForCondition(Map<String, Object> pageParam) {
		if(StringUtil.isNotEmpty(pageParam.get("workflowId"))){
			RskSupervisionIndividuationDto dto = this.getBussinessEntityByWorkflowId((String)pageParam.get("workflowId"));
            if(dto!=null&&dto.getConditionList()!=null&&dto.getConditionList().size()>0){
            	return dto.getConditionList();
            }
			return new ArrayList<RskIndividConditionDto>();
		}
		return restTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/findPageForCondition",
				pageParam, RskIndividConditionDto.class);
	}

	@Override
	public void doComplete(RskSupervisionIndividuationDto entity, OperationType type) {
		String action = StringUtil.EMPTY;
		RskSupervisionIndividuationDto dto = getEntity(entity);
		if (OperationType.NEW.equals(type)) {
            action = "insert";
        }
		Map<String,Object > pageParam=new HashMap<>();
		pageParam.put("indId",entity.getIndId());
		pageParam.put("prtBagId",entity.getPrtBagId());
		List<RskIndividUniteDto> rskIndividUniteDtoList=restTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/findPageForUnite",
				pageParam, RskIndividUniteDto.class);
        restTemplate.postForObject(
            ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/" + action, dto,
            Integer.class);
        String pageRedisFlag = CacheUtil.getSysParam("pageRedisFlag");
        if (CommonUtil.isType(pageRedisFlag, BussinessDictCodeListRds.是否_是)) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(),e);
			}
            // 更新Redis
            ImplCond cond = new ImplCond();
            //联合设置时需要同步更改其它指标
            if(!StringUtil.isEmptyList(entity.getUniteList())){
				Set<String> uniteId=entity.getUniteList().stream().map(entry->entry.getIndId()).collect(Collectors.toSet());
				//这个rskIndividUniteDtoList是为了查之前的历史痕迹，以便做了删除操作，比如之前A绑定B,C，现在删除了C，更新A指标的同时也要更新C
            	if(!StringUtil.isEmptyList(rskIndividUniteDtoList)){
            		for(RskIndividUniteDto subDto:rskIndividUniteDtoList) {
						uniteId.addAll(Arrays.asList(subDto.getUniteIndId().split(",")));
					}
				}
				uniteId.add(entity.getIndId());
				cond.setIndicatorCodeArray(uniteId.toArray(new String[uniteId.size()]));
			}else {
            	//这个rskIndividUniteDtoList是为了查之前的历史痕迹，以便做了删除操作，比如之前A绑定B,C，现在删除了C，更新A指标的同时也要更新C
				if(!StringUtil.isEmptyList(rskIndividUniteDtoList)){
					Set<String> uniteId=new HashSet<>();
					for(RskIndividUniteDto subDto:rskIndividUniteDtoList) {
						uniteId.addAll(Arrays.asList(subDto.getUniteIndId().split(",")));
					}
					//因为rskIndividUniteDtoList这个值没有包含当前这条，需要把当前这条也加入
					uniteId.add(entity.getIndId());
					cond.setIndicatorCodeArray(uniteId.toArray(new String[uniteId.size()]));
				}else {
					cond.setIndicatorCodeArray(entity.getIndId());
				}
			}
            redisRriter.dealRiskIndicator(cond);

            //--------add by 裴家俊 2019-12-03 更新redis----start-----
			cond.setPortfIdArray(entity.getPrtBagId());
			redisRriter.dealRiskPtlIndicator(cond);
			//--------add by 裴家俊 2019-12-03 更新redis----start-----
        }
	}

	private RskSupervisionIndividuationDto getEntity(RskSupervisionIndividuationDto entity) {
		if(!StringUtil.isEmptyList(entity.getConditionList())){
        	for(RskIndividConditionDto e:entity.getConditionList()){
				//条件设置重置主键
				e.setConditionId(IDGenerator.generate());
        		e.setPrtBagId(entity.getPrtBagId());
        		e.setIndId(entity.getIndId());
        	}
        }
        if(!StringUtil.isEmptyList(entity.getSpecialAssetList())){
        	for(RskIndividSpecialAssetDto e:entity.getSpecialAssetList()){
				//特殊资产阶段重置主键
				e.setUniteId(IDGenerator.generate());
        		if(!StringUtil.isNotEmpty(e.getUniteType())){
        			e.setUniteType(BussinessDictCodeListRds.阶段类型_特殊资产阶段.getValue());
        		}
        		e.setIndId(entity.getIndId());
        		e.setPrtBagId(entity.getPrtBagId());
        	}
        }
        if(!StringUtil.isEmptyList(entity.getSpecialStageList())){
        	for(RskIndividSpecialStageDto e:entity.getSpecialStageList()){
				//特殊阶段重置主键
				e.setUniteId(IDGenerator.generate());
        		if(!StringUtil.isNotEmpty(e.getUniteType())){
        			e.setUniteType(BussinessDictCodeListRds.阶段类型_特殊阶段.getValue());
        		}
        		e.setIndId(entity.getIndId());
        		e.setPrtBagId(entity.getPrtBagId());
        	}
        }
        if(!StringUtil.isEmptyList(entity.getAssetDelList())){
        	for(RskIndividAssetDelDto e:entity.getAssetDelList()){
				//剔除资产重置主键
				e.setDelId(IDGenerator.generate());
//        		if(!StringUtil.isNotEmpty(e.getDelId())){
//        			e.setDelId(IDGenerator.generate());
//        		}
        	}
        }
		if(!StringUtil.isEmptyList(entity.getUniteList())){
			List<RskIndividUniteDto> listUnite = new ArrayList<RskIndividUniteDto>();
			for(RskIndividUniteDto e:entity.getUniteList()){
				//联合设置重置主键
				e.setUniteId(IDGenerator.generate());
//				if(!StringUtil.isNotEmpty(e.getUniteId())){
//        			e.setUniteId(IDGenerator.generate());
//        		}
				//获取选中的指标编号
				List<String> indIdList = StringUtil.splitToList(",", e.getUniteIndId());
				//获取自己的指标编号
				indIdList.add(entity.getIndId());
				for(String indId:indIdList){
					RskIndividUniteDto unite = new RskIndividUniteDto();
					BeanUtil.copyPropertiesIgnoreNull(unite, e);
					unite.setIndId(indId);
					unite.setPrtBagId(entity.getPrtBagId());
					listUnite.add(unite);
				}
			}
			entity.setUniteList(listUnite);
		}
		return entity;
	}

	public List<SelectOptionDto> queryUniteIndIdList(Map<String, Object> pageParam) {
		return restTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/queryUniteIndIdList",
				pageParam, SelectOptionDto.class);
	}

	public List<SelectOptionDto> queryUniteIndIdList_RULE(Map<String, Object> pageParam) {
		return simpleRestTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/queryUniteIndIdList_RULE",
				pageParam, SelectOptionDto.class);
	}

	public List<RskIndividUniteDto> findPageForUnite(Map<String, Object> pageParam) {
		if(StringUtil.isNotEmpty(pageParam.get("workflowId"))){
			RskSupervisionIndividuationDto dto = this.getBussinessEntityByWorkflowId((String)pageParam.get("workflowId"));
            if(dto!=null&&dto.getUniteList()!=null&&dto.getUniteList().size()>0){
            	return dto.getUniteList();
            }
			return new ArrayList<RskIndividUniteDto>();
		}
		return restTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/findPageForUnite",
				pageParam, RskIndividUniteDto.class);
	}

	public Page<SelectOptionDto> queryFinprodIdList(Map<String, Object> param) {
		return restTemplate.postForPage(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/queryFinprodIdList",
				param, SelectOptionDto.class);
	}

	public List<RskIndividAssetDelDto> findPageForAssetDel(Map<String, Object> pageParam) {
		if(StringUtil.isNotEmpty(pageParam.get("workflowId"))){
			RskSupervisionIndividuationDto dto = this.getBussinessEntityByWorkflowId((String)pageParam.get("workflowId"));
            if(dto!=null&&dto.getAssetDelList()!=null&&dto.getAssetDelList().size()>0){
            	return dto.getAssetDelList();
            }
			return new ArrayList<RskIndividAssetDelDto>();
		}
		return restTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/findPageForAssetDel",
				pageParam, RskIndividAssetDelDto.class);
	}

	public List<RskIndividSpecialStageDto> findPageForSpecial(Map<String, Object> pageParam) {
		if(StringUtil.isNotEmpty(pageParam.get("workflowId"))){
			RskSupervisionIndividuationDto dto = this.getBussinessEntityByWorkflowId((String)pageParam.get("workflowId"));
            if(dto!=null&&dto.getSpecialStageList()!=null&&dto.getSpecialStageList().size()>0){
            	return dto.getSpecialStageList();
            }
			return new ArrayList<RskIndividSpecialStageDto>();
		}
		return restTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/findPageForSpecial",
				pageParam, RskIndividSpecialStageDto.class);
	}

	public List<RskIndividSpecialAssetDto> findPageForSpecialAsset(Map<String, Object> pageParam) {
		if(StringUtil.isNotEmpty(pageParam.get("workflowId"))){
			RskSupervisionIndividuationDto dto = this.getBussinessEntityByWorkflowId((String)pageParam.get("workflowId"));
            if(dto!=null&&dto.getSpecialAssetList()!=null&&dto.getSpecialAssetList().size()>0){
            	return dto.getSpecialAssetList();
            }
            return new ArrayList<RskIndividSpecialAssetDto>();
		}
		return restTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/findPageForSpecialAsset",
				pageParam, RskIndividSpecialAssetDto.class);
	}

	public String getIndType(String indId) {
		return restTemplate.getForObject(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/getIndType?indId="+indId,
				String.class);
	}

	public Page<SelectOptionDto> getFinprodId(Map<String, Object> pageParam) {
		return restTemplate.postForPage(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/getFinprodId",
				pageParam, SelectOptionDto.class);
	}

	public RskIndividSpecialStageDto queryForSpecial(String uniteId) {
		return restTemplate.getForObject(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/queryForSpecial?uniteId="+uniteId,
				RskIndividSpecialStageDto.class);
	}

	public RskIndividSpecialAssetDto queryForAsset(String uniteId) {
		return restTemplate.getForObject(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/queryForAsset?uniteId="+uniteId,
				RskIndividSpecialAssetDto.class);
	}

	public String getVdate(String portfolioId) {
		return restTemplate.getForObject(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/getVdate?portfolioId="+portfolioId,
				String.class);
	}

    public List<RskIndividConditionCustomDto> findPageForCustomized(Map<String, Object> pageParam) {
		return restTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/findPageForCustomized",
				pageParam, RskIndividConditionCustomDto.class);
    }

	public RskIndividConditionCustom findRskIndividConditionCustom(String conditionItem) {
		return rskIndividConditionCustomMapper.selectByPrimaryKey(conditionItem);
	}

	public void save(RskIndividConditionCustomDto dto) {
		RskIndividConditionCustom rskIndividConditionCustom = new RskIndividConditionCustom();
		BeanUtil.copyPropertiesIgnoreNull(rskIndividConditionCustom,dto);
		if ("edit".equals(dto.getOperateType())){
			rskIndividConditionCustomMapper.updateByPrimaryKeySelective(rskIndividConditionCustom);
		}else if ("delete".equals(dto.getOperateType())){
			rskIndividConditionCustomMapper.deleteByPrimaryKey(rskIndividConditionCustom.getConditionItem());
		}else {
			rskIndividConditionCustomMapper.insertSelective(rskIndividConditionCustom);
		}
	}

	public int checkExistsUsing(RskIndividConditionCustomDto dto) {
		Map<String, Object> params = new HashMap<>();
		params.put("conditionItem",dto.getConditionItem());
		return restTemplate.postForObject(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/checkExistsUsing",
				params, Integer.class);
	}

	public Page<SelectOptionDto> queryConditionList(Map<String, Object> pageParam) {
		return restTemplate.postForPage(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/queryConditionList",
				pageParam, SelectOptionDto.class);
	}

	public List<RskIndividConditionLineDto> findPageForCustomizedItem(Map<String, Object> params) {
		if(StringUtil.isNotEmpty(params.get("workflowId"))){
			RskSupervisionIndividuationDto dto = this.getBussinessEntityByWorkflowId((String)params.get("workflowId"));
			if(dto!=null&&dto.getConditionLines()!=null&&dto.getConditionLines().size()>0){
				return dto.getConditionLines();
			}
			return new ArrayList<RskIndividConditionLineDto>();
		}
		return restTemplate.postForList(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionIndividuation/findPageForCustomizedItem",
				params, RskIndividConditionLineDto.class);
	}
}

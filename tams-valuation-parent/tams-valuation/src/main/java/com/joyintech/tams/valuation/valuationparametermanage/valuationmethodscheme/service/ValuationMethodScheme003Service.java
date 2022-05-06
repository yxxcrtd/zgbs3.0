/*
 * 文件名：ValuationMethodScheme003Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年8月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.service;

import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dao.ValuationMethodScheme003Mapper;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dto.ValuationMethodScheme003Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

import java.util.List;
import java.util.Map;

/**
 * 
 * 估值方法方案 - 资产分类估值
 * @author zhangwei
 * @version 1.0 2018年8月28日
 * @see ValuationMethodScheme003Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ValuationMethodScheme003Service extends CommonExportService<ValuationMethodScheme003Dto> implements BaseProcessService<ValuationMethodScheme003Dto>{

	@Autowired
	private ValuationMethodScheme003Mapper valuationMethodScheme003Mapper;
	
	@Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @DataSource(DataSourceType.OTHER)
	public void doComplete(ValuationMethodScheme003Dto entity, OperationType type) {
//		if(OperationType.NEW.equals(type)){
//			valuationMethodScheme003Mapper.insert(entity);
//		}else if(OperationType.MODIFY.equals(type)){
//		    valuationMethodScheme003Mapper.updateByPrimaryKeySelective(entity);
//        }else if(OperationType.DELETE.equals(type)) {
//            valuationMethodScheme003Mapper.deleteByPrimaryKey(entity.getSchemeDetailId());
//        }
		
		String action = StringUtil.EMPTY;
	   	 if (OperationType.DELETE.equals(type)) {
	            action = "delete";
	        }
	        else if (OperationType.NEW.equals(type)) {
	            action = "insert";
	        }
	        else if(OperationType.MODIFY.equals(type)){
	            action = "update";
	        }
	   	  restTemplate.postForObject(
	   	            ServiceApplication.VALUATION_SERVICE_NAME + "/ValuationMethodScheme003/" + action, entity,
	   	            Integer.class);
	}
	
	/**
	 * 通过方案详情代码查询 
	 * @param schemeId
	 * @return
	 */
	public ValuationMethodScheme003Dto findBySchemeDetailId(String schemeDetailId) {
//		return valuationMethodScheme003Mapper.selectByPrimaryKey(schemeDetailId);
		return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME
                + "/ValuationMethodScheme003/findBySchemeDetailId",
                schemeDetailId, ValuationMethodScheme003Dto.class);
	}

	/**
	 * 删除，支持批量删除
	 * @param schemeDetailId
	 * @return
	 */
	public int deleteByBatch(String schemeDetailId) {
		return valuationMethodScheme003Mapper.deleteByBatch(schemeDetailId);
	}

	/**
	 * 新增
	 * @param dto
	 */
	public void insert(ValuationMethodScheme003Dto dto) {
		valuationMethodScheme003Mapper.insert(dto);
	}

	/**
	 * 修改
	 * @param dto
	 */
	public void updateByPrimaryKeySelective(ValuationMethodScheme003Dto dto) {
		valuationMethodScheme003Mapper.updateByPrimaryKeySelective(dto);
	}


	/**
	 * @Description:根据证券品种+投资目的 处理估值方法下拉框集合
	 * @auther: yuzj
	 * @date: 2021年9月22日
	 * @param:
	 * @return:
	 */
	public List<SelectOptionDto> getResultByInvAimAndSecType(List<SelectOptionDto> linkSelectOption, Map<String, Object> param){
		//当投资目的为  收取合同现金流量为目的[AC]时，估值方法默认只可选择摊余成本法；
		String invAim =param.get("invAim").toString();
		String secType = param.get("secType").toString();
		if(StringUtil.equals(invAim,DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())){//FVTPL
			if(StringUtil.equals(secType,DictCodeListExt1.证券品种_债券.getValue())
					|| StringUtil.equals(secType,DictCodeListExt1.证券品种_其他债权类.getValue())){
				SelectOptionDto zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_市值法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_市值法.getCodeName());
				linkSelectOption.add(zqOptionDto);
				zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_成本法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_成本法.getCodeName());
				linkSelectOption.add(zqOptionDto);
				zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_摊余成本法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_摊余成本法.getCodeName());
				linkSelectOption.add(zqOptionDto);
				zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_模型法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_模型法.getCodeName());
				linkSelectOption.add(zqOptionDto);
			}else if(StringUtil.equals(secType,DictCodeListExt1.证券品种_股票.getValue())
					|| StringUtil.equals(secType,DictCodeListExt1.证券品种_计划类资产_净值型.getValue())
					|| StringUtil.equals(secType,DictCodeListExt1.证券品种_计划类资产_货币型.getValue())){
				SelectOptionDto zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_市值法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_市值法.getCodeName());
				linkSelectOption.add(zqOptionDto);
			}else if(StringUtil.equals(secType,DictCodeListExt1.证券品种_计划类资产_收益型.getValue())){
				SelectOptionDto zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_市值法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_市值法.getCodeName());
				linkSelectOption.add(zqOptionDto);
				zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_成本法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_成本法.getCodeName());
				linkSelectOption.add(zqOptionDto);
			}
		}else if(StringUtil.equals(invAim,DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())){//FVOCI
			if(StringUtil.equals(secType,DictCodeListExt1.证券品种_债券.getValue())){
				SelectOptionDto zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_市值法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_市值法.getCodeName());
				linkSelectOption.add(zqOptionDto);
				zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_摊余成本法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_摊余成本法.getCodeName());
				linkSelectOption.add(zqOptionDto);
				zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_模型法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_模型法.getCodeName());
				linkSelectOption.add(zqOptionDto);
			}else if(StringUtil.equals(secType,DictCodeListExt1.证券品种_计划类资产_收益型.getValue())){
				SelectOptionDto zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_市值法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_市值法.getCodeName());
				linkSelectOption.add(zqOptionDto);
				zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_摊余成本法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_摊余成本法.getCodeName());
				linkSelectOption.add(zqOptionDto);
			}
		}else if(StringUtil.equals(invAim,DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())){ //AC
			if(StringUtil.equals(secType,DictCodeListExt1.证券品种_债券.getValue())
			      || StringUtil.equals(secType,DictCodeListExt1.证券品种_其他债权类.getValue())
				  || StringUtil.equals(secType,DictCodeListExt1.证券品种_计划类资产_收益型.getValue())){
				SelectOptionDto zqOptionDto = new SelectOptionDto();
				zqOptionDto.setId(DictCodeListExt1.估值方法_摊余成本法.getValue());
				zqOptionDto.setText(DictCodeListExt1.估值方法_摊余成本法.getCodeName());
				linkSelectOption.add(zqOptionDto);
			}
		}

		return  linkSelectOption;
	}


	/**
	 * 通用联动查询
	 * @param param : dict_code_parent,dict_code_child,item_code_parent
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@DataSource(DataSourceType.OTHER)
	public List<SelectOptionDto> getLinkSelectOption(Map<String, Object> param) {
		return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME
						+ "/ValuationMethodScheme001/getLinkSelectOption",
				param, SelectOptionDto.class);
	}
	




}

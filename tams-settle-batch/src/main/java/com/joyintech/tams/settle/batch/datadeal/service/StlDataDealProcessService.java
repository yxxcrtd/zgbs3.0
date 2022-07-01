package com.joyintech.tams.settle.batch.datadeal.service;

import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealOperatorDto;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealWriteDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 数据清算step2 processor Service
 * 
 * @author 陈卓
 * @date 2020/7/9
 * @see StlDataDealProcessService
 * @since 1.0
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StlDataDealProcessService {
	
	@Autowired
	protected StlDataDealSzdSjsmxProcessService stlDataDealSzdSjsmxProcessService;

	@Autowired
	private StlDataDealSzdZqbdProcessService stlDataDealSzdZqbdProcessService;

	@Autowired
	private StlDataDealSzdSjsjgProcessService stlDataDealSzdSjsjgProcessService;

	@Autowired
	private StlDataDealSzdSjsfxProcessService stlDataDealSzdSjsfxProcessService;

	@Autowired
	private StlDataDealSzdAbcsjProcessService stlDataDealSzdAbcsjProcessService;

	@Autowired
	protected StlDataDealSzdZsmxProcessService stlDataDealSzdZsmxProcessService;

	@Autowired
	protected StlDataDealSzdJsmxProcessService stlDataDealSzdJsmxProcessService;

	@Autowired
	protected StlDataDealSzdZqyProcessService stlDataDealSzdZqyProcessService;

	@Autowired
	protected StlDataDealSzdSjsksProcessService stlDataDealSzdSjsksProcessService;

	@Autowired
	protected StlDataDealSzdTzxxProcessService stlDataDealSzdTzxxProcessService;

	@Autowired
	protected StlDataDealSzdSjsgbProcessService stlDataDealSzdSjsgbProcessService;

	// 生成数据清算批处理step2 processor的结果Dto
	public StlDownDataDealWriteDto generatorStlDownDataDealWriteDto(StlDownDataDealOperatorDto stlDownDataDealOperatorDto) throws Exception {
		
		StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
		
		// 数据源【上中登JSMX】解析
		if (CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getJsmxList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdJsmxProcessService.generatorStlDownDataDealSzdJsmxWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【深中登SJSMX】解析
		if (CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsmxList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdSjsmxProcessService.generatorStlDownDataDealSzdSjsmxWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【深中登SJSJG】解析
		if (CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsjgList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdSjsjgProcessService.generatorStlDownDataDealSzdSjsjgWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【深中登SJSFX】解析
		if (CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsfxList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdSjsfxProcessService.generatorStlDownDataDealSzdSjsfxWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【上中登ZQBD】解析
		if (CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZqbdList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdZqbdProcessService.generatorStlDownDataDealSzdZqbdWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【上中登ABCSJ】解析
		if(CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getAbcsjList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdAbcsjProcessService.generatorStlDownDataDealSzdAbcsjWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【深中登ZSMX】解析
		if(CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZsmxList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdZsmxProcessService.generatorStlDownDataDealSzdZsmxWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【上中登ZQY】解析
		if(CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSzdZqyList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdZqyProcessService.generatorStlDownDataDealSzdZqyWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【深中登SJSKS】解析
		if(CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSzdSjsksList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdSjsksProcessService.generatorStlDownDataDealSzdSjsksWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【上中登TZXX】解析
		if(CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSzdTzxxList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdTzxxProcessService.generatorStlDownDataDealSzdTzxxWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		// 数据源【深中登SJSGB】解析
		if(CollectionUtils.isNotEmpty(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSzdSjsgbList())) {
			StlDownDataDealWriteDto temp = stlDataDealSzdSjsgbProcessService.generatorStlDownDataDealSzdSjsgbWriteDto(stlDownDataDealOperatorDto);
			if (temp != null) {
				stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList().add(temp);
			}
		}
		
		return stlDownDataDealWriteDto;
	}
}

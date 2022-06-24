/*
 * 文件名：TaxScheme006Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年10月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.taxparametermanage.taxscheme.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dao.TaxScheme006Mapper;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dto.TaxScheme006Dto;
import com.joyintech.tams.common.base.dao.BokSchemeConfigMapper;
import com.joyintech.tams.common.base.entity.BokSchemeConfig;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 附加税则表
 * @author zhangwei
 * @version 1.0 2018年10月16日
 * @see TaxScheme006Service
 * @since 1.0
 */
@Service(value="serviceTaxScheme006Service")
@Transactional(rollbackFor = Exception.class)
public class TaxScheme006Service extends CommonExportService<TaxScheme006Dto> implements BaseProcessService<TaxScheme006Dto> {

	@Autowired
	private TaxScheme006Mapper taxScheme006Mapper;
	
	/**
	 * 方案配置
	 */
	@Autowired
	private BokSchemeConfigMapper bokSchemeConfigMapper;
	
    /**
     * 流程审核完成时执行 {@inheritDoc}
     */
    @Override
    public void doComplete(TaxScheme006Dto entity, OperationType type) {
        
        if (OperationType.NEW.equals(type)) {
            this.insertSelective(entity);
        }
        else if (OperationType.MODIFY.equals(type)) {
            this.updateByPrimaryKeySelective(entity);
        }
        else if (OperationType.DELETE.equals(type)) {
            this.deleteByBatch(entity.getSeqId());
        }
    }
	
	
	/**
	 * 新建 附加税则表
	 * @param dto
	 * @return
	 */
	public int insertSelective(TaxScheme006Dto dto) {
		return taxScheme006Mapper.insertSelective(dto);
	}
	
	/**
	 * 查询 附加税则表
	 * @param seqNo
	 * @return
	 */
	public TaxScheme006Dto findByPrimaryKey(String seqNo) {
		return taxScheme006Mapper.selectByPrimaryKey(seqNo);
	}
	
	/**
	 * 修改 附加税则表
	 * @param dto
	 * @return
	 */
	public int updateByPrimaryKeySelective(TaxScheme006Dto dto) {
		return taxScheme006Mapper.updateByPrimaryKeySelective(dto);
	}

	/**
	 * 删除、批量删除 附加税则表
	 * @param seqNo
	 * @return
	 */
	public int deleteByBatch(String seqId) {
		int row = 0;
		for(String item: seqId.split(",")) {
			row += taxScheme006Mapper.deleteByPrimaryKey(item);
		}
		return row;
	}

    /**
     * 下拉框 - 项目
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> findProject(Map<String, Object> pageParam) {
        List<TaxScheme006Dto> resultList = taxScheme006Mapper.selectProject(pageParam);
        List<SelectOptionDto> selectOptionList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (TaxScheme006Dto item : resultList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(item.getConfigId()); 
                selectOptionDto.setText(item.getConfigName());
                selectOptionList.add(selectOptionDto);
            }
        }
        return selectOptionList;
    }

    /**
     * 下拉框 - 值
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public String findValue(Map<String, Object> pageParam) {
        BokSchemeConfig bokSchemeConfig = bokSchemeConfigMapper.selectByPrimaryKey((String) pageParam.get("configId"));
        if(bokSchemeConfig != null) {
            return bokSchemeConfig.getDictCode();
        }
        return StringUtils.EMPTY;
    }


	
	
}

package com.joyintech.service.risk.customerrelationtree.agencydetailsinf.service;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.risk.conf.DataSourceType;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.dao.AgencyDetailsInf001Mapper;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.dto.AgencyDetailsInf001Dto;


/**
 * 机构详细信息
 * @author wangkang
 * @version 1.0 2019年8月21日
 * @see AgencyDetailsInf001Service
 * @since 1.0
 */
@Service("backAgencyDetailsInf001Service")
public class AgencyDetailsInf001Service extends BaseService<AgencyDetailsInf001Dto> {
	
	@Autowired
	private AgencyDetailsInf001Mapper agencyDetailsInf001Mapper;
	
    /**
     * 持仓信息查询
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Page<AgencyDetailsInf001Dto> getPositionInf(int page, int length, Map<String, Object> pageParam) {
//        DynamicDataSourceHolder.putDataSource(DataSourceType.STRESSTEST);
        Page<AgencyDetailsInf001Dto> pag = super.queryForPage(page, length, () -> agencyDetailsInf001Mapper.getPositionInf(pageParam) );
//        DynamicDataSourceHolder.clearDataSource();
        return pag;
    }
    
    /**
     * 机构联想框
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAgency(int page, int length, Map<String, Object> pageParam) {
        String agencyId = (String) pageParam.get("search");
        return super.queryForPageForSelectReturn(page, length, () -> agencyDetailsInf001Mapper.getAgency(agencyId));
    }
    
    /**
     * 查询树数据
     * @param agencyId
     * @return
     * @see
     * @since 1.0
     */
    public List<AgencyDetailsInf001Dto> getTreeData(String agencyId) {
        return agencyDetailsInf001Mapper.getTreeData(agencyId);
    }
    
    /**
     * 删除机构
     * @param agencyId
     * @param pId
     * @return
     * @see
     * @since 1.0
     */
    public int delNode(String seqNo) {
        return agencyDetailsInf001Mapper.delNode(seqNo );
    }
}

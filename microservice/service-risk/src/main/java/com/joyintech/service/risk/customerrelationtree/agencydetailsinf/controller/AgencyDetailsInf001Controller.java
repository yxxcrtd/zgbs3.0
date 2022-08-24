/*
 * 文件名：AgencyDetailsInf001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2019年12月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.risk.customerrelationtree.agencydetailsinf.controller;


import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.dto.AgencyDetailsInf001Dto;
import com.joyintech.service.risk.customerrelationtree.agencydetailsinf.service.AgencyDetailsInf001Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 关联机构信息查询
 * @author wangkang
 * @version 1.0 2019年12月10日
 * @see AgencyDetailsInf001Controller
 * @since 1.0
 */
@RestController("backAgencyDetailsInf001Controller")
@RequestMapping("/AgencyDetailsInf001")
public class AgencyDetailsInf001Controller {

	/**
	 * agencyDetailsInf001Service
	 */
	@Autowired
	private AgencyDetailsInf001Service agencyDetailsInf001Service;

	/**
	 * 根据参数代码和参数值拼接sql
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @param map
	 * @return
	 * @see
	 * @since 1.0
	 */
	@PostMapping("findPage/{page}/{length}")
    public Page<AgencyDetailsInf001Dto> getPositionInf(@RequestBody Map<String, Object> param,
                                                       @PathVariable("page") int page, @PathVariable("length") int length) {
        return agencyDetailsInf001Service.getPositionInf(page, length, param);
    }
    
	/**
	 * 机构联想框
	 * @param agencyId
	 * @return
	 * @see
	 * @since 1.0
	 */
	@PostMapping("getAgency/{page}/{length}")
    public Page<SelectOptionDto> getAgency(@RequestBody Map<String, Object> param,
										   @PathVariable("page") int page, @PathVariable("length") int length) {
        return agencyDetailsInf001Service.getAgency(page, length, param);
    }
	
	/**
	 * 查询树数据
	 * @param agencyId
	 * @return
	 * @see
	 * @since 1.0
	 */
	@GetMapping("/getTreeData")
    public List<AgencyDetailsInf001Dto> getTreeData(String agencyId) {
        return agencyDetailsInf001Service.getTreeData(agencyId);
    }
	
	/**
	 * 删除机构
	 * @param agencyId
	 * @return
	 * @see
	 * @since 1.0
	 */
	@GetMapping("/delNode")
    public int delNode(String seqNo) {
        return agencyDetailsInf001Service.delNode(seqNo);
    }
	
	
	 
}

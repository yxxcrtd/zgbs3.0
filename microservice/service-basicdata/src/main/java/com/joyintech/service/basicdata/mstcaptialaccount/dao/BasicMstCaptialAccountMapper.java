/*
 * 文件名：MstCaptialAccountMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstcaptialaccount.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.joyintech.system.base.entity.SysWflBusinessList;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstcaptialaccount.dto.MstCaptialAccountDto;
import com.joyintech.service.basicdata.mstcaptialaccount.dto.MstCaptialAccountDto2;
import com.joyintech.tams.common.base.entity.StlCapacctReInfo;

/**
 * 
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年8月20日
 * @see BasicMstCaptialAccountMapper
 * @since 1.0
 */
@Repository
public interface BasicMstCaptialAccountMapper {
    /**
     * 
     * 模糊查询
     * <功能详细描述>
     * @param pageParam pageParam
     * @return List<MstCaptialAccountDto>
     * @see
     * @since 1.0
     */
    List<MstCaptialAccountDto> select(Map<String, Object> pageParam);
    List<MstCaptialAccountDto> selectCheck(Map<String, Object> pageParam);
	
    List<MstCaptialAccountDto2> selectForInterf(Map<String, Object> pageParam); 
    
    /**
     * 联想下拉框
     * <一句话功能简述>
     * <功能详细描述>
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    List<SuggestDto> getAcctName(Map<String, Object> map);
    
    /**
     * 
     * 删除
     * <功能详细描述>
     * @param dto 业务实体
     * @return int
     * @see
     * @since 1.0
     */
    int deleteByPrimaryKey(MstCaptialAccountDto dto);
    
    /**
     * 
     * 新建
     * <功能详细描述>
     * @param dto 业务实体
     * @return int
     * @see
     * @since 1.0
     */
    int insert(MstCaptialAccountDto dto);
    
    /**
     * 
     * 修改
     * <功能详细描述>
     * @param dto 业务实体
     * @return int
     * @see
     * @since 1.0
     */
    int updateByPrimaryKeySelective(MstCaptialAccountDto dto);
    
    /**
     * 
     * 根据网点账号与币种查询详细信息
     * <功能详细描述>
     * @param map map
     * @return MstCaptialAccountDto
     * @see
     * @since 1.0
     */
    MstCaptialAccountDto selectDetail(Map<String, Object> map);
    
    /**
     * 
     *删除时检查数据是否被使用过
     * <功能详细描述>
     * @param dto 业务实体
     * @return int
     * @see
     * @since 1.0
     */
    int checkCodeIsUse(MstCaptialAccountDto dto);
    
    /**
     * 
     *批量删除时检查数据是否被使用过
     * <功能详细描述>
     * @param item List<MstCaptialAccountDto>
     * @return List<MstCaptialAccountDto>
     * @see
     * @since 1.0
     */
    List<MstCaptialAccountDto> checkAllCodeIsUse(List<MstCaptialAccountDto> item);

    int updateTitle(Map<String, Object> map);

    int getUniqueAcctNoPayNo(Map<String, Object> map);
    
       /**
     * 
     * 查询当前节点信息
     * <功能详细描述>
     * @param workflowId
     * @return
     * @see
     * @since 1.0
     */
    SysWflBusinessList getSysWflBusinessList(String workflowId);
    
    /**
     * 获取利率
     * @param map 集合
     * @return 利率
     * @see
     * @since 1.0
     */
    BigDecimal getIndexIntRate(Map<String,Object> map);
    
    
    /**
     * 获取活期利率信息
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<MstCaptialAccountDto> getAccCurrentInterestList(Map<String,Object> map);
    
    
    /**
     * 
     * 获取所属营业部
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getOwnDept(Map<String,Object> map);
    
    /**
     * 
     * 资金账户关联删除
     * <功能详细描述>
     * @param map
     * @return 
     * @see
     * @since 1.0
     */
    void deleteStlCapacctReInfo(MstCaptialAccountDto mstCaptialAccountDto);
    
    /**
     * 查询账户管理表数据
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<StlCapacctReInfo> getStlCapacctReInfo(Map<String,Object> map);


    /**
     * add by xxg 20210726
     * 根据所属柜台获取第一次设置的利率
     * @param ownDept  所属柜台
     * @return
     */
    MstCaptialAccountDto getFirstRateByownDept(String ownDept);

    List<SelectOptionDto> getAcctInfo(Map<String,Object> param);

	}

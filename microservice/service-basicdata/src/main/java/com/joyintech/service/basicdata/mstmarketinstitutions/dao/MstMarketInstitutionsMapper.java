/*
 * 文件名：MstMarketInstitutionsMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstmarketinstitutions.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.common.base.entity.InterfCmsInstitution;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstmarketinstitutions.dto.MstMarketInstitutionsDto;
/**
 * 
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年8月20日
 * @see MstMarketInstitutionsMapper
 * @since 1.0
 */
@Repository
public interface MstMarketInstitutionsMapper {
    
    /**
     * 客户代码联想下拉框
     * <一句话功能简述>
     * <功能详细描述>
     * @param map map
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    List<SuggestDto> getCustomerId(Map<String, Object> map);
    
    /**
     * 客户机构联想下拉框
     * <一句话功能简述>
     * <功能详细描述>
     * @param map map
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    List<SuggestDto> getCustomerName(Map<String, Object> map);
    
    /**
     * 
     * 模糊查询
     * <功能详细描述>
     * @param pageParam pageParam
     * @return List<MstMarketInstitutionsDto>
     * @see
     * @since 1.0
     */
    List<MstMarketInstitutionsDto> select(Map<String, Object> pageParam);
    /**
     * 
     * 查看编辑页面表格查询
     * <功能详细描述>
     * @param pageParam 客户代码customerId
     * @return List<PaRskCustomerRating>
     * @see
     * @since 1.0
     */
    List<MstBondCreditRating> selectMstBondCreditPage(Map<String, Object> pageParam);
    
    /**
     * 根据客户代码查询市场机构详细信息
     * <一句话功能简述>
     * <功能详细描述>
     * @param map map
     * @return MstMarketInstitutionsDto
     * @see
     * @since 1.0
     */
    MstMarketInstitutionsDto selectDetail(Map<String, Object> map);
    
    /**
     * 
     *删除时检查数据是否被使用过
     * <功能详细描述>
     * @param dto MstMarketInstitutionsDto
     * @return int
     * @see
     * @since 1.0
     */
    int checkCodeIsUse(MstMarketInstitutionsDto dto);
    
    /**
     * 
     *批量删除时检查数据是否被使用过
     * <功能详细描述>
     * @param item List<MstMarketInstitutionsDto> item
     * @return List<MstMarketInstitutionsDto>
     * @see
     * @since 1.0
     */
    List<MstMarketInstitutionsDto> checkAllCodeIsUse(List<MstMarketInstitutionsDto> item);
    
    /**
     * 
     * 通过客户代码删除客户评级表信息 
     * <功能详细描述>
     * @param customerId String
     * @return int
     * @see
     * @since 1.0
     */
    int deleteMstBondCreditRatingByPrimaryKey(String customerId);
    /**
     * 
     * 通过客户代码查询客户评级表信息 
     * <功能详细描述>
     * @param customerId String
     * @return List<PaRskCustomerRating>
     * @see
     * @since 1.0
     */
    List<MstBondCreditRating> selectMstBondCreditRatingByPrimaryKey(String customerId);
    
    /**
     * 
     * 省市县联动
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getAddressSelectOption(Map<String, Object> param);
    
    /**
     * 
     * 查询所有的市场机构代码、名称
     * <功能详细描述>
     * @return MstMarketInstitutionsDto
     * @see
     * @since 1.0
     */
    List<MstMarketInstitutionsDto> customerNameCheck(Map<String, Object> param);
    
    /**
     * 
     * 根据becif查询CMS_机构信息
     * <功能详细描述>
     * @param becif String
     * @return InterfCmsInstitution
     * @see
     * @since 1.0
     */
    InterfCmsInstitution selectInterfCmsInstitution(String becif);

    /**
     * 获取评级机构
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
	List<SuggestDto> getGradeOrgId(Map<String, Object> param);

    /**
     * 
     * 批量删除时检查数据是否进行过客户性质匹配
     * <功能详细描述>
     * @param item List<MstMarketInstitutionsDto> 
     * @return int
     * @see
     * @since 1.0
     */
    List<MstMarketInstitutionsDto> deleteCheck(List<MstMarketInstitutionsDto> item);

    /**
     * 根据信用代码查询客户信息
     *
     * @param insitituteCode
     * @return
     */
    MstMarketInstitutionsDto getByInsitituteCode(@Param("insitituteCode") String insitituteCode);

    /**
     * 获取接口表中的数据
     *
     * @param insitituteCode
     * @return
     */
    MstMarketInstitutionsDto getInterfCustomerInfo(@Param("insitituteCode") String insitituteCode);

}

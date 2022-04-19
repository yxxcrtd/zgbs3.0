/*
 * 文件名：MstSecuritiesAccountMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangjun
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstsecuritiesaccount.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstsecuritiesaccount.dto.MstSecuritiesAccountDto;

/**
 * 证券账户管理mapper
 * 〈功能详细描述〉
 * @author zhangjun
 * @version 1.0 2018年9月11日
 * @see MstSecuritiesAccountMapper
 * @since 1.0
 */
@Repository
public interface MstSecuritiesAccountMapper {
    /**
     * 
     * 模糊查询
     * <功能详细描述>
     * @param  pageParam
     * @return List
     * @see
     * @since 1.0
     */
    List<MstSecuritiesAccountDto> select(Map<String, Object> pageParam);
    /**
     * 证券账户账号联想下拉框
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return List
     * @see
     * @since 1.0  
     */
    List<SuggestDto> getSecAcctId(@Param("secAcctId")String secAcctId);
    
    /**
     * 
     * 组合代码联想文本框
     * <功能详细描述>
     * @param map 联想参数
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    List<SuggestDto> getPortfolioId(Map<String,Object> map);
    
    /**
     * 回款账户以及资金结算账户联想下拉框
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return List
     * @see
     * @since 1.0  
     */
    List<SelectOptionDto> getBusiAcctNo(@Param("busiAcctNo")String busiAcctNo);
    
    /**
     * 根据证券账户账号查询
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return List
     * @see
     * @since 1.0  
     */
    MstSecuritiesAccountDto selectBySecAcctId(@Param("secAcctId")String secAcctId);
    /**
     * 检查证券账户账号是否用过
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return List
     * @see
     * @since 1.0  
     */
    int checkBySecAcctId(MstSecuritiesAccountDto dto);
    /**
     * 检查证券账户名称是否用过
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return List
     * @see
     * @since 1.0  
     */
    
    int checkBySecAcctName(MstSecuritiesAccountDto dto);
    /**
     * 检查资金结算账户是否存在
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return List
     * @see
     * @since 1.0  
     */
    int checkByTrdAcctId(MstSecuritiesAccountDto dto);
    /**
     * 
     *删除时检查数据是否被使用过
     * <功能详细描述>
     * @param dto 业务实体
     * @return int
     * @see
     * @since 1.0
     */
    int checkCodeIsUse(MstSecuritiesAccountDto dto);
    
    /**
     * 
     *批量删除时检查数据是否被使用过
     * <功能详细描述>
     * @param item List<MstSecuritiesAccountDto>
     * @return int
     * @see
     * @since 1.0
     */
    List<MstSecuritiesAccountDto> checkAllCodeIsUse(List<MstSecuritiesAccountDto> item);
//    /**
//     * 
//     * 上传
//     * <功能详细描述>
//     * @param dto FileDto dto
//     * @return int
//     * @see
//     * @since 1.0
//     */
//    int insertIntoFile(FileDto dto);
//    
//    /**
//     * 
//     * 查询上传文件
//     * <功能详细描述>
//     * @param businessId businessId
//     * @return List<FileArray>
//     * @see
//     * @since 1.0
//     */
//    List<FileDto> selectFileByBusinessId(@Param("businessId")String businessId);
//    
//    /**
//     * 
//     * 删除上传文件
//     * <功能详细描述>
//     * @param businessId businessId
//     * @return int
//     * @see
//     * @since 1.0
//     */
//    int deleteFileByBusinessId(@Param("businessId")String businessId);
    
    /**
     * 证券代码查询
     * @param map
     * @return
     */
    List<SelectOptionDto> getFinprodMarketInfo(Map<String,Object> map);
    
    /**
     * 标的查询
     * @param map
     * @return
     */
    List<SelectOptionDto> getFinprodId(Map<String,Object> map);
    
    /**
     * 证券账户查询
     * @param map
     * @return
     */
    List<SelectOptionDto> getSecAcctInfo(Map<String,Object> map);
    
    /**
     * 组合资金账户查询
     * @param map
     * @return
     */
    List<SelectOptionDto> getPtlSecAcctInfo(Map<String,Object> map);

    List<SelectOptionDto> getFinprodMarketInfoPosition(Map<String, Object> params);

    List<SelectOptionDto> getFinprodIdByMarketId(Map<String, Object> map);

    List<Map<String, Object>> getFaceValueList(Map<String, Object> map);
    
    List<SelectOptionDto> getFuturesCompany(@Param("companyNo")String companyNo);

    String IsBondAsset(Map<String, Object> map);

    String QueryForTestStatus(Map<String, Object> map);

    /**
     * 证券代码查询
     * @param map
     * @return
     */
    List<SelectOptionDto> getFinprodMarketInfoNewUi(Map<String,Object> map);

    List<SelectOptionDto> getFinprodMarketInfoSecRepo(Map<String, Object> map);
}

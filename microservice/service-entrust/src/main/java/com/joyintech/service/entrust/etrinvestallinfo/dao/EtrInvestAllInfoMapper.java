/*
 * 文件名：EtrInvestAllInfo001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etrinvestallinfo.dao;

import java.util.List;
import java.util.Map;

import com.joyintech.service.entrust.etrinvestallinfo.dto.EtrInvestAllInfoExportPdfDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.entrust.etrinvestallinfo.dto.EtrInvestAllInfoDto;

/**
 * 所有投资委托微服务端mapper
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年9月7日
 * @see EtrInvestAllInfoMapper
 * @since 1.0
 */
@Repository
public interface EtrInvestAllInfoMapper {
    /**
     * 
     * 获取组合代码联想文本框信息
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<SuggestDto> getentrustId(@Param("entrustId") String entrustId);
    /**
     * 
     * 获取资产代码联想文本框信息
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<SuggestDto> getfinprodId(@Param("finprodId") String finprodId);
    
    
    /**
     * 获取资产代码联想文本框信息
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    List<SuggestDto> getfinprodIdPub(@Param("finprodId") String finprodId);
    
    /**
     * 
     * 查询所有投资委托信息
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<EtrInvestAllInfoDto> select(Map<String, Object> param);
    /**
     * 
     * 查询PB导出信息 add by caoxue 2019-12-31
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<EtrInvestAllInfoDto> selectExportPB(Map<String, Object> param);
    /**
     * 
     * 变更PB是否导出状态 add by caoxue 2019-12-31
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    int updateExportPB(Map<String, Object> param);

    EtrInvestAllInfoDto getById(Map<String, Object> param);

    /**
     * 查询导出pdf数据 add by zengchengfa
     * @param param
     * @return
     */
    List<EtrInvestAllInfoExportPdfDto> selectExportPdf(Map<String, Object> param);
}

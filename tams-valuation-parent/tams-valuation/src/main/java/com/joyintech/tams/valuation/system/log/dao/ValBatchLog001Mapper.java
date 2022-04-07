/*
 * 文件名：ValBatchLog001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：ww1
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.system.log.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.tams.valuation.system.log.dto.ValBatchLog001Dto;
import com.joyintech.tams.common.base.entity.BokBatchMarketwarningInfo;


/**
 * 批处理日志Mapper
 * 
 * @author ww1
 * @version 1.0 2018年2月23日
 * @see ValBatchLog001Mapper
 * @since 1.0
 */
@Repository
public interface ValBatchLog001Mapper {

    /**
     * 根据条件查找日志
     * 
     * @param param 查询条件
     * @return 日志信息
     */
    List<ValBatchLog001Dto> select(Map<String, Object> param);
    
    /**
     * 取得任务组信息
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    List<SuggestDto> getTaskGroup(String param);
    
    /**
     * 取得任务信息
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    List<SuggestDto> getTask(String param);
    
    /**
     * 取得产品组信息
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    List<SuggestDto> getPrdGroup(@Param("param")String param ,@Param("institutionlist") List<String> institutionlist);

    ValBatchLog001Dto findLogDetail(String logSeqNo);

    ValBatchLog001Dto findLogDetailEx(String logSeqNo);

}

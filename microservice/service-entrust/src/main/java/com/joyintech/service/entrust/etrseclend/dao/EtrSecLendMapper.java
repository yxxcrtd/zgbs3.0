/*
 * 文件名：EtrSecLendMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etrseclend.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.entrust.etrseclend.dto.EtrSecLendDto;

/**
 * 债券投资委托mapper
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年9月11日
 * @see EtrSecLendMapper
 * @since 1.0
 */
@Repository
public interface EtrSecLendMapper {
    /**
     * 
     * 债券投资委托查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<EtrSecLendDto> selectForPage(Map<String, Object> param);
    /**
     * 
     * 获取资产代码联想文本框信息
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getFinprodId(Map<String, Object> param);
    /**
     * 查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    List<SuggestDto> getSecManageAcctId(Map<String, Object> param);

    /**
     * 
     * 通过主键查询修改信息
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */ 
    EtrSecLendDto selectByKey(Map<String, Object> map);
    
    /**
     * 
     * 通过主键查询流程中信息
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */ 
    EtrSecLendDto selectWflByKey(Map<String, Object> map);
    
    /**
     * 删除流程中数据
     * @param workflowId
     */
    void deleteWflwflEtrTarget(String workflowId);


    BigDecimal queryUsableSecPosition(PtlSecPositionFlowDto positionQueryDto);
}

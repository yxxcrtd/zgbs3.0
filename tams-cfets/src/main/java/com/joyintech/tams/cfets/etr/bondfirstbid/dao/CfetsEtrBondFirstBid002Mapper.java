/*
 * 文件名：EtrInvestBondInfoMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.etr.bondfirstbid.dao;

import com.joyintech.framework.base.entity.ProcessMark;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 一级投标指令mapper
 */
@Repository
public interface CfetsEtrBondFirstBid002Mapper {


    /**
     * 根据指令代码删除委托投标标位表数据
     * @param entrustId
     * @return
     */
    int deleteEntrustBidPriceByEntrustId(@Param("entrustId") String entrustId);

    /**
     * 根据指令代码删除委托指令明细数据
     * @param entrustId
     * @return
     */
    int deleteEntrustDetailByEntrustId(@Param("entrustId")String entrustId);

    /**
     * 查询留痕表数据
     * @param workflowId
     * @return
     */
    ProcessMark getFirstBidBusinessInfo(@Param("workflowId") String workflowId);
}

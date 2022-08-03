package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.StlPayTempOrders;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StlPayTempOrdersMapper {
    int deleteByPrimaryKey(String orderId);

    int insert(StlPayTempOrders record);

    int insertSelective(StlPayTempOrders record);

    /**
     * 批量入库
     */
    int insertBatch(List<StlPayTempOrders> list);

    int insertBasicByBasicInfo(StlPayTempOrders record);

    StlPayTempOrders selectByPrimaryKey(String orderId);

    int updateByPrimaryKeySelective(StlPayTempOrders record);

    int updateByPrimaryKey(StlPayTempOrders record);

    int updateByTradeId(StlPayTempOrders record);

    /**
     * 批量入库
     */
    int insertStlPayOrders(@Param("list") List<String> tradeIds);

    /**
     * 批量删除
     */
    int deleteByTradeIds(@Param("list") List<String> tradeIds);

    List<StlPayTempOrders> selectByTradeIds(@Param("list") List<String> list);
}
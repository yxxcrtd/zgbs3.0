package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StlPayOrdersMapper extends BaseBatchMapper<StlPayOrders, String> {
    int deleteByPrimaryKey(String orderId);

    int insert(StlPayOrders record);

    int insertSelective(StlPayOrders record);

    /**
     * 批量入库
     */
    int insertBatch(List<StlPayOrders> list);

    int insertBasicByBasicInfo(StlPayOrders record);

    StlPayOrders selectByPrimaryKey(String orderId);

    List<StlPayOrders> selectByTradeIds(@Param("list") List<String> list);

    /**
     * 批量删除
     */
    int deleteByTradeIds(@Param("list") List<String> tradeIds);

    int updateByPrimaryKeySelective(StlPayOrders record);

    int updateByPrimaryKey(StlPayOrders record);

    int updateByTradeId(StlPayOrders record);

    List<StlPayOrders> selectByTradeId(String orderId);

    /**
     * 检索交易对手资金账户共通
     * @param counterId 交易对手ID
     * @param ccy
     * @return 交易对手资金账户
     */
    List<StlCapacctInfo> selectCounterCapacctInfoList(@Param("counterId") String counterId, @Param("acctType") String acctType, @Param("ccy") String ccy);
}
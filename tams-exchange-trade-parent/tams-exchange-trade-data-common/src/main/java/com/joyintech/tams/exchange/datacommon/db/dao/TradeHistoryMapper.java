package com.joyintech.tams.exchange.datacommon.db.dao;

import com.joyintech.tams.exchange.common.bean.TradeQryBean;
import com.joyintech.tams.exchange.common.bean.TradeQueryBean;
import com.joyintech.tams.exchange.common.db.domain.TradeHistory;
import com.joyintech.tams.exchange.common.db.domain.TradeHistoryExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeHistoryMapper extends IMybatisMapper<TradeHistory, String, TradeHistoryExample> {
    long countByExample(TradeHistoryExample example);

    int deleteByExample(TradeHistoryExample example);

    int insert(TradeHistory record);

    int insertBatch(List<TradeHistory> records);

    int insertSelective(TradeHistory record);

    List<TradeHistory> selectByExample(TradeHistoryExample example);

    List<TradeQueryBean> selectTradeByPage(TradeQryBean tradeQryBean);

    List<TradeQueryBean> selectHistoryTradeByPage(TradeQryBean tradeQryBean);

    int updateByExampleSelective(@Param("record") TradeHistory record, @Param("example") TradeHistoryExample example);

    int updateByExample(@Param("record") TradeHistory record, @Param("example") TradeHistoryExample example);

    String queryLastClearDate();
}
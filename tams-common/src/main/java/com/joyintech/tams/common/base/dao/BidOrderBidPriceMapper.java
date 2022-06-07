package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.BidOrderBidPrice;
import com.joyintech.tams.common.base.entity.BidOrderBidPriceKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidOrderBidPriceMapper {
    int deleteByPrimaryKey(BidOrderBidPriceKey key);

    int insert(BidOrderBidPrice record);

    int insertSelective(BidOrderBidPrice record);

    int insertBasicByBasicInfo(BidOrderBidPrice record);

    BidOrderBidPrice selectByPrimaryKey(BidOrderBidPriceKey key);

    List<BidOrderBidPrice> selectListByPrimaryKey(String bidId);

    int updateByPrimaryKeySelective(BidOrderBidPrice record);

    int updateByPrimaryKey(BidOrderBidPrice record);

    int updateWinByBidId(BidOrderBidPrice record);

    List<BidOrderBidPrice> selectByBidId(BidOrderBidPrice key);
}
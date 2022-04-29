package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.WflBidOrderBidPrice;
import com.joyintech.tams.common.base.entity.WflBidOrderBidPriceKey;
import org.springframework.stereotype.Repository;

@Repository
public interface WflBidOrderBidPriceMapper {
    int deleteByPrimaryKey(WflBidOrderBidPriceKey key);

    int deleteByBidId(String BidId);

    int deleteByWorkflowId(String workflowId);

    int insert(WflBidOrderBidPrice record);

    int insertSelective(WflBidOrderBidPrice record);

    WflBidOrderBidPrice selectByPrimaryKey(WflBidOrderBidPriceKey key);

    int updateByPrimaryKeySelective(WflBidOrderBidPrice record);

    int updateByPrimaryKey(WflBidOrderBidPrice record);
}
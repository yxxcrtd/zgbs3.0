package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.TrdBankManualSettleFlow;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;
@Repository
public interface TrdBankManualSettleFlowMapper extends BaseBatchMapper<TrdBankManualSettleFlow, String> {
    int deleteByPrimaryKey(String seqNo);

    int insert(TrdBankManualSettleFlow record);

    int insertSelective(TrdBankManualSettleFlow record);

    TrdBankManualSettleFlow selectByPrimaryKey(String seqNo);

    int updateByPrimaryKeySelective(TrdBankManualSettleFlow record);

    int updateByPrimaryKey(TrdBankManualSettleFlow record);
}
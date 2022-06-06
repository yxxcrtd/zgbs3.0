package com.joyintech.tams.exchange.datacommon.db.dao;


import com.joyintech.tams.exchange.common.db.domain.InstructionPledgeHistory;
import com.joyintech.tams.exchange.common.db.domain.InstructionPledgeHistoryExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstructionPledgeHistoryMapper extends IMybatisMapper<InstructionPledgeHistory, String, InstructionPledgeHistoryExample> {
    int countByExample(InstructionPledgeHistoryExample example);

    int deleteByExample(InstructionPledgeHistoryExample example);

    int deleteByPrimaryKey(String entrustId);

    int deleteBatchByPrimaryKey(List<String> ids);

    int insert(InstructionPledgeHistory record);

    int insertSelective(InstructionPledgeHistory record);

    int updateOrInsertSelective(InstructionPledgeHistory record);

    List<InstructionPledgeHistory> selectByExample(InstructionPledgeHistoryExample example);

    InstructionPledgeHistory selectByPrimaryKey(String entrustId);

    int updateByExampleSelective(@Param("record") InstructionPledgeHistory record, @Param("example") InstructionPledgeHistoryExample example);

    int updateByExample(@Param("record") InstructionPledgeHistory record, @Param("example") InstructionPledgeHistoryExample example);

    int updateByPrimaryKeySelective(InstructionPledgeHistory record);

    int updateByPrimaryKey(InstructionPledgeHistory record);

}
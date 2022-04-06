package com.joyintech.tams.exchange.datacommon.db.dao;

import com.joyintech.tams.exchange.common.db.domain.AppointInstructionRecord;
import com.joyintech.tams.exchange.common.db.domain.AppointInstructionRecordExample;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lifangpeng
 * @date 2022/4/2 11:47
 */
@Repository
public interface AppointInstructionRecordMapper extends IMybatisMapper<AppointInstructionRecord, String, AppointInstructionRecordExample> {

    @Override
    AppointInstructionRecord selectByPrimaryKey(String s);

    @Override
    int insert(AppointInstructionRecord record);

    @Override
    int insertSelective(AppointInstructionRecord record);

    @Override
    int updateOrInsertSelective(AppointInstructionRecord record);

    @Override
    int updateByPrimaryKey(AppointInstructionRecord record);

    @Override
    int updateByPrimaryKeySelective(AppointInstructionRecord record);

    @Override
    int deleteByPrimaryKey(String s);

    @Override
    int deleteBatchByPrimaryKey(List<String> strings);

    @Override
    List<AppointInstructionRecord> selectByExample(AppointInstructionRecordExample example);
}

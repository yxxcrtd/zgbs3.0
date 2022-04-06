package com.joyintech.tams.exchange.datacommon.db.dao;

import com.joyintech.tams.exchange.common.db.domain.AppointInstructionRecord;
import com.joyintech.tams.exchange.common.db.domain.AppointInstructionRecordExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lifangpeng
 * @date 2022/4/2 11:47
 */
@Repository
public interface AppointInstructionRecordMapper extends IMybatisMapper<AppointInstructionRecord, String, AppointInstructionRecordExample> {

    @Override
    int insert(AppointInstructionRecord record);

    @Override
    int insertSelective(AppointInstructionRecord record);

    @Override
    int deleteBatchByPrimaryKey(List<String> strings);

    @Override
    List<AppointInstructionRecord> selectByExample(AppointInstructionRecordExample example);

    @Override
    int updateOrInsertSelective(AppointInstructionRecord record);

    @Override
    int updateByPrimaryKeySelective(AppointInstructionRecord record);
    /**
     * 修改指令分发数据状态
     *
     * @param keys   指令分发数据ID
     * @param status 指令分发数据状态
     * @return 修改结果
     */
    int updateStatusByKey(@Param("keys") List<String> keys, @Param("status") int status);
}

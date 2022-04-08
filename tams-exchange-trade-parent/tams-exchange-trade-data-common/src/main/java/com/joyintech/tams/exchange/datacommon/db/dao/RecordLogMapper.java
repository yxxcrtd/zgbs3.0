package com.joyintech.tams.exchange.datacommon.db.dao;

import com.joyintech.tams.exchange.common.db.domain.RecordLog;
import com.joyintech.tams.exchange.common.db.domain.RecordLogExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lifangpeng
 * @date 2022/4/2 11:47
 */
@Repository
public interface RecordLogMapper extends IMybatisMapper<RecordLog, String, RecordLogExample> {

    @Override
    int insert(RecordLog record);

    @Override
    int insertSelective(RecordLog record);

    @Override
    int deleteBatchByPrimaryKey(List<String> strings);

    @Override
    List<RecordLog> selectByExample(RecordLogExample example);

    @Override
    int updateOrInsertSelective(RecordLog record);

    @Override
    int updateByPrimaryKeySelective(RecordLog record);
}

package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.CommissionCaclProcess;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionCaclProcessMapper extends BaseBatchMapper<CommissionCaclProcess, String> {
    int deleteByPrimaryKey(String busiNo);

    int insert(CommissionCaclProcess record);

    int insertSelective(CommissionCaclProcess record);

    int insertBasicByBasicInfo(CommissionCaclProcess record);

    CommissionCaclProcess selectByPrimaryKey(String busiNo);

    int updateByPrimaryKeySelective(CommissionCaclProcess record);

    int updateByPrimaryKey(CommissionCaclProcess record);
}
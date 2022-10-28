package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.common.base.entity.MstCounterParty;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MstCounterPartyMapper {
    int deleteByPrimaryKey(String counterId);

    int insert(MstCounterParty record);

    int insertSelective(MstCounterParty record);

    int insertBasicByBasicInfo(MstCounterParty record);

    MstCounterParty selectByPrimaryKey(String counterId);

    int updateByPrimaryKeySelective(MstCounterParty record);

    int updateByPrimaryKey(MstCounterParty record);
    
    MstCounterParty selectPType(@Param("counterId") String counterId);
    
    List<String> getInsCounterIdByName(Map<String,Object> map);

    List<MstCounterParty> getCounterByName(String counterName);

    List<MstCounterParty> getAllCounterName(@Param("counterIds") List<String> counterIds);
}
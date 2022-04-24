package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.WflEtrEntrust;
import com.joyintech.tams.common.base.entity.WflEtrEntrustKey;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WflEtrEntrustMapper {
    int deleteByPrimaryKey(WflEtrEntrustKey key);

    int deleteByWorkflowId(String workflowId);

    int updateByWorkflowIdSelective(WflEtrEntrust record);

    int updateByWorkflowId(WflEtrEntrust record);

    int insertBasicByWorkflowId(Map paraMap);

    int insert(WflEtrEntrust record);

    int insertSelective(WflEtrEntrust record);

    int insertBasicByBasicInfo(WflEtrEntrust record);

    WflEtrEntrust selectByPrimaryKey(WflEtrEntrustKey key);

    WflEtrEntrust selectByEntrustId(@Param("entrustId") String entrustId);

    int updateByPrimaryKeySelective(WflEtrEntrust record);

    int updateByPrimaryKey(WflEtrEntrust record);
}
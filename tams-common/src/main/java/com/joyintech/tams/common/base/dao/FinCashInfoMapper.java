package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinCashInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FinCashInfoMapper {
    int deleteByPrimaryKey(String cashId);

    int insert(FinCashInfo record);

    int insertSelective(FinCashInfo record);

    int insertBasicByBasicInfo(FinCashInfo record);

    FinCashInfo selectByPrimaryKey(String cashId);

    int updateByPrimaryKeySelective(FinCashInfo record);

    int updateByPrimaryKey(FinCashInfo record);

    void deleteByFinprodIdAndBranch(@Param("finprodId") String finprodId,
                                    @Param("branch") Long branch);
}
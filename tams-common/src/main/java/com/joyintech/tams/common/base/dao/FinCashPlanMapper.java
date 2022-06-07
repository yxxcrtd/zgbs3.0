package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinCashPlanKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;

@Repository
public interface FinCashPlanMapper {
    int deleteByPrimaryKey(FinCashPlanKey key);

    int insert(FinCashPlan record);

    int insertSelective(FinCashPlan record);

    int insertBasicByBasicInfo(FinCashPlan record);

    FinCashPlan selectByPrimaryKey(FinCashPlanKey key);

    int updateByPrimaryKeySelective(FinCashPlan record);

    int updateByPrimaryKey(FinCashPlan record);
    //查询当天是否为收息日或到期日
    int getPayTrad(Map<String, Object> map);

    /**
     * 查询债券对应的最新面值
     */
    BigDecimal selectFaceValue(@Param("finprodId") String finprodId, @Param("sysDate") String sysDate);
}
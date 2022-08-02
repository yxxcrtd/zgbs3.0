package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.FinExercisePlanDto;
import com.joyintech.tams.common.base.entity.FinExercisePlanKey;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;

@Repository
public interface FinExercisePlanMapper {
    int deleteByPrimaryKey(FinExercisePlanKey key);

    int insert(FinExercisePlan record);

    int insertSelective(FinExercisePlan record);

    int insertBasicByBasicInfo(FinExercisePlan record);

    FinExercisePlan selectByPrimaryKey(FinExercisePlanKey key);

    FinExercisePlanDto selectDateByFinprodId(Map<String, Object> params);

    int updateByPrimaryKeySelective(FinExercisePlan record);

    int updateByPrimaryKey(FinExercisePlan record);
}
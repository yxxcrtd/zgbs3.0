package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinFloatCalcRuleDetail;
import com.joyintech.tams.common.base.entity.FinFloatCalcRuleDetailKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FinFloatCalcRuleDetailMapper {
    int deleteByPrimaryKey(FinFloatCalcRuleDetailKey key);

    int insert(FinFloatCalcRuleDetail record);

    int insertSelective(FinFloatCalcRuleDetail record);

    int insertBasicByBasicInfo(FinFloatCalcRuleDetail record);

    FinFloatCalcRuleDetail selectByPrimaryKey(FinFloatCalcRuleDetailKey key);

    int updateByPrimaryKeySelective(FinFloatCalcRuleDetail record);

    int updateByPrimaryKey(FinFloatCalcRuleDetail record);

    int deleteByCashIdAndEffDate(FinFloatCalcRuleDetailKey key);
}
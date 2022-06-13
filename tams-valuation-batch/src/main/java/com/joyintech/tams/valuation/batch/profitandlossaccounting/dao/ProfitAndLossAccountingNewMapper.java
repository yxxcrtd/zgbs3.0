/*
 * 文件名：ProfitAndLossAccountingMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.profitandlossaccounting.dao;

import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingNeedOperateEntity;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingSubject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProfitAndLossAccountingNewMapper {

    /**
     * 查询所有待处理账套
     *
     * @param booksetIdList 待处理账套代码列表
     * @return 待处理账套
     * @see
     * @since 1.0
     */
    List<ProfitAndLossAccountingNeedOperateEntity> selectTodoBooksetIdList(Map<String, Object> params);

    /**
     * 查询损益结转科目
     *
     * @param 账套代码
     * @param 科目类型
     * @param 基础科目性质1
     * @param 基础科目性质2
     * @return 损益结转科目列表
     * @see
     * @since 1.0
     */
    List<ProfitAndLossAccountingSubject> selectPalSubject(@Param("booksetId") String 账套代码,
                                                  @Param("subjectTypeList") List<String> 科目类型列表,
                                                  @Param("baseSubjectNatureList") List<String> 基础科目性质列表);
}

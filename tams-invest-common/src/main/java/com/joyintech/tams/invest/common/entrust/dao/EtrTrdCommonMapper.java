/*
 * 文件名：FieldEtrInfoQueryMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meiraxin
 * 修改时间：2021年03月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.entrust.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.common.base.entity.PtlCapacctInfo;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 场内指令查询
 *
 * @author msk
 * @version 1.0 2021年12月22日
 * @see EtrTrdCommonMapper
 * @since 1.0
 */
@Repository
public interface EtrTrdCommonMapper {


    List<PtlCapacctInfo> getPtlAcctInfo(@Param("portfolioId") String portfolioId, @Param("acctTypeList") List<String> acctTypeList);

    List<SelectOptionDto> getPtlAcctInfoByPortId(@Param("portfolioId") String portfolioId, @Param("acctTypeList") List<String> acctTypeList);

    List<StlCapacctInfo> getQsPtlAcctInfo(@Param("acctNo") String acctNo, @Param("portfolioId") String portfolioId);

    /**
     * 交易日历的节假日取得
     *
     * @param paraMap 参数
     * @return 节假日列表
     */
    List<String> getDealCalendarHoliday(Map<String, Object> paraMap);

    BigDecimal selectFaceValue(Map<String, Object> params);

    /**
     * 查询首期的到期结算金额
     *
     * @param finprodId 资产代码
     * @return 到期结算金额
     */
    BigDecimal getFirstTradeAmtByTradeId(@Param("finprodId") String finprodId);
}

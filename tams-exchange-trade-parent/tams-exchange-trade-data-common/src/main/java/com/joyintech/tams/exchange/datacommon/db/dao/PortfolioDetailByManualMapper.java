package com.joyintech.tams.exchange.datacommon.db.dao;

import com.joyintech.tams.exchange.common.db.domain.PortfolioDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author swwan
 * @date 2022/5/10 - 22:29
 */
public interface PortfolioDetailByManualMapper {

    /**
     * 根据指令代码和业务类型查询历史数量汇总
     */
    List<PortfolioDetail> selectSummaryByInstructionAndBusiType(@Param("instructionId")String instructionId, @Param("busiType")String busiType);

    /**
     * 根据指令代码和业务类型查询明细
     */
    List<PortfolioDetail> selectListByInstructionAndBusiType(@Param("instructionId")String instructionId, @Param("busiType")String busiType);

    /**
     * 更新业务状态为确认
     */
    int updateBusiStatusByBusiId(@Param("busiId")String busiId);

}
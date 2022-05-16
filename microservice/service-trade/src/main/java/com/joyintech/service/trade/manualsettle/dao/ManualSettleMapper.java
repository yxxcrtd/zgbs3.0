package com.joyintech.service.trade.manualsettle.dao;

import com.joyintech.service.trade.manualsettle.dto.ManualSettleDto;
import com.joyintech.service.trade.manualsettle.dto.ManualSettleFlowDto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zhangnian
 * @ClassName ManualSettleMapper
 * @description TODO
 * @date 2020/10/22 0022 14:09
 **/
@Repository
public interface ManualSettleMapper {

    List<ManualSettleDto> queryForPage(Map<String, Object> param);

    List<ManualSettleFlowDto> queryForFlowPage(Map<String, Object> param);

    /**
     *功能描述 ：交收延期
     * @author zhangnian
     * @date 2020/10/22 0022
     * @param  * @param null
     * @return
    */
    void updateSettleInfo(Map<String, Object> param);

    StlPayTempOrders selectStlPayOrders(@Param("tradeId") String tradeId);

    void deleteEtrCashFlow(@Param("orderId") String orderId);

    void deletePtlCashFlow(@Param("orderId") String orderId);

    void deletePtlSecFlow(@Param("tradeId") String tradeId);

    void deleteEtrSecFlow(@Param("tradeId") String tradeId);

    List<EtrCashPositionFlowDto> selectEtrCashPositionFlow(@Param("orderId") String orderId);

    List<EtrSecPositionFlowDto> selectEtrSecPositionFlow(@Param("tradeId") String tradeId);

    List<ManualSettleDto> findPageExport(Map<String, Object> param);

    InfContractDetailfile selectInfContractDetailfile(String entrustNo, String dealNo);

    /**
     * 组合持仓流水
     * @param tradeId
     * @return
     */
    List<PtlSecPosition> listPtlSecPositionFlow(String tradeId);

    /**
     *组合现金流水
     * @param orderId
     * @return
     */
    List<PtlCashPosition> listPtlCashPositionFlow(String orderId);

    /**
     * 委托持仓流水
     * @param tradeId
     * @return
     */
    List<EtrSecPosition> listEtrSecPositionFlow(String tradeId);

    /**
     * 委托现金流水
     * @param orderId
     * @return
     */
    List<EtrCashPosition> listEtrCashPositionFlow(String orderId);

    /**
     * 删除日间委托完成度
     * @param tradeId 参数
     * @return
     */
    int deleteEtrFinishStatus(String tradeId);

    int updatePtlCashPosition(PtlCashPosition ptlCashPosition);

    int updatePtlSecPosition(PtlSecPosition ptlSecPosition);

    int updateEtrCashPosition(EtrCashPosition etrCashPosition);

    int updateEtrSecPosition(EtrSecPosition etrSecPosition);

    List<TrdProductTempDeal> selectTrdProductTempDeals(@Param("totleTradeId") String totleTradeId);
} 


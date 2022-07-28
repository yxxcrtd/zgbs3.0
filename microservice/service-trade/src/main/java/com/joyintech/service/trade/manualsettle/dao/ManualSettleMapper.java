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

    // XYHGFXSJ-8:查询质押品信息list modify by fuyongsheng 20220718 start
    List<FinDealPledge> selectFinDealPledgeByTradeId(@Param("tradeId") String tradeId);
    // XYHGFXSJ-8:查询质押品信息list modify by fuyongsheng 20220718 end

    // modify by etliu 20220727 start
    // 3.0.6协议回购+风险升级 XYHGFXSJ-461
    //【现金流缺口分析】协议式上交所正回购提前购回数据，日间交收后，现金流缺口分析、组合未来头寸分析、实时资产头寸查询报表显示错误
    /**
     * 根据首期成交编号查询首期信息
     * @param finprodId 首期成交编号
     * @return 首期信息
     */
    List<ManualSettleDto> selectPledgeInfo(@Param("finprodId") String finprodId);
    // modify by etliu 20220727 end
} 


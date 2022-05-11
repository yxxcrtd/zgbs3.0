package com.joyintech.tams.settle.batch.dealrevoke.dao;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 何阳阳
 * @date 2020/8/3
 * @see InvestDealRevokeMapper
 * @since 1.0
 */
@Repository
public interface InvestDealRevokeMapper {
    /**
     * 查询汇总交易编号
     * @param param 查询参数
     * @return 汇总交易编号列表
     */
    List<String> selectTotalTradeId(Map<String, Object> param);
    
    /**
     * 关联结算下行映射表查询汇总交易编号
     * @param param 查询参数
     * @return 汇总交易编号列表
     */
    List<String> selectTotalTradeIdTgM(Map<String, Object> param);

    /**
     * 关联结算下行映射表查询汇总交易编号
     * @param param 查询参数
     * @return 汇总交易编号列表
     */
    List<String> selectSsltTotalTradeIdTgM(Map<String, Object> param);

    /**
     * 查询交易编号
     * @param param 查询参数
     * @return 交易编号列表
     */
    List<String> selectTradeId(Map<String, Object> param);

    /**
     * 关联结算下行映射表查询交易编号
     * @param param 查询参数
     * @return 交易编号列表
     */
    List<String> selectTradeIdTgM(Map<String, Object> param);

    /**
     * 删除组合持仓流水
     * @param param 参数
     * @return
     */
    int deletePtlSecPositionFlow(Map<String, Object> param);

    /**
     * 删除组合现金流水
     * @param param 参数
     * @return
     */
    int deletePtlCashPositionFlow(Map<String, Object> param);

    /**
     * 删除指令持仓流水
     * @param param 参数
     * @return
     */
    int deleteEtrSecPositionFlow(Map<String, Object> param);

    /**
     * 删除指令现金流水
     * @param param 参数
     * @return
     */
    int deleteEtrCashPositionFlow(Map<String, Object> param);

    /**
     * 删除质押券出入库表
     * @param param 参数
     * @return
     */
    int deleteTrdStasecFlow(Map<String, Object> param);

    /**
     * 删除质押券出入库明细表
     * @param param 参数
     * @return
     */
    int deleteTrdStasecFlowDetail(Map<String, Object> param);

    /**
     * 删除日间委托完成度
     * @param param 参数
     * @return
     */
    int deleteEtrFinishStatus(Map<String, Object> param);
    
    /**
     * 删除日终交易占用的委托完成度信息
     * @param param 参数
     * @return
     */
    int deleteEtrFinishStatusByTrdId(Map<String, Object> param);
    
    /**
     * 根据交易编号删除日间指令现金流水信息
     * @param param 参数
     * @return
     */
    int deleteEtrCashPositionFlowByTrdId(Map<String, Object> param);
    

    /**
     * 删除日间指令现金流水
     * @param param 参数
     * @return
     */
    int deleteEtrCashPositionFlowByTradeId(Map<String, Object> param);

    /**
     * 组合持仓流水
     * @param param
     * @return
     */
    List<PtlSecPosition> listPtlSecPositionFlow(Map<String, Object> param);

    /**
     *组合现金流水
     * @param param
     * @return
     */
    List<PtlCashPosition> listPtlCashPositionFlow(Map<String, Object> param);

    /**
     * 委托持仓流水
     * @param param
     * @return
     */
    List<EtrSecPosition> listEtrSecPositionFlow(Map<String, Object> param);

    /**
     * 委托现金流水
     * @param param
     * @return
     */
    List<EtrCashPosition> listEtrCashPositionFlow(Map<String, Object> param);

    int updatePtlCashPosition(PtlCashPosition ptlCashPosition);

    int updatePtlSecPosition(PtlSecPosition ptlSecPosition);

    int updateEtrCashPosition(EtrCashPosition etrCashPosition);

    int updateEtrSecPosition(EtrSecPosition etrSecPosition);

    int updateEtrStatusByTrdId(Map<String, Object> param);

    List<Map<String,String>> selectPsById(Map<String, Object> param);

    List<Map<String, String>> selectRepoPsById(Map<String, Object> param);

    void deletePtlSecPositionFlowByTradeIds(@Param("tradeIds") List<String> tradeIds);

    void deleteEtrSecPositionFlowByTradeIds(@Param("tradeIds") List<String> tradeIds);

    void deleteEtrCashPositionFlowByTradeIds(@Param("tradeIds") List<String> tradeIds);

    void deletePtlCashPositionFlowByTradeIds(@Param("tradeIds") List<String> tradeIds);

    List<String> searchTradeIdsByTotalTradeIds(@Param("totalTradeIds") List<String> totalTradeIds);

    List<PtlSecPositionFlowDto> getPtlSecPositionFlowsByTradeIds(@Param("tradeIds") List<String> tradeIds);

    List<EtrSecPositionFlowDto> getEtrSecPositionFlowsByTradeIds(@Param("tradeIds") List<String> tradeIds);

    void deleteZyTrdStasecFlows(@Param("tradeIds") List<String> tradeIds);

    void deleteZyTrdStasecFlowDetails(@Param("tradeIds") List<String> tradeIds);

	// add by fuyongsheng 20220308 start
    /**
     * 查询日间成交交易
     * @param param
     * @return
     */
    List<TrdProductTempDeal> getTrdProductTempDeals(Map<String, Object> param);
    // add by fuyongsheng 20220308 end

    /**
     * 查询日间标准券出入库流水表
     * @param param
     * @return
     */
    List<TrdTempStasecFlow> getTrdTempStasecFlows(Map<String, Object> param);

    // modify by etliu 20220227 start
    // 广农商二阶段 GNS-963:【广农商场内清算】协议回购上交所存续期（解除质押、到期购回、到期续做、提前购回）指令提交后，指令持仓更新不对
    void deleteDayFinTradeProduct(Map<String, Object> param);
    void deleteDayFinTradeProductAdd(Map<String, Object> param);
    void deleteDayFinProductType(Map<String, Object> param);
    void deleteDayFinDealPledge(Map<String, Object> param);
    // modify by etliu 20220227 end

    // modify by etliu 20220227 start
    // 广农商二阶段 GNS-890:【广农商场内清算】场内债券存续期交易重复跑批时，未对原交易数据进行撤销操作
    /**
     * 查询明细数据的汇总交易编号（如债券存续期）
     * @param param 查询参数
     * @return 汇总交易编号列表
     */
    List<TrdFinProductDealTotal> selectSubTotalTradeData(Map<String, Object> param);
    // modify by etliu 20220227 end

    /**
     * 查询交收成功的交易对应的交收结果
     * @param param 查询参数
     * @return 交收编号列表
     */
    List<String> selectManualSettleIdTgM(Map<String, Object> param);

    //删除交收结果表（交收成功的交易对应的交收结果删除）
    void deleteManualSetDetailBySettleId(@Param("settleIds") List<String> manualSettleIdTgMs);
    void deleteManualSettleInfoBySettleId(@Param("settleIds") List<String> manualSettleIdTgMs);
}

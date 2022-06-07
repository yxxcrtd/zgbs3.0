/*
 * 文件名：DateCalcMapper.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：何阳阳 修改时间：2018年9月21日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.trade.dao;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.trade.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务共通组件.交易占用/释放授信共通组件的Mapper
 *
 * @author sjj
 * @version 1.0 2019年1月14日
 * @see EtrCommonCashPositionMapper
 * @since 1.0
 */
@Repository
public interface EtrCommonCashPositionMapper {

    /**
     * 检索组合关联资金账户共通
     * 
     * @param portfolioId 组合代码
     * @param ccy 币种
     * @param capacctType 资金账户类型
     * @return 组合关联资金账户
     */
    List<StlCapacctInfo> selectProdCapacctInfoList(@Param("portfolioId") String portfolioId, @Param("ccy") String ccy,
                                                   @Param("capacctType") String capacctType);

    /**
     * 检索产品关联资金账户共通
     *
     * @param finprodId 产品代码
     * @param ccy 币种
     * @param capacctType 资金账户类型
     * @return 产品关联资金账户
     */
    List<StlCapacctInfo> selectProdCapacctInfoListForProd(@Param("finprodId") String finprodId, @Param("ccy") String ccy,
                                                          @Param("capacctType") String capacctType);

    /**
     * 检索通道关联资金账户共通
     *
     * @param chlId 产品代码
     * @return 通道关联资金账户
     */
    List<StlCapacctInfo> selectChlCapacctInfoList(String chlId);

    /**
     * 检索交易关联资金账户共通
     * 
     * @param secManageAcctId 证券托管户ID
     * @param ccy 币种
     * @return 交易关联资金账户
     */
    List<StlCapacctInfo> selectDealCapacctInfoList(@Param("secManageAcctId") String secManageAcctId, @Param("ccy") String ccy);
    
    /**
     * 查询可用持仓数量
     * @param PtlSecPosition 组合可用持仓查询Dto
     * @return 可用持仓
     */
    BigDecimal selectAvailablePositionAmount(EtrSecPositionDto etrSecPositionDto);
    
    /**
     * 
     * 查询指令持仓
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    List<EtrSecPosition> selectCommandPosition(EtrSecPositionFlow etrSecPositionFlow);
    
    /**
     * 更新指令持仓
     * <功能详细描述>
     * @param etrSecPositionFlow
     * @return
     * @see
     * @since 1.0
     */
    int updateCommandPosition(EtrSecPositionFlow etrSecPositionFlow);
    
    /**
     * 查询指令持仓最大日期
     * <功能详细描述>
     * @param etrSecPositionFlow
     * @return
     * @see
     * @since 1.0
     */
    Date selectCommandPositionMaxDate(EtrSecPositionFlow etrSecPositionFlow);
    
    /**
     * 根据区间日期更新指令持仓
     * <功能详细描述>
     * @param portfolioId
     * @param secAcctId
     * @param finprodId
     * @param branch
     * @param hodingType
     * @param amount
     * @param minDate
     * @param maxDate
     * @return
     * @see
     * @since 1.0
     */
    int updateCommandPositionByCDate(@Param("portfolioId") String portfolioId, @Param("secAcctId") String secAcctId,
        @Param("finprodId") String finprodId,@Param("branch") Long branch,@Param("hodingType") String hodingType,
        @Param("amount") BigDecimal amount,@Param("minDate") Date minDate,@Param("maxDate") Date maxDate);
    
    /**
     * 
     * 查询指令可用现金
     * <功能详细描述>
     * @param portfolioId
     * @param acctId
     * @param chlFinprodId
     * @param hodingType
     * @param cdate
     * @param ccy
     * @param acctTypes
     * @return
     * @see
     * @since 1.0
     */
    BigDecimal selectCommandAvailableCash(PtlCashPositionFlowDto dto);
    
    /**
     * 
     * 检索指令现金余额数据
     * <功能详细描述>
     * @param etrCashPositionFlow
     * @return
     * @see
     * @since 1.0
     */
    List<EtrCashPosition> selectCommandBalance(EtrCashPositionFlow etrCashPositionFlow);
    
    /**
     * 
     * 更新指令现金余额
     * <功能详细描述>
     * @param etrCashPositionFlow
     * @return
     * @see
     * @since 1.0
     */
    int updateCommandBalance(EtrCashPositionFlow etrCashPositionFlow);
    
    /**
     * 
     * 根据区间日期查询现金余额
     * <功能详细描述>
     * @param etrCashPositionFlow
     * @return
     * @see
     * @since 1.0
     */
    Date selectCommandBalanceMaxCdate(EtrCashPositionFlow etrCashPositionFlow);
    
    /**
     * 
     * 根据区间日期更新现金余额
     * <功能详细描述>
     * @param portfolioId
     * @param acctId
     * @param chlFinprodId
     * @param hodingType
     * @param ccy
     * @param amount
     * @param minDate
     * @param maxDate
     * @return
     * @see
     * @since 1.0
     */
    int updateCommandBalanceByCDate(@Param("portfolioId") String portfolioId, @Param("acctId") String acctId,
                                    @Param("chlFinprodId") String chlFinprodId,@Param("hodingType") String hodingType,
                                    @Param("ccy") String ccy,@Param("amount") BigDecimal amount,
                                    @Param("minDate") Date minDate,@Param("maxDate") Date maxDate);


    EntrustCommonDto findEntrust(String entrustId);

    /**
     * 查询已完成额度
     * @param entrustId 委托代码
     * @return
     */
    BigDecimal getEtrFinishAmt(String entrustId);
    /**
     * 查询剩余需要冲销的金额
     * @param map 委托代码
     * @return 剩余需要冲销的金额
     */
    BigDecimal getEtrSurplusAmt(Map<String,Object> map);
    /**
     * 根据债券信息和日期获取最新市价
     * @param map 参数
     * @return 最新市价
     */
    BigDecimal findLatestMarketPrice(Map<String, Object> map);
    /**
     * 根据债券信息和日期获取最新市价(接口表)
     * @param map 参数
     * @return 最新市价
     */
    BigDecimal findLatestInterfMarketPrice(Map<String, Object> map);
    /**
     * 根据股票代码获取最新收盘价
     * @param finprodId
     * @return
     */
    BigDecimal getClosePrice(@Param("finprodId") String finprodId, @Param("dealDate") Date dealDate);

    /**
     * 根据股票代码获取最新昨日收盘价
     * @param finprodId
     * @return
     */
    BigDecimal getPreClosePrice(@Param("finprodId") String finprodId, @Param("dealDate") Date dealDate);

    /**
     * 根据股票代码获取最新收盘价
     * @param finprodId
     * @return
     */
    BigDecimal getInterfClosePrice(@Param("finprodId") String finprodId, @Param("dealDate") Date dealDate);

    PtlSecValution selectByPrimaryKey(PtlSecValutionKey key);
    
    PtlSecValution selectPtlSecValutionData(PtlSecValutionKey paramDto);
    
    BigDecimal selectFirstUnitCpriceMax(String entrustId);
    BigDecimal selectEtrPledgeSurAmt(@Param("portfolioId") String portfolioId,@Param("finprodId") String finprodId,@Param("secId") String secId);
    
    /**
     * 查询交易所净额结算资产（债券）
     * @param finprodId
     * @return
     */
    String selectFinProduct(@Param("finprodId") String finprodId);
    
    /**
     * 查询交易所净额结算资产（债券质押式正回购、债券质押式逆回购 ）
     * @param finprodId
     * @return
     */
    String selectFinTradeProduct(@Param("finprodId") String finprodId);

    /**
     * 查询手工调整流水数据
     * @param dto
     * @return
     */
    BigDecimal selectManualCashFlow(PtlCashPositionFlowDto dto);

    BigDecimal selectCashPosition(EtrCashPositionFlowDto positionFlowDto);

    Date selectCashPositionMaxCdate(EtrCashPositionFlowDto positionFlowDto);

    String selectMaxCdate(EtrCashPositionFlowDto positionFlowDto);

    void updatePosition(Map<String, Object> params);

    BigDecimal selectSecPosition(EtrSecPositionFlowDto positionFlowDto);

    Date selectSecPositionMaxCdate(EtrSecPositionFlowDto positionFlowDto);

    String selectSecMaxCdate(EtrSecPositionFlowDto positionFlowDto);

    void updateSecPosition(Map<String, Object> params);

    List<RepoInfo> findRepoInfo(String finprodId);

    List<EtrSecPositionFlowDto> selectAllPositionByTotle(String tradeId);

    List<EtrCashPositionFlowDto> selectAllCashPositionByTotle(String tradeId);

    /**
     * 查询最新计划类净值型资产净值
     * @param params
     * @return
     */
    BigDecimal selectPlanNetValue(Map<String, Object> params);

    SecInfoModelDto getSecInfoModel(HashMap<String, Object> map);

    List<FinCashCalcRule> selectBondsIntResetList(Map<String, Object> param);

    List<FinCashPlanDto> getFinCashPlan(HashMap<String, Object> map);

    BigDecimal getCurSecAmt(Map<String, Object> param);

    /**
     * 查询交易对手的性质
     * @param param
     * @return check结果
     */
    String getCounterType(Map<String, Object> param);

    BigDecimal selectCommandPositionFlow(EtrSecPositionFlowDto etrSecPositionFlowDto);

    List<PtlSecPositionFlow> selectSecFlow(Map<String, Object> params);

    List<EtrSecPositionFlowDto> findNegateSecFlowByEntrustId(String entrustId);

    String selectAcctId(String secAcctId);

    // add by etliu 汇总表更新并发问题解决 20210511 start
    /**
     * 针对一笔流水，将该笔流水的数值加到指令现金的主表
     * @param record 一笔流水的数据
     * @return 更新条数
     */
    int updateByPrimaryKeySelectiveForFlow(EtrCashPosition record);

    /**
     * 针对一笔流水，将该笔流水的数值加到指令持仓的主表
     * @param record 一笔流水的数据
     * @return 更新条数
     */
    int updateByPrimaryKeySelectiveForSecFlow(EtrSecPosition record);
    // add by etliu 汇总表更新并发问题解决 20210511 end
	/**
	 * 获取汇总的标准券数量
	 * 
	 * @param param
	 */
	BigDecimal selectSumStandardTicket(Map<String, Object> param);

    /**
     * 查询场内指令已交易完成金额或者额度
     * @param map 委托代码
     * @return 场内指令已交易完成金额或者额度
     */
    BigDecimal getEtrExchangeFinishAmt(Map<String,Object> map);

    /**
     * 或者汇总交易份额
     * @param entrustId
     * @return
     */
    BigDecimal selectSumShareAmt(Map<String, Object> param);

    /**
     * 获取汇总交易金额
     * @param entrustId
     * @return
     */
    BigDecimal selectSumTradeAmt(Map<String, Object> param);


    /**
     * 根据股票代码获取昨日最新收盘价
     * @param finprodId
     * @return
     */
    BigDecimal getInterfPreClosePrice(@Param("finprodId") String finprodId, @Param("dealDate") Date dealDate);

    BigDecimal selectShengYuEntrustAmt(String entrustId);

    BigDecimal selectZhiLingChiCang(@Param("entrustId")String entrustId,@Param("finprodId")String finprodId);

    BigDecimal selectJiaoYiChiCang(@Param("entrustId")String entrustId, @Param("finprodId")String finprodId);
    /**
     * 系列户组合日间交易完成金额(日终和日间)
     * @param map
     * @return
     */
    BigDecimal getEtrExchangeFinishAmtMuilt(Map<String, Object> map);

    /**
     *
     * 查询指令可用现金(新表)
     * <功能详细描述>
     * @param portfolioId
     * @param acctId
     * @param chlFinprodId
     * @param hodingType
     * @param cdate
     * @param ccy
     * @param acctTypes
     * @return
     * @see
     * @since 1.0
     */
    BigDecimal queryEtrAvailableCashNew(PtlCashPositionFlowDto dto);
    /**
     *
     * 查询指令可用现金
     * <功能详细描述>
     * @param portfolioId
     * @param acctId
     * @param chlFinprodId
     * @param hodingType
     * @param cdate
     * @param ccy
     * @param acctTypes
     * @return
     * @see
     * @since 1.0
     */
    BigDecimal selectCommandAvailableCashFlowSum(PtlCashPositionFlowDto dto);

    // modify by etliu 20220226 start
    // 广农商二阶段 GNS-947:【广农商场内清算】场内债券存续交易生成的时候，指令现金流水处理有误
    int selectEtrCashPositionFlowCount(Map<String, Object> map);
    // modify by etliu 20220226 end

    /**
     * 根据汇总交易编号查询交易
     * @param totleTradeId
     * @return
     */
    List<TrdProductDeal> selectTrdProductDealByTotleTradeId(@Param("totleTradeId") String totleTradeId);
}

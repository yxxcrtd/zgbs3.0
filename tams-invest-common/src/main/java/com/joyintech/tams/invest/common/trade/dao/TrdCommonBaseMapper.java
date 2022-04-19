/*
 * 文件名：TrdCommonBaseMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.trade.dao;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.TrdStaSecInfoDto;
import com.joyintech.tams.invest.common.dto.mock.trade.MockTradeInfoDto;
import com.joyintech.tams.invest.common.dto.mock.trade.MockTrdProductDealDto;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.OrderInfoDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 交易基础组件调用Mapper
 *
 * @author 刘二通
 * @version 1.0 2018年11月2日
 * @see TrdCommonBaseMapper
 * @since 1.0
 */
@Repository
public interface TrdCommonBaseMapper {

    /**
     * 查询产品登记编码
     *
     * @param paramMap 参数
     * @return 产品登记编码
     * @see
     * @since 1.0
     */
    String selectProdRegistCode(Map<String, Object> paramMap);

    /**
     * 根据工作流代码查询金融产品交易附属业务流程
     * @param workflowId 工作流代码
     * @return 金融产品交易附属业务流程信息
     * @see
     * @since 1.0
     */
    List<WflTrdProductDealAdd> selectWflTrdProductDealAdd(@Param("workflowId") String workflowId);

    /**
     * 根据工作流代码查询指令编号列表
     * @param workflowId 工作流代码
     * @return 指令编号列表
     * @see
     * @since 1.0
     */
    List<WflStlPayOrders> selectOrderIdList(@Param("workflowId") String workflowId);

    /**
     * 利用金融产品代码作为条件把资产负债报送信息表中的报送状态更改为是
     *
     * @param paramMap 参数
     * @return 更新结果
     * @see
     * @since 1.0
     */
    int updateRepAssetDebt(Map<String, Object> paramMap);

    /**
     * 检索已质押标准券额度
     *
     * @param paramMap paramMap
     * @return 已质押标准券信息Dto列表
     * @see
     * @since 1.0
     */
    List<TrdStaSecInfoDto> selectPledgeStandardPrinAmt(Map<String, Object> paramMap);
    

	/**
     * 根据工作流代码查询金融产品交易流程
     * @param param
     * @return 金融产品交易流程信息
     * @see
     * @since 1.0
     */
    List<AllTradeCommonInputDto> selectWflTrdProductDeal(Map<String, Object> param);

    // 2.0自动生成反向交易 bug修改 start
    /**
     * 查询系统参数
     *
     * @return 系统参数
     */
    String selectParam(Map<String, Object> param);
    // 2.0自动生成反向交易 bug修改 end
    
    /**
     * 指令接收交易录单根据委托代码查询流程中已录入金额
     * @param map map
     * @return BigDecimal
     * @author wangguiqi
     * @since 1.0
     */
	ArrayList<BigDecimal> queryWflPrinAmt(Map<String, Object> map);
    /**
     * 指令接收交易录单根据委托代码查询委托金额
     * @param entrustId String
     * @return BigDecimal
     * @author wangguiqi
     * @since 1.0
     */
	BigDecimal queryEntrustAmtByEntrustId(String entrustId);

    List<PtlTrade> findPtlTradeByTradeIds(List<TrdProductDeal> trdProductDeals1);

    List<TrdProductDealAdd> findDealAddByTradeIds(List<TrdProductDeal> trdProductDeals1);

    List<TrdProductDeal> findDealByTotalIdForInterf(Map<String, Object> map);

    void deleteRepTradeByTradeId(@Param("tradeId")String bizCode);

    void inertRevokeFeeDetailByTradeId(@Param("bizCode") String bizCode, @Param("updateUser")String updateUser);

    void insertRevokePtlTrade(@Param("deal")PtlTrade deal, @Param("updateUser")String updateUser);

    void insertRevokeTrdProductDealAdd(@Param("bizCode")String bizCode, @Param("updateUser")String updateUser);

    void insertRevokePaTrdProductDeal(@Param("bizCode")String bizCode, @Param("updateUser")String updateUser);

    List<StlPayOrders> findOrdersByOrderId(@Param("tradeId") String bizCode);

    List<TrdProductDeal> findDealByTotalId(Map<String, Object> map);

    void inertRevokeFeeTotalByTradeId(@Param("bizCode")String bizCode, @Param("updateUser")String updateUser);

    List<String> findSubTotalTradeId(@Param("bizCode")String bizCode);

    List<PtlTrade> findPtlTradeByBusiNo(String busiNo);

    TrdProductDeal selectByTotleTradeId(Map<String, Object> map);

    void insertSelectFileInfo(Map<String, Object> params);

    void insertSelectFileInfoByFileNo(Map<String, Object> params);

    List<WflTrdProductDealAdd> checkEtrToTrdInFlow(String entrustId);

    String selectWorkflowId(String bizCode);

    boolean isNotActPortfolio(String portfolioId);

    List<String> checkTotleWflData(String findKey);
    
    // add by zhangrui1 20200511 判断转仓交易 start
    /**
     * 是否组合代码，可用此方法判断交易对手是否内部交易对手
     * 
     * @param portfolioId 组合代码/交易对手
     * @return true:是组合代码/内部交易对手
     */
    boolean isPortfolioId(String portfolioId);
    // add by zhangrui1 20200511 判断转仓交易 end

    /**
     * 根据交易编号查询另一笔未撤销的转仓交易信息
     * add by hpp 2020-09-17
     * @param tradeId
     * @return
     */
    TrdProductDeal selectZCDeal(@Param("tradeId") String tradeId);

    TrdProductDeal selectZCDealByTradeId(@Param("tradeId") String tradeId);

    /**
     * 根据交易编号查询已撤销数据条数
     *
     * @param paramMap 参数
     * @return 已撤销数据条数
     * @see
     * @since 1.0
     */
    int selectRevokedDataCount(Map<String, Object> paramMap);


    List<BigDecimal> selectProAvailableCash(Map<String, Object> paramMap);

	List<WflTrdProductDeal> findDealByBtradeId(String tradeId);

    /**
     * 多组合交易是否可以撤销
     * @param map
     * @return
     */
    int isCanRevoke(Map<String,Object> map);

    /**
	 *
	 * 模拟试算
    * 金融产品交易主方法查询
	 * <功能详细描述>
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	List<MockTradeInfoDto> selectTradeinfodeal(Map<String, Object> param);

    /**
	 *
	 * 模拟试算
     * 金融产品交易查询
	 * <功能详细描述>
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	List<MockTrdProductDealDto> selectTradedeal(Map<String, Object> param);

	/**
	 *
	 * 模拟试算
     * 费用交易列表查询
	 * <功能详细描述>
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	List<MockTrdProductDealDto> selectFyTradedeal(Map<String, Object> param);

    /**
     * 是否是内转交易
     * @param map 交易参数
     * @return 统计条数
     */
    int checkRevokeRTrade(Map<String, Object> map);

    int checkRevokeBTrade(Map<String, Object> map);

    // add by zhangrui1 20200619 取得反向交易编号 start
    /**
     * 取得反向交易编号
     *
     * @param tradeId 交易编号/汇总交易编号
     * @return 反向交易编号
     */
     String queryRTradeId(String tradeId);

    // add by zhangrui1 根据交易代码取指令代码 start
    /**
     * 根据交易代码反向取指令编号
     *
     * @param tradeId 交易代码
     * @return 指令编号
     */
    EtrEntrust selectEntrustInfo(String tradeId);
    // add by zhangrui1 根据交易代码取指令代码 end

    /**
     * 查询基金最新净值
     * @param map 参数
     * @return 查询结果集
     * @throws Exception 异常
     */
    BigDecimal getFundLatestValuation(Map<String, Object> map);
    // add by zhangrui1 20200619 取得反向交易编号 end

    //查询转仓前一天的(其他应收款-利息)的科目余额
    Map<String, Object> getYSKLXBalance(Map<String, Object> map);

    int selectTrdOnPayDate(Map<String, Object> params);

    // add by etliu 20210421 权益类资产相关代码合并 start
    /**
     * 根据资产代码查询流程中已录入金额
     * @param map map
     * @return BigDecimal
     * @author chaowei
     * @since 1.0
     */
    BigDecimal queryWflPrinAmtByAssetId(Map<String, Object> map);
    // add by etliu 20210421 权益类资产相关代码合并 end
	
	List<PtlPortfolio> selectPortfolioIds(Map<String, Object> map);

    /**
     * 取得反向交易编号
     *
     * @param map 交易编号/汇总交易编号
     * @return 反向交易编号
     */
    List<TrdProductDeal> queryRTradeIds(Map<String, Object> map);

    /**
     * 根据投资组合代码查询对应的理财产品信息
     * @param portfolidId
     * @return
     */
    FinProduct queryFinProductByPortfolidId(String portfolidId);

    //多级法人改造start add by hxy 20200617
    /**
     * 银行模式时，返回机构
     * @return
     */
    public String selectBankModeOrgName();
    /**
     * 联社机构时，选择账套，返回机构
     * @return
     */
    public String selectAssociationModeOrgName(Map<String,Object> orgmap);
    /**
     * 联社机构时，选择账套组返回机构
     * @return
     */
    public String selectAssociationModeOrgGrpName(Map<String,Object> orgmap);
    //多级法人改造end add by hxy 20200617

    /**
     * 查询标的现金流计划list
     *
     * @param paMap 传递参数
     * @return List<TargetCashPlan>
     * @see
     * @since 1.0
     */
    List<FinCashPlan> getTargetCashPlanList(Map<String, Object> paMap);

    /**
     * 根据採番名採番
     *
     * @param param 採番参数
     * @return 採番结果
     * @see
     */
    Long getSequenceIndexData(Map<String, String> param);


    /**
     * 根据组合ID查询母组合ID
     * @param portfolioId 组合ID
     * @return
     */
    public String selectParentPortfolioIdByPortfolioId(String portfolioId);

    /**
     * 根据市场代码查询金融产品代码
     *
     * @param param 入参
     * @return 金融产品代码
     * @see
     * @since 1.0
     */
    String queryFinprodIdByFinprodMarketId(Map<String, Object> param);

    // modify by etliu 20220110 start
    /**
     * 根据首期成交编号查询协议回购首期的交易编号
     * @param paraMap 参数
     * @return 协议回购首期的交易编号
     */
    String selectHgsqFinprodId(Map<String, Object> paraMap);
    // modify by etliu 20220110 end


    /**
     * 查询指令详情信息
     * @param entrustId
     * @return
     */
    List<EtrEntrustDetail> selectEtrEntrustDetailByEntrustId(String entrustId);

    /**
     * 查询关联交易编号
     * @param tradeId
     * @return
     */
    String selectFTradeId(String tradeId);

    /**
     * 查询下单对应的委托编号信息
     * @param dealNo
     * @return
     */
    InfContractDetailfile selectInfContractDetailfile(String dealNo);

    /**
     * 查询下单信息
     * @param dealNo
     * @return
     */
    OrderInfoDto selectOrderInfo(String dealNo);

    PtlCapacctInfo getPtlCapAcctInfo(Map<String, Object> map);

    List<FinPledgeDispDetailTemp> getFinPledgeDispDetailTemps(Map<String, Object> map);

    FinDealPledge selectFinDealPledgeByPrimaryKey(FinDealPledgeKey finDealPledgeKey);

    /**
     * 根据金融产品代码，日期，查询出当日折算率
     *
     * @param finprodId 金融产品代码
     * @return EtrEntrust
     * @see
     * @since 1.0
     */
    BigDecimal queryConverseRadio(@Param("finprodId") String finprodId, @Param("vdate") Date vdate);

    /**
     * 获取未完全匹配的成交明细汇总数据数量
     * @return
     */
    Integer getUnFinishMatchSettleTransDataNum(Map<String, Object> map);
}

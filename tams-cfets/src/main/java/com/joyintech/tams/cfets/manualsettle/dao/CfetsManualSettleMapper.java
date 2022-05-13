package com.joyintech.tams.cfets.manualsettle.dao;

import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleDealInfo;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleFlowRespond;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleInfoOtherListRespond;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleInfoRespond;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleTradeListRespond;
import com.joyintech.tams.cfets.manualsettle.dto.PortIntFenPeiInfoDto;
import com.joyintech.tams.cfets.manualsettle.dto.PortSettleinfoDto;
import com.joyintech.tams.cfets.manualsettle.dto.PtlFlowsDto;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdBondInvestTradeDto;
import com.joyintech.tams.cfets.trdquery.dto.CfetsTrdSettleInfoResponse;
import com.joyintech.tams.common.base.entity.TrdBankManualSetDetail;
import com.joyintech.tams.common.base.entity.TrdBankManualSettleInfo;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.User;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 手工交收
 * @author sjj
 * @version 1.0 2021年11月12日
 * @see CfetsManualSettleMapper
 * @since 1.0
 */
@Repository
public interface CfetsManualSettleMapper {


    /**
     * 指令列表分页查询
     * @param pageParam 参数
     * @return List<CfetsManualSettleTraderListRespond>
     */
    List<CfetsManualSettleTradeListRespond> findPage(Map<String, Object> pageParam);

    /**
     * 交收流水列表分页查询
     *
     * @param pageParam 参数
     * @return List<CfetsManualSettleFlowRespond>
     */
    List<CfetsManualSettleFlowRespond> findManualSettleFlow(Map<String, Object> pageParam);

    /**
     * 交收流水列表分页查询
     *
     * @param pageParam 参数
     * @return CfetsManualSettleInfoRespond
     */
    CfetsManualSettleInfoRespond findManualSettleInfo(Map<String, Object> pageParam);

    /**
     * 交收流水列表分页查询(临时表)
     *
     * @param pageParam 参数
     * @return CfetsManualSettleInfoRespond
     */
    CfetsManualSettleInfoRespond findTempManualSettleInfo(Map<String, Object> pageParam);
    /**
     * 组合分配信息List
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<PortIntFenPeiInfoDto> findPortIntFenPeiInfoList(Map<String, Object> pageParam);
    /**
     * 一级申购组合分配信息List
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<PortIntFenPeiInfoDto> findBidPortIntFenPeiInfoList(Map<String, Object> pageParam);

    /**
     * 组合分配信息List(临时交易表查询)
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<PortIntFenPeiInfoDto> findTempPortIntFenPeiInfoList(Map<String, Object> pageParam);

    /**
     * 成交确认明细查询
     *
     * @param tradeId 参数
     * @return List<CfetsManualSettleDealInfo>
     */
    List<CfetsManualSettleDealInfo> getDealDetailsByTradeId(@Param("tradeId") String tradeId,@Param("isCancel") String isCancel);

    /**
     * 登记编号查询
     *
     * @param tradeId 参数
     * @return String
     */
    String selectRegTradeId(@Param("tradeId") String tradeId,@Param("entrustId") String entrustId);

    /**
     * 登记编号查询
     *
     * @param tradeId 参数
     * @return String
     */
    String selectRegTradeIdBytradeId(@Param("tradeId") String tradeId);

    /**
     * 登记编号查询
     *
     * @param totleTradeId 参数
     * @return String
     */
    String selectRegTotleTradeIdByTotleTradeId(@Param("totleTradeId") String totleTradeId);

    /**
     * 登记编号查询
     *
     * @param totleTradeId 参数
     * @return String
     */
    String selectIbuQuoteIdByTotleTradeId(@Param("totleTradeId") String totleTradeId);

    /**
     * 报价编号查询
     *
     * @param totleTradeId 参数
     * @return String
     */
    String selectIbuBondQuoteIdByTotleTradeId(@Param("totleTradeId") String totleTradeId);

    /**
     * 报价编号查询
     *
     * @param tradeId 参数
     * @return String
     */
    String selectQuoteIdBytradeId(@Param("tradeId") String tradeId);

    /**
     * 划款指令查询
     *
     * @param tradeId 参数
     * @return List<StlPayOrdersDto>
     */
    List<StlPayOrdersDto> getStlPayOrdersByTradeId(@Param("tradeId") String tradeId);

    /**
     * 成交确认明细查询
     *
     * @param tradeId 参数
     * @return String
     */
    String qryEntrustId(@Param("tradeId") String tradeId);

    /**
     * 成交确认明细查询(临时表查询)
     *
     * @param tradeId 参数
     * @return List<CfetsManualSettleDealInfo>
     */
    List<CfetsManualSettleDealInfo> getTempDealDetailsByTradeId(@Param("tradeId") String tradeId);

    /**
     * 查询证券账户
     * @param param
     * @return
     */
    String qryPortfolioSecAcctId(Map<String, Object> param);

    /**
     * 查询托管户
     * @param param
     * @return
     */
    String qryPortfolioCashAcctId(Map<String, Object> param);

    /**
     * 批量更新银行间手工交收明细
     * @param listTrdBnakSetDetails
     * @return
     */
    int updateByTradeId(@Param("listTrdBnakSetDetails") List<TrdBankManualSetDetail> listTrdBnakSetDetails);

    /**
     * 插入/更新交收信息表
     * @param trdBankManualSettleInfo
     * @return
     * @see
     * @since 1.0
     */
    void mergeInfo(TrdBankManualSettleInfo trdBankManualSettleInfo);

    /**
     * 查询待修改记录
     * @param pageParam
     * @return
     */
    List<PortSettleinfoDto> queryPortSettleinfo(Map<String, Object> pageParam);

    /**
     * 查询待修改记录(临时表)
     * @param pageParam
     * @return
     */
    List<PortSettleinfoDto> queryTempPortSettleinfo(Map<String, Object> pageParam);

    /**
     * 查询金融产品交易附属表数据
     * @param tradeId
     * @return
     */
    List<TrdProductDealAdd> queryTrdProductDealsByTotalTradeId(@Param("tradeId") String tradeId);

//    /**
//     * 查询金融产品交易表数据
//     * @param tradeId
//     * @return
//     */
//    TrdProductDeal queryTrdProductDealsByTradeId(@Param("tradeId") String tradeId);

    /**
     * 一级市场投标交收流水列表分页查询
     *
     * @param pageParam 参数
     * @return CfetsManualSettleInfoF32Respond
     */
    CfetsManualSettleInfoOtherListRespond findManualSettleInfoOther(Map<String, Object> pageParam);

    /**
     * 一级申购交收流水列表分页查询
     *
     * @param pageParam 参数
     * @return CfetsManualSettleInfoF32Respond
     */
    CfetsManualSettleInfoOtherListRespond findManualSettleInfoBid(Map<String, Object> pageParam);

    /**
     * 一级市场投标交收流水列表分页查询
     *
     * @param pageParam 参数
     * @return CfetsManualSettleInfoF32Respond
     */
    CfetsManualSettleInfoOtherListRespond findTempManualSettleInfoOther(Map<String, Object> pageParam);

    /**
     * 根据用户名查找真实姓名
     * <功能详细描述>
     * @param userName
     * @return
     * @see
     * @since 1.0
     */
    User getRealNameByUserName(@Param("userName")String userName);

    /**
     *
     * 根据查询参数查询留痕表数据
     * <功能详细描述>
     * @param wflValidInputParameter 函数输入参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<String> getBusinessData(WflValidInputParameter wflValidInputParameter);

    /**
     * 查询债券交易基本信息
     * @param params
     * @return
     */
    CfetsTrdBondInvestTradeDto getTrdBondInvestTradeDto(Map<String, Object> params);

    /**
     * 回购质押券信息
     * @param tradeId
     * @return
     */
    List<PledgeBondInfoDto> getPledgeListByTradeId(@Param("tradeId") String tradeId);

    /**
     * 查询质押式回购交易基本信息
     *
     * @date 2022/1/26
     * @param params
     * @return void
     */
    CfetsTrdSettleInfoResponse findManualSettleInfoRepo(Map<String,Object> params);

    List<PortIntFenPeiInfoDto> findManualSettleFenPei(Map<String,Object> params);

    int countWflEtr(Map<String, Object> param);

    /**
     * 报价编号查询
     * @param tradeId1
     * @return
     */
    String selectIbTradeIdBytradeId(@Param("tradeId1") String tradeId1);

    /**
     * 报价编号查询报价信息
     *
     * @param tradeId
     * @return
     */
    List<PtlFlowsDto> selectIbQuoteBytradeId(@Param("tradeId") String tradeId);
}

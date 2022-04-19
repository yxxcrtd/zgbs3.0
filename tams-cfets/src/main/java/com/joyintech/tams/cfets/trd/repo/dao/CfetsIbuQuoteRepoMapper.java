package com.joyintech.tams.cfets.trd.repo.dao;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.tams.cfets.trd.repo.dto.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 本币交易-质押式回购交易 Mapper
 *
 * @author jiangpeng
 * @date 2021/12/4 15:39
 */
@Repository
public interface CfetsIbuQuoteRepoMapper {
    /**
     * 头寸信息
     *
     * @param RequestDto 条件
     * @return java.util.Map
     * @author jiangpeng
     * @date 2021/12/4 15:39
     */
    Map queryPositionInfo(Map RequestDto);

    /**
     * 查询质押券信息
     *
     * @param param 条件
     * @return 质押券列表
     */
    List<RepoEntrustPledgeDto> selectRepoEntrustPledges(Map<String, Object> param);

    /**
     * 委托列表初始化
     *
     * @param param 条件
     * @return 指令列表
     */
    List<CfetsRepoDealEntrustOrderDto> listEntrust(Map<String, Object> param);

    /**
     * 查询质押券信息
     *
     * @param param 条件
     * @return 质押券列表
     */
    List<RepoEntrustPledgeDto> selectRepoEntrustPledgesInit(Map<String, Object> param);

    /**
     * 查询可用质押券信息 按组合、债券维度汇总
     *
     * @param param 条件
     * @return 质押券列表
     */
    List<RepoEntrustPledgeDto> selectUsableEntrustPledges(Map<String, Object> param);

    /**
     * 查询可用质押券信息
     *
     * @param param 条件
     * @return 质押券列表
     */
    List<RepoEntrustPledgeDto> selectUsablePledges(Map<String, Object> param);

    /**
     * 已起息未到期的银行间债券
     *
     * @param param 条件
     * @return 质押券列表
     */
    List<RepoEntrustPledgeDto> selectBondInfo(Map<String, Object> param);

    /**
     * 按指令维度汇总质押额
     *
     * @param param
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.RepoEntrustPledgeDto>
     * @author jiangpeng
     * @date 2022/1/5 11:21
     */
    List<RepoEntrustPledgeDto> findEtrPrinByEtrIds(Map<String, Object> param);

    /**
     * 按指令维度汇总指令额度
     *
     * @param etrId 指令代码
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2021/12/11 16:27
     */
    CfetsRepoDealEntrustOrderDto findEtrLastNumByEtrId(@Param("etrId") String etrId, @Param("totleTradeId") String totleTradeId);

    /**
     * 查询产品质押债券 证券账户
     *
     * @param param
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/13 16:13
     */
    String qryPortfolioSecAcctId(Map<String, Object> param);

    /**
     * 查询产品资金账户
     *
     * @param param
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/13 16:14
     */
    String qryPortfolioCashAcctId(Map<String, Object> param);

    /**
     * @param param
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.DealTransactionDetailsDto>
     * @author jiangpeng
     * @date 2021/12/14 15:31
     */
    List<RepoDealTransactionDetailsDto> listDealTransactionDetails(Map<String, Object> param);

    /**
     * 查询质押券信息
     *
     * @param totleTradeId 汇总交易编号
     * @return java.util.List<com.joyintech.tams.common.base.entity.RegFinPledge>
     * @author jiangpeng
     * @date 2021/12/11 16:24
     */
    List<RegFinPledge> listRegFinPledge(@Param("totleTradeId") String totleTradeId);

    List<RegFinProductDeal> listRegFinProductDeal(@Param("totleTradeId") String totleTradeId);

    RegFinProductDealTotal listRegFinProductDealTotal(@Param("totleTradeId") String totleTradeId);

    List<RegTradeFee> listRegTradeFee(@Param("totleTradeId") String totleTradeId);

    List<RegTradeFeeTotal> listRegTradeFeeTotal(@Param("totleTradeId") String totleTradeId);

    /**
     * 当日登记
     *
     * @param param
     * @return
     */
    List<CfetsRepoDayRegDto> listDayReg(Map<String, Object> param);

    /**
     * 当日登记 本币业务导出
     *
     * @param param
     * @return
     */
    List<CfetsRepoDayRegDto> listDayRegExport(Map<String, Object> param);

    /**
     * 查看页面查询
     *
     * @param totleTradeId
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto
     * @author jiangpeng
     * @date 2021/12/15 10:18
     */
    CfetsRepoDealQuoteCommonDto selectCommDtoByTotleTradeId(@Param("totleTradeId") String totleTradeId);

    /**
     * 查询组合信息
     *
     * @param totleTradeId
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.RepoPtlAllocationDto>
     * @author jiangpeng
     * @date 2021/12/15 10:53
     */
    List<RepoPtlAllocationDto> listRepoPtlAllocationDtos(@Param("totleTradeId") String totleTradeId);

    /**
     * 查询质押券信息
     *
     * @param param
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.RepoEntrustPledgeDto>
     * @author jiangpeng
     * @date 2021/12/14 17:10
     */
    List<RepoEntrustPledgeDto> listRepoEntrustPledgeDtos(Map<String, Object> param);

    /**
     * 委托列表初始化
     *
     * @param param 条件
     * @return 指令列表
     */
    List<CfetsRepoDealEntrustOrderDto> listEntrustAvaNum(Map<String, Object> param);

    /**
     * 查询已经做了赎回登记的债券行权计划
     *
     * @param param 条件
     * @return 指令列表
     */
    List<FinExercisePlan> findFinExercisePlan(Map<String, Object> param);

    /**
     * 资产五级分类联动检索
     * <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<FinProductTypeConfig> getAssetFiveType(FinProductTypeConfig param);

    int countWflEtr(Map<String, Object> param);

    /**
     * 获取机构信息
     * @return
     */
    List<Map<String, String>> selectAllCustomer();

    /**
     * 根据工作流代码获取entity
     * <功能详细描述>
     * @param wflValidInputParameter
     * @return
     * @see
     * @since 1.0
     */
    ProcessMark getBusinessInfoByPageIdAndPk(WflValidInputParameter wflValidInputParameter);

    /**
     * 查询指令完成额度
     *
     * @param param
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/3/29 10:57
     */
    BigDecimal selectFinshAmt(Map<String, Object> param);

    int checkWflData(Map<String, Object> param);
}

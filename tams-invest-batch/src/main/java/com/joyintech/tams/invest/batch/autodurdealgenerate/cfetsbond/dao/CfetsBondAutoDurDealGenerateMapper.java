/*
 * 文件名：BondAutoDurDealGenerateMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年12月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.dao;

import com.joyintech.tams.common.base.entity.RegFinProductDeal;
import com.joyintech.tams.common.base.entity.RegFinProductDealAdd;
import com.joyintech.tams.common.base.entity.RegFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.dto.CfetsManualSettleDealInfo;
import com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.dto.CfetsTrdConfirmMatchDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurationDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.TrdRepoDurationDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Mapper
 *
 * @author etliu
 * @version 1.0 2018年12月11日
 * @see CfetsBondAutoDurDealGenerateMapper
 * @since 1.0
 */
@Repository
public interface CfetsBondAutoDurDealGenerateMapper {

    List<TrdBondDurationDto> findAllData(Map<String, Object> paramMap);

    List<FinAssetIntInfo> findAssetIntInfo(Map<String, Object> params);

    TrdBondDurationDto findEffectiveData(Map<String, Object> paramMap);

    //查询行权赎回待处理数据
    List<RegFinProductDealTotal> findShData(Map<String, Object> paramMap);

    //查询金融产品交易明细数据
    List<RegFinProductDeal> queryRegDealInfo(Map<String, Object> map);

    //新建下行成交匹配关系
    int insertTrdQutoMatchInfo(CfetsTrdConfirmMatchDto record);

    //查询托管户
    String qryPortfolioCashAcctId(Map<String, Object> param);

    /**
     * 查询证券账户
     * @param param
     * @return
     */
    String qryPortfolioSecAcctId(Map<String, Object> param);

    /**
     * 成交确认明细查询
     *
     * @param tradeId 参数
     * @return List<CfetsManualSettleDealInfo>
     */
    List<CfetsManualSettleDealInfo> getDealDetailsByTradeId(@Param("tradeId") String tradeId);

    List<TrdFinProductDealTotal> selectTrdFinProductDealTotalData(Map<String, Object> paramMap);

    String getSecAcctId(@Param("portfolioId") String portfolioId,@Param("finprodId") String finprodId,@Param("tradeMarket") String tradeMarket);

    TrdRepoDurationDto findRepoEffectiveData(Map<String, Object> param);

    /**
     * 存续期获取业务模式
     * @param paramMap
     * @return
     */
    String findInvAimData(Map<String, Object> paramMap);

    /**
     * 根据资产代码查询cashId
     *
     * @param paramMap 入参
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/7/6 14:29
     */
    String findCashIdByFinprodId(Map<String, Object> paramMap);
}

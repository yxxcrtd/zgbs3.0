package com.joyintech.service.basicdata.systrdconfig.dao;

import com.joyintech.service.basicdata.systrdconfig.dto.StandardCouponReConversionRateDto;
import com.joyintech.service.basicdata.systrdconfig.dto.*;
import com.joyintech.tams.common.base.entity.MstCounterPartyInfo;
import com.joyintech.tams.common.base.entity.SppiImportData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface InterfSysTrdConfigMapper {


    /**
     *
     * @Description //查询最小价差
     * @Param [map]
     * @return com.joyintech.service.basicdata.systrdconfig.dto.InterfSysTrdConfigDto
     * @version 1.0
     * @see
    **/
    List<MinPirceDto> getMinPrice(Map<String,Object> map);

    Map<String, Object> getFinProdInfo(Map<String,Object> map);

    List<MinPirceDto> getMinPriceConfig(Map<String,Object> map);


    /**
     *
     * @Description //最小交易单位
     * @Param [map]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @version 1.0
     * @see
    **/
    List<MinTrdUnitLimitDto> getMinTrdUnitLimit(Map<String,Object> map);
    /**
     *
     * @Description //查询交易时间段
     * @Param []
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @version 1.0
     * @see
     **/
     List<TrdTimeConfigDto> getTrdTimeConfig(Map<String,Object> map);

     List<TrdOthCodeInfoDto> getTrdOthCodeInfo (Map<String,Object> map);

    /**
     * 根据交易对手代码模糊查询交易对手
     * @param params
     * @return
     */
    List<Map<String,Object>> selectMstCounterParty(Map<String, Object> params);

    /**
     * 根据交易对手代码和席位代码模糊查询交易对手席位
     * @param params
     * @return
     */
    List<Map<String, Object>> selectCounterSeatId(Map<String, Object> params);
    /**
     * 根据交易对手代码和对手方交易员代码模糊查询对手方交易员
     * @param params
     * @return
     */
    List<Map<String, Object>> selectCounterTraderId(Map<String, Object> params);

    /**
     * 查询交易对手信息
     * @param params
     * @return
     */
    MstCounterPartyInfo findMstCounterPartyInfo(Map<String, Object> params);

    List<StockInfoDto> selectStockInfo(Map<String, Object> params);


    List<StandardCouponReConversionRateDto> getMoreConvrate (Map<String,Object> map);
    List<StandardCouponReConversionRateDto> getAllConvrate (Map<String,Object> map);

    List<PurchQuotaInfoDto> getPurchQuotaInfo(Map<String, Object> params);

    List<TradeInfoProResponse> getDealerTraderInfo(TradeInfoProRequest dto);

    Map<String, Object> getMstRepoProductInfo(Map<String,Object> map);

    //查询资产是否进行了SPPI测试
    SppiImportData queryAssetIsSppiTest(Map<String, Object> param);
    //是否有行权计划
    int countBondHasEexrPlan(Map<String, Object> param);
}

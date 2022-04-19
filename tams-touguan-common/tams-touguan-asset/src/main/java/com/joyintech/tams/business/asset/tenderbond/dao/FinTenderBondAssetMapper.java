package com.joyintech.tams.business.asset.tenderbond.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.business.asset.tenderbond.dto.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FinTenderBondAssetMapper {

    /**
     * 列表查询
     *
     * @param search
     * @return
     */
    List<FinTenderBondAsset001Dto> selectForPage(FinTenderBondAssetSearch search);

    /**
     * 查询评级最低
     *
     * @param finprodMarketId
     * @param platformDate
     * @return
     */
    List<MstBondCreditRatingDto> selectBondCreditRating(@Param("finprodMarketId") String finprodMarketId, @Param("platformDate") String platformDate);

    /**
     * 查询资产代码列表
     *
     * @param param
     * @return
     */
    List<SelectOptionDto> getFinprodIdForSelect(Map<String, Object> param);

    /**
     * 查询上市代码列表
     *
     * @param param
     * @return
     */
    List<SelectOptionDto> getAppearMarketIdForSelect(Map<String, Object> param);

    /**
     * 查询跨市场代码
     *
     * @param param
     * @return
     */
    List<SelectOptionDto> getCrossMarketCodeForSelect(Map<String, Object> param);

    /**
     * 获取基准利率行情列表
     *
     * @param param
     * @return
     */
    List<SelectOptionDto> getIntRateForSelect(Map<String, Object> param);

    /**
     * 查询金融产品代码
     *
     * @param param
     * @return
     */
    String selectFinprodId(Map<String, String> param);

    /**
     * 删除时判断金融产品代码是否使用过
     *
     * @param finprodId
     * @return
     */
    Integer countFinprodIdUse(@Param("finprodId") String finprodId, @Param("isCancel") String isCancel);

    /**
     * 判断是否存在上市流通交易数据
     *
     * @param finprodId
     * @return
     */
    Integer countListing(@Param("finprodId") String finprodId);

    /**
     * 根据查询参数查询留痕表数据
     *
     * @param wflValidInputParameter
     * @return
     */
    List<String> getBusinessData(WflValidInputParameter wflValidInputParameter);

    /**
     * 根据金融产品代码
     *
     * @param finprodId
     * @return
     */
    List<String> selectTotleTradeId(@Param("finprodId") String finprodId);

    /**
     * 平台日
     *
     * @param params{ iplatformDate:平台日
     *                }
     * @return
     */
    FinTenderBoundAsset004Dto getBoundMarketInfo(Map<String, Object> params);

    /**
     * 债券付息信息
     *
     * @param param
     * @return
     */
    List<FinTenderBoundAsset004PayHSrcSecSchedulDto> selectWindBondSchedule(Map<String, Object> param);
}

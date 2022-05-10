package com.joyintech.tams.business.asset.tenderbond.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.business.asset.tenderbond.dto.*;
import com.joyintech.tams.business.asset.tenderbond.dto.FinProductAddDto;
import com.joyintech.tams.business.asset.tenderbond.dto.FinProductIssueDto;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBondAsset002Dto;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBondAsset003Dto;
import com.joyintech.tams.common.base.entity.FinProductTypeConfig;
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
    Integer countFinprodIdUse(@Param("finprodId") String finprodId, @Param("isCancel") String isCancel,@Param("ps") String ps);

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
    String selectTotleTradeId(@Param("finprodId") String finprodId);

    /**
     * 获取接口表标的数据
     *
     * @param param
     * @return
     */
    FinTenderBondAsset002Dto getProductFromInterface(Map<String, String> param);

    /**
     * 获取接口表标的附属表数据
     *
     * @param param
     * @return
     */
    FinProductAddDto getProductAddFromInterface(Map<String, String> param);

    /**
     * 获取接口表标的发行数据
     *
     * @param param
     * @return
     */
    FinProductIssueDto getProductIssueFromInterface(Map<String, String> param);

    /**
     * 根据行内三级分类获取wind分类
     * @param param
     * @return
     */
    FinProductTypeConfig getWindType(FinProductTypeConfig param);

    /**
     * 获取上市流通数据
     *
     * @param finprodId
     * @return
     */
    FinTenderBondAsset003Dto getByFinProductId(@Param("finprodId") String finprodId);
}

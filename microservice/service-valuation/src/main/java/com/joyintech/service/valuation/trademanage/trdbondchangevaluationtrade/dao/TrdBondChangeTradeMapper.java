/*
 * 文件名：TrdBondInvestTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dao;


import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeDto;
import com.joyintech.tams.common.base.entity.FinProduct;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 债券交易Mapper
 *
 * @author Zane.Y
 * @version 1.0 2018年9月7日
 * @see TrdBondChangeTradeMapper
 * @since 1.0
 */
@Repository(value="serviceTrdBondChangeTradeMapper")
public interface TrdBondChangeTradeMapper {

    /**
     * 检索债券交易列表
     *
     * @param paraMap 参数
     * @return 债券交易列表
     * @see
     * @since 1.0
     */
    List<TrdBondChangeTradeDto> selectDeal(Map<String, Object> paraMap);

    /**
     *
     * 转出证券名称文本框信息
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getAssetId(Map<String, Object> map);

    /**
     *
     * 正股代码联想
     * <功能详细描述>
     * @param map 用户输入值
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getStockId(Map<String,Object> map);


    FinProduct selectInfoBySecId(Map<String, Object> map);

    int delBokDetailIdByTradeId(Map<String, Object> map);

    List<SelectOptionDto> getSecManageAcctId(Map<String, Object> param);

    List<SelectOptionDto> getChannelId(Map<String, Object> params);

    int querySequences();

    TrdBondChangeTradeDto queryTradeInfoByTradeId(String tradeId);

    List<SelectOptionDto> getSecExgId(Map<String, Object> map);

    List<SelectOptionDto> getFundId(Map<String, Object> map);

    List<SelectOptionDto> getCAssetId(Map<String, Object> map);

}

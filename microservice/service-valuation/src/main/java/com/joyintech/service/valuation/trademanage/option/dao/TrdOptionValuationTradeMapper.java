/*
 * 文件名：TrdBondInvestTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.option.dao;


import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.option.dto.TrdOptionValuationTrade001Dto;
import com.joyintech.service.valuation.trademanage.option.dto.TrdOptionValuationTrade002Dto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 场外期权交易Mapper
 *
 * @author sunshauishuai
 * @version 1.0 2022年01月10日
 * @see TrdOptionValuationTradeMapper
 * @since 1.0
 */
@Repository(value="serviceTrdOptionValuationTradeMapper")
public interface TrdOptionValuationTradeMapper {

    /**
     * 检索场外期权交易列表
     *
     * @param paraMap 参数
     * @return 债券交易列表
     * @see
     * @since 1.0
     */
    List<TrdOptionValuationTrade001Dto> selectDeal(Map<String, Object> paraMap);

    /**
     * 合约标的下拉框
     * @param pageParam
     * @return
     */
    List<SelectOptionDto> selectTargetIdAbbr(Map<String, Object> pageParam);

    int delBokDetailIdByTradeId(Map<String, Object> map);

    int querySequences();

    TrdOptionValuationTrade001Dto queryTradeInfoByTradeId(String tradeId);

    TrdOptionValuationTrade002Dto queryInfoByTradeId(String tradeId);

    /**
     * 交易对手下拉框
     * @param pageParam
     * @return
     */
    List<SelectOptionDto> getCounterId(Map<String, Object> pageParam);

}

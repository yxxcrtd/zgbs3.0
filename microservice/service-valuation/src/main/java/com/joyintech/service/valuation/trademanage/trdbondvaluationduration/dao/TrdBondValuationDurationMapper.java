/*
 * 文件名：TrdBondInvestTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dao;


import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDuration001Dto;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDuration002Dto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 债券存续期管理Mapper
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月11日
 * @see TrdBondValuationDurationMapper
 * @since 1.0
 */
@Repository(value="serviceTrdBondValuationDurationMapper")
public interface TrdBondValuationDurationMapper {

    List<TrdBondValuationDuration001Dto> selectDeal(Map<String, Object> paraMap);

    int delBokDetailIdByTradeId(Map<String, Object> map);

    int querySequences();

    TrdBondValuationDuration002Dto queryTradeInfoByTradeId(String tradeId);

}

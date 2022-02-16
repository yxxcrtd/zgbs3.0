/*
 * 文件名：TrdBondInvestTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.irs.dao;


import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.bondlend.dto.TrdBondLendTrade002Dto;
import com.joyintech.service.valuation.trademanage.irs.dto.TrdIRSValuationTrade001Dto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 利率互换交易Mapper
 *
 * @author
 * @version 1.0 2022年01月13日
 * @see TrdIRSValuationTradeMapper
 * @since 1.0
 */
@Repository(value="serviceTrdIRSValuationTradeMapper")
public interface TrdIRSValuationTradeMapper {

    List<TrdIRSValuationTrade001Dto> selectDeal(Map<String, Object> paraMap);

    int delBokDetailIdByTradeId(Map<String, Object> map);

    int querySequences();

    TrdBondLendTrade002Dto queryTradeInfoByTradeId(String tradeId);

    List<SelectOptionDto> getTrdAcctId(Map<String, Object> map);


}

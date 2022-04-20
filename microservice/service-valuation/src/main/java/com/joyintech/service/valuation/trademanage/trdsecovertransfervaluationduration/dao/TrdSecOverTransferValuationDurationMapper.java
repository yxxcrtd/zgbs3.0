/*
 * 文件名：TrdBondInvestTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdsecovertransfervaluationduration.dao;


import com.joyintech.service.valuation.trademanage.trdsecovertransfervaluationduration.dto.TrdSecOverTransferValuationDuration001Dto;
import com.joyintech.service.valuation.trademanage.trdsecovertransfervaluationduration.dto.TrdSecOverTransferValuationDuration002Dto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 应收款转让管理Mapper
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月15日
 * @see TrdSecOverTransferValuationDurationMapper
 * @since 1.0
 */
@Repository(value="serviceTrdSecOverTransferValuationDurationMapper")
public interface TrdSecOverTransferValuationDurationMapper {

    List<TrdSecOverTransferValuationDuration001Dto> selectDeal(Map<String, Object> paraMap);

    int delBokDetailIdByTradeId(Map<String, Object> map);

    int querySequences();

    TrdSecOverTransferValuationDuration002Dto queryTradeInfoByTradeId(String tradeId);

}

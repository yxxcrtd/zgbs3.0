/*
 * 文件名：TrdBondInvestTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.trademanage.trdirsvaluationduration.dao;


import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdirsvaluationduration.dto.TrdIrsValuationDuration001Dto;
import com.joyintech.service.valuation.trademanage.trdirsvaluationduration.dto.TrdIrsValuationDuration002Dto;
import com.joyintech.service.valuation.trademanage.trdirsvaluationduration.dto.TrdIrsValuationDuration003Dto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 利率互换存续期管理Mapper
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月16日
 * @see TrdIrsValuationDurationMapper
 * @since 1.0
 */
@Repository(value="serviceTrdIrsValuationDurationMapper")
public interface TrdIrsValuationDurationMapper {

    List<TrdIrsValuationDuration001Dto> selectDeal(Map<String, Object> paraMap);

    List<SelectOptionDto> getTradeId(Map<String, Object> paraMap);

    TrdIrsValuationDuration003Dto findInfoByTradeId(String pTradeId);

    int delBokDetailIdByTradeId(Map<String, Object> map);

    int querySequences();

    TrdIrsValuationDuration002Dto queryTradeInfoByTradeId(String tradeId);

}

/*
 * 文件名：EtrCommandDispatcherMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：tanxuan
 * 修改时间：2020年05月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.investtrade.commanddispatcher.dao;

import java.util.List;
import java.util.Map;

import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EtrEntrustDetailDto;
import com.joyintech.tams.common.base.pojo.OriPledgeInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrCommandDispatcherDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrTradePledgeDto;


/**
 * 指令接口查询mapper
 * 〈功能详细描述〉
 * @author tanxuan
 * @version 1.0 2020年5月14日
 * @see EntrCommandDispatcherMapper
 * @since 1.0
 */
@Repository
public interface EntrCommandDispatcherMapper {

    /**
     * 指令接口查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<EntrCommandDispatcherDto> selectList(Map<String, Object> param);


    List<EntrTradePledgeDto> queryEtrPledgeListByEtrId(@Param("entrustId") String entrustId);

    List<EtrEntrustDetailDto> selectEtrDetailList(Map<String, Object> param);

    /**
     * 原质押品信息查询
     * @param param
     * @return
     */
    List<OriPledgeInfo> getOriFinDealPledgeList(@Param("tradeId") String tradeId);
}

/*
 * 文件名：EtrBondFirstBiddingMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etragrereptrade.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.entrust.etragrereptrade.dto.EtrAgreRepTrade002Dto;
import com.joyintech.service.entrust.etragrereptrade.dto.EtrAgreRepTrade003Dto;
import com.joyintech.service.entrust.etragrereptrade.dto.EtrAgreRepTradeDto;
import com.joyintech.service.entrust.etragrereptrade.dto.OnWayRep002Dto;
import org.springframework.stereotype.Component;

import com.joyintech.framework.select.SuggestDto;
import org.springframework.stereotype.Repository;

/**
 *  场内交易委托
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年11月14日
 * @see EtrAgreRepTradeMapper
 * @since 1.0
 */
@Repository
public interface EtrAgreRepTradeMapper {



    /**
     * 对手方交易员联想文本框初始化。
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    List<SuggestDto> selectCounterTrader(Map<String, String> map);

    /**
     * 本方交易员联想文本框初始化。
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    List<SuggestDto> selectTrader(Map<String, String> map);

    List<OnWayRep002Dto> findOnWayRepPage(Map<String, Object> pageParam);

    List<EtrAgreRepTrade002Dto> findPositionsPage(Map<String, Object> pageParam);

    /**
     * 获取指令信息
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    List<EtrAgreRepTradeDto> getEntrustInfoByEntrustId(Map<String, String> map);
    /**
     * 查询原成交信息
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    List<EtrAgreRepTradeDto> getEntrustInfoByTradeId(Map<String, String> map);
    /**
     * 场内交易委托列表查询
     * @param param
     * @return
     */
    List<EtrAgreRepTrade003Dto> selectEtrEntrust(Map<String,Object> param);

    List<SelectOptionDto> getptlAcctNo(Map<String, String> map);

    Date selectSecPositionMaxCdate(Map<String, Object> map);

    BigDecimal selectAvailablePositionAmount(Map<String, Object> param);

    /**
     * 查询首期的到期结算金额
     *
     * @param map 检索参数
     * @return 到期结算金额
     */
    BigDecimal getFirstTradeAmtByTradeId(Map<String, String> map);
}

/*
 * 文件名：ActualTimeAssetMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yanya
 * 修改时间：2019年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.portfolio.actualtimeasset.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.portfolio.actualtimeasset.dto.ActualTimeAssetDto;

/**
 * 实时资产头寸查询Mapper
 * 〈功能详细描述〉
 * @author yanya
 * @version 1.0 2019年10月15日
 * @see ActualTimeAssetMapper
 * @since 1.0
 */
@Repository
public interface ActualTimeAssetMapper {
    /**
     * 画面查询方法
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<ActualTimeAssetDto> findPage(Map<String,Object> map);

    /**
     * 证券账户下拉框
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getSecAcct(Map<String, Object> map);

    /**
     * 资产代码下拉框
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getAssetsInfo(Map<String, Object> map);

    /**
     * 获取实际持仓列表  获取实际持仓最大的
     * @param map
     * @return
     */
    ActualTimeAssetDto getTradePosition(Map<String,Object> map);

    /**
     * 获取指令当日持仓汇总
     * @param map
     * @return
     */
    BigDecimal getEtrAmount(Map<String,Object> map);

    /**
     * 获取交易当日持仓汇总
     * @param map
     * @return
     */
    BigDecimal getTradeAmount(Map<String,Object> map);

    /**
     * 信用保护凭证最新行情查询
     * @param params
     * @return
     */
    BigDecimal getNewPriceForCreditAssert(Map<String, String> params);

}

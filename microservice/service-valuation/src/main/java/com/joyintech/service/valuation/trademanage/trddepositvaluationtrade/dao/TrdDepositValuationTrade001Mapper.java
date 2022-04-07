/*
 * 文件名：TrdDepositInvestTrade001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;

import com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTrade001Dto;
import com.joyintech.tams.common.base.entity.InterfSendOpics;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 存款类投资交易
 * @author fanshuanghu
 * @version 1.0 2018年10月10日
 * @see TrdDepositValuationTrade001Mapper
 * @since 1.0
 */
@Repository
public interface TrdDepositValuationTrade001Mapper {



    //查询是否重复
    Integer  selectCountByTradeId(@Param("tradeId")String tradeId);


    /**
     * 获取自增序列值
     * @param sequenceName 序列名
     * @return
     */
    String getNextId(@Param("sequenceName")String sequenceName);



    /**
     * 产品代码下拉框
     * @date: 2021/12/29
     * @param:
     * @return:
     */
    List<SelectOptionDto> findPortfolioId(Map<String, Object> params);

    /**
     * 资产代码下拉框
     * @date: 2021/12/29
     * @param:
     * @return:
     */
    List<SelectOptionDto> findFinprodId(Map<String, Object> params);


    /**
     * 查询交易对手
     * <功能详细描述>
     */
    List<SelectOptionDto> getCounterId(Map<String, Object> param);


    /**
     * 检索交易列表
     *
     * @param paraMap 参数
     * @return 交易列表
     * @see
     * @since 1.0
     */
    List<TrdDepositValuationTrade001Dto> selectDeal(Map<String, Object> paraMap);




    TrdDepositValuationTrade001Dto queryTradeInfoByTradeId(Map<String, Object> paraMap);







}

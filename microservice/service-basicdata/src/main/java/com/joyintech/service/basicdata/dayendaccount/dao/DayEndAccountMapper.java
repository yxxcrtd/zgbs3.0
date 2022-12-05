/*
 * 文件名：MstCustomerNatureMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwei
 * 修改时间：2018年8月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.basicdata.dayendaccount.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.invest.common.reconciliation.dto.DayEndAccountDaichuliDto;
import com.joyintech.service.basicdata.dayendaccount.dto.DayEndAccountDto;
import com.joyintech.service.basicdata.mstcustomernature.dto.MstCustomerNatureDto;
import com.joyintech.tams.common.base.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 日终对账
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author chengzhong
 * @version 1.0 2021年4月20日
 * @see DayEndAccountMapper
 * @since 1.0
 */
@Repository
public interface DayEndAccountMapper {

    /**
     * 客服性质管理查询按钮的查询方法
     * 
     * @param param map
     * @return 用户信息
     */
    List<MstCustomerNatureDto> findAccountInfo(Map<String, Object> param);

    List<SelectOptionDto> queryAssetCode(Map<String, Object> param);

    List<String> queryFinProdIds(Map<String, Object> param);

    int queryStlSecReconDealCount(Map<String, Object> param);

    int querySettleTransDataCount(Map<String, Object> param);

    List<StlSecReconPara> queryAccountPara(Map<String, Object> param);

    List<DayEndAccountDaichuliDto> queryAccountDaichuli(Map<String, Object> param);

    /**
     * 交易费用配置查询
     * @param searchParam 方案查询信息
     * @return
     */
    List<TrdFeeConfig> getTrdFeeConfig(Map<String, Object> searchParam);


    /**
     * 根据费用配置流水号，查询交易费用配置明细
     * @param seqno
     * @return
     */
    List<TrdFeeConfigDetail> getTrdFeeConfigDetail(String seqno);

    List<SettleTransData> getSettleTransData(Map<String, Object> param);

    void deleteStlSecReconDetail(Map<String, Object> param);

    void insertStlSecReconBatchData(List<StlSecRecon> list);

    void insertStlSecReconDetailBatchData(List<StlSecReconDetail> list);

    List<SettleTransData> selectAllSettleTransData(Map<String, Object> param);

    List<SettleTransData> selectSettleTransData(Map<String, Object> param);

    List<DayEndAccountDto> listTotalAccountInfo(Map<String, Object> param);

    List<SelectOptionDto> selectCustomerId(Map<String, Object> param);

    List<SelectOptionDto> queryBusiAcctNo(Map<String, Object> param);

    List<DayEndAccountDto> selectPage(Map<String, Object> param);
    List<DayEndAccountDto> selectPage1(Map<String, Object> param);
    List<DayEndAccountDto> selectPage2(Map<String, Object> param);

    List<SelectOptionDto> selectSecAcctId(Map<String, Object> param);

    StlSecReconDeal getStlSecReconDeal(String seqno);

    /**
     * 查询佣金对账方式
     * @param customerId
     * @return
     */
    List<String> getStlSecReconPara3(Map<String, Object> param);

    List<DayEndAccountDto> selectPageNew(Map<String, Object> param);
    List<DayEndAccountDto> selectPageNew1(Map<String, Object> param);
    List<DayEndAccountDto> selectPageNew2(Map<String, Object> param);

}

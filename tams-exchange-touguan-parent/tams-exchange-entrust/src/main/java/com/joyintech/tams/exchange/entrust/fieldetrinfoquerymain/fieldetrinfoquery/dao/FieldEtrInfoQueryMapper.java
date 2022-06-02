/*
 * 文件名：FieldEtrInfoQueryMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：msk
 * 修改时间：2021年12月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dao;

import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.CfetsEtrBondFirstExeListInfoResponse;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.EntrustQueryBean;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.FieldEtrInfoQueryListResponse;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.FinProdResponse;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.TradeQueryBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 场内指令查询
 * @author msk
 * @version 1.0 2021年12月22日
 * @see FieldEtrInfoQueryMapper
 * @since 1.0
 */
@Repository
public interface FieldEtrInfoQueryMapper {

    /**
     * 指令列表分页查询
     * @param paraMap 参数
     * @return List<FieldEtrInfoQueryListResponse>
     */
    List<FieldEtrInfoQueryListResponse> findAllData(Map<String, Object> paraMap);

    //查询登录用户是否是领导、交易处长岗位
    int queryRolePosByLoginUser(Map<String, Object> map);
    FieldEtrInfoQueryListResponse getEtrBidInfo(@Param("entrustId") String entrustId);

    List<EntrustQueryBean> getEtrDispatcherExeListInfo(Map<String, Object> pageParam);

    List<FinProdResponse> findFinProdMarketIdList(Map<String, Object> pageParam);

    List<EntrustQueryBean> queryEntrust2Touguan(Map<String, Object> pageParam);

    List<TradeQueryBean> getEtrDispatcherDealListInfo(Map<String, Object> pageParam);

    List<TradeQueryBean> queryTrade2Touguan(Map<String, Object> pageParam);

    /**
     * 一级债券指令执行查询
     * @param pageParam
     * @return
     */
    List<CfetsEtrBondFirstExeListInfoResponse> getEtrBondFirstExeListInfo(Map<String, Object> pageParam);
}

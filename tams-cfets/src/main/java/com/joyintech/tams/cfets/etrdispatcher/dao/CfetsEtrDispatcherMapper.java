/*
 * 文件名：EtrBondFirstBiddingMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.etrdispatcher.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherAutoRequest;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherDealListInfoResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherExeListInfoResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherInfoExcResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherInfoOtcResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherInfoResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherInvResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherTradeListResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherTraderAuthListResponse;
import com.joyintech.tams.cfets.etrdispatcher.dto.CfetsEtrDispatcherTraderListRequest;
import com.joyintech.tams.common.base.entity.EtrDistributeKey;
import com.joyintech.tams.common.base.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 场外指令分发
 *
 * @author sjj
 * @version 1.0 2021年10月26日
 * @see CfetsEtrDispatcherMapper
 * @since 1.0
 */
@Repository
public interface CfetsEtrDispatcherMapper {

    /**
     * 指令列表分页查询
     *
     * @param paraMap 参数
     * @return List<CfetsEtrDispatcherTradeListResponse>
     */
    List<CfetsEtrDispatcherTradeListResponse> findAllEtrDispatcherCommon(Map<String, Object> paraMap);

    /**
     * @MethodName: 删除自动分发规则
     * @Return: int
     * @Author: sjj
     * @Date: 2021/10/29
     **/
    int deleteEtrAutoRule();

    /**
     * @MethodName: 删除自动分发规则明细
     * @Return: int
     * @Author: sjj
     * @Date: 2021/10/29
     **/
    int deleteEtrAutoRuleDetail();

    /**
     * 查询交易员指令分发权限配置列表
     *
     * @return page分页参数
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    List<CfetsEtrDispatcherTraderAuthListResponse> getTraderAuthList();

    /**
     * 删除交易员指令分发的权限
     *
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    int deleteTraderAuthList();

    /**
     * 指令信息明细查询
     *
     * @param pageParam
     * @return Page<CfetsEtrDispatcherInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    CfetsEtrDispatcherInfoResponse getEtrDispatcherInfo(Map<String, Object> pageParam);

    /**
     * 交易员列表模糊查询
     *
     * @param param
     * @return List<TrdCommandDispatcher001Dto>
     * @see
     * @since 1.0
     */
    List<CfetsEtrDispatcherTraderListRequest> getEtrTraderList(Map<String, Object> param);

    /**
     * 交易员与工作量列表查询
     *
     * @param param
     * @return List<CfetsEtrDispatcherTraderListRequest>
     * @see
     * @since 1.0
     */
    List<CfetsEtrDispatcherTraderListRequest> getEtrTraderAndWorkloadList(Map<String, Object> param);

    List<CfetsEtrDispatcherExeListInfoResponse> getEtrDispatcherExeListInfo(Map<String, Object> pageParam);

    List<CfetsEtrDispatcherDealListInfoResponse> getEtrDispatcherDealListInfo(Map<String, Object> pageParam);

    /**
     * 指令分发成交信息
     * @param pageParam
     * @return
     */
    List<CfetsEtrDispatcherDealListInfoResponse> getEtrDispatcherBidDealListInfo(Map<String, Object> pageParam);

    /**
     * 根据委托代码查询委托状态
     * add by sjj 20211101
     *
     * @param map
     * @return
     */
    List<String> doDispatcherCheck(Map<String, Object> map);

    /**
     * 获取联想文本框交易员的查询方法
     *
     * @param param
     * @return List<TrdCommandDispatcher001Dto>
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getUsers(Map<String, Object> param);

    /**
     * 删除原先分发的所有交易员记录
     * <功能详细描述>
     *
     * @param key
     * @return
     * @see
     * @since 1.0
     */
    int deleteEtrDistributeByPrimaryKey(EtrDistributeKey key);

    /**
     * 根据用户名查找真实姓名
     * <功能详细描述>
     *
     * @param userName
     * @return
     * @see
     * @since 1.0
     */
    User getRealNameByUserName(@Param("userName") String userName);

    /**
     * 根据委托代码查询委托投标标位
     *
     * @return
     */
    CfetsEtrDispatcherTradeListResponse getEtrBidInfo(@Param("entrustId") String entrustId);

    /**
     * 查询一级投标的进度及完成信息
     * @param entrustId
     * @return
     */
    CfetsEtrDispatcherTradeListResponse getEtrDealInfo(@Param("entrustId") String entrustId);


    CfetsEtrDispatcherAutoRequest findAutoDetail(@Param("sysDate") String sysDate);

    /**
     * 获取委托投资人员信息
     *
     * @param pageParam
     * @return
     */
    CfetsEtrDispatcherInvResponse getInvInfo(Map<String, Object> pageParam);

    /**
     * 获取指令明细基本信息 场外
     *
     * @param pageParam
     * @return
     */
    CfetsEtrDispatcherInfoOtcResponse getEtrDispatcherOtc(Map<String, Object> pageParam);

    /**
     * 查询业务类型
     */
    String getBusiTypePosition(@Param("entrustId") String entrustId);

    /**
     * 指令信息明细查询
     *
     * @param pageParam
     * @return Page<FieldEtrDispatcherInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    CfetsEtrDispatcherInfoExcResponse getEtrDispatcherExcInfo(Map<String, Object> pageParam);

    List<CfetsEtrDispatcherDealListInfoResponse> queryTrade2Touguan(Map<String, Object> pageParam);

    List<CfetsEtrDispatcherExeListInfoResponse> queryEntrust2Touguan(Map<String, Object> pageParam);

    /**
     * 查询已分发交易员数据
     * @param finprodId
     * @param entrustStatus
     * @return
     */
    List<String> getDispatcherTraderList(@Param("finprodId") String finprodId, @Param("entrustStatus") String entrustStatus);

    /**
     * 查询未选择指令
     * @param finprodId
     * @return
     */
    List<String> getDispatcherEntrustIdList(@Param("finprodId") String finprodId, @Param("statusList") List<String> statusList);

    /**
     * 查询指令执行、成交进度信息
     * @param entrustId
     * @return
     */
    CfetsEtrDispatcherTradeListResponse getEtrFirstBidInfo(String entrustId);
}

/*
 * 文件名：EtrBondFirstBiddingMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.etr.bond.dao;

import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dto.*;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.SppiImportData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  场内交易委托
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年11月14日
 * @see CfetsEtrIbBondinfoMapper
 * @since 1.0
 */
@Repository
public interface CfetsEtrIbBondinfoMapper {

    /**
     * 产品持仓信息分页查询
     * <功能详细描述>
     * @param pageParam 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
    List<CfetsEtrIbBondinfo001Dto> findPositionsPage(Map<String, Object> pageParam);

    /**
     * 根据资产代码获取相关交易信息列表
     * @param map
     * @return
     */
    List<CfetsEtrTrdProductDealDto> getTradeListByFinprodId(Map<String, Object> map);
    /**
     * 查询产品基本信息
     * <功能详细描述>
     * @param pageParam 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
    List<CfetsEtrIbBondPosinfo001Dto> selectPortfolioInfoist(Map<String, Object> pageParam);
    /**
     * 查询资产基本信息
     * <功能详细描述>
     * @param pageParam 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
    List<CfetsEtrIbBondPosinfo001Dto> selectFinprodInfoList(Map<String, Object> pageParam);
    CfetsEtrFinProductDto getEtrFinProductInfo(Map map);

    List<CfetsEtrIbBondPosinfo001Dto> queryPositionsPage(Map pageParam);

    Map queryPositionInfo(Map RequestDto);

    /**
     * 查询前一日持仓指标
     * @param baseMap
     * @return
     */
    List<BaseMap> selectLastCostAmt(BaseMap baseMap);

    /**
     * 当日交易数据查询
     * @param baseMap
     * @return
     */
    List<BaseMap> selectCurrTrdData(BaseMap baseMap);
    
    //    交易日取最大值
    Date getMaxTradeDate(String secId);
    //    交易日取最小值
    Date getMinTradeDate(String secId);

    List<String> getEtrFinExerciseInfo(Map<String, Object> map);

    /**
     * 根据指令编号查询数据
     * @param pageParam
     * @return
     */
    CfetsEtrIbBondinfoDto findEtrinfo(CfetsEtrInvestBondInfoReqDto dto);
    /**
     * 指令管理查询分页查询
     *
     * @param pageParam 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
    List<CfetsEtrIbfindEtrinfoResponse> findEtrinfoPage(Map<String, Object> pageParam);

    FinProduct  findAssetInfo(String finprodId);

    /**
     * 查询最近的行权计划
     *
     * @param pageParam platDate 平台日 finprodId 资产代码 ps 交易方向
     * @return com.joyintech.tams.common.base.entity.FinExercisePlan
     * @author jiangpeng
     * @date 2022/6/6 10:39
     */
    FinExercisePlan findAssetExercise(Map<String, Object> pageParam);

    Map queryPositionByEtrInfo(Map RequestDto);

    PtlPortfolio findPtlInfo(@Param("portfolioId") String portfolioId);
    String getIsHoliday(@Param("cdate") Date cdate,@Param("calendarId") String calendarId);

    List<CfetsBussCombDistribtBaseDto> queryCfetsBussCombDisList(Map pageParam);

     boolean queryAssetIsRight(Map pageParam);

    List<CfetsBussCombDistribtDto> selectAllBusModelListByInfo(Map<String, Object> pageParam);

    CfetsEtribBondInfo003Dto  selectTransmitTimeByInfo(String entrustId);

     boolean queryAssetIsShReg(Map pageParam);

    /**
     * 通过持仓查询业务模式
     * @param pageParam
     * @return
     */
    public String findInvAimInTrd(Map pageParam);
    /**
     * 通过指令查询业务模式
     * @param pageParam
     * @return
     */
    public String findInvAimInEtr(Map pageParam);

    /**
     * 判断是否做过sppi
     * @param pageParam
     * @return
     */
    SppiImportData queryAssetIsSppiTest(Map pageParam);

}

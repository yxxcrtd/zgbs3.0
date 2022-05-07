/*
 * 文件名：CstpTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨静敏
 * 修改时间：2018年11月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.bankuplink.common.dao;

import com.joyintech.service.bankuplink.common.dto.TrdConfirmMatchDto;
import com.joyintech.service.bankuplink.common.dto.TrdConfirmRegAndIbuInfoDto;
import com.joyintech.tams.common.base.entity.InterfOpicsTradRepo;
import com.joyintech.tams.common.base.entity.InterfTradRepoasst;
import com.joyintech.tams.invest.common.dto.cstp.InterfTradSecdealDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * cstp交易接口查询
 * 
 * @author 杨静敏
 * @version 1.0 2018年11月23日
 * @see CstpTradeMapper
 * @since 1.0
 */
@Repository
public interface CstpTradeMapper {

    /**
     * 根据交易编号查询债券交易数据是否已经存在
     * 
     * @param execid 成交编号
     * @return 交易号
     * @see
     * @since 1.0
     */
    String selectSecByExecid(@Param("execid") String execid);

    /**
     * 查询债券交易信息
     * 
     * @param execid 成交编号
     * @param sysDate 平台日
     * @param businesstype 业务场景
     * @return 债券交易信息
     * @see
     * @since 1.0
     */
    InterfTradSecdealDto selectTradeSec(@Param("execid") String execid,
                                        @Param("sysDate") String sysDate,
                                        @Param("businesstype") String businesstype);

    /**
     * 根据交易员中文名称，查询本方交易员在投管系统的代码
     * 
     * @param ourtraderchinesename 交易员中文名称
     * @return 本方交易员
     * @see
     * @since 1.0
     */
    String selectBuytrader(@Param("ourtraderchinesename") String ourtraderchinesename);

    /**
     * 机构中文全称，查询序列号
     * 
     * @param institutionchinesefullname 中文全称
     * @return 机构代码
     * @see
     * @since 1.0
     */
    String selectSeller(@Param("institutionchinesefullname") String institutionchinesefullname);

    /**
     * 根据资金账号去的组合代码
     * 
     * @param map 资金账号，账户类型是dvp
     * @return 组合代码
     * @see
     * @since 1.0
     */
    String selectPortfolioId(Map<String, Object> map);

    /**
     * 获取交易编号
     * 
     * @param sysDate 平台日
     * @return 交易编号
     * @see
     * @since 1.0
     */
    String selectDealNo(@Param("sysDate") String sysDate, @Param("prefix") String prefix);

    /**
     * 查询回购交易信息
     * 
     * @param execid 成交编号
     * @param sysDate 平台日
     * @param businesstype 业务场景
     * @return 回购交易信息
     * @see
     * @since 1.0
     */
    InterfOpicsTradRepo selectTradRepo(@Param("execid") String execid,
                                       @Param("sysDate") String sysDate,
                                       @Param("businesstype") String businesstype);

    /**
     * 根据交易编号查询回购交易数据是否已经存在
     * 
     * @param execid 成交编号
     * @return 交易号
     * @see
     * @since 1.0
     */
    String selectRepoByExecid(@Param("execid") String execid);

    /**
     * 查询成交编号
     * 
     * @param secid 债券代码
     * @param execid 成交编号
     * @return 成交编号
     * @see
     * @since 1.0
     */
    String selectrepoAsset(@Param("secid") String secid, @Param("execid") String execid);

    /**
     * 更新抵押品信息
     * 
     * @param interfTradRepoasst 抵押品信息
     * @return 条数
     * @see
     * @since 1.0
     */
    int updateRepoAsst(InterfTradRepoasst interfTradRepoasst);

    String selectPortfolioIdByQuoteId(@Param("quoteid") String quoteid,@Param("businessType") String businessType);

    String selectSeqNo();

    String selectCounterId(@Param("cpPartyId") String cpPartyId);

    String getIsDzh(@Param("quoteId") String quoteId);

    int getPtlByOurinstitutioncode(@Param("ourinstitutioncode") String ourinstitutioncode);
    /**
     * 根据成交编号，查询下行成交匹配关系
     */
    TrdConfirmMatchDto selectMatchDtoByExecId(@Param("execid") String execid);

    /**
     * 根据下行交易编号，查询下行成交匹配关系
     *
     * @param cstpTradeId 下行交易编号
     * @return com.joyintech.service.bankuplink.common.dto.TrdConfirmMatchDto
     * @author jiangpeng
     * @date 2022/5/7 9:19
     */
    TrdConfirmMatchDto selectMatchDtoByCstpTradeId(@Param("cstpTradeId") String cstpTradeId);

    /**
     * 根据登记编号，查询登记信息
     */
    List<TrdConfirmRegAndIbuInfoDto> selectRegDtoByPrimaryKey(Map<String, Object> map);
    /**
     * 根据报价编号，查询报价信息
     */
    TrdConfirmRegAndIbuInfoDto selectIbuDtoByPrimaryKey(@Param("quoteId") String quoteId);
    /**
     * 新建下行成交匹配关系
     * @param record
     * @return
     */
    int insertTrdQutoMatchInfo(TrdConfirmMatchDto record);
    /**
     * 更新下行成交匹配关系
     * @param record
     * @return
     */
    int updateTrdQutoMatchInfo(TrdConfirmMatchDto record);

}

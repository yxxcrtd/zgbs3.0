package com.joyintech.tams.cfets.trdconfirm.dao;


import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondinfoDto;
import com.joyintech.tams.cfets.trdconfirm.dto.*;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 银行间交易成交确认
 * @author
 * @version 1.0 2021年10月26日
 * @see CfetsTrdConfirmMapper
 * @since 1.0
 */
@Repository
public interface CfetsTrdConfirmMapper {
    /**
     * 查询所有登记信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmRegInfoListResponse> findAllRegInfoList(Map<String, Object> pageParam);

    /**
     * 根据主键查询登记信息
     * @param pageParam
     * @return
     */
    CfetsTrdConfirmRegInfoListResponse selectRegInfoByKey(String totleTradeId);

    /**
     * 根据主键查询一级申购登记信息
     * @param pageParam
     * @return
     */
    CfetsTrdConfirmRegInfoListResponse selecBidInfoByKey(String totleTradeId);
    /**
     * 根据主键查询债券报价信息
     * @param pageParam
     * @return
     */
    CfetsTrdConfirmRegInfoListResponse selectIbuInfoByKey(Map<String, Object> pageParam);
    /**
     * 根据主键查询回购报价信息
     * @param pageParam
     * @return
     */
    CfetsTrdConfirmRegInfoListResponse selectRepoIbuInfoByKey(Map<String, Object> pageParam);
    /**
     * 查询所有登记费用信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmRegFeeInfoDto> findAllRegFeeInfoList(String tradeId);

    /**
     * 查询所有登记明细数据
     * @param pageParam
     * @return
     */

    List<CfetsRegFinProductDealDto> findAllRegDealList(String totleTradeId);

    /**
     * 查询所以报价明细数据
     * @param totleTradeId
     * @return
     */
    List<CfetsRegFinProductDealDto> findAllIbuDealList(String totleTradeId);

    /**
     * 查询所有登记明细费用信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmRegFeeInfoDto> findAllRegFeeDetailInfoList(String tradeId);

    /**
     * 检索账户来源
     * @param param
     * @return
     */
    String selectSecAcctSource(Map<String, Object> param);
    /**
     * 新建下行成交匹配关系
     * @param record
     * @return
     */
    int insertTrdQutoMatchInfo(CfetsTrdConfirmMatchDto record);
    /**
     * 更新下行成交匹配关系
     * @param record
     * @return
     */
    int updateTrdQutoMatchInfo(CfetsTrdConfirmMatchDto record);
    /**
     * 查询所有Cstp下行信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmCstpInfoListResponse> findAllCstpInfoList(Map<String, Object> pageParam);
    /**
     * 查询所有匹配确认数据
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmTrdCstpMatchResponse> findAllTrdCstpInfoList(Map<String, Object> pageParam);
    /**
     * 查询所有匹配确认-执行债券数据
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmExecBondInfoResponse> findAllTrdBondList(Map<String, Object> pageParam);
    /**
     * 查询所有匹配确认-执行一级申购数据
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmExecFirstBondInfoResponse> findAllTrdBidList(Map<String, Object> pageParam);
    /**
     * 查询所有匹配确认-执行质押式数据
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmExecRepoInfoResponse> findAllTrdRepoList(Map<String, Object> pageParam);
    /**
     * 匹配确认列表-cstp成交债券数据分页查询
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmCstpBondInfoResponse> findAllCstpBondList(Map<String, Object> pageParam);
    /**
     * 匹配确认列表-cstp成交一级申购数据分页查询
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmExecFirstBondInfoResponse> findAllCstpBidList(Map<String, Object> pageParam);
    /**
     * 匹配确认列表-cstp成交质押式分页查询
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmCstpRepoInfoResponse> findAllCstpRepoList(Map<String, Object> pageParam);

    /**
     * 根据主键删除匹配关系表
     * @param seqno
     * @return
     */
    int deleteByPrimaryKey(String seqno);
    /**
     * 根据指令编号查询数据
     * @param pageParam
     * @return
     */
    CfetsEtrIbBondinfoDto findEtrinfoByPrimaryKey(String entrustId);
    /**
     * 查询所有登记或报价信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmRegInfoListResponse> findAllRegAndIbuInfoList(Map<String, Object> pageParam);

    /**
     * 查询交易对手
     * @param cpPartyId
     * @return
     */
    String selectCounterId(@Param("cpPartyId") String cpPartyId);

    /**
     * 判断是否存在下行匹配关系
     * @param pageParam
     * @return
     */
    public boolean isExistMatchInfo(Map<String, Object> pageParam);
    /**
     * 根据交易汇总编号删除匹配关系表
     * @param seqno
     * @return
     */
    int deleteByTrdId(String trdId);
    /**
     * 判断是否已经撤销
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmRegInfoListResponse> isExistDealInfo(Map<String, Object> pageParam);
    /**
     * 查询所有质押明细数据
     * @param pageParam
     * @return
     */

    List<PledgeBondInfoDto> findAllSecPtlsList(String tradeId);
    /**
     * 指令投资目的
     */
    String selectInvAimByEntrustId(Map<String, Object> param);
    /**
     * 查询所有报价的质押明细数据
     */
    List<PledgeBondInfoDto> findAllIbuSecPtlsList(Map<String, Object> param);
    /**
     * 根据成交编号，查询下行成交匹配关系
     */
    CfetsTrdConfirmMatchDto selectMatchDtoByExecId(@Param("execid") String execid);


    /**
     * 根据下行交易编号，查询下行成交匹配关系
     *
     * @param cstpTradeId 下行交易编号
     * @return com.joyintech.service.bankuplink.common.dto.TrdConfirmMatchDto
     * @author jiangpeng
     * @date 2022/5/7 9:19
     */
    CfetsTrdConfirmMatchDto selectMatchDtoByCstpTradeId(@Param("cstpTradeId") String cstpTradeId);
    /**
     * 根据登记编号，查询登记信息
     */
    List<CfetsTrdConfirmRegInfoListResponse> selectRegDtoByPrimaryKey(Map<String, Object> map);

    /**
     * 查询所有登记或报价回购的质押券信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmRegInfoListResponse> findAllRegAndIbuPledgesList(@Param("busiCode") String busiCode);
    /**
     * 查询所有下行的质押券信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdConfirmCstpInfoListResponse> findAllCstpPledgesList(@Param("execid") String execid);

    /**
     * 根据交易编号,查询一级申购交易数据
     */
    List<CfetsTrdConfirmRegInfoListResponse> selectBidDtoByPrimaryKey(Map<String, Object> map);
    /**
     * 根据资产代码查询指令/申购流程中数据
     * @author jiangpeng
     * @date 2022/5/7 19:10
     * @param finprodId
     * @return java.util.List<java.lang.String>
     */
    List<String> selectWflBidDataByFinprodId(@Param("finprodId") String finprodId);
    /**
     * 根据资产代码查询资产发行状态
     * @author jiangpeng
     * @date 2022/5/7 19:10
     * @param finprodId 资产代码
     * @return java.util.List<java.lang.String>
     */
    FinProductAdd selectIssueInfoByFinprodId(@Param("finprodId") String finprodId);

    /**
     * 根据资产代码查询指令信息
     * @author jiangpeng
     * @date 2022/5/7 19:52
     * @param finprodIds 资产代码列表
     * @return int
     */
    List<EtrEntrust> selectEtrEntrustEByFinprodId(@Param("finprodIds") List<String> finprodIds);
}

package com.joyintech.tams.cfets.trdquery.dao;




import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleInfoOtherListRespond;
import com.joyintech.tams.cfets.manualsettle.dto.PortIntFenPeiInfoDto;
import com.joyintech.tams.cfets.trdquery.dto.*;
import com.joyintech.tams.common.base.entity.RegFinProductDealTotal;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 银行间交易成交确认
 * @author
 * @version 1.0 2021年10月26日
 * @see CfetsTrdQueryMapper
 * @since 1.0
 */
@Repository
public interface CfetsTrdQueryMapper {
    /**
     * 查询用户所有权限
     * @param userName
     * @return
     */
    List<String>  selectSysRoleById(String userName);
    /**
     * 查询所有登记信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdQueryRegInfoListResponse> queryAllRegInfoList(Map<String, Object> pageParam);
    /**
     * 根据主键查询登记信息
     * @param pageParam
     * @return
     */
    CfetsTrdQueryRegInfoListResponse selectRegFormDataByKey(Map<String, Object> pageParam);

    /**
     * 组合分配信息List
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<CfetsPortfolioIdFenPeiInfoResponse> selectRegTableFenPeiInfoList(Map<String, Object> pageParam);
    /**
     * 质押券信息List
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<CfetsPledgeInfoResponse> selectRegTablePledgeInfoList(Map<String, Object> pageParam);
    /**
     * 查询所有报价信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdQueryIbuInfoListResponse> queryAllIbuInfoList(Map<String, Object> pageParam);
    /**
     * 根据主键查询债券报价信息
     * @param pageParam
     * @return
     */
    CfetsTrdQueryIbuInfoListResponse selectIbuBondFormDataByKey(Map<String, Object> pageParam);
    /**
     * 根据主键查询回购报价信息
     * @param pageParam
     * @return
     */
    CfetsTrdQueryIbuInfoListResponse selectIbuRepoFormDataByKey(Map<String, Object> pageParam);
    /**
     * 债券报价组合分配信息List
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<CfetsPortfolioIdFenPeiInfoResponse> selectIbuBondTableFenPeiInfoList(Map<String, Object> pageParam);
    /**
     * 回购报价组合分配信息List
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<CfetsPortfolioIdFenPeiInfoResponse> selectIbuRepoTableFenPeiInfoList(Map<String, Object> pageParam);
    /**
     * 报价质押券信息List
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<CfetsPledgeInfoResponse> selectIbuTablePledgeInfoList(Map<String, Object> pageParam);
    /**
     * 查询所有成交信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdQueryCstpInfoListResponse> queryAllCstpInfoList(Map<String, Object> pageParam);
    /**
     * 根据主键查询债券成交信息
     * @param pageParam
     * @return
     */
    CfetsTrdQueryCstpInfoListResponse selectCstpBondFormDataByKey(Map<String, Object> pageParam);
    /**
     * 根据主键查询回购成交信息
     * @param pageParam
     * @return
     */
    CfetsTrdQueryCstpInfoListResponse selectCstpRepoFormDataByKey(Map<String, Object> pageParam);
    /**
     * 成交数据中质押券信息List
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<CfetsPledgeInfoResponse> selectCstpTablePledgeInfoList(Map<String, Object> pageParam);
    /**
     * 交易列表分页查询
     * @param pageParam 参数
     * @return List<CfetsManualSettleTraderListRespond>
     */
    List<CfetsTrdQueryTradeInfoListResponse> queryAllTradeInfoList(Map<String, Object> pageParam);
    /**
     * 交易明细列表分页查询
     * @param pageParam 参数
     * @return List<CfetsManualSettleTraderListRespond>
     */
    List<CfetsTrdQueryTradeInfoListResponse> queryAllTradeDetailInfoList(Map<String, Object> pageParam);
    /**
     * 交易明细表单内容
     *
     * @param pageParam 参数
     * @return CfetsManualSettleInfoF32Respond
     */
    CfetsManualSettleInfoOtherListRespond findManualSettleInfoOther(Map<String, Object> pageParam);
    /**
     * 组合分配信息List,针对交易明细
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<PortIntFenPeiInfoDto> findPortIntFenPeiInfoList(Map<String, Object> pageParam);

    /**
     * 查询债券交易基本信息
     * @param params
     * @return
     */
    CfetsTrdBondInvestTradeDto getTrdBondInvestTradeDto(Map<String, Object> params);
    /**
     * 根据主键查询登记信息
     * @param pageParam
     * @return
     */
    RegFinProductDealTotal selectRegTotalInfoById(String bizCode);
    /**
     * 根据主键更新登记数据
     * @param pageParam
     * @return
     */
    int updateRegTotalByPrimaryKeySelective(RegFinProductDealTotal record);

    /**
     * 根据更新报价数据
     * @param paramMap
     * @return
     */
    void updateIbuByPrimaryKeySelective(Map<String, Object> paramMap);

    /**
     * 根据更新报价数据
     * @param paramMap
     * @return
     */
    void updateIbuRepoByPrimaryKeySelective(Map<String, Object> paramMap);

    /**
     * 获取交易id
     *
     * @date 2022/1/19
     * @param tradeId
     * @return java.lang.String
     */
    String getTradeId(@Param("tradeId") String tradeId);

    String getTotalTradeId(@Param("tradeId") String tradeId);

    Integer getCountByReg(@Param("tradeId") String tradeId);
    /**
     * 查询汇总编号下的所有交易编号
     * @param userName
     * @return
     */
    List<String>  selectAllTrdDetailListById(String totleTradeId);

    /**
     * 查询回购交易基本信息
     * @param params
     * @return
     */
    CfetsTrdRepoTrade002Dto getTrdRepoInvestTradeDto(Map<String, Object> params);
    /**
     * 查询交易质押券
     *
     * @param pageParam 参数
     * @return List<CfetsPledgeInfoResponse>
     */
    List<CfetsPledgeInfoResponse> findPledgeInfoList(Map<String, Object> pageParam);


    /**
     * 根据
     * @param bizCode
     * @return
     */
    String seleteByTrdId(@Param("bizCode") String bizCode);

    /**
     * 更新债券交易信息接口表备注
     *
     * @param paramMap 参数
     */
    void updateInterfTradSecdealByTradeId(Map<String, Object> paramMap);
    /**
     * 交易明细回购表单内容
     *
     * @param pageParam 参数
     * @return CfetsManualSettleInfoF32Respond
     */
    CfetsTrdSettleInfoResponse findRepoManualSettleInfoOther(Map<String, Object> pageParam);

    /**
     * 组合分配信息List,针对交易明细回购
     *
     * @param pageParam 参数
     * @return List<PortIntFenPeiInfoDto>
     */
    List<PortIntFenPeiInfoDto> findRepoPortIntFenPeiInfoList(Map<String, Object> pageParam);
    /**
     * 查询交易质押券,针对交易明细回购
     *
     * @param pageParam 参数
     * @return List<CfetsPledgeInfoResponse>
     */
    List<CfetsPledgeInfoResponse> findRepoPledgeInfoList(Map<String, Object> pageParam);

    /**
     * 查询交易质押券,针对交易明细回购
     *
     * @param pageParam 参数
     * @return List<CfetsPledgeInfoResponse>
     */
    List<CfetsPledgeInfoResponse> findRepoPledgeInfo(Map<String, Object> pageParam);

    List<String> findEtrEntrustIsForcedFinish(Map<String, Object> pageParam);
}

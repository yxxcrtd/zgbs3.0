package com.joyintech.tams.invest.common.trade.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.trade.dto.CreditComInfoOneStepDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;

@Repository
public interface CreditLinesUpdateMapper {

    int updateLinesByPrimaryKeySelective(CreditLines creditLines);

    BigDecimal getFaceValue(Object inputDto);

    int isNbDeal(@Param("counterId") String counterId, @Param("counterType") String counterType);

    /**
     * 机构授信额度
     * @param oneStepInfo
     * @return
     */
    List<CreditLines> getOrgCeLineList(CreditComInfoOneStepDto oneStepInfo);

    /**
     * 机构分项授信额度
     * @param oneStepInfo
     * @return
     */
    List<CreditLines> getOrgCeLineItemList(CreditComInfoOneStepDto oneStepInfo);


    List<CreditLines> getAssetCeLineList(Map<String, Object> map);


    List<CreditLines> getAssetSeriesCeLineList(Map<String, Object> map);


    List<CreditComInfoOneStepDto> findCreditTagInfo(@Param("finprodId") String finprodId,@Param("isSus") String isSus,@Param("secBond") String secBond, @Param("level") String level);

    List<CreditComInfoOneStepDto> findCreditTagInfoOther(Map<String, Object> map);

    FinProductType selectByPrimaryKey(FinProductTypeKey key);
    /**
     * 判断资产是否重复
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<FinProductTypeConfig> getAssetFiveType(FinProductTypeConfig param);

    List<CreditUsingSeries> findCreditUsingSeries(CreditLinesInputDto inputDto);

    List<CreditUsingSeries> findCreditUsingSeriesByFin(CreditLinesInputDto inputDto);

    List<CreditUsingAst> getCreditUsingAstList(CreditLinesInputDto inputDto);

    List<CreditUsingAst> getCreditUsingAstListByFin(CreditLinesInputDto inputDto);

    List<CreditUsingCustomer> getCreditUsingCustomerList(CreditLinesInputDto inputDto);

    List<CreditUsingCustomerMst> getCreditUsingCustomerMsts(CreditLinesInputDto inputDto);


    /**
     * 查询资产的第一笔买入交易时间
     * @param
     * @return
     */
    Date findFirstTradeDate(@Param("finprodId")String finprodId, @Param("tradeDate")Date tradeDate, @Param("vdate")Date vdate, @Param("mdate")Date mdate);

    List<CreditUsingSeries> findCreditUsingSeriesByPre(@Param("entrustId")String entrustId);

    List<CreditUsingAst> findCreditUsingAstByPre(@Param("entrustId")String entrustId);

    List<CreditUsingCustomer> findCreditUsingCustomerByPre(@Param("entrustId")String entrustId);

    List<CreditUsingCustomerMst> findCreditUsingCustomerMstByPre(@Param("entrustId")String entrustId);

    String getCreditType(String creditMstId);

    BigDecimal findUsedAmtFromCustMst(Map<String,Object> maps);

    BigDecimal findUsedAmtFromCust(Map<String,Object> maps);

    // add by zhangrui1 批量取债券可用面值|基金净值等 start
    List<Map<String, Object>> selectNetPrice(@Param("prodList") List<String> finprodId,
                                             @Param("sysDate") Date sysDate);

    List<Map<String, Object>> findPledgeBondInfo(Map<String, Object> param);

    BigDecimal getShareAmt(String bTradeId);

    BigDecimal getOccupiedQuota(CreditLinesInputDto inputDto);

    BigDecimal getOccupiedQuota1(CreditLinesInputDto inputDto);

    // TISHKS-947:子期次占用母计划额度 modify by fuyongsheng 20220506 start
    String getJHLFinprodId(@Param("finprodId")String finprodId);
    // TISHKS-947:子期次占用母计划额度 modify by fuyongsheng 20220506 end
}

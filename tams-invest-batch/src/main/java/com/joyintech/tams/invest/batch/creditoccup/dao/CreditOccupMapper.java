package com.joyintech.tams.invest.batch.creditoccup.dao;

import com.joyintech.tams.invest.batch.creditoccup.dto.CreditUsingDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zznn
 * @date 2021/8/10
 */
@Repository
public interface CreditOccupMapper {

    /**
     * 系列额度占用
     * @param param
     * @return
     */
    List<CreditUsingDto> getCreditUsingSeries(Map<String,Object> param);

    /**
     * 资产额度占用
     * @param param
     * @return
     */
    List<CreditUsingDto> getCreditUsingAst(Map<String,Object> param);

    /**
     * 机构额度占用
     * @param param
     * @return
     */
    List<CreditUsingDto> getCreditUsingCustomer(Map<String,Object> param);

    /**
     * 机构分项额度占用
     * @param param
     * @return
     */
    List<CreditUsingDto> getCreditUsingCustomerMst(Map<String,Object> param);

    void delCreditUsingSeries(Map<String,Object> param);
    void delCreditUsingAst(Map<String,Object> param);
    void delCreditUsingCustomer(Map<String,Object> param);
    void delCreditUsingCustomerMst(Map<String,Object> param);

    void updateCreditUsingSeries(CreditUsingDto dto);
    void updateCreditUsingAst(CreditUsingDto dto);
    void updateCreditUsingCustomer(CreditUsingDto dto);
    void updateCreditUsingCustomerMst(CreditUsingDto dto);

    /**
     * 查询场内日终生成的交易
     * @param param
     * @return
     */
    List<CreditLinesInputDto> listTrdProductDeal(Map<String,Object> param);

    /**
     * 查询委外组合
     * @param param
     * @return
     */
    List<CreditLinesInputDto> listWwSec(Map<String,Object> param);

    /**
     * 查询一级债日间交易
     *
     * @param param
     * @return
     */
    List<CreditLinesInputDto> listTotalTempDeal(Map<String,Object> param);
}


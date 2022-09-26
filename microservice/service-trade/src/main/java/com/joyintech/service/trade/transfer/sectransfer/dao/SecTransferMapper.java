package com.joyintech.service.trade.transfer.sectransfer.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.transfer.common.dto.BondValuationDto;
import com.joyintech.service.trade.transfer.sectransfer.dto.SecTransfer001Dto;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
*
* 债券交易拆分
* @author zcw
* @version 1.0 2019年11月21日
* @see SecTransferMapper
* @since 1.0
*/
@Repository
public interface SecTransferMapper {

    /**
     * 获取资产代码联想框集合
     * @param map 资产代码入参
     * @return 联想文本框实体类
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getFinprodMarketId(Map<String,Object> map);

    /**
     * 由资产代码带出各种表单元素
     * @param map 资产代码
     * @return 资产类型、计息基础等
     * @see
     * @since 1.0
     */
    SecTransfer001Dto selectTradeInfo(Map<String,Object> map);

    /**
     * 查询已拆分金额
     *
     * @return 拆分前交易编号
     * @see
     * @since 1.0
     */
    BigDecimal getDistAmt(@Param("tradeId") String tradeId);

    /**
     * 获取拆分信息—组合代码
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getPortfolioId(Map<String,Object> map);

    /**
     * 查询组合托管户
     * @return 组合托管户
     * @see
     * @since 1.0
     */
    String getAccountInfo(Map<String,Object> map);

    /**
     * 查询组合券商资金账户
     * @return 组合券商资金账户
     * @see
     * @since 1.0
     */
    String getCounterAcctInfo(Map<String,Object> map);

    /**
     * @return 组合名称
     * @see
     * @since 1.0
     */
    String selectPortfolioName(String portfolioId);

    /**
     * 查询托管划款指令
     * <功能详细描述>
     * @param tradeId 查询条件
     * @see
     * @since 1.0
     */
    StlPayOrders selectStlPayOrder(@Param("tradeId") String tradeId);

    BigDecimal getLastNetPrice(Map<String, Object> param);

    int selectData(Map<String, Object> map);

    int selectYwmsData(Map<String, Object> map);

    int selectCpriceData(Map<String, Object> map);


    /**
     * 查询上市标的发行标的信息
     * @param param 资产代码
     * @see
     * @since 2.1.7
     */
    FinProduct selectApperInfo(Map<String, Object> param);

    List<BondValuationDto> selectBondValuationInfo(Map<String, Object> param);
}

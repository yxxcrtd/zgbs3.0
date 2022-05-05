package com.joyintech.tams.cfets.position.dao;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface PositionInfoMapper {

    /**
     * 查询指令现金
     * @param RequestDto
     * @return
     */
    Map queryEtrAvailableCash(Map RequestDto);
    /**
     * 查询交易现金
     * @param RequestDto
     * @return
     */
    Map queryTrdAvailableCash(Map RequestDto);

    /**
     * 查询指令持仓
     * @param RequestDto
     * @return
     */
    Map queryEtrPosition(Map RequestDto);

    /**
     * 查询交易持仓
     * @param RequestDto
     * @return
     */
    Map queryTrdPosition(Map RequestDto);

    /**
     * 查询证券账户
     * @param map
     * @return
     */
    String qryPortfolioSecAcctId(Map<String, String> map);
}

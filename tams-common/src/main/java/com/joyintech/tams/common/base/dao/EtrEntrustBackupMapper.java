package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.EtrEntrust;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EtrEntrustBackupMapper {

    /**
     * 撤回或退回再启动流程时转移至废弃备份表
     * @param entrustId
     * @return
     */
    int insertFromQuery(String entrustId);

    int insertAddFromQuery(String entrustId);
    /**
     * 作废时转移至指令表
     * @param entrustId
     * @return
     */
    int insertEtrFromQuery(String entrustId);

    int insertEtrAddFromQuery(String entrustId);
    /**
     * 删除废弃表
     */
    int deleteByPrimaryKey(String entrustId);

    int deleteAddByPrimaryKey(String entrustId);

    int deleteDetailByPrimaryKey(String entrustId);

    EtrEntrust selectByPrimaryKey(String entrustId);

    /**
     * 删除委托投标标位备份表
     * @param entrustId
     * @return
     */
    int deleteEntrustBidPriceByEntrustId(@Param("entrustId") String entrustId);

    /**
     * 删除委托明细备份表
     * @param entrustId
     * @return
     */
    int deleteEntrustDetailByEntrustId(@Param("entrustId") String entrustId);

    /**
     * 转移委托投标标位备份表数据到委托投标标位表
     * @param entrustId
     * @return
     */
    int insertBidPriceFromQuery(@Param("entrustId") String entrustId);

    /**
     * 转移委托明细备份表数据到委托明细表
     * @param entrustId
     * @return
     */
    int insertDetailFromQuery(@Param("entrustId") String entrustId);
}
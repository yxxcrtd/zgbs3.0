/*
 * 文件名：EtrInvestBondInfoMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.etr.bond.dao;

import com.joyintech.framework.select.SelectOptionDto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 债券投资委托mapper
 */
@Repository
public interface CfetsEtrIbBondinfo002Mapper {
    /**
     * 
     * 获取新建界面的资产代码联想下拉框框
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getfinprodIdForAdd(Map<String, Object> param);
    /**
     * 
     * 通过主键查询修改信息
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */ 
  //  EtrIbBondinfo002Dto selectByKey(Map<String, Object> map);
    /**
     * 可用面值计算
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
   // BigDecimal getAvailableNum(Map<String, Object> map);

    /**
     * 查询估值净价
     * @author wangguiqi
     */
//	BigDecimal getClosePriceByPara(Map<String, Object> param);

    /**
     * 查询行权价
     * @author wangguiqi
     */
    BigDecimal getExercisePricebyPara(Map<String, Object> param);


    int deleteWflEtrEntrustByEntrustId(String entrustId);

    int deleteWflEtrEntrustAddByEntrustId(String entrustId);

    String qryPortfolioSecAcctId(Map<String, Object> param);
    /**
     *
     * 获取新建界面的组合联想下拉框框
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> selectPortfolioIdList(Map<String, Object> param);

    String qryPortfolioCashAcctId(Map<String, Object> param);
}

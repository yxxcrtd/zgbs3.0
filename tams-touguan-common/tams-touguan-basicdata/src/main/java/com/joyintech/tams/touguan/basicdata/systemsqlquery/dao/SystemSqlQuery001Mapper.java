/*
 * 文件名：InvestTradeQuery001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年7月25日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.touguan.basicdata.systemsqlquery.dao;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 投资交易查询 - 数据查询
 * 
 * @author yangjian
 * @version 1.0 2018年7月25日
 * @see SystemSqlQuery001Mapper
 * @since 1.0
 */
@Repository
public interface SystemSqlQuery001Mapper {

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param replace
     * @return
     * @see
     * @since 1.0
     */
    List<LinkedHashMap<String, Object>> selectAllData(@Param("sqlText") String sqlText);

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param replace
     * @return
     * @see
     * @since 1.0
     */
    int insertAllData(@Param("sqlText") String sqlText);
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param replace
     * @return
     * @see
     * @since 1.0
     */
    int deleteAllData(@Param("sqlText") String sqlText);

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param replace
     * @return
     * @see
     * @since 1.0
     */
    int updateAllData(@Param("sqlText") String sqlText);
}

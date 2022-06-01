/*
 * 文件名：RskPreOccupyInfoProMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhengjunping
 * 修改时间：2019年9月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.ocphistory.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 预占/实占信息历史化
 * 
 * @author zhangrui1
 * @version 1.0 2020年11月5日
 * @see RskOccupyHistoryMapper
 * @since 1.0
 */
@Repository
public interface RskOccupyHistoryMapper {

    /**
     * 删除过期的预占信息
     * 
     * @param sysDate 平台日期
     * @return 删除结果
     */
    int deletePreOccupy(@Param("sysDate") Date sysDate);

    /**
     * 历史化预占信息
     * 
     * @param sysDate 平台日期
     * @return 执行结果
     */
    int historyPreOccupy(@Param("sysDate") Date sysDate);

    /**
     * 删除过期的实占信息
     * 
     * @param sysDate 平台日期
     * @return 删除结果
     */
    int deleteOccupy(@Param("sysDate") Date sysDate);

    /**
     * 历史化实占信息
     * 
     * @param sysDate 平台日期
     * @return 执行结果
     */
    int historyOccupy(@Param("sysDate") Date sysDate);
}

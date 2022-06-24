/*
 * 文件名：EntrustStatusMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.cfetsentrust.dao;

import com.joyintech.tams.invest.batch.cfetsentrust.dto.CfetsEntrustClosePosEtrReqDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 更新关闭委托状态
 * 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2019年2月15日
 * @see CfetsEntrustStatusMapper
 * @since 1.0
 */
@Repository
public interface CfetsEntrustStatusMapper {

    void updateEntrustStatus(Map<String, String> sysDate);

    List<CfetsEntrustClosePosEtrReqDto> selectEntrustList(Map<String, String> sysDate);

    List<String> selectEntrustIds(Map<String, String> sysDate);

    void updateDistributeStatus(Map<String, String> sysDate);

    void updateBusModel(Map<String, Object> param);
}

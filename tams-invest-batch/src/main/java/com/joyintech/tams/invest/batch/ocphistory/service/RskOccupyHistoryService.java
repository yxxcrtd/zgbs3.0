/*
 * 文件名：RskOccupyHistoryService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年9月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.ocphistory.service;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.ocphistory.dao.RskOccupyHistoryMapper;

/**
 * 批处理历史化预占/实占信息
 * 
 * @author zhangrui1
 * @version 1.0 2020年11月5日
 * @see RskOccupyHistoryService
 * @since 1.0
 */
@Service
public class RskOccupyHistoryService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private RskOccupyHistoryMapper rskPreOccupyInfoProMapper;

    /**
     * 历史化处理
     * 
     * @param param 传递参数
     */
    @Transactional
    public void execute(Map<String, Object> param) {
        //获取平台日
        Date platDate=(Date)param.get(BatchConstant.PLATE_DATE);
        String sysDate = DateUtil.format(platDate);
        logger.info("{} 历史化处理开始 ......", sysDate);
        // 实占历史化
        rskPreOccupyInfoProMapper.historyOccupy(platDate);
        // 预占历史化
        rskPreOccupyInfoProMapper.historyPreOccupy(platDate);
        // 实占删除
        rskPreOccupyInfoProMapper.deleteOccupy(platDate);
        // 预占删除
        rskPreOccupyInfoProMapper.deletePreOccupy(platDate);
        logger.info("{} 历史化处理结束 ......", sysDate);
    }
}

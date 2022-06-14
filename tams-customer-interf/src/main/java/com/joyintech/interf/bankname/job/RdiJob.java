/*
 * 文件名：DemoJob.java
 * 版权：Copyright 2019-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：王波
 * 修改时间：2019/5/27 15:04
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.interf.bankname.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.joyintech.interf.bankname.cfetsup.job.service.ImixCounterDealerXmlReaderService;

/**
 * 解析外汇交易中心rdi数据
 * @author 卫平文
 * @version 1.0 2020年01月04日
 * @see
 * @since 1.0
 */
@Component
@EnableScheduling
public class RdiJob {
    /** 日志 */
//    private static Logger log = Logger.getLogger(RdiJob.class);
	private static Logger log = LoggerFactory.getLogger(RdiJob.class);
    @Autowired
    private ImixCounterDealerXmlReaderService imixCounterDealerXmlReaderService;
    public void analysisRdi() {
        try{
            imixCounterDealerXmlReaderService.doBusiness();
        }catch (Exception e) {
            log.error("外汇rdi数据解析失败："+e.getMessage());
        }
    }
}

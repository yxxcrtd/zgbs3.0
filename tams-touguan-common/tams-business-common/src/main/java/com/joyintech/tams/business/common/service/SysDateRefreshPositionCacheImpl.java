/*
 * 文件名：EntrustRdsCommonAop.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.common.service;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.systemtools.service.SysDateRefreshPositionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
//@Aspect
public class SysDateRefreshPositionCacheImpl implements SysDateRefreshPositionCache {

    /**
     * 日志
     */
    public  static Logger logger = LoggerFactory.getLogger(SysDateRefreshPositionCacheImpl.class);

    public ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);


//    @Pointcut("execution(* com.joyintech.system.sysdate.controller.SysDate001Controller.update(..))")
//    protected void updatePoint() {
//        logger.info("----------平台日切换----------");
//    }


    /**
     * 平台日切换需要更新头寸内存数据库
     */
//    @AfterReturning("updatePoint()")
    public  void updateBalance(Date date) {
        if(StringUtil.equals(CacheUtil.getSysParam("positionRefreshCacheEnable"), DictCodeList.是否_是.getValue())) {//positionRefreshCacheEnable
            logger.info("----------平台日切换需要更新头寸内存数据库start----------");
            Object o = new HashMap<String, Object>();
            fixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    logger.info("----------平台日切换更新头寸内存数据库请求开始----------");
//                    try {
//                        Thread.sleep(1000);//平台日事务可能未提交，暂缓请求缓存。
//                    } catch (InterruptedException e) {
//                        logger.error("", e);
//                    }
                    long start = System.currentTimeMillis();
                    String res = PositionHttpRequest.updateBalance(o);
                    logger.info("缓存头寸缓存请求返回" + res);
                    logger.info("缓存头寸缓存请求结束" + (System.currentTimeMillis() - start));
                    logger.info("----------平台日切换更新头寸内存数据库请求结束----------");
                }
            });
            logger.info("----------平台日切换需要更新头寸内存数据库end----------");
        }
    }





}

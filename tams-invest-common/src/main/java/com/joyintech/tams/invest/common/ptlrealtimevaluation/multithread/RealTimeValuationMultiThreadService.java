package com.joyintech.tams.invest.common.ptlrealtimevaluation.multithread;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @program: tams-disclosure-busicommon
 * @description: 多线程启动类
 * @author: 强成西
 * @create: 2020-04-16 19:43
 **/
@Service
public class RealTimeValuationMultiThreadService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${invest.mock.concurrent.maxThread:10}")
    private int maxThread;

    private ExecutorService fixedThreadPool;

    public void execute(List<RealTimeValuationTask> realTimeValuationTasks) {
        if (fixedThreadPool == null) {
            fixedThreadPool = Executors.newFixedThreadPool(maxThread);
        }
        List<Future<String>> futures = null;
        try {
            futures = fixedThreadPool.invokeAll(realTimeValuationTasks);
        } catch (InterruptedException e) {
            logger.error("试算失败", e);
            Thread.currentThread().interrupt();
        }
        if(null != futures)
        futures.forEach(batchExecuteResultFuture -> {
            String result = null;
            try {
                result = batchExecuteResultFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("试算失败", e);
                Thread.currentThread().interrupt();
            }
        });

    }
}

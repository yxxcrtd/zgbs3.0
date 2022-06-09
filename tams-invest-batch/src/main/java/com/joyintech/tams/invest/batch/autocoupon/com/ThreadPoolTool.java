package com.joyintech.tams.invest.batch.autocoupon.com;

import com.joyintech.tams.invest.batch.autocoupon.dto.AutoCouponErrLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolTool<T,K> {
    //单个线程处理的数据量
    private int singleCount;
    //处理的总数据量
    private int listSize;
    //开启的线程数
    private int runSize;
    //操作的数据集
    private List<K> list;
    //计数器
    private CountDownLatch begin,end;
    //线程池
    private ExecutorService executorService;
    //回调
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public ThreadPoolTool(int singleCount, List<K> list){
        this.singleCount = singleCount;
        this.list = list;
        if (list != null){
            this.listSize = list.size();
            this.runSize = (this.listSize/this.singleCount) + 1;
        }
    }

    public List<Future> excute(){
        List<Future> futures = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(runSize);
        begin = new CountDownLatch(1);
        end = new CountDownLatch(runSize);
        //创建线程
        int startIndex = 0;
        int endIndex = 0;
        List<K> newList = null;
        for (int i = 0; i < runSize; i++) {
            //计算每个线程对应的数据
            if (i < (runSize - 1)){
                startIndex = i * singleCount;
                endIndex = (i + 1) * singleCount;
                newList = list.subList(startIndex,endIndex);
            }else {
                startIndex = i * singleCount;
                endIndex = listSize;
                newList = list.subList(startIndex,endIndex);
            }
            //创建线程类处理数据
            MyThread<K> myThread = new MyThread(newList, begin, end) {
                @Override
                public List<T> method(List list) {
                    return  callBack.method(list);
                }
            };
            //执行线程
            Future<List<T>> future = executorService.submit(myThread);
            futures.add(future);
        }
        //计数器减一
        begin.countDown();
        try {
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            // Sonar Fix modify by huwei 220608
            Thread.currentThread().interrupt();
        }
        //关闭线程池
        executorService.shutdown();
        return futures;
    }

    //抽象线程类
    public abstract class MyThread<K> implements Callable {

        private List<K> list;
        private CountDownLatch begin,end;

        public MyThread(List<K> list, CountDownLatch begin, CountDownLatch end){
            this.list = list;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public List<T> call() {

            try {
                //执行程序
                List<T> t = method(list);
                begin.await();
                //计数器减一
                end.countDown();
                return t;
            } catch (InterruptedException e) {
                e.printStackTrace();
                //计数器减一
                end.countDown();
                // Sonar Fix modify by huwei 220608
                Thread.currentThread().interrupt();
                return null;
            }
        }
        public abstract List<T> method(List<K> list);
    }

    //回调接口定义
    public interface CallBack<T,K>{
        public List<T> method(List<K> list);
    }
}

package com.joyintech.tams.valuation.batch.common.log.aspect;

import com.alibaba.fastjson.JSON;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.valuation.batch.common.fund.dao.FundCommonMapper;
import com.joyintech.tams.valuation.batch.common.log.dao.DetailBatchLogMapper;
import com.joyintech.tams.valuation.batch.common.log.dto.CustomDetailBatchLogDto;
import com.joyintech.tams.valuation.batch.common.log.dto.DetailBatchLogDto;
import com.joyintech.tams.valuation.batch.common.log.dto.DetailBatchLogExpand;
import com.joyintech.tams.valuation.batch.common.log.dto.DetailLogConfig;
import com.joyintech.tams.valuation.batch.common.log.exception.DetailBatchException;
import groovy.util.logging.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 * @ClassName DetailBatchLogAspect
 * @Description 日志切面实现类
 * @Date 2022/3/28 10:04
 * @Author 殷亚
 * @Version 1.0
 **/

@Component
@Aspect
public class DetailBatchLogAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //保存最新任务日志 map<任务组+taskID,日志>
    private ConcurrentHashMap<String, DetailBatchLogDto> taskLogger=new ConcurrentHashMap<String, DetailBatchLogDto>();

    //保存错误日志 map<任务组+taskID,日志>
    private ConcurrentHashMap<String, DetailBatchLogDto> errorLogger=new ConcurrentHashMap<String, DetailBatchLogDto>();

    //账套属性名称
    private final String BOOK_SET_ID="booksetId";
    //金融产品代码
    private final String FIN_PROD_ID="finprodId";
    //Task信息
    private final String BATCH_TASK="batchTask";
    //任务组信息
    private final String BATCH_EXCUTE_INFO="batchExecuteInfo";
    //整体任务调度的类名
    private final String BATCH_DIS_PATCH="com.joyintech.tams.valuation.batch.batchdispatch.runner.BatchDispatchTask";

    //NeedOperateEntity的产品id属性，用于处理task的输出
    private final String PROD_ID="prodId";

    private Map<String,Object> noBookSetId=new HashMap<String,Object>();

    @Autowired
    private DetailBatchLogMapper detailBatchLogMapper;
    /**
     * 事务管理器
     */
    @Autowired
    private DataSourceTransactionManager transactionManager;
    //定义切面范围 目前只考虑估值的批处理
    //process层切面方法，对process方法切面
    @Pointcut("execution(public * com.joyintech.tams.valuation.batch.*.processor..*.process(..))")
    public void processAspect() {

    }

    //task层切面方法，对task方法切面
//    @Pointcut("execution(public * com.joyintech.tams.valuation.batch.*.runner..*.execute(..))")
//    public void taskAspect() {
//
//    }


    /* @Author 殷亚
     * @Description task前置切面获取任务调度、任务组等信息
     * @Date  2022/3/29 11:52
     * @Version 1.0
     * @Param [point]
     * @Return void
     **/
//    @Before(value = "taskAspect()")
//    public void taskBefore(JoinPoint point) throws Throwable {
//        // 从切面织入点处通过反射机制获取织入点处的方法
//        MethodSignature signature = (MethodSignature) point.getSignature();
//        // 请求方法名
//        Method method = signature.getMethod();
//        Class clz = point.getTarget().getClass();
//        //从父类baseService中抽取任务信息
//        Class baseService = point.getTarget().getClass().getSuperclass();
//        Field[] fields = baseService.getDeclaredFields();
//        String methodName = method.getName();
//        // 请求的参数
//        Object[] args = point.getArgs();
//        DetailBatchLogDto lger = new DetailBatchLogDto();
//        lger.setClassName(clz.getName());
//        lger.setMethodName(methodName);
//        lger.setArgs(args);
//        lger.setStart(LocalDateTime.now());
//        SysBatchExecInfo batchExecuteInfo=new SysBatchExecInfo();
//        SysBatchTask batchTask=new SysBatchTask();
//
//        Field excuteInfo = baseService.getDeclaredField(BATCH_EXCUTE_INFO);
//        excuteInfo.setAccessible(true);
//        batchExecuteInfo=(SysBatchExecInfo)excuteInfo.get(point.getTarget());
//
//        Field taskinfo = baseService.getDeclaredField(BATCH_TASK);
//        taskinfo.setAccessible(true);
//        batchTask=(SysBatchTask)taskinfo.get(point.getTarget());
//        if(batchExecuteInfo!=null){
//            lger.setGroupId(batchExecuteInfo.getBatchExedetailSeqNo());
//        }
//        if(batchTask!=null){
//            lger.setTaskId(batchTask.getTaskId());
//            lger.setTaskName(batchTask.getTaskName());
//            lger.setTaskBeanName(batchTask.getBeanName());
//        }
//        String key=batchExecuteInfo.getBatchExedetailSeqNo()+batchTask.getTaskId();
//        //整体任务调度开始，初始化任务map
//        if(clz.getName().equals(BATCH_DIS_PATCH)){
//            logger.info("--------------------------------------------整体任务调度开始-----------------------------------------------------------");
//            logger.info("开始执行时间:"+lger.getStart().format(df));
////            taskLogger.clear();
////            errorLogger.clear();
//        }else{
//            logger.info("任务组：【"+lger.getGroupId()+ "】 【"+lger.getTaskName()+"】 任务开始----------------");
//        }
//        //保存任务-日志map  任务开始-->放
//        taskLogger.put(key ,lger);
//
//    }

    /* @Author 殷亚
     * @Description task执行完成切面方法，记录执行时间
     * @Date  2022/3/29 20:14
     * @Version 1.0
     * @Param [point, result]
     * @Return void
     **/
//    @AfterReturning(value = "taskAspect()")
//    public void taskAfter(JoinPoint point) {
//        Class clz = point.getTarget().getClass();
//        //从父类baseService中抽取任务信息
//        SysBatchTask batchTask=new SysBatchTask();
//        SysBatchExecInfo batchExecuteInfo=new SysBatchExecInfo();
//
//        try {
//            Class baseService = point.getTarget().getClass().getSuperclass();
//            Field excuteInfo = baseService.getDeclaredField(BATCH_EXCUTE_INFO);
//            excuteInfo.setAccessible(true);
//            batchExecuteInfo=(SysBatchExecInfo)excuteInfo.get(point.getTarget());
//            Field taskinfo = baseService.getDeclaredField(BATCH_TASK);
//            taskinfo.setAccessible(true);
//            batchTask=(SysBatchTask)taskinfo.get(point.getTarget());
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        if(batchExecuteInfo!=null&&batchTask!=null){
//            String key=batchExecuteInfo.getBatchExedetailSeqNo()+batchTask.getTaskId();
//            DetailBatchLogDto lger = taskLogger.get(key);
//            if(lger!=null){
//                lger.setEnd(LocalDateTime.now());
//                //释放日志 任务结束-->移除
//                Iterator<String> iter = taskLogger.keySet().iterator();
//                while(iter.hasNext()){
//                    String ikey = iter.next();
//                    if(key.equals(ikey)){
//                        iter.remove();
//                    }
//                }
//            }
//            if(clz.getName().equals(BATCH_DIS_PATCH)){
//                logger.info("--------------------------------------------整体任务调度结束-----------------------------------------------------------");
//            }else{
//                logger.info("任务组：【"+lger.getGroupId()+ "】 【"+lger.getTaskName()+"】 任务结束----------------");
//            }
//            logger.info("结束执行时间:"+lger.getEnd().format(df));
//
//        }
//
//    }

    /* @Author 殷亚
     * @Description process环绕切面,捕获异常，记录详情日志
     * @Date  2022/3/29 11:53
     * @Version 1.0
     * @Param [point]
     * @Return java.lang.Object
     **/
    @Around(value = "processAspect()")
    public Object processAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Object[] args = point.getArgs();
        Object items= (Object) args[0];
        Class classz = point.getTarget().getClass();
        Method method = signature.getMethod();
        Class <?> clz=items.getClass();
        Field[] fields = clz.getDeclaredFields();
        //从参数抽取账套ID
        String booksetId=null;
        String finProdId=null;
        for (Field field : fields) {
            //获取账套ID
            if(field.getName().equals(BOOK_SET_ID)){
                field.setAccessible(true);
                Method m = null;
                try {
                    m = (Method) items.getClass().getMethod("get"+getMethodName(field.getName()));
                    booksetId=(String)m.invoke(items);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            //获取金融产品代码
            if(field.getName().equals(FIN_PROD_ID)){
                field.setAccessible(true);
                Method m = null;
                try {
                    m = (Method) items.getClass().getMethod("get"+getMethodName(field.getName()));
                    finProdId=(String)m.invoke(items);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }


        //获取process的父类-->BaseProcessor
        Class baseProcessor=point.getTarget().getClass().getSuperclass();
        //从process父类属性中抽取任务组相关信息
        Field[] baseProcessFileds = baseProcessor.getDeclaredFields();
        SysBatchExecInfo batchExecuteInfo=new SysBatchExecInfo();
        SysBatchTask batchTask=new SysBatchTask();

        Field excuteInfo = baseProcessor.getDeclaredField(BATCH_EXCUTE_INFO);
        excuteInfo.setAccessible(true);
        batchExecuteInfo=(SysBatchExecInfo)excuteInfo.get(point.getTarget());

        Field taskinfo = baseProcessor.getDeclaredField(BATCH_TASK);
        taskinfo.setAccessible(true);
        batchTask=(SysBatchTask)taskinfo.get(point.getTarget());

        if(booksetId==null){
            noBookSetId.put(batchTask.getBeanName(),items);
        }
        String key=batchExecuteInfo.getSeqNo();
        DetailBatchLogDto loggers = taskLogger.get(key);
        if(loggers==null){
            loggers=new DetailBatchLogDto();
            loggers.setBooksetId(booksetId==null?"":booksetId);
            loggers.setFinprodId(finProdId);
            loggers.setClassName(classz.getName());
            loggers.setMethodName(method.getName());
            loggers.setArgs(args);
            if(batchExecuteInfo!=null){
                loggers.setExeSeqno(batchExecuteInfo.getSeqNo());
                loggers.setGroupId(batchExecuteInfo.getBatchExedetailSeqNo());
                loggers.setExcuteDate(batchExecuteInfo.getExecuteDate());
            }
            if(batchTask!=null){
                loggers.setTaskId(batchTask.getTaskId());
                loggers.setTaskName(batchTask.getTaskName());
                loggers.setTaskBeanName(batchTask.getBeanName());
            }
        }

        StopWatch stopWatch = new StopWatch();
        loggers.setStart(LocalDateTime.now());
        stopWatch.start();
        Object result = null;
        try {
            result = point.proceed();
        } catch (Exception e) {
            handleThrowing(point,e,loggers);
            throw new Exception(e.getMessage());
        }
        stopWatch.stop();
        loggers.setEnd(LocalDateTime.now());
        loggers.setResultInfo(result);
        return result;
    }

    /* @Author 殷亚
     * @Description process的后置切面方法，处理process的返回问题
     * @Date  2022/3/29 22:00
     * @Version 1.0
     * @Param [point]
     * @Return void
     **/
    @After(value = "processAspect()")
    public void processAfter(JoinPoint point) throws Throwable {
        //从父类baseProcess中抽取任务信息
        Class baseProcess = point.getTarget().getClass().getSuperclass();
        Field[] fields = baseProcess.getDeclaredFields();
        // 请求的参数
        Object[] args = point.getArgs();
        Object items= (Object) args[0];
        SysBatchExecInfo batchExecuteInfo=new SysBatchExecInfo();
        SysBatchTask batchTask=new SysBatchTask();

        Field excuteInfo = baseProcess.getDeclaredField(BATCH_EXCUTE_INFO);
        excuteInfo.setAccessible(true);
        batchExecuteInfo=(SysBatchExecInfo)excuteInfo.get(point.getTarget());

        Field taskinfo = baseProcess.getDeclaredField(BATCH_TASK);
        taskinfo.setAccessible(true);
        batchTask=(SysBatchTask)taskinfo.get(point.getTarget());
        String key=batchExecuteInfo.getSeqNo();
        DetailBatchLogDto lger = errorLogger.get(key);
        if(lger!=null){
            String booksetId=lger.getBooksetId();
            String finprodId=lger.getFinprodId();
            String busiId=lger.getBusiId();
            List<DetailBatchLogExpand> dto=lger.getDetailList();
            List<CustomDetailBatchLogDto> detailLsit=new  ArrayList<CustomDetailBatchLogDto>();
            if(CollectionUtils.isNotEmpty(dto)){
                dto.stream().forEach(s->{
                    CustomDetailBatchLogDto cus=(CustomDetailBatchLogDto) s;
                    detailLsit.add(cus);
                });
            }


            StringBuffer prodIdJson=new StringBuffer();
            prodIdJson.append("{");
            prodIdJson.append("账套代码："+booksetId);
            if(finprodId!=null){
                prodIdJson.append(",金融产品代码："+finprodId);
            }
            if(busiId!=null){
                prodIdJson.append(",业务代码："+busiId);
            }
            if(CollectionUtils.isNotEmpty(detailLsit)){
                prodIdJson.append(",详情信息："+getDetailJson(detailLsit));
            }

            prodIdJson.append("}");
            Class <?> clz=items.getClass().getSuperclass();
            Field prodId = clz.getDeclaredField(PROD_ID);
            prodId.setAccessible(true);
            //系统原有逻辑有些批会在reder层给process执行参数增加账套、业务明细等信息，所以不破坏原来的逻辑，为空才赋值
            String orcProdid=(String)prodId.get(items);
            if(StringUtils.isEmpty(orcProdid)){
                prodId.set(items,prodIdJson.toString());
            }
            //写完错误日志后释放errorLogger
            Iterator<String> erroriter = errorLogger.keySet().iterator();
            while(erroriter.hasNext()){
                String ikey = erroriter.next();
                if(key.equals(ikey)){
                    erroriter.remove();
                }
            }
        }




    }




    /* @Author 殷亚
     * @Description process正常返回的切面处理
     * @Date  2022/3/29 20:09
     * @Version 1.0
     * @Param [point, result]
     * @Return void
     **/
//    @AfterReturning(value = "processLogger()", returning = "result")
//    public void processAfterReturning(JoinPoint point,Object result) {
//        MethodSignature signature = (MethodSignature) point.getSignature();
//        // 请求方法名
//        Method method = signature.getMethod();
//        Class clz = point.getTarget().getClass();
//        Object o=point.getTarget().getClass().getSuperclass();
//        DetailBatchLog lger = new DetailBatchLog();
//        lger.setEnd(LocalDateTime.now());
//
//    }






    /**
     * 捕获异常的方法，输出error级别日志 <功能详细描述>
     * @author 殷亚
     * @date 2022/4/2 10:22
     * @param point 切点
     * @param e 异常信息
     * @return void
     * @see
     * @version 1.0
     */
    //@AfterThrowing(pointcut = "processLogger()", throwing = "e")
    public void handleThrowing(ProceedingJoinPoint point,Exception e,DetailBatchLogDto loggers) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Object[] args = point.getArgs();
        Object items= (Object) args[0];
        Class classz = point.getTarget().getClass();
        Method method = signature.getMethod();
        Class <?> clz=items.getClass();
        //从参数属性抽取booksetId
        Field[] argfields = clz.getDeclaredFields();
        String booksetId=null;
        String finProdId=null;
        for (Field field : argfields) {
            //获取账套ID
            if(field.getName().equals(BOOK_SET_ID)){
                field.setAccessible(true);
                Method m = null;
                try {
                    m = (Method) items.getClass().getMethod("get"+getMethodName(field.getName()));
                    booksetId=(String)m.invoke(items);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            //获取金融产品代码
            if(field.getName().equals(FIN_PROD_ID)){
                field.setAccessible(true);
                Method m = null;
                try {
                    m = (Method) items.getClass().getMethod("get"+getMethodName(field.getName()));
                    finProdId=(String)m.invoke(items);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        //获取process的父类-->BaseProcessor
        Class baseProcessor=point.getTarget().getClass().getSuperclass();
        //从process父类属性中抽取任务组相关信息
        Field[] baseProcessFileds = baseProcessor.getDeclaredFields();
        SysBatchExecInfo batchExecuteInfo=new SysBatchExecInfo();
        SysBatchTask batchTask=new SysBatchTask();

        try {
            Field excuteInfo = baseProcessor.getDeclaredField(BATCH_EXCUTE_INFO);
            excuteInfo.setAccessible(true);
            batchExecuteInfo=(SysBatchExecInfo)excuteInfo.get(point.getTarget());

            Field taskinfo = baseProcessor.getDeclaredField(BATCH_TASK);
            taskinfo.setAccessible(true);
            batchTask=(SysBatchTask)taskinfo.get(point.getTarget());
        } catch (NoSuchFieldException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
        if(booksetId==null){
            noBookSetId.put(batchTask.getBeanName(),items);
        }
        String key=batchExecuteInfo.getSeqNo();
        loggers.setBooksetId(booksetId==null?"":booksetId);
        loggers.setFinprodId(finProdId);
        loggers.setEnd(LocalDateTime.now());
        loggers.setCreateUser(batchExecuteInfo.getCreateUser());
        loggers.setUpdateUser(batchExecuteInfo.getUpdateUser());
        StringBuffer mes=new StringBuffer();
        String errorMsg = StringUtils.isEmpty(e.getMessage()) ? "系统异常" : e.getMessage();
        loggers.setResultInfo(errorMsg);
        //对于主动抛出的详细日志异常，写入详细日志
        if(e instanceof DetailBatchException){
            String detaiError = StringUtils.isEmpty(e.getMessage()) ? "系统异常" : e.getMessage();
            CustomDetailBatchLogDto detailLog = new CustomDetailBatchLogDto();
            loggers.setBusiId(((DetailBatchException) e).getBusiId());
            detailLog=(CustomDetailBatchLogDto) ((DetailBatchException) e).getDetailBatchLogExpand();
            if(detailLog!=null) {
                detailLog.setResult(detaiError);
                List<DetailBatchLogExpand> detailList = new ArrayList<>();
                detailList.add(detailLog);
                loggers.setDetailList(detailList);
            }
        }
        //由于框架限制了换行符，只能逐行打印
        synchronized(this) {
            logger.error("任务组【" + loggers.getGroupId() + "】详情日志---------------开始-------------------------------");
            logger.error("任务:" + loggers.getTaskName());
            logger.error("执行时间:" + loggers.getExcuteDate());
            logger.error("账套ID:" + booksetId);
            if (loggers.getBusiId() != null) {
                logger.error("业务编号:" + loggers.getBusiId());
            }
            if (loggers.getFinprodId() != null) {
                logger.error("金融产品代码:" + loggers.getFinprodId());
            }

            if (CollectionUtils.isNotEmpty(loggers.getDetailList())) {
                loggers.getDetailList().stream().forEach(s -> {
                    logger.error("----业务详细:" + JSON.toJSONString(s));
                    logger.error("----异常信息:");
                    logger.error(JSON.toJSONString(s.getResult()));
                });

            }
            logger.error("类名:" + loggers.getClassName());
            logger.error("方法名:" + loggers.getMethodName());
            logger.error("入参:" + JSON.toJSONString(args));
            logger.error("开始执行时间:" + loggers.getStart().format(df));
            logger.error("结束执行时间:" + loggers.getEnd().format(df));
            logger.error("执行结果:");
            logger.error(JSON.toJSONString(loggers.getResultInfo()));
            logger.warn("没有账套ID的集合" + JSON.toJSONString(noBookSetId));
            //放错误日志
            errorLogger.put(key, loggers);
            logger.error("任务组【" + loggers.getGroupId() + "】详情日志---------------结束-------------------------------");
        }
        //写入数据库
        logger.error("--写入详情日志表开始");
        DataSourceTransactionManager transactionManager = getTransactionManager();
        // 获取事务控制管理器
        TransactionStatus status = getTransactionStatus();
        //参数字段入库很慢，暂不入库
        loggers.setArgsClob(null);
        int a=detailBatchLogMapper.insertBatchDetailLog(loggers);
        transactionManager.commit(status);
        logger.error("--写入详情日志表开结束");

    }

    /* @Author 殷亚
     * @Description TODO 首字母转为大写
     * @Date  2022/3/30 15:18
     * @Version 1.0        
     * @Param [fildeName]
     * @Return java.lang.String
     **/
    private static String getMethodName(String fildeName) throws Exception{
                 byte[] items = fildeName.getBytes();
                 items[0] = (byte) ((char) items[0] - 'a' + 'A');
                 String name=new String(items);
                 return name;
    }

    /* @Author 殷亚
     * @Description TODO 组装日志明细的json，中文描述反射调用注解生成
     * @Date  2022/3/30 15:19
     * @Version 1.0        
     * @Param [dto]
     * @Return java.lang.String
     **/
    public String getDetailJson(List<CustomDetailBatchLogDto> list){
        StringBuffer json=new StringBuffer();
        Field[] field = CustomDetailBatchLogDto.class.getDeclaredFields();
        Field[] pfield = CustomDetailBatchLogDto.class.getSuperclass().getDeclaredFields();
        json.append("[");
        for(CustomDetailBatchLogDto dto:list) {
            json.append("{");
            if (pfield != null) {
                for (Field fie : pfield) {
                    if (!fie.isAccessible()) {
                        fie.setAccessible(true);
                    }
                    try {
                        String val = (String) fie.get(dto);
                        DetailLogConfig annon = fie.getAnnotation(DetailLogConfig.class);
                        String dis = annon.description();
                        boolean show = annon.show();
                        if (show&&val!=null) {
                            json.append(dis + ":" + val + ",");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
            if (field != null) {
                for (Field fie : field) {
                    if (!fie.isAccessible()) {
                        fie.setAccessible(true);
                    }
                    try {
                        String val = (String) fie.get(dto);
                        DetailLogConfig annon = fie.getAnnotation(DetailLogConfig.class);
                        String dis = annon.description();
                        boolean show = annon.show();
                        if (show&&val!=null) {
                            json.append(dis + ":" + val + ",");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
                json.delete(json.length() - 1, json.length());
            }


            json.append("},");
        }
        json.delete(json.length() - 1, json.length());
        json.append("]");
        return json.toString();
    }

    /**
     * 取得事务管理器
     *
     * @return 事务管理器
     * @see
     * @since 1.0
     */
    public DataSourceTransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * 获得事务状态
     *
     * @return 事务状态
     * @see
     * @since 1.0
     */
    public TransactionStatus getTransactionStatus() {
        // 获取事务定义
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 设置事务隔离级别，开启新事务
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // 获得事务状态
        return transactionManager.getTransaction(def);
    }

}

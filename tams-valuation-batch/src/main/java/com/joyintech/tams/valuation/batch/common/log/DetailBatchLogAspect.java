package com.joyintech.tams.valuation.batch.common.log;

import com.alibaba.fastjson.JSON;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import groovy.util.logging.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * @ClassName DetailBatchLogAspect
 * @Description 日志切面实现类
 * @Date 2022/3/28 10:04
 * @Author 殷亚
 * @Version 1.0
 **/
@Aspect
@Component
@Slf4j
public class DetailBatchLogAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private ThreadLocal<DetailBatchLog> detailLogs = new ThreadLocal<DetailBatchLog>();
    //账套属性名称
    private final String BOOK_SET_ID="booksetId";
    //Task信息
    private final String BATCH_TASK="batchTask";
    //任务组信息
    private final String BATCH_EXCUTE_INFO="batchExecuteInfo";

    private List<Object> noBookSetId=new ArrayList<>();
    //定义切面范围 目前只考虑估值的批处理
    //process层切面方法，对process方法切面
    @Pointcut("execution(public * com.joyintech.tams.valuation.batch.*.processor..*.process(..))")
    public void processLogger() {

    }

    //task层切面方法，对task方法切面
    @Pointcut("execution(public * com.joyintech.tams.valuation.batch.*.runner..*.execute(..))")
    public void taskLogger() {

    }


    /* @Author 殷亚
     * @Description task前置切面获取任务调度、任务组等信息
     * @Date  2022/3/29 11:52
     * @Version 1.0
     * @Param [point]
     * @Return void
     **/
    @Before(value = "taskLogger()")
    public void doBefore(JoinPoint point) throws Throwable {
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) point.getSignature();
        // 请求方法名
        Method method = signature.getMethod();
        Class clz = point.getTarget().getClass();
        Object o=point.getTarget().getClass().getSuperclass();
        Field[] fields = o.getClass().getDeclaredFields();
        SysBatchTask batchTask=new SysBatchTask();
        for (Field field : fields) {
            if(field.getName().equals(BATCH_TASK)){
                field.setAccessible(true);
                Method m = null;
                try {
                    m = (Method)o.getClass().getMethod("get"+getMethodName(field.getName()));
                    batchTask=(SysBatchTask)m.invoke(o);
                    break;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        String methodName = method.getName();
        // 请求的参数
        Object[] args = point.getArgs();

        DetailBatchLog lger = new DetailBatchLog();
        lger.setClassName(clz.getName());
        lger.setMethodName(methodName);
        lger.setArgs(args);
        lger.setStart(LocalDateTime.now());
        detailLogs.set(lger);
        logger.error("日志详情------start-----------------------------------------------------------");
    }

    @AfterReturning(value = "taskLogger()", returning = "result")
    public void doAferReturning(Object result) {
        DetailBatchLog lger = detailLogs.get();
        lger.setEnd(LocalDateTime.now());
        detailLogs.remove();
        logger.error("日志详情------end-----------------------------------------------------------");
    }


    /* @Author 殷亚
     * @Description process环绕切面输出info日志使用，项目可不开启
     * @Date  2022/3/29 11:53
     * @Version 1.0
     * @Param [point]
     * @Return java.lang.Object
     **/
    @Around(value = "processLogger()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Object[] args = point.getArgs();
        Object items= (Object) args[0];
        Class classz = point.getTarget().getClass();
        Method method = signature.getMethod();
        Class <?> clz=items.getClass();
        Field[] fields = clz.getDeclaredFields();
        DetailBatchLog loggers = detailLogs.get();
        String booksetId=null;
        for (Field field : fields) {
            if(field.getName().equals(BOOK_SET_ID)){
                field.setAccessible(true);
                Method m = null;
                try {
                    m = (Method) items.getClass().getMethod("get"+getMethodName(field.getName()));
                    booksetId=(String)m.invoke(items);
                    break;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
        if(booksetId==null){
            noBookSetId.add(items.getClass().getName());
        }

        if(loggers==null){
            loggers=new DetailBatchLog();
            loggers.setBooksetId(booksetId==null?"":booksetId);
            loggers.setClassName(classz.getName());
            loggers.setMethodName(method.getName());
            loggers.setArgs(args);
            loggers.setEnd(LocalDateTime.now());
        }
        StopWatch stopWatch = new StopWatch();
        loggers.setStart(LocalDateTime.now());
        stopWatch.start();
        Object result = point.proceed();
        stopWatch.stop();
        loggers.setEnd(LocalDateTime.now());
        loggers.setResult(result);
        //由于框架限制了换行符，只能逐行打印
        logger.error("账套ID:"+booksetId);
        if(CollectionUtils.isNotEmpty(loggers.getDetailList()) ){
            logger.error("----业务编号:"+loggers.getBusiId());
            logger.error("----详细信息:");
            logger.error(JSON.toJSONString(loggers.getDetailList().get(0).getResult()));
        }
        logger.error("类名:"+loggers.getClassName());
        logger.error("方法名:"+loggers.getMethodName());
        logger.error("入参:"+JSON.toJSONString(args));
        logger.error("开始执行时间:"+loggers.getStart().format(df));
        logger.error("结束执行时间:"+loggers.getEnd().format(df));
        logger.error("执行结果:");
        logger.error(JSON.toJSONString(loggers.getResult()));
        logger.error("没有账套ID的集合"+JSON.toJSONString(noBookSetId));



        return result;
    }

    /* @Author 殷亚
     * @Description 捕获异常的切面方法，输出error级别日志
     * @Date  2022/3/29 11:54
     * @Version 1.0
     * @Param [point, e]
     * @Return void
     **/
    @AfterThrowing(pointcut = "processLogger()", throwing = "e")
    public void handleThrowing(JoinPoint point,Exception e) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Object[] args = point.getArgs();
        Object items= (Object) args[0];
        Class classz = point.getTarget().getClass();
        Method method = signature.getMethod();
        Class <?> clz=items.getClass();
        Field[] fields = clz.getDeclaredFields();
        String booksetId=null;
        for (Field field : fields) {
            if(field.getName().equals(BOOK_SET_ID)){
                field.setAccessible(true);
                Method m = null;
                try {
                    m = (Method) items.getClass().getMethod("get"+getMethodName(field.getName()));
                    booksetId=(String)m.invoke(items);
                    break;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
        DetailBatchLog loggers = detailLogs.get();
        if(loggers==null){
             loggers=new DetailBatchLog();
             loggers.setStart(LocalDateTime.now());
        }
        loggers.setBooksetId(booksetId==null?"":booksetId);
        loggers.setClassName(classz.getName());
        loggers.setMethodName(method.getName());
        loggers.setArgs(args);
        loggers.setEnd(LocalDateTime.now());
        StringBuffer mes=new StringBuffer();
        String errorMsg = StringUtils.isEmpty(e.getMessage()) ? "系统异常" : e.getMessage();
        loggers.setResult(errorMsg);
        //对于主动抛出的详细日志异常，写入详细日志
        if(e instanceof DetailBatchException){
            String detaiError = StringUtils.isEmpty(e.getMessage()) ? "系统异常" : e.getMessage();
            DetailBatchLog detailLog = new DetailBatchLog();
            if(loggers != null){
                BeanUtil.copyPropertiesIgnoreNull(detailLog, loggers);
            }
            detailLog.setBusiId(((DetailBatchException) e).getBusiId());
            detailLog.setResult(detaiError);
            List<DetailBatchLog> detailList=new ArrayList<>();
            detailList.add(detailLog);
            loggers.setDetailList(detailList);
        }
        //由于框架限制了换行符，只能逐行打印
        logger.error("账套ID:"+booksetId);
        if(CollectionUtils.isNotEmpty(loggers.getDetailList()) ){
            logger.error("----业务编号:"+loggers.getBusiId());
            logger.error("----详细信息:");
            logger.error(JSON.toJSONString(loggers.getDetailList().get(0).getResult()));
        }
        logger.error("类名:"+loggers.getClassName());
        logger.error("方法名:"+loggers.getMethodName());
        logger.error("入参:"+JSON.toJSONString(args));
        logger.error("开始执行时间:"+loggers.getStart().format(df));
        logger.error("结束执行时间:"+loggers.getEnd().format(df));
        logger.error("执行结果:");
        logger.error(JSON.toJSONString(loggers.getResult()));

    }


    private static String getMethodName(String fildeName) throws Exception{
                 byte[] items = fildeName.getBytes();
                 items[0] = (byte) ((char) items[0] - 'a' + 'A');
                 String name=new String(items);
                 return name;
             }

}

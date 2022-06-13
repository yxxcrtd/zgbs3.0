package com.joyintech.tams.business.common.config;


import com.joyintech.tams.business.common.pojo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一处理一下controller层未捕获的ajax请求时发生的异常
 * 目前系统如果发生了未捕获的异常返回给前台的是一个错误页面,或者自己在controller中捕获异常
 * 修改为统一返回错误信息，同时controller层不建议捕获异常，应该由系统统一处理
 */
@ControllerAdvice(basePackages = "com.joyintech.tams.business")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseAdviceConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Message handlerException(Exception e) {
        logger.error("系统响应异常",e);
        return Message.fail("系统内部错误");
    }


}

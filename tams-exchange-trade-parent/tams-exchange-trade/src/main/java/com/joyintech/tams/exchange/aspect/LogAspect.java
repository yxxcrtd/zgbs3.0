package com.joyintech.tams.exchange.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
public class LogAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before(value = "execution(public * com.joyintech.tams.exchange.service.trade.controller..*.*(..))")
    public void doBefore(JoinPoint joinPoint) {
        logger.debug("className [{}] methodName [{}] reqMethod [{}] request for [{}] resulted in [{}] reqArgs [{}]",
                joinPoint.getTarget().getClass().getName(),
                joinPoint.getSignature().getName(),
                getRequest().getMethod(),
                getRequest().getRequestURL(),
                getResponse().getStatus(),
                cn.hutool.json.JSONUtil.toJsonStr(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(public * com.joyintech.tams.exchange.service.trade.controller..*.*(..))", returning = "returnVal")
    public void afterReturn(JoinPoint joinPoint, Object returnVal) {
        logger.debug("className [{}] methodName [{}] return result [{}]",
                joinPoint.getTarget().getClass().getName(),
                joinPoint.getSignature().getName(),
                null);
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }
}

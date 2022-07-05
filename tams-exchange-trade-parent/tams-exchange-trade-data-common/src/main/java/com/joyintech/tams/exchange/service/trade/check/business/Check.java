/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (c) 2012-2019. haiyi Inc.
 * i2-fit All rights reserved.
 */

package com.joyintech.tams.exchange.service.trade.check.business;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;


/**
 * @author lifangpeng
 * @date 2022/4/6 16:43
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Check {

    /**
     * 业务编码 交易通道唯一
     *
     * @return 业务编码
     */
    String[] action();
}

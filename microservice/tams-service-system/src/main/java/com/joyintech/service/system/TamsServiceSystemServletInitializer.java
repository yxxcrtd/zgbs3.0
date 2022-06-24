/*
 * 文件名：ServletInitializer.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年5月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.system;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * war包生成时需要用到此类，如果没有该类，则war包部署后无法启动
 * 
 * @author 何阳阳
 * @version 1.0 2018年5月21日
 * @see TamsServiceSystemServletInitializer
 * @since 1.0
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
public class TamsServiceSystemServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(TamsServiceSystemApplication.class);
        return super.configure(builder);
    }
}

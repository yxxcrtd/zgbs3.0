/*
 * 文件名：TamsInterfApplication.java
 * 版权：Copyright 2019-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：王波
 * 修改时间：2019/5/24 17:04
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.interf.bankname;

import com.joyintech.framework.JoyinApplication;
import com.joyintech.framework.config.Application;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王波
 * @version 1.0 2019年05月24日
 * @see
 * @since 1.0
 */

@ComponentScan(basePackages = "com.joyintech", includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {
        RestController.class}))
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class}, excludeName = {
        "org.activiti.spring.boot.SecurityAutoConfiguration",
        "org.activiti.spring.boot.JpaProcessEngineAutoConfiguration"})
//@EnableEurekaClient
@EnableDiscoveryClient(autoRegister = true)
@SpringBootApplication(exclude= SecurityAutoConfiguration.class)
public class TamsInterfApplication extends Application {

    public static void main(String [] args) {
        JoyinApplication.run(TamsInterfApplication.class, args);
    }
}

package com.joyintech.tams.exchange.quoteterminal;

import com.joyintech.framework.JoyinApplication;
import com.joyintech.framework.config.Application;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

//@SpringBootApplication(scanBasePackages = "com.joyintech.tams.exchange")
@ComponentScan(basePackages = "com.joyintech.tams.exchange", includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {
        RestController.class}))
//@EnableEurekaClient
@EnableDiscoveryClient(autoRegister = true)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class}, excludeName = {
        "org.activiti.spring.boot.SecurityAutoConfiguration",
        "org.activiti.spring.boot.JpaProcessEngineAutoConfiguration"})
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class QuoteTerminalApplication extends Application {


    public static void main(String[] args) {
        JoyinApplication.run(QuoteTerminalApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

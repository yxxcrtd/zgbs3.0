package com.joyintech.tams.exchange;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.joyintech.framework.JoyinApplication;
import com.joyintech.framework.config.WebApplication;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
//@EnableEurekaClient
@EnableDiscoveryClient(autoRegister = true)
public class ExchangeApplication extends WebApplication {
    public static void main(String[] args) {
        JoyinApplication.run(ExchangeApplication.class, args);
    }

//    @Bean
    //为RestTemplate开启客户端的负载均衡
//    @LoadBalanced
//    public JixuRestTemplate getRestTemplate() {
//        return new JixuRestTemplate();
//    }
//
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

}

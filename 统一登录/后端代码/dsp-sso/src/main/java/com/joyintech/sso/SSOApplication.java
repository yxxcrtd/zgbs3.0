package com.joyintech.sso;

import java.io.IOException;

import org.springframework.beans.PropertyValue;
import org.springframework.boot.SpringApplication;
//import org.springframework.boot.bind.PropertySourcesPropertyValues;
//import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import com.joyintech.framework.config.WebApplication;
import com.joyintech.system.SystemApplication;

/**
 * 系统启动类
 */
@ComponentScan(basePackages = "com.joyintech")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class })
public class SSOApplication {

	public static void main(String[] args)  {
//    	initLog();
		System.setProperty(LoggingSystem.SYSTEM_PROPERTY, "com.joyintech.framework.logging.logback.JoyinLogbackLoggingSystem");
		try {
			initSpring(args);
		}catch(Throwable e){
			e.printStackTrace();
		}
    }

	private static ConfigurableApplicationContext initSpring(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SystemApplication.class, args);
		return context;
	}
//
//    /**
//     * 启动类兼容资管0.0.3框架
//     */
//    private static void initLog() {
////        PropertySourcesLoader propertiesLoader = new PropertySourcesLoader();
////        ResourceLoader resourceLoader = new DefaultResourceLoader();
////        Resource resource = resourceLoader.getResource("classpath:/application-framework.yml");
////        try {
////        	PropertySource<?> propertySource = propertiesLoader.load(resource, null, "application", null);
////        	MutablePropertySources propertySources = new MutablePropertySources();
////        	propertySources.addFirst(propertySource);
////        	PropertySourcesPropertyValues values = new PropertySourcesPropertyValues(propertySources, false);
////        	PropertyValue value = values.getPropertyValue("log.path");
////        	if (null != value) {
////        		System.setProperty("logfolder", (String)value.getValue());
////        	} else {
////        		System.setProperty("logfolder", "logs");
////        	}
////        } catch (IOException e) {
////        	throw new RuntimeException(e);
////        }
//    }
//
}
package com.joyintech.tams.system;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;

import org.springframework.beans.PropertyValue;
import org.springframework.boot.SpringApplication;
//import org.springframework.boot.bind.PropertySourcesPropertyValues;
//import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.joyintech.framework.config.WebApplication;
import com.joyintech.system.SystemApplication;

import springfox.documentation.spring.web.plugins.Docket;

/**
 * 系统启动类
 */
@Configuration
public class TamsSystemApplication extends WebApplication {

	@Bean
	public Docket dataCreditProtectionCertificatePeriod() {
		return SwaggerConfig.createDocket("信用凭证保护存续期", "com.joyintech.tams.exchange.period.creditprotectioncertificate");
	}

	@Bean
	public Docket dataBusinessCommonDocket() {
		return SwaggerConfig.createDocket("通用接口", "com.joyintech.tams.business.common.controller");
	}

	@Bean
	public Docket dataEtrexchangetradeDocket() {
		return SwaggerConfig.createDocket("场内交易", "com.joyintech.tams.exchange.entrust.etrexchange");

	}

	@Bean
	public Docket dataTradeInfoDocket() {
		return SwaggerConfig.createDocket("交易商信息维护", "com.joyintech.tams.exchange.basicdata.bussiness.traderinfo");

	}


	public static void main(String[] args) throws Exception {
//    	initLog();
    	initSpring(args);
//    	initConfigData();
		printSystemInfo();
    }
    
//    private static void initConfigData() {
//		DataCoreLifecycleManager core = SpringUtil.getBean(DataCoreLifecycleManager.class);
//    	core.init();
//    	DataRouterLifecycleManager router = SpringUtil.getBean(DataRouterLifecycleManager.class);
//    	router.init();
//	}

	private static void printSystemInfo() throws UnknownHostException {
		String port = SpringUtil.getEnvironment().getProperty("server.port");
		String contextPath = SpringUtil.getEnvironment().getProperty("server.context-path");
		String ip = InetAddress.getLocalHost().getHostAddress();
		String banner = "\r\n########################################################################################################\r\n" + 
				        "                                                                                                      \r\n" + 
				        "   ____        _          ____                  _            ____                 _     _             \r\n" + 
				        "  |  _ \\  __ _| |_ __ _  / ___|  ___ _ ____   _(_) ___ ___  |  _ \\ _ __ _____   _(_) __| | ___ _ __   \r\n" + 
				        "  | | | |/ _` | __/ _` | \\___ \\ / _ \\ '__\\ \\ / / |/ __/ _ \\ | |_) | '__/ _ \\ \\ / / |/ _` |/ _ \\ '__|  \r\n" + 
				        "  | |_| | (_| | || (_| |  ___) |  __/ |   \\ V /| | (_|  __/ |  __/| | | (_) \\ V /| | (_| |  __/ |     \r\n" + 
				        "  |____/ \\__,_|\\__\\__,_| |____/ \\___|_|    \\_/ |_|\\___\\___| |_|   |_|  \\___/ \\_/ |_|\\__,_|\\___|_|     \r\n" + 
				        "                                                                                                      \r\n" + 
				        "                                                                                                      \r\n" + 
				        "                                    Joyin Data Service系统  V1.0.0                                     \r\n" + 
				        "                                                                                                      ";
		System.out.println(banner);
		System.out.println(MessageFormat.format("  系统访问地址 ->  http://{0}:{1}{2}/auth/login", ip, port,contextPath));
//		System.out.println(MessageFormat.format(" Druid监控地址 ->  http://{0}:{1}{2}/druid/login.html", ip, port,contextPath));
    	System.out.println(MessageFormat.format("   Swagger地址 ->  http://{0}:{1}{2}/swagger-ui.html",ip, port,contextPath));
    	System.out.println("########################################################################################################\r\n");
    	
    	try {
        	String os = System.getProperty("os.name").toLowerCase();
        	if(os.contains("windows")) {
        		Runtime.getRuntime().exec("cmd   /c   start   "+MessageFormat.format("http://{0}:{1}{2}/auth/login", ip, port,contextPath));
        	}
        } catch (Exception ex) {
        }
	}

	private static ConfigurableApplicationContext initSpring(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SystemApplication.class, args);
    	SpringUtil.setContext(context);
    	SpringUtil.setEnvironment(context.getEnvironment());
		return context;
	}
//
//    /**
//     * 启动类兼容资管0.0.3框架
//     */
//    private static void initLog() {
//        PropertySourcesLoader propertiesLoader = new PropertySourcesLoader();
//        ResourceLoader resourceLoader = new DefaultResourceLoader();
//        Resource resource = resourceLoader.getResource("classpath:/application-framework.yml");
//        try {
//        	PropertySource<?> propertySource = propertiesLoader.load(resource, null, "application", null);
//        	MutablePropertySources propertySources = new MutablePropertySources();
//        	propertySources.addFirst(propertySource);
//        	PropertySourcesPropertyValues values = new PropertySourcesPropertyValues(propertySources, false);
//        	PropertyValue value = values.getPropertyValue("log.path");
//        	if (null != value) {
//        		System.setProperty("logfolder", (String)value.getValue());
//        	} else {
//        		System.setProperty("logfolder", "logs");
//        	}
//        } catch (IOException e) {
//        	throw new RuntimeException(e);
//        }
//    }
//    
}
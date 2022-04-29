package com.joyintech.tams.system;

import com.joyintech.framework.config.WebApplication;
import com.joyintech.system.SystemApplication;
import org.springframework.beans.PropertyValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.bind.PropertySourcesPropertyValues;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;

/**
 * 系统启动类
 */
@Configuration
public class TamsSystemApplication extends WebApplication {

	@Bean
	public Docket dataEntrustDocket() {
		return SwaggerConfig.createDocket("场内指令接口", "com.joyintech.tams.exchange.entrust.etrexchange");
	}

	@Bean
	public Docket dataFieldetrinfoquerymainDocket() {
		return SwaggerConfig.createDocket("场内指令分发查询接口", "com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain");
	}

	@Bean
	public Docket dataExchangeDocket() {
		return SwaggerConfig.createDocket("场内交易接口", "com.joyintech.tams.exchange");
	}

	@Bean
	public Docket dataCfetsDocket() {
		return SwaggerConfig.createDocket("指令管理-银行间指令", "com.joyintech.tams.cfets.etrib");
	}
	
	@Bean
	public Docket dataBusinessCommonDocket() {
		return SwaggerConfig.createDocket("通用接口", "com.joyintech.tams.business.common.controller");
	}

	@Bean
	public Docket dataCfetsEtrDispatcherDocket() {
		return SwaggerConfig.createDocket("指令分发", "com.joyintech.tams.cfets.etrdispatcher.controller");
	}

	@Bean
	public Docket dataBankuplinkDocket() {
		return SwaggerConfig.createDocket("本币业务交易", "com.joyintech.tams.cfets.bankuplink.bond.controller");
	}

	@Bean
	public Docket dataBankuplinkRepoDocket() {
		return SwaggerConfig.createDocket("本币业务交易-质押式回购", "com.joyintech.tams.cfets.bankuplink.repo.controller");
	}

	@Bean
	public Docket dataCfetsManualSettleDocket() {
		return SwaggerConfig.createDocket("手工交收", "com.joyintech.tams.cfets.cfetsmanualsettle.controller");
	}

	@Bean
	public Docket dataCfetsEtrInfoQuerySettleDocket() {
		return SwaggerConfig.createDocket("银行间指令查询", "com.joyintech.tams.cfets.cfetsetrinfoquery.controller");
	}

	@Bean
	public Docket dataCfetsTrdConfirm() {
		return SwaggerConfig.createDocket("银行间交易成交确认", "com.joyintech.tams.cfets.trdconfirm.controller");
	}
	@Bean
	public Docket dataCfetsPosition() {
		return SwaggerConfig.createDocket("头寸相关查询", "com.joyintech.tams.cfets.position.controller");
	}
	@Bean
	public Docket dataCfetsTrdQuery() {
		return SwaggerConfig.createDocket("银行间交易查询", "com.joyintech.tams.cfets.trdquery.controller");
	}
	@Bean
	public Docket dataCfetsTrdRedeem() {
		return SwaggerConfig.createDocket("赎回登记", "com.joyintech.tams.cfets.trdredeem.controller");
	}

	@Bean
	public Docket dataAssetInfo() {
		return SwaggerConfig.createDocket("债券标的", "com.joyintech.tams.business.asset.bond");
	}

	@Bean
	public Docket datatenderbond() {
		return SwaggerConfig.createDocket("新债标的", "com.joyintech.tams.business.asset.tenderbond");
	}

	@Bean
	public Docket tenderBond() {
		return SwaggerConfig.createDocket("一级投标确认", "com.joyintech.tams.cfets.trd.firstbidfirm");
	}

	@Bean
	public Docket bondFirstBid() {
		return SwaggerConfig.createDocket("一级投标指令", "com.joyintech.tams.cfets.etr.bondfirstbid");
	}

	@Bean
	public Docket etrBond() {
		return SwaggerConfig.createDocket("本币业务指令-债券", "com.joyintech.tams.cfets.etr.bond");
	}

	@Bean
	public Docket bondMarketInfoQuery() {
		return SwaggerConfig.createDocket("债券市场信息查询", "com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery");
	}


	@Bean
	public Docket commonBondMarketInfoQuery() {
		return SwaggerConfig.createDocket("公共债券信息查询", "com.joyintech.tams.business.common");
	}

	public static void main(String[] args) throws Exception {
    	initLog();
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

    /**
     * 启动类兼容资管0.0.3框架
     */
    private static void initLog() {
        PropertySourcesLoader propertiesLoader = new PropertySourcesLoader();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:/application-framework.yml");
        try {
        	PropertySource<?> propertySource = propertiesLoader.load(resource, null, "application", null);
        	MutablePropertySources propertySources = new MutablePropertySources();
        	propertySources.addFirst(propertySource);
        	PropertySourcesPropertyValues values = new PropertySourcesPropertyValues(propertySources, false);
        	PropertyValue value = values.getPropertyValue("log.path");
        	if (null != value) {
        		System.setProperty("logfolder", (String)value.getValue());
        	} else {
        		System.setProperty("logfolder", "logs");
        	}
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }
    
}
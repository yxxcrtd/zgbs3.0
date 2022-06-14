package com.joyintech.jixu;

import java.sql.SQLException;
import java.util.Properties;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import com.alibaba.druid.pool.DruidDataSource;
import com.jixu.common.tool.JixuInterceptor;
import com.jixu.common.tool.MyJobFactory;
import com.jixu.common.type.UserTmpID;
import com.jixu.common.util.DBTypeSelect;
import com.jixu.common.util.JixuDataSourcePoolProp;

/**
 * ServletComponentScan	解决了springboot启动后,hibernate懒加载失败的问题(could not initialize proxy, no session)
 * @author 桃汰猪
 *
 */

@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
@EnableTransactionManagement
@ServletComponentScan(basePackages="com.jixu.common.tool")
@ComponentScan(basePackages = {"com.jixu","com.zhijun"}, useDefaultFilters = false, includeFilters = {
	    @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class,
	            Service.class, Component.class, Repository.class})})
public class JixuMvcConfig extends WebMvcConfigurerAdapter{
	
	//private static Logger logger = Logger.getLogger(JixuBeanConfig.class);
	
	@Value("${jixu.project.group:Interface-Auto}")
	private String quartzProjectGroup;
	
	@Value("${jixu.user.type}")
	private int userType;
	
	@Autowired
//	private BasicDataSource masterDataSource;
	private DruidDataSource masterDataSource;
	
	@Autowired
	private JixuDataSourcePoolProp jixuDataSourcePoolProp;
	
	@Bean
    public LocalSessionFactoryBean jixuSessionFactory()   {
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        //初始化数据库连接池参数
        initDataSource();
        
        String dialect="org.hibernate.dialect.OracleDialect";
        String tmpURL= masterDataSource.getUrl().toLowerCase();
        if(tmpURL.contains("oracle")) {
        	dialect="org.hibernate.dialect.OracleDialect";
        	DBTypeSelect.curDBType=DBTypeSelect.oracle;
        }else if(tmpURL.contains("db2")) {
        	dialect="org.hibernate.dialect.DB2Dialect";
        	DBTypeSelect.curDBType=DBTypeSelect.db2;
        }else if(tmpURL.contains("mysql")) {
        	dialect="org.hibernate.dialect.MySQLDialect";
        	DBTypeSelect.curDBType=DBTypeSelect.mysql;
        }
        
        localSessionFactoryBean.setDataSource(masterDataSource);
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql","true");
        properties.setProperty("hibernate.jdbc.fetch_size","80");
        properties.setProperty("hibernate.jdbc.batch_size","50");
        properties.setProperty("hibernate.jdbc.use_streams_for_binary","true");
        properties.setProperty("hibernate.id.new_generator_mappings","true");
        properties.setProperty("hibernate.allow_update_outside_transaction","true");
        properties.setProperty("hibernate.connection.release_mode","after_transaction");
        properties.setProperty("hibernate.connection.handling_mode","DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION");
        //properties.setProperty("hibernate.default_schema",source.getUsername());
        //properties.setProperty("hibernate.hbm2ddl.auto","update");
        localSessionFactoryBean.setHibernateProperties(properties);
        localSessionFactoryBean.setPackagesToScan("com.orm.obj","com.jixu.common.pojo","com.jixu.framework.report.pojo","com.zhujun.pojo");
        //ResourceLoader resourceLoader = new DefaultResourceLoader();
        //Resource resource = resourceLoader.getResource("resource/");
        //localSessionFactoryBean.setMappingDirectoryLocations(resource);
        //设置拦截器
        //localSessionFactoryBean.setEntityInterceptor(this.auditInterceptor());
        return localSessionFactoryBean;
    }
	
	/*@Bean
    public LocalSessionFactoryBean targetSessionFactory()   {
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(jixuDataSource());
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect","org.hibernate.dialect.OracleDialect");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql","true");
        properties.setProperty("hibernate.jdbc.fetch_size","80");
        properties.setProperty("hibernate.jdbc.batch_size","50");
        properties.setProperty("hibernate.default_schema","jixu");
        properties.setProperty("hibernate.id.new_generator_mappings","true");
        properties.setProperty("hibernate.hbm2ddl.auto","update");
        localSessionFactoryBean.setHibernateProperties(properties);
        return localSessionFactoryBean;
    }*/

//	@Bean
//    public DriverManagerDataSource jixuDataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
//        dataSource.setUrl("jdbc:oracle:thin:@//localhost:1521/jixu");
//        dataSource.setUsername("jixuadapter");
//        dataSource.setPassword("jixuadapter");
//        
//        return dataSource;
//    }
 
    //txManager事务开启
    @Bean
    public HibernateTransactionManager jixuTXManager() throws SQLException {
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(jixuSessionFactory().getObject());
        return hibernateTransactionManager;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(){
    	SchedulerFactoryBean bean=new SchedulerFactoryBean();
    	//Quartz集群配置
    	//bean.setDataSource(jdbcTemplate.getDataSource());
    	ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:jixu-quartz.properties");
        bean.setConfigLocation(resource);
        if(quartzProjectGroup==null || quartzProjectGroup.equals(""))
        	quartzProjectGroup="schedulerFactoryBean";
    	bean.setSchedulerName(quartzProjectGroup);
    	bean.setOverwriteExistingJobs(true);
    	bean.setJobFactory(jobFactory());
    	return bean;
    }
    
    @Bean
    public JobFactory jobFactory(){
    	MyJobFactory jobFactory=new MyJobFactory();
    	return jobFactory;
    }
    
    @Bean
    public ThreadPoolTaskExecutor taskExecutor(){
    	ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
    	executor.setCorePoolSize(50);
    	executor.setMaxPoolSize(80);
    	executor.setQueueCapacity(1000);
    	executor.setKeepAliveSeconds(300);
    	executor.setWaitForTasksToCompleteOnShutdown(true);
    	return executor;
    }
    
    /*@Bean
    public PropertiesFactoryBean propCfg(){
    	PropertiesFactoryBean bean=new PropertiesFactoryBean();
    	
    	ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:jixucfg.properties");
    	bean.setLocation(resource);
    	bean.setFileEncoding("UTF-8");
    	return bean;
    }*/
    
    /*@Bean
    public ViewResolver viewResolver() {
    	InternalResourceViewResolver viewResolver=new InternalResourceViewResolver();
    	viewResolver.setOrder(1);
    	viewResolver.setCache(true);
        viewResolver.setPrefix("classpath:/templates/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setContentType("text/html; charset=UTF-8");
        viewResolver.setExposeContextBeansAsAttributes(true);
    	
    	return viewResolver;
    }*/
    
    /*@Bean
    public ViewResolver viewResolver(SpringTemplateEngine springTemplateEngine){
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(springTemplateEngine);
        return resolver;
    }

    *//**
     * 模板引擎 —— 处理模板并渲染结果
     *
     * @param templateResolver 模板解析器
     * @return
     *//*
    @Bean
    public SpringTemplateEngine springTemplateEngine(ITemplateResolver templateResolver) {
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.setTemplateResolver(templateResolver);
        return springTemplateEngine;
    }

    *//**
     * 模板解析器 —— 加载 Thymeleaf 模板
     *
     * @return
     *//*
    @Bean
    public ITemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setOrder(1);
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".jsp");
        templateResolver.setTemplateMode("LEGACYHTML5");
        return templateResolver;
    }*/

    
    /**
     * 静态资源配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // weblogic 12c中resources/common的目录作为静态资源访问有问题，改为public
        registry.addResourceHandler("/public/**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }
    
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }
    
    
    @Bean
    public JixuInterceptor getJixuInterceptor(){
        return new JixuInterceptor();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	JixuInterceptor jixuInterceptor=getJixuInterceptor();
    	jixuInterceptor.setLoginFailedURL("jixulogin.do");
    	registry.addInterceptor(jixuInterceptor).addPathPatterns("/**/*.do")
    		.excludePathPatterns("/**/jixulogin.do")
    		.excludePathPatterns("/**/authenticate.do");
    }
    
    /**
     * 临时解决errorPageFilter
     * If your application is running on WebSphere Application Server you may be able to resolve 
     * this problem by setting com.ibm.ws.webcontainer.invokeFlushAfterService to false
     * @return
     */
//    @Bean
//    public ErrorPageFilter errorPageFilter() {
//         return new ErrorPageFilter();
//    }
//
//    @Bean
//    public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter(filter);
//        filterRegistrationBean.setEnabled(false);
//        return filterRegistrationBean;
//    }
//
    @Bean
    public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
        OpenSessionInViewInterceptor interceptor = new OpenSessionInViewInterceptor();
        interceptor.setSessionFactory(jixuSessionFactory().getObject());
        return interceptor;
    }
    
    @Bean
    public SimpleUrlHandlerMapping urlMapping(){
    	SimpleUrlHandlerMapping handler=new SimpleUrlHandlerMapping();
    	handler.setInterceptors(openSessionInViewInterceptor());
    	return handler;
    }
    
    @Primary
    @Bean("jixuCacheManager")
    public CacheManager cacheManager(EhCacheManagerFactoryBean cacheFactory) {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(cacheFactory.getObject());
        return cacheManager;
    }
    
    @Bean
    public EhCacheManagerFactoryBean cacheFactory() {
        EhCacheManagerFactoryBean cacheFactory = new EhCacheManagerFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        cacheFactory.setConfigLocation(resolver.getResource("classpath:jixu-ehcache.xml"));
        return cacheFactory;
    }
    
    private void initDataSource() {
    	//被joyin调用，直接采用joyin的配置
    	//jixu独立运行，初始化数据库连接池
    	if(userType==UserTmpID.userJixuType) {
	        masterDataSource.setValidationQuery(jixuDataSourcePoolProp.getValidationQuery());
	        masterDataSource.setInitialSize(jixuDataSourcePoolProp.getInitialSize());
	        
//	        masterDataSource.setMaxTotal(jixuDataSourcePoolProp.getMaxTotal());
	        masterDataSource.setMaxActive(jixuDataSourcePoolProp.getMaxTotal());
	        
	        masterDataSource.setMaxIdle(jixuDataSourcePoolProp.getMaxIdle());
	        masterDataSource.setMinIdle(jixuDataSourcePoolProp.getMinIdle());
	        
//	        masterDataSource.setMaxWaitMillis(jixuDataSourcePoolProp.getMaxWaitMillis());
	        masterDataSource.setMaxWait(jixuDataSourcePoolProp.getMaxWaitMillis());
	        
	        masterDataSource.setDefaultAutoCommit(jixuDataSourcePoolProp.getDefaultAutoCommit());
	        masterDataSource.setDefaultReadOnly(jixuDataSourcePoolProp.getDefaultReadOnly());
	        masterDataSource.setDefaultTransactionIsolation(jixuDataSourcePoolProp.getDefaultTransactionIsolation());
	        masterDataSource.setMinEvictableIdleTimeMillis(jixuDataSourcePoolProp.getMinEvictableIdleTimeMillis());
	        masterDataSource.setPoolPreparedStatements(jixuDataSourcePoolProp.isPoolPreparedStatements());
	        masterDataSource.setMaxOpenPreparedStatements(jixuDataSourcePoolProp.getMaxOpenPreparedStatements());
	        masterDataSource.setRemoveAbandonedTimeout(jixuDataSourcePoolProp.getRemoveAbandonedTimeout());
	        
//	        masterDataSource.setRemoveAbandonedOnBorrow(jixuDataSourcePoolProp.isRemoveAbandonedOnBorrow());
//	        masterDataSource.setRemoveAbandonedOnMaintenance(jixuDataSourcePoolProp.isRemoveAbandonedOnMaintenance());
	        masterDataSource.setRemoveAbandoned(jixuDataSourcePoolProp.isRemoveAbandonedOnMaintenance());
	        
	        masterDataSource.setTimeBetweenEvictionRunsMillis(jixuDataSourcePoolProp.getTimeBetweenEvictionRunsMillis());
	        masterDataSource.setNumTestsPerEvictionRun(jixuDataSourcePoolProp.getNumTestsPerEvictionRun());
	        masterDataSource.setTestOnBorrow(jixuDataSourcePoolProp.isTestOnBorrow());
	        masterDataSource.setTestOnReturn(jixuDataSourcePoolProp.isTestOnReturn());
	        
//	        masterDataSource.setTestOnCreate(jixuDataSourcePoolProp.isTestOnCreate());
	        masterDataSource.setTestOnBorrow(jixuDataSourcePoolProp.isTestOnCreate());
	        
	        masterDataSource.setLogAbandoned(jixuDataSourcePoolProp.isLogAbandoned());
        }
    }
}

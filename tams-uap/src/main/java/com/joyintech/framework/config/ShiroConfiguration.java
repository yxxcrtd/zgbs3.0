/*
 * 文件名：ShiroConfiguration.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改时间：2018年1月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.framework.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.joyintech.framework.jwt.JWTFilter;
import com.joyintech.framework.jwt.JWTRealm;
import com.joyintech.framework.security.*;
import com.joyintech.framework.security.impl.DefaultUserCredentialsServiceImpl;
import com.joyintech.framework.security.session.DBSessionDao;
import com.joyintech.framework.security.session.EnterpriseCacheSessionDao;
import com.joyintech.framework.security.session.dao.SessionDaoMapper;
import com.joyintech.framework.sso.SSOCookieOperateor;
import com.joyintech.framework.sso.SSOPermissionsAuthorizationFilter;
import com.joyintech.framework.util.StringUtil;
import io.buji.pac4j.filter.CallbackFilter;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AbstractAuthenticator;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类负责shiro和cas配置<br>
 * Shiro 配置 Apache Shiro 核心通过 Filter 来实现，就好像SpringMvc 通过DispachServlet 来主控制一样。 既然是使用 Filter
 * 一般也就能猜到，是通过URL规则来进行过滤和权限校验，所以我们需要定义一系列关于URL的规则和访问权限。
 *
 * @version 1.0 2018年1月18日
 * @see ShiroConfiguration
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(value = "shiro.enable", havingValue = "true")
@EnableConfigurationProperties(ShiroProperties.class)
@AutoConfigureAfter(ShiroCacheConfiguration.class)
public class ShiroConfiguration {

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);

    /**
     * ShiroProperties
     */
    @Autowired
    private ShiroProperties shiroProperties;

    /**
     * 从Cookie值里取出SSOUSERNAME的处理接口
     */
    @Autowired(required = false)
    private SSOCookieOperateor ssoCookieOpr;

    /**
     * UserService
     */
    @Autowired(required = false)
    private AuthorizationService userService;

    /**
     * 服务器端口号
     */
    @Value("${server.port}")
    private String serverPort;

    /**
     * 服务器端口号
     */
    @Value("${application.sessionTimeout}")
    private long sessionTimeout;

    /**
     * 服务器地址
     */
    @Value("${server.address}")
    private String serverAddress;

    /**
     * 服务器是否启用ssl
     */
    @Value("${server.ssl.enabled}")
    private boolean sslEnabled;

    /**
     * 项目根路径
     */
    @Value("${server.contextPath}")
    private String serverContextPath;

    @Configuration
    @ConditionalOnClass(name = "org.thymeleaf.dialect.AbstractProcessorDialect")
    static class ShiroThymeleafDialect {

        /**
         * thymeleaf使用shiro方言
         * 
         * @return ShiroDialect
         */
        @Bean
        @ConditionalOnMissingBean
        public ShiroDialect shiroDialect() {
            if (logger.isDebugEnabled()) {
                logger.debug("初始化ShiroDialect");
            }
            return new ShiroDialect();
        }
    }

    /**
     * 配置ShiroFilterFactoryBean<br>
     * 1、一个URL可以配置多个Filter，使用逗号分隔 <br>
     * 2、当设置多个过滤器时，全部验证通过，才视为通过 <br>
     * 3、部分过滤器可指定参数，如perms，roles <br>
     * 
     * @param securityManager SecurityManager
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, SessionDaoMapper sessionMapper) {
        if (logger.isDebugEnabled()) {
            logger.debug("初始化ShiroFilterFactoryBean");
        }
        ShiroFilterFactoryBean filterBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        filterBean.setSecurityManager(securityManager);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        filterBean.setLoginUrl(getLoginUrl());
        // 登录成功后要跳转的链接
        filterBean.setSuccessUrl(shiroProperties.getLoginSuccessUrl());
        filterBean.getFilters().put("authc", new JoyinFormAuthenticationFilter(sessionMapper, shiroProperties));
        // 未授权界面
        filterBean.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());
        if (shiroProperties.isCasEnable()) {
            // 添加casFilter到shiroFilter中
            filterBean.getFilters().put("casFilter", callbackFilter());
        }
        else if (shiroProperties.isSsoEnable()) {
            SSOPermissionsAuthorizationFilter ssoFilter = new SSOPermissionsAuthorizationFilter(sessionMapper, shiroProperties);
            ssoFilter.setSsoCookieOpr(ssoCookieOpr);
            ssoFilter.setUserService(userService);
            filterBean.getFilters().put("ssoFilter", ssoFilter);
        }
        if (shiroProperties.isJwtEnable()) {
            // 添加casFilter到shiroFilter中
            filterBean.getFilters().put("jwtFilter", new JWTFilter(securityManager, shiroProperties));
        }
        if (shiroProperties.isIhsEnable()) {
            // 开启IHS负载
            filterBean.getFilters().put("authc", new JoyinFormAuthenticationFilter(sessionMapper, shiroProperties));
            filterBean.getFilters().put("logout", new JoyinLogoutFilter(sessionMapper, shiroProperties));
        }
        // 拦截器.
        loadShiroFilterChain(filterBean);
        return filterBean;
    }

    /**
     * 配置SecurityManager
     * 
     * @param shiroRealm AuthorizingRealm
     * @param cacheManager CacheManager
     * @param sessionManager SessionManager
     * @return SecurityManager
     */
    @Bean
    public SecurityManager securityManager(AuthorizingRealm shiroRealm, CacheManager cacheManager,
                                           SessionManager sessionManager, @Autowired(required=false) List<AuthenticationListener> authenticationListeners) {
        if (logger.isDebugEnabled()) {
            logger.debug("初始化SecurityManager");
        }
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        securityManager.setCacheManager(cacheManager);
        securityManager.setSessionManager(sessionManager);
        Authenticator authenticator = securityManager.getAuthenticator();
        if (authenticator instanceof AbstractAuthenticator) {
            ((AbstractAuthenticator)authenticator).setAuthenticationListeners(authenticationListeners);
        }
        if (shiroProperties.isCasEnable()) {
            securityManager.setSubjectFactory(subjectFactory());
        }
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    /**
     * @return Config
     */
    @Bean
    public Config config() {
        // CAS
        CasClient casClient = casClient();
        final Clients clients = new Clients(
            getShiroServerUrlPrefix() + shiroProperties.getCasFilterUrlPattern() + "?client_name="
                                            + shiroProperties.getCasClientName(),
            casClient);
        // 默认casClient
        clients.setDefaultClient(casClient);
        return new Config(clients);
    }

    /**
     * CasClient
     * 
     * @return CasClient
     */
    public CasClient casClient() {
        String shiroServerUrlPrefix = getShiroServerUrlPrefix();
        final CasConfiguration configuration = new CasConfiguration(
            shiroProperties.getCasServerUrlPrefix() + shiroProperties.getCasLoginUrl(),
            shiroProperties.getCasServerUrlPrefix());
        configuration.setAcceptAnyProxy(true);
        CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(shiroServerUrlPrefix + shiroProperties.getCasFilterUrlPattern()
                                 + "?client_name=" + shiroProperties.getCasClientName());
        casClient.setName(shiroProperties.getCasClientName());
        return casClient;
    }

    /**
     * 配置CallbackFilter
     * 
     * @return CallbackFilter
     */
    public CallbackFilter callbackFilter() {
        CallbackFilter filter = new CallbackFilter();
        filter.setConfig(config());
        filter.setDefaultUrl(shiroProperties.getLoginSuccessUrl());
        return filter;
    }

    @Bean
    @ConditionalOnMissingBean(UserCredentialsService.class)
    public UserCredentialsService userCredentialsService() {
        return new DefaultUserCredentialsServiceImpl();
    }

    /**
     * 配置SubjectFactory
     * 
     * @return Pac4jSubjectFactory
     */
    @Bean(name = "subjectFactory")
    public Pac4jSubjectFactory subjectFactory() {
        return new Pac4jSubjectFactory();
    }

    /**
     * 配置 SessionManager
     * 
     * @param sessionDao SessionDAO
     * @return SessionManager
     */
    @Bean
    public SessionManager sessionManager(SessionDAO sessionDao,
                                         @Autowired(required = false) List<SessionListener> sessionListener) {
        Cookie cookie = new SimpleCookie(shiroProperties.getSessionId());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        // session监听
        WebSessionManager sessionManager = new WebSessionManager(shiroProperties);
        // session超时时间，单位为毫秒
        sessionManager.setGlobalSessionTimeout(sessionTimeout);
        // 删除失效的session
        sessionManager.setDeleteInvalidSessions(true);
        // 激活session校验任务
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookie(cookie);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionListeners(sessionListener);
        sessionManager.setSessionDAO(sessionDao);
        if (logger.isDebugEnabled()) {
            logger.debug("初始化SessionManager sessionid:{}", shiroProperties.getSessionId());
        }
        return sessionManager;
    }

    /**
     * @param cacheManager CacheManager
     * @return CachingSessionDAO
     */
    @Bean
    public SessionDAO sessionDao(CacheManager cacheManager, SessionDaoMapper ssessionMapper) {
        String storage = shiroProperties.getSessionStorage();
        if (StringUtil.equalsIgnoreCase(storage, "cache")) {
            CachingSessionDAO cacheSessionDAO = new EnterpriseCacheSessionDao();
            cacheSessionDAO.setCacheManager(cacheManager);
            return cacheSessionDAO;
        }
        if(StringUtil.equalsIgnoreCase(storage, "database")) {
            DBSessionDao dbSessionDao = new DBSessionDao(ssessionMapper);
            dbSessionDao.setCacheManager(cacheManager);
            return dbSessionDao;
        }
        return new MemorySessionDAO();
    }

    /**
     * 配置AuthShiroRealm
     * 
     * @param matcher CredentialsMatcher
     * @return AuthShiroRealm
     */
    @Bean
    public AuthorizingRealm shiroRealm(CredentialsMatcher matcher) {
        if (logger.isDebugEnabled()) {
            logger.debug("初始化AuthShiroRealm");
        }
        AuthorizingRealm realm = null;
        if (shiroProperties.isCasEnable()) {
            realm = new Pac4jAuthShiroRealm();
        }
        else if (shiroProperties.isJwtEnable()) {
            realm = new JWTRealm();
        }
        else {
            realm = new AuthShiroRealm();
        }
        realm.setCredentialsMatcher(matcher);
        return realm;
    }

    /**
     * 凭证匹配器 由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了所以我们需要修改下doGetAuthenticationInfo中的代码;
     * 
     * @param cacheManager CacheManager
     * @return RetryLimitHashedCredentialsMatcher
     */
    @Bean
    @ConditionalOnMissingBean(CredentialsMatcher.class)
    public RetryLimitHashedCredentialsMatcher hashedCredentialsMatcher(CacheManager cacheManager, UserCredentialsService userCredentialsService) {
        if (logger.isDebugEnabled()) {
            logger.debug("初始化CredentialsMatcher");
        }
        RetryLimitHashedCredentialsMatcher hashedCredentialsMatcher = new RetryLimitHashedCredentialsMatcher(
            cacheManager);
        // 散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName(PasswordHelper.ALGORITHM_NAME);
        // 散列的次数，比如散列两次，相当于 md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(PasswordHelper.HASH_ITERATIONS);
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        hashedCredentialsMatcher.setUserCredentialsService(userCredentialsService);
        return hashedCredentialsMatcher;
    }
    //
    // /**
    // * 配置LifecycleBeanPostProcessor
    // *
    // * @return LifecycleBeanPostProcessor
    // */
    // @Bean
    // public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
    // return new LifecycleBeanPostProcessor();
    // }

    /**
     * 注册单点登出listener
     * 
     * @return ServletListenerRegistrationBean
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // 优先级需要高于Cas的Filter
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new SingleSignOutHttpSessionListener());
        bean.setEnabled(true);
        return bean;
    }

    /**
     * 注册单点登出filter
     * 
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setName("singleSignOutFilter");
        bean.setFilter(new SingleSignOutFilter());
        bean.addUrlPatterns("/logout");
        bean.setEnabled(true);
        bean.setEnabled(shiroProperties.isCasEnable());
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerUrlPrefix", shiroProperties.getCasServerUrlPrefix());
        bean.setInitParameters(initParameters);
        return bean;
    }

    /**
     * 加载shiroFilter权限控制规则
     * 
     * @param shiroFilter ShiroFilterFactoryBean
     */
    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilter) {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

        // authc：该过滤器下的页面必须登录后才能访问，它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
        // anon: 可以理解为不拦截
        // user: 首先是判断是不是登录页面，如果是的话，就是allowd，如果不是,如果之前登录过，并且isRememberMe=true。则可访问，但authc的不能
        // roles["admin"] 用户拥有admin角色
        // perms["permission1"] 用户拥有permission1权限
        // filter顺序按照定义顺序匹配，匹配到就验证，验证完毕结束。
        // url匹配通配符支持：? * **,分别表示匹配1个，匹配0-n个（不含子路径），匹配下级所有路径

        // 1.shiro集成cas后，首先添加该规则
        if (shiroProperties.isCasEnable()) {
            filterChainDefinitionMap.put(shiroProperties.getCasFilterUrlPattern(), "casFilter");
        }
        else {
            // logut请求采用logout filter-------------------自己实现logout
//            filterChainDefinitionMap.put(shiroProperties.getLogoutUrl(), "logout");
        }

        // 2.不拦截的请求
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/error/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/image/**", "anon");
        filterChainDefinitionMap.put("/public/**", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/websocket/**", "anon");
        filterChainDefinitionMap.put("/api/**", "anon");
        filterChainDefinitionMap.put("/workflow/**", "anon");
        filterChainDefinitionMap.put("/main/about/refreshCache", "anon");
        if (!CollectionUtils.isEmpty(shiroProperties.getAllowUrl())) {
            shiroProperties.getAllowUrl().forEach(allowUrl -> {
                filterChainDefinitionMap.put(allowUrl, "anon");
            });
        }
        if (!filterChainDefinitionMap.containsKey("/**")) {
            // 3.拦截的请求（从本地数据库获取或者从casserver获取(webservice,http等远程方式)，看你的角色权限配置在哪里）
            if (shiroProperties.isSsoEnable()) {
                filterChainDefinitionMap.put("/**", "ssoFilter"); // 需要登录
            }
            else if (shiroProperties.isJwtEnable()) {
                filterChainDefinitionMap.put("/**", "jwtFilter"); // 需要登录
            }
            else {
                filterChainDefinitionMap.put("/**", "authc"); // 需要登录
            }
        }

        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
    }

    /**
     * 当前server的访问前缀
     * 
     * @return ShiroServerUrlPrefix
     */
    private String getShiroServerUrlPrefix() {
        StringBuilder shiroServerUrlPrefix = new StringBuilder();
        if (sslEnabled) {
            shiroServerUrlPrefix.append("https://");
        }
        else {
            shiroServerUrlPrefix.append("http://");
        }
        if (serverAddress != null) {
            shiroServerUrlPrefix.append(serverAddress);
        }
        else {
            shiroServerUrlPrefix.append("localhost");
        }
        if (serverPort != null) {
            shiroServerUrlPrefix.append(":" + serverPort);
        }
        if (serverContextPath != null) {
            shiroServerUrlPrefix.append(serverContextPath);
        }
        return StringUtils.removeEnd(shiroServerUrlPrefix.toString(), "/");
    }

    /**
     * 获取登录地址
     * 
     * @return 登录地址
     */
    private String getLoginUrl() {
        if (shiroProperties.isCasEnable()) {
            return shiroProperties.getCasServerUrlPrefix() + shiroProperties.getCasLoginUrl()
                   + "?service=" + getShiroServerUrlPrefix()
                   + shiroProperties.getCasFilterUrlPattern() + "?client_name="
                   + shiroProperties.getCasClientName();
        }
        return shiroProperties.getLoginUrl();
    }
    //
    // /**
    // * 获取登出地址
    // *
    // * @return 登出地址
    // */
    // private String getLogoutUrl() {
    // if (shiroProperties.isCasEnable()) {
    // return shiroProperties.getCasServerUrlPrefix() + shiroProperties.getCasLogoutUrl() +
    // "?service=" + getShiroServerUrlPrefix();
    // }
    // return shiroProperties.getLogoutUrl();
    // }
}

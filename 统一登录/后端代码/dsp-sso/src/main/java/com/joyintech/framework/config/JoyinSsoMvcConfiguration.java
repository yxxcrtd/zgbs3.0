/*
 * Copyright (C),  兆尹科技有限公司
 * FileName: MvcConfiguration.java
 * Author:   Administrator
 * Date:     2022年7月15日 下午4:49:19
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.joyintech.framework.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;

/**
 * 〈实现静态资源处理JOYINSSO增量配置〉<br>
 * 〈静态资源处理，新增配置，此处需要注意，不能跟框架中同名（MvcConfiguration会进行覆盖）〉
 *
 * @author ZY22350
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@EnableWebMvc
@Configuration
public class JoyinSsoMvcConfiguration implements WebMvcConfigurer {
    /**
     * 静态资源版本
     */
    private String version = DateUtil.format(DateUtil.now(), DateUtil.DEFAULT_OTHER_FORMAT);

    /**
     * 静态资源配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        if(applicationContext != null) {
            String systemVersion = applicationContext.getEnvironment().getProperty("application.versionInfo.systemVersion");
            if(StringUtil.isNotEmpty(systemVersion)) {
                version = systemVersion;
            }
        }
        version = EncryptUtil.getInstance().md5(version);
//      registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/public/**").addResourceLocations("classpath:/META-INF/resources/");
        
        registry.addResourceHandler("/public/js/**").addResourceLocations("classpath:/META-INF/resources/js/")
            .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/")
            .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/")
        .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/image/**").addResourceLocations("classpath:/static/image/")
            .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/")
            .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        
        
        registry.addResourceHandler("/invest/js/**").addResourceLocations("classpath:/static/invest/js/")
            .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/invest/css/**").addResourceLocations("classpath:/static/invest/css/")
            .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/invest/image/**").addResourceLocations("classpath:/static/invest/image/")
            .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        // 新增joyin-sso-login静态资源处理开始
        registry.addResourceHandler("/joyin-sso-login/img/**").addResourceLocations("classpath:/static/joyin-sso-login/img/")
                .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/joyin-sso-login/fonts/**").addResourceLocations("classpath:/static/joyin-sso-login/fonts/")
                .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/joyin-sso-login/js/**").addResourceLocations("classpath:/static/joyin-sso-login/js/")
                .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/joyin-sso-login/css/**").addResourceLocations("classpath:/static/joyin-sso-login/css/")
                .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        //joyin-sso-login静态资源处理结束

        registry.addResourceHandler("/vuestatic/**").addResourceLocations("classpath:/static/vuestatic/")
            .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/")
        .resourceChain(false).addResolver(new VersionResourceResolver().addFixedVersionStrategy(version, "/**"));
    
    }
}

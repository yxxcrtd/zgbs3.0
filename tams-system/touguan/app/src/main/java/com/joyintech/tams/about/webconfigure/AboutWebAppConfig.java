package com.joyintech.tams.about.webconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.joyintech.tams.about.handlerinterceptor.AboutHandlerInterceptor;

/**
 * 将版本信息个性化拦截器配置进系统
 * @author Administrator
 *
 */
@Configuration
public class AboutWebAppConfig implements WebMvcConfigurer {

	@Override
    public void addInterceptors(InterceptorRegistry registry) {  
        registry.addInterceptor(new AboutHandlerInterceptor()).addPathPatterns("/main/about/init");
    }  
}

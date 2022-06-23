package com.joyintech.tams;

import com.joyintech.framework.JoyinApplication;
import com.joyintech.framework.config.WebApplication;
import com.joyintech.tams.common.ErrorPageFilter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;

/**
 * @author 王波
 * @version 1.0 2019年04月23日
 * @see 
 * @since 1.0
 */
@MapperScan(value = "com.joyintech.**.dao", annotationClass = Repository.class)
@ComponentScan(basePackages = "com.joyintech")
public class TamsApplication extends WebApplication {
    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }
    public static void main(String [] args) {
        JoyinApplication.run(TamsApplication.class, args);
    }
}

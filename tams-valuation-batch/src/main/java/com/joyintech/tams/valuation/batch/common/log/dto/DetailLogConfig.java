package com.joyintech.tams.valuation.batch.common.log.dto;/*
 * @ClassName DetailLogConfig
 * @Description 日志说明信息注解
 * @Date 2022/3/30 13:39
 * @Author 殷亚
 * @Version 1.0
 **/

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DetailLogConfig {
    /**
     * 字段的中文名
     * @return
     */
    String description() default "";
    /**
     * 是否展示
     * @return
     */
    boolean show() default true;
}

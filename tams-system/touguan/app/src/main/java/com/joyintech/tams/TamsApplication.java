package com.joyintech.tams;

import cn.hutool.core.date.StopWatch;
import com.joyintech.framework.JoyinApplication;
import com.joyintech.framework.config.WebApplication;
import com.joyintech.tams.common.ErrorPageFilter;
import com.joyintech.tams.exchange.common.utils.SpringContextUtils;
import com.joyintech.tams.exchange.service.trade.management.service.InstrumentManagement;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author 王波
 * @version 1.0 2019年04月23日
 * @see 1
 * @since 1.0
 */
//@MapperScan(value = "com.joyintech.**.dao", annotationClass = Repository.class)
@ComponentScan(basePackages = "com.joyintech")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class TamsApplication extends WebApplication {
    private static final Logger logger = LoggerFactory.getLogger(TamsApplication.class);

   /* @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }

    @Primary
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.setDateFormat(format);
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleModule module = new SimpleModule("jaskson-m");
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        module.addSerializer(Date.class, new DateSerializer(true, format));
        module.addDeserializer(String.class, new StringDeserializer());
        module.addDeserializer(Date.class,new CustomDateDeserialize());
        objectMapper.registerModule(module);
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }*/

    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        JoyinApplication.run(TamsApplication.class, args);
        stopWatch.stop();
        System.out.println("======================================== 启动成功！耗时：" + stopWatch.getTotalTimeMillis() / 1000 + "秒！========================================");
    }
}

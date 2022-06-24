package com.joyintech.tams.batch;

import com.joyintech.batch.config.BatchConfiguration;
import com.joyintech.framework.JoyinApplication;
import com.joyintech.framework.config.WebApplication;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 批处理启动类
 * @author 王波
 *
 */
@ComponentScan(basePackages = "com.joyintech")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class })
@Import({ BatchConfiguration.class })
public class TamsBatchApplication  extends WebApplication {
	public static void main(String[] args) {
		JoyinApplication.run(TamsBatchApplication.class, args);
	}
}

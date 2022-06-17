package com.joyintech.tams.exchange.service.trade.check.business;

import cn.hutool.core.map.MapUtil;
import com.joyintech.tams.exchange.common.utils.SpringContextUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lifangpeng
 * @date 2022/4/6 16:44
 */
@Component
public class BusinessCheckListener implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        final ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
        final Map<String, Object> checks = applicationContext.getBeansWithAnnotation(Check.class);
        if (MapUtil.isNotEmpty(checks)) {
            checks.forEach((beanName, bean) -> {
                final Check check = AnnotationUtils.findAnnotation(bean.getClass(), Check.class);
                if (check == null) {
                    return;
                }
                final String[] actions = check.action();
                if (ArrayUtils.isEmpty(actions)) {
                    final String message = String.format("数据检查器%s未设置业务类型，请检查", bean.getClass());
                    throw new RuntimeException(message);
                }
                for (String action : actions) {
                    CheckActions.I.put(action, (ICheck) bean);
                }
            });
        }
    }

}

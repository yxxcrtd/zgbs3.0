package com.joyintech.tams.invest.batch.exchange.runner;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.batch.exchange.service.ExchangeSettleService;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tools.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName SettleTask
 * @Description TODO
 * @Author swwan
 * @Date 2020/9/3 11:32
 * @Version 1.0
 **/
@Service("exchangeSettleTask")
@Scope("prototype")
public class ExchangeSettleTask extends BatchBaseService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected DiscoveryClient discoveryClient;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextpath.single:false}")
    private boolean isSingle;

    @Autowired
    private ExchangeSettleService exchangeSettleService;

    @Override
    public JobExecution execute() throws BatchException {

        JobExecution result = new JobExecution(1l);

        try {
            exchangeSettleService.execute();
//            if (isSingle) {
//                String url = ServiceApplication.EXCHANGE_SERVICE_NAME + "/joyin/settle";
//                logger.info("一体化服务,url: {}", url);
//                simpleRestTemplate.getForObject(url, String.class);
//            } else {
//                logger.info("分布式服务");
//                refreshAppCache(ServiceApplication.EXCHANGE_SERVICE_NAME);
//            }
            result.setStatus(BatchStatus.COMPLETED);
            logger.info("场内交易清算任务执行成功");
        }
        catch (Exception e) {
            result.setStatus(BatchStatus.FAILED);
            logger.error("场内交易清算任务执行失败: {}", e.getStackTrace());
        }

        return result;
    }

    private void refreshAppCache(String serviceApplicationName) {
        String serverName = StringUtil.split(serviceApplicationName)[1];
        if (StringUtil.contains(serverName, "/")) {
            serverName = StringUtil.split(serverName, "/")[0];
        }
        List<ServiceInstance> appList = discoveryClient.getInstances(serverName);
        final String serviceNameFinal = serverName;
        if (appList != null) {
            appList.forEach(serviceInstance -> {
                EurekaDiscoveryClient.EurekaServiceInstance client = (EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance;
                String apiUrl = StringUtil.replaceOnce(serviceApplicationName, serviceNameFinal,
                        client.getInstanceInfo().getIPAddr() + ":" + client.getInstanceInfo().getPort());
                logger.debug("场内交易清算任务接口：{}", apiUrl);
                HttpClient.getString(apiUrl + "/joyin/settle", null, null);
            });
        }
    }
}

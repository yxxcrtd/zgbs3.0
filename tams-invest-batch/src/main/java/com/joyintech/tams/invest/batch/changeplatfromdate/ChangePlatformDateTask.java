/*
 * 文件名：ChangePlatformDateTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.changeplatfromdate;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.system.cache.CacheRefreshService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 平台日切换批处理
 *
 * @author 刘二通
 * @version 1.0 2018年11月23日
 * @see ChangePlatformDateTask
 * @since 1.0
 */
@Service("changePlatformDateTask")
@Scope("prototype")
public class ChangePlatformDateTask extends BatchBaseService {

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

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private CacheRefreshService cacheRefreshService;

    /**
     * {@inheritDoc}
     */
    @Override
    public JobExecution execute()
        throws BatchException {

        JobExecution result = new JobExecution(1l);
        result.setStatus(BatchStatus.FAILED);

        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();

        try {

            String institutionCode = "";
            if(MultilevelDataAuthorityUtil.isEnabled()){
                // 仅 省联社跑批/自动跑批 跑全部平台日  非省联社用户 跑自己机构的数据
                String createUser = batchExecuteInfo.getCreateUser();
                // 省联社  || system || admin
                if(StringUtil.equals(createUser, GlobalConstant.SYSTEM_USER) || StringUtil.equals(createUser, GlobalConstant.SUPER_ADMIN_USER_NAME) || MultilevelDataAuthorityUtil.isTopOrganization(createUser)){
//                    platformDateService.updateAllSysDate();
                    // 更新所有机构的平台日   各自基础上 +1
                    institutionCode = "ALL";
                }else{
                    // 更新自己机构的平台日即可
                    institutionCode = MultilevelDataAuthorityUtil.getOrganizationCode(createUser);
                }
                platformDateService.updateAllSysDate(institutionCode);

            }else{
                // 当前平台日
                Date platformDate = platformDateService.getSysDate();
                // 切日后平台日
                Date newPlatformDate = DateUtil.addDay(platformDate, 1);
                // 平台日切换
                platformDateService.updateSysDate(newPlatformDate);

            }


//            if (isSingle) {
//                final String[] services = ServiceApplication.PINGAN_SERVICE_NAME.split(",");
//                if (services != null && services.length > 0) {
//                    for (String service: services) {
//                        logger.debug("更新一体化应用{}平台日缓存", service);
//                        // 调用平安页面端service，刷新平台日缓存
//                        simpleRestTemplate.getForObject(
//                                service + "/api/sys/refreshPlatDate?institutionCode="+institutionCode,
//                                String.class);
//                    }
//                }
//
//            }
//            else {
//                // 更新web应用端缓存
//                refreshAppCache(institutionCode);
//            }

            this.cacheRefreshService.updatePlatDate((Set)null);
            
            result.setStatus(BatchStatus.COMPLETED);

        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;

    }

    /**
     * 更新web应用端缓存
     */
    private void refreshAppCache(String institutionCode) {
        String serverName = StringUtil.split(ServiceApplication.PINGAN_SERVICE_NAME, "://")[1];
        if (StringUtil.contains(serverName, "/")) {
            serverName = StringUtil.split(serverName, "/")[0];
        }
        logger.debug("更新应用{}平台日缓存", serverName);
        List<ServiceInstance> appList = discoveryClient.getInstances(serverName);
        final String serviceNameFinal = serverName;
        if (appList != null) {
            appList.forEach(serviceInstance -> {
                EurekaDiscoveryClient.EurekaServiceInstance client = (EurekaDiscoveryClient.EurekaServiceInstance)serviceInstance;
                String apiUrl = StringUtil.replaceOnce(ServiceApplication.PINGAN_SERVICE_NAME, serviceNameFinal,
                        client.getInstanceInfo().getIPAddr() + ":" + client.getInstanceInfo().getPort());
                logger.debug("更新平台日缓存刷新接口：{}", apiUrl);
                HttpClient.getString(apiUrl + ServiceApplication.SERVLET_CONTEXT_PATH + "/api/sys/refreshPlatDate?institutionCode="+institutionCode, null, null);
            });
        }
    }

}

package com.joyintech.tams.exchange.util;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.utils.SpringContextUtils;
import com.joyintech.tams.exchange.service.trade.scheduled.TokenJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class MasterUtil {

    public static final String LOAD_BALANCED_REST_TEMPLATE = "loadBalancedRestTemplate";

//	private static AtomicLong count= new AtomicLong(1);
    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(MasterUtil.class);

    private MasterUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isMaster() {
//		if(count.getAndIncrement()%2==0) {
//			return false;
//		}else {
//			return true;
//		}
        return TokenJob.isMaster();
    }

    public static boolean isSlave() {
        return !TokenJob.isMaster();
    }

    public static String getMasterUrl() {
        String url = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURL().toString();
        logger.info("Redirect to Master,url:{}", url);
        String queryString = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getQueryString();
        logger.info("Redirect to Master,queryString:{}", queryString);
        if (StringUtil.isNotEmpty(queryString)) {
            url += "?" + queryString;
        }
        String masterAddress = TokenJob.getMasterAddress();
        String[] splitTemp = url.split("//");
        String masterUrl = splitTemp[0] + "//" + masterAddress + splitTemp[1].substring(splitTemp[1].indexOf("/"));
        logger.info("Redirect to Master,masterUrl:{}", masterUrl);
        return masterUrl;
    }

    @SuppressWarnings("rawtypes")
    public static Message postForObject(Object paramObject) {
        JoyinRestTemplate restTemplate = getJoyinRestTemplate();
        return restTemplate.postForObject(MasterUtil.getMasterUrl(), paramObject, Message.class);
    }

    public static ResultBean postForObjectResultBean(Object paramObject) {
        JoyinRestTemplate restTemplate = getJoyinRestTemplate();
        return restTemplate.postForObject(MasterUtil.getMasterUrl(), paramObject, ResultBean.class);
    }

    @SuppressWarnings("rawtypes")
    public static Message getForObject() {
        JoyinRestTemplate restTemplate = getJoyinRestTemplate();
        return restTemplate.getForObject(MasterUtil.getMasterUrl(), Message.class);
    }

    public static ResultBean getForObjectResultBean() {
        JoyinRestTemplate restTemplate = getJoyinRestTemplate();
        return restTemplate.getForObject(MasterUtil.getMasterUrl(), ResultBean.class);
    }

    private static JoyinRestTemplate getJoyinRestTemplate() {
        return SpringContextUtils.getBean(LOAD_BALANCED_REST_TEMPLATE);
    }

}

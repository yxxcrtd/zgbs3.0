/*
 * 文件名：InterfBaseController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年7月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.riskrdsinf.core.controller;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.riskrdsinf.cache.InterfCacheUtil;
import com.joyintech.service.riskrdsinf.common.dto.InterfRiskRdsDto;
import com.joyintech.service.riskrdsinf.core.client.ClientData;
import com.joyintech.service.riskrdsinf.core.constant.InfColumns;
import com.joyintech.service.riskrdsinf.core.constant.InfList;
import com.joyintech.service.riskrdsinf.core.exception.InterfException;
import com.joyintech.service.riskrdsinf.core.service.InfBaseService;
import com.joyintech.service.riskrdsinf.http.service.HttpReceiveJsonService;
import com.joyintech.service.riskrdsinf.tool.service.RefreshCacheService;

/**
 * 风险接口Controller
 * 
 * @author zhangrui1
 * @version 1.0 2019年7月26日
 * @see InfBaseController
 * @since 1.0
 */
@Component
@RestController
@RequestMapping("/interf")
public class InfBaseController {

    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    private HttpReceiveJsonService httpReceiveJsonService;

    protected static final String UNKNOWN = "unknown";

    protected static final String X_FORWARDED_FOR = "x-forwarded-for";

    protected static final String PROXY_CLIENT_IP = "Proxy-Client-IP";

    protected static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    @Value("${infBaseController.localhost}")
    protected String LOCALHOST;

    @Autowired(required = false)
    public IPlatformDate platformDate;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private RefreshCacheService refreshService;

    @RequestMapping("refreshCache")
    public AjaxResult refreshCache(){
    	refreshService.execute();
    	return new AjaxResult();
    }

    /*@ResponseBody
    @RequestMapping("fileLoad")
    public FileBatchDto fileLoad(@RequestBody FileBatchDto dto) {
        try {
            return fileLoadSrv.execute(dto);
        } catch (InterfException e) {
            dto.setOK(false);
            return dto;
        }
    }*/

    private void long2Date(Map<String,Object> map) {
        for(Field field:InterfRiskRdsDto.class.getDeclaredFields()) {
       	 if (field.getGenericType().toString().equals("class java.util.Date")) {
       		 if(map.containsKey(field.getName())) {
       			 Object object = map.get(field.getName());
       			 String type =  object.getClass().toString();
       			 if("Long".equals(type.substring(type.lastIndexOf(".")+1,type.length()))) {
       				map.put(field.getName(), new Date((Long)object));
       			 }
       		 }
       	 }
       };
    }

    @ResponseBody
    @RequestMapping("execute")
    public Map<String, Object> execute(@RequestBody Map<String, Object> dataMap) {
        ClientData data = new ClientData();
        data.convert(dataMap);
        //将时间戳转换为Date,类型信息来自于InterfRiskRdsDto
        long2Date(data.getBody());
        List<String> keyList = Arrays.asList("rskCalcList", "RSK_LIST");
        for(String key:keyList) {
            if(data.getBody().containsKey(key)) {
            	List<Map> maps = (List<Map>)data.getBody().get(key);
            	for(Map<String,Object> map:maps) {
            		long2Date(map);
            	}
            }
        }
        //将时间戳转换为Date,类型信息来自于InterfRiskRdsDto
    	String resStr = JsonUtils.object2Json(data);
    	logger.info("请求报文:{}", resStr);
        // 1.1 若参数为空，则处理结束返回结果
        if (data.isEmpty()) {
            return doError("参数异常【空的参数列表】");
        }
        // 1.2 从头部信息中取出接口ID
        String interfId = (String)data.getHeadField(InfColumns.COM_INFID.fieldName());
        // 1.2.1 若接口ID为空，则处理结束返回结果
        if (StringUtil.isEmpty(interfId)) {
            return doError("参数异常【空的接口ID】");
        }
        // 2.根据接口ID取得接口模块实例进行事件分发
        InfBaseService<?> service = InfList.getBean(interfId);
        // 2.1 若接口模块不存在，则处理结束返回结果
        if (service == null) {
            return doError("接口模块异常【" + interfId + "对应的接口不存在】");
        }
        // 取得IP地址
        String ipAddress = getIpAddress();
        data.setBodyField(InfColumns.COM_IP.fieldName(), ipAddress);
        // 取得端口号
        int port = getRemotePort();
        data.setBodyField(InfColumns.COM_PORT.fieldName(), port);
        // 取得平台日期(风险里的日期，每隔20分钟刷新一次)
        //String sysDate = InterfCacheUtil.getSysDate();
        String sysDate = DateUtil.format(platformDate.getSysDate(),DateUtil.DEFAULT_OTHER_FORMAT);
        data.setBodyField(InfColumns.COM_SYSDATE.fieldName(), sysDate);
        // 2.2 接口模块处理
        return service.execute(data);
    }

    private Map<String, Object> doError(String errMsg) {
        ClientData data = new ClientData();
        data.setHeadField(InfColumns.COM_RTNCODE.fieldName(), 1);
        data.setHeadField(InfColumns.COM_RTNMSG.fieldName(), errMsg);
        return data.convertToMap();
    }

    /**
     * 获取 客户端IP
     * 
     * @return 客户端IP
     * @see
     * @since 1.0
     */
    private String getIpAddress() {
        String ipAddress = null;
        try {
            ipAddress = getHeader(X_FORWARDED_FOR);
            if (isIpUnknown(ipAddress)) {
                ipAddress = getHeader(PROXY_CLIENT_IP);
            }
            if (isIpUnknown(ipAddress)) {
                ipAddress = getHeader(WL_PROXY_CLIENT_IP);
            }
            if (isIpUnknown(ipAddress)) {
                ipAddress = getRemoteAddr();
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (!isIpUnknown(ipAddress)) {
                int index = StringUtil.indexOf(ipAddress, GlobalConstant.COMMA);
                ipAddress = index > 0 ? ipAddress.substring(0, index) : ipAddress;
            }
        }
        catch (Exception e) {
            logger.error("客户端IP解析异常", e);
            ipAddress = StringUtil.EMPTY;
        }

        return ipAddress;
    }

    /**
     * unknown ip address
     * 
     * @param ipAddress
     * @return true:null or unknown;
     * @see
     * @since 1.0
     */
    private boolean isIpUnknown(String ipAddress) {
        return StringUtil.isEmpty(ipAddress) || StringUtil.equalsIgnoreCase(UNKNOWN, ipAddress);
    }

    private String getHeader(String key) {
        return request.getHeader(key);
    }

    private String getRemoteAddr()
        throws UnknownHostException {
        String ipAddress = request.getRemoteAddr();
        if (StringUtil.equals(ipAddress, LOCALHOST)) {
            // 根据网卡取本机配置的IP
            InetAddress inet = InetAddress.getLocalHost();
            ipAddress = inet.getHostAddress();
        }
        return ipAddress;
    }

    private int getRemotePort() {
        return request.getRemotePort();
    }
    /**
     * 资管用户信息同步
     *
     * @param request 请求
     * @return 处理结果
     * @see
     */
    @RequestMapping("/ziguanSysUserInfo")
    @ResponseBody
    public String ziguanSysUserInfo(HttpServletRequest request) {

        // 获取资管用户信息
        String msg = getInfo(request);
        logger.info(msg);
        // 请求地址
        String url = request.getRequestURL().toString();
        String returnMsg = httpReceiveJsonService.ziguanSysUserInfo(msg, url);
        return returnMsg;
    }


    /**
     * 从 request 中获取消息
     *
     * @param request 请求
     * @return json 报文
     * @see
     * @since 1.0
     */
    private String getInfo(HttpServletRequest request) {
        // BufferedReader
        BufferedReader reader = null;
        String lines = null;
        StringBuffer sbf = new StringBuffer();
        try {
            // 新建输入流
            // 在读流之前设置编码格式为utf-8-luwenbo
            request.setCharacterEncoding("utf-8");
            reader = request.getReader();
            // 逐行读取
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes());
                sbf.append(lines);

            }
        }
        // 获取输入流异常
        catch (Exception e) {
            logger.error("接收报文：", e);
            return null;
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                // 关闭输入流时发生异常
                catch (Exception e) {
                    logger.error("接收报文：", e);
                }
            }
        }
        return sbf.toString();
    }
}

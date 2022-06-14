package com.joyintech.tams.exchange.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
//import com.netflix.appinfo.InstanceInfo;
//import com.netflix.discovery.shared.Application;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static String doRequest(String httpUrl, String method, Object content) throws Exception {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：GET\POST
            connection.setRequestMethod(method);
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：30000毫秒
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            // 发送请求
            connection.connect();
            if (Objects.nonNull(content)) {
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.write(JsonUtils.getJsonFromObject(content).getBytes(StandardCharsets.UTF_8));
                // 刷新、关闭
                out.flush();
                out.close();
            }
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                // 存放数据
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            } else {
                logger.info("doRequest not return 200 :{}, method:{}, param:{}, code:{}", httpUrl, method, JsonUtils.getJsonFromObject(content), connection.getResponseCode());
            }
        } finally {
            // 关闭资源
            if (null != br) {
                br.close();
            }

            if (null != is) {
                is.close();
            }
            connection.disconnect();// 关闭远程连接
        }
        return result;
    }

//    public static Map<String, String> getEurekaApplications(String url) throws Exception {
//        String xml = doRequest(url, "GET", null);
//
//        XStream xstream = new XStream();
//        xstream.processAnnotations(Application.class);
//        xstream.aliasField("app", InstanceInfo.class, "appName");
//        xstream.omitField(InstanceInfo.class, "dataCenterInfo");
//        xstream.omitField(InstanceInfo.class, "metadata");
//        Application application = (Application) xstream.fromXML(xml);
//        if (Objects.isNull(application)) return null;
//        return application.getInstances().stream().collect(Collectors.toMap(InstanceInfo::getInstanceId, InstanceInfo::getInstanceId));
//    }

    public static ResultBean getResultBeanByDoRequest(String url, String method, Object content) {
        try {
            String result = doRequest(url, method, content);
            return JsonUtils.getObjectFromJson(result, ResultBean.class);
        } catch (Exception e) {
            logger.info("getResultBeanByDoRequest Exception: {}", e);
            return new ResultBean(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    public static <T> ResultBean convertResultFromHttpStrForList(String result, Class<T> dataClass) {
        ResultBean resultBean = new ResultBean();
        JSONObject jsonObject = JSON.parseObject(result);
        resultBean.setErrorCode(jsonObject.getInteger("errorCode"));
        resultBean.setErrorMsg(jsonObject.getString("errorMsg"));
        String s1 = jsonObject.getString("data");
        List<T> objectListFromJson = JsonUtils.getObjectListFromJson(s1, dataClass);
        resultBean.setData(objectListFromJson);
        return resultBean;
    }

    public static <T> ResultBean convertResultFromHttpStrForObj(String result, Class<T> dataClass) {
        ResultBean resultBean = new ResultBean();
        JSONObject jsonObject = JSON.parseObject(result);
        resultBean.setErrorCode(jsonObject.getInteger("errorCode"));
        resultBean.setErrorMsg(jsonObject.getString("errorMsg"));
        String s1 = jsonObject.getString("data");
        T objectFromJson = JsonUtils.getObjectFromJson(s1, dataClass);
        resultBean.setData(objectFromJson);
        return resultBean;
    }
}

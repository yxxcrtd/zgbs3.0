package com.joyintech.tams.position.util;

import java.io.IOException;

import com.joyintech.tams.position.exception.PositionHttpRuntimeException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static Integer maxTotal = 100;      // 连接池最大并发连接数
    private static Integer maxPerRoute = 20;    // 单域名下最大连接数
    
    /**
     * get请求
     * @return
     */
    public static String doGet(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());

                return strResult;
            }
        }
        catch (IOException e) {
            logger.error("发送get请求异常",e);
        }

        return null;
    }

    /**
     * post请求（用于请求json格式的参数）
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, String params) {

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(maxTotal);//连接池最大并发连接数
        cm.setDefaultMaxPerRoute(maxPerRoute);//单域名下最大连接数
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
        
//        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);// 创建httpPost   
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        String charSet = "UTF-8";
        StringEntity entity = new StringEntity(params, charSet);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        try {
            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            if (state == HttpStatus.SC_OK) {
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            } else{
                throw new RuntimeException("http请求异常:" + status!=null?status.toString():"http请求异常");
            }
        }catch (IOException e){
            logger.error("http通信异常:" + e.getMessage(),e);
            throw new PositionHttpRuntimeException("http通信异常:" + e.getMessage());
        }catch (Exception e){
            logger.error("http通信异常:" + e.getMessage(),e);
            throw new RuntimeException("http通信异常:" + e.getMessage());
        } finally {
            if (response != null) {
                try {
                    final HttpEntity enty = response.getEntity();
                    EntityUtils.consume(enty);
                } catch (IOException e) {
                    logger.error("",e);
                }
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error("",e);
            }
        }
    }
}

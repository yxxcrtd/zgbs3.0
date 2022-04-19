package com.joyintech.tams.cfets.etr.socket;

import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.util.WebSocketClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Lazy(value=false)
public class BondSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(BondSocketClient.class);

    // 债券行情行息 mDEntryPx 最新净价
    private static Map<String, BaseMap> bonds = new ConcurrentHashMap<>();

    @Value("${bondWebSocketKey}")
    private String bondWebSocketKey;
    
    @Value("${cmdsWebSocketUrl}")
    private String cmdsWebSocketUrl;
    
    boolean isStart = false;  // 是否启动开关
    boolean loopFlag = false; // 10秒重连循环开关
    
    @PostConstruct
    public void init(){
        logger.info("WebSocketClient初始化一次...");
        
        new Thread(this::loopStart).start();
    }

    private WebSocketClientUtil util = null;
    
    public boolean start(){
        
        if (isStart){
            return true;
        }
        
        boolean rs = true;
        /**
         * 债券服务监听市场行情
         */
        try {
            util = new WebSocketClientUtil();
            
            util.start(cmdsWebSocketUrl, new WebSocketClientUtil.WebSocketListener() {
                @Override
                public void onMessage(String message) {
                    try {
                        BaseMap responseMap = JsonUtils.json2Object(message, BaseMap.class);

                        if (responseMap == null) {
                            return;
                        }
                        logger.info("收到webSocket消息: ");
                        logger.info(responseMap.toString());
                        messageIntoMap(responseMap);
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("债券数据同步解析失败！");
                    }
                }
                
                @Override
                public void onClose() {
                    // 重连
                    logger.info("债券服务关闭，开始重连");
                    
                    try {
                        util.close();} catch (IOException e) {e.printStackTrace();}
                    isStart = false;
                    
                    // 每间隔10秒重连
                    loopStart();

                    isStart = true; // 启动成功
                    logger.info("债券服务启动成功。。。");
                }
            });

            // 启动成功
            isStart = true; // 服务启动成功
            
            // 启动时发送一次
            send();
        } catch (Exception e) {
            e.printStackTrace();
            rs = false;
            isStart = false; // 服务启动失败
        }
        return rs;
    }

    /**
     * 从本币返回信息中获取对应数据
     * @param baseMap
     * @return
     */
    private void messageIntoMap(BaseMap baseMap) {
        if (baseMap != null) {
            Map noMDTypes = (Map) baseMap.get("noMDTypes");
            if (noMDTypes != null) {
                Map noRelatedSym = (Map) noMDTypes.get("noRelatedSym");
                if (noRelatedSym != null) {
                    String securityID = noRelatedSym.get("securityID") + "";
                    BaseMap result = BaseMap.newMap();
                    
                    BigDecimal mDEntryPx = null;
                    List<Map> noMDEntries = (List) noRelatedSym.get("noMDEntries");
                    if (noMDEntries != null && !noMDEntries.isEmpty()) {
                        for (Map m: noMDEntries){
                            if (StringUtil.equals("e", m.get("mDEntryType"))){
                                String mDEntryPxName = "mDEntryPx";
                                // 最新净价
                                Object obj = m.get(mDEntryPxName);
                                if (obj != null){
                                    mDEntryPx = new BigDecimal(obj.toString());
                                    result.put(mDEntryPxName, mDEntryPx);
                                    break;
                                }
                            }
                        }
                    }
                    
                    bonds.put(securityID, result);
                }
            }
        }
    }


    public void loopStart(){
        if(loopFlag){
            return;
        }
        loopFlag = true;
        synchronized (this){
            while (!start()){
                logger.info("债券服务通信失败，10秒后重连");
                logger.info(cmdsWebSocketUrl);
                try {
                    TimeUnit.SECONDS.sleep(10l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
        loopFlag =false;
    }


    public void send(){
        util.send(bondWebSocketKey);
        logger.info("债券服务发送成功。。。");
    }

    private static BaseMap getBond(String key) {
        
        return bonds.get(key);
    }


    /**
     * 根据债券代码直接获取净价
     * @param key
     * @return
     */
    public static BigDecimal getLocalCachePrice(String key){
        if (key == null) {
            return null;
        }
        // 去除后缀
        int index = key.lastIndexOf(".");
        String theKey = key;
        if (index > 0){
            theKey = StringUtils.substring(key, 0, index);
        }
        
        BaseMap bm = getBond(theKey);
        if (bm != null) {
            return bm.getBigDecimal("mDEntryPx");
        }
        return null;
    }
}

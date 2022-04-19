package com.joyintech.tams.cfets.etr.socket;

import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.business.common.util.WebSocketClientUtil;
import com.joyintech.tams.cfets.etr.bond.service.PositionSocketService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Lazy(value=false)
public class PositionSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(PositionSocketClient.class);
    
    @Autowired
    private PositionSocketService webSocketService;

    private static final String TABLE_NAME ="tableName";
    private static final String CDATE = "CDATE";
    private static final String PORTFOLIO_ID="PORTFOLIO_ID";
    private static final String ACCT_ID="ACCT_ID";
    private static final String HODING_TYPE="HODING_TYPE";
    private static final String DATE_TYPE="DATE_TYPE";
    private static final String CCY="CCY";
    private static final String AMOUNT="AMOUNT";
    private static final String SEC_ACCT_ID = "SEC_ACCT_ID";
    private static final String FINPROD_ID="FINPROD_ID";
    
    @Value("${webSocketUrl}")
    private String webSocketUrl;
    
    @Value("${webSocketKey}")
    private String webSocketKey;

    boolean isStart = false;  // 是否启动开关
    boolean loopFlag = false; // 10秒重连循环开关
    
    @PostConstruct
    public void init(){
        logger.info("WebSocketClient初始化一次...");
//        new Thread(this::loopStart).start();
    }

    @Autowired
    public void initPositionUtil(PositionUtil positionUtil){
        logger.info("positionUtil初始化");
    }
    
    private WebSocketClientUtil util = null;
    
    public boolean start(){
        
        if (isStart){
            return true;
        }
        
        boolean rs = true;
        /**
         * 头寸监听余额变化，更新投管数据库流水余额表
         */
        try {
            util = new WebSocketClientUtil();
            
            util.start(webSocketUrl, new WebSocketClientUtil.WebSocketListener() {
                @Override
                public void onMessage(String message) {
                    try {
                        List<Object> list = JsonUtils.json2Object(message, List.class);

                        logger.info(MessageFormat.format("收到webSocket消息: {0}", message));

                        if (list.size() == 2) {
                            String tableName = (String)list.get(0);
                            List arr = (ArrayList)list.get(1);
                            if ("ETR_CASH_POSITION_FLOW".equals(tableName)){
//                                List arr = JSON.parseObject(jsonStr, List.class);
                                webSocketService.insertOrUpdateForFlows(arr, "ETR_CASH_POSITION_FLOW_RULE");
                            }else if ("ETR_SEC_POSITION_FLOW".equals(tableName)){
//                                List arr = JSON.parseObject(jsonStr, List.class);
                                webSocketService.insertOrUpdateForFlows(arr, "ETR_SEC_POSITION_FLOW_RULE");
                            }else if ("PTL_CASH_POSITION_FLOW".equals(tableName)){
//                                List arr = JSON.parseObject(jsonStr, List.class);
                                webSocketService.insertOrUpdateForFlows(arr, "PTL_CASH_POSITION_FLOW_RULE");
                            }else if ("PTL_SEC_POSITION_FLOW".equals(tableName)){
//                                List arr = JSON.parseObject(jsonStr, List.class);
                                webSocketService.insertOrUpdateForFlows(arr, "PTL_SEC_POSITION_FLOW_RULE");
                            }
                            logger.info(MessageFormat.format("socket同步数据结束: {0}", tableName));
                            return ;
                        }
                        
                        if (list != null && !list.isEmpty()){
                            // 转换成map
                            BaseMap dataMap = convertedMap(list);
                            if (dataMap == null){
                                return;
                            }
                            // long转换成日期格式
                            dataMap.put(CDATE, dataMap.getDate(CDATE));
                            
                            webSocketService.insertOrUpdate(dataMap);

                            logger.info(MessageFormat.format("socket同步数据结束: {0}", dataMap));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("头寸数据同步解析失败！");
                    }
                }
                
                @Override
                public void onClose() {
                    // 重连
                    logger.info("头寸服务关闭，开始重连");
                    
                    try {
                        util.close();} catch (IOException e) {e.printStackTrace();}
                    isStart = false;
                    
                    // 每间隔10秒重连
                    loopStart();

                    isStart = true; // 启动成功
                    logger.info("头寸服务启动成功。。。");
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


    public void loopStart(){
        if(loopFlag){
            return;
        }
        loopFlag = true;
        synchronized (this){
            try {
                while (!start()){
                    logger.info("头寸通信失败，10秒后重连");
                    logger.info(webSocketUrl);
    
                    TimeUnit.SECONDS.sleep(10l);
                }
            } catch (InterruptedException e) {
                logger.error("thread sleep error");
                Thread.currentThread().interrupt();
            }
        }
        loopFlag =false;
    }


    /**
     * 转换map
     * @param list
     * @return
     */
    private BaseMap convertedMap(List<Object> list) {
        String tableName = (String)list.get(0);
        if (StringUtils.equals(tableName, "ETR_CASH_POSITION")){
            return BaseMap.newMap()
                    .put(TABLE_NAME, "ETR_CASH_POSITION_RULE")
                    .put(PORTFOLIO_ID,list.get(2))
                    .put(ACCT_ID,list.get(3))
                    .put(HODING_TYPE, list.get(4))
                    .put(CDATE, list.get(5))
                    .put(DATE_TYPE, list.get(6))
                    .put(CCY, list.get(7))
                    .put(AMOUNT, list.get(8))
                    ;
        }else if (StringUtils.equals(tableName, "ETR_SEC_POSITION")){
            return BaseMap.newMap()
                    .put(TABLE_NAME, "ETR_SEC_POSITION_RULE")
                    .put(PORTFOLIO_ID,list.get(2))
                    .put(SEC_ACCT_ID,list.get(3))
                    .put(FINPROD_ID, list.get(4))
                    .put(HODING_TYPE, list.get(5))
                    .put(CDATE, list.get(6))
                    .put(DATE_TYPE, list.get(7))
                    .put(CCY, list.get(8))
                    .put(AMOUNT, list.get(9))
                    ;
        }else if (StringUtils.equals(tableName, "PTL_SEC_POSITION")){
            return BaseMap.newMap()
                    .put(TABLE_NAME, "PTL_SEC_POSITION_RULE")
                    .put(PORTFOLIO_ID,list.get(2))
                    .put(SEC_ACCT_ID,list.get(3))
                    .put(FINPROD_ID, list.get(4))
                    .put(HODING_TYPE, list.get(5))
                    .put(CDATE, list.get(6))
                    .put(DATE_TYPE, list.get(7))
                    .put(CCY, list.get(8))
                    .put(AMOUNT, list.get(9))
                    ;
        }else if (StringUtils.equals(tableName, "PTL_CASH_POSITION")){
            return BaseMap.newMap()
                    .put(TABLE_NAME, "PTL_CASH_POSITION_RULE")
                    .put(PORTFOLIO_ID,list.get(2))
                    .put(ACCT_ID,list.get(3))
                    .put(HODING_TYPE, list.get(4))
                    .put(CDATE, list.get(5))
                    .put(DATE_TYPE, list.get(6))
                    .put(CCY, list.get(7))
                    .put(AMOUNT, list.get(8))
                    ;
        }
        //
        return BaseMap.newMap();
    }

    public void send(){
        // [{"table":"ETR_CASH_POSITION_RULE", "hodingTypes":[]},{"table":"ETR_SEC_POSITION_RULE", "hodingTypes":[]},{"table":"PTL_CASH_POSITION_RULE", "hodingTypes":[]},{"table":"PTL_SEC_POSITION_RULE", "hodingTypes":[]}]
        util.send(webSocketKey);
        logger.info("头寸服务发送成功。。。");
    }
}

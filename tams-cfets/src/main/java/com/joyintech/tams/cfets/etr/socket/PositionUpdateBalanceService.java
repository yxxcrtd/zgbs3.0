package com.joyintech.tams.cfets.etr.socket;

import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dao.WebSocketMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * mq消息发送与监听实现类
 */
@Component
public class PositionUpdateBalanceService implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LoggerFactory.getLogger(PositionUpdateBalanceService.class);

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


    private static RabbitTemplate rabbitTemplate;

    private static WebSocketMapper webSocketMapper;


    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        PositionUpdateBalanceService.rabbitTemplate = rabbitTemplate;
    }


    @Autowired
    public void setWebSocketMapper(WebSocketMapper webSocketMapper) {
        PositionUpdateBalanceService.webSocketMapper = webSocketMapper;
    }

    public static String QUEUE_POSITION_UPDATE = "queue.position.update.balance";

    private  ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

    private static boolean start=true;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(start){
            logger.info("监听余额队列启动！");
            fixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        receive();
                    }
                }
            });
            start=false;
        }
    }

    private  Message receive(){
        Message msg = null;
        try {
            if (rabbitTemplate != null){
                msg = rabbitTemplate.receive(PositionUpdateBalanceService.QUEUE_POSITION_UPDATE);
                if(msg!=null){
                    logger.info(msg.toString());
                    logger.info("头寸返回mq消息："+ new String(msg.getBody(), "UTF-8"));
                    List<Object> list =JsonUtils.json2Object(new String(msg.getBody(), "UTF-8"), List.class);
                    if (list.size() == 2) {
                        String tableName = (String)list.get(0);
                        List arr = (ArrayList)list.get(1);
                        if ("ETR_CASH_POSITION_FLOW".equals(tableName)){
//                                List arr = JSON.parseObject(jsonStr, List.class);
                            PositionUpdateBalanceService.insertOrUpdateForFlows(arr, "ETR_CASH_POSITION_FLOW_RULE");
                        }else if ("ETR_SEC_POSITION_FLOW".equals(tableName)){
//                                List arr = JSON.parseObject(jsonStr, List.class);
                            PositionUpdateBalanceService.insertOrUpdateForFlows(arr, "ETR_SEC_POSITION_FLOW_RULE");
                        }else if ("PTL_CASH_POSITION_FLOW".equals(tableName)){
//                                List arr = JSON.parseObject(jsonStr, List.class);
                            PositionUpdateBalanceService.insertOrUpdateForFlows(arr, "PTL_CASH_POSITION_FLOW_RULE");
                        }else if ("PTL_SEC_POSITION_FLOW".equals(tableName)){
//                                List arr = JSON.parseObject(jsonStr, List.class);
                            PositionUpdateBalanceService.insertOrUpdateForFlows(arr, "PTL_SEC_POSITION_FLOW_RULE");
                        }
                        logger.info(MessageFormat.format("mq同步数据结束: {0}", tableName));
                        return msg;
                    }

                    if (list != null && !list.isEmpty()){
                        // 转换成map
                        BaseMap dataMap = convertedMap(list);
                        if (dataMap == null){
                            return msg;
                        }
                        // long转换成日期格式
                        dataMap.put(CDATE, dataMap.getDate(CDATE));

                        PositionUpdateBalanceService.insertOrUpdate(dataMap);

                        logger.info(MessageFormat.format("mq同步数据结束: {0}", dataMap));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("mq同步数据失败{}");
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 转换map
     * @param list
     * @return
     */
    private static BaseMap convertedMap(List<Object> list) {
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

    /**
     * 头寸余额变动同步数据
     * @param dataMap
     */
    public static void insertOrUpdate(BaseMap dataMap) {
        int count = webSocketMapper.select(dataMap);
        if (count == 0){
            webSocketMapper.insert(dataMap);
        }else{
            webSocketMapper.update(dataMap);
        }
    }

    public static void insertOrUpdateForFlows(List list, String tableName) {
        if (list != null && !list.isEmpty()){
            Map param = (Map) list.get(0);
            param.put("tableName",tableName);
            webSocketMapper.deleteFlows(param);
        }
        for (Object obj : list){
            BaseMap each = BaseMap.newMap((Map) obj);
            if (each != null) {
                if (each.get("WORK_FLOW_ID") == null){ each.put("WORK_FLOW_ID", ""); }
                if (each.get("TRADE_ID") == null){ each.put("TRADE_ID", ""); }
                if (each.get("ORD_ID") == null){ each.put("ORD_ID", ""); }
                if (each.get("TOTAL_TRADE_ID") == null){ each.put("TOTAL_TRADE_ID", ""); }
                if (each.get("UPBLSTM") == null){ each.put("UPBLSTM", "0"); }
                if (each.get("BUSI_ACT") == null){ each.put("BUSI_ACT", ""); }
                if (each.get("BUSI_TYPE") == null){ each.put("BUSI_TYPE", ""); }
                if (each.get("PS") == null){ each.put("PS", ""); }
                if (each.get("SYSTEM_ACT") == null){ each.put("SYSTEM_ACT", ""); }
                if (each.get("OPERATIONTYPE") == null){ each.put("OPERATIONTYPE", ""); }
                if (each.get("ATAMOUNT") == null){ each.put("ATAMOUNT", "0"); }
                if (each.get("CREATE_USER") == null){ each.put("CREATE_USER", "system"); }
                if (each.get("ENTRUST_ID") == null){ each.put("ENTRUST_ID", ""); }

                each.put("CDATE", each.getDate("CDATE"));
                each.put("CREATE_TIME", each.getDate("CREATE_TIME"));
                each.put("tableName", tableName);
                webSocketMapper.insertFlows(each);
            }

        }
    }
}

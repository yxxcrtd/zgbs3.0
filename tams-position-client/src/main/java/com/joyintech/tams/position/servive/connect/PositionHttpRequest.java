package com.joyintech.tams.position.servive.connect;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.position.check.PositionCheck;
import com.joyintech.tams.position.constant.PositionConstant;
import com.joyintech.tams.position.constant.PzxMessageCode;
import com.joyintech.tams.position.exception.PositionException;
import com.joyintech.tams.position.exception.PositionHttpRuntimeException;
import com.joyintech.tams.position.pojo.BaseMap;
import com.joyintech.tams.position.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

//@PropertySource("classpath:config.properties")
@Component
@Lazy(value = false)
public class PositionHttpRequest {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(PositionHttpRequest.class);

    private static String positionUrl;

    @Autowired
    private PositionCheck positionCheck;

    @Autowired
    private PositionMqRequest positionMqRequest;

    @Value("${positionUrl}")
    public void setPositionUrl(String url) {
        PositionHttpRequest.positionUrl = url;
    }

    /**
     * 头寸调用方法，需处理异常，可使用PositionUtil.ajaxErrorReturn解析异常
     * 返回码 300|。。。  表示余额不足，需前端确认
     * 返回码 410|。。。  表示通信息或网络超时
     * 返回码 420|。。。  头寸引擎返回不成功，具体参考返回信息
     *
     * @param req
     * @throws PositionException
     */
    protected  Map<String, Object> send(Object req) throws PositionException {
        //处理seqno
        Map<String, Object> params = JsonUtils.stringToMap(JsonUtils.object2Json(req));
        if (null == params.get("PositionSeqno")) {
            String seqno = IDGenerator.generate();
            params.put("seqno", seqno);
        } else {
            params.put("seqno", params.get("PositionSeqno"));
        }

        logger.info("发送头寸报文:{}", JsonUtils.object2Json(params));
        Map msgMap = null;
        String res = sendPrivate(params);
        logger.info("头寸返回数据:{}", res);
        msgMap = JsonUtils.json2Object(res, Map.class);
        if (msgMap == null) {
            logger.error("头寸返回数据为空");
            throw new PositionException("头寸返回数据为空！");
        }
        if (!StringUtil.equals("success", msgMap.get("code") + "")) {
            if (StringUtil.equals(PzxMessageCode.FL0007.name(), msgMap.get("code"))) {
                // 头寸引擎提示现金不足时需要再校验原投管现金
                // 返回true，表示现金够，重新发送
                boolean isEnough = positionCheck.checkForFL0007(req, msgMap);
                if (!isEnough) {
                    // 软控时，加上原投管现金仍不足时需前端确认
                    throw new PositionException(PositionConstant.CODE_BUSI_RK + "|" + JsonUtils.object2Json(msgMap));
                }
            } else if (StringUtil.equals(PzxMessageCode.CT0001.name(), msgMap.get("code"))) {
                // 头寸引擎提示现金不足时需要再校验原投管现金
                throw new PositionException((String) msgMap.get("message"));
//                }
            } else {
                revokeCheck(msgMap, params);
            }
        }
        return params;
    }

    private boolean  contain (String s){
        for(PzxMessageCode pzx:PzxMessageCode.values()){
            if(StringUtil.equals(pzx.name(),s)){
                return true;
            }
        }
        return false;
    }
    /**
     * 校验是否需要头寸冲正
     *
     * @param message
     * @param req
     */
    private  void revokeCheck(Map message, Object req) {
        if (contain((String) message.get("code"))) {
            throw new PositionException("头寸更新异常：" + message);
        } else {
            String jsonStr = JsonUtils.object2Json(req);
            logger.info("发送头寸报文:" + jsonStr);
            BaseMap reqMap = JsonUtils.json2Object(jsonStr, BaseMap.class);
            String errorMsg = "";
            if (message != null) {
                errorMsg = (String) message.get("message");
            } else {
                errorMsg = "头寸返回数据为空";
            }
            roll(reqMap.getString("seqno"), reqMap.getString("modelType"), errorMsg);
        }
    }

    protected  void roll(String revokeId, String modelType, String errorMsg) {
        BaseMap bm = BaseMap.newMap() .put("revokeId", revokeId) .put("modelType", modelType) .put("userId", LoginUserUtil.getUserName());
        logger.info("发送回滚头寸报文:" + JsonUtils.object2Json(bm));
        try {
            rollHttp(bm);
        } catch (Exception e) {
            logger.error("回滚头寸报错", e);
        }
        // 页面端根据异常信息返回前端
        throw new PositionException(errorMsg);
    }

    protected  void sendRevoke(String revokeId, String modelType, String errorMsg) {
        BaseMap bm = BaseMap.newMap().put("revokeId", revokeId).put("modelType", modelType).put("userId", LoginUserUtil.getUserName());
        logger.info("发送头寸报文:" + JsonUtils.object2Json(bm));
        try {
            rollMq(bm);
        } catch (Exception e) {
            logger.error("回滚头寸报错", e);
        }
    }

    protected  Map<String, Object>  doDeleteByWorkFlowId(String revokeId, String modelType, String busiType, String ps) throws PositionException {
        //
        BaseMap bm = BaseMap.newMap().put("workFlowId", revokeId).put("modelType", modelType)
                .put("userId", LoginUserUtil.getUserName()).put("busiType", busiType).put("ps", ps);
        String seqno =IDGenerator.generate();
        bm.put("seqno", seqno);
        logger.info("发送头寸报文:" + JsonUtils.object2Json(bm));
        String res = delete(bm);
        Map message = JsonUtils.json2Object(res, Map.class);
        if (message == null) {
            throw new PositionException("头寸返回数据为空！");
        }
        logger.info("返回头寸报文: {}", message);
        return bm;
    }

    protected  Map<String, Object>  dorevoke(String revokeId, String modelType, String busiType, String ps) throws PositionException {
        BaseMap bm = BaseMap.newMap().put("revokeId", revokeId).put("modelType", modelType)
                .put("userId", StringUtil.isEmpty(LoginUserUtil.getUserName()) ? "system" : LoginUserUtil.getUserName())
                .put("busiType", busiType).put("ps", ps);
        String seqno =IDGenerator.generate();
        bm.put("seqno", seqno);
        logger.info("发送头寸报文:" + JsonUtils.object2Json(bm));
        String res = revoke(bm);
        logger.info("头寸返回数据:" + res);
        Map msgMap = JsonUtils.json2Object(res, Map.class);
        if (msgMap == null) {
            throw new PositionException("头寸返回数据为空！");
        }
        return bm;
    }


    /**
     * 转换为json
     *
     * @param url
     * @param param
     * @return
     */
    private  String httpPostRequest(String url, Object param) {
        return HttpUtil.doPost(url, JsonUtils.object2Json(param));
    }

    /**
     * 头寸调用方法，需处理异常，可使用PositionUtil.ajaxErrorReturn解析异常
     * 返回码 300|。。。  表示余额不足，需前端确认
     * 返回码 410|。。。  表示通信息或网络超时
     * 返回码 420|。。。  头寸引擎返回不成功，具体参考返回信息
     *
     * @param req
     * @throws PositionException
     */
    protected  Map<String, Object> batchClose(Object req) throws PositionException {
        //
        //处理seqno
        Map<String, Object> params = JsonUtils.stringToMap(JsonUtils.object2Json(req));
//        Long seqno = (long) Arrays.deepHashCode(new Object[]{params.get("totalTradeId"), params.get("modelType"), new Date().getTime(), Thread.currentThread().getId()});
        String seqno =IDGenerator.generate();
        params.put("seqno", seqno);
        logger.info("发送头寸报文:" + JsonUtils.object2Json(params));
        String res = batchClosePrivate(params);
        logger.info("头寸返回数据:" + res);
        Map msgMap = JsonUtils.json2Object(res, Map.class);
        if (msgMap == null) {
            throw new PositionException("头寸返回数据为空！");
        } else if (!StringUtil.equals("success", msgMap.get("code") + "")) {
            throw new PositionException(JsonUtils.object2Json(msgMap));
        }
        return params;
    }

    /**
     * 头寸调用方法，需处理异常，可使用PositionUtil.ajaxErrorReturn解析异常
     * 返回码 300|。。。  表示余额不足，需前端确认
     * 返回码 410|。。。  表示通信息或网络超时
     * 返回码 420|。。。  头寸引擎返回不成功，具体参考返回信息
     *
     * @param req
     * @throws PositionException
     */
    protected  Map<String, Object> batchRevoke(Object req) throws PositionException {
        //
        logger.info("batchRevoke发送头寸报文:" + JsonUtils.object2Json(req));
        //处理seqno
        Map<String, Object> params = JsonUtils.stringToMap(JsonUtils.object2Json(req));
//        Long seqno = (long) Arrays.deepHashCode(new Object[]{params.get("totalTradeId"), params.get("modelType"), new Date().getTime(), Thread.currentThread().getId()});
        String seqno =IDGenerator.generate();
        params.put("seqno", seqno);
        String res = batchRevokePrivate(params);
        logger.info("batchRevoke头寸返回数据:" + res);
        Map msgMap = JsonUtils.json2Object(res, Map.class);
        if (msgMap == null) {
            throw new PositionException("batchRevoke头寸返回数据为空！");
        } else if (!StringUtil.equals("success", msgMap.get("code") + "")) {
            throw new PositionException(JsonUtils.object2Json(msgMap));
        }
        return params;
    }

    /**
     * 从头寸引擎查询现金持仓余额
     *
     * @param param
     * @return
     */
    protected  String queryPosition(Object param) {
        logger.info("发送头寸查询报文:" + JsonUtils.object2Json(param));
        String returnStr = "";
        try {
            returnStr = httpPostRequest(positionUrl + "/position/endpoint/queryPosition", param);
        } catch (Exception e) {
//            logger.error("查询报错",e);
        }
        logger.info("头寸查询返回报文:positionUrl=" + positionUrl + "--结果=" + returnStr);
        return returnStr;

    }

    /**
     * 批处理发送
     * @param req
     * @throws PositionException
     */
    protected  void sendBatch(Object req) throws PositionException {
        Map<String, Object> params = JsonUtils.stringToMap(JsonUtils.object2Json(req));
        logger.info("发送头寸报文:" + JsonUtils.object2Json(params));
        String res = sendBatchRequest(params);
        logger.info("头寸返回数据:" + res);
        Map msgMap = JsonUtils.json2Object(res, Map.class);
        if (msgMap == null) {
            throw new PositionException("头寸返回数据为空！");
        } else if (!StringUtil.equals("success", msgMap.get("code") + "")) {
            throw new PositionException(JsonUtils.object2Json(msgMap));
        }
    }

    /**
     * 获取批处理日志
     * @param req
     * @return
     * @throws PositionException
     */
    protected  Map<String, Object> getBatchLog(Object req) throws PositionException {
        Map<String, Object> params = JsonUtils.stringToMap(JsonUtils.object2Json(req));
        logger.info("发送头寸报文:" + JsonUtils.object2Json(params));
        String res = getBatchLogRequest(params);
        logger.info("头寸返回数据:" + res);
        Map<String, Object> msgMap = JsonUtils.json2Object(res, Map.class);
        if (msgMap == null) {
            throw new PositionException("头寸返回数据为空！");
        } else if (!StringUtil.equals("success", msgMap.get("code") + "")) {
            throw new PositionException(JsonUtils.object2Json(msgMap));
        }
        return msgMap;
    }


    /**
     * 作废
     *
     * @param param
     * @return
     */
    private  String delete(Map<String, Object> param) {
        try{
            return httpPostRequest(positionUrl + "/position/endpoint/delete", param);
        }catch(PositionHttpRuntimeException ioException){
            logger.error("调用头寸报错io：", ioException);
        } catch (Exception e) {
            // 报错发送回滚头寸报文
            logger.error("调用头寸报错", e);
            roll((String) param.get("seqno"), (String) param.get("modelType"), e.getMessage());
        }
        return null;
    }

    /**
     * 撤销
     *
     * @param param
     * @return
     */
    private  String revoke(Map<String, Object> param) {
        try{
            return httpPostRequest(positionUrl + "/position/endpoint/revoke", param);
        }catch(PositionHttpRuntimeException ioException){
            logger.error("调用头寸报错io：", ioException);
        } catch (Exception e) {
            // 报错发送回滚头寸报文
            logger.error("调用头寸报错", e);
            roll((String) param.get("seqno"), (String) param.get("modelType"), e.getMessage());
        }
        return null;
    }

    /**
     * 回滚
     *
     * @param param
     * @return
     */
    private  String rollHttp(Object param) {
        return httpPostRequest(positionUrl + "/position/endpoint/roll", param);
    }

    private  void rollMq(Object param) {
         positionMqRequest.roll(param);
    }

//    /**
//     * 从头寸引擎查询头寸余额
//     *
//     * @param param
//     * @return
//     */
//    @Deprecated
//    protected  String query(Object param) {
//        return httpPostRequest(positionUrl + "/position/search/balance/etrSecPositionRule", param);
//    }


    /**
     * 批量关闭
     * @param param
     * @return
     */
    private  String batchClosePrivate(Map<String, Object> param) {
        try{
            return httpPostRequest(positionUrl + "/position/endpoint/batchClose", param);
        }catch(PositionHttpRuntimeException ioException){
            logger.error("调用头寸报错io：", ioException);
        } catch (Exception e) {
            // 报错发送回滚头寸报文
            logger.error("调用头寸报错", e);
            roll((String) param.get("seqno"), (String) param.get("modelType"), e.getMessage());
        }
        return null;
    }

    /**
     * 批量撤销
     * @param param
     * @return
     */
    private  String batchRevokePrivate(Map<String, Object> param) {
        try{
            return httpPostRequest(positionUrl + "/position/endpoint/batchRevoke", param);
        }catch(PositionHttpRuntimeException ioException){
            logger.error("调用头寸报错io：", ioException);
        } catch (Exception e) {
            // 报错发送回滚头寸报文
            logger.error("调用头寸报错", e);
            roll((String) param.get("seqno"), (String) param.get("modelType"), e.getMessage());
        }
        return null;
    }

    /**
     * 批处理任务调用
     * @param param
     * @return
     */
    private  String sendBatchRequest(Object param) {
        return httpPostRequest(positionUrl + "/position/batch/main/autoGenerationTask", param);
    }

    /**
     * 刷新缓存
     * @param param
     * @return
     */
    protected  String updateBalance(Object param) {
        return httpPostRequest(positionUrl + "/position/batch/main/updateBalance", param);
    }

    /**
     * 获取批处理日志
     * @param param
     * @return
     */
    private  String getBatchLogRequest(Object param) {
        return httpPostRequest(positionUrl + "/position/batch/main/getBatchLog", param);
    }

    /**
     * 占用接口
     * @param param
     * @return
     */
    private  String sendPrivate(Map<String, Object> param) {
        try{
            return httpPostRequest(positionUrl + "/position/endpoint/updatePosition", param);
        }catch(PositionHttpRuntimeException ioException){
            logger.error("调用头寸报错io：", ioException);
        } catch (Exception e) {
            // 报错发送回滚头寸报文
            logger.error("调用头寸报错", e);
            roll((String) param.get("seqno"), (String) param.get("modelType"), e.getMessage());
        }
        return null;
    }

}

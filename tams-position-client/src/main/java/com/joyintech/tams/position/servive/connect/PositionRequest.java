package com.joyintech.tams.position.servive.connect;

import com.joyintech.tams.position.exception.PositionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PositionRequest {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(PositionRequest.class);

    @Autowired
    private PositionHttpRequest positionHttpRequest;

    @Autowired
    private PositionMqRequest positionMqRequest;

    /**
     * 头寸调用方法，需处理异常，可使用PositionUtil.ajaxErrorReturn解析异常
     * 返回码 300|。。。  表示余额不足，需前端确认
     * 返回码 410|。。。  表示通信息或网络超时
     * 返回码 420|。。。  头寸引擎返回不成功，具体参考返回信息
     *
     * @param req
     * @throws PositionException
     */
    public  Map<String, Object> send(Object req) throws PositionException {
        return positionHttpRequest.send(req);
    }

    /**
     * 刷新缓存
     * @param param
     */
    public  void updateBalance(Object param) {
        positionHttpRequest.updateBalance(param);
    }

    /**
     * 回滚接口
     * @param revokeId
     * @param modelType
     * @param errorMsg
     */
    public  void roll(String revokeId, String modelType, String errorMsg) {
        positionMqRequest.roll(revokeId,modelType,errorMsg);
    }

    /**
     * 实际是roll接口
     * @param revokeId
     * @param modelType
     * @param errorMsg
     */
    public  void sendRevoke(String revokeId, String modelType, String errorMsg) {
        positionMqRequest.sendRevoke(revokeId,modelType,errorMsg);
    }

    /**
     * 作废
     * @param workFlowId
     * @param modelType
     * @param busiType
     * @param ps
     * @return
     * @throws PositionException
     */
    public  Map<String, Object> doDeleteByWorkFlowId(String workFlowId, String modelType, String busiType, String ps) throws PositionException {
        return positionHttpRequest.doDeleteByWorkFlowId(workFlowId,modelType,busiType,ps);
    }

    /**
     * 撤销接口
     * @param revokeId
     * @param modelType
     * @param busiType
     * @param ps
     * @return
     * @throws PositionException
     */
    public  Map<String, Object> dorevoke(String revokeId, String modelType, String busiType, String ps) throws PositionException {
        return positionHttpRequest.dorevoke(revokeId,modelType,busiType,ps);
    }


    /**
     * 批量关闭
     *
     * @param req
     * @throws PositionException
     */
    public  Map<String, Object> batchClose(Object req) throws PositionException {
        return positionHttpRequest.batchClose(req);
    }

    /**
     * 批量撤销
     *
     * @param req
     * @throws PositionException
     */
    public  Map<String, Object> batchRevoke(Object req) throws PositionException {
        return positionHttpRequest.batchRevoke(req);
    }

    /**
     * 从头寸引擎查询现金持仓余额
     *
     * @param param
     * @return
     */
    public  String queryPosition(Object param) {
        return positionHttpRequest.queryPosition(param);

    }

    /**
     * 批处理调用
     * @param req
     * @throws PositionException
     */
    public  void sendBatch(Object req) throws PositionException {
        positionHttpRequest.sendBatch(req);
    }

    /**
     * 获取批处理日志
     * @param req
     * @return
     * @throws PositionException
     */
    public  Map<String, Object> getBatchLog(Object req) throws PositionException {
        return  positionHttpRequest.getBatchLog(req);
    }

}

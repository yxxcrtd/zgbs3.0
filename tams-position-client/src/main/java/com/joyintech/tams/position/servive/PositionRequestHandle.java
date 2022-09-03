package com.joyintech.tams.position.servive;

import com.joyintech.tams.position.exception.PositionException;
import com.joyintech.tams.position.servive.connect.PositionRequest;
import com.joyintech.tams.position.util.ThreadLocalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PositionRequestHandle {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(PositionRequestHandle.class);

    @Autowired
    private PositionRequest positionRequest;

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
        Map<String, Object> param = positionRequest.send(req);
        ThreadLocalUtil.setLocalCache(param);
        return param;
    }

    /**
     * 回滚接口
     * @param revokeId
     * @param modelType
     * @param errorMsg
     */
    public  void roll(String revokeId, String modelType, String errorMsg) {
        positionRequest.roll(revokeId,modelType,errorMsg);
    }

    /**
     * 刷新缓存
     * @param param
     */
    public  String updateBalance(Object param) {
        return positionRequest.updateBalance(param);
    }



    /**
     * 实际是roll接口
     * @param revokeId
     * @param modelType
     * @param errorMsg
     */
    public  void sendRevoke(String revokeId, String modelType, String errorMsg) {
        positionRequest.sendRevoke(revokeId,modelType,errorMsg);
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
        Map<String, Object> param = positionRequest.doDeleteByWorkFlowId(workFlowId,modelType,busiType,ps);
        ThreadLocalUtil.setLocalCache(param);
        return param;
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
        Map<String, Object> param = positionRequest.dorevoke(revokeId,modelType,busiType,ps);
        ThreadLocalUtil.setLocalCache(param);
        return param;
    }


    /**
     * 批量关闭
     *
     * @param req
     * @throws PositionException
     */
    public  Map<String, Object> batchClose(Object req) throws PositionException {
        Map<String, Object> param = positionRequest.batchClose(req);
        ThreadLocalUtil.setLocalCache(param);
        return param;
    }

    /**
     * 批量撤销
     *
     * @param req
     * @throws PositionException
     */
    public  Map<String, Object> batchRevoke(Object req) throws PositionException {
        Map<String, Object> param = positionRequest.batchRevoke(req);
        ThreadLocalUtil.setLocalCache(param);
        return param;
    }

    /**
     * 从头寸引擎查询现金持仓余额
     *
     * @param param
     * @return
     */
    public  String queryPosition(Object param) {
        return positionRequest.queryPosition(param);

    }

    /**
     * 批处理调用
     * @param req
     * @throws PositionException
     */
    public  void sendBatch(Object req) throws PositionException {
        positionRequest.sendBatch(req);
    }

    /**
     * 获取批处理日志
     * @param req
     * @return
     * @throws PositionException
     */
    public  Map<String, Object> getBatchLog(Object req) throws PositionException {
        return  positionRequest.getBatchLog(req);
    }

    /**
     *
     * @return
     * @throws PositionException
     */
    public  Map<String, Object> threadRollAll() throws PositionException {
        ArrayList<ArrayList<String>> allist = ThreadLocalUtil.getTarget();
        for(ArrayList<String> slist:allist){
            roll(slist.get(0),slist.get(1),"");
        }
        ThreadLocalUtil.cleanLocalCache();
        return null;
    }

}

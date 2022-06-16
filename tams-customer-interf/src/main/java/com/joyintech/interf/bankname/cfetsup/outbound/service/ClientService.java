package com.joyintech.interf.bankname.cfetsup.outbound.service;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.interf.bankname.cfetsup.outbound.*;
import com.joyintech.interf.bankname.cfetsup.outbound.dao.CfetsUpCommonMapper;
import com.joyintech.interf.bankname.cfetsup.outbound.dto.ICfetsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClientService {

    @Autowired
    private CfetsUpCommonMapper cfetsUpCommonMapper;

    /**
     * 日志
     */
    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 服务端endPoint
     */
    String serverLocation;

    /**
     * 流程生效自动发送报价
     *
     * @return 发送结果
     * @throws Exception 发送异常
     */
    public CallReturn send(ICfetsDto dto, String operId, String methodName) {
        // 流程生效自动发送，sendmark尚未初始化 值为null
        return this.reSend(dto, null, operId, methodName);
    }

    /**
     * 手动点击'再次发送' 发送报价
     *
     * @param param    指令信息
     * @param sendMark 发送标志,每次发送对报价表进行+1操作，防止重复发送
     * @return 发送结果
     * @throws Exception 发送异常
     */
    @SuppressWarnings("unchecked")
    public CallReturn reSend(ICfetsDto param, Integer sendMark, String operId, String methodName) {
        try {
            // 校验
            this.validate(param);
        } catch (Exception e) {
            return CallReturn.failed(e.getLocalizedMessage());
        }

        String tableName = param.getClass().getAnnotation(Table.class).value();
        try {
            // 锁定更新
            this.lock(param, sendMark, operId);
            // 发送
            OutBoundData data = new OutBoundData();
            data.setSeqno(param.getSeqno());
            data.setOperId(operId);
            data.setClassSimpleName(param.getClass().getSimpleName());
            data.setBusinessType(param.getBusinessType());
            data.setIsResend(param.getIsResend());
            //判断其为对话报价还是请求报价
            String json =null;
            if(StringUtil.equals("FxSecdialogQuote",data.getClassSimpleName())){
                 json = (String) post(data, methodName,"FxSecdialogQuote");
            }else if(StringUtil.equals("FxSecRequestQuote",data.getClassSimpleName())){
                 json = (String) post(data, methodName,"FxSecRequestQuote");
            }else if(StringUtil.equals("FxSecrepoQuote",data.getClassSimpleName())){
                json = (String) post(data, methodName,"FxSecrepoQuote");
            }
            // 返回信息
            CallReturn callReturn = JSONUtil.json2Object(json, CallReturn.class);
            if (callReturn.isSuccess()) {
                updateSendStatus(param.getSeqno(), ImixConstants.STATUS.SENDSTATUS.SENDING, tableName);
            } else {
                updateSendStatus(param.getSeqno(), ImixConstants.STATUS.SENDSTATUS.FAILED, tableName);
            }

            return callReturn;

        } catch (Exception e) {
            logger.error("message:{}, stack:{}", e.getLocalizedMessage(), e.getStackTrace());

            updateSendStatus(param.getSeqno(), ImixConstants.STATUS.SENDSTATUS.FAILED, tableName);

            return CallReturn.failed("调用远程接口失败! 错误:" + e.getLocalizedMessage());
        }
    }


    private Object post(OutBoundData data, String requestMapper, String sendType) {

        String sr = null;
        logger.info(URLEncoder.encode(JSONUtil.object2Json(data)));
        try {
            sr = HttpRequest.sendPost(this.getServerLocation(sendType) + requestMapper, "msg=" + URLEncoder.encode(JSONUtil.object2Json(data), "UTF-8"));
        } catch (Exception e) {
            throw new BusinessException("调用远程接口失败! 错误:" + e.getLocalizedMessage());
        }

        return sr;
    }

    private void lock(ICfetsDto param, Integer sendMark, String operId) {
        Table table = param.getClass().getAnnotation(Table.class);
        String tableName = table.value();
        Map<String, Object> paras = new HashMap();
        paras.put("table", tableName);
        paras.put("seqno", param.getSeqno());
        paras.put("sendMark", sendMark);
        paras.put("operId", operId);
        cfetsUpCommonMapper.lock(paras);
    }

    private void updateSendStatus(String seqno, String sendstatus, String tableName) {
        Map<String, Object> paras = new HashMap();
        paras.put("seqno", seqno);
        paras.put("sendstatus", sendstatus);
        paras.put("table", tableName);
        cfetsUpCommonMapper.updateSendStatus(paras);
    }

    private void validate(ICfetsDto param) {
        Table table = param.getClass().getAnnotation(Table.class);
        if (table == null) {
            throw new BusinessException("请在dto类上加上@table注解，填入相关的表名!");
        }

        checkButton();

        Assert.hasLength(table.value(), "dto类table注解的value不能为空!");

        Assert.hasLength(getServerLocation(param.getClass().getSimpleName()), "请在子类实现getEndPoint方法!");

        // 检查报价是否有已经发送成功
        checkIfSuccessfulSended(param.getSeqno());
        // 检查3分钟内是否有发送的报价, 有则不允许发送
        checkSendInterval(param.getSeqno());
    }

    private void checkButton() {

    }

    private void checkIfSuccessfulSended(String seqno) {
        Integer successfulSendedCount = cfetsUpCommonMapper.getSuccessfulSendedCount(seqno);
        if (successfulSendedCount > 0) {
            throw new BusinessException("该笔报价已经发送成功，不允许再次发送!");
        }

    }

    private void checkSendInterval(String seqno) {
        Integer count = cfetsUpCommonMapper.getSendCountWithinInterval(seqno);
        if (count > 0) {
            throw new BusinessException("该笔报价最近已发送过请求，请稍后尝试!");
        }
    }

    public String getServerLocation(String sendType) {
        if (StringUtil.equals("FxSecrepoQuote",sendType)) {//回购新平台
            return this.serverLocation = "http://localhost:8099/cfetsrepo/secrepoQuote/";
        } else if(StringUtil.equals("FxSecdialogQuote",sendType)) {//现券对话报价新平台
            return this.serverLocation = "http://localhost:8090/cfets/dialogQuote/";
        }else if(StringUtil.equals("FxSecRequestQuote",sendType)) {//请求报价新平台  Sonar Fix modify by huwei 220607
            return this.serverLocation = "http://localhost:8090/cfets/dialogQuote/";
        }
        return null;
    }

}

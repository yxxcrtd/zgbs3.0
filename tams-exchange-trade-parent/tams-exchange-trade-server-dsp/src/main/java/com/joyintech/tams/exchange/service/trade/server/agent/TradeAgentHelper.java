package com.joyintech.tams.exchange.service.trade.server.agent;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.SpringContextUtils;
import com.joyintech.tams.exchange.service.trade.dao.domain.TerminalAgentInfo;
import com.joyintech.tams.exchange.service.trade.server.agent.ufx.UfxTradeAgent;
import com.joyintech.tams.exchange.service.trade.service.TerminalAgentInfoService;
import com.joyintech.tams.exchange.service.trade.util.http.HttpClientUtil;
import com.joyintech.tams.exchange.service.trade.util.http.HttpParameter;
import com.joyintech.tams.exchange.service.trade.util.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lifangpeng
 * @date 2021/10/9
 */
@Component
public class TradeAgentHelper {
    private static final Logger logger = LoggerFactory.getLogger(TradeAgentHelper.class);

    @Autowired
    protected TradeAgentManager tradeAgentManager;
    @Autowired
    protected AccountManager accountManager;

    /**
     * 访问接口平台的地址
     */
    @Value("${agent.terminal.host}")
    private String host;
    @Value("${agent.terminal.url}")
    private String url;

    protected AtomicLong requestID = new AtomicLong(1);

    public long generateReqId() {
        return requestID.incrementAndGet();
    }

    public synchronized ResultBean doRequest(TerminalAgentInfo agentInfo, Object message) {
        ResultBean resultBean=new ResultBean();
        String accountId = agentInfo.getAccountId();
        boolean loginStatus = tradeAgentManager.getAgentLoginStatus(accountId);
        if (loginStatus && message instanceof Map && "ufx".equals(agentInfo.getAgentType())) {
            Map map = (Map) message;
            map.put("password", ((UfxTradeAgent) SpringContextUtils.getBean("ufx")).getAccount(accountId).getTradePwd());
        }
        if (!loginStatus && agentInfo.getFunctionCode() != 4) {
            return new ResultBean(ErrorCode.ACCOUNT_NOT_LOGIN);
        }

        try {
            HttpResult result = doRequest(agentInfo.getGpCode(), agentInfo.getTriggerCode(), message);
            AgentResult agentResult = JsonUtils.getObjectFromJson(result.getResult(), AgentResult.class);
            JSONObject jsonObject = (JSONObject) agentResult.getData();
            if (null != jsonObject) {
                String data = JsonUtils.getJsonFromObject(jsonObject.get("data"));
                resultBean = JsonUtils.getObjectFromJson(data, ResultBean.class);
            }

            // region 异常处理
            if (null == jsonObject) {
                resultBean.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
                resultBean.setErrorMsg(agentResult.getMessage());
            }

            JSONObject jData = null;
            // xiazelei 20220402 error_info判断登录过期时使用；
            // "error_info": "[330030][登录已过期，请重新登录]\r\n[client_id=030800006116,fund_account=21506238,error_no_t=-17]\n",
            if (resultBean.getData() != null && resultBean.getData() instanceof JSONObject) {
                jData = (JSONObject) resultBean.getData();
            }
            int errorCode = resultBean.getErrorCode();
            if (errorCode == ErrorCode.ACCOUNT_NOT_LOGIN.value
                    || StringUtils.contains(resultBean.getErrorMsg(), "证书已经过期")
                    || (jData != null && StringUtils.contains(jData.getString("error_info"), "登录已过期"))
            ) {
                tradeAgentManager.addAgentLoginStatus(accountId, false);
                logger.error("{}终端登录状态异常：{}，{}", agentInfo.getAgentType(), accountId, JsonUtils.getJsonFromObject(resultBean));
            }
            // endregion
        } catch (Exception e) {
            logger.error("调用http接口异常：errorMsg={}", e.getMessage(), e);
            resultBean = new ResultBean(ErrorCode.TERMINAL_API_ERROR.value, e.getMessage());
        }

        return resultBean;
    }

    public HttpResult doRequest(String gpCode, String triggerCode, Object message) throws Exception {
        HttpClientUtil clientUtil = HttpClientUtil.create();
        clientUtil.url(host + url);
        HttpParameter parameter = buildHttpParameter(gpCode, triggerCode, message);
        clientUtil.context(JsonUtils.getJsonFromObject(parameter));
        return clientUtil.send();
    }

    private HttpParameter buildHttpParameter(String gpCode, String triggerCode, Object message) {
        HttpParameter parameter = new HttpParameter();
        parameter.setParameter(message);
        parameter.setTrigger(triggerCode);
        parameter.setGpcode(gpCode);
        parameter.setPreseqno(String.valueOf(generateReqId()));
        parameter.setPredate(DateUtil.format(LocalDateTime.now(), "yyyy-mm-dd"));
        parameter.setAsync(false);
        return parameter;
    }
}

package com.joyintech.tams.exchange.service.trade.server.agent.kcbpu;

import java.util.Map;

public class KCBPUAccount {

    private String accountId;
    private String agentType;



    /**
     * 交易市场
     */
    private String market;
    /**
     * 股东代码
     */
    private Map<String, String> secuIds;
    /**
     * 股东姓名
     */
    private String secuName;
    /**
     * 缺省资产账号
     */
    private String account;
    /**
     * 客户代码
     */
    private String userCode;
    /**
     * 客户姓名
     */
    private String userName;
    /**
     * 开户分支
     */
    private String branch;
    /**
     * 会话句柄
     */
    private String session;
    /**
     * 外部机构
     */
    private String extInst;

    /**
     * 外部机构名称
     */
    private String extInstName;

    /**
     * 证件有效期 N:在有效期内 Y:超过有效期
     */
    private String expiryId;
    /**
     * 密码过期 0：未过期，1：交易密码已过期，
     * 2：资金密码已过期，3：交易密码和资金密码已过期
     */
    private String pwdTime;
    /**
     * 外部类型 C or D 表示融资融劵客户
     */
    private String extCls;

    /**
     * 公共头部信息
     */
    private Map<String, String> headMessages;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

}

package com.joyintech.tams.exchange.service.trade.scheduled;

import com.google.common.collect.Lists;
import com.joyintech.tams.exchange.common.bean.AccountLoginReqBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.ServerExtendInfo;
import com.joyintech.tams.exchange.common.bean.investment.BusinessDepartmentBean;
import com.joyintech.tams.exchange.common.bean.investment.CapitalAccountBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.TerminalMapping;
import com.joyintech.tams.exchange.datacommon.db.service.TerminalMappingService;
import com.joyintech.tams.exchange.service.trade.management.service.InvestmentManagement;
import com.joyintech.tams.exchange.service.trade.processor.constants.AccountLoginStatus;
import com.joyintech.tams.exchange.service.trade.server.agent.AccountManager;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 账号登录任务
 */

@Component
@EnableScheduling
public class LoginJob {
    private static final Logger logger = LoggerFactory.getLogger(LoginJob.class);

    @Autowired
    private TerminalMappingService terminalMappingService;
    @Autowired
    private InvestmentManagement investmentManagement;
    @Autowired
    private TradeAgentManager tradeAgentManager;
    @Autowired
    private AccountManager accountManager;

    @Value("${terminal.default:default}")
    private String defaultTerminal;

    @Scheduled(fixedRateString = "${scheduled.login.fixedRate}")
    public void accountLogin() {
        Map<String, TradeAgent> tradingAgents = tradeAgentManager.getTradingAgents();
        if (tradingAgents.size() == 0) {
            logger.info("no terminal established with trade");
            return;
        }

        List<TerminalMapping> terminalMappings = terminalMappingService.findAll().stream()
                .filter(o -> tradingAgents.containsKey(o.getTerminalName()))
                .sorted(Comparator.comparingInt(TerminalMapping::getTerminalWeight).reversed())
                .collect(Collectors.toList());

        Set<String> accountSet = terminalMappings.stream().map(TerminalMapping::getAccountId).collect(Collectors.toSet());
        // 获取账号
        List<CapitalAccountBean> capitalAccountBeanList = investmentManagement.queryCapitalAccountAll().stream()
                .filter(o -> accountSet.contains(o.getAcctNo())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(capitalAccountBeanList)) {
            return;
        }
        for (CapitalAccountBean capitalAccountBean : capitalAccountBeanList) {
            Account account = capitalAccountBean.createAccount();
            if (Objects.nonNull(account)) {
                loginAccount(tradingAgents, terminalMappings, account);
            }
        }
    }

    public ResultBean loginAccount(Map<String, TradeAgent> tradingAgents, List<TerminalMapping> terminalMappings, Account account) {
        terminalMappings = terminalMappings.stream()
                .filter(o -> tradingAgents.containsKey(o.getTerminalName()) && StringUtils.equals(account.getId(), o.getAccountId()))
                .collect(Collectors.toList());
        Set<String> accountSet = terminalMappings.stream().map(TerminalMapping::getAccountId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(terminalMappings)) {
            for (TerminalMapping terminalMapping : terminalMappings) {
                String accountId = terminalMapping.getAccountId();
                boolean loginStatus = tradeAgentManager.getAgentLoginStatus(accountId);
                if (loginStatus) {
                    if (!accountSet.contains(accountId)) {
                        tradeAgentManager.addAgentLoginStatus(accountId, false);
                        TradeAgent tradeAgent = tradeAgentManager.getAccountAgent(accountId);
                        if (null != tradeAgent) {
                            tradeAgent.removeAccount(accountId);
                            tradeAgent.setStatus(accountId, AccountLoginStatus.NOT_LOGGED);
                        }
                        tradeAgentManager.removeAccountAgent(accountId);
                        accountManager.clearAccountMaps(Lists.newArrayList(accountId));
                    }
                    continue;
                }
                if (!accountSet.contains(accountId)) {
                    continue;
                }
                try {
                    return loginAccount(account, terminalMapping, tradingAgents);
                } catch (Exception e) {
                    logger.error("{}登录失败：{}", accountId, e.getMessage(), e);
                }
            }
        } else {
            // 如果登录的终端中包含默认终端，则登录账号到默认终端
            TradeAgent agent = tradingAgents.get(defaultTerminal);
            if (Objects.nonNull(agent)) {
                // 匹配该账号对应的营业部信息
                BusinessDepartmentBean businessDepartmentBean = investmentManagement.queryBusinessDepartment(account.getBrokerId()).iterator().next();
                ServerExtendInfo serverExtendInfo = businessDepartmentBean.createServerExtendInfo();
                if (Objects.nonNull(serverExtendInfo)) {
                    account.setAgentType(serverExtendInfo.getAgentType());
                    account.setBrokerName(serverExtendInfo.getBrockerName());
                    account.setDspUrl(serverExtendInfo.getDspUrl());
                    AccountLoginReqBean accountLoginReqBean = new AccountLoginReqBean(serverExtendInfo, account);
                    logger.info("login defaultTerminal account is {}", account.getId());
                    return agent.reqLoginAccount(accountLoginReqBean);
                }
            }
        }
        logger.info("account [{}] not found broker [{}] ", account.getId(), account.getBrokerId());
        return new ResultBean(ErrorCode.ACCOUNT_NOT_FOUND_BROKER);
    }

    public ResultBean loginAccount(Account account, TerminalMapping terminalMapping, Map<String, TradeAgent> tradingAgents) {
        // 匹配该账号对应的营业部信息
        BusinessDepartmentBean businessDepartmentBean = investmentManagement.queryBusinessDepartment(account.getBrokerId()).iterator().next();
        ServerExtendInfo serverExtendInfo = businessDepartmentBean.createServerExtendInfo();
        if (Objects.nonNull(serverExtendInfo)) {
            String terminalName = terminalMapping.getTerminalName();
            account.setAgentType(terminalName);
            account.setBrokerName(serverExtendInfo.getBrockerName());
            account.setDspUrl(serverExtendInfo.getDspUrl());
            AccountLoginReqBean accountLoginReqBean = new AccountLoginReqBean(serverExtendInfo, account);
            TradeAgent agent = tradingAgents.get(terminalName);
            if (Objects.nonNull(agent)) {
                logger.info("login {} terminal account is {}", terminalName, account.getId());
                return agent.reqLoginAccount(accountLoginReqBean);
            }
        }
        logger.info("account [{}] not found broker [{}] ", account.getId(), account.getBrokerId());
        return new ResultBean(ErrorCode.ACCOUNT_NOT_FOUND_BROKER);
    }
}

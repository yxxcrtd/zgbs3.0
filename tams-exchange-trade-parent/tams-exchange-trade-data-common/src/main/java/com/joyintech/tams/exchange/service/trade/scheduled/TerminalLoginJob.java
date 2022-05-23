package com.joyintech.tams.exchange.service.trade.scheduled;

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
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentManager;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 账号登录任务
 */
@Component
@EnableScheduling
@ConditionalOnProperty(value = "agent.active", matchIfMissing = true, havingValue = "false")
public class TerminalLoginJob implements LoginJob {
    private static final Logger logger = LoggerFactory.getLogger(TerminalLoginJob.class);

    @Autowired
    private TerminalMappingService terminalMappingService;
    @Autowired
    private InvestmentManagement investmentManagement;
    @Autowired
    private TradeAgentManager tradeAgentManager;

    @Value("${terminal.default:default}")
    private String defaultTerminal;

    @Scheduled(fixedRateString = "${scheduled.login.fixedRate}")
    public void accountLogin() {
        Map<String, TradeAgent> tradingAgents = tradeAgentManager.getTradingAgents();
        // 确认是否有连接成功的终端
        if (tradingAgents.size() == 0) {
            logger.info("no terminal established with trade, please wait...");
            return;
        }
        List<TerminalMapping> terminalMappingList = terminalMappingService.findAll();
        Set<String> accountIdSet = terminalMappingList.stream().map(TerminalMapping::getAccountId).collect(Collectors.toSet());
        // 获取账号
        List<CapitalAccountBean> capitalAccountBeanList = investmentManagement.queryCapitalAccountAll()
                .stream().filter(o -> accountIdSet.contains(o.getAcctNo())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(capitalAccountBeanList)) {
            logger.info("no accountId, please wait...");
        }
        for (CapitalAccountBean capitalAccountBean : capitalAccountBeanList) {
            // 筛选账号
            Account account = capitalAccountBean.createAccount();
            if (Objects.nonNull(account)) {
                // 若账号已经登录到终端，则不用重复登录
                TradeAgent agentByAccountId = tradeAgentManager.findAgentByAccountId(account.getId());
                if (Objects.nonNull(agentByAccountId)) {
                    //logger.info("账号{}已登录，不必重复登录",account.getId());
                    continue;
                }
                loginAccount(tradingAgents, terminalMappingList, account);
            }
        }
    }

    @Override
    public ResultBean loginAccount(Map<String, TradeAgent> tradingAgents, List<TerminalMapping> terminalMappingList, Account account) {
        // 匹配该账号对应的营业部信息
        BusinessDepartmentBean businessDepartmentBean = investmentManagement.queryBusinessDepartment(account.getBrokerId()).iterator().next();
        ServerExtendInfo serverExtendInfo = businessDepartmentBean.createServerExtendInfo();
        //logger.info("通过账号{},获取到营业部信息:{}", account.getId(), JsonUtils.getJsonFromObject(serverExtendInfo));
        if (Objects.nonNull(serverExtendInfo)) {
            account.setAgentType(serverExtendInfo.getAgentType());
            account.setBrokerName(serverExtendInfo.getBrockerName());
            AccountLoginReqBean accountLoginReqBean = new AccountLoginReqBean(serverExtendInfo, account);
            // 找出权重最高的账号对应的终端
            List<TerminalMapping> terminalMappings = terminalMappingList.stream().filter(terminalMapping -> account.getId().equals(terminalMapping.getAccountId()))
                    .sorted(Comparator.comparingInt(TerminalMapping::getTerminalWeight).reversed())
                    .collect(Collectors.toList());
            // 判断该账号应该登录那个终端,优先选择权重高的节点
            if (!terminalMappings.isEmpty()) {
                for (TerminalMapping terminalMapping : terminalMappings) {
                    String terminalName1 = terminalMapping.getTerminalName();
                    TradeAgent agent = tradingAgents.get(terminalName1);
                    if (Objects.nonNull(agent)) {
                        logger.info("login {} account is {}", terminalName1, account.getId());
                        return agent.reqLoginAccount(accountLoginReqBean);
                    }
                }
            } else {
                // 如果登录的终端中包含默认终端，则登录账号到默认终端
                TradeAgent agent = tradingAgents.get(defaultTerminal);
                if (Objects.nonNull(agent)) {
                    logger.info("login defaultTerminal account is {}", account.getId());
                    return agent.reqLoginAccount(accountLoginReqBean);
                }
            }
            return new ResultBean(ErrorCode.TERMINAL_NOT_ONLINE);
        } else {
            logger.info("account [{}] not found broker [{}] ", account.getId(), account.getBrokerId());
            return new ResultBean(ErrorCode.ACCOUNT_NOT_FOUND_BROKER);
        }
    }
}

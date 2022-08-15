package com.joyintech.tams.exchange.common.utils;

import com.joyintech.tams.exchange.common.constant.AgentType;
import com.joyintech.tams.exchange.common.constant.account.AccountType;

public class AccountUtils {

    public static String convertAccountTypeAndAgentType(String accountType) {
        // 兼容投管字典类型和
        if (AccountType.UFX.value.equals(accountType) || AgentType.UFX.value.equals(accountType)) {
            return AgentType.UFX.value;
        } else if (AccountType.KINGDOM_KCBPW.value.equals(accountType) || AgentType.KINGDOM_KCBPW.value.equals(accountType)) {
            return AgentType.KINGDOM_KCBPW.value;
        } else if (AccountType.JSD.value.equals(accountType) || AgentType.JSD.value.equals(accountType)) {
            return AgentType.JSD.value;
        } else if (AccountType.KDGateway.value.equals(accountType) || AgentType.KD_GATEWAY.value.equals(accountType)) {
            return AgentType.KD_GATEWAY.value;
        } else if (AccountType.CTP.value.equals(accountType) || AgentType.CTP.value.equals(accountType)) {
            return AgentType.CTP.value;
        }

        return AgentType.MOCK.value;
    }
}

package com.joyintech.tams.exchange.common.utils;


import cn.hutool.core.util.StrUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import org.apache.commons.lang3.StringUtils;

public class EntrustPropUtils {

    public static String getEntrustBs(Instruction instruction) {
        //协议回购
        String ps = instruction.getPs();
        if (EntrustUtils.isPledgegreementRepurchase(instruction)) {
            if (StrUtil.equals(instruction.getFirstRepoPs(), InstructionPs.POSITIVEREPO.value)) {
                return InstructionPs.POSITIVEREPO.entrustBs;
            } else {
                return InstructionPs.REVERSEDREPO.entrustBs;
            }
        }

        return EntrustBsProp.getEntrustBs(ps);
    }


    public static String getEntrustProp(Instruction instruction, String entrustProp) {
        if (null == instruction) {
            return entrustProp;
        }
        //新债改造
        if(false) {//TODO 判断出新债相关业务
            return entrustProp;
        }
        
        //竞价平台
        if (EntrustUtils.isNormalTrade(instruction)) {
            //市价
            if (StringUtil.isNotEmpty(entrustProp) && EntrustProp.isMarketPrice(entrustProp)) {
                return entrustProp;
            } else {
                return decideEntrustProp(instruction.getPs());
            }
        }

        //协议回购
        if (EntrustUtils.isPledgegreementRepurchase(instruction)) {
            return decideEntrustProp(instruction.getPs(), instruction.getTradeMarket());
        }

        //大宗平台、固收平台
        String dealMode = instruction.getDealMode();
        if (StrUtil.isNotEmpty(dealMode)) {
            if (StringUtils.equals(DictCodeListExt3.申报类型_不指定.getValue(), instruction.getDealMode())) {
                return entrustProp;
            } else {
                return decideEntrustProp(dealMode);
            }
        }

        return entrustProp;
    }

    public static String getEntrustProp(Instruction instruction) {
        if (null == instruction) {
            throw BusinessResultException.create(ErrorCode.INSTRUCTIONS_NOT_EXIST);
        }
        //竞价平台
        if (EntrustUtils.isNormalTrade(instruction)) {
            return decideEntrustProp(instruction.getPs());
        }

        //协议回购
        if (EntrustUtils.isPledgegreementRepurchase(instruction)) {
            return decideEntrustProp(instruction.getPs(), instruction.getTradeMarket());
        }

        //大宗平台、固收平台
        String dealMode = instruction.getDealMode();
        if (StrUtil.isNotEmpty(dealMode)) {
            return decideEntrustProp(dealMode);
        }

        return null;
    }

    private static String decideEntrustProp(String code) {
        EntrustProp entrustProp = EntrustBsProp.getEntrustProp(code);
        if (null == entrustProp) {
            throw BusinessResultException.create(ErrorCode.ENTRUST_PROP_NOT_EXIT);
        }
        return entrustProp.value;
    }

    private static String decideEntrustProp(String ps, String tradeMarket) {
        RepoOperation repoOperation = getRepoOperation(ps);
        if (null == repoOperation) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ENTRUST_BS_ERROR);
        }
        boolean isSh = StrUtil.equals(tradeMarket, Market.SHSE.investValue);
        boolean isPos = StrUtil.equals(ps, InstructionPs.POSITIVEREPO.value);
        if (repoOperation == RepoOperation.ESTABLISH) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_TRADE.value : EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_BEGIN.value;
        } else if (repoOperation == RepoOperation.RENEW) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_RENEWAL.value : EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_RENEWAL.value;
        } else if (repoOperation == RepoOperation.RELEASE) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_RELEASE.value : EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value) : null;
        } else if (repoOperation == RepoOperation.CHANGE) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_EXCHANGE.value : EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value) : null;
        } else if (repoOperation == RepoOperation.PRECEDE) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_TERMINATE.value : EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_BUYBACK.value;
        } else if (repoOperation == RepoOperation.TERMINATE) {
            return isSh ? EntrustProp.SH_REPURCHASE_EXPIRE.value : EntrustProp.SZ_REPURCHASE_BUYBACK.value;
        }

        return null;
    }

    private static RepoOperation getRepoOperation(String ps) {
        if (InstructionPs.POSITIVEREPO.value.equals(ps) || InstructionPs.REVERSEDREPO.value.equals(ps)) {
            return RepoOperation.ESTABLISH;
        }
        return RepoOperation.getRepoOperation(ps);
    }
}

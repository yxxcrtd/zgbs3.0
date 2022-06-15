package com.joyintech.tams.exchange.common.utils;


import cn.hutool.core.util.StrUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import org.apache.commons.lang3.StringUtils;

public class EntrustPropUtils {

//    public static String getEntrustBs(Instruction instruction) {
//        //协议回购
//        String ps = instruction.getPs();
//        if (EntrustUtils.isPledgegreementRepurchase(instruction)) {
//            if (StrUtil.equals(instruction.getFirstRepoPs(), InstructionPs.POSITIVEREPO.value)) {
//                return InstructionPs.POSITIVEREPO.entrustBs;
//            } else {
//                return InstructionPs.REVERSEDREPO.entrustBs;
//            }
//        }
//
//        return EntrustBsProp.getEntrustBs(ps);
//    }

    public static String getEntrustBs(Instruction instruction) {
        //协议回购
        if (EntrustUtils.isPledgeAgreementRepurchase(instruction.getBusiTypeTg())) {
            return InstructionPs.getEntrustBs(instruction.getFirstRepoPs());
        }

        return EntrustBsProp.getEntrustBs(instruction.getPs());
    }

    /**
     * 获取委托属性，处理前端可能传入的 FI9-1
     * @param instruction   指令对象
     * @param entrustProp   订单上的委托属性
     * @return  委托属性
     */
    public static String getEntrustPropWithHandleNewBond(Instruction instruction, String entrustProp) {
        return getEntrustProp(instruction, entrustProp).split(StrUtil.DASHED)[0];
    }


    public static String getEntrustProp(Instruction instruction, String entrustProp) {
        if (null == instruction) {
            return entrustProp;
        }
        //业务类型
        final String busiTypeTg = instruction.getBusiTypeTg();
        //申报类型
        final String dealMode = instruction.getDealMode();
        //新债业务
        if (EntrustUtils.isNewBond(busiTypeTg)) {
//            if (StringUtil.isNotEmpty(dealMode)) {
//                return dealMode;
//            }
            return entrustProp;
        }

        //判断是否非交易业务
        if (EntrustUtils.isNotTrade(busiTypeTg)) {
            return decideEntrustProp(instruction.getPs());
        }

        //通用质押式回购
        if (EntrustUtils.isRepurchase(busiTypeTg)) {
            return decideEntrustProp(instruction.getPs());
        }

        //判断是否竞价交易
        if (EntrustUtils.isNormalTrade(busiTypeTg)) {
            //市价
            if (StringUtil.isNotEmpty(entrustProp) && EntrustProp.isMarketPrice(entrustProp)) {
                return entrustProp;
            } else {
                return decideEntrustProp(instruction.getPs());
            }
        }

        //判断是否大宗交易
        if (EntrustUtils.isBulk(busiTypeTg) && StrUtil.isNotEmpty(dealMode)) {
            if (StringUtils.equals(DictCodeListExt3.申报类型_不指定.getValue(), dealMode)) {
                return entrustProp;
            } else {
                return decideEntrustProp(dealMode);
            }
        }

        //判断是否协议现券
        if (EntrustUtils.isAgreementCashBond(busiTypeTg) && StrUtil.isNotEmpty(dealMode)) {
            return decideEntrustProp(dealMode);
        }

        //判断是否协议回购
        if (EntrustUtils.isPledgeAgreementRepurchase(busiTypeTg)) {
            return decideEntrustProp(busiTypeTg, instruction.getPs(), instruction.getTradeMarket());
        }

        //竞价平台
//        if (EntrustUtils.isNormalTrade(instruction)) {
//            //市价
//            if (StringUtil.isNotEmpty(entrustProp) && EntrustProp.isMarketPrice(entrustProp)) {
//                return entrustProp;
//            } else {
//                return decideEntrustProp(instruction.getPs());
//            }
//        }

        //协议回购
//        if (EntrustUtils.isPledgegreementRepurchase(instruction)) {
//            return decideEntrustProp(instruction.getPs(), instruction.getTradeMarket());
//        }

        //大宗平台、固收平台
//        String dealMode = instruction.getDealMode();
//        if (StrUtil.isNotEmpty(dealMode)) {
//            if (StringUtils.equals(DictCodeListExt3.申报类型_不指定.getValue(), instruction.getDealMode())) {
//                return entrustProp;
//            } else {
//                return decideEntrustProp(dealMode);
//            }
//        }

        return entrustProp;
    }

    private static String decideEntrustProp(String code) {
        EntrustProp entrustProp = EntrustBsProp.getEntrustProp(code);
        if (null == entrustProp) {
            throw BusinessResultException.create(ErrorCode.ENTRUST_PROP_NOT_EXIT);
        }
        return entrustProp.value;
    }

    private static String decideEntrustProp(String busiTypeTg, String ps, String tradeMarket) {
        boolean isSh = StrUtil.equals(tradeMarket, Market.SHSE.investValue);
        boolean isPos = StrUtil.equals(ps, InstructionPs.POSITIVEREPO.value);
        if (StringUtil.equals(DictCodeListExt3.投管业务类型_首期.getValue(), busiTypeTg)) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_TRADE.value : EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_BEGIN.value;
        } else if (StringUtil.equals(DictCodeListExt3.投管业务类型_到期续做.getValue(), busiTypeTg)) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_RENEWAL.value : EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_RENEWAL.value;
        } else if (StringUtil.equals(DictCodeListExt3.投管业务类型_解除质押.getValue(), busiTypeTg)) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_RELEASE.value : EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_RELEASE.value;
        } else if (StringUtil.equals(DictCodeListExt3.投管业务类型_中途换券.getValue(), busiTypeTg)) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_EXCHANGE.value : EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value) :  EntrustProp.SZ_REPURCHASE_EXCHANGE.value;
        } else if (StringUtil.equals(DictCodeListExt3.投管业务类型_提前购回.getValue(), busiTypeTg)) {
            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_TERMINATE.value : EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_BUYBACK.value;
        } else if (StringUtil.equals(DictCodeListExt3.投管业务类型_到期购回.getValue(), busiTypeTg)) {
            return isSh ? EntrustProp.SH_REPURCHASE_EXPIRE.value : EntrustProp.SZ_REPURCHASE_BUYBACK.value;
        }

        return null;
    }


//    private static String decideEntrustProp(String ps, String tradeMarket) {
//        RepoOperation repoOperation = getRepoOperation(ps);
//        if (null == repoOperation) {
//            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ENTRUST_BS_ERROR);
//        }
//        boolean isSh = StrUtil.equals(tradeMarket, Market.SHSE.investValue);
//        boolean isPos = StrUtil.equals(ps, InstructionPs.POSITIVEREPO.value);
//        if (repoOperation == RepoOperation.ESTABLISH) {
//            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_TRADE.value : EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_BEGIN.value;
//        } else if (repoOperation == RepoOperation.RENEW) {
//            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_RENEWAL.value : EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_RENEWAL.value;
//        } else if (repoOperation == RepoOperation.RELEASE) {
//            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_RELEASE.value : EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value) : null;
//        } else if (repoOperation == RepoOperation.CHANGE) {
//            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_EXCHANGE.value : EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value) : null;
//        } else if (repoOperation == RepoOperation.PRECEDE) {
//            return isSh ? (isPos ? EntrustProp.SH_REPURCHASE_TERMINATE.value : EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value) : EntrustProp.SZ_REPURCHASE_BUYBACK.value;
//        } else if (repoOperation == RepoOperation.TERMINATE) {
//            return isSh ? EntrustProp.SH_REPURCHASE_EXPIRE.value : EntrustProp.SZ_REPURCHASE_BUYBACK.value;
//        }
//
//        return null;
//    }
//
//    private static RepoOperation getRepoOperation(String ps) {
//        if (InstructionPs.POSITIVEREPO.value.equals(ps) || InstructionPs.REVERSEDREPO.value.equals(ps)) {
//            return RepoOperation.ESTABLISH;
//        }
//        return RepoOperation.getRepoOperation(ps);
//    }
}

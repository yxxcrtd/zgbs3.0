package com.joyintech.tams.exchange.common.utils;

import com.google.common.base.Strings;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.constant.BSFlag;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.Market;

public class IntegratedEntrustUtils {

    //深证债券质押式协议回购
    public static boolean isProtocolBack_SZ(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (EntrustProp.SZ_REPURCHASE_BEGIN.value.equals(entrustProp)
                    || EntrustProp.SZ_REPURCHASE_RENEWAL.value.equals(entrustProp)
                    || EntrustProp.SZ_REPURCHASE_BUYBACK.value.equals(entrustProp)
                    || EntrustProp.SZ_REPURCHASE_RELEASE.value.equals(entrustProp)
                    || EntrustProp.SZ_REPURCHASE_EXCHANGE.value.equals(entrustProp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 深证债券质押式协议回购
     * 2C:初始交易
     * 2E:到期结算
     * 2K:到期续做
     * 2O:解除质押
     * 2Q:换券申报
     * 2M:提前终止
     */
    public static boolean isProtocolBack_SZ0(String bsFlag) {
        if (!Strings.isNullOrEmpty(bsFlag)) {
            if (BSFlag.CSJYRRZJ.value.equals(bsFlag)
                    || BSFlag.GHJYFHZJ.value.equals(bsFlag)
                    || BSFlag.DQXZ.value.equals(bsFlag)
                    || BSFlag.JCZY.value.equals(bsFlag)
                    || BSFlag.HQSB.value.equals(bsFlag)
                    || BSFlag.TQZZRRZJ.value.equals(bsFlag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 2V:初始交易确认
     * 2U:初始交易拒绝
     * 2L:到期续做确认
     * 2W:到期续做拒绝
     * 3e:解除质押确认
     * 3f:解除质押拒绝
     * 2Z:换券申报确认
     * 3a:换券申报拒绝
     * 3b:提前终止确认
     * 3d:提前终止拒绝
     */
    public static boolean isProtocolBack_SZ_Chengjiao(String bsFlag) {
        if (!Strings.isNullOrEmpty(bsFlag)) {
            if (BSFlag.CJSBQR.value.equals(bsFlag)
                    || BSFlag.CJSBJJ.value.equals(bsFlag)
                    || BSFlag.DQXZSBQR.value.equals(bsFlag)
                    || BSFlag.DQXZSBJJ.value.equals(bsFlag)
                    || BSFlag.JCZYSBQR.value.equals(bsFlag)
                    || BSFlag.JCZYSBJJ.value.equals(bsFlag)
                    || BSFlag.HQSBQR.value.equals(bsFlag)
                    || BSFlag.HQSBJJ.value.equals(bsFlag)
                    || BSFlag.TQZZRCQR.value.equals(bsFlag)
                    || BSFlag.TQZZSBJJ.value.equals(bsFlag)) {
                return true;
            }
        }
        return false;
    }

    // 上海债券质押式协议回购
    //意向申报
    public static boolean isProtocolBack_SH_YiXiang(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (EntrustProp.SH_REPURCHASE_INTENTION.value.equals(entrustProp))
                return true;
        }
        return false;
    }

    //报价申报
    public static boolean isProtocolBack_SH_BaoJia(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (EntrustProp.SH_REPURCHASE_TRADE.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_RELEASE.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_TERMINATE.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_EXPIRE.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrustProp)) {
                return true;
            }
        }
        return false;
    }

    //成交申报
    public static boolean isProtocolBack_SH_ChenJiao(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value.equals(entrustProp)
                    || EntrustProp.SH_REPURCHASE_TERMINATE_REFUSE.value.equals(entrustProp)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProtocolBack_SH(String entrustProp) {
        return isProtocolBack_SH_ChenJiao(entrustProp) || isProtocolBack_SH_BaoJia(entrustProp) || isProtocolBack_SH_YiXiang(entrustProp);
    }

    public static boolean isProtocolBack(String entrustProp) {
        return isProtocolBack_SH(entrustProp) || isProtocolBack_SZ(entrustProp);
    }

    // 大宗
    public static boolean isBulk(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (EntrustProp.INTENTION.value.equals(entrustProp)
                    || EntrustProp.SET_PRICE.value.equals(entrustProp)
                    || EntrustProp.CONFIRM.value.equals(entrustProp)
                    || EntrustProp.LIMIT_PRICE.value.equals(entrustProp)
                    || EntrustProp.MUTUAL_TRADE.value.equals(entrustProp)
                    || EntrustProp.LARGE_AFTER_CLOSING.value.equals(entrustProp)
                    || EntrustProp.LARGE_AFTER_CLOSING_PRICE.value.equals(entrustProp)
                    || EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value.equals(entrustProp)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIntegratedEntrust(String entrustProp) {
        return isBulk(entrustProp) || isProtocolBack_SZ(entrustProp) || isProtocolBack_SH(entrustProp) || isFixedIncome(entrustProp);
    }

    /***
     * @Description: 大宗_意向_定价
     * @Param: [bsFlag]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/4/1
     */
    public static boolean isBulk_YX_DJ(String bsFlag) {
        if (BSFlag.YXMR.value.equals(bsFlag)
                || BSFlag.YXMC.value.equals(bsFlag)
                || BSFlag.DJMR.value.equals(bsFlag)
                || BSFlag.DJMC.value.equals(bsFlag)) {
            return true;
        }
        return false;
    }

    /***
     * @Description: 大宗_点击成交_成交
     * @Param: [bsFlag]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/4/1
     */
    public static boolean isBulk_DJCJ_CJ(String bsFlag) {
        if (BSFlag.PDMR.value.equals(bsFlag)
                || BSFlag.PDMC.value.equals(bsFlag)
                || BSFlag.QRMR.value.equals(bsFlag)
                || BSFlag.QRMC.value.equals(bsFlag)) {
            return true;
        }
        return false;
    }

    // 上交所固收平台业务
    //委托买卖业务
    public static boolean isFixedIncome_BS(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value))
                return true;
        }
        return false;
    }

    //确定报价报价申报
    public static boolean isFixedIncome_BaoJia(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_BAOJIA_CONFIRM.value))
                return true;
        }
        return false;
    }

    //确定报价成交申报
    public static boolean isFixedIncome_ChenJiao(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_TRADE_CLICK.value)
                    || entrustProp.equals(EntrustProp.SH_FIXEDINCOME_KZHCJSB.value)
                    || entrustProp.equals(EntrustProp.SH_FIXEDINCOME_ZYJCJSB.value)) {
                return true;
            }
        }
        return false;
    }

    /***
     * @Description: 是否固收
     * @Param: [bsFlag]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/3/31
     */
    public static boolean isFixedIncome(String entrustProp) {
        return isFixedIncome_BS(entrustProp) || isFixedIncome_BaoJia(entrustProp) || isFixedIncome_ChenJiao(entrustProp);
    }

    /***
     * @Description: 是否固收_最优价成交申报
     * @Param: [bsFlag]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/3/31
     */
    public static boolean isFixedIncome_ZYJCJ(String bsFlag) {
        if ("4F".equals(bsFlag) || "4G".equals(bsFlag)) {
            return true;
        }
        return false;
    }

    /***
     * @Description: 是否固收_可转换成交申报
     * @Param: [bsFlag]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/3/31
     */
    public static boolean isFixedIncome_KZHCJ(String bsFlag) {
        if ("4H".equals(bsFlag) || "4I".equals(bsFlag)) {
            return true;
        }
        return false;
    }

    /***
     * @Description: 债券质押式协议回购_首期_深交所
     * @Param: [entrustProp]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/4/2
     */
    public static boolean isRepurchaseSQSZ(String entrustProp, String entrustBs, String market) {
        if (Market.SZSE.value.equals(market)//深交所
                && EntrustProp.SZ_REPURCHASE_BEGIN.value.equals(entrustProp)) {//深圳债券质押协议回购初始交易
            return true;
        }
        return false;
    }

    //深圳债券质押协议回购购回交易
    public static boolean isRepurchaseBuybackSZ(String entrustProp, String market) {
        if (Market.SZSE.value.equals(market)//深交所
                && EntrustProp.SZ_REPURCHASE_BUYBACK.value.equals(entrustProp)) {
            return true;
        }
        return false;
    }

    //深圳债券质押协议回购到期续做
    public static boolean isRepurchaseRenewalSZ(String entrustProp, String market) {
        if (Market.SZSE.value.equals(market)//深交所
                && EntrustProp.SZ_REPURCHASE_RENEWAL.value.equals(entrustProp)) {
            return true;
        }
        return false;
    }

    /***
     * @Description: 债券质押式协议回购_深交所
     * @Param: [entrustProp, entrustBs, market]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/4/9
     */
    public static boolean isRepurchaseSZ(String entrustProp, String entrustBs, String market) {
        if (Market.SZSE.value.equals(market)) {//深交所
            if (EntrustProp.SZ_REPURCHASE_BEGIN.value.equals(entrustProp)//深圳债券质押协议回购初始交易
                    || EntrustProp.SZ_REPURCHASE_RENEWAL.value.equals(entrustProp)//深圳债券质押协议回购到期续做
                    || EntrustProp.SZ_REPURCHASE_BUYBACK.value.equals(entrustProp)) {//深圳债券质押协议回购购回交易
                return true;
            }
        }
        return false;
    }

    /***
     * @Description: 债券质押式协议回购_首期_上交所
     * @Param: [entrustProp, entrustBs, market]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/4/2
     */
    public static boolean isRepurchaseSQSH(String entrustProp, String entrustBs, String market) {
        if (Market.SHSE.value.equals(market)) {//上交所
            if (EntrustBs.BUY.value.equals(entrustBs)) {//正回购方
                if (EntrustProp.SH_REPURCHASE_TRADE.value.equals(entrustProp)) {//上海协议回购成交申报
                    return true;
                }
            } else if (EntrustBs.SELL.value.equals(entrustBs)) {//逆回购方
                if (EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value.equals(entrustProp)) {//上海协议回购成交申报确认/拒绝
                    return true;
                }
            }
        }
        return false;
    }

    //上海协议回购非首期
    public static boolean isRepurchaseUNSQSH(String entrustProp, String entrustBs, String market) {
        if (Market.SHSE.value.equals(market)) {//上交所
            if (EntrustBs.BUY.value.equals(entrustBs)) {//正回购方
                if (EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_RELEASE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_TERMINATE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_EXPIRE.value.equals(entrustProp)) {
                    return true;
                }
            } else if (EntrustBs.SELL.value.equals(entrustBs)) {//逆回购方
                if (EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_TERMINATE_REFUSE.value.equals(entrustProp)) {
                    return true;
                }
            }
        }
        return false;
    }

    //上海协议回购到期续做
    public static boolean isRepurchaseRenewalSH(String entrustProp, String entrustBs, String market) {
        if (Market.SHSE.value.equals(market)) {//上交所
            if (EntrustBs.BUY.value.equals(entrustBs)) {//正回购方
                if (EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrustProp)) {//上海协议回购到期续做申报
                    return true;
                }
            } else if (EntrustBs.SELL.value.equals(entrustBs)) {//逆回购方
                if (EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(entrustProp)) {//上海协议回购到期续做确认/拒绝
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * @Description: 债券质押式协议回购_上交所
     * @Param: [entrustProp, entrustBs, market]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/4/3
     */
    public static boolean isRepurchaseSH(String entrustProp, String entrustBs, String market) {
        if (isRepurchaseSQSH(entrustProp, entrustBs, market)) {
            return true;
        }
        if (isRepurchaseUNSQSH(entrustProp, entrustBs, market)) {
            return true;
        }
        return false;
    }

    /***
     * @Description: 债券质押式协议回购_上交所_确认/拒绝
     * @Param: [entrustProp, entrustBs, market]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/4/3
     */
    public static boolean isRepurchase_SH_ConfirmOrRefuse(String entrustProp, String entrustBs, String market) {
        if (Market.SHSE.value.equals(market)) {//上交所
            if (EntrustBs.SELL.value.equals(entrustBs)) {//逆回购方
                if (EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value.equals(entrustProp)
                        || EntrustProp.SH_REPURCHASE_TERMINATE_REFUSE.value.equals(entrustProp)
                ) {//上海协议回购成交申报确认/拒绝
                    return true;
                }
            }
        }
        return false;
    }

    public static String formatFinprodId(String assetType, String stockCode) {
        return assetType + "_" + stockCode;
    }

    /***
     * 是否现券交易协商成交
     */
    public static boolean isNegotiatedTrade(String entrustProp) {
        if (Strings.isNullOrEmpty(entrustProp)) {
            return false;
        }
        return StringUtil.equals(entrustProp, EntrustProp.SZ_XSCJ_XD.value)
                || StringUtil.equals(entrustProp, EntrustProp.SZ_XSCJ_JS.value)
                || StringUtil.equals(entrustProp, EntrustProp.SZ_XSCJ_JJ.value);
    }

    /***
     * 新债 深交所 协商成交下单
     * @param entrustProp
     * @return
     */
    public static boolean isNegotiateXD_SZ(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (EntrustProp.SZ_XSCJ_XD.value.equals(entrustProp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 新债 深交所 协商成交回复
     *
     * @param entrustProp
     * @return
     */
    public static boolean isNegotiateHF_SZ(String entrustProp) {
        if (!Strings.isNullOrEmpty(entrustProp)) {
            if (EntrustProp.SZ_XSCJ_JS.value.equals(entrustProp)
                    || EntrustProp.SZ_XSCJ_JJ.value.equals(entrustProp)) {
                return true;
            }
        }
        return false;
    }
}

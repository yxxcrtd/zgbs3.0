package com.joyintech.tams.exchange.common.utils;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.OrderPlaceBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.constant.joyin.InstructionPriceMode;
import com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.enums.DeliveryMethodEnum;
import com.joyintech.tams.exchange.common.enums.ETFTypeEnum;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class EntrustUtils {
    private static final Set<String> buyBsFlag = new HashSet<>();
    private static final Set<String> sellBsFlag = new HashSet<>();
    private static final Map<String, EntrustProp> entrustPropMap = new HashMap<>();

    static {
        buyBsFlag.add(BSFlag.ZQMR.value);
        buyBsFlag.add(BSFlag.DFMR.value);
        buyBsFlag.add(BSFlag.BFMR.value);
        buyBsFlag.add(BSFlag.JSMR.value);
        buyBsFlag.add(BSFlag.WDMR_HS.value);
        buyBsFlag.add(BSFlag.QEMR.value);
        buyBsFlag.add(BSFlag.ZXMR_H.value);
        buyBsFlag.add(BSFlag.YXMR.value);
        buyBsFlag.add(BSFlag.DJMR.value);
        buyBsFlag.add(BSFlag.QRMR.value);
        buyBsFlag.add(BSFlag.PDMR.value);
        buyBsFlag.add(BSFlag.PSSG.value);
        buyBsFlag.add(BSFlag.JJSG.value);
        buyBsFlag.add(BSFlag.DJSG.value);
        buyBsFlag.add(BSFlag.JJRG.value);
        buyBsFlag.add(BSFlag.JIJISG.value);
        buyBsFlag.add(BSFlag.PGJK.value);
        buyBsFlag.add(BSFlag.PSJK.value);
        buyBsFlag.add(BSFlag.PHDJDZMR.value);
        buyBsFlag.add(BSFlag.PHDJMR.value);
        buyBsFlag.add(BSFlag.LOF_RG.value);
        buyBsFlag.add(BSFlag.LOF_SG.value);
        buyBsFlag.add(BSFlag.ETF_SWSG.value);
        buyBsFlag.add(BSFlag.BJHGMR.value);
        buyBsFlag.add(BSFlag.ZDDSFSBMR.value);
        buyBsFlag.add(BSFlag.XYJYSBMR.value);
        buyBsFlag.add(BSFlag.QDBJSBMR.value);
        buyBsFlag.add(BSFlag.DJCJSBMR.value);
        buyBsFlag.add(BSFlag.ZYJCJSBMR.value);
        buyBsFlag.add(BSFlag.KZHCJSBMR.value);
        buyBsFlag.add(BSFlag.CSJYRRZJ.value);
        buyBsFlag.add(BSFlag.YXRRZJ.value);
        sellBsFlag.add(BSFlag.ZQMC.value);
        sellBsFlag.add(BSFlag.DFMC.value);
        sellBsFlag.add(BSFlag.BFMC.value);
        sellBsFlag.add(BSFlag.JSMC.value);
        sellBsFlag.add(BSFlag.WDMC_HS.value);
        sellBsFlag.add(BSFlag.QEMC.value);
        sellBsFlag.add(BSFlag.ZXMC_H.value);
        sellBsFlag.add(BSFlag.YXMC.value);
        sellBsFlag.add(BSFlag.DJMC.value);
        sellBsFlag.add(BSFlag.QRMC.value);
        sellBsFlag.add(BSFlag.PDMC.value);
        sellBsFlag.add(BSFlag.ZG.value);
        sellBsFlag.add(BSFlag.JJSH.value);
        sellBsFlag.add(BSFlag.PHDJDZMC.value);
        sellBsFlag.add(BSFlag.PHDJMC.value);
        sellBsFlag.add(BSFlag.LOF_SH.value);
        sellBsFlag.add(BSFlag.ETF_SWSH.value);
        sellBsFlag.add(BSFlag.HS.value);
        sellBsFlag.add(BSFlag.ZDDSFSBMC.value);
        sellBsFlag.add(BSFlag.XYJYSBMC.value);
        sellBsFlag.add(BSFlag.QDBJSBMC.value);
        sellBsFlag.add(BSFlag.DJCJSBMC.value);
        sellBsFlag.add(BSFlag.ZYJCJSBMC.value);
        sellBsFlag.add(BSFlag.KZHCJSBMC.value);
        sellBsFlag.add(BSFlag.CSJYRCZJ.value);
        sellBsFlag.add(BSFlag.GHJYFHZJ.value);
        sellBsFlag.add(BSFlag.GHJYSQZJ.value);
        sellBsFlag.add(BSFlag.YXRCZJ.value);

        entrustPropMap.putAll(Arrays.stream(EntrustProp.values()).collect(Collectors.toMap(EntrustProp::getValue, o -> o)));
    }

    // 是否未拆单委托
    public static boolean isSplitOrder(String entrustProp, String remark) {
        if (EntrustProp.isSplitEntrust(entrustProp) && !Strings.isNullOrEmpty(remark)) {
            String[] splitRemark = remark.split("\\|");
            if (splitRemark.length == 4) {
                return BusinessFlag.SPLIT.value.equals(splitRemark[2]);
            }
        }
        return false;
    }

    // 仅针对拆单使用
    public static String convertRemarkToOrderId(String remark) {
        return remark.split("\\|")[1];
    }

    // 仅针对拆单使用
    public static String convertRemarkToInstructionId(String remark) {
        return remark.split("\\|")[0];
    }

    // 仅针对拆单使用
    public static String convertRemarkToEntrustId(String remark) {
        return remark.split("\\|")[3];
    }

    // 是否是 回报即 成交业务
    public static boolean isNoTrade(String entrustProp) {
        return entrustProp.equals(EntrustProp.APPLY.value)
                || entrustProp.equals(EntrustProp.ETF_APPLY.value)
                || entrustProp.equals(EntrustProp.CHANGE_TO_STOCK.value)
                || entrustProp.equals(EntrustProp.JJSHSH.value)
                || entrustProp.equals(EntrustProp.SH_LOF_SUBSCRIBE.value)
                || entrustProp.equals(EntrustProp.PS.value)
                || entrustProp.equals(EntrustProp.HS.value)
                || entrustProp.equals(EntrustProp.ALLOTMENT.value);
    }

    // 是否为意向单
    public static boolean isIntentionEntrust(String entrustProp) {
        return EntrustProp.INTENTION.value.equals(entrustProp);
    }

    public static String getEntrustBs(String bsFlag) {
        // 9B、9S针对平安质押式回购正逆回购业务
        if (buyBsFlag.contains(bsFlag) || BSFlag.ZYCZ.value.equals(bsFlag) || "9B".equals(bsFlag)) {
            return EntrustBs.BUY.value;
        } else if (sellBsFlag.contains(bsFlag) || BSFlag.ZYRZ.value.equals(bsFlag) || "9S".equals(bsFlag)) {
            return EntrustBs.SELL.value;
        }
        return EntrustBs.BUY.value;
    }

    public static String getEntrustType(String bsFlag) {
        return EntrustType.ENTRUST.value;
    }

    public static String getEntrustProp(String bsFlag, String market) {
        if (bsFlag.equals(BSFlag.ZQMR.value) || bsFlag.equals(BSFlag.ZQMC.value)
                || bsFlag.equals(BSFlag.ZQMC.kdValue) || bsFlag.equals(BSFlag.ZQMR.kdValue)) {
            return EntrustProp.NORMAL.value;
        } else if (bsFlag.equals(BSFlag.ZG.value) || bsFlag.equals(BSFlag.ZG.kdValue)) {
            return EntrustProp.CHANGE_TO_STOCK.value;
        } else if (bsFlag.equals(BSFlag.DFMR.value) || bsFlag.equals(BSFlag.DFMC.value)
                || bsFlag.equals(BSFlag.DFMR.kdValue) || bsFlag.equals(BSFlag.DFMC.kdValue)) {
            return EntrustProp.COUNTERPARTY.value;
        } else if (bsFlag.equals(BSFlag.BFMR.value) || bsFlag.equals(BSFlag.BFMC.value)
                || bsFlag.equals(BSFlag.BFMR.kdValue) || bsFlag.equals(BSFlag.BFMC.kdValue)) {
            return EntrustProp.OWN_FIRST.value;
        } else if (bsFlag.equals(BSFlag.JSMR.value) || bsFlag.equals(BSFlag.JSMC.value)
                || bsFlag.equals(BSFlag.JSMR.kdValue) || bsFlag.equals(BSFlag.JSMC.kdValue)) {
            return EntrustProp.IMMEDIATE_TO_CANCEL.value;
        } else if (bsFlag.equals(BSFlag.WDMR_HS.value) || bsFlag.equals(BSFlag.WDMC_HS.value)
                || bsFlag.equals(BSFlag.WDMR_HS.kdValue) || bsFlag.equals(BSFlag.WDMC_HS.kdValue)) {
            return EntrustProp.BEST5_TO_CANCEL.value;
        } else if (bsFlag.equals(BSFlag.QEMR.value) || bsFlag.equals(BSFlag.QEMC.value)
                || bsFlag.equals(BSFlag.QEMR.kdValue) || bsFlag.equals(BSFlag.QEMC.kdValue)) {
            return EntrustProp.FILL.value;
        } else if (bsFlag.equals(BSFlag.ZXMR_H.value) || bsFlag.equals(BSFlag.ZXMC_H.value)
                || bsFlag.equals(BSFlag.ZXMR_H.kdValue) || bsFlag.equals(BSFlag.ZXMC_H.kdValue)) {
            return EntrustProp.BEST5_TO_LIMIT.value;
        } else if (bsFlag.equals(BSFlag.PSSG.value) || bsFlag.equals(BSFlag.JJSG.value) || bsFlag.equals(BSFlag.DJSG.value)
                || bsFlag.equals(BSFlag.PSSG.kdValue) || bsFlag.equals(BSFlag.JJSG.kdValue) || bsFlag.equals(BSFlag.DJSG.kdValue)) {
            return EntrustProp.APPLY.value;
        } else if (bsFlag.equals(BSFlag.YXMR.value) || bsFlag.equals(BSFlag.YXMC.value)
                || bsFlag.equals(BSFlag.YXMR.kdValue) || bsFlag.equals(BSFlag.YXMC.kdValue)) {
            return EntrustProp.INTENTION.value;
        } else if (bsFlag.equals(BSFlag.DJMR.value) || bsFlag.equals(BSFlag.DJMC.value)
                || bsFlag.equals(BSFlag.DJMR.kdValue) || bsFlag.equals(BSFlag.DJMC.kdValue)) {
            return EntrustProp.SET_PRICE.value;
        } else if (bsFlag.equals(BSFlag.QRMR.value) || bsFlag.equals(BSFlag.QRMC.value)
                || bsFlag.equals(BSFlag.QRMR.kdValue) || bsFlag.equals(BSFlag.QRMC.kdValue)) {
            return EntrustProp.CONFIRM.value;
        } else if (bsFlag.equals(BSFlag.PDMR.value) || bsFlag.equals(BSFlag.PDMC.value)
                || bsFlag.equals(BSFlag.PDMR.kdValue) || bsFlag.equals(BSFlag.PDMC.kdValue)) {
            return EntrustProp.MUTUAL_TRADE.value;
        } else if (bsFlag.equals(BSFlag.PSJK.value) || bsFlag.equals(BSFlag.PSFQ.value)
                || bsFlag.equals(BSFlag.PSJK.kdValue) || bsFlag.equals(BSFlag.PSFQ.kdValue)) {
            return EntrustProp.PS.value;
        } else if (bsFlag.equals(BSFlag.ZYRZ.value) || bsFlag.equals(BSFlag.ZYCZ.value)
                || bsFlag.equals(BSFlag.ZYRZ.kdValue) || bsFlag.equals(BSFlag.ZYCZ.kdValue)) {
            return EntrustProp.ZYCRK.value;
        } else if (bsFlag.equals(BSFlag.PGJK.value) || bsFlag.equals(BSFlag.PGJK.kdValue)) {
            return EntrustProp.ALLOTMENT.value;
        } else if (bsFlag.equals(BSFlag.HS.value) || bsFlag.equals(BSFlag.HS.kdValue)) {
            return EntrustProp.HS.value;
        } else if (bsFlag.equals(BSFlag.JJSH.value) || bsFlag.equals(BSFlag.JIJISG.value)
                || bsFlag.equals(BSFlag.JJSH.kdValue) || bsFlag.equals(BSFlag.JIJISG.kdValue)) {
            return EntrustProp.JJSHSH.value;
        } else if (bsFlag.equals(BSFlag.PHDJMR.value) || bsFlag.equals(BSFlag.PHDJMC.value)
                || bsFlag.equals(BSFlag.PHDJMR.kdValue) || bsFlag.equals(BSFlag.PHDJMC.kdValue)) {
            return EntrustProp.NORMAL_AFTER_SET_PRICE.value;
            // 综合交易
        } else if (bsFlag.equals(BSFlag.PHDJDZMR.value) || bsFlag.equals(BSFlag.PHDJDZMC.value)
                || bsFlag.equals(BSFlag.PHDJDZMR.kdValue) || bsFlag.equals(BSFlag.PHDJDZMC.kdValue)) {
            return EntrustProp.LARGE_AFTER_CLOSING_PRICE.value;
        } else if (bsFlag.equals(BSFlag.YXRRZJ.value) || bsFlag.equals(BSFlag.YXRCZJ.value)
                || bsFlag.equals(BSFlag.YXRRZJ.kdValue) || bsFlag.equals(BSFlag.YXRCZJ.kdValue)) {
            return EntrustProp.SH_REPURCHASE_INTENTION.value;
            //
        } else if (bsFlag.equals(BSFlag.CSJYRRZJ.value) && market.equals(Market.SHSE.value)
                || (bsFlag.equals(BSFlag.CSJYRRZJ.kdValue) && market.equals(Market.SHSE.value))) {
            return EntrustProp.SH_REPURCHASE_TRADE.value;
        } else if ((bsFlag.equals(BSFlag.DQXZ.value) || bsFlag.equals(BSFlag.DQXZ.kdValue)) && Market.SHSE.value.equals(market)) {
            return EntrustProp.SH_REPURCHASE_RENEWAL.value;
        } else if (bsFlag.equals(BSFlag.JCZY.value) || bsFlag.equals(BSFlag.JCZY.kdValue)) {
            return EntrustProp.SH_REPURCHASE_RELEASE.value;
        } else if (bsFlag.equals(BSFlag.HQSB.value) || bsFlag.equals(BSFlag.HQSB.kdValue)) {
            return EntrustProp.SH_REPURCHASE_EXCHANGE.value;
        } else if (bsFlag.equals(BSFlag.TQZZRRZJ.value) || bsFlag.equals(BSFlag.TQZZRCZJ.value)
                || bsFlag.equals(BSFlag.TQZZRRZJ.kdValue) || bsFlag.equals(BSFlag.TQZZRCZJ.kdValue)) {
            return EntrustProp.SH_REPURCHASE_TERMINATE.value;
        } else if (bsFlag.equals(BSFlag.GHJYFHZJ.value) && market.equals(Market.SHSE.value)
                || (bsFlag.equals(BSFlag.GHJYFHZJ.kdValue) && market.equals(Market.SHSE.value))) {
            return EntrustProp.SH_REPURCHASE_EXPIRE.value;
        } else if (bsFlag.equals(BSFlag.CJSBQR.value) || bsFlag.equals(BSFlag.CJSBQR.kdValue)) {
            return EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value;
        } else if (bsFlag.equals(BSFlag.CJSBJJ.value) || bsFlag.equals(BSFlag.CJSBJJ.kdValue)) {
            return EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value;
        } else if (bsFlag.equals(BSFlag.DQXZSBQR.value) || bsFlag.equals(BSFlag.DQXZSBQR.kdValue)) {
            return EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value;
        } else if (bsFlag.equals(BSFlag.DQXZSBJJ.value) || bsFlag.equals(BSFlag.DQXZSBJJ.kdValue)) {
            return EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value;
        } else if (bsFlag.equals(BSFlag.JCZYSBQR.value) || bsFlag.equals(BSFlag.JCZYSBQR.kdValue)) {
            return EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value;
        } else if (bsFlag.equals(BSFlag.JCZYSBJJ.value) || bsFlag.equals(BSFlag.JCZYSBJJ.kdValue)) {
            return EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value;
        } else if (bsFlag.equals(BSFlag.HQSBQR.value) || bsFlag.equals(BSFlag.HQSBQR.kdValue)) {
            return EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value;
        } else if (bsFlag.equals(BSFlag.HQSBJJ.value) || bsFlag.equals(BSFlag.HQSBJJ.kdValue)) {
            return EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value;
        } else if (bsFlag.equals(BSFlag.TQZZRCQR.value) || bsFlag.equals(BSFlag.TQZZRCQR.kdValue)) {
            return EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value;
        } else if (bsFlag.equals(BSFlag.TQZZRRQR.value) || bsFlag.equals(BSFlag.TQZZRRQR.kdValue)) {
            return EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value;
        } else if (bsFlag.equals(BSFlag.TQZZSBJJ.value) || bsFlag.equals(BSFlag.TQZZSBJJ.kdValue)) {
            return EntrustProp.SH_REPURCHASE_TERMINATE_REFUSE.value;
        } else if (bsFlag.equals(BSFlag.CSJYRRZJ.value) || bsFlag.equals(BSFlag.CSJYRCZJ.value)
                || bsFlag.equals(BSFlag.CSJYRRZJ.kdValue) || bsFlag.equals(BSFlag.CSJYRCZJ.kdValue)) {
            return EntrustProp.SZ_REPURCHASE_BEGIN.value;
        } else if (bsFlag.equals(BSFlag.GHJYFHZJ.value) || bsFlag.equals(BSFlag.GHJYSQZJ.value)
                || bsFlag.equals(BSFlag.GHJYFHZJ.kdValue) || bsFlag.equals(BSFlag.GHJYSQZJ.kdValue)) {
            return EntrustProp.SZ_REPURCHASE_BUYBACK.value;
        } else if (bsFlag.equals(BSFlag.DQXZRCZJ.value) || bsFlag.equals(BSFlag.DQXZRCZJ.kdValue)
                || (bsFlag.equals(BSFlag.DQXZ.value) && Market.SZSE.value.equals(market))) {
            return EntrustProp.SZ_REPURCHASE_RENEWAL.value;
            // 固收平台
        } else if (bsFlag.equals(BSFlag.QDBJSBMR.value) || bsFlag.equals(BSFlag.QDBJSBMC.value)
                || bsFlag.equals(BSFlag.QDBJSBMR.kdValue) || bsFlag.equals(BSFlag.QDBJSBMC.kdValue)) {
            return EntrustProp.SH_FIXEDINCOME_BAOJIA_CONFIRM.value;
        } else if (bsFlag.equals(BSFlag.DJCJSBMR.value) || bsFlag.equals(BSFlag.DJCJSBMC.value)
                || bsFlag.equals(BSFlag.DJCJSBMR.kdValue) || bsFlag.equals(BSFlag.DJCJSBMC.kdValue)) {
            return EntrustProp.SH_FIXEDINCOME_TRADE_CLICK.value;
        } else if (bsFlag.equals(BSFlag.ZDDSFSBMR.value) || bsFlag.equals(BSFlag.ZDDSFSBMC.value)
                || bsFlag.equals(BSFlag.ZDDSFSBMR.kdValue) || bsFlag.equals(BSFlag.ZDDSFSBMC.kdValue)) {
            return EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value;
        } else if (bsFlag.equals(BSFlag.KZHCJSBMR.value) || bsFlag.equals(BSFlag.KZHCJSBMC.value)
                || bsFlag.equals(BSFlag.KZHCJSBMR.kdValue) || bsFlag.equals(BSFlag.KZHCJSBMC.kdValue)) {
            return EntrustProp.SH_FIXEDINCOME_KZHCJSB.value;
        } else if (bsFlag.equals(BSFlag.ZYJCJSBMR.value) || bsFlag.equals(BSFlag.ZYJCJSBMC.value)
                || bsFlag.equals(BSFlag.ZYJCJSBMR.kdValue) || bsFlag.equals(BSFlag.ZYJCJSBMC.kdValue)) {
            return EntrustProp.SH_FIXEDINCOME_ZYJCJSB.value;
        } else if ("9B".equals(bsFlag) || "9S".equals(bsFlag)) { // 针对平安质押式回购：正回购为9B，逆回购为9S
            return EntrustProp.NORMAL.value;
        } else if (bsFlag.equals(BSFlag.PPCJMR.value) || bsFlag.equals(BSFlag.PPCJMC.value)) {
            return EntrustProp.NORMAL.value;
        } else if (bsFlag.equals(BSFlag.XSCJSBMR.value) || bsFlag.equals(BSFlag.XSCJSBMC.value)) {
            return EntrustProp.SZ_XSCJ_XD.value;
        } else if (bsFlag.equals(BSFlag.XSCJHFJSMR.value) || bsFlag.equals(BSFlag.XSCJHFJSMC.value)) {
            return EntrustProp.SZ_XSCJ_JS.value;
        } else if (bsFlag.equals(BSFlag.XSCJHFJJMR.value) || bsFlag.equals(BSFlag.XSCJHFJJMC.value)) {
            return EntrustProp.SZ_XSCJ_JJ.value;
        }
        return EntrustProp.NORMAL.value;
    }

    // 综合业务  bsFlag 映射生成
    public static String getBsFlag(String entrustProp, String entrustBs) {
        if (entrustProp.equals(EntrustProp.SZ_REPURCHASE_BEGIN.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.CSJYRRZJ.value;
        } else if (entrustProp.equals(EntrustProp.SZ_REPURCHASE_BEGIN.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.CSJYRCZJ.value;
        } else if (entrustProp.equals(EntrustProp.SZ_REPURCHASE_BUYBACK.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.GHJYFHZJ.value;
        } else if (entrustProp.equals(EntrustProp.SZ_REPURCHASE_BUYBACK.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.GHJYSQZJ.value;
        } else if (entrustProp.equals(EntrustProp.SZ_REPURCHASE_RENEWAL.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.DQXZ.value;//到期续作 2K
        } else if (entrustProp.equals(EntrustProp.SZ_REPURCHASE_RENEWAL.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.DQXZRCZJ.value;
            // 意向 上海
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_INTENTION.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.YXRRZJ.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_INTENTION.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.YXRCZJ.value;
            // 报价 上海
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_TRADE.value)) {
            return BSFlag.CSJYRRZJ.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_EXPIRE.value)) {
            return BSFlag.GHJYFHZJ.value;//到期结算 2E
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_RENEWAL.value)) {
            return BSFlag.DQXZ.value;//到期续作 2K
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_RELEASE.value)) {
            return BSFlag.JCZY.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_EXCHANGE.value)) {
            return BSFlag.HQSB.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_TERMINATE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.TQZZRRZJ.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_TERMINATE.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.TQZZRCZJ.value;
            // 成交  上海
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value)) {
            return BSFlag.CJSBQR.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value)) {
            return BSFlag.CJSBJJ.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value)) {
            return BSFlag.DQXZSBQR.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value)) {
            return BSFlag.DQXZSBJJ.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value)) {
            return BSFlag.JCZYSBQR.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value)) {
            return BSFlag.JCZYSBJJ.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value)) {
            return BSFlag.HQSBQR.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value)) {
            return BSFlag.HQSBJJ.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.TQZZRCQR.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.TQZZRRQR.value;
        } else if (entrustProp.equals(EntrustProp.SH_REPURCHASE_TERMINATE_REFUSE.value)) {
            return BSFlag.TQZZSBJJ.value;
            // 固收平台
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_BAOJIA_CONFIRM.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.QDBJSBMR.value;
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_BAOJIA_CONFIRM.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.QDBJSBMC.value;
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_TRADE_CLICK.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.DJCJSBMR.value;
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_TRADE_CLICK.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.DJCJSBMC.value;
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.ZDDSFSBMR.value;
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.ZDDSFSBMC.value;
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_ZYJCJSB.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.ZYJCJSBMR.value;
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_ZYJCJSB.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.ZYJCJSBMC.value;
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_KZHCJSB.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.KZHCJSBMR.value;
        } else if (entrustProp.equals(EntrustProp.SH_FIXEDINCOME_KZHCJSB.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.KZHCJSBMC.value;
            // 大宗
        } else if (entrustProp.equals(EntrustProp.INTENTION.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.YXMR.value;
        } else if (entrustProp.equals(EntrustProp.INTENTION.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.YXMC.value;
        } else if (entrustProp.equals(EntrustProp.SET_PRICE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.DJMR.value;
        } else if (entrustProp.equals(EntrustProp.SET_PRICE.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.DJMC.value;
        } else if (entrustProp.equals(EntrustProp.CONFIRM.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.QRMR.value;
        } else if (entrustProp.equals(EntrustProp.CONFIRM.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.QRMC.value;
        } else if (entrustProp.equals(EntrustProp.MUTUAL_TRADE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.PDMR.value;
        } else if (entrustProp.equals(EntrustProp.MUTUAL_TRADE.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.PDMC.value;
        } else if (entrustProp.equals(EntrustProp.LARGE_AFTER_CLOSING_PRICE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.PHDJDZMR.value;
        } else if (entrustProp.equals(EntrustProp.LARGE_AFTER_CLOSING_PRICE.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.PHDJDZMC.value;
        } else if (entrustProp.equals(EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.PHDJDZMR.value;
        } else if (entrustProp.equals(EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.PHDJDZMC.value;
        } else if (entrustProp.equals(EntrustProp.SZ_XSCJ_XD.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.XSCJSBMR.value;
        } else if (entrustProp.equals(EntrustProp.SZ_XSCJ_XD.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.XSCJSBMC.value;
        } else if (entrustProp.equals(EntrustProp.SZ_XSCJ_JS.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.XSCJHFJSMR.value;
        } else if (entrustProp.equals(EntrustProp.SZ_XSCJ_JS.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.XSCJHFJSMC.value;
        } else if (entrustProp.equals(EntrustProp.SZ_XSCJ_JJ.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.XSCJHFJJMR.value;
        } else if (entrustProp.equals(EntrustProp.SZ_XSCJ_JJ.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.XSCJHFJJMC.value;
        }
        return BSFlag.ZQMR.value;
    }

    public static String getBsFlagForNormal(String entrustProp, String entrustBs, String assetType) {
        return getBsFlagForNormal(entrustProp, entrustBs, assetType, null, null);
    }

    /**
     * 处理ETF申赎买卖类别
     * <p>
     * 金证：
     * 深圳单市场ETF、上海单市场ETF、上海跨市场ETF、上海债券ETF、深圳货币ETF的申购或赎回，买卖类别送1或2；
     * 深圳跨境ETF、上海跨境ETF、上海货币ETF的申购或赎回、深圳跨市场现金申购赎回，买卖类别送90或91；
     * 深圳跨市场ETF的实物申购或实物赎回，买卖类别送82或84。
     *
     * @param entrustBs      买卖方向
     * @param deliveryMethod 申赎方式
     * @param market         市场
     * @param etfType        ETF类型
     * @return 买卖类别
     * @author swwan
     * @date 2020/12/2
     */
    public static String getBsFlagForETF(String entrustBs, String deliveryMethod, String market, String etfType) {
        if (ETFTypeEnum.ETF_CROSS_MARKET.code.equals(etfType)
                && (Market.SZSE.value.equals(market))
                && DeliveryMethodEnum.PHYSICAL_DELIVERY.value.equals(deliveryMethod)) {
            return EntrustBs.BUY.value.equals(entrustBs) ? BSFlag.ETF_SWSG.value : BSFlag.ETF_SWSH.value;
        }
        if (ETFTypeEnum.ETF_CROSS_BORDER.code.equals(etfType)
                || (ETFTypeEnum.ETF_CURRENCY.code.equals(etfType) && (Market.SHSE.value.equals(market)))
                || (ETFTypeEnum.ETF_CROSS_MARKET.code.equals(etfType)
                && (Market.SZSE.value.equals(market))
                && DeliveryMethodEnum.CASH_DELIVERY.value.equals(deliveryMethod))) {
            return EntrustBs.BUY.value.equals(entrustBs) ? BSFlag.ETF_KJKSCSG.value : BSFlag.ETF_KJKSCSH.value;
        }
        // 深圳单市场ETF、上海单市场ETF、上海跨市场ETF、上海债券ETF、深圳货币ETF的申购或赎回
        if (ETFTypeEnum.ETF_SINGLE_MARKET.code.equals(etfType)
                || (ETFTypeEnum.ETF_CROSS_MARKET.code.equals(etfType) && Market.SHSE.value.equals(market))
                || (ETFTypeEnum.ETF_BOND.code.equals(etfType) && Market.SHSE.value.equals(market))
                || (ETFTypeEnum.ETF_CURRENCY.code.equals(etfType) && Market.SZSE.value.equals(market))) {
            return EntrustBs.BUY.value.equals(entrustBs) ? BSFlag.ETF_SG.value : BSFlag.ETF_SH.value;
        }
        return null;
    }

    public static String getBsFlagForNormal(String entrustProp, String entrustBs, String assetType,
                                            Instrument instrument, String deliveryMethod) {
        if (Strings.isNullOrEmpty(entrustProp)
                || Strings.isNullOrEmpty(entrustBs)
                || Strings.isNullOrEmpty(assetType))
            return "";
        // 单独处理ETF申赎
        if (entrustProp.equals(EntrustProp.ETF_APPLY.value)) {
            if (Objects.isNull(instrument)) return "";
            return getBsFlagForETF(entrustBs, deliveryMethod, instrument.getMarket(), instrument.getEtfType());
        }
        if (entrustProp.equals(EntrustProp.NORMAL.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            if (InvestAssetTypeEnum.isBond(assetType) && !Objects.isNull(instrument) && !"B14".equals(instrument.getAssetType1())) {
                return BSFlag.PPCJMR.value;
            }
            return BSFlag.ZQMR.value;
        } else if (entrustProp.equals(EntrustProp.NORMAL.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            if (InvestAssetTypeEnum.isBond(assetType) && !Objects.isNull(instrument) && !"B14".equals(instrument.getAssetType1())) {
                return BSFlag.PPCJMC.value;
            }
            return BSFlag.ZQMC.value;
        } else if (entrustProp.equals(EntrustProp.IMMEDIATE_TO_CANCEL.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.JSMR.value;
        } else if (entrustProp.equals(EntrustProp.IMMEDIATE_TO_CANCEL.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.JSMC.value;
        } else if (entrustProp.equals(EntrustProp.REPURCHASE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.ZQMR.value;
        } else if (entrustProp.equals(EntrustProp.REPURCHASE.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.ZQMC.value;
        } else if (entrustProp.equals(EntrustProp.OWN_FIRST.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.BFMR.value;
        } else if (entrustProp.equals(EntrustProp.OWN_FIRST.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.BFMC.value;
        } else if (entrustProp.equals(EntrustProp.COUNTERPARTY.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.DFMR.value;
        } else if (entrustProp.equals(EntrustProp.COUNTERPARTY.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.DFMC.value;
        } else if (entrustProp.equals(EntrustProp.FILL.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.QEMR.value;
        } else if (entrustProp.equals(EntrustProp.FILL.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.QEMC.value;
        } else if (entrustProp.equals(EntrustProp.BEST5_TO_CANCEL.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.WDMC_HS.value;
        } else if (entrustProp.equals(EntrustProp.BEST5_TO_CANCEL.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.WDMC_HS.value;
        } else if (entrustProp.equals(EntrustProp.BEST5_TO_LIMIT.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.ZXMR_H.value;
        } else if (entrustProp.equals(EntrustProp.BEST5_TO_LIMIT.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.ZXMC_H.value;
        } else if (entrustProp.equals(EntrustProp.PS.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.PSJK.value;
        } else if (entrustProp.equals(EntrustProp.PS.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.PSFQ.value;
        } else if (entrustProp.equals(EntrustProp.HS.value)) {
            return BSFlag.HS.value;
        } else if (entrustProp.equals(EntrustProp.ALLOTMENT.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.PGJK.value;
            // 申购，区分 股债基
            // 上海：基金认购
        } else if (entrustProp.equals(EntrustProp.APPLY.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            if (assetType.equals(InvestAssetTypeEnum.FUND.value)) {
                return BSFlag.JJRG.value;
            }
            return BSFlag.DJSG.value;
        } else if (entrustProp.equals(EntrustProp.CHANGE_TO_STOCK.value)) {
            return BSFlag.ZG.value;
        } else if (entrustProp.equals(EntrustProp.JJSHSH.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.JIJISG.value;
        } else if (entrustProp.equals(EntrustProp.JJSHSH.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.JJSH.value;
        } else if (entrustProp.equals(EntrustProp.SH_LOF_SUBSCRIBE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.LOF_RG.value;
        } else if (entrustProp.equals(EntrustProp.SH_LOF_APPLY.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.LOF_SG.value;
        } else if (entrustProp.equals(EntrustProp.SH_LOF_RANSOM.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.LOF_SH.value;
        } else if (entrustProp.equals(EntrustProp.ZYCRK.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.ZYRZ.value;
        } else if (entrustProp.equals(EntrustProp.ZYCRK.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.ZYCZ.value;
        } else if (entrustProp.equals(EntrustProp.NORMAL_AFTER_SET_PRICE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
            return BSFlag.PHDJMR.value;
        } else if (entrustProp.equals(EntrustProp.NORMAL_AFTER_SET_PRICE.value) && entrustBs.equals(EntrustBs.SELL.value)) {
            return BSFlag.PHDJMC.value;
        }
        return BSFlag.ZQMR.value;
    }

    // 判断是否需要做公平交易
    public static boolean isFairOrder(String entrustProp) {
        return EntrustProp.NORMAL.value.equals(entrustProp)
                || EntrustProp.isMarketPrice(entrustProp);
    }

    // 判断是否可以做公平交易
    public static boolean allowFairOrder(Instruction instruction) {
        return (TradePlat.NORMAL.value.equals(instruction.getTradePlat())
                || TradePlat.NEW_BOND.value.equals(instruction.getTradePlat()))
                && (DictCodeListExt2.交易方向_买入.getValue().equals(instruction.getPs())
                || DictCodeListExt2.交易方向_卖出.getValue().equals(instruction.getPs())
                || DictCodeListExt2.交易方向_正回购.getValue().equals(instruction.getPs())
                || DictCodeListExt2.交易方向_逆回购.getValue().equals(instruction.getPs())
        );
    }

    /**
     * 是否处于公平交易模式
     */
    public static boolean isFairMode() {
        return ExchangeConstant.TRUE_IN_PARA.equals(CacheUtil.getSysParam(ExchangeConstant.FAIR_CODE_IN_PARA));
    }

    /**
     * 是否为算法单
     */
    public static boolean isAlgorithmOrder(OrderPlaceBean bean) {
        return StrUtil.isNotBlank(bean.getAlgorithmId());
    }

    /**
     * 是否为批量单
     */
    public static boolean isBatchOrder(OrderPlaceBean bean) {
        return Objects.nonNull(bean.getBatchInfo());
    }

    // 是否为公平单
    public static boolean isFairOrder(OrderPlaceBean bean, Instruction instruction) {
        return EntrustUtils.isFairMode()
                && EntrustUtils.allowFairOrder(instruction)
                && !EntrustUtils.isAlgorithmOrder(bean)
                && !EntrustUtils.isBatchOrder(bean);
    }

    // 是否为数量指令
    public static boolean isVolume(Instruction instruction) {
//        return instruction.getEntrustNum() > 0;
        return instruction.volumeInstruction();
    }

    // 是否为金额指令
    public static boolean isAmt(Instruction instruction) {
        return !isVolume(instruction);
    }

    // 是否为股票指令
    public static boolean isStock(Instruction instruction) {
        return InvestAssetTypeEnum.STOCK.value.equals(instruction.getAssetType());
    }

    // 是否为债券指令
    public static boolean isBond(Instruction instruction) {
        return InvestAssetTypeEnum.BOND.value.equals(instruction.getAssetType());
    }

    // 是否为基金指令
    public static boolean isFund(Instruction instruction) {
        return InvestAssetTypeEnum.FUND.value.equals(instruction.getAssetType());
    }

    // 是否为期货指令
    public static boolean isFutures(Instruction instruction) {
        return InvestAssetTypeEnum.FUTURES.value.equals(instruction.getAssetType());
    }

    // 是否为质押式回购指令
    public static boolean isPledgeRepurchase(Instruction instruction) {
        return InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(instruction.getAssetType());
    }

    // 是否为质押式竞价回购指令
    public static boolean isPledgeAuctionRepurchase(Instruction instruction) {
        return InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(instruction.getAssetType())
                && InvestmentAssetType1.BIDDING_REPO.value.equals(instruction.getAssetType1());
    }

    // 是否为质押式协议回购指令
    public static boolean isPledgegreementRepurchase(Instruction instruction) {
        return InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(instruction.getAssetType())
                && InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1());
    }

    // 是否为市价指令
    public static boolean isMarketPrice(Instruction instruction) {
        return InstructionPriceMode.MARKET.value.equals(instruction.getPriceMode());
    }

    public static boolean isMarketPriceByPriceMode(String priceMode) {
        return InstructionPriceMode.MARKET.value.equals(priceMode);
    }

    public static boolean isMarketPriceByEntrustProp(String entrustProp) {
        return EntrustProp.isMarketPrice(entrustProp);
    }

    // 判断是否为需要进行自成交检测的业务
    public static boolean isSelfTradeTyper(String entrustProp) {
        return EntrustProp.NORMAL.value.equals(entrustProp)
                || EntrustProp.isMarketPrice(entrustProp);
    }

    public static boolean isFairSwitchOpen(String fairCode) {
        return ExchangeConstant.TRUE_IN_PARA.equals(fairCode);
    }

    public static boolean isRiskApprovalSwitchOpen(String riskApprovalCode) {
        return ExchangeConstant.TRUE_IN_PARA.equals(riskApprovalCode);
    }

    public static boolean isfairModeEntrustPriceBestThanInstructionForbid(String fairModeEntrustPriceBestThanInstruction) {
        return ExchangeConstant.TRUE_IN_PARA.equals(fairModeEntrustPriceBestThanInstruction);
    }

    public static boolean isfairModeEntrustPriceBestThanInstructionForbid() {
        return ExchangeConstant.TRUE_IN_PARA.equals(CacheUtil.getSysParam(ExchangeConstant.FAIR_MODE_ENTRUST_PRICE_BEST_THAN_INSTRUCTION_IN_PARA));
    }

    public static boolean isfairModeWithUnacceptInstructionForbid(String fairModeWithUnacceptInstruction) {
        return ExchangeConstant.TRUE_IN_PARA.equals(fairModeWithUnacceptInstruction);
    }

    public static boolean isfairModeWithUnacceptInstructionForbid() {
        return ExchangeConstant.TRUE_IN_PARA.equals(CacheUtil.getSysParam(ExchangeConstant.FAIR_MODE_WITH_UNACCEPT_INSTRUCTION_IN_PARA));
    }

    // 判断是否是回购
    public static boolean isRepurchase(String entrustProp, String assetType) {
        return InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType)
                || IntegratedEntrustUtils.isProtocolBack_SH(entrustProp)
                || IntegratedEntrustUtils.isProtocolBack_SZ(entrustProp);
    }

    public static boolean needOthCode(String entrustProp) {
        return EntrustProp.HS.value.equals(entrustProp)
                || EntrustProp.ZYCRK.value.equals(entrustProp)
                || EntrustProp.CHANGE_TO_STOCK.value.equals(entrustProp)
                || EntrustProp.PS.value.equals(entrustProp)
                || EntrustProp.ALLOTMENT.value.equals(entrustProp)
                || EntrustProp.ETF_APPLY.value.equals(entrustProp);
    }

    //判断是否有全价净价，债券买入卖出、固收、大宗
    public static boolean isEntrustPropFullPrice(String entrustProp) {
        return EntrustProp.NORMAL.value.equals(entrustProp)
                || EntrustProp.COUNTERPARTY.value.equals(entrustProp)
                || EntrustProp.BEST5_TO_LIMIT.value.equals(entrustProp)
                || EntrustProp.OWN_FIRST.value.equals(entrustProp)
                || EntrustProp.IMMEDIATE_TO_CANCEL.value.equals(entrustProp)
                || EntrustProp.BEST5_TO_CANCEL.value.equals(entrustProp)
                || EntrustProp.FILL.value.equals(entrustProp)
                || EntrustProp.INTENTION.value.equals(entrustProp)
                || EntrustProp.SET_PRICE.value.equals(entrustProp)
                || EntrustProp.CONFIRM.value.equals(entrustProp)
                || EntrustProp.LIMIT_PRICE.value.equals(entrustProp)
                || EntrustProp.MUTUAL_TRADE.value.equals(entrustProp)
                || EntrustProp.LARGE_AFTER_CLOSING.value.equals(entrustProp)
                || EntrustProp.LARGE_AFTER_CLOSING_PRICE.value.equals(entrustProp)
                || EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value.equals(entrustProp)
                || EntrustProp.SH_FIXEDINCOME_BAOJIA_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_FIXEDINCOME_TRADE_CLICK.value.equals(entrustProp)
                || EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value.equals(entrustProp)
                || EntrustProp.SH_FIXEDINCOME_KZHCJSB.value.equals(entrustProp)
                || EntrustProp.SH_FIXEDINCOME_ZYJCJSB.value.equals(entrustProp);
    }

    //判断是否需要使用全价价格 stockCode需要去掉market
    public static boolean isFullPriceByInstrument(String market, String stock_code, String instructionAssetType) {
        if (!StringUtils.isNumeric(StockCodeUtils.getStockCode(stock_code))) {
            return false;
        }
        if (!Strings.isNullOrEmpty(instructionAssetType) && !InvestAssetTypeEnum.BOND.value.equals(instructionAssetType)) {
            return false;
        }
        int stockCode = Integer.parseInt(StockCodeUtils.getStockCode(stock_code));
        if (Market.SZSE.value.equals(market)) {
            return (114000 <= stockCode && stockCode <= 114999)
                    || (115100 <= stockCode && stockCode <= 115199)
                    || (117000 <= stockCode && stockCode <= 117499)
                    || (117500 <= stockCode && stockCode <= 117999)
                    || (118000 <= stockCode && stockCode <= 118899)
                    || (118900 <= stockCode && stockCode <= 118999)
                    || (121000 <= stockCode && stockCode <= 121499)
                    || (133000 <= stockCode && stockCode <= 134999)
                    || (135000 <= stockCode && stockCode <= 137999)
                    || (121500 <= stockCode && stockCode <= 121999)
                    || (138000 <= stockCode && stockCode <= 139999)
                    || (119000 <= stockCode && stockCode <= 119999)
                    || (116500 <= stockCode && stockCode <= 116999)
                    || (116000 <= stockCode && stockCode <= 116499)
                    || (113000 <= stockCode && stockCode <= 113999)
                    || (120000 <= stockCode && stockCode <= 122999)
                    || (124000 <= stockCode && stockCode <= 124999)
                    || (128000 <= stockCode && stockCode <= 128899)
                    || (127000 <= stockCode && stockCode <= 127999)
                    || (123000 <= stockCode && stockCode <= 123999)
                    || (115200 <= stockCode && stockCode <= 115999)
                    || (115000 <= stockCode && stockCode <= 115099);
        } else if (Market.SHSE.value.equals(market)) {
            return (128000 <= stockCode && stockCode <= 128999)
                    || (131000 <= stockCode && stockCode <= 131999)
                    || (142000 <= stockCode && stockCode <= 142999)
                    || (146000 <= stockCode && stockCode <= 146999)
                    || (149000 <= stockCode && stockCode <= 149999)
                    || (156000 <= stockCode && stockCode <= 156999)
                    || (165000 <= stockCode && stockCode <= 165999)
                    || (168000 <= stockCode && stockCode <= 169999)
                    || (123500 <= stockCode && stockCode <= 123999)
                    || (159000 <= stockCode && stockCode <= 159999);
        }
        return false;
    }

    public static boolean isFullPriceTerminal(String market, String stock_code, String instructionAssetType, String entrustProp) {
        if (!Strings.isNullOrEmpty(instructionAssetType) && !InvestAssetTypeEnum.BOND.value.equals(instructionAssetType)) {
            return false;
        }
        if (!EntrustUtils.isEntrustPropFullPrice(entrustProp)) {
            return false;
        }
        if (!StringUtils.isNumeric(StockCodeUtils.getStockCode(stock_code))) {
            return false;
        }
        int stockCode = Integer.parseInt(StockCodeUtils.getStockCode(stock_code));
        if (Market.SZSE.value.equals(market)) {
            return (114000 <= stockCode && stockCode <= 114999)
                    || (115100 <= stockCode && stockCode <= 115199)
                    || (117000 <= stockCode && stockCode <= 117499)
                    || (117500 <= stockCode && stockCode <= 117999)
                    || (118000 <= stockCode && stockCode <= 118899)
                    || (118900 <= stockCode && stockCode <= 118999)
                    || (121000 <= stockCode && stockCode <= 121499)
                    || (133000 <= stockCode && stockCode <= 134999)
                    || (135000 <= stockCode && stockCode <= 137999)
                    || (121500 <= stockCode && stockCode <= 121999)
                    || (138000 <= stockCode && stockCode <= 139999)
                    || (119000 <= stockCode && stockCode <= 119999)
                    || (116500 <= stockCode && stockCode <= 116999)
                    || (116000 <= stockCode && stockCode <= 116499)
                    || (113000 <= stockCode && stockCode <= 113999)
                    || (120000 <= stockCode && stockCode <= 122999)
                    || (124000 <= stockCode && stockCode <= 124999)
                    || (128000 <= stockCode && stockCode <= 128899)
                    || (127000 <= stockCode && stockCode <= 127999)
                    || (123000 <= stockCode && stockCode <= 123999)
                    || (115200 <= stockCode && stockCode <= 115999)
                    || (115000 <= stockCode && stockCode <= 115099);
        } else if (Market.SHSE.value.equals(market)) {
            return (128000 <= stockCode && stockCode <= 128999)
                    || (131000 <= stockCode && stockCode <= 131999)
                    || (142000 <= stockCode && stockCode <= 142999)
                    || (146000 <= stockCode && stockCode <= 146999)
                    || (149000 <= stockCode && stockCode <= 149999)
                    || (156000 <= stockCode && stockCode <= 156999)
                    || (165000 <= stockCode && stockCode <= 165999)
                    || (168000 <= stockCode && stockCode <= 169999)
                    || (123500 <= stockCode && stockCode <= 123999)
                    || (159000 <= stockCode && stockCode <= 159999);
        }
        return false;
    }

    public static boolean isFullPrice(String instructionAssetType, String entrustProp, String isFullPrice) {
        return isFullPrice(null, instructionAssetType, entrustProp, isFullPrice);
    }

    //判断是否需要使用全价价格 stockCode需要去掉market
    public static boolean isFullPrice(Instrument instrument, String instructionAssetType, String entrustProp, String isFullPrice) {
        if (!Strings.isNullOrEmpty(isFullPrice)) {
            return FullPriceConst.FULL_PRICE.value.equals(isFullPrice);
        }
        if (!Strings.isNullOrEmpty(instructionAssetType) && !InvestAssetTypeEnum.BOND.value.equals(instructionAssetType)) {
            return false;
        }
        if (!EntrustUtils.isEntrustPropFullPrice(entrustProp)) {
            return false;
        }
        return Objects.nonNull(instrument) && FullPriceConst.FULL_PRICE.value.equals(instrument.getIsFullPrice());
    }

    //判断是否是上海债券，单位为手（1手=10张）
    public static boolean isSHZQ(String market, String instructionAssetType, String stock_code) {
        if (!Strings.isNullOrEmpty(instructionAssetType) && !InvestAssetTypeEnum.BOND.value.equals(instructionAssetType)) {
            return false;
        }
        if (!StringUtils.isNumeric(StockCodeUtils.getStockCode(stock_code))) {
            return false;
        }
        if (Market.SHSE.value.equals(market)) {
            int stockCode = Integer.parseInt(StockCodeUtils.getStockCode(stock_code));
            return (10000 <= stockCode && stockCode <= 10999)
                    || (19000 <= stockCode && stockCode <= 19999)
                    || (20000 <= stockCode && stockCode <= 20999)
                    || (9000 <= stockCode && stockCode <= 9999)
                    || (121000 <= stockCode && stockCode <= 121999)
                    || (130000 <= stockCode && stockCode <= 130999)
                    || (140000 <= stockCode && stockCode <= 140999)
                    || (157000 <= stockCode && stockCode <= 157999)
                    || (147000 <= stockCode && stockCode <= 147999)
                    || (160000 <= stockCode && stockCode <= 160999)
                    || (18000 <= stockCode && stockCode <= 18999)
                    || (122000 <= stockCode && stockCode <= 122499)
                    || (127000 <= stockCode && stockCode <= 127999)
                    || (136000 <= stockCode && stockCode <= 136999)
                    || (143000 <= stockCode && stockCode <= 143999)
                    || (124000 <= stockCode && stockCode <= 124999)
                    || (155000 <= stockCode && stockCode <= 155999)
                    || (163000 <= stockCode && stockCode <= 163999)
                    || (135000 <= stockCode && stockCode <= 135499)
                    || (145000 <= stockCode && stockCode <= 145999)
                    || (150000 <= stockCode && stockCode <= 151999)
                    || (162000 <= stockCode && stockCode <= 162999)
                    || (120000 <= stockCode && stockCode <= 120999)
                    || (129000 <= stockCode && stockCode <= 129999)
                    || (152000 <= stockCode && stockCode <= 152999)
                    || (122500 <= stockCode && stockCode <= 122999)
                    || (139000 <= stockCode && stockCode <= 139999)
                    || (123000 <= stockCode && stockCode <= 123499)
                    || (125000 <= stockCode && stockCode <= 125999)
                    || (135500 <= stockCode && stockCode <= 135999)
                    || (110000 <= stockCode && stockCode <= 110999)
                    || (112000 <= stockCode && stockCode <= 112999)
                    || (113000 <= stockCode && stockCode <= 113999)
                    || (126000 <= stockCode && stockCode <= 126999)
                    || (132000 <= stockCode && stockCode <= 132999)
                    || (137000 <= stockCode && stockCode <= 137099)
                    || (138000 <= stockCode && stockCode <= 138099)
                    || (128000 <= stockCode && stockCode <= 128999)
                    || (131000 <= stockCode && stockCode <= 131999)
                    || (142000 <= stockCode && stockCode <= 142999)
                    || (146000 <= stockCode && stockCode <= 146999)
                    || (149000 <= stockCode && stockCode <= 149999)
                    || (156000 <= stockCode && stockCode <= 156999)
                    || (165000 <= stockCode && stockCode <= 165999)
                    || (168000 <= stockCode && stockCode <= 169999)
                    || (123500 <= stockCode && stockCode <= 123999)
                    || (159000 <= stockCode && stockCode <= 159999);
        }
        return false;
    }

    /***
     * @Description: 没有撤单委托
     * @Param: [entrustStatus]
     * @return: boolean
     * @Author: MAXX
     * @Date: 2020/8/24
     */
    public static boolean hasNoCancelEntrust(int entrustStatus) {
        return EntrustStatus.SUCCEEDED.value == entrustStatus
                || EntrustStatus.PART_TRADED_CANCELED.value == entrustStatus
                || EntrustStatus.CANCELED.value == entrustStatus
                || EntrustStatus.INVALID.value == entrustStatus
                || EntrustStatus.SYSTEM_INVALID.value == entrustStatus
                || EntrustStatus.NOT_APPROVAL.value == entrustStatus
                || EntrustStatus.NOT_RISK.value == entrustStatus
                || EntrustStatus.NOT_REVIEW.value == entrustStatus
                || EntrustStatus.NOT_REVOKE.value == entrustStatus;
    }

    //需要进行持仓检查的业务
    public static boolean needPositionCheck(String entrustProp, String assetType) {
        return EntrustProp.isSplitEntrust(entrustProp) && !InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType);
    }

    public static boolean isSettleTrade(String tradePlat, String entrustProp) {
        if (IntegratedEntrustUtils.isProtocolBack_SH(entrustProp)
                || IntegratedEntrustUtils.isProtocolBack_SZ(entrustProp)) {
            return true;
        } else if (TradePlat.SH_FIXED_INCOME.value.equals(tradePlat)
                || TradePlat.SZ_PROTOCOL.value.equals(tradePlat)) {
            return true;
        }
        return false;
    }

    public static boolean isSuccess(ResultBean resultBean) {
        return ErrorCode.isSuccess(resultBean.getErrorCode());
    }

    public static boolean isFailed(ResultBean resultBean) {
        return !isSuccess(resultBean);
    }

    // 是否为竞价指令
    public static boolean isNormalTrade(Instruction instruction) {
        return TradePlat.NORMAL.value.equals(instruction.getTradePlat());
    }

    public static String getEntrustPropStr(String code) {
        EntrustProp entrustProp = entrustPropMap.get(code);
        if (null == entrustProp) {
            return code;
        }
        return entrustProp.getName();
    }

    /**
     * 判断是否信用保护凭证
     */
    public static boolean isCreditProtectionCertificate(String busiTypeTg) {
        return StringUtil.equals(DictCodeListExt3.投管业务类型_信用保护凭证.getValue(), busiTypeTg);
    }

    /**
     * 判断是否新债业务
     */
    public static boolean isNewBond(String busiTypeTg) {
        return StringUtil.equals(DictCodeListExt3.投管业务类型_现券交易.getValue(), busiTypeTg);
    }

    /**
     * 判断是否通用质押式回购
     */
    public static boolean isRepurchase(String busiTypeTg) {
        return StringUtil.equals(DictCodeListExt3.投管业务类型_通用式质押回购.getValue(), busiTypeTg);
    }

    /**
     * 判断是否非交易业务: 新股申购、配股 是竞价交易不是非交易
     */
    public static boolean isNotTrade(String busiTypeTg) {
        return StringUtil.equals(DictCodeListExt3.投管业务类型_行权.getValue(), busiTypeTg)
                || StringUtil.equals(DictCodeListExt3.投管业务类型_债转股.getValue(), busiTypeTg)
                || StringUtil.equals(DictCodeListExt3.投管业务类型_质押券出入库.getValue(), busiTypeTg)
                || StringUtil.equals(DictCodeListExt3.投管业务类型_配债.getValue(), busiTypeTg)
                || StringUtil.equals(DictCodeListExt3.投管业务类型_可转债申购.getValue(), busiTypeTg);
//                || StringUtil.equals(DictCodeListExt3.投管业务类型_配股.getValue(), busiTypeTg)
//                || StringUtil.equals(DictCodeListExt3.投管业务类型_新股申购.getValue(), busiTypeTg);
    }

    /**
     * 判断是否竞价交易业务
     */
    public static boolean isNormalTrade(String busiTypeTg) {
        return StringUtil.equals(DictCodeListExt3.投管业务类型_竞价交易.getValue(), busiTypeTg);
    }

    /**
     * 判断是否大宗交易业务
     */
    public static boolean isBulk(String busiTypeTg) {
        return StringUtil.equals(DictCodeListExt3.投管业务类型_大宗交易.getValue(), busiTypeTg);
    }

    /**
     * 判断是否协议现券业务
     */
    public static boolean isAgreementCashBond(String busiTypeTg) {
        return StringUtil.equals(DictCodeListExt3.投管业务类型_协议现券.getValue(), busiTypeTg);
    }

    /**
     * 判断是否协议回购
     */
    public static boolean isPledgeAgreementRepurchase(String busiTypeTg) {
        return StringUtil.equals(DictCodeListExt3.投管业务类型_首期.getValue(), busiTypeTg)
                || StringUtil.equals(DictCodeListExt3.投管业务类型_提前购回.getValue(), busiTypeTg)
                || StringUtil.equals(DictCodeListExt3.投管业务类型_到期购回.getValue(), busiTypeTg)
                || StringUtil.equals(DictCodeListExt3.投管业务类型_到期续做.getValue(), busiTypeTg)
                || StringUtil.equals(DictCodeListExt3.投管业务类型_中途换券.getValue(), busiTypeTg)
                || StringUtil.equals(DictCodeListExt3.投管业务类型_解除质押.getValue(), busiTypeTg);
    }
}

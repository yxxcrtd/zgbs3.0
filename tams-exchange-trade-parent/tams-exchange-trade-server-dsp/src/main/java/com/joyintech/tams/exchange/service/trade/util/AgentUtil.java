package com.joyintech.tams.exchange.service.trade.util;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.Account;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AgentUtil {

    /**
     * 设置 kcbpu 成交状态
     *
     * @param map
     */
    public static int SetKCBPUTradeStatus(Map<String, String> map) {
        int tradeStatus = 0;
        // 数量 	QTY
        // 成交数量	MATCHED_QTY
        // 成交类型0:普通成交1:撤单成交2:废单3:内部撤单4:撤单废单
        BigDecimal qty = AgentUtil.Parse2BigDecimal(map.get("QTY"));
        BigDecimal matchedQty = AgentUtil.Parse2BigDecimal(map.get("MATCHED_QTY"));
        int cp = matchedQty.compareTo(BigDecimal.ZERO);
        if (cp == -1) {
            //  负数是撤单。忽略撤单成交记录
            tradeStatus = 1;
        } else if (cp == 1) {
            tradeStatus = 0;
        } else {
            tradeStatus = 2;
        }
        return tradeStatus;
    }

    /**
     * 设置 kcbpu 委托状态
     *
     * @param map
     */
    public static int SetKCBPUEntrustStatus(Map<String, String> map) {
        // 数量	QTY	INT
        // 撤单标志 	IS_WITHDRAW	CHAR(1)   1撤单；0正常单
        // 已撤单标志	IS_WITHDRAWN	CHAR(1)  0未撤 1 已撤
        // 合法标志	VALID_FLAG	CHAR(1)  1合法；0非法
        // 成交数量	MATCHED_QTY	INT
        // 可撤单标志	CAN_WITHDRAW	CHAR(1)
        // 已撤单数量	WITHDRAWN_QTY	INT

        int eStatus = EntrustStatus.REPORTED.value;
        String validFlag = map.get("VALID_FLAG");
        if ("0".equals(validFlag)) {
            // 0非法 废单
            eStatus = EntrustStatus.INVALID.value;
        } else {
            map.put("OP_REMARK", "");
            // 1合法
            BigDecimal qty = AgentUtil.Parse2BigDecimal(map.get("QTY"));
            String isWithdraw = map.get("IS_WITHDRAW");
            String isWithdrawn = map.get("IS_WITHDRAWN");
            BigDecimal matchedQty = AgentUtil.Parse2BigDecimal(map.get("MATCHED_QTY"));
            BigDecimal withdrawnQty = AgentUtil.Parse2BigDecimal(map.get("WITHDRAWN_QTY"));
            if ("1".equals(isWithdraw)) {
                // 撤单
                if (qty.compareTo(withdrawnQty) == 0) {
                    // 已撤
                    eStatus = EntrustStatus.CANCELED.value;
                } else {
                    // 部撤
                    eStatus = EntrustStatus.PART_TRADED_CANCELED.value;
                }
            } else {
                // 正常单
                if ("0".equals(isWithdrawn)) {
                    // 没有撤单时
                    if (qty.compareTo(matchedQty) == 0) {
                        // 已成
                        eStatus = EntrustStatus.SUCCEEDED.value;
                    } else {
                        // 部成
                        eStatus = EntrustStatus.PART_SUCCESS.value;
                    }
                    map.put("BIZ_NO", map.get("ORDER_ID"));
                    map.put("EXT_ORDER_ID", "");
                } else {
                    // 有撤单时
                    if (qty.compareTo(withdrawnQty) == 0) {
                        // 已撤
                        eStatus = EntrustStatus.CANCELED.value;
                    } else {
                        // 部撤
                        eStatus = EntrustStatus.PART_TRADED_CANCELED.value;
                    }
                }
            }
        }
        return eStatus;
    }


    /**
     * 密碼解密
     *
     * @param account
     */
    public static void SetDecodeBase64(Account account) {
        // 账户交易密码解密
        account.setPwd(new String(Base64.decodeBase64(account.getPwd())));
        account.setTradePwd(new String(Base64.decodeBase64(account.getTradePwd())));
    }

    /**
     * 日期格式化
     * 10442119
     *
     * @param time
     * @return
     */
    public static String FormatApiTimeToString(String time) {
        // 10442119
        if (Strings.isNullOrEmpty(time)) {
            time = "000000";
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
        cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(2, 4)));
        cal.set(Calendar.SECOND, Integer.parseInt(time.substring(4, 6)));
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeFormat.format(cal.getTime());
    }

    /**
     * 金证U版格式化
     *
     * @param time "ORDER_DATE": "2022-08-31 11:11:14.239829",
     * @return
     */
    public static String FormatApiTimeKCBPU(String time) {
        // 10442119
        if (time.length() <= 19) {
            return time;
        }
        return time.substring(0, 19);
    }

    /**
     * BigDecimal 转换
     *
     * @param value
     * @return
     */
    public static BigDecimal Parse2BigDecimal(String value) {
        if (Strings.isNullOrEmpty(value)) {
            value = "0";
        }
        return new BigDecimal(value);
    }

    /**
     * 日期格式化
     * 10442119
     *
     * @param time
     * @return
     */
    public static Long FormatApiTimeToLong(String time) {
        // 10442119
        if (Strings.isNullOrEmpty(time)) {
            time = "000000";
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
        cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(2, 4)));
        cal.set(Calendar.SECOND, Integer.parseInt(time.substring(4, 6)));
        return cal.getTime().getTime();
    }

    /**
     * 统一处理有空格的key
     *
     * @param resMap
     * @param key
     */
    public static String GetMapValue(Map<String, String> resMap, String key) {
        String value = resMap.get(key);
        if (value == null)
            value = resMap.get(" " + key);
        return value;
    }

    /**
     * 检测是否支持当前的业务场景
     *
     * @param prop        场景属性
     * @param market      市场
     * @param entrustProp 前台场景属性
     */
    public static void CheckEntrustProp(String prop, String market, String entrustProp) {
        if (Strings.isNullOrEmpty(prop)) {
            throw new RuntimeException(String.format("不支持当前委托属性market=%s,entrustProp=%s", market, entrustProp));
        }
    }

    /**
     * 投管与恒生柜台交易所代码转换
     *
     * @param market 投管交易所代码
     * @return 恒生柜台交易所代码
     */
    public static String ExchangeMarketUFX(String market) {
        if (Market.SZSE.value.equals(market))
            return ExchangeType.SZ.value;
        else if (Market.SHSE.value.equals(market))
            return ExchangeType.SH.value;
        else return "";
    }

    /**
     * 投管与金证柜台交易所代码转换
     *
     * @param market 投管交易所代码
     * @return 金证柜台交易所代码
     */
    public static String ExchangeMarketKCBPU(String market) {
        // 00:深圳10:上海
        if (Market.SZSE.value.equals(market)) {
            return "00";
        } else if (Market.SHSE.value.equals(market)) {
            return "10";
        } else
            return "";
    }

    /**
     * 投管与金证柜台交易所代码转换
     *
     * @param market 投管交易所代码
     * @return 金证柜台交易所代码
     */
    public static String GetMarketKCBPU(String market) {
        // 00、0:深圳10、1:上海
        if ("00".equals(market) || "0".equals(market)) {
            return Market.SZSE.value;
        } else if ("10".equals(market) || "1".equals(market)) {
            return Market.SHSE.value;
        } else
            return "";
    }

    public static String GetMarketKCBPW(String market) {
        if ("1".equals(market)) {
            return Market.SHSE.value;
        } else if ("0".equals(market)) {
            return Market.SZSE.value;
        }

        return market;
    }

    /**
     * 转换金证U版字典
     *
     * @param bsFlag
     * @return
     */
    public static String GetBSFlagForKCBPU(String bsFlag) {
        String bs = "";
        if (BSFlag.PPCJMR.value.equals(bsFlag)) {
            // 现券匹配成交
            bs = "0B";
        } else if (BSFlag.PPCJMC.value.equals(bsFlag)) {
            // 现券匹配成交
            bs = "0S";
        }
        return bs;
    }

    public static String ParseBSFlagForKCBPU(String bsFlag) {
        String bs = "";
        if ("Bp".equals(bsFlag)) {
            // 现券匹配成交
            bs = "0B";
        } else if ("Sp".equals(bsFlag)) {
            // 现券匹配成交
            bs = "0S";
        }
        return bs;
    }

    public static String ParseEntrustBsForKCBPU(String bsFlag) {
//        // 9B、9S针对平安质押式回购正逆回购业务
//        if (buyBsFlag.contains(bsFlag) || BSFlag.ZYCZ.value.equals(bsFlag) || "9B".equals(bsFlag)) {
//            return EntrustBs.BUY.value;
//        } else if (sellBsFlag.contains(bsFlag) || BSFlag.ZYRZ.value.equals(bsFlag) || "9S".equals(bsFlag)) {
//            return EntrustBs.SELL.value;
//        }
        return EntrustBs.BUY.value;
    }

    /**
     * 投管与金证柜台交易所代码转换
     *
     * @param market 投管交易所代码
     * @return 金证柜台交易所代码
     */
    public static String ExchangeMarketKCBPW(String market) {
        if (Market.SZSE.value.equals(market)) {
            return "0";
        } else if (Market.SHSE.value.equals(market)) {
            return "1";
        } else
            return "";
    }

    /**
     * 投管与金证柜台交易主体转换
     * 明确本方交易主体类型只有机构经纪和个人经纪，其他类型映射处理不明确
     * 410677 现券协商成交-成交申报
     * 金证柜台交易主体
     * 1=个人投资者
     * 101=交易所
     * 102=会员
     * 103=机构投资者
     * 104=自营交易
     * 105=流动性服务提供商
     * 106=结算公司
     *
     * @param tradeActor 投管交易主体
     * @return 金证柜台交易主体
     */
    public static String TradeActorKCBP(String tradeActor) {
        // if (InvestorType.SELF_SUPPORT.getValue().equals(tradeActor)) {
        //     // 01 自营  104=自营交易
        //     return "104";
        // } else if (InvestorType.ASSET_MANAGEMENT.getValue().equals(tradeActor)) {
        //     // 02 资管  104=自营交易
        //     return "104";
        // } else
        if (InvestorType.PARTY_BROKERAGE.getValue().equals(tradeActor)) {
            // 03 机构经纪 103=机构投资者
            return "103";
        } else if (InvestorType.SELF_SUPPORT.getValue().equals(tradeActor)) {
            // 04 个人经纪 1=个人投资者
            return "1";
        } else
            return "";
    }

    /**
     * 判断下单委托或撤销委托成功标识
     * true：成功  ；false失败
     * 通讯成功且返回柜台委托编号才是下单成功
     * xiazelei 修改于 2022/6/28 15:40
     *
     * @param ds
     * @param resultBean
     * @return
     */
    public static boolean getOrderSuccessFlagUFX(ResultBean resultBean, Map<String, Object> ds) {
        boolean successFlag = false;
        if (ds == null) {
            ds = (Map<String, Object>) resultBean.getData();
        }
        // 通讯成功且返回柜台委托编号才是下单成功
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            if (ds != null) {
                if (StringUtil.isNotEmpty(ds.get("entrust_no")))
                    successFlag = true;
            }
        }
        return successFlag;
    }


    /**
     * 判断查询成功标识
     * true：成功  ；false失败
     * 通讯成功且返回有效记录才是查询成功
     * xiazelei 修改于 2022/6/28 15:40
     *
     * @param resultBean
     * @return
     */
    public static boolean getQuerySuccessFlagUFX(ResultBean resultBean) {
        boolean successFlag = true;
        Map<String, Object> ds = null;
        if (resultBean.getData() instanceof JSONArray) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
            if (resList.size() == 0) {
                return successFlag = false;
            }
            ds = resList.get(0);
        } else
            ds = (Map<String, Object>) resultBean.getData();
        // 通讯成功有记录且没有错误码的才是真的成功
        // [[{"error_info":"找不到对应的股票代码","error_no":"620900"}]]
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            if (ds != null) {
                if (StringUtil.isNotEmpty(ds.get("error_no")))
                    successFlag = false;
            }
        } else {
            successFlag = false;
        }
        return successFlag;
    }


    /**
     * 获取查询列表
     *
     * @param resultBean
     * @return
     */
    public static List<Map<String, Object>> getQueryListUFX(ResultBean resultBean) {
        if (getQuerySuccessFlagUFX(resultBean)) {
            return (List<Map<String, Object>>) resultBean.getData();
        } else {
            // 空list 避免异常
            return Lists.newArrayList();
        }
    }


    /**
     * 获取查询列表
     *
     * @param resultBean
     * @return
     */
    public static List<Map<String, String>> getQueryListKCBPW(ResultBean resultBean) {
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            // 空list 避免异常
            return Lists.newArrayList();
        } else {
            return (List<Map<String, String>>) resultBean.getData();
        }
    }
}

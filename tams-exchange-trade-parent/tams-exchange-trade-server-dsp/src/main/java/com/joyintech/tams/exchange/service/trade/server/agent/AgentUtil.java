package com.joyintech.tams.exchange.service.trade.server.agent;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.ExchangeType;
import com.joyintech.tams.exchange.common.constant.InvestorType;
import com.joyintech.tams.exchange.common.constant.Market;

import java.util.List;
import java.util.Map;

public class AgentUtil {


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
    public static String ExchangeMarketKCBP(String market) {
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

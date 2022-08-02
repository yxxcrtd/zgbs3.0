package com.joyintech.tams.exchange.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.ExchangeType;
import com.joyintech.tams.exchange.common.constant.InvestorType;
import com.joyintech.tams.exchange.common.constant.Market;

import java.util.List;
import java.util.Map;

public class AgentServerUtil {

    /**
     * 交易缓存key
     * @param keyType         E 普通委托 I 综合委托
     * @param accountId
     * @param brokerEntrustId
     * @return
     */
    public static String GetTradeCacheKey(String keyType, String accountId, String brokerEntrustId) {
        String format = "%s_%s_%s_%s";
        return String.format(format, keyType, accountId, AgentServerUtil.GetPlatDate(), brokerEntrustId);
    }

    /**
     * 获取当日日期 yyyyMMdd
     */
    public static String GetPlatDate() {
        return DateUtil.format(DateUtil.platDateFromCache(), DateUtil.DEFAULT_OTHER_FORMAT);
    }
}

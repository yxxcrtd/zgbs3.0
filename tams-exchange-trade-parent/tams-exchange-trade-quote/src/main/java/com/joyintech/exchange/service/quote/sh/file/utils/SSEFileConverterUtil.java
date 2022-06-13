package com.joyintech.exchange.service.quote.sh.file.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.joyintech.exchange.service.quote.bean.InterfQuoteShFileBean;
import com.joyintech.exchange.service.quote.sh.file.bean.SSEFileFundQuoteBean;
import com.joyintech.exchange.service.quote.sh.file.bean.SSEFileQuote;
import com.joyintech.exchange.service.quote.sh.file.bean.SSEFileStockQuoteBean;
import com.joyintech.exchange.service.quote.sh.file.enums.LimitType;
import com.joyintech.exchange.service.quote.sh.file.enums.MDStreamID;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Author: think
 * @Description:
 * @Date: Created in 13:24 2021/8/10
 */
public class SSEFileConverterUtil {
    private static final Logger logger = LoggerFactory.getLogger(SSEFileConverterUtil.class);

    public static TickBean convertTick(String data) {
        TickBean tick = new TickBean();
        if (StringUtil.isEmpty(data)) {
            return null;
        }
        InterfQuoteShFileBean interfQuoteShFileBean = JSON.parseObject(data, InterfQuoteShFileBean.class);
        // 解析竞价行情
        String[] records = StringUtil.split(interfQuoteShFileBean.getMktdt(), GlobalConstant.VERTICAL_LINE);
        SSEFileQuote sseFileQuoteBean;
        if (records[0].equals(MDStreamID.FUND.value)) {
            sseFileQuoteBean = setParam(SSEFileFundQuoteBean.class, records);
        } else {
            sseFileQuoteBean = setParam(SSEFileStockQuoteBean.class, records);
        }
        if (Objects.isNull(sseFileQuoteBean)) {
            return null;
        }
        // 解析涨跌停价
        String[] contents = StringUtil.split(interfQuoteShFileBean.getCpxx(), GlobalConstant.VERTICAL_LINE);
        // 字段相关索引位置按交易所文件固定查询获取
        if (contents.length < 23) {
            logger.warn("记录格式错误，请检查该数据是否合法！！ record [{}]", Arrays.toString(contents));
        } else {
            // 无涨跌停价格
            if (!LimitType.isNotLimitPrice(contents[21].trim())) {
                tick.setUpperLimitPrice(Double.parseDouble(contents[22].trim()));
                tick.setLowerLimitPrice(Double.parseDouble(contents[23].trim()));
            }
        }
        tick.setExchange(Market.SHSE.value);
        String stockCode = StockCodeUtils.formatStockCode(sseFileQuoteBean.getSecurityID(), Market.SHSE.value);
        tick.setInstrumentID(stockCode);
        tick.setLastPrice(sseFileQuoteBean.getTradePrice());
        tick.setOpenPrice(sseFileQuoteBean.getOpenPrice());
        tick.setHighPrice(sseFileQuoteBean.getHighPrice());
        tick.setLowPrice(sseFileQuoteBean.getLowPrice());
        tick.setVolume(sseFileQuoteBean.getTradeVolume());
        tick.setTurnover(0); //nsq 没有换手率
        tick.setTradeBalance(sseFileQuoteBean.getTotalValueTraded());
        tick.setSettlePrice(0);
        tick.setPreClosePrice(sseFileQuoteBean.getPreClosePx());
        tick.addBidPrice(sseFileQuoteBean.getBuyPrice1());
        tick.addBidPrice(sseFileQuoteBean.getBuyPrice2());
        tick.addBidPrice(sseFileQuoteBean.getBuyPrice3());
        tick.addBidPrice(sseFileQuoteBean.getBuyPrice4());
        tick.addBidPrice(sseFileQuoteBean.getBuyPrice5());
        tick.addBidVolume(sseFileQuoteBean.getBuyVolume1());
        tick.addBidVolume(sseFileQuoteBean.getBuyVolume2());
        tick.addBidVolume(sseFileQuoteBean.getBuyVolume3());
        tick.addBidVolume(sseFileQuoteBean.getBuyVolume4());
        tick.addBidVolume(sseFileQuoteBean.getBuyVolume5());
        tick.addAskPrice(sseFileQuoteBean.getSellPrice1());
        tick.addAskPrice(sseFileQuoteBean.getSellPrice2());
        tick.addAskPrice(sseFileQuoteBean.getSellPrice3());
        tick.addAskPrice(sseFileQuoteBean.getSellPrice4());
        tick.addAskPrice(sseFileQuoteBean.getSellPrice5());
        tick.addAskVolume(sseFileQuoteBean.getSellVolume1());
        tick.addAskVolume(sseFileQuoteBean.getSellVolume2());
        tick.addAskVolume(sseFileQuoteBean.getSellVolume3());
        tick.addAskVolume(sseFileQuoteBean.getSellVolume4());
        tick.addAskVolume(sseFileQuoteBean.getSellVolume5());
        tick.setUpdateTime(calTimeStamp(sseFileQuoteBean.getTimestamp()));
        return tick;
    }

    public static long calTimeStamp(LocalTime localTime) {
        // 当前年月日时间 + 行情时、分、秒、毫秒时间
        long dayTime = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return dayTime + (localTime.getHour() * 3600 + localTime.getMinute() * 60 + localTime.getSecond()) * 1000 + localTime.getNano() / 1000000;
    }

    public static <T> T setParam(Class<T> clazz, String[] args) {
        try {
            if (clazz == null || args == null) {
                return null;
            }
            T t = clazz.newInstance();
            Class<? super T> superclass = clazz.getSuperclass();
            List<Field> fieldList = new ArrayList<>(Arrays.asList(superclass.getDeclaredFields()));
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            if (fieldList.size() > args.length) {
                logger.error("类字段与文件字段个数不对应，请确认该记录正确！！ fields length {} -- record {} -- length {}", fieldList.size(), Arrays.toString(args), args.length);
                return null;
            }
            for (int i = 0; i < fieldList.size(); i++) {
                Field field = fieldList.get(i);
                field.setAccessible(true);
                String typeName = field.getGenericType().getTypeName();
                args[i] = args[i].trim();
                Object arg = args[i];
                switch (typeName) {
                    case "java.lang.Integer":
                        arg = Integer.parseInt(args[i]);
                        break;
                    case "java.lang.Double":
                        arg = Double.parseDouble(args[i]);
                        break;
                    case "java.lang.Long":
                        arg = Long.parseLong(args[i]);
                        break;
                    case "java.time.LocalTime":
                        arg = LocalTime.parse(args[i]);
                        break;
                }
                field.set(t, arg);
            }
            return t;
        } catch (Exception e) {
            logger.error("setParam failed args {} exception {}", Arrays.toString(args), e);
            return null;
        }
    }
}

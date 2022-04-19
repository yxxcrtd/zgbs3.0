package com.joyintech.exchange.service.quote.service;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.joyintech.exchange.service.quote.bean.InterfQuoteBean;
import com.joyintech.exchange.service.quote.enums.InterfSrcType;
import com.joyintech.exchange.service.quote.sh.file.utils.SSEFileConverterUtil;
import com.joyintech.exchange.service.quote.sz.binary.utils.SZSEBinaryConverterUtil;
import com.joyintech.exchange.service.quote.utils.InterfHttpRequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.QuoteDataProto;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.TickByte;
import com.joyintech.tams.exchange.common.cache.QuoteTickService;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
//import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.proto.QuoteProto;
import com.joyintech.tams.exchange.common.utils.TestConverter;
import com.joyintech.tams.exchange.common.utils.TickConverter;
//import com.joyintech.tams.exchange.datacommon.db.service.FuturesInstrumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class QuoteService {
    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);
    private ThreadLocal<QuoteProto.TickBean.Builder> threadLocalBuilder = ThreadLocal.withInitial(QuoteProto.TickBean::newBuilder);

    @Autowired
    private QuoteTickService quoteTickService;
//    @Autowired
//    private FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private InterfHttpRequestUtil interfHttpRequestUtil;

    /**
     * 是否使用接口平台行情数据
     */
    @Value("${quote.flag:false}")
    private boolean useInterfQuoteFlag;

    public ResultBean getTick(String stockCode) {
        TickBean tickBean = null;

//        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByStockCode(stockCode);
//        if (Objects.nonNull(futuresInstrument)) {
//            stockCode = futuresInstrument.getInnerCode();
//        }
        if (useInterfQuoteFlag) {
            String code = interfHttpRequestUtil.getSimpleStockCode(stockCode);
            String quoteStr = interfHttpRequestUtil.doRequest(code);
            tickBean = this.convertTick(quoteStr);
        } else {
            tickBean = TickConverter.convertTick(quoteTickService.findFromRedisById(stockCode));
        }
        if (Objects.isNull(tickBean)) {
            logger.debug("[行情服务] 获取行情失败");
            return  new ResultBean(ErrorCode.QUOTATION_NOT_EXIST);
        }
        logger.debug("[行情服务] 获取行情：证券代码{}，行情数据{}", stockCode, JSON.toJSONString(tickBean));
        return new ResultBean(ErrorCode.SUCCESS, tickBean);
    }

    public ResultBean addTicks(List<TickBean> tickBeanList, byte[] getSnapshot) {
        Map<String, TickByte> tickBeans = new HashMap<>();
        QuoteProto.TickBean.Builder builder = threadLocalBuilder.get();
        if (Objects.nonNull(tickBeanList)) {
            try {
                tickBeanList.forEach(tickBean -> {
                    TickConverter.convertToQuoteProtoTickBean(tickBean, builder);
                    QuoteProto.TickBean tickBean1 = builder.build();
                    tickBeans.put(builder.getInstrumentID(), new TickByte(tickBean1.toByteArray()));
                });
            } catch (Exception e) {
                logger.error("getSnapshot error:{}", e);
                return new ResultBean(ErrorCode.SUCCESS, "获取数据失败");
            }
        } else {
            try {
                logger.info("start write quote data ...");
                QuoteDataProto.QuoteResult quoteResult = QuoteDataProto.QuoteResult.parseFrom(getSnapshot);
                List<ByteString> byteStringList = quoteResult.getDataList();
                byteStringList.forEach(bytes -> {
                    try {
                        QuoteDataProto.TickBean tickBean = QuoteDataProto.TickBean.parseFrom(bytes);
                        builder.clear();
                        QuoteProto.TickBean newTick = TestConverter.convertTick(tickBean, builder);
                        tickBeans.put(newTick.getInstrumentID(), new TickByte(newTick.toByteArray()));
                    } catch (InvalidProtocolBufferException e) {
                        logger.error("tickBean parseFrom:{}", e);
                    }
                });
            } catch (Exception e) {
                logger.error("getSnapshot error:{}", e);
            }
        }
        quoteTickService.saveTicks(tickBeans);
        logger.info("[行情服务] 写入行情数据成功");
        return new ResultBean(ErrorCode.SUCCESS, "存入数据成功");
    }

    public TickBean convertTick(InterfQuoteBean interfQuoteBean) {
        return convertTick(JSON.toJSONString(interfQuoteBean));
    }

    public TickBean convertTick(String message) {
        if (StringUtil.isEmpty(message)) {
            logger.warn("[行情服务] 行情数据转换 message is null!!!");
            return null;
        }
        InterfQuoteBean interfQuoteBean = null;
        try {
             interfQuoteBean = JSON.parseObject(message, InterfQuoteBean.class);
        } catch (Exception e) {
            return null;
        }
        TickBean tickBean = null;
        if (InterfSrcType.SH_FILE.code.equals(interfQuoteBean.getSrc())) {
            tickBean = SSEFileConverterUtil.convertTick(interfQuoteBean.getData());
        } else if (InterfSrcType.SZ_BINARY.code.equals(interfQuoteBean.getSrc())) {
            tickBean = SZSEBinaryConverterUtil.convertTick(interfQuoteBean.getData());
        }
        logger.info("[行情服务] 行情数据转换 message：{}, tick：{}", message, JSON.toJSONString(tickBean));
        return tickBean;
    }

}

package com.joyintech.service.trade.entrustmsgbymq.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.BaseTest;
import com.joyintech.service.trade.entrustmsgbymq.dto.TradeMessage;
import com.joyintech.tams.component.business.core.service.impl.BaseGetBusinessIdImpl;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Matchers.any;

/**
 * @author huwei1
 * @description 委托成交消息主推接口Service 测试类
 * @createDate 2022/6/2
 */
public class EntrustMsgbyMQServiceTest extends BaseTest {

    @Autowired
    private EntrustMsgbyMQService entrustMsgbyMQService;

    @MockBean
    BaseGetBusinessIdImpl BaseGetBusinessIdImpl;

    @MockBean
    AutoPeriodCommonService autoPeriodCommonService;

    /**
     * 【场内交易-头寸引擎】多组合基金买入, MQ接收 成交回报报文
     */
    @Test
    public void insertEntrustMsg() throws Exception {
        String tradeMessageStr = "{\"acctNo\":\"12345:ZJ51702\",\"ccy\":\"CNY\",\"dealBalance\":1660.00,\"dealNo\":\"175428761\",\"dealPrice\":10,\"dealVolume\":166.0,\"entrustId\":\"WTFUND202112214890\",\"entrustNo\":\"2060117542800035\",\"entrustVolume\":1000.0,\"finprodId\":\"F06_100001.SH\",\"finprodMarketId\":\"100001.SH\",\"fullPrice\":10.0,\"id\":\"22060117542900093\",\"orderId\":\"e22060117542800035\",\"pS\":\"T01\",\"portfolioId\":\"P05170201,P51702,P5170202\",\"portfolioTradeMessages\":[{\"busiId\":\"22060117542900093\",\"entrustAmt\":7000,\"entrustNum\":700,\"portfolioId\":\"P51702\",\"portfolioName\":\"组合51702\",\"targetType\":\"02\",\"tradeAmt\":660.00,\"tradeNum\":66},{\"busiId\":\"22060117542900093\",\"entrustAmt\":1000,\"entrustNum\":100,\"portfolioId\":\"P5170202\",\"portfolioName\":\"组合5170202\",\"targetType\":\"02\",\"tradeAmt\":0.00,\"tradeNum\":0},{\"busiId\":\"22060117542900093\",\"entrustAmt\":2000,\"entrustNum\":200,\"portfolioId\":\"P05170201\",\"portfolioName\":\"组合5170201\",\"targetType\":\"02\",\"tradeAmt\":1000.00,\"tradeNum\":100}],\"secAcctId\":\"ZQ51702\",\"tradeMarket\":\"03\",\"tradePlat\":\"02\",\"tradingDay\":\"20211221\"}";
        Mockito.doReturn("我是模拟的返回值").when(BaseGetBusinessIdImpl).buildBussinessCode(any());
        Mockito.doReturn("我是模拟的返回值").when(autoPeriodCommonService).buildBussinessCode(any());
        TradeMessage tradeMessage = JSON.parseObject(tradeMessageStr, TradeMessage.class);
        entrustMsgbyMQService.insertEntrustMsg(tradeMessage);
    }

    /**
     * 【场内交易-头寸引擎】信用保护凭证, MQ接收 成交回报报文
     */
    @Test
    public void insertF32EntrustMsg() throws Exception {
        String tradeMessageStr = "{\"acctNo\":\"123456:ZJ52702\",\"ccy\":\"CNY\",\"counterId\":\"JYDS000000001546\",\"dealBalance\":186666690.00,\"dealNo\":\"1659381606\",\"dealPrice\":105,\"dealVolume\":1777778.0,\"entrustId\":\"WTCDS202112217249\",\"entrustNo\":\"2060916593800003\",\"entrustVolume\":8000000.0,\"finprodId\":\"F37_002.SH\",\"finprodMarketId\":\"002.SH\",\"fullPrice\":105.0,\"id\":\"22060916593800020\",\"orderId\":\"i22060916593800003\",\"pS\":\"T04\",\"portfolioId\":\"CP601\",\"portfolioTradeMessages\":[{\"busiId\":\"22060916593800020\",\"entrustAmt\":840000000,\"entrustNum\":8000000,\"portfolioId\":\"CP601\",\"portfolioName\":\"产品601\",\"targetType\":\"01\",\"tradeAmt\":186666690.00,\"tradeNum\":1777778}],\"propAcctNo\":\":80000010101\",\"secAcctId\":\"ZQ52702\",\"tradeMarket\":\"03\",\"tradePlat\":\"99\",\"tradingDay\":\"20211221\"}";
        Mockito.doReturn("我是模拟的返回值").when(BaseGetBusinessIdImpl).buildBussinessCode(any());
        Mockito.doReturn("我是模拟的返回值").when(autoPeriodCommonService).buildBussinessCode(any());
        TradeMessage tradeMessage = JSON.parseObject(tradeMessageStr, TradeMessage.class);
        entrustMsgbyMQService.insertEntrustMsg(tradeMessage);
    }
}
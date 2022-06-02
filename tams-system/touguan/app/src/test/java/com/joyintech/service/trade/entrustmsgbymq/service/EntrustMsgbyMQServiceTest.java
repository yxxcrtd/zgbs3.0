package com.joyintech.service.trade.entrustmsgbymq.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.service.trade.entrustmsgbymq.dto.TradeMessage;
import com.joyintech.tams.TamsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author huwei1
 * @description
 * @createDate 2022/6/2
 */
@SpringBootTest(classes = TamsApplication.class)
@ActiveProfiles("allinone")
@RunWith(SpringRunner.class)
public class EntrustMsgbyMQServiceTest {

    @Autowired
    EntrustMsgbyMQService entrustMsgbyMQService;

    @Test
    public void insertEntrustMsg() throws Exception {
        String tradeMessageStr = "{\"acctNo\":\"12345:ZJ51702\",\"ccy\":\"CNY\",\"dealBalance\":1660.00,\"dealNo\":\"175428761\",\"dealPrice\":10,\"dealVolume\":166.0,\"entrustId\":\"WTFUND202112214890\",\"entrustNo\":\"2060117542800035\",\"entrustVolume\":1000.0,\"finprodId\":\"F06_100001.SH\",\"finprodMarketId\":\"100001.SH\",\"fullPrice\":10.0,\"id\":\"22060117542900093\",\"orderId\":\"e22060117542800035\",\"pS\":\"T01\",\"portfolioId\":\"P05170201,P51702,P5170202\",\"portfolioTradeMessages\":[{\"busiId\":\"22060117542900093\",\"entrustAmt\":7000,\"entrustNum\":700,\"portfolioId\":\"P51702\",\"portfolioName\":\"组合51702\",\"targetType\":\"02\",\"tradeAmt\":660.00,\"tradeNum\":66},{\"busiId\":\"22060117542900093\",\"entrustAmt\":1000,\"entrustNum\":100,\"portfolioId\":\"P5170202\",\"portfolioName\":\"组合5170202\",\"targetType\":\"02\",\"tradeAmt\":0.00,\"tradeNum\":0},{\"busiId\":\"22060117542900093\",\"entrustAmt\":2000,\"entrustNum\":200,\"portfolioId\":\"P05170201\",\"portfolioName\":\"组合5170201\",\"targetType\":\"02\",\"tradeAmt\":1000.00,\"tradeNum\":100}],\"secAcctId\":\"ZQ51702\",\"tradeMarket\":\"03\",\"tradePlat\":\"02\",\"tradingDay\":\"20211221\"}";
        TradeMessage tradeMessage = JSON.parseObject(tradeMessageStr, TradeMessage.class);
        entrustMsgbyMQService.insertEntrustMsg(tradeMessage);
    }
}
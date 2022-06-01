package com.joyintech.tams.exchange.service.trade.processor.check.concreate;

import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.constant.TradePlat;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.service.trade.processor.check.IBusinessCheckStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 校验深圳综合协议交易
 *
 * @author swwan
 * @date 2022/4/24 - 17:34
 */
@Component
public class SzProtocolCheckStrategy implements IBusinessCheckStrategy {
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(BidCheckStrategy.class);

    @Override
    public boolean support(Instruction instruction) {
        return TradePlat.SZ_PROTOCOL.codeEquals(instruction.getTradePlat())
                && EnumUtil.in(instruction.getAssetType(), InvestAssetTypeEnum.FUND, InvestAssetTypeEnum.STOCK, InvestAssetTypeEnum.BOND);
    }

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) {
        log.info("check() called with parameters => 【order = {}】, 【instruction = {}】, 【instrument = {}】, 【tickBean = {}】", order, instruction, instrument, tickBean);
        return ResultBean.success();
    }
}
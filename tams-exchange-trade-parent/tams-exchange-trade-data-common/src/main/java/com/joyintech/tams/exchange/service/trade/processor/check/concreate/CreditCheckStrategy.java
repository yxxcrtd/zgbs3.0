package com.joyintech.tams.exchange.service.trade.processor.check.concreate;

import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.constant.InstructionPs;
import com.joyintech.tams.exchange.common.constant.TradePlat;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.service.trade.processor.check.IBusinessCheckStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3.投管业务类型_信用保护凭证;

/**
 * @author lifangpeng
 * @date 2022/5/31 11:42
 */
@Component
public class CreditCheckStrategy implements IBusinessCheckStrategy {
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(CreditCheckStrategy.class);

    @Override
    public boolean support(Instruction instruction) {
        return 投管业务类型_信用保护凭证.getValue().equals(instruction.getBusiTypeTg());
    }

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) {
        log.info("CreditCheckStrategy：check() called with parameters => 【order = {}】, 【instruction = {}】, 【instrument = {}】, 【tickBean = {}】", order, instruction, instrument, tickBean);
        return ResultBean.success();
    }
}

package com.joyintech.tams.exchange.service.trade.processor.check.concreate;

import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.service.trade.processor.check.IBusinessCheckStrategy;
import org.springframework.stereotype.Component;

/**
 * 校验新债
 *
 * @author swwan
 * @date 2022/4/24 - 20:07
 */
@Component
public class NewBondCheckStrategy implements IBusinessCheckStrategy {
    @Override
    public boolean support(Instruction instruction) {
        return instruction.inconvertibleBond();
    }

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) {
        return ResultBean.success();
    }
}

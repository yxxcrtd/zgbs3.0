package com.joyintech.tams.exchange.service.trade.processor.check;

import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;

/**
 * @author swwan
 * @date 2022/4/24 - 17:16
 */
public interface IBusinessCheckStrategy {

    boolean support(Instruction instruction);

    ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean);
}

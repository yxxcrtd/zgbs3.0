package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.IBusinessCheck;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import org.springframework.stereotype.Component;

/**
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX016")
@Component
public class BusinessCheckEx016 implements IBusinessCheck {

    @Override
    public void check(CheckChainBean chainBean) {

    }
}

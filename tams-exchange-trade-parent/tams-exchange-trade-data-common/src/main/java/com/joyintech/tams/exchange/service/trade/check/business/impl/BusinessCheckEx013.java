package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.service.trade.check.business.AbstractCheck;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.CheckBean;
import org.springframework.stereotype.Component;

/**
 * 到期续做
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX013")
@Component
public class BusinessCheckEx013 extends AbstractCheck {

    @Override
    public void doBusinessCheck(CheckBean chainBean) {

    }
}

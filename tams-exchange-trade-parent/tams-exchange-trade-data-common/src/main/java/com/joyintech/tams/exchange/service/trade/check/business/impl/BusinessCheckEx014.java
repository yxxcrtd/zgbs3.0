package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.service.trade.check.business.AbstractCheck;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.CheckBean;
import org.springframework.stereotype.Component;

/**
 * 中途换券
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX014")
@Component
public class BusinessCheckEx014 extends AbstractCheck {

    @Override
    public void doBusinessCheck(CheckBean chainBean) {

    }
}

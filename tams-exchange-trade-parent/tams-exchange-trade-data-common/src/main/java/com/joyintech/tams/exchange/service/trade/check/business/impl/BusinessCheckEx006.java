package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.service.trade.check.business.AbstractCheck;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.CheckBean;
import org.springframework.stereotype.Component;

/**
 * 配债
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX006")
@Component
public class BusinessCheckEx006 extends AbstractCheck {

    @Override
    public void doBusinessCheck(CheckBean chainBean) {
        super.doBusinessCheck(chainBean);
    }
}

package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.service.trade.check.business.AbstractCheck;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.CheckBean;
import org.springframework.stereotype.Component;

/**
 * 提前购回
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX011")
@Component
public class BusinessCheckEx011 extends AbstractCheck {

    @Override
    public void doBusinessCheck(CheckBean chainBean) {

    }
}

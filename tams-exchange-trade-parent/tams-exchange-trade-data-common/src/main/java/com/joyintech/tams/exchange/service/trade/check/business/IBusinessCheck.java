package com.joyintech.tams.exchange.service.trade.check.business;

import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;

/**
 * @author lifangpeng
 * @date 2022/4/6 16:43
 */
public interface IBusinessCheck {

    /**
     * 具体业务数据检查
     *
     * @param chainBean 业务数据检查参数
     */
    void check(CheckChainBean chainBean);
}

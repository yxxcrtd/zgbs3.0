package com.joyintech.tams.exchange.service.trade.check.business;

/**
 * @author lifangpeng
 * @date 2022/4/6 16:43
 */
public interface ICheck {

    /**
     * 具体业务数据检查
     *
     * @param chainBean 业务数据检查参数
     */
    void check(CheckBean chainBean);
}

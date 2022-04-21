package com.joyintech.tams.exchange.service.trade.processor.check;

import cn.hutool.extra.validation.BeanValidationResult;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;

/**
 * @author swwan
 * @date 2022/4/13 - 16:03
 */
public abstract class BaseCheckKit {

    protected BeanValidationResult checkResult = new BeanValidationResult(true);

    protected BeanValidationResult getCheckResult() {
        return checkResult;
    }

    public abstract ResultBean check(BaseOrderRequest order);
}

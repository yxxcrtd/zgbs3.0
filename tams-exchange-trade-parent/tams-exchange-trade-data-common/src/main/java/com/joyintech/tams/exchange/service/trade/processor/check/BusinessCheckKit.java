package com.joyintech.tams.exchange.service.trade.processor.check;

import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.enums.SwitcheEnum;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author swwan
 * @date 2022/4/24 - 16:30
 */
@Component
public class BusinessCheckKit extends BaseCheckKit {

    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(BusinessCheckKit.class);

    @Autowired
    List<IBusinessCheckStrategy> strategies = Lists.newArrayList();

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) throws BusinessResultException {
        ResultBean resultBean = strategies.stream()
                .filter(item -> item.support(instruction))
                .findFirst()
                .orElseThrow(() -> new BusinessResultException(ErrorCode.REQ_PARAMETER_ERROR))
                .check(order, instruction, instrument, tickBean);

        //软控
        if (ErrorCode.isSoftControlFail(resultBean.getErrorCode())) {
            if (!SwitcheEnum.OPEN.codeEquals(order.getOrderSoftControl())) {
                return resultBean;
            }
            return ResultBean.success();
        }
        return resultBean;
    }
}

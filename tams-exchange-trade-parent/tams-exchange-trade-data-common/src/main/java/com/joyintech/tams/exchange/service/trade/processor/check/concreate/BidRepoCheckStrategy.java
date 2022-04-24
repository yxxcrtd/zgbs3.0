package com.joyintech.tams.exchange.service.trade.processor.check.concreate;

import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.service.trade.processor.check.IBusinessCheckStrategy;
import org.springframework.stereotype.Component;

import static com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1.BIDDING_REPO;

/**
 * 校验质押式竞价回购
 *
 * @author swwan
 * @date 2022/4/24 - 18:26
 */
@Component
public class BidRepoCheckStrategy implements IBusinessCheckStrategy {

    @Override
    public boolean support(Instruction instruction) {
        return BIDDING_REPO.codeEquals(instruction.getAssetType1());

    }

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) {
        return ResultBean.success();
    }
}
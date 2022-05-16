package com.joyintech.tams.exchange.service.trade.processor.check.concreate;

import cn.hutool.core.util.StrUtil;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.bean.request.TradeInfoProRequest;
import com.joyintech.tams.exchange.common.bean.response.TradeInfoProResponse;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.service.trade.management.service.CommonManagement;
import com.joyintech.tams.exchange.service.trade.processor.check.IBusinessCheckStrategy;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 校验新债
 *
 * @author swwan
 * @date 2022/4/24 - 20:07
 */
@Component
public class NewBondCheckStrategy implements IBusinessCheckStrategy {
    @Autowired
    private CommonManagement commonManagement;

    @Override
    public boolean support(Instruction instruction) {
        return instruction.newBond();
    }

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) {
        IntegratedEntrust entrust = new IntegratedEntrust();
        try {
            BeanUtil.copyProperties(entrust, order);
        } catch (Exception e) {
            throw new BusinessResultException(ErrorCode.SYSTEM_ERROR);
        }
        String entrustProp = order.getEntrustProp();
        if (EntrustProp.isConsultingTrade(entrustProp)) {
            return checkCounterDealerTraderInfo(entrust, instruction);
        }
        
        return ResultBean.success();
    }

    /**
     * 检查对手方交易商、交易主体、交易员
     */
    private ResultBean checkCounterDealerTraderInfo(IntegratedEntrust entrust, Instruction instruction) {
        TradeInfoProRequest dto = new TradeInfoProRequest();
        dto.setTradePlace(instruction.getTradeMarket());
        dto.setDealerNo(entrust.getPropDealerId());
        List<TradeInfoProResponse> list = commonManagement.getCounterDealerTraderInfo(dto);
        String errorMsg = "";
        if (CollectionUtils.isEmpty(list)) {
            errorMsg += StrUtil.format("对手方交易商[{}]未维护<br/>", entrust.getPropDealerId());
        } else {
            if (StringUtil.isNotEmpty(entrust.getPropInvestorId())) {
                Optional<TradeInfoProResponse> first = list.stream().filter(o -> StringUtil.equals(o.getTraderNo(), entrust.getPropInvestorId())).findFirst();
                if (!first.isPresent()) {
                    errorMsg += StrUtil.format("对手方交易主体[{}]未维护<br/>", entrust.getPropInvestorId());
                }
            }
            Optional<TradeInfoProResponse> propTrade = list.stream().filter(o -> StringUtil.equals(o.getTraderNo(), entrust.getPropTradeId())).findFirst();
            if (!propTrade.isPresent()) {
                errorMsg += StrUtil.format("对手方交易员[{}]未维护<br/>", entrust.getPropTradeId());
            }
        }

        if (StringUtil.isNotEmpty(errorMsg)) {
            ResultBean error = ResultBean.error(ErrorCode.TRIGGER_SOFT_CONTROL);
            error.setErrorMsg(errorMsg);
            return error;
        }

        return ResultBean.success();
    }

}

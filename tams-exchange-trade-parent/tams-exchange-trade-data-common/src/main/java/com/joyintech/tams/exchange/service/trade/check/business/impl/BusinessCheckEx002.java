package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.IBusinessCheck;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 大宗交易
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX002")
@Component
public class BusinessCheckEx002 implements IBusinessCheck {

    @Override
    public void check(CheckChainBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        //债券大宗交易
        if (InvestmentAssetType.isBond(instruction.getAssetType())) {
            if (!SubStockType.KZZ.value.trim().equals(instruction.getAssetType1())) {
                checkRsp.put("assetType1", "债券大宗交易只支持可转债");
            }

        }
    }
}

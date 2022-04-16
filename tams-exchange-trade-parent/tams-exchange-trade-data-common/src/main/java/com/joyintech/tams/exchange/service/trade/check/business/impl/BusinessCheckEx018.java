package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.IBusinessCheck;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 现券交易
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX018")
@Component
public class BusinessCheckEx018 implements IBusinessCheck {

    @Override
    public void check(CheckChainBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        if (!InvestmentAssetType.isBond(instruction.getAssetType())) {
            checkRsp.put("assetType", "现券交易只支持债券");
        }
        if (SubStockType.KZZ.value.equals(instruction.getAssetType1())) {
            checkRsp.put("assetType1", "现券交易只支持非可转债");
        }
        if (!EntrustProp.isNewBondTrade(instruction.getDealMode())) {
            checkRsp.put("dealMode", "成交方式只支持匹配成交或协商成交");
        }

    }
}

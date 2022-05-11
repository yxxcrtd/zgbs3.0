package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt1;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.IBusinessCheck;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 协议现券交易
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX016")
@Component
public class BusinessCheckEx016 implements IBusinessCheck {

    @Override
    public void check(CheckChainBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        if (!InvestmentAssetType.isBond(instruction.getAssetType())) {
            checkRsp.put("assetType", "协议现券交易只支持债券");
        }
        if (!SubStockType.KZZ.value.trim().equals(instruction.getAssetType1())) {
            checkRsp.put("assetType1", "协议现券交易只支持可转债");
        }

        instruction.setIsNetPrice(DictCodeListExt1.是否_否.getValue());
    }
}

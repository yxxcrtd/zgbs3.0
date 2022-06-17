package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt1;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.AbstractCheck;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.CheckBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 竞价交易
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX001")
@Component
public class BusinessCheckEx001 extends AbstractCheck {

    @Override
    public void doBusinessCheck(CheckBean chainBean) {
        super.doBusinessCheck(chainBean);

        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        //债券竞价交易
        if (InvestmentAssetType.isBond(instruction.getAssetType())) {
            if (!SubStockType.KZZ.value.trim().equals(instruction.getAssetType1())) {
                checkRsp.put("assetType1", "债券竞价交易只支持可转债");
            }
            instruction.setIsNetPrice(DictCodeListExt1.是否_否.getValue());
        }

    }
}

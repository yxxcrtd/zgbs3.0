package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.IBusinessCheck;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 通用质押式回购
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX017")
@Component
public class BusinessCheckEx017 implements IBusinessCheck {

    @Override
    public void check(CheckChainBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        if (!StringUtil.equals(InvestmentAssetType.PLEDGE_REPURCHASE.value, instruction.getAssetType())) {
            checkRsp.put("assetType", "资产类型非质押式回购");
        }
        if (!StringUtil.equals(InvestmentAssetType1.BIDDING_REPO.value, instruction.getAssetType1())) {
            checkRsp.put("assetType1", "二级资产类型非质押式回购");
        }
        String ps = instruction.getPs();
        if (!DictCodeListExt2.交易方向_正回购.getValue().equals(ps) && !DictCodeListExt2.交易方向_逆回购.getValue().equals(ps)) {
            checkRsp.put("ps", "通用质押式回购交易方向只支持正回购或逆回购");
        }
        if (Objects.isNull(instruction.getEntrustAmt())) {
            checkRsp.put("entrustAmt", "回购金额不能为空或0");
        }
    }
}

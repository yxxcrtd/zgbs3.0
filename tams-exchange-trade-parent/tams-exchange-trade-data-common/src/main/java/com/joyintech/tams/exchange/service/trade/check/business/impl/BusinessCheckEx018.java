package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
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
@Check(action = "EX018")
@Component
public class BusinessCheckEx018 implements IBusinessCheck {

    @Override
    public void check(CheckChainBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        if (!StringUtil.equals(InvestAssetTypeEnum.PLEDGE_REPURCHASE.value, instruction.getAssetType())) {
            checkRsp.put("assetType", "资产类型非质押式回购");
        }
        if (!StringUtil.equals(InvestmentAssetType1.BIDDING_REPO.value, instruction.getAssetType1())) {
            checkRsp.put("assetType1", "二级资产类型非质押式回购");
        }
        final String ps = instruction.getPs();
        if (!DictCodeListExt2.交易方向_正回购.getValue().equals(ps) && !DictCodeListExt2.交易方向_逆回购.getValue().equals(ps)) {
            checkRsp.put("ps", "通用质押式回购交易方向只支持正回购或逆回购");
        }
        if (Objects.isNull(instruction.getEntrustAmt())) {
            checkRsp.put("entrustAmt", "回购金额不能为空或0");
        }
        if (!DictCodeListExt3.目标类型_金额.getValue().equals(instruction.getTargetType())) {
            checkRsp.put("targetType", "通用质押式回购目标类型只支持金额");
        }
    }
}

package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.IBusinessCheck;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 质押券出入库
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX005")
@Component
public class BusinessCheckEx005 implements IBusinessCheck {

    @Override
    public void check(CheckChainBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        String ps = instruction.getPs();
        if (!DictCodeListExt2.交易方向_质押券入库.getValue().equals(ps) && !DictCodeListExt2.交易方向_质押券出库.getValue().equals(ps)) {
            checkRsp.put("ps", "质押券出入库交易方向只支持质押入库或质押出库");
        }
        if (DictCodeListExt2.交易方向_质押券入库.getValue().equals(ps) && !StringUtil.equals(InvestAssetTypeEnum.BOND.value, instruction.getAssetType())) {
            checkRsp.put("assetType", "质押入库只支持债券资产[F01]");
        }
        if (DictCodeListExt2.交易方向_质押券出库.getValue().equals(ps) && !StringUtil.equals(InvestAssetTypeEnum.PLEDGE_REPURCHASE.value, instruction.getAssetType())) {
            checkRsp.put("assetType", "质押出库只支持质押式回购资产[F07]");
        }
        if (instruction.getEntrustNum() == 0) {
            checkRsp.put("entrustNum", "质押券数量不能为空或0");
        }
        if (Objects.isNull(instruction.getEntrustPrice()) || instruction.getEntrustPrice().compareTo(BigDecimal.ZERO) < 1) {
            checkRsp.put("entrustPrice", "折算率不能为空或0");
        }
    }
}

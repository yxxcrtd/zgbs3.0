package com.joyintech.tams.exchange.service.trade.check.business.impl;

import cn.hutool.core.util.StrUtil;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.IBusinessCheck;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.joyintech.tams.exchange.common.constant.InstructionPs.*;

/**
 * 信用保护凭证
 *
 * @author lifangpeng
 * @date 2022/5/17 10:41
 */
@Check(action = "EX019")
@Component
public class BusinessCheckEx019 implements IBusinessCheck {

    @Override
    public void check(CheckChainBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        if (!InvestAssetTypeEnum.isCreditProtectionCertificate(instruction.getAssetType())) {
            checkRsp.put("assetType", StrUtil.format("资产类型不合法：{}", instruction.getAssetType()));
        }

        if (!EnumUtil.in(instruction.getPs(), BUY, SELL, APPLY)) {
            checkRsp.put("ps", StrUtil.format("信用保护凭证交易方向只支持申购、买入或卖出：{}", instruction.getPs()));
        }
//        //指令数量单位是元，转换成张
//        instruction.setEntrustNum(instruction.getEntrustNum() / 100);
    }
}

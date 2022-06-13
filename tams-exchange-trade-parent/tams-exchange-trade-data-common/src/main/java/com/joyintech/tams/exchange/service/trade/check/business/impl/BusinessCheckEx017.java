package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.constant.joyin.DictCode;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt1;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.IBusinessCheck;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 现券交易
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
        if (!InvestAssetTypeEnum.isBond(instruction.getAssetType())) {
            checkRsp.put("assetType", "现券交易只支持债券");
        }
        if (SubStockType.KZZ.value.trim().equals(instruction.getAssetType1())) {
            checkRsp.put("assetType1", "现券交易只支持非可转债");
        }
        if (StringUtil.isNotEmpty(instruction.getDealMode()) && !EntrustProp.isNewBondTrade(instruction.getDealMode())) {
            checkRsp.put("dealMode", "成交方式只支持匹配成交或协商成交");
        }

        if (StringUtils.isBlank(instruction.getIsNoGuard())) {
            checkRsp.put("isNoGuard", "是否非担保不能为空");
        } else if (null == DictCodeListExt1.from(DictCode.是否, instruction.getIsNoGuard())) {
            checkRsp.put("isNoGuard", "是否非担保取值不合法:" + instruction.getIsNoGuard());
        }

        if (StringUtils.isBlank(instruction.getIsNetPrice())) {
            checkRsp.put("isNetPrice", "是否净价交易不能为空");
        } else if (null == DictCodeListExt1.from(DictCode.是否, instruction.getIsNetPrice())) {
            checkRsp.put("isNetPrice", "是否净价交易取值不合法:" + instruction.getIsNetPrice());
        }
    }
}

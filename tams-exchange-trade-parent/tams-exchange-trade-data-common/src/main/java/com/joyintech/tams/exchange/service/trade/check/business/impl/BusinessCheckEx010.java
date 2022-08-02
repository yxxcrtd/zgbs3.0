package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.AbstractCheck;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.CheckBean;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 协议回购首期
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX010")
@Component
public class BusinessCheckEx010 extends AbstractCheck {

    @Override
    public void doBusinessCheck(CheckBean chainBean) {
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();

        if (StringUtil.equals(DictCodeListExt2.交易方向_正回购.getValue(), instruction.getPs())) {
            if (CollectionUtils.isEmpty(chainBean.getPledgeList())) {
                checkRsp.put("instructionPledge", "正回购首期质押券不能为空");
            } else if (chainBean.getPledgeList().size() > 1) {
                checkRsp.put("instructionPledge", "正回购首期质押券数量不能超过1");
            }
        }
    }
}

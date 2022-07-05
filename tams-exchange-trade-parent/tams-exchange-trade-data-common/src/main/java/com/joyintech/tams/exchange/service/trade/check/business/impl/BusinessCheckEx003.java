package com.joyintech.tams.exchange.service.trade.check.business.impl;

import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.business.AbstractCheck;
import com.joyintech.tams.exchange.service.trade.check.business.Check;
import com.joyintech.tams.exchange.service.trade.check.business.CheckBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 行权-回售
 *
 * @author lifangpeng
 * @date 2022/4/6 17:06
 */
@Check(action = "EX003")
@Component
public class BusinessCheckEx003 extends AbstractCheck {

    @Override
    public void doBusinessCheck(CheckBean chainBean) {
        super.doBusinessCheck(chainBean);
        Map<String, Object> checkRsp = chainBean.getCheckRsp();
        Instruction instruction = chainBean.getInstruction();
        if (StringUtils.isBlank(instruction.getStockHolderNo())) {
            checkRsp.put("stockHolderNo", "股东代码不能为空");
        }
    }
}

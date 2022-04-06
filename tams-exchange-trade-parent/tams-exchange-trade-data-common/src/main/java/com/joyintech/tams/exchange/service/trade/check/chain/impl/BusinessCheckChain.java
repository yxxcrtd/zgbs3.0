package com.joyintech.tams.exchange.service.trade.check.chain.impl;

import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.service.trade.check.chain.AbstractChain;
import com.joyintech.tams.exchange.service.trade.check.business.CheckActions;
import com.joyintech.tams.exchange.service.trade.check.business.IBusinessCheck;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 具体业务检查
 *
 * @author lifangpeng
 * @date 2022/4/1 14:34
 */
@Component
public class BusinessCheckChain extends AbstractChain<CheckChainBean> {
    private static final Logger logger = LoggerFactory.getLogger(BusinessCheckChain.class);

    @Override
    public void doChain(CheckChainBean chainBean) {
        Instruction instruction = chainBean.getInstruction();
        String busiTypeTg = instruction.getBusiTypeTg();
        logger.info("指令分发具体业务检查器：busiTypeTg={}", busiTypeTg);
        IBusinessCheck businessCheck = CheckActions.I.action(busiTypeTg);
        businessCheck.check(chainBean);
    }
}

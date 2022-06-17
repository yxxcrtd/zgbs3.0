package com.joyintech.tams.exchange.service.trade.check.business;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 具体业务检查
 *
 * @author lifangpeng
 * @date 2022/4/1 14:34
 */
@Component
public class BusinessCheckHandler {
    private static final Logger logger = LoggerFactory.getLogger(BusinessCheckHandler.class);

    public void doCheck(CheckBean chainBean) {
        Instruction instruction = chainBean.getInstruction();
        String busiTypeTg = instruction.getBusiTypeTg();
        logger.info("指令分发具体业务检查器：busiTypeTg={}", busiTypeTg);
        if (StringUtil.isEmpty(busiTypeTg)) {
            logger.error("指令业务类型为空，找不到对应的检查器");
            return;
        }
        ICheck businessCheck = CheckActions.I.action(busiTypeTg);
        if (Objects.isNull(businessCheck)) {
            logger.error("没有找到对应的检查器：busiTypeTg={}", busiTypeTg);
            return;
        }
        businessCheck.check(chainBean);
    }
}

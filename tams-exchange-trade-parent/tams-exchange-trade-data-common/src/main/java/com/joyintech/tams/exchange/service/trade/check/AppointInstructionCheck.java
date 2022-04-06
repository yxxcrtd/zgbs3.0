package com.joyintech.tams.exchange.service.trade.check;

import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.InstructionDetail;
import com.joyintech.tams.exchange.common.db.domain.InstructionPledge;
import com.joyintech.tams.exchange.service.trade.check.chain.impl.BusinessCheckChain;
import com.joyintech.tams.exchange.service.trade.check.chain.CheckChainBean;
import com.joyintech.tams.exchange.service.trade.check.chain.impl.CommonCheckChain;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 指令分发数据正确性检查
 *
 * @author lifangpeng
 * @date 2022/4/1 14:11
 */
@Component
public class AppointInstructionCheck {
    @Autowired
    private CommonCheckChain commonCheckChain;
    @Autowired
    private BusinessCheckChain businessCheckChain;


    @PostConstruct
    public void initialize() {
        commonCheckChain.nextChain(businessCheckChain);
    }

    public CheckResult checkAppointInstruction(List<Instruction> instructionList, List<InstructionDetail> detailList,
                                               List<InstructionPledge> pledgeList) {
        if (CollectionUtils.isEmpty(instructionList)) {
            return new CheckResult(1, "指令信息不能为空");
        }
        if (CollectionUtils.isEmpty(detailList)) {
            return new CheckResult(1, "指令组合信息不能为空");
        }
        Map<String, List<InstructionDetail>> detailMap = Maps.newHashMap();
        if (Objects.nonNull(detailList)) {
            detailMap.putAll(detailList.stream().collect(Collectors.groupingBy(InstructionDetail::getEntrustId)));
        }
        Map<String, List<InstructionPledge>> pledgeMap = Maps.newHashMap();
        if (Objects.nonNull(pledgeList)) {
            pledgeMap.putAll(pledgeList.stream().collect(Collectors.groupingBy(InstructionPledge::getEntrustId)));
        }

        //检查分发指令数据
        Map<String, Object> errorMap = checkAppointInstruction(instructionList, detailMap, pledgeMap);
        if (MapUtils.isNotEmpty(errorMap)) {
            return new CheckResult(2, "指令数据部分检查不通过", errorMap);
        }

        return new CheckResult(0, "指令数据检查通过");
    }


    private Map<String, Object> checkAppointInstruction(List<Instruction> instructionList, Map<String, List<InstructionDetail>> detailMap,
                                                        Map<String, List<InstructionPledge>> pledgeMap) {
        Map<String, Object> errorMap = Maps.newHashMap();
        instructionList.forEach(instruction -> {
            String entrustId = instruction.getEntrustId();
            Map<String, Object> checkRsp = doCheck(instruction, detailMap.get(entrustId), pledgeMap.get(entrustId));
            if (MapUtils.isNotEmpty(checkRsp)) {
                errorMap.put(entrustId, checkRsp);
            }
        });

        return errorMap;
    }

    private Map<String, Object> doCheck(Instruction instruction, List<InstructionDetail> detailList, List<InstructionPledge> pledgeList) {
        Map<String, Object> checkRsp = Maps.newHashMap();
        CheckChainBean checkChainBean = new CheckChainBean(checkRsp, instruction, detailList, pledgeList);
        commonCheckChain.execute(checkChainBean);

        return checkRsp;
    }


}

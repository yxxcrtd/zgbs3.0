package com.joyintech.tams.exchange.service.trade.check.chain;

import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.InstructionDetail;
import com.joyintech.tams.exchange.common.db.domain.InstructionPledge;
import com.joyintech.tams.exchange.service.trade.check.ChainBean;

import java.util.List;
import java.util.Map;

/**
 * @author lifangpeng
 * @date 2022/4/1 14:40
 */
public class CheckChainBean extends ChainBean {
    private static final long serialVersionUID = -3450358632920513923L;

    private Map<String, Object> checkRsp;
    private Instruction instruction;
    private List<InstructionDetail> detailList;
    private List<InstructionPledge> pledgeList;

    public CheckChainBean(Map<String, Object> checkRsp, Instruction instruction, List<InstructionDetail> detailList,
                          List<InstructionPledge> pledgeList) {
        this.checkRsp = checkRsp;
        this.instruction = instruction;
        this.detailList = detailList;
        this.pledgeList = pledgeList;
    }

    public Map<String, Object> getCheckRsp() {
        return checkRsp;
    }

    public void setCheckRsp(Map<String, Object> checkRsp) {
        this.checkRsp = checkRsp;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public List<InstructionDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<InstructionDetail> detailList) {
        this.detailList = detailList;
    }

    public List<InstructionPledge> getPledgeList() {
        return pledgeList;
    }

    public void setPledgeList(List<InstructionPledge> pledgeList) {
        this.pledgeList = pledgeList;
    }
}

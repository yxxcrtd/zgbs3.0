package com.joyintech.tams.exchange.datacommon.db.service.impl;

import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.db.domain.InstructionDetail;
import com.joyintech.tams.exchange.common.db.domain.InstructionDetailExample;
import com.joyintech.tams.exchange.datacommon.cache.impl.AsyncServiceImpl;
import com.joyintech.tams.exchange.datacommon.db.dao.IMybatisMapper;
import com.joyintech.tams.exchange.datacommon.db.dao.InstructionDetailMapper;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InstructionDetailServiceImpl extends AsyncServiceImpl<String, InstructionDetail> implements InstructionDetailService {

    private final Map<String, Map<String, InstructionDetail>> instructionIdMap =  Maps.newConcurrentMap();
    @Autowired
    private InstructionDetailMapper instructionDetailMapper;

    @Override
    public List<InstructionDetail> queryInstructionDetailsByInstructionId(String instructionId) {
        return new ArrayList<>(instructionIdMap.getOrDefault(instructionId, new HashMap<>()).values());
    }

    @Override
    public void deleteInstructionDetailsByInstructionsIds(Set<String> instructionIds) {
        instructionIds.forEach(s -> {
            List<InstructionDetail> instructionDetails = queryInstructionDetailsByInstructionId(s);
            if (Objects.nonNull(instructionDetails)) {
                List<InstructionDetail> instructionDetails1 = new ArrayList<>(instructionDetails);
                if (instructionDetails1.size() > 0) asyncDel(instructionDetails1);
            }
            instructionIdMap.remove(s);
        });
    }

    @Override
    public IMybatisMapper<InstructionDetail, String, InstructionDetailExample> getMapper() {
        return instructionDetailMapper;
    }

    @Override
    public void clearCache() {
        memory_cache.clear();
        instructionIdMap.clear();
    }

    @Override
    protected void update(InstructionDetail detail) {
        String instructionId = detail.getEntrustId();
        Map<String, InstructionDetail> detailMap = instructionIdMap.getOrDefault(instructionId, Maps.newHashMap());
        detailMap.put(detail.getId(), detail);
        instructionIdMap.putIfAbsent(instructionId, detailMap);
    }

    @Override
    protected void save(InstructionDetail instructionDetail) {
        update(instructionDetail);
    }

    @Override
    protected void delete(String PK) {
        InstructionDetail instructionDetail = memory_cache.get(PK);
        if (Objects.nonNull(instructionDetail)) {
            List<InstructionDetail> instructionDetails = queryInstructionDetailsByInstructionId(instructionDetail.getEntrustId());
            if (Objects.nonNull(instructionDetails)) {
                instructionDetails.remove(instructionDetail);
                if (instructionDetails.size() == 0)
                    instructionIdMap.remove(instructionDetail.getEntrustId());
            }
        }
    }


}

package com.joyintech.tams.invest.batch.tradorSeq.service;

import com.joyintech.tams.common.base.dao.TradorSeqMapper;
import com.joyintech.tams.common.base.entity.TradorSeq;
import com.joyintech.tams.invest.batch.tradorSeq.dao.TradorSeqForwardMapper;
import com.joyintech.tams.invest.batch.tradorSeq.dto.TradorSeqForwardDto;
import com.joyintech.tams.invest.batch.tradorSeq.dto.TradorSeqForwardResultDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class TradorSeqForwardService {
    @Autowired
    private TradorSeqForwardMapper tradorSeqMapper;
    @Autowired
    private TradorSeqMapper tradorSeqMapperTams;
    public List<TradorSeqForwardDto> selectTradorSeqByRole(){
        List<TradorSeqForwardDto> tradorSeqForwardDtos = tradorSeqMapper.selectTradorSeqByRole();
        return tradorSeqForwardDtos;
    }

    public TradorSeqForwardResultDto convertResultFromRead(TradorSeqForwardDto item, Map<String, Object> params){
        Random r = new Random();
        if(item!=null){
            TradorSeqForwardResultDto resultDto = new TradorSeqForwardResultDto();
            resultDto.setUserId(item.getUserId());
            resultDto.setSeq(new BigDecimal(Math.abs(r.nextInt())));
            return resultDto;
        }
        return null;
    }

    public void holdResultDto(List<? extends TradorSeqForwardResultDto> items) {
        if(CollectionUtils.isNotEmpty(items)){
            items.forEach(temp->{
                if(temp!=null){
                    TradorSeq seq = new TradorSeq();
                    seq.setUserId(temp.getUserId());
                    seq.setSeq(temp.getSeq());
                    tradorSeqMapperTams.insertSelective(seq);
                }
            });
        }

    }
}

package com.joyintech.tams.valuation.batch.common.log.dao;

import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.MstCounterAcct;
import com.joyintech.tams.valuation.batch.common.log.dto.DetailBatchLogDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DetailBatchLogMapper {

    int insertBatchDetailLog(DetailBatchLogDto dto);


}

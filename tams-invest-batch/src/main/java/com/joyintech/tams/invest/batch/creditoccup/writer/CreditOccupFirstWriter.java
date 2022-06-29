package com.joyintech.tams.invest.batch.creditoccup.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.batch.creditoccup.dao.CreditOccupMapper;
import com.joyintech.tams.invest.batch.creditoccup.dto.CreditOccupResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zznn
 * @date 2021/8/10
 */
@Service
@Scope("prototype")
public class CreditOccupFirstWriter extends BaseWriter<NeedToWriteEntity> {

    @Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
        //
    }
}


package com.joyintech.tams.invest.batch.creditoccup.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.tams.invest.batch.creditoccup.dto.CreditOccupReaderResult;
import com.joyintech.tams.invest.batch.creditoccup.service.CreditOccupReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gwl
 * @date 2022/6/30
 */
@Service
@Scope("prototype")
public class CreditOccupFirstReader extends BaseReader<CreditOccupReaderResult> {

    @Autowired
    private CreditOccupReaderService creditOccupReaderService;

    @Override
    public List<CreditOccupReaderResult> createList() {
        return creditOccupReaderService.execute3(this.getParams());
    }
}


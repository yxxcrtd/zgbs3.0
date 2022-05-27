package com.joyintech.tams.settle.batch.datagather.processor.credit;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datagather.processor.StlDataGatherProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

/**
 * @description 信用保护凭证交易
 * @author huwei1
 * @createDate 2022/5/24
 */
@Service
@Lazy
public class StlDataGatherCredit1Processor extends StlDataGatherProcessor {

    @Override
    protected List<String> getFinprodType() {
        return Arrays.asList(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
    }

    @Override
    protected String getSecType() {
        return DictCodeListStl.证券品种_信用保护凭证.getValue();
    }
}

package com.joyintech.tams.settle.batch.datagather.processor.bond;

import com.joyintech.tams.constant.*;
import com.joyintech.tams.settle.batch.datagather.processor.StlDataGatherProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @description 债券一级投标 （投资交易汇总批处理后，需要匹配指令）
 * @author huwei1
 * @createDate 2022/5/16
 */
@Service
@Lazy
public class StlDataGatherBond9Processor extends StlDataGatherProcessor {

    @Override
    protected List<String> getFinprodType() {
        return Arrays.asList(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
    }

    @Override
    protected String getSecType() {
        return DictCodeListExt1.证券品种_债券.getValue();
    }
}

package com.joyintech.tams.settle.batch.datagather.reader.bond;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datagather.reader.StlDataGatherReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * @description 债券一级投标(交收通知)
 * @author huwei1
 * @createDate 2022/5/16
 */
@Service
@Lazy
public class StlDataGatherBond9Reader extends StlDataGatherReader {
    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        searchParam.put("finprodType2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        searchParam.put("busiType", DictCodeList.业务类型_银行间债券交易.getValue());
        searchParam.put("psList", Arrays.asList(DictCodeList.交易方向_买入.getValue()));

        searchParam.put("deliveryResult05", DictCodeListExt4.交收结果_交收通知.getValue());
        searchParam.put("settleMode02", DictCodeListStl.交收方式_非担保.getValue());
        searchParam.put("settleMode01", DictCodeListStl.交收方式_担保.getValue());
        searchParam.put("accountSpeed0", 0);
    }

    @Override
    protected boolean isSecType(String secType) {
        return false;
    }

    @Override
    protected boolean isCreateTrade() {
        return false;
    }
}

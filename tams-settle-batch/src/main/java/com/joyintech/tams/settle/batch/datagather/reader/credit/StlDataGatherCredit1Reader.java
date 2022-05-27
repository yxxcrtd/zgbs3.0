package com.joyintech.tams.settle.batch.datagather.reader.credit;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.settle.batch.datagather.reader.StlDataGatherReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * @description 信用保护凭证交易
 * @author huwei1
 * @createDate 2022/5/24
 */
@Service
@Lazy
public class StlDataGatherCredit1Reader extends StlDataGatherReader {
    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        searchParam.put("busiType", DictCodeListExt3.头寸业务类型_信用保护凭证交易.getValue());
        searchParam.put("psList", Arrays.asList(DictCodeList.交易方向_买入.getValue(), DictCodeList.交易方向_卖出.getValue()));
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

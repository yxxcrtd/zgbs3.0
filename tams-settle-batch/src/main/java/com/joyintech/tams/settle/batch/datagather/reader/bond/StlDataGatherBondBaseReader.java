package com.joyintech.tams.settle.batch.datagather.reader.bond;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.settle.batch.datagather.reader.StlDataGatherReader;

import java.util.List;
import java.util.Map;

/**
 * 债券相关交易取值逻辑
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherBondBaseReader
 * @since 1.0
 */
public abstract class StlDataGatherBondBaseReader extends StlDataGatherReader {
    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        searchParam.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 业务类型
        searchParam.put("busiType", DictCodeList.业务类型_银行间债券交易.getValue());
        // 交易方向列表
        searchParam.put("psList", getPSList());
    }

    @Override
    protected boolean isSecType(String secType) {
        return StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), secType);
    }

    protected abstract List<String> getPSList();
}

package com.joyintech.tams.settle.batch.datagather.reader.bond;

import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.settle.batch.datagather.constant.StlDataGatherConstant;
import com.joyintech.tams.settle.batch.datagather.reader.StlDataGatherReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 一级投标债
 * @author fuyongsheng
 * @date 2022年05月12日
 * @see StlDataGatherBond8Reader
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherBond8Reader extends StlDataGatherReader {

    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        searchParam.put("readerType", StlDataGatherConstant.BOND_FIRST_BIDDING);
    }

    @Override
    protected boolean isSecType(String secType) {
        return false;
    }

    @Override
    protected boolean isCreateTrade(){
        return true;
    }
}

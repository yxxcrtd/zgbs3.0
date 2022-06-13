package com.joyintech.tams.settle.batch.datagather.reader.bond;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.settle.batch.datagather.constant.StlDataGatherConstant;
import com.joyintech.tams.settle.batch.datagather.reader.StlDataGatherReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 债券存续期
 * @author zznn
 * @date 2021/11/1
 */
@Service
@Lazy
public class StlDataGatherBondDurationReader extends StlDataGatherReader {

    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        // ZGZGST-4212:使用自定义字段类型标识债券存续期 fuyongsheng 20211214 modify start
        // 业务类型
        // searchParam.put("busiType", "R3S5");
        // reader 类型
        searchParam.put("readerType", StlDataGatherConstant.BOND_DURATION);
        // ZGZGST-4212:使用自定义字段类型标识债券存续期 fuyongsheng 20211214 modify end
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


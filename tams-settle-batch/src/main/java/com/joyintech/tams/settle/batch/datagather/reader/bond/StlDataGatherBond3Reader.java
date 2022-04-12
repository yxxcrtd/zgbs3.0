package com.joyintech.tams.settle.batch.datagather.reader.bond;

import com.joyintech.tams.constant.DictCodeList;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 债券申购确认交易取值逻辑
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherBond3Reader
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherBond3Reader extends StlDataGatherBondBaseReader {
    @Override
    protected List<String> getPSList() {
        return Arrays.asList(DictCodeList.交易方向_申购确认.getValue());
    }

    @Override
    protected boolean isCreateTrade(){
        //GZYHTZJY-1043:modify by fuyongsheng 20220107 start
//        return false;
        return true;
        //GZYHTZJY-1043:modify by fuyongsheng 20220107 end
    }
}

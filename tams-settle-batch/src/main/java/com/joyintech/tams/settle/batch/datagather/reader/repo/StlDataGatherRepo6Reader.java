package com.joyintech.tams.settle.batch.datagather.reader.repo;

import com.joyintech.tams.constant.DictCodeList;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 获取债券质押式协议回购-逆回购-到期交易数据
 * @author 何阳阳
 * @date 2020/7/30
 * @see StlDataGatherRepo6Reader
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherRepo6Reader extends StlDataGatherRepoDurationReader {

    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        // reader 类型
        searchParam.put("readerType", "RepoDuration");
        // reader 回购类型
        searchParam.put("readerRepoType", DictCodeList.回购类型_质押式协议回购.getValue());
    }

}

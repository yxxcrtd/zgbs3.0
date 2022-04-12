package com.joyintech.tams.settle.batch.datagather.reader.repo;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeReaderDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.reader.StlDataGatherReader;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 回购存续期
 * @author zznn
 * @date 2021/11/1
 */
@Service
@Lazy
public class StlDataGatherRepoDurationReader extends StlDataGatherReader {

    @Override
    protected void setSearchParam(Map<String, Object> searchParam) {
        // ZGZGST-4212:使用自定义字段类型标识回购存续期 fuyongsheng 20211214 modify start
        // 业务类型
        // searchParam.put("busiType", "R3S14");
        // reader 类型
        searchParam.put("readerType", "RepoDuration");
        // ZGZGST-4212:使用自定义字段类型标识回购存续期 fuyongsheng 20211214 modify end
        // reader 回购类型
        searchParam.put("readerRepoType", DictCodeList.回购类型_质押式竞价回购.getValue());
    }
    @Override
    protected boolean isSecType(String secType) {
        return false;
    }

    @Override
    protected boolean isCreateTrade(){
        return true;
    }

   /* @Override
    public List<InvestTradeReaderDto> createList() {
        InvestTradeReaderDto investTradeReaderDto = new InvestTradeReaderDto();
        SysBatchExecInfo execInfo = getBatchExecuteInfo();
        Map<String, Object> searchParam = new HashMap<>(4);
        searchParam.put("dealDate", execInfo.getExecuteDate());
        // 业务类型
        searchParam.put("finprodTypes", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()));
        // 交易方向列表
        searchParam.put("psList",  Arrays.asList(DictCodeList.交易方向_到期.getValue()));
        List<SettleTransDataDto> settleTransDataDtos = stlDataGatherService.listBondSettleTransData(searchParam);
        investTradeReaderDto.setSettleTransDataList(settleTransDataDtos);
        return Arrays.asList(investTradeReaderDto);
    }*/
}


package com.joyintech.service.basicdata.systrdconfig.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.basicdata.systrdconfig.dao.InterfSysTrdConfigMapper;
import com.joyintech.service.basicdata.systrdconfig.dto.StockInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockInfoService extends BaseService<StockInfoDto> {

    @Autowired
    private InterfSysTrdConfigMapper interfSysTrdConfigMapper;

    @Autowired
    private IPlatformDate iPlatformDateImpl;

    public Map<String,Object> selectStockInfo(Map<String, Object> params) {
        params.put("TRADE_MARKET_03",DictCodeListExt1.交易场所_上海证券交易所.getValue());
        params.put("TRADE_MARKET_04",DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        params.put("ASSET_TYPE_F01", DictCodeList.平安资产类型_债券.getValue());
        params.put("ASSET_TYPE_F02", DictCodeList.平安资产类型_基金.getValue());
        params.put("ASSET_TYPE_F11", DictCodeListExt2.平安资产类型_股票.getValue());
        params.put("ASSET_TYPE_F20", DictCodeListExt3.平安资产类型_期货.getValue());
        params.put("ASSET_TYPE_F37", DictCodeList.平安资产类型_信用保护凭证.getValue());
        params.put("FINPROD_TYPE_F02",DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
        params.put("FINPROD_TYPE_F03",DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
        params.put("platDate",iPlatformDateImpl.getSysDate());
        Map<String,Object> resultMap = new HashMap<>();
        if (params.get("page_no") != null && params.get("count") != null){
            int page,pageSize;
            try {
                page = Integer.parseInt(params.get("page_no").toString());
                pageSize = Integer.parseInt(params.get("count").toString());
            }catch (Exception e){
                e.printStackTrace();
                resultMap.put("errorCode",9);
                resultMap.put("errorMsg","参数错误：" + e.getMessage().substring(0,200));
                resultMap.put("data",new ArrayList<>());
                return resultMap;
            }
            Page<StockInfoDto> pages = this.queryForPage(page,pageSize,() -> interfSysTrdConfigMapper.selectStockInfo(params));
            resultMap.put("errorCode",0);
            resultMap.put("errorMsg","成功");
            resultMap.put("data",pages.getRoot());
            return resultMap;
        }else {
            List<StockInfoDto> resultList = interfSysTrdConfigMapper.selectStockInfo(params);
            resultMap.put("errorCode",0);
            resultMap.put("errorMsg","成功");
            resultMap.put("data",resultList);
            return resultMap;
        }

    }
}

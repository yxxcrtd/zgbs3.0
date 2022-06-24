package com.joyintech.tams.invest.batch.assetintdatecarryover.service;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.assetintdatecarryover.dto.AssetIntdateCarryForwardReaderResult;
import com.joyintech.tams.invest.batch.assetintdatecarryover.dao.AssetIntdateCarryForwardMapper;
import com.joyintech.tams.invest.batch.assetintdatecarryover.dto.AssetIntdateCarryForwardDto;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
/**
 * 资产理论日结转ReaderServic
 * @author WUJIE
 *
 */
@Service
public class AssetIntdateCarryForwardReaderService {
	
	@Autowired
	private AssetIntdateCarryForwardMapper mapper;
	/**
	 * 查询待处理现金流计划
	 * @param params
	 * @return
	 */
    public List<AssetIntdateCarryForwardReaderResult> execute(Map<String, Object> params) {
    	List<AssetIntdateCarryForwardReaderResult> resultList = new ArrayList<AssetIntdateCarryForwardReaderResult>();
    	params.put("BRANCH",0);
    	params.put("CASH_TYPE_2_BJ",DictCodeListExt1.现金流二级类型_本金.getValue());
        params.put("CASH_TYPE_2_LX",DictCodeListExt1.现金流二级类型_利息.getValue());
        
        params.put("PINAN_ASSET_TYPE_ZQ",DictCodeList.平安资产类型_债券.getValue());
        params.put("PINAN_ASSET_TYPE_CK",DictCodeList.平安资产类型_存款.getValue());
        params.put("PINAN_ASSET_TYPE_JHLLLX",DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        params.put("PINAN_ASSET_TYPE_XML",DictCodeList.平安资产类型_项目类资产.getValue());

    	List<AssetIntdateCarryForwardDto> list = mapper.queryAssetIntdateCarryForwardDtoList(params);
    	if(!CollectionUtils.isNotEmpty(list)){
    		resultList.add(new AssetIntdateCarryForwardReaderResult());
    		return resultList;
    	}
    	Map<String,AssetIntdateCarryForwardReaderResult> resultMap = new HashMap<String,AssetIntdateCarryForwardReaderResult>();
    	//根据金融产品代码进行分组
    	for(AssetIntdateCarryForwardDto dto:list){
    		AssetIntdateCarryForwardReaderResult result = resultMap.get(dto.getFinprodId()+dto.getFinprodType());
    		if(result==null){
    			result = new AssetIntdateCarryForwardReaderResult();
    			result.setFinprodId(dto.getFinprodId());
    			result.setFinprodType(dto.getFinprodType());
    			result.getOverdueFinTradeProductForwardDtoList().add(dto);
    			resultMap.put(dto.getFinprodId()+dto.getFinprodType(), result);
    		}else{
    			result.getOverdueFinTradeProductForwardDtoList().add(dto);
    			resultMap.put(dto.getFinprodId()+dto.getFinprodType(), result);
    		}
    	}
    	for(String key :resultMap.keySet()){
    		resultList.add(resultMap.get(key));
    	}
    	return resultList;
    }
}

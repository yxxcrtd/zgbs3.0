/*
 * 文件名：ExchangeFeeCalc.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年7月8日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */
package com.joyintech.tams.settle.common.service;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.settle.common.dao.SettleCommonMapper;
import com.joyintech.tams.settle.common.dto.FundInfoDto;
import com.joyintech.tams.settle.common.dto.StlDownDataDealAccountInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 清结算共通service
 *
 * @author 王波
 * @date 2020/7/8
 * @see SettleCommonService
 * @since 1.0
 */
@Service
public class SettleCommonService {

	/**
	 * 是否单体服务
	 */
	@Value("${application.micorservice.contextpath.single:false}")
	private boolean isSingle;

	@Autowired(required = false)
	private JoyinRestTemplate restTemplate;

	@Autowired(required = false)
	@Qualifier("simpleRestTemplate")
	protected JoyinRestTemplate simpleRestTemplate;

    @Autowired
    private SettleCommonMapper settleCommonMapper;

    @Value("${application.micorservice.contextpath.commonService:http://commonService}")
	private String commonServiceName;

	@Autowired(required = false)
	private FinProductMapper finProductMapper;

    // 数据清算附属信息（核算信息）全量获取
	public Map<String, List<StlDownDataDealAccountInfoDto>> getAllStlDownDataDealAccountInfoDtoList(boolean isNeedSeatNumber) {
		List<StlDownDataDealAccountInfoDto> list = settleCommonMapper.selectAllStlDownDataDealAccountInfoDtoList();
		Map<String, List<StlDownDataDealAccountInfoDto>> map = new HashMap<String, List<StlDownDataDealAccountInfoDto>>();
		if (isNeedSeatNumber) {
			for (StlDownDataDealAccountInfoDto stlDownDataDealAccountInfoDto : list) {
				String key = stlDownDataDealAccountInfoDto.getShareholdersCode() + stlDownDataDealAccountInfoDto.getSeatNumber();
				if (map.containsKey(key)) {
					map.get(key).add(stlDownDataDealAccountInfoDto);
				}
				else {
					List<StlDownDataDealAccountInfoDto> valueList = new ArrayList<StlDownDataDealAccountInfoDto>();
					valueList.add(stlDownDataDealAccountInfoDto);
					map.put(key, valueList);
				}
			}
		}
		else {
			for (StlDownDataDealAccountInfoDto stlDownDataDealAccountInfoDto : list) {
				String key = stlDownDataDealAccountInfoDto.getShareholdersCode();
				if (map.containsKey(key)) {
					map.get(key).add(stlDownDataDealAccountInfoDto);
				}
				else {
					List<StlDownDataDealAccountInfoDto> valueList = new ArrayList<StlDownDataDealAccountInfoDto>();
					valueList.add(stlDownDataDealAccountInfoDto);
					map.put(key, valueList);
				}
			}
		}
		return map;
	}
	
	// 基金全量信息获取（分类1和分类7）
    public Map<String, FundInfoDto> getAllFundInfoDto() {
    	Map<String, FundInfoDto> map = new HashMap<String, FundInfoDto>();
    	List<FundInfoDto> list = settleCommonMapper.getAllFundInfoDto();
    	if (list != null && list.size() > 0) {
    		for (FundInfoDto fundInfoDto : list) {
    			map.put(fundInfoDto.getFinprodId(), fundInfoDto);
    		}
    	}
    	return map;
    }

	/**
	 * 根据金融产品类型_投管用获取所有的标的代码，用来判断标的类型
	 * @param finprodType 金融产品类型_投管用
	 * @return
	 */
    public List<String> getAllAssetInfo(String finprodType) {
		return settleCommonMapper.getAllAssetInfo(finprodType);
	}

	/**
	 * 生成业务编号
	 * @param commonBusinessType 共通业务类型
	 * @return 业务编号
	 * @throws Exception
	 *//*
	public String buildBussinessCode(String commonBusinessType) {
		Map<String, String> param = new HashMap<>(1);
		param.put("commonBusinessType", commonBusinessType);
		// 生成的业务编号(此处对性能影响比较大)
		return restTemplate.postForObject(
				commonServiceName + "/core/selectRule", param,
				String.class);
	}*/

	/**
	 * 生成业务编号
	 * @param dictCodeList 共通业务类型
	 * @return 业务编号
	 * @throws Exception
	 */
	public String buildBussinessCodeExt(BaseDictCodeList dictCodeList) {
		Map<String,Object> retMap = new HashMap<String,Object>();
        retMap.put("codeName",dictCodeList.getCodeName());
        retMap.put("value",dictCodeList.getValue());
        retMap.put("dictCode",dictCodeList.getDictCode());
		Map<String, Object> param = new HashMap<>(1);
		param.put("commonBusinessExtType", retMap);
		// 生成的业务编号(此处对性能影响比较大)
		if(isSingle) {
			return simpleRestTemplate.postForObject(
					commonServiceName + "/core/selectRule", param,
					String.class);
		}
		else {
			return restTemplate.postForObject(
					commonServiceName + "/core/selectRule", param,
					String.class);
		}
	}

    /**
     * 生成业务编号
     * @param seqno 共通业务类型
     * @return 业务编号
     * @throws Exception
     */
    public String buildBussinessIdBySeqno(String seqno) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("seqNo", seqno);
        // 生成的业务编号(此处对性能影响比较大)
		if (isSingle) {
			return simpleRestTemplate.postForObject(
					commonServiceName + "/core/selectRule", param,
					String.class);
		}
		else {
			return restTemplate.postForObject(
					commonServiceName + "/core/selectRule", param,
					String.class);
		}

    }

	/**
	 * 根据金融产品代码查询金融产品信息
	 * @param finprodId 金融产品代码
	 * @return 金融产品信息
	 */
	public FinProduct selectFinProduct(String finprodId) {
		return finProductMapper.selectByPrimaryKey(finprodId);
	}
}

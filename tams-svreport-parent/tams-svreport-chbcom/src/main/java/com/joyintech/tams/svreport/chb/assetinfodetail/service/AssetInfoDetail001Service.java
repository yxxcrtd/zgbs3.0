/*
 * 文件名：InvestorsHoldInfoRegist001Service.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：zuoyinyi
 * 修改时间：2018年5月8日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.tams.svreport.chb.assetinfodetail.service;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.ChbAssetInfoErrodataMapper;
import com.joyintech.tams.common.base.dao.ChbAssetSubStatusMapper;
import com.joyintech.tams.common.base.entity.ChbAssetInfo;
import com.joyintech.tams.common.base.entity.ChbAssetSubStatus;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.report.common.constant.SvrServiceApplication;
import com.joyintech.tams.report.common.dto.ExcelValidDto;
import com.joyintech.tams.svreport.chb.assetinfodetail.dao.AssetInfoDetailMapper;
import com.joyintech.tams.svreport.chb.assetinfodetail.dto.AssetInfoDetailDto;
import com.joyintech.tams.svreport.chb.assetinforeg.dao.AssetInfoRegist001Mapper;
import com.joyintech.tams.svreport.chb.assetinforeg.dao.ChbAssetInfoSubHisMapper;
import com.joyintech.tams.svreport.chb.assetinforeg.dto.AssetInfoRegist001Dto;
import com.joyintech.tams.svreport.chb.assetinforeg.dto.ChbAssetInfoSubHisDto;
import com.joyintech.tams.svreport.chb.investtrade.dao.MstCounterPartyDtoMapper;
import com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbAssetInfoInterfMapper;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 资产基本信息维护Service
 */
@Service(value="svrAssetInfoDetailService_dtx")
@Transactional(rollbackFor = Exception.class)
public class AssetInfoDetail001Service extends CommonExportService<AssetInfoDetailDto> {

	/**
	 * 资产要素登记mapper
	 */
	@Autowired
	private AssetInfoDetailMapper assetInfoDetailMapper;

	@Autowired
	private ChbAssetInfoErrodataMapper chbAssetInfoErrodataMapper;



    private Logger logger = LoggerFactory.getLogger(getClass());


	/**
	 * 查询页面数据
	 *
	 * @param pageParam
	 *            查询条件
	 * @return 结果列表
	 * @see
	 * @since 1.6.0
	 */
	public Page<AssetInfoDetailDto> searchForPage(Map<String, Object> pageParam) {
        Page<AssetInfoDetailDto> page;
        //资产资产编号
		String assetCode = (String)pageParam.get("assetCode");
		if(StringUtil.isNotEmpty(assetCode)){
			pageParam.put("assetCodes", StringUtil.splitToList(GlobalConstant.COMMA,assetCode));
		}

		//资产/负债类别
		String assetType = (String)pageParam.get("assetType");
		if(StringUtil.isNotEmpty(assetType)){
			pageParam.put("assetTypes", StringUtil.splitToList(GlobalConstant.COMMA,assetType));
		}

		//资产/负债类别二级
		String assetType2 = (String)pageParam.get("assetType2");
		if(StringUtil.isNotEmpty(assetType2)){
			pageParam.put("assetType2s", StringUtil.splitToList(GlobalConstant.COMMA,assetType2));
		}

		//资产一级分类
		String orgAssetType1 = (String)pageParam.get("orgAssetType1");
		if(StringUtil.isNotEmpty(orgAssetType1)){
			pageParam.put("orgAssetType1s", StringUtil.splitToList(GlobalConstant.COMMA,orgAssetType1));
		}

		//资产二级分类
		String orgAssetType2 = (String)pageParam.get("orgAssetType2");
		if(StringUtil.isNotEmpty(orgAssetType2)){
			pageParam.put("orgAssetType2s", StringUtil.splitToList(GlobalConstant.COMMA,orgAssetType2));
		}


		page = queryForPage(AssetInfoDetailMapper.class,"selectChbInfoByParam",pageParam);
        return page;
	}

	@Transactional
	@DTXTransaction
	public Integer doComplete(AssetInfoDetailDto dto){
		int  flag = 0;
		if (StringUtil.equals(dto.getOperateType(), "DELETE")) {//删除操作
			flag = this.doDelete(dto);
		}else {
			List<ChbAssetInfo> assetInfo = new ArrayList<>();
			//数据保存.使用marge模式.避免出现老数据仍然出现的问题
			if (StringUtil.isNotEmpty(dto)) {
				assetInfo.add(dto);
				flag = assetInfoDetailMapper.mergeInfoListEx(assetInfo);
			}
			/**
			 * 如果是待处理数据,则将错误信息表里面的数据删除
			 */
			if (DictCodeListExt3.中债_处理状态_待处理.getValue().equals(dto.getDataStatus())) {
				assetInfoDetailMapper.deleteErrorMsg(dto.getAssetCode());

			}
		}

		return flag;
	}


	/**
	 * 数据删除
	 */
	public Integer doDelete(AssetInfoDetailDto dto){
		int  flag = 0;

		/***如果是资产基本信息,则修改状态为D*/
		if(DictCodeListExt3.中债_处理状态_已处理.getValue().equals(dto.getDataStatus())){
			assetInfoDetailMapper.updateChbAssetType(dto.getAssetUuid());
		}
		/**
		 * 如果是待处理数据,则将错误信息表里面的数据删除
		 */
		if(DictCodeListExt3.中债_处理状态_待处理.getValue().equals(dto.getDataStatus())){
			assetInfoDetailMapper.deleteError(dto.getAssetUuid());
		}

		return flag;
	}

	/**
	 * 根据uuid获取资产信息
	 * @param UUid
	 * @return
	 */
	public AssetInfoDetailDto getAssetInfoByUUid(String UUid){
		return assetInfoDetailMapper.getAssetInfoDetail(UUid);
	}
	
	public List<ExcelValidDto> importData(List<AssetInfoDetailDto> dataList){
		List<ExcelValidDto> checkResult = new ArrayList<>();
		if(!StringUtil.isEmptyList(dataList)){
	/*		List<AssetInfoDetailDto> existAsset = assetInfoDetailMapper.selectKeyListData(dataList);
			List<ChbAssetInfo> insertList = new ArrayList<>();
			List<ChbAssetInfo> updateList = new ArrayList<>();
			if (StringUtil.isEmptyList(existAsset)){
				insertList.addAll(dataList);
			}else {
				List<String> assetCodes = new ArrayList<>();
				Map<String,String> keyMap = new HashMap<>();
				existAsset.forEach(assetInfoDetailDto -> {
                    assetCodes.add(assetInfoDetailDto.getAssetCode());
                    keyMap.put(assetInfoDetailDto.getAssetCode(),assetInfoDetailDto.getAssetUuid());
				});
				dataList.forEach(assetInfoDetailDto -> {
					if (assetCodes.contains(assetInfoDetailDto.getAssetCode())){
						assetInfoDetailDto.setAssetUuid(keyMap.get(assetInfoDetailDto.getAssetCode()));
						updateList.add(assetInfoDetailDto);
					}else {
						insertList.add(assetInfoDetailDto);
					}
				});
			}
			if(!StringUtil.isEmptyList(insertList)){
				assetInfoDetailMapper.insertBatch(insertList);
			}
			if(!StringUtil.isEmptyList(updateList)){
				assetInfoDetailMapper.updateBatchNotCheckNull(updateList);
			}*/
			List<ChbAssetInfo> assetInfo = new ArrayList<>();
			dataList.forEach(assetInfoDetailDto -> {
				if(DictCodeListExt2.资产_负债类别_资产管理产品.getValue().equals(assetInfoDetailDto.getAssetType())
						||DictCodeListExt2.资产_负债类别_委外投资_协议方式.getValue().equals(assetInfoDetailDto.getAssetType())){
					assetInfoDetailDto.setPlanSdate(assetInfoDetailDto.getVdate());
					assetInfoDetailDto.setPlanEdate(assetInfoDetailDto.getMdate());
				}
				assetInfo.add(assetInfoDetailDto);				
			});
			assetInfoDetailMapper.mergeInfoListEx(assetInfo);
			return checkResult;
		}
		checkResult.add(new ExcelValidDto(StringUtil.EMPTY, String.valueOf(0),
				this.getMessage("REPORT_COMMON_FILE_E_0016")));
		return  checkResult;
	}


}

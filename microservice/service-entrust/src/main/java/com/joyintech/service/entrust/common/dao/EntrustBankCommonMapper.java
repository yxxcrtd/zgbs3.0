/*
 * 文件名：EntrustCommonPersonSelectMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年11月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.common.dao;

import com.joyintech.service.entrust.common.dto.EtrBondFirstBidInfoDto;
import com.joyintech.tams.common.base.entity.EtrQuoteFinishStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年11月9日
 * @see EntrustBankCommonMapper
 * @since 1.0
 */
@Repository
public interface EntrustBankCommonMapper {
	
    
	List<EtrQuoteFinishStatus> selectEtrQuoteFinishStatusList(Map<String, Object> param);
	
	int checkDataStatus(Map<String, Object> param);

	int checkRegDataStatus(Map<String, Object> param);

	int checkFirstBidDataStatus(Map<String, Object> param);

	/**
	 * 查询指令信息
	 * @param param
	 * @return
	 */
    EtrBondFirstBidInfoDto selectBidInfo(Map<String, Object> param);
}

package com.joyintech.tams.invest.batch.prebatchcheck.tenderbond.dao;

import com.joyintech.tams.invest.common.prebatch.dto.SysPrebatchResultDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 *
 * 新债上市流通批前检查
 *
 * @author gwl
 * 创建日期：2022-04-19
 */
@Repository
public interface FinTenderBondPreMapper {
	
	/**
	 * 发行阶段为已发行、发行状态不为发行失败且上市日为当日但未进行上市流通的新债标的，批前检查提示并卡批
	 * 发行阶段为已发行、发行状态不为发行失败且上市日为空但未进行上市流通的新债标的，批前检查提示不卡批
	 * @param map
	 * @return
	 */
	List<SysPrebatchResultDetail> selectPreList(Map<String,Object> map);

}

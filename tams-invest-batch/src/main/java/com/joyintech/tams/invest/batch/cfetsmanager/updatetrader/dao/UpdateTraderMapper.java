package com.joyintech.tams.invest.batch.cfetsmanager.updatetrader.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.common.base.entity.MstCfetsCustomerInfo;

@Repository
public interface UpdateTraderMapper {
	
	/**
	 * 查询在交易对手中不存在的银行间机构
	 * @return
	 */
	List<MstCfetsCustomerInfo> selectCustomerNew(@Param("batchDate") String batchDate);

	/**
	 * 查询在交易对手中的非法人机构
	 * @return
	 */
	int selectCustomer01(Map<String, Object> map);

	/**
	 * 查询在交易对手中的法人机构
	 * @return
	 */
	int selectCustomer02(Map<String, Object> map);

	/**
	 * 查询在交易对手中的非法人机构
	 * @return
	 */
	String checkCustomerName(Map<String, Object> map);


	/**
	 * 查询在主机构代码
	 * @return
	 */
	MstCfetsCustomerInfo selectInfo(@Param("batchDate") String batchDate);

	/**
	 * 交易成员全称匹配交易对手信息
	 * @param map
	 * @return
	 */
	String checkPartyNameOfParty(Map<String, Object> map);

	/**
	 * 查询主从机构的主机构的21位机构码
	 * @param map
	 * @return
	 */
	List<String> selectMpartyId(Map<String, Object> map);

	/**
	 * 获取机构信息
	 * @param mpartyId 6位机构码
	 * @return
	 */
	MstCfetsCustomerInfo selectMpartyInfo(@Param("mpartyId") String mpartyId);

	/**
	 * 获取所有的已匹配的银行间机构21位码
	 * @return
	 */
    List<String> getAllPartyIdIsNotNull();
}

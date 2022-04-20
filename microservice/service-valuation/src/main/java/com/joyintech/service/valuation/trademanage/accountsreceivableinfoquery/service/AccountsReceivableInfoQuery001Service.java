/*
 * 文件名：AccountsReceivableInfoQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangzhao
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.accountsreceivableinfoquery.service;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.accountsreceivableinfoquery.dao.AccountsReceivableInfoQuery001Mapper;
import com.joyintech.service.valuation.trademanage.accountsreceivableinfoquery.dto.AccountsReceivableInfoQuery001Dto;


/**
 * 应收款项信息查询
 * @author zhangzhao
 * @version 1.0 2018年10月9日
 * @see AccountsReceivableInfoQuery001Service
 * @since 1.0
 */
@Service(value="serviceAccountsReceivableInfoQuery001Service")
public class AccountsReceivableInfoQuery001Service extends CommonExportService<AccountsReceivableInfoQuery001Dto>{

    @Autowired
    private AccountsReceivableInfoQuery001Mapper accountsReceivableInfoQuery001Mapper;
    
    /**
     * 标的资产下拉框数据选择
     * @return pageParam
     * @exception Exception
     */
	public List<SelectOptionDto> getBdFinprodId(Map<String, Object> pageParam) {
		 /*List<AccountsReceivableInfoQuery001Dto> resultList = accountsReceivableInfoQuery001Mapper.getBdFinprodId(pageParam);
	        List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();
	        for (AccountsReceivableInfoQuery001Dto single : resultList) { // 组装select节点对象
	            SelectOptionDto selectOptionDto = new SelectOptionDto();
	            selectOptionDto.setId(single.getBdFinprodId());
	            selectOptionDto.setText(single.getBdFinprodName());
	            selectOptionDtoList.add(selectOptionDto);
	        }
	        return selectOptionDtoList;*/
		/**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 start **/
		String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
		pageParam.put("accountEntryType",accountEntryType);
		/**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 end **/
    	return accountsReceivableInfoQuery001Mapper.getBdFinprodId(pageParam);
	}
    
    /**
     * 根据条件分页查询
     * @param params
     * @return
     * @throws Exception
     */
    public Page<AccountsReceivableInfoQuery001Dto> findByPage(int page, int length, Map<String, Object> params){
    	
	    	String bdFinprodId = (String)params.get("bdFinprodId");
	    	String[] bdFinprodIdArr = null;
	        if(StringUtil.isNotEmpty(bdFinprodId)){
	        	bdFinprodIdArr = bdFinprodId.split(",");
	        }
	        params.put("bdFinprodIdArr", bdFinprodIdArr);
			/**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 start **/
			String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
			params.put("accountEntryType",accountEntryType);
		    /**add by yinya on 2022-03-24 新会计引擎升级，科目体系有变更，科目性质需要增加旧科目体系的兼容 end **/
    	 return super.queryForPage(page, length, ()-> accountsReceivableInfoQuery001Mapper.queryPageList(params));
    }
    /**
     *  导出功能
     * @param params
     * @return
     * @throws Exception
     */
    public List<AccountsReceivableInfoQuery001Dto> findPageout(Map<String, Object> params) {
	    	String bdFinprodId = (String)params.get("bdFinprodId");
	    	String[] bdFinprodIdArr = null;
	        if(StringUtil.isNotEmpty(bdFinprodId)){
	        	bdFinprodIdArr = bdFinprodId.split(",");
	        }
	        params.put("bdFinprodIdArr", bdFinprodIdArr);
    	return  accountsReceivableInfoQuery001Mapper.queryPageList(params);
	}
    
   

	
    
}

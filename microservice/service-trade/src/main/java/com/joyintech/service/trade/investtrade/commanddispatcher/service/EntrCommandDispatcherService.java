package com.joyintech.service.trade.investtrade.commanddispatcher.service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EtrEntrustDetailDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.service.trade.investtrade.commanddispatcher.dao.EntrCommandDispatcherMapper;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrCommandDispatcherDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrCommandDispatcherTotalDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrTradePledgeDto;

/**
 * 
 * 指令分发接口Service
 * 〈功能详细描述〉
 * @author tanxuan
 * @version 1.0 2020年5月11日
 * @see EntrCommandDispatcherService
 * @since 1.0
 */
@Service
public class EntrCommandDispatcherService extends BaseService<EntrCommandDispatcherDto>{
	
	@Autowired
	private EntrCommandDispatcherMapper entrTradeSysInterfMapper;
	
	/**
	 * 指令分发 <功能详细描述>
	 * 
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	public EntrCommandDispatcherTotalDto pushCommandMsg(Map<String, Object> param) {
		EntrCommandDispatcherTotalDto dto = new EntrCommandDispatcherTotalDto();
		List<EntrCommandDispatcherDto> selectList = this.selectList(param);
		if (param.get("isAutoReceive") != null) {
			String isAutoReceive = param.get("isAutoReceive").toString();
			if (StringUtils.isNotEmpty(isAutoReceive)) {
				for (EntrCommandDispatcherDto entrCommandDispatcherDto : selectList) {
					entrCommandDispatcherDto.setIsAutoReceive(isAutoReceive);
				}
			}
		}
		for (EntrCommandDispatcherDto entrCommandDispatcherDto : selectList) {
			entrCommandDispatcherDto.setEntrustNum(entrCommandDispatcherDto.getEntrustNum() == null
					? null : entrCommandDispatcherDto.getEntrustNum().setScale(0, RoundingMode.HALF_UP));
		}
		dto.setEtr_entrust_list(selectList);
		dto.setEtr_pledge_list(this.selectPledgeList(selectList));
		dto.setEtr_entrust_detail_list(selectEtrDetailList(param));
		if (selectList != null && !selectList.isEmpty()) {
			dto.setAMOUNT(selectList.size());
			dto.setPageCount(1);
		} else {
			dto.setAMOUNT(0);
			dto.setPageCount(0);
		}
		return dto;
	}
	
	/**
     * 指令接口查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<EntrCommandDispatcherDto> selectList(Map<String, Object> param){
    	return entrTradeSysInterfMapper.selectList(param);
    }
    
    /**
     * 指令回购质押品查询
     * <功能详细描述>
     * @param list
     * @return
     * @see
     * @since 1.0
     */
    public List<EntrTradePledgeDto> selectPledgeList(List<EntrCommandDispatcherDto> list){
    	List<EntrTradePledgeDto> pledgelist = new ArrayList<EntrTradePledgeDto>(); 
    	
    	if(list != null && !list.isEmpty()) {
	    	for(EntrCommandDispatcherDto dto : list) {
	    		if("F02".equals(dto.getFinprodType2()) || "F03".equals(dto.getFinprodType2()) ) {
		    		List<EntrTradePledgeDto> queryEtrPledgeList = entrTradeSysInterfMapper.queryEtrPledgeListByEtrId(dto.getEntrustId());
		    		if(queryEtrPledgeList!=null && !queryEtrPledgeList.isEmpty()) {
		    			pledgelist.addAll(queryEtrPledgeList);
		    		}
	    		}
	    	}
    	}
    	return pledgelist;
    }
	/**
	 * 委托指令明细列表查询
	 * <功能详细描述>
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	public List<EtrEntrustDetailDto> selectEtrDetailList(Map<String, Object> param){
		return entrTradeSysInterfMapper.selectEtrDetailList(param);
	}

}

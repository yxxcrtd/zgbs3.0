package com.joyintech.service.entrust.etrcommanddispatcher.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.service.entrust.etrcommanddispatcher.dao.EtrCommandDispatcherMapper;
import com.joyintech.service.entrust.etrcommanddispatcher.dto.EtrCommandDispatcherDto;
import com.joyintech.service.entrust.etrcommanddispatcher.dto.EtrCommandDispatcherTotalDto;
import com.joyintech.service.entrust.etrcommanddispatcher.dto.EtrEntrustDetailDto;
import com.joyintech.service.entrust.etrcommanddispatcher.dto.EtrTradePledgeDto;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * 指令分发接口Service
 * 〈功能详细描述〉
 * @author tanxuan
 * @version 1.0 2020年5月11日
 * @see EtrCommandDispatcherService
 * @since 1.0
 */
@Service
public class EtrCommandDispatcherService extends BaseService<EtrCommandDispatcherDto>{
	
	@Autowired
	private EtrCommandDispatcherMapper etrTradeSysInterfMapper;
	
    /**
     * 指令分发信息推送service
     */
    @Autowired
    private MessageSenderService messageSenderService;
    
    
    private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
     * 指令分发
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public void pushCommandMsg(Map<String, Object> param)  {
        	EtrCommandDispatcherTotalDto dto = new EtrCommandDispatcherTotalDto();
        	List<EtrCommandDispatcherDto> selectList = this.selectList(param);
        	if(selectList != null && selectList.size() == 1){
        		for(EtrCommandDispatcherDto etrCommandDispatcherDto : selectList){
        			etrCommandDispatcherDto.setIsAutoReceive(DictCodeListExt1.是否_是.getValue());
        		}
        	}
	    	if(selectList != null && selectList.size() > 0) {
				for (EtrCommandDispatcherDto etrCommandDispatcherDto : selectList) {
					if (!StringUtil.isNotEmpty(etrCommandDispatcherDto.getBusiTypeTg())
							&& StringUtil.equals(etrCommandDispatcherDto.getPS(), DictCodeListExt3.交易方向_债转股.getValue())) {
						etrCommandDispatcherDto.setBusiTypeTg(DictCodeListExt3.投管业务类型_债转股.getValue());
					}
				}
			}
    		dto.setEtr_entrust_list(selectList);
    		dto.setEtr_pledge_list(this.selectPledgeList(selectList));
		    dto.setEtr_entrust_detail_list(selectEtrDetailList(param));
    		if(selectList!=null && !selectList.isEmpty()) {
    			dto.setAMOUNT(selectList.size());
        		dto.setPageCount(1);
        		//放弃使用mq推送消息,采用接口调用
        		//messageSenderService.pushCommandMsgToMQ(dto);
        		sendMessageByInf(dto);
    		}
     } 
	
	/**
     * 指令接口查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<EtrCommandDispatcherDto> selectList(Map<String, Object> param){
    	return etrTradeSysInterfMapper.selectList(param);
    }
    
    /**
     * 指令回购质押品查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<EtrTradePledgeDto> selectPledgeList(List<EtrCommandDispatcherDto> list){
    	List<EtrTradePledgeDto> pledgelist = new ArrayList<EtrTradePledgeDto>(); 
    	
    	if(list != null && !list.isEmpty()) {
	    	for(EtrCommandDispatcherDto dto : list) {
	    		if("F02".equals(dto.getFinprodType2()) || "F03".equals(dto.getFinprodType2()) ) {
		    		List<EtrTradePledgeDto> queryEtrPledgeList = etrTradeSysInterfMapper.queryEtrPledgeListByEtrId(dto.getEntrustId());
		    		if(queryEtrPledgeList!=null && !queryEtrPledgeList.isEmpty()) {
		    			pledgelist.addAll(queryEtrPledgeList);
		    		}
	    		}
	    	}
    	}
    	return pledgelist;
    }


	@SuppressWarnings("unchecked")
	public String sendMessageByInf(EtrCommandDispatcherTotalDto dto) {
		String msg = "";
		logger.info("调用交易系统指令分发接口【{}】  接口URL：" + ServiceApplication.EXCHANGE_SERVICE_NAME + "/joyin/instruction/distribute");
		logger.info("请求报文【{}】", "message = " + JSON.toJSONString(dto));
		Map<String, Object> result = restTemplate.postForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + "/joyin/instruction/distribute", dto, Map.class);
		if (result != null) {
			String errorMsg = String.valueOf(result.get("errorMsg"));
			int errorCode = Integer.parseInt(result.get("errorCode").toString());
			logger.info("应答报文【{}】","errorCode:" + errorCode + ";errorMsg:" + errorMsg);
			if(errorCode != 0){
				msg = errorMsg;
			}
		}
		return msg;
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
		return etrTradeSysInterfMapper.selectEtrDetailList(param);
	}
}

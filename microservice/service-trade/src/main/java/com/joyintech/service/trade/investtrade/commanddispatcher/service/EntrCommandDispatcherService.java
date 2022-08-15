package com.joyintech.service.trade.investtrade.commanddispatcher.service;

import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.investtrade.commanddispatcher.dao.EntrCommandDispatcherMapper;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrCommandDispatcherDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrCommandDispatcherTotalDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EntrTradePledgeDto;
import com.joyintech.service.trade.investtrade.commanddispatcher.dto.EtrEntrustDetailDto;
import com.joyintech.tams.common.base.pojo.OriPledgeInfo;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令_换钱;

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
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(EntrCommandDispatcherService.class);

	/**
	 * 指令分发 <功能详细描述>
	 *
	 * @param param
	 * @return
	 * @see/EtrCommandDispatcher
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

		// ZGCPCN-1650 add 20220623
		for (EntrCommandDispatcherDto entrCommandDispatcherDto : dto.getEtr_entrust_list()) {
			boolean flag = true;
			for (EtrEntrustDetailDto entrustDetailDto : dto.getEtr_entrust_detail_list()) {
				if (entrustDetailDto.getEntrustId().equals(entrCommandDispatcherDto.getEntrustId())) {
					flag = false;
					break;
				}
			}
			if (flag) {
				EtrEntrustDetailDto entrustDetailDtoCopy = new EtrEntrustDetailDto();
				entrustDetailDtoCopy.setDisRate(BigDecimal.ONE);
				BeanUtils.copyProperties(entrCommandDispatcherDto, entrustDetailDtoCopy);
				dto.getEtr_entrust_detail_list().add(entrustDetailDtoCopy);
			}
		}

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

    	//已查询质押券的指令列表，防止重复查询质押券
    	List<String> entrustIds = new ArrayList<>();

    	if(list != null && !list.isEmpty()) {
	    	for(EntrCommandDispatcherDto dto : list) {
	    		if(entrustIds.contains(dto.getEntrustId())){
	    			continue;
				}
	    		if("F02".equals(dto.getFinprodType2()) || "F03".equals(dto.getFinprodType2()) ) {
					if (StringUtil.equals(DictCodeListExt3.投管业务类型_首期.getValue(), dto.getBusiTypeTg()) ||
							(StringUtil.equals(DictCodeListExt3.投管业务类型_中途换券.getValue(), dto.getBusiTypeTg()) &&
									!上农商_指令业务类型_协议回购中途换券指令_换钱.getValue().equals(dto.getEntrustBussType())
									)
					) {
						List<EntrTradePledgeDto> queryEtrPledgeList = entrTradeSysInterfMapper.queryEtrPledgeListByEtrId(dto.getEntrustId());
						if(queryEtrPledgeList!=null && !queryEtrPledgeList.isEmpty()) {
							pledgelist.addAll(queryEtrPledgeList);
						}
					}
					if(!StringUtil.equals(DictCodeListExt3.投管业务类型_首期.getValue(), dto.getBusiTypeTg())){
						List<OriPledgeInfo> oriFinDealPledgeList = entrTradeSysInterfMapper.getOriFinDealPledgeList(dto.getFinprodId());
						for (OriPledgeInfo oriPledgeInfo : oriFinDealPledgeList) {
							EntrTradePledgeDto pledgeDto = new EntrTradePledgeDto();
							BeanUtils.copyProperties(dto, pledgeDto);
							pledgeDto.setFinprodId(oriPledgeInfo.getPledgeFinprodId());
							pledgeDto.setFinprodAbbr(oriPledgeInfo.getPledgefinprodAbbr());
							pledgeDto.setTradeMarket(oriPledgeInfo.getPledgeTradeMarket());
							pledgeDto.setFinprodMarketId(oriPledgeInfo.getPledgeFinprodMarketId());
							pledgeDto.setPledgeFaceValue(oriPledgeInfo.getPledgeFaceValue());
							pledgeDto.setPledgeNumber(oriPledgeInfo.getPledgeNumber());
							pledgeDto.setPledgeRatio(oriPledgeInfo.getPledgeRatio());
							pledgeDto.setPledgeAmt(oriPledgeInfo.getPledgeTradeAmt());
							pledgeDto.setDisplaceDir("00");
							pledgelist.add(pledgeDto);
						}
					}
	    		}
	    		entrustIds.add(dto.getEntrustId());
	    	}
    	}
		logger.error("指令分发质押券详情：{}",JSONObject.toJSONString(pledgelist));
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

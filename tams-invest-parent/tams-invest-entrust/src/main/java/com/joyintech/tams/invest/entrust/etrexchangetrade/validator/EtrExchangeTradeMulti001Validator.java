/*
 * 文件名：EtrExchangeTrade001Validator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrexchangetrade.validator;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductConvertibleMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.FinProductConvertible;
import com.joyintech.tams.common.base.entity.WflEtrEntrust;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.*;
import com.joyintech.tams.invest.entrust.etrexchangetrade.service.EtrExchangeTrade001Service;
import com.joyintech.tams.invest.entrust.etrexchangetrade.service.EtrExchangeTradeMulti001Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * 
 * @author chendijun
 * @version 1.0 2020年7月1日
 * @see EtrExchangeTradeMulti001Validator
 * @since 1.0
 */
@Component
public class EtrExchangeTradeMulti001Validator implements EtrCommonValidator {

	@Autowired
	EtrExchangeTrade001Service etrExchangeTrade001Service;

	@Autowired
	EtrExchangeTradeMulti001Service etrExchangeTradeMulti001Service;
	
	 /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

	@Autowired
	private FinProductConvertibleMapper finProductConvertibleMapper;
	
	List<ValidMessage> resultList = new ArrayList<ValidMessage>();
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ValidMessage> bussValid(Object value) {
		List<ValidMessage> errorList = new ArrayList<>();
		// TODO Auto-generated method stub
		EtrExchangeTradeMulti001Dto dto = (EtrExchangeTradeMulti001Dto) value;
		if (dto.getOperateType().equals("save") || dto.getOperateType().equals("submit")) {
			//ckeck必填字段
			if (!StringUtil.isNotEmpty(dto.getPortfolioTreeId())) {
				errorList.add(new ValidMessage("portfolioTreeId", this.getMessage("ETR_ERROR_0065")));
			}

			if (!StringUtil.isNotEmpty(dto.getStockId())&&!StringUtil.isNotEmpty(dto.getFinprodId())) {
				errorList.add(new ValidMessage("stockId", this.getMessage("ETR_ERROR_0016")));
			}

			if (!StringUtil.isNotEmpty(dto.getPs())) {
				errorList.add(new ValidMessage("ps", this.getMessage("ETR_ERROR_0017")));
			}

//			if (!StringUtil.isNotEmpty(dto.getTargetType())) {
//				errorList.add(new ValidMessage("targetType", this.getMessage("ETR_ERROR_0022")));
//			}

			if (!StringUtil.isNotEmpty(dto.getTargetValue())) {
				errorList.add(new ValidMessage("targetValue", this.getMessage("ETR_ERROR_0066")));
			}

//			if(StringUtil.equals(CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"),"02")){
				if(!StringUtil.isNotEmpty(dto.getBusinessModel())
						&&dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
					errorList.add(new ValidMessage("businessModel", "请输入业务模式"));
				}
//			}
//			else{
//				if(!StringUtil.isNotEmpty(dto.getInvAim())
//						&&dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
//					errorList.add(new ValidMessage("invAim", this.getMessage("ETR_ERROR_0060")));
//				}
//			}

			//组合分配列表的校验
			if(!StringUtil.isEmptyList(dto.getTradeMultiPtl001Dtos())) {
				Map<String,List<EtrExchangeTradeMultiPtl001Dto>> groupMap= dto.getTradeMultiPtl001Dtos().stream().collect(Collectors.groupingBy(item->item.getPortfolioId()+"_"+item.getAcctNo()));
				BigDecimal totalProportion = BigDecimal.ZERO;
				Map<String,String> indexMap = new HashMap<>();
				for(int i = 0;i<dto.getTradeMultiPtl001Dtos().size();i++) {
					EtrExchangeTradeMultiPtl001Dto tradeMultiPtl001Dto = dto.getTradeMultiPtl001Dtos().get(i);
					if(!StringUtil.isNotEmpty(tradeMultiPtl001Dto.getPortfolioId()) || !StringUtil.isNotEmpty(tradeMultiPtl001Dto.getAcctNo())) {
						errorList.add(new ValidMessage("", "第"+(i+1)+"行：组合分配列表的组合和资金账户不能为空！"));
					}
					else {
						if(!StringUtil.isEmptyList(groupMap.get(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo()))
								&& groupMap.get(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo()).size()>1) {
							if(StringUtil.isNotEmpty(indexMap.get(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo()))) {
								indexMap.put(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo(),indexMap.get(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo())+","+i);
							}
							else {
								indexMap.put(tradeMultiPtl001Dto.getPortfolioId()+"_"+tradeMultiPtl001Dto.getAcctNo(),i+"");
							}
						}
					}

					if(StringUtil.equals(dto.getWeightType(),DictCodeListExt3.权重方式_自定义.getValue())) {
						if(!StringUtil.isNotEmpty(tradeMultiPtl001Dto.getProportion())) {
							errorList.add(new ValidMessage("", "第"+(i+1)+"行：权重方式为自定义时，分配比例不能为空！"));
						}
						else if(BigDecimal.ZERO.compareTo(tradeMultiPtl001Dto.getProportion())>0
								|| new BigDecimal(100).compareTo(tradeMultiPtl001Dto.getProportion())<0) {
							errorList.add(new ValidMessage("", "第"+(i+1)+"行：权重方式为自定义时，分配比例请输入0-100的数值！"));
						}
						else {
							totalProportion = ArithmeticUtil.doAdd(totalProportion,tradeMultiPtl001Dto.getProportion());
						}
					}

					if(!StringUtil.isNotEmpty(tradeMultiPtl001Dto.getPrice()) && !StringUtil.isNotEmpty(tradeMultiPtl001Dto.getFprice())
							&& !StringUtil.isNotEmpty(tradeMultiPtl001Dto.getYield()) && !StringUtil.isNotEmpty(tradeMultiPtl001Dto.getEntrustPrice())) {
						errorList.add(new ValidMessage("", "第"+(i+1)+"行：请输入组合分配列表的指令价格、净价、全价、到期收益率或回购利率！"));
					}

					EtrExchangeTrade001Dto etrExchangeTrade001Dto = etrExchangeTradeMulti001Service.genEtrExchangeTrade001Dto(dto,tradeMultiPtl001Dto);
					etrExchangeTrade001Service.setEntrustBussType(etrExchangeTrade001Dto);
					AjaxResult ajax  = etrExchangeTrade001Service.checkSave(etrExchangeTrade001Dto);
					if (ajax != null && !StringUtil.isEmptyList(ajax.getErrorList())) {
						for(ValidMessage validMessage:ajax.getErrorList()) {
							validMessage.setMessage("第"+(i+1)+"行："+validMessage.getMessage());
							errorList.add(validMessage);
						}
					}
				}

				for(String key:indexMap.keySet()) {
					errorList.add(new ValidMessage("", "第"+indexMap.get(key)+"行：组合分配列表的组合和资金账户重复！"));
				}

				if(StringUtil.equals(dto.getWeightType(),DictCodeListExt3.权重方式_自定义.getValue())
						&& new BigDecimal(100).compareTo(totalProportion)<0
				) {
					errorList.add(new ValidMessage("", "权重方式为自定义时，分配比例总和不能超过100！"));
				}
			}
			else {
				errorList.add(new ValidMessage("", "组合分配列表不能为空"));
			}
		}
		return errorList;
	}
}

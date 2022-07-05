/*
 * 文件名：CmpBlockTradeBondService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.background.component.order.exchange.business.service;

import java.math.BigDecimal;

import com.joyintech.background.component.order.exchange.common.dto.EtrPledgeDto;
import com.joyintech.background.component.order.exchange.database.dao.EntrustComponentMapper;
import com.joyintech.background.component.order.exchange.database.entity.EntrustComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepHwayChgSecDto;
import com.joyintech.background.component.order.exchange.common.constant.DictCodeList;
import com.joyintech.background.component.order.exchange.common.constant.LocalConstant;
import com.joyintech.background.component.order.exchange.common.service.EtrSubComponentService;
import com.joyintech.background.component.order.exchange.common.util.EtrComponentUtil;
import com.joyintech.background.component.order.exchange.database.dao.EtrPledgeComponentMapper;
import com.joyintech.background.component.order.exchange.database.entity.EtrPledgeComponent;


/**
 * 中途换券-Service
 * 
 * @author lihuadong
 * @version 1.0 2020年7月10日
 * @see AgreRepHwayChgSecService
 * @since 1.0
 */
@Component
class AgreRepHwayChgSecService extends EtrSubComponentService<AgreRepHwayChgSecDto> {
	@Autowired
	private EtrPledgeComponentMapper etrPledgeComponentMapper;
	@Autowired
	private EntrustComponentMapper etrMapper;

	@Override
    public BindingResult entrustCheck(AgreRepHwayChgSecDto dto, BindingResult result) {
    	//组合代码
    	if(StringUtil.isEmpty(dto.getPortfolioId())){
    		result.addError(error(dto.getClass().getSimpleName(), LocalConstant.PORTFOLIOID,
                    "ETR_COMPONENT_ERR_012","组合代码"
            ));
    	}
    	//资金账号
    	if(StringUtil.isEmpty(dto.getDealCurAcct())){
    		result.addError(error(dto.getClass().getSimpleName(), LocalConstant.ACCTNO,
                    "ETR_COMPONENT_ERR_012","资金账号"
            ));
    	}
    	//交易场所
    	if(StringUtil.isEmpty(dto.getTradeMarket())){
    		result.addError(error(dto.getClass().getSimpleName(), LocalConstant.TRADE_MARKET,
                    "ETR_COMPONENT_ERR_012","交易场所"
            ));
    	}
    	//委托方向
    	if(StringUtil.isEmpty(dto.getPs())){
    		result.addError(error(dto.getClass().getSimpleName(), LocalConstant.PS,
                    "ETR_COMPONENT_ERR_012","委托方向"
            ));
    	}
    	
    	//回购金额
    	if(dto.getEntrustAmt() == null){
    		result.addError(error(dto.getClass().getSimpleName(), LocalConstant.TRADEAMT,
                    "ETR_COMPONENT_ERR_012","回购金额"
            ));
    	}
    	//回购方向
    	if(dto.getBusiPs() == null){
    		result.addError(error(dto.getClass().getSimpleName(), "repPs",
                    "ETR_COMPONENT_ERR_012","回购方向"
            ));
    	}
    	//质押券
    	if(dto.getBusiPs()!=null && DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getBusiPs())&& StringUtil.isEmpty(dto.getFinprodId())){
    		result.addError(new FieldError(dto.getClass().getSimpleName(),"repSecId","正回购方质押券不能为空"));
    	}
    	//质押数量
    	if(dto.getBusiPs()!=null && DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getBusiPs())&& dto.getPledgeNumber() == null){
    		result.addError(new FieldError(dto.getClass().getSimpleName(),"repSecNum","正回购方质押券数量不能为空"));

    	}
    	//折算比例
    	if(dto.getBusiPs()!=null && DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getBusiPs())&& dto.getPledgeRatio() == null){
    		result.addError(new FieldError(dto.getClass().getSimpleName(),"conRatio","正回购方质折算比例为空"));

    	}
    	if(dto.getPledgeRatio() != null && !EtrComponentUtil.isNumberInRange(dto.getPledgeRatio(), BigDecimal.ZERO,
    			BigDecimal.valueOf(1), EtrComponentUtil.RANGE_MODE_CL_CL)){
    		result.addError(new FieldError(dto.getClass().getSimpleName(),"conRatio","折算比例不能超过100%"));

    	}
    	//成交编号
    	if(dto.getTradeId() == null){
    		if("03".equals(dto.getTradeMarket())){
    			result.addError(error(dto.getClass().getSimpleName(), "tradeId",
                        "ETR_COMPONENT_ERR_012","成交编号"
                ));
    		}else{
    			result.addError(error(dto.getClass().getSimpleName(), "intiTradeId",
                        "ETR_COMPONENT_ERR_012","初始成交编号"
                ));
    		}
    		
    	}
    	return result;
    }
	 /**
	    * 执行个别登录
	    * 
	    * @param dto 指令信息
	    */
		@Override
	   public void doInsert(AgreRepHwayChgSecDto dto) {
			EtrPledgeComponent etrPledgeEntity = new EtrPledgeComponent();
	       BeanUtil.copyPropertiesIgnoreNull(etrPledgeEntity, dto);

	       // 先根据指令编号删除所有该指令下的质押券数据，再插入新的质押券数据
			etrPledgeComponentMapper.deleteByEntrustId(etrPledgeEntity);
			if(!CollectionUtils.isEmpty(dto.getList())){
				for(EtrPledgeDto etrPledgeDto:dto.getList()){
					EtrPledgeComponent etrPledgeEntityDto = new EtrPledgeComponent();
					BeanUtil.copyPropertiesIgnoreNull(etrPledgeEntityDto, etrPledgeDto);
					etrPledgeComponentMapper.insertSelective(etrPledgeEntityDto);
				}
			}else{
				if(StringUtil.isNotEmpty(dto.getFinprodId())) {
					etrPledgeComponentMapper.insertSelective(etrPledgeEntity);
				}
			}



	   }
		
		 /**
	    * 执行个别更新
	    * 
	    * @param dto 指令信息
	    */
		@Override
	   public void doUpdate(AgreRepHwayChgSecDto dto) {
			EtrPledgeComponent etrPledgeEntity = new EtrPledgeComponent();
//			EtrPledgeComponent delEtrPledgeEntity = new EtrPledgeComponent();
	        BeanUtil.copyPropertiesIgnoreNull(etrPledgeEntity, dto);
//			BeanUtil.copyPropertiesIgnoreNull(delEtrPledgeEntity, dto);
			//改变了质押券，这里需要先删除再插入，否则无法改变质押券信息
//			if(StringUtil.isNotEmpty(dto.getOldFinprodId())) {
//				delEtrPledgeEntity.setFinprodId(dto.getOldFinprodId());
//				etrPledgeComponentMapper.deleteByPrimaryKey(delEtrPledgeEntity);
//			}
			// 先根据指令编号删除所有该指令下的质押券数据，再插入新的质押券数据
			etrPledgeComponentMapper.deleteByEntrustId(etrPledgeEntity);
			if(StringUtil.isNotEmpty(dto.getFinprodId())) {
				//        etrPledgeComponentMapper.updateByPrimaryKeySelective(etrPledgeEntity);
				etrPledgeComponentMapper.insertSelective(etrPledgeEntity);
			}
	   }
		 /**
	    * 执行个别删除
	    * 
	    * @param dto 指令信息
	    */
		@Override
	   public void doDelete(AgreRepHwayChgSecDto dto) {
			EtrPledgeComponent etrPledgeEntity = new EtrPledgeComponent();
	       BeanUtil.copyPropertiesIgnoreNull(etrPledgeEntity, dto);
	       if(StringUtil.isNotEmpty(dto.getFinprodId()))
	       etrPledgeComponentMapper.deleteByPrimaryKey(etrPledgeEntity);
	   }
}

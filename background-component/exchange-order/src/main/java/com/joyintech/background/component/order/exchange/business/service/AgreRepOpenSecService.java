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



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepOpenSecDto;
import com.joyintech.background.component.order.exchange.common.constant.DictCodeList;
import com.joyintech.background.component.order.exchange.common.constant.LocalConstant;
import com.joyintech.background.component.order.exchange.common.service.EtrSubComponentService;
import com.joyintech.background.component.order.exchange.database.dao.EtrPledgeComponentMapper;
import com.joyintech.background.component.order.exchange.database.entity.EtrPledgeComponent;



/**
 * 解除质押-Service
 * 
 * @author lihuadong
 * @version 1.0 2020年7月10日
 * @see AgreRepOpenSecService
 * @since 1.0
 */
@Component
class AgreRepOpenSecService extends EtrSubComponentService<AgreRepOpenSecDto> {
	@Autowired
	private EtrPledgeComponentMapper etrPledgeComponentMapper;
	@Override
    public BindingResult entrustCheck(AgreRepOpenSecDto dto, BindingResult result) {
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
		// XYHGFXSJ-111:质押券字段用错 modify by fuyongsheng 20220719 start
		// if(dto.getBusiPs()!=null && DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getBusiPs())&& StringUtil.isEmpty(dto.getFinprodId())){
		if(dto.getBusiPs()!=null && DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getBusiPs())&& StringUtil.isEmpty(dto.getRepSecId())){
    		result.addError(new FieldError(dto.getClass().getSimpleName(),"repSecId","正回购方质押券不能为空"));
    	}
		// XYHGFXSJ-111:质押券字段用错 modify by fuyongsheng 20220719 end
		//质押数量
    	if(dto.getBusiPs()!=null && DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getBusiPs())&& dto.getPledgeNumber() == null){
    		result.addError(new FieldError(dto.getClass().getSimpleName(),"repSecNum","正回购方质押券数量不能为空"));

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
	   public void doInsert(AgreRepOpenSecDto dto) {
		   EtrPledgeComponent etrPledgeEntity = new EtrPledgeComponent();
	       BeanUtil.copyPropertiesIgnoreNull(etrPledgeEntity, dto);
	       if(StringUtil.isNotEmpty(dto.getFinprodId()))
	       etrPledgeComponentMapper.insertSelective(etrPledgeEntity);
	   }
		
		 /**
	    * 执行个别更新
	    * 
	    * @param dto 指令信息
	    */
		@Override
	   public void doUpdate(AgreRepOpenSecDto dto) {
		   EtrPledgeComponent etrPledgeEntity = new EtrPledgeComponent();
	       BeanUtil.copyPropertiesIgnoreNull(etrPledgeEntity, dto);
	       if(StringUtil.isNotEmpty(dto.getFinprodId()))
	       etrPledgeComponentMapper.updateByPrimaryKeySelective(etrPledgeEntity);
	   }
		 /**
	    * 执行个别删除
	    * 
	    * @param dto 指令信息
	    */
		@Override
	   public void doDelete(AgreRepOpenSecDto dto) {
		   EtrPledgeComponent etrPledgeEntity = new EtrPledgeComponent();
	       BeanUtil.copyPropertiesIgnoreNull(etrPledgeEntity, dto);
	       if(StringUtil.isNotEmpty(dto.getFinprodId()))
	       etrPledgeComponentMapper.deleteByPrimaryKey(etrPledgeEntity);
	   }
}

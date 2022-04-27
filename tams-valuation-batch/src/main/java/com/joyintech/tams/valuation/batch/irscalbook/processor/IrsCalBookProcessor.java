/*
 * 文件名：IrsCalBookProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5
 * 修改人：殷亚
 * 修改时间：2022年4月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */
 
package com.joyintech.tams.valuation.batch.irscalbook.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.investratechange.dto.InvestRateChangeResult;
import com.joyintech.tams.valuation.batch.irscalbook.service.IrsCalBookNewService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 利率互换计提账务处理 - processor
 * @author 殷亚
 * @date 2022/4/25 15:30
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsCalBookProcessor extends BaseProcessor<NeedOperateEntity, InvestRateChangeResult>  {
	
    /**
     * 传入参数
     */
    private Map<String, Object> param = new HashMap<String, Object>();

    /**
     * 设置 param
     * 
     * @param param param
     */
    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    /**
     * processor方法的 Service
     */
    @Autowired
    private IrsCalBookNewService irsCalBookNewService;


    @Override
	public InvestRateChangeResult process(NeedOperateEntity items) throws Exception {
		//获取reader方法的返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult) items;
		
        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);
        InvestRateChangeResult result = new InvestRateChangeResult();
        result = irsCalBookNewService.execute(thisParams);
        //当为null时候，给予初始化
        if(result == null){
            result = new InvestRateChangeResult();
        }
        result.setProdId(
            acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());

        return result;
    }
}

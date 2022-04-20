/*
 * 文件名：PlanCurrencyProAccountingProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2019年1月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 * 
 * 
 */

package com.joyintech.tams.valuation.batch.valschemelanding.service;

import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.valschemelanding.dto.ValSchemeLandingProceesorDto;
import com.joyintech.tams.valuation.batch.valschemelanding.dto.ValSchemeLandingReaderDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.SchemeDetailDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 估值方案落地批处理 processor相关业务处理
 *
 * @author weipeng
 * @version 1.0 2022年03月25日
 * @see ValSchemeLandingProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ValSchemeLandingProcessorService {
    /**
     * 标的共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("unchecked")
    public ValSchemeLandingProceesorDto execute(Map<String, Object> param1) {

        // 获取reader返回值
        ValSchemeLandingReaderDto acctEntity = (ValSchemeLandingReaderDto)param1.get(
            GlobalConstant.READER_RESULT);
        // 定义返回值
        ValSchemeLandingProceesorDto returnResult = new ValSchemeLandingProceesorDto();
        TargetFinProduct finProduct = new TargetFinProduct();
        finProduct.setFinprodId(acctEntity.getFinprodId());
        finProduct.setType18(acctEntity.getSecType());
        finProduct.setTradeMarket(acctEntity.getTradeMarket());
        finProduct.setType1(acctEntity.getType1());
        SchemeDetailDto schemeDetailDto = finProductCommonService.querySchemaDetail(acctEntity.getHappenDate(), acctEntity.getBooksetId(), acctEntity.getInvAim(), finProduct);
        if(schemeDetailDto == null){
            return returnResult;
        }
        BeanUtil.copyPropertiesIgnoreNull(returnResult, schemeDetailDto);
        BeanUtil.copyPropertiesIgnoreNull(returnResult, acctEntity);
        return returnResult;
    }

}

/*
 * 文件名：IrsBaseInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.irs.service;

import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.IrsCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.LendStandardCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.irs.IrsBaseInfoRecService;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.irs.IrsCashPlanRecService;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.lendstandard.LendStandardPlanRecService;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.lendstandard.LendStandardRateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.lendstandard.LendStandardRecService;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.lendstandard.LendStandardRuleRecService;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.irs.dto.IrsBaseInfoRecResult;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.lendstandard.dto.LendStandardRecResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 利率互换基础信息接口数据处理
 * @author 殷亚
 * @date 2022/4/15 9:46
 * @return
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsBaseInfoService {
 
 

    /**
     * 利率互换标的信息
     */
    @Autowired
    private IrsBaseInfoRecService irsBaseInfoRecService;
    

    
    /**
     * 利率互换付息计划
     */
    @Autowired
    private IrsCashPlanRecService irsCashPlanRecService;

    
    /**
     * 
     * 利率互换基础信息接口数据处理
     * @param acctEntity
     * @return
     * @see
     * @since 1.0
     */
    public IrsBaseInfoRecResult execute(TemplateOrderInfoReaderResult acctEntity){
        // 定义返回变量
        IrsCommonResult resultCommon = new IrsCommonResult();
        // 设置启动源属性
        TemplateSourceFlgDTO templateSourceFlgDTO = new TemplateSourceFlgDTO();
        templateSourceFlgDTO.setCallSourceType(DictCodeInterfList.文件解析功能启动源_TASK.getValue());
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("TMP_E_NAME", acctEntity.getTmpEName());
        if (TempConstant.利率互换标的信息.equals(acctEntity.getTmpEName())) {
            resultCommon = irsBaseInfoRecService.execute(templateSourceFlgDTO,
                acctEntity);
        }else if(TempConstant.利率互换付息计划.equals(acctEntity.getTmpEName())){
        	resultCommon = irsCashPlanRecService.execute(templateSourceFlgDTO,
                    acctEntity);   
        }
        resultCommon.setParamMap(paramMap);
        return new IrsBaseInfoRecResult(resultCommon);
    }
}

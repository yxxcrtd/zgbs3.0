/*
 * 文件名：IrsDealRecComForInterfService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.service;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.IrsDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.irs.IrsDealRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.irs.IrsRecRateService;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.dto.IrsDealRecProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利率互换交易 实现
 * @author 殷亚
 * @date 2022/4/21 10:19
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsDealRecComForInterfService {

    /**
     * 利率互换首期交易
     */
    @Autowired
    private IrsDealRecService irsDealRecService;
    
    /**
     * 利率互换存续期交易
     */
    @Autowired
    private IrsRecRateService irsRecRateService;
    

    @Value("${interf.properties.fourLevelSubjectKg}")
    private String fourLevelSubjectKg;

    @SuppressWarnings({"unchecked"})
    public IrsDealRecProcessorResult execute(TemplateOrderInfoReaderResult acctEntity) {
        IrsDealCommonResult result = new IrsDealCommonResult();
        // 设置启动源属性
        TemplateSourceFlgDTO templateSourceFlgDTO = new TemplateSourceFlgDTO();
        templateSourceFlgDTO.setCallSourceType(DictCodeInterfList.文件解析功能启动源_TASK.getValue());
        
        Map<String,Object> paramMap = new HashMap<String,Object>();
        List<String> bookSetIds = (List<String>)acctEntity.getParams().get(InterfCommonConstant.接口_运行接口账套ID);
        paramMap.put(InterfCommonConstant.接口_运行接口账套ID,bookSetIds);
        paramMap.put(InterfCommonConstant.接口模式,(String)acctEntity.getParams().get(InterfCommonConstant.接口模式));
        paramMap.put("TMP_E_NAME", acctEntity.getTmpEName());
        List<String> ps = new ArrayList<String>();
        acctEntity.setFourLevelSubjectKg(fourLevelSubjectKg);
        
        if (TempConstant.利率互换交易信息.equals(acctEntity.getTmpEName())) {
            result = irsDealRecService.execute(templateSourceFlgDTO, acctEntity);
            paramMap.put("busiType", DictCodeList.业务类型_利率互换交易.getValue());
            ps.add(DictCodeList.交易方向_买入.getValue());
            paramMap.put("pslist", ps);
            List<String> fpsList = new ArrayList<String>();
            fpsList.add(DictCodeList.交易方向_买入.getValue());
            paramMap.put("fpsList", fpsList);
            paramMap.put(InterfCommonConstant.读取文件状态, result.getParamMap()==null?null:result.getParamMap().get(InterfCommonConstant.读取文件状态));

        }else if(TempConstant.利率互换存续期交易.equals(acctEntity.getTmpEName())){
//            result = irsRecRateService.execute(templateSourceFlgDTO, acctEntity);
//            paramMap.put("busiType", DictCodeList.业务类型_拆借交易.getValue());
//            ps.add(DictCodeList.交易方向_收息到账.getValue());
//            paramMap.put("pslist", ps);
//            List<String> fpsList = new ArrayList<String>();
//            fpsList.add(DictCodeList.交易方向_还本到账.getValue());
//            paramMap.put("fpsList", fpsList);
//            paramMap.put(InterfCommonConstant.读取文件状态, result.getParamMap()==null?null:result.getParamMap().get(InterfCommonConstant.读取文件状态));
        }
        result.setParamMap(paramMap);
        return new IrsDealRecProcessorResult(result);
    }
}

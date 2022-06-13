/*
 * 文件名：PtlCashFlowRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：tanxuan
 * 修改时间：2020年4月3日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.service;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.PtlCashFlowCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.ptlCashFlow.PtlCashFlowTemplateRecService;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.dto.PtlCashFlowProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批处理启动文件接收批处理 对自定义现金流交易信息文件系列机型处理，供给Writer使用
 * 
 * @author tanxuan
 * @version 1.0 2020年4月3日
 * @see PtlCashFlowRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PtlCashFlowRecService {

    /**
     * 自定义现金流交易 模板处理Service
     */
    @Autowired
    private PtlCashFlowTemplateRecService ptlCashFlowTemplateRecService;

    /**
     * 自定义现金流交易信息文件处理
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public PtlCashFlowProcessorResult execute(TemplateOrderInfoReaderResult acctEntity) {

        // 定义返回变量
        PtlCashFlowCommonResult rtnResult = new PtlCashFlowCommonResult();

        // 设置启动源属性
        TemplateSourceFlgDTO templateSourceFlgDTO = new TemplateSourceFlgDTO();
        templateSourceFlgDTO.setCallSourceType(DictCodeInterfList.文件解析功能启动源_TASK.getValue());
        Map<String,Object> paramMap = new HashMap<String,Object>();
        List<String> bookSetIds = (List)acctEntity.getParams().get(InterfCommonConstant.接口_运行接口账套ID);
        paramMap.put(InterfCommonConstant.接口_运行接口账套ID,bookSetIds);
        paramMap.put(InterfCommonConstant.接口模式,(String)acctEntity.getParams().get(InterfCommonConstant.接口模式));
        List<String> ps = new ArrayList<String>();
        List<String> busiTypes = new ArrayList<String>();
        // 01-债券买卖交易
        if (TempConstant.自定义现金流交易.equals(acctEntity.getTmpEName())) {
            rtnResult = ptlCashFlowTemplateRecService.execute(templateSourceFlgDTO,
                acctEntity);
            busiTypes.add(DictCodeListExt3.业务类型_自定义现金流交易.getValue());
            paramMap.put("busiTypelist", busiTypes);
            ps.add(DictCodeList.交易方向_收取.getValue());
            ps.add(DictCodeList.交易方向_支付.getValue());
            paramMap.put("pslist", ps);//删除除这些交易方向的数据
        }else{
        	paramMap.put("busiType", "");
        	paramMap.put("pslist", null);
        }
        rtnResult.setParamMap(paramMap);
        PtlCashFlowProcessorResult result = new PtlCashFlowProcessorResult(rtnResult);
        return result;
    }

}

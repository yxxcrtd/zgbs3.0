/*
 * 文件名：NPrdBaseInfoComForInterfService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年6月28日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.sec.service;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecExecDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecReceicableTradeTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecVestAssetDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecVestPeriodTemplateRecService;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.sec.dto.SecDealInfoProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批处理启动文件接收批处理 对债券交易信息文件系列机型处理，供给Writer使用
 * 
 * @author 魏鹏
 * @version 1.0 2018年6月28日
 * @see SecDealInfoRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecDealInfoRecService {

    /**
     * 债券买卖交易 模板处理Service
     */
    @Autowired
    private SecVestAssetDealTemplateRecService secVestAssetDealTemplateRecService;

    /**
     * 存款类资产-存款存续期 模板处理Service
     */
    @Autowired
    private SecVestPeriodTemplateRecService secVestPeriodTemplateRecService;
    
    /**
     * 债券应收款项转让交易 模板处理Service
     */
    @Autowired
    private SecReceicableTradeTemplateRecService secReceicableTradeTemplateRecService;

    /**
     * 债券行权交易 模板处理Service
     */
    @Autowired
    private SecExecDealTemplateRecService secExecDealTemplateRecService;
    
    /**
     * 债券交易信息文件处理
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public SecDealInfoProcessorResult execute(TemplateOrderInfoReaderResult acctEntity) {

        // 定义返回变量
        AssetDealInfoValCommonResult rtnResult = new AssetDealInfoValCommonResult();

        // 设置启动源属性
        TemplateSourceFlgDTO templateSourceFlgDTO = new TemplateSourceFlgDTO();
        templateSourceFlgDTO.setCallSourceType(DictCodeInterfList.文件解析功能启动源_TASK.getValue());
        Map<String,Object> paramMap = new HashMap<String,Object>();
        List<String> bookSetIds = (List<String>)acctEntity.getParams().get(InterfCommonConstant.接口_运行接口账套ID);
        paramMap.put(InterfCommonConstant.接口_运行接口账套ID,bookSetIds);
        paramMap.put(InterfCommonConstant.接口模式,(String)acctEntity.getParams().get(InterfCommonConstant.接口模式));
        List<String> ps = new ArrayList<String>();
        // 01-债券买卖交易
        if (TempConstant.债券买卖交易.equals(acctEntity.getTmpEName())) {
            rtnResult = secVestAssetDealTemplateRecService.execute(templateSourceFlgDTO,
                acctEntity);
            paramMap.put("busiType", DictCodeList.业务类型_银行间债券交易.getValue());
            ps.add(DictCodeList.交易方向_买入.getValue());
            ps.add(DictCodeList.交易方向_卖出.getValue());
            ps.add(DictCodeList.交易方向_申购.getValue());
            ps.add(DictCodeList.交易方向_申购确认.getValue());
            /**add by wujie on 20190223 start**/
            ps.add(DictCodeList.交易方向_行权_回售.getValue());
            ps.add(DictCodeList.交易方向_行权_赎回.getValue());
            /**add by wujie on 20190223 end**/
            ps.add(DictCodeListExt1.交易方向_债转股.getValue());
            
            paramMap.put("pslist", ps);

            /**add by wujie on 20210506 start**/
            paramMap.put(InterfCommonConstant.读取文件状态, rtnResult.getParamMap()==null?null:rtnResult.getParamMap().get(InterfCommonConstant.读取文件状态));
            /**add by wujie on 20210506 end**/
        }
        // 02-债券存续期交易
        else if (TempConstant.债券存续期交易.equals(acctEntity.getTmpEName())) {
            rtnResult = secVestPeriodTemplateRecService.execute(templateSourceFlgDTO,
                acctEntity);
            paramMap.put("busiType", DictCodeList.业务类型_银行间债券交易.getValue());
            ps.add(DictCodeList.交易方向_收息到账.getValue());
            ps.add(DictCodeList.交易方向_还本到账.getValue());
            paramMap.put("pslist", ps);//删除除这些交易方向的数据

            /**add by wujie on 20210506 start**/
            paramMap.put(InterfCommonConstant.读取文件状态, rtnResult.getParamMap()==null?null:rtnResult.getParamMap().get(InterfCommonConstant.读取文件状态));
            /**add by wujie on 20210506 end**/
        }
        // 03-债券应收款转让交易
        else if (TempConstant.债券应收款项转让交易.equals(acctEntity.getTmpEName())) {
            rtnResult = secReceicableTradeTemplateRecService.execute(templateSourceFlgDTO,
                    acctEntity);
                paramMap.put("busiType", DictCodeList.业务类型_银行间债券交易.getValue());
                ps.add(DictCodeList.交易方向_应收款项转入.getValue());
                ps.add(DictCodeList.交易方向_应收款项转出.getValue());
                paramMap.put("pslist", ps);

            /**add by wujie on 20210506 start**/
            paramMap.put(InterfCommonConstant.读取文件状态, rtnResult.getParamMap()==null?null:rtnResult.getParamMap().get(InterfCommonConstant.读取文件状态));
            /**add by wujie on 20210506 end**/
        }
        /** add by yanya on 20210309 添加债券行权交易 start**/
        else if (TempConstant.债券行权交易.equals(acctEntity.getTmpEName())) {
            rtnResult = secExecDealTemplateRecService.execute(templateSourceFlgDTO, acctEntity);
            List<String> busiTypes = new ArrayList<String>();
            busiTypes.add(DictCodeList.业务类型_银行间债券交易.getValue());
            busiTypes.add(DictCodeList.业务类型_债券交易预处理.getValue());
            paramMap.put("busiTypelist", busiTypes);
            ps.add(DictCodeListExt3.交易方向_行权回售申请.getValue());
            ps.add(DictCodeListExt3.交易方向_行权赎回结转.getValue());
            /** modify by yanya on 20210323 增加回售、赎回**/
            ps.add(DictCodeList.交易方向_行权_赎回.getValue());
            ps.add(DictCodeList.交易方向_行权_回售.getValue());
            paramMap.put("pslist", ps);
        }
        else{
        	rtnResult.setDataNum(0);
        }
        rtnResult.setParamMap(paramMap);
        SecDealInfoProcessorResult result = new SecDealInfoProcessorResult(rtnResult);
        return result;
    }

}

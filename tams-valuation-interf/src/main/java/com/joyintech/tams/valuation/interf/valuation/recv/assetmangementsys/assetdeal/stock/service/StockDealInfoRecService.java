/*
 * 文件名：NPrdBaseInfoComForInterfService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年6月28日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.stock.service;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecExecDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecReceicableTradeTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.stock.StockCirculateTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.stock.StockDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.stock.StockPeriodDealTemplateRecService;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.stock.dto.StockDealInfoProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批处理启动文件接收批处理 对股票交易信息文件系列机型处理，供给Writer使用
 * 
 * @author 魏鹏
 * @version 1.0 2018年6月28日
 * @see StockDealInfoRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class StockDealInfoRecService {

    /**
     * 股票买卖交易 模板处理Service
     */
    @Autowired
    private StockDealTemplateRecService stockDealTemplateRecService;

    /**
     * 股票买卖交易 模板处理Service
     */
    @Autowired
    private StockCirculateTemplateRecService stockCirculateTemplateRecService;

    /**
     * 存款类资产-存款存续期 模板处理Service
     */
    @Autowired
    private StockPeriodDealTemplateRecService stockPeriodDealTemplateRecService;
    
    /**
     * 股票应收款项转让交易 模板处理Service
     */
    @Autowired
    private SecReceicableTradeTemplateRecService secReceicableTradeTemplateRecService;

    /**
     * 股票行权交易 模板处理Service
     */
    @Autowired
    private SecExecDealTemplateRecService secExecDealTemplateRecService;
    
    /**
     * 股票交易信息文件处理
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public StockDealInfoProcessorResult execute(TemplateOrderInfoReaderResult acctEntity) {

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
        // 01-股票买卖交易
        if (TempConstant.股票交易.equals(acctEntity.getTmpEName())) {
            rtnResult = stockDealTemplateRecService.execute(templateSourceFlgDTO,
                acctEntity);
            paramMap.put("busiType", DictCodeList.业务类型_股票交易.getValue());
            ps.add(DictCodeListExt4.交易方向_新股申购.getValue());
            ps.add(DictCodeListExt2.交易方向_新股中签.getValue());
            ps.add(DictCodeList.交易方向_买入.getValue());
            ps.add(DictCodeListExt2.交易方向_增发.getValue());
            ps.add(DictCodeListExt2.交易方向_配股.getValue());
            ps.add(DictCodeList.交易方向_卖出.getValue());
            ps.add(DictCodeListExt4.交易方向_股票拆分交易.getValue());
            ps.add(DictCodeListExt4.交易方向_股票合并交易.getValue());

            paramMap.put("pslist", ps);

            /**add by wujie on 20210506 start**/
            paramMap.put(InterfCommonConstant.读取文件状态, rtnResult.getParamMap()==null?null:rtnResult.getParamMap().get(InterfCommonConstant.读取文件状态));
            /**add by wujie on 20210506 end**/
        }
        // 02-股票上市流通
        else if (TempConstant.股票上市流通.equals(acctEntity.getTmpEName())) {
            rtnResult = stockCirculateTemplateRecService.execute(templateSourceFlgDTO,
                acctEntity);
            paramMap.put("busiType", DictCodeList.业务类型_股票交易.getValue());
            ps.add(DictCodeList.交易方向_上市流通.getValue());
            paramMap.put("pslist", ps);//删除除这些交易方向的数据

            /**add by wujie on 20210506 start**/
            paramMap.put(InterfCommonConstant.读取文件状态, rtnResult.getParamMap()==null?null:rtnResult.getParamMap().get(InterfCommonConstant.读取文件状态));
            /**add by wujie on 20210506 end**/
        }
        // 02-股票存续期交易
        else if (TempConstant.股票存续期交易.equals(acctEntity.getTmpEName())) {
            rtnResult = stockPeriodDealTemplateRecService.execute(templateSourceFlgDTO,
                acctEntity);
            paramMap.put("busiType", DictCodeList.业务类型_股票交易.getValue());
            ps.add(DictCodeList.交易方向_现金分红.getValue());
            ps.add(DictCodeListExt4.交易方向_红利补税.getValue());
            ps.add(DictCodeListExt4.交易方向_零碎股转现金.getValue());
            paramMap.put("pslist", ps);//删除除这些交易方向的数据

            /**add by wujie on 20210506 start**/
            paramMap.put(InterfCommonConstant.读取文件状态, rtnResult.getParamMap()==null?null:rtnResult.getParamMap().get(InterfCommonConstant.读取文件状态));
            /**add by wujie on 20210506 end**/
        }
        else{
        	rtnResult.setDataNum(0);
        }
        rtnResult.setParamMap(paramMap);
        StockDealInfoProcessorResult result = new StockDealInfoProcessorResult(rtnResult);
        return result;
    }

}

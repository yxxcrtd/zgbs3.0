/*
 * 文件名：NPrdBaseinfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangwei1
 * 修改时间：2018年6月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.baseinfo.hqinfo;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.MstStockLiquidDiscountInfo;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.dto.HqInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValStockLiquidDiscountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行情信息-股票流动性折扣信息
 *
 * @author weipeng
 * @version 1.0 2022-04-28
 * @see ValStockLiquidDiscountInfoRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ValStockLiquidDiscountInfoRecService {

    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public HqInfoForInterfRecCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                  TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        HqInfoForInterfRecCommonResult result = new HqInfoForInterfRecCommonResult();
        List<ValStockLiquidDiscountInfo> hqStockInfo = new ArrayList<ValStockLiquidDiscountInfo>();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 此部分处理待以后从画面入口的时候进行acctEntity的文件路径的重置，根据templateSourceFlgDTO中的路径进行重新设置。
        // 1、文件读取解析 进行数据有效性的check,并生出进入日志表的数据结构
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        hqStockInfo = returnResult.getT(ValStockLiquidDiscountInfo.class);
        // 2、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        List<MstStockLiquidDiscountInfo> discountInfos = (List<MstStockLiquidDiscountInfo>) handleListToVaLInfo(hqStockInfo).get("valDiscountInfoList");
        result.setMstStockLiquidDiscountInfoList(discountInfos);
        // 添加日志
        result.setListValInterflog(returnResult.getValInterflog());
        return result;
    }

    /**
     * 转换为股票行情表数据
     * @param infoList
     * @return
     */
    private Map<String, Object> handleListToVaLInfo(List<ValStockLiquidDiscountInfo> infoList) {
        Map<String, Object> resultMap = new HashMap<>();
        List<MstStockLiquidDiscountInfo> valDiscountInfoList = new ArrayList<MstStockLiquidDiscountInfo>();
        for (ValStockLiquidDiscountInfo dto : infoList) {
            // 债券市场行情 处理
            MstStockLiquidDiscountInfo discountInfo = getDiscountInfo(dto);
            valDiscountInfoList.add(discountInfo);
        }
        resultMap.put("valDiscountInfoList", valDiscountInfoList);
        return resultMap;
    }

    /**
     * 转换为股票行情表数据
     * @param dto
     * @return
     */
    private MstStockLiquidDiscountInfo getDiscountInfo(ValStockLiquidDiscountInfo dto) {
        MstStockLiquidDiscountInfo discountInfo = new MstStockLiquidDiscountInfo();
        /**股票代码:市场代码**/
        discountInfo.setSharesId(dto.getSharesId());
        /**行情日期**/
        discountInfo.setValDate(dto.getValDate());
        /**预期上市流通日**/
        discountInfo.setExpCircDate(dto.getExpCircDate());
        // 剩余年限
        discountInfo.setTerms(dto.getTerms());
        /**标的股票最后交易日**/
        discountInfo.setLastTradeDate(dto.getLastTradeDate());
        /**参照标的**/
        discountInfo.setReferObject(dto.getReferObject());
        /**预期波动率%**/
        discountInfo.setExpVolatility(dto.getExpVolatility());
        /**标的股票公允价值**/
        discountInfo.setObjFairValue(dto.getObjFairValue());
        /**流动性折扣系数%**/
        discountInfo.setLiquidDiscFactor(dto.getLiquidDiscFactor());
        /**限售股估值（元）**/
        discountInfo.setLimitValue(dto.getLimitValue());
        /**数据来源**/
        if(StringUtil.isNotEmpty(dto.getDataSource())){
            discountInfo.setDataSource(dto.getDataSource());
        }else {
            discountInfo.setDataSource(DictCodeListExt1.生成方式_接口接入.getValue());
        }
        return discountInfo;
    }

}

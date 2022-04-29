/*
 * 文件名：NPrdBaseInfoComForInterfService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改时间：2018年6月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.interf.valuation.recv.informationsys.hqinfo.service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.HqInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.hqinfo.*;
import com.joyintech.tams.valuation.interf.valuation.recv.informationsys.hqinfo.dto.HqDetailInfoProcessorResult;
import com.joyintech.tams.valuation.interf.valuation.transdata.dao.ProtocolMapper;
import com.joyintech.tams.valuation.interf.valuation.transdata.entity.ValRecvServerInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
/**
  * 行情信息接口Service
 * @author wangwei1
 * @version 1.0 2018年6月28日
 * @see HqDetailInfoInfoRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class HqDetailInfoInfoRecService {
    
    /**
     * 基础信息 模板处理Service
     */
	@Autowired
    private StandardFondMarketValService standardFondMarketValService;
	@Autowired
    private StandardMarketBondPriceValService standardMarketBondPriceValService;
	@Autowired
    private StandardRateResetService standardRateResetService;
    /**add by chendijun on 20181122 title 债券市场行情**/
    @Autowired
    private StandardBondMarketInfoService standardBondMarketInfoService;
    /**add by chendijun on 20181122 title 债券市场行情**/
    @Autowired
    private HqBondMktInfoTemplateRecService bondMktRecService;
    @Autowired
    private HqFundMktInfoTemplateRecService fundMktRecService;
    @Autowired
    private HqParFundInfoTemplateRecService hqParFundInfoTemplateRecService;
    @Autowired
	private HqIntRateInfoTemplateRecService hqIntRateInfoTemplateRecService;
    @Autowired
    private HqBondValInfoTemplateRecService bondValService;
    /** add by dongjun on 20181011 title:债券行权计划 start**/
    @Autowired
    private HqPayhsrcsecoptionTemplateRecService hqPayhsrcsecoptionTemplateRecService;
    /** add by dongjun on 20181011 title:债券行权计划 end**/
    @Autowired
    private ProtocolMapper ProtocolMapper;//modify by dongjun on 20181031
    /** add by wangqidong on 20181101 title 委外行情 start**/
    @Autowired
    private OutsideCommitteeQuotationTemplateRecService outsideCommitteeQuotationTemplateRecService;
    /** add by wangqidong on 20181101 title 委外行情 end**/

    /** add by yanya on 20210831 增加股票行情接口 start**/
    @Autowired
    private ValStockQuotationRecService valStockQuotationRecService;
    /** add by yanya on 20210831 增加股票行情接口 end**/

    /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情信息接口 start**/
    @Autowired
    private ValStockLiquidDiscountInfoRecService valStockLiquidDiscountInfoRecService;
    /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情信息接口 end**/

    /**
     * <一句话功能简述>
     * <功能 详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public HqDetailInfoProcessorResult execute(TemplateOrderInfoReaderResult acctEntity) {
             
        //定义返回变量
        HqInfoForInterfRecCommonResult rtnResult = new HqInfoForInterfRecCommonResult();
        
        //设置启动源属性
        TemplateSourceFlgDTO templateSourceFlgDTO = new TemplateSourceFlgDTO();
        templateSourceFlgDTO.setCallSourceType(DictCodeInterfList.文件解析功能启动源_TASK.getValue());
       
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("TMP_E_NAME", acctEntity.getDtoName());
        
        /** modify by dongjun on 20181031 start **/
        if(StringUtil.equals(acctEntity.getTmpType(), DictCodeInterfList.接口文件类型_STTXT.getValue())){  
            Map<String,Object> param = new HashMap<>();
            param.put("SYSTEMID", acctEntity.getSystemid());
            param.put("rsStatus", "R");
            List<ValRecvServerInfoEntity> serverInfoList = ProtocolMapper.getValServerInfo(param);
            if(serverInfoList != null && !serverInfoList.isEmpty()){
                acctEntity.setFilePath(serverInfoList.get(0).getLocalPath() + File.separator +
                    serverInfoList.get(0).getSystemID() + File.separator + DateUtil.format(
                        acctEntity.getResovleDate(), DateUtil.DEFAULT_OTHER_FORMAT));  
            }
        }
        /** modify by dongjun on 20181031 end **/
        //数据模板
        if (TempConstant.债券市场行情.equals(acctEntity.getDtoName())) {
            /**add by chendijun 20181123 start**/
            if(TempConstant.咨询债券市场行情.equals(acctEntity.getTmpEName())) {
                rtnResult = standardBondMarketInfoService.execute(templateSourceFlgDTO, acctEntity);
            } else {                
                rtnResult = bondMktRecService.execute(templateSourceFlgDTO, acctEntity);
            }
            /**add by chendijun 20181123 end**/
        } else if (TempConstant.基金市场行情.equals(acctEntity.getDtoName())) {
            rtnResult = fundMktRecService.execute(templateSourceFlgDTO, acctEntity);
        } else if (TempConstant.场外金融产品估值行情.equals(acctEntity.getDtoName())) {
            if(TempConstant.基金市场估值.equals(acctEntity.getTmpEName())){
                rtnResult = standardFondMarketValService.execute(templateSourceFlgDTO, acctEntity);
            } else {
                rtnResult = hqParFundInfoTemplateRecService.execute(templateSourceFlgDTO, acctEntity);
            }
        } else if (TempConstant.利率行情.equals(acctEntity.getDtoName())) {
            if(TempConstant.基础利率重置.equals(acctEntity.getTmpEName())){
                rtnResult = standardRateResetService.execute(templateSourceFlgDTO, acctEntity);
            } else {
                rtnResult = hqIntRateInfoTemplateRecService.execute(templateSourceFlgDTO, acctEntity);                
            }
        } else if (TempConstant.债券估值行情.equals(acctEntity.getDtoName())) {
            if(TempConstant.债券市价估值.equals(acctEntity.getTmpEName())){
                rtnResult = standardMarketBondPriceValService.execute(templateSourceFlgDTO, acctEntity);
            } else {
                rtnResult = bondValService.execute(templateSourceFlgDTO, acctEntity);
            }            
        }
        /** add by dongjun on 20181011 title:债券行权计划 start**/
        else if (TempConstant.债券行权计划行情.equals(acctEntity.getDtoName())){
            rtnResult = hqPayhsrcsecoptionTemplateRecService.execute(templateSourceFlgDTO, acctEntity);
        }
        /** add by dongjun on 20181011 title:债券行权计划 end**/
        /** add by yanya on 20210831 增加股票行情接口 start**/
        else if (TempConstant.股票行情.equals(acctEntity.getDtoName())){
            rtnResult = valStockQuotationRecService.execute(templateSourceFlgDTO, acctEntity);
        }
        /** add by yanya on 20210831 增加股票行情接口 end**/
        else if(TempConstant.委外行情.equals(acctEntity.getTmpEName())){            
            rtnResult = outsideCommitteeQuotationTemplateRecService.execute(templateSourceFlgDTO,acctEntity);
        }
        /** add by wangqidong on 20181101 title 委外行情 end**/

        /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情信息接口 start**/
        else if (TempConstant.股票流动性折扣信息.equals(acctEntity.getDtoName())){
            rtnResult = valStockLiquidDiscountInfoRecService.execute(templateSourceFlgDTO, acctEntity);
        }
        else if (TempConstant.股票指数行情信息.equals(acctEntity.getDtoName())){
            rtnResult = valStockQuotationRecService.execute(templateSourceFlgDTO, acctEntity);
        }
        /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情信息接口 end**/
        rtnResult.setParamMap(paramMap);
        return new HqDetailInfoProcessorResult(rtnResult);
    }
    
}

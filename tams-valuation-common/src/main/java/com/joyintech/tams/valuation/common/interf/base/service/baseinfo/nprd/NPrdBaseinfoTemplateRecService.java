/*
 * 文件名：NPrdBaseinfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：qiangchengxi
 * 修改时间：2018年6月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.baseinfo.nprd;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdInfoN;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 净值型产品基础信息模板处理 (VAL_PRD_BASEINFO_INFO_N)
 * 
 * @author 吴德宝
 * @version 1.0 2018年6月22日
 * @see NPrdBaseinfoTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class NPrdBaseinfoTemplateRecService {

    /**
     * 净值型产品基本信息模板处理service <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    
    @Autowired
    private InterfCommonService interfCommonService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public NPrdBaseInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                           TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        NPrdBaseInfoValCommonResult result = new NPrdBaseInfoValCommonResult();
        List<ValPrdInfoN> listValPrdInfoN = new ArrayList<ValPrdInfoN>();

        //解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult= templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValPrdInfoN = returnResult.getT(ValPrdInfoN.class);

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValPrdInfoN(listValPrdInfoN);
        // 判断返回结果不为空
        if (map != null) {
        	//标的类金融产品表
            List<FinProduct> listValFinProduct = (List<FinProduct>)map.get("ValFinProduct");
            //标的类金融产品扩展表
            List<FinProductAdd> listValFinProductExt = (List<FinProductAdd>)map.get("ValFinProductExt");
            //账套信息初始化
            List<BooksetInitResult> booksetInitResultList = (List<BooksetInitResult>)map.get("booksetInitResultList");
            //现金流信息表
            List<FinCashInfo> finCashInfoList = (List<FinCashInfo>)map.get("FinCashInfo");
            //账务明细代码表
            List<BokDetailId> bokDetailIdList = (List<BokDetailId>)map.get("BokDetailId");
            //税务方案
            List<BokBooksetPlan> bokSetPlanList = (List<BokBooksetPlan>)map.get("BokBooksetPlan");

            //核算主体
            List<BokAcctSubjectInfo> listAcctSubjectInfo = (List<BokAcctSubjectInfo>)map.get("listAcctSubjectInfo");
            //核算分层
            List<BokAcctLayeringInfo> listAcctLayeringInfo = (List<BokAcctLayeringInfo>)map.get("listAcctLayeringInfo");
            result.setListAcctSubjectInfo(listAcctSubjectInfo);
            result.setListAcctLayeringInfo(listAcctLayeringInfo);

            // 现金流计算规则
            List<FinCashCalcRule> listValCashCalcRule = (List<FinCashCalcRule>)map.get("listValCashCalcRule");//add by dongjun on 20181019 start
            result.setListValCashCalcRule(listValCashCalcRule);
            // 交易金融商品
            List<FinTradeProduct> listFinTradeProduct = (List<FinTradeProduct>)map.get("listFinTradeProduct");
            result.setListFinTradeProduct(listFinTradeProduct);
            
            result.setListValFinProduct(listValFinProduct);
            result.setListValFinProductExt(listValFinProductExt);
            result.setBooksetInitResultList(booksetInitResultList);
            
            result.setListValCashInfo(finCashInfoList);
            result.setListValBokDetailId(bokDetailIdList);
            result.setListBokBooksetPlan(bokSetPlanList);
            //几许传数 add by chufengjie 20190522
            result.setListValPrdInfoN(listValPrdInfoN);
        }
        // 添加日志
        result.setValInterflog(returnResult.getValInterflog());
        return result;
    }

    /**
     * 01-基础信息
     * 
     * @param listValPrdInfoN 净值型产品基础信息表
     * @return 标的类金融产品 + 标的类金融产品扩展信息
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> handleValPrdInfoN(List<ValPrdInfoN> listValPrdInfoN) {
        Map<String, Object> resultMap = new HashMap<>();
        List<FinProduct> valFinProductList = new ArrayList<FinProduct>();
        List<ValFinProductExt> valFinProductExtList = new ArrayList<ValFinProductExt>();
        List<FinProductAdd> valFinProductAddList = new ArrayList<FinProductAdd>();
        List<BooksetInitResult> booksetInitResultList = new ArrayList<BooksetInitResult>();
        List<FinCashInfo> finCashInfoList = new ArrayList<FinCashInfo>();
        List<BokDetailId> bokDetailIdList = new ArrayList<BokDetailId>();
        List<BokBooksetPlan> bokSetPlanList = new ArrayList<BokBooksetPlan>();
        //分层产品集合
        List<LayerPrdInfo> listLayerPrdInfo = new ArrayList<>();
        //核算主体集合
        List<BokAcctSubjectInfo> listAcctSubjectInfo = new ArrayList<BokAcctSubjectInfo>();        
        //核算分层集合
        List<BokAcctLayeringInfo> listAcctLayeringInfo = new ArrayList<BokAcctLayeringInfo>();
        //本次同步数据生成的账套信息
        List<BokBookset> listBokBookset = new ArrayList<BokBookset>();
        // 现金流计算规则
        List<FinCashCalcRule> listValCashCalcRule = new ArrayList<FinCashCalcRule>();//add by wanggengyun on 20190118
        // 交易类金融产品
        List<FinTradeProduct> listFinTradeProduct = new ArrayList<FinTradeProduct>();//add by wanggengyun on 20190118
        //分层核算时，初始化账套信息时生成的核算分层信息
        List<BokAcctLayeringInfo> acctalyeringInfos = new ArrayList<BokAcctLayeringInfo>();
        //已有产品信息查询
        Map<String,LayerPrdInfo> mapLayerPrdInfo = interfCommonService.getExistPrdInfo();
        //待处理计税产品信息
        List<String> taxInitPrdList = new ArrayList<>();
        // 获取基础信息Dto
        for (ValPrdInfoN dto : listValPrdInfoN) {
            //挂钩母产品代码与产品代码值相同时，挂钩母产品代码设置空
            if(!DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getFinprodType())){
                if(dto.getProdId().equals(dto.getPmotherPrd())){
                    dto.setPmotherPrd(null);
                }
            }
            // 标的类金融产品 处理
            FinProduct valFinProduct = getValFinProduct(dto);

            //标的类金融产品扩展表
            ValFinProductExt valFinProductExt = getValFinProductExt(dto);
            FinProductAdd finProductAdd = getFinProductAdd(dto);
            valFinProductAddList.add(finProductAdd);
            // 现金流计算规则初始化
            FinCashCalcRule FinCashCalcRule = getFinCashCalcRule(dto);
            if(FinCashCalcRule != null){
                listValCashCalcRule.add(FinCashCalcRule);
            } 
            if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getFinprodType())){
               // 交易金融商品初始化
                listFinTradeProduct.add(getFinTradeProduct(dto));
            } 
            else if(DictCodeListExt1.金融产品类型_理财产品.getValue().equals(dto.getFinprodType())){
                valFinProductList.add(valFinProduct);
                valFinProductExtList.add(valFinProductExt);
            }
            //变量.分层产品Dto初始化
            LayerPrdInfo layerPrdInfo = new LayerPrdInfo();
            //变量.分层产品Dto.金融产品代码 = L.产品代码
            String finprodId = dto.getProdId();
            if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getFinprodType())){
                //分支序号 
                finprodId = finprodId+Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getVdate()));
            }
            layerPrdInfo.setFinprodId(finprodId);
            
            //变量.分层产品Dto.金融产品名称 = L.产品名称
            layerPrdInfo.setFinprodName(dto.getProdName());
            //变量.分层产品Dto.母金融产品代码 = L.父产品代码
            layerPrdInfo.setpFinprodId(dto.getPmotherPrd());
            //变量.分层产品Dto.金融产品类型 = L.金融产品类型
            layerPrdInfo.setFinprodType(dto.getFinprodType());
            //变量.分层产品Dto.收益类型 = 数据字典：收益类型.预期收益
            layerPrdInfo.setProfitType(DictCodeList.收益类型_净值型.getValue());
            //变量.分层产品Dto.起息日 = L.起息日
            layerPrdInfo.setVdate(dto.getVdate());
            //变量.分层产品Dto.到期日 = L.到期日
            layerPrdInfo.setMdate(dto.getMdate());
            //变量.分层产品Dto.实际到期日 = L.实际到期日
            layerPrdInfo.setActmdate(dto.getActmdate());
            //变量.分层产品Dto.投资组合代码 = L.投资组合代码
            layerPrdInfo.setPortfolioId(dto.getPortfolioId());
            //变量.分层产品Dto.交易日历 = L.交易日历
            layerPrdInfo.setCalendarId(valFinProduct.getCalendarId());
            
            //变量.分层代码 = L.产品代码+L.金融产品类型
            String layeringId  = finprodId+FinalString.LAYERING_SPLIT+dto.getFinprodType();
            layerPrdInfo.setLayeringId(layeringId);
            mapLayerPrdInfo.put(layeringId, layerPrdInfo);
            
            if(!DictCodeListExt1.金融产品类型_投资组合.getValue().equals(dto.getFinprodType())){
                listLayerPrdInfo.add(layerPrdInfo);
            }
           
            // 生成账套、账套日期表、账套扩展表、账套科目 
            //IF 接口.是否核算 eq 数据字典：是否.是
            if(DictCodeList.是否_是.getValue().equals(dto.getIsAccount())){
                OtherParamsDto otherParamsDto = new OtherParamsDto();
                otherParamsDto.setValuationTemplate(dto.getValuationTemplate());
                //新增估值方案=dto.产品估值方案
                otherParamsDto.setPrdValSche(dto.getPrdValSche());
                Map<String,String> sysParas = new HashMap<>();

                if(!StringUtil.isEmpty(dto.getShareDecDigits())){
                    //份额小数位=
                    sysParas.put(ValDictCodeList.账套配置表参数项_份额小数位位数.getValue(), dto.getShareDecDigits());
                }
                if(!StringUtil.isEmpty(dto.getShareDecType())){
                    //份额尾数处理
                    sysParas.put(ValDictCodeList.账套配置表参数项_份额小数位尾数处理.getValue(), dto.getShareDecType());
                }
                if(!StringUtil.isEmpty(dto.getValDecDigits())){
                    //单位净值小数位
                    sysParas.put(ValDictCodeList.账套配置表参数项_估值小数位位数.getValue(), dto.getValDecDigits());
                    /**ADD BY WUJIE ON 20210421 START REMARK:累计单位净值小数位数_估值表展示 临时处理，待后续优化**/
                    sysParas.put(ValDictCodeList.账套配置表参数项_累计单位净值小数位数_估值表展示.getValue(), dto.getValDecDigits());
                    /**ADD BY WUJIE ON 20210421 END REMARK:累计单位净值小数位数_估值表展示 临时处理，待后续优化**/
                }
                if(!StringUtil.isEmpty(dto.getValDecType())){
                    //单位净值尾数处理
                    sysParas.put(ValDictCodeList.账套配置表参数项_估值小数位处理方式.getValue(), dto.getValDecType());
                    /**ADD BY WUJIE ON 20210421 START REMARK:累计单位净值小数尾数_估值表展示 临时处理，待后续优化**/
                    sysParas.put(ValDictCodeList.账套配置表参数项_累计单位净值小数尾数_估值表展示.getValue(), dto.getValDecType());
                    /**ADD BY WUJIE ON 20210421 END REMARK:累计单位净值小数尾数_估值表展示 临时处理，待后续优化**/
                }

                if(!StringUtil.isEmpty(dto.getFloatCyclyearYieldDigits())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率小数位数.getValue(), dto.getFloatCyclyearYieldDigits());
                }
                if(!StringUtil.isEmpty(dto.getFloatCyclyearYieldType())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率位数处理方式.getValue(), dto.getFloatCyclyearYieldType());
                }
                if(!StringUtil.isEmpty(dto.getFloatEndtotalNetDigits())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值小数位数.getValue(), dto.getFloatEndtotalNetDigits());
                }
                if(!StringUtil.isEmpty(dto.getFloatEndtotalNetType())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值尾数处理方式.getValue(), dto.getFloatEndtotalNetType());
                }
                if(!StringUtil.isEmpty(dto.getFloatStarttotalNetDigits())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值小数位数.getValue(), dto.getFloatStarttotalNetDigits());
                }
                if(!StringUtil.isEmpty(dto.getFloatStarttotalNetType())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值尾数处理方式.getValue(), dto.getFloatStarttotalNetType());
                }
                if(!StringUtil.isEmpty(dto.getFloatStarttotalUnitnetDigit())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值小数位数.getValue(), dto.getFloatStarttotalUnitnetDigit());
                }
                if(!StringUtil.isEmpty(dto.getFloatStarttotalUnitnetType())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值尾数处理方式.getValue(), dto.getFloatStarttotalUnitnetType());
                }

                if(!StringUtil.isEmpty(dto.getShowAfterDecType())){
                    /**modify by wujie on 20190704 start
                     sysParas.put(DictCodeList.账套配置表参数项_产品费后估值小数位数_估值表展示.getValue(), dto.getShowAfterDecType());
                     **/
                    sysParas.put(ValDictCodeList.账套配置表参数项_产品费后估值小数尾数处理方式_估值表展示.getValue(), dto.getShowAfterDecType());
                    /**modify by wujie on 20190704 end**/
                }
                if(!StringUtil.isEmpty(dto.getShowAfterDecDigits())){
                    /**modify by wujie on 20190704 start
                     sysParas.put(DictCodeList.账套配置表参数项_产品费后估值小数尾数处理方式_估值表展示.getValue(), dto.getShowAfterDecDigits());
                     **/
                    sysParas.put(ValDictCodeList.账套配置表参数项_产品费后估值小数位数_估值表展示.getValue(), dto.getShowAfterDecDigits());
                    /**modify by wujie on 20190704 end**/
                }

                if(!StringUtil.isEmpty(dto.getShowBeforeDecDigits())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_产品费前估值小数位数_估值表展示.getValue(), dto.getShowBeforeDecDigits());
                }
                if(!StringUtil.isEmpty(dto.getShowBeforeDecType())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_产品费前估值小数尾数处理方式_估值表展示.getValue(), dto.getShowBeforeDecType());
                }
                if(!StringUtil.isEmpty(dto.getShowCyclyearYieldDigits())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_周期年化收益率小数位数_估值表展示.getValue(), dto.getShowCyclyearYieldDigits());
                }
                if(!StringUtil.isEmpty(dto.getShowCyclyearYieldType())){
                    sysParas.put(ValDictCodeList.账套配置表参数项_周期年化收益率小数尾数处理方式_估值表展示.getValue(), dto.getShowCyclyearYieldType());
                }                otherParamsDto.setSysParams(sysParas);
                BooksetInitResult booksetInit = interfCommonService.getParStrucProduct(valFinProduct,valFinProductExt,otherParamsDto);
                booksetInit.getValBokBookset().getBooksetIdTmpl();
                booksetInit.getValBokBookset().getStartDate();
                booksetInitResultList.add(booksetInit);
                //共通-分层结构产品账套信息初始化.核算主体代码
                //共通-分层结构产品账套信息初始化.账套信息
                if(booksetInit.getValBokBookset()!=null){
                    listBokBookset.add(booksetInit.getValBokBookset());
                }
                
                listAcctSubjectInfo.add(booksetInit.getAcctSubjectInfo());
                
                //共通-分层结构产品账套信息初始化.账务明细代码
                bokDetailIdList.add(booksetInit.getBokDetailId());
                
                //共通-分层结构产品账套信息初始化.核算分层信息
                acctalyeringInfos.add(booksetInit.getAcctLayeringInfo());
                if(booksetInit.getBokBooksetPlan()!=null){
                    bokSetPlanList.add(booksetInit.getBokBooksetPlan());
                }
            }
            /**待处理计税产品**/
            if (DictCodeList.是否_是.getValue().equals(dto.getIsTax())) {
                String taxProdId = dto.getProdId();
                if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getFinprodType())){
                    taxProdId = dto.getProdId() + new SimpleDateFormat("yyyyMMdd").format(dto.getVdate());
                } 
                taxProdId = taxProdId+FinalString.LAYERING_SPLIT+dto.getFinprodType();
                taxInitPrdList.add(taxProdId) ;
            }
        }
        //初始化分层信息
        if(CollectionUtils.isNotEmpty(listLayerPrdInfo)){
            //共通-分层结构子产品分层信息初始化SHEET
            Map<String,Object>  mapStrucSubProInit= interfCommonService.getStrucSubProInit(listLayerPrdInfo, 
                    mapLayerPrdInfo, listBokBookset,acctalyeringInfos);
            //共通-分层结构子产品分层信息初始化SHEET.核算分层代码
            List<BokAcctLayeringInfo> acctLayeringInfoList = (List<BokAcctLayeringInfo>)mapStrucSubProInit.get("ACCT_LAYERING_INFO");
            //共通-分层结构子产品分层信息初始化SHEET.账务明细代码
            List<BokDetailId> BokDetailIdList = (List<BokDetailId>)mapStrucSubProInit.get("BOK_DETAIL_ID");
            if(CollectionUtils.isNotEmpty(acctLayeringInfoList)){
                listAcctLayeringInfo.addAll(acctLayeringInfoList);
            }            
            if(CollectionUtils.isNotEmpty(BokDetailIdList)){
                bokDetailIdList.addAll(BokDetailIdList);
            }
        }
        else{
            if(CollectionUtils.isNotEmpty(acctalyeringInfos)){
                listAcctLayeringInfo.addAll(interfCommonService.initBokAcctLayeringInfo(acctalyeringInfos));
            } 
        }
        if(CollectionUtils.isNotEmpty(listAcctLayeringInfo)){
            //待处理核算分层信息
           List<BokAcctLayeringInfo> taxBokAcctLayeringInfo = new ArrayList<>();
            for(BokAcctLayeringInfo info : listAcctLayeringInfo){
                if(taxInitPrdList.contains(info.getLayeringId())){
                    taxBokAcctLayeringInfo.add(info);
                }
            }
            //调用共通 分层产品增值税初始化    
            Map<String,Object>  map = interfCommonService.initValueTax(taxBokAcctLayeringInfo);
            //现金流信息表
            if (map.get("FIN_CASH_INFO") != null) {
                finCashInfoList.addAll((List<FinCashInfo>)map.get("FIN_CASH_INFO"));
            }
            //账务明细代码表
            if (map.get("BOK_DETAIL_ID_LIST") != null) {
                bokDetailIdList.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID_LIST"));
            }
            //账套估值方案
            if (map.get("BOK_BOOKSET_PLAN") != null) {
                bokSetPlanList.addAll((List<BokBooksetPlan>)map.get("BOK_BOOKSET_PLAN"));
            }
        }
        resultMap.put("ValFinProduct", valFinProductList);
        resultMap.put("ValFinProductExt", valFinProductAddList);
        resultMap.put("booksetInitResultList", booksetInitResultList);
        
        resultMap.put("FinCashInfo", finCashInfoList);
        resultMap.put("BokDetailId", bokDetailIdList);
        resultMap.put("BokBooksetPlan", bokSetPlanList);
        //核算分层信息
        resultMap.put("listAcctLayeringInfo", listAcctLayeringInfo);
        //核算主体信息
        resultMap.put("listAcctSubjectInfo", listAcctSubjectInfo);
        // 现金流计算规则
        resultMap.put("listValCashCalcRule", listValCashCalcRule);
        // 交易金融商品
        resultMap.put("listFinTradeProduct", listFinTradeProduct);
        return resultMap;
    }

    /**
     * 标的类金融产品表 接收
     * 
     * @param dto 01-基础信息
     * @return 标的类金融产品
     * @see
     * @since 1.0
     */
    private FinProduct getValFinProduct(ValPrdInfoN dto) {

        FinProduct valFinProduct = new FinProduct();
        // 金融产品代码 = A.产品代码
        valFinProduct.setFinprodId(dto.getProdId());
        // 金融产品类型 = 固定值(F16：理财产品)
        valFinProduct.setFinprodType(dto.getFinprodType());
        // 金融产品简称= A.产品简称
        valFinProduct.setFinprodAbbr(dto.getProdAbbr());
        // 金融产品全称= A.产品名称
        valFinProduct.setFinprodName(dto.getProdName());
        // 收益类型= 01：净值
        valFinProduct.setProfitType(DictCodeList.收益类型_净值型.getValue());
        // 币种= A.认购币种
        valFinProduct.setCcy(dto.getCcy());
        // 交易日历= 
     	valFinProduct.setCalendarId(interfCommonService.getCalendarId(dto.getTradeMarket()));
     	// 交易场所 = A.交易场所
     	valFinProduct.setTradeMarket(dto.getTradeMarket());
        // 市场代码=  A.市场代码
        valFinProduct.setFinprodMarketId(dto.getFinprodMarketId());
        // 发行方式 = A.发行方式
//        valFinProduct.setIssueType(dto.getIssueType());
        // tis传的是开放类型，接收的数据字典为发行方式，增加数据字典
        if(DictCodeList.开放类型_开放式.getValue().equals(dto.getIssueType())){
            valFinProduct.setIssueType(DictCodeList.发行方式_公开发行.getValue());
        }else{
            valFinProduct.setIssueType(DictCodeList.发行方式_非公开发行.getValue());
        }
        // 起息日= A.起息日
        valFinProduct.setVdate(dto.getVdate());
        // 到期日= A.到期日
        valFinProduct.setMdate(dto.getMdate());
        // 实际到期日= A.到期日
        //实际到期日 = A.实际到期日
        valFinProduct.setActmdate(dto.getActmdate()==null?dto.getMdate():dto.getActmdate());
        // 母金融产品代码 = A.产品代码
        valFinProduct.setpFinprodId(dto.getPmotherPrd());
        // 原金融产品代码 = A.产品代码
        valFinProduct.setoFinprodId(dto.getProdId());
        // 原金融产品类型 = F16：理财产品
//        valFinProduct.setoFinprodType(dto.getFinprodType());
        // 创建人 = SYS
        
        // 所属机构 = A.所属机构   add by tanxuan on 20210310 多级法人权限控制改造
		valFinProduct.setBelongInstitutionCode(dto.getBelongInstitutionCode());
        /*add by sunshuaishuai 20210429 增加托管机构 start*/
        valFinProduct.setRegistOrg(dto.getTruInstitut());
        /*add by sunshuaishuai 20210429 增加托管机构 start*/
        return valFinProduct;
    }

    /**
     * 标的类金融产品扩展信息表 接收
     * 
     * @param dto 01-基础信息
     * @return 标的类金融产品扩展信息
     * @see
     * @since 1.0
     */
    private ValFinProductExt getValFinProductExt(ValPrdInfoN dto) {
    	ValFinProductExt finProductExt = new ValFinProductExt();
        
        //金融产品代码 = A.产品代码
        finProductExt.setFinprodId(dto.getProdId());
        // 金融产品类型 = 固定值(F16：理财产品)
        finProductExt.setFinprodType(dto.getFinprodType());
        //托管活期利息计提 = A.托管活期利息计提
        finProductExt.setIsCalc(dto.getIsCalc());
        
        /**add by wujie on 20181126 start remark:扩展表.收益标识设值**/
        // 收益标识 = A.产品收益类型
        finProductExt.setProfitFlag(dto.getPrdIncomeType());
        /**add by wujie on 20181126 end**/
        /**modify by dongjun on 20181016 start**/
        // 投资组合代码 = A.投资组合代码
        finProductExt.setPortfolioId(dto.getPortfolioId());
        // 是否核算 = A.是否核算
        finProductExt.setIsAccount(dto.getIsAccount());
        // 是否分层核算 = A.是否分层核算
        finProductExt.setIsLayerAccount(dto.getIsLayerAccount());
        /**modify by dongjun on 20181016 end**/
        
        /**modify by dongxiangyang on 20190221 start**/
        //是否对账 = A.是否对账
//        finProductExt.setIsReconciliation(dto.getIsReconciliation());
        
        /**modify by dongxiangyang on 20190221 end**/        
        return finProductExt;
    }
    
    
    
    private FinProductAdd getFinProductAdd(ValPrdInfoN dto) {
    	FinProductAdd finProductAdd = new FinProductAdd();
        
        //金融产品代码 = A.产品代码
    	finProductAdd.setFinprodId(dto.getProdId());
        // 金融产品类型 = 固定值(F16：理财产品)
//        finProductExt.setFinprodType(dto.getFinprodType());
        //托管活期利息计提 = A.托管活期利息计提
//        finProductExt.setIsCalc(dto.getIsCalc());
        
        /**add by wujie on 20181126 start remark:扩展表.收益标识设值**/
        // 收益标识 = A.产品收益类型
    	finProductAdd.setProfitFlag(dto.getPrdIncomeType());
        /**add by wujie on 20181126 end**/
        /**modify by dongjun on 20181016 start**/
        // 投资组合代码 = A.投资组合代码
    	finProductAdd.setPortfolioId(dto.getPortfolioId());
        // 是否核算 = A.是否核算
//        finProductExt.setIsAccount(dto.getIsAccount());
        // 是否分层核算 = A.是否分层核算
//        finProductExt.setIsLayerAccount(dto.getIsLayerAccount());
        /**modify by dongjun on 20181016 end**/
        
        /**modify by dongxiangyang on 20190221 start**/
        //是否对账 = A.是否对账
//        finProductExt.setIsReconciliation(dto.getIsReconciliation());
        
        return finProductAdd;
    }
    

    /**
     * 
     * 现金流计算规则初始化
     */
    private FinCashCalcRule getFinCashCalcRule(ValPrdInfoN dto){
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        // 现金流代码   Y   T2  InterfHelper.getMd5CashId_（A.产品代码,A.金融产品类型,数据字典：现金流二级类型.利息)
        /** modify by dongjun on 20181029 start **/
        String cashId = null; 
        if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            cashId = InterfHelper.getMd5CashId_(dto.getProdId() + new SimpleDateFormat("yyyyMMdd").format(dto.getVdate()),
                dto.getFinprodType(),DictCodeListExt1.现金流二级类型_利息.getValue());
        } else {
            cashId = InterfHelper.getMd5CashId_(dto.getProdId(),dto.getFinprodType(),DictCodeListExt1.现金流二级类型_利息.getValue());
        }
        finCashCalcRule.setCashId(cashId);//modify by dongjun on 20181023
        /** modify by dongjun on 20181029 end **/
        // 生效日期    Y   A   A.起息日
        finCashCalcRule.setEffDate(dto.getVdate());
        // 计算类型    N   A   A.利率类型
//        finCashCalcRule.setCalcType(dto.getRateType());
        // 生效利率    N   A   A.预期收益率
        //finCashCalcRule.setYield(dto.getProprate());
        // 计算基础    N   A   A.计息基础
        //finCashCalcRule.setBasis(dto.getBasis());
        // 金融产品代码  Y   A   A.产品代码
        finCashCalcRule.setFinprodId(dto.getProdId());
        // 金融产品类型  Y   固定值 数据字典：金融产品类型.理财产品
        finCashCalcRule.setFinprodType(dto.getFinprodType());//modify by dongjun on 20181025 
        // 分支序号    Y   固定值 设置全局变量(默认为0）
        /** modify by dongjun on 20181029 start **/
        Long branch = null; 
        if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getVdate()));
        } else {
            branch = InterfCommonConstant.分支序号_默认;
        }
        finCashCalcRule.setBranch(branch);
        /** modify by dongjun on 20181029 end **/
        
        return finCashCalcRule;
    }//add by dongjun on 20181019 end
    /**
     *  交易金融商品初始化
     */
    private FinTradeProduct getFinTradeProduct(ValPrdInfoN dto){//add by dongjun on 20181019 start
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        // 金融产品代码  = A.产品代码
        finTradeProduct.setFinprodId(dto.getProdId());
        // 金融产品类型 = A.金融产品类型
        finTradeProduct.setFinprodType(dto.getFinprodType());//modify by dongjun on 20181023
        // 分支序号 = 固定值 设置全局变量(默认为0）
        finTradeProduct.setBranch(Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getVdate())));//modify by dongjun on 20181029
        // 本金币种 = A.认购币种
        finTradeProduct.setCcy(dto.getCcy());
        // 起息日 = A.起息日
        finTradeProduct.setVdate(dto.getVdate());
        // 到期日 = A.到期日
        finTradeProduct.setMdate(dto.getMdate());
        // 期限(天) = A.到期日-A.起息日
        finTradeProduct.setTerm(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()));
        // 期限品种 = A.产品期限
        //finTradeProduct.setTermType(dto.getTerm());
        // 利率类型 = A.利率类型
        //finTradeProduct.setIntType(dto.getRateType());
        // 固定利率 = A.预期收益率
        //finTradeProduct.setIntRate(dto.getProprate());
        // 计息基础 = A.计息基础
        //finTradeProduct.setBasis(dto.getBasis());
        // 原金融产品代码 = A.产品代码
        finTradeProduct.setoFinprodId(dto.getProdId());
        // 原金融产品类型 = A.金融产品类型
//        finTradeProduct.setoFinprodType(dto.getFinprodType());//modify by dongjun on 20181023
        // 交易场所 = A.交易场所
        finTradeProduct.setTradeMarket(dto.getTradeMarket());
        
        /**add by chendijun start**/
        //收益类型 = 固定值:数据字典:收益类型.预期收益
        finTradeProduct.setProfitType(DictCodeList.收益类型_收益型.getValue());
        //交易日历 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->交易日历映射关系SHEET
        finTradeProduct.setCalendarId(interfCommonService.getCalendarId(dto.getTradeMarket()));
        /**add by chendijun end**/
        return finTradeProduct;
    }
}

package com.joyintech.tams.valuation.valapi;

import com.google.common.collect.Lists;
import com.jixu.msg.dto.JJBkCashAcctDto;
import com.jixu.msg.dto.JJPortfolioDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.common.ResultCode;
import com.joyintech.tams.valuation.valapi.annotations.ValInterfApiMethod;
import com.joyintech.tams.valuation.valapi.annotations.ValInterfApiParameter;
import com.joyintech.tams.valuation.valapi.constant.ValApiConstant;
import com.joyintech.tams.valuation.valapi.dto.*;
import com.joyintech.tams.valuation.valapi.service.ValInterfCommonApiService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ValInterfCommonServiceApi
 * @Description: 投管调用估值api，生成估值侧相关数据
 * @Author yanya
 * @Date 2021/2/2
 * @Version V1.0
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ValInterfCommonApi {

    @Autowired
    ValInterfCommonApiService valInterfCommonApiService;
    /**
     * 接口共通
     */
    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * 产品账套信息初始化
     * 用于产品生成账套相关信息，传递的类型为：F16
     * @param dto 产品信息
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject prodOtherInfoInitF16(@ValInterfApiParameter ProdOtherInfoInitF16Dto dto){
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        // 定义 变量.分层代码 = （根据产品代码、产品类型生成分层代码）
        String layeringId =valInterfCommonApiService.generateLayeringId(dto.getFinprodId(),
                dto.getFinprodType(), dto.getVdate());
        // 变量.入库账套Dto = DTO.账套信息
        BokBookset bokBookset = null;
        // 变量.变量.入库账套日期Dto = DTO.账套日期Dto
        BokBooksetDate bokBooksetDate =  null;
        // 定义 变量.入库账套配置List
        List<BokBooksetExt> listBokBooksetExt = Lists.newArrayList();
        // 变量.入库核算主体信息Dto = DTO.核算主体信息Dto
        BokAcctSubjectInfo bokAcctSubjectInfo = null;
        // 定义 变量.入库账务明细代码List
        List<BokDetailId> listBokDetailId = Lists.newArrayList();
        // 定义 变量.核算分层信息List
        List<BokAcctLayeringInfo> listBokAcctLayeringInfo = Lists.newArrayList();
        // 定义 变量.入库估值方案List
        List<BokBooksetPlan> listBokBooksetPlan = Lists.newArrayList();
        //定义 变量.几许产品信息
        JJPortfolioDto jJPortfolio = null;
        if (DictCodeList.是否_是.getValue().equals(dto.getIsAccount())) {
            // 定义 变量.标的类金融产品信息Dto FinProduct
            FinProduct finProduct = valInterfCommonApiService.buildFinProduct(dto.getFinprodId(),dto.getFinprodType(),
                    dto.getFinprodAbbr(),dto.getFinprodName(),dto.getProfitType(),dto.getVdate(),dto.getMdate(),
                    dto.getCcy(),dto.getCalendarId(),dto.getActmdate());
            //定义 变量.标的类金融产品扩展信息表Dto ValFinProductExt
            //定义 变量.托管活期利息计提 =
            String tgIsCalc = StringUtil.isEmpty(dto.getDepositAccout()) ? DictCodeList.是否_否.getValue() : dto.getDepositAccout();
            //构建估值侧标的类金融产品扩展信息表
            ValFinProductExt valFinProductExt = valInterfCommonApiService.buildValFinProductExt(tgIsCalc,
                    dto.getIsLayerAccount(),dto.getPortfolioId(),dto.getIsAccount());
            // 变量.其他参数值Dto 赋值
            OtherParamsDto otherParamsDto = valInterfCommonApiService.buildOtherParamsDto(dto.getValuationTemplate(),
                    StringUtils.EMPTY, new HashMap<String,String>());
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息初始化_分层产品SHEET
            BooksetInitResult booksetInitResult = interfCommonService.getParStrucProduct(finProduct, valFinProductExt, otherParamsDto);

            // 变量.入库账套Dto = DTO.账套信息
            bokBookset = booksetInitResult.getValBokBookset();
            // 变量.变量.入库账套日期Dto = DTO.账套日期Dto
            bokBooksetDate = booksetInitResult.getValBokBooksetDate();
            // 变量.入库账套配置List.addAll(DTO.账套配置List)
            listBokBooksetExt.addAll(booksetInitResult.getListValBokBooksetExt());
            // 变量.入库核算主体信息Dto = DTO.核算主体信息Dto
            bokAcctSubjectInfo = booksetInitResult.getAcctSubjectInfo();
            // 变量.入库账务明细代码List.addAll(DTO.账务明细代码List）
            listBokDetailId.add(booksetInitResult.getBokDetailId());
            // 变量.核算分层信息List.add(DTO.核算分层信息DTO)
            listBokAcctLayeringInfo.add(booksetInitResult.getAcctLayeringInfo());
            listBokBooksetPlan.add(booksetInitResult.getBokBooksetPlan());
            //定义 变量.几许产品信息
            jJPortfolio = valInterfCommonApiService.buildJJPortfolioDto(bokBookset.getBooksetId(), bokBookset.getStandardCcy(),
                    bokBookset.getBooksetIdTmpl(), bokBookset.getStartDate(), bokBookset.getBooksetFullname(),
                    bokBookset.getBooksetName(), dto.getCalendarId());
        }
        // 2.3 初始化核算分层信息
        // 定义 变量.本次新增分层产品List
        List<LayerPrdInfo> listLayerPrdInfo = Lists.newArrayList();
        // 定义 变量.本次新增账套信息List
        List<BokBookset> listBokBookset = Lists.newArrayList();
        // IF 变量.入库账套Dto IS NOT NULL and 变量.入库账套Dto.账套代码 IS NOT NULL
        if (bokBookset != null && StringUtils.isNotEmpty(bokBookset.getBooksetId())) {
            // 变量.本次新增账套信息List.add(变量.入库账套Dto)
            listBokBookset.add(bokBookset);
        }
        // 定义 变量.已有分层产品Map
        Map<String, LayerPrdInfo> mapLayerPrdInfo = interfCommonService.getExistPrdInfo();
        // 定义 变量.金融产品代码 = 输入.产品标的Dto.金融产品代码
        String finProductId = dto.getFinprodId();
        // 定义 变量.分层产品Dto
        LayerPrdInfo layerPrdInfo = valInterfCommonApiService.buildLayerPrdInfo(finProductId,
                dto.getFinprodType(), dto.getFinprodName(), dto.getpFinprodId(), dto.getProfitType(),
                dto.getVdate(), dto.getMdate(), dto.getPortfolioId(), layeringId, dto.getCalendarId());
        // 变量.已有分层产品Map.put(变量.分层产品Dto.分层代码,变量.分层产品Dto)
        mapLayerPrdInfo.put(layerPrdInfo.getLayeringId(), layerPrdInfo);
        // 变量.本次新增分层产品List.add(变量.分层产品Dto)
        listLayerPrdInfo.add(layerPrdInfo);
        // 定义 变量.入库核算分层信息List
        List<BokAcctLayeringInfo> listEnterBokAcctLayeringInfo = Lists.newArrayList();
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->核算分层信息初始化_分层产品SHEET
        Map<String, Object> map = interfCommonService.getStrucSubProInit(listLayerPrdInfo, mapLayerPrdInfo, listBokBookset,
                listBokAcctLayeringInfo);
        // IF DTO.get("ACCT_LAYERING_INFO") is not null
        if (map.get("ACCT_LAYERING_INFO") != null) {
            // 变量.入库核算分层信息List.addAll(DTO.get("ACCT_LAYERING_INFO"))
            listEnterBokAcctLayeringInfo.addAll((List<BokAcctLayeringInfo>)map.get("ACCT_LAYERING_INFO"));
        }
        // IF DTO.get("BOK_DETAIL_ID") is not null
        if (map.get("BOK_DETAIL_ID") != null) {
            // 变量.入库账务明细代码List.addAll(DTO.get("BOK_DETAIL_ID"))
            listBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
        }
        // 2.4 增值税信息初始化
        // 定义 变量.入库现金流信息List
        List<FinCashInfo> listFinCashInfo = Lists.newArrayList();
        // IF 数据字典：是否.是 eq 输入.产品标的Dto.是否计税 OR 变量.入库核算分层信息List is not null
        if (DictCodeList.是否_是.getValue().equals(dto.getIsTax()) && CollectionUtils.isNotEmpty(listEnterBokAcctLayeringInfo)) {
            // 定义 变量.计税产品List
            List<BokAcctLayeringInfo> listTaxProduct = Lists.newArrayList();
            // LOOP 变量.入库核算分层信息List L
            for (BokAcctLayeringInfo item : listEnterBokAcctLayeringInfo) {
                // IF L.分层代码 eq 变量.分层代码
                if (StringUtils.equals(item.getLayeringId(), layeringId)) {
                    // 变量.计税产品List.add(L)
                    listTaxProduct.add(item);
                }
            }
            // IF 变量.计税产品List IS NOT NULL AND 变量.计税产品List.size()>0
            if (CollectionUtils.isNotEmpty(listTaxProduct)) {
                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->增值税初始化_分层产品SHEET
                Map<String, Object> taxMap = interfCommonService.initValueTax(listTaxProduct);
                // IF DTO.get("FIN_CASH_INFO") is not null
                if (taxMap.get("FIN_CASH_INFO") != null) {
                    // 变量.入库现金流信息List.addAll(DTO.get("FIN_CASH_INFO"));
                    listFinCashInfo.addAll((List<FinCashInfo>)taxMap.get("FIN_CASH_INFO"));
                }
                // IF DTO.get("BOK_DETAIL_ID_LIST") is not null
                if (taxMap.get("BOK_DETAIL_ID_LIST") != null) {
                    // 变量.入库账务明细代码List.addAll(DTO.get("BOK_DETAIL_ID_LIST"));
                    listBokDetailId.addAll((List<BokDetailId>)taxMap.get("BOK_DETAIL_ID_LIST"));
                }
                // IF DTO.get("BOK_BOOKSET_PLAN") is not null
                if (taxMap.get("BOK_BOOKSET_PLAN") != null) {
                    // 变量.入库估值方案List.addAll(DTO.get("BOK_BOOKSET_PLAN"));
                    listBokBooksetPlan.addAll((List<BokBooksetPlan>)taxMap.get("BOK_BOOKSET_PLAN"));
                }

            }
        }
        // 2.6 根据前台传的账套参数信息进行初始化
        // IF 变量.入库账套配置List IS NOT NULL AND 变量.入库账套配置List.SIZE()>0
        if (CollectionUtils.isNotEmpty(listBokBooksetExt)) {
            Map<String,String> qtMap = new HashMap<String,String>();
            //根据字段值映射账套参数信息
            valInterfCommonApiService.initBookParamByField(qtMap, dto.getValDecDigits(), dto.getValDecType(),
                    dto.getYeildDecDigits(), dto.getYeildDecType());
            // 根据已赋值好的账套参数值赋给账套
            valInterfCommonApiService.initBookParamsByMap(listBokBooksetExt, qtMap);

        }
        //产品初始化账套 入库操作
        valInterfCommonApiService.prodOtherInfoDataOpt(bokBookset, bokBooksetDate, listBokBooksetExt, bokAcctSubjectInfo,
                listEnterBokAcctLayeringInfo, listBokDetailId, listFinCashInfo, listBokBooksetPlan, jJPortfolio);
        return result;
    }

    /**
     * 理财子产品账套信息初始化
     * 用于理财子产品生成账套相关信息，传递的类型为：F22
     * @param dto 产品信息
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject prodOtherInfoInitF22(@ValInterfApiParameter ProdOtherInfoInitF22Dto dto){
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        // 定义 变量.分层代码 = 根据产品代码、产品类型生成分层代码
        String layeringId =valInterfCommonApiService.generateLayeringId(dto.getFinprodId(),
                dto.getFinprodType(), dto.getVdate());
        // 变量.入库账套Dto = DTO.账套信息
        BokBookset bokBookset = null;
        // 变量.变量.入库账套日期Dto = DTO.账套日期Dto
        BokBooksetDate bokBooksetDate =  null;
        // 定义 变量.入库账套配置List
        List<BokBooksetExt> listBokBooksetExt = Lists.newArrayList();
        // 变量.入库核算主体信息Dto = DTO.核算主体信息Dto
        BokAcctSubjectInfo bokAcctSubjectInfo = null;
        // 定义 变量.入库账务明细代码List
        List<BokDetailId> listBokDetailId = Lists.newArrayList();
        // 定义 变量.核算分层信息List
        List<BokAcctLayeringInfo> listBokAcctLayeringInfo = Lists.newArrayList();
        // 定义 变量.入库估值方案List
        List<BokBooksetPlan> listBokBooksetPlan = Lists.newArrayList();
        //定义 变量.几许产品信息
        JJPortfolioDto jJPortfolio = null;
        if (DictCodeList.是否_是.getValue().equals(dto.getIsAccount())) {
            // 定义 变量.标的类金融产品信息Dto FinProduct
            FinProduct finProduct = valInterfCommonApiService.buildFinProduct(dto.getFinprodId(),dto.getFinprodType(),
                    dto.getFinprodAbbr(),dto.getFinprodName(),dto.getProfitType(),dto.getVdate(),dto.getMdate(),
                    dto.getCcy(),dto.getCalendarId(),dto.getActmdate());
            //定义 变量.标的类金融产品扩展信息表Dto ValFinProductExt
            //定义 变量.托管活期利息计提 =
            String tgIsCalc = StringUtil.isEmpty(dto.getDepositAccout()) ? DictCodeList.是否_否.getValue() : dto.getDepositAccout();
            //构建估值侧标的类金融产品扩展信息表
            ValFinProductExt valFinProductExt = valInterfCommonApiService.buildValFinProductExt(tgIsCalc,
                    dto.getIsLayerAccount(),dto.getPortfolioId(),dto.getIsAccount());
            // 变量.其他参数值Dto 赋值
            OtherParamsDto otherParamsDto = valInterfCommonApiService.buildOtherParamsDto(dto.getValuationTemplate(),
                    StringUtils.EMPTY, new HashMap<String,String>());
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息初始化_分层产品SHEET
            BooksetInitResult booksetInitResult = interfCommonService.getParStrucProduct(finProduct, valFinProductExt, otherParamsDto);

            // 变量.入库账套Dto = DTO.账套信息
            bokBookset = booksetInitResult.getValBokBookset();
            // 变量.变量.入库账套日期Dto = DTO.账套日期Dto
            bokBooksetDate = booksetInitResult.getValBokBooksetDate();
            // 变量.入库账套配置List.addAll(DTO.账套配置List)
            listBokBooksetExt.addAll(booksetInitResult.getListValBokBooksetExt());
            // 变量.入库核算主体信息Dto = DTO.核算主体信息Dto
            bokAcctSubjectInfo = booksetInitResult.getAcctSubjectInfo();
            // 变量.入库账务明细代码List.addAll(DTO.账务明细代码List）
            listBokDetailId.add(booksetInitResult.getBokDetailId());
            // 变量.核算分层信息List.add(DTO.核算分层信息DTO)
            listBokAcctLayeringInfo.add(booksetInitResult.getAcctLayeringInfo());
            listBokBooksetPlan.add(booksetInitResult.getBokBooksetPlan());
            //定义 变量.几许产品信息
            jJPortfolio = valInterfCommonApiService.buildJJPortfolioDto(bokBookset.getBooksetId(), bokBookset.getStandardCcy(),
                    bokBookset.getBooksetIdTmpl(), bokBookset.getStartDate(), bokBookset.getBooksetFullname(),
                    bokBookset.getBooksetName(), dto.getCalendarId());
        }
        // 2.3 初始化核算分层信息
        // 定义 变量.本次新增分层产品List
        List<LayerPrdInfo> listLayerPrdInfo = Lists.newArrayList();
        // 定义 变量.本次新增账套信息List
        List<BokBookset> listBokBookset = Lists.newArrayList();
        // IF 变量.入库账套Dto IS NOT NULL and 变量.入库账套Dto.账套代码 IS NOT NULL
        if (bokBookset != null && StringUtils.isNotEmpty(bokBookset.getBooksetId())) {
            // 变量.本次新增账套信息List.add(变量.入库账套Dto)
            listBokBookset.add(bokBookset);
        }
        // 定义 变量.已有分层产品Map
        Map<String, LayerPrdInfo> mapLayerPrdInfo = interfCommonService.getExistPrdInfo();
        // 定义 变量.金融产品代码 = 输入.产品标的Dto.金融产品代码
        String finProductId = dto.getFinprodId();
        // 变量.金融产品代码 += Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(输入.产品标的Dto.起息日))
        finProductId += DateUtil.format(dto.getVdate(), DateUtil.DEFAULT_OTHER_FORMAT);
        // 定义 变量.分层产品Dto
        LayerPrdInfo layerPrdInfo = valInterfCommonApiService.buildLayerPrdInfo(finProductId,
                dto.getFinprodType(), dto.getFinprodName(), dto.getpFinprodId(), dto.getProfitType(),
                dto.getVdate(), dto.getMdate(), dto.getPortfolioId(), layeringId, dto.getCalendarId());
        // 变量.已有分层产品Map.put(变量.分层产品Dto.分层代码,变量.分层产品Dto)
        mapLayerPrdInfo.put(layerPrdInfo.getLayeringId(), layerPrdInfo);
        // 变量.本次新增分层产品List.add(变量.分层产品Dto)
        listLayerPrdInfo.add(layerPrdInfo);
        // 定义 变量.入库核算分层信息List
        List<BokAcctLayeringInfo> listEnterBokAcctLayeringInfo = Lists.newArrayList();
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->核算分层信息初始化_分层产品SHEET
        Map<String, Object> map = interfCommonService.getStrucSubProInit(listLayerPrdInfo, mapLayerPrdInfo, listBokBookset,
                listBokAcctLayeringInfo);
        // IF DTO.get("ACCT_LAYERING_INFO") is not null
        if (map.get("ACCT_LAYERING_INFO") != null) {
            // 变量.入库核算分层信息List.addAll(DTO.get("ACCT_LAYERING_INFO"))
            listEnterBokAcctLayeringInfo.addAll((List<BokAcctLayeringInfo>)map.get("ACCT_LAYERING_INFO"));
        }
        // IF DTO.get("BOK_DETAIL_ID") is not null
        if (map.get("BOK_DETAIL_ID") != null) {
            // 变量.入库账务明细代码List.addAll(DTO.get("BOK_DETAIL_ID"))
            listBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
        }
        // 2.4 增值税信息初始化
        // 定义 变量.入库现金流信息List
        List<FinCashInfo> listFinCashInfo = Lists.newArrayList();
        // IF 数据字典：是否.是 eq 输入.产品标的Dto.是否计税 OR 变量.入库核算分层信息List is not null
        if (DictCodeList.是否_是.getValue().equals(dto.getIsTax()) && CollectionUtils.isNotEmpty(listEnterBokAcctLayeringInfo)) {
            // 定义 变量.计税产品List
            List<BokAcctLayeringInfo> listTaxProduct = Lists.newArrayList();
            // LOOP 变量.入库核算分层信息List L
            for (BokAcctLayeringInfo item : listEnterBokAcctLayeringInfo) {
                // IF L.分层代码 eq 变量.分层代码
                if (StringUtils.equals(item.getLayeringId(), layeringId)) {
                    // 变量.计税产品List.add(L)
                    listTaxProduct.add(item);
                }
            }
            // IF 变量.计税产品List IS NOT NULL AND 变量.计税产品List.size()>0
            if (CollectionUtils.isNotEmpty(listTaxProduct)) {
                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->增值税初始化_分层产品SHEET
                Map<String, Object> taxMap = interfCommonService.initValueTax(listTaxProduct);
                // IF DTO.get("FIN_CASH_INFO") is not null
                if (taxMap.get("FIN_CASH_INFO") != null) {
                    // 变量.入库现金流信息List.addAll(DTO.get("FIN_CASH_INFO"));
                    listFinCashInfo.addAll((List<FinCashInfo>)taxMap.get("FIN_CASH_INFO"));
                }
                // IF DTO.get("BOK_DETAIL_ID_LIST") is not null
                if (taxMap.get("BOK_DETAIL_ID_LIST") != null) {
                    // 变量.入库账务明细代码List.addAll(DTO.get("BOK_DETAIL_ID_LIST"));
                    listBokDetailId.addAll((List<BokDetailId>)taxMap.get("BOK_DETAIL_ID_LIST"));
                }
                // IF DTO.get("BOK_BOOKSET_PLAN") is not null
                if (taxMap.get("BOK_BOOKSET_PLAN") != null) {
                    // 变量.入库估值方案List.addAll(DTO.get("BOK_BOOKSET_PLAN"));
                    listBokBooksetPlan.addAll((List<BokBooksetPlan>)taxMap.get("BOK_BOOKSET_PLAN"));
                }

            }
        }
        // 2.6 根据前台传的账套参数信息进行初始化
        // IF 变量.入库账套配置List IS NOT NULL AND 变量.入库账套配置List.SIZE()>0
        if (CollectionUtils.isNotEmpty(listBokBooksetExt)) {
            Map<String,String> qtMap = new HashMap<String,String>();
            //根据字段值映射账套参数信息
            valInterfCommonApiService.initBookParamByField(qtMap, dto.getValDecDigits(), dto.getValDecType(),
                    dto.getYeildDecDigits(), dto.getYeildDecType());
            // 根据已赋值好的账套参数值赋给账套
            valInterfCommonApiService.initBookParamsByMap(listBokBooksetExt, qtMap);

        }
        //产品初始化账套 入库操作
        valInterfCommonApiService.prodOtherInfoDataOpt(bokBookset, bokBooksetDate, listBokBooksetExt, bokAcctSubjectInfo,
                listEnterBokAcctLayeringInfo, listBokDetailId, listFinCashInfo, listBokBooksetPlan, jJPortfolio);
        return result;
    }

    /**
     * 组合账套信息初始化
     * 用于组合生成账套相关信息，传递的类型为：F18
     * @param dto 产品信息
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject prodOtherInfoInitF18(@ValInterfApiParameter ProdOtherInfoInitF18Dto dto){
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        // 定义 变量.分层代码 = （根据产品代码、产品类型生成分层代码）
        String layeringId =valInterfCommonApiService.generateLayeringId(dto.getFinprodId(),
                dto.getFinprodType(), dto.getVdate());
        // 变量.入库账套Dto = DTO.账套信息
        BokBookset bokBookset = null;
        // 变量.变量.入库账套日期Dto = DTO.账套日期Dto
        BokBooksetDate bokBooksetDate =  null;
        // 定义 变量.入库账套配置List
        List<BokBooksetExt> listBokBooksetExt = Lists.newArrayList();
        // 变量.入库核算主体信息Dto = DTO.核算主体信息Dto
        BokAcctSubjectInfo bokAcctSubjectInfo = null;
        // 定义 变量.入库账务明细代码List
        List<BokDetailId> listBokDetailId = Lists.newArrayList();
        // 定义 变量.核算分层信息List
        List<BokAcctLayeringInfo> listBokAcctLayeringInfo = Lists.newArrayList();
        // 定义 变量.入库估值方案List
        List<BokBooksetPlan> listBokBooksetPlan = Lists.newArrayList();
        //定义 变量.几许产品信息
        JJPortfolioDto jJPortfolio = null;
        if (DictCodeList.是否_是.getValue().equals(dto.getIsAccount())) {
            // 定义 变量.标的类金融产品信息Dto FinProduct
            FinProduct finProduct = valInterfCommonApiService.buildFinProduct(dto.getFinprodId(),dto.getFinprodType(),
                    dto.getFinprodAbbr(),dto.getFinprodName(),dto.getProfitType(),dto.getVdate(),dto.getMdate(),
                    dto.getCcy(),dto.getCalendarId(),dto.getActmdate());
            //定义 变量.标的类金融产品扩展信息表Dto ValFinProductExt
            //定义 变量.托管活期利息计提 =
            String tgIsCalc = StringUtil.isEmpty(dto.getDepositAccout()) ? DictCodeList.是否_否.getValue() : dto.getDepositAccout();
            //构建估值侧标的类金融产品扩展信息表
            ValFinProductExt valFinProductExt = valInterfCommonApiService.buildValFinProductExt(tgIsCalc,
                    dto.getIsLayerAccount(),dto.getPortfolioId(),dto.getIsAccount());
            // 变量.其他参数值Dto 赋值
            OtherParamsDto otherParamsDto = valInterfCommonApiService.buildOtherParamsDto(dto.getValuationTemplate(),
                    StringUtils.EMPTY, new HashMap<String,String>());
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息初始化_分层产品SHEET
            BooksetInitResult booksetInitResult = interfCommonService.getParStrucProduct(finProduct, valFinProductExt, otherParamsDto);

            // 变量.入库账套Dto = DTO.账套信息
            bokBookset = booksetInitResult.getValBokBookset();
            // 变量.变量.入库账套日期Dto = DTO.账套日期Dto
            bokBooksetDate = booksetInitResult.getValBokBooksetDate();
            // 变量.入库账套配置List.addAll(DTO.账套配置List)
            listBokBooksetExt.addAll(booksetInitResult.getListValBokBooksetExt());
            // 变量.入库核算主体信息Dto = DTO.核算主体信息Dto
            bokAcctSubjectInfo = booksetInitResult.getAcctSubjectInfo();
            // 变量.入库账务明细代码List.addAll(DTO.账务明细代码List）
            listBokDetailId.add(booksetInitResult.getBokDetailId());
            // 变量.核算分层信息List.add(DTO.核算分层信息DTO)
            listBokAcctLayeringInfo.add(booksetInitResult.getAcctLayeringInfo());
            listBokBooksetPlan.add(booksetInitResult.getBokBooksetPlan());
            //定义 变量.几许产品信息
            jJPortfolio = valInterfCommonApiService.buildJJPortfolioDto(bokBookset.getBooksetId(), bokBookset.getStandardCcy(),
                    bokBookset.getBooksetIdTmpl(), bokBookset.getStartDate(), bokBookset.getBooksetFullname(),
                    bokBookset.getBooksetName(), dto.getCalendarId());
        }
        // 定义 变量.入库核算分层信息List
        List<BokAcctLayeringInfo> listEnterBokAcctLayeringInfo = Lists.newArrayList();
        // IF 变量.核算分层信息List IS NOT NULL and 变量.核算分层信息List.size()>0
        if (CollectionUtils.isNotEmpty(listBokAcctLayeringInfo)) {
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->核算分层信息部分字段值初始化SHEET
            listEnterBokAcctLayeringInfo.addAll(interfCommonService.initBokAcctLayeringInfo(listBokAcctLayeringInfo));
        }
        // 2.4 增值税信息初始化
        // 定义 变量.入库现金流信息List
        List<FinCashInfo> listFinCashInfo = Lists.newArrayList();
        // IF 数据字典：是否.是 eq 输入.产品标的Dto.是否计税 OR 变量.入库核算分层信息List is not null
        if (DictCodeList.是否_是.getValue().equals(dto.getIsTax()) && CollectionUtils.isNotEmpty(listEnterBokAcctLayeringInfo)) {
            // 定义 变量.计税产品List
            List<BokAcctLayeringInfo> listTaxProduct = Lists.newArrayList();
            // LOOP 变量.入库核算分层信息List L
            for (BokAcctLayeringInfo item : listEnterBokAcctLayeringInfo) {
                // IF L.分层代码 eq 变量.分层代码
                if (StringUtils.equals(item.getLayeringId(), layeringId)) {
                    // 变量.计税产品List.add(L)
                    listTaxProduct.add(item);
                }
            }
            // IF 变量.计税产品List IS NOT NULL AND 变量.计税产品List.size()>0
            if (CollectionUtils.isNotEmpty(listTaxProduct)) {
                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->增值税初始化_分层产品SHEET
                Map<String, Object> taxMap = interfCommonService.initValueTax(listTaxProduct);
                // IF DTO.get("FIN_CASH_INFO") is not null
                if (taxMap.get("FIN_CASH_INFO") != null) {
                    // 变量.入库现金流信息List.addAll(DTO.get("FIN_CASH_INFO"));
                    listFinCashInfo.addAll((List<FinCashInfo>)taxMap.get("FIN_CASH_INFO"));
                }
                // IF DTO.get("BOK_DETAIL_ID_LIST") is not null
                if (taxMap.get("BOK_DETAIL_ID_LIST") != null) {
                    // 变量.入库账务明细代码List.addAll(DTO.get("BOK_DETAIL_ID_LIST"));
                    listBokDetailId.addAll((List<BokDetailId>)taxMap.get("BOK_DETAIL_ID_LIST"));
                }
                // IF DTO.get("BOK_BOOKSET_PLAN") is not null
                if (taxMap.get("BOK_BOOKSET_PLAN") != null) {
                    // 变量.入库估值方案List.addAll(DTO.get("BOK_BOOKSET_PLAN"));
                    listBokBooksetPlan.addAll((List<BokBooksetPlan>)taxMap.get("BOK_BOOKSET_PLAN"));
                }

            }
        }
        // 2.6 根据前台传的账套参数信息进行初始化
        // IF 变量.入库账套配置List IS NOT NULL AND 变量.入库账套配置List.SIZE()>0
        if (CollectionUtils.isNotEmpty(listBokBooksetExt)) {
            Map<String,String> qtMap = new HashMap<String,String>();
            //根据字段值映射账套参数信息
            valInterfCommonApiService.initBookParamByField(qtMap, dto.getValDecDigits(), dto.getValDecType(),
                    dto.getYeildDecDigits(), dto.getYeildDecType());
            // 根据已赋值好的账套参数值赋给账套
            valInterfCommonApiService.initBookParamsByMap(listBokBooksetExt, qtMap);

        }
        //产品初始化账套 入库操作
        valInterfCommonApiService.prodOtherInfoDataOpt(bokBookset, bokBooksetDate, listBokBooksetExt, bokAcctSubjectInfo,
                listEnterBokAcctLayeringInfo, listBokDetailId, listFinCashInfo, listBokBooksetPlan, jJPortfolio);
        return result;
    }

    /**
     * 产品清算账户初始化
     * 用于初始化产品\理财子产品\组合生成的账套与清算账户关系
     * @param dto 账户信息
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject prodAcctInfoInit(@ValInterfApiParameter ProdAcctInit001Dto dto) {
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        //根据产品ID获取产品信息
        FinProduct finProduct = valInterfCommonApiService.getFinProductById(dto.getFinprodId());
        if(finProduct == null) {
            result = new ValApiResultObject(ResultCode.WARNING);
            result.setResultMsg("产品代码[" + dto.getFinprodId() + "]无产品信息！");
            return result;
        }
        //赋值 金融产品类型 = 产品信息.金融产品类型
        dto.setFinprodType(finProduct.getFinprodType());
        // 定义 变量.分层代码 = （根据产品代码、产品类型生成分层代码）
        String layeringId =valInterfCommonApiService.generateLayeringId(dto.getFinprodId(),
                dto.getFinprodType(), finProduct.getVdate());
        //根据产品ID获取账套信息
        BokBookset bokBookset = interfCommonService.getBooksetByProdId(dto.getFinprodId());
        // 定义 变量.本次新增产品对应账套List
        List<ValBooksetInfoDto> listValBooksetInfoDto = Lists.newArrayList();
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
        List<ValBooksetInfoDto> list = interfCommonService.getBokBooksetByProdId_Layering(layeringId);
        if (CollectionUtils.isNotEmpty(list)) {
            //根据分层信息及账套信息构建要操作的账套列表
            valInterfCommonApiService.getValBooksetInfoList(bokBookset, list, listValBooksetInfoDto);
        }
        else {
            if(finProduct == null) {
                result = new ValApiResultObject(ResultCode.WARNING);
                result.setResultMsg("产品代码[" + dto.getFinprodId() + "]无核算主体信息！");
                return result;
            }
            // 变量.本次新增产品对应账套List.add（变量.产品对应账套Dto）
            listValBooksetInfoDto.add(valInterfCommonApiService.getValBooksetInfoDto(bokBookset));
        }
        // IF 变量.本次新增产品对应账套List IS NOT NULL
        if (CollectionUtils.isNotEmpty(listValBooksetInfoDto)) {
            // 定义 变量.入库账套清算账户List
            List<BokBooksetCapacctInfo> listBokBooksetCapacctInfo = Lists.newArrayList();
            // 定义 变量.入库账务明细代码List
            List<BokDetailId> listBokDetailId = Lists.newArrayList();
            //定义 变量.几许产品资金账户信息List
            List<JJBkCashAcctDto> listJJBkCashAcct = new ArrayList<JJBkCashAcctDto>();
            //账户数据及账套列表构建账套账户数据
            valInterfCommonApiService.prodAcctInit(dto, listValBooksetInfoDto,
                    listBokBooksetCapacctInfo, listJJBkCashAcct, listBokDetailId);
            //账户相关信息入库
            valInterfCommonApiService.prodAcctInfoDataOpt(listBokBooksetCapacctInfo, listJJBkCashAcct, listBokDetailId);
        }
        else {
            result = new ValApiResultObject(ResultCode.WARNING);
            result.setResultMsg("产品代码[" + dto.getFinprodId() + "]未获取到账套相关的信息！");
            return result;
        }

        return result;
    }

    /**
     * 交易入账
     * 根据交易信息生成估值入账相关信息
     * @param dto 产品交易信息
     * @param ptlTradeList 组合list
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject bokTradeBuildForAdd(@ValInterfApiParameter BokTradeBuild001Dto dto, List<BokTradeBuildPtl001Dto> ptlTradeList) {
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        //根据传递的组合数据，生成组合交易数据
        List<PtlTrade> listPtlTrade = valInterfCommonApiService.generatePtlTradeList(ptlTradeList);
        // 根据传递的数据构建交易信息
        TrdProductDeal deal = valInterfCommonApiService.buildTrdProductDeal(dto.getTradeId(), dto.getFinprodId(),
                dto.getFinprodType(), dto.getBusiType(), dto.getFpriceAmt(),
                dto.getChlId(), dto.getInvAim(), dto.getPs(), dto.getTradeDate(), dto.getSettleDate(), dto.getCcy(),
                dto.getTradeAmt(), dto.getTradeMarket(), dto.getTradeMarketMode(), dto.getTradeStatus(),
                dto.getPsPayment(), dto.getpTradeId(),
                null,null,null,null,
                null,null,null,null,
                null,null,null,null,
                null,null,null,null);
        //根据数据构建金融产品实物数据
        TrdFinProductBarter barter = valInterfCommonApiService.buildTrdFinProductBarter(dto.getBarterFinprodId(), dto.getBarterFinprodType(),
                dto.getBarterBranch());
        // 构建指令信息
        StlOrders stlOrders = valInterfCommonApiService.buildStlOrders(dto.getStlSettleAmt(), dto.getStlSettleDate(), dto.getStlMyAcctNo(),
                dto.getStlCounterAcctNo(),null,null,null,null,null,null,null,null,
                null,null,null,null);
        //根据产品代码、产品类型返回真正的产品代码、产品类型（因为组合需要处理）
        Map<String, String> realProdMap = valInterfCommonApiService.getRealFinprodInfo(dto.getProdId(), dto.getProdFinprodType());
        //产品id
        String prodId = realProdMap.get(ValApiConstant.产品代码字段);
        //产品类型
        String prodFinprodType = realProdMap.get(ValApiConstant.产品类型字段);
        // 定义 变量.入库结算指令List
        List<StlOrders> listStlOrders = Lists.newArrayList();
        // 定义 变量.入库结算指令扩展List
        List<StlOrdersExt> listStlOrdersExt = Lists.newArrayList();
        // 定义 变量.入库入账信息List
        List<BokDealAccounting> listBokDealAcct = Lists.newArrayList();
        // 定义 变量.入库入账明细信息List
        List<BokDealAccountingDetail> listBokDealAcctDetail = Lists.newArrayList();
        // 定义 变量.入库账务明细代码List
        List<BokDetailId> listBokDetailId = Lists.newArrayList();
//        // 定义 变量.账套信息List
//        List<ValBooksetInfoDto> listValBooksetInfoDto = Lists.newArrayList();
        //判断交易是否入账
        boolean isTradeDeal = valInterfCommonApiService.judgeIsTradeDealAcccount(dto.getBusiType(), dto.getPs());
        // 2.1 交易入账
        if (!isTradeDeal) {
            //核算主体代码
            String acctSubjectId = valInterfCommonApiService.generateAcctSubjectId(dto.getProdId(), dto.getChlId());
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
            List<ValBooksetInfoDto> list = interfCommonService.getBokBooksetByProdId_Layering(acctSubjectId);
            if (CollectionUtils.isEmpty(list)) {
                // 变量.resultObject.处理结果代码 = "1111"
                // 变量.resultObject.处理结果描述 = "产品代码["+输入.产品代码+"]无对应核算主体"
                result = new ValApiResultObject(ResultCode.WARNING);
                result.setResultMsg("产品代码[" + acctSubjectId + "]无对应核算主体");
                // return 变量.resultObject
                return result;
            } else {
//                // 变量.账套信息List.addAll(DTO)
//                listValBooksetInfoDto.addAll(list);
            }
//            //如果为投资组合且满足指定条件，获取额外的账套信息
//            valInterfCommonApiService.bokTradeBuildAdditionalBookset(listValBooksetInfoDto, prodId,
//                    prodFinprodType, dto.getChlId(), dto.getBusiType(), listPtlTrade);
//            //剔除重复的账套数据
//            valInterfCommonApiService.clearRepeatBookset(listValBooksetInfoDto);
        }
        else {
            //IF 数据字典：业务类型.活期存款交易 eq输入.交易信息Dto.业务类型
            if(DictCodeList.业务类型_活期存款交易.getValue().equals(dto.getBusiType())) {
                //核算主体代码
                String acctSubjectId = valInterfCommonApiService.generateAcctSubjectId(dto.getProdId(), dto.getChlId());
                //调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
                List<ValBooksetInfoDto> acctSubjectlist = interfCommonService.getBokBooksetByProdId_Layering(acctSubjectId);
                if(CollectionUtils.isEmpty(acctSubjectlist)){
                    result = new ValApiResultObject(ResultCode.WARNING);
                    result.setResultMsg("产品代码[" + acctSubjectId + "]无对应核算主体");
                    // return 变量.resultObject
                    return result;
                }
                else {
                    //定义 变量.账套代码
                    String booksetId = acctSubjectlist.get(0).getBooksetId();
                    BokBooksetCapacctInfo bokBooksetCapacctInfo = interfCommonService.queryBokBooksetCapacctInfo(booksetId, dto.getFinprodId(),
                            dto.getCcy(),null);
                    if(bokBooksetCapacctInfo == null){
                        result = new ValApiResultObject(ResultCode.WARNING);
                        result.setResultMsg("产品代码[" + prodId + "]无对应核算主体");
                        return result;
                    } else {
                        dto.setFinprodType(bokBooksetCapacctInfo.getAcctType());
                    }
                    // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->交易入账_分层产品SHEET
                    InterfProductDealBookCommonDto layeringDto = interfCommonService.dealAccounting_Layering_Branch(deal, prodId, dto.getBranch(),
                            prodFinprodType, listPtlTrade, barter);
                    if (CollectionUtils.isNotEmpty(layeringDto.getBokDealAccountingList())) {
                        // 变量.入库入账信息List.addAll(DTO.bokDealAccountingList)
                        listBokDealAcct.addAll(layeringDto.getBokDealAccountingList());
                    }
                    if (CollectionUtils.isNotEmpty(layeringDto.getBokDealAccountingDetailList())) {
                        // 变量.入库入账明细信息List.addAll(DTO.bokDealAccountingDetailList)
                        listBokDealAcctDetail.addAll(layeringDto.getBokDealAccountingDetailList());
                    }
                    if (CollectionUtils.isNotEmpty(layeringDto.getBokDetailIdList())) {
                        //根据账务明细代码数据构建最终交易生成的账务明细代码list
                        valInterfCommonApiService.generateTradeBuildBokDetailIdList(layeringDto.getBokDetailIdList(),
                                listBokDetailId,dto.getBusiType(), dto.getrFinprodId());
                    }
//                    if (CollectionUtils.isNotEmpty(layeringDto.getValBooksetInfoDtoList())) {
//                        //特殊交易及业务类型构建账套信息
//                        valInterfCommonApiService.bokTradeBuildSpecialBookset(prodFinprodType, dto.getChlId(), dto.getBusiType(), dto.getPs(),
//                                layeringDto.getValBooksetInfoDtoList(), listValBooksetInfoDto);
//                    }
                }
            }
        }
        //根据数据生成交易入账最终需要入库的数据
        valInterfCommonApiService.generateBokTradeBuildData(prodId, prodFinprodType, dto.getBranch(), deal, stlOrders,
                listPtlTrade, listStlOrders, listStlOrdersExt, listBokDealAcct, listBokDealAcctDetail, listBokDetailId);
        //交易入账最终进行入库操作
        valInterfCommonApiService.bokTradeBuildForAddDataOpt(listStlOrders, listStlOrdersExt, listBokDealAcct, listBokDealAcctDetail, listBokDetailId);
        return result;
    }

    /**
     * 交易撤销
     * 根据原交易编号或者原指令编号撤销估值入账相关信息
     * @param pTradeId 原交易编号或者原指令编号
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject bokTradeForDel(@ValInterfApiParameter String pTradeId) {
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        List<TrdProductDeal> dealList = new ArrayList<TrdProductDeal>();
        //查询入账交易
        TrdProductDeal undeal = valInterfCommonApiService.queryAccountDeal(pTradeId, DictCodeListExt1.交易状态_初始.getValue());
        if(undeal == null){
            //查询待删除交易List
            List<TrdProductDeal> unDealquerys = valInterfCommonApiService.queryToDelDeal(pTradeId, DictCodeListExt1.交易状态_初始.getValue());
            if(CollectionUtils.isNotEmpty(unDealquerys)){
                dealList.addAll(unDealquerys);
            }
        }
        else{
            TrdProductDeal deal = new TrdProductDeal();
            deal.setTradeId(pTradeId);
            dealList.add(deal);
        }
        //交易入账撤销数据库操作
        valInterfCommonApiService.bokTradeForDelDataOpt(dealList);
        return result;
    }

    /**
     * 结算指令交易入账
     * @param dto 结算指令入账dto
     * @param ptlTradeList 组合交易数据
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject stlOrdersBokTradeBuild(@ValInterfApiParameter BokStlOrderBuild001Dto dto, List<BokTradeBuildPtl001Dto> ptlTradeList) {
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        //根据传递的组合数据，生成组合交易数据
        List<PtlTrade> listPtlTrade = valInterfCommonApiService.generatePtlTradeList(ptlTradeList);
        // 构建指令信息
        StlOrders stlOrders = valInterfCommonApiService.buildStlOrders(dto.getSettleAmt(), dto.getSettleDate(), dto.getMyAcctNo(),
                dto.getCounterAcctNo(),dto.getOrderId(),dto.getBusiType(),dto.getOrderType(),dto.getOrderNum(),dto.getTradeId(),
                dto.getPs(),dto.getCcy(),dto.getSettleStatus(),dto.getOrderStatus(),dto.getpOrderId(),
                dto.getfOrderId(),dto.getChannelId());
        // 定义 变量.入库入账信息List
        List<BokDealAccounting> listBokDealAcct = Lists.newArrayList();
        // 定义 变量.入库入账明细信息List
        List<BokDealAccountingDetail> listBokDealAcctDetail = Lists.newArrayList();
        // 定义 变量.入库账务明细代码List
        List<BokDetailId> listBokDetailId = Lists.newArrayList();
        // 定义 变量.账套信息List List<ValBooksetInfoDto>
        List<ValBooksetInfoDto> listValBooksetInfoDto = Lists.newArrayList();
        //特殊指令类型需要交行本方账户及对方账户
        valInterfCommonApiService.exchangeStlOrdersByStlOrder(stlOrders);
        //根据产品代码、产品类型返回真正的产品代码、产品类型（因为组合需要处理）
        Map<String, String> realProdMap = valInterfCommonApiService.getRealFinprodInfo(dto.getProdId(), dto.getProdFinprodType());
        //产品id
        String prodId = realProdMap.get(ValApiConstant.产品代码字段);
        //产品类型
        String prodFinprodType = realProdMap.get(ValApiConstant.产品类型字段);
        // 2.2.1 查询账套信息
        //定义 变量.核算主体代码
        String acctSubjectId = valInterfCommonApiService.generateAcctSubjectId(dto.getProdId(), dto.getChannelId());
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
        List<ValBooksetInfoDto> list = interfCommonService.getBokBooksetByProdId_Layering(acctSubjectId);
        if (CollectionUtils.isEmpty(list)) {
            result.setResultCode(ResultCode.WARNING.getCode());
            result.setResultMsg("产品代码[" + acctSubjectId + "]无对应核算主体");
            return result;
        }
        // 变量.账套信息List.addAll(DTO)
        listValBooksetInfoDto.addAll(list);
        //指令入账特殊增加账套信息
        valInterfCommonApiService.stlOrdersBokTradeAdditionalBookset(listValBooksetInfoDto, prodId, prodFinprodType,
                dto.getChannelId(), listPtlTrade);
        //剔除重复的账套数据
        valInterfCommonApiService.clearRepeatBookset(listValBooksetInfoDto);
        /**ADD BY WUJIE ON 20191118 END**/
        //定义 变量.四级科目尾号集合 Map<String,BokDetailId>
        Map<String,BokDetailId> bokDetailIdMap= new HashMap<>();
        // LOOP 变量.账套信息List L
        for (ValBooksetInfoDto item : listValBooksetInfoDto) {
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询账套清算款账户SHEET
            BokBooksetCapacctInfo bokBooksetCapacctInfo = interfCommonService.queryBokBooksetCapacctInfo(item.getBooksetId(),
                    stlOrders.getMyAcctNo(), stlOrders.getCcy(),null);
            // IF DTO IS NULL
            if (bokBooksetCapacctInfo == null) {
                // 变量.resultObject.处理结果代码 = "1111"
                // 变量.resultObject.处理结果描述 = "账套["+变量.账套代码+"]无资金账户["+输入.结算指令交易信息Dto.本方资金账户+"]"
                result.setResultCode(ResultCode.WARNING.getCode());
                result.setResultMsg("账套[" + item.getBooksetId() + "]无资金账户[" + stlOrders.getMyAcctNo() + "]");
                // 直接执行第3步
                return result;
            }
            // 变量.业务明细类型 = DTO.资金账户类型
            String busiDetailType = bokBooksetCapacctInfo.getAcctType();
            // 定义 变量.账务明细代码 =
            // interfCommonService.getBokDetailId(输入.结算指令交易信息Dto.本方资金账户,null,变量.业务明细类型,null,null)
            BokDetailId bokDetailId = interfCommonService.getBokDetailId(stlOrders.getMyAcctNo(), null, busiDetailType, null,null,stlOrders.getCcy());
            //定义 变量.bokDetailIdTail = 变量.四级科目尾号集合.get(L.账套代码+变量.账务明细代码)
            BokDetailId bokDetailIdTail = bokDetailIdMap.get(item.getBooksetId() + bokDetailId.getBokDetailId());
            if (!StringUtil.isNotEmpty(bokDetailIdTail)){
                //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                bokDetailIdTail = interfCommonService.settingDetailSubjectNoTaiL(stlOrders.getMyAcctNo(), busiDetailType, null, null, item.getBooksetId());
            }
            //变量.四级科目尾号集合.put(L.账套代码+变量.账务明细代码,变量.bokDetailId)
            bokDetailIdMap.put(item.getBooksetId() + bokDetailId.getBokDetailId(), bokDetailIdTail);

            // 2.2.3 初始化账务明细代码表
            BokDetailId bokDetailIdInitDto = valInterfCommonApiService.buildBokDetailId(item.getBooksetId(), bokDetailId.getBokDetailId(), dto.getMyAcctNo(),
                    busiDetailType, dto.getOrderId(), dto.getCcy(), bokDetailIdTail.getDetailSubjectNoTail(), bokDetailIdTail.getDetailSubnameTail());

            // 2.2.4 初始化入账信息表
            BokDealAccounting bokDealAcctInitDto = valInterfCommonApiService.buildBokDealAccounting(item.getBooksetId(), dto.getOrderId(), DictCodeList.业务表_结算指令表.getValue(),
                    stlOrders.getSettleDate(), stlOrders.getSettleDate(), stlOrders.getSettleDate(), DictCodeList.是否_否.getValue());
            // 变量.入库入账信息List.add(初始化结果)
            listBokDealAcct.add(bokDealAcctInitDto);
            // 2.2.5 初始化入账明细信息表
            BokDealAccountingDetail bokDealAcctDetailInitDto = valInterfCommonApiService.buildBokDealAccountingDetail(item.getBooksetId(), dto.getOrderId(),
                    DictCodeList.业务表_结算指令表.getValue(), DictCodeList.业务类型_指令交易.getValue(), bokDetailId.getBokDetailId());
            // 变量.入库入账明细信息List.add(初始化结果)
            listBokDealAcctDetail.add(bokDealAcctDetailInitDto);
            //2.2.6 对方资金账户：账务明细代码生成
            if(DictCodeListExt3.指令类型_债券类资产_申购划款.getValue().equals(dto.getOrderType())
                    || DictCodeListExt3.指令类型_债券类资产_申购退款.getValue().equals(dto.getOrderType())){
                /**ADD BY LBY ON 20201226 START**/
                //定义 变量.bokDetailId = 变量.四级科目尾号集合.get(L.账套代码+变量.账务明细代码DTO.账务明细代码)
                BokDetailId bokDetailIdTail1 = bokDetailIdMap.get(item.getBooksetId() + dto.getCounterAcctNo());
                if (!StringUtil.isNotEmpty(bokDetailIdTail1)){
                    //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                    //变量.bokDetailId = 返回值
                    bokDetailIdTail1 = interfCommonService.settingDetailSubjectNoTaiL(dto.getCounterAcctNo(), DictCodeListExt1.资金账户类型_交易保证金账户.getValue(), null, null, item.getBooksetId());
                }
                //变量.四级科目尾号集合.put(L.账套代码+变量.账务明细代码DTO.账务明细代码,变量.bokDetailId)
                bokDetailIdMap.put(item.getBooksetId() + dto.getCounterAcctNo(), bokDetailIdTail1);
                BokDetailId bokDetailIdDto = new BokDetailId();
                bokDetailIdDto = valInterfCommonApiService.buildBokDetailId(item.getBooksetId(), null,
                        dto.getCounterAcctNo(), DictCodeListExt1.资金账户类型_交易保证金账户.getValue(),
                        null, dto.getCcy(),null, null);
                bokDetailIdDto = valInterfCommonApiService.buildBokDetailId(item.getBooksetId(), interfCommonService.genMd5BokDetailId(bokDetailIdDto),
                        dto.getCounterAcctNo(), DictCodeListExt1.资金账户类型_交易保证金账户.getValue(),null, dto.getCcy(),
                        bokDetailIdTail1.getDetailSubjectNoTail(), bokDetailIdTail1.getDetailSubnameTail());
            }

            //指令表入账
            List<StlOrders> listStlOrders = new ArrayList<StlOrders>();
            listStlOrders.add(stlOrders);
            //指令扩展表入账
            List<StlOrdersExt> listvalStlOrdersExt = new ArrayList<StlOrdersExt>();
            listvalStlOrdersExt.add(valInterfCommonApiService.buildStlOrdersExt(dto.getOrderId(), DictCodeList.是否_否.getValue()));

        }
        return result;
    }

    /**
     * 存续期交易合并
     * 估值侧针对拆入、拆出、回购存续期交易进行合并
     * @param dto 存续期产品及费用信息
     * @param durationList 存续期交易信息
     * @param ptlTradeList 组合信息
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject durationTradeMerge(@ValInterfApiParameter DurationTradeMerge001Dto dto,
                                                 @ValInterfApiParameter List<DurationTrade001Dto> durationList,
                                                 List<BokTradeBuildPtl001Dto> ptlTradeList) {
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        //根据产品代码、产品类型返回真正的产品代码、产品类型（因为组合需要处理）
        Map<String, String> realProdMap = valInterfCommonApiService.getRealFinprodInfo(dto.getProdId(), dto.getProdFinprodType());
        //产品id
        String prodId = realProdMap.get(ValApiConstant.产品代码字段);
        //产品类型
        String prodFinprodType = realProdMap.get(ValApiConstant.产品类型字段);

        Map<String, BigDecimal> 金额Map = valInterfCommonApiService.calcDurationTradeMergeAmt(durationList);
        // 2.2.1 初始化金融产品表
        // 定义 变量.本金金额
        BigDecimal prinAmt = 金额Map.get(ValApiConstant.本金金额字段);
        // 定义 变量.实收利息
        BigDecimal intAmt = 金额Map.get(ValApiConstant.实收利息字段);
        // 定义 变量.交易份额
        BigDecimal shareAmt = 金额Map.get(ValApiConstant.交易份额字段);
        // 定义 变量.净价总额
        BigDecimal cpriceAmt = 金额Map.get(ValApiConstant.净价总额字段);
        // 定义 变量.全价总额
        BigDecimal fpriceAmt = 金额Map.get(ValApiConstant.全价总额字段);
        // 定义 变量.交易金额
        BigDecimal tradeAmt = 金额Map.get(ValApiConstant.交易金额字段);
        //获取交易方向
        String ps = valInterfCommonApiService.getDurationTradeMergePs(dto.getPsType(), durationList.get(0).getBusiType());
        // 根据传递的数据构建交易信息
        TrdProductDeal trdProductDealVo = valInterfCommonApiService.buildTrdProductDeal(interfCommonService.getTradeid(ps, durationList.get(0).getTradeDate(), "A"),
                durationList.get(0).getFinprodId(),durationList.get(0).getFinprodType(), durationList.get(0).getBusiType(), fpriceAmt,
                durationList.get(0).getChlId(), null, ps, durationList.get(0).getTradeDate(),
                durationList.get(0).getSettleDate(), durationList.get(0).getCcy(), tradeAmt, durationList.get(0).getTradeMarket(),
                durationList.get(0).getTradeMarketMode(), durationList.get(0).getTradeStatus(),null, null,
                durationList.get(0).getFinprodType2(), InterfCommonConstant.分支序号_默认,durationList.get(0).getVdate(),durationList.get(0).getMdate(),
                cpriceAmt,shareAmt,prinAmt,intAmt,DictCodeListExt1.交易状态_初始.getValue(),DictCodeList.是否_否.getValue(),
                durationList.get(0).getTotleTradeId(),durationList.get(0).getCounterId(),
                durationList.get(0).getTrader(),durationList.get(0).getCashId(),durationList.get(0).getTradePlat(),durationList.get(0).getSecManageAcctId());
        // 定义 变量.金融产品交易附属表Dto
        TrdProductDealAdd trdProductDealAddVo = valInterfCommonApiService.buildTrdProductDealAdd(trdProductDealVo.getTradeId(), prodId,
                DictCodeListExt1.生成方式_接口接入.getValue());

        boolean chlFeeProFlag = trdProductDealVo != null
                && trdProductDealVo.getChlId() != null
                && StringUtil.isNotEmpty(dto.getFeeType())
                && dto.getFeeAmt() != null
                && dto.getFeeAmt().compareTo(BigDecimal.ZERO) > 0
                /**ADD BY WUJIE ON 20191025 START**/
                && DictCodeList.是否_是.getValue().equals(dto.getIsPayWithTrade())
                ;
        //定义 变量.随交易支付通道费 =0
        BigDecimal tdFeeAmt = BigDecimal.ZERO;
        if(chlFeeProFlag){
            tdFeeAmt = dto.getFeeAmt();
            trdProductDealVo.setFeeAmtPay(tdFeeAmt);
            if(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(trdProductDealVo.getFinprodType())
                    || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(trdProductDealVo.getFinprodType())
                    || DictCodeListExt1.金融产品类型_拆入.getValue().equals(trdProductDealVo.getFinprodType())){
                trdProductDealVo.setTradeAmt(ArithmeticUtil.doAdd(trdProductDealVo.getTradeAmt(),tdFeeAmt));//交易金额+通道费

            }else{
                trdProductDealVo.setTradeAmt(ArithmeticUtil.subtract(trdProductDealVo.getTradeAmt(),tdFeeAmt));//交易金额 - 通道费
            }
        }
        //金融产品及附属表入库
        valInterfCommonApiService.durationTradeMergeDataOpt(trdProductDealVo, trdProductDealAddVo, null);
        //构建交易入账接口的参数
        BokTradeBuild001Dto bokTradeBuild001Dto = valInterfCommonApiService.buildBokTradeBuild001Dto(prodId, prodFinprodType, dto.getBranch(), trdProductDealVo);
        // 2.2.4 调用交易入账接口
        // 交易入账
        result = this.bokTradeBuildForAdd(bokTradeBuild001Dto, ptlTradeList);
        //通道随 交易支付
        if(chlFeeProFlag){
            //通道费用
            tdFeeAmt = dto.getFeeAmt();
            //随交易支付交易
            TrdProductDeal feeDeal = valInterfCommonApiService.buildTrdProductDeal(interfCommonService.getTradeid(DictCodeList.交易方向_付费.getValue(), trdProductDealVo.getTradeDate(), "A"),
                    null,DictCodeListExt1.一次性费用类型_通道费.getValue(), DictCodeList.业务类型_产品费用交易.getValue(), null,
                    trdProductDealVo.getChlId(), null, DictCodeList.交易方向_付费.getValue(), trdProductDealVo.getTradeDate(),
                    trdProductDealVo.getSettleDate(), trdProductDealVo.getCcy(), tdFeeAmt, null,
                    null, trdProductDealVo.getTradeStatus(),null, null,
                    null, InterfCommonConstant.分支序号_默认,null,null,
                    null,null,null,null,null,null,
                    trdProductDealVo.getTradeId(),null,
                    null,null,null,null);
            //交易附属表
            TrdProductDealAdd feeDealAdd = valInterfCommonApiService.buildTrdProductDealAdd(feeDeal.getTradeId(), prodId, DictCodeListExt1.生成方式_接口接入.getValue());
            //费用明细数据
            TrdTradeFeeDetail feeDetail = valInterfCommonApiService.buildTrdTradeFeeDetail(feeDeal.getTradeId(),DictCodeListExt1.一次性费用类型_通道费.getValue(),
                    dto.getIsPayWithTrade(), tdFeeAmt, DictCodeListExt1.一次性费用类型_通道费.getValue());
            //金融产品及附属表入库
            valInterfCommonApiService.durationTradeMergeDataOpt(feeDeal, feeDealAdd, feeDetail);
            //构建交易入账接口的参数
            BokTradeBuild001Dto chnBokTradeBuild001Dto = valInterfCommonApiService.buildBokTradeBuild001Dto(prodId, prodFinprodType, dto.getBranch(), trdProductDealVo);
            // 调用交易入账
            result = this.bokTradeBuildForAdd(chnBokTradeBuild001Dto, ptlTradeList);
        }
        //通道不为空
        if(StringUtil.isNotEmpty(trdProductDealVo.getChlId())){
            FinProductAdd payType = valInterfCommonApiService.getFinProductAdd(trdProductDealVo.getChlId());
            if (payType != null && DictCodeListExt2.划款方式_逐笔划款.getValue().equals(payType.getPayType())){
                /**ADD BY WUJIE ON 20191025 START**/
                //定义 变量.交易金额 =	变量.金融产品表Dto.交易金额
                BigDecimal tradeAmt_TD = trdProductDealVo.getTradeAmt() == null ? BigDecimal.ZERO : trdProductDealVo.getTradeAmt();
                if(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(trdProductDealVo.getFinprodType())
                        || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(trdProductDealVo.getFinprodType())
                        || DictCodeListExt1.金融产品类型_拆入.getValue().equals(trdProductDealVo.getFinprodType())){
                    tradeAmt_TD = tradeAmt_TD.subtract(tdFeeAmt);
                }else{
                    tradeAmt_TD = tradeAmt_TD.add(tdFeeAmt);
                }
                /**ADD BY WUJIE ON 20191025 END**/
                //TODO 调用通道费用随交易支付api接口
            }
        }

        return result;
    }

    /**
     * 合并交易撤销
     * 估值撤销拆入、拆出、回购存续期交易生成的合并交易信息
     * @param totleTradeId 汇总交易编号
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject durationTradeDel(@ValInterfApiParameter String totleTradeId) {
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        //定义 变量.待删除到期交易
        List<TrdProductDeal> toDeltrdProductDealList = new ArrayList<>();
        // 2.1 根据汇总交易编号、交易方向、交易状态查询需要删除的到期交易
        // 定义 变量.拆入到期交易编号
        List<TrdProductDeal> listTrdProductDeal = valInterfCommonApiService.getTradeIdByTotleTradeId(totleTradeId);
        // 获取不到交易情况
        if(CollectionUtils.isEmpty(listTrdProductDeal)){
            result.setResultCode(ResultCode.WARNING.getCode());
            result.setResultMsg("汇总交易编号["+totleTradeId+"]无对应拆入到期交易");
            return result;
        }
        // 变量.待删除交易信息List.addAll(变量.待删除到期交易)
        toDeltrdProductDealList.addAll(listTrdProductDeal);
        // 2.1.2 查询到期交易对应的通道费用交易
        List<TrdProductDeal> toDelFeeDealList = valInterfCommonApiService.getFeeDealByTotleTradeId(totleTradeId);
        if(CollectionUtils.isNotEmpty(toDelFeeDealList)){
            toDeltrdProductDealList.addAll(toDelFeeDealList);
        }
        // 2.2 删除交易信息及入账信息
        if(CollectionUtils.isNotEmpty(toDeltrdProductDealList)){
            toDeltrdProductDealList.forEach(i -> {
                // 调用交易撤销api接口
                this.bokTradeForDel(i.getTradeId());
                // 调用交易信息删除
                valInterfCommonApiService.delValTradeInfo(i.getTradeId());
            });
        }
        return result;
    }

    /**
     * 核算到期日调整
     * 用于调整估值侧账套的到期日
     * @param dto 核算到期日调整dto
     * @return
     */
    @ValInterfApiMethod
    public ValApiResultObject updateProdMdate(@ValInterfApiParameter ProdMdateUpdate001Dto dto) {
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        //定义 变量.账套信息
        BokBookset valBokBookset = new BokBookset();
        //定义 变量.子账套信息
        List<BokBooksetRe> valBokBooksetReList = new ArrayList<BokBooksetRe>();
        //2.处理详情
        //2.1 查询产品作为核算主体时的账套信息
        if(!StringUtil.equals(DictCodeListExt1.金融产品类型_理财子产品.getValue(), dto.getProdType())) {
            //根据产品代码、产品类型查询账套信息
            valBokBookset = valInterfCommonApiService.queryBooksetAsAcct(dto.getProdId(), dto.getProdType());
        }
        //2.2 查询子账套信息
        if(valBokBookset != null) {
            //根据账套代码查询关联账套信息
            valBokBooksetReList =  valInterfCommonApiService.queryBokBooksetReList(valBokBookset.getBooksetId());
            //根据账套代码根据账套到期日
            valInterfCommonApiService.updateBooksetAsAcct(valBokBookset.getBooksetId(), dto.getMdate());
        }
        //根据产品信息更新分层信息到期日
        valInterfCommonApiService.updateAcctLayeringInfoByProd(dto.getProdId(), dto.getProdType(),
                dto.getBranch(), dto.getMdate());
        //根据关联账套信息更新分层信息的到期日
        valInterfCommonApiService.updateAcctLayeringInfoByBookset(valBokBooksetReList, dto.getMdate());
        return result;
    }

    @ValInterfApiMethod
    public ValApiResultObject test(@ValInterfApiParameter String pTradeId) {
        ValApiResultObject result = new ValApiResultObject(ResultCode.SUCCESS);
        return result;
    }
}

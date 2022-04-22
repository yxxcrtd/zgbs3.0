/*
 * 文件名：ValVoucherQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月3日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.valscheme.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListNatureGroup;
import com.joyintech.tams.valuation.batch.common.accounting.entity.CorrespondSubjectEntity;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.calculation.entity.InterestBasisDaysParameter;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.valscheme.dao.ValSchemeMapper;
import com.joyintech.tams.valuation.batch.valscheme.dto.*;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.MarketPriceDto;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tools.collectors.JoyinCollectors;
import groovy.lang.Tuple2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 杭州估值表分层生成service
 * 
 * @author yangzhi
 * @version 2.0 2018年5月3日
 * @see ValSchemeHZLayerNewService
 * @since 2.0
 */
@Service
public class ValSchemeHZLayerNewService extends CommonExportService<ValSchemeBatchDto> {

    /**
     * 共通方法的 Service
     */
    @Autowired
    private ValSchemeMapper valSchemeMapper;

    @Autowired
    private CalculationCommonService calculationCommonService;

    @Autowired
    private AccountingCommonService accountingCommonService;

    @Autowired
    private FinProductCommonService finProductCommonService;

    @Autowired
    private FinProductMapper finProductMapper;

    public ValSchemeGenerateResult execute(Map<String, Object> params1)
         {
        // 获取reader返回值
        ValSchemeTodoDto acctEntity = (ValSchemeTodoDto)params1.get(GlobalConstant.READER_RESULT);

        // 进行批处理需要将结果集封装到result
        ValSchemeGenerateResult result = new ValSchemeGenerateResult();

        // 定义全局变量-账套代码 booksetId
        String booksetId = acctEntity.getBooksetId();
        // 定义全局变量--入账日
        Date bookDate = acctEntity.getBookDate();
        //科目性质分组：分组代码_估值表_公允价值变动列表
        List<String> 公允价值变动列表 = accountingCommonService.getValSubjectNatureListWithAcctStandardId(
        		DictCodeListNatureGroup.分组代码_估值表_公允价值变动列表.getValue(),
                DictCodeListExt2.会计准则_企业准则.getValue());
        
      //科目性质分组：分组代码_估值表_利息调整列表
        List<String> 利息调整列表 = accountingCommonService.getValSubjectNatureListWithAcctStandardId(
        		DictCodeListNatureGroup.分组代码_估值表_利息调整列表.getValue(),
                DictCodeListExt2.会计准则_企业准则.getValue());
        // 收益类型查询-共通：账套参数查询
        List<String> paramCodeList = new ArrayList<>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_收益类型.getValue());
        
//        List<BokBooksetExt> listBokBooksetExt = accountingCommonService.getValBokBooksetExt(
//            booksetId, paramCodeList);
//        // 定义收益类型
//        String profitType = (CollectionUtils.isEmpty(
//            listBokBooksetExt)) ? "" : listBokBooksetExt.get(0).getParamValue();
         // 变量.收益类型 = 估值表处理结果集.核算方式
         String profitType = acctEntity.getAcctMode();
        //add by tanxuan 20200102 start
        // 定义 变量.待抵扣投资收益税
        BigDecimal deductInvIncomeTax = BigDecimal.ZERO;
        // 定义 变量.待抵扣暂估投资收益税
        BigDecimal extDeductInvIncomeTax = BigDecimal.ZERO;
        //add by tanxuan 20200102 end

        Map<String, Object> params = new HashMap<>();
        params.put("maxSubjectLevel", BookConstant.MAX_SUBJECT_LEVEL);
        params.put("booksetId", booksetId);
        params.put("bookDate", bookDate);
        params.put("subjectTypeList", Arrays.asList(DictCodeList.科目类型_资产类.getValue(),
            DictCodeList.科目类型_负债类.getValue(), DictCodeList.科目类型_资产负债共同类.getValue()));
        params.put("fairValChangeList", 公允价值变动列表);
        Map<String, BokSubject> 科目集合 = valSchemeMapper.selectSubjectInfo(params).stream().collect(
            JoyinCollectors.toMap(BokSubject::getSubjectNo, item -> item));
        // 资产、负债、共同类科目处理数据
        List<ValSchemeBatchDto> listDeal = valSchemeMapper.selectValSchemeDataDeal(params);
        // 注：上述结果为空，直接return
        if (CollectionUtils.isEmpty(listDeal)) {
            return null;
        }
        Map<String, ValSchemeBatchDto> 科目明细集合 = listDeal.stream().collect(
            JoyinCollectors.toMap(item -> item.getSubjectNo(), item -> item));
        Map<String, BigDecimal> 汇率集合 = listDeal.stream().map(
            ValSchemeBatchDto::getoCcy).distinct().map(item -> {
                return new Tuple2<String, BigDecimal>(item,
                    calculationCommonService.queryExchangeRate(item, acctEntity.getStandardCcy(),
                        bookDate));
            }).collect(JoyinCollectors.toMap(item -> item.getFirst(), item -> item.getSecond()));
        result.setProdValuationDtoList(new ArrayList<>());
        result.setBokAssetMarketInfoList(new ArrayList<>());
        // 定义 变量.偏离金额 = 0
        BigDecimal deviationAmt = BigDecimal.ZERO;
        HashMap<String, ProdValuationDto> 科目余额集合 = new HashMap<>();
        for (ValSchemeBatchDto dto : listDeal) {
//            BigDecimal 汇率 = 汇率集合.get(dto.getoCcy());
            BigDecimal 行情价格 = null;
            BigDecimal 原币估值 = null;
            BigDecimal 本币估值 = null;
            BigDecimal 原币估值增值 = BigDecimal.ZERO;
            BigDecimal 本币估值增值 = BigDecimal.ZERO;
            BigDecimal 影子价格估值 = BigDecimal.ZERO;
            // 行情日期
            Date marketDate = null;

            // 是否向上汇总 默认为是
            boolean isNotXshz = true;
            // 向上汇总方式--默认为全部
            String rollUpType = ValDictCodeList.估值表向上汇总方式_全部.getValue();
            // 科目为0的数据剔除
            // if（变量.科目明细.原币余额 = 0&& 变量.科目明细.本位币余额 = 0）
            // 科目为0的数据剔除
            // if（变量.科目明细.原币余额 = 0&& 变量.科目明细.本位币余额 = 0）
            if (ArithmeticUtil.equals(dto.getoAmt(), BigDecimal.ZERO) && ArithmeticUtil.equals(dto.getbAmt(), BigDecimal.ZERO)) {
                continue;
            }
            
            // add by tanxuan 20200101 start
            // 待抵扣投资收益税
            if (StringUtil.equals(dto.getBaseSubjectNature(),
                    DictCodeBaseSubjectNatrueNew.应交税费_应交增值税_价差.getValue())
                    && dto.getSubjectLevel() == 3) {
            	deductInvIncomeTax = deductInvIncomeTax.add(dto.getbAmt());
            }
            // 待抵扣暂估投资收益税
            if (StringUtil.equals(dto.getBaseSubjectNature(),
                    DictCodeBaseSubjectNatrueNew.应交税费_预估销项税_价差.getValue())
                    && dto.getSubjectLevel() == 3) {
            	extDeductInvIncomeTax = extDeductInvIncomeTax.add(dto.getbAmt());
            }
            // add by tanxuan 20200101 end
            
            //if（公允价值变动列表.包含(变量.科目明细.基础科目性质) and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
            if (公允价值变动列表.contains(dto.getBaseSubjectNature())
                    && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                //变量.原币估值 = 变量.科目明细.原币余额
                原币估值 = dto.getoAmt();
                //变量.本币估值 = 变量.科目明细.本币余额
                本币估值 = dto.getbAmt();
            }
            // 余额方向调整
            if ((dto.getSubjectType().equals(DictCodeList.科目类型_资产类.getValue())
                 || dto.getSubjectType().equals(DictCodeList.科目类型_资产负债共同类.getValue()))
                && StringUtil.equals(dto.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                dto.setoAmt(dto.getoAmt().negate());

                //变量.科目明细.本位币余额 = -1*变量.科目明细.本位币余额
                dto.setbAmt(dto.getbAmt().negate());
            }
            else if (dto.getSubjectType().equals(DictCodeList.科目类型_负债类.getValue())
                     && StringUtil.equals(dto.getBalFlag(), DictCodeList.余额方向_借方.getValue())) {
                dto.setoAmt(dto.getoAmt().negate());

                //变量.科目明细.本位币余额 = -1*变量.科目明细.本位币余额
                dto.setbAmt(dto.getbAmt().negate());
            }

            // 以下处理 金额*汇率在过程中或结果均是保留2位小数
            // if（变量.科目明细.基础科目性质 = "摊余成本类成本" and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
            // modify by weipeng on 20220331 : if（使用摊余成本法估值 and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
            if (dto.getIsAcMethod()
                && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{摊余成本类利息调整}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 利息调整科目数据 = accountingCommonService.getCorrespondSubject(
                    dto.getFsubjectNo(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue()));
                //add jinmaodong 20201019 减值相关设计变更 START
                // 变量.科目信息列表-摊余成本类资产减值 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{摊余成本类利息调整}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 资产减值科目数据 = accountingCommonService.getCorrespondSubject(
                    dto.getFsubjectNo(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue()));

                ValSchemeBatchDto 资产减值明细 = null;
                if (!CollectionUtils.isEmpty(资产减值科目数据)) {
                	资产减值明细 = 科目明细集合.get(
                			资产减值科目数据.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                }
                if(null != 资产减值明细) {
                	// 变量.偏离金额 = 变量.偏离金额 + 变量.利息调整明细.余额(贷方)
                	if (StringUtil.equals(资产减值明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                    	deviationAmt = ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, deviationAmt,资产减值明细.getoAmt());
                    }else {
                    	deviationAmt = ArithmeticUtil.subtract(deviationAmt,资产减值明细.getoAmt(),NumberEnum.MONEY.getCode(), null);
                    }
                }
                //add jinmaodong 20201019 减值相关设计变更 END

                ValSchemeBatchDto 利息调整明细 = null;
                if (!CollectionUtils.isEmpty(利息调整科目数据)) {
                    利息调整明细 = 科目明细集合.get(
                        利息调整科目数据.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                }
                BigDecimal 单位成本tmp = BigDecimal.ZERO;
                if (利息调整明细 != null) {
                    if (StringUtil.equals(利息调整明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                        // 变量.单位成本 = (变量.科目明细.原币余额 - 变量.利息调整明细.原币余额）/变量.科目明细.数量
                        单位成本tmp = ArithmeticUtil.divide(
                            ArithmeticUtil.doSub(dto.getoAmt(), 利息调整明细.getoAmt()),
                            dto.getNumAmt()).setScale(4, RoundingMode.HALF_UP);
                    }
                    else {
                        // 变量.单位成本 = (变量.科目明细.原币余额 + 变量.利息调整明细.原币余额）/变量.科目明细.数量
                        单位成本tmp = ArithmeticUtil.divide(
                            ArithmeticUtil.doAdd(dto.getoAmt(), 利息调整明细.getoAmt()),
                            dto.getNumAmt()).setScale(4, RoundingMode.HALF_UP);
                    }
                }
                else {
                    单位成本tmp = ArithmeticUtil.divide(dto.getoAmt(), dto.getNumAmt()).setScale(4,
                        RoundingMode.HALF_UP);
                }
                // 变量.单位净价 = 调用标的类共通【影子定价查询（参数.入账日，参数.账套代码，变量.科目明细.四级科目号尾号）】 保留2位小数
                BigDecimal 单位净价 = BigDecimal.ZERO;
                MarketPriceDto priceDto = finProductCommonService.getShadowPrice(bookDate,
                    booksetId, dto.getDetailSubjectNoTail());
                if (StringUtil.isNotEmpty(priceDto)) {
                    if (StringUtil.isNotEmpty(priceDto.getPrice())) {
                        单位净价 = priceDto.getPrice();
                        marketDate = priceDto.getValDate();
                    }
                }

                if (ArithmeticUtil.equals(BigDecimal.ZERO, 单位净价)) {
                    单位净价 = 单位成本tmp;
                }
                // 变量.影子价格估值 = 变量.科目明细.数量*变量.单位净价
                影子价格估值 = ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null, dto.getNumAmt(),
                    单位净价 == null ? BigDecimal.ZERO : 单位净价);
                行情价格 = 单位净价;
                if (利息调整明细 == null) {
                    // 变量.偏离金额 += 变量.科目明细.数量*变量.单位净价 - （ 变量.科目明细.原币余额）
                    // 2019年9月18日17:06:08 魏鹏：与影子价格一致保持2位小数
                    deviationAmt = ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, deviationAmt, ArithmeticUtil.subtract(
                        影子价格估值, dto.getoAmt()));
                    //变量.原币估值  =  变量.科目明细.原币余额
                    原币估值 = dto.getoAmt();
                    //变量.本币估值  =  变量.科目明细.本位币余额
                    本币估值 = dto.getbAmt();
                }
                else {
                    if (StringUtil.equals(利息调整明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                        // 变量.偏离金额 += 变量.科目明细.数量*变量.单位净价 - （ 变量.科目明细.原币余额 - 变量.利息调整明细.原币余额）
                        // 2019年9月18日17:06:08 魏鹏：与影子价格一致保持2位小数
                        deviationAmt = ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, deviationAmt,
                            ArithmeticUtil.subtract(影子价格估值,
                                ArithmeticUtil.subtract(dto.getoAmt(), 利息调整明细.getoAmt())));

                        //变量.原币估值 =  变量.科目明细.原币余额 - 变量.利息调整明细.原币余额
                        原币估值 = ArithmeticUtil.doSub(dto.getoAmt(),利息调整明细.getoAmt());
                        //变量.本币估值 =  变量.科目明细.本位币余额 - 变量.利息调整明细.本位币余额
                        本币估值 = ArithmeticUtil.doSub(dto.getbAmt(),利息调整明细.getbAmt());
                    }
                    else {
                        影子价格估值 = ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null,
                            dto.getNumAmt(), 单位净价 == null ? BigDecimal.ZERO : 单位净价);

                        // 变量.偏离金额 += 变量.科目明细.数量*变量.单位净价 - （ 变量.科目明细.原币余额 + 变量.利息调整明细.原币余额）
                        // 2019年9月18日17:06:08 魏鹏：与影子价格一致保持2位小数
                        deviationAmt = ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, deviationAmt,
                            ArithmeticUtil.subtract(影子价格估值,
                                ArithmeticUtil.doAdd(dto.getoAmt(), 利息调整明细.getoAmt())));

                        //变量.原币估值 =  变量.科目明细.原币余额 + 变量.利息调整明细.原币余额
                        原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),利息调整明细.getoAmt());
                        //变量.本币估值 =  变量.科目明细.本位币余额 + 变量.利息调整明细.本位币余额
                        本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),利息调整明细.getbAmt());
                    }

                }

                // add by weipeng 20210807：增加资产行情信息入库 start
                // 变量.资产行情信息DTO
                BokAssetMarketInfo bokAssetMarketInfo = new BokAssetMarketInfo();
                //变量.资产行情信息DTO.流水号
                bokAssetMarketInfo.setSeqNo(IDGenerator.generate());
                // 变量.资产行情信息DTO.账套代码
                bokAssetMarketInfo.setBooksetId(dto.getBooksetId());
                // 变量.资产行情信息DTO.发生日期
                bokAssetMarketInfo.setHappenDate(bookDate);
                // 变量.资产行情信息DTO.入账日期
                bokAssetMarketInfo.setBookDate(bookDate);
                // 变量.资产行情信息DTO.金融产品代码 = 变量.标的信息.金融产品代码
                bokAssetMarketInfo.setFinprodId(dto.getDetailSubjectNoTail());
                // 查询对应标的信息
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getDetailSubjectNoTail());
                if(finProduct != null){
                    // 变量.资产行情信息DTO.金融产品类型 = 变量.标的信息.金融产品类型
                    bokAssetMarketInfo.setFinprodType(finProduct.getFinprodType());
                    // 变量.资产行情信息DTO.市场代码 = 变量.标的信息.市场代码
                    bokAssetMarketInfo.setMarketCode(finProduct.getFinprodMarketId());
                }
                // 变量.资产行情信息DTO.投资目的 = 摊余成本类
                bokAssetMarketInfo.setInvAim(DictCodeListExt1.投资目的_摊余成本类.getValue());
                // 变量.资产行情信息DTO.市价 = 变量.市价
                bokAssetMarketInfo.setMarketPrice(行情价格);
                // 变量.资产行情信息DTO.是否来源于行情数据
                // if 市价共通返回.市价== null || 变量.市价 != 市价共通返回.市价
                if(priceDto== null || priceDto.getPrice() ==null ||  行情价格.compareTo(priceDto.getPrice()) != 0){
                    // 变量.资产行情信息DTO.行情日期 = 市价共通返回.行情日期
                    bokAssetMarketInfo.setValueDate(bookDate);
                    bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_系统生成.getValue());
                }
                else{
                    // 变量.资产行情信息DTO.估值来源 = 市价共通返回.估值来源
                    bokAssetMarketInfo.setValueSource(priceDto.getValueSource());
                    // 变量.资产行情信息DTO.行情日期 = 市价共通返回.行情日期
                    bokAssetMarketInfo.setValueDate(priceDto.getValDate());
                    // 变量.资产行情信息DTO.估值方案代码 = 市价共通返回.估值方案代码
                    bokAssetMarketInfo.setSchemeId(priceDto.getSchemeId());
                    // 变量.资产行情信息DTO.估值方案明细代码 = 市价共通返回.估值方案明细代码
                    bokAssetMarketInfo.setSchemeDetailId(priceDto.getSchemeDetailId());

                    bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_行情信息.getValue());
                }
                // 添加到返回值中
                result.getBokAssetMarketInfoList().add(bokAssetMarketInfo);
                // add by weipeng 20210807：增加资产行情信息入库 end
            }
            else if (StringUtil.equals(dto.getBaseSubjectNature(),
                DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue())
                     && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {

                // 变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{公允价值损益类-公允价值变动}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                        dto.getFsubjectNo(), booksetId,
                        Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue()));
                // 变量.公允价值明细 = 科目明细Map.get(变量.科目信息列表.get（0）.科目号,变量.科目明细.四级科目号尾号)
                ValSchemeBatchDto 公允价值明细 = null;
                if (!CollectionUtils.isEmpty(科目信息列表)) {
                    公允价值明细 = 科目明细集合.get(
                        科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                }
                if (公允价值明细 != null) {
                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt().negate();
                        //变量.本币估值增值 = -变量.公允价值明细 .本位币余额
                        本币估值增值 = 公允价值明细.getbAmt().negate();
                    }
                    else {
                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt();
                        //变量.本币估值增值 = 变量.公允价值明细 .本位币余额
                        本币估值增值 = 公允价值明细.getbAmt();
                    }
                }
                // 变量.原币估值 = 变量.科目明细.原币币余额 + 变量.原币估值增值（为空取0）
                原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
                // 变量.本币估值 = 变量.科目明细.本币余额 + 变量.本币估值增值（为空取0）
                本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));

                // 变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
                行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
            }
            // else if 变量.科目明细.基础科目性质 = "金融负债成本" and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
            else if(StringUtil.equals(dto.getBaseSubjectNature(),
                    DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue())
                    && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())){
                //变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{公允价值变动}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                        dto.getFsubjectNo(), booksetId,
                        Arrays.asList(DictCodeBaseSubjectNatrueNew.交易性金融负债_公允价值变动.getValue()));
                // 变量.公允价值明细 = 科目明细Map.get(变量.科目信息列表.get（0）.科目号,变量.科目明细.四级科目号尾号)
                ValSchemeBatchDto 公允价值明细 = null;
                if (!CollectionUtils.isEmpty(科目信息列表)) {
                    公允价值明细 = 科目明细集合.get(
                            科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                }
                if (公允价值明细 != null) {
                    // if（变量.公允价值明细 .余额方向 = "借方"）
                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_借方.getValue())) {
                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt().negate();
                        //变量.本币估值增值 = -变量.公允价值明细 .本位币余额
                        本币估值增值 = 公允价值明细.getbAmt().negate();
                    }
                    else {
                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt();
                        //变量.本币估值增值 = 变量.公允价值明细 .本位币余额
                        本币估值增值 = 公允价值明细.getbAmt();
                    }
                }
                // 变量.原币估值 = 变量.科目明细.原币币余额 + 变量.原币估值增值（为空取0）
                原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
                // 变量.本币估值 = 变量.科目明细.本币余额 + 变量.本币估值增值（为空取0）
                本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));

                // 变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
                行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
            }
            else if (StringUtil.equals(dto.getBaseSubjectNature(),
                DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue())
                     && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询】
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                    dto.getFsubjectNo(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue(),
                        DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue()));
                ValSchemeBatchDto 利息调整明细 = null;
                ValSchemeBatchDto 公允价值明细 = null;
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{计划类资产-利息调整}，变量.科目明细.上级科目号）】

                for (CorrespondSubjectEntity correspondSubjectEntity : 科目信息列表) {
                    if (StringUtil.equals(correspondSubjectEntity.getBaseSubjectNature(),
                        DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue())) {
                        利息调整明细 = 科目明细集合.get(
                            correspondSubjectEntity.getSubjectNo() + dto.getDetailSubjectNoTail());
                    }
                    else if (StringUtil.equals(correspondSubjectEntity.getBaseSubjectNature(),
                        DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue())) {
                        公允价值明细 = 科目明细集合.get(
                            correspondSubjectEntity.getSubjectNo() + dto.getDetailSubjectNoTail());
                    }

                }
                // 变量.原币成本 = 变量.科目明细.原币余额
                BigDecimal 原币成本 = dto.getoAmt();
                // 变量.本币成本 = 变量.科目明细.本币余额
                BigDecimal 本币成本 = dto.getoAmt();
                if (利息调整明细 != null) {
                    if (StringUtil.equals(利息调整明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                        //变量.原币成本 =  变量.科目明细.原币余额 - 变量.利息调整明细.原币余额
                        原币成本 = ArithmeticUtil.doSub(dto.getoAmt(),利息调整明细.getoAmt());
                        //变量.本币成本 =  变量.科目明细.本币余额 - 变量.利息调整明细.本币余额
                        本币成本 = ArithmeticUtil.doSub(dto.getbAmt(),利息调整明细.getbAmt());
                    }
                    else {
                        //变量.原币成本 =  变量.科目明细.原币余额 + 变量.利息调整明细.原币余额
                        原币成本 = ArithmeticUtil.doAdd(dto.getoAmt(),利息调整明细.getoAmt());
                        //变量.本币成本 =  变量.科目明细.本币余额 + 变量.利息调整明细.本币余额
                        本币成本 = ArithmeticUtil.doAdd(dto.getbAmt(),利息调整明细.getbAmt());
                    }
                }
                // 变量.单位成本 = 变量.成本/变量.科目明细.数量
                // 单位成本 = ArithmeticUtil.divide(成本, dto.getNumAmt());
                if (公允价值明细 != null) {
                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt().negate();
                        //变量.本币估值增值 = -变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt().negate();
                    }
                    else {
                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt();
                        //变量.本币估值增值 = 变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt();
                    }
                }
                //变量.原币估值 = 变量.原币成本 + 变量.原币估值增值
                原币估值 = ArithmeticUtil.doAdd(原币成本,(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
                //变量.本币估值 = 变量.本币成本 + 变量.本币估值增值
                本币估值 = ArithmeticUtil.doAdd(本币成本,(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));

                if (公允价值明细 != null) {
                    //变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
                    行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
                }
            }
//            /** add by yanya on 2020021 增加期权科目性质处理 start**/
//            //else if(变量.科目明细.基础科目性质 = "买入期权-成本" and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
//            else if(StringUtil.equals(dto.getBaseSubjectNature(), DictCodeBaseSubjectNatrue.买入期权成本.getValue())
//                    && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())
//            ) {
//                //变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{买入期权-公允价值变动}，变量.科目明细.上级科目号）】
//                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
//                        dto.getFsubjectNo(), booksetId, Arrays.asList(DictCodeBaseSubjectNatrue.买入期权公允价值变动.getValue()));
//                //变量.公允价值明细 = 科目明细Map.get(变量.科目信息列表.get（0）.科目号,变量.科目明细.四级科目号尾号)
//                ValSchemeBatchDto 公允价值明细 = null;
//                if (!CollectionUtils.isEmpty(科目信息列表)) {
//                    公允价值明细 = 科目明细集合.get(科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
//                }
//                if (公允价值明细 != null) {
//                    //if（变量.公允价值明细 .余额方向 = "贷方"）
//                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
//                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
//                        原币估值增值 = 公允价值明细.getoAmt().negate();
//                        //变量.本币估值增值 = -变量.公允价值明细 .本币余额
//                        本币估值增值 = 公允价值明细.getbAmt().negate();
//                    }
//                    else {
//                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
//                        原币估值增值 = 公允价值明细.getoAmt();
//                        //变量.本币估值增值 = 变量.公允价值明细 .本币余额
//                        本币估值增值 = 公允价值明细.getbAmt();
//                    }
//                }
//                //变量.原币估值 = 变量.原币成本 + 变量.原币估值增值
//                // 备注：变量.原币成本 = 变量.科目明细.原币币余额
//                原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
//                //变量.本币估值 = 变量.本币成本 + 变量.本币估值增值
//                // 备注：变量.本币成本 = 变量.科目明细.本币成本
//                本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));
//
//                if (公允价值明细 != null) {
//                    //变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
//                    行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
//                }
//
//            }
//            //else if(变量.科目明细.基础科目性质 = "卖出期权-成本" and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
//            else if(StringUtil.equals(dto.getBaseSubjectNature(), DictCodeBaseSubjectNatrue.卖出期权成本.getValue())
//                    && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())
//            ) {
//                //变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{卖出期权-公允价值变动}，变量.科目明细.上级科目号）】
//                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
//                        dto.getFsubjectNo(), booksetId, Arrays.asList(DictCodeBaseSubjectNatrue.卖出期权公允价值变动.getValue()));
//                //变量.公允价值明细 = 科目明细Map.get(变量.科目信息列表.get（0）.科目号,变量.科目明细.四级科目号尾号)
//                ValSchemeBatchDto 公允价值明细 = null;
//                if (!CollectionUtils.isEmpty(科目信息列表)) {
//                    公允价值明细 = 科目明细集合.get(科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
//                }
//                if (公允价值明细 != null) {
//                    //if（变量.公允价值明细 .余额方向 = "贷方"）
//                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_借方.getValue())) {
//                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
//                        原币估值增值 = 公允价值明细.getoAmt().negate();
//                        //变量.本币估值增值 = -变量.公允价值明细 .本币余额
//                        本币估值增值 = 公允价值明细.getbAmt().negate();
//                    }
//                    else {
//                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
//                        原币估值增值 = 公允价值明细.getoAmt();
//                        //变量.本币估值增值 = 变量.公允价值明细 .本币余额
//                        本币估值增值 = 公允价值明细.getbAmt();
//                    }
//                }
//                //变量.原币估值 = 变量.原币成本 + 变量.原币估值增值
//                // 备注：变量.原币成本 = 变量.科目明细.原币币余额
//                原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
//                //变量.本币估值 = 变量.本币成本 + 变量.本币估值增值
//                // 备注：变量.本币成本 = 变量.科目明细.本币成本
//                本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));
//
//                if (公允价值明细 != null) {
//                    //变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
//                    行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
//                }
//
//            }
//            /** add by yanya on 2020021 增加期权科目性质处理 end**/
            // else if（公允价值变动列表.包含(变量.科目明细.基础科目性质)）
            else if (公允价值变动列表.contains(dto.getBaseSubjectNature())) {
                // if （变量.科目明细.科目类型 != "共同类"）
                if(!DictCodeList.科目类型_资产负债共同类.getValue().equals(dto.getSubjectType())){
                    // 是否向上汇总 = 否
                    isNotXshz = false;
                }
                else{
                    // 向上汇总方式 = 仅市值
                    rollUpType = ValDictCodeList.估值表向上汇总方式_仅市值.getValue();
                }
                // 变量.原币估值 = 变量.科目明细.原币余额
                原币估值 = dto.getoAmt();
                // 变量.本币估值 = 变量.科目明细.本币余额
                本币估值 = dto.getbAmt();
            }
            else if (!利息调整列表.contains(dto.getBaseSubjectNature())) {
                // 变量.原币估值 = 变量.科目明细.原币余额
                原币估值 = dto.getoAmt();
                // 变量.本币估值 = 变量.科目明细.本币余额
                本币估值 = dto.getbAmt();
            }


            // 行情日期处理：
            // if（变量.科目明细.基础科目性质 ！= "摊余成本类成本"）
            if (!DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(dto.getBaseSubjectNature())) {
                // if (变量.科目明细.科目级别 = 全局变量【底层科目级别】)
                if (BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                    // 调用共通【统计分析账务明细汇总查询】
                    // 变量.统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
                    BokStatDetPosiiton bokSummaryTable = accountingCommonService.getCountAnalysis(
                            bookDate, bookDate, bookDate, booksetId, null,
                            dto.getDetailSubjectNoTail(), null);
                    // 返回结果不为空时赋值
                    if (bokSummaryTable != null) {
                        // 变量.行情日期 = 上述共通返回.估值日
                        marketDate = bokSummaryTable.getValDate();
                    }
                }
            }

            ProdValuationDto prodValuationDto = new ProdValuationDto();
            // 科目代码 -> 变量.科目明细.科目号
            prodValuationDto.setSubjectNo(dto.getSubjectNo());
            //科目名称 -> 变量.科目明细.科目名称
            prodValuationDto.setSubjectName(dto.getSubjectName());
            // 原币币种 -> 变量.科目明细.原币
            prodValuationDto.setoCcy(dto.getoCcy());
            // 数量 -> 变量.科目明细.数量余额 -> 空时保持为空
            if (ArithmeticUtil.equals(BigDecimal.ZERO, dto.getNumAmt())) {
                prodValuationDto.setNum(null);
            }
            else {
                prodValuationDto.setNum(dto.getNumAmt());
            }
            // 原币成本 -> 变量.科目明细.原币余额
            prodValuationDto.setCost(dto.getoAmt());
            // 本币成本 -> 变量.科目明细.本币余额
            prodValuationDto.setbAmtCost(dto.getbAmt());
            // 单位价格设值(单位成本)
            // case 变量.单位成本不为空时 then 变量.单位成本 else 表样.成本/表样.数量
            if (!ArithmeticUtil.equals(prodValuationDto.getNum(), BigDecimal.ZERO)) {
                prodValuationDto.setUnitCost(ArithmeticUtil.divide(prodValuationDto.getCost(),
                        prodValuationDto.getNum()).setScale(2, RoundingMode.HALF_UP));
            }
            //本币成本占净值比 -> 空
            prodValuationDto.setCostPercent(null);
            // 行情价格 -> 变量.行情价格
            // prodValuationDto.setPrice(行情价格);
            // 原币估值 -> 变量.原币估值
            prodValuationDto.setValue(原币估值);
            // 本币估值 -> 变量.本币估值
            prodValuationDto.setbAmtValue(本币估值);
            // 汇率 -> 表样.本币成本/表样.原币成本 -> 四舍五入保留4位小数
            // 2020年2月15日 weipeng: 应付利润本位币与原币成本不一致，因为结转只结转本币，汇率处理相同币种为1
            if(acctEntity.getStandardCcy().equals(dto.getoCcy())){
                prodValuationDto.setExRate(BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP));
            }
            else{
                if(prodValuationDto.getbAmtCost()!=null
                    && prodValuationDto.getbAmtCost().compareTo(BigDecimal.ZERO) != 0
                    && prodValuationDto.getCost()!=null
                    && prodValuationDto.getCost().compareTo(BigDecimal.ZERO) != 0){
                    prodValuationDto.setExRate(
                            ArithmeticUtil.divide(
                                    prodValuationDto.getbAmtCost(),
                                    prodValuationDto.getCost()
                            ).setScale(4, RoundingMode.HALF_UP)
                    );
                }
            }
            //本币估值占净值比 -> 空
            prodValuationDto.setValuePercent(null);
            //本位币估值增值 -> 变量.本位币估值增值
            prodValuationDto.setValueIncrement(ArithmeticUtil.equals(BigDecimal.ZERO, 本币估值增值) ? null : 本币估值增值);
            //影子价格估值 -> 变变量.影子价格估值*汇率 -> 变空时保持为空
            prodValuationDto.setShadowPriceValue(
                    ArithmeticUtil.equals(BigDecimal.ZERO, 影子价格估值) ? null : 影子价格估值);
            //上级科目号
            prodValuationDto.setFsubjectId(dto.getFsubjectNo());
            //余额方向
            prodValuationDto.setBalFlag(dto.getBalFlag());
            //科目级别
            prodValuationDto.setSubjectLevel(dto.getSubjectLevel());
            //科目类型
            prodValuationDto.setSubjectType(dto.getSubjectType());
            //收益类型
            prodValuationDto.setProfitType(profitType);
            // 行情价格设值
            prodValuationDto.setPrice(行情价格);
            // 行情日期 -> 变量.行情日期
            prodValuationDto.setMarketDate(marketDate);
            // 向上汇总方式 -> 变量.向上汇总方式
            prodValuationDto.setRollUpType(rollUpType);

            result.getProdValuationDtoList().add(prodValuationDto);
            balanceSummarize(prodValuationDto, dto.getFsubjectNo(), 科目余额集合, 科目集合,
                result.getProdValuationDtoList(), isNotXshz);
        }
        Collections.sort(result.getProdValuationDtoList(), new Comparator<ProdValuationDto>() {

            @Override
            public int compare(ProdValuationDto var1, ProdValuationDto var2) {
                return var1.getSubjectNo().compareTo(var2.getSubjectNo());
            }
        });

        List<ProdValuationDto> sumList = new ArrayList<>();
        // 账套核算参数查询 份额、万份收益、年化收益率小数位及尾数控制
        List<String> codeList = new ArrayList<>();
        codeList.add(ValDictCodeList.账套配置表参数项_份额小数位位数.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_份额小数位尾数处理.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_万份收益小数位位数.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_万份收益小数位尾数处理.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_收益率小数位位数.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_收益率小数位处理方式.getValue());
        
        
        /**ADD BY WUJIE ON 20190822 START**/
        codeList.add(ValDictCodeList.账套配置表参数项_产品费后估值小数位数_估值表展示.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_产品费后估值小数尾数处理方式_估值表展示.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_产品费前估值小数位数_估值表展示.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_产品费前估值小数尾数处理方式_估值表展示.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_周期年化收益率小数位数_估值表展示.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_周期年化收益率小数尾数处理方式_估值表展示.getValue());
        
        codeList.add(ValDictCodeList.账套配置表参数项_累计单位净值小数位数_估值表展示.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_累计单位净值小数尾数_估值表展示.getValue());
        
        codeList.add(ValDictCodeList.账套配置表参数项_累积万份收益小数位数_估值表展示.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_累积万份收益小数尾数_估值表展示.getValue());
        
        codeList.add(ValDictCodeList.账套配置表参数项_本日年化收益率小数位数_估值表展示.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_本日年化收益率小数尾数_估值表展示.getValue());
        
        codeList.add(ValDictCodeList.账套配置表参数项_7日年化收益率小数位数_估值表展示.getValue());
        codeList.add(ValDictCodeList.账套配置表参数项_7日年化收益率小数尾数_估值表展示.getValue());
        
        /**ADD BY WUJIE ON 20190822 END**/
        
        /**ADD BY WUJIE ON 20190907 START**/
        codeList.add(ValDictCodeList.账套配置表参数项_昨日单位净值_估值日期类型.getValue());
        /**ADD BY WUJIE ON 20190907 END**/
        
        Map<String, Object> bokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
            booksetId, codeList);
        
        String showAfterDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费后估值小数位数_估值表展示.getValue());
        String showAfterDecType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费后估值小数尾数处理方式_估值表展示.getValue());
        String showBeforeDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费前估值小数位数_估值表展示.getValue());
        String showBeforeDecType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费前估值小数尾数处理方式_估值表展示.getValue());
        String showCyclyearYieldDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_周期年化收益率小数位数_估值表展示.getValue());
        String showCyclyearYieldType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_周期年化收益率小数尾数处理方式_估值表展示.getValue());
        
        String showTotbounsDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_累计单位净值小数位数_估值表展示.getValue());
        String showTotbounsDecType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_累计单位净值小数尾数_估值表展示.getValue());
      
        
        String showAfterDecSumDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_累积万份收益小数位数_估值表展示.getValue());
        String showAfterDecSumType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_累积万份收益小数尾数_估值表展示.getValue());

        String tdyYeildDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本日年化收益率小数位数_估值表展示.getValue());
        String tdyYeildDecType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本日年化收益率小数尾数_估值表展示.getValue());
        
        String day7YeildDecDigits = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_7日年化收益率小数位数_估值表展示.getValue());
        String day7YeildDecType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_7日年化收益率小数尾数_估值表展示.getValue());
        
        /**ADD BY WUJIE ON 20190907 START**/
        String valuationDateType = null;
        if(bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_昨日单位净值_估值日期类型.getValue())!=null){
        	valuationDateType = (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_昨日单位净值_估值日期类型.getValue());
        }
        /**ADD BY WUJIE ON 20190907 END**/
        
        // 定义 份额小数位数
        int shareDecDigits = 0;
        // 定义 年化收益率小数位
        int yeildDigits = 0;
        // 定义 份额尾数处理
        String shareDecType = "";
        // 定义 年化收益率小数位尾数控制
        String yeildDecType = "";
        if (StringUtil.isNotEmpty(
            bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_份额小数位位数.getValue()))) {
            shareDecDigits = Integer.valueOf(
                (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_份额小数位位数.getValue()));
        }
        if (StringUtil.isNotEmpty(
            bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_份额小数位尾数处理.getValue()))) {
            shareDecType = (String)bokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_份额小数位尾数处理.getValue());
        }
        if (StringUtil.isNotEmpty(
            bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_万份收益小数位位数.getValue()))) {
        }
        if (StringUtil.isNotEmpty(
            bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_万份收益小数位尾数处理.getValue()))) {
        }
        if (StringUtil.isNotEmpty(
            bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_收益率小数位位数.getValue()))) {
            yeildDigits = Integer.valueOf(
                (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_收益率小数位位数.getValue()));
        }
        if (StringUtil.isNotEmpty(
            bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_收益率小数位处理方式.getValue()))) {
            yeildDecType = (String)bokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_收益率小数位处理方式.getValue());
        }
        // 定义 份额尾数处理方式 默认为舍位
        RoundingMode shareMode = RoundingMode.DOWN;
        // 定义 份额尾数处理方式 默认为四舍五入
        RoundingMode yeildMode = RoundingMode.DOWN;
        // 份额尾数
        if (StringUtil.equals(DictCodeList.估值舍入规则_四舍五入.getValue(), shareDecType)) {
            shareMode = RoundingMode.HALF_UP;
        }
        else if (StringUtil.equals(DictCodeList.估值舍入规则_舍位.getValue(), shareDecType)) {
            shareMode = RoundingMode.DOWN;
        }
        else if (StringUtil.equals(DictCodeList.估值舍入规则_进位.getValue(), shareDecType)) {
            shareMode = RoundingMode.UP;
        }
        // 年化收益率尾数
        if (StringUtil.equals(DictCodeList.估值舍入规则_四舍五入.getValue(), yeildDecType)) {
            yeildMode = RoundingMode.HALF_UP;
        }
        else if (StringUtil.equals(DictCodeList.估值舍入规则_舍位.getValue(), yeildDecType)) {
            yeildMode = RoundingMode.DOWN;
        }
        else if (StringUtil.equals(DictCodeList.估值舍入规则_进位.getValue(), yeildDecType)) {
            yeildMode = RoundingMode.UP;
        }

        // 4. 实收资本、损益平准金未实现、损益平准金已实现科目的数据处理
        // 4.1 实收资本查询
        params.put("baseSubjectNature00", DictCodeBaseSubjectNatrueNew.实收资本.getValue());
        List<ValSchemeBatchDto> listRealCapital = valSchemeMapper.selectValSchemeDataRealCapital(
            params);
        ProdValuationDto actRecAmount = new ProdValuationDto();
        actRecAmount.setSubjectNo("实收资本：");
        // 收益类型 = 变量.收益类型
        actRecAmount.setProfitType(profitType);
        if (listRealCapital.size() > 0) {
            ValSchemeBatchDto dto = listRealCapital.get(0);

            //数量 <- 变量.科目明细.数量余额（当前为非分层，依然用非分层数量设值逻辑）
            actRecAmount.setNum(dto.getNumAmt());
            // 小数位处理
            if (shareDecDigits != 0) {
                //本币成本 <- 变量.科目明细.本币余额
                actRecAmount.setbAmtCost(ArithmeticUtil.round(dto.getbAmt(), shareDecDigits, shareMode));
                // 本币估值 <- 变量.科目明细.本币余额 *汇率
                actRecAmount.setbAmtValue(ArithmeticUtil.multiply(shareDecDigits, shareMode, dto.getbAmt(), 汇率集合.get(dto.getbCcy())));

            }
            else{
                //本币成本 <- 变量.科目明细.本币余额
                actRecAmount.setbAmtCost(dto.getbAmt());
                // 本币估值 <- 变量.科目明细.本币余额 *汇率
                actRecAmount.setbAmtValue(ArithmeticUtil.multiply(dto.getbAmt(), 汇率集合.get(dto.getbCcy())));
            }
            // 单位成本 <- 1
            actRecAmount.setUnitCost(BigDecimal.ONE);
            // 本币估值增值 <- 0
            actRecAmount.setValueIncrement(BigDecimal.ZERO);
            // 影子价格估值 <- 0
            actRecAmount.setShadowPriceValue(BigDecimal.ZERO);
        }

        result.setActRecAmount(actRecAmount);
        sumList.add(actRecAmount);

        
        // 5.资产类合计、负债类合计等合计项目统计
        // 出参.结构集.资产类合计 初始化
        ProdValuationDto assetTotal = new ProdValuationDto("资产类合计：", profitType);
        result.setAssetTotal(assetTotal);
        sumList.add(assetTotal);
        // 出参.结构集.负债类合计 初始化
        ProdValuationDto debtTotal = new ProdValuationDto("负债类合计：", profitType);
        result.setDebtTotal(debtTotal);
        sumList.add(debtTotal);

        for (ProdValuationDto dto : result.getProdValuationDtoList()) {
            if (dto.getSubjectLevel() == 1) {
                if (StringUtil.equals(dto.getSubjectType(), DictCodeList.科目类型_资产类.getValue())
                    || (StringUtil.equals(dto.getSubjectType(),
                        DictCodeList.科目类型_资产负债共同类.getValue()) && BigDecimal.ZERO.compareTo(
                            dto.getbAmtCost()) <= 0)) {
                    // 资产类合计：
                    assetTotal.addUpdate(dto);
                }
                else if (StringUtil.equals(dto.getSubjectType(),
                    DictCodeList.科目类型_负债类.getValue())) {
                    // 负债类合计：
                    debtTotal.addUpdate(dto);
                }
                else if (StringUtil.equals(dto.getSubjectType(),
                    DictCodeList.科目类型_资产负债共同类.getValue())
                         && BigDecimal.ZERO.compareTo(dto.getbAmtCost()) > 0) {
                    // 负债类合计：
                    debtTotal.subUpdate(dto);
                }
            }

        }
        
        //add by tanxuan 20200102 start
        params.put("vatType", DictCodeList.增值税类型_价差收入.getValue());
        params.put("taxPType", DictCodeList.增值税_确认类型_发生计税.getValue());
        TaxBokBalanceSum dtoInvIncomeTax = valSchemeMapper.selectInvIncomeTax(params);
        if((deductInvIncomeTax == null || BigDecimal.ZERO.compareTo(deductInvIncomeTax) <= 0) && StringUtil.isNotEmpty(dtoInvIncomeTax)){
        	deductInvIncomeTax = dtoInvIncomeTax.getTaxAmtSum().setScale(2, RoundingMode.HALF_UP);
        }
        
        ProdValuationDto deductInvestIncomeTax = new ProdValuationDto();
        // 科目代码 <- "待抵扣投资收益税："
        deductInvestIncomeTax.setSubjectNo("待抵扣投资收益税： ");
        // 科目名称 <- 变量.待抵扣投资收益税：
        deductInvestIncomeTax.setSubjectName(
            (deductInvIncomeTax == null) ? BigDecimal.ZERO.toString() : deductInvIncomeTax.toString());
        result.setDeductInvestIncomeTax(deductInvestIncomeTax);
        
        params.put("vatType", DictCodeList.增值税类型_价差收入.getValue());
        params.put("taxPType", DictCodeList.增值税_确认类型_预估计税.getValue());
        TaxBokBalanceSum dtoProEstInvIncomeTax = valSchemeMapper.selectInvIncomeTax(params);
        if((extDeductInvIncomeTax == null || BigDecimal.ZERO.compareTo(extDeductInvIncomeTax) <= 0) && StringUtil.isNotEmpty(dtoProEstInvIncomeTax)){
        	extDeductInvIncomeTax = dtoProEstInvIncomeTax.getTaxAmtSum().setScale(2, RoundingMode.HALF_UP);
        }
        ProdValuationDto extDeductInvestIncomeTax = new ProdValuationDto();
        // 科目代码 <- "待抵扣暂估投资收益税："
        extDeductInvestIncomeTax.setSubjectNo("待抵扣暂估投资收益税：");
        // 科目名称 <- 变量.待抵扣暂估投资收益税：
        extDeductInvestIncomeTax.setSubjectName(
            (extDeductInvIncomeTax == null) ? BigDecimal.ZERO.toString() : extDeductInvIncomeTax.toString());
        result.setExtDeductInvestIncomeTax(extDeductInvestIncomeTax);
        //add by tanxuan 20200102 end

        if (DictCodeList.收益类型_收益型.getValue().equals(profitType)) {
            BokStatBokPositionDto position = new BokStatBokPositionDto();
            // 查询日开始
            position.setSdateStart(bookDate);
            // 查询结束日
            position.setSdateEnd(bookDate);
            // 入账日
            position.setBookDate(bookDate);
            // 账套
            position.setBooksetId(booksetId);
            // 分层代码 = 分层代码
            position.setLayeringId(acctEntity.getLayeringId());
            // 调用账务类共通【帐套资产负债查询】
            // 变量.当日账套汇总信息 = 帐套资产负债信息列表.get(0)
            BokStatBokPosition tdyBokPosition = new BokStatBokPosition();
            // List<BokStatBokPosition> listPostion =
            // accountingCommonService.getValStatBokPosition(bookDate, bookDate, bookDate,
            // booksetId, null, null);
            List<BokStatBokPosition> listPostion = accountingCommonService.getValStatBokPosition(
                position);
            if (!CollectionUtils.isEmpty(listPostion)) {
                tdyBokPosition = listPostion.get(0);
            }

            // 出参.结构集.本日客户收益 设置
            ProdValuationDto tdyCustProfit = new ProdValuationDto();
            tdyCustProfit.setSubjectNo("当日客户收益：");
            // 本币成本 <- 当日账套汇总信息.应付利润
            tdyCustProfit.setbAmtCost(tdyBokPosition.getProfitAmt());
            // 本币估值 <- 当日账套汇总信息.应付利润
            tdyCustProfit.setbAmtValue(tdyBokPosition.getProfitAmt());
            result.setTdyCustProfit(tdyCustProfit);

            // 出参.结构集.累计客户收益 设置
            ProdValuationDto totalCustProfit = new ProdValuationDto();
            totalCustProfit.setSubjectNo("累计客户收益：");
            // 本币成本 <- 当日账套汇总信息.累计应付利润
            totalCustProfit.setbAmtCost(tdyBokPosition.getTotProfitAmt());
            // 本币估值 <- 当日账套汇总信息.累计应付利润
            totalCustProfit.setbAmtValue(tdyBokPosition.getTotProfitAmt());
            result.setTotalCustProfit(totalCustProfit);

            // 出参.结构集.本日客户收益率 设置
            ProdValuationDto tdyCustProfitRate = new ProdValuationDto();
            tdyCustProfitRate.setSubjectNo("当日客户收益率%：");
            // 母产品没有利率
            if (StringUtil.isNotEmpty(tdyBokPosition.getIntRate())) {
                // 本币成本 <- 当日账套汇总信息.计提利率
            	BigDecimal intRate = ArithmeticUtil.multiply(
                        yeildDigits, yeildMode, tdyBokPosition.getIntRate(),
                        new BigDecimal(100));
                tdyCustProfitRate.setbAmtCost(intRate);
                // 本币估值 <- 当日账套汇总信息.计提利率
                tdyCustProfitRate.setbAmtValue(intRate);
            }
            result.setTdyCustProfitRate(tdyCustProfitRate);

            // 定义 资产净值 = 资产类合计.本币市值 - 负债类合计.本币市值
            BigDecimal prodAsset = ArithmeticUtil.doSub(assetTotal.getbAmtValue(),
                debtTotal.getbAmtValue());

            // 科目明细列表
            for (ProdValuationDto dto : result.getProdValuationDtoList()) {
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }

            for (ProdValuationDto dto : sumList) {
                //  本币成本占净值比 = 对应表样. 本币成本/出参.结构集.产品资产净值.成本 保留四位小数
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                //  本币市值占净值比 = 对应表样. 本币估值/出参.结构集.产品资产净值.成本保留四位小数
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }
        }
        // if（变量.收益类型 = "净值型"）
        else if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
            // 4.2 损益平淮金查询
            params.put("baseSubjectNature01", DictCodeBaseSubjectNatrueNew.损益平准金_已实现.getValue());
            params.put("baseSubjectNature02", DictCodeBaseSubjectNatrueNew.损益平准金_未实现.getValue());
            List<ValSchemeBatchDto> listPL = valSchemeMapper.selectValSchemeDataProfitAndLoss(
                params);
            // 出参.结构集.实收资本/损益平准金已实现/损益平准金未实现 赋值如下
            ProdValuationDto profLossRealAmount = new ProdValuationDto("损益平准金已实现：", profitType);
            result.setProfLossRealAmount(profLossRealAmount);
            sumList.add(profLossRealAmount);
            profLossRealAmount = new ProdValuationDto("损益平准金未实现：", profitType);
            result.setProfLossUnrealAmount(profLossRealAmount);
            sumList.add(profLossRealAmount);
            for (ValSchemeBatchDto dto : listPL) {
                if (StringUtil.equals(dto.getBaseSubjectNature(),
                    DictCodeBaseSubjectNatrueNew.损益平准金_已实现.getValue())) {
                    profLossRealAmount = result.getProfLossRealAmount();
                }
                else {
                    profLossRealAmount = result.getProfLossUnrealAmount();
                }

                //数量 <- 变量.科目明细.数量余额 （当前为非分层，依然用非分层数量设值逻辑）
                profLossRealAmount.setNum(dto.getNumAmt());
                //本币成本 <- 变量.科目明细.本币余额
                profLossRealAmount.setbAmtCost(dto.getbAmt());
                // 本币估值 <- 变量.科目明细.本币余额 *汇率
                profLossRealAmount.setbAmtValue(ArithmeticUtil.multiply(dto.getbAmt(), 汇率集合.get(dto.getbCcy())));

                // 单位成本 <- 1
                profLossRealAmount.setUnitCost(BigDecimal.ONE);
                // 本币估值增值 <- 0
                profLossRealAmount.setValueIncrement(BigDecimal.ZERO);
                // 影子价格估值 <- 0
                profLossRealAmount.setShadowPriceValue(BigDecimal.ZERO);
            }
            
            
            // 查询日期准备：
            // 定义 变量.查询日列表
            List<Date> queryDates = new ArrayList<>();
            
            // if（变量.产品信息 != null && 变量.产品信息.交易日历 != null）
            Date ystday = null;
            if(valuationDateType!=null && valuationDateType.equals("01")){
            	ystday = DateUtil.addDay(bookDate, -1);
            }else{
            	// 变量.产品信息 = 调用标的类共通【核算主体查询（账套代码）】
                FinProduct prodInfo = finProductCommonService.queryAcctSubject(booksetId);
                if (prodInfo != null && StringUtils.isNotEmpty(prodInfo.getCalendarId())) {
                   ystday = calculationCommonService.getCDate(bookDate, prodInfo.getCalendarId());
                   if (ystday == null) {
                   	// 昨日 = 估值表处理结果集.入账日 - 1
                       ystday = DateUtil.addDay(bookDate, -1);
                   }
                }
                else {
                   // 昨日 = 估值表处理结果集.入账日 - 1
                   ystday = DateUtil.addDay(bookDate, -1);
                }
             }
            // 将上述日期组装成变量.查询日列表
            queryDates.add(ystday);
            // 入账日
            queryDates.add(bookDate);
            BokStatBokPositionDto position = new BokStatBokPositionDto();
            // 入账日
            position.setBookDate(bookDate);
            // 账套
            position.setBooksetId(booksetId);
            // 业务明细代码
            position.setQueryDates(queryDates);
            // 分层代码 = 分层代码
            position.setLayeringId(acctEntity.getLayeringId());
            // 调用账务类共通【帐套资产负债查询】
            List<BokStatBokPosition> listBokStatBokPosition = accountingCommonService.getValStatBokPosition(
                position);
            // 定义 账套汇总信息map(Key：发生日，VALUE:帐套资产负债信息)
            Map<Date, BokStatBokPosition> bokStatBokPositionMap = new HashMap<>();
            // 将上述返回帐套资产负债信息列表组装成 账套汇总信息map(Key：发生日，VALUE:帐套资产负债信息)
            for (BokStatBokPosition item : listBokStatBokPosition) {
                bokStatBokPositionMap.put(item.getBookDate(), item);
            }

            // 变量.产品累计单位净值 = 账套汇总信息map.get（入账日）.累计分红净值 + 账套汇总信息map.get（入账日）.单位净值

            BigDecimal prodTotUnitNet = (bokStatBokPositionMap.get(bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getTotBounsNet();
            
            prodTotUnitNet = ArithmeticUtil.doAdd(
            		accountingCommonService.filterByDecTypeAndDigits(prodTotUnitNet, showTotbounsDecType, showTotbounsDecDigits),
            		accountingCommonService.filterByDecTypeAndDigits(bokStatBokPositionMap.get(bookDate) == null ? 
            				BigDecimal.ZERO:bokStatBokPositionMap.get(bookDate).getNetUnitValue(), 
            				showAfterDecType, showAfterDecDigits)
            		);    
                
            prodTotUnitNet = accountingCommonService.filterByDecTypeAndDigits(prodTotUnitNet,
            		showTotbounsDecType,showTotbounsDecDigits);
            
            
            // 定义 昨日单位净值 = 账套汇总信息map.get（昨日）.单位净值
            BigDecimal ystNetUnitValue = BigDecimal.ZERO;
            if (bokStatBokPositionMap.get(ystday) != null) {
                ystNetUnitValue = bokStatBokPositionMap.get(ystday).getNetUnitValue();
            }
            ystNetUnitValue = accountingCommonService.filterByDecTypeAndDigits(ystNetUnitValue,showAfterDecType,showAfterDecDigits);
            
            
            
            
            // 账套汇总信息map.get（入账日）.资产净值
            BigDecimal nowNetAssetValue = (bokStatBokPositionMap.get(bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getNetAssetValue();
            //账套汇总信息map.get（入账日）.费前资产净值
            BigDecimal nowNetAssetValueBef = (bokStatBokPositionMap.get(bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getNetAssetValueBef();
            //当日浮动管理费计提金额查询 =  账套汇总信息map.get（入账日）.费前资产净值 - 账套汇总信息map.get（入账日）.资产净值 ;
            BigDecimal tdyFloatAcctAmt = ArithmeticUtil.subtract(nowNetAssetValueBef,nowNetAssetValue);
            
            // 出参.结构集.产品资产净值-费前 设置
            ProdValuationDto netAssetValueBef = new ProdValuationDto();
            netAssetValueBef.setSubjectNo("产品资产净值-费前：");
            // 成 本	<- 账套汇总信息map.get（入账日）.费前资产净值
            netAssetValueBef.setbAmtCost(ArithmeticUtil.doAdd(ArithmeticUtil.subtract(result.getAssetTotal().getbAmtCost(),
                    result.getDebtTotal().getbAmtCost()),tdyFloatAcctAmt));

            // 估 值 <- 账套汇总信息map.get（入账日）.费前资产净值
            netAssetValueBef.setbAmtValue((bokStatBokPositionMap.get(
                    bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getNetAssetValueBef());
            result.setNetAssetValueBef(netAssetValueBef);
            
            // 出参.结构集.产品资产净值-费后 设置
            ProdValuationDto netAssetValue = new ProdValuationDto();
            netAssetValue.setSubjectNo("产品资产净值-费后：");
            // 本币成本	<- 出参.结构集.资产类合计.本币成本 - 出参.结构集.负债类合计.本币成本
            netAssetValue.setbAmtCost(ArithmeticUtil.subtract(result.getAssetTotal().getbAmtCost(), result.getDebtTotal().getbAmtCost()));
            // 本币估值 <- 出参.结构集.资产类合计.本币估值 - 出参.结构集.负债类合计.本币估值
            netAssetValue.setbAmtValue(ArithmeticUtil.subtract(result.getAssetTotal().getbAmtValue(), result.getDebtTotal().getbAmtValue()));
            result.setNetAssetValue(netAssetValue);
            
            
            // 出参.结构集.产品单位净值-费前 设置
            ProdValuationDto netUnitValueBef = new ProdValuationDto();
            netUnitValueBef.setSubjectNo("产品单位净值-费前：");
            // 科目名称	<- 账套汇总信息map.get（入账日）.费前单位净值
            netUnitValueBef.setSubjectName(String.valueOf(accountingCommonService.filterByDecTypeAndDigits((bokStatBokPositionMap.get(
                    bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getNetUnitValueBef(),
                    showBeforeDecType, showBeforeDecDigits)));
            result.setNetUnitValueBef(netUnitValueBef);
            
            
            ProdValuationDto prodUnitVal = new ProdValuationDto();
            prodUnitVal.setSubjectNo("产品单位净值-费后：");
            // 科目名称 <- 变量.资产净值/出参.结构集.实收资本.本币成本
            BigDecimal prodUnitValValue = ArithmeticUtil.divide(netAssetValue.getbAmtValue(),result.getActRecAmount().getbAmtCost());
            prodUnitValValue = accountingCommonService.filterByDecTypeAndDigits(prodUnitValValue,showAfterDecType, showAfterDecDigits);
            prodUnitVal.setSubjectName(String.valueOf(prodUnitValValue));
            result.setProdUnitVal(prodUnitVal);
            
            
            // 出参.结构集.产品累计单位净值 设置
            ProdValuationDto prodTotalUnitPrice = new ProdValuationDto();
            // 科目代码 <-"产品累计单位净值"
            prodTotalUnitPrice.setSubjectNo("产品累计单位净值：");
            // 科目名称 <- 变量.产品累计单位净值
            prodTotalUnitPrice.setSubjectName(String.valueOf(prodTotUnitNet));
            result.setProdTotalUnitPrice(prodTotalUnitPrice);

            // 出参.结构集.昨日单位净值: 设置
            ProdValuationDto ystUnitPrice = new ProdValuationDto();
            // 科目代码 <-"昨日单位净值"
            ystUnitPrice.setSubjectNo("昨日单位净值：");
            // 科目名称 <- 账套汇总信息map.get（昨日）.单位净值 查询不到时为空
            if (bokStatBokPositionMap.get(ystday) != null) {
                ystUnitPrice.setSubjectName(String.valueOf(ystNetUnitValue));
            }
            else {
                ystUnitPrice.setSubjectName(BigDecimal.ZERO.toString());
            }
            result.setYstUnitPrice(ystUnitPrice);
            
            
            // 出参.结构集.偏离金额： 设置
            ProdValuationDto deviationAmount = new ProdValuationDto();
            // 科目代码 <- "偏离金额："
            deviationAmount.setSubjectNo("偏离金额：");
            // 科目名称 <- 变量.偏离金额
            deviationAmount.setSubjectName(deviationAmt.setScale(NumberEnum.MONEY.getCode(),
                RoundingMode.HALF_UP).toString());
            result.setDeviationAmount(deviationAmount);

            // 出参.结构集.偏离度： 设置
            ProdValuationDto deviationDegree = new ProdValuationDto();
            // 科目代码 <- "偏离度："
            deviationDegree.setSubjectNo("偏离度%：");
            // 科目名称 <- 变量.偏离金额/出参.结构集.产品资产净值.本币估值 * 100 保留4位小数
            deviationDegree.setSubjectName(ArithmeticUtil.multiply(4, RoundingMode.HALF_UP,
                ArithmeticUtil.divide(deviationAmt, netAssetValue.getbAmtValue()),
                new BigDecimal(100)).toString());
            result.setDeviationDegree(deviationDegree);
            
            // 出参.结构集.周期年化收益率： 设置
            ProdValuationDto yieldTerm = new ProdValuationDto();
            yieldTerm.setSubjectNo("周期年化收益率：");
            // 科目名称	<- 账套汇总信息map.get（入账日）.周期年化收益率
            yieldTerm.setSubjectName(String.valueOf(accountingCommonService.filterByDecTypeAndDigits((bokStatBokPositionMap.get(
                    bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getYieldTerm(),
                    showCyclyearYieldType, showCyclyearYieldDigits)));
            result.setYieldTerm(yieldTerm);
            
            // 6.净值占比处理
            // 对于以下表样数据中占比计算
            // 科目明细列表
            for (ProdValuationDto dto : result.getProdValuationDtoList()) {
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), netAssetValue.getbAmtValue()),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), netAssetValue.getbAmtValue()),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }
            // 实收资本\损益平准金未实现\损益平准金已实现\ 资产类合计\负债类合计\产品资产净值
            // 证券投资合计\其中股票投资\其中债券投资\其中基金投资\其中权证投资\融资业务合计\融券业务合计\今日可用头寸
            for (ProdValuationDto dto : sumList) {
                // 成本占净值比 = 对应表样.本币成本/出参.结构集.产品资产净值.成本 保留四位小数
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), netAssetValue.getbAmtValue()),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                // 市值占净值比 = 对应表样.本币估值/出参.结构集.产品资产净值.成本保留四位小数
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), netAssetValue.getbAmtValue()),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }
        }
        else if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
        	ProdValuationDto prodAssetVal = new ProdValuationDto();
            prodAssetVal.setSubjectNo("产品资产净值：");
            prodAssetVal.setbAmtCost(ArithmeticUtil.doSub(assetTotal.getbAmtCost(), debtTotal.getbAmtCost()));
            prodAssetVal.setbAmtValue(
                ArithmeticUtil.doSub(assetTotal.getbAmtValue(), debtTotal.getbAmtValue()));
            result.setProdAssetVal(prodAssetVal);
            sumList.add(prodAssetVal);
        	
            ProdValuationDto prodUnitVal = new ProdValuationDto();
            prodUnitVal.setSubjectNo("产品单位净值：");
            // 科目名称 <- 变量.资产净值/出参.结构集.实收资本.本币成本
            BigDecimal prodUnitValValue = ArithmeticUtil.divide(prodAssetVal.getbAmtValue(),result.getActRecAmount().getbAmtCost());
            prodUnitValValue = accountingCommonService.filterByDecTypeAndDigits(prodUnitValValue,showAfterDecType, showAfterDecDigits);
            prodUnitVal.setSubjectName(String.valueOf(prodUnitValValue));
            result.setProdUnitVal(prodUnitVal);
            
            // 出参.结构集.偏离金额： 设置
            ProdValuationDto deviationAmount = new ProdValuationDto();
            // 科目代码 <- "偏离金额："
            deviationAmount.setSubjectNo("偏离金额：");
            // 科目名称 <- 变量.偏离金额
            deviationAmount.setSubjectName(deviationAmt.setScale(NumberEnum.MONEY.getCode(),
                RoundingMode.HALF_UP).toString());
            result.setDeviationAmount(deviationAmount);

            // 出参.结构集.偏离度： 设置
            ProdValuationDto deviationDegree = new ProdValuationDto();
            // 科目代码 <- "偏离度："
            deviationDegree.setSubjectNo("偏离度%：");
            // 科目名称 <- 变量.偏离金额/出参.结构集.产品资产净值.本币估值 * 100 保留4位小数
            deviationDegree.setSubjectName(ArithmeticUtil.multiply(4, RoundingMode.HALF_UP,
                ArithmeticUtil.divide(deviationAmt, prodAssetVal.getbAmtValue()),
                new BigDecimal(100)).toString());
            result.setDeviationDegree(deviationDegree);
            BokStatBokPositionDto position = new BokStatBokPositionDto();
            // 查询日开始
            position.setSdateStart(bookDate);
            // 查询结束日
            position.setSdateEnd(bookDate);
            // 入账日
            position.setBookDate(bookDate);
            // 账套
            position.setBooksetId(booksetId);
            // 分层代码 = 分层代码
            position.setLayeringId(acctEntity.getLayeringId());
            // 调用账务类共通【帐套资产负债查询】
            // 变量.当日账套汇总信息 = 帐套资产负债信息列表.get(0)
            BokStatBokPosition tdyBokPosition = new BokStatBokPosition();
            List<BokStatBokPosition> listPostion = accountingCommonService.getValStatBokPosition(
                position);
            if (!CollectionUtils.isEmpty(listPostion)) {
                tdyBokPosition = listPostion.get(0);
            }

            // 出参.结构集.产品本日收益-费前： 设置
            ProdValuationDto tdyIncomeBef = new ProdValuationDto();
            // 科目代码 <- "产品本日收益-费前："
            tdyIncomeBef.setSubjectNo("产品本日收益-费前：");
            // 科目名称 <- 变量.当日账套汇总信息.费前资产净值 - 变量.当日账套汇总信息.实收资本
            tdyIncomeBef.setSubjectName(String.valueOf(ArithmeticUtil.doSub(tdyBokPosition.getNetAssetValueBef(),tdyBokPosition.getCapital()).setScale(NumberEnum.MONEY.getCode(),
                    RoundingMode.HALF_UP)));
            result.setTdyIncomeBef(tdyIncomeBef);
            // add by jiangpeng on 20210427 start
            // TISHKBS-1998 【估值核算-估值表】货币型产品估值表中的“本日收益-费后”统计的是当日应付利润发生额，但是并未将手工账的应付利润发生额统计进去
            Map<String, Object> param = new HashMap<>();
            param.put("booksetId", position.getBooksetId());
            param.put("cdFlag", DictCodeList.借贷方向_贷.getValue());
            param.put("bookDate", position.getBookDate());
            BigDecimal vouchAmt = accountingCommonService.getVouchAmt(param);
            BigDecimal profitAmt = ArithmeticUtil.round(ArithmeticUtil.doAdd(vouchAmt, tdyBokPosition.getProfitAmt()), 2, RoundingMode.HALF_UP);
            // add by jiangpeng on 20210427 end
            // 出参.结构集.产品本日收益： 设置
            ProdValuationDto tdyProdProfit = new ProdValuationDto();
            // 科目代码 <- "产品本日收益："
            tdyProdProfit.setSubjectNo("产品本日收益-费后：");
            // 科目名称 <- 变量.当日账套汇总信息.应付利润
            // add by modify on 20210427 start
            // TISHKBS-1998 【估值核算-估值表】货币型产品估值表中的“本日收益-费后”统计的是当日应付利润发生额，但是并未将手工账的应付利润发生额统计进去
            tdyProdProfit.setSubjectName(StringUtil.isNotEmpty(profitAmt) ? profitAmt.toString() : BigDecimal.ZERO.toString());
            // add by modify on 20210427 end
            result.setTdyProdProfit(tdyProdProfit);

            
            // 出参.结构集.万份收益-费前： 设置
            ProdValuationDto pYieldBef = new ProdValuationDto();
            // 科目代码 <- "产品本日收益-费前："
            pYieldBef.setSubjectNo("万份收益-费前：");
            // 科目名称 <- 变量.当日账套汇总信息.万份收益_费前
            pYieldBef.setSubjectName(String.valueOf(accountingCommonService.filterByDecTypeAndDigits(tdyBokPosition.getpYieldBef(),
                    showBeforeDecType, showBeforeDecDigits)));
            result.setpYieldBef(pYieldBef);
            
            // 出参.结构集.万份收益： 设置
            ProdValuationDto pYield = new ProdValuationDto();
            // 科目代码 <- "产品本日收益："
            pYield.setSubjectNo("万份收益-费后： ");
            // 科目名称 <- 变量.当日账套汇总信息.万份收益：
            BigDecimal prodPYield = (tdyBokPosition.getpYield() == null) ?BigDecimal.ZERO:tdyBokPosition.getpYield();
            prodPYield = accountingCommonService.filterByDecTypeAndDigits(prodPYield,
            		showAfterDecType, showAfterDecDigits);
            pYield.setSubjectName(prodPYield.toString());
            result.setPYield(pYield);
            
            // 出参.结构集.本年累积万份收益： 设置
            ProdValuationDto pYieldYear = new ProdValuationDto();
            // 科目代码 <- "本年累积万份收益："
            pYieldYear.setSubjectNo("本年累积万份收益：");
            // 科目名称 <- 查询.本年累积万份收益
            // 去年末
            Date lastYearEnd = DateUtil.addDay(DateUtil.getFirstDayOfYear(bookDate), -1);
            BigDecimal prodPYieldYear = valSchemeMapper.getPYieldSumSince(booksetId, bookDate, lastYearEnd);
            if (prodPYieldYear != null) {
            	prodPYieldYear = accountingCommonService.filterByDecTypeAndDigits(prodPYieldYear,
            			showAfterDecSumType, showAfterDecSumDigits);
            	pYield.setSubjectName(prodPYieldYear.toString());
            }
            else {
                pYieldYear.setSubjectName(BigDecimal.ZERO.toString());
            }
            result.setPYieldYear(pYieldYear);
            
            // 出参.结构集.从成立以来累计万份收益: 设置
            ProdValuationDto pYieldSince = new ProdValuationDto();
            // 科目代码 <- "从成立以来累计万份收益:"
            pYieldSince.setSubjectNo("从成立以来累计万份收益:");
            // 科目名称 <- 查询.从成立以来累计万份收益
            BigDecimal prodPYieldSince = valSchemeMapper.getPYieldSumSince(booksetId, bookDate, null);
            if (prodPYieldSince != null) {
            	prodPYieldSince = accountingCommonService.filterByDecTypeAndDigits(prodPYieldSince,
            			showAfterDecSumType, showAfterDecSumDigits);
            	pYieldSince.setSubjectName(prodPYieldSince.toString());
            }
            else {
                pYieldSince.setSubjectName(BigDecimal.ZERO.toString());
            }
            result.setPYieldSince(pYieldSince);
            
            // 出参.结构集.本日年化收益率： 设置
            ProdValuationDto tdyYield = new ProdValuationDto();
            // 科目代码 <- "本日年化收益率："
            tdyYield.setSubjectNo("本日年化收益率%： ");
            // 科目名称 <- 变量.当日账套汇总信息.当日年化收益率
            BigDecimal prodTdyYield = ArithmeticUtil.multiply((tdyBokPosition.getTdyYield() == null)?BigDecimal.ZERO:tdyBokPosition.getTdyYield(),new BigDecimal(100));
            prodTdyYield = accountingCommonService.filterByDecTypeAndDigits(prodTdyYield,
            		tdyYeildDecType, tdyYeildDecDigits);
            tdyYield.setSubjectName(prodTdyYield.toString());
            result.setTdyYield(tdyYield);
            
            
            // 出参.结构集.7日年化收益率： 设置
            ProdValuationDto yield7 = new ProdValuationDto();
            // 科目代码 <- "7日年化收益率："
            yield7.setSubjectNo("7日年化收益率%： ");
            // 科目名称 <- 变量.当日账套汇总信息.当日7日年化收益率
            BigDecimal prodYield7 =ArithmeticUtil.multiply((tdyBokPosition.getYield7() == null) ? BigDecimal.ZERO:tdyBokPosition.getYield7(),
            		new BigDecimal(100));
            prodYield7 = accountingCommonService.filterByDecTypeAndDigits(prodYield7,
            		day7YeildDecType, day7YeildDecDigits);
            yield7.setSubjectName(prodYield7.toString());
            result.setYield7(yield7);
            
            // 分层信息DTO查询
            ValLayeringInfo valLayeringInfo = valSchemeMapper.queryBokAcctLayeringInfo(bookDate, booksetId, acctEntity.getAcctSubjectId(), null);
            // 定义 变量.变量.成立以来年化收益率
            BigDecimal toTdyyieldDecimal = BigDecimal.ZERO;
            if(valLayeringInfo != null){
                // 计息基础天数
                BigDecimal basisDays = new BigDecimal(calculationCommonService.
                        calcInterestBasisDays(new InterestBasisDaysParameter(valLayeringInfo.getBasis())));

                BigDecimal calcDays = new BigDecimal(DateUtil.getBetweenDayNumber(valLayeringInfo.getvDate(), bookDate) + 1);

                toTdyyieldDecimal = ArithmeticUtil.divide(ArithmeticUtil.divide(ArithmeticUtil.multiply(
                        new BigDecimal(result.getpYieldSince().getSubjectName()),basisDays), calcDays),new BigDecimal(10000));
                toTdyyieldDecimal = accountingCommonService.filterByDecTypeAndDigits(toTdyyieldDecimal,showCyclyearYieldType, showCyclyearYieldDigits);
            }
            // 出参.结构集.成立以来年化收益率： 设置
            ProdValuationDto toTdyyield = new ProdValuationDto();
            // 科目代码 <- "从成立以来累计万份收益:"
            toTdyyield.setSubjectNo("成立以来年化收益率：");
            // 科目名称 <- 变量.成立以来年化收益率
            toTdyyield.setSubjectName(String.valueOf(toTdyyieldDecimal));
            result.setToTdyyield(toTdyyield);
            
            // 6.净值占比处理
            // 对于以下表样数据中占比计算
            // 科目明细列表
            for (ProdValuationDto dto : result.getProdValuationDtoList()) {
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), prodAssetVal.getbAmtValue()),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), prodAssetVal.getbAmtValue()),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }
            // 实收资本\损益平准金未实现\损益平准金已实现\ 资产类合计\负债类合计\产品资产净值
            // 证券投资合计\其中股票投资\其中债券投资\其中基金投资\其中权证投资\融资业务合计\融券业务合计\今日可用头寸
            for (ProdValuationDto dto : sumList) {
                // 成本占净值比 = 对应表样.本币成本/出参.结构集.产品资产净值.成本 保留四位小数
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), prodAssetVal.getbAmtValue()),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                // 市值占净值比 = 对应表样.本币估值/出参.结构集.产品资产净值.成本保留四位小数
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), prodAssetVal.getbAmtValue()),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }
        }
        // ====================================分层逻辑start================================
        // 若特殊核算模型不为空，则需要分层
        if (StringUtils.isNotEmpty(acctEntity.getBokSpecMode())) {
            List<String> listBaseSubjectNature = new ArrayList<>();
            listBaseSubjectNature.add(DictCodeBaseSubjectNatrueNew.实收资本.getValue());
            params.put("listBaseSubjectNature", listBaseSubjectNature);
            // 定义 变量.实收资本科目余额明细List
            List<ValSchemeBatchDto> listDetailRealCapital = valSchemeMapper.selectDetailRealCapital(
                params);
            // 定义 变量.分层信息List
            List<ProdValuationDto> listProdValDto = new ArrayList<>();
            // 定义 变量.分层代码List
            List<ProdValuationDto> listlayeringInfo = new ArrayList<>();
            // 定义 变量.四级科目尾号List ----防止重复录入第一层分层：
            // 例如产品A：，循环list的时候包含实收资本、损益平准金已实现和未实现，不加判断则会重复录入
            List<String> listDetailSubjectNoTail = new ArrayList<>();
            for (ValSchemeBatchDto item : listDetailRealCapital) {
                ProdValuationDto layerProd = new ProdValuationDto();
                // 顶部 分层产品简称：
                layerProd.setSubjectNo(item.getSubjectName() + "：");
                // 明细区分 <- 分层
                layerProd.setDetailDist(DictCodeList.估值表类型_分层.getValue());
                // 分层代码 <- 四级科目尾号
                layerProd.setLayeringId(item.getDetailSubjectNoTail());
                // 出参.结构集.实收资本分层列表.实收资本分层
                listProdValDto.add(layerProd);
                // 添加到变量.已录入分层的四级科目号尾号List
                listDetailSubjectNoTail.add(item.getDetailSubjectNoTail());
                listlayeringInfo.add(layerProd);
                // 子类：实收资本-分层
                ProdValuationDto actRecAmountLaying = new ProdValuationDto();
                actRecAmountLaying.setSubjectNo("实收资本-" + item.getSubjectName() + "：");
                // 明细区分 <- 实收资本
                actRecAmountLaying.setDetailDist(DictCodeList.估值表类型_实收资本.getValue());
                // 分层代码 <- 四级科目尾号
                actRecAmountLaying.setLayeringId(item.getDetailSubjectNoTail());

                //数量 <- 科目明细.本币余额（当前为分层，采用分层数量设值逻辑）
                actRecAmountLaying.setNum(item.getbAmt());
                // 小数位处理
                if (shareDecDigits != 0) {
                    //本币成本 <- 变量.科目明细.本币余额
                    actRecAmountLaying.setbAmtCost( ArithmeticUtil.round(item.getbAmt(), shareDecDigits, shareMode));
                    // 本币估值 <- 变量.科目明细.本币余额 *汇率
                    actRecAmountLaying.setbAmtValue(ArithmeticUtil.multiply(shareDecDigits, shareMode, item.getbAmt(), 汇率集合.get(item.getbCcy())));
                }
                else{
                    //本币成本 <- 变量.科目明细.本币余额
                    actRecAmountLaying.setbAmtCost(item.getbAmt());
                    // 本币估值 <- 变量.科目明细.本币余额 *汇率
                    actRecAmountLaying.setbAmtValue(ArithmeticUtil.multiply(item.getbAmt(), 汇率集合.get(item.getbCcy())));
                }
                // 单位成本 <- 1
                actRecAmountLaying.setUnitCost(BigDecimal.ONE);
                // 本币估值增值 <- 0
                actRecAmountLaying.setValueIncrement(BigDecimal.ZERO);
                // 影子价格估值 <- 0
                actRecAmountLaying.setShadowPriceValue(BigDecimal.ZERO);

                // 分层信息.add(实收资本分层)
                listProdValDto.add(actRecAmountLaying);
            }

            // if（变量.收益类型 = "净值型"）
            if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
                // 损益平准金查询
                listBaseSubjectNature.clear();
                listBaseSubjectNature.add(DictCodeBaseSubjectNatrueNew.损益平准金_未实现.getValue());
                listBaseSubjectNature.add(DictCodeBaseSubjectNatrueNew.损益平准金_已实现.getValue());
                // 变量.科目余额明细List
                params.put("listBaseSubjectNature", listBaseSubjectNature);
                // 损益平准金科目余额明细
                List<ValSchemeBatchDto> listDetailProfit = valSchemeMapper.selectDetailRealCapital(
                    params);
                for (ValSchemeBatchDto item : listDetailProfit) {
                    // DECODE(科目余额.余额方向,'贷方',科目余额.原币余额,-科目余额.原币余额)
                    if (DictCodeList.余额方向_借方.getValue().equals(item.getBalFlag())) {
                        item.setoAmt(
                            ArithmeticUtil.multiply(item.getoAmt(), new BigDecimal("-1")));
                    }
                    // 判断是否已经有过该条记录，防止重复录入
                    if (!listDetailSubjectNoTail.contains(item.getDetailSubjectNoTail())) {
                        ProdValuationDto layerProd = new ProdValuationDto();
                        // 顶部 分层产品简称：
                        layerProd.setSubjectNo(item.getSubjectName() + "：");
                        // 明细区分 <- 分层
                        layerProd.setDetailDist(DictCodeList.估值表类型_分层.getValue());
                        // 分层代码 <- 四级科目尾号
                        layerProd.setLayeringId(item.getDetailSubjectNoTail());
                        // 出参.结构集.实收资本分层列表.实收资本分层
                        listProdValDto.add(layerProd);
                        listDetailSubjectNoTail.add(item.getDetailSubjectNoTail());
                        listlayeringInfo.add(layerProd);
                    }
                    // 子类：实收资本/损益平准金未实现/损益平准金已实现-分层
                    ProdValuationDto actRecAmountLaying = new ProdValuationDto();
                    if (StringUtil.equals(DictCodeBaseSubjectNatrueNew.损益平准金_未实现.getValue(),
                        item.getBaseSubjectNature())) {
                        actRecAmountLaying.setSubjectNo("损益平准金未实现-" + item.getSubjectName() + "：");
                        // 明细区分 <-损益平准金未实现
                        actRecAmountLaying.setDetailDist(DictCodeList.估值表类型_损益平准金未实现.getValue());
                    }
                    else if (StringUtil.equals(DictCodeBaseSubjectNatrueNew.损益平准金_已实现.getValue(),
                        item.getBaseSubjectNature())) {
                        actRecAmountLaying.setSubjectNo("损益平准金已实现-" + item.getSubjectName() + "：");
                        // 明细区分 <-损益平准金已实现
                        actRecAmountLaying.setDetailDist(DictCodeList.估值表类型_损益平准金已实现.getValue());
                    }
                    // 分层代码 <- 四级科目尾号
                    actRecAmountLaying.setLayeringId(item.getDetailSubjectNoTail());

                    //数量 <-  <- 科目明细.本币余额（当前为分层，采用分层数量设值逻辑）
                    actRecAmountLaying.setNum(item.getbAmt());
                    //本币成本 <- 变量.科目明细.本币余额
                    actRecAmountLaying.setbAmtCost(item.getbAmt());
                    // 本币估值 <- 变量.科目明细.本币余额 *汇率
                    actRecAmountLaying.setbAmtValue(ArithmeticUtil.multiply(item.getbAmt(), 汇率集合.get(item.getbCcy())));

                    // 单位成本 <- 1
                    actRecAmountLaying.setUnitCost(BigDecimal.ONE);
                    // 本币估值增值 <- 0
                    actRecAmountLaying.setValueIncrement(BigDecimal.ZERO);
                    // 影子价格估值 <- 0
                    actRecAmountLaying.setShadowPriceValue(BigDecimal.ZERO);

                    listProdValDto.add(actRecAmountLaying);
                }
                // 分层产品资产净值、分层产品单位净值、分层累计单位净值设定
                // LOOP 变量.分层代码List
                for (ProdValuationDto dto : listlayeringInfo) {
                    // 查询日期准备：
                    // 定义 变量.查询日列表
                    List<Date> queryDates = new ArrayList<>();
                    
                    // if（变量.分层产品信息 != null && 变量.分层产品信息.交易日历 != null）
                    Date ystday = null;
                    //账套参数【VALUATION_DATETYPE】 ==  上一自然日
//                    if(valuationDateType!=null && valuationDateType.equals("01")){
                    if(ValDictCodeList.估值日期类型_上一自然日.getValue().equals(valuationDateType)){
                    	ystday = DateUtil.addDay(bookDate, -1);
                    }else{
                    	// 变量.分层产品信息 = 调用标的类共通【变量.分层代码.四级科目尾号】
                        FinProduct layeringProdInfo = finProductCommonService.queryAcctSubject(
                            dto.getLayeringId());
                        if (layeringProdInfo != null
                            && StringUtils.isNotEmpty(layeringProdInfo.getCalendarId())) {
                            ystday = calculationCommonService.getCDate(bookDate,
                                layeringProdInfo.getCalendarId());
                        }
                        else {
                            // 昨日 = 估值表处理结果集.入账日 - 1
                            ystday = DateUtil.addDay(bookDate, -1);
                        }
                    }
                    // 将上述日期组装成变量.查询日列表
                    queryDates.add(ystday);
                    // 入账日
                    queryDates.add(bookDate);
                    BokStatBokPositionDto position = new BokStatBokPositionDto();
                    // 入账日
                    position.setBookDate(bookDate);
                    // 账套
                    position.setBooksetId(booksetId);
                    // 查询日列表
                    position.setQueryDates(queryDates);
                    // 变量.分层代码.四级科目尾号
                    position.setLayeringId(dto.getLayeringId());
                    // 调用账务类共通【帐套资产负债查询】
                    List<BokStatBokPosition> listBokStatBokPosition = accountingCommonService.getValStatBokPosition(
                        position);
                    // 定义 账套汇总信息map(Key：发生日，VALUE:帐套资产负债信息)
                    Map<Date, BokStatBokPosition> bokStatBokPositionMap = new HashMap<>();
                    // 将上述返回帐套资产负债信息列表组装成 账套汇总信息map(Key：发生日，VALUE:帐套资产负债信息)
                    for (BokStatBokPosition item : listBokStatBokPosition) {
                        bokStatBokPositionMap.put(item.getBookDate(), item);
                    }
                    // 变量.分层产品累计单位净值 = 账套汇总信息map.get（入账日）.累计分红净值 + 账套汇总信息map.get（入账日）.单位净值
                    BigDecimal layerProdTotUnitNet = bokStatBokPositionMap.get(bookDate) == null ? 
                    		BigDecimal.ZERO:bokStatBokPositionMap.get(bookDate).getTotBounsNet();
                   
                    layerProdTotUnitNet = ArithmeticUtil.doAdd(
                    		accountingCommonService.filterByDecTypeAndDigits(layerProdTotUnitNet, showTotbounsDecType, showTotbounsDecDigits),
                    		accountingCommonService.filterByDecTypeAndDigits(bokStatBokPositionMap.get(bookDate) == null ? 
                    				BigDecimal.ZERO:bokStatBokPositionMap.get(bookDate).getNetUnitValue(), 
                    				showAfterDecType, showAfterDecDigits));
                    
                    
                    // 变量.分层资产净值 = 账套汇总信息map.get（入账日）.资产净值
                    BigDecimal layerNetAssetValue = (bokStatBokPositionMap.get(
                        bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(
                            bookDate).getNetAssetValue();
                    // 定义 变量.分层单位净值 = 账套汇总信息map.get（入账日）.单位净值
                    BigDecimal layerNetUnitValue = (bokStatBokPositionMap.get(
                        bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(
                            bookDate).getNetUnitValue();
                    // 舍位规则默认为null
                    RoundingMode roundingMode = null;
                    // 变量.小数位数
                    int valDecDigits = StringUtil.isNotEmpty(
                        acctEntity.getValDecDigits()) ? Integer.valueOf(
                            acctEntity.getValDecDigits()) : 4;
                    // 按照账套配置 估值小数位处理方式和估值小数位处理方式 处理小数位
                    if (StringUtil.equals(DictCodeList.估值舍入规则_四舍五入.getValue(),
                        acctEntity.getValDecType())) {
                        roundingMode = RoundingMode.HALF_UP;
                    }
                    else if (StringUtil.equals(DictCodeList.估值舍入规则_舍位.getValue(),
                        acctEntity.getValDecType())) {
                        roundingMode = RoundingMode.DOWN;
                    }
                    else if (StringUtil.equals(DictCodeList.估值舍入规则_进位.getValue(),
                        acctEntity.getValDecType())) {
                        roundingMode = RoundingMode.UP;
                    }
                    layerNetAssetValue = ArithmeticUtil.round(layerNetAssetValue, valDecDigits,
                        roundingMode);
                    
                    layerNetUnitValue = accountingCommonService.filterByDecTypeAndDigits(layerNetUnitValue, 
                    		showAfterDecType, showAfterDecDigits);
                    //==================================2019年9月11日11:32:56 魏鹏：之前设计变更遗漏start===========================
                    // 出参.结构集.分层数据列表.资产净值-费后 设置
                    ProdValuationDto netAssetValue = new ProdValuationDto();
                    // 科目代码 <-"产品资产净值-费后-变量.分层代码List.四级科目尾号："
                    netAssetValue.setSubjectNo("产品资产净值-费后-" + dto.getSubjectNo());
                    // 本币成本 <- 变量.分层资产净值
                    netAssetValue.setbAmtCost(layerNetAssetValue);
                    // 本币估值 <- 变量.分层资产净值
                    netAssetValue.setbAmtValue(layerNetAssetValue);
                    netAssetValue.setLayeringId(dto.getLayeringId());
                    netAssetValue.setDetailDist(DictCodeList.估值表类型_产品资产净值_费后.getValue());
                    listProdValDto.add(netAssetValue);
                    
                    // 出参.结构集.分层数据列表.产品单位净值-费后 设置
                    ProdValuationDto prodUnitVal = new ProdValuationDto();
                    // 科目代码 <-"产品单位资产净值-费后-变量.分层代码List.四级科目尾号："
                    prodUnitVal.setSubjectNo("产品单位净值-费后-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.分层单位净值
                    prodUnitVal.setSubjectName(String.valueOf(layerNetUnitValue));
                    prodUnitVal.setLayeringId(dto.getLayeringId());
                    prodUnitVal.setDetailDist(DictCodeList.估值表类型_产品单位净值_费后.getValue());
                    listProdValDto.add(prodUnitVal);
                    
                    // 出参.结构集.分层数据列表.产品资产净值-费前 设置
                    ProdValuationDto netAssetValueBef = new ProdValuationDto();
                    // 科目代码 <-"产品资产净值-费前-变量.分层代码List.四级科目尾号："
                    netAssetValueBef.setSubjectNo("产品资产净值-费前-" + dto.getSubjectNo());
                    // 本币成 本  <- 账套汇总信息map.get（入账日）.费前资产净值
                    netAssetValueBef.setbAmtCost((bokStatBokPositionMap.get(
                        bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getNetAssetValueBef());
                    // 本币估 值 <- 账套汇总信息map.get（入账日）.费前资产净值
                    netAssetValueBef.setbAmtValue((bokStatBokPositionMap.get(
                            bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getNetAssetValueBef());
                    netAssetValueBef.setLayeringId(dto.getLayeringId());
                    netAssetValueBef.setDetailDist(DictCodeList.估值表类型_产品资产净值_费前.getValue());
                    listProdValDto.add(netAssetValueBef);
                    
                    // 出参.结构集.分层数据列表.产品单位净值-费前 设置
                    ProdValuationDto netUnitValueBef = new ProdValuationDto();
                    netUnitValueBef.setSubjectNo("产品单位净值-费前-" + dto.getSubjectNo());
                    // 科目名称 <- 账套汇总信息map.get（入账日）.费前单位净值
                    netUnitValueBef.setSubjectName(String.valueOf(accountingCommonService.filterByDecTypeAndDigits((bokStatBokPositionMap.get(
                            bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getNetUnitValueBef(),
                            showBeforeDecType, showBeforeDecDigits)));
                    netUnitValueBef.setLayeringId(dto.getLayeringId());
                    netUnitValueBef.setDetailDist(DictCodeList.估值表类型_产品单位净值_费前.getValue());
                    listProdValDto.add(netUnitValueBef);
                    
                    // 出参.结构集.分层数据列表.资产净值 设置
//                    ProdValuationDto prodNetValue = new ProdValuationDto();
//                    // 科目代码 <-"产品资产净值-变量.分层代码List.四级科目尾部名称"
//                    prodNetValue.setSubjectNo("产品资产净值-" + dto.getSubjectNo());
//                    // 成本 <- 变量.分层资产净值
//                    prodNetValue.setbAmtCost(layerNetAssetValue);
//                    // 估值 <- 变量.分层资产净值
//                    prodNetValue.setbAmtValue(layerNetAssetValue);
//                    prodNetValue.setLayeringId(dto.getLayeringId());
//                    prodNetValue.setDetailDist(DictCodeList.估值表类型_产品资产净值.getValue());
//                    listProdValDto.add(prodNetValue);
                    
                    // 出参.结构集.周期年化收益率： 设置
                    ProdValuationDto yieldTerm = new ProdValuationDto();
                    yieldTerm.setSubjectNo("周期年化收益率-" + dto.getSubjectNo());
                    // 科目名称 <- 账套汇总信息map.get（入账日）.周期年化收益率
                    yieldTerm.setSubjectName(String.valueOf(accountingCommonService.filterByDecTypeAndDigits((bokStatBokPositionMap.get(
                            bookDate) == null) ? BigDecimal.ZERO : bokStatBokPositionMap.get(bookDate).getYieldTerm(),
                            showCyclyearYieldType, showCyclyearYieldDigits)));
                    yieldTerm.setLayeringId(dto.getLayeringId());
                    yieldTerm.setDetailDist(DictCodeList.估值表类型_周期年化收益率.getValue());
                    listProdValDto.add(yieldTerm);

//                    // 出参.结构集.分层数据列表.产品单位净值 设置
//                    ProdValuationDto prodUnitPrice = new ProdValuationDto();
//                    // 科目代码 <-"产品单位净值-变量.分层代码List.四级科目尾部名称："
//                    prodUnitPrice.setSubjectNo("产品单位净值-" + dto.getSubjectNo());
//                    // 科目名称 <- 变量.分层单位净值
//                    prodUnitPrice.setSubjectName(String.valueOf(layerNetUnitValue));
//                    prodUnitPrice.setLayeringId(dto.getLayeringId());
//                    prodUnitPrice.setDetailDist(DictCodeList.估值表类型_产品单位净值.getValue());
//                    listProdValDto.add(prodUnitPrice);
                    //==================================2019年9月11日11:32:56 魏鹏：之前设计变更遗漏end===========================
                    
                    // 出参.结构集.分层数据列表.资产净值 设置
                    ProdValuationDto prodTotalUnitPrice = new ProdValuationDto();
                    // 科目代码 <-"产品累计单位净值-变量.分层代码List.四级科目尾部名称："
                    prodTotalUnitPrice.setSubjectNo("产品累计单位净值-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.分层产品累计单位净值
                    prodTotalUnitPrice.setSubjectName(String.valueOf(layerProdTotUnitNet));
                    prodTotalUnitPrice.setLayeringId(dto.getLayeringId());
                    prodTotalUnitPrice.setDetailDist(DictCodeList.估值表类型_产品累计单位净值.getValue());
                    listProdValDto.add(prodTotalUnitPrice);

                    // 出参.结构集.昨日单位净值: 设置
                    ProdValuationDto ystUnitPrice = new ProdValuationDto();
                    // 科目代码 <-"昨日单位净值-变量.分层代码List.四级科目尾部名称："
                    ystUnitPrice.setSubjectNo("昨日单位净值-" + dto.getSubjectNo());
                    // 科目名称 <- 账套汇总信息map.get（昨日）.单位净值 查询不到时为空
                    if (bokStatBokPositionMap.get(ystday) != null) {
                        ystUnitPrice.setSubjectName(String.valueOf(accountingCommonService.filterByDecTypeAndDigits(bokStatBokPositionMap.get(ystday).getNetUnitValue(),
                    			showAfterDecType,showAfterDecDigits)));
                    }
                    else {
                        ystUnitPrice.setSubjectName(BigDecimal.ZERO.toString());
                    }
                    ystUnitPrice.setLayeringId(dto.getLayeringId());
                    ystUnitPrice.setDetailDist(DictCodeList.估值表类型_昨日单位净值.getValue());
                    listProdValDto.add(ystUnitPrice);
                }
            }
            else if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
                // 分层产品资产净值、分层产品单位净值、分层累计单位净值设定
                // LOOP 变量.分层代码List
                for (ProdValuationDto dto : listlayeringInfo) {
                    BokStatBokPositionDto position = new BokStatBokPositionDto();
                    // 查询日开始
                    position.setSdateStart(bookDate);
                    // 查询结束日
                    position.setSdateEnd(bookDate);
                    // 入账日
                    position.setBookDate(bookDate);
                    // 账套
                    position.setBooksetId(booksetId);
                    // 分层代码 = 分层代码
                    position.setLayeringId(dto.getLayeringId());
                    // 调用账务类共通【帐套资产负债查询】
                    // 变量.当日账套汇总信息 = 帐套资产负债信息列表.get(0)
                    BokStatBokPosition tdyBokPosition = new BokStatBokPosition();
                    List<BokStatBokPosition> listPostion = accountingCommonService.getValStatBokPosition(
                        position);
                    if (!CollectionUtils.isEmpty(listPostion)) {
                        tdyBokPosition = listPostion.get(0);
                    }
                    // 出参.结构集.产品本日收益： 设置
                    ProdValuationDto tdyProdProfit = new ProdValuationDto();
                    // 科目代码 <- "产品本日收益-变量.分层代码List.四级科目尾部名称："
                    tdyProdProfit.setSubjectNo("产品本日收益-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.应付利润
                    tdyProdProfit.setSubjectName(
                        (tdyBokPosition.getProfitAmt() == null) ? BigDecimal.ZERO.toString() : tdyBokPosition.getProfitAmt().toString());
                    tdyProdProfit.setLayeringId(dto.getLayeringId());
                    tdyProdProfit.setDetailDist(DictCodeList.估值表类型_产品本日收益_费后.getValue());
                    listProdValDto.add(tdyProdProfit);

                    // 出参.结构集.万份收益： 设置
                    ProdValuationDto pYield = new ProdValuationDto();
                    // 科目代码 <- "产品本日收益-变量.分层代码List.四级科目尾部名称："
                    pYield.setSubjectNo("万份收益-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.万份收益：
                    BigDecimal prodPYield = (tdyBokPosition.getpYield() == null) ?BigDecimal.ZERO:tdyBokPosition.getpYield();
                    prodPYield = accountingCommonService.filterByDecTypeAndDigits(prodPYield,
                    		showAfterDecType, showAfterDecDigits);
                    pYield.setSubjectName(prodPYield.toString());
                    pYield.setLayeringId(dto.getLayeringId());
                    pYield.setDetailDist(DictCodeList.估值表类型_万份收益_费后.getValue());
                    listProdValDto.add(pYield);
                    
                    // 出参.结构集.产品本日收益-费前： 设置
                    ProdValuationDto tdyIncomeBef = new ProdValuationDto();
                    // 科目代码 <- "产品本日收益-费前："
                    tdyIncomeBef.setSubjectNo("产品本日收益-费前-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.费前资产净值 - 变量.当日账套汇总信息.实收资本
                    tdyIncomeBef.setSubjectName(String.valueOf(ArithmeticUtil.doSub(tdyBokPosition.getNetAssetValueBef(),tdyBokPosition.getCapital()).setScale(NumberEnum.MONEY.getCode(),
                            RoundingMode.HALF_UP)));
                    tdyIncomeBef.setLayeringId(dto.getLayeringId());
                    tdyIncomeBef.setDetailDist(DictCodeList.估值表类型_产品本日收益_费前.getValue());
                    listProdValDto.add(tdyIncomeBef);
                    
                    // 出参.结构集.万份收益-费前： 设置
                    ProdValuationDto pYieldBef = new ProdValuationDto();
                    // 科目代码 <- 变量.当日账套汇总信息.万份收益_费前：
                    pYieldBef.setSubjectNo("万份收益-费前-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.应付利润 + 查询.当日浮动管理费计提金额
                    pYieldBef.setSubjectName(String.valueOf(accountingCommonService.filterByDecTypeAndDigits(tdyBokPosition.getpYieldBef(),
                            showBeforeDecType, showBeforeDecDigits)));
                    pYieldBef.setLayeringId(dto.getLayeringId());
                    pYieldBef.setDetailDist(DictCodeList.估值表类型_万份收益_费前.getValue());
                    listProdValDto.add(pYieldBef);

                    // 出参.结构集.本日年化收益率： 设置
                    ProdValuationDto tdyYield = new ProdValuationDto();
                    // 科目代码 <- "本日年化收益率-变量.分层代码List.四级科目尾部名称："
                    tdyYield.setSubjectNo("本日年化收益率%-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.当日年化收益率
                    BigDecimal prodTdyYield = (tdyBokPosition.getTdyYield() == null)?BigDecimal.ZERO:tdyBokPosition.getTdyYield();
                    prodTdyYield = accountingCommonService.filterByDecTypeAndDigits(prodTdyYield,
                    		tdyYeildDecType, tdyYeildDecDigits);
                    tdyYield.setSubjectName(prodTdyYield.toString());
                    
                    tdyYield.setLayeringId(dto.getLayeringId());
                    tdyYield.setDetailDist(DictCodeList.估值表类型_本日年化收益率.getValue());
                    listProdValDto.add(tdyYield);

                    // 出参.结构集.7日年化收益率： 设置
                    ProdValuationDto yield7 = new ProdValuationDto();
                    // 科目代码 <- "7日年化收益率-变量.分层代码List.四级科目尾部名称："
                    yield7.setSubjectNo("7日年化收益率%-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.当日7日年化收益率
                    BigDecimal prodYield7 =(tdyBokPosition.getYield7() == null) ? BigDecimal.ZERO:tdyBokPosition.getYield7();
                    prodYield7 = accountingCommonService.filterByDecTypeAndDigits(prodYield7,
                    		day7YeildDecType, day7YeildDecDigits);
                    yield7.setSubjectName(prodYield7.toString());
                    yield7.setLayeringId(dto.getLayeringId());
                    yield7.setDetailDist(DictCodeList.估值表类型_7日年化收益率.getValue());
                    listProdValDto.add(yield7);

                    // 出参.结构集.从成立以来累计万份收益: 设置
                    ProdValuationDto pYieldSince = new ProdValuationDto();
                    // 科目代码 <- "从成立以来累计万份收益:"
                    pYieldSince.setSubjectNo("从成立以来累计万份收益-" + dto.getSubjectNo());
                    // 科目名称 <- 查询.从成立以来累计万份收益
                    BigDecimal prodPYieldSince = valSchemeMapper.getLayerPYieldSumSince(booksetId, bookDate, null,
                            dto.getLayeringId());
                    if (prodPYieldSince!= null) {
                    	prodPYieldSince = accountingCommonService.filterByDecTypeAndDigits(prodPYieldSince,
                    			showAfterDecSumType, showAfterDecSumDigits);
                    	pYieldSince.setSubjectName(prodPYieldSince.toString());
                    }
                    else {
                        pYieldSince.setSubjectName(BigDecimal.ZERO.toString());
                    }
                    pYieldSince.setLayeringId(dto.getLayeringId());
                    pYieldSince.setDetailDist(DictCodeList.估值表类型_从成立以来累计万份收益.getValue());
                    listProdValDto.add(pYieldSince);

                    // 出参.结构集.本年累积万份收益： 设置
                    ProdValuationDto pYieldYear = new ProdValuationDto();
                    // 科目代码 <- "本年累积万份收益-变量.分层代码List.四级科目尾部名称："
                    pYieldYear.setSubjectNo("本年累积万份收益-" + dto.getSubjectNo());
                    // 科目名称 <- 查询.本年累积万份收益
                    // 去年末
                    Date lastYearEnd = DateUtil.addDay(DateUtil.getFirstDayOfYear(bookDate), -1);
                    BigDecimal prodPYieldYear = valSchemeMapper.getLayerPYieldSumSince(booksetId, bookDate, lastYearEnd,
                            dto.getLayeringId());
                    if (prodPYieldYear!= null) {
                    	prodPYieldYear = accountingCommonService.filterByDecTypeAndDigits(prodPYieldYear,
                    			showAfterDecSumType, showAfterDecSumDigits);
                    	pYieldYear.setSubjectName(prodPYieldYear.toString());
                    }
                    else {
                        pYieldYear.setSubjectName(BigDecimal.ZERO.toString());
                    }
                    pYieldYear.setLayeringId(dto.getLayeringId());
                    pYieldYear.setDetailDist(DictCodeList.估值表类型_本年累积万份收益.getValue());
                    listProdValDto.add(pYieldYear);
                    
                    // 分层信息DTO查询
                    ValLayeringInfo valLayeringInfo = valSchemeMapper.queryBokAcctLayeringInfo(bookDate, booksetId, acctEntity.getAcctSubjectId(), dto.getLayeringId());
                    // 定义 变量.变量.成立以来年化收益率
                    BigDecimal toTdyyieldDecimal = BigDecimal.ZERO;
                    if(valLayeringInfo != null){
                        // 计息基础天数
                        BigDecimal basisDays = new BigDecimal(calculationCommonService.
                                calcInterestBasisDays(new InterestBasisDaysParameter(valLayeringInfo.getBasis())));

                        BigDecimal calcDays = new BigDecimal(DateUtil.getBetweenDayNumber(valLayeringInfo.getvDate(), bookDate) + 1);

                        toTdyyieldDecimal = ArithmeticUtil.divide(ArithmeticUtil.divide(ArithmeticUtil.multiply(
                                new BigDecimal(result.getpYieldSince().getSubjectName()),basisDays), calcDays),new BigDecimal(10000));
                        toTdyyieldDecimal = accountingCommonService.filterByDecTypeAndDigits(toTdyyieldDecimal,showCyclyearYieldType, showCyclyearYieldDigits);
                    }
                    // 出参.结构集.成立以来年化收益率： 设置
                    ProdValuationDto toTdyyield = new ProdValuationDto();
                    // 科目代码 <- "从成立以来累计万份收益:"
                    toTdyyield.setSubjectNo("成立以来年化收益率-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.成立以来年化收益率
                    toTdyyield.setSubjectName(String.valueOf(toTdyyieldDecimal));
                    toTdyyield.setLayeringId(dto.getLayeringId());
                    toTdyyield.setDetailDist(DictCodeList.估值表类型_成立以来年化收益率.getValue());
                    listProdValDto.add(toTdyyield);
                }
            } else if (DictCodeList.收益类型_收益型.getValue().equals(profitType)) {
            	// LOOP 变量.分层代码List
                for (ProdValuationDto dto : listlayeringInfo) {
                    BokStatBokPositionDto position = new BokStatBokPositionDto();
                    // 查询日开始
                    position.setSdateStart(bookDate);
                    // 查询结束日
                    position.setSdateEnd(bookDate);
                    // 入账日
                    position.setBookDate(bookDate);
                    // 账套
                    position.setBooksetId(booksetId);
                    // 分层代码 = 分层代码
                    position.setLayeringId(dto.getLayeringId());
                    // 调用账务类共通【帐套资产负债查询】
                    // 变量.当日账套汇总信息 = 帐套资产负债信息列表.get(0)
                    BokStatBokPosition tdyBokPosition = new BokStatBokPosition();
                    List<BokStatBokPosition> listPostion = accountingCommonService.getValStatBokPosition(
                        position);
                    if (!CollectionUtils.isEmpty(listPostion)) {
                        tdyBokPosition = listPostion.get(0);
                    }

                    // 出参.结构集.本日客户收益 设置
                    ProdValuationDto tdyCustProfit = new ProdValuationDto();
                    tdyCustProfit.setSubjectNo("当日客户收益-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.应付利润
                    tdyCustProfit.setSubjectName(
                        (tdyBokPosition.getProfitAmt() == null) ? BigDecimal.ZERO.toString() : tdyBokPosition.getProfitAmt().toString());
                    tdyCustProfit.setLayeringId(dto.getLayeringId());
                    tdyCustProfit.setDetailDist(DictCodeList.估值表类型_当日客户收益.getValue());
                    listProdValDto.add(tdyCustProfit);

                    // 出参.结构集.累计客户收益 设置
                    ProdValuationDto totalCustProfit = new ProdValuationDto();
                    totalCustProfit.setSubjectNo("累计客户收益-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.应付利润
                    totalCustProfit.setSubjectName(
                        (tdyBokPosition.getTotProfitAmt() == null) ? BigDecimal.ZERO.toString() : tdyBokPosition.getTotProfitAmt().toString());
                    totalCustProfit.setLayeringId(dto.getLayeringId());
                    totalCustProfit.setDetailDist(DictCodeList.估值表类型_累计客户收益.getValue());
                    listProdValDto.add(totalCustProfit);

                    // 出参.结构集.本日客户收益率 设置
                    ProdValuationDto tdyCustProfitRate = new ProdValuationDto();
                    tdyCustProfitRate.setSubjectNo("当日客户收益率%-" + dto.getSubjectNo());
                    BigDecimal intRate = BigDecimal.ZERO;
                    // 母产品没有利率
                    if (StringUtil.isNotEmpty(tdyBokPosition.getIntRate())) {
                        // 成 本 <- 当日账套汇总信息.计提利率
                    	intRate = ArithmeticUtil.multiply(
                                yeildDigits, yeildMode, tdyBokPosition.getIntRate(),
                                new BigDecimal(100));
                        tdyCustProfitRate.setbAmtCost(intRate);
                        // 估 值 <- 当日账套汇总信息.计提利率
                        tdyCustProfitRate.setbAmtValue(intRate);
                    }
                    // 科目名称 <- 变量.当日账套汇总信息.应付利润
                    tdyCustProfitRate.setSubjectName(intRate.toString());
                    tdyCustProfitRate.setLayeringId(dto.getLayeringId());
                    tdyCustProfitRate.setDetailDist(DictCodeList.估值表类型_当日客户收益率.getValue());
                    listProdValDto.add(tdyCustProfitRate);
                }
            }
            result.setLayeredProdValDtoList(listProdValDto);
        }
        // ====================================分层逻辑end================================

        result.setProfitType(profitType);
        result.setBookDate(bookDate);
        result.setBooksetId(booksetId);
        result.setBooksetName(acctEntity.getBooksetName());
        result.setFinprodName(acctEntity.getFinprodName());
        // 是否生成净值 确认（下发）
        String netConfirmGennerate = CacheUtil.getSysParam("NET_CONFIRM_GENNERATE");
        if (DictCodeList.是否_是.getValue().equals(netConfirmGennerate)) {
            // 净值确认（下发）
            this.generateNetConfirm(result);
        }
        return result;
    }

    private void balanceSummarize(ProdValuationDto prodValuationDto, String subjectNo,
                                  HashMap<String, ProdValuationDto> 科目余额集合,
                                  Map<String, BokSubject> 科目集合, List<ProdValuationDto> 科目明细列表,
                                  boolean isNotXshz) {

        BokSubject 科目信息 = 科目集合.get(subjectNo);

        // if（"否" == 入参.表样明细.是否向上汇总）
        if (!isNotXshz) {
            return;
        }

        ProdValuationDto 本科目余额明细 = 科目余额集合.get(subjectNo);
        if (本科目余额明细 == null) {
            本科目余额明细 = new ProdValuationDto();
            本科目余额明细.setSubjectNo(科目信息.getSubjectNo());
            本科目余额明细.setSubjectName(科目信息.getSubjectName());
            本科目余额明细.setFsubjectId(科目信息.getFsubjectId());
            本科目余额明细.setBalFlag(prodValuationDto.getBalFlag());
            本科目余额明细.setNum(prodValuationDto.getNum());
            // 向上汇总方式 仅成本或全部:"入参.表样明细.数量/成本 else 0
            if(ValDictCodeList.估值表向上汇总方式_仅成本.getValue().equals(prodValuationDto.getRollUpType())
                    ||ValDictCodeList.估值表向上汇总方式_全部.getValue().equals(prodValuationDto.getRollUpType())){
                本科目余额明细.setNum(prodValuationDto.getNum());
                本科目余额明细.setbAmtCost(prodValuationDto.getbAmtCost());
            }
            else{
                本科目余额明细.setNum(BigDecimal.ZERO);
                本科目余额明细.setbAmtCost(BigDecimal.ZERO);
            }
            本科目余额明细.setUnitCost(null);
            // 向上汇总方式 仅市值或全部:"入参.表样明细.估值else 0
            if(ValDictCodeList.估值表向上汇总方式_仅市值.getValue().equals(prodValuationDto.getRollUpType())
                    ||ValDictCodeList.估值表向上汇总方式_全部.getValue().equals(prodValuationDto.getRollUpType())){
                本科目余额明细.setbAmtValue(prodValuationDto.getbAmtValue());
            }
            else{
                本科目余额明细.setbAmtValue(BigDecimal.ZERO);
            }
            本科目余额明细.setValueIncrement(prodValuationDto.getValueIncrement());
            本科目余额明细.setShadowPriceValue(prodValuationDto.getShadowPriceValue());
            本科目余额明细.setSubjectLevel(科目信息.getSubjectLevel());
            本科目余额明细.setSubjectType(科目信息.getSubjectType());
            科目余额集合.put(subjectNo, 本科目余额明细);
            科目明细列表.add(本科目余额明细);
        }
        else {
            if (本科目余额明细.getNum() == null && prodValuationDto.getNum() == null) {
                本科目余额明细.setNum(null);
            }
            else {
                // 数    量 += 向上汇总方式 仅成本或全部:入参.表样明细.数量else 0
                if(ValDictCodeList.估值表向上汇总方式_仅成本.getValue().equals(prodValuationDto.getRollUpType())
                        ||ValDictCodeList.估值表向上汇总方式_全部.getValue().equals(prodValuationDto.getRollUpType())){
                    本科目余额明细.setNum(ArithmeticUtil.doAdd(本科目余额明细.getNum(), prodValuationDto.getNum()));
                }
                else{
                    本科目余额明细.setNum(ArithmeticUtil.doAdd(本科目余额明细.getNum(), BigDecimal.ZERO));
                }
            }
            // 成本 += 向上汇总方式 仅成本或全部:入参.表样明细.成本else 0
            if(ValDictCodeList.估值表向上汇总方式_仅成本.getValue().equals(prodValuationDto.getRollUpType())
                    ||ValDictCodeList.估值表向上汇总方式_全部.getValue().equals(prodValuationDto.getRollUpType())){
                本科目余额明细.setbAmtCost(ArithmeticUtil.doAdd(本科目余额明细.getbAmtCost(), prodValuationDto.getbAmtCost()));
            }
            else{
                本科目余额明细.setbAmtCost(ArithmeticUtil.doAdd(本科目余额明细.getbAmtCost(), BigDecimal.ZERO));
            }
            本科目余额明细.setUnitCost(null);
            // 估值 += 向上汇总方式 仅成本或全部:入参.表样明细.估值else 0
            if(ValDictCodeList.估值表向上汇总方式_仅市值.getValue().equals(prodValuationDto.getRollUpType())
                    ||ValDictCodeList.估值表向上汇总方式_全部.getValue().equals(prodValuationDto.getRollUpType())){
                本科目余额明细.setbAmtValue(
                        ArithmeticUtil.doAdd(本科目余额明细.getbAmtValue(), prodValuationDto.getbAmtValue()));
            }
            else{
                本科目余额明细.setbAmtValue(
                        ArithmeticUtil.doAdd(本科目余额明细.getbAmtValue(), BigDecimal.ZERO));
            }
            本科目余额明细.setValueIncrement(ArithmeticUtil.doAdd(本科目余额明细.getValueIncrement(),
                    prodValuationDto.getValueIncrement()));
            本科目余额明细.setShadowPriceValue(ArithmeticUtil.doAdd(本科目余额明细.getShadowPriceValue(),
                    prodValuationDto.getShadowPriceValue()));
            if (ArithmeticUtil.equals(BigDecimal.ZERO, 本科目余额明细.getValueIncrement())) {
                本科目余额明细.setValueIncrement(null);
            }
            if (ArithmeticUtil.equals(BigDecimal.ZERO, 本科目余额明细.getShadowPriceValue())) {
                本科目余额明细.setShadowPriceValue(null);
            }
        }
        if (StringUtil.isNotEmpty(科目信息.getFsubjectId())) {
            balanceSummarize(prodValuationDto, 科目信息.getFsubjectId(), 科目余额集合, 科目集合, 科目明细列表,
                    isNotXshz);
        }
    }

    /**
     * 净值确认（下发）
     * 
     * @param result 估值表数据
     * @see
     * @since 1.0
     */
    private void generateNetConfirm(ValSchemeGenerateResult result) {
        // 定义 估值表余额表状态表
        BokValbalTableStatus bokValbalTableStatus = new BokValbalTableStatus();
        // 金融产品代码
        bokValbalTableStatus.setFinprodId(result.getBooksetId());
        String profitType = result.getProfitType();
        // 金融产品名称
        bokValbalTableStatus.setFinprodName(result.getFinprodName());
        // 估值日期
        bokValbalTableStatus.setValDate(result.getBookDate());
        // 理财份额
        bokValbalTableStatus.setCapital(result.getActRecAmount().getbAmtCost());
        if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
            // 产品总净值/本日收益 = 产品资产净值
            //MODIFY JINMAODONG BUG 修复：空指针
//            bokValbalTableStatus.setAssetValue((result.getProdAssetVal().getMarketValue()));
            bokValbalTableStatus.setAssetValue(result.getNetAssetValue().getbAmtValue());
            // 单位净值/万份收益/利率 = 产品单位净值
            bokValbalTableStatus.setNetUnitValue(
                new BigDecimal(result.getProdUnitVal().getSubjectName()));
            // 七日年化收益率 = 7日年化收益率
            bokValbalTableStatus.setSevenDayRate(BigDecimal.ZERO);
            // 累计净值 = 累计单位净值
            bokValbalTableStatus.setTotalNetValue(
                new BigDecimal(result.getProdTotalUnitPrice().getSubjectName()));
            // 应付利润 = 0
            bokValbalTableStatus.setPayProfit(BigDecimal.ZERO);
        }
        else if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
            // 产品总净值/本日收益 = 产品本日收益
            /*bokValbalTableStatus.setAssetValue(result.getTdyProdProfit().getCost());*/
            bokValbalTableStatus.setAssetValue(new BigDecimal(result.getTdyProdProfit().getSubjectName()));
            // 单位净值/万份收益/利率 = 万份收益
            bokValbalTableStatus.setNetUnitValue(
                new BigDecimal(result.getPYield().getSubjectName()));
            // 七日年化收益率 = 7日年化收益率
            bokValbalTableStatus.setSevenDayRate(
                new BigDecimal(result.getYield7().getSubjectName()));
            //当日年化收益率
            bokValbalTableStatus.setTdyYield(new BigDecimal(result.getTdyYield().getSubjectName()));
            // 累计净值 = 累计单位净值
            bokValbalTableStatus.setTotalNetValue(BigDecimal.ZERO);
            // 应付利润 = 应付利润科目对应的值
            List<ProdValuationDto> listDto = result.getProdValuationDtoList();
            for(ProdValuationDto dto : listDto){
                if("22320101".equals(dto.getSubjectNo())){
                    bokValbalTableStatus.setPayProfit(dto.getbAmtValue());
                    break;
                }
            }
        }

        // 放入结果集中
        result.setBokValbalTableStatus(bokValbalTableStatus);

        // 对账状态--未对账
        bokValbalTableStatus.setCheckStatus(DictCodeList.对账状态_未对账.getValue());
        // 估值表余额表标志 = 文件类型_估值表
        bokValbalTableStatus.setValBalFlag(DictCodeList.文件类型_估值表.getValue());
        // 定义 估值表余额表生成日志表
        BokValbalTableLog bokValbalTableLog = new BokValbalTableLog();
        // 产品代码
        bokValbalTableLog.setFinprodId(bokValbalTableStatus.getFinprodId());
        // 生成状态
        bokValbalTableLog.setBuildStatus(DictCodeList.生成状态_生成成功.getValue());
        // 文件类型 = 估值表
        bokValbalTableLog.setValBalFlag(DictCodeList.文件类型_估值表.getValue());
        bokValbalTableLog.setValDate(bokValbalTableStatus.getValDate());
        // 放入返回共通结果集中
        result.setBokValbalTableLog(bokValbalTableLog);
    }

}

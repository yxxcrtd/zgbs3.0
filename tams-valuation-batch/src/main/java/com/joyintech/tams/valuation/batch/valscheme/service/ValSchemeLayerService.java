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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListNatureGroup;
import com.joyintech.tams.valuation.batch.common.accounting.entity.CorrespondSubjectEntity;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.valscheme.dao.ValSchemeMapper;
import com.joyintech.tams.valuation.batch.valscheme.dto.InvGruopDto;
import com.joyintech.tams.valuation.batch.valscheme.dto.ProdValuationDto;
import com.joyintech.tams.valuation.batch.valscheme.dto.ValSchemeBatchDto;
import com.joyintech.tams.valuation.batch.valscheme.dto.ValSchemeGenerateResult;
import com.joyintech.tams.valuation.batch.valscheme.dto.ValSchemeTodoDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.MarketPriceDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tools.collectors.JoyinCollectors;

import groovy.lang.Tuple2;

/**
 * 估值表分层生成service
 * 
 * @author yangzhi
 * @version 2.0 2018年5月3日
 * @see ValSchemeLayerService
 * @since 2.0
 */
@Service
public class ValSchemeLayerService extends CommonExportService<ValSchemeBatchDto> {

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
        throws Exception {
        // 获取reader返回值
        ValSchemeTodoDto acctEntity = (ValSchemeTodoDto)params1.get(GlobalConstant.READER_RESULT);

        // 进行批处理需要将结果集封装到result
        ValSchemeGenerateResult result = new ValSchemeGenerateResult();
        // // 成本
        // BigDecimal cost = BigDecimal.ZERO;
        // // 数量
        // BigDecimal num = BigDecimal.ZERO;
        // // 实收成本
        // BigDecimal realCost = BigDecimal.ZERO;
        // // 实收数量
        // BigDecimal realNum = BigDecimal.ZERO;
        // 损益平淮金未实现成本
        // BigDecimal unTrueCost = BigDecimal.ZERO;
        // 损益平淮金未实现数量
        // BigDecimal unTrueNum = BigDecimal.ZERO;
        // // 损益平淮金已实现成本
        // BigDecimal comeTrueCost = BigDecimal.ZERO;
        // // 损益平淮金已实现数量
        // BigDecimal comeTrueNum = BigDecimal.ZERO;
        // // 资产类成本
        // BigDecimal assetCost = BigDecimal.ZERO;
        // // 负债类成本
        // BigDecimal debtCost = BigDecimal.ZERO;

        // 定义全局变量-账套代码 booksetId
        String booksetId = acctEntity.getBooksetId();
        // 定义全局变量--入账日
        Date bookDate = acctEntity.getBookDate();
		//      List<String> 公允价值变动列表 = Arrays.asList(DictCodeBaseSubjectNatrue.综合收益类_公允价值变动.getValue(),
		//      DictCodeBaseSubjectNatrue.公允价值损益类_公允价值变动.getValue(),
		//      DictCodeBaseSubjectNatrue.公允价值变动.getValue(),
		//      DictCodeBaseSubjectNatrue.公允价值损益类_计划类资产_公允价值变动.getValue(),
		//          DictCodeBaseSubjectNatrue.基金投资_估值增值.getValue(),
		//          DictCodeBaseSubjectNatrue.股票_公允价值变动.getValue(),
		//          /** add by yanya on 20200220 增加期权买入、卖出公允变动 start**/
		//          DictCodeBaseSubjectNatrue.买入期权公允价值变动.getValue(),
		//          DictCodeBaseSubjectNatrue.卖出期权公允价值变动.getValue()
		//  );
		  //科目性质分组：分组代码_估值表_公允价值变动列表
		  List<String> 公允价值变动列表 = accountingCommonService.getValSubjectNatureListWithAcctStandardId(
		  		DictCodeListNatureGroup.分组代码_估值表_公允价值变动列表.getValue(),
		          DictCodeListExt2.会计准则_企业准则.getValue());
		  
		//  List<String> 利息调整列表 = Arrays.asList(DictCodeBaseSubjectNatrue.摊余成本类利息调整.getValue(),
		//      DictCodeBaseSubjectNatrue.综合收益类利息调整.getValue(),
		//      DictCodeBaseSubjectNatrue.计划类资产_利息调整.getValue());
		
		//科目性质分组：分组代码_估值表_利息调整列表
		  List<String> 利息调整列表 = accountingCommonService.getValSubjectNatureListWithAcctStandardId(
		  		DictCodeListNatureGroup.分组代码_估值表_利息调整列表.getValue(),
		          DictCodeListExt2.会计准则_企业准则.getValue());
        // 收益类型查询-共通：账套参数查询
        // List<String> paramCodeList = new ArrayList<>();
        // paramCodeList.add(ValDictCodeList.账套配置表参数项_收益类型.getValue());
        // List<BokBooksetExt> listBokBooksetExt = accountingCommonService.getValBokBooksetExt(
        // booksetId, paramCodeList);
        // 变量.收益类型
        // String profitType = (CollectionUtils.isEmpty(
        // listBokBooksetExt)) ? "" : listBokBooksetExt.get(0).getParamValue();
        // 变量.收益类型 = 估值表处理结果集.核算方式
        String profitType = acctEntity.getAcctMode();

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
            BigDecimal 汇率 = 汇率集合.get(dto.getoCcy());
            BigDecimal 行情价格 = null;
            BigDecimal 原币估值 = null;
            BigDecimal 本币估值 = null;
            BigDecimal 原币估值增值 = BigDecimal.ZERO;
            BigDecimal 本币估值增值 = BigDecimal.ZERO;
            // BigDecimal 影子价格估值 = BigDecimal.ZERO;
            BigDecimal 单位成本 = null;
            // 行情日期
            Date marketDate = null;
            // 是否向上汇总 默认为是
            boolean isNotXshz = true;
            // 向上汇总方式--默认为全部
            String rollUpType = ValDictCodeList.估值表向上汇总方式_全部.getValue();
            // 科目为0的数据剔除
            // if（变量.科目明细.原币余额 = 0）
            if (ArithmeticUtil.equals(dto.getoAmt(), BigDecimal.ZERO) && ArithmeticUtil.equals(dto.getbAmt(), BigDecimal.ZERO)) {
                continue;
            }

            // if（公允价值变动列表.包含(变量.科目明细.基础科目性质) and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
            // and 变量.科目明细.科目类型 != "共同类"）
//            if (公允价值变动列表.contains(dto.getBaseSubjectNature())
//                    && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())
//                    && !DictCodeList.科目类型_资产负债共同类.getValue().equals(dto.getSubjectType())) {
//                // continue
//                continue;
//            }
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
            if (StringUtil.equals(dto.getBaseSubjectNature(),
                DictCodeBaseSubjectNatrue.摊余成本类成本.getValue())
                && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{摊余成本类利息调整}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 利息调整科目数据 = accountingCommonService.getCorrespondSubject(
                    dto.getDetailSubjectNoTail(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrue.摊余成本类利息调整.getValue()));
                
                //add jinmaodong 20201019 减值相关设计变更 START
                // 变量.科目信息列表-摊余成本类资产减值 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{摊余成本类利息调整}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 资产减值科目数据 = accountingCommonService.getCorrespondSubject(
                    dto.getDetailSubjectNoTail(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue()));
                
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
//                else {
//                    单位净价 = 单位净价.setScale(NumberEnum.MONEY.getCode(), RoundingMode.HALF_UP);
//                }
                // 变量.估值 = 变量.科目明细.数量*变量.单位净价
                原币估值 = ArithmeticUtil.multiply(dto.getNumAmt(), 单位净价);
                行情价格 = 单位净价;
                if (利息调整明细 == null) {
                    // 变量.偏离金额 += 变量.科目明细.数量*变量.单位净价 - （ 变量.科目明细.原币余额）
                    deviationAmt = ArithmeticUtil.doAdd(deviationAmt, ArithmeticUtil.subtract(
                        ArithmeticUtil.multiply(dto.getNumAmt(), 单位净价), dto.getoAmt()));
                    // 变量.估值增值 = 变量.科目明细.数量*变量.单位净价 - （ 变量.科目明细.原币余额）
//                    本币估值增值 = ArithmeticUtil.doSub(ArithmeticUtil.multiply(dto.getNumAmt(), 单位净价),
//                        dto.getoAmt());

                    //变量.原币估值  =  变量.科目明细.原币余额
                    原币估值 = dto.getoAmt();
                    //变量.本币估值  =  变量.科目明细.本位币余额
                    本币估值 = dto.getbAmt();
                }
                else {
                    if (StringUtil.equals(利息调整明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                        // 影子价格估值 = ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null,
                        // dto.getNumAmt(), 单位净价 == null ? BigDecimal.ZERO : 单位净价);

                        // 变量.偏离金额 += 变量.科目明细.数量*变量.单位净价 - （ 变量.科目明细.原币余额 - 变量.利息调整明细.原币余额）
                        deviationAmt = ArithmeticUtil.doAdd(deviationAmt,
                            ArithmeticUtil.subtract(ArithmeticUtil.multiply(dto.getNumAmt(), 单位净价),
                                ArithmeticUtil.subtract(dto.getoAmt(), 利息调整明细.getoAmt())));

                        // 变量.估值增值 = 变量.科目明细.数量*变量.单位净价 - （ 变量.科目明细.原币余额 - 变量.利息调整明细.原币余额）
//                        本币估值增值 = ArithmeticUtil.doSub(ArithmeticUtil.multiply(dto.getNumAmt(), 单位净价),
//                            ArithmeticUtil.doSub(dto.getoAmt(), 利息调整明细.getoAmt()));

                        //变量.原币估值 =  变量.科目明细.原币余额 - 变量.利息调整明细.原币余额
                        原币估值 = ArithmeticUtil.doSub(dto.getoAmt(),利息调整明细.getoAmt());
                        //变量.本币估值 =  变量.科目明细.本位币余额 - 变量.利息调整明细.本位币余额
                        本币估值 = ArithmeticUtil.doSub(dto.getbAmt(),利息调整明细.getbAmt());
                    }
                    else {
                        // 影子价格估值 = ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null,
                        // dto.getNumAmt(), 单位净价 == null ? BigDecimal.ZERO : 单位净价);

                        // 变量.偏离金额 += 变量.科目明细.数量*变量.单位净价 - （ 变量.科目明细.原币余额 + 变量.利息调整明细.原币余额）
                        deviationAmt = ArithmeticUtil.doAdd(deviationAmt,
                            ArithmeticUtil.subtract(ArithmeticUtil.multiply(dto.getNumAmt(), 单位净价),
                                ArithmeticUtil.doAdd(dto.getoAmt(), 利息调整明细.getoAmt())));

                        // 变量.估值增值 = 变量.科目明细.数量*变量.单位净价 - （ 变量.科目明细.原币余额 + 变量.利息调整明细.原币余额）
//                        本币估值增值 = ArithmeticUtil.doSub(ArithmeticUtil.multiply(dto.getNumAmt(), 单位净价),
//                            ArithmeticUtil.doAdd(dto.getoAmt(), 利息调整明细.getoAmt()));

                        //变量.原币估值 =  变量.科目明细.原币余额 + 变量.利息调整明细.原币余额
                        原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),利息调整明细.getoAmt());
                        //变量.本币估值 =  变量.科目明细.本位币余额 + 变量.利息调整明细.本位币余额
                        本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),利息调整明细.getbAmt());
                    }

                }

                // 估值 = ArithmeticUtil.multiply(dto.getoAmt(), 汇率).setScale(2,
                // RoundingMode.HALF_UP);

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
                bokAssetMarketInfo.setInvAim(DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
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
                DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue())
                     && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{公允价值损益类-公允价值变动}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                        dto.getDetailSubjectNoTail(), booksetId,
                        Arrays.asList(DictCodeBaseSubjectNatrue.公允价值损益类_公允价值变动.getValue()));
                // 变量.公允价值明细 = 科目明细Map.get(变量.科目信息列表.get（0）.科目号,变量.科目明细.四级科目号尾号)
                ValSchemeBatchDto 公允价值明细 = null;
                if (!CollectionUtils.isEmpty(科目信息列表)) {
                    公允价值明细 = 科目明细集合.get(
                        科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                }
                // BigDecimal netPrice = finProductCommonService.getNetprice(bookDate, booksetId,
                // dto.getDetailSubjectNoTail());
                if (公允价值明细 != null) {
                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt().negate(), 汇率).setScale(4,
//                            RoundingMode.HALF_UP);

                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt().negate();
                        //变量.本币估值增值 = -变量.公允价值明细 .本位币余额
                        本币估值增值 = 公允价值明细.getbAmt().negate();

                    }
                    else {
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt(), 汇率).setScale(4,
//                            RoundingMode.HALF_UP);

                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt();
                        //变量.本币估值增值 = 变量.公允价值明细 .本位币余额
                        本币估值增值 = 公允价值明细.getbAmt();
                    }
                }

                //原币估值 = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(dto.getoAmt(), 汇率), 本币估值增值==null?BigDecimal.ZERO:本币估值增值);

                // 变量.原币估值 = 变量.科目明细.原币币余额 + 变量.原币估值增值（为空取0）
                原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
                // 变量.本币估值 = 变量.科目明细.本币余额 + 变量.本币估值增值（为空取0）
                本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));

                // 变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
                行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
            }
            // else if 变量.科目明细.基础科目性质 = "金融负债成本" and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
            else if(StringUtil.equals(dto.getBaseSubjectNature(),
                    DictCodeBaseSubjectNatrue.金融负债成本.getValue())
                    && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())){
                //变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{公允价值变动}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                        dto.getDetailSubjectNoTail(), booksetId,
                        Arrays.asList(DictCodeBaseSubjectNatrue.公允价值变动.getValue()));
                // 变量.公允价值明细 = 科目明细Map.get(变量.科目信息列表.get（0）.科目号,变量.科目明细.四级科目号尾号)
                ValSchemeBatchDto 公允价值明细 = null;
                if (!CollectionUtils.isEmpty(科目信息列表)) {
                    公允价值明细 = 科目明细集合.get(
                            科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                }
                if (公允价值明细 != null) {
                    // if（变量.公允价值明细 .余额方向 = "借方"）
                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_借方.getValue())) {
//                        // 变量.估值增值 = -变量.公允价值明细 .原币余额 *汇率
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt().negate(), 汇率).setScale(4,
//                                RoundingMode.HALF_UP);

                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt().negate();
                        //变量.本币估值增值 = -变量.公允价值明细 .本位币余额
                        本币估值增值 = 公允价值明细.getbAmt().negate();
                    }
                    else {
//                        // 变量.估值增值 = 变量.公允价值明细 .原币余额 *汇率
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt(), 汇率).setScale(4,
//                                RoundingMode.HALF_UP);

                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt();
                        //变量.本币估值增值 = 变量.公允价值明细 .本位币余额
                        本币估值增值 = 公允价值明细.getbAmt();
                    }
                }
                // 变量.估值 = 变量.科目明细.原币余额*汇率(保留2位系数) + 变量.估值增值（为空取0）
                //原币估值 = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(),
                //        null, dto.getoAmt(), 汇率), 本币估值增值 == null ? BigDecimal.ZERO : 本币估值增值);

                // 变量.原币估值 = 变量.科目明细.原币币余额 + 变量.原币估值增值（为空取0）
                原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
                // 变量.本币估值 = 变量.科目明细.本币余额 + 变量.本币估值增值（为空取0）
                本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));

                // 变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
                行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
            }
            else if (StringUtil.equals(dto.getBaseSubjectNature(),
                DictCodeBaseSubjectNatrue.综合收益类成本.getValue())
                     && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询】
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                    dto.getDetailSubjectNoTail(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrue.综合收益类利息调整.getValue(),
                        DictCodeBaseSubjectNatrue.综合收益类_公允价值变动.getValue()));
                ValSchemeBatchDto 利息调整明细 = null;
                ValSchemeBatchDto 公允价值明细 = null;
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{计划类资产-利息调整}，变量.科目明细.上级科目号）】

                for (CorrespondSubjectEntity correspondSubjectEntity : 科目信息列表) {
                    if (StringUtil.equals(correspondSubjectEntity.getBaseSubjectNature(),
                        DictCodeBaseSubjectNatrue.综合收益类利息调整.getValue())) {
                        利息调整明细 = 科目明细集合.get(
                            correspondSubjectEntity.getSubjectNo() + dto.getDetailSubjectNoTail());
                    }
                    else if (StringUtil.equals(correspondSubjectEntity.getBaseSubjectNature(),
                        DictCodeBaseSubjectNatrue.综合收益类_公允价值变动.getValue())) {
                        公允价值明细 = 科目明细集合.get(
                            correspondSubjectEntity.getSubjectNo() + dto.getDetailSubjectNoTail());
                    }

                }

                // 变量.成本 = 变量.科目明细.原币余额*汇率
//                BigDecimal 成本 = ArithmeticUtil.multiply(dto.getoAmt(), 汇率);
                // 变量.原币成本 = 变量.科目明细.原币余额
                BigDecimal 原币成本 = dto.getoAmt();
                // 变量.本币成本 = 变量.科目明细.本币余额
                BigDecimal 本币成本 = dto.getoAmt();
                if (利息调整明细 != null) {
                    if (StringUtil.equals(利息调整明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
//                        成本 = ArithmeticUtil.multiply(
//                            ArithmeticUtil.subtract(dto.getoAmt(), 利息调整明细.getoAmt()), 汇率);

                        //变量.原币成本 =  变量.科目明细.原币余额 - 变量.利息调整明细.原币余额
                        原币成本 = ArithmeticUtil.doSub(dto.getoAmt(),利息调整明细.getoAmt());
                        //变量.本币成本 =  变量.科目明细.本币余额 - 变量.利息调整明细.本币余额
                        本币成本 = ArithmeticUtil.doSub(dto.getbAmt(),利息调整明细.getbAmt());
                    }
                    else {
//                        成本 = ArithmeticUtil.multiply(
//                            ArithmeticUtil.doAdd(dto.getoAmt(), 利息调整明细.getoAmt()), 汇率);

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
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt().negate(), 汇率).setScale(2,
//                            RoundingMode.HALF_UP);

                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt().negate();
                        //变量.本币估值增值 = -变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt().negate();
                    }
                    else {
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt(), 汇率).setScale(2,
//                            RoundingMode.HALF_UP);

                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt();
                        //变量.本币估值增值 = 变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt();
                    }
                }
                //原币估值 = ArithmeticUtil.doAdd(成本, 本币估值增值);

                //变量.原币估值 = 变量.原币成本 + 变量.原币估值增值
                原币估值 = ArithmeticUtil.doAdd(原币成本,(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
                //变量.本币估值 = 变量.本币成本 + 变量.本币估值增值
                本币估值 = ArithmeticUtil.doAdd(本币成本,(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));

                if (公允价值明细 != null) {
                    //变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
                    行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
                }
            }
            else if (StringUtil.equals(dto.getBaseSubjectNature(),
                DictCodeBaseSubjectNatrue.基金投资_成本.getValue())
                     && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                    dto.getDetailSubjectNoTail(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrue.基金投资_估值增值.getValue()));
                ValSchemeBatchDto 公允价值明细 = CollectionUtils.isEmpty(科目信息列表) ? null : 科目明细集合.get(
                    科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                if (公允价值明细 != null) {
                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt().negate(), 汇率).setScale(4,
//                            RoundingMode.HALF_UP);

                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt().negate();
                        //变量.本币估值增值 = -变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt().negate();

                    }
                    else {
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt(), 汇率).setScale(2,
//                            RoundingMode.HALF_UP);

                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt();
                        //变量.本币估值增值 = 变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt();
                    }
                }

                //原币估值 = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null, dto.getoAmt(), 汇率), 本币估值增值);

                //变量.原币估值 = 变量.原币成本 + 变量.原币估值增值
                // 备注：变量.原币成本 = 变量.科目明细.原币币余额
                原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
                //变量.本币估值 = 变量.本币成本 + 变量.本币估值增值
                // 备注：变量.本币成本 = 变量.科目明细.本币成本
                本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));

                if (公允价值明细 != null) {
                    //变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
                    行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
                }
            }
            else if (StringUtil.equals(dto.getBaseSubjectNature(),
                DictCodeBaseSubjectNatrue.公允价值损益类_计划类资产_成本.getValue())
                     && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                    dto.getDetailSubjectNoTail(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrue.公允价值损益类_计划类资产_公允价值变动.getValue()));
                ValSchemeBatchDto 公允价值明细 = CollectionUtils.isEmpty(科目信息列表) ? null : 科目明细集合.get(
                    科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                if (公允价值明细 != null) {
                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt().negate(), 汇率).setScale(4,
//                            RoundingMode.HALF_UP);

                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt().negate();
                        //变量.本币估值增值 = -变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt().negate();
                    }
                    else {
//                        本币估值增值 = ArithmeticUtil.multiply(公允价值明细.getoAmt(), 汇率).setScale(2,
//                            RoundingMode.HALF_UP);

                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt();
                        //变量.本币估值增值 = 变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt();
                    }
                }
                //原币估值 = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(2, null, dto.getoAmt(), 汇率), 本币估值增值);

                //变量.原币估值 = 变量.原币成本 + 变量.原币估值增值
                // 备注：变量.原币成本 = 变量.科目明细.原币币余额
                原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
                //变量.本币估值 = 变量.本币成本 + 变量.本币估值增值
                // 备注：变量.本币成本 = 变量.科目明细.本币成本
                本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));

                if (公允价值明细 != null) {
                    //变量.行情价格 = 变量.原币估值/ 变量.科目明细.数量余额
                    行情价格 = ArithmeticUtil.divide(原币估值, dto.getNumAmt(), 4);
                }
            }
            else if (StringUtil.equals(dto.getBaseSubjectNature(),
                DictCodeBaseSubjectNatrue.计划类资产_成本.getValue())
                     && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{计划类资产-利息调整}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                    dto.getDetailSubjectNoTail(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrue.计划类资产_利息调整.getValue()));
                // 变量.利息调整明细 = 科目明细Map.get(变量.科目信息列表.get（0）.科目号,变量.科目明细.四级科目号尾号)
                ValSchemeBatchDto 利息调整明细 = CollectionUtils.isEmpty(科目信息列表) ? null : 科目明细集合.get(
                    科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                // if（变量.利息调整明细.余额方向 = "贷方"）
                if (利息调整明细 != null) {
                    if (StringUtil.equals(利息调整明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                        // 变量.单位成本 = (变量.科目明细.原币余额 - 变量.利息调整明细.原币余额）/变量.科目明细.数量
                        单位成本 = ArithmeticUtil.divide(
                            ArithmeticUtil.doSub(dto.getoAmt(), 利息调整明细.getoAmt()),
                            dto.getNumAmt()).setScale(4, RoundingMode.HALF_UP);
                        // 估值 = 变量.科目明细.原币余额 - 变量.利息调整明细.原币余额
//                        原币估值 = ArithmeticUtil.doSub(dto.getoAmt(), 利息调整明细.getoAmt());

                        //变量.原币估值 = 变量.科目明细.原币余额 - 变量.利息调整明细.原币余额
                        原币估值 = ArithmeticUtil.doSub(dto.getoAmt(), 利息调整明细.getoAmt());
                        //变量.本币估值 = 变量.科目明细.本币余额 - 变量.利息调整明细.本币余额
                        本币估值 = ArithmeticUtil.doSub(dto.getbAmt(), 利息调整明细.getbAmt());

                    }
                    else {
                        // 变量.单位成本 = (变量.科目明细.原币余额 + 变量.利息调整明细.原币余额）/变量.科目明细.数量
                        单位成本 = ArithmeticUtil.divide(
                            ArithmeticUtil.doAdd(dto.getoAmt(), 利息调整明细.getoAmt()),
                            dto.getNumAmt()).setScale(4, RoundingMode.HALF_UP);
                        // 估值 = 变量.科目明细.原币余额 + 变量.利息调整明细.原币余额
//                        原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(), 利息调整明细.getoAmt());

                        //变量.原币估值 = 变量.科目明细.原币余额 + 变量.利息调整明细.原币余额
                        原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(), 利息调整明细.getoAmt());
                        //变量.本币估值 = 变量.科目明细.本币余额 + 变量.利息调整明细.本币余额
                        本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(), 利息调整明细.getbAmt());

                    }
                    //                    // 变量.估值 = 变量.科目明细.数量*变量.单位成本
//                    估值 = ArithmeticUtil.multiply(dto.getNumAmt(), 单位成本);
                }
                else{
                    // 变量.估值 = 变量.科目明细.原币余额 *汇率
//                    原币估值 = ArithmeticUtil.multiply(dto.getoAmt(), 汇率);

                    //变量.原币估值 = 变量.科目明细.原币余额
                    原币估值 = dto.getoAmt();
                    //变量.本币估值 = 变量.科目明细.本币余额
                    本币估值 = dto.getbAmt();
                }
            }

            //else if（（变量.科目明细.基础科目性质 = "公允价值类计划类收益型资产_成本" ）  and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
            else if (StringUtil.equals(dto.getBaseSubjectNature(),
                    DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_成本.getValue())
                         && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {

                    // 变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{公允价值损益类-公允价值变动}，变量.科目明细.上级科目号）】
                    List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                        dto.getDetailSubjectNoTail(), booksetId,
                        Arrays.asList(DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_公允价值变动.getValue()));
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
            //else if（（变量.科目明细.基础科目性质 = "综合收益类计划类收益型资产_成本" ）  and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
            else if (StringUtil.equals(dto.getBaseSubjectNature(),
                    DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_成本.getValue())
                         && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())) {
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询】
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                    dto.getDetailSubjectNoTail(), booksetId,
                    Arrays.asList(DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_利息调整.getValue(),
                        DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_公允价值变动.getValue()));
                ValSchemeBatchDto 利息调整明细 = null;
                ValSchemeBatchDto 公允价值明细 = null;
                // 变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{计划类资产-利息调整}，变量.科目明细.上级科目号）】

                for (CorrespondSubjectEntity correspondSubjectEntity : 科目信息列表) {
                    if (StringUtil.equals(correspondSubjectEntity.getBaseSubjectNature(),
                        DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_利息调整.getValue())) {
                        利息调整明细 = 科目明细集合.get(
                            correspondSubjectEntity.getSubjectNo() + dto.getDetailSubjectNoTail());
                    }
                    else if (StringUtil.equals(correspondSubjectEntity.getBaseSubjectNature(),
                        DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_公允价值变动.getValue())) {
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
            //else if（变量.科目明细.基础科目性质 = "股票-成本" and 变量.科目明细.科目级别 = 全局变量【底层科目级别】）
            else if(StringUtil.equals(dto.getBaseSubjectNature(),
                    DictCodeBaseSubjectNatrue.股票_成本.getValue())
                    && BookConstant.MAX_SUBJECT_LEVEL.equals(dto.getSubjectLevel())){
                //变量.科目信息列表 = 调用账务类共通【对应科目查询（参数.账套代码，基础科目性质列表{股票-公允价值变动}，变量.科目明细.上级科目号）】
                List<CorrespondSubjectEntity> 科目信息列表 = accountingCommonService.getCorrespondSubject(
                        dto.getDetailSubjectNoTail(), booksetId,
                        Arrays.asList(DictCodeBaseSubjectNatrue.股票_公允价值变动.getValue()));

                //变量.公允价值明细 = 科目明细Map.get(变量.科目信息列表.get（0）.科目号,变量.科目明细.四级科目号尾号)
                ValSchemeBatchDto 公允价值明细 = null;
                if (!CollectionUtils.isEmpty(科目信息列表)) {
                    公允价值明细 = 科目明细集合.get(
                            科目信息列表.get(0).getSubjectNo() + dto.getDetailSubjectNoTail());
                }

                if (公允价值明细 != null) {
                    //if（变量.公允价值明细 .余额方向 = "贷方"）
                    if (StringUtil.equals(公允价值明细.getBalFlag(), DictCodeList.余额方向_贷方.getValue())) {
                        //变量.原币估值增值 = -变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt().negate();
                        //变量.本币估值增值 = -变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt().negate();
                    }
                    else{
                        //变量.原币估值增值 = 变量.公允价值明细 .原币余额
                        原币估值增值 = 公允价值明细.getoAmt();
                        //变量.本币估值增值 = 变量.公允价值明细 .本币余额
                        本币估值增值 = 公允价值明细.getbAmt();
                    }
                }

                //变量.原币估值 = 变量.原币成本 + 变量.原币估值增值
                // 备注：变量.原币成本 = 变量.科目明细.原币币余额
                原币估值 = ArithmeticUtil.doAdd(dto.getoAmt(),(原币估值增值==null ? BigDecimal.ZERO : 原币估值增值));
                //变量.本币估值 = 变量.本币成本 + 变量.本币估值增值
                // 备注：变量.本币成本 = 变量.科目明细.本币成本
                本币估值 = ArithmeticUtil.doAdd(dto.getbAmt(),(本币估值增值==null ? BigDecimal.ZERO : 本币估值增值));

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
//                        dto.getDetailSubjectNoTail(), booksetId, Arrays.asList(DictCodeBaseSubjectNatrue.买入期权公允价值变动.getValue()));
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
//                        dto.getDetailSubjectNoTail(), booksetId, Arrays.asList(DictCodeBaseSubjectNatrue.卖出期权公允价值变动.getValue()));
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
                //原币估值 = ArithmeticUtil.multiply(dto.getoAmt(), 汇率);

                // 变量.原币估值 = 变量.科目明细.原币余额
                原币估值 = dto.getoAmt();
                // 变量.本币估值 = 变量.科目明细.本币余额
                本币估值 = dto.getbAmt();

            }
            else if (!利息调整列表.contains(dto.getBaseSubjectNature())) {
                //原币估值 = ArithmeticUtil.multiply(dto.getoAmt(), 汇率);

                // 变量.原币估值 = 变量.科目明细.原币余额
                原币估值 = dto.getoAmt();
                // 变量.本币估值 = 变量.科目明细.本币余额
                本币估值 = dto.getbAmt();

            }

            // 行情日期处理：
            // if（变量.科目明细.基础科目性质 ！= "摊余成本类成本"）
            if (!DictCodeBaseSubjectNatrue.摊余成本类成本.getValue().equals(dto.getBaseSubjectNature())) {
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

//            ProdValuationDto prodValuationDto = new ProdValuationDto();
//            prodValuationDto.setSubjectNo(dto.getSubjectNo());
//            prodValuationDto.setSubjectName(dto.getSubjectName());
//            prodValuationDto.setFsubjectId(dto.getFsubjectNo());
//            prodValuationDto.setBalFlag(dto.getBalFlag());
//            if (ArithmeticUtil.equals(BigDecimal.ZERO, dto.getNumAmt())) {
//                prodValuationDto.setNum(null);
//            }
//            else {
//                prodValuationDto.setNum(dto.getNumAmt());
//            }
//            prodValuationDto.setCost(ArithmeticUtil.multiply(dto.getoAmt(), 汇率));
//            prodValuationDto.setCostPercent(null);
//            prodValuationDto.setValue(估值);
//            prodValuationDto.setValuePercent(null);
//            prodValuationDto.setValueIncrement(
//                ArithmeticUtil.equals(BigDecimal.ZERO, 估值增值) ? null : 估值增值);
//            // prodValuationDto.setShadowPriceValue(
//            // ArithmeticUtil.equals(BigDecimal.ZERO, 影子价格估值) ? null : 影子价格估值);
//            prodValuationDto.setSubjectLevel(dto.getSubjectLevel());
//            prodValuationDto.setSubjectType(dto.getSubjectType());
//            prodValuationDto.setProfitType(profitType);
//            // 单位价格设值
//            // case 变量.单位成本不为空时 then 变量.单位成本 else 表样.成本/表样.数量
//            if (单位成本 != null) {
//                prodValuationDto.setUnitCost(ArithmeticUtil.round(单位成本, NumberEnum.MONEY));
//            }
//            else if (!ArithmeticUtil.equals(prodValuationDto.getNum(), BigDecimal.ZERO)) {
//                prodValuationDto.setUnitCost(ArithmeticUtil.divide(prodValuationDto.getCost(),
//                    prodValuationDto.getNum()).setScale(2, RoundingMode.HALF_UP));
//            }
//            // 行情价格设值
//            prodValuationDto.setPrice(行情价格);
//            // 行情日期 -> 变量.行情日期
//            prodValuationDto.setMarketDate(marketDate);
//            // 向上汇总方式 -> 变量.向上汇总方式
//            prodValuationDto.setRollUpType(rollUpType);

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
            // 单位成本 -> case 变量.单位成本不为空时 then 变量.单位成本 else  表样.原币成本/表样.数量 -> 数量为空或0时，此项为空，保留2位小数
            if (单位成本 != null) {
                prodValuationDto.setUnitCost(ArithmeticUtil.round(单位成本, NumberEnum.MONEY));
            }
            else if (!ArithmeticUtil.equals(prodValuationDto.getNum(), BigDecimal.ZERO)) {
                prodValuationDto.setUnitCost(
                        ArithmeticUtil.divide(prodValuationDto.getCost(), prodValuationDto.getNum()).setScale(2,
                                RoundingMode.HALF_UP));
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
            //本币估值占净值比 -> 空
            prodValuationDto.setValuePercent(null);
            //本位币估值增值 -> 变量.本位币估值增值
            prodValuationDto.setValueIncrement(ArithmeticUtil.equals(BigDecimal.ZERO, 本币估值增值) ? null : 本币估值增值);
            //影子价格估值 -> 变变量.影子价格估值*汇率 -> 变空时保持为空
            // prodValuationDto.setShadowPriceValue(影子价格估值==null ? null : ArithmeticUtil.multiply(影子价格估值,汇率));
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
        Map<String, Object> bokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
            booksetId, codeList);
        // 定义 份额小数位数
        int shareDecDigits = 0;
        // 定义 万份收益小数位
        int pyeildDigits = 0;
        // 定义 年化收益率小数位
        int yeildDigits = 0;
        // 定义 份额尾数处理
        String shareDecType = "";
        // 定义 万份收益小数位尾数控制
        String pyeildDecType = "";
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
            pyeildDigits = Integer.valueOf(
                (String)bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_万份收益小数位位数.getValue()));
        }
        if (StringUtil.isNotEmpty(
            bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_万份收益小数位尾数处理.getValue()))) {
            pyeildDecType = (String)bokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_万份收益小数位尾数处理.getValue());
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
        // 定义 万份收益尾数处理方式 默认为四舍五入
        RoundingMode pyeildMode = RoundingMode.DOWN;
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
        // 万份收益尾数
        if (StringUtil.equals(DictCodeList.估值舍入规则_四舍五入.getValue(), pyeildDecType)) {
            pyeildMode = RoundingMode.HALF_UP;
        }
        else if (StringUtil.equals(DictCodeList.估值舍入规则_舍位.getValue(), pyeildDecType)) {
            pyeildMode = RoundingMode.DOWN;
        }
        else if (StringUtil.equals(DictCodeList.估值舍入规则_进位.getValue(), pyeildDecType)) {
            pyeildMode = RoundingMode.UP;
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
        params.put("baseSubjectNature00", DictCodeBaseSubjectNatrue.实收资本.getValue());
        List<ValSchemeBatchDto> listRealCapital = valSchemeMapper.selectValSchemeDataRealCapital(
            params);
        ProdValuationDto actRecAmount = new ProdValuationDto();
        actRecAmount.setSubjectNo("实收资本：");
        // 收益类型 = 变量.收益类型
        actRecAmount.setProfitType(profitType);
        if (listRealCapital.size() > 0) {
            ValSchemeBatchDto dto = listRealCapital.get(0);
//            // 小数位数和尾数处理
//            actRecAmount.setNum(ArithmeticUtil.multiply(dto.getoAmt(), 汇率集合.get(dto.getoCcy())));
//            if(shareDecDigits != 0){
//                actRecAmount.setValue(ArithmeticUtil.multiply(shareDecDigits, shareMode, dto.getoAmt(), 汇率集合.get(dto.getoCcy())));
//                actRecAmount.setCost(ArithmeticUtil.round(actRecAmount.getNum(), shareDecDigits, shareMode));
//            }
//            // 否则，不做控制
//            else{
//                actRecAmount.setValue(ArithmeticUtil.multiply(dto.getoAmt(), 汇率集合.get(dto.getoCcy())));
//                actRecAmount.setCost(actRecAmount.getNum());
//            }
//            actRecAmount.setUnitCost(BigDecimal.ONE);
//            actRecAmount.setValueIncrement(BigDecimal.ZERO);
//            // actRecAmount.setShadowPriceValue(BigDecimal.ZERO);


            //数量 <- 变量.科目明细.数量余额（当前为非分层，依然用非分层数量设值逻辑）
            actRecAmount.setNum(dto.getNumAmt());
            // 小数位处理
            if (shareDecDigits != 0) {
                //本币成本 <- 变量.科目明细.本币余额
                actRecAmount.setbAmtCost(ArithmeticUtil.round(dto.getbAmt(), shareDecDigits, shareMode));
                // 本币估值 <- 变量.科目明细.本币余额 *汇率
                actRecAmount.setbAmtValue(ArithmeticUtil.multiply(shareDecDigits, shareMode, dto.getbAmt(), 汇率集合.get(dto.getbCcy())));

                // 原币成本 <- 变量.科目明细.原币余额
                // actRecAmount.setCost( ArithmeticUtil.round(dto.getoAmt(), shareDecDigits, shareMode));
                // 原币估值 <- 变量.科目明细.原币余额 *汇率
                // actRecAmount.setValue(ArithmeticUtil.multiply(shareDecDigits, shareMode, dto.getoAmt(), 汇率集合.get(dto.getoCcy())));
            }
            else{
                //本币成本 <- 变量.科目明细.本币余额
                actRecAmount.setbAmtCost(dto.getbAmt());
                // 本币估值 <- 变量.科目明细.本币余额 *汇率
                actRecAmount.setbAmtValue(ArithmeticUtil.multiply(dto.getbAmt(), 汇率集合.get(dto.getbCcy())));

                // 原币成本 <- 变量.科目明细.原币余额
                // actRecAmount.setCost(dto.getoAmt());
                // 原币估值 <- 变量.科目明细.原币余额 *汇率
                // actRecAmount.setValue(ArithmeticUtil.multiply(dto.getoAmt(), 汇率集合.get(dto.getoCcy())));
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

        // if（变量.收益类型 = "净值型"）
        if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
            // 4.2 损益平淮金查询
            params.put("baseSubjectNature01", DictCodeBaseSubjectNatrue.损益平准金_已实现.getValue());
            params.put("baseSubjectNature02", DictCodeBaseSubjectNatrue.损益平准金_未实现.getValue());
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
                    DictCodeBaseSubjectNatrue.损益平准金_已实现.getValue())) {
                    profLossRealAmount = result.getProfLossRealAmount();
                }
                else {
                    profLossRealAmount = result.getProfLossUnrealAmount();
                }

//                // 成 本 <- 变量.科目明细.原币余额 *汇率
//                profLossRealAmount.setCost(
//                    ArithmeticUtil.multiply(dto.getoAmt(), 汇率集合.get(dto.getoCcy())));
//                profLossRealAmount.setValueIncrement(BigDecimal.ZERO);
//                // 数 量 <- 变量.科目明细.原币余额 *汇率
//                profLossRealAmount.setNum(
//                    ArithmeticUtil.multiply(dto.getoAmt(), 汇率集合.get(dto.getoCcy())));
//                profLossRealAmount.setValueIncrement(BigDecimal.ZERO);
//                // 单位成本 <- 1
//                profLossRealAmount.setUnitCost(BigDecimal.ONE);
//                // 估值 <- 变量.科目明细.原币余额 *汇率
//                profLossRealAmount.setValue(
//                    ArithmeticUtil.multiply(dto.getoAmt(), 汇率集合.get(dto.getoCcy())));
//                profLossRealAmount.setValueIncrement(BigDecimal.ZERO);
//                // profLossRealAmount.setShadowPriceValue(BigDecimal.ZERO);

                //数量 <- 变量.科目明细.数量余额 （当前为非分层，依然用非分层数量设值逻辑）
                profLossRealAmount.setNum(dto.getNumAmt());
                //本币成本 <- 变量.科目明细.本币余额
                profLossRealAmount.setbAmtCost(dto.getbAmt());
                // 本币估值 <- 变量.科目明细.本币余额 *汇率
                profLossRealAmount.setbAmtValue(ArithmeticUtil.multiply(dto.getbAmt(), 汇率集合.get(dto.getbCcy())));

                // 原币成本 <- 变量.科目明细.原币余额
                // profLossRealAmount.setCost(dto.getoAmt());
                // 原币估值 <- 变量.科目明细.原币余额 *汇率
                // profLossRealAmount.setValue(ArithmeticUtil.multiply(dto.getoAmt(), 汇率集合.get(dto.getoCcy())));

                // 单位成本 <- 1
                profLossRealAmount.setUnitCost(BigDecimal.ONE);
                // 本币估值增值 <- 0
                profLossRealAmount.setValueIncrement(BigDecimal.ZERO);
                // 影子价格估值 <- 0
                profLossRealAmount.setShadowPriceValue(BigDecimal.ZERO);
            }
        }

        // 5.资产类合计、负债类合计等合计项目统计
        // 出参.结构集.资产类合计 初始化
        ProdValuationDto assetTotal = new ProdValuationDto("资产类合计：", profitType);
        result.setAssetTotal(assetTotal);
        sumList.add(assetTotal);
        // 出参.结构集.负债类合计 初始化
        ProdValuationDto debtTotal = new ProdValuationDto("负债类合计：", profitType);
        result.setDebtTotal(debtTotal);
        sumList.add(debtTotal);
        // // 出参.结构集.证券投资合计 初始化
        // ProdValuationDto securityInvTotal = new ProdValuationDto("证券投资合计：", profitType);
        // result.setSecurityInvTotal(securityInvTotal);
        // sumList.add(securityInvTotal);
        // // 出参.结构集.其中股票投资 初始化
        // ProdValuationDto shareInvInside = new ProdValuationDto("其中股票投资：", profitType);
        // result.setShareInvInside(shareInvInside);
        // sumList.add(shareInvInside);
        // // 出参.结构集.其中债券投资 初始化
        // ProdValuationDto bondInvInside = new ProdValuationDto("其中债券投资：", profitType);
        // result.setBondInvInside(bondInvInside);
        // sumList.add(bondInvInside);
        // // 出参.结构集.其中基金投资 初始化
        // ProdValuationDto fundInvInside = new ProdValuationDto("其中基金投资：", profitType);
        // result.setFundInvInside(fundInvInside);
        // sumList.add(fundInvInside);
        // // 出参.结构集.其中权证投资 初始化
        // ProdValuationDto warrantInvInside = new ProdValuationDto("其中权证投资：", profitType);
        // result.setWarrantInvInside(warrantInvInside);
        // sumList.add(warrantInvInside);
        // // 出参.结构集.融资业务合计 初始化
        // ProdValuationDto financBusiTotal = new ProdValuationDto("融资业务合计：", profitType);
        // result.setFinancBusiTotal(financBusiTotal);
        // sumList.add(financBusiTotal);
        // // 出参.结构集.融券业务合计 初始化
        // ProdValuationDto marginBusiTotal = new ProdValuationDto("融券业务合计：", profitType);
        // result.setMarginBusiTotal(marginBusiTotal);
        // sumList.add(marginBusiTotal);
        // // 出参.结构集.今日可用头寸 初始化
        // ProdValuationDto tdyAvailPosition = new ProdValuationDto("今日可用头寸：", profitType);
        // result.setTdyAvailPosition(tdyAvailPosition);
        // sumList.add(tdyAvailPosition);

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
                // // 变量.科目明细.科目代码 in（"1108:公允价值损益类-股票投资")
                // else if ("1108".equals(dto.getSubjectNo())) {
                // // 其中股票投资
                // shareInvInside.addUpdate(dto);
                // // 证券投资合计
                // securityInvTotal.addUpdate(dto);
                // }
                // // 变量.科目明细.科目代码 in（"1102:摊余成本类债券投资","1103:以公允价值计量且其变动计入当期损益类债券投资
                // // ,"1104:以公允价值且其变动计入其他综合收益类债券投资))
                // else if ("1102".equals(dto.getSubjectNo()) || "1103".equals(dto.getSubjectNo())
                // || "1104".equals(dto.getSubjectNo())) {
                // // 其中债券投资
                // bondInvInside.addUpdate(dto);
                // // 证券投资合计
                // securityInvTotal.addUpdate(dto);
                // }
                // // 变量.科目明细.科目代码 in（"1105:公允价值损益类-基金投资"))
                // else if ("1105".equals(dto.getSubjectNo())) {
                // // 其中基金投资:
                // fundInvInside.addUpdate(dto);
                // // 证券投资合计
                // securityInvTotal.addUpdate(dto);
                // }
                // // // 变量.科目明细.科目代码 in（待定))
                // // else if ("待定".equals(dto.getSubjectNo())) {
                // // // 其中权证投资:
                // // warrantInvInside.addUpdate(dto);
                // // // 证券投资合计
                // // securityInvTotal.addUpdate(dto);
                // // }
                // // 变量.科目明细.科目代码 in（"1202:买入返售金融资产"))
                // else if ("1202".equals(dto.getSubjectNo())) {
                // // 融资业务合计:
                // financBusiTotal.addUpdate(dto);
                // }
                // // 变量.科目明细.科目代码 in（"2202:卖出回购金融资产款"))
                // else if ("2202".equals(dto.getSubjectNo())) {
                // // 融券业务合计:
                // marginBusiTotal.addUpdate(dto);
                // }
                // // 变量.科目明细.科目代码 in（"1002:银行存款"))
                // else if ("1002".equals(dto.getSubjectNo())) {
                // // 今日可用头寸:
                // tdyAvailPosition.addUpdate(dto);
                // }
            }

        }

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

            // 科目明细列表--计算占净值比
            for (ProdValuationDto dto : result.getProdValuationDtoList()) {
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }

            for (ProdValuationDto dto : sumList) {
                // 本币成本占净值比 = 对应表样.本币成本/出参.结构集.产品资产净值.成本 保留四位小数
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                // 本币市值占净值比 = 对应表样.本币估值/出参.结构集.产品资产净值.成本保留四位小数
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }

        }
        else {
            ProdValuationDto prodAssetVal = new ProdValuationDto();
            prodAssetVal.setSubjectNo("产品资产净值：");
            prodAssetVal.setbAmtCost(ArithmeticUtil.doSub(assetTotal.getbAmtCost(), debtTotal.getbAmtCost()));
            prodAssetVal.setbAmtValue(
                ArithmeticUtil.doSub(assetTotal.getbAmtValue(), debtTotal.getbAmtValue()));
            result.setProdAssetVal(prodAssetVal);
            sumList.add(prodAssetVal);

            // 变量.资产净值 = 0
            BigDecimal prodAsset = BigDecimal.ZERO;
            // if（变量.收益类型 = "净值型"）
            if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
                // 变量.资产净值 = 出参.结构集.产品资产净值.本币估值
                prodAsset = prodAssetVal.getbAmtValue();
            }
            else {
                // 变量.资产净值 = 出参.结构集.产品资产净值.本币成本
                prodAsset = prodAssetVal.getbAmtCost();
            }

            ProdValuationDto prodUnitVal = new ProdValuationDto();
            prodUnitVal.setSubjectNo("产品单位净值：");
            // 科目名称 <- 变量.资产净值/出参.结构集.实收资本.本币成本
            BigDecimal subjectName = ArithmeticUtil.divide(prodAsset,
                result.getActRecAmount().getbAmtCost(), NumberEnum.ALL_PRECISION.getCode());
            // 按照账套配置 估值小数位处理方式和估值小数位处理方式 处理小数位
            if (StringUtil.equals(DictCodeList.估值舍入规则_四舍五入.getValue(),
                acctEntity.getValDecType())) {
                prodUnitVal.setSubjectName(
                    subjectName.setScale(Integer.valueOf(acctEntity.getValDecDigits()),
                        RoundingMode.HALF_UP).toString());

            }
            else if (StringUtil.equals(DictCodeList.估值舍入规则_舍位.getValue(),
                acctEntity.getValDecType())) {
                prodUnitVal.setSubjectName(subjectName.setScale(
                    Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.DOWN).toString());
            }
            else if (StringUtil.equals(DictCodeList.估值舍入规则_进位.getValue(),
                acctEntity.getValDecType())) {
                prodUnitVal.setSubjectName(subjectName.setScale(
                    Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.UP).toString());
            }
            else {
                prodUnitVal.setSubjectName(subjectName.toString());
            }
            result.setProdUnitVal(prodUnitVal);

            // if（变量.收益类型 = "净值型"）
            if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
                // 查询日期准备：
                // 定义 变量.查询日列表
                List<Date> queryDates = new ArrayList<>();
                // 变量.产品信息 = 调用标的类共通【核算主体查询（账套代码）】
                FinProduct prodInfo = finProductCommonService.queryAcctSubject(booksetId);
                // if（变量.产品信息 != null && 变量.产品信息.交易日历 != null）
                Date ystday = null;
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

                // 去年末 = 估值表处理结果集.入账日的去年年末(取得 年初 - 1)
                Date lastYearEnd = DateUtil.addDay(DateUtil.getFirstDayOfYear(bookDate), -1);
                // 上季末 = 估值表处理结果集.入账日的上季末
                Date lastQuarterEnd = DateUtil.addDay(DateUtil.getFirstDayOfQuarter(bookDate), -1);
                // 上月末 = 估值表处理结果集.入账日的上月末
                Date lastMonthEnd = DateUtil.addDay(DateUtil.getFirstDay(bookDate), -1);
                // 上周末 = 估值表处理结果集.入账日的上周末（最后一天为星期六）
                // 算出当前是星期几，对应减去（天数+1）就是上周六的日期
                int day = DateUtil.getWeekDay(bookDate) + 1;
                Date lastWeekend = DateUtil.addDay(bookDate, -1 * (day + 1));
                // 将上述日期组装成变量.查询日列表
                queryDates.add(ystday);
                queryDates.add(lastYearEnd);
                queryDates.add(lastQuarterEnd);
                queryDates.add(lastMonthEnd);
                queryDates.add(lastWeekend);
                // 入账日
                queryDates.add(bookDate);
                BokStatBokPositionDto position = new BokStatBokPositionDto();
                // 入账日
                position.setBookDate(bookDate);
                // 账套
                position.setBooksetId(booksetId);
                // 查询日列表
                position.setQueryDates(queryDates);
                // 分层代码 = 分层代码
                position.setLayeringId(acctEntity.getLayeringId());
                // 调用账务类共通【帐套资产负债查询】
                // List<BokStatBokPosition> listBokStatBokPosition =
                // accountingCommonService.getValStatBokPosition(null, null, bookDate, booksetId,
                // queryDates, null);
                List<BokStatBokPosition> listBokStatBokPosition = accountingCommonService.getValStatBokPosition(
                    position);
                // 定义 账套汇总信息map(Key：发生日，VALUE:帐套资产负债信息)
                Map<Date, BokStatBokPosition> bokStatBokPositionMap = new HashMap<>();
                // 将上述返回帐套资产负债信息列表组装成 账套汇总信息map(Key：发生日，VALUE:帐套资产负债信息)
                for (BokStatBokPosition item : listBokStatBokPosition) {
                    bokStatBokPositionMap.put(item.getBookDate(), item);
                }

                // 变量.产品累计单位净值 = 账套汇总信息map.get（入账日）.累计分红净值 + 账套汇总信息map.get（入账日）.单位净值

                BigDecimal prodTotUnitNet = (bokStatBokPositionMap.get(
                    bookDate) == null) ? BigDecimal.ZERO : ArithmeticUtil.doAdd(
                        bokStatBokPositionMap.get(bookDate).getTotBounsNet(),
                        bokStatBokPositionMap.get(bookDate).getNetUnitValue());
                // 定义 昨日单位净值 = 账套汇总信息map.get（昨日）.单位净值
                BigDecimal ystNetUnitValue = BigDecimal.ZERO;
                if (bokStatBokPositionMap.get(ystday) != null) {
                    ystNetUnitValue = bokStatBokPositionMap.get(ystday).getNetUnitValue();
                }
                // 按照账套配置 估值小数位处理方式和估值小数位处理方式 处理小数位
                if (StringUtil.equals(DictCodeList.估值舍入规则_四舍五入.getValue(),
                    acctEntity.getValDecType())) {
                    prodTotUnitNet = ArithmeticUtil.round(prodTotUnitNet,
                        Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.HALF_UP);
                    ystNetUnitValue = ArithmeticUtil.round(ystNetUnitValue,
                        Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.HALF_UP);
                }
                else if (StringUtil.equals(DictCodeList.估值舍入规则_舍位.getValue(),
                    acctEntity.getValDecType())) {
                    prodTotUnitNet = ArithmeticUtil.round(prodTotUnitNet,
                        Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.DOWN);
                    ystNetUnitValue = ArithmeticUtil.round(ystNetUnitValue,
                        Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.DOWN);
                }
                else if (StringUtil.equals(DictCodeList.估值舍入规则_进位.getValue(),
                    acctEntity.getValDecType())) {
                    prodTotUnitNet = ArithmeticUtil.round(prodTotUnitNet,
                        Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.UP);
                    ystNetUnitValue = ArithmeticUtil.round(ystNetUnitValue,
                        Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.UP);
                }
                else {
                    prodTotUnitNet = ArithmeticUtil.round(prodTotUnitNet, 4, RoundingMode.HALF_UP);
                    ystNetUnitValue = ArithmeticUtil.round(ystNetUnitValue, 4,
                        RoundingMode.HALF_UP);
                }

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

                // 出参.结构集.成立以来净值增长率(%): 设置
                ProdValuationDto priceGrowRateSince = new ProdValuationDto("成立以来净值增长率(%)：",
                    prodTotUnitNet, BigDecimal.valueOf(1.0000));
                result.setPriceGrowRateSince(priceGrowRateSince);

                // 变量.上一年末日产品累计净值
                BigDecimal prodTotUnitNetLastYear = BigDecimal.ONE;
                // if 账套汇总信息map.get（去年末） != null)
                if (bokStatBokPositionMap.get(lastYearEnd) != null) {
                    // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（去年末）.累计分红净值 + 账套汇总信息map.get（去年末）.单位净值
                    prodTotUnitNetLastYear = ArithmeticUtil.doAdd(
                        bokStatBokPositionMap.get(lastYearEnd).getTotBounsNet(),
                        bokStatBokPositionMap.get(lastYearEnd).getNetUnitValue());
                }
                // 出参.结构集.净值年增长率(%): 设置
                ProdValuationDto priceGrowRateYear = new ProdValuationDto("净值年增长率(%)：",
                    prodTotUnitNet, prodTotUnitNetLastYear);
                result.setPriceGrowRateYear(priceGrowRateYear);

                // 变量.上一季末日产品累计净值
                BigDecimal prodTotUnitNetLastQuarter = BigDecimal.ONE;
                // if 账套汇总信息map.get（上季末） != null)
                if (bokStatBokPositionMap.get(lastQuarterEnd) != null) {
                    // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（上季末）.累计分红净值 + 账套汇总信息map.get（上季末）.单位净值
                    prodTotUnitNetLastQuarter = ArithmeticUtil.doAdd(
                        bokStatBokPositionMap.get(lastQuarterEnd).getTotBounsNet(),
                        bokStatBokPositionMap.get(lastQuarterEnd).getNetUnitValue());
                }
                // 出参.结构集.净值季度增长率(%): 设置
                ProdValuationDto priceGrowRateQuarter = new ProdValuationDto("净值季度增长率(%)：",
                    prodTotUnitNet, prodTotUnitNetLastQuarter);
                result.setPriceGrowRateQuarter(priceGrowRateQuarter);

                // 变量.上一月末日产品累计净值
                BigDecimal prodTotUnitNetLastMonth = BigDecimal.ONE;
                // if 账套汇总信息map.get（上月末） != null)
                if (bokStatBokPositionMap.get(lastMonthEnd) != null) {
                    // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（上月末）.累计分红净值 + 账套汇总信息map.get（上月末）.单位净值
                    prodTotUnitNetLastMonth = ArithmeticUtil.doAdd(
                        bokStatBokPositionMap.get(lastMonthEnd).getTotBounsNet(),
                        bokStatBokPositionMap.get(lastMonthEnd).getNetUnitValue());
                }
                // 净值月增长率(%): 设置
                ProdValuationDto priceGrowRateMonth = new ProdValuationDto("净值月增长率(%)：",
                    prodTotUnitNet, prodTotUnitNetLastMonth);
                result.setPriceGrowRateMonth(priceGrowRateMonth);

                // 变量.上一周末日产品累计净值
                BigDecimal prodTotUnitNetLastWeek = BigDecimal.ONE;
                // if 账套汇总信息map.get（上周末） != null)
                if (bokStatBokPositionMap.get(lastWeekend) != null) {
                    // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（上周末）.累计分红净值 + 账套汇总信息map.get（上周末）.单位净值
                    prodTotUnitNetLastWeek = ArithmeticUtil.doAdd(
                        bokStatBokPositionMap.get(lastWeekend).getTotBounsNet(),
                        bokStatBokPositionMap.get(lastWeekend).getNetUnitValue());
                }
                // 净值周增长率(%): 设置
                ProdValuationDto priceGrowRateWeek = new ProdValuationDto("净值周增长率(%)：",
                    prodTotUnitNet, prodTotUnitNetLastWeek);
                result.setPriceGrowRateWeek(priceGrowRateWeek);

                // 变量.昨日产品累计净值
                BigDecimal prodTotUnitNetYst = BigDecimal.ONE;
                // if 账套汇总信息map.get（上周末） != null)
                if (bokStatBokPositionMap.get(ystday) != null) {
                    // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（上周末）.累计分红净值 + 账套汇总信息map.get（上周末）.单位净值
                    prodTotUnitNetYst = ArithmeticUtil.doAdd(
                        bokStatBokPositionMap.get(ystday).getTotBounsNet(),
                        bokStatBokPositionMap.get(ystday).getNetUnitValue());
                }
                // 净值日增长率(%): 设置
                ProdValuationDto priceGrowRateDay = new ProdValuationDto("净值日增长率(%)：",
                    prodTotUnitNet, prodTotUnitNetYst);
                result.setPriceGrowRateDay(priceGrowRateDay);

            }
            // else if（变量.收益类型 = "货币型"）
            else if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
                // // 出参.结构集.影子价格单位净值： 设置
                // ProdValuationDto shandowUnitPrice = new ProdValuationDto();
                // shandowUnitPrice.setSubjectNo("影子价格单位净值：");
                // // （出参.结构集.产品资产净值.估值 + 变量.偏离金额）/出参.结构集.实收资本.估值
                // BigDecimal value = ArithmeticUtil.divide(
                // ArithmeticUtil.doAdd(prodAssetVal.getValue(), deviationAmt),
                // actRecAmount.getValue());
                // // 按照账套配置 估值小数位处理方式和估值小数位处理方式 处理小数位
                // if (StringUtil.equals(DictCodeList.估值舍入规则_四舍五入.getValue(),
                // acctEntity.getValDecType())) {
                // shandowUnitPrice.setSubjectName(
                // value.setScale(Integer.valueOf(acctEntity.getValDecDigits()),
                // RoundingMode.HALF_UP).toString());
                //
                // }
                // else if (StringUtil.equals(DictCodeList.估值舍入规则_舍位.getValue(),
                // acctEntity.getValDecType())) {
                // shandowUnitPrice.setSubjectName(value.setScale(
                // Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.DOWN).toString());
                // }
                // else if (StringUtil.equals(DictCodeList.估值舍入规则_进位.getValue(),
                // acctEntity.getValDecType())) {
                // shandowUnitPrice.setSubjectName(value.setScale(
                // Integer.valueOf(acctEntity.getValDecDigits()), RoundingMode.UP).toString());
                // }
                // else {
                // shandowUnitPrice.setSubjectName(value.toString());
                // }
                // result.setShandowUnitPrice(shandowUnitPrice);

                // 出参.结构集.偏离金额： 设置
                ProdValuationDto deviationAmount = new ProdValuationDto();
                // 科目代码 <- "偏离金额："
                deviationAmount.setSubjectNo("偏离金额：");
                // 科目名称 <- 变量.偏离金额
                BigDecimal varPlje = ArithmeticUtil.subtract(prodAssetVal.getbAmtValue(),
                    prodAssetVal.getbAmtCost());
                deviationAmount.setSubjectName(
                    varPlje.setScale(NumberEnum.MONEY.getCode(), RoundingMode.HALF_UP).toString());
                result.setDeviationAmount(deviationAmount);

                // 出参.结构集.偏离度： 设置
                ProdValuationDto deviationDegree = new ProdValuationDto();
                // 科目代码 <- "偏离度："
                deviationDegree.setSubjectNo("偏离度%：");
                // 科目名称 <- 变量.偏离金额/出参.结构集.产品资产净值.本币成本 * 100 保留4位小数
                deviationDegree.setSubjectName(ArithmeticUtil.multiply(4, RoundingMode.HALF_UP,
                    ArithmeticUtil.divide(varPlje, prodAssetVal.getbAmtCost()),
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
                // List<BokStatBokPosition> listPostion =
                // accountingCommonService.getValStatBokPosition(bookDate, bookDate, bookDate,
                // booksetId, null, null);
                List<BokStatBokPosition> listPostion = accountingCommonService.getValStatBokPosition(
                    position);
                if (!CollectionUtils.isEmpty(listPostion)) {
                    tdyBokPosition = listPostion.get(0);
                }

                // 出参.结构集.产品本日收益： 设置
                ProdValuationDto tdyProdProfit = new ProdValuationDto();
                // 科目代码 <- "产品本日收益："
                tdyProdProfit.setSubjectNo("产品本日收益：");
                // 科目名称 <- 变量.当日账套汇总信息.应付利润
                tdyProdProfit.setSubjectName(
                    (tdyBokPosition.getProfitAmt() == null) ? BigDecimal.ZERO.toString() : tdyBokPosition.getProfitAmt().toString());
                result.setTdyProdProfit(tdyProdProfit);

                // 出参.结构集.万份收益： 设置
                ProdValuationDto pYield = new ProdValuationDto();
                // 科目代码 <- "产品本日收益："
                pYield.setSubjectNo("万份收益： ");
                // 科目名称 <- 变量.当日账套汇总信息.万份收益：
                if (pyeildDigits != 0) {
                    pYield.setSubjectName(
                        (tdyBokPosition.getpYield() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.round(
                            tdyBokPosition.getpYield(), pyeildDigits, pyeildMode).toString());
                }
                else {
                    pYield.setSubjectName(
                        (tdyBokPosition.getpYield() == null) ? BigDecimal.ZERO.toString() : tdyBokPosition.getpYield().toString());
                }
                result.setPYield(pYield);

                // 出参.结构集.本日年化收益率： 设置
                ProdValuationDto tdyYield = new ProdValuationDto();
                // 科目代码 <- "本日年化收益率："
                tdyYield.setSubjectNo("本日年化收益率%： ");
                // 科目名称 <- 变量.当日账套汇总信息.当日年化收益率
                if (yeildDigits != 0) {
                    tdyYield.setSubjectName(
                        (tdyBokPosition.getTdyYield() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.multiply(
                            yeildDigits, yeildMode, tdyBokPosition.getTdyYield(),
                            new BigDecimal(100)).toString());
                }
                else {
                    tdyYield.setSubjectName(
                        (tdyBokPosition.getTdyYield() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.round(
                            ArithmeticUtil.multiply(tdyBokPosition.getTdyYield(),
                                new BigDecimal(100)),
                            4, RoundingMode.HALF_UP).toString());
                }
                result.setTdyYield(tdyYield);

                // 出参.结构集.7日年化收益率： 设置
                ProdValuationDto yield7 = new ProdValuationDto();
                // 科目代码 <- "7日年化收益率："
                yield7.setSubjectNo("7日年化收益率%： ");
                // 科目名称 <- 变量.当日账套汇总信息.当日7日年化收益率
                if (yeildDigits != 0) {
                    yield7.setSubjectName(
                        (tdyBokPosition.getYield7() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.multiply(
                            yeildDigits, yeildMode, tdyBokPosition.getYield7(),
                            new BigDecimal(100)).toString());
                }
                else {
                    yield7.setSubjectName(
                        (tdyBokPosition.getYield7() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.round(
                            ArithmeticUtil.multiply(tdyBokPosition.getYield7(),
                                new BigDecimal(100)),
                            4, RoundingMode.HALF_UP).toString());
                }
                result.setYield7(yield7);

             // 出参.结构集.从成立以来累计万份收益: 设置
                ProdValuationDto pYieldSince = new ProdValuationDto();
                // 科目代码 <- "从成立以来累计万份收益:"
                pYieldSince.setSubjectNo("从成立以来累计万份收益：");
                // 科目名称 <- 查询.从成立以来累计万份收益
                if (valSchemeMapper.getPYieldSumSince(booksetId, bookDate, null) != null) {
                    if (pyeildDigits != 0) {
                        pYieldSince.setSubjectName(ArithmeticUtil.round(
                            valSchemeMapper.getPYieldSumSince(booksetId, bookDate, null),
                            pyeildDigits, pyeildMode).toString());
                    }
                    else {
                        pYield.setSubjectName(valSchemeMapper.getPYieldSumSince(booksetId,
                            bookDate, null).toString());
                    }
                }
                else {
                    pYieldSince.setSubjectName(BigDecimal.ZERO.toString());
                }
                result.setPYieldSince(pYieldSince);

                // 出参.结构集.本年累积万份收益： 设置
                ProdValuationDto pYieldYear = new ProdValuationDto();
                // 科目代码 <- "本年累积万份收益："
                pYieldYear.setSubjectNo("本年累积万份收益：");
                // 科目名称 <- 查询.本年累积万份收益
                // 去年末
                Date lastYearEnd = DateUtil.addDay(DateUtil.getFirstDayOfYear(bookDate), -1);
                if (valSchemeMapper.getPYieldSumSince(booksetId, bookDate, lastYearEnd) != null) {
                    if (pyeildDigits != 0) {
                        pYieldYear.setSubjectName(ArithmeticUtil.round(
                            valSchemeMapper.getPYieldSumSince(booksetId, bookDate, lastYearEnd),
                            pyeildDigits, pyeildMode).toString());
                    }
                    else {
                        pYield.setSubjectName(valSchemeMapper.getPYieldSumSince(booksetId,
                            bookDate, lastYearEnd).toString());
                    }
                }
                else {
                    pYieldYear.setSubjectName(BigDecimal.ZERO.toString());
                }
                result.setPYieldYear(pYieldYear);

                // 2018年10月10日---设置投资组合平均到期日、剩余存续期：
                Map<String, Object> paraMap = new HashMap<>();
                paraMap.put("booksetId", booksetId);
                paraMap.put("bookDate", bookDate);
                List<String> listFinprodType = new ArrayList<String>();
                listFinprodType.add(DictCodeListExt1.金融产品类型_债券.getValue());
                listFinprodType.add(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
                listFinprodType.add(DictCodeListExt1.金融产品类型_存款.getValue());
                listFinprodType.add(DictCodeListExt1.金融产品类型_拆出.getValue());
                listFinprodType.add(DictCodeListExt1.金融产品类型_计划类资产.getValue());
                listFinprodType.add(DictCodeListExt1.金融产品类型_资金账户.getValue());
                listFinprodType.add(DictCodeListExt1.金融产品类型_同业借款.getValue());
                paraMap.put("listFinprodType", listFinprodType);
                paraMap.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());
                // 查询.投资组合
                InvGruopDto invGruopDto = valSchemeMapper.queryInvGruop(paraMap);
                //和设计人员确认剩余期限是有可能取不到的
                if (invGruopDto == null) {
                	invGruopDto = new InvGruopDto();
                }
                // 变量.剩余期限分子 = 上述查询.剩余期限分子
                BigDecimal remainTermSum = invGruopDto.getRemainTermSum() == null ? BigDecimal.ZERO
						: invGruopDto.getRemainTermSum();
                // 变量.剩余存续期限分子 = 上述查询.剩余存续期限分子
                BigDecimal remainDurationSum = invGruopDto.getRemainDurationSum() == null ? BigDecimal.ZERO
						: invGruopDto.getRemainDurationSum();
                // 变量.分母 = 上述查询.分母
                BigDecimal costSum = invGruopDto.getCostSum() == null ? BigDecimal.ZERO : invGruopDto.getCostSum();
                // 查询 债券其他应收款余额
                List<String> listBaseSubNature = new ArrayList<String>();
                listBaseSubNature.add(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue());
                listBaseSubNature.add(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue());
                paraMap.put("listBaseSubNature", listBaseSubNature);
                BigDecimal recvBalance = valSchemeMapper.getOtherRecvBalance(paraMap);
                // 循环结构集中科目明细，获取3003和1021对应科目的成本
                BigDecimal cost3003 = BigDecimal.ZERO;
                BigDecimal cost1021 = BigDecimal.ZERO;
                BigDecimal cost1031 = BigDecimal.ZERO;
                for (ProdValuationDto dto : result.getProdValuationDtoList()) {
                    if (dto.getSubjectNo().equals("3003")) {
                        cost3003 = dto.getbAmtCost();
                    }
                    else if (dto.getSubjectNo().equals("1021")) {
                        cost1021 = dto.getbAmtCost();
                    }
                    else if (dto.getSubjectNo().equals("1031")) {
                        cost1031 = dto.getbAmtCost();
                    }
                }
                // 变量.剩余期限分子=变量.剩余期限分子+(出参.结构集中3003科目对应的成本+上述查询.债券其他应收款余额)
                remainTermSum = ArithmeticUtil.doAdd(remainTermSum, cost3003, recvBalance);
                // 变量.剩余存续期限分子=变量.剩余存续期限分子+(出参.结构集中3003科目对应的成本+上述查询.债券其他应收款余额)
                remainDurationSum = ArithmeticUtil.doAdd(remainDurationSum, cost3003, recvBalance);
                // 变量.分母=变量.分母+(出参.结构集中3003科目对应的成本+上述查询.债券其他应收余额)+出参.结构集中1021科目对应的成本
                // +出参.结构集中1031科目对应的成本
                costSum = ArithmeticUtil.doAdd(costSum, cost3003, recvBalance, cost1021, cost1031);

                // 出参.结构集.投资组合平均到期日： 设置
                ProdValuationDto invGroupAvrMday = new ProdValuationDto();
                // 科目代码 <- "投资组合平均到期日："
                invGroupAvrMday.setSubjectNo("投资组合平均到期日：");
                // 科目名称 <- 变量.剩余期限分子/变量.分母 保留整数
                invGroupAvrMday.setSubjectName(
                    ArithmeticUtil.divide(remainTermSum, costSum, 0).toString());
                // 入结构集
                result.setInvGroupAvrMday(invGroupAvrMday);

                // 出参.结构集.剩余存续期： 设置
                ProdValuationDto remainDuration = new ProdValuationDto();
                // 科目代码 <- "剩余存续期： "
                remainDuration.setSubjectNo("剩余存续期： ");
                // 科目名称 <- 变量.剩余存续期限分子/变量.分母 保留整数
                remainDuration.setSubjectName(
                    ArithmeticUtil.divide(remainDurationSum, costSum, 0).toString());
                // 入结构集
                result.setRemainDuration(remainDuration);
            }


            // 6.净值占比处理
            // 对于以下表样数据中占比计算
            // 科目明细列表
            for (ProdValuationDto dto : result.getProdValuationDtoList()) {
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }

            // 实收资本\损益平准金未实现\损益平准金已实现\ 资产类合计\负债类合计\产品资产净值
            // 证券投资合计\其中股票投资\其中债券投资\其中基金投资\其中权证投资\融资业务合计\融券业务合计\今日可用头寸
            for (ProdValuationDto dto : sumList) {
                // 成本占净值比 = 对应表样.本币成本/出参.结构集.产品资产净值.成本 保留四位小数
                dto.setCostPercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtCost(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
                // 市值占净值比 = 对应表样.本币估值/出参.结构集.产品资产净值.成本保留四位小数
                dto.setValuePercent(
                    ArithmeticUtil.multiply(ArithmeticUtil.divide(dto.getbAmtValue(), prodAsset),
                        new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP));
            }
        }

        // ====================================分层逻辑start================================
        // 若特殊核算模型不为空，则需要分层
        if (StringUtils.isNotEmpty(acctEntity.getBokSpecMode())) {
            List<String> listBaseSubjectNature = new ArrayList<>();
            listBaseSubjectNature.add(DictCodeBaseSubjectNatrue.实收资本.getValue());
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

//                actRecAmountLaying.setNum(
//                        ArithmeticUtil.multiply(item.getoAmt(), 汇率集合.get(item.getoCcy())));
//                if (shareDecDigits != 0) {
//                    actRecAmountLaying.setValue(ArithmeticUtil.multiply(shareDecDigits, shareMode,
//                            item.getoAmt(), 汇率集合.get(item.getoCcy())));
//                    actRecAmountLaying.setCost(
//                            ArithmeticUtil.round(item.getoAmt(), shareDecDigits, shareMode));
//                }
//                // 否则，不做控制
//                else {
//                    actRecAmountLaying.setValue(
//                            ArithmeticUtil.multiply(item.getoAmt(), 汇率集合.get(item.getoCcy())));
//                    actRecAmountLaying.setCost(actRecAmountLaying.getNum());
//                }
//                actRecAmountLaying.setUnitCost(BigDecimal.ONE);
//                actRecAmountLaying.setValueIncrement(BigDecimal.ZERO);
//                actRecAmountLaying.setShadowPriceValue(BigDecimal.ZERO);

                //数量 <- 科目明细.本币余额（当前为分层，采用分层数量设值逻辑）
                actRecAmountLaying.setNum(item.getbAmt());
                // 小数位处理
                if (shareDecDigits != 0) {
                    //本币成本 <- 变量.科目明细.本币余额
                    actRecAmountLaying.setbAmtCost( ArithmeticUtil.round(item.getbAmt(), shareDecDigits, shareMode));
                    // 本币估值 <- 变量.科目明细.本币余额 *汇率
                    actRecAmountLaying.setbAmtValue(ArithmeticUtil.multiply(shareDecDigits, shareMode, item.getbAmt(), 汇率集合.get(item.getbCcy())));

                    // 原币成本 <- 变量.科目明细.原币余额
                    // actRecAmountLaying.setCost( ArithmeticUtil.round(item.getoAmt(), shareDecDigits, shareMode));
                    // 原币估值 <- 变量.科目明细.原币余额 *汇率
                    // actRecAmountLaying.setValue(ArithmeticUtil.multiply(shareDecDigits, shareMode, item.getoAmt(), 汇率集合.get(item.getoCcy())));
                }
                else{
                    //本币成本 <- 变量.科目明细.本币余额
                    actRecAmountLaying.setbAmtCost(item.getbAmt());
                    // 本币估值 <- 变量.科目明细.本币余额 *汇率
                    actRecAmountLaying.setbAmtValue(ArithmeticUtil.multiply(item.getbAmt(), 汇率集合.get(item.getbCcy())));

                    // 原币成本 <- 变量.科目明细.原币余额
                    // actRecAmountLaying.setCost(item.getoAmt());
                    // 原币估值 <- 变量.科目明细.原币余额 *汇率
                    // actRecAmountLaying.setValue(ArithmeticUtil.multiply(item.getoAmt(), 汇率集合.get(item.getoCcy())));
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
                listBaseSubjectNature.add(DictCodeBaseSubjectNatrue.损益平准金_未实现.getValue());
                listBaseSubjectNature.add(DictCodeBaseSubjectNatrue.损益平准金_已实现.getValue());
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
                    if (StringUtil.equals(DictCodeBaseSubjectNatrue.损益平准金_未实现.getValue(),
                        item.getBaseSubjectNature())) {
                        actRecAmountLaying.setSubjectNo("损益平准金未实现-" + item.getSubjectName() + "：");
                        // 明细区分 <-损益平准金未实现
                        actRecAmountLaying.setDetailDist(DictCodeList.估值表类型_损益平准金未实现.getValue());
                    }
                    else if (StringUtil.equals(DictCodeBaseSubjectNatrue.损益平准金_已实现.getValue(),
                        item.getBaseSubjectNature())) {
                        actRecAmountLaying.setSubjectNo("损益平准金已实现-" + item.getSubjectName() + "：");
                        // 明细区分 <-损益平准金已实现
                        actRecAmountLaying.setDetailDist(DictCodeList.估值表类型_损益平准金已实现.getValue());
                    }
                    // 分层代码 <- 四级科目尾号
                    actRecAmountLaying.setLayeringId(item.getDetailSubjectNoTail());

//                    // 数量 <- 变量.科目明细.原币余额 *汇率
//                    actRecAmountLaying.setNum(
//                        ArithmeticUtil.multiply(item.getoAmt(), 汇率集合.get(item.getoCcy())));
//                    // 成本 <- 变量.科目明细.原币余额 *汇率
//                    actRecAmountLaying.setCost(
//                        ArithmeticUtil.multiply(item.getoAmt(), 汇率集合.get(item.getoCcy())));
//                    // 单位成本 <- 1
//                    actRecAmountLaying.setUnitCost(BigDecimal.ONE);
//                    // 估值 <- 变量.科目明细.原币余额 *汇率
//                    actRecAmountLaying.setValue(
//                        ArithmeticUtil.multiply(item.getoAmt(), 汇率集合.get(item.getoCcy())));

                    //数量 <-  <- 科目明细.本币余额（当前为分层，采用分层数量设值逻辑）
                    actRecAmountLaying.setNum(item.getbAmt());
                    //本币成本 <- 变量.科目明细.本币余额
                    actRecAmountLaying.setbAmtCost(item.getbAmt());
                    // 本币估值 <- 变量.科目明细.本币余额 *汇率
                    actRecAmountLaying.setbAmtValue(ArithmeticUtil.multiply(item.getbAmt(), 汇率集合.get(item.getbCcy())));

                    // 原币成本 <- 变量.科目明细.原币余额
                    // actRecAmountLaying.setCost(item.getoAmt());
                    // 原币估值 <- 变量.科目明细.原币余额 *汇率
                    // actRecAmountLaying.setValue(ArithmeticUtil.multiply(item.getoAmt(), 汇率集合.get(item.getoCcy())));

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
                    // 变量.分层产品信息 = 调用标的类共通【变量.分层代码.四级科目尾号】
                    FinProduct layeringProdInfo = finProductCommonService.queryAcctSubject(
                        dto.getLayeringId());
                    // if（变量.分层产品信息 != null && 变量.分层产品信息.交易日历 != null）
                    Date ystday = null;
                    if (layeringProdInfo != null
                        && StringUtils.isNotEmpty(layeringProdInfo.getCalendarId())) {
                        ystday = calculationCommonService.getCDate(bookDate,
                            layeringProdInfo.getCalendarId());
                    }
                    else {
                        // 昨日 = 估值表处理结果集.入账日 - 1
                        ystday = DateUtil.addDay(bookDate, -1);
                    }

                    // 去年末 = 估值表处理结果集.入账日的去年年末(取得 年初 - 1)
                    Date lastYearEnd = DateUtil.addDay(DateUtil.getFirstDayOfYear(bookDate), -1);
                    // 上季末 = 估值表处理结果集.入账日的上季末
                    Date lastQuarterEnd = DateUtil.addDay(DateUtil.getFirstDayOfQuarter(bookDate),
                        -1);
                    // 上月末 = 估值表处理结果集.入账日的上月末
                    Date lastMonthEnd = DateUtil.addDay(DateUtil.getFirstDay(bookDate), -1);
                    // 上周末 = 估值表处理结果集.入账日的上周末（最后一天为星期六）
                    // 算出当前是星期几，对应减去（天数+1）就是上周六的日期
                    int day = DateUtil.getWeekDay(bookDate) + 1;
                    Date lastWeekend = DateUtil.addDay(bookDate, -1 * (day + 1));
                    // 将上述日期组装成变量.查询日列表
                    queryDates.add(ystday);
                    queryDates.add(lastYearEnd);
                    queryDates.add(lastQuarterEnd);
                    queryDates.add(lastMonthEnd);
                    queryDates.add(lastWeekend);
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
                    BigDecimal layerProdTotUnitNet = (bokStatBokPositionMap.get(
                        bookDate) == null) ? BigDecimal.ZERO : ArithmeticUtil.doAdd(
                            bokStatBokPositionMap.get(bookDate).getTotBounsNet(),
                            bokStatBokPositionMap.get(bookDate).getNetUnitValue());
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
                    layerProdTotUnitNet = ArithmeticUtil.round(layerProdTotUnitNet, valDecDigits,
                        roundingMode);
                    layerNetAssetValue = ArithmeticUtil.round(layerNetAssetValue, valDecDigits,
                        roundingMode);
                    layerNetUnitValue = ArithmeticUtil.round(layerNetUnitValue, valDecDigits,
                        roundingMode);

                    // 出参.结构集.分层数据列表.资产净值 设置
                    ProdValuationDto prodNetValue = new ProdValuationDto();
                    // 科目代码 <-"产品资产净值-变量.分层代码List.四级科目尾部名称"
                    prodNetValue.setSubjectNo("产品资产净值-" + dto.getSubjectNo());
                    // 成本 <- 变量.分层资产净值
                    prodNetValue.setbAmtCost(layerNetAssetValue);
                    // 估值 <- 变量.分层资产净值
                    prodNetValue.setbAmtValue(layerNetAssetValue);
                    prodNetValue.setLayeringId(dto.getLayeringId());
                    prodNetValue.setDetailDist(DictCodeList.估值表类型_产品资产净值.getValue());
                    listProdValDto.add(prodNetValue);

                    // 出参.结构集.分层数据列表.产品单位净值 设置
                    ProdValuationDto prodUnitPrice = new ProdValuationDto();
                    // 科目代码 <-"产品单位净值-变量.分层代码List.四级科目尾部名称："
                    prodUnitPrice.setSubjectNo("产品单位净值-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.分层单位净值
                    prodUnitPrice.setSubjectName(String.valueOf(layerNetUnitValue));
                    prodUnitPrice.setLayeringId(dto.getLayeringId());
                    prodUnitPrice.setDetailDist(DictCodeList.估值表类型_产品单位净值.getValue());
                    listProdValDto.add(prodUnitPrice);

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
                        ystUnitPrice.setSubjectName(String.valueOf(bokStatBokPositionMap.get(ystday).getNetUnitValue()));
                    }
                    else {
                        ystUnitPrice.setSubjectName(BigDecimal.ZERO.toString());
                    }
                    ystUnitPrice.setLayeringId(dto.getLayeringId());
                    ystUnitPrice.setDetailDist(DictCodeList.估值表类型_昨日单位净值.getValue());
                    listProdValDto.add(ystUnitPrice);

                    // 出参.结构集.成立以来净值增长率(%) 设置
                    ProdValuationDto priceGrowRateSince = new ProdValuationDto(
                        "成立以来净值增长率(%)-" + dto.getSubjectNo(), layerProdTotUnitNet,
                        BigDecimal.valueOf(1.0000));
                    priceGrowRateSince.setLayeringId(dto.getLayeringId());
                    priceGrowRateSince.setDetailDist(DictCodeList.估值表类型_成立以来净值增长率.getValue());
                    listProdValDto.add(priceGrowRateSince);

                    // 变量.上一年末日产品累计净值
                    BigDecimal prodTotUnitNetLastYear = BigDecimal.ONE;
                    // if 账套汇总信息map.get（去年末） != null)
                    if (bokStatBokPositionMap.get(lastYearEnd) != null) {
                        // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（去年末）.累计分红净值 + 账套汇总信息map.get（去年末）.单位净值
                        prodTotUnitNetLastYear = ArithmeticUtil.doAdd(
                            bokStatBokPositionMap.get(lastYearEnd).getTotBounsNet(),
                            bokStatBokPositionMap.get(lastYearEnd).getNetUnitValue());
                    }
                    // 出参.结构集.净值年增长率(%)-变量.分层代码List.四级科目尾部名称: 设置
                    ProdValuationDto priceGrowRateYear = new ProdValuationDto(
                        "净值年增长率(%)-" + dto.getSubjectNo(), layerProdTotUnitNet,
                        prodTotUnitNetLastYear);
                    priceGrowRateYear.setLayeringId(dto.getLayeringId());
                    priceGrowRateYear.setDetailDist(DictCodeList.估值表类型_净值年增长率.getValue());
                    listProdValDto.add(priceGrowRateYear);

                    // 变量.上一季末日产品累计净值
                    BigDecimal prodTotUnitNetLastQuarter = BigDecimal.ONE;
                    // if 账套汇总信息map.get（上季末） != null)
                    if (bokStatBokPositionMap.get(lastQuarterEnd) != null) {
                        // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（上季末）.累计分红净值 + 账套汇总信息map.get（上季末）.单位净值
                        prodTotUnitNetLastQuarter = ArithmeticUtil.doAdd(
                            bokStatBokPositionMap.get(lastQuarterEnd).getTotBounsNet(),
                            bokStatBokPositionMap.get(lastQuarterEnd).getNetUnitValue());
                    }
                    // 出参.结构集.净值季度增长率(%): 设置
                    ProdValuationDto priceGrowRateQuarter = new ProdValuationDto(
                        "净值季度增长率(%)-" + dto.getSubjectNo(), layerProdTotUnitNet,
                        prodTotUnitNetLastQuarter);
                    priceGrowRateQuarter.setLayeringId(dto.getLayeringId());
                    priceGrowRateQuarter.setDetailDist(DictCodeList.估值表类型_净值季度增长率.getValue());
                    listProdValDto.add(priceGrowRateQuarter);

                    // 变量.上一月末日产品累计净值
                    BigDecimal prodTotUnitNetLastMonth = BigDecimal.ONE;
                    // if 账套汇总信息map.get（上月末） != null)
                    if (bokStatBokPositionMap.get(lastMonthEnd) != null) {
                        // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（上月末）.累计分红净值 + 账套汇总信息map.get（上月末）.单位净值
                        prodTotUnitNetLastMonth = ArithmeticUtil.doAdd(
                            bokStatBokPositionMap.get(lastMonthEnd).getTotBounsNet(),
                            bokStatBokPositionMap.get(lastMonthEnd).getNetUnitValue());
                    }
                    // 净值月增长率(%): 设置
                    ProdValuationDto priceGrowRateMonth = new ProdValuationDto(
                        "净值月增长率(%)-" + dto.getSubjectNo(), layerProdTotUnitNet,
                        prodTotUnitNetLastMonth);
                    priceGrowRateMonth.setLayeringId(dto.getLayeringId());
                    priceGrowRateMonth.setDetailDist(DictCodeList.估值表类型_净值月增长率.getValue());
                    listProdValDto.add(priceGrowRateMonth);

                    // 变量.上一周末日产品累计净值
                    BigDecimal prodTotUnitNetLastWeek = BigDecimal.ONE;
                    // if 账套汇总信息map.get（上周末） != null)
                    if (bokStatBokPositionMap.get(lastWeekend) != null) {
                        // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（上周末）.累计分红净值 + 账套汇总信息map.get（上周末）.单位净值
                        prodTotUnitNetLastWeek = ArithmeticUtil.doAdd(
                            bokStatBokPositionMap.get(lastWeekend).getTotBounsNet(),
                            bokStatBokPositionMap.get(lastWeekend).getNetUnitValue());
                    }
                    // 净值周增长率(%): 设置
                    ProdValuationDto priceGrowRateWeek = new ProdValuationDto(
                        "净值周增长率(%)-" + dto.getSubjectNo(), layerProdTotUnitNet,
                        prodTotUnitNetLastWeek);
                    priceGrowRateWeek.setLayeringId(dto.getLayeringId());
                    priceGrowRateWeek.setDetailDist(DictCodeList.估值表类型_净值周增长率.getValue());
                    listProdValDto.add(priceGrowRateWeek);

                    // 变量.昨日产品累计净值
                    BigDecimal prodTotUnitNetYst = BigDecimal.ONE;
                    // if 账套汇总信息map.get（上周末） != null)
                    if (bokStatBokPositionMap.get(ystday) != null) {
                        // 变量.上一年末日产品累计净值 = 账套汇总信息map.get（上周末）.累计分红净值 + 账套汇总信息map.get（上周末）.单位净值
                        prodTotUnitNetYst = ArithmeticUtil.doAdd(
                            bokStatBokPositionMap.get(ystday).getTotBounsNet(),
                            bokStatBokPositionMap.get(ystday).getNetUnitValue());
                    }
                    // 净值日增长率(%): 设置
                    ProdValuationDto priceGrowRateDay = new ProdValuationDto(
                        "净值日增长率(%)-" + dto.getSubjectNo(), layerProdTotUnitNet, prodTotUnitNetYst);
                    priceGrowRateDay.setLayeringId(dto.getLayeringId());
                    priceGrowRateDay.setDetailDist(DictCodeList.估值表类型_净值日增长率.getValue());
                    listProdValDto.add(priceGrowRateDay);
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
                    if (pyeildDigits != 0) {
                        pYield.setSubjectName(
                            (tdyBokPosition.getpYield() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.round(
                                tdyBokPosition.getpYield(), pyeildDigits, pyeildMode).toString());
                    }
                    else {
                        pYield.setSubjectName(
                            (tdyBokPosition.getpYield() == null) ? BigDecimal.ZERO.toString() : tdyBokPosition.getpYield().toString());
                    }
                    pYield.setLayeringId(dto.getLayeringId());
                    pYield.setDetailDist(DictCodeList.估值表类型_万份收益_费后.getValue());
                    listProdValDto.add(pYield);

                    // 出参.结构集.本日年化收益率： 设置
                    ProdValuationDto tdyYield = new ProdValuationDto();
                    // 科目代码 <- "本日年化收益率-变量.分层代码List.四级科目尾部名称："
                    tdyYield.setSubjectNo("本日年化收益率%-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.当日年化收益率
                    if (yeildDigits != 0) {
                        tdyYield.setSubjectName(
                            (tdyBokPosition.getTdyYield() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.multiply(
                                yeildDigits, yeildMode, tdyBokPosition.getTdyYield(),
                                new BigDecimal(100)).toString());
                    }
                    else {
                        tdyYield.setSubjectName(
                            (tdyBokPosition.getTdyYield() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.round(
                                ArithmeticUtil.multiply(tdyBokPosition.getTdyYield(),
                                    new BigDecimal(100)),
                                4, RoundingMode.HALF_UP).toString());
                    }
                    tdyYield.setLayeringId(dto.getLayeringId());
                    tdyYield.setDetailDist(DictCodeList.估值表类型_本日年化收益率.getValue());
                    listProdValDto.add(tdyYield);

                    // 出参.结构集.7日年化收益率： 设置
                    ProdValuationDto yield7 = new ProdValuationDto();
                    // 科目代码 <- "7日年化收益率-变量.分层代码List.四级科目尾部名称："
                    yield7.setSubjectNo("7日年化收益率%-" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.当日7日年化收益率
                    if (yeildDigits != 0) {
                        yield7.setSubjectName(
                            (tdyBokPosition.getYield7() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.multiply(
                                yeildDigits, yeildMode, tdyBokPosition.getYield7(),
                                new BigDecimal(100)).toString());
                    }
                    else {
                        yield7.setSubjectName(
                            (tdyBokPosition.getYield7() == null) ? BigDecimal.ZERO.toString() : ArithmeticUtil.round(
                                ArithmeticUtil.multiply(tdyBokPosition.getYield7(),
                                    new BigDecimal(100)),
                                4, RoundingMode.HALF_UP).toString());
                    }
                    yield7.setLayeringId(dto.getLayeringId());
                    yield7.setDetailDist(DictCodeList.估值表类型_7日年化收益率.getValue());
                    listProdValDto.add(yield7);

                    // 出参.结构集.从成立以来累计万份收益: 设置
                    ProdValuationDto pYieldSince = new ProdValuationDto();
                    // 科目代码 <- "从成立以来累计万份收益:"
                    pYieldSince.setSubjectNo("从成立以来累计万份收益-" + dto.getSubjectNo());
                    // 科目名称 <- 查询.从成立以来累计万份收益
                    if (valSchemeMapper.getLayerPYieldSumSince(booksetId, bookDate, null,
                        dto.getLayeringId()) != null) {
                        if (pyeildDigits != 0) {
                            pYieldSince.setSubjectName(
                                ArithmeticUtil.round(
                                    valSchemeMapper.getLayerPYieldSumSince(booksetId, bookDate,
                                        null, dto.getLayeringId()),
                                    pyeildDigits, pyeildMode).toString());
                        }
                        else {
                            pYield.setSubjectName(valSchemeMapper.getLayerPYieldSumSince(booksetId,
                                bookDate, null, dto.getLayeringId()).toString());
                        }
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
                    if (valSchemeMapper.getLayerPYieldSumSince(booksetId, bookDate, lastYearEnd,
                        dto.getLayeringId()) != null) {
                        if (pyeildDigits != 0) {
                            pYieldYear.setSubjectName(ArithmeticUtil.round(
                                valSchemeMapper.getLayerPYieldSumSince(booksetId, bookDate,
                                    lastYearEnd, dto.getLayeringId()),
                                pyeildDigits, pyeildMode).toString());
                        }
                        else {
                            pYieldYear.setSubjectName(valSchemeMapper.getLayerPYieldSumSince(
                                booksetId, bookDate, lastYearEnd, dto.getLayeringId()).toString());
                        }

                    }
                    else {
                        pYieldYear.setSubjectName(BigDecimal.ZERO.toString());
                    }
                    pYieldYear.setLayeringId(dto.getLayeringId());
                    pYieldYear.setDetailDist(DictCodeList.估值表类型_本年累积万份收益.getValue());
                    listProdValDto.add(pYieldYear);
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
                    tdyCustProfit.setSubjectNo("当日客户收益：" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.应付利润
                    tdyCustProfit.setSubjectName(
                        (tdyBokPosition.getProfitAmt() == null) ? BigDecimal.ZERO.toString() : tdyBokPosition.getProfitAmt().toString());
                    tdyCustProfit.setLayeringId(dto.getLayeringId());
                    tdyCustProfit.setDetailDist(DictCodeList.估值表类型_当日客户收益.getValue());
                    listProdValDto.add(tdyCustProfit);

                    // 出参.结构集.累计客户收益 设置
                    ProdValuationDto totalCustProfit = new ProdValuationDto();
                    totalCustProfit.setSubjectNo("累计客户收益：" + dto.getSubjectNo());
                    // 科目名称 <- 变量.当日账套汇总信息.应付利润
                    totalCustProfit.setSubjectName(
                        (tdyBokPosition.getTotProfitAmt() == null) ? BigDecimal.ZERO.toString() : tdyBokPosition.getTotProfitAmt().toString());
                    totalCustProfit.setLayeringId(dto.getLayeringId());
                    totalCustProfit.setDetailDist(DictCodeList.估值表类型_累计客户收益.getValue());
                    listProdValDto.add(totalCustProfit);

                    // 出参.结构集.本日客户收益率 设置
                    ProdValuationDto tdyCustProfitRate = new ProdValuationDto();
                    tdyCustProfitRate.setSubjectNo("当日客户收益率%：" + dto.getSubjectNo());
                    BigDecimal intRate = BigDecimal.ZERO;
                    // 母产品没有利率
                    if (StringUtil.isNotEmpty(tdyBokPosition.getIntRate())) {
                        // 成 本 <- 当日账套汇总信息.计提利率
                    	intRate = ArithmeticUtil.multiply(
                                yeildDigits, yeildMode, tdyBokPosition.getIntRate(),
                                new BigDecimal(100));
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
        result.setBooksetFullName(acctEntity.getBooksetFullName());
        result.setFinprodName(acctEntity.getFinprodName());
        // 净值确认（下发）
        this.generateNetConfirm(result);
        return result;
    }

    private void balanceSummarize(ProdValuationDto prodValuationDto, String subjectNo,
                                  HashMap<String, ProdValuationDto> 科目余额集合,
                                  Map<String, BokSubject> 科目集合, List<ProdValuationDto> 科目明细列表,
                                  boolean isNotXshz) {

        BokSubject 科目信息 = 科目集合.get(subjectNo);

        // if（公允价值变动列表.包含(变量.科目信息.基础科目性质)）
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
            // 本科目余额明细.setShadowPriceValue(prodValuationDto.getShadowPriceValue());
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
            // 本科目余额明细.setShadowPriceValue(ArithmeticUtil.doAdd(本科目余额明细.getShadowPriceValue(),
            // prodValuationDto.getShadowPriceValue()));
            if (ArithmeticUtil.equals(BigDecimal.ZERO, 本科目余额明细.getValueIncrement())) {
                本科目余额明细.setValueIncrement(null);
            }
            // if (ArithmeticUtil.equals(BigDecimal.ZERO, 本科目余额明细.getShadowPriceValue())) {
            // 本科目余额明细.setShadowPriceValue(null);
            // }
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
            bokValbalTableStatus.setAssetValue((result.getProdAssetVal().getMarketValue()));
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
                // TODO 此处应付利润只能取应付利润科目号去比对
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

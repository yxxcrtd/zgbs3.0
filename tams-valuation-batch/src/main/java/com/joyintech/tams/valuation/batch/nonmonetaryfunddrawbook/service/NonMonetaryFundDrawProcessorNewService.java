/*
 * 文件名：secDealAccountsProcessServices.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.nonmonetaryfunddrawbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokAssetMarketInfo;
import com.joyintech.tams.common.base.entity.BokBatchMarketwarningInfo;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.MarketPriceDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 非货币基金公允价值变动账务处理
 * 
 * @author wangwei1
 * @version 1.0 2018年6月04日
 * @see NonMonetaryFundDrawProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class NonMonetaryFundDrawProcessorNewService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 公用方法services
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;
    
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * process 数据处理
     * 
     * @param param1 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param1) {
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param1.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        TreeNode bookSetTreeNodNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 发生日
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        // 定义变量
        BigDecimal varCostSubjectAmt = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal varCostSubjectAmtBccy = BigDecimal.ZERO;// 变量.成本科目余额_本位币
        BigDecimal varCostSubjectNum = BigDecimal.ZERO;// 变量.成本科目数量
        BigDecimal varGyjzxbdSubjectAmt = BigDecimal.ZERO;// 变量.公允价值变动科目余额
        BigDecimal varGyjzxbdSubjectAmtBccy = BigDecimal.ZERO;// 变量.公允价值变动科目余额_本位币
        BigDecimal varGyjzxbdAmt = BigDecimal.ZERO; // 变量.公允价值变动
        BigDecimal varGyjzxbdAmtBccy = BigDecimal.ZERO;// 变量.公允价值变动_本位币
        BigDecimal varSjAmt = null;// 变量.市价
        // 定义 变量.估值日期
        Date valDate = null;

        // 汇总信息查询
        // 变量.统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
        BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
            acctEntity.getHappenDate(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
            acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);
        
        // 获取标的信息
        TargetFinProduct targetFinProduct = this.getTargetInfo(acctEntity.getBusiId());
        // 保证标的信息不为空
        if (targetFinProduct == null) {
            return null;
        }
        
        // 获取金融产品类型
        String finprodTypeNow = targetFinProduct.getFinprodType();

        // 1.公允价值变动科目余额（根据金融产品类型区分【非货基公允价值变动】【计划类净值型资产】）
//        String baseSubjectNatureForGyjz = DictCodeBaseSubjectNatrue.基金投资_估值增值.getValue();// 默认为【非货基公允价值变动】
//        if (DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(finprodTypeNow)) {
//            baseSubjectNatureForGyjz = DictCodeBaseSubjectNatrue.公允价值损益类_计划类资产_公允价值变动.getValue();
//        }
        String baseSubjectNatureForGyjz = DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue();
        SubjectBalance subjectBalance1 = this.getSubjectBalance(booksetIdNow,
            acctEntity.getBokDetailId(), happenDateNow, bookDateNow,
            DictCodeList.余额方向_借方.getValue(), baseSubjectNatureForGyjz, null, taskPri);
        if (subjectBalance1 != null) {
            varGyjzxbdSubjectAmt = subjectBalance1.getBalance() == null ? BigDecimal.ZERO : subjectBalance1.getBalance();
            varGyjzxbdSubjectAmtBccy = subjectBalance1.getbAmt() == null ? BigDecimal.ZERO : subjectBalance1.getbAmt();
        }

        // 2.成本科目余额 成本科目数量（根据金融产品类型区分【非货基公允价值变动】【计划类净值型资产】）
//        String baseSubjectNatureForCB = DictCodeBaseSubjectNatrue.基金投资_成本.getValue();// 默认为【非货基公允价值变动】
//        if (DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(finprodTypeNow)) {
//            baseSubjectNatureForCB = DictCodeBaseSubjectNatrue.公允价值损益类_计划类资产_成本.getValue();
//        }
        String baseSubjectNatureForCB = DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue();// 默认为【非货基公允价值变动】

        SubjectBalance subjectBalance2 = this.getSubjectBalance(booksetIdNow,
            acctEntity.getBokDetailId(), happenDateNow, bookDateNow,
            DictCodeList.余额方向_借方.getValue(), baseSubjectNatureForCB, null, taskPri);
        if (subjectBalance2 != null) {
            varCostSubjectAmt = subjectBalance2.getBalance() == null ? BigDecimal.ZERO : subjectBalance2.getBalance();
            varCostSubjectAmtBccy = subjectBalance2.getbAmt() == null ? BigDecimal.ZERO : subjectBalance2.getbAmt();
            varCostSubjectNum = subjectBalance2.getNum() == null ? BigDecimal.ZERO : subjectBalance2.getNum();
        }

        // 循环金融产品交易信息list,进行逻辑处理
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow) {
        	/**add by taoran 20191231 start**/
            // 汇率
            BigDecimal changeRate = BigDecimal.ONE;
            if(StringUtil.isNotEmpty(targetFinProduct.getCcy()) && !targetFinProduct.getCcy().equals(dto.getStandardCcy())){
                // 调用共通汇率查询
            	//changeRate = calculationCommonService.queryExchangeRateForTrade(targetFinProduct.getCcy(), dto.getStandardCcy(), acctEntity.getHappenDate());
                /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start**/
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(bookDateNow, booksetIdNow,
                        dto.getStandardCcy(), targetFinProduct.getCcy());
                if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                    changeRate = currencyMarketList.get(0).getClosePrice();
                }
                /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 end**/
            }
            /**add by taoran 20191231 end**/
            // 获取市价
            // 定义 变量.市价Dto
            MarketPriceDto marketPriceDto = new MarketPriceDto();
            if (DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(finprodTypeNow)) { // 【计划类净值型资产】情况
                // 调用共通 - 市价查询-计划类净值型
                marketPriceDto = finProductCommonService.getFundMarketPriceForPlanAsset(
                    happenDateNow, dto.getBusiId(), dto.getBooksetId());
            }
            else {// 【非货基公允价值变动】情况
                  // 调用共通 - 市价查询-基金
                marketPriceDto = finProductCommonService.getFundMarketPrice(happenDateNow,
                    booksetIdNow, dto.getBusiId());
            }
            // 变量.市价 = 变量.市价Dto.市价
            varSjAmt = marketPriceDto.getPrice();
            // 变量.估值日期 = 变量.市价Dto.估值日期
            valDate = marketPriceDto.getValDate();

            // 若无市价
            if (varSjAmt == null || BigDecimal.ZERO.compareTo(varSjAmt) == 0) {
                // 变量.净价(市价) = 变量.成本科目余额/变量.成本科目数量
                varSjAmt = ArithmeticUtil.divide(varCostSubjectAmt, varCostSubjectNum);
            }

            // 变量.公允价值变动 = （变量.净价*变量.成本科目数量 -变量.成本科目余额） -变量.公允价值变动科目余额
            varGyjzxbdAmt = ArithmeticUtil.doSub(
                ArithmeticUtil.multiply(varSjAmt, varCostSubjectNum), varCostSubjectAmt,
                varGyjzxbdSubjectAmt);
            //变量.公允价值变动_本位币 = 变量.市价*变量.成本科目数量 * 变量.汇率 - 变量.公允价值变动科目余额_本位币 -变量.成本科目余额_本位币
            varGyjzxbdAmtBccy = ArithmeticUtil.doSub(calculationCommonService.doMultiply(
    				varSjAmt,varCostSubjectNum,changeRate),varCostSubjectAmtBccy,varGyjzxbdSubjectAmtBccy);
    		
            // 调用金额共通方法设置有效小数位
            varGyjzxbdAmt = ArithmeticUtil.round(varGyjzxbdAmt, NumberEnum.MONEY);
            varGyjzxbdAmtBccy = ArithmeticUtil.round(varGyjzxbdAmtBccy, NumberEnum.MONEY);
            
            // 变量.标的信息.境内外
            String blnArea = targetFinProduct.getBlnArea();

            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // **********共通元素设值 - 设置账务引擎参数**********
            // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
            // ad.setTradeId(dealNow.getTradeId());//ad.交易编号=交易编号
            ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
            // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
            // 场景代码
            ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_公允价值变动.getValue());

            // ********** accountSceneMap设置如下**********
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, dto);
            accountSceneMap.put(BookConstant.BLN_AREA, blnArea);
            accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());
            accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());
            accountSceneMap.put(BookConstant.ASSET_TYPE, targetFinProduct.getFinprodType());
            // 备注 <- "非货币基金公允价值变动" REMARK
            accountSceneMap.put(BookConstant.BOOK_REMARK, "非货币基金公允价值变动");

            // ********** accountAmountMap设置如下**********

            // 发生数量<-交易明细.交易份额 + 交易明细.利息总额
            if (BigDecimal.ZERO.compareTo(varGyjzxbdAmt) < 0) {
            	// 公允价值变动_贷<- 变量.冲减公允价值变动 FLOAT_C
                accountAmountMap.put(BookConstant.FLOAT_D, varGyjzxbdAmt);
                // 公允价值变动损益_借<- 变量.冲减公允价值变动 FLOAT_INGPL_D
                accountAmountMap.put(BookConstant.FLOAT_INGPL_C, varGyjzxbdAmt);
                /**20200102 taoran add start**/
 	           	// 公允价值变动_借_本位币  <-  变量.公允价值变动_本位币 FLOAT_D
 	           	accountAmountMap.put(BookConstant.FLOAT_D + BookConstant.BCCYAMT, varGyjzxbdAmtBccy);
 	           	// 公允价值变动损益_贷_本位币  <- 变量.公允价值变动_本位币 FLOAT_INGPL_C
 	           	accountAmountMap.put(BookConstant.FLOAT_INGPL_C + BookConstant.BCCYAMT, varGyjzxbdAmtBccy);
 	           	/**20200102 taoran add end**/
            }
            else {
                // 公允价值变动_借<-变量.公允价值变动*-1 FLOAT_D
                accountAmountMap.put(BookConstant.FLOAT_C, varGyjzxbdAmt.negate());
                // 公允价值变动损益_贷<-变量.公允价值变动*-1 FLOAT_INGPL_C
                accountAmountMap.put(BookConstant.FLOAT_INGPL_D, varGyjzxbdAmt.negate());
                /**20200102 taoran add start**/
 	           	// 公允价值变动_贷_本位币  <- 变量.公允价值变动_本位币*-1	 FLOAT_C
 	           	accountAmountMap.put(BookConstant.FLOAT_C, varGyjzxbdAmtBccy.negate());
 	           	// 公允价值变动损益_借_本位币  <-  变量.公允价值变动_本位币*-1 FLOAT_INGPL_D
 	           	accountAmountMap.put(BookConstant.FLOAT_INGPL_D, varGyjzxbdAmtBccy.negate());
 	           	/**20200102 taoran add end**/
            }

            // 记账金额元素币种信息塞值
            // 因为与记账金额元素map的key及数量一致，故采用循环
            for (String keyNow : accountAmountMap.keySet()) {
                accountCurrencyMap.put(keyNow, dto.getoCcy());// value值塞入当前交易币种 （当前为获取计提里面的原币）
            }
            accountCurrencyMap.put(BookConstant.B_CCY, dto.getStandardCcy());
            // ********* 全局变量更新 **********
            // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 + 变量.公允价值变动
            varGyjzxbdSubjectAmt = ArithmeticUtil.doAdd(varGyjzxbdSubjectAmt, varGyjzxbdAmt);

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
            ad.setSubjectMappingConfigDefault(dto.getSubjectMappingConfig());
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());

            // 3.3 统计分析账务明细汇总信息赋值
            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            if (bokSummaryTableYestdy == null
                || StringUtils.isEmpty(bokSummaryTableYestdy.getBooksetId())) {
                // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
                bokSummaryTableNew.setPosAmt(varCostSubjectNum);
                // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
                bokSummaryTableNew.setIntRate(null);
                // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =NULL
                bokSummaryTableNew.setDailydscYield(null);
                // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
                bokSummaryTableNew.setTdyIntincexpAdd(null);
                // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
                bokSummaryTableNew.setTdyIntincexp(null);
                // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.数量余额
                bokSummaryTableNew.setTdyPosition(varCostSubjectNum);
                // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
                bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
                // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
                bokSummaryTableNew.setTdyCostAmt(varCostSubjectAmt);
                // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
                bokSummaryTableNew.setFinprodType(dto.getBusiDetailType());
                // 账套代码
                bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
                // 发生日期
                bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
                // 入账日期
                bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
                // 账务明细代码
                bokSummaryTableNew.setBokDetailId(acctEntity.getBokDetailId());
                // 账套日期
                bokSummaryTableNew.setBooksetDate(dto.getBooksetDate());
                // 处理序列
                bokSummaryTableNew.setBookSummaryOrder(
                    new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
                // 起息日
                bokSummaryTableNew.setVdate(targetFinProduct.getVdate());
                // 到期日
                bokSummaryTableNew.setMdate(targetFinProduct.getActmdate());
                // 变量.当日统计分析账务明细汇总信息.当日公允价值变动 = 变量.公允价值变动
                bokSummaryTableNew.setTdyFloatIngpl(varGyjzxbdAmt);
                //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动 = 变量.公允价值变动科目余额 + 变量.公允价值变动 
                bokSummaryTableNew.setTdyFloatIngplExp(varGyjzxbdSubjectAmt);
                // 变量.当日统计分析账务明细汇总信息.行情日期 = 变量.估值日期
                bokSummaryTableNew.setValDate(valDate);
                // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
                bokSummaryTableNew.setBusiId(acctEntity.getBusiId());
                
                bokSummaryTableNew.setCcy(targetFinProduct.getCcy());
                
            }
            else {
                BeanUtil.copyPropertiesIgnoreNull(bokSummaryTableNew, bokSummaryTableYestdy);
                // 变量.当日统计分析账务明细汇总信息.当日公允价值变动 = 变量.公允价值变动
                bokSummaryTableNew.setTdyFloatIngpl(varGyjzxbdAmt);
                //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动 = 变量.公允价值变动科目余额 + 变量.公允价值变动 
                bokSummaryTableNew.setTdyFloatIngplExp(varGyjzxbdSubjectAmt);
                // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 变量.标的信息.金融产品类型
                bokSummaryTableNew.setFinprodType(dto.getBusiDetailType());
                // 变量.当日统计分析账务明细汇总信息.行情日期 = 变量.估值日期
                bokSummaryTableNew.setValDate(valDate);
            }


            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== beign
            //本位币
            bokSummaryTableNew.setbCcy(bokSummaryTableNew.getCcy());
            //当日发生应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(bokSummaryTableNew.getTdyIntincexpAdd());
            // 当日成本_本位币
            bokSummaryTableNew.setTdyCostAmtB(bokSummaryTableNew.getTdyCostAmt());
            // 当日公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplB(bokSummaryTableNew.getTdyFloatIngpl());
            // 当日累计公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplExpB(bokSummaryTableNew.getTdyFloatIngplExp());
            // 当日应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpB(bokSummaryTableNew.getTdyIntincexp());
            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== end



            // 添加到返回值中
            returnResult.getBokSummaryList().add(bokSummaryTableNew);
            
            //非货币基金市价警告信息
            if (!DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(finprodTypeNow)) {
                // 3.3 行情价格警告信息赋值      ------add wangwei1 20190131
                BokBatchMarketwarningInfo bokBatchMarketwarningInfo =new BokBatchMarketwarningInfo();
                //1.获取批处理信息 
                SysBatchExecInfo sysBatchExecInfo = (SysBatchExecInfo) param1.get("BATCHINFO");
                 //变量.行情价格警告信息DTO.流水号
                bokBatchMarketwarningInfo.setSeqNo(IDGenerator.generate());
                //变量.行情价格警告信息DTO.批处理任务组执行ID
                bokBatchMarketwarningInfo.setBatchExeSeqNo(sysBatchExecInfo.getBatchExedetailSeqNo());
                //变量.行情价格警告信息DTO.任务代码
                bokBatchMarketwarningInfo.setTaskId(sysBatchExecInfo.getTaskId());
                // 变量.行情价格警告信息DTO.账套代码
                bokBatchMarketwarningInfo.setBooksetId(acctEntity.getBooksetId());
                // 变量.行情价格警告信息DTO.发生日期
                bokBatchMarketwarningInfo.setHappenDate(acctEntity.getHappenDate());
                // 变量.行情价格警告信息DTO.账套日期
                bokBatchMarketwarningInfo.setBooksetDate(dto.getBooksetDate());
                // 变量.行情价格警告信息DTO.入账日期
                bokBatchMarketwarningInfo.setBookDate(acctEntity.getBookDate());
    			//变量.行情价格警告信息DTO.金融产品代码 = 变量.标的信息.金融产品代码		
                bokBatchMarketwarningInfo.setFinprodId(targetFinProduct.getFinprodId());
    			//变量.行情价格警告信息DTO.金融产品类型 = 变量.标的信息.金融产品类型		
                bokBatchMarketwarningInfo.setFinprodType(targetFinProduct.getFinprodType());
    			//变量.行情价格警告信息DTO.投资目的 = 计提类账务处理结果.投资目的	
                bokBatchMarketwarningInfo.setInvAim(acctEntity.getInvAim());  
    			//变量.行情价格警告信息DTO.市场代码 = 变量.标的信息.市场代码	
                bokBatchMarketwarningInfo.setMarketCode(targetFinProduct.getFinprodMarketId());
                
              //变量.行情价格警告信息DTO.估值来源 = 市价共通返回.估值来源	
                bokBatchMarketwarningInfo.setValueSource(marketPriceDto.getValueSource());
    			//变量.行情价格警告信息DTO.理论行情日期 = 市价共通返回.理论行情日期			
                bokBatchMarketwarningInfo.setTheValueDate(marketPriceDto.getSchemeValDate()); 
    			//变量.行情价格警告信息DTO.实际行情日期 = 市价共通返回.实际行情日期	
                bokBatchMarketwarningInfo.setActValueDate(marketPriceDto.getValDate());
              //变量.行情价格警告信息DTO.实际行情价格 = 市价共通返回.实际行情价格
                bokBatchMarketwarningInfo.setActMarketPrice(marketPriceDto.getPrice());
    			//变量.行情价格警告信息DTO.是否一致 = 市价共通返回.是否一致	
                bokBatchMarketwarningInfo.setYesOrNo(DictCodeList.是否_是.getValue());
                
                //如果不一致插入警告表
                if(marketPriceDto.getValDate()==null ||marketPriceDto.getSchemeValDate().compareTo(marketPriceDto.getValDate())!=0){
                	bokBatchMarketwarningInfo.setYesOrNo(DictCodeList.是否_否.getValue());
                	// 添加到返回值中
                	returnResult.getBokBatchMarketwarningInfoList().add(bokBatchMarketwarningInfo); 
                }

                // add by weipeng 20210807：增加资产行情信息入库
                // 变量.资产行情信息DTO
                BokAssetMarketInfo bokAssetMarketInfo = new BokAssetMarketInfo();
                //变量.资产行情信息DTO.流水号
                bokAssetMarketInfo.setSeqNo(IDGenerator.generate());
                // 变量.资产行情信息DTO.账套代码
                bokAssetMarketInfo.setBooksetId(acctEntity.getBooksetId());
                // 变量.资产行情信息DTO.发生日期
                bokAssetMarketInfo.setHappenDate(acctEntity.getHappenDate());
                // 变量.资产行情信息DTO.入账日期
                bokAssetMarketInfo.setBookDate(acctEntity.getBookDate());
                // 变量.资产行情信息DTO.金融产品代码 = 变量.标的信息.金融产品代码
                bokAssetMarketInfo.setFinprodId(targetFinProduct.getFinprodId());
                // 变量.资产行情信息DTO.金融产品类型 = 变量.标的信息.金融产品类型
                bokAssetMarketInfo.setFinprodType(targetFinProduct.getFinprodType());
                // 变量.资产行情信息DTO.投资目的 = 计提类账务处理结果.投资目的
                bokAssetMarketInfo.setInvAim(acctEntity.getInvAim());
                // 变量.资产行情信息DTO.市场代码 = 变量.标的信息.市场代码
                bokAssetMarketInfo.setMarketCode(targetFinProduct.getFinprodMarketId());
                // 变量.资产行情信息DTO.市价 = 变量.市价
                bokAssetMarketInfo.setMarketPrice(varSjAmt);
                // 变量.资产行情信息DTO.是否来源于行情数据
                // if 市价共通返回.市价== null || 变量.市价!=市价共通返回.市价
                if(marketPriceDto == null || marketPriceDto.getPrice() == null ||  varSjAmt.compareTo(marketPriceDto.getPrice()) != 0){
                    // 变量.资产行情信息DTO.行情日期 = 变量.发生日期
                    bokAssetMarketInfo.setValueDate(happenDateNow);
                    bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_系统生成.getValue());
                }
                else{
                    // 变量.资产行情信息DTO.估值来源 = 市价共通返回.估值来源
                    bokAssetMarketInfo.setValueSource(marketPriceDto.getValueSource());
                    // 变量.资产行情信息DTO.行情日期 = 市价共通返回.行情日期
                    bokAssetMarketInfo.setValueDate(marketPriceDto.getValDate());
                    // 变量.资产行情信息DTO.估值方案代码 = 市价共通返回.估值方案代码
                    bokAssetMarketInfo.setSchemeId(marketPriceDto.getSchemeId());
                    // 变量.资产行情信息DTO.估值方案明细代码 = 市价共通返回.估值方案明细代码
                    bokAssetMarketInfo.setSchemeDetailId(marketPriceDto.getSchemeDetailId());
                    bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_行情信息.getValue());
                }
                // 添加到返回值中
                returnResult.getBokAssetMarketInfoList().add(bokAssetMarketInfo);
                }
        }

        return returnResult;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param invAim 投资目的
     * @param taskPri 任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                             Date bookDate, String expBalanceDirect,
                                             String baseSubjectNature, String invAim,
                                             Long taskPri) {

        // 定义返回的model
        SubjectBalance subjectBalance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 塞入投资目的
        List<String> invAimList = null;
        if (invAim != null && invAim.length() > 0) {
            invAimList = new ArrayList<String>();
            invAimList.add(invAim);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setInvAimList(invAimList);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);
        if (subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
    }

    /**
     * 获取标的信息
     * 
     * @param busiId
     * @return
     * @see
     * @since 1.0
     */
    private TargetFinProduct getTargetInfo(String busiId) {
        // 获取标的信息
        // 查询类型
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(targetSearchDto);

        // 调用共通方法
        Map<TargetSearchDto, Object> targetInfo = finProductCommonService.getTargetInfo(busiId,
            queryCriteria);
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetInfo.get(targetSearchDto);

        return targetFinProduct;
    }

}

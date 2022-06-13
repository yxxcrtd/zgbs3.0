/*
 * 文件名：IrsFairValeAlterationsProcessorNewService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5
 * 修改人：殷亚
 * 修改时间：2022年4月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.batch.irsfairvalealterations.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.CalCommonResult;
import com.joyintech.tams.valuation.batch.common.calculation.entity.PrincipalInfo;
import com.joyintech.tams.valuation.batch.common.calculation.entity.QuoteInfo;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.irscalbook.service.IrsCalBookNewService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.batch.finproduct.service.FinProductCommonBatchService;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 利率互换公允价值变动账务处理 Processor
 * @author 殷亚
 * @date 2022/4/28 11:31
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsFairValeAlterationsProcessorNewService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 标的类共通方法类
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 账务类共通方法类
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;
    
    @Autowired
    private CalculationCommonService calculationCommonService;
    /**
     * 标的类共通方法Service
     */
    @Autowired
    private FinProductCommonBatchService finProductCommonBatchService;

    /**
     * 债权类资产公允价值批处理的业务处理
     * 
     * @param drawBaseInfoSelectComResult 计提基础信息取得结果DTO
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResultWithBokSummary execute(DrawBaseInfoSelectComResult drawBaseInfoSelectComResult,
                                                    Map<String, Object> param1) {
        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult) param1.get(
                GlobalConstant.READER_RESULT);
        // 获取从reader中拿到的值
        Date happenDate = drawBaseInfoSelectComResult.getHappenDate();
        // 入账日
        Date bookDate = drawBaseInfoSelectComResult.getBookDate();
        // 账套代码
        String booksetId = drawBaseInfoSelectComResult.getBooksetId();
        // 金融产品代码
        String bokDetailId = drawBaseInfoSelectComResult.getBokDetailId();
        // 投资目的
        String invAim = drawBaseInfoSelectComResult.getInvAim();
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）
        String standardCcy = listDrawBaseInfoSelectComDTONow.get(0).getStandardCcy(); // 本位币币种

        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param1.get(GlobalConstant.BATCH_INFO);
        // 1. 数据准备
        // 1.1 定义变量
        // 账套核算参数查询
        List<String> paramCodeList = new ArrayList<String>();
        // 3. 处理交易
        // 3.1、循环处理计提明细
        // 循环 计提基础信息列表 进行逻辑处理
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow) {
            // 成本科目余额
            BigDecimal cbkmBalancce = BigDecimal.ZERO;
            // 成本科目数量
            BigDecimal cbkmNum = BigDecimal.ZERO;
            // 公允价值变动科目余额
            BigDecimal secFairValeAltBalance = BigDecimal.ZERO;
            // 公允价值变动科目余额_本位币
            BigDecimal secFairValeAltBalance_bccy = BigDecimal.ZERO;
            // 公允价值变动_本位币
            BigDecimal secFairValeAlt_bccy = BigDecimal.ZERO;
            // 溢折价科目余额
            BigDecimal spilledPriceBalance = BigDecimal.ZERO;
            // 原币中
            String oCcy = null;
            // 市价
            BigDecimal marketPrice = BigDecimal.ZERO;
            // 公允价值变动
            BigDecimal secFairValeAlt = BigDecimal.ZERO;
            BigDecimal varDrjscbKmsl = BigDecimal.ZERO;
            // 估值日期
            Date valDate = null;
            String baseSubjectNature = null;
            String expBalanceDirect = DictCodeList.余额方向_借方.getValue();
            // 调用共通方法【科目余额查询-期待方向】,获取成本类科目余额
            baseSubjectNature = DictCodeBaseSubjectNatrueNew.应收利率互换.getValue();
            //成本科目
            SubjectBalance subjectBalance = this.getSubjectBalanceExpectDirection(booksetId,
                    bokDetailId, happenDate, baseSubjectNature, expBalanceDirect, taskPri,batchExecuteInfo.getBatchExedetailSeqNo());
            cbkmBalancce = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            cbkmNum = subjectBalance.getNum() == null ? BigDecimal.ZERO : subjectBalance.getNum();
            // 变量.当日计算成本科目数量 = 变量.当日成本科目数量
            varDrjscbKmsl = cbkmNum;
            // 变量.当日计算成本科目余额 = 变量.当日成本科目余额
            BigDecimal varDrjscbKmye = cbkmBalancce;
            // 变量.当日计算摊销溢折价科目余额 = 变量.溢折价科目余额
            BigDecimal varDrjstxzyjKmye = spilledPriceBalance;

            baseSubjectNature = DictCodeBaseSubjectNatrueNew.利率互换公允价值变动.getValue();
            subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri,batchExecuteInfo.getBatchExedetailSeqNo());
            secFairValeAltBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            secFairValeAltBalance_bccy = subjectBalance.getbAmt() == null ? BigDecimal.ZERO : subjectBalance.getbAmt();

            // 变量.当日计算公允价值变动科目余额 = 变量.公允价值变动科目余额
            BigDecimal varDrjsgyjzbdKmye = secFairValeAltBalance;

            // 变量.当日计算公允价值变动科目余额 _本位币= 变量.公允价值变动科目余额 _本位币
            BigDecimal varDrjsgyjzbdKmye_bccy = secFairValeAltBalance_bccy;

            // 获取标的信息.金融产品类型
            TargetFinProduct targetFinProduct = acctEntity.getTargetFinProduct();
            Date mdateYsdWithTarget = this.getYesterday(targetFinProduct.getMdate());
            // if (标的信息.起息日 > 计提类账务处理结果.发生日 )
            if (DateUtil.compareDate(targetFinProduct.getVdate(), happenDate) > 0) {
                return null;
            }
            MstValIrs marketPriceDto=new MstValIrs();
            //查詢估值方法方案
            List<SchemeDetailDto> listSchemeDetailDto =finProductCommonBatchService.getSchemeDetail(happenDate, booksetId, targetFinProduct.getType18(), DictCodeListExt1.配置方案类型_估值方案.getValue(), targetFinProduct.getTradeMarket(), targetFinProduct.getType1(), invAim);
            SchemeDetailDto schemeDetailDto=listSchemeDetailDto.get(0);
            if (DateUtil.compareDate(happenDate, mdateYsdWithTarget) <= 0) {
                //估值來源
                String valuationSource=schemeDetailDto.getValuationPrice();
                //估值模型
                String valuationId=schemeDetailDto.getValuationId();
                //行情位数
                BigDecimal quotaDigit=schemeDetailDto.getQuotaDigit();
                //行情尾数处理方式
                String priceTailMode=schemeDetailDto.getPriceTailMode();

                Map<String, Object> map=new HashMap<String, Object>();
                map.put("finprodId",targetFinProduct.getFinprodId());
                map.put("happenDate",happenDate);
                map.put("datasourceType",valuationSource);
                map.put("valmodelId",valuationId);

                marketPriceDto = finProductCommonService.getIrsMarketPrice(map,quotaDigit,priceTailMode);
                if(marketPriceDto!=null){
                    // 市价 = 返回值.市价
                    marketPrice = marketPriceDto.getValuation();
                    // 估值日期 = 返回值.估值日期
                    valDate = marketPriceDto.getValDate();
                }else{
                    marketPrice = BigDecimal.ZERO;
                }


            } else {
                marketPrice = BigDecimal.ZERO;
            }

            // 汇率
            BigDecimal changeRate = BigDecimal.ONE;
            if(StringUtil.isNotEmpty(targetFinProduct.getCcy()) && !targetFinProduct.getCcy().equals(dto.getStandardCcy())){
                // 调用共通汇率查询
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(bookDate, booksetId,
                        dto.getStandardCcy(), targetFinProduct.getCcy());
                if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                    changeRate = currencyMarketList.get(0).getClosePrice();
                }
            }

            if (cbkmNum.compareTo(BigDecimal.ZERO) == 0
                    && varDrjscbKmsl.compareTo(BigDecimal.ZERO) == 0) {
                // 变量.公允价值变动 = -1*变量.公允价值变动科目余额
                secFairValeAlt = secFairValeAltBalance.negate();

                // 变量.公允价值变动 _本位币= -1*变量.公允价值变动科目余额_本位币
                secFairValeAlt_bccy = secFairValeAltBalance_bccy.negate();
            }
            else {
                // if（变量.市价 = null）
                if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
                    // 变量.市价 = （变量.当日计算成本科目余额+变量.当日计算溢折价科目余额）/变量.当日计算成本科目数量
                    marketPrice = ArithmeticUtil.divide(
                            ArithmeticUtil.doAdd(varDrjscbKmye, varDrjstxzyjKmye), varDrjscbKmsl,
                            NumberEnum.HIGH_PREC_NUM.getCode());
                }
                secFairValeAlt = marketPrice;
                secFairValeAlt_bccy = calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, secFairValeAlt, changeRate);

                secFairValeAlt = ArithmeticUtil.doSub(secFairValeAlt,varDrjsgyjzbdKmye);

                secFairValeAlt_bccy = ArithmeticUtil.doSub(secFairValeAlt_bccy,varDrjsgyjzbdKmye_bccy);


            }

            // 组装记账元素
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();

            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // 共通元素设值 - 设置账务引擎参数
            // 塞入记账场景树节点 = 会计场景
            ad.setTreeNode(bookSetTreeNodeNow);
            // ad.会计场景代码 = 利率互换_每日公允价值变动;
            ad.setTableId(DictCodeListTableId.会计场景代码_利率互换_每日公允价值变动.getValue());
            // 记账场景要素map塞值
            // 调用共通场景元素赋值-计提类场景元素
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, dto);
            // 备注 <- "存款类资产每日计提"
            accountSceneMap.put(BookConstant.BOOK_REMARK, "利率互换每日公允价值变动");
            accountSceneMap.put(BookConstant.TRADE_ID, dto.getBusiId());
            // 分类1 <- 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());
            // 分类2 <- 变量.交易信息.分类2
            accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());

            // 记账金额元素map塞值
            // 定义 变量.汇率 = 1
            // 公允价值变动（贷）
            accountAmountMap.put(BookConstant.FLOAT_C, secFairValeAlt);
            //公允价值变动（借）
            accountAmountMap.put(BookConstant.FLOAT_D, secFairValeAlt);
            //公允价值变动损益（贷）
            accountAmountMap.put(BookConstant.FLOAT_INGPL_C, secFairValeAlt);
            //公允价值变动损益（借）
            accountAmountMap.put(BookConstant.FLOAT_INGPL_D, secFairValeAlt);
            for (String key : accountAmountMap.keySet()) {
                accountCurrencyMap.put(key, dto.getoCcy());
            }
            secFairValeAltBalance = ArithmeticUtil.doAdd(secFairValeAltBalance,
                    secFairValeAlt);
            secFairValeAltBalance_bccy = ArithmeticUtil.doAdd(secFairValeAltBalance_bccy,secFairValeAlt_bccy);
            // 本位币 <- 变量.本位币
            accountCurrencyMap.put(BookConstant.B_CCY, standardCcy);
            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            ad.setSubjectMappingConfigDefault(dto.getSubjectMappingConfig());
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                    resultNowNow.getValBokBookdetailBatch());

            // 3.3 统计分析账务明细汇总信息赋值
            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            // 变量.统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
            BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
                    drawBaseInfoSelectComResult.getHappenDate(),
                    drawBaseInfoSelectComResult.getHappenDate(), drawBaseInfoSelectComResult.getBookDate(),
                    drawBaseInfoSelectComResult.getBooksetId(),
                    drawBaseInfoSelectComResult.getBokDetailId(), null, null,null);
            if (bokSummaryTableYestdy == null
                    || StringUtils.isEmpty(bokSummaryTableYestdy.getBooksetId())) {
                // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
                bokSummaryTableNew.setPosAmt(cbkmNum);
                // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
                bokSummaryTableNew.setIntRate(null);
                // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =NULL
                bokSummaryTableNew.setDailydscYield(null);
                // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
                bokSummaryTableNew.setTdyIntincexpAdd(null);
                // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
                bokSummaryTableNew.setTdyIntincexp(null);
                // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.当日计算成本科目数量
                bokSummaryTableNew.setTdyPosition(varDrjscbKmsl);
                // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
                bokSummaryTableNew.setInvAim(drawBaseInfoSelectComResult.getInvAim());
                // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
                bokSummaryTableNew.setTdyCostAmt(ArithmeticUtil.doAdd(cbkmBalancce, spilledPriceBalance));
                // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
                bokSummaryTableNew.setFinprodType(targetFinProduct.getFinprodType());
                // 账套代码
                bokSummaryTableNew.setBooksetId(drawBaseInfoSelectComResult.getBooksetId());
                // 发生日期
                bokSummaryTableNew.setHappenDate(drawBaseInfoSelectComResult.getHappenDate());
                // 入账日期
                bokSummaryTableNew.setBookDate(drawBaseInfoSelectComResult.getBookDate());
                // 账务明细代码
                bokSummaryTableNew.setBokDetailId(bokDetailId);
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
                bokSummaryTableNew.setTdyFloatIngpl(secFairValeAlt);
                //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动 = 变量.公允价值变动科目余额 + 变量.公允价值变动
                bokSummaryTableNew.setTdyFloatIngplExp(secFairValeAltBalance);

                // 变量.当日统计分析账务明细汇总信息.剩余期限 = 返回.剩余期限
                bokSummaryTableNew.setEndDays1(new BigDecimal(DateUtil.getBetweenDayNumber(
                        dto.getHappenDate(), targetFinProduct.getActmdate())));

                // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 返回.剩余存续期限
                bokSummaryTableNew.setEndDays2(new BigDecimal(DateUtil.getBetweenDayNumber(
                        dto.getHappenDate(), targetFinProduct.getActmdate())));

                // 变量.当日统计分析账务明细汇总信息.剩余期限用成本 = 变量.当日成本科目余额 + 变量.溢折价科目余额
                bokSummaryTableNew.setEndDaysCostAmt(
                        ArithmeticUtil.doAdd(cbkmBalancce, spilledPriceBalance));
                // 变量.当日统计分析账务明细汇总信息.行情日期= 计提类账务处理结果.估值日期
                bokSummaryTableNew.setValDate(valDate);
                // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
                bokSummaryTableNew.setBusiId(drawBaseInfoSelectComResult.getBusiId());
                bokSummaryTableNew.setCcy(targetFinProduct.getCcy());
                // 变量.当日统计分析账务明细汇总信息.当日公允价值变动_本位币 = 变量.公允价值变动_本位币
                bokSummaryTableNew.setTdyFloatIngplB(secFairValeAlt_bccy);
                //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动_本位币 = 变量.公允价值变动科目余额_本位币 + 变量.公允价值变动_本位币
                bokSummaryTableNew.setTdyFloatIngplExpB(secFairValeAltBalance_bccy);
            }
            else {
                BeanUtil.copyPropertiesIgnoreNull(bokSummaryTableNew, bokSummaryTableYestdy);
                // 变量.当日统计分析账务明细汇总信息.当日公允价值变动 = 变量.公允价值变动
                bokSummaryTableNew.setTdyFloatIngpl(secFairValeAlt);
                //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动 = 变量.公允价值变动科目余额 + 变量.公允价值变动
                bokSummaryTableNew.setTdyFloatIngplExp(secFairValeAltBalance);
                // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 变量.标的信息.金融产品类型
                bokSummaryTableNew.setFinprodType(dto.getBusiDetailType());
                // 变量.当日统计分析账务明细汇总信息.行情日期= 计提类账务处理结果.估值日期
                bokSummaryTableNew.setValDate(valDate);
                // 变量.当日统计分析账务明细汇总信息.当日公允价值变动_本位币 = 变量.公允价值变动_本位币
                bokSummaryTableNew.setTdyFloatIngplB(secFairValeAlt_bccy);
                //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动_本位币 = 变量.公允价值变动科目余额_本位币 + 变量.公允价值变动_本位币
                bokSummaryTableNew.setTdyFloatIngplExpB(secFairValeAltBalance_bccy);

            }

            //本位币
            bokSummaryTableNew.setbCcy(bokSummaryTableNew.getCcy());
            //当日发生应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(bokSummaryTableNew.getTdyIntincexpAdd());
            // 当日成本_本位币
            bokSummaryTableNew.setTdyCostAmtB(bokSummaryTableNew.getTdyCostAmt());
            // 当日应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpB(bokSummaryTableNew.getTdyIntincexp());
            // 添加到返回值中
            returnResult.getBokSummaryList().add(bokSummaryTableNew);


            // 3.3 行情价格警告信息赋值
//            BokBatchMarketwarningInfo bokBatchMarketwarningInfo =new BokBatchMarketwarningInfo();
//            //1.获取批处理信息
//            SysBatchExecInfo sysBatchExecInfo = (SysBatchExecInfo) param1.get(GlobalConstant.BATCH_INFO);
//            //变量.行情价格警告信息DTO.流水号
//            bokBatchMarketwarningInfo.setSeqNo(IDGenerator.generate());
//            //变量.行情价格警告信息DTO.批处理任务组执行ID
//            bokBatchMarketwarningInfo.setBatchExeSeqNo(sysBatchExecInfo.getBatchExedetailSeqNo());
//            //变量.行情价格警告信息DTO.任务代码
//            bokBatchMarketwarningInfo.setTaskId(sysBatchExecInfo.getTaskId());
//            // 变量.行情价格警告信息DTO.账套代码
//            bokBatchMarketwarningInfo.setBooksetId(booksetId);
//            // 变量.行情价格警告信息DTO.发生日期
//            bokBatchMarketwarningInfo.setHappenDate(happenDate);
//            // 变量.行情价格警告信息DTO.账套日期
//            bokBatchMarketwarningInfo.setBooksetDate(bookDate);
//            // 变量.行情价格警告信息DTO.入账日期
//            bokBatchMarketwarningInfo.setBookDate(bookDate);
//            //变量.行情价格警告信息DTO.金融产品代码 = 变量.标的信息.金融产品代码
//            bokBatchMarketwarningInfo.setFinprodId(targetFinProduct.getFinprodId());
//            //变量.行情价格警告信息DTO.金融产品类型 = 变量.标的信息.金融产品类型
//            bokBatchMarketwarningInfo.setFinprodType(targetFinProduct.getFinprodType());
//            //变量.行情价格警告信息DTO.投资目的 = 计提类账务处理结果.投资目的
//            bokBatchMarketwarningInfo.setInvAim(invAim);
//            //变量.行情价格警告信息DTO.市场代码 = 变量.标的信息.市场代码
//            bokBatchMarketwarningInfo.setMarketCode(targetFinProduct.getFinprodMarketId());
//
//            //变量.行情价格警告信息DTO.估值来源 = 市价共通返回.估值来源
//            bokBatchMarketwarningInfo.setValueSource(marketPriceDto.getDatasourceType());
//            //变量.行情价格警告信息DTO.理论行情日期 = 市价共通返回.理论行情日期
//            bokBatchMarketwarningInfo.setTheValueDate(marketPriceDto.getValDate());
//            //变量.行情价格警告信息DTO.实际行情日期 = 市价共通返回.实际行情日期
//            bokBatchMarketwarningInfo.setActValueDate(marketPriceDto.getValDate());
//            //变量.行情价格警告信息DTO.实际行情价格 = 市价共通返回.实际行情价格
//            bokBatchMarketwarningInfo.setActMarketPrice(marketPriceDto.getValuation());
//
//            //变量.行情价格警告信息DTO.是否一致 = 市价共通返回.是否一致	，如果不一致 添加到返回值中
//            // bokBatchMarketwarningInfo.setYesOrNo(DictCodeList.是否_是.getValue());
//            if(marketPriceDto.getValDate()==null || marketPriceDto.getValDate().compareTo(marketPriceDto.getValDate())!=0){
//                bokBatchMarketwarningInfo.setYesOrNo(DictCodeList.是否_否.getValue());
//                returnResult.getBokBatchMarketwarningInfoList().add(bokBatchMarketwarningInfo);
//            }

            // 变量.资产行情信息DTO
            BokAssetMarketInfo bokAssetMarketInfo = new BokAssetMarketInfo();
            //变量.资产行情信息DTO.流水号
            bokAssetMarketInfo.setSeqNo(IDGenerator.generate());
            // 变量.资产行情信息DTO.账套代码
            bokAssetMarketInfo.setBooksetId(dto.getBooksetId());
            // 变量.资产行情信息DTO.发生日期
            bokAssetMarketInfo.setHappenDate(dto.getHappenDate());
            // 变量.资产行情信息DTO.入账日期
            bokAssetMarketInfo.setBookDate(bookDate);
            // 变量.资产行情信息DTO.金融产品代码 = 变量.标的信息.金融产品代码
            bokAssetMarketInfo.setFinprodId(targetFinProduct.getFinprodId());
            // 变量.资产行情信息DTO.金融产品类型 = 变量.标的信息.金融产品类型
            bokAssetMarketInfo.setFinprodType(targetFinProduct.getFinprodType());
            // 变量.资产行情信息DTO.投资目的 = 计提类账务处理结果.投资目的
            bokAssetMarketInfo.setInvAim(dto.getInvAim());
            // 变量.资产行情信息DTO.市场代码 = 变量.标的信息.市场代码
            bokAssetMarketInfo.setMarketCode(targetFinProduct.getFinprodMarketId());
            // 变量.资产行情信息DTO.市价 = 变量.市价
            bokAssetMarketInfo.setMarketPrice(marketPrice);
            // 变量.资产行情信息DTO.是否来源于行情数据
            // if 市价共通返回.市价== null || 变量.市价 != 市价共通返回.市价
            if(marketPriceDto == null || marketPriceDto.getValuation() == null || marketPriceDto.getDatasourceType().equals(DictCodeListRsk.估值来源_行内.getValue())){
                // 变量.资产行情信息DTO.行情日期 = 变量.发生日期
                bokAssetMarketInfo.setValueDate(happenDate);
                bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_系统生成.getValue());
                bokAssetMarketInfo.setSchemeId(schemeDetailDto.getSchemeId());
                // 变量.资产行情信息DTO.估值方案明细代码 = 市价共通返回.估值方案明细代码
                bokAssetMarketInfo.setSchemeDetailId(schemeDetailDto.getSchemeDetailId());
                bokAssetMarketInfo.setValueSource(DictCodeListRsk.估值来源_行内.getValue());
            }
            else{
                // 变量.资产行情信息DTO.估值来源 = 市价共通返回.估值来源
                bokAssetMarketInfo.setValueSource(schemeDetailDto.getValuationSource());
                // 变量.资产行情信息DTO.行情日期 = 市价共通返回.行情日期
                bokAssetMarketInfo.setValueDate(marketPriceDto.getValDate());
                // 变量.资产行情信息DTO.估值方案代码 = 市价共通返回.估值方案代码
                bokAssetMarketInfo.setSchemeId(schemeDetailDto.getSchemeId());
                // 变量.资产行情信息DTO.估值方案明细代码 = 市价共通返回.估值方案明细代码
                bokAssetMarketInfo.setSchemeDetailId(schemeDetailDto.getSchemeDetailId());
                bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_行情信息.getValue());
            }
            // 添加到返回值中
            returnResult.getBokAssetMarketInfoList().add(bokAssetMarketInfo);



        }


        return returnResult;

    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     *
     * @param booksetId         账套代码
     * @param bokDetailId       账务明细代码
     * @param happenDate        发生日
     * @param bookDate          入账日
     * @param expBalanceDirect  期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri           任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalanceEx(String booksetId, String bokDetailId,
                                               Date happenDate, Date bookDate,
                                               String expBalanceDirect, String baseSubjectNature,
                                               Long taskPri) {
        // 定义返回的科目余额
        SubjectBalance balance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0);
        } else {
            balance.setBalance(BigDecimal.ZERO);
            balance.setNum(BigDecimal.ZERO);
        }

        return balance;
    }

    /**
     * 获取前一天时间
     *
     * @param nowDate 传递时间
     * @return Date
     * @see
     * @since 1.0
     */
    private Date getYesterday(Date nowDate) {
        // 非空判断
        if (nowDate == null) {
            return null;
        }

        // 获取科目余额数据
        Calendar calendar = Calendar.getInstance();// 获取日历实例
        calendar.setTime(nowDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
        Date yesterday = calendar.getTime();// 获得前一天

        return yesterday;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     *
     * @param booksetId         账套代码
     * @param bokDetailId       账务明细代码
     * @param happenDate        发生日
     * @param bookDate          入账日
     * @param expBalanceDirect  期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri           任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private BigDecimal getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                         Date bookDate, String expBalanceDirect,
                                         String baseSubjectNature, Long taskPri) {
        // 定义返回的科目余额
        BigDecimal balance = BigDecimal.ZERO;

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0).getBalance();
        }

        return ArithmeticUtil.round(balance, NumberEnum.MONEY);
    }

    /**
     * 科目余额查询--期待方向
     *
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日期
     * @param baseSubjectNature 基础科目性质
     * @param expBalanceDirect 期待方向
     * @return 科目余额实体
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalanceExpectDirection(String booksetId, String bokDetailId,
                                                            Date happenDate,
                                                            String baseSubjectNature,
                                                            String expBalanceDirect,
                                                            Long taskPri,String batchSeqno) {
        List<String> baseSubjectNatureList = new ArrayList<>();
        SubjectBalance subjectBalance = new SubjectBalance();
        baseSubjectNatureList.add(baseSubjectNature);

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        searchDto.setTaskPri(taskPri);
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDto, expBalanceDirect);
        if (!CollectionUtils.isEmpty(subjectBalanceList)) {
            subjectBalance = subjectBalanceList.get(0);
        }
        return subjectBalance;
    }

}

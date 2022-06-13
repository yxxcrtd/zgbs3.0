/*
 * 文件名：DemandDepositCalBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chendijun
 * 修改时间：2018年8月2日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.demanddepositcalbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
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
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.InterestBasisDaysParameter;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashCalcRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 活期存款 账务处理processor 的service
 * 
 * @author chendijun
 * @version 1.0 2018年8月2日
 * @see DemandDepositCalBookNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DemandDepositCalBookNewService {

    /**
     * 记账引擎
     */
    @Autowired
    BookAccountGenLogicNew bookAccountGenLogicNew;

    /**
     * 公用方法services 账务类共通方法
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * processor 的数据处理方法
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param) {
        // 初始化返回值
        BookGenerateResultWithBokSummary bookGenerateResult = new BookGenerateResultWithBokSummary();
        // 账务明细若为空赋初始值
        if (bookGenerateResult.getValBokBookdetailBatch() == null) {
            bookGenerateResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取reader返回值
        DrawBaseInfoSelectComResult readerEntity = (DrawBaseInfoSelectComResult)param.get(
            GlobalConstant.READER_RESULT);

        // 获取从reader中拿到的值
        TreeNode bookSetTreeNodeNow = readerEntity.getBookSetTreeNode();// 会计场景
        List<DrawBaseInfoSelectComDTO> listFinProductDealInfoComDTONow = readerEntity.getListDrawBaseInfoSelectComDTO();// 计提基础信息列表
        // 发生日取批处理发生日
        Date happenDate = (Date)param.get(BookConstant.HAPPEN_DATE);

        // 如果没有明细，就结束
        if (listFinProductDealInfoComDTONow == null || listFinProductDealInfoComDTONow.isEmpty()) {
            return null;
        }

        // 1. 数据准备
        // 1.1 定义变量
        BigDecimal cashBalance = BigDecimal.ZERO;// 变量.现金科目余额
        BigDecimal cashBalanceBccy = BigDecimal.ZERO;// 变量.现金科目余额_本位币

        // 1.2 汇总信息查询
        // 变量.昨日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
//        BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
//            DateUtil.addDay(readerEntity.getHappenDate(), -1),
//            DateUtil.addDay(readerEntity.getHappenDate(), -1), readerEntity.getBookDate(),
//            readerEntity.getBooksetId(), readerEntity.getBokDetailId(), null, null);

        // 1.2活期存款是否计提查询
        // 账务类共通方法:账套参数查询
        String isCal = null;
        List<BokBooksetExt> listValBokBooksetExt = accountingCommonService.getValBokBooksetExt(
            readerEntity.getBooksetId(),
            new ArrayList<String>(Arrays.asList(ValDictCodeList.账套配置表参数项_活期存款是否计提.getValue())));// 疑问：未找到对应的字典值

        if (listValBokBooksetExt == null || listValBokBooksetExt.isEmpty()) {
            return null;
        }
        // if 返回值=“N” return
        isCal = listValBokBooksetExt.get(0).getParamValue();
        if (DictCodeList.是否_否.getValue().equals(isCal)) {
            return null;
        }

        // 1.3 活期存款利率查询

        // 利率
        BigDecimal yield = BigDecimal.ZERO;
        // 计息基础
        String basis = null;
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        // 处理交易
        for (DrawBaseInfoSelectComDTO drawBaseInfoSelectComDTO : listFinProductDealInfoComDTONow) {

            // modify by weipeng on 20220303 ：会计引擎升级，资金账户科目性质拆分 start
            // 本方资金账户类型 或 对方资金账户类型对应资金账户类型，赋值对应金额元素
            // 定义 变量.资金账户类型
            String acctType=drawBaseInfoSelectComDTO.getBusiDetailType();
            // 定义 变量.本金基础科目性质
            String subjectNature_BJ = DictCodeBaseSubjectNatrueNew.活期存款_本金.getValue();
            // 定义 变量.应计利息基础科目性质
            String subjectNature_YJLX = DictCodeBaseSubjectNatrueNew.活期存款_应计利息.getValue();
            // 定义 变量.记账金额元素 = 默认.托管户
            String 记账金额元素 = BookConstant.INTAMT_HQ;
            // 券商资金账户现划分在结算备付金科目性质下
            if(DictCodeListExt1.资金账户类型_结算备付金账户.getValue().equals(acctType)
                    || DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(acctType)){
                记账金额元素 = BookConstant.INTAMT_JS;
                subjectNature_BJ = DictCodeBaseSubjectNatrueNew.结算备付金_本金.getValue();
                subjectNature_YJLX = DictCodeBaseSubjectNatrueNew.结算备付金_应计利息.getValue();
            }
            // 券商资金账户现划分在结算备付金科目性质下
            if(DictCodeListExt1.资金账户类型_交易保证金账户.getValue().equals(acctType)){
                记账金额元素 = BookConstant.INTAMT_CC;
                subjectNature_BJ = DictCodeBaseSubjectNatrueNew.存出保证金_本金.getValue();
                subjectNature_YJLX = DictCodeBaseSubjectNatrueNew.存出保证金_应计利息.getValue();
            }
            // 1.4 现金科目余额查询(基础科目性质:现金)
            SubjectBalance subjectBalance = this.getSubjectBalance(readerEntity,
                    DictCodeList.余额方向_借方.getValue(), subjectNature_BJ, taskPri, drawBaseInfoSelectComDTO.getBokDetailId());
            cashBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();

            // 变量.现金类科目余额_本位币 = 返回值.科目余额.本位币余额
            cashBalanceBccy = subjectBalance.getbAmt() == null ? BigDecimal.ZERO : subjectBalance.getbAmt();


            // 成本数量
//        @SuppressWarnings("unused")
//        BigDecimal cashNum = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getNum();

            // 查询 前一日科目余额：应计利息类科目余额
            SubjectBalance subjectBalanceYst = this.getSubjectBalance(readerEntity,
                    DictCodeList.余额方向_借方.getValue(), subjectNature_YJLX, taskPri, drawBaseInfoSelectComDTO.getBokDetailId());
//        cashBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            // 定义 变量.应计利息前一日余额 = 科目余额
            BigDecimal ystInterestBalance = subjectBalanceYst.getBalance();
            // 变量.应计利息前一日余额_本位币 = 科目余额.本位币余额
            BigDecimal ystInterestBalanceB = subjectBalanceYst.getbAmt();
            // modify by weipeng on 20220303 ：会计引擎升级，资金账户科目性质拆分 end
            // // 获取标的交易信息 交易DTO
            // TradeInfoDto tradeInfoDto = this.getTargetInfo(drawBaseInfoSelectComDTO.getBusiId(),
            // drawBaseInfoSelectComDTO.getBusiDetailType(),
            // drawBaseInfoSelectComDTO.getBranch());

            // 初始化查询类型
            List<TargetSearchDto> queryCriteria = new ArrayList<>();

            // 查询 标的信息
            TargetSearchDto targetSearchDto = new TargetSearchDto();
            // 数据代码 = RST_CASH_CALCRULE
            targetSearchDto.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());// 现金流类型
            // 现金流类型：利息
            //targetSearchDto.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());// 利息
            // 针对估值一体化：计提式批处理，获取现金流计算规则，不支持投管【计提式费用】，故只取利息。
            targetSearchDto.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
            // 生效日期
            targetSearchDto.setEffdateEnd(happenDate);
            // 调用标的信息共通方法
            queryCriteria.add(targetSearchDto);

            Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(
                drawBaseInfoSelectComDTO.getBusiId(), queryCriteria);

            // 报价列表
            List<TargetCashCalcRule> targetCashCalcRuleList = targetMap.get(
                targetSearchDto) == null ? null : (List<TargetCashCalcRule>)targetMap.get(
                    targetSearchDto);
            
            if (targetCashCalcRuleList != null && targetCashCalcRuleList.size() > 0) {
                // 按有效日期降序排序
                Collections.sort(targetCashCalcRuleList, new Comparator<TargetCashCalcRule>() {

                    @Override
                    public int compare(TargetCashCalcRule o1, TargetCashCalcRule o2) {
                        return DateUtil.compareDate(o2.getEffDate(), o1.getEffDate());
                    }
                });
                // 取最大有效日期
                TargetCashCalcRule calcRule = targetCashCalcRuleList.get(0);
                yield = calcRule.getYield();
                basis = calcRule.getBasis();
            }
            // if (targetCashCalcRuleList != null && !targetCashCalcRuleList.isEmpty()) {
            // TargetCashCalcRule calcRule = this.getTargetCashCalcRuleByMaxEffDate(
            // targetCashCalcRuleList);
            // yield = calcRule.getYield();
            // basis = calcRule.getBasis();
            // }

            // 2.应收利息计算
            // 获取计息基础天数
            BigDecimal basisDay = new BigDecimal(calculationCommonService.calcInterestBasisDays(
                new InterestBasisDaysParameter(basis)));
            // 变量.应收利息 = 变量.现金类科目余额*变量.利率/变量.计息基础
            BigDecimal interest = ArithmeticUtil.round(
                ArithmeticUtil.divide(ArithmeticUtil.multiply(cashBalance, yield), basisDay),
                NumberEnum.MONEY);
            // 变量.应收利息_本位币 = 变量.应收利息 * 当日最新汇率
            //查询当日最新汇率
            List<CurrencyMarketDto> list = finProductCommonService.getCurrencyMarket(readerEntity.getBookDate(), readerEntity.getBooksetId(), drawBaseInfoSelectComDTO.getStandardCcy(),drawBaseInfoSelectComDTO.getoCcy());
            BigDecimal interest_bccy = interest;
            if(list!=null && !list.isEmpty()&&list.get(0)!=null){
            	interest_bccy = ArithmeticUtil.round(
            			ArithmeticUtil.multiply(list.get(0).getClosePrice(), interest),
                		NumberEnum.MONEY);
            }

            // 组装记账元素
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();

            // 共通元素设值 - 设置账务引擎参数
            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
            ad.setTableId(DictCodeListTableId.会计场景代码_活期存款利息_计提.getValue());// ad.会计场景代码 = 活期存款计提;

            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // accountSceneMap设置如下：
            // 调用共通场景元素赋值-计提类场景元素
            accountSceneMap = bookCommonLogic.accrualElementAssign(readerEntity,
                drawBaseInfoSelectComDTO);
            // 备注<-活期存款每日计提
            // 分类1<-变量.交易信息.分类1
            // 分类2<-变量.交易信息.分类2
            accountSceneMap.put(BookConstant.BOOK_REMARK, "活期存款每日计提");
            // accountSceneMap.put(BookConstant.TYPE_1, tradeInfoDto.getType1());
            // accountSceneMap.put(BookConstant.TYPE_2, tradeInfoDto.getType2());
            //资金账户类型
            accountSceneMap.put(BookConstant.ACCT_TYPE, drawBaseInfoSelectComDTO.getBusiDetailType());
            // accountAmountMap设置如下：
            // add by weipeng on 20220221 区分资金账户金额元素 start
//            // 应收利息 <-变量.应计利息
//            accountAmountMap.put(BookConstant.INTAMT, interest);
//            //应收利息_本位币
//            accountAmountMap.put(BookConstant.INTAMT+BookConstant.BCCYAMT, interest_bccy);

            // 应收利息 <-变量.应计利息
            accountAmountMap.put(记账金额元素, interest);
            //应收利息_本位币
            accountAmountMap.put(记账金额元素+BookConstant.BCCYAMT, interest_bccy);
            accountCurrencyMap.put(记账金额元素, drawBaseInfoSelectComDTO.getoCcy());
            // add by weipeng on 20220221 区分资金账户金额元素 end

            // accountCurrencyMap设置如下：
//            // 应收利息 <-计提明细.交易币种
//            accountCurrencyMap.put(BookConstant.INTAMT, drawBaseInfoSelectComDTO.getoCcy());

            
            ad.setAccountCurrencyMap(accountCurrencyMap);
            ad.setAccountAmountMap(accountAmountMap);
            ad.setAccountSceneMap(accountSceneMap);
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
            ad.setSubjectMappingConfigDefault(drawBaseInfoSelectComDTO.getSubjectMappingConfig());
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookAccountGenLogicNew.genAccount(ad,
                new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            bookGenerateResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());

            // 3.3 统计分析账务明细汇总信息赋值
            // 定义：变量.计提利率
//            BigDecimal jTrate = BigDecimal.ZERO;
//            if (targetCashCalcRuleList != null) {
//                for (TargetCashCalcRule rule : targetCashCalcRuleList) {
//                    // 报价.生效日 > 计提类账务处理结果.发生日
//                    if (rule.getEffDate().compareTo(readerEntity.getHappenDate()) > 0) {
//                        break;
//                    }
//
//                    // 变量.计提利率 = 报价temp.生效利率
//                    jTrate = rule.getYield();
//                }
//            }
            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
            bokSummaryTableNew.setPosAmt(drawBaseInfoSelectComDTO.getNumAmtHappenDate());
            // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
            bokSummaryTableNew.setIntRate(yield);
            // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =NULL
            bokSummaryTableNew.setDailydscYield(null);
            // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
            bokSummaryTableNew.setTdyIntincexpAdd(interest);
            // // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
            // BigDecimal tdyintincexpNow = BigDecimal.ZERO;
            // if (bokSummaryTableYestdy != null) {
            // tdyintincexpNow = ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexp(),
            // interest);
            // }
            // else {
            // tdyintincexpNow = interest;
            // }
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应计利息前一日余额+ 变量.应计利息 
            BigDecimal tdyintincexpNow = ArithmeticUtil.doAdd(ystInterestBalance, interest);
            bokSummaryTableNew.setTdyIntincexp(tdyintincexpNow);
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.数量余额
            bokSummaryTableNew.setTdyPosition(drawBaseInfoSelectComDTO.getNumAmtHappenDate());
            // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
            bokSummaryTableNew.setInvAim(readerEntity.getInvAim());
            // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
            bokSummaryTableNew.setTdyCostAmt(drawBaseInfoSelectComDTO.getoAmtHappenDate());
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = “资金账户”
            bokSummaryTableNew.setFinprodType(DictCodeListExt1.金融产品类型_资金账户.getValue());
            // 账套代码
            bokSummaryTableNew.setBooksetId(readerEntity.getBooksetId());
            // 发生日期
            bokSummaryTableNew.setHappenDate(readerEntity.getHappenDate());
            // 入账日期
            bokSummaryTableNew.setBookDate(readerEntity.getBookDate());
            // 账务明细代码
            bokSummaryTableNew.setBokDetailId(readerEntity.getBokDetailId());
            // 账套日期
            bokSummaryTableNew.setBooksetDate(drawBaseInfoSelectComDTO.getBooksetDate());
            // 处理序列
            bokSummaryTableNew.setBookSummaryOrder(
                new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
            // 变量.当日统计分析账务明细汇总信息.起息日 = null
            bokSummaryTableNew.setVdate(null);
            // 变量.当日统计分析账务明细汇总信息.到期日 = null
            bokSummaryTableNew.setMdate(null);
            // 变量.当日统计分析账务明细汇总信息.剩余期限 = 0
            bokSummaryTableNew.setEndDays1(BigDecimal.ZERO);
            // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 0
            bokSummaryTableNew.setEndDays2(BigDecimal.ZERO);
            // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
            bokSummaryTableNew.setBusiId(readerEntity.getBusiId());

            // 变量.当日统计分析账务明细汇总信息.币种= 计提类账务处理结果.原币币种
            bokSummaryTableNew.setCcy(drawBaseInfoSelectComDTO.getoCcy());


            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== beign
//            //本位币
//            bokSummaryTableNew.setbCcy(bokSummaryTableNew.getCcy());
//            //当日发生应计利息_本位币
//            bokSummaryTableNew.setTdyIntincexpAddB(bokSummaryTableNew.getTdyIntincexpAdd());
//            // 当日成本_本位币
//            bokSummaryTableNew.setTdyCostAmtB(bokSummaryTableNew.getTdyCostAmt());
//            // 当日公允价值变动_本位币
//            bokSummaryTableNew.setTdyFloatIngplB(bokSummaryTableNew.getTdyFloatIngpl());
//            // 当日累计公允价值变动_本位币
//            bokSummaryTableNew.setTdyFloatIngplExpB(bokSummaryTableNew.getTdyFloatIngplExp());
//            // 当日应计利息_本位币
//            bokSummaryTableNew.setTdyIntincexpB(bokSummaryTableNew.getTdyIntincexp());
            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== end


            // 变量.当日统计分析账务明细汇总信息.当日发生应计利息_本位币 = 变量.应收利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(interest_bccy);
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额_本位币 =变量.应计利息前一日余额_本位币+ 变量.应计利息_本位币
            bokSummaryTableNew.setTdyFloatIngplExpB(ArithmeticUtil.doAdd(ystInterestBalanceB,interest_bccy));
            // 变量.当日统计分析账务明细汇总信息.当日成本_本位币= 变量.现金类科目余额_本位币
            bokSummaryTableNew.setTdyCostAmtB(cashBalanceBccy);
            // 变量.当日统计分析账务明细汇总信息.本位币= 计提类账务处理结果.本位币币种
            bokSummaryTableNew.setbCcy(drawBaseInfoSelectComDTO.getStandardCcy());

            // 当日公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplB(bokSummaryTableNew.getTdyFloatIngpl());
            // 当日应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpB(bokSummaryTableNew.getTdyIntincexp());

            // 添加到返回值中
            bookGenerateResult.getBokSummaryList().add(bokSummaryTableNew);

        }
        return bookGenerateResult;
    }

    /**
     * 调用共通查询发生日前一日余额 调用共通查询：科目余额查询-期待方向，查询现金类科目余额
     * 
     * @param readerEntity reader传递的参数
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri 任务优先级
     * @return
     * @see
     * @since 1.0
     */

    private SubjectBalance getSubjectBalance(DrawBaseInfoSelectComResult readerEntity,
                                             String expBalanceDirect, String baseSubjectNature,
                                             Long taskPri, String bokDetailId) {
        // 初始化返回结果
        SubjectBalance balance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(readerEntity.getBooksetId());
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(readerEntity.getHappenDate());
        searchDto.setBookDate(readerEntity.getBookDate());
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0);
        }

        return balance;
    }

    /**
     * 返回生效日期最大的记录
     * 
     * @param targetCashCalcRuleList 标的现金流计算规则 记录列表
     * @return TargetCashCalcRule 生效日期最大的记录
     * @see
     * @since 1.0
     */

    @SuppressWarnings("unused")
    private TargetCashCalcRule getTargetCashCalcRuleByMaxEffDate(List<TargetCashCalcRule> targetCashCalcRuleList) {
        for (int i = 0; i < targetCashCalcRuleList.size(); i++ ) {
            for (int j = 1; j < targetCashCalcRuleList.size() - i; j++ ) {
                if (DateUtil.compareDate(targetCashCalcRuleList.get(j - 1).getEffDate(),
                    targetCashCalcRuleList.get(j).getEffDate()) < 0) {
                    TargetCashCalcRule cashCalcRuleTemp,
                        cashCalcRule1 = targetCashCalcRuleList.get(j - 1),
                        cashCalcRule2 = targetCashCalcRuleList.get(j);

                    cashCalcRuleTemp = cashCalcRule1;
                    cashCalcRule1 = cashCalcRule2;
                    cashCalcRule2 = cashCalcRuleTemp;

                    targetCashCalcRuleList.set(j - 1, cashCalcRule1);
                    targetCashCalcRuleList.set(j, cashCalcRule2);
                }
            }
        }

        return targetCashCalcRuleList.get(0);
    }

    // /**
    // * 调用共通方法：[标的类共通方法-查询交易信息]，获取标的交易信息
    // *
    // * @param finprodId 金融产品代码【必输项】
    // * @param finprodType 金融产品类型【必输项】
    // * @param branch 分支序号
    // * @return ValFinProductDeal 标的查询
    // * @see
    // * @since 1.0
    // */
    // private TradeInfoDto getTargetInfo(String finprodId, String finprodType, Long branch) {
    // // 定义返回值 - 标的交易信息
    // TradeInfoDto tradeInfoDtoForReturn = null;
    //
    // // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
    // List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(
    // finprodId, finprodType, branch);
    //
    // // 当查询结果不为空
    // if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
    // // 默认取第一条标的信息
    // tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
    // }
    //
    // return tradeInfoDtoForReturn;
    // }
}

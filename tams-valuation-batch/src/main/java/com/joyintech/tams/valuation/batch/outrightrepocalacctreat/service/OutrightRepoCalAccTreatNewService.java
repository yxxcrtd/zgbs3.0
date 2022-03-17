/*
 * 文件名：OutrightRepoCalAccTreatProcessService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chufengjie
 * 修改时间：2018年7月27日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.outrightrepocalacctreat.service;

import com.google.common.collect.Lists;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.dao.AccountingCommonMapper;
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
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 买断式回购计提账务处理 process相关业务处理
 * 
 * @author chufengjie
 * @version 1.0 2018年7月27日
 * @see OutrightRepoCalAccTreatNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class OutrightRepoCalAccTreatNewService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 计算类共通方法
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 凭证最新账务查询
     */
    @Autowired
    private AccountingCommonMapper accountingCommonMapper;

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * process 数据处理
     * 
     * @param param 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param) {
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数或计提基础信息列表为空，不进行后续处理
        if (acctEntity == null || acctEntity.getListDrawBaseInfoSelectComDTO() == null
            || acctEntity.getListDrawBaseInfoSelectComDTO().isEmpty()) {
            return null;
        }

        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        // 定义变量
        BigDecimal glb_subjectAmt = listDrawBaseInfoSelectComDTONow.get(0).getoAmtHappenDate();// 变量.成本科目余额=计提类账务处理结果.科目余额
        BigDecimal glb_subjectInt = BigDecimal.ZERO;// 变量.应计利息
        BigDecimal glb_subjectProfit = BigDecimal.ZERO;// 变量.应计利息科目余额
        BigDecimal glb_subjectNum = listDrawBaseInfoSelectComDTONow.get(0).getNumAmtHappenDate();// 变量.数量余额
        BigDecimal glb_depcharge = BigDecimal.ZERO;// 变量.计提费用
        

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期


        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();//任务优先级（计提基础信息列表的任务优先级都是一致的，故取第一个）

        // 通过业务明细代码，获取标的交易信息 变量.交易标的DTO = 上述共通返回报价列表
        TradeInfoDto dealInfoForTarget = this.getTargetInfo(listDrawBaseInfoSelectComDTONow.get(0).getBusiId(),
            listDrawBaseInfoSelectComDTONow.get(0).getBusiDetailType(), listDrawBaseInfoSelectComDTONow.get(0).getBranch());
        // 保证获取的标的交易信息不为空
        if (dealInfoForTarget == null) {
            return null;
        }
        String expBalanceDirect="";
        //塞入 基础科目性质
        List<String> baseSubjectNatureList =Lists.newArrayList();

        if (dealInfoForTarget.getOFinprodType().equals( DictCodeListExt1.金融产品类型_债券买断式正回购.getValue())) {
            expBalanceDirect=DictCodeList.余额方向_贷方.getValue();
            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.卖出回购金融资产_应计利息.getValue());
        }else{
            expBalanceDirect=DictCodeList.余额方向_借方.getValue();
            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.买入返售金融资产_应计利息.getValue());
        }
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setHappenDate(happenDateNow);// 发生日
        searchDto.setBookDate(bookDateNow);// 入账日
        searchDto.setBokDetailId(acctEntity.getBokDetailId());// 账务明细代码
        searchDto.setBooksetId(acctEntity.getBooksetId());// 账套代码
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);// 基础科目性质
        searchDto.setTaskPri(taskPri);// 任务优先级

        //调用共通方法 - 科目余额查询
        List<SubjectBalance> subjectLatestBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(searchDto, expBalanceDirect);
        SubjectBalance subjectBalance = new SubjectBalance(); // 返回的实体类
        if (CollectionUtils.isNotEmpty(subjectLatestBalanceList)) {
            subjectBalance = subjectLatestBalanceList.get(0);
        }
        //变量.应计利息科目余额=科目余额
        glb_subjectProfit=subjectBalance.getBalance()==null?BigDecimal.ZERO:subjectBalance.getBalance();
        // 1.2 汇总信息查询
        // 变量.昨日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
//        BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
//            DateUtil.addDay(acctEntity.getHappenDate(), -1),
//            DateUtil.addDay(acctEntity.getHappenDate(), -1), acctEntity.getBookDate(),
//            acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);
        // 账套核算参数查询
        List<String> paramCodeList = new ArrayList<String>();
        // DEPO_ACCR_TYPE 账套配置表参数项_存款计提方式
        paramCodeList.add(ValDictCodeList.账套配置表参数项_回购计提方式_买断式.getValue());
        // 调用账务类共通（DTO.账套代码, 参数项：如下）
        List<BokBooksetExt> listBooksetExt = accountingCommonService.getValBokBooksetExt(
            booksetIdNow, paramCodeList);
        // 变量.计提方式 = 账套配置表参数项_拆借计提方式 对应的计提方式
        String calType = "";
        if (!CollectionUtils.isEmpty(listBooksetExt)) {
            // 获取第一条即可
            calType = listBooksetExt.get(0).getParamValue();
        }

        // 循环 计提基础信息列表 进行逻辑处理
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow) {


            if (happenDateNow.compareTo(dealInfoForTarget.getMdate()) < 0) {
                // 2.2.1 派息交易查询
                // 调用账务共通设计->【凭证最新账务查询】
                // 塞入要查询的参数-基础科目性质
                baseSubjectNatureList = new ArrayList<String>();
                if(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(dto.getBusiDetailType())){
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.卖出回购金融资产_成本.getValue());
                }
                else{
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.买入返售金融资产_成本.getValue());
                }
                // 塞入要查询的参数-场景代码
                String scenecode = "";
                if (dealInfoForTarget.getOFinprodType().equals(
                    DictCodeListExt1.金融产品类型_债券买断式正回购.getValue())) {
                    scenecode = DictCodeListTableId.会计场景代码_买断式正回购_期间债券还本派息.getValue();
                }
                else {
                    scenecode = DictCodeListTableId.会计场景代码_买断式逆回购_期间债券还本派息.getValue();
                }
                List<BookLatestInfoDto> listInfo = this.getBookLatestInfo(booksetIdNow,
                    bookDateNow, bokDetailIdNow, baseSubjectNatureList, scenecode, null,
                        happenDateNow, null);
                // 2.2.2 计提成本处理
                List<PrincipalInfo> prinList = new ArrayList<PrincipalInfo>();// 变量.本金列表
                // 变量.折算后初始本金
                BigDecimal convertedprincipal = glb_subjectNum;
                for (BookLatestInfoDto dto1 : listInfo) {
                    PrincipalInfo principalInfo = new PrincipalInfo();// 变量.本金变动
                    // 变量.本金变动.本金金额 = 变量.账务明细.发生额 * 变量.账务明细.数量余额（发生日）/变量.数量余额 * -1
                    BigDecimal prinamount = ArithmeticUtil.divide(
                            ArithmeticUtil.multiply(dto1.getoHappenAmt(), dto1.getNumAmtHappenDate(),
                                    new BigDecimal(-1)),
                            glb_subjectNum);
                    // 调用金额共通方法设置有效小数位
                    prinamount = ArithmeticUtil.round(prinamount, NumberEnum.MONEY);
                    // 塞入本金金额
                    principalInfo.setPrinAmount(prinamount);
                    // 变量.本金变动.变动日 = 变量.账务明细.发生日
                    Date changedate = dto1.getHappenDate();
                    // 塞入变动日
                    principalInfo.setChangeDate(changedate);
                    // 变量.本金列表.ADD(变量.本金变动)
                    prinList.add(principalInfo);
                    // 变量.折算后初始本金 += 变量.本金变动.本金金额
                    convertedprincipal = convertedprincipal.add(prinamount);
                }
                // 变量.本金变动
                PrincipalInfo principalInfo1 = new PrincipalInfo();

                // 调用共通设计->【详细设计-计算类共通方法设计.xlsx】->通用计算SHEET
                // 调用共通：通用计算算法（根据【算头不算尾】的原则，所以到期日加一天
                List<QuoteInfo> quoteList = new ArrayList<QuoteInfo>();
                QuoteInfo quoteInfo = new QuoteInfo();
                CalCommonResult calCommonResult=new CalCommonResult();
                if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {
                    // 变量.本金变动.本金金额 = 变量.折算后初始本金
                    principalInfo1.setPrinAmount(convertedprincipal);
                    // 变量.本金变动.变动日 = 交易标的DTO.起息日
                    principalInfo1.setChangeDate(dealInfoForTarget.getVdate());
                    // 变量.本金列表.ADD(变量.本金变动)
                    prinList.add(principalInfo1);
                    // 变量.本金列表 按照变动日 升序重新排序
                    ListSort(prinList);
                    
                    quoteInfo.setIssueDate(dealInfoForTarget.getVdate());
                    quoteInfo.setInterest(dealInfoForTarget.getIntRate());
                    quoteInfo.setAccountBasis(dealInfoForTarget.getBasis());
                    quoteList.add(quoteInfo);
                    
                    calCommonResult = calculationCommonService.calCommon(
                        dealInfoForTarget.getVdate(), DateUtil.addDay(happenDateNow, 1), prinList,
                        quoteList);
                    // 参数准备
                    BigDecimal day1 = new BigDecimal(DateUtil.getBetweenDayNumber(dealInfoForTarget.getVdate(), DateUtil.addDay(happenDateNow, 1)));
                    
                    BigDecimal day2 = new BigDecimal(DateUtil.getBetweenDayNumber(dealInfoForTarget.getVdate(), dealInfoForTarget.getMdate()));
                    if (dealInfoForTarget.getOFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue())) {
                        // 变量.计提费用 = （变量.成本科目余额 - 变量.数量余额）*（DTO.发生日 - 交易标的DTO.起息日）/（交易标的DTO.到期日 -
                        // 交易标的DTO.起息日）
                        glb_depcharge = ArithmeticUtil.divide(glb_subjectAmt.subtract(glb_subjectNum).multiply(day1), day2);

                        // 变量.应计利息 = 返回值.总利息金额 - 变量.计提费用 - 变量.应计利息余额
                        glb_subjectInt = calCommonResult.getTotalInterestAmt().subtract(
                            glb_depcharge).subtract(glb_subjectProfit);
                        // 调用金额共通方法设置有效小数位
                        glb_subjectInt = ArithmeticUtil.round(glb_subjectInt, NumberEnum.MONEY);
                    }else {
                        // 变量.计提费用 = （变量.数量余额 - 变量.成本科目余额）*（DTO.发生日 - 交易标的DTO.起息日）/（交易标的DTO.到期日 -
                        // 交易标的DTO.起息日）
                        glb_depcharge = ArithmeticUtil.divide(glb_subjectNum.subtract(glb_subjectAmt).multiply(day1), day2);
                        // 变量.应计利息 = 返回值.总利息金额 + 变量.计提费用 - 变量.应计利息余额
                        glb_subjectInt = calCommonResult.getTotalInterestAmt().add(glb_depcharge).subtract(glb_subjectProfit);
                        // 调用金额共通方法设置有效小数位
                        glb_subjectInt = ArithmeticUtil.round(glb_subjectInt, NumberEnum.MONEY);
                    }
                }else{
                    principalInfo1 = new PrincipalInfo();
                    // 变量.本金变动.本金金额 = 变量.数量余额
                    principalInfo1.setPrinAmount(glb_subjectNum);
                    // 变量.本金变动.变动日 = 参数发生日
                    principalInfo1.setChangeDate(happenDateNow);
                    // 变量.本金列表.ADD(变量.本金变动)
                    prinList.add(principalInfo1);
                    // 变量.本金列表 按照变动日 升序重新排序
                    ListSort(prinList);
                    

                    quoteInfo.setIssueDate(happenDateNow);
                    quoteInfo.setInterest(dealInfoForTarget.getIntRate());
                    quoteInfo.setAccountBasis(dealInfoForTarget.getBasis());
                    quoteList.add(quoteInfo);
                    
                    calCommonResult = calculationCommonService.calCommon(
                        dealInfoForTarget.getVdate(), DateUtil.addDay(happenDateNow, 1), prinList,
                        quoteList);
                    // 参数准备
                    BigDecimal day1 = new BigDecimal(DateUtil.getBetweenDayNumber(dealInfoForTarget.getVdate(), dealInfoForTarget.getMdate()));
                    if (dealInfoForTarget.getOFinprodType().equals(
                        DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue())) {
                        // 变量.计提费用 = （变量.成本科目余额 - 变量.数量余额）/（交易标的DTO.到期日 -交易标的DTO.起息日）
                        glb_depcharge = ArithmeticUtil.divide( glb_subjectAmt.subtract(glb_subjectNum),day1);

                        // 变量.应计利息 = 返回值.总利息金额 - 变量.计提费用 - 变量.应计利息余额
                        glb_subjectInt = calCommonResult.getTotalInterestAmt().subtract(glb_depcharge);
                        // 调用金额共通方法设置有效小数位
                        glb_subjectInt = ArithmeticUtil.round(glb_subjectInt, NumberEnum.MONEY);
                    }else {
                        // 变量.计提费用 = （变量.数量余额 - 变量.成本科目余额）*（DTO.发生日 - 交易标的DTO.起息日）/（交易标的DTO.到期日 -
                        // 交易标的DTO.起息日）
                        glb_depcharge = ArithmeticUtil.divide(glb_subjectNum.subtract(glb_subjectAmt), day1);
                        // 变量.应计利息 = 返回值.总利息金额 + 变量.计提费用 - 变量.应计利息余额
                        glb_subjectInt = calCommonResult.getTotalInterestAmt().add(glb_depcharge);
                        // 调用金额共通方法设置有效小数位
                        glb_subjectInt = ArithmeticUtil.round(glb_subjectInt, NumberEnum.MONEY);
                    }
                }
                
            }
            else {
                glb_subjectInt = BigDecimal.ZERO;// 变量.应计利息 = 0
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

            // 设置账务引擎参数
            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景

            // 记账场景要素map塞值,(调用共通方法-计提类场景元素赋值)
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, dto);
            String finprodTypeName = DictCodeListExt1.from(DictCode.金融产品类型,
                    dto.getBusiDetailType()).getCodeName();
            accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + "每日计提");// 金融产品类型+"每日计提"
            accountSceneMap.put(BookConstant.TYPE_1, dealInfoForTarget.getType1());// 分类1 -
                                                                                   // 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_2, dealInfoForTarget.getType2());// 分类2 -
                                                                                   // 变量.交易信息.分类2

            /**modify by yanya on 20201215 回购计提增加元素，利息计提分支：交易市场**/
            accountSceneMap.put(BookConstant.TRADE_MARKET, dealInfoForTarget.getTradeMarket());

            // 交易标的DTO.金融产品类型 = “债券买断式正回购”
            if (DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(dto.getBusiDetailType())) {// 分录场景为【债券买断式正回购】
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_买断式正回购_利息计提.getValue());// ad.会计场景代码="买断式正逆回购-计提"

            }
            else {// 会计场景代码="买断式逆回购-计提"
                  // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_买断式逆回购_利息计提.getValue());// ad.会计场景代码="买断式逆回购-计提"
            }
            // 应计利息 <-变量.应计利息
            accountAmountMap.put(BookConstant.INTAMT, glb_subjectInt);

            // 应计利息 <- 计提明细.交易币种
            accountCurrencyMap.put(BookConstant.INTAMT, dto.getoCcy());
            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            /*** modify by weipeng on 20220317 传入辅助核算项 start **/
            ad.setSubjectMappingConfigDefault(dto.getSubjectMappingConfig());
            /*** modify by weipeng on 20220317 传入辅助核算项 end **/

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());

            // 3.3 统计分析账务明细汇总信息赋值
            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
            bokSummaryTableNew.setPosAmt(glb_subjectNum);
            // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
            bokSummaryTableNew.setIntRate(dealInfoForTarget.getIntRate());
            // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =NULL
            bokSummaryTableNew.setDailydscYield(null);
            // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
            bokSummaryTableNew.setTdyIntincexpAdd(glb_subjectInt);
            // //变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
            // BigDecimal tdyintincexpNow = BigDecimal.ZERO;
            // if(bokSummaryTableYestdy != null){
            // tdyintincexpNow = ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexp(),
            // glb_subjectInt);
            // }else{
            // tdyintincexpNow = glb_subjectInt;
            // }
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应计利息科目余额 + 变量.应计利息
            bokSummaryTableNew.setTdyIntincexp(
                ArithmeticUtil.doAdd(glb_subjectProfit, glb_subjectInt));
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.数量余额
            bokSummaryTableNew.setTdyPosition(glb_subjectNum);
            // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
            bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
            // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
            bokSummaryTableNew.setTdyCostAmt(glb_subjectAmt);
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
            bokSummaryTableNew.setFinprodType(dto.getBusiDetailType());
            // 账套代码
            bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
            // 发生日期
            bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
            // 入账日期
            bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
            // 账务明细代码
            bokSummaryTableNew.setBokDetailId(bokDetailIdNow);
            // 账套日期
            bokSummaryTableNew.setBooksetDate(dto.getBooksetDate());
            // 处理序列
            bokSummaryTableNew.setBookSummaryOrder(
                new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
            // 起息日
            bokSummaryTableNew.setVdate(dealInfoForTarget.getVdate());
            // 到期日
            bokSummaryTableNew.setMdate(dealInfoForTarget.getMdate());
            // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
            bokSummaryTableNew.setBusiId(acctEntity.getBusiId());
            bokSummaryTableNew.setCcy(dealInfoForTarget.getCcy());


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
        }

        return returnResult;
    }

    /**
     * 调用共通方法：[标的类共通方法-查询交易信息]，获取标的交易信息
     * 
     * @param finprodId 金融产品代码【必输项】
     * @param finprodType 金融产品类型【必输项】
     * @param branch 分支序号
     * @return ValFinProductDeal 标的查询
     * @see
     * @since 1.0
     */
    private TradeInfoDto getTargetInfo(String finprodId, String finprodType, Long branch) {
        // 定义返回值 - 标的交易信息
        TradeInfoDto tradeInfoDtoForReturn = null;

        // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
        List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(
            finprodId, finprodType, branch);

        // 当查询结果不为空
        if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
            // 默认取第一条标的信息
            tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
        }

        return tradeInfoDtoForReturn;
    }

    /**
     * 凭证最新账务查询
     * 
     * @param booksetId 账套代码
     * @param bookDate 入账日
     * @param bokDetailId 账务明细代码
     * @param baseSubjectNatureList 基础科目性质
     * @param tableId 场景代码
     * @param tradeId 交易编号
     * @param happenDate 发生日
     * @param onwayDate 在途日期
     * @return BookLatestInfoDto
     * @see
     * @since 1.0
     */
    public List<BookLatestInfoDto> getBookLatestInfo(String booksetId, Date bookDate,
                                                     String bokDetailId,
                                                     List<String> baseSubjectNatureList,
                                                     String tableId, String tradeId,
                                                     Date happenDate, Date onwayDate) {

        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<String, Object>();
        paMap.put("booksetId", booksetId);
        paMap.put("bookDate", bookDate);
        paMap.put("bokDetailId", bokDetailId);
        paMap.put("baseSubjectNatureList", baseSubjectNatureList);
        paMap.put("tableId", tableId);
        paMap.put("tradeId", tradeId);
        paMap.put("happenDate", happenDate);
        paMap.put("onwayDate", onwayDate);
        paMap.put("offsetFlag", DictCodeList.冲正标识_正常交易.getValue());

        return accountingCommonMapper.getBookLatestInfo(paMap);
    }

    /**
     * 变量.本金列表 按照变动日 升序重新排
     * 
     * @param prinList 本金列表
     * @return prinList 本金列表
     * @see
     * @since 1.0
     */
    private void ListSort(List<PrincipalInfo> prinList) {
        Collections.sort(prinList, new Comparator<PrincipalInfo>() {

            @Override
            public int compare(PrincipalInfo o1, PrincipalInfo o2) {
                try {
                    Date dt1 = o1.getChangeDate();
                    Date dt2 = o2.getChangeDate();
                    if (dt1.getTime() > dt2.getTime()) {
                        return 1;
                    }
                    else if (dt1.getTime() == dt2.getTime()) {
                        return 0;
                    }
                    else {
                        return -1;
                    }
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                return 0;
            }

        });
    }

}

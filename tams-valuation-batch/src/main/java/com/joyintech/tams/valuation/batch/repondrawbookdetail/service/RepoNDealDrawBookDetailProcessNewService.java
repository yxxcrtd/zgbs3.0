/*
 * 文件名：LendDrawBookDetailProcessService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年5月3日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.repondrawbookdetail.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
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
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 质押式回购计提账务处理 process相关业务处理
 * 
 * @author yangjian
 * @version 1.0 2018年5月3日
 * @see RepoNDealDrawBookDetailProcessNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class RepoNDealDrawBookDetailProcessNewService {

    /**
     * 共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 共通方法service
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

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

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（计提基础信息列表的任务优先级都是一致的，故取第一个）

        // 定义变量
        BigDecimal glb_subjectAmt = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal glb_subjectNum = BigDecimal.ZERO;// 变量.数量余额
        BigDecimal glb_subjectInt = BigDecimal.ZERO;// 变量.应计利息
        BigDecimal glb_subjectProfit = BigDecimal.ZERO;// 变量.应计利息余额

        BigDecimal 计提成本科目余额 = BigDecimal.ZERO;// 变量.计提成本科目余额
        BigDecimal 计提数量余额 = BigDecimal.ZERO;// 变量.计提数量余额

        BigDecimal 次日计提成本科目余额 = BigDecimal.ZERO;// 变量.次日计提成本科目余额

        BigDecimal 计提应计利息余额 = BigDecimal.ZERO;// 变量.计提应计利息余额


        // 1.2 汇总信息查询
        // 变量.昨日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
        // BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
        // DateUtil.addDay(acctEntity.getHappenDate(), -1),
        // DateUtil.addDay(acctEntity.getHappenDate(), -1), acctEntity.getBookDate(),
        // acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);

        // 账套核算参数查询
        List<String> paramCodeList = new ArrayList<String>();
        // REPO_ACCR_TYPE_F02_F03 回购计提方式_质押式
        paramCodeList.add(ValDictCodeList.账套配置表参数项_回购计提方式_质押式.getValue());
        // 调用账务类共通（DTO.账套代码, 参数项：如下）
        List<BokBooksetExt> listBooksetExt = accountingCommonService.getValBokBooksetExt(
            booksetIdNow, paramCodeList);
        // 变量.计提方式 = 根据DTO.现金流二级类型 对应的 产品费用计提方式_XX费
        String calType = "";
        if (!CollectionUtils.isEmpty(listBooksetExt)) {
            // 获取第一条即可
            calType = listBooksetExt.get(0).getParamValue();
        }

        // 循环 计提基础信息列表 进行逻辑处理
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow) {

            String ps = DictCodeList.余额方向_借方.getValue();
            if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(dto.getBusiDetailType())) {// 分录场景为【债券质押式正回购】
                ps = DictCodeList.余额方向_贷方.getValue();
            }


            // 通过业务明细代码，获取标的交易信息 变量.交易标的DTO = 上述共通返回报价列表
            TradeInfoDto dealInfoForTarget = this.getTargetInfo(dto.getBusiId(),
                dto.getBusiDetailType(), dto.getBranch());
            // 保证获取的标的交易信息不为空
            if (dealInfoForTarget == null) {
                continue;
            }
            // 基础科目性质 : 成本
            String baseSubjectNatureCB = DictCodeBaseSubjectNatrueNew.买入返售金融资产_成本.getValue();
            String baseSubjectNatureYjlx = DictCodeBaseSubjectNatrueNew.买入返售金融资产_应计利息.getValue();
            if(dealInfoForTarget.getOFinprodType().equals(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())){
                baseSubjectNatureCB = DictCodeBaseSubjectNatrueNew.卖出回购金融资产_成本.getValue();
                baseSubjectNatureYjlx = DictCodeBaseSubjectNatrueNew.卖出回购金融资产_应计利息.getValue();
            }
            // 获取初始成本科目余额
            SubjectBalance subjectBalanceForAmt = this.getSubjectBalance(booksetIdNow,
                    bokDetailIdNow, happenDateNow, bookDateNow, null,ps,
                    baseSubjectNatureCB, taskPri);

            if (subjectBalanceForAmt != null) {
                // 变量.成本科目余额= DTO.科目余额
                glb_subjectAmt = subjectBalanceForAmt.getBalance() == null ? BigDecimal.ZERO : subjectBalanceForAmt.getBalance();
                计提成本科目余额=subjectBalanceForAmt.getBalance() == null ? BigDecimal.ZERO : subjectBalanceForAmt.getBalance();
                // 变量.成本科目数量=DTO.数量
                glb_subjectNum = subjectBalanceForAmt.getNum() == null ? BigDecimal.ZERO : subjectBalanceForAmt.getNum();
                计提数量余额=subjectBalanceForAmt.getNum() == null ? BigDecimal.ZERO : subjectBalanceForAmt.getNum();
            }
            //根据 交易明细.交易场所IN （“上海证券交易所”，“深圳证券交易所”） 获取费用处理方式
            if(DictCodeList.交易流通场所_上海证券交易所.getValue().equals(dealInfoForTarget.getTradeMarket())
                    || DictCodeList.交易流通场所_深圳证券交易所.getValue().equals(dealInfoForTarget.getTradeMarket())){
/*              //计提成本目余额查询
                SubjectBalance 计提成本目余额查询 = this.getSubjectBalance(booksetIdNow,
                    bokDetailIdNow, happenDateNow, bookDateNow,happenDateNow, ps,
                    baseSubjectNatureCB, taskPri);

                if (计提成本目余额查询 != null) {
                	// 变量.计提成本科目余额= DTO.科目余额
                	计提成本科目余额 = 计提成本目余额查询.getBalance() == null ? BigDecimal.ZERO : 计提成本目余额查询.getBalance();
                    // 变量.计提成本科目数量 = DTO.数量
                	计提数量余额 = 计提成本目余额查询.getNum() == null ? BigDecimal.ZERO : 计提成本目余额查询.getNum();

                }
*/
                // 次日获取初始成本科目余额
                SubjectBalance 次日计提成本目余额查询 = this.getSubjectBalance(booksetIdNow,
                        bokDetailIdNow, happenDateNow,bookDateNow,DateUtil.addDay(happenDateNow,1) , ps,
                        baseSubjectNatureCB, taskPri);
                if (次日计提成本目余额查询 != null) {
                    //变量.次日计提成本科目余额 = DTO.科目余额
                    次日计提成本科目余额 = 次日计提成本目余额查询.getBalance() == null ? BigDecimal.ZERO : 次日计提成本目余额查询.getBalance();
                }

                // 最新余额：应收利息类科目余额
                SubjectBalance 应收利息类科目余额 = this.getSubjectBalance(booksetIdNow,
                        bokDetailIdNow, happenDateNow,bookDateNow,null, ps,
                        baseSubjectNatureYjlx, taskPri);
                if (应收利息类科目余额 != null) {
                    //变量.应计利息余额 = DTO.科目余额
                    glb_subjectInt = 应收利息类科目余额.getBalance() == null ? BigDecimal.ZERO : 应收利息类科目余额.getBalance();
                    计提应计利息余额 = 应收利息类科目余额.getBalance() == null ? BigDecimal.ZERO : 应收利息类科目余额.getBalance();
                    // modify by wj 20200520 --- TISHKBS-430   上交所质押式逆回购交易PTL_SEC_VALUTION表中TDY_INTINCEXP字段统计错误
                    //变量.应计利息余额  = 返回值. 
                    glb_subjectProfit =  应收利息类科目余额.getBalance() == null ? BigDecimal.ZERO : 应收利息类科目余额.getBalance();
                }
/*            	// 最新余额：应收利息类科目余额
                SubjectBalance 计提应收利息类科目余额 = this.getSubjectBalance(booksetIdNow,
                    bokDetailIdNow, happenDateNow,bookDateNow,happenDateNow, ps,
                    baseSubjectNatureYjlx, taskPri);
                if (计提应收利息类科目余额 != null) {
                	//变量.计提应计利息余额 = DTO.科目余额
                	计提应计利息余额 = 计提应收利息类科目余额.getBalance() == null ? BigDecimal.ZERO : 计提应收利息类科目余额.getBalance();
                }
*/
                //在途凭证查询
                List<BookLatestInfoDto> bookLatestInfoDtoList=accountingCommonService.getBookLatestInfo(booksetIdNow,bookDateNow,bokDetailIdNow,null,null,null,null,happenDateNow,null,null);
                for(BookLatestInfoDto bookLatestInfoDto:bookLatestInfoDtoList) {
                    // 分录场景为【债券质押式正回购】
                    if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(dto.getBusiDetailType())) {
                        if(bookLatestInfoDto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.买入返售金融资产_成本.getValue())) {
                            if(bookLatestInfoDto.getCdFlag().equals(DictCodeList.余额方向_贷方.getValue())) {
                                计提成本科目余额=ArithmeticUtil.doSub(计提成本科目余额,bookLatestInfoDto.getoHappenAmt());
                                计提数量余额=ArithmeticUtil.doSub(计提数量余额,bookLatestInfoDto.getHappenNumber());
                            }else {
                                计提成本科目余额=ArithmeticUtil.doAdd(计提成本科目余额,bookLatestInfoDto.getoHappenAmt());
                                计提数量余额=ArithmeticUtil.doAdd(计提数量余额,bookLatestInfoDto.getHappenNumber());
                            }
                        }else if(bookLatestInfoDto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.买入返售金融资产_应计利息.getValue())) {
                            if(bookLatestInfoDto.getCdFlag().equals(DictCodeList.余额方向_贷方.getValue())) {
                                计提应计利息余额=ArithmeticUtil.doSub(计提应计利息余额,bookLatestInfoDto.getoHappenAmt());
                            }else {
                                计提应计利息余额=ArithmeticUtil.doAdd(计提应计利息余额,bookLatestInfoDto.getoHappenAmt());
                            }
                        }
                    }else {
                        if(bookLatestInfoDto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.买入返售金融资产_成本.getValue())) {
                            if(bookLatestInfoDto.getCdFlag().equals(DictCodeList.余额方向_借方.getValue())) {
                                计提成本科目余额=ArithmeticUtil.doSub(计提成本科目余额,bookLatestInfoDto.getoHappenAmt());
                                计提数量余额=ArithmeticUtil.doSub(计提数量余额,bookLatestInfoDto.getHappenNumber());
                            }else {
                                计提成本科目余额=ArithmeticUtil.doAdd(计提成本科目余额,bookLatestInfoDto.getoHappenAmt());
                                计提数量余额=ArithmeticUtil.doAdd(计提数量余额,bookLatestInfoDto.getHappenNumber());
                            }
                        }else if(bookLatestInfoDto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.买入返售金融资产_应计利息.getValue())) {
                            if(bookLatestInfoDto.getCdFlag().equals(DictCodeList.余额方向_借方.getValue())) {
                                计提应计利息余额=ArithmeticUtil.doSub(计提应计利息余额,bookLatestInfoDto.getoHappenAmt());
                            }else {
                                计提应计利息余额=ArithmeticUtil.doAdd(计提应计利息余额,bookLatestInfoDto.getoHappenAmt());
                            }
                        }
                    }
                }
                Date mDateTemp;
                if(DictCodeListExt1.交易所回购计提天数方式_实际占款天数.getValue().equals(CacheUtil.getSysParam("SE_REP_DAY_TYPE"))){
                     mDateTemp = dealInfoForTarget.getMpayDate();
                }else{
                    mDateTemp = DateUtil.addDay(dealInfoForTarget.getMdate(),1);
                }
                if(happenDateNow.compareTo(dealInfoForTarget.getVdate())>=0 && happenDateNow.compareTo(mDateTemp) < 0) {
                    if(glb_subjectAmt.compareTo(BigDecimal.ZERO)==0 && 次日计提成本科目余额.compareTo(BigDecimal.ZERO)==0) {
                        glb_subjectInt=glb_subjectInt.negate();
                    }else {
                        //调用共通设计->【详细设计-计算类共通方法设计.xlsx】->质押式回购计提_交易所
                        BigDecimal[] 应计利息Dto = calculationCommonService.calCollateralisedRepo(dealInfoForTarget,计提数量余额,计提成本科目余额,happenDateNow,calType,计提应计利息余额);
                        //变量.应计利息 = 返回值.发生应计利息
                        glb_subjectInt=ArithmeticUtil.round(应计利息Dto[1], NumberEnum.MONEY);
                        
                    }
                }else {
                    glb_subjectInt=BigDecimal.ZERO;
                }
            }else {

                // 获取前一日余额：应收利息类科目余额
                SubjectBalance subjectBalanceForAmtYsd = this.getSubjectBalance(booksetIdNow,
                        bokDetailIdNow, happenDateNow, bookDateNow,null, ps,
                        baseSubjectNatureYjlx, taskPri);
                // 变量.应计利息余额 = DTO.科目余额
                if (subjectBalanceForAmtYsd != null) {
                    glb_subjectProfit = subjectBalanceForAmtYsd.getBalance() == null ? BigDecimal.ZERO : subjectBalanceForAmtYsd.getBalance();// 变量.成本科目余额
                    // =
                    // DTO.科目余额
                }

                if (happenDateNow.compareTo(dealInfoForTarget.getMdate()) < 0 && happenDateNow.compareTo(dealInfoForTarget.getVdate()) >= 0) {
                    // 调用共通设计->【详细设计-计算类共通方法设计.xlsx】->质押式回购计提
                    BigDecimal calRepo = calculationCommonService.calRepo(dealInfoForTarget,
                            glb_subjectNum, glb_subjectAmt, DateUtil.addDay(happenDateNow, 1), calType,
                            glb_subjectProfit)[1];
                    // 变量.应计利息 = 返回值.发生应计利息
                    // update by caiqun 20201102 合并代码TISHKBS-1527
                    //glb_subjectInt = calRepo;
                    glb_subjectInt=ArithmeticUtil.round(calRepo, NumberEnum.MONEY);
                }
                else {
                    // 变量.应计利息 = 0
                    glb_subjectInt = BigDecimal.ZERO;
                }
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
            // 分类1 -变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_1, dealInfoForTarget.getType1());
            // 分类2 - 变量.交易信息.分类2
            accountSceneMap.put(BookConstant.TYPE_2, dealInfoForTarget.getType2());
            /**modify by yanya on 20201215 回购计提增加元素，利息计提分支：交易市场**/
            accountSceneMap.put(BookConstant.TRADE_MARKET, dealInfoForTarget.getTradeMarket());

            // 交易标的DTO.金融产品类型 = “债券质押式正回购”
            if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(dto.getBusiDetailType())) {// 分录场景为【债券质押式正回购】
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_质押式正回购_计提.getValue());// ad.会计场景代码="质押正逆回购-计提"

            }
            else {// 会计场景代码="质押式逆回购-计提"
                  // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_质押式逆回购_计提.getValue());// ad.会计场景代码="质押式逆回购-计提"
            }
            // 应计利息 <- 变量.应计利息
            accountAmountMap.put(BookConstant.INTAMT, glb_subjectInt);

            // 应计利息 <- 计提明细.交易币种
            accountCurrencyMap.put(BookConstant.INTAMT, dto.getoCcy());

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            // add by weipeng on 20220317：增加辅助核算项配置信息 start
            ad.setSubjectMappingConfigDefault(dto.getSubjectMappingConfig());
            // add by weipeng on 20220317：增加辅助核算项配置信息 end

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
            // // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
            // BigDecimal tdyintincexpNow = BigDecimal.ZERO;
            // if (bokSummaryTableYestdy != null) {
            // tdyintincexpNow = ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexp(),
            // glb_subjectInt);
            // }
            // else {
            // tdyintincexpNow = glb_subjectInt;
            // }
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应计利息余额+ 变量.应计利息
            bokSummaryTableNew.setTdyIntincexp(
                ArithmeticUtil.doAdd(glb_subjectProfit, glb_subjectInt));
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.数量余额
            bokSummaryTableNew.setTdyPosition(计提数量余额);
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
            // 变量.当日统计分析账务明细汇总信息.起息日 = 交易标的DTO.起息日
            bokSummaryTableNew.setVdate(dealInfoForTarget.getVdate());
            // 变量.当日统计分析账务明细汇总信息.到期日 = 交易标的DTO.实际到期日
            bokSummaryTableNew.setMdate(dealInfoForTarget.getMdate());
            // 变量.当日统计分析账务明细汇总信息.剩余期限 = 交易标的DTO.实际到期日-计提类账务处理结果.发生日>=0 ? 交易标的DTO.实际到期日-计提类账务处理结果.发生日：0
            bokSummaryTableNew.setEndDays1(new BigDecimal(DateUtil.getBetweenDayNumber(
                    acctEntity.getHappenDate(), dealInfoForTarget.getMdate())>=0?DateUtil.getBetweenDayNumber(
                    acctEntity.getHappenDate(), dealInfoForTarget.getMdate()):0));
            //变量.当日统计分析账务明细汇总信息.剩余存续期 = 交易标的DTO.实际到期日-计提类账务处理结果.发生日 >=0 ? 交易标的DTO.实际到期日-计提类账务处理结果.发生日 :0
            bokSummaryTableNew.setEndDays2(new BigDecimal(DateUtil.getBetweenDayNumber(
                    acctEntity.getHappenDate(), dealInfoForTarget.getMdate())>=0?DateUtil.getBetweenDayNumber(
                    acctEntity.getHappenDate(), dealInfoForTarget.getMdate()):0));
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
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param onwayDate 在途日期
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri 任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                             Date bookDate, Date onwayDate, String expBalanceDirect,
                                             String baseSubjectNature, Long taskPri) {
        // 定义返回实体类
        SubjectBalance subjectBalance = null;

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
        searchDto.setOnwayDate(onwayDate);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
    }

}

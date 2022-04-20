/*
 * 文件名：DemandDepositTransBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chendijun
 * 修改时间：2018年8月2日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.demanddeposittransbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
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
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 活期存款交易财务处理processor相关处理
 * 
 * @author chendijun
 * @version 1.0 2018年8月2日
 * @see DemandDepositTransBookNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DemandDepositTransBookNewService {

    /**
     * 记账引擎 记账凭证生成类
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 公共记账类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 账务类共通方法
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的共通方法
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * process 数据处理
     * 
     * @param param1
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param1) {
        FinProductDealInfoResult readerEntity = (FinProductDealInfoResult)param1.get(
            GlobalConstant.READER_RESULT);
        // 如果为reader传递的参数为空，返回空，跳出这次处理
        if (readerEntity == null) {
            return null;
        }

        // 初始化返回值
        BookGenerateResult bookGenerateResult = new BookGenerateResult();
        // 获取待处理任务信息
        if (bookGenerateResult.getValBokBookdetailBatch() == null) {
            bookGenerateResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = readerEntity.getListFinProductDealInfoComDTO(); // 金融产品交易信息list
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        // 1.数据准备
        // 1.1 变量定义
        // 定义 变量.现金科目余额
        BigDecimal cashBalance = BigDecimal.ZERO;
        // 定义 变量.应收利息科目余额
        BigDecimal interestBalance = BigDecimal.ZERO;

        // 2.调用共通查询发生日前一日余额
        // 交易期待方向——借方
        String expBalanceDirect = DictCodeList.余额方向_借方.getValue();
//
//        // 2.1.现金类科目余额
//        // 账务类共通方法:科目余额查询-期待方向
//        SubjectBalance subjectBalance = this.getSubjectBalance(readerEntity, expBalanceDirect,
//            subjectNature_BJ, null, taskPri, readerEntity.getHappenDate());
//        cashBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
//
//        // 2.2.应收利息类科目余额
//        // 账务类共通方法:科目余额查询
//        subjectBalance = this.getSubjectBalance(readerEntity, expBalanceDirect,
//            subjectNature_YJLX, null, taskPri, readerEntity.getHappenDate());
//        interestBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();

        // 3.处理交易
        for (FinProductDealInfoComDTO dealInfo : listFinProductDealInfoComDTONow) {
            // 交易信息查询，方法内部调用 标的类共通方法：交易信息查询
//            TradeInfoDto tradeInfoDto = this.getTradeInfo(dealInfo);

            // 记账标准参数类 ,初始化
            AccountDriver ad = new AccountDriver();
            // 设置会计场景树
            ad.setTreeNode(readerEntity.getBookSetTreeNode());

            // 初始化会计场景要素Map
            Map<String, Object> accountSceneMap = new HashMap<String, Object>();
            // 初始化相关金额Map
            Map<String, BigDecimal> accountAmountMap = new HashMap<String, BigDecimal>();
            // 初始化相关币种Map
            Map<String, Object> accountCurrencyMap = new HashMap<String, Object>();

            // 调用共通场景元素赋值-交易类场景元素
            accountSceneMap = bookCommonLogic.transactionElementAssign(readerEntity, dealInfo);

            // 备注 "活期存款交易登账"
            accountSceneMap.put(BookConstant.BOOK_REMARK, "活期存款交易登账");
//            // 分类1 变量.交易信息.分类1
//            accountSceneMap.put(BookConstant.TYPE_1, tradeInfoDto.getType1());
//            // 分类2 变量.交易信息.分类2
//            accountSceneMap.put(BookConstant.TYPE_2, tradeInfoDto.getType2());
            //资金账户类型
            accountSceneMap.put(BookConstant.ACCT_TYPE, dealInfo.getAcctType());

            // modify by weipeng on 20220303 ：会计引擎升级，资金账户科目性质拆分 start
            // 定义 变量.金额元素, 默认 = 活期存款-托管户
            String 交易金额记账元素 =  BookConstant.DEALAMT_HQ;
            String 利息金额记账元素 = BookConstant.INTAMT_HQ;
            // 定义 变量.本金基础科目性质
            String subjectNature_BJ = DictCodeBaseSubjectNatrueNew.活期存款_本金.getValue();
            // 定义 变量.应计利息基础科目性质
            String subjectNature_YJLX = DictCodeBaseSubjectNatrueNew.活期存款_应计利息.getValue();
            // 本方资金账户类型 或 对方资金账户类型对应资金账户类型，赋值对应金额元素
            // 券商资金账户现划分在结算备付金科目性质下
            if (DictCodeListExt1.资金账户类型_结算备付金账户.getValue().equals(dealInfo.getAcctType())
                    || DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(dealInfo.getAcctType())){
                交易金额记账元素 = BookConstant.DEALAMT_JS;
                利息金额记账元素 = BookConstant.INTAMT_JS;
                subjectNature_BJ = DictCodeBaseSubjectNatrueNew.结算备付金_本金.getValue();
                subjectNature_YJLX = DictCodeBaseSubjectNatrueNew.结算备付金_应计利息.getValue();
            }
            if(DictCodeListExt1.资金账户类型_交易保证金账户.getValue().equals(dealInfo.getAcctType())){
                交易金额记账元素 = BookConstant.DEALAMT_CC;
                利息金额记账元素 = BookConstant.INTAMT_CC;
                subjectNature_BJ = DictCodeBaseSubjectNatrueNew.存出保证金_本金.getValue();
                subjectNature_YJLX = DictCodeBaseSubjectNatrueNew.存出保证金_应计利息.getValue();
            }

            // 2.1.现金类科目余额
            // 账务类共通方法:科目余额查询-期待方向
            SubjectBalance subjectBalance = this.getSubjectBalance(readerEntity, expBalanceDirect,
                    subjectNature_BJ, null, taskPri, readerEntity.getHappenDate(), dealInfo.getBokDetailId());
            cashBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();

            // 2.2.应收利息类科目余额
            // 账务类共通方法:科目余额查询
            subjectBalance = this.getSubjectBalance(readerEntity, expBalanceDirect,
                    subjectNature_YJLX, null, taskPri, readerEntity.getHappenDate(), dealInfo.getBokDetailId());
            interestBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            // modify by weipeng on 20220303 ：会计引擎升级，资金账户科目性质拆分 end
            // add by weipeng on 20220221 区分资金账户金额元素 end
            if (DictCodeList.交易方向_买入.getValue().equals(dealInfo.getPs())) {// 买入
                // 会计场景代码="活期存款买入（转仓）"
                ad.setTableId(DictCodeListTableId.会计场景代码_活期存款利息_转仓买入.getValue());

                // 设置accountAmountMap
                // 实付金额：交易明细.交易金额
                accountAmountMap.put(交易金额记账元素,
                    ArithmeticUtil.round(dealInfo.getTradeAmt(), NumberEnum.MONEY));

                // 设置accountCurrencyMap
                // 实付金额：交易明细.交易币种
                accountCurrencyMap.put(交易金额记账元素, dealInfo.getCcy());

                // 全局变量的更新
                // 变量.现金科目余额 = 变量.现金科目余额 + 交易明细.交易金额
                cashBalance = ArithmeticUtil.doAdd(cashBalance, dealInfo.getTradeAmt());
            }
            else if (DictCodeList.交易方向_收息.getValue().equals(dealInfo.getPs())) {// 收息
                // 会计场景代码="活期存款买入（转仓）"
                ad.setTableId(DictCodeListTableId.会计场景代码_活期存款利息_结息.getValue());

                // 设置accountAmountMap

                // 获取交易到期日对应的应收利息 ，并重新赋值给应收利息-【 调用共通方法：科目余额查询-期待方向】
                // 发生日 ：交易明细.到期日-1；基础科目性质:应计利息
                // ArithmeticUtil处理一下应计利息
                BigDecimal interest = this.getSubjectBalance(readerEntity, expBalanceDirect,
                    subjectNature_YJLX, null, null,
                    DateUtil.addDay(dealInfo.getMdate(), -1), dealInfo.getBokDetailId()).getBalance();
                interest = interest == null ? BigDecimal.ZERO : interest;
                interest = ArithmeticUtil.round(interest, NumberEnum.MONEY);

                // 实付金额：交易明细.交易金额
                accountAmountMap.put(交易金额记账元素,
                    ArithmeticUtil.round(dealInfo.getTradeAmt(), NumberEnum.MONEY));
                // 应收利息：变量.应收利息
                accountAmountMap.put(利息金额记账元素,
                    ArithmeticUtil.round(interest, NumberEnum.MONEY));
                // 交易明细.交易金额- 变量.应收利息科目余额 >0
                // 利息差额_贷 = 交易明细.交易金额- 变量.应收利息科目余额
                // 交易明细.交易金额- 变量.应收利息科目余额 <=0
                // 利息差额_借=交易明细.交易金额- 变量.应收利息科目余额
                if (dealInfo.getTradeAmt().compareTo(interest) > 0) {
                    accountAmountMap.put(BookConstant.INCOMEAMT_C, ArithmeticUtil.subtract(
                        dealInfo.getTradeAmt(), interest, NumberEnum.MONEY.getCode(), null));
                }
                else {
                    accountAmountMap.put(BookConstant.INCOMEAMT_D,
                        ArithmeticUtil.multiply(ArithmeticUtil.subtract(dealInfo.getTradeAmt(),
                            interest, NumberEnum.MONEY.getCode(), null), new BigDecimal("-1")));
                }

                // 记账金额元素币种信息塞值--key是随记账金额map的
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealInfo.getCcy());
                }

                // 全局变量的更新
                // 变量.应收利息科目余额 = 变量.应收利息科目余额 - 变量.应收利息
                interestBalance = ArithmeticUtil.doSub(interestBalance, interest);
            }
            else {// 卖出
                  // 会计场景代码="活期存款买入（转仓）"
                ad.setTableId(DictCodeListTableId.会计场景代码_活期存款利息_转仓卖出.getValue());

                // // 获取交易到期日对应的应收利息 ，并重新赋值给应收利息-【 调用共通方法：科目余额查询-期待方向】
                // // 发生日 ：交易明细.到期日；基础科目性质:应计利息
                // // ArithmeticUtil处理一下应计利息
                // BigDecimal interest = this.getSubjectBalance(readerEntity, expBalanceDirect,
                // subjectNature_YJLX, null, taskPri).getBalance();
                // interest = interest == null ? BigDecimal.ZERO : interest;
                // interest = ArithmeticUtil.round(interest, NumberEnum.MONEY);

                // 设置accountAmountMap
                // 实收金额 <-交易明细.交易金额
                // 应收利息余额 <-变量.应收利息科目余额
                // if 交易明细.交易金额- 变量.应收利息科目余额 >0
                // 利息差额_贷 <-交易明细.交易金额- 变量.应收利息科目余额
                // else
                // 利息差额_借<- 交易明细.交易金额- 变量.应收利息科目余额
                BigDecimal dealamt = dealInfo.getTradeAmt();
                BigDecimal intamt = interestBalance;
                accountAmountMap.put(交易金额记账元素,
                    ArithmeticUtil.round(dealamt, NumberEnum.MONEY));
                accountAmountMap.put(利息金额记账元素,
                    ArithmeticUtil.round(intamt, NumberEnum.MONEY));
                if (dealInfo.getTradeAmt().compareTo(interestBalance) > 0) {
                    accountAmountMap.put(BookConstant.INCOMEAMT_C,
                        ArithmeticUtil.subtract(dealInfo.getTradeAmt(), interestBalance,
                            NumberEnum.MONEY.getCode(), null));
                }
                else {

                    accountAmountMap.put(BookConstant.INCOMEAMT_D,
                        ArithmeticUtil.multiply(ArithmeticUtil.subtract(dealInfo.getTradeAmt(),
                            interestBalance, NumberEnum.MONEY.getCode(), null),
                            new BigDecimal("-1")));

                }

                // 记账金额元素币种信息塞值--key是随记账金额map的
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealInfo.getCcy());
                }

                // 全局变量的更新
                // 变量.现金科目余额 = 变量.现金科目余额 + 变量.实收金额
                // 变量.应收利息科目余额 = 变量.应收利息科目余额 - 变量.应收利息余额
                cashBalance = ArithmeticUtil.doAdd(cashBalance, dealInfo.getTradeAmt());
                interestBalance = ArithmeticUtil.doSub(interestBalance, intamt);// ?疑问：interestBalance和intamt一直是一个值
            }

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);
            ad.setAccountAmountMap(accountAmountMap);
            ad.setAccountCurrencyMap(accountCurrencyMap);
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
            ad.setSubjectMappingConfigDefault(dealInfo.getSubjectMappingConfig());
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
            // 记账凭证生成
            BookGenerateResult result = bookLogicNew.genAccount(ad, new SysBatchExecInfo());

            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            bookGenerateResult.getValBokBookdetailBatch().addAll(
                result.getValBokBookdetailBatch());
        }

        return bookGenerateResult;

    }

    /**
     * 调用共通查询发生日前一日余额 调用共通查询：科目余额查询-期待方向，查询现金类科目余额
     * 
     * @param readerEntity reader传递的参数
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param branch 分支号
     * @param taskPri 优先级
     * @param happenDate 发生日
     * @return SubjectBalance 科目余额dto
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(FinProductDealInfoResult readerEntity,
                                             String expBalanceDirect, String baseSubjectNature,
                                             Long branch, Long taskPri, Date happenDate, String bokDetailId) {
        // 定义返回的科目余额
        SubjectBalance balance = new SubjectBalance();

        // 将baseSubjectNature存入list中
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(readerEntity.getBooksetId());
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(readerEntity.getBookDate());
        searchDto.setBranch(branch);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询-期待方向 】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);
        if (subjectBalanceList != null && !subjectBalanceList.isEmpty()) {
            balance = subjectBalanceList.get(0);
        }

        return balance;
    }

    /**
     * 调用共通方法---标的类共通方法：交易信息查询
     * 
     * @param dealInfo 金融产品交易信息对象
     * @return TradeInfoDto 交易信息对象
     * @see
     * @since 1.0
     */

    public TradeInfoDto getTradeInfo(FinProductDealInfoComDTO dealInfo) {
        // 初始化返回值--标的交易信息
        TradeInfoDto tradeInfoDto = new TradeInfoDto();

        // 调用共通方法---标的类共通方法：交易信息查询
        List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(
            dealInfo.getFinprodId(), dealInfo.getFinprodType(), dealInfo.getBranch());

        if (tradeInfoDtoList != null && !tradeInfoDtoList.isEmpty()) {
            tradeInfoDto = tradeInfoDtoList.get(0);
        }
        return tradeInfoDto;
    }

}

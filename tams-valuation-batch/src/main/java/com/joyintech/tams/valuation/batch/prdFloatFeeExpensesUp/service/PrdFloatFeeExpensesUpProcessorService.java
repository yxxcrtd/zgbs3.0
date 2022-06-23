/*
 * 文件名：PrdFloatFeeExpensesUpProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangJin
 * 修改时间：2019年9月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.prdFloatFeeExpensesUp.service;

import java.math.BigDecimal;
import java.util.*;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.tams.common.base.dao.BokDetailIdMapper;
import com.joyintech.tams.common.base.dao.BokSubjectMapper;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokDetailIdKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.PrdCashEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;

/**
 * 浮动管理费挂账批处理Processor相关业务处理
 * 
 * @author zhangJin
 * @version 1.0 2019年9月26日
 * @see PrdFloatFeeExpensesUpProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PrdFloatFeeExpensesUpProcessorService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogic bookLogic;

    @Autowired
    private BokSubjectMapper bokSubjectMapper;
    /**
     * 记账公共类：bookCommonLogic
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    @Autowired
    private BokDetailIdMapper bokDetailIdMapper;
    /**
     * 账套参数查询
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的查询共通方法
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 系统参数：周期型产品浮动管理费是否进行理财账务处理
     */
    public static final String 周期型产品浮动管理费是否进行理财账务处理 = "IS_CYCLY_PRD_DEAL_ACCOUNT";

    private static final String 待分配利润 = DictCodeBaseSubjectNatrue.待分配利润.getValue();

    private static final String 应付超额管理费 = DictCodeBaseSubjectNatrue.应付超额管理费.getValue();


    /**
     * process 数据处理
     * 
     * @param entity
     * @param param1 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param, FinProductDealInfoResult entity) {
        // 若reader传递的参数或金融产品交易信息列表为空，不进行后续处理
        if (entity == null) {
            return null;
        }
        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();

        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        // 周期型产品浮动管理费是否进行理财账务处理
        paramCodeList.add(ValDictCodeList.周期型产品浮动管理费是否进行理财账务处理.getValue());
        // 调用公用方法 - 账套参数查询
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
            entity.getBooksetId(), paramCodeList);

        // 周期型产品浮动管理费是否进行理财账务处理
        String isCyclyPrdDealAccount = (String)getValBokBooksetExtMap.get(
            ValDictCodeList.周期型产品浮动管理费是否进行理财账务处理.getValue());
        // 如果为空，从系统参数中取值
        if (StringUtil.isEmpty(isCyclyPrdDealAccount)) {
            isCyclyPrdDealAccount = CacheUtil.getSysParam(周期型产品浮动管理费是否进行理财账务处理);
        }

        if (StringUtil.isNotEmpty(isCyclyPrdDealAccount)
            && StringUtil.equals(isCyclyPrdDealAccount, "01")) {
            // 获取待处理任务信息
            if (returnResult.getValBokBookdetailBatch() == null) {
                returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
            }

            // 获取待处理任务信息
            SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");

            // 获取从reader中拿到的值
            String finprodIdNow = entity.getBokDetailId();// 账务明细代码
            TreeNode bookSetTreeNodNow = entity.getBookSetTreeNode();// 会计场景
            List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = entity.getListFinProductDealInfoComDTO();// 金融产品交易信息list

            // 获取标的信息.金融产品类型
            TargetFinProduct targetFinProduct = this.getTargetFinProduct(finprodIdNow);
            // 交易信息.分类1
            String type1Now = targetFinProduct.getType1();
            // 交易信息.分类2
            String type2Now = targetFinProduct.getType2();
            BokDetailIdKey key = new BokDetailIdKey();
            key.setBooksetId(entity.getBooksetId());
            key.setBokDetailId(entity.getBokDetailId());
            BokDetailId bokDetailId = bokDetailIdMapper.selectByPrimaryKey(key);

            Map<String, SubjectBalance> balanceMap = new HashMap<>();
            Map<String, String> balFlagMap = new HashMap<>();
            balFlagMap.put(DictCodeBaseSubjectNatrue.待分配利润.getValue(), DictCodeList.余额方向_借方.getValue());
            balFlagMap.put(DictCodeBaseSubjectNatrue.应付超额管理费.getValue(), DictCodeList.余额方向_贷方.getValue());
            // 循环金融产品交易信息list,进行逻辑处理
            for (FinProductDealInfoComDTO dealNow : listFinProductDealInfoComDTONow) {
                // 初始化定义 - 记账场景要素管理
                AccountDriver ad = new AccountDriver();
                // 初始化定义 - 存放该笔记账的场景要素 map定义
                Map<String, Object> accountSceneMap = new HashMap<>();
                // 初始化定义 - 存放该笔记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountMap = new HashMap<>();
                // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
                Map<String, Object> accountCurrencyMap = new HashMap<>();

                // 共通元素设值 - 设置账务引擎参数
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景

                // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
                PrdCashEntity prdCashEntity = new PrdCashEntity();
                prdCashEntity.setBooksetId(dealNow.getBooksetId());
                prdCashEntity.setBookDate(dealNow.getBookDate());
                prdCashEntity.setBooksetDate(dealNow.getBooksetDate());
                prdCashEntity.setTaskPri(dealNow.getTaskPri());
                prdCashEntity.setStandardCcy(dealNow.getStandardCcy());
                prdCashEntity.setFinprodId(bokDetailId.getBusiId());
                prdCashEntity.setFinprodType(bokDetailId.getBusiDetailType());
                prdCashEntity.setBokDetailId(bokDetailId.getBokDetailId());
                accountSceneMap = bookCommonLogic.accrualElementAssignForTaFee(dealNow.getTaskId(),
                    dealNow.getHappenDate(), prdCashEntity);
                // 是否累计余额
                accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_否.getValue());
                accountSceneMap.put(BookConstant.TYPE_1, type1Now);// 分类1 - 变量.交易信息.分类1
                accountSceneMap.put(BookConstant.TYPE_2, type2Now);// 分类2 - 变量.交易信息.分类2
                accountSceneMap.put(BookConstant.MDATE, dealNow.getMdate()); // 结息日 - 到期日
                // 其他字段设值同损益entity
                // 计算余额

                // TODO 会计场景未配置
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_浮动管理费分配.getValue());// ad.会计场景代码 = XXXX;
                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK, "产品_浮动管理费分配");// 凭证说明
                BigDecimal intAmt = dealNow.getIntAmt();
                // 金额元素.申购金额 = [待处理交易].[利息总额]
                accountAmountMap.put(BookConstant.ACCRUED_CHARDES, intAmt);

                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealNow.getCcy());// value值塞入当前交易币种
                }

                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                for(ValBokBookdetailBatch detail :resultNowNow.getValBokBookdetailBatch()) {
                    SubjectBalance data = balanceMap.get(detail.getBaseSubjectNature());
                    if (data == null) {
                        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
                        // 账套代码
                        searchDto.setBooksetId(detail.getBooksetId());
                        // 账务明细代码
                        searchDto.setBokDetailId(detail.getBokDetailId());
                        // 科目号
                        searchDto.setSubjectNo(detail.getSubjectNo());
                        // 入账日
                        searchDto.setBookDate(detail.getBookDate());
                        // 发生日
                        searchDto.setHappenDate(detail.getHappenDate());
                        // 基础科目性质
                        searchDto.setBaseSubjectNatureList(Arrays.asList(detail.getBaseSubjectNature()));
                        // 任务优先级
                        searchDto.setTaskPri(detail.getTaskPri());
                        // 分支序号
                        searchDto.setBranch(detail.getBranch());
                        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectLatestBalance(
                            searchDto);
                        if (CollectionUtils.isEmpty(subjectBalanceList)) {
                            data = new SubjectBalance();
                            data.setBalance(BigDecimal.ZERO);
                            data.setBalFlagHappenDate(balFlagMap.get(detail.getBaseSubjectNature()));
                            data.setNum(BigDecimal.ZERO);
                            data.setBalFlag(balFlagMap.get(detail.getBaseSubjectNature()));
                            data.setbAmt(BigDecimal.ZERO);
                        }
                        else {
                            data = subjectBalanceList.get(0);
                        }
                    }
                    //calBalance(balFlagMap.get(detail.getBaseSubjectNature()), data, detail);
                    SubjectBalance data1 = new SubjectBalance();
                    data1.setBalance(detail.getoAmtHappenDate());
                    data1.setBalFlagHappenDate(detail.getBalFlagHappenDate());
                    data1.setNum(detail.getHappenNumber());
                    data1.setBalFlag(balFlagMap.get(detail.getBaseSubjectNature()));
                    data1.setbAmt(detail.getbAmtHappenDate());
                    balanceMap.put(detail.getBaseSubjectNature(), data1);
                }
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                returnResult.getValBokBookdetailBatch().addAll(
                    resultNowNow.getValBokBookdetailBatch());
            }
            return returnResult;
        }
        return null;

    }

    private void calBalance(String balFlag, SubjectBalance data, ValBokBookdetailBatch detail) {
        balFlag = balFlag != null ? balFlag : DictCodeList.余额方向_借贷双方.getValue();
        if (data.getBalFlagHappenDate() == null) {
            data.setBalFlagHappenDate(DictCodeList.余额方向_借方.getValue());
        }
        Map<String, Object> mapHappenDate = accountingCommonService.balanceDirectDealAccrualAddBalance(
            balFlag, data.getBalFlagHappenDate(), data.getBalance(), data.getNum(),
            detail.getCdFlag(), detail.getoHappenAmt(), detail.getHappenNumber(),
            /**MODIFY BY WUJI ON 20190724 START
             * null,null**/
            data.getbAmt(),detail.getbHappenAmt()
            /**MODIFY BY WUJIE ON 2010724 END**/
        );
        // 原币余额
        detail.setoAmtHappenDate(
            ArithmeticUtil.round((BigDecimal)mapHappenDate.get("balance"), NumberEnum.MONEY));
        // 本币余额
        detail.setbAmtHappenDate(
            ArithmeticUtil.round((BigDecimal)mapHappenDate.get("benbalance"), NumberEnum.MONEY));
        // 数量
        detail.setNumAmtHappenDate(
            ArithmeticUtil.round((BigDecimal)mapHappenDate.get("num"), NumberEnum.MONEY));
        // 余额方向
        detail.setBalFlagHappenDate((String)mapHappenDate.get("balDirect"));
    }
    /**
     * 获取标的基本信息
     * 
     * @param bokDetailId 账务明细代码
     * @return TargetFinProduct 标的基本信息
     * @see
     * @since 1.0
     */
    private TargetFinProduct getTargetFinProduct(String bokDetailId) {
        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
        // 定义查询类型 实体类
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto);

        // 获取标的信息
        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(bokDetailId,
            targetSearchDtoList);
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);

        // 确保不为null
        if (targetFinProduct == null) {
            targetFinProduct = new TargetFinProduct();
        }

        return targetFinProduct;
    }
}

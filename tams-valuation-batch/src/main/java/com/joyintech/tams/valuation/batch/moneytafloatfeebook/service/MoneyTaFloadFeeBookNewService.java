/*
 * 文件名：MoneyTaFloadFeeBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年6月5日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.moneytafloatfeebook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokAcctLayeringInfo;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.common.base.entity.BokStatFeePosiiton;
import com.joyintech.tams.common.base.entity.MstIntRateInfo;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokStatFeePosition;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.InterestBasisDaysParameter;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 货币型TA浮动费用计提入账批处理 - Service
 * 
 * @author chengzhong
 * @version 1.0 2018年6月5日
 * @see MoneyTaFloadFeeBookNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class MoneyTaFloadFeeBookNewService {

    /**
     * 标的类共通方法
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 批处理账务相关公用方法
     */
    @Autowired
    private AccountingCommonService accountCommonService;

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 计算类共通方法
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * process 数据处理
     * 
     * @return BookGenerateResult 入账结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public BookGenerateResultWithBokStatFeePosition execute(Map<String, Object> params)
         {
        // 获取reader返回值
        StayDrawSubprdEntity acctEntity = (StayDrawSubprdEntity)params.get(
            GlobalConstant.READER_RESULT);// modify by dongjun on 20190115

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // 定义此process的返回变量（因为浮动管理费要塞入帐套资产负债插入及更新，故用此实体类）
        BookGenerateResultWithBokStatFeePosition returnResult = new BookGenerateResultWithBokStatFeePosition();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取从task拿到的参数
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)params.get("BATCHINFO");// 获取待处理任务信息
        Date happenDate = (Date)params.get(BookConstant.HAPPEN_DATE); // 发生日：批处理任务执行信息.处理日（发生日）
        String taskId = batchExecuteInfo.getTaskId();

        // 获取标的信息
        Map<String, Object> targetInfo = this.getTargetInfo(acctEntity.getFinprodId(),
            acctEntity.getCashId(), acctEntity.getCashType1(), acctEntity.getCashType2(),
            acctEntity.getVdate(), happenDate, acctEntity.getFinprodType(),
            acctEntity.getBranch());

        // 标的_业绩报酬计算规则
        List<TargetPerfRewardCalRule> targetPerfRewardCalRuleList = (List<TargetPerfRewardCalRule>)targetInfo.get(
            ValDictCodeList.标的_业绩报酬计算规则.getValue());
        /**add by wujie on 20210507 start**/
        if(targetPerfRewardCalRuleList.size() <= 0){
            return null;
        }
        /**add by wujie on 20210507 end**/

        TargetPerfRewardCalRule targetPerfRewardCalRule = targetPerfRewardCalRuleList.get(
            targetPerfRewardCalRuleList.size() - 1);

        // 标的_业绩报酬计提规则
        List<TargetPerfRewardRule> targetPerfRewardRuleList = (List<TargetPerfRewardRule>)targetInfo.get(
            ValDictCodeList.标的_业绩报酬计提规则.getValue());
        TargetPerfRewardRule targetPerfRewardRule = targetPerfRewardRuleList.get(
            targetPerfRewardRuleList.size() - 1);

        // 标的_信息
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetInfo.get(
            ValDictCodeList.标的_信息.getValue());

        // 标的_业绩报酬计提计划
        List<TargetPerfRewardAccrsche> targetPerfRewardAccrscheList = (List<TargetPerfRewardAccrsche>)targetInfo.get(
            ValDictCodeList.标的_业绩报酬计提计划.getValue());

        // 获取日期信息
        // 定义变量：上次计提日
        Date varScjtDate = null;
        // 定义变量：本次计提日
        Date varBcjtDate = null;
        //起初日期
        Date varQiChuDate = null;
        

        // IF(业绩报酬规则.计提类型 = [数据字典].[计提类型].[高频]）
        if (DictCodeList.计提类型_高频.getValue().equals(targetPerfRewardRule.getAccrType())) {
        	/**ADD BY YANYA ON 20190916 计提频率为高频，则在首次计提日开始计提 即：参数.发生日 < 业绩报酬计提规则.首次计提日return **/
            /**ADD BY WEIPENG ON 20191022 计提频率为高频，且计提结束日不为空时，则在计提结束日后停止计提 即：参数.发生日 >业绩报酬计提规则.计提结束日return **/
            if ((targetPerfRewardRule.geteAccrDate() != null
                && DateUtil.compareDate(happenDate,targetPerfRewardRule.geteAccrDate()) > 0)
               || DateUtil.compareDate(targetPerfRewardRule.getfAccrDate(),happenDate) > 0) {
               return null;
            }
            // 本次计提日 = 参数.发生日
            varBcjtDate = happenDate;
            // 上次计提日 = 本次计提日.上一日
            varScjtDate = DateUtil.addDay(happenDate, -1);
            // 起初日期
            // update by caiqun 20200925 TISHKBS-1097 货币型高频 期初日期设置成本次计提日.上一日，会影响
            // 调用共通方法查询：期初资产净值计算   最后算出来的计费基数会多减了发生日前一日的红利再投，所以期初日期改成发生日，使计费基数只减当日红利再投
            // varQiChuDate = DateUtil.addDay(happenDate, -1);
            varQiChuDate = happenDate;
        } // 标的_业绩报酬计提计划list.size == 0
        else if (targetPerfRewardAccrscheList == null
                 || targetPerfRewardAccrscheList.size() == 0) {
            // 当日无需计提
            return null;

        } // 标的_业绩报酬计提计划list.size == 1
        else if (targetPerfRewardAccrscheList.size() == 1) {
            TargetPerfRewardAccrsche 当次业绩报酬计提计划 = targetPerfRewardAccrscheList.get(0);
            // 本次计提日 = 当次业绩报酬计提计划.计提日
            varBcjtDate = 当次业绩报酬计提计划.getAccrDate();
            // 上次计提日 = 当次业绩报酬计提计划.区间开始日
            varScjtDate = 当次业绩报酬计提计划.getAccrStrDate();
            // 起初日期
            varQiChuDate = DateUtil.addDay(当次业绩报酬计提计划.getAccrStrDate(), 1);

        } // 标的_业绩报酬计提计划list.size >= 2
        else if (targetPerfRewardAccrscheList.size() >= 2) {
            TargetPerfRewardAccrsche 当次业绩报酬计提计划 = targetPerfRewardAccrscheList.get(
                targetPerfRewardAccrscheList.size() - 1);
            // 本次计提日 = 当次业绩报酬计提计划.计提日
            varBcjtDate = 当次业绩报酬计提计划.getAccrDate();
            // 上次计提日 = 当次业绩报酬计提计划.区间开始日
            varScjtDate = 当次业绩报酬计提计划.getAccrStrDate();
            // 起初日期
            varQiChuDate = DateUtil.addDay(当次业绩报酬计提计划.getAccrStrDate(), 1);

        }
        //// 非计提日不做计提
        if (varBcjtDate == null || varBcjtDate.compareTo(happenDate) != 0) {
            return null;
        }
        // 定义变量：资产起息日 = 标的信息.起息日
        Date varVdate = targetFinProduct.getVdate();
        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->查询分层结构子结构
        params.put("booksetId", acctEntity.getBooksetId());
        params.put("lev", acctEntity.getLev());
        List<StayDrawSubprdEntity> listStayDrawSubprdEntity = accountCommonService.querySubBokAcctInfo(
            params);
        String varBasis = DictCodeList.计息基础_A_365.getValue();
        // 定义变量：区间天数 = 本次计提日 - 上次计提日
        long varDateNum =  DateUtil.getBetweenDayNumber(varScjtDate, varBcjtDate);
        // 定义变量：分层代码
        String layeringId = acctEntity.getLayeringId();
        // 定义变量：子分层信息.账务明细代码List
        List<String> bokDetailIdList = new ArrayList<>();
        if (listStayDrawSubprdEntity != null && listStayDrawSubprdEntity.size() > 0) {
            listStayDrawSubprdEntity.forEach(i -> bokDetailIdList.add(i.getBokDetailId()));
        }
        // 定义变量：
        // 计费基础 = 调用共通算法.计息基础天数（NULL，NULL，NULL，NULL，计息基础，NULL）
        int varBasisNum = calculationCommonService.calcInterestBasisDays(
            new InterestBasisDaysParameter(varBasis));
     
        BokStatBokPositionDto position = new BokStatBokPositionDto();
        // 查询日开始
        position.setSdateStart(varBcjtDate);
        // 查询结束日
        position.setSdateEnd(varBcjtDate);
        // 入账日
        position.setBookDate(acctEntity.getBookDate());
        // 账套
        position.setBooksetId(acctEntity.getBooksetId());
        // 分层代码
        position.setLayeringId(layeringId);
        // 本次计提日帐套资产负债信息
        // 获取发生日当日资产负债情况
        List<BokStatBokPosition> 当日账套资产负债信息List = accountCommonService.getValStatBokPosition(
            position);
        BokStatBokPosition 当日账套资产负债信息 = new BokStatBokPosition();
        if (当日账套资产负债信息List != null && 当日账套资产负债信息List.size() > 0) {
            当日账套资产负债信息 = 当日账套资产负债信息List.get(0);
        }
        // 调用共通方法查询：期初资产净值计算 变量.计费基数 = 返回.期初资产净值
        /**MODIFY BY YANYA ON 20190921 【期初资产净值计算]:增加输入参数基数类型(=业绩报酬计算规则.基数类型) (货币型暂未使用) START **/
        BigDecimal 计费基数 = accountCommonService.calStateBokPositionBegin(
            varQiChuDate, varBcjtDate, varVdate, null,
            当日账套资产负债信息, bokDetailIdList, acctEntity.getBookDate(), acctEntity.getBooksetId(),
            DictCodeList.收益类型_货币型.getValue(), targetPerfRewardCalRule.getBaseType());
        /**MODIFY BY YANYA ON 20190921 【期初资产净值计算]:增加输入参数基数类型(=业绩报酬计算规则.基数类型) (货币型暂未使用) END **/
        // 年化收益率 = 当日帐套资产负债信息.周期年化收益率_费前
        BigDecimal varNhsyl = 当日账套资产负债信息.getYieldTermBef();

        // 浮动管理费率 = 调用算法：计算浮动管理费率（业绩报酬计算规则，年化收益率，本次计提日+1，上次计提日）
        BigDecimal 浮动管理费率 = calFloatManageFee(targetPerfRewardCalRule, varNhsyl,
            DateUtil.addDay(varScjtDate, 1), varBcjtDate, targetPerfRewardCalRuleList);

        // 浮动管理费计提金额 = 浮动管理费率 * 计费基数 * 区间天数 / 计费基础
        BigDecimal 浮动管理费计提金额 = ArithmeticUtil.divide(
            ArithmeticUtil.multiply(ArithmeticUtil.multiply(浮动管理费率, 计费基数),
                new BigDecimal(varDateNum)),
            new BigDecimal(varBasisNum), NumberEnum.MONEY.getCode());

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
        ad.setTreeNode(acctEntity.getBookSetTreeNode());// 塞入记账场景树节点 = 会计场景
        ad.setTableId(DictCodeListTableId.会计场景代码_产品_浮动管理费计提.getValue());
         // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
        accountSceneMap = bookCommonLogic.accrualElementAssignForTaFee(taskId, happenDate,
            acctEntity);
        accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());// 分类1 - 变量.交易信息.分类1
        accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());// 分类2 - 变量.交易信息.分类2
        accountSceneMap.put(BookConstant.BOOK_REMARK, "浮动管理费计提登账");// 凭证说明

        // 记账金额元素map塞值
        accountAmountMap.put(BookConstant.ACCRUED_CHARDES, 浮动管理费计提金额); // 费用计提 -> 浮动管理费计提金额

        // 记账金额元素币种信息塞值
        // 因为与记账金额元素map的key及数量一致，故采用循环
        for (String keyNow : accountAmountMap.keySet()) {
            accountCurrencyMap.put(keyNow, acctEntity.getStandardCcy());// value值塞入当前交易币种
        }

        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
        // add by weipeng on 20220312:会计引擎升级改造，增加辅助核算项入参 start
        // 定义 变量.辅助核算项
        SubjectMappingConfig subjectMappingConfig = new SubjectMappingConfig();
        subjectMappingConfig.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
        ad.setSubjectMappingConfigDefault(subjectMappingConfig);
        // add by weipeng on 20220312:会计引擎升级改造，增加辅助核算项入参 end

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

        //定义变量.当日统计分析费用明细汇总信息
        BokStatFeePosiiton bokStatFeePosiiton = new BokStatFeePosiiton();
        
        //3.3 统计分析费用汇总信息赋值
        //变量.当日统计分析费用明细汇总信息.当日费率 = 变量.浮动管理费率
        bokStatFeePosiiton.setTdyFeeRate(浮动管理费率);
        //变量.当日统计分析费用明细汇总信息.当日份额 = 变量.计费基数
        bokStatFeePosiiton.setTdyPosition(计费基数);
        //变量.当日统计分析费用明细汇总信息.费用类型 = DTO.现金流类型
        bokStatFeePosiiton.setFeeType(acctEntity.getCashType2());
        //变量.当日统计分析费用明细汇总信息.当日新增费用 = 变量.浮动管理费计提金额
        bokStatFeePosiiton.setTdyIntincexpAdd(浮动管理费计提金额);
        //变量.当日统计分析费用明细汇总信息.金融产品类型 = DTO.金融产品类型
        bokStatFeePosiiton.setFinprodType(acctEntity.getFinprodType());
        //变量.当日统计分析费用明细汇总信息.金融产品代码 = DTO.金融产品代码
        bokStatFeePosiiton.setFinprodId(acctEntity.getFinprodId());
        // 注当前为旧版本，不是行设计reader中的【调用共通查询：待计提现金流查询(分层)】的方法，故此根据【账务明细代码】代码获取分层信息
        BokAcctLayeringInfo bokAcctLayeringInfo = accountCommonService.getBokAcctLayeringInfoByBokDetailId(acctEntity.getBokDetailId());
        if(bokAcctLayeringInfo!=null){
    		//变量.当日统计分析费用明细汇总信息.分支序号 = DTO.分支序号
            bokStatFeePosiiton.setBranch(new BigDecimal(bokAcctLayeringInfo.getBranch()));
    		//变量.当日统计分析费用明细汇总信息.分层代码 = DTO.分层代码
            bokStatFeePosiiton.setLayeringId(bokAcctLayeringInfo.getLayeringId());
        }
        
        //账套代码
        bokStatFeePosiiton.setBooksetId(acctEntity.getBooksetId());
        //发生日期
        bokStatFeePosiiton.setHappenDate(happenDate);
        //入账日期
        bokStatFeePosiiton.setBookDate(acctEntity.getBookDate());
        //账套日期
        bokStatFeePosiiton.setBooksetDate(acctEntity.getBooksetDate());
        
        returnResult.getBokStatFeePosiitonList().add(bokStatFeePosiiton);
        
        
        
        
        return returnResult;
    }

    /**
     * 计算浮动管理费率
     * 
     * @param targetPerfRewardCalRule 业绩报酬规则
     * @param nhsyl 年化收益率
     * @param lastAccrualDate 上次计提结束日
     * @param accrualDate 本次计提开始日
     * @return 浮动管理费
     */
    private BigDecimal calFloatManageFee(TargetPerfRewardCalRule targetPerfRewardCalRule,
                                         BigDecimal nhsyl, Date lastAccrualDate,
                                         Date accrualDate, List<TargetPerfRewardCalRule> targetPerfRewardCalRuleList) {
        // 基准利率生效日
        Date cdate = null;
        if (DictCodeList.生效规则_期初.getValue().equals(targetPerfRewardCalRule.getActRule())) {
            cdate = lastAccrualDate;
        }
        else if (DictCodeList.生效规则_期末.getValue().equals(
            targetPerfRewardCalRule.getActRule())) {
            cdate = accrualDate;
        } else {
        	cdate = accrualDate;
        }
        // 基准利率集合 = 调用共通查询：利率行情信息查询（基准利率生效日）
        Map<String, MstIntRateInfo> mapIntRateInfo = finProductCommonService.getValIntRateInfo(
            cdate, null);

        // modify by weipeng ：此处改造为一体化版本与单估值版本的生效规则影响逻辑不同，做改造处理，若利率行情信息为空，则添加单估值逻辑 start
        // 定义 变量.计提规则 = 参数.业绩报酬计提规则 ---默认初始化值
        TargetPerfRewardCalRule calRule = targetPerfRewardCalRule;
        if(mapIntRateInfo == null || mapIntRateInfo.isEmpty()){
            // 变量.回补计提规则  = 初始化对象，防止期初业绩基准不存在的情况
            calRule = new TargetPerfRewardCalRule();
            // 业绩报酬计提规则明细与业绩报酬计提规则为一一对应，故此处根据生效日取对应的业绩报酬计提规则即可
            for(TargetPerfRewardCalRule item : targetPerfRewardCalRuleList){
                // 取生效日对应的业绩报酬计提规则，下面计算时取生效规则对应生效日的业绩报酬计提明细
                if(DateUtil.compareDate(cdate, item.getEffDate()) == 0){
                    calRule = item;
                    break;
                }
            }
        }
        // modify by weipeng ：此处改造为一体化版本与单估值版本的生效规则影响逻辑不同，做改造处理，若利率行情信息为空，则添加单估值逻辑 end

        List<TargetPerfRewardCalRuleDetail> targetPerfRewardRuleDetailList = calRule.getTargetPerfRewardCalRuleDetailList();
        List<TargetPerfRewardCalRuleDetail> targetPerfRewardRuleDetailListTmp = new ArrayList<>();
        // 防止期初基准利率为空，加判空处理
        if(!CollectionUtils.isEmpty(targetPerfRewardRuleDetailList)){
            // 集合需要按照层级进行排序
            Collections.sort(targetPerfRewardRuleDetailList,
                new Comparator<TargetPerfRewardCalRuleDetail>() {

                    @Override
                    public int compare(TargetPerfRewardCalRuleDetail o1,
                                       TargetPerfRewardCalRuleDetail o2) {
                        return o1.getLevel() - o2.getLevel();
                    }
                });

            for (TargetPerfRewardCalRuleDetail targetPerfRewardRuleDetail : targetPerfRewardRuleDetailList) {
                // 调整系数 = 业绩报酬规则明细.调整系数
                BigDecimal coefficient = targetPerfRewardRuleDetail.getCoefficient();
                // 利差 = 业绩报酬规则明细.利差
                BigDecimal spreadRate = targetPerfRewardRuleDetail.getSpreadRate();
                BigDecimal basicRate = BigDecimal.ZERO;
                if (!StringUtil.isEmpty(targetPerfRewardRuleDetail.getBasicRate())
                    && mapIntRateInfo != null && mapIntRateInfo.size() > 0) {
                    /**增加为空判定 add by yanya on 20191210**/
                    if(mapIntRateInfo.get(targetPerfRewardRuleDetail.getBasicRate()) != null) {
                        basicRate = mapIntRateInfo.get(
                            targetPerfRewardRuleDetail.getBasicRate()).getIntRate();
                    }
                }
                // 业绩比较基准 = 基准利率 * 调整系数 + 利差
                BigDecimal 业绩比较基准 = ArithmeticUtil.doAdd(
                    ArithmeticUtil.multiply(basicRate, coefficient), spreadRate);
                // 业绩报酬规则明细.业绩比较基准 = 业绩比较基准
                targetPerfRewardRuleDetail.setCompareBasis(业绩比较基准);
                if (业绩比较基准.compareTo(nhsyl) < 0) {
                    targetPerfRewardRuleDetailListTmp.add(targetPerfRewardRuleDetail);
                }
                else {
                    break;
                }
            }
        }

        BigDecimal 浮动管理费率 = BigDecimal.ZERO;

        for (int i = 0; i < targetPerfRewardRuleDetailListTmp.size() - 1; i++ ) {
            TargetPerfRewardCalRuleDetail tmpI = targetPerfRewardRuleDetailListTmp.get(i);
            TargetPerfRewardCalRuleDetail tmpI1 = targetPerfRewardRuleDetailListTmp.get(i + 1);
            // 浮动管理费率 = 浮动管理费率 + （业绩报酬规则明细-(I+1).业绩比较基准 - 业绩报酬规则明细-I.业绩比较基准） *
            // 业绩报酬规则明细-I.银行收取比例
            浮动管理费率 = ArithmeticUtil.doAdd(浮动管理费率,
                ArithmeticUtil.multiply(
                    ArithmeticUtil.doSub(tmpI1.getCompareBasis(), tmpI.getCompareBasis()),
                    tmpI.getChargeRatio()));
        }

        if (targetPerfRewardRuleDetailListTmp != null
            && targetPerfRewardRuleDetailListTmp.size() > 0) {
            // 浮动管理费率 = 浮动管理费率 + （年化收益率 - 【业绩报酬规则明细集合-临时.GET(业绩报酬规则明细集合-临时.SIZE -1).业绩比较基准】） *
            // 【业绩报酬规则明细集合-临时.GET(业绩报酬规则明细集合-临时.SIZE -1)】..银行收取比例
            浮动管理费率 = ArithmeticUtil.doAdd(浮动管理费率,
                ArithmeticUtil.multiply(
                    ArithmeticUtil.subtract(nhsyl,
                        targetPerfRewardRuleDetailListTmp.get(
                            targetPerfRewardRuleDetailListTmp.size() - 1).getCompareBasis()),
                    targetPerfRewardRuleDetailListTmp.get(
                        targetPerfRewardRuleDetailListTmp.size() - 1).getChargeRatio()));
        }

        return 浮动管理费率;
    }
    /**
     * 获取标的相关信息
     * 
     * @param finprodId 金融产品代码
     * @param cashId 现金流代码
     * @param cashType1 现金流一级类型
     * @param cashType2 现金流二级类型
     * @param vdate 起息日
     * @param happenDate 发生日
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    private Map<String, Object> getTargetInfo(String finprodId, String cashId, String cashType1,
                                              String cashType2, Date vdate, Date happenDate,
                                              String finprodType, Long branch) {// modify by
                                                                                // dongjun on
                                                                                // 20190115

        // 定义返回值
        Map<String, Object> returnMap = new HashMap<String, Object>();

        // 查询类型
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();

        // 传入[标的_现金流计算规则]Dto
        TargetSearchDto 现金流计算规则 = new TargetSearchDto();
        现金流计算规则.setCashId(cashId);
        现金流计算规则.setCashType1(cashType1);
        现金流计算规则.setCashType2(cashType2);
        现金流计算规则.setEffdateStart(vdate);
        现金流计算规则.setEffdateEnd(happenDate);
        现金流计算规则.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        queryCriteria.add(现金流计算规则);

        // 传入[标的_现金流支付规则]Dto
        TargetSearchDto 现金流支付规则 = new TargetSearchDto();
        现金流支付规则.setCashId(cashId);
        现金流支付规则.setCashType1(cashType1);
        现金流支付规则.setCashType2(cashType2);
        现金流支付规则.setEffdateStart(vdate);
        现金流支付规则.setEffdateEnd(happenDate);
        现金流支付规则.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
        queryCriteria.add(现金流支付规则);

        // 传入[标的_信息]Dto
        TargetSearchDto 标的信息 = new TargetSearchDto();
        标的信息.setCashId(cashId);
        标的信息.setCashType1(cashType1);
        标的信息.setCashType2(cashType2);
        标的信息.setEffdateStart(vdate);
        标的信息.setEffdateEnd(happenDate);
        标的信息.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(标的信息);

        // 传入[标的_业绩报酬计提计划]Dto
        TargetSearchDto 业绩报酬计提计划 = new TargetSearchDto();
        业绩报酬计提计划.setCashId(cashId);
        业绩报酬计提计划.setCashType1(cashType1);
        业绩报酬计提计划.setCashType2(cashType2);
        业绩报酬计提计划.setEffdateStart(vdate);
        业绩报酬计提计划.setEffdateEnd(happenDate);
        业绩报酬计提计划.setQueryType(ValDictCodeList.标的_业绩报酬计提计划.getValue());
        queryCriteria.add(业绩报酬计提计划);

        // 传入[标的_业绩报酬计算规则]Dto
        TargetSearchDto 业绩报酬计算规则 = new TargetSearchDto();
        业绩报酬计算规则.setCashId(cashId);
        业绩报酬计算规则.setCashType1(cashType1);
        业绩报酬计算规则.setCashType2(cashType2);
        业绩报酬计算规则.setEffdateStart(vdate);
        业绩报酬计算规则.setEffdateEnd(happenDate);
        业绩报酬计算规则.setQueryType(ValDictCodeList.标的_业绩报酬计算规则.getValue());
        queryCriteria.add(业绩报酬计算规则);

        // 传入[标的_业绩报酬计算规则]Dto
        TargetSearchDto 业绩报酬计提规则 = new TargetSearchDto();
        业绩报酬计提规则.setCashId(cashId);
        业绩报酬计提规则.setCashType1(cashType1);
        业绩报酬计提规则.setCashType2(cashType2);
        业绩报酬计提规则.setEffdateStart(vdate);
        业绩报酬计提规则.setEffdateEnd(happenDate);
        业绩报酬计提规则.setQueryType(ValDictCodeList.标的_业绩报酬计提规则.getValue());
        queryCriteria.add(业绩报酬计提规则);

        // 查询标的信息
        Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(
            finprodId, queryCriteria);

        // 有返回值数据
        if (!MapUtils.isEmpty(targetInfoMap)) {
            returnMap.put(ValDictCodeList.标的_现金流计算规则.getValue(), targetInfoMap.get(现金流计算规则)); // 标的_现金流计算规则list
            returnMap.put(ValDictCodeList.标的_信息.getValue(), targetInfoMap.get(标的信息)); // 标的_信息list
            returnMap.put(ValDictCodeList.标的_业绩报酬计提计划.getValue(), targetInfoMap.get(业绩报酬计提计划)); // 标的_业绩报酬计提计划list
            returnMap.put(ValDictCodeList.标的_业绩报酬计算规则.getValue(), targetInfoMap.get(业绩报酬计算规则)); // 标的_业绩报酬计算规则list
            returnMap.put(ValDictCodeList.标的_业绩报酬计提规则.getValue(), targetInfoMap.get(业绩报酬计提规则)); // 标的_业绩报酬计提规则list
        }

        return returnMap;
    }

}

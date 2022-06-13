/*
 * 文件名：TaFloadFeeAccountsService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2018年6月5日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.tafloatfeeaccounts.service;

import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.PrdCashEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.*;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.InterestBasisDaysParameter;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.tafloatfeeaccounts.dao.TaFloadFeeAccountsMapper;
import com.joyintech.tams.valuation.batch.tafloatfeeaccounts.entity.TaFloadFeeAccountsDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * TA浮动费用计提入账批处理 - Service
 * 
 * @author 王波
 * @version 1.0 2018年6月5日
 * @see TaFloadFeeAccountsNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class TaFloadFeeAccountsNewService {

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

    @Autowired
    TaFloadFeeAccountsMapper taFloadFeeAccountsMapper;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * process 数据处理
     * 
     * @return BookGenerateResult 入账结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public BookGenerateResultWithBokStatFeePosition execute(Map<String, Object> params) throws BatchException {
        // 获取reader返回值
        StayDrawSubprdEntity acctEntity = (StayDrawSubprdEntity)params.get(
            GlobalConstant.READER_RESULT);

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
        Date happenDate = (Date)params.get("HAPPEN_DATE"); // 发生日：批处理任务执行信息.处理日（发生日）
        String taskId = batchExecuteInfo.getTaskId();

        // 获取从reader中拿到的值
        String booksetId = acctEntity.getBooksetId();// 账套代码
        String finprodId = acctEntity.getFinprodId();// 金融产品代码
        // String bokDetailId = acctEntity.getBokDetailId(); // 账务明细代码
        String cashId = acctEntity.getCashId();// 现金流代码
        String cashType1 = acctEntity.getCashType1();// 现金流一级类型
        String cashType2 = acctEntity.getCashType2();// 现金流二级类型
        Date vdate = acctEntity.getVdate();// 起息日
        Date bookDate = acctEntity.getBookDate();// 入账日
        String standardCcy = acctEntity.getStandardCcy(); // 本位币
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Long taskPri = acctEntity.getTaskPri();// 任务优先级

        // add by weipeng on 20220312:会计引擎升级改造，增加辅助核算项入参 start
        // 定义 变量.辅助核算项
        SubjectMappingConfig subjectMappingConfig = new SubjectMappingConfig();
        subjectMappingConfig.setCashType2(cashType2);
        // add by weipeng on 20220312:会计引擎升级改造，增加辅助核算项入参 end

        // 获取标的信息
        Map<String, Object> targetInfo = this.getTargetInfo(finprodId, cashId, cashType1,
            cashType2, vdate, happenDate, acctEntity.getFinprodType(), acctEntity.getBranch());// modify
                                                                                               // by
                                                                                               // dongjun
                                                                                               // on
                                                                                               // 20190115

        // 标的_业绩报酬计算规则
        List<TargetPerfRewardCalRule> targetPerfRewardCalRuleList = (List<TargetPerfRewardCalRule>)targetInfo.get(
            ValDictCodeList.标的_业绩报酬计算规则.getValue());
        if(targetPerfRewardCalRuleList.size() <= 0){
            return null;
        }
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

        /**add by yanya on 20190924 邮储个性化：净值产品记录年化收益率R 根据ycbranch20181218版本合并  start**/
        BokStatBokPositionDto tdyPosition = new BokStatBokPositionDto();
        // 查询日开始 = 本次计提日
        tdyPosition.setSdateStart(happenDate);
        // 查询结束日 = 本次计提日
        tdyPosition.setSdateEnd(happenDate);
        tdyPosition.setBooksetId(booksetId);
        tdyPosition.setBookDate(bookDate);
        tdyPosition.setLayeringId(acctEntity.getLayeringId());
        List<BokStatBokPosition> listBokStatBokPosition = accountCommonService.getValStatBokPosition(tdyPosition);
        BokStatBokPosition 期初帐套资产负债信息 = new BokStatBokPosition();
        if (listBokStatBokPosition != null && listBokStatBokPosition.size() > 0) {
            // 期初帐套资产负债信息 = 期初帐套资产负债信息集合.get(0)
            期初帐套资产负债信息 = listBokStatBokPosition.get(0);
            期初帐套资产负债信息.setLayeringId(acctEntity.getLayeringId());
        }
        /**add by yanya on 20190924 邮储个性化：净值产品记录年化收益率R 根据ycbranch20181218版本合并  end**/
        
        // 获取上次和本次计提日
        Date lastAccrualDate = null;// 上次计提日
        Date accrualDate = null;// 本次计提日

        // 定义账套参数查询参数项list 从下面移上来 用作判断
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_估值小数位.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_估值小数尾数处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率位数处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值尾数处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值尾数处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值尾数处理方式.getValue());
        /** add by yanya on 20200115 增加区间内浮动管理费计提方式参数查询**/
        paramCodeList.add(ValDictCodeList.账套配置表参数项_区间内浮动管理费计提方式.getValue());

        Map<String, Object> getValBokBooksetExtMap = accountCommonService
                .getValBokBooksetExtForMap(acctEntity.getBooksetId(), paramCodeList);

        //计提方式
        Object prdIntAccTypeObj =  getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_区间内浮动管理费计提方式.getValue());

        // 业绩报酬规则.计提类型 = 高频
        if (DictCodeList.计提类型_高频.getValue().equals(targetPerfRewardRule.getAccrType())) {
        	/**ADD BY YANYA ON 20190916 计提频率为高频，则在首次计提日开始计提 即：参数.发生日 < 业绩报酬计提规则.首次计提日return **/
            /**ADD BY WEIPENG ON 20191022 计提频率为高频，且计提结束日不为空时，则在计提结束日后停止计提 即：参数.发生日 >业绩报酬计提规则.计提结束日return **/
            if ((targetPerfRewardRule.geteAccrDate() != null
                && DateUtil.compareDate(happenDate,targetPerfRewardRule.geteAccrDate()) > 0)
               || DateUtil.compareDate(targetPerfRewardRule.getfAccrDate(),happenDate) > 0) {
               return null;
            }
            accrualDate = happenDate;
            lastAccrualDate = DateUtil.addDay(accrualDate, -1);// 上次计提日 = 本次计提日 - 1

        } // 标的_业绩报酬计提计划list.size == 0
        else if (targetPerfRewardAccrscheList == null
                 || targetPerfRewardAccrscheList.size() == 0) {
            // 当日无需计提
            return null;

        } // 标的_业绩报酬计提计划list.size == 1
//        else if (targetPerfRewardAccrscheList.size() == 1) {
//            TargetPerfRewardAccrsche 当次业绩报酬计提计划 = targetPerfRewardAccrscheList.get(0);
//            accrualDate = 当次业绩报酬计提计划.getAccrDate();
//            lastAccrualDate = 当次业绩报酬计提计划.getAccrStrDate();
//
//        } // 标的_业绩报酬计提计划list.size >= 2
//        else if (targetPerfRewardAccrscheList.size() >= 2) {
//            TargetPerfRewardAccrsche 当次业绩报酬计提计划 = targetPerfRewardAccrscheList.get(
//                targetPerfRewardAccrscheList.size() - 1);
//            accrualDate = 当次业绩报酬计提计划.getAccrDate();
//            lastAccrualDate = 当次业绩报酬计提计划.getAccrStrDate();
//        }
        //modify by yanya  非区间倒减，不是计提日直接return
        else {
            TargetPerfRewardAccrsche 当次业绩报酬计提计划 = targetPerfRewardAccrscheList.get(targetPerfRewardAccrscheList.size() - 1);
            //IF !账套参数.区间内浮动管理费计提方式 = 区间内每日倒减计提
            if(prdIntAccTypeObj == null || !DictCodeListExt3.区间内浮动管理费计提方式_区间内每日倒减计提.getValue().equals(prdIntAccTypeObj.toString())) {
                // 非计提日不做计提，需要设置账套资产负债信息（不生成账务明细数据）
                //if(本次计提日 != DTO.发生日)
                if (DateUtil.compareDate(当次业绩报酬计提计划.getAccrDate(), happenDate) != 0) {
                    return returnResult;
                }
            }
            //accrualDate = 当次业绩报酬计提计划.getAccrDate();
            accrualDate = happenDate;
            lastAccrualDate = 当次业绩报酬计提计划.getAccrStrDate();
        }

        // 计算浮动管理费
        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        // searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(accrualDate);// 本次计提日
        searchDto.setBookDate(bookDate);// 入账日
        searchDto.setTaskPri(taskPri);

        
        /** modify by wanggengyun on 20190223 start **/
        params.put("booksetId", acctEntity.getBooksetId());
        params.put("lev", acctEntity.getLev());
        // 查询分层结构子结构
        List<StayDrawSubprdEntity> listStayDrawSubprdEntity = accountCommonService.querySubBokAcctInfo(
            params);
        /** modify by wanggengyun on 20190223 end **/
        // 定义 变量.子分层信息.账务明细代码
        List<String> listBokDetailId = new ArrayList<String>();
        if (listStayDrawSubprdEntity != null) {
            listStayDrawSubprdEntity.forEach(item -> {
                listBokDetailId.add(item.getBokDetailId());
            });
        }

        // 定义变量
        Date 资产起息日 = targetFinProduct.getVdate();// 标的信息.起息日
        // Date 资产到期日 = targetFinProduct.getMdate();//标的信息.到期日
        // String 计息基础 = targetCashCalcRule.getBasis(); // 现金流计算规则 计算基础
        // 2018年11月27日 魏鹏：设计变更----定义变量：计息基础 = A/365
        String 计息基础 = DictCodeList.计息基础_A_365.getValue();


//        //IF !账套参数.区间内浮动管理费计提方式 = 区间内每日倒减计提
//        if(prdIntAccTypeObj == null || !DictCodeListExt3.区间内浮动管理费计提方式_区间内每日倒减计提.getValue().equals(prdIntAccTypeObj.toString())) {
//            // 非计提日不做计提，需要设置账套资产负债信息（不生成账务明细数据）
//            //if(本次计提日 != DTO.发生日)
//            if (DateUtil.compareDate(accrualDate, happenDate) != 0) {
//                return returnResult;
//            }
//        }
        /**add by yanya on 20190924 邮储个性化：净值产品记录年化收益率R 根据ycbranch20181218版本合并  start**/
        long 区间天数 = 0L;
        int 计息基础天数 = 0;
        BigDecimal 年化收益率 = BigDecimal.ZERO;
        /**
        if(DateUtil.compareDate(accrualDate, happenDate) != 0){
            if(DateUtil.compareDate(lastAccrualDate, 资产起息日) < 0
                    && !StringUtil.equals(DictCodeList.计提类型_高频.getValue(), targetPerfRewardRule.getAccrType())){
                区间天数 = DateUtil.getBetweenDayNumber(资产起息日, happenDate);
            } else{
                区间天数 = DateUtil.getBetweenDayNumber(lastAccrualDate, happenDate);
            }
            计息基础天数 = calculationCommonService.calcInterestBasisDays(new InterestBasisDaysParameter(计息基础));
            年化收益率 = getNhsyl(happenDate, lastAccrualDate, finprodId, new BigDecimal(计息基础天数), 区间天数, 
                bookDate, acctEntity, 资产起息日, acctEntity.getLayeringId(),getValBokBooksetExtMap);
            期初帐套资产负债信息.setYieldTerm(年化收益率);
            returnResult.setBokStatBokPosition(期初帐套资产负债信息);         
        }
        // 非计提日不做计提，需要设置账套资产负债信息（不生成账务明细数据）
        if (DateUtil.compareDate(accrualDate, happenDate) != 0) {
            return returnResult;
        }
        **/
        /**add by yanya on 20190924 邮储个性化：净值产品记录年化收益率R 根据ycbranch20181218版本合并  end**/
        
        区间天数 =  DateUtil.getBetweenDayNumber(lastAccrualDate, accrualDate);
        // - 上次计提日
        // 计费基础 = 调用公用方法[计息基础天数()]
        计息基础天数 = calculationCommonService.calcInterestBasisDays(new InterestBasisDaysParameter(计息基础));
        // 本次计提日帐套资产负债信息 = 调用共通方法查询：帐套资产负债查询（本次计提日，本次计提日，DTO.帐套代码，DTO.入账日, 分层代码）
        BokStatBokPositionDto position = new BokStatBokPositionDto();
        // 查询日开始 = 本次计提日
        position.setSdateStart(accrualDate);
        // 查询结束日 = 本次计提日
        position.setSdateEnd(accrualDate);
        position.setBooksetId(booksetId);
        position.setBookDate(bookDate);
        // 分层代码 = DTO.分层代码
        position.setLayeringId(acctEntity.getLayeringId());
        // 本次计提日帐套资产负债信息集合 = 调用共通方法查询：帐套资产负债查询（变量：本次计提日，变量：本次计提日，DTO.帐套代码，DTO.入账日,分层代码）
        List<BokStatBokPosition> tdyStatBokPositionList = accountCommonService.getValStatBokPosition(
            position);
        // 定义 变量.本次计提日帐套资产负债信息
        BokStatBokPosition tdyBokStatBokPosition = new BokStatBokPosition();
        if (tdyStatBokPositionList != null && tdyStatBokPositionList.size() > 0) {
            // 本次计提日帐套资产负债信息 = 本次计提日帐套资产负债信息集合.get(0)
            tdyBokStatBokPosition = tdyStatBokPositionList.get(0);
        }
        // 期初帐套资产负债信息 = 调用共通方法查询：帐套资产负债查询（上次计提日，上次计提日，DTO.帐套代码，DTO.入账日,分层代码）
        // 查询日开始 = 上次计提日
        position.setSdateStart(lastAccrualDate);
        // 查询结束日 = 上次计提日
        position.setSdateEnd(lastAccrualDate);
        position.setBooksetId(booksetId);
        position.setBookDate(bookDate);
        // 分层代码 = DTO.分层代码
        position.setLayeringId(acctEntity.getLayeringId());
        // 期初帐套资产负债信息集合 = 调用共通方法查询：帐套资产负债查询（变量：本次计提日，变量：本次计提日，DTO.帐套代码，DTO.入账日,分层代码）
        List<BokStatBokPosition> beginBokStatBokPositionList = accountCommonService.getValStatBokPosition(
            position);
        // 定义 变量.本次计提日帐套资产负债信息
        BokStatBokPosition beginBokStatBokPosition = new BokStatBokPosition();
        if (beginBokStatBokPositionList != null && beginBokStatBokPositionList.size() > 0) {
            // 期初帐套资产负债信息 = 期初帐套资产负债信息集合.get(0)
            beginBokStatBokPosition = beginBokStatBokPositionList.get(0);
        }
        /***
         * ADD BY WUJIE ON 20190907 START
         */
        String floatStarttotalUnitnetDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值小数位数.getValue());
        String floatStarttotalUnitnetType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值尾数处理方式.getValue());
        BigDecimal netUnitValue = beginBokStatBokPosition.getNetUnitValue();
        netUnitValue = accountCommonService.filterByDecTypeAndDigits(netUnitValue, floatStarttotalUnitnetType, floatStarttotalUnitnetDigits);
        /**ADD BY WUJIE ON 20190907 END**/
        /**MODIFY BY YANYA ON 20190921 【期初资产净值计算]:增加输入参数基数类型(=业绩报酬计算规则.基数类型) START **/
        // 调用共通方法查询：期初资产净值计算 变量.计费基数 = 返回.期初资产净值
        BigDecimal 计费基数 = accountCommonService.calStateBokPositionBegin(
            DateUtil.addDay(lastAccrualDate, 1), accrualDate, 资产起息日, netUnitValue,
            tdyBokStatBokPosition, listBokDetailId, bookDate, booksetId,
            DictCodeList.收益类型_净值型.getValue(), targetPerfRewardCalRule.getBaseType());
        /**MODIFY BY YANYA ON 20190921 【期初资产净值计算]:增加输入参数基数类型(=业绩报酬计算规则.基数类型) END **/
        BigDecimal 昨日区间浮动管理费余额 = BigDecimal.ZERO;
        //IF [账套参数.区间内浮动管理费计提方式 = 区间内每日倒减计提] AND [本次计提日 > 上次计提日 +1]
        if(prdIntAccTypeObj != null && DictCodeListExt3.区间内浮动管理费计提方式_区间内每日倒减计提.getValue().equals(prdIntAccTypeObj.toString())
            && DateUtil.compareDate(accrualDate, DateUtil.addDay(lastAccrualDate,1)) > 0) {
            // add by weipeng on 20220304:会计引擎升级改造，增加辅助核算项入参 start


            // add by weipeng on 20220304:会计引擎升级改造，增加辅助核算项入参 end
            // add by weipeng on 20220304：由于费用科目对应基础科目性质为一个，查询余额需要按照科目查询 start
            List<BokSubject> bokSubjectList = bookLogicNew.getFeeSubjectByCode(DictCodeBaseSubjectNatrueNew.运营费用.getValue(), booksetId, standardCcy);
            // 定义 变量.费用对应科目信息
            BokSubject feeSubject = bookLogicNew.getSubjectByInnerCode(bokSubjectList, subjectMappingConfig);

            // add by weipeng on 20220304:会计引擎升级改造，增加辅助核算项入参 end
            /**
            调用 SHEET 查询账务明细当前表发生额 方法查询浮动管理费区间余额
            参数名
            开始日期					上次计提日+1
            结束日期					本次计提日-1
            账套代码					DTO.账套代码
            场景代码					null
            基础科目性质					应付超额管理费
            入账日					DTO.入账日
            借贷方向					贷方
            优先级					本次批处理优先级
            返回结果
            List<账务明细当前表> **/
            Map<String, Object> param = new HashMap<>();
            param.put("booksetId", booksetId);//DTO.账套代码
            param.put("startHappenDate", DateUtil.addDay(lastAccrualDate,1));//上次计提日+1
            param.put("endHappenDate", DateUtil.addDay(accrualDate,-1));//本次计提日-1
            param.put("bookDate", bookDate);//入账日
            param.put("cdFlag", DictCodeList.余额方向_贷方.getValue());//贷方
            param.put("taskId", taskPri);//本次批处理优先级
            param.put("baseSubjectNatureList", Arrays.asList(DictCodeBaseSubjectNatrueNew.应付运营费.getValue()));//应付超额管理费
            param.put("bokDetailId",acctEntity.getBokDetailId());//账务明细代码
            if(feeSubject != null){
                param.put("subjectNo",feeSubject.getSubjectNo());//科目号
            }
            else{
                logger.warn("账套【" + booksetId +"】：未查询到对应浮动管理费科目信息，查询前一日浮动管理费余额可能有误");
            }
            List<TaFloadFeeAccountsDto> taFloadFeeAccountsList = taFloadFeeAccountsMapper.selectBokBookdetailCurrent(param);
            //LOOP 查询结果List<账务明细当前表>，调用账务类共通.余额方向处理_余额加发生额
            if(!StringUtil.isEmptyList(taFloadFeeAccountsList)){
                //变量.实际余额方向 = 贷方
                String actCdFlag = DictCodeList.余额方向_贷方.getValue();
                //变量.实际余额 = 0
                BigDecimal actBalance = BigDecimal.ZERO;
                for(TaFloadFeeAccountsDto item : taFloadFeeAccountsList) {
                    //调用账务类共通.余额方向处理_余额加发生额
                    Map<String, Object> balanceDealMap = accountCommonService.balanceDirectDealAccrualAddBalance(DictCodeList.余额方向_贷方.getValue(),
                            actCdFlag, actBalance, BigDecimal.ZERO, item.getCdFlag(), item.getoHappenAmt(),
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                    //变量.实际余额方向 = 返回结果.余额方向
                    actCdFlag = (String)balanceDealMap.get("balDirect");
                    //变量.实际余额 = 返回结果.科目余额
                    actBalance = (BigDecimal)balanceDealMap.get("balance");
                }
                昨日区间浮动管理费余额 = actBalance;
                昨日区间浮动管理费余额 = ArithmeticUtil.round(昨日区间浮动管理费余额, NumberEnum.MONEY);
            }
        }
        
        // 年化收益率 = 调用查询方法：年化收益率查询（本次计提开始日,本次计提结束日，金融产品代码，计费基础，区间天数，计提结束日累计净值）
        年化收益率 = getNhsyl(accrualDate, lastAccrualDate, finprodId,
                new BigDecimal(计息基础天数), 区间天数, bookDate, acctEntity, 资产起息日, acctEntity.getLayeringId()
                ,getValBokBooksetExtMap,prdIntAccTypeObj.toString(),昨日区间浮动管理费余额);
        /**ADD BY WUJIE ON 20191118 START**/
        期初帐套资产负债信息.setYieldTerm(年化收益率);
        returnResult.setBokStatBokPosition(期初帐套资产负债信息);  
        /**ADD BY WUJIE ON 20191118 END**/     
        // 浮动管理费率 = 调用算法：计算浮动管理费率（业绩报酬规则，年化收益率，本次计提结束日+1，本次计提开始日）
        BigDecimal 浮动管理费率 = calFloatManageFee(targetPerfRewardCalRule, 年化收益率, DateUtil.addDay(lastAccrualDate,1),
            accrualDate,targetPerfRewardCalRuleList);
        // 浮动管理费计提金额 = 浮动管理费率 * 计费基数 * 区间天数 / 计费基础
        BigDecimal 浮动管理费计提金额 = ArithmeticUtil.divide(
            ArithmeticUtil.multiply(ArithmeticUtil.multiply(浮动管理费率, 计费基数), new BigDecimal(区间天数)),
            new BigDecimal(计息基础天数));
        // 小数位处理：根据金额类型处理
        浮动管理费计提金额 = ArithmeticUtil.round(浮动管理费计提金额, NumberEnum.MONEY);

        /** modify by yanya on 20200109 浮动管理费支持每日计提 start**/
        //定义昨日区间浮动管理费余额
        
        //变量.本日浮动管理费计提金额 = 浮动管理费计提金额 - 昨日区间浮动管理费余额
        BigDecimal 本日浮动管理费计提金额 = ArithmeticUtil.doSub(浮动管理费计提金额, 昨日区间浮动管理费余额);
        /** modify by yanya on 20200109 浮动管理费支持每日计提 end**/

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
        ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
        ad.setTableId(DictCodeListTableId.会计场景代码_产品_浮动管理费计提.getValue());

        // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
        accountSceneMap = bookCommonLogic.accrualElementAssignForTaFee(taskId, happenDate,
            acctEntity);
        accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());// 分类1 - 变量.交易信息.分类1
        accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());// 分类2 - 变量.交易信息.分类2
        accountSceneMap.put(BookConstant.BOOK_REMARK, "TA浮动管理费计提登账");// 凭证说明

        // 记账金额元素map塞值
        //accountAmountMap.put(BookConstant.ACCRUED_CHARDES, 浮动管理费计提金额); // 费用计提 -> 浮动管理费计提金额
        accountAmountMap.put(BookConstant.ACCRUED_CHARDES, 本日浮动管理费计提金额); // 费用计提 -> 本日浮动管理费计提金额

        // 记账金额元素币种信息塞值
        // 因为与记账金额元素map的key及数量一致，故采用循环
        for (String keyNow : accountAmountMap.keySet()) {
            accountCurrencyMap.put(keyNow, standardCcy);// value值塞入当前交易币种
        }

        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

        // add by weipeng on 20220304:会计引擎升级改造，增加辅助核算项入参 start
        ad.setSubjectMappingConfigDefault(subjectMappingConfig);
        // add by weipeng on 20220304:会计引擎升级改造，增加辅助核算项入参 end
        
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

        // 3.3 统计分析费用汇总信息赋值
        BokStatFeePosiiton bokStatFeePosiiton = new BokStatFeePosiiton();
		//变量.当日统计分析费用明细汇总信息.当日份额 = 变量.计费基数
        bokStatFeePosiiton.setTdyPosition(计费基数);
		//变量.当日统计分析费用明细汇总信息.当日费率 = 变量.浮动管理费率
        bokStatFeePosiiton.setTdyFeeRate(浮动管理费率);
		//变量.当日统计分析费用明细汇总信息.费用类型 = DTO.现金流类型
        bokStatFeePosiiton.setFeeType(acctEntity.getCashType2());
		//变量.当日统计分析费用明细汇总信息.当日新增费用 = 变量.浮动管理费计提金额
        //bokStatFeePosiiton.setTdyIntincexpAdd(浮动管理费计提金额);
        //变量.当日统计分析费用明细汇总信息.当日新增费用 = 变量.本日浮动管理费计提金额
        bokStatFeePosiiton.setTdyIntincexpAdd(本日浮动管理费计提金额);
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
        
        // 插入【统计分析明细表（费用）】 数据
        returnResult.getBokStatFeePosiitonList().add(bokStatFeePosiiton);
        
        /** add by yanya on 20190924 邮储个性化：净值产品记录年化收益率R 根据ycbranch20181218版本合并  start **/
        期初帐套资产负债信息.setYieldTerm(年化收益率);
        returnResult.setBokStatBokPosition(期初帐套资产负债信息);  
        /** add by yanya on 20190924 邮储个性化：净值产品记录年化收益率R 根据ycbranch20181218版本合并  end **/
         
        return returnResult;
    }

    /**
     * 计算浮动管理费率
     * 
     * @param targetPerfRewardRule 业绩报酬规则
     * @param nhsyl 年化收益率
     * @param lastAccrualDate 本次计提结束日
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
        List<TargetPerfRewardCalRuleDetail> targetPerfRewardRuleDetailListTmp = new ArrayList<>();
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
                    if (StringUtil.isNotEmpty(mapIntRateInfo.get(targetPerfRewardRuleDetail.getBasicRate()))){
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
     * 年化收益率查询
     * 
     * @param accrualDate 本次计提日
     * @param lastAccrualDate 上次计提日
     * @param finprodid 金融产品代码
     * @param feeBasic 计费基础
     * @param days 区间天数
     * @param bookDate 入账日
     * @param dto 待计提金融产品现金流
     * @param zcVdate 资产起息日
     * @return
     * @throws BatchException
     * @see
     * @since 1.0
     */
    private BigDecimal getNhsyl(Date accrualDate, Date lastAccrualDate, String finprodid,
                                BigDecimal feeBasic, long days, Date bookDate, PrdCashEntity dto,
                                Date zcVdate, String layeringId,
                                Map<String, Object> getValBokBooksetExtMap,Object prdIntAccTypeObj,
                                BigDecimal 区间内的浮动管理费计提合计)
            throws BatchException {

        String floatCyclyearYieldDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率小数位数.getValue());
        String floatCyclyearYieldType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率位数处理方式.getValue());
        String floatEndtotalNetDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值小数位数.getValue());
        String floatEndtotalNetType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期末累计净值尾数处理方式.getValue());
        String floatStarttotalNetDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值小数位数.getValue());
        String floatStarttotalNetType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初累计净值尾数处理方式.getValue());
        String floatStarttotalUnitnetDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值小数位数.getValue());
        String floatStarttotalUnitnetType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值尾数处理方式.getValue());

        BigDecimal 年化收益率 = BigDecimal.ZERO;
        BigDecimal 期初的单位净值 = BigDecimal.ONE;
        BigDecimal 期初的累计净值 = BigDecimal.ONE;
        BigDecimal 期末的累计净值 = BigDecimal.ONE;
        BokStatBokPositionDto position = new BokStatBokPositionDto();
        // 查询日开始
        position.setSdateStart(lastAccrualDate);
        // 查询结束日
        position.setSdateEnd(lastAccrualDate);
        // 入账日
        position.setBookDate(bookDate);
        // 账套
        position.setBooksetId(dto.getBooksetId());
        // 分层代码
        position.setLayeringId(layeringId);// modify by dongjun on 20190115
        // 上次计提日 < 变量：资产起息日
        if (DateUtil.compareDate(lastAccrualDate, zcVdate) < 0) {
            // 期初的累计净值,默认为1
            期初的累计净值 = BigDecimal.ONE;
            // 期初单位净值，默认为1
            期初的单位净值 = BigDecimal.ONE;
        }
        else {
            // 获取上次计提日，帐套资产负债信息
            List<BokStatBokPosition> valStatBokPositionList = accountCommonService.getValStatBokPosition(
                position);
            // 根据获取的负债信息，获取对应的净值信息
            if (valStatBokPositionList != null && valStatBokPositionList.size() > 0) {
                BokStatBokPosition valStatBokPosition = valStatBokPositionList.get(0);
                期初的累计净值 = ArithmeticUtil.doAdd(valStatBokPosition.getNetUnitValue(),valStatBokPosition.getTotBounsNet());
                期初的累计净值 = accountCommonService.filterByDecTypeAndDigits(期初的累计净值, floatStarttotalNetType, floatStarttotalNetDigits);
                期初的单位净值 = valStatBokPosition.getNetUnitValue();
                期初的单位净值 = accountCommonService.filterByDecTypeAndDigits(期初的单位净值, floatStarttotalUnitnetType, floatStarttotalUnitnetDigits);
            }
        }
        // 查询日开始
        position.setSdateStart(accrualDate);
        // 查询结束日
        position.setSdateEnd(accrualDate);
        // 本次计提日帐套资产负债信息:调用共通方法查询：帐套资产负债查询（本次计提日，本次计提日，DTO.帐套代码，DTO.入账日）
        List<BokStatBokPosition> valStatBokPositionListForNowOnce = accountCommonService.getValStatBokPosition(
            position);
        if (valStatBokPositionListForNowOnce != null
            && valStatBokPositionListForNowOnce.size() > 0) {
            BokStatBokPosition valStatBokPositionForNowOnce = valStatBokPositionListForNowOnce.get(
                0);
            // 期末的累计净值 = 本次计提日帐套资产负债信息.费前单位净值 + 本次计提日帐套资产负债信息.累计分红净值
            if(prdIntAccTypeObj != null && DictCodeListExt3.区间内浮动管理费计提方式_区间内每日倒减计提.getValue().equals(prdIntAccTypeObj.toString()))
            {        
            	BigDecimal 期末资产净值= valStatBokPositionForNowOnce.getNetAssetValueBef();
            	期末资产净值=期末资产净值.add(区间内的浮动管理费计提合计);
            	BigDecimal 费前净值 = ArithmeticUtil.divide(期末资产净值,valStatBokPositionForNowOnce.getCapital());
            	期末的累计净值 = ArithmeticUtil.doAdd(费前净值,
        	                    valStatBokPositionForNowOnce.getTotBounsNet());		
            }
            else{
	            期末的累计净值 = ArithmeticUtil.doAdd(valStatBokPositionForNowOnce.getNetUnitValueBef(),
	                    valStatBokPositionForNowOnce.getTotBounsNet());
            }
            期末的累计净值 = accountCommonService.filterByDecTypeAndDigits(期末的累计净值, floatEndtotalNetType, floatEndtotalNetDigits);
        }
        // 年化收益率 = （期末的累计净值-期初的累计净值）/ 期初的单位净值 * 计息基础 / 区间天数
        年化收益率 = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(
                        ArithmeticUtil.divide(ArithmeticUtil.doSub(期末的累计净值, 期初的累计净值), 期初的单位净值), feeBasic),
                new BigDecimal(days));
        年化收益率 = accountCommonService.filterByDecTypeAndDigits(年化收益率, floatCyclyearYieldType, floatCyclyearYieldDigits);
        return 年化收益率;
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

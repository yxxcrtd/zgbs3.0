/*
 * 文件名：ExpReturnFeeAccountsService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王耕耘
 * 修改时间：2018年10月8日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.expreturnfeeaccounts.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.BokAcctLayeringInfo;
import com.joyintech.tams.common.base.entity.BokStatFeePosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.PrdCashEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.*;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 预期收益型TA中收计提入账批处理- Service
 * 
 * @author 王波
 * @version 1.0 2018年10月8日
 * @see ExpReturnFeeAccountsNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ExpReturnFeeAccountsNewService {

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
     * @return BookGenerateResultWithBokStatFeePosition 入账结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public BookGenerateResultWithBokStatFeePosition execute(Map<String, Object> params)
         {
        // 获取reader返回值
        PrdCashEntity acctEntity = (PrdCashEntity)params.get(GlobalConstant.READER_RESULT);

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
        Date bookDate = acctEntity.getBookDate();// 入账日
        String standardCcy = acctEntity.getStandardCcy(); // 本位币
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Long taskPri = acctEntity.getTaskPri();// 任务优先级
        //计提中收
        BigDecimal acctMidFee=BigDecimal.ZERO;
        List<String> dataList = new ArrayList<>();
        /*---------------------------------------------------损益类科目、所有者权益类科目余额----------------------------------------------------------------*/
        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        dataList.add(DictCodeList.科目类型_损益类.getValue());
        searchDto.setSubjectTypeList(dataList);      
        searchDto.setTaskPri(taskPri);//批处理优先级
        searchDto.setHappenDate(happenDate);// 发生日
        searchDto.setBookDate(bookDate);// 入账日

        Map<String, BigDecimal> balanceMapSyl=accountCommonService.getSubjectBalanceCollection(searchDto,DictCodeList.借贷方向_贷.getValue(),DictCodeList.是否_否.getValue());
        //计提结束日损益类科目余额
        BigDecimal nowBalanceSyl=calculationCommonService.calcExchangeAmt(balanceMapSyl,standardCcy,happenDate);
        
    
        dataList=new ArrayList<>();
        dataList.add(DictCodeList.科目类型_所有者权益类.getValue());
        searchDto.setSubjectTypeList(dataList);
       
        Map<String, BigDecimal> balanceMapQyl=accountCommonService.getSubjectBalanceCollection(searchDto,DictCodeList.借贷方向_贷.getValue(),DictCodeList.是否_否.getValue());
        //计提结束日所有者权益余额
        BigDecimal balanceQyl=calculationCommonService.calcExchangeAmt(balanceMapQyl,standardCcy,happenDate);
        
        /*---------------------------------------------------实收资本----------------------------------------------------------------*/     
        searchDto.setSubjectTypeList(null);
        dataList=new ArrayList<>();
        dataList.add(DictCodeBaseSubjectNatrueNew.实收资本.getValue());
        searchDto.setBaseSubjectNatureList(dataList);      
        Map<String, BigDecimal> balanceMapSSZB=accountCommonService.getSubjectBalanceCollection(searchDto,DictCodeList.借贷方向_贷.getValue(),DictCodeList.是否_否.getValue());
        //计提结束日实收资本余额
        BigDecimal balanceSSZB=calculationCommonService.calcExchangeAmt(balanceMapSSZB,standardCcy,happenDate);
        
        /*-------------------------------------------计提结束日其他综合收益余额------------------------------------------------------*/     
        BigDecimal balanceQTZHSY = BigDecimal.ZERO;
        String isIncomeassetsDeductQtzhsy = CacheUtil.getSysParam("IS_INCOMEASSETS_DEDUCT_QTZHSY");
        if(DictCodeListExt1.是或否_是.getValue().equals(isIncomeassetsDeductQtzhsy)){
        	searchDto.setSubjectTypeList(null);
            dataList=new ArrayList<>();
            dataList.add(DictCodeBaseSubjectNatrueNew.其他综合收益_证券.getValue());
            dataList.add(DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue());
            dataList.add(DictCodeBaseSubjectNatrueNew.其他综合收益_申赎.getValue());
            searchDto.setBaseSubjectNatureList(dataList);
            Map<String, BigDecimal> balanceMapQTZHSY=accountCommonService.getSubjectBalanceCollection(searchDto,DictCodeList.借贷方向_贷.getValue(),DictCodeList.是否_否.getValue());
            //计提结束日其他综合收益余额
            balanceQTZHSY=calculationCommonService.calcExchangeAmt(balanceMapQTZHSY,standardCcy,happenDate);
        }
        
        /*---------------------------------------------------计算结果---------------------------------------------------------------*/
        //计提中收 = 计提结束日所有者权益余额+计提结束日损益类科目余额-计提结束日实收资本-变量.计提结束日其他综合收益余额
        acctMidFee=ArithmeticUtil.subtract(ArithmeticUtil.subtract(ArithmeticUtil.doAdd(balanceQyl,nowBalanceSyl),balanceSSZB),balanceQTZHSY);
        /*---------------------------------------------------组装记账元素---------------------------------------------------------------*/
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
        ad.setTableId(DictCodeListTableId.会计场景代码_产品_浮动管理费计提.getValue());
        
        // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
        accountSceneMap = bookCommonLogic.accrualElementAssignForTaFee(taskId, happenDate, acctEntity);
        accountSceneMap.put(BookConstant.BOOK_REMARK, "预期收益型TA中收计提");// 凭证说明

        // 记账金额元素map塞值
        // 应计利息 -> 计提中收
        accountAmountMap.put(BookConstant.ACCRUED_CHARDES,ArithmeticUtil.round(acctMidFee, NumberEnum.MONEY));

        // 记账金额元素币种信息塞值
        // 因为与记账金额元素map的key及数量一致，故采用循环
        for (String keyNow : accountAmountMap.keySet()) {
            // value值塞入当前交易币种
            accountCurrencyMap.put(keyNow, standardCcy);
        }

        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
        // 辅助核算项配置
        SubjectMappingConfig mappingConfig = new SubjectMappingConfig();
        mappingConfig.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
        ad.setSubjectMappingConfigDefault(mappingConfig);
        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

        // 3.3 统计分析费用汇总信息赋值
        BokStatFeePosiiton bokStatFeePosiiton = new BokStatFeePosiiton();
		//变量.当日统计分析费用明细汇总信息.费用类型 = DTO.现金流类型
        bokStatFeePosiiton.setFeeType(acctEntity.getCashType2());
		//变量.当日统计分析费用明细汇总信息.当日新增费用 = 变量.计提中收 
        bokStatFeePosiiton.setTdyIntincexpAdd(acctMidFee);
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
         
        return returnResult;
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
//    private Map<String, Object> getTargetInfo(String finprodId, String cashId, String cashType1, String cashType2, Date vdate, Date happenDate) {
//
//        // 定义返回值
//        Map<String, Object> returnMap = new HashMap<String, Object>();
//
//        // 查询类型
//        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();
//
//        // 传入[标的_现金流计算规则]Dto
//        TargetSearchDto 现金流计算规则 = new TargetSearchDto();
//        现金流计算规则.setCashId(cashId);
//        现金流计算规则.setCashType1(cashType1);
//        现金流计算规则.setCashType2(cashType2);
//        现金流计算规则.setEffdateStart(vdate);
//        现金流计算规则.setEffdateEnd(happenDate);
//        现金流计算规则.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
//        queryCriteria.add(现金流计算规则);
//
//        // 传入[标的_现金流支付规则]Dto
//        TargetSearchDto 现金流支付规则 = new TargetSearchDto();
//        现金流支付规则.setCashId(cashId);
//        现金流支付规则.setCashType1(cashType1);
//        现金流支付规则.setCashType2(cashType2);
//        现金流支付规则.setEffdateStart(vdate);
//        现金流支付规则.setEffdateEnd(happenDate);
//        现金流支付规则.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
//        queryCriteria.add(现金流支付规则);
//
//        // 传入[标的_信息]Dto
//        TargetSearchDto 标的信息 = new TargetSearchDto();
//        标的信息.setCashId(cashId);
//        标的信息.setCashType1(cashType1);
//        标的信息.setCashType2(cashType2);
//        标的信息.setEffdateStart(vdate);
//        标的信息.setEffdateEnd(happenDate);
//        标的信息.setQueryType(ValDictCodeList.标的_信息.getValue());
//        queryCriteria.add(标的信息);
//
//        // 传入[标的_业绩报酬计提计划]Dto
//        TargetSearchDto 业绩报酬计提计划 = new TargetSearchDto();
//        业绩报酬计提计划.setCashId(cashId);
//        业绩报酬计提计划.setCashType1(cashType1);
//        业绩报酬计提计划.setCashType2(cashType2);
//        业绩报酬计提计划.setEffdateStart(vdate);
//        业绩报酬计提计划.setEffdateEnd(happenDate);
//        业绩报酬计提计划.setQueryType(ValDictCodeList.标的_业绩报酬计提计划.getValue());
//        queryCriteria.add(业绩报酬计提计划);
//
//        // 传入[标的_业绩报酬计算规则]Dto
//        TargetSearchDto 业绩报酬计算规则 = new TargetSearchDto();
//        业绩报酬计算规则.setCashId(cashId);
//        业绩报酬计算规则.setCashType1(cashType1);
//        业绩报酬计算规则.setCashType2(cashType2);
//        业绩报酬计算规则.setEffdateStart(vdate);
//        业绩报酬计算规则.setEffdateEnd(happenDate);
//        业绩报酬计算规则.setQueryType(ValDictCodeList.标的_业绩报酬计算规则.getValue());
//        queryCriteria.add(业绩报酬计算规则);
//
//        // 传入[标的_业绩报酬计算规则]Dto
//        TargetSearchDto 业绩报酬计提规则 = new TargetSearchDto();
//        业绩报酬计提规则.setCashId(cashId);
//        业绩报酬计提规则.setCashType1(cashType1);
//        业绩报酬计提规则.setCashType2(cashType2);
//        业绩报酬计提规则.setEffdateStart(vdate);
//        业绩报酬计提规则.setEffdateEnd(happenDate);
//        业绩报酬计提规则.setQueryType(ValDictCodeList.标的_业绩报酬计提规则.getValue());
//        queryCriteria.add(业绩报酬计提规则);
//
//        // 查询标的信息
//        Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(finprodId, queryCriteria);
//
//        // 有返回值数据
//        if (!MapUtils.isEmpty(targetInfoMap)) {
//            returnMap.put(ValDictCodeList.标的_现金流计算规则.getValue(), targetInfoMap.get(现金流计算规则)); // 标的_现金流计算规则list
//            returnMap.put(ValDictCodeList.标的_信息.getValue(), targetInfoMap.get(标的信息)); // 标的_信息list
//            returnMap.put(ValDictCodeList.标的_业绩报酬计提计划.getValue(), targetInfoMap.get(业绩报酬计提计划)); // 标的_业绩报酬计提计划list
//            returnMap.put(ValDictCodeList.标的_业绩报酬计算规则.getValue(), targetInfoMap.get(业绩报酬计算规则)); // 标的_业绩报酬计算规则list
//            returnMap.put(ValDictCodeList.标的_业绩报酬计提规则.getValue(), targetInfoMap.get(业绩报酬计提规则)); // 标的_业绩报酬计提规则list
//        }
//
//        return returnMap;
//    }

}

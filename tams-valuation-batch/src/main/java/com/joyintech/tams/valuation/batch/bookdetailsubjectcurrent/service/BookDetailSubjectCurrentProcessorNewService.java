/*
 * 文件名：BookDetailSubjectCurrentProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：huangjian
 * 修改时间：2018年6月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.bookdetailsubjectcurrent.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.tams.valuation.batch.bookdetailcompareinto.service.BookDetailCompareIntoProcessorService;
import com.joyintech.tams.valuation.batch.bookdetailsubjectcurrent.dto.BookDetailSubjectCurrentReadResult;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 更新账务明细（历史表）科目余额批处理 - processor相关业务处理
 * 
 * @author huangjian
 * @version 1.0 2018年6月1日
 * @see BookDetailCompareIntoProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class BookDetailSubjectCurrentProcessorNewService {


    /**
     * AccountingCommonService
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * process 数据处理
     * 
     * @param param 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param) {
        //获取reader返回值
        BookDetailSubjectCurrentReadResult acctEntity = (BookDetailSubjectCurrentReadResult)param.get(GlobalConstant.READER_RESULT);
        
        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }
        // 获取从reader中拿到的值
        // 账套代码
        String booksetIdNow = acctEntity.getBooksetId();
        // 账务明细代码
        String bokDetailIdNow = acctEntity.getBokDetailId();
        // 发生日
        Date happenDateNow = acctEntity.getHappenDate();
        // 入账日
        Date bookDateNow = acctEntity.getBookDate();
        // 基础科目性质
        String baseSubjectNature = acctEntity.getBaseSubjectNature();
        // 该账套下的账务明细代码 - 排序后的
        List<ValBokBookdetailBatch> sortListNow = acctEntity.getValBokBookdetailList();
        // String booksetIdNow = sortListNow.get(0).getBooksetId();
        // 以【账套代码 -业务明细代码 - 明细科目号】为维度，重置【原币余额】和【数量余额】。
        String balFlag = null;// 科目余额方向
        BigDecimal subjectBalanceHappenDate = BigDecimal.ZERO;// 原币科目余额（发生日）
        BigDecimal subjectBalanceHappenDateb = BigDecimal.ZERO;// 本币科目余额（发生日）
        BigDecimal numAmtHappenDate = BigDecimal.ZERO;// 数量余额（发生日
        String balFlagHappenDate = null;// 实际余额方向
        // 科目号
        String subjectNo = sortListNow.get(0).getSubjectNo();
        // 获取科目余额方向
        balFlag = accountingCommonService.getBalFlag(subjectNo, booksetIdNow);
        // 基础科目性质参数
        List<String> baseSubjectNatureList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(baseSubjectNature)) {
            baseSubjectNatureList.add(baseSubjectNature);
        }
        // 获取历史科目余额 科目余额-当前表
        // 设值查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        // 发生日
        searchDto.setHappenDate(happenDateNow);
        //入账日
        searchDto.setBookDate(bookDateNow);
        //账套代码
        searchDto.setBooksetId(booksetIdNow);
        //账务明细代码
        searchDto.setBokDetailId(bokDetailIdNow);
        //分支序号
        searchDto.setBranch(sortListNow.get(0).getBranch());
        //基础科目性质
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        //任务优先级
        searchDto.setTaskPri(acctEntity.getTaskPri() != null ? acctEntity.getTaskPri() : null);
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectLatestBalance(
            searchDto);
        SubjectBalance subjectBalance = new SubjectBalance();
        // 定义 变量.余额Map
        Map<String, SubjectBalance> balanceMap = new HashMap<>();
        // 查询余额结果按照科目号分组
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            for(SubjectBalance item : subjectBalanceList){
                balanceMap.put(item.getSubjectNo(), item);
            }
        }
        // 循环计算科目余额
        for (ValBokBookdetailBatch item : sortListNow) {
            // if 变量.实际余额方向 = null
            if (StringUtils.isEmpty(balFlagHappenDate)) {
                balFlagHappenDate = item.getCdFlag();
            }
            // 根据科目号获取对应余额
            if(balanceMap.get(item.getSubjectNo()) != null){
                subjectBalance = balanceMap.get(item.getSubjectNo());
                //变量.实际余额方向 = 明细科目结果.余额方向
                balFlagHappenDate = subjectBalance.getBalFlagHappenDate();
                // 变量.原币科目余额（发生日） = 明细科目结果.原币科目余额;
                subjectBalanceHappenDate = subjectBalance.getBalance();
                // 变量.本币科目余额（发生日） = 明细科目结果.本币科目余额;
                subjectBalanceHappenDateb = subjectBalance.getbAmt();
                // 变量.数量余额（发生日） = 明细科目结果.数量余额;
                numAmtHappenDate = subjectBalance.getNum();
            }
            else{
                subjectBalance = new SubjectBalance();
                subjectBalance.setSubjectNo(item.getSubjectNo());
            }

            // 原币余额+发生额
            // 调用共通方法【账务类共通方法设计-余额方向处理_发生额加余额SHEET】获取余额+发生额
            Map<String, Object> mapHappenDate = accountingCommonService.balanceDirectDealAccrualAddBalance(balFlag,
                    balFlagHappenDate, subjectBalanceHappenDate, numAmtHappenDate, item.getCdFlag(),
                    item.getoHappenAmt(), item.getHappenNumber()
                    ,subjectBalanceHappenDateb,item.getbHappenAmt()
            );
            // 本位币余额+发生额
            // 调用共通方法【账务类共通方法设计-余额方向处理_发生额加余额SHEET】获取余额+发生额
//			Map<String, Object> mapHappenDateb = accountingCommonService.balanceDirectDealAccrualAddBalance(balFlag,
//					balFlagHappenDate, subjectBalanceHappenDateb, numAmtHappenDate, item.getCdFlag(),
//					item.getbHappenAmt(), item.getHappenNumber());

            // 调用共通方法【账务类共通方法设计-余额方向处理_期待方向SHEET】获取
//			Map<String, Object> directionInfo = accountingCommonService.balanceDirectDealExpectDirection(
//					balFlagHappenDate, (String) mapHappenDateb.get("balDirect"),
//					(BigDecimal) mapHappenDateb.get("balance"), new BigDecimal(0));

            // 原币余额
            item.setoAmtHappenDate(ArithmeticUtil.round((BigDecimal) mapHappenDate.get("balance"), NumberEnum.MONEY));
            // 本币余额
            item.setbAmtHappenDate(ArithmeticUtil.round((BigDecimal) mapHappenDate.get("benbalance"), NumberEnum.MONEY));
            // 数量
            item.setNumAmtHappenDate(ArithmeticUtil.round((BigDecimal) mapHappenDate.get("num"), NumberEnum.MONEY));
            // 余额方向
            item.setBalFlagHappenDate((String) mapHappenDate.get("balDirect"));

            // 更新余额Map
            // 变量.实际余额方向 = 余额方向
            subjectBalance.setBalFlagHappenDate((String) mapHappenDate.get("balDirect"));
            // 原币余额
            subjectBalance.setBalance((BigDecimal) mapHappenDate.get("balance"));
            // 本位币余额
            subjectBalance.setbAmt((BigDecimal) mapHappenDate.get("benbalance"));
            //数量
            subjectBalance.setNum((BigDecimal) mapHappenDate.get("num"));
            balanceMap.put(item.getSubjectNo(), subjectBalance);
        }

        // // 记账凭证生成结果定义
        BookGenerateResult returnResult = new BookGenerateResult();
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        // // 塞入账务明细数据
        returnResult.setValBokBookdetailBatch(sortListNow);

        return returnResult;
    }

}

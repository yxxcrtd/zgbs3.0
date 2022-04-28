/*
 * 文件名：BookDetailCompareIntoProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年5月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.bookdetailcompareinto.service;

import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.BokBookdetailCurrent;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.bookdetailcompareinto.dto.BokBokBookdetailCurrent;
import com.joyintech.tams.valuation.batch.bookdetailcompareinto.dto.BokBookdetailKey;
import com.joyintech.tams.valuation.batch.bookdetailcompareinto.dto.ValBokBookdetailAllExt;
import com.joyintech.tams.valuation.batch.bookdetailcompareinto.dto.ValBokBookdetailCurrentExt;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * 账务明细比较合并批处理 - processor相关业务处理
 *
 * @author wangwei1
 * @version 1.0 2018年6月7日
 * @see BookDetailCompareIntoProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class BookDetailCompareIntoProcessorService {

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
     * @param param1 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param1) {
        // 获取reader返回值
        BokBokBookdetailCurrent acctEntity = (BokBokBookdetailCurrent)param1.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }
        // 账套代码
        // String bookSetIdNow = acctEntity.getBooksetId();
        /************* 1、定义: ************************/
        // 结果集(调整）<账务明细> L
        List<BokBookdetailCurrent> temp = new ArrayList<BokBookdetailCurrent>();
        // 账务明细（当前）待比对<账务明细> LIST_A = 账务明细比对DTO.账务明细（当前）
        List<ValBokBookdetailCurrentExt> LIST_A = acctEntity.getListValBokBookdetailCurrent();
        // 账务明细（历史）待比对<账务明细> LIST_B = 账务明细比对DTO.账务明细（历史）
        List<ValBokBookdetailAllExt> LIST_B = acctEntity.getListValBokBookdetailAll();
        // 账务明细（历史）待比对<账务明细> MAP_C
        Map<BokBookdetailKey, ValBokBookdetailAllExt> MAP_C = new HashMap<BokBookdetailKey, ValBokBookdetailAllExt>();
        Map<BokBookdetailKey, ValBokBookdetailCurrentExt> MAP_A = new HashMap<BokBookdetailKey, ValBokBookdetailCurrentExt>();
        /************* 2、重组账务明细（历史）待比对<账务明细>数据 ************************/
        //2.1、将账务明细（当前）待比对<账务明细>数据按当前表的KEY维度进行汇总，考虑科目的借贷方向，统一按照账套科目表的余额方向进行转化
        BokBookdetailKey key_a = new BokBookdetailKey();
        if (LIST_A != null && LIST_A.size() > 0) {
            for (ValBokBookdetailCurrentExt val : LIST_A) {

                // key_a = A.账套代码||A.发生日||A.账务明细代码||A.场景代码||A.分支号||A.交易编号||A.基础科目性质||A.借贷方向||A.凭证编号||A.原币
                key_a = new BokBookdetailKey();
                key_a.setBooksetId(val.getBooksetId());// 账套代码
                key_a.setHappenDate(val.getHappenDate());// 发生日
                key_a.setBokDetailId(val.getBokDetailId());// 账务明细代码
                key_a.setTableId(val.getTableId());// 场景代码
                key_a.setBranch(val.getBranch());// 分支号
                key_a.setAssetId(val.getTradeId());// 交易编号
                /**新会计引擎升级后，一个分录中可能存在基础科目性质一样的，但不能将其合并 add by yinya on 2022-03-08 start**/
                // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
                String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
                if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
                    key_a.setBaseSubjectNature(val.getSubjectNo());//只能按科目号合并
                }else{
                    key_a.setBaseSubjectNature(val.getBaseSubjectNature());// 基础科目性质
                }
                /**新会计引擎升级后，一个分录中可能存在基础科目性质一样的，但不能将其合并 add by yinya on 2022-03-08 end**/
                key_a.setCdFlag(val.getCdFlag());// 借贷方向
                key_a.setBookId(val.getBookId());
                key_a.setoCcy(val.getoCcy());//原币币种
                if (MAP_A.get(key_a) == null)
                    MAP_A.put(key_a, val);
                else {
                    // 调用共通方法【账务类共通方法设计-余额方向处理_发生额加余额SHEET】获取余额+发生额
                    Map<String, Object> map = accountingCommonService.balanceDirectDealAccrualAddBalance(
                        val.getCdFlag(), // B.借贷方向
                        MAP_A.get(key_a).getCdFlag(), // 实际余额方向 MAP_A.get(key_a).余额方向（入账日）
                        MAP_A.get(key_a).getoHappenAmt(), // 实际余额 MAP_A.get(key_a).余额（入账日）
                        MAP_A.get(key_a).getHappenNumber(), // 实际数量 MAP_A.get(key_a).数量（入账日）
                        val.getCdFlag(), // 借贷方向
                        val.getoHappenAmt(), // .发生额 B.原币发生额
                        val.getHappenNumber(),// 发生数量 B.发生数量
                        MAP_A.get(key_a).getbHappenAmt(), // 实际本币余额
                        val.getbHappenAmt() // .发生额 B.本币发生额
                    );
                    // MAP_A.get(key_a).余额方向（发生日） = 返回值.余额方向
                    MAP_A.get(key_a).setCdFlag(map.get("balDirect").toString());
                    // MAP_A.get(key_a).原币发生额 = 返回值.原币余额
                    MAP_A.get(key_a).setoHappenAmt(new BigDecimal(map.get("balance").toString()));
                    // MAP_A.get(key_a).本币发生额 = 返回值.本币余额
                    MAP_A.get(key_a).setbHappenAmt(new BigDecimal(map.get("benbalance").toString()));
                    // MAP_A.get(key_a).发生数量 = 返回值.数量
                    MAP_A.get(key_a).setHappenNumber(new BigDecimal(map.get("num").toString()));
                }
            }
        }

        // 2.2、将账务明细（历史）待比对<账务明细>数据按当前表的KEY维度进行汇总，考虑科目的借贷方向，统一按照账套科目表的余额方向进行转化
        BokBookdetailKey key_c = new BokBookdetailKey();
        if (LIST_B != null && LIST_B.size() > 0) {
            for (ValBokBookdetailAllExt val : LIST_B) {

                // key_c = B.账套代码||B.发生日||B.账务明细代码||B.场景代码||B.分支号||B.交易编号||B.基础科目性质||B.借贷方向||B.凭证编号||A.原币
                key_c = new BokBookdetailKey();
                key_c.setBooksetId(val.getBooksetId());// 账套代码
                key_c.setHappenDate(val.getHappenDate());// 发生日
                key_c.setBokDetailId(val.getBokDetailId());// 账务明细代码
                key_c.setTableId(val.getTableId());// 场景代码
                key_c.setBranch(val.getBranch());// 分支号
                key_c.setAssetId(val.getTradeId());// 交易编号
                /**新会计引擎升级后，一个分录中可能存在基础科目性质一样的，但不能将其合并 add by yinya on 2022-03-08 start**/
                // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
                String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
                if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
                    key_c.setBaseSubjectNature(val.getSubjectNo());//只能按科目号合并
                }else{
                    key_c.setBaseSubjectNature(val.getBaseSubjectNature());// 基础科目性质
                }
                /**新会计引擎升级后，一个分录中可能存在基础科目性质一样的，但不能将其合并 add by yinya on 2022-03-08 end**/
                key_c.setCdFlag(val.getCdFlag());// 借贷方向
                key_c.setBookId(val.getBookId());
                key_c.setoCcy(val.getoCcy());//原币币种
                if (MAP_C.get(key_c) == null)
                    MAP_C.put(key_c, val);
                else {
                    // 调用共通方法【账务类共通方法设计-余额方向处理_发生额加余额SHEET】获取余额+发生额
                    Map<String, Object> map = accountingCommonService.balanceDirectDealAccrualAddBalance(
                        val.getCdFlag(), // B.借贷方向
                        MAP_C.get(key_c).getCdFlag(), // 实际余额方向 MAP_C.get(key_c).余额方向（入账日）
                        MAP_C.get(key_c).getoHappenAmt(), // 实际余额 MAP_C.get(key_c).余额（入账日）
                        MAP_C.get(key_c).getHappenNumber(), // 实际数量 MAP_C.get(key_c).数量（入账日）
                        val.getCdFlag(), // 借贷方向
                        val.getoHappenAmt(), // 发生额 B.原币发生额
                        val.getHappenNumber(),// 发生数量 B.发生数量
                        MAP_C.get(key_c).getbHappenAmt(), // 实际本币余额
                        val.getbHappenAmt() // .发生额 B.本币发生额
                    );
                    // MAP_C.get(key_c).余额方向（发生日） = 返回值.余额方向
                    MAP_C.get(key_c).setCdFlag(map.get("balDirect").toString());
                    // MAP_C.get(key_c).原币发生额 = 返回值.原币余额
                    MAP_C.get(key_c).setoHappenAmt(new BigDecimal(map.get("balance").toString()));
                    // MAP_C.get(key_c).本币发生额 = 返回值.本币余额
                    MAP_C.get(key_c).setbHappenAmt(new BigDecimal(map.get("benbalance").toString()));
                    // MAP_C.get(key_c).发生数量 = 返回值.数量
                    MAP_C.get(key_c).setHappenNumber(new BigDecimal(map.get("num").toString()));
                }
            }
        }
        // 2.3、比对当前表数据和历史表数据MAP
        // 2.3.1、以当前表数据为主，查询差异
        BokBookdetailKey key = new BokBookdetailKey();
        for (Entry<BokBookdetailKey, ValBokBookdetailCurrentExt> entry : MAP_A.entrySet()) {
            ValBokBookdetailCurrentExt val = MAP_A.get(entry.getKey());
            key = new BokBookdetailKey();
            key.setBooksetId(val.getBooksetId());// 账套代码
            key.setHappenDate(val.getHappenDate());// 发生日
            key.setBokDetailId(val.getBokDetailId());// 账务明细代码
            key.setTableId(val.getTableId());// 场景代码
            key.setBranch(val.getBranch());// 分支号
            key.setAssetId(val.getTradeId());// 交易编号
            key.setBaseSubjectNature(val.getBaseSubjectNature());// 基础科目性质
            key.setCdFlag(val.getCdFlag());// 借贷方向
            key.setBookId(val.getBookId());
            /**add by wujie on 20210113 start remark:key 少了原币金额**/
            key.setoCcy(val.getoCcy());//原币币种
            /**add by wujie on 20210113 start remark:end 少了原币金额**/
            if (MAP_C.get(key) == null) {
                BokBookdetailCurrent comparisonOfAccountRecords = accountingCommonService.comparisonOfAccountRecords(
                    val, null, val.getBookDate(), val.getCdFlag());
                if (comparisonOfAccountRecords != null) {
                    temp.add(comparisonOfAccountRecords);
                }
            }
            else {
                BokBookdetailCurrent comparisonOfAccountRecords = accountingCommonService.comparisonOfAccountRecords(
                    val, MAP_C.get(key), val.getBookDate(), val.getCdFlag());
                if (comparisonOfAccountRecords != null) {
                    temp.add(comparisonOfAccountRecords);
                }
                MAP_C.remove(key);
            }
        }

        // 2.3.2、以历史表数据为主，查询在历史表中但是不在当前表中数据
        for (Entry<BokBookdetailKey, ValBokBookdetailAllExt> entry : MAP_C.entrySet()) {
            BokBookdetailKey key_c1 = entry.getKey();
            BokBookdetailCurrent comparisonOfAccountRecords = accountingCommonService.comparisonOfAccountRecords(
                null, MAP_C.get(key_c1), MAP_C.get(key_c1).getBookDate() ,null);
            if (comparisonOfAccountRecords != null) {
                temp.add(comparisonOfAccountRecords);
            }
        }

        // 排序
        Collections.sort(temp, new Comparator<BokBookdetailCurrent>() {

            @Override
            public int compare(BokBookdetailCurrent p1, BokBookdetailCurrent p2) {
                if (p1.getBookOrder().compareTo(p2.getBookOrder()) > 0) {
                    return 1;
                }
                else if (p1.getBookOrder().compareTo(p2.getBookOrder()) < 0) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });

        // 定义数据处理后的账务明细数据list
        List<ValBokBookdetailBatch> rightDetailList = new ArrayList<ValBokBookdetailBatch>();
        for (BokBookdetailCurrent cDetail : temp) {
            ValBokBookdetailBatch item = new ValBokBookdetailBatch();
            // 复制BEAN
            BeanUtil.copyPropertiesIgnoreNull(item, cDetail);
            // 处理序列
            item.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
            rightDetailList.add(item);
        }

        // 记账凭证生成结果定义
        BookGenerateResult returnResult = new BookGenerateResult();
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        // 塞入账务明细数据
        returnResult.setValBokBookdetailBatch(rightDetailList);

        return returnResult;
    }

}

/*
 * 文件名：ValAddedTaxDrawWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年7月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.common.base.entity.TaxBokBalance;
import com.joyintech.tams.common.base.entity.TaxBokBalanceSum;
import com.joyintech.tams.common.base.entity.TaxBokDetail;
import com.joyintech.tams.common.base.entity.TaxDeductedBalance;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.vatdraw.dao.VatDrawMapper;
import com.joyintech.tams.valuation.batch.vatdraw.dto.VatBatchResult;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;

/**
 * 增值税计提批处理Writer
 * 
 * @author weipeng
 * @version 1.0 2018年7月31日
 * @see VatDrawWriter
 * @since 1.0
 */
@Service
public class VatDrawWriter extends BaseWriter<NeedToWriteEntity> {

    /**
     * 记账凭证生成类
     */
    @Autowired
    private BookAccountGenLogic bookAccountGenLogic;
    
    /**
     * 增值税批处理Mapper
     */
    @Autowired
    private VatDrawMapper vatDrawMapper;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends NeedToWriteEntity> items)
        throws Exception {
        // 获取process返回组合的list值 - 记账凭证list
        List<VatBatchResult> resultList = (List<VatBatchResult>)items;

        // 循环记账凭证list，对每个记账凭证进行操作
        for (VatBatchResult result : resultList) {
            // 插入记账凭证主表信息 和 插入记账凭证明细表信息
            if (result.getBookGenerateResult() != null) {
                bookAccountGenLogic.insertKeepAccount(result.getBookGenerateResult());
            }
            // 插入增值税账务明细流水表
            insertTaxBokDetail(result.getListValTaxBokDetail());
            // vatDrawMapper.insertTaxBokDetail(result.getListValTaxBokDetail());
            // 插入增值税账务明细余额表
            insertTaxBokBalance(result.getListTaxBokBalance());
            // vatDrawMapper.insertTaxBokBalance(result.getListTaxBokBalance());
            // 插入增值税账务明细余额汇总表
            insertTaxBokBalanceSum(result.getListTaxBokBalanceSum());
            // vatDrawMapper.insertTaxBokBalanceSum(result.getListTaxBokBalanceSum());
            // 插入增值税已抵扣增值额
            insertTaxDeductedBalance(result.getListTaxDeductedBalance());
        }
    }

    /**
     * 插入增值税已抵扣增值额
     * @param listTaxDeductedBalance
     */
    private void insertTaxDeductedBalance(List<TaxDeductedBalance> listTaxDeductedBalance) {
    	if (listTaxDeductedBalance == null || listTaxDeductedBalance.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
    	List<List<TaxDeductedBalance>> list = DataHandleUtil.splitList(listTaxDeductedBalance);
        for (List<TaxDeductedBalance> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                vatDrawMapper.insertTaxDeductedBalance(listInsert);
            }
        }
//        List<TaxDeductedBalance> listInsert = new ArrayList<TaxDeductedBalance>();
//        listTaxDeductedBalance.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                vatDrawMapper.insertTaxDeductedBalance(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            vatDrawMapper.insertTaxDeductedBalance(listInsert);
//        }
	}

	/**
     * <一句话功能简述>
     * <功能详细描述>
     * @param listTaxBokBalanceSum
     * @see
     * @since 1.0
     */
    private void insertTaxBokBalanceSum(List<TaxBokBalanceSum> listTaxBokBalanceSum) {
        if (listTaxBokBalanceSum == null || listTaxBokBalanceSum.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<TaxBokBalanceSum>> list = DataHandleUtil.splitList(listTaxBokBalanceSum);
        for (List<TaxBokBalanceSum> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                vatDrawMapper.insertTaxBokBalanceSum(listInsert);
            }
        }
//        List<TaxBokBalanceSum> listInsert = new ArrayList<>();
//        listTaxBokBalanceSum.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                vatDrawMapper.insertTaxBokBalanceSum(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            vatDrawMapper.insertTaxBokBalanceSum(listInsert);
//        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param listTaxBokBalance
     * @see
     * @since 1.0
     */
    private void insertTaxBokBalance(List<TaxBokBalance> listTaxBokBalance) {
        if (listTaxBokBalance == null || listTaxBokBalance.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<TaxBokBalance>> list = DataHandleUtil.splitList(listTaxBokBalance);
        for (List<TaxBokBalance> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                vatDrawMapper.insertTaxBokBalance(listInsert);
            }
        }
//        List<TaxBokBalance> listInsert = new ArrayList<>();
//        listTaxBokBalance.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                vatDrawMapper.insertTaxBokBalance(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            vatDrawMapper.insertTaxBokBalance(listInsert);
//        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param listValTaxBokDetail
     * @see
     * @since 1.0
     */
    private void insertTaxBokDetail(List<TaxBokDetail> listValTaxBokDetail) {
        if (listValTaxBokDetail == null || listValTaxBokDetail.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<TaxBokDetail>> list = DataHandleUtil.splitList(listValTaxBokDetail);
        for (List<TaxBokDetail> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                vatDrawMapper.insertTaxBokDetail(listInsert);
            }
        }
//        List<TaxBokDetail> listInsert = new ArrayList<>();
//        listValTaxBokDetail.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                vatDrawMapper.insertTaxBokDetail(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            vatDrawMapper.insertTaxBokDetail(listInsert);
//        }
    }

}

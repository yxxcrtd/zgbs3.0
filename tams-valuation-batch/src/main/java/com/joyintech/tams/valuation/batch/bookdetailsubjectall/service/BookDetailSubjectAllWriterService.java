/*
 * 文件名：BookDetailSubjectAllReaderService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：huangjian
 * 修改时间：2018年6月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.bookdetailsubjectall.service;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.valuation.batch.bookdetailsubjectall.dao.BookDetailSubjectAllMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;

/**
 * 更新账务明细（历史表）科目余额批处理 - writer相关业务处理
 * 
 * @author huangjian
 * @version 1.0 2018年6月1日
 * @see BookDetailSubjectAllWriterService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class BookDetailSubjectAllWriterService {

    /**
     * reader方法的 Service
     */
    @Autowired
    private BookDetailSubjectAllMapper bookDetailSubjectAllMapper;

    /**
     * 更新账务明细（历史表）
     * 
     * @param result BookGenerateResult
     * @return int
     * @see
     * @since 1.0
     */

    public int updateValBokBookDetailAll(BookGenerateResult result) {
        int count = 0;

        if (result == null) {
            return count;
        }

        // 获取账务明细历史数据
        List<ValBokBookdetailBatch> valBokBookdetailBatchList = result.getValBokBookdetailBatch();

        // 进行批量更新
        for (ValBokBookdetailBatch vo : valBokBookdetailBatchList) {
            bookDetailSubjectAllMapper.updateValBokBookDetailAll(vo);
            count++ ;
        }

        return count;
    }

    /**
     * 批量更新
     * 
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    public int updateValBokBookDetailAllBatch(List<ValBokBookdetailBatch> valBokBookdetailBatchList) {
        int count = 0;

        if (valBokBookdetailBatchList == null || valBokBookdetailBatchList.isEmpty()) {
            return count;
        }
        List<List<ValBokBookdetailBatch>> list = DataHandleUtil.splitList(valBokBookdetailBatchList);
        for (List<ValBokBookdetailBatch> listUpdate : list) {
            if (CollectionUtils.isNotEmpty(listUpdate)) {
                bookDetailSubjectAllMapper.updateValBokBookDetailAllBatch(listUpdate);
            }
        }
        // 一组执行最大条数
        // int maxSqlNum = Integer.parseInt(CacheUtil.getSysParam("UPDATE_MAX_SQL_NUM"));
//        int maxSqlNum = CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class);
        // 获取账务明细历史数据
//        List<ValBokBookdetailBatch> valBokBookdetailBatchList = result.getValBokBookdetailBatch();

        // 进行批量更新
//        if (CollectionUtils.isNotEmpty(valBokBookdetailBatchList)) {
//            List<ValBokBookdetailBatch> listUpdate = new ArrayList<>();
//            for (ValBokBookdetailBatch list : valBokBookdetailBatchList) {
//                if (listUpdate.size() < maxSqlNum) {
//                    listUpdate.add(list);
//                }
//                else if (listUpdate.size() >= maxSqlNum) {
//                    bookDetailSubjectAllMapper.updateValBokBookDetailAllBatch(listUpdate);
//                    listUpdate.clear();
//                    listUpdate.add(list);
//                }
//            }
//
//            if (CollectionUtils.isNotEmpty(listUpdate)) {
//                bookDetailSubjectAllMapper.updateValBokBookDetailAllBatch(listUpdate);
//            }
//        }
        return valBokBookdetailBatchList.size();
    }
}

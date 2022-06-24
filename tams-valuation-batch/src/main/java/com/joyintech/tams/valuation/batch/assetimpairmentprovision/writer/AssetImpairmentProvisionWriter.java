/*
 * 文件名：AssetIntdateCarryoverWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年5月9日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.assetimpairmentprovision.writer;


import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.common.base.dao.ModEclResultMapper;
import com.joyintech.tams.common.base.entity.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.valuation.batch.assetimpairmentprovision.dao.AssetImpairmentProvisionMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.base.dao.BaseAccountWriterMapper;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.common.batch.bookcommon.dao.BokBatchMarketwarningInfoWriterMapper;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;


/**
 * 资产减值计提批处理-writer
 * 
 * @author chufengjie
 * @version 1.0 2020年10月13日
 * @see AssetImpairmentProvisionWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class AssetImpairmentProvisionWriter extends BaseWriter<NeedToWriteEntity>{


    /**
     * 记账凭证生成类
     */
    @Autowired
    private BookAccountGenLogic bookAccountGenLogic;
    
    /**
     * 账务汇总表（账套）表,数据库操作
     */
/*  @Autowired
    private BokStatDetPosiitonMapper bokSummaryTableMapper;*/
    
    /**
     * BokKeepaccountdetailMapper
     */
    @Autowired
    private BaseAccountWriterMapper baseAccountWriterMapper;
    
    
    @Autowired
    private BokBatchMarketwarningInfoWriterMapper bokBatchMarketwarningInfoMapper;
    

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountingMethodMapper accountingMethodMapper;
    
    
    @Autowired
    private LossCommonService lossCommonService;
    @Autowired
    private AssetImpairmentProvisionMapper assetImpairmentProvisionMapper;
    @Autowired
    private ModEclResultMapper modEclResultMapper;
     
    /**
     * 记账凭证数据写入数据库(针对账务明细当前表) {@inheritDoc}
     */
    @Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
        // 获取process返回组合的list值 - 记账凭证list
        List<BookGenerateResultWithBokSummary> resultList = (List<BookGenerateResultWithBokSummary>)items;
  
        
        // 第一步：此处和【BookGenerateResultForCurrentCommonWriter】逻辑一致
        List<ValBokBookdetailBatch> valBokBookdetailBatchList = new ArrayList<>();
        List<ModEclResult> listEclResult = new ArrayList<ModEclResult>();
        // 循环记账凭证list，对每个记账凭证进行操作，
        for (BookGenerateResultWithBokSummary bookGenerateResult : resultList) {
            // 插入记账凭证主表信息 和 插入记账凭证明细表信息
            bookAccountGenLogic.insertKeepAccount(bookGenerateResult);  
            if (bookGenerateResult.getValBokBookdetailBatch() != null) {
                valBokBookdetailBatchList.addAll(bookGenerateResult.getValBokBookdetailBatch());
                listEclResult.add(bookGenerateResult.getModEclResult());
            }
        }
        //第二步：循环数据，插入【账务汇总表（账套）】表
        for(BookGenerateResultWithBokSummary bookGenerateResult : resultList){
            this.insertBokSummary(bookGenerateResult.getBokSummaryList());
        }
        
        // 第三步：循环数据，插入【账务明细辅助表】数据
        for(BookGenerateResultWithBokSummary bookGenerateResult : resultList){
            this.insertBokBookdetailCurrentExt(bookGenerateResult.getBokBookdetailCurrentExtList());
        }
        
        // 第四步：循环数据，插入【行情价格警告信息】数据
        for(BookGenerateResultWithBokSummary bookGenerateResult : resultList){
            this.insertBokBatchMarketwarningInfo(bookGenerateResult.getBokBatchMarketwarningInfoList());
        }
      // 第五步：循环数据，插入【统计分析明细表（资产减值）】数据
      for(BookGenerateResultWithBokSummary bookGenerateResult : resultList){
          lossCommonService.insertBokStatLossPosiitonList(bookGenerateResult.getBokStatLossPosiitonList());
      }
  
	 for(BookGenerateResultWithBokSummary bookGenerateResult : resultList){
	     for(BokStatLossPosiiton bokStatLossPosiiton:bookGenerateResult.getBokStatLossPosiitonList()){
	         assetImpairmentProvisionMapper.updateBokStatDetPosiiton(bokStatLossPosiiton);
	     }
     }
	 // 循环数据更新ECL减值结果
       for(ModEclResult item : listEclResult){
           modEclResultMapper.updateByPrimaryKey(item);
       }
    }
    
    /**
     * 将数据，插入【行情价格警告信息】表
     * @param BokBatchMarketwarningInfoList
     * @return
     * @see
     * @since 1.0
     */
    private int insertBokBatchMarketwarningInfo(List<BokBatchMarketwarningInfo> bokBatchMarketwarningInfoList) {
        
        int sucessNum = 0;
        //数据存在
        if(bokBatchMarketwarningInfoList!=null && !bokBatchMarketwarningInfoList.isEmpty()){
            //循环列表进行插入
//            for(BokBatchMarketwarningInfo vo: bokBatchMarketwarningInfoList){
//                //插入
//                sucessNum = sucessNum + bokBatchMarketwarningInfoMapper.insert(vo);
//            }
            List<List<BokBatchMarketwarningInfo>> list = DataHandleUtil.splitList(bokBatchMarketwarningInfoList);
            for(List<BokBatchMarketwarningInfo> bokBatchMarketwarningInfos : list){
                bokBatchMarketwarningInfoMapper.insertbatch(bokBatchMarketwarningInfos);
            }

        }
        return sucessNum;
    }

    /**
     * 将数据，插入【账务汇总表（账套）】表
     * @param bokSummaryList
     * @return
     * @see
     * @since 1.0
     */
    private int insertBokSummary(List<BokStatDetPosiiton> bokSummaryList){
        
        int sucessNum = 0;
        
        //数据存在
        if(bokSummaryList!=null && !bokSummaryList.isEmpty()){

            List<List<BokStatDetPosiiton>> list = DataHandleUtil.splitList(bokSummaryList);
            for(List<BokStatDetPosiiton> bokStatDetPosiitons : list){
                accountingMethodMapper.deleteBokStatDetPosiitonBatch(bokStatDetPosiitons);
                accountingMethodMapper.insertBokStatDetPosiitonBatch(bokStatDetPosiitons);
            }
        }
        
        return sucessNum;
    }

    /**
     * 将数据，插入【账务明细辅助表】
     * @param bokBookdetailCurrentExtList
     * @return
     * @see
     * @since 1.0
     */
    private int insertBokBookdetailCurrentExt(List<BokBookdetailCurrentExt> bokBookdetailCurrentExtList){

        // 为空返回0
        if (bokBookdetailCurrentExtList == null || bokBookdetailCurrentExtList.isEmpty()) {
            return 0;
        }
        
        // 定义一个固定大小插入存放集合
        List<List<BokBookdetailCurrentExt>> list = DataHandleUtil.splitList(bokBookdetailCurrentExtList);
        for (List<BokBookdetailCurrentExt> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                baseAccountWriterMapper.insertBokBookdetailCurrentExt(listInsert);
            }
        }
//        List<BokBookdetailCurrentExt> listInsert = new ArrayList<BokBookdetailCurrentExt>();
//        bokBookdetailCurrentExtList.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                baseAccountWriterMapper.insertBokBookdetailCurrentExt(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            baseAccountWriterMapper.insertBokBookdetailCurrentExt(listInsert);
//        }
  
        return bokBookdetailCurrentExtList.size();
    }   


}

/*
 * 文件名：BookAccountGenerateWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年6月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.bookaccountgenerate.writer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.valuation.batch.bookaccountgenerate.dao.BookAccountGenerateMapper;
import com.joyintech.tams.valuation.batch.bookaccountgenerate.dto.BookAccountGenerateResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import com.joyintech.tams.common.base.dao.BokVouchDetailMapper;
import com.joyintech.tams.common.base.dao.BokVouchMainMapper;
import com.joyintech.tams.common.base.dao.BokVouchRelMapper;
import com.joyintech.tams.common.base.entity.BokVouchDetail;
import com.joyintech.tams.common.base.entity.BokVouchMain;

@Service
@Scope("prototype")
public class BookAccountGenerateWriter extends BaseWriter<NeedToWriteEntity> {

    @Autowired
    private BookAccountGenerateMapper bookAccountGenerateMapper;

//    @Autowired
//    private BokVouchMainMapper valVouchMainMapper;
//
//    @Autowired
//    private BokVouchDetailMapper valVouchDetailMapper;
//
//    @Autowired
//    private BokVouchRelMapper valVouchRelMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookAccountGenerateWriter.class);

    @Override
    public void write(List<? extends NeedToWriteEntity> items)
        throws Exception {
//        Date bookDate = (Date)this.getParams().get(BookConstant.BOOK_DATE);
//        String bookDateStr = DateUtil.format(bookDate);
//        String yearStr = DateUtil.getYearStr(bookDateStr);
//        String monthStr = String.valueOf(DateUtil.getMonth(bookDateStr));
      //20181123  记账凭证修改   leipeng  start
        //        if (items.stream().map(item -> (BookAccountGenerateResult)item).filter(
//            item -> item.isUpdateHistory()).count() > 0) {
//            bookAccountGenerateMapper.updateHistory(yearStr, monthStr);
//        }
      //20181123  记账凭证修改   leipeng  end
//    	Map<String, Object> params1=this.getParams();
    	//SysBatchExecInfo batchExecInfo=(SysBatchExecInfo)params1.get("BATCHINFO"); 
    	//String batchSeqno = batchExecInfo.getBatchExedetailSeqNo();
    	//String SWITCH  = CacheUtil.getSysParam(BatchCacheCommonConstant.估值核算批处理使用缓存存储开关);
        List<BokVouchMain> mainList = new ArrayList<BokVouchMain>();
        List<BokVouchDetail> detailList = new ArrayList<BokVouchDetail>();
//        List<SubjectBalance> subjectBalance =  new ArrayList<SubjectBalance>();;
//        if(StringUtil.equals(SWITCH, GlobalConstant.SWITCH_ON)){
//        	subjectBalance =  CuerrentMapUtil.get(batchSeqno+BatchCacheCommonConstant.凭证缓存信息, List.class);
//        	if(subjectBalance == null){
//        		subjectBalance = new ArrayList<SubjectBalance>();
//        		CuerrentMapUtil.put(batchSeqno+BatchCacheCommonConstant.凭证缓存信息, subjectBalance);
//        	}
//        }
        
        for (NeedToWriteEntity needToWriteEntity : items) {
            BookAccountGenerateResult result = (BookAccountGenerateResult)needToWriteEntity;
            mainList.addAll(result.getValVouchMain());
            detailList.addAll(result.getValVouchDetail());
//            if(StringUtil.equals(SWITCH, GlobalConstant.SWITCH_ON)){
//            	subjectBalance.addAll(result.getSubjectBalance());
//            }      
//            result.getValVouchRel().forEach(item -> {
//                valVouchRelMapper.insert(item);
//            });
        }
        //批量插入
        List<List<BokVouchMain>> mian = DataHandleUtil.splitList(mainList);
        for (List<BokVouchMain> listInsert : mian) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
            	bookAccountGenerateMapper.insertBokMainBatch(listInsert);
            	logger.debug("批量插入成功");
            }
        }
        List<List<BokVouchDetail>> detail = DataHandleUtil.splitList(detailList);
        for (List<BokVouchDetail> listInsert : detail) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
            	 bookAccountGenerateMapper.insertBokDetailBatch(listInsert);
            	logger.debug("批量插入成功");
            }
        }
       
    }

}

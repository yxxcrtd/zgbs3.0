/*
 * 文件名：BookDetailSubjectCurrentProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：huangjian
 * 修改时间：2018年6月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.bookdetailsubjectcurrent.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.bookdetailsubjectcurrent.dto.BookDetailSubjectCurrentReadResult;
import com.joyintech.tams.valuation.batch.bookdetailsubjectcurrent.service.BookDetailSubjectCurrentProcessorNewService;
import com.joyintech.tams.valuation.batch.bookdetailsubjectcurrent.service.BookDetailSubjectCurrentProcessorService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 更新账务明细（历史表）科目余额批处理- processor
 * 
 * @author huangjian
 * @version 1.0 2018年6月1日
 * @see BookDetailSubjectCurrentProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class BookDetailSubjectCurrentProcessor extends BaseProcessor<NeedOperateEntity, BookGenerateResult> {

    /**
     * processor方法的 Service
     */
    @Autowired
    private BookDetailSubjectCurrentProcessorService bookDetailSubjectCurrentProcessorService;
    /**
     * processor方法的 Service
     */
    @Autowired
    private BookDetailSubjectCurrentProcessorNewService bookDetailSubjectCurrentProcessorNewService;

    @Override
    public BookGenerateResult process(NeedOperateEntity items)
        throws Exception {
    	
        // 获取reader方法的返回值
        BookDetailSubjectCurrentReadResult acctEntity = (BookDetailSubjectCurrentReadResult)items;
        
        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        
        Map<String, BookDetailSubjectCurrentReadResult> temp = new HashMap<>();
        for (ValBokBookdetailBatch entity : acctEntity.getValBokBookdetailList()) {
            // 按基础科目性质,分支号分组
            String key = entity.getBaseSubjectNature() + GlobalConstant.COMMA
                    + entity.getBranch()+ GlobalConstant.COMMA + entity.getoCcy();
            if(temp.containsKey(key)){
                temp.get(key).getValBokBookdetailList().add(entity);
            }else {
                BookDetailSubjectCurrentReadResult paramAcctEntity = new BookDetailSubjectCurrentReadResult();
                //账套代码
                paramAcctEntity.setBooksetId(acctEntity.getBooksetId());
                // 账务明细代码
                paramAcctEntity.setBokDetailId(acctEntity.getBokDetailId());
                // 基础科目性质
                paramAcctEntity.setBaseSubjectNature(entity.getBaseSubjectNature());
                // 发生日
                paramAcctEntity.setHappenDate(acctEntity.getHappenDate());
                // 入账日
                paramAcctEntity.setBookDate(acctEntity.getBookDate());
                // 任务优先级
                paramAcctEntity.setTaskPri(acctEntity.getTaskPri());
                List<ValBokBookdetailBatch> list = new ArrayList<>();
                list.add(entity);
                paramAcctEntity.setValBokBookdetailList(list);
                temp.put(key, paramAcctEntity);
            }
        }
        
        BookGenerateResult result = new BookGenerateResult();
        result.setValBokBookdetailBatch(new ArrayList<>());
        // modify by weipeng on 20220302 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
        for(Entry<String, BookDetailSubjectCurrentReadResult> entry : temp.entrySet()){
            // 将返回值塞入参数中
            thisParams.put(GlobalConstant.READER_RESULT, entry.getValue());
            BookGenerateResult resultOnce = new BookGenerateResult();
            // 调用service方法进行逻辑逻辑
            if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
                resultOnce = bookDetailSubjectCurrentProcessorNewService.execute(thisParams);
            }
            else{
                resultOnce = bookDetailSubjectCurrentProcessorService.execute(thisParams);
            }
            result.getValBokBookdetailBatch().addAll(resultOnce.getValBokBookdetailBatch());
        }
        result.setProdId(
            acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());

        return result;
    }
}

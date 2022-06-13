/*
 * 文件名：SecDealAccountsProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.secdealpreprocessingbook.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.secdealpreprocessingbook.service.SecDealPreprocessingBookNewService;
import com.joyintech.tams.valuation.batch.secdealpreprocessingbook.service.SecDealPreprocessingBookService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 债券交易账务预处理 - processor
 * 
 * @author chengzhong
 * @version 1.0 2018年5月24日
 * @see SecDealPreprocessingBookProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecDealPreprocessingBookProcessor extends BaseProcessor<NeedOperateEntity, BookGenerateResult> {
    
    /**
     * processor方法的 Service
     */
    @Autowired
    private SecDealPreprocessingBookService secDealPreprocessingBookService;

    /**
     * processor方法的 Service
     */
    @Autowired
    private SecDealPreprocessingBookNewService secDealPreprocessingBookNewService;

    @Override
    public BookGenerateResult process(NeedOperateEntity items)
        throws Exception {
        // 获取reader方法的返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)items;

        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);

    // modify by weipeng on 20220113 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        BookGenerateResult result = new BookGenerateResult();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
        // 若按照内部科目编码分录，则需要映射分录对应科目信息
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            result = secDealPreprocessingBookNewService.execute(thisParams);
        }
        else{
            result = secDealPreprocessingBookService.execute(thisParams);
        }
        // modify by weipeng on 20220113 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end
        
        //当为null时候，给予初始化
        if(result == null){
            result = new BookGenerateResult();
        }

        result.setProdId(
            acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());

        return result;
    }
}

/*
 * 文件名：DepositeAssetTransBookProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.outrightrepodealacctreat.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.outrightrepodealacctreat.service.OutrightRepoDealAccTreatNewService;
import com.joyintech.tams.valuation.batch.outrightrepodealacctreat.service.OutrightRepoDealAccTreatService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 买断式回购交易账务处理 - processor
 * 
 * @author chufengjie 
 * @version 1.0 2018年7月26日
 * @see OutrightRepoDealAccTreatProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class OutrightRepoDealAccTreatProcessor extends
        BaseProcessor<NeedOperateEntity, BookGenerateResult> {

    /**
     * 传入参数
     */
    private Map<String, Object> param = new HashMap<String, Object>();

    /**
     * 设置 param
     * 
     * @param param param
     */
    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    /**
     * processor方法的 Service
     */
    @Autowired
    private OutrightRepoDealAccTreatService outrightRepoDealAccTreatService;
    /**
     * processor方法的 Service
     */
    @Autowired
    private OutrightRepoDealAccTreatNewService outrightRepoDealAccTreatNewService;

    @Override
    public BookGenerateResult process(NeedOperateEntity items) throws Exception {
        // 获取reader方法的返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult) items;
        
        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);

        /*** modify by weipeng on 20220317 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start **/
        // 调用service方法进行逻辑逻辑
        BookGenerateResult result =  new BookGenerateResult();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
        // 若按照内部科目编码分录，则需要映射分录对应科目信息
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            result = outrightRepoDealAccTreatNewService.execute(thisParams);
        }
        else{
            result = outrightRepoDealAccTreatService.execute(thisParams);
        }
        /*** modify by weipeng on 20220317 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end **/

        //当为null时候，给予初始化
        if(result == null){
            result = new BookGenerateResult();
        }

        result.setProdId(
            acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());

        return result;
    }
}

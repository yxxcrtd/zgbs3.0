/*
 * 文件名：IrsTransBookProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5
 * 修改人：殷亚
 * 修改时间：2022年4月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.batch.irstransbook.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.irstransbook.service.IrsTransBookNewService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 利率互换交易-账务明细批处理
 * @author 殷亚
 * @date 2022/4/22 14:25
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsTransBookProcessor extends
        BaseProcessor<NeedOperateEntity, BookGenerateResult> {

    /**
     * processor方法的 Service
     */
    @Autowired
    private IrsTransBookNewService irsTransBookNewService;

    @Override
    public BookGenerateResult process(NeedOperateEntity items) throws Exception {
        // 获取reader方法的返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult) items;
        
        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);

        BookGenerateResult result = new BookGenerateResultWithBokSummary();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
        // 若按照内部科目编码分录，则需要映射分录对应科目信息 只实现新引擎的分录，旧的逻辑依然调用jixu
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            //调用service方法进行逻辑逻辑
            result = irsTransBookNewService.execute(thisParams);
        }
        //当为null时候，给予初始化
        if(result == null){
            result = new BookGenerateResult();
        }

        result.setProdId(
            acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());

        return result;
    }
}

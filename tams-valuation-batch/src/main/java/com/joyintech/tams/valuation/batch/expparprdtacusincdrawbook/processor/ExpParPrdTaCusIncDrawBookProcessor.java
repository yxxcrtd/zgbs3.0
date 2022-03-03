/*
 * 文件名：ExpParPrdTaCusIncDrawBookProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：lihuadong
 * 修改时间：2018年9月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.expparprdtacusincdrawbook.processor;

import com.google.common.collect.Maps;
import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.expparprdtacusincdrawbook.service.ExpParPrdTaCusIncDrawBookNewService;
import com.joyintech.tams.valuation.batch.expparprdtacusincdrawbook.service.ExpParPrdTaCusIncDrawBookService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 预期收益型母产品TA客户收益计提入账 Processor
 * 
 * @author lihuadong
 * @version 1.0 2018年9月29日
 * @see ExpParPrdTaCusIncDrawBookProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ExpParPrdTaCusIncDrawBookProcessor extends BaseProcessor<NeedOperateEntity, BookGenerateResult> {

    /**
     * processor方法的 Service
     */
    @Autowired
    private ExpParPrdTaCusIncDrawBookService expectService;

    /**
     * processor方法的 Service
     */
    @Autowired
    private ExpParPrdTaCusIncDrawBookNewService expectNewService;
    /**
     * {@inheritDoc}
     */
    @Override
    public BookGenerateResult process(NeedOperateEntity item)
        throws Exception {

        StayDrawSubprdEntity acctEntity = (StayDrawSubprdEntity)item; // 获取 reader 方法的返回值

        Map<String, Object> params = Maps.newHashMap();
        params.putAll(this.getParams());
        params.put(GlobalConstant.READER_RESULT, acctEntity); // 将reader返回值塞入参数中

        // modify by weipeng on 20220113 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        BookGenerateResult result = new BookGenerateResult();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
        // 若按照内部科目编码分录，则需要映射分录对应科目信息
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            //调用service方法进行逻辑逻辑
            result = expectNewService.execute(params);
        }
        else{
            result = expectService.execute(params);
        }
        // modify by weipeng on 20220113 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end

        if (result == null) {// 当为null时候，给予初始化
            result = new BookGenerateResult();
        }

        result.setProdId(
            acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBooksetId()); // 标识

        return result;
    }

}

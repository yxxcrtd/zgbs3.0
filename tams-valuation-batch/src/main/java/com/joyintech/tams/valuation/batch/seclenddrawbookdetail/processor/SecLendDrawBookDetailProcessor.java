/*
 * 文件名：bondDebitCreditCalBookProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年10月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.seclenddrawbookdetail.processor;

import com.google.common.collect.Maps;
import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.seclenddrawbookdetail.service.SecLendDrawBookDetailNewService;
import com.joyintech.tams.valuation.batch.seclenddrawbookdetail.service.SecLendDrawBookDetailService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 债券借贷计提账务处理 processor
 * 
 * @author zhangwei
 * @version 1.0 2018年10月20日
 * @see bondDebitCreditCalBookProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecLendDrawBookDetailProcessor extends BaseProcessor<NeedOperateEntity, BookGenerateResult> {

    /**
     * processor方法的 Service
     */
    @Autowired
    private SecLendDrawBookDetailService secLendDrawBookDetailService;

    /**
     * processor方法的 Service
     */
    @Autowired
    private SecLendDrawBookDetailNewService secLendDrawBookDetailNewService;

    @Override
    public BookGenerateResultWithBokSummary process(NeedOperateEntity item)
        throws Exception {

        // 获取reader方法的返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)item;

        Map<String, Object> thisParams = Maps.newHashMap();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);

        // modify by weipeng on 20220321 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        BookGenerateResultWithBokSummary result = new BookGenerateResultWithBokSummary();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
        // 若按照内部科目编码分录，则需要映射分录对应科目信息
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            result = secLendDrawBookDetailNewService.execute(thisParams);
        }
        else{
            result = secLendDrawBookDetailService.execute(thisParams);
        }
        // modify by weipeng on 20220321 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end

        // 当为null时候，给予初始化
        if (result == null) {
            result = new BookGenerateResultWithBokSummary();
        }

        result.setProdId(
            acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());

        return result;
    }

}

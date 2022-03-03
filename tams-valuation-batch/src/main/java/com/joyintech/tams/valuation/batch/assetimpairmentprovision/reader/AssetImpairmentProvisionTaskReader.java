/*
 * 文件名：AssetIntdateCarryoverReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年5月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.assetimpairmentprovision.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.assetimpairmentprovision.dto.AssetImpairmentProvisionReaderResult;
import com.joyintech.tams.valuation.batch.assetimpairmentprovision.service.AssetImpairmentProvisionReaderService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产减值计提批处理 - 读入
 * 
 * @author chufengjie
 * @version 1.0 2020年10月13日
 * @see AssetImpairmentProvisionTaskReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class AssetImpairmentProvisionTaskReader extends BaseReader<AssetImpairmentProvisionReaderResult> {

    /**
     * reader方法的 Service
     */
    @Autowired
    private AssetImpairmentProvisionReaderService assetImpairmentProvisionReaderService;
    /**
     * reader方法的 Service
     */
    @Autowired
    private AssetImpairmentProvisionReaderService assetImpairmentProvisionReaderNewService;
    
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 读取数据 {@inheritDoc}
     */
    @Override
    public List<AssetImpairmentProvisionReaderResult> createList() {
    	
        Map<String, Object> serviceParamMap = new HashMap<String, Object>();
        serviceParamMap.put("BATCHINFO", this.getBatchExecuteInfo());
        serviceParamMap.put("BATCHDEL", this.getBatchExecuteDetail());
        serviceParamMap.put("BATCHTASK", this.getBatchTask());
        Map<String, String> dimValueMap = bookCommonLogic.getDimensionValue(this.getBatchExecuteInfo());
        serviceParamMap.put(BookConstant.BOOKSET_DATE, DateUtil.parse(dimValueMap.get(BookConstant.BOOKSET_DATE)));
        serviceParamMap.put(BookConstant.BOOK_DATE, DateUtil.parse(dimValueMap.get(BookConstant.BOOK_DATE)));
        serviceParamMap.put(BookConstant.HAPPEN_DATE, DateUtil.parse(dimValueMap.get(BookConstant.HAPPEN_DATE)));
        serviceParamMap.put("seqNo", this.getBatchExecuteDetail().getSeqNo());
        serviceParamMap.put("taskId", this.getBatchExecuteInfo().getTaskId());
        serviceParamMap.put(BookConstant.IS_SUB_BOOKSET, dimValueMap.get(BookConstant.IS_SUB_BOOKSET));
        if (this.getParams() != null) {
            this.getParams().putAll(serviceParamMap);
        }

        // 获取reader返回值
        // modify by weipeng on 20220113 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        List<AssetImpairmentProvisionReaderResult> resultList  = new ArrayList<AssetImpairmentProvisionReaderResult>();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
        // 若按照内部科目编码分录，则需要映射分录对应科目信息.
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            resultList = assetImpairmentProvisionReaderNewService.execute(serviceParamMap);
        }
        else{
            resultList = assetImpairmentProvisionReaderService.execute(serviceParamMap);
        }
        // modify by weipeng on 20220113 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end

        return resultList;
    }

}

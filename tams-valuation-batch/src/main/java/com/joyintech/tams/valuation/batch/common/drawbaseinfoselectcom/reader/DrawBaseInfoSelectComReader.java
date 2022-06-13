/*
 * 文件名：DrawBaseInfoSelectComReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.service.DrawBaseInfoSelectComReaderNewService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.service.DrawBaseInfoSelectComReaderService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计提批处理基本信息读入共通reader
 *
 * @author wudebao
 * @version 1.0 2018年5月24日
 * @see DrawBaseInfoSelectComReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DrawBaseInfoSelectComReader extends BaseReader<DrawBaseInfoSelectComResult> {

    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 存款类资产理财账读入Service
     */
    @Autowired
    private DrawBaseInfoSelectComReaderService drawBaseInfoSelectComReaderService;

    /**
     * 存款类资产理财账读入Service
     */
    @Autowired
    private DrawBaseInfoSelectComReaderNewService drawBaseInfoSelectComReaderNewService;

    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * {@inheritDoc}
     */
    @Override
    public List<DrawBaseInfoSelectComResult> createList() {

        // 变量定义
        List<DrawBaseInfoSelectComResult> listDrawBaseInfoSelectComResult = new ArrayList<DrawBaseInfoSelectComResult>();

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
        // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录
        String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
        // 获取reader返回值
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            listDrawBaseInfoSelectComResult = drawBaseInfoSelectComReaderNewService.execute(
                this.getParams());
        }
        else{
            listDrawBaseInfoSelectComResult = drawBaseInfoSelectComReaderService.execute(
                this.getParams());
        }

//        logger.error("【调查】计提Reader:[name = " + this.getBatchTask().getBeanName() + "| id="
//                    + this.getBatchTask().getTaskId() + "| pri = " + this.getBatchTask().getTaskPri()
//                    + "| count = " + listDrawBaseInfoSelectComResult.size() + "]");
//        
        // 返回处理结果
        return listDrawBaseInfoSelectComResult;

    }

}

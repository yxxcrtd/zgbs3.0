/*
 * 文件名：IrsBaseInfoProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.irs.processor;

import com.joyintech.tams.valuation.common.interf.base.dto.InterfNeedOperateEntity;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.interf.core.InterfBaseProcessor;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.irs.dto.IrsBaseInfoRecResult;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.irs.service.IrsBaseInfoService;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.lendstandard.dto.LendStandardRecResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 利率互换基础信息 procesor
 * @author 殷亚
 * @date 2022/4/15 9:13
 * @return
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsBaseInfoProcessor extends InterfBaseProcessor<InterfNeedOperateEntity, IrsBaseInfoRecResult> {
    @Autowired
    private IrsBaseInfoService irsBaseInfoService;

    @Override
    public IrsBaseInfoRecResult process(InterfNeedOperateEntity item)
        throws Exception {

        // 获取reader方法的返回值(processor并发数据结构)
        TemplateOrderInfoReaderResult acctEntity = (TemplateOrderInfoReaderResult)item;
        // 调用利率互换service
        IrsBaseInfoRecResult result = irsBaseInfoService.execute(
            acctEntity);
        return result;
    }
}

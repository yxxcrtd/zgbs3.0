/*
 * 文件名：IrsDealRecProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.processor;

import com.joyintech.tams.valuation.common.interf.base.dto.InterfNeedOperateEntity;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.interf.core.InterfBaseProcessor;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.dto.IrsDealRecProcessorResult;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.service.IrsDealRecComForInterfService;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.lend.dto.LendDealRecProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 利率互换交易  process
 * @author 殷亚
 * @date 2022/4/21 10:16
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsDealRecProcessor extends InterfBaseProcessor<InterfNeedOperateEntity, IrsDealRecProcessorResult> {
    /**
     * IrsDealRecComForInterfService
     */
    @Autowired
    private IrsDealRecComForInterfService irsDealRecComForInterfService;


    /**
     * {@inheritDoc}
     */
    @Override
    public IrsDealRecProcessorResult process(InterfNeedOperateEntity item)
        throws Exception {
        // 获取reader方法的返回值(processor并发数据结构)
        TemplateOrderInfoReaderResult acctEntity = (TemplateOrderInfoReaderResult)item;
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get("executeInfo");
        acctEntity.setResovleDate(interfExecuteInfo.getInterfDate());
        acctEntity.setParams(this.getParams());
        // 调用净值型基础信息处理公用service
        IrsDealRecProcessorResult result = irsDealRecComForInterfService.execute(
            acctEntity);
        return result;
    }

}

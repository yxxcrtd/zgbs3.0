/*
 * 文件名：IrsDealRunner.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.runner;


import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfNeedOperateEntity;
import com.joyintech.tams.valuation.interf.core.*;
import com.joyintech.tams.valuation.interf.core.exception.InterfException;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.processor.IrsDealRecProcessor;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.writer.IrsDealRecWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.common.reader.AssetMangBaseInfoReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 利率互换交易runner
 * @author 殷亚
 * @date 2022/4/21 10:52
 * @see
 * @version 1.0
 */
@Service("irsDealRunner")
@Scope("prototype")
public class IrsDealRunner extends InterfBaseService {

    /**
     * step1Reader
     */
    @Autowired
    private AssetMangBaseInfoReader step1Reader;

    /**
     * step1Processor
     */
    @Autowired
    private IrsDealRecProcessor step1Processor;

    /**
     * step1Writer
     */
    @Autowired
    private IrsDealRecWriter step1Writer;

    /**
     * interfBuilder
     */
    @Autowired
    private InterfBuilder<InterfBaseReader<InterfNeedOperateEntity>, InterfBaseProcessor<InterfNeedOperateEntity, BaseEntity>, InterfBaseWriter<BaseEntity>> interfBuilder;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void execute() throws InterfException {
        logger.info(getInterfExecuteInfo().getTaskName() + "开始！");
        //参数列表增加"executeInfo"参数
        this.getParams().put("executeInfo", this.getInterfExecuteInfo());

        try {
            interfBuilder.init(this).batch("step1", (InterfBaseReader)step1Reader,
                (InterfBaseProcessor)step1Processor, (InterfBaseWriter)step1Writer).builder(
                    "lendDealRecRunner").run();
        }
        catch (InterfException e) {
            logger.error("", e);
            logger.error(getInterfExecuteInfo().getTaskName() + "异常结束！");
            throw e;
        }
        logger.info(getInterfExecuteInfo().getTaskName() + "结束！");
    }

}

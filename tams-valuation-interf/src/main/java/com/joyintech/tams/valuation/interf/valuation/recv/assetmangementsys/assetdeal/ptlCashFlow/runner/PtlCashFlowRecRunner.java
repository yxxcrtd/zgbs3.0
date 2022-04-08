/*
 * 文件名：PtlCashFlowRecRunner.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：tanxuan
 * 修改时间：2020年4月2日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.runner;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfNeedOperateEntity;
import com.joyintech.tams.valuation.interf.core.*;
import com.joyintech.tams.valuation.interf.core.exception.InterfException;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.processor.PtlCashFlowRecProcessor;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.writer.PtlCashFlowRecWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.common.reader.AssetMangBaseInfoReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 自定义现金流交易信息接口Runner
 * 
 * @author tanxuan
 * @version 1.0 2020年4月2日
 * @see PtlCashFlowRecRunner
 * @since 1.0
 */
@Service("ptlCashFlowRecRunner")
@Scope("prototype")
public class PtlCashFlowRecRunner extends InterfBaseService {

    @Autowired
    private AssetMangBaseInfoReader step1Reader;

    @Autowired
    private PtlCashFlowRecProcessor step1Processor;

    @Autowired
    private PtlCashFlowRecWriter step1Writer;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * interfBuilder
     */
    @Autowired
    private InterfBuilder<InterfBaseReader<InterfNeedOperateEntity>, InterfBaseProcessor<InterfNeedOperateEntity, BaseEntity>, InterfBaseWriter<BaseEntity>> interfBuilder;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void execute() throws InterfException {
        logger.info(getInterfExecuteInfo().getTaskName() + "开始！");
        //参数列表增加"executeInfo"参数
        this.getParams().put("executeInfo", this.getInterfExecuteInfo());

        try {
            interfBuilder.init(this).batch("step1", (InterfBaseReader)step1Reader,
                (InterfBaseProcessor)step1Processor, (InterfBaseWriter)step1Writer).builder(
                    "ptlCashFlowRecRunner").run();
        }
        catch (InterfException e) {
            logger.error("", e);
            logger.error(getInterfExecuteInfo().getTaskName() + "异常结束！");
            throw e;
        }
        logger.info(getInterfExecuteInfo().getTaskName() + "结束！");
    }

}

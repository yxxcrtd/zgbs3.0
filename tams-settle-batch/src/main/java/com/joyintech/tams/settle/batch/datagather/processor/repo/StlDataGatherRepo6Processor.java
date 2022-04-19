package com.joyintech.tams.settle.batch.datagather.processor.repo;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


/**
 * 协议回购逆回购到期
 * @author fuyongsheng
 * @date 2022/4/9
 * @see StlDataGatherRepo6Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherRepo6Processor extends StlDataGatherRepoDurationProcessor {
    // 生成交易逻辑与竞价回购存续期逻辑一致
}

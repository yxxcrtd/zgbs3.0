package com.joyintech.tams.settle.batch.datagather.processor.repo;

import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;
import com.joyintech.tams.settle.batch.datagather.dto.TrdProductDealDto;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 债券协议回购逆回购到期
 * @author fuyongsheng
 * @date 2022/4/9
 * @see StlDataGatherRepo6Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherRepo6Processor extends StlDataGatherRepoDurationProcessor {
    // 生成交易逻辑与债券竞价回购存续期逻辑一致
}

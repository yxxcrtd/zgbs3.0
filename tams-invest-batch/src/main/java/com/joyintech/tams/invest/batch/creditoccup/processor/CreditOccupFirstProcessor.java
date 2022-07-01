package com.joyintech.tams.invest.batch.creditoccup.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.batch.creditoccup.dto.CreditOccupReaderResult;
import com.joyintech.tams.invest.batch.creditoccup.dto.CreditOccupResult;
import com.joyintech.tams.invest.batch.creditoccup.dto.CreditUsingDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zznn
 * @date 2021/8/10
 */
@Service
@Scope("prototype")
public class CreditOccupFirstProcessor extends BaseProcessor<NeedOperateEntity, CreditOccupResult> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public CreditOccupResult process(NeedOperateEntity needOperateEntity) throws Exception {
        CreditOccupReaderResult readerResult = (CreditOccupReaderResult) needOperateEntity;
        List<CreditLinesInputDto> list = readerResult.getCreditLinesInputs();
        if (CollectionUtils.isNotEmpty(list)) {
            try {
                AllTradeCommonUtil.handleBeforeTradeComplete(list, DictCodeListRsk.授信占用类别_实占.getValue());
            } catch (Exception e) {
                logger.error("授信日终实占批处理异常:" + e.getMessage());
            }
        }
        return null;
    }


}


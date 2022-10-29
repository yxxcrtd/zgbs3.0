package com.joyintech.tams.invest.trade.exchangetransmatch.common.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalMatch;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonParamDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.service.ExchangeTransMatchCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/invest/trade/exchangetransmatch/ExchangeTransMatchCommon")
public class ExchangeTransMatchCommonController {
    @Autowired
    private ExchangeTransMatchCommonService exchangeTransMatchCommonService;

    /**
     * 批量处理
     * @param paramDto
     */
    @ResponseBody
    @PostMapping("transactionMacthBatch")
    @Transactional
    @DTXTransaction
    public void transactionMacthBatch(@RequestBody ExchangeTransMatchCommonParamDto paramDto){
        if (paramDto != null && !StringUtil.isEmptyList(paramDto.getNeedUpdateStatusEntrustIdList())) {
            exchangeTransMatchCommonService.updateEntrustStatus(paramDto.getNeedUpdateStatusEntrustIdList());
        }

        if (paramDto == null || StringUtil.isEmptyList(paramDto.getParamDataList())) {
            return;
        }
        exchangeTransMatchCommonService.transactionMacthBatch(paramDto.getParamDataList());
    }

    /**
     * 批量处理
     * @param paramDto
     */
    @ResponseBody
    @PostMapping("transactionRevokeAutoBatch")
    public void transactionRevokeAutoBatch(@RequestBody ExchangeTransMatchCommonParamDto paramDto){
        if (paramDto == null || StringUtil.isEmptyList(paramDto.getRevokeDataList())) {
            return;
        }
        exchangeTransMatchCommonService.revokeTransactionAutoMacthBatch(paramDto.getRevokeDataList());
    }
}

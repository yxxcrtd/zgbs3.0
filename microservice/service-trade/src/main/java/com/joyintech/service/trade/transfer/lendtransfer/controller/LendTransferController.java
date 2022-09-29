package com.joyintech.service.trade.transfer.lendtransfer.controller;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.transfer.common.service.TransferCommonService;
import com.joyintech.service.trade.transfer.lendtransfer.dto.LendTransferDto;
import com.joyintech.service.trade.transfer.lendtransfer.service.LendTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 拆借资产转仓
 * @author liuwei
 * @version 1.0 2020年02月18日
 * @see LendTransferController
 * @since 1.0
 */
@RestController("LendTransferControllerCtr_dtx")
@RequestMapping("/LendTransfer")
public class LendTransferController {

    /**
     *  拆借资产转仓Service
     */
    @Autowired
    private LendTransferService lendTransferService;
    @Autowired
    private TransferCommonService transferCommonService;

    /**
     * 获取转仓基本信息
     * @return 交易信息
     * @see
     * @since 1.0
     */
    @GetMapping("getTradeInfo")
    public LendTransferDto getTradeInfo(String tradeId){
        return lendTransferService.getTradeInfo(tradeId);
    }

    /**
     * 获取转仓信息—组合代码
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    @PostMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(@RequestBody Map<String,Object> map){
//        return lendTransferService.getPortfolioId(map);
        return transferCommonService.selectPortoId(map);
    }
    
    /**
     * 获取处于流程中的持仓（但是未记录在在持仓流水中）
     * @return 处于流程中的持仓（但是未记录在在持仓流水中）
     * @see
     * @since 1.0
     */
    @PostMapping("selectWflAmtLend")
    public BigDecimal selectWflAmtLend(@RequestBody Map<String,Object> map){
        return lendTransferService.selectWflAmtLend(map);
    }
}

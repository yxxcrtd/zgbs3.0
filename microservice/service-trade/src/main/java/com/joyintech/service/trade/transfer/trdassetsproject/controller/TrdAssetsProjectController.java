package com.joyintech.service.trade.transfer.trdassetsproject.controller;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.transfer.common.service.TransferCommonService;
import com.joyintech.service.trade.transfer.trdassetsproject.service.TrdAssetsProjectService;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 项目类资产转仓
 */
@RestController("backTrdAssetsProjectController_dtx")
@RequestMapping("/trade/transfer/trdassetsproject/TrdAssetsProject")
public class TrdAssetsProjectController {
    /**
     * trdassetsProjectService
     */
    @Autowired
    TrdAssetsProjectService trdassetsProjectService;
    @Autowired
    private TransferCommonService transferCommonService;

    /**
     * 查询已拆分信息
     * @return 已拆分信息
     */
    @RequestMapping("selectSplitInfo")
    public com.joyintech.service.trade.transfer.trdassetsproject.dto.ProjSplitInfoDto selectSplitInfo(@RequestBody Map<String,Object>param){
        return trdassetsProjectService.selectSplitInfo(param);
    }
    /**
     * 查询组合
     */
    @RequestMapping("selectPortfolioId")
    public List<SelectOptionDto> selectPortfolioId(@RequestBody Map<String,Object>param){
//        return trdassetsProjectService.selectPortfolioId(param);
        return transferCommonService.selectPortoId(param);
    }

    /**
     * 查询划款指令
     */
    @RequestMapping("selectBussTrdTraOrderByTradeId")
    public List<BussTrdTransferOrderDto> selectBussTrdTraOrderByTradeId(@RequestBody Map<String,Object>param){
        return trdassetsProjectService.selectBussTrdTraOrderByTradeId(param);
    }
    /**
     * 获取处于流程中的持仓（但是未记录在在持仓流水中）
     * @return 处于流程中的持仓（但是未记录在在持仓流水中）
     * @see
     * @since 1.0
     */
    @PostMapping("selectWflAmtAsset")
    public BigDecimal selectWflAmtAsset(@RequestBody Map<String,Object> map){
        return trdassetsProjectService.selectWflAmtAsset(map);
    }
}

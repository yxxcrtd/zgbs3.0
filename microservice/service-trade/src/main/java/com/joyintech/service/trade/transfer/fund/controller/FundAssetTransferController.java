package com.joyintech.service.trade.transfer.fund.controller;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.transfer.common.dto.BondValuationDto;
import com.joyintech.service.trade.transfer.common.service.TransferCommonService;
import com.joyintech.service.trade.transfer.fund.dto.FundAssetTransfer001Dto;
import com.joyintech.service.trade.transfer.fund.service.FundAssetTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 基金类资产交易转仓
 * @author liupengfei
 * @version 1.0 2019年11月13日
 * @see FundAssetTransferController
 * @since 1.0
 */
@RestController("FundAssetTransfer_dtx")
@RequestMapping("/trade/transfer/fund/FundAssetTransfer")
public class FundAssetTransferController {
    /**
     * FundAssetTransferService
     */
    @Autowired
    private FundAssetTransferService fundAssetTransferService;
    @Autowired
    private TransferCommonService transferCommonService;

    /**
     * 查询组合信息
     * @return list
     */
    @RequestMapping("selectPortfolioId")
    public List<SelectOptionDto> selectPortfolioId(@RequestBody Map<String,Object>param){
//        return fundAssetTransferService.selectPortfolioId(param);
        return transferCommonService.selectPortoId(param);
    }
    /**
     * 非货币基金单位净值初始化值
     * @param param 参数
     * @return 非货币基金单位净值初始化值
     */
    @RequestMapping("findInitCarryOverQty")
    public BigDecimal findInitCarryOverQty(@RequestBody Map<String,Object>param){
        return fundAssetTransferService.findInitCarryOverQty(param);
    }

    /**
     * 每日估值
     * @param param 参数
     * @return 每日估值
     */
    @RequestMapping("findValuation")
    public BondValuationDto findValuation(@RequestBody Map<String,Object>param){
        return fundAssetTransferService.findValuation(param);
    }

    /**
     * 查询单位净价
     * @param param 参数
     * @return 单位净价list
     */
    @RequestMapping("findUnitCprice")
    public List<BigDecimal> findUnitCprice(@RequestBody Map<String,Object>param){
        return fundAssetTransferService.findUnitCprice(param);
    }
    /**
     * 查询产品登记编码
     * @param param 参数
     * @return 产品登记编码
     */
    @RequestMapping("selProdCode")
    public String selProdCode(@RequestBody Map<String,Object>param){
        return fundAssetTransferService.selProdCode(param);
    }
    /**
     * 查询收盘价
     * @param param 参数
     * @return 收盘价
     */
    @RequestMapping("selCloPrice")
    public BigDecimal selCloPrice(@RequestBody Map<String,Object>param){
        return fundAssetTransferService.selCloPrice(param);
    }
    /**
     * 更新资产负债报送组件
     * @param param 参数
     * @return int
     */
    @RequestMapping("updateRep")
    public int updateRep(@RequestBody Map<String,Object>param){
        return fundAssetTransferService.updateRep(param);
    }
}

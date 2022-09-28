package com.joyintech.service.trade.transfer.netplantransfer.controller;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.transfer.common.service.TransferCommonService;
import com.joyintech.service.trade.transfer.netplantransfer.service.NetPlanTransferService;
import com.joyintech.tams.common.base.entity.TrdProductDeal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 净值型计划转仓
 * @author liuwei
 * @version 1.0 2019年12月23日
 * @see NetPlanTransferController
 * @since 1.0
 */
@RestController("NetPlanTransfer001Ctr_dtx")
@RequestMapping("/NetPlanTransfer001")
public class NetPlanTransferController {

    /**
     *  净值型计划转仓Service
     */
    @Autowired
    private NetPlanTransferService netPlanTransferService;
    @Autowired
    private TransferCommonService transferCommonService;

    /**
     * 获取单位净值
     * @param map 请求参数
     * @return 单位净值
     * @see
     * @since 1.0
     */
    @PostMapping("getUnitValue")
    public BigDecimal getUnitValue(@RequestBody Map<String,Object> map){
        return netPlanTransferService.getUnitValue(map);
    }

    /**
     * 获取转仓信息—组合代码
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    @PostMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(@RequestBody Map<String,Object> map){
//        return netPlanTransferService.getPortfolioId(map);
        return transferCommonService.selectPortoId(map);
    }

    /**
     * 查询产品登记编码
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("selProdCode")
    public String selProdCode(@RequestBody Map<String,Object> map){
        return netPlanTransferService.selProdCode(map);
    }

    /**
     * 查询收盘价
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("selCloPrice")
    public BigDecimal selCloPrice(@RequestBody Map<String,Object> map){
        return netPlanTransferService.selCloPrice(map);
    }

    /**
     * 更新交易报送表
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("updateRep")
    public Integer updateRep(@RequestBody Map<String,Object> map){
        return netPlanTransferService.updateRep(map);
    }

    /**
     * 获取资产类型7
     * @return
     * @see
     * @since 1.0
     */
    @GetMapping("selectType7")
    public String selectType7(String finprodId){
        return netPlanTransferService.selectType7(finprodId);
    }

    /**
     * 获取单位净值列表
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("getUnitValueList")
    public List<TrdProductDeal> getUnitValueList(@RequestBody Map<String,Object> map){
    	return netPlanTransferService.getUnitValueList(map);
    }

}

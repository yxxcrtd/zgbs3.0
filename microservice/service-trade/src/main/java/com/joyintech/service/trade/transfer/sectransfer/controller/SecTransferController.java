package com.joyintech.service.trade.transfer.sectransfer.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.transfer.common.dto.BondValuationDto;
import com.joyintech.service.trade.transfer.common.service.TransferCommonService;
import com.joyintech.service.trade.transfer.sectransfer.dto.SecTransfer001Dto;
import com.joyintech.service.trade.transfer.sectransfer.service.SecTransferService;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
*
* 债券交易转仓
* @author 
* @version 1.0 2019年12月30日
* @see SecTransferController
* @since 1.0
*/
@RestController("backSecTransferController_dtx")
@RequestMapping("/SecTransfer")
public class SecTransferController {

    /**
     * 注入债券交易转仓Service
     */
    @Autowired
    private SecTransferService secResoluteService;
    @Autowired
    private TransferCommonService transferCommonService;

    /**
     * 获取联想下拉框实体类
     * @param map 请求参数
     * @return 联想下拉框实体类
     * @see
     * @since 1.0
     */
    @PostMapping("getFinprodMarketId")
    public List<SelectOptionDto> getFinprodMarketId(@RequestBody Map<String,Object> map){
        return secResoluteService.getFinprodMarketId(map);
    }

    /**
     * 资产代码带出的信息
     * @param map 资产代码
     * @return 资产类投资交易实体类
     * @see
     * @since 1.0
     */
    @PostMapping("selectTradeInfo")
    public SecTransfer001Dto selectTradeInfo(@RequestBody Map<String,Object> map){
        return secResoluteService.selectTradeInfo(map);
    }

    /**
     * 查询已拆分金额
     *
     * @return 拆分前交易编号
     * @see
     * @since 1.0
     */
    @GetMapping("getDistAmt")
    public BigDecimal getDistAmt(String tradeId){
        return secResoluteService.getDistAmt(tradeId);
    }

    /**
     * 获取拆分信息—组合代码
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    @PostMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(@RequestBody Map<String,Object> map){
//        return secResoluteService.getPortfolioId(map);
        return transferCommonService.selectPortoId(map);
    }

    /**
     * 插入数据，并删除流程表数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doComplete")
    public String doComplete(@RequestBody SecTransfer001Dto secResolute001Dto) {
        return secResoluteService.doComplete(secResolute001Dto);
    }

    /**
     * 查询组合托管户
     * @return 组合托管户
     * @see
     * @since 1.0
     */
    @PostMapping("getAccountInfo")
    public String getAccountInfo(@RequestBody Map<String,Object> map){
        return secResoluteService.getAccountInfo(map);
    }

    /**
     * 查询组合券商资金账户
     * @return 查询组合券商资金账户
     * @see
     * @since 1.0
     */
    @PostMapping("getCounterAcctInfo")
    public String getCounterAcctInfo(@RequestBody Map<String,Object> map){
        return secResoluteService.getCounterAcctInfo(map);
    }

    /**
     * 查询托管划款指令
     * <功能详细描述>
     * @param map 查询条件
     * @see
     * @since 1.0
     */
    @GetMapping("selectStlPayOrder")
    public StlPayOrders selectStlPayOrder(String tradeId){
        return secResoluteService.selectStlPayOrder(tradeId);
    }

    // add by xuzhihao start
    @PostMapping("findUnitCprice")
    public BigDecimal findUnitCprice(@RequestBody Map<String, Object> param) {
        return secResoluteService.findUnitCprice(param);
    }
    // add by xuzhihao end

    /**
     * @return 查询组合名称
     * @see
     * @since 1.0
     */
    @PostMapping("selectPortfolioName")
    public String selectPortfolioName(@RequestBody String portfolioId){
        return secResoluteService.selectPortfolioName(portfolioId);
    }

    @PostMapping("selectData")
    public int selectData(@RequestBody Map<String,Object> map){
        return secResoluteService.selectData(map);
    }

    @PostMapping("selectYwmsData")
    public int selectYwmsData(@RequestBody Map<String,Object> map){
        return secResoluteService.selectYwmsData(map);
    }

    @PostMapping("selectCpriceData")
    public int selectCpriceData(@RequestBody Map<String,Object> map){
        return secResoluteService.selectCpriceData(map);
    }

    /**
     * 估值净价
     * @param param 参数
     * @return 估值净价
     */
    @PostMapping("findValuation")
    public BondValuationDto findValuation(@RequestBody Map<String,Object>param){
        return secResoluteService.findValuation(param);
    }
}

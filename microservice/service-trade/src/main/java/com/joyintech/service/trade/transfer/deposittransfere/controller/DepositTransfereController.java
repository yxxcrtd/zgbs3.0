package com.joyintech.service.trade.transfer.deposittransfere.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.transfer.common.service.TransferCommonService;
import com.joyintech.service.trade.transfer.deposittransfere.dto.DepositTransfereService001Dto;
import com.joyintech.service.trade.transfer.deposittransfere.dto.DepositTransfereService002Dto;
import com.joyintech.service.trade.transfer.deposittransfere.service.DepositTransfereService;
import com.joyintech.tams.common.base.entity.RepTrade;
import com.joyintech.tams.component.base.report.trade.BaseRepTradeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 存款类资产交易转仓
 *
 * @author hepengli
 * @version 1.0 2019年12月16日
 * @see DepositTransfereController
 * @since 1.0
 */
@RestController("DepositTransfere001Ctr_dtx")
@RequestMapping("/DepositTransfere001")
public class DepositTransfereController {

    @Autowired
    private DepositTransfereService depositTransfereService;

    /**
     * 交易报送信息组件
     */
    @Autowired
    private BaseRepTradeInfoService baseRepTradeInfoService;

    @Autowired
    private TransferCommonService transferCommonService;

    /**
     * 组合代码下拉框
     *
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception Exception
     */
    @RequestMapping("queryProdIdInfo")
    @ResponseBody
    public List<SelectOptionDto> queryProdIdInfo(@RequestBody Map<String, Object> param) {
//        return depositTransfereService.selectPortoId(param);
        return transferCommonService.selectPortoId(param);
    }

    @RequestMapping("queryAssetInt")
    @ResponseBody
    public DepositTransfereService001Dto queryAssetInt(@RequestBody Map<String, Object> param) {
        return depositTransfereService.queryAssetInt(param);
    }

    /**
     * 获取登记编号
     *
     * @param map 查询参数
     * @return 登记编号
     * @see
     * @since 1.0
     */
    @PostMapping("getProdRegistCode")
    public String getProdRegistCode(@RequestBody Map<String, Object> map) {
        return depositTransfereService.getProdRegistCode(map);
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
    public String doComplete(@RequestBody DepositTransfereService002Dto depositTransfereService001Dto) {
        return depositTransfereService.doComplete(depositTransfereService001Dto);
    }

    /**
     * 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
     *
     * @param repTradeList 交易报送信息Entity列表
     * @return 成功/失败
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insertRepTrade")
    public boolean insertRepTrade(@RequestBody List<RepTrade> repTradeList) {
        return baseRepTradeInfoService.insert(repTradeList);
    }

    /**
     * 获取交易编号list
     *
     * @param map 日期、币种
     * @return 汇率（收盘价）
     * @see
     * @since 1.0
     */
    @PostMapping("getWorkflowTradeId")
    public int getWorkflowTradeId(@RequestBody Map<String, Object> map) {
        return depositTransfereService.getWorkflowTradeId(map);
    }
}

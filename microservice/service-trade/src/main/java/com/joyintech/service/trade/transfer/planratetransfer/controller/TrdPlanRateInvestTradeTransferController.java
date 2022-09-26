package com.joyintech.service.trade.transfer.planratetransfer.controller;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.transfer.common.service.TransferCommonService;
import com.joyintech.service.trade.transfer.planratetransfer.dto.PlanTransferDto;
import com.joyintech.service.trade.transfer.planratetransfer.dto.PlanTransferDtos;
import com.joyintech.service.trade.transfer.planratetransfer.service.TrdPlanRateInvestTradeTransferService;
import com.joyintech.tams.common.base.entity.RepTrade;
import com.joyintech.tams.component.base.report.trade.BaseRepTradeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @package com.joyintech.service.report.hxbank.resolute.planratetransfer.controller
 * @author: Xu Zhihao
 * @date: 2019/12/18 10:13
 * @version: V1.0
 * @description:
 */
@RestController("backTrdPlanRateInvestTradeTransferController_dtx")
@RequestMapping("/TrdPlanRateInvestTradeTransfer")
public class TrdPlanRateInvestTradeTransferController extends BaseController {

    @Autowired
    private TrdPlanRateInvestTradeTransferService trdPlanRateInvestTradeTransferService;

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
//        return trdPlanRateInvestTradeTransferService.selectPortoId(param);
        return transferCommonService.selectPortoId(param);
    }

    @RequestMapping("queryAssetInt")
    @ResponseBody
    public PlanTransferDto queryAssetInt(@RequestBody Map<String, Object> param) {
        return trdPlanRateInvestTradeTransferService.queryAssetInt(param);
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
        return trdPlanRateInvestTradeTransferService.getProdRegistCode(map);
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
    public String doComplete(@RequestBody PlanTransferDtos planTransferDtos) {
        return trdPlanRateInvestTradeTransferService.doComplete(planTransferDtos);
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
}

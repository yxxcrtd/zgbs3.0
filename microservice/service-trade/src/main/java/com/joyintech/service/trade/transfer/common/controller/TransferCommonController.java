package com.joyintech.service.trade.transfer.common.controller;

import com.joyintech.framework.base.BaseController;
import com.joyintech.service.trade.transfer.common.dto.PortfolioValuationDto;
import com.joyintech.service.trade.transfer.common.dto.TransferCommonDto;
import com.joyintech.service.trade.transfer.common.service.TransferCommonService;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlPortfolioValuationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 转仓交易共同
 *
 * @author liupengfei
 * @version 2019/12/23
 */
@RestController("backTransferCommonController_dtx")
@RequestMapping("/trade/transfer/common/TransferCommon")
public class TransferCommonController extends BaseController {
    @Autowired
    private TransferCommonService transferCommonService;

    /**
     * 根据工作流代码查询流程中的交易信息
     *
     * @param workflowId 参数
     * @return 交易信息list
     */
    @RequestMapping("selectDealByworkflowId")
    public List<WflTrdProductDeal> selectDealByworkflowId(@RequestBody String workflowId) {
        return transferCommonService.selectDealByworkflowId(workflowId);
    }

    /**
     * 查询待转仓组合信息
     *
     * @param paramMap 参数
     * @return 组合信息list
     */
    @RequestMapping("findAvaPortfolioInfo")
    public List<TransferCommonDto> findAvaPortfolioInfo(@RequestBody Map<String, Object> paramMap) {
        return transferCommonService.findAvaPortfolioInfo(paramMap);
    }

    /**
     * 查询组合估值信息
     *
     * @param paramMap 参数
     * @return 组合估值信息
     */
    @RequestMapping("getPtlPortfolioValuation")
    public List<PortfolioValuationDto> getPtlPortfolioValuation(@RequestBody Map<String, Object> paramMap) {
        return transferCommonService.getPtlPortfolioValuation(paramMap);
    }
}

package com.joyintech.tams.valuation.trademanage.irs.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.irs.dto.TrdIRSValuationTrade002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TrdIRSValuationTrade002Service extends CommonExportService<TrdIRSValuationTrade002Dto> implements BaseProcessService<TrdIRSValuationTrade002Dto> {

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdIRSValuationTrade002Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/queryTradeInfoByTradeId", tradeId,
                TrdIRSValuationTrade002Dto.class);
    }

    /**
     * 资金账户下拉框
     *
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getTrdAcctId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/getTrdAcctId" , map,
                SelectOptionDto.class);
    }

    //自增序列号
    public int querySequences(){
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondLendTrade002/querySequences", null,Integer.class);
    }


    @Override
    public void doComplete(TrdIRSValuationTrade002Dto trdIRSValuationTrade002Dto, OperationType operationType) {
//        TrdBondLendTradeResult trdBondLendTradeResult = generateEntity(TrdBondLendTrade002Dto);
//        String action = StringUtil.EMPTY;
//         if (OperationType.NEW.equals(operationType)) {
//             action = "insert";
//        }
//        else {
//            action = "update";
//        }
//        restTemplate.postForObject(
//                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/" + action, trdBondLendTradeResult,
//                Integer.class);

    }
}

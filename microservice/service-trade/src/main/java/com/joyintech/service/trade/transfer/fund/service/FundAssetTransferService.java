package com.joyintech.service.trade.transfer.fund.service;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.trade.transfer.common.dto.BondValuationDto;
import com.joyintech.service.trade.transfer.fund.dao.FundAssetTransferMapper;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 基金类交易转仓Service
 *
 * @author liupengfei
 * @version 1.0 2019年11月13日
 * @see FundAssetTransferService
 * @since 1.0
 */
@Service("backFundAssetTransferService")
public class FundAssetTransferService {
    @Autowired
    private FundAssetTransferMapper fundAssetTransferMapper;

    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    public List<SelectOptionDto> selectPortfolioId(Map<String, Object> param) {
        param.put("capacctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        List<SelectOptionDto> result = fundAssetTransferMapper.selectPortfolioId(param);
        //过滤现金余额
        if (basePtlCashPositionInfoService != null) {
            Iterator<SelectOptionDto> iterator = result.iterator();
            while (iterator.hasNext()) {
                SelectOptionDto dto = iterator.next();
                PtlCashPositionFlowDto pcpdto = new PtlCashPositionFlowDto();
                Date sysdate = DateUtil.parse((String) param.get("tradeDate"));
                pcpdto.setCdate(sysdate);
                List<String> acctTypeList = new ArrayList<>();
                acctTypeList.add(BussinessDictCodeList.资金账户类型_托管账户.getValue());
                acctTypeList.add(BussinessDictCodeList.资金账户类型_交易所资金账户.getValue());
                pcpdto.setAcctType(acctTypeList);
                pcpdto.setPortfolioId(dto.getId());
                pcpdto.setCcy((String) param.get("ccy"));
                BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(pcpdto);
                if (cashT.compareTo(new BigDecimal(0)) <= 0) {
                    iterator.remove();
                }
            }
        }
        return result;
    }

    public BigDecimal findInitCarryOverQty(Map<String, Object> param) {
        return fundAssetTransferMapper.findInitCarryOverQty(param);
    }

    public BondValuationDto findValuation(Map<String, Object> param) {
        List<BondValuationDto> dtoList = fundAssetTransferMapper.findValuation(param);
        return CollectionUtils.isNotEmpty(dtoList) ? dtoList.get(0) : null;
    }

    public List<BigDecimal> findUnitCprice(Map<String, Object> param) {
        return fundAssetTransferMapper.findUnitCprice(param);
    }

    public String selProdCode(Map<String, Object> param) {
        return fundAssetTransferMapper.selProdCode(param);
    }

    public BigDecimal selCloPrice(Map<String, Object> param) {
        return fundAssetTransferMapper.selCloPrice(param);
    }

    public int updateRep(Map<String, Object> param) {
        return fundAssetTransferMapper.updateRep(param);
    }
}

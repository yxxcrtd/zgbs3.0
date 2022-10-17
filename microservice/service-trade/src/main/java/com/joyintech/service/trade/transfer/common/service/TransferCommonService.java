package com.joyintech.service.trade.transfer.common.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.common.service.TrdAvailableCashQueryService;
import com.joyintech.service.trade.transfer.common.dao.TransferCommonMapper;
import com.joyintech.service.trade.transfer.common.dto.PortfolioValuationDto;
import com.joyintech.service.trade.transfer.common.dto.TransferCommonDto;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecDto;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecRequest;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt1.交易场所_上海证券交易所;
import static com.joyintech.tams.constant.DictCodeListExt1.交易场所_深圳证券交易所;

@Service("backTransferCommonService")
public class TransferCommonService extends BaseService {

    @Autowired
    private TransferCommonMapper transferCommonMapper;
    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
    @Autowired
    private TrdAvailableCashQueryService trdAvailableCashQueryService;
    @Autowired(required = false)
    private DateCalService dateCalService;
    @Autowired
    private EtrTradeCommonervice etrTradeCommonervice;

    public List<WflTrdProductDeal> selectDealByworkflowId(String workflowId) {
        return transferCommonMapper.selectDealByworkflowId(workflowId);
    }

    public List<SelectOptionDto> selectPortoId(Map<String, Object> param) {
        List<SelectOptionDto> result = Lists.newArrayList();
        List<TransferCommonDto> dtoList = findAvaPortfolioInfo(param);

        BigDecimal carryOverQty = transferCommonMapper.findCarryOverQty(param);
        SelectOptionDto selectOptionDto;
        for (TransferCommonDto dto : dtoList) {
            if (Objects.isNull(dto) || dto.getAvailableCash().compareTo(new BigDecimal(0)) <= 0) {
                continue;
            }
            dto.setCarryOverQty(carryOverQty);
            selectOptionDto = new SelectOptionDto();
            selectOptionDto.setId(dto.getPortfolioId());
            selectOptionDto.setText(dto.getPortfolioId() + ":" + dto.getPortfolioName());
            selectOptionDto.setObject(dto);

            result.add(selectOptionDto);
        }

        return result;
    }

    public List<TransferCommonDto> findAvaPortfolioInfo(Map<String, Object> param) {
        List<TransferCommonDto> result = new ArrayList<>();
        param.put("capacctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        param.put("userId", LoginUserUtil.getUserName());
        if (StringUtil.isNotEmpty((String) param.get("secManageAcctId"))) {
            param.put("secAcctId", (String) param.get("secManageAcctId"));
        }
        if (StringUtil.isNotEmpty((String) param.get("investPortfolioId"))) {
            param.put("portfolioId", (String) param.get("investPortfolioId"));
        }
        if (StringUtil.isNotEmpty((String) param.get("invTransPortf"))) {
            param.put("portfolioId", (String) param.get("invTransPortf"));
        }
        if (StringUtil.isNotEmpty((String) param.get("transferPortfolioId"))) {
            param.put("portfolioId", (String) param.get("transferPortfolioId"));
        }
        if (StringUtil.isNotEmpty((String) param.get("portfolioIdOut"))) {
            param.put("portfolioId", (String) param.get("portfolioIdOut"));
        }

        List<TransferCommonDto> resultList = transferCommonMapper.findAvaPortfolioInfo(param);
        for (TransferCommonDto dto : resultList) {
            if (StringUtil.equals(dto.getTradePlace(), 交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(dto.getTradePlace(), 交易场所_深圳证券交易所.getValue())) {
                // 新头寸
                PositionCashSecRequest requestDto = new PositionCashSecRequest();
                requestDto.setFlowType("01");

                List<PositionCashSecDto> ptlList = Lists.newArrayList();
                PositionCashSecDto secDto = new PositionCashSecDto();
                secDto.setPortfolioId(dto.getPortfolioId());
                secDto.setSecAcctId((String) param.get("secAcctId"));
                secDto.setCdate((String) param.get("tradeDate"));
                ptlList.add(secDto);
                requestDto.setPtlList(ptlList);

                BigDecimal availableCash = etrTradeCommonervice.queryPositionAvailableCash(requestDto);
                dto.setAvailableCash(availableCash);
                result.add(dto);
            } else {
                BigDecimal availableCash = getAvailableCash(param, dto);
                if (Objects.nonNull(availableCash) && availableCash.compareTo(BigDecimal.ZERO) != 0) {
                    dto.setAvailableCash(availableCash);
                    result.add(dto);
                }
            }
        }
        return result;
    }

    public List<PortfolioValuationDto> getPtlPortfolioValuation(Map<String, Object> paramMap) {
        return transferCommonMapper.getPtlPortfolioValuation(paramMap);
    }

    private BigDecimal queryAvailableCash(Map<String, Object> param, TransferCommonDto dto) {
        PtlCashPositionFlowDto pcpdto = new PtlCashPositionFlowDto();
        Date sysdate = DateUtil.parse((String) param.get("tradeDate"));
        pcpdto.setCdate(sysdate);
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(BussinessDictCodeList.资金账户类型_托管账户.getValue());
//                acctTypeList.add(BussinessDictCodeList.资金账户类型_交易所资金账户.getValue());
        pcpdto.setAcctType(acctTypeList);
        pcpdto.setPortfolioId(dto.getPortfolioId());
        pcpdto.setCcy((String) param.get("ccy"));
        if (basePtlCashPositionInfoService != null) {
            return basePtlCashPositionInfoService.queryUsableCashPosition(pcpdto);
        }

        return null;
    }

    private BigDecimal getAvailableCash(Map<String, Object> param, TransferCommonDto dto) {
        PtlCashPositionFlowDto pcpdto = new PtlCashPositionFlowDto();
        Date sysdate = DateUtil.parse((String) param.get("tradeDate"));
        pcpdto.setCdate(sysdate);
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(BussinessDictCodeList.资金账户类型_托管账户.getValue());
        pcpdto.setAcctType(acctTypeList);
        pcpdto.setPortfolioId(dto.getPortfolioId());
        pcpdto.setCcy((String) param.get("ccy"));

        return transferCommonMapper.queryUsableCashPosition(pcpdto);
    }


}

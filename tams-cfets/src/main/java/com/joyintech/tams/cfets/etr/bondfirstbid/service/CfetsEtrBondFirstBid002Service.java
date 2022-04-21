package com.joyintech.tams.cfets.etr.bondfirstbid.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsBiddingProductDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBid002Dto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一级投标指令
 *
 * @Author: kchen
 * @Date: 2022/4/19 14:52
 */
@Service
public class CfetsEtrBondFirstBid002Service extends CommonExportService<CfetsEtrBondFirstBid002Dto> implements BaseProcessService<CfetsEtrBondFirstBid002Dto> {
    @Autowired
    private CfetsEtrIbBondinfoMapper cfetsetrIbBondinfoMapper;


    @Override
    public void doComplete(CfetsEtrBondFirstBid002Dto cfetsEtrBondFirstBid002Dto, OperationType operationType) {

    }

    /**
     * 保存提交时，后台数据转换
     *
     * @param cfetsDto
     * @return
     */
    public CfetsEtrBondFirstBid002Dto packageEquity(CfetsEtrBondFirstBidSaveRequest cfetsDto) {
        FinProduct fp = null;
        if (StringUtil.isNotEmpty(cfetsDto.getFinprodId())) {
            fp = cfetsetrIbBondinfoMapper.findAssetInfo(cfetsDto.getFinprodId());
        }
        CfetsEtrBondFirstBid002Dto dto = new CfetsEtrBondFirstBid002Dto();
        BeanUtil.copyPropertiesIgnoreNull(dto, cfetsDto);
        if (StringUtil.isNotEmpty(fp)) {
            dto.setTradeMarket(fp.getTradeMarket());
            dto.setCalendarId(fp.getCalendarId());
            dto.setCcy(fp.getCcy());
            dto.setEntrustDate(cfetsDto.getEtrDate());
            dto.setFinprodAbbr(fp.getFinprodAbbr());
        }
        dto.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
        dto.setEntrustNum(cfetsDto.getTotalBiddingNum());
        dto.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue());
        dto.setElementId(cfetsDto.getElementId());

        List<CfetsBiddingProductDto> biddingProductList = cfetsDto.getBiddingProductList();
        List<CfetsEtrBondFirstBid002Dto> etrInvestBondFirstBidList = new ArrayList<>();
        //报价发行时，按照证券账户维度拆分为多条指令信息
        if (DictCodeListExt4.新债_发行方式_报价发行.getValue().equals(cfetsDto.getIssueClass())) {
            Map<String,List<CfetsBiddingProductDto>> secAcctIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getSecAcctId));
            CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
            BeanUtil.copyPropertiesIgnoreNull(dd, dto);
            for (Map.Entry<String,List<CfetsBiddingProductDto>> entry : secAcctIdMap.entrySet()) {
                dd.setBiddingProductList(null);
                dd.setBiddingProductList(entry.getValue());
                etrInvestBondFirstBidList.add(dd);
            }

        } else {
            CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
            BeanUtil.copyPropertiesIgnoreNull(dd, dto);
            etrInvestBondFirstBidList.add(dd);
        }

        //风险
        CfetsEtribdoRiskcalResponse riskDto = cfetsDto.getCfetsEtribdoRiskcalReturn();
        List<RiskTrialRetDto> riskTrialRetDto = new ArrayList<>();
        if (StringUtil.isNotEmpty(riskDto) && !StringUtil.isEmptyList(riskDto.getRiskcalReturnList())) {
            for (CfetsEtribdoRiskcalResponse it : riskDto.getRiskcalReturnList()) {
                RiskTrialRetDto t = new RiskTrialRetDto();
                BeanUtil.copyPropertiesIgnoreNull(t, it);
                t.setConclusion(riskDto.getRiskTrialSpan());
                riskTrialRetDto.add(t);
            }
        }
        dto.setRiskTrialRetDto(riskTrialRetDto);
        dto.setEtrInvestBondFirstBidDtos(etrInvestBondFirstBidList);
        return dto;
    }
}

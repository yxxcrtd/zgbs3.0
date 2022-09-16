package com.joyintech.service.period.trdbondlendtargetduration.service;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.period.trdbondlendtargetduration.dao.TrdBondLendTargetDurationMapper;
import com.joyintech.tams.common.base.dao.PaTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.TrdDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.entity.PtlSecPositionFlow;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.trdbondlendtargetduration.InvestTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdbondlendtargetduration.TrdBondLendTargetDurationDto;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: microservice
 * @Package com.joyintech.service.period.trdbondduration.service
 * @Description: TODO
 * @date Date : 2018年09月21日 14:40
 */
@Service
public class TrdBondLendTargetDurationService extends BaseService {

    @Autowired
    private TrdBondLendTargetDurationMapper trdBondLendTargetDurationMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private IPlatformDate platformDateImpl;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper totalAddMapper;

    public Page<SelectOptionDto> getBusiCode(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("PINAN_BUSINESS_TYPE137", DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue());
        param.put("PINAN_BUSINESS_TYPE138", DictCodeListExt3.平安业务类型_融出债券付息还本_收.getValue());
        return queryForPageForSelectReturn(page, length, () -> trdBondLendTargetDurationMapper.getBusiCode(param));
    }

    public Page<SelectOptionDto> getSecManageAcctId(int page, int length, Map<String, Object> param) {
        return queryForPageForSelectReturn(page, length, () -> trdBondLendTargetDurationMapper.getSecManageAcctId(param));
    }

    public Page<SelectOptionDto> getFinprodMarketId(int page, int length, Map<String, Object> param) {
        return queryForPageForSelectReturn(page, length, () -> trdBondLendTargetDurationMapper.getFinprodMarketId(param));
    }

    public Page<TrdBondLendTargetDurationDto> findPage(int page, int length, Map<String, Object> param) {
        List<String> statusValid = param.get("statusValid") == null ? null : (List)param.get("statusValid");
        setParams(param);
        this.setParamForFindAllData(param);
        Page<TrdBondLendTargetDurationDto> result = null;
        // update by wangcheng SHBS-2192 查询条件输入 资金到账日 ，有效状态选择 待处理 报错
        if (statusValid != null && StringUtil.isNotEmpty((String)param.get("tradeDateStart"))
            && ((statusValid.size() == 1) && statusValid.contains("01"))) {
            result = new Page<TrdBondLendTargetDurationDto>();
        }
        else {
            result = queryForPageForSelectReturn(page, length, () -> trdBondLendTargetDurationMapper.findPage(param));
        }
        return result;
    }

    private void setParamForFindAllData(Map<String, Object> param) {
        List<String> statusValid = param.get("statusValid") == null ? null : (List)param.get("statusValid");

        String cashType = param.get("cashType") == null ? null : (String)param.get("cashType");
        param.put("paBizType", cashType);
        StringBuilder cashTypeSb = new StringBuilder();
        if (cashType != null) {
            if (cashType.contains(DictCodeListExt1.平安业务类型_债券中间收息.getValue())
                || cashType.contains(DictCodeListExt1.平安业务类型_债券中间还本.getValue())) {
                cashTypeSb.append(DictCodeList.现金流类型_中间收息.getValue());
                cashTypeSb.append(DictCodeList.现金流类型_中间还本.getValue());
            }
            if (cashType.contains(DictCodeListExt1.平安业务类型_债券到期.getValue())) {
                cashTypeSb.append(DictCodeList.现金流类型_到期收息.getValue());
                cashTypeSb.append(DictCodeList.现金流类型_到期还本.getValue());
            }

            if (cashType.contains(DictCodeListExt1.平安业务类型_债券中间收息.getValue())) {
                param.put("cashType", DictCodeList.现金流类型_中间收息.getValue());
            }
            else if (cashType.contains(DictCodeListExt1.平安业务类型_债券到期.getValue())) {
                param.put("cashType", DictCodeList.现金流类型_到期还本.getValue());
            }
            else if (cashType.contains(DictCodeListExt1.平安业务类型_债券中间还本.getValue())) {
                param.put("cashType", DictCodeList.现金流类型_中间还本.getValue());
            }
            param.put("cashTypeSb", cashTypeSb.toString());
        }
    }

    private void setParams(Map<String, Object> param) {
        param.put("PAGE_DATE_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
        param.put("PAGE_DATE_STATUS03", DictCodeListExt1.画面数据状态_流程中.getValue());
        param.put("PAGE_DATE_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
        param.put("PAGE_DATE_STATUS06", DictCodeListExt1.画面数据状态_撤销.getValue());
        String plateDate = platformDateImpl.getSysDateStr();
        param.put("PLATEFORM_DATE", DateUtil.parse(plateDate));
        if (param.get(BussinessConstant.PLUS_DAYS) != null) {
            param.put("PLATEFORM_DATE_OTHER", DateUtil.plusDays(plateDate, (Integer)param.get(BussinessConstant.PLUS_DAYS)));
        }
        else {
            param.put("PLATEFORM_DATE_OTHER", DateUtil.plusDays(plateDate, 90));
        }
    }

    public TrdBondLendTargetDurationDto findEffectiveData(Map<String, Object> params) {
        setParams(params);
        if (com.joyintech.framework.util.StringUtil.isNotEmpty(params.get("cashType"))) {
            List<String> cashType = com.joyintech.framework.util.StringUtil.splitToList(",", (String)params.get("cashType"));
            params.put("cashTypeList",cashType );
        }
        // 根据statusValid查询数据
        return trdBondLendTargetDurationMapper.findEffectiveData(params);
    }

    public List<FinAssetIntInfo> findAssetIntInfo(Map<String, Object> params) {
        setParams(params);
        return trdBondLendTargetDurationMapper.findAssetIntInfo(params);
    }

    public List<PtlSecPositionFlow> findAmountGroupByInv(String tradeId, String portfolioId) {
        return trdBondLendTargetDurationMapper.findAmountGroupByInv(tradeId, portfolioId);
    }

    public InvestTradeInfoDto findFinProductTradeDto(String totalTradeId) {
        InvestTradeInfoDto investTradeInfoDto = new InvestTradeInfoDto();
        investTradeInfoDto.setTrdFinProductDealTotal(trdFinProductDealTotalMapper.selectByPrimaryKey(totalTradeId));
        investTradeInfoDto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotalMapper.selectByPrimaryKey(totalTradeId));
        investTradeInfoDto.setTrdDealTotalAdd(totalAddMapper.selectByPrimaryKey(totalTradeId));
        investTradeInfoDto.setStlPayOrdersList(trdBondLendTargetDurationMapper.selectStlPayOrders(totalTradeId));
        investTradeInfoDto.setTrdProductDealList(trdBondLendTargetDurationMapper.selectTrdProductDealList(totalTradeId));
        Collection<String> tradeIds = investTradeInfoDto.getTrdProductDealList().stream().collect(
                JoyinCollectors.toMap(TrdProductDeal::getTradeId, TrdProductDeal::getTradeId)).values();
        if (tradeIds != null && !tradeIds.isEmpty()) {
            investTradeInfoDto.setTrdProductDealAddList(trdBondLendTargetDurationMapper.selectDealAdds(tradeIds));
            investTradeInfoDto.setPaTrdProductDealList(trdBondLendTargetDurationMapper.selectPaDeals(tradeIds));
            investTradeInfoDto.setPtlTradeList(trdBondLendTargetDurationMapper.selectPtlDeals(tradeIds));
        }
        return investTradeInfoDto;
    }
}

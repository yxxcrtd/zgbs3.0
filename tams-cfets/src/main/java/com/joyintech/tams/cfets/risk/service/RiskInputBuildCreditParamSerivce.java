package com.joyintech.tams.cfets.risk.service;

import com.joyintech.framework.util.*;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveRequest;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmRegInfoListResponse;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdFirstBondInvestTradeDto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.BidOrder;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import com.joyintech.tams.component.business.riskall.utils.RiskUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesDealsMapper;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 构建授信计算参数
 *
 * @author jiangpeng
 * @date 2022/6/14 15:38
 */
@Service
public class RiskInputBuildCreditParamSerivce extends RiskCommonService {

    private static final Logger logger = LoggerFactory.getLogger(RiskInputBuildCreditParamSerivce.class);

    @Autowired
    private CreditLinesDealsMapper creditLinesDealsMapper;

    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 一级投标指令 构建授信计算参数
     *
     * @param data 数据
     * @param dtl  风控节点配置明细表
     * @return java.util.List<com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto>
     * @author jiangpeng
     * @date 2022/6/14 15:51
     */
    List<CreditLinesInputDto> buildCreditCfetsEtrBondFirstBidParam(Object data, RiskNodeConfigDtl dtl) {
        logger.debug("一级投标指令 构建授信计算参数:" + JsonUtils.object2Json(data));
        List<CreditLinesInputDto> result = new ArrayList<>();
        //这里处理授信
        CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, data);
        CfetsEtrBondFirstBidSaveRequest dto = (CfetsEtrBondFirstBidSaveRequest) data;
        inputDto.setTradeId(dto.getEntrustId());
        inputDto.setFinprodId(dto.getFinprodId());
        inputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        inputDto.setTradeDate(dto.getVdate());
        inputDto.setPs(dto.getPs());
        inputDto.setCounterId(dto.getCounterId());
        inputDto.setTradeAmt(dto.getEntrustNum());
        if (dto.getEntrustNum() != null) {
            inputDto.setShareAmt(dto.getEntrustNum().divide(new BigDecimal(100), RoundingMode.HALF_UP));
        }
        //最新面值
        BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(inputDto);
        if (newFaceValue == null) {
            newFaceValue = new BigDecimal(100);
        }
        if (inputDto.getShareAmt() != null) {
            inputDto.setTradeAmt(newFaceValue.multiply(inputDto.getShareAmt()));
        } else if (inputDto.getTradeAmt() != null) {
            inputDto.setTradeAmt(newFaceValue.multiply(ArithmeticUtil.divide(inputDto.getTradeAmt(), 100, 2)));
        }
        result.add(inputDto);
        return result;
    }

    /**
     * 一级投标确认(自主投标/分销买入/代投代缴) 构建授信计算参数
     *
     * @param data 数据
     * @param dtl  风控节点配置明细表
     * @return java.util.List<com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto>
     * @author jiangpeng
     * @date 2022/6/14 15:51
     */
    List<CreditLinesInputDto> buildCreditCfetsTrdFirstBidFirmInfoParam(Object data, RiskNodeConfigDtl dtl) {
        logger.debug("一级投标确认 构建授信计算参数:" + JsonUtils.object2Json(data));
        List<CreditLinesInputDto> result = new ArrayList<>();
        CfetsTrdFirstBidFirmInfoDto dto = (CfetsTrdFirstBidFirmInfoDto) data;
        //这里处理授信
        CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, data);
        BeanUtil.copyPropertiesIgnoreNull(inputDto, dto);
        inputDto.setTradeId(dto.getBidId());
        inputDto.setFinprodId(dto.getFinprodId());
        inputDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        inputDto.setTradeDate(dto.getTradeDate());
        inputDto.setPs(DictCodeList.交易方向_买入.getValue());
        inputDto.setCounterId(dto.getCounterId());
        if (StringUtil.equals(dto.getBiddingMethod(), DictCodeListExt4.投标方式_自主投标.getValue())) {
            inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            inputDto.setTradeAmt(dto.getTotalBiddingNum());
            if (dto.getTotalBiddingNum() != null) {
                inputDto.setShareAmt(dto.getTotalBiddingNum().divide(new BigDecimal(100), RoundingMode.HALF_UP));
            }
        } else if (StringUtil.equals(dto.getBiddingMethod(), DictCodeListExt4.投标方式_分销买入.getValue())) {
            inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
            inputDto.setTradeAmt(dto.getBidPriceTotal());
            if (dto.getBidPriceTotal() != null) {
                inputDto.setShareAmt(dto.getBidPriceTotal().divide(new BigDecimal(100), RoundingMode.HALF_UP));
            }
        }
        result.add(inputDto);
        return result;
    }

    /**
     * 一级申购 构建授信计算参数
     *
     * @param data 数据
     * @param dtl  风控节点配置明细表
     * @return java.util.List<com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto>
     * @author jiangpeng
     * @date 2022/6/14 15:51
     */
    List<CreditLinesInputDto> buildCreditCfetsTrdBondFirstSubInfoParam(Object data, RiskNodeConfigDtl dtl) {
        logger.debug("一级申购 构建授信计算参数:" + JsonUtils.object2Json(data));
        List<CreditLinesInputDto> result = new ArrayList<>();
        CfetsTrdBondFirstSubInfoDto dto = (CfetsTrdBondFirstSubInfoDto) data;
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
        //这里处理授信
        CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, data);
        BeanUtil.copyPropertiesIgnoreNull(inputDto, dto);
        inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
        inputDto.setTradeId(dto.getBidId());
        inputDto.setFinprodId(dto.getFinprodId());
        inputDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        inputDto.setTradeDate(dto.getTradeDate());
        inputDto.setPs(DictCodeList.交易方向_买入.getValue());
        inputDto.setCounterId(finProduct.getIssuer()); //发行人?
        inputDto.setTradeAmt(dto.getPayableAmount());
        if (dto.getSubscriptionAmount() != null) {
            inputDto.setShareAmt(dto.getSubscriptionAmount().divide(new BigDecimal(100), RoundingMode.HALF_UP));
        }
        result.add(inputDto);
        return result;
    }

    /**
     * 成交确认-一级申购 构建授信计算参数
     *
     * @param data 数据
     * @param dtl  风控节点配置明细表
     * @return java.util.List<com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto>
     * @author jiangpeng
     * @date 2022/6/14 15:51
     */
    List<CreditLinesInputDto> buildCreditCfetsTrdFirstBondInvestTradeParam(Object data, RiskNodeConfigDtl dtl) {
        logger.debug("成交确认-一级申购 构建授信计算参数:" + JsonUtils.object2Json(data));
        List<CreditLinesInputDto> result = new ArrayList<>();
        CfetsTrdFirstBondInvestTradeDto dto = (CfetsTrdFirstBondInvestTradeDto) data;
        CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, null);
        BeanUtil.copyPropertiesIgnoreNull(inputDto, dto);
        inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
        inputDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        inputDto.setEntrustId(dto.getRegIbuId());
        inputDto.setTradeDate(dto.getTradeDate());
        inputDto.setFinprodId(dto.getFinprodId());
        if (StringUtil.isNotEmpty(dto.getCounterId())) {
            inputDto.setCounterId(dto.getCounterId());
        }
        inputDto.setTradeId(dto.getTotleTradeId());
        result.add(inputDto);
        return result;
    }

    /**
     * 成交确认-发行失败 构建授信计算参数
     *
     * @param data 数据
     * @param dtl  风控节点配置明细表
     * @return java.util.List<com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto>
     * @author jiangpeng
     * @date 2022/6/14 15:51
     */
    List<CreditLinesInputDto> buildCreditCfetsTrdConfirmRegInfoListResponseParam(Object data, RiskNodeConfigDtl dtl) {
        logger.debug("成交确认-发行失败 构建授信计算参数:" + JsonUtils.object2Json(data));
        List<CreditLinesInputDto> result = new ArrayList<>();
        CfetsTrdConfirmRegInfoListResponse dto = (CfetsTrdConfirmRegInfoListResponse) data;
        List<CfetsTrdConfirmRegInfoListResponse> selectedItem = dto.getSelectedItem();
        List<String> finprodIds = new ArrayList<>();
        if (StringUtil.isNotEmpty(selectedItem)) {
            selectedItem.forEach(item -> {
                if (!finprodIds.contains(item.getFinprodId())) {
                    finprodIds.add(item.getFinprodId());
                }
            });
        }
        Map<String, Object> param = new HashMap<>();
        param.put("finprodIds", finprodIds);
        param.put("isCancel", DictCodeList.是否_否.getValue());
        param.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
        List<BidOrder> bidOrders = creditLinesDealsMapper.selectBidOrderByFinprodIds(param);
        bidOrders.forEach(item -> {
                    CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, null);
                    String tradeStatus = inputDto.getTradeStatus();
                    BeanUtil.copyPropertiesIgnoreNull(inputDto, item);
                    inputDto.setTradeStatus(tradeStatus);
                    inputDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
                    inputDto.setEntrustId(item.getBidId());
                    inputDto.setTradeDate(item.getBidDate());
                    inputDto.setFinprodId(item.getFinprodId());
                    if (StringUtil.isNotEmpty(item.getCounterId())) {
                        inputDto.setCounterId(item.getCounterId());
                    }
                    inputDto.setTradeId(item.getBidId());
                    result.add(inputDto);
                }
        );
        return result;
    }

    /**
     * 中标结果维护 构建授信计算参数
     *
     * @param data 数据
     * @param dtl  风控节点配置明细表
     * @return java.util.List<com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto>
     * @author jiangpeng
     * @date 2022/6/14 15:51
     */
    List<CreditLinesInputDto> buildCfetsPrimaryBidManageSaveRequestParam(Object data, RiskNodeConfigDtl dtl) {
        logger.debug("中标结果维护 构建授信计算参数:" + JsonUtils.object2Json(data));
        List<CreditLinesInputDto> result = new ArrayList<>();
        CreditLinesInputDto inputDto = RiskUtil.getCreditLinesInputDto(dtl, data);
        CfetsPrimaryBidManageSaveRequest dto = (CfetsPrimaryBidManageSaveRequest) data;

        BeanUtil.copyPropertiesIgnoreNull(inputDto, dto);
        inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
        inputDto.setEntrustId(dto.getBidId());
        inputDto.setPs(DictCodeList.交易方向_买入.getValue());
        inputDto.setTradeDate(DateUtil.platDateFromCache());
        inputDto.setCounterId(dto.getCounterId());
        inputDto.setTradeId(dto.getTotalWinId());
        inputDto.setTradeAmt(dto.getTotalBiddingNum());
//        if(StringUtil.equals(DictCodeListExt1.交易状态_撤销.getValue(), inputDto.getTradeStatus())){
//            inputDto.setEntrustId(dto.getTotalWinId());
//            inputDto.setTradeId(dto.getTotalWinId()+"R");
//        }
        //最新面值
        BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(inputDto);
        if (newFaceValue == null) {
            newFaceValue = new BigDecimal(100);
        }
        if (inputDto.getShareAmt() != null) {
            inputDto.setTradeAmt(newFaceValue.multiply(inputDto.getShareAmt()));
        } else if (inputDto.getTradeAmt() != null) {
            inputDto.setTradeAmt(newFaceValue.multiply(ArithmeticUtil.divide(inputDto.getTradeAmt(), 100, 2)));
        }
        result.add(inputDto);
        return result;
    }
}

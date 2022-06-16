package com.joyintech.tams.cfets.risk.service;

import com.joyintech.background.component.common.pack.common.util.ArithmeticUtil;
import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.common.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmProductDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveRequest;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdFirstBondInvestTradeDto;
import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import com.joyintech.tams.component.business.riskall.utils.RiskUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 构建指标计算参数
 *
 * @author jiangpeng
 * @date 2022/6/10 11:39
 */
@Service
public class RiskInputBuildRiskRdsParamSerivce extends RiskCommonService {
    private static final Logger logger = LoggerFactory.getLogger(RiskInputBuildRiskRdsParamSerivce.class);

    @Autowired
    private InterfRiskRdsService interfRiskRdsService;

    /**
     * 构建指标计算参数-一级申购
     *
     * @param data    数据
     * @param dtl     风险配置
     * @param isCheck 是否校验
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author jiangpeng
     * @date 2022/6/15 19:44
     */
    List<Map<String, Object>> buildRiskRdsCfetsTrdBondFirstSubInfoParam(Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        logger.debug("构建指标计算参数-一级申购:" + JsonUtils.object2Json(data));
        InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
        riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
        riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
        riskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_一级投标确认_申购_登记.getValue());
        CfetsTrdBondFirstSubInfoDto dto = (CfetsTrdBondFirstSubInfoDto) data;
        Map<String, CfetsTrdBondFirstSubProductDto> map = new HashMap<>();
        dto.getProductDtos().forEach(item -> {
            CfetsTrdBondFirstSubProductDto productDto = new CfetsTrdBondFirstSubProductDto();
            if (map.get(dto.getBidId() + item.getPortfolioId()) == null) {
                BeanUtil.copyPropertiesIgnoreNull(productDto, item);
                productDto.setEntrustId(dto.getBidId() + item.getPortfolioId());
                productDto.setFinprodId(dto.getFinprodId());
            } else {
                productDto = map.get(dto.getBidId() + item.getPortfolioId());
                // 剩余指令额度
                productDto.setAvaNum(ArithmeticUtil.doAdd(productDto.getAvaNum(), item.getAvaNum()));
                // 交易手续费
                productDto.setSettFeeAmt(ArithmeticUtil.doAdd(productDto.getSettFeeAmt(), item.getSettFeeAmt()));
                // 应缴款金额
                productDto.setPayableAmount(ArithmeticUtil.doAdd(productDto.getPayableAmount(), item.getPayableAmount()));
                // 认购量
                productDto.setSubscriptionAmount(ArithmeticUtil.doAdd(productDto.getSubscriptionAmount(), item.getSubscriptionAmount()));
                productDto.setEntrustId(dto.getBidId() + item.getPortfolioId());
                productDto.setFinprodId(dto.getFinprodId());
            }
            map.put(dto.getBidId() + item.getPortfolioId(), productDto);
        });
        List<CfetsTrdBondFirstSubProductDto> list = new ArrayList<>(map.values());
        return interfRiskRdsService.getRiskRdsListData(riskRdsDto, list, null);
    }

    /**
     * 构建指标计算参数-一级投标确认
     *
     * @param data    数据
     * @param dtl     风险配置
     * @param isCheck 是否校验
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author jiangpeng
     * @date 2022/6/15 19:44
     */
    List<Map<String, Object>> buildRiskRdsCfetsTrdFirstBidFirmInfoParam(Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        logger.debug("构建指标计算参数-一级投标确认:" + JsonUtils.object2Json(data));
        InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
        riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
        riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
        CfetsTrdFirstBidFirmInfoDto dto = (CfetsTrdFirstBidFirmInfoDto) data;
        if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), dto.getBiddingMethod())) {
            //  自主投标
            riskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_一级投标确认_投标_自主投标.getValue());
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), dto.getBiddingMethod())) {
            //代投代缴
            riskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_一级投标确认_投标_代投代缴.getValue());
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), dto.getBiddingMethod())) {
            // 分销买入
            riskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入.getValue());
        }
        Map<String, CfetsTrdFirstBidFirmProductDto> map = new HashMap<>();
        dto.getProductDtos().forEach(item -> {
            CfetsTrdFirstBidFirmProductDto productDto = new CfetsTrdFirstBidFirmProductDto();
            if (map.get(dto.getBidId() + item.getPortfolioId()) == null) {
                BeanUtil.copyPropertiesIgnoreNull(productDto, item);
                productDto.setEntrustId(dto.getBidId() + item.getPortfolioId());
                productDto.setFinprodId(dto.getFinprodId());
            } else {
                productDto = map.get(dto.getBidId() + item.getPortfolioId());
                // 剩余指令额度
                productDto.setAvaNum(ArithmeticUtil.doAdd(productDto.getAvaNum(), item.getAvaNum()));
                // 交易手续费
                productDto.setSettFeeAmt(ArithmeticUtil.doAdd(productDto.getSettFeeAmt(), item.getSettFeeAmt()));
                // 缴款金额
                productDto.setPayAmt(ArithmeticUtil.doAdd(productDto.getPayAmt(), item.getPayAmt()));
                // 交易金额
                productDto.setTradeAmt(ArithmeticUtil.doAdd(productDto.getTradeAmt(), item.getTradeAmt()));
                // 汇总投标券面总额
                productDto.setTotalBiddingNum(ArithmeticUtil.doAdd(productDto.getTotalBiddingNum(), item.getTotalBiddingNum()));
                // 指令投标券面总额
                productDto.setEtrBidPriceTotal(ArithmeticUtil.doAdd(productDto.getEtrBidPriceTotal(), item.getEtrBidPriceTotal()));
                // 券面总额
                productDto.setBidPrinAmt(ArithmeticUtil.doAdd(productDto.getBidPrinAmt(), item.getBidPrinAmt()));
                // 应计利息
                productDto.setIntAmt(ArithmeticUtil.doAdd(productDto.getIntAmt(), item.getIntAmt()));
                // 应计利息总额
                productDto.setIntAmtTatol(ArithmeticUtil.doAdd(productDto.getIntAmtTatol(), item.getIntAmtTatol()));
                productDto.setEntrustId(dto.getBidId() + item.getPortfolioId());
                productDto.setFinprodId(dto.getFinprodId());
            }
            map.put(dto.getBidId() + item.getPortfolioId(), productDto);
        });
        List<CfetsTrdFirstBidFirmProductDto> list = new ArrayList<>(map.values());
        return interfRiskRdsService.getRiskRdsListData(riskRdsDto, list, null);
    }

    /**
     * 构建指标计算参数-成交确认-一级申购
     *
     * @param data    数据
     * @param dtl     风险配置
     * @param isCheck 是否校验
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author jiangpeng
     * @date 2022/6/15 19:44
     */
    List<Map<String, Object>> buildRiskRdsCfetsTrdFirstBondInvestTradeParam(Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        logger.debug("构建指标计算参数-成交确认-一级申购:" + JsonUtils.object2Json(data));
        InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
        CfetsTrdFirstBondInvestTradeDto entity = (CfetsTrdFirstBondInvestTradeDto) data;
        riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
        riskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_一级投标确认_申购_登记.getValue());
        Map<String, TrdBondInvestTradeDto> map = new HashMap<>();
        entity.getTrdProductDeals().forEach(item -> {
            TrdBondInvestTradeDto bondDto = new TrdBondInvestTradeDto();
            if (map.get(entity.getTotleTradeId() + item.getPortfolioId()) == null) {
                BeanUtil.copyPropertiesIgnoreNull(bondDto, item);
                bondDto.setFpriceAmt(item.getTradeAmt());
                bondDto.setEntrustId(entity.getRegIbuId() + item.getPortfolioId());
                bondDto.setTradeId(entity.getTotleTradeId() + item.getPortfolioId());
                bondDto.setFinprodId(entity.getFinprodId());
            } else {
                bondDto = map.get(entity.getRegIbuId() + item.getPortfolioId());
                // 交易份额
                bondDto.setShareAmt(ArithmeticUtil.doAdd(bondDto.getShareAmt(), item.getShareAmt()));
                // 总交易费用
                bondDto.setFeeAmt(ArithmeticUtil.doAdd(bondDto.getFeeAmt(), item.getFeeAmt()));
                // 未随交易支付费用
                bondDto.setFeeAmtUnpay(ArithmeticUtil.doAdd(bondDto.getFeeAmtUnpay(), item.getFeeAmtUnpay()));
                // 随交易支付费用
                bondDto.setFeeAmtPay(ArithmeticUtil.doAdd(bondDto.getFeeAmtPay(), item.getFeeAmtPay()));
                // 交易金额
                bondDto.setTradeAmt(ArithmeticUtil.doAdd(bondDto.getTradeAmt(), item.getTradeAmt()));
                // 全价总额
                bondDto.setFpriceAmt(ArithmeticUtil.doAdd(bondDto.getFpriceAmt(), item.getFpriceAmt()));
                bondDto.setEntrustId(entity.getRegIbuId() + item.getPortfolioId());
                bondDto.setTradeId(entity.getTotleTradeId() + item.getPortfolioId());
                bondDto.setFinprodId(entity.getFinprodId());
            }
            map.put(entity.getTotleTradeId() + item.getPortfolioId(), bondDto);
        });
        List<TrdBondInvestTradeDto> list = new ArrayList<>(map.values());
        return interfRiskRdsService.getRiskRdsListData(riskRdsDto, list, null);
    }

    /**
     * 构建指标计算参数-中表结果维护
     *
     * @param data    数据
     * @param dtl     风险配置
     * @param isCheck 是否校验
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author jiangpeng
     * @date 2022/6/15 19:44
     */
    List<Map<String, Object>> buildRiskRdsCfetsPrimaryBidManageSaveRequestParam(Object data, RiskNodeConfigDtl dtl, Boolean isCheck) {
        logger.debug("构建指标计算参数-中表结果维护:" + JsonUtils.object2Json(data));
        InterfRiskRdsDto riskRdsDto = RiskUtil.getInterfRiskRdsDto(dtl, isCheck);
        riskRdsDto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
        riskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
        CfetsPrimaryBidManageSaveRequest dto = (CfetsPrimaryBidManageSaveRequest) data;
        if (StringUtil.equals(dto.getBidModle(), DictCodeListExt4.投标方式_自主投标.getValue())) {
            riskRdsDto.setEventStatus(DictCodeListExt4.共通业务类型_中标结果维护自主投标.getValue());
        } else {
            riskRdsDto.setEventStatus(DictCodeListExt4.共通业务类型_中标结果维护代投代缴.getValue());
        }
        dto.getAllocationDetails().forEach(item -> {
            item.setBidId(dto.getBidId());
            item.setBidDate(dto.getPayDate());
        });
        return interfRiskRdsService.getRiskRdsListData(riskRdsDto, dto.getAllocationDetails(), null);
    }

}

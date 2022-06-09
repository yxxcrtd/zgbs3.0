/*
 * 文件名：TrdFundInvestTradeValidator.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：xiexinting
 * 修改时间：2018年11月22日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.fund.validator;

import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.common.validator.TradeCommonValidator;
import com.joyintech.tams.invest.trade.fund.dto.TrdFundInvestTradeDto;
import com.joyintech.tams.invest.trade.fund.service.TrdFundInvestTrade001Service;
import com.joyintech.tams.invest.trade.fund.service.TrdFundInvestTrade002Service;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.invest.trade.common.service.TradeCommonService.金融产品代码;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 *
 * @author xiexinting
 * @version 1.0 2018年11月22日
 * @see TrdFundInvestTradeValidator
 * @since 1.0
 */
@Component
public class TrdFundInvestTradeValidator implements TradeCommonValidator {

    @Autowired
    private TrdFundInvestTrade001Service trdFundInvestTrade001Service;

    @Autowired
    private TrdFundInvestTrade002Service trdFundInvestTrade002Service;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Autowired
    private TradeCommonService tradeCommonService;

    /**
     * BussTrdTransferOrderService
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

//    @Autowired
//    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
        TrdFundInvestTradeDto dto = (TrdFundInvestTradeDto)value;

        // 场内基金改造 add by renping 20191117 start
        Map<String, Object> params = new HashMap<>();
        if (StringUtil.equals(dto.getOperateType(), "revoke")) {
            params.put("finprodId", dto.getSelectedItem().get(0).getFinprodId());
        }
        else {
            params.put("finprodId", dto.getFinprodId());
        }
        Page<SelectOptionDto> pageList = trdFundInvestTrade002Service.getFinprodId(params);
        Map<String, Object> map = (Map<String, Object>)pageList.getRoot().get(0).getObject();
        if (StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit")) {
            if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
                if (!StringUtil.isNotEmpty(map.get("toffStrdate")) || !StringUtil.isNotEmpty(map.get("toffEnddate"))
                        || dto.getTradeDateString().compareTo((String)map.get("toffStrdate")) < 0
                        || dto.getTradeDateString().compareTo((String)map.get("toffEnddate")) > 0) {
                    errorList.add(new ValidMessage(this.getMessage("TRD_FUND_E_002")));
                }

                if (dto.getPurCfmDate() != null && StringUtil.isNotEmpty(map.get("vdate"))
                        && DateUtil.format(dto.getPurCfmDate()).compareTo((String)map.get("vdate")) < 0) {
                    errorList.add(new ValidMessage("purCfmDate", this.getMessage("TRD_FUND_E_004")));
                }
            }
            if (!StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
                if (StringUtil.isNotEmpty(map.get("toffStrdate")) && StringUtil.isNotEmpty(map.get("toffEnddate"))
                        && dto.getTradeDateString().compareTo((String)map.get("toffStrdate")) >= 0
                        && dto.getTradeDateString().compareTo((String)map.get("toffEnddate")) <= 0) {
                    errorList.add(new ValidMessage("ps", this.getMessage("TRD_FUND_E_007")));
                }
            }
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                if ((StringUtil.isNotEmpty(map.get("etfType"))
                        && !StringUtil.equals(map.get("etfType"), DictCodeListExt2.ETF类型_货币ETF.getValue()))
                        || (StringUtil.isNotEmpty(map.get("trmFundType"))
                        && StringUtil.equals(map.get("trmFundType"), DictCodeListExt2.交易所基金类型_封闭式基金.getValue()))
                        || (StringUtil.isNotEmpty(map.get("trmFundType"))
                        && StringUtil.equals(map.get("trmFundType"), DictCodeListExt2.交易所基金类型_分级基金.getValue())
                        && StringUtil.isNotEmpty(map.get("pFinprodId")))) {
                    errorList.add(new ValidMessage(this.getMessage("TRD_FUND_E_003")));
                }
            }
            if ((StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                    && (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()))) {
                if (dto.getUnitCprice() == null) {
                    errorList.add(new ValidMessage("unitCprice",
                            this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradePrice"))));
                }

                if (StringUtil.isNotEmpty(dto.getEntrustId()) && dto.getTrdInvestPersonInfo() != null
                        && (StringUtil.equals(dto.getTrdInvestPersonInfo().getCounterId(),
                        BussinessConstant.COUNTER_PARTY_ID_SHANGHAI)
                        || StringUtil.equals(dto.getTrdInvestPersonInfo().getCounterId(),
                        BussinessConstant.COUNTER_PARTY_ID_SHENZHEN))) {
                    BaseEtrInvestInfoDto baseEtrInvestInfoDto = trdFundInvestTrade002Service.getEtrustDto(dto.getEntrustId());
                    if (baseEtrInvestInfoDto != null && baseEtrInvestInfoDto.getEtrEntrust() != null) {
                        BigDecimal etrPrice = baseEtrInvestInfoDto.getEtrEntrust().getPrice();
                        if (etrPrice != null && dto.getUnitCprice() != null) {
                            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                                    && etrPrice.compareTo(dto.getUnitCprice()) < 0) {
                                errorList.add(new ValidMessage("unitCprice", this.getMessage("TRD_FUND_E_006")));
                            }
                            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                                    && etrPrice.compareTo(dto.getUnitCprice()) > 0) {
                                errorList.add(new ValidMessage("unitCprice", this.getMessage("TRD_FUND_E_005")));
                            }
                        }
                    }
                }
            }
            if (DictCodeListExt2.交易方向_转换申请.getValue().equals(dto.getPs()) && StringUtil.isEmpty(dto.getChangeToFinprodMarketId())) {
                errorList.add(new ValidMessage("changeToFinprodMarketId", "基金转换申请时，转入资产必填。"));
            }
//            if(bussCommonMapperDelegateService != null && StringUtil.isNotEmpty(dto.getFinprodId())) {
//                FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, dto.getFinprodId(), FinProduct.class);
//                if (finProduct != null && finProduct.getVdate() != null && (DateUtil.compareDate(DateUtil.parse(dto.getTradeDateString()), finProduct.getVdate()) < 0)) {
//                    errorList.add(new ValidMessage("tradeDate", getMessage("BUSS_COM_E_0004", getMessage("TrdFundInvestTrade002.tradeDate2"), "成立日期")));
//                }
//            }
        }
        // 场内基金改造 add by renping 20191117 end
        String entrustId = dto.getEntrustId();
        BigDecimal tradeAmt = dto.getPrinAmt();
        String tradeId = dto.getTradeId();
        Map<String, Object> param = new HashMap<>();
        param.put("entrustId", entrustId);
        param.put("tradeId", tradeId);
        BigDecimal shareAmt = dto.getShareAmt();
        BigDecimal zero = new BigDecimal(0);
        // 获取委托的份额,基金的赎回委托属于限量委托 yjm start 20191121
        if (StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit")) {
            if (StringUtil.isNotEmpty(entrustId) && StringUtil.isNotEmpty(shareAmt)
                    && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                Map<String, Object> resultMap = trdFundInvestTrade002Service.selectEntrustAmt(param);
                BigDecimal entrustNum = new BigDecimal((String) resultMap.get("ENTRUST_NUM"));
                BigDecimal finishAmt = new BigDecimal((String) resultMap.get("FINISH_AMT"));

                // 未使用额度 为  entrustNum -  已使用额度
                BigDecimal availablEntrustNum = ArithmeticUtil.subtract(entrustNum, finishAmt);

                if (StringUtil.isNotEmpty(availablEntrustNum) && shareAmt.compareTo(availablEntrustNum) > 0) {
//                        errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0003",
//                                this.getMessage("TrdFundInvestTrade002.tradeSha"), "委托份额:" + entrustNum)));
                    errorList.add(new ValidMessage("shareAmt", "基金赎回份额不能大于基金可交易额度份额，可交易额度份额:" + availablEntrustNum));

                }
            }
//             else {
//                 if (DictCodeList.交易方向_赎回.getValue().equals(dto.getPs())) {
//                     List<BigDecimal> ssList= tradeCommonService.selectProAvailableCash(dto.getEntrustId());
//                     if (!StringUtil.isEmptyList(ssList) &&
//                             ssList.get(0).compareTo(BigDecimal.ZERO) != 0) {
//                         if (dto.getShareAmt().compareTo(ssList.get(0)) == 1) {
////                             errorList.add(new ValidMessage("shareAmt", this.getMessage("BUSS_COM_E_0003",
////                                     this.getMessage("TrdFundInvestTrade002.tradeSha"), "委托份额:" + ssList.get(0))));
//                             errorList.add(new ValidMessage("shareAmt", "基金赎回份额不能大于基金可交易额度份额，可交易额度份额:" + ssList.get(0)));
//                         }
//                     }
//                 }
//             }
            // OPEX-878 公募基金分红后赎回交易问题 如果存在存续期流程中红利再投数据，禁止赎回操作。 qcx 20200305
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                List<String> 流程中 = tradeCommonService.checkWflData(dto.getFinprodId(), 金融产品代码, true);
                if (流程中 != null && !流程中.isEmpty()) {
                    errorList.add(new ValidMessage("finprodId", "该资产存在流程中的存续期数据数据，交易编号：" + 流程中));
                }
            }

            if(StringUtil.isEmptyList(errorList)) {
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    if (StringUtil.isNotEmpty(shareAmt) && StringUtil.isNotEmpty(dto.getAssetAvailablePosition())
                            && shareAmt.compareTo(dto.getAssetAvailablePosition()) == 1) {
                        errorList.add(new ValidMessage("shareAmt",
                                this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeSha"),
                                        this.getMessage("TrdBondInvestTrade002.avaliblePosition"))));
                    }
                }
            }
        }
        // 获取委托的份额,基金的赎回委托属于限量委托 yjm end 20191121

        if (StringUtil.equals(dto.getOperateType(), "save")) {
            if (StringUtil.isNotEmpty(entrustId) && tradeAmt != null) {
                Map<String, Object> resultMap = trdFundInvestTrade002Service.selectEntrustAmt(param);
                if (resultMap != null) {
                    BigDecimal entrustAmt = new BigDecimal((String)resultMap.get("ENTRUST_AMT"));
                    BigDecimal entrustNum = new BigDecimal((String)resultMap.get("ENTRUST_NUM"));
                    BigDecimal finishAmt = new BigDecimal((String)resultMap.get("FINISH_AMT"));
                    BigDecimal prinAmt = new BigDecimal((String)resultMap.get("PRIN_AMT"));
                    if (entrustAmt.compareTo(new BigDecimal("-1")) == 0) {
                        entrustAmt = ArithmeticUtil.multiply(entrustNum, dto.getUnitCprice());
                    }
                    if (finishAmt != null) {
                        entrustAmt = entrustAmt.subtract(finishAmt);
                    }
                    if (prinAmt != null) {
                        entrustAmt = entrustAmt.subtract(prinAmt);
                    }
                    if (tradeAmt.compareTo(entrustAmt) > 0) {
                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("TRD_X_0004") + entrustAmt));
                    }
                }
            }
            // 验证流程中是否存在该委托
            if (StringUtil.isNotEmpty(entrustId) && WorkFlowUtil.isNotFromToDoList()) {
                String str = tradeCommonService.check(entrustId, "TrdFundInvestTrade002");
                if (StringUtil.isNotEmpty(str)) {
                    ValidMessage validMessage = new ValidMessage(str);
                    errorList.add(validMessage);
                }
            }
            if (StringUtil.isNotEmpty(dto.getPs())) {
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    if (!StringUtil.isNotEmpty(dto.getSettleSpreed())) {
                        errorList.add(new ValidMessage("settleSpreed",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.settleSpreed"))));
                    }
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    if (shareAmt == null) {
                        errorList.add(new ValidMessage("shareAmt",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeSha"))));
                    }
                    if (tradeAmt == null) {
                        errorList.add(new ValidMessage("tradeAmt",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeAmt"))));
                    }
                }
                // 场内基金改造 modify by renping 20191117 start
                if (StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                    // 场内基金改造 modify by renping 20191117 end
                    if (tradeAmt == null) {
                        errorList.add(new ValidMessage("tradeAmt",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeAmt"))));
                    }
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    if (shareAmt == null) {
                        errorList.add(new ValidMessage("shareAmt",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeSha"))));
                    }
                    if (StringUtil.isNotEmpty(shareAmt) && StringUtil.isNotEmpty(dto.getAssetAvailablePosition())
                            && shareAmt.compareTo(dto.getAssetAvailablePosition()) == 1) {
                        errorList.add(new ValidMessage("shareAmt",
                                this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeSha"),
                                        this.getMessage("TrdBondInvestTrade002.avaliblePosition"))));
                    }
                }
                if (tradeAmt != null && tradeAmt.compareTo(zero) == -1) {
                    errorList.add(new ValidMessage("tradeAmt",
                            this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeAmt"), "0")));
                }
                if (shareAmt != null && shareAmt.compareTo(zero) == -1) {
                    errorList.add(new ValidMessage("shareAmt",
                            this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeSha"), "0")));
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    BigDecimal assetAvailablePosition = dto.getAssetAvailablePosition();
                    if (assetAvailablePosition != null && shareAmt != null && shareAmt.compareTo(assetAvailablePosition) == 1) {
                        errorList.add(new ValidMessage("shareAmt",
                                this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeSha"),
                                        this.getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                    }
                }
                // 场内基金改造 modify by renping 20191117 start
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
                    // 场内基金改造 modify by renping 20191117 end
                    // 划款指令
                    if (bussTrdTransferOrderService != null) {
                        List<ValidMessage> list = bussTrdTransferOrderService.check(dto.getBussTrdTransferOrder());
                        errorList.addAll(list);
                    }
                }
            }
            /* 基金类资产交易必填CHECK取消 update by zhuyongkang 2019/09/18 start */
            // if (dto.getFileArray() == null || dto.getFileArray().size() <= 0) {
            // errorList.add(new ValidMessage(
            // getMessage("BUSS_COM_E_0038", getMessage("TrdFundInvestTrade002.file"))));
            // }
            /* 基金类资产交易必填CHECK取消 update by zhuyongkang 2019/09/18 end */
            if (dto.getTrdInvestPersonInfo() == null || StringUtil.isEmpty(dto.getTrdInvestPersonInfo().getCounterId())) {
                errorList.add(new ValidMessage("trdInvestPersonInfo.counterId", getMessage("BUSS_COM_E_0038", "交易对手")));
            }
            // else if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())){ // 内部交易只能选择卖出，这个注释掉
            // PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
            // ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
            // ptlSecPositionFlowDto.setPortfolioId(dto.getTrdInvestPersonInfo().getCounterId());
            // ptlSecPositionFlowDto.setSecAcctId(dto.getChlId());
            // ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
            // BigDecimal assetPosition = trdFundInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
            // //资产可用持仓小于交易份额
            // if (dto.getShareAmt() == null) {
            // errorList.add(new ValidMessage("unitCprice", getMessage("TRD_FUND_E_001")));
            // }
            // else if(assetPosition == null || assetPosition.compareTo(dto.getShareAmt())== -1){
            // errorList.add(new ValidMessage("",
            // getMessage("BUSS_COM_E_0003",
            // ptlSecPositionFlowDto.getPortfolioId()+" "+getMessage("TrdFundInvestTrade002.tradeSha"),
            // getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
            // }
            //
            // //同时验证买入方是否有足够的现金
            // Map<String, Object> param = new HashMap<>();
            // param.put("portfolioId", dto.getPortfolioId());
            // param.put("ccy", dto.getCcy());
            // param.put("vdate", DateUtil.format(dto.getTradeDate()));
            // param.put("calId", dto.getCalendarId());
            // param.put("chlId", dto.getChlId());
            // Map<String, Object> para = trdFundInvestTrade002Service.getProAvailableCash(param);
            // BigDecimal accCash = new BigDecimal((String)para.get("cashT3"));
            // if(accCash.compareTo(dto.getTradeAmt())==-1){
            // errorList.add(new ValidMessage("",
            // getMessage("BUSS_COM_E_0003",
            // param.get("portfolioId")+" "+getMessage("TrdFundInvestTrade002.tradeAmt"),
            // getMessage("TrdFundInvestTrade002.accAvailableCash"))));
            // }
            // }
            else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                param.put("portfolioId", dto.getTrdInvestPersonInfo().getCounterId());
                param.put("ccy", dto.getCcy());
                param.put("vdate", DateUtil.format(dto.getTradeDate()));
                param.put("calId", dto.getCalendarId());
                param.put("chlId", dto.getChlId());
                Map<String, Object> para = trdFundInvestTrade002Service.getProAvailableCash(param);
                BigDecimal accCash = new BigDecimal((String)para.get("cashT3"));
                // update by 张年 2019/08/26 start
                // 交易对手没有足够的现金
                /*
                 * if(accCash.compareTo(dto.getTradeAmt())==-1){ errorList.add(new ValidMessage("", getMessage("BUSS_COM_E_0003",
                 * param.get("portfolioId")+"  "+getMessage("TrdFundInvestTrade002.tradeAmt"), getMessage("TrdFundInvestTrade002.accAvailableCash")))); }
                 */
                // update by 张年 2019/08/26 end
                // 同时验证卖出方是否有足够的持仓
                if (StringUtil.isNotEmpty(dto.getPortfolioId()) && dto.getTradeDate() != null
                        && StringUtil.isNotEmpty(dto.getFinprodId())) {
                    PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                    ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
                    ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
                    // 场内基金改造 modify by renping 20191117 start
                    ptlSecPositionFlowDto.setSecAcctId(
                            StringUtil.isEmpty(dto.getSecManageAcctId()) ? dto.getChlId() : dto.getSecManageAcctId());
                    // 场内基金改造 modify by renping 20191117 end
                    ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
                    BigDecimal assetPosition = trdFundInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
                    if (assetPosition.compareTo(dto.getShareAmt()) == -1) {
                        errorList.add(new ValidMessage("",
                                getMessage("BUSS_COM_E_0003",
                                        ptlSecPositionFlowDto.getPortfolioId() + "  " + getMessage("TrdFundInvestTrade002.tradeSha"),
                                        getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                    }
                }
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "submit") && !StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            if (StringUtil.isNotEmpty(entrustId) && tradeAmt != null) {
                Map<String, Object> resultMap = trdFundInvestTrade002Service.selectEntrustAmt(param);
                if (resultMap != null) {
                    BigDecimal entrustAmt = new BigDecimal((String)resultMap.get("ENTRUST_AMT"));
                    BigDecimal entrustNum = new BigDecimal((String)resultMap.get("ENTRUST_NUM"));
                    BigDecimal finishAmt = new BigDecimal((String)resultMap.get("FINISH_AMT"));
                    BigDecimal prinAmt = new BigDecimal((String)resultMap.get("PRIN_AMT"));
                    if (entrustAmt.compareTo(new BigDecimal("-1")) == 0) {
                        entrustAmt = ArithmeticUtil.multiply(entrustNum, dto.getUnitCprice());
                    }
                    //add  wsz 修改数据为空判定失败问题 ZGCPCN-1139 start
                    if (finishAmt != null && entrustAmt != null) {
                        entrustAmt = entrustAmt.subtract(finishAmt);
                    }
                    if (prinAmt != null && entrustAmt != null) {
                        entrustAmt = entrustAmt.subtract(prinAmt);
                    }
                    if (entrustAmt != null && tradeAmt.compareTo(entrustAmt) == 0) {
                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("TRD_X_0004") + entrustAmt));
                    }
                    //add  wsz 修改数据为空判定失败问题 ZGCPCN-1139 end
                }
            }
            // 验证流程中是否存在该委托
            if (StringUtil.isNotEmpty(entrustId) && WorkFlowUtil.isNotFromToDoList()) {
                String str = tradeCommonService.check(entrustId, "TrdFundInvestTrade002");
                if (StringUtil.isNotEmpty(str)) {
                    ValidMessage validMessage = new ValidMessage(str);
                    errorList.add(validMessage);
                }
            }

            if (StringUtil.isNotEmpty(dto.getPs())) {
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    if (!StringUtil.isNotEmpty(dto.getSettleSpreed())) {
                        errorList.add(new ValidMessage("settleSpreed",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.settleSpreed"))));
                    }
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    if (shareAmt == null) {
                        errorList.add(new ValidMessage("shareAmt",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeSha"))));
                    }
                    if (tradeAmt == null) {
                        errorList.add(new ValidMessage("tradeAmt",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeAmt"))));
                    }
                }
                // 场内基金改造 modify by renping 20191117 start
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                ) {
                    // 场内基金改造 modify by renping 20191117 end
                    if (tradeAmt == null) {
                        errorList.add(new ValidMessage("tradeAmt",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeAmt"))));
                    }
                }
                else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    if (shareAmt == null) {
                        errorList.add(new ValidMessage("shareAmt",
                                this.getMessage("BUSS_COM_E_0038", this.getMessage("TrdFundInvestTrade002.tradeSha"))));
                    }
                }
                if (tradeAmt != null && tradeAmt.compareTo(zero) == -1) {
                    errorList.add(new ValidMessage("tradeAmt",
                            this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeAmt"), "0")));
                }
                if (shareAmt != null && shareAmt.compareTo(zero) == -1) {
                    errorList.add(new ValidMessage("shareAmt",
                            this.getMessage("BUSS_COM_E_0004", this.getMessage("TrdFundInvestTrade002.tradeSha"), "0")));
                }
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                    if(dto.getPortfolioId().equals("dzh")){
                        for(BussTrdTransfenpeiDto feipeiDto : dto.getBussTrdTransfenpeiDto()){
                            BigDecimal assetAvailablePosition = feipeiDto.getAssetsavailablepositions();
                            BigDecimal feipeishareAmt = feipeiDto.getShareAmt();
                            if (assetAvailablePosition != null && feipeishareAmt != null && feipeishareAmt.compareTo(assetAvailablePosition) == 1) {
                                errorList.add(new ValidMessage("shareAmt",
                                        this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeSha"),
                                                this.getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                            }
                        }
                    }else{
                        BigDecimal assetAvailablePosition = dto.getAssetAvailablePosition();
                        if (assetAvailablePosition != null && shareAmt != null && shareAmt.compareTo(assetAvailablePosition) == 1) {
                            errorList.add(new ValidMessage("shareAmt",
                                    this.getMessage("BUSS_COM_E_0003", this.getMessage("TrdFundInvestTrade002.tradeSha"),
                                            this.getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                        }
                    }
                }
                // 场内基金改造 modify by renping 20191117 start
                if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
                    // 场内基金改造 modify by renping 20191117 end
                    // 划款指令
                    if (bussTrdTransferOrderService != null) {
                        List<ValidMessage> list = bussTrdTransferOrderService.check(dto.getBussTrdTransferOrder());
                        errorList.addAll(list);
                    }
                }
            }
            /* 基金类资产交易必填CHECK取消 update by zhuyongkang 2019/09/18 start */
            // if (dto.getFileArray() == null || dto.getFileArray().size() <= 0) {
            // errorList.add(new ValidMessage(
            // getMessage("BUSS_COM_E_0038", getMessage("TrdFundInvestTrade002.file"))));
            // }
            /* 基金类资产交易必填CHECK取消 update by zhuyongkang 2019/09/18 end */
            if (dto.getTrdInvestPersonInfo() == null || StringUtil.isEmpty(dto.getTrdInvestPersonInfo().getCounterId())) {
                errorList.add(new ValidMessage("trdInvestPersonInfo.counterId", getMessage("BUSS_COM_E_0038", "交易对手")));
            }
            // else if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())){ 内部交易只能卖出
            // PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
            // ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
            // ptlSecPositionFlowDto.setPortfolioId(dto.getTrdInvestPersonInfo().getCounterId());
            // ptlSecPositionFlowDto.setSecAcctId(dto.getChlId());
            // ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
            // BigDecimal assetPosition = trdFundInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
            // //资产可用持仓小于交易份额
            // if (dto.getShareAmt() == null) {
            // errorList.add(new ValidMessage("unitCprice", getMessage("TRD_FUND_E_001")));
            // }
            // else if(assetPosition == null || assetPosition.compareTo(dto.getShareAmt())== -1){
            // errorList.add(new ValidMessage("",
            // getMessage("BUSS_COM_E_0003",
            // ptlSecPositionFlowDto.getPortfolioId()+" "+getMessage("TrdFundInvestTrade002.tradeSha"),
            // getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
            // }
            // //同时验证买入方是否有足够的现金
            // Map<String, Object> param = new HashMap<>();
            // param.put("portfolioId", dto.getPortfolioId());
            // param.put("ccy", dto.getCcy());
            // param.put("vdate", DateUtil.format(dto.getTradeDate()));
            // param.put("calId", dto.getCalendarId());
            // param.put("chlId", dto.getChlId());
            // Map<String, Object> para = trdFundInvestTrade002Service.getProAvailableCash(param);
            // BigDecimal accCash = new BigDecimal((String)para.get("cashT3"));
            // if(accCash != null && dto.getTradeAmt() != null && accCash.compareTo(dto.getTradeAmt()) < 0){
            // errorList.add(new ValidMessage("",
            // getMessage("BUSS_COM_E_0003",
            // param.get("portfolioId")+" "+getMessage("TrdFundInvestTrade002.tradeAmt"),
            // getMessage("TrdFundInvestTrade002.accAvailableCash"))));
            // }

            // }
            else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                param.put("portfolioId", dto.getTrdInvestPersonInfo().getCounterId());
                param.put("ccy", dto.getCcy());
                param.put("vdate", DateUtil.format(dto.getTradeDate()));
                param.put("calId", dto.getCalendarId());
                param.put("chlId", dto.getChlId());
                Map<String, Object> para = trdFundInvestTrade002Service.getProAvailableCash(param);
                BigDecimal accCash = new BigDecimal((String)para.get("cashT3"));
                // update by 张年 2019/08/26 start
                // 交易对手没有足够的现金
                /*
                 * if(StringUtil.isNotEmpty(dto.getTradeAmt())&&accCash.compareTo(dto.getTradeAmt())==-1){ errorList.add(new ValidMessage("",
                 * getMessage("BUSS_COM_E_0003", param.get("portfolioId")+"  "+getMessage("TrdFundInvestTrade002.tradeAmt"),
                 * getMessage("TrdFundInvestTrade002.accAvailableCash")))); }
                 */
                // update by 张年 2019/08/26 end
                // 同时验证卖出方是否有足够的持仓
                if (StringUtil.isNotEmpty(dto.getPortfolioId()) && dto.getTradeDate() != null
                        && StringUtil.isNotEmpty(dto.getFinprodId())) {
                    if(dto.getPortfolioId().equals("dzh")){
                        for(BussTrdTransfenpeiDto feipeiDto : dto.getBussTrdTransfenpeiDto()){
                            PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                            ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
                            ptlSecPositionFlowDto.setPortfolioId(feipeiDto.getPortfolioId());
                            // 场内基金改造 modify by renping 20191117 start
                            ptlSecPositionFlowDto.setSecAcctId(
                                    StringUtil.isEmpty(dto.getSecManageAcctId()) ? dto.getChlId() : dto.getSecManageAcctId());
                            // 场内基金改造 modify by renping 20191117 start
                            ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
                            BigDecimal assetPosition = trdFundInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
                            if (StringUtil.isNotEmpty(feipeiDto.getShareAmt()) && assetPosition.compareTo(feipeiDto.getShareAmt()) == -1) {
                                errorList.add(new ValidMessage("",
                                        getMessage("BUSS_COM_E_0003",
                                                ptlSecPositionFlowDto.getPortfolioId() + "  " + getMessage("TrdFundInvestTrade002.tradeSha"),
                                                getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                            }
                        }
                    }else {
                        PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                        ptlSecPositionFlowDto.setCdate(dto.getTradeDate());
                        ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
                        // 场内基金改造 modify by renping 20191117 start
                        ptlSecPositionFlowDto.setSecAcctId(
                                StringUtil.isEmpty(dto.getSecManageAcctId()) ? dto.getChlId() : dto.getSecManageAcctId());
                        // 场内基金改造 modify by renping 20191117 start
                        ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
                        BigDecimal assetPosition = trdFundInvestTrade002Service.getPtlSecPosition(ptlSecPositionFlowDto);
                        if (StringUtil.isNotEmpty(dto.getShareAmt()) && assetPosition.compareTo(dto.getShareAmt()) == -1) {
                            errorList.add(new ValidMessage("",
                                    getMessage("BUSS_COM_E_0003",
                                            ptlSecPositionFlowDto.getPortfolioId() + "  " + getMessage("TrdFundInvestTrade002.tradeSha"),
                                            getMessage("TrdFundInvestTrade002.assetAvailablePosition"))));
                        }
                    }
                }
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "revoke")) {
            TrdFundInvestTradeDto dtocopy = dto.getSelectedItem().get(0);
            if (StringUtil.equals(dtocopy.getValidStatus(), DictCodeListExt1.交易流程状态_撤销.getValue())
                    || StringUtil.equals(dtocopy.getValidStatus(), DictCodeListExt1.交易流程状态_流程中.getValue())) {
                errorList.add(new ValidMessage(this.getMessage("TRD_X_0005")));
            }
            if (StringUtil.equals(dtocopy.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                    && trdFundInvestTrade001Service.selRedemption(dtocopy.getTradeId()) != 0) {
                errorList.add(new ValidMessage(this.getMessage("TRD_X_0006")));
            }
            String ps = dtocopy.getPs();
            if (StringUtil.isNotEmpty(ps)
                    // 场内基金改造 modify by renping 20191117 start
                    && (((StringUtil.equals(ps, DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(ps, DictCodeList.交易方向_赎回.getValue())
                    || StringUtil.equals(ps, DictCodeListExt2.交易方向_转换申请.getValue())
                    // 流程中数据check 修正 add by renping 20191020 start
                    || StringUtil.equals(ps, DictCodeList.交易方向_赎回确认.getValue())
                    // 流程中数据check 修正 add by renping 20191020 end
            ) && !StringUtil.equals(map.get("trmFundType"), DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue()))
                    || StringUtil.equals(ps, DictCodeListExt2.交易方向_认购.getValue()))) {
                // 场内基金改造 modify by renping 20191117 end
                if (!trdFundInvestTrade001Service.revokeCheck(dtocopy)) {
                    errorList.add(new ValidMessage(this.getMessage("TRD_X_0007")));
                }
            }
            if (StringUtil.isNotEmpty(ps) && StringUtil.equals(ps, DictCodeList.交易方向_赎回到账.getValue())) {
                errorList.add(new ValidMessage(this.getMessage("TRD_X_0008")));
            }
            // if(StringUtil.isNotEmpty(ps) && StringUtil.equals(ps,
            // DictCodeList.交易方向_赎回确认.getValue())){
            // WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            // wflValidInputParameter.setBusinessDataPk1(dtocopy.getTradeId());
            // wflValidInputParameter.setBusinessDataPk2(dtocopy.getBusiType());
            // wflValidInputParameter.setPageId("TrdFundInvestTrade003");
            // boolean flag = mstWorkflowCommonService.isInWflProcess(wflValidInputParameter);
            // if(flag){
            // errorList.add(new ValidMessage("赎回到账交易在流程中不能撤销"));
            // }
            // }
        }

        //add by msk TISHKBS-1038 新增一次性划款通道交易前是否划款判断 start
        if(dto.getChlId()!=null&&!"".equals(dto.getChlId()) && dto.getTrdInvestPersonInfo()!=null){
            Integer count;
            Map<String,Object> sMap= new HashMap<String ,Object>();
            if(!StringUtil.equals(dto.getTrdInvestPersonInfo().getIsOutTrade(), DictCodeList.是否_否.getValue()) &&
                    (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) ||
                            StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) ||
                            StringUtil.equals(dto.getPs(), DictCodeList.交易方向_认购.getValue()))){
                sMap.put("chlId",dto.getChlId());
                sMap.put("finprodId",dto.getPortfolioId());
                count = trdFundInvestTrade002Service.selectchlTrade(sMap);
                if(count <= 0){
                    ValidMessage validMessage = new ValidMessage(getMessage("通过一次性通道投资请先进行划款！",getMessage("invest.channelId")));
                    errorList.add(validMessage);
                }

            }else if(StringUtil.equals(dto.getTrdInvestPersonInfo().getIsOutTrade(), DictCodeList.是否_否.getValue()) &&
                    (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()) ||
                            StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())     )){
                sMap.put("chlId",dto.getChlId());
                sMap.put("counterId",dto.getCounterId());
                count = trdFundInvestTrade002Service.selectchlTrade(sMap);
                if(count <= 0){
                    ValidMessage validMessage = new ValidMessage(getMessage("交易对手通过一次性通道投资请先进行划款！",getMessage("invest.channelId")));
                    errorList.add(validMessage);
                }
            }
        }

        if (!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            if (StringUtil.isNotEmpty(dto.getTradeAmt())) {
//                errorList.add(new ValidMessage("tradeAmt", getMessage("交易金额不能小于0")));
            }
        }

        //add by msk TISHKBS-1038 新增一次性划款通道交易前是否划款判断 end

        if (StringUtil.equals(dto.getOperateType(), "submit") || StringUtil.equals(dto.getOperateType(), "save") ){
            if (StringUtil.isNotEmpty(dto.getBussTrdTransfenpeiDto())){
                Map<String, List<BussTrdTransfenpeiDto>> map1 = dto.getBussTrdTransfenpeiDto().stream().collect(JoyinCollectors.groupingBy(item->item.getPortfolioId()));
                if (map1!=null && map1.keySet().size()<dto.getBussTrdTransfenpeiDto().size()){
                    errorList.add(new ValidMessage("组合分配不能重复分配给同一组合。"));
                }
            }

        }


        return errorList;
    }

}

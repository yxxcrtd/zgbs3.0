package com.joyintech.tams.invest.common.trade.service.impl;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.CommonResult;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioCashPositionsFlowInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实现类
 */
@Service // TODO 这里不要放开影响正常业务运行
public class BaseEtrCashPositionCommServiceImpl extends BaseService implements BaseEtrCashPositionCommService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 共通Service
     */
    @Autowired(required = false)
    private EtrCommonCashPositionDealService etrCommonCashPositionDealService;

    @Autowired
    private BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;
    
    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate platformDateImpl;
    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Override
    public CommonResult executeEtrCashPosition(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                               boolean needNegative,String ieFlag,String isOverdueDeal) {
        // modify by etliu 20220308 start
        // 场内的头寸处理，全部已经对接了头寸引擎，需要标记下
        if (finProductTradeDto.getTrdProductDeal() != null
                && (StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), finProductTradeDto.getTrdProductDeal().getTradeMarket())
                || StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), finProductTradeDto.getTrdProductDeal().getTradeMarket()))) {
            finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
        }
        // modify by etliu 20220308 end

        // 资产类型
        String finprodType = finProductTradeDto.getTrdProductDeal().getFinprodType2();
        // 处理结果
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
        // 持仓流水
        List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();
        // 现金流水
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();
        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
        etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
        etrPositionHoldDto.setNeedNegative(needNegative);
        Map<String, Object> vDateNato;
        // 2、资产二级类型
        if(StringUtil.isEmpty(finProductTradeDto.getAssetType2())){
            finProductTradeDto.setAssetType2(baseAssetCashPositionCommMapper.selectFundType(
                    finProductTradeDto.getTrdProductDeal().getFinprodId()));
        }
        String profitType = null;
        if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())) {
            vDateNato = baseAssetCashPositionCommMapper.selectVDateNato(finProductTradeDto.getTrdProductDeal().getFinprodId());
            /**
             * 收益类型
             */
            profitType = vDateNato == null ? null : (String)vDateNato.get("PROFIT_TYPE");
        }

        if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                && !(StringUtil.equals(finProductTradeDto.getTrdProductDeal().getPs(), DictCodeList.交易方向_申购确认.getValue())
                || StringUtil.equals(finProductTradeDto.getTrdProductDeal().getPs(), DictCodeListExt3.交易方向_债转股.getValue())
                // GNS-929:质押券出入库无需更新cash流水 modify by fuyongsheng 20220226 start
                || StringUtil.equals(finProductTradeDto.getTrdProductDeal().getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())
                || StringUtil.equals(finProductTradeDto.getTrdProductDeal().getPs(), DictCodeListExt3.交易方向_质押券出库.getValue()))
                // GNS-929:质押券出入库无需更新cash流水 modify by fuyongsheng 20220226 end
                // add by jiangpeng on 20210416 start
                // ZGBSS-1148 【债券借贷存续期】：到期交易中有“是否现金交割”的功能没有按照预期实现。
                && !StringUtil.equals(DictCodeList.是否_是.getValue(), finProductTradeDto.getIsSLDeal())
            // add by jiangpeng on 20210416 end
        ) {
            EtrSecCashPositionDealService.deal债券CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_存款.getValue())) {
            EtrDepoCashPositionDealService.deal存款CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                 && DictCodeList.收益类型_收益型.getValue().equals(profitType)) {
            EtrPlanRateCashPositionDealService.deal计划利率型CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                 || StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_拆出.getValue())) {
            EtrLendCashPositionDealService.genLendDealCashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())) {
            EtrProjectCashPositionDealService.deal项目类资产CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())
                || StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue())
                // add by jiangpeng on 20210421 start
                // ZGBSS-1148 【债券借贷存续期】：到期交易中有“是否现金交割”的功能没有按照预期实现。
                || StringUtil.equals(DictCodeList.是否_是.getValue(), finProductTradeDto.getIsSLDeal())) {
            // add by jiangpeng on 20210421 end
            EtrSecLendCashPositionDealService.deal债券借贷CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
            EtrFundCashPositionDealService.deal基金CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                 && (DictCodeList.收益类型_净值型.getValue().equals(profitType) || DictCodeList.收益类型_货币型.getValue().equals(profitType))) {
            EtrNetCashPositionDealService.deal计划类CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                 || StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
            EtrRepoCashPositionDealService.deal质押式回购CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto,ieFlag);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                 || StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())) {
            EtrBuyoutCashPositionDealService.deal买断式回购CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
            EtrSharesCashPositionDealService.deal股票CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                &&
                (
                    (StringUtil.equals(finProductTradeDto.getTrdProductDeal().getPs(),DictCodeList.交易方向_申购确认.getValue())
                    && DictCodeListExt2.申购方式_线下申购.getValue().equals(finProductTradeDto.getTrdProductDealAdd().getPurchModle())
                    )
                ||
                    StringUtil.equals(finProductTradeDto.getTrdProductDeal().getPs(), DictCodeListExt3.交易方向_债转股.getValue()))) {
            EtrConBondCashPositionDealService.deal可转债CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        // add by etliu 20210421 权益类资产相关代码合并 start
        else if(StringUtil.equals(finprodType, DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())){
            EtrNetCashPositionDealService.deal计划类CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);
        }
        // add by etliu 20210421 权益类资产相关代码合并 end
        else if (StringUtil.equals(finprodType, DictCodeListExt1.一次性费用类型_通道费.getValue())) {
            if (DictCodeList.持仓类型_实际.equals(持仓类型)) { // 有效
                TrdProductDeal deal = finProductTradeDto.getTrdProductDeal();
                TrdProductDealAdd dealAdd = finProductTradeDto.getTrdProductDealAdd();
                // 指令信息
                List<StlPayOrdersDto> stlPayOrdersDtos = finProductTradeDto.getStlPayOrdersList();
                // 托管账户
                String acctId = EtrCommonCashPositionDealService.select组合托管户(dealAdd.getPortfolioId(),
                        deal.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
                if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                    stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                        // 交易生成的现金流水
                        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
                                finProductTradeDto);
                        // 可用现金 到期日减少到账金额
                        etrCashPositionFlowDto.setAmount(ArithmeticUtil.multiply(stlPayOrdersDto.getSettleAmt(), new BigDecimal(-1)));
                        etrCashPositionFlowDto.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto.setCdate(stlPayOrdersDto.getSettleDate());
                        etrCashPositionFlowDto.setOrderId(stlPayOrdersDto.getOrderId());
                        etrCashPositionFlowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                        etrCashPositionFlowDto.setChlFinprodId(null);
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                    });
                }
            }
        }
        else if (DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue().equals(finprodType)) {
            EtrSecCashPositionDealService.deal信用保护凭证CashPosition(finProductTradeDto, 持仓类型, etrPositionHoldDto);

        }
        // 反转金额
        if (needNegative) {
            if (!etrCashPositionFlows.isEmpty()) {
                for (EtrCashPositionFlowDto etrCashPosition : etrCashPositionFlows) {
                    if (etrCashPosition.getAmount() != null) {
                        etrCashPosition.setAmount(etrCashPosition.getAmount().negate());
                    }
                    if (etrCashPosition.getOrderId().lastIndexOf('R') != etrCashPosition.getOrderId().length() - 1
                            && !StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) {
                        etrCashPosition.setOrderId(etrCashPosition.getOrderId() + "R");
                    }
                }
            }
            if (!etrSecPositionFlows.isEmpty()) {
                for (EtrSecPositionFlowDto etrSecPosition : etrSecPositionFlows) {
                    if (etrSecPosition.getAmount() != null) {
                        etrSecPosition.setAmount(etrSecPosition.getAmount().negate());
                    }
                    if (etrSecPosition.getTradeId().lastIndexOf('R') != etrSecPosition.getTradeId().length() - 1
                            && !StringUtil.equals(finProductTradeDto.getIsExistNew(), DictCodeList.是否_是.getValue())) {
                        etrSecPosition.setTradeId(etrSecPosition.getTradeId() + "R");
                    }
                }
            }
        }
        //逾期交易不需要维护持仓
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), isOverdueDeal)){
        	etrPositionHoldDto.setPtlSecPositionFlows(null);
        }
        // 数据是否已入新头寸
        if(StringUtil.isNotEmpty(finProductTradeDto.getIsExistNew())) {
            etrPositionHoldDto.getPtlCashPositionFlows().forEach(item->item.setIsExistNew(finProductTradeDto.getIsExistNew()));
            etrPositionHoldDto.getPtlSecPositionFlows().forEach(item->item.setIsExistNew(finProductTradeDto.getIsExistNew()));
        }

        // 维护入库流水及持仓
        etrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    @Override
    public CommonResult entrustStartExecute(EntrustCommonDto entrustCommonDto) {
        if (entrustCommonDto != null
                && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            // 处理结果
            EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
            // 持仓流水
            List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();
            // 现金流水
            List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();
            etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
            etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
            etrPositionHoldDto.setDealEtr(true);
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();

            // 资产类型
            String finprodType = etrEntrust.getFinprodType2();

            Map<String, Object> vDateNato;
            String profitType = null;
            if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())) {
                vDateNato = baseAssetCashPositionCommMapper.selectVDateNato(etrEntrust.getFinprodId());
                /**
                 * 收益类型
                 */
                profitType = vDateNato == null ? null : (String)vDateNato.get("PROFIT_TYPE");
            }

            if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType)
                    && !(StringUtil.equals(etrEntrust.getPs(),DictCodeList.交易方向_申购.getValue())
                    		||StringUtil.equals(etrEntrust.getPs(),DictCodeListExt1.交易方向_债转股.getValue()))) {
                EtrSecCashPositionDealService.deal债券EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_存款.getValue())) {
                EtrDepoCashPositionDealService.deal存款EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                    && DictCodeList.收益类型_收益型.getValue().equals(profitType)) {
                EtrPlanRateCashPositionDealService.deal计划利率型CEntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                     || StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_拆出.getValue())) {
                EtrLendCashPositionDealService.genLendEtrStartCashPosition(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())) {
                EtrProjectCashPositionDealService.deal项目类资产EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
                EtrSecPrimaryBiddingCashPositionDealService.deal债券一级投标EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                EtrFundCashPositionDealService.deal基金EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())
                     || StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue())) {
                EtrSecLendCashPositionDealService.deal债券借贷CEntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                    && (DictCodeList.收益类型_净值型.getValue().equals(profitType) || DictCodeList.收益类型_货币型.getValue().equals(profitType))) {
                EtrNetCashPositionDealService.deal计划类EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                     || StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                EtrRepoCashPositionDealService.deal质押式回购EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                     || StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())) {
                EtrBuyoutCashPositionDealService.deal买断式回购EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                EtrSharesCashPositionDealService.deal股票EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            //add by gaopeng 增加债转股委托资金头寸 start
            else if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType)
                    && (StringUtil.equals(etrEntrust.getPs(),DictCodeList.交易方向_申购.getValue())
                    		||StringUtil.equals(etrEntrust.getPs(),DictCodeListExt1.交易方向_债转股.getValue()))) {
                EtrConBondCashPositionDealService.deal可转债EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
          //add by gaopeng 增加债转股委托资金头寸 end
            // add by etliu 20210421 权益类资产相关代码合并 start
            //权益类资产
            else if(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(finprodType)){
                EtrNetCashPositionDealService.deal计划类EntrustStart(entrustCommonDto, etrPositionHoldDto);
            }
            // add by etliu 20210421 权益类资产相关代码合并 end
            // 数据是否已入新头寸
            if(StringUtil.isNotEmpty(entrustCommonDto.getIsExistNew())) {
                etrPositionHoldDto.getPtlCashPositionFlows().forEach(item->item.setIsExistNew(entrustCommonDto.getIsExistNew()));
                etrPositionHoldDto.getPtlSecPositionFlows().forEach(item->item.setIsExistNew(entrustCommonDto.getIsExistNew()));
            }
            EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
        }
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }
    @Override
    public CommonResult entrustCloseExecute(String entrustId){
        return this.entrustCloseExecute(entrustId, null,null);
    }

    @Override
    public CommonResult entrustCloseExecute(String entrustId, String isExistNew){
        return this.entrustCloseExecute(entrustId, isExistNew,null);
    }

    @Override
    public CommonResult entrustCloseExecute(String entrustId, String isExistNew, BigDecimal forcedFinishAmt) {
        if (StringUtil.isNotEmpty(entrustId)) {
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
            if (etrEntrust != null && etrEntrust.getEntrustClass() != null && StringUtil.equals(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue(), etrEntrust.getEntrustClass())) {
                List<EntrustCommonDto> entrustCommonDtoList = EtrCommonCashPositionDealService.findEntrustMulti(entrustId);
                if (!CollectionUtils.isEmpty(entrustCommonDtoList)) {
                    entrustCommonDtoList.stream().forEach(entrustCommonDto -> {
                        entrustCommonDto.setIsExistNew(isExistNew);
                        entrustCloseExecuteSinglePortfolio(entrustCommonDto);
                    });
                }
            } else {
                EntrustCommonDto entrustCommonDto = EtrCommonCashPositionDealService.findEntrust(entrustId);
                // 委托指令附属表查询
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
                if (StringUtil.isNotEmpty(forcedFinishAmt)) {
                    entrustCommonDto.setForcedFinishAmt(forcedFinishAmt);//强制完成额度
                }
                entrustCommonDto.setIsExistNew(isExistNew);
                entrustCloseExecuteSinglePortfolio(entrustCommonDto);
            }

        }
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    @Override
    public CommonResult executeBidResultsMaintenanceExecute(BidOrderBidPrice bidPriceDto) {
        // 处理结果
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
        // 现金流水
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();
        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
        // 债券一级投标中标结果维护
        EtrSecPrimaryBiddingCashPositionDealService.中标结果维护(bidPriceDto, etrPositionHoldDto);
//        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);//中标结果维护指令可用持仓不变
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    @Override
    public BigDecimal qryAvailablePosition(EtrSecPositionDto etrSecPositionDto) {
        return EtrCommonCashPositionDealService.selectAvailablePositionAmount(etrSecPositionDto);
    }

    @Override
    public BigDecimal qryCommandAvailableCash(PtlCashPositionFlowDto ptlCashPositionFlowDto) {
        return etrCommonCashPositionDealService.selectCommandAvailableCash(ptlCashPositionFlowDto);
    }

    @Override
    public BigDecimal qryCommandAdvanceCash(PtlCashPositionFlowDto ptlCashPositionFlowDto) {
        return etrCommonCashPositionDealService.selectCommandAdvanceCash(ptlCashPositionFlowDto);
    }

    // 标准券 入库
    @Override
    public CommonResult StandardBondExecute(List<EtrSecPositionFlowDto> list, DictCodeList 出入库方向) {
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
        // 持仓流水
        List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();
        // 现金流水
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();
        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
        etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
        EtrRepoCashPositionDealService.deal标准券_出入库CashPosition(list, 出入库方向, etrPositionHoldDto);
        // 维护入库流水及持仓
        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
        // 处理结果
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    /**
     * 产品端影响实时现金共通方法
     * // modify by jiangpeng on 20210429 start
     * // ZGBSS-1148 【债券借贷存续期】：到期交易中有“是否现金交割”的功能没有按照预期实现。
     * 
     * @param cashInputDto
     * @return
     */
    public CommonResult productEtrCashPosition(PortfolioCashPositionsFlowInputDto cashInputDto,String positionType) {
        String ps = cashInputDto.getTrdProductDeal().getPs();

        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);

        cashInputDto.getStlPayOrders().forEach(stlPayOrdersDto -> {
            // 现金流水
            EtrCashPositionFlowDto ret = new EtrCashPositionFlowDto();
            ret.setSeqno(IDGenerator.generate());
            ret.setCcy(cashInputDto.getTrdProductDeal().getCcy());
            ret.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
            ret.setChlFinprodId(cashInputDto.getTrdProductDeal().getChlId());
            ret.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
            ret.setPortfolioId(cashInputDto.getPortfolioId());
            ret.setCdate(cashInputDto.getTrdProductDeal().getTradeDate());
            String acctId = EtrCommonCashPositionDealService.select组合托管户(ret.getPortfolioId(), ret.getCcy(),
                DictCodeListExt1.资金账户类型_产品托管户.getValue());
            ret.setAcctId(acctId);
            ret.setOrderId(stlPayOrdersDto.getOrderId());
            // modify by jiangpeng on 20210429 start
            // ZGBSS-1148 【债券借贷存续期】：到期交易中有“是否现金交割”的功能没有按照预期实现。
            boolean flag = false;// 用于判断指令现金流水是否需要入库

            if ((StringUtil.equals(ps, DictCodeList.交易方向_产品认购.getValue())
                    || StringUtil.equals(ps, DictCodeList.交易方向_产品申购.getValue()))
                    && StringUtil.equals(DictCodeList.持仓类型_实际.getValue(), positionType)) {//流程生效,撤销调用
                ret.setAmount(stlPayOrdersDto.getSettleAmt());
                flag = true;
            } else if ((StringUtil.equals(ps, DictCodeList.交易方向_产品赎回.getValue())
                    || StringUtil.equals(ps, DictCodeList.交易方向_产品到期.getValue())
                    || StringUtil.equals(ps, DictCodeList.交易方向_产品现金分红.getValue())
                    || StringUtil.equals(ps, DictCodeList.交易方向_产品提前赎回.getValue())
                    || StringUtil.equals(ps, DictCodeList.交易方向_提前终止.getValue()))  //交易方向判断增加提前终止的判断
                    && (StringUtil.equals(DictCodeList.持仓类型_可用.getValue(), positionType) || cashInputDto.getNeedNegative())) {//流程中,撤销调用
                ret.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                flag = true;
            }
            if(cashInputDto.getNeedNegative()){//确定是撤销交易，交易金额取反
                ret.setAmount(ret.getAmount().negate());// 指令金额取反会对本身就需要取反的赎回等交易产生干扰,故交易金额取反
            }

            /*if(ret.getAmount()!=null && cashInputDto.getNeedNegative() && (StringUtil.equals(ps, DictCodeList.交易方向_产品赎回.getValue())
            		|| StringUtil.equals(ps, DictCodeList.交易方向_产品到期.getValue())
    				|| StringUtil.equals(ps, DictCodeList.交易方向_产品现金分红.getValue()))){//fixed BSTOZ-858bug
            	ret.setAmount(ret.getAmount().negate());//赎回撤销交易金额取反  add by zhouxiuping at 2019/10/31
            }*/// 上面已对撤销的交易金额取反,此处注释
            
            // 添加现金流水到目标dto
            if(flag) {
                etrPositionHoldDto.getPtlCashPositionFlows().add(ret);
            }
        });
        // modify by jiangpeng on 20210429 end
        // 维护入库流水及持仓
        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    @Override
    public CommonResult portfolioEtrCashPosition(PtlPositionHoldDto ptlPositionHoldDto, boolean 是否插入母组合持仓) {
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
        etrPositionHoldDto.setPtlSecPositionFlows(new ArrayList<>());

        if (ptlPositionHoldDto != null && ptlPositionHoldDto.getPtlCashPositionFlows() != null && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
            ptlPositionHoldDto.getPtlCashPositionFlows().forEach(cashInputDto -> {
                EtrCashPositionFlowDto ret = new EtrCashPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(ret, cashInputDto);
                ret.setSeqno(IDGenerator.generate());
                ret.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
                ret.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                etrPositionHoldDto.getPtlCashPositionFlows().add(ret);
                etrPositionHoldDto.setDealEtr(是否插入母组合持仓);
            });
        }


        if (ptlPositionHoldDto != null && ptlPositionHoldDto.getPtlSecPositionFlows() != null && !ptlPositionHoldDto.getPtlSecPositionFlows().isEmpty()) {
            ptlPositionHoldDto.getPtlSecPositionFlows().forEach(secInputDto -> {
                EtrSecPositionFlowDto ret = new EtrSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(ret, secInputDto);
                ret.setSeqno(IDGenerator.generate());
                ret.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                etrPositionHoldDto.getPtlSecPositionFlows().add(ret);
                etrPositionHoldDto.setDealEtr(是否插入母组合持仓);
            });
        }

        // 维护入库流水及持仓
        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);

        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    /**
     * 股票存续期撤销
     * @param tradeId
     * @return
     */
    public CommonResult revokePosition(String tradeId) {
        EtrPositionHoldDto etrPositionHoldDto = EtrCommonCashPositionDealService.getRevokePosition(tradeId);
        // 维护入库流水及持仓
        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    //单组合关闭指令头寸处理
    public void entrustCloseExecuteSinglePortfolio(EntrustCommonDto entrustCommonDto) {
        if (entrustCommonDto != null && entrustCommonDto.getEtrEntrust()!=null) {
            // 处理结果
            EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
            // 持仓流水
            List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();
            // 现金流水
            List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();
            etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
            etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
            etrPositionHoldDto.setDealEtr(true);
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            EtrEntrustAdd etrEntrustAdd = entrustCommonDto.getEtrEntrustAdd();
            // 资产类型
            String finprodType = etrEntrust.getFinprodType2();

            Map<String, Object> vDateNato;
            String profitType = null;
            if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())) {
                vDateNato = baseAssetCashPositionCommMapper.selectVDateNato(etrEntrust.getFinprodId());
                /**
                 * 收益类型
                 */
                profitType = vDateNato == null ? null : (String)vDateNato.get("PROFIT_TYPE");
            }

            if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(etrEntrust.getFinprodType2())
                    && !(StringUtil.equals(etrEntrust.getPs(),DictCodeList.交易方向_申购.getValue())
                    ||StringUtil.equals(etrEntrust.getPs(),DictCodeListExt1.交易方向_债转股.getValue()))) {
                EtrSecCashPositionDealService.deal债券EntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())) {
                EtrDepoCashPositionDealService.deal存款EntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                    && DictCodeList.收益类型_收益型.getValue().equals(profitType)) {
                EtrPlanRateCashPositionDealService.deal计划利率型CEntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
                    || StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆出.getValue())) {
                EtrLendCashPositionDealService.genLendEtrCloseCashPosition(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())) {
                EtrProjectCashPositionDealService.deal项目类资产EntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
                EtrSecPrimaryBiddingCashPositionDealService.deal债券一级投标EntrustEnd(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                EtrFundCashPositionDealService.deal基金EntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())
                    || StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue())) {
                EtrSecLendCashPositionDealService.deal债券借贷CEntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                    && (DictCodeList.收益类型_净值型.getValue().equals(profitType) || DictCodeList.收益类型_货币型.getValue().equals(profitType))) {
                EtrNetCashPositionDealService.deal计划类EntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                EtrRepoCashPositionDealService.deal质押式回购EntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
                    || StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())) {
                EtrBuyoutCashPositionDealService.deal买断式回购EntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                EtrSharesCashPositionDealService.deal股票EntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            else if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(etrEntrust.getFinprodType2())
                    && (StringUtil.equals(etrEntrust.getPs(),DictCodeList.交易方向_申购.getValue())||
                    StringUtil.equals(etrEntrust.getPs(),DictCodeListExt1.交易方向_债转股.getValue()))) {
                EtrEntrust etr = entrustCommonDto.getEtrEntrust();
                String payType = baseAssetCashPositionCommMapper.findPayTypeByProductId(etr.getChlId());
                EtrConBondCashPositionDealService.deal可转债EntrustClose(entrustCommonDto, etrPositionHoldDto,payType);
            }
            // add by etliu 20210421 权益类资产相关代码合并 start
            //权益类资产
            else if(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(finprodType)){
                EtrNetCashPositionDealService.deal计划类EntrustClose(entrustCommonDto, etrPositionHoldDto);
            }
            // add by etliu 20210421 权益类资产相关代码合并 end
            // 委托撤回或拒绝时，当前平台日处理实时持仓
            //协议是回购处理头寸  走配置化头寸共通
            for(EtrCashPositionFlowDto flowDto:etrPositionHoldDto.getPtlCashPositionFlows()){
                if(StringUtil.isEmpty(flowDto.getAcctId())){
                    logger.error("entrustId="+etrEntrust.getEntrustId()+" , AccId IS NULL");
                }
            }
            // 数据是否已入新头寸
            if(StringUtil.isNotEmpty(entrustCommonDto.getIsExistNew())) {
                etrPositionHoldDto.getPtlCashPositionFlows().forEach(item->item.setIsExistNew(entrustCommonDto.getIsExistNew()));
                etrPositionHoldDto.getPtlSecPositionFlows().forEach(item->item.setIsExistNew(entrustCommonDto.getIsExistNew()));
            }
            // 非新系统生成的旧头寸数据不入库
            List<String> finprodType2List = StringUtil.splitToList(",", CacheUtil.getSysParam(BussinessConstant.finprodType2List));
            if (finprodType2List.contains(etrEntrust.getFinprodType2())
                    && StringUtil.isEmpty(entrustCommonDto.getIsExistNew())
                    && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))
                    && StringUtil.equals(etrEntrust.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
                return;
            }
            if(etrEntrustAdd != null) {
                // 委托指令附属表查询
                etrEntrustAddMapper.updateByPrimaryKeySelective(etrEntrustAdd);
            }
            if(etrEntrustAdd != null && !StringUtil.equals(etrEntrustAdd.getAssetType1(), DictCodeList.回购类型_质押式协议回购.getValue())){
                etrPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> etrCashPositionFlowDto.setCdate(platformDateImpl.getSysDate()));
                etrPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> etrSecPositionFlowDto.setCdate(platformDateImpl.getSysDate()));
                EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
            }
        }
    }
    @Override
    public BigDecimal queryEtrAvailableCashNew(PtlCashPositionFlowDto ptlCashPositionFlowDto) {
        return etrCommonCashPositionDealService.queryEtrAvailableCashNew(ptlCashPositionFlowDto);
    }

}

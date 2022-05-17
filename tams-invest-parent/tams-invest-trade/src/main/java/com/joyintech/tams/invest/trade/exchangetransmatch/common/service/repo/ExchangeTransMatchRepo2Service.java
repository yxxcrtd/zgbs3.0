package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.TrdProductDealDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDetailDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dto.ExchangeTransMatchBaseTradeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 债券质押式协议回购（首期）
 * @author meifaxin
 * @date 2021/11/1
 * @see ExchangeTransMatchRepo1Service
 * @since 1.0
 */
@Service
public class ExchangeTransMatchRepo2Service extends ExchangeTransMatchRepoCommonService {
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Override
    public List<ExchangeTransMatchBaseTradeDto> genFinProductTradeDto(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto){
        List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtoList = new ArrayList<>();

        if(StringUtil.equals(entrustDto.getEntrustClass(),DictCodeListExt3.投资指令类型_多组合单资产指令.getValue())) {
//            TrdFinProductDealTotal trdFinProductDealTotal = null;
//            if(!StringUtil.equals(dealDto.getPs(),DictCodeList.交易方向_买入.getValue())) {
//                trdFinProductDealTotal = this.genTrdFinProductDealTotal(dealDto,entrustDto);
//            }

            for (ExchangeTransMatchCommonEntrustDetailDto detailDto:entrustDto.getEntrustDetailDtoList()) {
                if(detailDto.getMarchQty() == null || detailDto.getMarchQty().compareTo(BigDecimal.ZERO) <= 0){
                    continue;
                }
                ExchangeTransMatchBaseTradeDto baseTradeProductDealDto = new ExchangeTransMatchBaseTradeDto();

                ExchangeTransMatchCommonEntrustDto entrustDtoTmp = new ExchangeTransMatchCommonEntrustDto();
                BeanUtil.copyPropertiesIgnoreNull(entrustDtoTmp,entrustDto);
                entrustDtoTmp.setPortfolioId(detailDto.getPortfolioId());
                entrustDtoTmp.setMarchQty(detailDto.getMarchQty());
                entrustDtoTmp.setMarchAmt(detailDto.getMarchAmt());
                entrustDtoTmp.setMarchFeeAmtUnPay(detailDto.getMarchFeeAmtUnPay());
                entrustDtoTmp.setMarchFeeAmtPay(detailDto.getMarchFeeAmtPay());

//                //生成汇总表数据
//                if(!StringUtil.equals(dealDto.getPs(),DictCodeList.交易方向_买入.getValue())) {
//                    baseTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
//                }

                //生成金融产品交易信息
                baseTradeProductDealDto.setTrdProductDeal(this.genTrdProductDeal(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdFinProductDealTotal()));
                //生成金融产品交易附属表信息
                baseTradeProductDealDto.setTrdProductDealAdd(this.genTrdProductDealAdd(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成组合交易表数据
                baseTradeProductDealDto.setPtlTrade(this.generatePtlTrade(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成费用信息list
                baseTradeProductDealDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成金融产品交易扩展
                baseTradeProductDealDto.setPaTrdProductDeal(this.genPaTrdProductDeal(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成划款指令
                baseTradeProductDealDto.setStlPayOrdersList(this.generateStlPayOrdersList(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));

                SettleTransData settleTransData = new SettleTransData();
                BeanUtil.copyPropertiesIgnoreNull(settleTransData,dealDto);
                baseTradeProductDealDto.setFinTradeProduct(this.genFinTradeProduct(settleTransData, baseTradeProductDealDto.getTrdProductDeal()));
                baseTradeProductDealDto.setFinProductType(this.genFinProductType(dealDto,baseTradeProductDealDto.getTrdProductDeal()));
                baseTradeProductDealDto.setFinDealPledgeList(this.genFinDealPledgeList(dealDto,baseTradeProductDealDto.getTrdProductDeal()));

                baseTradeProductDealDtoList.add(baseTradeProductDealDto);
            }
        }
        else {
            ExchangeTransMatchBaseTradeDto baseTradeProductDealDto = new ExchangeTransMatchBaseTradeDto();
//            //首期不入汇总表
//            if(!StringUtil.equals(dealDto.getPs(),DictCodeList.交易方向_买入.getValue())) {
//                baseTradeProductDealDto.setTrdFinProductDealTotal(this.genTrdFinProductDealTotal(dealDto,entrustDto));
//            }
            //生成金融产品交易信息
            baseTradeProductDealDto.setTrdProductDeal(this.genTrdProductDeal(dealDto,entrustDto,baseTradeProductDealDto.getTrdFinProductDealTotal()));
            //生成金融产品交易附属表信息
            baseTradeProductDealDto.setTrdProductDealAdd(this.genTrdProductDealAdd(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            //生成组合交易表数据
            baseTradeProductDealDto.setPtlTrade(this.generatePtlTrade(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            //生成费用信息list
            baseTradeProductDealDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            //生成划款指令
            baseTradeProductDealDto.setStlPayOrdersList(this.generateStlPayOrdersList(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            //生成金融产品交易扩展
            baseTradeProductDealDto.setPaTrdProductDeal(this.genPaTrdProductDeal(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));

            SettleTransData settleTransData = new SettleTransData();
            BeanUtil.copyPropertiesIgnoreNull(settleTransData,dealDto);
            settleTransData.setCjbh(dealDto.getApplyTradeId());
            baseTradeProductDealDto.setFinTradeProduct(this.genFinTradeProduct(settleTransData, baseTradeProductDealDto.getTrdProductDeal()));
            baseTradeProductDealDto.setFinProductType(this.genFinProductType(dealDto,baseTradeProductDealDto.getTrdProductDeal()));
            baseTradeProductDealDto.setFinDealPledgeList(this.genFinDealPledgeList(dealDto,baseTradeProductDealDto.getTrdProductDeal()));

            baseTradeProductDealDtoList.add(baseTradeProductDealDto);
        }

        return baseTradeProductDealDtoList;
    }


    /**
     * 生成汇总交易
     * @param dealDto
     * @param entrustDto
     * @return
     */
    @Override
    public TrdFinProductDealTotal genTrdFinProductDealTotal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto) {
        TrdFinProductDealTotal dealTotal = new TrdFinProductDealTotal();

        // 交易编号，按现有的交易编号生成规则，自动生成
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), dealDto.getFinprodType2())) {
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
        }   else {
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
        }
        dealTotal.setTotleTradeId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));

        // 业务类型
        dealTotal.setBusiType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        // 金融产品代码
        dealTotal.setFinprodId(entrustDto.getFinprodId());
        // 单位净价
        dealTotal.setUnitCprice(dealDto.getUnitCprice());
        // 单位利息
        dealTotal.setUnitInt(dealDto.getUnitInt());
        // 单位全价
        dealTotal.setUnitFprice(dealDto.getUnitFprice());
        // 单位面值
        dealTotal.setParValue(dealDto.getParValue());

        // 净价总额
        dealTotal.setCpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitCprice()));
        // 全价总额
        dealTotal.setFpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitFprice()));
        // 交易本金
        dealTotal.setPrinAmt(ArithmeticUtil.subtract(dealTotal.getFpriceAmt(),entrustDto.getMarchFeeAmtPay()));
        // 交易金额
        dealTotal.setTradeAmt(ArithmeticUtil.subtract(entrustDto.getMarchAmt(),entrustDto.getMarchFeeAmtPay()));
        // 利息总额
        dealTotal.setIntAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitInt()));
        // 随交易支付费用
        dealTotal.setFeeAmtPay(entrustDto.getMarchFeeAmtPay());
        // 未随交易支付费用
        dealTotal.setFeeAmtUnpay(entrustDto.getMarchFeeAmtPay());
        // 到期收益率--在TrdProductDeal生成的时候同时设值
        dealTotal.setYield(null);
        // 行权收益率--在TrdProductDeal生成的时候同时设值
        dealTotal.setExerYield(null);
        // 到期日
        dealTotal.setMdate(dealDto.getMdate());
        // 交割日
        dealTotal.setSettleDate(dealDto.getSettleDate());
        // 清算速度
        dealTotal.setSettleSpeed(StringUtil.isNotEmpty(dealDto.getSettleSpeed())?dealDto.getSettleSpeed():0L);
        // 交易币种
        dealTotal.setCcy(dealDto.getCcy());
        // 首期结算方式
        dealTotal.setDeliveryType(null);
        // 投管业务类型
        dealTotal.setTgBusiType(null);
        // 利率
        dealTotal.setRate(dealDto.getIntRate());
        // 回购期限(天)
        dealTotal.setRepoTerm(null);
        // 期限品种
        dealTotal.setTermType(dealDto.getTermType());

        // 金融产品类型（投管分类）
        dealTotal.setFinprodType2(entrustDto.getFinprodType2());
        // 分支序号
        dealTotal.setBranch(0L);
        // 交易日
        dealTotal.setTradeDate(dealDto.getTradeDate());
        // 起息日
        dealTotal.setVdate(dealDto.getVdate());
        // 交易份额
        dealTotal.setShareAmt(entrustDto.getMarchQty());
        // 总交易费用
        dealTotal.setFeeAmt(ArithmeticUtil.doAdd(entrustDto.getMarchFeeAmtPay(),entrustDto.getMarchFeeAmtPay()));
        // 交易方向
        dealTotal.setPs(entrustDto.getPs());
        // 投资目的
        dealTotal.setInvAim(entrustDto.getInvAim());
        // 交易状态
        dealTotal.setTradeStatus(BussinessDictCodeList.交易状态_初始.getValue());
        // 是否已撤销
        dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        // 证券管理户代码
        dealTotal.setSecManageAcctId(dealDto.getSecAcctId());
        // 前置交易编号
        dealTotal.setbTradeId(null);
        // 是否外部交易
        dealTotal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        dealTotal.setTradeMarket(dealDto.getTradeMarket());

        if(StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            dealTotal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
        }
        else if(StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
            dealTotal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
        }

        // 交易平台
        dealTotal.setTradePlat(dealDto.getTradePlat());
        // 交易市场
        dealTotal.setTradeMarketMode(dealDto.getTradeMarketMode());
        // 投资组合代码
        dealTotal.setPortfolioId(entrustDto.getPortfolioId());
        // 备注
        dealTotal.setRemark(entrustDto.getRemark());
        //是否逾期
        dealTotal.setIsOverdueDeal(DictCodeList.是否_否.getValue());

        return dealTotal;
    }

    /**
     * 生成金融产品交易明细
     */

    public TrdProductDeal genTrdProductDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        // 创建交易信息
        TrdProductDealDto trdProductDeal = createProductDeal(dealDto);
        // 业务类型
        trdProductDeal.setBusiType(null);
        // 金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(dealDto.getFinprodType2());
        // 金融产品代码
        trdProductDeal.setFinprodId(trdProductDeal.getTradeId());
        // 到期日
        trdProductDeal.setMdate(dealDto.getMdate());
        // 交付日
        trdProductDeal.setPayDate(dealDto.getVpayDate());
        // 交割日
        trdProductDeal.setSettleDate(dealDto.getSettleDate());
        // 清算速度
        trdProductDeal.setSettleSpeed(StringUtil.isNotEmpty(dealDto.getSettleSpeed())?dealDto.getSettleSpeed():0L);
        // 单位净价
        trdProductDeal.setUnitCprice(null);
        // 交易本金
        trdProductDeal.setPrinAmt(entrustDto.getMarchQty());
        // 利息总额
        trdProductDeal.setIntAmt(dealDto.getIntAmt());
        // 全价总额
        trdProductDeal.setFpriceAmt(entrustDto.getMarchAmt());
        // 随交易支付费用
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), dealDto.getIsPayWithTrade())) {
            trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(dealDto.getJsfAmt(), dealDto.getGhfAmt(), dealDto.getZgfAmt(), dealDto.getSxfAmt(), dealDto.getSettleFee(), dealDto.getStampFee(), dealDto.getCommissionAmt()));
        }else {
            trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(dealDto.getJsfAmt(), dealDto.getGhfAmt(), dealDto.getZgfAmt(), dealDto.getSxfAmt(), dealDto.getSettleFee(), dealDto.getStampFee()));
        }
        // 交易金额 正回购：匹配金额 - 随交易支付费用 逆回购：匹配金额+ 随交易支付费用
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), dealDto.getFinprodType2())) {
            trdProductDeal.setTradeAmt(ArithmeticUtil.subtract(entrustDto.getMarchAmt(),trdProductDeal.getFeeAmtPay()));
        }   else {
            trdProductDeal.setTradeAmt(ArithmeticUtil.doAdd(entrustDto.getMarchAmt(),trdProductDeal.getFeeAmtPay()));
        }

        //交易份额
        trdProductDeal.setShareAmt(entrustDto.getMarchAmt());
        // 未随交易支付费用
        if(!StringUtil.equals(DictCodeList.是否_是.getValue(), dealDto.getIsPayWithTrade())) {
            trdProductDeal.setFeeAmtUnpay(dealDto.getCommissionAmt());
        }
        // 总交易费用
        trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(trdProductDeal.getFeeAmtPay(), trdProductDeal.getFeeAmtUnpay()));
        // 交易方向
        trdProductDeal.setPs(dealDto.getPs());
        // 利息总额
        trdProductDeal.setIntAmt(null);
        // 全价总额
        trdProductDeal.setFpriceAmt(null);
        // 交易方向
        trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
        // 投管业务类型
        if(StringUtil.equals(dealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_正回购.getValue());
        }
        else {
            trdProductDeal.setTgBusiType(DictCodeListExt1.平安业务类型_逆回购.getValue());
        }
        //汇总交易编号
        if(StringUtil.equals(entrustDto.getEntrustClass(), DictCodeListExt3.投资指令类型_多组合单资产指令.getValue())) {
            trdProductDeal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
            trdFinProductDealTotal.setTgBusiType(trdProductDeal.getTgBusiType());
        }
        return trdProductDeal;
    }


    public List<StlPayOrdersDto> generateStlPayOrdersList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();
        StlPayOrdersDto stlPayOsrdersDto = createStlPayOrders(dealDto, entrustDto, trdProductDeal);
        // 收付方向
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), dealDto.getFinprodType2())) {
            stlPayOsrdersDto.setPs(DictCodeListExt1.收付方向_支出.getValue());
        } else {
            stlPayOsrdersDto.setPs(DictCodeList.收付方向_收入.getValue());
        }
        stlPayOrdersList.add(stlPayOsrdersDto);
        return stlPayOrdersList;
    }


    public List<FinDealPledge> genFinDealPledgeList(ExchangeTransMatchCommonDto dealDto,TrdProductDeal trdProductDeal){
        List<FinDealPledge> finDealPledgeList = new ArrayList<>();
        if(!StringUtil.isEmptyList(dealDto.getSettleTransDataTotalPledgeList())) {
            for(SettleTransDataTotalPledge transDataTotalPledge:dealDto.getSettleTransDataTotalPledgeList()) {
                FinDealPledge finDealPledge = new FinDealPledge();
                finDealPledge.setFinprodId(trdProductDeal.getFinprodId());
                finDealPledge.setBranch(trdProductDeal.getBranch());
                finDealPledge.setPledgeFinprodId(transDataTotalPledge.getPledgeFinprodId());
                finDealPledge.setPledgeFaceValue(transDataTotalPledge.getPledgeFaceValue());
                finDealPledge.setPledgeNumber(transDataTotalPledge.getPledgeNumber());
                finDealPledge.setPledgeRatio(transDataTotalPledge.getPledgeRatio());
                finDealPledge.setPledgeAmt(transDataTotalPledge.getPledgeAmt());
                finDealPledgeList.add(finDealPledge);
            }
        }

        return finDealPledgeList;
    }
}

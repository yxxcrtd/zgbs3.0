package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.repo;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalPledge;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.TrdProductDealDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDetailDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dto.ExchangeTransMatchBaseTradeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 质押式协议回购（中途换券,解除质押）
 */
@Service
public class ExchangeTransMatchRepo3Service extends ExchangeTransMatchRepoCommonService {
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 生成金融产品交易信息实体
     * @param dealDto
     * @param entrustDto
     * @return
     */
    @Override
    public List<ExchangeTransMatchBaseTradeDto> genFinProductTradeDto(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto) {
        List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtoList = new ArrayList<>();
        //生成汇总表数据
        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(dealDto, entrustDto);
        TrdDealTotalAdd trdDealTotalAdd = this.generateDealTotalAdd(dealDto, entrustDto,trdFinProductDealTotal);

        List<TrdProductDeal> dealList = this.genTrdProductDealList(dealDto, entrustDto,trdFinProductDealTotal);


        for (TrdProductDeal trdProductDeal : dealList) {
            ExchangeTransMatchBaseTradeDto baseTradeProductDealDto = new ExchangeTransMatchBaseTradeDto();
            baseTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
            //生成汇总表数据
            baseTradeProductDealDto.setTrdDealTotalAdd(trdDealTotalAdd);
            //生成金融产品交易信息
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
            //生成金融产品交易附属表信息
            baseTradeProductDealDto.setTrdProductDealAdd(this.genTrdProductDealAdd(dealDto, entrustDto, trdProductDeal));
            //生成组合交易表数据
            baseTradeProductDealDto.setPtlTrade(this.generatePtlTrade(dealDto, entrustDto, trdProductDeal));
            //生成金融产品交易扩展
            baseTradeProductDealDto.setPaTrdProductDeal(this.genPaTrdProductDeal(dealDto, entrustDto, trdProductDeal));

            baseTradeProductDealDtoList.add(baseTradeProductDealDto);
        }
        return baseTradeProductDealDtoList;
    }

    /**
     * 生成金融产品交易汇总信息
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
        dealTotal.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());
        // 金融产品代码
        TrdProductDeal firstRepoTrade = this.getFirstRepoTrade(dealDto.getfCjbh(),dealDto.getVdate(),DictCodeList.交易方向_买入.getValue(),
                entrustDto.getPortfolioId());
        if(StringUtil.isNotEmpty(firstRepoTrade)) {
            //金融产品代码
            dealTotal.setFinprodId(firstRepoTrade.getFinprodId());
            //金融产品类型
            dealTotal.setFinprodType(firstRepoTrade.getFinprodType());
            //金融产品类型投管
            dealTotal.setFinprodType2(firstRepoTrade.getFinprodType2());
        }
        // 分支序号
        dealTotal.setBranch(0L);
        // 交易日
        dealTotal.setTradeDate(dealDto.getTradeDate());
        // 起息日
        dealTotal.setVdate(dealDto.getVdate());
        // 到期日
        dealTotal.setMdate(null);
        //交付日
        dealTotal.setPayDate(null);
        // 交割日
        dealTotal.setSettleDate(dealDto.getTradeDate());
        // 清算速度
        dealTotal.setSettleSpeed(StringUtil.isNotEmpty(dealDto.getSettleSpeed())?dealDto.getSettleSpeed():0L);
        // 交易币种
        dealTotal.setCcy(null);

        // 单位净价
        dealTotal.setUnitCprice(null);
        // 单位利息
        dealTotal.setUnitInt(null);
        // 单位全价
        dealTotal.setUnitFprice(null);
        // 单位面值
        dealTotal.setParValue(null);
        // 净价总额
        dealTotal.setCpriceAmt(null);
        // 交易份额
        dealTotal.setShareAmt(entrustDto.getMarchQty());
        // 交易本金
        dealTotal.setPrinAmt(null);
        // 利息总额
        dealTotal.setIntAmt(null);
        // 全价总额
        dealTotal.setFpriceAmt(null);
        // 随交易支付费用
        dealTotal.setFeeAmtPay(null);
        // 交易金额
        dealTotal.setTradeAmt(null);
        // 未随交易支付费用
        dealTotal.setFeeAmtUnpay(null);
        // 总交易费用
        dealTotal.setFeeAmt(null);
        // 交易方向
        dealTotal.setPs(entrustDto.getPs());
        // 到期收益率
        dealTotal.setYield(null);
        // 行权收益率
        dealTotal.setExerYield(null);
        // 首期结算方式
        dealTotal.setDeliveryType(null);
        // 交易状态
        dealTotal.setTradeStatus(BussinessDictCodeList.交易状态_初始.getValue());
        // 是否已撤销
        dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        // 证券管理户代码
        dealTotal.setSecManageAcctId(dealDto.getSecAcctId());
        //关联系统交易编号
        dealTotal.setfTradeId(dealDto.getSeqId());
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

        // 投资组合代码
        dealTotal.setPortfolioId(entrustDto.getPortfolioId());
        // 投管业务类型
        if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(dealTotal.getFinprodType2())){
            dealTotal.setTgBusiType(DictCodeListExt3.平安业务类型_债券质押式正回购.getValue());
        }else{
            dealTotal.setTgBusiType(DictCodeListExt3.平安业务类型_债券质押式逆回购.getValue());
        }
        // dealTotal.setTgBusiType(DictCodeListExt3.共通业务类型_质押券置换.getValue());

        //交易市场
        dealTotal.setTradeMarketMode(dealDto.getTradeMarketMode());
        //是否逾期
        dealTotal.setIsOverdueDeal(DictCodeList.是否_否.getValue());
        // 备注
        dealTotal.setRemark("数据清算自动处理");

        // 处理换钱场景
        if(DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换钱.getValue().equals(dealDto.getBusiSceneCalss())){
            dealTotal.setShareAmt(null);
            dealTotal.setPrinAmt(dealDto.getPrinAmt());
            dealTotal.setTradeAmt(dealDto.getPrinAmt());
        }
        return dealTotal;
    }


    private List<TrdProductDeal> genTrdProductDealList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        List<TrdProductDeal> trdProductDealList =  new ArrayList<>();
        if(DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换钱.getValue().equals(dealDto.getBusiSceneCalss())){
            TrdProductDealDto trdProductDeal = this.createTrdProductDealCommon(dealDto, entrustDto, trdFinProductDealTotal);
            // 金融产品ID
            trdProductDeal.setFinprodId(trdFinProductDealTotal.getFinprodId());
            // 交易方向
            trdProductDeal.setPs(entrustDto.getPs());
            // 交易金额
            trdProductDeal.setTradeAmt(dealDto.getPrinAmt());
            trdProductDealList.add(trdProductDeal);
            return trdProductDealList;
        }
        for(SettleTransDataTotalPledge settleTransDataTotalPledge:dealDto.getSettleTransDataTotalPledgeList()) {
            TrdProductDealDto trdProductDeal = this.createTrdProductDealCommon(dealDto, entrustDto, trdFinProductDealTotal);
            //金融产品ID
            trdProductDeal.setFinprodId(settleTransDataTotalPledge.getPledgeFinprodId());
            //单位净价
            trdProductDeal.setUnitCprice(settleTransDataTotalPledge.getPledgeRatio());
            //交易方向
            trdProductDeal.setPs(settleTransDataTotalPledge.getDisplaceDirection());
            //交易份额 中途换券换入使用匹配份额，换出不需要处理直接使用质押券数量
            if(StringUtil.equals(settleTransDataTotalPledge.getDisplaceDirection(),DictCodeListExt3.质押券置换_置换方向_换出.getValue())) {
                trdProductDeal.setShareAmt(settleTransDataTotalPledge.getPledgeNumber());
            }
            else {
                trdProductDeal.setShareAmt(entrustDto.getMarchQty());
            }
            trdProductDealList.add(trdProductDeal);
        }

        return trdProductDealList;
    }

    /**
     * 中途换券的换券和换钱共通交易字段
     * @param dealDto
     * @param entrustDto
     * @param trdFinProductDealTotal
     * @return
     */
    private TrdProductDealDto createTrdProductDealCommon(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto,
                                                         TrdFinProductDealTotal trdFinProductDealTotal){
        TrdProductDealDto trdProductDeal = new TrdProductDealDto();
        // 交易编号，按现有的交易编号生成规则，自动生成
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), dealDto.getFinprodType2())) {
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
        }   else {
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
        }
        trdProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));
        trdProductDeal.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());
        // 金融产品类型
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 分支序号
        trdProductDeal.setBranch(0L);
        // 交易日
        trdProductDeal.setTradeDate(dealDto.getTradeDate());
        // 起息日
        trdProductDeal.setVdate(dealDto.getVdate());
        // 到期日
        trdProductDeal.setMdate(null);
        //支付日
        trdProductDeal.setPayDate(null);
        //交割日
        trdProductDeal.setSettleDate(dealDto.getTradeDate());
        //清算速度
        trdProductDeal.setSettleSpeed(StringUtil.isNotEmpty(dealDto.getSettleSpeed())?dealDto.getSettleSpeed():0L);
        // 交易币种
        trdProductDeal.setCcy(dealDto.getCcy());
        // 净值日期
        trdProductDeal.setCpriceDate(null);
        // 单位利息
        trdProductDeal.setUnitInt(null);
        // 单位全价
        trdProductDeal.setUnitFprice(null);
        // 单位面值
        trdProductDeal.setParValue(null);
        // 净价总额
        trdProductDeal.setCpriceAmt(null);
        // 投资目的
        trdProductDeal.setInvAim(null);
        // 交易状态
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 原交易编号
        trdProductDeal.setpTradeId(null);
        // 是否结清
        trdProductDeal.setIsClean(null);
        // 反向交易编号
        trdProductDeal.setrTradeId(null);
        // 通道代码
        trdProductDeal.setChlId(null);
        // 交易对手
        trdProductDeal.setCounterId(null);
        // 是否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        trdProductDeal.setTradeMarket(dealDto.getTradeMarket());
        // 交易平台
        trdProductDeal.setTradePlat(dealDto.getTradePlat());
        // 交易市场
        trdProductDeal.setTradeMarketMode(dealDto.getTradeMarketMode());

        if(StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
        }
        else if(StringUtil.equals(dealDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
            trdProductDeal.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
        }
        // 本方交易员
        trdProductDeal.setTrader(null);
        // 对手方交易员
        trdProductDeal.setCounterTrader(null);
        // 对手方联系方式
        trdProductDeal.setCounterContact(null);
        // 证券管理户代码
        trdProductDeal.setSecManageAcctId(dealDto.getSecAcctId());
        // 关联系统交易编号
        trdProductDeal.setfTradeId(null);
        // 现金流代码
        trdProductDeal.setCashId(null);
        // 登记托管机构
        trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
        // 备注
        trdProductDeal.setRemark(null);
        // 撤销说明
        trdProductDeal.setCancelRemark(null);
        // 销售代码
        trdProductDeal.setSaleCode(null);
        // 计提份额
        trdProductDeal.setAccrNum(null);
        // 交收方式
        trdProductDeal.setSettlemenType(null);
        // 席位号
        trdProductDeal.setSeatId(dealDto.getSeatNumber());
        // 投资组合代码
        trdProductDeal.setPortfolioId(entrustDto.getPortfolioId());

        trdProductDeal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        return trdProductDeal;
    }
}

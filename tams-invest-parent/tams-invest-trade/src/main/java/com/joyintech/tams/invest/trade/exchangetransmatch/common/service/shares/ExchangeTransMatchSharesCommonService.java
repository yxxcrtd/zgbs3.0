package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.shares;

import com.joyintech.background.component.common.pack.common.base.BaseService;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDetailDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dto.ExchangeTransMatchBaseTradeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeTransMatchSharesCommonService extends BaseService {

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    public List<ExchangeTransMatchBaseTradeDto> genFinProductTradeDto(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto){
        List<ExchangeTransMatchBaseTradeDto> exchangeTransMatchBaseTradeDtoList = new ArrayList<>();
        if(StringUtil.equals(entrustDto.getEntrustClass(),DictCodeListExt3.投资指令类型_多组合单资产指令.getValue())) {
            //生成汇总表数据
            TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(dealDto,entrustDto);
            for (ExchangeTransMatchCommonEntrustDetailDto detailDto:entrustDto.getEntrustDetailDtoList()) {
                if(detailDto.getMarchQty() == null || detailDto.getMarchQty().compareTo(BigDecimal.ZERO) <= 0){
                    continue;
                }
                ExchangeTransMatchBaseTradeDto exchangeTransMatchBaseTradeDto = new ExchangeTransMatchBaseTradeDto();

                ExchangeTransMatchCommonEntrustDto entrustDtoTmp = new ExchangeTransMatchCommonEntrustDto();
                BeanUtil.copyPropertiesIgnoreNull(entrustDtoTmp, entrustDto);
                entrustDtoTmp.setPortfolioId(detailDto.getPortfolioId());
                entrustDtoTmp.setMarchQty(detailDto.getMarchQty());
                entrustDtoTmp.setMarchAmt(detailDto.getMarchAmt());
                entrustDtoTmp.setMarchFeeAmtUnPay(detailDto.getMarchFeeAmtUnPay());
                entrustDtoTmp.setMarchFeeAmtPay(detailDto.getMarchFeeAmtPay());

                exchangeTransMatchBaseTradeDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
                //生成金融产品交易信息
                exchangeTransMatchBaseTradeDto.setTrdProductDeal(this.genTrdProductDeal(dealDto,entrustDtoTmp,exchangeTransMatchBaseTradeDto.getTrdFinProductDealTotal()));
                //生成金融产品交易信息附属表
                exchangeTransMatchBaseTradeDto.setTrdProductDealAdd(this.genTrdProductDealAdd(dealDto,entrustDtoTmp,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
                //生成组合交易表数据
                exchangeTransMatchBaseTradeDto.setPtlTrade(this.generatePtlTrade(dealDto,entrustDtoTmp,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
                //生成费用信息list
                exchangeTransMatchBaseTradeDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(dealDto,entrustDtoTmp,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
                //生成划款指令
                exchangeTransMatchBaseTradeDto.setStlPayOrdersList(this.generateStlPayOrdersList(dealDto,entrustDtoTmp,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
                //生成金融产品交易扩展
                exchangeTransMatchBaseTradeDto.setPaTrdProductDeal(this.genPaTrdProductDeal(dealDto, entrustDto, exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
                exchangeTransMatchBaseTradeDtoList.add(exchangeTransMatchBaseTradeDto);
            }
        }
        else {
            ExchangeTransMatchBaseTradeDto exchangeTransMatchBaseTradeDto=new ExchangeTransMatchBaseTradeDto();
            //生成金融产品交易信息
            exchangeTransMatchBaseTradeDto.setTrdProductDeal(this.genTrdProductDeal(dealDto,entrustDto,exchangeTransMatchBaseTradeDto.getTrdFinProductDealTotal()));
            //生成金融产品交易信息附属表
            exchangeTransMatchBaseTradeDto.setTrdProductDealAdd(this.genTrdProductDealAdd(dealDto,entrustDto,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
            //生成组合交易表数据
            exchangeTransMatchBaseTradeDto.setPtlTrade(this.generatePtlTrade(dealDto,entrustDto,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
            //生成费用信息list
            exchangeTransMatchBaseTradeDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(dealDto,entrustDto,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
            //生成划款指令
            exchangeTransMatchBaseTradeDto.setStlPayOrdersList(this.generateStlPayOrdersList(dealDto,entrustDto,exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
            //生成金融产品交易扩展
            exchangeTransMatchBaseTradeDto.setPaTrdProductDeal(this.genPaTrdProductDeal(dealDto, entrustDto, exchangeTransMatchBaseTradeDto.getTrdProductDeal()));
            exchangeTransMatchBaseTradeDtoList.add(exchangeTransMatchBaseTradeDto);
        }

        return exchangeTransMatchBaseTradeDtoList;
    }


    /**
     * 生成汇总交易
     * @param dealDto
     * @param entrustDto
     * @return
     */
    public TrdFinProductDealTotal genTrdFinProductDealTotal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto) {
        TrdFinProductDealTotal dealTotal = new TrdFinProductDealTotal();
        // 汇总交易编号
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeListExt2.共通业务类型_股票投资交易);
        dealTotal.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));

        // 业务类型
        dealTotal.setBusiType(dealDto.getBusiType());
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
        dealTotal.setTradeAmt(ArithmeticUtil.subtract(dealTotal.getFpriceAmt(),entrustDto.getMarchFeeAmtPay()));
        // 利息总额
        dealTotal.setIntAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealDto.getUnitInt()));
        // 到期收益率--在TrdProductDeal生成的时候同时设值
        dealTotal.setYield(null);
        // 行权收益率--在TrdProductDeal生成的时候同时设值
        dealTotal.setExerYield(null);
        // 首期结算方式
        dealTotal.setDeliveryType("DVP");

        // 金融产品代码
        dealTotal.setFinprodId(entrustDto.getFinprodId());
        // 金融产品类型（投管分类）
        dealTotal.setFinprodType2(entrustDto.getFinprodType2());
        // 分支序号
        dealTotal.setBranch(0L);
        // 交易方向
        dealTotal.setPs(entrustDto.getPs());
        // 交易日期
        dealTotal.setTradeDate(dealDto.getTradeDate());
        // 起息日
        dealTotal.setVdate(null);
        // 到期日
        dealTotal.setMdate(null);
        // 交割日
        dealTotal.setSettleDate(dealDto.getSettleDate());
        // 清算速度
        dealTotal.setSettleSpeed(dealDto.getSettleSpeed());
        // 交易币种
        dealTotal.setCcy(dealDto.getCcy());
        // 交易份额
        dealTotal.setShareAmt(entrustDto.getMarchQty());
        // 随交易支付费用
        dealTotal.setFeeAmtPay(entrustDto.getMarchFeeAmtPay());
        // 交易金额
        dealTotal.setTradeAmt(ArithmeticUtil.subtract(entrustDto.getMarchAmt(),entrustDto.getMarchFeeAmtPay()));
        // 未随交易支付费用
        dealTotal.setFeeAmtUnpay(entrustDto.getMarchFeeAmtPay());
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
        // 投管业务类型
        dealTotal.setTgBusiType(DictCodeListExt2.平安业务类型_股票交易.getValue());
        // 利率
        dealTotal.setRate(null);
        // 回购期限(天)
        dealTotal.setRepoTerm(null);
        // 期限品种
        dealTotal.setTermType(null);
        // GNS-878:配股生成产品交易表的交易日期应该是清算日期 modify by fuyongsheng 20220217 start
        if(DictCodeListExt2.交易方向_配股.getValue().equals(dealDto.getPs())){
            dealTotal.setTradeDate(dealDto.getSettleDate());
            // GNS-862:配股时价格 modify by fuyongsheng 20220218 start
            dealTotal.setUnitCprice(entrustDto.getPrice());
            dealTotal.setCpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealTotal.getUnitCprice()));
            dealTotal.setUnitFprice(entrustDto.getPrice());
            dealTotal.setFpriceAmt(ArithmeticUtil.multiply(entrustDto.getMarchQty(),dealTotal.getUnitFprice()));
            dealTotal.setTradeAmt(ArithmeticUtil.subtract(dealTotal.getFpriceAmt(),entrustDto.getMarchFeeAmtPay()));
            // GNS-862:配股时价格 modify by fuyongsheng 20220218 end
        }
        // GNS-878:配股生成产品交易表的交易日期应该是清算日期 modify by fuyongsheng 20220217 end
        return dealTotal;
    }

    /**
     * 生成金融产品交易明细
     */

    public TrdProductDeal genTrdProductDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto,TrdFinProductDealTotal trdFinProductDealTotal) {
        return createDeal(dealDto,entrustDto,trdFinProductDealTotal);
    }

    /**
     * 生成股票新股中签交易TRD_PRODUCT_DEAL的数据
     * @param dealDto ExchangeTransMatchCommonDto
     * @param entrustDto ExchangeTransMatchCommonEntrustDto
     */
    public TrdProductDeal createDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto,TrdFinProductDealTotal trdFinProductDealTotal) {
        return null;
    }

    /**
     * 金融产品交易附属表
     */
    public TrdProductDealAdd genTrdProductDealAdd(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, trdProductDeal);
        trdProductDealAdd.setEntrustId(entrustDto.getEntrustId());
        trdProductDealAdd.setPortfolioId(entrustDto.getPortfolioId());

        if(StringUtil.equals(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue(),dealDto.getMatchType())) {
            trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
        }
        else {
            trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        }
        return trdProductDealAdd;
    }



    public List<PtlTrade> generatePtlTrade(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDealDto) {
        List<PtlTrade> ptlTradeList = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        // 组合代码
        ptlTrade.setPortfolioId(entrustDto.getPortfolioId());
        // 业务编号
        ptlTrade.setBusiNo(trdProductDealDto.getTradeId());
        // 业务表
        ptlTrade.setBusiTable(DictCodeListStl.业务表_金融产品交易表.getValue());
        // 业务明细代码
        ptlTrade.setBusiId(trdProductDealDto.getFinprodId());
        // 投资目的
        ptlTrade.setInvAim(trdProductDealDto.getInvAim());
        // 证券托管户代码
        ptlTrade.setSecManageAcctId(trdProductDealDto.getSecManageAcctId());
        // 实际发生日
        ptlTrade.setSettleDate(trdProductDealDto.getSettleDate());
        // 业务类型
        ptlTrade.setBusiType(DictCodeListExt2.共通业务类型_股票投资交易.getValue());
        ptlTradeList.add(ptlTrade) ;
        return ptlTradeList;
    }



    public List<TrdTradeFeeDetail> genTrdTradeFeeDetailList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<TrdTradeFeeDetail> result = new ArrayList<TrdTradeFeeDetail>();
        // 经手费
        if (dealDto.getJsfAmt() != null && dealDto.getJsfAmt().compareTo(new BigDecimal(0)) > 0) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 费用类型
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_经手费.getValue());
            // 是否随交易支付
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            // 费用金额
            trdTradeFeeDetail.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getJsfAmt()));
            // 登记托管机构
            trdTradeFeeDetail.setRegistOrg(trdProductDeal.getRegistOrg());
            result.add(trdTradeFeeDetail);
        }
        // 过户费
        if (dealDto.getGhfAmt() != null && dealDto.getGhfAmt().compareTo(new BigDecimal(0)) > 0) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 费用类型
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_过户费.getValue());
            // 是否随交易支付
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            // 费用金额
            trdTradeFeeDetail.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getGhfAmt()));
            // 登记托管机构
            trdTradeFeeDetail.setRegistOrg(trdProductDeal.getRegistOrg());
            result.add(trdTradeFeeDetail);
        }
        // 证管费
        if (dealDto.getZgfAmt() != null && dealDto.getZgfAmt().compareTo(new BigDecimal(0)) > 0) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 费用类型
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_证管费.getValue());
            // 是否随交易支付
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            // 费用金额
            trdTradeFeeDetail.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getZgfAmt()));
            // 登记托管机构
            trdTradeFeeDetail.setRegistOrg(trdProductDeal.getRegistOrg());
            result.add(trdTradeFeeDetail);
        }
        // 手续费
        if (dealDto.getSxfAmt() != null && dealDto.getSxfAmt().compareTo(new BigDecimal(0)) > 0) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 费用类型
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
            // 是否随交易支付
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            // 费用金额
            trdTradeFeeDetail.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getSxfAmt()));
            // 登记托管机构
            trdTradeFeeDetail.setRegistOrg(trdProductDeal.getRegistOrg());
            result.add(trdTradeFeeDetail);
        }
        // 结算费
        if (dealDto.getSettleFee() != null && dealDto.getSettleFee().compareTo(new BigDecimal(0)) > 0) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 费用类型
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_结算费.getValue());
            // 是否随交易支付
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            // 费用金额
            trdTradeFeeDetail.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getSettleFee()));
            // 登记托管机构
            trdTradeFeeDetail.setRegistOrg(trdProductDeal.getRegistOrg());
            result.add(trdTradeFeeDetail);
        }
        // 印花税
        if (dealDto.getStampFee() != null && dealDto.getStampFee().compareTo(new BigDecimal(0)) > 0) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 费用类型
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_印花税.getValue());
            // 是否随交易支付
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            // 费用金额
            trdTradeFeeDetail.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getStampFee()));
            // 登记托管机构
            trdTradeFeeDetail.setRegistOrg(trdProductDeal.getRegistOrg());
            result.add(trdTradeFeeDetail);
        }
        // 佣金
        if (dealDto.getCommissionAmt() != null && dealDto.getCommissionAmt().compareTo(new BigDecimal(0)) > 0) {
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 费用类型
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
            // 是否随交易支付
            trdTradeFeeDetail.setIsPayWithTrade(dealDto.getIsPayWithTrade());
            // 费用金额
            trdTradeFeeDetail.setFeeAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustDto.getMarchQty(),dealDto.getRemainNum(),4),dealDto.getCommissionAmt()));
            // 登记托管机构
            trdTradeFeeDetail.setRegistOrg(trdProductDeal.getRegistOrg());

            result.add(trdTradeFeeDetail);
        }
        return result;
    }

    public List<StlPayOrdersDto> generateStlPayOrdersList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        // GZYHTZJY-1016:股票的申购确认只处理持仓,不处理现金 modify by fuyongsheng 20220104 start
        if(StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_申购确认.getValue())){
            return new ArrayList<>();
        }
        // GZYHTZJY-1016:股票的申购确认只处理持仓,不处理现金 modify by fuyongsheng 20220104 end
        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();
        StlPayOrdersDto stlPayOsrdersDto = new StlPayOrdersDto();
        // 指令编号
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setSeqNo("9998");
        stlPayOsrdersDto.setOrderId(baseGetBusinessId.buildBussinessCode(bussinessCodeParameter));            // 结算日期
        stlPayOsrdersDto.setSettleDate(trdProductDeal.getSettleDate());
        // 本方资金账户
        stlPayOsrdersDto.setMyAcctNo(dealDto.getAcctNo());
        // 收付方向
        if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), trdProductDeal.getPs())) {
            stlPayOsrdersDto.setPs(DictCodeList.收付方向_收入.getValue());
        }
        else {
            stlPayOsrdersDto.setPs(DictCodeListExt1.收付方向_支出.getValue());
        }
        // 结算金额
        stlPayOsrdersDto.setSettleAmt(trdProductDeal.getTradeAmt());
        // 币种
        stlPayOsrdersDto.setCcy(trdProductDeal.getCcy());
        // 是否需要发送
        stlPayOsrdersDto.setIsNeedSend(DictCodeList.是否_否.getValue());
        // 是否汇总交易
        stlPayOsrdersDto.setIsTotalTrade(DictCodeList.是否_否.getValue());
        // 交易编号
        stlPayOsrdersDto.setTradeId(trdProductDeal.getTradeId());
        // 指令状态
        stlPayOsrdersDto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
        // 投资组合代码
        stlPayOsrdersDto.setPortfolioId(entrustDto.getPortfolioId());
        // 划款类型
        stlPayOsrdersDto.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        stlPayOrdersList.add(stlPayOsrdersDto);

        return stlPayOrdersList;
    }

    /**
     * 金融产品交易附属表
     */
    private PaTrdProductDeal genPaTrdProductDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, trdProductDeal);
        paTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_股票交易.getValue());
        return paTrdProductDeal;
    }

    /**
     * 生成汇总附属交易
     * @param dealDto
     * @param entrustDto
     * @return
     */
    public TrdDealTotalAdd generateDealTotalAdd(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto,TrdFinProductDealTotal trdFinProductDealTotal ) {
        TrdDealTotalAdd trdDealTotalAdd =  new TrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, trdFinProductDealTotal);
        trdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        trdDealTotalAdd.setEntrustId(entrustDto.getEntrustId());
        return  trdDealTotalAdd;
    }
}

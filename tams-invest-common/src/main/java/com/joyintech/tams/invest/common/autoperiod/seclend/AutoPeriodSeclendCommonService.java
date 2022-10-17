package com.joyintech.tams.invest.common.autoperiod.seclend;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.asset.dto.BondAssetCalcInputDto;
import com.joyintech.tams.invest.common.asset.service.BondAssetCalcService;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.trdseclendduration.TrdSecLendDurationDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 债券借贷存续期共通Service
 *
 * @author 裴家俊
 * @version 1.0 2019年7月4日
 * @see AutoPeriodSeclendCommonService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class AutoPeriodSeclendCommonService extends BaseService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * AutoPeriodCommonService
     */
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private BondAssetCalcService bondAssetCalcService;

    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    public static WflTrdFinProductDealTotal gen汇总流程表(TrdSecLendDurationDto entity, String bizType) {
        WflTrdFinProductDealTotal trdFinProductDealTotal = new WflTrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBusiCode());
        trdFinProductDealTotal.setBusiType(bizType);
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setFinprodType2(entity.getFinprodType2());
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_到期.getValue());
        trdFinProductDealTotal.setSecManageAcctId(entity.getSecManageAcctId());
        trdFinProductDealTotal.setBranch(
            AutoPeriodCommonService.changePsToBranch(DateUtil.format(entity.getMdate()), DictCodeList.交易方向_到期.getValue()));
        trdFinProductDealTotal.setTradeDate(entity.getTradeDate());
        trdFinProductDealTotal.setVdate(entity.getVdate());
        trdFinProductDealTotal.setMdate(entity.getMdate());
        trdFinProductDealTotal.setSettleDate(entity.getSettleDate());
        trdFinProductDealTotal.setShareAmt(entity.getShareAmt());
        trdFinProductDealTotal.setIntAmt(entity.getmTradeAmt());// 暂时用借贷费用替换实际结算金额
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());// 暂时用借贷费用替换实际结算金额
        trdFinProductDealTotal.setPrinAmt(entity.getPrinAmt());
        trdFinProductDealTotal.setTradeMarket(entity.getTradeMarket());
        trdFinProductDealTotal.setfTradeId(entity.getfTradeId());// 银行间成交编号
        trdFinProductDealTotal.setCounterId(entity.getCounterId());
        trdFinProductDealTotal.setTrader(entity.getTrader());
        trdFinProductDealTotal.setCounterTrader(entity.getCounterTrader());
        trdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setInvAim(entity.getInvAim());
        if (DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue().equals(entity.getFinprodType2())) {
            trdFinProductDealTotal.setTgBusiType(DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue());
        }
        else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue().equals(entity.getFinprodType2())) {
            trdFinProductDealTotal.setTgBusiType(DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue());
        }
        return trdFinProductDealTotal;
    }

    public static WflPaTrdFinDealTotal gen汇总扩展业务流程(TrdSecLendDurationDto entity,
                                                   WflTrdFinProductDealTotal trdFinProductDealTotal) {
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        wflPaTrdFinDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        if (DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue().equals(entity.getFinprodType2())) {
            wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue());
        }
        else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue().equals(entity.getFinprodType2())) {
            wflPaTrdFinDealTotal.setPaBussType(DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue());
        }
        wflPaTrdFinDealTotal.setRate(entity.getmYield());
        wflPaTrdFinDealTotal.setTheoryAmt(entity.getTradeAmt());
        wflPaTrdFinDealTotal.setTheoryPrin(entity.getPrinAmt());
        wflPaTrdFinDealTotal.setTheoryInt(entity.getmTradeAmt());
        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        return wflPaTrdFinDealTotal;
    }

    public List<WflTrdProductDeal> gen交易业务流程(TrdSecLendDurationDto entity) {
        List<FinCashPlan> 现金流 = restTemplate.postForList(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecLendDuration/findFinCashInfo", entity.getFinprodId(),
            FinCashPlan.class);
        List<WflTrdProductDeal> deals = new ArrayList<>();
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        // add by jiangpeng on 20210428 start
        // TISHKBS-2068 CLONE - 债券借贷融入到期， 融入券可用持仓 < 融入券到期交易.交易份额，选择现金交割时，自动生成的债券买入交易的单位面值和交易员字段为空，应该有值
        String user = LoginUserUtil.getUserName();
        // add by jiangpeng on 20210428 end
        现金流.forEach(finCashPlan -> {
            WflTrdProductDeal deal = new WflTrdProductDeal();
            deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
            deal.setBusiType(DictCodeListExt2.共通业务类型_债券借贷存续期.getValue());
            deal.setFinprodId(entity.getFinprodId());
            deal.setFinprodType2(entity.getFinprodType2());
            deal.setSecManageAcctId(entity.getSecManageAcctId());
            deal.setCcy(entity.getCcy());
            deal.setTradeDate(iPlatformDate.getSysDate());// 平台日
            deal.setVdate(entity.getVdate());
            deal.setMdate(entity.getMdate());
            deal.setSettleDate(entity.getSettleDate());
            deal.setShareAmt(entity.getShareAmt());
            deal.setDeliveryType(entity.getDeliveryType());
            deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            deal.setIsCancel(DictCodeList.是否_否.getValue());
            deal.setTradeMarket(entity.getTradeMarket());
            deal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            deal.setWorkflowId(entity.getWorkflowId());
            deal.setTotleTradeId(entity.getBusiCode());
            deal.setCashId(finCashPlan.getCashId());
            deal.setCancelRemark(entity.getCancelRemark());
            deal.setInvAim("999999");
            // 20190818 添加名义本金
            // 名义本金
            deal.setPrinAmt(entity.getShareAmt());
            deal.setStatus(DictCodeList.交易方向_到期.getValue());
            deal.setPs(DictCodeList.交易方向_到期.getValue());
            deal.setBranch(AutoPeriodCommonService.changePsToBranch(DateUtil.format(entity.getMdate()), deal.getPs()));
            deal.setIntAmt(entity.getmTradeAmt());
            deal.setFpriceAmt(entity.getmTradeAmt());
            deal.setTradeAmt(entity.getmTradeAmt());
            // add by jiangpeng on 20210428 start
            // ZGZGST-157 投管系统，债券借贷到期交易未存储登记托管机构
            deal.setRegistOrg(getRegisOrgInfo(entity.getPledgeFinprodId()));//登记托管机构
            // add by jiangpeng on 20210428 end
            deals.add(deal);
            if (DictCodeList.是否_是.getValue().equals(entity.getIsCashPay())) {
                BondAssetCalcInputDto bondAssetCalcInputDto = new BondAssetCalcInputDto();
                bondAssetCalcInputDto.setFinprodId(entity.getPledgeFinprodId());
                bondAssetCalcInputDto.setTradeMarket(entity.getTradeMarket());
                bondAssetCalcInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                bondAssetCalcInputDto.setSettleDate(entity.getSettleDate());
                BigDecimal unitInt = null;
                try {
                    unitInt = bondAssetCalcService.计算债券单位利息(bondAssetCalcInputDto);
                } catch (Exception e) {
                    logger.error("计算利息失败，置为0", e);
                    unitInt = BigDecimal.ZERO;
                }

                if (DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue().equals(entity.getFinprodType2())) {
                    WflTrdProductDeal deal2 = null;
                    if (entity.getAssetIntegrateAvailablePosition() != null
                        && entity.getAssetIntegrateAvailablePosition().compareTo(entity.getShareAmt()) < 0) {
                        // 登入卖空买入交易
                        deal2 = new WflTrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(deal2, deal);
                        deal2.setFinprodId(entity.getPledgeFinprodId());
                        deal2.setBusiType(DictCodeList.业务类型_债券借贷标的债券交易.getValue());
                        deal2.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                        deal2.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
                        deal2.setPs(DictCodeList.交易方向_买入.getValue());
                        deal2.setUnitInt(unitInt);
                        deal2.setUnitFprice(ArithmeticUtil.divide(entity.getPrinAmt(), ArithmeticUtil.divide(entity.getShareAmt(), new BigDecimal(100), 4), 8));
                        deal2.setUnitCprice(ArithmeticUtil.subtract(deal2.getUnitFprice(), deal2.getUnitInt()));
                        deal2.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                        deal2.setShareAmt(ArithmeticUtil.divide(
                            ArithmeticUtil.subtract(entity.getShareAmt(), entity.getAssetIntegrateAvailablePosition()), 100, 2));
                        deal2.setIntAmt(ArithmeticUtil.round(
                            ArithmeticUtil.multiply(deal2.getShareAmt(), deal2.getUnitInt()), 2,
                            RoundingMode.HALF_UP));
                        deal2.setFpriceAmt(ArithmeticUtil.round(ArithmeticUtil.multiply(deal2.getShareAmt(), deal2.getUnitFprice()), 2, RoundingMode.HALF_UP));
                        deal2.setTradeAmt(deal2.getFpriceAmt());
                        deal2.setCpriceAmt(ArithmeticUtil.subtract(deal2.getFpriceAmt(), deal2.getIntAmt()));
                        deal2.setPrinAmt(ArithmeticUtil.subtract(deal2.getTradeAmt(), deal2.getIntAmt()));
                        // add by jiangpeng on 20210429 start
                        // TISHKBS-2068 CLONE - 债券借贷融入到期， 融入券可用持仓 < 融入券到期交易.交易份额，选择现金交割时，自动生成的债券买入交易的单位面值和交易员字段为空，应该有值
                        deal2.setTrader(user);
                        deal2.setParValue(this.getUnitPrice(deal2.getFinprodId(), deal2.getSettleDate()));
                        // add by jiangpeng on 20210429 end
                        deals.add(deal2);
                    }

                    // 本方券买入
                    WflTrdProductDeal deal1 = new WflTrdProductDeal();
                    if (deal2 != null) {
                        BeanUtil.copyPropertiesIgnoreNull(deal1, deal2);
                        deal1.setInvAim(entity.getInvAim());
                        deal1.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
                        deal1.setShareAmt(ArithmeticUtil.subtract(ArithmeticUtil.divide(entity.getShareAmt(), 100, 2), deal2.getShareAmt()));
                        deal1.setIntAmt(ArithmeticUtil.round(
                            ArithmeticUtil.multiply(deal1.getShareAmt(), deal1.getUnitInt()), 2,
                            RoundingMode.HALF_UP));
                        deal1.setFinprodId(entity.getPledgeFinprodId());
                        deal1.setFpriceAmt(ArithmeticUtil.subtract(entity.getPrinAmt(), deal2.getFpriceAmt()));
                        deal1.setTradeAmt(deal1.getFpriceAmt());
                        deal1.setPrinAmt(ArithmeticUtil.subtract(deal1.getTradeAmt(), deal1.getIntAmt()));
                    }
                    else {
                        BeanUtil.copyPropertiesIgnoreNull(deal1, deal);
                        deal1.setInvAim(entity.getInvAim());
                        deal1.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
                        deal1.setFinprodId(entity.getPledgeFinprodId());
                        deal1.setPs(DictCodeList.交易方向_买入.getValue());
                        deal1.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                        deal1.setUnitInt(unitInt);
                        deal1.setUnitFprice(ArithmeticUtil.divide(entity.getPrinAmt(), ArithmeticUtil.divide(entity.getShareAmt(), new BigDecimal(100), 4), 8));
                        deal1.setUnitCprice(ArithmeticUtil.subtract(deal1.getUnitFprice(), deal1.getUnitInt()));
                        deal1.setShareAmt(ArithmeticUtil.divide(entity.getShareAmt(), 100, 2));
                        deal1.setIntAmt(ArithmeticUtil.round(
                                ArithmeticUtil.multiply(deal1.getShareAmt(), deal1.getUnitInt()), 2,
                                RoundingMode.HALF_UP));
                        deal1.setFpriceAmt(entity.getPrinAmt());
                        deal1.setTradeAmt(deal1.getFpriceAmt());
                        deal1.setPrinAmt(ArithmeticUtil.subtract(deal1.getTradeAmt(), deal1.getIntAmt()));
                    }
                    deal1.setBusiType(DictCodeList.业务类型_债券交易.getValue());
                    deal1.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    deal1.setCpriceAmt(ArithmeticUtil.subtract(deal1.getFpriceAmt(), deal1.getIntAmt()));
                    // add by jiangpeng on 20210428 start
                    // TISHKBS-2068 CLONE - 债券借贷融入到期， 融入券可用持仓 < 融入券到期交易.交易份额，选择现金交割时，自动生成的债券买入交易的单位面值和交易员字段为空，应该有值
                    deal1.setTrader(user);
                    deal1.setParValue(this.getUnitPrice(deal1.getFinprodId(), deal1.getSettleDate()));
                    // add by jiangpeng on 20210428 end
                    deals.add(deal1);
                }
                else if (DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue().equals(entity.getFinprodType2())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("portfolioId", entity.getActPortfolioId());
                    params.put("secAcctId", entity.getSecManageAcctId());
                    params.put("finprodId", entity.getPledgeFinprodId());
                    params.put("branch", 0);
                    params.put("hodingType", DictCodeList.持仓类型_融出.getValue());
                    params.put("cdate", DateUtil.platDateFromCache());

                    List<PtlSecPosition> ptlSecPositionFlows = autoPeriodCommonService.findAllProvided(params);
                    BigDecimal 总金额 = entity.getPrinAmt();
                    BigDecimal 最大持仓 = null;
                    List<WflTrdProductDeal> trdProductDeals = new ArrayList<>();
                    WflTrdProductDeal 最大持仓的交易 = null;
                    if (ptlSecPositionFlows != null) {
                        for (PtlSecPosition ptlSecPosition : ptlSecPositionFlows) {// 本方券卖出
                            WflTrdProductDeal deal1 = new WflTrdProductDeal();
                            BeanUtil.copyPropertiesIgnoreNull(deal1, deal);
                            deal1.setFinprodId(entity.getPledgeFinprodId());
                            deal1.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
                            deal1.setPs(DictCodeList.交易方向_卖出.getValue());
                            deal1.setBusiType(DictCodeList.业务类型_债券借贷标的债券交易.getValue());
                            deal1.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                            deal1.setUnitInt(unitInt);
                            deal1.setUnitFprice(ArithmeticUtil.divide(entity.getPrinAmt(), deal.getShareAmt(), 8));
                            deal1.setUnitCprice(ArithmeticUtil.subtract(deal1.getUnitFprice(), deal1.getUnitInt()));
                            deal1.setShareAmt(ptlSecPosition.getAmount());
                            deal1.setIntAmt(ArithmeticUtil.multiply(deal1.getShareAmt(), deal1.getUnitInt()));
                            // add by jiangpeng on 20210428 start
                            // TISHKBS-2068 CLONE - 债券借贷融入到期， 融入券可用持仓 < 融入券到期交易.交易份额，选择现金交割时，自动生成的债券买入交易的单位面值和交易员字段为空，应该有值
                            deal1.setTrader(user);
                            deal1.setParValue(this.getUnitPrice(deal1.getFinprodId(), deal1.getSettleDate()));
                            // add by jiangpeng on 20210428 end
                            deal1.setFpriceAmt(ArithmeticUtil.round(
                                ArithmeticUtil.multiply(deal.getShareAmt(),
                                    ArithmeticUtil.divide(entity.getPrinAmt(), entity.getShareAmt(), 12)),
                                2, RoundingMode.HALF_UP));
                            deal1.setTradeAmt(deal1.getFpriceAmt());
                            if (最大持仓 == null || ptlSecPosition.getAmount().compareTo(最大持仓) > 0) {
                                最大持仓 = ptlSecPosition.getAmount();
                                最大持仓的交易 = deal1;
                            }
                            deal1.setInvAim(ptlSecPosition.getInvAim());
                            if ("999999".equals(ptlSecPosition.getInvAim())) {
                                deal1.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                            }
                            trdProductDeals.add(deal1);
                        }

                        BigDecimal 不带最大持仓的其他金额 = BigDecimal.ZERO;
                        for (WflTrdProductDeal wflTrdProductDeal : trdProductDeals) {
                            if (最大持仓的交易 != null && !wflTrdProductDeal.getTradeId().equals(最大持仓的交易.getTradeId())) {
                                不带最大持仓的其他金额 = ArithmeticUtil.doAdd(不带最大持仓的其他金额, deal.getFpriceAmt());
                            }
                        }
                        if (最大持仓的交易 != null){
                            最大持仓的交易.setFpriceAmt(ArithmeticUtil.subtract(总金额, 不带最大持仓的其他金额));
                            最大持仓的交易.setTradeAmt(最大持仓的交易.getFpriceAmt());    
                        }
                    }
                    deals.addAll(trdProductDeals);
                }
            }
        });

        return deals;
    }

    /***
     * 获取当前标的登记托管机构
     * ZGZGST-157 投管系统，债券借贷到期交易未存储登记托管机构
     * @param finprodId
     * @return
     */
    private String getRegisOrgInfo(String finprodId){
        FinProduct retFinBondInfo = restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME
                        + "/FinBondTargetAsset/getBasicInfo?finprodId="
                        + finprodId,
                FinProduct.class);

        return  StringUtil.isNotEmpty(retFinBondInfo)?retFinBondInfo.getRegistOrg():null;
    }

    /**
     * 1.8计算单位面值
     * TISHKBS-2068 CLONE - 债券借贷融入到期， 融入券可用持仓 < 融入券到期交易.交易份额，选择现金交割时，自动生成的债券买入交易的单位面值和交易员字段为空，应该有值
     *
     * @param finprodId 金融产品代码
     * @param settleDate 交割日
     * @return 单位面值
     * @since 1.0
     */
    public BigDecimal getUnitPrice(String finprodId, Date settleDate) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("finprodId", finprodId);
        parameters.put("settleDate", DateUtil.format(settleDate));
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getUnitPrice", parameters,
                BigDecimal.class);
    }

    /**
     * 为调用共通组件组装的entity
     *
     * @param entity
     * @return
     */
    public static TrdProductDeal getTrdProductDeal(TrdSecLendDurationDto entity) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity);
        trdProductDeal.setTradeId(entity.getBusiCode());
        trdProductDeal.setBusiType(DictCodeListExt2.共通业务类型_债券借贷存续期.getValue());
        trdProductDeal.setPs(DictCodeList.交易方向_到期.getValue());
        return trdProductDeal;
    }

    public WflTrdDealTotalAdd gen汇总附属业务流程(TrdSecLendDurationDto entity, WflTrdFinProductDealTotal trdFinProductDealTotal) {
        WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
        wflTrdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        wflTrdDealTotalAdd.setIsCashset(entity.getIsCashPay());
        wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
        wflTrdDealTotalAdd.setWorkflowStatus(DictCodeListExt1.画面数据状态_流程中.getValue());
        return wflTrdDealTotalAdd;
    }
}

package com.joyintech.tams.exchange.period.creditprotectioncertificate.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月17日
 * @since 1.0
 */
public class BaseBussinessDto extends BaseEntity {
    /**
     * 汇总交易表
     */
    private TrdFinProductDealTotal trdFinProductDealTotal;

    /**
     * 金融产品交易汇总附属表
     */
    private TrdDealTotalAdd trdDealTotalAdd;

    /**
     * 金融产品交易
     */
    private TrdProductDeal trdProductDeal;

    /**
     * 金融产品交易附属表
     */
    private TrdProductDealAdd trdProductDealAdd;

    /**
     * 划款指令
     */
    private StlPayOrders stlPayOrders;

    /**
     * 组合交易表
     */
    private PtlTrade ptlTrade;


    public TrdFinProductDealTotal getTrdFinProductDealTotal() {
        return trdFinProductDealTotal;
    }

    public void setTrdFinProductDealTotal(TrdFinProductDealTotal trdFinProductDealTotal) {
        this.trdFinProductDealTotal = trdFinProductDealTotal;
    }

    public TrdDealTotalAdd getTrdDealTotalAdd() {
        return trdDealTotalAdd;
    }

    public void setTrdDealTotalAdd(TrdDealTotalAdd trdDealTotalAdd) {
        this.trdDealTotalAdd = trdDealTotalAdd;
    }

    public TrdProductDeal getTrdProductDeal() {
        return trdProductDeal;
    }

    public void setTrdProductDeal(TrdProductDeal trdProductDeal) {
        this.trdProductDeal = trdProductDeal;
    }

    public TrdProductDealAdd getTrdProductDealAdd() {
        return trdProductDealAdd;
    }

    public void setTrdProductDealAdd(TrdProductDealAdd trdProductDealAdd) {
        this.trdProductDealAdd = trdProductDealAdd;
    }

    public StlPayOrders getStlPayOrders() {
        return stlPayOrders;
    }

    public void setStlPayOrders(StlPayOrders stlPayOrders) {
        this.stlPayOrders = stlPayOrders;
    }

    public PtlTrade getPtlTrade() {
        return ptlTrade;
    }

    public void setPtlTrade(PtlTrade ptlTrade) {
        this.ptlTrade = ptlTrade;
    }
}

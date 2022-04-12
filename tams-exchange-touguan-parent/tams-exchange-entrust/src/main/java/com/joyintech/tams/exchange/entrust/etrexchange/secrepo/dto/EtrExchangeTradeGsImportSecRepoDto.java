/*
 * 文件名：EtrExchangeTradeGsImportDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2021年7月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.invest.common.asset.annotation.DictList;
import com.joyintech.tams.invest.common.asset.annotation.Number;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  固定收益交易指令导入Dto
 * @author 裴家俊
 * @version 1.0 2021年7月28日
 * @see EtrExchangeTradeGsImportSecRepoDto
 * @since 1.0
 */
public class EtrExchangeTradeGsImportSecRepoDto extends EtrExchangeTradeSaveSecRepoRequest {

    /**
     * @param portfolioId 组合代码
     */
    @Number(1)
    public void setPortfolioId(String portfolioId) { super.setPortfolioId(portfolioId); }

    /**
     * @param acctNo 组合资金账户
     */
    @Number(2)
    public void setAcctNo(String acctNo) {
        super.setAcctNo(acctNo);
    }

    /**
     * @param finprodId 金融产品代码
     */
    @Number(3)
    public void setFinprodId(String finprodId) {
        super.setFinprodId(finprodId);
    }

    /**
     * @param ps 交易方向
     */
    @Number(4)
    public void setPs(String ps) {
        super.setPs(ps);
    }

    /**
     * @param priceModel 价格模式
     */
    @Number(5)
    public void setPriceModel(String priceModel) {
        super.setPriceModel(priceModel);
    }

    /**
     * @param price 净价价格
     */
    @Number(6)
    public void setPrice(BigDecimal price) { super.setPrice(price); }

    /**
     * @param fprice 全价价格
     */
    @Number(7)
    public void setFprice(BigDecimal fprice) { super.setFprice(fprice); }

    /**
     * @param yield 收益率
     */
    @Number(8)
    public void setYield(BigDecimal yield) { super.setYield(yield); }

    /**
     * @param targetType 目标类型
     */
    @Number(9)
    public void setTargetType(String targetType) { super.setTargetType(targetType); }

    /**
     * @param entrustNum 指令数量
     */
    @Number(10)
    public void setEntrustNum(BigDecimal entrustNum) { super.setEntrustNum(entrustNum); }

    /**
     * @param entrustAmt 指令金额
     */
    @Number(11)
    public void setEntrustAmt(BigDecimal entrustAmt) { super.setEntrustAmt(entrustAmt); }

    /**
     * @param counterId 交易对手
     */
    @Number(12)
    public void setCounterId(String counterId) { super.setCounterId(counterId); }

    /**
     * @param invAim 投资目的
     */
    @Number(13)
    @DictList(link=DictCode.投资目的)
    public void setInvAim(String invAim) { super.setInvAim(invAim); }

    /**
     * @param trader 本方交易员
     */
    @Number(14)
    public void setTrader(String trader) { super.setTrader(trader); }

    /**
     * @param counterTrader 对方交易员
     */
    @Number(15)
    public void setCounterTrader(String counterTrader) { super.setCounterTrader(counterTrader); }

    /**
     * @param agreeOnId 约定号
     */
    @Number(16)
    public void setAgreeOnId(String agreeOnId) { super.setAgreeOnId(agreeOnId); }

    /**
     * @param vdate 开始日期
     */
    @Number(17)
    public void setVdate(Date vdate) { super.setVdate(vdate); }

    /**
     * @param mdate 结束日期
     */
    @Number(18)
    public void setMdate(Date mdate) { super.setMdate(mdate); }

    /**
     * 申报类型代码
     */
    public String getDeclaType() {
        return DictCodeListExt3.申报类型_指定对手方成交申报.getValue();
    }

    /**
     * 申报类型名称
     */
    public String getDeclaTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.申报类型,getDeclaType());
    }


    /**
     * 获取 settleSpreedName
     *
     * @return settleSpreedName.
     */
    public String getSettleSpreedName() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, StringUtil.isNotEmpty(this.getSettleSpeed())?this.getSettleSpeed(): DictCodeList.清算速度_T_1.getValue());
    }

}

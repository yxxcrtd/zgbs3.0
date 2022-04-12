/*
 * 文件名：EtrExchangeTradeDzImportDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2021年7月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.invest.common.asset.annotation.DictList;
import com.joyintech.tams.invest.common.asset.annotation.Number;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  大宗交易指令导入Dto
 * @author 裴家俊
 * @version 1.0 2021年7月28日
 * @see EtrExchangeTradeDzImportSecRepoDto
 * @since 1.0
 */
public class EtrExchangeTradeDzImportSecRepoDto extends EtrExchangeTradeSaveSecRepoRequest {

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
    @DictList(link= DictCode.交易方向)
    public void setPs(String ps) {
        super.setPs(ps);
    }

    /**
     * @param priceModel 价格模式
     */
    @Number(5)
    @DictList(link=DictCode.价格模式)
    public void setPriceModel(String priceModel) {
        super.setPriceModel(priceModel);
    }

    /**
     * @param declaType 申报类型
     */
    @Number(6)
    @DictList(link=DictCode.申报类型)
    public void setDeclaType(String declaType) { super.setDeclaType(declaType); }

    /**
     * @param entrustPrice 指令价格
     */
    @Number(7)
    public void setEntrustPrice(BigDecimal entrustPrice) { super.setEntrustPrice(entrustPrice); }

    /**
     * @param fprice 全价
     */
    @Number(8)
    public void setFprice(BigDecimal fprice) { super.setFprice(fprice); }

    /**
     * @param yield 收益率
     */
    @Number(9)
    public void setYield(BigDecimal yield) { super.setYield(yield); }

    /**
     * @param targetType 目标类型
     */
    @Number(10)
    @DictList(link=DictCode.目标类型)
    public void setTargetType(String targetType) { super.setTargetType(targetType); }

    /**
     * @param entrustNum 指令数量
     */
    @Number(11)
    public void setEntrustNum(BigDecimal entrustNum) { super.setEntrustNum(entrustNum); }

    /**
     * @param entrustAmt 指令金额
     */
    @Number(12)
    public void setEntrustAmt(BigDecimal entrustAmt) { super.setEntrustAmt(entrustAmt); }

    /**
     * @param counterId 交易对手
     */
    @Number(13)
    public void setCounterId(String counterId) { super.setCounterId(counterId); }

    /**
     * @param invAim 投资目的
     */
    @Number(14)
    @DictList(link=DictCode.投资目的)
    public void setInvAim(String invAim) { super.setInvAim(invAim); }

    /**
     * @param seatId 对方席位
     */
    @Number(15)
    public void setSeatId(String seatId) { super.setSeatId(seatId); }

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


/*    *
     * 申报类型代码

    public String getDeclaType() {
        return DictCodeListExt3.申报类型_不指定.getValue();
    }*/

/*    *
     * 申报类型名称*/

    public String getDeclaTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.申报类型,super.getDeclaType());
    }

    /**
     * 目标类型名称
     * @return
     */
    public String getTargetTypeStr() { return  SysDataUtil.getNameFromDataDict(DictCode.目标类型,getTargetType()); }
}

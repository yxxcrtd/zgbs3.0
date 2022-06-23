/*
 * 文件名：TaxBalanceAmtDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年8月16日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.dto;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.tams.common.base.entity.TaxBokBalance;
import com.joyintech.tams.constant.DictCodeList;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 增值税余额汇总金额类Dto
 * 
 * @author weipeng
 * @version 1.0 2018年8月16日
 * @see TaxBalanceAmtDto
 * @since 1.0
 */
public class TaxBalanceAmtDto {

    /**
     * 利息收入确认预估 结转金额
     */
    private BigDecimal lxqrygCarryAmt = BigDecimal.ZERO;

    /**
     * 利息收入确认预发生 结转金额
     */
    private BigDecimal lxqrfsCarryAmt = BigDecimal.ZERO;

    /**
     * 预估利息 收入发生额
     */
    private BigDecimal yglxIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 预估利息 发生额
     */
    private BigDecimal yglxHappenAmt = BigDecimal.ZERO;

    /**
     * 确认利息 收入发生额
     */
    private BigDecimal qrlxIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 确认利息 发生额
     */
    private BigDecimal qrlxHappenAmt = BigDecimal.ZERO;

    /**
     * 红利收入确认预估 结转金额
     */
    private BigDecimal hlqrygCarryAmt = BigDecimal.ZERO;

    /**
     * 红利收入确认预发生 结转金额
     */
    private BigDecimal hlqrfsCarryAmt = BigDecimal.ZERO;

    /**
     * 预估红利 收入发生额
     */
    private BigDecimal yghlIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 预估红利 发生额
     */
    private BigDecimal yghlHappenAmt = BigDecimal.ZERO;

    /**
     * 确认红利 收入发生额
     */
    private BigDecimal qrhlIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 确认红利 发生额
     */
    private BigDecimal qrhlHappenAmt = BigDecimal.ZERO;

    /**
     * 确认价差 收入发生额
     */
    private BigDecimal qrjcIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 确认价差收入 当日发生额
     */
    private BigDecimal qrjcHappenAmt = BigDecimal.ZERO;
    
    /**
     * 确认价差收入 当日发生额(不互相抵扣)
     */
    private BigDecimal qrjcHappenAmtNo = BigDecimal.ZERO;

    /**
     * 公允价值变动（预估价差） 收入发生额
     */
    private BigDecimal ygjcIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 公允价值变动（预估价差） 当日发生额
     */
    private BigDecimal ygjcHappenAmt = BigDecimal.ZERO;
    
    /**
     * 手工记账发生额
     */
    private BigDecimal maygjcAmt = BigDecimal.ZERO;
    
    /**
     * 手工记账发生额
     */
    private BigDecimal maqrjcAmt = BigDecimal.ZERO;
    /**
     * 手工记账发生额
     */
    private BigDecimal mayglxAmt = BigDecimal.ZERO;
    /**
     * 手工记账发生额
     */
    private BigDecimal maqrlxAmt = BigDecimal.ZERO;
    /**
     * 手工记账发生额
     */
    private BigDecimal mayghlAmt = BigDecimal.ZERO;
    /**
     * 手工记账发生额
     */
    private BigDecimal maqrhlAmt = BigDecimal.ZERO;

    /**
     * 获取 利息收入确认预估 结转金额
     * 
     * @return 利息收入确认预估 结转金额
     */
    public BigDecimal getLxqrygCarryAmt() {
        return lxqrygCarryAmt;
    }

    /**
     * 设置 利息收入确认预估 结转金额
     * 
     * @param lxqrygCarryAmt 利息收入确认预估 结转金额
     */
    public void setLxqrygCarryAmt(BigDecimal lxqrygCarryAmt) {
        this.lxqrygCarryAmt = lxqrygCarryAmt;
    }

    /**
     * 获取 利息收入确认预发生 结转金额
     * 
     * @return 利息收入确认预发生 结转金额
     */
    public BigDecimal getLxqrfsCarryAmt() {
        return lxqrfsCarryAmt;
    }

    /**
     * 设置 利息收入确认预发生 结转金额
     * 
     * @param lxqrfsCarryAmt 利息收入确认预发生 结转金额
     */
    public void setLxqrfsCarryAmt(BigDecimal lxqrfsCarryAmt) {
        this.lxqrfsCarryAmt = lxqrfsCarryAmt;
    }

    /**
     * 获取 预估利息 收入发生额
     * 
     * @return 预估利息 收入发生额
     */
    public BigDecimal getYglxIncomeAmtAll() {
        return yglxIncomeAmtAll;
    }

    /**
     * 设置 预估利息 收入发生额
     * 
     * @param yglxIncomeAmtAll 预估利息 收入发生额
     */
    public void setYglxIncomeAmtAll(BigDecimal yglxIncomeAmtAll) {
        this.yglxIncomeAmtAll = yglxIncomeAmtAll;
    }

    /**
     * 获取 预估利息 发生额
     * 
     * @return 预估利息 发生额
     */
    public BigDecimal getYglxHappenAmt() {
        return yglxHappenAmt;
    }

    /**
     * 设置 预估利息 发生额
     * 
     * @param yglxHappenAmt 预估利息 发生额
     */
    public void setYglxHappenAmt(BigDecimal yglxHappenAmt) {
        this.yglxHappenAmt = yglxHappenAmt;
    }

    /**
     * 获取 确认利息 收入发生额
     * 
     * @return 确认利息 收入发生额
     */
    public BigDecimal getQrlxIncomeAmtAll() {
        return qrlxIncomeAmtAll;
    }

    /**
     * 设置 确认利息 收入发生额
     * 
     * @param qrlxIncomeAmtAll 确认利息 收入发生额
     */
    public void setQrlxIncomeAmtAll(BigDecimal qrlxIncomeAmtAll) {
        this.qrlxIncomeAmtAll = qrlxIncomeAmtAll;
    }

    /**
     * 获取 确认利息 发生额
     * 
     * @return 确认利息 发生额
     */
    public BigDecimal getQrlxHappenAmt() {
        return qrlxHappenAmt;
    }

    /**
     * 设置 确认利息 发生额
     * 
     * @param qrlxHappenAmt 确认利息 发生额
     */
    public void setQrlxHappenAmt(BigDecimal qrlxHappenAmt) {
        this.qrlxHappenAmt = qrlxHappenAmt;
    }

    /**
     * 获取 红利收入确认预估 结转金额
     * 
     * @return 红利收入确认预估 结转金额
     */
    public BigDecimal getHlqrygCarryAmt() {
        return hlqrygCarryAmt;
    }

    /**
     * 设置 红利收入确认发生 结转金额
     * 
     * @param hlqrygCarryAmt 红利收入确认发生 结转金额
     */
    public void setHlqrygCarryAmt(BigDecimal hlqrygCarryAmt) {
        this.hlqrygCarryAmt = hlqrygCarryAmt;
    }

    /**
     * 获取 红利收入确认发生 结转金额
     * 
     * @return 红利收入确认发生 结转金额
     */
    public BigDecimal getHlqrfsCarryAmt() {
        return hlqrfsCarryAmt;
    }

    /**
     * 设置 红利收入确认预发生 结转金额
     * 
     * @param hlqrfsCarryAmt 红利收入确认预发生 结转金额
     */
    public void setHlqrfsCarryAmt(BigDecimal hlqrfsCarryAmt) {
        this.hlqrfsCarryAmt = hlqrfsCarryAmt;
    }

    /**
     * 获取 预估红利 收入发生额
     * 
     * @return 预估红利 收入发生额
     */
    public BigDecimal getYghlIncomeAmtAll() {
        return yghlIncomeAmtAll;
    }

    /**
     * 设置 预估红利 收入发生额
     * 
     * @param yghlIncomeAmtAll 预估红利 收入发生额
     */
    public void setYghlIncomeAmtAll(BigDecimal yghlIncomeAmtAll) {
        this.yghlIncomeAmtAll = yghlIncomeAmtAll;
    }

    /**
     * 获取 预估红利 发生额
     * 
     * @return 预估红利 发生额
     */
    public BigDecimal getYghlHappenAmt() {
        return yghlHappenAmt;
    }

    /**
     * 设置 预估红利 发生额
     * 
     * @param yghlHappenAmt 预估红利 发生额
     */
    public void setYghlHappenAmt(BigDecimal yghlHappenAmt) {
        this.yghlHappenAmt = yghlHappenAmt;
    }

    /**
     * 获取 确认红利 收入发生额
     * 
     * @return 确认红利 收入发生额
     */
    public BigDecimal getQrhlIncomeAmtAll() {
        return qrhlIncomeAmtAll;
    }

    /**
     * 设置 确认红利 收入发生额
     * 
     * @param qrhlIncomeAmtAll 确认红利 收入发生额
     */
    public void setQrhlIncomeAmtAll(BigDecimal qrhlIncomeAmtAll) {
        this.qrhlIncomeAmtAll = qrhlIncomeAmtAll;
    }

    /**
     * 获取 确认红利 发生额
     * 
     * @return 确认红利 发生额
     */
    public BigDecimal getQrhlHappenAmt() {
        return qrhlHappenAmt;
    }

    /**
     * 设置 确认红利 发生额
     * 
     * @param qrhlHappenAmt 确认红利 发生额
     */
    public void setQrhlHappenAmt(BigDecimal qrhlHappenAmt) {
        this.qrhlHappenAmt = qrhlHappenAmt;
    }

    /**
     * 获取 确认价差收入 当日发生额
     * 
     * @return 确认价差收入 当日发生额
     */
    public BigDecimal getQrjcHappenAmt() {
        return qrjcHappenAmt;
    }

    /**
     * 设置 确认价差收入 当日发生额
     * 
     * @param qrjcHappenAmt 确认价差收入 当日发生额
     */
    public void setQrjcHappenAmt(BigDecimal qrjcHappenAmt) {
        this.qrjcHappenAmt = qrjcHappenAmt;
    }

    /**
     * 获取 公允价值变动（预估价差） 当日发生额
     * 
     * @return 公允价值变动（预估价差） 当日发生额
     */
    public BigDecimal getYgjcHappenAmt() {
        return ygjcHappenAmt;
    }

    /**
     * 设置 公允价值变动（预估价差） 当日发生额
     * 
     * @param ygjcHappenAmt 公允价值变动（预估价差） 当日发生额
     */
    public void setYgjcHappenAmt(BigDecimal ygjcHappenAmt) {
        this.ygjcHappenAmt = ygjcHappenAmt;
    }

    /**
     * 获取 确认价差 收入发生额
     * 
     * @return 确认价差 收入发生额
     */
    public BigDecimal getQrjcIncomeAmtAll() {
        return qrjcIncomeAmtAll;
    }

    /**
     * 设置 确认价差 收入发生额
     * 
     * @param qrjcIncomeAmtAll 确认价差 收入发生额
     */
    public void setQrjcIncomeAmtAll(BigDecimal qrjcIncomeAmtAll) {
        this.qrjcIncomeAmtAll = qrjcIncomeAmtAll;
    }

    /**
     * 获取 公允价值变动（预估价差） 收入发生额
     * 
     * @return 公允价值变动（预估价差） 收入发生额
     */
    public BigDecimal getYgjcIncomeAmtAll() {
        return ygjcIncomeAmtAll;
    }

    /**
     * 设置 公允价值变动（预估价差） 收入发生额
     * 
     * @param ygjcIncomeAmtAll 公允价值变动（预估价差） 收入发生额
     */
    public void setYgjcIncomeAmtAll(BigDecimal ygjcIncomeAmtAll) {
        this.ygjcIncomeAmtAll = ygjcIncomeAmtAll;
    }

    /**
     * 获取 qrjcHappenAmtNo
     * @return qrjcHappenAmtNo.
     */
    public BigDecimal getQrjcHappenAmtNo() {
        return qrjcHappenAmtNo;
    }

    /**
     * 设置 qrjcHappenAmtNo
     * @param qrjcHappenAmtNo qrjcHappenAmtNo
     */
    public void setQrjcHappenAmtNo(BigDecimal qrjcHappenAmtNo) {
        this.qrjcHappenAmtNo = qrjcHappenAmtNo;
    }

    /**
     * 通过账务明细余额,生成增值税汇总余额
     * 
     * @param listValTaxBokBalance 原税务余额
     * @param groupedTaxBokBalanceList 汇总后的税务余额
     * @return
     */
    public static TaxBalanceAmtDto createDtoByBalance(List<TaxBokBalance> listValTaxBokBalance,
                                                      List<TaxBokBalance> groupedTaxBokBalanceList) {
        TaxBalanceAmtDto amtDto = new TaxBalanceAmtDto();
        // added by wangzhiang 【辽阳银行】利息收入增加参数，支持按资产维度先初步汇总舍位后在进行账套级别汇总 20191115 start
        List<TaxBokBalance> sumList = null;
        if (CollectionUtils.isNotEmpty(groupedTaxBokBalanceList)) {
            sumList = groupedTaxBokBalanceList;
        }
        else {
            sumList = listValTaxBokBalance;
        }
        // added by wangzhiang 【辽阳银行】利息收入增加参数，支持按资产维度先初步汇总舍位后在进行账套级别汇总 20191115 end
        for (TaxBokBalance item : sumList) {
        	if (DictCodeList.计税方式_简易计税.getValue().equals(item.getCalcType())) {
        	    if (item.getVatType().equals(DictCodeList.增值税类型_利息收入.getValue())) {
        	        if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
        	            // round(sum(增值税账务明细余额.当日结转累计发生额),2) 预估结转金额
        	            amtDto.setLxqrygCarryAmt(
        	                    ArithmeticUtil.doAdd(item.getCarryAmt(), amtDto.getLxqrygCarryAmt()));
        	            // round(sum(增值税账务明细余额.当日计税收入发生额),2) 收入发生额
        	            amtDto.setYglxIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeHappenAmt(),
        	                    amtDto.getYglxIncomeAmtAll()));
        	            // round(sum(增值税账务明细余额.当日发生额),2) 发生额
        	            amtDto.setYglxHappenAmt(
        	                    ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getYglxHappenAmt()));
        	        }
        	        else {
        	            // round(sum(增值税账务明细余额.当日结转累计发生额),2) 发生结转金额
        	            amtDto.setLxqrfsCarryAmt(
        	                    ArithmeticUtil.doAdd(item.getCarryAmt(), amtDto.getLxqrfsCarryAmt()));
        	            // round(sum(增值税账务明细余额.当日计税收入发生额),2) 收入发生额
        	            amtDto.setQrlxIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeHappenAmt(),
        	                    amtDto.getQrlxIncomeAmtAll()));
        	            // round(sum(增值税账务明细余额.当日发生额),2) 发生额
        	            amtDto.setQrlxHappenAmt(
        	                    ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getQrlxHappenAmt()));
        	        }
        	        
        	    }
        	    else if (item.getVatType().equals(DictCodeList.增值税类型_红利收入.getValue())) {
        	        if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
        	            // round(sum(增值税账务明细余额.当日结转累计发生额),2) 预估结转金额
        	            amtDto.setHlqrygCarryAmt(
        	                    ArithmeticUtil.doAdd(item.getCarryAmt(), amtDto.getHlqrygCarryAmt()));
        	            // round(sum(增值税账务明细余额.当日计税收入发生额),2) 收入发生额
        	            amtDto.setYghlIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeHappenAmt(),
        	                    amtDto.getYghlIncomeAmtAll()));
        	            // round(sum(增值税账务明细余额.当日发生额),2) 发生额
        	            amtDto.setYghlHappenAmt(
        	                    ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getYghlHappenAmt()));
        	        }
        	        else {
        	            // round(sum(增值税账务明细余额.当日结转累计发生额),2) 发生结转金额
        	            amtDto.setHlqrfsCarryAmt(
        	                    ArithmeticUtil.doAdd(item.getCarryAmt(), amtDto.getHlqrfsCarryAmt()));
        	            // round(sum(增值税账务明细余额.当日计税收入发生额),2) 收入发生额
        	            amtDto.setQrhlIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeHappenAmt(),
        	                    amtDto.getQrhlIncomeAmtAll()));
        	            // round(sum(增值税账务明细余额.当日发生额),2) 发生额
        	            amtDto.setQrhlHappenAmt(
        	                    ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getQrhlHappenAmt()));
        	        }
        	    }
        	    else if (item.getVatType().equals(DictCodeList.增值税类型_价差收入.getValue())) {
        	        if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
        	            // round(sum(增值税账务明细余额.当日结转累计发生额),2) 收入发生额
        	            amtDto.setYgjcIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeHappenAmt(),
        	                    amtDto.getYgjcIncomeAmtAll()));
        	            // round(sum(增值税账务明细余额.当日结转累计发生额),2) 发生额
        	            amtDto.setYgjcHappenAmt(
        	                    ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getYgjcHappenAmt()));
        	        }
        	        else {
        	            // round(sum(增值税账务明细余额.当日结转累计发生额),2) 收入发生额
        	            amtDto.setQrjcIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeHappenAmt(),
        	                    amtDto.getQrjcIncomeAmtAll()));
        	            // round(sum(增值税账务明细余额.当日结转累计发生额),2) 发生额
        	            amtDto.setQrjcHappenAmt(
        	                    ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getQrjcHappenAmt()));
        	            if (BigDecimal.ZERO.compareTo(item.getHappenAmt()) < 0) {
        	                amtDto.setQrjcHappenAmtNo(
        	                        ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getQrjcHappenAmtNo()));
        	            }
        	            
        	        }
        	    }
        	}
        	if ("MA".equals(item.getCalcType())) {
                if (item.getVatType().equals(DictCodeList.增值税类型_利息收入.getValue())) {
                    if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        amtDto.setMayglxAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getMayglxAmt()));
                    }
                    else {
                        amtDto.setMaqrlxAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getMaqrlxAmt()));
                    }
                    
                }
                else if (item.getVatType().equals(DictCodeList.增值税类型_红利收入.getValue())) {
                    if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        amtDto.setMayghlAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getMayghlAmt()));
                    }
                    else {
                        amtDto.setMaqrhlAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getMaqrhlAmt()));
                    }
                }
                else if (item.getVatType().equals(DictCodeList.增值税类型_价差收入.getValue())) {
                    if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        amtDto.setMaygjcAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getMaygjcAmt()));
                    }
                    else {
                        amtDto.setMaqrjcAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getMaqrjcAmt()));
                    }
                }
               
        	}
        }
        for (TaxBokBalance item : listValTaxBokBalance) {
            // IF 增值税账务明细余额.计税收入余额 = 0
            if (BigDecimal.ZERO.compareTo(item.getIncomeAmtAll()) == 0) {
                // 当日发生额 = 当日发生额 + -1 * 计税余额
                item.setHappenAmt(ArithmeticUtil.doAdd(item.getHappenAmt(),
                    ArithmeticUtil.multiply(new BigDecimal(-1), item.getTaxAmt())));
            }
        }

        return amtDto;

    }
    public static void  formatAmtDtoByScale(TaxBalanceAmtDto amtDto, int intScale, int jcScale) {
        // amtDto 各金额位数精确到scale位
        amtDto.setHlqrfsCarryAmt(ArithmeticUtil.round(amtDto.getHlqrfsCarryAmt(), intScale, null));
        amtDto.setHlqrygCarryAmt(ArithmeticUtil.round(amtDto.getHlqrygCarryAmt(), intScale, null));
        amtDto.setLxqrfsCarryAmt(ArithmeticUtil.round(amtDto.getLxqrfsCarryAmt(), intScale, null));
        amtDto.setLxqrygCarryAmt(ArithmeticUtil.round(amtDto.getLxqrygCarryAmt(), intScale, null));
        amtDto.setQrhlHappenAmt(ArithmeticUtil.round(amtDto.getQrhlHappenAmt(), intScale, null));
        amtDto.setQrhlIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getQrhlIncomeAmtAll(), intScale, null));
        amtDto.setQrjcHappenAmt(ArithmeticUtil.round(amtDto.getQrjcHappenAmt(), jcScale, null));
        amtDto.setQrjcIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getQrjcIncomeAmtAll(), intScale, null));
        amtDto.setQrlxHappenAmt(ArithmeticUtil.round(amtDto.getQrlxHappenAmt(), intScale, null));
        amtDto.setQrlxIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getQrlxIncomeAmtAll(), intScale, null));
        amtDto.setYghlHappenAmt(ArithmeticUtil.round(amtDto.getYghlHappenAmt(), intScale, null));
        amtDto.setYghlIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getYghlIncomeAmtAll(), intScale, null));
        amtDto.setYgjcHappenAmt(ArithmeticUtil.round(amtDto.getYgjcHappenAmt(), jcScale, null));
        amtDto.setYgjcIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getYgjcIncomeAmtAll(), intScale, null));
        amtDto.setYglxHappenAmt(ArithmeticUtil.round(amtDto.getYglxHappenAmt(), intScale, null));
        amtDto.setYglxIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getYglxIncomeAmtAll(), intScale, null));
    }

    /**
     * 获取 maygjcAmt
     * @return maygjcAmt.
     */
    public BigDecimal getMaygjcAmt() {
        return maygjcAmt;
    }

    /**
     * 设置 maygjcAmt
     * @param maygjcAmt maygjcAmt
     */
    public void setMaygjcAmt(BigDecimal maygjcAmt) {
        this.maygjcAmt = maygjcAmt;
    }

    /**
     * 获取 maqrjcAmt
     * @return maqrjcAmt.
     */
    public BigDecimal getMaqrjcAmt() {
        return maqrjcAmt;
    }

    /**
     * 设置 maqrjcAmt
     * @param maqrjcAmt maqrjcAmt
     */
    public void setMaqrjcAmt(BigDecimal maqrjcAmt) {
        this.maqrjcAmt = maqrjcAmt;
    }

    /**
     * 获取 mayglxAmt
     * @return mayglxAmt.
     */
    public BigDecimal getMayglxAmt() {
        return mayglxAmt;
    }

    /**
     * 设置 mayglxAmt
     * @param mayglxAmt mayglxAmt
     */
    public void setMayglxAmt(BigDecimal mayglxAmt) {
        this.mayglxAmt = mayglxAmt;
    }

    /**
     * 获取 maqrlxAmt
     * @return maqrlxAmt.
     */
    public BigDecimal getMaqrlxAmt() {
        return maqrlxAmt;
    }

    /**
     * 设置 maqrlxAmt
     * @param maqrlxAmt maqrlxAmt
     */
    public void setMaqrlxAmt(BigDecimal maqrlxAmt) {
        this.maqrlxAmt = maqrlxAmt;
    }

    /**
     * 获取 mayghlAmt
     * @return mayghlAmt.
     */
    public BigDecimal getMayghlAmt() {
        return mayghlAmt;
    }

    /**
     * 设置 mayghlAmt
     * @param mayghlAmt mayghlAmt
     */
    public void setMayghlAmt(BigDecimal mayghlAmt) {
        this.mayghlAmt = mayghlAmt;
    }

    /**
     * 获取 maqrhlAmt
     * @return maqrhlAmt.
     */
    public BigDecimal getMaqrhlAmt() {
        return maqrhlAmt;
    }

    /**
     * 设置 maqrhlAmt
     * @param maqrhlAmt maqrhlAmt
     */
    public void setMaqrhlAmt(BigDecimal maqrhlAmt) {
        this.maqrhlAmt = maqrhlAmt;
    }

}

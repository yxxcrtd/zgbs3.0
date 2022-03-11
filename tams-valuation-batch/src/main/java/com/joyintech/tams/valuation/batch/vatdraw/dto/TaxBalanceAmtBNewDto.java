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
import com.joyintech.tams.constant.DictCodeList;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 增值税余额汇总金额类Dto
 * 
 * @author weipeng
 * @version 1.0 2018年8月16日
 * @see TaxBalanceAmtBNewDto
 * @since 1.0
 */
public class TaxBalanceAmtBNewDto {

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
    private BigDecimal fslxIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 确认利息 发生额
     */
    private BigDecimal fslxHappenAmt = BigDecimal.ZERO;

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
    private BigDecimal fshlIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 确认红利 发生额
     */
    private BigDecimal fshlHappenAmt = BigDecimal.ZERO;

    /**
     * 确认价差 收入发生额
     */
    private BigDecimal fsjcIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 确认价差收入 当日发生额
     */
    private BigDecimal fsjcHappenAmt = BigDecimal.ZERO;
    
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

//    /**
//     * 预估价差收入 增值税金额
//     */
//    private Map<String, TaxBokBalanceDto> ygjcTaxAmtMap = new HashMap<>();
//
//    /**
//     * 预估利息收入 增值税金额
//     */
//    private Map<String, TaxBokBalanceDto> yglxTaxAmtMap = new HashMap<>();
//
//    /**
//     * 预估红利 增值税金额
//     */
//    private Map<String, TaxBokBalanceDto> yghlTaxAmtMap = new HashMap<>();

    /**
     * 预估利息_当期含税销售额本币合计
     */
    private BigDecimal yglxIncomeDurAmt = BigDecimal.ZERO;

    /**
     * 预估利息_当日含税销售额本币合计
     */
    private BigDecimal yglxIncomeHappenAmt = BigDecimal.ZERO;

    /**
     * 预估利息_当日含税销售额本币合计_结转
     */
    private BigDecimal yglxIncomeCarryAmt = BigDecimal.ZERO;

    /**
     * 预估利息_当日含税销售额本币合计_计提
     */
    private BigDecimal yglxIncomeAccrAmt = BigDecimal.ZERO;

    /**
     * 预估红利_当期含税销售额本币合计
     */
    private BigDecimal yghlIncomeDurAmt = BigDecimal.ZERO;

    /**
     * 预估红利_当日含税销售额本币合计
     */
    private BigDecimal yghlIncomeHappenAmt = BigDecimal.ZERO;

    /**
     * 预估红利_当日含税销售额本币合计_结转
     */
    private BigDecimal yghlIncomeCarryAmt = BigDecimal.ZERO;

    /**
     * 预估红利_当日含税销售额本币合计_计提
     */
    private BigDecimal yghlIncomeAccrAmt = BigDecimal.ZERO;

    /**
     * 预估价差_当期含税销售额本币合计
     */
    private BigDecimal ygjcIncomeDurAmt = BigDecimal.ZERO;

    /**
     * 预估价差_当日含税销售额本币合计
     */
    private BigDecimal ygjcIncomeHappenAmt = BigDecimal.ZERO;

    /**
     * 预估价差_当日含税销售额本币合计_结转
     */
    private BigDecimal ygjcIncomeCarryAmt = BigDecimal.ZERO;

    /**
     * 预估价差_当日含税销售额本币合计_计提
     */
    private BigDecimal ygjcIncomeAccrAmt = BigDecimal.ZERO;

    /**
     * 发生利息_当期含税销售额本币合计
     */
    private BigDecimal fslxIncomeDurAmt = BigDecimal.ZERO;

    /**
     * 发生利息_当日含税销售额本币合计
     */
    private BigDecimal fslxIncomeHappenAmt = BigDecimal.ZERO;

    /**
     * 发生利息_当日含税销售额本币合计_结转
     */
    private BigDecimal fslxIncomeCarryAmt = BigDecimal.ZERO;

    /**
     * 发生利息_当日含税销售额本币合计_计提
     */
    private BigDecimal fslxIncomeAccrAmt = BigDecimal.ZERO;

    /**
     * 发生红利_当期含税销售额本币合计
     */
    private BigDecimal fshlIncomeDurAmt = BigDecimal.ZERO;

    /**
     * 发生红利_当日含税销售额本币合计
     */
    private BigDecimal fshlIncomeHappenAmt = BigDecimal.ZERO;

    /**
     * 发生红利_当日含税销售额本币合计_结转
     */
    private BigDecimal fshlIncomeCarryAmt = BigDecimal.ZERO;

    /**
     * 发生红利_当日含税销售额本币合计_计提
     */
    private BigDecimal fshlIncomeAccrAmt = BigDecimal.ZERO;

    /**
     * 发生价差_当期含税销售额本币合计
     */
    private BigDecimal fsjcIncomeDurAmt = BigDecimal.ZERO;

    /**
     * 发生价差_当日含税销售额本币合计
     */
    private BigDecimal fsjcIncomeHappenAmt = BigDecimal.ZERO;

    /**
     * 发生价差_当日含税销售额本币合计_结转
     */
    private BigDecimal fsjcIncomeCarryAmt = BigDecimal.ZERO;

    /**
     * 发生价差_当日含税销售额本币合计_计提
     */
    private BigDecimal fsjcIncomeAccrAmt = BigDecimal.ZERO;
    /**
     * 预估利息_当日增值税额本币_余额计算
     */
    private BigDecimal yglxTdyBalCalcTaxAmt = BigDecimal.ZERO;

    /**
     * 预估红利_当日增值税额本币_余额计算
     */
    private BigDecimal yghlTdyBalCalcTaxAmt = BigDecimal.ZERO;

    /**
     * 预估价差_当日增值税额本币_余额计算
     */
    private BigDecimal ygjcTdyBalCalcTaxAmt = BigDecimal.ZERO;

    /**
     * 发生利息_当日增值税额本币_余额计算
     */
    private BigDecimal fslxTdyBalCalcTaxAmt = BigDecimal.ZERO;

    /**
     * 发生红利_当日增值税额本币_余额计算
     */
    private BigDecimal fshlTdyBalCalcTaxAmt = BigDecimal.ZERO;

    /**
     * 发生价差_当日增值税额本币_余额计算
     */
    private BigDecimal fsjcTdyBalCalcTaxAmt = BigDecimal.ZERO;


    /**
     * 手工凭证_预估利息_含税销售额
     */
    private BigDecimal maYglxIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 手工凭证_预估红利_含税销售额
     */
    private BigDecimal maYghlIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 手工凭证_预估价差_含税销售额
     */
    private BigDecimal maYgjcIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 手工凭证_发生利息_含税销售额
     */
    private BigDecimal maFslxIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 手工凭证_发生红利_含税销售额
     */
    private BigDecimal maFshlIncomeAmtAll = BigDecimal.ZERO;

    /**
     * 手工凭证_发生价差_含税销售额
     */
    private BigDecimal maFsjcIncomeAmtAll = BigDecimal.ZERO;

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
     * @param amtDtoMap
     * @param listValTaxBokBalance 原税务余额
     * @param groupedTaxBokBalanceList 汇总后的税务余额
     * @param exRateMem
     * @return
     */
    public static TaxBalanceAmtBNewDto createDtoByBalance(List<TaxBokBalanceDto> listValTaxBokBalance)
        throws InvocationTargetException, IllegalAccessException {
        TaxBalanceAmtBNewDto amtDto = new TaxBalanceAmtBNewDto();
//        // added by wangzhiang 【辽阳银行】利息收入增加参数，支持按资产维度先初步汇总舍位后在进行账套级别汇总 20191115 start
//        List<TaxBokBalanceDto> sumList = null;
//        if (CollectionUtils.isNotEmpty(groupedTaxBokBalanceList)) {
//            sumList = groupedTaxBokBalanceList;
//        }
//        else {
//            sumList = listValTaxBokBalance;
//        }
//        // added by wangzhiang 【辽阳银行】利息收入增加参数，支持按资产维度先初步汇总舍位后在进行账套级别汇总 20191115 end
        for (TaxBokBalanceDto item : listValTaxBokBalance) {
        	if (DictCodeList.计税方式_简易计税.getValue().equals(item.getCalcType())) {
        	    if (item.getVatType().equals(DictCodeList.增值税类型_利息收入.getValue())) {
        	        if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计),2) 当日含税销售额本币合计
                        amtDto.setYglxIncomeHappenAmt(ArithmeticUtil.doAdd(item.getIncomeHappenAmtB(),
                            amtDto.getYglxIncomeHappenAmt()));
        	            // round(sum(增值税账务明细余额.当日税额本币合计_结转),2) 预估结转金额
        	            amtDto.setLxqrygCarryAmt(
        	                    ArithmeticUtil.doAdd(item.getCarryAmtB(), amtDto.getLxqrygCarryAmt()));
        	            // round(sum(增值税账务明细余额.当日含税销售额本币合计_结转),2) 含税销售额合计_结转
        	            amtDto.setYglxIncomeCarryAmt(ArithmeticUtil.doAdd(item.getIncomeCarryAmtB(),
                            amtDto.getYglxIncomeCarryAmt()));
        	            // round(sum(增值税账务明细余额.当日税额本币合计_计提),2) 当日税额本币合计_计提
                        amtDto.setYglxHappenAmt(ArithmeticUtil.doAdd(item.getHappenAmtB(),
                            amtDto.getYglxHappenAmt()));
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计_计提),2) 含税销售额合计_计提
                        amtDto.setYglxIncomeAccrAmt(ArithmeticUtil.doAdd(item.getIncomeAccrAmtB(),
                            amtDto.getYglxIncomeAccrAmt()));
                        // round(sum(增值税账务明细余额.当日增值税额本币_余额计算),2) 当日增值税额本币_余额计算
                        amtDto.setYglxTdyBalCalcTaxAmt(ArithmeticUtil.doAdd(item.getTdyBalCalcTaxAmtB(),
                            amtDto.getYglxTdyBalCalcTaxAmt()));

                        //add by xr for TISHKBS-1198 start
                        // round(sum(增值税账务明细余额.当日增值税额本币_余额计算),2) 当日增值税额本币_余额计算_含结转
                        amtDto.setYglxTdyBalCalcTaxAmtHjz(ArithmeticUtil.doAdd(item.getTdyBalCalcTaxAmtBHjz(),
                                amtDto.getYglxTdyBalCalcTaxAmtHjz()));
                        //add by xr for TISHKBS-1198 end

                        // round(增值税资产余额.含税销售额本币合计,2) 含税销售额余额
                        amtDto.setYglxIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getYglxIncomeAmtAll()));
                        // round(增值税资产余额.当期含税销售额本币合计,2) 当期含税销售额余额
                        amtDto.setYglxIncomeDurAmt(ArithmeticUtil.doAdd(item.getIncomeDurAmt(),
                            amtDto.getYglxIncomeDurAmt()));
//                         TODO
//                        if (amtDto.getYglxTaxAmtMap().containsKey(item.getoCcy())) {
//                            TaxBokBalanceDto entity = amtDto.getYglxTaxAmtMap().get(item.getoCcy());
//                            entity.setTaxAmt(ArithmeticUtil.doAdd(entity.getTaxAmt(), item.getTaxAmt()));
//                            entity.setIncomeAmtAll(
//                                ArithmeticUtil.doAdd(entity.getIncomeAmtAll(), item.getIncomeAmtAll()));
//                        } else {
//                            TaxBokBalanceDto entity = new TaxBokBalanceDto();
//                            BeanUtil.copyProperties(entity, item);
//                            amtDto.getYglxTaxAmtMap().put(item.getoCcy(), entity);
//                        }
        	        }
        	        else {
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计),2) 当日含税销售额本币合计
                        amtDto.setFslxIncomeHappenAmt(ArithmeticUtil.doAdd(item.getIncomeHappenAmtB(),
                            amtDto.getFslxIncomeHappenAmt()));
        	            // round(sum(增值税账务明细余额.当日税额本币合计_结转),2) 发生结转金额
        	            amtDto.setLxqrfsCarryAmt(
        	                    ArithmeticUtil.doAdd(item.getCarryAmtB(), amtDto.getLxqrfsCarryAmt()));
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计_结转),2) 含税销售额合计_结转
                        amtDto.setFslxIncomeCarryAmt(ArithmeticUtil.doAdd(item.getIncomeCarryAmtB(),
                            amtDto.getFslxIncomeCarryAmt()));
                        // round(sum(增值税账务明细余额.当日税额本币合计_计提),2) 当日税额本币合计_计提
                        amtDto.setFslxHappenAmt(ArithmeticUtil.doAdd(item.getHappenAmtB(),
                            amtDto.getFslxHappenAmt()));
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计_计提),2) 含税销售额合计_计提
                        amtDto.setFslxIncomeAccrAmt(ArithmeticUtil.doAdd(item.getIncomeAccrAmtB(),
                            amtDto.getFslxIncomeAccrAmt()));
                        // round(sum(增值税账务明细余额.当日增值税额本币_余额计算),2) 当日增值税额本币_余额计算
                        amtDto.setFslxTdyBalCalcTaxAmt(ArithmeticUtil.doAdd(item.getTdyBalCalcTaxAmtB(),
                            amtDto.getFslxTdyBalCalcTaxAmt()));
                        // round(增值税资产余额.含税销售额本币合计,2) 含税销售额余额
                        amtDto.setFslxIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getFslxIncomeAmtAll()));
                        // round(增值税资产余额.当期含税销售额本币合计,2) 当期含税销售额余额
                        amtDto.setFslxIncomeDurAmt(ArithmeticUtil.doAdd(item.getIncomeDurAmt(),
                            amtDto.getFslxIncomeDurAmt()));
        	        }
        	        
        	    }
        	    else if (item.getVatType().equals(DictCodeList.增值税类型_红利收入.getValue())) {
        	        if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计),2) 当日含税销售额本币合计
                        amtDto.setYghlIncomeHappenAmt(ArithmeticUtil.doAdd(item.getIncomeHappenAmtB(),
                            amtDto.getYghlIncomeHappenAmt()));
        	            // round(sum(增值税账务明细余额.当日税额本币合计_结转),2) 预估结转金额
        	            amtDto.setHlqrygCarryAmt(
        	                    ArithmeticUtil.doAdd(item.getCarryAmtB(), amtDto.getHlqrygCarryAmt()));
        	            // round(sum(增值税账务明细余额.当日含税销售额本币合计),2) 含税销售额合计_结转
        	            amtDto.setYghlIncomeCarryAmt(ArithmeticUtil.doAdd(item.getIncomeCarryAmtB(),
        	                    amtDto.getYghlIncomeCarryAmt()));
        	            // round(sum(增值税账务明细余额.当日税额本币合计_计提),2) 当日税额本币合计_计提
        	            amtDto.setYghlHappenAmt(
        	                    ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getYghlHappenAmt()));
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计_计提),2) 含税销售额合计_计提
                        amtDto.setYghlIncomeAccrAmt(ArithmeticUtil.doAdd(item.getIncomeAccrAmtB(),
                            amtDto.getYghlIncomeAccrAmt()));
                        // round(sum(增值税账务明细余额.当日增值税额本币_余额计算),2) 当日增值税额本币_余额计算
                        amtDto.setYghlTdyBalCalcTaxAmt(ArithmeticUtil.doAdd(item.getTdyBalCalcTaxAmtB(),
                            amtDto.getYghlTdyBalCalcTaxAmt()));
                        // round(增值税资产余额.含税销售额本币合计,2) 含税销售额余额
                        amtDto.setYghlIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getYghlIncomeAmtAll()));
                        // round(增值税资产余额.当期含税销售额本币合计,2) 当期含税销售额余额
                        amtDto.setYghlIncomeDurAmt(ArithmeticUtil.doAdd(item.getIncomeDurAmt(),
                            amtDto.getYghlIncomeDurAmt()));
                        // TODO
//                        if (amtDto.getYghlTaxAmtMap().containsKey(item.getoCcy())) {
//                            TaxBokBalanceDto entity = amtDto.getYghlTaxAmtMap().get(item.getoCcy());
//                            entity.setTaxAmt(ArithmeticUtil.doAdd(entity.getTaxAmt(), item.getTaxAmt()));
//                            entity.setIncomeAmtAll(
//                                ArithmeticUtil.doAdd(entity.getIncomeAmtAll(), item.getIncomeAmtAll()));
//                        } else {
//                            TaxBokBalanceDto entity = new TaxBokBalanceDto();
//                            BeanUtil.copyProperties(entity, item);
//                            amtDto.getYghlTaxAmtMap().put(item.getoCcy(), entity);
//                        }
        	        }
        	        else {
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计),2) 当日含税销售额本币合计
                        amtDto.setFshlIncomeHappenAmt(ArithmeticUtil.doAdd(item.getIncomeHappenAmtB(),
                            amtDto.getFshlIncomeHappenAmt()));
        	            // round(sum(增值税账务明细余额.当日税额本币合计_结转),2) 发生结转金额
        	            amtDto.setHlqrfsCarryAmt(
        	                    ArithmeticUtil.doAdd(item.getCarryAmtB(), amtDto.getHlqrfsCarryAmt()));
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计),2) 含税销售额合计_结转
                        amtDto.setFshlIncomeCarryAmt(ArithmeticUtil.doAdd(item.getIncomeCarryAmtB(),
                            amtDto.getFshlIncomeCarryAmt()));
                        // round(sum(增值税账务明细余额.当日税额本币合计_计提),2) 当日税额本币合计_计提
        	            amtDto.setFshlHappenAmt(
        	                    ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getFshlHappenAmt()));
                        // round(sum(增值税账务明细余额.当日含税销售额本币合计_计提),2) 含税销售额合计_计提
                        amtDto.setFshlIncomeAccrAmt(ArithmeticUtil.doAdd(item.getIncomeAccrAmtB(),
                            amtDto.getFshlIncomeAccrAmt()));
                        // round(sum(增值税账务明细余额.当日增值税额本币_余额计算),2) 当日增值税额本币_余额计算
                        amtDto.setFshlTdyBalCalcTaxAmt(ArithmeticUtil.doAdd(item.getTdyBalCalcTaxAmtB(),
                            amtDto.getFshlTdyBalCalcTaxAmt()));
                        // round(增值税资产余额.含税销售额本币合计,2) 含税销售额余额
                        amtDto.setFshlIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getFshlIncomeAmtAll()));
                        // round(增值税资产余额.当期含税销售额本币合计,2) 当期含税销售额余额
                        amtDto.setFshlIncomeDurAmt(ArithmeticUtil.doAdd(item.getIncomeDurAmt(),
                            amtDto.getFshlIncomeDurAmt()));
        	        }
        	    }
        	    else if (item.getVatType().equals(DictCodeList.增值税类型_价差收入.getValue())) {
        	        if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
        	            // round(sum(增值税账务明细余额.当日含税销售额本币合计),2) 当日含税销售额本币合计
                        amtDto.setYgjcIncomeHappenAmt(ArithmeticUtil.doAdd(item.getIncomeHappenAmt(),
                            amtDto.getYgjcIncomeHappenAmt()));
                        // round(sum(增值税账务明细余额.当日发生额),2) 发生额
                        amtDto.setYgjcHappenAmt(
                            ArithmeticUtil.doAdd(item.getHappenAmt(), amtDto.getYgjcHappenAmt()));
                        // round(sum(增值税账务明细余额.当日增值税额本币_余额计算),2) 当日增值税额本币_余额计算
                        amtDto.setYgjcTdyBalCalcTaxAmt(ArithmeticUtil.doAdd(item.getTdyBalCalcTaxAmtB(),
                            amtDto.getYgjcTdyBalCalcTaxAmt()));
                        // round(增值税资产余额.含税销售额本币合计,2) 含税销售额余额
                        amtDto.setYgjcIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getYgjcIncomeAmtAll()));
                        // round(增值税资产余额.当期含税销售额本币合计,2) 当期含税销售额余额
                        amtDto.setYgjcIncomeDurAmt(ArithmeticUtil.doAdd(item.getIncomeDurAmt(),
                            amtDto.getYgjcIncomeDurAmt()));
//                        if (amtDto.getYgjcTaxAmtMap().containsKey(item.getoCcy())) {
//                            TaxBokBalanceDto entity = amtDto.getYgjcTaxAmtMap().get(item.getoCcy());
//                            entity.setTaxAmt(ArithmeticUtil.doAdd(entity.getTaxAmt(), item.getTaxAmt()));
//                            entity.setIncomeAmtAll(
//                                ArithmeticUtil.doAdd(entity.getIncomeAmtAll(), item.getIncomeAmtAll()));
//                        } else {
//                            TaxBokBalanceDto entity = new TaxBokBalanceDto();
//                            BeanUtil.copyProperties(entity, item);
//                            amtDto.getYgjcTaxAmtMap().put(item.getoCcy(), entity);
//                        }
        	        }
        	        else {
        	            // round(sum(增值税账务明细余额.当日含税销售额本币合计),2) 当日含税销售额本币合计
        	            amtDto.setFsjcIncomeHappenAmt(ArithmeticUtil.doAdd(item.getIncomeHappenAmtB(),
        	                    amtDto.getFsjcIncomeHappenAmt()));
        	            // round(sum(增值税账务明细余额.当日结转累计发生额),2) 发生额
        	            amtDto.setFsjcHappenAmt(
        	                    ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getFsjcHappenAmt()));
        	            if (BigDecimal.ZERO.compareTo(item.getHappenAmtB()) < 0) {
        	                amtDto.setQrjcHappenAmtNo(
        	                        ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getQrjcHappenAmtNo()));
        	            }
                        // round(sum(增值税账务明细余额.当日增值税额本币_余额计算),2) 当日增值税额本币_余额计算
                        amtDto.setFsjcTdyBalCalcTaxAmt(ArithmeticUtil.doAdd(item.getTdyBalCalcTaxAmtB(),
                            amtDto.getFsjcTdyBalCalcTaxAmt()));
                        // round(增值税资产余额.含税销售额本币合计,2) 含税销售额余额
                        amtDto.setFsjcIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getFsjcIncomeAmtAll()));
                        // round(增值税资产余额.当期含税销售额本币合计,2) 当期含税销售额余额
                        amtDto.setFsjcIncomeDurAmt(ArithmeticUtil.doAdd(item.getIncomeDurAmt(),
                            amtDto.getFsjcIncomeDurAmt()));
        	        }
        	    }
        	}
        	if ("MA".equals(item.getCalcType())) {
                if (item.getVatType().equals(DictCodeList.增值税类型_利息收入.getValue())) {
                    if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        amtDto.setMayglxAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getMayglxAmt()));
                        // 含税销售额合计
                        amtDto.setMaYglxIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getMaYglxIncomeAmtAll()));
//                        if (amtDto.getYglxTaxAmtMap().containsKey(item.getoCcy())) {
//                            TaxBokBalanceDto entity = amtDto.getYglxTaxAmtMap().get(item.getoCcy());
//                            entity.setTaxAmt(ArithmeticUtil.doAdd(entity.getTaxAmt(), item.getTaxAmt()));
//                            entity.setIncomeAmtAll(
//                                ArithmeticUtil.doAdd(entity.getIncomeAmtAll(), item.getIncomeAmtAll()));
//                        } else {
//                            TaxBokBalanceDto entity = new TaxBokBalanceDto();
//                            BeanUtil.copyProperties(entity, item);
//                            amtDto.getYglxTaxAmtMap().put(item.getoCcy(), entity);
//                        }
                    }
                    else {
                        amtDto.setMaqrlxAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getMaqrlxAmt()));
                        // 含税销售额合计
                        amtDto.setMaFslxIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getMaFslxIncomeAmtAll()));
                    }
                    
                }
                else if (item.getVatType().equals(DictCodeList.增值税类型_红利收入.getValue())) {
                    if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        amtDto.setMayghlAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getMayghlAmt()));
                        // 含税销售额合计
                        amtDto.setMaYghlIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getYghlIncomeAmtAll()));
//                        if (amtDto.getYghlTaxAmtMap().containsKey(item.getoCcy())) {
//                            TaxBokBalanceDto entity = amtDto.getYghlTaxAmtMap().get(item.getoCcy());
//                            entity.setTaxAmt(ArithmeticUtil.doAdd(entity.getTaxAmt(), item.getTaxAmt()));
//                            entity.setIncomeAmtAll(
//                                ArithmeticUtil.doAdd(entity.getIncomeAmtAll(), item.getIncomeAmtAll()));
//                        } else {
//                            TaxBokBalanceDto entity = new TaxBokBalanceDto();
//                            BeanUtil.copyProperties(entity, item);
//                            amtDto.getYghlTaxAmtMap().put(item.getoCcy(), entity);
//                        }
                    }
                    else {
                        amtDto.setMaqrhlAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getMaqrhlAmt()));
                        // 含税销售额合计
                        amtDto.setMaFshlIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getFshlIncomeAmtAll()));
                    }
                }
                else if (item.getVatType().equals(DictCodeList.增值税类型_价差收入.getValue())) {
                    if (item.getTaxPType().equals(DictCodeList.增值税_确认类型_预估计税.getValue())) {
                        amtDto.setMaygjcAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getMaygjcAmt()));
                        // 含税销售额合计
                        amtDto.setMaYgjcIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getYgjcIncomeAmtAll()));
//                        if (amtDto.getYgjcTaxAmtMap().containsKey(item.getoCcy())) {
//                            TaxBokBalanceDto entity = amtDto.getYgjcTaxAmtMap().get(item.getoCcy());
//                            entity.setTaxAmt(ArithmeticUtil.doAdd(entity.getTaxAmt(), item.getTaxAmt()));
//                            entity.setIncomeAmtAll(
//                                ArithmeticUtil.doAdd(entity.getIncomeAmtAll(), item.getIncomeAmtAll()));
//                        } else {
//                            TaxBokBalanceDto entity = new TaxBokBalanceDto();
//                            BeanUtil.copyProperties(entity, item);
//                            amtDto.getYgjcTaxAmtMap().put(item.getoCcy(), entity);
//                        }
                    }
                    else {
                        amtDto.setMaqrjcAmt(
                                ArithmeticUtil.doAdd(item.getHappenAmtB(), amtDto.getMaqrjcAmt()));
                        // 含税销售额合计
                        amtDto.setMaFsjcIncomeAmtAll(ArithmeticUtil.doAdd(item.getIncomeAmtAllB(),
                            amtDto.getFsjcIncomeAmtAll()));
                    }
                }
               
        	}
        }

        for (TaxBokBalanceDto item : listValTaxBokBalance) {
            // IF 增值税账务明细余额.计税收入余额 = 0
            if (BigDecimal.ZERO.compareTo(item.getIncomeAmtAll()) == 0) {
                // 当日发生额 = 当日发生额 + -1 * 计税余额
                item.setHappenAmt(ArithmeticUtil.doAdd(item.getHappenAmt(),
                    ArithmeticUtil.multiply(new BigDecimal(-1), item.getTaxAmt())));
                // 当日发生额 = 当日发生额 + -1 * 计税余额
                item.setHappenAmtB(ArithmeticUtil.doAdd(item.getHappenAmtB(),
                    ArithmeticUtil.multiply(new BigDecimal(-1), item.getTaxAmtB())));
            }
        }

        return amtDto;

    }
    public static void  formatAmtDtoByScale(TaxBalanceAmtBNewDto amtDto, int intScale, int jcScale) {
        // amtDto 各金额位数精确到scale位
        amtDto.setHlqrfsCarryAmt(ArithmeticUtil.round(amtDto.getHlqrfsCarryAmt(), intScale, null));
        amtDto.setHlqrygCarryAmt(ArithmeticUtil.round(amtDto.getHlqrygCarryAmt(), intScale, null));
        amtDto.setLxqrfsCarryAmt(ArithmeticUtil.round(amtDto.getLxqrfsCarryAmt(), intScale, null));
        amtDto.setLxqrygCarryAmt(ArithmeticUtil.round(amtDto.getLxqrygCarryAmt(), intScale, null));
        amtDto.setFshlHappenAmt(ArithmeticUtil.round(amtDto.getFshlHappenAmt(), intScale, null));
        amtDto.setFshlIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getFshlIncomeAmtAll(), intScale, null));
        amtDto.setFsjcHappenAmt(ArithmeticUtil.round(amtDto.getFsjcHappenAmt(), jcScale, null));
        amtDto.setFsjcIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getFsjcIncomeAmtAll(), intScale, null));
        amtDto.setFslxHappenAmt(ArithmeticUtil.round(amtDto.getFslxHappenAmt(), intScale, null));
        amtDto.setFslxIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getFslxIncomeAmtAll(), intScale, null));
        amtDto.setYghlHappenAmt(ArithmeticUtil.round(amtDto.getYghlHappenAmt(), intScale, null));
        amtDto.setYghlIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getYghlIncomeAmtAll(), intScale, null));
        amtDto.setYgjcHappenAmt(ArithmeticUtil.round(amtDto.getYgjcHappenAmt(), jcScale, null));
        amtDto.setYgjcIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getYgjcIncomeAmtAll(), intScale, null));
        amtDto.setYglxHappenAmt(ArithmeticUtil.round(amtDto.getYglxHappenAmt(), intScale, null));
        amtDto.setYglxIncomeAmtAll(
            ArithmeticUtil.round(amtDto.getYglxIncomeAmtAll(), intScale, null));
//        amtDto.getYglxTaxAmtMap().forEach((x, y) -> {
//            y.setTaxAmt(ArithmeticUtil.round(y.getTaxAmt(), intScale, RoundingMode.HALF_UP));
//            y.setIncomeAmtAll(ArithmeticUtil.round(y.getIncomeAmtAll(), intScale, RoundingMode.HALF_UP));
//        });
//        amtDto.getYghlTaxAmtMap().forEach((x, y) -> {
//            y.setTaxAmt(ArithmeticUtil.round(y.getTaxAmt(), intScale, RoundingMode.HALF_UP));
//            y.setIncomeAmtAll(ArithmeticUtil.round(y.getIncomeAmtAll(), intScale, RoundingMode.HALF_UP));
//        });
//        amtDto.getYgjcTaxAmtMap().forEach((x, y) -> {
//            y.setTaxAmt(ArithmeticUtil.round(y.getTaxAmt(), intScale, RoundingMode.HALF_UP));
//            y.setIncomeAmtAll(ArithmeticUtil.round(y.getIncomeAmtAll(), intScale, RoundingMode.HALF_UP));
//        });

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

//    public Map<String, TaxBokBalanceDto> getYgjcTaxAmtMap() {
//        return ygjcTaxAmtMap;
//    }
//
//    public void setYgjcTaxAmtMap(Map<String, TaxBokBalanceDto> ygjcTaxAmtMap) {
//        this.ygjcTaxAmtMap = ygjcTaxAmtMap;
//    }
//
//    public Map<String, TaxBokBalanceDto> getYglxTaxAmtMap() {
//        return yglxTaxAmtMap;
//    }
//
//
//    public void setYglxTaxAmtMap(Map<String, TaxBokBalanceDto> yglxTaxAmtMap) {
//        this.yglxTaxAmtMap = yglxTaxAmtMap;
//    }
//
//    public Map<String, TaxBokBalanceDto> getYghlTaxAmtMap() {
//        return yghlTaxAmtMap;
//    }
//
//    public void setYghlTaxAmtMap(Map<String, TaxBokBalanceDto> yghlTaxAmtMap) {
//        this.yghlTaxAmtMap = yghlTaxAmtMap;
//    }

    /**
     * Gets the value of yglxIncomeCarryAmt.
     *
     * @return the value of yglxIncomeCarryAmt
     */
    public BigDecimal getYglxIncomeCarryAmt() {
        return yglxIncomeCarryAmt;
    }

    /**
     * Sets the yglxIncomeCarryAmt.
     * <p>You can use getYglxIncomeCarryAmt() to get the value of yglxIncomeCarryAmt</p>
     *
     * @param yglxIncomeCarryAmt yglxIncomeCarryAmt
     */
    public void setYglxIncomeCarryAmt(BigDecimal yglxIncomeCarryAmt) {
        this.yglxIncomeCarryAmt = yglxIncomeCarryAmt;
    }

    /**
     * Gets the value of yglxIncomeAccrAmt.
     *
     * @return the value of yglxIncomeAccrAmt
     */
    public BigDecimal getYglxIncomeAccrAmt() {
        return yglxIncomeAccrAmt;
    }

    /**
     * Sets the yglxIncomeAccrAmt.
     * <p>You can use getYglxIncomeAccrAmt() to get the value of yglxIncomeAccrAmt</p>
     *
     * @param yglxIncomeAccrAmt yglxIncomeAccrAmt
     */
    public void setYglxIncomeAccrAmt(BigDecimal yglxIncomeAccrAmt) {
        this.yglxIncomeAccrAmt = yglxIncomeAccrAmt;
    }

    /**
     * Gets the value of yghlIncomeCarryAmt.
     *
     * @return the value of yghlIncomeCarryAmt
     */
    public BigDecimal getYghlIncomeCarryAmt() {
        return yghlIncomeCarryAmt;
    }

    /**
     * Sets the yghlIncomeCarryAmt.
     * <p>You can use getYghlIncomeCarryAmt() to get the value of yghlIncomeCarryAmt</p>
     *
     * @param yghlIncomeCarryAmt yghlIncomeCarryAmt
     */
    public void setYghlIncomeCarryAmt(BigDecimal yghlIncomeCarryAmt) {
        this.yghlIncomeCarryAmt = yghlIncomeCarryAmt;
    }

    /**
     * Gets the value of yghlIncomeAccrAmt.
     *
     * @return the value of yghlIncomeAccrAmt
     */
    public BigDecimal getYghlIncomeAccrAmt() {
        return yghlIncomeAccrAmt;
    }

    /**
     * Sets the yghlIncomeAccrAmt.
     * <p>You can use getYghlIncomeAccrAmt() to get the value of yghlIncomeAccrAmt</p>
     *
     * @param yghlIncomeAccrAmt yghlIncomeAccrAmt
     */
    public void setYghlIncomeAccrAmt(BigDecimal yghlIncomeAccrAmt) {
        this.yghlIncomeAccrAmt = yghlIncomeAccrAmt;
    }

    /**
     * Gets the value of fslxIncomeCarryAmt.
     *
     * @return the value of fslxIncomeCarryAmt
     */
    public BigDecimal getFslxIncomeCarryAmt() {
        return fslxIncomeCarryAmt;
    }

    /**
     * Sets the fslxIncomeCarryAmt.
     * <p>You can use getFslxIncomeCarryAmt() to get the value of fslxIncomeCarryAmt</p>
     *
     * @param fslxIncomeCarryAmt fslxIncomeCarryAmt
     */
    public void setFslxIncomeCarryAmt(BigDecimal fslxIncomeCarryAmt) {
        this.fslxIncomeCarryAmt = fslxIncomeCarryAmt;
    }

    /**
     * Gets the value of fslxIncomeAccrAmt.
     *
     * @return the value of fslxIncomeAccrAmt
     */
    public BigDecimal getFslxIncomeAccrAmt() {
        return fslxIncomeAccrAmt;
    }

    /**
     * Sets the fslxIncomeAccrAmt.
     * <p>You can use getFslxIncomeAccrAmt() to get the value of fslxIncomeAccrAmt</p>
     *
     * @param fslxIncomeAccrAmt fslxIncomeAccrAmt
     */
    public void setFslxIncomeAccrAmt(BigDecimal fslxIncomeAccrAmt) {
        this.fslxIncomeAccrAmt = fslxIncomeAccrAmt;
    }

    /**
     * Gets the value of fshlIncomeCarryAmt.
     *
     * @return the value of fshlIncomeCarryAmt
     */
    public BigDecimal getFshlIncomeCarryAmt() {
        return fshlIncomeCarryAmt;
    }

    /**
     * Sets the fshlIncomeCarryAmt.
     * <p>You can use getFshlIncomeCarryAmt() to get the value of fshlIncomeCarryAmt</p>
     *
     * @param fshlIncomeCarryAmt fshlIncomeCarryAmt
     */
    public void setFshlIncomeCarryAmt(BigDecimal fshlIncomeCarryAmt) {
        this.fshlIncomeCarryAmt = fshlIncomeCarryAmt;
    }

    /**
     * Gets the value of fshlIncomeAccrAmt.
     *
     * @return the value of fshlIncomeAccrAmt
     */
    public BigDecimal getFshlIncomeAccrAmt() {
        return fshlIncomeAccrAmt;
    }

    /**
     * Sets the fshlIncomeAccrAmt.
     * <p>You can use getFshlIncomeAccrAmt() to get the value of fshlIncomeAccrAmt</p>
     *
     * @param fshlIncomeAccrAmt fshlIncomeAccrAmt
     */
    public void setFshlIncomeAccrAmt(BigDecimal fshlIncomeAccrAmt) {
        this.fshlIncomeAccrAmt = fshlIncomeAccrAmt;
    }

    /**
     * Gets the value of yglxTdyBalCalcTaxAmt.
     *
     * @return the value of yglxTdyBalCalcTaxAmt
     */
    public BigDecimal getYglxTdyBalCalcTaxAmt() {
        return yglxTdyBalCalcTaxAmt;
    }

    /**
     * Sets the yglxTdyBalCalcTaxAmt.
     * <p>You can use getYglxTdyBalCalcTaxAmt() to get the value of yglxTdyBalCalcTaxAmt</p>
     *
     * @param yglxTdyBalCalcTaxAmt yglxTdyBalCalcTaxAmt
     */
    public void setYglxTdyBalCalcTaxAmt(BigDecimal yglxTdyBalCalcTaxAmt) {
        this.yglxTdyBalCalcTaxAmt = yglxTdyBalCalcTaxAmt;
    }

    /**
     * Gets the value of yghlTdyBalCalcTaxAmt.
     *
     * @return the value of yghlTdyBalCalcTaxAmt
     */
    public BigDecimal getYghlTdyBalCalcTaxAmt() {
        return yghlTdyBalCalcTaxAmt;
    }

    /**
     * Sets the yghlTdyBalCalcTaxAmt.
     * <p>You can use getYghlTdyBalCalcTaxAmt() to get the value of yghlTdyBalCalcTaxAmt</p>
     *
     * @param yghlTdyBalCalcTaxAmt yghlTdyBalCalcTaxAmt
     */
    public void setYghlTdyBalCalcTaxAmt(BigDecimal yghlTdyBalCalcTaxAmt) {
        this.yghlTdyBalCalcTaxAmt = yghlTdyBalCalcTaxAmt;
    }

    /**
     * Gets the value of ygjcTdyBalCalcTaxAmt.
     *
     * @return the value of ygjcTdyBalCalcTaxAmt
     */
    public BigDecimal getYgjcTdyBalCalcTaxAmt() {
        return ygjcTdyBalCalcTaxAmt;
    }

    /**
     * Sets the ygjcTdyBalCalcTaxAmt.
     * <p>You can use getYgjcTdyBalCalcTaxAmt() to get the value of ygjcTdyBalCalcTaxAmt</p>
     *
     * @param ygjcTdyBalCalcTaxAmt ygjcTdyBalCalcTaxAmt
     */
    public void setYgjcTdyBalCalcTaxAmt(BigDecimal ygjcTdyBalCalcTaxAmt) {
        this.ygjcTdyBalCalcTaxAmt = ygjcTdyBalCalcTaxAmt;
    }

    /**
     * Gets the value of fslxTdyBalCalcTaxAmt.
     *
     * @return the value of fslxTdyBalCalcTaxAmt
     */
    public BigDecimal getFslxTdyBalCalcTaxAmt() {
        return fslxTdyBalCalcTaxAmt;
    }

    /**
     * Sets the fslxTdyBalCalcTaxAmt.
     * <p>You can use getFslxTdyBalCalcTaxAmt() to get the value of fslxTdyBalCalcTaxAmt</p>
     *
     * @param fslxTdyBalCalcTaxAmt fslxTdyBalCalcTaxAmt
     */
    public void setFslxTdyBalCalcTaxAmt(BigDecimal fslxTdyBalCalcTaxAmt) {
        this.fslxTdyBalCalcTaxAmt = fslxTdyBalCalcTaxAmt;
    }

    /**
     * Gets the value of fshlTdyBalCalcTaxAmt.
     *
     * @return the value of fshlTdyBalCalcTaxAmt
     */
    public BigDecimal getFshlTdyBalCalcTaxAmt() {
        return fshlTdyBalCalcTaxAmt;
    }

    /**
     * Sets the fshlTdyBalCalcTaxAmt.
     * <p>You can use getFshlTdyBalCalcTaxAmt() to get the value of fshlTdyBalCalcTaxAmt</p>
     *
     * @param fshlTdyBalCalcTaxAmt fshlTdyBalCalcTaxAmt
     */
    public void setFshlTdyBalCalcTaxAmt(BigDecimal fshlTdyBalCalcTaxAmt) {
        this.fshlTdyBalCalcTaxAmt = fshlTdyBalCalcTaxAmt;
    }

    /**
     * Gets the value of fsjcTdyBalCalcTaxAmt.
     *
     * @return the value of fsjcTdyBalCalcTaxAmt
     */
    public BigDecimal getFsjcTdyBalCalcTaxAmt() {
        return fsjcTdyBalCalcTaxAmt;
    }

    /**
     * Sets the fsjcTdyBalCalcTaxAmt.
     * <p>You can use getFsjcTdyBalCalcTaxAmt() to get the value of fsjcTdyBalCalcTaxAmt</p>
     *
     * @param fsjcTdyBalCalcTaxAmt fsjcTdyBalCalcTaxAmt
     */
    public void setFsjcTdyBalCalcTaxAmt(BigDecimal fsjcTdyBalCalcTaxAmt) {
        this.fsjcTdyBalCalcTaxAmt = fsjcTdyBalCalcTaxAmt;
    }

    /**
     * Gets the value of yglxIncomeHappenAmt.
     *
     * @return the value of yglxIncomeHappenAmt
     */
    public BigDecimal getYglxIncomeHappenAmt() {
        return yglxIncomeHappenAmt;
    }

    /**
     * Sets the yglxIncomeHappenAmt.
     * <p>You can use getYglxIncomeHappenAmt() to get the value of yglxIncomeHappenAmt</p>
     *
     * @param yglxIncomeHappenAmt yglxIncomeHappenAmt
     */
    public void setYglxIncomeHappenAmt(BigDecimal yglxIncomeHappenAmt) {
        this.yglxIncomeHappenAmt = yglxIncomeHappenAmt;
    }

    /**
     * Gets the value of yghlIncomeHappenAmt.
     *
     * @return the value of yghlIncomeHappenAmt
     */
    public BigDecimal getYghlIncomeHappenAmt() {
        return yghlIncomeHappenAmt;
    }

    /**
     * Sets the yghlIncomeHappenAmt.
     * <p>You can use getYghlIncomeHappenAmt() to get the value of yghlIncomeHappenAmt</p>
     *
     * @param yghlIncomeHappenAmt yghlIncomeHappenAmt
     */
    public void setYghlIncomeHappenAmt(BigDecimal yghlIncomeHappenAmt) {
        this.yghlIncomeHappenAmt = yghlIncomeHappenAmt;
    }

    /**
     * Gets the value of fslxIncomeHappenAmt.
     *
     * @return the value of fslxIncomeHappenAmt
     */
    public BigDecimal getFslxIncomeHappenAmt() {
        return fslxIncomeHappenAmt;
    }

    /**
     * Sets the fslxIncomeHappenAmt.
     * <p>You can use getFslxIncomeHappenAmt() to get the value of fslxIncomeHappenAmt</p>
     *
     * @param fslxIncomeHappenAmt fslxIncomeHappenAmt
     */
    public void setFslxIncomeHappenAmt(BigDecimal fslxIncomeHappenAmt) {
        this.fslxIncomeHappenAmt = fslxIncomeHappenAmt;
    }

    /**
     * Gets the value of fshlIncomeHappenAmt.
     *
     * @return the value of fshlIncomeHappenAmt
     */
    public BigDecimal getFshlIncomeHappenAmt() {
        return fshlIncomeHappenAmt;
    }

    /**
     * Sets the fshlIncomeHappenAmt.
     * <p>You can use getFshlIncomeHappenAmt() to get the value of fshlIncomeHappenAmt</p>
     *
     * @param fshlIncomeHappenAmt fshlIncomeHappenAmt
     */
    public void setFshlIncomeHappenAmt(BigDecimal fshlIncomeHappenAmt) {
        this.fshlIncomeHappenAmt = fshlIncomeHappenAmt;
    }

    /**
     * Gets the value of ygjcIncomeHappenAmt.
     *
     * @return the value of ygjcIncomeHappenAmt
     */
    public BigDecimal getYgjcIncomeHappenAmt() {
        return ygjcIncomeHappenAmt;
    }

    /**
     * Sets the ygjcIncomeHappenAmt.
     * <p>You can use getYgjcIncomeHappenAmt() to get the value of ygjcIncomeHappenAmt</p>
     *
     * @param ygjcIncomeHappenAmt ygjcIncomeHappenAmt
     */
    public void setYgjcIncomeHappenAmt(BigDecimal ygjcIncomeHappenAmt) {
        this.ygjcIncomeHappenAmt = ygjcIncomeHappenAmt;
    }

    /**
     * Gets the value of ygjcIncomeCarryAmt.
     *
     * @return the value of ygjcIncomeCarryAmt
     */
    public BigDecimal getYgjcIncomeCarryAmt() {
        return ygjcIncomeCarryAmt;
    }

    /**
     * Sets the ygjcIncomeCarryAmt.
     * <p>You can use getYgjcIncomeCarryAmt() to get the value of ygjcIncomeCarryAmt</p>
     *
     * @param ygjcIncomeCarryAmt ygjcIncomeCarryAmt
     */
    public void setYgjcIncomeCarryAmt(BigDecimal ygjcIncomeCarryAmt) {
        this.ygjcIncomeCarryAmt = ygjcIncomeCarryAmt;
    }

    /**
     * Gets the value of ygjcIncomeAccrAmt.
     *
     * @return the value of ygjcIncomeAccrAmt
     */
    public BigDecimal getYgjcIncomeAccrAmt() {
        return ygjcIncomeAccrAmt;
    }

    /**
     * Sets the ygjcIncomeAccrAmt.
     * <p>You can use getYgjcIncomeAccrAmt() to get the value of ygjcIncomeAccrAmt</p>
     *
     * @param ygjcIncomeAccrAmt ygjcIncomeAccrAmt
     */
    public void setYgjcIncomeAccrAmt(BigDecimal ygjcIncomeAccrAmt) {
        this.ygjcIncomeAccrAmt = ygjcIncomeAccrAmt;
    }

    /**
     * Gets the value of fsjcIncomeHappenAmt.
     *
     * @return the value of fsjcIncomeHappenAmt
     */
    public BigDecimal getFsjcIncomeHappenAmt() {
        return fsjcIncomeHappenAmt;
    }

    /**
     * Sets the fsjcIncomeHappenAmt.
     * <p>You can use getFsjcIncomeHappenAmt() to get the value of fsjcIncomeHappenAmt</p>
     *
     * @param fsjcIncomeHappenAmt fsjcIncomeHappenAmt
     */
    public void setFsjcIncomeHappenAmt(BigDecimal fsjcIncomeHappenAmt) {
        this.fsjcIncomeHappenAmt = fsjcIncomeHappenAmt;
    }

    /**
     * Gets the value of fsjcIncomeCarryAmt.
     *
     * @return the value of fsjcIncomeCarryAmt
     */
    public BigDecimal getFsjcIncomeCarryAmt() {
        return fsjcIncomeCarryAmt;
    }

    /**
     * Sets the fsjcIncomeCarryAmt.
     * <p>You can use getFsjcIncomeCarryAmt() to get the value of fsjcIncomeCarryAmt</p>
     *
     * @param fsjcIncomeCarryAmt fsjcIncomeCarryAmt
     */
    public void setFsjcIncomeCarryAmt(BigDecimal fsjcIncomeCarryAmt) {
        this.fsjcIncomeCarryAmt = fsjcIncomeCarryAmt;
    }

    /**
     * Gets the value of fsjcIncomeAccrAmt.
     *
     * @return the value of fsjcIncomeAccrAmt
     */
    public BigDecimal getFsjcIncomeAccrAmt() {
        return fsjcIncomeAccrAmt;
    }

    /**
     * Sets the fsjcIncomeAccrAmt.
     * <p>You can use getFsjcIncomeAccrAmt() to get the value of fsjcIncomeAccrAmt</p>
     *
     * @param fsjcIncomeAccrAmt fsjcIncomeAccrAmt
     */
    public void setFsjcIncomeAccrAmt(BigDecimal fsjcIncomeAccrAmt) {
        this.fsjcIncomeAccrAmt = fsjcIncomeAccrAmt;
    }

    /**
     * Gets the value of fslxIncomeAmtAll.
     *
     * @return the value of fslxIncomeAmtAll
     */
    public BigDecimal getFslxIncomeAmtAll() {
        return fslxIncomeAmtAll;
    }

    /**
     * Sets the fslxIncomeAmtAll.
     * <p>You can use getFslxIncomeAmtAll() to get the value of fslxIncomeAmtAll</p>
     *
     * @param fslxIncomeAmtAll fslxIncomeAmtAll
     */
    public void setFslxIncomeAmtAll(BigDecimal fslxIncomeAmtAll) {
        this.fslxIncomeAmtAll = fslxIncomeAmtAll;
    }

    /**
     * Gets the value of fshlIncomeAmtAll.
     *
     * @return the value of fshlIncomeAmtAll
     */
    public BigDecimal getFshlIncomeAmtAll() {
        return fshlIncomeAmtAll;
    }

    /**
     * Sets the fshlIncomeAmtAll.
     * <p>You can use getFshlIncomeAmtAll() to get the value of fshlIncomeAmtAll</p>
     *
     * @param fshlIncomeAmtAll fshlIncomeAmtAll
     */
    public void setFshlIncomeAmtAll(BigDecimal fshlIncomeAmtAll) {
        this.fshlIncomeAmtAll = fshlIncomeAmtAll;
    }

    /**
     * Gets the value of fsjcIncomeAmtAll.
     *
     * @return the value of fsjcIncomeAmtAll
     */
    public BigDecimal getFsjcIncomeAmtAll() {
        return fsjcIncomeAmtAll;
    }

    /**
     * Sets the fsjcIncomeAmtAll.
     * <p>You can use getFsjcIncomeAmtAll() to get the value of fsjcIncomeAmtAll</p>
     *
     * @param fsjcIncomeAmtAll fsjcIncomeAmtAll
     */
    public void setFsjcIncomeAmtAll(BigDecimal fsjcIncomeAmtAll) {
        this.fsjcIncomeAmtAll = fsjcIncomeAmtAll;
    }

    /**
     * Gets the value of yglxIncomeDurAmt.
     *
     * @return the value of yglxIncomeDurAmt
     */
    public BigDecimal getYglxIncomeDurAmt() {
        return yglxIncomeDurAmt;
    }

    /**
     * Sets the yglxIncomeDurAmt.
     * <p>You can use getYglxIncomeDurAmt() to get the value of yglxIncomeDurAmt</p>
     *
     * @param yglxIncomeDurAmt yglxIncomeDurAmt
     */
    public void setYglxIncomeDurAmt(BigDecimal yglxIncomeDurAmt) {
        this.yglxIncomeDurAmt = yglxIncomeDurAmt;
    }

    /**
     * Gets the value of yghlIncomeDurAmt.
     *
     * @return the value of yghlIncomeDurAmt
     */
    public BigDecimal getYghlIncomeDurAmt() {
        return yghlIncomeDurAmt;
    }

    /**
     * Sets the yghlIncomeDurAmt.
     * <p>You can use getYghlIncomeDurAmt() to get the value of yghlIncomeDurAmt</p>
     *
     * @param yghlIncomeDurAmt yghlIncomeDurAmt
     */
    public void setYghlIncomeDurAmt(BigDecimal yghlIncomeDurAmt) {
        this.yghlIncomeDurAmt = yghlIncomeDurAmt;
    }

    /**
     * Gets the value of ygjcIncomeDurAmt.
     *
     * @return the value of ygjcIncomeDurAmt
     */
    public BigDecimal getYgjcIncomeDurAmt() {
        return ygjcIncomeDurAmt;
    }

    /**
     * Sets the ygjcIncomeDurAmt.
     * <p>You can use getYgjcIncomeDurAmt() to get the value of ygjcIncomeDurAmt</p>
     *
     * @param ygjcIncomeDurAmt ygjcIncomeDurAmt
     */
    public void setYgjcIncomeDurAmt(BigDecimal ygjcIncomeDurAmt) {
        this.ygjcIncomeDurAmt = ygjcIncomeDurAmt;
    }

    /**
     * Gets the value of fslxIncomeDurAmt.
     *
     * @return the value of fslxIncomeDurAmt
     */
    public BigDecimal getFslxIncomeDurAmt() {
        return fslxIncomeDurAmt;
    }

    /**
     * Sets the fslxIncomeDurAmt.
     * <p>You can use getFslxIncomeDurAmt() to get the value of fslxIncomeDurAmt</p>
     *
     * @param fslxIncomeDurAmt fslxIncomeDurAmt
     */
    public void setFslxIncomeDurAmt(BigDecimal fslxIncomeDurAmt) {
        this.fslxIncomeDurAmt = fslxIncomeDurAmt;
    }

    /**
     * Gets the value of fshlIncomeDurAmt.
     *
     * @return the value of fshlIncomeDurAmt
     */
    public BigDecimal getFshlIncomeDurAmt() {
        return fshlIncomeDurAmt;
    }

    /**
     * Sets the fshlIncomeDurAmt.
     * <p>You can use getFshlIncomeDurAmt() to get the value of fshlIncomeDurAmt</p>
     *
     * @param fshlIncomeDurAmt fshlIncomeDurAmt
     */
    public void setFshlIncomeDurAmt(BigDecimal fshlIncomeDurAmt) {
        this.fshlIncomeDurAmt = fshlIncomeDurAmt;
    }

    /**
     * Gets the value of fsjcIncomeDurAmt.
     *
     * @return the value of fsjcIncomeDurAmt
     */
    public BigDecimal getFsjcIncomeDurAmt() {
        return fsjcIncomeDurAmt;
    }

    /**
     * Sets the fsjcIncomeDurAmt.
     * <p>You can use getFsjcIncomeDurAmt() to get the value of fsjcIncomeDurAmt</p>
     *
     * @param fsjcIncomeDurAmt fsjcIncomeDurAmt
     */
    public void setFsjcIncomeDurAmt(BigDecimal fsjcIncomeDurAmt) {
        this.fsjcIncomeDurAmt = fsjcIncomeDurAmt;
    }

    /**
     * Gets the value of fslxHappenAmt.
     *
     * @return the value of fslxHappenAmt
     */
    public BigDecimal getFslxHappenAmt() {
        return fslxHappenAmt;
    }

    /**
     * Sets the fslxHappenAmt.
     * <p>You can use getFslxHappenAmt() to get the value of fslxHappenAmt</p>
     *
     * @param fslxHappenAmt fslxHappenAmt
     */
    public void setFslxHappenAmt(BigDecimal fslxHappenAmt) {
        this.fslxHappenAmt = fslxHappenAmt;
    }

    /**
     * Gets the value of fshlHappenAmt.
     *
     * @return the value of fshlHappenAmt
     */
    public BigDecimal getFshlHappenAmt() {
        return fshlHappenAmt;
    }

    /**
     * Sets the fshlHappenAmt.
     * <p>You can use getFshlHappenAmt() to get the value of fshlHappenAmt</p>
     *
     * @param fshlHappenAmt fshlHappenAmt
     */
    public void setFshlHappenAmt(BigDecimal fshlHappenAmt) {
        this.fshlHappenAmt = fshlHappenAmt;
    }

    /**
     * Gets the value of fsjcHappenAmt.
     *
     * @return the value of fsjcHappenAmt
     */
    public BigDecimal getFsjcHappenAmt() {
        return fsjcHappenAmt;
    }

    /**
     * Sets the fsjcHappenAmt.
     * <p>You can use getFsjcHappenAmt() to get the value of fsjcHappenAmt</p>
     *
     * @param fsjcHappenAmt fsjcHappenAmt
     */
    public void setFsjcHappenAmt(BigDecimal fsjcHappenAmt) {
        this.fsjcHappenAmt = fsjcHappenAmt;
    }

    /**
     * Gets the value of maYglxIncomeAmtAll.
     *
     * @return the value of maYglxIncomeAmtAll
     */
    public BigDecimal getMaYglxIncomeAmtAll() {
        return maYglxIncomeAmtAll;
    }

    /**
     * Sets the maYglxIncomeAmtAll.
     * <p>You can use getMaYglxIncomeAmtAll() to get the value of maYglxIncomeAmtAll</p>
     *
     * @param maYglxIncomeAmtAll maYglxIncomeAmtAll
     */
    public void setMaYglxIncomeAmtAll(BigDecimal maYglxIncomeAmtAll) {
        this.maYglxIncomeAmtAll = maYglxIncomeAmtAll;
    }

    /**
     * Gets the value of maYghlIncomeAmtAll.
     *
     * @return the value of maYghlIncomeAmtAll
     */
    public BigDecimal getMaYghlIncomeAmtAll() {
        return maYghlIncomeAmtAll;
    }

    /**
     * Sets the maYghlIncomeAmtAll.
     * <p>You can use getMaYghlIncomeAmtAll() to get the value of maYghlIncomeAmtAll</p>
     *
     * @param maYghlIncomeAmtAll maYghlIncomeAmtAll
     */
    public void setMaYghlIncomeAmtAll(BigDecimal maYghlIncomeAmtAll) {
        this.maYghlIncomeAmtAll = maYghlIncomeAmtAll;
    }

    /**
     * Gets the value of maYgjcIncomeAmtAll.
     *
     * @return the value of maYgjcIncomeAmtAll
     */
    public BigDecimal getMaYgjcIncomeAmtAll() {
        return maYgjcIncomeAmtAll;
    }

    /**
     * Sets the maYgjcIncomeAmtAll.
     * <p>You can use getMaYgjcIncomeAmtAll() to get the value of maYgjcIncomeAmtAll</p>
     *
     * @param maYgjcIncomeAmtAll maYgjcIncomeAmtAll
     */
    public void setMaYgjcIncomeAmtAll(BigDecimal maYgjcIncomeAmtAll) {
        this.maYgjcIncomeAmtAll = maYgjcIncomeAmtAll;
    }

    /**
     * Gets the value of maFslxIncomeAmtAll.
     *
     * @return the value of maFslxIncomeAmtAll
     */
    public BigDecimal getMaFslxIncomeAmtAll() {
        return maFslxIncomeAmtAll;
    }

    /**
     * Sets the maFslxIncomeAmtAll.
     * <p>You can use getMaFslxIncomeAmtAll() to get the value of maFslxIncomeAmtAll</p>
     *
     * @param maFslxIncomeAmtAll maFslxIncomeAmtAll
     */
    public void setMaFslxIncomeAmtAll(BigDecimal maFslxIncomeAmtAll) {
        this.maFslxIncomeAmtAll = maFslxIncomeAmtAll;
    }

    /**
     * Gets the value of maFshlIncomeAmtAll.
     *
     * @return the value of maFshlIncomeAmtAll
     */
    public BigDecimal getMaFshlIncomeAmtAll() {
        return maFshlIncomeAmtAll;
    }

    /**
     * Sets the maFshlIncomeAmtAll.
     * <p>You can use getMaFshlIncomeAmtAll() to get the value of maFshlIncomeAmtAll</p>
     *
     * @param maFshlIncomeAmtAll maFshlIncomeAmtAll
     */
    public void setMaFshlIncomeAmtAll(BigDecimal maFshlIncomeAmtAll) {
        this.maFshlIncomeAmtAll = maFshlIncomeAmtAll;
    }

    /**
     * Gets the value of maFsjcIncomeAmtAll.
     *
     * @return the value of maFsjcIncomeAmtAll
     */
    public BigDecimal getMaFsjcIncomeAmtAll() {
        return maFsjcIncomeAmtAll;
    }

    /**
     * Sets the maFsjcIncomeAmtAll.
     * <p>You can use getMaFsjcIncomeAmtAll() to get the value of maFsjcIncomeAmtAll</p>
     *
     * @param maFsjcIncomeAmtAll maFsjcIncomeAmtAll
     */
    public void setMaFsjcIncomeAmtAll(BigDecimal maFsjcIncomeAmtAll) {
        this.maFsjcIncomeAmtAll = maFsjcIncomeAmtAll;
    }



    /**
     * add by xr start
     * 预估利息_当日增值税额本币_余额计算_含结转
     */
    private BigDecimal yglxTdyBalCalcTaxAmtHjz = BigDecimal.ZERO;

    public BigDecimal getYglxTdyBalCalcTaxAmtHjz() {
        return yglxTdyBalCalcTaxAmtHjz;
    }

    public void setYglxTdyBalCalcTaxAmtHjz(BigDecimal yglxTdyBalCalcTaxAmtHjz) {
        this.yglxTdyBalCalcTaxAmtHjz = yglxTdyBalCalcTaxAmtHjz;
    }
    /**
     * 预估红利_当日增值税额本币_余额计算_含结转
     */
    private BigDecimal yghlTdyBalCalcTaxAmtHjz = BigDecimal.ZERO;

    public BigDecimal getYghlTdyBalCalcTaxAmtHjz() {
        return yghlTdyBalCalcTaxAmtHjz;
    }

    public void setYghlTdyBalCalcTaxAmtHjz(BigDecimal yghlTdyBalCalcTaxAmtHjz) {
        this.yghlTdyBalCalcTaxAmtHjz = yghlTdyBalCalcTaxAmtHjz;
    }

    /**
     * add by xr end
     * 预估利息_当日增值税额本币_余额计算_含结转
     */
}

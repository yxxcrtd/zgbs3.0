/*
 * 文件名：RiskTrialDayTimeResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年9月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.risktrialrds.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.common.base.entity.RskIndividCondition;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.calendar.entity.ICalendar;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * 个性化条件设置DTO
 *
 * @author 裴家俊
 * @version 1.0 2019年9月2日
 * @see RiskIndividConditionDto
 * @since 1.0
 */
public class RiskIndividConditionDto extends RskIndividCondition {

    /**
     * 资产净值
     */
    private BigDecimal assetCprice;
    /**
     * 单位净值
     */
    private BigDecimal unitCprice;

    public BigDecimal getAssetCprice() {
        return assetCprice;
    }

    public void setAssetCprice(BigDecimal assetCprice) {
        this.assetCprice = assetCprice;
    }

    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    public <T extends FinOpenPlan> Date getEnableStrTime(T purPlan,T repPlan, ICalendar iCalendar){
        Date date=filterDate(purPlan,repPlan,()->getDateStr());
        if(null==date){
            return null;
        }
        if (iCalendar != null&&StringUtil.isNotEmpty(getValueStr())) {
            return DateUtil.parse(iCalendar.calcDate(DateUtil.format(date), Integer.valueOf(getValueStr().toString()),
                    BussinessDictCodeListRds.操作符_减.getValue(), getDateUnitStr()));
        }else{
            return DateUtil.addDay(date,StringUtil.isNotEmpty(getValueStr())?Integer.valueOf(getValueStr().toString())*-1:0);
        }
    }

    public <T extends FinOpenPlan> Date getEnableEndTime(T purPlan,T repPlan, ICalendar iCalendar){
        Date date=filterDate(purPlan,repPlan,()->getDateEnd());
        if(null==date){
            return null;
        }
        if (iCalendar != null&&StringUtil.isNotEmpty(getValueEnd())) {
            return DateUtil.parse(iCalendar.calcDate(DateUtil.format(date), Integer.valueOf(getValueEnd().toString()),
                    BussinessDictCodeListRds.操作符_加.getValue(), getDateUnitEnd()));
        }else{
            return DateUtil.addDay(date,StringUtil.isNotEmpty(getValueEnd())?Integer.valueOf(getValueEnd().toString()):0);
        }
    }

    private <T extends FinOpenPlan> Date filterDate(T purPlan,T repPlan, Supplier<String> supplier){
        if(StringUtil.equals(BussinessDictCodeListRds.日期选项_申购开始日.getValue(),supplier.get())){
            return purPlan.getOpenDateStr();
        }
        if(StringUtil.equals(BussinessDictCodeListRds.日期选项_赎回开始日.getValue(),supplier.get())){
            return repPlan.getOpenDateStr();
        }
        if(StringUtil.equals(BussinessDictCodeListRds.日期选项_申购结束日.getValue(),supplier.get())){
            return purPlan.getOpenDateEnd();
        }
        if(StringUtil.equals(BussinessDictCodeListRds.日期选项_赎回结束日.getValue(),supplier.get())){
            return repPlan.getOpenDateEnd();
        }
        if(StringUtil.equals(BussinessDictCodeListRds.日期选项_申购确认日.getValue(),supplier.get())){
            return purPlan.getOpenVdate();
        }
        if(StringUtil.equals(BussinessDictCodeListRds.日期选项_赎回确认日.getValue(),supplier.get())){
            return repPlan.getOpenVdate();
        }
        return null;
    }
}

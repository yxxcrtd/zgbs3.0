package com.joyintech.tams.exchange.common.constant;

import com.joyintech.tams.exchange.common.enums.IEnum;

public enum CacheClassMappingEnum implements IEnum<String> {

    ENTRUST("普通委托", "com.joyintech.tams.exchange.common.db.domain.Entrust", "com.joyintech.tams.exchange.datacommon.db.service.impl.EntrustServiceImpl"),
    INTEGRATED_ENTRUST("综合委托", "com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust", "com.joyintech.tams.exchange.datacommon.db.service.impl.IntegratedEntrustServiceImpl"),
    ORDER("订单", "com.joyintech.tams.exchange.common.db.domain.Order", "com.joyintech.tams.exchange.datacommon.db.service.impl.OrderServiceImpl"),
    TRADE("成交", "com.joyintech.tams.exchange.common.db.domain.Trade", "com.joyintech.tams.exchange.datacommon.db.service.impl.TradeServiceImpl"),
    ACCOUNT_RISK_PARAMETER("账户风控参数", "com.joyintech.tams.exchange.common.db.domain.AccountRiskParameter", "com.joyintech.tams.exchange.datacommon.db.service.impl.AccountRiskParameterServiceImpl"),
    INSTRUMENT("合约", "com.joyintech.tams.exchange.common.db.domain.Instrument", "com.joyintech.tams.exchange.datacommon.db.service.impl.InstrumentServiceImpl"),
    INSTRUCTION("指令", "com.joyintech.tams.exchange.common.db.domain.Instruction", "com.joyintech.tams.exchange.datacommon.db.service.impl.InstructionServiceImpl"),
    INSTRUCTION_PLEDGE("指令抵押", "com.joyintech.tams.exchange.common.db.domain.InstructionPledge", "com.joyintech.tams.exchange.datacommon.db.service.impl.InstructionPledgeServiceImpl"),
    INSTRUCTION_DETAIL("指令明细", "com.joyintech.tams.exchange.common.db.domain.InstructionDetail", "com.joyintech.tams.exchange.datacommon.db.service.impl.InstructionDetailServiceImpl"),
//    FUTURES_INSTRUMENT("期货合约", "com.joyintech.tams.exchange.common.db.domain.FuturesInstrument", "com.joyintech.tams.exchange.datacommon.db.service.impl.FuturesInstrumentServiceImpl"),
    ;

    public final String name;
    public final String beanPath;
    public final String classPath;

    CacheClassMappingEnum(String name, String beanPath, String classPath) {
        this.name = name;
        this.beanPath = beanPath;
        this.classPath = classPath;
    }

    public String getName() {
        return name;
    }

    public String getBeanPath() {
        return beanPath;
    }

    public String getClassPath() {
        return classPath;
    }

    public static String getBeanPathByName(String name) {
        for (CacheClassMappingEnum c : CacheClassMappingEnum.values()) {
            if (c.getName().equals(name)) {
                return c.beanPath;
            }
        }
        return null;
    }

    public static String getClassPathByName(String name) {
        for (CacheClassMappingEnum c : CacheClassMappingEnum.values()) {
            if (c.getName().equals(name)) {
                return c.classPath;
            }
        }
        return null;
    }

    public static String getNameByClassPath(String classPath) {
        for (CacheClassMappingEnum c : CacheClassMappingEnum.values()) {
            if (c.getClassPath().equals(classPath)) {
                return c.name;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return name;
    }

    @Override
    public String getDescription() {
        return beanPath;
    }
}

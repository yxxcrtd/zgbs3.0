/*
 * 文件名：BussinessConstant.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.exchangetransmatch.position.constant;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 *
 * @author yflv
 * @version 1.0 2022年2月16日
 * @see PositionConstant
 * @since 1.0
 */
public final class PositionConstant {

    private PositionConstant() {
    }

    /**
     * 指令头寸类型
     */
    public static final String POSITION_TYPE_ETR = "ETR";
    /**
     * 交易头寸类型
     */
    public static final String POSITION_TYPE_TRD = "TRD";

    /**
     * 银行间现券
     */
    public static final String INTERF_TYPE_BOND001 = "Bond001";
    
    /**
     * 场内现券
     */
    public static final String INTERF_TYPE_BOND002 = "Bond002";

    /**
     * 场内可转债
     */
    public static final String INTERF_TYPE_BOND003 = "Bond003";

    /**
     * 基金--LOF基金
     */
    public static final String INTERF_TYPE_FUND001 = "Fund001";

    /**
     * 基金--封闭式基金
     */
    public static final String INTERF_TYPE_FUND002 = "Fund002";

    /**
     * 质押式双边回购--竞价回购
     */
    public static final String INTERF_TYPE_REPO001 = "REPO001";

    /**
     * 质押式协议回购--协议回购
     */
    public static final String INTERF_TYPE_REPO002 = "REPO002";

    /**
     * 质押式通用回购
     */
    public static final String INTERF_TYPE_REPO003 = "REPO003";

    /**
     * 股票（A股）
     */
    public static final String INTERF_TYPE_STOCK001 = "Stock001";

    /**
     * 信用保护凭证交易
     */
    public static final String INTERF_TYPE_CREDIT001 = "CPC001";

}

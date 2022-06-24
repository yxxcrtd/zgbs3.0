/*
 * 文件名：InterfUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年1月5日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.riskrds.constant;

/**
 * 常量类
 *
 * @author 裴家俊
 * @version 1.0 2020年1月5日
 * @see InterfUtil
 * @since 1.0
 */
public class InterfUtil {

    /**
     * Key:一体化风险接口地址
     */
    public static final String RISK_INF_URL = "/interf/execute";
    /**
     * Key:系统代码
     */
    public static final String SYS_CODE = "riskinf_system_code";

    /**
     * KEY:报文类型(0001非金融类)
     */
    public static final String MES_TYPE = "0001";

    /**
     * KEY:风险接口调用地址
     */
    public static final String INTERF_URL = "risk_redis_interf_url";

    /**
     * 报文类型
     */
    public static final String stdmsgtype = "stdmsgtype";
    /**
     * 业务归属系统代码
     */
    public static final String std400bsys = "std400bsys";
    /**
     * 交易码
     */
    public static final String stdprocode = "stdprocode";
    /**
     * 发起方系统代码
     */
    public static final String std400aqid = "std400aqid";
    /**
     * 发起方流水号
     */
    public static final String stdtermtrc = "stdtermtrc";
    /**
     * 发起方系统日期
     */
    public static final String std400rbdt = "std400rbdt";
    /**
     * 发起方系统时间戳
     */
    public static final String std400rsts = "std400rsts";

    /**
     * 发起方系统时间
     */
    public static final String std400rtim = "std400rtim";
    /**
     * 返回信息
     */
    public static final String std400msgt = "std400msgt";

    /**
     * 返回状态
     */
    public static final String stdrtninfo = "stdrtninfo";

    /**
     * 系统代码
     */
    public static final String systemcode = "TAMS-INVEST";

    /**
     * 金融产品代码
     */
    public static final String finprodId = "finprodId";

    /**
     * 金融产品分类_投管
     */
    public static final String finprodType2 = "finprodType2";

    /**
     * 交易方向
     */
    public static final String ps = "ps";

    /**
     * 币种
     */
    public static final String CCY = "ccy";

    /**
     * 异常画面前缀
     */
    public static final String MSG = "MSG:";

    /**
     * 异常状态
     */
    public static final String E = "E";

    /**
     * 多组合返回画面标识
     */
    public static final String BACK_PAGE_FLAG = "backPageFlag";

    /**
     * 多组合调用常规异常提示
     */
    public static final String INF_REMAIN_FLAG = "infRemainFlag";

    /**
     * 多组合返回结果列表
     */
    public static final String RISK_RDS_LIST = "riskRdsList";

    /**
     * 多组合返回消息
     */
    public static final String RISK_BACK_MES = "riskBackMes";

    /**
     * 超违规线提示信息
     */
    public static final String FORBID_MES = "指标试算未通过";
}

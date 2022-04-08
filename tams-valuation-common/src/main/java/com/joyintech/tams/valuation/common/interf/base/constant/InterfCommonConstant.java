/*
 * 文件名：InterfCommonConstant.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2018年6月26日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.constant;

import java.math.BigDecimal;


/**
 * 接口共通常量定义 
 * 
 * @author 王波
 * @version 1.0 2018年6月26日--[
 * @see InterfCommonConstant
 * @since 1.0
 */
public class InterfCommonConstant {
    // URL 相关常量定义
    /**
     * 接口_接口时间
     */
    public static final String 接口_接口时间 = "dateTime";
    
    /**
     * 接口_发送接收 R-接收 S-发送
     */
    public static final String 接口_发送接收 = "RS";
    
    /**
     * 接口_对接系统ID
     */
    public static final String 接口_对接系统ID = "SYSTEMID";
    
    /**
     * 接口_批次号列表 -- 以, 分割
     */
    public static final String 接口_批次号列表 = "ORDERNOS";
    
    
//    /**
//     * 接口_模板编号列表 -- 以, 分割
//     */
//    public static final String 接口_模板编号号列表 = "TMP_ENAME";
    
    /**
     * 接口_运行接口名称 -- 以, 分割
     */
    public static final String 接口_运行接口名称 = "INTERF_NAME";
    
    /**
     * 接口_运行接口账套ID -- 以, 分割
     */
    public static final String 接口_运行接口账套ID = "INTERF_BOOKSETID";
    
    /**
     * 接口_运行接口名称 -- 以, 分割
     */
    public static final String 接口_任务执行ID = "INTERF_EXECTASK_SEQNO";
    
    /**
     * 接口_运行接口列表
     */
    public static final String 接口_运行接口列表 = "INTERF_LIST";
    
    /**
     * 接口_开始日
     */
    public static final String 接口_开始日 = "STARTDATE";
    
    /**
     * 接口_结束日
     */
    public static final String 接口_结束日 = "ENDDATE";
    
    /**
     * 接口_默认用户
     */
    public static final String 接口_默认用户 = "interf";
    
    /**
     * 接口_模式
     */
    public static final String 接口模式 = "VAL_MODEL";
    
    /**
     * 接口_批次列表
     */
    public static final String INTERF_ORDER_LIST = "orderNoList";
    
    //默认全局变量常量定义
    /**
     * 默认分支序号
     */
    public static final long 分支序号_默认 = 0;
    /**
     * 是否结清_默认
     */
    public static final String 是否结清_默认 = "Y";
    
    // 账套信息查询相关变量
    /**
     * 账套信息查询_账套信息
     */
    public static final String 账套信息查询_账套信息 = "BOOKSET";
    
    /**
     * 账套信息查询_账套日期
     */
    public static final String 账套信息查询_账套日期 = "BOOKSET_DATE";
    
    /**
     * 账套信息查询_账套配置
     */
    public static final String 账套信息查询_账套配置 = "BOOKSET_EXT";
    
    /**
     * 账套信息查询_账套科目信息
     */
    public static final String 账套信息查询_账套科目信息 = "SUBJECT";
    
    /**
     * 账套信息查询_会计场景
     */
    public static final String 账套信息查询_会计场景 = "TABLELIST";
    
    /**
     * 账套信息查询_会计元素
     */
    public static final String 账套信息查询_会计元素 = "TABLEDICT";
    
    /**
     * 账套信息查询_场景科目关系
     */
    public static final String 账套信息查询_场景科目关系 = "SCENESUBJECT";
    
    /**
     * 账套信息查询_场景分支配置
     */
    public static final String 账套信息查询_场景分支配置 = "SCENECONFIG";
    
    /**
     * 账套信息查询_账套估值方案
     */
    public static final String 账套信息查询_账套估值方案 = "BOOKSET_VAL_PLAN";

    /**
     *  普通账套代码
     */
    public static final String BOOKSETID = "00000000000000000002";
    public static final String BOOKSETID_NEW = "SUBJECT_INTERNAL_001";

    /**add by wujie on 20181017 start**/
    /**
     *  普通分层账套代码
     */
    public static final String BOOKSETID_LAYERING_NOR = "00000000000000000003";
    public static final String BOOKSETID_LAYERING_NOR_NEW = "SUBJECT_INTERNAL_001";

    /**
     *  权益分层账套代码
     */
    public static final String BOOKSETID_LAYERING_RI = "00000000000000000004";
    public static final String BOOKSETID_LAYERING_RI_NEW = "SUBJECT_INTERNAL_001";
    /**add by wuji eon 20181017 end**/
    
    
    /**
     *  银行账_账套代码
     *  add by wujie on 20190416
     */
    public static final String BOOKSETID_BANK = "BANK0001";
    
    
    /**
     * 接口接入
     */
    public static final String 接入方式="接口接入";
    
    /**
     * 接口接入:清算款账户号
     */
    public static final String 清算款账户号 = "QSKZH0001";
    
    
    /**
     * 接口:画面调用Controller RequestMapping
     */
    public static final String 接口消息入口 = "/interfMsg";
    
    /**
     * 接口:画面调用Controller RequestMapping
     */
    public static final String 组装XMl文件 = "/buildXml";
    
    public static final String 接口_债券_中债登估值 = "CBondAnalysisCNBD";
    //CMoneyMarketDailyFIncome(货币基金每天净值收益)
    public static final String 接口_基金_货币基金每天净值收益 = "CMoneyMarketDailyFIncome";
    //ChinaMutualFundNAV(中国共同基金净值)
    public static final String 接口_基金_中国共同基金净值 = "ChinaMutualFundNAV";
    //利率行情
    public static final String 接口_源表_利率行情 = "ShiborPrices";
    
    
    /**
     * 默认单位净价
     */
    public static final BigDecimal 单位净价_默认 = BigDecimal.ONE;
    
    /**
     * 默认估值来源默认值
     */
    public static final String 估值来源_资讯_默认值 = "VALUE_SOURCE_DEFAULT";
    
    /**
     * 默认估值来源默认值取值开关
     */
    public static final String 估值来源_资讯_默认值开关 = "VALUE_SOURCE_KG";
    /**
     * 新增/删除条数
     */
    public static final int maxSqlNum=1000;
    
    public static final String 接口模式_全账套 = "INTERF_MODEL_ALL";

    public static final String 接口模式_指定账套 = "INTERF_MODEL_SINGLE";

    /**
     * 接口_界面账套选择类型
     * ADD BY WUJIE ON 20190521
     */
    public static final String 接口_界面账套选择类型 = "BATCH_BOOKSET_TYPE";

    /**
     * 接口_界面账套选择账套或账套组 -- 以, 分割
     * ADD BY WUJIE ON 20190521
     */
    public static final String 接口_界面选择账套或账套组 = "INTERF_BOOKSETID_BEF";

    public static final String 券商资金账户类型 = "A07";//交易所资金账户
    
    /**
     * 深证通接口消息入口 从xybranch20190115版本合并过来 add by yanya on 20191102
     */
    public static final String 深证通接口消息入口 = "/interfSztMsg";

    /**ADD BY WUJIE ON 20210506 START**/
    public static final String 读取文件状态 = "isDealTrade";
    /**ADD BY WUJIE ON 20210506 END**/

}

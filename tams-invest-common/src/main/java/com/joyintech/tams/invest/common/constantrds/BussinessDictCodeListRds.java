/*
 * 文件名：BussinessDictCodeListRds.java'
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年08月04日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.common.constantrds;

import com.joyintech.framework.base.BaseDictCode;
import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 风险数据字典明细
 * @author 裴家俊
 * @version 1.0 2019年08月04日
 * @see BussinessDictCodeListRds
 * @since 1.0
 */
public enum BussinessDictCodeListRds implements BaseDictCodeList {
    /**
     * 持仓类型_实际
     */
    持仓类型_实际(BussinessDictCodeRds.持仓类型,"03","实际"),
    /**
     * 复合模板取值类型_成本
     */
    复合模板取值类型_成本(BussinessDictCodeRds.复合模板取值类型,"CST","成本"),
    /**
     * 复合模板取值类型_市值
     */
    复合模板取值类型_市值(BussinessDictCodeRds.复合模板取值类型,"MKT","市值"),
    /**
     * 复合模板取值类型_数量
     */
    复合模板取值类型_数量(BussinessDictCodeRds.复合模板取值类型,"QTY","数量"),
    /**
     * 阶段类型_特殊资产阶段
     */
    阶段类型_特殊资产阶段(BussinessDictCodeRds.阶段类型,"02","特殊资产阶段"),
    /**
     * 阶段类型_特殊阶段
     */
    阶段类型_特殊阶段(BussinessDictCodeRds.阶段类型,"01","特殊阶段"),
    /**
     * 控制类型_大于
     */
    控制类型_大于(BussinessDictCodeRds.控制类型,"01",">"),
    /**
     * 控制类型_大于等于
     */
    控制类型_大于等于(BussinessDictCodeRds.控制类型,"02",">="),
    /**
     * 控制类型_小于
     */
    控制类型_小于(BussinessDictCodeRds.控制类型,"03","<"),
    /**
     * 控制类型_小于等于
     */
    控制类型_小于等于(BussinessDictCodeRds.控制类型,"04","<="),
    /**
     * 控制类型_范围控制
     */
    控制类型_范围控制(BussinessDictCodeRds.控制类型,"05","范围控制"),
    /**
     * 净值型资产申购
     */
    共同业务类型_净值型资产申购(BussinessDictCodeRds.共同业务类型,"TRD31","净值型资产申购"),
    /**
     * 净值型资产赎回
     */
    共同业务类型_净值型资产赎回(BussinessDictCodeRds.共同业务类型,"TRD32","净值型资产赎回"),
    /**
     * 货币基金申购
     */
    共同业务类型_货币基金申购(BussinessDictCodeRds.共同业务类型,"TRD29","货币基金申购"),
    /**
     * 货币基金赎回
     */
    共同业务类型_货币基金赎回(BussinessDictCodeRds.共同业务类型,"TRD30","货币基金赎回"),

    /**
     * 逆回购录入
     */
    共同业务类型_逆回购录入(BussinessDictCodeRds.共同业务类型,"TRD27","逆回购录入"),
    /**
     * 正回购录入
     */
    共同业务类型_正回购录入(BussinessDictCodeRds.共同业务类型,"TRD28","正回购录入"),
    
    /**
     * 风险事件管理
     */
    共同业务类型_风险事件管理(BussinessDictCodeRds.共同业务类型,"RSK29","风控事项管理"),
    /**
     * 风险审批
     */
    共同业务类型_风险审批(BussinessDictCodeRds.共同业务类型,"RSK30","风险审批"),
    /**
     * 风险事件关联指标参数
     */
    共同业务类型_风险事件关联指标参数(BussinessDictCodeRds.共同业务类型,"RSK31","风控事项关联指标参数"),
    /**
     * REDIS模块_指标参数值
     */
    REDIS模块_指标参数值(BussinessDictCodeRds.REDIS模块,"RSK{VALUE}","指标参数值"),
    /**
     * 投资类组合
     */
    组合类型_投资类组合(BussinessDictCodeRds.组合类型,"01","投资类组合"),
    /**
     * 委外组合
     */
    组合类型_委外组合(BussinessDictCodeRds.组合类型,"02","委外组合"),
    /**
     * 基准类型
     */
    基准类型_SAA(BussinessDictCodeRds.基准类型,"01","SAA"),
    /**
     * 基准类型
     */
    基准类型_复合(BussinessDictCodeRds.基准类型,"05","复合"),
    /**z
     * 基准类型
     */
    基准类型_利率(BussinessDictCodeRds.基准类型,"04","利率"),
    /**
     * 基准类型
     */
    基准类型_指数(BussinessDictCodeRds.基准类型,"02","指数"),
    /**
     * 基准类型
     */
    基准类型_曲线(BussinessDictCodeRds.基准类型,"03","曲线"),
     /**
      * 基准类型
      */
     基准类型_业绩基准(BussinessDictCodeRds.基准类型,"06","业绩基准"),
     /**
      * 其他资产管理计划
      */
     业务编号一级分类_其他资产管理计划(BussinessDictCodeRds.业务编号一级分类,"25","其他资产管理计划"),
     /**
      * 基金资产管理计划
      */
     业务编号一级分类_基金资产管理计划(BussinessDictCodeRds.业务编号一级分类,"21","基金资产管理计划"),
     /**
      * 期货资产管理计划
      */
     业务编号一级分类_期货资产管理计划(BussinessDictCodeRds.业务编号一级分类,"22","期货资产管理计划"),
     /**
      * 非标债权
      */
     业务编号一级分类_非标债权(BussinessDictCodeRds.业务编号一级分类,"08","非标债权"),
     /**
      * 理财直融
      */
     业务编号一级分类_理财直融(BussinessDictCodeRds.业务编号一级分类,"09","理财直融"),
     /**
      * 信贷资产流转项目
      */
     业务编号一级分类_信贷资产流转项目(BussinessDictCodeRds.业务编号一级分类,"10","信贷资产流转项目"),
     /**
      * 基础设施基金
      */
     业务编号一级分类_基础设施基金(BussinessDictCodeRds.业务编号一级分类,"11","基础设施基金"),
     /**
      * 其他产业投资基金
      */
     业务编号一级分类_其他产业投资基金(BussinessDictCodeRds.业务编号一级分类,"12","其他产业投资基金"),
     /**
      * 结构化证券
      */
     业务编号一级分类_结构化证券(BussinessDictCodeRds.业务编号一级分类,"13","结构化证券"),
     /**
      * 股票定向增发
      */
     业务编号一级分类_股票定向增发(BussinessDictCodeRds.业务编号一级分类,"14","股票定向增发"),
     /**
      * 资产受益权投资
      */
     业务编号一级分类_资产受益权投资(BussinessDictCodeRds.业务编号一级分类,"15","资产受益权投资"),
     /**
      * 券商收益凭证
      */
     业务编号一级分类_券商收益凭证(BussinessDictCodeRds.业务编号一级分类,"16","券商收益凭证"),
     /**
      * 股票质押式回购
      */
     业务编号一级分类_股票质押式回购(BussinessDictCodeRds.业务编号一级分类,"17","股票质押式回购"),
     /**
      * 信托产品
      */
     业务编号一级分类_信托产品(BussinessDictCodeRds.业务编号一级分类,"06","信托产品"),
     /**
      * 其他权益类投资
      */
     业务编号一级分类_其他权益类投资(BussinessDictCodeRds.业务编号一级分类,"26","其他权益类投资"),
     /**
      * 保险资产管理计划
      */
     业务编号一级分类_保险资产管理计划(BussinessDictCodeRds.业务编号一级分类,"07","保险资产管理计划"),
    /**
     * 券商资产管理计划
     */
    业务编号一级分类_券商资产管理计划(BussinessDictCodeRds.业务编号一级分类,"05","券商资产管理计划"),
    /**
     * 委托债券
     */
    业务编号一级分类_委托债券(BussinessDictCodeRds.业务编号一级分类,"01","委托债券"),
    /**
     * 委托权益
     */
    业务编号一级分类_委托权益(BussinessDictCodeRds.业务编号一级分类,"02","委托权益"),
    /**
     * 公募计划
     */
    业务编号一级分类_公募计划(BussinessDictCodeRds.业务编号一级分类,"03","公募计划"),
    /**
     * 自主管理
     */
    业务编号一级分类_自主管理(BussinessDictCodeRds.业务编号一级分类,"04","自主管理"),
    /**
     * 同业借款
     */
    业务编号一级分类_同业借款(BussinessDictCodeRds.业务编号一级分类,"18","同业借款"),
    /**
     * 存放同业
     */
    业务编号一级分类_存放同业(BussinessDictCodeRds.业务编号一级分类,"19","存放同业"),
    /**
     * 普通拆借
     */
    业务编号一级分类_普通拆借(BussinessDictCodeRds.业务编号一级分类,"23","普通拆借"),
    /**
     * 特殊目的载体
     */
    业务编号一级分类_特殊目的载体(BussinessDictCodeRds.业务编号一级分类,"24","特殊目的载体"),
    /**
     * 普通拆借
     */
    拆借类型_普通拆借(BussinessDictCodeRds.拆借类型,"L01","普通拆借"),
    /**
     * 特殊目的载体
     */
    拆借类型_特殊目的载体(BussinessDictCodeRds.拆借类型,"L02","特殊目的载体"),
    /**
     * 买入
     */
    交易方向_买入(BussinessDictCodeRds.交易方向,"T01","买入"),
    /**
     * 卖出
     */
    交易方向_卖出(BussinessDictCodeRds.交易方向,"T02","卖出"),
    /**
     * 卖出到账
     */
    交易方向_卖出到账(BussinessDictCodeRds.交易方向,"T40","卖出到账"),

    // 一体化对应etliu start
//    /**
//     * 行权
//     */
//    交易方向_行权(BussinessDictCode.交易方向,"T03","行权"),
    /**
     * 交易方向_行权回售
     */
    交易方向_行权回售(BussinessDictCodeRds.交易方向,"T03","行权-回售"),
    /**
     * 交易方向_行权赎回
     */
    交易方向_行权赎回(BussinessDictCodeRds.交易方向,"T39","行权-赎回"),
    // 一体化对应etliu end

    /**
     * 申购
     */
    交易方向_申购(BussinessDictCodeRds.交易方向,"T04","申购"),
    /**
     * 申购确认
     */
    交易方向_申购确认(BussinessDictCodeRds.交易方向,"T05","申购确认"),
    /**
     * 赎回
     */
    交易方向_赎回(BussinessDictCodeRds.交易方向,"T06","赎回"),
    /**
     * 到期
     */
    交易方向_到期(BussinessDictCodeRds.交易方向,"T07","到期"),
    /**
     * 还本
     */
    交易方向_还本(BussinessDictCodeRds.交易方向,"T08","还本"),
    /**
     * 收息
     */
    交易方向_收息(BussinessDictCodeRds.交易方向,"T09","收息"),
    /**
     * 付费
     */
    交易方向_付费(BussinessDictCodeRds.交易方向,"T10","付费"),
    /**
     * 交易方向_现金分红
     */
    交易方向_现金分红(BussinessDictCodeRds.交易方向, "T11", "现金分红"),
    /**
     * 红利再投
     */
    交易方向_红利再投(BussinessDictCodeRds.交易方向,"T12","红利再投"),
    /**
     * 除权登记
     */
    交易方向_除权登记(BussinessDictCodeRds.交易方向, "T13", "除权登记"),
   /** 认购确认
    */
   交易方向_认购确认(BussinessDictCodeRds.交易方向,"T18","认购确认"),
    /**
     * 赎回确认
     */
    交易方向_赎回确认(BussinessDictCodeRds.交易方向,"T19","赎回确认"),
    /**
     * 转入
     */
    交易方向_转入(BussinessDictCodeRds.交易方向,"T20","转入"),
    /**
     * 转出
     */
    交易方向_转出(BussinessDictCodeRds.交易方向,"T21","转出"),
    交易方向_收息到账(BussinessDictCodeRds.交易方向,"T22", "收息到账"),
    交易方向_还本到账(BussinessDictCodeRds.交易方向,"T23","还本到账"),
    /**
     * 赎回确认
     */
    交易方向_赎回到账(BussinessDictCodeRds.交易方向,"T24","赎回到账"),

    /**
     * 交易方向_提前支取
     */
    交易方向_提前支取(BussinessDictCodeRds.交易方向,"T38","提前支取"),
 // 一体化对应etliu end

    /**
     * 交易方向_收取
     */
    交易方向_收取(BussinessDictCodeRds.交易方向, "T98", "收取"),
    /**
     * 交易方向_支付
     */
    交易方向_支付(BussinessDictCodeRds.交易方向, "T99", "支付"),
    /**
     * 产品认购
     */
    交易方向_产品认购(BussinessDictCodeRds.交易方向,"C1","产品认购"),
    /**
     * 产品申购
     */
    交易方向_产品申购(BussinessDictCodeRds.交易方向, "C2", "产品申购"),
    /**
     * 份额强增
     */
    交易方向_份额强增(BussinessDictCodeRds.交易方向, "C3", "份额强增"),
    /**
     * 产品赎回
     */
    交易方向_产品赎回(BussinessDictCodeRds.交易方向, "C4", "产品赎回"),
    /**
     * 产品到期
     */
    交易方向_产品到期(BussinessDictCodeRds.交易方向,"C5","产品到期"),
    /**
     * 份额强减
     */
    交易方向_份额强减(BussinessDictCodeRds.交易方向, "C6", "份额强减"),
    /**
     * 中间付息
     */
    交易方向_中间付息(BussinessDictCodeRds.交易方向, "C7", "中间付息"),
    /**
     * 交易方向_产品除权登记
     */
    交易方向_产品除权登记(BussinessDictCodeRds.交易方向, "C8", "产品除权登记"),
    /**
     * 产品现金分红
     */
    交易方向_产品现金分红(BussinessDictCodeRds.交易方向, "C9", "产品现金分红"),
    /**
     * 产品红利转投
     */
    交易方向_产品红利转投(BussinessDictCodeRds.交易方向, "C10", "产品红利转投"),
    /**
     * 产品付费
     */
    交易方向_产品付费(BussinessDictCodeRds.交易方向, "C11", "产品付费"),
    // 一体化对应etliu start
    /**
     * 税费缴税
     */
    交易方向_税费缴税(BussinessDictCodeRds.交易方向, "C12", "税费缴税"),
    /**
     * 税费退税
     */
    交易方向_税费退税(BussinessDictCodeRds.交易方向, "C13", "税费退税"),
    // 一体化对应etliu end
    /**
     * 产品到期分红
     */
    交易方向_产品到期分红(BussinessDictCodeRds.交易方向, "C14", "产品到期分红"),
    /**
     * 赎回收益确认
     */
    交易方向_赎回收益确认(BussinessDictCodeRds.交易方向, "C15", "赎回收益确认"),
    /**
     * 行内
     */
    资产来源_行内(BussinessDictCodeRds.资产来源, "01", "行内"),
    /**
     * 行内
     */
    资产来源_行外(BussinessDictCodeRds.资产来源, "02", "行外"),
    /**
     * 正常
     */
    监督状态_正常(BussinessDictCodeRds.监督状态, "NORMAL", "正常"),
    /**
     * 正常
     */
    监督状态_触发审批(BussinessDictCodeRds.监督状态, "APPROVE", "触发审批"),
    /**
     * 警告
     */
    监督状态_触发预警(BussinessDictCodeRds.监督状态, "WARNING", "触发预警"),
    /**
     * 违规
     */
    监督状态_触发超限(BussinessDictCodeRds.监督状态, "VIOLATION", "触发超限"),
    /**
     * 异常
     */
    监督状态_异常(BussinessDictCodeRds.监督状态, "ERROR", "异常"),
    /**
     * 单组合
     */
    组合层次_单组合(BussinessDictCodeRds.组合层次, "01", "单组合"),
    /**
     * 组合包
     */
    组合层次_组合包(BussinessDictCodeRds.组合层次, "02", "组合包"),

    /**
     * SQL
     */
    指标参数实现类型_SQL(BussinessDictCodeRds.指标参数实现类型, "SQL", "SQL"),
    /**
     * 固定值
     */
    指标参数实现类型_固定值(BussinessDictCodeRds.指标参数实现类型, "FIX", "固定值"),
    /**
     * 类名
     */
    指标参数实现类型_类名(BussinessDictCodeRds.指标参数实现类型, "PLUGIN", "类名"),
    /**
     * REDIS
     */
    指标参数计算方式_REDIS(BussinessDictCodeRds.指标参数计算方式, "REDIS", "REDIS"),
    /**
     * DATABASE
     */
    指标参数计算方式_DB(BussinessDictCodeRds.指标参数计算方式, "DATABASE", "数据库"),
    /**
     * 插件模板
     */
    指标参数计算方式_PLUGIN(BussinessDictCodeRds.指标参数计算方式, "PLUGIN", "插件模板"),
    /**
     * 券商资产管理计划
     */
    计划类资产一级分类_券商资产管理计划(BussinessDictCodeRds.计划类资产一级分类,"N01","券商资产管理计划"),
    /**
     * 信托产品
     */
    计划类资产一级分类_信托产品(BussinessDictCodeRds.计划类资产一级分类,"N02","信托产品"),
    /**
     * 基金资产管理计划
     */
    计划类资产一级分类_基金资产管理计划(BussinessDictCodeRds.计划类资产一级分类,"N03","基金资产管理计划"),
    /**
     * 期货资产管理计划
     */
    计划类资产一级分类_期货资产管理计划(BussinessDictCodeRds.计划类资产一级分类,"N05","期货资产管理计划"),
    /**
     * 其他资产管理计划
     */
    计划类资产一级分类_其他资产管理计划(BussinessDictCodeRds.计划类资产一级分类,"N06","其他资产管理计划"),
    /**
     * 保险资产管理计划
     */
    计划类资产一级分类_保险资产管理计划(BussinessDictCodeRds.计划类资产一级分类,"N04","保险资产管理计划"),

     /**
     * 公募计划
     */
    计划类资产二级分类_公募计划(BussinessDictCodeRds.计划类资产二级分类,"01","公募计划"),
    /**
     * 委托债券
     */
    计划类资产二级分类_委托债券(BussinessDictCodeRds.计划类资产二级分类,"02","委托债券"),
    /**
     * 委托权益
     */
    计划类资产二级分类_委托权益(BussinessDictCodeRds.计划类资产二级分类,"03","委托权益"),
    /**
     * 自主管理
     */
    计划类资产二级分类_自主管理(BussinessDictCodeRds.计划类资产二级分类,"04","自主管理"),
    /**
     * 其他交易流通场所
     */
    交易流通市场_其他交易流通场所(BussinessDictCodeRds.交易流通场所,"99","其他交易流通场所"),
    /**
     * 接口接入
     */
    生成方式_接口接入(BussinessDictCodeRds.生成方式,"01","接口接入"),
    /**
     * 画面录入
     */
    生成方式_画面录入(BussinessDictCodeRds.生成方式,"02","画面录入"),
    /**
     * 集合
     */
    资管计划属性_集合(BussinessDictCodeRds.资管计划属性,"01","集合"),
    /**
     * 其他
     */
    资管计划属性_其他(BussinessDictCodeRds.资管计划属性,"99","其他"),
    /**
     * 一级市场
     */
    交易市场_一级市场(BussinessDictCodeRds.交易市场,"01","一级市场"),
    /**
     * 二级市场
     */
    交易市场_二级市场(BussinessDictCodeRds.交易市场,"02","二级市场"),
    /**
     * 深圳证券交易所
     */
    交易场所_深圳证券交易所(BussinessDictCodeRds.交易场所,"04","深圳证券交易所"),
    /**
     * 上海证券交易所
     */
    交易场所_上海证券交易所(BussinessDictCodeRds.交易场所,"03","上海证券交易所"),
    /**
     * 场外
     */
    交易场所_场外(BussinessDictCodeRds.交易场所,"09","场外"),
    /**
     * 银行间
     */
    交易场所_银行间(BussinessDictCodeRds.交易场所,"01","银行间"),
    /**
     * 商业银行柜台市场
     */
    交易场所_商业银行柜台市场(BussinessDictCodeRds.交易场所, "04", "商业银行柜台市场"),
    /**
     * 机构间私募系统
     */
    交易场所_机构间私募系统(BussinessDictCodeRds.交易场所, "05", "机构间私募系统"),
    /**
     * 其他
     */
    交易场所_其他(BussinessDictCodeRds.交易场所, "07", "其他"),

    /**
     * 315检查
     */
    提醒类型_大分类_315检查(BussinessDictCodeRds.提醒类型_大分类,"B01","315检查"),
    /**
     * 申赎及存续期确认
     */
    提醒类型_大分类_申赎及存续期确认(BussinessDictCodeRds.提醒类型_大分类,"B02","申赎及存续期确认"),
    /**
     * 债券行权
     */
    提醒类型_大分类_债券行权(BussinessDictCodeRds.提醒类型_大分类,"B03","债券行权"),
    /**
     * 债券覆盖
     */
    提醒类型_大分类_债券覆盖(BussinessDictCodeRds.提醒类型_大分类,"B04","债券覆盖"),
    /**
     * 债券提前3个月
     */
    提醒类型_小分类_债券提前3个月 (BussinessDictCodeRds.提醒类型_小分类,"S01","债券-提前3个月"),
    /**
     * 债券提前1个月
     */
    提醒类型_小分类_债券提前1个月 (BussinessDictCodeRds.提醒类型_小分类,"S02","债券-提前1个月"),
    /**
     * 债券提前5天
     */
    提醒类型_小分类_债券提前5天 (BussinessDictCodeRds.提醒类型_小分类,"S03","债券-提前5天"),
    /**
     * 项目类提前3个月
     */
    提醒类型_小分类_项目类提前3个月 (BussinessDictCodeRds.提醒类型_小分类,"S04","项目类-提前3个月"),
    /**
     * 项目类提前1个月
     */
    提醒类型_小分类_项目类提前1个月 (BussinessDictCodeRds.提醒类型_小分类,"S05","项目类-提前1个月"),
    /**
     * 项目类提前5天
     */
    提醒类型_小分类_项目类提前5天 (BussinessDictCodeRds.提醒类型_小分类,"S06","项目类-提前5天"),
    /**
     * 利率型提前3个月
     */
    提醒类型_小分类_计划类_利率型提前3个月 (BussinessDictCodeRds.提醒类型_小分类,"S07","计划类（利率型）-提前3个月"),
    /**
     * 利率型提前1个月
     */
    提醒类型_小分类_计划类_利率型提前1个月 (BussinessDictCodeRds.提醒类型_小分类,"S08","计划类（利率型）-提前1个月"),
    /**
     * 利率型提前5天
     */
    提醒类型_小分类_计划类_利率型提前5天 (BussinessDictCodeRds.提醒类型_小分类,"S09","计划类（利率型）-提前5天"),
    /**
     * 货币型申购确认
     */
    提醒类型_小分类_基金类_货币型申购确认  (BussinessDictCodeRds.提醒类型_小分类,"S10","基金类（货币型）申购确认 "),
    /**
     * 货币型赎回确认
     */
    提醒类型_小分类_基金类_货币型赎回确认 (BussinessDictCodeRds.提醒类型_小分类,"S11","基金类（货币型）赎回确认 "),
    /**
     * 货币型回款确认
     */
    提醒类型_小分类_基金类_货币型回款确认 (BussinessDictCodeRds.提醒类型_小分类,"S12","基金类（货币型）回款确认 "),
    /**
     * 净值型申购确认
     */
    提醒类型_小分类_基金类_净值型申购确认 (BussinessDictCodeRds.提醒类型_小分类,"S13","基金类（净值型）申购确认 "),
    /**
     * 净值型赎回确认
     */
    提醒类型_小分类_基金类_净值型赎回确认 (BussinessDictCodeRds.提醒类型_小分类,"S14","基金类（净值型）赎回确认 "),
    /**
     * 净值型回款确认
     */
    提醒类型_小分类_基金类_净值型回款确认 (BussinessDictCodeRds.提醒类型_小分类,"S15","基金类（净值型）回款确认 "),
    /**
     * 净值型申购确认
     */
    提醒类型_小分类_计划类_净值型申购确认 (BussinessDictCodeRds.提醒类型_小分类,"S16","计划类（净值型）申购确认 "),
    /**
     * 净值型赎回确认
     */
    提醒类型_小分类_计划类_净值型赎回确认 (BussinessDictCodeRds.提醒类型_小分类,"S17","计划类（净值型）赎回确认 "),
    /**
     * 净值型回款确认
     */
    提醒类型_小分类_计划类_净值型回款确认 (BussinessDictCodeRds.提醒类型_小分类,"S18","计划类（净值型）回款确认 "),
    /**
     * 债券存续期
     */
    提醒类型_小分类_债券存续期  (BussinessDictCodeRds.提醒类型_小分类,"S19","债券存续期"),
    /**
     * 项目类资产存续期
     */
    提醒类型_小分类_项目类资产存续期  (BussinessDictCodeRds.提醒类型_小分类,"S20","项目类资产存续期"),
    /**
     * 计划类_利率型存续期
     */
    提醒类型_小分类_计划类_利率型存续期 (BussinessDictCodeRds.提醒类型_小分类,"S21","计划类（利率型）存续期"),
    /**
     * 债券行权
     */
    提醒类型_小分类_债券行权 (BussinessDictCodeRds.提醒类型_小分类,"S22","债券行权"),
    /**
     * 债券覆盖
     */
    提醒类型_小分类_债券覆盖 (BussinessDictCodeRds.提醒类型_小分类,"S23","债券覆盖"),
    /**
     * 市场日历信息
     */
    共同业务类型_市场日历信息(BussinessDictCodeRds.共同业务类型, "MST01", "市场日历信息"),
    /**
     * 风险权数管理
     */
    共同业务类型_风险权数管理(BussinessDictCodeRds.共同业务类型, "FINPROD25", "风险权数管理"),
    /**
     * 金融产品
     */
    共同业务类型_金融产品(BussinessDictCodeRds.共同业务类型,"01","金融产品"),
    /**
     * 债券估值行情信息
     */
    共同业务类型_债券估值行情信息(BussinessDictCodeRds.共同业务类型, "MST02", "债券估值行情信息"),
    /**
     * 基金类估值行情信息
     */
    共同业务类型_基金类估值行情信息(BussinessDictCodeRds.共同业务类型, "MST03", "基金类估值行情信息"),
    /**
     * 计划类（净值型）资产估值行情信息
     */
    共同业务类型_计划类_净值型_资产估值行情信息(BussinessDictCodeRds.共同业务类型, "MST04", "计划类（净值型）资产估值行情信息"),
    /**
     * 汇率行情信息
     */
    共同业务类型_汇率行情信息(BussinessDictCodeRds.共同业务类型, "MST05", "汇率行情信息"),
    /**
     * 利率行情信息
     */
    共同业务类型_利率行情信息(BussinessDictCodeRds.共同业务类型, "MST06", "利率行情信息"),
    /**
     * 指数行情信息
     */
    共同业务类型_指数行情信息(BussinessDictCodeRds.共同业务类型, "MST07", "指数行情信息"),
    /**
     * 收益率曲线信息
     */
    共同业务类型_收益率曲线信息(BussinessDictCodeRds.共同业务类型, "MST08", "收益率曲线信息"),
    /**
     * 资金账户管理
     */
    共同业务类型_资金账户管理(BussinessDictCodeRds.共同业务类型, "MST09", "资金账户管理"),
    /**
     * 证券账户管理
     */
    共同业务类型_证券账户管理(BussinessDictCodeRds.共同业务类型, "MST10", "证券账户管理"),
    /**
     * 业绩基准管理
     */
    共同业务类型_业绩基准管理(BussinessDictCodeRds.共同业务类型, "MST11", "业绩基准管理"),
    /**
     * 自然人管理
     */
    共同业务类型_自然人管理(BussinessDictCodeRds.共同业务类型, "MST12", "自然人管理"),
    /**
     * 市场机构管理
     */
    共同业务类型_市场机构管理(BussinessDictCodeRds.共同业务类型, "MST13", "市场机构管理"),
    /**
     * 客户性质管理
     */
    共同业务类型_客户性质管理(BussinessDictCodeRds.共同业务类型, "MST14", "客户性质管理"),
    /**
     * 交易对手管理
     */
    共同业务类型_交易对手管理(BussinessDictCodeRds.共同业务类型, "MST15", "交易对手管理"),
    /**
     * 费用品种管理
     */
    共同业务类型_费用品种管理(BussinessDictCodeRds.共同业务类型, "MST16", "费用品种管理"),
    /**
     * 日历类型管理
     */
    共同业务类型_日历类型管理(BussinessDictCodeRds.共同业务类型, "MST17", "日历类型管理"),
    /**
     * OPICS参数维护
     */
    共同业务类型_OPICS参数维护(BussinessDictCodeRds.共同业务类型, "MST18", "OPICS参数维护"),
    /**
     * 复合基准
     */
    共同业务类型_复合基准(BussinessDictCodeRds.共同业务类型, "MST19", "复合基准"),
    /**
     * 资金后台管理
     */
    共同业务类型_资金后台管理(BussinessDictCodeRds.共同业务类型, "STL01", "资金后台管理"),
    /**
     * 流入资金核对
     */
    共同业务类型_流入资金核对(BussinessDictCodeRds.共同业务类型, "STL02", "流入资金核对"),
    /**
     * 流出资金核对
     */
    共同业务类型_流出资金核对(BussinessDictCodeRds.共同业务类型, "STL03", "流出资金核对"),
    /**
     * DVP清算指令管理
     */
    共同业务类型_DVP清算指令管理(BussinessDictCodeRds.共同业务类型, "STL04", "DVP清算指令管理"),
    /**
     * 清算指令管理
     */
    共同业务类型_清算指令管理(BussinessDictCodeRds.共同业务类型, "STL05", "清算指令管理"),
    /**
     * 风险试算
     */
    共同业务类型_风险试算(BussinessDictCodeRds.共同业务类型, "RSK01", "风险试算"),
    /**
     * 授权管理
     */
    共同业务类型_授权管理(BussinessDictCodeRds.共同业务类型, "RSK02", "授权管理"),
    /**
     * 组合自动关联指标
     */
    共同业务类型_组合自动关联指标(BussinessDictCodeRds.共同业务类型, "RSK25", "组合自动关联指标"),
    /**
     * 共同业务类型_个性化参数
     */
    共同业务类型_个性化参数(BussinessDictCodeRds.共同业务类型, "RSK26", "个性化参数"),
    /**
     * 主体授信额度管理
     */
    共同业务类型_主体授信额度管理(BussinessDictCodeRds.共同业务类型, "RSK03", "主体授信额度管理"),
    /**
     * 授信额度调整
     */
    共同业务类型_授信额度调整(BussinessDictCodeRds.共同业务类型, "RSK04", "授信额度调整"),
    /**
     * 额度扣减规则管理
     */
    共同业务类型_额度扣减规则管理(BussinessDictCodeRds.共同业务类型, "RSK05", "额度扣减规则管理"),
    /**
     * 限额指标参数管理
     */
    共同业务类型_限额指标参数管理(BussinessDictCodeRds.共同业务类型, "RSK06", "限额指标参数管理"),
    /**
     * 限额指标表达式管理
     */
    共同业务类型_限额指标表达式管理(BussinessDictCodeRds.共同业务类型, "RSK07", "限额指标表达式管理"),
    /**
     * 限额监督指标管理
     */
    共同业务类型_限额监督指标管理(BussinessDictCodeRds.共同业务类型, "RSK08", "限额监督指标管理"),
    /**
     * 限额组合关联监督指标管理
     */
    共同业务类型_限额组合关联监督指标管理(BussinessDictCodeRds.共同业务类型, "RSK09", "限额组合关联监督指标管理"),
    /**
     * 估值表指标结构映射
     */
    共同业务类型_估值表指标结构映射(BussinessDictCodeRds.共同业务类型, "RSK10", "估值表指标结构映射"),
    /**
     * 估值表资产科目映射
     */
    共同业务类型_估值表资产科目映射(BussinessDictCodeRds.共同业务类型, "RSK11", "估值表资产科目映射"),
    /**
     * 估值表汇总科目映射
     */
    共同业务类型_估值表汇总科目映射(BussinessDictCodeRds.共同业务类型, "RSK12", "估值表汇总科目映射"),
    /**
     * 估值表关联导入映射
     */
    共同业务类型_估值表关联导入映射(BussinessDictCodeRds.共同业务类型, "RSK13", "估值表关联导入映射"),
    /**
     * 估值表导入管理
     */
    共同业务类型_估值表导入管理(BussinessDictCodeRds.共同业务类型, "RSK14", "估值表导入管理"),
    /**
     * 估值表底层资产映射
     */
    共同业务类型_估值表底层资产映射(BussinessDictCodeRds.共同业务类型, "RSK15", "估值表底层资产映射"),
    /**
     * 机构授信
     */
    共同业务类型_机构授信(BussinessDictCodeRds.共同业务类型, "RSK16", "机构授信"),
    /**
     * 资产授信
     */
    共同业务类型_资产授信(BussinessDictCodeRds.共同业务类型, "RSK17", "资产授信"),
    /**
     * 资产系列授信
     */
    共同业务类型_资产系列授信(BussinessDictCodeRds.共同业务类型, "RSK18", "资产系列授信"),
    /**
     * 预占用管理
     */
    共同业务类型_预占用管理(BussinessDictCodeRds.共同业务类型, "RSK19", "预占用管理"),
    /**
     * 组合管理
     */
    共同业务类型_组合管理(BussinessDictCodeRds.共同业务类型, "PTL01", "组合管理"),
    /**
     * 用户组合管理
     */
    共同业务类型_用户组合管理(BussinessDictCodeRds.共同业务类型, "PTL02", "用户组合管理"),
    /**
     * 活期结息
     */
    共同业务类型_活期结息(BussinessDictCodeRds.共同业务类型, "PTL03", "活期结息"),
    /**
     * 自定义现金流
     */
    共同业务类型_自定义现金流(BussinessDictCodeRds.共同业务类型, "PTL04", "自定义现金流"),
    /**
     * 资金划拨管理
     */
    共同业务类型_资金划拨管理(BussinessDictCodeRds.共同业务类型, "PTL05", "资金划拨管理"),
    /**
     * 账户活期利率管理
     */
    共同业务类型_账户活期利率管理(BussinessDictCodeRds.共同业务类型, "PTL06", "账户活期利率管理"),
    /**
     * 自动指令分发（交易主管）
     */
    共同业务类型_自动指令分发_交易主管_(BussinessDictCodeRds.共同业务类型, "TRD01", "自动指令分发（交易主管）"),
    /**
     * 债券投资交易
     */
    共同业务类型_债券投资交易(BussinessDictCodeRds.共同业务类型, "TRD02", "债券投资交易"),
    /**
     * 拆借投资交易
     */
    共同业务类型_拆借投资交易(BussinessDictCodeRds.共同业务类型, "TRD03", "拆借投资交易"),
    /**
     * 存款类资产投资交易
     */
    共同业务类型_存款类资产投资交易(BussinessDictCodeRds.共同业务类型, "TRD04", "存款类资产投资交易"),
    /**
     * 基金类资产投资交易
     */
    共同业务类型_基金类资产投资交易(BussinessDictCodeRds.共同业务类型, "TRD05", "基金类资产投资交易"),
    /**
     * 计划类资产（净值型）投资交易
     */
    共同业务类型_计划类资产_净值型_投资交易(BussinessDictCodeRds.共同业务类型, "TRD06", "计划类资产（净值型）投资交易"),
    /**
     * 计划类资产（利率型）投资交易
     */
    共同业务类型_计划类资产_利率型_投资交易(BussinessDictCodeRds.共同业务类型, "TRD07", "计划类资产（利率型）投资交易"),
    /**
     * 回购投资交易
     */
    共同业务类型_回购投资交易(BussinessDictCodeRds.共同业务类型, "TRD08", "回购投资交易"),
    /**
     * 项目类资产投资交易
     */
    共同业务类型_项目类资产投资交易(BussinessDictCodeRds.共同业务类型, "TRD09", "项目类资产投资交易"),
    /**
     * 债券存续期交易
     */
    共同业务类型_债券存续期交易(BussinessDictCodeRds.共同业务类型, "TRD10", "债券存续期交易"),
    /**
     * 拆借存续期交易
     */
    共同业务类型_拆借存续期交易(BussinessDictCodeRds.共同业务类型, "TRD11", "拆借存续期交易"),
    /**
     * 存款类资产存续期交易
     */
    共同业务类型_存款类资产存续期交易(BussinessDictCodeRds.共同业务类型, "TRD12", "存款类资产存续期交易"),
    /**
     * 净值型资产存续期交易
     */
    共同业务类型_净值型资产存续期交易(BussinessDictCodeRds.共同业务类型, "TRD13", "净值型资产存续期交易"),
    /**
     * 基金存续期交易
     */
    共同业务类型_基金存续期交易(BussinessDictCodeRds.共同业务类型, "TRD14", "基金存续期交易"),
    /**
     * 计划类资产（利率型）存续期交易
     */
    共同业务类型_计划类资产_利率型_存续期交易(BussinessDictCodeRds.共同业务类型, "TRD15", "计划类资产（利率型）存续期交易"),
    /**
     * 回购存续期交易
     */
    共同业务类型_回购存续期交易(BussinessDictCodeRds.共同业务类型, "TRD16", "回购存续期交易"),
    /**
     * 买断式回购投资交易
     */
    共同业务类型_买断式回购投资交易(BussinessDictCodeRds.共同业务类型, "TRD48", "买断式回购投资交易"),
    /**
     * 买断式回购存续期交易
     */
    共同业务类型_买断式回购存续期交易(BussinessDictCodeRds.共同业务类型, "TRD49","买断式回购存续期交易"),
    /**
     * 项目类资产存续期交易
     */
    共同业务类型_项目类资产存续期交易(BussinessDictCodeRds.共同业务类型, "TRD17", "项目类资产存续期交易"),
    /**
     * 逾期资产管理
     */
    共同业务类型_逾期资产管理(BussinessDictCodeRds.共同业务类型, "TRD18", "逾期资产管理"),
    /**
     * 债券买入
     */
    共同业务类型_债券买入(BussinessDictCodeRds.共同业务类型, "TRD19", "债券买入"),
    /**
     * 债券卖出
     */
    共同业务类型_债券卖出(BussinessDictCodeRds.共同业务类型, "TRD20", "债券卖出"),
    /**
     * 拆入交易
     */
    共同业务类型_拆入交易(BussinessDictCodeRds.共同业务类型, "TRD22", "拆入交易"),
    /**
     * 特拆交易
     */
    共同业务类型_特拆交易(BussinessDictCodeRds.共同业务类型, "TRD23", "特拆交易"),
    /**
     * 拆出交易
     */
    共同业务类型_拆出交易(BussinessDictCodeRds.共同业务类型, "TRD24", "拆出交易"),
    /**
     * 项目类资产买入
     */
    共同业务类型_项目类资产买入(BussinessDictCodeRds.共同业务类型, "TRD25", "项目类资产买入"),
    /**
     * 项目类资产卖出
     */
    共同业务类型_项目类资产卖出(BussinessDictCodeRds.共同业务类型, "TRD26", "项目类资产卖出"),
    /**
     * 资产存续期交易
     */
    共同业务类型_资产存续期交易(BussinessDictCodeRds.共同业务类型, "TRD46", "资产存续期交易"),
    /**
     * 投资委托管理
     */
    共同业务类型_投资委托管理(BussinessDictCodeRds.共同业务类型, "ETR01", "投资委托管理"),
    /**
     * 债券投资委托
     */
    共同业务类型_债券投资委托(BussinessDictCodeRds.共同业务类型, "ETR02", "债券投资委托"),
    /**
     * 拆借投资委托
     */
    共同业务类型_拆借投资委托(BussinessDictCodeRds.共同业务类型, "ETR03", "拆借投资委托"),
    /**
     * 存款类资产投资委托
     */
    共同业务类型_存款类资产投资委托(BussinessDictCodeRds.共同业务类型, "ETR04", "存款类资产投资委托"),
    /**
     * 基金类资产投资委托
     */
    共同业务类型_基金类资产投资委托(BussinessDictCodeRds.共同业务类型, "ETR05", "基金类资产投资委托"),
    /**
     * 计划类资产（净值型）投资委托
     */
    共同业务类型_计划类资产_净值型_投资委托(BussinessDictCodeRds.共同业务类型, "ETR06", "计划类资产（净值型）投资委托"),
    /**
     * 计划类资产（利率型）投资委托
     */
    共同业务类型_计划类资产_利率型_投资委托(BussinessDictCodeRds.共同业务类型, "ETR07", "计划类资产（利率型）投资委托"),
    /**
     * 回购投资委托
     */
    共同业务类型_回购投资委托(BussinessDictCodeRds.共同业务类型, "ETR08", "回购投资委托"),
    /**
     * 项目类资产投资委托
     */
    共同业务类型_项目类资产投资委托(BussinessDictCodeRds.共同业务类型, "ETR09", "项目类资产投资委托"),
    /**
     * 项目类资产投资委托
     */
    共同业务类型_计划类资产_货币型_投资委托(BussinessDictCodeRds.共同业务类型, "ETR10", "计划类资产（货币型）投资委托"),
    /**
     * 债券类资产管理
     */
    共同业务类型_债券类资产管理(BussinessDictCodeRds.共同业务类型, "FINASSET01", "债券类资产管理"),
    /**
     * 存款类资产管理
     */
    共同业务类型_存款类资产管理(BussinessDictCodeRds.共同业务类型, "FINASSET02", "存款类资产管理"),
    /**
     * 基金类资产管理
     */
    共同业务类型_基金类资产管理(BussinessDictCodeRds.共同业务类型, "FINASSET03", "基金类资产管理"),
    /**
     * 计划类资产（净值型）资产管理
     */
    共同业务类型_计划类资产_净值型_资产管理(BussinessDictCodeRds.共同业务类型, "FINASSET04", "计划类资产（净值型）资产管理"),
    /**
     * 计划类资产（利率型）资产管理
     */
    共同业务类型_计划类资产_利率型_资产管理(BussinessDictCodeRds.共同业务类型, "FINASSET05", "计划类资产（利率型）资产管理"),
    /**
     * 项目类资产管理
     */
    共同业务类型_项目类资产管理(BussinessDictCodeRds.共同业务类型, "FINASSET06", "项目类资产管理"),

    /**
     * 债券类资产利率调整
     */
    共同业务类型_债券类资产利率调整(BussinessDictCodeRds.共同业务类型, "FINASSET07", "债券类资产利率调整"),
    /**
     * 存款类资产利率调整
     */
    共同业务类型_存款类资产利率调整(BussinessDictCodeRds.共同业务类型, "FINASSET08", "存款类资产利率调整"),
    /**
     * 计划类资产利率调整
     */
    共同业务类型_计划类资产利率调整(BussinessDictCodeRds.共同业务类型, "FINASSET09", "计划类资产利率调整"),
    /**
     * 项目类资产利率调整
     */
    共同业务类型_项目类资产利率调整(BussinessDictCodeRds.共同业务类型, "FINASSET10", "项目类资产利率调整"),
    /**
     * 项目类资产利率调整
     */
    共同业务类型_计划类资产_货币型_资产管理(BussinessDictCodeRds.共同业务类型, "FINASSET11", "计划类资产（货币型）资产管理"),
    /**
     * 通道管理
     */
    共同业务类型_通道管理(BussinessDictCodeRds.共同业务类型, "FINASSET12", "通道管理"),
    /**
     * 通道利率调整
     */
    共同业务类型_通道利率调整(BussinessDictCodeRds.共同业务类型, "FINASSET15", "通道利率调整"),
    /**
     * 通道费用支付
     */
    共同业务类型_通道费用支付(BussinessDictCodeRds.共同业务类型, "FINASSET14", "通道费用支付"),
    /**
     * 信息披露模板管理
     */
    共同业务类型_信息披露模板管理(BussinessDictCodeRds.共同业务类型, "FINPROD01", "信息披露模板管理"),
    /**
     * 产品要素模板管理
     */
    共同业务类型_产品要素模板管理(BussinessDictCodeRds.共同业务类型, "FINPROD02", "产品要素模板管理"),
    /**
     * 产品排期管理
     */
    共同业务类型_产品排期管理(BussinessDictCodeRds.共同业务类型, "FINPROD03", "产品排期管理"),
    /**
     * 产品要素管理
     */
    共同业务类型_产品要素管理(BussinessDictCodeRds.共同业务类型, "FINPROD04", "产品要素管理"),
    /**
     * 产品报价管理
     */
    共同业务类型_产品报价管理(BussinessDictCodeRds.共同业务类型, "FINPROD05", "产品报价管理"),
    /**
     * 产品费率报价管理
     */
    共同业务类型_产品费率报价管理(BussinessDictCodeRds.共同业务类型, "FINPROD06", "产品费率报价管理"),
    /**
     * 产品业绩基准报价管理
     */
    共同业务类型_产品业绩基准报价管理(BussinessDictCodeRds.共同业务类型, "FINPROD07", "产品业绩基准报价管理"),
    /**
     * 浮动管理费报价管理
     */
    共同业务类型_浮动管理费报价管理(BussinessDictCodeRds.共同业务类型, "FINPROD08", "浮动管理费报价管理"),
    /**
     * 开放计划调整
     */
    共同业务类型_开放计划调整(BussinessDictCodeRds.共同业务类型, "FINPROD09", "开放计划调整"),
    /**
     * 付息计划调整
     */
    共同业务类型_付息计划调整(BussinessDictCodeRds.共同业务类型, "FINPROD10", "付息计划调整"),
    /**
     * 付费计划调整
     */
    共同业务类型_付费计划调整(BussinessDictCodeRds.共同业务类型, "FINPROD24", "付费计划调整"),
    /**
     * 到期日调整
     */
    共同业务类型_到期日调整(BussinessDictCodeRds.共同业务类型, "FINPROD11", "到期日调整"),
    /**
     * 付息日调整
     */
    共同业务类型_付息日调整(BussinessDictCodeRds.共同业务类型, "FINPROD12", "付息日调整"),
    /**
     * 公告及产品说明书管理
     */
    共同业务类型_公告及产品说明书管理(BussinessDictCodeRds.共同业务类型, "FINPROD13", "公告及产品说明书管理"),
    /**
     * 产品成立管理
     */
    共同业务类型_产品成立管理(BussinessDictCodeRds.共同业务类型, "FINPROD14", "产品成立管理"),
    /**
     * 强增强减管理
     */
    共同业务类型_强增强减管理(BussinessDictCodeRds.共同业务类型, "FINPROD15", "强增强减管理"),
    /**
     * 中间付息管理
     */
    共同业务类型_中间付息管理(BussinessDictCodeRds.共同业务类型, "FINPROD16", "中间付息管理"),
    /**
     * 产品到期管理
     */
    共同业务类型_产品到期管理(BussinessDictCodeRds.共同业务类型, "FINPROD17", "产品到期管理"),
    /**
     * 净值型产品分红方案管理
     */
    共同业务类型_净值型产品分红方案管理(BussinessDictCodeRds.共同业务类型, "FINPROD18", "净值型产品分红方案管理"),
    /**
     * 产品费用支付管理
     */
    共同业务类型_产品费用支付管理(BussinessDictCodeRds.共同业务类型, "FINPROD19", "产品费用支付管理"),
    /**
     * 产品申购
     */
    共同业务类型_产品申购(BussinessDictCodeRds.共同业务类型, "FINPROD20", "产品申购"),
    /**
     * 产品赎回
     */
    共同业务类型_产品赎回(BussinessDictCodeRds.共同业务类型, "FINPROD21", "产品赎回"),
    /**
     * 申报附件方案管理
     */
    共同业务类型_申报附件方案管理(BussinessDictCodeRds.共同业务类型, "FINPROD23", "申报附件方案管理"),
    /**
     * 存款录入
     */
    共同业务类型_存款录入(BussinessDictCodeRds.共同业务类型,"TRD21", "存款录入"),
    /**
     * 计划类（货币型）投资交易
     */
    共同业务类型_计划类_货币型_投资交易(BussinessDictCodeRds.共同业务类型,"TRD47", "计划类（货币型）投资交易"),
    /**
     * 计划类（货币型）存续期
     */
    共同业务类型_计划类_货币型_存续期(BussinessDictCodeRds.共同业务类型,"TRD50", "计划类（货币型）存续期"),

    /**
     * 托管流水借贷方向_借
     */
    托管流水借贷方向_借(BussinessDictCodeRds.托管流水借贷方向,"01","借"),

    /**
     * 托管流水借贷方向_贷
     */
    托管流水借贷方向_贷(BussinessDictCodeRds.托管流水借贷方向,"02","贷"),

    /**
     * 债券(投管用)
     */
    金融产品类型_债券(BussinessDictCodeRds.金融产品类型,"F01","债券"),

    /**
     * 金融产品类型_债券买断式正回购
     */
    金融产品类型_债券买断式正回购(BussinessDictCodeRds.金融产品类型, "F04", "债券买断式正回购"),

    /**
     * 金融产品类型_债券买断式逆回购
     */
    金融产品类型_债券买断式逆回购(BussinessDictCodeRds.金融产品类型, "F05", "债券买断式逆回购"),

    /**
     *理财产品 (投管用)
     */
    金融产品类型_理财产品(BussinessDictCodeRds.金融产品类型,"F16","理财产品"),
    /**
     * 存款(投管用)
     */
    金融产品类型_存款(BussinessDictCodeRds.金融产品类型,"F07","存款"),
    /**
     * 固定管理费(投管用)
     */
    金融产品类型_固定管理费(BussinessDictCodeRds.金融产品类型, "C03", "固定管理费"),
    /**
     * 浮动管理费(投管用)
     */
    金融产品类型_浮动管理费(BussinessDictCodeRds.金融产品类型, "C04", "浮动管理费"),
    /**
     * 托管费(投管用)
     */
    金融产品类型_托管费(BussinessDictCodeRds.金融产品类型, "C05", "托管费"),
    /**
     * 销售费(投管用)
     */
    金融产品类型_销售费(BussinessDictCodeRds.金融产品类型, "C06", "销售费"),
    /**
     * 税额(投管用)
     */
    金融产品类型_税额(BussinessDictCodeRds.金融产品类型, "C07", "税额"),
    /**
     * 代销费(投管用)
     */
    金融产品类型_代销费(BussinessDictCodeRds.金融产品类型, "C10", "代销费"),
    /**
     * 债券质押式逆回购
     */
    金融产品类型_债券质押式逆回购(BussinessDictCodeRds.金融产品类型,"F03","债券质押式逆回购"),
    /**
     * 债券质押式正回购
     */
    金融产品类型_债券质押式正回购(BussinessDictCodeRds.金融产品类型,"F02","债券质押式正回购"),
    /**
     * 拆出
     */
    金融产品类型_拆出(BussinessDictCodeRds.金融产品类型,"F09","拆出"),
    /**
     * 拆入
     */
    金融产品类型_拆入(BussinessDictCodeRds.金融产品类型,"F08","拆入"),
    /**
     * 特拆拆入
     */
    金融产品类型_特拆拆入(BussinessDictCodeRds.金融产品类型,"F25","特拆拆入"),
    /**
     * 理财子产品
     */
    金融产品类型_理财子产品(BussinessDictCodeRds.金融产品类型,"F26","理财子产品"),
    /**
     * 项目类资产
     */
    金融产品类型_项目类资产(BussinessDictCodeRds.金融产品类型,"F18","项目类资产"),
    /**
     * 自定义现金流
     */
    金融产品类型_自定义现金流(BussinessDictCodeRds.金融产品类型,"F23","自定义现金流"),

    /**
     * 理财产品分层
     */
    金融产品类型_理财产品分层(BussinessDictCodeRds.金融产品类型,"F24","理财产品分层"),

    /**
     * 清算操作类型_新建
     */
    清算操作类型_新建(BussinessDictCodeRds.清算操作类型,"01","新建"),
    /**
     * 清算操作类型_撤销
     */
    清算操作类型_撤销(BussinessDictCodeRds.清算操作类型,"03","撤销"),

    /**
     * 中间收息
     */
    现金流类型_中间收息(BussinessDictCodeRds.现金流类型,"01","中间收息"),
    /**
     * 中间还本
     */
    现金流类型_中间还本(BussinessDictCodeRds.现金流类型,"02","中间还本"),
    /**
     * 到期还本
     */
    现金流类型_到期还本(BussinessDictCodeRds.现金流类型,"03","到期还本"),
    /**
     * 到期收息
     */
    现金流类型_到期收息(BussinessDictCodeRds.现金流类型,"04","到期收息"),
    /**
     * 费用支付
     */
    现金流类型_费用支付(BussinessDictCodeRds.现金流类型,"05","费用支付"),
    /**
     * 已结算
     */
    结算状态_已结算(BussinessDictCodeRds.结算状态,"01","已结算"),
    /**
     * 未结算
     */
    结算状态_未结算(BussinessDictCodeRds.结算状态,"02","未结算"),
    /**
     * 质押式协议回购
     */
    回购类型_质押式协议回购(BussinessDictCodeRds.回购类型,"H01","质押式协议回购"),
    /**
     * 质押式竞价回购
     */
    回购类型_质押式竞价回购(BussinessDictCodeRds.回购类型,"H02","质押式竞价回购"),
    /**
     * 回购类型_买断式回购
     */
    回购类型_买断式回购(BussinessDictCodeRds.回购类型,"H03","买断式回购"),
    /**
     * 债券
     */
    收益类型_净值(BussinessDictCodeRds.收益类型,"01","净值"),
    /**
     * 预期收益
     */
    收益类型_预期收益(BussinessDictCodeRds.收益类型,"02","预期收益"),
    /**
     * 分层类型_金额分层
     */
    分层类型_金额分层(BussinessDictCodeRds.分层类型,"01","金额分层"),
    /**
     * 分层类型_期限分层
     */
    分层类型_期限分层(BussinessDictCodeRds.分层类型,"02","期限分层"),
    /**
     * 分层类型_客户类型
     */
    分层类型_客户类型(BussinessDictCodeRds.分层类型,"03","客户类型"),

 // 一体化对应etliu start
//    /**
//     * 手工维护
//     */
//    录入方式_手工维护(BussinessDictCode.录入方式,"02","手工维护"),
//    /**
//     *外部接入
//     */
//    录入方式_外部接入(BussinessDictCode.录入方式,"01","外部接入"),
    /**
     * 手工录入
     */
    录入方式_手工录入(BussinessDictCodeRds.录入方式,"01","手工录入"),
    /**
     * 批处理
     */
    录入方式_批处理(BussinessDictCodeRds.录入方式,"04","批处理生成"),
    /**
     *接口导入
     */
    录入方式_接口导入(BussinessDictCodeRds.录入方式,"05","接口导入"),
 // 一体化对应etliu end


    /**
     * 货币
     */
    收益类型_货币(BussinessDictCodeRds.收益类型,"03","货币"),
    /**
     * 是
     */
    是否_是(BussinessDictCodeRds.是否,"Y","是"),
    /**
     * 否
     */
    是否_否(BussinessDictCodeRds.是否,"N","否"),
    /**
     * 自然人
     */
    客户类型_自然人(BussinessDictCodeRds.客户类型,"02","自然人"),
    /**
     * 企业法人
     */
    客户类型_企业法人(BussinessDictCodeRds.客户类型,"01","企业法人"),
    /**
     * 发行人
     */
    客户性质_发行人(BussinessDictCodeRds.客户性质,"01","发行人"),
    /**
     * 融资人
     */
    客户性质_融资人(BussinessDictCodeRds.客户性质,"02","融资人"),
    /**
     * 管理人
     */
    客户性质_管理人(BussinessDictCodeRds.客户性质,"03","管理人"),
    /**
     * 托管人
     */
    客户性质_托管人(BussinessDictCodeRds.客户性质,"04","托管人"),
    /**
     * 担保人
     */
    客户性质_担保人(BussinessDictCodeRds.客户性质,"05","担保人"),
    /**
     * 担保人
     */
    客户性质_承销商(BussinessDictCodeRds.客户性质,"07","承销商"),
    /**
     * 担保人
     */
    客户性质_存放行(BussinessDictCodeRds.客户性质,"08","存放行"),
    /**
     * 担保人
     */
    客户性质_投资顾问(BussinessDictCodeRds.客户性质,"09","投资顾问"),
    /**
     * 担保人
     */
    客户性质_出质人(BussinessDictCodeRds.客户性质,"06","出质人"),

    /**
     * 基金(投管用)
     */
    金融产品类型_基金(BussinessDictCodeRds.金融产品类型,"F06","基金"),
    /**
     * 计划类资产(投管用)
     */
    金融产品类型_计划类资产(BussinessDictCodeRds.金融产品类型,"F17","计划类资产"),
    /**
     * 货币基金
     */
     基金类资产类型_货币基金(BussinessDictCodeRds.基金类资产类型,"F01","货币基金"),
    /**
     * 非货币基金
     */
    基金类资产类型_非货币基金(BussinessDictCodeRds.基金类资产类型,"F02","非货币基金"),

    /**
     * 美元
     */
    币种_美元(BussinessDictCodeRds.币种,"USD","美元"),
    /**
     * 澳大利亚元
     */
    币种_澳大利亚元(BussinessDictCodeRds.币种,"AUD","澳大利亚元"),
    /**
     * 加拿大元
     */
    币种_加拿大元(BussinessDictCodeRds.币种,"CAD","加拿大元"),
    /**
     * 男
     */
    性别_男(BussinessDictCodeRds.性别,"M","男"),
    /**
     * 女
     */
    性别_女(BussinessDictCodeRds.性别,"W","女"),
    /**
     * 分红方式_现金分红
     */
     分红方式_现金分红(BussinessDictCodeRds.分红方式,"01","现金分红"),
     /**
      * 分红方式_红利再投
      */
      分红方式_红利再投(BussinessDictCodeRds.分红方式,"02","红利再投"),
      /**
       * 交易对手类型_市场机构
       */
      交易对手类型_市场机构(BussinessDictCodeRds.交易对手类型, "01", "市场机构"),
      /**
       * 交易对手类型_资管产品
       */
     交易对手类型_资管产品(BussinessDictCodeRds.交易对手类型, "02", "资管产品"),
    /**
     * 交易对手类型_分行
     */
    交易对手类型_分行(BussinessDictCodeRds.交易对手类型, "04", "分行"),
    /**
     * 交易对手类型_内部交易对手
     */
    交易对手类型_内部交易对手(BussinessDictCodeRds.交易对手类型, "05", "内部交易对手"),
    /**
     * 交易对手类型_通道计划
     */
    交易对手类型_通道计划(BussinessDictCodeRds.交易对手类型, "03", "通道计划"),
    /**
     * 报告主文件
     */
    附件类型_报告主文件(BussinessDictCodeRds.附件类型,"01","报告主文件"),
    /**
     * 静态模板
     */
    模板类型_静态模板(BussinessDictCodeRds.模板类型,"01","静态模板"),
    /**
     * 资产买入
     */
    资金流动类型_资产买入(BussinessDictCodeRds.资金流动类型,"01","资产买入"),
    /**
     * 资产卖出或到期
     */
    资金流动类型_资产卖出或到期(BussinessDictCodeRds.资金流动类型,"02","资产卖出或到期"),
    /**
     * 银行存款到期
     */
    资金流动类型_银行存款到期(BussinessDictCodeRds.资金流动类型,"04","银行存款到期"),
    /**
     * 拆出和逆回购首期
     */
    资金流动类型_拆出和逆回购首期(BussinessDictCodeRds.资金流动类型,"05","拆出和逆回购首期"),
    /**
     * 拆入和正回购首期
     */
    资金流动类型_拆入和正回购首期(BussinessDictCodeRds.资金流动类型,"06","拆入和正回购首期"),
    /**
     * 拆出和逆回购到期
     */
    资金流动类型_拆出和逆回购到期(BussinessDictCodeRds.资金流动类型,"07","拆出和逆回购到期"),
    /**
     * 拆入和正回购到期
     */
    资金流动类型_拆入和正回购到期(BussinessDictCodeRds.资金流动类型,"08","拆入和正回购到期"),
    /**
     * 投资资产所得收益
     */
    资金流动类型_投资资产所得收益(BussinessDictCodeRds.资金流动类型,"09","投资资产所得收益"),
    /**
     * 投资资产支出
     */
    资金流动类型_投资资产支出(BussinessDictCodeRds.资金流动类型,"10","投资资产支出"),
    /**
     * 其他收入
     */
    资金流动类型_其他收入(BussinessDictCodeRds.资金流动类型,"11","其他收入"),
    /**
     * 其他支出
     */
    资金流动类型_其他支出(BussinessDictCodeRds.资金流动类型,"12","其他支出"),
    /**
     * 存入银行存款
     */
    资金流动类型_存入银行存款(BussinessDictCodeRds.资金流动类型,"03","存入银行存款"),
    /**
     * 本金
     */
    现金流一级类型_本金(BussinessDictCodeRds.现金流一级类型,"01","本金"),
    /**
     * 利息
     */
    现金流一级类型_利息(BussinessDictCodeRds.现金流一级类型,"02","利息"),
    /**
     * 费用
     */
    现金流一级类型_费用(BussinessDictCodeRds.现金流一级类型,"03","费用"),
    /**
     * 税额
     */
    现金流一级类型_税额(BussinessDictCodeRds.现金流一级类型,"04","税额"),
    /**
     * 本金
     */
    现金流二级类型_本金(BussinessDictCodeRds.现金流二级类型,"C01","本金"),
    /**
     * 利息
     */
    现金流二级类型_利息(BussinessDictCodeRds.现金流二级类型,"C02","利息"),
    /**
     * 固定管理费
     */
    现金流二级类型_固定管理费(BussinessDictCodeRds.现金流二级类型,"C03","固定管理费"),
    /**
     * 托管费
     */
    现金流二级类型_托管费(BussinessDictCodeRds.现金流二级类型,"C05","托管费"),
    /**
     * 浮动管理费
     */
    现金流二级类型_浮动管理费(BussinessDictCodeRds.现金流二级类型,"C04","浮动管理费"),
    /**
     * 销售费
     */
    现金流二级类型_销售费(BussinessDictCodeRds.现金流二级类型,"C06","销售费"),
    /**
     * 税额
     */
    现金流二级类型_税额(BussinessDictCodeRds.现金流二级类型,"C07","税额"),
    /**
     * 现金流二级类型_资产推荐费
     */
    现金流二级类型_资产推荐费(BussinessDictCodeRds.现金流二级类型,"C08","资产推荐费"),
 // 一体化对应etliu start
    /**
     * 现金流二级类型_外包服务费
     */
    现金流二级类型_外包服务费(BussinessDictCodeRds.现金流二级类型,"C09","外包服务费"),
    /**
     * 现金流二级类型_代销费
     */
    现金流二级类型_代销费(BussinessDictCodeRds.现金流二级类型,"C10","代销费"),
    /**
     * 现金流二级类型_通道托管费
     */
    现金流二级类型_通道托管费(BussinessDictCodeRds.现金流二级类型,"C11","通道托管费"),
    /**
     * 现金流二级类型_通道管理费
     */
    现金流二级类型_通道管理费(BussinessDictCodeRds.现金流二级类型,"C12","通道管理费"),
    /**
     * 现金流二级类型_通道其他费
     */
    现金流二级类型_通道其他费(BussinessDictCodeRds.现金流二级类型,"C13","通道其他费"),
    /**
     * 金融产品
     */
    现金流业务类型_金融产品(BussinessDictCodeRds.现金流业务类型,"01","金融产品"),
    /**
     * 资金账户产品
     */
    现金流业务类型_资金账户产品(BussinessDictCodeRds.现金流业务类型,"02","资金账户产品"),
    /**
     * 投资组合
     */
    现金流业务类型_投资组合(BussinessDictCodeRds.现金流业务类型,"03","投资组合"),

    /**
     * 新建
     */
    指令状态_新建(BussinessDictCodeRds.指令状态, "01", "新建"),
    /**
     * 撤销
     */
    指令状态_撤销(BussinessDictCodeRds.指令状态, "02", "撤销"),
    /**
     * 金融产品
     */
    管理对象类型_金融产品(BussinessDictCodeRds.管理对象类型,"01","金融产品"),
    /**
     * 投资组合
     */
    管理对象类型_投资组合(BussinessDictCodeRds.管理对象类型,"02","投资组合"),
    /**
     * 产品交易
     */
    金融产品交易业务类型_产品交易(BussinessDictCodeRds.金融产品交易业务类型,"01","产品交易"),
    /**
     * 初始
     */
    交易状态_初始(BussinessDictCodeRds.交易状态,"01","初始"),
    /**
     * 交易状态_撤销
     */
    交易状态_撤销(BussinessDictCodeRds.交易状态,"03","撤销"),
    /**
     * 固定利率
     */
    利率类型_固定利率(BussinessDictCodeRds.利率类型,"01","固定利率"),
    /**
     * 浮动利率
     */
    利率类型_浮动利率(BussinessDictCodeRds.利率类型,"02","浮动利率"),
    /**
     * 产品成立公告（横表）
     */
    文件类型_产品成立公告_横表(BussinessDictCodeRds.文件类型,"01","产品成立公告（横表）"),
    /**
     * 产品成立公告（竖表）
     */
    文件类型_产品成立公告_竖表(BussinessDictCodeRds.文件类型,"02","产品成立公告（竖表）"),
    /**
     * 产品到期公告（横表）
     */
    文件类型_产品到期公告_横表(BussinessDictCodeRds.文件类型,"03","产品到期公告（横表）"),
    /**
     * 产品到期公告（竖表）
     */
    文件类型_产品到期公告_竖表(BussinessDictCodeRds.文件类型,"04","产品到期公告（竖表）"),
    /**
     * T+0
     */
    清算速度_T加0(BussinessDictCodeRds.清算速度,"01","T+0"),
    /**
     * T+1
     */
    清算速度_T加1(BussinessDictCodeRds.清算速度,"02","T+1"),
    /**
     * 发行状态_发行成功
     */
    发行状态_发行成功(BussinessDictCodeRds.发行状态,"01","发行成功"),
    /**
    * 债券类资产买入
    */
    平安业务类型_债券类资产买入(BussinessDictCodeRds.平安业务类型,"09","债券类资产买入"),
    /**
    * 债券类资产卖出
    */
    平安业务类型_债券类资产卖出(BussinessDictCodeRds.平安业务类型,"10","债券类资产卖出"),
    /**
    * 债券类资产行权
    */
    平安业务类型_债券类资产行权(BussinessDictCodeRds.平安业务类型,"11","债券类资产行权"),
    /**
    * 普通拆入
    */
    平安业务类型_普通拆入(BussinessDictCodeRds.平安业务类型,"14","普通拆入"),
    /**
    * 特拆拆入
    */
    平安业务类型_特拆拆入(BussinessDictCodeRds.平安业务类型,"15","特拆拆入"),
    /**
    * 普通拆出
    */
    平安业务类型_普通拆出(BussinessDictCodeRds.平安业务类型,"16","普通拆出"),
    /**
     * 平安业务类型_逆回购
     */
    平安业务类型_逆回购(BussinessDictCodeRds.平安业务类型,"19","逆回购"),
    /**
     * 平安业务类型_正回购
     */
    平安业务类型_正回购(BussinessDictCodeRds.平安业务类型,"20","正回购"),
    /**
     * 计划类资产（利率型）申购
     */
    平安业务类型_计划类资产_利率型_申购(BussinessDictCodeRds.平安业务类型,"33","计划类资产（利率型）申购"),
    /**
     * 计划类资产（利率型）赎回
     */
    平安业务类型_计划类资产_利率型_赎回(BussinessDictCodeRds.平安业务类型,"34","计划类资产（利率型）赎回"),
    /**
     * 债券中间收息
     */
    平安业务类型_债券中间收息(BussinessDictCodeRds.平安业务类型,"35","债券中间收息"),
    /**
     * 债券中间还本
     */
    平安业务类型_债券中间还本(BussinessDictCodeRds.平安业务类型,"36","债券中间还本"),
    /**
     * 债券到期
     */
    平安业务类型_债券到期(BussinessDictCodeRds.平安业务类型,"37","债券到期"),
    /**
     * 存款中间还本
     */
    平安业务类型_存款中间还本(BussinessDictCodeRds.平安业务类型,"38","存款中间还本"),
    /**
     * 存款中间付息
     */
    平安业务类型_存款中间付息(BussinessDictCodeRds.平安业务类型,"39","存款中间付息"),
    /**
     * 存款到期还本
     */
    平安业务类型_存款到期还本(BussinessDictCodeRds.平安业务类型,"40","存款到期还本"),
    /**
     * 存款提前支取
     */
    平安业务类型_存款提前支取(BussinessDictCodeRds.平安业务类型,"41","存款提前支取"),
    /**
     * 普通拆入到期
     */
      平安业务类型_普通拆入到期(BussinessDictCodeRds.平安业务类型,"42","普通拆入到期"),
    /**
     * 特拆拆入到期
     */
      平安业务类型_特拆拆入到期(BussinessDictCodeRds.平安业务类型,"43","特拆拆入到期"),
    /**
     * 普通拆出到期
     */
      平安业务类型_普通拆出到期(BussinessDictCodeRds.平安业务类型,"44","普通拆出到期"),
    /**
     * 项目类资产中间付息
     */
    平安业务类型_项目类资产中间付息(BussinessDictCodeRds.平安业务类型,"45","项目类资产中间付息"),
    /**
     * 项目类资产中间还本
     */
    平安业务类型_项目类资产中间还本(BussinessDictCodeRds.平安业务类型,"46","项目类资产中间还本"),
    /**
     * 项目类资产到期
     */
    平安业务类型_项目类资产到期(BussinessDictCodeRds.平安业务类型,"47","项目类资产到期"),
    /**
     * 平安业务类型_计划类资产_利率型_中间付息
     */
    平安业务类型_计划类资产_利率型_中间付息(BussinessDictCodeRds.平安业务类型,"48","计划类资产（利率型）中间付息"),
    /**
     * 平安业务类型_计划类资产_利率型_中间还本
     */
    平安业务类型_计划类资产_利率型_中间还本(BussinessDictCodeRds.平安业务类型,"49","计划类资产（利率型）中间还本"),
    /**
     * 平安业务类型_计划类资产_利率型_到期
     */
    平安业务类型_计划类资产_利率型_到期(BussinessDictCodeRds.平安业务类型,"50","计划类资产（利率型）到期"),
    /**
     * 平安业务类型_逆回购到期
     */
    平安业务类型_逆回购到期(BussinessDictCodeRds.平安业务类型,"51","逆回购到期"),
    /**
     * 平安业务类型_正回购到期
     */
    平安业务类型_正回购到期(BussinessDictCodeRds.平安业务类型,"52","正回购到期"),

    /**
     * 平安业务类型_货币基金红利再投
     */
    平安业务类型_货币基金红利再投(BussinessDictCodeRds.平安业务类型,"53","货币基金红利再投"),
    /**
     * 平安业务类型_货币基金现金分红
     */
    平安业务类型_货币基金现金分红(BussinessDictCodeRds.平安业务类型,"54","货币基金现金分红"),
    /**
     * 平安业务类型_非货币基金红利再投
     */
    平安业务类型_非货币基金红利再投(BussinessDictCodeRds.平安业务类型,"55","非货币基金红利再投"),
    /**
     * 平安业务类型_非货币基金现金分红
     */
    平安业务类型_非货币基金现金分红(BussinessDictCodeRds.平安业务类型,"56","非货币基金现金分红"),
    /**
     * 平安业务类型_非货币基金分红除权
     */
    平安业务类型_非货币基金分红除权(BussinessDictCodeRds.平安业务类型,"57","非货币基金分红除权"),
    /**
     * 平安业务类型_计划类资产_净值型_现金分红
     */
    平安业务类型_计划类资产_净值型_现金分红(BussinessDictCodeRds.平安业务类型,"58","计划类资产（净值型）现金分红"),
    /**
     * 平安业务类型_计划类资产_净值型_红利再投
     */
    平安业务类型_计划类资产_净值型_红利再投(BussinessDictCodeRds.平安业务类型,"59","计划类资产（净值型）红利再投"),
    /**
     * 平安业务类型_计划类资产_净值型_分红除权
     */
    平安业务类型_计划类资产_净值型_分红除权(BussinessDictCodeRds.平安业务类型,"60","计划类资产（净值型）分红除权"),
    /**
     * 平安业务类型_活期结息
     */
    平安业务类型_活期结息(BussinessDictCodeRds.平安业务类型,"61","活期结息"),
    /**
     * 平安业务类型_项目类资产_中间付息
     */
    平安业务类型_项目类资产_中间付息(BussinessDictCodeRds.平安业务类型,"45","项目类资产中间付息"),
    /**
     * 平安业务类型_项目类资产_中间还本
     */
    平安业务类型_项目类资产_中间还本(BussinessDictCodeRds.平安业务类型,"46","项目类资产中间还本"),
    /**
     * 平安业务类型_项目类资产_到期
     */
    平安业务类型_项目类资产_到期(BussinessDictCodeRds.平安业务类型,"47","项目类资产到期"),
    /**
     * 平安业务类型_自定义现金流收取
     */
    平安业务类型_自定义现金流收取(BussinessDictCodeRds.平安业务类型,"62","自定义现金流收取"),
    /**
     * 平安业务类型_自定义现金流支付
     */
    平安业务类型_自定义现金流支付(BussinessDictCodeRds.平安业务类型,"63","自定义现金流支付"),
    /**
     *平安业务类型_证银转账
     */
    平安业务类型_证银转账(BussinessDictCodeRds.平安业务类型,"64","资金划拨证银转账"),
    /**
     *平安业务类型_银证转账
     */
    平安业务类型_银证转账(BussinessDictCodeRds.平安业务类型,"65","资金划拨银证转账"),
    /**
     * 平安业务类型_货币基金赎回到账
     */
    平安业务类型_货币基金赎回到账(BussinessDictCodeRds.平安业务类型,"66","货币基金赎回到账"),
    /**
     * 平安业务类型_非货币基金赎回到账
     */
    平安业务类型_非货币基金赎回到账(BussinessDictCodeRds.平安业务类型,"67","非货币基金赎回到账"),
    /**
     * 平安业务类型_计划类_净值型_赎回到账
     */
    平安业务类型_计划类_净值型_赎回到账(BussinessDictCodeRds.平安业务类型,"68","计划类（净值型）赎回到账"),
    /**
     * 平安业务类型_货币基金分红除权
     */
    平安业务类型_货币基金分红除权(BussinessDictCodeRds.平安业务类型, "82", "货币基金分红除权"),
    /**
     * 平安业务类型_计划类货币型红利再投
     */
    平安业务类型_计划类_货币型_红利再投(BussinessDictCodeRds.平安业务类型, "78", "计划类资产（货币型）红利再投"),
    /**
     * 平安业务类型_计划类货币型除权登记
     */
    平安业务类型_计划类_货币型_除权登记(BussinessDictCodeRds.平安业务类型, "79", "计划类资产（货币型）除权登记"),
    /**
     * 平安业务类型_计划类货币型现金分红
     */
    平安业务类型_计划类_货币型_现金分红(BussinessDictCodeRds.平安业务类型, "80", "计划类资产（货币型）现金分红"),
    /**
     * 平安业务类型_DVP划款
     */
    平安业务类型_DVP划款(BussinessDictCodeRds.平安业务类型,"81","DVP划款"),
    /**
     * 平安业务类型_项目类资产卖出到账
     */
    平安业务类型_项目类资产卖出到账(BussinessDictCodeRds.平安业务类型,"83","项目类资产卖出到账"),
    /**
     * 平安业务类型_产品成立
     */
    平安业务类型_产品成立(BussinessDictCodeRds.平安业务类型,"01","产品成立"),
    /**
     * 平安业务类型_产品强增
     */
    平安业务类型_产品强增(BussinessDictCodeRds.平安业务类型,"02","产品强增"),
    /**
     * 平安业务类型_产品强减
     */
    平安业务类型_产品强减(BussinessDictCodeRds.平安业务类型,"03","产品强减"),
    /**
     * 平安业务类型_产品申购
     */
    平安业务类型_产品申购(BussinessDictCodeRds.平安业务类型,"04","产品申购"),
    /**
     * 平安业务类型_产品赎回
     */
    平安业务类型_产品赎回(BussinessDictCodeRds.平安业务类型,"05","产品赎回"),
    /**
     * 平安业务类型_产品中间付息
     */
    平安业务类型_产品中间付息(BussinessDictCodeRds.平安业务类型,"06","产品中间付息"),
    /**
     * 平安业务类型_产品到期
     */
    平安业务类型_产品到期(BussinessDictCodeRds.平安业务类型,"07","产品到期"),
    /**
     * 平安业务类型_产品费用支付
     */
    平安业务类型_产品费用支付(BussinessDictCodeRds.平安业务类型,"08","产品费用支付"),
    /**
     * 平安业务类型_存款类资产买入
     */
    平安业务类型_存款类资产买入(BussinessDictCodeRds.平安业务类型,"12","存款类资产买入"),
    /**
     * 平安业务类型_存款类资产买入
     */
    平安业务类型_存款类资产卖出(BussinessDictCodeRds.平安业务类型,"13","存款类资产卖出"),
    /**
     * 平安业务类型_项目类资产买入
     */
    平安业务类型_项目类资产买入(BussinessDictCodeRds.平安业务类型,"17","项目类资产买入"),
    /**
     * 平安业务类型_项目类资产买入
     */
    平安业务类型_项目类资产卖出(BussinessDictCodeRds.平安业务类型,"18","项目类资产卖出"),
    /**
     * 货币基金申购申请
     */
    平安业务类型_货币基金申购申请(BussinessDictCodeRds.平安业务类型,"21","货币基金申购申请"),
    /**
     * 货币基金申购确认
     */
    平安业务类型_货币基金申购确认(BussinessDictCodeRds.平安业务类型,"22","货币基金申购确认"),
    /**
     * 货币基金赎回申请
     */
    平安业务类型_货币基金赎回申请(BussinessDictCodeRds.平安业务类型,"23","货币基金赎回申请"),
    /**
     * 货币基金赎回确认
     */
    平安业务类型_货币基金赎回确认(BussinessDictCodeRds.平安业务类型,"24","货币基金赎回确认"),
    /**
     * 非货币基金申购申请
     */
    平安业务类型_非货币基金申购申请(BussinessDictCodeRds.平安业务类型,"25","非货币基金申购申请"),
    /**
     * 非货币基金申购确认
     */
    平安业务类型_非货币基金申购确认(BussinessDictCodeRds.平安业务类型,"26","非货币基金申购确认"),
    /**
     * 非货币基金赎回申请
     */
    平安业务类型_非货币基金赎回申请(BussinessDictCodeRds.平安业务类型,"27","非货币基金赎回申请"),
    /**
     * 非货币基金赎回确认
     */
    平安业务类型_非货币基金赎回确认(BussinessDictCodeRds.平安业务类型,"28","非货币基金赎回确认"),
    /**
     * 计划类资产（净值型）申购申请
     */
    平安业务类型_计划类资产净值型_申购申请(BussinessDictCodeRds.平安业务类型,"29","计划类资产（净值型）申购申请"),
    /**
     * 计划类资产（净值型）申购确认
     */
    平安业务类型_计划类资产净值型_申购确认(BussinessDictCodeRds.平安业务类型,"30","计划类资产（净值型）申购确认"),
    /**
     * 计划类资产（净值型）赎回申请
     */
    平安业务类型_计划类资产净值型_赎回申请(BussinessDictCodeRds.平安业务类型,"31","计划类资产（净值型）赎回申请"),
    /**
     * 计划类资产（净值型）赎回确认
     */
    平安业务类型_计划类资产净值型_赎回确认(BussinessDictCodeRds.平安业务类型,"32","计划类资产（净值型）赎回确认"),
    /**
     * 计划类资产（货币型）申购申请
     */
    平安业务类型_计划类资产货币型_申购申请(BussinessDictCodeRds.平安业务类型,"69","计划类资产（货币型）申购申请"),
    /**
     * 计划类资产（货币型）申购确认
     */
    平安业务类型_计划类资产货币型_申购确认(BussinessDictCodeRds.平安业务类型,"70","计划类资产（货币型）申购确认"),
    /**
     * 计划类资产（货币型）赎回申请
     */
    平安业务类型_计划类资产货币型_赎回申请(BussinessDictCodeRds.平安业务类型,"71","计划类资产（货币型）赎回申请"),
    /**
     * 计划类资产（货币型）赎回确认
     */
    平安业务类型_计划类资产货币型_赎回确认(BussinessDictCodeRds.平安业务类型,"72","计划类资产（货币型）赎回确认"),
    /**
     * 计划类资产（货币型）赎回到账
     */
    平安业务类型_计划类资产货币型_赎回到账(BussinessDictCodeRds.平安业务类型,"73","计划类资产（货币型）赎回确认"),
    /**
     * 金融产品类型_系列模板
     */
    金融产品类型_系列模板(BussinessDictCodeRds.金融产品类型,"F19","系列模板"),
    /**
     * 本金收益特征_保本
     */
    本金收益特征_保本(BussinessDictCodeRds.本金收益特征,"01","保本"),
    /**
     * 本金收益特征_非保本
     */
    本金收益特征_非保本(BussinessDictCodeRds.本金收益特征,"02","非保本"),
    /**
     * 付息周期_周期
     */
    付息周期_周期(BussinessDictCodeRds.付息周期,"02","周期支付"),
    /**
     * 付息周期_到期
     */
    付息周期_到期(BussinessDictCodeRds.付息周期,"01","到期支付"),

 // 一体化对应etliu start
//    /**
//     * 周期单位_日
//     */
//    周期单位_日(BussinessDictCode.周期单位,"07","日"),
//    /**
//     * 周期单位_月
//     */
//    周期单位_月(BussinessDictCode.周期单位,"05","月"),
//    /**
//     * 周期单位_年
//     */
//    周期单位_年(BussinessDictCode.周期单位,"02","年"),
    /**
     * 周期单位_日
     */
    周期单位_日(BussinessDictCodeRds.周期单位,"01","日"),
    /**
     * 周期单位_月
     */
    周期单位_月(BussinessDictCodeRds.周期单位,"03","月"),
    /**
     * 周期单位_年
     */
    周期单位_年(BussinessDictCodeRds.周期单位,"05","年"),
    /**
     * 画面数据状态_待处理
     */
    画面数据状态_待处理(BussinessDictCodeRds.画面数据状态,"01","待处理"),
    /**
     * 画面数据状态_新建
     */
    画面数据状态_新建(BussinessDictCodeRds.画面数据状态,"02","新建"),
    /**
     * 画面数据状态_流程中
     */
    画面数据状态_流程中(BussinessDictCodeRds.画面数据状态,"03","流程中"),
    /**
     * 画面数据状态_修改
     */
    画面数据状态_修改(BussinessDictCodeRds.画面数据状态,"04","修改"),
    /**
     * 画面数据状态_有效
     */
    画面数据状态_有效(BussinessDictCodeRds.画面数据状态,"05","有效"),
    /**
     * 画面数据状态_撤销
     */
    画面数据状态_撤销(BussinessDictCodeRds.画面数据状态,"06","撤销"),

    /**
     * 交易流程状态_流程中
     */
    交易流程状态_流程中(BussinessDictCodeRds.交易流程状态,"01","流程中"),

    /**
     * 交易流程状态_流程中
     */
    交易流程状态_有效(BussinessDictCodeRds.交易流程状态,"02","有效"),

    /**
     * 交易流程状态_撤销
     */
    交易流程状态_撤销(BussinessDictCodeRds.交易流程状态,"03","撤销"),

    /**
     * 有效状态_有效
     */
    有效状态_有效(BussinessDictCodeRds.有效状态,"01","有效"),
    /**
     * 有效状态_待处理
     */
    有效状态_待处理(BussinessDictCodeRds.有效状态,"02","待处理"),
    /**
     * 有效状态_撤销
     */
    有效状态_撤销(BussinessDictCodeRds.有效状态,"03","撤销"),
    /**
     * 同业借款
     */
    存款类资产类型_同业借款(BussinessDictCodeRds.存款类资产类型,"D01","同业借款"),
    /**
     * 存放同业
     */
    存款类资产类型_存放同业(BussinessDictCodeRds.存款类资产类型,"D02","存放同业"),
    /**
     * 开放模式_定期开放
     */
    开放模式_定期开放(BussinessDictCodeRds.开放模式,"01","定期开放"),
    /**
     * 开放模式_高频开放
     */
    开放模式_高频开放(BussinessDictCodeRds.开放模式,"02","高频开放"),

    /**
     * 开放模式_周期开放
     */
    开放模式_周期开放(BussinessDictCodeRds.开放模式,"03","周期开放"),
    /**
     * 交易平台_银行间
     */
    交易平台_银行间(BussinessDictCodeRds.交易平台,"01","银行间"),
    /**
     * 交易平台_场内交易
     */
    交易平台_场内交易(BussinessDictCodeRds.交易平台,"02","场内交易"),
    /**
     * 交易平台_其它
     */
    交易平台_其它(BussinessDictCodeRds.交易平台,"99","其它"),
    /**
     * 资金账户类型_托管账户
     */
    资金账户类型_托管账户(BussinessDictCodeRds.资金账户类型,"A01","托管账户"),
    /**
     * 资金账户类型_销售费账户
     */
    资金账户类型_销售费账户(BussinessDictCodeRds.资金账户类型,"A02","销售费账户"),
    /**
     * 资金账户类型_托管费账户
     */
    资金账户类型_托管费账户(BussinessDictCodeRds.资金账户类型,"A03","托管费账户"),
    /**
     * 资金账户类型_管理费账户
     */
    资金账户类型_管理费账户(BussinessDictCodeRds.资金账户类型,"A04","管理费账户"),
    /**
     * 资金账户类型_交易对手账户
     */
    资金账户类型_交易对手账户(BussinessDictCodeRds.资金账户类型,"A05","交易对手账户"),
    /**
     * 资金账户类型_通道方账户
     */
    资金账户类型_通道方账户(BussinessDictCodeRds.资金账户类型,"A06","通道方账户"),
    /**
     * 资金账户类型_交易所资金账户
     */
    资金账户类型_交易所资金账户(BussinessDictCodeRds.资金账户类型, "A07", "交易所资金账户"),
    /**
     * 资金账户类型_甲类户
     */
    资金账户类型_甲类户(BussinessDictCodeRds.资金账户类型, "A08", "甲类户"),
    /**
     * 资金账户类型_上清所DVP账户
     */
    资金账户类型_上清所DVP账户(BussinessDictCodeRds.资金账户类型,"A09","上清所DVP账户"),
    /**
     * 资金账户类型_中债登DVP账户
     */
    资金账户类型_中债登DVP账户(BussinessDictCodeRds.资金账户类型,"A10","中债登DVP账户"),
    /**
     * 资金账户类型_赎回过渡户
     */
    资金账户类型_赎回过渡户(BussinessDictCodeRds.资金账户类型,"A11","赎回过渡户"),
    /**
     * 收入
     */
    收支方向_收入(BussinessDictCodeRds.收支方向,"01","收入"),
    /**
     * 支出
     */
    收支方向_支出(BussinessDictCodeRds.收支方向,"02","支出"),

    /**
     * 收益结转频率_按日结转
     */
    收益结转频率_按日结转(BussinessDictCodeRds.收益结转频率,"01","按日结转"),
    /**
     * 收益结转频率_按月结转
     */
    收益结转频率_按月结转(BussinessDictCodeRds.收益结转频率,"02","按月结转"),
    /**
     * 计息基础_A_360
     */
    计息基础_A_360(BussinessDictCodeRds.计息基础,"A/360","A/360"),
    /**
     * 计息基础_A_365
     */
    计息基础_A_365(BussinessDictCodeRds.计息基础,"A/365","A/365"),
    /**
     * 计息基础_A_A
     */
    计息基础_A_A(BussinessDictCodeRds.计息基础,"A/A","A/A"),
    /**
     * 计息基础_A_A_B_COUP
     */
    计息基础_A_A_B_COUP(BussinessDictCodeRds.计息基础,"A/A_B_COUP","A/A(平均分配)"),
    /**
     * 计息基础_A_A_B_DAY
     */
    计息基础_A_A_B_DAY(BussinessDictCodeRds.计息基础,"A/A_B_DAY","A/A(按实际天数分配)"),
    /**
     * 计息基础_30_360
     */
    计息基础_30_360(BussinessDictCodeRds.计息基础,"30/360","30/360"),
    /**
     * 计息基础_A_365F
     */
    计息基础_A_365F(BussinessDictCodeRds.计息基础,"A/365F","A/365F"),
    /**
     * 中债_基金类二级资产类别_货币市场基金
     */
    中债_基金类二级资产类别_货币市场基金(BussinessDictCodeRds.中债_基金类二级资产类别,"2401","货币市场基金"),
    /**
     * 中债_基金类二级资产类别_债券基金
     */
    中债_基金类二级资产类别_债券基金(BussinessDictCodeRds.中债_基金类二级资产类别,"1106","债券基金"),
    /**
     * 中债_基金类二级资产类别_股票基金
     */
    中债_基金类二级资产类别_股票基金(BussinessDictCodeRds.中债_基金类二级资产类别,"1303","股票基金"),
    /**
     * 中债_基金类二级资产类别_基金中基金
     */
    中债_基金类二级资产类别_基金中基金(BussinessDictCodeRds.中债_基金类二级资产类别,"2403","基金中基金"),
    /**
     * 中债_基金类二级资产类别_混合基金
     */
    中债_基金类二级资产类别_混合基金(BussinessDictCodeRds.中债_基金类二级资产类别,"2402","混合基金"),
    /**
     * 中债_基金类二级资产类别_其他公募基金
     */
    中债_基金类二级资产类别_其他公募基金(BussinessDictCodeRds.中债_基金类二级资产类别,"2499","其他公募基金"),
    //    中债字段对应 20190416 code值变化 start
    /**
     * 基金类资产二级类型_货币市场基金
     */
    基金类资产二级类型_货币市场基金(BussinessDictCodeRds.基金类资产二级类型,"02","货币市场基金"),
    /**
     * 基金类资产二级类型_债券基金
     */
    基金类资产二级类型_债券基金(BussinessDictCodeRds.基金类资产二级类型,"01","债券基金"),
    /**
     * 基金类资产二级类型_股票基金
     */
    基金类资产二级类型_股票基金(BussinessDictCodeRds.基金类资产二级类型,"03","股票基金"),
    /**
     * 基金类资产二级类型_混合基金
     */
    基金类资产二级类型_混合基金(BussinessDictCodeRds.基金类资产二级类型,"04","混合基金"),
    /**
     * 基金类资产二级类型_保本基金
     */
    基金类资产二级类型_保本基金(BussinessDictCodeRds.基金类资产二级类型,"05","保本基金"),
    /**
     * 基金类资产二级类型_其他公募基金
     */
    基金类资产二级类型_其他公募基金(BussinessDictCodeRds.基金类资产二级类型,"06","其他公募基金"),
    /**
     * 基金类资产二级类型_基金中基金
     */
    基金类资产二级类型_基金中基金(BussinessDictCodeRds.基金类资产二级类型,"07","基金中基金"),

    开放类型_开放式(BussinessDictCodeRds.开放类型,"02","开放式"),

    开放类型_封闭式(BussinessDictCodeRds.开放类型,"01","封闭式"),

    /**
     * 规律开放周期_其他规律开放周期
     */
    规律开放周期_其他规律开放周期(BussinessDictCodeRds.规律开放周期,"99","其他规律开放周期"),

    /**
     * 开放期业务_其他
     */
    开放期业务_其他(BussinessDictCodeRds.开放期业务,"99","其他"),

    /**
     * 处理模式_普通
     */
    处理模式_普通(BussinessDictCodeRds.处理模式,"01","普通"),

    /**
     * 处理模式_滚动
     */
    处理模式_滚动(BussinessDictCodeRds.处理模式,"02","滚动"),

    /**
     * 处理模式_滚动N天
     */
    处理模式_滚动N天(BussinessDictCodeRds.处理模式,"03","滚动N天"),

    /**
     * 处理模式_普通N天
     */
    处理模式_普通N天(BussinessDictCodeRds.处理模式,"04","普通N天"),

    /**
     * 处理模式_开放阶梯
     */
    处理模式_开放阶梯(BussinessDictCodeRds.处理模式,"05","开放阶梯"),

    /**
     * 处理模式_日添利
     */
    处理模式_日添利(BussinessDictCodeRds.处理模式,"06","日添利"),

    /**
     * 处理模式_智能日添利
     */
    处理模式_智能日添利(BussinessDictCodeRds.处理模式,"07","智能日添利"),

    /**
     * 处理模式_货币型
     */
    处理模式_货币型(BussinessDictCodeRds.处理模式,"08","货币型"),

    /**
     * 处理模式_净值型
     */
    处理模式_净值型(BussinessDictCodeRds.处理模式,"09","净值型"),

    /**
     * 周期单位_周
     */
    周期单位_周(BussinessDictCodeRds.周期单位,"02","周"),

    /**
     * 发行状态_确认发行
     */
    发行状态_确认发行(BussinessDictCodeRds.发行状态,"03","确认发行"),

    /**
     * 息票品种_附息
     */
    息票品种_附息(BussinessDictCodeRds.息票品种,"01","附息"),

    /**
     * 息票品种_贴现
     */
    息票品种_贴现(BussinessDictCodeRds.息票品种,"02","贴现"),

    /**
     * 息票品种_零息
     */
    息票品种_零息(BussinessDictCodeRds.息票品种,"03","零息"),

    /**
     * 息票品种_利随本清
     */
    息票品种_利随本清(BussinessDictCodeRds.息票品种,"04","利随本清"),
    /**
     * 日期类型_自然日
     */
    日期类型_自然日(BussinessDictCodeRds.日期类型,"01","自然日"),
    /**
     * 日期类型_交易日
     */
    日期类型_交易日(BussinessDictCodeRds.日期类型,"02","交易日"),

    /**
     * 申赎开放类型_申赎开放
     */
    申赎开放类型_申赎开放(BussinessDictCodeRds.申赎开放类型,"03","申赎开放"),
    /**
     * 申赎开放类型_申购开放
     */
    申赎开放类型_申购开放(BussinessDictCodeRds.申赎开放类型,"01","申购开放"),
    /**
     * 现金流单位_百元法
     */
    现金流单位_百元法(BussinessDictCodeRds.现金流单位,"01","百元法"),
    /**
     * 现金流单位_总额法
     */
    现金流单位_总额法(BussinessDictCodeRds.现金流单位,"02","总额法"),

    /**
     * 回购拆借方向_拆入
     */
    回购拆借方向_拆入(BussinessDictCodeRds.回购拆借方向, "01", "拆入"),
    /**
     * 回购拆借方向_拆出
     */
    回购拆借方向_拆出(BussinessDictCodeRds.回购拆借方向, "02", "拆出"),
    /**
     * 回购拆借方向_正回购
     */
    回购拆借方向_正回购(BussinessDictCodeRds.回购拆借方向, "03", "正回购"),
    /**
     * 回购拆借方向_逆回购
     */
    回购拆借方向_逆回购(BussinessDictCodeRds.回购拆借方向, "04", "逆回购"),
    /**
     * 角色岗位_领导
     */
    角色岗位_领导(BussinessDictCodeRds.角色岗位, "01", "领导"),
    /**
     * 角色岗位_投资处长
     */
    角色岗位_投资处长(BussinessDictCodeRds.角色岗位, "02", "投资处长"),
    /**
     * 角色岗位_交易处长
     */
    角色岗位_交易处长(BussinessDictCodeRds.角色岗位, "03", "交易处长"),
    /**
     * 角色岗位_交易员
     */
    角色岗位_交易员(BussinessDictCodeRds.角色岗位, "06", "交易员"),
    /**
     * 角色岗位_清算岗
     */
    角色岗位_清算岗(BussinessDictCodeRds.角色岗位, "09","清算岗"),
    /**
     * 角色岗位_交易员_资本境外岗
     */
    角色岗位_交易员岗_资本境外(BussinessDictCodeRds.角色岗位, "27", "交易员(资本境外)岗"),
    /**
     * 角色岗位_交易员_资本境内岗
     */
    角色岗位_交易员岗_资本境内(BussinessDictCodeRds.角色岗位, "28", "交易员(资本境内)岗"),
    /**
     * 角色岗位_投资经理
     */
    角色岗位_投资经理(BussinessDictCodeRds.角色岗位, "05", "投资经理"),
    /**
     * 角色岗位_项目经理岗
     */
    角色岗位_项目经理岗(BussinessDictCodeRds.角色岗位, "12", "项目经理岗"),
    /**
     * 排期发行方式_集合
     */
    排期发行方式_集合(BussinessDictCodeRds.排期发行方式, "01", "集合"),
    /**
     * 排期发行方式_单一
     */
    排期发行方式_单一(BussinessDictCodeRds.排期发行方式, "02", "单一"),
    /**
     * 产品收益标识_非保本浮动收益
     */
    产品收益标识_非保本浮动收益(BussinessDictCodeRds.产品收益标识, "03", "非保本浮动收益"),
    /**
     * 募集方式_公募
     */
    募集方式_公募(BussinessDictCodeRds.募集方式, "01", "公募"),
    /**
     * 投资性质_固定收益类
     */
    投资性质_固定收益类(BussinessDictCodeRds.投资性质, "01", "固定收益类"),
    /**
     * 产品风险等级_一级
     */
    产品风险等级_一级(BussinessDictCodeRds.产品风险等级, "01", "一级（低）"),
    /**
     * 期限品种_产品端_T_PLUS_0
     */
    期限品种_产品端_T_PLUS_0(BussinessDictCodeRds.期限品种_产品端, "01", "T+0"),
    /**
     * 期限品种_产品端_7天含以内
     */
    期限品种_产品端_7天含以内(BussinessDictCodeRds.期限品种_产品端, "02", "7 天（含）以内"),
    /**
     * 期限品种_产品端_7天至1个月含
     */
    期限品种_产品端_7天至1个月含(BussinessDictCodeRds.期限品种_产品端, "03", "7 天-1 个月（含）"),
    /**
     * 期限品种_产品端_1至3个月含
     */
    期限品种_产品端_1至3个月含(BussinessDictCodeRds.期限品种_产品端, "04", "1-3 个月（含）"),
    /**
     * 期限品种_产品端_3至6个月含
     */
    期限品种_产品端_3至6个月含(BussinessDictCodeRds.期限品种_产品端, "05", "3-6个月(含)"),
    /**
     * 期限品种_产品端_6至12个月含
     */
    期限品种_产品端_6至12个月含(BussinessDictCodeRds.期限品种_产品端, "06", "6-12 个月（含）"),
    /**
     * 期限品种_产品端_1年以上
     */
    期限品种_产品端_1年以上(BussinessDictCodeRds.期限品种_产品端, "07", "1 年以上"),
    /**
     * 开放状态_开放
     */
    开放状态_开放(BussinessDictCodeRds.开放状态,"01","开放"),
    /**
     * 项目类一级分类_非标债权
     */
    项目类一级分类_非标债权(BussinessDictCodeRds.项目类一级分类,"P01","非标债权"),
    /**
     * 项目类一级分类_结构化证券
     */
    项目类一级分类_结构化证券(BussinessDictCodeRds.项目类一级分类,"P02","结构化证券"),
    /**
     * 项目类一级分类_其他权益类投资
     */
    项目类一级分类_其他权益类投资(BussinessDictCodeRds.项目类一级分类,"P03","其他权益类投资"),
    /**
     * 项目类一级分类_其他产业投资基金
     */
    项目类一级分类_其他产业投资基金(BussinessDictCodeRds.项目类一级分类,"P04","其他产业投资基金"),
    /**
     * 项目类一级分类_股票定向增发投资
     */
    项目类一级分类_股票定向增发投资(BussinessDictCodeRds.项目类一级分类,"P05","股票定向增发投资"),
    /**
     * 项目类一级分类_基础设施基金
     */
    项目类一级分类_基础设施基金(BussinessDictCodeRds.项目类一级分类,"P06","基础设施基金"),
    /**
     * 项目类一级分类_券商债权收益权投资
     */
    项目类一级分类_券商债权收益权投资(BussinessDictCodeRds.项目类一级分类,"P07","券商债权收益权投资"),
    /**
     * 项目类一级分类_信贷资产流转项目
     */
    项目类一级分类_信贷资产流转项目(BussinessDictCodeRds.项目类一级分类,"P08","信贷资产流转项目"),
    /**
     * 项目类一级分类_券商收益凭证
     */
    项目类一级分类_券商收益凭证(BussinessDictCodeRds.项目类一级分类,"P09","券商收益凭证"),
    /**
     * 项目类一级分类_股票质押式回购
     */
    项目类一级分类_股票质押式回购(BussinessDictCodeRds.项目类一级分类,"P10","股票质押式回购"),
    /**
     * 项目类一级分类_理财直接融资工具
     */
    项目类一级分类_理财直接融资工具(BussinessDictCodeRds.项目类一级分类,"P11","理财直接融资工具"),
    /**
     * 项目类一级分类_保险资产管理计划
     */
    项目类一级分类_保险资产管理计划(BussinessDictCodeRds.项目类一级分类,"P12","保险资产管理计划"),
    /**
     *  分类维度_收益类型
     */
    分类维度_收益类型(BussinessDictCodeRds.分类维度,"01","收益类型"),
    /**
     *  分类维度_开放类型
     */
    分类维度_开放类型(BussinessDictCodeRds.分类维度,"02","开放类型"),
    /**
     *  分类维度_收益类型
     */
    分类维度_募集方式(BussinessDictCodeRds.分类维度,"03","募集方式"),
    /**
     *  分类维度_投资性质
     */
    分类维度_投资性质(BussinessDictCodeRds.分类维度,"04","投资性质"),
    /**
     *  分类维度_开放周期
     */
    分类维度_开放周期(BussinessDictCodeRds.分类维度,"05","开放周期"),
    /**
     *  分类维度_是否面向单一投资者
     */
    分类维度_是否面向单一投资者(BussinessDictCodeRds.分类维度,"06","是否面向单一投资者"),
    /**
     *  分类维度_说明书是否披露风险标志
     */
    分类维度_说明书是否披露风险标志(BussinessDictCodeRds.分类维度,"07","说明书是否披露风险标志"),
    /**
     *  使用状态_启用
     */
    使用状态_启用(BussinessDictCodeRds.使用状态,"01","启用"),
    /**
     *  使用状态_停用
     */
    使用状态_停用(BussinessDictCodeRds.使用状态,"02","停用"),
    /**
     *  一次性费用类型_交易手续费
     */
    一次性费用类型_交易手续费(BussinessDictCodeRds.一次性费用类型,"02","交易手续费"),
    /**
     *  一次性费用类型_资产推荐费
     */
    一次性费用类型_资产推荐费(BussinessDictCodeRds.一次性费用类型,"04","资产推荐费"),
    /**
     * 一次性费用类型_税费
     */
    一次性费用类型_税费(BussinessDictCodeRds.一次性费用类型,"05","税费"),
    /**
     * 一次性费用类型_通道费
     */
    一次性费用类型_通道费(BussinessDictCodeRds.一次性费用类型,"03", "通道费"),
    /**
     * 平安资产类型_债券
     */
    平安资产类型_债券(BussinessDictCodeRds.平安资产类型,"F01","债券"),
    /**
     * 平安资产类型_基金
     */
    平安资产类型_基金(BussinessDictCodeRds.平安资产类型,"F02","基金"),
    /**
     * 平安资产类型_计划类资产(利率型)
     */
    平安资产类型_计划类资产_利率型(BussinessDictCodeRds.平安资产类型,"F03","计划类资产_利率型"),
    /**
     * 平安资产类型_计划类资产(净值型)
     */
    平安资产类型_计划类资产_净值型(BussinessDictCodeRds.平安资产类型,"F04","计划类资产_净值型"),
    /**
     * 平安资产类型_存款
     */
    平安资产类型_存款(BussinessDictCodeRds.平安资产类型,"F05","存款"),
    /**
     * 平安资产类型_项目类资产
     */
    平安资产类型_项目类资产(BussinessDictCodeRds.平安资产类型,"F06","项目类资产"),
    /**
     * 平安资产类型_回购
     */
    平安资产类型_回购(BussinessDictCodeRds.平安资产类型,"F07","质押式回购"),
    /**
     * 平安资产类型_拆借
     */
    平安资产类型_拆借(BussinessDictCodeRds.平安资产类型,"F08","拆借"),
    /**
     * 平安资产类型_计划类资产_净值型
     */
    平安资产类型_计划类资产_货币型(BussinessDictCodeRds.平安资产类型,"F09","平安资产类型_计划类资产_货币型"),
    /**
     * 平安资产类型_买断式回购
     */
    平安资产类型_买断式回购(BussinessDictCodeRds.平安资产类型,"F10","买断式回购"),
    /**
     * 二级资产类别_票据类
     */
    二级资产类别_票据类(BussinessDictCodeRds.二级资产类别,"1201","票据类"),
    /**
     * 二级资产类别_信用证
     */
    二级资产类别_信用证(BussinessDictCodeRds.二级资产类别,"1210","信用证"),
    /**
     * 二级资产类别_其他非标准化债权类投资
     */
    二级资产类别_其他非标准化债权类投资(BussinessDictCodeRds.二级资产类别,"1299","其他非标准化债权类投资"),
    /**
     * 拆出
     */
    二级资产类别_拆出(BussinessDictCodeRds.二级资产类别,"1107","拆出"),
    /**
     * 本行存款
     */
    二级资产类别_本行存款(BussinessDictCodeRds.二级资产类别,"1002","本行存款"),
    /**
     * 他行存款
    */
   二级资产类别_他行存款(BussinessDictCodeRds.二级资产类别,"1003","他行存款"),
   /**
    * 本行发行的大额存单
    */
   二级资产类别_本行发行的大额存单(BussinessDictCodeRds.二级资产类别,"1004","本行发行的大额存单"),
   /**
    * 他行发行的大额存单
    */
   二级资产类别_他行发行的大额存单(BussinessDictCodeRds.二级资产类别,"1005","他行发行的大额存单"),
    /**
     * 自定义现金流类型_逾期利息
     */
    自定义现金流类型_逾期利息(BussinessDictCodeRds.自定义现金流类型,"04","逾期利息"),
    /**
     * 自定义现金流类型_费用
     */
    自定义现金流类型_费用(BussinessDictCodeRds.自定义现金流类型,"05","费用"),
    /**
     * 业务表_金融产品交易表
     */
    业务表_金融产品交易表(BussinessDictCodeRds.业务表,"01","金融产品交易表"),
    /**
     * 业务表_交易资金指令表
     */
    业务表_交易资金指令表(BussinessDictCodeRds.业务表,"02","交易资金指令表"),
    /**
     * 机构类型_按规模划分_大型企业
     */
    机构类型_按规模划分_大型企业(BussinessDictCodeRds.机构类型_按规模划分,"02","02 大型企业"),
    /**
     * 日期规则_平台日期
     */
    日期规则_平台日期(BussinessDictCodeRds.日期规则,"01","平台日期"),
    /**
     * 日期规则_AP服务器日期
     */
    日期规则_AP服务器日期(BussinessDictCodeRds.日期规则,"02","系统日期（AP服务器日期）"),
    /**
     * 日期规则_DB日期
     */
    日期规则_DB日期(BussinessDictCodeRds.日期规则,"03","系统日期（DB日期）"),
    /**
     * 指标类型_投资范围类
     */
    指标类型_限制投资金额类(BussinessDictCodeRds.指标类型,"FORBID_FUND","投资范围类"),
    /**
     * 指标类型_当日交易量/市场成交量控制类
     */
    指标类型_当日交易量_市场成交量控制类(BussinessDictCodeRds.指标类型,"LIMIT_AMOUNT","当日交易量/市场成交量控制类"),
    /**
     * 指标类型_集中度类
     */
    指标类型_集中度类(BussinessDictCodeRds.指标类型,"LIMIT_CONCENT","集中度类"),
    /**
     * 指标类型_执行价格控制类
     */
    指标类型_执行价格控制类(BussinessDictCodeRds.指标类型,"LIMIT_EXECU","执行价格控制类"),
    /**
     * 指标类型_流动性管理类
     */
    指标类型_流动性管理类(BussinessDictCodeRds.指标类型,"LIMIT_FLUID","流动性管理类"),
    /**
     * 指标类型_投向控制类
     */
    指标类型_投向控制类(BussinessDictCodeRds.指标类型,"LIMIT_INVEST","投向控制类"),
    /**
     * 指标类型_杠杆控制类
     */
    指标类型_杠杆控制类(BussinessDictCodeRds.指标类型,"LIMIT_LECER","杠杆控制类"),
    /**
     * 指标类型_期限控制类
     */
    指标类型_期限控制类(BussinessDictCodeRds.指标类型,"LIMIT_TERM","期限控制类"),
    /**
     * 指标类型_交易额控制类
     */
    指标类型_交易额控制类(BussinessDictCodeRds.指标类型,"LIMIT_TRAN","交易额控制类"),
    /**
     * 匹配状态_未匹配
     */
    匹配状态_未匹配(BussinessDictCodeRds.匹配状态,"02","未匹配"),
    /**
     * 费用品种类型_托管费
     */
    费用品种类型_托管费(BussinessDictCodeRds.费用品种类型,"01","托管费"),
    /**
     * 费用品种类型_销售手续费
     */
    费用品种类型_销售手续费(BussinessDictCodeRds.费用品种类型,"02","销售手续费"),
    /**
     * 费用品种类型_固定管理费
     */
    费用品种类型_固定管理费(BussinessDictCodeRds.费用品种类型,"03","固定管理费"),
    /**
     * 费用品种类型_浮动差额中收
     */
    费用品种类型_浮动差额中收(BussinessDictCodeRds.费用品种类型,"04","浮动差额中收"),
    /**
     * 费用品种类型_浮动管理费
     */
    费用品种类型_浮动管理费(BussinessDictCodeRds.费用品种类型,"12","浮动管理费"),
    /**
     * 费用品种类型_代销费
     */
    费用品种类型_代销费(BussinessDictCodeRds.费用品种类型,"05","代销费"),
    /**
     * 费用品种类型_交易手续费
     */
    费用品种类型_交易手续费(BussinessDictCodeRds.费用品种类型,"06","交易手续费"),
    /**
     * 费用品种类型_结算服务费
     */
    费用品种类型_结算服务费(BussinessDictCodeRds.费用品种类型,"07","结算服务费"),
    /**
     * 费用品种类型_账户维护费
     */
    费用品种类型_账户维护费(BussinessDictCodeRds.费用品种类型,"08","账户维护费"),
    /**
     * 费用品种类型_增值附加税费
     */
    费用品种类型_增值附加税费(BussinessDictCodeRds.费用品种类型,"09","增值附加税费"),
    /**
     * 费用品种类型_通道托管费
     */
    费用品种类型_通道托管费(BussinessDictCodeRds.费用品种类型,"10","通道托管费"),
    /**
     * 费用品种类型_通道管理费
     */
    费用品种类型_通道管理费(BussinessDictCodeRds.费用品种类型,"11","通道管理费"),
    /**
     * 费用品种类型_外包服务费
     */
    费用品种类型_外包服务费(BussinessDictCodeRds.费用品种类型,"13","外包服务费"),
    /**
     * 费用品种类型_其他费用
     */
    费用品种类型_其他费用(BussinessDictCodeRds.费用品种类型,"99","其他费用"),
    /**
     * 匹配状态_已匹配
     */
    匹配状态_已匹配(BussinessDictCodeRds.匹配状态,"01","已匹配"),
    /**
     * 连接符_并且
     */
    连接符_并且(BussinessDictCodeRds.连接符,"AND","并且"),
    /**
     * 连接符_或
     */
    连接符_或(BussinessDictCodeRds.连接符,"OR","或"),
    /**
     * 单位_万元
     */
    单位_万元(BussinessDictCodeRds.单位,"WRMB","万元"),
    /**
     * 单位_BP
     */
    单位_BP(BussinessDictCodeRds.单位,"BP","BP"),
    /**
     * 监督时分_委托监控
     */
    监督时分_委托监控(BussinessDictCodeRds.监督时分,"01","委托监控"),
    /**
     * 监督时分_交易监控
     */
    监督时分_交易监控(BussinessDictCodeRds.监督时分,"02","交易监控"),
    /**
     * 监督时分_事后监控
     */
    监督时分_事后监控(BussinessDictCodeRds.监督时分,"03","事后监控"),
    /**
     * 监督时分_拆分监控
     */
    监督时分_拆分监控(BussinessDictCodeRds.监督时分,"04","拆分监控"),
    /**
     * 触警操作_预警
     */
    触警操作_预警(BussinessDictCodeRds.触警操作,"01","预警"),
    /**
     * 触警操作_审批
     */
    触警操作_审批(BussinessDictCodeRds.触警操作,"02","审批"),
    /**
     * 触警操作_禁止
     */
    触警操作_禁止(BussinessDictCodeRds.触警操作,"03","禁止"),

    /**
     * 除0通过条件_无条件通过
     */
    除0通过条件_无条件通过(BussinessDictCodeRds.除0通过条件, "01", "无条件通过"),
    /**
     * 除0通过条件_提示异常
     */
    除0通过条件_提示异常(BussinessDictCodeRds.除0通过条件, "02", "提示异常"),
    /**
     * 除0通过条件_起息日通过
     */
    除0通过条件_起息日通过(BussinessDictCodeRds.除0通过条件, "03", "起息日通过"),

    // 一体化对应etliu start
    /**
     * WIND二级分类_含债券二级分类等_同业存单
     */
    WIND一级分类_含债券一级分类等_同业存单(BussinessDictCodeRds.WIND一级分类_含债券一级分类等,"04","同业存单"),
    // 一体化对应etliu end

    /**
     * WIND二级分类_含债券二级分类等_金融债
     */
    WIND一级分类_含债券一级分类等_金融债(BussinessDictCodeRds.WIND一级分类_含债券一级分类等,"05","金融债"),
    /**
     * WIND二级分类_含债券二级分类等_短期融资券
     */
    WIND一级分类_含债券一级分类等_短期融资券(BussinessDictCodeRds.WIND一级分类_含债券一级分类等,"09","短期融资券"),
    /**
     * WIND二级分类_含债券二级分类等_定向工具
     */
    WIND一级分类_含债券一级分类等_定向工具(BussinessDictCodeRds.WIND一级分类_含债券一级分类等,"10","定向工具"),
    /**
     * WIND二级分类_含债券二级分类等_中期票据
     */
    WIND一级分类_含债券一级分类等_中期票据(BussinessDictCodeRds.WIND一级分类_含债券一级分类等,"08","中期票据"),
    /**
     * WIND二级分类_含债券二级分类等_企业债
     */
    WIND一级分类_含债券一级分类等_企业债(BussinessDictCodeRds.WIND一级分类_含债券一级分类等,"06","企业债"),
    /**
     * WIND二级分类_含债券二级分类等_公司债
     */
    WIND一级分类_含债券一级分类等_公司债(BussinessDictCodeRds.WIND一级分类_含债券一级分类等,"07","公司债"),
    /**
     * WIND二级分类_含债券二级分类等_资产支持证券
     */
    WIND一级分类_含债券一级分类等_资产支持证券(BussinessDictCodeRds.WIND一级分类_含债券一级分类等,"13","资产支持证券"),
    /**
     * 估值来源_中债登
     */
    估值来源_中债登(BussinessDictCodeRds.估值来源,"CBC","中债登"),
    /**
     * 估值来源_中证登
     */
    估值来源_中证登(BussinessDictCodeRds.估值来源,"CSD","中证登"),
    /**
     * 估值来源_上清所
     */
    估值来源_上清所(BussinessDictCodeRds.估值来源,"SCH","上清所"),
    /**
     * 币种钞汇标志_钞
     */
    币种钞汇标志_钞(BussinessDictCodeRds.币种钞汇标志,"01","钞"),

    /**
     * 资产附件类型_批复文件
     */
    资产附件类型_批复文件(BussinessDictCodeRds.资产附件类型,"01","批复文件"),
    /**
     * 资产附件类型_CMS批复文件
     */
    资产附件类型_CMS批复文件(BussinessDictCodeRds.币种钞汇标志,"02","CMS批复文件"),

    授权类型_不授权(BussinessDictCodeRds.授权类型,"01","不授权"),
    /**
     * 授权
     */
    授权类型_授权(BussinessDictCodeRds.授权类型,"02","授权"),
    /**
     * 无限授权
     */
    授权类型_无限授权(BussinessDictCodeRds.授权类型,"03","无限授权"),
    /**
     * 中债_项目类二级资产类别_理财直接融资工具
     */
    中债_项目类二级资产类别_理财直接融资工具(BussinessDictCodeRds.中债_项目类二级资产类别,"2101","理财直接融资工具"),
    /**
     * 中债_项目类二级资产类别_基础设施基金
     */
    中债_项目类二级资产类别_基础设施基金(BussinessDictCodeRds.中债_项目类二级资产类别,"2612","基础设施基金"),
    /**
     * 境内外_境内
     */
    境内外_境内(BussinessDictCodeRds.境内外,"01","境内"),
    /**
     * 境内外_境外
     */
    境内外_境外(BussinessDictCodeRds.境内外,"02","境外"),
    /**
     * 回购期限品种_GC001(上交所)
     */
    回购期限品种_GC001(BussinessDictCodeRds.回购期限品种,"GC001","GC001"),
    /**
     * 回购期限品种_GC002(上交所)
     */
    回购期限品种_GC002(BussinessDictCodeRds.回购期限品种,"GC002","GC002"),
    /**
     * 回购期限品种_GC003(上交所)
     */
    回购期限品种_GC003(BussinessDictCodeRds.回购期限品种,"GC003","GC003"),
    /**
     * 回购期限品种_GC004(上交所)
     */
    回购期限品种_GC004(BussinessDictCodeRds.回购期限品种,"GC004","GC004"),
    /**
     * 回购期限品种_GC007(上交所)
     */
    回购期限品种_GC007(BussinessDictCodeRds.回购期限品种,"GC007","GC007"),
    /**
     * 回购期限品种_GC014(上交所)
     */
    回购期限品种_GC014(BussinessDictCodeRds.回购期限品种,"GC014","GC014"),
    /**
     * 回购期限品种_GC028(上交所)
     */
    回购期限品种_GC028(BussinessDictCodeRds.回购期限品种,"GC028","GC028"),
    /**
     * 回购期限品种_GC091(上交所)
     */
    回购期限品种_GC091(BussinessDictCodeRds.回购期限品种,"GC091","GC091"),
    /**
     * 回购期限品种_GC182(上交所)
     */
    回购期限品种_GC182(BussinessDictCodeRds.回购期限品种,"GC182","GC182"),
    /**
     * 回购期限品种_R-001(深交所)
     */
    回购期限品种_R_001(BussinessDictCodeRds.回购期限品种,"R-001","R-001"),
    /**
     * 回购期限品种_R-002(深交所)
     */
    回购期限品种_R_002(BussinessDictCodeRds.回购期限品种,"R-002","R-002"),
    /**
     * 回购期限品种_R-003(深交所)
     */
    回购期限品种_R_003(BussinessDictCodeRds.回购期限品种,"R-003","R-003"),
    /**
     * 回购期限品种_R-004(深交所)
     */
    回购期限品种_R_004(BussinessDictCodeRds.回购期限品种,"R-004","R-004"),
    /**
     * 回购期限品种_R-007(深交所)
     */
    回购期限品种_R_007(BussinessDictCodeRds.回购期限品种,"R-007","R-007"),
    /**
     * 回购期限品种_R-014(深交所)
     */
    回购期限品种_R_014(BussinessDictCodeRds.回购期限品种,"R-014","R-014"),
    /**
     * 回购期限品种_R-028(深交所)
     */
    回购期限品种_R_028(BussinessDictCodeRds.回购期限品种,"R-028","R-028"),
    /**
     * 回购期限品种_R-091(深交所)
     */
    回购期限品种_R_091(BussinessDictCodeRds.回购期限品种,"R-091","R-091"),
    /**
     * 回购期限品种_R-182(深交所)
     */
    回购期限品种_R_182(BussinessDictCodeRds.回购期限品种,"R-182","R-182"),
    /**
     * 回购期限品种_RC001(深交所)
     */
    回购期限品种_RC001(BussinessDictCodeRds.回购期限品种,"RC001","RC001"),
    /**
     * 回购期限品种_RC003(深交所)
     */
    回购期限品种_RC003(BussinessDictCodeRds.回购期限品种,"RC003","RC003"),
    /**
     * 回购期限品种_RC007(深交所)
     */
    回购期限品种_RC007(BussinessDictCodeRds.回购期限品种,"RC007","RC007"),
    /**
     * 回购期限品种_R001(银行间)
     */
    回购期限品种_R001(BussinessDictCodeRds.回购期限品种,"R001","R001"),
    /**
     * 回购期限品种_R007(银行间)
     */
    回购期限品种_R007(BussinessDictCodeRds.回购期限品种,"R007","R007"),
    /**
     * 回购期限品种_R014(银行间)
     */
    回购期限品种_R014(BussinessDictCodeRds.回购期限品种,"R014","R014"),
    /**
     * 回购期限品种_R021(银行间)
     */
    回购期限品种_R021(BussinessDictCodeRds.回购期限品种,"R021","R021"),
    /**
     * 回购期限品种_R1M(银行间)
     */
    回购期限品种_R1M(BussinessDictCodeRds.回购期限品种,"R1M","R1M"),
    /**
     * 回购期限品种_R2M(银行间)
     */
    回购期限品种_R2M(BussinessDictCodeRds.回购期限品种,"R2M","R2M"),
    /**
     * 回购期限品种_R3M(银行间)
     */
    回购期限品种_R3M(BussinessDictCodeRds.回购期限品种,"R3M","R3M"),
    /**
     * 回购期限品种_R4M(银行间)
     */
    回购期限品种_R4M(BussinessDictCodeRds.回购期限品种,"R4M","R4M"),
    /**
     * 回购期限品种_R6M(银行间)
     */
    回购期限品种_R6M(BussinessDictCodeRds.回购期限品种,"R6M","R6M"),
    /**
     * 账户来源_理财
     */
    账户来源_理财(BussinessDictCodeRds.账户来源,"01","理财"),
    /**
     * 账户来源_自营
     */
    账户来源_自营(BussinessDictCodeRds.账户来源,"02","自营"),
    /**
     * 映射状态_未映射
     */
    映射状态_未映射(BussinessDictCodeRds.映射状态,"01","未映射"),
    /**
     * 映射状态_已映射
     */
    映射状态_已映射(BussinessDictCodeRds.映射状态,"02","已映射"),
    /**
     * 导入方式_手工
     */
    导入方式_手工(BussinessDictCodeRds.导入方式,"01","手工"),
    /**
     * 导入方式_自动
     */
    导入方式_自动(BussinessDictCodeRds.导入方式,"02","自动"),

    /**
     * 业务类型_拆借交易
     */
    业务类型_拆借交易(BussinessDictCodeRds.业务类型,"01","拆借交易"),
    /**
     * 业务类型_银行间债券交易
     */
    业务类型_银行间债券交易(BussinessDictCodeRds.业务类型,"02","银行间债券交易"),
    /**
     * 业务类型_存款类资产交易
     */
    业务类型_存款类资产交易(BussinessDictCodeRds.业务类型,"03","存款类资产交易"),
    /**
     * 业务类型_债券质押式回购交易
     */
    业务类型_债券质押式回购交易(BussinessDictCodeRds.业务类型,"04","债券质押式回购交易"),
    /**
     * 业务类型_债券买断式回购交易
     */
    业务类型_债券买断式回购交易(BussinessDictCodeRds.业务类型,"05","债券买断式回购交易"),
    /**
     * 业务类型_货币基金交易_货基类货基资产交易
     */
    业务类型_货币基金交易_货基类货基资产交易(BussinessDictCodeRds.业务类型,"06","货币基金交易（货基、类货基资产交易）"),
    /**
     * 业务类型_非货币基金交易（非货基、非类货基资产交易）
     */
    业务类型_非货币基金交易_非货基非类货基资产交易(BussinessDictCodeRds.业务类型,"07","非货币基金交易（非货基、非类货基资产交易）"),
    /**
     * 业务类型_计划类资产交易（类债券资产交易）
     */
    业务类型_计划类资产交易_类债券资产交易(BussinessDictCodeRds.业务类型,"09","计划类资产交易（类债券资产交易）"),
    /**
     * 业务类型_活期存款交易
     */
    业务类型_活期存款交易(BussinessDictCodeRds.业务类型,"15","活期存款交易"),
    /**
     * 业务类型_增值税交易
     */
    业务类型_增值税交易(BussinessDictCodeRds.业务类型,"16","增值税交易"),
    /**
     * 业务类型_净值型TA交易
     */
    业务类型_净值型TA交易(BussinessDictCodeRds.业务类型,"17","净值型TA交易"),
    /**
     * 业务类型_产品费用交易
     */
    业务类型_产品费用交易(BussinessDictCodeRds.业务类型,"18","产品费用交易"),
    /**
     * 业务类型_指令交易
     */
    业务类型_指令交易(BussinessDictCodeRds.业务类型,"19","指令交易"),
    /**
     * 业务类型_货币型TA交易
     */
    业务类型_货币型TA交易(BussinessDictCodeRds.业务类型,"21","货币型TA交易"),
    /**
     * 业务类型_预期收益型TA交易
     */
    业务类型_预期收益型TA交易(BussinessDictCodeRds.业务类型,"27","预期收益型TA交易"),


    /**
     * 资金账户
     */
    金融产品类型_资金账户(BussinessDictCodeRds.金融产品类型,"F21","资金账户"),
 // 一体化对应etliu end

    /**
     * 中债_项目类二级资产类别_票据类
     */
    中债_项目类二级资产类别_票据类(BussinessDictCodeRds.中债_项目类二级资产类别,"1201","票据类"),
    /**
     * 中债_项目类二级资产类别_信用证
     */
    中债_项目类二级资产类别_信用证(BussinessDictCodeRds.中债_项目类二级资产类别,"1210","信用证"),
    /**
     * 资产投资性质_债权类
     */
    资产投资性质_债权类(BussinessDictCodeRds.资产投资性质,"01","债权类"),
    /**
     * 资产投资性质_权益类
     */
    资产投资性质_权益类(BussinessDictCodeRds.资产投资性质,"02","权益类"),
    /**
     * 资产投资性质_商品及金融衍生品类
     */
    资产投资性质_商品及金融衍生品类(BussinessDictCodeRds.资产投资性质,"03","商品及金融衍生品类"),
    /**
     * 机构类型_按经济类型划分_国有企业
     */
    机构类型_按经济类型划分_国有企业(BussinessDictCodeRds.机构类型_按经济类型划分,"01","01 国有企业"),
    /**
     * 机构类型_按经济类型划分_集体企业
     */
    机构类型_按经济类型划分_集体企业(BussinessDictCodeRds.机构类型_按经济类型划分,"02","02 集体企业"),
    /**
     * 机构类型_按经济类型划分_私营企业
     */
    机构类型_按经济类型划分_私营企业(BussinessDictCodeRds.机构类型_按经济类型划分,"03","03 私营企业"),
    /**
     * 机构类型_按经济类型划分_其他
     */
    机构类型_按经济类型划分_其他(BussinessDictCodeRds.机构类型_按经济类型划分,"99","99 其他"),
    /**
     * 评级类型_主体评级
     */
    评级类型_主体评级(BussinessDictCodeRds.评级类型,"01","主体评级"),
    /**
     * 评级类型_债项评级
     */
    评级类型_债项评级(BussinessDictCodeRds.评级类型,"02","债项评级"),

  /**
   * 发行方式_公开发行
   */
  发行方式_公开发行(BussinessDictCodeRds.发行方式,"01","公开发行"),
  /**
   * 税类型_非公开发行
   */
  发行方式_非公开发行(BussinessDictCodeRds.发行方式,"02","非公开发行"),
  /**
   * 中债_是否_是
   */
  中债_是否_是(BussinessDictCodeRds.中债_是否,"01","是"),
  /**
   * 中债_是否_否
   */
  中债_是否_否(BussinessDictCodeRds.中债_是否,"02","否"),
  /**
   * 中债_二级资产类别_同业拆入
   */
  中债_二级资产类别_同业拆入(BussinessDictCodeRds.二级资产类别,"1901","同业拆入"),
  /**
   * 中债_二级资产类别_卖出回购
   */
  中债_二级资产类别_卖出回购(BussinessDictCodeRds.二级资产类别,"1902","卖出回购"),
  /**
   * 中债_二级资产类别_拆放同业
   */
  中债_二级资产类别_拆放同业(BussinessDictCodeRds.二级资产类别,"1107","拆放同业"),
  /**
   * 中债_二级资产类别_买入返售
   */
  中债_二级资产类别_买入返售(BussinessDictCodeRds.二级资产类别,"1108","买入返售"),
  /**
   * 中债_二级资产类别_货币市场基金
   */
  中债_二级资产类别_货币市场基金(BussinessDictCodeRds.二级资产类别,"2401","货币市场基金"),
  /**
   * 中债_二级资产类别_债券基金
   */
  中债_二级资产类别_债券基金(BussinessDictCodeRds.二级资产类别,"1106","债券基金"),
  /**
   * 中债_二级资产类别_股票基金
   */
  中债_二级资产类别_股票基金(BussinessDictCodeRds.二级资产类别,"1303","股票基金"),
  /**
   * 中债_二级资产类别_基金中基金
   */
  中债_二级资产类别_基金中基金(BussinessDictCodeRds.二级资产类别,"2403","基金中基金"),
  /**
   * 中债_二级资产类别_混合基金
   */
  中债_二级资产类别_混合基金(BussinessDictCodeRds.二级资产类别,"2402","混合基金"),
  /**
   * 中债_二级资产类别_其他公募基金
   */
  中债_二级资产类别_其他公募基金(BussinessDictCodeRds.二级资产类别,"2499","其他公募基金"),
  /**
   * 全国银行间同业拆借市场
   */
  交易流通市场_全国银行间同业拆借市场(BussinessDictCodeRds.交易流通场所,"17","全国银行间同业拆借市场"),
  /**
   * 银行间债券市场
   */
  交易流通市场_银行间债券市场(BussinessDictCodeRds.交易流通场所,"01","银行间债券市场"),
  /**
   * 上海证券交易所
   */
  交易流通市场_上海证券交易所(BussinessDictCodeRds.交易流通场所,"03","上海证券交易所"),
  /**
   * 深圳证券交易所
   */
  交易流通市场_深圳证券交易所(BussinessDictCodeRds.交易流通场所,"04","深圳证券交易所"),
  /**
   * 中债_计息基础_ACT366
   */
  中债_计息基础_ACT366(BussinessDictCodeRds.中债_计息基础,"01","ACT/366"),
  /**
   * 中债_计息基础_ACT360
   */
  中债_计息基础_ACT360(BussinessDictCodeRds.中债_计息基础,"02","ACT/360"),
  /**
   * 中债_计息基础_ACT365
   */
  中债_计息基础_ACT365(BussinessDictCodeRds.中债_计息基础,"03","ACT/365"),
  /**
   * 中债_计息基础_ACTACT
   */
  中债_计息基础_ACTACT(BussinessDictCodeRds.中债_计息基础,"04","ACT/ACT"),
  /**
   * 中债_计息基础_30360
   */
  中债_计息基础_30360(BussinessDictCodeRds.中债_计息基础,"05","30/360"),

  /**
   * 展示层级_01
   */
  展示层级_01(BussinessDictCodeRds.展示层级,"01","一级"),
  /**
   * 展示层级_02
   */
  展示层级_02(BussinessDictCodeRds.展示层级,"02","二级"),
  /**
   * 展示层级_03
   */
  展示层级_03(BussinessDictCodeRds.展示层级,"03","三级"),
  /**
   * 展示层级_04
   */
  展示层级_04(BussinessDictCodeRds.展示层级,"04","四级"),

  /**
   * 共同业务类型_期次管理
   */
  共同业务类型_期次管理(BussinessDictCodeRds.共同业务类型, "FINASSET13", "期次管理"),
  //sjj add 授信参数传值方式修改  end
  /**
   * 金融产品类型_投管_母资管计划
   */
  金融产品类型_母资管计划(BussinessDictCodeRds.金融产品类型,"F27","母资管计划"),
  /**
   * 金融产品类型_投管_通道
   */
  金融产品类型_通道(BussinessDictCodeRds.金融产品类型,"F28","通道"),
  /**
   * 逆回购到期
   */
  共同业务类型_逆回购到期(BussinessDictCodeRds.共同业务类型,"TRD42","逆回购到期"),
  /**
   * 正回购到期
   */
  共同业务类型_正回购到期(BussinessDictCodeRds.共同业务类型,"TRD43","正回购到期"),
  /**
   * 通道资金划拨管理
   */
  共同业务类型_通道资金划拨管理(BussinessDictCodeRds.共同业务类型,"PTL10","通道资金划拨管理"),
  /**
   * 通道划款
   */
  交易方向_通道划款(BussinessDictCodeRds.交易方向,"HK09","通道划款"),
  /**
   * 通道回款
   */
  交易方向_通道回款(BussinessDictCodeRds.交易方向,"HK10","通道回款"),
  /**
   * 成功失败标志_成功
   */
  成功失败标志_成功(BussinessDictCodeRds.成功失败标志,"01","成功"),
  /**
   * 平安业务类型_通道划款
   */
  平安业务类型_通道划款(BussinessDictCodeRds.平安业务类型,"86","通道划款"),
  /**
   * 平安业务类型_通道回款
   */
  平安业务类型_通道回款(BussinessDictCodeRds.平安业务类型,"87","通道回款"),

  /**
   * 银证转账
   */
  交易方向_银证转账(BussinessDictCodeRds.交易方向,"HK01","银证转账"),
  /**
   * 证银转账
   */
  交易方向_证银转账(BussinessDictCodeRds.交易方向,"HK02","证银转账"),

  /**
   * 项目类资产付息计划调整
   */
  共同业务类型_项目类资产付息计划调整(BussinessDictCodeRds.共同业务类型, "FINASSET16", "项目类资产付息计划调整"),
  /**
   * 项目类资产还本计划调整
   */
  共同业务类型_项目类资产还本计划调整(BussinessDictCodeRds.共同业务类型, "FINASSET17", "项目类资产还本计划调整"),

  /**
   * 托管户-上清DVP
   */
  交易方向_托管户_上清DVP(BussinessDictCodeRds.交易方向,"HK03","托管户-上清DVP"),
  /**
   * 托管户-中债DVP
   */
  交易方向_托管户_中债DVP(BussinessDictCodeRds.交易方向,"HK04","托管户-中债DVP"),
  /**
   * 上清DVP-托管户
   */
  交易方向_上清DVP_托管户(BussinessDictCodeRds.交易方向,"HK05","上清DVP-托管户"),
  /**
   * 中债DVP-托管户
   */
  交易方向_中债DVP_托管户(BussinessDictCodeRds.交易方向,"HK06","中债DVP-托管户"),
  /**
   * 上清DVP-中债DVP
   */
  交易方向_上清DVP_中债DVP(BussinessDictCodeRds.交易方向,"HK07","上清DVP-中债DVP"),
  /**
   * 中债DVP-上清DVP
   */
  交易方向_中债DVP_上清DVP(BussinessDictCodeRds.交易方向,"HK08","中债DVP-上清DVP"),

  /**
   * DVP资金调拨管理
   */
  共同业务类型_DVP资金调拨管理(BussinessDictCodeRds.共同业务类型, "PTL09", "DVP资金调拨管理"),

  /**
   * 平安业务类型_DVP资金调拨_托管户_上清DVP
   */
  平安业务类型_DVP资金调拨_托管户_上清DVP(BussinessDictCodeRds.平安业务类型,"88","DVP资金调拨-托管户-上清DVP"),
  /**
   * 平安业务类型_DVP资金调拨_托管户_中债DVP
   */
  平安业务类型_DVP资金调拨_托管户_中债DVP(BussinessDictCodeRds.平安业务类型,"89","DVP资金调拨-托管户-中债DVP"),
  /**
   * 平安业务类型_DVP资金调拨_上清DVP_托管户
   */
  平安业务类型_DVP资金调拨_上清DVP_托管户(BussinessDictCodeRds.平安业务类型,"90","DVP资金调拨-上清DVP-托管户"),
  /**
   * 平安业务类型_DVP资金调拨_中债DVP_托管户
   */
  平安业务类型_DVP资金调拨_中债DVP_托管户(BussinessDictCodeRds.平安业务类型,"91","DVP资金调拨-中债DVP-托管户"),
  /**
   * 平安业务类型_DVP资金调拨_上清DVP_中债DVP
   */
  平安业务类型_DVP资金调拨_上清DVP_中债DVP(BussinessDictCodeRds.平安业务类型,"92","DVP资金调拨-上清DVP-中债DVP"),
  /**
   * 平安业务类型_DVP资金调拨_中债DVP_上清DVP
   */
  平安业务类型_DVP资金调拨_中债DVP_上清DVP(BussinessDictCodeRds.平安业务类型,"93","DVP资金调拨-中债DVP-上清DVP"),

  /**
   * 销售模式_直销
   */
  销售模式_直销(BussinessDictCodeRds.销售模式,"01","销售模式_直销"),
  
    /**
     * 业务类型_债券交易
     */
    业务类型_债券交易(BussinessDictCodeRds.业务类型, "02", "债券交易"),


  // etliu add 上农商版本添加 start
  /**
   * 处理模式_周期型
   */
  处理模式_周期型(BussinessDictCodeRds.处理模式,"10","周期型"),
  /**
   * 处理模式_分档
   */
  处理模式_分档(BussinessDictCodeRds.处理模式,"11","分档"),
  /**
   * 处理模式_分档周期型
   */
  处理模式_分档周期型(BussinessDictCodeRds.处理模式,"12","分档周期型"),
  /**
   * 调整类型_提前终止
   */
  调整类型_提前终止(BussinessDictCodeRds.调整类型,"01","提前终止"),
  /**
   * 调整类型_产品展期
   */
  调整类型_产品展期(BussinessDictCodeRds.调整类型,"02","产品展期"),
  /**
   * 调整类型_到期调整
   */
  调整类型_到期调整(BussinessDictCodeRds.调整类型,"03","到期调整"),
  /**
   * 净值型产品存续期交易新建
   */
  共同业务类型_净值型产品存续期交易(BussinessDictCodeRds.共同业务类型, "FINPROD99", "净值型产品存续期交易"),
  /**
   * 计划类型_申购计划
   */
  计划类型_申购计划(BussinessDictCodeRds.计划类型,"01","申购计划"),
  /**
   * 计划类型_赎回计划
   */
  计划类型_赎回计划(BussinessDictCodeRds.计划类型,"02","赎回计划"),
  /**
   * 计提方式_每日
   */
  计提方式_每日(BussinessDictCodeRds.计提方式,"01","每日"),
  /**
   * 计提方式_区间
   */
  计提方式_区间(BussinessDictCodeRds.计提方式,"02","区间"),
  交易方向_产品提前终止(BussinessDictCodeRds.交易方向, "C16", "产品提前终止"),
  交易方向_产品提前赎回(BussinessDictCodeRds.交易方向, "C17", "产品提前赎回"),
  交易方向_产品违约赎回(BussinessDictCodeRds.交易方向, "C18", "产品违约赎回"),

  /**
   * 申赎开放类型_申购开放
   */
  申赎开放类型_赎回开放(BussinessDictCodeRds.申赎开放类型,"02","赎回开放"),
  /**
   * 有效状态_有效
   */
  有效状态_流程中(BussinessDictCodeRds.有效状态,"04","流程中"),
  // etliu add 上农商版本添加 end
  /**
   * 风险事件运算关系对象_交易金额
   */
  风险事件运算关系对象_交易金额(BussinessDictCodeRds.风险事件运算关系对象,"01","交易金额"),
  /**
   * 风险事件运算关系对象_交易价格
   */
  风险事件运算关系对象_交易价格(BussinessDictCodeRds.风险事件运算关系对象,"02","交易价格"),
  /**
   * 风险事件运算关系对象_交易数量
   */
  风险事件运算关系对象_交易数量(BussinessDictCodeRds.风险事件运算关系对象,"03","交易数量"),
  /**
   * 风险事件运算关系对象_数量
   */
  风险事件运算关系对象_数量(BussinessDictCodeRds.风险事件运算关系对象,"tradeNum","数量"),
  /**
   * 风险事件运算关系对象_金额
   */
  风险事件运算关系对象_金额(BussinessDictCodeRds.风险事件运算关系对象,"amount","金额"),
  /**
   * 运算符_大于
   */
  运算符_大于(BussinessDictCodeRds.运算符,">","> / 大于"),
  /**
   * 运算符_大于等于
   */
  运算符_大于等于(BussinessDictCodeRds.运算符,">=",">= / 大于等于"),
  /**
   * 运算符_小于
   */
  运算符_小于(BussinessDictCodeRds.运算符,"<","< / 小于"),
  /**
   * 运算符_小于等于
   */
  运算符_小于等于(BussinessDictCodeRds.运算符,"<=","<= / 小于等于"),
  /**
   * 运算符_等于
   */
  运算符_等于(BussinessDictCodeRds.运算符,"=","= / 等于"),
  /**
   * 运算符_不等于
   */
  运算符_不等于(BussinessDictCodeRds.运算符,"<>","<> / 不等于"),
  /**
   * 运算符_包含
   */
  运算符_包含(BussinessDictCodeRds.运算符,"IN","IN / 包含"),
  /**
   * 运算符_不包含
   */
  运算符_不包含(BussinessDictCodeRds.运算符,"NO","NO / 不包含"),
  /**
   * 操作符_加
   */
  操作符_加(BussinessDictCodeRds.操作符,"+","+"),
  /**
   * 操作符_减
   */
  操作符_减(BussinessDictCodeRds.操作符,"-","-"),
  /**
   * 操作符_乘
   */
  操作符_乘(BussinessDictCodeRds.操作符,"*","*"),
  /**
   * 操作符_除
   */
  操作符_除(BussinessDictCodeRds.操作符,"/","/"),
  /**
   * 指标状态_通过
   */
  指标状态_通过(BussinessDictCodeRds.指标状态,"0","通过"),
  /**
   * 指标状态_预警
   */
  指标状态_预警(BussinessDictCodeRds.指标状态,"1","预警"),
  /**
   * 指标状态_审批
   */
  指标状态_审批(BussinessDictCodeRds.指标状态,"2","审批"),
  /**
   * 指标状态_禁止
   */
  指标状态_禁止(BussinessDictCodeRds.指标状态,"3","禁止"),
  /**
   * 指标状态_异常（除0）
   */
  指标状态_异常_除0(BussinessDictCodeRds.指标状态,"4","异常(除0)"),
  /**
   * 事件类型_投资委托
   */
  事件类型_投资委托(BussinessDictCodeRds.事件类型,"0","投资委托"),
  /**
   * 事件类型_投资交易
   */
  事件类型_投资交易(BussinessDictCodeRds.事件类型,"1","投资交易"),
  /**
   * 事件类型_交易拆分
   */
  事件类型_交易拆分(BussinessDictCodeRds.事件类型,"2","交易拆分"),

    /**
     * 事件类型_投资交易_存续期(只是作为常量而已，不参与接口与数据字典)
     */
    事件类型_投资交易_存续期(BussinessDictCodeRds.事件类型,"3","投资交易存续期"),

    /**
     * 批机能类型_日间指标计算
     */
    批机能类型_日间指标计算(BussinessDictCodeRds.批机能类型,"01","日间指标计算"),
    /**
     * 批机能类型_日终指标计算
     */
    批机能类型_日终指标计算(BussinessDictCodeRds.批机能类型,"02","日终指标计算"),
    /**
     * 批机能类型_REDIS初始化
     */
    批机能类型_REDIS初始化(BussinessDictCodeRds.批机能类型,"03","REDIS初始化"),
    /**
     * 批机能类型_交易数据接入
     */
    批机能类型_交易数据接入(BussinessDictCodeRds.批机能类型,"04","交易数据接入"),
    /**
     * 批机能类型_主动提醒
     */
    批机能类型_主动提醒(BussinessDictCodeRds.批机能类型,"05","主动提醒"),

  //added by wangzhen 20190822 start
  /**
  *  风险事件特征值类型
  */
  风险事件特征值类型_系统资产大类(BussinessDictCodeRds.风险事件特征值类型, "assetType", "系统资产大类"),
  风险事件特征值类型_资管细类(BussinessDictCodeRds.风险事件特征值类型, "assetType2", "资管细类"),
  风险事件特征值类型_交易方向(BussinessDictCodeRds.风险事件特征值类型, "ps", "交易方向"),
  // 资产代码
  风险事件特征值类型_资产代码(BussinessDictCodeRds.风险事件特征值类型, "assetCode", "资产代码"),
  // 监管一级分类
  风险事件特征值类型_监管一级分类(BussinessDictCodeRds.风险事件特征值类型, "type1", "监管一级分类"),
  // 监管二级分类
  风险事件特征值类型_监管二级分类(BussinessDictCodeRds.风险事件特征值类型, "type2", "监管二级分类"),
  // 投资性质
  风险事件特征值类型_投资性质(BussinessDictCodeRds.风险事件特征值类型, "investType", "投资性质"),
  // 估值方法
  风险事件特征值类型_估值方法(BussinessDictCodeRds.风险事件特征值类型, "valuationMd", "估值方法"),
  // 事件类型
  风险事件特征值类型_事件类型(BussinessDictCodeRds.风险事件特征值类型, "eventType", "事项类型"),
  // 资产到期日 mdate
  风险事件特征值类型_资产到期日(BussinessDictCodeRds.风险事件特征值类型, "mdate", "资产到期日"),
    //所属wind行业一级分类
     风险事件特征值类型_所属wind行业一级分类(BussinessDictCodeRds.风险事件特征值类型, "industry", "所属wind行业一级分类"),
     // 所属wind行业二级分类
     风险事件特征值类型_所属wind行业二级分类(BussinessDictCodeRds.风险事件特征值类型, "industry2", "所属wind行业二级分类"),
    //债券外部评级
    风险事件特征值类型_债券外部评级(BussinessDictCodeRds.风险事件特征值类型, "bondGradeType", "债券外部评级"),
    //主体外部评级
    风险事件特征值类型_主体外部评级(BussinessDictCodeRds.风险事件特征值类型, "bodyGradeType", "主体外部评级"),
    //债券外部评级
    风险事件特征值类型_wind一级分类(BussinessDictCodeRds.风险事件特征值类型, "windType1", "wind一级分类"),
    //主体外部评级
    风险事件特征值类型_wind二级分类(BussinessDictCodeRds.风险事件特征值类型, "windType2", "wind二级分类"),
    
  /**
   *  特征值偏移单位
   */
  特征值偏移单位_工作日(BussinessDictCodeRds.特征值偏移单位, "01", "工作日"),
  特征值偏移单位_自然日(BussinessDictCodeRds.特征值偏移单位, "02", "自然日"),
  特征值偏移单位_年(BussinessDictCodeRds.特征值偏移单位, "03", "年"),
  特征值偏移单位_月(BussinessDictCodeRds.特征值偏移单位, "04", "月"),
    
  // added by wangzhen 20190822 end
    
    
    
    
    
    
    //------------------------------DICTCODE_LIST----------------------------------

    监管_现金及银行存款(
            BussinessDictCodeRds.中债_监管一级分类, "1", "现金及银行存款"
    ), 监管_同业存单(
            BussinessDictCodeRds.中债_监管一级分类, "2", "同业存单"
    ), 监管_拆放同业及买入返售(
            BussinessDictCodeRds.中债_监管一级分类, "3", "拆放同业及买入返售"
    ), 监管_债券(
            BussinessDictCodeRds.中债_监管一级分类, "4", "债券"
    ), 监管_理财直接融资工具(
            BussinessDictCodeRds.中债_监管一级分类, "5", "理财直接融资工具"
    ), 监管_新增可投资资产(
            BussinessDictCodeRds.中债_监管一级分类, "6", "新增可投资资产"
    ), 监管_非标准化债权类资产(
            BussinessDictCodeRds.中债_监管一级分类, "7", "非标准化债权类资产"
    ), 监管_权益类资产(
            BussinessDictCodeRds.监管一级分类, "8", "权益类资产"
    ), 监管_金融衍生品(
            BussinessDictCodeRds.中债_监管一级分类, "9", "金融衍生品"
    ), 监管_代客境外理财投资QDII(
            BussinessDictCodeRds.中债_监管一级分类, "10", "代客境外理财投资QDII"
    ), 监管_商品类资产(
            BussinessDictCodeRds.中债_监管一级分类, "11", "商品类资产"
    ), 监管_另类资产(
            BussinessDictCodeRds.中债_监管一级分类, "12", "另类资产"
    ), 监管_公募基金(
            BussinessDictCodeRds.中债_监管一级分类, "13", "公募基金"
    ), 监管_私募基金(
            BussinessDictCodeRds.中债_监管一级分类, "14", "私募基金"
    ), 监管_资产管理产品(
            BussinessDictCodeRds.中债_监管一级分类, "15", "资产管理产品"
    ), 监管_委外投资_协议方式(
            BussinessDictCodeRds.中债_监管一级分类, "16", "委外投资-协议方式"
    ), 监管_同业拆入及卖出回购(
            BussinessDictCodeRds.中债_监管一级分类, "17", "同业拆入及卖出回购"
    ), 监管_其他(
            BussinessDictCodeRds.中债_监管一级分类, "18", "其他"
    ), 监管_货币市场工具(
            BussinessDictCodeRds.中债_监管一级分类, "91", "货币市场工具"
    ), 监管_货币市场_负债(
            BussinessDictCodeRds.中债_监管一级分类, "92", "货币市场(负债)"
    ), 资管_存款类资产(
            BussinessDictCodeRds.资管一级分类, "01", "存款类资产(资管)"
    ), 资管_债券类资产(
            BussinessDictCodeRds.资管一级分类, "02", "债券类资产(资管)"
    ), 资管_项目类资产(
            BussinessDictCodeRds.资管一级分类, "03", "项目类资产(资管)"
    ), 资管_基金类资产(
            BussinessDictCodeRds.资管一级分类, "04", "基金类资产(资管)"
    ), 资管_计划类资产(
            BussinessDictCodeRds.资管一级分类, "05", "计划类资产(资管)"
    ), 资管_质押式回购(
            BussinessDictCodeRds.资管一级分类, "06", "质押式回购(资管)"
    ), 授权资产一级分类_货币市场类业务(
            BussinessDictCodeRds.授权资产一级分类, "1", "货币市场类业务"
    ), 授权资产一级分类_债券类业务(
            BussinessDictCodeRds.授权资产一级分类, "2", "债券类业务"
    ), 授权资产一级分类_公募证券投资基金(
            BussinessDictCodeRds.授权资产一级分类, "3", "公募证券投资基金"
    ), 授权资产一级分类_基金专户及资产管理计划(
            BussinessDictCodeRds.授权资产一级分类, "4", "基金专户及资产管理计划"
    ), 授权资产一级分类_权益类资产(
            BussinessDictCodeRds.授权资产一级分类, "5", "权益类资产"
    ), 资产类型分类_授权分类(
            BussinessDictCodeRds.资产类型分类, "01", "授权分类"
    ), 资产类型分类_中债分类(
            BussinessDictCodeRds.资产类型分类, "02", "中债分类"
    ), 资产类型分类_资管分类(
            BussinessDictCodeRds.资产类型分类, "03", "资管分类"
    ), 资产类型分类_债券wind分类(
            BussinessDictCodeRds.资产类型分类, "04", "债券wind分类"
    ), 所属行业分类_所属WIND行业(
            BussinessDictCodeRds.所属行业分类, "WIND", "所属WIND行业"
    ), 所属行业分类_所属申万行业(
            BussinessDictCodeRds.所属行业分类, "SWHY", "所属申万行业"
    ), 所属行业分类_所属国标行业(
            BussinessDictCodeRds.所属行业分类, "GB", "所属国标行业"
    ), 所属行业分类_所属证监会行业(
            BussinessDictCodeRds.所属行业分类, "CSRC", "所属证监会行业"
    ), 评级分类_外部评级(
            BussinessDictCodeRds.评级分类, "01", "外部评级"
    ), 评级分类_内部评级(
            BussinessDictCodeRds.评级分类, "02", "内部评级"
    ),  单位_无(
            BussinessDictCodeRds.单位, "NONE", "无"
    ), 参数模板_复合模板(
            BussinessDictCodeRds.参数模板, "FHMB", "复合模板"
    ), 参数模板_前N日净资产市值(
            BussinessDictCodeRds.参数模板, "JZCSZ", "前N日净资产市值"
    ), 参数模板_前N日净资产成本(
            BussinessDictCodeRds.参数模板, "JZCCB", "前N日净资产成本"
    ), 日期范围类型_自定义(
            BussinessDictCodeRds.日期范围类型, "CUSTOM", "自定义"
    ), 日期范围类型_半年内(
            BussinessDictCodeRds.日期范围类型, "HY", "半年内"
    ), 日期范围类型_一月内(
            BussinessDictCodeRds.日期范围类型, "M", "一月内"
    ), 日期范围类型_上一日(
            BussinessDictCodeRds.日期范围类型, "PD", "上一日"
    ), 日期范围类型_上月末(
            BussinessDictCodeRds.日期范围类型, "PM", "上月末"
    ), 日期范围类型_上季度末(
            BussinessDictCodeRds.日期范围类型, "PQ", "上季度末"
    ), 日期范围类型_一季度内(
            BussinessDictCodeRds.日期范围类型, "Q", "一季度内"
    ), 日期范围类型_一年内(
            BussinessDictCodeRds.日期范围类型, "Y", "一年内"
    ), 参数粒度_实时(
            BussinessDictCodeRds.参数粒度, "01", "实时"
    ), 参数粒度_买入(
            BussinessDictCodeRds.参数粒度, "02", "买入"
    ), 参数粒度_卖出(
            BussinessDictCodeRds.参数粒度, "03", "卖出"
    ), 参数粒度_事后_日间(
            BussinessDictCodeRds.参数粒度, "04", "事后(日间)"
    ), 参数粒度_事后_日终(
            BussinessDictCodeRds.参数粒度, "05", "事后(日终)"
    ), 指标参数实现类型_自定义(
            BussinessDictCodeRds.指标参数实现类型, "CUSTOM", "自定义"
    ), 条件项_建仓期时间(
            BussinessDictCodeRds.条件项, "03", "建仓期时间"
    ), 条件项_启用时间(
            BussinessDictCodeRds.条件项, "04", "启用时间"
    ), 条件项_单位净值控制(
            BussinessDictCodeRds.条件项, "02", "单位净值控制"
    ), 条件项_资产净值控制(
            BussinessDictCodeRds.条件项, "01", "资产净值控制"
    ), 机构所属行业一级分类_农_林_牧_渔业(
            BussinessDictCodeRds.机构所属行业一级分类, "01", "农、林、牧、渔业"
    ), 机构所属行业一级分类_采矿业(
            BussinessDictCodeRds.机构所属行业一级分类, "02", "采矿业"
    ), 机构所属行业一级分类_制造业(
            BussinessDictCodeRds.机构所属行业一级分类, "03", "制造业"
    ), 机构所属行业一级分类_电力_热力_燃气及水生产和供应业(
            BussinessDictCodeRds.机构所属行业一级分类, "04", "电力、热力、燃气及水生产和供应业"
    ), 机构所属行业一级分类_建筑业(
            BussinessDictCodeRds.机构所属行业一级分类, "05", "建筑业"
    ), 机构所属行业一级分类_批发和零售业(
            BussinessDictCodeRds.机构所属行业一级分类, "06", "批发和零售业"
    ), 机构所属行业一级分类_交通运输_仓储和邮政业(
            BussinessDictCodeRds.机构所属行业一级分类, "07", "交通运输、仓储和邮政业"
    ), 机构所属行业一级分类_住宿和餐饮业(
            BussinessDictCodeRds.机构所属行业一级分类, "08", "住宿和餐饮业"
    ), 机构所属行业一级分类_信息传输_软件和信息技术服务业(
            BussinessDictCodeRds.机构所属行业一级分类, "09", "信息传输、软件和信息技术服务业"
    ), 机构所属行业一级分类_金融业(
            BussinessDictCodeRds.机构所属行业一级分类, "10", "金融业"
    ), 机构所属行业一级分类_房地产(
            BussinessDictCodeRds.机构所属行业一级分类, "11", "房地产"
    ), 机构所属行业一级分类_租赁和商务服务业(
            BussinessDictCodeRds.机构所属行业一级分类, "12", "租赁和商务服务业"
    ), 机构所属行业一级分类_科学研究和技术服务业(
            BussinessDictCodeRds.机构所属行业一级分类, "13", "科学研究和技术服务业"
    ), 机构所属行业一级分类_水利_环境和公共设施管理业(
            BussinessDictCodeRds.机构所属行业一级分类, "14", "水利、环境和公共设施管理业"
    ), 机构所属行业一级分类_居民服务_修理和其他服务业(
            BussinessDictCodeRds.机构所属行业一级分类, "15", "居民服务、修理和其他服务业"
    ), 机构所属行业一级分类_教育(
            BussinessDictCodeRds.机构所属行业一级分类, "16", "教育"
    ), 机构所属行业一级分类_卫生和社会工作(
            BussinessDictCodeRds.机构所属行业一级分类, "17", "卫生和社会工作"
    ), 机构所属行业一级分类_文化_体育和娱乐业(
            BussinessDictCodeRds.机构所属行业一级分类, "18", "文化、体育和娱乐业"
    ), 机构所属行业一级分类_公共管理_社会保障和社会组织(
            BussinessDictCodeRds.机构所属行业一级分类, "19", "公共管理、社会保障和社会组织"
    ), 机构所属行业一级分类_国际组织(
            BussinessDictCodeRds.机构所属行业一级分类, "20", "国际组织"
    ), 机构所属行业二级分类_农业(
            BussinessDictCodeRds.机构所属行业二级分类, "A01", "农业"
    ), 机构所属行业二级分类_林业(
            BussinessDictCodeRds.机构所属行业二级分类, "A02", "林业"
    ), 机构所属行业二级分类_畜牧业(
            BussinessDictCodeRds.机构所属行业二级分类, "A03", "畜牧业"
    ), 机构所属行业二级分类_渔业(
            BussinessDictCodeRds.机构所属行业二级分类, "A04", "渔业"
    ), 机构所属行业二级分类_农_林_牧_渔服务业(
            BussinessDictCodeRds.机构所属行业二级分类, "A05", "农、林、牧、渔服务业"
    ), 机构所属行业二级分类_煤炭开采和洗选业(
            BussinessDictCodeRds.机构所属行业二级分类, "B06", "煤炭开采和洗选业"
    ), 机构所属行业二级分类_石油和天然气开采业(
            BussinessDictCodeRds.机构所属行业二级分类, "B07", "石油和天然气开采业"
    ), 机构所属行业二级分类_黑色金属矿采选业(
            BussinessDictCodeRds.机构所属行业二级分类, "B08", "黑色金属矿采选业"
    ),

    收益类型_净值型(
            BussinessDictCodeRds.收益类型, "01", "净值型"
    ), 收益类型_收益型(
            BussinessDictCodeRds.收益类型, "02", "收益型"
    ), 收益类型_货币型(
            BussinessDictCodeRds.收益类型, "03", "货币型"
    ),

    币种_人民币_CNY(
            BussinessDictCodeRds.币种, "CNY", "人民币（CNY）"
    ), 币种_港元_HKD(
            BussinessDictCodeRds.币种, "HKD", "港元（HKD）"
    ), 币种_美元_USD(
            BussinessDictCodeRds.币种, "USD", "美元（USD）"
    ), 币种_欧元_EUR(
            BussinessDictCodeRds.币种, "EUR", "欧元（EUR）"
    ), 币种_日元_JPY(
            BussinessDictCodeRds.币种, "JPY", "日元（JPY）"
    ), 币种_澳门元_MOP(
            BussinessDictCodeRds.币种, "MOP", "澳门元（MOP）"
    ), 币种_瑞士法郎_CHF(
            BussinessDictCodeRds.币种, "CHF", "瑞士法郎（CHF）"
    ), 币种_英镑_GBP(
            BussinessDictCodeRds.币种, "GBP", "英镑（GBP）"
    ), 币种_俄罗斯卢布_RUB(
            BussinessDictCodeRds.币种, "RUB", "俄罗斯卢布（RUB）"
    ), 币种_加拿大元_CAD(
            BussinessDictCodeRds.币种, "CAD", "加拿大元（CAD）"
    ), 币种_丹麦克朗_DKK(
            BussinessDictCodeRds.币种, "DKK", "丹麦克朗（DKK）"
    ), 币种_澳大利亚元_AUD(
            BussinessDictCodeRds.币种, "AUD", "澳大利亚元（AUD）"
    ), 币种_瑞典克朗_SEK(
            BussinessDictCodeRds.币种, "SEK", "瑞典克朗（SEK）"
    ), 币种_新加坡元_SGD(
            BussinessDictCodeRds.币种, "SGD", "新加坡元（SGD）"
    ), 币种_新台币_TWD(
            BussinessDictCodeRds.币种, "TWD", "新台币（TWD）"
    ), 交易流通场所_银行间债券市场(
            BussinessDictCodeRds.交易流通场所, "01", "银行间债券市场"
    ), 交易流通场所_商业银行柜台市场(
            BussinessDictCodeRds.交易流通场所, "02", "商业银行柜台市场"
    ), 交易流通场所_上海证券交易所(
            BussinessDictCodeRds.交易流通场所, "03", "上海证券交易所"
    ), 交易流通场所_深圳证券交易所(
            BussinessDictCodeRds.交易流通场所, "04", "深圳证券交易所"
    ), 交易流通场所_全国中小企业股份转让系统(
            BussinessDictCodeRds.交易流通场所, "05", "全国中小企业股份转让系统"
    ), 交易流通场所_上海期货交易所(
            BussinessDictCodeRds.交易流通场所, "06", "上海期货交易所"
    ), 交易流通场所_郑州商品交易所(
            BussinessDictCodeRds.交易流通场所, "07", "郑州商品交易所"
    ), 交易流通场所_大连商品交易所(
            BussinessDictCodeRds.交易流通场所, "08", "大连商品交易所"
    ), 交易流通场所_中国金融期货交易所(
            BussinessDictCodeRds.交易流通场所, "09", "中国金融期货交易所"
    ), 交易流通场所_上海黄金交易所(
            BussinessDictCodeRds.交易流通场所, "10", "上海黄金交易所"
    ), 交易流通场所_区域性产权_资产交易市场(
            BussinessDictCodeRds.交易流通场所, "11", "区域性产权/资产交易市场"
    ), 交易流通场所_区域性商品交易所(
            BussinessDictCodeRds.交易流通场所, "12", "区域性商品交易所"
    ), 交易流通场所_区域性贵金属交易所(
            BussinessDictCodeRds.交易流通场所, "13", "区域性贵金属交易所"
    ), 交易流通场所_艺术品市场(
            BussinessDictCodeRds.交易流通场所, "14", "艺术品市场"
    ), 交易流通场所_基金市场(
            BussinessDictCodeRds.交易流通场所, "15", "基金市场"
    ), 交易流通场所_信托市场(
            BussinessDictCodeRds.交易流通场所, "16", "信托市场"
    ), 交易流通场所_全国银行间同业拆借市场(
            BussinessDictCodeRds.交易流通场所, "17", "全国银行间同业拆借市场"
    ), 交易流通场所_上海票据交易所(
            BussinessDictCodeRds.交易流通场所, "18", "上海票据交易所"
    ), 交易流通场所_其他交易流通场所(
            BussinessDictCodeRds.交易流通场所, "99", "其他交易流通场所"
    ),
    WIND一级分类_含债券一级分类等_国债(
            BussinessDictCodeRds.WIND一级分类_含债券一级分类等, "01", "国债"
    ), WIND一级分类_含债券一级分类等_地方政府债(
            BussinessDictCodeRds.WIND一级分类_含债券一级分类等, "02", "地方政府债"
    ), WIND一级分类_含债券一级分类等_央行票据(
            BussinessDictCodeRds.WIND一级分类_含债券一级分类等, "03", "央行票据"
    ), WIND一级分类_含债券一级分类等_国际机构债(
            BussinessDictCodeRds.WIND一级分类_含债券一级分类等, "11", "国际机构债"
    ), WIND一级分类_含债券一级分类等_政府支持机构债(
            BussinessDictCodeRds.WIND一级分类_含债券一级分类等, "12", "政府支持机构债"
    ), WIND一级分类_含债券一级分类等_可转债(
            BussinessDictCodeRds.WIND一级分类_含债券一级分类等, "14", "可转债"
    ), WIND一级分类_含债券一级分类等_可交换债(
            BussinessDictCodeRds.WIND一级分类_含债券一级分类等, "15", "可交换债"
    ), WIND一级分类_含债券一级分类等_可分离转债存债(
            BussinessDictCodeRds.WIND一级分类_含债券一级分类等, "16", "可分离转债存债"
    ), WIND一级分类_含债券一级分类等_优先股(
            BussinessDictCodeRds.WIND一级分类_含债券一级分类等, "17", "优先股"
    ), WIND二级分类_含债券二级分类等_政策银行债(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "01", "政策银行债"
    ), WIND二级分类_含债券二级分类等_商业银行债(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "02", "商业银行债"
    ), WIND二级分类_含债券二级分类等_商业银行次级债券(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "03", "商业银行次级债券"
    ), WIND二级分类_含债券二级分类等_保险公司债(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "04", "保险公司债"
    ), WIND二级分类_含债券二级分类等_证券公司债(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "05", "证券公司债"
    ), WIND二级分类_含债券二级分类等_证券公司短期融资券(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "06", "证券公司短期融资券"
    ), WIND二级分类_含债券二级分类等_其它金融机构债(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "07", "其它金融机构债"
    ), WIND二级分类_含债券二级分类等_一般企业债(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "08", "一般企业债"
    ), WIND二级分类_含债券二级分类等_集合企业债(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "09", "集合企业债"
    ), WIND二级分类_含债券二级分类等_一般公司债(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "10", "一般公司债"
    ), WIND二级分类_含债券二级分类等_私募债(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "11", "私募债"
    ), WIND二级分类_含债券二级分类等_一般中期票据(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "12", "一般中期票据"
    ), WIND二级分类_含债券二级分类等_集合票据(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "13", "集合票据"
    ), WIND二级分类_含债券二级分类等_超短期融资债券(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "14", "超短期融资债券"
    ), WIND二级分类_含债券二级分类等_一般短期融资券(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "15", "一般短期融资券"
    ), WIND二级分类_含债券二级分类等_证监会主管ABS(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "16", "证监会主管ABS"
    ), WIND二级分类_含债券二级分类等_银监会主管ABS(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "17", "银监会主管ABS"
    ), WIND二级分类_含债券二级分类等_交易商协会ABN(
            BussinessDictCodeRds.WIND二级分类_含债券二级分类等, "18", "交易商协会ABN"
    ),
     长期短期_长期(
            BussinessDictCodeRds.长期短期, "01", "长期"
    ), 长期短期_短期(
            BussinessDictCodeRds.长期短期, "02", "短期"
    ), 评级结果_AAAPLUS(
            BussinessDictCodeRds.评级结果, "01", "AAA+"
    ), 评级结果_AAA(
            BussinessDictCodeRds.评级结果, "02", "AAA"
    ), 评级结果_AAADAWN(
            BussinessDictCodeRds.评级结果, "03", "AAA-"
    ), 评级结果_AAPLUS(
            BussinessDictCodeRds.评级结果, "04", "AA+"
    ), 评级结果_AA(
            BussinessDictCodeRds.评级结果, "05", "AA"
    ), 评级结果_AADAWN(
            BussinessDictCodeRds.评级结果, "06", "AA-"
    ), 评级结果_APLUS(
            BussinessDictCodeRds.评级结果, "07", "A+"
    ), 评级结果_A(
            BussinessDictCodeRds.评级结果, "08", "A"
    ), 评级结果_ADAWN(
            BussinessDictCodeRds.评级结果, "09", "A-"
    ), 评级结果_ADAWN1(
            BussinessDictCodeRds.评级结果, "10", "A-1"
    ), 评级结果_ADAWN2(
            BussinessDictCodeRds.评级结果, "11", "A-2"
    ), 评级结果_ADAWN3(
            BussinessDictCodeRds.评级结果, "12", "A-3"
    ), 评级结果_BBBPLUS(
            BussinessDictCodeRds.评级结果, "13", "BBB+"
    ), 评级结果_BBB(
            BussinessDictCodeRds.评级结果, "14", "BBB"
    ), 评级结果_BBBDAWN(
            BussinessDictCodeRds.评级结果, "15", "BBB-"
    ), 评级结果_BBPLUS(
            BussinessDictCodeRds.评级结果, "16", "BB+"
    ), 评级结果_BB(
            BussinessDictCodeRds.评级结果, "17", "BB"
    ), 评级结果_BBDAWN(
            BussinessDictCodeRds.评级结果, "18", "BB-"
    ), 评级结果_BPLUS(
            BussinessDictCodeRds.评级结果, "19", "B+"
    ), 评级结果_B(
            BussinessDictCodeRds.评级结果, "20", "B"
    ), 评级结果_BDAWN(
            BussinessDictCodeRds.评级结果, "21", "B-"
    ), 评级结果_CCCPLUS(
            BussinessDictCodeRds.评级结果, "22", "CCC+"
    ), 评级结果_CCC(
            BussinessDictCodeRds.评级结果, "23", "CCC"
    ), 评级结果_CCCDAWN(
            BussinessDictCodeRds.评级结果, "24", "CCC-"
    ), 评级结果_CCPLUS(
            BussinessDictCodeRds.评级结果, "25", "CC+"
    ), 评级结果_CC(
            BussinessDictCodeRds.评级结果, "26", "CC"
    ), 评级结果_CCDAWN(
            BussinessDictCodeRds.评级结果, "27", "CC-"
    ), 评级结果_CPLUS(
            BussinessDictCodeRds.评级结果, "28", "C+"
    ), 评级结果_C(
            BussinessDictCodeRds.评级结果, "29", "C"
    ), 评级结果_CDAWN(
            BussinessDictCodeRds.评级结果, "30", "C-"
    ), 评级结果_D(
            BussinessDictCodeRds.评级结果, "31", "D"
    ), 存款类资产存款类型_存放本行(
            BussinessDictCodeRds.存款类资产存款类型, "01", "存放本行"
    ), 存款类资产存款类型_存放他行(
            BussinessDictCodeRds.存款类资产存款类型, "02", "存放他行"
    ),

    业务编号类型_UUID(
            BussinessDictCodeRds.业务编号类型, "01", "UUID"
    ), 业务编号类型_序列号(
            BussinessDictCodeRds.业务编号类型, "02", "序列号"
    ), 业务编号类型_前缀_后缀(
            BussinessDictCodeRds.业务编号类型, "03", "前缀+后缀"
    ), 业务编号类型_前缀_日期_后缀(
            BussinessDictCodeRds.业务编号类型, "04", "前缀+日期+后缀"
    ), 业务编号类型_独特规则(
            BussinessDictCodeRds.业务编号类型, "05", "独特规则"
    ), 日期规则_系统日期_AP服务器日期(
            BussinessDictCodeRds.日期规则, "02", "系统日期（AP服务器日期）"
    ), 日期规则_系统日期_DB日期(
            BussinessDictCodeRds.日期规则, "03", "系统日期（DB日期）"
    ), 共通业务类型_机构管理(
            BussinessDictCodeRds.共通业务类型, "S05", "机构管理"
    ), 共通业务类型_角色分配管理(
            BussinessDictCodeRds.共通业务类型, "S06", "角色分配管理"
    ), 共通业务类型_角色权限管理(
            BussinessDictCodeRds.共通业务类型, "S07", "角色权限管理"
    ), 共通业务类型_代办人管理(
            BussinessDictCodeRds.共通业务类型, "S08", "代办人管理"
    ), 共通业务类型_系统参数管理(
            BussinessDictCodeRds.共通业务类型, "S09", "系统参数管理"
    ), 共通业务类型_资金后台管理(
            BussinessDictCodeRds.共通业务类型, "STL01", "资金后台管理"
    ), 共通业务类型_市场日历信息(
            BussinessDictCodeRds.共通业务类型, "MST01", "市场日历信息"
    ), 共通业务类型_债券估值行情信息(
            BussinessDictCodeRds.共通业务类型, "MST02", "债券估值行情信息"
    ), 共通业务类型_基金类估值行情信息(
            BussinessDictCodeRds.共通业务类型, "MST03", "基金类估值行情信息"
    ), 共通业务类型_计划类资产行情信息(
            BussinessDictCodeRds.共通业务类型, "MST04", "计划类资产行情信息"
    ), 共通业务类型_汇率行情信息(
            BussinessDictCodeRds.共通业务类型, "MST05", "汇率行情信息"
    ), 共通业务类型_基准利率行情(
            BussinessDictCodeRds.共通业务类型, "MST06", "基准利率行情"
    ), 共通业务类型_指数行情信息(
            BussinessDictCodeRds.共通业务类型, "MST07", "指数行情信息"
    ), 共通业务类型_收益率曲线信息(
            BussinessDictCodeRds.共通业务类型, "MST08", "收益率曲线信息"
    ), 共通业务类型_资金账户管理(
            BussinessDictCodeRds.共通业务类型, "MST09", "资金账户管理"
    ), 共通业务类型_证券账户管理(
            BussinessDictCodeRds.共通业务类型, "MST10", "证券账户管理"
    ), 共通业务类型_业绩基准管理(
            BussinessDictCodeRds.共通业务类型, "MST11", "业绩基准管理"
    ), 共通业务类型_组合管理(
            BussinessDictCodeRds.共通业务类型, "RSK32", "组合管理"
    ), 共通业务类型_用户组合管理(
            BussinessDictCodeRds.共通业务类型, "PTL02", "用户组合管理"
    ), 共通业务类型_风险试算(
            BussinessDictCodeRds.共通业务类型, "RSK01", "风险试算"
    ), 共通业务类型_限额指标参数管理(
            BussinessDictCodeRds.共通业务类型, "RSK06", "限额指标参数管理"
    ), 共通业务类型_限额指标表达式管理(
            BussinessDictCodeRds.共通业务类型, "RSK07", "限额指标表达式管理"
    ), 共通业务类型_限额监督指标管理(
            BussinessDictCodeRds.共通业务类型, "RSK08", "限额监督指标管理"
    ), 共通业务类型_限额组合关联监督指标管理(
            BussinessDictCodeRds.共通业务类型, "RSK09", "限额组合关联监督指标管理"
    ), 共通业务类型_语言参数管理(
            BussinessDictCodeRds.共通业务类型, "RSK27", "语言参数管理"
    ), 共通业务类型_固定值参数管理(
            BussinessDictCodeRds.共通业务类型, "RSK28", "固定值参数管理"
    ), 共通业务类型_自定义参数管理(
            BussinessDictCodeRds.共通业务类型, "RSK33", "自定义参数管理"
    ), 共通业务类型_REDIS功能参数配置(
            BussinessDictCodeRds.共通业务类型, "RSK36", "REDIS功能参数配置"
    ), 共通业务类型_报表规则定义管理(
            BussinessDictCodeRds.共通业务类型, "S01", "报表规则定义管理"
    ), 共通业务类型_用户管理(
            BussinessDictCodeRds.共通业务类型, "S02", "用户管理"
    ), 共通业务类型_报表面板配置管理(
            BussinessDictCodeRds.共通业务类型, "S03", "报表面板配置管理"
    ), 共通业务类型_部门管理(
            BussinessDictCodeRds.共通业务类型, "S04", "部门管理"
    ), 基金类二级资产类别_中债_债券基金(
            BussinessDictCodeRds.基金类二级资产类别_中债, "01", "债券基金"
    ), 基金类二级资产类别_中债_货币市场基金(
            BussinessDictCodeRds.基金类二级资产类别_中债, "02", "货币市场基金"
    ), 基金类二级资产类别_中债_股票基金(
            BussinessDictCodeRds.基金类二级资产类别_中债, "03", "股票基金"
    ), 基金类二级资产类别_中债_基金中基金(
            BussinessDictCodeRds.基金类二级资产类别_中债, "04", "基金中基金"
    ), 基金类二级资产类别_中债_混合基金(
            BussinessDictCodeRds.基金类二级资产类别_中债, "05", "混合基金"
    ), 基金类二级资产类别_中债_其他公募基金(
            BussinessDictCodeRds.基金类二级资产类别_中债, "07", "其他公募基金"
    ), 结构性存款挂钩标的类别_中债_利率(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "01", "利率"
    ), 结构性存款挂钩标的类别_中债_汇率(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "02", "汇率"
    ), 结构性存款挂钩标的类别_中债_股票(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "03", "股票"
    ), 结构性存款挂钩标的类别_中债_农产品(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "04", "农产品"
    ), 结构性存款挂钩标的类别_中债_大宗商品(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "05", "大宗商品"
    ), 结构性存款挂钩标的类别_中债_贵金属(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "06", "贵金属"
    ), 结构性存款挂钩标的类别_中债_债券(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "07", "债券"
    ), 结构性存款挂钩标的类别_中债_指数(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "08", "指数"
    ), 结构性存款挂钩标的类别_中债_基金(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "09", "基金"
    ), 结构性存款挂钩标的类别_中债_另类资产(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "10", "另类资产"
    ), 结构性存款挂钩标的类别_中债_期货(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "11", "期货"
    ), 结构性存款挂钩标的类别_中债_期权(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "12", "期权"
    ), 结构性存款挂钩标的类别_中债_远期(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "13", "远期"
    ), 结构性存款挂钩标的类别_中债_互换(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "14", "互换"
    ), 结构性存款挂钩标的类别_中债_复合金融衍生品(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "15", "复合金融衍生品"
    ), 结构性存款挂钩标的类别_中债_其它(
            BussinessDictCodeRds.结构性存款挂钩标的类别_中债, "99", "其它"
    ),
     文件类型_投管_产品成立公告_横表(
            BussinessDictCodeRds.文件类型_投管, "01", "产品成立公告（横表）"
    ), 文件类型_投管_产品成立公告_竖表(
            BussinessDictCodeRds.文件类型_投管, "02", "产品成立公告（竖表）"
    ), 文件类型_投管_产品到期公告_横表(
            BussinessDictCodeRds.文件类型_投管, "03", "产品到期公告（横表）"
    ), 文件类型_投管_产品到期公告_竖表(
            BussinessDictCodeRds.文件类型_投管, "04", "产品到期公告（竖表）"
    ), 附件类型_理财产品可行性评估报告(
            BussinessDictCodeRds.附件类型, "02", "理财产品可行性评估报告"
    ), 附件类型_内部审核文件(
            BussinessDictCodeRds.附件类型, "03", "内部审核文件"
    ), 附件类型_相关人员尽职调查文件(
            BussinessDictCodeRds.附件类型, "04", "相关人员尽职调查文件"
    ), 附件类型_相关方签署的法律文件(
            BussinessDictCodeRds.附件类型, "05", "相关方签署的法律文件"
    ), 附件类型_理财销售文件(
            BussinessDictCodeRds.附件类型, "06", "理财销售文件"
    ), 附件类型_理财产品说明书(
            BussinessDictCodeRds.附件类型, "07", "理财产品说明书"
    ), 附件类型_理财产品宣传材料(
            BussinessDictCodeRds.附件类型, "08", "理财产品宣传材料"
    ), 附件类型_其他材料(
            BussinessDictCodeRds.附件类型, "09", "其他材料"
    ), 产品收益标识_保证收益(
            BussinessDictCodeRds.产品收益标识, "01", "保证收益"
    ), 产品收益标识_保本浮动收益(
            BussinessDictCodeRds.产品收益标识, "02", "保本浮动收益"
    ), 投资性质_权益类(
            BussinessDictCodeRds.投资性质, "02", "权益类"
    ), 投资性质_商品及金融衍生品类(
            BussinessDictCodeRds.投资性质, "03", "商品及金融衍生品类"
    ), 投资性质_混合类(
            BussinessDictCodeRds.投资性质, "04", "混合类"
    ), 尾数处理方式_四舍五入(
            BussinessDictCodeRds.尾数处理方式, "01", "四舍五入"
    ), 尾数处理方式_去尾(
            BussinessDictCodeRds.尾数处理方式, "02", "去尾"
    ), 目标客户类型_私人银行客户专属(
            BussinessDictCodeRds.目标客户类型, "01", "私人银行客户专属"
    ), 目标客户类型_机构客户专属(
            BussinessDictCodeRds.目标客户类型, "02", "机构客户专属"
    ), 目标客户类型_金融同业专属(
            BussinessDictCodeRds.目标客户类型, "04", "金融同业专属"
    ), 目标客户类型_一般个人客户(
            BussinessDictCodeRds.目标客户类型, "05", "一般个人客户"
    ), 目标客户类型_高资产净值客户(
            BussinessDictCodeRds.目标客户类型, "06", "高资产净值客户"
    ), 交易方向_应收款项转入(
            BussinessDictCodeRds.交易方向, "T25", "应收款项转入"
    ), 交易方向_应收款项转出(
            BussinessDictCodeRds.交易方向, "T26", "应收款项转出"
    ), 交易方向_债券派息(
            BussinessDictCodeRds.交易方向, "T27", "债券派息"
    ),

    利率行情_个人活期(
            BussinessDictCodeRds.利率行情, "01010100", "个人活期"
    ), 利率行情_个人定期_整存整取_三个月(
            BussinessDictCodeRds.利率行情, "01010201", "个人定期(整存整取)三个月"
    ), 利率行情_个人定期_整存整取_半年(
            BussinessDictCodeRds.利率行情, "01010202", "个人定期(整存整取)半年"
    ), 利率行情_个人定期_整存整取_一年(
            BussinessDictCodeRds.利率行情, "01010203", "个人定期(整存整取)一年"
    ), 利率行情_个人定期_整存整取_二年(
            BussinessDictCodeRds.利率行情, "01010204", "个人定期(整存整取)二年"
    ), 利率行情_个人定期_整存整取_三年(
            BussinessDictCodeRds.利率行情, "01010205", "个人定期(整存整取)三年"
    ), 利率行情_个人定期_整存整取_五年(
            BussinessDictCodeRds.利率行情, "01010206", "个人定期(整存整取)五年"
    ), 利率行情_个人定期_整存整取_八年及以上(
            BussinessDictCodeRds.利率行情, "01010207", "个人定期(整存整取)八年及以上"
    ), 利率行情_个人定期_零存整取_整存零取_存本取息_一年(
            BussinessDictCodeRds.利率行情, "01010301", "个人定期(零存整取,整存零取,存本取息)一年"
    ), 利率行情_个人定期_零存整取_整存零取_存本取息_三年(
            BussinessDictCodeRds.利率行情, "01010302", "个人定期(零存整取,整存零取,存本取息)三年"
    ), 利率行情_个人定期_零存整取_整存零取_存本取息_五年(
            BussinessDictCodeRds.利率行情, "01010303", "个人定期(零存整取,整存零取,存本取息)五年"
    ), 利率行情_协定存款(
            BussinessDictCodeRds.利率行情, "01010600", "协定存款"
    ), 利率行情_通知存款一天(
            BussinessDictCodeRds.利率行情, "01010701", "通知存款一天"
    ), 利率行情_通知存款七天(
            BussinessDictCodeRds.利率行情, "01010702", "通知存款七天"
    ), 利率行情_个人住房公积金贷款5年以下_含5年(
            BussinessDictCodeRds.利率行情, "01010801", "个人住房公积金贷款5年以下(含5年)"
    ), 利率行情_个人住房公积金贷款5年以上(
            BussinessDictCodeRds.利率行情, "01010802", "个人住房公积金贷款5年以上"
    ), 利率行情_短期贷款六个月(
            BussinessDictCodeRds.利率行情, "02020206", "短期贷款六个月"
    ), 利率行情_短期贷款一年(
            BussinessDictCodeRds.利率行情, "02020207", "短期贷款一年"
    ), 利率行情_中长期贷款一至三年_含(
            BussinessDictCodeRds.利率行情, "02020208", "中长期贷款一至三年(含)"
    ), 利率行情_中长期贷款三至五年_含(
            BussinessDictCodeRds.利率行情, "02020209", "中长期贷款三至五年(含)"
    ), 利率行情_中长期贷款五年以上(
            BussinessDictCodeRds.利率行情, "02020210", "中长期贷款五年以上"
    ), 利率行情_澳元1月拆借(
            BussinessDictCodeRds.利率行情, "AUDIBO1M.IB", "澳元1月拆借"
    ), 利率行情_澳元1周拆借(
            BussinessDictCodeRds.利率行情, "AUDIBO1W.IB", "澳元1周拆借"
    ), 利率行情_澳元2月拆借(
            BussinessDictCodeRds.利率行情, "AUDIBO2M.IB", "澳元2月拆借"
    ), 利率行情_澳元2周拆借(
            BussinessDictCodeRds.利率行情, "AUDIBO2W.IB", "澳元2周拆借"
    ), 利率行情_澳元3月拆借(
            BussinessDictCodeRds.利率行情, "AUDIBO3M.IB", "澳元3月拆借"
    ), 利率行情_澳元次日拆借(
            BussinessDictCodeRds.利率行情, "AUDIBOTN.IB", "澳元次日拆借"
    ), 利率行情_银行间1天质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR001.IB", "银行间1天质押式回购利率"
    ), 利率行情_银行间7天质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR007.IB", "银行间7天质押式回购利率"
    ), 利率行情_银行间14天质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR014.IB", "银行间14天质押式回购利率"
    ), 利率行情_银行间21天质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR021.IB", "银行间21天质押式回购利率"
    ), 利率行情_银行间1个月质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR1M.IB", "银行间1个月质押式回购利率"
    ), 利率行情_银行间1年质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR1Y.IB", "银行间1年质押式回购利率"
    ), 利率行情_银行间2个月质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR2M.IB", "银行间2个月质押式回购利率"
    ), 利率行情_银行间3个月质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR3M.IB", "银行间3个月质押式回购利率"
    ), 利率行情_银行间4个月质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR4M.IB", "银行间4个月质押式回购利率"
    ), 利率行情_银行间6个月质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR6M.IB", "银行间6个月质押式回购利率"
    ), 利率行情_银行间9个月质押式回购利率(
            BussinessDictCodeRds.利率行情, "DR9M.IB", "银行间9个月质押式回购利率"
    ), 利率行情_欧元1月拆借(
            BussinessDictCodeRds.利率行情, "EURIBO1M.IB", "欧元1月拆借"
    ), 利率行情_欧元1周拆借(
            BussinessDictCodeRds.利率行情, "EURIBO1W.IB", "欧元1周拆借"
    ), 利率行情_欧元1年拆借(
            BussinessDictCodeRds.利率行情, "EURIBO1Y.IB", "欧元1年拆借"
    ), 利率行情_欧元2月拆借(
            BussinessDictCodeRds.利率行情, "EURIBO2M.IB", "欧元2月拆借"
    ), 利率行情_欧元2周拆借(
            BussinessDictCodeRds.利率行情, "EURIBO2W.IB", "欧元2周拆借"
    ), 利率行情_欧元3月拆借(
            BussinessDictCodeRds.利率行情, "EURIBO3M.IB", "欧元3月拆借"
    ), 利率行情_欧元3周拆借(
            BussinessDictCodeRds.利率行情, "EURIBO3W.IB", "欧元3周拆借"
    ), 利率行情_欧元6月拆借(
            BussinessDictCodeRds.利率行情, "EURIBO6M.IB", "欧元6月拆借"
    ), 利率行情_欧元9月拆借(
            BussinessDictCodeRds.利率行情, "EURIBO9M.IB", "欧元9月拆借"
    ), 利率行情_欧元隔夜拆借(
            BussinessDictCodeRds.利率行情, "EURIBOON.IB", "欧元隔夜拆借"
    ), 利率行情_欧元次日拆借(
            BussinessDictCodeRds.利率行情, "EURIBOTN.IB", "欧元次日拆借"
    ), 利率行情_港元1月拆借(
            BussinessDictCodeRds.利率行情, "HKDIBO1M.IB", "港元1月拆借"
    ), 利率行情_港元1周拆借(
            BussinessDictCodeRds.利率行情, "HKDIBO1W.IB", "港元1周拆借"
    ), 利率行情_港元2月拆借(
            BussinessDictCodeRds.利率行情, "HKDIBO2M.IB", "港元2月拆借"
    ), 利率行情_港元2周拆借(
            BussinessDictCodeRds.利率行情, "HKDIBO2W.IB", "港元2周拆借"
    ), 利率行情_港元3月拆借(
            BussinessDictCodeRds.利率行情, "HKDIBO3M.IB", "港元3月拆借"
    ), 利率行情_港元3周拆借(
            BussinessDictCodeRds.利率行情, "HKDIBO3W.IB", "港元3周拆借"
    ), 利率行情_港元6月拆借(
            BussinessDictCodeRds.利率行情, "HKDIBO6M.IB", "港元6月拆借"
    ), 利率行情_港元9月拆借(
            BussinessDictCodeRds.利率行情, "HKDIBO9M.IB", "港元9月拆借"
    ), 利率行情_港元隔夜拆借(
            BussinessDictCodeRds.利率行情, "HKDIBOON.IB", "港元隔夜拆借"
    ), 利率行情_港元次日拆借(
            BussinessDictCodeRds.利率行情, "HKDIBOTN.IB", "港元次日拆借"
    ), 利率行情_银行间1天同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO001.IB", "银行间1天同业拆借利率"
    ), 利率行情_银行间7天同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO007.IB", "银行间7天同业拆借利率"
    ), 利率行情_银行间14天同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO014.IB", "银行间14天同业拆借利率"
    ), 利率行情_银行间21天同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO021.IB", "银行间21天同业拆借利率"
    ), 利率行情_银行间1个月同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO1M.IB", "银行间1个月同业拆借利率"
    ), 利率行情_银行间1年同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO1Y.IB", "银行间1年同业拆借利率"
    ), 利率行情_银行间2个月同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO2M.IB", "银行间2个月同业拆借利率"
    ), 利率行情_银行间3个月同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO3M.IB", "银行间3个月同业拆借利率"
    ), 利率行情_银行间4个月同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO4M.IB", "银行间4个月同业拆借利率"
    ), 利率行情_银行间6个月同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO6M.IB", "银行间6个月同业拆借利率"
    ), 利率行情_银行间9个月同业拆借利率(
            BussinessDictCodeRds.利率行情, "IBO9M.IB", "银行间9个月同业拆借利率"
    ), 利率行情_日元1月拆借(
            BussinessDictCodeRds.利率行情, "JPYIBO1M.IB", "日元1月拆借"
    ), 利率行情_日元1周拆借(
            BussinessDictCodeRds.利率行情, "JPYIBO1W.IB", "日元1周拆借"
    ), 利率行情_日元2月拆借(
            BussinessDictCodeRds.利率行情, "JPYIBO2M.IB", "日元2月拆借"
    ), 利率行情_日元2周拆借(
            BussinessDictCodeRds.利率行情, "JPYIBO2W.IB", "日元2周拆借"
    ), 利率行情_日元3月拆借(
            BussinessDictCodeRds.利率行情, "JPYIBO3M.IB", "日元3月拆借"
    ), 利率行情_日元6月拆借(
            BussinessDictCodeRds.利率行情, "JPYIBO6M.IB", "日元6月拆借"
    ), 利率行情_日元隔夜拆借(
            BussinessDictCodeRds.利率行情, "JPYIBOON.IB", "日元隔夜拆借"
    ), 利率行情_日元次日拆借(
            BussinessDictCodeRds.利率行情, "JPYIBOTN.IB", "日元次日拆借"
    ), 利率行情_银行间债券借贷1天(
            BussinessDictCodeRds.利率行情, "L001.IB", "银行间债券借贷1天"
    ), 利率行情_银行间债券借贷7天(
            BussinessDictCodeRds.利率行情, "L007.IB", "银行间债券借贷7天"
    ), 利率行情_银行间债券借贷14天(
            BussinessDictCodeRds.利率行情, "L014.IB", "银行间债券借贷14天"
    ), 利率行情_银行间债券借贷21天(
            BussinessDictCodeRds.利率行情, "L021.IB", "银行间债券借贷21天"
    ), 利率行情_银行间债券借贷1个月(
            BussinessDictCodeRds.利率行情, "L1M.IB", "银行间债券借贷1个月"
    ), 利率行情_银行间债券借贷1年(
            BussinessDictCodeRds.利率行情, "L1Y.IB", "银行间债券借贷1年"
    ), 利率行情_银行间债券借贷2个月(
            BussinessDictCodeRds.利率行情, "L2M.IB", "银行间债券借贷2个月"
    ), 利率行情_银行间债券借贷3个月(
            BussinessDictCodeRds.利率行情, "L3M.IB", "银行间债券借贷3个月"
    ), 利率行情_银行间债券借贷4个月(
            BussinessDictCodeRds.利率行情, "L4M.IB", "银行间债券借贷4个月"
    ), 利率行情_银行间债券借贷6个月(
            BussinessDictCodeRds.利率行情, "L6M.IB", "银行间债券借贷6个月"
    ), 利率行情_银行间债券借贷9个月(
            BussinessDictCodeRds.利率行情, "L9M.IB", "银行间债券借贷9个月"
    ), 利率行情_瑞士法郎LIBOR12月(
            BussinessDictCodeRds.利率行情, "LICHF12M.IR", "瑞士法郎LIBOR12月"
    ), 利率行情_瑞士法郎LIBOR1月(
            BussinessDictCodeRds.利率行情, "LICHF1M.IR", "瑞士法郎LIBOR1月"
    ), 利率行情_瑞士法郎LIBOR1周(
            BussinessDictCodeRds.利率行情, "LICHF1W.IR", "瑞士法郎LIBOR1周"
    ), 利率行情_瑞士法郎LIBOR2月(
            BussinessDictCodeRds.利率行情, "LICHF2M.IR", "瑞士法郎LIBOR2月"
    ), 利率行情_瑞士法郎LIBOR3月(
            BussinessDictCodeRds.利率行情, "LICHF3M.IR", "瑞士法郎LIBOR3月"
    ), 利率行情_瑞士法郎LIBOR6月(
            BussinessDictCodeRds.利率行情, "LICHF6M.IR", "瑞士法郎LIBOR6月"
    ), 利率行情_瑞士法郎LIBOR隔夜(
            BussinessDictCodeRds.利率行情, "LICHFON.IR", "瑞士法郎LIBOR隔夜"
    ), 利率行情_欧元LIBOR12月(
            BussinessDictCodeRds.利率行情, "LIEUR12M.IR", "欧元LIBOR12月"
    ), 利率行情_欧元LIBOR1月(
            BussinessDictCodeRds.利率行情, "LIEUR1M.IR", "欧元LIBOR1月"
    ), 利率行情_欧元LIBOR1周(
            BussinessDictCodeRds.利率行情, "LIEUR1W.IR", "欧元LIBOR1周"
    ), 利率行情_欧元LIBOR2月(
            BussinessDictCodeRds.利率行情, "LIEUR2M.IR", "欧元LIBOR2月"
    ), 利率行情_欧元LIBOR3月(
            BussinessDictCodeRds.利率行情, "LIEUR3M.IR", "欧元LIBOR3月"
    ), 利率行情_欧元LIBOR6月(
            BussinessDictCodeRds.利率行情, "LIEUR6M.IR", "欧元LIBOR6月"
    ), 利率行情_欧元LIBOR隔夜(
            BussinessDictCodeRds.利率行情, "LIEURON.IR", "欧元LIBOR隔夜"
    ), 利率行情_英镑LIBOR12月(
            BussinessDictCodeRds.利率行情, "LIGBP12M.IR", "英镑LIBOR12月"
    ), 利率行情_英镑LIBOR1月(
            BussinessDictCodeRds.利率行情, "LIGBP1M.IR", "英镑LIBOR1月"
    ), 利率行情_英镑LIBOR1周(
            BussinessDictCodeRds.利率行情, "LIGBP1W.IR", "英镑LIBOR1周"
    ), 利率行情_英镑LIBOR2月(
            BussinessDictCodeRds.利率行情, "LIGBP2M.IR", "英镑LIBOR2月"
    ), 利率行情_英镑LIBOR3月(
            BussinessDictCodeRds.利率行情, "LIGBP3M.IR", "英镑LIBOR3月"
    ), 利率行情_英镑LIBOR6月(
            BussinessDictCodeRds.利率行情, "LIGBP6M.IR", "英镑LIBOR6月"
    ), 利率行情_英镑LIBOR隔夜(
            BussinessDictCodeRds.利率行情, "LIGBPON.IR", "英镑LIBOR隔夜"
    ), 利率行情_日元LIBOR12月(
            BussinessDictCodeRds.利率行情, "LIJPY12M.IR", "日元LIBOR12月"
    ), 利率行情_日元LIBOR1月(
            BussinessDictCodeRds.利率行情, "LIJPY1M.IR", "日元LIBOR1月"
    ), 利率行情_日元LIBOR1周(
            BussinessDictCodeRds.利率行情, "LIJPY1W.IR", "日元LIBOR1周"
    ), 利率行情_日元LIBOR2月(
            BussinessDictCodeRds.利率行情, "LIJPY2M.IR", "日元LIBOR2月"
    ), 利率行情_日元LIBOR3月(
            BussinessDictCodeRds.利率行情, "LIJPY3M.IR", "日元LIBOR3月"
    ), 利率行情_日元LIBOR6月(
            BussinessDictCodeRds.利率行情, "LIJPY6M.IR", "日元LIBOR6月"
    ), 利率行情_日元LIBOR隔夜(
            BussinessDictCodeRds.利率行情, "LIJPYON.IR", "日元LIBOR隔夜"
    ), 利率行情_美元LIBOR12月(
            BussinessDictCodeRds.利率行情, "LIUSD12M.IR", "美元LIBOR12月"
    ), 利率行情_美元LIBOR1月(
            BussinessDictCodeRds.利率行情, "LIUSD1M.IR", "美元LIBOR1月"
    ), 利率行情_美元LIBOR1周(
            BussinessDictCodeRds.利率行情, "LIUSD1W.IR", "美元LIBOR1周"
    ), 利率行情_美元LIBOR2月(
            BussinessDictCodeRds.利率行情, "LIUSD2M.IR", "美元LIBOR2月"
    ), 利率行情_美元LIBOR3月(
            BussinessDictCodeRds.利率行情, "LIUSD3M.IR", "美元LIBOR3月"
    ), 利率行情_美元LIBOR6月(
            BussinessDictCodeRds.利率行情, "LIUSD6M.IR", "美元LIBOR6月"
    ), 利率行情_美元LIBOR隔夜(
            BussinessDictCodeRds.利率行情, "LIUSDON.IR", "美元LIBOR隔夜"
    ), 利率行情_银买断1日(
            BussinessDictCodeRds.利率行情, "OR001.IB", "银买断1日"
    ), 利率行情_银买断7日(
            BussinessDictCodeRds.利率行情, "OR007.IB", "银买断7日"
    ), 利率行情_银买断14日(
            BussinessDictCodeRds.利率行情, "OR014.IB", "银买断14日"
    ), 利率行情_银买断21日(
            BussinessDictCodeRds.利率行情, "OR021.IB", "银买断21日"
    ), 利率行情_银买断1月(
            BussinessDictCodeRds.利率行情, "OR1M.IB", "银买断1月"
    ), 利率行情_银买断2月(
            BussinessDictCodeRds.利率行情, "OR2M.IB", "银买断2月"
    ), 利率行情_银买断3月(
            BussinessDictCodeRds.利率行情, "OR3M.IB", "银买断3月"
    ), 利率行情_银存间质押1日(
            BussinessDictCodeRds.利率行情, "R001.IB", "银存间质押1日"
    ), 利率行情_银存间质押7日(
            BussinessDictCodeRds.利率行情, "R007.IB", "银存间质押7日"
    ), 利率行情_银存间质押14日(
            BussinessDictCodeRds.利率行情, "R014.IB", "银存间质押14日"
    ), 利率行情_银存间质押21日(
            BussinessDictCodeRds.利率行情, "R021.IB", "银存间质押21日"
    ), 利率行情_银存间质押1月(
            BussinessDictCodeRds.利率行情, "R1M.IB", "银存间质押1月"
    ), 利率行情_银存间质押1年(
            BussinessDictCodeRds.利率行情, "R1Y.IB", "银存间质押1年"
    ), 利率行情_银存间质押3月(
            BussinessDictCodeRds.利率行情, "R3M.IB", "银存间质押3月"
    ), 利率行情_银存间质押4月(
            BussinessDictCodeRds.利率行情, "R4M.IB", "银存间质押4月"
    ), 利率行情_银存间质押6月(
            BussinessDictCodeRds.利率行情, "R6M.IB", "银存间质押6月"
    ), 利率行情_银存间质押9月(
            BussinessDictCodeRds.利率行情, "R9M.IB", "银存间质押9月"
    ), 利率行情_SHIBOR1月(
            BussinessDictCodeRds.利率行情, "SHIBOR1M.IR", "SHIBOR1月"
    ), 利率行情_SHIBOR1周(
            BussinessDictCodeRds.利率行情, "SHIBOR1W.IR", "SHIBOR1周"
    ), 利率行情_SHIBOR1年(
            BussinessDictCodeRds.利率行情, "SHIBOR1Y.IR", "SHIBOR1年"
    ), 利率行情_SHIBOR2周(
            BussinessDictCodeRds.利率行情, "SHIBOR2W.IR", "SHIBOR2周"
    ), 利率行情_SHIBOR3月(
            BussinessDictCodeRds.利率行情, "SHIBOR3M.IR", "SHIBOR3月"
    ), 利率行情_SHIBOR6月(
            BussinessDictCodeRds.利率行情, "SHIBOR6M.IR", "SHIBOR6月"
    ), 利率行情_SHIBOR9月(
            BussinessDictCodeRds.利率行情, "SHIBOR9M.IR", "SHIBOR9月"
    ), 利率行情_SHIBOR隔夜(
            BussinessDictCodeRds.利率行情, "SHIBORON.IR", "SHIBOR隔夜"
    ), 利率行情_美元1月拆借(
            BussinessDictCodeRds.利率行情, "USDIBO1M.IB", "美元1月拆借"
    ), 利率行情_美元1周拆借(
            BussinessDictCodeRds.利率行情, "USDIBO1W.IB", "美元1周拆借"
    ), 利率行情_美元2周拆借(
            BussinessDictCodeRds.利率行情, "USDIBO2W.IB", "美元2周拆借"
    ), 利率行情_美元3月拆借(
            BussinessDictCodeRds.利率行情, "USDIBO3M.IB", "美元3月拆借"
    ), 利率行情_美元3周拆借(
            BussinessDictCodeRds.利率行情, "USDIBO3W.IB", "美元3周拆借"
    ), 利率行情_美元6月拆借(
            BussinessDictCodeRds.利率行情, "USDIBO6M.IB", "美元6月拆借"
    ), 利率行情_美元9月拆借(
            BussinessDictCodeRds.利率行情, "USDIBO9M.IB", "美元9月拆借"
    ), 利率行情_美元隔夜拆借(
            BussinessDictCodeRds.利率行情, "USDIBOON.IB", "美元隔夜拆借"
    ), 利率行情_美元1日拆借(
            BussinessDictCodeRds.利率行情, "USDIBOSN.IB", "美元1日拆借"
    ), 利率行情_美元次日拆借(
            BussinessDictCodeRds.利率行情, "USDIBOTN.IB", "美元次日拆借"
    ), 指数行情_中国债券指数(
            BussinessDictCodeRds.指数行情, "01", "中国债券指数"
    ), 指数行情_中国A股指数(
            BussinessDictCodeRds.指数行情, "02", "中国A股指数"
    ), 利率行情_美元1年拆借(
            BussinessDictCodeRds.利率行情, "USDIBO1Y.IB", "美元1年拆借"
    ), 利率行情_美元2月拆借(
            BussinessDictCodeRds.利率行情, "USDIBO2M.IB", "美元2月拆借"
    ), 收益率曲线类型_SP即期(
            BussinessDictCodeRds.收益率曲线类型, "01", "SP即期"
    ), 收益率曲线类型_MA远期(
            BussinessDictCodeRds.收益率曲线类型, "02", "MA远期"
    ), 收益率曲线类型_FS远期的即期(
            BussinessDictCodeRds.收益率曲线类型, "03", "FS远期的即期"
    ), 收益率曲线类型_FM远期的到期(
            BussinessDictCodeRds.收益率曲线类型, "04", "FM远期的到期"
    ), 收益率曲线_中债登收益率曲线(
            BussinessDictCodeRds.收益率曲线, "01", "中债登收益率曲线"
    ), 收益率曲线_中国债权上海清算所收益率曲线(
            BussinessDictCodeRds.收益率曲线, "02", "中国债权上海清算所收益率曲线"
    ), 委托状态_新建(
            BussinessDictCodeRds.委托状态, "01", "新建"
    ), 委托状态_待接收(
            BussinessDictCodeRds.委托状态, "02", "待接收"
    ), 委托状态_已接收(
            BussinessDictCodeRds.委托状态, "03", "已接收"
    ), 委托状态_已完成(
            BussinessDictCodeRds.委托状态, "04", "已完成"
    ), 委托状态_已退回(
            BussinessDictCodeRds.委托状态, "05", "已退回"
    ), 委托状态_已关闭(
            BussinessDictCodeRds.委托状态, "06", "已关闭"
    ), 委托状态_已撤回(
            BussinessDictCodeRds.委托状态, "07", "已撤回"
    ),

    债券类型_国债(
            BussinessDictCodeRds.债券类型, "001", "国债"
    ), 债券类型_地方政府债(
            BussinessDictCodeRds.债券类型, "002", "地方政府债"
    ), 债券类型_央行票据(
            BussinessDictCodeRds.债券类型, "003", "央行票据"
    ), 债券类型_同业存单(
            BussinessDictCodeRds.债券类型, "004", "同业存单"
    ), 债券类型_政策银行债(
            BussinessDictCodeRds.债券类型, "005", "政策银行债"
    ), 债券类型_商业银行债(
            BussinessDictCodeRds.债券类型, "006", "商业银行债"
    ), 债券类型_商业银行次级债券(
            BussinessDictCodeRds.债券类型, "007", "商业银行次级债券"
    ), 债券类型_保险公司债(
            BussinessDictCodeRds.债券类型, "008", "保险公司债"
    ), 债券类型_证券公司债(
            BussinessDictCodeRds.债券类型, "009", "证券公司债"
    ), 债券类型_证券公司短期融资券(
            BussinessDictCodeRds.债券类型, "010", "证券公司短期融资券"
    ), 债券类型_其它金融机构债(
            BussinessDictCodeRds.债券类型, "011", "其它金融机构债"
    ), 债券类型_一般企业债(
            BussinessDictCodeRds.债券类型, "012", "一般企业债"
    ), 债券类型_集合企业债(
            BussinessDictCodeRds.债券类型, "013", "集合企业债"
    ), 债券类型_一般公司债(
            BussinessDictCodeRds.债券类型, "014", "一般公司债"
    ), 债券类型_私募债(
            BussinessDictCodeRds.债券类型, "015", "私募债"
    ), 债券类型_一般中期票据(
            BussinessDictCodeRds.债券类型, "016", "一般中期票据"
    ), 债券类型_集合票据(
            BussinessDictCodeRds.债券类型, "017", "集合票据"
    ), 债券类型_超短期融资债券(
            BussinessDictCodeRds.债券类型, "018", "超短期融资债券"
    ), 债券类型_一般短期融资券(
            BussinessDictCodeRds.债券类型, "019", "一般短期融资券"
    ), 债券类型_短期融资券(
            BussinessDictCodeRds.债券类型, "020", "短期融资券"
    ), 债券类型_定向工具(
            BussinessDictCodeRds.债券类型, "021", "定向工具"
    ), 债券类型_国际机构债(
            BussinessDictCodeRds.债券类型, "022", "国际机构债"
    ), 债券类型_政府支持机构债(
            BussinessDictCodeRds.债券类型, "023", "政府支持机构债"
    ), 债券类型_证监会主管ABS(
            BussinessDictCodeRds.债券类型, "024", "证监会主管ABS"
    ), 债券类型_银监会主管ABS(
            BussinessDictCodeRds.债券类型, "025", "银监会主管ABS"
    ), 债券类型_交易商协会ABN(
            BussinessDictCodeRds.债券类型, "026", "交易商协会ABN"
    ), 债券类型_RMBS(
            BussinessDictCodeRds.债券类型, "027", "RMBS"
    ), 债券类型_可转债(
            BussinessDictCodeRds.债券类型, "028", "可转债"
    ), 债券类型_可交换债(
            BussinessDictCodeRds.债券类型, "029", "可交换债"
    ), 债券类型_可分离转债存债(
            BussinessDictCodeRds.债券类型, "030", "可分离转债存债"
    ), 债券类型_项目收益票据(
            BussinessDictCodeRds.债券类型, "031", "项目收益票据"
    ), 债券类型_理财直接融资工具(
            BussinessDictCodeRds.债券类型, "032", "理财直接融资工具"
    ), 债券类型_优先股(
            BussinessDictCodeRds.债券类型, "033", "优先股"
    ), 债券类型_其他债券(
            BussinessDictCodeRds.债券类型, "034", "其他债券"
    ), 债券类型_境外国债(
            BussinessDictCodeRds.债券类型, "035", "境外国债"
    ), 债券类型_境外其他债(
            BussinessDictCodeRds.债券类型, "036", "境外其他债"
    ), 基金类型_货币基金(
            BussinessDictCodeRds.基金类型, "01", "货币基金"
    ), 基金类型_债券基金(
            BussinessDictCodeRds.基金类型, "02", "债券基金"
    ), 基金类型_股票基金(
            BussinessDictCodeRds.基金类型, "03", "股票基金"
    ), 基金类型_混合基金(
            BussinessDictCodeRds.基金类型, "04", "混合基金"
    ), 基金类型_其他公募基金(
            BussinessDictCodeRds.基金类型, "05", "其他公募基金"
    ), 基金类型_私募基金(
            BussinessDictCodeRds.基金类型, "06", "私募基金"
    ),

    计划类资产类型_非标债权(
            BussinessDictCodeRds.计划类资产类型, "01", "非标债权"
    ), 计划类资产类型_结构化证券(
            BussinessDictCodeRds.计划类资产类型, "02", "结构化证券"
    ), 计划类资产类型_股票定向增发(
            BussinessDictCodeRds.计划类资产类型, "03", "股票定向增发"
    ), 计划类资产类型_股票质押式回购(
            BussinessDictCodeRds.计划类资产类型, "04", "股票质押式回购"
    ), 计划类资产类型_信贷资产流转项目(
            BussinessDictCodeRds.计划类资产类型, "05", "信贷资产流转项目"
    ), 计划类资产类型_券商收益凭证(
            BussinessDictCodeRds.计划类资产类型, "06", "券商收益凭证"
    ),

    流程状态_新建(
            BussinessDictCodeRds.流程状态, "NEW", "新建"
    ), 流程状态_流程中(
            BussinessDictCodeRds.流程状态, "ING", "流程中"
    ), 流程状态_已退回(
            BussinessDictCodeRds.流程状态, "BACK", "已退回"
    ), 流程状态_流程结束(
            BussinessDictCodeRds.流程状态, "END", "流程结束"
    ), 批处理执行状态_未执行(
            BussinessDictCodeRds.批处理执行状态, "UNR", "未执行"
    ), 批处理执行状态_执行中(
            BussinessDictCodeRds.批处理执行状态, "RUN", "执行中"
    ), 批处理执行状态_异常结束(
            BussinessDictCodeRds.批处理执行状态, "EXP", "异常结束"
    ), 批处理执行状态_正常结束(
            BussinessDictCodeRds.批处理执行状态, "END", "正常结束"
    ), 批处理执行状态_中断(
            BussinessDictCodeRds.批处理执行状态, "BRK", "中断"
    ), 批处理启动模式_自动(
            BussinessDictCodeRds.批处理启动模式, "0", "自动"
    ), 角色岗位_固收处长(
            BussinessDictCodeRds.角色岗位, "04", "固收处长"
    ), 角色岗位_结算岗(
            BussinessDictCodeRds.角色岗位, "07", "结算岗"
    ), 角色岗位_核算岗(
            BussinessDictCodeRds.角色岗位, "08", "核算岗"
    ), 角色岗位_业务数据录入岗(
            BussinessDictCodeRds.角色岗位, "10", "业务数据录入岗"
    ), 角色岗位_信息披露岗(
            BussinessDictCodeRds.角色岗位, "11", "信息披露岗"
    ), 角色岗位_中债报送岗(
            BussinessDictCodeRds.角色岗位, "13", "中债报送岗"
    ), 角色岗位_人行报送岗(
            BussinessDictCodeRds.角色岗位, "14", "人行报送岗"
    ), 角色岗位_报表管理岗(
            BussinessDictCodeRds.角色岗位, "15", "报表管理岗"
    ), 角色岗位_市场与销售岗(
            BussinessDictCodeRds.角色岗位, "16", "市场与销售岗"
    ), 角色岗位_财务岗(
            BussinessDictCodeRds.角色岗位, "17", "财务岗"
    ), 角色岗位_财会部报表查询岗(
            BussinessDictCodeRds.角色岗位, "18", "财会部报表查询岗"
    ), 角色岗位_EAST报表报送岗(
            BussinessDictCodeRds.角色岗位, "19", "EAST报表报送岗"
    ), 角色岗位_系统参数岗(
            BussinessDictCodeRds.角色岗位, "20", "系统参数岗"
    ), 角色岗位_风险岗(
            BussinessDictCodeRds.角色岗位, "21", "风险岗"
    ), 角色岗位_风险合规处处长(
            BussinessDictCodeRds.角色岗位, "22", "风险合规处处长"
    ), 角色岗位_运营处长(
            BussinessDictCodeRds.角色岗位, "23", "运营处长"
    ), 角色岗位_运管处长(
            BussinessDictCodeRds.角色岗位, "24", "运管处长"
    ), 角色岗位_项目处长(
            BussinessDictCodeRds.角色岗位, "25", "项目处长"
    ), 角色岗位_市场与销售处长(
            BussinessDictCodeRds.角色岗位, "26", "市场与销售处长"
    ), 角色岗位_交易员_资本境外_岗(
            BussinessDictCodeRds.角色岗位, "27", "交易员(资本境外)岗"
    ), 角色岗位_交易员_资本境内_岗(
            BussinessDictCodeRds.角色岗位, "28", "交易员(资本境内)岗"
    ), 展示样式_菜单导航(
            BussinessDictCodeRds.展示样式, "01", "菜单导航"
    ), 展示样式_肤色设置(
            BussinessDictCodeRds.展示样式, "02", "肤色设置"
    ), 展示样式_列表样式(
            BussinessDictCodeRds.展示样式, "03", "列表样式"
    ), 展示样式_窗口视角(
            BussinessDictCodeRds.展示样式, "04", "窗口视角"
    ), 展示样式_树形层级(
            BussinessDictCodeRds.展示样式, "05", "树形层级"
    ), 金额显示类型_元(
            BussinessDictCodeRds.金额显示类型, "01", "元"
    ), 金额显示类型_万元(
            BussinessDictCodeRds.金额显示类型, "02", "万元"
    ), 金额显示类型_亿元(
            BussinessDictCodeRds.金额显示类型, "03", "亿元"
    ), 小数显示类型_2位(
            BussinessDictCodeRds.小数显示类型, "01", "2位"
    ), 小数显示类型_4位(
            BussinessDictCodeRds.小数显示类型, "02", "4位"
    ), 小数显示类型_6位(
            BussinessDictCodeRds.小数显示类型, "03", "6位"
    ), 小数显示类型_8位(
            BussinessDictCodeRds.小数显示类型, "04", "8位"
    ), 数据权限类型_查询(
            BussinessDictCodeRds.数据权限类型, "01", "查询"
    ), 数据权限类型_修改(
            BussinessDictCodeRds.数据权限类型, "02", "修改"
    ), 数据权限类型_删除(
            BussinessDictCodeRds.数据权限类型, "03", "删除"
    ), 分享类型_主页配置_公共(
            BussinessDictCodeRds.分享类型_主页配置, "01", "公共"
    ), 分享类型_主页配置_角色分享(
            BussinessDictCodeRds.分享类型_主页配置, "02", "角色分享"
    ), 分享类型_主页配置_用户分享(
            BussinessDictCodeRds.分享类型_主页配置, "03", "用户分享"
    ), 分享类型_主页配置_私有(
            BussinessDictCodeRds.分享类型_主页配置, "04", "私有"
    ),

    处理状态_成功(
            BussinessDictCodeRds.处理状态, "0", "成功"
    ), 处理状态_失败(
            BussinessDictCodeRds.处理状态, "1", "失败"
    ),

    成功失败标志_失败(
            BussinessDictCodeRds.成功失败标志, "02", "失败"
    ), 指令处理状态_处理成功(
            BussinessDictCodeRds.指令处理状态, "01", "处理成功"
    ), 指令处理状态_处理失败(
            BussinessDictCodeRds.指令处理状态, "02", "处理失败"
    ), 指令处理状态_已处理(
            BussinessDictCodeRds.指令处理状态, "03", "已处理"
    ), 金额显示精度_元(
            BussinessDictCodeRds.金额显示精度, "01", "元"
    ), 金额显示精度_万元(
            BussinessDictCodeRds.金额显示精度, "02", "万元"
    ), 金额显示精度_百万(
            BussinessDictCodeRds.金额显示精度, "03", "百万"
    ), 金额显示精度_亿(
            BussinessDictCodeRds.金额显示精度, "04", "亿"
    ), 共同业务类型_债券一级投标委托(
            BussinessDictCodeRds.共通业务类型, "ETR18", "债券一级投标委托"
    ), 交易类型_CSTP_现券买卖(
            BussinessDictCodeRds.交易类型_CSTP, "T01", "现券买卖"
    ), 交易类型_CSTP_质押式回购(
            BussinessDictCodeRds.交易类型_CSTP, "T02", "质押式回购"
    ), 项目类一级分类_中债_票据类(
            BussinessDictCodeRds.项目类一级分类_中债, "1201", "票据类"
    ), 项目类一级分类_中债_信托贷款(
            BussinessDictCodeRds.项目类一级分类_中债, "1202", "信托贷款 "
    ), 项目类一级分类_中债_委托贷款(
            BussinessDictCodeRds.项目类一级分类_中债, "1203", "委托贷款"
    ), 项目类一级分类_中债_信贷资产转让(
            BussinessDictCodeRds.项目类一级分类_中债, "1204", "信贷资产转让"
    ), 项目类一级分类_中债_收_受益权(
            BussinessDictCodeRds.项目类一级分类_中债, "1205", "收/受益权"
    ), 项目类一级分类_中债_委托债权(
            BussinessDictCodeRds.项目类一级分类_中债, "1206", "委托债权"
    ), 项目类一级分类_中债_应收账款(
            BussinessDictCodeRds.项目类一级分类_中债, "1207", "应收账款"
    ), 项目类一级分类_中债_带回购条款的股权性融资(
            BussinessDictCodeRds.项目类一级分类_中债, "1208", "带回购条款的股权性融资"
    ), 项目类一级分类_中债_债权融资类产品(
            BussinessDictCodeRds.项目类一级分类_中债, "1209", "债权融资类产品"
    ), 项目类一级分类_中债_信用证(
            BussinessDictCodeRds.项目类一级分类_中债, "1210", "信用证"
    ), 项目类一级分类_中债_其他权益类投资(
            BussinessDictCodeRds.项目类一级分类_中债, "1399", "其他权益类投资"
    ), 项目类一级分类_中债_理财直接融资工具(
            BussinessDictCodeRds.项目类一级分类_中债, "2101", "理财直接融资工具"
    ), 项目类一级分类_中债_股权(
            BussinessDictCodeRds.项目类一级分类_中债, "1301", "股权"
    ), 项目类一级分类_中债_基础设施基金(
            BussinessDictCodeRds.项目类一级分类_中债, "2612", "基础设施基金"
    ), WIND一级分类_含债券中债_国债(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B01", "国债"
    ), WIND一级分类_含债券中债_地方政府债(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B02", "地方政府债"
    ), WIND一级分类_含债券中债_央行票据(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B03", "央行票据"
    ), WIND一级分类_含债券中债_企业债(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B04", "企业债"
    ), WIND一级分类_含债券中债_公司债(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B05", "公司债"
    ), WIND一级分类_含债券中债_企业债务融资工具(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B06", "企业债务融资工具"
    ), WIND一级分类_含债券中债_商业性金融债(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B07", "商业性金融债"
    ), WIND一级分类_含债券中债_政策性金融债(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B08", "政策性金融债"
    ), WIND一级分类_含债券中债_政府支持机构债(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B09", "政府支持机构债"
    ), WIND一级分类_含债券中债_资产支持证券(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B10", "资产支持证券"
    ), WIND一级分类_含债券中债_外国债券(
            BussinessDictCodeRds.WIND一级分类_含债券中债, "B11", "外国债券"
    ), 存款类一级分类_中债_本行存款(
            BussinessDictCodeRds.存款类一级分类_中债, "1002", "本行存款"
    ), 存款类一级分类_中债_他行存款(
            BussinessDictCodeRds.存款类一级分类_中债, "1003", "他行存款"
    ), 存款类一级分类_中债_本行发行的大额存单(
            BussinessDictCodeRds.存款类一级分类_中债, "1004", "本行发行的大额存单"
    ), 存款类一级分类_中债_他行发行的大额存单(
            BussinessDictCodeRds.存款类一级分类_中债, "1005", "他行发行的大额存单"
    ), 存款类一级分类_中债_拆放同业(
            BussinessDictCodeRds.存款类一级分类_中债, "1107", "拆放同业"
    ), 客户评级使用模型类别_高等教育事业模型(
            BussinessDictCodeRds.客户评级使用模型类别, "24", "高等教育事业模型"
    ), 客户评级使用模型类别_中型商贸企业评级模型(
            BussinessDictCodeRds.客户评级使用模型类别, "25", "中型商贸企业评级模型"
    ), 客户评级使用模型类别_小企业评级模型_商贸服务(
            BussinessDictCodeRds.客户评级使用模型类别, "26", "小企业评级模型（商贸服务）"
    ), 客户评级使用模型类别_直接认定评级(
            BussinessDictCodeRds.客户评级使用模型类别, "27", "直接认定评级"
    ), 客户评级使用模型类别_建筑业评级模型(
            BussinessDictCodeRds.客户评级使用模型类别, "28", "建筑业评级模型"
    ), 客户评级使用模型类别_大型商贸服务业评级模型(
            BussinessDictCodeRds.客户评级使用模型类别, "29", "大型商贸服务业评级模型"
    ), 客户评级使用模型类别_新建企业评级模型(
            BussinessDictCodeRds.客户评级使用模型类别, "30", "新建企业评级模型"
    ), 客户评级使用模型类别_大型批零评级模型(
            BussinessDictCodeRds.客户评级使用模型类别, "31", "大型批零评级模型"
    ), 客户评级使用模型类别_金融租赁公司评级模型(
            BussinessDictCodeRds.客户评级使用模型类别, "32", "金融租赁公司评级模型"
    ), 客户评级使用模型类别_寿险公司评级模型(
            BussinessDictCodeRds.客户评级使用模型类别, "33", "寿险公司评级模型"
    ), 交易方向_外汇即期(
            BussinessDictCodeRds.交易方向, "T14", "外汇即期"
    ), 交易方向_外汇远期(
            BussinessDictCodeRds.交易方向, "T15", "外汇远期"
    ), 交易方向_外汇掉期(
            BussinessDictCodeRds.交易方向, "T16", "外汇掉期"
    ), 交易方向_利率互换(
            BussinessDictCodeRds.交易方向, "T17", "利率互换"
    ), 交易方向_融入债券收息(
            BussinessDictCodeRds.交易方向, "T28", "融入债券收息"
    ), 交易方向_融入债券利息返还(
            BussinessDictCodeRds.交易方向, "T29", "融入债券利息返还"
    ), 交易方向_卖空债券结息(
            BussinessDictCodeRds.交易方向, "T30", "卖空债券结息"
    ), 交易方向_融入债券还本(
            BussinessDictCodeRds.交易方向, "T31", "融入债券还本"
    ), 交易方向_融入债券本金返还(
            BussinessDictCodeRds.交易方向, "T32", "融入债券本金返还"
    ), 交易方向_卖空债券还本(
            BussinessDictCodeRds.交易方向, "T33", "卖空债券还本"
    ), 交易方向_融入债券卖出(
            BussinessDictCodeRds.交易方向, "T34", "融入债券卖出"
    ), 交易方向_资产重分类(
            BussinessDictCodeRds.交易方向, "T35", "资产重分类"
    ), 交易方向_融入债券购回(
            BussinessDictCodeRds.交易方向, "T36", "融入债券购回"
    ), 交易方向_债券借贷本方券交割(
            BussinessDictCodeRds.交易方向, "T37", "债券借贷本方券交割"
    ), 交易方向_行权_赎回(
            BussinessDictCodeRds.交易方向, "T39", "行权-赎回"
    ), 交易方向_上市流通(
            BussinessDictCodeRds.交易方向, "T41", "上市流通"
    ), 交易方向_债券派本(
            BussinessDictCodeRds.交易方向, "T42", "债券派本"
    ), 交易方向_融入债券派息到账(
            BussinessDictCodeRds.交易方向, "T43", "融入债券派息到账"
    ), 交易方向_融入债券派本到账(
            BussinessDictCodeRds.交易方向, "T44", "融入债券派本到账"
    ), 交易方向_付息(
            BussinessDictCodeRds.交易方向, "T45", "付息"
    ), 交易方向_回补(
            BussinessDictCodeRds.交易方向, "C16", "回补"
    ), 交易方向_提前赎回(
            BussinessDictCodeRds.交易方向, "C19", "提前赎回"
    ), 交易方向_提前终止(
            BussinessDictCodeRds.交易方向, "C20", "提前终止"
    ), 共通业务类型_特拆交易(
            BussinessDictCodeRds.共通业务类型, "TRD23", "特拆交易"
    ), 共通业务类型_拆出交易(
            BussinessDictCodeRds.共通业务类型, "TRD24", "拆出交易"
    ), 共通业务类型_项目类资产买入(
            BussinessDictCodeRds.共通业务类型, "TRD25", "项目类资产买入"
    ), 共通业务类型_项目类资产卖出(
            BussinessDictCodeRds.共通业务类型, "TRD26", "项目类资产卖出"
    ), 共通业务类型_逆回购录入(
            BussinessDictCodeRds.共通业务类型, "TRD27", "逆回购录入"
    ), 共通业务类型_正回购录入(
            BussinessDictCodeRds.共通业务类型, "TRD28", "正回购录入"
    ), 机构所属行业二级分类_有色金属矿采选业(
            BussinessDictCodeRds.机构所属行业二级分类, "B09", "有色金属矿采选业"
    ), 机构所属行业二级分类_非金属矿采选业(
            BussinessDictCodeRds.机构所属行业二级分类, "B10", "非金属矿采选业"
    ), 机构所属行业二级分类_开采辅助活动(
            BussinessDictCodeRds.机构所属行业二级分类, "B11", "开采辅助活动"
    ), 机构所属行业二级分类_其他采矿业(
            BussinessDictCodeRds.机构所属行业二级分类, "B12", "其他采矿业"
    ), 机构所属行业二级分类_农副食品加工业(
            BussinessDictCodeRds.机构所属行业二级分类, "C13", "农副食品加工业"
    ), 机构所属行业二级分类_食品制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C14", "食品制造业"
    ), 机构所属行业二级分类_酒_饮料和精制茶制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C15", "酒、饮料和精制茶制造业"
    ), 机构所属行业二级分类_烟草制品业(
            BussinessDictCodeRds.机构所属行业二级分类, "C16", "烟草制品业"
    ), 机构所属行业二级分类_纺织业(
            BussinessDictCodeRds.机构所属行业二级分类, "C17", "纺织业"
    ), 机构所属行业二级分类_纺织服装_服饰业(
            BussinessDictCodeRds.机构所属行业二级分类, "C18", "纺织服装、服饰业"
    ), 机构所属行业二级分类_皮革_毛皮_羽毛及其制品和制鞋业(
            BussinessDictCodeRds.机构所属行业二级分类, "C19", "皮革、毛皮、羽毛及其制品和制鞋业"
    ), 机构所属行业二级分类_木材加工和木_竹_藤_棕_草制品业(
            BussinessDictCodeRds.机构所属行业二级分类, "C20", "木材加工和木、竹、藤、棕、草制品业"
    ), 机构所属行业二级分类_家具制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C21", "家具制造业"
    ), 机构所属行业二级分类_造纸及纸制品业(
            BussinessDictCodeRds.机构所属行业二级分类, "C22", "造纸及纸制品业"
    ), 机构所属行业二级分类_印刷业和记录媒介的复制业(
            BussinessDictCodeRds.机构所属行业二级分类, "C23", "印刷业和记录媒介的复制业"
    ), 机构所属行业二级分类_文教_工美_体育和娱乐用品制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C24", "文教、工美、体育和娱乐用品制造业"
    ), 机构所属行业二级分类_石油加工_炼焦及核燃料加工业(
            BussinessDictCodeRds.机构所属行业二级分类, "C25", "石油加工、炼焦及核燃料加工业"
    ), 机构所属行业二级分类_化学原料和化学制品制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C26", "化学原料和化学制品制造业"
    ), 机构所属行业二级分类_医药制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C27", "医药制造业"
    ), 机构所属行业二级分类_化学纤维制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C28", "化学纤维制造业"
    ), 机构所属行业二级分类_橡胶和塑料制品业(
            BussinessDictCodeRds.机构所属行业二级分类, "C29", "橡胶和塑料制品业"
    ), 机构所属行业二级分类_非金属矿物制品业(
            BussinessDictCodeRds.机构所属行业二级分类, "C30", "非金属矿物制品业"
    ), 机构所属行业二级分类_黑色金属冶炼及压延加工业(
            BussinessDictCodeRds.机构所属行业二级分类, "C31", "黑色金属冶炼及压延加工业"
    ), 机构所属行业二级分类_有色金属冶炼及压延加工业(
            BussinessDictCodeRds.机构所属行业二级分类, "C32", "有色金属冶炼及压延加工业"
    ), 机构所属行业二级分类_金属制品业(
            BussinessDictCodeRds.机构所属行业二级分类, "C33", "金属制品业"
    ), 机构所属行业二级分类_通用设备制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C34", "通用设备制造业"
    ), 机构所属行业二级分类_专用设备制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C35", "专用设备制造业"
    ), 机构所属行业二级分类_汽车制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C36", "汽车制造业"
    ), 机构所属行业二级分类_铁路_船舶_航空航天和其他运输设备制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C37", "铁路、船舶、航空航天和其他运输设备制造业"
    ), 机构所属行业二级分类_电气机械和器材制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C38", "电气机械和器材制造业"
    ), 机构所属行业二级分类_计算机_通信和其他电子设备制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C39", "计算机、通信和其他电子设备制造业"
    ), 机构所属行业二级分类_仪器仪表制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C40", "仪器仪表制造业"
    ), 机构所属行业二级分类_其他制造业(
            BussinessDictCodeRds.机构所属行业二级分类, "C41", "其他制造业"
    ), 机构所属行业二级分类_废弃资源综合利用业(
            BussinessDictCodeRds.机构所属行业二级分类, "C42", "废弃资源综合利用业"
    ), 机构所属行业二级分类_金属制品_机械和设备修理业(
            BussinessDictCodeRds.机构所属行业二级分类, "C43", "金属制品、机械和设备修理业"
    ), 机构所属行业二级分类_电力_热力生产和供应业(
            BussinessDictCodeRds.机构所属行业二级分类, "D44", "电力、热力生产和供应业"
    ), 机构所属行业二级分类_煤气生产和供应业(
            BussinessDictCodeRds.机构所属行业二级分类, "D45", "煤气生产和供应业"
    ), 机构所属行业二级分类_水的生产和供应业(
            BussinessDictCodeRds.机构所属行业二级分类, "D46", "水的生产和供应业"
    ), 机构所属行业二级分类_房屋建筑业(
            BussinessDictCodeRds.机构所属行业二级分类, "E47", "房屋建筑业"
    ), 机构所属行业二级分类_土木工程建筑业(
            BussinessDictCodeRds.机构所属行业二级分类, "E48", "土木工程建筑业"
    ), 机构所属行业二级分类_建筑安装业(
            BussinessDictCodeRds.机构所属行业二级分类, "E49", "建筑安装业"
    ), 机构所属行业二级分类_建筑装饰和其他建筑业(
            BussinessDictCodeRds.机构所属行业二级分类, "E50", "建筑装饰和其他建筑业"
    ), 机构所属行业二级分类_批发业(
            BussinessDictCodeRds.机构所属行业二级分类, "F51", "批发业"
    ), 机构所属行业二级分类_零售业(
            BussinessDictCodeRds.机构所属行业二级分类, "F52", "零售业"
    ), 机构所属行业二级分类_铁路运输业(
            BussinessDictCodeRds.机构所属行业二级分类, "G53", "铁路运输业"
    ), 机构所属行业二级分类_道路运输业(
            BussinessDictCodeRds.机构所属行业二级分类, "G54", "道路运输业"
    ), 机构所属行业二级分类_水上运输业(
            BussinessDictCodeRds.机构所属行业二级分类, "G55", "水上运输业"
    ), 机构所属行业二级分类_航空运输业(
            BussinessDictCodeRds.机构所属行业二级分类, "G56", "航空运输业"
    ), 机构所属行业二级分类_管道运输业(
            BussinessDictCodeRds.机构所属行业二级分类, "G57", "管道运输业"
    ), 机构所属行业二级分类_装卸搬运和其他运输服务业(
            BussinessDictCodeRds.机构所属行业二级分类, "G58", "装卸搬运和其他运输服务业"
    ), 机构所属行业二级分类_仓储业(
            BussinessDictCodeRds.机构所属行业二级分类, "G59", "仓储业"
    ), 机构所属行业二级分类_邮政业(
            BussinessDictCodeRds.机构所属行业二级分类, "G60", "邮政业"
    ), 机构所属行业二级分类_住宿业(
            BussinessDictCodeRds.机构所属行业二级分类, "H61", "住宿业"
    ), 机构所属行业二级分类_餐饮业(
            BussinessDictCodeRds.机构所属行业二级分类, "H62", "餐饮业"
    ), 机构所属行业二级分类_电信_广播电视和卫星传输服务(
            BussinessDictCodeRds.机构所属行业二级分类, "I63", "电信、广播电视和卫星传输服务"
    ), 机构所属行业二级分类_互联网和相关服务(
            BussinessDictCodeRds.机构所属行业二级分类, "I64", "互联网和相关服务"
    ), 机构所属行业二级分类_软件和信息技术服务业(
            BussinessDictCodeRds.机构所属行业二级分类, "I65", "软件和信息技术服务业"
    ), 机构所属行业二级分类_货币金融服务(
            BussinessDictCodeRds.机构所属行业二级分类, "J66", "货币金融服务"
    ), 机构所属行业二级分类_资本市场服务(
            BussinessDictCodeRds.机构所属行业二级分类, "J67", "资本市场服务"
    ), 机构所属行业二级分类_保险业(
            BussinessDictCodeRds.机构所属行业二级分类, "J68", "保险业"
    ), 机构所属行业二级分类_其他金融业(
            BussinessDictCodeRds.机构所属行业二级分类, "J69", "其他金融业"
    ), 机构所属行业二级分类_房地产业(
            BussinessDictCodeRds.机构所属行业二级分类, "K70", "房地产业"
    ), 机构所属行业二级分类_租赁业(
            BussinessDictCodeRds.机构所属行业二级分类, "L71", "租赁业"
    ), 机构所属行业二级分类_商务服务业(
            BussinessDictCodeRds.机构所属行业二级分类, "L72", "商务服务业"
    ), 机构所属行业二级分类_研究与试验发展(
            BussinessDictCodeRds.机构所属行业二级分类, "M73", "研究与试验发展"
    ), 机构所属行业二级分类_专业技术服务业(
            BussinessDictCodeRds.机构所属行业二级分类, "M74", "专业技术服务业"
    ), 机构所属行业二级分类_科技推广和应用服务业(
            BussinessDictCodeRds.机构所属行业二级分类, "M75", "科技推广和应用服务业"
    ), 机构所属行业二级分类_水利管理业(
            BussinessDictCodeRds.机构所属行业二级分类, "N76", "水利管理业"
    ), 机构所属行业二级分类_生态保护和环境治理业(
            BussinessDictCodeRds.机构所属行业二级分类, "N77", "生态保护和环境治理业"
    ), 机构所属行业二级分类_公共设施管理业(
            BussinessDictCodeRds.机构所属行业二级分类, "N78", "公共设施管理业"
    ), 机构所属行业二级分类_居民服务业(
            BussinessDictCodeRds.机构所属行业二级分类, "O79", "居民服务业"
    ), 机构所属行业二级分类_机动车_电子产品和日用产品修理业(
            BussinessDictCodeRds.机构所属行业二级分类, "O80", "机动车、电子产品和日用产品修理业"
    ), 机构所属行业二级分类_其他服务业(
            BussinessDictCodeRds.机构所属行业二级分类, "O81", "其他服务业"
    ), 机构所属行业二级分类_教育(
            BussinessDictCodeRds.机构所属行业二级分类, "P82", "教育"
    ), 机构所属行业二级分类_卫生(
            BussinessDictCodeRds.机构所属行业二级分类, "Q83", "卫生"
    ), 机构所属行业二级分类_社会工作(
            BussinessDictCodeRds.机构所属行业二级分类, "Q84", "社会工作"
    ), 机构所属行业二级分类_新闻和出版业(
            BussinessDictCodeRds.机构所属行业二级分类, "R85", "新闻和出版业"
    ), 机构所属行业二级分类_广播_电视_电影和影视录音制作业(
            BussinessDictCodeRds.机构所属行业二级分类, "R86", "广播、电视、电影和影视录音制作业"
    ), 机构所属行业二级分类_文化艺术业(
            BussinessDictCodeRds.机构所属行业二级分类, "R87", "文化艺术业"
    ), 机构所属行业二级分类_体育(
            BussinessDictCodeRds.机构所属行业二级分类, "R88", "体育"
    ), 机构所属行业二级分类_娱乐业(
            BussinessDictCodeRds.机构所属行业二级分类, "R89", "娱乐业"
    ), 机构所属行业二级分类_中国共产党机关(
            BussinessDictCodeRds.机构所属行业二级分类, "S90", "中国共产党机关"
    ), 机构所属行业二级分类_国家机构(
            BussinessDictCodeRds.机构所属行业二级分类, "S91", "国家机构"
    ), 机构所属行业二级分类_人民政协_民主党派(
            BussinessDictCodeRds.机构所属行业二级分类, "S92", "人民政协、民主党派"
    ), 机构所属行业二级分类_社会保障(
            BussinessDictCodeRds.机构所属行业二级分类, "S93", "社会保障"
    ), 机构所属行业二级分类_群众团体_社会团体和其他成员组织(
            BussinessDictCodeRds.机构所属行业二级分类, "S94", "群众团体、社会团体和其他成员组织"
    ), 机构所属行业二级分类_基层群众自治组织(
            BussinessDictCodeRds.机构所属行业二级分类, "S95", "基层群众自治组织"
    ), 机构所属行业二级分类_国际组织(
            BussinessDictCodeRds.机构所属行业二级分类, "T96", "国际组织"
    ), 估值来源_中债登_CBC(
            BussinessDictCodeRds.估值来源, "CBC", "中债登(CBC)"
    ), 估值来源_中证登_CSD(
            BussinessDictCodeRds.估值来源, "CSD", "中证登(CSD)"
    ), 估值来源_上清所_SCH(
            BussinessDictCodeRds.估值来源, "SCH", "上清所(SCH)"
    ), 行情类型_指数(
            BussinessDictCodeRds.行情类型, "02", "指数"
    ), 行情类型_收益率曲线(
            BussinessDictCodeRds.行情类型, "03", "收益率曲线"
    ), 行情类型_利率(
            BussinessDictCodeRds.行情类型, "04", "利率"
    ), 公司性质_总公司(
            BussinessDictCodeRds.公司性质, "01", "总公司"
    ), 公司性质_下属公司(
            BussinessDictCodeRds.公司性质, "02", "下属公司"
    ), 客户类型_法人(
            BussinessDictCodeRds.客户类型, "01", "法人"
    ), 控股类型_公有控股经济(
            BussinessDictCodeRds.控股类型, "A", "公有控股经济"
    ), 控股类型_国有控股(
            BussinessDictCodeRds.控股类型, "A01", "国有控股"
    ), 控股类型_国有相对控股(
            BussinessDictCodeRds.控股类型, "A0101", "国有相对控股"
    ), 控股类型_国有绝对控股(
            BussinessDictCodeRds.控股类型, "A0102", "国有绝对控股"
    ), 控股类型_集体控股(
            BussinessDictCodeRds.控股类型, "A02", "集体控股"
    ), 控股类型_集体相对控股(
            BussinessDictCodeRds.控股类型, "A0201", "集体相对控股"
    ), 控股类型_集体绝对控股(
            BussinessDictCodeRds.控股类型, "A0202", "集体绝对控股"
    ), 控股类型_非公有控股经济(
            BussinessDictCodeRds.控股类型, "B", "非公有控股经济"
    ), 控股类型_私人控股(
            BussinessDictCodeRds.控股类型, "B01", "私人控股"
    ), 控股类型_私人相对控股(
            BussinessDictCodeRds.控股类型, "B0101", "私人相对控股"
    ), 控股类型_私人绝对控股(
            BussinessDictCodeRds.控股类型, "B0102", "私人绝对控股"
    ), 控股类型_港澳台控股(
            BussinessDictCodeRds.控股类型, "B02", "港澳台控股"
    ), 控股类型_港澳台相对控股(
            BussinessDictCodeRds.控股类型, "B0201", "港澳台相对控股"
    ), 控股类型_港澳台绝对控股(
            BussinessDictCodeRds.控股类型, "B0202", "港澳台绝对控股"
    ), 控股类型_外商控股(
            BussinessDictCodeRds.控股类型, "B03", "外商控股"
    ), 控股类型_外商相对控股(
            BussinessDictCodeRds.控股类型, "B0301", "外商相对控股"
    ), 控股类型_外商绝对控股(
            BussinessDictCodeRds.控股类型, "B0302", "外商绝对控股"
    ), 客户主体类型_我国中央政府(
            BussinessDictCodeRds.客户主体类型, "01", "我国中央政府"
    ), 客户主体类型_境外主权国家或经济实体区域政府(
            BussinessDictCodeRds.客户主体类型, "02", "境外主权国家或经济实体区域政府"
    ), 客户主体类型_中国人民银行(
            BussinessDictCodeRds.客户主体类型, "03", "中国人民银行"
    ), 客户主体类型_境外中央银行(
            BussinessDictCodeRds.客户主体类型, "04", "境外中央银行"
    ), 客户主体类型_我国公共部门实体(
            BussinessDictCodeRds.客户主体类型, "05", "我国公共部门实体"
    ), 客户主体类型_境外公共部门实体(
            BussinessDictCodeRds.客户主体类型, "06", "境外公共部门实体"
    ), 客户主体类型_多边开发银行(
            BussinessDictCodeRds.客户主体类型, "07", "多边开发银行"
    ), 客户主体类型_国际清算银行(
            BussinessDictCodeRds.客户主体类型, "08", "国际清算银行"
    ), 客户主体类型_国际货币基金组织(
            BussinessDictCodeRds.客户主体类型, "09", "国际货币基金组织"
    ), 客户主体类型_境外商业银行(
            BussinessDictCodeRds.客户主体类型, "10", "境外商业银行"
    ), 客户主体类型_我国政策性银行(
            BussinessDictCodeRds.客户主体类型, "11", "我国政策性银行"
    ), 客户主体类型_我国商业银行(
            BussinessDictCodeRds.客户主体类型, "12", "我国商业银行"
    ), 客户主体类型_我国其他银行类金融机构(
            BussinessDictCodeRds.客户主体类型, "13", "我国其他银行类金融机构"
    ), 客户主体类型_我国其他非银行金融机构(
            BussinessDictCodeRds.客户主体类型, "14", "我国其他非银行金融机构"
    ), 客户主体类型_其他法人企业(
            BussinessDictCodeRds.客户主体类型, "15", "其他法人企业"
    ), 客户主体类型_其他非法人企业(
            BussinessDictCodeRds.客户主体类型, "16", "其他非法人企业"
    ), 客户主体类型_个人_自然人(
            BussinessDictCodeRds.客户主体类型, "17", "个人(自然人)"
    ), 估值字段类型_字符(
            BussinessDictCodeRds.估值字段类型, "STRING", "字符"
    ), 估值字段类型_数值(
            BussinessDictCodeRds.估值字段类型, "NUMBER", "数值"
    ), 估值字段类型_百分比(
            BussinessDictCodeRds.估值字段类型, "PERCENT", "百分比"
    ), 汇总科目_实收资本(
            BussinessDictCodeRds.汇总科目, "601", "实收资本"
    ), 汇总科目_基金资产净值(
            BussinessDictCodeRds.汇总科目, "701", "基金资产净值"
    ), 汇总科目_证券投资合计(
            BussinessDictCodeRds.汇总科目, "101", "证券投资合计"
    ), 汇总科目_资产合计(
            BussinessDictCodeRds.汇总科目, "604", "资产合计"
    ), 汇总科目_负债合计(
            BussinessDictCodeRds.汇总科目, "605", "负债合计"
    ), 汇总科目_基金资产单位净值(
            BussinessDictCodeRds.汇总科目, "702", "基金资产单位净值"
    ), 汇总科目_期初单位净值(
            BussinessDictCodeRds.汇总科目, "901", "期初单位净值"
    ), 汇总科目_基金本日收益(
            BussinessDictCodeRds.汇总科目, "90102", "基金本日收益"
    ), 汇总科目_今日单位净值(
            BussinessDictCodeRds.汇总科目, "902", "今日单位净值"
    ), 汇总科目_净值增长率(
            BussinessDictCodeRds.汇总科目, "903", "净值增长率"
    ), 汇总科目_净值日增长率(
            BussinessDictCodeRds.汇总科目, "90310", "净值日增长率"
    ), 汇总科目_累计单位净值(
            BussinessDictCodeRds.汇总科目, "905", "累计单位净值"
    ), 汇总科目_本期实现收益(
            BussinessDictCodeRds.汇总科目, "912", "本期实现收益"
    ), 汇总科目_可分配收益(
            BussinessDictCodeRds.汇总科目, "913", "可分配收益"
    ), 交易平台_竞价撮合平台(
            BussinessDictCodeRds.交易平台, "02", "竞价撮合平台"
    ), 交易平台_固定收益平台(
            BussinessDictCodeRds.交易平台, "03", "固定收益平台"
    ), 交易平台_大宗交易平台(
            BussinessDictCodeRds.交易平台, "04", "大宗交易平台"
    ), 交易平台_综合协议交易平台(
            BussinessDictCodeRds.交易平台, "05", "综合协议交易平台"
    ), 共通业务类型_货币基金申购(
            BussinessDictCodeRds.共通业务类型, "TRD29", "货币基金申购"
    ), 共通业务类型_货币基金赎回(
            BussinessDictCodeRds.共通业务类型, "TRD30", "货币基金赎回"
    ), 出入库方向_入库(
            BussinessDictCodeRds.出入库方向, "01", "入库"
    ), 出入库方向_出库(
            BussinessDictCodeRds.出入库方向, "02", "出库"
    ),
     登记托管机构_中债_中央国债登记结算有限责任公司(
            BussinessDictCodeRds.登记托管机构_中债, "01", "中央国债登记结算有限责任公司"
    ), 登记托管机构_中债_上海清算所(
            BussinessDictCodeRds.登记托管机构_中债, "02", "上海清算所"
    ), 登记托管机构_中债_中国证券登记结算有限责任公司(
            BussinessDictCodeRds.登记托管机构_中债, "03", "中国证券登记结算有限责任公司"
    ), 登记托管机构_中债_其他(
            BussinessDictCodeRds.登记托管机构_中债, "99", "其他"
    ),

    业务类型_债券借贷标的债券交易(
            BussinessDictCodeRds.业务类型, "29", "债券借贷标的债券交易"
    ), 业务类型_资产重分类(
            BussinessDictCodeRds.业务类型, "30", "资产重分类"
    ), 业务类型_产品回补交易(
            BussinessDictCodeRds.业务类型, "31", "产品回补交易"
    ), 交易日历_中国银行间交易日历(
            BussinessDictCodeRds.交易日历, "IB", "中国银行间交易日历"
    ), 交易日历_上海交易所日历(
            BussinessDictCodeRds.交易日历, "SH", "上海交易所日历"
    ), 交易日历_深圳交易所日历(
            BussinessDictCodeRds.交易日历, "SZ", "深圳交易所日历"
    ),

    行情类型_利率_利率行情(
            BussinessDictCodeRds.行情类型_利率, "01", "利率行情"
    ), 行情类型_利率_汇率行情(
            BussinessDictCodeRds.行情类型_利率, "02", "汇率行情"
    ), 行情类型_利率_指数行情(
            BussinessDictCodeRds.行情类型_利率, "03", "指数行情"
    ), 现金流单位_总额(
            BussinessDictCodeRds.现金流单位, "02", "总额"
    ), 基数类型_资产净值(
            BussinessDictCodeRds.基数类型, "03", "资产净值"
    ), 基数类型_实收资本(
            BussinessDictCodeRds.基数类型, "04", "实收资本"
    ), 回购拆借方向_融入(
            BussinessDictCodeRds.回购拆借方向, "05", "融入"
    ), 回购拆借方向_融出(
            BussinessDictCodeRds.回购拆借方向, "06", "融出"
    ),

    数据状态_删除(
            BussinessDictCodeRds.数据状态, "D", "删除"
    ), 数据状态_作废(
            BussinessDictCodeRds.数据状态, "R", "作废"
    ), 数据状态_新建中(
            BussinessDictCodeRds.数据状态, "A", "新建中"
    ), 数据状态_删除中(
            BussinessDictCodeRds.数据状态, "P", "删除中"
    ), 数据状态_新建(
            BussinessDictCodeRds.数据状态, "N", "新建"
    ), 数据状态_修改(
            BussinessDictCodeRds.数据状态, "M", "修改"
    ), 数据状态_修改中(
            BussinessDictCodeRds.数据状态, "U", "修改中"
    ), 报表展示方式_列表(
            BussinessDictCodeRds.报表展示方式, "01", "列表"
    ),

    接口系统ID_预占接口(
            BussinessDictCodeRds.接口系统ID, "RSK001", "预占接口"
    ), 接口系统ID_风险试算接口(
            BussinessDictCodeRds.接口系统ID, "RSK002", "风险试算接口"
    ), 接口系统ID_实占接口(
            BussinessDictCodeRds.接口系统ID, "RSK003", "实占接口"
    ), 接口系统ID_实占释放接口(
            BussinessDictCodeRds.接口系统ID, "RSK004", "实占释放接口"
    ), 接口系统ID_预占释放接口(
            BussinessDictCodeRds.接口系统ID, "RSK005", "预占释放接口"
    ), 接口系统ID_模糊委托接口(
            BussinessDictCodeRds.接口系统ID, "RSK006", "模糊委托接口"
    ), 消费系统_交易系统(
            BussinessDictCodeRds.消费系统, "520", "交易系统"
    ), 接口系统ID_风险试算批量接口(
            BussinessDictCodeRds.接口系统ID, "RSK011", "风险试算批量接口"
    ), 接口系统ID_实占释放批量接口(
            BussinessDictCodeRds.接口系统ID, "RSK012", "实占释放批量接口"
    ), 接口系统ID_实占批量接口(
            BussinessDictCodeRds.接口系统ID, "RSK200", "实占批量接口"
    ), 接口系统ID_预占批量接口(
            BussinessDictCodeRds.接口系统ID, "RSK101", "预占批量接口"
    ), 接口系统ID_预占释放批量接口(
            BussinessDictCodeRds.接口系统ID, "RSK105", "预占释放批量接口"
    ), 接口系统ID_风险审批接口(
            BussinessDictCodeRds.接口系统ID, "RSK007", "风险审批接口"
    ), 接口系统ID_批量风险审批接口(
            BussinessDictCodeRds.接口系统ID, "RSK0017", "批量风险审批接口"
    ),

    评级变动_调高(
            BussinessDictCodeRds.评级变动, "01", "调高"
    ), 评级变动_调低(
            BussinessDictCodeRds.评级变动, "02", "调低"
    ), 评级变动_维持(
            BussinessDictCodeRds.评级变动, "03", "维持"
    ), 评级变动_首次(
            BussinessDictCodeRds.评级变动, "04", "首次"
    ), 评级展望_正面(
            BussinessDictCodeRds.评级展望, "01", "正面"
    ), 评级展望_负面(
            BussinessDictCodeRds.评级展望, "02", "负面"
    ), 评级展望_稳定(
            BussinessDictCodeRds.评级展望, "03", "稳定"
    ), 评级展望_观望(
            BussinessDictCodeRds.评级展望, "04", "观望"
    ), 评级展望_待决(
            BussinessDictCodeRds.评级展望, "05", "待决"
    ),

    监管一级分类_现金及银行存款(
            BussinessDictCodeRds.监管一级分类, "1", "现金及银行存款"
    ), 监管一级分类_同业存单(
            BussinessDictCodeRds.监管一级分类, "2", "同业存单"
    ), 监管一级分类_拆放同业及买入返售(
            BussinessDictCodeRds.监管一级分类, "3", "拆放同业及买入返售"
    ), 监管一级分类_债券(
            BussinessDictCodeRds.监管一级分类, "4", "债券"
    ), 监管一级分类_理财直接融资工具(
            BussinessDictCodeRds.监管一级分类, "5", "理财直接融资工具"
    ), 监管一级分类_新增可投资资产(
            BussinessDictCodeRds.监管一级分类, "6", "新增可投资资产"
    ), 监管一级分类_非标准化债权类资产(
            BussinessDictCodeRds.监管一级分类, "7", "非标准化债权类资产"
    ), 监管一级分类_权益类资产(
            BussinessDictCodeRds.监管一级分类, "8", "权益类资产"
    ), 监管一级分类_金融衍生品(
            BussinessDictCodeRds.监管一级分类, "9", "金融衍生品"
    ), 监管一级分类_代客境外理财投资QDII(
            BussinessDictCodeRds.监管一级分类, "10", "代客境外理财投资QDII"
    ), 监管一级分类_商品类资产(
            BussinessDictCodeRds.监管一级分类, "11", "商品类资产"
    ), 监管一级分类_另类资产(
            BussinessDictCodeRds.监管一级分类, "12", "另类资产"
    ), 监管一级分类_公募基金(
            BussinessDictCodeRds.监管一级分类, "13", "公募基金"
    ), 监管一级分类_私募基金(
            BussinessDictCodeRds.监管一级分类, "14", "私募基金"
    ), 监管一级分类_资产管理基金(
            BussinessDictCodeRds.监管一级分类, "15", "资产管理基金"
    ), 监管一级分类_委外投资_协议方式(
            BussinessDictCodeRds.监管一级分类, "16", "委外投资_协议方式"
    ), 监管一级分类_同业拆入及卖出回购(
            BussinessDictCodeRds.监管一级分类, "17", "同业拆入及卖出回购"
    ), 监管一级分类_其他(
            BussinessDictCodeRds.监管一级分类, "18", "其他"
    ), 监管一级分类_货币市场工具(
            BussinessDictCodeRds.监管一级分类, "91", "货币市场工具"
    ), 监管一级分类_货币市场_负债(
            BussinessDictCodeRds.监管一级分类, "92", "货币市场_负债"
    ),

    系统资产大类_存款类资产(
            BussinessDictCodeRds.系统资产大类, "04", "存款类资产"
    ), 系统资产大类_债券类资产(
            BussinessDictCodeRds.系统资产大类, "05", "债券类资产"
    ), 系统资产大类_债券质押式回购交易(
            BussinessDictCodeRds.系统资产大类, "12", "债券质押式回购交易"
    ), 系统资产大类_债券借贷(
            BussinessDictCodeRds.系统资产大类, "212", "债券借贷"
    ), 系统资产大类_计划类资产_利率型(
            BussinessDictCodeRds.系统资产大类, "80", "计划类资产(利率型)"
    ), 系统资产大类_计划类资产_净值型(
            BussinessDictCodeRds.系统资产大类, "81", "计划类资产(净值型)"
    ), 系统资产大类_计划类资产_货币型(
            BussinessDictCodeRds.系统资产大类, "82", "计划类资产(货币型)"
    ), 系统资产大类_货币基金资产(
            BussinessDictCodeRds.系统资产大类, "84", "货币基金资产"
    ), 系统资产大类_非货币基金资产(
            BussinessDictCodeRds.系统资产大类, "85", "非货币基金资产"
    ), 系统资产大类_项目类资产(
            BussinessDictCodeRds.系统资产大类, "06", "项目类资产"
    ),

    估值舍入规则_四舍五入(
            BussinessDictCodeRds.估值舍入规则, "03", "四舍五入"
    ), 估值舍入规则_舍位(
            BussinessDictCodeRds.估值舍入规则, "02", "舍位"
    ), 估值舍入规则_进位(
            BussinessDictCodeRds.估值舍入规则, "01", "进位"
    ),

    确认状态_未确认(
            BussinessDictCodeRds.确认状态, "0", "未确认"
    ), 确认状态_已确认(
            BussinessDictCodeRds.确认状态, "1", "已确认"
    ),

    提醒类型_指标超限(
            BussinessDictCodeRds.提醒类型, "01", "指标超限"
    ), 提醒类型_固定值参数(
            BussinessDictCodeRds.提醒类型, "02", "固定值参数"
    ),
    
    
    
    //--------------------DICTCODE_LIST_EXT1--------------------------------------

    季度_第一季度(
            BussinessDictCodeRds.季度, "01", "第一季度"
    ), 季度_第二季度(
            BussinessDictCodeRds.季度, "02", "第二季度"
    ), 季度_第三季度(
            BussinessDictCodeRds.季度, "03", "第三季度"
    ), 季度_第四季度(
            BussinessDictCodeRds.季度, "04", "第四季度"
    ), 半年_上半年(
            BussinessDictCodeRds.半年, "01", "上半年"
    ), 半年_下半年(
            BussinessDictCodeRds.半年, "02", "下半年"
    ), 星期号_周日(
            BussinessDictCodeRds.星期号, "0", "周日"
    ), 星期号_周一(
            BussinessDictCodeRds.星期号, "1", "周一"
    ), 星期号_周二(
            BussinessDictCodeRds.星期号, "2", "周二"
    ), 星期号_周三(
            BussinessDictCodeRds.星期号, "3", "周三"
    ), 星期号_周四(
            BussinessDictCodeRds.星期号, "4", "周四"
    ), 星期号_周五(
            BussinessDictCodeRds.星期号, "5", "周五"
    ), 星期号_周六(
            BussinessDictCodeRds.星期号, "6", "周六"
    ), 子系统代码_共通(
            BussinessDictCodeRds.子系统代码, "COM", "共通"
    ), 企业性质_国有企业(
            BussinessDictCodeRds.企业性质, "01", "国有企业"
    ), 企业性质_集体企业(
            BussinessDictCodeRds.企业性质, "02", "集体企业"
    ), 企业性质_民营企业(
            BussinessDictCodeRds.企业性质, "03", "民营企业"
    ), 企业性质_外资企业(
            BussinessDictCodeRds.企业性质, "04", "外资企业"
    ), 企业性质_公众企业(
            BussinessDictCodeRds.企业性质, "05", "公众企业"
    ), 企业性质_其他企业(
            BussinessDictCodeRds.企业性质, "99", "其他企业"
    ), 有无_有(
            BussinessDictCodeRds.有无, "01", "有"
    ), 有无_无(
            BussinessDictCodeRds.有无, "02", "无"
    ),

    元素数据类型_下拉框(
            BussinessDictCodeRds.元素数据类型, "P", "下拉框"
    ), 元素数据类型_数值型(
            BussinessDictCodeRds.元素数据类型, "N", "数值型"
    ), 估值方法_市值法(
            BussinessDictCodeRds.估值方法, "01", "市值法"
    ), 估值方法_成本法(
            BussinessDictCodeRds.估值方法, "02", "成本法"
    ), 估值方法_市值成本孰低法(
            BussinessDictCodeRds.估值方法, "03", "市值成本孰低法"
    ), 估值方法_摊余成本法(
            BussinessDictCodeRds.估值方法, "04", "摊余成本法"
    ), 报表展示方式_饼状图(
            BussinessDictCodeRds.报表展示方式, "02", "饼状图"
    ), 报表展示方式_柱状图(
            BussinessDictCodeRds.报表展示方式, "03", "柱状图"
    ), 报表展示方式_折线图(
            BussinessDictCodeRds.报表展示方式, "04", "折线图"
    ), 报表展示方式_仪表盘(
            BussinessDictCodeRds.报表展示方式, "05", "仪表盘"
    ), 报表面板展示方式_1行1列(
            BussinessDictCodeRds.报表面板展示方式, "01", "1行1列"
    ), 报表面板展示方式_2行1列(
            BussinessDictCodeRds.报表面板展示方式, "02", "2行1列"
    ), 报表面板展示方式_3行1列(
            BussinessDictCodeRds.报表面板展示方式, "03", "3行1列"
    ), 报表面板展示方式_4行1列(
            BussinessDictCodeRds.报表面板展示方式, "04", "4行1列"
    ), 报表面板展示方式_1行2列(
            BussinessDictCodeRds.报表面板展示方式, "05", "1行2列"
    ), 报表面板展示方式_2行2列(
            BussinessDictCodeRds.报表面板展示方式, "06", "2行2列"
    ), 报表面板展示方式_3行2列(
            BussinessDictCodeRds.报表面板展示方式, "07", "3行2列"
    ), 报表面板展示方式_4行2列(
            BussinessDictCodeRds.报表面板展示方式, "08", "4行2列"
    ), 是或否_是(
            BussinessDictCodeRds.是或否, "01", "是"
    ), 是或否_否(
            BussinessDictCodeRds.是或否, "02", "否"
    ),

    数据类型_文字列(
            BussinessDictCodeRds.数据类型, "01", "文字列"
    ), 数据类型_下拉框(
            BussinessDictCodeRds.数据类型, "02", "下拉框"
    ), 数据类型_日期框(
            BussinessDictCodeRds.数据类型, "03", "日期框"
    ), 数据库类型_ORACLE(
            BussinessDictCodeRds.数据库类型, "01", "ORACLE"
    ), 数据库类型_DB2(
            BussinessDictCodeRds.数据库类型, "02", "DB2"
    ), 数据库类型_MYSQL(
            BussinessDictCodeRds.数据库类型, "03", "MYSQL"
    ), 数据库类型_SQLSERVER(
            BussinessDictCodeRds.数据库类型, "04", "SQLSERVER"
    ),

    映射类型_未映射(
            BussinessDictCodeRds.映射类型, "01", "未映射"
    ), 映射类型_已映射(
            BussinessDictCodeRds.映射类型, "02", "已映射"
    ),
    交易状态_修改(
            BussinessDictCodeRds.交易状态, "02", "修改"
    ), 交易场所_OTC场外(
            BussinessDictCodeRds.交易场所, "09", "OTC场外"
    ),

    监督类型_监管(
            BussinessDictCodeRds.监督类型, "SUPERVISION", "监管"
    ), 监督类型_内控(
            BussinessDictCodeRds.监督类型, "INTERNAL", "内控"
    ), 监督频率_日终(
            BussinessDictCodeRds.监督频率, "DAYEND", "日终"
    ), 监督频率_日间(
            BussinessDictCodeRds.监督频率, "DAYTIME", "日间"
    ), 单位_元(
            BussinessDictCodeRds.单位, "RMB", "元"
    ), 单位_年(
            BussinessDictCodeRds.单位, "YEAR", "年"
    ), 单位_日(
            BussinessDictCodeRds.单位, "DAY", "日"
    ), 单位_PERCENT(
            BussinessDictCodeRds.单位, "PERCENT", "%"
    ),

    节假日_交易市场_银行间(
            BussinessDictCodeRds.节假日_交易市场, "IB", "银行间"
    ), 节假日_交易市场_上交所(
            BussinessDictCodeRds.节假日_交易市场, "SH", "上交所"
    ), 节假日_交易市场_深交所(
            BussinessDictCodeRds.节假日_交易市场, "SZ", "深交所"
    ), 是否节假日_否(
            BussinessDictCodeRds.是否节假日, "0", "否"
    ), 是否节假日_是(
            BussinessDictCodeRds.是否节假日, "1", "是"
    ), 日志级别_信息(
            BussinessDictCodeRds.日志级别, "INFO", "信息"
    ), 日志级别_警告(
            BussinessDictCodeRds.日志级别, "WARNING", "警告"
    ), 日志级别_错误(
            BussinessDictCodeRds.日志级别, "ERROR", "错误"
    ), 募集方式_私募(
            BussinessDictCodeRds.募集方式, "2", "私募"
    ),

    /**
     * 特定资产类型设置
     */
    共同业务类型_特定资产类型设置(BussinessDictCodeRds.共同业务类型, "RSK34", "特定资产类型设置"),

    /**
     * 流动性权重配置
     */
    共同业务类型_流动性权重配置(BussinessDictCodeRds.共同业务类型, "RSK35", "流动性权重配置"),

    枚举LIST_EXT转换LIST(BussinessDictCodeRds.是否,"Y","是"
    )
    , 参数数据类型_文本(
            DictCode.报表数据格式类型, "String", "文本"
    ), 参数数据类型_日期(
            DictCode.报表数据格式类型, "Date", "日期"
    )
    //add by libaisong 2020-12-29 start
    /**
     * 商业银行净资产维护_有效状态
     */
    ,商业银行净资产维护_有效状态_有效(BussinessDictCodeRds.商业银行净资产维护_有效状态,"E","有效")
    ,商业银行净资产维护_有效状态_失效(BussinessDictCodeRds.商业银行净资产维护_有效状态,"D","失效")
    //add by libaisong 2020-12-29 end

    ,共通业务类型_商业银行净资产维护(
            BussinessDictCodeRds.共通业务类型, "RSK37", "商业银行净资产维护"
    )
    /**
     * 开放周期_小于90天
     */
    ,开放周期_小于90天(BussinessDictCodeRds.开放周期,"01","小于90天")
    /**
     * 开放周期_大于等于90天
     */
    ,开放周期_大于等于90天(BussinessDictCodeRds.开放周期,"02","大于等于90天")
    /**
     * 处理类型_规则
     */
    ,处理类型_规则(BussinessDictCodeRds.处理类型,"RULE","规则")
    /**
     * 处理类型_组合包
     */
    ,处理类型_组合包(BussinessDictCodeRds.处理类型,"PRT","组合包")
    /**
     * 日期选项_申购开始日
     */
    ,日期选项_申购开始日(BussinessDictCodeRds.日期选项,"01","申购开始日")
    /**
     * 日期选项_申购开始日
     */
    ,日期选项_申购结束日(BussinessDictCodeRds.日期选项,"02","申购结束日")
    /**
     * 日期选项_申购开始日
     */
    ,日期选项_申购确认日(BussinessDictCodeRds.日期选项,"03","申购确认日")
    /**
     * 日期选项_申购开始日
     */
    ,日期选项_赎回开始日(BussinessDictCodeRds.日期选项,"04","赎回开始日")
    /**
     * 日期选项_申购开始日
     */
    ,日期选项_赎回结束日(BussinessDictCodeRds.日期选项,"05","赎回结束日")
    /**
     * 日期选项_申购开始日
     */
    ,日期选项_赎回确认日(BussinessDictCodeRds.日期选项,"06","赎回确认日")
    /**
     * 日期类型_信批_工作日
     */
    ,日期类型_信批_工作日(BussinessDictCodeRds.日期类型_信批,"01","工作日")
    /**
     * 日期类型_信批_自然日
     */
    ,日期类型_信批_自然日(BussinessDictCodeRds.日期类型_信批,"02","自然日")
    /**
     * 是否面向单一投资者_是
     */
    ,是否面向单一投资者_是(BussinessDictCodeRds.是否面向单一投资者,"Y","是")
    /**
     * 是否面向单一投资者_否
     */
    ,是否面向单一投资者_否(BussinessDictCodeRds.是否面向单一投资者,"N","否")
    /**
     * 说明书是否披露风险标志_是
     */
    ,说明书是否披露风险标志_是(BussinessDictCodeRds.说明书是否披露风险标志,"Y","是")
    /**
     * 说明书是否披露风险标志_否
     */
    ,说明书是否披露风险标志_否(BussinessDictCodeRds.说明书是否披露风险标志,"N","否")
  ;


    /**
     * 对应于数据字典code
     */
    private String value;

    /**
     * 数据字典枚举
     */
    private BaseDictCode dictCode;

    /**
     * 明细名称
     */
    private String codeName;
    BussinessDictCodeListRds(BaseDictCode dictCode, String value, String codeName) {
        this.dictCode = dictCode;
        this.value = value;
        this.setCodeName(codeName);
    }

    /**
     * 获得数据字典详细类
     *
     * @param value 数据字典值
     * @param dictCode 数据字典DictCode
     * @return BussinessDictCodeList
     * @see
     * @since 1.0
     */
    public static BussinessDictCodeListRds from(BaseDictCode dictCode, String value) {
        BussinessDictCodeListRds[] values = BussinessDictCodeListRds.values();
        for (BussinessDictCodeListRds code : values) {
            if (StringUtil.equals(value, code.getValue()) && code.getDictCode().equals(dictCode)) {
                return code;
            }
        }
        return null;
    }

    /**
     * 获取数据字典明细List
     *
     * @param dictCode DictCode
     * @return List&lt;SelectOptionDto&gt;
     * @see
     * @since 1.0
     */
    public static List<SelectOptionDto> fromDictCode(BaseDictCode dictCode) {
        List<SelectOptionDto> result = new ArrayList<>();
        BussinessDictCodeListRds[] values = BussinessDictCodeListRds.values();
        for (BussinessDictCodeListRds code : values) {
            if (code.getDictCode() == dictCode) {
                SelectOptionDto option = new SelectOptionDto();
                option.setId(code.getValue());
                option.setText(code.getCodeName());
                result.add(option);
            }
        }
        return result;
    }

    /**
     * 获取数据字典明细valueList
     *
     * @param dictCode DictCode
     * @return List&lt;SelectOptionDto&gt;
     * @see
     * @since 1.0
     */
    public static List<String> valuesfromDictCode(BaseDictCode dictCode) {
        List<String> result = new ArrayList<>();
        BussinessDictCodeListRds[] values = BussinessDictCodeListRds.values();
        for (BussinessDictCodeListRds code : values) {
            if (code.getDictCode() == dictCode) {
                result.add(code.getValue());
            }
        }
        return result;
    }

    /**
     * 获取 DictCode
     *
     * @return value.
     */
    @Override
    public BaseDictCode getDictCode() {
        return dictCode;
    }

    /**
     * 获取 value
     *
     * @return value.
     */
    @Override
    public String getValue() {
        return value;
    }
    /**
     * 设置 value
     *
     * @param value value
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }
    /**
     * 获取 codeName
     *
     * @return codeName.
     */
    @Override
    public String getCodeName() {
        return codeName;
    }
    /**
     * 获取 带有引号的名称
     *
     * @return 带有引号的名称.
     */
    public String getQuotesCodeName() {
        return "“" + codeName + "”";
    }
    /**
     * 获取Integer value
     *
     * @return Integer value
     */
    public Integer getIntegerCodeName() {
        Integer result = null;
        try {
            result = Integer.parseInt(codeName);
        }
        catch (Exception e) {
        }
        return result;
    }
    /**
     * 获取Integer value
     *
     * @return Integer value
     */
    public BigDecimal getBigDecimalCodeName() {
        BigDecimal result = null;
        try {
            result = new BigDecimal(codeName);
        }
        catch (Exception e) {
        }
        return result;
    }
    /**
     * 设置 codeName
     *
     * @param codeName codeName
     */
    @Override
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
}

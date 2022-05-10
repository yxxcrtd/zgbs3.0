/*
 * 文件名：IAssetInfo.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年9月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.riskinfrds.info.entity;

import java.util.Date;

import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.riskinfrds.base.entity.IBusiness;

/**
 * 资产基本信息Redis存储结构
 * 
 * @author zhangrui1
 * @version 1.0 2019年9月18日
 * @see IAssetInfo
 * @since 1.0
 */
public interface IAssetInfo extends IBusiness {

    /**
     * {@inheritDoc}
     */
    @Override
    default String getKey() {
        return RedisKeyEnum.资产基本信息.key();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default String getField() {
        return getAssetCode();
    }

    /**
     * 取得 资产代码
     * 
     * @return 资产代码
     */
    String getAssetCode();

    /**
     * 取得 资产名称
     * 
     * @return 资产名称
     */
    String getAssetName();

    /**
     * 取得 起息日
     * 
     * @return 起息日
     */
    Date getVdate();

    /**
     * 取得 到期日
     * 
     * @return 到期日
     */
    Date getMdate();

    /**
     * 取得 系统资产大类(对应投管系统投管系统大类type8)
     * 
     * @return 系统资产大类(对应投管系统投管系统大类type8)
     */
    String getAssetType();

    /**
     * 取得 系统资产细类(对应投管系统投管系统大类标的：type6 非标的：type7)
     * 
     * @return 系统资产细类(对应投管系统投管系统大类标的：type6 非标的：type7)
     */
    String getAssetType2();

    /**
     * 取得 监管一级分类
     * 
     * @return 监管一级分类
     */
    String getType1();

    /**
     * 取得 监管二级分类
     * 
     * @return 监管二级分类
     */
    String getType2();

    /**
     * 取得 WIND一级分类
     * 
     * @return WIND一级分类
     */
    String getWindType1();

    /**
     * 取得 WIND二级分类
     * 
     * @return WIND二级分类
     */
    String getWindType2();

    /**
     * 取得 投资性质
     * 
     * @return 投资性质
     */
    String getInvestType();

    /**
     * 取得 交易日历代码
     * 
     * @return 交易日历代码
     */
    String getCalendarCode();

    /**
     * 取得 市场代码
     * 
     * @return 市场代码
     */
    String getMarketCode();

    /**
     * 取得 交易产所
     * 
     * @return 交易产所
     */
    String getTradePlace();

    /**
     * 取得 所属wind行业一级分类
     * 
     * @return 所属wind行业一级分类
     * @see
     * @since 1.0
     */
    String getIndustry();

    /**
     * 取得 所属wind行业二级分类
     * 
     * @return 所属wind行业二级分类
     * @see
     * @since 1.0
     */
    String getIndustry2();

    /**
     * 取得 所属债项评级信息
     * @return 所属债项评级信息
     */
    String getBondGradeType();

    /**
     * 取得 所属主体评级信息
     * @return 所属主体评级信息
     */
    String getBodyGradeType();

    /**
     * 取得 资产发行方式
     * @return 资产发行方式(公开,非公开)
     */
    String getIssueType();

    /**
     * 取得 发行机构(处理过后的机构ID)
     * @return 发行机构
     */
    String getIssuerCode();

    /**
     * 取得 行内二级类型
     * @return 行内二级类型
     */
    String getType9();

    /**
     * 取得 行内二级类型
     * @return 行内二级类型
     */
    String getType10();

    /**
     * 取得 行内三级类型
     * @return 行内三级类型
     */
    String getType11();

    /**
     * 取得 行内业务类型
     * @return 行内业务类型
     */
    String getType12();

    /**
     * 取得 股票类型
     * @return 股票类型
     */
    String getStockShareType();

    /**
     * 取得 股票交易状态(行情)
     * @return 股票交易状态(行情)
     */
    String getStockTradeStatus();

    /**
     * 取得 计划类资产最新赎回日
     * @return 计划类资产最新赎回日
     */
    Date getAstLastRedempDate();

    /**
     * 取得 资产最新还本日
     * @return 计划类资产最新还本日
     */
    Date getAstLastRebackDate();

    /**
     * 取得 资产最新付息日
     * @return 资产最新付息日
     */
    Date getAstLastPayDate();

    /**
     * 取得 名单级别
     * @return
     */
    String getListLevel();

    /**
     * 取得 是否有关联方
     * @return
     */
    String getIsCorOrg();
    // add by zhangrui1 增加资产属性 start
    /**
     * 取得 是否永续
     * 
     * @return 是否永续
     */
    String getIsSus();

    /**
     * 取得 是否含权
     * 
     * @return 是否含权
     */
    String getIsRight();

    /**
     * 取得 最终利率调整期开始日
     * 
     * @return 最终利率调整期开始日
     */
    Date getVdateLast();

    /**
     * 取得 利率类型
     * 
     * @return 利率类型
     */
    String getCalcType();
    // add by zhangrui1 增加资产属性 end

    // add by etliu 增加资产属性:是否可提前支取 20200821 start
    /**
     * 取得 是否可提前支取
     *
     * @return 是否可提前支取
     */
    String getIsCanDrawInAdvance();
    // add by etliu 增加资产属性:是否可提前支取 20200821 end

    // add by zhangrui1 增加资产属性:是否流动性受限资产 start
    /**
     * 获取 是否流动性受限
     * 
     * @return 是否流动性受限.
     */
    String getIsFlowLimited();
    /**
     * 获取 是否无活跃交易市场
     *
     * @return 是否无活跃交易市场.
     */
    String getIsLiquidity1();
    /**
     * 获取 是否不具备活跃交易市场
     *
     * @return 是否不具备活跃交易市场
     */
    String getIsLiquidity2();
    /**
     * 获取 是否7个工作日可变现资产
     *
     * @return 是否7个工作日可变现资产
     */
    String getIsLiquidity3();

    /**
     * 标的是否7日可变现
     * @return
     */
    String getIsSevenDayCashable();

    /**
     * 是否每日开放申赎
     * @return
     */
    String getIsOpenEveryDay();

    /**
     * 是否不良资产
     * @return
     */
    String getIsBadAsset();
    // add by zhangrui1 增加资产属性:是否流动性受限资产 end
    // add by zhangrui1 增加资产属性:机构主体类型 start
    /**
     * 获取 机构主体类型
     * 
     * @return 机构主体类型.
     */
    String getCustomerObjType();
    // add by zhangrui1 增加资产属性:机构主体类型 end
}

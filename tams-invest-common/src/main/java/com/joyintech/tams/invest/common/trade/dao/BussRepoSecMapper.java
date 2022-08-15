/*
 * 文件名：TrdRepoTrade003Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：陈志力
 * 修改时间：2019年01月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.common.trade.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;

/**
 * 质押劵管理共通组件-微服务调用Mapper
 *
 * @author 陈志力
 * @version 1.0 2019年01月11日
 * @see TrdCommonBaseMapper
 * @since 1.0
 */
@Repository
public interface BussRepoSecMapper {

    /**
     * 债券代码：下拉框
     *
     * @param pageParam 画面参数
     * @return 债券代码：下拉框
     */
    List<SelectOptionDto> getBondCode(Map<String, Object> pageParam);
    
    /**
     * 债券质押回购多组合新增
     * 张庚林
     * 20190723
     */   
    PledgeBondInfoDto portfolioName(Map<String, Object> pageParam);

    /**
     * 根据画面条件检索可质押券信息(正回购时）
     *
     * @param params 画面参数
     * @return List<PledgeBondInfoDto>
     */
    List<PledgeBondInfoDto> selectAvaPledgeBondDetailListSellRepo(Map<String, Object> params);

    /**
     * 根据画面条件检索可质押券信息(逆回购时）
     *
     * @param params 画面参数
     * @return List<PledgeBondInfoDto>
     */
    List<PledgeBondInfoDto> selectAvaPledgeBondDetailListAntiRepo(Map<String, Object> params);

    /**
     * 通过entrustId/bizCode检索已质押券信息(正回购时）
     *
     * @param params 画面参数
     * @return List<BussRepoSecDto>
     */
    List<PledgeBondInfoDto> selectBussRepoSecSellRepo(Map<String, Object> params);
    List<PledgeBondInfoDto> selectBussRepoSecPtlSellRepo(Map<String, Object> params);

    /**
     * 通过entrustId/bizCode检索已质押券信息(逆回购时）
     *
     * @param params 画面参数
     * @return List<BussRepoSecDto>
     */
    List<PledgeBondInfoDto> selectBussRepoSecAntiRepo(Map<String, Object> params);
    List<PledgeBondInfoDto> selectBussRepoSecPtlAntiRepo(Map<String, Object> params);

    /**
     * 撤销委托/回购交易时，删除回购质押品信息
     *
     * @param param 删除参数
     * @return successCount
     * @see
     * @since 1.0
     */
    int deletePledgeBond(Map<String, Object> param);

    /**
     * 质押券置换换出列表按投资目的获取数据
     * nixiukuan
     * 20191129
     */
    List<PledgeBondInfoDto> selectOutPledgeBondDetailList(Map<String, Object> param);

    /**
     *查询证券账户信息
     * @param params
     * @return
     */
    String querySecAcctId(Map<String, Object> params);
    
    /**
     * 产品化_质押式回购交易界面,正回购时：点击添加后的可质押券范围为投资经理移入可质押库的、该组合下的债券，不可选不在质押库里债券
     * add by zhangpei 20201116
     * @param params 画面参数
     * @return List<PledgeBondInfoDto>
     */
    List<PledgeBondInfoDto> selectAvaPledgeBondDetailListSellRepoForEtrPledge(Map<String, Object> params);

    /**
     * 银行间上下行多组合变更
     * add by zhangpei 20201116
     * @param params 画面参数
     * @return List<PledgeBondInfoDto>
     */
    List<PledgeBondInfoDto> selectAvaPledgeBondDetailListSellRepoForBankDzh(Map<String, Object> params);

    List<PledgeBondInfoDto> selectNewAvaPledgeBondDetailList(Map<String, Object> params);

    List<String> selectFinprodIdByPledge (@Param("pledgeStockId")String pledgeStockId);
}

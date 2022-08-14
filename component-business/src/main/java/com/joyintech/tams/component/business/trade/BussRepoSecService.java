/*
 * 文件名：TrdRepoTrade003Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：陈志力
 * 修改时间：2019年01月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.component.business.trade;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;

/**
 * 质押劵管理共通组件-接口Service
 */
public interface BussRepoSecService {

    /**
     * 默认entity名称
     */
    String DEFAULT_ENTITY_NAME = "bussRepoSec";
    
    /**
     * 债券质押回购多组合新增
     * 张庚林
     * 20190723
     */
    String DEFAULT_ENTITY_NAME2 = "bussRepoSecPtl";

    /**
     * 引用画面：EtrRepo002（回购委托新建）
     */
    String PAGE_FROM_EtrRepo002 = "EtrRepo002";
    
    /**
     * 引用画面：EtrRepo002（回购委托新建）
     */
    String PAGE_FROM_EtrIbRepoinfo002 = "EtrIbRepoinfo002";
    
    /**
     * 引用画面：EtrRepo002（回购委托新建）
     */
    String PAGE_FROM_EtribRepoInfo003 = "EtribRepoInfo003";

    /**
     * 引用画面：TrdRepoTrade002（回购交易新建）
     */
    String PAGE_FROM_TrdRepoTrade002 = "TrdRepoTrade002";
    
    /**
     * 引用画面：TrdRepoTrade002（多组合回购交易新建）
     */
    String PAGE_FROM_TrdRepoTrade003 = "TrdRepoTrade003";

    String PAGE_FROM_TrdBondLendTrade002 = "TrdBondLendTrade002";

    String PAGE_FROM_EtrSecLend002 = "EtrSecLend002";



    /**
     * 债券代码：下拉框
     *
     * @param params 画面参数
     * @return 债券代码：下拉框
     */
    public List<SelectOptionDto> getBondCode(Map<String, Object> params);

    /**
     * 通过bizCode检索已质押券信息
     *
     * @param param 检索参数
     * @return BussRepoSecDto
     */
    public BussRepoSecDto selectBussRepoSec(Map<String, Object> param);
    
    /**
     * 债券质押回购多组合新增
     * 张庚林
     * 20190723
     */
    public PledgeBondInfoDto portfolioName(Map<String, Object> param);
    
    /**
     * 根据画面条件检索可质押券信息（分页）
     * @param params
     * @return
     */
    public Page<PledgeBondInfoDto> selectAvaPledgeBondDetailPage(Map<String, Object> params);
    
    /**
     * 根据画面条件检索可质押券信息
     *
     * @param params 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeBondDetailList(Map<String, Object> params);

    /**
     * 7.6写入回购质押品信息
     *
     * @param bussRepoSec bussRepoSec
     * @param bizCode     委托代码/交易编号
     * @return successCount
     * @see
     * @since 1.0
     */
    public int insert(BussRepoSecDto bussRepoSec, String tradeId);

    /**
     * 撤销委托/回购交易时，删除回购质押品信息
     *
     * @param bizCode  委托代码/交易编号
     * @param pageFrom pageFrom
     * @return successCount
     * @see
     * @since 1.0
     */
    public int deletePledgeBond(String tradeId, String pageFrom);

    /**
     * 根据组合代码查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
	public List<SuggestDto> getSecManageAcctId(Map<String, Object> param);
	
	/**
     * 自动导入质押券 查询
     *
     * @param param 画面参数
     * @return list
     * @see
     * @since 1.0
     */
	public List<PledgeBondInfoDto> selectAutoPledge(Map<String, String> param) ;

    int deleteAndInsert(String entrustId, String pageFrom, BussRepoSecDto bussRepoSec);

    /**
     *查询证券账户信息
     * @param params
     * @return
     */
    public String querySecAcctId(Map<String, Object> params);
}

/*
 * 文件名：AccountDriver.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统 修改人：qiangchengxi 修改时间：2017年1月3日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.valuation.batch.common.accountingmethod.entity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 记账标准参数类
 * 
 * @author qiangchengxi
 * @version 1.0 2017年1月3日
 * @see AccountDriver
 * @since 1.0
 */
public class AccountDriver {

    /**
     * 会计场景代码<br/>
     * 指定该笔记账所使用的会计事件。
     */
    private String tableId;

    /**
     * 记账场景要素<br/>
     * 存放该笔记账的场景要素
     */
    private Map<String, Object> accountSceneMap;

    /**
     * 记账金额要素<br/>
     * 存放该笔记账的相关金额要素
     */
    private Map<String, BigDecimal> accountAmountMap;

    /**
     * 记账金额币种<br/>
     * 存放该笔记账的相关金额对应的币种
     */
    private Map<String, Object> accountCurrencyMap;

    /**
     * 基础科目性质<br/>
     * 存该笔记账的相关基础科目性质对应的场景元素
     */
    private Map<String, Map<String, Object>> accountNatrueMap;
    
    /**
     * 是否累计余额：部分科目累计余额
     * key:基础科目性质  value:是否累计余额
     * add by wujie on 20201130
     */
    private Map<String,String> accountNatrueBalanceMap;
    
    /**
     * 记账场景树节点
     */
    private TreeNode treeNode;

    /**
     *  辅助核算项配置Map(bokDetailId,SubjectMappingConfig)
     */
    private SubjectMappingConfig subjectMappingConfigDefault;

    /**
     *  辅助核算项配置Map(科目性质,SubjectMappingConfig)
     */
    private Map<String,SubjectMappingConfig> subjectMappingConfigMap;

    /**
     * 获取 tableId
     * 
     * @return tableId.
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * 设置 tableId
     * 
     * @param tableId tableId
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    /**
     * 获取 accountSceneMap
     * 
     * @return accountSceneMap.
     */
    public Map<String, Object> getAccountSceneMap() {
        return accountSceneMap;
    }

    /**
     * 设置 accountSceneMap
     * 
     * @param accountSceneMap accountSceneMap
     */
    public void setAccountSceneMap(Map<String, Object> accountSceneMap) {
        this.accountSceneMap = accountSceneMap;
    }

    /**
     * 获取 accountAmountMap
     * 
     * @return accountAmountMap.
     */
    public Map<String, BigDecimal> getAccountAmountMap() {
        return accountAmountMap;
    }

    /**
     * 设置 accountAmountMap
     * 
     * @param accountAmountMap accountAmountMap
     */
    public void setAccountAmountMap(Map<String, BigDecimal> accountAmountMap) {
        this.accountAmountMap = accountAmountMap;
    }

    /**
     * 获取 accountCurrencyMap
     * 
     * @return accountCurrencyMap.
     */
    public Map<String, Object> getAccountCurrencyMap() {
        return accountCurrencyMap;
    }

    /**
     * 设置 accountCurrencyMap
     * 
     * @param accountCurrencyMap accountCurrencyMap
     */
    public void setAccountCurrencyMap(Map<String, Object> accountCurrencyMap) {
        this.accountCurrencyMap = accountCurrencyMap;
    }

    
    /**
     * 获取 accountNatrueMap
     * @return accountNatrueMap.
     */
    public Map<String, Map<String, Object>> getAccountNatrueMap() {
        return accountNatrueMap;
    }

    
    /**
     * 设置 accountNatrueMap
     * @param accountNatrueMap accountNatrueMap
     */
    public void setAccountNatrueMap(Map<String, Map<String, Object>> accountNatrueMap) {
        this.accountNatrueMap = accountNatrueMap;
    }

    /**
     * 获取 treeNode
     * 
     * @return treeNode.
     */
    public TreeNode getTreeNode() {
        return treeNode;
    }

    /**
     * 设置 treeNode
     * 
     * @param treeNode treeNode
     */
    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }

	public Map<String, String> getAccountNatrueBalanceMap() {
		return accountNatrueBalanceMap;
	}

	public void setAccountNatrueBalanceMap(Map<String, String> accountNatrueBalanceMap) {
		this.accountNatrueBalanceMap = accountNatrueBalanceMap;
	}

    /**
     *  获取 辅助核算项配置Map
     * @return 辅助核算项配置Map
     */
    public Map<String, SubjectMappingConfig> getSubjectMappingConfigMap() {
        return subjectMappingConfigMap;
    }

    /**
     * 设置 辅助核算项配置Map
     * @param subjectMappingConfig 辅助核算项配置Map
     */
    public void setSubjectMappingConfigMap(Map<String, SubjectMappingConfig> subjectMappingConfigMap) {
        this.subjectMappingConfigMap = subjectMappingConfigMap;
    }

    /**
     * 获取 默认辅助核算项配置
     * @return 默认辅助核算项配置
     */
    public SubjectMappingConfig getSubjectMappingConfigDefault() {
        return subjectMappingConfigDefault;
    }

    /**
     * 设置 默认辅助核算项配置
     * @param subjectMappingConfigDefault 默认辅助核算项配置
     */
    public void setSubjectMappingConfigDefault(SubjectMappingConfig subjectMappingConfigDefault) {
        this.subjectMappingConfigDefault = subjectMappingConfigDefault;
    }
}

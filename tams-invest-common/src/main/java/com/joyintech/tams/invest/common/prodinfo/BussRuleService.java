/*
 * 文件名：BussRuleService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.common.prodinfo;

import com.joyintech.tams.common.base.entity.RskRulePoolConfig;
import com.joyintech.tams.invest.common.prodinfo.dto.IPtlRule;

import java.util.List;
import com.joyintech.tams.invest.common.prodinfo.dto.IRskRulePoolConfig;
import java.util.function.Supplier;

/**
 * 规则信息
 * 〈功能详细描述〉
 * @author 裴家俊
 * @version 1.0 2019年10月11日
 * @see BussRuleService
 * @since 1.0
 */
public interface BussRuleService<V extends IPtlRule>{

    /**
     * 匹配规则的组合
     * @param ruleList
     * @return
     */
    List<String> matchRulePort(V ruleList);

    /**
     * 匹配规则的组合
     * @param ruleList
     * @return
     */
   <T extends IRskRulePoolConfig> List<String> matchRulePortForPage(List<T> ruleList);

    /**
     * 查询规则关联指标
     * @param ruleList
     * @return
     */
    List<String> queryRuleRelInd(V ruleList);

    /**
     * 查询规则关联组合
     * @param ruleList
     * @return
     */
    List<String> queryRuleRelPort(V ruleList);

    /**
     * 组合关联指标
     * @param portList
     * @param indIdList
     * @return
     */
   void relatePortIndicator(List<String> portList, List<String> indIdList);

    /**
     * 规则匹配组合入库
     * @param portList 组合
     * @param ruleList 规则ID
     * @param supplier 判断是否从组合维度出发匹配，若是则带组合条件去删除
     * @return
     */
    void insertRulePortList(List<String> portList, List<String> ruleList, Supplier<List<String>> supplier);
}

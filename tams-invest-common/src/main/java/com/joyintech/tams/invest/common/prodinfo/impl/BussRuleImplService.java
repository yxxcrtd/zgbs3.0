/*
 * 文件名：BussRuleImplService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.common.prodinfo.impl;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.RskPortfolioIndicator;
import com.joyintech.tams.common.base.entity.RskRulePortfolio;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.batchList.service.BatchListService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.prodinfo.BussRuleService;
import com.joyintech.tams.invest.common.prodinfo.dao.RskRuleCommonMapper;
import com.joyintech.tams.invest.common.prodinfo.dto.IPtlRule;
import com.joyintech.tams.invest.common.prodinfo.dto.IRskRulePoolConfig;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 规则匹配类
 * 〈功能详细描述〉
 * @author 裴家俊
 * @version 1.0 2019年10月11日
 * @see BussRuleImplService
 * @since 1.0
 */
@Service
public class BussRuleImplService extends BaseService implements BussRuleService<IPtlRule> {

    @Autowired
    private BatchListService<RskPortfolioIndicator> rskPortIndicatorService;

    @Autowired
    private RskRuleCommonMapper rskRuleCommonMapper;

    @Autowired(required = false)
    public IPlatformDate platformDate;

    /**
     * 匹配获取相应的组合
     * @param ruleData
     * @return
     */
    @Override
    public List<String> matchRulePort(IPtlRule ruleData) {
        //查询规则信息，按参数序号倒序排列
        List<IRskRulePoolConfig> resultList=rskRuleCommonMapper.getRulePoolConfig(ruleData.getRuleId());
        if(StringUtil.isEmptyList(resultList)){
            return new ArrayList<String>();
        }
        return matchCommonRule(resultList);
    }

    /**
     * 匹配获取相应的组合(直接走内存，不走库，分布式事务)
     * @param resultList
     * @return
     */
    @Override
    public <T extends IRskRulePoolConfig> List<String>  matchRulePortForPage(List<T> resultList) {
        if(StringUtil.isEmptyList(resultList)){
            return new ArrayList<String>();
        }
        //查询规则信息，按参数序号倒序排列
        Collections.sort(resultList, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o2.getParamNum().compareTo(o1.getParamNum());
            }
        });
        return matchCommonRule(resultList);
    }

    private <T extends IRskRulePoolConfig> List<String> matchCommonRule(List<T> resultList){
        List<String> portList =new ArrayList<>();
        Date platDate=platformDate.getSysDate();
        String sqlWhere=StringUtil.EMPTY;
        //系统内处理为（（A∪B）∩C）∪D，由外向内逐层处理
        for(IRskRulePoolConfig dto:resultList){
            //---流动性风险改造过滤开放周期 start---
            if(CommonUtil.isType(dto.getClassifyDimension(),BussinessDictCodeListRds.分类维度_开放周期)){
                portList= rskRuleCommonMapper.getMatchPortData(RiskUtil.getTableColumn(DictCodeListExt1.分类维度_开放类型.getValue())
                        +"='"+ DictCodeList.开放类型_开放式.getValue()+"'",BussinessDictCodeListRds.是否_否.getValue(),DateUtil.format(StringUtil.isNotEmpty(platDate) ? platDate : new Date()));
                sqlWhere=dto.getPoolSql(portList,sqlWhere);
            }else {
                sqlWhere=dto.getPoolSql(portList,sqlWhere);
            }
            //---流动性风险改造过滤开放周期 end---
        }
        //查询符合规则的组合且在组合关联指标表中已有的组合排除，即之前已绑定的不用再管了(现改成不排除)
        portList= rskRuleCommonMapper.getMatchPortData(sqlWhere,BussinessDictCodeListRds.是否_否.getValue(),DateUtil.format(StringUtil.isNotEmpty(platDate) ? platDate : new Date()));
        return portList;
    }

    /**
     * 根据规则ID获取关联的指标
     * @param ruleData
     * @return
     */
    @Override
    public List<String> queryRuleRelInd(IPtlRule ruleData) {
        List<String> ruleIdList=Arrays.asList(ruleData).stream().map(IPtlRule::getRuleId).collect(Collectors.toList());
        List<String> indIdList=rskRuleCommonMapper.selectRuleRelInd(ruleIdList);
        return indIdList;
    }

    /**
     * 根据规则ID获取关联的组合
     * @param ruleData
     * @return
     */
    @Override
    public List<String> queryRuleRelPort(IPtlRule ruleData) {
        List<String> ruleIdList=Arrays.asList(ruleData).stream().map(IPtlRule::getRuleId).collect(Collectors.toList());
        List<String> indIdList=rskRuleCommonMapper.selectRuleRelPort(ruleIdList);
        return indIdList;
    }

    @Override
    public void relatePortIndicator(List<String> portList,List<String> indIdList) {
        List<RskPortfolioIndicator> list=new ArrayList<>();
        Date sysDate=DateUtil.platDateFromCache();
        portList.forEach(item->{
            indIdList.forEach(key->{
                RskPortfolioIndicator rskPort=new RskPortfolioIndicator();
                rskPort.setPortfolioId(item);
                rskPort.setIndId(key);
                rskPort.setPortfolioLevel(BussinessDictCodeListRds.组合层次_单组合.getValue());
                rskPort.setStartDate(sysDate);
                list.add(rskPort);
            });
        });
        //删除操作
        deleteBatchList(list);
        rskPortIndicatorService.setBatchListMapper(rskRuleCommonMapper);
        rskRuleCommonMapper.insertBatchList(list);
    }

    @Override
    public void insertRulePortList(List<String> portList, List<String> ruleList, Supplier<List<String>> supplier) {
        if(StringUtil.isEmptyList(portList)||StringUtil.isEmptyList(ruleList)){
            return;
        }
        //是否从组合维度出发的,非空代表从组合维度出发，空代表从规则维度出发
        List<String> isPortCondList=supplier.get();
        if(!StringUtil.isEmptyList(isPortCondList)){
            //从组合维度出发的需删除匹配规则之外其它规则与该组合的关联关系
            rskRuleCommonMapper.deleteRulePortReverse(ruleList,portList);
        }
        ruleList.forEach(ruleId->{
            List<RskRulePortfolio> list=new ArrayList<>();
            portList.forEach(item->{
                RskRulePortfolio rskPort=new RskRulePortfolio();
                rskPort.setRuleId(ruleId);
                rskPort.setPortfolioId(item);
                list.add(rskPort);
            });
            //删除操作
            rskRuleCommonMapper.deleteRulePort(ruleId,isPortCondList);
            rskRuleCommonMapper.insertRskRulePortBatchList(list);
        });
    }

    /**
     * 删除组合关联指标
     * @param list
     */
    @Transactional(rollbackFor = Throwable.class)
    void deleteBatchList(List<RskPortfolioIndicator> list){
        if (!StringUtil.isEmptyList(list)) {
            int size = list.size();
            int tail = size % 50;
            rskRuleCommonMapper.deletePortIndicator(list.subList(0, tail));
            int len = size / 50;
            for (int i = 0; i < len; i++ ) {
                int start = tail + i * 50;
                rskRuleCommonMapper.deletePortIndicator(list.subList(start, start + 50));
            }
        }
    }

}

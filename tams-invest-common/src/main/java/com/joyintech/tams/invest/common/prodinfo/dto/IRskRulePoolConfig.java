package com.joyintech.tams.invest.common.prodinfo.dto;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.RskRulePoolConfig;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.asset.util.AssetCommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IRskRulePoolConfig extends RskRulePoolConfig {

    public  String getPoolColumn(){
        return RiskUtil.getTableColumn(getClassifyDimension());
    }

    public  String getPoolSql(List<String> portList,String sqlWhere){
        return RiskUtil.getTableColumnSql(getLinkSign(),getPoolColumn(),  CommonUtil.isType(getClassifyDimension(),BussinessDictCodeListRds.分类维度_开放周期)?
                matchEnableTime(portList):getClassifyDimensionVal(),sqlWhere);
    }

    /**
     * 匹配开放周期
     * @param portList
     * @return 返回匹配到的组合，多个用逗号分隔
     */
    public  String matchEnableTime(List<String> portList){
        //暂时支持90天比较
        if(CommonUtil.isType(getClassifyDimension(), BussinessDictCodeListRds.分类维度_开放周期)) {
            Integer compare= RiskUtil.openCycleMap.get(getClassifyDimensionVal());
            if(null!=compare){
                Map<String,Integer> ptlCycleMap= AssetCommonUtil.comparePtlCycle(portList,compare,null);
                if(!CollectionUtils.isEmpty(ptlCycleMap)) {
                    //使用迭代器删除防止修改modCount属性。导致抛出异常java.util.ConcurrentModificationException
                    Iterator<String> iterator = portList.iterator();
                    while (iterator.hasNext()) {
                        String item=iterator.next();
                        Integer cycle=ptlCycleMap.getOrDefault(item,null);
                        if (!match(cycle==null?null:cycle==-1?BussinessDictCodeListRds.开放周期_小于90天.getValue():
                                cycle>=0?BussinessDictCodeListRds.开放周期_大于等于90天.getValue():null)) {
                            iterator.remove();
                        }
                    };
                    return portList.size()==0?"null":StringUtil.joinString(portList, GlobalConstant.COMMA);
                }
            }
        }
        return "null";
    }


    /**
     * 匹配配置项
     * @param val
     * @return
     */
    private boolean match(String val){
        if(null==val)return true;
        List<String> clsValList=StringUtil.splitToList(GlobalConstant.COMMA,getClassifyDimensionVal());
        return clsValList.contains(val);
    }
}
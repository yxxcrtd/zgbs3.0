/*
 * 文件名：InvestTradeQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年7月25日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.trademanage.investtradequery.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.valuation.trademanage.investtradequery.dao.InvestTradeQuery001Mapper;
import com.joyintech.service.valuation.trademanage.investtradequery.dto.InvestTradeDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 投资交易查询-service
 * 
 * @author yangjian
 * @version 1.0 2018年7月25日
 * @see InvestTradeQuery001Service
 * @since 1.0
 */
@Service(value="serviceInvestTradeQuery001Service")
public class InvestTradeQuery001Service extends CommonExportService<InvestTradeDto> {

    /**
     * 数据获取
     */
    @Autowired
    private InvestTradeQuery001Mapper mapper;

    /**
     * 投资交易分页查询
     * 
     * @param params 参数
     * @return 投资交易分页数据
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<InvestTradeDto> findPage(int pageNum, int length, Map<String, Object> params) {

        // 获取业务类型(筛选条件的业务类型和符合页面查询的业务类型同时过滤)
        String[] busiTypeArr = this.filterBusitype((String)params.get("selectBusiType"), (String)params.get("filterBusiType"));
        // 每个页面都必须有业务类型，故不存在无业务类型查询全部的情况
        if(busiTypeArr==null || busiTypeArr.length==0){
            return new Page<InvestTradeDto>();
        }
        params.put("paBusiTypeArr", busiTypeArr);
        String paAsset = (String) params.get("paAsset");
        List<String> queryTypeList = new ArrayList<String>();
        // 当输入资产代码时，只查询有对应的资产代码的数据
        if(StringUtils.isNotEmpty(paAsset)){
//            queryTypeList.add(DictCodeList.业务类型_债券买断式回购交易.getValue());
//            queryTypeList.add(DictCodeList.业务类型_债券质押式回购交易.getValue());
            queryTypeList.add(DictCodeList.业务类型_债券交易.getValue());
            queryTypeList.add(DictCodeList.业务类型_债券交易预处理.getValue());
            queryTypeList.add(DictCodeList.业务类型_债券借贷交易.getValue());
//            queryTypeList.add(DictCodeList.业务类型_交易所债券交易.getValue());
            queryTypeList.add(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
            queryTypeList.add(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
            queryTypeList.add(DictCodeList.业务类型_存款类资产交易.getValue());
            queryTypeList.add(DictCodeList.业务类型_资产重分类.getValue());
            //<!-- modify by huangjian on 20190111 start -->
            queryTypeList.add(DictCodeList.业务类型_计划类资产交易_类债券资产交易.getValue());
            //<!-- modify by huangjian on 20190111 end -->

        }
        params.put("queryTypeList", queryTypeList);

        // 获取投资账户
        String bookSetIdArr = (String)params.get("bookSetId");
        params.put("bookSetIdArr", this.String2Arr(bookSetIdArr));//paAcctSubjectTypeString

        // 获取交易方向
        String paPs = (String)params.get("paPs");
        params.put("paPsArr", this.String2Arr(paPs));

        String filterStr =  (String)params.get("filterBusiType");
        Page<InvestTradeDto> page = new Page<>();
        page.setRoot(Lists.newArrayList());
        // 收益类型
        String profitType = (String)params.get("profitType");
        if(filterStr.contains("02") && filterStr.contains("20") && filterStr.contains("24")) {//过滤业务类型 - 债券交易,交易所债券交易、债券预交易
            page = super.queryForPage(pageNum, length, () -> mapper.selectBondList(params));
        }else if(filterStr.contains("03")){ //存借款交易
            page = super.queryForPage(pageNum, length, () -> mapper.queryPageList(params));
        }else if(filterStr.contains("01")){// 拆借交易
            page = super.queryForPage(pageNum, length, () -> mapper.selectLendList(params));
        }else if(filterStr.contains("06")||filterStr.contains("07")){//过滤业务类型 - 基金类资产
            page = super.queryForPage(pageNum, length, () -> mapper.queryFundCurrencyList(params));
        }else if(filterStr.contains("15")){//过滤业务类型 - 活期存款收息交易
            page = super.queryForPage(pageNum, length, () -> mapper.queryCurrentDepositList(params));
        }else if(filterStr.contains("19")){//过滤业务类型 - 交易账户收划款
            page = super.queryForPage(pageNum, length, () -> mapper.queryTradeAccountList(params));
        }if(filterStr.contains("04") || filterStr.contains("05")){// 回购交易（债券质押式回购交易 和 债券买断式回购交易）
            page = super.queryForPage(pageNum, length, () -> mapper.selectRepurchaseList(params));
        }if(filterStr.contains("18")){ //现金类交易(费用交易)
            page = super.queryForPage(pageNum, length,() -> mapper.queryCashTransactions(params));
        }if(filterStr.contains("16")){ //增值税交易查询
            page = super.queryForPage(pageNum, length, () -> mapper.queryValueAddedTaxs(params));
        }else if(filterStr.contains("09") && "01".equals(profitType)){//计划类资产（profitType：01-净值型；02-收益型；03-货币型）
            page = super.queryForPage(pageNum, length, () -> mapper.selectPlanNetvalueList(params));
        }else if(filterStr.contains("09") && "02".equals(profitType)){//计划类资产（profitType：01-净值型；02-收益型；03-货币型）
            page = super.queryForPage(pageNum, length, () -> mapper.selectPlanProfitList(params));
        }else if(filterStr.contains("09") && "03".equals(profitType)){//计划类资产（profitType：01-净值型；02-收益型；03-货币型）
            page = super.queryForPage(pageNum, length, () -> mapper.selectPlanCureencyList(params));
        }else if(filterStr.contains("30")){//资产重分类
            page = super.queryForPage(pageNum, length, () -> mapper.selectReclassList(params));
        }else if(filterStr.contains("14")){//债券借贷交易查询
            page = super.queryForPage(pageNum, length, () -> mapper.selectBondLendList(params));
        }else if(filterStr.contains("37")){//逾期罚息交易查询
            page = super.queryForPage(pageNum, length, () -> mapper.selectOverduePenaltyList(params));
        }

        return page;
    }
    
    
    /**
     * 投资交易分页查询
     * 
     * @param params 参数
     * @return 投资交易分页数据
     * @throws Exception
     * @see
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
    public List<InvestTradeDto> findList(Map<String, Object> params) {
        List list = new ArrayList();
        // 获取业务类型(筛选条件的业务类型和符合页面查询的业务类型同时过滤)
        String[] busiTypeArr = this.filterBusitype((String)params.get("selectBusiType"), (String)params.get("filterBusiType"));
        // 每个页面都必须有业务类型，故不存在无业务类型查询全部的情况
        if(busiTypeArr==null || busiTypeArr.length==0){
            return new ArrayList<InvestTradeDto>();
        }
        params.put("paBusiTypeArr", busiTypeArr);

        // 获取投资账户
        String bookSetIdArr = (String)params.get("bookSetId");
        params.put("bookSetIdArr", this.String2Arr(bookSetIdArr));//paAcctSubjectTypeString

        // 获取交易方向
        String paPs = (String)params.get("paPs");
        params.put("paPsArr", this.String2Arr(paPs));
 
        String filterStr =  (String)params.get("filterBusiType");
        // 收益类型
        String profitType = (String)params.get("profitType");
        if(filterStr.contains("02") && filterStr.contains("20")) {//过滤业务类型 - 债券交易,交易所债券交易
        	list = mapper.selectBondList(params);
        }else if(filterStr.contains("03")){ //存借款交易
            list = mapper.queryPageList(params);
        }else if(filterStr.contains("01")){// 拆借交易      
            list =  mapper.selectLendList(params);
        }else if(filterStr.contains("06")||filterStr.contains("07")){//过滤业务类型 - 基金类资产
            list =  mapper.queryFundCurrencyList(params);
        }else if(filterStr.contains("15")){//过滤业务类型 - 活期存款收息交易
            list =  mapper.queryCurrentDepositList(params);
        }else if(filterStr.contains("19")){//过滤业务类型 - 交易账户收划款
            list =  mapper.queryTradeAccountList(params);
        }if(filterStr.contains("04") || filterStr.contains("05")){// 回购交易（债券质押式回购交易 和 债券买断式回购交易）   
            list =  mapper.selectRepurchaseList(params);
        }if(filterStr.contains("18")){ //现金类交易(费用交易)
            list =  mapper.queryCashTransactions(params);
        }if(filterStr.contains("16")){ //增值税交易查询
            list =  mapper.queryValueAddedTaxs(params);
        }else if(filterStr.contains("09") && "01".equals(profitType)){//计划类资产（profitType：01-净值型；02-收益型；03-货币型）
            list =  mapper.selectPlanNetvalueList(params);
        }else if(filterStr.contains("09") && "02".equals(profitType)){//计划类资产（profitType：01-净值型；02-收益型；03-货币型）
            list =  mapper.selectPlanProfitList(params);
        }else if(filterStr.contains("09") && "03".equals(profitType)){//计划类资产（profitType：01-净值型；02-收益型；03-货币型）
            list =  mapper.selectPlanCureencyList(params);
        }else if(filterStr.contains("30")){//资产重分类
            list =  mapper.selectReclassList(params);
        }else if(filterStr.contains("14")){//债券借贷交易查询
            list =  mapper.selectBondLendList(params);
        }
       
        return list;
    }

    /**
     * 获取符合条件的业务类型参数 (筛选条件的业务类型和符合页面查询的业务类型同时过滤)
     * 
     * @param selectBusiType 筛选条件中选择的业务类型
     * @param filterBusiType 当前页面需要查询的业务类型
     * @return arr
     * @see
     * @since 1.0
     */
    private String[] filterBusitype(String selectBusiType, String filterBusiType) {
        // 定义符合条件的业务类型参数
        Set<String> busiTypeSet = new HashSet<String>();

        // 将字符串转为数组
        String[] selectBusiTypeArr = this.String2Arr(selectBusiType); // 筛选条件中选择的业务类型 数组
        String[] filterBusiTypeArr = this.String2Arr(filterBusiType); // 当前页面需要查询的业务类型 数组

        // 当筛选条件中选择了业务类型的情况
        if (selectBusiTypeArr!=null && selectBusiTypeArr.length > 0) {

            // 当前页面需要查询的业务类型，必须包含在 筛选条件中选择的业务类型中
            for (String filterValue : filterBusiTypeArr) {
                // 符合页面查询条件的业务类型，同时又包含再筛选条件的中的业务类型，记录下来。
                for (String selectValue : selectBusiTypeArr) {
                    if (selectValue.equals(filterValue)) {
                        busiTypeSet.add(filterValue);
                    }
                }

            }

        } // 筛选条件中未选择业务类型的情况
        else {
            if(filterBusiTypeArr!=null){
                // 直接取当前页面需要查询的业务类型
                for (String value : filterBusiTypeArr) {
                    busiTypeSet.add(value);
                }
            }
        }

        // 定义返回数组
        String[] returnArr = null;

        // set转数组
        if (busiTypeSet != null && busiTypeSet.size() > 0) {
            returnArr = new String[busiTypeSet.size()];
            int index = 0;
            for (String va : busiTypeSet) {
                returnArr[index] = va;
                index++ ;
            }
        }

        return returnArr;
    }

    /**
     * 字符串转数组
     * 
     * @param valueString
     * @return
     * @see
     * @since 1.0
     */
    private String[] String2Arr(String valueString) {
        // 定义返回数组
        String[] returnArr = null;

        // 字符串转数组
        if (valueString != null && valueString.trim().length() > 0) {
            returnArr = valueString.trim().split(",");
        }

        return returnArr;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public InvestTradeDto queryFinProductDealBytradeId(Map<String, Object> map) {
        List<InvestTradeDto> list=mapper.queryPageListCommon(map);
        InvestTradeDto dto=new InvestTradeDto();
        if(list.size()>0){
            dto=list.get(0);
        }
        return dto;
    }
}

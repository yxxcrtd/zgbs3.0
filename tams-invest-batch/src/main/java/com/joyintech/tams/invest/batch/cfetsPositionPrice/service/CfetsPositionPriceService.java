package com.joyintech.tams.invest.batch.cfetsPositionPrice.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.cfetsPositionPrice.dao.CfetsPositionPriceMapper;
import com.joyintech.tams.invest.batch.cfetsPositionPrice.dto.CfetsUnitCostDto;
import org.activiti.engine.impl.util.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class CfetsPositionPriceService extends BaseService{
    private static final Logger logger = LoggerFactory.getLogger(CfetsPositionPriceService.class);
    @Autowired
    private CfetsPositionPriceMapper cfetsPositionPriceMapper;

    @Autowired
    private SqlSessionTemplate template;
    
    public void autoSettle(Date cdate, List<String> portfolioIdList) {
//        if (!StringUtil.equals(CacheUtil.getSysParam("auto_settle_flag"),"Y")){
//            return;
//        }

        /** 返回结果
         * KEY:组合+资产代码+投资目的
         * VALUE:持仓单位成本dto
         */
        Map<String, CfetsUnitCostDto> resultMap = new HashMap<>();
        List<BaseMap> lastCostAmt = selectLastCostAmt(cdate, portfolioIdList);

        if (lastCostAmt != null){
            lastCostAmt.forEach(e->{
                if(BigDecimal.ZERO.compareTo(e.getBigDecimal("SHARE_AMT")) != 0){
                    // 数量不为0时生成单位净价
                    CfetsUnitCostDto dto = new CfetsUnitCostDto();
                    dto.setPortfolioId(e.getString("PORTFOLIO_ID"));
                    dto.setFinprodId(e.getString("FINPROD_ID"));
                    dto.setBuyCostAmt(e.getBigDecimal("BUY_COST_AMT"));
                    dto.setShareAmt(e.getBigDecimal("SHARE_AMT"));
                    dto.setUnitCost(e.getBigDecimal("UNIT_COST"));
                    dto.setCdate(cdate);
                    resultMap.put(getKey(e), dto);
                }
            });
        }

        // 当日交易数据查询
        List<BaseMap> currTrdData = selectCurrTrdData(cdate, portfolioIdList);
        //交易所当日交易数据查询
        List<BaseMap> exchangeCurrTrdData = selectExchangeCurrTrdData(cdate, portfolioIdList);

        if(CollectionUtils.isNotEmpty(exchangeCurrTrdData)){
            currTrdData.addAll(exchangeCurrTrdData);
        }

        if (currTrdData != null){
            currTrdData.forEach(i -> {
                String key = getKey(i);
                CfetsUnitCostDto dto = resultMap.get(key);
                if (dto == null){
                    dto = new CfetsUnitCostDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                    resultMap.put(key, dto);
                }

                if (StringUtil.equals(i.getString("PS"), DictCodeList.交易方向_买入.getValue())){
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .add(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().add(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 4));
                }else if (StringUtil.equals(i.getString("PS"), DictCodeList.交易方向_申购.getValue())) {
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .add(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().add(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 4));
                }else if (StringUtil.equals(i.getString("PS"), DictCodeListExt1.交易方向_债转股.getValue())) {
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .add(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().add(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 4));
                }else{ // 卖出或行权回售
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .subtract(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().subtract(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 2));
                }
                dto.setPortfolioId(i.getString("PORTFOLIO_ID"));
                dto.setFinprodId(i.getString("FINPROD_ID"));
                dto.setCdate(cdate);
                
                resultMap.put(key, dto);
            });
        }
        
        // 入库 批量
        // cdate
        insertBatch(resultMap);
    }

    private void insertBatch(Map<String, CfetsUnitCostDto> map) {
        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        //批量插入mapper
        CfetsPositionPriceMapper cfetsPositionPriceMapper =  session.getMapper(CfetsPositionPriceMapper.class);
        try{
            int count = 0;
            Iterator<CfetsUnitCostDto> it = map.values().iterator();
            while (it.hasNext()){
                CfetsUnitCostDto next = it.next();
                cfetsPositionPriceMapper.delete(next); // could reInsert
                cfetsPositionPriceMapper.insert(next);
                count++;
                if(count % 1000==0){
                    session.commit();
                    session.clearCache();
                }
            }
            session.commit();
            session.clearCache();
        } finally {
            session.close();
        }
    }
    
    /**
     * 查询前一日持仓指标
     * @param cdate 交易日期
     * @param portfolioIdList 组合代码
     * @param secType1 债券一级分类
     */
    public List<BaseMap> selectLastCostAmt(Date cdate, List<String> portfolioIdList){
        BaseMap reqMap = BaseMap.newMap()
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())    // 债券
                .put("SVR_TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue())           // 银行间
                .put("SVR_TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue())
                .put("SVR_TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                .put("cdate", DateUtil.addDay(cdate, -1));

        if(portfolioIdList != null && !portfolioIdList.isEmpty()) reqMap.put("portfolioIdList", portfolioIdList);

        List<BaseMap> list = cfetsPositionPriceMapper.selectLastCostAmt(reqMap);
        /**
         * 返回结果 
         * PORTFOLIO_ID、FINPROD_ID、
         * BUY_COST_AMT 成本
         * SHARE_AMT    数量
         * UNIT_COST    单位成本
         */
        return list;
    }

    /**
     * 当日交易数据查询
     * @param cdate 交易日期
     * @param portfolioIdList 组合代码
     * @param secType1 债券一级分类
     */
    public List<BaseMap> selectCurrTrdData(Date cdate, List<String> portfolioIdList){
        BaseMap reqMap = BaseMap.newMap()
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())    // 债券
                .put("SVR_TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue())           // 银行间
                .put("PS_T01", DictCodeList.交易方向_买入.getValue())
                .put("PS_T02", DictCodeList.交易方向_卖出.getValue())
                .put("PS_T03", DictCodeList.交易方向_行权_回售.getValue())
                .put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue())
                .put("YN_N", DictCodeList.是否_否.getValue())
                .put("cdate", cdate);

        if(portfolioIdList != null && !portfolioIdList.isEmpty()) reqMap.put("portfolioIdList", portfolioIdList);

        List<BaseMap> list = cfetsPositionPriceMapper.selectCurrTrdData(reqMap);
        /**
         *  PORTFOLIO_ID、FINPROD_ID、INV_AIM、PS
         *  PRICE,  净价
         *  AMT     数量
         */
        return list;
    }

    /**
     * 当日交易数据查询
     * @param cdate 交易日期
     * @param portfolioIdList 组合代码
     * @param secType1 债券一级分类
     */
    public List<BaseMap> selectExchangeCurrTrdData(Date cdate, List<String> portfolioIdList) {
        BaseMap reqMap = BaseMap.newMap()
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())    // 债券
                .put("SVR_TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue())           // 银行间
                .put("PS_T01", DictCodeList.交易方向_买入.getValue())
                .put("PS_T02", DictCodeList.交易方向_卖出.getValue())
                .put("PS_T03", DictCodeList.交易方向_行权_回售.getValue())
                .put("PS_T04", DictCodeList.交易方向_申购.getValue())
                .put("PS_T49", DictCodeListExt1.交易方向_债转股.getValue())
                .put("SVR_TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue())
                .put("SVR_TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                .put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue())
                .put("YN_N", DictCodeList.是否_否.getValue())
                .put("cdate", cdate);

        if (portfolioIdList != null && !portfolioIdList.isEmpty()) reqMap.put("portfolioIdList", portfolioIdList);

        List<BaseMap> list = cfetsPositionPriceMapper.selectExchangeCurrTrdData(reqMap);
        /**
         *  PORTFOLIO_ID、FINPROD_ID、INV_AIM、PS
         *  PRICE,  净价
         *  AMT     数量
         */
        return list;
    }

    private static String getKey(BaseMap bm){
        return bm.getString("PORTFOLIO_ID") + bm.get("FINPROD_ID");
    }
}

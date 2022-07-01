/*
 * 文件名：TrdPlanRateInvestTradeService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.trade.trdplanrateinvesttrade.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.trdplanrateinvesttrade.dao.TrdPlanRateInvestTradeMapper;
import com.joyintech.service.trade.trdplanrateinvesttrade.dto.TrdPlanRateInvestTradeDto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.asset.dto.FinCashCalcRuleDto;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;

/**
 * 计划类（利率型）投资交易
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see TrdPlanRateInvestTradeService
 * @since 1.0
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class TrdPlanRateInvestTradeService extends BaseService<TrdPlanRateInvestTradeDto>{
    /**
     * 计划类资产投资交易Mapper
     */
    @Autowired
    private TrdPlanRateInvestTradeMapper trdPlanRateInvestTradeMapper;
    /**
     * 组合交易Mapper
     */
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    /**
     * 金融产品交易扩展Mapper
     */
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    
    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;
    
    @Autowired
    private FinProductMapper finProductMapper;
    
    /**
     * 分页查询
     * @param map 查询参数
     * @return 分页查询结果
     * @throws Exception 
     * @see
     * @since 1.0
     */
    public Page<TrdPlanRateInvestTradeDto> findPage(int page,int length,Map<String,Object> map) {
        map.put("ps_shengou",DictCodeList.交易方向_买入.getValue());
        map.put("ps_shuhui",DictCodeList.交易方向_卖出.getValue());
        map.put("PROFIT_TYPE", DictCodeList.收益类型_收益型.getValue());
        map.put("TRADE_STATUS_03", DictCodeListExt1.交易状态_撤销.getValue());
        map.put("PS_T75", DictCodeListExt3.交易方向_清理核销.getValue());
        return super.queryForPage(page, length, () -> trdPlanRateInvestTradeMapper.findTotalPage(map));
    }

    /**
    * 获取资产代码下拉框
    * @param map 请求参数
    * @return 下拉框集合类
    * @see
    * @since 1.0
    */
   public List<SelectOptionDto> getFinprodMarketId(Map<String,Object> map){
       map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
       map.put("PROFIT_TYPE", DictCodeList.收益类型_收益型.getValue());
       return trdPlanRateInvestTradeMapper.getFinprodMarketId(map);
   }
   /**
    * 获取金融产品类型
    * @param map 请求参数金融产品代码
    * @return 金融产品类型
    * @see
    * @since 1.0
    */
   public Map<String,Object> getFinprodType(Map<String,Object> map){
       Map<String,Object> finTypeMap = new HashMap<>();
       String investRate = "";
       String ccy = "";
       String repayIntFlag="Y";//默认是还本带息的
   /*原因、zhuyongkang 2019/7/30  start */
       FinProductType finprodType=new FinProductType();
       if(map.get("finprodId") != null){
           finprodType = trdPlanRateInvestTradeMapper.getFinprodType(map);
           investRate = trdPlanRateInvestTradeMapper.getInvestRate(map);
           ccy = trdPlanRateInvestTradeMapper.getCcy(map);
           map.put("cashType",DictCodeList.现金流类型_到期还本.getValue());
           repayIntFlag=trdPlanRateInvestTradeMapper.getIsPayInt(map);
       }
       
       finTypeMap.put("finprodType", finprodType.getType6());
       finTypeMap.put("investRate", investRate);
       finTypeMap.put("ccy", ccy);
       finTypeMap.put("type7",finprodType.getType7());
       finTypeMap.put("type9",finprodType.getType9());
       finTypeMap.put("type10",finprodType.getType10());
       finTypeMap.put("type11",finprodType.getType11());
       if(repayIntFlag.equals(DictCodeList.是否_否.getValue())) {
           finTypeMap.put("repayIntFlag", repayIntFlag);//是否还本不带息
       }
      /* 原因、zhuyongkang 2019/7/30  end*/
       return finTypeMap;
   }
   
   /**
    * 获取登记编号
    * @param map 查询参数
    * @return 登记编号
    * @see
    * @since 1.0
    */
   public String getProdRegistCode(Map<String,Object> map){
       return trdPlanRateInvestTradeMapper.getProdRegistCode(map);
   }
   /**
    * 获取收盘价
    * @param map 日期、币种
    * @return 汇率（收盘价）
    * @see
    * @since 1.0
    */
   public BigDecimal getClosePrice( Map<String,Object> map){
       return trdPlanRateInvestTradeMapper.getClosePrice(map);
   }
   /**
    * 查询交易员
    * @param map 委托代码，接受状态
    * @return 交易员
    * @see
    * @since 1.0
    */
   public String getTrader(Map<String,Object> map){
       return trdPlanRateInvestTradeMapper.getTrader(map);
   }
   /**
    * 获取计息基础
    * @param finprodId 金融产品代码
    * @return 计息基础
    * @see
    * @since 1.0
    */
   public String getBasis(String finprodId){
       return trdPlanRateInvestTradeMapper.getBasis(finprodId);
   }
   /**
    * 新建交易
    * @param dto 存款类投资交易实体类
    * @return 是否成功
    * @see
    * @since 1.0
    */
   public int insert(TrdPlanRateInvestTradeDto dto){
       if (CollectionUtils.isNotEmpty(dto.getPtlTrade())) {
           dto.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insert(ptlTrade));
       }
       paTrdProductDealMapper.insert(dto.getPaTrdProductDeal());
       return 1;
   }
   
   /**
    * 撤销组合交易表和金融产品扩展表数据
    * @param dto 存款类投资交易实体类
    * @return 是否成功
    * @see
    * @since 1.0
    */
   public int undo(TrdPlanRateInvestTradeDto dto){
       // 插入方法已被注释，其他的东西留着没用
//       PaTrdProductDeal paTrdProductDeal = paTrdProductDealMapper.selectByPrimaryKey(dto.getTradeId());
//       paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId()+"R");
//       paTrdProductDealMapper.insertSelective(paTrdProductDeal);
//       PtlTradeKey ptlTradeKey = new PtlTradeKey();
//       ptlTradeKey.setBusiNo(dto.getTradeId());
//       ptlTradeKey.setPortfolioId(dto.getPortfolioId());
//       PtlTrade ptlTrade = ptlTradeMapper.selectByPrimaryKey(ptlTradeKey);
       return 1;
   }
   /**
    * 获取上次付息日
    * @param map 请求参数
    * @return 上次付息日
    * @see
    * @since 1.0
    */
   public FinCashPlan getLastVDate(Map<String,Object> map){
       map.put("CASH_TYPE_01", DictCodeList.现金流类型_中间收息.getValue());
       map.put("CASH_TYPE_04", DictCodeList.现金流类型_到期收息.getValue());
       return trdPlanRateInvestTradeMapper.getLastVDate(map);
   }
   /**
    * 获取计算规则
    * @param map 请求参数
    * @return 计算规则集合
    * @see
    * @since 1.0
    */
   public List<FinCashCalcRuleDto> getFinCashCalcRule(Map<String,Object> map){
       return trdPlanRateInvestTradeMapper.getFinCashCalcRule(map);
   }
   
   /**
    * 获取已完成委托额度
    * @param entrustId 请求参数
    * @return 已完成委托额度
    * @see
    * @since 1.0
    */
   public BigDecimal queryActiveMandate(String entrustId) {
	   BigDecimal isFinish = BigDecimal.ZERO;
	   if (baseEtrInvestInfoService == null) {
           return BigDecimal.ZERO;
       }
	   BaseEtrInvestInfoDto resultDto = baseEtrInvestInfoService.queryActiveMandate(entrustId);
       if (resultDto != null && resultDto.getEtrEntrust() != null) {
           // 委托数量中扣除已完成额度
           if (!StringUtil.isEmptyList(resultDto.getEtrFinishStatusList())) {
               for (EtrFinishStatus etrFinishStatus : resultDto.getEtrFinishStatusList()) {
            	   isFinish = ArithmeticUtil.doAdd(isFinish,etrFinishStatus.getFinishAmt());
               }
           }
       }
	   return isFinish;
   }
   
   /**
    * 查询金融产品信息
    * @param finprodId
    * @return
    */
   public FinProduct getFinProductInfo(String finprodId){
       return finProductMapper.selectByPrimaryKey(finprodId);
   }

    public FinCashPayRule getFinCashPayRule(Map<String, Object> map) {
        return trdPlanRateInvestTradeMapper.getFinCashPayRule(map);
    }
}

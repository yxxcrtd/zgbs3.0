package com.joyintech.tams.settle.batch.datadeal.service;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.settle.batch.datadeal.dao.StlDownDataDealMapper;
import com.joyintech.tams.settle.batch.datagather.dto.BondTradeDealParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据清算批处理Service
 * 
 * @author 陈卓
 * @date 2020/7/7
 * @see StlDownDataDealService
 * @since 1.0
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StlDownDataDealService {

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;

    @Autowired(required = false)
    private JoyinRestTemplate restTemplate;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    @Value("${application.micorservice.contextPath.tamsServer:http://tams-server}")
    private String tamsSerever;
	
    @Autowired
    protected StlDownDataDealMapper stlDownDataDealMapper;
	
	@Autowired
    protected StlDataDealProcessCommonService stlDataDealProcessCommonService;
    
    // 查询所有参数
    public List<SettleBatchPara> selectAllParam(Date dealDate) {
        return stlDownDataDealMapper.selectAllParam(dealDate);
    }
    
    /**
     * 计算债券到期收益率和行权收益率
     * @param productDeal
     */
    public void calc(SettleTransData settleTransData) {
        // 设置计算参数
        BondTradeDealParam dealParam = new BondTradeDealParam();
        dealParam.setFinprodId(settleTransData.getFinprodId());
        dealParam.setTradeAmt(settleTransData.getTradeAmt());
        dealParam.setSettleDate(settleTransData.getSettleDate());
        dealParam.setVdate(settleTransData.getSettleDate());
        dealParam.setTradeDate(settleTransData.getTradeDate());
        dealParam.setTradeMarket(settleTransData.getTradeMarket());
        dealParam.setTradeMarketMode(settleTransData.getTradeMarketMode());
        dealParam.setPs(settleTransData.getPs());
        // dealParam.setUnitInt(settleTransData.getUnitInt());
        dealParam.setUnitFprice(settleTransData.getUnitFprice());

        // 设置计算类型为全价总额（根据全价总额计算到期收益率和行权收益率）
        dealParam.setCalculateField("fpriceAmt");
        dealParam.setFpriceAmt(settleTransData.getFpriceAmt());
        // dealParam.setIntAmt(settleTransData.getIntAmt());
        dealParam.setInvAim(DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        String profitType = stlDataDealProcessCommonService.getPortfolioProfitType(settleTransData.getPortfolioId());
        if (!StringUtil.equals(profitType, DictCodeList.收益类型_净值型.getValue())) {
        	dealParam.setInvAim(DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        }
        
        // 调用债券交易计算逻辑
        Map ajaxResult = new HashMap();
        if (isSingle) {
            ajaxResult = simpleRestTemplate.postForObject(tamsSerever + "/invest/trade/bond/TrdBondInvestTrade002/calculateData", dealParam, Map.class);
        } else {
            ajaxResult = restTemplate.postForObject(tamsSerever + "/invest/trade/bond/TrdBondInvestTrade002/calculateData", dealParam, Map.class);
        }
        if (ajaxResult != null && ajaxResult.containsKey("data")) {
            Object result = ajaxResult.get("data");
            if(result instanceof Map) {
                Map<String, Object> calcResult = (Map)result;
                Object unitInt = calcResult.get("unitInt");
                if(StringUtil.isNotEmpty(unitInt)) {
                    BigDecimal unitIntNum = BigDecimal.valueOf(Double.parseDouble(unitInt.toString()));
                    settleTransData.setUnitInt(unitIntNum);
                }
                settleTransData.setUnitCprice(ArithmeticUtil.subtract(settleTransData.getUnitFprice(), settleTransData.getUnitInt()));
                settleTransData.setCpriceAmt(ArithmeticUtil.multiply(settleTransData.getUnitCprice(), settleTransData.getShareAmt()).setScale(2, RoundingMode.HALF_UP));
                settleTransData.setIntAmt(ArithmeticUtil.subtract(settleTransData.getFpriceAmt(), settleTransData.getCpriceAmt()).setScale(2, RoundingMode.HALF_UP));
            }
        }
    }
}

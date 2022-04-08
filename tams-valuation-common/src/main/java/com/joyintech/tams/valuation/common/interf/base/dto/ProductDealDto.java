/*
 * 文件名：ProductDealDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年7月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.dto;

import com.joyintech.framework.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 交易标的
 * 
 * @author HuangJian
 * @version 1.0 2018年7月11日
 * @see ProductDealDto
 * @since 1.0
 */
public class ProductDealDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 交易日
     */
    private Date tradeDate;
    
    /**
     * 交割日
     */
    private Date effDate;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易金额
     */
    private BigDecimal tardeAmt;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型
     */
    private String finprodType;

    /**
     * 业务类型
     */
    private String busiType;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 交易对手类型
     */
    private String counterType;

    /**
     * 理财产品代码
     */
    private String prodId;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 是否随交易出账
     */
    private String isTrade;
    
    /**
     * 账套代码
     */
    private String bookNo;
    /**
     * 交易状态
     */
    private String tradeStatus;
    
    /**add by wujie on 20181017 start remark:增加账套信息**/
    private List<ValBooksetInfoDto> valBooksetInfoDtoList;

    /**add by wujie on 20181017 end remark:增加账套信息**/
    
    /**
     * add by chendijun 2018/11/14
     * 原交易编号
     */
    private String pTradeId;
    
    /**add by chendijun 20181120 start**/
    /**
     * 交易场所
     */
    private String tradeMarket;
    /**
     * 交易市场,一级市场、二级市场
     */
    private String tradeMarketMode;
    /**add by chendijun 20181120 start**/
    
    /**
     * 投资目的
     */
    private String invAim;
    
    /**
     * 通道虚拟资产
     */
    private String channelAsset;
    
    /**
     * 映射类型
     */
    private String mappingType;
    
    /**
     * 全价总额
     */
    private BigDecimal fpriceAmt;

    /**
     * 券商资金账户
     */
    private String acctNo;
    
    /**
     * 参数条件
     */
    private Map<String,String> paramMap;
    
    /**
     * 取得 交易编号
     * 
     * @return 交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 交易编号
     * 
     * @param tradeId 交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    /**
     * 取得 交易日
     * 
     * @return 交易日
     */
    public Date getTradeDate() {
        return tradeDate;
    }

    /**
     * 设置 交易日
     * 
     * @param tradeDate 交易日
     */
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 取得 交易方向
     * 
     * @return 交易方向
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 交易方向
     * 
     * @param ps 交易方向
     */
    public void setPs(String ps) {
        this.ps = ps;
    }

    /**
     * 取得 交易金额
     * 
     * @return 交易金额
     */
    public BigDecimal getTardeAmt() {
        return tardeAmt;
    }

    /**
     * 设置 交易金额
     * 
     * @param tardeAmt 交易金额
     */
    public void setTardeAmt(BigDecimal tardeAmt) {
        this.tardeAmt = tardeAmt;
    }

    /**
     * 取得 金融产品代码
     * 
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * 
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 取得 金融产品类型
     * 
     * @return 金融产品类型
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型
     * 
     * @param finprodType 金融产品类型
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    /**
     * 取得 业务类型
     * 
     * @return 业务类型
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 业务类型
     * 
     * @param busiType 业务类型
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    /**
     * 取得 交易对手
     * 
     * @return 交易对手
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 交易对手
     * 
     * @param counterId 交易对手
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    /**
     * 取得 交易对手类型
     * 
     * @return 交易对手类型
     */
    public String getCounterType() {
        return counterType;
    }

    /**
     * 设置 交易对手类型
     * 
     * @param counterType 交易对手类型
     */
    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

    /**
     * 取得 理财产品代码
     * 
     * @return 理财产品代码
     */
    public String getProdId() {
        return prodId;
    }

    /**
     * 设置 理财产品代码
     * 
     * @param prodId 理财产品代码
     */
    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    /**
     * 取得 币种
     * 
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     * 
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 取得 是否随交易出账
     * 
     * @return 是否随交易出账
     */
    public String getIsTrade() {
        return isTrade;
    }

    /**
     * 设置 是否随交易出账
     * 
     * @param isTrade 是否随交易出账
     */
    public void setIsTrade(String isTrade) {
        this.isTrade = isTrade;
    }

    
    /**
     * 获取 effDate
     * @return effDate.
     */
    public Date getEffDate() {
        return effDate;
    }

    
    /**
     * 设置 effDate
     * @param effDate effDate
     */
    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    
    /**
     * 获取 bookNo
     * @return bookNo.
     */
    public String getBookNo() {
        return bookNo;
    }

    
    /**
     * 设置 bookNo
     * @param bookNo bookNo
     */
    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    /**
     * 取得 有交易状态
     * @return 交易状态
     */
	public String getTradeStatus() {
		return tradeStatus;
	}
	
	/**
     * 设置 交易状态
     * @param isClean 交易状态
     */
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	/**
	 * @return the valBooksetInfoDtoList
	 */
	public List<ValBooksetInfoDto> getValBooksetInfoDtoList() {
		return valBooksetInfoDtoList;
	}

	/**
	 * @param valBooksetInfoDtoList the valBooksetInfoDtoList to set
	 */
	public void setValBooksetInfoDtoList(List<ValBooksetInfoDto> valBooksetInfoDtoList) {
		this.valBooksetInfoDtoList = valBooksetInfoDtoList;
	}

    /**
     * 获取 pTradeId
     * @return pTradeId.
     */
    public String getpTradeId() {
        return pTradeId;
    }

    /**
     * 设置 pTradeId
     * @param pTradeId pTradeId
     */
    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId;
    }

    /**
     * 获取 tradeMarketMode
     * @return tradeMarketMode.
     */
    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    /**
     * 设置 tradeMarketMode
     * @param tradeMarketMode tradeMarketMode
     */
    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode;
    }

    /**
     * 获取 tradeMarket
     * @return tradeMarket.
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 tradeMarket
     * @param tradeMarket tradeMarket
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

	/**
	 * @return the invAim
	 */
	public String getInvAim() {
		return invAim;
	}

	/**
	 * @param invAim the invAim to set
	 */
	public void setInvAim(String invAim) {
		this.invAim = invAim;
	}

	public String getChannelAsset() {
		return channelAsset;
	}

	public void setChannelAsset(String channelAsset) {
		this.channelAsset = channelAsset;
	}
	

    /**
    * 获取 mappingType 映射类型
    * @return mappingType.
    */
    public String getMappingType() {
        return mappingType;
    }

    /**
     * 设置 mappingType 映射类型
     * @param mappingType
     */
    public void setMappingType(String mappingType) {
        this.mappingType = mappingType;
    }

    /**
     * 设置 fpriceAmt 全价总额
     * @param fpriceAmt
     */
    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    /**
     * 获取 fpriceAmt 全价总额
     * @return fpriceAmt.
     */
    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    /**
     * 获取 paramMap 参数条件
     * @return paramMap.
     */
    public Map<String, String> getParamMap() {
        return paramMap;
    }

    /**
     * 获取 paramMap 参数条件
     * @return paramMap.
     */
    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }
    
    

}

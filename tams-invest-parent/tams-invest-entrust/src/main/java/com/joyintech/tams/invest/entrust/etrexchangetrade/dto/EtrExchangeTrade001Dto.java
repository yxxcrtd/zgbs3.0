/*
 * 文件名：EtrExchangeTradeDto001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrexchangetrade.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etrbrowdepoinfo.dto.EtrBrowDepoInfoDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.validator.EtrExchangeTrade001Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 *  交易信息 （左边下）
 * @author chendijun
 * @version 1.0 2020年7月2日
 * @see EtrExchangeTrade001Dto
 * @since 1.0
 */
@PageId("EtrExchangeTradeMain")
@ValidLogic(targetClass = EtrExchangeTrade001Validator.class , groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class EtrExchangeTrade001Dto extends EtrEntrust implements ProcessEntity{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 操作类型
     */
    private String operateType;
    
    /**
     * 页面ID
     */
    private String pageId;
    
    /**
     * 按钮id
     */
    private String elementId;
    
    /**
     * 资产名称
     */
    private String finprodName;
    
    /**
     * 申报类型
     */
    private String declaType;
    
    /**
     * 全价价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fprice;
    
    /**
     * 收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield;
    
    /**
     * 可转股数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal toShareAvailableNum; 
    
    /**
     * 指令价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustPrice; 
    
    /**
     * 席位号
     */
    private String seatId;
    
    /**
     * 约定号
     */
    private String agreeOnId;
    
    /**
     * 对方交易商
     */
    private String counterDealer;
    
    
    /**
     * 本方交易员
     */
    private String traders;

    /**
     * 回购期限
     */
    private String term;
    
    /**
     * 证券账户
     */
    private String secAcctId;
    
    
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;
    
    /**
     * 组合资金账户
     */
    private String acctNo;
    
   /**
    * 
    */
    private String pageModel;
    
    /**
     * 指令业务类型
     */
    private String entrustBussType;
    
    /**
     * 计算方式
     */
    private String calculateField;
    
    /**
     * 记录操作类型
     */
    private String optType;
    
    /**
     * 行号
     */
    private String lineNo;
    
    /**
     * 涨停
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal limitUp;
    
    /**
     * 跌停
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal limitDown;
    
    /**
     * 可用金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal accAvailableCash;
    
    /**
     * 可用持仓
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal accAvailablePos;

    /**
     * 最新价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal latestPrice;

    /**
     * 业务类型名称
     */
    private String businessTypeName;
    
    /**
     * 目标类型
     */
    private String targetType;
    
    /**
     * 数据来源
     */
    private String dataSource;
    
    /**
     * 对手方交易商
     */
    private String counterOrg;
    
    /**
     * 本方交易员
     */
    private String trader;
    
    
	/**
	 * 序号
	 */
	private String seqNo;
	
	/**
     * 选中的数据
     */
    private transient List<EtrExchangeTrade001Dto> selectedItem;
    
    /**
     * 股票属性
     */
    private String board;
    
    /**
     * 清算速度
     */
    private String settleSpeed;

    /**
     * 是否多组合系列户的指令
     */
    private String isMulti;


    /**
     * 取得 委托代码
     * @return 委托代码
     */
    @Override
    @PrimaryKey(value = Field.PK_1)
    public String getEntrustId() {
        return super.getEntrustId();
    }
    /**
     * 获取 fprice
     * @return fprice.
     */
    public BigDecimal getFprice() {
        return fprice;
    }

    
    /**
     * 设置 fprice
     * @param fprice fprice
     */
    public void setFprice(BigDecimal fprice) {
        this.fprice = fprice;
    }

    /**
     * 获取 finprodName
     * @return finprodName.
     */
    public String getFinprodName() {
        return finprodName;
    }

    /**
     * 设置 finprodName
     * @param finprodName finprodName
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }


    
    /**
     * 获取 yield
     * @return yield.
     */
    public BigDecimal getYield() {
        return yield;
    }


    
    /**
     * 设置 yield
     * @param yield yield
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    /**
     * 获取 yield
     * @return yield.
     */
    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }


    
    /**
     * 设置 yield
     * @param entrustPrice
     */
    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    /**
     * 获取 operateType
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }


    /**
     * 设置 operateType
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }


    /**
     * 获取 elementId
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }


    /**
     * 设置 elementId
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 获取 seatId
     * @return seatId.
     */
    public String getSeatId() {
        return seatId;
    }


    /**
     * 设置 seatId
     * @param seatId seatId
     */
    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }


    /**
     * 获取 agreeOnId
     * @return agreeOnId.
     */
    public String getAgreeOnId() {
        return agreeOnId;
    }


    /**
     * 设置 agreeOnId
     * @param agreeOnId agreeOnId
     */
    public void setAgreeOnId(String agreeOnId) {
        this.agreeOnId = agreeOnId;
    }


    /**
     * 获取 counterDealer
     * @return counterDealer.
     */
    public String getCounterDealer() {
        return counterDealer;
    }


    /**
     * 设置 counterDealer
     * @param counterDealer counterDealer
     */
    public void setCounterDealer(String counterDealer) {
        this.counterDealer = counterDealer;
    }


    /**
     * 获取 traders
     * @return traders.
     */
    public String getTraders() {
        return traders;
    }


    /**
     * 设置 traders
     * @param traders traders
     */
    public void setTraders(String traders) {
        this.traders = traders;
    }


    /**
     * 获取 term
     * @return term.
     */
    public String getTerm() {
        return term;
    }


    /**
     * 设置 term
     * @param term term
     */
    public void setTerm(String term) {
        this.term = term;
    }


	public String getSecAcctId() {
		return secAcctId;
	}


	public void setSecAcctId(String secAcctId) {
		this.secAcctId = secAcctId;
	}
    /**
     * 获取 riskTrialRetDto
     * @return riskTrialRetDto.
     */
    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }
    /**
     * 设置 riskTrialRetDto
     * @param riskTrialRetDto riskTrialRetDto
     */
    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }
	public String getAcctNo() {
		return acctNo;
	}
	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}
	public String getPageModel() {
		return pageModel;
	}
	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}
	public String getEntrustBussType() {
		return entrustBussType;
	}
	public void setEntrustBussType(String entrustBussType) {
		this.entrustBussType = entrustBussType;
	}
	public String getCalculateField() {
		return calculateField;
	}
	public void setCalculateField(String calculateField) {
		this.calculateField = calculateField;
	}
	public BigDecimal getLimitUp() {
		return limitUp;
	}
	public void setLimitUp(BigDecimal limitUp) {
		this.limitUp = limitUp;
	}
	public BigDecimal getLimitDown() {
		return limitDown;
	}
	public void setLimitDown(BigDecimal limitDown) {
		this.limitDown = limitDown;
	}
	public BigDecimal getAccAvailableCash() {
		return accAvailableCash;
	}
	public void setAccAvailableCash(BigDecimal accAvailableCash) {
		this.accAvailableCash = accAvailableCash;
	}
	public BigDecimal getAccAvailablePos() {
		return accAvailablePos;
	}
	public void setAccAvailablePos(BigDecimal accAvailablePos) {
		this.accAvailablePos = accAvailablePos;
	}

    public BigDecimal getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(BigDecimal latestPrice) {
        this.latestPrice = latestPrice;
    }

    public String getBusinessTypeName() {
		return businessTypeName;
	}
	public void setBusinessTypeName(String businessTypeName) {
		this.businessTypeName = businessTypeName;
	}
	public String getOptType() {
		return optType;
	}
	public void setOptType(String optType) {
		this.optType = optType;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
    /**
     * 获取 dataSource
     * 
     * @return dataSource
     */
    public String getDataSource() {
        return dataSource;
    }
    /**
     * 设置 dataSource
     * 
     * @param dataSource String
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    /**
     * 获取 counterOrg
     * 
     * @return counterOrg
     */
    public String getCounterOrg() {
        return counterOrg;
    }
    /**
     * 设置 counterOrg
     * 
     * @param counterOrg String
     */
    public void setCounterOrg(String counterOrg) {
        this.counterOrg = counterOrg;
    }
    /**
     * 获取 trader
     * 
     * @return trader
     */
    public String getTrader() {
        return trader;
    }
    /**
     * 设置 trader
     * 
     * @param trader String
     */
    public void setTrader(String trader) {
        this.trader = trader;
    }
    
    /**
     * declaType
     * @return
     */
	public String getDeclaType() {
		return declaType;
	}
	
	/**
	 * 
	 * @param declaType
	 */
	public void setDeclaType(String declaType) {
		this.declaType = declaType;
	}
    /**
     * 获取 toShareAvailableNum
     * 
     * @return toShareAvailableNum
     */
    public BigDecimal getToShareAvailableNum() {
        return toShareAvailableNum;
    }
    /**
     * 设置 toShareAvailableNum
     * 
     * @param toShareAvailableNum BigDecimal
     */
    public void setToShareAvailableNum(BigDecimal toShareAvailableNum) {
        this.toShareAvailableNum = toShareAvailableNum;
    }
    
    /**
     * 获取 getPsStr
     * @return getPsStr.
     */
    public String getPsStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
    }

    public String getTradeMarketString(){
       return  SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }
    //add by chenjiaxing 投资目的值转化 start
    public String getInvAimStr(){
        return  SysDataUtil.getNameFromDataDict(DictCode.投资目的, super.getInvAim());
    }

    public String getBusinessModelStr(){
        return  SysDataUtil.getNameFromDataDict(DictCode.业务模式, super.getBusinessModel());
    }
    //add by chenjiaxing 投资目的值转化 end

    public String getPriceModelStr(){
        return  SysDataUtil.getNameFromDataDict(DictCode.价格模式, super.getPriceModel());
    }
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getPageId() {
		return pageId;
	}
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	public List<EtrExchangeTrade001Dto> getSelectedItem() {
		return selectedItem;
	}
	public void setSelectedItem(List<EtrExchangeTrade001Dto> selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	
    /**
     * 获取 fromEtrExchange  判断是否来自场内交易指令
     * @return fromEtrExchange.
     */
    public String getFromEtrExchange() {
        return DictCodeList.是否_是.getValue();
    }
	public String getLineNo() {
		return lineNo;
	}
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}
	public String getBoard() {
		return board;
	}
	public void setBoard(String board) {
		this.board = board;
	}
	public String getSettleSpeed() {
		return settleSpeed;
	}
	public void setSettleSpeed(String settleSpeed) {
		this.settleSpeed = settleSpeed;
	}

    public String getTargetTypeStr() {
        return  SysDataUtil.getNameFromDataDict(DictCode.目标类型, this.getTargetType());
    }

    public String getIsMulti() {
        return isMulti;
    }

    public void setIsMulti(String isMulti) {
        this.isMulti = isMulti;
    }
}

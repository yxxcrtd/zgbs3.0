/*
 * 文件名：LendStandardCommonResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年7月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 利率互换-基础数据接口模板处理返回共通结果
 * @author 殷亚
 * @date 2022/4/15 10:13
 * @return
 * @see
 * @version 1.0
 */
public class IrsCommonResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 交易类金融产品
     */
    private List<FinTradeProduct> listValTradeFinProduct = new ArrayList<FinTradeProduct>();

    /**
     * 金融产品分类表
     */
    private List<FinProductType> listValFinProductType = new ArrayList<FinProductType>();
    
	/**
     * 现金流信息
     */
    List<FinCashInfo> listValCashInfo = new ArrayList<FinCashInfo>();

    /**
     * 现金流计算规则
     */
    List<FinCashCalcRule> listValCashCalcRule = new ArrayList<FinCashCalcRule>();
    
    /**
     * 现金流支付规则
     */
    List<FinCashPayRule> listValCashPayRule = new ArrayList<FinCashPayRule>();

	/**
	 * 现金流计划
	 */
	List<FinCashPlan> listValCashPlan=new ArrayList<FinCashPlan>();
    
    /**
     * 日志
     */
    private List<ValInterflog> valInterflog = new ArrayList<ValInterflog>();


	/**
	 * 现金流利率信息表
	 */
	private List<FinRateInfo> listValCashIntInfo = new ArrayList<FinRateInfo>();

	/**
	 * 用于存放去重数据筛选条件
	 */
	private Map<String,Object> paramMap;



	public List<FinTradeProduct> getListValTradeFinProduct() {
		return listValTradeFinProduct;
	}

	public void setListValTradeFinProduct(List<FinTradeProduct> listValTradeFinProduct) {
		this.listValTradeFinProduct = listValTradeFinProduct;
	}

	public List<FinProductType> getListValFinProductType() {
		return listValFinProductType;
	}

	public void setListValFinProductType(List<FinProductType> listValFinProductType) {
		this.listValFinProductType = listValFinProductType;
	}

	public List<FinCashInfo> getListValCashInfo() {
		return listValCashInfo;
	}

	public void setListValCashInfo(List<FinCashInfo> listValCashInfo) {
		this.listValCashInfo = listValCashInfo;
	}

	public List<FinCashCalcRule> getListValCashCalcRule() {
		return listValCashCalcRule;
	}

	public void setListValCashCalcRule(List<FinCashCalcRule> listValCashCalcRule) {
		this.listValCashCalcRule = listValCashCalcRule;
	}

	public List<FinCashPayRule> getListValCashPayRule() {
		return listValCashPayRule;
	}

	public void setListValCashPayRule(List<FinCashPayRule> listValCashPayRule) {
		this.listValCashPayRule = listValCashPayRule;
	}

	public List<ValInterflog> getValInterflog() {
		return valInterflog;
	}

	public void setValInterflog(List<ValInterflog> valInterflog) {
		this.valInterflog = valInterflog;
	}

	public List<FinRateInfo> getListValCashIntInfo() {
		return listValCashIntInfo;
	}

	public void setListValCashIntInfo(List<FinRateInfo> listValCashIntInfo) {
		this.listValCashIntInfo = listValCashIntInfo;
	}

	public List<FinCashPlan> getListValCashPlan() {
		return listValCashPlan;
	}

	public void setListValCashPlan(List<FinCashPlan> listValCashPlan) {
		this.listValCashPlan = listValCashPlan;
	}



	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
}

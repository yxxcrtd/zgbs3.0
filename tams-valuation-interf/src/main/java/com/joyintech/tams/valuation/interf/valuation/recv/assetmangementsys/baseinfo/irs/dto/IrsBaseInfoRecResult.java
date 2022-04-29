/*
 * 文件名：IrsBaseInfoRecResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.irs.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.dto.IrsCommonResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 利率互换标的信息实体
 * @author 殷亚
 * @date 2022/4/21 10:42
 * @see
 * @version 1.0
 */
public class IrsBaseInfoRecResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * @param result
     */
    public IrsBaseInfoRecResult(IrsCommonResult result) {
        this.listValFinProductType = result.getListValFinProductType();
        this.listValTradeFinProduct = result.getListValTradeFinProduct();
        this.valInterflog = result.getValInterflog();
        this.paramMap = result.getParamMap();
        this.setListValCashInfo(result.getListValCashInfo());
        this.setListValCashCalcRule(result.getListValCashCalcRule());
        this.setListValCashPayRule(result.getListValCashPayRule());
        this.setListValCashPlan(result.getListValCashPlan());
        this.setListValCashIntInfo(result.getListValCashIntInfo());

    }
    /**
     * 标的类金融产品
     */
    private List<FinProduct> listValFinProduct = new ArrayList<FinProduct>();

    /**新增 标的类金融产品表 add by chendijun on 20190429 end**/

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
     * 现金流计划表
     */
    List<FinCashPlan> listValCashPlan = new ArrayList<FinCashPlan>();

    /**
     * 现金流利率信息表
     */
    private List<FinRateInfo> listValCashIntInfo = new ArrayList<FinRateInfo>();

    /**
     * 金融产品扩展表
     */
    List<FinProductAdd> listValFinProductExt = new ArrayList<>();


    /**
     * 日志
     */
    private List<ValInterflog> valInterflog = new ArrayList<ValInterflog>();

    /**
     * 获取 listValTradeFinProduct
     * 
     * @return listValTradeFinProduct.
     */
    public List<FinTradeProduct> getListValTradeFinProduct() {
        return listValTradeFinProduct;
    }

    /**
     * 设置 listValTradeFinProduct
     * 
     * @param listValTradeFinProduct listValTradeFinProduct
     */
    public void setListValTradeFinProduct(List<FinTradeProduct> listValTradeFinProduct) {
        this.listValTradeFinProduct = listValTradeFinProduct;
    }

    /**
     * 获取 listValFinProductType
     * 
     * @return listValFinProductType.
     */
    public List<FinProductType> getListValFinProductType() {
        return listValFinProductType;
    }

    /**
     * 设置 listValFinProductType
     * 
     * @param listValFinProductType listValFinProductType
     */
    public void setListValFinProductType(List<FinProductType> listValFinProductType) {
        this.listValFinProductType = listValFinProductType;
    }

    /**
     * 获取 valInterflog
     * 
     * @return valInterflog.
     */
    public List<ValInterflog> getValInterflog() {
        return valInterflog;
    }

    /**
     * 设置 valInterflog
     * 
     * @param valInterflog valInterflog
     */
    public void setValInterflog(List<ValInterflog> valInterflog) {
        this.valInterflog = valInterflog;
    }

    private Map<String,Object> paramMap;

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
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

	/**
	 * 获取 listValCashPayRule
	 * @return listValCashPayRule.
	 */
	public List<FinCashPayRule> getListValCashPayRule() {
		return listValCashPayRule;
	}

	/**
	 * 设置 listValCashPayRule
	 * @param listValCashPayRule listValCashPayRule
	 */
	public void setListValCashPayRule(List<FinCashPayRule> listValCashPayRule) {
		this.listValCashPayRule = listValCashPayRule;
	}

	/**
	 * 获取 listValCashPlan
	 * @return listValCashPlan.
	 */
	public List<FinCashPlan> getListValCashPlan() {
		return listValCashPlan;
	}

	/**
	 * 设置 listValCashPlan
	 * @param listValCashPlan listValCashPlan
	 */
	public void setListValCashPlan(List<FinCashPlan> listValCashPlan) {
		this.listValCashPlan = listValCashPlan;
	}

    /**
     * 获取 listValFinProduct
     * @return listValFinProduct.
     */
    public List<FinProduct> getListValFinProduct() {
        return listValFinProduct;
    }

    /**
     * 设置 listValFinProduct
     * @param listValFinProduct listValFinProduct
     */
    public void setListValFinProduct(List<FinProduct> listValFinProduct) {
        this.listValFinProduct = listValFinProduct;
    }

    /**add by wujie on 20180823 end**/

    /** 标的类金融产品扩展表 add by sunshuaishuai on 20210322 start**/
    public List<FinProductAdd> getListValFinProductExt() {
        return listValFinProductExt;
    }

    public void setListValFinProductExt(List<FinProductAdd> listValFinProductExt) {
        this.listValFinProductExt = listValFinProductExt;
    }
    /** 标的类金融产品扩展表 add by sunshuaishuai on 20210320 end**/

    /** 现金流利率信息表 add by sunshuaishuai on 20210322 start**/
    public List<FinRateInfo> getListValCashIntInfo() {
        return listValCashIntInfo;
    }

    public void setListValCashIntInfo(List<FinRateInfo> listValCashIntInfo) {
        this.listValCashIntInfo = listValCashIntInfo;
    }
    /** 现金流利率信息表 add by sunshuaishuai on 20210322 end**/

}

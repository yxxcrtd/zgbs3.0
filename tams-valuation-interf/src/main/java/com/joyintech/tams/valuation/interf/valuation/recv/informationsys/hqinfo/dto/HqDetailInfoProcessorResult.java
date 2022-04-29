/*
 * 文件名：AssetMangementRecResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangwei1
 * 修改时间：2018年6月28日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.informationsys.hqinfo.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.FinProductValuationDto;
import com.joyintech.tams.valuation.common.interf.base.dto.HqInfoForInterfRecCommonResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行情信息接口Processor返回共通结果
 * @author wangwei1
 * @version 1.0 2018年6月28日
 * @see HqDetailInfoProcessorResult
 * @since 1.0
 */
public class HqDetailInfoProcessorResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 基金市场行情
     */
    private List<MstFundMarketInfo> listValFundMarketInfo = new ArrayList<MstFundMarketInfo>();
    /**
     * 债券市场行情
     */
    private List<MstBondMarketInfo> listValBondMarketInfo = new ArrayList<MstBondMarketInfo>();
    
    /**
     * 场外金融产品行情
     */
    private List<FinProductValuation> listValFinProductValuation = new ArrayList<FinProductValuation>();
    
    /**
     * 债券估值行情
     */
    private List<MstBondValuationInfo> listBondValuation = new ArrayList<MstBondValuationInfo>();
    
    /**
     * 利率行情表
     */
    private List<MstIntRateInfo> mstIntRateInfoList = new ArrayList<MstIntRateInfo>();
    
    /**
     * 行情信息表
     */
//	private List<MstRateInfo> mstRateInfoList = new ArrayList<MstRateInfo>();
    
	/**
     * 基金市场估值
     */
	private List<FinProductValuationDto> standardFondMarketValList = new  ArrayList<FinProductValuationDto>();
	/**
     * 债券市价估值
     */
	private List<MstBondValuationInfo> standardMarketBondPriceValList = new  ArrayList<MstBondValuationInfo>();
	/**
     * 基础利率重置
     */
	//private List<StandardRateReset> standardRateResetList = new  ArrayList<StandardRateReset>();
	
    /**
     * 日志
     */
    private List<ValInterflog> listValInterflog = new ArrayList<ValInterflog>();
    
    /** add by dongjun on 20181011 title:债券行权计划 start**/
    /**
     * 债券行权
     */
    private List<FinExercisePlan> finExercisePlanList = new ArrayList<FinExercisePlan>();
    /** add by dongjun on 20181011 title:债券行权计划 end**/

    /** add by yanya on 20210831 增加股票行情接口 start**/
    List<MstSharePrice> valShareInfo = new ArrayList<>();
    /** add by yanya on 20210831 增加股票行情接口 end**/

    /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情 start**/
    /**
     * 股票流动性折扣信息
     */
    List<MstStockLiquidDiscountInfo> mstStockLiquidDiscountInfoList = new ArrayList<>();

    /**
     * 指数行情信息
     */
    List<MstIndicatorQuotation> mstIndicatorQuotationList = new ArrayList<>();
    /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情 end**/

    /**
     * 重载构造方法 从valuation-common中返回的result重新赋值
     * 
     * @param rtnResult 共通结果
     */
    public HqDetailInfoProcessorResult(HqInfoForInterfRecCommonResult result) {
        this.setListValBondMarketInfo(result.getListValBondMarketInfo());
        this.setListValFundMarketInfo(result.getListValFundMarketInfo());
        this.setListValFinProductValuation(result.getListValFinProductValuation());
        this.setListValInterflog(result.getListValInterflog());
        this.setMstIntRateInfoList(result.getMstIntRateInfoList());
//        this.setMstRateInfoList(result.getMstRateInfoList());
        this.setListBondValuation(result.getMstBondValuationInfoList());
        this.setParamMap(result.getParamMap());
        /** add by dongjun on 20181011 title:债券行权计划 start**/
        this.finExercisePlanList = result.getFinExercisePlanList();
        /** add by dongjun on 20181011 title:债券行权计划 end**/
        /** add by yanya on 20210831 增加股票行情接口 start**/
        this.setValShareInfo(result.getValShareInfo());
        /** add by yanya on 20210831 增加股票行情接口 end**/

        /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情 start**/
        this.setMstStockLiquidDiscountInfoList(result.getMstStockLiquidDiscountInfoList());
        this.setMstIndicatorQuotationList(result.getMstIndicatorQuotationList());
        /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情 end**/
    }

    
    /**
     * 获取 listValBondMarketInfo
     * @return listValBondMarketInfo.
     */
    public List<MstBondMarketInfo> getListValBondMarketInfo() {
        return listValBondMarketInfo;
    }

    
    /**
     * 设置 listValBondMarketInfo
     * @param listValBondMarketInfo listValBondMarketInfo
     */
    public void setListValBondMarketInfo(List<MstBondMarketInfo> listValBondMarketInfo) {
        this.listValBondMarketInfo = listValBondMarketInfo;
    }




    /**
     * 获取 listValFundMarketInfo
     * @return listValFundMarketInfo.
     */
    public List<MstFundMarketInfo> getListValFundMarketInfo() {
        return listValFundMarketInfo;
    }



    
    /**
     * 设置 listValFundMarketInfo
     * @param listValFundMarketInfo listValFundMarketInfo
     */
    public void setListValFundMarketInfo(List<MstFundMarketInfo> listValFundMarketInfo) {
        this.listValFundMarketInfo = listValFundMarketInfo;
    }



    
    /**
     * 获取 listValFinProductValuation
     * @return listValFinProductValuation.
     */
    public List<FinProductValuation> getListValFinProductValuation() {
        return listValFinProductValuation;
    }



    
    /**
     * 设置 listValFinProductValuation
     * @param listValFinProductValuation listValFinProductValuation
     */
    public void setListValFinProductValuation(List<FinProductValuation> listValFinProductValuation) {
        this.listValFinProductValuation = listValFinProductValuation;
    }



    
    /**
     * 获取 listValInterflog
     * @return listValInterflog.
     */
    public List<ValInterflog> getListValInterflog() {
        return listValInterflog;
    }



    
    /**
     * 设置 listValInterflog
     * @param listValInterflog listValInterflog
     */
    public void setListValInterflog(List<ValInterflog> listValInterflog) {
        this.listValInterflog = listValInterflog;
    }

    
    /**
     * 获取 serialversionuid
     * @return serialversionuid.
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }


	/**
	 * @return the mstIntRateInfoList
	 */
	public List<MstIntRateInfo> getMstIntRateInfoList() {
		return mstIntRateInfoList;
	}


	/**
	 * @param mstIntRateInfoList the mstIntRateInfoList to set
	 */
	public void setMstIntRateInfoList(List<MstIntRateInfo> mstIntRateInfoList) {
		this.mstIntRateInfoList = mstIntRateInfoList;
	}


	/**
	 * @return the mstRateInfoList
	 */
//	PUBLIC LIST<MSTRATEINFO> GETMSTRATEINFOLIST() {
//		RETURN MSTRATEINFOLIST;
//	}


	/**
	 * @param mstRateInfoList the mstRateInfoList to set
	 */
//	public void setMstRateInfoList(List<MstRateInfo> mstRateInfoList) {
//		this.mstRateInfoList = mstRateInfoList;
//	}


    
    /**
     * 获取 listBondValuation
     * @return listBondValuation.
     */
    public List<MstBondValuationInfo> getListBondValuation() {
        return listBondValuation;
    }


    
    /**
     * 设置 listBondValuation
     * @param listBondValuation listBondValuation
     */
    public void setListBondValuation(List<MstBondValuationInfo> listBondValuation) {
        this.listBondValuation = listBondValuation;
    }
    
    /**add by wujie on 20180910 start**/
    private Map<String,Object> paramMap = new HashMap<String,Object>();


	/**
	 * @return the paramMap
	 */
	public Map<String, Object> getParamMap() {
		return paramMap;
	}


	/**
	 * @param paramMap the paramMap to set
	 */
	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
    
    
    /**add by wujie on 20180910 end**/
    
	   /**
	 * 获取 standardFondMarketValList
	 * @return standardFondMarketValList.
	 */
	public List<FinProductValuationDto> getStandardFondMarketValList() {
		return standardFondMarketValList;
	}


	/**
	 * 设置 standardFondMarketValList
	 * @param standardFondMarketValList standardFondMarketValList
	 */
	public void setStandardFondMarketValList(List<FinProductValuationDto> standardFondMarketValList) {
		this.standardFondMarketValList = standardFondMarketValList;
	}


	/**
	 * 获取 standardMarketBondPriceValList
	 * @return standardMarketBondPriceValList.
	 */
	public List<MstBondValuationInfo> getStandardMarketBondPriceValList() {
		return standardMarketBondPriceValList;
	}


	/**
	 * 设置 standardMarketBondPriceValList
	 * @param standardMarketBondPriceValList standardMarketBondPriceValList
	 */
	public void setStandardMarketBondPriceValList(List<MstBondValuationInfo> standardMarketBondPriceValList) {
		this.standardMarketBondPriceValList = standardMarketBondPriceValList;
	}


	/** add by dongjun on 20181011 title:债券行权计划 start**/
    /**
    * 获取 payhsrcsecoptionList
    * @return payhsrcsecoptionList.
    */
   public List<FinExercisePlan> getFinExercisePlanList() {
       return finExercisePlanList;
   }
 
   /**
    * 设置 payhsrcsecoptionList
    * @param payhsrcsecoptionList payhsrcsecoptionList
    */
   public void setFinExercisePlanList(List<FinExercisePlan> finExercisePlanList) {
       this.finExercisePlanList = finExercisePlanList;
   }
   /** add by dongjun on 20181011 title:债券行权计划 end**/

    /** add by yanya on 20210831 增加股票行情接口 start**/
    public List<MstSharePrice> getValShareInfo() {
        return valShareInfo;
    }

    public void setValShareInfo(List<MstSharePrice> valShareInfo) {
        this.valShareInfo = valShareInfo;
    }
    /** add by yanya on 20210831 增加股票行情接口 end**/

    public List<MstStockLiquidDiscountInfo> getMstStockLiquidDiscountInfoList() {
        return mstStockLiquidDiscountInfoList;
    }

    public void setMstStockLiquidDiscountInfoList(List<MstStockLiquidDiscountInfo> mstStockLiquidDiscountInfoList) {
        this.mstStockLiquidDiscountInfoList = mstStockLiquidDiscountInfoList;
    }

    public List<MstIndicatorQuotation> getMstIndicatorQuotationList() {
        return mstIndicatorQuotationList;
    }

    public void setMstIndicatorQuotationList(List<MstIndicatorQuotation> mstIndicatorQuotationList) {
        this.mstIndicatorQuotationList = mstIndicatorQuotationList;
    }
}

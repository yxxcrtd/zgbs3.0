package com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.dto.RepoNStandardCommonResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//买断式回购标的返回共通结果
public class TrdOutRightRepoValuationTrade002Result extends BaseEntity{

	  /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 交易类金融产品
     */
    List<FinTradeProduct> listValTradeFinProduct = new ArrayList<FinTradeProduct>();

    /**
     * 质押品信息
     */
    private List<FinDealPledge> listFinDealPledge = new ArrayList<FinDealPledge>();


    /**
     * 金融产品分类
     */
    List<FinProductType> listValFinProductType = new ArrayList<FinProductType>();

    /**
     * 交易类金融产品附属表
     */
    private List<FinTradeProductAdd> listFinTradeProductAdd = new ArrayList<FinTradeProductAdd>();

    /**
     * 日志
     */
    private List<ValInterflog> valInterflog = new ArrayList<ValInterflog>();

    /**
     * 金融产品估值参数表
     * add by wujie on 20190420
     */
    private List<FinProductValuationPara> listFinProductValuationPara = new ArrayList<FinProductValuationPara>();

	public TrdOutRightRepoValuationTrade002Result() {
	}

	private TrdOutRightRepoValuationTrade001Dto trdOutRightRepoValuationTrade001Dto;

	public TrdOutRightRepoValuationTrade001Dto getTrdOutRightRepoValuationTrade001Dto() {
		return trdOutRightRepoValuationTrade001Dto;
	}

	public void setTrdOutRightRepoValuationTrade001Dto(TrdOutRightRepoValuationTrade001Dto trdOutRightRepoValuationTrade001Dto) {
		this.trdOutRightRepoValuationTrade001Dto = trdOutRightRepoValuationTrade001Dto;
	}


    /**
     * 重载构造方法 从valuation-common中返回的result重新赋值
     *
     * @param result 共通结果
     */


	public TrdOutRightRepoValuationTrade002Result(RepoNStandardCommonResult result) {
		this.setValInterflog(result.getValInterflog());
		this.setListValTradeFinProduct(result.getListValTradeFinProduct());
		this.setListValFinProductType(result.getListValFinProductType());
		this.setListFinDealPledge(result.getListFinDealPledge());
		this.setParamMap(result.getParamMap());
		this.setListFinTradeProductAdd(result.getListFinTradeProductAdd());
		/**add by wujie on 20190420 start**/
		this.setListFinProductValuationPara(result.getListFinProductValuationPara());
		/**add by wujie on 20190420 end**/
	}
	

	public List<FinDealPledge> getListFinDealPledge() {
		return listFinDealPledge;
	}


	public void setListFinDealPledge(List<FinDealPledge> listFinDealPledge) {
		this.listFinDealPledge = listFinDealPledge;
	}

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




	public List<ValInterflog> getValInterflog() {
		return valInterflog;
	}




	public void setValInterflog(List<ValInterflog> valInterflog) {
		this.valInterflog = valInterflog;
	}




	/**
	 * @return the listFinTradeProductAdd
	 */
	public List<FinTradeProductAdd> getListFinTradeProductAdd() {
		return listFinTradeProductAdd;
	}


	/**
	 * @param listFinTradeProductAdd the listFinTradeProductAdd to set
	 */
	public void setListFinTradeProductAdd(List<FinTradeProductAdd> listFinTradeProductAdd) {
		this.listFinTradeProductAdd = listFinTradeProductAdd;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
	 private Map<String,Object> paramMap;

		public Map<String, Object> getParamMap() {
			return paramMap;
		}

		public void setParamMap(Map<String, Object> paramMap) {
			this.paramMap = paramMap;
		}
		


		public List<FinProductValuationPara> getListFinProductValuationPara() {
			return listFinProductValuationPara;
		}


		public void setListFinProductValuationPara(List<FinProductValuationPara> listFinProductValuationPara) {
			this.listFinProductValuationPara = listFinProductValuationPara;
		}
    
}

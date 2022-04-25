
package com.joyintech.tams.valuation.common.interf.base.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.framework.base.BaseEntity;

    /**
     * 〈一句话功能简述〉
     * 〈功能详细描述〉
     * @author HuangJian
     * @version 1.0 2018年6月27日
     * @see LendDealCommonResult
     * @since 1.0
     */
    public class LendDealCommonResult extends BaseEntity{
        private static final long serialVersionUID = 1L;
        /**
         * 金融产品交易表
         */
        private List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();;
        /**
         * 估值核算交易扩展
         */
        private List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        /**
         * 交易费用明细表
         */
        private List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        
        /**
         * 结算指令
         */
        private List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        private List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        
        /**
         * 日志
         */
        private List<ValInterflog> listValInterflog = new ArrayList<ValInterflog>();
        
        /**
         * 交易类金融产品
         */
        private List<FinTradeProduct> listValFinTradeProduct = new ArrayList<FinTradeProduct>();
        
        /**
         * 交易入账
         */
        private List<InterfProductDealBookCommonDto> listInterfProductDealBookCommonDto = new ArrayList<InterfProductDealBookCommonDto>();

        /**add by wujie on 20181211 start**/
        /**
         * 入账信息List
         */
        private List<BokDealAccounting> bokDealAccountingList;
        /**
         * 入账账务明细List
         */
        private  List<BokDealAccountingDetail> bokDealAccountingDetailList;
        /**
         * 账务明细代码表List 
         */
        private  List<BokDetailId> bokDetailIdList;
        
        /**add by wujie on 20181211 end**/
        
        /**
         * 读取数据数量
         */
        private int dataNum;
        
        /**
         * 获取 listValFinProductDeal
         * @return listValFinProductDeal.
         */
        public List<TrdProductDeal> getListValFinProductDeal() {
            return listValFinProductDeal;
        }

        
        /**
         * 设置 listValFinProductDeal
         * @param listValFinProductDeal listValFinProductDeal
         */
        public void setListValFinProductDeal(List<TrdProductDeal> listValFinProductDeal) {
            this.listValFinProductDeal = listValFinProductDeal;
        }

        
        /**
         * 获取 listValFinProductDealExt
         * @return listValFinProductDealExt.
         */
        public List<TrdProductDealAdd> getListValFinProductDealExt() {
            return listValFinProductDealExt;
        }

        
        /**
         * 设置 listValFinProductDealExt
         * @param listValFinProductDealExt listValFinProductDealExt
         */
        public void setListValFinProductDealExt(List<TrdProductDealAdd> listValFinProductDealExt) {
            this.listValFinProductDealExt = listValFinProductDealExt;
        }

        
        /**
         * 获取 listValTradeFeeDetail
         * @return listValTradeFeeDetail.
         */
        public List<TrdTradeFeeDetail> getListValTradeFeeDetail() {
            return listValTradeFeeDetail;
        }

        
        /**
         * 设置 listValTradeFeeDetail
         * @param listValTradeFeeDetail listValTradeFeeDetail
         */
        public void setListValTradeFeeDetail(List<TrdTradeFeeDetail> listValTradeFeeDetail) {
            this.listValTradeFeeDetail = listValTradeFeeDetail;
        }

        
        /**
         * 获取 listValOrders
         * @return listValOrders.
         */
        public List<StlOrders> getListValOrders() {
            return listValOrders;
        }

        
        /**
         * 设置 listValOrders
         * @param listValOrders listValOrders
         */
        public void setListValOrders(List<StlOrders> listValOrders) {
            this.listValOrders = listValOrders;
        }

        
        /**
         * 获取 listValOrdersExt
         * @return listValOrdersExt.
         */
        public List<StlOrdersExt> getListValOrdersExt() {
            return listValOrdersExt;
        }

        
        /**
         * 设置 listValOrdersExt
         * @param listValOrdersExt listValOrdersExt
         */
        public void setListValOrdersExt(List<StlOrdersExt> listValOrdersExt) {
            this.listValOrdersExt = listValOrdersExt;
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
         * 获取 listInterfProductDealBookCommonDto
         * @return listInterfProductDealBookCommonDto.
         */
        public List<InterfProductDealBookCommonDto> getListInterfProductDealBookCommonDto() {
            return listInterfProductDealBookCommonDto;
        }

        
        /**
         * 设置 listInterfProductDealBookCommonDto
         * @param listInterfProductDealBookCommonDto listInterfProductDealBookCommonDto
         */
        public void setListInterfProductDealBookCommonDto(List<InterfProductDealBookCommonDto> listInterfProductDealBookCommonDto) {
            this.listInterfProductDealBookCommonDto = listInterfProductDealBookCommonDto;
        }

        /**
         * 用于存放去重数据筛选条件
         */
        private Map<String,Object> paramMap = new HashMap<String,Object>();

    	public Map<String, Object> getParamMap() {
    		return paramMap;
    	}

    	public void setParamMap(Map<String, Object> paramMap) {
    		this.paramMap = paramMap;
    	}


        /**
         * 获取 dataNum
         * @return dataNum.
         */
        public int getDataNum() {
            return dataNum;
        }


        /**
         * 设置 dataNum
         * @param dataNum dataNum
         */
        public void setDataNum(int dataNum) {
            this.dataNum = dataNum;
        }


		/**
		 * @return the bokDealAccountingList
		 */
		public List<BokDealAccounting> getBokDealAccountingList() {
			return bokDealAccountingList;
		}


		/**
		 * @param bokDealAccountingList the bokDealAccountingList to set
		 */
		public void setBokDealAccountingList(List<BokDealAccounting> bokDealAccountingList) {
			this.bokDealAccountingList = bokDealAccountingList;
		}


		/**
		 * @return the bokDealAccountingDetailList
		 */
		public List<BokDealAccountingDetail> getBokDealAccountingDetailList() {
			return bokDealAccountingDetailList;
		}


		/**
		 * @param bokDealAccountingDetailList the bokDealAccountingDetailList to set
		 */
		public void setBokDealAccountingDetailList(List<BokDealAccountingDetail> bokDealAccountingDetailList) {
			this.bokDealAccountingDetailList = bokDealAccountingDetailList;
		}


		/**
		 * @return the bokDetailIdList
		 */
		public List<BokDetailId> getBokDetailIdList() {
			return bokDetailIdList;
		}


		/**
		 * @param bokDetailIdList the bokDetailIdList to set
		 */
		public void setBokDetailIdList(List<BokDetailId> bokDetailIdList) {
			this.bokDetailIdList = bokDetailIdList;
		}
		
	    /**
	     * 获取 listValFinTradeProduct
	     * @return listValFinTradeProduct.
	     */
	    public List<FinTradeProduct> getListValFinTradeProduct() {
	        return listValFinTradeProduct;
	    }

	    
	    /**
	     * 设置 listValFinTradeProduct
	     * @param listValFinTradeProduct listValFinTradeProduct
	     */
	    public void setListValFinTradeProduct(List<FinTradeProduct> listValFinTradeProduct) {
	        this.listValFinTradeProduct = listValFinTradeProduct;
	    }
        
    }

